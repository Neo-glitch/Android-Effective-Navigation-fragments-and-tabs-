package com.neo.implementingeffectivenavigation.models;


import androidx.fragment.app.Fragment;


/**
 * class to hold a fragment and it's tag
 */
public class FragmentTag {

    private Fragment fragment;
    private String tag;

    public FragmentTag(Fragment fragment, String tag) {
        this.fragment = fragment;
        this.tag = tag;
    }

    public FragmentTag() {
    }

    public Fragment getFragment() {
        return fragment;
    }

    public void setFragment(Fragment fragment) {
        this.fragment = fragment;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
