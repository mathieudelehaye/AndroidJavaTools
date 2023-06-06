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
import android.widget.Toast
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidViewBinding
import com.android.java.androidjavatools.controller.Navigator
import com.android.java.androidjavatools.controller.template.FragmentCompose
import com.android.java.androidjavatools.databinding.FragmentAccountBinding
import com.android.java.androidjavatools.model.AppUser
import com.android.java.androidjavatools.model.TaskCompletionManager
import com.android.java.androidjavatools.model.UserInfoDBEntry
import com.google.firebase.firestore.FirebaseFirestore

abstract class FragmentAccount : FragmentCompose() {
    private val mDatabase = FirebaseFirestore.getInstance()
    private val mDatabaseEntry = UserInfoDBEntry(mDatabase, AppUser.getInstance().id)

    // state variables
    private var mUserFirstName: MutableState<String> = mutableStateOf("")
    private var mUserLastName: MutableState<String> = mutableStateOf("")
    private var mUserAddress: MutableState<String> = mutableStateOf("")
    private var mUserCity: MutableState<String> = mutableStateOf("")
    private var mUserPostcode: MutableState<String> = mutableStateOf("")
    private var mUserEmail: MutableState<String> = mutableStateOf("")

    @Composable
    override fun contentView() {
        val mNavigatorManager : Navigator.NavigatorManager = mActivity!! as Navigator.NavigatorManager

        var firstName by remember { mUserFirstName }
        var lastName by remember { mUserLastName }
        var address by remember { mUserAddress }
        var city by remember { mUserCity }
        var postCode by remember { mUserPostcode }
        var email by remember { mUserEmail }

        AndroidViewBinding(
            factory = FragmentAccountBinding::inflate,
            modifier = Modifier
        ) {
            accountFirstName.setText(firstName)
            accountLastName.setText(lastName)
            accountAddress.setText(address)
            accountCity.setText(city)
            accountPostCode.setText(postCode)
            accountEmail.setText(email)

            accountConfirm.setOnClickListener {
                // TODO: avoid repeating instructions and call a method instead
                if (mDatabaseEntry.firstName != accountFirstName.text.toString()) {
                    firstName = accountFirstName.text.toString()
                    mDatabaseEntry.firstName = firstName
                }

                if (mDatabaseEntry.lastName != accountLastName.text.toString()) {
                    lastName = accountLastName.text.toString()
                    mDatabaseEntry.lastName = lastName
                }

                if (mDatabaseEntry.address != accountAddress.text.toString()) {
                    address = accountAddress.text.toString()
                    mDatabaseEntry.address = address
                }

                if (mDatabaseEntry.city != accountCity.text.toString()) {
                    city = accountCity.text.toString()
                    mDatabaseEntry.city = city
                }

                if (mDatabaseEntry.postCode != accountPostCode.text.toString()) {
                    postCode = accountPostCode.text.toString()
                    mDatabaseEntry.postCode = postCode
                }

                if (mDatabaseEntry.email != accountEmail.text.toString()) {
                    email = accountEmail.text.toString()
                    mDatabaseEntry.email = email
                }

                val dBUpdateCallback = object : TaskCompletionManager {
                    override fun onSuccess() {
                        Toast.makeText(context, "Data saved", Toast.LENGTH_SHORT).show()
                    }

                    override fun onFailure() {
                        TODO("Not yet implemented")
                    }
                }
                mDatabaseEntry.updateDBFields(dBUpdateCallback)

                // Go back to the Profile menu
                mNavigatorManager.navigator().back()
            }

            accountBack.setOnClickListener {
                // Go back to the Profile menu
                mNavigatorManager.navigator().back()
            }

            accountLogOut.setOnClickListener{
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

            mDatabaseEntry.readDBFields(object : TaskCompletionManager {
                override fun onSuccess() {
                    mUserFirstName.value = mDatabaseEntry.firstName
                    mUserLastName.value = mDatabaseEntry.lastName
                    mUserAddress.value = mDatabaseEntry.address
                    mUserCity.value = mDatabaseEntry.city
                    mUserPostcode.value = mDatabaseEntry.postCode
                    mUserEmail.value = mDatabaseEntry.email
                }

                override fun onFailure() {}
            })
        } else {
            Log.d("AndroidJavaTools", "Account page becomes hidden")
        }
    }

    abstract fun onLogout()
}