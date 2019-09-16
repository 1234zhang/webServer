package com.xcc.server.core.exception;

import com.xcc.server.core.exception.base.ServletException;
import com.xcc.server.core.statusenum.HttpStatus;

/**
 * @author Brandon.
 * @date 2019/9/10.
 * @time 0:00.
 */

public class RequestParseException extends ServletException {
    private static final HttpStatus status = HttpStatus.BAD_REQUEST;
    public RequestParseException() {
        super(status);
    }
}
