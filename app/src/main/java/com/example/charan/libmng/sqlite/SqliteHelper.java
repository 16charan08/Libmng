package com.example.charan.libmng.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.TableLayout;

public class SqliteHelper extends SQLiteOpenHelper {
    // Constants
    public static final String DatabaseName = " LibMng.db";
    public static final String TableName = "Books";
    public static final String Col_1 = "BookID";
    public static final String Col_2 = "BookDetails";
    public static final String Col_3 = "ToReadPrority";
    public static final String Col_4 = "ToReadStatus";
    public static final String Col_5 = "BookNotes";
    private static final String TAG = SqliteHelper.class.getSimpleName();
    private SQLiteDatabase mWritableDB;
    private SQLiteDatabase mReadableDB;

    public SqliteHelper(Context context) {
        super(context, DatabaseName, null, 1);

    }
    private static final String[] COLUMNS =
            {Col_1, Col_2};

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableQuery = "create table if not exists " + TableName + " ( BookID INTEGER PRIMARY KEY AUTOINCREMENT, BookDetails TEXT, ToReadPrority TEXT, ToReadStatus TEXT,BookNotes TEXT)";
        db.execSQL(createTableQuery);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TableName);
        onCreate(db);
    }


    public boolean insertInto(ContentValues cv) {
        SQLiteDatabase db = this.getWritableDatabase();
        long results = db.insert(TableName, null, cv);
        if (results == -1) {
            return false;
        } else {
            return true;
        }
    }
    public Cursor search(String searchString) {
        String[] columns = new String[]{Col_2,Col_5};
        String where =  Col_2 + " LIKE ?";
        searchString = "%" + searchString + "%";
        String[] whereArgs = new String[]{searchString};

        Cursor cursor = null;
        try {
            if (mReadableDB == null) {
                mReadableDB = getReadableDatabase();
            }
            cursor = mReadableDB.query(TableName, columns, where, whereArgs, null, null, null);
        } catch (Exception e) {
            Log.d(TAG, "SEARCH EXCEPTION! " + e); // Just log the exception
        }
        return cursor;
    }

    public Cursor selectAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "select * from " + TableName;
        Cursor result = db.rawQuery(query, null);
        return result;
    }


    public Cursor updateTask(com.example.charan.libmng.modal.LibData td) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE "
                + TableName
                + " SET "
                + Col_2 + "='" + td.getBookDetails()
                + "', "
                + Col_3 + "='" + td.getToReadPrority()
                + "', "
                + Col_4 + "='" + td.getToReadStatus()
                + "', "
                + Col_5 + "='" + td.getBookNotes()
                + "' WHERE " + Col_1 + "='" + td.getBookID() + "'";
        Cursor results = db.rawQuery(query, null);
        return results;
    }


    public Cursor deleteTask(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + TableName
                + " WHERE "
                + Col_1 + "='"
                + id + "'";
        Cursor result = db.rawQuery(query, null);
        return result;
    }
}
