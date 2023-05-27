//
//  FragmentAccount.kt
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

package com.android.java.androidjavatools.controller.tabview.profile

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidViewBinding
import com.android.java.androidjavatools.controller.tabview.Navigator
import com.android.java.androidjavatools.controller.template.FragmentCompose
import com.android.java.androidjavatools.databinding.FragmentAccountBinding
import com.android.java.androidjavatools.model.AppUser
import com.android.java.androidjavatools.model.TaskCompletionManager
import com.android.java.androidjavatools.model.UserInfoDBEntry
import com.google.firebase.firestore.FirebaseFirestore

abstract class FragmentAccount : FragmentCompose() {
    private var mDatabase = FirebaseFirestore.getInstance()

    @Composable
    override fun contentView() {
        val mNavigatorManager : Navigator.NavigatorManager = mActivity!! as Navigator.NavigatorManager

        AndroidViewBinding(
            factory = FragmentAccountBinding::inflate,
            modifier = Modifier
        ) {
            backRegister.setOnClickListener {
                // Go back to the Profile menu
                mNavigatorManager.navigator().back()
            }

            logOutRegister.setOnClickListener{
                AppUser.getInstance().authenticate("", AppUser.AuthenticationType.NONE);
                onLogout()
            }
        }
    }

    @Preview
    @Composable
    fun accountContentView() {
        contentView()
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)

        if (isVisibleToUser) {
            Log.d("AndroidJavaTools", "Account page becomes visible")

            val entry = UserInfoDBEntry(mDatabase, AppUser.getInstance().id)

            entry.readDBFields(object : TaskCompletionManager {
                override fun onSuccess() {
                    val currentUserFirstName = entry.firstName
                    val currentUserLastName = entry.lastName
                    val currentUserAddress = entry.address
                    val currentUserCity = entry.city
                    val currentUserPostcode = entry.postcode

                    Log.d("AndroidJavaTools", "mdl setUserVisibleHint: "
                        + "currentUserFirstName = $currentUserFirstName, "
                        + "currentUserLastName = $currentUserLastName, "
                        + "currentUserAddress = $currentUserAddress, "
                        + "currentUserCity = $currentUserCity, "
                        + "currentUserPostcode = $currentUserPostcode"
                    )
                }

                override fun onFailure() {}
            })
        } else {
            Log.d("AndroidJavaTools", "Account page becomes hidden")
        }
    }

    abstract fun onLogout()
}