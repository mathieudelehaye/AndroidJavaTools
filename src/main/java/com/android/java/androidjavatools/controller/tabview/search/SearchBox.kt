//  SearchBox.kt
//
//  Created by Mathieu Delehaye on 8/05/2023.
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

package com.android.java.androidjavatools.controller.tabview.search

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Filter.FilterListener
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidViewBinding
import com.android.java.androidjavatools.databinding.SearchViewBinding

class SearchBox: FilterListener {
    @Composable
    fun show() {
        AndroidViewBinding(SearchViewBinding::inflate) {
            // Edit text
            val textWatcher = object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (searchViewQuery.text.toString().equals("")) {
                        // If the query is empty, hide the Clear button
                        searchViewClearButton.visibility = View.GONE
                    } else {
                        searchViewClearButton.visibility = View.VISIBLE
                    }

                    //performFiltering()
                }

                override fun afterTextChanged(s: Editable?) {}
            }
            searchViewQuery.addTextChangedListener(textWatcher)

            // Clear button
            searchViewClearButton.visibility = View.GONE
            searchViewClearButton.setOnClickListener {
                v: View? -> searchViewQuery.text.clear()
            }
        }
    }

    @Preview
    @Composable
    fun previewBrowserSearch() {
        var searchBox = SearchBox()
        searchBox.show()
    }

    override fun onFilterComplete(count: Int) {
        TODO("Not yet implemented")
    }
}
