package com.sam_chordas.android.stockhawk.rest.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by robin on 4/13/2016.
 */
public class Quote {
    @SerializedName("symbol")
    String symbol;

    @SerializedName("date")
    String date;

    @SerializedName("open")
    float open;

    @SerializedName("high")
    float high;

    @SerializedName("close")
    float close;

    @SerializedName("volume")
    int volume;

    @SerializedName("adj_close")
    float adj_close;

}
