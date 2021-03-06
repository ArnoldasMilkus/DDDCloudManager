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
        <h2><spring:message code="dbxfiles.deletedfiles"/><span class="pull-right">${spaceUsage}</span></h2>
        <fielset>
            <legend>
                <a href="/dbx/files?path="><spring:message code="dbxfiles.home"/></a>
                <c:forTokens var="folder" items="${currentPath}" delims="/">
                    <c:set var="varPath" value="${varPath}/${folder}"/>
                    > <a href="/dbx/files?path=${varPath}">${folder}</a>
                </c:forTokens>
            </legend>
        <table class="table table-bordered" style="background-color:whitesmoke">
            <thead>
            <tr>
                <th><spring:message code="dbxfiles.table.col1"/></th>
                <th><spring:message code="dbxfiles.table.col5"/></th>
                <th><spring:message code="dbxfiles.table.col4"/></th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="file" items="${files}">
                <c:url var="restoreUrl" value="/dbx/restore">
                    <c:param name="path" value="${file.pathLower}"/>
                </c:url>
                <tr>
                    <td style="width:auto">
                            ${file.name}
                    </td>
                    <td style="width:auto">
                            ${file.pathDisplay}
                    </td>
                    <td style="width:auto">
                        <a href="${restoreUrl}" title="<spring:message code="dbxfiles.restoreButton"/>"><span
                                class="glyphicon glyphicon-export"></span></a>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </div>
    </body>
    </html>
</customtags:pageTemplate>
