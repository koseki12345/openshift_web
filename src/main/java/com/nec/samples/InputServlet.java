package com.nec.samples;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import argo.jdom.JdomParser;
import argo.jdom.JsonNode;
import argo.jdom.JsonRootNode;

public class InputServlet extends HttpServlet {

	ServletContext ctx = null ;
	Logger logger = LogManager.getLogger( this.getClass() ) ;

	public void init( ServletConfig config ) {

		synchronized (this) {
			if( ctx == null ) {
				ctx = config.getServletContext() ;
			}

		}

	}

	protected void doGet( HttpServletRequest req, HttpServletResponse resp ) throws ServletException, IOException {

		doPost( req, resp ) ;

	}

	protected void doPost( HttpServletRequest req, HttpServletResponse resp ) throws ServletException, IOException {

		Cookie[] cookies = req.getCookies();
		if(cookies != null){
			for (Cookie cookie : cookies){
				logger.info("name :" + cookie.getName() + "value :" + cookie.getValue());
			}
		}

		String session_id = req.getRequestedSessionId() ;

		if( session_id == null ) {
			logger.info("session reget...");
			HttpSession session = req.getSession() ;
			session_id = session.getId() ;
		}

		cookies = req.getCookies();
		if(cookies != null){
			for (Cookie cookie : cookies){
				logger.info("name :" + cookie.getName() + "value :" + cookie.getValue());
			}
		}

		logger.info( "session id=" + session_id );
		
		// 一意な値を生成
		String newId = UUID.randomUUID().toString();
		logger.info("---new id :" + newId);
		// Cookie生成
		Cookie newCookie = new Cookie("newId", newId);
		// HttpResponseへCookie追加
		resp.addCookie(newCookie);

		Connection dbcon = null ;
		try {
			dbcon = getDBConnection();
		} catch (Exception e) {
			throw new ServletException(e);
		}

		String user = (String) req.getParameter( "user" ) ;
		String password = (String) req.getParameter( "pass" ) ;

		logger.debug( "user=" + user );
		logger.debug( "password=" + password );

		String insert_sql = "insert into session_info values ( ?, ?, ? )" ;

		try {
			if( dbcon != null && !dbcon.isClosed() ) {
				PreparedStatement stmt = dbcon.prepareStatement( insert_sql ) ;
				stmt.setString( 1, newId );
				stmt.setString( 2, "user");
				stmt.setString( 3, user );

				stmt.executeUpdate() ;

				stmt.setString( 1, newId );
				stmt.setString( 2, "password");
				stmt.setString( 3, password );

				stmt.executeUpdate() ;


			}
		} catch (SQLException e) {
			logger.error( "db operation is failed.", e );
			throw new ServletException(e) ;
		} finally {
			try {
				dbcon.close() ;
			} catch (SQLException e) {
				logger.warn( "db connection close.", e ) ;
			}
		}

		RequestDispatcher rd = ctx.getRequestDispatcher( "/jsp/confirm.jsp" ) ;
		rd.forward(req, resp);
	}


	public Connection getDBConnection() throws Exception {

		String vcap_services = System.getenv( "VCAP_SERVICES" ) ;
		logger.debug( "VCAP_SERVICES=" + vcap_services );
		logger.debug( "env=" + System.getenv() ) ;
		Connection dbConnection = null ;

		try {

			Class.forName( "com.mysql.jdbc.Driver" ) ;

			if( vcap_services != null && vcap_services.length() > 0 ) {
				JsonRootNode root = new JdomParser().parse( vcap_services ) ;

				JsonNode mysqlNode = root.getNode( "mysql" ) ;
				JsonNode credentials = mysqlNode.getNode(0).getNode( "credentials" ) ;

				String dbname = credentials.getStringValue( "name" ) ;
				String hostname = credentials.getStringValue( "hostname" );
				String user = credentials.getStringValue( "user" ) ;
				String password = credentials.getStringValue( "password" ) ;
				String port = credentials.getNumberValue( "port" ) ;

				String dbUrl = "jdbc:mysql://" + hostname + ":" + port + "/" + dbname ;
				logger.debug( dbUrl );

				dbConnection = DriverManager.getConnection( dbUrl, user, password ) ;

				if( dbConnection != null) {
					logger.debug( "Get DB Connection." ) ;
				}
			} else {

				String dbUrl = "jdbc:mysql://localhost:3306/helion_test" ;
				logger.debug( dbUrl );

				dbConnection = DriverManager.getConnection( dbUrl, "test1", "test1" ) ;

				if( dbConnection != null) {
					logger.debug( "Get DB Connection." ) ;
				}
			}

		} catch ( Exception e ) {
			logger.error( "Caught error:", e );
			throw e ;
		}

		return dbConnection ;

	}

}
