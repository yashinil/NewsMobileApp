package com.example.newsapp;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AutoSuggest {
    private static AutoSuggest mInstance;
    private RequestQueue mRequestQueue;
    private static Context mCtx;
    public AutoSuggest(Context ctx) {
        mCtx = ctx;
        mRequestQueue = getRequestQueue();
    }
    public static synchronized AutoSuggest getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new AutoSuggest(context);
        }
        return mInstance;
    }
    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return mRequestQueue;
    }
    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }
    public static void make(Context ctx, String query, Response.Listener<JSONObject>
            listener, Response.ErrorListener errorListener) {
        String url = "https://yashi.cognitiveservices.azure.com/bing/v7.0/suggestions?mkt=fr-Fr&q=" + query;
        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.GET, url, null, listener, errorListener){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<String, String>();
                parameters.put("Ocp-Apim-Subscription-Key", "91d45552e8a348009ccfdfa555647ce1");
                return parameters;
            }
        };
        AutoSuggest.getInstance(ctx).addToRequestQueue(stringRequest);
    }
}
