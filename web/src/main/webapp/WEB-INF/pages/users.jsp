<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="customtags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<customtags:pageTemplate>
    <html>
    <body>
    <div class="container col-md-12">
        <h2><spring:message code="users.table.title"/></h2>
        <table class="table table-striped">
            <thead>
            <tr>
                <th><spring:message code="users.table.col1"/></th>
                <th><spring:message code="users.table.col2"/></th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="user" items="${users}">
                <tr>
                    <td style="width:150px">
                            ${user.username}
                    </td>
                    <td style="width:200px">
                            ${user.email}
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </div>
    </body>
    </html>
</customtags:pageTemplate>
