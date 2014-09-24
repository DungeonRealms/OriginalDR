package minecade.dungeonrealms.ModerationMechanics.commands;

import minecade.dungeonrealms.Main;
import minecade.dungeonrealms.Hive.Hive;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class CommandPlayerClone implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player p = null;
		if(sender instanceof Player) {
			p = (Player) sender;
		}
		
		if(p != null) {
			if(!(p.isOp())) { return true; }
			
			if(args.length != 1 && args.length != 2) {
				if(p != null) {
					p.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Invalid Syntax. " + ChatColor.RED + "/playerclone <PLAYER>");
					p.sendMessage(ChatColor.GRAY + "Copies all player data of <PLAYER> to your account.");
				}
				return true;
			}
			
			String to_name = p.getName();
			
			if(args.length == 2) {
				to_name = args[1];
			}
			final String p_name = args[0];
			
			Hive.sql_query.add("DELETE FROM player_database where p_name='" + to_name + "'");
			Hive.sql_query.add("CREATE TEMPORARY TABLE tmp_playerdb SELECT * FROM player_database WHERE p_name='" + p_name + "'");
			Hive.sql_query.add("UPDATE tmp_playerdb SET p_name='" + to_name + "' WHERE p_name='" + p_name + "'");
			Hive.sql_query.add("INSERT INTO player_database SELECT * FROM tmp_playerdb WHERE p_name='" + to_name + "'");
			Hive.sql_query.add("DROP TABLE tmp_playerdb");
			
			Hive.sql_query.add("DELETE FROM bank_database where p_name='" + to_name + "'");
			Hive.sql_query.add("CREATE TEMPORARY TABLE tmp_bankdb SELECT * FROM bank_database WHERE p_name='" + p_name + "'");
			Hive.sql_query.add("UPDATE tmp_bankdb SET p_name='" + to_name + "' WHERE p_name='" + p_name + "'");
			Hive.sql_query.add("INSERT INTO bank_database SELECT * FROM tmp_bankdb WHERE p_name='" + to_name + "'");
			Hive.sql_query.add("DROP TABLE tmp_bankdb");
			
			Hive.sql_query.add("DELETE FROM shop_database where p_name='" + to_name + "'");
			Hive.sql_query.add("CREATE TEMPORARY TABLE tmp_shopdb SELECT * FROM shop_database WHERE p_name='" + p_name + "'");
			Hive.sql_query.add("UPDATE tmp_shopdb SET p_name='" + to_name + "' WHERE p_name='" + p_name + "'");
			Hive.sql_query.add("INSERT INTO shop_database SELECT * FROM tmp_shopdb WHERE p_name='" + to_name + "'");
			Hive.sql_query.add("DROP TABLE tmp_shopdb");
			
			Hive.no_upload.add(to_name);
			
			final String f_to_name = to_name;
			
			new BukkitRunnable() {
				@Override
				public void run() {
					if(Bukkit.getPlayer(f_to_name) != null) {
						Bukkit.getPlayer(f_to_name).kickPlayer(p_name + "'s data is copying, please wait 5-10 seconds and login.");
					}
				}
			}.runTaskLater(Main.plugin, 2L);
			
		}
		
		return true;
	}
	
}
