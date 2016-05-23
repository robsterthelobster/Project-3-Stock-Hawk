package com.sam_chordas.android.stockhawk.ui;

import android.animation.PropertyValuesHolder;
import android.app.Fragment;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.db.chart.Tools;
import com.db.chart.listener.OnEntryClickListener;
import com.db.chart.model.LineSet;
import com.db.chart.model.Point;
import com.db.chart.view.ChartView;
import com.db.chart.view.LineChartView;
import com.db.chart.view.Tooltip;
import com.db.chart.view.animation.Animation;
import com.db.chart.view.animation.easing.CubicEase;
import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.rest.models.HistoricalDataModel;
import com.sam_chordas.android.stockhawk.rest.models.Quote;

import org.w3c.dom.Text;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by robin on 4/11/2016.
 */
public class DetailFragment extends Fragment {

    LineChartView mLineChartView;
    TextView mTooltipTextView;
    TextView emptyView;
    TextView loadingView;
    String symbol;
    HistoricalDataService service;
    String query;
    String[] mLabels= {};
    float[][] mValues = {{}};
    Tooltip mTip;

    static final String URL = "https://query.yahooapis.com/v1/public/";
    static final String LOG_TAG = DetailFragment.class.getSimpleName();

    public interface HistoricalDataService{
        //https://query.yahooapis.com/v1/public/yql?q=?query&...
        @GET("yql")
        Call<HistoricalDataModel> getHistoricalData(@Query("q") String query);
    }

    /*
        Interceptor to append query parameters
     */
    static class ServiceInterceptor implements Interceptor {
        @Override
        public okhttp3.Response intercept(Chain chain) throws IOException {
            HttpUrl url = chain.request().url().newBuilder()
                    .addEncodedQueryParameter("format", "json")
                    .addEncodedQueryParameter("diagnostics", "false")
                    .addEncodedQueryParameter("env", "store%3A%2F%2Fdatatables.org%2Falltableswithkeys")
                    .build();
            Request request = chain.request().newBuilder().url(url).build();
            return chain.proceed(request);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.detail_fragment, container, false);
        mLineChartView = (LineChartView) rootView.findViewById(R.id.linechart);
        emptyView = (TextView) rootView.findViewById(R.id.chart_empty_view);
        loadingView = (TextView) rootView.findViewById(R.id.chart_loading_view);
        mTooltipTextView = (TextView) rootView.findViewById(R.id.value);

        Bundle args = getArguments();
        if(args != null){
            symbol = args.getString(MyStocksActivity.STOCK_POSITION, "");
        }

        //build query to fetch data
        query = "select * from yahoo.finance.historicaldata where symbol = \"" + symbol +"\" and startDate = \"2016-04-04\" and endDate = \"2016-04-15\"";
        fetchHistoricalData();

        // Inflate the layout for this fragment
        return rootView;
    }

    private void fetchHistoricalData(){
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.interceptors().add(new ServiceInterceptor());
        OkHttpClient client = builder.build();

        // Set the custom client when building adapter
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        service = retrofit.create(HistoricalDataService.class);

        Call<HistoricalDataModel> call = service.getHistoricalData(query);

        call.enqueue(new Callback<HistoricalDataModel>() {
            @Override
            public void onResponse(Call<HistoricalDataModel> call, Response<HistoricalDataModel> response) {
                if(response != null && response.body()!=null && isAdded()){
                    List<Quote> quotes = response.body().getQuery().getResults().getQuote();
                    final int arraySize = quotes.size();
                    float[] values = new float[arraySize];
                    mLabels = new String[arraySize];

                    float minValue = Float.MAX_VALUE, maxValue = Float.MIN_VALUE;

                    LineSet dataset = new LineSet();

                    for(int i = arraySize - 1; i >= 0; i--){
                        Quote quote = quotes.get(i);
                        float value = quote.getClose();
                        if(value < minValue){
                            minValue = value;
                        }
                        if(value > maxValue){
                            maxValue = value;
                        }

                        values[i] = value;
                        mLabels[i] = Utility.truncateYearFromDate(quote.getDate());
                        dataset.addPoint(new Point(Utility.truncateYearFromDate(quote.getDate()), value));
                    }
                    mValues[0] = values;

                    // chart
                    //dataset = new LineSet(mLabels, values);
                    dataset.setColor(getResources().getColor(R.color.material_pink_a400))
                            .setDotsColor(getResources().getColor(R.color.material_blue_600))
                            .setThickness(6).setDotsRadius(24f);

                    mLineChartView.addData(dataset);
                    mLineChartView.setLabelsColor(getResources().getColor(R.color.material_pink_a400));

                    float buffer = (maxValue-minValue)/4f;
                    int max = (maxValue > 1) ? Math.round(maxValue+buffer) : 1;
                    int min = (minValue > 1) ? Math.round(minValue-buffer) : 1;
                    // set value labels, step is default by 1
                    mLineChartView.setAxisBorderValues(min, max, 1);

                    Paint gridPaint = new Paint();
                    gridPaint.setColor(Color.parseColor("#2d374c"));
                    gridPaint.setStyle(Paint.Style.STROKE);
                    gridPaint.setAntiAlias(true);
                    gridPaint.setStrokeWidth(Tools.fromDpToPx(1.5f));
                    mLineChartView.setGrid(ChartView.GridType.FULL, (int)(arraySize*1.5), arraySize, gridPaint);

                    // Animation customization
                    Animation anim = new Animation(1500);
                    anim.setEasing(new CubicEase());
                    mLineChartView.show(anim);

                    // Tooltip -- sample and resources from the sample WilliamChart LineCardThree
                    mTip = new Tooltip(getActivity(), R.layout.tooltip, R.id.value);
                    mTip.setVerticalAlignment(Tooltip.Alignment.BOTTOM_TOP);
                    mTip.setDimensions((int) Tools.fromDpToPx(80), (int) Tools.fromDpToPx(25));

                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {

                        mTip.setEnterAnimation(PropertyValuesHolder.ofFloat(View.ALPHA, 1),
                                PropertyValuesHolder.ofFloat(View.SCALE_Y, 1f),
                                PropertyValuesHolder.ofFloat(View.SCALE_X, 1f)).setDuration(200);

                        mTip.setExitAnimation(PropertyValuesHolder.ofFloat(View.ALPHA, 0),
                                PropertyValuesHolder.ofFloat(View.SCALE_Y, 0f),
                                PropertyValuesHolder.ofFloat(View.SCALE_X, 0f)).setDuration(200);

                        mTip.setPivotX(Tools.fromDpToPx(80) / 2);
                        mTip.setPivotY(Tools.fromDpToPx(25));
                    }

                    mTip.setValueFormat(new DecimalFormat("0.00"));
                    mLineChartView.setTooltips(mTip);

                    mLineChartView.setOnEntryClickListener(new OnEntryClickListener() {
                        @Override
                        public void onClick(int setIndex, int entryIndex, Rect rect) {
                            if(mTip.getParent() != null){
                                ((ViewGroup)mTip.getParent()).removeView(mTip);
                            }
                            float value = mValues[setIndex][entryIndex];
                            String label = mLabels[arraySize - entryIndex - 1];
                            mTip.prepare(mLineChartView.getEntriesArea(setIndex).get(entryIndex), value);
                            mLineChartView.showTooltip(mTip, true);
                            int format = R.string.a11y_tooltip;
                            String description = String.format(getActivity().getString(format), value, label);

                            mTooltipTextView = (TextView) getActivity().findViewById(R.id.value);

                            if(mTooltipTextView != null){
                                mTooltipTextView.setContentDescription(description);
                            }
                        }
                    });
                }
                emptyView.setVisibility(View.INVISIBLE);
                loadingView.setVisibility(View.INVISIBLE);

                for(View view : mLineChartView.getTouchables()){
                    view.setFocusable(true);
                }
            }

            @Override
            public void onFailure(Call<HistoricalDataModel> call, Throwable t) {
                Log.e(LOG_TAG, t.getStackTrace().toString());
                emptyView.setVisibility(View.VISIBLE);
            }
        });

    }
}


