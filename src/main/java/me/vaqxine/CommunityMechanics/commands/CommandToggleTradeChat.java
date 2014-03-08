package me.vaqxine.CommunityMechanics.commands;

import java.util.List;

import me.vaqxine.CommunityMechanics.CommunityMechanics;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandToggleTradeChat implements CommandExecutor {

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
			p.sendMessage(ChatColor.GRAY + "Usage: /toggletradechat");
			p.sendMessage(ChatColor.GRAY + "Description: Enables / Disables sending and recieving trade messages.");
			return true;
		}

		if(CommunityMechanics.toggle_list.get(p.getName()).contains("tchat")){
			List<String> ltoggle_list = CommunityMechanics.toggle_list.get(p.getName());
			ltoggle_list.remove("tchat");
			CommunityMechanics.toggle_list.put(p.getName(), ltoggle_list);
			p.sendMessage(ChatColor.GREEN + "Trade Chat - " + ChatColor.BOLD + "ENABLED");
			return true;
		}

		if(!CommunityMechanics.toggle_list.get(p.getName()).contains("tchat")){
			List<String> ltoggle_list = CommunityMechanics.toggle_list.get(p.getName());
			ltoggle_list.add("tchat");
			CommunityMechanics.toggle_list.put(p.getName(), ltoggle_list);
			p.sendMessage(ChatColor.RED + "Trade Chat - " + ChatColor.BOLD + "DISABLED");
			return true;
		}
		return true;
	}
	
}