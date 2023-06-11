//
//  ComponentSet.java
//
//  Created by Mathieu Delehaye on 27/05/2023.
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

package com.android.java.androidjavatools.controller.template

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.java.androidjavatools.R
import com.android.java.androidjavatools.controller.template.Navigator.NavigatorManager

@Composable
fun buttonWithText(
    title: String
    , color: Color
    , width: Dp = 188.dp
    , height: Dp =  60.dp
    , radius: Dp = 15.dp
    , onClick: () -> Unit
) {
    Button(
        modifier = Modifier
            .width(width = width)
            .height(height = height)
        , shape = RoundedCornerShape(size = radius)
        , border = BorderStroke(
            1.dp
            , Color.Black
        )
        , onClick = {
            onClick()
        }
        , colors = ButtonDefaults.buttonColors(
            backgroundColor = color
        )
    ) {
        Text(
            text = title
            , fontWeight = FontWeight.W400
            , fontSize = 22.sp
            , textAlign = TextAlign.Center
        )
    }
}

@Preview
@Composable
fun buttonWithTextPreview() {
    buttonWithText("Hello", Color.Blue) {}
}

@Composable
fun backButton(
    navigatorManager : NavigatorManager?
) {
    Button(
        modifier = Modifier
            .width(width = 50.dp)
            .height(height = 50.dp)
        , colors = ButtonDefaults.buttonColors(
        backgroundColor = Color.Black
    )
        , shape = CircleShape
        , onClick = {
            navigatorManager?.navigator()?.back()
        }
    ) {
        // TODO: improve Back button integration
        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.chevron_left)
            , contentDescription = "Back icon"
            , tint = Color.White
            , modifier = Modifier
            .width(width = 60.dp)
            .height(height = 60.dp)
        )
    }
}

@Preview
@Composable
fun backButtonPreview() {
    backButton(null)
}