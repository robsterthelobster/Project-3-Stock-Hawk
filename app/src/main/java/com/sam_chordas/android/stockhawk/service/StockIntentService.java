package com.sam_chordas.android.stockhawk.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.gcm.GcmListenerService;
import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.GcmTaskService;
import com.google.android.gms.gcm.TaskParams;
import com.sam_chordas.android.stockhawk.R;

/**
 * Created by sam_chordas on 10/1/15.
 */
public class StockIntentService extends IntentService {

    public static final String ACTION_DATA_UPDATED =
            "com.sam_chordas.android.stockhawk.service.ACTION_DATA_UPDATED";

    public StockIntentService(){
        super(StockIntentService.class.getName());
    }

    public StockIntentService(String name) {
        super(name);
    }

    @Override protected void onHandleIntent(Intent intent) {
        //Log.d(StockIntentService.class.getSimpleName(), "Stock Intent Service");
        StockTaskService stockTaskService = new StockTaskService(this);
        Bundle args = new Bundle();
        if (intent.getStringExtra("tag").equals("add")){
            args.putString("symbol", intent.getStringExtra("symbol"));
        }
        // We can call OnRunTask from the intent service to force it to run immediately instead of
        // scheduling a task.
        int result = stockTaskService.onRunTask(new TaskParams(intent.getStringExtra("tag"), args));

        /*
            Toast if result failed, which is if batch returns 0, or failed to connect
         */
        if(result == GcmNetworkManager.RESULT_FAILURE) {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                public void run() {
                    Toast.makeText(getApplicationContext(), getString(R.string.add_non_ticker_toast), Toast.LENGTH_SHORT).show();
                }
            });
        }

        updateWidgets();
    }

    private void updateWidgets() {
        Intent dataUpdatedIntent = new Intent(ACTION_DATA_UPDATED)
                .setPackage(this.getPackageName());
        this.sendBroadcast(dataUpdatedIntent);
    }
}
