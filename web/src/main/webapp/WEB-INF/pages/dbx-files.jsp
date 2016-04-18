<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="customtags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<customtags:pageTemplate>
    <html>
    <body>

    <h2>${currentPath}</h2>
    <div class="container col-md-12">
        <table class="table table-bordered">
            <thead>
            <tr>
                <th><spring:message code="dbxfiles.table.col1"/></th>
                <th><spring:message code="dbxfiles.table.col2"/></th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="file" items="${files}">
                <tr>
                    <td style="width:150px">
                        <a href="/dbx/files?path=${file.pathLower}">${file.name}</a>
                    </td>
                    <td style="width:200px">
                            ${file}
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </div>
    <c:if test="${dbxAuth eq false}">
        <form name="authForm"
              action="<c:url value="/dbx/auth-start" />" method='POST'>
            <input type="submit" style="height:30px; width:245px" value=<spring:message
                    code="dbxfiles.linkbutton"/>/>

            <input type="hidden"
                   name="${_csrf.parameterName}" value="${_csrf.token}"/>
        </form>
    </c:if>
    <c:if test="${dbxAuth eq true}">
        <div class="container">
            <form method="POST" enctype="multipart/form-data" action=
                <c:url value="/dbx/upload?path=${currentPath}&${_csrf.parameterName}=${_csrf.token}"/> >
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
            </form>
        </div>

        <div class="container">
            <form name="authForm"
                  action="<c:url value="/dbx/auth-clear" />" method='POST'>
                <input type="submit" style="height:30px; width:245px" value=<spring:message
                        code="dbxfiles.unlinkbutton"/>/>
                <input type="hidden"
                       name="${_csrf.parameterName}" value="${_csrf.token}"/>
            </form>
        </div>
    </c:if>
    </body>
    </html>
</customtags:pageTemplate>
