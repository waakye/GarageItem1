package com.waakye.garageitem.data;

import android.provider.BaseColumns;

/**
 * Created by lesterlie on 5/20/17.
 */

public final class UsedItemContract {

    // To prevent someone from accidentally instantiating the contract class, give it an empty
    // constructor
    private UsedItemContract() {}

    /**
     * Inner class that defines constant values for the used_items database table.
     * Each entry represents a unique used_item
     */
    public static final class UsedItemEntry implements BaseColumns {

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
