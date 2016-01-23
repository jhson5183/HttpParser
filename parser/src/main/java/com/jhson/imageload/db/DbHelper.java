package com.jhson.imageload.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.jhson.imageload.db.cache.CacheDb;

/**
 * Created by sonjunghyun on 2016. 1. 17..
 */
public class DbHelper {

    private Context mContext = null;
    private SQLiteDatabase mDb = null;
    private DbHelper(Context context){
        mContext = context;
    }

    private static DbHelper instance = null;
    public synchronized static DbHelper getInstance(Context context){
        if(instance == null){
            instance = new DbHelper(context);
            instance.init(context);
        }
        return instance;
    }

    private void init(Context context){

        OpneHelper opneHelper = new OpneHelper(context);
        mDb = opneHelper.getWritableDatabase();
    }

    public SQLiteDatabase getDb(){
        return mDb;
    }



    class OpneHelper extends SQLiteOpenHelper{

        OpneHelper(Context context){
            super(context, "cache", null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CacheDb.CREATE_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }

}
