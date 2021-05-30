<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<jsp:include page="grass.jsp"></jsp:include>
<html>
<head>
    <title>classes.DogServlet</title>
    <style>
        <%@include file="/style.css" %>
    </style>
</head>
<body>
<p>Select second dog's parent</p>

<form action="classes.DogServlet" method="post">
    <label class="container">Ovch
        <input type="radio" name="radio" value="Ovch" ${ sessionScope.page2=="Ovch" ? 'checked' : ""}>
        <span class="checkmark"></span>
    </label><br>
    <img src="Ovch.jpg">
    <label class="container">Chau
        <input type="radio" name="radio" value="Chau" ${ sessionScope.page2=="Chau" ? 'checked' : ""}>
        <span class="checkmark"></span>
    </label>
    <br><img src="Chau.jpg">
    <label class="container">Dalm
        <input type="radio" name="radio" value="Dalm" ${ sessionScope.page2=="Dalm" ? 'checked' : ""}>
        <span class="checkmark"></span>
    </label>
    <br><img src="Dalm.jpg"><br>
    <input type="submit" class="button" value="Previous" name="previous">
    <input type="submit" class="button" value="Next" name="next">
</form>
</body>
</html>
