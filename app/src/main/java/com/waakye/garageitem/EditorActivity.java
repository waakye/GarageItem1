package com.waakye.garageitem;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.waakye.garageitem.data.UsedItemContract;

import java.io.FileDescriptor;
import java.io.IOException;

/**
 * Created by lesterlie on 5/15/17.
 */

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * Identifier for the used_item data loader
     */
    private static final int EXISTING_USED_ITEM_LOADER = 0;

    /**
     * Content URI for the existing used_item (null if it's a new used_item)
     */
    private Uri mCurrentUsedItemUri;

    private boolean isGalleryPicture = false;

    private static final int REQUEST_IMAGE_CAPTURE = 1;

    /**
     * Tag for logging errors
     */
    private static final String LOG_TAG = CatalogActivity.class.getSimpleName();

    /**
     * EditText field to enter the item's name
     */
    private EditText nameEditText;

    /**
     * EditText field to enter the item's price
     */
    private EditText priceEditText;

    /**
     * EditText field to enter the item's quantity
     */
    private EditText quantityEditText;

    /**
     * Variables and constants related to image picker
     */
    private static final int PICK_IMAGE_REQUEST = 0;
//    private static final int SEND_MAIL_REQUEST = 1;

    private static final String STATE_URI = "STATE_URI";

    /**
     * Boolean flag that keeps track of whether the used_item has been edited (true) or not (false)
     */
    private boolean mUsedItemHasChanged = false;

    private ImageView mImageView;
    private TextView mTextView;
    private Button imageButton;

    private Uri mUri;
    private Bitmap mBitmap;

    /**
     * OnTouchListener that listens for any user touches on a View, implying that they are modifying
     * the view, and we change the mUsedItemHasChanged boolean is true
     */
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mUsedItemHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e(LOG_TAG, "onCreate() method called ... ");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        // Examine the intent that was used to launch this activity
        // in order to figure out if we're creating a new used_item or editing an existing one
        Intent intent = getIntent();
        mCurrentUsedItemUri = intent.getData();

        // If the intent DOES NOT contain a used_item content URI, then we know that we are creating
        // a new used_item
        if (mCurrentUsedItemUri == null) {
            // This is a new used_item, so change the app bar to say "Add a Used Item"
            setTitle(getString(R.string.editor_activity_title_new_used_item));
        } else {
            // Otherwise, this is an existing used_item, so change app bar to say "Edit Used Item"
            setTitle(getString(R.string.editor_activity_title_edit_used_item));

            // Initialize a loader to read the used_item data from the database
            // and display the current values to the editor
            getLoaderManager().initLoader(EXISTING_USED_ITEM_LOADER, null, this);
        }

        // Find all relevant views that we will need to read user input from
        nameEditText = (EditText) findViewById(R.id.edit_item_name);
        priceEditText = (EditText) findViewById(R.id.edit_item_price);
        quantityEditText = (EditText) findViewById(R.id.edit_item_quantity);

        // Setup OnTouchListeners on all the input fields, so we can determine if the user has
        // touched or modified them. This will let us know if there are any unsaved changes or not,
        // if the user tries to leave the editor without saving.
        nameEditText.setOnTouchListener(mTouchListener);
        priceEditText.setOnTouchListener(mTouchListener);
        quantityEditText.setOnTouchListener(mTouchListener);


        mTextView = (TextView) findViewById(R.id.image_uri);
        mImageView = (ImageView) findViewById(R.id.image);


        imageButton = (Button) findViewById(R.id.add_image_button);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openImageSelector();
            }
        });
    }

    /**
     * Get user input from editor and save used_item into database
     */
    private void saveUsedItemViaEditorActivity() {
        // TODO: If the user does not save the image again, then the app crashes

        // Read from input fields
        // Use trim to eliminate leading or trailing white space
        String nameString = nameEditText.getText().toString().trim();
        String priceString = priceEditText.getText().toString().trim();
        String quantityString = quantityEditText.getText().toString().trim();
        String imageUriString = mUri.toString().trim();
        int price = Integer.parseInt(priceString);
        int weight = Integer.parseInt(quantityString);

        // Create a ContentValues object where column names are the keys, and used_item attributes
        // from the editor are the values
        ContentValues contentValues = new ContentValues();
        contentValues.put(UsedItemContract.UsedItemEntry.COLUMN_USED_ITEM_NAME, nameString);
        contentValues.put(UsedItemContract.UsedItemEntry.COLUMN_USED_ITEM_PRICE, priceString);
        contentValues.put(UsedItemContract.UsedItemEntry.COLUMN_USED_ITEM_QUANTITY, quantityString);
        contentValues.put(UsedItemContract.UsedItemEntry.COLUMN_USED_ITEM_IMAGE_URI, imageUriString);

        // Determine if this is a new or existing used_item by checking if mCurrentUsedItemUri is null or not
        if (mCurrentUsedItemUri == null) {
            // This is a NEW used_item, so insert a new used_item into the provider
            // returning the content URI for the new used_item
            Uri newUri = getContentResolver().insert(UsedItemContract.UsedItemEntry.CONTENT_URI, contentValues);

            // Show a toast message depending on whether or not the insertion was successful
            if (newUri == null) {
                // If the new content URI is null, then there was an error with insertion
                Toast.makeText(this, getString(R.string.editor_insert_used_item_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast
                Toast.makeText(this, getString(R.string.editor_insert_used_item_successful),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            // Otherwise, this is an EXISTING used_item, so update the used_item with content URI:
            // mCurrentUsedItemUri and pass in the new ContentValues.  Pass in null for the
            // selection and selection args because mCurrentUsedItemUri will already identify the
            // correct row in the database that we want to modify
            int rowsAffected = getContentResolver().update(mCurrentUsedItemUri, contentValues, null, null);

            // Show a toast message depending on whether or not the update was successful
            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the update
                Toast.makeText(this, getString(R.string.editor_update_used_item_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the update was successful and we can display a toast
                Toast.makeText(this, getString(R.string.editor_update_used_item_successful),
                        Toast.LENGTH_SHORT).show();
            }

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.e(LOG_TAG, "onCreateOptionsMenu() method called ... ");
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.e(LOG_TAG, "onOptionsItemSelected() method called ... ");
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                // Save used_item to database
                saveUsedItemViaEditorActivity();
                // Exit activity
                finish();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                // Do nothing for now
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // If the used_item hasn't changed, continue with navigating up to parent activity
                // which is the {@link CatalogActivity}
                if (!mUsedItemHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }
                // Otherwise, if there are unsaved changes, set up a dialog to warn the user.
                // Create a click listener to handle the user confirming that changes should be
                // discarded
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };
                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * This method is called when the back button is pressed
     */
    @Override
    public void onBackPressed() {
        // If the used_item hasn't changed, continue with handling back button press
        if (!mUsedItemHasChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise, if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    public void openImageSelector() {
        Log.e(LOG_TAG, "openImageSelector() called ...");
        Intent intent;

        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
        } else {
            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
        }

        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    /**
     * After the user selects a document in the picker, onActivityResult() gets called.
     * The resultData parameter contains the URI that points to the selected document.
     * Extract the URI using getData().  When you have it, you can use it to retrieve the document
     * the user wants
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        Log.i(LOG_TAG, "Received an \"Activity Result\"");
        // The ACTION_OPEN_DOCUMENT intent was sent with the request code READ_REQUEST_CODE.
        // If the request code seen here doesn't match, it's the response to some other intent,
        // and the below code shouldn't run at all.

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            // The document selected by the user won't be returned in the intent.
            // Instead, a URI to that document will be contained in the return intent
            // provided to this method as a parameter.  Pull that uri using "resultData.getData()"

            if (resultData != null) {
                mUri = resultData.getData();
                Log.i(LOG_TAG, "Uri: " + mUri.toString());

                mTextView.setText(mUri.toString());
                mBitmap = getBitmapFromUri(mUri);
                mImageView.setImageBitmap(mBitmap);

                isGalleryPicture = true;
            }
        } else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            Log.i(LOG_TAG, "Uri: " + mUri.toString());

            mTextView.setText(mUri.toString());
            mBitmap = getBitmapFromUri(mUri);
            mImageView.setImageBitmap(mBitmap);

            isGalleryPicture = false;
        }
    }


    private Bitmap getBitmapFromUri(Uri uri) {
        Log.e(LOG_TAG, "getBitmapFromUri() method called ... ");
        ParcelFileDescriptor parcelFileDescriptor = null;
        try {
            parcelFileDescriptor =
                    getContentResolver().openFileDescriptor(uri, "r");
            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
            Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
            parcelFileDescriptor.close();
            return image;
        } catch (Exception e) {
            Log.e(LOG_TAG, "Failed to load image.", e);
            return null;
        } finally {
            try {
                if (parcelFileDescriptor != null) {
                    parcelFileDescriptor.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(LOG_TAG, "Error closing ParcelFile Descriptor");
            }
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Since the editor shows all used_item attributes, define a projection that contains all
        // columns from the used_items table
        String[] projection = {
                UsedItemContract.UsedItemEntry._ID,
                UsedItemContract.UsedItemEntry.COLUMN_USED_ITEM_NAME,
                UsedItemContract.UsedItemEntry.COLUMN_USED_ITEM_PRICE,
                UsedItemContract.UsedItemEntry.COLUMN_USED_ITEM_QUANTITY,
                UsedItemContract.UsedItemEntry.COLUMN_USED_ITEM_IMAGE_URI};

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                mCurrentUsedItemUri,    // Query the content URI for the current used_item
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }

    /**
     * Show a dialog that warns the user there are unsaved changes that will be lost if they
     * continue leaving the editor
     * @param discardButtonClickListener    is the click listener for what to do when the user
     *                                      confirms they want to discard their changes
     */
    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int id){
                // User clicked the "Keep editing" button, so dismiss the dialog and continue
                // editing the used_item
                if(dialog != null){
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // End early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (cursor.moveToFirst()) {
            // Find the columns of used_item attributes that we're interested in
            int nameColumnIndex = cursor.getColumnIndex(UsedItemContract.UsedItemEntry.COLUMN_USED_ITEM_NAME);
            int priceColumnIndex = cursor.getColumnIndex(UsedItemContract.UsedItemEntry.COLUMN_USED_ITEM_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(UsedItemContract.UsedItemEntry.COLUMN_USED_ITEM_QUANTITY);
            int imageUriColumnIndex = cursor.getColumnIndex(UsedItemContract.UsedItemEntry.COLUMN_USED_ITEM_IMAGE_URI);

            // Extract out the value from the Cursor for the given column index
            String name = cursor.getString(nameColumnIndex);
            int price = cursor.getInt(priceColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);
            String imageUri = cursor.getString(imageUriColumnIndex);

            // Update the views on the screen with the values from the database
            nameEditText.setText(name);
            priceEditText.setText(Integer.toString(price));
            quantityEditText.setText(Integer.toString(quantity));
            mTextView.setText(imageUri);
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields
        nameEditText.setText("");
        priceEditText.setText("");
        quantityEditText.setText("");
        mTextView.setText("");

    }
}
