package com.xcc.server.core.statusenum;

/**
 * @author Brandon.
 * @date 2019/9/7.
 * @time 10:23.
 */

public enum HttpStatus {

    OK(200),

    NOT_FOUND(404),

    INTERNAL_SERVER_ERROR(500),

    BAD_REQUEST(400),

    MOVE_TEMPORARILY(302);

    private Integer code;
    private HttpStatus(Integer code){
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }
}
