<%--
  Created by IntelliJ IDEA.
  User: aveasura
  Date: 19.02.2025
  Time: 15:54
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" isELIgnored="false" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<html>
<head>
    <title>exchange</title>
</head>
<body>

<h2>all rates</h2>
<ul>
    <c:forEach var="rates" items="${ratesList}">
        <li>ID: ${rates.id}</li>
        <li>from: ${rates.baseCurrencyId}</li>
        <li>to: ${rates.targetCurrencyId}</li>
        <li>rate: ${rates.rate}</li>
        <hr/>
    </c:forEach>
</ul>

</body>
</html>
