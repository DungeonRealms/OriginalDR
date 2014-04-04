package me.vaqxine.CommunityMechanics.commands;

import java.util.List;

import me.vaqxine.CommunityMechanics.CommunityMechanics;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandToggleTells implements CommandExecutor {
	
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
			p.sendMessage(ChatColor.GRAY + "Usage: /toggletells");
			p.sendMessage(ChatColor.GRAY + "Description: Enables / Disables recieving non-bud private messages.");
			return true;
		}
		
		if(CommunityMechanics.toggle_list.get(p.getName()).contains("tells")) {
			List<String> ltoggle_list = CommunityMechanics.toggle_list.get(p.getName());
			ltoggle_list.remove("tells");
			CommunityMechanics.toggle_list.put(p.getName(), ltoggle_list);
			p.sendMessage(ChatColor.GREEN + "Non-BUD Private Messages - " + ChatColor.BOLD + "ENABLED");
			return true;
		}
		
		if(!CommunityMechanics.toggle_list.get(p.getName()).contains("tells")) {
			List<String> ltoggle_list = CommunityMechanics.toggle_list.get(p.getName());
			ltoggle_list.add("tells");
			CommunityMechanics.toggle_list.put(p.getName(), ltoggle_list);
			p.sendMessage(ChatColor.RED + "Non-BUD Private Messages - " + ChatColor.BOLD + "DISABLED");
			return true;
		}
		return true;
	}
	
}