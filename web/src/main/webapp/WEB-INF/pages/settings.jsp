<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="customtags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<customtags:pageTemplate>
    <html>
    <body>
    <div class="container">
        <sec:authentication var="user" property="principal"/>
        <h2>${user.username}</h2>
        <c:if test="${dbxAuth eq false}">
            <form name="authForm"
                  action="<c:url value="/dbx-auth-start" />" method='POST'>
                <input type="submit" style="height:30px; width:245px" value="Link dropbox account"/>

                <input type="hidden"
                       name="${_csrf.parameterName}" value="${_csrf.token}"/>
            </form>
        </c:if>
        <c:if test="${dbxAuth eq true}">
            <form name="authForm"
                  action="<c:url value="/dbx-auth-clear" />" method='POST'>
                <input type="submit" style="height:30px; width:245px" value="Unlink dropbox account"/>

                <input type="hidden"
                       name="${_csrf.parameterName}" value="${_csrf.token}"/>
            </form>
        </c:if>
    </div>
    </body>
    </html>
</customtags:pageTemplate>
