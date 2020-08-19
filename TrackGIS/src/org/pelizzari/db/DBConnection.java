package org.pelizzari.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBConnection {

	public static Connection con;

	public static Connection getCon() {
		if(con == null) {
			try {
				Class.forName("org.postgresql.Driver");

				String url = "jdbc:postgresql://localhost:5433/bd_evotram";
				Properties props = new Properties();
				props.setProperty("user","postgres");
				props.setProperty("password","postgres");
				//props.setProperty("ssl","true");
				con = DriverManager.getConnection(url, props);
				
				//// EMSA
				//String url = "jdbc:mysql://tstatdata1.emsa.local:3306/ai";
				//con = DriverManager.getConnection(url, "pelizan", "ais");
				
			} catch (SQLException | ClassNotFoundException e) {
				System.err.println("Cannot make DB connection");
				e.printStackTrace();
				System.exit(-1);			}			
		}
		return con;
	}

}
