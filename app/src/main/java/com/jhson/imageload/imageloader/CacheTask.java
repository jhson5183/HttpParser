package com.jhson.imageload.imageloader;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.jhson.imageload.imageloader.process.Blur;

/**
 * Created by INT-jhson5183 on 2016. 1. 19..
 */
public class CacheTask implements Runnable {

    private final String TAG = "CacheTask";

    private Context mContext;
    private String mUri = "";
    private long mViewHashCode = -1;
    private MonetRequest mRequest;

    public CacheTask(Context context, String uri, long viewHashCode, MonetRequest request) {
        mContext = context;
        mUri = uri;
        mViewHashCode = viewHashCode;
        mRequest = request;
    }

    @Override
    public void run() {
        if (!NewMonet.with(mContext).isValidRequest(mViewHashCode, this))
            return;

        Bitmap raw = null;
        int width = mRequest.getBuilder().width;
        int height = mRequest.getBuilder().height;
        int sampleSize = mRequest.getBuilder().simpleSize;
        int blur = mRequest.getBuilder().blur;
        boolean clip = mRequest.getBuilder().clip;
        int blankColor = mRequest.getBuilder().blankColor;

        raw = NewBitmapManager.getInstrance().getBitmapImage(mUri, MonetDownloader.getCachedImageFile(mUri, mContext), width, height);

        if (null != raw) {
            if (blur > 0) {
                raw = Blur.fastblur(mContext, raw, blur);
            }
            if (mViewHashCode > 0) {
                if (!NewMonet.with(mContext).unRegistRequest(mViewHashCode, this, true)) {
                    // 해당 이미지뷰에 해당 스레드가 더이상 유효하지 않다면, 다른 스레드가 이미지뷰를 점유했으므로 자원을 뱉고 스레드 종
                    Log.d(TAG, "request is invalidate");
                    return;
                }
            }
            Log.d(TAG, "request is comp");
            NewMonet.with(mContext).sImageHandler.post(mRequest.new ImageRunnable(raw));
        } else {
            NewMonet.with(mContext).executeNetworkRequest(mUri, mViewHashCode, mRequest, this);
        }
    }
}
