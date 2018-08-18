package com.example.rachael.inventoryapp;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.ConditionVariable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.example.rachael.inventoryapp.data.StockContract;
import com.example.rachael.inventoryapp.data.StockContract.StockEntry;
import com.example.rachael.inventoryapp.data.StockDbHelper;

public class EditorActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>{

    // EditText fields for all different product attributes
    private EditText mNameEditText;
    private EditText mPriceEditText;
    private EditText mQuantityEditText;
    private EditText mSupplierEditText;
    private EditText mTelephoneEditText;

    private Uri mCurrentProductUri;

    private String LOG_TAG = EditorActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        Intent intent = getIntent();
        mCurrentProductUri = intent.getData();

        if (mCurrentProductUri == null) {
            setTitle(getString(R.string.editor_title_new_product));
            invalidateOptionsMenu();
            Log.e(LOG_TAG, "New Product Screen Success");
        } else {
            setTitle(getString(R.string.editor_title_existing_product));
            Log.e(LOG_TAG, "Update product screen success");
        }

        // find all the views that will need to be read when checking user input
        mNameEditText = findViewById(R.id.edit_product_name);
        mPriceEditText = findViewById(R.id.edit_stock_price);
        mQuantityEditText = findViewById(R.id.edit_stock_quantity);
        mSupplierEditText = findViewById(R.id.edit_supplier_name);
        mTelephoneEditText = findViewById(R.id.edit_supplier_phone_num);
    }

    // inflate overflow menu options
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    // actions when menu options selected
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_save_product:
                saveProduct();
                finish();
                return true;
            case R.id.action_delete_product:
                // TODO add functionality
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // get user input from EditText fields and save into the database
    private void saveProduct() {
        // read from the EditText views
        String nameString = mNameEditText.getText().toString().trim();
        String priceString = mPriceEditText.getText().toString().trim();
        String quantityString = mQuantityEditText.getText().toString().trim();
        String supplierString = mSupplierEditText.getText().toString().trim();
        String telephoneString = mTelephoneEditText.getText().toString().trim();

        // create a ContentValues object
        // column names = keys, EditText = values
        ContentValues values = new ContentValues();
        values.put(StockEntry.COLUMN_ITEM_NAME, nameString);
        values.put(StockEntry.COLUMN_ITEM_PRICE, priceString);
        values.put(StockEntry.COLUMN_ITEM_QUANTITY, quantityString);
        values.put(StockEntry.COLUMN_SUPPLIER_NAME, supplierString);
        values.put(StockEntry.COLUMN_SUPPLIER_PHONE, telephoneString);

        Uri newUri = getContentResolver().insert(StockEntry.CONTENT_URI, values);

        // toast to inform user if action successful for not
        if (newUri == null) {
            Toast.makeText(this, getString(R.string.editor_insert_product_failed),
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, getString(R.string.editor_insert_product_success),
                    Toast.LENGTH_SHORT).show();
        }
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

    }
}
