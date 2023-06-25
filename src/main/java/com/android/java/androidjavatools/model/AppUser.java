//
//  AppUser.java
//
//  Created by Mathieu Delehaye on 24/12/2022.
//
//  AndroidJavaTools: A framework to develop Android apps with Java Technologies.
//
//  Copyright © 2022 Mathieu Delehaye. All rights reserved.
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

import androidx.annotation.NonNull;

public class AppUser {
    public enum AuthenticationType {
        NONE,
        NOT_REGISTERED,
        REGISTERED
    }

    private static final AppUser mInstance = new AppUser();

    private AuthenticationType mAuthenticationType = AuthenticationType.NONE;

    private StringBuilder mId = new StringBuilder();

    // private constructor to avoid client applications using it
    private AppUser(){}

    public static AppUser getInstance() {
        return mInstance;
    }

    public AuthenticationType getAuthenticationType() {
        return this.mAuthenticationType;
    }

    public String getId() {
        return mId.toString();
    }

    public void authenticate(@NonNull String _uid, @NonNull AuthenticationType _type) {

        if (_uid.equals("") || (_type == AuthenticationType.NONE)) {
            return;
        }

        mAuthenticationType = _type;
        mId.setLength(0);
        mId.append(_uid);
    }

    public void logOut() {
        mAuthenticationType = AuthenticationType.NONE;
        mId.setLength(0);
    }
}
