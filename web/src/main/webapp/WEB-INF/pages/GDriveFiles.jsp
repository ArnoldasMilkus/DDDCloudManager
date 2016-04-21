<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="customtags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<customtags:pageTemplate>
    <html>
    <head>
        <input type="hidden" id="currenId" name="currenId" value="${curId}"/>
        <script language="javascript">
            function rootAction(){
                window.location = '/GDriveFiles?rootId=root';
            }
        </script>
        <script language="javascript">
            function backAction(){
                var id = $("#currenId").val();
                var path = '/GDriveFiles?backId=';
                path = path.concat(id);
                window.location.href = path;
            }
        </script>
        <script language="javascript">
            function uploadAction(){
                var id = $("#currenId").val();
                var path = '/GDriveUpload?parentId=';
                path = path.concat(id);
                window.location.href = path;
            }
        </script>
    </head>
    <body>
    <div class="container col-md-12">
        <h2><spring:message code="users.table.title"/></h2>
        <input type='button' value="Root" name="Root" href="#" onclick="return rootAction()">
        <input type='button' value="Back" name="Back" href="#" onclick="return backAction()">
        <input type='button' value="Upload here" name="Upload here" href="#" onclick="return uploadAction()">
        <table class="table table-striped">
            <thead>
            <tr>
                <th><spring:message code="users.table.col1"/></th>
                <th><spring:message code="users.table.col2"/></th>
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
    </div>
    </body>
    </html>
</customtags:pageTemplate>
