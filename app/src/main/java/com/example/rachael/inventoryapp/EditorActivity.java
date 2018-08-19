package com.example.rachael.inventoryapp;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.rachael.inventoryapp.data.StockContract.StockEntry;

public class EditorActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    // EditText fields for all different product attributes
    private EditText mNameEditText;
    private EditText mPriceEditText;
    private EditText mQuantityEditText;
    private EditText mSupplierEditText;
    private EditText mTelephoneEditText;

    private static final int EXISTING_PRODUCT_LOADER = 0;
    private Uri mCurrentProductUri;
    private boolean mProductHasChanged = false;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mProductHasChanged = true;
            return false;
        }
    };


    private String LOG_TAG = EditorActivity.class.getSimpleName();

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        Intent intent = getIntent();
        mCurrentProductUri = intent.getData();
        final Button callSupplierButton = findViewById(R.id.action_call_supplier);
        final ImageButton increaseQuantityButton = findViewById(R.id.action_quantity_increase);
        final ImageButton decreaseQuantityButton = findViewById(R.id.action_quantity_decrease);


        if (mCurrentProductUri == null) {
            setTitle(getString(R.string.editor_title_new_product));
            invalidateOptionsMenu();
            // make buttons invisible if adding new product
            callSupplierButton.setVisibility(View.GONE);
            increaseQuantityButton.setVisibility(View.GONE);
            decreaseQuantityButton.setVisibility(View.GONE);

        } else {
            setTitle(getString(R.string.editor_title_existing_product));

            getSupportLoaderManager().initLoader(EXISTING_PRODUCT_LOADER, null, this);
        }

        // find all the views that will need to be read when checking user input
        mNameEditText = findViewById(R.id.edit_product_name);
        mPriceEditText = findViewById(R.id.edit_stock_price);
        mQuantityEditText = findViewById(R.id.edit_stock_quantity);
        mSupplierEditText = findViewById(R.id.edit_supplier_name);
        mTelephoneEditText = findViewById(R.id.edit_supplier_phone_num);

        // check if any fields have been changed
        mNameEditText.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);
        mQuantityEditText.setOnTouchListener(mTouchListener);
        mSupplierEditText.setOnTouchListener(mTouchListener);
        mTelephoneEditText.setOnTouchListener(mTouchListener);

        // set onClickListeners for various buttons
        callSupplierButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callSupplier();
            }
        });
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (mCurrentProductUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete_product);
            menuItem.setVisible(false);
        }
        return true;
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
        switch (item.getItemId()) {
            case R.id.action_save_product:
                checkProduct();
                return true;
            case R.id.action_delete_product:
                showDeleteConfirmationDialog();
                return true;
            case android.R.id.home:
                if (!mProductHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }
                //if user has unsaved changes, alert them
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };
                showUnsavedChangesAlert(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void checkProduct() {

        String nameString = mNameEditText.getText().toString().trim();
        String priceString = mPriceEditText.getText().toString().trim();
        String supplierString = mSupplierEditText.getText().toString().trim();
        String telephoneString = mTelephoneEditText.getText().toString().trim();

        if (mCurrentProductUri == null && TextUtils.isEmpty(nameString) ||
                TextUtils.isEmpty(priceString) || TextUtils.isEmpty(supplierString) ||
                TextUtils.isEmpty(telephoneString)) {
            Toast.makeText(this, getString(R.string.editor_required_fields_warning),
                    Toast.LENGTH_SHORT).show();
        } else {
            saveProduct();
            finish();
        }
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
        // if no value has been provided for the quantity, use 0 as default
        int quantity = 0;
        if (!TextUtils.isEmpty(quantityString)) {
            quantity = Integer.parseInt(quantityString);
        }
        values.put(StockEntry.COLUMN_ITEM_QUANTITY, quantity);
        values.put(StockEntry.COLUMN_SUPPLIER_NAME, supplierString);
        values.put(StockEntry.COLUMN_SUPPLIER_PHONE, telephoneString);

        // toast to inform user if action successful for not
        if (mCurrentProductUri == null) {

            Uri newUri = getContentResolver().insert(StockEntry.CONTENT_URI, values);

            if (newUri == null) {
                Toast.makeText(this, getString(R.string.editor_insert_product_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_insert_product_success),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            int rowsAffected = getContentResolver().update(mCurrentProductUri, values, null, null);

            if (rowsAffected == 0) {
                Toast.makeText(this, getString(R.string.editor_update_product_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_update_product_success),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void deleteProduct() {
        if (mCurrentProductUri != null) {
            int rowsDeleted = getContentResolver().delete(mCurrentProductUri, null, null);

            if (rowsDeleted == 0) {
                Toast.makeText(this, getString(R.string.editor_delete_product_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_delete_product_success),
                        Toast.LENGTH_SHORT).show();
            }
            finish();
        }
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        String[] projection = {
                StockEntry._ID,
                StockEntry.COLUMN_ITEM_NAME,
                StockEntry.COLUMN_ITEM_PRICE,
                StockEntry.COLUMN_ITEM_QUANTITY,
                StockEntry.COLUMN_SUPPLIER_NAME,
                StockEntry.COLUMN_SUPPLIER_PHONE
        };

        return new CursorLoader(this,
                mCurrentProductUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {

        if (data == null || data.getCount() < 1) {
            return;
        }

        if (data.moveToFirst()) {
            int nameColumnIndex = data.getColumnIndex(StockEntry.COLUMN_ITEM_NAME);
            int priceColumnIndex = data.getColumnIndex(StockEntry.COLUMN_ITEM_PRICE);
            int quantityColumnIndex = data.getColumnIndex(StockEntry.COLUMN_ITEM_QUANTITY);
            int supplierColumnIndex = data.getColumnIndex(StockEntry.COLUMN_SUPPLIER_NAME);
            int telephoneColumnIndex = data.getColumnIndex(StockEntry.COLUMN_SUPPLIER_PHONE);

            String name = data.getString(nameColumnIndex);
            int price = data.getInt(priceColumnIndex);
            int quantity = data.getInt(quantityColumnIndex);
            String supplier = data.getString(supplierColumnIndex);
            String telephone = data.getString(telephoneColumnIndex);

            mNameEditText.setText(name);
            mPriceEditText.setText(Integer.toString(price));
            mQuantityEditText.setText(Integer.toString(quantity));
            mSupplierEditText.setText(supplier);
            mTelephoneEditText.setText(telephone);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

        mNameEditText.setText("");
        mPriceEditText.setText("");
        mQuantityEditText.setText("");
        mSupplierEditText.setText("");
        mTelephoneEditText.setText("");
    }

    // Alert that the user is shown when trying to leave activity with unsaved changes
    private void showUnsavedChangesAlert(DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.dialog_unsaved_changes);
        builder.setPositiveButton(R.string.dialog_discard_changes, discardButtonClickListener);
        builder.setNegativeButton(R.string.dialog_keep_editing, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    //ask for user confirmation before deleting product from database
    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.dialog_delete_changes);
        // user selects delete option
        builder.setPositiveButton(R.string.dialog_delete_confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteProduct();
            }
        });
        builder.setNegativeButton(R.string.dialog_delete_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void callSupplier() {
    }

}
