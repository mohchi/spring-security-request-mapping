<!DOCTYPE html>
<html>
<head>
<title>Spring Security Request Mapping Demo</title>
</head>
<body>
You are not logged in. <a href="${pageContext.request.contextPath}/spring_security_login">Login</a> using:
<ul>
	<li>Username: demo</li>
	<li>Password: demo</li>
</ul>
to access the <a href="${pageContext.request.contextPath}/secure">secure</a> page.
</body>
</html>