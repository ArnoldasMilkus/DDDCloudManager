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
            <spring:message code="dbxerror.${error}"/>
        </h3>
        <c:if test="${error == 1 || error == 2}">
            <form name="authForm"
                  action="<c:url value="/dbx/auth-start" />" method='POST'>
                <input type="submit" style="height:30px; width:245px" value="<spring:message
                        code="dbxfiles.linkbutton"/>"/>

                <input type="hidden"
                       name="${_csrf.parameterName}" value="${_csrf.token}"/>
            </form>
        </c:if>
    </div>
    </body>
    </html>
</customtags:pageTemplate>
