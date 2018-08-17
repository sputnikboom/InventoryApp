package com.example.rachael.inventoryapp;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.example.rachael.inventoryapp.data.StockContract.StockEntry;
import com.example.rachael.inventoryapp.data.StockDbHelper;

public class StockActivity extends AppCompatActivity {

    public static final String LOG_TAG = StockActivity.class.getSimpleName();

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

        ListView stockListView = findViewById(R.id.list);

        View emptyView = findViewById(R.id.empty_text_view);
        stockListView.setEmptyView(emptyView);

    }

    @Override
    protected void onStart() {
        super.onStart();
        displayDatabaseInfo();
    }

    private void displayDatabaseInfo() {

        String[] databaseProjection = {
                StockEntry._ID,
                StockEntry.COLUMN_ITEM_NAME,
                StockEntry.COLUMN_ITEM_PRICE,
                StockEntry.COLUMN_ITEM_QUANTITY,
                StockEntry.COLUMN_SUPPLIER_NAME,
                StockEntry.COLUMN_SUPPLIER_PHONE
        };

        Cursor cursor = getContentResolver().query(
                StockEntry.CONTENT_URI,
                databaseProjection,
                null,
                null,
                null);

        ListView stockListView = findViewById(R.id.list);
        StockCursorAdapter adapter = new StockCursorAdapter(this, cursor);
        stockListView.setAdapter(adapter);
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

        ContentValues values = new ContentValues();
        values.put(StockEntry.COLUMN_ITEM_NAME, "Sourdough Loaf");
        values.put(StockEntry.COLUMN_ITEM_PRICE, 2);
        values.put(StockEntry.COLUMN_ITEM_QUANTITY, 5);
        values.put(StockEntry.COLUMN_SUPPLIER_NAME, "Blinky's Bakery");
        values.put(StockEntry.COLUMN_SUPPLIER_PHONE, "647-884-5494");

        Uri newUri = getContentResolver().insert(StockEntry.CONTENT_URI, values);
        if (newUri == null) {
            throw new IllegalArgumentException("Error adding sample product to database");
        }
    }
}
