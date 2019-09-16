package com.xcc.server.core.context.holder;

import com.xcc.server.core.filter.Filter;
import lombok.Data;

/**
 * @author Brandon.
 * @date 2019/9/8.
 * @time 0:01.
 */

@Data
public class FilterHolder {
    private String filterClass;
    private Filter filter;

    public FilterHolder(String filterClass){
        this.filterClass = filterClass;
    }
}
