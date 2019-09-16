package com.xcc.server.core.cookie;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Brandon.
 * @date 2019/9/7.
 * @time 16:22.
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Cookie {
    private String key;
    private String value;
}
