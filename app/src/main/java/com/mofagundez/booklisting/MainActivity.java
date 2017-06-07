package com.mofagundez.booklisting;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.List;

/**
 * Book List
 * Created by Mauricio on May 27, 2017
 * <p>
 * Udacity Android Basics Nanodegree
 * Project 7: Book Listing App
 */
public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Book>> {

    public static final String LOG_TAG = Utils.class.getSimpleName();
    private String querySubject;
    private ProgressBar progressBar;
    private BookAdapter mAdapter;
    private TextView emptyStateTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create a ProgressBar object and finds a layout reference
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        // Create a TextView objec and finds a layout reference
        emptyStateTextView = (TextView) findViewById(R.id.empty_text_view);

        // Check if LoaderManager is null; if not, continues to run the worker thread
        if (getLoaderManager().getLoader(0) != null) {
            getLoaderManager().initLoader(0, null, MainActivity.this);
        }

        final SearchView searchView = (SearchView) findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                try {
                    // Pass the value of the SearchView to the querySubject String to be used to perform the query later
                    querySubject = searchView.getQuery().toString();
                    // Call the method performQuery that eventually calls LoaderManager
                    performQuery();
                } catch (EmptyStackException e) {
                    e.printStackTrace();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Do nothing
                return false;
            }
        });
    }

    /**
     * Perform a query with LoaderManager with the keyword input by the user
     */
    private void performQuery() {
        clearAdapter();
        // Make progressBar visible and hide TextView to inform user that the query is being processed
        progressBar.setVisibility(View.VISIBLE);
        emptyStateTextView.setVisibility(View.GONE);
        // Check whether or not network connectivity is available and update UI accordingly
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnectedOrConnecting()) {
            getLoaderManager().restartLoader(0, null, MainActivity.this);
        } else {
            // Update UI passing a boolean parameter isConnected as false
            updateUi(null, false);
        }
    }

    /**
     * Check if mAdapter is null and clear so it won't be in the UI when performing a new query
     */
    private void clearAdapter() {
        if (mAdapter != null) {
            mAdapter.clear();
        }
    }

    @Override
    public Loader<List<Book>> onCreateLoader(int id, Bundle args) {
        // Perform search with the custom class BookLoader in a worker thread
        return new BookLoader(this, querySubject);
    }

    @Override
    public void onLoadFinished(Loader<List<Book>> loader, List<Book> data) {
        // Update the UI passing an ArrayList of books and 'true' to the boolean value isConnected
        updateUi(data, true);
        // Hide progress bar and text after UI is updated
        if (data != null) {
            emptyStateTextView.setVisibility(View.GONE);
        }
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onLoaderReset(Loader<List<Book>> loader) {
        // Clear the adapter when reseting the loader
        clearAdapter();
    }

    /**
     * Update the UI with books retrieved from a successful search, or no search result when unsuccessful search,
     * and no network connection when connection is not available.
     *
     * @param books:       This parameter is @{@link Nullable} since no {@link ArrayList} will be passed as a parameter
     *                     when connection is not available.
     * @param isConnected: Passed from when the connection is tested, it's either true or false and will update the UI
     *                     accordingly
     */
    public void updateUi(@Nullable List<Book> books, boolean isConnected) {
        // Find a reference to the {@link ListView} in the layout
        ListView bookListView = (ListView) findViewById(R.id.list_view);
        // Check boolean parameter if connection is available
        if (isConnected) {
            // Check if ArrayList of books is not null
            if (books != null) {
                // Initialise the BookAdapter
                mAdapter = new BookAdapter(this, books);
                // Set the adapter on the {@link ListView}
                // so the list can be populated in the user interface
                bookListView.setAdapter(mAdapter);
            } else {
                // Update the UI with no search results found
                emptyStateTextView.setText(R.string.empty_view_no_result);
                emptyStateTextView.setVisibility(View.VISIBLE);
                bookListView.setEmptyView(emptyStateTextView);
            }
        } else {
            // Update the UI with network not found
            emptyStateTextView.setText(R.string.empty_view_no_connection);
            emptyStateTextView.setVisibility(View.VISIBLE);
            bookListView.setEmptyView(emptyStateTextView);
            progressBar.setVisibility(View.GONE);
        }
    }

}
