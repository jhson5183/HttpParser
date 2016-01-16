package com.jhson.imageload.parser;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Created by INT-jhson5183 on 2016. 1. 14..
 */
public class BaseXmlPullParser<Result> extends BaseParser<Result> {

    protected void onStartDocument(XmlPullParser xmlPullParser){
    }

    protected void onStartTag(XmlPullParser xmlPullParser){
    }

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
