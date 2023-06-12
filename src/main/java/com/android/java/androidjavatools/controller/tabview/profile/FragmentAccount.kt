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

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidViewBinding
import com.android.java.androidjavatools.R
import com.android.java.androidjavatools.controller.template.FragmentCompose
import com.android.java.androidjavatools.controller.template.Navigator
import com.android.java.androidjavatools.databinding.FragmentAccountBinding
import com.android.java.androidjavatools.model.AppUser
import com.android.java.androidjavatools.model.TaskCompletionManager
import com.android.java.androidjavatools.model.UserInfoDBEntry
import com.google.firebase.firestore.FirebaseFirestore

abstract class FragmentAccount : FragmentCompose() {
    protected val mDatabase = FirebaseFirestore.getInstance()
    protected open val mUserInfoDBEntry = UserInfoDBEntry(mDatabase, AppUser.getInstance().id)

    // state variables
    protected var mUserFirstName: MutableState<String> = mutableStateOf("")
    protected var mUserLastName: MutableState<String> = mutableStateOf("")
    protected var mUserAddress: MutableState<String> = mutableStateOf("")
    protected var mUserCity: MutableState<String> = mutableStateOf("")
    protected var mUserPostcode: MutableState<String> = mutableStateOf("")
    protected var mUserEmail: MutableState<String> = mutableStateOf("")

    override fun onCreateView(
        inflater: LayoutInflater
        , container: ViewGroup?
        , savedInstanceState: Bundle?
    ): View {
        return super.onCreateView(inflater, container, savedInstanceState)
    }

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
                if (mUserInfoDBEntry.firstName != accountFirstName.text.toString()) {
                    firstName = accountFirstName.text.toString()
                    mUserInfoDBEntry.firstName = firstName
                }

                if (mUserInfoDBEntry.lastName != accountLastName.text.toString()) {
                    lastName = accountLastName.text.toString()
                    mUserInfoDBEntry.lastName = lastName
                }

                if (mUserInfoDBEntry.address != accountAddress.text.toString()) {
                    address = accountAddress.text.toString()
                    mUserInfoDBEntry.address = address
                }

                if (mUserInfoDBEntry.city != accountCity.text.toString()) {
                    city = accountCity.text.toString()
                    mUserInfoDBEntry.city = city
                }

                if (mUserInfoDBEntry.postCode != accountPostCode.text.toString()) {
                    postCode = accountPostCode.text.toString()
                    mUserInfoDBEntry.postCode = postCode
                }

                if (mUserInfoDBEntry.email != accountEmail.text.toString()) {
                    email = accountEmail.text.toString()
                    mUserInfoDBEntry.email = email
                }

                val dBUpdateCallback = object : TaskCompletionManager {
                    override fun onSuccess() {
                        Toast.makeText(context, "Data saved", Toast.LENGTH_SHORT).show()
                    }

                    override fun onFailure() {
                        TODO("Not yet implemented")
                    }
                }
                mUserInfoDBEntry.updateDBFields(dBUpdateCallback)

                // Go back to the Profile menu
                mNavigatorManager.navigator().back()
            }

            accountBack.setOnClickListener {
                // Go back to the Profile menu
                mNavigatorManager.navigator().back()
            }

            accountLogOut.setOnClickListener{
                AppUser.getInstance().logOut()

                // Delete the app current user
                val pref: SharedPreferences = (mActivity!! as Context).getSharedPreferences(
                    getString(R.string.lib_name), Context.MODE_PRIVATE)
                pref.edit().putString(getString(R.string.app_uid), "").commit()

                onLogout()
            }
        }
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)

        if (isVisibleToUser) {
            Log.d("AJT", "Account page becomes visible")

            mUserInfoDBEntry.key = AppUser.getInstance().id
            mUserInfoDBEntry.readDBFields(object : TaskCompletionManager {
                override fun onSuccess() {
                    mUserFirstName.value = mUserInfoDBEntry.firstName
                    mUserLastName.value = mUserInfoDBEntry.lastName
                    mUserAddress.value = mUserInfoDBEntry.address
                    mUserCity.value = mUserInfoDBEntry.city
                    mUserPostcode.value = mUserInfoDBEntry.postCode
                    mUserEmail.value = mUserInfoDBEntry.email
                }

                override fun onFailure() {}
            })
        } else {
            Log.d("AJT", "Account page becomes hidden")
        }
    }

    abstract fun onLogout()
}