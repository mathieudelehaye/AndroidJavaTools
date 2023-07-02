//
//  FragmentResult.java
//
//  Created by Mathieu Delehaye on 25/03/2023.
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
import com.android.java.androidjavatools.controller.template.FragmentHelpDialog;
import com.android.java.androidjavatools.controller.template.FragmentWithSearch;
import com.android.java.androidjavatools.controller.template.SearchProvider;
import com.android.java.androidjavatools.model.user.AppUser;
import com.android.java.androidjavatools.model.GeoPosition;
import com.android.java.androidjavatools.model.SetWithImages;
import com.android.java.androidjavatools.model.TaskCompletionManager;
import com.android.java.androidjavatools.R;
import org.jetbrains.annotations.NotNull;
import org.osmdroid.config.Configuration;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

public abstract class FragmentResult extends FragmentWithSearch {
    public enum ResultPageType {
        LIST,
        MAP
    }

    protected GeoPosition mSearchStart;
    protected SetWithImages mFoundResult = new SetWithImages();
    protected MyLocationNewOverlay mLocationOverlay;
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

    public GeoPosition getSearchStart() {
        return mSearchStart;
    }

    public SetWithImages getFoundResult() {
        return mFoundResult;
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
        locationProvider.startLocationProvider((location, source) -> {

            // TODO: improve the way we detect the first gps position fix
            if(!firstLocationReceived[0]) {
                firstLocationReceived[0] = true;

                Log.d("AJT", "First received location for the user: " + location.toString());
                AppUser.getInstance().setGeoPosition(new GeoPosition(location));
                Log.v("AJT", "First received location at timestamp: "
                    + Helpers.getTimestamp());

                writeCachedUserLocation();
            }
        });
    }

    public void updateSearchResults() {
        final String searchQuery = (sResultQuery.equals("Around current location") ? "usr" : sResultQuery);

        final boolean userLocationReadFromCache = readCachedUserLocation();

        if (!searchQuery.equals("") && !searchQuery.equals("usr")) {
            // If a query has been received by the searchable activity, use it
            // to find the search start
            Log.v("AJT", "Searching for the query: " + searchQuery);
            setSearchStart(getCoordinatesFromAddress(searchQuery));
        } else if (userLocationReadFromCache) {
            // Otherwise, if the user location was cached, search around it
            Log.v("AJT", "Searching around the user location from the cache");
            final Location location = AppUser.getInstance().getGeoPosition().getLocation();
            setSearchStart(location);
        } else {
            String dialogText = "Please wait until the app has found your position";
            var dialogFragment = new FragmentHelpDialog(dialogText, () -> null);
            dialogFragment.show(getChildFragmentManager(), "Searching position dialog");
            return;
        }

        // Search and display the items in the child fragment
        searchAndDisplayItems();
    }

    public void tryAndCopySearch(@NotNull GeoPosition newStart, SetWithImages newResult) {
        // Check if the search start are different. If not, do not copy the searches
        if (mSearchStart != null) {
            final Location originalLocation = mSearchStart.getLocation();
            final Location newLocation = newStart.getLocation();
            final int multiplier = 100000;

            if(Math.floor(originalLocation.getLatitude() * multiplier) ==
                Math.floor(newLocation.getLatitude() * multiplier) &&
                (Math.floor(originalLocation.getLongitude() * multiplier) ==
                Math.floor(newLocation.getLongitude() * multiplier))) {

                return;
            }
        }

        mSearchStart = newStart;
        mFoundResult = newResult;
    }

    protected Location getCoordinatesFromAddress(@NotNull String locationName) {

        if (mGeocoder == null || locationName.equals("")) {
            Log.w("AJT", "Cannot get coordinates, as no geocoder or empty address");
            return null;
        }

        try {
            List<Address> geoResults = mGeocoder.getFromLocationName(locationName, 1);

            if (!geoResults.isEmpty()) {
                final Address addr = geoResults.get(0);
                final var location = new Location((String) null);
                location.setLatitude(addr.getLatitude());
                location.setLongitude(addr.getLongitude());
                return location;
            } else {
                Log.w("AJT", "No coordinate found for the address: " + locationName);
                Toast toast = Toast.makeText(requireContext(),"Location Not Found",Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                toast.show();
            }
        } catch (IOException e) {
            Log.e("AJT", "Error getting a location: " + e);
        }

        return null;
    }

    protected boolean readCachedUserLocation() {
        final double cacheLatitude = Location.convert(mSharedPref.getString("user_latitude", ""));
        final double cacheLongitude = Location.convert(mSharedPref.getString("user_longitude", ""));

        if (cacheLatitude != 0f && cacheLongitude != 0) {
            Log.d("AJT", "User location read from cache: " + cacheLatitude + " " + cacheLongitude);
            Log.v("AJT", "User location read from cache at timestamp: " + Helpers.getTimestamp());

            final var location = new Location((String) null);
            location.setLatitude(cacheLatitude);
            location.setLongitude(cacheLongitude);
            AppUser.getInstance().getGeoPosition().setLocation(location);

            return true;
        }

        return false;
    }

    protected void writeCachedUserLocation() {
        // Store the user location for the next startup
        final var location = AppUser.getInstance().getGeoPosition().getLocation();

        final String cacheLatitude = Location.convert(location.getLatitude(), Location.FORMAT_DEGREES);
        final String cacheLongitude = Location.convert(location.getLongitude(), Location.FORMAT_DEGREES);

        // Do not use an R.string resource here to store
        // "user_location". As sometimes
        // the context won't be available yet, when creating again the child view of
        // the FragmentApp object (e.g.: after tapping the search view switch button).
        // In such a case, `getString` will throw an exception.
        mSharedPref.edit().putString("user_latitude", cacheLatitude).commit();
        mSharedPref.edit().putString("user_longitude", cacheLongitude).commit();

        Log.d("AJT", "User location cached: " + cacheLatitude + " " + cacheLongitude);
        Log.v("AJT", "User location cached at timestamp: " + Helpers.getTimestamp());
    }

    protected void searchForResults(TaskCompletionManager... cbManager) {
        if (mSearchStart == null) {
            Log.w("AJT", "Cannot search for the results because no search start");
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
            Log.w("AJT", "No container view found when changing the search switch");
            return;
        }

        Button viewSwitch = containerView.findViewById(R.id.search_view_switch_button);
        if (viewSwitch == null) {
            Log.w("AJT", "No view found when changing the search switch");
            return;
        }

        Log.v("AJT", "Changing the search switch to the page: " + destination.toString());

        final int icon = (destination == ResultPageType.LIST) ? R.drawable.bullet_list : R.drawable.map;

        viewSwitch.setOnClickListener(view -> {
            Log.d("AJT", "Switch button pressed, navigate to: " + destination);

            final String dest = (destination == ResultPageType.LIST) ?
                "list" :
                "map";

            mNavigatorManager.navigator().showFragment(dest);
        });

        viewSwitch.setCompoundDrawablesWithIntrinsicBounds(icon, 0, 0, 0);
    }

    protected boolean mustShowBrand() {
        if (mContext == null) {
            Log.w("AJT", "Cannot check if brand must be shown, as no context");
            return false;
        }

        return !mContext.getResources().getConfiguration().getLocales().get(0).getDisplayName().contains("Belgique");
    }

    private void setSearchStart(Location value) {
        Log.v("AJT", "Search start set to: " + value);
        mSearchStart = new GeoPosition(value);
    }
}
