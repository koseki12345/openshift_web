<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="org.apache.logging.log4j.LogManager" %>
<%@ page import="org.apache.logging.log4j.Logger" %>
<%@ page import="java.net.InetAddress" %>
<% Logger logger = LogManager.getLogger("jsptest");
   String hostName = InetAddress.getLocalHost().getHostName();
   logger.info("complete.jsp:" + hostName);
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>完了画面</title>
</head>
<body>
<p class="midashi">完了画面</p>
<p>登録に成功しました。</p>
</body>
</html>