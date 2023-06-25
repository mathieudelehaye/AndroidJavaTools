//
//  SearchResult.java
//
//  Created by Mathieu Delehaye on 26/02/2023.
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

package com.android.java.androidjavatools.model;

import android.util.Log;
import com.android.java.androidjavatools.Helpers;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.ArrayList;
import java.util.HashMap;

public class SearchResult {
    class ImageDownloaderThread extends Thread {
        private TaskCompletionManager[] mCbManager;

        public void setCallbackManagers(TaskCompletionManager... cbManager) {
            mCbManager = cbManager;
        }

        @Override
        public void run() {
            // Asynchronously start the image downloads
            for (String key: new ArrayList<>(mResultItems.keySet())) {
                downloadAndDisplayImage(mImageUrls.get(key), mResultItems.get(key), mCbManager);
            }
        }
    }

    private HashMap<String, ResultItemInfo> mResultItems = new HashMap<>();
    private HashMap<String, String> mImageUrls = new HashMap<>();
    private int mReceivedImageNumber = 0;
    private final Object mImageUpdateLock = new Object();

    public ResultItemInfo get(String key) {
        return mResultItems.get(key);
    }

    public ResultItemInfo get(int index) {
        ArrayList<String> keys = new ArrayList<>(mResultItems.keySet());
        return mResultItems.get(keys.get(index));
    }

    public int size() {
        return mResultItems.size();
    }

    public void add(String key, ResultItemInfo info, String imageURL) {
        mResultItems.put(key, info);
        mImageUrls.put(key, imageURL);
    }

    public void downloadImages(TaskCompletionManager... cbManager) {
        final ImageDownloaderThread imageDownloader = new ImageDownloaderThread();
        imageDownloader.setCallbackManagers(cbManager);
        imageDownloader.start();
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
