//
//  SetWithImages.java
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
import com.android.java.androidjavatools.controller.template.ImageDownloaderThread;
import com.android.java.androidjavatools.controller.template.ItemWithImage;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.ArrayList;
import java.util.HashMap;
import kotlin.Unit;

public class SetWithImages {
    private HashMap<String, ItemWithImage> mItems = new HashMap<>();
    private HashMap<String, String> mImageUrls = new HashMap<>();
    private int mReceivedImageNumber = 0;
    private final Object mImageUpdateLock = new Object();

    public ItemWithImage get(String key) {
        return mItems.get(key);
    }

    public ItemWithImage get(int index) {
        ArrayList<String> keys = new ArrayList<>(mItems.keySet());
        return mItems.get(keys.get(index));
    }

    public int size() {
        return mItems.size();
    }

    public void add(String key, ItemWithImage info, String imageURL) {
        mItems.put(key, info);
        mImageUrls.put(key, imageURL);
    }

    public void downloadImages(TaskCompletionManager... cbManager) {
        final var imageDownloader = new ImageDownloaderThread(mItems, mImageUrls,
            this::downloadImage, cbManager);
        imageDownloader.start();
    }

    private Unit downloadImage(String imageUrl, ItemWithImage itemInfo,
                               TaskCompletionManager... cbManager) {

        if (imageUrl == null || imageUrl.equals("") || itemInfo == null) {
            Log.w("AJT", "Try to download an image but some parameters is missing");
            return null;
        }

        var storage = FirebaseStorage.getInstance();

        StorageReference gsReference = storage.getReferenceFromUrl(imageUrl);

        final long ONE_MEGABYTE = 1024 * 1024;

        gsReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(bytes -> {

            synchronized (mImageUpdateLock) {
                itemInfo.setImage(Helpers.toObjects(bytes));

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

        return Unit.INSTANCE;
    }
}
