package io.renren.common.interceptor;

import io.renren.common.annotation.RequestDataSign;
import io.renren.common.util.HttpUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 */
@Slf4j
public class RequestDataSignInterceptor extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        //TODO content-type:application/json 的在WithSignMessageConverter中处理
/*        String requestURI = request.getRequestURI();
        log.info("数据签名校验处理开始：content-type[{}] method-type[{}] uri[{}] remoteAddr[{}:{}] url[{}].",
                request.getContentType(), request.getMethod(),
                requestURI, HttpUtils.getRemoteAddr(request), request.getRemotePort(), request.getRequestURL());
//        ResourceHttpRequestHandler
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            RequestDataSign methodAnnotation = handlerMethod.getMethodAnnotation(RequestDataSign.class);
            if (null != methodAnnotation) {
                if (MediaType.APPLICATION_JSON_VALUE.equalsIgnoreCase(request.getContentType())) {
                    // 挪至 WithSignMessageConverter | XssHttpServletRequestWrapper 中处理 ,避免出现 Required request body is missing 异常，在  中处理
                } else {

                }

            }
        } else if (handler instanceof ResourceHttpRequestHandler) {
            ResourceHttpRequestHandler handlerMethod = (ResourceHttpRequestHandler) handler;

        }*/

        return true;
    }

}
