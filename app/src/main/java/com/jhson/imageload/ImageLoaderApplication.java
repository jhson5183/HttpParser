package com.jhson.imageload;

import android.app.Application;

import com.jhson.imageload.db.DbHelper;
import com.jhson.imageload.imageloader.Gogh;
import com.jhson.imageload.imageloader.NewBitmapManager;

/**
 * Created by INT-jhson5183 on 2016. 1. 15..
 */
public class ImageLoaderApplication extends Application{

    @Override
    public void onCreate() {
        super.onCreate();
        Gogh.with(getApplicationContext()); // "초기화."
    }

    @Override
    public void onTerminate() {
        DbHelper.getInstance(getApplicationContext()).getDb().close();
        NewBitmapManager.getInstrance().clearMemoryCache();
//        Gogh.with(getApplicationContext()).clear();

        super.onTerminate();
    }
}
