package com.nec.samples;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

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

public class CancelServlet extends HttpServlet {

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
		Connection dbcon;
		try {
			dbcon = getDBConnection();
		} catch (Exception e) {
			throw new ServletException(e) ;
		}

		String delete_sql = "delete from session_info where id = ?" ;

		try {
			if( dbcon != null && !dbcon.isClosed() ) {
				PreparedStatement stmt = dbcon.prepareStatement( delete_sql ) ;
				stmt.setString( 1, session_id );

				stmt.executeUpdate() ;

			}
		} catch (SQLException e) {
			logger.error( "db operation is failed.", e );
			throw new ServletException(e);
		} finally {
			try {
				dbcon.close() ;
			} catch (SQLException e) {
				logger.warn( "db connection close.", e ) ;
			}
		}


		RequestDispatcher rd = ctx.getRequestDispatcher( "/jsp/complete.jsp" ) ;
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
