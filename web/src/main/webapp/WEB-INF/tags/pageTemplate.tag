<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="description" content="Technology to merge multiple clouds, name pending">
    <meta name="author" content="Some guys">
    <link rel="icon" type="image/png" href="/resources/favicon.png">

    <link href="/resources/bootstrap/css/bootstrap.css" rel="stylesheet">
    <link href="/resources/bootstrap/css/bootstrap.min.css" rel="stylesheet">
    <link href="/resources/bootstrap/css/bootstrap-theme.min.css" rel="stylesheet">
    <script src="/resources/jquery/reloader.js"></script>
    <script src="/resources/jquery/jquery-2.2.0.js"></script>
    <script src="/resources/bootstrap/js/bootstrap.js"></script>

    <!--[if lt IE 9]>
    <script src="https://oss.maxcdn.com/html5shiv/3.7.2/html5shiv.min.js"></script>
    <script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
    <![endif]-->
    <title><spring:message code="template.title"/></title>
    <sec:authentication var="user" property="principal"/>
    <sec:authorize access="isAuthenticated()">
        <c:set var="username" value="${user.username}"/>
    </sec:authorize>

    <script type="text/javascript">
        $(function(){
            function stripTrailingSlash(str) {
                if(str.substr(-1) == '/') {
                    return str.substr(0, str.length - 1);
                }
                return str;
            }

            var url = window.location.pathname;
            var activePage = stripTrailingSlash(url);

            $('.nav li a').each(function(){
                var currentPage = stripTrailingSlash($(this).attr('href'));

                if ((activePage.indexOf (currentPage) != -1 && currentPage.length > 0) || activePage == currentPage) {
                    $(this).parent().addClass('active');
                }

            });
        });
    </script>
</head>

<body onload="myFunction1()">
<div class="container-fluid">
    <div class="row">
        <div class="col-md-2">
        </div>
        <div class="col-md-8">
            <div class="page-header">
                <%--<input type='button' Onclick="document.body.style.cssText+=';background: url(/resources/dropbox.png);'" />--%>
                <%--<form method="get" action="/error404">--%>
                    <%--<button type="submit">ccc</button>--%>
                <%--</form>--%>
                <%--<h1 style="font-family:'Comic Sans MS';">
                        <spring:message code="template.header"/>
                </h1>--%>
                <img src="/resources/DDD%20Cloud%20Manager%20logo.png" alt="Logotype" width="400" height="150"/>
                    <ul align="right">
                    <c:if test="${!empty username}">
                            <label><font color="#1e90ff"><spring:message code="template.logged"/>: </font>${username}</label>
                    </c:if>
                </ul>
            </div>
            <ul class="nav nav-pills">
                <li>
                    <a href="/"><spring:message code="template.home"/></a>
                </li>
                <li>
                    <a href="/users/all"><spring:message code="template.users"/></a>
                </li>
                <c:if test="${!empty username}">
                    <li>
                        <a href="/settings"><spring:message code="template.settings"/></a>
                    </li>
                    <li>
                        <a href="/dbx/"><spring:message code="template.dropbox"/></a>
                    </li>
                </c:if>
                <c:if test="${!empty username}">
                    <li>
                        <a href="/GDriveFiles"><spring:message code="template.GDriveFiles"/></a>
                    </li>
                </c:if>
                <li>
                    <a href="/about"><spring:message code="template.about"/></a>
                </li>
                <li class="dropdown pull-right">
                    <a href="#" data-toggle="dropdown" class="dropdown-toggle">
                        <spring:message code="template.button.language"/>
                        <strong class="caret"></strong></a>
                    <ul class="dropdown-menu">
                        <li>
                            <a href="?lang=en"><img src="/resources/uk_flag.png"> <spring:message code="template.language.en"/></a>
                        </li>
                        <li>
                            <a href="?lang=lt"><img src="/resources/lt_flag.png"> <spring:message code="template.language.lt"/></a>
                        </li>
                    </ul>
                </li>
                <li class="pull-right">
                    <c:choose>
                        <c:when test="${!empty username}">
                            <a href="/logout" onclick="return confirm('<spring:message code="template.confirmation"/>');"><spring:message code="template.logout"/></a>
                        </c:when>
                        <c:otherwise>
                            <ul >
                                <li>
                                    <a href="/login"><spring:message code="template.login"/></a>
                                </li>

                                <li>
                                    <a href="/registration"><spring:message code="template.registration"/></a>
                                </li>

                            </ul>
                        </c:otherwise>
                    </c:choose>
                </li>
            </ul>
            <div class="col-md-2">
            </div>
            <jsp:doBody/>
        </div>
    </div>
</div>
</body>
</html>
