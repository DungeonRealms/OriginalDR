package minecade.dungeonrealms.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;

public class Config {

	
	
	
	
	// Watcha doin here?
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	// U sure u wanna be here?
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	// Well okay...
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
    public static List<String> us_public_servers = new ArrayList<String>(Arrays.asList("US-1", "US-2", "US-3", "US-4", "US-11"));
    public static List<String> us_private_servers = new ArrayList<String>(Arrays.asList("US-9", "US-10"));
    public static List<String> br_servers = new ArrayList<String>(Arrays.asList("BR-1"));
    
    public static int transfer_port = 6427;
    public static String Hive_IP = "72.20.40.38";

    public static int SQL_port = 7447;
    public static String sql_user = "slave_3XNZvi";
    public static String sql_password = "SgUmxYSJSFmOdro3";
    public static String sql_url = "jdbc:mysql://" + Hive_IP + ":" + SQL_port + "/dungeonrealms";

    public static int FTP_port = 21;
    public static String ftp_user = "agent";
    public static String ftp_pass = "9bgsMKsknkJ6OY"; 
    
    public static String version = "1.7";
    
    public static String local_IP = Bukkit.getIp();
    
}
