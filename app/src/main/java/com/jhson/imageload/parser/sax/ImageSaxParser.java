package com.jhson.imageload.parser.sax;

import android.util.Log;

import com.jhson.imageload.model.ImageModel;

import org.xml.sax.Attributes;

import java.util.ArrayList;

/**
 * Created by jhson on 2016-01-17.
 */
public class ImageSaxParser extends BaseSaxParser<ImageModel>{

    private String TAG = "ImageSaxParser";
    private ImageModel mImageModel = null;
    private boolean isImageStartTag = false;
    private boolean isItemTextStartTag = false;
    private boolean isItemTitleStartTag = false;

    public ImageSaxParser(){
        mList = new ArrayList<ImageModel>();
    }

    @Override
    protected void onStartElement(String uri, String localName, String qName, Attributes attributes) {
        super.onStartElement(uri, localName, qName, attributes);
        if("img".equals(qName)){
            String imageUrl = attributes.getValue("src");
            if(isImageStartTag && imageUrl != null && !"".equals(imageUrl)){
                mImageModel = new ImageModel();
                mImageModel.setmImageUrl(IMAGE_HOST + imageUrl);
                mList.add(mImageModel);
                isImageStartTag = false;
            }
        }else if("div".equals(qName)){
            String cls = attributes.getValue("class");
            if("gallery-item-group exitemrepeater".equals(cls) || "gallery-item-group lastitemrepeater".equals(cls)){
                isImageStartTag = true;
            }else if("gallery-item-caption".equals(cls)){
                isItemTextStartTag = true;
            }
        }else if(isItemTextStartTag && "a".equals(qName)){
            isItemTitleStartTag = true;
            isItemTextStartTag = false;
        }
    }

    @Override
    protected void onCharacters(char[] ch, int start, int length) {
        super.onCharacters(ch, start, length);
        if(isItemTitleStartTag && mImageModel != null){
            mImageModel.setmTitle(new String(ch, start, length));
            isItemTitleStartTag = false;
        }
    }
}
