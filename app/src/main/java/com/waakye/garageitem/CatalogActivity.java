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
        // Define a projection that specifies which columns from the database you will actually use
        // after this query
        String[] projection = {
                UsedItemContract.UsedItemEntry._ID,
                UsedItemContract.UsedItemEntry.COLUMN_USED_ITEM_NAME,
                UsedItemContract.UsedItemEntry.COLUMN_USED_ITEM_PRICE,
                UsedItemContract.UsedItemEntry.COLUMN_USED_ITEM_QUANTITY,
                UsedItemContract.UsedItemEntry.COLUMN_USED_ITEM_IMAGE_URI};

        // Perform a query on the provider using the ContactResolver.
        // Use the {@link UsedItemEntry#CONTENT_URI} to access the used_item data
        Cursor cursor = getContentResolver().query(
                UsedItemContract.UsedItemEntry.CONTENT_URI,   // The content URI of the used_items table
                projection,                                   // The columns to return for each row
                null,                                         // Selection criteria
                null,                                         // Selection criteria
                null);                                        // The sort order for the returned rows

        TextView displayView = (TextView)findViewById(R.id.text_view_used_item);

        try {
            // Create a header in the TextView that looks like this:
            //
            // The used_items table contains <number of rows in Cursor> used_items
            // _id - name - price - quantity - image uri
            //
            //  In the while loop below, iterate through the rows of the cursor and display the
            // information from each column in this order
            displayView.setText("The used_items table contains " + cursor.getCount() + " used_items.\n\n");
            displayView.append(UsedItemContract.UsedItemEntry._ID + " - "
                        + UsedItemContract.UsedItemEntry.COLUMN_USED_ITEM_NAME + " - "
                        + UsedItemContract.UsedItemEntry.COLUMN_USED_ITEM_PRICE + " - "
                        + UsedItemContract.UsedItemEntry.COLUMN_USED_ITEM_QUANTITY + " - "
                        + UsedItemContract.UsedItemEntry.COLUMN_USED_ITEM_IMAGE_URI + "\n");

            // Figure out the index of each column
            int idColumnIndex = cursor.getColumnIndex(UsedItemContract.UsedItemEntry._ID);
            int nameColumnIndex = cursor.getColumnIndex(UsedItemContract.UsedItemEntry.COLUMN_USED_ITEM_NAME);
            int priceColumnIndex = cursor.getColumnIndex(UsedItemContract.UsedItemEntry.COLUMN_USED_ITEM_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(UsedItemContract.UsedItemEntry.COLUMN_USED_ITEM_QUANTITY);
            int imageUriColumnIndex = cursor.getColumnIndex(UsedItemContract.UsedItemEntry.COLUMN_USED_ITEM_IMAGE_URI);

            // iterate through all the returned rows in the cursor
            while(cursor.moveToNext()) {
                // Use that index to extract the String or Int value of the word
                // at the current row the cursor is on
                int currentID = cursor.getInt(idColumnIndex);
                String currentName = cursor.getString(nameColumnIndex);
                int currentPrice = cursor.getInt(priceColumnIndex);
                int currentQuantity = cursor.getInt(quantityColumnIndex);
                String currentImageUri = cursor.getString(imageUriColumnIndex);

                // Display the values from each column of the current row in the cursor in the TextView
                displayView.append("\n" + currentID + " - "
                        + currentName + " - "
                        + currentPrice + " - "
                        + currentQuantity + " - "
                        + currentImageUri);
            }
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
