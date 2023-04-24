//
//  FragmentHelpDialog.java
//
//  Created by Mathieu Delehaye on 17/02/2023.
//
//  AndroidJavaTools: A framework to develop Android apps in Java.
//
//  Copyright © 2023 Mathieu Delehaye. All rights reserved.
//
//
//  This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by
//  the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
//
//  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
//  FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
//
//  You should have received a copy of the GNU Affero General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.

package com.android.java.androidjavatools.controller.tabview.dialog;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import com.android.java.androidjavatools.R;
import com.android.java.androidjavatools.databinding.FragmentHelpDialogBinding;

public class FragmentHelpDialog extends DialogFragment {
    private FragmentHelpDialogBinding mBinding;
    private String mTextToDisplay;

    public FragmentHelpDialog(String text) {
        mTextToDisplay = text;
    }

    @Override
    public View onCreateView(
        LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState
    ) {
        mBinding = FragmentHelpDialogBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);

        // Use the Builder class for convenient dialog construction
        var builder = new AlertDialog.Builder(getActivity());

        // Get the layout inflater
        var inflater = requireActivity().getLayoutInflater();
        View rootView = inflater.inflate(R.layout.fragment_help_dialog, null);
        builder.setView(rootView);

        Dialog dialog = builder.create();

        // Configure the dialog
        Button closeHelpDialog = rootView.findViewById(R.id.close_help_dialog);
        if (closeHelpDialog == null) {
            Log.e("AndroidJavaTools", "No view found for the close button on help dialog");
            return null;
        }
        closeHelpDialog.setOnClickListener(view -> dialog.dismiss());

        TextView dialogDescription = rootView.findViewById(R.id.description_help_dialog);
        if (dialogDescription == null) {
            Log.e("AndroidJavaTools", "No view found for the description text on help dialog");
            return null;
        }
        dialogDescription.setText(mTextToDisplay);

        // Set the window background transparent, so the custom background is visible
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        return dialog;
    }
}