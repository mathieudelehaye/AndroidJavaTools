//
//  ResultItemInfo.java
//
//  Created by Mathieu Delehaye on 24/01/2023.
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

package com.android.java.androidjavatools.model;

import org.osmdroid.util.GeoPoint;

public class ResultItemInfo {
    private String mkey;
    private String mTitle;
    private String mDescription;
    private GeoPoint mLocation;
    private byte[] mImage;
    private boolean mShowImage;

    public ResultItemInfo(String key, String title, String description, GeoPoint location, byte[] image,
        boolean displayBrand) {

        mkey = key;
        mTitle = title;
        mDescription = description;
        mLocation = location;
        mImage = image;
        mShowImage = displayBrand;
    }

    public String getKey() {
        return mkey;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public GeoPoint getLocation() {
        return mLocation;
    }

    public byte[] getImage() {
        return mImage;
    }

    public void setImage(byte[] image) {
        mImage = image;
    }

    public boolean isImageShown() {
        return mShowImage;
    }
}