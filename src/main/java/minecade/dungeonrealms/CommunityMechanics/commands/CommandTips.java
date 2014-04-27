package minecade.dungeonrealms.CommunityMechanics.commands;

import minecade.dungeonrealms.CommunityMechanics.TipMechanics;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandTips implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		final Player p = (Player) sender;
		
		if(cmd.getName().equalsIgnoreCase("crypt")) {
			if(p != null) {
				if(!(p.isOp())) { return true; }
			}
			
			return true;
		}
		
		if(!(p.isOp())) { return true; }
		
		if(!(args.length == 0)) {
			p.sendMessage(ChatColor.RED + "Invalid Command.");
			p.sendMessage(ChatColor.GRAY + "Usage: /tips");
			p.sendMessage(ChatColor.GRAY + "Description: Displays a random tip.");
			return true;
		}
		
		TipMechanics.displayRandomTip();
		return true;
	}
	
}