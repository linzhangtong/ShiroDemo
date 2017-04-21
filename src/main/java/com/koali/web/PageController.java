package com.koali.web;

import com.koali.service.UserService;
import org.apache.ibatis.annotations.Param;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.view.InternalResourceViewResolver;


/**
 * Created by Elric on 2017/4/20.
 */
@Controller
public class PageController {
    @Autowired
    private UserService userService;
    @RequestMapping("/")
    public String showIndex(){
        return "index";
    }
    @RequestMapping("/login")
    public String showAdmin(@Param("username") String username,@Param("password") String password){
        System.out.println("用户名密码:"+username+password);
        System.out.println("-------------------------------------------------------");

        UsernamePasswordToken token = new UsernamePasswordToken(username,password);
        token.setRememberMe(true);
        Subject currentUser = SecurityUtils.getSubject();
        try {
            currentUser.login(token);
        }catch (AuthenticationException e){
            e.printStackTrace();
        }
        //验证是否登录成功
        if(currentUser.isAuthenticated()){
            System.out.println("用户[" + username + "]登录认证通过（这里可进行一些认证通过后的系统参数初始化操作）");
            return "main";
        }else{
            token.clear();
            return InternalResourceViewResolver.FORWARD_URL_PREFIX + "/";
        }
    }
}
