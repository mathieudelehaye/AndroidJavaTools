//
//  FragmentProductDetail.kt
//
//  Created by Mathieu Delehaye on 24/05/2023.
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

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
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
import com.android.java.androidjavatools.controller.tabview.Navigator
import com.android.java.androidjavatools.controller.template.FragmentCompose

open class FragmentProductDetail : FragmentCompose() {
    private var mImage: MutableState<Int> = mutableStateOf(R.drawable.product01)
    private var mTitle: MutableState<String> = mutableStateOf("")
    private var mDescription: MutableState<String> = mutableStateOf("")

    @Composable
    override fun contentView() {
        //val mNavigatorManager : Navigator.NavigatorManager = mActivity!! as Navigator.NavigatorManager
        var image by remember { mImage }
        var title by remember { mTitle }
        var description by remember { mDescription }

        Column {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
                , modifier = Modifier
                    .background(Color.White)
            ) {
                Spacer(modifier = Modifier.height(56.dp))
                Box(
                    modifier = Modifier
                        .background(Color.White)
                        .fillMaxWidth()
                        .height(250.dp)
                        .border(width = 1.dp, Color.DarkGray)
                ) {
                    Image(
                        painter = painterResource(id = image)
                        , contentDescription = "Image with id $image"
                        , contentScale = ContentScale.Fit
                        , modifier = Modifier
                            .align(Alignment.Center)
                    )
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                    , modifier = Modifier
                    .background(Color(0xFFF1F1F4))
                ) {
                    Text(
                        text = title
                        , fontWeight = FontWeight.W600
                        , fontSize = 20.sp
                        , textAlign = TextAlign.Center
                        , color = Color.Blue
                        , modifier = Modifier
                            .padding(start = 2.dp)
                    )

                    Text(
                        text = description
                        , fontWeight = FontWeight.W500
                        , fontSize = 18.sp
                        , textAlign = TextAlign.Center
                        , color = Color.Black
                        , modifier = Modifier
                            .padding(start = 2.dp)
                    )
                }

            }
        }
    }

    fun setImage(image: Int) {
        mImage.value = image
    }

    fun setTitle(title: String) {
        mTitle.value = title
    }

    fun setDescription(description: String) {
        mDescription.value = description
    }

    @Preview
    @Composable
    fun profileContentPreview() {
        setImage(R.drawable.product01)
        setTitle("Guerlain")
        setDescription("Abeille Royale Double Renew & Repair Advanced Serum 345ml")
        contentView()
    }
}