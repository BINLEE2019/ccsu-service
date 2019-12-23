package com.hang.interceptors;

import com.alibaba.druid.util.StringUtils;
import com.hang.enums.ResultEnum;
import com.hang.utils.HeaderParser;
import com.hang.utils.RespUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @author hangs.zhang
 * @date 2018/11/20
 * *****************
 * function: 自定义检查请求头拦截器
 */
@Slf4j
@Component
public class HeaderCheckInterceptor extends HandlerInterceptorAdapter {

    @Value("${requiredHeaders}")
    private String requiredHeaders;


    /**
     * 预先处理器
     *
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     * @apiNote 在业务处理器处理请求也就是调用接口之前被调用，判断前端的请求头是否符合要求
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Map<String, String> headers = null;
        try {
            headers = HeaderParser.parse(request);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        if (null == headers || headers.size() == 0) {
            log.info("Request Intercepted: {}, Case: headers is empty", request.getRequestURI());
            RespUtil.render(response, ResultEnum.HEADERS_ERROR);
            return false;
        }
        String[] rqh = requiredHeaders.split(",");
        log.info("requiredHeaders: {}, conut: {}", requiredHeaders, rqh.length);
        for (String h : rqh) {
            String headerValue = headers.get(h.trim());
            if (StringUtils.isEmpty(headerValue)) {
                // 只要有一个必填项为空，则拦截请求
                String msg = String.format("Case: Header %s can not be empty", h);
                log.info(msg);
                RespUtil.render(response, ResultEnum.HEADERS_ERROR);
                return false;
            }
        }
        log.info("access interceptor");
        return true;
    }
}

