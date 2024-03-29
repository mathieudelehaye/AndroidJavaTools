//
//  NotSwipeableViewPager.java
//
//  Created by Mathieu Delehaye on 13/01/2023.
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

package com.android.java.androidjavatools.controller.tabview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import androidx.viewpager.widget.ViewPager;

public class NotSwipeableViewPager extends ViewPager {

    private boolean mSwipingEnabled;

    public NotSwipeableViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mSwipingEnabled = true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mSwipingEnabled ? super.onTouchEvent(event) : false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return mSwipingEnabled ? super.onInterceptTouchEvent(event) : false;
    }

    public void setSwipingEnabled(boolean enabled) {
        this.mSwipingEnabled = enabled;
    }

    public boolean isSwipingEnabled() {
        return mSwipingEnabled;
    }
}
