package minecade.dungeonrealms.PartyMechanics.commands;

import minecade.dungeonrealms.AchievementMechanics.AchievementMechanics;
import minecade.dungeonrealms.PartyMechanics.PartyMechanics;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandPAccept implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player p = (Player) sender;
		
		if(args.length != 0) {
			p.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Invalid Syntax. " + ChatColor.RED + "/paccept");
			return true;
		}
		
		if(!(PartyMechanics.party_invite.containsKey(p.getName()))) {
			p.sendMessage(ChatColor.RED + "No pending party invites.");
			return true;
		}
		
		String party_name = PartyMechanics.party_invite.get(p.getName());
		
		if(Bukkit.getPlayer(party_name) == null) {
			p.sendMessage(ChatColor.RED + "This party invite is no longer available.");
			PartyMechanics.party_invite.remove(p.getName());
			PartyMechanics.party_invite_time.remove(p.getName());
			return true;
		}
		
		if(PartyMechanics.party_map.get(party_name).getPartyMembers().size() >= 8) {
			p.sendMessage(ChatColor.RED + "This party is currently full (8/8).");
			PartyMechanics.party_invite.remove(p.getName());
			PartyMechanics.party_invite_time.remove(p.getName());
			return true;
		}
		
		for(String s : PartyMechanics.party_map.get(party_name).getPartyMembers()) {
			if(Bukkit.getPlayer(s) != null) {
				Player pty_mem = Bukkit.getPlayer(s);
				pty_mem.sendMessage(ChatColor.LIGHT_PURPLE.toString() + "<" + ChatColor.BOLD + "P" + ChatColor.LIGHT_PURPLE + ">" + ChatColor.GRAY + " " + p.getName() + ChatColor.GRAY.toString() + " has " + ChatColor.LIGHT_PURPLE + ChatColor.UNDERLINE + "joined" + ChatColor.GRAY + " your party.");
			}
		}
		
		PartyMechanics.party_map.get(party_name).addPlayer(p);
		PartyMechanics.party_map.put(p.getName(), PartyMechanics.party_map.get(party_name));
		p.sendMessage("");
		p.sendMessage(ChatColor.LIGHT_PURPLE + "You have joined " + ChatColor.BOLD + party_name + "'s" + ChatColor.LIGHT_PURPLE + " party.");
		p.sendMessage(ChatColor.GRAY + "To chat with your party, use " + ChatColor.BOLD + "/p" + ChatColor.GRAY + " OR " + ChatColor.BOLD + " /p <message>");
		AchievementMechanics.addAchievement(p.getName(), "Party up!");
		PartyMechanics.party_invite.remove(p.getName());
		PartyMechanics.party_invite_time.remove(p.getName());
		return true;
	}
	
}