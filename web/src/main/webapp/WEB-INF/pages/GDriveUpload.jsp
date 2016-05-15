<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="customtags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<customtags:pageTemplate>
    <html>
    <body>
    <div th:if="${message}">
        <h2 th:text="${message}"/>
    </div>

    <div>
        <form method="POST" enctype="multipart/form-data" action="/GDriveUpload?${_csrf.parameterName}=${_csrf.token}">
            <table>
                <tr>
                    <td><spring:message code="GDrive.fileToUpload"/></td>
                    <td><input type="file" name="file"/></td>
                </tr>
                <tr>
                    <td></td>
                    <td><input type="submit" value="<spring:message code="GDrive.upload"/>"/></td>
                </tr>
            </table>
            <input type="hidden" name="parentId" value="${parentId}"/>
        </form>
    </div>

    <div>
        <h4>${progressReport}</h4>
    </div>
    </body>
    </html>
</customtags:pageTemplate>
