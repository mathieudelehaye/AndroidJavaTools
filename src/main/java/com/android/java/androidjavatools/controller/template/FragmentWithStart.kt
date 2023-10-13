//
//  FragmentWithStart.kt
//
//  Created by Mathieu Delehaye on 9/07/2023.
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
//  You should have received a copy of the GNU Affero General Public License along with this program. If not,
//  see <https://www.gnu.org/licenses/>.

package com.android.java.androidjavatools.controller.template

import androidx.fragment.app.Fragment

open class FragmentWithStart : Fragment() {
    // TODO: authenticate before starting the tab view fragment
    private var mFragmentStarted = true
    private val mFragmentStartLock = Any()

    override fun onStart() {
        super.onStart()

        synchronized (mFragmentStartLock) {
            mFragmentStarted = true;
        }
    }

    fun isStarted() : Boolean {
        var isStarted: Boolean
        synchronized (mFragmentStartLock) {
            isStarted = mFragmentStarted
        }

        return isStarted
    }
}