package com.jhson.imageload.imageloader;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.jhson.imageload.imageloader.process.Blur;

import java.io.File;
import java.util.LinkedList;

/**
 * Created by INT-jhson5183 on 2016. 1. 19..
 */
public class NetworkTask implements Runnable {

    private Context mContext;
    private String mUrl = "";
    private long mViewHashCode = -1;
    private MonetRequest mRequest;
    private MonetDownloader mDownloader;

    public NetworkTask(Context context, String url, long viewHashCode, MonetRequest request) {
        mContext = context;
        mUrl = url;
        mViewHashCode = viewHashCode;
        mRequest = request;
    }

    public void imgDownloadStop() {
        if (null != mDownloader) mDownloader.stop();
    }

    @Override
    public void run() {
        if (!NewMonet.with(mContext).isValidRequest(mViewHashCode, this))
            return;

        if (NewMonet.with(mContext).mWaitingJobs.containsKey(mUrl)) { // "다운로드 중인 동일한 URL 이 있다면."
            try {
                LinkedList<Runnable> waitingSameUrl = NewMonet.with(mContext).mWaitingJobs.get(mUrl);
                if (waitingSameUrl != null) {
                    waitingSameUrl.add(this);
//					Log.i(TAG, "동일한 URL 스레드를 보관한다. "+waitingSameUrl.size());
                } else {
                    LinkedList<Runnable> waitingUrl = new LinkedList<Runnable>();
                    waitingUrl.add(this);
                    NewMonet.with(mContext).mWaitingJobs.put(mUrl, waitingUrl);
//					Log.i(TAG, "신규 스레드를 보관한한다. "+waitingUrl.size());
                }
                synchronized (this) {
                    wait();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        if (!NewMonet.with(mContext).isValidRequest(mViewHashCode, this)) {
            NewMonet.with(mContext).unLock(mUrl);
            return;
        }

        File cachedFile = MonetDownloader.getCachedImageFile(mUrl, mContext);
        if (cachedFile.exists() && cachedFile.length() > 1) {
        } else {
            mDownloader = new MonetDownloader(mContext);
            mDownloader.get(mContext, mUrl, cachedFile); // "error 이면."
        }

        NewMonet.with(mContext).unLock(mUrl); // "다운로드 실패가 발생해도 모두 잡혀있는 쓰레드 UNLOCK 해야한다."

        Bitmap raw = NewBitmapManager.getInstrance().getBitmapImage(mUrl, cachedFile, mRequest.getBuilder().width, mRequest.getBuilder().height);

        int blur = mRequest.getBuilder().blur;
        if (blur > 0) {
            raw = Blur.fastblur(mContext, raw, blur);
        }

        if (mViewHashCode > 0) {
            if (!NewMonet.with(mContext).unRegistRequest(mViewHashCode, this, true)) {
                // jsyoo 해당 이미지뷰에 해당 스레드가 더이상 유효하지 않다면, 다른 스레드가 이미지뷰를 점유했으므로 자원을 뱉고 스레드 종
                Log.d("test04", "request is invalidate");
                return;
            }
        }
        Log.d("test04", "request is comp");
        NewMonet.with(mContext).sImageHandler.post(mRequest.new ImageRunnable(raw));
    }
}