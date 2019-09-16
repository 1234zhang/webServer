package com.xcc.server.core.exception;

import com.xcc.server.core.exception.base.ServletException;
import com.xcc.server.core.statusenum.HttpStatus;

/**
 * @author Brandon.
 * @date 2019/9/8.
 * @time 17:08.
 */

public class ServerErrorException extends ServletException {
    private static final HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
    public ServerErrorException() {
        super(status);
    }
}
