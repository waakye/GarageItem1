package com.waakye.garageitem;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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

public class EditorActivity extends AppCompatActivity {

    private boolean isGalleryPicture = false;


    private static final int REQUEST_IMAGE_CAPTURE = 1;

    /** Tag for logging errors */
    private static final String LOG_TAG = CatalogActivity.class.getSimpleName();

    /** EditText field to enter the item's name */
    private EditText nameEditText;

    /** EditText field to enter the item's price */
    private EditText priceEditText;

    /** EditText field to enter the item's quantity */
    private EditText quantityEditText;

    /** Variables and constants related to image picker */
    private static final int PICK_IMAGE_REQUEST = 0;
//    private static final int SEND_MAIL_REQUEST = 1;

    private static final String STATE_URI = "STATE_URI";

    private ImageView mImageView;
    private TextView mTextView;
    private Button imageButton;

    private Uri mUri;
    private Bitmap mBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e(LOG_TAG, "onCreate() method called ... ");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        // Find all relevant views that we will need to read user input from
        nameEditText = (EditText)findViewById(R.id.edit_item_name);
        priceEditText = (EditText)findViewById(R.id.edit_item_price);
        quantityEditText = (EditText)findViewById(R.id.edit_item_quantity);

        mTextView = (TextView) findViewById(R.id.image_uri);
        mImageView = (ImageView) findViewById(R.id.image);


        imageButton = (Button)findViewById(R.id.add_image_button);
        imageButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                openImageSelector();
            }
        });
    }

    /**
     * Get user input from editor and save new used_item into database
     */
    private void insertUsedItemViaEditorActivity() {

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

        // Insert a new used_item into the provider, returning the content URI for the new used_item
        Uri newUri = getContentResolver().insert(UsedItemContract.UsedItemEntry.CONTENT_URI,
                contentValues);
        if(newUri == null) {
            // If the new content URI is null, then there was an error with insertion
            Toast.makeText(this, getString(R.string.editor_insert_used_item_failed),
                    Toast.LENGTH_SHORT).show();
        } else {
            // Otherwise, the insertion was successful and we can display a toast.
            Toast.makeText(this, getString(R.string.editor_insert_used_item_successful),
                    Toast.LENGTH_SHORT).show();
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
        switch (item.getItemId()){
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                // Save used_item to database
                insertUsedItemViaEditorActivity();
                // Exit activity
                finish();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                // Do nothing for now
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // Navigate back to parent activity (CatalogActivity)
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
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

}
