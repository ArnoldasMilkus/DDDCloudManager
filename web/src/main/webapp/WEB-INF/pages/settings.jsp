<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="customtags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<customtags:pageTemplate>
    <html>
    <body>
    <div class="container">
        <sec:authentication var="user" property="principal" />
        <h2>${user.username}</h2>
    </div>
    </body>
    </html>
</customtags:pageTemplate>
