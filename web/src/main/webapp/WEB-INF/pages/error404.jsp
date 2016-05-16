<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE HTML>
<html>
<head>
    <title><spring:message code="err.page.not.found"/></title>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <link href='http://fonts.googleapis.com/css?family=Fjalla+One' rel='stylesheet' type='text/css'>
    <link href="/resources/bootstrap/css/error404.css" rel="stylesheet">
</head>
<body>
<div class="wrap">
    <div class="title">
        <h1><spring:message code="err.page.not.found"/></h1>
        <div class="thunder_cloud">
            <img src="/resources/thunder_cloud.png" width="100" height="100"/>
        </div>
        <h2><spring:message code="err.404"/></h2>
    </div>
</div>
<div class="wrap">
    <div class="gray">
        <a href="/" class="ag-3d_button orange"><spring:message code="err.back"/></a>
    </div>
</div>
</body>