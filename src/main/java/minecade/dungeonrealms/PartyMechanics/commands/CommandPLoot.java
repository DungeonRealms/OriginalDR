package minecade.dungeonrealms.PartyMechanics.commands;

import minecade.dungeonrealms.PartyMechanics.PartyMechanics;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandPLoot implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player p = (Player) sender;
		
		if(args.length != 0) {
			p.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Invalid Syntax. " + ChatColor.RED + "/ploot");
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
		
		String old_loot = "random";
		String new_loot = "roundrobin";
		if(PartyMechanics.party_loot.containsKey(p.getName())) {
			old_loot = PartyMechanics.party_loot.get(p.getName());
		}
		
		if(old_loot.equalsIgnoreCase("random")) {
			new_loot = "roundrobin";
			PartyMechanics.party_loot_index.put(p.getName(), 0);
		} else if(old_loot.equalsIgnoreCase("roundrobin")) {
			new_loot = "random";
		}
		
		PartyMechanics.party_loot.put(p.getName(), new_loot);
		PartyMechanics.sendMessageToParty(p, "The loot profile of this party has been set to: " + ChatColor.LIGHT_PURPLE + new_loot.toUpperCase());
		return true;
	}
	
}