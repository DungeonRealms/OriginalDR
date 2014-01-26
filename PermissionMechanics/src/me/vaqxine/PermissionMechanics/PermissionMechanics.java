package me.vaqxine.PermissionMechanics;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import me.vaqxine.CommunityMechanics.CommunityMechanics;
import me.vaqxine.DonationMechanics.DonationMechanics;
import me.vaqxine.Hive.Hive;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class PermissionMechanics extends JavaPlugin implements Listener {
	static Logger log = Logger.getLogger("Minecraft");

	public static HashMap<String, String> rank_map = new HashMap<String, String>();

	public static HashMap<String, Integer> rank_forumgroup = new HashMap<String, Integer>();
	// Rank name, Forum group ID.

	public void onEnable() {    
		getServer().getPluginManager().registerEvents(this, this);
		
		rank_forumgroup.put("default", 2);
		rank_forumgroup.put("pmod", 11);
		rank_forumgroup.put("sub", 75);
		rank_forumgroup.put("sub+", 76);
		rank_forumgroup.put("sub++", 79);
		rank_forumgroup.put("gm", 72);
		rank_forumgroup.put("wd", 72);

		this.getServer().getScheduler().scheduleAsyncRepeatingTask(this, new Runnable() {
			public void run() {
				ConnectionPool.refresh = true;
			}
		}, 120 * 20L, 120 * 20L);

		log.info("[PermissionMechanics] has been enabled.");
	}

	public void onDisable() {
		log.info("[PermissionMechanics] has been disabled.");
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerJoin(final PlayerJoinEvent e){
		this.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
			public void run() {

				final Player p = e.getPlayer();
				String p_name = p.getName();
				if(p_name.length() > 13){
					p_name = p_name.substring(0, 13);
				}
				if(!(rank_map.containsKey(p.getName()))){return;}
				final String rank = getRank(p.getName());
				boolean set = false;

				if(rank.equalsIgnoreCase("pmod")){
					p.setPlayerListName(ChatColor.WHITE.toString() + p_name);
					set = true;
				}
				if(p.isOp() || rank.equalsIgnoreCase("gm")){
					p.setPlayerListName(ChatColor.AQUA.toString() + p_name);
					set = true;
				}
				if(set == false){
					p.setPlayerListName(ChatColor.GRAY.toString() + p_name);
				}
			}
		}, 10L);
	}

	public static void downloadRank(String p_name){
		Connection con = null;
		PreparedStatement pst = null;

		try {
			pst = ConnectionPool.getConneciton().prepareStatement( 
					"SELECT rank FROM player_database WHERE p_name = '" + p_name + "'");

			pst.execute();
			ResultSet rs = pst.getResultSet();
			if(!rs.next()){
				setRank(p_name, "default", true);
				return;
			}
			String rank = rs.getString("rank");
			setRank(p_name, rank, false);

		} catch (SQLException ex) {
			log.log(Level.SEVERE, ex.getMessage(), ex);      

		} finally {
			try {
				if (pst != null) {
					pst.close();
				}
				if (con != null) {
					con.close();
				}

			} catch (SQLException ex) {
				log.log(Level.WARNING, ex.getMessage(), ex);
			}
		}
	}

	public static void uploadRank(final String pname){

		String rank = getRank(pname);

		Hive.sql_query.add("INSERT INTO player_database (p_name, rank)"
				+ " VALUES"
				+ "('"+ pname + "', '"+ rank +"') ON DUPLICATE KEY UPDATE rank ='" + rank + "'");

	}

	public static void setRank(String p_name, String rank, boolean upload_sql){
		Player p = null;
		if(Bukkit.getPlayer(p_name) != null && Bukkit.getPlayer(p_name).isOnline()){
			p = Bukkit.getPlayer(p_name);
			p_name = p.getName(); // Correct the name if we can.
		}

		if(rank == null){
			rank = "default";
		}

		if(rank_map.containsKey(p_name)){
			String current_rank = rank_map.get(p_name);
			if(current_rank == null){
				current_rank = "default";
			}
			if(current_rank != null){
				if((current_rank.equalsIgnoreCase("pmod") && !rank.equalsIgnoreCase("gm") && !rank.equalsIgnoreCase("default")) 
						|| current_rank.equalsIgnoreCase("gm") && !rank.equalsIgnoreCase("default")){
					// Not demote, not promote, ignore sub.
					return;
				}
				else{
					rank_map.put(p_name, rank);
				}
			}
		}
		else{
			rank_map.put(p_name, rank);
		}

		final String frank = rank;

		if(upload_sql){
			uploadRank(p_name);
			final String fp_name = p_name;
			Thread t = new Thread(new Runnable(){
				public void run(){
					int user_id = DonationMechanics.getForumUserID(fp_name);
					DonationMechanics.addForumGroup(user_id, rank_forumgroup.get(frank.toLowerCase()));
					Socket kkSocket = null;
					PrintWriter out = null;
					try {

						kkSocket = new Socket();
						//kkSocket.bind(new InetSocketAddress(Hive.local_IP, Hive.transfer_port+1));
						kkSocket.connect(new InetSocketAddress(Hive.Proxy_IP, Hive.transfer_port), 250);
						out = new PrintWriter(kkSocket.getOutputStream(), true);

						out.println("[rank]" + fp_name + "@" + frank);

					} catch (IOException e) {
						e.printStackTrace();
					}

					if(out != null){
						out.close();
					}
				}
			});
			t.start();
		}

		if(p != null){
			String format_rank = rank;
			if(rank.equalsIgnoreCase("sub")){
				format_rank = ChatColor.GREEN + "Subscriber";
			}
			if(rank.equalsIgnoreCase("sub+")){
				format_rank = ChatColor.GOLD + "Subscriber+";
			}
			if(rank.equalsIgnoreCase("sub++")){
				format_rank = ChatColor.DARK_AQUA + "Subscriber++";
			}
			if(rank.equalsIgnoreCase("pmod")){
				format_rank = ChatColor.WHITE + "Player Moderator";
			}
			if(rank.equalsIgnoreCase("gm")){
				format_rank = ChatColor.AQUA + "Game Master";
			}
			if(rank.equalsIgnoreCase("wd")){
				format_rank = ChatColor.AQUA + "World Designer";
			}

			p.sendMessage("");
			p.sendMessage(ChatColor.YELLOW + "" + "Your Dungeon Realms rank has changed to '" + ChatColor.BOLD + format_rank + ChatColor.YELLOW + "'");
			p.sendMessage("");
		}
		
		log.info("[PermissionMechanics] Set " + p_name + "'s RANK to " + rank);
	}

	public static String getRank(String p_name){
		p_name = p_name.replaceAll("'", "''");

		if(!(rank_map.containsKey(p_name))){
			PreparedStatement pst = null;

			try {
				pst = ConnectionPool.getConneciton().prepareStatement( 
						"SELECT rank FROM player_database WHERE p_name = '" + p_name + "'");

				pst.execute();
				ResultSet rs = pst.getResultSet();
				
				if(!rs.next()){
					return "default";
				}
				
				final String rank = rs.getString("rank");
				final String fp_name = p_name;
				
				if(rank == null || rank.equalsIgnoreCase("null")){
					return "default";
				}
				
				if(rank != null){
					// Send rank cross-server so it's locally saved.
					List<Object> qdata = new ArrayList<Object>();
					qdata.add("[rank_map]" + fp_name + ":" + rank);
					qdata.add(null);
					qdata.add(true);
					CommunityMechanics.social_query_list.put(p_name, qdata);
					//CommunityMechanics.sendPacketCrossServer("[rank_map]" + fp_name + ":" + rank, -1, true);
				}

				return rank;

			} catch (SQLException ex) {
				log.log(Level.SEVERE, ex.getMessage(), ex);
				return "default";

			} finally {
				try {
					if (pst != null) {
						pst.close();
					}

				} catch (SQLException ex) {
					log.log(Level.WARNING, ex.getMessage(), ex);
					return "default";
				}
			}
		}

		return rank_map.get(p_name);
	}

	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if (cmd.getName().equalsIgnoreCase("setrank")) {
			Player p = null;
			if(sender instanceof Player){
				p = (Player) sender;
				if(!(p.isOp())){
					return true;
				}
			}
			if(args.length != 2){
				if(p != null){
					p.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Incorrect Syntax");
					p.sendMessage(ChatColor.RED + "/setrank <PLAYER> <RANK>");
					p.sendMessage(ChatColor.RED + "Ranks: " + ChatColor.GRAY + "Default | SUB | SUB+ | SUB++ | PMOD | GM | WD");
				}
				return true;
			}

			String p_name = args[0];
			String rank = args[1];

			if(!(rank.equalsIgnoreCase("default")) && !(rank.equalsIgnoreCase("wd")) && !(rank.equalsIgnoreCase("sub")) && !(rank.equalsIgnoreCase("sub+")) && !(rank.equalsIgnoreCase("sub++")) && !(rank.equalsIgnoreCase("PMod")) && !(rank.equalsIgnoreCase("GM"))){
				if(p != null){
					p.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Invalid Rank '" + rank + "'");
					p.sendMessage(ChatColor.RED + "/setrank <PLAYER> <RANK>");
					p.sendMessage(ChatColor.RED + "Ranks: " + ChatColor.GRAY + "Default | SUB | SUB+ | SUB++ | PMOD | GM | WD");
				}
				return true;
			}

			setRank(p_name, rank, true);

			if(p != null){
				p.sendMessage(ChatColor.GREEN + "You have set the user " + p_name + " to the rank of " + rank + " on all Dungeon Realm servers.");
			}

		}

		if (cmd.getName().equalsIgnoreCase("gmhelp")) {
			Player p = (Player)sender;

			if(!getRank(p.getName()).equalsIgnoreCase("gm") && !(p.isOp())){
				return true;
			}

			if(args.length != 0){
				p.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Incorrect Syntax");
				p.sendMessage(ChatColor.RED + "/gmhelp");
				p.sendMessage(ChatColor.GRAY + "Displays a list of Game Master commands.");
				return true;
			}

			p.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "              " + " *** Game Master Commands ***");
			p.sendMessage(ChatColor.AQUA + "/mute <PLAYER> <TIME(minutes)>" + ChatColor.GRAY + " Mutes PLAYER for TIME minutes from local and global chat.");
			p.sendMessage(ChatColor.AQUA + "/kick <PLAYER> <REASON>" + ChatColor.GRAY + " Kicks PLAYER and displays REASON to them.");
			p.sendMessage(ChatColor.AQUA + "/ban <PLAYER> <TIME(hours)>" + ChatColor.GRAY + " Bans PLAYER for TIME minutes from all servers.");
			//p.sendMessage(ChatColor.AQUA + "/flag <PLAYER> (REASON)" + ChatColor.GRAY + " Flags PLAYER for REASON for other mods to see. Leave REASON blank to view flags on player.");
		}

		if (cmd.getName().equalsIgnoreCase("pmhelp")) {
			Player p = (Player) sender;

			if(!getRank(p.getName()).equalsIgnoreCase("pmod") && !(p.isOp())){
				return true;
			}

			if(args.length != 0){
				p.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Incorrect Syntax");
				p.sendMessage(ChatColor.RED + "/pmhelp");
				p.sendMessage(ChatColor.GRAY + "Displays a list of Player Moderator commands.");
				return true;
			}

			p.sendMessage(ChatColor.WHITE + "" + ChatColor.BOLD + "              " + " *** Player Moderator Commands ***");
			p.sendMessage(ChatColor.WHITE + "/mute <PLAYER> <TIME(minutes)>" + ChatColor.GRAY + " Mutes PLAYER for TIME minutes from local and global chat.");
			p.sendMessage(ChatColor.WHITE + "/kick <PLAYER> <REASON>" + ChatColor.GRAY + " Kicks PLAYER and displays REASON to them.");
			p.sendMessage(ChatColor.WHITE + "/ban <PLAYER> <TIME(hours)>" + ChatColor.GRAY + " Bans PLAYER for TIME minutes from all servers.");
			p.sendMessage(ChatColor.WHITE + "/flag <PLAYER> (REASON)" + ChatColor.GRAY + " Flags PLAYER for REASON for other mods to see. Leave REASON blank to view flags on player.");
		}

		return true;
	}
}
