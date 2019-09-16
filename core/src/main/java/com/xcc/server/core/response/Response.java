package com.xcc.server.core.response;

import com.xcc.server.core.contant.CharContant;
import com.xcc.server.core.contant.CharsetContant;
import com.xcc.server.core.contant.ContextConstant;
import com.xcc.server.core.cookie.Cookie;
import com.xcc.server.core.network.handle.BaseRequestHandle;
import com.xcc.server.core.request.dispatchar.BaseRequestDispatcher;
import com.xcc.server.core.statusenum.HttpStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.security.cert.CRL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.xcc.server.core.contant.CharContant.BLANK;
import static com.xcc.server.core.contant.CharContant.CRLF;
import static com.xcc.server.core.contant.CharsetContant.UTF_8_CHARSET;

/**
 * @author Brandon.
 * @date 2019/9/7.
 * @time 16:17.
 * 响应实体
 */

@Slf4j
@Getter
@Setter
public class Response {
    private StringBuilder headerAppender;
    private HttpStatus status = HttpStatus.OK;
    private List<Cookie> cookies;
    private List<Header> headers;
    private String contentType = ContextConstant.DEFAULT_CONSTANT_TYPE;
    private byte[] body = new byte[0];
    private BaseRequestHandle requestHandle;

    public Response(){
        headerAppender = new StringBuilder();
        cookies = new ArrayList<>();
        headers = new ArrayList<>();
    }

    /**
     * 添加HttpStatus请求
     * @param status
     */
    public void setStatus(HttpStatus status){
        this.status = status;
    }
    public void setContentType(String contentType){
        this.contentType = contentType;
    }

    public void setBody(byte[] body){
        this.body = body;
    }

    public void addCookie(Cookie cookie){
        cookies.add(cookie);
    }
    public void addHeader(Header header){
        this.headers.add(header);
    }

    /**
     *     构建响应体
     */
    private void buildResponse(){
        buildHeader();
        buildBody();
    }

    /**
     * 组装响应头
     */
    private void buildHeader(){
        // HTTP/1.1 200 OK
        headerAppender.append("HTTP/1.1").append(BLANK).append(status.getCode()).append(BLANK).append(status).append(CRLF);
        // date Sat, 07 Sep 2019 08:26:58 GMT
        headerAppender.append("date:").append(BLANK).append(new Date()).append(CRLF);
        headerAppender.append("content-Type:").append(BLANK).append(contentType).append(CRLF);

        if(headers != null){
            headers.forEach(b->{
                headerAppender.append(b.getKey()).append(":").append(BLANK).append(b.getValue()).append(CRLF);
            });
        }
        if(cookies.size() > 0){
            cookies.forEach(b->{
                headerAppender.append("set-Cookie:").append(BLANK).append(b.getKey()).append("=").append(b.getValue()).append(CRLF);
            });
        }
        headerAppender.append("Content-length").append(BLANK);
    }

    /**
     * 分析响应体长度
     */
    private void buildBody(){
        this.headerAppender.append(body.length).append(CRLF).append(CRLF);
    }

    /**
     * 返回构建的response的数据，用于bio
     * @return byte[]
     */
    public byte[] getResponseByte(){
        buildResponse();
        byte[] header = headerAppender.toString().getBytes(UTF_8_CHARSET);
        byte[] response = new byte[header.length + body.length];
        // 将响应头与body中的数据，全部复制到response的字节数组中。
        System.arraycopy(header, 0, response, 0, header.length);
        System.arraycopy(body, 0, response, header.length, body.length);
        return response;
    }

    /**
     * 重定向
     * @param url
     */
    public void redirect(String url){
        log.info("重定向:{}" ,url);
        addHeader(new Header("Location", url));
        setStatus(HttpStatus.MOVE_TEMPORARILY);
        buildResponse();
        //刷新至客户端
        requestHandle.flushResponse();
    }

    /**
     * 用于调用不同RequestHandle 的写刷新（将response写入客户端）
     * @param requestHandle
     */
    public void setRequestHandle(BaseRequestHandle requestHandle){
        this.requestHandle = requestHandle;
    }
}
