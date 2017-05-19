package com.waakye.garageitem;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.waakye.garageitem.data.UsedItemContract;
import com.waakye.garageitem.data.UsedItemDbHelper;

public class CatalogActivity extends AppCompatActivity {

    public static final String LOG_TAG = CatalogActivity.class.getSimpleName();

    /** Database helper that will provide us access to the database */
    private UsedItemDbHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        // Set up Add Item Button to open EditorActivity
        Button addItemButton = (Button) findViewById(R.id.add_item_button);
        addItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        // To access our database, we instantiate our subclass of SQLiteOpenHelper and pass the
        // context, which is the current activity
        mDbHelper = new UsedItemDbHelper(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        displayDatabaseInfo();
    }

    /**
     * Temproary helper method to display information in the onscreen TextView about the state of
     * the used_items database
     */
    private void displayDatabaseInfo() {
        // Create and/or open a database to read from it
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // Define a projection that specifies which columns from the database you will actually use
        // after this query
        String[] projection = {
                UsedItemContract.UsedItemEntry._ID,
                UsedItemContract.UsedItemEntry.COLUMN_USED_ITEM_NAME,
                UsedItemContract.UsedItemEntry.COLUMN_USED_ITEM_PRICE,
                UsedItemContract.UsedItemEntry.COLUMN_USED_ITEM_QUANTITY,
                UsedItemContract.UsedItemEntry.COLUMN_USED_ITEM_IMAGE_URI};

        // Perform a query on the used_items table
        Cursor cursor = db.query(
                UsedItemContract.UsedItemEntry.TABLE_NAME,  // The table to query
                projection,                                 // The columns to return
                null,                                       // The columns for the WHERE clause
                null,                                       // The values for the WHERE clause
                null,                                       // Don't group the rows
                null,                                       // Don't filter by row groups
                null);                                      // The sort order

        try {
            // Display the number of rows in the Cursor (which reflects the number of rows in the
            // used_items table in the database).
            TextView displayView = (TextView)findViewById(R.id.text_view_used_item);
            displayView.setText("Number of rows in used_items database table: " + cursor.getCount());
        } finally {
            // Always close the cursor when you're done reading from it. This releases all its
            // resources and makes it invalid
            cursor.close();
        }
    }

    /**
     * Helper method to insert hardcoded used_items data into the database.  For debugging purposes
     * only
     */
    private void insertUsedItemViaCatalogActivity() {
        // Gets the database in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Create a ContentValues object where column names are the keys, and Used Socks's
        // used_items attributes are the values
        ContentValues values = new ContentValues();
        values.put(UsedItemContract.UsedItemEntry.COLUMN_USED_ITEM_NAME, "Used Socks");
        values.put(UsedItemContract.UsedItemEntry.COLUMN_USED_ITEM_PRICE, 2);
        values.put(UsedItemContract.UsedItemEntry.COLUMN_USED_ITEM_QUANTITY, 5);
        values.put(UsedItemContract.UsedItemEntry.COLUMN_USED_ITEM_IMAGE_URI, "");


        // Insert a new row for Used Socks in the database, returning the ID of that new row
        // The first argument for db.insert() is the used_items table name.
        // The second argument provides the name of a column in which the framework
        // can insert NULL in the event that the ContentValues is empty (if
        // this is set to "null", then the framework will not insert a row when
        // there are no values).
        // The third argument is the ContentValues object containing the info for Used Socks.
        long newRowId = db.insert(UsedItemContract.UsedItemEntry.TABLE_NAME, null, values);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file
        // This adds menu items to the app bar
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch(item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                insertUsedItemViaCatalogActivity();
                displayDatabaseInfo();
                return true;
            case R.id.action_delete_all_entries:
                // Do nothing for now
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
