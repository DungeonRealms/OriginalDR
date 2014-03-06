package me.vaqxine.PetMechanics;

import java.sql.Connection;
import java.sql.DriverManager;

import me.vaqxine.Hive.Hive;

public class ConnectionPool {
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
    		
    		con = DriverManager.getConnection(Hive.sql_url, Hive.sql_user, Hive.sql_password);  
    	}
        if(con==null){  
            Class.forName("com.mysql.jdbc.Driver");  
            con= DriverManager.getConnection(Hive.sql_url, Hive.sql_user, Hive.sql_password);  
        }  
        }catch(Exception e){e.printStackTrace();}  
        return con;  
    } 
}
