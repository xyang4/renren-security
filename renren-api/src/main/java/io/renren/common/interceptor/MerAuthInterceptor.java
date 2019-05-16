package io.renren.common.interceptor;

import io.renren.common.util.HttpUtils;
import io.renren.common.util.StaticConstant;
import io.renren.common.utils.SpringContextUtils;
import io.renren.modules.mer.service.MerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * mer 拦截处理
 */
@Slf4j
public class MerAuthInterceptor extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String requestURI = request.getRequestURI();
        log.info("Mer商户鉴权拦截处理开始：content-type[{}] method-type[{}] uri[{}] remoteAddr[{}:{}] url[{}].",
                request.getContentType(), request.getMethod(),
                requestURI, HttpUtils.getRemoteAddr(request), request.getRemotePort(), request.getRequestURL());

        // TODO mer 商户接口请求过滤 公共校验
        String timeStamp = request.getParameter(StaticConstant.TIMESTAMP_KEY);
        Integer merId = Integer.parseInt(request.getParameter(StaticConstant.MER_KEY));
        String sign = request.getParameter(StaticConstant.SIGN_KEY);
        //校验时间戳，链接请求5分钟有效 TODO
        MerService merService = SpringContextUtils.getBean(MerService.class);
        //商户有效性,可从缓存中获取商户状态 TODO 改进
        boolean checkMer = merService.checkMer(merId);
        // TODO 临时不校验
//        if (checkMer == false) {
//            return false;
//        }
        //校验签名sign 从缓存中获取签名key? TODO

        return true;
    }

}
