package com.example.rachael.inventoryapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import static com.example.rachael.inventoryapp.data.StockContract.StockEntry;

public class StockProvider extends ContentProvider {

    public static final String LOG_TAG = StockProvider.class.getSimpleName();

    // URI matcher codes
    private static final int STOCK = 100; //for stock table
    private static final int STOCK_ID = 101; //for individual product

    // creates a uri matcher object
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        // uri code for content://com.example.rachael.inventoryapp/stock
        sUriMatcher.addURI(StockContract.CONTENT_AUTHORITY, StockContract.PATH_STOCK, STOCK);

        // uri code for content://com.example.rachael.inventoryapp/stock/#
        // in which # is substituted for a row number
        sUriMatcher.addURI(StockContract.CONTENT_AUTHORITY, StockContract.PATH_STOCK + "/#", STOCK_ID);
    }

    private StockDbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new StockDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        Cursor cursor;

        int match = sUriMatcher.match(uri);
        switch (match) {
            case STOCK:
                // query the table directly
                cursor = database.query(StockEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case STOCK_ID:
                // extract the required id from the uri
                selection = StockEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                // query the table for the row at that specific id
                cursor = database.query(StockEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case STOCK:
                return StockEntry.CONTENT_LIST_TYPE;
            case STOCK_ID:
                return StockEntry.CONTENT_ITEM_BASE_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case STOCK:
                return insertProduct(uri, values);
            default:
                throw new IllegalArgumentException("Insertion not possible for " + uri);
        }
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int rowsDeleted;

        final int match = sUriMatcher.match(uri);

        switch(match) {
            case STOCK:
                // delete all rows that match the given selection and selection arguments
                rowsDeleted = database.delete(StockEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case STOCK_ID:
                // delete a single row from the database of a given id
                selection = StockEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri)) };
                rowsDeleted = database.delete(StockEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not possible for " + uri);
        }
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case STOCK:
                return updateProduct(uri, values, selection, selectionArgs);
            case STOCK_ID:
                selection = StockEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updateProduct(uri, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not possible for " + uri);
        }
        }



    /**
     * Method to add a product to the database, with values provided by the user
     */
    private Uri insertProduct(Uri uri, ContentValues values) {
        // check that values provided by the user are valid
        String name = values.getAsString(StockEntry.COLUMN_ITEM_NAME);
        if (name == null) {
            throw new IllegalArgumentException("Product name is required field");
        }
        Integer price = values.getAsInteger(StockEntry.COLUMN_ITEM_PRICE);
        if (price == null) {
            throw new IllegalArgumentException("Produce price is required field");
        }
        Integer quantity = values.getAsInteger(StockEntry.COLUMN_ITEM_QUANTITY);
        if (quantity == null) {
            throw new IllegalArgumentException("Product quantity is required field");
        }
        String supplier = values.getAsString(StockEntry.COLUMN_SUPPLIER_NAME);
        if (supplier == null) {
            throw new IllegalArgumentException("Supplier's name is required field");
        }
        String phone = values.getAsString(StockEntry.COLUMN_SUPPLIER_PHONE);
        if (phone == null) {
            throw new IllegalArgumentException("Supplier's phone number is required field");
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        long id = database.insert(StockEntry.TABLE_NAME, null, values);
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri + " to database");
            return null;
        }
        return ContentUris.withAppendedId(uri, id);
    }

    /**
     * Update products in the database with values provided
     * Returns the number of rows that were updated
     */
    private int updateProduct(Uri uri, ContentValues values, String selection,
                              String[] selectionArgs) {
        // check that any values presented are valid and not null
        if (values.containsKey(StockEntry.COLUMN_ITEM_NAME)) {
            String name = values.getAsString(StockEntry.COLUMN_ITEM_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Product name required");
            }
        }
        if (values.containsKey(StockEntry.COLUMN_ITEM_PRICE)) {
            Integer price = values.getAsInteger(StockEntry.COLUMN_ITEM_PRICE);
            if (price == null) {
                throw new IllegalArgumentException("Product price required");
            }
        }
        if (values.containsKey(StockEntry.COLUMN_ITEM_QUANTITY)) {
            Integer quantity = values.getAsInteger(StockEntry.COLUMN_ITEM_QUANTITY);
            if (quantity == null) {
                throw new IllegalArgumentException("Product quantity required");
            }
        }
        if (values.containsKey(StockEntry.COLUMN_SUPPLIER_NAME)) {
            String supplier = values.getAsString(StockEntry.COLUMN_SUPPLIER_NAME);
            if (supplier == null) {
                throw new IllegalArgumentException("Supplier's name required");
            }
        }
        if (values.containsKey(StockEntry.COLUMN_SUPPLIER_PHONE)) {
            String telephone = values.getAsString(StockEntry.COLUMN_SUPPLIER_PHONE);
            if (telephone == null) {
                throw new IllegalArgumentException("Supplier's phone number required");
            }
        }

        // check if there are any values that need to be updated, if not don't try to
        if (values.size() == 0) {
            return 0;
        }
        // update the database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int rowsUpdated = database.update(StockEntry.TABLE_NAME, values, selection, selectionArgs);
        return rowsUpdated;
    }

}
