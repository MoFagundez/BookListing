package com.mofagundez.booklisting;

import android.graphics.Bitmap;

/**
 * Book List
 * Created by Mauricio on May 27, 2017
 * <p>
 * Udacity Android Basics Nanodegree
 * Project 7: Book Listing App
 */
class Book {
    private String mTitle;
    private String mAuthor;
    private String mYear;
    private Bitmap mThumbnail;
    private String mUrl;

    /**
     * Default constructor to instantiate the class
     */
    Book(String title, String author, String year, Bitmap thumbnail, String url) {
        this.mTitle = title;
        this.mAuthor = author;
        this.mYear = year;
        this.mThumbnail = thumbnail;
        this.mUrl = url;
    }

    /**
     * List of getters
     */
    String getTitle() {
        return mTitle;
    }

    String getAuthor() {
        return mAuthor;
    }

    String getYear() {
        return mYear;
    }

    Bitmap getThumbnail() {
        return mThumbnail;
    }

    String getUrl() {
        return mUrl;
    }
}
