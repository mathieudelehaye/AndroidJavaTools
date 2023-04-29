//
//  FragmentResultDetail.java
//
//  Created by Mathieu Delehaye on 14/02/2023.
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

package com.android.java.androidjavatools.controller.tabview.result;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.android.java.androidjavatools.Helpers;
import com.android.java.androidjavatools.controller.tabview.Navigator;
import com.android.java.androidjavatools.databinding.FragmentResultDetailBinding;
import com.android.java.androidjavatools.model.ResultItemInfo;
import com.android.java.androidjavatools.R;

public abstract class FragmentResultDetail extends Fragment {
    private FragmentResultDetailBinding mBinding;
    private Context mContext;
    private FragmentResult.ResultProvider mResultProvider;
    private ResultItemInfo mSelectedItem;
    private String mSelectedItemKey;
    private boolean mIsSaved;
    private Button mSaveButton;

    @Override
    public View onCreateView(
        LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState
    ) {
        mBinding = FragmentResultDetailBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @SuppressLint({"ResourceAsColor", "UseCompatTextViewDrawableApis"})
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        Log.v("AndroidJavaTools", "Result detail view created at timestamp: "
            + Helpers.getTimestamp());

        super.onViewCreated(view, savedInstanceState);

        mContext = getContext();
        mResultProvider = (FragmentResult.ResultProvider) getActivity();

        mSaveButton = mBinding.saveResultDetail;
        mSaveButton.setOnClickListener(v -> {
            if (mSelectedItem == null || mSelectedItemKey == null) {
                return;
            }

            boolean toSave = !mResultProvider.isSavedResult(mSelectedItemKey);

            if (toSave) {
                mResultProvider.createSavedResult(mSelectedItem);
            } else {
                mResultProvider.deleteSavedResult(mSelectedItemKey);
            }

            updateSavedButton();

            final int toastText = toSave ? R.string.save_text : R.string.unsave_text;
            Toast.makeText(mContext, toastText, Toast.LENGTH_SHORT).show();
        });

        final var navigatorManager = (Navigator.NavigatorManager)mContext;
        mBinding.backResultDetail.setOnClickListener(v -> {
            navigatorManager.navigator().back();
        });
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser) {
            Log.d("AndroidJavaTools", "Result detail view becomes visible");

            updateDetails();
            updateSavedButton();
        }
    }

    private void updateDetails() {
        // Description and image
        mSelectedItem = mResultProvider.getSelectedResultItem();
        mSelectedItemKey = mSelectedItem.getKey();

        final byte[] imageBytes = mSelectedItem.getImage();
        final boolean showImage = mSelectedItem.isImageShown();

        String title = showImage ? mSelectedItem.getTitle() : "Lorem ipsum dolor sit";
        String description = showImage ? mSelectedItem.getDescription() : "Lorem ipsum dolor sit amet. Ut enim "
            + "corporis ea labore esse ea illum consequatur. Et reiciendis ducimus et repellat magni id ducimus "
            + "nesc.";

        TextView resultDescription = getView().findViewById(R.id.description_result_detail);
        resultDescription.setText(title + "\n\n" + description);

        ImageView resultImage = getView().findViewById(R.id.image_result_detail);
        if (imageBytes != null && showImage) {
            Bitmap image = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            resultImage.setImageBitmap(image);
        } else {
            // Use a placeholder if the image has not been set
            resultImage.setImageResource(R.drawable.camera);
        }
        resultImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
    }

    @SuppressLint("UseCompatTextViewDrawableApis")
    private void updateSavedButton() {
        mIsSaved = mResultProvider.isSavedResult(mSelectedItemKey);

        final int icon = mIsSaved ? R.drawable.heart : R.drawable.heart_outline;
        final int color = mIsSaved ? R.color.black : R.color.ButtonGray;

        mSaveButton.setCompoundDrawablesWithIntrinsicBounds(icon, 0, 0, 0);
        mSaveButton.setCompoundDrawableTintList(ColorStateList.valueOf(ContextCompat.getColor(mContext, color)));
    }
}
