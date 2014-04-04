package me.vaqxine.Hive;

import java.sql.Connection;
import java.sql.DriverManager;

public class WebsiteConnectionPool {
	private static Connection con;
	
	private WebsiteConnectionPool() {}
	
	public static boolean refresh = false;
	
	public static Connection getConneciton() {
		try {
			if(refresh) {
				refresh = false;
				if(con != null) {
					con.close();
				}
				
				con = DriverManager.getConnection(Hive.site_sql_url, Hive.site_sql_user, Hive.site_sql_password);
			}
			if(con == null || con.isClosed()) {
				Class.forName("com.mysql.jdbc.Driver");
				con = DriverManager.getConnection(Hive.site_sql_url, Hive.site_sql_user, Hive.site_sql_password);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return con;
	}
}
