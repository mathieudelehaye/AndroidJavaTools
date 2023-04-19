//
//  SearchResult.java
//
//  Created by Mathieu Delehaye on 26/02/2023.
//
//  AndroidJavaTools: A framework to develop Android apps in Java.
//
//  Copyright © 2023 Mathieu Delehaye. All rights reserved.
//
//
//  This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by
//  the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
//
//  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
//  FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
//
//  You should have received a copy of the GNU Affero General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.

package com.android.java.androidjavatools.model;

import android.util.Log;
import com.android.java.androidjavatools.model.ResultItemInfo;
import com.android.java.androidjavatools.model.TaskCompletionManager;
import com.android.java.androidjavatools.Helpers;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.ArrayList;

public class SearchResult {
    private ArrayList<ResultItemInfo> mResultItems = new ArrayList<>();
    private ArrayList<String> mImageUrls = new ArrayList<>();
    private int mReceivedImageNumber = 0;
    private final Object mImageUpdateLock = new Object();

    public ArrayList<ResultItemInfo> getResultItems() {
        return mResultItems;
    }

    public ResultItemInfo get(int index) {
        return mResultItems.get(index);
    }

    public void add(ResultItemInfo info, String imageURL) {
        mResultItems.add(info);
        mImageUrls.add(imageURL);
    }

    public void downloadImages(TaskCompletionManager... cbManager) {
        // Asynchronously download the images then update the view adapter
        for (int i = 0; i < mImageUrls.size(); i++) {
            downloadAndDisplayImage(mImageUrls.get(i), mResultItems.get(i), cbManager);
        }
    }

    private void downloadAndDisplayImage(String imageUrl, ResultItemInfo itemInfo,
        TaskCompletionManager... cbManager) {

        if (imageUrl == null || imageUrl.equals("") || itemInfo == null) {
            Log.w("AJT", "Try to download an image but some parameters is missing");
            return;
        }

        var storage = FirebaseStorage.getInstance();

        StorageReference gsReference = storage.getReferenceFromUrl(imageUrl);

        final long ONE_MEGABYTE = 1024 * 1024;

        gsReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(bytes -> {

            synchronized (mImageUpdateLock) {
                itemInfo.setImage(bytes);

                if (cbManager.length >= 1) {
                    cbManager[0].onSuccess();
                }

                mReceivedImageNumber++;
                if (mReceivedImageNumber == mImageUrls.size()) {
                    Log.v("AJT", "Last result image received at timestamp: "
                        + Helpers.getTimestamp());
                }
            }
        }).addOnFailureListener(exception -> {
            if (cbManager.length >= 1) {
                cbManager[0].onFailure();
            }
        });
    }
}
