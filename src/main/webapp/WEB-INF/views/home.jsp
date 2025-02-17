<%--
  Created by IntelliJ IDEA.
  User: aveasura
  Date: 15.02.2025
  Time: 18:21
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" isELIgnored="false" language="java" %>
<html>
<head>
    <title>Main</title>
</head>
<body>

<h1>welcome</h1>

<a href="${pageContext.request.contextPath}/currencies">GET currencies (jsp)</a>
<br>
<br>
<a href="${pageContext.request.contextPath}/addCurrency.jsp">POST add currency (jsp)</a>
<hr/>

<form action="${pageContext.request.contextPath}/currency/" method="GET">
    <a>fast check currency by id (jsp)</a>
    <label for="curr"></label>
    <input type="text" id="curr" name="id" placeholder="select currency id" required>
    <button type="submit">apply</button>
</form>

</body>
</html>
