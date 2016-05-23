package com.sam_chordas.android.stockhawk.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.sam_chordas.android.stockhawk.R;


public class DetailActivity extends AppCompatActivity {

    String mSymbol;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_graph);

        if(savedInstanceState == null){
            Bundle bundle = new Bundle();
            String symbol = getIntent().getStringExtra(MyStocksActivity.STOCK_POSITION);
            bundle.putString(MyStocksActivity.STOCK_POSITION, symbol);
            mSymbol = symbol;

            updateTitleWithSymbol();

            DetailFragment fragment = new DetailFragment();
            fragment.setArguments(bundle);

            getFragmentManager().beginTransaction().replace(R.id.detail_containter, fragment).commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume(){
        super.onResume();
        updateTitleWithSymbol();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        /*
            Preserve symbol for the title
         */
        savedInstanceState.putString("Symbol", mSymbol);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
         /*
            Preserve symbol for the title
         */
        super.onRestoreInstanceState(savedInstanceState);
        mSymbol = savedInstanceState.getString("Symbol");
    }

    /*
    Update the detail activity label with the stock symbol
    */
    private void updateTitleWithSymbol(){
        if(mSymbol != null) {
            this.setTitle(getString(R.string.detail_label, mSymbol.toUpperCase()));
        }
    }
}