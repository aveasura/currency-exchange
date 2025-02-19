<%--
  Created by IntelliJ IDEA.
  User: aveasura
  Date: 19.02.2025
  Time: 16:31
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>add exchange rate</title>
</head>
<body>

<form action="${pageContext.request.contextPath}/exchangeRates" method="post">
    <label for="from">from</label>
    <input type="text" id="from" name="from" placeholder="id from" required><br/>

    <label for="to">to</label>
    <input type="text" id="to" name="to" placeholder="id to" required><br/>

    <label for="rate">exchange rate</label>
    <input type="text" id="rate" name="rate" placeholder="rate" required><hr/>

    <button type="submit">Apply</button>
</form>

</body>
</html>
