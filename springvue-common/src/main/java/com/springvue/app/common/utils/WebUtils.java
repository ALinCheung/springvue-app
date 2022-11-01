package com.springvue.app.common.utils;

import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.util.Assert;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;


/**
 * Web应用工具类
 */
@Slf4j
public class WebUtils extends org.springframework.web.util.WebUtils {

    private static final String UNKNOWN = "unknown";

    /**
     * 获取 HttpServletRequest
     */
    public static HttpServletRequest getRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    }

    /**
     * 获取 HttpServletResponse
     */
    public static HttpServletResponse getResponse() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
    }

    /**
     * 获取主机
     */
    public static String getHost(String url) {
        String host = url.replaceAll("(http|https)://", "").split("/", -1)[0];
        return host;
    }

    /**
     * 获取域名
     */
    public static String getDomain() {
        HttpServletRequest request = getRequest();
        return request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
    }

    /**
     * 获取ip
     */
    public static String getIP(HttpServletRequest request) {
        Assert.notNull(request, "HttpServletRequest is null");
        String ip = request.getHeader("X-Requested-For");
        if (StringUtils.isBlank(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Forwarded-For");
        }
        if (StringUtils.isBlank(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (StringUtils.isBlank(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (StringUtils.isBlank(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (StringUtils.isBlank(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (StringUtils.isBlank(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return StringUtils.isBlank(ip) ? null : ip.split(",")[0];
    }

    /**
     * 获取token
     */
    public static String extractToken(HttpServletRequest request) {
        // 从请求头获取token
        String token = extractHeaderToken(request);
        if (token == null) {
            // 从请求参数获取token
            token = request.getParameter("access_token");
        }
        return token;
    }

    /**
     * 从请求头里面获取access_token
     */
    private static String extractHeaderToken(HttpServletRequest request) {
        Enumeration<String> headers = request.getHeaders("Authorization");
        while (headers.hasMoreElements()) {
            String value = headers.nextElement();
            if ((value.toLowerCase().startsWith("Bearer".toLowerCase()))) {
                String authHeaderValue = value.substring("Bearer".length()).trim();
                int commaIndex = authHeaderValue.indexOf(',');
                if (commaIndex > 0) {
                    authHeaderValue = authHeaderValue.substring(0, commaIndex);
                }
                return authHeaderValue;
            }
        }
        return null;
    }

    /**
     * 返回json
     */
    public static void renderJson(HttpServletResponse response, Object result) {
        printResponse(response, JSONUtil.toJsonStr(result), MediaType.APPLICATION_JSON_UTF8_VALUE);
    }

    /**
     * 返回前台顶级跳转
     */
    public static void windowTopRedirect(HttpServletResponse response, String url) {
        printResponse(response, "<script type=\"text/javascript\">window.top.location.href = '" + url + "'</script>", MediaType.TEXT_HTML_VALUE);
    }

    /**
     * 返回响应
     */
    public static void printResponse(HttpServletResponse response, String result, String contentType) {
        response.setCharacterEncoding("UTF-8");
        response.setContentType(contentType);
        try (PrintWriter out = response.getWriter()) {
            out.append(result);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 将请求参数封装到对象里
     */
    public static <T> T setRequestParam(HttpServletRequest request, T object) {
        try {
            Field[] fields = object.getClass().getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                String value = request.getParameter(field.getName());
                if (value != null && !"".equals(value) && !"null".equals(value)) {
                    if (field.getType() == Boolean.class) {
                        field.set(object, Boolean.parseBoolean(value));
                    } else {
                        field.set(object, value.trim());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 将对象参数拼接成地址参数
     */
    public static String getUrlParam(Object object) {
        List<String> list = new ArrayList<>();
        try {
            Field[] fields = object.getClass().getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                Object o = field.get(object);
                if (o != null) {
                    String s = o.toString();
                    if (!"".equals(s)) {
                        list.add(field.getName() + "=" + s);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return StringUtils.join(list, "&");
    }
}

