package minecade.dungeonrealms.Hive.commands;

import minecade.dungeonrealms.Main;
import minecade.dungeonrealms.Hive.Hive;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandWhois implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player pl = null;
		if(sender instanceof Player) {
			pl = (Player) sender;
			if(!pl.isOp()) { return true; }
		}
		
		if(pl != null) {
			if(args.length != 1) {
				pl.sendMessage("Syntax. /whois <player>");
				return true;
			}
			String p_name = args[0];
			int server_num = Hive.getPlayerServer(p_name, false);
			if(server_num == -1) {
				pl.sendMessage(ChatColor.RED + p_name + ", currently offline.");
				return true;
			}
			
			String server_prefix = Hive.getServerPrefixFromNum(server_num);
			
			if(Bukkit.getPlayer(p_name) == null) {
				pl.sendMessage(ChatColor.YELLOW + p_name + ", currently on server " + ChatColor.UNDERLINE + server_prefix);
			} else {
				pl.sendMessage(ChatColor.YELLOW + p_name + ", currently on " + ChatColor.UNDERLINE + "YOUR" + ChatColor.YELLOW + " server.");
			}
		} else if(pl == null) {
			if(args.length != 1) {
				Main.log.info("Syntax. /whois <player>");
				return true;
			}
			String p_name = args[0];
			int server_num = Hive.getPlayerServer(p_name, false);
			if(server_num == -1) {
				Main.log.info(ChatColor.RED + p_name + ", currently offline.");
				return true;
			}
			
			String server_prefix = Hive.getServerPrefixFromNum(server_num);
			
			if(Bukkit.getPlayer(p_name) == null) {
				Main.log.info(ChatColor.YELLOW + p_name + ", currently on server " + ChatColor.UNDERLINE + server_prefix);
			} else {
				Main.log.info(ChatColor.YELLOW + p_name + ", currently on " + ChatColor.UNDERLINE + "YOUR" + ChatColor.YELLOW + " server.");
			}
			// Log to console.
		}
		return true;
	}
	
}