//
//  UserGeoPosition.kt
//
//  Created by Mathieu Delehaye on 25/06/2023.
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

package com.android.java.androidjavatools.model

import android.location.Location
import java.util.*

class GeoPosition() {
    var mLocation : Location? = null
    var mTimestamp : Long = 0

    constructor(location : Location) : this() {
        mLocation = location
    }

    fun getLocation() : Location {
        return mLocation!!
    }

    fun setLocation(location : Location) {
        mLocation = location
        mTimestamp = getTimestamp()
    }

    fun updateRequired () : Boolean {
        return if (mTimestamp > 0)
            // (time elapsed * speed in mps) > 10 m.
            // If speed == 0, no update is required.
            ((Date().time / 1000) - mTimestamp) * (mLocation?.speed ?: .0f) > 10
        else true
    }

    private fun getTimestamp() : Long {
        return Date().time / 1000 // secs since January 1, 1970, 00:00:00 GTM
    }
}