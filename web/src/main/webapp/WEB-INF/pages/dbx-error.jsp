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
            <%-- If user hasn't linked to his dropbox account --%>
        <h3>
            <c:choose>
                <c:when test="${error == 1}">
                    <spring:message code="dbxerror.1"/>
                </c:when>
                <c:when test="${error == 2}">
                    <spring:message code="dbxerror.2"/>
                </c:when>
                <c:otherwise>
                    <spring:message code="dbxerror.0"/>
                </c:otherwise>
            </c:choose>
        </h3>
        <form name="authForm"
              action="<c:url value="/dbx/auth-start" />" method='POST'>
            <input type="submit" style="height:30px; width:245px" value="<spring:message
                        code="dbxfiles.linkbutton"/>"/>

            <input type="hidden"
                   name="${_csrf.parameterName}" value="${_csrf.token}"/>
        </form>
    </div>
    </body>
    </html>
</customtags:pageTemplate>
