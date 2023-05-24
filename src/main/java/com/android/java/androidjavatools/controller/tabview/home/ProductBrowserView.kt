//
//  ProductBrowserView.java
//
//  Created by Mathieu Delehaye on 19/05/2023.
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

package com.android.java.androidjavatools.controller.tabview.home

import android.app.Activity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.java.androidjavatools.R
import com.android.java.androidjavatools.controller.tabview.Navigator
import com.android.java.androidjavatools.controller.tabview.search.SearchBox
import com.android.java.androidjavatools.controller.template.FragmentWithSearch
import com.android.java.androidjavatools.databinding.FragmentHomeBinding
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState

class ProductBrowserView {
    private val mActivity: Activity?
    private val mContainer: FragmentWithSearch?
    private val mBinding: FragmentHomeBinding?
    private val mNavigatorManager: Navigator.NavigatorManager?

    constructor() {
        mActivity = null
        mContainer = null
        mBinding = null
        mNavigatorManager = null
    }

    constructor(activity: Activity, container: FragmentWithSearch, binding: FragmentHomeBinding) {
        mActivity = activity
        mContainer = container
        mBinding = binding
        mNavigatorManager = mActivity as Navigator.NavigatorManager
    }

    fun show() {
        mBinding!!.productBrowserView.apply {
            // Dispose of the Composition when the view's LifecycleOwner is destroyed
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)

            setContent {
                browserView()
            }
        }
    }

    @Composable
    fun browserView(
    ) {
        var searchBox = SearchBox(mActivity as Activity, mContainer!!, null)

        val images = intArrayOf(R.drawable.product01, R.drawable.product02, R.drawable.product03,
            R.drawable.product04, R.drawable.product05)

        Column {
            Spacer(modifier = Modifier.height(56.dp))
            searchBox.show()

            Spacer(modifier = Modifier.height(45.dp))
            Row {
                Spacer(modifier = Modifier.width(15.dp))
                browserButton("Free Samples", Color(0xFF3FA3BD))    // Light blue
                Spacer(modifier = Modifier.width(5.dp))
                browserButton("Free Products", Color(0xFFD0A038))     // Orange
            }

            Spacer(modifier = Modifier.height(5.dp))
            browserPager("Sustainable Brands", images)
            browserPager("Popular on ECOBEAUTY", images)
        }
    }

    @Composable
    fun browserButton(
        title: String
        , color: Color
    ) {
        Button(
            modifier = Modifier
                .width(width = 188.dp)
                .height(height = 60.dp)
            , shape = RoundedCornerShape(size = 15.dp)
            , border = BorderStroke(
                1.dp
                , Color.Black
            )
            , onClick = {
        }
            , colors = ButtonDefaults.buttonColors(
            backgroundColor = color
        )
        ) {
            Text(
                text = title
                , fontWeight = FontWeight.W400
                , fontSize = 22.sp
                , textAlign = TextAlign.Center
            )
        }
    }

    @Preview
    @Composable
    fun previewBrowserButton() {
        browserButton("Free Samples", Color(0xFF3FA3BD))
    }

    @Composable
    fun browserPager(
        title: String
        , images: IntArray)
    {
        Column {
            Spacer(modifier = Modifier.height(5.dp))
            Row {
                Spacer(modifier = Modifier.width(25.dp))
                Text(
                    text = title
                    , fontSize = 20.sp
                    , fontWeight = FontWeight.Bold
                    , modifier = Modifier
                        .padding(all = 4.dp)
                    , style = MaterialTheme.typography.h1
                )
            }
            infinitePager(images)
            Spacer(modifier = Modifier.height(10.dp))
            Divider(color = Color.LightGray, thickness = 2.dp)
        }
    }

    @Preview
    @Composable
    fun previewBrowserPager() {
        val images = intArrayOf(R.drawable.product01, R.drawable.product02, R.drawable.product03,
            R.drawable.product04, R.drawable.product05)
        browserPager("Browse by Functions", images)
    }

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