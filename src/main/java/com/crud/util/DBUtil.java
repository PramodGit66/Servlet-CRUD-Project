package com.crud.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBUtil {
	private static final String JDBC_URL = "jdbc:mysql://localhost:3306/cruddb?useSSL=false";
	private static final String JDBC_USERNAME = "root";
	private static final String JDBC_PASSWORD = "root";

	public static Connection getConnection() throws SQLException {
		Connection connection = null;
		try {
			// Load MySQL JDBC Driver
			Class.forName("com.mysql.cj.jdbc.Driver");
			// Establish the connection
			connection = DriverManager.getConnection(JDBC_URL, JDBC_USERNAME, JDBC_PASSWORD);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new SQLException(e);
		}

		return connection;
	}

}
