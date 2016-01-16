package com.jhson.imageload.task;

import android.content.Context;

import com.jhson.imageload.connection.BaseConnection;
import com.jhson.imageload.connection.HttpConnection;
import com.jhson.imageload.model.ImageModel;
import com.jhson.imageload.parser.BaseParser;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

/**
 * Http, parser를 컨트롤 하며 실제로 실행되는 쓰레드
 * 사용자가 생성자를 통해 정의한 파서와 커넥션을 가지고 통신,파싱을 한다.
 * Created by INT-jhson5183 on 2016. 1. 14..
 */
public class ImageParsingTask extends BaseAsyncTask<String, Void, List<ImageModel>>{

    private String TAG = "ImageParsingTask";


    private BaseParser mBaseParser = null;
    private BaseConnection mBaseConnection = null;

    public ImageParsingTask(Context context, BaseParser baseParser, BaseConnection baseConnection){
       super(context);
        mBaseParser = baseParser;
        mBaseConnection = baseConnection;
    }

    @Override
    protected List<ImageModel> doInBackground(String... params) {
        return HttpParsingExecute(params[0], null);
    }

    private List<ImageModel> HttpParsingExecute(String httpUrl, HashMap<String, String> map) {

        InputStream is = mBaseConnection.getInputStream(httpUrl, map);
        if(is == null){
            return null;
        }
        BufferedInputStream bufferedInputStream = new BufferedInputStream(is);

        List<ImageModel> model = null;
        try {
            model = mBaseParser.startParser(bufferedInputStream);
            if(bufferedInputStream != null){
                bufferedInputStream.close();
            }
            if(is != null){
                is.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return model;

    }

    //    private synchronized List<ImageModel> HttpQueryCache(boolean isPost, String apiUrl, String method, SortedMap<String, String> map) {
//
//        List<ImageModel> result = null;
//        boolean save = false;
//        boolean isCache = true;
//        boolean isExist = false;
//
//        getUrl(apiUrl + method, map);
//
//        File cacheFile = getCacheFile(method, map);
//
//        boolean cacheExists = cacheFile.exists();
//        long ttl = 0; // " DB 에서 가져오고 없으면 기본 설정시간을 따른다. "
//
//        String apiMethod = getUrl(method, map);
////        if (cacheExists) {
////            ApiCacheInfo info = BugsDb.getInstance(mContext).getBugsDbApi().getApiCacheInfo(apiMethod);
////            if (info != null) {
////                isExist = true;
////                ttl = info.mNextDate;
////
////                if (!isPastTime(ttl)) { // "mNextDate 보다 현재 시간이 지나지 않았으면 cache load  "
////                    isCache = true;
////                    result = loadCache(cacheFile);
////                }
//////                Log.d("apitime", "DB 캐쉬로드 : " + (result != null) + " next date : " + MiscUtils.getDispYMDT(ttl) + " now date : " + MiscUtils.getDispYMDT(MiscUtils.getCurrentTime()) + " ID : " + apiMethod);
////
////            } else {
////
////                if (!isDefaultUpdateTime(cacheFile)) { // " 자정, 정오 일때 API 데이터를 불러온다. "
////                    isCache = true;
////                    result = loadCache(cacheFile);
////                }
//////                Log.d("apitime", "기본 캐쉬로드 : " + (result != null) + " ID : " + apiMethod);
////            }
////        }
//
//        if (result == null) {
//            isCache = false;
//            if (isPost) {
////                result = HttpPostExecute(apiUrl + method, map);
//            } else {
//                result = HttpParsingExecute(apiUrl + method, map);
//            }
//            save = true;
//        }
//
//        if (result == null && cacheExists) { // " HTTP통신으로 데이터를 못가져왔고 이전 cache가 있다면 "
//            isCache = true;
//            result = loadCache(cacheFile);
//        }
//
//        if (result != null) {
////            JSONObject json = getJSONObject(result, isCache);
////            if (save && json != null) {
////                int retCode = json.optInt("ret_code");
////                if (retCode == SUCCESS) {
////                    saveCache(cacheFile, result);
////                    saveNextDate(json, isExist, apiMethod);
////                }
////            }
////            return json;
//        }
//
//        return null;
//    }

    //    private String loadCache(File cacheFile) {
//
//        BufferedReader buf = null;
//        String result = null;
//
//        try {
//
//            StringBuilder sb = new StringBuilder();
//            buf = new BufferedReader(new FileReader(cacheFile));
//            String line;
//            while ((line = buf.readLine()) != null) {
//                sb.append(line);
//            }
//
//            Log.i(TAG, "Cache = " + cacheFile.getAbsolutePath());
//            buf.close();
//
//            result = sb.toString();
//
//        } catch (IOException e) {
//            Log.e(TAG, e.getClass().getSimpleName(), e);
//        } finally {
//            closeStream(buf);
//        }
//
//        return result;
//    }

}
