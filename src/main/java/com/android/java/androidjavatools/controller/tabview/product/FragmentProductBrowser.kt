//
//  FragmentProductBrowser.kt
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

package com.android.java.androidjavatools.controller.tabview.product

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
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState

abstract class FragmentProductBrowser : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater
        , container: ViewGroup?
        , savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            // Dispose of the Composition when the view's LifecycleOwner
            // is destroyed
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)

            setContent {
                viewContent()
            }
        }
    }

    @Composable
    abstract fun viewContent()

    @OptIn(ExperimentalPagerApi::class)
    @Composable
    fun infinitePager(
        images: IntArray
    ) {
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
                    .width(191.dp)
                    .height(130.dp)
            ) {
                Image(
                    contentDescription = "Contact profile picture"
                    , painter = painterResource(images[page % 5])
                    , contentScale = ContentScale.FillHeight
                    , modifier = Modifier
                        .align(Alignment.Center)
                )
            }
        }
    }
}