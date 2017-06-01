package com.mofagundez.booklisting;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Book List
 * Created by Mauricio on May 27, 2017
 * <p>
 * Udacity Android Basics Nanodegree
 * Project 7: Book Listing App
 */
class BookAdapter extends ArrayAdapter<Book> {

    BookAdapter(@NonNull Context context, @NonNull List<Book> objects) {
        super(context, 0, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        // Check if View is already created - if not, create a View
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.book_list_view, parent, false);
        }
        // Create a variable to store ibnformation of the current book populating listItemView
        final Book currentBook = getItem(position);

        // Create variable titleTextView, find a layout reference and pass the value from currentBook
        TextView titleTextView = (TextView) listItemView.findViewById(R.id.title_text_view);
        titleTextView.setText(currentBook.getTitle());

        // Create variable authorTextView, find a layout reference and pass the value from currentBook
        TextView authorTextView = (TextView) listItemView.findViewById(R.id.author_text_view);
        authorTextView.setText(currentBook.getAuthor());

        // Create variable yearTextView, find a layout reference and pass the value from currentBook
        TextView yearTextView = (TextView) listItemView.findViewById(R.id.year_text_view);
        yearTextView.setText(currentBook.getYear());

        // Create variable thumbnailImageView, find a layout reference and pass the value from currentBook
        ImageView thumbnailImageView = (ImageView) listItemView.findViewById(R.id.image_thumbnail);
        thumbnailImageView.setImageBitmap(currentBook.getThumbnail());

        // Set click listener to the listItemView throwing an implicit intent
        // to open the book's website
        listItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = currentBook.getUrl();
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                getContext().startActivity(i);

            }
        });

        return listItemView;
    }

}
