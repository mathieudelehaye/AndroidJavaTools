//
//  FragmentAuthenticateDialog.java
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
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import com.android.java.androidjavatools.controller.template.Navigator;
import com.android.java.androidjavatools.model.AuthManager;
import org.jetbrains.annotations.NotNull;

public abstract class FragmentAuthenticateDialog extends DialogFragment {
    protected Context mContext;
    protected Navigator.NavigatorManager mNavigatorManager;

    // Use this instance of the interface to deliver action events from the dialog modal
    protected AuthenticateDialogListener mListener;
    protected int mLayoutId;
    protected FragmentAuthenticateDialog mThis;
    protected View mContainerView;

    public FragmentAuthenticateDialog(AuthManager manager, Integer layoutId) {
        mListener = manager;
        mLayoutId = layoutId;
    }

    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        mThis = this;
        Dialog dialog = super.onCreateDialog(savedInstanceState);

        // Inflate the content layout, so the derived fragments can configure the widgets
        var inflater = requireActivity().getLayoutInflater();
        mContainerView = inflater.inflate(mLayoutId, null);

        return initializeGUI(dialog);
    }

    protected abstract Dialog initializeGUI(Dialog parentDialog);

    protected Dialog buildDialogFromLayout() {
        // Use the Builder class for convenient dialog construction
        var builder = new AlertDialog.Builder(getActivity());

        builder.setView(mContainerView);

        // Set the background as transparent and prevent the dialog from cancelling
        Dialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        return dialog;
    }

    @Override
    public void onAttach (Context context) {
        super.onAttach(context);

        mContext = context;
        mNavigatorManager = (Navigator.NavigatorManager)mContext;
    }
}
