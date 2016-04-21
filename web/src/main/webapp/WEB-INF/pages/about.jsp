<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="customtags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<customtags:pageTemplate>
    <html>
    <body>
    <div class="container">
        <h2><spring:message code="about.question"/></h2>
    </div>
    <div class="container" style="max-width: 1000px">
        <h4><spring:message code="about.description"/></h4>
        <h4><spring:message code="about.clouds"/></h4>
    </div>
    <div class="row">
        <div class="col-md-2">
            <img src="/resources/onedrive.jpg" alt="One Drive" width="150" height="100"/>
        </div>
        <div class="col-md-2">
            <img src="/resources/google_drive.png" alt="Google Drive" width="100" height="100"/>
        </div>
        <div class="col-md-2">
            <img src="/resources/dropbox.jpeg" alt="Dropbox" width="100" height="100"/>
        </div>
    </div>
    <div class="container col-md-13">
        <h2><spring:message code="about.table.title"/></h2>
        <table class="table">
            <thead>
            <tr>
                <th><spring:message code="about.table.col1"/></th>
                <th><spring:message code="about.table.col2"/></th>
                <th><spring:message code="about.table.col3"/></th>
            </tr>
            </thead>
            <tbody>
            <tr>
                <td>Gediminas</td>
                <td>Kurpavičius</td>
                <td>gediminas.kurpavicius@ktu.edu</td>
            </tr>
            <tr>
                <td>Tomas</td>
                <td>Brusokas</td>
                <td>tomas.brusokas@ktu.edu</td>
            </tr>
            <tr>
                <td>Vilintas</td>
                <td>Strielčiūnas</td>
                <td>vilintas.strielciunas@ktu.edu</td>
            </tr>
            <tr>
                <td>Arnoldas</td>
                <td>Milkus</td>
                <td>arnoldas.milkus@ktu.edu</td>
            </tr>
            </tbody>
        </table>
    </div>
    </body>
    </html>
</customtags:pageTemplate>
