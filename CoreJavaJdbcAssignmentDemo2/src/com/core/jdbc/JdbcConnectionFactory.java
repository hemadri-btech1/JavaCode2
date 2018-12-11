package com.core.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Class to manage the database connection
 * 
 * @author Hemadri
 *
 */
public class JdbcConnectionFactory {

	private static final String CLASS_NOT_FOUND_EXCEPTION_WHILE_CONNECTING_TO_THE_DATABASE = "ClassNotFoundException while connecting to the database";
	private static final String SQL_EXCEPTION_WHILE_CONNECTING_TO_THE_DATABASE = "SQLException while connecting to the database";
	private static final String COM_MYSQL_JDBC_DRIVER = "com.mysql.jdbc.Driver";
	private static final String MYSQL_URL = "jdbc:mysql://localhost:3306/mysql_jdbc2";
	private static final String USERNAME = "root";
	private static final String PASSWORD = "hemadri";

	/**
	 * GET a connection to database
	 * 
	 * @return Connection
	 */
	public static Connection getConnection() {
		try {
			Class.forName(COM_MYSQL_JDBC_DRIVER);
			return DriverManager.getConnection(MYSQL_URL, USERNAME, PASSWORD);
		} catch (SQLException ex) {
			throw new RuntimeException(SQL_EXCEPTION_WHILE_CONNECTING_TO_THE_DATABASE, ex);
		} catch (ClassNotFoundException ex) {
			throw new RuntimeException(CLASS_NOT_FOUND_EXCEPTION_WHILE_CONNECTING_TO_THE_DATABASE, ex);
		}
	}

}