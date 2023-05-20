//
//  ProfileMenuAdapter.java
//
//  Created by Mathieu Delehaye on 20/05/2023.
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
//  You should have received a copy of the GNU Affero General Public License along with this program. If not,
//  see <https://www.gnu.org/licenses/>.

package com.android.java.androidjavatools.controller.tabview.profile

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

class ProfileMenuAdapter() : BaseAdapter() {
    var mMenuItems = ArrayList<String>()
    var mContext: Context? = null

    constructor(ctxt: Context?, items: ArrayList<String>?) : this() {
        mContext = ctxt

        if (items != null) {
            mMenuItems = items!!
        }
    }

    override fun getCount(): Int {
        return mMenuItems.size
    }

    override fun getItem(position: Int): Any {
        return mMenuItems[position]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        val text = TextView(mContext)
        text.text = mMenuItems[position]
        return text
    }
}