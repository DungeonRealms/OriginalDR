package me.vaqxine.ClassMechanics;


import java.awt.Color;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.minecraft.server.EntityPlayer;
import net.minecraft.server.Packet20NamedEntitySpawn;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class ClassMechanics extends JavaPlugin implements Listener {
	Logger log = Logger.getLogger("Minecraft");
	
	//TODO: Make sure the system can set playesr back to default classes if they no longer have a stat above 20.
	//TODO: Address text going off page on high leves for EXP + %.
	
	//TODO: Fix the rounding of %...
	
    private static String sql_url = "jdbc:mysql://74.63.199.162:3306/dungeonrealms";
    private static String sql_user = "agent";
    private static String sql_password = "charliesheen";
	
	ChatColor sword_color = ChatColor.GOLD;
	ChatColor axe_color = ChatColor.DARK_RED;
	ChatColor spear_color = ChatColor.DARK_AQUA;
	
	ChatColor bows_color = ChatColor.GREEN;
	ChatColor daggers_color = ChatColor.GRAY;
	ChatColor traps_color = ChatColor.DARK_GRAY;
	
	ChatColor arcane_color = ChatColor.AQUA;
	ChatColor illusion_color = ChatColor.LIGHT_PURPLE;
	ChatColor necromacy_color = ChatColor.DARK_PURPLE;
	
	static HashMap<String, String> class_map = new HashMap<String, String>();
	
	static HashMap<Player, ChatColor> color_map = new HashMap<Player, ChatColor>();

	
	public void onEnable() {
		getServer().getPluginManager().registerEvents(this, this);
		log.info("[ClassMechanics] has been enabled.");
	}
	
	public void onDisable() {
		log.info("[ClassMechanics] has been disabled.");
	}
	
	public boolean loadClassData(Player p){
        Connection con = null;
        PreparedStatement pst = null;
        String class_data = "";
        try {
            con = DriverManager.getConnection(sql_url, sql_user, sql_password);
            pst = con.prepareStatement( 
         		   "SELECT pname, data FROM class_map WHERE pname = '" + p.getName() + "'");
            
 		  pst.execute();
 		  ResultSet rs = pst.getResultSet();
 		  
 	      if(rs.next() == false){return false;}
 	    
 	      class_data = rs.getString("data");
 	      
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
        
        class_map.put(p.getName(), class_data);
        log.info("[ClassMechanics] Loaded profile for " + p.getName() + ".");
        return true;
	}
	
	public boolean uploadClassData(Player p){
		    Connection con = null;
	        PreparedStatement pst = null;
	        String class_data = "";
	        if(!(class_map.containsKey(p.getName()))){
	        	return false;
	        }
	        class_data = class_map.get(p.getName());
	        try {
	            con = DriverManager.getConnection(sql_url, sql_user, sql_password);
	            pst = con.prepareStatement( 
	          		   "INSERT INTO class_map (pname, data)"
	  	               + " VALUES"
	  	               + "('"+ p.getName() + "', '"+ class_data +"') ON DUPLICATE KEY UPDATE data = '" + class_data + "'");
	            
	 		  pst.executeUpdate();

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
	        
	        log.info("[ClassMechanics] Uploaded profile for " + p.getName() + ".");
	        return true;
	}
	
	public void setClassColor(Player p){
		ChatColor c = getClassColor(p);
		String real_name = p.getName();
		
		EntityPlayer ent_p = ((CraftPlayer) p).getHandle();
		ent_p.name = c.toString() + p.getName();
        for(Player pl : this.getServer().getOnlinePlayers()){
            if(pl != p){
                ((CraftPlayer) pl).getHandle().netServerHandler.sendPacket(new Packet20NamedEntitySpawn(ent_p));
            }
        }

        ent_p.name = real_name;
		color_map.put(p, c);
		
	}
	
	public ChatColor getClassColor(Player p){
		String class_name = getClass(p);
		ChatColor c = null;
		
		if(class_name.equalsIgnoreCase("fighter")){
			c = ChatColor.RED;
		}
			if(class_name.equalsIgnoreCase("knight")){
				c = sword_color;
			}
			if(class_name.equalsIgnoreCase("beserker")){
				c = axe_color;
			}
			if(class_name.equalsIgnoreCase("spearman")){
				c = spear_color;
			}
		
		if(class_name.equalsIgnoreCase("hunter")){
			c = ChatColor.DARK_GREEN;
		}
			if(class_name.equalsIgnoreCase("archer")){
				c = bows_color;
			}
			if(class_name.equalsIgnoreCase("rogue")){
				c = daggers_color;
			}
			if(class_name.equalsIgnoreCase("trapper")){
				c = traps_color;
			}
		
		if(class_name.equalsIgnoreCase("mage")){
			c = ChatColor.BLUE;
		}
			if(class_name.equalsIgnoreCase("arcanist")){
				c = arcane_color;
			}
			if(class_name.equalsIgnoreCase("illusionist")){
				c = illusion_color;
			}
			if(class_name.equalsIgnoreCase("necromancer")){
				c = necromacy_color;
			}
		
		return c;
	}
	
	public ChatColor getStatColor(String stat){
		if(stat.equalsIgnoreCase("swords")){
			return sword_color;
		}
		if(stat.equalsIgnoreCase("axes")){
			return axe_color;
		}
		if(stat.equalsIgnoreCase("spears")){
			return spear_color;
		}
		if(stat.equalsIgnoreCase("bows")){
			return bows_color;
		}
		if(stat.equalsIgnoreCase("daggers")){
			return daggers_color;
		}
		if(stat.equalsIgnoreCase("traps")){
			return traps_color;
		}
		if(stat.equalsIgnoreCase("arcane")){
			return arcane_color;
		}
		if(stat.equalsIgnoreCase("illusion")){
			return illusion_color;
		}
		if(stat.equalsIgnoreCase("necromancy")){
			return necromacy_color;
		}
		
		return ChatColor.BLACK;
	}
	
	public boolean setClass(Player p, String class_name, boolean overwrite_stats){
		if(overwrite_stats == true){
			class_map.put(p.getName(), class_name + ":0=0,0=0,0=0");
			return true;
		}
		if(overwrite_stats == false && class_map.containsKey(p.getName())){
			// TODO: Need to just change the name.
			String old_data = class_map.get(p.getName());
			String old_class = old_data.substring(0, old_data.indexOf(":"));
			old_data = old_data.replaceAll(old_class, class_name.toLowerCase());
			class_map.put(p.getName(), old_data);
			setClassColor(p);
			return true;
		}
		
		else{return false;}
	}
	
	public void setLevel(Player p , String stat, int new_val){
		if(!(hasSkill(p, stat))){
			return;
		}
		
		String class_data = class_map.get(p.getName());
		String class_name = class_data.substring(0, class_data.indexOf(":"));
		
		String[] stats = class_data.substring(class_data.indexOf(":") + 1, class_data.length()).split(",");
		
			if(stat.equalsIgnoreCase("swords")){
				class_map.put(p.getName(), class_name + ":" + new_val + "=0," + stats[1] + "," + stats[2]);
			}
			if(stat.equalsIgnoreCase("axes")){
				class_map.put(p.getName(), class_name + ":" + stats[0] + "," + new_val + "=0," + stats[2]);
			}
			if(stat.equalsIgnoreCase("spears")){
				class_map.put(p.getName(), class_name + ":" + stats[0] + "," + stats[1] + "," + new_val + "=0");
			}
		
			if(stat.equalsIgnoreCase("bows")){
				class_map.put(p.getName(), class_name + ":" + new_val + "=0," + stats[1] + "," + stats[2]);
			}
			if(stat.equalsIgnoreCase("daggers")){
				class_map.put(p.getName(), class_name + ":" + stats[0] + "," + new_val + "=0," + stats[2]);
			}
			if(stat.equalsIgnoreCase("traps")){
				class_map.put(p.getName(), class_name + ":" + stats[0] + "," + stats[1] + "," + new_val + "=0");
			}
		
		    if(stat.equalsIgnoreCase("arcane")){
				class_map.put(p.getName(), class_name + ":" + new_val + "=0," + stats[1] + "," + stats[2]);
			}
			if(stat.equalsIgnoreCase("illusion")){
				class_map.put(p.getName(), class_name + ":" + stats[0] + "," + new_val + "=0," + stats[2]);
			}
			if(stat.equalsIgnoreCase("necromancy")){
				class_map.put(p.getName(), class_name + ":" + stats[0] + "," + stats[1] + "," + new_val + "=0");
			}
			
			if(new_val >= 20){
				if(isHighestLevel(p, stat) && !(getClass(p).equalsIgnoreCase(getAStatsClass(stat)))){
					updateClassWithStat(p, stat);
					p.sendMessage("");
					p.sendMessage(ChatColor.GRAY + "*****************************");
					p.sendMessage(ChatColor.WHITE + "YOU ARE NOW A " + getClass(p).toUpperCase() + "!");
					p.sendMessage(ChatColor.GRAY + "*****************************");
				}
			}
	}
	
	public int getLevel(Player p, String stat){
		if(!(hasSkill(p, stat))){
			return 0;
		}
		String class_data = class_map.get(p.getName());
		//String class_name = class_data.substring(0, class_data.indexOf(":"));
		//log.info(class_data);
		String[] stats = class_data.substring(class_data.indexOf(":") + 1, class_data.length()).split(",");
		
		String[] stat_1, stat_2, stat_3;
		stat_1 = stats[0].split("=");
		stat_2 = stats[1].split("=");
		stat_3 = stats[2].split("=");
		//log.info(stat_2[0]);
		
			if(stat.equalsIgnoreCase("swords")){
				return Integer.parseInt(stat_1[0].replaceAll(":", "")); // 1st #
			}
			if(stat.equalsIgnoreCase("axes")){
				return Integer.parseInt(stat_2[0]); // 2nd #
			}
			if(stat.equalsIgnoreCase("spears")){
				return Integer.parseInt(stat_3[0]); // 3rd #
			}
		
			if(stat.equalsIgnoreCase("bows")){
				return Integer.parseInt(stat_1[0].replaceAll(":", "")); // 1st #
			}
			if(stat.equalsIgnoreCase("daggers")){
				return Integer.parseInt(stat_2[0]); // 2nd #
			}
			if(stat.equalsIgnoreCase("traps")){
				return Integer.parseInt(stat_3[0]); // 3rd #
			}
		
			if(stat.equalsIgnoreCase("arcane")){
				return Integer.parseInt(stat_1[0].replaceAll(":", "")); // 1st #
			}
			if(stat.equalsIgnoreCase("illusion")){
				return Integer.parseInt(stat_2[0]); // 2nd #
			}
			if(stat.equalsIgnoreCase("necromancy")){
				return Integer.parseInt(stat_3[0]); // 3rd #
			}

		return 0;
	}
	
	public void addXP(Player p, String stat, int amount){
		boolean level_up = false;
		
		if(!(hasSkill(p, stat))){
			log.info("[ClassMechanics] ERROR - Player " + p.getName() + " does not have a stat: " + stat);
			return;
		}
		
		int current_xp = getCurrentExp(p, stat);
		int needed_xp = getNeededExp(p, stat);
		
		int current_lvl = getLevel(p, stat);
		if(current_lvl >= 100){
			p.sendMessage(ChatColor.RED + "EXP gain event cancelled, already level 100.");
			return;
		}
		
		int new_total_exp = current_xp + amount;
		
		if(new_total_exp >= needed_xp){
			level_up = true; 
		}
		
		String class_data = class_map.get(p.getName());
		String class_name = class_data.substring(0, class_data.indexOf(":"));
		
		String[] stats = class_data.substring(class_data.indexOf(":") + 1, class_data.length()).split(",");

		int level_data = 0;
		if(level_up == true){
			level_data++;
			new_total_exp = 0;
		}
		
			if(stat.equalsIgnoreCase("swords")){
				level_data += Integer.parseInt(stats[0].split("=")[0]);
				class_map.put(p.getName(), class_name + ":" + level_data + "=" + new_total_exp + "," + stats[1] + "," + stats[2]);
			}
			if(stat.equalsIgnoreCase("axes")){
				level_data += Integer.parseInt(stats[1].split("=")[0]);
				class_map.put(p.getName(), class_name + ":" + stats[0] + "," + level_data + "=" + new_total_exp + "," + stats[2]);
			}
			if(stat.equalsIgnoreCase("spears")){
				level_data += Integer.parseInt(stats[2].split("=")[0]);
				class_map.put(p.getName(), class_name + ":" + stats[0] + "," + stats[1] + "," + level_data + "=" + new_total_exp);
			}
		
			if(stat.equalsIgnoreCase("bows")){
				level_data += Integer.parseInt(stats[0].split("=")[0]);
				class_map.put(p.getName(), class_name + ":" + level_data + "=" + new_total_exp + "," + stats[1] + "," + stats[2]);
			}
			if(stat.equalsIgnoreCase("daggers")){
				level_data += Integer.parseInt(stats[1].split("=")[0]);
				class_map.put(p.getName(), class_name + ":" + stats[0] + "," + level_data + "=" + new_total_exp + "," + stats[2]);
			}
			if(stat.equalsIgnoreCase("traps")){
				level_data += Integer.parseInt(stats[2].split("=")[0]);
				class_map.put(p.getName(), class_name + ":" + stats[0] + "," + stats[1] + "," + level_data + "=" + new_total_exp);
			}
		
			if(stat.equalsIgnoreCase("arcane")){
				level_data += Integer.parseInt(stats[0].split("=")[0]);
				class_map.put(p.getName(), class_name + ":" + level_data + "=" + new_total_exp + "," + stats[1] + "," + stats[2]);
			}
			if(stat.equalsIgnoreCase("illusion")){
				level_data += Integer.parseInt(stats[1].split("=")[0]);
				class_map.put(p.getName(), class_name + ":" + stats[0] + "," + level_data + "=" + new_total_exp + "," + stats[2]);
			}
			if(stat.equalsIgnoreCase("necromancy")){
				level_data += Integer.parseInt(stats[2].split("=")[0]);
				class_map.put(p.getName(), class_name + ":" + stats[0] + "," + stats[1] + "," + level_data + "=" + new_total_exp);
			}

 		   // QSkills's method:
		  //p.sendMessage(ChatColor.YELLOW + stat.substring(0, 1).toUpperCase() + stat.substring(1, stat.length()) + ": +" + (amount) + " EXP");
		
		ChatColor c = getStatColor(stat);
		if(level_up == false){
			p.sendMessage(ChatColor.GRAY + "+" + amount + " EXP " + c + "[" + calculatePercent(new_total_exp, needed_xp) + "%]");
		}
		if(level_up == true){
			int new_level = level_data;
			
			p.sendMessage(ChatColor.GRAY + "+" + amount + " EXP " + c + "[100%]");
			p.sendMessage(ChatColor.GREEN + "You have gained a level in " + stat + " (" + (new_level) + ").");
			
			if(new_level >= 20){
				if(isHighestLevel(p, stat) && !(getClass(p).equalsIgnoreCase(getAStatsClass(stat)))){
					updateClassWithStat(p, stat);
					p.sendMessage("");
					p.sendMessage(ChatColor.GRAY + "*****************************");
					p.sendMessage(ChatColor.WHITE + "YOU ARE NOW A " + getClass(p).toUpperCase() + "!");
					p.sendMessage(ChatColor.GRAY + "*****************************");
				}
			}

		}
	}
	
	public String getAStatsClass(String stat){
		if(stat.equalsIgnoreCase("swords")){
			return "Knight";
		}
		if(stat.equalsIgnoreCase("axes")){
			return "Beserker";
		}
		if(stat.equalsIgnoreCase("spears")){
			return "Spearman";
		}
		
		if(stat.equalsIgnoreCase("bows")){
			return "Archer";
		}
		if(stat.equalsIgnoreCase("daggers")){
			return "Rogue";
		}
		if(stat.equalsIgnoreCase("traps")){
			return "Trapper";
		}
		
		if(stat.equalsIgnoreCase("arcane")){
			return "Arcanist";
		}
		if(stat.equalsIgnoreCase("illusion")){
			return "Illusionist";
		}
		if(stat.equalsIgnoreCase("necromancy")){
			return "Necromancer";
		}
		
		return "";
	}
	
	public boolean isHighestLevel(Player p, String stat){
		String[] stat_list = getStatNames(getClass(p));
		int stat_lvl = getLevel(p, stat);
		for(String s : stat_list){
			if(s.equalsIgnoreCase(stat)){continue;}
			int temp_lvl = getLevel(p, s);
			if(stat_lvl < temp_lvl){
				return false;
			}
		}
		return true;
	}
	
	public int getCurrentExp(Player p, String stat){
		if(!(hasSkill(p, stat))){
			return 0;
		}
		
		String class_data = class_map.get(p.getName());
		//String class_name = class_data.substring(0, class_data.indexOf(":"));
		
		String[] stats = class_data.substring(class_data.indexOf(":") + 1, class_data.length()).split(",");
		
		String[] stat_1, stat_2, stat_3;
		stat_1 = stats[0].split("=");
		stat_2 = stats[1].split("=");
		stat_3 = stats[2].split("=");
		
			if(stat.equalsIgnoreCase("swords")){
				return Integer.parseInt(stat_1[1]); // 1st #
			}
			if(stat.equalsIgnoreCase("axes")){
				return Integer.parseInt(stat_2[1]); // 2nd #
			}
			if(stat.equalsIgnoreCase("spears")){
				return Integer.parseInt(stat_3[1]); // 3rd #
			}
		
			if(stat.equalsIgnoreCase("bows")){
				return Integer.parseInt(stat_1[1]); // 1st #
			}
			if(stat.equalsIgnoreCase("daggers")){
				return Integer.parseInt(stat_2[1]); // 2nd #
			}
			if(stat.equalsIgnoreCase("traps")){
				return Integer.parseInt(stat_3[1]); // 3rd #
			}
		
			if(stat.equalsIgnoreCase("arcane")){
				return Integer.parseInt(stat_1[1]); // 1st #
			}
			if(stat.equalsIgnoreCase("illusion")){
				return Integer.parseInt(stat_2[1]); // 2nd #
			}
			if(stat.equalsIgnoreCase("necromancy")){
				return Integer.parseInt(stat_3[1]); // 3rd #
			}
		
		return 0;
	}
	
	public int calculateNeededExp(int current_level){
		// 12(x^2 + 5x + 4)
		return 12 * ((current_level*current_level) + (5 * current_level) + 4);
	}
	
	public double calculatePercent(int currentxp, int neededxp){
		double cur = currentxp;
		double need = neededxp;
		
		return (double)Math.round((( ((cur / need) * 100.0)) * 1000) / 1000);
	}
	
	public int getNeededExp(Player p, String stat){
		if(!(hasSkill(p, stat))){
			return 0;
		}
		
		String class_data = class_map.get(p.getName());
		//String class_name = class_data.substring(0, class_data.indexOf(":"));
		
		String[] stats = class_data.substring(class_data.indexOf(":") + 1, class_data.length()).split(",");
		
		String[] stat_1, stat_2, stat_3;
		stat_1 = stats[0].split("=");
		stat_2 = stats[1].split("=");
		stat_3 = stats[2].split("=");
		
			if(stat.equalsIgnoreCase("swords")){
				return calculateNeededExp(Integer.parseInt(stat_1[0])); // 1st #
			}
			if(stat.equalsIgnoreCase("axes")){
				return calculateNeededExp(Integer.parseInt(stat_2[0])); // 2nd #
			}
			if(stat.equalsIgnoreCase("spears")){
				return calculateNeededExp(Integer.parseInt(stat_3[0])); // 3rd #
			}
		
			if(stat.equalsIgnoreCase("bows")){
				return calculateNeededExp(Integer.parseInt(stat_1[0])); // 1st #
			}
			if(stat.equalsIgnoreCase("daggers")){
				return calculateNeededExp(Integer.parseInt(stat_2[0])); // 2nd #
			}
			if(stat.equalsIgnoreCase("traps")){
				return calculateNeededExp(Integer.parseInt(stat_3[0])); // 3rd #
			}
		
			if(stat.equalsIgnoreCase("arcane")){
				return calculateNeededExp(Integer.parseInt(stat_1[0])); // 1st #
			}
			if(stat.equalsIgnoreCase("illusion")){
				return calculateNeededExp(Integer.parseInt(stat_2[0])); // 2nd #
			}
			if(stat.equalsIgnoreCase("necromancy")){
				return calculateNeededExp(Integer.parseInt(stat_3[0])); // 3rd #
			}
		
		return 0;
		
	}
	
	public String getClass(Player p){
		if(!(class_map.containsKey(p.getName()))){
			return null;
		}
		
		String class_string = class_map.get(p.getName());
		String result_string = class_string.substring(0, class_string.indexOf(":"));
		
		return result_string;
	}
	
	public String[] getStatNames(String class_name){
		String[] stat_list = new String[]{""};
		if(class_name.equalsIgnoreCase("fighter") || class_name.equalsIgnoreCase("knight") || class_name.equalsIgnoreCase("beserker") || class_name.equalsIgnoreCase("spearman")){
			stat_list = new String[]{"Swords", "Axes", "Spears"};
		}
		if(class_name.equalsIgnoreCase("hunter") || class_name.equalsIgnoreCase("archer") || class_name.equalsIgnoreCase("rogue") || class_name.equalsIgnoreCase("trapper")){
			stat_list = new String[]{"Bows", "Daggers", "Traps"};
		}
		if(class_name.equalsIgnoreCase("mage") || class_name.equalsIgnoreCase("arcanist") || class_name.equalsIgnoreCase("illusionist") || class_name.equalsIgnoreCase("necromancer")){
			stat_list = new String[]{"Arcane", "Illusion", "Necromancy"};
		}
		return stat_list;
	}
	
	public boolean hasSkill(Player p, String skill){
		String[] stat_list = getStatNames(getClass(p));
		for(String s : stat_list){
			if(s.equalsIgnoreCase(skill)){
				return true;
			}
		}
		
		return false;
	}
	
	public int getTotalLevel(Player p){
		int total_level = 0;
		String[] all_stats = getStatNames(getClass(p));
		for(String s : all_stats){
			int lvl = getLevel(p, s);
			total_level += lvl;
		}
		
		return total_level;
	}
	
	public void updateClassWithStat(Player p, String stat){
		if(stat.equalsIgnoreCase("swords")){
			setClass(p, "Knight", false);
		}
		if(stat.equalsIgnoreCase("axes")){
			setClass(p, "Beserker", false);
		}
		if(stat.equalsIgnoreCase("spears")){
			setClass(p, "Spearman", false);
		}
		
		if(stat.equalsIgnoreCase("bows")){
			setClass(p, "Archer", false);
		}
		if(stat.equalsIgnoreCase("daggers")){
			setClass(p, "Rogue", false);
		}
		if(stat.equalsIgnoreCase("traps")){
			setClass(p, "Trapper", false);
		}
		
		if(stat.equalsIgnoreCase("arcane")){
			setClass(p, "Arcanist", false);
		}
		if(stat.equalsIgnoreCase("illusion")){
			setClass(p, "Illusionist", false);
		}
		if(stat.equalsIgnoreCase("necromancy")){
			setClass(p, "Necromancer", false);
		}
	}
	
	public void updateClass(Player p){
		String current_class = getClass(p);
		if(current_class.equalsIgnoreCase("mage") || current_class.equalsIgnoreCase("arcanist") || current_class.equalsIgnoreCase("illusionist") || current_class.equalsIgnoreCase("necromancer")){
			int stat_1, stat_2, stat_3;
			stat_1 = getLevel(p, "arcane");
			stat_2 = getLevel(p, "illusion");
			stat_3 = getLevel(p, "necromancy");
			
			if(stat_1 >= stat_2 && stat_1 >= stat_3 && stat_1 >= 20){
				setClass(p, "Arcanist", false);

				return;
			}
			
			if(stat_2 >= stat_1 && stat_2 >= stat_3 && stat_2 >= 20){
				setClass(p, "Illusionist", false);
				return;
			}
			 
			if(stat_3 >= stat_2 && stat_3 >= stat_1 && stat_3 >= 20){
				setClass(p, "Necromancer", false);
				return;
			}
			
			else{
				setClass(p, "Mage", false);
			}
			
		}
	}
	
	public String generateLevelBar(Player p){
		String class_name = getClass(p);
		int stat_1, stat_2, stat_3;
		
		if(class_name.equalsIgnoreCase("fighter") || class_name.equalsIgnoreCase("knight") || class_name.equalsIgnoreCase("beserker") || class_name.equalsIgnoreCase("spearman")){
			stat_1 = getLevel(p, "swords");
			stat_2 = getLevel(p, "axes");
			stat_3 = getLevel(p, "spears");

			
			int sword_length, axe_length, spear_length = 0;
			String s1 = sword_color.toString(), s2 = axe_color.toString(), s3 = spear_color.toString();
			sword_length = stat_1 / 3;
			axe_length = stat_2 / 3;
			spear_length = stat_3 / 3;
			
		    int levels_accounted_for = sword_length + axe_length + spear_length;
		    
		    while(sword_length > 0){
		    	s1 += "|";
		    	sword_length--;
		    }
		    while(axe_length > 0){
		    	s2 += "|";
		    	axe_length--;
		    }
		    while(spear_length > 0){
		    	s3 += "|";
		    	spear_length--;
		    }
		    
		    String filler = ChatColor.BLACK.toString() + "";
		    int filler_needed = 33 - levels_accounted_for;

		    while(filler_needed > 0){
		    	filler += "|";
		        filler_needed --;
		    }
		    filler += "]";
		    String return_val = ChatColor.BLACK.toString() + "[" + s1 + s2 + s3 + filler;
		    return return_val;
		}
		if(class_name.equalsIgnoreCase("hunter") || class_name.equalsIgnoreCase("archer") || class_name.equalsIgnoreCase("rogue") || class_name.equalsIgnoreCase("trapper")){
			stat_1 = getLevel(p, "bows");
			stat_2 = getLevel(p, "daggers");
			stat_3 = getLevel(p, "traps");

			int bow_length, daggers_length, traps_length = 0;
			String s1 = bows_color.toString(), s2 = daggers_color.toString(), s3 = traps_color.toString();
			bow_length = stat_1 / 3;
			daggers_length = stat_2 / 3;
			traps_length = stat_3 / 3;
			
		    int levels_accounted_for = bow_length + daggers_length + traps_length;
		    
		    while(bow_length > 0){
		    	s1 += "|";
		    	bow_length--;
		    }
		    while(daggers_length > 0){
		    	s2 += "|";
		    	daggers_length--;
		    }
		    while(traps_length > 0){
		    	s3 += "|";
		    	traps_length--;
		    }
		    
		    String filler = ChatColor.BLACK.toString() + "";
		    int filler_needed = 33 - levels_accounted_for;

		    while(filler_needed > 0){
		    	filler += "|";
		        filler_needed --;
		    }
		    filler += "]";
		    String return_val = ChatColor.BLACK.toString() + "[" + s1 + s2 + s3 + filler;
		    return return_val;
		}
		if(class_name.equalsIgnoreCase("mage")  || class_name.equalsIgnoreCase("arcanist") || class_name.equalsIgnoreCase("illusionist") || class_name.equalsIgnoreCase("necromancer")){
			stat_1 = getLevel(p, "arcane");
			stat_2 = getLevel(p, "illusion");
			stat_3 = getLevel(p, "necromancy");

			int arcane_length, illusion_length, necromancy_length = 0;
			String s1 = arcane_color.toString(), s2 = illusion_color.toString(), s3 = necromacy_color.toString();
			arcane_length = (stat_1 / 3);
			illusion_length = (stat_2 / 3);
			necromancy_length = (stat_3 / 3);
			
		    int levels_accounted_for = arcane_length + illusion_length + necromancy_length;
		    
		    while(arcane_length > 0){
		    	s1 += "|";
		    	arcane_length--;
		    }
		    while(illusion_length > 0){
		    	s2 += "|";
		    	illusion_length--;
		    }
		    while(necromancy_length > 0){
		    	s3 += "|";
		    	necromancy_length--;
		    }
		    
		    String filler = ChatColor.BLACK.toString() + "";
		    int filler_needed = 33 - levels_accounted_for;

		    while(filler_needed > 0){
		    	filler += "|";
		        filler_needed --;
		    }
		    filler += "]";
		    String return_val = ChatColor.BLACK.toString() + "[" + s1 + s2 + s3 + filler;
		    return return_val;
		}
		
		return "";
	}
	
	//TODO: Stat book handling when I have no class.
	
	public void generateStatBook(Player p){
		//updateClass(p);
		String class_name = getClass(p);
		String format_class_name = class_name.substring(0, 1).toUpperCase() + class_name.substring(1, class_name.length());
		//p.sendMessage(ChatColor.YELLOW + "Class: " + class_name);
		
		String stat1, stat2, stat3;
		String[] stat_list = getStatNames(class_name);
		stat1 = stat_list[0] + ": ";
		stat2 = stat_list[1] + ": ";
		stat3 = stat_list[2] + ": ";
		
		BookItem bi = new BookItem(new ItemStack(387, 1));
		ChatColor c = getClassColor(p);
		ChatColor black = ChatColor.BLACK;
		ChatColor hide = ChatColor.WHITE;
		
		int c1=0, c2=0, c3=0;
		int n1=0, n2=0, n3=0;
		
		c1 = getCurrentExp(p, stat_list[0]);
		c2 = getCurrentExp(p, stat_list[1]);
		c3 = getCurrentExp(p, stat_list[2]);
		
		n1 = getNeededExp(p, stat_list[0]);
		n2 = getNeededExp(p, stat_list[1]);
		n3 = getNeededExp(p, stat_list[2]);
		
		String[] stat_page = new String[] {ChatColor.BOLD.toString() + "Class: " + c.toString() + format_class_name + black.toString() + "\n" + hide.toString() + "`" + "\n" + black.toString() + ChatColor.BOLD.toString() + "Total Level: " + c.toString() + getTotalLevel(p) + "\n" + black.toString() + generateLevelBar(p) + "\n" + hide.toString() + "`" + "\n" + black.toString() + ChatColor.BOLD.toString() + "Stats:" + "\n" + black.toString() + stat1 + getStatColor(stat_list[0]) + "Lvl " + getLevel(p, stat_list[0]) + black.toString() + "\n" + stat2 + getStatColor(stat_list[1]) + "Lvl " + getLevel(p, stat_list[1]) + black.toString() + "\n" + stat3 + getStatColor(stat_list[2]) + "Lvl " + getLevel(p, stat_list[2]) + hide.toString() + "\n" + "`" + "\n" + black.toString() + ChatColor.BOLD.toString() + "EXP:" + "\n" + black.toString() + getStatColor(stat_list[0]) + "[" + String.valueOf(calculatePercent(c1, n1)) + "%] " + c1 + " / " + n1 + "\n" + black.toString() + getStatColor(stat_list[1]) + "[" + String.valueOf(calculatePercent(c2, n2)) + "%] " + c2 + " / " + n2 + "\n" + black.toString() + getStatColor(stat_list[2]) + "[" + String.valueOf(calculatePercent(c3, n3)) + "%] " + c3 + " / " + n3};
		bi.addPages(stat_page);
		bi.setTitle("Stat Book");
		bi.setAuthor("A book that records your progress.");
		
		p.getInventory().setItem(7, bi.getItemStack());
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDrop(PlayerDropItemEvent e){
		if(e.getItemDrop().getItemStack().getType() == Material.WRITTEN_BOOK){
			e.getItemDrop().remove();
			Player p = e.getPlayer();
			generateStatBook(p);
			p.updateInventory();
		}
	}
	
	@EventHandler
	public void onInventoryInteractEvent(InventoryClickEvent e){
		if(e.getSlot() == 7){e.setCancelled(true);}
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onItemSelect(PlayerItemHeldEvent e){
		if(e.getNewSlot() == 7){
			Player p = e.getPlayer();
			
			Inventory i = p.getInventory();
			i.remove(7);
			//if(i.getItem(7).getType() == Material.WRITTEN_BOOK){
				generateStatBook(p);
			//}
		}
	}
	
	/*@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerInteract(PlayerInteractEvent e){
		if((e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) && e.hasItem() && e.getItem().getType() == Material.WRITTEN_BOOK){
			Player p = e.getPlayer();
			generateStatBook(p);
		}
	}*/
	
	@EventHandler
	public void onPlayerLogin(PlayerLoginEvent e){
		Player p = e.getPlayer();
		if(!loadClassData(p)){
			// The player does not have any class data.
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerJoin(PlayerJoinEvent e){
		final Player p = e.getPlayer();
	
		//if(class_map.containsKey(p.getName())){
			//generateStatBook(p);
		//}
		
			 this.getServer().getScheduler().scheduleAsyncDelayedTask(this, new Runnable() {
		            public void run() {
		            	
		        		for (Map.Entry<Player, ChatColor> entry : color_map.entrySet()){
		        			Player p_to_sendout = entry.getKey();
		        			String real_name = p_to_sendout.getName();
		        			ChatColor c = entry.getValue();
		        			
		        			EntityPlayer ent_p = ((CraftPlayer) p_to_sendout).getHandle();
		        			ent_p.name = c.toString() + p_to_sendout.getName();
		        			((CraftPlayer) p).getHandle().netServerHandler.sendPacket(new Packet20NamedEntitySpawn(ent_p));
		        			ent_p.name = real_name;
		        		}
		            	
		        		if(class_map.containsKey(p.getName())){	
		    			setClassColor(p);
		        		}
		            }
			 }, 5L); 
		}
	
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e){
		Player p = e.getPlayer();
		if(color_map.containsKey(p)){
			color_map.remove(p);
		}
		
		uploadClassData(p);
		class_map.remove(p);
		//TODO: Upload class data, remove from map.
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent e){
		Player p = e.getPlayer();
		if(e.getBlock().getType() == Material.DIRT){
			addXP(p, "swords", 500);
		}
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
		Player p = (Player)sender;
		if(cmd.getName().equalsIgnoreCase("class")){
			if(args.length == 0){
				p.sendMessage("/class set [class] - Sets yourself to specified class.");
				p.sendMessage("/class info - Show information about your class.");
			}
			if(args[0].equalsIgnoreCase("set")){ // /class set
				if(args.length <= 1){
					p.sendMessage(ChatColor.RED + "Invalid class selection.");
					p.sendMessage(ChatColor.GRAY + "Fighter | Hunter | Mage");
					return true;
				}
				String class_name = args[1];
				if(setClass(p, class_name, true) == false){
					p.sendMessage(ChatColor.RED + "Invalid class selection.");
					p.sendMessage(ChatColor.GRAY + "Fighter | Hunter | Mage");
					return true;
				}
				p.sendMessage(ChatColor.BLUE + "Congratulations, you are now a " + class_name + "!");
			}
			if(args[0].equalsIgnoreCase("setstat")){ // /class setstat <stat> <val>
				if(args.length != 3){
					p.sendMessage(ChatColor.RED + "Invalid use of command.");
					p.sendMessage(ChatColor.GRAY + "/class setstat <stat> <val>");
					return true;
				}
				
				String stat = args[1];
				int val = Integer.parseInt(args[2]);
				setLevel(p, stat, val);
				p.sendMessage(ChatColor.YELLOW + stat + " level set to " + val + ".");
			}
			if(args[0].equalsIgnoreCase("info")){ // /class info
				generateStatBook(p);
			}
		}
		return true;
	}
}
