<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="customtags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<customtags:pageTemplate>
    <script src="/resources/jquery/reloader.js"></script>
    <html>
    <body>
    <div style="max-width: 1000px">
        <h2><spring:message code="index.welcome"/></h2>
        <h4><spring:message code="index.description"/></h4>
        <img src="/resources/sync.png" width="479" height="357"/>
    </div>
    <div>
        <h2><spring:message code="index.benefits.header"/></h2>
        <h4><spring:message code="index.benefits"/></h4>
        <h4><spring:message code="index.idea"/></h4>
        <img src="/resources/alldrives.png" width="500" height="101"/>
    </div>
    <div>
        <h2><spring:message code="index.accessibility.header"/></h2>
        <h4><spring:message code="index.accessibility"/></h4>
    </div>
    </body>
    </html>
</customtags:pageTemplate>
