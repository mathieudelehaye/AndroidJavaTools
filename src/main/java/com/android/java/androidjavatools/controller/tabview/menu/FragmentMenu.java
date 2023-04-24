//
//  FragmentMenu.java
//
//  Created by Mathieu Delehaye on 28/12/2022.
//
//  AndroidJavaTools: A framework to develop Android apps in Java.
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

package com.android.java.androidjavatools.controller.tabview.menu;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.android.java.androidjavatools.Helpers;
import com.android.java.androidjavatools.databinding.FragmentMenuBinding;
import com.android.java.androidjavatools.model.AppUser;

public abstract class FragmentMenu extends Fragment {
    protected FragmentMenuBinding mBinding;

    @Override
    public View onCreateView(
        LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState
    ) {
        mBinding = FragmentMenuBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        Log.v("AndroidJavaTools", "Menu view created at timestamp: "
            + Helpers.getTimestamp());

        super.onViewCreated(view, savedInstanceState);

        switchLogoutButtonVisibility();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser) {
            Log.d("AndroidJavaTools", "Menu view becomes visible");
            switchLogoutButtonVisibility();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinding = null;
    }

    private void switchLogoutButtonVisibility() {
        // Show the logout button if the uid is a registered one. Hide the button otherwise
        mBinding.logOutMenu.setVisibility(
            (AppUser.getInstance().getAuthenticationType() == AppUser.AuthenticationType.REGISTERED) ?
                View.VISIBLE :
                View.GONE
        );
    }
}