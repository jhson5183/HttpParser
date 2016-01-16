package com.jhson.imageload.parser;

import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by jhson on 2016-01-16.
 */
public class BaseDomParser<T> extends BaseParser<T>{

    private final String TAG = "BaseDomParser";

    @Override
    public List<T> startParser(InputStream inputStream) throws IOException {

        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        try{
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(inputStream);

            Element element = document.getDocumentElement();
            NodeList nodeList = element.getElementsByTagName("sdk:archive");
            for (int i = 0; i < nodeList.getLength(); i++){
                Node item = nodeList.item(i);
                Node text = item.getFirstChild();
                String itemName = text.getLocalName();
                Log.e(TAG, "itemName : " + itemName);
            }
        }catch (ParserConfigurationException e){
            e.printStackTrace();
        }catch (SAXException e){
            e.printStackTrace();
        }

        return null;
    }
}
