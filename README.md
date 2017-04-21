Apache Shiro是一个安全框架，对于身份验证还有权限管理比较简单（一点都不简单）,尤其是在看了网上很多博客教程，各种坑，真不知那些人怎么想的，乱写代码就乱发。
本篇文章用的环境如下：
```javascript
1.Intellij IDEA 2017.1
2.JDK1.8
3.Maven3.0
```
****
这里是Shiro的是官方的地址（貌似是吧）https://github.com/apache/shiro
这里是我觉得写的最用心最不错的教程：http://blog.csdn.net/Angel_G/article/category/6655167
***
从我给的第一个官方的Shiro网址里我截取了一部分的配置文件，可以看出，Shiro相关的有用户名，密码，权限，还有许可。
```javacript
# user 'lonestarr' with password 'vespa' and roles 'goodguy' and 'schwartz'
lonestarr = vespa, goodguy, schwartz
# The 'schwartz' role can do anything (*) with any lightsaber:
schwartz = lightsaber:*
```
接下来就是在SSM基础上添加Shiro框架。
步骤1：
在Pom.xml添加依赖:
```javascript
<!--SSM依赖或者搭建请看我的文章《SSM个人相册》或者下面给予的Github地址-->
       <dependency>
			<groupId>org.apache.shiro</groupId>
			<artifactId>shiro-spring</artifactId>
			<version>1.2.2</version>
		</dependency>
```
在已经搭建完的SSM框架上，我们创建数据库还有表
```javascript
create database shiro;
use shiro;
CREATE TABLE `user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
<!--自行插入一条数据-->
```
然后写dao层，service层，web层（很简单就是查一条数据而已）。
***
步骤2：
整合Shiro安全框架
首先在web.xml添加Filter
```javascript
<!--Shiro过滤器搭建-->
    <filter>
        <filter-name>shiroFilter</filter-name>
        <filter-class>org.springframework.web.filter.DelegatingFilterProxy</filter-class>
        <init-param>
            <!-- 缺省为false，表示由SpringApplicationContext管理生命周期，置为true则表示由ServletContainer管理 -->
            <param-name>targetFilterLifecycle</param-name>
            <param-value>true</param-value>
        </init-param>
    </filter>
    <filter-mapping>
        <filter-name>shiroFilter</filter-name>
        <url-pattern>/*</url-pattern>
    </filter-mapping>
```
然后再spring-web.xml添加配置
```javascript
<bean id="securityManager" class="org.apache.shiro.web.mgt.DefaultWebSecurityManager">
        <!-- 指定Shiro验证用户登录的类为自定义的Realm（若有多个Realm，可使用[realms]属性代替） -->
        <property name="realm">
            <bean class="com.koali.realm.myrealm"/>
        </property>
        <!--
        Shiro默认会使用Servlet容器的Session，此时修改超时时间的话，可以修改web.xml或者这里自定义的MyRealm
        而若想使用Shiro原生Session则可以设置sessionMode属性为native，此时修改超时时间则只能修改MyRealm
        -->
        <!-- <property name="sessionMode" value="native"/> -->
    </bean>

    <!-- Shiro主过滤器本身功能十分强大，其强大之处就在于它支持任何基于URL路径表达式的、自定义的过滤器的执行 -->
    <!-- Web应用中，Shiro可控制的Web请求必须经过Shiro主过滤器的拦截，并且Shiro对基于Spring的Web应用提供了完美的支持 -->
    <bean id="shiroFilter" class="org.apache.shiro.spring.web.ShiroFilterFactoryBean">
        <!-- Shiro的核心安全接口，这个属性是必须的 -->
        <property name="securityManager" ref="securityManager"/>
        <!-- 要求登录时的链接（可根据项目的URL进行替换），非必须的属性，默认会找Web工程根目录下的[/login.jsp] -->
        <property name="loginUrl" value="/"/>
        <!-- 登录成功后要跳转的连接（本例中此属性用不到，因为登录成功后的处理逻辑已在LoginController中硬编码为main.jsp） -->
        <!-- <property name="successUrl" value="/system/main"/> -->
        <!--
        用户访问未授权的资源时，所显示的连接
        若想更明显的测试此属性可以修改它的值，比如unauthor.jsp
        然后用[xuanyu]登录后访问/admin/list.jsp就看见浏览器会显示unauthor.jsp
        -->
        <property name="unauthorizedUrl" value="/"/>
        <!--
        Shiro连接约束配置，即过滤链的定义
        更详细介绍，见本文最下方提供的Shiro-1.2.2内置的FilterChain说明
        下面value值的第一个'/'代表的路径是相对于HttpServletRequest.getContextPath()的值来的
        anon：它对应的过滤器里面是空的，什么都没做，另外.do和.jsp后面的*表示参数，比方说[login.jsp?main]这种
        authc：该过滤器下的页面必须验证后才能访问，它是Shiro内置的org.apache.shiro.web.filter.authc.FormAuthenticationFilter
        注意：对于类似资源，既有authc验证，也有anon允许匿名访问的情况下，需要将anon设置放在authc前面，才会生效
        -->
        <property name="filterChainDefinitions">
            <value>
                /admin/alist*=authc,perms[admin:manage]
               /user/ulist*=authc,perms[user:manage]
            </value>
        </property>
    </bean>

    <!-- 保证实现了Shiro内部lifecycle函数的bean执行 -->
    <!-- http://shiro.apache.org/static/1.2.1/apidocs/org/apache/shiro/spring/LifecycleBeanPostProcessor.html -->
    <bean id="lifecycleBeanPostProcessor" class="org.apache.shiro.spring.LifecycleBeanPostProcessor"/>
```
接下来写我们验证信息的Realm<strong><bean class="com.koali.realm.myrealm"/>
</strong>  
myrealm
```javascript
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
```
至此整合搭建完毕！
测试一：
输入错误的用户名或者密码

![user表的账号密码.png](http://upload-images.jianshu.io/upload_images/3435345-8e974e8451023d8b.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)


![测试一.gif](http://upload-images.jianshu.io/upload_images/3435345-debc6ddc4444c6d8.gif?imageMogr2/auto-orient/strip)
结果登陆不了
测试二：
输入正确的账号密码：

![测试二.gif](http://upload-images.jianshu.io/upload_images/3435345-22137f5ad4f8417c.gif?imageMogr2/auto-orient/strip)
登陆成功！
由于在MyRealm中我们给用户露娜添加了
```javascript
        / /添加权限
        simpleAuthorInfo.addStringPermission("admin:manage");
//然后我们在spring-web.xml中添加资源的访问是这样子的
<property name="filterChainDefinitions">
            <value>
                /admin/alist*=authc,perms[admin:manage]
               /user/ulist*=authc,perms[user:manage]
            </value>
        </property>
```
也就是说明我们的用户露娜所拥有的权限是（admin:manage），他可以访问/admin/alist，而不能访问user/ulist。
测试三：


![测试三.gif](http://upload-images.jianshu.io/upload_images/3435345-902aec1aaa331e42.gif?imageMogr2/auto-orient/strip)

结果是用户露娜真的可以访问admin，不能访问user/ulist。权限控制启到作用。
