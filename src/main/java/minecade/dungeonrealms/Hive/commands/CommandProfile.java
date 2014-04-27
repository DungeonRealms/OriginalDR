package minecade.dungeonrealms.Hive.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandProfile implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player p = (Player) sender;
		p.sendMessage(ChatColor.RED + "This feature is temporarily disabled due to host transfer.");
		//p.sendMessage(ChatColor.UNDERLINE + "http://www.dungeonrealms.net/profile/?player=" + p.getName());
		return true;
	}
	
}