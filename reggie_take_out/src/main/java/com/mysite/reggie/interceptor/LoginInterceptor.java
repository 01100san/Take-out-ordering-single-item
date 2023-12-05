package com.mysite.reggie.interceptor;

import com.alibaba.fastjson.JSON;
import com.mysite.reggie.common.BaseContext;
import com.mysite.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

/**
 * ClassName: LoginInterceptor
 * Package: com.mysite.reggie.interceptor
 * Description
 *
 * @Author zhl
 * @Create 2023/11/22 11:14
 * version 1.0
 */
@Slf4j
public class LoginInterceptor implements HandlerInterceptor {
    /*public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();*/
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //如果管理端用户已登录，放行
        if (request.getSession().getAttribute("employee") != null) {
            log.info("管理端员工已登录,员工id为：{}", request.getSession().getAttribute("employee"));
            Long id = (Long) request.getSession().getAttribute("employee");
            BaseContext.setCurrentId(id);
            return true;
        }
        //如果移动端用户已登录，放行
        if (request.getSession().getAttribute("user") != null) {
            log.info("移动端用户已登录,用户id为：{}", request.getSession().getAttribute("user"));
            Long userId = (Long) request.getSession().getAttribute("user");
            BaseContext.setCurrentId(userId);
            return true;
        }
        //用户未登录，向前端响应数据
        log.info("用户未登录");
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return false;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
    }
}
