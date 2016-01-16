package com.jhson.imageload.parser;

import com.jhson.imageload.model.ImageModel;

import org.xmlpull.v1.XmlPullParser;

import java.util.ArrayList;

/**
 * Created by INT-jhson5183 on 2016. 1. 14..
 */
public class ImageXmlPullParser extends BaseXmlPullParser<ImageModel> {

    private String TAG = "ImageXmlPullParser";
    private boolean isImageStartTag = false;
    private boolean isItemTextStartTag = false;
    private boolean isItemTitleStartTag = false;
    private ImageModel mImageModel = null;

    public ImageXmlPullParser(){
        mList = new ArrayList<ImageModel>();
    }

    @Override
    protected void onStartDocument(XmlPullParser xmlPullParser){
        super.onStartDocument(xmlPullParser);
    }

    @Override
    protected void onStartTag(XmlPullParser xmlPullParser){
        super.onStartTag(xmlPullParser);
        String name = xmlPullParser.getName();
        if("img".equals(name)){
            String imageUrl = xmlPullParser.getAttributeValue(null, "src");

            if(isImageStartTag && imageUrl != null && !"".equals(imageUrl)){
                mImageModel = new ImageModel();
                mImageModel.setmImageUrl(IMAGE_HOST + imageUrl);
                mList.add(mImageModel);
                isImageStartTag = false;
            }
        }else if("div".equals(name)){
            String cls = xmlPullParser.getAttributeValue(null, "class");
            if("gallery-item-group exitemrepeater".equals(cls) || "gallery-item-group lastitemrepeater".equals(cls)){
                isImageStartTag = true;
            }else if("gallery-item-caption".equals(cls)){
                isItemTextStartTag = true;
            }
        }else if(isItemTextStartTag && "a".equals(name)){
            isItemTitleStartTag = true;
            isItemTextStartTag = false;
        }
    }

    @Override
    protected void onText(XmlPullParser xmlPullParser){
        super.onText(xmlPullParser);
        if(isItemTitleStartTag && mImageModel != null){
            mImageModel.setmTitle(xmlPullParser.getText());
            isItemTitleStartTag = false;
        }
    }

}
