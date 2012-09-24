<!DOCTYPE html>
<html>
<head>
<title>Spring Security Request Mapping Demo</title>
</head>
<body>
<p>Hello ${name}! You may visit your own secure page but not the other:</p>
<ul>
	<li><a href="${pageContext.request.contextPath}/secure/demo1">demo1's secure page</a></li>
	<li><a href="${pageContext.request.contextPath}/secure/demo2">demo2's secure page</a></li>
</ul>
<p><a href="${pageContext.request.contextPath}/j_spring_security_logout">Log out</a></p>
</body>
</html>