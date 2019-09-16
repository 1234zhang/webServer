package com.xcc.server.core.template;

import com.xcc.server.core.exception.TemplateResolveException;
import com.xcc.server.core.request.Request;
import com.xcc.server.core.statusenum.ModelScope;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Brandon.
 * @date 2019/9/8.
 * @time 18:27.
 * 模板引擎，基于正则表达式的替换
 * ${a,b,c} 就可以解析为a.getB().getC() ,并将值填充至占位符
 */

@Slf4j
public class TemplateResolver {
    public static final Pattern regex = Pattern.compile("\\$\\{(.*?)}");

    public static String resolve (String content, Request request) throws TemplateResolveException {
        Matcher matcher = regex.matcher(content);
        StringBuffer buffer = new StringBuffer();
        while (matcher.find()) {
            log.info("{}", matcher.group(1));
            // placeHolder 格式为scope.x.y.x
            // scope值为requestScope，sessionScope, applicationScope
            String placeHolder = matcher.group(1);
            if (placeHolder.indexOf('.') == -1) {
                throw new TemplateResolveException();
            }
            ModelScope scope = ModelScope.
                    valueOf(placeHolder
                            .substring(0, placeHolder.indexOf('.'))
                            .replace("scope", "")
                            .toUpperCase());
            // key的形式为 x.y.z
            String key = placeHolder.substring(placeHolder.indexOf('.') + 1);
            if (scope == null) {
                throw new TemplateResolveException();
            }
            Object value = null;
            //将x.y.z分割成[x,y,z]
            String[] segment = key.split("\\.");
            log.info("key: {}, segment: {}", key, Arrays.toString(segment));
            switch (scope) {
                case REQUEST:
                    value = request.getAttributes(segment[0]);
                    break;
                case APPLICATION:
                    value = request.getServletContext().getAttribute(segment[0]);
                    break;
                case SESSION:
                    value = request.getSession().getAttribute(segment[0]);
                default:
                    break;
            }
            // 此时value 是x， 如果没有y,z 就直接返回，如果有就递归的进行属性的读取(基于反射)
            if (segment.length > 1) {
                try {
                    value = parse(value, segment, 1);
                } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                    log.error(e.getMessage());
                    throw new TemplateResolveException();
                }
            }
            log.info("value:{}", value);
            // 如果解析到的值是空，则将占位符去掉，否则将占位符替换为值
            if (value == null) {
                matcher.appendReplacement(buffer, "");
            } else {
                // 把group(1)得到的数据，替换为value;
                matcher.appendReplacement(buffer, value.toString());
            }
        }
        matcher.appendTail(buffer);
        String result = buffer.toString();
        return result.length() == 0 ? content : result;
    }

    private static Object parse(Object value, String[] segment, int index) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        if(index == segment.length){
            return value;
        }
        Method method = value.getClass().getMethod("get" + StringUtils.capitalize(segment[index]), new Class[0]);
        // TODO 这里反射需要总结
        return parse(method.invoke(value, new Object[0]), segment, index + 1);
    }
}
