package com.sam_chordas.android.stockhawk.rest.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by robin on 4/13/2016.
 */
public class Quote {
    @SerializedName("Symbol")
    String symbol;

    @SerializedName("Date")
    String date;

    @SerializedName("Open")
    float open;

    @SerializedName("High")
    float high;

    @SerializedName("Close")
    float close;

    @SerializedName("Volume")
    int volume;

    @SerializedName("Adj_Close")
    float adj_close;

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public float getOpen() {
        return open;
    }

    public void setOpen(float open) {
        this.open = open;
    }

    public float getHigh() {
        return high;
    }

    public void setHigh(float high) {
        this.high = high;
    }

    public float getClose() {
        return close;
    }

    public void setClose(float close) {
        this.close = close;
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    public float getAdj_close() {
        return adj_close;
    }

    public void setAdj_close(float adj_close) {
        this.adj_close = adj_close;
    }
}
