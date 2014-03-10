package me.vaqxine.PartyMechanics;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import me.vaqxine.Main;
import me.vaqxine.ChatMechanics.ChatMechanics;
import me.vaqxine.CommunityMechanics.CommunityMechanics;
import me.vaqxine.DuelMechanics.DuelMechanics;
import me.vaqxine.HealthMechanics.HealthMechanics;
import me.vaqxine.Hive.Hive;
import me.vaqxine.InstanceMechanics.InstanceMechanics;
import me.vaqxine.KarmaMechanics.KarmaMechanics;
import me.vaqxine.PartyMechanics.commands.CommandP;
import me.vaqxine.PartyMechanics.commands.CommandPAccept;
import me.vaqxine.PartyMechanics.commands.CommandPDecline;
import me.vaqxine.PartyMechanics.commands.CommandPInvite;
import me.vaqxine.PartyMechanics.commands.CommandPKick;
import me.vaqxine.PartyMechanics.commands.CommandPLoot;
import me.vaqxine.PartyMechanics.commands.CommandPPromote;
import me.vaqxine.PartyMechanics.commands.CommandPQuit;
import me.vaqxine.PartyMechanics.commands.CommandParty;
import net.minecraft.server.v1_7_R1.EntityPlayer;
import net.minecraft.server.v1_7_R1.Packet;
import net.minecraft.server.v1_7_R1.PacketPlayOutEntityEquipment;
import net.minecraft.server.v1_7_R1.PacketPlayOutNamedEntitySpawn;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_7_R1.entity.CraftPlayer;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

public class PartyMechanics implements Listener {
	static Logger log = Logger.getLogger("Minecraft");
	public static ScoreboardManager manager;

	public static HashMap<String, String> party_invite = new HashMap<String, String>();
	// Player_name, Party_title

	public static ConcurrentHashMap<String, Long> party_invite_time = new ConcurrentHashMap<String, Long>();
	// Player_name, Party_title

	public static HashMap<String, List<String>> party_map = new HashMap<String, List<String>>();
	// Party_name, Party_members

	public static HashMap<String, String> party_loot = new HashMap<String, String>();
	// Party_name, Loot Type (random/roundrobin)

	public static HashMap<String, Integer> party_loot_index = new HashMap<String, Integer>();
	// Party_name, Index for next roundrobin (from party_map)

	public static HashMap<String, String> inv_party_map = new HashMap<String, String>();
	// Player_name, Party_name

	public static HashMap<String, Integer> player_hp = new HashMap<String, Integer>();
	// Player_name, Level (hp) -- used as an Async method of accessing getLevel().

	public static List<String> party_only = new ArrayList<String>();
	// Party-only chat toggle.

	public static HashMap<String, Integer> party_name_incr = new HashMap<String, Integer>();
	// Player_name, increment -- this is used to prevent crashes from players reciving same scoreboard twice.

	@SuppressWarnings("deprecation")
	public void onEnable(){
		Main.plugin.getServer().getPluginManager().registerEvents(this, Main.plugin);
		manager = Bukkit.getScoreboardManager();
		
		Main.plugin.getCommand("p").setExecutor(new CommandP()); // TODO - Check Command
		Main.plugin.getCommand("paccept").setExecutor(new CommandPAccept());
		Main.plugin.getCommand("party").setExecutor(new CommandParty());
		Main.plugin.getCommand("pdecline").setExecutor(new CommandPDecline());
		Main.plugin.getCommand("pinvite").setExecutor(new CommandPInvite());
		Main.plugin.getCommand("pkick").setExecutor(new CommandPKick());
		Main.plugin.getCommand("ploot").setExecutor(new CommandPLoot());
		Main.plugin.getCommand("ppromote").setExecutor(new CommandPPromote());
		Main.plugin.getCommand("pquit").setExecutor(new CommandPQuit());
		
		Main.plugin.getServer().getScheduler().scheduleAsyncRepeatingTask(Main.plugin, new Runnable() {
			public void run() {
				for(Player pl : Main.plugin.getServer().getOnlinePlayers()){
					player_hp.put(pl.getName(), HealthMechanics.getPlayerHP(pl.getName()));
				}
			}
		}, 5 * 20L, 2L);

		Main.plugin.getServer().getScheduler().scheduleAsyncRepeatingTask(Main.plugin, new Runnable() {
			public void run() {
				for(String party_name : party_map.keySet()){
					Scoreboard sb = Bukkit.getPlayer(party_name).getScoreboard();
					if(sb == null){
						continue;
					}
					/*if(!(party_map.containsKey(sb.getName()))){
						continue;
					}*/
					List<OfflinePlayer> reset = new ArrayList<OfflinePlayer>();
					for(OfflinePlayer pl : sb.getPlayers()){
						if(!(player_hp.containsKey(pl.getName()))){
							reset.add(pl);
							continue;
						}
						String special_char = "";
						String pl_name = pl.getName();
						if(isPartyLeader(ChatColor.stripColor(pl.getName()))){
							special_char = ChatColor.BOLD.toString(); //ChatColor.BOLD.toString();
							if(pl_name.length() > 13){
								pl_name = pl_name.substring(0, 13);
							}
						}

						if(pl_name.length() > 14){
							pl_name = pl_name.substring(0, 14);
						}

						try{
							Objective obj = sb.getObjective(DisplaySlot.SIDEBAR);
	
							Score hp = obj.getScore(Bukkit.getOfflinePlayer(special_char + ChatColor.stripColor(pl_name))); 
							hp.setScore(player_hp.get(pl.getName()));
							//sb.setItem(special_char + ChatColor.stripColor(pl_name), player_hp.get(pl.getName()), false);
							
						} catch(NullPointerException npe){
							continue;
						}
					}
					for(OfflinePlayer pl : reset){
						sb.resetScores(pl);
					}
				}
			}
		}, 5 * 20L, 5L);

		Main.plugin.getServer().getScheduler().scheduleAsyncRepeatingTask(Main.plugin, new Runnable() {
			public void run() {
				for(Entry<String, Long> data : party_invite_time.entrySet()){
					String p_name = data.getKey();
					long time = data.getValue();

					if((System.currentTimeMillis() - time) >= (30 * 1000)){
						// 30s invite timeout.
						String party_owner = party_invite.get(p_name);
						party_invite.remove(p_name);
						party_invite_time.remove(p_name);
						if(Bukkit.getPlayer(p_name) != null){
							Player pl = Bukkit.getPlayer(p_name);
							pl.sendMessage(ChatColor.RED + "Party invite from " + ChatColor.BOLD + party_owner + ChatColor.RED + " expired.");
						}
						if(Bukkit.getPlayer(party_owner) != null){
							Player pl = Bukkit.getPlayer(party_owner);
							pl.sendMessage(ChatColor.RED + "Party invite to " + ChatColor.BOLD + p_name + ChatColor.RED + " has expired.");
						}
					}
				}
			}
		}, 5 * 20L, 20L);

		log.info("[PartyMechanics] has been ENABLED.");
	}

	public void onDisable() {
		log.info("[PartyMechanics] has been disabled.");
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e){
		Player pl = e.getPlayer();
		if(inv_party_map.containsKey(pl.getName())){
			removePlayerFromParty(pl, inv_party_map.get(pl.getName()));
		}

		if(party_invite.containsKey(pl.getName())){
			pl.performCommand("pdecline");
		}

		player_hp.remove(pl.getName());
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onItemPickup(PlayerPickupItemEvent e){
		Player pl = e.getPlayer();
		if(hasParty(pl.getName())){
			String party_leader = inv_party_map.get(pl.getName());
			if(getPartyCount(party_leader) > 1 && party_loot.containsKey(party_leader)){
				String loot_type = party_loot.get(party_leader);
				if(loot_type.equalsIgnoreCase("random")){
					return;
				}
				else if(loot_type.equalsIgnoreCase("roundrobin")){
					int index = 0;
					if(!party_loot_index.containsKey(party_leader)){
						index = 0;
					}
					else{
						index = party_loot_index.get(party_leader);
					}

					String member = party_map.get(party_leader).get(index);
					if(Main.plugin.getServer().getPlayer(member) != null && !(pl.getName().equalsIgnoreCase(member))){
						Player p_member = Main.plugin.getServer().getPlayer(member);
						if(HealthMechanics.in_combat.containsKey(p_member.getName()) && p_member.getWorld().getName().equalsIgnoreCase(pl.getWorld().getName())){
							if(p_member.getLocation().distanceSquared(pl.getLocation()) <= 2304){
								ItemStack loot = e.getItem().getItemStack();

								if(p_member.getInventory().firstEmpty() != -1){
									e.setCancelled(true);
									e.getItem().remove();

									p_member.getInventory().addItem(loot);
									p_member.playSound(p_member.getLocation(), Sound.ITEM_PICKUP, 1F, 1F);
								}
								else{
									// No room, greeaaatt... Skip them, give whoever was gonna pickup the item, idiots.
								}
							}
						}
					}

					index++;
					if(index >= getPartyCount(party_leader)){
						// Overflow, restart.
						index = 0;
					}

					party_loot_index.put(party_leader, index);
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onEntityDamageByEntityEvent(EntityDamageEvent e){

		if(!(e instanceof EntityDamageByEntityEvent)){
			return;
		}

		if(e.getCause() == DamageCause.ENTITY_ATTACK){
			EntityDamageByEntityEvent edbee = (EntityDamageByEntityEvent)e;

			if(e.getEntity() instanceof Player && edbee.getDamager() instanceof Player){
				Player attacker = (Player)edbee.getDamager();
				Player hurt = (Player)e.getEntity();

				if(arePartyMembers(attacker.getName(), hurt.getName())){
					if(DuelMechanics.duel_map.containsKey(attacker.getName()) && DuelMechanics.duel_map.containsKey(hurt.getName())){
						return;
					}
					e.setCancelled(true); // Friendly fire is OFF.
					e.setDamage(0);
				}
			}
		}

		if(e.getCause() == DamageCause.PROJECTILE){
			EntityDamageByEntityEvent edbee = (EntityDamageByEntityEvent)e;

			if(e.getEntity() instanceof Player && edbee.getDamager() instanceof Arrow){
				Arrow a = (Arrow)edbee.getDamager();
				if(a.getShooter() instanceof Player){
					Player shooter = (Player)a.getShooter();
					Player hurt = (Player)e.getEntity();

					if(arePartyMembers(shooter.getName(), hurt.getName())){
						if(DuelMechanics.duel_map.containsKey(shooter.getName()) && DuelMechanics.duel_map.containsKey(hurt.getName())){
							return;
						}
						e.setCancelled(true); // Friendly fire is OFF.
						e.setDamage(0);
					}
				}
			}
		}

	}

	public static void sendPartyColor(Player p_to_send, Player p_viewer, boolean in_party){
		if(p_to_send.getName().equalsIgnoreCase(p_viewer.getName())){
			return;
		}

		ChatColor c = null;
		c = ChatColor.LIGHT_PURPLE;

		if(in_party == false){
			c = ChatColor.WHITE;
		}

		if(p_to_send.isOp()){
			c = ChatColor.AQUA;
			CommunityMechanics.setColor(p_to_send, c);
			return;
		}

		String r_name = p_to_send.getName();

		EntityPlayer ent_p_edited = ((CraftPlayer) p_to_send).getHandle();
		net.minecraft.server.v1_7_R1.ItemStack boots = null, legs = null, chest = null, head = null;

		try{
			if(ent_p_edited.getEquipment(1) != null){
				boots = ent_p_edited.getEquipment(1);
			}
			if(ent_p_edited.getEquipment(2) != null){
				legs = ent_p_edited.getEquipment(2);
			}
			if(ent_p_edited.getEquipment(3) != null){
				chest = ent_p_edited.getEquipment(3);
			}
			if(ent_p_edited.getEquipment(4) != null){
				head = ent_p_edited.getEquipment(4);
			}
		} catch(Exception e){
			e.printStackTrace();
		}

		if(c == ChatColor.WHITE){
			ent_p_edited.displayName = ChatColor.stripColor(p_to_send.getName());
			// No color.
		}
		else{
			ent_p_edited.displayName = c.toString() + ChatColor.stripColor(p_to_send.getName());
		}

		((CraftPlayer) p_viewer).getHandle().playerConnection.sendPacket(new PacketPlayOutNamedEntitySpawn(ent_p_edited));

		List<Packet> pack_list = new ArrayList<Packet>();
		if(boots != null){
			pack_list.add(new PacketPlayOutEntityEquipment(ent_p_edited.getId(), 1, boots));
		}
		if(legs != null){
			pack_list.add(new PacketPlayOutEntityEquipment(ent_p_edited.getId(), 2, legs));
		}
		if(chest != null){
			pack_list.add(new PacketPlayOutEntityEquipment(ent_p_edited.getId(), 3, chest));
		}
		if(head != null){
			pack_list.add(new PacketPlayOutEntityEquipment(ent_p_edited.getId(), 4, head));
		}

		for(Packet pa : pack_list){
			((CraftPlayer) p_viewer).getHandle().playerConnection.sendPacket(pa);
		}

		ent_p_edited.displayName = ChatColor.stripColor(r_name);
	}

	public static boolean arePartyMembers(String p1, String p2){
		if(inv_party_map.containsKey(p1) && inv_party_map.containsKey(p2)){
			if(inv_party_map.get(p1).equalsIgnoreCase(inv_party_map.get(p2))){
				return true;
			}
		}
		return false;
	}

	public static int getPartyCount(String party_name){
		if(!(party_map.containsKey(party_name))){
			return 0;
		}
		return party_map.get(party_name).size();
	}

	public static List<String> getPartyMembers(String player_name){
		if(!(inv_party_map.containsKey(player_name))){
			// No party.
			return new ArrayList<String>(Arrays.asList(player_name));
		}
		String party_name = inv_party_map.get(player_name);
		if(!(party_map.containsKey(party_name))){
			return new ArrayList<String>(Arrays.asList(player_name));
		}

		if(party_map.get(party_name) == null || party_map.get(party_name).size() <= 0){
			return new ArrayList<String>(Arrays.asList(player_name));
		}

		return party_map.get(party_name);
	}

	public static boolean hasParty(String p_name){
		if(!(inv_party_map.containsKey(p_name))){
			return false;
		}
		return true;
	}

	// AnEpicPlayerName
	public static void addPlayerToParty(final Player pl, String party_title){
		if(party_title.contains(".")){
			party_title = party_title.substring(0, party_title.indexOf("."));
		}
		party_invite.remove(pl.getName());
		party_invite_time.remove(pl.getName());

		List<String> party_members = party_map.get(party_title);
		party_members.add(pl.getName());
		party_map.put(party_title, party_members);
		inv_party_map.put(pl.getName(), party_title);

		String p_name = pl.getName();
		if(p_name.length() > 14){
			p_name = p_name.substring(0, 14);
		}

		Scoreboard party_ui = Bukkit.getPlayer(party_title).getScoreboard();
		Objective obj = party_ui.getObjective(DisplaySlot.SIDEBAR);
		Score hp = obj.getScore(Bukkit.getOfflinePlayer(ChatColor.stripColor(p_name))); 
		hp.setScore(HealthMechanics.getPlayerHP(pl.getName()));
		pl.setScoreboard(party_ui);
		/*party_ui.setItem(ChatColor.stripColor(p_name), HealthMechanics.getPlayerHP(pl.getName()), false);
		party_ui.showToPlayer(pl);*/

		int party_count = getPartyCount(party_title);

		for(String s : party_members){
			if(s.equalsIgnoreCase(pl.getName())){
				continue;
			}
			if(Bukkit.getPlayer(s) == null){
				continue;
			}
			final Player p_mem = Bukkit.getPlayer(s);
			if(party_count == 4){
				p_mem.sendMessage(ChatColor.GRAY + "You now have " + ChatColor.BOLD + "4/8" + ChatColor.GRAY + " party members. You will now recieve increased drop rates when fighting together.");
			}
			if(party_count == 8){
				p_mem.sendMessage(ChatColor.GRAY + "You now have " + ChatColor.BOLD + "8/8" + ChatColor.GRAY + " party members. You will now recieve +5% DMG/ARMOR AND " + ChatColor.UNDERLINE + "GREATLY" + ChatColor.GRAY + " increased drop rates when fighting together.");
			}
			
			if(Bukkit.getPlayer(s) != null){
				Bukkit.getPlayer(s).setScoreboard(party_ui);
			}
			/*this.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
				public void run() {
					sendPartyColor(pl, p_mem, true);
				}
			}, 20L);*/
		}
	}

	public static void removePlayerFromParty(Player pl, String party_name){
		// TODO: Fix this with the new party names (16 char bullshit)

		/*int incr = 0;
		if(party_name_incr.containsKey(party_name)){
			incr = party_name_incr.get(party_name);
		}

		if(party_name.length() > 13){
			party_name = party_name.substring(0, 13);
		}

		party_name += "." + incr;*/

		if(!(party_map.containsKey(party_name))){
			log.info("[PartyMechanics] No party found for: " + party_name);
			return;
		}

		List<String> party_members = party_map.get(party_name);
		party_members.remove(pl.getName());
		party_map.put(party_name, party_members);
		inv_party_map.remove(pl.getName());
		party_only.remove(pl.getName());
		KarmaMechanics.sendAlignColor(pl, pl);

		Scoreboard party_ui = Bukkit.getPlayer(party_name).getScoreboard();
		Objective obj = party_ui.getObjective(DisplaySlot.SIDEBAR);
		if(obj != null){
			Score hp = obj.getScore(Bukkit.getOfflinePlayer(pl.getName()));
			hp.setScore(0);
		}
		
		/*Scoreboard party_ui = api.getScoreboard(getPartyTitle(party_name));
		party_ui.removeItem(pl.getName());
		party_ui.removeItem(ChatColor.BOLD.toString() + pl.getName());
		party_ui.showToPlayer(pl, false);*/

		InstanceMechanics.teleport_on_load.remove(pl.getName());

		if(InstanceMechanics.saved_location_instance.containsKey(pl.getName())){
			// Inside an instance.
			pl.teleport(InstanceMechanics.saved_location_instance.get(pl.getName()));
			InstanceMechanics.saved_location_instance.remove(pl.getName());
		}
		InstanceMechanics.removeFromInstanceParty(pl.getName());

		if(pl.getName().equalsIgnoreCase(party_name) && party_map.get(party_name).size() > 0){
			// TODO: Disband party. Assign new leader.
			String new_leader = "";
			int size_mod = 1;
			if(party_map.get(party_name).size() <= 1){
				size_mod = 0;
			}
			int party_index = new Random().nextInt(party_map.get(party_name).size() - size_mod);
			List<String> remaining_members = new ArrayList<String>();
			for(String s : party_map.get(party_name)){
				if(s.equalsIgnoreCase(pl.getName())){
					continue;
				}
				remaining_members.add(s);
			}
			new_leader = remaining_members.get(party_index);
			party_map.remove(pl.getName());

			try{
				for(OfflinePlayer mem : pl.getScoreboard().getPlayers()){
					if(mem.isOnline()){
						Bukkit.getPlayer(mem.getName()).setScoreboard(manager.getNewScoreboard());
					}
					//api.getScoreboard(getPartyTitle(pl.getName())).showToPlayer(mem, false);
				}

				//api.getScoreboard(getPartyTitle(pl.getName())).stopShowingAllPlayers();
			} catch(Exception err){
				err.printStackTrace();
				//api.getScoreboard(getPartyTitle(pl.getName())).stopShowingAllPlayers();
			}
			//api.getScoreboards().remove(api.getScoreboard(getPartyTitle(pl.getName())));

			createParty(new_leader, Bukkit.getPlayer(new_leader), remaining_members);
			//Scoreboard new_ui = Bukkit.getPlayer(new_leader).getScoreboard();

			for(String s : remaining_members){ 
				inv_party_map.put(s, new_leader);
				if(Bukkit.getPlayer(s) != null){
					Player pty_mem = Bukkit.getPlayer(s);
					if(!(pty_mem.getScoreboard().getPlayers().contains(Bukkit.getOfflinePlayer(s)))){
						pty_mem.setScoreboard(Bukkit.getPlayer(s).getScoreboard());
					}
					/*if(!new_ui.hasPlayerAdded(pty_mem)){
						new_ui.showToPlayer(pty_mem);
					}*/
					pty_mem.sendMessage(ChatColor.LIGHT_PURPLE.toString() + "<" + ChatColor.BOLD + "P" + ChatColor.LIGHT_PURPLE + ">" + ChatColor.GRAY + " " + pl.getName() + ChatColor.GRAY.toString() + " has " + ChatColor.LIGHT_PURPLE + ChatColor.UNDERLINE + "left" + ChatColor.GRAY.toString() + " your party.");
					pty_mem.sendMessage(ChatColor.LIGHT_PURPLE.toString() + "<" + ChatColor.BOLD + "P" + ChatColor.LIGHT_PURPLE + "> " + ChatColor.GRAY + ChatColor.LIGHT_PURPLE.toString() + new_leader + ChatColor.GRAY.toString() + " has been promoted to " + ChatColor.UNDERLINE + "Party Leader");
				}
			}
		}
		else{
			for(String s : party_map.get(party_name)){
				if(Bukkit.getPlayer(s) != null && s != pl.getName()){
					Player pty_mem = Bukkit.getPlayer(s);
					pty_mem.sendMessage(ChatColor.LIGHT_PURPLE.toString() + "<" + ChatColor.BOLD + "P" + ChatColor.LIGHT_PURPLE + ">" + ChatColor.GRAY + " " + pl.getName() + ChatColor.GRAY.toString() + " has " + ChatColor.RED + ChatColor.UNDERLINE + "left" + ChatColor.GRAY.toString() + " your party.");
				}
			}
		}

		if(!Hive.pending_upload.contains(pl.getName())){
			HealthMechanics.setOverheadHP(pl, HealthMechanics.getPlayerHP(pl.getName()));
		}

	}

	public String getPartyTitle(String party_owner){
		if(party_owner.length() > 13){
			party_owner = party_owner.substring(0, 13);
		}
		if(party_name_incr.containsKey(party_owner)){
			return party_owner + "." + party_name_incr.get(party_owner);
		}
		return party_owner;
	}

	public static void createParty(String party_title, Player p_owner, List<String> existing_members){
		if(!p_owner.getName().equalsIgnoreCase("Notch")){
			p_owner.sendMessage(ChatColor.RED + "Parties are temporarily disabled due to a 1.7.2 conflict.");
		//	return;
		}
		
		int incr = 0;

		String p_owner_name = p_owner.getName();

		if(p_owner.getName().length() > 13){
			p_owner_name = p_owner.getName().substring(0, 13);
		}

		if(party_name_incr.containsKey(p_owner_name)){
			incr = party_name_incr.get(p_owner_name);
		}
		incr = incr + 1;
		party_name_incr.put(p_owner_name, incr);
		party_title = p_owner_name + "." + incr;

		Scoreboard party_ui = manager.getNewScoreboard();
		Objective obj = party_ui.getObjective("player_data");
		if(obj == null) obj = party_ui.registerNewObjective("player_data", "dummy");
		obj.setDisplayName(ChatColor.RED.toString() + ChatColor.BOLD.toString() + "Party");
		obj.setDisplaySlot(DisplaySlot.SIDEBAR);
		
		/*party_ui.setType(Scoreboard.Type.SIDEBAR);
		party_ui.setScoreboardName(ChatColor.RED.toString() + ChatColor.BOLD.toString() + "Party");*/

		Score hp = obj.getScore(Bukkit.getOfflinePlayer(ChatColor.BOLD + p_owner_name));
		hp.setScore(HealthMechanics.getPlayerHP(p_owner.getName()));
		//party_ui.setItem(ChatColor.BOLD + p_owner_name, HealthMechanics.getPlayerHP(p_owner.getName()), false);
		//party_ui.showToPlayer(p_owner);
		p_owner.setScoreboard(party_ui);
		
		if(existing_members == null){
			party_map.put(p_owner.getName(), new ArrayList<String>(Arrays.asList(p_owner.getName())));
			party_loot.put(p_owner.getName(), "random");
			party_loot_index.put(p_owner.getName(), 0);
		}
		else{
			party_map.put(p_owner.getName(), existing_members);
			for(String s : existing_members){
				if(p_owner.getName().equalsIgnoreCase(s)){
					continue; // Prevents duplicates.
				}
				if(Bukkit.getPlayer(s) != null){
					// Set them in menu.
					Player pl = Bukkit.getPlayer(s);
					String p_name = pl.getName();
					if(p_name.length() > 14){
						p_name = p_name.substring(0, 14);
					}
					
					hp = obj.getScore(Bukkit.getOfflinePlayer(p_name));
					hp.setScore(HealthMechanics.getPlayerHP(p_owner.getName()));
					//party_ui.setItem(p_name, HealthMechanics.getPlayerHP(pl.getName()), false);
				}
			}

			// TODO: Make this inherent old loot style
			party_loot.put(p_owner.getName(), "random");
			party_loot_index.put(p_owner.getName(), 0);
			//sendRawMessageToParty(p_owner.getName(), "The loot profile of this party has been set to: " + ChatColor.LIGHT_PURPLE + "RANDOM");
		}

		inv_party_map.put(p_owner.getName(), p_owner.getName());
	}

	public static void inviteToParty(Player to_invite, Player p_owner){
		if(!party_map.containsKey(p_owner)) party_map.put(p_owner.getName(), new ArrayList<String>());
		if(!(isPartyLeader(p_owner.getName()))){
			if(inv_party_map.containsKey(p_owner.getName())){ // In another party.
				p_owner.sendMessage(ChatColor.RED.toString() + "You are NOT the leader of your party.");
				p_owner.sendMessage(ChatColor.GRAY.toString() + "Type " + ChatColor.BOLD.toString() + "/pquit" + ChatColor.GRAY + " to quit your current party.");
				return;
			}
			else{ // No party.
				createParty(p_owner.getName(), p_owner, null);
				p_owner.sendMessage(ChatColor.GREEN.toString() + ChatColor.BOLD + "Party created.");
				p_owner.sendMessage(ChatColor.GRAY.toString() + "To invite more people to join your party, " + ChatColor.UNDERLINE + "Left Click" + ChatColor.GRAY.toString() + " them with your character journal or use " 
						+ ChatColor.BOLD + "/pinvite" + ChatColor.GRAY + ". To kick, use " +ChatColor.BOLD + "/pkick" + ChatColor.GRAY + ". To chat with party, use " + ChatColor.BOLD + "/p"  + ChatColor.GRAY + " To change the loot profile, use " + ChatColor.BOLD + "/ploot");
			}
		}

		if(party_map.get(p_owner.getName()).size() >= 8){
			p_owner.sendMessage(ChatColor.RED + "You cannot have more than " + ChatColor.ITALIC + "8 players" + ChatColor.RED + " in a party.");
			p_owner.sendMessage(ChatColor.GRAY + "You may use /pkick to kick out unwanted members.");
		}

		if(inv_party_map.containsKey(to_invite.getName())){
			if(inv_party_map.get(to_invite.getName()).equalsIgnoreCase(p_owner.getName())){
				p_owner.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + to_invite.getName() + ChatColor.RED + " is already in your party.");
				p_owner.sendMessage(ChatColor.GRAY + "Type /pkick " + to_invite.getName() + " to kick them out.");
			}
			else{
				p_owner.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + to_invite.getName() + ChatColor.RED + " is already in another party.");
			}
			return;
		}
		if(party_invite.containsKey(to_invite.getName())){
			p_owner.sendMessage(ChatColor.RED + to_invite.getName() + " has a pending party invite.");
			return;
		}

		to_invite.sendMessage(ChatColor.LIGHT_PURPLE.toString() + ChatColor.UNDERLINE + p_owner.getName() + ChatColor.GRAY + " has invited you to join their party. To accept, type " + ChatColor.LIGHT_PURPLE.toString() + "/paccept" + ChatColor.GRAY + " or to decline, type " + ChatColor.LIGHT_PURPLE.toString() + "/pdecline");
		p_owner.sendMessage(ChatColor.GRAY + "You have invited " + ChatColor.LIGHT_PURPLE.toString() + to_invite.getName() + ChatColor.GRAY + " to join your party.");

		party_invite.put(to_invite.getName(), p_owner.getName());
		party_invite_time.put(to_invite.getName(), System.currentTimeMillis());
	}

	public static boolean isPartyLeader(String p_name){
		if(inv_party_map.containsKey(p_name) && inv_party_map.get(p_name).equalsIgnoreCase(p_name)){
			return true;
		}
		return false;
	}

	public static int getPlayersInArea(String party, Location center, double radius){
		List<String> mem_list = party_map.get(party);
		int count = 0;
		double rad_sqr = Math.pow(radius, 2);

		for(String mem : mem_list){
			if(Bukkit.getPlayer(mem) != null){
				Player p_mem = Bukkit.getPlayer(mem);
				if(!p_mem.getWorld().getName().equalsIgnoreCase(center.getWorld().getName())){
					continue;
				}
				Location p_mem_loc = p_mem.getLocation();
				if(p_mem_loc.distanceSquared(center) <= rad_sqr){
					count++;
					continue;
				}
			}
		}	
		return count;
	}

	public static void sendMessageToParty(Player sender, String raw_msg){
		List<Player> to_send = new ArrayList<Player>();

		if(party_map.get(inv_party_map.get(sender.getName())) == null){
			return;
		}

		for(String mem : party_map.get(inv_party_map.get(sender.getName()))){
			if(Bukkit.getPlayer(mem) != null){
				to_send.add(Bukkit.getPlayer(mem));
			}
		}

		for(Player pl : to_send){
			ChatColor p_color = ChatMechanics.getPlayerColor(sender, pl);
			String prefix = ChatMechanics.getPlayerPrefix(sender.getName(), true);

			String personal_msg = raw_msg;
			if(ChatMechanics.hasAdultFilter(pl.getName())){
				personal_msg = ChatMechanics.censorMessage(personal_msg);
			}

			if(personal_msg.endsWith(" ")){
				personal_msg = personal_msg.substring(0, personal_msg.length() - 1);
			}

			personal_msg = ChatMechanics.fixCapsLock(personal_msg);

			pl.sendMessage(ChatColor.LIGHT_PURPLE.toString() + "<" + ChatColor.BOLD + "P" + ChatColor.LIGHT_PURPLE + ">" + ChatColor.GRAY + " " + prefix + p_color + sender.getName() + ": " + ChatColor.GRAY + personal_msg);
		}

		log.info("<P> " + sender.getName() + ": " + raw_msg);
	}

	public static void sendRawMessageToParty(String party_leader, String raw_msg){
		List<Player> to_send = new ArrayList<Player>();

		if(party_map.get(inv_party_map.get(party_leader)) == null){
			log.info("[PartyMechanics] Null exception for " + party_leader + " -> " + inv_party_map.get(party_leader));
			return;
		}

		for(String mem : getPartyMembers(party_leader)){
			if(mem == null){
				continue;
			}
			if(Bukkit.getPlayer(mem) != null){
				to_send.add(Bukkit.getPlayer(mem));
			}
		}

		for(Player pl : to_send){
			if(pl.getName().equalsIgnoreCase(party_leader)){
				continue;
			}
			String personal_msg = raw_msg;
			if(ChatMechanics.hasAdultFilter(pl.getName())){
				personal_msg = ChatMechanics.censorMessage(personal_msg);
			}

			if(personal_msg.endsWith(" ")){
				personal_msg = personal_msg.substring(0, personal_msg.length() - 1);
			}

			personal_msg = ChatMechanics.fixCapsLock(personal_msg);

			pl.sendMessage(ChatColor.LIGHT_PURPLE.toString() + "<" + ChatColor.BOLD + "P" + ChatColor.LIGHT_PURPLE + ">" + ChatColor.GRAY + " " + personal_msg);
		}
		
		if(Bukkit.getPlayer(party_leader) != null){
			Bukkit.getPlayer(party_leader).sendMessage(ChatColor.LIGHT_PURPLE.toString() + "<" + ChatColor.BOLD + "P" + ChatColor.LIGHT_PURPLE + ">" + ChatColor.GRAY + " " + raw_msg);
		}

		log.info("<P> " + raw_msg);
	}

}
