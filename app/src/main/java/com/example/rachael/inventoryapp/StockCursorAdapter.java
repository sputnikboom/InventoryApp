package com.example.rachael.inventoryapp;

import android.content.Context;
import android.database.Cursor;
import android.nfc.TagLostException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
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
    public void bindView(View view, Context context, Cursor cursor) {

        TextView nameTextView = view.findViewById(R.id.list_product_name);
        TextView priceTextView = view.findViewById(R.id.list_product_price);
        TextView quantityTextView = view.findViewById(R.id.list_product_quantity);

        int nameColumnIndex = cursor.getColumnIndex(StockEntry.COLUMN_ITEM_NAME);
        int priceColumnIndex = cursor.getColumnIndex(StockEntry.COLUMN_ITEM_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(StockEntry.COLUMN_ITEM_QUANTITY);

        String productName = cursor.getString(nameColumnIndex);
        String productPrice = cursor.getColumnName(priceColumnIndex);
        String productQuantity = cursor.getColumnName(quantityColumnIndex);

        nameTextView.setText(productName);
        priceTextView.setText(productPrice);
        quantityTextView.setText(productQuantity);
    }
}
