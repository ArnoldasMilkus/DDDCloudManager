<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="customtags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<customtags:pageTemplate>
    <c:set var="test" value="folder"/>
    <html>
    <body>
    <div class="container col-md-12">
        <h2><a href="/dbx/files?path=">home</a>${currentPath}/</h2> <br/>
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
            <table class="table table-bordered" style="background-color:whitesmoke">
                <thead>
                <tr>
                    <th><spring:message code="dbxfiles.table.col1"/></th>
                    <th><spring:message code="dbxfiles.table.col2"/></th>
                </tr>
                </thead>
                <tbody>
                <c:set var="folder_tag" value="folder"></c:set>
                <c:forEach var="file" items="${files}">
                    <c:set var="file_tag" value="${fn:substring(file, 1, 16)}"/>
                    <tr>
                        <td style="width:150px">
                            <c:if test="${fn:contains(file_tag, folder_tag)}">
                                <a href="/dbx/files?path=${file.pathLower}">${file.name}</a>
                            </c:if>
                            <c:if test="${!fn:contains(file_tag, folder_tag)}">
                                ${file.name}
                            </c:if>
                        </td>
                        <td style="width:150px">
                                ${file.pathDisplay}
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
            <form method="POST" enctype="multipart/form-data" action=
                    <c:url value="/dbx/upload?path=${currentPath}&${_csrf.parameterName}=${_csrf.token}"/>>
                <table style="background-color:whitesmoke">
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
            <br/>

            <form name="authForm"
                  action="<c:url value="/dbx/auth-clear" />" method='POST'>
                <input type="submit" value=<spring:message code="dbxfiles.unlinkbutton"/>/>
                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
            </form>
        </c:if>
    </div>
    </body>
    </html>
</customtags:pageTemplate>
