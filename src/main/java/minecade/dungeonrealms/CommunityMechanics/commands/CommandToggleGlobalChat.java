package minecade.dungeonrealms.CommunityMechanics.commands;

import java.util.List;

import minecade.dungeonrealms.managers.PlayerManager;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandToggleGlobalChat implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		final Player p = (Player) sender;
		
		if(!(args.length == 0)) {
			p.sendMessage(ChatColor.RED + "Invalid Command.");
			p.sendMessage(ChatColor.GRAY + "Usage: /toggleglobalchat");
			p.sendMessage(ChatColor.GRAY + "Description: Enables / Disables talking in global chat.");
			return true;
		}
		
		if(PlayerManager.getPlayerModel(p).getToggleList().contains("globalchat")){
			List<String> ltoggle_list = PlayerManager.getPlayerModel(p).getToggleList();
			ltoggle_list.remove("globalchat");
			PlayerManager.getPlayerModel(p).setToggleList(ltoggle_list);
			p.sendMessage(ChatColor.RED + "Global Only Chat - " + ChatColor.BOLD + "DISABLED");
			return true;
		}
		
		if(!PlayerManager.getPlayerModel(p).getToggleList().contains("globalchat")){
			List<String> ltoggle_list = PlayerManager.getPlayerModel(p).getToggleList();
			ltoggle_list.add("globalchat");
			PlayerManager.getPlayerModel(p).setToggleList(ltoggle_list);
			p.sendMessage(ChatColor.GREEN + "Global Only Chat - " + ChatColor.BOLD + "ENABLED");
			return true;
		}
		
		return true;
	}
	
}
