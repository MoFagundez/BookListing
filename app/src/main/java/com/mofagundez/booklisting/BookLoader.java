package com.mofagundez.booklisting;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Book List
 * Created by Mauricio on May 27, 2017
 * <p>
 * Udacity Android Basics Nanodegree
 * Project 7: Book Listing App
 * <p>
 * IMPORTANT NOTE: THIS CLASS IS BASED OUT OF THE EARTHQUAKE PROJECT (re-using the code we did in class)
 */
class BookLoader extends AsyncTaskLoader<List<Book>> {

    private static final String QUERY_PREFIX = "https://www.googleapis.com/books/v1/volumes?q=";
    private static final String QUERY_SUFFIX = "&maxResults=20";
    private static String queryUrl = null;

    BookLoader(Context context, String querySubject) {
        super(context);
        // Concatenate strings to be used as the Google Book API query parameter
        queryUrl = QUERY_PREFIX + querySubject + QUERY_SUFFIX;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<Book> loadInBackground() {
        List<Book> books = new ArrayList<>();
        // Create URL object
        URL url = Utils.createUrl(queryUrl);
        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = "";
        try {
            jsonResponse = Utils.makeHttpRequest(url);
            books = Utils.extractBookFromJson(jsonResponse);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return books;
    }
}
