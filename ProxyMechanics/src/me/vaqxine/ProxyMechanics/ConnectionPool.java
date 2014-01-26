package me.vaqxine.ProxyMechanics;

import java.sql.Connection;
import java.sql.DriverManager;

public class ConnectionPool {
	public static String sql_url = "jdbc:mysql://72.20.40.38:7447/dungeonrealms";
	public static String eu_sql_url = "jdbc:mysql://37.59.22.14:7447/dungeonrealms";

	public static String sql_user = "slave_3XNZvi";
	public static String sql_password = "SgUmxYSJSFmOdro3";
	
    private static Connection con;  
    private ConnectionPool(){}  
    public static boolean refresh = false;
    public static Connection getConneciton()  
    {  
        try{  
    	if(refresh){
    		refresh = false;
    		if(con != null){
    			con.close();
    		}
    		
    		con = DriverManager.getConnection(sql_url, sql_user, sql_password);  
    	}
        if(con == null || con.isClosed()){  
            Class.forName("com.mysql.jdbc.Driver"); 
            con = DriverManager.getConnection(sql_url, sql_user, sql_password);  
        }  
        }catch(Exception e){e.printStackTrace();}  
        return con;  
    } 
}
