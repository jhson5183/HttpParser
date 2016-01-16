package com.jhson.imageload.connection;

import java.io.InputStream;
import java.util.Map;

/**
 * Created by jhson on 2016-01-16.
 */
public interface BaseConnection {

    public InputStream getInputStream(String httpUrl, Map<String, String> map);
}
