package com.koali.realm;

import com.koali.pojo.User;
import com.koali.service.UserService;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by Elric on 2017/4/20.
 */
public class myrealm extends AuthorizingRealm {
    @Autowired
    private UserService userService;

    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        String currentUsername = (String)super.getAvailablePrincipal(principalCollection);
        System.out.println("-----------------------doGetAuthorizationInfo----------------------");
        System.out.println("当前名字:"+currentUsername);
        SimpleAuthorizationInfo simpleAuthorInfo = new SimpleAuthorizationInfo();
        simpleAuthorInfo.addRole("admin");
        //添加权限
        simpleAuthorInfo.addStringPermission("admin:manage");
        return simpleAuthorInfo;
    }

    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        UsernamePasswordToken token = (UsernamePasswordToken) authenticationToken;
        System.out.print("验证当前Subject时获取到token：");
        System.out.println(ReflectionToStringBuilder.toString(token, ToStringStyle.MULTI_LINE_STYLE));
        User user = userService.selectUserByNameService(token.getUsername());
        String password = new String((char[])token.getCredentials());
        System.out.println("--------------密码是:------"+password);
        if (user.getPassword().equals(password)) {
            AuthenticationInfo authcInfo = new SimpleAuthenticationInfo(user.getUsername(), user.getPassword(), this.getName());
            this.setAuthenticationSession(user.getUsername());
            return authcInfo;
        }
        return null;
    }

    private void setAuthenticationSession(Object value) {
        Subject currentUser = SecurityUtils.getSubject();
        if (null != currentUser) {
            Session session = currentUser.getSession();
            System.out.println("当前Session超时时间为[" + session.getTimeout() + "]毫秒");
            session.setTimeout(1000 * 60 * 60 * 2);
            System.out.println("修改Session超时时间为[" + session.getTimeout() + "]毫秒");
            session.setAttribute("currentUser", value);
        }
    }
}
