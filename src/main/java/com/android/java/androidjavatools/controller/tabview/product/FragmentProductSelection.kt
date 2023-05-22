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

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.android.java.androidjavatools.controller.tabview.Navigator
import com.android.java.androidjavatools.controller.template.FragmentComposeWithSearch
import com.android.java.androidjavatools.model.AppUser

open class FragmentProductSelection() : FragmentComposeWithSearch() {
    private val signedInUser = AppUser.getInstance().authenticationType == AppUser.AuthenticationType.REGISTERED
    private val userName : String =  if (signedInUser) AppUser.getInstance().id else "Anonymous user"

    @Composable
    override fun contentView() {
        val mNavigatorManager : Navigator.NavigatorManager = mActivity!! as Navigator.NavigatorManager

        Column (
            modifier = Modifier
                .fillMaxSize()
            , horizontalAlignment = Alignment.CenterHorizontally
        ) {
        }
    }

    @Preview
    @Composable
    fun profileContentPreview() {
        AppUser.getInstance().authenticate("mathieu.delehaye@gmail.com", AppUser.AuthenticationType.REGISTERED)
        contentView()
    }

    override fun searchAndDisplayItems() {
        TODO("Not yet implemented")
    }
}