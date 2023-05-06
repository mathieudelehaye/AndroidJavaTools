//
//  FragmentResultDetail.kt
//
//  Created by Mathieu Delehaye on 6/05/2023.
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

package com.android.java.androidjavatools.controller.tabview.sample

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import com.android.java.androidjavatools.R
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState

abstract class FragmentSampleBrowser : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            // Dispose of the Composition when the view's LifecycleOwner
            // is destroyed
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)

            val images = intArrayOf(R.drawable.beauty01, R.drawable.beauty02, R.drawable.beauty03,
                R.drawable.beauty04, R.drawable.beauty05)

            setContent {
                Column {
                    Spacer(modifier = Modifier.height(130.dp))
                    infinitePager(images)
                    Spacer(modifier = Modifier.height(40.dp))
                    infinitePager(images)
                    Spacer(modifier = Modifier.height(40.dp))
                    infinitePager(images)
                }
            }
        }
    }

    @OptIn(ExperimentalPagerApi::class)
    @Composable
    fun infinitePager(images: IntArray) {
        // Add padding around our message
        HorizontalPager(
            count = Int.MAX_VALUE,
            state = rememberPagerState(
                initialPage = Int.MAX_VALUE / 2
            )
        ) { page ->
            Box(
                modifier = Modifier
                    .background(Color.White)
                    .border(width = 2.dp, Color.DarkGray)
                    .width(251.dp)
                    .height(180.dp)
            ) {
                Image(
                    contentDescription = "Contact profile picture",
                    painter = painterResource(images[page % 5]),
                    contentScale = ContentScale.FillHeight,
                    modifier = Modifier
                    .align(Alignment.Center)
                )
            }
        }
    }
}