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
                background: #fff;
            }
        </style>
    </head>

    <div>
        <form name="logoutForm"
              action="<c:url value="/logout" />" method="POST">

            <table>
                <tr>
                    Are you sure you want to log you
                </tr>
                <tr>
                    <td>
                        <input name="submit" type="submit" value=
                            <spring:message code="login.submit"/>/>
                    </td>
                </tr>
            </table>

            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
        </form>
    </div>

    </body>
    </html>
</customtags:pageTemplate>
