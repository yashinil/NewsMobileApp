package com.example.newsapp;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;


/**
 * A simple {@link Fragment} subclass.
 */
public class PoliticsFragment extends Fragment {

    RecyclerView recyclerView;
    LinearLayout progressBar;
    SwipeRefreshLayout mSwipeRefreshLayout;

    public static Adapter adapter;

    public PoliticsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_politics, container, false);

        progressBar = view.findViewById(R.id.circle_progress_bar);
        progressBar.setVisibility(View.VISIBLE);

        recyclerView = view.findViewById(R.id.politics_cards_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(),linearLayoutManager.getOrientation()));

        find_news();

        mSwipeRefreshLayout = view.findViewById(R.id.refresh_politics);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                progressBar.setVisibility(View.VISIBLE);
                // Your code to make your refresh action
                find_news();
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(mSwipeRefreshLayout.isRefreshing()) {
                            mSwipeRefreshLayout.setRefreshing(false);
                        }
                    }
                }, 1000);
            }
        });

        return view;
    }

    public void find_news(){
        String url= "https://webtechassignment-273804.wl.r.appspot.com/politics?api=true";
        final List<NewsCardModel> newsCardModelList = new ArrayList<>();

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject data = response.getJSONObject("data");
                    JSONObject resp = data.getJSONObject("response");
                    JSONArray arr = resp.getJSONArray("results");

                    for(int i=0;i<arr.length();i++){
                        String img_url = "";
                        JSONObject main;
                        JSONArray elements;
                        JSONObject article = arr.getJSONObject(i);
                        JSONObject blocks = article.getJSONObject("blocks");
                        if(blocks.has("main")) {
                            main = blocks.getJSONObject("main");
                            elements = main.getJSONArray("elements");

                            if (elements.length() > 0 && elements.getJSONObject(0).has("assets") && elements.getJSONObject(0).getJSONArray("assets").length() > 0) {
                                JSONObject element = elements.getJSONObject(0);
                                JSONArray assets = element.getJSONArray("assets");
                                JSONObject asset = assets.getJSONObject(0);
                                img_url = asset.getString("file");
                            }
                        }
                        if(img_url == "" || img_url == " " || img_url == null){
                            img_url = "https://assets.guim.co.uk/images/eada8aa27c12fe2d5afa3a89d3fbae0d/fallback-logo.png";
                        }
                        String identification = article.getString("id");
                        String twitter_url = article.getString("webUrl");
                        String title = article.getString("webTitle");
                        String date = article.getString("webPublicationDate");
                        String section = article.getString("sectionName");

                        newsCardModelList.add(new NewsCardModel(identification, img_url, title, date, section, twitter_url));
                    }
                    progressBar.setVisibility(View.GONE);

                    adapter = new Adapter(getActivity(), newsCardModelList );
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }
        );
        RequestQueue queue = Volley.newRequestQueue(getActivity().getApplicationContext());
        queue.add(req);

        adapter = new Adapter(getActivity(), newsCardModelList );
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
}
