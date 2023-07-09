//
//  FragmentBase.java
//
//  Created by Mathieu Delehaye on 19/05/2023.
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

package com.android.java.androidjavatools.controller.template

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.unit.dp
import com.android.java.androidjavatools.Helpers
import com.android.java.androidjavatools.controller.template.Navigator.NavigatorManager

abstract class FragmentCompose : FragmentWithStart() {
    protected var mActivity : Activity? = null
    protected var mNavigatorManager: NavigatorManager? = null

    override fun onCreateView(
        inflater: LayoutInflater
        , container: ViewGroup?
        , savedInstanceState: Bundle?
    ): View {
        mActivity = requireActivity()
        mNavigatorManager = mActivity!! as NavigatorManager

        return ComposeView(mActivity!!).apply {

            // Dispose of the Composition when the view's LifecycleOwner is destroyed
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)

            setContent {
                Column {
                    Spacer(modifier = Modifier.height(56.dp))   // toolbar
                    contentView()
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.v("AJT", "Base view created at timestamp: "
            + Helpers.getTimestamp())
        mNavigatorManager = mActivity as NavigatorManager
        super.onViewCreated(view, savedInstanceState)
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)

        if (isVisibleToUser) {
            Log.d("AJT", "Compose page becomes visible")
        } else {
            Log.d("AJT", "Compose page becomes hidden")
        }
    }

    @Composable
    protected abstract fun contentView()
}