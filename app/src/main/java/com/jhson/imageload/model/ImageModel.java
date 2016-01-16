package com.jhson.imageload.model;

/**
 * Created by INT-jhson5183 on 2016. 1. 14..
 */
public class ImageModel {

    public ImageModel(){}

    public ImageModel(String title, String imageUrl){
        mTitle = title;
        mImageUrl = imageUrl;
    }

    private String mTitle = null;
    private String mImageUrl = null;

    public String getmImageUrl() {
        return mImageUrl;
    }

    public void setmImageUrl(String mImageUrl) {
        this.mImageUrl = mImageUrl;
    }

    public String getmTitle() {

        return mTitle;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }
}
