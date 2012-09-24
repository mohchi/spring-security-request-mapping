<!DOCTYPE html>
<html>
<head>
<title>Spring Security Request Mapping Demo</title>
</head>
<body>
Hello ${name}, you may visit the <a href="${pageContext.request.contextPath}/secure">secure</a> page or <a href="${pageContext.request.contextPath}/j_spring_security_logout">log out</a>
</body>
</html>