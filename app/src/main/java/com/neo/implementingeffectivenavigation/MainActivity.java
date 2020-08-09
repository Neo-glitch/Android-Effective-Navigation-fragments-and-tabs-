package com.neo.implementingeffectivenavigation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.neo.implementingeffectivenavigation.models.FragmentTag;
import com.neo.implementingeffectivenavigation.models.Message;
import com.neo.implementingeffectivenavigation.models.User;
import com.neo.implementingeffectivenavigation.settings.SettingsFragment;
import com.neo.implementingeffectivenavigation.util.PreferenceKeys;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements IMainActivity, BottomNavigationViewEx.OnNavigationItemSelectedListener,
        NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "MainActivity";

    // constants
    public static final int HOME_FRAGMENT = 0;
    public static final int CONNECTIONS_FRAGMENT = 1;
    public static final int MESSAGES_FRAGMENT = 2;

    //widgets
    private BottomNavigationViewEx mBottomNavigationViewEx;
    private ImageView mHeaderImage;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;

    //fragments
    private HomeFragment mHomeFragment;
    private ViewProfileFragment mViewProfileFragment;
    private ChatFragment mChatFragment;
    private SettingsFragment mSettingsFragment;
    private AgreementFragment mAgreementFragment;
    private SavedConnectionsFragment mSavedConnectionsFragment;
    private MessagesFragment mMessagesFragment;

    //vars

    // for implementing own backStack
    private ArrayList<String> mFragmentTags = new ArrayList<>();      // list of all our fragmentTags
    private ArrayList<FragmentTag> mFragments = new ArrayList<>();    // holds list of fragments in mainActivity
    private int mExitCount = 0;      // resp for making app close if backStack is empty


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBottomNavigationViewEx = findViewById(R.id.bottom_nav_view);
        mNavigationView = findViewById(R.id.navigation_view);

        View headerView = mNavigationView.getHeaderView(0);     // gets ref to the view used as the header;
        mHeaderImage = headerView.findViewById(R.id.header_image);
        mDrawerLayout = findViewById(R.id.drawer_layout);

        isFirstLogin();
        initBottomNavigationView();
        setNavigationViewListener();
        setHeaderImage();
        init();
    }


    /**
     * hides other fragments apart from top fragment in backStack i.e fragment in view
     *
     * @param tagName
     */
    private void setFragmentVisibilities(String tagName) {
        if (tagName.equals(getString(R.string.tag_fragment_home)) || tagName.equals(getString(R.string.tag_fragment_saved_connections))
                || tagName.equals(getString(R.string.tag_fragment_messages))) { // fragments to show the bottomNav
            showBottomNavigation();
        } else{
            hideBottomNavigation();
        }

        for (int i = 0; i < mFragments.size(); i++) {
            if (tagName.equals(mFragments.get(i).getTag())) {
                // show the fragment TagName passed i,e the fragment in View
                FragmentTransaction transaction = getSupportFragmentManager()
                        .beginTransaction()
                        .show(mFragments.get(i).getFragment());
                transaction.commit();
            } else {
                //don't show
                FragmentTransaction transaction = getSupportFragmentManager()
                        .beginTransaction()
                        .hide(mFragments.get(i).getFragment());
                transaction.commit();
            }
        }
        setNavigationIcon(tagName);
    }

    /**
     * method to set the navHeaderImage using Glide
     */
    private void setHeaderImage() {
        Glide.with(this)
                .load(R.drawable.couple)
                .into(mHeaderImage);
    }

    private void hideBottomNavigation() {
        if (mBottomNavigationViewEx != null) {
            mBottomNavigationViewEx.setVisibility(View.GONE);
        }
    }

    private void showBottomNavigation() {
        if (mBottomNavigationViewEx != null) {
            mBottomNavigationViewEx.setVisibility(View.VISIBLE);
        }

    }

    // method to init bottom nav
    private void initBottomNavigationView() {
        Log.d(TAG, "initBottomNavigationView: starts");
//        mBottomNavigationViewEx.enableAnimation(false);  // disables the animation when switching tabs in bottomNav
        mBottomNavigationViewEx.setOnNavigationItemSelectedListener(this);

    }


    /**
     * init the Home fragment for activity
     */
    private void init() {
        if (mHomeFragment == null) {
            mHomeFragment = new HomeFragment();
            FragmentTransaction transaction = getSupportFragmentManager()
                    // fragment tag is used inorder to identify fragment transaction and easily identify the fragment
                    .beginTransaction().add(R.id.main_content_frame, mHomeFragment, getString(R.string.tag_fragment_home));
            transaction.commit();
            mFragmentTags.add(getString(R.string.tag_fragment_home));
            mFragments.add(new FragmentTag(mHomeFragment, getString(R.string.tag_fragment_home)));
        } else {
            mFragmentTags.remove(getString(R.string.tag_fragment_home));
            mFragmentTags.add(getString(R.string.tag_fragment_home));
        }
        setFragmentVisibilities(getString(R.string.tag_fragment_home));


    }

    private void setNavigationViewListener() {
        Log.d(TAG, "setNavigationViewListener: starting the navView Listener");
        NavigationView navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);
    }


    /**
     * shows alertDialog first time user logsin
     */
    private void isFirstLogin() {
        Log.d(TAG, "isFirstLogin: checking if this is first login");

        // gets ref to sharedPreferences of this app
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isFirstLogin = preferences.getBoolean(PreferenceKeys.FIRST_TIME_LOGIN, true);    // ret the pref value with key pair else return the defValue i.e true

        if (isFirstLogin) {
            Log.d(TAG, "isFirstLogin: launching alert dialog");
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setMessage(R.string.first_time_user_message)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Log.d(TAG, "onClick: closing of dialog, +ve btn pressed");

                            SharedPreferences.Editor editor = preferences.edit();     // editor to change values in sharedPreferences
                            editor.putBoolean(PreferenceKeys.FIRST_TIME_LOGIN, false);
                            editor.commit();
                            dialog.dismiss();
                        }
                    })
                    .setIcon(R.drawable.tabian_dating)
                    .setTitle(" ");     // if no title set at all, icon will not show on dialog

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
    }

    @Override
    public void inflateViewProfileFragment(User user) {
        if (mViewProfileFragment != null) {
            getSupportFragmentManager().beginTransaction().remove(mViewProfileFragment).commitAllowingStateLoss();     // removes the fragment if previously created# destroy it
        }

        mViewProfileFragment = new ViewProfileFragment();
        Bundle args = new Bundle();
        args.putParcelable(getString(R.string.intent_user), user);
        mViewProfileFragment.setArguments(args);

        FragmentTransaction transaction = getSupportFragmentManager()
                // to enable saving the fragment state
                .beginTransaction().add(R.id.main_content_frame, mViewProfileFragment, getString(R.string.tag_fragment_view_profile));
        transaction.commit();
        mFragmentTags.remove(getString(R.string.tag_fragment_view_profile));
        mFragments.add(new FragmentTag(mViewProfileFragment, getString(R.string.tag_fragment_view_profile)));
        setFragmentVisibilities(getString(R.string.tag_fragment_view_profile));


    }

    @Override
    public void onMessageSelected(Message message) {
        if (mChatFragment != null) {
            getSupportFragmentManager().beginTransaction().remove(mChatFragment).commitAllowingStateLoss();
        }
        mChatFragment = new ChatFragment();
        Bundle args = new Bundle();
        args.putParcelable(getString(R.string.intent_message), message);
        mChatFragment.setArguments(args);

        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.main_content_frame, mChatFragment, getString(R.string.tag_fragment_chat));
        transaction.commit();
        mFragmentTags.add(getString(R.string.tag_fragment_chat));
        mFragments.add(new FragmentTag(mMessagesFragment, getString(R.string.tag_fragment_chat)));
        setFragmentVisibilities(getString(R.string.tag_fragment_chat));

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // this same interface method is what handles bottomNav and NavigationView
        switch (item.getItemId()) {
            case R.id.home:   // in case, reset the back stack
                mFragmentTags.clear();
                mFragments = new ArrayList<>();
                init();    // does transaction for the home fragment
                break;
            case R.id.settings:
                Log.d(TAG, "onNavigationItemSelected: Home fragment");
                if (mSettingsFragment == null) {   // avoids building the fragment again if it's already created
                    mSettingsFragment = new SettingsFragment();
                    FragmentTransaction settingsTransaction = getSupportFragmentManager()
                            .beginTransaction().add(R.id.main_content_frame, mSettingsFragment, getString(R.string.tag_fragment_settings));
                    settingsTransaction.commit();
                    mFragmentTags.add(getString(R.string.tag_fragment_settings));   // adds tag to the list
                    mFragments.add(new FragmentTag(mSettingsFragment, getString(R.string.tag_fragment_settings)));     // adds the fragment class obj to list

                } else {  // moves fragment from lowerEntry in stack and move it to top # implementing backStack implementation
                    mFragmentTags.remove(getString(R.string.tag_fragment_settings));
                    mFragmentTags.add(getString(R.string.tag_fragment_settings));
                }
                setFragmentVisibilities(getString(R.string.tag_fragment_settings));
                break;
            case R.id.agreement:
                Log.d(TAG, "onNavigationItemSelected: Home fragment");
                if (mAgreementFragment == null) {
                    mAgreementFragment = new AgreementFragment();
                    FragmentTransaction agreementTransaction = getSupportFragmentManager()
                            .beginTransaction().add(R.id.main_content_frame, mAgreementFragment, getString(R.string.tag_fragment_agreement));
                    agreementTransaction.commit();
                    mFragmentTags.add(getString(R.string.tag_fragment_agreement));
                    mFragments.add(new FragmentTag(mAgreementFragment, getString(R.string.tag_fragment_agreement)));

                } else {
                    mFragmentTags.remove(getString(R.string.tag_fragment_agreement));
                    mFragmentTags.add(getString(R.string.tag_fragment_agreement));
                }
                setFragmentVisibilities(getString(R.string.tag_fragment_agreement));
                break;
            case R.id.bottom_nav_home:
                Log.d(TAG, "onNavigationItemSelected: Home fragment");
                if (mHomeFragment == null) {
                    mHomeFragment = new HomeFragment();
                    FragmentTransaction transaction = getSupportFragmentManager()
                            // fragment tag is used inorder to identify fragment transaction and easily identify the fragment
                            .beginTransaction().add(R.id.main_content_frame, mHomeFragment, getString(R.string.tag_fragment_home));
                    transaction.commit();
                    mFragmentTags.add(getString(R.string.tag_fragment_home));
                    mFragments.add(new FragmentTag(mHomeFragment, getString(R.string.tag_fragment_home)));
                } else {
                    mFragmentTags.remove(getString(R.string.tag_fragment_home));
                    mFragmentTags.add(getString(R.string.tag_fragment_home));
                }
                item.setChecked(true);  // checks the item selected
                setFragmentVisibilities(getString(R.string.tag_fragment_home));
                break;
            case R.id.bottom_nav_connections:
                Log.d(TAG, "onNavigationItemSelected: connections fragment");
                if (mSavedConnectionsFragment == null) {
                    mSavedConnectionsFragment = new SavedConnectionsFragment();
                    FragmentTransaction connectionTransaction = getSupportFragmentManager()
                            // fragment tag is used inorder to identify fragment transaction and easily identify the fragment
                            .beginTransaction().add(R.id.main_content_frame, mSavedConnectionsFragment, getString(R.string.tag_fragment_home));
                    connectionTransaction.commit();
                    mFragmentTags.add(getString(R.string.tag_fragment_saved_connections));
                    mFragments.add(new FragmentTag(mSavedConnectionsFragment, getString(R.string.tag_fragment_saved_connections)));
                } else {
                    mFragmentTags.remove(getString(R.string.tag_fragment_saved_connections));
                    mFragmentTags.add(getString(R.string.tag_fragment_saved_connections));
                }
                item.setChecked(true);  // checks the item selected
                setFragmentVisibilities(getString(R.string.tag_fragment_saved_connections));
                break;
            case R.id.bottom_nav_messages:
                Log.d(TAG, "onNavigationItemSelected: Message fragments");
                if (mMessagesFragment == null) {
                    mMessagesFragment = new MessagesFragment();
                    FragmentTransaction messageTransaction = getSupportFragmentManager()
                            // fragment tag is used inorder to identify fragment transaction and easily identify the fragment
                            .beginTransaction().add(R.id.main_content_frame, mMessagesFragment, getString(R.string.tag_fragment_home));
                    messageTransaction.commit();
                    mFragmentTags.add(getString(R.string.tag_fragment_messages));
                    mFragments.add(new FragmentTag(mMessagesFragment, getString(R.string.tag_fragment_messages)));
                } else {
                    mFragmentTags.remove(getString(R.string.tag_fragment_messages));
                    mFragmentTags.add(getString(R.string.tag_fragment_messages));
                }
                item.setChecked(true);  // checks the item selected
                setFragmentVisibilities(getString(R.string.tag_fragment_messages));
                break;
        }
        mDrawerLayout.closeDrawer(GravityCompat.START);    // closes the drawer layout after one of this cases is true
        return false;
    }

    @Override
    public void onBackPressed() {
        // remove fragment at top and show the one directly below it.. but check if there's another fragment below top
        int backStackCount = mFragmentTags.size();
        if (backStackCount > 1) {   // true if a fragment after fragment in focus
            String topFragmentTag = mFragmentTags.get(backStackCount - 1);
            String newTopFragmentTag = mFragmentTags.get(backStackCount - 2);   // the one after the top or previous fragment in view

            setFragmentVisibilities(newTopFragmentTag);
            mFragmentTags.remove(topFragmentTag);

            mExitCount = 0;        // reset the count
        } else if (backStackCount == 1) {
            String topFragment = mFragmentTags.get(backStackCount - 1);
            if (topFragment.equals(getString(R.string.tag_fragment_home))) {
                mHomeFragment.scrollToTop();
                mExitCount++;
                Toast.makeText(this, " click again to exit the app", Toast.LENGTH_SHORT).show();
            } else {
                mExitCount++;
                Toast.makeText(this, " click again to exit the app", Toast.LENGTH_SHORT).show();
            }
        }
        if (mExitCount >= 2) { // true when user taps backButton 1 more times after message is shown
            super.onBackPressed();
        }
    }

    /**
     * method to fix the bottomNav icon not being marked in fragment in focus when back button is pressed
     */
    private void setNavigationIcon(String tagName) {
        Menu menu = mBottomNavigationViewEx.getMenu();
        MenuItem menuItem = null;

        if (tagName.equals(getString(R.string.tag_fragment_home))) {
            Log.d(TAG, "setNavigationIcon: HomeFragment in focus");
            menuItem = menu.getItem(HOME_FRAGMENT);   // the get home_fragment menu item since first item in bottomNav
            menuItem.setChecked(true);
        } else if (tagName.equals(getString(R.string.tag_fragment_saved_connections))) {
            Log.d(TAG, "setNavigationIcon: ConnectionsFragment in focus");
            menuItem = menu.getItem(CONNECTIONS_FRAGMENT);   // the get home_fragment menu item since first item in bottomNav
            menuItem.setChecked(true);
        } else if (tagName.equals(getString(R.string.tag_fragment_messages))) {
            Log.d(TAG, "setNavigationIcon: MessagesFragment in focus");
            menuItem = menu.getItem(MESSAGES_FRAGMENT);   // the get home_fragment menu item since first item in bottomNav
            menuItem.setChecked(true);
        }

    }
}