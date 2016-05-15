<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE HTML>
<html>
<head>
    <title><spring:message code="err.page.not.found"/></title>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <link href='http://fonts.googleapis.com/css?family=Fjalla+One' rel='stylesheet' type='text/css'>
    <style type="text/css">
        body{
            background:url(/resources/cloud.png) ;
        }
        .wrap{
            margin:0 auto;
            width:1000px;
        }
        .title{
            margin-bottom: 40px;
        }
        .title h1{
            font-size:100px;
            color:yellow;
            text-align:center;
            margin-top:80px;
            margin-bottom:0px;
            text-shadow:6px 1px 6px #333;
            font-family: 'Fjalla One', sans-serif;
        }
        .title h2{
            font-size:100px;
            color:yellow;
            text-align:center;
            margin-bottom:1px;
            text-shadow:6px 1px 6px #333;
            font-family: 'Fjalla One', sans-serif;
            margin-top: 0px;
        }
        .gray {
            margin-bottom: 20px;
            background: rgba(12, 52, 77, 0.34);
            text-shadow: 0 -1px 1px rgba(0, 0, 0, 0.25);
            border-radius: 4px;
            -webkit-border-radius: 4px;
            -moz-border-radius: 4px;
            -o-border-radius: 4px;
            color:yellow;
            text-decoration:none;
            padding:30px 30px;
            font-size: 20px;
            font-weight:bold;
            font-family: 'Fjalla One', sans-serif;
            text-align: center;
        }
        .ag-3d_button.orange {
            box-shadow: rgba(155, 142, 50, 0.98) 0 3px 0px, rgba(0, 0, 0, 0.3) 0 3px 3px;
        }
        .ag-3d_button {
            vertical-align: top;
            border-radius: 4px;
            border: none;
            padding: 10px 25px 12px;
        }
        .thunder_cloud {
            display: flex;
            justify-content: center;
        }
        .orange {
            background: #fdde02;
            background: -moz-linear-gradient(top,  #fdde02 0%, #dec829 99%);
            background: -webkit-gradient(linear, left top, left bottom, color-stop(0%,#fdde02), color-stop(99%,#dec829));
            background: -webkit-linear-gradient(top,  #fdde02 0%,#dec829 99%);
            background: -o-linear-gradient(top,  #fdde02 0%,#dec829 99%);
            background: -ms-linear-gradient(top,  #fdde02 0%,#dec829 99%);
            background: linear-gradient(to bottom,  #fdde02 0%,#dec829 99%);
            filter: progid:DXImageTransform.Microsoft.gradient( startColorstr='#fdde02', endColorstr='#dec829',GradientType=0 );
            color:#fff;
            text-shadow:1px 1px 3px rgba(155, 142, 50, 0.98);
            border: 1px solid rgba(155, 142, 50, 0.98);
            text-decoration: none;
        }
    </style>
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