package com.example.rachael.inventoryapp;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
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
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.rachael.inventoryapp.data.StockContract.StockEntry;
import com.example.rachael.inventoryapp.data.StockDbHelper;

public class StockActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String LOG_TAG = StockActivity.class.getSimpleName();

    StockCursorAdapter mCursorAdapter;
    private static final int STOCK_LOADER = 0;

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

        mCursorAdapter = new StockCursorAdapter(this, null);
        stockListView.setAdapter(mCursorAdapter);

        stockListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(StockActivity.this, EditorActivity.class);
                Uri currentProductUri = ContentUris.withAppendedId(StockEntry.CONTENT_URI, id);
                intent.setData(currentProductUri);
                startActivity(intent);
            }
        });

        getLoaderManager().initLoader(STOCK_LOADER, null, this);
    }

    @Override
    protected void onStart() {
        super.onStart();
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

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                StockEntry._ID,
                StockEntry.COLUMN_ITEM_NAME,
                StockEntry.COLUMN_ITEM_PRICE,
                StockEntry.COLUMN_ITEM_QUANTITY
        };

        return new CursorLoader(this,
                StockEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }
}
