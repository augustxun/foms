package com.itheima.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter(filterName = "LoginCheckFilter", urlPatterns = "/*")
@Slf4j
public class LoginCheckFilter implements Filter {
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse resp = (HttpServletResponse) servletResponse;
        // 1. 获取本次请求的URI
        String requestURI = req.getRequestURI();
        // 2. 定义不需要处理的请求路径
        String[] urls = new String[]{
            "/employee/login",
            "/employee/logout",
            "/backend/**",
            "/front/**",
            "/common/**",
            "/user/sendMsg",
            "/user/login",
                "/doc.html",
                "/webjars/**",
                "/swagger-resources",
                "/v2/api-docs"
        };
        // 2.判断本次目标是否需要处理
        boolean check = check(urls, requestURI);
        // 3. 如果无需处理，直接放行
        if (check) {
            filterChain.doFilter(req, resp);
            return;
        }
        // 4-1 判断网页端登陆状态，如果已经登陆，则放行
        if (req.getSession().getAttribute("employee") != null) {
            Long empId = (Long) req.getSession().getAttribute("employee");
            BaseContext.setCurrentId(empId);
            // 放行
            filterChain.doFilter(req, resp);
            return;
        }
        // 4-1 判断移动端登陆状态，如果已经登陆，则放行
        if (req.getSession().getAttribute("user") != null) {
            Long userId = (Long) req.getSession().getAttribute("user");
            BaseContext.setCurrentId(userId);
            // 放行
            filterChain.doFilter(req, resp);
            return;
        }

        // 5. 如果未登录则返回NOTLOGIN结果
        resp.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
    }

    /**
     *
     * @param urls
     * @param requestURI
     * @return
     */
    public boolean check(String[] urls,  String requestURI) {
        for (String url : urls) {
            boolean match = PATH_MATCHER.match(url, requestURI);
            if (match) {
                return true;
            }
        }
        return false;
    }
}
