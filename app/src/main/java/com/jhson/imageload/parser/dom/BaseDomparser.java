package com.jhson.imageload.parser.dom;

import com.jhson.imageload.parser.BaseParser;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Created by jhson on 2016-01-16.
 */
public class BaseDomparser<T> extends BaseParser<T>{

    @Override
    public List<T> startParser(InputStream inputStream) throws IOException {
        return null;
    }
}
