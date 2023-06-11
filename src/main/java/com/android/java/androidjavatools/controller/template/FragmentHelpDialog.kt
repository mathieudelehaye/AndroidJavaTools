//
//  FragmentHelpDialog.kt
//
//  Created by Mathieu Delehaye on 11/06/2023.
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

package com.android.java.androidjavatools.controller.template

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.viewinterop.AndroidViewBinding
import androidx.fragment.app.DialogFragment
import com.android.java.androidjavatools.databinding.FragmentHelpDialogBinding
import com.android.java.androidjavatools.R

class FragmentHelpDialog (text : String): DialogFragment() {
    private var mTextToDisplay: String? = text

    @SuppressLint("ResourceAsColor")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)

        // Use the Builder class for convenient dialog construction
        val builder = AlertDialog.Builder(requireActivity())

        val rootView = ComposeView(requireActivity()).apply {

            // Dispose of the Composition when the view's LifecycleOwner is destroyed
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)

            setContent {
                AndroidViewBinding(
                    factory = FragmentHelpDialogBinding::inflate,
                    modifier = Modifier
                ) {

                    closeHelpDialog.setOnClickListener {
                        dialog?.dismiss()
                    }

                    descriptionHelpDialog.text = mTextToDisplay
                }
            }
        }

        val dialog = builder.setView(rootView).create()

        // Set the window background transparent, so the custom background is visible
        dialog.window!!.setBackgroundDrawableResource(R.drawable.dialog_background)

        return dialog
    }
}