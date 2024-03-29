//  SearchBox.kt
//
//  Created by Mathieu Delehaye on 8/05/2023.
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

package com.android.java.androidjavatools.controller.tabview.search

import android.annotation.SuppressLint
import android.app.Activity
import android.app.SearchManager
import android.app.SearchableInfo
import android.content.Context
import android.database.DataSetObserver
import android.text.Editable
import android.view.KeyEvent
import android.util.Log
import android.text.TextWatcher
import android.view.View
import android.widget.*
import android.widget.Filter.FilterListener
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidViewBinding
import com.android.java.androidjavatools.controller.template.Navigator.NavigatorManager
import com.android.java.androidjavatools.controller.tabview.result.list.FragmentResultList
import com.android.java.androidjavatools.controller.tabview.result.map.FragmentMap
import com.android.java.androidjavatools.controller.template.FragmentWithSearch
import com.android.java.androidjavatools.controller.template.ResultProvider
import com.android.java.androidjavatools.databinding.SearchViewBinding

class SearchBox: FilterListener {
    private val mActivity: Activity?
    private val mContainer: FragmentWithSearch?
    private val mSuggestionsContainer: Boolean?
    private val mResultListContainer: Boolean?
    private val mMapListContainer: Boolean?
    private val mNavigatorManager: NavigatorManager?
    private val mSearchManager: SearchManager?
    private val mSearchableConfig: SearchableInfo?
    private val mResultProvider: ResultProvider?
    private val mQueryHint: String?

    private var mObserver: DataSetObserver? = null
    private var mSuggestionsAdapter: CursorAdapter? = null
    private var mFilter: Filter? = null

    constructor(activity: Activity, container: FragmentWithSearch, adapter: SuggestionsAdapter?,
        provider: ResultProvider?) {

        mActivity = activity
        mContainer = container
        mSuggestionsContainer = mContainer is FragmentSuggestion
        mResultListContainer = mContainer is FragmentResultList
        mMapListContainer = mContainer is FragmentMap
        mNavigatorManager = mActivity as NavigatorManager
        mSearchManager = mActivity.getSystemService(Context.SEARCH_SERVICE) as SearchManager
        mSearchableConfig = mSearchManager.getSearchableInfo(mActivity.componentName)
        mQueryHint = mActivity.getString(mSearchableConfig.hintId)
        mSuggestionsAdapter = adapter
        mResultProvider = provider
    }

    @SuppressLint("SetTextI18n")
    @Composable
    fun show(
        query: String = "",
        onQueryChange: (String) -> Unit = {},
        hasQueryFocus: Boolean = false,
        onQueryFocusChange: (Boolean) -> Unit = {}) {

        AndroidViewBinding(
            factory = SearchViewBinding::inflate
            , modifier = Modifier
        ) {

            // Navigate back when pressing it
            searchViewBackButton.setOnClickListener { v: View? ->
                val navigatorManager = mActivity as NavigatorManager
                navigatorManager.navigator().back()
            }

            // Show it only if on the Suggestions or Result list page
            searchViewBackButtonLayout.visibility = if (mSuggestionsContainer!! || mResultListContainer!!)
                View.VISIBLE else View.GONE
            ;
            // Filter the suggestions and show the Clear button while editing text
            val textWatcher = object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val query: String = s.toString()

                    if (query == "") {
                        // If the query is empty, hide the Clear button
                        searchViewClearButton.visibility = View.GONE
                    } else {
                        searchViewClearButton.visibility = View.VISIBLE
                    }

                    onQueryChange(query)
                    performFiltering(query)
                }

                override fun afterTextChanged(s: Editable?) {}
            }
            searchViewQuery.addTextChangedListener(textWatcher)

            // Navigate to the Suggestions fragment when clicking on the edit text view
            searchViewQuery.onFocusChangeListener = object : View.OnFocusChangeListener {
                override fun onFocusChange(v: View?, hasFocus: Boolean) {
                    onQueryFocusChange(hasFocus)

                    if (!hasFocus) {
                        return
                    }
                    Log.d("AJT", "View $v has focus")

                    // Possibly show the Suggestions fragment
                    if (mSuggestionsContainer) {
                        // Return if already shown
                        Log.v("AJT", "View has already the focus")
                        return
                    }

                    // If this is the map fragment which originally asked for the search, save it
                    if (mMapListContainer!!) {
                        mResultProvider?.searchResultFragment = "map"
                    } else {
                        mResultProvider?.searchResultFragment = "list"
                    }

                    mNavigatorManager!!.navigator().showFragment("suggestion")
                }
            }

            if (searchViewQuery.text.toString() != query) {
                searchViewQuery.setText(query)
            }

            if (hasQueryFocus) {
                // If this is the Suggestions fragment, focus on the edit text
                searchViewQuery.requestFocus()
                onQueryFocusChange(true)
            }

            // Start the search when the Enter key is sent to the edit text view
            searchViewQuery.setOnKeyListener(object : View.OnKeyListener {
                override fun onKey(v: View?, i: Int, event: KeyEvent?): Boolean {
                    if (event != null) {
                        if (event.action == KeyEvent.ACTION_DOWN && event.keyCode == KeyEvent.KEYCODE_ENTER) {
                            val query: String = searchViewQuery.text.toString()
                            Log.v("AJT", "Search query validated by pressing enter: $query")

                            mContainer!!.runSearch(query, mResultProvider?.searchResultFragment)
                        }
                    }
                    return false
                }
            })

            // Set query hint
            searchViewQuery.hint = mQueryHint

            // Clear button
            searchViewClearButton.setOnClickListener {
                searchViewQuery.text.clear()
            }
        }
    }

    @Preview
    @Composable
    fun previewBrowserSearch() {
        var searchBox = SearchBox(mActivity!!, mContainer!!, null, null)
        val adapter = SuggestionsAdapter(mActivity, searchBox, searchBox.getSearchableConfig())
        searchBox.setSuggestionsAdapter(adapter)

        searchBox.show()
    }

    fun getSearchableConfig(): SearchableInfo {
        return mSearchableConfig!!
    }

    fun <T> setSuggestionsAdapter(adapter: T) where T : ListAdapter?, T : Filterable? {
        if (mObserver == null) {
            mObserver = object : DataSetObserver() {
            }
        } else {
            mSuggestionsAdapter?.unregisterDataSetObserver(mObserver)
        }

        mSuggestionsAdapter = adapter as CursorAdapter
        if (mSuggestionsAdapter != null) {
            mFilter = (mSuggestionsAdapter as Filterable).filter
            adapter.registerDataSetObserver(mObserver)
        } else {
            mFilter = null
        }
    }

    override fun onFilterComplete(count: Int) {
    }

    private fun performFiltering(query: String) {
        mFilter?.filter(query, this)
    }
}
