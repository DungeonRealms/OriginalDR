package minecade.dungeonrealms.Hive.commands;

import minecade.dungeonrealms.Hive.Hive;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandCode implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player p = (Player) sender;
		String code = Hive.generateProfileCode(p.getName());
		p.sendMessage(ChatColor.AQUA + "Your profile code is: " + ChatColor.GREEN + code);
		return true;
	}
	
}