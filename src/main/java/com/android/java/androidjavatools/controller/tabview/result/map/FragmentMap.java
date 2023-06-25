//
//  FragmentmMap.java
//
//  Created by Mathieu Delehaye on 1/12/2022.
//
//  AndroidJavaTools: A framework to develop Android apps with Java Technologies.
//
//  Copyright © 2022 Mathieu Delehaye. All rights reserved.
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

package com.android.java.androidjavatools.controller.tabview.result.map;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.*;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import com.android.java.androidjavatools.controller.tabview.result.detail.ResultDetailAdapter;
import com.android.java.androidjavatools.controller.tabview.result.map.MapView;
import com.android.java.androidjavatools.controller.template.ResultProvider;
import com.android.java.androidjavatools.controller.template.SearchProvider;
import com.android.java.androidjavatools.model.AppUser;
import com.android.java.androidjavatools.model.TaskCompletionManager;
import com.android.java.androidjavatools.controller.tabview.result.FragmentResult;
import com.android.java.androidjavatools.databinding.FragmentMapBinding;
import com.android.java.androidjavatools.Helpers;
import com.android.java.androidjavatools.model.ResultItemInfo;
import com.android.java.androidjavatools.controller.template.FragmentHelpDialog;
import com.android.java.androidjavatools.R;
import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.*;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;
import java.util.ArrayList;

public class FragmentMap extends FragmentResult {
    private FragmentMapBinding mBinding;
    private org.osmdroid.views.MapView mMap = null;
    private IMapController mMapController;
    private ItemizedOverlayWithFocus<OverlayItem> mRPOverlay;
    private boolean mIsViewVisible = false;
    private boolean mKeyboardDisplayed = false;
    private final int mMapInitialHeight = 1413; // = 807 dp
    private final int mMapHeightDiff = 540; // = 309 dp
    private final int mMapReducedHeight = mMapInitialHeight - mMapHeightDiff;
    private final int mKilometerByCoordinateDeg = 111;  // # km by latitude degree

    public FragmentMap(SearchProvider provider) {
        super(provider);
    }

    @Override
    public View onCreateView(
        LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState
    ) {
        mBinding = FragmentMapBinding.inflate(inflater, container, false);

        var contentView = new MapView(getActivity(), this, mBinding);
        contentView.show();

        // Disable StrictMode policy in onCreate, in order to make a network call in the main thread
        // TODO: call the network from a child thread instead
        var policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        return mBinding.getRoot();
    }

    @SuppressLint("ResourceAsColor")
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        Log.v("AJT", "Map view created at timestamp: "
            + Helpers.getTimestamp());

        super.onViewCreated(view, savedInstanceState);

        setupMap(view);
        changeSearchSwitch(ResultPageType.LIST);

        view.getViewTreeObserver().addOnGlobalLayoutListener(() -> {

            var viewBorder = new Rect();

            // border will be populated with the overall visible display size in which the window this view is
            // attached to has been positioned in.
            view.getWindowVisibleDisplayFrame(viewBorder);

            final int viewBorderHeight = viewBorder.height();

            // height of the fragment root view
            View mapRootView = view.getRootView();
            View mapLayout = view.findViewById(R.id.map_layout);

            if (mapRootView == null || mapLayout == null) {
                return;
            }

            final int viewPagerRootViewHeight = mapRootView.getHeight();

            final int heightDiff = viewPagerRootViewHeight - viewBorderHeight;

            if (heightDiff > 0.25*viewPagerRootViewHeight) {
                // if more than 25% of the screen, it's probably a keyboard
                if (!mKeyboardDisplayed) {
                    mKeyboardDisplayed = true;
                    Log.v("AJT", "Keyboard displayed");

                    ViewGroup.LayoutParams mapLayoutParams = mapLayout.getLayoutParams();
                    mapLayoutParams.height = mMapReducedHeight;
                    mapLayout.requestLayout();
                }
            } else {
                if (mKeyboardDisplayed) {
                    mKeyboardDisplayed = false;
                    Log.v("AJT", "Keyboard hidden");

                    ViewGroup.LayoutParams params = mapLayout.getLayoutParams();
                    params.height = mMapInitialHeight;
                    mapLayout.requestLayout();
                }
            }
        });

        mBinding.mapUserLocation.setOnClickListener(view1 -> {

            final var userPosition= AppUser.getInstance().getGeoPosition();

            if(AppUser.getInstance().getGeoPosition() != null) {
                Log.d("AJT", "Change map focus to user location");

                final var location = userPosition.getLocation();
                final var geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
                mMapController.animateTo(geoPoint);
                setZoomLevel(14);
            }
        });

        toggleDetailsView(false);

        showHelp();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinding = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        mMap.onResume(); //needed for compass, my location overlays, v6.0.0 and up
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser) {
            mIsViewVisible = true;

            Log.d("AJT", "Map view becomes visible");
            Log.v("AJT", "Map view becomes visible at timestamp: "
                + Helpers.getTimestamp());

            changeSearchSwitch(ResultPageType.LIST);

            showHelp();
        } else {
            mIsViewVisible = false;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().save(this, prefs);
        mMap.onPause();  //needed for compass, my location overlays, v6.0.0 and up
    }

    public void toggleDetailsView(boolean visible) {

        View rootView = getView();

        if (rootView == null) {
            Log.w("AJT", "Cannot toggle the details view, as no root view available");
        }

        View detailLayout = rootView.findViewById(R.id.map_detail_layout);
        detailLayout.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    public void updateMapOverlay() {
        // possibly remove the former RP overlay
        if (mRPOverlay != null) {
            mMap.getOverlays().remove(mRPOverlay);
        }

        final var resultList = new ArrayList<OverlayItem>();
        for (int i = 0; i < mFoundResult.size(); i++) {
            resultList.add(new OverlayItem(
                mFoundResult.get(i).getTitle(),
                mFoundResult.get(i).getDescription(),
                mFoundResult.get(i).getLocation()
            ));
        }

        final var result = mResultProvider.getSearchResult();

        // display the overlay
        mRPOverlay = new ItemizedOverlayWithFocus<>(resultList,
            new ItemizedIconOverlay.OnItemGestureListener<>() {
                @Override
                public boolean onItemSingleTapUp(final int index, final OverlayItem item) {
                    Log.i("AJT", "Single tap");
                    mMapController.animateTo(item.getPoint());

                    showDetails(result.get(index));

                    return true;
                }

                @Override
                public boolean onItemLongPress(int index, OverlayItem item) {
                    return false;
                }
            }, mContext);

        mMap.getOverlays().add(mRPOverlay);

        // Refresh the map
        mMap.invalidate();

        setZoomInKilometer(mSearchRadiusInCoordinate * mKilometerByCoordinateDeg);

        final var location = mSearchStart.getLocation();
        mMapController.animateTo(new GeoPoint(location.getLatitude(), location.getLongitude()));

        final var resultProvider = (ResultProvider)getActivity();
        resultProvider.setSearchResult(result);
    }

    @Override
    protected void searchAndDisplayItems() {

        searchForResults(new TaskCompletionManager() {
            @Override
            public void onSuccess() {
                // Prepare the search results and download the images
                // TODO: make the following code reusable and share it with `FragmentResultList`

                mFoundResult.downloadImages(new TaskCompletionManager() {
                    @Override
                    public void onSuccess() {
                    }

                    @Override
                    public void onFailure() {
                    }
                });

                updateMapOverlay();
            }

            @Override
            public void onFailure() {
            }
        });
    }

    private int computeZoomLevelForRadius(double valueInMeter) {
        return (16 - (int)(Math.log(valueInMeter / 500) / Math.log(2)));
    }

    private void setZoomInKilometer(double radiusInKilometer) {
        final int zoomLevel = computeZoomLevelForRadius(radiusInKilometer * 1000);
        Log.v("AJT", "Map zoom set to level " + String.valueOf(zoomLevel)
            + " for radius of " + String.valueOf(radiusInKilometer) + " km");
        mMapController.setZoom(zoomLevel);
    }

    private void setZoomLevel(int level) {
        Log.v("AJT", "Map zoom set to level " + String.valueOf(level));
        mMapController.setZoom(level);
    }

    private void setupMap(View view) {

        // inflate and create the map
        mMap = view.findViewById(R.id.map);
        mMap.setTileSource(TileSourceFactory.MAPNIK);

        mMap.setBuiltInZoomControls(true);
        mMap.setMultiTouchControls(true);

        mMapController = mMap.getController();
        setZoomInKilometer(mSearchRadiusInCoordinate * mKilometerByCoordinateDeg);

        mLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(mContext), mMap);
        mLocationOverlay.enableMyLocation();

        mMap.getOverlays().add(mLocationOverlay);
    }

    private void showHelp() {

        if (mIsViewVisible && mSharedPref != null) {
            if (!Boolean.parseBoolean(mSharedPref.getString("map_help_displayed", "false"))) {
                mSharedPref.edit().putString("map_help_displayed", "true").commit();
                var dialogFragment = new FragmentHelpDialog(getString(R.string.map_help), () -> null);
                dialogFragment.show(getChildFragmentManager(), "Map help dialog");
            }
        }
    }

    private void showDetails(ResultItemInfo itemInfo) {

        final byte[] itemImageBytes = itemInfo.getImage();
        final boolean showImage = itemInfo.isContentAllowed();

        String itemTitle = showImage ? itemInfo.getTitle() : "Lorem ipsum dolor sit";
        String itemDescription = showImage ? itemInfo.getDescription() : "Lorem ipsum dolor sit amet. Ut enim "
            + "corporis ea labore esse ea illum consequatur. Et reiciendis ducimus et repellat magni id ducimus "
            + "nesc.";

        ImageView resultImage = getView().findViewById(R.id.map_detail_image);
        if (itemImageBytes != null && showImage) {
            Bitmap image = BitmapFactory.decodeByteArray(itemImageBytes, 0, itemImageBytes.length);
            resultImage.setImageBitmap(image);
        } else {
            // Use a placeholder if the image has not been set
            resultImage.setImageResource(R.drawable.camera);
        }
        resultImage.setScaleType(ImageView.ScaleType.CENTER_CROP);

        TextView resultDescription = getView().findViewById(R.id.map_detail_description);
        resultDescription.setText(itemTitle + "\n\n" + itemDescription);

        mBinding.mapDetailLayout.setOnClickListener(view1 -> {
            final var  adapter = new ResultDetailAdapter(mContext, itemInfo);
            showResultItem(adapter);
        });

        toggleDetailsView(true);
    }
}