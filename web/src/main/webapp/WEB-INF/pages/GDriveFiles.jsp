<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="customtags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<customtags:pageTemplate>
    <html>
    <head>
        <input type="hidden" id="currenId" name="currenId" value="${curId}"/>
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
            <input type='button' value="<spring:message code="GDrive.backButtonName"/>" name="Back" href="#" onclick="return backAction()">
            <input type='button' value="<spring:message code="GDrive.uploadButtonName"/>" name="Upload here" href="#" onclick="return uploadAction()">
            <table class="table table-striped">
                <thead>
                <tr>
                    <th><spring:message code="Grive.table.col1"/></th>
                    <th><spring:message code="Grive.table.col2"/></th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="file" items="${files}">
                    <tr>
                        <td style="width:150px">
                            <c:choose>
                                <c:when test="${file.mimeType eq 'folder'}">
                                    <a href="GDriveFiles?rootId=${file.id}">${file.name}</a>
                                </c:when>
                                <c:otherwise>${file.name}</c:otherwise>
                            </c:choose>
                        </td>
                        <td style="width:200px">
                                ${file.mimeType}
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
