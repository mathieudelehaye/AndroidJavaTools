//
//  ResultListAdapter.java
//
//  Created by Mathieu Delehaye on 24/01/2023.
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

package com.android.java.androidjavatools.controller.tabview.result.list;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.java.androidjavatools.Helpers;
import com.android.java.androidjavatools.model.result.ResultItemInfo;
import com.android.java.androidjavatools.R;
import com.android.java.androidjavatools.model.SetWithImages;

public class ResultListAdapter extends BaseAdapter {
    private Context mContext;
    private SetWithImages mResultItems;

    static private String description1 =
        "Lorem ipsum dolor sit amet,\n" +
        " consectetur adipiscing elit.\n" +
        "Sed do eiusmod tempor\n" +
        "incididunt.";
    static private String description2 =
        "Quis autem vel eum iure \n" +
        "reprehenderit qui in ea \n" +
        "voluptate velit esse quam.\n";
    static private String description3 =
        "At vero eos et accusamus et \n" +
        "iusto odio dignissimos ducimus \n" +
        "qui blanditiis praesentium.";

    public ResultListAdapter(Context context, SetWithImages result) {
        mContext = context;
        mResultItems = result;
    }

    @Override
    public int getCount() {
        return mResultItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mResultItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = View.inflate(mContext, R.layout.result_list_item,null);
        ImageView imageView = view.findViewById(R.id.result_list_item_image);
        TextView textViewTitle = view.findViewById(R.id.result_list_item_text_title);
        TextView textViewDescription = view.findViewById(R.id.result_list_item_text_description);

        var itemInfo=(ResultItemInfo) getItem(position);
        final boolean showImage = itemInfo.isContentAllowed();

        // Image
        final byte[] imageByte = Helpers.toPrimitives(itemInfo.getImage());
        if (imageByte != null && showImage) {
            Bitmap bmp = BitmapFactory.decodeByteArray(imageByte, 0, imageByte.length);
            imageView.setImageBitmap(bmp);
        } else {
            // Use a placeholder if the image has not been set
            imageView.setImageResource(R.drawable.camera);
        }
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        // Title
        textViewTitle.setText(showImage ? (itemInfo.getTitle()) : "Lorem ipsum dolor sit");
        final var currentTypeface = textViewTitle.getTypeface();
        textViewTitle.setTypeface(currentTypeface, Typeface.BOLD);
        textViewTitle.setTextColor(Color.BLACK);
        textViewTitle.setTextSize(16);

        // Description
        final int randomValue = (int) (Math.random() * 3);
        final String descriptionText = (randomValue == 0) ?
            description1 : (randomValue == 1) ?
            description2 : (randomValue == 2) ?
            description3 : "";
        textViewDescription.setText(descriptionText);

        return view;
    }
}
