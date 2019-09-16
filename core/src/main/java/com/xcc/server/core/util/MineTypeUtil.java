package com.xcc.server.core.util;

import com.xcc.server.core.contant.ContextConstant;
import eu.medsea.mimeutil.MimeUtil;

import java.util.Collection;

/**
 * @author Brandon.
 * @date 2019/9/9.
 * @time 1:24.
 */

public class MineTypeUtil {
    static{
        MimeUtil.registerMimeDetector("eu.medsea.mimeutil.detector.MagicMimeMimeDetector");
    }
    public static String getTypes(String fileName){
        if(fileName.endsWith(".html")){
            return ContextConstant.DEFAULT_CONSTANT_TYPE;
        }
        Collection mimeType = MimeUtil.getMimeTypes(MineTypeUtil.class.getResource(fileName));
        return mimeType.toArray()[0].toString();
    }
}
