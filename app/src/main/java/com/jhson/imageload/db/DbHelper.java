package com.jhson.imageload.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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

    public void init(Context context){

        CacheDb cacheDb = new CacheDb(context);
        mDb = cacheDb.getWritableDatabase();

    }

    class CacheDb extends SQLiteOpenHelper{

        public static final String ID = "id";
        public static final String TABLE_NAME = "api_cache_info";
        public static final String API_METHOD = "api_method";
        public static final String NEXT_DATE = "next_date";
        public final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME
                + "( "
                + ID + " INTEGER primary key autoincrement, "
                + API_METHOD + " TEXT unique,"
                + NEXT_DATE + " INTEGER"
                + ");";


        public CacheDb(Context context) {
            super(context, "cache", null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_TABLE);

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }

}
