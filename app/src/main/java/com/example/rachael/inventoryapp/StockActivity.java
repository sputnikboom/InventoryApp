package com.example.rachael.inventoryapp;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.rachael.inventoryapp.data.StockContract.StockEntry;
import com.example.rachael.inventoryapp.data.StockDbHelper;

public class StockActivity extends AppCompatActivity {

    public static final String LOG_TAG = StockActivity.class.getSimpleName();

    private StockDbHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StockActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        mDbHelper = new StockDbHelper(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        displayDatabaseInfo();
    }

    private void displayDatabaseInfo() {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String[] databaseProjection = {
                StockEntry._ID,
                StockEntry.COLUMN_ITEM_NAME,
                StockEntry.COLUMN_ITEM_PRICE,
                StockEntry.COLUMN_ITEM_QUANTITY,
                StockEntry.COLUMN_SUPPLIER_NAME,
                StockEntry.COLUMN_SUPPLIER_PHONE
        };

        Cursor cursor = db.query(
                StockEntry.TABLE_NAME,
                databaseProjection,
                null,
                null,
                null,
                null,
                null,
                null);

        TextView displayView = findViewById(R.id.stock_text_view);

        try {
            displayView.setText("This stock table contains " + cursor.getCount() + " products.\n\n");
            displayView.append(StockEntry._ID + " - " +
            StockEntry.COLUMN_ITEM_NAME + " - " +
            StockEntry.COLUMN_ITEM_QUANTITY + " - " +
            StockEntry.COLUMN_ITEM_PRICE + " - " +
            StockEntry.COLUMN_SUPPLIER_NAME + " - " +
            StockEntry.COLUMN_SUPPLIER_PHONE + "\n");

            int idColumnIndex = cursor.getColumnIndex(StockEntry._ID);
            int nameColumnIndex = cursor.getColumnIndex(StockEntry.COLUMN_ITEM_NAME);
            int priceColumnIndex = cursor.getColumnIndex(StockEntry.COLUMN_ITEM_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(StockEntry.COLUMN_ITEM_QUANTITY);
            int supplierColumnIndex = cursor.getColumnIndex(StockEntry.COLUMN_SUPPLIER_NAME);
            int phoneColumnIndex = cursor.getColumnIndex(StockEntry.COLUMN_SUPPLIER_PHONE);

            while (cursor.moveToNext()) {

                int currentId = cursor.getInt(idColumnIndex);
                String currentName = cursor.getString(nameColumnIndex);
                int currentPrice = cursor.getInt(priceColumnIndex);
                int currentQuantity = cursor.getInt(quantityColumnIndex);
                String currentSupplier = cursor.getString(supplierColumnIndex);
                String currentPhone = cursor.getString(phoneColumnIndex);

                displayView.append("\n" + currentId + " - " +
                    currentName + " - " +
                    currentPrice + " - " +
                    currentQuantity + " - " +
                    currentSupplier + " - " +
                    currentPhone);
                Log.i(LOG_TAG, "this row in the database is" + displayView.getText());
            }
        } finally {
            cursor.close();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_stock, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_dummy_data:
                addSampleItem();
                displayDatabaseInfo();
                return true;
            case R.id.action_delete_all_items:
                // TODO code to delete all items
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // method to add dummy data to demonstrate database
    private void addSampleItem() {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(StockEntry.COLUMN_ITEM_NAME, "Sourdough Loaf");
        values.put(StockEntry.COLUMN_ITEM_PRICE, 2);
        values.put(StockEntry.COLUMN_ITEM_QUANTITY, 5);
        values.put(StockEntry.COLUMN_SUPPLIER_NAME, "Blinky's Bakery");
        values.put(StockEntry.COLUMN_SUPPLIER_PHONE, "647-884-5494");

        long newRowId = db.insert(StockEntry.TABLE_NAME, null, values);
    }
}
