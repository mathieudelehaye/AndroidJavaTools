//
//  FragmentProductSelection.kt
//
//  Created by Mathieu Delehaye on 21/05/2023.
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

package com.android.java.androidjavatools.controller.tabview.product

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.java.androidjavatools.Helpers
import com.android.java.androidjavatools.R
import com.android.java.androidjavatools.controller.tabview.search.SuggestionsAdapter
import com.android.java.androidjavatools.controller.template.FragmentComposeWithSearch
import com.android.java.androidjavatools.controller.template.backButton
import com.android.java.androidjavatools.model.SetWithImages
import com.android.java.androidjavatools.model.TaskCompletionManager
import com.android.java.androidjavatools.model.product.ProductInfo
import com.android.java.androidjavatools.model.product.ProductItemInfo
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

    private val mProducts = SetWithImages()

    private var mProductKeys: MutableState<Array<String>> = mutableStateOf(emptyArray())
    private var mProductImages: MutableState<Array<Array<Byte>>> = mutableStateOf(emptyArray())
    private var mProductTitles: MutableState<Array<String>> = mutableStateOf(emptyArray())
    private var mProductSubtitles: MutableState<Array<String>> = mutableStateOf(emptyArray())

    fun launchSearch() {
        searchProducts()
    }

    @OptIn(ExperimentalPagerApi::class)
    @Composable
    override fun contentView() {
        var productKeys by remember { mProductKeys }
        var productImages by remember { mProductImages }
        var productTitles by remember { mProductTitles }
        var productSubtitles by remember { mProductSubtitles }

        val productNumber = productKeys.size
        val pages: Int = (productNumber / 5) + 1
        Log.d("AJT", "Product selection updated: $productNumber product(s), $pages page(s)")

        val adapter = SuggestionsAdapter(mActivity, mSearchBox, mSearchBox.getSearchableConfig())
        mSearchBox.setSuggestionsAdapter(adapter)

        Column {
            productSelectionHeader()

            mSearchBox.show()

            Spacer(modifier = Modifier.height(45.dp))

            HorizontalPager(
                count = pages,
                state = rememberPagerState(
                    initialPage = 0
                )
            ) { page ->
                val pageProductNumber =  if (page < pages - 1) 4 else (productNumber % 5)
                val startIndex = page * 4
                val endIndex = page * 4 + pageProductNumber

                Log.d("AJT", "Page $page has $pageProductNumber products, fom index $startIndex " +
                    "to $endIndex")

                productGridPage(
                    pageProductNumber,
                    productImages.copyOfRange(startIndex, endIndex)
                    , productTitles.copyOfRange(startIndex, endIndex)
                    , productSubtitles.copyOfRange(startIndex, endIndex)
                    , productKeys.copyOfRange(startIndex, endIndex)
                )
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
    fun productGridPage(productNumber: Int, images : Array<Array<Byte>>, titles : Array<String>,
        descriptions : Array<String>, keys : Array<String>) {

        if (productNumber > 4) {
            Log.e("AJT", "Cannot display a product grid page with more than 4 items")
            return
        }

        LazyVerticalGrid(
            cells = GridCells.Fixed(2)
        ) {
            items(productNumber) { index ->
                val key = keys[index % productNumber]
                val title = titles[index % productNumber]
                val description = descriptions[index % productNumber]
                val image = images[index % productNumber]

                Box(
                    modifier = Modifier
                        .padding(3.dp)
                ) {
                    productCard(image, title, description, key)
                }
            }
        }
    }

    @Preview
    @Composable
    fun productGridPagePreview() {
        val keys = arrayOf("", "", "", "")
        // TODO: re-add the preview images
        val images =
            intArrayOf(R.drawable.product01, R.drawable.product02, R.drawable.product03,
            R.drawable.product04, R.drawable.product05)
        val titles = arrayOf("Guerlain", "Sisley", "YSL", "Emporio Armani")
        val descriptions = arrayOf(
            "Abeille Royale Double Renew & Repair Advanced Serum 345ml"
            , "Night Cream with Collagen with Maximum Hydration 500ml"
            , "Touch Eclat Le Teint Foundation Infused with Light 100ml"
            , "Because it's You EAU DE PARFUM Delicious and Sparkling 150ml")

//        productGridPage(titles.size, images.toTypedArray(), titles, descriptions, keys)
    }

    @Preview
    @Composable
    fun productCardPreview() {
//        productCard(R.drawable.camera, "Guerlain",
//            "Abeille Royale Double Renew & Repair Advanced Serum 345ml", "")
    }

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    fun productCard(image: Array<Byte>, title: String, description: String, key: String) {

        val imageBitmap = BitmapFactory.decodeByteArray(Helpers.toPrimitives(image),0, image.size)
            .asImageBitmap()

        Card(
            onClick = {
                val productDetailFragment
                    = mNavigatorManager.navigator().getFragment("product") as FragmentProductDetail
                productDetailFragment.setImage(image)
                productDetailFragment.setTitle(title)
                productDetailFragment.setSubtitle(description)
                productDetailFragment.setKey(key)

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
                        painter = BitmapPainter(imageBitmap)
                        , contentDescription = "Image for key $key"
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
            Log.d("AJT", "Product selection page becomes visible")
        } else {
            Log.d("AJT", "Product selection page becomes hidden")
        }
    }

    override fun searchAndDisplayItems() {
        TODO("Not yet implemented")
    }

    private fun searchProducts() {
        val productInfo = ProductInfo(FirebaseFirestore.getInstance())
        productInfo.setValueBasedFilter(arrayOf(mFilterField), arrayOf("true"))

        productInfo.readDBFieldsForCurrentFilter(arrayOf("title", "subtitle", "description", "image_url"),
            object : TaskCompletionManager {

                @SuppressLint("UseCompatLoadingForDrawables")
                override fun onSuccess() {
                    val productNumber = productInfo.data.size

                    val keyList = mutableListOf<String>()
                    val titleList = mutableListOf<String>()
                    val subtitleList = mutableListOf<String>()

                    mProducts.clear();

                    for (i in 0 until      productNumber) {
                        val key: String = productInfo.getKeyAtIndex(i)!!
                        val title: String = productInfo.getTitleAtIndex(i)!!
                        val subtitle: String = productInfo.getSubtitleAtIndex(i)!!
                        val description: String = productInfo.getSubtitleAtIndex(i)!!
                        val imageURL: String = productInfo.getImageURLAtIndex(i)!!

                        // Prepare the state data for the Compose text views
                        keyList.add(key)
                        titleList.add(productInfo.getTitleAtIndex(i)!!)
                        subtitleList.add(productInfo.getSubtitleAtIndex(i)!!)

                        // Update the set to download the images
                        mProducts.create(
                            key,
                            ProductItemInfo(key, title, subtitle, description, true),
                            imageURL
                        )
                    }

                    // Update the states for the Compose text views
                    mProductKeys.value = keyList.toTypedArray()
                    mProductTitles.value = titleList.toTypedArray()
                    mProductSubtitles.value = subtitleList.toTypedArray()

                    updateProductImages()

                    Log.d("AJT", "Found $productNumber items for filter `$mFilterField`")
                }

                override fun onFailure() {}
            })
    }

    private fun updateProductImages() {
        val productNumber = mProducts.size()
        val imageList = mutableListOf<Array<Byte>>()

        // Update the Compose image view with the placeholder image
        for (i in 0 until      productNumber) {
            imageList.add(Helpers.getPlaceholderImageByteArray(requireActivity()))
        }
        mProductImages.value = imageList.toTypedArray()

        mProducts.downloadImages(object : TaskCompletionManager {
            override fun onSuccess() {
                // Update the Compose image view with the latest downloaded images
                for (i in 0 until      mProducts.size()) {
                    if (mProducts[i].mustShowImage()) {
                        imageList[i] = mProducts[i].getImage()
                    }
                }
                mProductImages.value = imageList.toTypedArray()
            }

            override fun onFailure() {}
        })
    }
}