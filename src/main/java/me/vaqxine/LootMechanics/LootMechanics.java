package me.vaqxine.LootMechanics;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import me.vaqxine.Main;
import me.vaqxine.EnchantMechanics.EnchantMechanics;
import me.vaqxine.InstanceMechanics.InstanceMechanics;
import me.vaqxine.ItemMechanics.ItemGenerators;
import me.vaqxine.ItemMechanics.ItemMechanics;
import me.vaqxine.LootMechanics.commands.CommandHideLoot;
import me.vaqxine.LootMechanics.commands.CommandLoadLoot;
import me.vaqxine.LootMechanics.commands.CommandLoot;
import me.vaqxine.LootMechanics.commands.CommandShowLoot;
import me.vaqxine.MoneyMechanics.MoneyMechanics;
import me.vaqxine.MonsterMechanics.MonsterMechanics;
import me.vaqxine.PowerupMechanics.PowerupMechanics;
import me.vaqxine.ShopMechanics.ShopMechanics;
import me.vaqxine.TeleportationMechanics.TeleportationMechanics;
import me.vaqxine.TutorialMechanics.TutorialMechanics;
import net.minecraft.server.v1_7_R2.Packet;
import net.minecraft.server.v1_7_R2.PacketPlayOutWorldEvent;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_7_R2.CraftServer;
import org.bukkit.craftbukkit.v1_7_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_7_R2.inventory.CraftItemStack;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class LootMechanics implements Listener {
	static HashMap<String, List<String>> loot_templates = new HashMap<String, List<String>>();
	
	static HashMap<Player, Integer> loot_spawn_step = new HashMap<Player, Integer>();
	static HashMap<Player, String> loot_spawn_data = new HashMap<Player, String>();
	static HashMap<Player, Location> loot_spawn_location = new HashMap<Player, Location>();
	
	public static ConcurrentHashMap<Location, String> loot_spawns = new ConcurrentHashMap<Location, String>();
	public static ConcurrentHashMap<Location, String> loot_chests_to_spawn = new ConcurrentHashMap<Location, String>();
	public static ConcurrentHashMap<Location, Inventory> loot_chest_inv = new ConcurrentHashMap<Location, Inventory>();
	public static HashMap<Player, Location> last_opened_loot_chest = new HashMap<Player, Location>();
	
	public static HashMap<Location, Long> last_loot_spawn_tick = new HashMap<Location, Long>();
	public static HashMap<Location, Long> skip_loot_spawn_tick = new HashMap<Location, Long>();
	public static List<Location> accessed_chests = new ArrayList<Location>();
	
	public static volatile ConcurrentHashMap<Location, Inventory> sync_block_place = new ConcurrentHashMap<Location, Inventory>();
	
	//public static volatile CopyOnWriteArrayList<Location> sync_block_place = new CopyOnWriteArrayList<Location>();
	
	public static String templatePath_s = "plugins/LootMechanics/loot_templates/";
	public static File templatePath = new File(templatePath_s);
	static Logger log = Logger.getLogger("Minecraft");
	
	public int player_count = 0;
	
	public void onEnable() {
		Main.plugin.getServer().getPluginManager().registerEvents(this, Main.plugin);
		templatePath.mkdir();
		
		Main.plugin.getCommand("hideloot").setExecutor(new CommandHideLoot());
		Main.plugin.getCommand("loadloot").setExecutor(new CommandLoadLoot());
		Main.plugin.getCommand("loot").setExecutor(new CommandLoot());
		Main.plugin.getCommand("showloot").setExecutor(new CommandShowLoot());
		
		Main.plugin.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
			public void run() {
				loadGameWorldlootSpawnerData(); // Loads DungeonRealms loot chests.
				loadlootSpawnTemplates();
			}
		}, 8 * 20L);
		
		Main.plugin.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
			public void run() {
				populateLootChests();
			}
		}, 10 * 20L);
		
		Main.plugin.getServer().getScheduler().scheduleSyncRepeatingTask(Main.plugin, new Runnable() {
			public void run() {
				spawnChestBlocks();
			}
		}, 14 * 20L, 2 * 20L);
		
		new BukkitRunnable() {
			@Override
			public void run() {
				LootChestSpawnEvent();
			}
		}.runTaskTimerAsynchronously(Main.plugin, 12 * 20L, 3 * 20L);
		
		new BukkitRunnable() {
			@Override
			public void run() {
				if(!Bukkit.getMotd().contains("US-0")) {
					cancel();
				}
				savelootSpawnerData();
			}
		}.runTaskTimer(Main.plugin, 600 * 20L, 600 * 20L);
		
		Main.plugin.getServer().getScheduler().scheduleSyncRepeatingTask(Main.plugin, new Runnable() {
			public void run() {
				player_count = Bukkit.getOnlinePlayers().length;
			}
		}, 30 * 20L, 60 * 20L);
		
		log.info("[LootMechanics] V1.0 has been enabled.");
	}
	
	public void onDisable() {
		
		for(Location l : loot_spawns.keySet()) {
			l.getBlock().setType(Material.AIR);
			
			l.add(0, 1, 0).getBlock().setType(Material.AIR);
			
			l.subtract(0, 2, 0);
			while(l.getBlock().getType() == Material.FIRE || l.getBlock().getType() == Material.BEDROCK || l.getBlock().getType() == Material.CHEST) {
				l.getBlock().setType(Material.AIR);
				log.info("[LootMechanics] Fixed a corrupt BEACON placed under loot chest at " + l.getBlockX() + ", " + l.getBlockY() + ", " + l.getBlockZ());
				l.subtract(0, 1, 0);
			}
			
			l.add(0, 1, 0);
		}
		
		savelootSpawnerData();
		log.info("[LootMechanics] V1.0 has been disabled.");
	}
	
	public void populateLootChests() {
		// Just copy the index, and then on the next tick we'll spawn everything.
		for(Entry<Location, String> data : loot_spawns.entrySet()) {
			loot_chests_to_spawn.put(data.getKey(), data.getValue());
		}
	}
	
	public double getLootSpawnDelayMultiplier() {
		if(player_count <= 10) { return 1.25D; }
		if(player_count <= 30) { return 0.75D; }
		if(player_count <= 50) { return 0.50D; }
		if(player_count <= 70) { return 0.40D; }
		if(player_count <= 100) { return 0.39D; }
		if(player_count <= 150) { return 0.20D; }
		return 0.20D; // player_count is greater than 100.
	}
	
	public static boolean isMonsterNearPlayer(Player pl, int radius) {
		List<LivingEntity> le = new ArrayList<LivingEntity>();
		for(Entity ent : pl.getNearbyEntities(radius, radius / 2, radius)) {
			
			if(ent instanceof LivingEntity && MonsterMechanics.mob_health.containsKey(ent) && MonsterMechanics.mob_health.get(ent) > 1 && !(ent.getType() == EntityType.BAT)) {
				/*EntityCreature ec = (EntityCreature) ((CraftEntity) ent).getHandle();
				if(ec.target != null && ec.target.getBukkitEntity().getType() == EntityType.PLAYER){*/
				le.add((LivingEntity) ent);
				//}
			}
		}
		
		if(le.size() > 0) { return true; }
		return false;
	}
	
	@SuppressWarnings("deprecation")
	public static void SpawnInstanceLootChests(String instance) { // TODO: Load loot chests / mobs in instances
	
		if(!(InstanceMechanics.instance_loot.containsKey(instance))) {
			// No loot template found.
			log.info("[InstanceMechanics] No loaded loot templates found for " + instance + ".");
			return;
		}
		
		String loot_template_s = "";
		HashMap<Location, Inventory> local_loot_inventories = new HashMap<Location, Inventory>();
		// We'll cache all the chest locations and inventories in here then put it inside the InstanceMechanics.instance_loot_inv map.
		
		for(Map.Entry<Location, String> entry : InstanceMechanics.instance_loot.get(instance).entrySet()) {
			try {
				Location loc = entry.getKey();
				
				String spawn_data = entry.getValue();
				if(entry.getValue() == null) {
					log.info("[LM] Null entry value for loot chest, skipping, will not remove due to instance code...");
					log.info(loc.toString());
					continue;
				}
				
				loot_template_s = spawn_data; // spawn_data.substring(1, spawn_data.lastIndexOf("@"));
				Inventory loot_chest_inventory = Bukkit.createInventory(null, 27, "Loot Chest");
				
				/*if(InstanceMechanics.instance_loot_inv.get(instance).containsKey(loc) && InstanceMechanics.instance_loot_inv.get(instance).get(loc).getViewers().size() > 0){
					continue; // Someone is viewing the chest.
				}*/
				
				List<String> loot_template = loot_templates.get(loot_template_s);
				//log.info("[LootMechanics] Loading chest with template " + loot_template_s);
				
				for(String s : loot_template) {
					if(s.contains(" ")) {
						s = s.substring(0, s.indexOf(" "));
						if(s.equalsIgnoreCase("")) {
							continue;
						}
					}
					
					// s = 297:1-2%50
					String item_id_s = s.substring(0, s.indexOf(":"));
					int item_id = 0;
					int item_tier = -1;
					short item_meta = 0;
					if(item_id_s.contains("T")) {
						item_id = -1;
						item_tier = Integer.parseInt(item_id_s.substring(1, item_id_s.length())); // Skip the 'T'.
					} else if(!(item_id_s.contains("T"))) {
						if(item_id_s.contains(",")) {
							item_meta = Short.parseShort(item_id_s.split(",")[1]);
							item_id = Integer.parseInt(item_id_s.split(",")[0]);
						} else if(!(item_id_s.contains(","))) {
							item_meta = 0;
							item_id = Integer.parseInt(item_id_s);
						}
					}
					
					if(item_id != -1) { // Spawn given item.
						double spawn_chance = Double.parseDouble(s.substring(s.indexOf("%") + 1, s.length())) * 10.0D;
						double do_i_spawn = new Random().nextInt(1000);
						
						//spawn_chance = spawn_chance * 10; // It's a decimal, so we * it by 10.
						
						if(spawn_chance < 1) {
							spawn_chance = 1;
						}
						
						if(spawn_chance > do_i_spawn) {
							int min_amount = Integer.parseInt(s.substring(s.indexOf(":") + 1, s.indexOf("-")));
							int max_amount = Integer.parseInt(s.substring(s.indexOf("-") + 1, s.indexOf("%")));
							Material m = Material.getMaterial(item_id);
							int amount_to_spawn = 0;
							if(max_amount - min_amount > 0) {
								amount_to_spawn = new Random().nextInt((max_amount - min_amount)) + min_amount;
							} else if(max_amount - min_amount <= 0) {
								amount_to_spawn = max_amount; // They're the same value.
							}
							
							if(m == Material.EMERALD) {
								if(amount_to_spawn > 64) {
									short real_id = 777;
									ItemStack money = new ItemStack(Material.PAPER, 1, real_id);
									loot_chest_inventory.setItem(loot_chest_inventory.firstEmpty(), MoneyMechanics.signBankNote(money, ChatColor.GREEN.toString() + "Bank Note", ChatColor.WHITE.toString() + ChatColor.BOLD.toString() + "Value:" + ChatColor.WHITE.toString() + " " + amount_to_spawn + " Gems" + "," + ChatColor.GRAY.toString() + "Exchange at any bank for GEM(s)"));
								} else if(amount_to_spawn <= 64) {
									loot_chest_inventory.setItem(loot_chest_inventory.firstEmpty(), MoneyMechanics.makeGems(amount_to_spawn));
								}
							} else if(m == Material.POTION) {
								if(item_meta == 1) {
									loot_chest_inventory.setItem(loot_chest_inventory.firstEmpty(), ItemMechanics.signNewCustomItem(Material.POTION, (short) 1, ChatColor.WHITE.toString() + "Minor Health Potion", ChatColor.GRAY.toString() + "A potion that restores " + ChatColor.WHITE.toString() + "15HP"));
								}
								if(item_meta == 5) {
									loot_chest_inventory.setItem(loot_chest_inventory.firstEmpty(), ItemMechanics.signNewCustomItem(Material.POTION, (short) 5, ChatColor.GREEN.toString() + "Health Potion", ChatColor.GRAY.toString() + "A potion that restores " + ChatColor.GREEN.toString() + "75HP"));
								}
								if(item_meta == 9) {
									loot_chest_inventory.setItem(loot_chest_inventory.firstEmpty(), ItemMechanics.signNewCustomItem(Material.POTION, (short) 9, ChatColor.AQUA.toString() + "Major Health Potion", ChatColor.GRAY.toString() + "A potion that restores " + ChatColor.AQUA.toString() + "300HP"));
								}
								if(item_meta == 12) {
									loot_chest_inventory.setItem(loot_chest_inventory.firstEmpty(), ItemMechanics.signNewCustomItem(Material.POTION, (short) 12, ChatColor.LIGHT_PURPLE.toString() + "Superior Health Potion", ChatColor.GRAY.toString() + "A potion that restores " + ChatColor.LIGHT_PURPLE.toString() + "750HP"));
								}
								if(item_meta == 3) {
									loot_chest_inventory.setItem(loot_chest_inventory.firstEmpty(), ItemMechanics.signNewCustomItem(Material.POTION, (short) 3, ChatColor.YELLOW.toString() + "Legendary Health Potion", ChatColor.GRAY.toString() + "A potion that restores " + ChatColor.YELLOW.toString() + "1800HP"));
								}
								
								if(item_meta == 16385) {
									loot_chest_inventory.setItem(loot_chest_inventory.firstEmpty(), ItemMechanics.signNewCustomItem(Material.POTION, (short) 16385, ChatColor.WHITE.toString() + "Minor Splash Health Potion", ChatColor.GRAY.toString() + "A potion that restores " + ChatColor.WHITE.toString() + "15HP," + ChatColor.GRAY.toString() + "to players in a 4x4 AREA"));
								}
								if(item_meta == 16389) {
									loot_chest_inventory.setItem(loot_chest_inventory.firstEmpty(), ItemMechanics.signNewCustomItem(Material.POTION, (short) 16389, ChatColor.GREEN.toString() + "Splash Health Potion", ChatColor.GRAY.toString() + "A potion that restores " + ChatColor.GREEN.toString() + "40HP," + ChatColor.GRAY.toString() + "to players in a 4x4 AREA"));
								}
								if(item_meta == 16393) {
									loot_chest_inventory.setItem(loot_chest_inventory.firstEmpty(), ItemMechanics.signNewCustomItem(Material.POTION, (short) 16393, ChatColor.AQUA.toString() + "Major Splash Health Potion", ChatColor.GRAY.toString() + "A potion that restores " + ChatColor.AQUA.toString() + "150HP," + ChatColor.GRAY.toString() + "to players in a 4x4 AREA"));
								}
								if(item_meta == 16396) {
									loot_chest_inventory.setItem(loot_chest_inventory.firstEmpty(), ItemMechanics.signNewCustomItem(Material.POTION, (short) 16396, ChatColor.LIGHT_PURPLE.toString() + "Superior Splash Health Potion", ChatColor.GRAY.toString() + "A potion that restores " + ChatColor.LIGHT_PURPLE.toString() + "375HP," + ChatColor.GRAY.toString() + "to players in a 4x4 AREA"));
								}
								if(item_meta == 16387) {
									loot_chest_inventory.setItem(loot_chest_inventory.firstEmpty(), ItemMechanics.signNewCustomItem(Material.POTION, (short) 16387, ChatColor.YELLOW.toString() + "Legendary Splash Health Potion", ChatColor.GRAY.toString() + "A potion that restores " + ChatColor.YELLOW.toString() + "900HP," + ChatColor.GRAY.toString() + "to players in a 4x4 AREA"));
								}
							} else if(m == Material.MAGMA_CREAM) {
								loot_chest_inventory.setItem(loot_chest_inventory.firstEmpty(), ItemMechanics.signNewCustomItem(Material.MAGMA_CREAM, (short) 0, ChatColor.LIGHT_PURPLE.toString() + "Orb of Alteration", ChatColor.GRAY.toString() + "Randomizes bonus stats of selected equipment"));
							} else if(m == Material.EMPTY_MAP) {
								if(item_meta == 1) {
									loot_chest_inventory.setItem(loot_chest_inventory.firstEmpty(), CraftItemStack.asCraftCopy(TeleportationMechanics.Cyrennica_scroll));
								}
								if(item_meta == 2) {
									loot_chest_inventory.setItem(loot_chest_inventory.firstEmpty(), CraftItemStack.asCraftCopy(TeleportationMechanics.Harrison_scroll));
								}
								if(item_meta == 3) {
									loot_chest_inventory.setItem(loot_chest_inventory.firstEmpty(), CraftItemStack.asCraftCopy(TeleportationMechanics.Dark_Oak_Tavern_scroll));
								}
								if(item_meta == 4) {
									loot_chest_inventory.setItem(loot_chest_inventory.firstEmpty(), CraftItemStack.asCraftCopy(TeleportationMechanics.Deadpeaks_Mountain_Camp_scroll));
								}
								if(item_meta == 11) {
									loot_chest_inventory.setItem(loot_chest_inventory.firstEmpty(), CraftItemStack.asCraftCopy(EnchantMechanics.t1_wep_scroll));
								}
								if(item_meta == 12) {
									loot_chest_inventory.setItem(loot_chest_inventory.firstEmpty(), CraftItemStack.asCraftCopy(EnchantMechanics.t2_wep_scroll));
								}
								if(item_meta == 13) {
									loot_chest_inventory.setItem(loot_chest_inventory.firstEmpty(), CraftItemStack.asCraftCopy(EnchantMechanics.t3_wep_scroll));
								}
								if(item_meta == 14) {
									loot_chest_inventory.setItem(loot_chest_inventory.firstEmpty(), CraftItemStack.asCraftCopy(EnchantMechanics.t4_wep_scroll));
								}
								if(item_meta == 15) {
									loot_chest_inventory.setItem(loot_chest_inventory.firstEmpty(), CraftItemStack.asCraftCopy(EnchantMechanics.t5_wep_scroll));
								}
								if(item_meta == 21) {
									loot_chest_inventory.setItem(loot_chest_inventory.firstEmpty(), CraftItemStack.asCraftCopy(EnchantMechanics.t1_armor_scroll));
								}
								if(item_meta == 22) {
									loot_chest_inventory.setItem(loot_chest_inventory.firstEmpty(), CraftItemStack.asCraftCopy(EnchantMechanics.t2_armor_scroll));
								}
								if(item_meta == 23) {
									loot_chest_inventory.setItem(loot_chest_inventory.firstEmpty(), CraftItemStack.asCraftCopy(EnchantMechanics.t3_armor_scroll));
								}
								if(item_meta == 24) {
									loot_chest_inventory.setItem(loot_chest_inventory.firstEmpty(), CraftItemStack.asCraftCopy(EnchantMechanics.t4_armor_scroll));
								}
								if(item_meta == 25) {
									loot_chest_inventory.setItem(loot_chest_inventory.firstEmpty(), CraftItemStack.asCraftCopy(EnchantMechanics.t5_armor_scroll));
								}
							} else {
								loot_chest_inventory.addItem(new ItemStack(m, amount_to_spawn, item_meta));
							}
							
							continue;
						}
						
					}
					
					if(item_tier != -1) { // Spawn random tier weapon.
						double spawn_chance = Double.parseDouble(s.substring(s.indexOf("%") + 1, s.length())) * 10.0D;
						double do_i_spawn = new Random().nextInt(1000);
						
						if(spawn_chance > do_i_spawn) {
							ItemStack i = ItemMechanics.generateRandomTierItem(item_tier);
							int random_dur = new Random().nextInt(i.getType().getMaxDurability()) + (i.getType().getMaxDurability() / 10);
							if(random_dur > i.getType().getMaxDurability()) {
								random_dur = i.getType().getMaxDurability();
							}
							i.setDurability((short) random_dur);
							loot_chest_inventory.setItem(loot_chest_inventory.firstEmpty(), i); // quantity ALWAYS = 1x.
						}
						
						continue;
					}
				}
				
				int count = 0;
				for(ItemStack i : loot_chest_inventory.getContents()) {
					if(i == null || i.getType() == Material.AIR) {
						continue;
					}
					count++;
				}
				if(count <= 0 && loot_template.size() > 0) {
					log.info("[LootMechanics] Spawned empty chest of " + loot_template_s);
					continue; // We won't say we've respawned this chest yet, cause it's empty. We'll try again.
				}
				
				final Block b = loc.getBlock();
				if(b.getType() != Material.AIR && b.getType() != Material.CHEST) {
					continue;
				}
				
				for(Entity e : b.getChunk().getEntities()) {
					if(e instanceof EnderCrystal && e.getLocation().distanceSquared(b.getLocation()) <= 4) {
						e.remove();
					}
				}
				
				if(!(b.getType() == Material.CHEST)) {
					b.setType(Material.CHEST);
				}
				
				local_loot_inventories.put(b.getLocation(), loot_chest_inventory);
				
				if(b.getLocation().add(0, 1, 0).getBlock().getType() == Material.FIRE) {
					b.getLocation().getBlock().setType(Material.AIR);
					if(b.getLocation().add(0, 1, 0).getBlock().getType() == Material.BEDROCK) {
						b.getLocation().getBlock().setType(Material.AIR);
					}
				}
				
			} catch(IndexOutOfBoundsException e) {
				log.info("[LootMechanics] Corrupt loot template at " + loot_template_s + ".");
				continue;
			}
		}
		
		InstanceMechanics.instance_loot_inv.put(instance, local_loot_inventories);
		// Store the data.
	}
	
	@SuppressWarnings("deprecation")
	public void LootChestSpawnEvent() {
		if(loot_chests_to_spawn.size() <= 0) { return; }
		String loot_template_s = "";
		final List<Location> to_remove = new ArrayList<Location>();
		
		for(Map.Entry<Location, String> entry : loot_chests_to_spawn.entrySet()) {
			try {
				final Location loc = entry.getKey();
				
				if(InstanceMechanics.isInstance(loc.getWorld().getName())) {
					continue;
				}
				
				if(skip_loot_spawn_tick.containsKey(loc)) {
					if((System.currentTimeMillis() - skip_loot_spawn_tick.get(loc)) <= (10 * 1000)) {
						continue;
					} else {
						skip_loot_spawn_tick.remove(loc);
					}
				}
				
				boolean nearby = false;
				
				boolean chunk_loaded = false;
				for(Location l : MonsterMechanics.player_locations.values()) {
					if(!l.getWorld().getName().equalsIgnoreCase(loc.getWorld().getName())) {
						continue;
					}
					l.setY(loc.getY());
					double distance = l.distanceSquared(loc);
					if(distance <= 6400) { // 80 blocks, chunk is loaded.
						chunk_loaded = true;
						if(distance <= 100) {
							nearby = true;
						}
						break;
					}
				}
				
				if(!chunk_loaded) {
					continue;
				}
				
				if(nearby == true && !(TutorialMechanics.onTutorialIsland(loc))) {
					//last_loot_spawn_tick.put(loc, System.currentTimeMillis());
					skip_loot_spawn_tick.put(loc, System.currentTimeMillis());
					continue; // Someone is nearby, fuck out the way.
				}
				
				if(PowerupMechanics.beacons.contains(loc)) {
					last_loot_spawn_tick.put(loc, System.currentTimeMillis());
					continue;
				}
				
				/*if(loc.getBlock().getType() != Material.AIR && loc.getBlock().getType() != Material.CHEST){
				last_loot_spawn_tick.put(loc, System.currentTimeMillis());
				continue; //Could be a beacon here.
				}*/
				
				String spawn_data = entry.getValue();
				if(entry.getValue() == null) {
					log.info("[LM] Null entry value for loot chest, skipping, marking for removal...");
					log.info(loc.toString());
					to_remove.add(loc);
					continue;
				}
				loot_template_s = spawn_data.substring(1, spawn_data.lastIndexOf("@"));
				
				final Inventory loot_chest_inventory = Bukkit.createInventory(null, 27, "Loot Chest");
				double delay_multiplier = getLootSpawnDelayMultiplier();
				long spawn_delay = Math.round(Double.parseDouble(spawn_data.substring(spawn_data.lastIndexOf("@") + 1, spawn_data.indexOf("#"))) * (double) delay_multiplier); // Multiplier to make the delay TWICE as long.
				long last_spawn = 0;
				if(last_loot_spawn_tick.containsKey(loc)) {
					last_spawn = last_loot_spawn_tick.get(loc);
				}
				long cur_time = System.currentTimeMillis();
				if(loot_chest_inv.containsKey(loc) && loot_chest_inv.get(loc).getViewers().size() > 0) {
					continue; // Someone is viewing the chest.
				}
				
				if((cur_time - last_spawn) < (spawn_delay * 1000)) {
					//log.info("Not time to spawn yet.");
					//log.info((cur_time - last_spawn) + " / " + (spawn_delay * 1000));
					continue; // Not time yet.
				}
				
				/*if(loc.getBlock().getType() != Material.AIR && (!(accessed_chests.contains(loc))) && loot_chest_inv.containsKey(loc)){ // The chest is there and unaccessed. Don't reshuffle.
				continue;
				}*/
				
				List<String> loot_template = loot_templates.get(loot_template_s);
				
				for(String s : loot_template) {
					if(s.contains(" ")) {
						s = s.substring(0, s.indexOf(" "));
						if(s.equalsIgnoreCase("")) {
							continue;
						}
					}
					
					// s = 297:1-2%50
					String item_id_s = s.substring(0, s.indexOf(":"));
					int item_id = 0;
					int item_tier = -1;
					short item_meta = 0;
					if(item_id_s.startsWith("T")) {
						item_id = -1;
						item_tier = Integer.parseInt(item_id_s.substring(1, item_id_s.length())); // Skip the 'T'.
					} else if(item_id_s.startsWith("*")) {
						item_id = -1;
						item_tier = -1;
					} else {
						if(item_id_s.contains(",")) {
							item_meta = Short.parseShort(item_id_s.split(",")[1]);
							item_id = Integer.parseInt(item_id_s.split(",")[0]);
						} else if(!(item_id_s.contains(","))) {
							item_meta = 0;
							item_id = Integer.parseInt(item_id_s);
						}
					}
					
					if(item_id != -1) { // Spawn given item.
						double spawn_chance = Double.parseDouble(s.substring(s.indexOf("%") + 1, s.length())) * 10.0D;
						double do_i_spawn = new Random().nextInt(1000);
						
						//spawn_chance = spawn_chance * 10; // It's a decimal, so we * it by 10.
						
						if(spawn_chance < 1) {
							spawn_chance = 1;
						}
						
						if(spawn_chance > do_i_spawn) {
							int min_amount = Integer.parseInt(s.substring(s.indexOf(":") + 1, s.indexOf("-")));
							int max_amount = Integer.parseInt(s.substring(s.indexOf("-") + 1, s.indexOf("%")));
							Material m = Material.getMaterial(item_id);
							int amount_to_spawn = 0;
							if(max_amount - min_amount > 0) {
								amount_to_spawn = new Random().nextInt((max_amount - min_amount)) + min_amount;
							} else if(max_amount - min_amount <= 0) {
								amount_to_spawn = max_amount; // They're the same value.
							}
							
							if(m == Material.EMERALD) {
								if(amount_to_spawn > 64) {
									short real_id = 777;
									ItemStack money = new ItemStack(Material.PAPER, 1, real_id);
									loot_chest_inventory.setItem(loot_chest_inventory.firstEmpty(), MoneyMechanics.signBankNote(money, ChatColor.GREEN.toString() + "Bank Note", ChatColor.WHITE.toString() + ChatColor.BOLD.toString() + "Value:" + ChatColor.WHITE.toString() + " " + amount_to_spawn + " Gems" + "," + ChatColor.GRAY.toString() + "Exchange at any bank for GEM(s)"));
								} else if(amount_to_spawn <= 64) {
									loot_chest_inventory.setItem(loot_chest_inventory.firstEmpty(), MoneyMechanics.makeGems(amount_to_spawn));
								}
							} else if(m == Material.POTION) {
								if(item_meta == 1) {
									loot_chest_inventory.setItem(loot_chest_inventory.firstEmpty(), ItemMechanics.signNewCustomItem(Material.POTION, (short) 1, ChatColor.WHITE.toString() + "Minor Health Potion", ChatColor.GRAY.toString() + "A potion that restores " + ChatColor.WHITE.toString() + "15HP"));
								}
								if(item_meta == 5) {
									loot_chest_inventory.setItem(loot_chest_inventory.firstEmpty(), ItemMechanics.signNewCustomItem(Material.POTION, (short) 5, ChatColor.GREEN.toString() + "Health Potion", ChatColor.GRAY.toString() + "A potion that restores " + ChatColor.GREEN.toString() + "75HP"));
								}
								if(item_meta == 9) {
									loot_chest_inventory.setItem(loot_chest_inventory.firstEmpty(), ItemMechanics.signNewCustomItem(Material.POTION, (short) 9, ChatColor.AQUA.toString() + "Major Health Potion", ChatColor.GRAY.toString() + "A potion that restores " + ChatColor.AQUA.toString() + "300HP"));
								}
								if(item_meta == 12) {
									loot_chest_inventory.setItem(loot_chest_inventory.firstEmpty(), ItemMechanics.signNewCustomItem(Material.POTION, (short) 12, ChatColor.LIGHT_PURPLE.toString() + "Superior Health Potion", ChatColor.GRAY.toString() + "A potion that restores " + ChatColor.LIGHT_PURPLE.toString() + "750HP"));
								}
								if(item_meta == 3) {
									loot_chest_inventory.setItem(loot_chest_inventory.firstEmpty(), ItemMechanics.signNewCustomItem(Material.POTION, (short) 3, ChatColor.YELLOW.toString() + "Legendary Health Potion", ChatColor.GRAY.toString() + "A potion that restores " + ChatColor.YELLOW.toString() + "1800HP"));
								}
								
								if(item_meta == 16385) {
									loot_chest_inventory.setItem(loot_chest_inventory.firstEmpty(), ItemMechanics.signNewCustomItem(Material.POTION, (short) 16385, ChatColor.WHITE.toString() + "Minor Splash Health Potion", ChatColor.GRAY.toString() + "A potion that restores " + ChatColor.WHITE.toString() + "15HP," + ChatColor.GRAY.toString() + "to players in a 4x4 AREA"));
								}
								if(item_meta == 16389) {
									loot_chest_inventory.setItem(loot_chest_inventory.firstEmpty(), ItemMechanics.signNewCustomItem(Material.POTION, (short) 16389, ChatColor.GREEN.toString() + "Splash Health Potion", ChatColor.GRAY.toString() + "A potion that restores " + ChatColor.GREEN.toString() + "40HP," + ChatColor.GRAY.toString() + "to players in a 4x4 AREA"));
								}
								if(item_meta == 16393) {
									loot_chest_inventory.setItem(loot_chest_inventory.firstEmpty(), ItemMechanics.signNewCustomItem(Material.POTION, (short) 16393, ChatColor.AQUA.toString() + "Major Splash Health Potion", ChatColor.GRAY.toString() + "A potion that restores " + ChatColor.AQUA.toString() + "150HP," + ChatColor.GRAY.toString() + "to players in a 4x4 AREA"));
								}
								if(item_meta == 16396) {
									loot_chest_inventory.setItem(loot_chest_inventory.firstEmpty(), ItemMechanics.signNewCustomItem(Material.POTION, (short) 16396, ChatColor.LIGHT_PURPLE.toString() + "Superior Splash Health Potion", ChatColor.GRAY.toString() + "A potion that restores " + ChatColor.LIGHT_PURPLE.toString() + "375HP," + ChatColor.GRAY.toString() + "to players in a 4x4 AREA"));
								}
								if(item_meta == 16387) {
									loot_chest_inventory.setItem(loot_chest_inventory.firstEmpty(), ItemMechanics.signNewCustomItem(Material.POTION, (short) 16387, ChatColor.YELLOW.toString() + "Legendary Splash Health Potion", ChatColor.GRAY.toString() + "A potion that restores " + ChatColor.YELLOW.toString() + "900HP," + ChatColor.GRAY.toString() + "to players in a 4x4 AREA"));
								}
							} else if(m == Material.MAGMA_CREAM) {
								loot_chest_inventory.setItem(loot_chest_inventory.firstEmpty(), ItemMechanics.signNewCustomItem(Material.MAGMA_CREAM, (short) 0, ChatColor.LIGHT_PURPLE.toString() + "Orb of Alteration", ChatColor.GRAY.toString() + "Randomizes bonus stats of selected equipment"));
							} else if(m == Material.EMPTY_MAP) {
								if(item_meta == 1) {
									loot_chest_inventory.setItem(loot_chest_inventory.firstEmpty(), CraftItemStack.asCraftCopy(TeleportationMechanics.Cyrennica_scroll));
								}
								if(item_meta == 2) {
									loot_chest_inventory.setItem(loot_chest_inventory.firstEmpty(), CraftItemStack.asCraftCopy(TeleportationMechanics.Harrison_scroll));
								}
								if(item_meta == 3) {
									loot_chest_inventory.setItem(loot_chest_inventory.firstEmpty(), CraftItemStack.asCraftCopy(TeleportationMechanics.Dark_Oak_Tavern_scroll));
								}
								if(item_meta == 4) {
									loot_chest_inventory.setItem(loot_chest_inventory.firstEmpty(), CraftItemStack.asCraftCopy(TeleportationMechanics.Deadpeaks_Mountain_Camp_scroll));
								}
								if(item_meta == 11) {
									loot_chest_inventory.setItem(loot_chest_inventory.firstEmpty(), CraftItemStack.asCraftCopy(EnchantMechanics.t1_wep_scroll));
								}
								if(item_meta == 12) {
									loot_chest_inventory.setItem(loot_chest_inventory.firstEmpty(), CraftItemStack.asCraftCopy(EnchantMechanics.t2_wep_scroll));
								}
								if(item_meta == 13) {
									loot_chest_inventory.setItem(loot_chest_inventory.firstEmpty(), CraftItemStack.asCraftCopy(EnchantMechanics.t3_wep_scroll));
								}
								if(item_meta == 14) {
									loot_chest_inventory.setItem(loot_chest_inventory.firstEmpty(), CraftItemStack.asCraftCopy(EnchantMechanics.t4_wep_scroll));
								}
								if(item_meta == 15) {
									loot_chest_inventory.setItem(loot_chest_inventory.firstEmpty(), CraftItemStack.asCraftCopy(EnchantMechanics.t5_wep_scroll));
								}
								if(item_meta == 21) {
									loot_chest_inventory.setItem(loot_chest_inventory.firstEmpty(), CraftItemStack.asCraftCopy(EnchantMechanics.t1_armor_scroll));
								}
								if(item_meta == 22) {
									loot_chest_inventory.setItem(loot_chest_inventory.firstEmpty(), CraftItemStack.asCraftCopy(EnchantMechanics.t2_armor_scroll));
								}
								if(item_meta == 23) {
									loot_chest_inventory.setItem(loot_chest_inventory.firstEmpty(), CraftItemStack.asCraftCopy(EnchantMechanics.t3_armor_scroll));
								}
								if(item_meta == 24) {
									loot_chest_inventory.setItem(loot_chest_inventory.firstEmpty(), CraftItemStack.asCraftCopy(EnchantMechanics.t4_armor_scroll));
								}
								if(item_meta == 25) {
									loot_chest_inventory.setItem(loot_chest_inventory.firstEmpty(), CraftItemStack.asCraftCopy(EnchantMechanics.t5_armor_scroll));
								}
							} else {
								loot_chest_inventory.addItem(new ItemStack(m, amount_to_spawn, item_meta));
							}
							
							continue;
						}
						
					}
					
					if(item_tier != -1) { // Spawn random tier weapon.
						double spawn_chance = Double.parseDouble(s.substring(s.indexOf("%") + 1, s.length())) * 10.0D;
						double do_i_spawn = new Random().nextInt(1000);
						
						if(spawn_chance > do_i_spawn) {
							ItemStack i = ItemMechanics.generateRandomTierItem(item_tier);
							int random_dur = new Random().nextInt(i.getType().getMaxDurability()) + (i.getType().getMaxDurability() / 10);
							if(random_dur > i.getType().getMaxDurability()) {
								random_dur = i.getType().getMaxDurability();
							}
							i.setDurability((short) random_dur);
							loot_chest_inventory.setItem(loot_chest_inventory.firstEmpty(), i); // quantity ALWAYS = 1x.
						}
						
						continue;
					}
					
					if(item_id_s.startsWith("*")) {
						String template_name = item_id_s.substring(1, item_id_s.indexOf(":"));
						double spawn_chance = Double.parseDouble(s.substring(s.indexOf("%") + 1, s.length())) * 10.0D;
						double do_i_spawn = new Random().nextInt(1000);
						
						if(spawn_chance > do_i_spawn) {
							ItemStack i = ItemGenerators.customGenerator(template_name);
							loot_chest_inventory.setItem(loot_chest_inventory.firstEmpty(), i); // quantity ALWAYS = 1x.
						}
						
						continue;
					}
				}
				
				int count = 0;
				for(ItemStack i : loot_chest_inventory.getContents()) {
					if(i == null || i.getType() == Material.AIR) {
						continue;
					}
					count++;
				}
				
				if(count <= 0 && !(TutorialMechanics.onTutorialIsland(loc))) {
					//log.info("loot_chest_inventory: " + String.valueOf(loot_chest_inventory.getContents().length));
					continue; // We won't say we've respawned this chest yet, cause it's empty. We'll try again.
				}
				new BukkitRunnable() {
					public void run() {
						for(Entity e : loc.getChunk().getEntities()) {
							if(e instanceof EnderCrystal && e.getLocation().distanceSquared(loc) <= 4) {
								e.remove();
							}
						}
						to_remove.add(loc.getBlock().getLocation());
						sync_block_place.put(loc.getBlock().getLocation(), loot_chest_inventory);
					}
				}.runTask(Main.plugin);
				
			} catch(IndexOutOfBoundsException e) {
				log.info("[LootMechanics] Corrupt loot template at " + loot_template_s + ".");
				continue;
			}
		}
		
		for(Location loc : to_remove) {
			loot_chests_to_spawn.remove(loc);
		}
		
	}
	
	// Spawns all chest blocks on the main thread.
	public void spawnChestBlocks() {
		long cur_time = System.currentTimeMillis();
		
		for(Entry<Location, Inventory> data : sync_block_place.entrySet()) {
			Location loc = data.getKey().getBlock().getLocation();
			Block b = loc.getBlock();
			Inventory loot_chest_inventory = data.getValue();
			
			if(b.getType() != Material.AIR && b.getType() != Material.CHEST && b.getType() != Material.BEDROCK) {
				sync_block_place.remove(loc);
				loot_chests_to_spawn.put(loc, loot_spawns.get(loc));
				continue;
			}
			
			if(b.getType() != Material.AIR && b.getType() != Material.CHEST && b.getType() != Material.BEDROCK) {
				sync_block_place.remove(loc);
				loot_chests_to_spawn.put(loc, loot_spawns.get(loc));
				continue;
			}
			
			loot_chest_inv.put(loc.getBlock().getLocation(), loot_chest_inventory);
			last_loot_spawn_tick.put(loc.getBlock().getLocation(), (cur_time));
			accessed_chests.remove(loc.getBlock().getLocation());
			
			if(!(b.getType() == Material.CHEST)) {
				b.setType(Material.CHEST);
			}
			
			if(b.getLocation().add(0, 1, 0).getBlock().getType() == Material.FIRE) {
				b.getLocation().getBlock().setType(Material.AIR);
				if(b.getLocation().add(0, 1, 0).getBlock().getType() == Material.BEDROCK) {
					b.getLocation().getBlock().setType(Material.AIR);
				}
			}
			
			sync_block_place.remove(loc);
		}
	}
	
	public static void savelootSpawnerData() {
		String all_dat = "";
		int count = 0;
		
		for(Entry<Location, String> entry : loot_spawns.entrySet()) {
			Location loc = entry.getKey();
			Block b = loc.getBlock();
			String spawn_data = entry.getValue();
			all_dat += loc.getBlockX() + "," + b.getLocation().getBlockY() + "," + loc.getBlockZ() + "=" + spawn_data + "\r\n";
			count++;
		}
		
		if(all_dat.length() > 1) {
			try {
				DataOutputStream dos = new DataOutputStream(new FileOutputStream("plugins/LootMechanics/global_loot.dat", false));
				dos.writeBytes(all_dat + "\n");
				dos.close();
			} catch(IOException e) {}
		}
		
		log.info("[LootMechanics] GLOBAL: " + count + " LOOT CHEST SPAWN profiles have been SAVED.");
		
		String loot_string = "";
		for(Entry<String, HashMap<Location, String>> data : InstanceMechanics.instance_loot.entrySet()) {
			
			// Clear local variables.
			loot_string = "";
			count = 0;
			
			String instance_name = data.getKey();
			if(instance_name.contains(".")) {
				continue; // Instance, not template.
			}
			HashMap<Location, String> location_data = data.getValue();
			for(Entry<Location, String> l_loot_spawns : location_data.entrySet()) {
				Location loc = l_loot_spawns.getKey();
				Block b = loc.getBlock();
				String spawn_data = l_loot_spawns.getValue();
				loot_string += loc.getBlockX() + "," + b.getLocation().getBlockY() + "," + loc.getBlockZ() + "=" + spawn_data + "\r\n";
				count++;
			}
			
			// Now loot_string is populated, let's save it.
			try {
				DataOutputStream dos = new DataOutputStream(new FileOutputStream("plugins/InstanceMechanics/loot/" + instance_name + ".dat", false));
				dos.writeBytes(loot_string + "\n");
				dos.close();
			} catch(IOException e) {}
			
			log.info("[LootMechanics] " + instance_name.toUpperCase() + ": " + count + " LOOT CHEST SPAWN profiles have been SAVED.");
			
		}
	}
	
	public static void loadInstancelootSpawnerData(String instance_name) { // This will only occur when the instance is loaded.
		int count = 0;
		
		String instance_template = instance_name;
		if(instance_name.contains(".")) {
			instance_template = instance_name.substring(0, instance_name.indexOf("."));
		}
		
		for(File f : new File("plugins/InstanceMechanics/loot/").listFiles()) {
			count = 0;
			String f_name = f.getName().replaceAll(".dat", "");
			if(f_name.equalsIgnoreCase(instance_template)) {
				// It's the one we want.
				HashMap<Location, String> local_loot_spawns = new HashMap<Location, String>();
				try {
					BufferedReader reader = new BufferedReader(new FileReader(f));
					String line = "";
					while((line = reader.readLine()) != null) {
						if(line.contains("=")) {
							count++;
							String[] cords = line.split("=")[0].split(",");
							Location loc = new Location(Bukkit.getWorld(instance_name), Double.parseDouble(cords[0]), Double.parseDouble(cords[1]), Double.parseDouble(cords[2]));
							
							String spawn_data = line.split("=")[1];
							local_loot_spawns.put(loc, spawn_data);
							count++;
						}
					}
					// Read entire file, now store local_loot_spawns.
					reader.close();
					log.info("[LootMechanics] Loaded " + count + " loot chests into " + instance_name);
					InstanceMechanics.instance_loot.put(instance_name, local_loot_spawns);
				} catch(Exception err) {
					log.info("[LootMechanics] Failed to load instance loot data for " + f.getName());
					err.printStackTrace();
					continue;
				}
			}
		}
	}
	
	public static void loadGameWorldlootSpawnerData() { // loot1=0,0,0
		int count = 0;
		int skip_count = 0;
		
		try {
			File file = new File("plugins/LootMechanics/global_loot.dat");
			if(!(file.exists())) { return; // NPE Exception.
			}
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line = "";
			while((line = reader.readLine()) != null) {
				if(line.contains("=")) {
					
					String[] cords = line.split("=")[0].split(",");
					Location loc = new Location(Bukkit.getWorlds().get(0), Double.parseDouble(cords[0]), Double.parseDouble(cords[1]), Double.parseDouble(cords[2]));
					
					boolean mob_near = false;
					for(Location mob_loc : MonsterMechanics.mob_spawns.keySet()) {
						if(mob_loc.distanceSquared(loc) <= 900) {
							mob_near = true;
						}
					}
					
					if(mob_near == true) {
						String spawn_data = line.split("=")[1];
						loot_spawns.put(loc, spawn_data);
						count++;
					} else {
						log.info("SKIPPED: " + line);
						skip_count++;
					}
				}
			}
			reader.close();
			log.info("[LootMechanics] " + count + " LOOT CHEST SPAWN profiles have been LOADED.");
			log.info("[LootMechanics] " + skip_count + " LOOT CHEST SPAWN profiles SKIPPED due to lack of mobs.");
		} catch(IOException ioe) {
			ioe.printStackTrace();
		}
	}
	
	public static void loadlootSpawnTemplates() {
		//373,5:1-1%20 /healthpotion
		//T2:1-1%4
		
		int count = 0;
		
		try {
			for(File f : templatePath.listFiles()) {
				List<String> lloot_template = new ArrayList<String>();
				String tn = f.getName();
				if(tn.endsWith(".loot")) {
					BufferedReader reader = new BufferedReader(new FileReader(f));
					String line = "";
					while((line = reader.readLine()) != null) {
						if(line.contains(" ")) {
							line = line.substring(0, line.indexOf(" "));
						}
						lloot_template.add(line);
					}
					reader.close();
				}
				loot_templates.put(tn, lloot_template);
				count++;
			}
			
			log.info("[LootMechanics] " + count + " LOOT CHEST TEMPLATE profiles have been LOADED.");
		} catch(IOException ioe) {
			ioe.printStackTrace();
		}
	}
	
	public String getAvailableTemplates() {
		String template_list = "";
		for(File f : templatePath.listFiles()) {
			String tn = f.getName();
			if(tn.endsWith(".loot")) {
				template_list += tn.substring(0, tn.lastIndexOf(".")) + ", "; // test.loot -> test
			}
		}
		return template_list;
	}
	
	public boolean doesTemplateExist(String template_name) {
		File f = new File(templatePath_s + template_name + ".loot");
		if(f.exists()) { return true; }
		return false;
	}
	
	public File getTemplateFile(String template_name) {
		File f = new File(templatePath_s + template_name);
		if(f.exists()) { return f; }
		return null;
	}
	
	public boolean isLootChest(Block b) {
		Location b_loc = b.getLocation();
		if(loot_spawns.containsKey(b_loc)) { return true; }
		return false;
	}
	
	public boolean isInstanceLootChest(Block b) {
		Location b_loc = b.getLocation();
		if(InstanceMechanics.instance_loot_inv.containsKey(b.getWorld().getName()) && InstanceMechanics.instance_loot_inv.get(b.getWorld().getName()).containsKey(b_loc)) { return true; }
		return false;
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		Location ploc = p.getLocation();
		
		List<Entity> nearby = p.getNearbyEntities(32, 32, 32);
		for(Entity ent : nearby) {
			if(ent instanceof Player) {
				Player pl = (Player) ent;
				if(pl.getName().equalsIgnoreCase(p.getName())) {
					continue;
				}
				return; // Player nearby, let's not ruin anyone's fun.
			}
		}
		
		for(Location l : loot_chest_inv.keySet()) {
			ploc.setY(l.getY()); // Ignore any Y-coords.
			if(l.distanceSquared(ploc) <= 1225) {
				Block chest = l.getBlock();
				chest.setType(Material.AIR);
				last_loot_spawn_tick.put(chest.getLocation(), (System.currentTimeMillis()));
				loot_chests_to_spawn.put(chest.getLocation(), loot_spawns.get(chest.getLocation()));
				loot_chest_inv.remove(l);
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onInventoryClose(InventoryCloseEvent e) {
		Player p = (Player) e.getPlayer();
		
		if(e.getInventory().getName().equalsIgnoreCase("Loot Chest")) {
			int items_left = 0;
			for(ItemStack i : e.getInventory().getContents()) {
				if(i != null && i.getType() != Material.AIR) {
					items_left++;
				}
			}
			if(items_left <= 0) { // It's empty!
				Location loc = last_opened_loot_chest.get(p);
				if(loc != null && loc.getBlock().getType() == Material.CHEST) {
					final Block chest = loc.getBlock();
					loc.getBlock().setType(Material.AIR);
					p.playSound(p.getLocation(), Sound.ZOMBIE_WOODBREAK, 0.5F, 1.2F);
					Main.plugin.getServer().getScheduler().scheduleAsyncDelayedTask(Main.plugin, new Runnable() {
						public void run() {
							Packet particles = new PacketPlayOutWorldEvent(2001, (int) Math.round(chest.getLocation().getX()), (int) Math.round(chest.getLocation().getY()), (int) Math.round(chest.getLocation().getZ()), 54, false);
							((CraftServer) Main.plugin.getServer()).getServer().getPlayerList().sendPacketNearby(chest.getLocation().getX(), chest.getLocation().getY(), chest.getLocation().getZ(), 24, ((CraftWorld) chest.getWorld()).getHandle().dimension, particles);
							
						}
					}, 2L);
					last_opened_loot_chest.remove(p);
					
					if(isLootChest(chest)) {
						loot_chest_inv.remove(chest.getLocation());
						last_loot_spawn_tick.put(chest.getLocation(), (System.currentTimeMillis()));
						loot_chests_to_spawn.put(chest.getLocation(), loot_spawns.get(chest.getLocation()));
					} else if(isInstanceLootChest(chest)) {
						InstanceMechanics.instance_loot_inv.get(p.getWorld().getName()).remove(chest.getLocation());
					}
				}
			}
			
			p.playSound(p.getLocation(), Sound.CHEST_CLOSE, 1F, 1F);
		}
		
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		loot_spawn_step.remove(p);
		loot_spawn_data.remove(p);
		if(loot_spawn_location.containsKey(p)) {
			loot_spawn_location.get(p).getBlock().setType(Material.AIR);
		}
		loot_spawn_location.remove(p);
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = false)
	// LOW == So shops don't say 'locked chest' on destruction.
	public void onPlayerInteract(PlayerInteractEvent e) {
		if(e.hasBlock() && (e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK)) {
			Block chest = e.getClickedBlock();
			if((InstanceMechanics.isInstance(e.getClickedBlock().getWorld().getName()) || e.getClickedBlock().getWorld().getName().equalsIgnoreCase(Bukkit.getWorlds().get(0).getName())) && (chest.getType() == Material.CHEST || chest.getType() == Material.LOCKED_CHEST) && !isLootChest(chest) && !(ShopMechanics.isShop(chest))) {
				Player p = e.getPlayer();
				if(!(p.isOp())) {
					p.sendMessage(ChatColor.GRAY + "The chest is locked.");
					e.setCancelled(true);
					return;
				}
			}
			if(isLootChest(chest)) {
				Player p = e.getPlayer();
				if(chest.getType() != Material.CHEST) { return; // Not a chest.
				}
				if(isMonsterNearPlayer(p, 10)) {
					p.sendMessage(ChatColor.RED + "It is " + ChatColor.BOLD + "NOT" + ChatColor.RED + " safe to open that right now.");
					p.sendMessage(ChatColor.GRAY + "Eliminate the monsters in the area first.");
					e.setCancelled(true);
					return;
				}
				Inventory loot = loot_chest_inv.get(chest.getLocation());
				if(loot == null) {
					p.playSound(p.getLocation(), Sound.ZOMBIE_WOODBREAK, 0.5F, 1.2F);
					chest.setType(Material.AIR);
					Packet particles = new PacketPlayOutWorldEvent(2001, (int) Math.round(chest.getLocation().getX()), (int) Math.round(chest.getLocation().getY()), (int) Math.round(chest.getLocation().getZ()), 54, false);
					((CraftServer) Main.plugin.getServer()).getServer().getPlayerList().sendPacketNearby(chest.getLocation().getX(), chest.getLocation().getY(), chest.getLocation().getZ(), 24, ((CraftWorld) chest.getWorld()).getHandle().dimension, particles);
					return;
				}
				if(loot != null && loot.getViewers() != null && loot.getViewers().size() > 0) {
					List<Player> viewers = new ArrayList<Player>();
					for(HumanEntity he : loot.getViewers()) {
						if(he instanceof Player) {
							Player phe = (Player) he;
							viewers.add(phe);
						}
					}
					
					for(Player pl : viewers) {
						pl.closeInventory();
						pl.playSound(pl.getLocation(), Sound.CHEST_CLOSE, 1F, 1F);
					}
				}
				
				p.playSound(p.getLocation(), Sound.ZOMBIE_WOODBREAK, 0.5F, 1.2F);
				chest.setType(Material.AIR);
				Packet particles = new PacketPlayOutWorldEvent(2001, (int) Math.round(chest.getLocation().getX()), (int) Math.round(chest.getLocation().getY()), (int) Math.round(chest.getLocation().getZ()), 54, false);
				((CraftServer) Main.plugin.getServer()).getServer().getPlayerList().sendPacketNearby(chest.getLocation().getX(), chest.getLocation().getY(), chest.getLocation().getZ(), 24, ((CraftWorld) chest.getWorld()).getHandle().dimension, particles);
				last_loot_spawn_tick.put(chest.getLocation(), (System.currentTimeMillis()));
				loot_chests_to_spawn.put(chest.getLocation(), loot_spawns.get(chest.getLocation()));
				loot_chest_inv.remove(chest.getLocation());
				
				for(ItemStack i : loot.getContents()) {
					if(i == null) {
						continue;
					}
					chest.getWorld().dropItem(chest.getLocation(), i);
				}
				return;
			}
			
			if(isInstanceLootChest(chest)) {
				Player p = e.getPlayer();
				if(chest.getType() != Material.CHEST) { return; // Not a chest.
				}
				if(isMonsterNearPlayer(p, 10)) {
					p.sendMessage(ChatColor.RED + "It is " + ChatColor.BOLD + "NOT" + ChatColor.RED + " safe to open that right now.");
					p.sendMessage(ChatColor.GRAY + "Eliminate the monsters in the area first.");
					e.setCancelled(true);
					return;
				}
				Inventory loot = InstanceMechanics.instance_loot_inv.get(p.getWorld().getName()).get(chest.getLocation());
				if(loot == null) {
					p.playSound(p.getLocation(), Sound.ZOMBIE_WOODBREAK, 0.5F, 1.2F);
					chest.setType(Material.AIR);
					Packet particles = new PacketPlayOutWorldEvent(2001, (int) Math.round(chest.getLocation().getX()), (int) Math.round(chest.getLocation().getY()), (int) Math.round(chest.getLocation().getZ()), 54, false);
					((CraftServer) Main.plugin.getServer()).getServer().getPlayerList().sendPacketNearby(chest.getLocation().getX(), chest.getLocation().getY(), chest.getLocation().getZ(), 24, ((CraftWorld) chest.getWorld()).getHandle().dimension, particles);
					return;
				}
				if(loot.getViewers().size() > 0) {
					for(HumanEntity he : loot.getViewers()) {
						if(he instanceof Player) {
							Player phe = (Player) he;
							phe.closeInventory();
							phe.playSound(phe.getLocation(), Sound.CHEST_CLOSE, 1F, 1F);
						}
					}
				}
				p.playSound(p.getLocation(), Sound.ZOMBIE_WOODBREAK, 0.5F, 1.2F);
				chest.setType(Material.AIR);
				Packet particles = new PacketPlayOutWorldEvent(2001, (int) Math.round(chest.getLocation().getX()), (int) Math.round(chest.getLocation().getY()), (int) Math.round(chest.getLocation().getZ()), 54, false);
				((CraftServer) Main.plugin.getServer()).getServer().getPlayerList().sendPacketNearby(chest.getLocation().getX(), chest.getLocation().getY(), chest.getLocation().getZ(), 24, ((CraftWorld) chest.getWorld()).getHandle().dimension, particles);
				InstanceMechanics.instance_loot_inv.get(p.getWorld().getName()).remove(chest.getLocation());
				
				for(ItemStack i : loot.getContents()) {
					if(i == null) {
						continue;
					}
					chest.getWorld().dropItem(chest.getLocation(), i);
				}
				return;
			}
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerInteractEvent(PlayerInteractEvent e) {
		
		if(e.getAction() == Action.RIGHT_CLICK_BLOCK && e.hasBlock() && e.getClickedBlock().getType() == Material.GLOWSTONE) {
			Player p = e.getPlayer();
			if(p.isOp() && loot_spawns.containsKey(e.getClickedBlock().getLocation())) {
				p.sendMessage(ChatColor.GRAY + "DEBUG: " + loot_spawns.get(e.getClickedBlock().getLocation()));
			}
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onPlayerOpenLootChest(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		
		if(e.getAction() == Action.RIGHT_CLICK_BLOCK && e.hasBlock() && (e.getClickedBlock().getType() == Material.CHEST || e.getClickedBlock().getType() == Material.TRAPPED_CHEST)) {
			if(e.getClickedBlock().getWorld().getName().equalsIgnoreCase(Bukkit.getWorlds().get(0).getName()) && !isLootChest(e.getClickedBlock()) && !(ShopMechanics.isShop(e.getClickedBlock()))) {
				p.sendMessage(ChatColor.GRAY + "The chest is locked.");
				e.setCancelled(true);
				return;
			}
			if(!isLootChest(e.getClickedBlock()) && !(isInstanceLootChest(e.getClickedBlock()))) { return; }
			if(isMonsterNearPlayer(p, 10)) {
				p.sendMessage(ChatColor.RED + "It is " + ChatColor.BOLD + "NOT" + ChatColor.RED + " safe to open that right now.");
				p.sendMessage(ChatColor.GRAY + "Eliminate the monsters in the area first.");
				e.setCancelled(true);
				return;
			}
			Location l = e.getClickedBlock().getLocation();
			Block chest = l.getBlock();
			
			if(!accessed_chests.contains(chest.getLocation())) {
				accessed_chests.add(chest.getLocation());
				last_loot_spawn_tick.put(l, (System.currentTimeMillis()));
				loot_chests_to_spawn.put(chest.getLocation(), loot_spawns.get(chest.getLocation()));
				//log.info("[LootMechanics] Player accessed chest at " + e.getClickedBlock().getLocation().toString() + ", queued for reshuffle.");
			}
			
			e.setCancelled(true);
			last_opened_loot_chest.put(p, l);
			
			if(loot_chest_inv.containsKey(l) && p != null) {
				p.playSound(p.getLocation(), Sound.CHEST_OPEN, 1F, 1F);
				p.openInventory(loot_chest_inv.get(l));
			} else if(InstanceMechanics.instance_loot_inv.containsKey(p.getWorld().getName()) && InstanceMechanics.instance_loot_inv.get(p.getWorld().getName()).containsKey(l)) {
				p.playSound(p.getLocation(), Sound.CHEST_OPEN, 1F, 1F);
				p.openInventory(InstanceMechanics.instance_loot_inv.get(p.getWorld().getName()).get(l));
			}
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onBlockBreak(BlockBreakEvent e) {
		Location loc = e.getBlock().getLocation();
		if(loot_spawns.containsKey(loc) || (InstanceMechanics.instance_loot.containsKey(loc.getWorld().getName()) && InstanceMechanics.instance_loot.get(loc.getWorld().getName()).containsKey(loc))) {
			if(loc.getBlock().getType() == Material.GLOWSTONE) {
				// Unregister the loot spawner!
				Player p = e.getPlayer();
				if(!(p.isOp())) { return; }
				
				if(InstanceMechanics.isInstance(p.getWorld().getName())) {
					HashMap<Location, String> instance_loot_copy = new HashMap<Location, String>();
					if(InstanceMechanics.instance_loot.containsKey(p.getWorld().getName())) {
						instance_loot_copy = InstanceMechanics.instance_loot.get(p.getWorld().getName());
					}
					instance_loot_copy.remove(loc);
					InstanceMechanics.instance_loot.put(p.getWorld().getName(), instance_loot_copy);
				} else {
					loot_spawns.remove(loc);
				}
				
				p.sendMessage(ChatColor.GREEN + "LOOT CHEST SPAWNER LOCATION UNREGISTERED.");
				p.sendMessage(ChatColor.GRAY + "DEBUG: " + loot_spawns.get(loc));
			}
		} else if(loc.getBlock().getType() == Material.GLOWSTONE) {
			Player p = e.getPlayer();
			if(!(p.isOp())) { return; }
			if(!(loot_spawn_location.containsKey(p))) {
				e.setCancelled(true);
				p.sendMessage(ChatColor.YELLOW + "You did not initiate this loot spawner's registration, therfore you cannot destroy it.");
				p.sendMessage(ChatColor.GRAY + "It will automatically delete on a server reboot.");
				return;
			}
			loot_spawn_step.remove(p);
			loot_spawn_data.remove(p);
			loot_spawn_location.get(p).getBlock().setType(Material.AIR);
			loot_spawn_location.remove(p);
			p.sendMessage(ChatColor.RED + "New loot chest spawn creation cancelled.");
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onBlockPlaceEvent(BlockPlaceEvent e) {
		if(e.getBlockPlaced().getType() == Material.GLOWSTONE) {
			Player p = e.getPlayer();
			if(!(p.isOp())) { return; }
			Location loc = e.getBlockPlaced().getLocation();
			if(loot_spawns.containsKey(loc) || (InstanceMechanics.instance_loot.containsKey(loc.getWorld().getName()) && InstanceMechanics.instance_loot.get(loc.getWorld().getName()).containsKey(loc))) {
				p.sendMessage(ChatColor.RED + "A loot chest spawn point is registered at this location. Block placement cancelled.");
				p.sendMessage(ChatColor.GRAY + "Use " + ChatColor.BOLD + "/showloot <radius>" + ChatColor.GRAY + " to visually view the loot spawn points.");
				e.setCancelled(true);
				return;
			}
			
			if(loot_spawn_step.containsKey(p)) {
				e.setCancelled(true);
				p.sendMessage(ChatColor.RED + "You already have a pending loot spawner registration.");
				p.sendMessage(ChatColor.GRAY + "Relogging will clear all loot spawner registrations");
				return;
			}
			
			loot_spawn_step.put(p, 0);
			loot_spawn_data.put(p, "");
			loot_spawn_location.put(p, loc);
			
			p.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + "NEW LOOT CHEST SPAWNER PLACED.");
			p.sendMessage(ChatColor.YELLOW + "Step 1 of 2: " + ChatColor.WHITE + "Please enter the LOOT TEMPLATE that this chest should use.");
			p.sendMessage(ChatColor.YELLOW + "Available Templates: " + ChatColor.WHITE + getAvailableTemplates());
			p.sendMessage(ChatColor.GRAY + "EX: " + ChatColor.DARK_GRAY + "test" + ChatColor.GRAY + " -> Will use the 'test.dat' data file to spawn random loot.");
		}
	}
	
	@EventHandler
	public void onAsyncChatEvent(AsyncPlayerChatEvent e) {
		Player p = e.getPlayer();
		if(!(loot_spawn_step.containsKey(p))) { return; }
		
		e.setCancelled(true);
		
		if(e.getMessage().equalsIgnoreCase("cancel")) {
			loot_spawn_step.remove(p);
			loot_spawn_data.remove(p);
			loot_spawn_location.get(p).getBlock().setType(Material.AIR);
			loot_spawn_location.remove(p);
			
			p.sendMessage(ChatColor.RED + "Loot spawner placement cancelled. No changes saved.");
			return;
		}
		
		int step = loot_spawn_step.get(p);
		String loot_data = loot_spawn_data.get(p);
		
		if(step == 0) {
			if(!(doesTemplateExist(e.getMessage()))) {
				loot_spawn_step.remove(p);
				loot_spawn_data.remove(p);
				loot_spawn_location.get(p).getBlock().setType(Material.AIR);
				loot_spawn_location.remove(p);
				
				p.sendMessage(ChatColor.RED + "The template '" + e.getMessage() + "' does not exist.");
				return;
			}
			
			loot_data = "@" + e.getMessage() + ".loot" + "@";
			
			if(InstanceMechanics.isInstance(p.getWorld().getName())) {
				loot_data += p.getWorld().getName() + "$";
				loot_spawn_step.put(p, 2);
				p.sendMessage("");
				p.sendMessage(ChatColor.YELLOW + "Ok, looks like you're in an instance! How exciting! Since this chest will never respawn, you don't need to specify a respawn time.");
				
				HashMap<Location, String> instance_loot_copy = new HashMap<Location, String>();
				if(InstanceMechanics.instance_loot.containsKey(p.getWorld().getName())) {
					instance_loot_copy = InstanceMechanics.instance_loot.get(p.getWorld().getName());
				}
				instance_loot_copy.put(loot_spawn_location.get(p), e.getMessage() + ".loot");
				InstanceMechanics.instance_loot.put(p.getWorld().getName(), instance_loot_copy);
				
				loot_spawn_step.remove(p);
				loot_spawn_data.remove(p);
				Location loot_spawner_loc = loot_spawn_location.get(p);
				loot_spawner_loc.getBlock().setType(Material.AIR);
				loot_spawn_location.remove(p);
				p.sendMessage(ChatColor.GREEN + "Loot Chest registered for instance '" + p.getWorld().getName() + "'.");
			} else {
				loot_spawn_step.put(p, 1);
				p.sendMessage("");
				p.sendMessage(ChatColor.YELLOW + "Step 2 of 2: " + ChatColor.WHITE + "Please enter the interval (in seconds) between each loot chest spawn event.");
				p.sendMessage(ChatColor.YELLOW + "Note: The countdown for a chest respawning starts AFTER the chest has been accessed at least once.");
				p.sendMessage(ChatColor.GRAY + "EX: " + ChatColor.DARK_GRAY + "300" + ChatColor.GRAY + " -> Chance for chest to respawn with new loot every 300 seconds / 5 minutes.");
			}
			
			loot_spawn_data.put(p, loot_data);
			return;
		}
		
		if(step == 1) {
			int spawn_interval = 60; // Default 60 seconds if something goes wrong.
			try {
				spawn_interval = Integer.parseInt(e.getMessage());
				if(spawn_interval <= 0) {
					p.sendMessage(ChatColor.RED + "You must enter a valid NUMBER greater than 0 for the spawning interval (in seconds).");
					return;
				}
			} catch(NumberFormatException nfe) {
				p.sendMessage(ChatColor.RED + "You must enter a valid NUMBER for the spawning interval (in seconds).");
				return;
			}
			
			loot_data += spawn_interval + "#";
			loot_spawn_step.remove(p);
			loot_spawn_data.remove(p);
			//loot_loot_data.put(p, loot_data);
			
			Location loot_spawner_loc = loot_spawn_location.get(p);
			loot_spawns.put(loot_spawner_loc, loot_data);
			
			loot_spawner_loc.getBlock().setType(Material.AIR);
			loot_spawn_location.remove(p);
			p.sendMessage("");
			p.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "LOOT SPAWNER REGISTRATION COMPLETE.");
			p.sendMessage(ChatColor.GREEN + "The new loot chest spawn data has been synced with the loot chest thread.");
			return;
		}
		
	}
	
}