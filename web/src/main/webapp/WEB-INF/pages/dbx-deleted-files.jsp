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

            <%-- If user hasn't linked to his dropbox account --%>
        <c:if test="${dbxAuth eq false}">
            <form name="authForm"
                  action="<c:url value="/dbx/auth-start" />" method='POST'>
                <input type="submit" style="height:30px; width:245px" value="<spring:message
                        code="dbxfiles.linkbutton"/>"/>

                <input type="hidden"
                       name="${_csrf.parameterName}" value="${_csrf.token}"/>
            </form>
        </c:if>

            <%-- If user has linked to his dropbox account --%>
        <c:if test="${dbxAuth eq true}">
        <table class="table table-bordered" style="background-color:whitesmoke">
            <thead>
            <tr>
                <th><spring:message code="dbxfiles.table.col1"/></th>
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
                        <a href="${restoreUrl}"><span
                                class="glyphicon glyphicon-repeat"></span></a>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
        <br/>
        <form name="authForm"
              action="<c:url value="/dbx/auth-clear" />" method='POST'>
            <input type="submit" value="<spring:message code="dbxfiles.unlinkbutton"/>"/>
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
        </form>
    </div>

    </c:if>
    </div>
    </body>
    </html>
</customtags:pageTemplate>
