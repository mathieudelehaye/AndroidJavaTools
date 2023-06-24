//
//  ResultDetailAdapter.kt
//
//  Created by Mathieu Delehaye on 14/06/2023.
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

package com.android.java.androidjavatools.controller.tabview.result.detail

import android.content.Context
import android.database.DataSetObservable
import android.database.DataSetObserver
import android.graphics.BitmapFactory
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.Adapter.IGNORE_ITEM_VIEW_TYPE
import android.widget.ImageView
import com.android.java.androidjavatools.model.ResultItemInfo

class ResultDetailAdapter(context : Context, item : ResultItemInfo) : Adapter {
    private val mContext : Context = context
    private val mResultItem : ResultItemInfo = item

    private val mDataSetObservable = DataSetObservable()

    override fun registerDataSetObserver(observer: DataSetObserver) {
        mDataSetObservable.registerObserver(observer)
    }

    override fun unregisterDataSetObserver(observer: DataSetObserver) {
        mDataSetObservable.unregisterObserver(observer)
    }

    fun notifyDataSetChanged() {
        mDataSetObservable.notifyChanged()
    }

    override fun getCount(): Int {
        return 1
    }

    override fun getItem(position: Int): Any {
        return mResultItem
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun hasStableIds(): Boolean {
        return true
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val imageBytes: ByteArray? = mResultItem.image

        return if (imageBytes != null) {
            val image = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
            val resultImage = ImageView(mContext)
            resultImage.setImageBitmap(image)
            resultImage.scaleType = ImageView.ScaleType.FIT_CENTER

            resultImage
        } else {
            ImageView(mContext)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return IGNORE_ITEM_VIEW_TYPE
    }

    override fun getViewTypeCount(): Int {
        return 1
    }

    override fun isEmpty(): Boolean {
        return false
    }
}