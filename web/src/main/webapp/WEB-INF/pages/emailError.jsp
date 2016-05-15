<%--
  Created by IntelliJ IDEA.
  User: Arnoldas
  Date: 2016-05-14
  Time: 15:16
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="customtags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<customtags:pageTemplate>
    <fmt:setBundle basename="messages" />
    <%@ page session="true"%>
    <html>
    <head>
        <link href="<c:url value="/resources/bootstrap/css/bootstrap.css" />" rel="stylesheet">
    </head>
    <body>
    <h1><spring:message code="register.emailSendError"></spring:message></h1>
    <br>

    </body>
    </html>
</customtags:pageTemplate>

