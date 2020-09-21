package com.example.newsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.min;

public class MainActivity extends AppCompatActivity {
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private AutoSuggestAdapter autoSuggestAdapter;
    private Handler handler;
    private static final int TRIGGER_AUTO_COMPLETE = 100;
    private static final long AUTO_COMPLETE_DELAY = 300;
    BottomNavigationView bottomNavigationView;

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
//            }
            return false;
        } else {
            return true;
        }
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            return;
//        }
//        locationManager.requestLocationUpdates(provider, 400, 1, this);
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            return;
//        }
//        locationManager.removeUpdates(this);
//    }

//    @Override
//    public void onLocationChanged(Location location) {
//        lat = location.getLatitude();
//        lng = location.getLongitude();
//        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
//        List<Address> addresses = null;
//        try {
//            addresses = geocoder.getFromLocation(lat, lng, 1);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
////        String add = addresses.get(0).getAddressLine(0);
//        city = addresses.get(0).getSubAdminArea();
//        state = addresses.get(0).getAdminArea();
//        country = addresses.get(0).getCountryName();
//        Log.v("Location info: country", String.valueOf(addresses));
//    }

//    @Override
//    public void onStatusChanged(String provider, int status, Bundle extras) {
//    }
//
//    @Override
//    public void onProviderEnabled(String provider) {
//    }
//    @Override
//    public void onProviderDisabled(String provider) {
//    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        //Request location updates:
//                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 400, 1, this);
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new HomeFragment()).commit();
                    }
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_navigation);

//

        if(checkLocationPermission()){
//            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
//            locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

//            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new HomeFragment(city, state, country)).commit();
//
//            if (savedInstanceState == null){
//                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new HomeFragment(city, state, country)).commit();
//
//            }
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                //Request location updates:
//                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 400, 1, this);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new HomeFragment()).commit();
            }
        }
        else {
//            while (!checkLocationPermission()) {
//                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
//            }
//            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        }
//        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new HomeFragment(city, state, country)).commit();

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment = null;

                switch (item.getItemId()){
                    case R.id.home:
                        fragment = new HomeFragment();
                        break;
                    case R.id.headlines:
                        fragment = new HeadlinesFragment();
                        break;
                    case R.id.trending:
                        fragment = new TrendingFragment();
                        break;
                    case R.id.bookmarks:
                        fragment = new BookmarksFragment();
                        break;
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,fragment).commit();
                return true;
            }
        });

        //        Log.v("provider info:",provider);
//        System.out.println(locationManager);

//        Location location = locationManager.getLastKnownLocation(provider);
//        System.out.println(location);
//        if (location != null) {
//            Log.v("Location Info", "Location achieved!");
//        } else {
//            Log.v("Location Info", "No location :(");
//        }

//        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,5000, 0,this);

//        lat = location.getLatitude();
//        lng = location.getLongitude();
//        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
//        List<Address> addresses = null;
//        try {
//            addresses = geocoder.getFromLocation(lat, lng, 1);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
////        String add = addresses.get(0).getAddressLine(0);
//        city = addresses.get(0).getSubAdminArea();
//        state = addresses.get(0).getAdminArea();
//        country = addresses.get(0).getCountryName();
    }

    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_bar,menu);
        MenuItem item = menu.findItem(R.id.search_toolbar);
        SearchView searchView = (SearchView) item.getActionView();

        final SearchView.SearchAutoComplete searchAutoComplete = (SearchView.SearchAutoComplete) searchView.findViewById(R.id.search_src_text);
        searchAutoComplete.setDropDownBackgroundResource(R.color.colorWhite);

        autoSuggestAdapter = new AutoSuggestAdapter(this, android.R.layout.simple_dropdown_item_1line);
        searchAutoComplete.setThreshold(3);
        searchAutoComplete.setAdapter(autoSuggestAdapter);

//        final AppCompatAutoCompleteTextView autoCompleteTextView = findViewById(R.id.);
//        final TextView selectedText = findViewById(R.id.selected_item);
        //Setting up the adapter for AutoSuggest
//        autoCompleteTextView.setThreshold(2);
//        autoCompleteTextView.setAdapter(autoSuggestAdapter);
        searchAutoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                String query = (String) parent.getItemAtPosition(position);
                searchAutoComplete.setText("" + query);
                Intent intent = new Intent(getApplication(), SearchActivity.class);
                intent.putExtra("keyword",query);

                System.out.println("inside onitemclick"+query);
                startActivity(intent);
                }
             });

        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if (msg.what == TRIGGER_AUTO_COMPLETE) {
                    if (!TextUtils.isEmpty(searchAutoComplete.getText())) {
                        makeApiCall(searchAutoComplete.getText().toString());
                    }
                }
                return false;
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Intent intent = new Intent(getApplication(), SearchActivity.class);
                intent.putExtra("keyword", query);
                System.out.println("inside onquery text submit"+query);
                startActivity(intent);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                handler.removeMessages(TRIGGER_AUTO_COMPLETE);
                handler.sendEmptyMessageDelayed(TRIGGER_AUTO_COMPLETE,AUTO_COMPLETE_DELAY);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    private void makeApiCall(String text) {
        AutoSuggest.make(this, text, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                System.out.println(response);
                //parsing logic, please change it as per your requirement
                List<String> stringList = new ArrayList<>();
                try {
//                    JSONObject responseObject = new JSONObject(response);
                    JSONArray array = response.getJSONArray("suggestionGroups");
                    JSONObject res = array.getJSONObject(0);
                    JSONArray suggestions = res.getJSONArray("searchSuggestions");
                    for (int i = 0; i < min(5,suggestions.length()); i++) {
                        JSONObject row = suggestions.getJSONObject(i);
                        stringList.add(row.getString("displayText"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //IMPORTANT: set data here and notify
                autoSuggestAdapter.setData(stringList);
                autoSuggestAdapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
    }
}
