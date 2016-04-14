package com.sam_chordas.android.stockhawk.rest.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by robin on 4/13/2016.
 */
public class QuoteModel {
    @SerializedName("quote")
    List<Quote> quote;
}
