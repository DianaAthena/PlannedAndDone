package com.pnd.future_bosses.plannedanddone;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DBAdapter implements Serializable{
    // NOT IN USE
   /*
    static final String TASK_ID = "_id";
    static final String TASK_NAME = "name";
    static final String TASK_TIME = "time";
    static final String TASK_DEADLINE = "deadline";
    static final String TASK_PRIORITY = "priority";
    static final String TASK_CATEGORY = "category";
    static final String TASK_DONE = "done";
    static final String TASK_TABLE = "tasks";

    static final String CATEGORY_ID= "_id";
    static final String CATEGORY_NAME= "name";
    static final String CATEGORY_TABLE = "categories";



    static final String TASK_CREATE =
            "create table tasks (_id integer primary key autoincrement, "
                    + "name text not null, time text, deadline text, priority integer, category integer, done integer);";
    static final String CATEGORY_CREATE ="create table categories (_id integer primary key autoincrement, "
            + "name text not null);";

    static final String TAG = "DBAdapter";
    static final String DATABASE_NAME = "PD_DB";
    static final int DATABASE_VERSION = 5;


    final Context context;

    DatabaseHelper DBHelper;
    SQLiteDatabase db;

    public DBAdapter(Context ctx)
    {
        this.context = ctx;
        DBHelper = new DatabaseHelper(context);
    }

    private static class DatabaseHelper extends SQLiteOpenHelper
    {
        DatabaseHelper(Context context)
        {
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
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
        {
            Log.w(TAG, "Upgrading db from" + oldVersion + "to"
                    + newVersion );
            db.execSQL("DROP TABLE IF EXISTS tasks");
            db.execSQL("DROP TABLE IF EXISTS categories");
            onCreate(db);
        }
    }

    //otvori bazu
    public DBAdapter open() throws SQLException
    {
        db = DBHelper.getWritableDatabase();
        return this;
    }

    //zatvori bazu
    public void close()
    {
        DBHelper.close();
    }

    //dodavanje novog zadatka
    public long insertTask(String name, String time, String deadline, int priority, int category, int done)
    {
        ContentValues initialValues = new ContentValues();
        initialValues.put(TASK_NAME, name);
        initialValues.put(TASK_TIME, time);
        initialValues.put(TASK_DEADLINE, deadline);
        initialValues.put(TASK_PRIORITY, priority);
        initialValues.put(TASK_CATEGORY, category);
        initialValues.put(TASK_DONE, done);

        return db.insert(TASK_TABLE, null, initialValues);
    }

    //dodavanje nove kategorije
    public long insertCategory(String name){

        ContentValues initialValues = new ContentValues();
        initialValues.put(CATEGORY_NAME, name);
        return db.insert(CATEGORY_TABLE, null, initialValues);

    }


    //brisanje zadatka
    public boolean deleteTask(long rowId)
    {
        return db.delete(TASK_TABLE, TASK_ID + "=" + rowId, null) > 0;

    }

    //brisanje kategorija
    public boolean deleteCategory(long rowId)
    {
        return db.delete(CATEGORY_TABLE, CATEGORY_ID + "=" + rowId, null) > 0;

    }

    //dohvati sve zadatke
    public Cursor getAllTasks()
    {
        return db.query(TASK_TABLE, new String[] {TASK_ID, TASK_NAME,
                TASK_TIME, TASK_DEADLINE, TASK_PRIORITY,TASK_CATEGORY, TASK_DONE}, null, null, null, null, null);
    }

    //dohvati sve kategorije
    public Cursor getAllCategories()
    {
        return db.query(CATEGORY_TABLE, new String[] {CATEGORY_ID, CATEGORY_NAME}, null, null, null, null, null);
    }

    //dohvati zadatak
    public Cursor getTask(long rowId) throws SQLException
    {
        Cursor mCursor =
                db.query(true, TASK_TABLE, new String[] {TASK_ID, TASK_NAME,
                                TASK_TIME, TASK_DEADLINE, TASK_PRIORITY,TASK_CATEGORY, TASK_DONE}, TASK_ID + "=" + rowId, null,
                        null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    //dohvati kategorija
    public Cursor getCategory(long rowId) throws SQLException
    {
        Cursor mCursor =
                db.query(true, CATEGORY_TABLE, new String[] {CATEGORY_ID, CATEGORY_NAME}, CATEGORY_ID + "=" + rowId, null,
                        null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    //uredi zadatak
    public boolean updateTask(long rowId,String name, String time, String deadline, int priority, int category, int done)
    {
        ContentValues update = new ContentValues();
        update.put(TASK_NAME, name);
        update.put(TASK_TIME, time);
        update.put(TASK_DEADLINE, deadline);
        update.put(TASK_PRIORITY, priority);
        update.put(TASK_CATEGORY, category);
        update.put(TASK_DONE, done);
        return db.update(TASK_TABLE, update, TASK_ID + "=" + rowId, null) > 0;
    }


    //FILTRI

    //po kategorijama
    public Cursor filterTaskByCategory(long idCategory){
        Cursor mCursor =
                db.query(true, TASK_TABLE, new String[] {TASK_ID, TASK_NAME,
                                TASK_TIME, TASK_DEADLINE, TASK_PRIORITY,TASK_CATEGORY, TASK_DONE}, TASK_CATEGORY + "=" + idCategory, null,
                        null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    //po prioritetima
    public Cursor filterTaskByPriority(int priority){
        Cursor mCursor =
                db.query(true, TASK_TABLE, new String[] {TASK_ID, TASK_NAME,
                                TASK_TIME, TASK_DEADLINE, TASK_PRIORITY,TASK_CATEGORY, TASK_DONE}, TASK_PRIORITY + "=" + priority, null,
                        null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }


    //po stanju
    public Cursor filterTaskByState(int done){
        Cursor mCursor =
                db.query(true, TASK_TABLE, new String[] {TASK_ID, TASK_NAME,
                                TASK_TIME, TASK_DEADLINE, TASK_PRIORITY,TASK_CATEGORY, TASK_DONE}, TASK_DONE + "=" + done, null,
                        null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    //po deadlineu
    public Cursor filterTaskByDeadline(int time){

        String timeStamp = new SimpleDateFormat("yyyyMMdd").format(Calendar.getInstance().getTime());
        Cursor mCursor;
        String filter;

        //filter -> cijeli datum, mjesec i godina, godina
        switch(time){
            case 1:
                filter = timeStamp;
                break;
            case 2:
                filter = timeStamp.substring(0,5);
                break;
            case 3:
                filter = timeStamp.substring(0,3);
                break;
            default:
                filter = timeStamp;
        }

        mCursor =  db.query(true, TASK_TABLE, new String[] {TASK_ID, TASK_NAME,
                                TASK_TIME, TASK_DEADLINE, TASK_PRIORITY,TASK_CATEGORY, TASK_DONE}, "where "+ TASK_DEADLINE+" like '"+ filter+"%'" , null,
                        null, null, null, null);

        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    //po zadanom terminu
    public Cursor filterTaskByTime(int time){

        String timeStamp = new SimpleDateFormat("yyyyMMdd").format(Calendar.getInstance().getTime());
        Cursor mCursor;
        String filter;

        //filter -> cijeli datum, mjesec i godina, godina
        switch(time){
            case 1:
                filter = timeStamp;
                break;
            case 2:
                filter = timeStamp.substring(0,5);
                break;
            case 3:
                filter = timeStamp.substring(0,3);
                break;
            default:
                filter = timeStamp;
        }

        mCursor =  db.query(true, TASK_TABLE, new String[] {TASK_ID, TASK_NAME,
                        TASK_TIME, TASK_DEADLINE, TASK_PRIORITY,TASK_CATEGORY, TASK_DONE}, "where "+ TASK_TIME+" like '"+ filter+"%'" , null,
                null, null, null, null);

        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    //obriši sve gotove zadatke
    public boolean deleteAllCopmletedTasks(){
        //return true;
        return db.delete(TASK_TABLE, TASK_DONE + "= 1", null) > 0;
    }
    */
}

