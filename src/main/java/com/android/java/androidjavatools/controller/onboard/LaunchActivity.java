//
//  LaunchActivity.java
//
//  Created by Mathieu Delehaye on 22/02/2023.
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

package com.android.java.androidjavatools.controller.onboard;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;

public abstract class LaunchActivity extends Activity {

    protected SharedPreferences mSharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //SplashScreen splashScreen = SplashScreen.installSplashScreen(this);

        super.onCreate(savedInstanceState);

        // Keep the splash screen visible for this Activity
        //splashScreen.setKeepOnScreenCondition(() -> true);

        startNextActivity();

        finish();
    }

    protected abstract void startNextActivity();
}
