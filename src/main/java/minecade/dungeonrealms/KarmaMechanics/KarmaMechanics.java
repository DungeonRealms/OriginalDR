package minecade.dungeonrealms.KarmaMechanics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import minecade.dungeonrealms.Main;
import minecade.dungeonrealms.AchievementMechanics.AchievementMechanics;
import minecade.dungeonrealms.CommunityMechanics.CommunityMechanics;
import minecade.dungeonrealms.DuelMechanics.DuelMechanics;
import minecade.dungeonrealms.HealthMechanics.HealthMechanics;
import minecade.dungeonrealms.Hive.Hive;
import minecade.dungeonrealms.ItemMechanics.ItemMechanics;
import minecade.dungeonrealms.MountMechanics.MountMechanics;
import minecade.dungeonrealms.PetMechanics.PetMechanics;
import minecade.dungeonrealms.ProfessionMechanics.ProfessionMechanics;
import minecade.dungeonrealms.RecordMechanics.RecordMechanics;
import minecade.dungeonrealms.RepairMechanics.RepairMechanics;
import minecade.dungeonrealms.managers.PlayerManager;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

public class KarmaMechanics implements Listener {
	static Logger log = Logger.getLogger("Minecraft");

	public static HashMap<String, String> align_map = new HashMap<String, String>();
	// Player_name, simple version of alignment: (good, neutral, evil)

	public static ConcurrentHashMap<String, Integer> align_time = new ConcurrentHashMap<String, Integer>();
	// Seconds until alignment expires.

	public static HashMap<String, String> plast_hit = new HashMap<String, String>();
	// PLAYER_NAME, LAST DAMAGER_NAME

	public static HashMap<String, Long> last_hit_time = new HashMap<String, Long>();
	// Last time a player was hit in LONG format.

	public static HashMap<String, Long> last_attack_time = new HashMap<String, Long>();
	// Last time a player attacked another player.

	public static HashMap<String, List<ItemStack>> saved_gear = new HashMap<String, List<ItemStack>>();
	// Used on player death event to ensure gear is safe on respawn.
	// TODO: Make this cross-server

	public static HashMap<String, String> lost_gear = new HashMap<String, String>();
	// Used to prevent dupe from neutral deaths not coordinating correct lost items.
	// ',' delim list of slot #'s of armor/weapon that was dropped.

	public static HashMap<String, Location> saved_location = new HashMap<String, Location>();
	// Location to TP players back to on asyncmove event.

	public static List<Location> evil_spawns = new ArrayList<Location>();

	static KarmaMechanics instance = null;

	public void onEnable() {
		instance = this;
		Main.plugin.getServer().getPluginManager().registerEvents(this, Main.plugin);

		evil_spawns.add(new Location(Bukkit.getWorlds().get(0), -382, 68, 867));
		evil_spawns.add(new Location(Bukkit.getWorlds().get(0), -350, 67, 883));
		evil_spawns.add(new Location(Bukkit.getWorlds().get(0), -330, 65, 898));
		evil_spawns.add(new Location(Bukkit.getWorlds().get(0), -419, 61, 830));

		// Handles Async movement.
		Main.plugin.getServer().getScheduler().runTaskTimerAsynchronously(Main.plugin, new Runnable() {
			public void run() {
				for(Player p : Main.plugin.getServer().getOnlinePlayers()) {
					if(p == null || p.getPlayerListName().equalsIgnoreCase("")) {
						continue; // NPC.
					}

					if(!(saved_location.containsKey(p.getName()))) {
						saved_location.put(p.getName(), p.getLocation());
					}

					Location from = saved_location.get(p.getName());
					try {
						String p_align = getRawAlignment(p.getName());
						boolean pvp_off_from = DuelMechanics.isPvPDisabled(from);

						if(p_align.equalsIgnoreCase("evil") && pvp_off_from) {
							p.sendMessage(ChatColor.RED + "The guards have kicked you out of the " + ChatColor.UNDERLINE + "protected area" + ChatColor.RED + " due to your chaotic alignment.");
							int spawn = new Random().nextInt(evil_spawns.size());
							Location espawn = evil_spawns.get(spawn);
							p.teleport(espawn);
							saved_location.put(p.getName(), espawn);
							// TODO: Randomize this respawn ^
							continue;
						}

						boolean pvp_off_to = DuelMechanics.isPvPDisabled(p.getLocation());

						if(p_align.equalsIgnoreCase("evil") && pvp_off_to) {
							if(from.getWorld().getName().equalsIgnoreCase(p.getLocation().getWorld().getName())) {
								// Don't teleport them if they're changing realms, the above check will kick them out of city.
								p.teleport(from);
							} else if(!from.getWorld().getName().equalsIgnoreCase(p.getLocation().getWorld().getName())) {
								p.sendMessage(ChatColor.RED + "The guards have kicked you out of the " + ChatColor.UNDERLINE + "protected area" + ChatColor.RED + " due to your chaotic alignment.");
								int spawn = new Random().nextInt(evil_spawns.size());
								Location espawn = evil_spawns.get(spawn);
								p.teleport(espawn);
								saved_location.put(p.getName(), espawn);
								// TODO: Randomize this respawn ^
								continue;
							}
							p.sendMessage(ChatColor.RED + "You " + ChatColor.UNDERLINE + "cannot" + ChatColor.RED + " enter " + ChatColor.BOLD.toString() + "NON-PVP" + ChatColor.RED + " zones with a chaotic alignment.");
							// The player is chaotic and going into a PVP-off zone, nty.
							continue;
						}

						if((getSecondsSinceLastAttack(p.getName()) <= 10) && pvp_off_to && !(pvp_off_from)) {
							// Crossing over from chaotic -> neutral while in combat, stop them.
							if(from.getWorld().getName().equalsIgnoreCase(p.getLocation().getWorld().getName())) {
								// Don't teleport them if they're changing realms, the above check will kick them out of city.
								p.teleport(from);
							}

							long last_att = last_attack_time.get(p.getName());
							double seconds_left = ((System.currentTimeMillis() - last_att) / 1000.0D);
							int return_val = (int) ((10 - Math.round(seconds_left)));

							p.sendMessage(ChatColor.RED + "You " + ChatColor.UNDERLINE + "cannot" + ChatColor.RED + " leave a chaotic zone while in combat.");
							p.sendMessage(ChatColor.GRAY + "Out of combat in: " + ChatColor.BOLD + return_val + "s");
							continue;
						}

						saved_location.put(p.getName(), p.getLocation()); // If it reaches this point, store new location as a safe place.

					} catch(NullPointerException npe) {
						continue;
					}
				}
			}
		}, 5 * 20L, 20L);

		// Handles ticking from Evil -> Neutral -> Good
		Main.plugin.getServer().getScheduler().runTaskTimerAsynchronously(Main.plugin, new Runnable() {
			public void run() {
				List<String> to_remove = new ArrayList<String>();

				for(Entry<String, Integer> data : align_time.entrySet()) {
					String p_name = data.getKey();

					if(p_name == null) {
						continue;
					}

					int penalty = data.getValue();

					if(Bukkit.getPlayer(p_name) != null) {
						Player pl = Bukkit.getPlayer(p_name);
						if(!pl.getWorld().getName().equalsIgnoreCase(Main.plugin.getServer().getWorlds().get(0).getName())) {
							continue; // Don't reduce karma timer when they're in realm / an instance.
						}
					} else if(Bukkit.getPlayer(p_name) == null) {
						continue;
					}

					penalty -= 1; // Subtract 1 second.

					if(penalty <= 0) {
						// Time to change alignments!
						try {
							if(getRawAlignment(p_name).equalsIgnoreCase("evil")) { // Evil -> Neutral
								setAlignment(p_name, "neutral", 2);
								align_time.put(p_name, (120)); // 2m Delay until Lawful again.
								continue;
							}
							if(getRawAlignment(p_name).equalsIgnoreCase("neutral")) { // Neutral -> Good
								setAlignment(p_name, "good", 2);
								to_remove.add(p_name);
								continue;
							}
						} catch(NullPointerException npe) {
							continue;
						}
					} else if(penalty > 0) {
						align_time.put(p_name, penalty);
					}
				}

				for(String s : to_remove) {
					align_time.remove(s);
				}
			}
		}, 5 * 20L, 20L);

		log.info("[KarmaMechanics] has been enabled.");
	}

	public void onDisable() {
		log.info("[KarmaMechanics] has been disabled.");
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		final Player p = e.getPlayer();

		Main.plugin.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
			public void run() {
				if(Main.plugin.getServer().getPlayer(p.getName()) != null) {
					Player pn = Main.plugin.getServer().getPlayer(p.getName());
					sendAlignColor(pn, pn);
				}
			}
		}, 30L);

	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		plast_hit.remove(p.getName());
		saved_location.remove(p.getName());
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onEntityDamageEntity(EntityDamageByEntityEvent e) {

		Entity ent = e.getEntity();
		if(e.getEntity().getPassenger() != null) {
			ent = e.getEntity().getPassenger();
		}

		if(ent instanceof Player && !(e.getDamager() instanceof Player) && !(e.getDamager() instanceof Arrow)) { // The attacker ain't a player.
			Player attacked = (Player) ent;
			if(last_hit_time.containsKey(attacked.getName())) {
				if((System.currentTimeMillis() - last_hit_time.get(attacked.getName()) >= (6 * 1000))) {
					// It's been more than 8 seconds since they were last hit. No one is responsible.
					plast_hit.remove(attacked.getName());
					return;
				}
			}
		}

		if(e.getDamager() instanceof Player && ent instanceof Player) {
			Player attacker = (Player) e.getDamager();
			Player attacked = (Player) ent;
			if(attacker.getName().equalsIgnoreCase(attacked.getName())) { return; // Same person!
			}

			if(DuelMechanics.isPvPDisabled(attacker) || DuelMechanics.isPvPDisabled(attacked)) { return; } // No damage ever taken.
			if(e.getDamage() <= 0) { return; }

			if(DuelMechanics.duel_map.containsKey(attacker.getName()) && DuelMechanics.duel_map.get(attacker.getName()).equalsIgnoreCase(attacked.getName())) { return; // They're dueling.
			}

			last_attack_time.put(attacker.getName(), System.currentTimeMillis());

			if(HealthMechanics.noob_players.contains(attacker.getName())) {
				// Warn them.
				if(HealthMechanics.noob_player_warning.containsKey(attacker.getName())) {
					int warning_count = HealthMechanics.noob_player_warning.get(attacker.getName());
					warning_count++;
					if(warning_count > 3) {
						attacker.sendMessage("");
						attacker.sendMessage(ChatColor.RED + "You have forfeited your 'newbie protection' by engaging in active PvP.");
						attacker.playSound(attacker.getLocation(), Sound.LAVA_POP, 1F, 1F);
						HealthMechanics.noob_players.remove(attacker.getName());
						HealthMechanics.noob_player_warning.remove(attacker.getName());
					} else if(warning_count <= 3) {
						attacker.sendMessage(ChatColor.RED.toString() + ChatColor.UNDERLINE.toString() + "WARNING " + warning_count + "/3: " + ChatColor.RED.toString() + "If you engage in PvP with another player, you will FORFEIT your 'newbie protection' status.");
						HealthMechanics.noob_player_warning.put(attacker.getName(), warning_count);
					}
				} else {
					attacker.sendMessage(ChatColor.RED.toString() + ChatColor.UNDERLINE.toString() + "WARNING 0/3: " + ChatColor.RED.toString() + "If you engage in PvP with another player, you will FORFEIT your 'newbie protection' status.");
					HealthMechanics.noob_player_warning.put(attacker.getName(), 0);
				}
			}

			if(getRawAlignment(attacker.getName()).equalsIgnoreCase("good")) { // They were good until now!
				if(PlayerManager.getPlayerModel(attacker).getToggleList() != null){
					if(PlayerManager.getPlayerModel(attacker).getToggleList().contains("pvp")){
						e.setCancelled(true);
						e.setDamage(0);
						attacker.sendMessage(ChatColor.RED + "You " + ChatColor.UNDERLINE + "cannot" + ChatColor.RED + " deal damage to other players while you have /togglepvp on.");
						attacker.sendMessage(ChatColor.GRAY + "Use /togglechaos to access the previous functionality of togglepvp.");
						return;
					}
				}
				setAlignment(attacker.getName(), "neutral", 2);
				align_time.put(attacker.getName(), (120)); // Expires in 2 minutes from last hit.
			}
			if(getRawAlignment(attacker.getName()).equalsIgnoreCase("neutral")) { // update the timer.
				align_time.put(attacker.getName(), (120)); // Expires in 2 minutes from last hit.
			}

			last_hit_time.put(attacked.getName(), System.currentTimeMillis());
			plast_hit.put(attacked.getName(), attacker.getName()); // Store who the last player to hurt this person was.

		}

		if(e.getDamager().getType() == EntityType.ARROW && ent instanceof Player) {
			Arrow a = (Arrow) e.getDamager();
			if(a.getShooter() instanceof Player) {
				Player attacker = (Player) a.getShooter();
				Player attacked = (Player) ent;
				if(attacker.getName().equalsIgnoreCase(attacked.getName())) { return; // Same person!
				}
				if(DuelMechanics.isPvPDisabled(attacker) || DuelMechanics.isPvPDisabled(attacked)) { return; } // No damage ever taken.
				if(e.getDamage() <= 0) { return; }
				if(DuelMechanics.duel_map.containsKey(attacker.getName()) && DuelMechanics.duel_map.get(attacker.getName()).equalsIgnoreCase(attacked.getName())) { return; // They're dueling.
				}
				last_attack_time.put(attacker.getName(), System.currentTimeMillis());

				if(getRawAlignment(attacker.getName()).equalsIgnoreCase("good")) { // They were good until now!
					if(PlayerManager.getPlayerModel(attacker).getToggleList() != null){
						if(PlayerManager.getPlayerModel(attacker).getToggleList().contains("pvp")){
							e.setCancelled(true);
							e.setDamage(0);
							attacker.sendMessage(ChatColor.RED + "You " + ChatColor.UNDERLINE + "cannot" + ChatColor.RED + " deal damage to other players while you have /togglepvp on.");
							attacker.sendMessage(ChatColor.GRAY + "Use /togglechaos to access the previous functionality of togglepvp.");
							return;
						}
					}
					setAlignment(attacker.getName(), "neutral", 2);
					align_time.put(attacker.getName(), (120)); // Expires in 2 minutes from last hit.
				}
				if(getRawAlignment(attacker.getName()).equalsIgnoreCase("neutral")) { // update the timer.
					align_time.put(attacker.getName(), (120)); // Expires in 2 minutes from last hit.
				}

				last_hit_time.put(attacked.getName(), System.currentTimeMillis());
				plast_hit.put(attacked.getName(), attacker.getName()); // The attacker killed the other player. FUCK!
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerKilled(PlayerDeathEvent e) {
		if(e.getEntity() instanceof Player) {
			final Player p = (Player) e.getEntity();
			boolean impossible_kill = false;
			for(ItemStack items : new ArrayList<ItemStack>(e.getDrops())){
			    if(ItemMechanics.isSoulbound(items)){
			        //Remove all soulbound items.
			        e.getDrops().remove(items);
			    }
			}
			if(saved_gear.containsKey(p.getName())) {
				// They already have saved gear, so they shouldn't drop anything, logging in after a while.
				e.getDrops().clear();
				return;
			}
			if(HealthMechanics.last_hit_location.containsKey(p.getName()) && DuelMechanics.isDamageDisabled(HealthMechanics.last_hit_location.get(p.getName()))) {
				impossible_kill = true;
			}

			List<ItemStack> saved_gear = new ArrayList<ItemStack>();
			String align = getRawAlignment(p.getName());

			if(Bukkit.getMotd().toLowerCase().contains("developer server")) {
				e.getDrops().clear();
				return; // No loot on US-0
			}

			if(align == null) { return; } // NPC death, not important.

			boolean neutral_boots = false, neutral_legs = false, neutral_chest = false, neutral_helmet = false, neutral_weapon = false;
			if(HealthMechanics.combat_logger.contains(p.getName()) && align.equalsIgnoreCase("neutral")) {
				// So we need to know what is dropped  and what isn't ... can't we just use saved_gear and then detect they're a combat logger?
				// No we can't, because saved_gear is AFTER death, we need a different column for 'lost gear', store 0, 1, 2, 3, 4 for weapon, helmet, chest, legs, boots
				// Then when players log in, check lost_gear, and take away any armor pieces in that column. guuci.
				//align = "evil";
				if(lost_gear.containsKey(p.getName())) {
					for(String index : lost_gear.get(p.getName()).split(",")) {
						if(index.equalsIgnoreCase("0")) {
							// Remove weapon from drop list, void it.
							if(!ItemMechanics.getDamageData(p.getInventory().getItem(0)).equalsIgnoreCase("no")) {
								e.getDrops().remove(p.getInventory().getItem(0));
								p.getInventory().setItem(0, new ItemStack(Material.AIR));
							}
						}
						if(index.equalsIgnoreCase("1")) {
							e.getDrops().remove(p.getInventory().getBoots());
							p.getInventory().setBoots(new ItemStack(Material.AIR));
						}
						if(index.equalsIgnoreCase("2")) {
							e.getDrops().remove(p.getInventory().getLeggings());
							p.getInventory().setLeggings(new ItemStack(Material.AIR));
						}
						if(index.equalsIgnoreCase("3")) {
							e.getDrops().remove(p.getInventory().getChestplate());
							p.getInventory().setChestplate(new ItemStack(Material.AIR));
						}
						if(index.equalsIgnoreCase("4")) {
							e.getDrops().remove(p.getInventory().getHelmet());
							p.getInventory().setHelmet(new ItemStack(Material.AIR));
						}
					}
					lost_gear.remove(p.getName());
					Hive.sql_query.add("INSERT INTO player_database(p_name, lost_gear) VALUES('" + p.getName() + "', '') ON DUPLICATE KEY UPDATE lost_gear=''");
				}
			}
			if(align.equalsIgnoreCase("neutral") && !HealthMechanics.combat_logger.contains(p.getName())) {
				// 50% of weapon dropping, 25% for every piece of equipped armor.
				if(new Random().nextInt(100) <= 50) {
					neutral_weapon = true;
				}

				if(new Random().nextInt(100) <= 25) {
					int index = new Random().nextInt(4);
					if(index == 0) {
						neutral_boots = true;
					}
					if(index == 1) {
						neutral_legs = true;
					}
					if(index == 2) {
						neutral_chest = true;
					}
					if(index == 3) {
						neutral_helmet = true;
					}
				}
			}

			if(impossible_kill == false && (align.equalsIgnoreCase("good") || align.equalsIgnoreCase("neutral"))) { // Damage armor, but keep it.
				double durability_to_take = (RepairMechanics.blocks_per_armor * 0.30D); // 30%
				double w_durability_to_take = (RepairMechanics.hits_per_weapon * 0.30D);

				if(!neutral_boots && p.getInventory().getBoots() != null && p.getInventory().getBoots().getType() != Material.AIR) {
					ItemStack boots = p.getInventory().getBoots();
					e.getDrops().remove(boots);
					p.getInventory().setBoots(new ItemStack(Material.AIR));
					//e.getDrops().remove(boots);
					if((RepairMechanics.getCustomDurability(boots, "armor") - durability_to_take) > 0.1D) {
						RepairMechanics.subtractCustomDurability(p, boots, durability_to_take, "armor");
						saved_gear.add(boots);
					}
				}
				if(!neutral_legs && p.getInventory().getLeggings() != null && p.getInventory().getLeggings().getType() != Material.AIR) {
					ItemStack leggings = p.getInventory().getLeggings();
					e.getDrops().remove(leggings);
					p.getInventory().setLeggings(new ItemStack(Material.AIR));
					//e.getDrops().remove(leggings);
					if((RepairMechanics.getCustomDurability(leggings, "armor") - durability_to_take) > 0.1D) {
						RepairMechanics.subtractCustomDurability(p, leggings, durability_to_take, "armor");
						saved_gear.add(leggings);
					}
				}
				if(!neutral_chest && p.getInventory().getChestplate() != null && p.getInventory().getChestplate().getType() != Material.AIR) {
					ItemStack chestplate = p.getInventory().getChestplate();
					e.getDrops().remove(chestplate);
					p.getInventory().setChestplate(new ItemStack(Material.AIR));
					//e.getDrops().remove(chestplate);
					if((RepairMechanics.getCustomDurability(chestplate, "armor") - durability_to_take) > 0.1D) {
						RepairMechanics.subtractCustomDurability(p, chestplate, durability_to_take, "armor");
						saved_gear.add(chestplate);
					}
				}
				if(!neutral_helmet && p.getInventory().getHelmet() != null && p.getInventory().getHelmet().getType() != Material.AIR) {
					ItemStack helmet = p.getInventory().getHelmet();
					e.getDrops().remove(helmet);
					p.getInventory().setHelmet(new ItemStack(Material.AIR));
					//e.getDrops().remove(helmet);
					if((RepairMechanics.getCustomDurability(helmet, "armor") - durability_to_take) > 0.1D) {
						RepairMechanics.subtractCustomDurability(p, helmet, durability_to_take, "armor");
						saved_gear.add(helmet);
					}
				}

				List<ItemStack> drop_copy = new ArrayList<ItemStack>(e.getDrops());

				for(ItemStack is : drop_copy) {
					if(ProfessionMechanics.isSkillItem(is)) {
						e.getDrops().remove(is);
						if((RepairMechanics.getCustomDurability(is, "wep") - w_durability_to_take) > 0.1D) {
							RepairMechanics.subtractCustomDurability(p, is, w_durability_to_take, "wep");
							saved_gear.add(is);
						}
					}
				}

				ItemStack weapon_slot = p.getInventory().getItem(0);
				if(!neutral_weapon && !ItemMechanics.getDamageData(weapon_slot).equalsIgnoreCase("no")) {
					e.getDrops().remove(weapon_slot);
					if((RepairMechanics.getCustomDurability(weapon_slot, "wep") - w_durability_to_take) > 0.1D) {
						RepairMechanics.subtractCustomDurability(p, weapon_slot, w_durability_to_take, "wep");
						saved_gear.add(weapon_slot);
					}
				}
			}

			if(impossible_kill == false && MountMechanics.mule_inventory.containsKey(p.getName())) {
				// Check if drops contain the mule, if so, drop the loot.
				boolean has_mule = false;
				//ItemStack mule = null;
				for(ItemStack is : e.getDrops()) {
					if(is == null) {
						continue;
					}
					if(is.getType() == Material.LEASH) { // MountMechanics.isMule(is)
						has_mule = true;
						//mule = is;
					}
					break;
				}

				if(has_mule) {
					// Drop the loots!
					for(ItemStack is : MountMechanics.mule_inventory.get(p.getName()).getContents()) {
						e.getDrops().add(is);
					}

					//final ItemStack f_mule = mule;
					Main.plugin.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
						public void run() {
							if(Main.plugin.getServer().getPlayer(p.getName()) != null) {
								log.info("Removing mule_inventory and mule_itemlist_string from player.");
								MountMechanics.mule_inventory.remove(p.getName()); //put(p.getName(), Bukkit.createInventory(null, MountMechanics.getMuleSlots(ItemMechanics.getItemTier(f_mule)), "Mobile Storage Chest"));
								MountMechanics.mule_itemlist_string.remove(p.getName());
							}
						}
					}, 4L);

				}
			}

			if(impossible_kill == true) {
				for(ItemStack is : e.getDrops()) {
					if(!(saved_gear.contains(is))) {
						saved_gear.add(is);
					}
				}
				e.getDrops().clear();
				// No items lost in safe zones.
			}

			for(ItemStack is : e.getDrops()) {
				if(PetMechanics.isPermUntradeable(is)) {
					saved_gear.add(is);
				}
			}

			for(ItemStack is : saved_gear) {
				if(e.getDrops().contains(is)) {
					e.getDrops().remove(is);
				}
			}

			if(saved_gear.size() > 0) { // We have some saved gear we need to store for when player respawns lol.
				KarmaMechanics.saved_gear.put(p.getName(), saved_gear);
			}

			if(HealthMechanics.combat_logger.contains(p.getName())) {
				e.getDrops().clear(); // Drop no items, just save some.
				MountMechanics.mule_inventory.remove(p.getName());
				MountMechanics.mule_itemlist_string.remove(p.getName());
				HealthMechanics.combat_logger.remove(p.getName());
			}
		}
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e) {
		if(e.getEntity() instanceof Player) {
			Player p = (Player) e.getEntity();
			p.closeInventory();

			if(!plast_hit.containsKey(p.getName())) { return; } // No data for who killed them.

			if(last_hit_time.containsKey(p.getName())) {
				if((System.currentTimeMillis() - last_hit_time.get(p.getName()) > (8 * 1000))) { return; // Been 8 seconds since they were last hit, so we don't need to punish anyone.
				}
			}

			String killer_name = plast_hit.get(p.getName());

			plast_hit.remove(p.getName());
			if(Bukkit.getPlayer(killer_name) != null && Bukkit.getPlayer(killer_name).isOnline()) {
				Player killer = Bukkit.getPlayer(killer_name);
				int unlawful_kills = RecordMechanics.player_kills.get(killer.getName()).get(0);
				int lawful_kills = RecordMechanics.player_kills.get(killer.getName()).get(1);

				if(killer.getWorld().getName().equalsIgnoreCase(killer.getName())) {
					AchievementMechanics.addAchievement(p.getName(), "Home Field Advantage");
				}

				if((getRawAlignment(p.getName()).equalsIgnoreCase("good") || p.getWorld().getName().equalsIgnoreCase(p.getName())) && !(killer.getWorld().getName().equalsIgnoreCase(killer.getName()))) { // The person killed was Lawful or in their home, AND the killer was NOT in their home.

					if(p.getWorld().getName().equalsIgnoreCase(p.getName())) {
						AchievementMechanics.addAchievement(p.getName(), "Knock, Knock");
					}

					if(!getRawAlignment(killer.getName()).equalsIgnoreCase("evil")) { // The killer is now going to turn chaotic.
						setAlignment(killer.getName(), "evil", 2); // Killer is now chaotic.
						unlawful_kills += 1;
					}

					if(align_time.containsKey(killer.getName())) {
						int cur_penalty = align_time.get(killer.getName());
						align_time.put(killer.getName(), (cur_penalty + (1200))); // +20 more minutes, bandit scum!
						killer.sendMessage(ChatColor.RED + "LAWFUL player slain, " + ChatColor.BOLD + "+1200s" + ChatColor.RED + " added to Chaotic timer.");
						unlawful_kills = unlawful_kills++;
					} else if(!(align_time.containsKey(killer.getName()))) {
						align_time.put(killer.getName(), (1200)); // 20 minutes in the future.
						unlawful_kills = unlawful_kills++;
					}
				}

				if(getRawAlignment(killer.getName()).equalsIgnoreCase("evil") && getRawAlignment(p.getName()).equalsIgnoreCase("neutral")) { // The person killed was neutral, killer was chaotic.
					// Add 10 minutes.
					int cur_penalty = align_time.get(killer.getName());
					align_time.put(killer.getName(), cur_penalty + (600)); // +20 more minutes, bandit scum!
					if(cur_penalty >= 3600) {
						AchievementMechanics.addAchievement(killer.getName(), "A long wait...");
					}
					killer.sendMessage(ChatColor.RED + "NEUTRAL player slain, " + ChatColor.BOLD + "+600s" + ChatColor.RED + " added to Chaotic timer.");
					unlawful_kills += 1;
				}

				if(getRawAlignment(p.getName()).equalsIgnoreCase("evil")) { // The person killed was chaotic.
					if(getRawAlignment(killer.getName()).equalsIgnoreCase("evil")) { // The killer was also evil, bandit v. bandit! Take 10 mins of penalty off.
						int cur_penalty = align_time.get(killer.getName());
						align_time.put(killer.getName(), cur_penalty - (600)); // -10 minutes, thanks for killing a bandit, bandit!
						killer.sendMessage(ChatColor.GREEN + "Chaotic player slain, " + ChatColor.BOLD + "-600s" + ChatColor.GREEN + " removed from Chatoic timer.");
						lawful_kills += 1;
						if(lawful_kills >= 5) {
							AchievementMechanics.addAchievement(killer.getName(), "Enforcer of Justice I");
							if(lawful_kills >= 10) {
								AchievementMechanics.addAchievement(killer.getName(), "Enforcer of Justice II");
								if(lawful_kills >= 25) {
									AchievementMechanics.addAchievement(killer.getName(), "Enforcer of Justice III");
								}
							}
						}
					} else {
						AchievementMechanics.addAchievement(killer.getName(), "Hero");
					}
				}

				int total_kills = unlawful_kills + lawful_kills;
				if(total_kills >= 1) {
					AchievementMechanics.addAchievement(killer.getName(), "Man Hunter I");
					if(total_kills >= 3) {
						AchievementMechanics.addAchievement(killer.getName(), "Man Hunter II");
						if(total_kills >= 5) {
							AchievementMechanics.addAchievement(killer.getName(), "Man Hunter III");
							if(total_kills >= 10) {
								AchievementMechanics.addAchievement(killer.getName(), "Man Hunter IV");
								if(total_kills >= 15) {
									AchievementMechanics.addAchievement(killer.getName(), "Man Hunter V");
									if(total_kills >= 20) {
										AchievementMechanics.addAchievement(killer.getName(), "Man Hunter VI");
									}
								}
							}
						}
					}
				}

				if(unlawful_kills > 0 && lawful_kills > 0) {
					AchievementMechanics.addAchievement(killer.getName(), "A Sinner and a Saint");
				}

				List<Integer> all_kills = new ArrayList<Integer>(Arrays.asList(unlawful_kills, lawful_kills));
				RecordMechanics.player_kills.put(killer_name, all_kills);
			}

		}
	}

	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent e) {
		final Player p = e.getPlayer();

		// If they have saved_gear, they SHOULD be dead.
		/*if(p.getLocation().distanceSquared(e.getRespawnLocation()) <= 2){
			// They're respawning on themselves, ignore.
				// Non-legit death, they won't loose any items.
			return;
		}*/

		if(saved_gear.containsKey(p.getName())) {
			List<ItemStack> l_saved_gear = saved_gear.get(p.getName());
			saved_gear.remove(p.getName());

			for(ItemStack i : l_saved_gear) {
				if(i == null || i.getType() == Material.AIR) {
					continue;
				}
				p.getInventory().setItem(p.getInventory().firstEmpty(), ItemMechanics.removeAttributes(i));
			}
		}

	}

	/*@EventHandler(ignoreCancelled = false)
	public void onPlayerMoveEvent(PlayerMoveEvent e){
		Player p = e.getPlayer(); // The person MOVING.
		if(p == null || p.getPlayerListName().equalsIgnoreCase("")){
			return; // NPC.
		}
		Location from = e.getFrom();
		try{
			String p_align = getRawAlignment(p.getName());
			boolean pvp_off_from = DuelMechanics.isPvPDisabled(from);

			if(p_align.equalsIgnoreCase("evil") && pvp_off_from){
				p.sendMessage(ChatColor.RED + "The guards have kicked you out of the " + ChatColor.UNDERLINE + "protected area" + ChatColor.RED + " due to your chaotic alignment.");
				List<Location> evil_spawns = new ArrayList<Location>();
				evil_spawns.add(new Location(Bukkit.getWorlds().get(0), -382, 68, 867));
				evil_spawns.add(new Location(Bukkit.getWorlds().get(0), -350, 67, 883));
				evil_spawns.add(new Location(Bukkit.getWorlds().get(0), -330, 65, 898));
				evil_spawns.add(new Location(Bukkit.getWorlds().get(0), -419, 61, 830));
				int spawn = new Random().nextInt(evil_spawns.size());
				p.teleport(evil_spawns.get(spawn));
				// TODO: Randomize this respawn ^
				return;
			}

			boolean pvp_off_to = DuelMechanics.isPvPDisabled(e.getTo());

			if(p_align.equalsIgnoreCase("evil") && pvp_off_to){
				e.setCancelled(true);
				p.teleport(e.getFrom());
				p.sendMessage(ChatColor.RED + "You " + ChatColor.UNDERLINE + "cannot" + ChatColor.RED + " enter " + ChatColor.BOLD.toString() + "NON-PVP" + ChatColor.RED + " zones with a chaotic alignment.");
				// The player is chaotic and going into a PVP-off zone, nty.
				return;
			}

			if((getSecondsSinceLastAttack(p.getName()) <= 10) && pvp_off_to && !(pvp_off_from)){
				// Crossing over from chaotic -> neutral while in combat, stop them.
				e.setCancelled(true);
				p.teleport(e.getFrom());

				long last_att = last_attack_time.get(p.getName());
				double seconds_left = ((System.currentTimeMillis() - last_att) / 1000.0D);
				int return_val = (int)((10 - Math.round(seconds_left)));

				p.sendMessage(ChatColor.RED + "You " + ChatColor.UNDERLINE + "cannot" + ChatColor.RED + " leave a chaotic zone while in combat.");
				p.sendMessage(ChatColor.GRAY + "Out of combat in: " + ChatColor.BOLD + return_val + "s");
			}

		} catch(NullPointerException npe){
			return;
		}
	}*/

	public int getSecondsSinceLastAttack(String p_name) {
		if(!(last_attack_time.containsKey(p_name))) { return 9999; }
		long last_att = last_attack_time.get(p_name);
		double seconds_left = ((System.currentTimeMillis() - last_att) / 1000.0D);
		int return_val = (int) Math.round(seconds_left);
		return return_val;
	}

	public static void sendAlignColor(Player p_to_send, Player p_viewer) {
		p_viewer = null; // Completely depreciated, kept for backwards compatibility. (variable)

		/*if(CommunityMechanics.isPlayerOnBuddyList(p_viewer, p_to_send.getName()) && (!(p_to_send.isOp()))){
			return; // They're buds, don't change colors.
		}
		if(PartyMechanics.arePartyMembers(p_viewer.getName(), p_to_send.getName())){
			return; // Let party plugin handle colors.
		}*/

		String align = getRawAlignment(p_to_send.getName());
		ChatColor c = null;
		if(align == null || align.equalsIgnoreCase("")) {
			align = "good";
		}

		if(align.equalsIgnoreCase("good")) {
			c = ChatColor.WHITE;
			CommunityMechanics.setColor(p_to_send, c);
			return;
		}
		if(align.equalsIgnoreCase("neutral")) {
			c = ChatColor.YELLOW;
			CommunityMechanics.setColor(p_to_send, c);
			return;
		}
		if(align.equalsIgnoreCase("evil")) {
			c = ChatColor.RED;
			CommunityMechanics.setColor(p_to_send, c);
			return;
		}

		/*EntityPlayer ent_p_edited = ((CraftPlayer) p_to_send).getHandle();
		if(ent_p_edited instanceof Player){
			HealthMechanics.setOverheadHP((Player) ent_p_edited, p_to_send.getLevel());
		}

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
			ent_p_edited.name = ChatColor.stripColor(p_to_send.getName());
			// No color.
		}
		else{
			ent_p_edited.name = c.toString() + ChatColor.stripColor(p_to_send.getName());
		}

		((CraftPlayer) p_viewer).getHandle().playerConnection.sendPacket(new Packet20NamedEntitySpawn(ent_p_edited));

		List<Packet> pack_list = new ArrayList<Packet>();
		if(boots != null){
			pack_list.add(new Packet5EntityEquipment(ent_p_edited.id, 1, boots));
		}
		if(legs != null){
			pack_list.add(new Packet5EntityEquipment(ent_p_edited.id, 2, legs));
		}
		if(chest != null){
			pack_list.add(new Packet5EntityEquipment(ent_p_edited.id, 3, chest));
		}
		if(head != null){
			pack_list.add(new Packet5EntityEquipment(ent_p_edited.id, 4, head));
		}

		for(Packet pa : pack_list){
			((CraftPlayer) p_viewer).getHandle().playerConnection.sendPacket(pa);
		}

		ent_p_edited.name = ChatColor.stripColor(r_name);*/
	}

	public static int getSecondsUntilAlignmentChange(String p_name) {
		if(!(align_time.containsKey(p_name))) { return 0; }
		return align_time.get(p_name);
		/*long cur_penalty = align_time.get(p_name);
		long cur_time = System.currentTimeMillis();

		long seconds_difference = (cur_penalty - cur_time) / 1000;
		if(seconds_difference > 9999){
			seconds_difference = 9999;
			align_time.put(p_name, (9999 * 1000));
			// If time is > 10,000 seconds, we'll set it back to 10,000.
		}
		return (int)seconds_difference;*/
	}

	public static void setAlignment(String p_name, String new_align, int echo_type) { // 0 = none, 1 = short, 2 = all
		align_map.put(p_name, new_align);
		//align_time.put(p_name, System.currentTimeMillis()); // Time of last alignment change.

		if(Bukkit.getPlayer(p_name) != null && Bukkit.getPlayer(p_name).isOnline() && !Bukkit.getPlayer(p_name).getPlayerListName().equalsIgnoreCase("")) {
			final Player p = Bukkit.getPlayer(p_name);
			sendAlignColor(p, p);
			p.playSound(p.getLocation(), Sound.ZOMBIE_INFECT, 3F, 1.2F);
			if(new_align.equalsIgnoreCase("good")) {
				if(!(p.getPlayerListName().contains(ChatColor.AQUA.toString()))) {
					String test_name = ChatColor.GRAY.toString() + ChatColor.stripColor(p.getName());
					if(test_name.length() >= 16) {
						p.setPlayerListName(test_name.substring(0, 15));
					} else {
						p.setPlayerListName(ChatColor.GRAY.toString() + p.getName());
					}
				}
				if(echo_type > 0) {
					p.sendMessage("");
					p.sendMessage(ChatColor.GREEN + "              " + "* YOU ARE NOW " + ChatColor.BOLD + " LAWFUL " + ChatColor.GREEN + "ALIGNMENT *");
					if(echo_type > 1) {
						p.sendMessage(ChatColor.GRAY + "While lawful, you will not lose any equipped armor on death, instead, all armor will lose 30% of its durability when you die. Any players who kill you while you're lawfully aligned will become chaotic.");
						p.sendMessage(ChatColor.GREEN + "              " + "* YOU ARE NOW " + ChatColor.BOLD + " LAWFUL " + ChatColor.GREEN + "ALIGNMENT *");
					}
				}
			}
			if(new_align.equalsIgnoreCase("neutral")) {
				if(!(p.getPlayerListName().contains(ChatColor.AQUA.toString()))) {
					String test_name = ChatColor.YELLOW.toString() + ChatColor.stripColor(p.getName());
					if(test_name.length() >= 16) {
						p.setPlayerListName(test_name.substring(0, 15));
					} else {
						p.setPlayerListName(ChatColor.YELLOW.toString() + p.getName());
					}
				}
				if(echo_type > 0) {
					p.sendMessage("");
					p.sendMessage(ChatColor.YELLOW + "              " + "* YOU ARE NOW " + ChatColor.BOLD + " NEUTRAL " + ChatColor.YELLOW + "ALIGNMENT *");
					if(echo_type > 1) {
						p.sendMessage(ChatColor.GRAY + "While neutral, players who kill you will not become chaotic. You have a 50% chance of dropping your weapon, and a 25% chance of dropping each piece of equipped armor on death. Neutral alignment will expire 2 minutes after last hit on player.");
						p.sendMessage(ChatColor.YELLOW + "              " + "* YOU ARE NOW " + ChatColor.BOLD + " NEUTRAL " + ChatColor.YELLOW + "ALIGNMENT *");
					}
				}
			}
			if(new_align.equalsIgnoreCase("evil")) {
				if(!(p.getPlayerListName().contains(ChatColor.AQUA.toString()))) {
					String test_name = ChatColor.RED.toString() + ChatColor.stripColor(p.getName());
					if(test_name.length() >= 16) {
						p.setPlayerListName(test_name.substring(0, 15));
					} else {
						p.setPlayerListName(ChatColor.RED.toString() + p.getName());
					}
				}
				if(echo_type > 0) {
					p.sendMessage("");
					p.sendMessage(ChatColor.RED + "              " + "* YOU ARE NOW " + ChatColor.BOLD + " CHAOTIC " + ChatColor.RED + "ALIGNMENT *");
					if(echo_type > 1) {
						p.sendMessage(ChatColor.GRAY + "While chaotic, you cannot enter any major cities or safe zones. If you are killed while chaotic, you will lose everything in your inventory. Chaotic alignment will expire 20 minutes after your last player kill.");
						p.sendMessage(ChatColor.RED + "              " + "* YOU ARE NOW " + ChatColor.BOLD + " CHAOTIC " + ChatColor.RED + "ALIGNMENT *");
					}
				}
			}
		}
	}

	public static String getRawAlignment(String p_name) {
		if(!(align_map.containsKey(p_name))) {
			align_map.put(p_name, "good");
		}
		return align_map.get(p_name);
	}

	public static String getAlignment(String p_name) {
		if(!(align_map.containsKey(p_name))) { return ChatColor.GRAY.toString() + "N/A"; }
		if(align_map.get(p_name).equalsIgnoreCase("good")) { return ChatColor.DARK_GREEN.toString() + ChatColor.UNDERLINE.toString() + "Lawful"; }
		if(align_map.get(p_name).equalsIgnoreCase("neutral")) { return ChatColor.GOLD.toString() + ChatColor.UNDERLINE.toString() + "Neutral"; }
		if(align_map.get(p_name).equalsIgnoreCase("evil")) { return ChatColor.DARK_RED.toString() + ChatColor.UNDERLINE.toString() + "Chaotic"; }

		return ChatColor.GRAY.toString() + "N/A";
	}

	public static String getAlignmentDescription(String align) {
		if(align == null || align.equalsIgnoreCase("")) { return ChatColor.ITALIC.toString() + "-30% Durability Arm/Wep on Death"; }
		if(align.equalsIgnoreCase("good")) { return ChatColor.ITALIC.toString() + "-30% Durability Arm/Wep on Death"; }
		if(align.equalsIgnoreCase("neutral")) { return ChatColor.ITALIC.toString() + "25%/50% Arm/Wep LOST on Death"; }
		if(align.equalsIgnoreCase("evil")) { return ChatColor.ITALIC.toString() + "Inventory LOST on Death"; }
		return "Error 001"; // Lol'd.
	}

}
