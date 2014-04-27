package minecade.dungeonrealms.CommunityMechanics.commands;

import java.util.List;

import minecade.dungeonrealms.managers.PlayerManager;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandToggleGuild implements CommandExecutor {
	
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
			p.sendMessage(ChatColor.GRAY + "Usage: /toggleguild");
			p.sendMessage(ChatColor.GRAY + "Description: Enables / Disables Non-BUD guild invitations.");
			return true;
		}
		
		if(PlayerManager.getPlayerModel(p).getToggleList().contains("guild")){
			List<String> ltoggle_list = PlayerManager.getPlayerModel(p).getToggleList();
			ltoggle_list.remove("guild");
			PlayerManager.getPlayerModel(p).setToggleList(ltoggle_list);
			p.sendMessage(ChatColor.GREEN + "Non-BUD Guild Invites - " + ChatColor.BOLD + "ENABLED");
			return true;
		}
		
		if(!PlayerManager.getPlayerModel(p).getToggleList().contains("guild")){
			List<String> ltoggle_list = PlayerManager.getPlayerModel(p).getToggleList();
			ltoggle_list.add("guild");
			PlayerManager.getPlayerModel(p).setToggleList(ltoggle_list);
			p.sendMessage(ChatColor.RED + "Non-BUD Guild Invites - " + ChatColor.BOLD + "DISABLED");
			return true;
		}
		return true;
	}
	
}