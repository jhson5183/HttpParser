package com.jhson.imageload.connection;

import android.text.TextUtils;
import android.util.Log;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;

/**
 * InputStream을 얻어오는 클래스
 * Created by jhson on 2016-01-16.
 */
public class HttpConnection implements BaseConnection {

    private final String TAG = "HttpConnection";

    protected HttpURLConnection mHttpURLConnection = null;

    public HttpConnection(){
    }

    protected InputStream getHttpInputStream(URL url){
        InputStream is = null;
        try {
            mHttpURLConnection = (HttpURLConnection) url.openConnection();
            mHttpURLConnection.setConnectTimeout(15000);
            is = mHttpURLConnection.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.e(TAG, "httpconnection is : " + is);

        return is;
    }

    @Override
    public InputStream getInputStream(String httpUrl, Map<String, String> map) {

        try{
            String apiUrl = getUrl(httpUrl, map);
            URL url = new URL(apiUrl);
            return getHttpInputStream(url);
        }catch(IOException e){
            e.printStackTrace();
        }
        return null;
    }

    protected String getUrl(String methodUrl, Map<String, String> map) {
        String url = methodUrl;
        String data = makeData(map);
        if (!TextUtils.isEmpty(data))
            url += "&" + data;

        Log.d(TAG, "uri " + url);
        return url;
    }

    private String makeData(Map<String, String> map) {

        if (map == null)
            return "";

        String result = "";
        int i = 0;

        Iterator<String> it = map.keySet().iterator();
        while (it.hasNext()) {

            if (i > 0)
                result += "&";

            String key = it.next();
            result += key + "=" + map.get(key);

            i++;
        }

        return result;
    }

    protected void closeStream(Closeable stream) {
        if (stream != null) {
            try {
                stream.close();
            } catch (IOException e) {
                Log.e(TAG, "Could not close stream", e);
            }
        }
    }

}
