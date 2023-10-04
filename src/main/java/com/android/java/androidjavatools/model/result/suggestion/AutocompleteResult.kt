//
//  AutocompleteResult.kt
//
//  Created by Mathieu Delehaye on 3/10/2023.
//
//  AndroidDemo: A demo mobile app to rent an accommodation, re-using the AndroidJavaTools library.
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

package com.android.java.androidjavatools.model.result.suggestion

class AutocompleteResult {
    var type: String? = null
    var text: AutocompleteResultText? = null
    var link: String? = null
    var address: AutocompleteResultAddress? = null
    var geo: AutocompleteResultGeo? = null

    fun getInfo() : String {
        return "{" +
            "  \"type\": \"$type\", " +
            "  \"text\": \"${if (text != null) text!!.getInfo() else ""}\", " +
            "  \"link\": \"$link\", " +
            "  \"address\": \"${if (address != null) address!!.getInfo() else ""}\", " +
            "  \"geo\": \"${if (geo != null) geo!!.getInfo() else ""}\"" +
        "}"
    }
}