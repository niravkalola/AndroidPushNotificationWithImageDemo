package com.nkdroid.pushnotification.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by nirav on 01/05/16.
 */
public class JSONresponse {

    @SerializedName("status")
    private int status;
    @SerializedName("msg")
    private String message;

    public String getMessage() {
        return message;
    }

    public int getStatus() {
        return status;
    }

}
