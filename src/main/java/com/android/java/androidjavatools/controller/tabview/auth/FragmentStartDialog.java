//
//  FragmentStartDialog.java
//
//  Created by Mathieu Delehaye on 3/02/2023.
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

package com.android.java.androidjavatools.controller.tabview.auth;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;
import com.android.java.androidjavatools.model.AuthManager;

public abstract class FragmentStartDialog extends FragmentAuthenticateDialog {
    protected Button mAnonymousLogIn;
    protected Button mEmailSignUp;
    protected Button mFacebookLogIn;
    protected Button mGoogleLogIn;
    protected Button mRegisteredLogIn;

    public FragmentStartDialog(AuthManager manager, Integer layoutId) {
        super(manager, layoutId);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }

    protected abstract Button getAnonymousLogIn();
    protected abstract Button getEmailSignUp();
    protected abstract Button getFacebookLogIn();
    protected abstract Button getGoogleLogIn();
    protected abstract Button getRegisteredLogIn();

    @Override
    protected Dialog initializeGUI(Dialog parentDialog) {
        // Do not use the parent dialog

        Dialog dialog = buildDialogFromLayout();

        mAnonymousLogIn = getAnonymousLogIn();
        mEmailSignUp = getEmailSignUp();
        mFacebookLogIn = getFacebookLogIn();
        mGoogleLogIn = getGoogleLogIn();
        mRegisteredLogIn = getRegisteredLogIn();

        if (mAnonymousLogIn == null) {
            Log.e("AJT", "No view found for the anonymous sign-in button on start dialog");
            return null;
        }
        mAnonymousLogIn.setOnClickListener(view -> mListener.onDialogAnonymousSignInClick(mThis));

        if (mEmailSignUp == null) {
            Log.e("AJT", "No view found when setting the email sign-up button");
            return null;
        }
        mEmailSignUp.setOnClickListener(view -> {
            mNavigatorManager.navigator().showFragment("signup");
        });

        if (mFacebookLogIn == null) {
            Log.e("AJT", "No view found when setting the Facebook sign-up button");
            return null;
        }
        mFacebookLogIn.setOnClickListener(view -> Toast.makeText(getContext(),
            "Facebook sign-up not yet available", Toast.LENGTH_SHORT).show());

        if (mGoogleLogIn == null) {
            Log.e("AJT", "No view found when setting the Google sign-up button");
            return null;
        }
        mGoogleLogIn.setOnClickListener(view -> Toast.makeText(getContext(),
            "Google sign-up not yet available", Toast.LENGTH_SHORT).show());

        if (mRegisteredLogIn == null) {
            Log.e("AJT", "No view found when setting the registered sign-in button");
            return null;
        }
        mRegisteredLogIn.setOnClickListener(view -> {
            mNavigatorManager.navigator().showFragment("login");
        });

        return dialog;
    }
}