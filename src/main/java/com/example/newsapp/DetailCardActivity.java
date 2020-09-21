package com.example.newsapp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.text.HtmlCompat;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;


public class DetailCardActivity extends AppCompatActivity {
    String identity;
    String title;
    String bookmark_data;
    String twitter_url;

    JSONObject bookmarkJson = new JSONObject();
    LinearLayout progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_card);

        Toolbar toolbar = findViewById(R.id.detail_card_navbar);
        setSupportActionBar(toolbar);

        progressBar = findViewById(R.id.circle_progress_bar);
        progressBar.setVisibility(View.VISIBLE);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getIncomingIntent();
    }

    private void getIncomingIntent(){
        if(getIntent().hasExtra("identification")){
            identity = getIntent().getStringExtra("identification");

            String url = "https://webtechassignment-273804.wl.r.appspot.com/bigCard?identification="+identity+"&api=true";
            JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        String img_url = "";
                        JSONObject data = response.getJSONObject("data");
                        JSONObject resp = data.getJSONObject("response");
                        JSONObject content = resp.getJSONObject("content");
                        JSONObject blocks = content.getJSONObject("blocks");
                        JSONObject main = blocks.getJSONObject("main");
                        JSONArray elements = main.getJSONArray("elements");
                        if( elements.length()>0 && elements.getJSONObject(0).has("assets") && elements.getJSONObject(0).getJSONArray("assets").length()>0 ){
                            JSONObject element = elements.getJSONObject(0);
                            JSONArray assets = element.getJSONArray("assets");
                            JSONObject asset = assets.getJSONObject(0);
                            img_url = asset.getString("file");
                        }
                        if(img_url == "" || img_url == " " || img_url == null){
                            img_url = "https://assets.guim.co.uk/images/eada8aa27c12fe2d5afa3a89d3fbae0d/fallback-logo.png";
                        }
                        twitter_url = content.getString("webUrl");
                        title = content.getString("webTitle");
                        String date = content.getString("webPublicationDate");
                        String section = content.getString("sectionName");
                        String description = "";

                        JSONArray body = blocks.getJSONArray("body");

                        for(int i=0;i<body.length();i++){
                            JSONObject bodyitem = body.getJSONObject(i);
                            String line = bodyitem.getString("bodyHtml");
                            description = description + line;
                        }

                        Gson gson = new Gson();
                        NewsCardModel newsCardModel = new NewsCardModel(identity , img_url, title, date, section, twitter_url);
                        bookmark_data = gson.toJson(newsCardModel);

                        setData(twitter_url, img_url, date, section, description);

                    } catch (JSONException | ParseException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {}
            }
            );
            RequestQueue queue = Volley.newRequestQueue(this);
            queue.add(req);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setData(final String twitter_url, String img_url, String date, String section, String description ) throws ParseException {
        getSupportActionBar().setTitle(title);

        ImageView detail_image = findViewById(R.id.detail_card_image);
        Picasso.get().load(img_url).into(detail_image);

        TextView detail_title = findViewById(R.id.detail_card_title);
        detail_title.setText(title);

        TextView detail_section = findViewById(R.id.detail_card_section);
        detail_section.setText(section);

        TextView detail_desc = findViewById(R.id.detail_card_description);
        detail_desc.setText(HtmlCompat.fromHtml(description, 0));

        TextView detail_date = findViewById(R.id.detail_card_date);
//        String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

        String DATE_FORMAT_PATTERN = "yyyy-MM-dd'T'HH:mm:ss'Z'";
        SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT_PATTERN);
        format.setTimeZone(TimeZone.getTimeZone("Z"));

        Date zpast = format.parse(date);
        ZoneId zoneId = ZoneId.of("America/Los_Angeles");
        ZonedDateTime zonedDateTime = zpast.toInstant().atZone(zoneId);
        Date date1 = Date.from(zonedDateTime.toInstant());
        date = date1.toString();
        String new_date = date.substring(8,10) + " " + date.substring(4,7) + " " + date.substring(24);

        detail_date.setText(new_date);

        TextView detail_url = findViewById(R.id.detail_card_link);

        SpannableString content = new SpannableString("View Full Article");
        content.setSpan(new UnderlineSpan(), 0, "View Full Article".length(), 0);
        detail_url.setText(content);

        detail_url.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(twitter_url));
                startActivity(browserIntent);
            }
        });
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail_actionbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem detail_bookmark_icon = menu.findItem(R.id.detail_nav_bookmark);
        SharedPreferences store = getSharedPreferences("com.example.newsapp.sharedPreferences", Context.MODE_PRIVATE);

        String check = store.getString(identity, "");
        if(check == ""){
            detail_bookmark_icon.setIcon(R.drawable.ic_bookmark);
        }
        else{
            detail_bookmark_icon.setIcon(R.drawable.ic_bookmark2);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        SharedPreferences store = getSharedPreferences("com.example.newsapp.sharedPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = store.edit();
        switch (item.getItemId()){
            case R.id.detail_nav_bookmark:
                String check = store.getString(identity, "");
                if(check == ""){
                    String add_message = '"' + title + '"' + " was added to bookmarks";
                    Toast.makeText( this, add_message,Toast.LENGTH_SHORT).show();
                    item.setIcon(R.drawable.ic_bookmark2);
                    System.out.println(bookmark_data);
                    editor.putString(identity, bookmark_data);
                    editor.commit();
                    if(HomeFragment.adapter != null){
                        HomeFragment.adapter.notifyDataSetChanged();
                    }
                    if(BusinessFragment.adapter != null){
                        BusinessFragment.adapter.notifyDataSetChanged();
                    }
                    if(PoliticsFragment.adapter != null){
                        PoliticsFragment.adapter.notifyDataSetChanged();
                    }
                    if(TechnologyFragment.adapter != null){
                        TechnologyFragment.adapter.notifyDataSetChanged();
                    }
                    if(SportsFragment.adapter != null){
                        SportsFragment.adapter.notifyDataSetChanged();
                    }
                    if(ScienceFragment.adapter != null){
                        ScienceFragment.adapter.notifyDataSetChanged();
                    }
                    if(WorldFragment.adapter != null){
                        WorldFragment.adapter.notifyDataSetChanged();
                    }
                    if(SearchActivity.adapter != null){
                        SearchActivity.adapter.notifyDataSetChanged();
                    }
                }
                else{
                    String remove_message = '"' + title + '"' + " was removed from bookmarks";
                    Toast.makeText( this, remove_message,Toast.LENGTH_SHORT).show();
                    item.setIcon(R.drawable.ic_bookmark);
                    editor.remove(identity);
                    editor.apply();
                    if(HomeFragment.adapter != null){
                        HomeFragment.adapter.notifyDataSetChanged();
                    }
                    if(BusinessFragment.adapter != null){
                        BusinessFragment.adapter.notifyDataSetChanged();
                    }
                    if(PoliticsFragment.adapter != null){
                        PoliticsFragment.adapter.notifyDataSetChanged();
                    }
                    if(TechnologyFragment.adapter != null){
                        TechnologyFragment.adapter.notifyDataSetChanged();
                    }
                    if(SportsFragment.adapter != null){
                        SportsFragment.adapter.notifyDataSetChanged();
                    }
                    if(ScienceFragment.adapter != null){
                        ScienceFragment.adapter.notifyDataSetChanged();
                    }
                    if(WorldFragment.adapter != null){
                        WorldFragment.adapter.notifyDataSetChanged();
                    }
                    if(SearchActivity.adapter != null){
                        SearchActivity.adapter.notifyDataSetChanged();
                    }
                }
                return true;
            case R.id.detail_nav_twitter:
                String url = "https://twitter.com/intent/tweet?text=Check%20Out%20This%20Link:%20"+twitter_url+"&hashtags=CSCI571NewsSearch";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
