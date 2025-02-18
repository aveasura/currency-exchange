<%--
  Created by IntelliJ IDEA.
  User: aveasura
  Date: 17.02.2025
  Time: 13:10
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" isELIgnored="false" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<html>
<head>
    <title>html currency list</title>
</head>
<body>

<br/>
<a href="${pageContext.request.contextPath}/">back</a>
<br/>
<a href="${pageContext.request.contextPath}/currencyUpdate.jsp">go to currency update page</a>
<hr/>

<h2>all currencies:</h2>
<ul>
    <c:forEach var="currency" items="${currList}">
        <li>ID: ${currency.id} <br/>
            Code: ${currency.code} <br/>
            Full name: ${currency.fullName} <br/>
            Sign: ${currency.sign}
        </li>
        <hr/>
    </c:forEach>
</ul>

</body>
</html>
