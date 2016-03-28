<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="org.apache.logging.log4j.LogManager" %>
<%@ page import="org.apache.logging.log4j.Logger" %>
<%@ page import="java.net.InetAddress" %>
<% Logger logger = LogManager.getLogger("jsptest");
   String hostName = InetAddress.getLocalHost().getHostName();
   logger.info("input.jsp:" + hostName);
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<!-- <link rel="stylesheet" href="css/sample.css" type="text/css"> -->
<title>入力画面</title>
</head>
<body>

<p class="midashi">入力画面</p>
<form action="input" method="post">
<table>
<tr>
<td>account:</td>
<td><input type="text" name="user"></input></td>
</tr>
<tr>
<td>pass:</td>
<td><input type="password" name="pass"></input></td>
</tr>
</table>
<br/>
<input type="submit" id="send" value="送信" onclick="form.action='input'; return true" />
</form>
</body>
</html>

