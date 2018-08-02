package com.example.rachael.inventoryapp.data;

import android.provider.BaseColumns;

public final class StockContract {

    private StockContract() {}

    /**
     * Inner class for constant values in the database table
     */
    public static final class StockEntry implements BaseColumns {

        public final static String TABLE_NAME = "stock";
        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_ITEM_NAME = "name";
        public final static String COLUMN_ITEM_PRICE = "price";
        public final static String COLUMN_ITEM_QUANTITY = "quantity";
        public final static String COLUMN_SUPPLIER_NAME = "supplier";
        public final static String COLUMN_SUPPLIER_PHONE = "phone";
    }

}
