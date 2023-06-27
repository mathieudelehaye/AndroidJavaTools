//
//  MapView.kt
//
//  Created by Mathieu Delehaye on 25/06/2023.
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

package com.android.java.androidjavatools.controller.tabview.result.map

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.ViewCompositionStrategy
import com.android.java.androidjavatools.controller.tabview.search.SearchBox
import com.android.java.androidjavatools.databinding.FragmentMapBinding

class MapView(binding: FragmentMapBinding, search: SearchBox) {
    private val mBinding: FragmentMapBinding = binding
    private val mSearchBox: SearchBox = search

    fun show() {
        mBinding.mapSearchComposeView.apply {
            // Dispose of the Composition when the view's LifecycleOwner is destroyed
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)

            setContent {
                var hasQueryFocus by remember { mutableStateOf(false) }

                mSearchBox.show(
                    hasQueryFocus = hasQueryFocus
                    , onQueryFocusChange = {
                        hasQueryFocus = it
                    })
            }
        }
    }
}