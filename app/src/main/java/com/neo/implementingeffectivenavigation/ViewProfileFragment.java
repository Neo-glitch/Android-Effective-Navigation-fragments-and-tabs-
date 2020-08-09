package com.neo.implementingeffectivenavigation;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.neo.implementingeffectivenavigation.models.User;
import com.neo.implementingeffectivenavigation.util.PreferenceKeys;
import com.neo.implementingeffectivenavigation.util.Resources;

import java.util.HashSet;
import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;


public class ViewProfileFragment extends Fragment implements
        // needed for like button to work well
        OnLikeListener,
        View.OnClickListener {

    private static final String TAG = "ViewProfileFragment";

    //widgets
    private TextView mFragmentHeading, mName, mGender, mInterestedIn, mStatus;
    private LikeButton mLikeButton;     // love widget
    private RelativeLayout mBackArrow;
    private CircleImageView mProfileImage;

    //vars
    private User mUser;
    private IMainActivity mInterface;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {  // n.b: don't make UI calls here
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            mUser = bundle.getParcelable(getString(R.string.intent_user));
            Log.d(TAG, "onCreate: got incoming bundle: " + mUser.getName());
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mInterface = (IMainActivity) getActivity();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mInterface = null;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_profile, container, false);
        Log.d(TAG, "onCreateView: started.");
        mBackArrow = view.findViewById(R.id.back_arrow);
        mFragmentHeading = view.findViewById(R.id.fragment_heading);
        mProfileImage = view.findViewById(R.id.profile_image);
        mLikeButton = view.findViewById(R.id.heart_button);
        mName = view.findViewById(R.id.name);
        mGender = view.findViewById(R.id.gender);
        mInterestedIn = view.findViewById(R.id.interested_in);
        mStatus = view.findViewById(R.id.status);

        mLikeButton.setOnLikeListener(this);
        mBackArrow.setOnClickListener(this);

        checkIfConnected();
        setBackgroundImage(view);
        init();


        return view;
    }


    /**
     * sets the widgets with data from user obj
     */
    private void init() {
        if (mUser != null) {
            Glide.with(getActivity())
                    .load(mUser.getProfile_image())
                    .into(mProfileImage);     // sets url for image in load method into the imageView

            mName.setText(mUser.getName());
            mGender.setText(mUser.getGender());
            mInterestedIn.setText(mUser.getInterested_in());
            mStatus.setText(mUser.getStatus());
        }
    }

    /**
     * checks if user being viewed is in the users connection list
     */
    private void checkIfConnected() {
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        Set<String> savedNames = preferences.getStringSet(PreferenceKeys.SAVED_CONNECTIONS, new HashSet<String>());

        if (savedNames.contains(mUser.getName())) {  // if set contains this user
            mLikeButton.setLiked(true);
        } else {
            mLikeButton.setLiked(false);
        }

    }

    /**
     * sets background image for the profile screen
     *
     * @param view
     */
    private void setBackgroundImage(View view) {
        ImageView backgroundImageView = view.findViewById(R.id.background);
        Glide.with(getActivity())
                .load(Resources.BACKGROUND_HEARTS)     // drawable
                .into(backgroundImageView);

    }


    @Override
    public void liked(LikeButton likeButton) {
        Log.d(TAG, "liked: starts");
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor = preferences.edit();    // editor for editing stuff in the sharedPreferences

        // gets saves list of all savedConnections for user # set is like list but has unique values
        Set<String> savedNames = preferences.getStringSet(PreferenceKeys.SAVED_CONNECTIONS, new HashSet<String>());
        savedNames.add(mUser.getName());
        editor.putStringSet(PreferenceKeys.SAVED_CONNECTIONS, savedNames);    // passes the new Set with added value to editor for saving to the SharedPreferences
        editor.commit();


    }

    @Override
    public void unLiked(LikeButton likeButton) {
        Log.d(TAG, "unLiked: starts");

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor = preferences.edit();    // editor for editing stuff in the sharedPreferences

        Set<String> savedNames = preferences.getStringSet(PreferenceKeys.SAVED_CONNECTIONS, new HashSet<String>());
        savedNames.remove(mUser.getName());
        editor.remove(PreferenceKeys.SAVED_CONNECTIONS);    // removes the value assoc with this key
        editor.commit();

        editor.putStringSet(PreferenceKeys.SAVED_CONNECTIONS, savedNames);   // new set with removedUser
        editor.commit();

    }

    @Override
    public void onClick(View v) {
        Log.d(TAG, "onClick: called");
        if(v.getId() == R.id.back_arrow){
            Log.d(TAG, "onClick: navigating back");
            mInterface.onBackPressed();
        }
    }
}
















