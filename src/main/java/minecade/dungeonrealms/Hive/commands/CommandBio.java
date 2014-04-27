package minecade.dungeonrealms.Hive.commands;

import minecade.dungeonrealms.Hive.Hive;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandBio implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player pl = (Player) sender;
		if(args.length == 0) {
			if(Hive.player_bio.containsKey(pl.getName())) {
				pl.sendMessage(ChatColor.RED + "You already have a pending profile biography. Type 'cancel' to void that one before starting another.");
				return true;
			}
			
			Hive.player_bio.put(pl.getName(), "");
			pl.sendMessage("");
			pl.sendMessage(ChatColor.YELLOW + "Start typing your profile biography just like you would chat.");
			pl.sendMessage(ChatColor.GRAY + "Send your typed message at any time as a line break. Type '" + ChatColor.GREEN.toString() + "confirm" + ChatColor.GRAY + "' when you're done OR '" + ChatColor.RED + "cancel" + ChatColor.GRAY + "' to void this process.");
			pl.sendMessage("");
			pl.sendMessage(ChatColor.GREEN.toString() + ChatColor.BOLD + "0/255 Characters");
		}
		
		else if(args.length != 0) {
			pl.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "Invalid Syntax.");
			pl.sendMessage(ChatColor.GRAY + "Usage: /bio");
			return true;
		}
		return true;
	}
	
}