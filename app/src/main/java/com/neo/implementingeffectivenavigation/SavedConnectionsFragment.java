package com.neo.implementingeffectivenavigation;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.neo.implementingeffectivenavigation.models.User;
import com.neo.implementingeffectivenavigation.util.PreferenceKeys;
import com.neo.implementingeffectivenavigation.util.Users;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;


/**
 * similar to homeFragment
 */
public class SavedConnectionsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

    private static final String TAG = "SavedConnFragment";

    //constants
    private static final int NUM_GRID_COLUMNS = 2;

    //widgets
    private MainRecyclerViewAdapter mRecyclerViewAdapter;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    //vars
    private ArrayList<User> mUsers = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_saved_connections, container, false);
        Log.d(TAG, "onCreateView: started.");

        mRecyclerView = view.findViewById(R.id.recycler_view);
        mSwipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        getConnections();

        return view;
    }

    /**
     * gets all the saved connections made by user i.e all people user has liked or loved
     */
    private void getConnections(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        // gets the list connection(names) from sharedPreferences
        Set<String> savedNames = preferences.getStringSet(PreferenceKeys.SAVED_CONNECTIONS, new HashSet<String>());

        Users users = new Users();
        if(mUsers != null){
            mUsers.clear();
        }
        for(User user: users.USERS){
            if(savedNames.contains(user.getName())){
                mUsers.add(user);
            }
        }
        if(mRecyclerViewAdapter == null){
            initRecyclerView();
        }
    }

    private void initRecyclerView(){
        Log.d(TAG, "initRecyclerView: init recyclerview.");
        mRecyclerViewAdapter = new MainRecyclerViewAdapter(getActivity(), mUsers);
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(NUM_GRID_COLUMNS, LinearLayoutManager.VERTICAL);
        mRecyclerView.setAdapter(mRecyclerViewAdapter);
        mRecyclerView.setLayoutManager(staggeredGridLayoutManager);
    }


    @Override
    public void onRefresh() {
        getConnections();
        onItemsLoadComplete();
    }

    /**
     * notifies adapter that data set might have changed and tells refresh listener to stop refreshing
     */
    private void onItemsLoadComplete(){
        mRecyclerViewAdapter.notifyDataSetChanged();
        mSwipeRefreshLayout.setRefreshing(false);
    }
}
