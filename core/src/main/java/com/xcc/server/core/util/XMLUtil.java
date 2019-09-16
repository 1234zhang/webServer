package com.xcc.server.core.util;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;

import javax.xml.parsers.SAXParser;
import java.io.InputStream;

/**
 * @author Brandon.
 * @date 2019/9/8.
 * @time 16:47.
 */

public class XMLUtil {
    public static Document getDocument(InputStream in) throws DocumentException {
        SAXReader reader = new SAXReader();
        return reader.read(in);
    }
}
