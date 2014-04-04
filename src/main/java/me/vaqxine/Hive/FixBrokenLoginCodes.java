package me.vaqxine.Hive;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import me.vaqxine.CommunityMechanics.CommunityMechanics;
import me.vaqxine.HealthMechanics.HealthMechanics;
import me.vaqxine.KarmaMechanics.KarmaMechanics;
import me.vaqxine.MountMechanics.MountMechanics;
import me.vaqxine.RealmMechanics.RealmMechanics;
import me.vaqxine.database.ConnectionPool;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class FixBrokenLoginCodes extends Thread {
	
	public void run() {
		
		// pending_upload(string)
		PreparedStatement pst = null;
		String motd = Hive.MOTD;
		
		int server_num_local = 1;
		if(motd.contains("US-V")) { return; }
		if(motd.contains("US-YT")) {
			server_num_local += 3000;
		}
		try {
			server_num_local = Integer.parseInt(motd.substring(motd.indexOf("-") + 1, motd.indexOf(" ")));
		} catch(NumberFormatException nfe) {
			// This will be thrown by US-YT or any none CC-## format.
			nfe.printStackTrace();
			return;
		}
		
		if(motd.contains("EU-")) {
			server_num_local += 1000;
		}
		
		if(motd.contains("BR-")) {
			server_num_local += 2000;
		}
		
		List<String> reported_online = new ArrayList<String>();
		List<String> sql_reported_online = new ArrayList<String>();
		List<String> to_fix = new ArrayList<String>();
		try {
			pst = ConnectionPool.getConnection().prepareStatement("SELECT p_name FROM player_database WHERE server_num = '" + server_num_local + "'");
			
			pst.execute();
			ResultSet rs = pst.getResultSet();
			while(rs.next()) {
				reported_online.add(rs.getString("p_name"));
				sql_reported_online.add(rs.getString("p_name"));
			}
			
		} catch(SQLException ex) {
			Hive.log.log(Level.SEVERE, ex.getMessage(), ex);
			
		} finally {
			try {
				if(pst != null) {
					pst.close();
				}
				
			} catch(SQLException ex) {
				Hive.log.log(Level.WARNING, ex.getMessage(), ex);
			}
		}
		
		File dir = new File(Hive.main_world_name + "/players/");
		for(File f : dir.listFiles()) {
			String p_name = f.getName().replaceAll(".dat", "");
			if(!(reported_online.contains(p_name))) {
				reported_online.add(p_name);
			}
		}
		
		List<String> actual_online = new ArrayList<String>();
		for(Player pl : Bukkit.getOnlinePlayers()) {
			if(pl.getPlayerListName().equalsIgnoreCase("")) {
				continue;
			} // NPC.
			if(pl.hasMetadata("NPC")) {
				continue;
			}
			actual_online.add(pl.getName());
		}
		
		for(String s : reported_online) {
			if(Hive.pending_upload.contains(s) && sql_reported_online.contains(s)) {
				continue;
			} // They are pending upload and still marked as 'online' this server, so the upload has not finished.
			if(!(actual_online.contains(s)) && !(Hive.lockout_players.contains(s)) && !(Hive.player_to_npc.containsKey(s)) && !(Hive.logout_time.containsKey(s))) {
				to_fix.add(s); // They're not online, but SQL says they are!
			}
		}
		
		setAllOffline(to_fix);
	}
	
	public void setAllOffline(List<String> plist) {
		PreparedStatement pst = null;
		//con = DriverManager.getConnection(sql_url, sql_user, sql_password);
		for(String p_name : plist) {
			
			try {
				if(p_name.length() > 16) {
					continue;
				}
				
				Hive.log.info("[Hive (SLAVE EDITION)] Fixed a corrupt server listing data code for " + p_name);
				new File(Hive.main_world_name + "/players/" + p_name + ".dat").delete();
				
				Hive.player_inventory.remove(p_name);
				Hive.player_location.remove(p_name);
				Hive.player_hp.remove(p_name);
				Hive.player_level.remove(p_name);
				Hive.player_food_level.remove(p_name);
				Hive.player_armor_contents.remove(p_name);
				Hive.player_ecash.remove(p_name);
				Hive.player_ip.remove(p_name);
				Hive.player_portal_shards.remove(p_name);
				Hive.player_first_login.remove(p_name);
				
				Hive.local_player_ip.remove(p_name);
				Hive.remote_player_data.remove(p_name);
				Hive.last_sync.remove(p_name);
				Hive.forum_usergroup.remove(p_name);
				Hive.login_time.remove(p_name);
				
				KarmaMechanics.align_map.remove(p_name);
				KarmaMechanics.align_time.remove(p_name);
				
				CommunityMechanics.ignore_list.remove(p_name);
				CommunityMechanics.buddy_list.remove(p_name);
				
				HealthMechanics.noob_player_warning.remove(p_name);
				HealthMechanics.noob_players.remove(p_name);
				
				RealmMechanics.realm_title.remove(p_name);
				RealmMechanics.realm_tier.remove(p_name);
				
				MountMechanics.mule_inventory.remove(p_name);
				MountMechanics.mule_itemlist_string.remove(p_name);
				
				pst = ConnectionPool.getConnection().prepareStatement("INSERT INTO player_database (p_name, server_num)" + " VALUES" + "('" + p_name + "', '" + (-1) + "') ON DUPLICATE KEY UPDATE server_num = '" + (-1) + "'");
				pst.executeUpdate();
				
			} catch(SQLException ex) {
				Hive.log.log(Level.SEVERE, ex.getMessage(), ex);
				Hive.log.info("[HIVE] Could not set " + p_name + " to offline.");
				continue;
				
			} finally {
				try {
					if(pst != null) {
						pst.close();
					}
					
				} catch(SQLException ex) {
					Hive.log.log(Level.WARNING, ex.getMessage(), ex);
				}
			}
		}
		
	}
	
}
