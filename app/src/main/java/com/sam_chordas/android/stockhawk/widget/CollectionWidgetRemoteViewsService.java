package com.sam_chordas.android.stockhawk.widget;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;
import com.sam_chordas.android.stockhawk.rest.Utils;
import com.sam_chordas.android.stockhawk.ui.MyStocksActivity;
import com.sam_chordas.android.stockhawk.ui.Utility;

/**
 * Created by robin on 5/16/2016.
 */
public class CollectionWidgetRemoteViewsService extends  RemoteViewsService{
    public final String LOG_TAG = CollectionWidgetRemoteViewsService.class.getSimpleName();
    private static final String[] STOCKS_COLUMNS = {
            QuoteColumns._ID,
            QuoteColumns.SYMBOL,
            QuoteColumns.BIDPRICE,
            QuoteColumns.PERCENT_CHANGE,
            QuoteColumns.CHANGE,
            QuoteColumns.ISUP
    };
    // these indices must match the projection
    static final int INDEX_STOCKS_ID = 0;
    static final int INDEX_STOCKS_SYMBOL = 1;
    static final int INDEX_STOCKS_BIDPRICE = 2;
    static final int INDEX_STOCKS_PERCENT_CHANGE = 3;
    static final int INDEX_STOCKS_CHANGE = 4;
    static final int INDEX_STOCKS_ISUP = 5;

    @Override
    public RemoteViewsService.RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RemoteViewsService.RemoteViewsFactory() {
            private Cursor data = null;

            @Override
            public void onCreate() {
                // Nothing to do
            }

            @Override
            public void onDataSetChanged() {
                if (data != null) {
                    data.close();
                }
                final long identityToken = Binder.clearCallingIdentity();
                Uri uri = QuoteProvider.Quotes.CONTENT_URI;
                data = getContentResolver().query(uri,
                        STOCKS_COLUMNS,
                        QuoteColumns.ISCURRENT + " = ?",
                        new String[]{"1"},
                        null);

                Binder.restoreCallingIdentity(identityToken);
            }

            @Override
            public void onDestroy() {
                if (data != null) {
                    data.close();
                    data = null;
                }
            }

            @Override
            public int getCount() {
                return data == null ? 0 : data.getCount();
            }

            @Override
            public RemoteViews getViewAt(int position) {
                if (position == AdapterView.INVALID_POSITION ||
                        data == null || !data.moveToPosition(position)) {
                    return null;
                }
                RemoteViews views = new RemoteViews(getPackageName(), R.layout.widget_list_item);

                String symbol = data.getString(INDEX_STOCKS_SYMBOL);
                String bid_price = data.getString(INDEX_STOCKS_BIDPRICE);
                String change = data.getString(INDEX_STOCKS_PERCENT_CHANGE);

                views.setTextViewText(R.id.stock_symbol, symbol);
                views.setContentDescription(R.id.stock_symbol, getString(R.string.a11y_symbol, symbol));

                views.setTextViewText(R.id.bid_price, bid_price);
                views.setContentDescription(R.id.bid_price, getString(R.string.a11y_price, bid_price));

                views.setTextViewText(R.id.change, change);
                views.setContentDescription(R.id.change, getString(R.string.a11y_change, symbol));

                if (Utils.showPercent){
                    views.setContentDescription(R.id.change, getString(R.string.a11y_change, change));
                } else{
                    views.setContentDescription(R.id.change, getString(R.string.a11y_change_dollars, change));
                }

                int isUp = data.getInt(INDEX_STOCKS_ISUP);
                if(isUp == 1){
                    views.setInt(R.id.change, "setBackgroundResource", R.drawable.percent_change_pill_green);
                }else{
                    views.setInt(R.id.change, "setBackgroundResource", R.drawable.percent_change_pill_red);
                }

                final Intent fillInIntent = new Intent();
                fillInIntent.putExtra(MyStocksActivity.STOCK_POSITION, symbol);
                views.setOnClickFillInIntent(R.id.widget_list_item, fillInIntent);

                return views;
            }

            @Override
            public RemoteViews getLoadingView() {
                return new RemoteViews(getPackageName(), R.layout.widget_list_item);
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int position) {
                if (data.moveToPosition(position))
                    return data.getLong(INDEX_STOCKS_ID);
                return position;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }
        };
    }
}
