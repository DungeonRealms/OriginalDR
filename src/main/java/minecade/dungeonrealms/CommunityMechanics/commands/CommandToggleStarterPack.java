package minecade.dungeonrealms.CommunityMechanics.commands;

import java.util.List;

import minecade.dungeonrealms.managers.PlayerManager;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandToggleStarterPack implements CommandExecutor {
	
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
			p.sendMessage(ChatColor.GRAY + "Usage: /togglestarterpack");
			p.sendMessage(ChatColor.GRAY + "Description: Enables / Disables getting starter items on reboot.");
			return true;
		}
		
		if(PlayerManager.getPlayerModel(p).getToggleList().contains("starterpack")){
			List<String> ltoggle_list = PlayerManager.getPlayerModel(p).getToggleList();
			ltoggle_list.remove("starterpack");
			PlayerManager.getPlayerModel(p).setToggleList(ltoggle_list);
			p.sendMessage(ChatColor.GREEN + "Respawn Item Pack - " + ChatColor.BOLD + "ENABLED");
			return true;
		}
		
		if(!PlayerManager.getPlayerModel(p).getToggleList().contains("starterpack")) {
			List<String> ltoggle_list = PlayerManager.getPlayerModel(p).getToggleList();
			ltoggle_list.add("starterpack");
			PlayerManager.getPlayerModel(p).setToggleList(ltoggle_list);
			p.sendMessage(ChatColor.RED + "Respawn Item Pack - " + ChatColor.BOLD + "DISABLED");
			return true;
		}
		return true;
	}
	
}