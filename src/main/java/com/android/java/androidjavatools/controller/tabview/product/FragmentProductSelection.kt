//
//  FragmentProductSelection.kt
//
//  Created by Mathieu Delehaye on 21/05/2023.
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

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.java.androidjavatools.R
import com.android.java.androidjavatools.controller.tabview.search.SearchBox
import com.android.java.androidjavatools.controller.tabview.search.SuggestionsAdapter
import com.android.java.androidjavatools.controller.template.FragmentComposeWithSearch
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState

open class FragmentProductSelection() : FragmentComposeWithSearch() {
    @OptIn(ExperimentalFoundationApi::class, ExperimentalPagerApi::class)
    @Composable
    override fun contentView() {
//        val mNavigatorManager : Navigator.NavigatorManager = mActivity!! as Navigator.NavigatorManager

        var searchBox = SearchBox(mActivity as Activity, this, null)
        val adapter = SuggestionsAdapter(mActivity, searchBox, searchBox.getSearchableConfig())
        searchBox.setSuggestionsAdapter(adapter)

        Column {
            Spacer(modifier = Modifier.height(56.dp))

            searchBox.show()

            Spacer(modifier = Modifier.height(45.dp))

            HorizontalPager(
                count = Int.MAX_VALUE,
                state = rememberPagerState(
                    initialPage = Int.MAX_VALUE / 2
                )
            ) { page ->
                productGridPage()
            }
        }
    }

    @OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)
    @Composable
    fun productGridPage() {
        val imageNumber = 4
        val images = intArrayOf(R.drawable.product01, R.drawable.product02, R.drawable.product03,
            R.drawable.product04, R.drawable.product05)
        val titles = arrayOf("Guerlain", "Sisley", "YSL", "Emporio Armani")
        val descriptions = arrayOf(
            "Abeille Royale Double R..."
            , "Night Cream with Collagen..."
            , "Touch Eclat Le Teint Foun..."
            , "Because it's You EAU DE...")

        LazyVerticalGrid(
                cells = GridCells.Fixed(2)
        ) {
            items(imageNumber) {index ->
                val imageId = images[index % images.size]

                Box(
                    modifier = Modifier
                        .padding(3.dp)
                ) {
                    Card(
                        onClick = {
                            Toast.makeText(context, "This is item number $index", Toast.LENGTH_SHORT).show()
                        }
                        , modifier = Modifier
                            .background(Color.White)
                            .width(200.dp)
                            .height(200.dp)
                            .border(width = 1.dp, Color.DarkGray)
                        , elevation = 6.dp
                    ) {
                        Column {
                            Box(
                                modifier = Modifier
                                    .background(Color.White)
                                    .width(200.dp)
                                    .height(155.dp)
                            ) {
                                Image(
                                    painter = painterResource(id = imageId)
                                    , contentDescription = "Image with id $imageId"
                                    , contentScale = ContentScale.Fit
                                    , modifier = Modifier
                                    .align(Alignment.Center)
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .background(Color(0xfff1f1f4))
                                    .width(200.dp)
                                    .height(45.dp)
                            ) {
                                Column {
                                    Text(
                                        text = titles[index % titles.size]
                                        , fontWeight = FontWeight.W600
                                        , fontSize = 16.sp
                                        , textAlign = TextAlign.Center
                                        , color = Color.Blue
                                        , modifier = Modifier
                                        .padding(start = 2.dp)
                                    )

                                    Text(
                                        text = descriptions[index % titles.size]
                                        , fontWeight = FontWeight.W500
                                        , fontSize = 14.sp
                                        , textAlign = TextAlign.Center
                                        , color = Color.Black
                                        , modifier = Modifier
                                        .padding(start = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Preview
    @Composable
    fun profileContentPreview() {
        contentView()
    }

    override fun searchAndDisplayItems() {
        TODO("Not yet implemented")
    }
}