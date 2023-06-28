//
//  TabViewActivity.java
//
//  Created by Mathieu Delehaye on 1/12/2022.
//
//  AJT: An Android app to order and recycle cosmetics.
//
//  Copyright © 2022 Mathieu Delehaye. All rights reserved.
//
//
//  This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
//  Public License as published by
//  the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
//
//  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
//  warranty of MERCHANTABILITY or FITNESS
//  FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
//
//  You should have received a copy of the GNU Affero General Public License along with this program. If not, see
//  <https://www.gnu.org/licenses/>.

package com.android.java.androidjavatools.controller.tabview;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.android.java.androidjavatools.Helpers;
import com.android.java.androidjavatools.controller.tabview.result.FragmentResult;
import com.android.java.androidjavatools.controller.tabview.result.detail.FragmentResultDetail;
import com.android.java.androidjavatools.controller.tabview.result.detail.ResultDetailAdapter;
import com.android.java.androidjavatools.controller.tabview.result.list.FragmentResultList;
import com.android.java.androidjavatools.controller.tabview.result.map.FragmentMap;
import com.android.java.androidjavatools.controller.template.Navigator;
import com.android.java.androidjavatools.controller.template.ResultProvider;
import com.android.java.androidjavatools.controller.template.SearchHistoryManager;
import com.android.java.androidjavatools.model.*;
import com.android.java.androidjavatools.R;
import com.google.firebase.firestore.FirebaseFirestore;
import org.jetbrains.annotations.Nullable;
import java.util.*;

abstract public class TabViewActivity extends AppCompatActivity implements ActivityWithAsyncTask,
    SearchHistoryManager, ResultProvider, Navigator.NavigatorManager {

    protected SharedPreferences mSharedPref;
    protected FirebaseFirestore mDatabase;

    // Fragments: properties
    protected Navigator mNavigator;

    // Search: properties
    private final int mSavedListMaxSize = 100;
    private HashMap<String, ResultItemInfo> mPastResults = new HashMap<>();
    private HashMap<String, ResultItemInfo> mSavedRP = new HashMap<>();
    private ArrayList<String> mSavedRPKeys = new ArrayList<>();
    private CircularKeyBuffer<String> mPastRPKeys = new CircularKeyBuffer<>(2);
    private CircularKeyBuffer<String> mPastSearchQueries = new CircularKeyBuffer<>(4);
    private SearchResult mSearchResult = new SearchResult();
    private ResultDetailAdapter mSelectedItemAdapter;
    private String mSearchResultFragment = "list";

    // Search: getter-setter
    public ResultDetailAdapter getSelectedItemAdapter() {
        return mSelectedItemAdapter;
    }

    public void setSelectedItemAdapter(ResultDetailAdapter value) {
        final var selectedItem = (ResultItemInfo)(value.getItem(0));
        final String key = selectedItem.getKey();

        mSelectedItemAdapter = value;

        if (!mPastResults.containsKey(key)) {
            mPastResults.put(key, selectedItem);
        }

        if (!key.equals("")) {
            mPastRPKeys.add(key);
        }
    }

    public SearchResult getSearchResult() {
        return mSearchResult;
    }

    @Override
    public void setSearchResult(SearchResult result) {
        mSearchResult = result;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Helpers.startTimestamp();
        Log.i("AJT", "Main activity started");

        super.onCreate(savedInstanceState);

        if(this.getSupportActionBar()!=null) {
            this.getSupportActionBar().hide();
        }

        setContentView(R.layout.activity_main);

        // Only portrait orientation
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        mDatabase = FirebaseFirestore.getInstance();
        mSharedPref = getSharedPreferences(
            getString(R.string.lib_name), Context.MODE_PRIVATE);

        createNavigator();
    }

    protected abstract void createNavigator();

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    // Search: methods
    public int getPreviousQueryNumber() {
        return mPastSearchQueries.items();
    }

    @Override
    public String getPreviousSearchQuery(int index) {
        return mPastSearchQueries.getFromEnd(index);
    }

    @Override
    public int getPreviousResultNumber() {
        return mPastRPKeys.items();
    }

    @Override
    public ResultItemInfo getPreviousResultItem(int index) {
        final String key = mPastRPKeys.getFromEnd(index);

        return mPastResults.get(key);
    }

    @Override
    public Map<String, ResultItemInfo> getSavedResults() {
        return mSavedRP;
    }

    @Override
    public List<String> getSavedResultKeys() {
        return mSavedRPKeys;
    }

    @Override
    public boolean createSavedResult(ResultItemInfo value) {
        if (mSavedRPKeys.size() >= mSavedListMaxSize) {
            Log.w("AJT", "Cannot save more than " + mSavedListMaxSize + " RP");
            return false;
        }

        final String key = value.getKey();
        mSavedRP.put(key, value);
        mSavedRPKeys.add(key);

        return true;
    }

    @Override
    public boolean isSavedResult(String key) {
        return mSavedRP.containsKey(key);
    }

    @Override
    public void deleteSavedResult(String key) {
        mSavedRP.remove(key);

        for (int i = 0; i < mSavedRPKeys.size(); i++) {
            if (mSavedRPKeys.get(i).equals(key)) {
                mSavedRPKeys.remove(i);
                break;
            }
        }
    }

    @Override
    public void storeSearchQuery(@NonNull String query) {
        mPastSearchQueries.add(query);
    }

    // Fragments: methods

    public void toggleToolbar(Boolean visible) {
        Log.v("AJT", "Toolbar visibility toggled to " + visible);
        Toolbar mainToolbar = findViewById(R.id.main_activity_toolbar);
        mainToolbar.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    @Override
    public Navigator navigator() {
        return mNavigator;
    }

    @Override
    public void onNavigation(@NonNull String dest, @NonNull String orig) {
        switch (dest) {
            case "products":
            case "tab":
                switch (orig) {
                    case "help":
                    case "terms":
                        CollectionPagerAdapter.setPage(2);
                        break;
                    case "suggestion":
                        // Show toolbar when coming from the Suggestion page
                        toggleToolbar(true);

                        // Hide the keyboard
                        Helpers.toggleKeyboard(this, false);

                        break;
                    default:
                        break;
                }
                break;
            case "list":
                switch (orig) {
                    case "suggestion":
                        // Show toolbar when coming from the Suggestion page
                        toggleToolbar(true);

                        // Hide the keyboard
                        Helpers.toggleKeyboard(this, false);

                        // Update the results
                        ((FragmentResult)navigator().getFragment("list")).updateSearchResults();
                        break;
                    case "map":
                        // Copy the search
                        final var map = ((FragmentMap)navigator().getFragment("map"));
                        final var list
                            = ((FragmentResultList)navigator().getFragment("list"));

                        list.tryAndCopySearch(map.getSearchStart(), map.getFoundResult());

                        // Update the list
                        list.updateList();

                        break;
                    case "detail":
                    default:
                        // Do not refresh the result list
                        break;
                }
                break;
            case "map":
                switch (orig) {
                    case "list":
                        // Copy the search
                        final var map = ((FragmentMap)navigator().getFragment("map"));
                        final var list
                            = ((FragmentResultList)navigator().getFragment("list"));

                        map.tryAndCopySearch(list.getSearchStart(), list.getFoundResult());

                        // Update the map overlay
                        map.updateMapOverlay();

                        toggleToolbar(true);
                        Helpers.toggleKeyboard(this, false);

                        break;
                    case "suggestion":
                        // Show toolbar when coming from the Suggestion page
                        toggleToolbar(true);

                        // Hide the keyboard
                        Helpers.toggleKeyboard(this, false);

                        // Update the results
                        ((FragmentResult)navigator().getFragment("map")).updateSearchResults();

                        break;
                    case "detail":
                    default:
                        // Do not refresh the result list
                        break;
                }
                break;
            case "suggestion":
                // Hide toolbar when going to the Suggestion page
                toggleToolbar(false);

                // Show the keyboard
                Helpers.toggleKeyboard(this, true);

                break;
            case "detail":
                switch (orig) {
                    case "list":
                        // Pass the result item adapter to the detail fragment,
                        // so the latter can be updated by an observer
                        final var detailFragment =
                            ((FragmentResultDetail)(navigator().getFragment("detail")));
                        final var itemAdapter = getSelectedItemAdapter();
                        detailFragment.setAdapter(itemAdapter);
                    default:
                        break;
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void toggleTabSwiping(boolean enable) {
        mNavigator.toggleTabSwiping(enable);
    }

    @Nullable
    @Override
    public String getSearchResultFragment() {
        return mSearchResultFragment;
    }

    @Override
    public void setSearchResultFragment(@Nullable String s) {
        mSearchResultFragment = s;
    }

    protected boolean isNetworkAvailable() {
        var connectivityManager
            = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = (connectivityManager != null) ?
            connectivityManager.getActiveNetworkInfo() : null;
        return (activeNetworkInfo != null) && activeNetworkInfo.isConnected();
    }
}