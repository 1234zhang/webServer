package com.xcc.server.core.util;

import lombok.extern.slf4j.Slf4j;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author Brandon.
 * @date 2019/9/7.
 * @time 10:41.
 */

@Slf4j
public class PropertiesUtil {

    private static  Properties prop;

    static{
        initProperty();
    }

    private synchronized static void initProperty(){
        log.info("初始化资源文件.......");
        prop = new Properties();

        InputStream in = null;
        try {
            in = PropertiesUtil.class.getClassLoader().getResourceAsStream("server.properties");
            prop.load(in);
        }catch (FileNotFoundException e) {
            log.error("server.properties文件没有找到");
        }catch (IOException e) {
            log.info("出现io错误");
        }finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                log.error("server.properties 关闭流出现异常");
            }
        }
        log.info("资源文件加载完成");
    }

    public static String getProperty(String key){
        if(prop == null){
            initProperty();
        }
        return prop.getProperty(key);
    }

    /**
     *  根据key和默认值获取资源文件中的值
     * @param key 资源文件中的key
     * @param defaultValue 资源文件的默认值
     * @return 资源文件中的值
     */
    public static String getProperty(String key, String defaultValue){
        if(prop == null){
            initProperty();
        }
        return prop.getProperty(key,defaultValue);
    }
}
