package com.example.newsapp;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TrendingFragment extends Fragment {

    LineChart lineChart;
    EditText input_keyword;
    String current_keyword="CoronaVirus";
    String curr_key = "";
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.trending_fragment,container,false);
        get_keyword_data();
        input_keyword = view.findViewById(R.id.trending_keyword);
        input_keyword.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(keyCode == KeyEvent.KEYCODE_ENTER){
                    if (event.getAction() == KeyEvent.ACTION_DOWN){
                        String keyword = input_keyword.getText().toString();

//                        if(keyword != null && keyword.length() != 0){
//                            Toast.makeText(getActivity().getApplicationContext(), input_keyword.getText().toString(),Toast.LENGTH_LONG).show();
//                        }
                        input_keyword.clearFocus();
                        curr_key = keyword;
                        get_keyword_data();
                        InputMethodManager imm = (InputMethodManager) getActivity().getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(v.getWindowToken(),0);
                        return true;
                    }
                }
                return false;
            }
        });

//        input_keyword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                String keyword = input_keyword.getText().toString();
//
//                InputMethodManager imm = (InputMethodManager) getActivity().getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
//                imm.hideSoftInputFromWindow(v.getWindowToken(),0);
//
////                if(!hasFocus && keyword != null && keyword.length() != 0){
////                    Toast.makeText(getActivity().getApplicationContext(), input_keyword.getText().toString(),Toast.LENGTH_LONG).show();
////                }
//                curr_key = keyword;
//                get_keyword_data();
//            }
//        });

        lineChart = (LineChart) view.findViewById(R.id.trending_chart);
        return view;
    }

    public void get_keyword_data(){
        if(curr_key == "" || curr_key.length() == 0 || curr_key == null){
            curr_key = current_keyword;
        }
        String url= "https://webtechassignment-273804.wl.r.appspot.com/trendingChart?key="+curr_key;
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject default_ = response.getJSONObject("default");
                    JSONArray values = default_.getJSONArray("timelineData");

                    ArrayList data = new ArrayList<>();
                    for(int i=0;i<values.length();i++){
                        JSONObject value = values.getJSONObject(i);
                        JSONArray value_ = value.getJSONArray("value");
                        data.add(new Entry(i+1, Float.parseFloat(value_.getString(0))));
                    }
                    LineDataSet input_data = new LineDataSet(data, "Trending chart for "+ curr_key);
                    input_data.setColor(Color.parseColor("#6200EE"));
                    input_data.setValueTextSize(8f);
                    input_data.setCircleColor(Color.rgb(98,0,238));
                    input_data.setCircleHoleColor(Color.rgb(98,0,238));
                    input_data.setValueTextColor(Color.parseColor("#6200EE"));
                    //Render the chart
                    List<ILineDataSet> dataSets = new ArrayList<>();
                    dataSets.add(input_data);
                    LineData data1 = new LineData(dataSets);
                    Legend l = lineChart.getLegend();
                    l.setTextSize(16f);
                    l.setFormSize(14f);
                    l.setTextColor(Color.BLACK);
                    lineChart.getXAxis().setDrawGridLines(false);
                    lineChart.getAxisLeft().setDrawGridLines(false);
                    lineChart.getAxisRight().setDrawGridLines(false);
                    lineChart.getAxisRight().setAxisLineWidth(1);
                    lineChart.getAxisLeft().setDrawAxisLine(false);

                    lineChart.setData(data1);
                    lineChart.invalidate();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {}
        }
        );
        RequestQueue queue = Volley.newRequestQueue(getActivity().getApplicationContext());
        queue.add(req);
    }
}
