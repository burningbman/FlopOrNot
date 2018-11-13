<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
<script type="text/javascript" src="<c:url value="/resources/js/index.js" />"></script>
<script type="text/javascript" src="<c:url value="/resources/js/jquery-3.3.1.js" />"></script>
<title>Flop or Not</title>
</head>
<body>
	<h1>Flop or Not</h1>

	<form id="form">
	Actor:<br>
	<input type="text" name="actor"><br>
	<input type="submit" value="Calculate Rating">
	</form>
</body>
</html>