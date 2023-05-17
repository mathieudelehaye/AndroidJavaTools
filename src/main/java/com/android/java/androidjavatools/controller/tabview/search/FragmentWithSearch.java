//
//  FragmentWithSearch.java
//
//  Created by Mathieu Delehaye on 22/01/2023.
//
//  AndroidJavaTools: A framework to develop Android apps in Java.
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

package com.android.java.androidjavatools.controller.tabview.search;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.android.java.androidjavatools.controller.tabview.Navigator;
import com.android.java.androidjavatools.controller.tabview.result.list.FragmentResultList;
import com.android.java.androidjavatools.R;
import com.android.java.androidjavatools.model.ResultItemInfo;
import com.android.java.androidjavatools.model.SearchResult;
import com.android.java.androidjavatools.model.TaskCompletionManager;
import com.google.firebase.firestore.FirebaseFirestore;
import org.osmdroid.util.GeoPoint;
import java.util.List;
import java.util.Map;

public abstract class FragmentWithSearch extends Fragment {
    public interface SearchHistoryManager {
        int getPreviousQueryNumber();
        String getPreviousSearchQuery(int index);
        void storeSearchQuery(@NonNull String query);
    }

    public interface SearchProvider {
        SearchResult getSearchResults();
        void searchGeoPointResults(GeoPoint searchStart, double searchRadiusInCoordinate,
            FirebaseFirestore database, TaskCompletionManager... cbManager);
    }

    public interface ResultProvider {
        SearchResult getSearchResult();
        void setSearchResult(SearchResult result);
        ResultItemInfo getSelectedResultItem();
        void setSelectedResultItem(ResultItemInfo value);
        int getPreviousResultNumber();
        ResultItemInfo getPreviousResultItem(int index);
        Map<String, ResultItemInfo> getSavedResults();
        List<String> getSavedResultKeys();
        boolean createSavedResult(ResultItemInfo value);
        boolean isSavedResult(String key);
        void deleteSavedResult(String key);
    }

    protected FirebaseFirestore mDatabase;
    protected SharedPreferences mSharedPref;
    protected Context mContext;
    protected SearchHistoryManager mHistoryManager;
    protected ResultProvider mResultProvider;
    protected Navigator.NavigatorManager mNavigatorManager;
    protected SearchBox mSearchView;

    protected abstract void searchAndDisplayItems();

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mDatabase = FirebaseFirestore.getInstance();
        mContext = view.getContext();
        mSharedPref = mContext.getSharedPreferences(
            getString(R.string.lib_name), Context.MODE_PRIVATE);

        mHistoryManager = (SearchHistoryManager)mContext;
        mResultProvider = (ResultProvider)mContext;
        mNavigatorManager = (Navigator.NavigatorManager)mContext;
    }

    public void runSearch(String query) {
        FragmentResultList.setResultQuery(query);
        mHistoryManager.storeSearchQuery(query);
        mNavigatorManager.navigator().showFragment("list");
    }

    protected void showResult(ResultItemInfo item) {
        final String title = item.getTitle();
        Log.d("AndroidJavaTools", "Result to show: " + title);
        mResultProvider.setSelectedResultItem(item);
        mNavigatorManager.navigator().showFragment("detail");
    }
}
