//
//  ResultItemInfo.java
//
//  Created by Mathieu Delehaye on 24/01/2023.
//
//  AndroidJavaTools: A framework to develop Android apps with Java Technologies.
//
//  Copyright © 2023 Mathieu Delehaye. All rights reserved.
//
//
//  This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
//  Public License as published by
//  the Free Software Foundation, either version 3 of the License, or (at your option) any later version.0
//
//  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
//  warranty of MERCHANTABILITY or FITNESS
//  FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
//
//  You should have received a copy of the GNU Affero General Public License along with this program. If not, see
//  <https://www.gnu.org/licenses/>.

package com.android.java.androidjavatools.model.result;

import com.android.java.androidjavatools.controller.template.ItemWithImage;
import org.osmdroid.util.GeoPoint;

public class ResultItemInfo extends ItemWithImage {
    public final String mKey;
    private String mTitle;
    private String mDescription;
    private GeoPoint mLocation;
    private boolean mContentAllowed;

    public ResultItemInfo(String key, boolean displayBrand) {
        mKey = key;
        mContentAllowed = displayBrand;
    }

    public ResultItemInfo(String key, String title, String description, GeoPoint location, boolean displayBrand) {
        mKey = key;
        mTitle = title;
        mDescription = description;
        mLocation = location;
        mContentAllowed = displayBrand;
    }

    public String getKey() {
        return mKey;
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

    public void setLocation(GeoPoint value) {
        mLocation = value;
    }

    public boolean isContentAllowed() {
        return mContentAllowed;
    }
}