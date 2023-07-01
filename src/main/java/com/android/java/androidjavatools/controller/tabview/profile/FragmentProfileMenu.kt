//
//  FragmentProfileMenu.kt
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

package com.android.java.androidjavatools.controller.tabview.profile

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.java.androidjavatools.R
import com.android.java.androidjavatools.controller.template.FragmentCompose
import com.android.java.androidjavatools.model.user.AppUser

data class MenuItem(
    val name: String,
    val icon: Int
)

open class FragmentProfileMenu : FragmentCompose() {
    private val mSignedInUser : MutableState<Boolean> = mutableStateOf(false)
    private val mUserName : MutableState<String> = mutableStateOf("")

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    override fun contentView() {
        val signedInUser by remember { mSignedInUser }
        val userName by remember { mUserName }

        Column (
            modifier = Modifier
                .fillMaxSize()
            , horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier
                .height(200.dp)
            )
            Image(
                painter = painterResource(R.drawable.account_circle),
                contentDescription = "Contact profile picture",
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
            )
            Text(
                text = userName
                , fontSize = 20.sp
                , fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier
                .height(30.dp)
            )

            var menuItems: List<MenuItem> = ArrayList()

            if (signedInUser) {
                // If signed-in user, show the account page button
                menuItems += MenuItem("Manage Account", R.drawable.account)
            }
            menuItems += MenuItem("Help", R.drawable.lifebuoy)
            menuItems += MenuItem("Terms", R.drawable.scale_balance)

            LazyColumn {
                itemsIndexed(menuItems) { index, _ ->
                    val itemName = menuItems[index].name

                    Card(
                        onClick = {
                            when (index) {
                                0 -> mNavigatorManager?.navigator()?.showFragment("account")
                                1 -> mNavigatorManager?.navigator()?.showFragment("help")
                                else -> mNavigatorManager?.navigator()?.showFragment("terms")
                            }
                        }
                        , modifier = Modifier.padding(8.dp)
                        , elevation = 6.dp
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(8.dp)
                                .fillMaxWidth()
                        ) {
                            Spacer(modifier = Modifier.width(5.dp))
                            Image(
                                painter = painterResource(id = menuItems[index].icon),
                                contentDescription = itemName,
                                modifier = Modifier
                                    .height(60.dp)
                                    .width(60.dp)
                                    .padding(5.dp)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = itemName,
                                modifier = Modifier.padding(4.dp),
                                color = Color.Black, textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }

    @Preview
    @Composable
    fun profileContentPreview() {
        AppUser.getInstance().authenticate("mathieu.delehaye@gmail.com", AppUser.AuthenticationType.REGISTERED)
        contentView()
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)

        if (isVisibleToUser) {
            Log.d("AJT", "Account page becomes visible")

            mSignedInUser.value = AppUser.getInstance().authenticationType == AppUser.AuthenticationType.REGISTERED
            mUserName.value = if (mSignedInUser.value) AppUser.getInstance().id else "Anonymous user"
        } else {
            Log.d("AJT", "Account page becomes hidden")
        }
    }
}