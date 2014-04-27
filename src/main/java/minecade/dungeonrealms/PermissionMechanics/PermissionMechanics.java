package minecade.dungeonrealms.PermissionMechanics;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import minecade.dungeonrealms.Main;
import minecade.dungeonrealms.CommunityMechanics.CommunityMechanics;
import minecade.dungeonrealms.Hive.Hive;
import minecade.dungeonrealms.PermissionMechanics.commands.CommandGMHelp;
import minecade.dungeonrealms.PermissionMechanics.commands.CommandPMHelp;
import minecade.dungeonrealms.PermissionMechanics.commands.CommandSetRank;
import minecade.dungeonrealms.database.ConnectionPool;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PermissionMechanics implements Listener {
	static Logger log = Logger.getLogger("Minecraft");
	
	public static HashMap<String, String> rank_map = new HashMap<String, String>();
	
	public static HashMap<String, Integer> rank_forumgroup = new HashMap<String, Integer>();
	
	// Rank name, Forum group ID.
	
	public void onEnable() {
		Main.plugin.getServer().getPluginManager().registerEvents(this, Main.plugin);
		
		Main.plugin.getCommand("setrank").setExecutor(new CommandSetRank());
		Main.plugin.getCommand("gmhelp").setExecutor(new CommandGMHelp());
		Main.plugin.getCommand("pmhelp").setExecutor(new CommandPMHelp());
		
		rank_forumgroup.put("default", 2);
		rank_forumgroup.put("pmod", 11);
		rank_forumgroup.put("sub", 75);
		rank_forumgroup.put("sub+", 76);
		rank_forumgroup.put("sub++", 79);
		rank_forumgroup.put("gm", 72);
		rank_forumgroup.put("wd", 72);
		
		log.info("[PermissionMechanics] has been enabled.");
	}
	
	public void onDisable() {
		log.info("[PermissionMechanics] has been disabled.");
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerJoin(final PlayerJoinEvent e) {
		Main.plugin.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
			public void run() {
				
				final Player p = e.getPlayer();
				String p_name = p.getName();
				if(p_name.length() > 13) {
					p_name = p_name.substring(0, 13);
				}
				if(!(rank_map.containsKey(p.getName()))) { return; }
				final String rank = getRank(p.getName());
				boolean set = false;
				
				if(rank.equalsIgnoreCase("pmod")) {
					p.setPlayerListName(ChatColor.WHITE.toString() + p_name);
					set = true;
				}
				if(p.isOp() || rank.equalsIgnoreCase("gm")) {
					p.setPlayerListName(ChatColor.AQUA.toString() + p_name);
					set = true;
				}
				if(set == false) {
					p.setPlayerListName(ChatColor.GRAY.toString() + p_name);
				}
			}
		}, 10L);
	}
	
	public static void downloadRank(String p_name) {
		Connection con = null;
		PreparedStatement pst = null;
		
		try {
			pst = ConnectionPool.getConnection().prepareStatement("SELECT rank FROM player_database WHERE p_name = '" + p_name + "'");
			
			pst.execute();
			ResultSet rs = pst.getResultSet();
			if(!rs.next()) {
				setRank(p_name, "default", true);
				return;
			}
			String rank = rs.getString("rank");
			setRank(p_name, rank, false);
			
		} catch(SQLException ex) {
			log.log(Level.SEVERE, ex.getMessage(), ex);
			
		} finally {
			try {
				if(pst != null) {
					pst.close();
				}
				if(con != null) {
					con.close();
				}
				
			} catch(SQLException ex) {
				log.log(Level.WARNING, ex.getMessage(), ex);
			}
		}
	}
	
	public static void uploadRank(final String pname) {
		
		String rank = getRank(pname);
		
		Hive.sql_query.add("INSERT INTO player_database (p_name, rank)" + " VALUES" + "('" + pname + "', '" + rank + "') ON DUPLICATE KEY UPDATE rank ='" + rank + "'");
		
	}

	@SuppressWarnings("deprecation")	
	public static void setRank(String p_name, String rank, boolean upload_sql) {
		Player p = null;
		if(Bukkit.getPlayer(p_name) != null && Bukkit.getPlayer(p_name).isOnline()) {
			p = Bukkit.getPlayer(p_name);
			p_name = p.getName(); // Correct the name if we can.
		}
		
		if(rank == null) {
			rank = "default";
		}
		
		if(rank_map.containsKey(p_name)) {
			String current_rank = rank_map.get(p_name);
			if(current_rank == null) {
				current_rank = "default";
			}
			if(current_rank != null) {
				if((current_rank.equalsIgnoreCase("pmod") && !rank.equalsIgnoreCase("gm") && !rank.equalsIgnoreCase("default")) || current_rank.equalsIgnoreCase("gm") && !rank.equalsIgnoreCase("default")) {
					// Not demote, not promote, ignore sub.
					return;
				} else {
					rank_map.put(p_name, rank);
				}
			}
		} else {
			rank_map.put(p_name, rank);
		}
		
		if(upload_sql) {
			uploadRank(p_name);
		}
		
		if(p != null) {
			String format_rank = rank;
			if(rank.equalsIgnoreCase("sub")) {
				format_rank = ChatColor.GREEN + "Subscriber";
			}
			if(rank.equalsIgnoreCase("sub+")) {
				format_rank = ChatColor.GOLD + "Subscriber+";
			}
			if(rank.equalsIgnoreCase("sub++")) {
				format_rank = ChatColor.DARK_AQUA + "Subscriber++";
			}
			if(rank.equalsIgnoreCase("pmod")) {
				format_rank = ChatColor.WHITE + "Player Moderator";
			}
			if(rank.equalsIgnoreCase("gm")) {
				format_rank = ChatColor.AQUA + "Game Master";
			}
			if(rank.equalsIgnoreCase("wd")) {
				format_rank = ChatColor.AQUA + "World Designer";
			}
			
			p.sendMessage("");
			p.sendMessage(ChatColor.YELLOW + "" + "Your Dungeon Realms rank has changed to '" + ChatColor.BOLD + format_rank + ChatColor.YELLOW + "'");
			p.sendMessage("");
		}
		
		log.info("[PermissionMechanics] Set " + p_name + "'s RANK to " + rank);
	}
	
	public static String getRank(String p_name) {
		p_name = p_name.replaceAll("'", "''");
		
		if(!(rank_map.containsKey(p_name))) {
			PreparedStatement pst = null;
			
			try {
				pst = ConnectionPool.getConnection().prepareStatement("SELECT rank FROM player_database WHERE p_name = '" + p_name + "'");
				
				pst.execute();
				ResultSet rs = pst.getResultSet();
				
				if(!rs.next()) { return "default"; }
				
				final String rank = rs.getString("rank");
				final String fp_name = p_name;
				
				if(rank == null || rank.equalsIgnoreCase("null")) { return "default"; }
				
				if(rank != null) {
					// Send rank cross-server so it's locally saved.
					List<Object> qdata = new ArrayList<Object>();
					qdata.add("[rank_map]" + fp_name + ":" + rank);
					qdata.add(null);
					qdata.add(true);
					CommunityMechanics.social_query_list.put(p_name, qdata);
					//CommunityMechanics.sendPacketCrossServer("[rank_map]" + fp_name + ":" + rank, -1, true);
				}
				
				return rank;
				
			} catch(SQLException ex) {
				log.log(Level.SEVERE, ex.getMessage(), ex);
				return "default";
				
			} finally {
				try {
					if(pst != null) {
						pst.close();
					}
					
				} catch(SQLException ex) {
					log.log(Level.WARNING, ex.getMessage(), ex);
					return "default";
				}
			}
		}
		
		return rank_map.get(p_name);
	}
	
	public boolean isPMOD(String p_name) {
		String rank = getRank(p_name);
		return rank.equalsIgnoreCase("GM") || rank.equalsIgnoreCase("PMOD") ? true : false;
	}
	
	public static boolean isGM(String p_name) {
		String rank = getRank(p_name);
		return rank.equalsIgnoreCase("GM");
	}
	
}
