package me.vaqxine.PartyMechanics;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

import me.vaqxine.HealthMechanics.HealthMechanics;
import me.vaqxine.Hive.Hive;
import me.vaqxine.InstanceMechanics.InstanceMechanics;
import me.vaqxine.KarmaMechanics.KarmaMechanics;
import me.vaqxine.ScoreboardMechanics.ScoreboardMechanics;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;

public class Party {
	Player leader;
	CopyOnWriteArrayList<Player> players = new CopyOnWriteArrayList<Player>();
	
	public Party(Player leader) {
		this.leader = leader;
		addPlayer(leader);
	}
	
	public List<Player> getPartyMembers() {
		return players;
	}
	
	public void addPlayer(Player pl) {
		if(players.contains(pl)) return;
		players.add(pl);
		PartyMechanics.party_map.put(pl.getName(), this);
		if(getPartyList().size() == 1) {
			// Just the leader so dont really do anything pls ty
			return;
		}
		
		updateScoreboard(Update.HEALTH);
		
		int party_count = getPartyMembers().size();
		
		for(String s : getPartyList()) {
			if(s.equalsIgnoreCase(pl.getName())) {
				continue;
			}
			if(Bukkit.getPlayer(s) == null) {
				continue;
			}
			final Player p_mem = Bukkit.getPlayer(s);
			if(party_count == 4) {
				p_mem.sendMessage(ChatColor.GRAY + "You now have " + ChatColor.BOLD + "4/8" + ChatColor.GRAY + " party members. You will now recieve increased drop rates when fighting together.");
			}
			if(party_count == 8) {
				p_mem.sendMessage(ChatColor.GRAY + "You now have " + ChatColor.BOLD + "8/8" + ChatColor.GRAY + " party members. You will now recieve +5% DMG/ARMOR AND " + ChatColor.UNDERLINE + "GREATLY" + ChatColor.GRAY + " increased drop rates when fighting together.");
			}
			
			/*
			 * this.getServer().getScheduler().scheduleSyncDelayedTask(this, new
			 * Runnable() { public void run() { sendPartyColor(pl, p_mem, true);
			 * } }, 20L);
			 */
		}
	}
	
	public void removePlayer(Player p) {
		players.remove(p);
		PartyMechanics.party_map.remove(p.getName());
		// Removes the invite
		// Removes the party only chat
		PartyMechanics.party_only.remove(p.getName());
		KarmaMechanics.sendAlignColor(p, p);
		if(ScoreboardMechanics.getBoard(p) != null && ScoreboardMechanics.getBoard(p).getObjective(DisplaySlot.SIDEBAR) != null){
		ScoreboardMechanics.getBoard(p).getObjective(DisplaySlot.SIDEBAR).unregister();
		}
		String name = (p == leader ? ChatColor.BOLD : "") + "" + p.getName();
		if(name.length() > 16) name = name.substring(0, 16);
		for(Player x : getPartyMembers()) {
			ScoreboardMechanics.getBoard(x).resetScores(Bukkit.getOfflinePlayer(name));
		}
		
		InstanceMechanics.teleport_on_load.remove(p.getName());
		
		if(InstanceMechanics.saved_location_instance.containsKey(p.getName())) {
			// Inside an instance.
			p.teleport(InstanceMechanics.saved_location_instance.get(p.getName()));
			InstanceMechanics.saved_location_instance.remove(p.getName());
		}
		InstanceMechanics.removeFromInstanceParty(p.getName());
		
		if(p.getName().equalsIgnoreCase(leader.getName()) && getPartyList().size() > 0) {
			String new_leader = "";
			int size_mod = 1;
			if(getPartyList().size() <= 1) {
				size_mod = 0;
			}
			int party_index = new Random().nextInt(getPartyList().size() - size_mod);
			List<String> remaining_members = new ArrayList<String>();
			for(String s : getPartyList()) {
				if(s.equalsIgnoreCase(p.getName())) {
					continue;
				}
				remaining_members.add(s);
			}
			leader = Bukkit.getPlayer(remaining_members.get(party_index));
			for(Player x : getPartyMembers()) {
				ScoreboardMechanics.getBoard(x).resetScores(leader);
			}
			// TODO MOVES THIS
			
			for(String s : remaining_members) {
				if(Bukkit.getPlayer(s) != null) {
					Player pty_mem = Bukkit.getPlayer(s);
					if(!(pty_mem.getScoreboard().getPlayers().contains(Bukkit.getOfflinePlayer(s)))) {
						pty_mem.setScoreboard(Bukkit.getPlayer(s).getScoreboard());
					}
					/*
					 * if(!new_ui.hasPlayerAdded(pty_mem)){
					 * new_ui.showToPlayer(pty_mem); }
					 */
					pty_mem.sendMessage(ChatColor.LIGHT_PURPLE.toString() + "<" + ChatColor.BOLD + "P" + ChatColor.LIGHT_PURPLE + ">" + ChatColor.GRAY + " " + p.getName() + ChatColor.GRAY.toString() + " has " + ChatColor.LIGHT_PURPLE + ChatColor.UNDERLINE + "left" + ChatColor.GRAY.toString() + " your party.");
					pty_mem.sendMessage(ChatColor.LIGHT_PURPLE.toString() + "<" + ChatColor.BOLD + "P" + ChatColor.LIGHT_PURPLE + "> " + ChatColor.GRAY + ChatColor.LIGHT_PURPLE.toString() + new_leader + ChatColor.GRAY.toString() + " has been promoted to " + ChatColor.UNDERLINE + "Party Leader");
				}
			}
		} else {
			for(String s : getPartyList()) {
				if(Bukkit.getPlayer(s) != null && s != p.getName()) {
					Player pty_mem = Bukkit.getPlayer(s);
					pty_mem.sendMessage(ChatColor.LIGHT_PURPLE.toString() + "<" + ChatColor.BOLD + "P" + ChatColor.LIGHT_PURPLE + ">" + ChatColor.GRAY + " " + p.getName() + ChatColor.GRAY.toString() + " has " + ChatColor.RED + ChatColor.UNDERLINE + "left" + ChatColor.GRAY.toString() + " your party.");
				}
			}
		}
		
		if(!Hive.pending_upload.contains(p.getName())) {
			HealthMechanics.setOverheadHP(p, HealthMechanics.getPlayerHP(p.getName()));
		}
		
	}
	
	public Player getLeader() {
		return leader;
	}
	
	public List<String> getPartyList() {
		List<String> to_return = new ArrayList<String>();
		for(Player p : players) {
			to_return.add(p.getName());
		}
		return to_return;
	}
	
	public String getPlayerName(Player p) {
		return ChatColor.WHITE.toString() + (getLeader().getName().equalsIgnoreCase(p.getName()) ? ChatColor.BOLD.toString() + p.getName() : p.getName());
	}
	
	public void updateScoreboard(Update update) {
		if(update == Update.HEALTH) {
			for(Player p : getPartyMembers()) {
				if(ScoreboardMechanics.getBoard(p).getObjective(DisplaySlot.SIDEBAR) == null) {
					Objective obj = ScoreboardMechanics.getBoard(p).registerNewObjective("player_data", "dummy");
					obj.setDisplayName(ChatColor.RED.toString() + ChatColor.BOLD.toString() + "Party");
					obj.setDisplaySlot(DisplaySlot.SIDEBAR);
					updateScoreboard(Update.HEALTH);
				}
				for(Player t : getPartyMembers()) {
					String name = getPlayerName(p);
					if(name.length() > 16) name = name.substring(0, 16);
					ScoreboardMechanics.getBoard(p).getObjective(DisplaySlot.SIDEBAR).getScore(Bukkit.getOfflinePlayer(name)).setScore(HealthMechanics.getPlayerHP(ChatColor.stripColor(t.getName())));
				}
			}
		}
	}
	
	public enum Update {
		HEALTH, LEADER_CHANGE;
	}
}
