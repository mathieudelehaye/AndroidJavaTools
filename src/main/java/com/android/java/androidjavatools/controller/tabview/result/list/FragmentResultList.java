//
//  FragmentResultList.java
//
//  Created by Mathieu Delehaye on 21/01/2023.
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

package com.android.java.androidjavatools.controller.tabview.result.list;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import androidx.annotation.NonNull;
import com.android.java.androidjavatools.Helpers;
import com.android.java.androidjavatools.controller.tabview.result.detail.ResultDetailAdapter;
import com.android.java.androidjavatools.controller.template.FragmentHelpDialog;
import com.android.java.androidjavatools.controller.tabview.result.FragmentResult;
import com.android.java.androidjavatools.controller.template.ResultProvider;
import com.android.java.androidjavatools.controller.template.SearchProvider;
import com.android.java.androidjavatools.databinding.FragmentResultListBinding;
import com.android.java.androidjavatools.model.result.ResultItemInfo;
import com.android.java.androidjavatools.model.TaskCompletionManager;
import com.android.java.androidjavatools.R;

public abstract class FragmentResultList extends FragmentResult {
    protected FragmentResultListBinding mBinding;
    private ResultListAdapter mResultListAdapter;
    private boolean mIsViewVisible = false;

    public FragmentResultList(SearchProvider provider) {
        super(provider);
    }

    @Override
    public View onCreateView(
        LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState
    ) {
        super.onCreateView(inflater, container, savedInstanceState);

        mBinding = FragmentResultListBinding.inflate(inflater, container, false);

        var contentView = new ResultListView(getActivity(), this, mBinding, mSearchBox);
        contentView.show();

        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        Log.v("AJT", "Result list view created at timestamp: "
            + Helpers.getTimestamp());

        super.onViewCreated(view, savedInstanceState);

        changeSearchSwitch(ResultPageType.MAP);

        showHelp();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser) {
            mIsViewVisible = true;

            Log.d("AJT", "Result list view becomes visible");

            changeSearchSwitch(ResultPageType.MAP);

            showHelp();
        } else {
            mIsViewVisible = false;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinding = null;
    }

    @Override
    public void onStart() {
        super.onStart();

        updateSearchResults();
    }

    public void updateList() {
        var resultList = (ListView) getView().findViewById(R.id.result_list_view);

        mResultListAdapter = new ResultListAdapter(getContext(), mFoundResult);
        resultList.setAdapter(mResultListAdapter);

        resultList.setOnItemClickListener((adapterView, view, position, l) -> {
            final var resultItem = ((ResultItemInfo)mResultListAdapter.getItem(position));
            final var  itemAdapter = new ResultDetailAdapter(mContext, resultItem);
            showResultItem(itemAdapter);
        });

        final var resultProvider = (ResultProvider)getActivity();
        resultProvider.setSearchResult(mFoundResult);
    }

    @Override
    protected void searchAndDisplayItems() {

        // Search for the RP around the user
        searchForResults(new TaskCompletionManager() {
            @Override
            public void onSuccess() {
                Log.v("AJT", "Results received from database at timestamp: "
                    + Helpers.getTimestamp());

                updateList();

                mFoundResult.downloadImages(new TaskCompletionManager() {
                    @Override
                    public void onSuccess() {
                        mResultListAdapter.notifyDataSetChanged();

                        var selectionAdapter = mResultProvider.getSelectedItemAdapter();
                        if (selectionAdapter != null &&
                            ((ResultItemInfo)selectionAdapter.getItem(0)).mustShowImage()) {

                            // Show the selected item image, if not yet done and if this image was just downloaded
                            selectionAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onFailure() {
                    }
                });
            }

            @Override
            public void onFailure() {
            }
        });
    }

    private void showHelp() {

        if (mIsViewVisible && mSharedPref != null) {
            if (!Boolean.parseBoolean(mSharedPref.getString("list_help_displayed", "false"))) {
                mSharedPref.edit().putString("list_help_displayed", "true").apply();
                var dialogFragment = new FragmentHelpDialog(getString(R.string.list_help),
                    () -> null);
                dialogFragment.show(getChildFragmentManager(), "List help dialog");
            }
        }
    }
}