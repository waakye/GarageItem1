package com.waakye.garageitem;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.waakye.garageitem.data.UsedItemContract;

public class CatalogActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor>{

    /** Identifier for the used_items data loader */
    private static final int USED_ITEM_LOADER = 0;

    /** Adapter for the ListView */
    UsedItemCursorAdapter mCursorAdapter;

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

        // There is no used_item data yet (until the loader finishes) so pass in null for the Cursor
        mCursorAdapter = new UsedItemCursorAdapter(this, null);
        usedItemListView.setAdapter(mCursorAdapter);

        // Setup the item click listener
        usedItemListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // Create new intent to go to {@link EditorActivity}
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);

                // Form the content URI that represents the specific used_item that was clicked on,
                // by appending the "id" (passed as input to this method) onto the
                // {@link UsedItemEntry#CONTENT_URI}.
                // For example, the URI would be "content://com.waakye.garageitem/used_items/2"
                // if the used_item with ID 2 was clicked on
                Uri currentUsedItemUri
                        = ContentUris.withAppendedId(UsedItemContract.UsedItemEntry.CONTENT_URI,id);

                // Set the URI on the data field of the intent
                intent.setData(currentUsedItemUri);

                // Launch the {@link EditorActivity} to display the data for the current used_item
                startActivity(intent);
            }
        });

        // Kick off the loader
        getLoaderManager().initLoader(USED_ITEM_LOADER, null, this);
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
                return true;
            case R.id.action_delete_all_entries:
                // Do nothing for now
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Define a projection that specifies the columns from the table we care about
        String[] projection = {
                UsedItemContract.UsedItemEntry._ID,
                UsedItemContract.UsedItemEntry.COLUMN_USED_ITEM_NAME,
                UsedItemContract.UsedItemEntry.COLUMN_USED_ITEM_PRICE,
                UsedItemContract.UsedItemEntry.COLUMN_USED_ITEM_QUANTITY,
                UsedItemContract.UsedItemEntry.COLUMN_USED_ITEM_IMAGE_URI};

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,                           // Parent activity context
                UsedItemContract.UsedItemEntry.CONTENT_URI,     // Provider content URI to query
                projection,                                     // Columns to include in the resulting Cursor
                null,                                           // No selection clause
                null,                                           // No selection arguments
                null);                                          // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Update {@link UsedItemCursorAdapter} with this new cursor containing updated used_item data
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Callback called when the data needs to be deleted
        mCursorAdapter.swapCursor(null);
    }
}
