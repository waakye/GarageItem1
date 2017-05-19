package com.waakye.garageitem.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by lesterlie on 5/20/17.
 */

public class UsedItemDbHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = UsedItemDbHelper.class.getSimpleName();

    /** Name of the database file */
    private static final String DATABASE_NAME = "used_items.db";

    /**
     * Database version.  If you change the database schema, you must increment the database version
     */
    private static final int DATABASE_VERSION = 1;

    /**
     * Constructs a new instance of {@link UsedItemDbHelper}
     * @param context of the app
     */
    public UsedItemDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * This is called when the database is created for the first time
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create a String that contains the SQL statement to create the used_items table
        String SQL_CREATE_USED_ITEMS_TABLE = "CREATE TABLE "
                + UsedItemContract.UsedItemEntry.TABLE_NAME + " ("
                + UsedItemContract.UsedItemEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + UsedItemContract.UsedItemEntry.COLUMN_USED_ITEM_NAME + " TEXT NOT NULL, "
                + UsedItemContract.UsedItemEntry.COLUMN_USED_ITEM_PRICE + " INTEGER DEFAULT 0, "
                + UsedItemContract.UsedItemEntry.COLUMN_USED_ITEM_QUANTITY + " INTEGER DEFAULT 0, "
                + UsedItemContract.UsedItemEntry.COLUMN_USED_ITEM_IMAGE_URI + " TEXT);";

        // Execute the SQL statement
        db.execSQL(SQL_CREATE_USED_ITEMS_TABLE);
    }

    /**
     * This is called when the database needs to be upgraded
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // The database is still at version 1, so there's nothing to be done here.
    }

}

