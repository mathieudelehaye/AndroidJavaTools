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

import android.annotation.SuppressLint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import com.android.java.androidjavatools.databinding.FragmentSavedListBinding;
import com.android.java.androidjavatools.R;
import org.jetbrains.annotations.NotNull;

public abstract class FragmentSavedList extends Fragment {
    protected FragmentSavedListBinding mBinding;
    private Toolbar mToolbar;

    @Override
    public View onCreateView(
        LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState
    ) {
        mBinding = FragmentSavedListBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mToolbar = mBinding.savedListViewToolbarLayout.findViewById(R.id.ajt_toolbar);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        toggleToolbar(isVisibleToUser);
    }

    protected void setToolbarBackgroundColor(int color) {
        mToolbar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(color)));
    }

    private void toggleToolbar(Boolean visible) {
        Log.v("AndroidJavaTools", "Saved page toolbar " + (visible ? "shown" : "hidden"));

        mToolbar.setVisibility(visible ? View.VISIBLE : View.GONE);
    }
}
