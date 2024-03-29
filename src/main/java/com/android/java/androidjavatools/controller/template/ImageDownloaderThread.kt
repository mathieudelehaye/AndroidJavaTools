//
//  ImageDownloaderThread.kt
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

import com.android.java.androidjavatools.model.ItemWithImage
import com.android.java.androidjavatools.model.TaskCompletionManager
import java.util.HashMap

class ImageDownloaderThread (
    items : HashMap<String, ItemWithImage>,
    uRLs : HashMap<String, String>,
    onDownload: (String, ItemWithImage, Array<TaskCompletionManager>) -> Unit,
    vararg cbManager: TaskCompletionManager
) : Thread() {
    private val mItems : HashMap<String, ItemWithImage> = items
    private val mURLs : HashMap<String, String> = uRLs
    private val mOnDownload : (String, ItemWithImage, Array<TaskCompletionManager>) -> Unit = onDownload
    private var mCbManager: Array<TaskCompletionManager> = cbManager as Array<TaskCompletionManager>

    override fun run() {
        // Asynchronously start the image downloads
        for (key in ArrayList<String>(mItems.keys)) {
            mURLs[key]?.let { mItems[key]?.let { it1 -> mOnDownload(it, it1, mCbManager) } }
        }
    }
}