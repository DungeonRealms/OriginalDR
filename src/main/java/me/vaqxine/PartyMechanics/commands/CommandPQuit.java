package me.vaqxine.PartyMechanics.commands;

import me.vaqxine.PartyMechanics.PartyMechanics;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandPQuit implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player p = (Player)sender;

		if(args.length != 0){
			p.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Invalid Syntax. " + ChatColor.RED + "/pquit");
			return true;
		}

		if(!(PartyMechanics.inv_party_map.containsKey(p.getName()))){
			p.sendMessage(ChatColor.RED + "You are not in a party.");
			return true;
		}

		String party_name = PartyMechanics.inv_party_map.get(p.getName());
		PartyMechanics.removePlayerFromParty(p, party_name);
		p.sendMessage(ChatColor.RED.toString() + "You have left the party.");
		return true;
	}
	
}