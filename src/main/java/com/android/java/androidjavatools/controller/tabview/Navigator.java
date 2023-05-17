//
//  Navigator.java
//
//  Created by Mathieu Delehaye on 27/02/2023.
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

package com.android.java.androidjavatools.controller.tabview;

import android.util.Log;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import java.util.HashMap;
import java.util.Stack;

public class Navigator {
    public interface NavigatorManager {
        Navigator navigator();
        void onNavigation(@NonNull String dest, @NonNull String orig);
        void toggleTabSwiping(boolean enable);
    }

    final private NavigatorManager mManager;
    final private FragmentManager mFragmentManager;
    final private int mContentLayoutId;
    private HashMap<String, Fragment> mFragments = new HashMap<>();
    private String mShownFragment;
    Stack<String> mPrevFragments = new Stack<>();

    public Navigator(NavigatorManager manager, int contentLayoutId) {
        if (!(manager instanceof AppCompatActivity)) {
            Log.e("AndroidJavaTools", "Navigator manager must be an activity");
        }

        mManager = manager;
        mFragmentManager = ((AppCompatActivity)mManager).getSupportFragmentManager();
        mContentLayoutId = contentLayoutId;
    }

    public void createFragment(String key, Class<?> fragmentType) {
        if (key.equals("")) {
            return;
        }

        Log.v("AndroidJavaTools", "Try to create a navigable fragment with the key: " + key);

        if (!mFragments.containsKey(key)) {
            try {
                var fragment = (Fragment)fragmentType.newInstance();

                mFragments.put(key, fragment);
                Log.v("AndroidJavaTools", "Fragment created and added to the navigator registry");

                mFragmentManager
                    .beginTransaction()
                    .add(mContentLayoutId, fragment)
                    .hide(fragment)
                    .commit();

                Log.v("AndroidJavaTools", "Fragment added to the fragment manager");
            } catch (IllegalAccessException iAE) {
                Log.e("AndroidJavaTools", String.valueOf(iAE));
            } catch (InstantiationException iE) {
                Log.e("AndroidJavaTools", String.valueOf(iE));
            }
        } else {
            Log.w("AndroidJavaTools", "Fragment already existing");
        }
    }

    // TODO: find automatically the fragment type from its instance.
    public void updateFragment(String key, Fragment newFragment) {
        if (key.equals("")) {
            return;
        }

        Log.v("AndroidJavaTools", "Try to update a navigable fragment with the key: " + key);

        if (mFragments.containsKey(key)) {
            final Fragment oldFragment = mFragments.get(key);

            mFragments.put(key, newFragment);
            Log.v("AndroidJavaTools", "Fragment updated into the navigator registry");

            mFragmentManager
                .beginTransaction()
                .remove(oldFragment)
                .replace(mContentLayoutId, newFragment)
                .hide(newFragment)
                .commit();

            Log.w("AndroidJavaTools", "Fragment updated in the fragment manager");
        } else {
            Log.w("AndroidJavaTools", "Fragment not existing");
        }
    }

    public Fragment getShownFragment() {
        return mFragments.get(mShownFragment);
    }

    public void showFragment(String key) {
        if (key.equals("")) {
            return;
        }

        Fragment fragmentToShow = mFragments.get(key);

        if (fragmentToShow == null) {
            Log.w("AndroidJavaTools", "No registered fragment to show for the key: " + key);
            return;
        }

        if (mShownFragment != null) {
            mManager.onNavigation(key, mShownFragment);
            mFragments.get(mShownFragment).setUserVisibleHint(false);

            mPrevFragments.push(mShownFragment);
            Log.v("AndroidJavaTools", "Fragment pushed to the previous fragment stack: "
                + mPrevFragments.peek());

            hideFragment(mShownFragment);
        }

        mFragmentManager
            .beginTransaction()
            .show(fragmentToShow)
            .commit();

        mShownFragment = key;
        fragmentToShow.setUserVisibleHint(true);
        Log.v("AndroidJavaTools", "Shown fragment updated to " + mShownFragment);
    }

    public void back() {
        if (mPrevFragments.empty()) {
            Log.w("AndroidJavaTools", "Cannot navigate back, as previous fragment stack empty");
            return;
        }

        final String prevFragmentKey = mPrevFragments.pop();
        Log.v("AndroidJavaTools", "Fragment popped from the previous fragment stack: " + prevFragmentKey);

        if (prevFragmentKey == null || prevFragmentKey.equals("")) {
            Log.w("AndroidJavaTools", "Cannot navigate back, as previous fragment null or empty");
        }

        Fragment fragmentToShow = mFragments.get(prevFragmentKey);
        if (fragmentToShow == null) {
            Log.w("AndroidJavaTools", "No registered fragment to show for the key: " + prevFragmentKey);
            return;
        }

        if (mShownFragment != null) {
            mManager.onNavigation(prevFragmentKey, mShownFragment);
            hideFragment(mShownFragment);
        }

        mFragmentManager
            .beginTransaction()
            .show(mFragments.get(prevFragmentKey))
            .commit();

        mShownFragment = prevFragmentKey;
        fragmentToShow.setUserVisibleHint(true);
        Log.d("AndroidJavaTools", "Navigated back to the fragment of type " + prevFragmentKey);
    }

    public void toggleTabSwiping(boolean enable) {
        if (!mFragments.containsKey("tab")) {
            Log.w("AndroidJavaTools", "Cannot toggle tab swiping, as no Tab fragment registered in the "
                + "navigator");
            return;
        }
        var tabFragment = (FragmentTabView)mFragments.get("tab");

        // Enable or disable swiping gesture for the view pager
        if (enable) {
            tabFragment.enableTabSwiping();
        } else {
            tabFragment.disableTabSwiping();
        }
    }

    private void hideFragment(String fragment) {
        if (!mFragments.containsKey(fragment)) {
            Log.w("AndroidJavaTools", "Cannot hide fragment, as none created for the provided key: "
                + fragment);
            return;
        }
        Fragment fragmentToHide = mFragments.get(fragment);

        mFragmentManager
            .beginTransaction()
            .hide(fragmentToHide)
            .commit();

        fragmentToHide.setUserVisibleHint(false);
    }
}
