<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="org.apache.logging.log4j.LogManager" %>
<%@ page import="org.apache.logging.log4j.Logger" %>
<%@ page import="java.net.InetAddress" %>
<% Logger logger = LogManager.getLogger("jsptest");
   String hostName = InetAddress.getLocalHost().getHostName();
   logger.info("confirm.jsp:" + hostName);
%>
<%
	String user = (String)request.getParameter( "user" ) ;
	String password = (String)request.getParameter( "pass" ) ;
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>確認画面</title>
</head>
<body>
<form method="post" id="confirm" >
<p class="midashi">確認画面</p>
<table>
<tr>
<td>account:</td>
<td><%= user %></td>
</tr>
<tr>
<td>pass:</td>
<td><%= password %></td>
</tr>
</table>
<br/>
<input type="submit" id="send" value="OK" onclick="form.action='regist'; return true" /><input type="submit" id="cancel" value="Cancel" onclick="form.action='cancel'; return true" />
</form>
</body>
</html>