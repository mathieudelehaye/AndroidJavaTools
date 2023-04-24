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
import androidx.fragment.app.FragmentTransaction;
import java.util.HashMap;
import java.util.Stack;

public class Navigator {
    public interface NavigatorManager {
        Navigator navigator();
        void onNavigation(String dest, String orig);
    }

    final private NavigatorManager mManager;
    final private int mContentLayoutId;
    private HashMap<String, Fragment> mFragments = new HashMap<>();
    private String mShownFragment;
    Stack<String> mPrevFragments = new Stack<>();

    public Navigator(NavigatorManager manager, int contentLayoutId) {
        if (!(manager instanceof AppCompatActivity)) {
            Log.e("AndroidJavaTools", "Navigator manager must be an activity");
        }

        mManager = manager;
        mContentLayoutId = contentLayoutId;
    }

    public void createFragment(Class<?> fragmentType) {
        if (!(fragmentType.isAssignableFrom(Fragment.class))) {
            Log.e("AndroidJavaTools", "Navigator cannot create a fragment of a type "
                + "not extending Fragment");
            return;
        }

        final String key = fragmentType.getTypeName();
        Log.v("AndroidJavaTools", "Try to create a navigable fragment with the key: " + key);

        if (!mFragments.containsKey(key)) {
            try {
                var fragment = (Fragment)fragmentType.newInstance();

                mFragments.put(key, fragment);
                Log.v("AndroidJavaTools", "Fragment created and added to the navigator registry");

                ((AppCompatActivity)mManager).getSupportFragmentManager().beginTransaction()
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
    public void updateFragment(Class<?> fragmentType, Fragment newFragment) {
        if (!(fragmentType.isAssignableFrom(Fragment.class))) {
            Log.e("AndroidJavaTools", "Navigator cannot create a fragment of a type not extending Fragment");
            return;
        }

        final String key = fragmentType.getTypeName();
        Log.v("AndroidJavaTools", "Try to update a navigable fragment with the key: " + key);

        if (mFragments.containsKey(key)) {
            final Fragment oldFragment = mFragments.get(key);

            final FragmentTransaction transaction =
                ((AppCompatActivity)mManager).getSupportFragmentManager().beginTransaction();

            mFragments.put(key, newFragment);
            Log.v("AndroidJavaTools", "Fragment updated into the navigator registry");

            transaction
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

    public void showFragment(@NonNull String key) {
        if (!mFragments.containsKey(key)) {
            Log.w("AndroidJavaTools", "Cannot show fragment, as none created for the provided key: " + key);
            return;
        }
        Fragment frag = mFragments.get(key);

        if (mShownFragment != null) {
            hideFragment(mShownFragment);

            mPrevFragments.push(mShownFragment);
            Log.v("AndroidJavaTools", "Fragment pushed to the previous fragment stack: "
                + mPrevFragments.peek());

            mManager.onNavigation(key, mShownFragment);
        }

        mShownFragment = key;
        Log.v("AndroidJavaTools", "Shown fragment updated to " + mShownFragment);

        ((AppCompatActivity)mManager).getSupportFragmentManager().beginTransaction()
            .show(frag)
            .commit();

        frag.setUserVisibleHint(true);
    }

    public void back() {
        if (mPrevFragments.empty()) {
            Log.w("BeautyAndroid", "Cannot navigate back, as previous fragment stack empty");
            return;
        }

        final String prevFragmentKey = mPrevFragments.pop();

        Log.v("BeautyAndroid", "Fragment popped from the previous fragment stack: " + prevFragmentKey);
        Log.d("BeautyAndroid", "Navigating back to the fragment of type " + prevFragmentKey);

        mManager.onNavigation(prevFragmentKey, mShownFragment);

        ((AppCompatActivity)mManager).getSupportFragmentManager().beginTransaction()
            .show(mFragments.get(prevFragmentKey))
            .commit();
    }

    private void hideFragment(String key) {
        if (!mFragments.containsKey(key)) {
            Log.w("AndroidJavaTools", "Cannot hide fragment, as none created for the provided key: " + key);
            return;
        }
        Fragment frag = mFragments.get(key);

        ((AppCompatActivity)mManager).getSupportFragmentManager().beginTransaction()
            .hide(frag)
            .commit();

        mShownFragment = null;

        frag.setUserVisibleHint(false);
    }
}
