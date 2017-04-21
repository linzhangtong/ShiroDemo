<%@ page language="java" pageEncoding="UTF-8"%>

<form action="${pageContext.request.contextPath}/login" method="POST">
    姓名：<input type="text" name="username"/><br/>
    密码：<input type="text" name="password"/><br/>
    <input type="submit"/>
</form>
