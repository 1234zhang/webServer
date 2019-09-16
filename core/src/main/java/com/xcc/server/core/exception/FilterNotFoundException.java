package com.xcc.server.core.exception;

import com.xcc.server.core.exception.base.ServletException;
import com.xcc.server.core.statusenum.HttpStatus;

import java.nio.ByteBuffer;

/**
 * @author Brandon.
 * @date 2019/9/8.
 * @time 16:34.
 */

public class FilterNotFoundException extends ServletException {
    private static final HttpStatus status = HttpStatus.NOT_FOUND;
    public FilterNotFoundException() {
        super(status);
    }
    public void test(){
        ByteBuffer buf = ByteBuffer.allocate(100);
        buf.get();
    }
}
