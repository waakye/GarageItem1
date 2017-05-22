package com.waakye.garageitem;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.waakye.garageitem.data.UsedItemContract;

public class CatalogActivity extends AppCompatActivity {

    public static final String LOG_TAG = CatalogActivity.class.getSimpleName();

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

        // Find the ListView which will be populated with the used_items data
        ListView usedItemListView = (ListView)findViewById(R.id.list);

        // Find and set the empty view on the ListView so that it only shows when the list has
        // 0 items
        View emptyView = findViewById(R.id.empty_view);
        usedItemListView.setEmptyView(emptyView);

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

        // Find the ListView which will be populated with used_item data
        ListView usedItemListView = (ListView)findViewById(R.id.list);

        // Set up an Adapter to create a list item for each row of used_item data in the Cursor
        UsedItemCursorAdapter adapter = new UsedItemCursorAdapter(this, cursor);

        // Attach adapter to the ListView
        usedItemListView.setAdapter(adapter);
    }

    /**
     * Helper method to insert hardcoded used_items data into the database.  For debugging purposes
     * only
     */
    private void insertUsedItemViaCatalogActivity() {
        // Create a ContentValues object where column names are the keys, and Used Socks's
        // used_items attributes are the values
        ContentValues values = new ContentValues();
        values.put(UsedItemContract.UsedItemEntry.COLUMN_USED_ITEM_NAME, "Used Socks");
        values.put(UsedItemContract.UsedItemEntry.COLUMN_USED_ITEM_PRICE, 2);
        values.put(UsedItemContract.UsedItemEntry.COLUMN_USED_ITEM_QUANTITY, 5);
        values.put(UsedItemContract.UsedItemEntry.COLUMN_USED_ITEM_IMAGE_URI, "");

        // Insert a row for Used Socks into the provider using the ContentResolver.
        // Use the {@link UsedItemEntry#CONTENT_URI} to indicate that we want to insert into the
        // used_items database table
        // Receive the new content URI that will allow us to access Used Socks's data in the future
        Uri newUri = getContentResolver().insert(UsedItemContract.UsedItemEntry.CONTENT_URI, values);
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
