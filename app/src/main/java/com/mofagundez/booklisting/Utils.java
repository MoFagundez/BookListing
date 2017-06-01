package com.mofagundez.booklisting;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import static com.mofagundez.booklisting.MainActivity.LOG_TAG;

/**
 * Book List
 * Created by Mauricio on May 27, 2017
 * <p>
 * Udacity Android Basics Nanodegree
 * Project 7: Book Listing App
 * <p>
 * IMPORTANT NOTE: THIS CLASS IS BASED OUT OF THE EARTHQUAKE PROJECT (re-using some of the code we did in class)
 */
final class Utils {

    /**
     * List of constants to be used parsing {@link JSONObject and {@link JSONArray}}
     */
    private static final String JSON_ITEMS = "items";
    private static final String JSON_VOLUME_INFO = "volumeInfo";
    private static final String JSON_TITLE = "title";
    private static final String JSON_AUTHORS = "authors";
    private static final String JSON_PUBLISHED_DATE = "publishedDate";
    private static final String JSON_URL = "canonicalVolumeLink";
    private static final String JSON_IMAGE_LINKS = "imageLinks";
    private static final String JSON_SMALL_THUMBNAIL = "smallThumbnail";
    private static final String JSON_NULL_RESULT = "Unknown";

    /**
     * Create a private constructor because no one should ever create a {@link Utils} object.
     * This class is only meant to hold static methods for networking.
     * <p>
     * Instancing this class will throw and exception.
     */
    private Utils() {
        throw new AssertionError();
    }

    /**
     * Check if the Url passed is valid
     *
     * @param stringUrl: Url passed from {@link BookLoader} containing the search terms
     */
    static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error with creating URL ", e);
        }
        return url;
    }

    /**
     * Static method that makes the HTTP connection type GET to proceed with the search
     *
     * @param url: Url passed as a parameter from the {@link BookLoader} class,
     *             already validated by the createUrl method of this class
     */
    static String makeHttpRequest(URL url) throws IOException {
        Log.i(LOG_TAG, "makeHTTPRequest");
        String jsonResponse = "";
        if (url == null) {
            return jsonResponse;
        }
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;

        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.connect();
            Log.i("HTTP Response Code:", String.valueOf(urlConnection.getResponseCode()));

            // Check if response code is 200 to proceed parsing data
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, String.valueOf(urlConnection.getResponseCode()));
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Exception thrown:" + e.toString());
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Read the stream of bytes received and transforms into a String to be used later by JSON
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Read the stream collected with the HTTP request and extract the {@link Book} objects
     * to an {@link ArrayList} in order to populate the UI later
     */
    static List<Book> extractBookFromJson(String bookJSON) {
        // Check if JSON is null
        if (TextUtils.isEmpty(bookJSON)) {
            return null;
        }
        ArrayList<Book> books = new ArrayList<>();

        try {
            JSONObject baseJsonResponse = new JSONObject(bookJSON);
            JSONArray itemsArray = baseJsonResponse.getJSONArray(JSON_ITEMS);

            for (int i = 0; i < itemsArray.length(); i++) {
                JSONObject item = itemsArray.getJSONObject(i);
                JSONObject volumeInfo = item.getJSONObject(JSON_VOLUME_INFO);

                // Extract title, publishedYear and url from JSONObject and pass to a variable
                // using the method parseBookInformation
                String title = parseBookInformation(volumeInfo, JSON_TITLE);
                String publishedYear = parseBookInformation(volumeInfo, JSON_PUBLISHED_DATE);
                String url = parseBookInformation(volumeInfo, JSON_URL);

                // Extract authors from JSONObject and create a JSONArray
                String authors = JSON_NULL_RESULT;
                try {
                    JSONArray authorsArray = volumeInfo.getJSONArray(JSON_AUTHORS);
                    // Loop through the authorsArray and assign values to the authors String using the method parseAuthors
                    authors = parseAuthors(authorsArray);
                } catch (JSONException e) {
                    Log.e(LOG_TAG, "Problem parsing book information at Utils.parseBookInformation from Google API", e);
                }

                // Download the small thumbnail and pass to a Bitmap object
                Bitmap bitmap = null;
                try {
                    // Extract thumbnail URL from a new JSONObject and pass to a variable
                    JSONObject volumeImages = volumeInfo.getJSONObject(JSON_IMAGE_LINKS);
                    InputStream inputStream = new java.net.URL(volumeImages.getString(JSON_SMALL_THUMBNAIL)).openStream();
                    bitmap = BitmapFactory.decodeStream(inputStream);
                } catch (Exception e) {
                    Log.e("Error", e.getMessage());
                    e.printStackTrace();
                }

                // Create Book object with the variables initialised above
                Book book = new Book(title, authors, publishedYear, bitmap, url);
                // Add book to list of books
                books.add(book);
            }
            return books;

        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problem parsing books JSON results from Google API", e);

        }
        return null;
    }

    /**
     * Loop through an {@link JSONArray} passed as a parameter and
     * proper formats the String in order to correctly update the UI
     */
    private static String parseAuthors(JSONArray array) {
        // Create an ArrayList of strings
        ArrayList<String> list = new ArrayList<>();
        // Loop through the JSONArray
        for (int i = 0; i < array.length(); i++) {
            try {
                // Add each String to the list
                list.add(array.getString(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        // Return the author String with authors separated by comma
        return TextUtils.join(", ", list);
    }

    /**
     * Method used to parse simple Strings from {@link JSONObject}
     *
     * @param jsonObject: Declare the {@link JSONObject} being parsed
     * @param jsonTag:    Declare the desired JSON tag according to the API - in this case - Google Books
     */
    private static String parseBookInformation(JSONObject jsonObject, String jsonTag) {
        try {
            // Try to return a String value with jsonTag being parsed from jsonObject
            return jsonObject.getString(jsonTag);
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problem parsing book information at Utils.parseBookInformation from Google API", e);
        }
        // Return a constant String if not successfully parsed from jsonObject
        return JSON_NULL_RESULT;
    }

}
