//
//  ProductBrowserView.kt
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

package com.android.java.androidjavatools.controller.tabview.home

import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.java.androidjavatools.R
import com.android.java.androidjavatools.controller.tabview.product.FragmentProductSelection
import com.android.java.androidjavatools.controller.tabview.search.SearchBox
import com.android.java.androidjavatools.controller.template.*
import com.android.java.androidjavatools.databinding.FragmentHomeBinding
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState

open class ProductBrowserView {
    protected val mNavigatorManager: Navigator.NavigatorManager?
    protected val mSearchBox: SearchBox
    private val mActivity: Activity?
    private val mContainer: FragmentWithSearch?
    private val mBinding: FragmentHomeBinding?
    private val mResultProvider: ResultProvider

    constructor(activity: Activity, container: FragmentWithSearch, binding: FragmentHomeBinding,
        provider : ResultProvider, search : SearchBox) {

        mActivity = activity
        mContainer = container
        mBinding = binding
        mNavigatorManager = mActivity as Navigator.NavigatorManager
        mResultProvider = provider
        mSearchBox = search
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
    open fun browserView(
    ) {
        val images = intArrayOf(R.drawable.product01, R.drawable.product02, R.drawable.product03,
            R.drawable.product04, R.drawable.product05)

        Column {
            Spacer(modifier = Modifier.height(56.dp))
            mSearchBox.show()

            Spacer(modifier = Modifier.height(45.dp))
            Row {
                Spacer(modifier = Modifier.width(15.dp))
                // Light blue color
                buttonWithText("Free Samples", Color(0xFF3FA3BD)) {}
                Spacer(modifier = Modifier.width(5.dp))
                // Orange color
                buttonWithText("Free Products", Color(0xFFD0A038)) {}
            }

            Spacer(modifier = Modifier.height(5.dp))
            browserPager("Sustainable Brands", images, ContentScale.FillHeight) {
                FragmentProductSelection.setFilterField("sustainable")
                mNavigatorManager!!.navigator().showFragment("products")
            }
            browserPager("Popular products", images, ContentScale.FillHeight) {
                FragmentProductSelection.setFilterField("popular")
                mNavigatorManager!!.navigator().showFragment("products")
            }
        }
    }

    @Preview
    @Composable
    fun previewBrowserButton() {
        buttonWithText("Free Samples", Color(0xFF3FA3BD)) {}
    }

    @Composable
    fun browserPager(
        title: String
        , images: IntArray
        , scaleType: ContentScale
        , onClick: () -> Unit
    ) {
        Column {
            Spacer(modifier = Modifier.height(5.dp))
            Row {
                Spacer(modifier = Modifier
                    .width(16.dp)
                )
                Text(
                    text = title
                    , fontSize = 20.sp
                    , fontWeight = FontWeight.Bold
                    , modifier = Modifier
                        .padding(all = 4.dp)
                    , style = MaterialTheme.typography.h1
                )
            }
            infinitePager(images, scaleType) {
                onClick()
            }
            Spacer(modifier = Modifier.height(10.dp))
        }
    }

    @Preview
    @Composable
    fun previewBrowserPager() {
        val images = intArrayOf(R.drawable.product01, R.drawable.product02, R.drawable.product03,
            R.drawable.product04, R.drawable.product05)
        browserPager("Browse by Functions", images, ContentScale.FillHeight) {}
    }

    @OptIn(ExperimentalPagerApi::class, ExperimentalMaterialApi::class)
    @Composable
    fun infinitePager(
        images: IntArray,
        scaleType : ContentScale,
        onClick: () -> Unit
    ) {
        // Add padding around our message
        HorizontalPager(
            count = Int.MAX_VALUE,
            state = rememberPagerState(
                initialPage = Int.MAX_VALUE / 2
            )
        ) { page ->
            Card(
                onClick = {
                    onClick()
                }
                , modifier = Modifier
                    .background(Color.White)
                    .border(width = 2.dp, Color.DarkGray)
                    .width(191.dp)
                    .height(130.dp)
            ) {
                Image(
                    contentDescription = "Contact profile picture"
                    , painter = painterResource(images[page % images.size])
                    , contentScale = scaleType
                )
            }
        }
    }
}