package com.jhson.imageload.imageloader;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


public final class IOUtils {

	private static final String TAG = "IOUtils";
    
	public static final int IO_BUFFER_SIZE = 4 * 1024;

    public static String sPathPrefix = "";

    public static void closeStream(Closeable stream) {
        if (stream != null) {
            try {
                stream.close();
            } catch (IOException e) {
                Log.e(TAG, "Could not close stream");
            }
        }
    }
    
    /**
     * 
     * @param stream
     * @return
     */
    public static boolean sync(FileOutputStream stream) {
        try {
            if (stream != null) {
                stream.getFD().sync();
            }
            return true;
        } catch (IOException e) {
        }
        return false;
    }
    
    /** 네트워크에 연결되었는지 여부를 체크 */
	public static boolean checkNetwork(Context context) {
		try {
			ConnectivityManager cManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mobileInfo = cManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
			NetworkInfo wifiInfo = cManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

			if (mobileInfo == null && wifiInfo.isConnected()) {
				return true;
			} else if (wifiInfo == null && mobileInfo.isConnected()) {
				return true;
			} else if ((mobileInfo!=null && mobileInfo.isConnected()) || (wifiInfo!=null && wifiInfo.isConnected())){
				return true;
			}
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

    public static String getFilePrefix(Context context) {

        if (TextUtils.isEmpty(sPathPrefix)) {
            // 캐시 폴더
            File cacheDir = null;
            try {
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    cacheDir = new File(context.getExternalCacheDir(), "/cache");
                } else {
                    cacheDir = new File(context.getCacheDir(), "/cache");
                }
            } catch (Exception ignore) {
                Log.w(TAG, "err ", ignore);
                return null;
            }

            // 캐시폴더 없으면 만들어라잉
            if (!cacheDir.exists()) {
                cacheDir.mkdir();
            }
            sPathPrefix = cacheDir.getAbsolutePath();
        }

        return sPathPrefix;
    }
}
