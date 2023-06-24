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

package com.android.java.androidjavatools.controller.tabview.result.detail;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.android.java.androidjavatools.Helpers;
import com.android.java.androidjavatools.controller.template.Navigator;
import com.android.java.androidjavatools.controller.template.ResultProvider;
import com.android.java.androidjavatools.databinding.FragmentResultDetailBinding;
import com.android.java.androidjavatools.model.ResultItemInfo;
import com.android.java.androidjavatools.R;

public abstract class FragmentResultDetail extends Fragment {
    class DetailDataSetObserver extends DataSetObserver {
        @Override
        public void onChanged() {
            updateContent();
        }

        @Override
        public void onInvalidated() {
            updateContent();
        }

        private void updateContent() {
            updateImage();
            mDetailImage.requestLayout();
        }
    }

    private FragmentResultDetailBinding mBinding;
    private Context mContext;
    private ResultProvider mResultProvider;
    private ResultItemInfo mSelectedItem;
    private String mSelectedItemKey;
    private boolean mIsSaved;
    private Button mSaveButton;
    private ResultDetailAdapter mAdapter;
    private DetailDataSetObserver mDataSetObserver;
    private ImageView mDetailImage;

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
        Log.v("AJT", "Result detail view created at timestamp: "
            + Helpers.getTimestamp());

        super.onViewCreated(view, savedInstanceState);

        mContext = getContext();
        mResultProvider = (ResultProvider) getActivity();

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
            Log.d("AJT", "Result detail view becomes visible");

            // Set the adapter passed by the Result list
            ResultDetailAdapter adapter = mResultProvider.getSelectedItemAdapter();
            if (adapter != null) {
                mAdapter = adapter;
            } else {
                Log.e("AJT", "Result detail view shown but no detail adapter available");
            }

            updateDetails();
            updateSavedButton();
        }
    }

    public Adapter getAdapter() {
        return mAdapter;
    }

    public void setAdapter(ResultDetailAdapter adapter) {
        mAdapter = adapter;

        if (mAdapter != null) {
            mDataSetObserver = new DetailDataSetObserver();
            mAdapter.registerDataSetObserver(mDataSetObserver);
        }
    }

    public void updateImage() {
        mSelectedItem = (ResultItemInfo) (mResultProvider.getSelectedItemAdapter().getItem(0));

        mSelectedItemKey = mSelectedItem.getKey();
        final boolean show = mSelectedItem.isContentAllowed();

        final ConstraintLayout imageLayout = mBinding.imageResultDetailLayout;
        mDetailImage = (ImageView)mAdapter.getView(0,null, null);

        // Add the image view to the fragment layout
        imageLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT));
        mDetailImage.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT));
        imageLayout.addView(mDetailImage);

        if (show && mSelectedItem.getImage() == null) {
            // Use a placeholder if the image cannot be shown or is not downloaded
            mDetailImage.setImageResource(R.drawable.camera);
        }
    }

    @SuppressLint("SetTextI18n")
    private void updateDetails() {
        mSelectedItem = (ResultItemInfo) (mResultProvider.getSelectedItemAdapter().getItem(0));

        mSelectedItemKey = mSelectedItem.getKey();
        final boolean show = mSelectedItem.isContentAllowed();

        String title = show ? mSelectedItem.getTitle() : "Lorem ipsum dolor sit";
        String description = show ? mSelectedItem.getDescription() : "Lorem ipsum dolor sit amet. Ut enim "
            + "corporis ea labore esse ea illum consequatur. Et reiciendis ducimus et repellat magni id ducimus "
            + "nesc.";

        TextView resultDescription = mBinding.descriptionResultDetail;
        resultDescription.setText(title + "\n\n" + description);

        updateImage();
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
