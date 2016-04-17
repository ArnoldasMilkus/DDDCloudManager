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
                <th>name</th>
                <th>pathDisplay</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="file" items="${files}">
                <tr>
                    <td style="width:150px">
                            ${file.name}
                    </td>
                    <td style="width:200px">
                            <a href="/files?path=${file.pathLower}">${file.pathDisplay}</a>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </div>
    </body>
    </html>
</customtags:pageTemplate>
