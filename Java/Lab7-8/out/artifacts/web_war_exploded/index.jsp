<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<jsp:include page="grass.jsp"/>
<%request.getSession().setAttribute("pageNumber", 1);%>
<html>
<head>
    <title>Lab7-8</title>
    <style>
        <%@include file="/style.css" %>
    </style>
</head>
<body>
<p>Select first dog's parent</p>
<form action="classes.DogServlet" method="post">
    <label class="container">Haski
        <input type="radio" name="radio"
               value="Haski"  ${ sessionScope.page1=="Haski" ? 'checked' : ""}>
        <span class="checkmark"></span>
    </label>
    <br><img src="Haski.jpg">
    <label class="container">Korgi
        <input type="radio" name="radio"
               value="Korgi"  ${ sessionScope.page1=="Korgi" ? 'checked' : ""}>
        <span class="checkmark"></span>
    </label>
    <br><img src="Korgi.jpg"><br>
    <br>
    <input type="submit" class="button" value="Next" name="next">
</form>
</body>
</html>
