package com.xcc.server.core.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Brandon.
 * @date 2019/9/7.
 * @time 16:25.
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Header {
    private String key;
    private String value;
}
