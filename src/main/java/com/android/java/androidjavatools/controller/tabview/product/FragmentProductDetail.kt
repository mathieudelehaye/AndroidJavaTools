//
//  FragmentProductDetail.kt
//
//  Created by Mathieu Delehaye on 24/05/2023.
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

import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
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
import com.android.java.androidjavatools.controller.template.FragmentCompose
import com.android.java.androidjavatools.controller.template.backButton
import com.android.java.androidjavatools.controller.template.buttonWithText
import com.android.java.androidjavatools.model.user.AppUser
import com.android.java.androidjavatools.model.user.AuthManager
import com.android.java.androidjavatools.model.TaskCompletionManager
import com.android.java.androidjavatools.model.user.UserInfoDBEntry
import com.google.firebase.firestore.FirebaseFirestore

abstract class FragmentProductDetail : FragmentCompose() {
    protected val mDatabase = FirebaseFirestore.getInstance()
    protected open val mUserInfoDBEntry = UserInfoDBEntry(mDatabase, AppUser.getInstance().id)

    private var mKey: String = ""
    private var mImage: MutableState<Array<Byte>> = mutableStateOf(emptyArray())
    private var mTitle: MutableState<String> = mutableStateOf("")
    private var mSubtitle: MutableState<String> = mutableStateOf("")

    @Composable
    override fun contentView() {
        productDetail()
    }

    @Composable
    fun productDetail() {
        var image by remember { mImage }
        var title by remember { mTitle }
        var subtitle by remember { mSubtitle }

        val imageByteArray = if (image.isNotEmpty()) image
            else Helpers.getPlaceholderImageByteArray(requireActivity())
        val imageBitmap = BitmapFactory.decodeByteArray(Helpers.toPrimitives(imageByteArray),0,
            imageByteArray.size).asImageBitmap()

        Column {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
                , modifier = Modifier
                    .background(Color.White)
            ) {
                Box(
                    modifier = Modifier
                        .background(Color.White)
                        .fillMaxWidth()
                        .height(370.dp)
                        .border(width = 0.5.dp, Color.DarkGray)
                ) {
                    Image(
                        painter = BitmapPainter(imageBitmap)
                        , contentDescription = "Image with id $image"
                        , contentScale = ContentScale.Fit
                        , modifier = Modifier
                        .align(Alignment.Center)
                    )
                    Column {
                        Spacer(modifier = Modifier.height(25.dp))
                        Row {
                            Spacer(modifier = Modifier.width(25.dp))
                            backButton(mNavigatorManager)
                        }
                    }
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                    , modifier = Modifier
                        .background(Color(0xFFF1F1F4))
                        .fillMaxWidth()
                ) {
                    Text(
                        text = title
                        , fontWeight = FontWeight.W600
                        , fontSize = 20.sp
                        , textAlign = TextAlign.Center
                        , color = Color.Blue
                    )
                    Text(
                        text = subtitle
                        , fontWeight = FontWeight.W500
                        , fontSize = 18.sp
                        , textAlign = TextAlign.Center
                        , maxLines = 2
                        , overflow = TextOverflow.Visible
                        , softWrap = true
                        , color = Color.Black
                        , modifier = Modifier
                            .height(60.dp)
                    )
                }
                Column(
                    modifier = Modifier
                        .background(Color.White)
                        .fillMaxWidth()
                ) {
                    productDescription()

                    Spacer(modifier = Modifier
                        .height(5.dp)
                    )
                    Divider(color = Color.LightGray, thickness = 2.dp)
                    Spacer(modifier = Modifier
                        .height(5.dp)
                    )
                    Row {
                        Spacer(modifier = Modifier.width(40.dp))
                        buttonWithText("Buy Now", Color.Green, width = 150.dp, radius = 30.dp) {}
                        Spacer(modifier = Modifier.width(30.dp))
                        // Orange color
                        buttonWithText("Freebies", Color(0xFFD0A038), width = 150.dp, radius = 30.dp) {
                            if (!isUserConnected()) {
                                Log.v("AJT", "User not connected when ordering samples:"
                                    + " starting the authentication activity")

                                // After authentication, navigate back to the Product detail fragment
                                AuthManager.setAppFirstFragment("product")
                                // Stop recording the navigation while authenticating
                                mNavigatorManager?.navigator()?.setNavigationRecording(false)

                                mNavigatorManager?.navigator()?.showFragment("start")
                            } else {
                                // Restore the first fragment to show after authentication
                                AuthManager.setAppFirstFragment("tab")
                                // Restore the navigation recording
                                mNavigatorManager?.navigator()?.setNavigationRecording(true)

                                mUserInfoDBEntry.key = AppUser.getInstance().id;
                                mUserInfoDBEntry.readDBFields(object : TaskCompletionManager {
                                    override fun onSuccess() {
                                        val address = mUserInfoDBEntry.address
                                        val city = mUserInfoDBEntry.city
                                        val postcode = mUserInfoDBEntry.postCode

                                        onOrdering(mKey,
                                            "Sample ordered at address: $address $city $postcode") {
                                                mNavigatorManager?.navigator()?.back()
                                            }
                                    }

                                    override fun onFailure() {}
                                })
                            }
                        }
                    }
                    Spacer(modifier = Modifier
                        .height(5.dp)
                    )
                }
            }
        }
    }

    @Composable
    open fun productDescription() {
    }

    fun setImage(image: Array<Byte>) {
        mImage.value = image
    }

    fun setTitle(title: String) {
        mTitle.value = title
    }

    fun setSubtitle(text: String) {
        mSubtitle.value = text
    }

    fun setKey(text: String) {
        mKey = text
    }

    abstract fun onOrdering(productKey : String, successDialogMessage : String, onSuccessDialogClose: () -> Unit = {})

    @Preview
    @Composable
    fun productDetailPreview() {
//        setImage(R.drawable.product01)
//        setTitle("Guerlain")
//        setSubtitle("Abeille Royale Double Renew & Repair Advanced Serum 345ml")
//        productDetail()
    }

    private fun isUserConnected(): Boolean {
        return (AppUser.getInstance().authenticationType
            == AppUser.AuthenticationType.REGISTERED) &&
            (!AppUser.getInstance().id.equals(""));
    }
}