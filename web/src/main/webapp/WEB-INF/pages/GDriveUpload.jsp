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
                    <td>File to upload:</td>
                    <td><input type="file" name="file"/></td>
                </tr>
                <tr>
                    <td></td>
                    <td><input type="submit" value="Upload"/></td>
                </tr>
            </table>
            <input type="hidden" name="parentId" value="${parentId}"/>
        </form>
    </div>

    <div>
        <ul>
            <li th:each="file : ${files}" th:text="${file}"></li>
        </ul>
    </div>
    </body>
    </html>
</customtags:pageTemplate>
