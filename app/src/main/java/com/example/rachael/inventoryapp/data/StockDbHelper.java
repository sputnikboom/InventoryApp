package com.example.rachael.inventoryapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.rachael.inventoryapp.data.StockContract.StockEntry;


public class StockDbHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = StockDbHelper.class.getSimpleName();

    private static final String DATABASE_NAME = "store.db";
    private static final int DATABASE_VERSION = 1;

    /**
     * construct new instance of {@link StockDbHelper}
     */
    public StockDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * function called when our database is created for the very first time
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_STOCK_TABLE = "CREATE TABLE " + StockEntry.TABLE_NAME + " ("
                + StockEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + StockEntry.COLUMN_ITEM_NAME + " TEXT NOT NULL, "
                + StockEntry.COLUMN_ITEM_PRICE + " INTEGER NOT NULL, "
                + StockEntry.COLUMN_ITEM_QUANTITY + " INTEGER NOT NULL DEFAULT 0, "
                + StockEntry.COLUMN_SUPPLIER_NAME + " TEXT NOT NULL, "
                + StockEntry.COLUMN_SUPPLIER_PHONE + " TEXT NOT NULL);";

        // log to make sure sql creating sting is being properly created
        Log.i(LOG_TAG, "SQL string created is " + SQL_CREATE_STOCK_TABLE);
        db.execSQL(SQL_CREATE_STOCK_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //still at version 1
    }
}
