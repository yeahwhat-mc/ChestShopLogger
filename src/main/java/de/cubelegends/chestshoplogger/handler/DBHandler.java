package de.cubelegends.chestshoplogger.handler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBHandler {
	
	private String host;
	private int port;
	private String user;
	private String password;
	private String database;
	
	private Connection con;
	
	public DBHandler(String host, int port, String user, String password, String database) {
		this.host = host;
		this.port = port;
		this.user = user;
		this.password = password;
		this.database = database;
	}

	private Connection openConnection() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database + "?autoReconnect=true", user, password);
		} catch (SQLException e) {
		} catch (ClassNotFoundException e) {
		}
		return con;
	}

	public Connection getConnection() {
		if(con != null) {
			return con;
		} else {
			return openConnection();
		}
	}

	public void closeConnection() {
		if(con != null) {
			try {
				con.close();
			} catch (SQLException e) {
			} finally {
				con = null;
			}
		}
	}

}