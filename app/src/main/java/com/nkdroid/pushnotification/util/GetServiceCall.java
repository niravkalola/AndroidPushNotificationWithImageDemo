package com.nkdroid.pushnotification.util;


import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.nkdroid.pushnotification.app.MyApplication;

import org.json.JSONArray;
import org.json.JSONObject;

public abstract class GetServiceCall implements IService {

    public abstract void response(String response);

    public abstract void error(VolleyError error);

    private String url;
    String response = null;

    public static int TYPE_JSONOBJECT = 0;
    public static int TYPE_JSONARRAY = 1;
    public static int TYPE_STRING = 2;
    public int type = 0;

    public GetServiceCall(String url, int type) {
        super();
        this.url = url;
        this.type = type;
    }

    // Main implementation of calling the webservice.

    public synchronized final GetServiceCall start() {

        call();

        return this;

    }

    public void call() {

        switch (type) {

            // case  for requesting json object
            case 0:

                JsonObjectRequest request = new JsonObjectRequest(url, null,
                        new Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject jobj) {

                                response(jobj.toString());

                            }
                        }, new ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError e) {

                        error(e);
                    }
                });

                MyApplication.getInstance().addToRequestQueue(request);

                break;


            // case for requesting json array
            case 1:

                JsonArrayRequest request2 = new JsonArrayRequest(url,
                        new Listener<JSONArray>() {

                            @Override
                            public void onResponse(JSONArray jArray) {

                                response(jArray.toString());
                            }
                        }, new ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError arg0) {

                        error(arg0);
                    }
                });

                MyApplication.getInstance().addToRequestQueue(request2);

                break;

            case 2:

                break;

        }

    }

}
