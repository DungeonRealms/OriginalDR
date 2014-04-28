package minecade.dungeonrealms.RestrictionMechanics.commands;

import minecade.dungeonrealms.Main;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandList implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player p = (Player) sender;
		if(!(Main.isMaster(p.getName()))) { return true; }
		if(p.isOp()) {
			p.sendMessage(ChatColor.RED + "An internal exception has occured.");
			return true;
		}
		return true;
	}
	
}