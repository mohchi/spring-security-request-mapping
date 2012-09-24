<!DOCTYPE html>
<html>
<head>
<title>Spring Security Request Mapping Demo</title>
</head>
<body>
<p>You are on ${name}'s secure page. Return
<a href="${pageContext.request.contextPath}/">home</a>
or <a href="${pageContext.request.contextPath}/j_spring_security_logout">log out</a>.</p>
</body>
</html>