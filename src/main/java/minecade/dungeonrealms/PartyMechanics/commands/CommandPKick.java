package minecade.dungeonrealms.PartyMechanics.commands;

import minecade.dungeonrealms.PartyMechanics.Party;
import minecade.dungeonrealms.PartyMechanics.PartyMechanics;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandPKick implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player p = (Player) sender;
		
		if(args.length != 1) {
			p.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Invalid Syntax. " + ChatColor.RED + "/pkick <player>");
			return true;
		}
		
		if(!(PartyMechanics.isPartyLeader(p.getName()))) {
			if(PartyMechanics.party_map.containsKey(p.getName())) { // In another party.
				p.sendMessage(ChatColor.RED.toString() + "You are NOT the leader of your party.");
				p.sendMessage(ChatColor.GRAY.toString() + "Type " + ChatColor.BOLD.toString() + "/pquit" + ChatColor.GRAY + " to quit your current party.");
				return true;
			} else { // No party.
				PartyMechanics.createParty(p.getName(), p, null);
				p.sendMessage(ChatColor.GREEN.toString() + ChatColor.BOLD + "Party created.");
				p.sendMessage(ChatColor.GRAY.toString() + "To invite more people to join your party, " + ChatColor.UNDERLINE + "Left Click" + ChatColor.GRAY.toString() + " them with your character journal or use " + ChatColor.BOLD + "/pinvite" + ChatColor.GRAY + ". To kick, use " + ChatColor.BOLD + "/pkick" + ChatColor.GRAY + ". To chat with party, use " + ChatColor.BOLD + "/p" + ChatColor.GRAY + " To change the loot profile, use " + ChatColor.BOLD + "/ploot");
			}
		}
		
		String p_2kick = args[0];
		
		if(!(PartyMechanics.arePartyMembers(p.getName(), p_2kick))) {
			p.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + p_2kick + " is not in your party.");
			return true;
		}
		
		Player to_kick = Bukkit.getPlayer(p_2kick);
		Party party_name = PartyMechanics.party_map.get(to_kick.getName());
		party_name.removePlayer(to_kick);
		to_kick.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD.toString() + "You have been kicked out of the party.");
		return true;
	}
	
}