package com.jhson.imageload;

import android.app.Application;
import android.util.Log;

import com.jhson.imageload.db.DbHelper;
import com.jhson.imageload.imageloader.NewBitmapManager;
import com.jhson.imageload.imageloader.NewMonet;

/**
 * Created by INT-jhson5183 on 2016. 1. 15..
 */
public class ImageLoaderApplication extends Application{

    @Override
    public void onCreate() {
        super.onCreate();
        NewMonet.with(getApplicationContext()); // "초기화."
    }

    @Override
    public void onTerminate() {
        DbHelper.getInstance(getApplicationContext()).getDb().close();
        NewBitmapManager.getInstrance().clearMemoryCache();
//        NewMonet.with(getApplicationContext()).clear();

        super.onTerminate();
    }
}
