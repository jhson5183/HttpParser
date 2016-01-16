package com.jhson.imageload;

import android.app.Application;

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
}
