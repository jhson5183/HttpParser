package com.jhson.imageload.parser;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Created by INT-jhson5183 on 2016. 1. 14..
 */
public abstract class BaseParser<Result> {

    protected String IMAGE_HOST = "http://www.gettyimagesgallery.com";

    protected List<Result> mList = null;

    abstract public List<Result> startParser(InputStream inputStream) throws IOException;

}
