package com.waakye.garageitem.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by lesterlie on 5/20/17.
 */

public final class UsedItemContract {

    // To prevent someone from accidentally instantiating the contract class, give it an empty
    // constructor
    private UsedItemContract() {}

    /**
     * The "Content authority" is a name for the entire content provider, similar to the
     * relationship between a domain name and its website.  A convenient string to use for the
     * content authority is the package name for the app, which is guaranteed to be unique on the
     * device
     */
    public static final String CONTENT_AUTHORITY = "com.waakye.garageitem";

    /**
     * Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact the
     * content provider
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /**
     * Possible path (appended to base content URI for possible URI's)
     */
    public static final String PATH_USED_ITEMS = "used_items";

    /**
     * Inner class that defines constant values for the used_items database table.
     * Each entry represents a unique used_item
     */
    public static final class UsedItemEntry implements BaseColumns {

        /** The content URI to access the used_item data in the provider. */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI,
                PATH_USED_ITEMS);

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of used_items
         */
        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
                + "/" + CONTENT_AUTHORITY + "/" + PATH_USED_ITEMS;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single used_item
         */
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
                + "/" + PATH_USED_ITEMS;

        /** Name of database table for used_items */
        public final static String TABLE_NAME = "used_items";

        /**
         * Unique ID number for the used_items (only for use in the database table).
         * Type: INTEGER
         */
        public final static String _ID = BaseColumns._ID;

        /**
         * Name of the used_item
         * TYPE: TEXT
         */
        public final static String COLUMN_USED_ITEM_NAME = "name";

        /**
         * Price of the used_item
         * TYPE: INTEGER
         */
        public final static String COLUMN_USED_ITEM_PRICE = "price";

        /**
         * Quantity of the used_item
         * TYPE: INTEGER
         */
        public final static String COLUMN_USED_ITEM_QUANTITY = "quantity";

        /**
         * URI of image for used_item
         * TYPE: TEXT
         */
        public final static String COLUMN_USED_ITEM_IMAGE_URI = "image_uri";
    }
}
