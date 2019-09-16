package com.xcc.server.core.exception.base;

import com.xcc.server.core.statusenum.HttpStatus;
import lombok.Getter;

/**
 * @author Brandon.
 * @date 2019/9/7.
 * @time 16:12.
 */

@Getter
public class ServletException extends Exception {
    private HttpStatus status;
    public ServletException(HttpStatus status) {
        this.status = status;
    }
}
