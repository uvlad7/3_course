<%@ taglib prefix="rt" uri="/WEB-INF/custom.tld" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<jsp:include page="grass.jsp"/>
<%@ page errorPage="error.jsp" %>
<html>
<head>
    <title>First</title>
    <style>
        <%@include file="/style.css" %>
    </style>
</head>
<body>
<p>This is your dog ^_^</p>

<rt:DogTag
        firstDog="${sessionScope.page1}"
        secondDog="${sessionScope.page2}">
    Ваш прекрасный метис!
</rt:DogTag>

<br><br>
<form action="classes.DogServlet" method="post">
    <input type="submit" class="button" value="Previous" name="previous">
    <input type="submit" class="button" value="Throw exception" name="throwException">
</form>
</body>
</html>
