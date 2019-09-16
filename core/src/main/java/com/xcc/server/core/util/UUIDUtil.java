package com.xcc.server.core.util;

import java.util.UUID;

/**
 * @author Brandon.
 * @date 2019/9/8.
 * @time 16:46.
 */

public class UUIDUtil {
    public static String uuid(){
        return UUID.randomUUID().toString().replaceAll("-","").toUpperCase();
    }
}
