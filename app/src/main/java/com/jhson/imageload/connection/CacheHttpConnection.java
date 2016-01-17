package com.jhson.imageload.connection;

import android.content.Context;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;

import com.jhson.imageload.db.cache.CacheDb;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

/**
 * 통신한 결과값을 캐시하고 전달하는 Connection class
 * Created by jhson on 2016-01-16.
 */
public class CacheHttpConnection extends HttpConnection{

    private final String TAG = "CacheHttpConnection";
    private final static long DEFUALT_CACHE_TTL = 60000 * 10;
    private File mCacheDir = null;
    private boolean isCacheRefresh = false;
    private Context mContext = null;
    private String mHttpUrl = null;
    private long mTtl = 0;

    public CacheHttpConnection(Context context, boolean isCacheRefresh){
        this(context, isCacheRefresh, DEFUALT_CACHE_TTL);
    }

    /*
    캐시를 얼마나 유지 할 것인지 ttl을 통해서 정의 할수 있다. (단위는 ms)
     */
    public CacheHttpConnection(Context context, boolean isCacheRefresh, long ttl){
        mContext = context;
        mCacheDir = new File(context.getCacheDir(), "sonload");
        this.isCacheRefresh = isCacheRefresh;
        mTtl = ttl;
    }



    @Override
    public InputStream getInputStream(String httpUrl, Map<String, String> map) {

        InputStream is = null;
        mHttpUrl = httpUrl;
        try{
            String apiUrl = getUrl(httpUrl, map);
            URL url = new URL(apiUrl);
            File cacheDir = getCacheDir(apiUrl);
            if(!cacheDir.exists()){
                cacheDir.mkdirs();
            }
            String fileName = getCacheFile(apiUrl);
            File cacheFile = new File(cacheDir.getAbsoluteFile() + "/" + fileName);
            boolean cacheExists = cacheFile.exists();
            CacheDb cacheDb = new CacheDb(mContext);
            CacheDb.CacheCursor cursor = cacheDb.getCacheInfo(httpUrl);

            boolean isCacheTimeOver = isCacheRefresh;
            if(cursor != null){
                Log.e(TAG, "System.currentTimeMillis() : " + DateFormat.format("yyyy.MM.dd hh:mm:ss", getCurrentTime()));
                Log.e(TAG, "reg_date : " + DateFormat.format("yyyy.MM.dd hh:mm:ss", cursor.mRegDate) );
                Log.e(TAG, "next_date : " + DateFormat.format("yyyy.MM.dd hh:mm:ss", cursor.mNextDate));
            }
            if(cursor != null && cursor.mNextDate < System.currentTimeMillis() && !isCacheRefresh){
                isCacheTimeOver = true;
            }

            if (!cacheExists || isCacheTimeOver) {
                saveCache(url, cacheFile);
            }
            Log.e(TAG, (!cacheExists || isCacheTimeOver) ? "connection cache not hit" : "connection cache hit");
            is = new FileInputStream(cacheFile);
        }catch(MalformedURLException e){
            e.printStackTrace();
        }catch(IOException e){
            e.printStackTrace();
        }
        return is;
    }

    /*
    네트워크를 연결하여 데이터를 받아 디스크에 캐시
     */
    private void saveCache(URL url, File cacheFile){
        long contentLenght = 0;
        InputStream is = getHttpInputStream(url);
        BufferedInputStream bufferedInputStream = new BufferedInputStream(is);
        saveCacheFile(cacheFile, bufferedInputStream, contentLenght);
        mHttpURLConnection.disconnect();
        closeStream(bufferedInputStream);
        closeStream(is);
    }

    private File getCacheDir(String method){
        return new File(mCacheDir, "cache");
    }

    private String getCacheFile(String method) {

        String match = "[^\\uAC00-\\uD7A3xfe0-9a-zA-Z\\\\s]";
        method = method.replaceAll(match, "");
        String encodeName = urlEncode(method);
        //파일의 이름이 128자가 넘어가면 생성 할수 없다.
        if(TextUtils.isEmpty(encodeName) == false) {
            if(encodeName.length() > 122){
                encodeName = encodeName.substring(0, 122);
            }
        }

        return encodeName + ".html";
    }

    /*
    디스크에 캐시파일을 생성한다.
     */
    private long saveCacheFile(File cacheFile, BufferedInputStream in, long contentLenght) {

        long lenght = -1;
        if (in == null)
            return lenght;

        FileOutputStream fout = null;
        try {
            fout = new FileOutputStream(cacheFile);
            int read = 0;
            int readLenght = 0;
            byte[] buffer = new byte[1024];

            while((read = in.read(buffer, 0, buffer.length)) > 0){
                readLenght = read;
                fout.write(buffer, 0, read);
            }
            fout.flush();
            fout.close();

            Log.e(TAG, "readLenght : " + readLenght);
            if(readLenght > 0){
                CacheDb cacheDb = new CacheDb(mContext);
                CacheDb.CacheCursor cursor = cacheDb.getCacheInfo(mHttpUrl);
                if(cursor == null){
                    long ret = cacheDb.addCacheInfo(mHttpUrl, getCurrentTime(), getCurrentTime() + mTtl);
                    Log.e(TAG, "addCacheInfo : " + ret);
                }else{
                    long ret = cacheDb.updateCacheInfo(mHttpUrl, getCurrentTime(), getCurrentTime() + mTtl);
                    Log.e(TAG, "updateCacheInfo : " + ret);
                }
            }

            lenght = cacheFile.length();
            if(lenght != contentLenght){
//                cacheFile.delete();
            }


        } catch (IOException e) {

            Log.e(TAG, e.getClass().getSimpleName(), e);

        } finally {

            closeStream(fout);
        }
        return lenght;
    }

    private String urlEncode(String msg) {
        try {
            return URLEncoder.encode(msg, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, "urlEncode err " + e.getMessage());
        }
        return "";
    }

    private long getCurrentTime() {
        return System.currentTimeMillis();
    }

    /**
     * 기본 설정 업데이트 "파일 변경날짜가" 자정, 정오 시간이 지나면 업데이트 한다.
     *
     */
    private boolean isDefaultUpdateTime(File cacheFile) {

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(getCurrentTime());
        int currentDays = (cal.get(Calendar.YEAR) * 365) + cal.get(Calendar.DAY_OF_YEAR);
        int currentTime = cal.get(Calendar.HOUR_OF_DAY);
        Log.d(TAG, "현재 일 : " + getDisplayYMDT(cal.getTimeInMillis()));

        Long mt = cacheFile.lastModified();
        cal.setTimeInMillis(mt);
        int lastDays = (cal.get(Calendar.YEAR) * 365) + cal.get(Calendar.DAY_OF_YEAR);
        int lastTime = cal.get(Calendar.HOUR_OF_DAY);

        Log.d(TAG, "파일변경일 : " + getDisplayYMDT(mt));
        if (currentDays != lastDays)
            return true;

        return lastTime < 12 && currentTime > 11;

    }

    private String getDisplayYMDT(long msec) {
        Calendar cal = Calendar.getInstance(TimeZone.getDefault(), Locale.KOREAN);
        cal.setTimeInMillis(msec);

        return String.format("%04d.%02d.%02d %02d:%02d:%02d", cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DATE), cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), cal.get(Calendar.SECOND));

    }
}
