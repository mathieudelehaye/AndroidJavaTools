//
//  ItemWithImage.kt
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

package com.android.java.androidjavatools.controller.template

open class ItemWithImage {
    private var mImage: Array<Byte> = emptyArray()
    private var mImageShownInDetails = false

    fun getImage(): Array<Byte> {
        return mImage
    }

    fun setImage(image: Array<Byte>) {
        mImage = image
    }

    open fun mustShowImage(): Boolean {
        val res = mImage != null && !mImageShownInDetails
        if (res) {
            mImageShownInDetails = true
        }
        return res
    }
}