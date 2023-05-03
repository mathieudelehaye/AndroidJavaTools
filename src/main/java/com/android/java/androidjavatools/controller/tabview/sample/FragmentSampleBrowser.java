//
//  FragmentSampleBrowser.java
//
//  Created by Mathieu Delehaye on 3/05/2023.
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

package com.android.java.androidjavatools.controller.tabview.sample;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.constraintlayout.helper.widget.Carousel;
import androidx.fragment.app.Fragment;
import com.android.java.androidjavatools.Helpers;
import com.android.java.androidjavatools.R;
import com.android.java.androidjavatools.databinding.FragmentSampleBrowserBinding;

public abstract class FragmentSampleBrowser extends Fragment {
    protected FragmentSampleBrowserBinding mBinding;

    @Override
    public View onCreateView(
        LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState
    ) {
        mBinding = FragmentSampleBrowserBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        Log.v("AndroidJavaTools", "Terms view created at timestamp: "
            + Helpers.getTimestamp());

        super.onViewCreated(view, savedInstanceState);

        final int [] images = {R.drawable.beauty01, R.drawable.beauty02, R.drawable.beauty03,
            R.drawable.beauty04, R.drawable.beauty05};

        var carousel1 = (Carousel)mBinding.carouselLayout1.findViewById(R.id.carousel);
        var carousel2 = (Carousel)mBinding.carouselLayout2.findViewById(R.id.carousel);
        var carousel3 = (Carousel)mBinding.carouselLayout3.findViewById(R.id.carousel);

        var carouselAdapter = new Carousel.Adapter() {
            @Override
            public int count() {
                return images.length;
            }

            @Override
            public void populate(View view, int index) {
                var imageView = (ImageView) view;
                imageView.setImageResource(images[index]);
                imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            }

            @Override
            public void onNewItem(int index) {
                // called when an item is set
            }
        };

        carousel1.setAdapter(carouselAdapter);
        carousel2.setAdapter(carouselAdapter);
        carousel3.setAdapter(carouselAdapter);
    }
}