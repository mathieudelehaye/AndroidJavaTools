//
//  FragmentSignupDialog.java
//
//  Created by Mathieu Delehaye on 5/02/2023.
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
import android.widget.EditText;
import com.android.java.androidjavatools.model.user.AuthManager;

public abstract class FragmentSignupDialog extends FragmentAuthenticateDialog {
    protected Button mAnonymousLogIn;
    protected EditText mEmailToRegister;
    protected EditText mPasswordToRegister;
    protected EditText mPasswordConfirmation;
    protected Button mConfirmLogIn;
    protected Button mBack;

    protected abstract Button getAnonymousLogIn();
    protected abstract EditText getEmailToRegister();
    protected abstract EditText getPasswordToRegister();
    protected abstract EditText getPasswordConfirmation();
    protected abstract Button getConfirmSignUp();
    protected abstract Button getBack();

    public FragmentSignupDialog(AuthManager manager, Integer layoutId) {
        super(manager, layoutId);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }

    @Override
    protected Dialog initializeGUI(Dialog parentDialog) {
        // Do not use the parent dialog

        Dialog dialog = buildDialogFromLayout();

        mAnonymousLogIn = getAnonymousLogIn();
        mEmailToRegister = getEmailToRegister();
        mPasswordToRegister = getPasswordToRegister();
        mPasswordConfirmation = getPasswordConfirmation();
        mConfirmLogIn = getConfirmSignUp();
        mBack = getBack();

        if (mAnonymousLogIn == null) {
            Log.e("AJT", "No view found for the anonymous sign-in button on signup dialog");
            return null;
        }
        mAnonymousLogIn.setOnClickListener(view -> mListener.onDialogAnonymousSignInClick(mThis));

        if (mConfirmLogIn == null) {
            Log.e("AJT", "No view found for the confirm button on signup dialog");
            return null;
        }
        mConfirmLogIn.setOnClickListener(view -> mListener.onDialogSignUpClick(mThis,
            new AuthenticateDialogListener.SigningDialogCredentialViews(
                mEmailToRegister,
                mPasswordToRegister,
                mPasswordConfirmation)));

        if (mBack == null) {
            Log.e("AJT", "No view found for the back button on signup dialog");
            return null;
        }
        mBack.setOnClickListener(view -> {
            mNavigatorManager.navigator().showFragment("start");
        });

        return dialog;
    }
}