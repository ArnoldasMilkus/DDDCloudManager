<%--
  Created by IntelliJ IDEA.
  User: Arnoldas
  Date: 2016-04-19
  Time: 01:00
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="customtags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<customtags:pageTemplate>
    <html>
    <body>
    <div class="container">
        <em><spring:message code="register.success"/></em>
    </div>
    </body>
    </html>
</customtags:pageTemplate>
