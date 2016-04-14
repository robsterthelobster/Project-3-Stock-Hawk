package com.sam_chordas.android.stockhawk.ui;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.db.chart.view.LineChartView;
import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.rest.models.HistoricalDataModel;

import java.io.IOException;

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
    String symbol;
    HistoricalDataService service;

    static final String URL = "https://query.yahooapis.com/v1/public/";

    public interface HistoricalDataService{
        @GET("yql")
        Call<HistoricalDataModel> getHistoricalData(@Query("q") String query);
    }

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

        Bundle args = getArguments();
        if(args != null){
            symbol = args.getString(MyStocksActivity.STOCK_POSITION, "");
        }

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


        String query = "select * from yahoo.finance.historicaldata where symbol = \"YHOO\" and startDate = \"2009-09-11\" and endDate = \"2010-03-10\"";
        Call<HistoricalDataModel> call = service.getHistoricalData(query);

        call.enqueue(new Callback<HistoricalDataModel>() {
            @Override
            public void onResponse(Call<HistoricalDataModel> call, Response<HistoricalDataModel> response) {
                System.out.println("enqueue " + response.body());
                if(response!=null){
                    System.out.println("response" + response.raw());
                    if(response.body()!=null){
                        System.out.println("body");
                    }
                }
            }

            @Override
            public void onFailure(Call<HistoricalDataModel> call, Throwable t) {
                System.out.println(t.getStackTrace());

                System.out.println("failed");
            }
        });

        // Inflate the layout for this fragment
        return rootView;
    }
}


