package com.filter;

import com.alibaba.fastjson.JSON;
import com.common.BaseContext;
import com.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 使用javaweb的过滤器检查用户是否登录
 */
@Slf4j
@WebFilter(filterName = "loginCheckFilter",urlPatterns = "/*")
public class LoginCheckFilter implements Filter {
    //创建一个路径匹配器，支持通配符
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        //先把ServletRequest，ServletResponse强转成HttpServletRequest，HttpServletResponse，方便使用
        HttpServletRequest request=(HttpServletRequest) servletRequest;
        HttpServletResponse response=(HttpServletResponse) servletResponse;

        //1、获取本次请求的URI
        String requestURI = request.getRequestURI();//获取本次请求的地址
        //创建一个数组里面存放不需要拦截的资源路径
        String[] urls = new String[]{
                "/employee/login",//登录请求
                "/employee/logout",//退出请求
                "/backend/**",//静态资源
                "/front/**",//静态资源
                "/user/sendMsg",//移动端发送短信
                "/user/login"//移动端登录
        };
        //2、判断本次请求是否需要处理
        Boolean check = check(urls, requestURI);
        //3、如果不需要处理，则直接放行
        if (check){
            log.info("放行了");
            filterChain.doFilter(request,response);
            return;
        }
        //4、判断登录状态，如果已登录，则直接放行(以session是否为空为判断标准)
        if (request.getSession().getAttribute("employee")!=null){
            log.info("已经登录了");
            Long employeeId =(Long)request.getSession().getAttribute("employee");//获取当前登录人id
            BaseContext.setCurrentId(employeeId);//把id传入ThreadLocal中这样MyMetaObjectHandler类里面就可以读取登录人id，这个和登录业务没有关系只是用来存登录用户id
            filterChain.doFilter(request,response);
            return;
        }

        //4、判断登录状态，如果已登录，则直接放行(以session是否为空为判断标准)
        if (request.getSession().getAttribute("user")!=null){
            log.info("已经登录了");
            Long userId =(Long)request.getSession().getAttribute("user");//获取当前登录人id
            BaseContext.setCurrentId(userId);//把id传入ThreadLocal中这样MyMetaObjectHandler类里面就可以读取登录人id，这个和登录业务没有关系只是用来存登录用户id
            filterChain.doFilter(request,response);
            return;
        }

        //5、如果未登录则返回未登录结果，通过输出流方式向客户端页面响应数据
        log.info("未登录，拦截了");
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;
    }
    public Boolean check(String[] urls, String requestURI){
        for (String url : urls) {
            boolean match = PATH_MATCHER.match(url, requestURI);
            if (match){
                return true;
            }
        }
        return false;
    }

}
