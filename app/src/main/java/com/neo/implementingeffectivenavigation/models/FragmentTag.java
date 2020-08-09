package com.neo.implementingeffectivenavigation.models;

import android.nfc.Tag;

import androidx.fragment.app.Fragment;

/**
 * class to hold a fragment and it's tag
 */
public class FragmentTag {
    private Fragment mFragment;
    private String mTag;

    public FragmentTag(Fragment fragment, String tag) {
        mFragment = fragment;
        mTag = tag;
    }

    public Fragment getFragment() {
        return mFragment;
    }

    public void setFragment(Fragment fragment) {
        mFragment = fragment;
    }

    public String getTag() {
        return mTag;
    }

    public void setTag(String tag) {
        mTag = tag;
    }
}
