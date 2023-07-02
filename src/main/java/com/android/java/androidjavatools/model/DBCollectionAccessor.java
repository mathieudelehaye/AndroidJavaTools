//
//  DBCollectionAccessor.java
//
//  Created by Mathieu Delehaye on 22/01/2023.
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

package com.android.java.androidjavatools.model;

import android.util.Log;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DBCollectionAccessor {
    public enum SearchFilterType {
        RANGE,
        VALUE
    }

    private class SearchFilter {
        private SearchFilterType mType;
        private String[] mFields;
        private double[] mMinRanges;
        private double[] mMaxRanges;
        private String[] mValues;

        public SearchFilter(String[] fields, double[] minRanges, double[] maxRanges, String[] values,
            SearchFilterType type) {

            mFields = fields;
            mMinRanges = minRanges;
            mMaxRanges = maxRanges;
            mValues = values;
            mType = type;
        }

        public int getRanges() {
            return mMaxRanges.length;
        }

        public int getValues() {
            return mValues.length;
        }

        public String getFieldAtIndex(int i) {
            return mFields[i];
        }

        public double getMinRangeAtIndex(int i) {
            return mMinRanges[i];
        }

        public double getMaxRangeAtIndex(int i) {
            return mMaxRanges[i];
        }

        public String getValue(int i) {
            return mValues[i];
        }

        public SearchFilterType getType() {
            return mType;
        }
    }

    protected FirebaseFirestore mDatabase;
    protected String mCollectionName = "";
    protected String mKey;
    protected SearchFilter mFilter;

    // TODO: use a polymorphic type for `mData` and `mDataChanged` in order to avoid the map list.
    protected ArrayList<Map<String, String>> mData;
    protected ArrayList<Map<String, Boolean>> mDataChanged;

    public ArrayList<Map<String, String>> getData() {
        return mData;
    }

    public DBCollectionAccessor(FirebaseFirestore database) {
        mDatabase = database;
    }

    public DBCollectionAccessor(FirebaseFirestore database, String collection) {
        mDatabase = database;
        mCollectionName = collection;
    }

    public String getKey() {
        return mKey;
    }

    public void setKey(String value) {
        mKey = value;
    }

    public void setRangeBasedFilter(String[] fields, double[] minRanges, double[] maxRanges) {
        mFilter = new SearchFilter(fields, minRanges, maxRanges, new String[]{}, SearchFilterType.RANGE);
    }

    public void setValueBasedFilter(String[] fields, String[] values) {
        mFilter = new SearchFilter(fields, new double[]{}, new double[]{}, values, SearchFilterType.VALUE);
    }

    public boolean readDBFieldsForCurrentKey(String[] fields, TaskCompletionManager... cbManager) {

        if (mKey == null || mKey.equals("")) {
            Log.w("AJT", "Try to read with no valid key the fields from the DB collection: "
                + mCollectionName);
            return false;
        }

        mDatabase.collection(mCollectionName)
            .whereEqualTo("__name__", mKey)
            .get()
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {

                    readResultFields(task.getResult(), fields, null);

                    if (cbManager.length >= 1) {
                        cbManager[0].onSuccess();
                    }
                } else {
                    Log.e("AJT", "Error reading documents: ", task.getException());

                    if (cbManager.length >= 1) {
                        cbManager[0].onFailure();
                    }
                }
            });

        return true;
    }

    public boolean readDBFieldsForCurrentFilter(String[] fields, TaskCompletionManager... cbManager) {
        return (mFilter.mType == SearchFilterType.RANGE) ?
            readDBFieldsForCurrentRangeFilter(fields, cbManager) :
            readDBFieldsForCurrentValueFilter(fields, cbManager);
    }

    private boolean readDBFieldsForCurrentRangeFilter(String[] fields, TaskCompletionManager... cbManager) {
        if (mFilter == null && mFilter.getRanges() < 1) {
            Log.w("AJT", "Try to read with no valid range filter the fields from the DB collection: "
                + mCollectionName);
            return false;
        }

        String firstFilterField = mFilter.getFieldAtIndex(0);

        mDatabase.collection(mCollectionName)
            .whereLessThan(firstFilterField, mFilter.getMaxRangeAtIndex(0))
            .whereGreaterThan(firstFilterField, mFilter.getMinRangeAtIndex(0))
            .get()
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {

                    readResultFields(task.getResult(), fields, mFilter);

                    if (cbManager.length >= 1) {
                        cbManager[0].onSuccess();
                    }
                } else {
                    Log.e("AJT", "Error reading documents: ", task.getException());

                    if (cbManager.length >= 1) {
                        cbManager[0].onFailure();
                    }
                }
            });

        return true;
    }

    private boolean readDBFieldsForCurrentValueFilter(String[] fields, TaskCompletionManager... cbManager) {
        if (mFilter == null) {
            Log.w("AJT", "Try to read with no valid value filter the fields from the DB collection: "
                + mCollectionName);
            return false;
        }

        String firstFilterField = mFilter.getFieldAtIndex(0);

        mDatabase.collection(mCollectionName)
            .whereEqualTo(firstFilterField, Boolean.valueOf(mFilter.getValue(0)))
            .get()
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {

                    readResultFields(task.getResult(), fields, mFilter);

                    if (cbManager.length >= 1) {
                        cbManager[0].onSuccess();
                    }
                } else {
                    Log.e("AJT", "Error reading documents: ", task.getException());

                    if (cbManager.length >= 1) {
                        cbManager[0].onFailure();
                    }
                }
            });

        return false;
    }

    private void readResultFields(QuerySnapshot result, String[] fields, SearchFilter filter) {

        // mData and mDataChanged come with a default item. So, it needs to be cleared before
        // iterating on the results.
        mData.clear();
        mDataChanged.clear();

        for (QueryDocumentSnapshot document : result) {

            // Possibly filter out the document
            if (filter != null && !filterDocument(document, filter)) {
                continue;
            }

            //Log.v("AJT", document.getId() + " => " + document.getData());

            var dataItem = new HashMap<String, String>();
            var dataChangeItem = new HashMap<String, Boolean>();
            mData.add(dataItem);
            mDataChanged.add(dataChangeItem);

            //dataItem.put("documentId", document.getId());   // uncomment to debug
            //dataChangeItem.put("documentId", false);    // uncomment to debug

            // TODO: move the coordinate out of this class, in order to keep it reusable
            // Get the Coordinates map
            var coordinatesMap = (HashMap<String, Double>) document.getData().get("Coordinates");

            for (String field :fields) {
                if (coordinatesMap != null && field == "Latitude") {
                    dataItem.put("Latitude", String.valueOf(coordinatesMap.get("latitude")));
                    dataChangeItem.put("Latitude", false);
                    continue;
                }

                if (coordinatesMap != null && field == "Longitude") {
                    dataItem.put("Longitude", String.valueOf(coordinatesMap.get("longitude")));
                    dataChangeItem.put("Longitude", false);
                    continue;
                }

                final Object fieldObject = document.getData().get(field);
                dataItem.put(field, (fieldObject != null) ? fieldObject.toString() : "");
                dataChangeItem.put(field, false);
            }
            dataItem.put("key", document.getId());
            dataChangeItem.put("key", false);
        }
    }

    private boolean filterDocument(QueryDocumentSnapshot document, SearchFilter filter) {
        // The filter item at index 0 has been used for the query
        switch(filter.getType()) {
            case VALUE:
                for(int i = 1; i < filter.getRanges(); i++) {
                    final var fieldValue = (double)document.getData().get(filter.getFieldAtIndex(i));

                    if (fieldValue < filter.getMinRangeAtIndex(i) || fieldValue > filter.getMaxRangeAtIndex(i)) {
                        return false;
                    }
                }
                break;
            case RANGE:
            default:
                for(int i = 1; i < filter.getValues(); i++) {
                    final var fieldValue = String.valueOf(document.getData().get(filter.getFieldAtIndex(i)));

                    if (!fieldValue.equals(filter.getValue(i))) {
                        return false;
                    }
                }
                break;
        }

        return true;
    }
}
