package com.hx.imageserver.config;

import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Description 跨域配置
 * @Author AN
 * @Date 2019-06-11
 **/
@Component
public class CorsFilter implements Filter {
    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
//        System.out.println("请求进来了，请求名称：" + request.getRequestURL());
        HttpServletResponse response = (HttpServletResponse) res;
       /* String curOrigin = request.getHeader("Origin");
        System.out.println("###跨域过滤器->当前访问来源->"+curOrigin+"###");   */
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "*");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers", "x-requested-with");
        chain.doFilter(req, res);
    }

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void destroy() {
    }
}
