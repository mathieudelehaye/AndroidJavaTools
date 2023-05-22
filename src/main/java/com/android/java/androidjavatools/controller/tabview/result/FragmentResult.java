//
//  FragmentResult.java
//
//  Created by Mathieu Delehaye on 25/03/2023.
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

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import androidx.annotation.NonNull;
import com.android.java.androidjavatools.Helpers;
import com.android.java.androidjavatools.controller.tabview.dialog.FragmentHelpDialog;
import com.android.java.androidjavatools.controller.template.FragmentWithSearch;
import com.android.java.androidjavatools.controller.template.SearchProvider;
import com.android.java.androidjavatools.model.SearchResult;
import com.android.java.androidjavatools.model.TaskCompletionManager;
import com.android.java.androidjavatools.R;
import org.jetbrains.annotations.NotNull;
import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.IMyLocationConsumer;
import org.osmdroid.views.overlay.mylocation.IMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

public abstract class FragmentResult extends FragmentWithSearch {
    public enum ResultPageType {
        LIST,
        MAP
    }

    protected GeoPoint mUserLocation;
    protected GeoPoint mSearchStart;
    protected MyLocationNewOverlay mLocationOverlay;
    protected SearchResult mFoundResult = new SearchResult();
    protected final double mSearchRadiusInCoordinate = 0.045;
    protected abstract void searchAndDisplayItems();
    private SearchProvider mSearchProvider;
    private static String sResultQuery = "";
    private Geocoder mGeocoder;

    public static String getResultQuery() {
        return sResultQuery;
    }

    public static void setResultQuery(String query) {
        sResultQuery = query;
    }

    public FragmentResult(SearchProvider provider) {
        mSearchProvider = provider;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mGeocoder = new Geocoder(requireContext(), Locale.getDefault());

        //load/initialize the osmdroid configuration, this can be done
        Configuration.getInstance().load(mContext, PreferenceManager.getDefaultSharedPreferences(mContext));
        //setting this before the layout is inflated is a good idea
        //it 'should' ensure that the map has a writable location for the map cache, even without permissions
        //if no tiles are displayed, you can try overriding the cache path using Configuration.getInstance()
        //.setCachePath
        //see also StorageUtils
        //note, the load method also sets the HTTP User Agent to your application's package name, abusing osm's
        //tile servers will get you banned based on this string

        // Get the current user geolocation
        final boolean[] firstLocationReceived = {false};
        var locationProvider = new GpsMyLocationProvider(mContext);
        locationProvider.startLocationProvider(new IMyLocationConsumer() {
            @Override
            public void onLocationChanged(Location location, IMyLocationProvider source) {

                // TODO: improve the way we detect the first gps position fix
                if(!firstLocationReceived[0]) {
                    firstLocationReceived[0] = true;

                    Log.d("AndroidJavaTools", "First received location for the user: " + location.toString());
                    mUserLocation = new GeoPoint(location.getLatitude(), location.getLongitude());
                    Log.v("AndroidJavaTools", "First received location at timestamp: "
                        + Helpers.getTimestamp());

                    writeCachedUserLocation();

                    // Start a search if none happened so far
                    if (mSearchStart == null) {
                        setSearchStart(mUserLocation);
                        searchAndDisplayItems();
                    }
                }
            }
        });

        updateSearchResults();
    }

    protected void updateSearchResults() {
        // If there is no search start yet, find it and get the items to display
        if (mSearchStart == null && getContext() != null) {
            final String searchQuery = sResultQuery;

            boolean userLocationReadFromCache = readCachedUserLocation();

            if (!searchQuery.equals("") && !searchQuery.equals("usr")) {
                // If a query has been received by the searchable activity, use it
                // to find the search start
                Log.v("AndroidJavaTools", "Searching for the query: " + searchQuery);
                setSearchStart(getCoordinatesFromAddress(searchQuery));
            } else if (userLocationReadFromCache) {
                // Otherwise, if the user location was cached, search around it
                Log.v("AndroidJavaTools", "Searching around the user location from the cache");
                setSearchStart(mUserLocation);
            } else {
                String dialogText = "Please wait until the app has found your position"
                    + "No search until user position is found";
                var dialogFragment = new FragmentHelpDialog(dialogText);
                dialogFragment.show(getChildFragmentManager(), "Searching position dialog");
            }
        }

        if  (mSearchStart != null) {
            // Search and display the items in the child fragment
            searchAndDisplayItems();
        }
    }

    protected GeoPoint getCoordinatesFromAddress(@NotNull String locationName) {

        if (mGeocoder == null || locationName.equals("")) {
            Log.w("AndroidJavaTools", "Cannot get coordinates, as no geocoder or empty address");
            return null;
        }

        try {
            List<Address> geoResults = mGeocoder.getFromLocationName(locationName, 1);

            if (!geoResults.isEmpty()) {
                final Address addr = geoResults.get(0);
                var location = new GeoPoint(addr.getLatitude(), addr.getLongitude());
                return location;
            } else {
                Log.w("AndroidJavaTools", "No coordinate found for the address: " + locationName);
                Toast toast = Toast.makeText(requireContext(),"Location Not Found",Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                toast.show();
            }
        } catch (IOException e) {
            Log.e("AndroidJavaTools", "Error getting a location: " + e);
        }

        return null;
    }

    protected boolean readCachedUserLocation() {
        String cacheLocation = mSharedPref.getString("user_location", "");

        if (cacheLocation != "") {
            Log.d("AndroidJavaTools", "User location read from cache: " + cacheLocation);
            Log.v("AndroidJavaTools", "User location read from cache at timestamp: "
                + Helpers.getTimestamp());

            mUserLocation = GeoPoint.fromDoubleString(cacheLocation, ',');

            return true;
        }

        return false;
    }

    protected void writeCachedUserLocation() {
        // Store the user location for the next startup
        String cacheLocation = mUserLocation.toDoubleString();

        // Do not use an R.string resource here to store "user_location". As sometimes
        // the context won't be available yet, when creating again the child view of
        // the FragmentApp object (e.g.: after tapping the search view switch button).
        // In such a case, `getString` will throw an exception.
        mSharedPref.edit().putString("user_location", cacheLocation)
            .commit();

        Log.d("AndroidJavaTools", "User location cached: " + cacheLocation);
        Log.v("AndroidJavaTools", "User location cached at timestamp: "
            + Helpers.getTimestamp());
    }

    protected void setSearchStart(GeoPoint value) {
        Log.v("AndroidJavaTools", "Search start set to: " + value);
        mSearchStart = value;
    }

    protected void searchForResults(TaskCompletionManager... cbManager) {
        if (mSearchStart == null) {
            Log.w("AndroidJavaTools", "Cannot search for the results because no search start");
            return;
        }

        mSearchProvider.searchGeoPointResults(mSearchStart, mSearchRadiusInCoordinate, mDatabase,
            new TaskCompletionManager() {
                @Override
                public void onSuccess() {
                    mFoundResult = mSearchProvider.getSearchResults();

                    if (cbManager.length >= 1) {
                        cbManager[0].onSuccess();
                    }
                }

                @Override
                public void onFailure() {
                    if (cbManager.length >= 1) {
                        cbManager[0].onFailure();
                    }
                }
            });
    }

    protected void changeSearchSwitch(ResultPageType destination) {
        var containerView = getView();
        if (containerView == null) {
            Log.w("AndroidJavaTools", "No container view found when changing the search switch");
            return;
        }

        Button viewSwitch = containerView.findViewById(R.id.search_view_switch_button);
        if (viewSwitch == null) {
            Log.w("AndroidJavaTools", "No view found when changing the search switch");
            return;
        }

        Log.v("AndroidJavaTools", "Changing the search switch to the page: " + destination.toString());

        final int icon = (destination == ResultPageType.LIST) ? R.drawable.bullet_list : R.drawable.map;

        viewSwitch.setOnClickListener(view -> {
            Log.d("AndroidJavaTools", "Switch button pressed, navigate to: " + destination);

            final String dest = (destination == ResultPageType.LIST) ?
                "list" :
                "map";

            mNavigatorManager.navigator().showFragment(dest);
        });

        viewSwitch.setCompoundDrawablesWithIntrinsicBounds(icon, 0, 0, 0);
    }

    protected boolean mustShowBrand() {
        if (mContext == null) {
            Log.w("AndroidJavaTools", "Cannot check if brand must be shown, as no context");
            return false;
        }

        return !mContext.getResources().getConfiguration().getLocales().get(0).getDisplayName().contains("Belgique");
    }
}
