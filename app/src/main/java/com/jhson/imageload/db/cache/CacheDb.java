package com.jhson.imageload.db.cache;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.jhson.imageload.db.DbHelper;

/**
 * Created by jhson on 2016-01-17.
 */
public class CacheDb {

    public static final String ID = "id";
    public static final String TABLE_NAME = "cache_info";
    public static final String URL = "url";
    public static final String REG_DATE = "reg_date";
    public static final String NEXT_DATE = "next_date";
    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME
            + "( "
            + ID + " INTEGER primary key autoincrement, "
            + URL + " TEXT unique,"
            + REG_DATE + " INTEGER,"
            + NEXT_DATE + " INTEGER"
            + ");";

    private Context mContext = null;
    public CacheDb(Context context){
        mContext = context;
    }

    public CacheCursor getCacheInfo(String url){

        Cursor cursor = DbHelper.getInstance(mContext).getDb().query(CacheDb.TABLE_NAME, new String[]{CacheDb.URL, CacheDb.REG_DATE, CacheDb.NEXT_DATE}, CacheDb.URL + " = ? ", new String[]{url}, null, null, null);
        if(cursor == null || cursor.getCount() == 0){
            cursor.close();
            return null;
        }

        CacheCursor cacheCursor = new CacheCursor();
        if(cursor.moveToFirst()){
            cacheCursor.mUrl = cursor.getString(0);
            cacheCursor.mRegDate = cursor.getLong(1);
            cacheCursor.mNextDate = cursor.getLong(2);
        }

        cursor.close();

        return cacheCursor;

    }

    public long addCacheInfo(String url, long regDate, long nextDate){

        ContentValues contentValues = new ContentValues();
        contentValues.put(CacheDb.URL, url);
        contentValues.put(CacheDb.REG_DATE, regDate);
        contentValues.put(CacheDb.NEXT_DATE, nextDate);

        return DbHelper.getInstance(mContext).getDb().insert(CacheDb.TABLE_NAME, null, contentValues);
    }

    public int updateCacheInfo(String url, long regDate, long nextDate){

        ContentValues contentValues = new ContentValues();
        contentValues.put(CacheDb.NEXT_DATE, nextDate);
        contentValues.put(CacheDb.REG_DATE, regDate);
        return DbHelper.getInstance(mContext).getDb().update(CacheDb.TABLE_NAME, contentValues, CacheDb.URL + " = ? ", new String[] {url});

    }

    public class CacheCursor{
        public String mUrl = null;
        public long mRegDate = -1;
        public long mNextDate = -1;
    }

//    public long getLongCol(String name) {
//        try {
//            return this.getLong(this.getColumnIndexOrThrow(name));
//        } catch (IllegalArgumentException e) {
//            return 0;
//        }
//    }
//
//    public int getIntCol(String name) {
//        try {
//            return this.getInt(this.getColumnIndexOrThrow(name));
//        } catch (IllegalArgumentException e) {
//            return 0;
//        }
//    }
//
//    public int getPositionIntCol(String name) {
//        try {
//            return this.getInt(this.getColumnIndexOrThrow(name));
//        } catch (IllegalArgumentException e) {
//            return -1;
//        }
//    }
//
//    public String getStringCol(String name) {
//        try {
//            return getString(getColumnIndexOrThrow(name));
//        } catch (IllegalArgumentException e) {
//            return null;
//        }
//    }
//
//    public float getFloatCol(String name) {
//        try {
//            return getFloat(getColumnIndexOrThrow(name));
//        } catch (IllegalArgumentException e) {
//            return 0;
//        }
//    }

}
