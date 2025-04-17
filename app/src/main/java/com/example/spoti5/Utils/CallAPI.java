package com.example.spoti5.Utils;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class CallAPI {
    private static CallAPI instance;
    private RequestQueue requestQueue;
    private static Context ctx;

    private CallAPI(Context context) {
        ctx = context;
        requestQueue = getRequestQueue();
    }

    public static synchronized CallAPI getInstance(Context context) {
        if (instance == null) {
            instance = new CallAPI(context);
        }
        return instance;
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(ctx.getApplicationContext());
        }
        return requestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }
}
