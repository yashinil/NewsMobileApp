package com.example.newsapp;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import okio.GzipSource;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {
    private List<NewsCardModel> newsCardsList;

    Context context;
    SharedPreferences store;
    SharedPreferences.Editor editor;

    public Adapter( Context context ,List<NewsCardModel> newsCardsList){
        this.context = context;
        this.newsCardsList = newsCardsList;
        store = context.getSharedPreferences("com.example.newsapp.sharedPreferences", Context.MODE_PRIVATE);
        editor = store.edit();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.horizontal_card, parent, false);
        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final String identification = newsCardsList.get(position).getIdentification();
        final String img_resource = newsCardsList.get(position).getImage_resource();
        final String title = newsCardsList.get(position).getTitle();
        String date = newsCardsList.get(position).getDate();
        String section = newsCardsList.get(position).getSection();
        final String twitter_url = newsCardsList.get(position).getTwitter_url();

        final String bookmark_data;
        Gson gson = new Gson();
        bookmark_data = gson.toJson(newsCardsList.get(position));

        try {
            holder.setData(identification, img_resource, title, date, section);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        holder.news_card.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
//                Toast.makeText( v.getContext(), "Long clicked the card",Toast.LENGTH_SHORT).show();
                openDialog(v, identification, img_resource, title, twitter_url, bookmark_data, position);
                return true;
            }
        });

        holder.news_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText( v.getContext(), identification,Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(v.getContext(), DetailCardActivity.class);
                intent.putExtra("identification",identification);
                v.getContext().startActivity(intent);
            }
        });

        holder.bookmark_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String check = store.getString(identification, "");
                if(check == ""){
                    String add_message = '"' + title + '"' + " was added to bookmarks";
                    Toast.makeText( v.getContext(), add_message,Toast.LENGTH_SHORT).show();
                    holder.bookmark_icon.setImageResource(R.drawable.ic_bookmark2);
                    editor.putString(identification, bookmark_data);
                    editor.commit();
                }
                else {
                    String remove_message = '"' + title + '"' + " was removed from bookmarks";
                    Toast.makeText( v.getContext(), remove_message,Toast.LENGTH_SHORT).show();
                    holder.bookmark_icon.setImageResource(R.drawable.ic_bookmark);
                    editor.remove(identification);
                    editor.apply();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return newsCardsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView news_card_image;
        private TextView news_card_title;
        private TextView news_card_details;
        private CardView news_card;
        private ImageView bookmark_icon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            news_card_image = itemView.findViewById(R.id.news_card_image);
            news_card_title = itemView.findViewById(R.id.news_card_title);
            news_card_details = itemView.findViewById(R.id.news_card_data);
            news_card = itemView.findViewById(R.id.news_card);
            bookmark_icon = itemView.findViewById(R.id.news_card_bookmark);
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        public void setData(String identification, String img_resource, String title, String date, String section) throws ParseException {
            Picasso.get().load(img_resource).into(news_card_image);
            news_card_title.setText(title);

            String details = "";
            String DATE_FORMAT_PATTERN = "yyyy-MM-dd'T'HH:mm:ss'Z'";
            SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT_PATTERN);
            format.setTimeZone(TimeZone.getTimeZone("Z"));

            Date zpast = format.parse(date);
            ZoneId zoneId = ZoneId.of("America/Los_Angeles");
            ZonedDateTime zonedDateTime = zpast.toInstant().atZone(zoneId);
            Date date1 = Date.from(zonedDateTime.toInstant());
            Date date2 = new Date();

            double diff = date2.getTime() - date1.getTime();
            diff = diff/1000.0;
            if(diff < 60){
                details = (int)diff + "s ago | ";
            }
            else if(diff/60 < 60){
                details = (int)diff/60 + "m ago | ";
            }
            else if(diff/3600 < 72){
                details = (int)diff/3600 + "h ago | ";
            }
            else{
                details = (int)diff/86400 + "d ago | ";
            }

            details = details + section;
            news_card_details.setText(details);

            String check = store.getString(identification, "");
            if(check == ""){
                bookmark_icon.setImageResource(R.drawable.ic_bookmark);
            }
            else{
                bookmark_icon.setImageResource(R.drawable.ic_bookmark2);
            }
        }
    }

    public void openDialog(View v, final String identification, String img_url, final String title, final String twitter_url, final String bookmark_data, final int position){
        Dialog dialog = new Dialog(v.getContext());
        dialog.setContentView(R.layout.dialog_box);

        ImageView dialog_image = dialog.findViewById(R.id.dialog_box_image);
        Picasso.get().load(img_url).into(dialog_image);

        TextView dialog_title = dialog.findViewById(R.id.dialog_box_title);
        dialog_title.setText(title);

        ImageView dialog_bookmark_icon = dialog.findViewById(R.id.dialog_box_bookmark);
        String check = store.getString(identification, "");
        if(check == ""){
            dialog_bookmark_icon.setImageResource(R.drawable.ic_bookmark);
        }
        else{
            dialog_bookmark_icon.setImageResource(R.drawable.ic_bookmark2);
        }

        final ImageView dialog_bookmark = dialog.findViewById(R.id.dialog_box_bookmark);

        dialog_bookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String check = store.getString(identification, "");
                if(check == ""){
                    String add_message = '"' + title + '"' + " was added to bookmarks";
                    Toast.makeText( v.getContext(), add_message,Toast.LENGTH_SHORT).show();
                    dialog_bookmark.setImageResource(R.drawable.ic_bookmark2);
                    editor.putString(identification, bookmark_data);
                    editor.commit();
                    notifyItemChanged(position);
                }
                else {
                    String remove_message = '"' + title + '"' + " was removed from bookmarks";
                    Toast.makeText( v.getContext(), remove_message,Toast.LENGTH_SHORT).show();
                    dialog_bookmark.setImageResource(R.drawable.ic_bookmark);
                    editor.remove(identification);
                    editor.apply();
                    notifyItemChanged(position);
                }
            }
        });
        //put onclick listeners for twitter and bookmarks button clicks

        ImageView dialog_twitter_icon = dialog.findViewById(R.id.dialog_box_twitter);

        dialog_twitter_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://twitter.com/intent/tweet?text=Check%20Out%20This%20Link:%20"+twitter_url+"&hashtags=CSCI571NewsSearch";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                context.startActivity(i);
            }
        });

        dialog.show();
    }
}
