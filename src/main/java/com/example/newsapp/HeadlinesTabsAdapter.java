package com.example.newsapp;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class HeadlinesTabsAdapter extends FragmentPagerAdapter {

    private int numOfTabs;

    public HeadlinesTabsAdapter(FragmentManager fm, int numOfTabs) {
        super(fm);
        this.numOfTabs = numOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new WorldFragment();
            case 1:
                return new BusinessFragment();
            case 2:
                return new PoliticsFragment();
            case 3:
                return new SportsFragment();
            case 4:
                return new TechnologyFragment();
            case 5:
                return new ScienceFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return numOfTabs;
    }
}
