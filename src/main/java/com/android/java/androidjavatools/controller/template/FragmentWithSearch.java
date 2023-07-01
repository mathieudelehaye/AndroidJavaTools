//
//  FragmentWithSearch.java
//
//  Created by Mathieu Delehaye on 22/01/2023.
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
//  You should have received a copy of the GNU Affero General Public License along with this program. If not,
//  see <https://www.gnu.org/licenses/>.

package com.android.java.androidjavatools.controller.template;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.android.java.androidjavatools.controller.tabview.result.detail.ResultDetailAdapter;
import com.android.java.androidjavatools.controller.tabview.result.list.FragmentResultList;
import com.android.java.androidjavatools.R;
import com.android.java.androidjavatools.controller.tabview.search.SearchBox;
import com.android.java.androidjavatools.model.result.ResultItemInfo;
import com.google.firebase.firestore.FirebaseFirestore;
import org.jetbrains.annotations.NotNull;

public abstract class FragmentWithSearch extends Fragment {
    protected FirebaseFirestore mDatabase;
    protected SharedPreferences mSharedPref;
    protected Context mContext;
    protected SearchHistoryManager mHistoryManager;
    protected ResultProvider mResultProvider;
    protected Navigator.NavigatorManager mNavigatorManager;
    protected SearchBox mSearchBox;

    protected abstract void searchAndDisplayItems();

    @Override
    public View onCreateView(
        @NotNull LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState
    ) {
        mContext = requireActivity();
        mResultProvider = (ResultProvider)mContext;
        mSearchBox = new SearchBox((Activity) mContext, this, null, mResultProvider);
        return null;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mDatabase = FirebaseFirestore.getInstance();
        mSharedPref = mContext.getSharedPreferences(
            getString(R.string.lib_name), Context.MODE_PRIVATE);

        mHistoryManager = (SearchHistoryManager)mContext;
        mNavigatorManager = (Navigator.NavigatorManager)mContext;
    }

    public void runSearch(String query, String resultFragment) {
        FragmentResultList.setResultQuery(query);
        mHistoryManager.storeSearchQuery(query);
        mNavigatorManager.navigator().showFragment(resultFragment);
    }

    protected void showResultItem(ResultDetailAdapter adapter) {
        final String title = ((ResultItemInfo)adapter.getItem(0)) .getTitle();
        Log.d("AJT", "Result item to show: " + title);
        mResultProvider.setSelectedItemAdapter(adapter);
        mNavigatorManager.navigator().showFragment("detail");
    }
}
