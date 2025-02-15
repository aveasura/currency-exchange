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

<a href="${pageContext.request.contextPath}/currencies">GET currencies JSON</a>
<br>
<a href="${pageContext.request.contextPath}/curr.jsp">POST add currency form</a>

</body>
</html>
