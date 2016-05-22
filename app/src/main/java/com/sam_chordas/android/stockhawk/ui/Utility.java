package com.sam_chordas.android.stockhawk.ui;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by robin on 4/15/2016.
 */
public class Utility {

    public static boolean isNetworkAvailable(Context context){
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }

    /*
        API format is yyyy-MM-dd
        need to format to fit in graph labels
     */
    public static String truncateYearFromDate(String str){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = sdf.parse(str);
            return new SimpleDateFormat("MMM dd", Locale.US).format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }
}
