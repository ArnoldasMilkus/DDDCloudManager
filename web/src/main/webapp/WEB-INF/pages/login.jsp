<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="customtags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<customtags:pageTemplate>
    <html>
    <head>
        <style>
            #login-form {
                width: 300px;
                padding: 20px;
                margin: 100px auto;
            }
        </style>
    </head>

    <body>
    <div id="login-form">

        <c:if test="${not empty error}">
            <label>${error}</label>
        </c:if>
        <c:if test="${not empty message}">
            <label>${message}</label>
        </c:if>

        <form name="loginForm"
              action="<c:url value="/login" />" method='POST'>

            <table>
                <tr>
                    <td><spring:message code="login.username"/></td>
                    <td><input type="text" name="username" value=""></td>
                </tr>
                <tr>
                    <td><spring:message code="login.password"/></td>
                    <td><input type="password" name="password"/></td>
                </tr>
            </table>
            <input type="submit" style="height:30px; width:278px; font-weight:bold; color:#337AB7" value="<spring:message code="login.submit"/>"/>
            <input type="hidden"
                   name="${_csrf.parameterName}" value="${_csrf.token}"/>
        </form>
            <c:if test="${param.error != null}">
            <div class="alert alert-danger">
                    <spring:message code="login.accountNotVerified"></spring:message>
            </div>
            </c:if>
            <div class="container" style="max-width: 1000px">
                <br>
                <a href="/registration"><spring:message code="login.registration"/></a>
            </div>
    </body>
    </html>
</customtags:pageTemplate>
