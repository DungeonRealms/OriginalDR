package minecade.dungeonrealms.PartyMechanics.commands;

import minecade.dungeonrealms.PartyMechanics.PartyMechanics;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandPDecline implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player p = (Player) sender;
		
		if(args.length != 0) {
			p.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Invalid Syntax. " + ChatColor.RED + "/pdecline");
			return true;
		}
		
		if(!(PartyMechanics.party_invite.containsKey(p.getName()))) {
			p.sendMessage(ChatColor.RED + "No pending party invites.");
			return true;
		}
		
		String party_name = PartyMechanics.party_invite.get(p.getName());
		PartyMechanics.party_invite.remove(p.getName());
		PartyMechanics.party_invite_time.remove(p.getName());
		p.sendMessage(ChatColor.RED + "Declined " + ChatColor.BOLD + party_name + "'s" + ChatColor.RED + " party invitation.");
		if(Bukkit.getPlayer(party_name) != null) {
			Player owner = Bukkit.getPlayer(party_name);
			owner.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + p.getName() + ChatColor.RED.toString() + " has " + ChatColor.UNDERLINE + "DECLINED" + ChatColor.RED + " your party invitation.");
		}
		return true;
	}
	
}