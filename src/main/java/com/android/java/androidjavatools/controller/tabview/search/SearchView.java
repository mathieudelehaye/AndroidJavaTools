//  SearchView.java
//
//  Created by Mathieu Delehaye on 9/04/2023.
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
//  You should have received a copy of the GNU Affero General Public License along with this program. If not, see
//  <https://www.gnu.org/licenses/>.


package com.android.java.androidjavatools.controller.tabview.search;

import android.app.Activity;
import android.app.SearchableInfo;
import android.content.Context;
import android.database.DataSetObserver;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import androidx.appcompat.widget.LinearLayoutCompat;
import com.android.java.androidjavatools.R;
import com.android.java.androidjavatools.controller.tabview.Navigator;

public abstract class SearchView extends LinearLayoutCompat implements Filter.FilterListener {
    private Activity mActivity;
    private Context mContext;
    private View mContainerView;
    private EditText mQuery;
    private DataSetObserver mObserver;
    private SearchableInfo mConfiguration;
    private CursorAdapter mAdapter;
    private Filter mFilter;

    public SearchView(Context context) {
        this(context, null);
    }

    public SearchView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SearchView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mContext = context;

        final var inflater = (LayoutInflater) context.getSystemService(
            Context.LAYOUT_INFLATER_SERVICE);
        mContainerView = inflater.inflate(R.layout.search_view, this, true);

        init();
    }

    public void setSearchableInfo(SearchableInfo config) {
        mConfiguration = config;
        final var queryHint = mActivity.getString(mConfiguration.getHintId());
        mQuery.setHint(queryHint);
    }

    public <T extends ListAdapter & Filterable> void setAdapter(T adapter) {
        if (mObserver == null) {
            mObserver = new DataSetObserver() {
                @Override
                public void onChanged() {
                    super.onChanged();
                }

                @Override
                public void onInvalidated() {
                    super.onInvalidated();
                }
            };
        } else if (mAdapter != null) {
            mAdapter.unregisterDataSetObserver(mObserver);
        }

        mAdapter = (CursorAdapter) adapter;

        if (mAdapter != null) {
            //noinspection unchecked
            mFilter = ((Filterable) mAdapter).getFilter();
            adapter.registerDataSetObserver(mObserver);
        } else {
            mFilter = null;
        }
    }

    @Override
    public void onFilterComplete(int count) {
    }

    private void init() {
        if (mContext == null) {
            Log.e("AndroidJavaTools", "Error with search view layout, as no context");
            return;
        }

        mActivity = (Activity)mContext;
        if (mActivity == null) {
            Log.e("AndroidJavaTools", "Error with search view layout, as no activity");
            return;
        }

        // Set up the Back button
        final var back = (Button)mContainerView.findViewById(R.id.search_view_back_button);
        if (back == null) {
            Log.e("AndroidJavaTools", "Error with search view layout, as no Back button");
            return;
        }
        back.setOnClickListener(v -> {
            // Hide the keyboard
            final var inputManager = (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(mContainerView.getWindowToken(), 0);

            final var navigatorManager = (Navigator.NavigatorManager)mActivity;
            navigatorManager.navigator().back();
        });

        // Set up the Clear button
        final var clear = (ImageButton)mContainerView.findViewById(R.id.search_view_clear_button);
        if (clear == null) {
            Log.e("AndroidJavaTools", "Error with search view layout, as no Clear button");
            return;
        }
        clear.setVisibility(GONE);
        clear.setOnClickListener(v -> mQuery.getText().clear());

        // Set up the Query edit text
        mQuery = mContainerView.findViewById(R.id.search_view_query);
        if (mQuery == null) {
            Log.e("AndroidJavaTools", "Error with search view layout, as no Query edit text");
            return;
        }
        mQuery.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mQuery.getText().toString().equals("")) {
                    // If the query is empty, hide the Clear button
                    clear.setVisibility(GONE);
                } else {
                    clear.setVisibility(VISIBLE);
                }

                performFiltering();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void performFiltering() {
        if (mFilter == null) {
            return;
        }

        mFilter.filter(mQuery.getText(), this);
    }
}