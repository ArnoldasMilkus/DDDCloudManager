<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="customtags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<link href="/resources/bootstrap/css/custom.css" rel="stylesheet">
<customtags:pageTemplate>
    <script src="/resources/jquery/reloader.js"></script>
    <html>
    <script language="javascript">
        function unlinkGDriveAction() {
            var path = '/GDriveFiles/revokeToken';
            window.location.href = path;
        }
    </script>

    <script type="text/javascript">
        function backgroundChange(imageNumber) {
            sessionStorage.removeItem("backgroundIndex");
            sessionStorage.setItem("backgroundIndex", imageNumber);
            location.reload();
        }
    </script>

    <script type="text/javascript">
        function fontChange(fontNumber) {
            sessionStorage.removeItem("fontSize");
            sessionStorage.setItem("fontSize", fontNumber);
            location.reload();
        }
    </script>

    <script>
        function mark(el) {
            el.style.border = "10px solid #2C2C2C";
        }
    </script>

    <body>
    <sec:authentication var="user" property="principal"/>
    <div class="container col-md-12 pull-left">
        <c:choose>
            <c:when test="${!empty gDriveAccount}">
                <input type='button' style="margin-top: 40px; width: 250px" value="<spring:message code="GDrive.revokeButtonName"/>" name="revokeGDriveToken"
                       href="#" onclick="return unlinkGDriveAction()">
                <label><spring:message code="settings.gDriveAccount"/>: ${gDriveAccount}</label>
            </c:when>
            <c:otherwise>
                <input type='button' style="margin-top: 40px; width: 250px" value="<spring:message code="GDrive.linkButtonName"/>" name="revokeGDriveToken"
                       href="#" onclick="return unlinkGDriveAction()">
                <label><spring:message code="settings.noGDriveAccount"/></label>
            </c:otherwise>
        </c:choose>

    </div>
        <div class="container col-md-12">
        <c:choose>
            <c:when test="${!empty dbxAccount}">
                <form name="authForm"
                      action="<c:url value="/dbx/auth-clear" />" method='POST'>
                    <input type="submit" style="width: 250px" value="<spring:message code="dbxfiles.unlinkbutton"/>"/>
                    <label><spring:message code="settings.dbxAccount"/>: ${dbxAccount}</label><br/>
                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                </form>
            </c:when>
            <c:otherwise>
                <form name="authForm"
                      action="<c:url value="/dbx/auth-start" />" method='POST'>
                    <input type="submit" style="width: 250px" value="<spring:message code="dbxfiles.linkbutton"/>"/>
                    <label><spring:message code="settings.noAccount"/></label>
                    <input type="hidden"
                           name="${_csrf.parameterName}" value="${_csrf.token}"/>
                </form>
            </c:otherwise>
        </c:choose>
    </div>

    <h2 style="margin-top: 100pt"><spring:message code="settings.background"/></h2>
    <div id="backgrounds">
        <img src="/resources/cloud.png" height="300" width="300" style="margin:0 50px 20px"
             onclick="backgroundChange(null);onclick=mark(this)">
        <img src="/resources/cloudgreen.png" height="300" width="300" style="margin:0 50px 20px"
             onclick="backgroundChange(0);onclick=mark(this)">
        <img src="/resources/nightcloud.jpg" height="300" width="300" style="margin:0 50px 20px"
             onclick="backgroundChange(1);onclick=mark(this)">
        <img src="/resources/stormcloud.jpg" height="300" width="300" style="margin:0 50px 20px"
             onclick="backgroundChange(2);onclick=mark(this)">
    </div>

    <div id="fonts">
        <span style="white-space: nowrap; font-size: 16pt; font-weight: bold"><spring:message code="settings.font.size"/>: </span>
        <label style="font-size: 12pt;" onclick="fontChange(null)">12pt</label>
        <label style="font-size: 14pt;" onclick="fontChange(14)">14pt</label>
        <label style="font-size: 16pt;" onclick="fontChange(16)">16pt</label>
        <label style="font-size: 18pt;" onclick="fontChange(18)">18pt</label>
        <label style="font-size: 20pt;" onclick="fontChange(20)">20pt</label>
        <label style="font-size: 24pt;" onclick="fontChange(22)">22pt</label>
        <label style="font-size: 24pt;" onclick="fontChange(24)">24pt</label>
    </div>
    </body>
    </html>
</customtags:pageTemplate>