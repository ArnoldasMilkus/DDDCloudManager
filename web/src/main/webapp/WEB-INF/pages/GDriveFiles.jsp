<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="customtags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<customtags:pageTemplate>
    <html>
    <head>
        <input type="hidden" id="currentId" name="currentId" value="${curId}"/>
        <input type="hidden" id="dbxFilePath" name="dbxFilePath" value="${dbxFilePath}"/>
        <input type="hidden" id="isTrashed" name="currenId" value="${isTrashed}"/>
        <script language="javascript">
            function rootAction() {
                window.location = '/GDriveFiles?rootId=root';
            }
        </script>
        <script language="javascript">
            function backAction() {
                var id = $("#currentId").val();
                var path = '/GDriveFiles?backId=';
                path = path.concat(id);
                window.location.href = path;
            }
        </script>
        <script language="javascript">
            function uploadAction() {
                var id = $("#currentId").val();
                var path = '/GDriveUpload?parentId=';
                path = path.concat(id);
                window.location.href = path;
            }
        </script>
        <script language="javascript">
            function trashBinAction() {
                var path = '/GDriveFiles?parentId=root&isTrashed=true';
                window.location.href = path;
            }
        </script>
        <script language="javascript">
            function sendPathToDBXAction() {
                var id = $("#currentId").val();
                var dbxFilePath = $("#dbxFilePath").val();
                var path = '/dbx/copy/gd?from=';
                var pathEnd = '&to=';
                path = path.concat(dbxFilePath);
                path = path.concat(pathEnd);
                path = path.concat(id);
                window.location.href = path;
            }
        </script> <script language="javascript">
        function rootChooseAction() {
            var path = '/GDriveFiles?rootId=root&dbxFilePath=';
            var dbxFilePath = $("#dbxFilePath").val();
            var pathEnd = '&isOnlyPathChoose=true';
            path = path.concat(dbxFilePath);
            path = path.concat(pathEnd);
            window.location.href = path;
        }
    </script>
        <script language="javascript">
            function backChooseAction() {
                window.location.href = path;var id = $("#currentId").val();
                var path = '/GDriveFiles?backId=';
                var id = $("#currentId").val();
                var pathMiddle = '&dbxFilePath=';
                var dbxFilePath = $("#dbxFilePath").val();
                var pathEnd = '&isOnlyPathChoose=true';
                path = path.concat(id);
                path = path.concat(pathMiddle);
                path = path.concat(dbxFilePath);
                path = path.concat(pathEnd);
                window.location.href = path;
            }
        </script>
    </head>
    <body>
    <div class="container col-md-12">
        <c:if test="${driveAuth eq false}">
            <form name="authForm"
                  action="<c:url value="/GDriveFiles/startAuth" />" method='GET'>
                <input type="submit" style="height:30px; width:400px" value="<spring:message
                        code="GDrive.linkButtonName"/>"/>
                <c:if test="${isError eq true}">
                    <h2><spring:message code="${error}"/></h2>
                </c:if>

                <input type="hidden"
                       name="${_csrf.parameterName}" value="${_csrf.token}"/>
            </form>
        </c:if>
        <c:if test="${driveAuth eq true}">
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
            <c:choose>
                <c:when test="${isOnlyPathChoose}">
                    <input type='button' value="<spring:message code="GDrive.rootButtonName"/>" name="Root" href="#" onclick="return rootChooseAction()">
                </c:when>
                <c:otherwise>
                    <input type='button' value="<spring:message code="GDrive.rootButtonName"/>" name="Root" href="#" onclick="return rootAction()">
                </c:otherwise>
            </c:choose>


            <c:choose>
                <c:when test="${!isTrashed}">
                    <c:choose>
                        <c:when test="${isOnlyPathChoose}">
                            <input type='button' value="<spring:message code="GDrive.backButtonName"/>" name="Back" href="#" onclick="return backChooseAction()">
                        </c:when>
                        <c:otherwise>
                            <input type='button' value="<spring:message code="GDrive.backButtonName"/>" name="Back" href="#" onclick="return backAction()">
                        </c:otherwise>
                    </c:choose>
                    <c:if test="${!isOnlyPathChoose}">
                        <input type='button' value="<spring:message code="GDrive.uploadButtonName"/>" name="Upload here" href="#" onclick="return uploadAction()">
                    </c:if>
                </c:when>
            </c:choose>
            <c:if test="${!isOnlyPathChoose}">
                <input type='button' value="<spring:message code="GDrive.trashButtonName"/>" name="TrashBin" href="#" onclick="return trashBinAction()">
            </c:if>
            <c:if test="${isOnlyPathChoose}">
                <input type='button' value="<spring:message code="GDrive.chooseButtonName"/>" name="DownloadHere" href="#" onclick="return sendPathToDBXAction()">
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
                                            <a href="GDriveFiles?rootId=${file.id}&dbxFilePath=${dbxFilePath}&isOnlyPathChoose=true">${file.name}</a>
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
                                        |
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
                            <c:if test="${file.mimeType ne 'folder'}">|
                                <a title="<spring:message code="GDrive.sendToDropbox"/>" href="/GDriveFiles/workWithDBX?from=${file.id}">
                                    <span class="glyphicon glyphicon-share"></span></a>
                            </c:if>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
            <c:if test="${!isTrashed && !isOnlyPathChoose}">
                <form name="newFolder"
                      action="<c:url value="/GDriveFiles/newFolder" />" method='POST'>
                    <label><spring:message code="GDrive.newFolderText"/></label><input type="text" name="folderName" value=""/>
                    <input type="submit" style="height:30px; width:auto" value="<spring:message
                            code="GDrive.newFolder"/>"/>

                    <input type="hidden"
                           name="${_csrf.parameterName}" value="${_csrf.token}"/>
                    <input type="hidden" name="parentId" value="${curId}"/>
                </form>
            </c:if>
        </c:if>
    </div>
    </body>
    </html>
</customtags:pageTemplate>
