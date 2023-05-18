//
//  FragmentSuggestion.kt
//
//  Created by Mathieu Delehaye on 11/05/2023.
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

package com.android.java.androidjavatools.controller.tabview.search

import android.content.Context
import android.database.Cursor
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.viewinterop.AndroidViewBinding
import com.android.java.androidjavatools.databinding.FragmentSuggestionBinding
import com.android.java.androidjavatools.Helpers

abstract class FragmentSuggestion : FragmentWithSearch() {
    private var mQuery: MutableState<String> = mutableStateOf("")
    private var mHasQueryFocus: MutableState<Boolean> = mutableStateOf(false)

    override fun onCreateView(
        inflater: LayoutInflater
        , container: ViewGroup?
        , savedInstanceState: Bundle?
    ): View {
        val activity = requireActivity()
        mSearchView = SearchBox(activity, this, null)
        val adapter = SuggestionsAdapter(activity, mSearchView, mSearchView.getSearchableConfig())
        mSearchView.setSuggestionsAdapter(adapter)

        return ComposeView(activity).apply {

            // Dispose of the Composition when the view's LifecycleOwner is destroyed
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)

            setContent {
                Column {
                    var query by remember { mQuery }
                    var focus by remember { mHasQueryFocus }

                    mSearchView.show(
                        query = query
                        , onQueryChange = {
                            query = it
                        }
                        , hasQueryFocus = focus
                        , onQueryFocusChange = {
                        })

                    AndroidViewBinding(
                        factory = FragmentSuggestionBinding::inflate,
                        modifier = Modifier,
                        update = {

                            suggestionList.adapter = adapter

                            suggestionList.onItemClickListener = AdapterView.OnItemClickListener {
                                _: AdapterView<*>?, _: View?, position: Int, _: Long ->

                                query = (adapter.getItem(position) as Cursor).getString(1)
                                Log.d("AndroidJavaTools",
                                    "Search query set from tapped suggestion to: $query")
                            }
                        })
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.v("AndroidJavaTools", "Suggestion view created at timestamp: "
            + Helpers.getTimestamp())
        super.onViewCreated(view, savedInstanceState)
        mContext = activity as Context
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)

        if (isVisibleToUser) {
            Log.d("AndroidJavaTools", "Suggestions page becomes visible")

            // Give the focus to the edit text view
            mHasQueryFocus.value = true

            // Clear the edit text content
            mQuery.value = ""
        } else {
            Log.d("AndroidJavaTools", "Suggestions page becomes hidden")

            // Remove the focus from the edit text view
            mHasQueryFocus.value = false
        }
    }

    override fun searchAndDisplayItems() {}
}