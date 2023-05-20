//
//  FragmentProfile.java
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

package com.android.java.androidjavatools.controller.tabview.profile

import android.widget.ListView
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.android.java.androidjavatools.R
import com.android.java.androidjavatools.controller.template.FragmentBase
import com.android.java.androidjavatools.model.AppUser

open class FragmentProfile : FragmentBase() {
    private val userName : String = AppUser.getInstance().id

    @Composable
    override fun contentView() {
        val pageWidth = 411.dp
        val pageHeight = 914.dp
        val menuItems : ArrayList<String> = arrayListOf(
            "Manage Account"
            , "Help"
            , "Terms")

        val adapter: ProfileMenuAdapter? = ProfileMenuAdapter(context, menuItems)

        Column (
            modifier = Modifier
            // TODO: do not hardcode the page dim
            .width(pageWidth)
            .height(pageHeight)
            , horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier
                .height(200.dp)
            )
            Image(
                painter = painterResource(R.drawable.account),
                contentDescription = "Contact profile picture",
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .width(pageWidth)
            )
            Text(
                text = userName
            )
            AndroidView(
                modifier = Modifier.fillMaxSize(), // Occupy the max size in the Compose UI tree
                factory = { context ->
                    ListView(context).apply {
                        setAdapter(adapter)
                    }
                },
                update = { view ->
                }
            )

        }
    }

    @Preview
    @Composable
    fun profileContentPreview() {
        AppUser.getInstance().authenticate("mathieu.delehaye@gmail.com", AppUser.AuthenticationType.REGISTERED)
        contentView()
    }
}