package com.xcc.server.core.util;

import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Brandon.
 * @date 2019/9/9.
 * @time 1:04.
 */

@Slf4j
public class IOUtil {
    public static byte[] getBytesFromFile(String fileName) throws IOException {
        InputStream in = IOUtil.class.getResourceAsStream(fileName);
        if(in == null){
            log.info("file not found");
            throw new FileNotFoundException();
        }
        log.info("正在读取文件");
        return getBytesFromStream(in);
    }
    private static byte[] getBytesFromStream(InputStream inputStream) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while(( len = inputStream.read(buffer)) != -1){
            outputStream.write(buffer , 0, len);
        }
        outputStream.close();
        inputStream.close();
        return outputStream.toByteArray();
    }
}
