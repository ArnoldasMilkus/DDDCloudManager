<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="customtags" tagdir="/WEB-INF/tags" %>
<%@ page session="false" %>
<customtags:pageTemplate>
    <style type="text/css">
        td
        {
            padding:0 15px 0 15px;
        }
        button
        {
            margin-top:30px;
            background-color: #4c74ff;
            font-weight: bold;
        }
    </style>
    <html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=US-ASCII">
        <title><spring:message code="register.title"></spring:message></title>
    </head>
    <body>
    <H1 align="center">
        <spring:message code="register.title"></spring:message>
    </H1>
    <form:form modelAttribute="user" method="POST" enctype="utf8">
        <br>
        <table align="center">
            <tr>
                <td><label><spring:message code="register.username"></spring:message></label></td>
                <td><form:input path="username" value="" type="text"/></td>
                <form:errors path="username" element="div"/>
            </tr>
            <tr>
                <td><label><spring:message code="register.firstName"></spring:message></label></td>
                <td><form:input path="firstName" value="" type="text"/></td>
                <form:errors path="firstName" element="div"/>
            </tr>
            <tr>
                <td><label><spring:message code="register.lastName"></spring:message></label></td>
                <td><form:input path="lastName" value="" type="text"/></td>
                <form:errors path="lastName" element="div"/>
            </tr>
            <tr>
                <td><label><spring:message code="register.elmail"></spring:message></label></td>
                <td><form:input path="email" value=""/></td>
                <form:errors path="email" element="div"/>
            </tr>
            <tr>
                <td><label><spring:message code="register.password"></spring:message></label></td>
                <td><form:input path="password" value="" type="password"/></td>
                <form:errors path="password" element="div"/>
            </tr>
            <tr>
                <td><label><spring:message code="register.confirmPassword"></spring:message></label></td>
                <td><form:input path="matchingPassword" value="" type="password"/></td>
                <form:errors element="div"/>
            </tr>
        </table>
        <center><button type="submit" style="height:30px; width:245px">
            <spring:message code="register.submit"></spring:message>
        </button></center>
    </form:form>
    <%--<a href="<c:url value="login.jsp" />">
        <spring:message code="register.loginLink"></spring:message>
    </a>--%>
    </body>
    </html>
</customtags:pageTemplate>