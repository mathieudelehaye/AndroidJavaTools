//
//  ProductItemInfo.kt
//
//  Created by Mathieu Delehaye on 30/06/2023.
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

package com.android.java.androidjavatools.model.product

import com.android.java.androidjavatools.controller.template.ItemWithImage

class ProductItemInfo (
    key : String,
    title : String,
    subtitle : String,
    description : String,
    displayBrand : Boolean
) : ItemWithImage() {

    private val mKey: String? = key
    private var mTitle: String? = title
    private val mSubtitle: String? = subtitle
    private var mDescription: String? = description
    private val mContentAllowed = displayBrand

    fun getKey(): String? {
        return mKey
    }

    fun getTitle(): String? {
        return mTitle
    }

    fun getSubtitle(): String? {
        return mSubtitle
    }

    fun getDescription(): String? {
        return mDescription
    }

    fun isContentAllowed(): Boolean {
        return mContentAllowed
    }
}