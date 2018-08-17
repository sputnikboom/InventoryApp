package com.example.rachael.inventoryapp.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class StockContract {

    private StockContract() {}

    public final static String CONTENT_AUTHORITY = "com.example.rachael.inventoryapp";
    public final static Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public final static String PATH_STOCK = "stock";

    /**
     * Inner class for constant values in the database table
     */
    public static final class StockEntry implements BaseColumns {

        public final static Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_STOCK);
        public final static String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + PATH_STOCK;
        public final static String CONTENT_ITEM_BASE_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_STOCK;

        public final static String TABLE_NAME = "stock";
        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_ITEM_NAME = "name";
        public final static String COLUMN_ITEM_PRICE = "price";
        public final static String COLUMN_ITEM_QUANTITY = "quantity";
        public final static String COLUMN_SUPPLIER_NAME = "supplier";
        public final static String COLUMN_SUPPLIER_PHONE = "phone";
    }

}
