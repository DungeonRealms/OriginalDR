package me.vaqxine.PartyMechanics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import me.vaqxine.Main;
import me.vaqxine.ChatMechanics.ChatMechanics;
import me.vaqxine.CommunityMechanics.CommunityMechanics;
import me.vaqxine.DuelMechanics.DuelMechanics;
import me.vaqxine.PartyMechanics.Party.Update;
import me.vaqxine.PartyMechanics.commands.CommandP;
import me.vaqxine.PartyMechanics.commands.CommandPAccept;
import me.vaqxine.PartyMechanics.commands.CommandPDecline;
import me.vaqxine.PartyMechanics.commands.CommandPInvite;
import me.vaqxine.PartyMechanics.commands.CommandPKick;
import me.vaqxine.PartyMechanics.commands.CommandPLoot;
import me.vaqxine.PartyMechanics.commands.CommandPQuit;
import me.vaqxine.PartyMechanics.commands.CommandParty;
import net.minecraft.server.v1_7_R2.EntityPlayer;
import net.minecraft.server.v1_7_R2.Packet;
import net.minecraft.server.v1_7_R2.PacketPlayOutEntityEquipment;
import net.minecraft.server.v1_7_R2.PacketPlayOutNamedEntitySpawn;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_7_R2.entity.CraftPlayer;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.ScoreboardManager;

public class PartyMechanics implements Listener {
	static Logger log = Logger.getLogger("Minecraft");
	public static ScoreboardManager manager;
	
	public static HashMap<String, String> party_invite = new HashMap<String, String>();
	// Player_name, Party_title
	
	public static ConcurrentHashMap<String, Long> party_invite_time = new ConcurrentHashMap<String, Long>();
	// Player_name, Party_title
	
	public static ConcurrentHashMap<String, Party> party_map = new ConcurrentHashMap<String, Party>();
	// Party_name, Party_members
	
	public static HashMap<String, String> party_loot = new HashMap<String, String>();
	// Party_name, Loot Type (random/roundrobin)
	
	public static HashMap<String, Integer> party_loot_index = new HashMap<String, Integer>();
	// Party_name, Index for next roundrobin (from party_map)
	
	// Player_name, Party_name
	// Player_name, Level (hp) -- used as an Async method of accessing getLevel().
	
	public static List<String> party_only = new ArrayList<String>();
	public static HashSet<Party> parties_checked = new HashSet<Party>();
	
	// Party-only chat toggle.
	
	// public static HashMap<String, Integer> party_name_incr = new HashMap<String, Integer>();
	// Player_name, increment -- this is used to prevent crashes from players reciving same scoreboard twice.
	
	public void onEnable() {
		Main.plugin.getServer().getPluginManager().registerEvents(this, Main.plugin);
		manager = Bukkit.getScoreboardManager();
		
		Main.plugin.getCommand("p").setExecutor(new CommandP()); // TODO - Check Command
		Main.plugin.getCommand("paccept").setExecutor(new CommandPAccept());
		Main.plugin.getCommand("party").setExecutor(new CommandParty());
		Main.plugin.getCommand("pdecline").setExecutor(new CommandPDecline());
		Main.plugin.getCommand("pinvite").setExecutor(new CommandPInvite());
		Main.plugin.getCommand("pkick").setExecutor(new CommandPKick());
		Main.plugin.getCommand("ploot").setExecutor(new CommandPLoot());
		// Main.plugin.getCommand("ppromote").setExecutor(new CommandPPromote());
		Main.plugin.getCommand("pquit").setExecutor(new CommandPQuit());
		
		Main.plugin.getServer().getScheduler().scheduleSyncRepeatingTask(Main.plugin, new Runnable() {
			public void run() {
				for(Entry<String, Long> data : party_invite_time.entrySet()) {
					String p_name = data.getKey();
					long time = data.getValue();
					
					if((System.currentTimeMillis() - time) >= (30 * 1000)) {
						// 30s invite timeout.
						String party_owner = party_invite.get(p_name);
						party_invite.remove(p_name);
						party_invite_time.remove(p_name);
						if(Bukkit.getPlayer(p_name) != null) {
							Player pl = Bukkit.getPlayer(p_name);
							pl.sendMessage(ChatColor.RED + "Party invite from " + ChatColor.BOLD + party_owner + ChatColor.RED + " expired.");
						}
						if(Bukkit.getPlayer(party_owner) != null) {
							Player pl = Bukkit.getPlayer(party_owner);
							pl.sendMessage(ChatColor.RED + "Party invite to " + ChatColor.BOLD + p_name + ChatColor.RED + " has expired.");
						}
					}
				}
			}
		}, 5 * 20L, 20L);
		
		new BukkitRunnable() {
			
			@Override
			public void run() {
				for(String p_name : party_map.keySet()) {
					if(Bukkit.getPlayer(p_name) == null){
					    party_map.remove(p_name);
					    continue;
					}
					if(parties_checked.contains(party_map.get(p_name))) continue;
					//The party was already updated
					Party party = party_map.get(p_name);
					party.updateScoreboard(Update.HEALTH);
					parties_checked.add(party);
				}
				//Clear it for the next go around
				parties_checked.clear();
			}
		}.runTaskTimer(Main.plugin, 0, 20L);
		log.info("[PartyMechanics] has been ENABLED.");
	}
	
	public void onDisable() {
		log.info("[PartyMechanics] has been disabled.");
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		Player pl = e.getPlayer();
		if(party_map.containsKey(pl.getName())) {
			party_map.get(pl.getName()).removePlayer(pl);
		}
		if(party_invite.containsKey(pl.getName())) {
			pl.performCommand("pdecline");
		}
	}
	
	// TODO ADD SOME SORT OF LOOTING SYSTEM
	/*
	 * @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true) public void onItemPickup(PlayerPickupItemEvent e) { Player pl = e.getPlayer(); if
	 * (hasParty(pl.getName())) { String party_leader = inv_party_map.get(pl.getName()); if (getPartyCount(party_leader) > 1 &&
	 * party_loot.containsKey(party_leader)) { String loot_type = party_loot.get(party_leader); if (loot_type.equalsIgnoreCase("random")) { return; } else if
	 * (loot_type.equalsIgnoreCase("roundrobin")) { int index = 0; if (!party_loot_index.containsKey(party_leader)) { index = 0; } else { index =
	 * party_loot_index.get(party_leader); }
	 * 
	 * String member = party_map.get(party_leader).getPartyList().get(index); if (Main.plugin.getServer().getPlayer(member) != null &&
	 * !(pl.getName().equalsIgnoreCase(member))) { Player p_member = Main.plugin.getServer().getPlayer(member); if
	 * (HealthMechanics.in_combat.containsKey(p_member.getName()) && p_member.getWorld().getName().equalsIgnoreCase(pl.getWorld().getName())) { if
	 * (p_member.getLocation().distanceSquared(pl.getLocation()) <= 2304) { ItemStack loot = e.getItem().getItemStack();
	 * 
	 * if (p_member.getInventory().firstEmpty() != -1) { e.setCancelled(true); e.getItem().remove();
	 * 
	 * p_member.getInventory().addItem(loot); p_member.playSound(p_member.getLocation(), Sound.ITEM_PICKUP, 1F, 1F); } else { // No room, greeaaatt... Skip
	 * them, give whoever was gonna pickup the item, idiots. } } } }
	 * 
	 * index++; if (index >= getPartyCount(party_leader)) { // Overflow, restart. index = 0; }
	 * 
	 * party_loot_index.put(party_leader, index); } } } }
	 */
	@EventHandler(priority = EventPriority.LOWEST)
	public void onEntityDamageByEntityEvent(EntityDamageEvent e) {
		
		if(!(e instanceof EntityDamageByEntityEvent)) { return; }
		
		if(e.getCause() == DamageCause.ENTITY_ATTACK) {
			EntityDamageByEntityEvent edbee = (EntityDamageByEntityEvent) e;
			
			if(e.getEntity() instanceof Player && edbee.getDamager() instanceof Player) {
				Player attacker = (Player) edbee.getDamager();
				Player hurt = (Player) e.getEntity();
				
				if(arePartyMembers(attacker.getName(), hurt.getName())) {
					if(DuelMechanics.duel_map.containsKey(attacker.getName()) && DuelMechanics.duel_map.containsKey(hurt.getName())) { return; }
					e.setCancelled(true); // Friendly fire is OFF.
					e.setDamage(0);
				}
			}
		}
		
		if(e.getCause() == DamageCause.PROJECTILE) {
			EntityDamageByEntityEvent edbee = (EntityDamageByEntityEvent) e;
			
			if(e.getEntity() instanceof Player && edbee.getDamager() instanceof Arrow) {
				Arrow a = (Arrow) edbee.getDamager();
				if(a.getShooter() instanceof Player) {
					Player shooter = (Player) a.getShooter();
					Player hurt = (Player) e.getEntity();
					
					if(arePartyMembers(shooter.getName(), hurt.getName())) {
						if(DuelMechanics.duel_map.containsKey(shooter.getName()) && DuelMechanics.duel_map.containsKey(hurt.getName())) { return; }
						e.setCancelled(true); // Friendly fire is OFF.
						e.setDamage(0);
					}
				}
			}
		}
		
	}
	
	public static void sendPartyColor(Player p_to_send, Player p_viewer, boolean in_party) {
		if(p_to_send.getName().equalsIgnoreCase(p_viewer.getName())) { return; }
		
		ChatColor c = null;
		c = ChatColor.LIGHT_PURPLE;
		
		if(in_party == false) {
			c = ChatColor.WHITE;
		}
		
		if(p_to_send.isOp()) {
			c = ChatColor.AQUA;
			CommunityMechanics.setColor(p_to_send, c);
			return;
		}
		
		String r_name = p_to_send.getName();
		
		EntityPlayer ent_p_edited = ((CraftPlayer) p_to_send).getHandle();
		net.minecraft.server.v1_7_R2.ItemStack boots = null, legs = null, chest = null, head = null;
		
		try {
			if(ent_p_edited.getEquipment(1) != null) {
				boots = ent_p_edited.getEquipment(1);
			}
			if(ent_p_edited.getEquipment(2) != null) {
				legs = ent_p_edited.getEquipment(2);
			}
			if(ent_p_edited.getEquipment(3) != null) {
				chest = ent_p_edited.getEquipment(3);
			}
			if(ent_p_edited.getEquipment(4) != null) {
				head = ent_p_edited.getEquipment(4);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		if(c == ChatColor.WHITE) {
			ent_p_edited.displayName = ChatColor.stripColor(p_to_send.getName());
			// No color.
		} else {
			ent_p_edited.displayName = c.toString() + ChatColor.stripColor(p_to_send.getName());
		}
		
		((CraftPlayer) p_viewer).getHandle().playerConnection.sendPacket(new PacketPlayOutNamedEntitySpawn(ent_p_edited));
		
		List<Packet> pack_list = new ArrayList<Packet>();
		if(boots != null) {
			pack_list.add(new PacketPlayOutEntityEquipment(ent_p_edited.getId(), 1, boots));
		}
		if(legs != null) {
			pack_list.add(new PacketPlayOutEntityEquipment(ent_p_edited.getId(), 2, legs));
		}
		if(chest != null) {
			pack_list.add(new PacketPlayOutEntityEquipment(ent_p_edited.getId(), 3, chest));
		}
		if(head != null) {
			pack_list.add(new PacketPlayOutEntityEquipment(ent_p_edited.getId(), 4, head));
		}
		
		for(Packet pa : pack_list) {
			((CraftPlayer) p_viewer).getHandle().playerConnection.sendPacket(pa);
		}
		
		ent_p_edited.displayName = ChatColor.stripColor(r_name);
	}
	
	public static boolean arePartyMembers(String p1, String p2) {
		if(party_map.containsKey(p1) && party_map.containsKey(p2)) {
			//They are in the same hashmap
			if(party_map.get(p1).getPartyList().contains(p2) && party_map.get(p2).getPartyList().contains(p1)) { return true; }
		}
		return false;
	}
	
	public static int getPartyCount(String party_name) {
		if(!(party_map.containsKey(party_name))) { return 0; }
		return party_map.get(party_name).getPartyMembers().size();
	}
	
	public static Party getPlayerParty(Player p) {
		if(party_map.containsKey(p.getName())) { return party_map.get(p.getName()); }
		return new Party(p);
	}
	
	public static boolean hasParty(String p_name) {
		if(!(party_map.containsKey(p_name))) { return false; }
		return true;
	}
	
	// AnEpicPlayerName
	
	public static void createParty(String party_title, Player p_owner, List<String> existing_members) {
		/*
		 * if(!p_owner.getName().equals("Notch")){ p_owner.sendMessage(ChatColor.RED + "Parties are temporarily disabled due to a 1.7.2 conflict."); return; }
		 */
		
		//String p_owner_name = p_owner.getName();
		
		//if (p_owner.getName().length() > 13) {
		//    p_owner_name = p_owner.getName().substring(0, 13);
		//}
		//Scoreboard party_ui = p_owner.getScoreboard();
		///p_owner.sendMessage(ChatColor.DARK_AQUA + "SCOREBOARD SET!");
		//Objective obj = party_ui.registerNewObjective("player_data", "dummy");
		//obj.setDisplayName(ChatColor.RED.toString() + ChatColor.BOLD.toString() + "Party");
		//obj.setDisplaySlot(DisplaySlot.SIDEBAR);
		/*
		 * party_ui.setType(Scoreboard.Type.SIDEBAR); party_ui.setScoreboardName(ChatColor.RED.toString() + ChatColor.BOLD.toString() + "Party");
		 */
		//Score hp = obj.getScore(Bukkit.getOfflinePlayer(ChatColor.BOLD + p_owner_name));
		//hp.setScore(HealthMechanics.getPlayerHP(p_owner.getName()));
		
		//p_owner.setScoreboard(party_ui);
		//final Player p_test = p_owner;
		// party_ui.setItem(ChatColor.BOLD + p_owner_name, HealthMechanics.getPlayerHP(p_owner.getName()), false);
		// party_ui.showToPlayer(p_owner);
		Party party = new Party(p_owner);
		if(existing_members == null) {
			party_map.put(p_owner.getName(), party);
			party_loot.put(p_owner.getName(), "random");
			party_loot_index.put(p_owner.getName(), 0);
			
		} else {
			for(String s : existing_members) {
				if(Bukkit.getPlayer(s) == null) continue;
				party.addPlayer(Bukkit.getPlayer(s));
			}
			for(String s : existing_members) {
				if(p_owner.getName().equalsIgnoreCase(s)) {
					continue; // Prevents duplicates.
				}
				if(Bukkit.getPlayer(s) != null) {
					// Set them in menu.
					Player pl = Bukkit.getPlayer(s);
					String p_name = pl.getName();
					if(p_name.length() > 14) {
						p_name = p_name.substring(0, 14);
					}
					
					//hp = obj.getScore(Bukkit.getOfflinePlayer(p_name));
					//hp.setScore(HealthMechanics.getPlayerHP(p_owner.getName()));
					//System.out.print("SET SCOREBOARD TO " + pl.getName() + " FROM LINE 377 OBJECTIVES SIZE: " + party_ui.getObjectives().size() + "OBJECTIVES 1: " + party_ui.getObjectives().toArray());
					//pl.setScoreboard(party_ui);
				}
			}
			
			// TODO: Make this inherent old loot style
			party_loot.put(p_owner.getName(), "random");
			party_loot_index.put(p_owner.getName(), 0);
			// sendRawMessageToParty(p_owner.getName(), "The loot profile of this party has been set to: " + ChatColor.LIGHT_PURPLE + "RANDOM");
		}
	}
	
	public static void inviteToParty(Player to_invite, Player p_owner) {
		/*
		 * if (!p_owner.getName().equals("Notch")) { p_owner.sendMessage(ChatColor.RED + "Parties are temporarily disabled due to a 1.7.2 conflict."); return; }
		 */
		if(!party_map.containsKey(p_owner)) party_map.put(p_owner.getName(), new Party(p_owner));
		if(!(isPartyLeader(p_owner.getName()))) {
			if(party_map.containsKey(p_owner.getName())) { // In another party.
				p_owner.sendMessage(ChatColor.RED.toString() + "You are NOT the leader of your party.");
				p_owner.sendMessage(ChatColor.GRAY.toString() + "Type " + ChatColor.BOLD.toString() + "/pquit" + ChatColor.GRAY + " to quit your current party.");
				return;
			} else { // No party.
				createParty(p_owner.getName(), p_owner, null);
				p_owner.sendMessage(ChatColor.GREEN.toString() + ChatColor.BOLD + "Party created.");
				p_owner.sendMessage(ChatColor.GRAY.toString() + "To invite more people to join your party, " + ChatColor.UNDERLINE + "Left Click" + ChatColor.GRAY.toString() + " them with your character journal or use " + ChatColor.BOLD + "/pinvite" + ChatColor.GRAY + ". To kick, use " + ChatColor.BOLD + "/pkick" + ChatColor.GRAY + ". To chat with party, use " + ChatColor.BOLD + "/p" + ChatColor.GRAY + " To change the loot profile, use " + ChatColor.BOLD + "/ploot");
			}
		}
		
		if(party_map.get(p_owner.getName()).getPartyList().size() >= 8) {
			p_owner.sendMessage(ChatColor.RED + "You cannot have more than " + ChatColor.ITALIC + "8 players" + ChatColor.RED + " in a party.");
			p_owner.sendMessage(ChatColor.GRAY + "You may use /pkick to kick out unwanted members.");
		}
		
		if(party_map.containsKey(to_invite.getName())) {
			if(party_map.get(to_invite.getName()).getLeader().getName().equalsIgnoreCase(p_owner.getName())) {
				p_owner.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + to_invite.getName() + ChatColor.RED + " is already in your party.");
				p_owner.sendMessage(ChatColor.GRAY + "Type /pkick " + to_invite.getName() + " to kick them out.");
			} else {
				p_owner.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + to_invite.getName() + ChatColor.RED + " is already in another party.");
			}
			return;
		}
		if(party_invite.containsKey(to_invite.getName())) {
			p_owner.sendMessage(ChatColor.RED + to_invite.getName() + " has a pending party invite.");
			return;
		}
		
		to_invite.sendMessage(ChatColor.LIGHT_PURPLE.toString() + ChatColor.UNDERLINE + p_owner.getName() + ChatColor.GRAY + " has invited you to join their party. To accept, type " + ChatColor.LIGHT_PURPLE.toString() + "/paccept" + ChatColor.GRAY + " or to decline, type " + ChatColor.LIGHT_PURPLE.toString() + "/pdecline");
		p_owner.sendMessage(ChatColor.GRAY + "You have invited " + ChatColor.LIGHT_PURPLE.toString() + to_invite.getName() + ChatColor.GRAY + " to join your party.");
		
		party_invite.put(to_invite.getName(), p_owner.getName());
		party_invite_time.put(to_invite.getName(), System.currentTimeMillis());
	}
	
	public static boolean isPartyLeader(String p_name) {
		if(party_map.containsKey(p_name) && party_map.get(p_name).getLeader().getName().equalsIgnoreCase(p_name)) { return true; }
		return false;
	}
	
	public static int getPlayersInArea(Party party, Location center, double radius) {
		int count = 0;
		double rad_sqr = Math.pow(radius, 2);
		
		for(String mem : party.getPartyList()) {
			if(Bukkit.getPlayer(mem) != null) {
				Player p_mem = Bukkit.getPlayer(mem);
				if(!p_mem.getWorld().getName().equalsIgnoreCase(center.getWorld().getName())) {
					continue;
				}
				Location p_mem_loc = p_mem.getLocation();
				if(p_mem_loc.distanceSquared(center) <= rad_sqr) {
					count++;
					continue;
				}
			}
		}
		return count;
	}
	
	public static void sendMessageToParty(Player sender, String raw_msg) {
		List<Player> to_send = new ArrayList<Player>();
		
		if(party_map.get(sender.getName()) == null) { return; }
		
		for(Player mem : getPlayerParty(sender).getPartyMembers()) {
			to_send.add(mem);
		}
		
		for(Player pl : to_send) {
			ChatColor p_color = ChatMechanics.getPlayerColor(sender, pl);
			String prefix = ChatMechanics.getPlayerPrefix(sender.getName(), true);
			
			String personal_msg = raw_msg;
			if(ChatMechanics.hasAdultFilter(pl.getName())) {
				personal_msg = ChatMechanics.censorMessage(personal_msg);
			}
			personal_msg.trim();
			personal_msg = ChatMechanics.fixCapsLock(personal_msg);
			
			pl.sendMessage(ChatColor.LIGHT_PURPLE.toString() + "<" + ChatColor.BOLD + "P" + ChatColor.LIGHT_PURPLE + ">" + ChatColor.GRAY + " " + prefix + p_color + sender.getName() + ": " + ChatColor.GRAY + personal_msg);
		}
		
		log.info("<P> " + sender.getName() + ": " + raw_msg);
	}
	
}
