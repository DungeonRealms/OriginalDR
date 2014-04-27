package minecade.dungeonrealms.PartyMechanics.commands;

import minecade.dungeonrealms.PartyMechanics.Party;
import minecade.dungeonrealms.PartyMechanics.PartyMechanics;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandPQuit implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player p = (Player) sender;
		
		if(args.length != 0) {
			p.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Invalid Syntax. " + ChatColor.RED + "/pquit");
			return true;
		}
		
		if(!(PartyMechanics.party_map.containsKey(p.getName()))) {
			p.sendMessage(ChatColor.RED + "You are not in a party.");
			return true;
		}
		
		Party party_name = PartyMechanics.party_map.get(p.getName());
		party_name.removePlayer(p);
		p.sendMessage(ChatColor.RED.toString() + "You have left the party.");
		return true;
	}
	
}