package com.jhson.imageload;

import android.app.Application;

import com.jhson.gogh.Gogh;
import com.jhson.gogh.NewBitmapManager;
import com.jhson.imageload.db.DbHelper;

/**
 * Created by INT-jhson5183 on 2016. 1. 15..
 */
public class ImageLoaderApplication extends Application{

    @Override
    public void onCreate() {
        super.onCreate();
        Gogh.getInstance(getApplicationContext()); // "초기화."
    }

    @Override
    public void onTerminate() {
        DbHelper.getInstance(getApplicationContext()).getDb().close();
        NewBitmapManager.getInstrance().clearMemoryCache();
//        Gogh.getInstance(getApplicationContext()).clear();

        super.onTerminate();
    }
}
