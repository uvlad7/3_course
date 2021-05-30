<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true" %>
<%@page import="org.apache.commons.httpclient.HttpStatus" %>
<html>
<head>
    <title>Lab9 Error Page</title>
    <link type="text/css" rel="stylesheet"
          href="${pageContext.request.contextPath}/resources/error.css">
    <link rel="stylesheet"
          href="https://maxcdn.bootstrapcdn.com/bootstrap/3.4.0/css/bootstrap.min.css">
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.4.1/jquery.min.js"></script>
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.4.0/js/bootstrap.min.js"></script>
    <link rel="shortcut icon" href="${pageContext.request.contextPath}/resources/favicon.ico">
    <link rel="apple-touch-icon" href="${pageContext.request.contextPath}/resources/favicon.png">
    <link rel="image_src" href="${pageContext.request.contextPath}/resources/favicon.ico">
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
</head>
<body style="height: 100%; position: relative">
<div class="container">
    <div class="row">
        <div class="col-md-12">
            <div class="error-template">
                <h1>Oops!</h1>
                <h2>${pageContext.errorData.statusCode} <%=HttpStatus.getStatusText(pageContext.getErrorData().getStatusCode())%>
                </h2>
                <div class="error-details">Sorry, an error has been occurred!</div>
                <div class="error-details">${pageContext.errorData.throwable.message}</div>
                <div class="error-actions">
                    <a onclick="window.history.back()" class="btn btn-primary btn-lg"><span
                            class="glyphicon glyphicon-menu-left"></span>Return</a>
                </div>
            </div>
        </div>
    </div>
</div>
</body>
</html>