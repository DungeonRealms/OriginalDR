package me.vaqxine.database;

import java.sql.Connection;
import java.sql.DriverManager;

import me.vaqxine.Hive.Hive;
import me.vaqxine.enums.CC;

public class ConnectionPool {
	
	private static Connection con;
	
	public static boolean refresh = false;
	
	public static Connection getConnection() {
		try {
			if(refresh || con == null || con.isClosed()) {
				refresh = false;				
				if(con != null) con.close();
				con = DriverManager.getConnection(Hive.sql_url, Hive.sql_user, Hive.sql_password);
			}
		} catch(Exception e) {
			System.err.println(CC.RED + "Couldn't connect to the database!");
		}
		return con;
	}
	
}
