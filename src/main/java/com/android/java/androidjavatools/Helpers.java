//
//  Helpers.java
//
//  Created by Mathieu Delehaye on 27/12/2022.
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

package com.android.java.androidjavatools;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.inputmethod.InputMethodManager;
import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Helpers {

    private static long mStartTimestamp = 0;

    // String
    public static boolean isEmail(String text) {
        return (!TextUtils.isEmpty((CharSequence) text) && Patterns.EMAIL_ADDRESS
            .matcher((CharSequence) text).matches());
    }

    public static boolean isEmpty(String text) {
        return TextUtils.isEmpty((CharSequence)text);
    }

    // Time
    public static void startTimestamp() {
        mStartTimestamp = (new Date()).getTime();
    }

    public static long getTimestamp() {
        return ((new Date()).getTime() - mStartTimestamp);
    }

    public static Date parseTime(SimpleDateFormat format, String time) {
        try {
            return format.parse(time);
        } catch (ParseException e) {
            Log.e("AJT", "Error while parsing the time from database: "
                + e);

            return new Date();
        }
    }

    public static Date getDayBeforeDate(Date date) {
        return new java.util.Date(date.getTime() - 1000 * 60 * 60 * 24);    // ms in 1 day
    }

    public static int compareYearDays(Date d1, Date d2) {
        if (d1.getYear() != d2.getYear()) {
            return d1.getYear() - d2.getYear();
        }

        if (d1.getMonth() != d2.getMonth()) {
            return d1.getMonth() - d2.getMonth();
        }

        return (d1.getDate() - d2.getDate());
    }

    public static void toggleKeyboard(Context context, boolean visible) {
        Log.d("AJT", "Soft keyboard " + (visible ? "shown" : "hidden"));

        ((InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE))
            .toggleSoftInput(
                visible ? InputMethodManager.RESULT_SHOWN : InputMethodManager.RESULT_HIDDEN, 0);
    }

    public static byte[] toPrimitives(Byte[] oBytes) {
        byte[] bytes = new byte[oBytes.length];
        for(int i = 0; i < oBytes.length; i++){
            bytes[i] = oBytes[i];
        }
        return bytes;

    }

    public static Byte[] toObjects(byte[] bytesPrim) {
        Byte[] bytes = new Byte[bytesPrim.length];
        int i = 0;
        for (byte b : bytesPrim) bytes[i++] = b; //Autoboxing
        return bytes;

    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public static Byte[] getPlaceholderImageByteArray(Activity activity) {
        final Bitmap placeholderBitmap =
            ((BitmapDrawable)(activity.getDrawable(R.drawable.camera_raster))).getBitmap();
        final var stream = new ByteArrayOutputStream();
        placeholderBitmap.compress(Bitmap.CompressFormat.PNG, 90, stream);
        return Helpers.toObjects(stream.toByteArray());
    }
}
