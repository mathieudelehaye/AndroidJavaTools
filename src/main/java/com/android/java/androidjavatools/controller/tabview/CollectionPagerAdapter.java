//
//  CollectionPagerAdapter.java
//
//  Created by Mathieu Delehaye on 19/12/2022.
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

package com.android.java.androidjavatools.controller.tabview;

import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import com.android.java.androidjavatools.R;
import com.android.java.androidjavatools.controller.template.ResultProvider;
import com.android.java.androidjavatools.controller.template.SearchProvider;

abstract public class CollectionPagerAdapter extends FragmentStatePagerAdapter {
    protected ResultProvider mResultProvider;
    protected SearchProvider mSearchProvider;
    // current page of the app ViewPager
    protected FragmentActivity mActivity;
    private static int mCurrentPage = 0;

    public CollectionPagerAdapter(FragmentManager fm, FragmentActivity fa,
                                  ResultProvider rProvider,
                                  SearchProvider sProvider) {

        super(fm);
        mActivity = fa;
        mResultProvider = rProvider;
        mSearchProvider = sProvider;
    }

    public static int getPage() {
        return mCurrentPage;
    }

    public static void setPage(int value) {
        mCurrentPage = value;
    }

    @Override
    public Fragment getItem(int i) {
        return findTabFragment(i);
    }

    protected abstract Fragment findTabFragment(int i);

    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public CharSequence getPageTitle(int position) {

        Drawable image;

        switch (position) {
            case 0:
                image = mActivity.getResources().getDrawable(R.drawable.magnify);
                break;
            case 1:
                image = mActivity.getResources().getDrawable(R.drawable.heart_outline);
                break;
            case 2:
                image = mActivity.getResources().getDrawable(R.drawable.camera);
                break;
            case 3:
            default:
                image = mActivity.getResources().getDrawable(R.drawable.dots_horizontal);
                break;
        }

        image.setBounds(0, 0, image.getIntrinsicWidth(), image.getIntrinsicHeight());
        var sb = new SpannableString(" ");
        var imageSpan = new ImageSpan(image, ImageSpan.ALIGN_BOTTOM);
        sb.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        return sb;
    }

    @Override
    public int getItemPosition(Object object) {
        // Ensure that the view pager is reloaded when `notifyDataSetChanged` is called:
        return POSITION_NONE;
    }
}