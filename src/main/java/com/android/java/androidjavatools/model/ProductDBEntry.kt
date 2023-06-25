//
//  ProductDBEntry.kt
//
//  Created by Mathieu Delehaye on 28/05/2023.
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

package com.android.java.androidjavatools.model

import com.google.firebase.firestore.FirebaseFirestore

open class ProductDBEntry : DBCollectionAccessor {

    constructor(database: FirebaseFirestore, collection: String, key: String) : super(database, collection){
        mKey = key
        mData = ArrayList()

        val dataItem = HashMap<String, String>()
        mData.add(dataItem)
        dataItem["description"] = ""
        dataItem["ingredients"] = ""

        initializeDataChange()
    }

    private fun initializeDataChange() {
        mDataChanged = ArrayList()
        val dataChangeItem = HashMap<String, Boolean>()
        mDataChanged.add(dataChangeItem)
        dataChangeItem["description"] = false
        dataChangeItem["ingredients"] = false
    }

    fun getDescription(): String? {
        return mData[0]["description"]
    }

    fun setDescription(value: String?) {
        mData[0]["description"] = value
        mDataChanged[0]["description"] = true
    }

    fun getIngredients(): String? {
        return mData[0]["ingredients"]
    }

    fun setIngredients(value: String?) {
        mData[0]["ingredients"] = value
        mDataChanged[0]["ingredients"] = true
    }

    fun getQuantityInMl(): String? {
        return mData[0]["quantity_in_ml"]
    }

    fun setQuantityInMl(value: String?) {
        mData[0]["quantity_in_ml"] = value
        mDataChanged[0]["quantity_in_ml"] = true
    }

    fun getSubtitle(): String? {
        return mData[0]["subtitle"]
    }

    fun setSubtitle(value: String?) {
        mData[0]["subtitle"] = value
        mDataChanged[0]["subtitle"] = true
    }

    fun getTitle(): String? {
        return mData[0]["title"]
    }

    fun setTitle(value: String?) {
        mData[0]["title"] = value
        mDataChanged[0]["title"] = true
    }
}