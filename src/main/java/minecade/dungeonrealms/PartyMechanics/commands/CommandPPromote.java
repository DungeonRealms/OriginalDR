package minecade.dungeonrealms.PartyMechanics.commands;

import java.util.ArrayList;
import java.util.List;

import minecade.dungeonrealms.PartyMechanics.PartyMechanics;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;

@SuppressWarnings("deprecation")
public class CommandPPromote implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player p = (Player) sender;
		
		if(args.length != 1) {
			p.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Invalid Syntax. " + ChatColor.RED + "/ppromote <player>");
			return true;
		}
		
		String p_name = args[0];
		if(Bukkit.getPlayer(p_name) == null) {
			p.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + p_name + ChatColor.RED + " is OFFLINE");
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
		
		p_name = Bukkit.getPlayer(p_name).getName();
		
		if(!(PartyMechanics.arePartyMembers(p.getName(), p_name))) {
			p.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + p_name + " is not in your party.");
			return true;
		}
		
		String new_leader = "";
		
		List<String> remaining_members = new ArrayList<String>();
		for(String s : PartyMechanics.party_map.get(p.getName()).getPartyMembers()) {
			remaining_members.add(s);
		}
		new_leader = Bukkit.getPlayer(p_name).getName();
		PartyMechanics.party_map.remove(p.getName());
		//PartyMechanics.party_map.put(new_leader, remaining_members);
		
		//api.getScoreboard(p.getName()).stopShowingAllPlayers();
		for(OfflinePlayer mem : p.getScoreboard().getPlayers()) {
			//api.getScoreboard(getPartyTitle(p.getName())).showToPlayer(mem, false);
			if(Bukkit.getPlayer(mem.getName()) != null) {
				Bukkit.getPlayer(mem.getName()).setScoreboard(PartyMechanics.manager.getNewScoreboard());
			}
		}
		
		//api.getScoreboard(getPartyTitle(p.getName())).stopShowingAllPlayers();
		//api.getScoreboards().remove(api.getScoreboard(getPartyTitle(p.getName())));
		
		PartyMechanics.createParty(new_leader, Bukkit.getPlayer(new_leader), remaining_members);
		Scoreboard new_ui = PartyMechanics.manager.getNewScoreboard();
		//Scoreboard new_ui = api.getScoreboard(getPartyTitle(new_leader));
		
		for(String s : remaining_members) {
			//PartyMechanics.party_map.put(s, new_leader);
			if(Bukkit.getPlayer(s) != null) {
				Player pty_mem = Bukkit.getPlayer(s);
				if(!new_ui.getPlayers().contains(pty_mem.getName())) {
					pty_mem.setScoreboard(new_ui);
					//new_ui.showToPlayer(pty_mem);
				}
				pty_mem.sendMessage(ChatColor.LIGHT_PURPLE.toString() + "<" + ChatColor.BOLD + "P" + ChatColor.LIGHT_PURPLE + "> " + ChatColor.GRAY + ChatColor.LIGHT_PURPLE.toString() + new_leader + ChatColor.GRAY.toString() + " has been promoted to " + ChatColor.UNDERLINE + "Party Leader");
			}
		}
		return true;
	}
	
}