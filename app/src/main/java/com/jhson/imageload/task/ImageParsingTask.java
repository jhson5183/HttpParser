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

}
