<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="customtags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<customtags:pageTemplate>
    <script src="/resources/jquery/reloader.js"></script>
    <html>

    <style>
        #backgrounds img:hover {
            border:2px solid #6D3ECD;
            box-shadow: 0 0 10px #333;
            -webkit-box-shadow: 0 0 10px #333;
            -moz-box-shadow: 0 0 10px #333;
            -o-box-shadow: 0 0 10px #333;
            -ms-box-shadow: 0 0 10px #333;
        }
        #fonts label:hover {
            border:2px solid #333;
        }
    </style>

    <script type="text/javascript">
        function backgroundChange(imageNumber) {
            sessionStorage.removeItem("backgroundIndex");
            sessionStorage.setItem("backgroundIndex",imageNumber);
            location.reload();
        }
    </script>

    <script type="text/javascript">
        function fontChange(fontNumber) {
            sessionStorage.removeItem("fontSize");
            sessionStorage.setItem("fontSize",fontNumber);
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
    <h2>Here you can change website's background</h2>
    <div id="backgrounds">
        <img src="/resources/cloud.png" height="300" width="300" style="margin:0 50px 20px" onclick="backgroundChange(null);onclick=mark(this)">
        <img src="/resources/cloudgreen.png" height="300" width="300" style="margin:0 50px 20px" onclick="backgroundChange(0);onclick=mark(this)">
        <img src="/resources/nightcloud.jpg" height="300" width="300" style="margin:0 50px 20px" onclick="backgroundChange(1);onclick=mark(this)">
        <img src="/resources/stormcloud.jpg" height="300" width="300" style="margin:0 50px 20px" onclick="backgroundChange(2);onclick=mark(this)">
    </div>

    <div id="fonts">
        <span style="white-space: nowrap; font-size: 16pt; font-weight: bold">Font size change:</span>
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