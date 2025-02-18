<%--
  Created by IntelliJ IDEA.
  User: aveasura
  Date: 15.02.2025
  Time: 18:24
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" isELIgnored="false" language="java" %>
<html>
<head>
    <title>Curr</title>
</head>
<body>

<a href="${pageContext.request.contextPath}/">back</a>
<hr/>

<h1>add currency</h1>

<form action="${pageContext.request.contextPath}/currencies" method="post">
    <label for="code">enter currency code</label>
    <input type="text" id="code" name="code" placeholder="code" required>
    <br>
    <label for="name">enter currency name</label>
    <input type="text" id="name" name="fullName" placeholder="name" required>
    <br>
    <label for="sign">enter currency sign</label>
    <input type="text" id="sign" name="sign" placeholder="sign" required>

    <hr>
    <button type="submit">add</button>
</form>

</body>
</html>
