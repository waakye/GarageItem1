package com.waakye.garageitem.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

/**
 * Created by lesterlie on 5/20/17.
 */

public class UsedItemProvider extends ContentProvider {

    /** Tag for the log messages */
    public static final String LOG_TAG = UsedItemProvider.class.getSimpleName();

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
        // Get readable database
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        // This cursor will hold the result of the query
        Cursor cursor;

        // Figure out if the URI matcher can match the URI to a specific code
        int match = sUriMatcher.match(uri);
        switch(match) {
            case USED_ITEMS:
                // For the USED_ITEMS code, query the used_items table directly with the given
                // projection, selection, selection arguments, and sort order. The cursor could
                // contain multiple rows of the used_items table
                cursor = database.query(UsedItemContract.UsedItemEntry.TABLE_NAME, projection,
                        selection, selectionArgs, null, null, sortOrder);
                break;
            case USED_ITEM_ID:
                // For the USED_ITEM_ID code, extract out the ID from the URI.  For an example URI
                // such as "content://com.waakye.garageitem.used_items/used_items/3", the
                // selection will be "__id=?" and the selection argument will be a String array
                // containing the actual ID of 3 in this case.
                //
                // For every "?" in the selection, we need to have an element in the selection
                // arguments that will fill in the "?".  Since we have 1 question mark in the
                // selection, we have 1 String in the selection arguments' String array.
                selection = UsedItemContract.UsedItemEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};

                // This will perform a query on the used_items table where the _id equals 3 to
                // return a Cursor containing that row of the table.
                cursor = database.query(UsedItemContract.UsedItemEntry.TABLE_NAME, projection,
                        selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri,  ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch(match) {
            case USED_ITEMS:
                return insertUsedItemViaProvider(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    /**
     * Insert a used_item into the database with the given content values.  Return the new content
     * URI for that specific row in the database
     */
    private Uri insertUsedItemViaProvider(Uri uri, ContentValues values) {
        // Check that the name is not null
        String name = values.getAsString(UsedItemContract.UsedItemEntry.COLUMN_USED_ITEM_NAME);
        if(name == null) {
            throw new IllegalArgumentException("Used Item requires a name");
        }

        // If the price is provided, check that it's greater than or equal to 0
        Integer price = values.getAsInteger(UsedItemContract.UsedItemEntry.COLUMN_USED_ITEM_PRICE);
        if (price != null && price < 0) {
            throw new IllegalArgumentException("Used Item requires a valid price");
        }

        // If the quantity is provided, check that it's greater than or equal to 0
        Integer quantity = values.getAsInteger(UsedItemContract.UsedItemEntry.COLUMN_USED_ITEM_QUANTITY);
        if (quantity != null & quantity < 0) {
            throw new IllegalArgumentException("Used Item requires a valid quantity");
        }

        // Check that there is a content URI for the image
        String image = values.getAsString(UsedItemContract.UsedItemEntry.COLUMN_USED_ITEM_IMAGE_URI);
        if (image == null) {
            throw new IllegalArgumentException("Used Item requires a photo");
        }

        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Insert the new used_item with the given values
        long id = database.insert(UsedItemContract.UsedItemEntry.TABLE_NAME, null, values);
        // If the ID is -1, then the insertion failed.  Log an error and return null
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        // Return the new URI with the ID (of the newly inserted row) appended at the end
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        final int match = sUriMatcher.match(uri);
        switch(match) {
            case USED_ITEMS:
                // Delete all rows that match the selection and selection args
                return database.delete(UsedItemContract.UsedItemEntry.TABLE_NAME, selection, selectionArgs);
            case USED_ITEM_ID:
                // Delete a single row given by the ID in the URI
                selection = UsedItemContract.UsedItemEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                return database.delete(UsedItemContract.UsedItemEntry.TABLE_NAME, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch(match) {
            case USED_ITEMS:
                return updateUsedItem(uri, contentValues, selection, selectionArgs);
            case USED_ITEM_ID:
                // For the USED_ITEM code, extract out the ID from the URI, so we know which row
                // to update.  Selection will be "_id=?" and selection arguments will be a String
                // array containing the actual ID
                selection = UsedItemContract.UsedItemEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                return updateUsedItem(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    /**
     * Updated used_items in the database with the given content values.  Apply the changes to the
     * rows specified in the selection and selection arguments (which could be 0 or 1 or more
     * used_items).
     * Return the number of rows that were successful updated.
     */
    private int updateUsedItem(Uri uri, ContentValues values, String selection,
                               String[] selectionArgs) {
        if (values.containsKey(UsedItemContract.UsedItemEntry.COLUMN_USED_ITEM_NAME)){
            String name = values.getAsString(UsedItemContract.UsedItemEntry.COLUMN_USED_ITEM_NAME);
            if(name == null) {
                throw new IllegalArgumentException("Used Item needs a name");
            }
        }

        // If the {@link UsedItemEntry#COLUMN_USED_ITEM_NAME} key is present, check that the name
        // value is not null
        if(values.containsKey(UsedItemContract.UsedItemEntry.COLUMN_USED_ITEM_NAME)) {
            String name = values.getAsString(UsedItemContract.UsedItemEntry.COLUMN_USED_ITEM_NAME);
            if(name == null) {
                throw new IllegalArgumentException("Used Item requires a name.");
            }
        }

        // If the {@link UsedItemEntry#COLUMN_USED_ITEM_PRICE} key is present, check that the price
        // is valid
        if (values.containsKey(UsedItemContract.UsedItemEntry.COLUMN_USED_ITEM_PRICE)){
            // Check that the price is greater than or equal to 0
            Integer price = values.getAsInteger(UsedItemContract.UsedItemEntry.COLUMN_USED_ITEM_PRICE);
            if(price != null && price < 0) {
                throw new IllegalArgumentException("Used Item requires valid price.");
            }
        }

        // If the {@link UsedItemEntry#COLUMN_USED_ITEM_QUANTITY} key is present, check that the
        // quantity is valid
        if (values.containsKey(UsedItemContract.UsedItemEntry.COLUMN_USED_ITEM_QUANTITY)){
            // Check that the price is greater than or equal to 0
            Integer quantity = values.getAsInteger(UsedItemContract.UsedItemEntry.COLUMN_USED_ITEM_QUANTITY);
            if(quantity != null && quantity < 0) {
                throw new IllegalArgumentException("Used Item requires valid quantity.");
            }
        }

        if (values.containsKey(UsedItemContract.UsedItemEntry.COLUMN_USED_ITEM_IMAGE_URI)){
            String imageUri = values.getAsString(UsedItemContract.UsedItemEntry.COLUMN_USED_ITEM_IMAGE_URI);
            if(imageUri == null) {
                throw new IllegalArgumentException("Used Item needs an image uri.");
            }
        }
        // Otherwise, get writeable database to update the data
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Return the number of database rows affected by the update statement
        return database.update(UsedItemContract.UsedItemEntry.TABLE_NAME, values, selection,
                selectionArgs);
    }
}
