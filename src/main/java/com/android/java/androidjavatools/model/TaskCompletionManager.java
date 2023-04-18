//
//  TaskCompletionManager.java
//
//  Created by Mathieu Delehaye on 15/12/2022.
//
//  AndroidJavaTools: A framework to develop Android apps in Java.
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

public interface TaskCompletionManager {
    // Callback function if the task is successful
    void onSuccess();

    // Callback function if the task is failing
    void onFailure();
}
