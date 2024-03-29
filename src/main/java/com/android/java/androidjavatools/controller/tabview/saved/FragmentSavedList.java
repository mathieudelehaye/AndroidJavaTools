//
//  FragmentSavedList.java
//
//  Created by Mathieu Delehaye on 29/04/2023.
//
//  AndroidJavaTools: A framework to develop Android apps with Java Technologies.
//
//  Copyright © 2023 Mathieu Delehaye. All rights reserved.
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

package com.android.java.androidjavatools.controller.tabview.saved;

import android.annotation.SuppressLint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import com.android.java.androidjavatools.controller.template.ResultProvider;
import com.android.java.androidjavatools.controller.template.SearchProvider;
import com.android.java.androidjavatools.databinding.FragmentSavedListBinding;
import com.android.java.androidjavatools.R;
import com.android.java.androidjavatools.model.SetWithImages;
import com.android.java.androidjavatools.model.TaskCompletionManager;
import com.android.java.androidjavatools.model.result.ResultItemInfo;
import com.google.firebase.firestore.FirebaseFirestore;
import org.jetbrains.annotations.NotNull;

public abstract class FragmentSavedList extends Fragment {
    protected FragmentSavedListBinding mBinding;
    protected FirebaseFirestore mDatabase;
    protected ResultProvider mResultProvider;
    private SearchProvider mSearchProvider;
    private Toolbar mToolbar;
    private Button mToolbarBackButton;
    private ListView mSavedItemList;
    private BaseAdapter mListAdapter;

    public FragmentSavedList(
        ResultProvider rProvider,
        SearchProvider sProvider) {

        mDatabase = FirebaseFirestore.getInstance();
        mResultProvider = rProvider;
        mSearchProvider = sProvider;
    }

    @Override
    public View onCreateView(
        LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState
    ) {
        mBinding = FragmentSavedListBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onViewCreated(@NonNull @NotNull View view,
        @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        mToolbar = mBinding.savedListViewToolbarLayout.findViewById(R.id.ajt_toolbar);
        mToolbarBackButton = mToolbar.findViewById(R.id.ajt_toolbar_back);

        mSavedItemList = mBinding.savedListView;
        mListAdapter = new SavedListAdapter(getContext(), mResultProvider);
        mSavedItemList.setAdapter(mListAdapter);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser) {
            Log.d("AJT", "Saved view becomes visible");

            updateSavedResults();
        }

        toggleToolbar(isVisibleToUser);
    }

    protected void setToolbarBackButtonVisibility(boolean visible) {
        mToolbarBackButton.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    protected void setToolbarBackgroundColor(int color) {
        mToolbar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(color)));
    }

    private void toggleToolbar(Boolean visible) {
        Log.v("AJT", "Saved page toolbar " + (visible ? "shown" : "hidden"));

        mToolbar.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    private void updateSavedResults() {
        SetWithImages resultToDisplay = mResultProvider.getSavedResult();

        if (resultToDisplay == null) {
            Log.e("AJT", "Cannot show the saved result as no set created at startup");
            return;
        }

        final var itemNumberToDownload = resultToDisplay.size();
        final int[] downloadedItemNumber = {0};

        mSearchProvider.resetSearchResults();

        for (String key : resultToDisplay.keySet()) {
            final var resultItemToDisplay = (ResultItemInfo)(resultToDisplay.get(key));

            mSearchProvider.searchResultForKey(key, mDatabase, new TaskCompletionManager() {
                @Override
                public void onSuccess() {
                    // Copy the result item read from DB to the one to display
                    final SetWithImages results = mSearchProvider.getSearchResults();

                    var readResultItem = (ResultItemInfo)(results.get(key));

                    resultItemToDisplay.setTitle(readResultItem.getTitle());
                    resultItemToDisplay.setDescription(readResultItem.getDescription());
                    resultItemToDisplay.setLocation(readResultItem.getLocation());

                    // Copy the URLs, so the images can be downloaded for all the keys
                    resultToDisplay.setImageUrl(key, results.getImageUrl(key));

                    downloadedItemNumber[0]++;

                    if (downloadedItemNumber[0] >= itemNumberToDownload) {
                        Log.d("AJT", "Last saved item data downloaded");

                        // Download the images for all the items
                        resultToDisplay.downloadImages(new TaskCompletionManager() {
                            @Override
                            public void onSuccess() {
                                mListAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onFailure() {
                            }
                        });
                    }
                }

                @Override
                public void onFailure() {
                }
            });
        }
    }
}
