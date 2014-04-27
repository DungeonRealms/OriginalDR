package minecade.dungeonrealms.Hive.commands;

import minecade.dungeonrealms.Hive.Hive;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandReboot implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		long diff = System.currentTimeMillis() - Hive.serverStart;
		diff = (4 * 60 * 60 * 1000) - diff; // Subtract hourly schedule for reboots
		//(int)(diff / 86400000) + ChatColor.BOLD + "d " + 
		final String msg = ChatColor.YELLOW.toString() + ChatColor.UNDERLINE + "Next Scheduled Reboot: " + ChatColor.YELLOW + (int) (diff / 3600000 % 24) + ChatColor.BOLD + "h " + ChatColor.YELLOW + (int) (diff / 60000 % 60) + ChatColor.BOLD + "m " + ChatColor.YELLOW + (int) (diff / 1000 % 60) + ChatColor.BOLD + "s";
		sender.sendMessage(msg);
		return true;
	}
	
}