//
//  FragmentSavedList.java
//
//  Created by Mathieu Delehaye on 29/04/2023.
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

package com.android.java.androidjavatools.controller.tabview.saved;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.android.java.androidjavatools.Helpers;
import com.android.java.androidjavatools.controller.tabview.FragmentToolbar;
import com.android.java.androidjavatools.databinding.FragmentSavedListBinding;

public abstract class FragmentSavedList extends Fragment {
    protected FragmentSavedListBinding mBinding;
    private FragmentToolbar mFragmentToolbar;

    @Override
    public View onCreateView(
        LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState
    ) {
        mBinding = FragmentSavedListBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        Log.v("AndroidJavaTools", "Saved list view created at timestamp: "
            + Helpers.getTimestamp());

        mFragmentToolbar = new FragmentToolbar();

        getChildFragmentManager()
            .beginTransaction()
            .show(mFragmentToolbar)
            .commit();

        super.onViewCreated(view, savedInstanceState);
    }
}