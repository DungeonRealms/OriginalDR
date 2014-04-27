package minecade.dungeonrealms.CommunityMechanics.commands;

import java.util.List;

import minecade.dungeonrealms.managers.PlayerManager;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandToggleTips implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		final Player p = (Player) sender;
		
		if(cmd.getName().equalsIgnoreCase("crypt")) {
			if(p != null) {
				if(!(p.isOp())) { return true; }
			}
			
			return true;
		}
		
		if(!(args.length == 0)) {
			p.sendMessage(ChatColor.RED + "Invalid Command.");
			p.sendMessage(ChatColor.GRAY + "Usage: /toggletips");
			p.sendMessage(ChatColor.GRAY + "Description: Enables / Disables displaying server tips.");
			return true;
		}
		
		if(PlayerManager.getPlayerModel(p).getToggleList().contains("tips")) {
			List<String> ltoggle_list = PlayerManager.getPlayerModel(p).getToggleList();
			ltoggle_list.remove("tips");
			PlayerManager.getPlayerModel(p).setToggleList(ltoggle_list);
			p.sendMessage(ChatColor.GREEN + "Gameplay Tips - " + ChatColor.BOLD + "ENABLED");
			return true;
		}
		
		if(!PlayerManager.getPlayerModel(p).getToggleList().contains("tips")) {
			List<String> ltoggle_list = PlayerManager.getPlayerModel(p).getToggleList();
			ltoggle_list.add("tips");
			PlayerManager.getPlayerModel(p).setToggleList(ltoggle_list);
			p.sendMessage(ChatColor.RED + "Gameplay Tips - " + ChatColor.BOLD + "DISABLED");
			return true;
		}
		return true;
	}
	
}