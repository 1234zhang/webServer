package com.xcc.server.core.context;

import com.xcc.server.core.contant.ContextConstant;
import com.xcc.server.core.context.holder.FilterHolder;
import com.xcc.server.core.context.holder.ServletHolder;
import com.xcc.server.core.cookie.Cookie;
import com.xcc.server.core.exception.FilterNotFoundException;
import com.xcc.server.core.exception.ServletNotFoundException;
import com.xcc.server.core.filter.Filter;
import com.xcc.server.core.listener.HttpSessionListener;
import com.xcc.server.core.listener.ServletContextListener;
import com.xcc.server.core.listener.ServletRequestListener;
import com.xcc.server.core.listener.event.HttpSessionEvent;
import com.xcc.server.core.listener.event.ServletContextEvent;
import com.xcc.server.core.listener.event.ServletRequestEvent;
import com.xcc.server.core.request.Request;
import com.xcc.server.core.response.Response;
import com.xcc.server.core.servlet.Servlet;
import com.xcc.server.core.session.HttpSession;
import com.xcc.server.core.session.IdleSessionCleaner;
import com.xcc.server.core.util.UUIDUtil;
import com.xcc.server.core.util.XMLUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.springframework.util.AntPathMatcher;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author Brandon.
 * @date 2019/9/7.
 * @time 17:48.
 */

@Slf4j
@Data
public class ServletContext {
    /**
     * 别名->类名
     * 一个Servlet 类只能有一个servlet类别名
     */
    Map<String, ServletHolder> servlets;
    /**
     * 一个url对应一个servlet，一个servlet可对应多个URL
     *URL Pattern -> servlet别名
     */
    Map<String, String> servletMapping;

    /**
     * 一个filter 对应一个别名
     */
    Map<String, FilterHolder> filters;
    /**
     * Pattern URL -> 别名列表, 同一个url 可以对应多个filter， 但是只对应一个servlet
     */
    Map<String, List<String>> filterMapping;

    /**
     * 各大监视器
     */
    private List<HttpSessionListener> httpSessionListeners;
    private List<ServletRequestListener> servletRequestListeners;
    private List<ServletContextListener> servletContextListeners;

    /**
     * 域
     */
    Map<String, Object> attributes;

    /**
     * 整个应用的HttpSession
     */
    Map<String, HttpSession> sessions;

    /**
     * 路径匹配器，由spring 提供
     */
    private AntPathMatcher antPathMatcher;

    /**
     * 过期session清除器
     */
    private IdleSessionCleaner idleSessionCleaner;



    public ServletContext() throws IllegalAccessException, ClassNotFoundException, InstantiationException, DocumentException {
        init();
    }

    /**
     * 根据url获取对应的一个Servlet实例
     * @param url
     * @return
     */
    public Servlet mapServlet(String url) throws ClassNotFoundException, ServletNotFoundException, InstantiationException, IllegalAccessException {
        // 1.精确匹配
        log.info("get url from servletMapping : {} ", url);
        String servletAlias = servletMapping.get(url);
        log.info("servletAlias : {}", servletAlias);
        if(servletAlias != null){
            return initAndGetServlet(servletAlias);
        }

        // 2. 路径匹配
        List<String> matchingPatterns = new ArrayList<>();
        Set<String> patterns = servletMapping.keySet();
        for (String pattern : patterns){
            log.info("pattern : {}", pattern);
            if(antPathMatcher.match(pattern, url)){
                matchingPatterns.add(pattern);
            }
        }
        if(!matchingPatterns.isEmpty()){
            Comparator<String> patternComparator = antPathMatcher.getPatternComparator(url);
            Collections.sort(matchingPatterns, patternComparator);
            String bestMatch = matchingPatterns.get(0);
            log.info("bestMatch : {}", bestMatch);
            return initAndGetServlet(bestMatch);
        }
        return initAndGetServlet(ContextConstant.DEFAULT_SERVLET_ALIAS);
    }

    /**
     * 初始化并获取servlet实例，如果已经初始化，则直接返回
     * @param servletAlias
     * @return
     * @throws ServletNotFoundException
     * @throws ClassNotFoundException
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public Servlet initAndGetServlet(String servletAlias) throws ServletNotFoundException, ClassNotFoundException, IllegalAccessException, InstantiationException {
        ServletHolder servletHolder = servlets.get(servletAlias);
        if(servletHolder == null){
            throw new ServletNotFoundException();
        }
        if(servletHolder.getServlet() == null){
            Servlet servlet = (Servlet) Class.forName(servletHolder.getServletClass()).newInstance();
            servlet.init();
            servletHolder.setServlet(servlet);
        }
        return servletHolder.getServlet();
    }

    /**
     * 根据url获取一系列的filter
     * @param url
     * @return
     */
    public List<Filter> mapFilter(String url) throws ClassNotFoundException, FilterNotFoundException, InstantiationException, IllegalAccessException {
        List<String> matchingPattern = new ArrayList<>();
        Set<String> urlPatterns = filterMapping.keySet();
        for (String pattern : urlPatterns){
            if(antPathMatcher.match(pattern, url)){
                matchingPattern.add(pattern);
            }
        }
        // 获取到所有的filter别名
        Set<String> filterAlias = matchingPattern.stream().flatMap(pattern -> this.filterMapping.get(pattern).stream()).collect(Collectors.toSet());;
        // 根据别名加载所有的filter类
        List<Filter> result = new ArrayList<>();
        for (String alias : filterAlias){
            result.add(initAndGetFilter(alias));
        }
        return result;
    }

    private Filter initAndGetFilter(String alias) throws FilterNotFoundException, ClassNotFoundException, IllegalAccessException, InstantiationException {
        FilterHolder filterHolder = filters.get(alias);
        if(filterHolder == null){
            throw new FilterNotFoundException();
        }
        if(filterHolder.getFilter() == null){
            Filter filter = (Filter) Class.forName(filterHolder.getFilterClass()).newInstance();
            filter.init();
            filterHolder.setFilter(filter);
        }
        return filterHolder.getFilter();
    }

    /**
     * ServletContext 初始化方法。
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws ClassNotFoundException
     */
    private void init() throws IllegalAccessException, InstantiationException, ClassNotFoundException, DocumentException {
        this.servlets = new HashMap<>();
        this.servletMapping = new HashMap<>();
        this.filters = new HashMap<>();
        this.filterMapping = new HashMap<>();
        this.attributes = new ConcurrentHashMap<>();
        this.sessions = new ConcurrentHashMap<>();
        this.antPathMatcher = new AntPathMatcher();
        this.idleSessionCleaner = new IdleSessionCleaner();
        this.servletContextListeners = new ArrayList<>();
        this.servletRequestListeners = new ArrayList<>();
        this.httpSessionListeners = new ArrayList<>();
        ServletContextEvent servletContextEvent = new ServletContextEvent(this);
        parseConfig();
        for (ServletContextListener listener: servletContextListeners) {
            listener.servletContextInitialized(servletContextEvent);
        }
    }

    public void destroy(){
        servlets.values().forEach(servletHolder -> {
            if(servletHolder != null){
                servletHolder.getServlet().destory();
            }
        });
        filters.values().forEach(filterHolder -> {
            if(filterHolder != null){
                filterHolder.getFilter().destory();
            }
        });
        ServletContextEvent contextEvent = new ServletContextEvent(this);
        servletContextListeners.forEach(listener -> {
            listener.servletContextDestroy(contextEvent);
        });
    }

    /**
     * 解析web.xml
     */
    private void parseConfig() throws ClassNotFoundException, IllegalAccessException, InstantiationException, DocumentException {
        Document doc = XMLUtil.getDocument(ServletContext.class.getResourceAsStream("/web.xml"));
        Element root = doc.getRootElement();
        //解析servlet
        List<Element> servlets = root.elements("servlet");
        for(Element element : servlets){
            String key = element.element("servlet-name").getText();
            String value = element.element("servlet-class").getText();
            log.info("value : {} " , value);
            this.servlets.put(key, new ServletHolder(value));
        }

        List<Element> servletMapping = root.elements("servlet-mapping");
        servletMapping.forEach(b -> {
            List<Element> urlPattern = b.elements("url-pattern");
            String value = b.element("servlet-name").getText();
            log.info("servlet-name : {}", value);
            urlPattern.forEach(url ->{
                log.info("url-pattern init : {}", url.getText());
                this.servletMapping.put(url.getText(), value);
            });
        });

        // 解析filter
        List<Element> filters = root.elements("filter");
        filters.forEach(filter -> {
            String key = filter.element("filter-name").getText();
            String value = filter.element("filter-class").getText();
            this.filters.put(key, new FilterHolder(value));
        });

        List<Element> filterMapping = root.elements("filter-mapping");
        filterMapping.forEach(mapping -> {
            List<Element> urlPattens = mapping.elements("url-pattern");
            String value = mapping.element("filter-name").getText();
            urlPattens.forEach(url -> {
                List<String> values = this.filterMapping.get(url.getText());
                if(values == null){
                    values = new ArrayList<>();
                    this.filterMapping.put(url.getText(), values);
                }
                values.add(value);
            });
        });

        // 解析listener

        Element listener = root.element("listener");
        List<Element> listeners = listener.elements("listener-class");
        for(Element element : listeners){
            EventListener eventListener = (EventListener) Class.forName(element.getText()).newInstance();
            if(eventListener instanceof ServletContextListener){
                servletContextListeners.add((ServletContextListener) eventListener);
            }
            if(eventListener instanceof HttpSessionListener){
                httpSessionListeners.add((HttpSessionListener) eventListener);
            }
            if(eventListener instanceof ServletRequestListener){
                servletRequestListeners.add((ServletRequestListener) eventListener);
            }
        }
    }

    /**
     * 获取session
     * @param JSESSIONID
     * @return
     */
    public HttpSession getSession(String JSESSIONID){return sessions.get(JSESSIONID);}

    /**
     * 创建session
     * @param response
     * @return
     */
    public HttpSession createSession(Response response){
        HttpSession session = new HttpSession(UUIDUtil.uuid());
        sessions.put(session.getId(), session);
        response.addCookie(new Cookie("JSESSION", session.getId()));
        HttpSessionEvent httpSessionEvent = new HttpSessionEvent(session);
        for (HttpSessionListener listener : httpSessionListeners) {
            listener.createSession(httpSessionEvent);
        }
        return session;
    }

    /**
     * 销毁session
     * @param session
     */
    public void invalidateSession(HttpSession session){
        sessions.remove(session.getId());
        afterSessionDestroyed(session);
    }

    /**
     * 清除过期session
     */
    public void cleanIdSession(){
        for(Iterator<Map.Entry<String,HttpSession>> it = sessions.entrySet().iterator(); it.hasNext(); ){
            Map.Entry<String, HttpSession> entry = it.next();
            if(Duration.between(entry.getValue().getLastAccessed(), Instant.now()).getSeconds() >= ContextConstant.DEFAULT_SESSION_EXPIRE_TIME){
                afterSessionDestroyed(entry.getValue());
                it.remove();
            }
        }
    }

    /**
     * 清除监听器中的session
     * @param session
     */
    public void afterSessionDestroyed(HttpSession session){
        HttpSessionEvent httpSessionEvent = new HttpSessionEvent(session);
        for(HttpSessionListener listener : httpSessionListeners){
            listener.destorySessioni(httpSessionEvent);
        }
    }

    /**
     * 将创建的请求创建监听器
     * @param request
     */
    public void afterRequestCreate(Request request){
        ServletRequestEvent requestEvent = new ServletRequestEvent(this, request);
        for(ServletRequestListener listener : servletRequestListeners){
            listener.requestInitialized(requestEvent);
        }
    }

    /**
     * 将销毁后的请求从监听器中删除
     * @param request
     */
    public void afterRequestDestroyed(Request request){
        ServletRequestEvent requestEvent = new ServletRequestEvent(this, request);
        for(ServletRequestListener listener : servletRequestListeners){
            listener.requestDestroyed(requestEvent);
        }
    }
    public Object getAttribute(String key){
        return attributes.get(key);
    }
    public void setAttribute(String key, Object value){
        attributes.put(key, value);
    }
}
