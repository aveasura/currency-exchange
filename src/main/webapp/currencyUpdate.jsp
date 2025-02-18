<%--
  Created by IntelliJ IDEA.
  User: aveasura
  Date: 18.02.2025
  Time: 19:44
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" isELIgnored="false" language="java" %>
<html>
<head>
    <title>Update</title>
</head>
<body>

<a href="${pageContext.request.contextPath}/">go to home page</a>
<br/>
<a href="${pageContext.request.contextPath}/currencies">check currency list</a>
<hr/>

<form action="${pageContext.request.contextPath}/currency/" method="post">
  <label for="choose">Choose id or code currency for update</label><br>
  <input type="text" id="choose" name="choose" placeholder="id or code" required><br/>
  <hr/>

  <span>Only filled fields will be updated</span>
  <br/>
  <label for="code">Code. (etc: USD)</label>
  <input type="text" id="code" name="code" placeholder="code"><br/>

  <label for="name">Full name. (etc: United States dollar)</label>
  <input type="text" id="name" name="name" placeholder="full name"><br/>

  <label for="sign">Sign. (etc: $)</label>
  <input type="text" id="sign" name="sign" placeholder="sign"><hr/>

  <button type="submit">Apply</button>
</form>

</body>
</html>
