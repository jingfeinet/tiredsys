package com.sicau.tiredsys.filter;

import com.sicau.tiredsys.common.Const;
import com.sicau.tiredsys.config.JwtToken;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.web.filter.authc.BasicHttpAuthenticationFilter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//import javax.security.sasl.AuthenticationException;

/**
 * Created by zhong  on 2019/3/14 13:42
 */
//@Component
public class ShiroFilter extends BasicHttpAuthenticationFilter {


    /**
     * 判断用户是否想要登入。
     * 检测header里面是否包含Authorization字段即可
     */
    @Override
    protected boolean isLoginAttempt(ServletRequest request, ServletResponse response) {
        System.out.println("判断");
        HttpServletRequest req = (HttpServletRequest) request;
        String authorization = req.getHeader(Const.AUTH_NAME);
        if(authorization==null){
            authorization = req.getParameter(Const.AUTH_NAME);
            //if (authorization==null) throw new AuthenticationException("没有携带token");
        }
        return authorization != null;
    }

    /**
     * 执行登录认证
     *
     * @param request
     * @param response
     * @param mappedValue
     * @return
     */
    @Override
    public boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {

        System.out.println("是否允许");
        if (isLoginAttempt(request, response)) {
            try {
                executeLogin(request, response);

            } catch (Exception e) {
                return false;
            }
        }
        return true;
    }


    /**
     * 会话超时或权限校验未通过的，统一返回401，由前端页面弹窗提示
     */
//    @Override
//    protected boolean onAccessDenied(ServletRequest servletRequest,
//                                     ServletResponse servletResponse)throws Exception  {
//        throw new UnauthorizedException("操作授权失败！");
//    }
    /**
     *
     */
    @Override
    protected boolean executeLogin(ServletRequest request, ServletResponse response) throws AuthenticationException{

        System.out.println("登录");

        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String token = httpServletRequest.getHeader(Const.AUTH_NAME);
        if(token==null){
            token = httpServletRequest.getParameter(Const.AUTH_NAME);
        }
        JwtToken jwtToken = new JwtToken(token);
        try{
            getSubject(request, response).login(jwtToken);
        }catch (AuthenticationException e){
            throw e;
            // 提交给realm进行登入，如果错误他会抛出异常并被捕获
        }



        // 如果没有抛出异常则代表登入成功，返回true
        return true;
    }



    /**
     * 对跨域提供支持
     */
    @Override
    protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {

        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        //httpServletResponse.setHeader("Access-control-Allow-Origin","*");
        httpServletResponse.setHeader("Access-control-Allow-Origin", httpServletRequest.getHeader("Origin"));
        httpServletResponse.setHeader("Access-Control-Allow-Methods", "GET,POST,OPTIONS,PUT,DELETE");
        httpServletResponse.setHeader("Access-Control-Allow-Headers", "token,Origin, X-Requested-With, Content-Type,status,Accept,Content-Disposition");
        httpServletResponse.setHeader("Access-Control-Expose-Headers","token,Content-Disposition,status");
        httpServletResponse.setHeader("Access-Control-Allow-Headers", httpServletRequest.getHeader("Access-Control-Request-Headers"));
        // 跨域时会首先发送一个option请求，这里我们给option请求直接返回正常状态
        if (httpServletRequest.getMethod().equals(RequestMethod.OPTIONS.name())) {
            httpServletResponse.setStatus(HttpStatus.OK.value());
            return false;
        }
        return super.preHandle(request, response);
    }

}
