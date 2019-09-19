package com.xcc.server.core.request;

import com.xcc.server.core.contant.CharContant;
import com.xcc.server.core.contant.CharsetContant;
import com.xcc.server.core.context.ServletContext;
import com.xcc.server.core.context.WebApplication;
import com.xcc.server.core.cookie.Cookie;
import com.xcc.server.core.exception.RequestInvalidException;
import com.xcc.server.core.network.handle.BaseRequestHandle;
import com.xcc.server.core.request.dispatchar.BaseRequestDispatcher;
import com.xcc.server.core.request.dispatchar.lmp.ApplicationRequestDispatcher;
import com.xcc.server.core.session.HttpSession;
import com.xcc.server.core.statusenum.RequestMethod;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.net.URLDecoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Brandon.
 * @date 2019/9/7.
 * @time 16:36.
 */

@Slf4j
@Data
public class Request {
    private BaseRequestHandle requestHandle;
    private RequestMethod method;
    private String url;
    private Map<String, List<String>> params;
    private Map<String, List<String>> headers;
    private Map<String, Object> attributes;
    private ServletContext servletContext;
    private Cookie[] cookies;
    private HttpSession session;

    /**
     * 获取queryString或者body(表单格式)的键值类型的数据
     * @param key
     * @return
     */
    public String getParameter(String key){
        List<String> params = this.params.get(key);
        if(params == null){
            return null;
        }
        return params.get(0);
    }

    /**
     * 解析http中的请求
     * @param data
     */
    public Request(byte[] data) throws RequestInvalidException {
        this.attributes = new HashMap<>();
        String[] lines = null;
        // 对中文的url进行解码
        lines = URLDecoder.decode(new String(data, CharsetContant.UTF_8_CHARSET), CharsetContant.UTF_8_CHARSET).split(CharContant.CRLF);
        log.info("Request 读取完毕");
        log.info("请求行：{}", Arrays.toString(lines));
        if(lines.length < 1){
            throw new RequestInvalidException();
        }
        parseHandle(lines);
        if(headers.containsKey("Content-Length") && !headers.get("Content-Length").get(0).equals("0")){
            parseBody(lines[lines.length - 1]);
        }
        WebApplication.getServletContext().afterRequestCreate(this);
    }
    public void setAttributes(String key, Object value){attributes.put(key, value);}
    public Object getAttributes(String key){return attributes.get(key);}
    public BaseRequestDispatcher getRequestDispatcher(String url){return new ApplicationRequestDispatcher(url);}

    /**
     * 如果请求报文中带有JSESSIONID这个cookie，那么就取出对应的session
     * 否则就创建一个session，并在响应报文中添加一个响应头Set-Cookie：JSESSIONID=xxxxxxxxxxxxx
     * 所有从请求报文中得到的Cookie，都会在请求报文中返回
     * 服务器只会在客户端第一次请求响应的时候，在响应头上添加Set-Cookie:"JSESSIONID=xxxxxxxx"信息
     * 接下来在同一个会话的第二次第三次请求响应头里，是不会添加Set-Cookie:"JSESSIONID=xxxxxx"信息
     * 即，如果在Cookie中读到JSESSIONID，那么不会创建新的session，也不会在响应头中加入Set-Cookie:"JSESSIONID=xxxxxxx"
     * 如果没有读到，那么就会创建一个新的session，并在响应头Cookie中加入Set-Cookie:"JSESSIONID=xxxxxxx"
     * 如果没有调用getSession，就不会创建新的session
     * @param createIfNotExists 如果为true，在不存在session的时候创建一个新的session，如果为false，就会返回一个null
     * @return HttpSession
     */
    public HttpSession getSession(boolean createIfNotExists){
        if(session != null){
            return session;
        }
        for(Cookie cookie : cookies){
            if(cookie.getKey().equals("JSESSIONID")){
                HttpSession currentSession = servletContext.getSession(cookie.getValue());
                if(currentSession != null){
                    this.session = currentSession;
                    return this.session;
                }
            }
        }
        if(!createIfNotExists){
            return null;
        }
        session = servletContext.createSession(requestHandle.getResponse());
        return session;
    }

    public HttpSession getSession(){
        return getSession(true);
    }

    /**
     * 获取servlet访问url
     * @return
     */
    public String servletPattern(){
        return url;
    }

    /**
     * 解析请求头
     * @param lines
     */
    private void parseHandle(String[] lines){
        log.info("解析请求头 : {}", lines);
        String firstLine = lines[0];
        // 解析方法
        String[] firstLineSplit = firstLine.split(CharContant.BLANK);

        this.method = RequestMethod.valueOf(firstLineSplit[0]);
        log.debug("method : {} " , method);

        // 解析url
        String rawUrl = firstLineSplit[1];
        String[] urlSplit = rawUrl.split("\\?");
        this.url = urlSplit[0];
        log.debug("url : {}", this.url);

        //解析参数
        if(urlSplit.length > 1){
            parseParam(urlSplit[1]);
        }
        log.debug("params : {}", this.params);

        // 解析请求头
        String header;
        this.headers = new HashMap<>();
        for (int i = 1; i < lines.length; i++) {
            header = lines[i];
            if(header.equals("")){
                break;
            }
            int colonIndex = header.indexOf(':');
            String key = header.substring(0, colonIndex);
            String[] value = header.substring(colonIndex + 2).split(",");
            headers.put(key, Arrays.asList(value));
        }
        log.debug("header : {}", this.headers);

        //解析cookie
        if(headers.containsKey("Cookie")){
            String[] rawCookie = headers.get("Cookie").get(0).split(";");
            cookies = new Cookie[rawCookie.length];
            for (int i = 0; i < rawCookie.length; i++) {
                String[] kv = rawCookie[i].split("=");
                cookies[i] = new Cookie(kv[0],kv[1]);
            }
            headers.remove("Cookie");
        }else{
            cookies = new Cookie[0];
        }
        log.debug("cookies : {}", Arrays.toString(cookies));
    }

    private void parseBody(String body){
        log.info("解析请求体");
        List<String> length = this.headers.get("Content-Length");
        byte[] bytes = body.getBytes(CharsetContant.UTF_8_CHARSET);
        if(length != null){
            int len = Integer.parseInt(length.get(0));
            log.info("the length : {}", len);
            parseParam(new String(bytes, 0, Math.min(len, bytes.length), CharsetContant.UTF_8_CHARSET).trim());
        }else{
            parseParam(body.trim());
        }
        if(params == null){
            params = new HashMap<>();
        }
    }

    private void parseParam(String params){
        String[] urlParam = params.split("&");
        if(this.params == null){
            this.params = new HashMap<>();
        }
        for(String param : urlParam){
            String[] kv = param.split("=");
            String key = kv[0];
            String[] values = kv[1].split(",");
            this.params.put(key, Arrays.asList(values));
        }
    }
}
