package com.waakye.garageitem;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.waakye.garageitem.data.UsedItemContract;

/**
 * Created by lesterlie on 5/22/17.
 */

public class UsedItemCursorAdapter extends CursorAdapter{

    /**
     * Constructs a new {@link UsedItemCursorAdapter}
     * @param context  The context
     * @param c    The cursor from which to get the data
     */
    public UsedItemCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    /**
     * Makes a blank new list item view.  No data is set (or bound) to the views yet.
     * @param context   app context
     * @param cursor    The cursor from which to get data.  The cursor is already moved to the
     *                  correct position
     * @param parent    The parent to which the new view is attached to
     * @return          The newly created list item view
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Inflate a list item view using the layout specified in list_item.xml
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    /**
     * This item binds the used_items data (in the current row pointed to by cursor) to the given
     * list item layout.  For example, the name for the current used_item can be set on the name
     * TextView in the list item layout
     * @param view      Existing view, returned earlier by newView() method
     * @param context   app context
     * @param cursor    The cursor from which to get the data.  The cursor is already moved to the
     *                  correct row
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Find the individual views that we want to modify in the list item layout
        TextView nameTextView = (TextView)view.findViewById(R.id.used_item_name);
        TextView priceTextView = (TextView)view.findViewById(R.id.used_item_price);
        TextView quantityTextView = (TextView)view.findViewById(R.id.used_item_quantity);
        TextView imageUriTextView = (TextView)view.findViewById(R.id.used_item_image_uri);

        // Find the columns of used_item attributes that we're interested in
        int nameColumnIndex = cursor.getColumnIndex(UsedItemContract.UsedItemEntry.COLUMN_USED_ITEM_NAME);
        int priceColumnIndex = cursor.getColumnIndex(UsedItemContract.UsedItemEntry.COLUMN_USED_ITEM_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(UsedItemContract.UsedItemEntry.COLUMN_USED_ITEM_QUANTITY);
        int imageUriColumnIndex = cursor.getColumnIndex(UsedItemContract.UsedItemEntry.COLUMN_USED_ITEM_IMAGE_URI);

        // Read the used_item attributes that we're interested in
        String usedItemName = cursor.getString(nameColumnIndex);
        String usedItemPrice = cursor.getString(priceColumnIndex);
        String usedItemQuantity = cursor.getString(quantityColumnIndex);
        String usedItemImageUri = cursor.getString(imageUriColumnIndex);

        // Update the TextViews with the attributes for the current used_item
        nameTextView.setText(usedItemName);
        priceTextView.setText(usedItemPrice);
        quantityTextView.setText(usedItemQuantity);
        imageUriTextView.setText(usedItemImageUri);
    }
}
