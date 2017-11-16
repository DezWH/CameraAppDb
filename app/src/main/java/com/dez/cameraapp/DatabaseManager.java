package com.dez.cameraapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v7.widget.ContentFrameLayout;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Dez on 11/8/2017.
 */

public class DatabaseManager {

    private Context context;
    private SQLHelper helper;
    private SQLiteDatabase db;

    protected static final String DB_NAME = "picture.db";
    protected static final String TABLE_NAME = "picture_table";
    protected static final String COL_ID = "_id";
    protected static final String COL_PICTURENAME = "picture_name";
    protected static final String COL_DATETAKEN = "date_taken";
    protected static final String COL_TAGS = "tags";

    private static final String DBTAG = "DatabaseManager";
    private static final String SQL_TAG = "SQLHelper";


    public DatabaseManager(Context c) {
        this.context = c;
        helper = new SQLHelper(c);
        this.db = helper.getWritableDatabase();


    }
    public void close()

    {
        helper.close(); //Closes the database - very important!
    }


    //Delete a product by name.
    // Return true if at least one row was deleted, false otherwise.
    public boolean deleteProduct(long pictureId) {
        String[] whereArgs = {Long.toString(pictureId)};
        String where = COL_ID + " = ?";
        int rowsDeleted = db.delete(DB_NAME, where, whereArgs);

        Log.i(DBTAG, "Delete " + pictureId + " rows deleted:" + rowsDeleted);

        if (rowsDeleted > 0) {
            return true;   //at least one row deleted
        }
        return false; //nothing deleted
    }


    //Method to update the quantity of a product.
    //Return false if no update is made, for example, product not found
    public boolean updateQuantity(String name, int newQuantity) {
        ContentValues updateProduct = new ContentValues();
        String[] whereArgs = {name};
        String where = COL_PICTURENAME + " = ?";

        int rowsChanged = db.update(DB_NAME, updateProduct, where, whereArgs);
        Log.i(DBTAG, "Update " + name + " new quantity " + newQuantity +
                " rows modified " + rowsChanged);

        if (rowsChanged > 0) {
            return true;    //If at least one row changed, an update was made.
        }
        return false;  //Otherwise, no rows changed. Return false to indicate.
    }


    //Add a product and quantity to the database.
    // Returns true if product added, false if product is already in the database
    public boolean addProduct(String name, int quantity) {
        ContentValues newPictures = new ContentValues();
        newPictures.put(COL_PICTURENAME, name);
        try {
            db.insertOrThrow(DB_NAME, null, newPictures);
            return true;

        } catch (SQLiteConstraintException sqlce) {
            Log.e(DBTAG, "error inserting data into table. " +
                    "Name:" + name + " quantity:" + quantity, sqlce);
            return false;
        }
    }

    //If you wanted to get a list of everything in the DB
    public ArrayList<Picture> fetchAllProducts() {

        ArrayList<Picture> pictures = new ArrayList<>();

        Cursor cursor = db.query(DB_NAME, null, null, null, null, null, COL_PICTURENAME);

        while (cursor.moveToNext())
        {
            int _id= cursor.getInt(0);
            String name= cursor.getString(1);
            Long date_taken= cursor.getLong(2);
            String tags = cursor.getString(3);
            Picture p = new Picture(name, _id,  date_taken, tags);
            pictures.add(p);
        }

        Log.i(DBTAG, pictures.toString());

        cursor.close();
        return pictures;
    }


    //Return a Cursor of all of the data in the database, sorted by NAME_COL.
    //This will be used by the CursorAdapter to provide data for the ListView.
    public Cursor getCursorAll() {

        Cursor cursor = db.query(DB_NAME, null, null, null, null, null, null);
        return cursor;
    }

    //Returns a quantity for a product, or -1 if product is not found in database
    //Not case sensitive
    public int getQuantityForProduct(String pictureName) {

        // This query is case sensitive. If you don't care about case, convert the search
        // query to uppercase and compare to the uppercase versions of the data in the database
        // select quantity from products where upper(product_name) = upper(productName)
        String selection = COL_PICTURENAME + " = ? ";
        String[] selectionArgs = {pictureName};

        Cursor cursor = db.query(DB_NAME, null, selection, selectionArgs, null, null, null, "1");

        if (cursor.getCount() == 1) {
            cursor.moveToFirst();
            int quantity = cursor.getInt(0);
            cursor.close();
            return quantity;
        } else {
            // 0 products found - the product is not in the database.
            // (Or more than one, which would indicate a problem with the design.
            // When the db was created, the product_name was configured to be unique.)
            return -1;    //todo - better way to indicate product not found?
        }
    }

    public class SQLHelper extends SQLiteOpenHelper {


        public SQLHelper(Context c) {
            super(context, DB_NAME, null, 1);

        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            String createTable = ("CREATE TABLE " + TABLE_NAME  + "TEXT," + "("+
                    COL_ID + "INTEGER," + "INTEGER PRIMARY KEY AUTOINCREMENT, " + COL_PICTURENAME + "TEXT," + COL_DATETAKEN + "INTEGER," + COL_TAGS + "TEXT,");
            Log.d(SQL_TAG, createTable);
            db.execSQL(createTable);

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
        }
    }
}