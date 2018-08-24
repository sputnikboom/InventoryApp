package com.example.rachael.inventoryapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.nfc.TagLostException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rachael.inventoryapp.data.StockContract;
import com.example.rachael.inventoryapp.data.StockContract.StockEntry;

public class StockCursorAdapter extends CursorAdapter {


    public StockCursorAdapter(Context context, Cursor c) {
        super(context, c);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {

        TextView nameTextView = view.findViewById(R.id.list_product_name);
        TextView priceTextView = view.findViewById(R.id.list_product_price);
        final TextView quantityTextView = view.findViewById(R.id.list_product_quantity);
        final ImageButton saleButton = view.findViewById(R.id.sale_button);
        final LinearLayout stockListView = view.findViewById(R.id.list_item);


        int idColumnIndex = cursor.getColumnIndex(StockEntry._ID);
        int nameColumnIndex = cursor.getColumnIndex(StockEntry.COLUMN_ITEM_NAME);
        int priceColumnIndex = cursor.getColumnIndex(StockEntry.COLUMN_ITEM_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(StockEntry.COLUMN_ITEM_QUANTITY);

        final int id = cursor.getInt(idColumnIndex);
        String productName = cursor.getString(nameColumnIndex);
        String productPrice = cursor.getString(priceColumnIndex);
        String productQuantity = cursor.getString(quantityColumnIndex);

        nameTextView.setText(productName);
        priceTextView.setText(productPrice);
        quantityTextView.setText(productQuantity);

        final Uri currentProductUri = ContentUris.withAppendedId(StockEntry.CONTENT_URI, id);

        stockListView.setOnClickListener(new AdapterView.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, EditorActivity.class);
                intent.setData(currentProductUri);
                context.startActivity(intent);
            }
        });

        //set onClickListener for sale buttons
        saleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String quantityString = quantityTextView.getText().toString();
                int quantity = Integer.parseInt(quantityString);

                if (quantity == 0) {
                    Toast.makeText(context, R.string.stock_quantity_warning,
                            Toast.LENGTH_SHORT).show();
                } else {
                    quantity--;

                    ContentValues values = new ContentValues();
                    quantityString = Integer.toString(quantity);

                    values.put(StockEntry.COLUMN_ITEM_QUANTITY, quantityString);

                    int rowsUpdated = context.getContentResolver().update(currentProductUri, values, null, null);
                        if (rowsUpdated == 0) {
                            Toast.makeText(context, R.string.stock_update_failed,
                                    Toast.LENGTH_SHORT).show();
                        }
                    quantityTextView.setText(quantityString);
                }
            }
        });


//
//                } else {
//                    quantity--;
//                    values.put(StockEntry.COLUMN_ITEM_QUANTITY, quantity);
//                    quantityString = Integer.toString(quantity);
//                    quantityTextView.setText(quantityString);
//                }
//            }
//        });

    }
}



