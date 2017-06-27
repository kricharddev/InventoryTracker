package com.example.android.inventorytracker.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.inventorytracker.data.ProductContract.*;

/**
 * Created by kyle on 5/1/17.
 */

public class InvDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "inventory.db";

    private static final int DATABASE_VERSION = 6;

    public InvDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_PRODUCTS_TABLE = ("CREATE TABLE " + ProductEntry.TABLE_NAME + " ("
                + ProductEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ProductEntry.COLUMN_PRODUCT_NAME + " TEXT NOT NULL, "
                + ProductEntry.COLUMN_QTY_AVAILABLE + " INTEGER NOT NULL, "
                + ProductEntry.COLUMN_PRODUCT_PRICE + " TEXT NOT NULL, "
                + ProductEntry.COLUMN_PRODUCT_INFO + " TEXT NOT NULL, "
                + ProductEntry.COLUMN_PRODUCT_EMAIL + " TEXT NOT NULL, "
                + ProductEntry.COLUMN_PRODUCT_IMAGE + " TEXT" + ")");

        db.execSQL(SQL_CREATE_PRODUCTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // If need to update table, put here and update DATABASE_VERSION
    }
}