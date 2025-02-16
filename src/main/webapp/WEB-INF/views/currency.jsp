<%@ page import="org.myapp.dto.CurrencyDto" %><%--
  Created by IntelliJ IDEA.
  User: aveasura
  Date: 16.02.2025
  Time: 21:40
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
<head>
    <title>choose currency id</title>
</head>
<body>
<h1>Currency Details</h1>

<% CurrencyDto currency = (CurrencyDto) request.getAttribute("currency"); %>

<% if (currency != null) { %>
<p>ID: <%= currency.getId() %>
</p>
<p>Code: <%= currency.getCode() %>
</p>
<p>Full Name: <%= currency.getFullName() %>
</p>
<p>Sign: <%= currency.getSign() %>
</p>
<% } else { %>
<p>Currency not found</p>
<% } %>

</body>
</html>
