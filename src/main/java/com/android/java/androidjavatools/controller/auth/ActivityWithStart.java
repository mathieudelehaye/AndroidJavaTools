//
//  ActivityWithStart.java
//
//  Created by Mathieu Delehaye on 2/01/2022.
//
//  AJT: An Android app to order and recycle cosmetics.
//
//  Copyright © 2022 Mathieu Delehaye. All rights reserved.
//
//
//  This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by
//  the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
//
//  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
//  FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
//
//  You should have received a copy of the GNU Affero General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.

package com.android.java.androidjavatools.controller.auth;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import com.android.java.androidjavatools.model.AppUser;
import com.android.java.androidjavatools.R;

public class ActivityWithStart extends AppCompatActivity {

    protected SharedPreferences mSharedPref;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Read the app preferences
        mSharedPref = getSharedPreferences(
            getString(R.string.lib_name), Context.MODE_PRIVATE);
    }

    public String getAnonymousUidFromPreferences() {
        if (mSharedPref == null) {
            Log.w("AJT", "Try to get the anonymous uid from the app preferences but "
                + "view not created");
            return "";
        }

        var anonymousUid = new StringBuilder();
        anonymousUid.append(mSharedPref.getString(getString(R.string.anonymous_uid), ""));

        if (!anonymousUid.toString().equals("")) {
            var uid = anonymousUid.toString();

            // Reuse the anonymous uid if it already exists in the app preferences
            Log.v("AJT", "Anonymous uid loaded from the app preferences: "
                + uid);

            return uid;
        } else {
            return "";
        }
    }

    public void setAnonymousUidToPreferences(String value) {
        if (mSharedPref == null) {
            Log.w("AJT", "Try to set the anonymous uid to the app preferences but "
                + "view not created");
            return;
        }

        Log.v("AJT", "Anonymous uid stored to the app preferences: "
            + value);

        mSharedPref.edit().putString(getString(R.string.anonymous_uid), value)
            .commit();
    }

    public void startAppWithUser(String _uid, AppUser.AuthenticationType _userType) {

        if (mSharedPref == null) {
            Log.w("AJT", "Try to start the app with a user but no preference loaded");
            return;
        }

        // Store the uid in the app preferences
        mSharedPref.edit().putString(getString(R.string.app_uid), _uid)
            .commit();
        Log.v("AJT", "Latest uid stored to the app preferences: " + _uid);

        // Update the current app user
        AppUser.getInstance().authenticate(_uid, _userType);

//        startActivity(new Intent(this, EBTabViewActivity.class));
    }
    
    private Class findStartActivity() {
        
        try {
            ActivityInfo[] activities = getActivityList();

            for (ActivityInfo activity : activities) {
                Log.d("AJT", "mdl findStartActivity: activity found: " + activity.name);
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("AJT", "Error while finding app activities: " + e.getMessage());
        }

        return null;
    }

    private ActivityInfo[] getActivityList() throws PackageManager.NameNotFoundException {
        PackageManager pm = this.getPackageManager();

        PackageInfo info = pm.getPackageInfo(getApplicationContext().getPackageName(), PackageManager.GET_ACTIVITIES);

        ActivityInfo[] list = info.activities;

        return list;
    }
}
