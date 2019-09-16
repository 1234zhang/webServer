package com.xcc.server.core.session;

import com.xcc.server.core.context.WebApplication;
import lombok.Data;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Brandon.
 * @date 2019/9/7.
 * @time 18:05.
 */

@Data
public class HttpSession {
    private String id;
    private Map<String, Object> attributes;
    private boolean isValid;
    /**
     * 用于判断session 是否过期， 标准为当前时间 - 创建时间 >= 阈值
     */
    private Instant lastAccessed;

    public HttpSession(String id){
        this.id = id;
        this.attributes = new ConcurrentHashMap<>();
        this.isValid = true;
        this.lastAccessed = Instant.now();
    }

    public void invalidate(){
        this.isValid = false;
        this.attributes.clear();
        WebApplication.getServletContext().invalidateSession(this);
    }

    public Object getAttribute(String key){
        if(isValid){
            this.lastAccessed = Instant.now();
            return attributes.get(key);
        }
        throw new IllegalStateException("session has invalidated");
    }

    public void setAttribute(String key, Object object){
        if (isValid){
            this.lastAccessed = Instant.now();
            attributes.put(key, object);
        }
        throw new IllegalStateException("session has invalidated");
    }

    public String getId(){
        return this.id;
    }
    public Instant getLastAccessed(){return lastAccessed;}
    public void removeAttribute(String key){attributes.remove(key);}

}
