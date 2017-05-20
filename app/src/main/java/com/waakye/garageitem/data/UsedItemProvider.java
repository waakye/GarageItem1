package com.waakye.garageitem.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

/**
 * Created by lesterlie on 5/20/17.
 */

public class UsedItemProvider extends ContentProvider {

    /** URI matcher code for the content URI for the used_items table */
    private static final int USED_ITEMS = 100;

    /** URI matcher code for the content URI for a single used_item in the used_items table */
    private static final int USED_ITEM_ID = 101;

    /**
     * UriMatcher object to match a content URI to a corresponding code.
     * The input passed into the constructor represents the code to return for the root URI.
     * It's common to use NO_MATCH as the input for this case
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer.  This is run the first time anything is called from the class
    static {
        // The calls to addURI() go here, for all of the content URI patterns that the provider
        // should recognize. All paths added to the UriMatcher have a corresponding code to return
        // when a match is found.

        // The content URI of the form "content://com.waakye.garageitem/used_items" will map to the
        // integer code {@link #USED_ITEMS}. This URI is used to provide access to MULTIPLE rows
        // of the used_items table.
        sUriMatcher.addURI(UsedItemContract.CONTENT_AUTHORITY, UsedItemContract.PATH_USED_ITEMS,
                USED_ITEMS);

        // The content URI of the form "content://com.waakye.garageitem/used_items/#" will map to the
        // integer code {@link #USED_ITEM_ID}. This URI is used to provide access to ONE single row
        // of the used_items table.
        //
        // In this case, the "#" wildcard is used where "#" can be substituted for an integer.
        // For example, "content://com.waakye.garageitems/used_items/used_items/3" matches, but
        // "content://com.waakye.garageitem.used_items/used_items" (without a number at the end)
        // doesn't match.
        sUriMatcher.addURI(UsedItemContract.CONTENT_AUTHORITY, UsedItemContract.PATH_USED_ITEMS
                + "/#", USED_ITEM_ID);
    }

    /** Database helper object */
    private UsedItemDbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new UsedItemDbHelper(getContext());
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        return null;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri,  ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,  String[] selectionArgs) {
        return 0;
    }
}
