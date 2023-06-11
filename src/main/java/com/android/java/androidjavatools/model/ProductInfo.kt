//
//  ProductInfo.kt
//
//  Created by Mathieu Delehaye on 29/05/2023.
//
//  BeautyAndroid: An Android app to order and recycle cosmetics.
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

package com.android.java.androidjavatools.model

import com.google.firebase.firestore.FirebaseFirestore

class ProductInfo : DBCollectionAccessor {
    constructor(database : FirebaseFirestore) : super(database, "samples") {
        mData = arrayListOf()
        mDataChanged = arrayListOf()
    }

    fun getTitleAtIndex(i: Int): String? {
        val productTitle = mData[i]["title"]
        return if (productTitle != null
            && productTitle != "?") "$productTitle" else ""
    }

    fun getSubtitleAtIndex(i: Int): String? {
        val productDescription = mData[i]["subtitle"]
        return if (productDescription != null
            && productDescription != "?") "$productDescription" else ""
    }

    fun getKeyAtIndex(i: Int): String? {
        val productKey = mData[i]["key"]
        return if (productKey != null
            && productKey != "?") "$productKey" else ""
    }
}