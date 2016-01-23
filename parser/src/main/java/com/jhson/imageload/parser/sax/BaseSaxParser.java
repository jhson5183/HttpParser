package com.jhson.imageload.parser.sax;

import com.jhson.imageload.parser.BaseParser;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * Created by jhson on 2016-01-17.
 */
public class BaseSaxParser<Result> extends BaseParser<Result>{

    @Override
    public List<Result> startParser(InputStream inputStream) throws IOException {

        SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
        try {
            SAXParser saxParser = saxParserFactory.newSAXParser();
            XMLReader xmlReader = saxParser.getXMLReader();
            xmlReader.setContentHandler(mDefaultHandler);
            xmlReader.parse(new InputSource(inputStream));


        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        }

        return mList;
    }

    DefaultHandler mDefaultHandler = new DefaultHandler(){

        @Override
        public void startDocument() throws SAXException {
            super.startDocument();
            onStartDocument();
        }

        @Override
        public void endDocument() throws SAXException {
            super.endDocument();
            onEndDocument();
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            super.startElement(uri, localName, qName, attributes);
            onStartElement(uri, localName, qName, attributes);
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            super.endElement(uri, localName, qName);
            onEndElement(uri, localName, qName);
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            super.characters(ch, start, length);
            onCharacters(ch, start, length);
        }
    } ;

    protected void onStartDocument(){

    }

    protected void onEndDocument(){

    }

    protected void onStartElement(String uri, String localName, String qName, Attributes attributes){

    }

    protected void onEndElement(String uri, String localName, String qName){

    }

    protected void onCharacters(char[] ch, int start, int length){

    }

}
