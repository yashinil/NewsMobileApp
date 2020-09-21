package com.example.newsapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;

public class HeadlinesFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.headlines_fragment,container,false);

        TabLayout tabLayout = view.findViewById(R.id.headlines_tabs);
//        TabItem head_world = view.findViewById(R.id.headlines_world);
//        TabItem head_business = view.findViewById(R.id.headlines_business);
//        TabItem head_politics = view.findViewById(R.id.headlines_politics);
//        TabItem head_sports = view.findViewById(R.id.headlines_sports);
//        TabItem head_technology = view.findViewById(R.id.headlines_technology);
//        TabItem head_science = view.findViewById(R.id.headlines_science);
        final ViewPager viewPager = view.findViewById(R.id.headlines_view);

        HeadlinesTabsAdapter headlinesTabsAdapter = new HeadlinesTabsAdapter(getChildFragmentManager(),tabLayout.getTabCount());
        viewPager.setAdapter(headlinesTabsAdapter);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        return view;
    }
}
