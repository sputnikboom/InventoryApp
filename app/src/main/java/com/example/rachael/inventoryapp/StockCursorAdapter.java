package com.example.rachael.inventoryapp;

import android.content.Context;
import android.database.Cursor;
import android.nfc.TagLostException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

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
    public void bindView(View view, Context context, final Cursor cursor) {

        TextView nameTextView = view.findViewById(R.id.list_product_name);
        TextView priceTextView = view.findViewById(R.id.list_product_price);
        final TextView quantityTextView = view.findViewById(R.id.list_product_quantity);

        int nameColumnIndex = cursor.getColumnIndex(StockEntry.COLUMN_ITEM_NAME);
        int priceColumnIndex = cursor.getColumnIndex(StockEntry.COLUMN_ITEM_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(StockEntry.COLUMN_ITEM_QUANTITY);

        String productName = cursor.getString(nameColumnIndex);
        String productPrice = cursor.getString(priceColumnIndex);
        String productQuantity = cursor.getString(quantityColumnIndex);

        nameTextView.setText(productName);
        priceTextView.setText(productPrice);
        quantityTextView.setText(productQuantity);

        // set onClickListener for sale buttons
        final ImageButton saleButton = view.findViewById(R.id.sale_button);
        saleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processSale();
            }

            public void processSale() {
                String quantityString = quantityTextView.getText().toString();
                int quantity = Integer.parseInt(quantityString);

                if (quantity == 0) {
                    // TODO naughty naughty toast
                } else {
                    quantity--;
                    quantityString = Integer.toString(quantity) ;
                    quantityTextView.setText(quantityString);
                }
            }
        });

        }
    }



