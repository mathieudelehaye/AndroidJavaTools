//
//  SearchSuggestionProvider.java
//
//  Created by Mathieu Delehaye on 7/02/2023.
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

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.android.java.androidjavatools.model.result.suggestion.AutocompleteResult;
import com.android.java.androidjavatools.model.result.suggestion.AutocompleteResults;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class SearchSuggestionProvider extends ContentProvider {

    @Override
    public boolean onCreate() {
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        final String userInput = selectionArgs[0];
        Log.v("AJT", "Received the query: " + userInput);

        String[] suggestions = findSuggestions(userInput);

        String[] columns = {"_ID", SearchManager.SUGGEST_COLUMN_TEXT_1};
        var cursor = new MatrixCursor(columns);

        cursor.addRow(new Object[] {0, "Around current location"});
        //cursor.addRow(new Object[] {1, userInput});

        int suggestionIndex = 1;

        for (String suggestion : suggestions) {
            cursor.addRow(new Object[] {suggestionIndex, suggestion});
            suggestionIndex++;
        }

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s,
        @Nullable String[] strings) {

        return 0;
    }

    private URL getSuggestionURL(String query) {
        try {
            return new URL("https://api.foursquare.com/v3/autocomplete?query=" + query);
        } catch (MalformedURLException mue) {
            Log.e("AJT", "Cannot get the URL for suggestion from the query: " + query);
            return null;
        }
    }

    private String[] findSuggestions(String query) {
        String[] output = { "Partick", "G37EE" };

        final var client = new OkHttpClient();

        final URL suggestionURL = getSuggestionURL(query);
        if (suggestionURL == null) {
            return new String[]{};
        }

        Log.v("AJT", "Suggestion URL: " + suggestionURL + " formed from the query: " + query);

        // TODO: do not commit the API key to the repo
        final var request = new Request.Builder()
            .url(suggestionURL)
            .get()
            .addHeader("accept", "application/json")
            .addHeader("Authorization", "fsq35loOQWuCcNPy6qWmJI3PQvSqoyYw5NN0z6zqilnTtc4=")
            .build();

        try {
            // TODO: change the call to make it asynchronous
            final Response response = client.newCall(request).execute();

            if (response.isSuccessful()) {
                final ResponseBody responseBody = response.body();
                if (responseBody != null) {

                    final String jsonResponse = responseBody.string();

                    if (!jsonResponse.isEmpty()) {
                        Log.v("AJT", "JSON response received from GET: jsonResponse = "
                            + jsonResponse);

                        final var mapper = new ObjectMapper();

                        // The mapper doesn't fail on a JSON field not defined as class property
                        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

                        final var results = mapper.readValue(jsonResponse,
                            AutocompleteResults.class);

                        Log.d("AJT", "Parsed response: results = "
                            + results.getInfo());

                        for (AutocompleteResult result : results.getResults()) {
                            if (result.getType().equals("geo")) {
                                final String suggestion = result.getText().getPrimary();
                                Log.d("AJT", "Received suggestion: " + suggestion);
                            }
                        }
                    } else {
                        Log.d("AJT", "JSON Response is empty.");
                    }
                } else {
                    Log.d("AJT", "Response body is null.");
                }
            } else {
                Log.e("AJT", "Response was not successful: " + response.code() + " " + response.message());
            }
        } catch (IOException ioe) {
            Log.e("AJT", "Suggestion provider API call threw an exception: " + ioe.getMessage());
        }

        return output;
    }
}
