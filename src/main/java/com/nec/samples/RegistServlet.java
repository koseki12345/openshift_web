package com.nec.samples;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

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

public class RegistServlet extends HttpServlet {

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

		String newId = "";
		
		Cookie[] cookies = req.getCookies();
		if(cookies != null){
			for (Cookie cookie : cookies){
				logger.info("name :" + cookie.getName() + "value :" + cookie.getValue());
				if("newId".equals(cookie.getName())){
					newId = cookie.getValue();
				}
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
			throw new ServletException(e);
		}


		String insert_sql = "insert into login_info values ( ?, ? )" ;
		String select_sql = "select name, value from session_info where id = ? " ;

		try {

			if( dbcon != null && !dbcon.isClosed() ) {
				PreparedStatement stmt = dbcon.prepareStatement( select_sql ) ;
				stmt.setString( 1, newId);
				ResultSet rs = stmt.executeQuery() ;

				logger.info("-------- row size : "+rs.getRow());
				Map<String, String> resultMap = new HashMap<String, String>() ;
				while( rs.next() ) {

					String name = rs.getString( "name" ) ;
					String value = rs.getString( "value" ) ;

					resultMap.put( name, value ) ;

				}

				stmt.close();

				if( resultMap.containsKey( "user" ) && resultMap.containsKey( "password" ) ) {

					stmt = dbcon.prepareStatement( insert_sql ) ;
					stmt.setString( 1, resultMap.get( "user" ) ) ;
					stmt.setString( 2, resultMap.get( "password" ) ) ;

					stmt.executeUpdate() ;

				}


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
