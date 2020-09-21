package com.example.newsapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BookmarksFragment extends Fragment {
    RecyclerView recyclerView;
    public static BookmarkAdapter adapter;
    SharedPreferences store;
    SharedPreferences.Editor editor;
    public static TextView no_bookmark;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bookmarks_fragment,container,false);

        store = getActivity().getApplicationContext().getSharedPreferences("com.example.newsapp.sharedPreferences", Context.MODE_PRIVATE);
        editor = store.edit();
        recyclerView = view.findViewById(R.id.bookmarks_card_list);
        no_bookmark = view.findViewById(R.id.no_bookmarks);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity().getApplicationContext(),2);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(),gridLayoutManager.getOrientation()));
        find_bookmarks();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        find_bookmarks();
    }

    public void find_bookmarks(){
        final List<NewsCardModel> newsCardModelList = new ArrayList<>();

        Map<String, ?> allEntries = store.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            Log.d("map values", entry.getKey() + ": " + entry.getValue().toString());
            String identification = entry.getKey();
            String full_data = entry.getValue().toString();
            NewsCardModel ncw = null;
            Gson gson =new Gson();
            if(full_data!=null){
                ncw = gson.fromJson(full_data,NewsCardModel.class);
            }
            newsCardModelList.add(new NewsCardModel(identification, ncw.getImage_resource(), ncw.getTitle(), ncw.getDate(), ncw.getSection(), ncw.getTwitter_url()));
        }
        if(newsCardModelList.size() == 0){
            no_bookmark.setText("No Bookmarked Articles");
        }else{
            no_bookmark.setText("");
        }
        adapter = new BookmarkAdapter( getActivity() ,newsCardModelList );
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
}
