<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="customtags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<customtags:pageTemplate>
    <html>
    <head>
        <input type="hidden" id="currenId" name="currenId" value="${curId}"/>
        <input type="hidden" id="isTrashed" name="currenId" value="${isTrashed}"/>
        <script language="javascript">
            function rootAction() {
                window.location = '/GDriveFiles?rootId=root';
            }
        </script>
        <script language="javascript">
            function backAction() {
                var id = $("#currenId").val();
                var path = '/GDriveFiles?backId=';
                path = path.concat(id);
                window.location.href = path;
            }
        </script>
        <script language="javascript">
            function uploadAction() {
                var id = $("#currenId").val();
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
            <h2><spring:message code="Grive.table.title"/></h2>
            <input type='button' value="<spring:message code="GDrive.rootButtonName"/>" name="Root" href="#" onclick="return rootAction()">
            <c:choose>
                <c:when test="${!isTrashed}">
                    <input type='button' value="<spring:message code="GDrive.backButtonName"/>" name="Back" href="#" onclick="return backAction()">
                    <input type='button' value="<spring:message code="GDrive.uploadButtonName"/>" name="Upload here" href="#" onclick="return uploadAction()">
                </c:when>
            </c:choose>
            <input type='button' value="<spring:message code="GDrive.trashButtonName"/>" name="TrashBin" href="#" onclick="return trashBinAction()">
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
                                    <a href="GDriveFiles?rootId=${file.id}">${file.name}</a>
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
                                <c:when test="${file.mimeType ne 'folder'}"><a href="/GDriveFiles/download?fileId=${file.id}"><span
                                        class="glyphicon glyphicon-download-alt"></span></a>
                                    |
                                </c:when>
                            </c:choose>

                            <a href="/GDriveFiles/delete?parentId=${curId}&fileId=${file.id}&isTrashed=${isTrashed}">
                                <c:choose>
                                    <c:when test="${isTrashed}">
                                        <span class="glyphicon glyphicon-export"></span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="glyphicon glyphicon-trash"></span>
                                    </c:otherwise>
                                </c:choose></a>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </c:if>
    </div>
    </body>
    </html>
</customtags:pageTemplate>
