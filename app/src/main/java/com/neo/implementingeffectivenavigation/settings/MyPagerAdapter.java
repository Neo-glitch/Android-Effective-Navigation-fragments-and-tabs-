package com.neo.implementingeffectivenavigation.settings;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;


/**
 * adapter class for ViewPager implementation using fragments
 */
public class MyPagerAdapter extends FragmentPagerAdapter {
    private final ArrayList<Fragment> mFragmentList = new ArrayList<>();    // list to hold tab fragments

    public MyPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {  // get the fragment at the pos in foucus
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {   // defines the number of tabs to have
        return mFragmentList.size();
    }

    public void addFragment(Fragment fragment) {    // method to add fragment to ViewPager adapter fragment list
        mFragmentList.add(fragment);

    }
}
