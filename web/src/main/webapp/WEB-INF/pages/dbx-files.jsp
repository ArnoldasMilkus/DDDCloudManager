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
        <h3 style="background-color: whitesmoke">
            <a href="/dbx/files?path="><spring:message code="dbxfiles.home"/></a>
            <c:forTokens var="folder" items="${currentPath}" delims="/">
                <c:set var="varPath" value="${varPath}/${folder}"/>
                > <a href="/dbx/files?path=${varPath}">${folder}</a>
            </c:forTokens>
        </h3>
        <a href="/dbx/trash">Trash</a>

        <table class="table table-bordered" style="background-color:whitesmoke">
            <thead>
            <tr>
                <th><spring:message code="dbxfiles.table.col1"/></th>
                <th><spring:message code="dbxfiles.table.col2"/></th>
                <th><spring:message code="dbxfiles.table.col3"/></th>
                <th><spring:message code="dbxfiles.table.col4"/></th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="folder" items="${folders}">
                <c:url var="folderUrl" value="/dbx/files">
                    <c:param name="path" value="${folder.pathLower}"/>
                </c:url>
                <c:url var="deleteUrl" value="/dbx/delete">
                    <c:param name="path" value="${folder.pathLower}"/>
                </c:url>
                <tr>
                    <td style="width:auto">
                        <a href="${folderUrl}"> ${folder.name}</a>
                    </td>
                    <td style="width:auto">
                        --
                    </td>
                    <td style="width:auto">
                        --
                    </td>
                    <td style="width:auto">
                        <a href="${deleteUrl}" title="<spring:message code="dbxfiles.removeButton"/>"><span
                                class="glyphicon glyphicon-trash"></span></a>
                    </td>
                </tr>
            </c:forEach>
            <c:forEach var="file" items="${files}">
                <c:url var="deleteUrl" value="/dbx/delete">
                    <c:param name="path" value="${file.pathLower}"/>
                </c:url>
                <c:url var="moveToGDUrl" value="/dbx/copy/gd">
                    <c:param name="path" value="${file.pathLower}"/>
                </c:url>
                <c:url var="downloadUrl" value="/dbx/download">
                    <c:param name="path" value="${file.pathLower}"/>
                </c:url>
                <tr>
                    <td style="width:auto">
                            ${file.name}
                    </td>
                    <td style="width:auto">
                            ${file.clientModified}
                    </td>
                    <td style="width:auto">
                            ${file.size}
                    </td>
                    <td title="<spring:message code="dbxfiles.downloadButton"/>" style="width:auto">
                        <a  href="${downloadUrl}"><span
                                class="glyphicon glyphicon-download-alt"></span></a>
                        <a title="<spring:message code="dbxfiles.removeButton"/>" href="${deleteUrl}"><span
                                class="glyphicon glyphicon-trash"></span></a>
                        <a title="<spring:message code="dbxfiles.removeButton"/>" href="${moveToGDUrl}"><span
                                class="glyphicon glyphicon-cloud-upload"></span></a>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
        <div class="container-fluid">
            <div class="row">
                <div class="col-md-6">
                    <c:url var="url" value="/dbx/upload">
                        <c:param name="path" value="${currentPath}"/>
                        <c:param name="${_csrf.parameterName}" value="${_csrf.token}"/>
                    </c:url>
                    <form method="POST" enctype="multipart/form-data" action=${url}>
                        <table style="background-color:whitesmoke">
                            <tr>
                                <td><spring:message code="dbxfiles.uploadform.chosen"/></td>
                                <td><input type="file" name="file"/></td>
                            </tr>
                            <tr>
                                <td></td>
                                <td><input type="submit" value="<spring:message code="dbxfiles.uploadform.submit"/>"/>
                                </td>
                            </tr>
                        </table>
                    </form>
                </div>
                <div class="col-md-6">
                    <form method="POST" action=
                            <c:url value="/dbx/create"/>>
                        <table style="background-color:whitesmoke">
                            <tr>
                                <td>Name:</td>
                                <td><input type="text" name="name" value=""/></td>
                            </tr>
                            <tr>
                                <td></td>
                                <td><input type="submit" value="<spring:message code="dbxfiles.createfolder.submit"/>"/>
                                </td>
                            </tr>
                            <input type="hidden"
                                   name="${_csrf.parameterName}" value="${_csrf.token}"/>
                            <input type="hidden"
                                   name="path" value="${currentPath}"/>
                        </table>
                    </form>

                </div>
            </div>
        </div>
        <br/>
        <form name="authForm"
              action="<c:url value="/dbx/auth-clear" />" method='POST'>
            <input type="submit" value="<spring:message code="dbxfiles.unlinkbutton"/>"/>
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
        </form>
    </div>
    </body>
    </html>
</customtags:pageTemplate>
