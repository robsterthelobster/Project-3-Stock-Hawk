package com.sam_chordas.android.stockhawk.rest.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by robin on 4/13/2016.
 */
public class QueryModel {
    @SerializedName("count")
    int count;

    @SerializedName("created")
    String created;

    @SerializedName("lang")
    String lang;

    @SerializedName("results")
    QuoteModel results;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public QuoteModel getResults() {
        return results;
    }

    public void setResults(QuoteModel results) {
        this.results = results;
    }
}
