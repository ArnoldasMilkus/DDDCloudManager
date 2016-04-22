<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="customtags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<customtags:pageTemplate>
    <script src="/resources/jquery/reloader.js"></script>
    <html>

    <body >

    <div class="container">
        <sec:authentication var="user" property="principal"/>
        <h2>${user.username}</h2>
        <button type="button" onclick="myFunction()">Change Backgroud image</button>
        <div>
            <p>Laikrodis</p>
            <p id="clock"></p>
        </div>
        <p></p>
        <div id="result"></div>
        <script>

        </script>
        <script>
            var myVar = setInterval(myTimer, 1000);

            function myTimer() {
                var d = new Date();
                document.getElementById("clock").innerHTML = d.toLocaleTimeString();
            }
        </script>

        <script type="text/javascript">

            function myFunction() {
                if(sessionStorage.getItem("lastname1")==1){

                    //if (typeof(Storage) !== "undefined") {
                    // Store
                    sessionStorage.removeItem("lastname1");
                        localStorage.setItem("lastname",0);
                    sessionStorage.setItem("lastname1",0);
                        // Retrieve
                        document.getElementById("result").innerHTML = "special";
                    location.reload();

                } else{
                    req = false;
                    change = false;
                    sessionStorage.removeItem("lastname1");
                    //if (typeof(Storage) !== "undefined") {
                        // Store
                    localStorage.setItem("lastname", 1);
                    sessionStorage.setItem("lastname1", 1);
                        // Retrieve
                        document.getElementById("result").innerHTML = "default";
                    location.reload();

                }

            }

        </script>
    </div>
    </body>
    </html>
</customtags:pageTemplate>
