//
//  FragmentTabView.java
//
//  Created by Mathieu Delehaye on 17/12/2022.
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

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import com.android.java.androidjavatools.Helpers;
import com.android.java.androidjavatools.R;
import com.android.java.androidjavatools.controller.tabview.home.FragmentHome;
import com.android.java.androidjavatools.databinding.FragmentTabViewBinding;
import com.google.android.material.tabs.TabLayout;

public abstract class FragmentTabView extends Fragment {
    protected FragmentTabViewBinding mBinding;
    protected NotSwipeableViewPager mViewPager;
    private Activity mActivity;

    @Override
    public View onCreateView(
        LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState
    ) {
        mBinding = FragmentTabViewBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        Log.v("AJT", "App view created at timestamp: "
            + Helpers.getTimestamp());

        super.onViewCreated(view, savedInstanceState);

        mActivity = getActivity();

        mViewPager = mBinding.tabViewPager;
        TabLayout tabLayout = mBinding.tabViewTabbar;
        tabLayout.getTabAt(0).setIcon(com.android.java.androidjavatools.R.drawable.home);
        tabLayout.getTabAt(1).setIcon(com.android.java.androidjavatools.R.drawable.camera);
        tabLayout.setupWithViewPager(mViewPager);
        mViewPager.setOffscreenPageLimit(3);    // display up to 3 pages without recreating them at each swipe
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinding = null;
    }

    @Override
    public void onResume() {
        Log.v("AJT", "Tab view fragment resumed");

        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public void enableTabSwiping() {
        // Enable swiping gesture for the view pager
        if (mViewPager.isFakeDragging()) {
            Log.v("AJT", "Tab swiping enabled from the current page on");
            mViewPager.setSwipingEnabled(true);
            mViewPager.endFakeDrag();
        }
    }

    public void disableTabSwiping() {
        // Disable the swiping gesture for the view pager
        if (!mViewPager.isFakeDragging()) {
            Log.v("AJT", "Tab swiping disabled from the current page on");
            mViewPager.setSwipingEnabled(false);
            mViewPager.beginFakeDrag();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            Log.d("AJT", "Tab view becomes visible");

            if(mActivity == null) {
                return;
            }

            final View homeFragmentView = mActivity.findViewById(R.id.rp_history_title);
            if (homeFragmentView == null) {
                return;
            }

            final var fragmentHome = (FragmentHome)FragmentManager.findFragment(homeFragmentView);
            fragmentHome.updateRecentResults();
            fragmentHome.updateRecentSearches();
        }
    }
}