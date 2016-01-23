package com.jhson.imageload.parser;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * 최상위 파서 클래스
 * Created by INT-jhson5183 on 2016. 1. 14..
 */
public abstract class BaseParser<Result> {

    protected String IMAGE_HOST = "http://www.gettyimagesgallery.com";

    protected List<Result> mList = null;    //파싱이 끝나고 정의된 리스트로 데이터를 반환한다.

    /*
    파싱 시작 - 파싱 방식을 구현
     */
    abstract public List<Result> startParser(InputStream inputStream) throws IOException;

}
