<%@page contentType="text/html" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Lab9</title>
    <link type="text/css" rel="stylesheet"
          href="${pageContext.request.contextPath}/resources/table.css">
    <link rel="stylesheet"
          href="https://maxcdn.bootstrapcdn.com/bootstrap/3.4.0/css/bootstrap.min.css">
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.4.1/jquery.min.js"></script>
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.4.0/js/bootstrap.min.js"></script>
    <link rel="shortcut icon" href="${pageContext.request.contextPath}/resources/favicon.ico">
    <link rel="apple-touch-icon" href="${pageContext.request.contextPath}/resources/favicon.png">
    <link rel="image_src" href="${pageContext.request.contextPath}/resources/favicon.ico">
    <script src="${pageContext.request.contextPath}/resources/table.js"
            type="text/javascript"></script>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
</head>
<body>
<jsp:useBean id="current_voc" class="app.entity.Vocabulary" scope="request"/>
<jsp:setProperty property="*" name="current_voc"/>
<form id="actionsTableForm" method="post">
    <input type="hidden" id="word_id" name="word_id">
    <input type="hidden" id="record_id" name="record_id">
    <input type="hidden" id="is_exp" name="is_exp">
    <input type="hidden" id="action_name" name="action_name">
    <input type="hidden" id="voc_id" name="voc_id" value="${current_voc.id}">
    <input type="hidden" id="voc_exp" name="voc_exp" value="${current_voc.explanatory}">
</form>

<div class="my-auto">
    <jsp:include page="menu.jsp"/>
    <jsp:include page="table.jsp"/>
</div>
</body>
</html>