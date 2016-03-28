<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="org.apache.logging.log4j.LogManager" %>
<%@ page import="org.apache.logging.log4j.Logger" %>
<%@ page import="java.net.InetAddress" %>
<% Logger logger = LogManager.getLogger("jsptest");
   String hostName = InetAddress.getLocalHost().getHostName();
   logger.info("error.jsp:" + hostName);
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>システムエラー</title>
</head>
<body>
<p>システムエラーが発生しました。</p>
</body>
</html>