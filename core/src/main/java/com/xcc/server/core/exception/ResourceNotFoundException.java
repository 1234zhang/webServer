package com.xcc.server.core.exception;

import com.xcc.server.core.exception.base.ServletException;
import com.xcc.server.core.statusenum.HttpStatus;

/**
 * @author Brandon.
 * @date 2019/9/7.
 * @time 16:10.
 */

public class ResourceNotFoundException extends ServletException {
    private static final HttpStatus status = HttpStatus.NOT_FOUND;
    public ResourceNotFoundException() {
        super(status);
    }
}
