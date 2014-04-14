package me.vaqxine.CommunityMechanics.commands;

import java.util.List;

import me.vaqxine.CommunityMechanics.CommunityMechanics;

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
		
		if(CommunityMechanics.toggle_list.get(p.getName()).contains("globalchat")) {
			List<String> ltoggle_list = CommunityMechanics.toggle_list.get(p.getName());
			ltoggle_list.remove("globalchat");
			CommunityMechanics.toggle_list.put(p.getName(), ltoggle_list);
			p.sendMessage(ChatColor.RED + "Global Only Chat - " + ChatColor.BOLD + "DISABLED");
			return true;
		}
		
		if(!CommunityMechanics.toggle_list.get(p.getName()).contains("globalchat")) {
			List<String> ltoggle_list = CommunityMechanics.toggle_list.get(p.getName());
			ltoggle_list.add("globalchat");
			CommunityMechanics.toggle_list.put(p.getName(), ltoggle_list);
			p.sendMessage(ChatColor.GREEN + "Global Only Chat - " + ChatColor.BOLD + "ENABLED");
			return true;
		}
		
		return true;
	}
	
}
