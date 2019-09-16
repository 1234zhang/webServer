package com.xcc.server.core.exception;

import com.xcc.server.core.exception.base.ServletException;
import com.xcc.server.core.statusenum.HttpStatus;

/**
 * @author Brandon.
 * @date 2019/9/7.
 * @time 17:56.
 */

public class ServletNotFoundException extends ServletException {
    private static final HttpStatus status = HttpStatus.NOT_FOUND;
    public ServletNotFoundException() {
        super(status);
    }
}
