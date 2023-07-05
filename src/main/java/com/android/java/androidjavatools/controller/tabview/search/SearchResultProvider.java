//
//  SearchResultProvider.java
//
//  Created by Mathieu Delehaye on 4/07/2023.
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

package com.android.java.androidjavatools.controller.tabview.search;

import android.util.Log;
import com.android.java.androidjavatools.controller.template.SearchProvider;
import com.android.java.androidjavatools.model.GeoPosition;
import com.android.java.androidjavatools.model.result.ResultInfo;
import com.android.java.androidjavatools.model.result.ResultItemInfo;
import com.android.java.androidjavatools.model.SetWithImages;
import com.android.java.androidjavatools.model.TaskCompletionManager;
import com.google.firebase.firestore.FirebaseFirestore;
import org.osmdroid.util.GeoPoint;

abstract public class SearchResultProvider implements SearchProvider {
    private SetWithImages mSearchResults;

    @Override
    public SetWithImages getSearchResults() {
        return mSearchResults;
    }

    @Override
    public void searchGeoPointResults(GeoPosition searchStart, double searchRadiusInCoordinate,
                                      FirebaseFirestore database, TaskCompletionManager... cbManager) {

        final double startLatitude = searchStart.getLocation().getLatitude();
        final double startLongitude = searchStart.getLocation().getLongitude();
        final String startLatitudeText = startLatitude+"";
        final String startLongitudeText = startLongitude+"";
        Log.d("AJT", "Display the recycling points around the location (" + startLatitudeText
            + ", " + startLongitudeText + ")");

        // Search for the recycling points (RP)
        final double truncatedLatitude = Math.floor(startLatitude * 100) / 100;
        final double truncatedLongitude = Math.floor(startLongitude * 100) / 100;
        final double maxSearchLatitude = truncatedLatitude + searchRadiusInCoordinate;
        final double minSearchLatitude = truncatedLatitude - searchRadiusInCoordinate;
        final double maxSearchLongitude = truncatedLongitude + searchRadiusInCoordinate;
        final double minSearchLongitude = truncatedLongitude - searchRadiusInCoordinate;

        String[] outputFields = { "Latitude", "Longitude", "PointName", "BuildingName", "BuildingNumber",
            "Address", "Postcode", "City", "3Words", "RecyclingProgram", "ImageUrl" };
        String[] filterFields = { "Latitude", "Longitude" };
        double[] filterMinRanges = { minSearchLatitude, minSearchLongitude };
        double[] filterMaxRanges = { maxSearchLatitude, maxSearchLongitude };

        var pointInfo = createResultInfo(database);
        pointInfo.setRangeBasedFilter(filterFields, filterMinRanges, filterMaxRanges);

        mSearchResults = new SetWithImages();

        pointInfo.readAllDBFields(outputFields, new TaskCompletionManager() {
            @Override
            public void onSuccess() {
                updateSearchResult(pointInfo);

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

    @Override
    public void searchResultForKey(String key, FirebaseFirestore database, TaskCompletionManager... cbManager) {

        String[] outputFields = { "Latitude", "Longitude", "PointName", "BuildingName", "BuildingNumber",
            "Address", "Postcode", "City", "3Words", "RecyclingProgram", "ImageUrl" };

        var pointInfo = createResultInfo(database);
        pointInfo.setKey(key);

        mSearchResults = new SetWithImages();

        pointInfo.readDBFieldsForKey(outputFields, new TaskCompletionManager() {
            @Override
            public void onSuccess() {
                updateSearchResult(pointInfo);

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

    abstract protected ResultInfo createResultInfo(FirebaseFirestore database);

    private void updateSearchResult(ResultInfo info) {
        for (int i = 0; i < info.getData().size(); i++) {
            // Uncomment to write back to DB the coordinates from the RP address
            //writeBackRPAddressCoordinatesToDB(pointInfo.getData().get(i).get("documentId"),
            //    pointInfo.getAddressAtIndex(i));

            final String key = info.getKeyAtIndex(i);

            final double latitude = info.getLatitudeAtIndex(i);
            final double longitude = info.getLongitudeAtIndex(i);

            String itemTitle = info.getTitleAtIndex(i);
            String itemSnippet = info.getSnippetAtIndex(i);
            String itemImageUrl = info.getImageUrlAtIndex(i);

            mSearchResults.create(
                key,
                new ResultItemInfo(
                    key, itemTitle, itemSnippet, new GeoPoint(latitude,longitude), true),
                itemImageUrl
            );
        }
    }
}
