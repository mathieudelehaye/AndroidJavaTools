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

import android.app.Activity
import android.content.Context
import android.database.Cursor
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.BaseAdapter
import android.widget.ListView
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.viewinterop.AndroidViewBinding
import com.android.java.androidjavatools.Helpers
import com.android.java.androidjavatools.databinding.FragmentSuggestionBinding

abstract class FragmentSuggestion : FragmentWithSearch() {
    private var mSuggestionsAdapter: SuggestionsAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater
        , container: ViewGroup?
        , savedInstanceState: Bundle?
    ): View {
        val context = requireContext()
        val containerFragment = this
        val sb = SearchBox(activity as Activity, containerFragment)

        mSuggestionsAdapter = SuggestionsAdapter(context, sb, mConfiguration)
        setListAdapter(mSuggestionsAdapter!!)

        return ComposeView(context).apply {
            // Dispose of the Composition when the view's LifecycleOwner is destroyed
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)

            setContent {
                AndroidViewBinding(FragmentSuggestionBinding::inflate) {
                    suggestionSearchComposeView.setContent {
                        sb.show()
                    }
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.v("AndroidJavaTools", "Suggestion view created at timestamp: "
            + Helpers.getTimestamp())
        super.onViewCreated(view, savedInstanceState)
    }

    open fun setListAdapter(adapter: BaseAdapter) {
        val suggestionsList = requireView().findViewById<View>(R.id.suggestion_list) as ListView

        if (suggestionsList == null) {
            Log.e("AndroidJavaTools", "Cannot set the Suggestions adapter, as no suggestions list view")
        }
        suggestionsList.adapter = adapter
        suggestionsList.onItemClickListener = AdapterView.OnItemClickListener { adapterView: AdapterView<*>?, view: View?, position: Int, l: Long ->
            val query = (adapter.getItem(position) as Cursor).getString(1)
            mSearchView.setQuery(query)
            Log.v("AndroidJavaTools", "Search query set from tapped suggestion to: $query")

            // Start the search
            runSearch(query)
        }
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)

        if (isVisibleToUser) {
            Log.d("AndroidJavaTools", "Suggestions page becomes visible")
            if (mContext == null) {
                Log.w("AndroidJavaTools", "Cannot prepare the edit text view, as no context")
                return
            }

            // When the view is displayed, the keyboard is visible. So, give the focus to the edit text view
            Log.v("AndroidJavaTools", "Focus requested on the edit text view")
            //            mSearchQuery.requestFocus();

            // Show the keyboard
            val inputManager = mContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)

            // Clear the edit text
//            mSearchView.setQuery("");
        }
    }

    override fun searchAndDisplayItems() {}
}