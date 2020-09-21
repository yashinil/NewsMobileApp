package com.example.newsapp;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class HomeFragment extends Fragment {

    LocationManager locationManager;
    String provider;
    Double lat;
    Double lng;
    String city;
    String state;
    String country;
    ImageView weather_back;
    TextView weather_city, weather_state, weather_degrees, weather_type;
    LocationListener locationListener;

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    RecyclerView recyclerView;
    public static Adapter adapter;
    LinearLayout progressBar;
    SwipeRefreshLayout mSwipeRefreshLayout;
//
//    public HomeFragment(String city, String state, String country){
//        this.city=city;
//        this.state=state;
//        this.country=country;
//    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_fragment,container,false);
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        provider = locationManager.getBestProvider(new Criteria(), false);

        if(checkLocationPermission()){
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, getActivity());

                locationListener = new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        Log.v("heree","here");
                        lat = location.getLatitude();
                        lng = location.getLongitude();
                        Geocoder geocoder = new Geocoder(getActivity().getApplicationContext(), Locale.getDefault());
                        List<Address> addresses = null;
                        try {
                            addresses = geocoder.getFromLocation(lat, lng, 1);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        city = addresses.get(0).getLocality();
                        state = addresses.get(0).getAdminArea();
                        country = addresses.get(0).getCountryName();
                        Log.v("Location info: country", String.valueOf(addresses));
                        find_weather();
                        locationManager.removeUpdates(this);
                        locationManager=null;
                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {

                    }

                    @Override
                    public void onProviderEnabled(String provider) {

                    }

                    @Override
                    public void onProviderDisabled(String provider) {

                    }
                };
                locationManager.requestLocationUpdates(provider, 5000,0,locationListener);
            }
        }
        else {
//            locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }

        progressBar = view.findViewById(R.id.circle_progress_bar);
        progressBar.setVisibility(View.VISIBLE);

        //Data related to weather API to display on weather card
        weather_city = (TextView) view.findViewById(R.id.weather_city_name);
        weather_degrees = (TextView) view.findViewById(R.id.weather_degrees);
        weather_state = (TextView) view.findViewById(R.id.weather_state_name);
        weather_type = (TextView) view.findViewById(R.id.weather_type);
        weather_back = (ImageView) view.findViewById(R.id.weather_background);

        //Everything related to news on the home page

        recyclerView = view.findViewById(R.id.news_cards_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(),linearLayoutManager.getOrientation()));
        find_news();

        mSwipeRefreshLayout = view.findViewById(R.id.refresh_home);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                progressBar.setVisibility(View.VISIBLE);
                // Your code to make your refresh action
                find_weather();
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

    public void find_weather(){
        String weatherAPIkey = "788e64613053f4d7f18c43039bbd47ac";
        city = city.replaceAll(" ","%20");

        String url= "https://api.openweathermap.org/data/2.5/weather?q="+city+"&units=metric&appid="+weatherAPIkey;
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    System.out.println(response);
                    JSONObject main = response.getJSONObject("main");
                    String temperature = String.valueOf(main.getDouble("temp"));
                    temperature = String.valueOf((int)Math.round(Double.parseDouble(temperature)));
                    weather_degrees.setText(temperature+" "+(char) 0x00B0+"C");

                    JSONArray arr = response.getJSONArray("weather");
                    JSONObject weather = arr.getJSONObject(0);
                    String type = weather.getString("main");
                    weather_type.setText(type);

                    String city_name = response.getString("name");
                    weather_city.setText(city_name);
                    weather_state.setText(state);

                    if(type.equals("Clouds")){
                        Picasso.get().load("https://csci571.com/hw/hw9/images/android/cloudy_weather.jpg").into(weather_back);
                    }
                    else if(type.equals("Clear")){
                        Picasso.get().load("https://csci571.com/hw/hw9/images/android/clear_weather.jpg").into(weather_back);
                    }
                    else if(type.equals("Snow")){
                        Picasso.get().load("https://csci571.com/hw/hw9/images/android/snowy_weather.jpg").into(weather_back);
                    }
                    else if(type.equals("Rain") || type.equals("Drizzle")){
                        Picasso.get().load("https://csci571.com/hw/hw9/images/android/rainy_weather.jpg").into(weather_back);
                    }
                    else if(type.equals("Thunderstorm")){
                        Picasso.get().load("https://csci571.com/hw/hw9/images/android/thunder_weather.jpg").into(weather_back);
                    }
                    else{
                        Picasso.get().load("https://csci571.com/hw/hw9/images/android/sunny_weather.jpg").into(weather_back);
                    }
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

    public void find_news(){
        String url= "https://webtechassignment-273804.wl.r.appspot.com/appHome";
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
                        String img_url="";
                        JSONObject article = arr.getJSONObject(i);
                        JSONObject fields = article.getJSONObject("fields");

                        if(fields.has("thumbnail")){
                            img_url = fields.getString("thumbnail");
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
                    adapter = new Adapter( getActivity() ,newsCardModelList );
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

        adapter = new Adapter( getActivity(), newsCardModelList );
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }
    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
//                new AlertDialog.Builder(this)
//                        .setTitle(R.string.title_location_permission)
//                        .setMessage(R.string.text_location_permission)
//                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//                                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
//                            }
//                        })
//                        .create()
//                        .show();
//            } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
//            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(getActivity(),
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        //Request location updates:
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 400, 1, locationListener);
//                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new HomeFragment()).commit();
                    }
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
        }
    }

}
