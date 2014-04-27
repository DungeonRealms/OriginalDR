package minecade.dungeonrealms.database;

import java.sql.Connection;
import java.sql.DriverManager;

import minecade.dungeonrealms.Main;
import minecade.dungeonrealms.config.Config;
import minecade.dungeonrealms.enums.CC;

public class ConnectionPool {
	
	private static Connection con;
	
	public static boolean refresh = false;
	
	public static Connection getConnection() {
		try {
			if(refresh || con == null || con.isClosed()) {
				refresh = false;
				if(con != null) con.close();
				con = DriverManager.getConnection(Config.sql_url, Config.sql_user, Config.sql_password);
			}
		} catch(Exception e) {
			Main.d("Couldn't connect to the database!", CC.RED);
		}
		return con;
	}
	
}
