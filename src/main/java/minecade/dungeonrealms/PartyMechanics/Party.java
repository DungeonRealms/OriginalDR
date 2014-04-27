package minecade.dungeonrealms.PartyMechanics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import minecade.dungeonrealms.HealthMechanics.HealthMechanics;
import minecade.dungeonrealms.Hive.Hive;
import minecade.dungeonrealms.InstanceMechanics.InstanceMechanics;
import minecade.dungeonrealms.KarmaMechanics.KarmaMechanics;
import minecade.dungeonrealms.ScoreboardMechanics.ScoreboardMechanics;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;

@SuppressWarnings("deprecation")
public class Party {
	
	private Player leader;
	private ArrayList<String> players = new ArrayList<String>();
	
	public Party(Player leader) {
		this.leader = leader;
		addPlayer(leader);
	}
	
	public List<String> getPartyMembers() {
		return players;
	}
	
	public void addPlayer(Player pl) {
		if(players.contains(pl)) return;
		players.add(pl.getName());
		PartyMechanics.party_map.put(pl.getName(), this);
		updateScoreboard();
		if(getPartyMembers().size() == 1) { return; }
		
		int party_count = getPartyMembers().size();
		
		for(String s : getPartyMembers()) {
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
		for(String tn : getPartyMembers()) {
			if(Bukkit.getPlayer(tn) == null) {
				continue;
			}
			Player t = Bukkit.getPlayer(tn);
			String name = getPlayerName(p);
			if(name.length() > 16) name = name.substring(0, 16);
			ScoreboardMechanics.getBoard(t).resetScores(Bukkit.getOfflinePlayer(name));
		}
		
		Objective o = ScoreboardMechanics.getBoard(p).getObjective(DisplaySlot.SIDEBAR);
		if(o != null) o.unregister();
		
		players.remove(p.getName());
		
		PartyMechanics.party_map.remove(p.getName());
		PartyMechanics.party_only.remove(p.getName());
		
		KarmaMechanics.sendAlignColor(p, p);
		if(ScoreboardMechanics.getBoard(p) != null && ScoreboardMechanics.getBoard(p).getObjective(DisplaySlot.SIDEBAR) != null) {
			ScoreboardMechanics.getBoard(p).getObjective(DisplaySlot.SIDEBAR).unregister();
		}
		
		InstanceMechanics.teleport_on_load.remove(p.getName());
		
		if(InstanceMechanics.saved_location_instance.containsKey(p.getName())) {
			// Inside an instance.
			p.teleport(InstanceMechanics.saved_location_instance.get(p.getName()));
			InstanceMechanics.saved_location_instance.remove(p.getName());
		}
		InstanceMechanics.removeFromInstanceParty(p.getName());
		
		if(p.getName().equalsIgnoreCase(leader.getName()) && getPartyMembers().size() > 0) {
			String new_leader = "";
			int size_mod = 1;
			if(getPartyMembers().size() <= 1) {
				size_mod = 0;
			}
			int party_index = new Random().nextInt(getPartyMembers().size() - size_mod);
			List<String> remaining_members = new ArrayList<String>();
			for(String s : getPartyMembers()) {
				if(s.equalsIgnoreCase(p.getName())) {
					continue;
				}
				remaining_members.add(s);
			}
			leader = Bukkit.getPlayer(remaining_members.get(party_index));
			for(String x : getPartyMembers()) {
				if(Bukkit.getPlayer(x) == null) continue;
				ScoreboardMechanics.getBoard(Bukkit.getPlayer(x)).resetScores(leader);
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
			for(String s : getPartyMembers()) {
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
	
	public String getPlayerName(Player p) {
		return ChatColor.WHITE.toString() + (getLeader().getName().equalsIgnoreCase(p.getName()) ? ChatColor.BOLD.toString() + p.getName() : p.getName());
	}
	
	public String[] toStringArray(Object[] oa) {
		String[] s = new String[oa.length];
		for(int x = 0; x < oa.length; x++) {
			s[x] = oa[x].toString();
		}
		return s;
	}
	
	public void updateScoreboard() {
		List<String> second = Arrays.asList(toStringArray(getPartyMembers().toArray()));
		Iterator<String> players = getPartyMembers().iterator();
		while(players.hasNext()) {
			String pn = players.next();
			if(Bukkit.getPlayer(pn) == null) continue;
			Player p = Bukkit.getPlayer(pn);
			Objective o = ScoreboardMechanics.getBoard(p).getObjective(DisplaySlot.SIDEBAR);
			if(o == null) {
				o = ScoreboardMechanics.getBoard(p).registerNewObjective("player_data", "dummy");
				o.setDisplayName(ChatColor.RED.toString() + ChatColor.BOLD.toString() + "Party");
				o.setDisplaySlot(DisplaySlot.SIDEBAR);
			}
			for(String tn : second) {
				if(Bukkit.getPlayer(tn) == null) {
					ScoreboardMechanics.getBoard(p).resetScores(Bukkit.getOfflinePlayer(tn));
					continue;
				}
				Player t = Bukkit.getPlayer(tn);
				String name = getPlayerName(t);
				if(name.length() > 16) name = name.substring(0, 16);
				o.getScore(Bukkit.getOfflinePlayer(name)).setScore(HealthMechanics.getPlayerHP(t.getName()));
			}
		}
	}
}
