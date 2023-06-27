//
//  FragmentHome.java
//
//  Created by Mathieu Delehaye on 25/03/2023.
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

package com.android.java.androidjavatools.controller.tabview.home;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import com.android.java.androidjavatools.controller.tabview.result.detail.ResultDetailAdapter;
import com.android.java.androidjavatools.controller.template.FragmentWithSearch;
import com.android.java.androidjavatools.controller.template.ResultProvider;
import com.android.java.androidjavatools.controller.template.SearchHistoryManager;
import com.android.java.androidjavatools.databinding.FragmentHomeBinding;
import com.google.firebase.firestore.FirebaseFirestore;

public abstract class FragmentHome extends FragmentWithSearch {
    protected FragmentHomeBinding mBinding;
    protected Context mContext;
    protected FirebaseFirestore mDatabase;
    protected SearchHistoryManager mHistoryManager;
    protected ResultProvider mResultProvider;

    public FragmentHome(ResultProvider provider) {
        super();
        mResultProvider = provider;
    }

    @Override
    public View onCreateView(
        LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState
    ) {
        super.onCreateView(inflater, container, savedInstanceState);

        mBinding = FragmentHomeBinding.inflate(inflater, container, false);

        var contentView = new ProductBrowserView(getActivity(), this, mBinding, mResultProvider, mSearchBox);
        contentView.show();

        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mDatabase = FirebaseFirestore.getInstance();
        mContext = getContext();

        mHistoryManager = (SearchHistoryManager)getActivity();
        mResultProvider = (ResultProvider) getActivity();

        updateRecentResults();
        updateRecentSearches();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            Log.d("AJT", "Home view becomes visible");

            updateRecentResults();
            updateRecentSearches();
        }
    }

    public void updateRecentResults() {
        final int buttonNumber = 2;

        if (mContext == null) {
            Log.w("AJT", "Cannot update the recent searches, as no context");
            return;
        }

        final var results = mResultProvider.getPreviousResultNumber();

        for(int i = 0; i < buttonNumber; i++) {
            var historyButton = (i == 0) ?
                mBinding.rpHistoryButton1a :
                mBinding.rpHistoryButton1b;

            if (i < results) {
                // Update the RP history buttons
                final var result = mResultProvider.getPreviousResultItem(i);
                if (result == null) {
                    continue;
                }

                final String key = result.getTitle();
                final String title = result.getTitle();

                Log.v("AJT", "updateRecentRP: index = " + i + ", key = " + key
                    + ", title = " + title);

                historyButton.setText(title.substring(0, Math.min(key.length(), 15)));

                historyButton.setOnClickListener( v -> {
                    final var  adapter = new ResultDetailAdapter(mContext, result);
                    showResultItem(adapter);
                });

                historyButton.setVisibility(View.VISIBLE);
            } else {
                // Hide the button if no related RP
                historyButton.setVisibility(View.GONE);
            }
        }
    }

    public void updateRecentSearches() {
        final int buttonNumber = 4;

        if (mContext == null) {
            Log.w("AJT", "Cannot update the recent searches, as no context");
            return;
        }

        final var queries = mHistoryManager.getPreviousQueryNumber();

        for(int i = 0; i < buttonNumber; i++) {
            var historyButton =
                (i == 0) ? mBinding.searchHistoryButton1a :
                (i == 1) ? mBinding.searchHistoryButton1b :
                (i == 2) ? mBinding.searchHistoryButton2a :
                mBinding.searchHistoryButton2b;

            if (i < queries) {
                // Update the search history buttons
                final String query = mHistoryManager.getPreviousSearchQuery(i);

                Log.v("AJT", "updateRecentSearches: age = " + i + ", query = " + query);

                historyButton.setText(query.substring(0, Math.min(query.length(), 15)));
                historyButton.setOnClickListener(v -> runSearch(query, "list"));
                historyButton.setVisibility(View.VISIBLE);
            } else {
                // Hide the button if no related query
                historyButton.setVisibility(View.GONE);
            }
        }
    }
}
