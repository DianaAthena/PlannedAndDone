package com.pnd.future_bosses.plannedanddone;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;
import android.util.StringBuilderPrinter;

public class DataBase extends ContentProvider {

    public static final String PROVIDER_NAME =
            "hr.math.provider.contprov";
    public static final Uri TASK_URI =
            Uri.parse("content://"+ PROVIDER_NAME + "/task");

    public static final Uri CATEGORY_URI =
            Uri.parse("content://"+ PROVIDER_NAME + "/category");

    static final String TASK_ID = "_id";
    static final String TASK_NAME = "name";
    static final String TASK_TIME = "time";
    static final String TASK_DEADLINE = "deadline";
    static final String TASK_PRIORITY = "priority";
    static final String TASK_CATEGORY = "category";
    static final String TASK_DONE = "done";

    static final String CATEGORY_ID= "_id";
    static final String CATEGORY_NAME= "name";

    static final int TASK = 1;
    static final int CATEGORY = 2;


    private static final UriMatcher uriMatcher;
    static{
        // nejasno što ovo znaci
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, "task", TASK);
        uriMatcher.addURI(PROVIDER_NAME, "category", CATEGORY);
    }

    SQLiteDatabase db;
    static final String DATABASE_NAME = "DB";
    static final String TASK_TABLE = "task";
    static final String CATEGORY_TABLE = "category";

    static final int DATABASE_VERSION = 8;

    static final String TASK_CREATE =
            "create table task (_id integer primary key autoincrement, "
                    + "name text not null, time text, deadline text, priority integer, category integer, done integer);";

    static final String CATEGORY_CREATE ="create table category (_id integer primary key autoincrement, "
            + "name text not null);";

    private static class DatabaseHelper extends SQLiteOpenHelper
    {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db)
        {
            try {
                db.execSQL(TASK_CREATE);
                db.execSQL(CATEGORY_CREATE);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion,
                              int newVersion) {
            Log.w("CONTPROVDB", "Upgrading from " + oldVersion + " to " + newVersion);



            db.execSQL("DROP TABLE IF EXISTS task");
            db.execSQL("DROP TABLE IF EXISTS category");
            onCreate(db);
        }
    }


    @Override
    public int delete(Uri table, String where, String[] arg2) {

        int count=0;
        switch (uriMatcher.match(table)){
            //ovi case-evi mi nisu bas najjasniji
            case TASK:
                Log.e("Unutra sam", "delete ");
                count = db.delete(
                        TASK_TABLE,
                        where,
                        arg2);
                break;
            case CATEGORY:
                count = db.delete(
                        CATEGORY_TABLE,
                        where,
                        arg2);
                break;
            default: throw new IllegalArgumentException("Unknown URI " + table);
        }
        getContext().getContentResolver().notifyChange(table, null);
        return count;
    }

    @Override
    public String getType(Uri table) {
        // Cemu ovo sluzi!?
        switch (uriMatcher.match(table)){
            //---get all books---
            case TASK:
                return "vnd.android.cursor.dir/vnd.math.task ";
            //---get a particular book---
            case CATEGORY:
                return "vnd.android.cursor.item/vnd.math.category ";

            default:
                throw new IllegalArgumentException("Unsupported URI: " + table);
        }
    }

    @Override
    public Uri insert(Uri table, ContentValues values) {

        long rowID;
        switch (uriMatcher.match(table)){
            case TASK:
                rowID = db.insert(TASK_TABLE, "", values);
                if (rowID>0)
                {
                    Uri _uri = ContentUris.withAppendedId(TASK_URI, rowID);
                    getContext().getContentResolver().notifyChange(_uri, null);
                    return _uri;
                }
                throw new SQLException("Failed to insert row into " + table);
            case CATEGORY:
                rowID = db.insert(CATEGORY_TABLE, "", values);
                if (rowID>0)
                {
                    Uri _uri = ContentUris.withAppendedId(CATEGORY_URI, rowID);
                    getContext().getContentResolver().notifyChange(_uri, null);
                    return _uri;
                }
                throw new SQLException("Failed to insert row into " + table);
            default:
                throw new IllegalArgumentException("Unsupported URI: " + table);
        }
    }

    @Override
    public boolean onCreate() {

        Context context = getContext();
        DataBase.DatabaseHelper dbHelper = new DataBase.DatabaseHelper(context);
        db = dbHelper.getWritableDatabase();

        return (db == null)? false:true;
    }

    @Override
    public Cursor query(Uri table, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        SQLiteQueryBuilder sqlBuilder = new SQLiteQueryBuilder();

        switch (uriMatcher.match(table)){
            case TASK:
                sqlBuilder.setTables(TASK_TABLE);
                if (sortOrder==null || sortOrder=="")
                    sortOrder = TASK_NAME;
                break;
            case CATEGORY:
                sqlBuilder.setTables(CATEGORY_TABLE);
                if (sortOrder==null || sortOrder=="")
                    sortOrder = CATEGORY_NAME;
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + table);
        }

        Cursor c = sqlBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        c.setNotificationUri(getContext().getContentResolver(), table);
        return c;
    }

    @Override
    public int update(Uri table, ContentValues values, String selection,
                      String[] selectionArgs) {

        int count = 0;
        switch (uriMatcher.match(table)){
            case TASK:
                count = db.update(
                        TASK_TABLE,
                        values,
                        selection,
                        selectionArgs);
                break;
            case CATEGORY:
                count = db.update(
                        CATEGORY_TABLE,
                        values,
                        selection,
                        selectionArgs);
                break;
            default: throw new IllegalArgumentException("Unknown URI " + table);
        }
        getContext().getContentResolver().notifyChange(table, null);
        return count;
    }

}
