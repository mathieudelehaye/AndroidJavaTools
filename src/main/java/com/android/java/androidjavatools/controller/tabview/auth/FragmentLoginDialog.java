//
//  FragmentLoginDialog.java
//
//  Created by Mathieu Delehaye on 4/02/2023.
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

package com.android.java.androidjavatools.controller.tabview.auth;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.android.java.androidjavatools.model.AuthManager;

public abstract class FragmentLoginDialog extends FragmentAuthenticateDialog {
    protected Button mAnonymousLogIn;
    protected Button mFacebookLogIn;
    protected Button mGoogleLogIn;
    protected Button mConfirmLogIn;
    protected Button mResetPassword;
    protected Button mEmailSignUp;
    protected EditText mRegisteredEmail;
    protected EditText mRegisteredPassword;

    public FragmentLoginDialog(AuthManager manager, Integer layoutId) {
        super(manager, layoutId);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }

    protected abstract Button getAnonymousLogIn();
    protected abstract Button getFacebookLogIn();
    protected abstract Button getGoogleLogIn();
    protected abstract Button getConfirmLogIn();
    protected abstract Button getResetPassword();
    protected abstract Button getEmailSignUp();
    protected abstract EditText getRegisteredEmail();
    protected abstract EditText getRegisteredPassword();

    @Override
    protected Dialog initializeGUI(Dialog parentDialog) {
        // Do not use the parent dialog

        Dialog dialog = buildDialogFromLayout();

        mAnonymousLogIn = getAnonymousLogIn();
        mFacebookLogIn = getFacebookLogIn();
        mGoogleLogIn = getGoogleLogIn();
        mConfirmLogIn = getConfirmLogIn();
        mResetPassword = getResetPassword();
        mEmailSignUp = getEmailSignUp();
        mRegisteredEmail = getRegisteredEmail();
        mRegisteredPassword = getRegisteredPassword();

        if (mAnonymousLogIn == null) {
            Log.e("EBT", "No view found for the anonymous sign-in button on login dialog");
            return null;
        }
        mAnonymousLogIn.setOnClickListener(view -> mListener.onDialogAnonymousSignInClick(mThis));

        if (mFacebookLogIn == null) {
            Log.e("EBT", "No view found when setting the Facebook sign-in button");
            return null;
        }
        mFacebookLogIn.setOnClickListener(view -> Toast.makeText(getContext(),
            "Facebook sign-in not yet available", Toast.LENGTH_SHORT).show());

        if (mGoogleLogIn == null) {
            Log.e("EBT", "No view found when setting the Google sign-in button");
            return null;
        }
        mGoogleLogIn.setOnClickListener(view -> Toast.makeText(getContext(),
            "Google sign-in not yet available", Toast.LENGTH_SHORT).show());

        if (mConfirmLogIn == null) {
            Log.e("EBT", "No view found for the confirm button on login dialog");
            return null;
        }
        mConfirmLogIn.setOnClickListener(view -> mListener.onDialogRegisteredSignInClick(mThis,
            new AuthenticateDialogListener.SigningDialogCredentialViews(mRegisteredEmail, mRegisteredPassword,
                null)));

        if (mResetPassword == null) {
            Log.e("EBT", "No view found for the reset password button on login dialog");
            return null;
        }
        mResetPassword.setOnClickListener(view -> mListener.onDialogResetPasswordClick(mThis,
            new AuthenticateDialogListener.SigningDialogCredentialViews(mRegisteredEmail, mRegisteredPassword,
                null)));

        if (mEmailSignUp == null) {
            Log.e("EBT", "No view found for the sign-up button on login dialog");
            return null;
        }
        mEmailSignUp.setOnClickListener(view -> {
            mNavigatorManager.navigator().showFragment("signup");
        });

        return dialog;
    }
}