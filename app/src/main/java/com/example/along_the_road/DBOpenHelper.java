package com.example.along_the_road;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.drawable.Drawable;
import androidx.annotation.Nullable;

public class DBOpenHelper {

    private static final String TAG = "DBOpenHelper";

    private static final String DATABASE_NAME = "UserDB.db";
    private static final int DATABASE_VERSION = 1;
    public static SQLiteDatabase mDB;
    private DatabaseHelper mDBHelper;
    private Context mCtx;

    private Drawable image;

    private class DatabaseHelper extends SQLiteOpenHelper {

//        public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
//            super(context, name, factory, version);
//        }

        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(Databases.UserDB.SQL_CREATE_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL(Databases.UserDB.SQL_DROP_TABLE);
            onCreate(db);
        }

        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            onUpgrade(db, oldVersion, newVersion);
        }

    }

    public DBOpenHelper(Context context) {
        this.mCtx = context;
    }

    public DBOpenHelper open() throws SQLException {
        mDBHelper = new DatabaseHelper(mCtx);
        mDB = mDBHelper.getWritableDatabase();
        return this;
    }

    public void create() {
        mDBHelper.onCreate(mDB);
    }

    public void drop() {
        mDB.execSQL("DROP TABLE IF EXISTS " + Databases.UserDB.TABLE_NAME + ";");
    }

    public void close() {
        mDB.close();
    }

    public long insertColumn(String uid, String email, String name) {
        SQLiteDatabase db = mDBHelper.getReadableDatabase();

        ContentValues values = new ContentValues();
        values.put(Databases.UserDB.UID, uid);
        values.put(Databases.UserDB.EMAIL, email);
        values.put(Databases.UserDB.NAME, name);
        // SQLite 이미지 저장 코드 추가 필요

        return mDB.insert(Databases.UserDB.TABLE_NAME, null, values);
    }

    public Cursor selectColumns() {
        return mDB.query(Databases.UserDB.TABLE_NAME, null, null, null, null, null, null);
    }

//    public boolean updateColumn(long id, String title, String content, @Nullable Drawable image) {
//        ContentValues values = new ContentValues();
//        values.put(Databases.UserDB.TITLE, title);
//        values.put(Databases.UserDB.CONTENT, content);
//
//        if(image != null) {
//            values.put(Databases.UserDB.IMAGE, "Y");
//            updateImageColumns(id, image);
//        }
//
//        return mDB.update(Databases.UserDB.TABLE_NAME, values, "_id=" + id, null) > 0;
//    }

    public void deleteAllColumns() {
        mDB.delete(Databases.UserDB.TABLE_NAME, null, null);
    }

    // Delete Column
    public boolean deleteColumn(String uid) {
        return mDB.delete(Databases.UserDB.TABLE_NAME, "uid='" + uid + "'", null) > 0;
    }

    public Cursor selectColumn(String uid) {
        Cursor c = mDB.rawQuery("SELECT name, email FROM " + Databases.UserDB.TABLE_NAME + " WHERE uid='" + uid + "';", null);
        return c;
    }

}
