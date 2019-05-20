package io.renren.common.interceptor;

import com.alibaba.fastjson.JSON;
import io.renren.common.annotation.RequestDataSign;
import io.renren.common.util.HttpUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 *
 */
@Slf4j
public class RequestDataSignInterceptor extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        String requestURI = request.getRequestURI();
        log.info("访问数据签名校验拦截处理开始：content-type[{}] method-type[{}] uri[{}] remoteAddr[{}:{}] url[{}].",
                request.getContentType(), request.getMethod(),
                requestURI, HttpUtils.getRemoteAddr(request), request.getRemotePort(), request.getRequestURL());

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        RequestDataSign methodAnnotation = handlerMethod.getMethodAnnotation(RequestDataSign.class);
        if (null != methodAnnotation) {
            if (MediaType.APPLICATION_JSON_VALUE.equalsIgnoreCase(request.getContentType())) {
                // TODO Json Handle
                // 避免出现 Required request body is missing 异常，在 XssHttpServletRequestWrapper 中处理
            } else {
                Map<String, String[]> parameterMap = request.getParameterMap();
                // TODO SIGN valid
                log.info("Sign Data:{}", JSON.toJSONString(parameterMap));
            }

        }


        // 为空，直接返回


        return true;
    }

}
