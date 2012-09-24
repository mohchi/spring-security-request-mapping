<!DOCTYPE html>
<html>
<head>
<title>Spring Security Request Mapping Demo</title>
</head>
<body>
<p>You are not logged in.
<a href="${pageContext.request.contextPath}/spring_security_login">Login</a> using
one of the following username/password combinations:</p>
<ul>
	<li>demo1 / demo1</li>
	<li>demo2 / demo2</li>
</ul>
<p>Once you've logged in, you may access either:</p>
<ul>
	<li><a href="${pageContext.request.contextPath}/secure/demo1">demo1's secure page</a></li>
	<li>or <a href="${pageContext.request.contextPath}/secure/demo2">demo2's secure page</a></li>
</ul>
</body>
</html>