//
//  SavedListAdapter.java
//
//  Created by Mathieu Delehaye on 29/04/2023.
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

package com.android.java.androidjavatools.controller.tabview.saved;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.android.java.androidjavatools.R;
import com.android.java.androidjavatools.controller.template.ResultProvider;
import com.android.java.androidjavatools.model.ResultItemInfo;
import java.util.List;
import java.util.Map;

public class SavedListAdapter extends BaseAdapter {
    private Context mContext;
    protected ResultProvider mResultProvider;
    private Map<String, ResultItemInfo> mResults;
    private List<String> mResultKeys;

    public SavedListAdapter(Context ctxt, ResultProvider resultProvider) {
        mContext = ctxt;
        mResultProvider = resultProvider;
        mResults = resultProvider.getSavedResults();
        mResultKeys = resultProvider.getSavedResultKeys();
    }

    @Override
    public int getCount() {
        return mResults.size();
    }

    @Override
    public Object getItem(int position) {
        return mResults.get(mResultKeys.get(position));
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = View.inflate(mContext, R.layout.saved_list_item,null);

        TextView textView = view.findViewById(R.id.saved_list_item_text);
        ImageView imageView = view.findViewById(R.id.saved_list_item_image);
        ImageButton buttonView = view.findViewById(R.id.saved_list_item_delete);

        var itemInfo=(ResultItemInfo) getItem(position);

        final byte[] imageByte = itemInfo.getImage();
        final boolean showImage = itemInfo.isImageShown();

        textView.setText(showImage ? (itemInfo.getTitle()) : "Lorem ipsum dolor sit");

        if (imageByte != null && showImage) {
            Bitmap bmp = BitmapFactory.decodeByteArray(imageByte, 0, imageByte.length);
            imageView.setImageBitmap(bmp);
        } else {
            // Use a placeholder if the image has not been set
            imageView.setImageResource(R.drawable.camera);
        }
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);

        buttonView.setOnClickListener(v -> {
            Log.v("AndroidJavaTools", "mdl button clicked for saved item in position " + position);

            mResultProvider.deleteSavedResult(mResultKeys.get(position));
            notifyDataSetChanged();
        });

        return view;
    }
}
