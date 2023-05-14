//
//  ResultListView.java
//
//  Created by Mathieu Delehaye on 14/05/2023.
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

package com.android.java.androidjavatools.controller.tabview.result.list

import android.app.Activity
import androidx.compose.runtime.*
import androidx.compose.ui.platform.ViewCompositionStrategy
import com.android.java.androidjavatools.controller.tabview.Navigator.NavigatorManager
import com.android.java.androidjavatools.controller.tabview.search.FragmentWithSearch
import com.android.java.androidjavatools.controller.tabview.search.SearchBox
import com.android.java.androidjavatools.databinding.FragmentResultListBinding


class ResultListView {
    private val mActivity: Activity
    private val mContainer: FragmentWithSearch
    private val mBinding: FragmentResultListBinding
    private val mNavigatorManager: NavigatorManager

    constructor(activity: Activity, container: FragmentWithSearch, binding: FragmentResultListBinding) {
        mActivity = activity
        mContainer = container
        mBinding = binding
        mNavigatorManager = mActivity as NavigatorManager
    }

    fun show() {
        mBinding.resultListSearchComposeView.apply {
            // Dispose of the Composition when the view's LifecycleOwner is destroyed
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)

            setContent {
                val sb = SearchBox(mActivity, mContainer, null)

                var hasQueryFocus by remember { mutableStateOf(false) }

                sb.show(
                    hasQueryFocus = hasQueryFocus
                    , onQueryFocusChange = {
                    hasQueryFocus = it
                    if (hasQueryFocus) {
                        mNavigatorManager.navigator().showFragment("suggestion")
                    }
                })
            }
        }
    }
}
