package me.vaqxine.TradeMechanics.commands;

import java.util.List;

import me.vaqxine.CommunityMechanics.CommunityMechanics;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandToggleTrade implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player p = (Player)sender;
		if(!(args.length == 0)){
			p.sendMessage(ChatColor.RED + "Invalid Command.");
			p.sendMessage(ChatColor.GRAY + "Usage: /toggletrade");
			p.sendMessage(ChatColor.GRAY + "Description: Enables / Disables recieving trade requests.");
			return true;
		}

		if(CommunityMechanics.toggle_list.get(p.getName()).contains("trade")){
			List<String> ltoggle_list = CommunityMechanics.toggle_list.get(p.getName());
			ltoggle_list.remove("trade");
			CommunityMechanics.toggle_list.put(p.getName(), ltoggle_list);
			p.sendMessage(ChatColor.GREEN + "Trade - " + ChatColor.BOLD + "ENABLED");
			return true;
		}

		if(!CommunityMechanics.toggle_list.get(p.getName()).contains("trade")){
			List<String> ltoggle_list = CommunityMechanics.toggle_list.get(p.getName());
			ltoggle_list.add("trade");
			CommunityMechanics.toggle_list.put(p.getName(), ltoggle_list);
			p.sendMessage(ChatColor.RED + "Trade - " + ChatColor.BOLD + "DISABLED");
			return true;
		}
		return true;
	}
	
}