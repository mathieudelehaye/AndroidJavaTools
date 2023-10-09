//
//  Navigator.java
//
//  Created by Mathieu Delehaye on 27/02/2023.
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

package com.android.java.androidjavatools.controller.template;

import android.util.Log;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import com.android.java.androidjavatools.controller.tabview.FragmentTabView;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Stack;

public class Navigator {
    public static class FragmentArgument
    {
        private Class<?> mType;
        private Object mValue;

        public FragmentArgument(Class<?> type, Object value) {
            mType = type;
            mValue = value;
        }
    };

    public interface NavigatorManager {
        Navigator navigator();
        void onNavigation(@NonNull String dest, @NonNull String orig);
        void toggleTabSwiping(boolean enable);
    }

    final private NavigatorManager mManager;
    final private FragmentManager mFragmentManager;
    final private int mContentLayoutId;
    private HashMap<String, Class<?>> mFragmentClasses = new HashMap<>();
    private HashMap<String, FragmentArgument[]> mFragmentConstructorArgs = new HashMap<>();
    private HashMap<String, Fragment> mFragments = new HashMap<>();
    private String mShownFragment;
    Stack<String> mPrevFragments = new Stack<>();
    private boolean mNavigationRecording = true;

    public void setNavigationRecording(boolean value) {
        Log.d("AJT", "Navigation recording set to: " + value);
        mNavigationRecording = value;
    }

    public Navigator(NavigatorManager manager, int contentLayoutId) {
        if (!(manager instanceof AppCompatActivity)) {
            Log.e("AJT", "Navigator manager must be an activity");
        }

        mManager = manager;
        mFragmentManager = ((AppCompatActivity)mManager).getSupportFragmentManager();
        mContentLayoutId = contentLayoutId;
    }

    public void declareFragment(String key, Class<?> fragmentType, FragmentArgument... constructorArguments) {
        Log.v("AJT", "Declaring fragment with class: " + fragmentType + " and constructor arguments "
            + Arrays.toString(constructorArguments));

        mFragmentClasses.put(key, fragmentType);
        mFragmentConstructorArgs.put(key, constructorArguments);
    }

    // TODO: find automatically the fragment type from its instance.
    public void updateFragment(String key, Fragment newFragment) {
        if (key.equals("")) {
            return;
        }

        Log.v("AJT", "Try to update a navigable fragment with the key: " + key);

        if (mFragments.containsKey(key)) {
            final Fragment oldFragment = mFragments.get(key);

            mFragments.put(key, newFragment);
            Log.v("AJT", "Fragment updated into the navigator registry");

            // Only update non-dialog fragments to the manager
            if (!(oldFragment instanceof DialogFragment)) {
                mFragmentManager
                    .beginTransaction()
                    .remove(oldFragment)
                    .replace(mContentLayoutId, newFragment)
                    .hide(newFragment)
                    .commit();

                Log.w("AJT", "Fragment updated in the fragment manager");
            }
        } else {
            Log.w("AJT", "Fragment not existing");
        }
    }

    public Fragment getShownFragment() {
        return mFragments.get(mShownFragment);
    }

    public Fragment getFragment(String key) {
        if (key.equals("")) {
            return null;
        }

        return mFragments.get(key);
    }

    public void showFragment(String key) {
        if (key.equals("")) {
            return;
        }

        Fragment fragmentToShow = mFragments.get(key);

        if (fragmentToShow == null) {
            Log.w("AJT", "Creating fragment for the key: " + key);

            createFragment(key);
            fragmentToShow = getFragment(key);
        }

        if (fragmentToShow instanceof DialogFragment) {
            // Dialog fragment
            DialogFragment dialog = (DialogFragment)fragmentToShow;
            dialog.show(mFragmentManager, "FragmentStartDialog");
        } else {
            // Other fragment
            mFragmentManager
                .beginTransaction()
                .show(fragmentToShow)
                .commit();
        }

        if (mShownFragment != null) {
            mManager.onNavigation(key, mShownFragment);
            mFragments.get(mShownFragment).setUserVisibleHint(false);

            if (mNavigationRecording) {
                mPrevFragments.push(mShownFragment);
                Log.v("AJT", "Fragment pushed to the previous fragment stack: "
                    + mPrevFragments.peek());
            }

            hideFragment(mShownFragment);
        }

        mShownFragment = key;
        fragmentToShow.setUserVisibleHint(true);
        Log.v("AJT", "Shown fragment updated to: " + mShownFragment);
    }

    public void back() {
        if (mPrevFragments.empty()) {
            Log.w("AJT", "Cannot navigate back, as previous fragment stack empty");
            return;
        }

        final String prevFragmentKey = mPrevFragments.pop();
        Log.v("AJT", "Fragment popped from the previous fragment stack: " + prevFragmentKey);

        if (prevFragmentKey == null || prevFragmentKey.equals("")) {
            Log.w("AJT", "Cannot navigate back, as previous fragment null or empty");
        }

        Fragment fragmentToShow = mFragments.get(prevFragmentKey);
        if (fragmentToShow == null) {
            Log.w("AJT", "No registered fragment to show for the key: " + prevFragmentKey);
            return;
        }

        if (mShownFragment != null) {
            mManager.onNavigation(prevFragmentKey, mShownFragment);
            hideFragment(mShownFragment);
        }

        if (fragmentToShow instanceof DialogFragment) {
            // Dialog fragment
            DialogFragment dialog = (DialogFragment)fragmentToShow;
            dialog.show(mFragmentManager, "FragmentStartDialog");
        } else {
            mFragmentManager
                .beginTransaction()
                .show(fragmentToShow)
                .commit();
        }

        mShownFragment = prevFragmentKey;
        fragmentToShow.setUserVisibleHint(true);
        Log.d("AJT", "Navigated back to the fragment of type: " + prevFragmentKey);
    }

    public void toggleTabSwiping(boolean enable) {
        if (!mFragments.containsKey("tab")) {
            Log.w("AJT", "Cannot toggle tab swiping, as no Tab fragment registered in the "
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

    private void createFragment(String key) {
        if (key.equals("")) {
            return;
        }

        final Class<?> fragmentType = mFragmentClasses.get(key);
        final FragmentArgument[] constructorArguments = mFragmentConstructorArgs.get(key);

        if (fragmentType == null) {
            return;
        }

        if (!mFragments.containsKey(key)) {
            Log.v("AJT", "Try to create a navigable fragment with the key: " + key);

            // Split argument struct data
            final var argumentTypes = new ArrayList<Class<?>>();
            final var argumentValues = new ArrayList<>();

            for (int i = 0; i < constructorArguments.length; i++) {
                var argument= constructorArguments[i];
                argumentTypes.add(argument.mType);
                argumentValues.add(argument.mValue);
            }

            Fragment fragment;

            try {
                // Create fragment instance
                Constructor<?> fragmentConstructor = fragmentType
                    .getConstructor(argumentTypes.toArray(new Class<?>[0]));

                fragment = (Fragment)fragmentConstructor.newInstance(argumentValues.toArray());
            } catch (IllegalAccessException |
                 InvocationTargetException |
                 InstantiationException |
                 NoSuchMethodException e) {

                Log.e("AJT", e.toString());
                return;
            }

            mFragments.put(key, fragment);
            Log.v("AJT", "Fragment created and added to the navigator registry");

            // Only add non-dialog fragments to the manager
            if (!(fragment instanceof DialogFragment)) {
                mFragmentManager
                    .beginTransaction()
                    .add(mContentLayoutId, fragment)
                    .hide(fragment)
                    .commit();

                Log.v("AJT", "Fragment added to the fragment manager");
            }
        } else {
            Log.w("AJT", "Fragment already existing");
        }
    }

    private void hideFragment(String fragment) {
        if (!mFragments.containsKey(fragment)) {
            Log.w("AJT", "Cannot hide fragment, as none created for the provided key: "
                + fragment);
            return;
        }

        Fragment fragmentToHide = mFragments.get(fragment);
        if (fragmentToHide instanceof DialogFragment) {
            DialogFragment dialog = (DialogFragment)fragmentToHide;
            dialog.dismiss();
        } else {
            mFragmentManager
                .beginTransaction()
                .hide(fragmentToHide)
                .commit();
        }

        fragmentToHide.setUserVisibleHint(false);
    }
}
