package me.vaqxine.CommunityMechanics.commands;

import java.util.List;

import me.vaqxine.CommunityMechanics.CommunityMechanics;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandToggleProfile implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		final Player p = (Player)sender;

		if(cmd.getName().equalsIgnoreCase("crypt")){
			if(p != null){
				if(!(p.isOp())){
					return true;
				}
			}

			return true;
		}
		
		if(!(args.length == 0)){
			p.sendMessage(ChatColor.RED + "Invalid Command.");
			p.sendMessage(ChatColor.GRAY + "Usage: /toggleprofile");
			p.sendMessage(ChatColor.GRAY + "Description: Toggles displaying identifying information such as inventory and location on your online profile.");
			return true;
		}

		if(CommunityMechanics.toggle_list.get(p.getName()).contains("profile")){
			List<String> ltoggle_list = CommunityMechanics.toggle_list.get(p.getName());
			ltoggle_list.remove("profile");
			CommunityMechanics.toggle_list.put(p.getName(), ltoggle_list);
			p.sendMessage(ChatColor.GREEN + "Online Profile - " + ChatColor.BOLD + "ENABLED");
			return true;
		}

		if(!CommunityMechanics.toggle_list.get(p.getName()).contains("profile")){
			List<String> ltoggle_list = CommunityMechanics.toggle_list.get(p.getName());
			ltoggle_list.add("profile");
			CommunityMechanics.toggle_list.put(p.getName(), ltoggle_list);
			p.sendMessage(ChatColor.RED + "Online Profile - " + ChatColor.BOLD + "DISABLED");
			return true;
		}
		return true;
	}
	
}