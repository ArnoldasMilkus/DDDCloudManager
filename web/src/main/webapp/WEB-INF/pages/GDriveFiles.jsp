<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="customtags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<customtags:pageTemplate>
    <html>
    <head>
        <style>
            #topGlyphs .glyphicon {
                font-size: 50px;
                margin-right: 20px;
            }
        </style>
    </head>
    <body>
    <div class="container col-md-12">
        <c:if test="${driveAuth eq false}">
            <form name="authForm"
                  action="<c:url value="/GDriveFiles/startAuth" />" method='GET'>
                <input type="submit" style="height:30px; width:auto" value="<spring:message
                        code="GDrive.linkButtonName"/>"/>
                <input type="hidden"
                       name="${_csrf.parameterName}" value="${_csrf.token}"/>
            </form>
        </c:if>
        <c:if test="${isError eq true}">
            <h2><spring:message code="${error}"/></h2>
        </c:if>
        <c:if test="${driveAuth eq true && isUploading ne true}">
            <c:choose>
                <c:when test="${isTrashed}">
                    <h2><spring:message code="GDrive.table.trashTitle"/></h2>
                </c:when>
                <c:otherwise>
                    <c:choose>
                        <c:when test="${isOnlyPathChoose}">
                            <h2><spring:message code="GDrive.table.choseDBXTitle"/></h2>
                        </c:when>
                        <c:otherwise>
                            <h2><spring:message code="GDrive.table.title"/></h2>
                        </c:otherwise>
                    </c:choose>
                </c:otherwise>
            </c:choose>

            <div id="topGlyphs">
                <c:choose>
                    <c:when test="${isOnlyPathChoose}">
                        <a title="<spring:message code="GDrive.rootButtonName"/>" href="/GDriveFiles?rootId=root&from=${dbxFilePath}&isOnlyPathChoose=true">
                            <span class="glyphicon glyphicon-folder-open"></span></a>
                    </c:when>
                    <c:otherwise>
                        <a title="<spring:message code="GDrive.rootButtonName"/>" href="/GDriveFiles?rootId=root">
                            <span class="glyphicon glyphicon-folder-open"></span></a>
                    </c:otherwise>
                </c:choose>
                <c:choose>
                    <c:when test="${!isTrashed}">
                        <c:choose>
                            <c:when test="${isOnlyPathChoose}">
                                <a title="<spring:message code="GDrive.backButtonName"/>" href="/GDriveFiles?backId=${curId}&from=${dbxFilePath}&isOnlyPathChoose=true">
                                    <span class="glyphicon glyphicon-arrow-left" style="size: 40pt"></span></a>
                            </c:when>
                            <c:otherwise>
                                <a title="<spring:message code="GDrive.backButtonName"/>" href="/GDriveFiles?backId=${curId}">
                                    <span class="glyphicon glyphicon-arrow-left"></span></a>
                            </c:otherwise>
                        </c:choose>
                        <c:if test="${!isOnlyPathChoose}">
                            <a title="<spring:message code="GDrive.uploadButtonName"/>" href="/GDriveFiles?isUploading=true&rootId=${curId}">
                                <span class="glyphicon glyphicon-cloud-upload"></span></a>
                        </c:if>
                    </c:when>
                </c:choose>
                <c:if test="${!isOnlyPathChoose}">
                    <a title="<spring:message code="GDrive.trashButtonName"/>" href="/GDriveFiles?parentId=root&isTrashed=true">
                        <span class="glyphicon glyphicon-trash"></span></a>
                </c:if>
                <c:if test="${isOnlyPathChoose}">
                    <a title="<spring:message code="GDrive.chooseButtonName"/>" href="/GDriveFiles/copyFromDropbox?from=${dbxFilePath}&to=${curId}">
                        <span class="glyphicon glyphicon-download-alt"></span></a>
                </c:if>
            </div>
            <c:if test="${!isTrashed && !isOnlyPathChoose}">
                <form name="newFolder" style="margin-top: 10pt"
                      action="<c:url value="/GDriveFiles/newFolder" />" method='POST'>
                    <label><spring:message code="GDrive.newFolderText"/></label><input type="text" name="folderName" value=""/>
                    <input type="submit" style="height:30px; width:auto" value="<spring:message
                                code="GDrive.newFolder"/>"/>

                    <input type="hidden"
                           name="${_csrf.parameterName}" value="${_csrf.token}"/>
                    <input type="hidden" name="parentId" value="${curId}"/>
                </form>
            </c:if>
            <table class="table table-striped">
                <thead>
                <tr>
                    <th><spring:message code="GDrive.table.col1"/></th>
                    <th><spring:message code="GDrive.table.col2"/></th>
                    <th><spring:message code="GDrive.table.col3"/></th>
                    <th><spring:message code="GDrive.table.col4"/></th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="file" items="${files}">
                    <tr>
                        <td style="width:auto">
                            <c:choose>
                                <c:when test="${file.mimeType eq 'folder' && isTrashed eq 'false'}">
                                    <c:choose>
                                        <c:when test="${isOnlyPathChoose}">
                                            <a href="GDriveFiles?rootId=${file.id}&from=${dbxFilePath}&isOnlyPathChoose=true">${file.name}</a>
                                        </c:when>
                                        <c:otherwise><a href="GDriveFiles?rootId=${file.id}">${file.name}</a></c:otherwise>
                                    </c:choose>
                                </c:when>
                                <c:otherwise>${file.name}</c:otherwise>
                            </c:choose>
                        </td>
                        <td style="width:auto">
                                ${file.mimeType}
                        </td>
                        <td style="width:auto">
                            <c:choose>
                                <c:when test="${file.mimeType ne 'folder'}">${file.size} KB</c:when>
                            </c:choose>
                        </td>
                        <td style="width:auto">
                            <c:choose>
                                <c:when test="${!isOnlyPathChoose}">
                                    <c:if test="${file.mimeType ne 'folder'}"><a title="<spring:message code="GDrive.downloadAction"/>" href="/GDriveFiles/download?fileId=${file.id}"><span
                                            class="glyphicon glyphicon-download-alt"></span></a>
                                    </c:if>
                                    <c:choose>
                                    <c:when test="${isTrashed}">
                                    <a title="<spring:message code="GDrive.restoreAction"/>" href="/GDriveFiles/delete?parentId=${curId}&fileId=${file.id}&isTrashed=${isTrashed}">
                                        <span class="glyphicon glyphicon-export"></span></a>
                                        </c:when>
                                        <c:otherwise>
                                        <a title="<spring:message code="GDrive.removeAction"/>" href="/GDriveFiles/delete?parentId=${curId}&fileId=${file.id}&isTrashed=${isTrashed}">
                                            <span class="glyphicon glyphicon-trash"></span></a>
                                            </c:otherwise>
                                    </c:choose>
                                </c:when>
                            </c:choose>
                            <c:if test="${file.mimeType ne 'folder' && !isOnlyPathChoose}">
                                <a title="<spring:message code="GDrive.sendToDropbox"/>" href="/dbx/files?from=${file.id}">
                                    <span class="glyphicon glyphicon-share"></span></a>
                            </c:if>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </c:if>
        <c:if test="${isUploading && driveAuth eq true}">
            <form method="POST" enctype="multipart/form-data" style="margin-top: 30pt" action="/GDriveUpload?${_csrf.parameterName}=${_csrf.token}">
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
                <input type="hidden" name="parentId" value="${curId}"/>
            </form>
        </c:if>
    </div>
    </body>
    </html>
</customtags:pageTemplate>
