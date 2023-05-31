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
import android.util.Log
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.java.androidjavatools.R
import com.android.java.androidjavatools.controller.tabview.search.SearchBox
import com.android.java.androidjavatools.controller.tabview.search.SuggestionsAdapter
import com.android.java.androidjavatools.controller.template.FragmentComposeWithSearch
import com.android.java.androidjavatools.controller.template.backButton
import com.android.java.androidjavatools.model.ProductInfo
import com.android.java.androidjavatools.model.TaskCompletionManager
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.google.firebase.firestore.FirebaseFirestore

open class FragmentProductSelection : FragmentComposeWithSearch() {
    companion object {
        private var mFilterField = ""

        fun setFilterField(value : String) {
            mFilterField = value
        }
    }

    @OptIn(ExperimentalPagerApi::class)
    @Composable
    override fun contentView() {
        var searchBox = SearchBox(mActivity as Activity, this, null)
        val adapter = SuggestionsAdapter(mActivity, searchBox, searchBox.getSearchableConfig())
        searchBox.setSuggestionsAdapter(adapter)

        Column {
            productSelectionHeader()

            searchBox.show()

            Spacer(modifier = Modifier.height(45.dp))

            HorizontalPager(
                count = Int.MAX_VALUE,
                state = rememberPagerState(
                    initialPage = Int.MAX_VALUE / 2
                )
            ) { page ->
//                productGridPage()
            }
        }
    }

    @Composable
    fun productSelectionHeader() {
        Column {
            Spacer(modifier = Modifier.height(5.dp))
            Row {
                Spacer(modifier = Modifier.width(40.dp))
                backButton(mNavigatorManager)
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Spacer(modifier = Modifier.height(5.dp))
                    Text(
                        text = "Choose Samples"
                        , fontWeight = FontWeight.W600
                        , fontSize = 28.sp
                        , color = Color.Black
                    )
                }

            }
            Spacer(modifier = Modifier.height(5.dp))
        }
    }

    @Preview
    @Composable
    fun productSelectionHeaderPreview() {
        productSelectionHeader()
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    fun productGridPage(imageNumber: Int, images : IntArray, titles : Array<String>, descriptions : Array<String>) {
        if (imageNumber > 4) {
            Log.e("AndroidJavaTools", "Cannot display a product grid page with more than 4 items")
            return
        }

        LazyVerticalGrid(
            cells = GridCells.Fixed(2)
        ) {
            items(imageNumber) {index ->
                val imageId = images[index % imageNumber]
                val title = titles[index % imageNumber]
                val description = descriptions[index % imageNumber]

                Box(
                    modifier = Modifier
                        .padding(3.dp)
                ) {
                    productCard(imageId, title, description)
                }
            }
        }
    }

    @Preview
    @Composable
    fun productGridPagePreview() {
        val images = intArrayOf(R.drawable.product01, R.drawable.product02, R.drawable.product03,
            R.drawable.product04, R.drawable.product05)
        val titles = arrayOf("Guerlain", "Sisley", "YSL", "Emporio Armani")
        val descriptions = arrayOf(
            "Abeille Royale Double Renew & Repair Advanced Serum 345ml"
            , "Night Cream with Collagen with Maximum Hydration 500ml"
            , "Touch Eclat Le Teint Foundation Infused with Light 100ml"
            , "Because it's You EAU DE PARFUM Delicious and Sparkling 150ml")

        productGridPage(titles.size, images, titles, descriptions)
    }

    @Preview
    @Composable
    fun productCardPreview() {
        productCard(R.drawable.product01, "Guerlain",
            "Abeille Royale Double Renew & Repair Advanced Serum 345ml")
    }

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    fun productCard(imageId: Int, title: String, description: String) {
        Card(
            onClick = {
                val productDetailFragment
                    = mNavigatorManager.navigator().getFragment("product") as FragmentProductDetail
                productDetailFragment.setImage(imageId)
                productDetailFragment.setTitle(title)
                productDetailFragment.setSubtitle(description)

                mNavigatorManager.navigator().showFragment("product")
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
                        .background(Color(0xFFF1F1F4))
                        .width(200.dp)
                        .height(45.dp)
                ) {
                    Column {
                        Text(
                            text = title
                            , fontWeight = FontWeight.W600
                            , fontSize = 16.sp
                            , textAlign = TextAlign.Center
                            , color = Color.Blue
                            , modifier = Modifier
                            .padding(start = 2.dp)
                        )
                        Text(
                            text = description
                            , fontWeight = FontWeight.W500
                            , fontSize = 14.sp
                            , color = Color.Black
                            , textAlign = TextAlign.Center
                            , maxLines = 1
                            , overflow = TextOverflow.Clip
                            , softWrap = false
                            , modifier = Modifier
                            .padding(start = 2.dp)
                        )
                    }
                }
            }
        }
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)

        if (isVisibleToUser) {
            Log.d("AndroidJavaTools", "Product detail page becomes visible")

            val productInfo = ProductInfo(FirebaseFirestore.getInstance())
            productInfo.SetValueBasedFilter(arrayOf(mFilterField), arrayOf("true"))

            productInfo.readDBFieldsForCurrentFilter(arrayOf("title"), object : TaskCompletionManager {
                override fun onSuccess() {
                    for (i in 0 until productInfo.data.size) {
                        val title = productInfo.getTitleAtIndex(i)
                        Log.d("AndroidJavaTools", "mdl title = $title")
                    }
                }

                override fun onFailure() {}
            })
        } else {
            Log.d("AndroidJavaTools", "Product detail page becomes hidden")
        }
    }

    override fun searchAndDisplayItems() {
        TODO("Not yet implemented")
    }
}