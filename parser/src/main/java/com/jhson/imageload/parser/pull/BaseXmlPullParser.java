package com.jhson.imageload.parser.pull;

import com.jhson.imageload.parser.BaseParser;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * XmlPullParser 베이스 클래스
 * Created by INT-jhson5183 on 2016. 1. 14..
 */
public class BaseXmlPullParser<Result> extends BaseParser<Result> {

    /*
    파싱중 START_DOCUMENT 이벤트일때 호출 된다.
     */
    protected void onStartDocument(XmlPullParser xmlPullParser){
    }

    /*
    파싱중 START_TAG 이벤트일 때 호출 된다.
     */
    protected void onStartTag(XmlPullParser xmlPullParser){
    }

    /*
    파싱중 TEXT 이벤트일 때 호출 된다.
     */
    protected void onText(XmlPullParser xmlPullParser){
    }

    @Override
    public List<Result> startParser(InputStream inputStream) throws IOException {
        XmlPullParser xmlPullParser = null;
        XmlPullParserFactory xmlPullParserFactory = null;

        try{
            xmlPullParserFactory = XmlPullParserFactory.newInstance();
            xmlPullParser = xmlPullParserFactory.newPullParser();
            xmlPullParser.setInput(inputStream, "utf-8");

            int parserEventType = xmlPullParser.getEventType();

            while(parserEventType != XmlPullParser.END_DOCUMENT){
                switch(parserEventType){
                    case XmlPullParser.START_DOCUMENT:
                        onStartDocument(xmlPullParser);
                        break;
                    case XmlPullParser.START_TAG:
                        onStartTag(xmlPullParser);
                        break;
                    case XmlPullParser.TEXT:
                        onText(xmlPullParser);
                        break;

                }
                parserEventType = xmlPullParser.next();
            }

        }catch (XmlPullParserException e){
            e.printStackTrace();
        }

        return mList;

    }

}
