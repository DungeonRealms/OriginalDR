package me.vaqxine.MonsterMechanics;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

import me.vaqxine.Main;
import me.vaqxine.AchievmentMechanics.AchievmentMechanics;
import me.vaqxine.BossMechanics.BossMechanics;
import me.vaqxine.DuelMechanics.DuelMechanics;
import me.vaqxine.EnchantMechanics.EnchantMechanics;
import me.vaqxine.HealthMechanics.HealthMechanics;
import me.vaqxine.Hive.Hive;
import me.vaqxine.Hive.ParticleEffect;
import me.vaqxine.InstanceMechanics.InstanceMechanics;
import me.vaqxine.ItemMechanics.ItemGenerators;
import me.vaqxine.ItemMechanics.ItemMechanics;
import me.vaqxine.MoneyMechanics.MoneyMechanics;
import me.vaqxine.MonsterMechanics.commands.CommandHideMS;
import me.vaqxine.MonsterMechanics.commands.CommandMon;
import me.vaqxine.MonsterMechanics.commands.CommandMonSpawn;
import me.vaqxine.MonsterMechanics.commands.CommandShowMS;
import me.vaqxine.PartyMechanics.PartyMechanics;
import me.vaqxine.PetMechanics.PetMechanics;
import me.vaqxine.ProfessionMechanics.ProfessionMechanics;
import me.vaqxine.RealmMechanics.RealmMechanics;
import me.vaqxine.RecordMechanics.RecordMechanics;
import me.vaqxine.RepairMechanics.RepairMechanics;
import me.vaqxine.TeleportationMechanics.TeleportationMechanics;
import me.vaqxine.enums.CC;
import me.vaqxine.enums.Delay;
import net.minecraft.server.v1_7_R2.DataWatcher;
import net.minecraft.server.v1_7_R2.EntityCreature;
import net.minecraft.server.v1_7_R2.EntityLiving;
import net.minecraft.server.v1_7_R2.EntityPlayer;
import net.minecraft.server.v1_7_R2.GenericAttributes;
import net.minecraft.server.v1_7_R2.NBTTagCompound;
import net.minecraft.util.io.netty.util.internal.ConcurrentSet;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.EntityEffect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_7_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_7_R2.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_7_R2.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_7_R2.entity.CraftSkeleton;
import org.bukkit.craftbukkit.v1_7_R2.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Blaze;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Creature;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Horse.Color;
import org.bukkit.entity.Horse.Variant;
import org.bukkit.entity.LargeFireball;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.MagmaCube;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.Ocelot.Type;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.SmallFireball;
import org.bukkit.entity.Snowball;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.entity.Witch;
import org.bukkit.entity.WitherSkull;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTargetEvent.TargetReason;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class MonsterMechanics implements Listener {
	
	// Used for E-CASH items {
	public static volatile boolean loot_buff = false;
	public static volatile long loot_buff_timeout = 0L;
	// }
	
	public static volatile CopyOnWriteArrayList<Chunk> chunk_copy = new CopyOnWriteArrayList<Chunk>();
	// Copy of all loaded chunks -- used for multithread operations.
	
	// Mob Attributes -- health, armor, damage, tier, etc.
	public static ConcurrentHashMap<Entity, Integer> mob_health = new ConcurrentHashMap<Entity, Integer>();
	static HashMap<Entity, Integer> max_mob_health = new HashMap<Entity, Integer>();
	public static HashMap<Entity, List<Integer>> mob_damage = new HashMap<Entity, List<Integer>>();
	public static HashMap<Entity, Integer> mob_armor = new HashMap<Entity, Integer>();
	static HashMap<Entity, Integer> mob_tier = new HashMap<Entity, Integer>();
	public static HashMap<Entity, List<ItemStack>> mob_loot = new HashMap<Entity, List<ItemStack>>();
	public static HashMap<Entity, Integer> mob_level = new HashMap<Entity, Integer>();
	// Special mob attacks START.
	static ConcurrentHashMap<Entity, Integer> special_attack = new ConcurrentHashMap<Entity, Integer>();
	static ConcurrentHashMap<Entity, Integer> power_strike = new ConcurrentHashMap<Entity, Integer>();
	static ConcurrentHashMap<Entity, Integer> whirlwind = new ConcurrentHashMap<Entity, Integer>();
	static ConcurrentHashMap<Entity, Float> mob_yaw = new ConcurrentHashMap<Entity, Float>();
	// Special mob attacks END.
	
	public static ConcurrentHashMap<Entity, String> mob_target = new ConcurrentHashMap<Entity, String>();
	// Player name of mob target.
	
	static ConcurrentHashMap<Location, List<Entity>> local_spawned_mobs = new ConcurrentHashMap<Location, List<Entity>>();
	// Chunk 0,0 location, list of all living entities the system is aware of.
	
	public static volatile ConcurrentHashMap<Location, String> mob_spawns = new ConcurrentHashMap<Location, String>();
	// ALL mob spawns are held within this hashmap, it is not accessed often -- used as a reference point.
	
	static HashMap<Location, List<String>> mob_to_spawn = new HashMap<Location, List<String>>();
	// Mobs that have been selected to go through Phase #2 selection to determine if they should be added to mobs_being_spawned and loaded into the main thread.
	
	public static ConcurrentHashMap<Entity, String> mob_spawn_ownership = new ConcurrentHashMap<Entity, String>(); // Location:mob_num
	// Entity, Location(x,y,z):mob_num -- information to track entities and to know which to respawn when they're killed.
	
	static ConcurrentHashMap<Location, List<Integer>> spawned_mobs = new ConcurrentHashMap<Location, List<Integer>>();
	// Inverse of mob_spawn_ownership, give a location of a mob spawner and it will return which mob_num's are currently alive.
	
	// Data stored throughout the process of placing a mob spawner.
	static HashMap<Player, Integer> mob_spawn_step = new HashMap<Player, Integer>();
	static HashMap<Player, String> mob_spawn_data = new HashMap<Player, String>();
	static HashMap<Player, Location> mob_spawn_location = new HashMap<Player, Location>();
	
	public static volatile ConcurrentHashMap<Location, List<String>> loaded_mobs = new ConcurrentHashMap<Location, List<String>>();
	// Mob Spawner Location, The spawning string -- this map will only contain mob spawner locations of mobs in loaded chunks.
	// Used in mobs_to_spawn to save memory and skip masses of already spawned mobs.
	
	public static HashMap<Entity, Long> mob_last_hit = new HashMap<Entity, Long>();
	// The last time a mob hit a player. (or tried)
	
	public static ConcurrentHashMap<Entity, Long> mob_last_hurt = new ConcurrentHashMap<Entity, Long>();
	// The last time a mob took damage.
	
	public static HashMap<String, Long> player_slow = new HashMap<String, Long>();
	// Player Name, Time of last engagement -- used to apply the "combat slowness" effect.
	
	public static HashMap<Location, Long> mob_spawn_check_delay = new HashMap<Location, Long>();
	// Saves memory. Don't try to spawn mobs that are in chunks that have no players within a far distance.
	// Location of Spawner, Last Check
	
	public static volatile ConcurrentHashMap<String, Location> player_locations = new ConcurrentHashMap<String, Location>();
	// Used in MULTIPLE plugins as a thread safe way to access player location.
	
	//static HashMap<Chunk, Entity[]> unload_chunks = new HashMap<Chunk, Entity[]>();
	public ConcurrentSet<Entity> pathing_away = new ConcurrentSet<Entity>();
	public static List<Entity> no_delay_kills = new ArrayList<Entity>();
	// List of monsters to NOT have a spawn delay to come back. Used for chunk unloading/loading.
	
	public CopyOnWriteArrayList<Location> chunks_to_unload = new CopyOnWriteArrayList<Location>();
	// 0,0 Chunk locations of chunks that are being passed along to the main thread for unloading. Phase #2 unload.
	
	public static CopyOnWriteArrayList<Entity> entities_to_kill = new CopyOnWriteArrayList<Entity>();
	// A list of entities being passed along to the main thread for removal. Normally as a result of chunk unload.
	
	public static volatile CopyOnWriteArrayList<Location> loaded_chunks = new CopyOnWriteArrayList<Location>();
	// List of loaded chunks for cross-thread access.
	
	static HashMap<Location, Long> recent_loaded_chunks = new HashMap<Location, Long>();
	// List of chunks that have been loaded recently, prevents infinite loading loops.
	
	static HashMap<String, String> passive_hunter_achiev = new HashMap<String, String>();
	// Kill 1x of every passive animal.
	
	static HashMap<String, List<String>> mob_messages = new HashMap<String, List<String>>();
	// Mob Type, List of possible messages for it to send to players onEntityTarget();
	
	public static ConcurrentHashMap<Entity, Location> entities_to_remove = new ConcurrentHashMap<Entity, Location>();
	// A list of entities being passed along to the main thread for reset. Normally a pathback.
	
	public static HashMap<Entity, Long> last_respawn = new HashMap<Entity, Long>();
	// A list of entities being passed along to the main thread for reset. Normally a pathback.
	
	static ConcurrentHashMap<String, Long> last_mob_message_get = new ConcurrentHashMap<String, Long>();
	// Player Name, Last time they got a mob message -- prevents message spam.
	
	public static ArrayList<ArrayList<Object>> mobs_being_spawned = new ArrayList<ArrayList<Object>>();
	// Main thread-safe data for spawning mobs. Once a mob is on this list, it will be spawned momentarily.
	
	public static volatile CopyOnWriteArrayList<EntityTargetEvent> async_entity_target = new CopyOnWriteArrayList<EntityTargetEvent>();
	// Main thread-safe data for spawning mobs. Once a mob is on this list, it will be spawned momentarily.
	
	public static ConcurrentHashMap<Player, String> async_message_send = new ConcurrentHashMap<Player, String>();
	// Main thread-safe data for spawning mobs. Once a mob is on this list, it will be spawned momentarily.
	
	List<Location> never_unload_chunks = new ArrayList<Location>();
	// High priority chunks that contain T4/T5. Prevents abuse.
	
	public static ConcurrentHashMap<String, List<String>> custom_mob_loot_tables = new ConcurrentHashMap<String, List<String>>();
	// CUSTOM_MOB_NAME, LIST OF DROP TABLE RULES (loot mechanics)
	
	public static List<Entity> ignore_target_event = new ArrayList<Entity>();
	// Prevents lag from entitytarget() event.
	
	public static List<Entity> no_pathing = new ArrayList<Entity>();
	// Entites that no longer use cleanupMonsters() to prevent loops.
	
	public static CopyOnWriteArrayList<Enderman> enderman_list = new CopyOnWriteArrayList<Enderman>();
	// Used for AoE enderman effects
	
	public static CopyOnWriteArrayList<Entity> approaching_mage_list = new CopyOnWriteArrayList<Entity>();
	// Magic weilder approaching, entity they have as a target.
	
	//static HashMap<Entity, Entity> approaching_mage_list = new HashMap<Entity, Entity>();
	// Magic weilder approaching, entity they have as a target.
	
	public static String templatePath_s = "plugins/MonsterMechanics/mob_messages/";
	public static File templatePath = new File(templatePath_s);
	
	static Logger log = Logger.getLogger("Minecraft");
	
	public String main_world_name = "";
	public int player_count = 0;
	
	static MonsterMechanics instance = null;
	
	@SuppressWarnings("deprecation")
	public void onEnable() {
		//loadNewEntities();
		instance = this;
		Main.plugin.getServer().getPluginManager().registerEvents(this, Main.plugin);
		
		Main.plugin.getCommand("hidems").setExecutor(new CommandHideMS());
		Main.plugin.getCommand("mon").setExecutor(new CommandMon());
		Main.plugin.getCommand("monspawn").setExecutor(new CommandMonSpawn());
		Main.plugin.getCommand("showms").setExecutor(new CommandShowMS());
		
		Main.plugin.getServer().getWorld("DungeonRealms").setGameRuleValue("doMobLoot", "false");
		Main.plugin.getServer().getWorld("DungeonRealms").setGameRuleValue("mobGriefing", "false");
		// TODO: See if this even works.
		CustomEntityType.registerEntities();
		loadMobSpawnerData();
		loadMobMessageTemplates();
		loadCustomMobDrops();
		
		main_world_name = Bukkit.getWorlds().get(0).getName();
		new File("MonsterMechaniccs/custom_mobs").mkdirs();
		
		Bukkit.getWorlds().get(0).setAutoSave(false);
		Bukkit.getWorlds().get(0).setKeepSpawnInMemory(false);
		
		Thread asyncEntTarget = new asyncEntityTarget();
		asyncEntTarget.start();
		
		Main.plugin.getServer().getScheduler().scheduleSyncRepeatingTask(Main.plugin, new Runnable() {
			public void run() {
				if(loot_buff == true) {
					if((System.currentTimeMillis() - loot_buff_timeout) > 0) {
						// Time to stop the fun.
						loot_buff = false;
						Bukkit.broadcastMessage(ChatColor.GOLD + "" + ChatColor.BOLD + ">> " + ChatColor.GOLD + "The " + ChatColor.UNDERLINE + "+20% Global Drop Rates" + ChatColor.GOLD + " has expired.");
					}
				}
			}
		}, 15 * 20L, 1 * 20L);
		
		Main.plugin.getServer().getScheduler().scheduleSyncRepeatingTask(Main.plugin, new Runnable() {
			public void run() {
				for(Enderman end : enderman_list) {
					if(end != null && end.getHealth() > 0 && !end.isDead()) {
						try {
							ParticleEffect.sendToLocation(ParticleEffect.WITCH_MAGIC, end.getLocation().add(0, 1.5, 0), new Random().nextFloat(), new Random().nextFloat(), new Random().nextFloat(), 2F, 50);
							ParticleEffect.sendToLocation(ParticleEffect.PORTAL, end.getLocation().add(0, 0.5, 0), new Random().nextFloat(), new Random().nextFloat(), new Random().nextFloat(), 1F, 20);
						} catch(Exception err) {
							err.printStackTrace();
						}
						//end.getLocation().getWorld().spawnParticle(end.getLocation().add(0, 1.5, 0), Particle.WITCH_MAGIC, 2F, 50);
						//end.getLocation().getWorld().spawnParticle(end.getLocation().add(0, 0.5, 0), Particle.PORTAL, 1F, 20);
						
						int random_n = mob_damage.get((Entity) end).get(1) - mob_damage.get((Entity) end).get(0);
						if(random_n <= 0) {
							random_n = mob_damage.get((Entity) end).get(1);
						}
						int dmg = new Random().nextInt(random_n) + mob_damage.get((Entity) end).get(0);
						dmg = dmg / 2;
						for(Entity ent : end.getNearbyEntities(4, 4, 4)) {
							if(ent instanceof Player) {
								Player pl = (Player) ent;
								pl.damage(dmg, end);
								pl.playSound(pl.getLocation(), Sound.ENDERMAN_STARE, 1F, 1F);
								try {
									ParticleEffect.sendToLocation(ParticleEffect.WITCH_MAGIC, pl.getLocation().add(0, 2, 0), new Random().nextFloat(), new Random().nextFloat(), new Random().nextFloat(), 1F, 50);
								} catch(Exception err) {
									err.printStackTrace();
								}
								//pl.getWorld().spawnParticle(pl.getLocation().add(0, 2, 0), Particle.WITCH_MAGIC, 1F, 50);
							}
						}
					} else {
						enderman_list.remove(end);
					}
				}
			}
		}, 15 * 20L, 40L);
		
		Main.plugin.getServer().getScheduler().scheduleSyncRepeatingTask(Main.plugin, new Runnable() {
			public void run() {
				for(Entity ent : approaching_mage_list) {
					try {
						if(ent != null && !ent.isDead() && ent instanceof Creature && MonsterMechanics.mob_health.containsKey(ent)) {
							LivingEntity le = (LivingEntity) ent;
							ItemStack weapon = le.getEquipment().getItemInHand();
							Creature c = (Creature) le;
							
							if(c.getTarget() != null) {
								LivingEntity target = c.getTarget();
								if(!target.getWorld().getName().equalsIgnoreCase(ent.getWorld().getName())) {
									continue;
								}
								double distance = target.getLocation().distanceSquared(ent.getLocation());
								lookAtEntity(target, (EntityLiving) ((CraftEntity) ent).getHandle());
								
								if(ent.hasMetadata("boss_type")) {
									String boss_type = ent.getMetadata("boss_type").get(0).asString();
									if(boss_type.equalsIgnoreCase("tnt_bandit") && new Random().nextInt(2) == 0) {
										Projectile pj = null;
										pj = le.launchProjectile(ThrownPotion.class);
										ItemMechanics.projectile_map.put(pj, null);
										continue;
									}
								}
								
								if(distance > 4 && distance <= 144 && ((180 - Math.abs(target.getLocation().getYaw() - le.getLocation().getYaw())) <= 15)) {
									Projectile pj = null;
									ItemStack weapon_is = new ItemStack(Material.WOOD_SWORD);
									ItemMeta im = weapon.getItemMeta();
									weapon_is.setItemMeta(im);
									
									if(ItemMechanics.getItemTier(weapon_is) == 1) {
										pj = le.launchProjectile(Snowball.class);
									}
									if(ItemMechanics.getItemTier(weapon_is) == 2) {
										pj = le.launchProjectile(SmallFireball.class);
									}
									if(ItemMechanics.getItemTier(weapon_is) == 3) {
										pj = le.launchProjectile(EnderPearl.class);
										pj.setVelocity(pj.getVelocity().multiply(1.25));
									}
									if(ItemMechanics.getItemTier(weapon_is) == 4) {
										pj = le.launchProjectile(WitherSkull.class);
									}
									if(ItemMechanics.getItemTier(weapon_is) == 5) {
										pj = le.launchProjectile(LargeFireball.class);
										pj.setVelocity(pj.getVelocity().multiply(2));
									}
									
									ItemMechanics.projectile_map.put(pj, weapon);
								}
							} else {
								approaching_mage_list.remove(ent);
								// No current target, asyncTarget will kick in again.
							}
						} else {
							approaching_mage_list.remove(ent);
						}
					} catch(IllegalArgumentException err) {
						//err.printStackTrace(); TOOD: Fix this, world names are same but UUId different?
						approaching_mage_list.remove(ent);
						continue;
					}
				}
			}
		}, 15 * 20L, 1 * 25L);
		
		Main.plugin.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
			public void run() {
				clearAllEntities();
			}
		}, 2 * 20L);
		
		Main.plugin.getServer().getScheduler().scheduleAsyncDelayedTask(Main.plugin, new Runnable() {
			public void run() {
				populateSpawnList();
			}
		}, 6 * 20L);
		
		// REPEATING TASKS:
		Main.plugin.getServer().getScheduler().scheduleSyncRepeatingTask(Main.plugin, new Runnable() {
			public void run() {
				logRecentChunks();
				processChunks();
			}
		}, 20 * 20L, 10 * 20L);
		
		Main.plugin.getServer().getScheduler().scheduleSyncRepeatingTask(Main.plugin, new Runnable() {
			public void run() {
				processPossibleMobSpawns();
				MobSpawnEvent();
			}
		}, 9 * 20L, 40L);
		
		Main.plugin.getServer().getScheduler().scheduleSyncRepeatingTask(Main.plugin, new Runnable() {
			public void run() {
				unloadChunks();
			}
		}, 21 * 20L, 15 * 20L);
		
		Main.plugin.getServer().getScheduler().scheduleSyncRepeatingTask(Main.plugin, new Runnable() {
			public void run() {
				spawnMobsIn();
				// Spawns all mobs whose data have been selected to load from processPossibleMobSpawns();
			}
		}, 10 * 20L, 10L);
		
		Main.plugin.getServer().getScheduler().scheduleAsyncRepeatingTask(Main.plugin, new Runnable() {
			public void run() {
				if(async_message_send.size() > 0) {
					for(Entry<Player, String> data : async_message_send.entrySet()) {
						Player pl = data.getKey();
						String msg = data.getValue();
						pl.sendMessage(msg);
						async_message_send.remove(pl);
					}
				}
			}
		}, 5 * 20L, 1L);
		
		// DEPRECIATED, merged into cleanupMonsters();
		/*this.getServer().getScheduler()
				.scheduleAsyncRepeatingTask(this, new Runnable() {
					public void run() {
						pathbackEvent();
						// Adds mobs who are out of range of their spawner to entities_to_remove
					}
				}, 12 * 20L, 4 * 20L);*/
		
		Main.plugin.getServer().getScheduler().scheduleAsyncRepeatingTask(Main.plugin, new Runnable() {
			public void run() {
				updateNametags();
				updateParticles();
			}
		}, 15 * 20L, 20L);
		
		Main.plugin.getServer().getScheduler().scheduleSyncRepeatingTask(Main.plugin, new Runnable() {
			public void run() {
				cleanupMonsters();
				// Add mobs who have no target, null target, etc. to entities_to_remove
			}
		}, 15 * 20L, 20L);
		
		Main.plugin.getServer().getScheduler().scheduleAsyncRepeatingTask(Main.plugin, new Runnable() {
			public void run() {
				ignore_target_event.clear();
			}
		}, 10 * 20L, 20L);
		
		Main.plugin.getServer().getScheduler().scheduleSyncRepeatingTask(Main.plugin, new Runnable() {
			public void run() {
				removeListEntities(); // Teleport mobs back to spawn.
				killListEntities(); // Kills null mobs.
				// Removes all entities present in entities_to_remove
			}
		}, 13 * 20L, 10L);
		
		Main.plugin.getServer().getScheduler().scheduleSyncRepeatingTask(Main.plugin, new Runnable() {
			public void run() {
				fixStuckEntities();
				// Removes all entities present in entities_to_remove
			}
		}, 20 * 20L, 10L);
		
		// Is it even needed?
		Main.plugin.getServer().getScheduler()
			.scheduleSyncRepeatingTask(Main.plugin, new Runnable() {
				public void run() {
					removeNoHPMobs(); // Kill all mobs that are ghosts
				}
		}, 13 * 20L, 4 * 20L);
		
		// DEPRECIATED, added code to cleanupMobs() event.
		/*this.getServer().getScheduler()
			.scheduleSyncRepeatingTask(this, new Runnable() {
				public void run() {
					fixNullMobs();
				}
			}, 13 * 20L, 20L);*/
		
		// DEPRECIATED, running along with MobSpawnEvent() to keep stuff in sync.
		/*this.getServer().getScheduler()
				.scheduleSyncRepeatingTask(this, new Runnable() {
					public void run() {
						unloadChunks();
						// Custom chunk unloading.
						// Unloads all chunks that do not have a player within XXX blocks of them, adding all entities to the entities_to_remove list.
					}
		}, 12 * 20L, 40L);*/
		
		// DEPRECIATED, running along with processMobsToSpawn() to keep stuff in sync.
		/*this.getServer().getScheduler()
		.scheduleAsyncRepeatingTask(this, new Runnable() {
				public void run() {
					processChunks();
					// Populates chunks_to_unload used in unloadChunks();
				}	
		}, 12 * 20L, 5 * 20L);*/
		
		// DEPRECIATEd, running along with unloadChunks to keep a more accurate list.
		/*this.getServer().getScheduler()
		.scheduleAsyncRepeatingTask(this, new Runnable() {
				public void run() {
					populateLoadedChunks();
					// Populates loaded_chunks
				}	
		}, 5 * 20L, 20L);*/
		
		if(Bukkit.getMotd().contains("US-0")) {
			Main.plugin.getServer().getScheduler().scheduleAsyncRepeatingTask(Main.plugin, new Runnable() {
				public void run() {
					saveMobSpawnerData();
					// Backup event.
				}
			}, 800 * 20L, 800 * 20L);
		}
		
		Main.plugin.getServer().getScheduler().scheduleSyncRepeatingTask(Main.plugin, new Runnable() {
			public void run() {
				player_count = Bukkit.getOnlinePlayers().length;
				// Gets player count for mob density settings.
			}
		}, 30 * 20L, 60 * 20L);
		
		Main.plugin.getServer().getScheduler().scheduleAsyncRepeatingTask(Main.plugin, new Runnable() {
			public void run() {
				chunk_copy.clear();
				try {
					try {
						for(Chunk c : Main.plugin.getServer().getWorlds().get(0).getLoadedChunks()) {
							chunk_copy.add(c);
						}
					} catch(ConcurrentModificationException cme) {
						return;
					}
				} catch(NoSuchElementException nsee) {
					return;
				}
			}
		}, 12 * 20L, 2 * 20L);
		
		Main.plugin.getServer().getScheduler().scheduleSyncRepeatingTask(Main.plugin, new Runnable() {
			public void run() {
				// Combat 'slowness'
				List<String> to_remove = new ArrayList<String>();
				for(Entry<String, Long> data : player_slow.entrySet()) {
					String p_name = data.getKey();
					long time = data.getValue();
					if(System.currentTimeMillis() - time > 3000) { // 3 seconds have passed.
						if(Bukkit.getPlayer(p_name) != null) {
							Player pl = Bukkit.getPlayer(p_name);
							if(pl.isBlocking()) {
								continue; // don't bug out the block.
							}
						}
						to_remove.add(p_name);
					}
				}
				
				for(String s : to_remove) {
					player_slow.remove(s);
					if(Bukkit.getPlayer(s) != null) {
						Player pl = Bukkit.getPlayer(s);
						pl.setWalkSpeed(0.2F);
					}
				}
			}
		}, 10 * 20L, 1 * 20L);
		
		Main.plugin.getServer().getScheduler().scheduleAsyncRepeatingTask(Main.plugin, new Runnable() {
			public void run() {
				// Custom mob messages.
				for(String s : last_mob_message_get.keySet()) {
					if((System.currentTimeMillis() - last_mob_message_get.get(s)) > (30 * 1000)) {
						last_mob_message_get.remove(s);
					}
				}
			}
		}, 10 * 20L, 1 * 20L);
		
		// Power Strike custom attack.
		Main.plugin.getServer().getScheduler().scheduleSyncRepeatingTask(Main.plugin, new Runnable() {
			public void run() {
				tickPowerStrike();
			}
		}, 10 * 20L, 10L);
		
		// Whirlwind custom attack.
		Main.plugin.getServer().getScheduler().scheduleSyncRepeatingTask(Main.plugin, new Runnable() {
			public void run() {
				tickWhirlwind();
			}
		}, 10 * 20L, 20L);
		
		// Whirlwind custom attack.
		Main.plugin.getServer().getScheduler().scheduleSyncRepeatingTask(Main.plugin, new Runnable() {
			public void run() {
				twirlWhirldwind();
			}
		}, 10 * 20L, 1L);
		
		/*this.getServer().getScheduler().scheduleAsyncRepeatingTask(this, new Runnable() {
			public void run() {
				for(LivingEntity le : getServer().getWorlds().get(0).getLivingEntities()){
					if(le instanceof Zombie){
						Zombie z = (Zombie)le;
						if(update_goalselector.containsKey(le)){
							EntityZombie ez = ((CraftZombie)z).getHandle();
							ez.m();
							if((System.currentTimeMillis() - update_goalselector.get(le)) > 500){
								update_goalselector.remove(le);
								continue;
							}
						}
						if(z.getTarget() != null){
							LivingEntity target = z.getTarget();
							double y_diff = Math.abs(target.getLocation().getY() - z.getLocation().getY());
							if(y_diff >= 1.0D){
								update_goalselector.put(le, System.currentTimeMillis());
								//EntityZombie ez = ((CraftZombie)z).getHandle();
								//ez.m();
							}
						}
					}
					if(le instanceof org.bukkit.entity.Skeleton){
						Skeleton s = (Skeleton)le;
						if(s.getTarget() != null){
							EntitySkeleton es = ((CraftSkeleton)s).getHandle();
							es.updateGoal();
						}
						if(update_goalselector.containsKey(le)){
							EntitySkeleton es = ((CraftSkeleton)s).getHandle();
							es.updateGoal();
							if((System.currentTimeMillis() - update_goalselector.get(le)) > 500){
								update_goalselector.remove(le);
								continue;
							}
						}
						if(s.getTarget() != null){
							LivingEntity target = s.getTarget();
							double y_diff = Math.abs(target.getLocation().getY() - s.getLocation().getY());
							if(y_diff >= 1.0D){
								update_goalselector.put(le, System.currentTimeMillis());
								//EntitySkeleton es = ((CraftSkeleton)s).getHandle();
								//es.updateGoal();
							}
						}
					}
				}
			}
		}, 10 * 20L, 20L);*/
		
		log.info("[MonsterMechanics] V1.0 has been enabled.");
	}
	
	public void onDisable() {
		clearAllEntities();
		saveMobSpawnerData();
		log.info("[MonsterMechanics] V1.0 has been disabled.");
	}
	
	public void lookAtEntity(Entity to_look_at, EntityLiving ent) {
		Location entLoc = ent.getBukkitEntity().getLocation(), target = to_look_at.getLocation();
		double xDiff = target.getX() - entLoc.getX();
		double yDiff = target.getY() - entLoc.getY();
		double zDiff = target.getZ() - entLoc.getZ();
		double DistanceXZ = Math.sqrt(xDiff * xDiff + zDiff * zDiff);
		double DistanceY = Math.sqrt(DistanceXZ * DistanceXZ + yDiff * yDiff);
		double newYaw = (Math.acos(xDiff / DistanceXZ) * 180 / Math.PI);
		double newPitch = (Math.acos(yDiff / DistanceY) * 180 / Math.PI) - 90;
		if(zDiff < 0.0) {
			newYaw = newYaw + (Math.abs(180 - newYaw) * 2);
		}
		ent.yaw = (float) (newYaw - 90);
		ent.pitch = (float) newPitch;
	}
	public static int getMaxMobHealth(Entity e){
	    if(max_mob_health.containsKey(e)){
	        return max_mob_health.get(e);
	    }
	    Main.d(e + " was not in the system..");
	    return 0;
	}
	public void tickPowerStrike() {
		List<Entity> to_remove = new ArrayList<Entity>();
		
		for(Entry<Entity, Integer> data : power_strike.entrySet()) {
			Entity ent = data.getKey();
			try {
				if(ent == null || ent.isDead() || !(mob_health.containsKey(ent)) || !(max_mob_health.containsKey(ent))) {
					to_remove.add(ent);
					continue;
				}
				int step = data.getValue();
				if(step <= 4) { // Charging...
					step += 1;
					power_strike.put(ent, step);
					special_attack.put(ent, step);
					boolean is_elite = false;
					ItemStack weapon = CraftItemStack.asBukkitCopy(((CraftEntity) ent).getHandle().getEquipment()[0]);
					if(weapon.getEnchantments().containsKey(Enchantment.KNOCKBACK)) {
						is_elite = true;
					}
					LivingEntity le = (LivingEntity) ent;
					le.setCustomName(generateOverheadBar(ent, mob_health.get(ent), max_mob_health.get(ent), getMobTier(ent), is_elite));
					ent.getWorld().playSound(ent.getLocation(), Sound.PISTON_EXTEND, 1F, 2.0F);
					continue;
				}
				if(step == 5) { // Ready!
					// Maintain particle effect.
					if(BossMechanics.boss_map.containsKey(ent)) {
						try {
							ParticleEffect.sendToLocation(ParticleEffect.WITCH_MAGIC, ent.getLocation().add(0, 0.5, 0), new Random().nextFloat(), new Random().nextFloat(), new Random().nextFloat(), 0.2F, 100);
						} catch(Exception err) {
							//err.printStackTrace();
						}
						//ent.getWorld().spawnParticle(ent.getLocation().add(0, 0.5, 0), Particle.WITCH_MAGIC, 0.2F, 100);
					} else {
						try {
						    ParticleEffect.sendToLocation(ParticleEffect.WITCH_MAGIC, ent.getLocation().add(0, 1, 0), new Random().nextFloat(), new Random().nextFloat(), new Random().nextFloat(), 0.5F, 35);
						} catch(Exception err) {
							//err.printStackTrace();
						}
					}
					
					boolean is_elite = false;
					ItemStack weapon = CraftItemStack.asBukkitCopy(((CraftEntity) ent).getHandle().getEquipment()[0]);
					if(weapon.getEnchantments().containsKey(Enchantment.KNOCKBACK)) {
						is_elite = true;
					}
					LivingEntity le = (LivingEntity) ent;
					le.setCustomName(generateOverheadBar(ent, mob_health.get(ent), max_mob_health.get(ent), getMobTier(ent), is_elite));
				}
			} catch(Exception err) {
				if(err instanceof NullPointerException) {
					err.printStackTrace();
					to_remove.add(ent);
				}
				continue;
			}
		}
		
		for(Entity ent : to_remove) {
			power_strike.remove(ent);
			special_attack.remove(ent);
		}
	}
	
	public static boolean hasCustomDrops(String custom_name) {
		if(custom_mob_loot_tables.containsKey(custom_name)) { return true; }
		return false;
	}
	
	public boolean hasCustomDrops(Entity ent) {
		if(!(ent instanceof LivingEntity)) { return false; }
		LivingEntity le = (LivingEntity) ent;
		if(!(le.hasMetadata("mobname"))) { return false; }
		//if(InstanceMechanics.isInstance(ent.getWorld().getName())){return false;}
		String custom_name = ChatColor.stripColor(le.getMetadata("mobname").get(0).asString());
		if(custom_mob_loot_tables.containsKey(custom_name)) { return true; }
		return false;
	}
	
	@SuppressWarnings("deprecation")
	public static List<ItemStack> getCustomDrops(Entity ent, String custom_name) {
		if(ent != null) {
			LivingEntity le = (LivingEntity) ent;
			if(!(le.hasMetadata("mobname"))) { return null; }
			
			custom_name = ChatColor.stripColor(le.getMetadata("mobname").get(0).asString());
		}
		
		if(custom_name == null) { return null; }
		
		List<ItemStack> loot = new ArrayList<ItemStack>();
		
		for(String s : custom_mob_loot_tables.get(custom_name)) {
			if(s.contains(" ")) {
				s = s.substring(0, s.indexOf(" "));
				if(s.equalsIgnoreCase("")) {
					continue;
				}
			}
			if(!(s.contains(":"))) {
				continue;
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
							loot.add(MoneyMechanics.signBankNote(money, ChatColor.GREEN.toString() + "Bank Note", ChatColor.WHITE.toString() + ChatColor.BOLD.toString() + "Value:" + ChatColor.WHITE.toString() + " " + amount_to_spawn + " Gems" + "," + ChatColor.GRAY.toString() + "Exchange at any bank for GEM(s)"));
						} else if(amount_to_spawn <= 64) {
							loot.add(MoneyMechanics.makeGems(amount_to_spawn));
						}
					} else if(m == Material.POTION) {
						if(item_meta == 1) {
							loot.add(ItemMechanics.signNewCustomItem(Material.POTION, (short) 1, ChatColor.WHITE.toString() + "Minor Health Potion", ChatColor.GRAY.toString() + "A potion that restores " + ChatColor.WHITE.toString() + "15HP"));
						}
						if(item_meta == 5) {
							loot.add(ItemMechanics.signNewCustomItem(Material.POTION, (short) 5, ChatColor.GREEN.toString() + "Health Potion", ChatColor.GRAY.toString() + "A potion that restores " + ChatColor.GREEN.toString() + "75HP"));
						}
						if(item_meta == 9) {
							loot.add(ItemMechanics.signNewCustomItem(Material.POTION, (short) 9, ChatColor.AQUA.toString() + "Major Health Potion", ChatColor.GRAY.toString() + "A potion that restores " + ChatColor.AQUA.toString() + "300HP"));
						}
						if(item_meta == 12) {
							loot.add(ItemMechanics.signNewCustomItem(Material.POTION, (short) 12, ChatColor.LIGHT_PURPLE.toString() + "Superior Health Potion", ChatColor.GRAY.toString() + "A potion that restores " + ChatColor.LIGHT_PURPLE.toString() + "750HP"));
						}
						if(item_meta == 3) {
							loot.add(ItemMechanics.signNewCustomItem(Material.POTION, (short) 3, ChatColor.YELLOW.toString() + "Legendary Health Potion", ChatColor.GRAY.toString() + "A potion that restores " + ChatColor.YELLOW.toString() + "1800HP"));
						}
						
						if(item_meta == 16385) {
							loot.add(ItemMechanics.signNewCustomItem(Material.POTION, (short) 16385, ChatColor.WHITE.toString() + "Minor Splash Health Potion", ChatColor.GRAY.toString() + "A potion that restores " + ChatColor.WHITE.toString() + "15HP," + ChatColor.GRAY.toString() + "to players in a 4x4 AREA"));
						}
						if(item_meta == 16389) {
							loot.add(ItemMechanics.signNewCustomItem(Material.POTION, (short) 16389, ChatColor.GREEN.toString() + "Splash Health Potion", ChatColor.GRAY.toString() + "A potion that restores " + ChatColor.GREEN.toString() + "40HP," + ChatColor.GRAY.toString() + "to players in a 4x4 AREA"));
						}
						if(item_meta == 16393) {
							loot.add(ItemMechanics.signNewCustomItem(Material.POTION, (short) 16393, ChatColor.AQUA.toString() + "Major Splash Health Potion", ChatColor.GRAY.toString() + "A potion that restores " + ChatColor.AQUA.toString() + "150HP," + ChatColor.GRAY.toString() + "to players in a 4x4 AREA"));
						}
						if(item_meta == 16396) {
							loot.add(ItemMechanics.signNewCustomItem(Material.POTION, (short) 16396, ChatColor.LIGHT_PURPLE.toString() + "Superior Splash Health Potion", ChatColor.GRAY.toString() + "A potion that restores " + ChatColor.LIGHT_PURPLE.toString() + "375HP," + ChatColor.GRAY.toString() + "to players in a 4x4 AREA"));
						}
						if(item_meta == 16387) {
							loot.add(ItemMechanics.signNewCustomItem(Material.POTION, (short) 16387, ChatColor.YELLOW.toString() + "Legendary Splash Health Potion", ChatColor.GRAY.toString() + "A potion that restores " + ChatColor.YELLOW.toString() + "900HP," + ChatColor.GRAY.toString() + "to players in a 4x4 AREA"));
						}
					} else if(m == Material.MAGMA_CREAM) {
						loot.add(ItemMechanics.signNewCustomItem(Material.MAGMA_CREAM, (short) 0, ChatColor.LIGHT_PURPLE.toString() + "Orb of Alteration", ChatColor.GRAY.toString() + "Randomizes bonus stats of selected equipment"));
					} else if(m == Material.EMPTY_MAP) {
						if(item_meta == 1) {
							loot.add(CraftItemStack.asCraftCopy(TeleportationMechanics.Cyrennica_scroll));
						}
						if(item_meta == 2) {
							loot.add(CraftItemStack.asCraftCopy(TeleportationMechanics.Harrison_scroll));
						}
						if(item_meta == 3) {
							loot.add(CraftItemStack.asCraftCopy(TeleportationMechanics.Dark_Oak_Tavern_scroll));
						}
						if(item_meta == 4) {
							loot.add(CraftItemStack.asCraftCopy(TeleportationMechanics.Deadpeaks_Mountain_Camp_scroll));
						}
						if(item_meta == 11) {
							loot.add(CraftItemStack.asCraftCopy(EnchantMechanics.t1_wep_scroll));
						}
						if(item_meta == 12) {
							loot.add(CraftItemStack.asCraftCopy(EnchantMechanics.t2_wep_scroll));
						}
						if(item_meta == 13) {
							loot.add(CraftItemStack.asCraftCopy(EnchantMechanics.t3_wep_scroll));
						}
						if(item_meta == 14) {
							loot.add(CraftItemStack.asCraftCopy(EnchantMechanics.t4_wep_scroll));
						}
						if(item_meta == 15) {
							loot.add(CraftItemStack.asCraftCopy(EnchantMechanics.t5_wep_scroll));
						}
						if(item_meta == 21) {
							loot.add(CraftItemStack.asCraftCopy(EnchantMechanics.t1_armor_scroll));
						}
						if(item_meta == 22) {
							loot.add(CraftItemStack.asCraftCopy(EnchantMechanics.t2_armor_scroll));
						}
						if(item_meta == 23) {
							loot.add(CraftItemStack.asCraftCopy(EnchantMechanics.t3_armor_scroll));
						}
						if(item_meta == 24) {
							loot.add(CraftItemStack.asCraftCopy(EnchantMechanics.t4_armor_scroll));
						}
						if(item_meta == 25) {
							loot.add(CraftItemStack.asCraftCopy(EnchantMechanics.t5_armor_scroll));
						}
					} else {
						loot.add(new ItemStack(m, amount_to_spawn, item_meta));
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
					loot.add(i); // quantity ALWAYS = 1x.
				}
				
				continue;
			}
			
			if(item_id_s.startsWith("*")) {
				String template_name = s.substring(1, s.indexOf(":"));
				double spawn_chance = Double.parseDouble(s.substring(s.indexOf("%") + 1, s.length())) * 10.0D;
				double do_i_spawn = new Random().nextInt(1000);
				
				if(spawn_chance > do_i_spawn) {
					ItemStack i = ItemGenerators.customGenerator(template_name);
					loot.add(i); // quantity ALWAYS = 1x.
				}
				
				continue;
			}
		}
		
		return loot;
	}
	
	public void twirlWhirldwind() {
		for(Entry<Entity, Integer> data : whirlwind.entrySet()) {
			Entity ent = data.getKey();
			if(ent == null || ent.isDead()) {
				continue;
			}
			
			Location loc = ent.getLocation();
			float yaw = 0;
			if(mob_yaw.containsKey(ent)) {
				yaw = mob_yaw.get(ent);
			}
			
			yaw += 20;
			
			if(yaw > 360) {
				yaw = 0;
			}
			
			//log.info("yaw= " + yaw);
			
			mob_yaw.put(ent, yaw);
			loc.setYaw(yaw);
			
			EntityLiving el = (EntityLiving) ((CraftEntity) ent).getHandle();
			el.yaw = (float) (yaw);
			EntityCreature ec = (EntityCreature) ((CraftEntity) ent).getHandle();
			ec.setTarget(null);
			ec.yaw = (float) (yaw);
			ent.teleport(loc);
			//el.teleportTo(loc, false);
		}
	}
	
	public void tickWhirlwind() {
		List<Entity> to_remove = new ArrayList<Entity>();
		
		for(Entry<Entity, Integer> data : whirlwind.entrySet()) {
			try {
				Entity ent = data.getKey();
				if(ent == null || ent.isDead()) {
					to_remove.add(ent);
					continue;
				}
				int step = data.getValue();
				if(step <= 4) { // Charging...
					EntityCreature ec = (EntityCreature) ((CraftEntity) ent).getHandle();
					ec.setTarget(null);
					
					step += 1;
					whirlwind.put(ent, step);
					special_attack.put(ent, step);
					boolean is_elite = false;
					ItemStack weapon = CraftItemStack.asBukkitCopy(((CraftEntity) ent).getHandle().getEquipment()[0]);
					if(weapon.getEnchantments().containsKey(Enchantment.KNOCKBACK)) {
						is_elite = true;
					}
					LivingEntity le = (LivingEntity) ent;
					if(le != null && mob_health.containsKey(ent)) {
						le.setCustomName(generateOverheadBar(ent, mob_health.get(ent), max_mob_health.get(ent), getMobTier(ent), is_elite));
						ent.getWorld().playSound(ent.getLocation(), Sound.CREEPER_HISS, 1F, 4.0F);
						try {
							ParticleEffect.sendToLocation(ParticleEffect.LARGE_EXPLODE, ent.getLocation().add(0, 1, 0), new Random().nextFloat(), new Random().nextFloat(), new Random().nextFloat(), 0.3F, 40);
						} catch(Exception err) {
							err.printStackTrace();
						}
						//ent.getWorld().spawnParticle(ent.getLocation().add(0, 1, 0), Particle.LARGE_EXPLODE, 0.3F, 40);
					}
					continue;
				}
				if(step == 5) { // Ready!
					// Maintain particle effect.
					/*Packet particles = new Packet61WorldEvent(2001, (int)Math.round(ent.getLocation().getX()), (int)Math.round(ent.getLocation().getY()), (int)Math.round(ent.getLocation().getZ()), 41, false);
					((CraftServer) getServer()).getServer().getPlayerList().sendPacketNearby(ent.getLocation().getX(), ent.getLocation().getY(), ent.getLocation().getZ(), 32, ((CraftWorld) ent.getWorld()).getHandle().dimension, particles);*/
					
					// Blow them up!
					ent.getWorld().playSound(ent.getLocation(), Sound.EXPLODE, 1F, 0.5F);
					try {
						ParticleEffect.sendToLocation(ParticleEffect.HUGE_EXPLOSION, ent.getLocation().add(0, 1, 0), new Random().nextFloat(), new Random().nextFloat(), new Random().nextFloat(), 1F, 40);
					} catch(Exception err) {
						err.printStackTrace();
					}
					
					//ent.getWorld().spawnParticle(ent.getLocation().add(0, 1, 0), Particle.HUGE_EXPLOSION, 1.0F, 40);
					
					for(Entity enemy : ent.getNearbyEntities(8, 8, 8)) {
						if(enemy instanceof Player) {
							Player pl = (Player) enemy;
							if(HealthMechanics.getPlayerHP(pl.getName()) > 0) {
								List<Integer> dmg_range = mob_damage.get(ent);
								int min_dmg = dmg_range.get(0);
								int max_dmg = dmg_range.get(1);
								
								if(max_dmg - min_dmg <= 0) {
									max_dmg = min_dmg + 1;
								}
								
								int dmg = new Random().nextInt(max_dmg - min_dmg) + min_dmg;
								dmg = dmg * 4;
								if(pl == null || ent == null) continue;
								pl.damage(dmg, ent);
								pushAwayPlayer(ent, pl, 3.0F);
							}
						}
					}
					
					whirlwind.remove(ent);
					mob_yaw.remove(ent);
					special_attack.remove(ent);
					
					boolean is_elite = false;
					ItemStack weapon = CraftItemStack.asBukkitCopy(((CraftEntity) ent).getHandle().getEquipment()[0]);
					if(weapon.getEnchantments().containsKey(Enchantment.KNOCKBACK)) {
						is_elite = true;
					}
					LivingEntity le = (LivingEntity) ent;
					
					if(le != null && mob_health.containsKey(ent)) {
						le.setCustomName(generateOverheadBar(ent, mob_health.get(ent), max_mob_health.get(ent), getMobTier(ent), is_elite));
					}
				}
				
			} catch(ConcurrentModificationException cme) {
				continue;
			}
		}
		
		for(Entity ent : to_remove) {
			whirlwind.remove(ent);
			mob_yaw.remove(ent);
			special_attack.remove(ent);
		}
	}
	
	public void logRecentChunks() {
		for(Location l : player_locations.values()) {
			recent_loaded_chunks.put(l.getChunk().getBlock(0, 0, 0).getLocation(), System.currentTimeMillis());
		}
	}

	@SuppressWarnings("deprecation")
	public void fixStuckEntities() {
		for(Entity ent : mob_target.keySet()) {
			if(!(mob_health.containsKey(ent))) {
				continue;
			}
			if(!(mob_last_hit.containsKey(ent))) {
				continue;
			}
			//Main.d("Size: " + mob_target.keySet().size() + " Last time hit: " + (System.currentTimeMillis() - mob_last_hit.get(ent)));
			/*if((System.currentTimeMillis() - mob_last_hit.get(ent)) > 500){
			    String target = mob_target.get(ent);
                if(Bukkit.getPlayer(target) != null) {
                    Player pl = Bukkit.getPlayer(target);
                    if(!pl.getWorld().getName().equalsIgnoreCase(ent.getWorld().getName())) {
                        continue;
                    }
                    //Main.d("MOB SHOULD BE HITTING " + target);
                    if(ent.getLocation().distance(pl.getLocation()) < 2.5){
                        //Main.d("DISTANCE: " + ent.getLocation().distance(pl.getLocation()));
                        if(!DuelMechanics.isDamageDisabled(pl.getLocation())){
                        pl.damage(1, ent);
                        ent.teleport(pl.getLocation().add(0, .3, 0));
                        continue;
                        }
                    }
                    
                }
			}*/
			
			if((System.currentTimeMillis() - mob_last_hit.get(ent)) > 2 * 1000) {
				String target = mob_target.get(ent);
				if(Bukkit.getPlayer(target) != null) {
					Player pl = Bukkit.getPlayer(target);
					if(!pl.getWorld().getName().equalsIgnoreCase(ent.getWorld().getName())) {
						continue;
					}
					
					double y_diff = Math.abs(pl.getLocation().getY() - ent.getLocation().getY());
					if(pl.getLocation().getBlock().getType() == Material.LADDER || pl.getLocation().getBlock().getType() == Material.VINE) {
						continue;
					}
					if(y_diff <= 4.0D && (y_diff >= 1.4D || y_diff == 0.5D || ((pl.getLocation().getY() < ent.getLocation().getY()) && y_diff >= 0.65D) || (ent.getLocation().getBlock().getType() == Material.WATER && y_diff >= 0.6D))) {
						try {
							Location loc = pl.getLocation();
							loc.setY(ent.getLocation().getY());
							double distance = ent.getLocation().distanceSquared(loc);
							if(distance <= 6) {
								// Ok so, no hits in last 2 seconds, and player is within 9 (not counting Y) blocks. This could be an issue.
								Location ploc = pl.getLocation().add(0, 0.25, 0);
								ent.teleport(ploc);
								ent.setFallDistance(0.0F);
							}
						} catch(IllegalArgumentException IAE) {
							// Cannot measure distance between DODungeon.0 and DODungeon.0 ??
							continue;
						}
					}
				}
			}
		
		}
		
	}
	
	public void unloadChunks() {
		List<Location> to_remove = new ArrayList<Location>();
		for(final Location l : chunks_to_unload) {
			/*if(chunks_done >= 10){
				return; // Skip all the rest. Only process 10 / tick.
			}*/
			if(to_remove.contains(l)) {
				continue;
				// Don't remove the same location multiple times.
			}
			
			final Chunk c = l.getChunk();
			if(c == null) continue;
			for(Entity ent : c.getEntities()) {
				if(!(ent instanceof LivingEntity) || !ent.hasMetadata("mobname")) {
					continue;
				}
				if(ent.getType() == EntityType.PLAYER || ent.getType() == EntityType.DROPPED_ITEM || ent.getType() == EntityType.ARROW || ent.getType() == EntityType.BOAT || ent.getType() == EntityType.WEATHER || ent.getType() == EntityType.THROWN_EXP_BOTTLE || ent.getType() == EntityType.EGG || ent.getType() == EntityType.ITEM_FRAME || ent.getType() == EntityType.ENDER_SIGNAL || ent.getType() == EntityType.FIREBALL || ent.getType() == EntityType.SNOWBALL) {
					continue;
				}
				/*if(player_inside == true){
					chunks_to_unload.remove(l);
					continue;
				}*/
				//log.info("[MM] Killing off " + ent.toString() + "...cuhnk unload");
				Location loc = getMobsHomeSpawner(ent);
				
				if(loc != null) {
					spawned_mobs.remove(loc);
					if(loaded_mobs.containsKey(loc)) {
						//no_delay_kills.add(ent);
						loaded_mobs.remove(loc); // Remove them from main thread list, they're no longer loaded.
						// Now mobs_to_spawn will once again pick it up and check for nearby players.
					}
				}
				
				LivingEntity le = (LivingEntity) ent;
				le.damage(le.getHealth());
				ent.remove();
				continue;
			}
			
			c.unload(true, true);
			to_remove.add(l);
		}
		
		for(Location l : to_remove) {
			chunks_to_unload.remove(l);
		}
		
	}
	
	public void updateNametags() {
		for(Entry<Entity, Long> data : mob_last_hurt.entrySet()) {
			if((System.currentTimeMillis() - data.getValue()) > (6 * 1000)) {
				// Change nametag back to name/level.
				Entity e = data.getKey();
				if(!(e instanceof LivingEntity)) {
					continue;
				}
				LivingEntity le = (LivingEntity) e;
				if(!(le.hasMetadata("mobname"))) {
					continue;
				}
				le.setCustomName(le.getMetadata("mobname").get(0).asString());
				le.setCustomNameVisible(true);
			}
		}
	}
	
	public void updateParticles() {
		for(Entity ent : mob_last_hurt.keySet()) {
			if(ent instanceof LivingEntity && ent.hasMetadata("etype")) {
				LivingEntity le = (LivingEntity) ent;
				String elemental_type = le.getMetadata("etype").get(0).asString();
				if(elemental_type.equalsIgnoreCase("poison")) {
					attachPotionEffect(le, 0x669900);
				}
				if(elemental_type.equalsIgnoreCase("fire")) {
					attachPotionEffect(le, 0xCC0033);
				}
				if(elemental_type.equalsIgnoreCase("ice")) {
					attachPotionEffect(le, 0x33FFFF);
				}
				if(elemental_type.equalsIgnoreCase("pure")) {
					attachPotionEffect(le, 0xFFFFFF);
				}
			}
		}
	}
	
	public static void attachPotionEffect(final LivingEntity entity, int color) {
		EntityLiving el = ((CraftLivingEntity) entity).getHandle();
		
		/*DataWatcher dw = el.getDataWatcher();
		dw.watch(8, Integer.valueOf(color));*/
		
		DataWatcher dw = el.getDataWatcher();
		dw.watch(8, Byte.valueOf((byte) color));
		
		/*final DataWatcher dw = new DataWatcher();
		dw.a(8, Integer.valueOf(0));
		dw.watch(8, Integer.valueOf(color));*/
		
		/*Packet40EntityMetadata packet = new Packet40EntityMetadata(entity.getEntityId(), dw, false);
		((CraftServer) getServer()).getServer().getPlayerList().sendPacketNearby(entity.getLocation().getBlockX(), entity.getLocation().getBlockY(), entity.getLocation().getBlockZ(), 32, ((CraftWorld) entity.getWorld()).getHandle().dimension, packet);*/
		
		/*Bukkit.getScheduler().scheduleAsyncDelayedTask(instance, new Runnable() {
			public void run() {
				DataWatcher dwReal = ((CraftLivingEntity)entity).getHandle().getDataWatcher();
				dw.watch(8, dwReal.getByte(8));
				Packet40EntityMetadata packet = new Packet40EntityMetadata(entity.getEntityId(), dw, false);
			}
		}, duration);*/
	}
	
	public void processChunks() {
		List<Chunk> to_remove = new ArrayList<Chunk>();
		
		try {
			Long cur_time = System.currentTimeMillis();
			
			if(chunk_copy.size() <= 0) { return; }
			for(Chunk c : chunk_copy) {
				Location l = c.getBlock(0, 0, 0).getLocation();
				if(!(loaded_chunks.contains(l))) {
					//log.info("[MonsterMechanics] No record for " + l.toString());
					loaded_chunks.add(l);
					recent_loaded_chunks.put(l, System.currentTimeMillis());
					continue;
				}
				if(never_unload_chunks.contains(l)) {
					continue; // High end chunk.
				}
				boolean nearby = false;
				for(Location p_loc : player_locations.values()) {
					if(!p_loc.getWorld().getName().equalsIgnoreCase("DungeonRealms")) {
						continue;
					}
					//if(pl.getPlayerListName().equalsIgnoreCase("")){continue;}
					//Location p_loc = pl.getLocation();
					Location copy_p_loc = p_loc.clone(); // No height distance needs to be calculated.
					copy_p_loc.setY(l.getY());
					
					if(copy_p_loc.distanceSquared(l) <= 6400) { // A player is 80 (160) blocks from the 0,0 point of the chunk. View distance = 80 (80) blocks.
						nearby = true;
						break;
					}
				}
				
				if(nearby == false) {
					if(recent_loaded_chunks.containsKey(l)) {
						long last_loaded = recent_loaded_chunks.get(l);
						if((cur_time - last_loaded) <= (30 * 1000)) {
							continue;
							// Don't unload, no one's near but it was loaded less than 30 seconds ago.
							// Issue with the stupid loop load.
						} else {
							recent_loaded_chunks.remove(l);
						}
					}
					for(Location rloc : RealmMechanics.portal_map.keySet()) {
						if(!(rloc.getBlock().getType() == Material.PORTAL)) {
							RealmMechanics.portal_map.remove(rloc);
							continue;
						}
						if(!rloc.getWorld().getName().equalsIgnoreCase(l.getWorld().getName())) {
							continue;
						}
						Location copy = rloc.clone();
						copy.setY(l.getY());
						if(copy.distanceSquared(l) <= 6400) {
							nearby = true;
							break;
						}
					}
				}
				
				if(nearby) {
					continue;
				}
				
				/*if(c.getEntities().length > 0){
				continue;
				}*/
				
				/*for(Entity ent : c.getEntities()){
				if(ent.getType() == EntityType.PLAYER){
					continue;
				}
				}*/
				
				to_remove.add(c);
			}
			
			for(Chunk c : to_remove) {
				Location loc = c.getBlock(0, 0, 0).getLocation();
				loaded_chunks.remove(loc);
				chunks_to_unload.add(loc);
			}
			
		} catch(Exception e) {
			e.printStackTrace();
			for(Chunk c : to_remove) {
				Location loc = c.getBlock(0, 0, 0).getLocation();
				loaded_chunks.remove(loc);
				chunks_to_unload.add(loc);
			}
			return; // Let's try again in a second.
		}
	}
	
	public void spawnMobsIn() {
		/*main_thread.add(loc);
		main_thread.add(local_loc);
		main_thread.add(et);
		main_thread.add(tier);
		main_thread.add(mob_num);
		main_thread.add(elite);
		main_thread.add(meta_data);*/
		
		if(mobs_being_spawned.size() <= 0) { return; }
		ArrayList<ArrayList<Object>> mobs_being_spawned_copy = new ArrayList<ArrayList<Object>>();
		for(ArrayList<Object> data : mobs_being_spawned) {
			mobs_being_spawned_copy.add(data);
		}
		for(ArrayList<Object> data : mobs_being_spawned_copy) {
			try {
				Location loc = (Location) data.get(0);
				Location local_loc = (Location) data.get(1);
				EntityType et = (EntityType) data.get(2);
				int tier = (int) data.get(3);
				int mob_num = (int) data.get(4);
				boolean elite = (boolean) data.get(5);
				String meta_data = (String) data.get(6);
				String custom_name = "";
				if(data.get(7) != null) {
					custom_name = (String) data.get(7);
				}
				int level_tier = 1;
				if(data.get(8) != null){
				    level_tier = (int) data.get(8);
				}
				if(spawnTierMob(local_loc, et, tier, mob_num, loc, elite, meta_data, custom_name, true, level_tier) != null) {
					mobs_being_spawned.remove(data);
				}
			} catch(NullPointerException npe) {
				// TODO: Handle nulls
				npe.printStackTrace();
				if(data != null) {
					log.info("[MM] Found a null entry in a mob on main thread, skipping... index: " + data.toString());
					mobs_being_spawned.remove(data);
				}
				continue;
			}
		}
	}
	
	public double getMobSpawnDelayMultiplier() {
		if(player_count <= 10) { return 4.0D; }
		if(player_count <= 30) { return 3.0D; }
		if(player_count <= 50) { return 2.5D; }
		if(player_count <= 70) { return 2.0D; }
		if(player_count <= 100) { return 2.0D; }
		if(player_count <= 150) { return 1.5D; }
		
		return 1.0D; // player_count is greater than 100.
	}
	
	public void clearAllEntities() {
		if(Bukkit.getMotd().contains("US-0")) { return; }
		for(Entity e : Bukkit.getWorlds().get(0).getEntities()) {
			if(e.getType() == EntityType.PLAYER) {
				continue;
			}
			e.remove();
		}
		for(LivingEntity le : Bukkit.getWorlds().get(0).getLivingEntities()) {
			if(le.getType() == EntityType.PLAYER) {
				continue;
			}
			le.remove();
		}
	}
	
	public void populateSpawnList() {
		long cur_time = System.currentTimeMillis();
		
		for(Map.Entry<Location, String> entry : mob_spawns.entrySet()) {
			try {
				Location l = entry.getKey();
				String spawn_data = entry.getValue();
				String mob_data[] = spawn_data.substring(spawn_data.indexOf("@") + 1, spawn_data.lastIndexOf("@")).split(",");
				
				List<String> to_spawn_list = new ArrayList<String>();
				
				for(String s : mob_data) {
					if(s.equalsIgnoreCase("")) {
						continue;
					}
					if(!s.split(":")[1].contains("-")) continue;
					if(s.split(":")[1].split("-").length == 1) continue;
					int mob_num = Integer.parseInt(s.split(":")[1].split("-")[1]);
					to_spawn_list.add(mob_num + ":" + cur_time);
				}
				
				if(to_spawn_list.size() <= 0) {
					//log.info("[MM] Empty to_spawn_list over here at " + l.toString());
				}
				
				mob_to_spawn.put(l, to_spawn_list);
			} catch(Exception err) {
				log.info("Problematic entry: " + entry.getValue());
				err.printStackTrace();
				continue;
			}
		}
	}
	
	public void addMobToRespawnList(Entity e) {
		if(!(mob_spawn_ownership.containsKey(e))) {
			log.info("[MonsterMechanics] Entity " + e.toString() + " does not have mob_spawn_ownership information. Removing from spawn list.");
			//log.info(e.getLocation().toString());
			return;
		}
		
		Location l = getMobsHomeSpawner(e);
		
		/*if(InstanceMechanics.isInstance(l.getWorld().getName())){
			return;
		}*/
		
		int mob_num = getMobsUniqueNumber(e);
		
		double pop_multiplier = getMobSpawnDelayMultiplier();
		int delay = Math.round((int) ((double) getSpawnDelay(e) * pop_multiplier)); // Multiplier for low population of servers.
		
		if(InstanceMechanics.isInstance(l.getWorld().getName())) {
			delay = Integer.MAX_VALUE;
			// Only respawn if it's cleanup for isValid().
		}
		
		List<String> to_spawn = new ArrayList<String>();
		if(loaded_mobs.containsKey(l)) {
			to_spawn = loaded_mobs.get(l);
		}
		
		if(no_delay_kills.contains(e)) {
			delay = 0;
		}
		
		to_spawn.add(mob_num + ":" + (System.currentTimeMillis() + (delay * 1000)));
		mob_spawn_ownership.remove(e);
		////log.info("[MM] Adding a new entity to respawn list. LOCATION: " + l.toString() + " MOB NUMBER: " + mob_num);
		no_delay_kills.remove(e);
		loaded_mobs.put(l, to_spawn);
	}
	
	public void removeMobFromRespawnList(Entity e) {
		// TODO: Remove it at the location where we loop through mob_to_spawn.
	}
	
	public String getMobDataFromNum(Location l, int num) {
		String spawn_data = mob_spawns.get(l);
		String mob_data[] = spawn_data.substring(spawn_data.indexOf("@") + 1, spawn_data.lastIndexOf("@")).split(",");
		String the_mdata = "";
		
		for(String s : mob_data) {
			if(s.equalsIgnoreCase("")) {
				continue;
			}
			int lnum = Integer.parseInt(s.substring(s.lastIndexOf("-") + 1, s.length()));
			//log.info("$ " + s + " -> " + lnum);
			//log.info("LOOKING FOR: " + num);
			if(lnum == num) {
				//log.info(s);
				return s;
			}
		}
		
		//log.info("[MM] Returning NOTHING for mob data of num " + num + " at " + l.toString());
		return the_mdata;
		//return mob_data[(num - 1)];
	}
	
	public int getSpawnDelay(Entity e) { // Returns SECONDS between spawns.
		if(getMobsHomeSpawner(e) == null) {
			log.info("[MM] Entity " + e.toString() + " has no information.");
			return 0; // Don't respawn corruption.
		}
		String spawn_data = mob_spawns.get(getMobsHomeSpawner(e));
		int spawn_delay = Integer.parseInt(spawn_data.substring(spawn_data.lastIndexOf("@") + 1, spawn_data.indexOf("#")));
		if(spawn_delay < 20) {
			spawn_delay = 20;
		}
		return spawn_delay;
	}
	
	public static void saveMobSpawnerData() {
		String all_dat = "";
		int count = 0;
		
		for(Entry<Location, String> entry : mob_spawns.entrySet()) {
			Location loc = entry.getKey();
			if(!loc.getWorld().getName().equalsIgnoreCase(Bukkit.getWorlds().get(0).getName())) {
				continue;
			}
			String spawn_data = entry.getValue();
			all_dat += loc.getX() + "," + loc.getY() + "," + loc.getZ() + "=" + spawn_data + "\r\n";
			count++;
			
		}
		
		if(all_dat.length() > 1) {
			try {
				DataOutputStream dos = new DataOutputStream(new FileOutputStream("plugins/MonsterMechanics/global_mob_spawns.dat", false));
				dos.writeBytes(all_dat + "\n");
				dos.close();
			} catch(IOException e) {}
		}
		
		log.info("[MonsterMechanics] GLOBAL: " + count + " MOB SPAWN profiles have been SAVED.");
		
		String mob_spawns_string = "";
		for(Entry<String, HashMap<Location, String>> data : InstanceMechanics.instance_mob_spawns.entrySet()) {
			
			// Clear local variables.
			mob_spawns_string = "";
			count = 0;
			
			String instance_name = data.getKey();
			if(instance_name.contains(".")) {
				continue; // It's an instance, not an instance template.
			}
			HashMap<Location, String> location_data = data.getValue();
			for(Entry<Location, String> l_mob_spawns_spawns : location_data.entrySet()) {
				Location loc = l_mob_spawns_spawns.getKey();
				Block b = loc.getBlock();
				String spawn_data = l_mob_spawns_spawns.getValue();
				mob_spawns_string += loc.getBlockX() + "," + b.getLocation().getBlockY() + "," + loc.getBlockZ() + "=" + spawn_data + "\r\n";
				count++;
			}
			
			// Now mob_spawns_string is populated, let's save it.
			try {
				DataOutputStream dos = new DataOutputStream(new FileOutputStream("plugins/InstanceMechanics/mob_spawns/" + instance_name + ".dat", false));
				dos.writeBytes(mob_spawns_string + "\n");
				dos.close();
			} catch(IOException e) {}
			
			log.info("[MonsterMechanics] " + instance_name.toUpperCase() + ": " + count + " MOB SPAWN profiles have been SAVED.");
			
		}
	}
	
	public void loadMobMessageTemplates() {
		int count = 0;
		
		try {
			for(File f : templatePath.listFiles()) {
				List<String> lmsg_template = new ArrayList<String>();
				String tn = f.getName();
				if(tn.endsWith(".msg")) {
					BufferedReader reader = new BufferedReader(new FileReader(f));
					String line = "";
					while((line = reader.readLine()) != null) {
						lmsg_template.add(line);
					}
					reader.close();
				}
				mob_messages.put(tn.replaceAll(".msg", ""), lmsg_template);
				count++;
			}
			
			log.info("[MonsterMechanics] " + count + " MOB MESSAGES TEMPLATE profiles have been LOADED.");
		} catch(IOException ioe) {
			ioe.printStackTrace();
		}
	}
	
	public void loadCustomMobDrops() {
		int count = 0;
		
		try {
			for(File f : new File("plugins/MonsterMechanics/custom_mobs").listFiles()) {
				List<String> lmsg_template = new ArrayList<String>();
				String tn = f.getName();
				if(tn.endsWith(".loot")) {
					BufferedReader reader = new BufferedReader(new FileReader(f));
					String line = "";
					while((line = reader.readLine()) != null) {
						lmsg_template.add(line);
					}
					reader.close();
				}
				log.info(tn + " -> " + lmsg_template);
				custom_mob_loot_tables.put(tn.replaceAll(".loot", ""), lmsg_template);
				count++;
			}
			
			log.info("[MonsterMechanics] " + count + " CUSTOM MOB DROP profiles have been LOADED.");
		} catch(IOException ioe) {
			ioe.printStackTrace();
		}
	}
	
	public void loadMobSpawnerData() {
		int count = 0;
		try {
			File file = new File("plugins/MonsterMechanics/global_mob_spawns.dat");
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line = "";
			while((line = reader.readLine()) != null) {
				if(line.contains("=")) {
					String[] cords = line.split("=")[0].split(",");
					Location loc = new Location(Bukkit.getWorlds().get(0), Double.parseDouble(cords[0]), Double.parseDouble(cords[1]), Double.parseDouble(cords[2]));
					String spawn_data = line.split("=")[1];
					mob_spawns.put(loc, spawn_data);
					//mob_to_spawn.put(loc, spawn_data);
					count++;
				}
			}
			reader.close();
			log.info("[MonsterMechanics] GLOBAL: " + count + " MOB SPAWN profiles have been LOADED.");
		} catch(IOException ioe) {
			ioe.printStackTrace();
		}
	}
	
	public static void loadInstanceMobSpawnerData(String instance_name) { // This will only occur when the instance is loaded.
		int count = 0;
		
		String instance_template = instance_name;
		if(instance_name.contains(".")) {
			instance_template = instance_name.substring(0, instance_name.indexOf("."));
		}
		
		for(File f : new File("plugins/InstanceMechanics/mob_spawns/").listFiles()) {
			count = 0;
			String f_name = f.getName().replaceAll(".dat", "");
			if(f_name.equalsIgnoreCase(instance_template)) {
				// It's the one we want.
				System.out.print("Found " + instance_template + " mob spawns");
				HashMap<Location, String> local_mob_spawns = new HashMap<Location, String>();
				try {
					BufferedReader reader = new BufferedReader(new FileReader(f));
					String line = "";
					while((line = reader.readLine()) != null) {
						if(line.contains("=")) {
							count++;
							String[] cords = line.split("=")[0].split(",");
							Location loc = new Location(Bukkit.getWorld(instance_name), Double.parseDouble(cords[0]), Double.parseDouble(cords[1]), Double.parseDouble(cords[2]));
							
							String spawn_data = line.split("=")[1];
							local_mob_spawns.put(loc, spawn_data);
							count++;
						}
					}
					// Read entire file, now store local_mob_spawns_spawns.
					reader.close();
					
					for(Entry<Location, String> data : local_mob_spawns.entrySet()) {
						Location loc = data.getKey();
						String spawn_data = data.getValue();
						
						List<String> mob_list = new ArrayList<String>();
						for(String s : spawn_data.split(",")) {
							mob_list.add(s);
						}
						
						List<String> new_data = new ArrayList<String>();
						for(String s : spawn_data.substring(spawn_data.indexOf("@") + 1, spawn_data.lastIndexOf("@")).split(",")) {
							if(s.equalsIgnoreCase("")) {
								continue;
							}
							int mob_num = Integer.parseInt(s.split(":")[1].split("-")[1]);
							new_data.add(mob_num + ":" + System.currentTimeMillis());
						}
						
						loaded_mobs.put(loc, new_data);
						mob_spawns.put(loc, spawn_data);
						// This will add it to the spawn thread so they can spawn into the instance.
					}
					
					InstanceMechanics.instance_mob_spawns.put(instance_name, local_mob_spawns);
					log.info("[MonsterMechanics] Loaded " + count + " mob_spawns into " + instance_name);
					break;
					
				} catch(Exception err) {
					log.info("[MonsterMechanics] Failed to load INSTANCE mob_spawns data for " + f.getName());
					err.printStackTrace();
					continue;
				}
			}
		}
		
		// local_mob_spawns
	}
	
	public void killListEntities() {
		List<Entity> to_remove = new ArrayList<Entity>();
		if(entities_to_kill.size() <= 0) { return; }
		
		for(Entity ent : entities_to_kill) {
			to_remove.add(ent);
			LivingEntity le = (LivingEntity) ent;
			le.damage(le.getHealth());
			ent.remove();
			continue;
		}
		
		entities_to_kill.clear();
		final List<Entity> safe_to_remove = new ArrayList<Entity>(to_remove);
		
		//this.getServer().getScheduler().scheduleAsyncDelayedTask(this, new Runnable() {
		//	public void run() {
		for(Entity ent : safe_to_remove) {
			//entities_to_kill.remove(ent);
			// Been handled, remove them.
			if(mob_spawn_ownership.containsKey(ent)) {
				int mob_num = getMobsUniqueNumber(ent);
				Location spawner_loc = getMobsHomeSpawner(ent);
				if(spawned_mobs.containsKey(spawner_loc)) {
					no_delay_kills.add(ent);
					List<Integer> spawned_mob_nums = spawned_mobs.get(spawner_loc);
					
					List<Entity> lspawned_mobs = new ArrayList<Entity>();
					if(local_spawned_mobs.containsKey(spawner_loc)) {
						lspawned_mobs = local_spawned_mobs.get(spawner_loc);
						lspawned_mobs.remove(ent);
						local_spawned_mobs.put(spawner_loc, lspawned_mobs);
					} else if(!(local_spawned_mobs.containsKey(spawner_loc))) {
						lspawned_mobs.remove(ent);
						local_spawned_mobs.put(spawner_loc, lspawned_mobs);
					}
					
					spawned_mob_nums.remove(new Integer(mob_num));
					spawned_mobs.put(spawner_loc, spawned_mob_nums);
					
					addMobToRespawnList(ent);
				} else if(!(spawned_mobs.containsKey(spawner_loc))) {
					mob_spawn_ownership.remove(ent);
					//no_delay_kills.remove(ent);
				}
			}
		}
		//	}
		//}, 0L);
	}
	
	public void removeListEntities() {
		List<Entity> to_remove = new ArrayList<Entity>();
		if(entities_to_remove.size() <= 0) { return; }
		long cur_time = System.currentTimeMillis();
		
		for(Entry<Entity, Location> data : entities_to_remove.entrySet()) {
			Entity ent = data.getKey();
			
			if(ent == null) {
				log.info("[MM] NULL ENTITY in entities_to_remove: " + data.getValue());
				continue;
			}
			
			/*if(no_pathing.contains(ent)){
				to_remove.add(ent);
				continue;
			}*/
			
			if(last_respawn.containsKey(ent) && ((cur_time - last_respawn.get(ent) <= (10 * 1000)))) {
				continue; // They already repositioned 15 seconds ago, chill the fuck out.
			}
			
			Location spawner_loc = data.getValue();
			if(InstanceMechanics.isInstance(spawner_loc.getWorld().getName())) {
				log.info("[MM] Skipping ENTITY remove --> instance.");
				to_remove.add(ent);
				continue; // Don't despawn mobs in instances. 
			}
			
			if(!(mob_spawns.containsKey(spawner_loc))) {
				log.info("[MM] NULL LOCATION in entities_to_remove: " + data.getKey().toString());
				continue;
				//TODO: Possible memory leak?
			}
			
			String spawn_data = mob_spawns.get(spawner_loc);
			String loc_range[] = spawn_data.substring(spawn_data.indexOf("#") + 1, spawn_data.lastIndexOf("$")).split("-");
			double min_xz = Integer.parseInt(loc_range[0]);
			double max_xz = Integer.parseInt(loc_range[1]);
			
			Location local_loc = getRandomLocation(spawner_loc, ((spawner_loc.getX() - min_xz) - max_xz), ((spawner_loc.getX() + min_xz) + max_xz), ((spawner_loc.getZ() - min_xz) - max_xz), ((spawner_loc.getZ() + min_xz) + max_xz));
			
			if((local_loc.getBlock().getType() != Material.AIR || local_loc.add(0, 1, 0).getBlock().getType() != Material.AIR)) {
				local_loc = spawner_loc.clone().add(0, 1, 0);
			} else {
				local_loc.subtract(0, 1, 0);
			}
			
			ent.teleport(local_loc); // Teleport the mob back to spawner. What if the chunk is unloaded? LAWL.
			
			LivingEntity le = (LivingEntity) ent;
			if(le.hasMetadata("mobname")) {
				le.setCustomNameVisible(true);
				le.setCustomName(le.getMetadata("mobname").get(0).asString());
			}
			
			int max_hp = HealthMechanics.generateMaxHP(ent);
			mob_health.put(ent, max_hp);
			max_mob_health.put(ent, max_hp);
			
			/*boolean is_elite = false;
			if(le.getEquipment().getItemInHand().getEnchantments().size() > 0){
				is_elite = true;
			}

			le.setCustomName(generateOverheadBar(max_hp, max_hp, getMobTier(ent), is_elite));*/
			
			to_remove.add(ent);
			//log.info("[MM] Transported entity back to spawn. " + ent.toString());
		}
		
		for(Entity e : to_remove) {
			entities_to_remove.remove(e);
			last_respawn.put(e, System.currentTimeMillis());
			// Been handled, remove them.
		}
		
		ignore_target_event.clear();
	}
	
	public void removeNoHPMobs() {
		List<Entity> alive_ents = new ArrayList<Entity>();
		for(Entity ent : Main.plugin.getServer().getWorlds().get(0).getLivingEntities()) {
			if(ent instanceof Player) {
				continue;
			}
			if(ent.getType() == EntityType.DROPPED_ITEM || ent.getType() == EntityType.ARROW || ent.getType() == EntityType.BOAT || ent.getType() == EntityType.WEATHER || ent.getType() == EntityType.HORSE || ent.getType() == EntityType.THROWN_EXP_BOTTLE || ent.getType() == EntityType.EGG) {
				continue;
			}
			alive_ents.add(ent);
		}
		for(Entity ent : alive_ents) {
		    boolean isPet = false;
		    for(Entry<String, List<Entity>> petmap : PetMechanics.pet_map.entrySet()){
		        List<Entity> ents = petmap.getValue();
		        if(ents.contains(ent)){
		            isPet = true;
		            break;
		        }
		    }
			if(!(mob_health.containsKey(ent)) && isHostile(ent.getType()) && !isPet) {
				ent.remove();
				//to_remove.add(ent);
				//LivingEntity le = (LivingEntity)ent;
				//le.damage(le.getHealth());
				//log.info("[MM] Killing a mob due to <1 HP value, cleanup event. " + ent.toString());
				continue; // Null mob, let's kill them.
			}
		}
		
	}
	
	/*public void fixNullMobs(){
		for(Location l : spawned_mobs.keySet()){
			//if(l.getChunk().getEntities().length <= 0){ // Ok, so no entites are in the chunk, they're either bugged or running / patrolling around.
			List<Entity> copy_lsm = new ArrayList<Entity>();
			if(!local_spawned_mobs.containsKey(l)){continue;}
			for(Entity ent : local_spawned_mobs.get(l)){
				copy_lsm.add(ent);
			}

				for(Entity ent : copy_lsm){
					//log.info(ent.getLocation().toString());
					if((ent == null || !ent.isValid()) && !(entities_to_remove.containsKey(ent))){
					  	LivingEntity le = (LivingEntity)ent;
					  	le.damage(le.getHealth());
					  	continue;
					}
				}
		}

		//log.info("[MM] " + mob_count);
		/*for(Location l : to_remove){
			spawned_mobs.remove(l);
			if(!(loaded_mobs.containsKey(l))){
				loaded_mobs.put(l, mob_to_spawn.get(l));
			}
			//log.info("[MM] Corrupt mob spawning located at " + l.toString() + ", removing from spawned_mobs...");
		}
	}*/
	
	public void populateLoadedChunks() {
		try {
			for(Chunk c : Main.plugin.getServer().getWorlds().get(0).getLoadedChunks()) {
				Location l = c.getBlock(0, 0, 0).getLocation();
				if(!(chunks_to_unload.contains(l)) && !(loaded_chunks.contains(l))) {
					loaded_chunks.add(l);
				}
			}
		} catch(NoSuchElementException nsee) {
			log.info("[MM] Failed to get a new snapshot of loaded chunks, NSEE error.");
			return; // Try again in a bit.
		}
	}
	
	public void cleanupMonsters() {
		List<Entity> to_remove = new ArrayList<Entity>();
		
		for(Map.Entry<Entity, String> entry : mob_spawn_ownership.entrySet()) {
			Entity e = entry.getKey();
			try {
				if(e == null) {
					//addMobToRespawnList(e);
					//to_remove.add(e);
					log.info("[MM] Null dead entity, skipping... " + entry.getValue());
					continue;
				}
				
				if(entities_to_remove.containsKey(e)) {
					continue; // Irrelevant.
				}
				
				boolean is_instance = InstanceMechanics.isInstance(e.getWorld().getName());
				
				if(!is_instance) {
					Location spawner_loc = getMobsHomeSpawner(e);
					Location current_mob_loc = e.getLocation();
					
					if(e.getType() == EntityType.BAT) {
						continue;
					}
					
					String spawn_data = mob_spawns.get(spawner_loc);
					String loc_range[] = spawn_data.substring(spawn_data.indexOf("#") + 1, spawn_data.lastIndexOf("$")).split("-");
					//double min_xz = Integer.parseInt(loc_range[0]);
					double max_xz = Integer.parseInt(loc_range[1]);
					
					double distance = current_mob_loc.distanceSquared(spawner_loc);
					
					if(distance <= (Math.pow(max_xz, 2) + 625.0D)) { // Monster is less than 25 blocks from the spawner, who  cares.
						continue;
					}
					
					// If the last time the mob was hurt was less than 6 seconds ago and they're not TOO far out, we won't TP them.
					if(((mob_last_hurt.containsKey(e) && ((System.currentTimeMillis() - mob_last_hurt.get(e)) <= (6 * 1000))) && distance <= (Math.pow(max_xz, 2) + 2500.0D))) {
						continue;
					}
					
					if(distance >= (Math.pow(max_xz, 2) + 2500.0D)) {
						if(!(entities_to_remove.containsKey(e))) {
							entities_to_remove.put(e, spawner_loc);
						}
						continue; // They're way too far away for anything else to matter.
					}
					
					// If the code reaches this point, it's all about making sure the mob has a target.
					// The mob is between 20-40 blocks from the spawner.
					if(e instanceof EntityCreature) {
						EntityCreature ec = (EntityCreature) ((CraftEntity) e).getHandle();
						if(ec.target != null) {
							if(!(ec.target instanceof Player)) {
								continue;
							}
							
							Player e_target = (Player) (((EntityPlayer) ec.target).getBukkitEntity());
							Location target_loc = e_target.getLocation();
							if(target_loc.getWorld().getName().equalsIgnoreCase(current_mob_loc.getWorld().getName())) {
								if(e_target != null && e_target.getHealth() > 0 && e_target.isOnline() && e_target.getGameMode() == GameMode.SURVIVAL && !e_target.getPlayerListName().equalsIgnoreCase("") && current_mob_loc.distanceSquared(target_loc) <= 1600) {
									// They have a target, within 30 blocks, carry on. If they go too far out, pathbackevent will take care of them.
									continue;
								}
							}
						}
						if(!(entities_to_remove.containsKey(e)) && !is_instance) {
							entities_to_remove.put(e, spawner_loc);
						}
					}
				}
			} catch(NullPointerException npe) {
				npe.printStackTrace();
				to_remove.add(e);
				continue;
			}
		}
		
		for(Entity e : to_remove) {
			log.info("[MM] Cleaning up monster, " + e.toString());
			//mob_spawn_ownership.remove(e);
		}
		
		//log.info("[MonsterMechanics] spawned_mobs size: " + spawned_mobs.size());
		
		for(Location l : spawned_mobs.keySet()) {
			if(!loaded_chunks.contains(l.getChunk().getBlock(0, 0, 0).getLocation())) {
				spawned_mobs.remove(l);
			}
			List<Entity> copy_lsm = new ArrayList<Entity>();
			if(!local_spawned_mobs.containsKey(l)) {
				continue;
			}
			for(Entity ent : local_spawned_mobs.get(l)) {
				copy_lsm.add(ent);
			}
			
			for(Entity ent : copy_lsm) {
				//log.info(ent.getLocation().toString());
				if(ent == null && !(entities_to_remove.containsKey(ent))) {
					entities_to_kill.add(ent);
					continue;
				}
				//TODO: Check the last time the mob has gotten action AKA had health change. If no health change, no target, we'll refresh.
				LivingEntity le = (LivingEntity) ent;
				if(!(mob_target.containsKey(ent)) && le.getHealth() > 0) {
					if(ent.isDead() || !(ent.isValid())) {
						entities_to_kill.add(ent);
						continue;
					}
					/*if(!(mob_last_hurt.containsKey(ent))){
							entities_to_kill.add(ent);
							continue;
						}
						long last_hit = mob_last_hurt.get(ent);
						if((System.currentTimeMillis() - last_hit) > (360 * 1000)){
							// They haven't been hit in 5 minutes, no target.
							entities_to_kill.add(ent);
						}*/
				}
			}
		}
		
		/*for(Entity ent : mob_health.keySet()){
			if(ent.isDead() || !ent.isValid() || !ent.hasMetadata("mobname")){
				//log.info("[MM] Removing null'd entry in mob_hashmaps.");

				if(ent != null){
					entities_to_kill.add(ent);
					mob_health.remove(ent);
					mob_damage.remove(ent);
					max_mob_health.remove(ent);
					mob_armor.remove(ent);
					mob_tier.remove(ent);
					mob_loot.remove(ent);
					//mob_spawn_ownership.remove(ent);
					//no_delay_kills.remove(ent);
				}
			}
		}*/
		
		/*for(Entity ent : mob_spawn_ownership.keySet()){
			if(ent.isDead() || !ent.isValid() || !ent.hasMetadata("mobname")){
				//log.info("[MM] Removing null'd entry in mob_hashmaps.");

				if(ent != null){
					mob_spawn_ownership.remove(ent);
					//no_delay_kills.remove(ent);
				}
			}
		}*/
		
	}
	
	@SuppressWarnings("deprecation")
	public static boolean isThereAPlayerNearLocation(Block b, int maxradius) {
		// if(b.getType() != Material.CAULDRON){
		// return false;
		// }
		BlockFace[] faces = { BlockFace.UP, BlockFace.NORTH, BlockFace.EAST };
		BlockFace[][] orth = { { BlockFace.NORTH, BlockFace.EAST }, { BlockFace.UP, BlockFace.EAST }, { BlockFace.NORTH, BlockFace.UP } };
		for(int r = 0; r <= maxradius; r++) {
			for(int s = 0; s < 6; s++) {
				BlockFace f = faces[s % 3];
				BlockFace[] o = orth[s % 3];
				if(s >= 3) f = f.getOppositeFace();
				if(!(b.getRelative(f, r) == null)) {
					Block c = b.getRelative(f, r);
					
					for(int x = -r; x <= r; x++) {
						for(int y = -r; y <= r; y++) {
							Block a = c.getRelative(o[0], x).getRelative(o[1], y);
							if(a.getTypeId() == 130) return true;
						}
					}
				}
			}
		}
		return false;
	}
	
	public void addLoadedChunk(Chunk c) {
		Location loc = c.getBlock(0, 0, 0).getLocation();
		
		//log.info("loaded_chunks size: " + loaded_chunks.size());
		
		if(loaded_chunks.contains(loc)) { return; }
		
		loaded_chunks.add(loc);
		recent_loaded_chunks.put(loc, System.currentTimeMillis());
		chunks_to_unload.remove(loc);
	}
	
	@EventHandler
	public void onEntityDamageLava(EntityDamageEvent event) {
		if(event.getEntity() instanceof Player) return;
		if(event.getCause() == DamageCause.LAVA || event.getCause() == DamageCause.FIRE) {
			event.setCancelled(true);
			event.setDamage(0);
		}
		if(event.getCause() == DamageCause.FIRE_TICK){
		    if(event.getEntity().getFireTicks() > 30){
		        event.getEntity().setFireTicks(30);
		    }
		}
		if(isLavaNearby(event.getEntity().getLocation(), 3)){
		    event.setCancelled(true);
		    event.setDamage(0);
		    event.getEntity().setFireTicks(0);
		}
		if(event.getCause() == DamageCause.WITHER) {
			if(InstanceMechanics.isInstance(event.getEntity().getWorld().getName())) return;
			event.setCancelled(true);
			for(PotionEffect effect : ((LivingEntity) event.getEntity()).getActivePotionEffects()) {
				if(effect.getType() == PotionEffectType.WITHER) {
					((LivingEntity) event.getEntity()).removePotionEffect(effect.getType());
					break;
				}
			}
		}
	}
	public boolean isLavaNearby(Location l, int to_check){
	    int add_y = l.getBlockY() + to_check / 2;
        int add_X = l.getBlockX() + to_check;
        int add_Z = l.getBlockZ() + to_check;
        // The numbers seem to act strange and not dissapear
        int minus_X = l.getBlockX() - to_check;
        int minus_Z = l.getBlockZ() - to_check;
        int minus_Y = l.getBlockY() - to_check / 2;
        for (int x = minus_X; x < add_X; x++) {
            for (int z = minus_Z; z < add_Z; z++) {
                for (int y = minus_Y; y < add_y; y++) {
                    final Block b = l.getWorld().getBlockAt(x, y, z);
                    if (b.getType() ==  Material.LAVA) {
                        return true;
                    }
                }
            }
        }
	    return false;
	}
	
	public boolean isLavaNearby(Location l){
	    if(l.getBlock().getType() == Material.FIRE || l.getBlock().getType() == Material.LAVA ||
                l.clone().add(0, 1, 0).getBlock().getType() == Material.FIRE || l.clone().add(0, 1, 0).getBlock().getType() == Material.LAVA 
                || l.clone().add(0, -1, 0).getBlock().getType() == Material.FIRE || l.clone().add(0, -1, 0).getBlock().getType() == Material.LAVA
                ||  l.clone().add(0, -2, 0).getBlock().getType() == Material.FIRE || l.clone().add(0, -2, 0).getBlock().getType() == Material.LAVA){
	        return true;
	    }
	    return false;
	}
	@EventHandler
	public void onNPCDamage(EntityDamageEvent e){
	    if(e.getEntity() instanceof Player){
	        Player p = (Player) e.getEntity();
	        if(!p.hasMetadata("NPC"))return;
	        e.setCancelled(true);
	    }
	}
	@EventHandler
	public void onEntityTarget(EntityTargetLivingEntityEvent e){
	    if(e.getTarget() instanceof Player && e.getTarget().hasMetadata("NPC")){
	        e.setCancelled(true);
	        e.setTarget(null);
	    }
	    if(e.getEntity() instanceof LivingEntity){
	        LivingEntity le = (LivingEntity) e.getEntity();
	        if(le.getType() == EntityType.ZOMBIE || le.getType() == EntityType.SKELETON || le.getType() == EntityType.SILVERFISH){
	            ItemStack item_in_hand = le.getEquipment().getItemInHand();
	            if(item_in_hand == null){
	                return;
	            }
	            if(!isStaff(item_in_hand)){
	                ((CraftLivingEntity)le).getHandle().getAttributeInstance(GenericAttributes.a).setValue(.35D);
	            }
	        }
	    }
	}
	
	public boolean isStaff(ItemStack is){
	    if(is == null){
	        return false;
	    }
	    Material m = is.getType();
	    if(m == Material.WOOD_HOE || m == Material.STONE_HOE || m == Material.IRON_HOE || m == Material.DIAMOND_HOE || m == Material.GOLD_HOE){
	        return true;
	    }
	    return false;
	}
	@EventHandler
	public void onFireballExplodeEvent(ProjectileHitEvent e) {
		if(e.getEntity() instanceof LargeFireball) {
			LivingEntity shooter = (LivingEntity) ((Projectile) e.getEntity()).getShooter();
			if(shooter instanceof Ghast) {
				for(Entity ent : e.getEntity().getNearbyEntities(4, 4, 4)) {
					if(ent instanceof Player) {
						Player pl = (Player) ent;
						double max_hp = HealthMechanics.getMaxHealthValue(pl.getName());
						double dmg = max_hp * 0.15D;
						pl.damage(dmg, shooter);
						pl.setFireTicks(40);
					}
				}
				
				if(new Random().nextInt(10) == 0) {
					// 10% chance of adds on explosion.
					Location hit_loc = e.getEntity().getLocation();
					spawnTierMob(hit_loc, EntityType.MAGMA_CUBE, 2, -1, hit_loc, false, "", "Lesser Spawn of Inferno", true, 3);
					spawnTierMob(hit_loc, EntityType.MAGMA_CUBE, 2, -1, hit_loc, false, "", "Lesser Spawn of Inferno", true, 3);
					spawnTierMob(hit_loc, EntityType.MAGMA_CUBE, 2, -1, hit_loc, false, "", "Lesser Spawn of Inferno", true, 3);
				}
			}
		}
		
		if(e.getEntity() instanceof SmallFireball) {
			LivingEntity shooter = (LivingEntity) ((Projectile) e.getEntity()).getShooter();
			boolean ignite = false;
			if(shooter instanceof Blaze) {
				ItemStack weapon = shooter.getEquipment().getItemInHand();
				String dmg_data = ItemMechanics.getDamageData(weapon);
				if(dmg_data.equalsIgnoreCase("no")) { return; }
				int dmg = Integer.parseInt(dmg_data.substring(0, dmg_data.indexOf(":")));
				// Blaze fireball.
				
				int effect = new Random().nextInt(5); // 0=magma_cubes, 1=inferno
				
				if(effect == 0) {
					Location spawn_loc = e.getEntity().getLocation();
					int tier = getMobTier(shooter);
					int tier_to_spawn = tier;
					if(tier_to_spawn > 1) {
						tier_to_spawn--;
					}
					
					int number_to_spawn = 1; //new Random().nextInt(0);
					
					while(number_to_spawn > 0) {
						number_to_spawn--;
						spawnTierMob(spawn_loc, EntityType.MAGMA_CUBE, tier_to_spawn, -1, spawn_loc, false, "", "", true, 3);
					}
				}
				
				if(effect != 0) {
					Location fire_epicenter = e.getEntity().getLocation().add(0, 1, 0);
					if(fire_epicenter.getBlock().getType() == Material.AIR) {
						fire_epicenter.getBlock().setType(Material.FIRE);
					}
					ignite = true;
				}
				
				for(Entity ent : e.getEntity().getNearbyEntities(4, 4, 4)) {
					if(ent instanceof Player) {
						Player pl = (Player) ent;
						if(ignite == true) {
							pl.setFireTicks(80);
						}
						pl.damage(dmg, shooter);
					}
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onChunkLoad(ChunkLoadEvent e) {
		
		if(!(e.getWorld().getName().equalsIgnoreCase(main_world_name))) { return; // No need to do this shit with realms.
		}
		
		final Chunk c = e.getChunk();
		
		//Thread t = new Thread(new Runnable(){
		//	public void run(){
		addLoadedChunk(c);
		//	}
		//});
		//
		//t.start();
		// Multithread the loading process, make sure this doesn't piss it off lol.
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		final Player p = e.getPlayer();
		Location cloc = p.getLocation().getChunk().getBlock(0, 0, 0).getLocation();
		chunks_to_unload.remove(cloc);
		if(!(loaded_chunks.contains(cloc))) {
			loaded_chunks.add(cloc);
		}
		List<Location> to_remove = new ArrayList<Location>();
		Location p_loc = p.getLocation();
		player_locations.put(p.getName(), p_loc);
		
		for(Location l : mob_spawn_check_delay.keySet()) {
			if(l.distanceSquared(p_loc) <= 6400) {
				to_remove.add(l);
			}
		}
		for(Location l : to_remove) {
			mob_spawn_check_delay.remove(l);
		}
		
		Main.plugin.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
			public void run() {
				if(loot_buff == true) {
					int minutes_left = (int) (((loot_buff_timeout - System.currentTimeMillis()) / 1000.0D) / 60.0D);
					if(p != null) {
						p.sendMessage("");
						p.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + ">> " + ChatColor.UNDERLINE + "+20% Global Drop Rates" + ChatColor.GOLD + " is active for " + ChatColor.UNDERLINE + minutes_left + ChatColor.RESET + ChatColor.GOLD + " more minute(s)!");
						p.sendMessage("");
					}
				}
			}
		}, 40L);
		
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerMoveEvent(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		if(p.getPlayerListName().equalsIgnoreCase("")) { return; }
		if(p.hasMetadata("NPC")) { return; }
		Location l = e.getTo();
		player_locations.put(p.getName(), l);
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onChunkUnload(ChunkUnloadEvent e) {
		if(Hive.shutting_down) { return; // We're shutting down, vanilla can handle.
		}
		if(!e.getWorld().getName().equalsIgnoreCase(main_world_name)) { return; }
		
		Location loc = e.getChunk().getBlock(0, 0, 0).getLocation();
		//chunks_to_unload.add(loc);
		/*if(!(loaded_chunks.contains(loc))){
			log.info("[MM] Chunk was not registered. Skipping custom unload.");
			return; // Something ain't right.
		}*/
		if(loaded_chunks.contains(loc)) {
			e.setCancelled(true);
		} else {
			loaded_chunks.add(loc);
			e.setCancelled(true);
		}
		/*Chunk c = e.getChunk();
		Location l = c.getBlock(0, 0, 0).getLocation();
		boolean nearby = false;
		for(Player pl : this.getServer().getOnlinePlayers()){
			if(!pl.getWorld().getName().equalsIgnoreCase(e.getWorld().getName())){continue;}
			if(pl.getPlayerListName().equalsIgnoreCase("")){continue;}
			if(pl.getLocation().distanceSquared(l) <= 16384){ // A player is 64 blocks from the 0,0 point of the chunk.
				nearby = true;
				break;
			}
		}

		if(nearby){
			e.setCancelled(true);
			return;
		}

		if(e.getChunk().getEntities().length > 0){
			e.setCancelled(true);
		}

		for(Entity ent : c.getEntities()){
			if(ent.getType() == EntityType.PLAYER){
				e.setCancelled(false);
				return;
			}
		}

		for(Entity ent : c.getEntities()){
			if(ent instanceof LivingEntity && mob_health.containsKey(ent)){
				//log.info("[MM] Killing off " + ent.toString() + "...cuhnk unload");
				no_delay_kills.add(ent);
				LivingEntity le = (LivingEntity)ent;
				le.damage(le.getHealth());
				/*Location loc = getMobsHomeSpawner(ent);
				if(loc != null){
					if(loaded_mobs.containsKey(loc)){
						loaded_mobs.remove(loc); // Remove them from main thread list, they're no longer loaded.
						// Will this cause a problem, what if some are spawned and other aren't...
					}
				}
				continue;
			}
			ent.remove();
		 }

		c.unload(true);*/
	}
	
	public void processPossibleMobSpawns() {
		for(Map.Entry<Location, List<String>> entry : mob_to_spawn.entrySet()) { // ALL mobs are in mob_to_spawn.
			boolean player_near = false;
			boolean player_around = false;
			
			Location loc = entry.getKey();
			//List<String> mob_list = entry.getValue();
			
			if(InstanceMechanics.isInstance(loc.getWorld().getName())) {
				continue;
			}
			
			if(loaded_mobs.containsKey(loc)) {
				// They're loaded, so let that event take care of them.
				continue;
			}
			
			boolean chunk_loaded = false;
			for(Location l : player_locations.values()) {
				if(!l.getWorld().getName().equalsIgnoreCase(loc.getWorld().getName())) {
					continue;
				}
				
				Location copy_p_loc = l.clone(); // No height distance needs to be calculated.
				copy_p_loc.setY(loc.getY());
				
				if(copy_p_loc.distanceSquared(loc) <= 4096) { // A player is 80 (160) blocks from the 0,0 point of the chunk. View distance = 80 (80) blocks.
					chunk_loaded = true;
					break;
					// Somewhat near.
				}
			}
			
			/*for(Location rloc : RealmMechanics.portal_map.keySet()){
				if(!(rloc.getBlock().getType() == Material.PORTAL)){
					RealmMechanics.portal_map.remove(rloc);
					continue;
				}
				if(!rloc.getWorld().getName().equalsIgnoreCase(loc.getWorld().getName())){
					continue;
				}
				Location copy = rloc.clone();
				copy.setY(loc.getY());
				if(copy.distanceSquared(loc) <= 6400){
					chunk_loaded = true;
					break;
				}
			}*/
			
			if(!chunk_loaded) {
				continue;
			}
			
			/*if(!(loaded_chunks.contains(loc.getChunk().getBlock(0, 0, 0).getLocation()))){
				continue;
				// Chunk is not loaded.
			}*/
			
			if(mob_spawn_check_delay.containsKey(loc) && ((System.currentTimeMillis() - mob_spawn_check_delay.get(loc)) <= (10 * 1000))) {
				continue; // No one was near within last 5 seconds.
			}
			
			// We're going to see if players are nearby.
			for(Location l : player_locations.values()) {
				if(!l.getWorld().getName().equalsIgnoreCase(loc.getWorld().getName())) {
					continue;
				}
				
				Location copy_p_loc = l.clone(); // No height distance needs to be calculated.
				copy_p_loc.setY(loc.getY());
				
				double distance = copy_p_loc.distanceSquared(loc);
				
				if(distance <= 4096) { // A player is 80 (160) blocks from the 0,0 point of the chunk. View distance = 80 (80) blocks.
					player_near = true;
					player_around = true;
					break;
				}
				if(distance <= 6400) { // 80 blocks, chunk is loaded.
					player_around = true;
					continue;
					// Somewhat near.
				}
			}
			
			if(player_near == false && player_around == false) { // Chunk is likely unloaded / queued to unload, no one around.
				mob_spawn_check_delay.put(loc, System.currentTimeMillis());
				// We won't check again for a few seconds to save CPU.
				continue;
			}
			
			if(player_near == false && player_around == true) {
				// Chunk is loaded, check again in a second.
				continue;
			}
			
			// If the code reaches this point, the mob is good to spawn, so let's add it to a list that the main thread will accesss.
			if(!loaded_mobs.containsKey(loc)) {
				loaded_mobs.put(loc, entry.getValue());
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	public void MobSpawnEvent() {
		// @zombie:1-1,skeleton:1-2,skeleton:2-3@30#1-5$
		List<Location> to_remove = new ArrayList<Location>();
		
		for(Map.Entry<Location, List<String>> entry : loaded_mobs.entrySet()) {
			Location loc = entry.getKey();
			
			if(loc == null) {
				continue;
			}
			
			boolean is_instance = InstanceMechanics.isInstance(loc.getWorld().getName());
			
			if(entry.getValue().size() <= 0) {
				continue;
			}
			
			/*if(loc.getY() > 128.0D){
				continue;
			}*/
			
			if(!is_instance) {
				if(chunks_to_unload.contains(loc)) {
					continue;
				}
				
				boolean chunk_loaded = false;
				
				for(Location l : player_locations.values()) {
					if(!l.getWorld().getName().equalsIgnoreCase(loc.getWorld().getName())) {
						continue;
					}
					Location copy_p_loc = l.clone(); // No height distance needs to be calculated.
					copy_p_loc.setY(loc.getY());
					
					if(copy_p_loc.distanceSquared(loc) <= 6400) { // A player is 80 (160) blocks from the 0,0 point of the chunk. View distance = 80 (80) blocks.
						chunk_loaded = true;
						break;
						// Somewhat near.
					}
				}
				
				for(Location rloc : RealmMechanics.portal_map.keySet()) {
					if(!(rloc.getBlock().getType() == Material.PORTAL)) {
						RealmMechanics.portal_map.remove(rloc);
						continue;
					}
					if(!rloc.getWorld().getName().equalsIgnoreCase(loc.getWorld().getName())) {
						continue;
					}
					Location copy = rloc.clone();
					copy.setY(loc.getY());
					if(copy.distanceSquared(loc) <= 6400) {
						chunk_loaded = true;
						break;
					}
				}
				
				if(!chunk_loaded) {
					// Ok so it can be removed.
					// We should remove them from this gay list.
					Location l = loc.getChunk().getBlock(0, 0, 0).getLocation();
					if(!(never_unload_chunks.contains(l))) {
						to_remove.add(loc);
						continue;
					}
				}
			}
			
			try {
				String meta_data = "";
				List<String> local_spawning_data = new ArrayList<String>(entry.getValue()); // All the mobs we need to check to spawn at this location.
				String spawn_data = mob_spawns.get(loc);
				
				//log.info(spawn_data);
				String loc_range[] = spawn_data.substring(spawn_data.indexOf("#") + 1, spawn_data.lastIndexOf("$")).split("-");
				int tier_level = 1;
				if(spawn_data.contains("%")){
				    tier_level = Integer.parseInt(spawn_data.split("$")[1].split("%")[0]);
				}else{
				    tier_level = new Random().nextInt(3) + 1;
				}
				int min_xz = Integer.parseInt(loc_range[0]);
				int max_xz = Integer.parseInt(loc_range[1]);
				
				long cur_time = System.currentTimeMillis();
				
				List<String> new_data = new ArrayList<String>(local_spawning_data);
				
				for(String s : local_spawning_data) { // S has the format of: mob_num:when_to_spawn(long)
					long time_to_spawn = Long.parseLong(s.split(":")[1]);
					
					if(cur_time - time_to_spawn < 0) {
						continue; // Not yet time to spawn.
					}
					
					int mob_num = Integer.parseInt(s.split(":")[0]);
					if(spawned_mobs.containsKey(loc)) {
						List<Integer> lspawned_mobs = spawned_mobs.get(loc);
						if(lspawned_mobs.contains(mob_num)) {
							continue;
							// This specific mob is already alive! Skipping...
						}
					}
					
					String lmob_data = getMobDataFromNum(loc, mob_num);
					String mob_type = lmob_data.split(":")[0].replaceAll("\\*", "");
					
					String custom_name = "";
					if(lmob_data.contains("(")) {
						custom_name = lmob_data.substring(lmob_data.indexOf("(") + 1, lmob_data.indexOf(")"));
					}
					
					if(mob_type.contains("(")) {
						mob_type = mob_type.substring(0, mob_type.indexOf("("));
					}
					
					mob_type = mob_type.toUpperCase();
					
					if(mob_type.equalsIgnoreCase("imp")) {
						mob_type = EntityType.PIG_ZOMBIE.getName();
						meta_data = "imp";
					}
					
					if(mob_type.equalsIgnoreCase("acolyte")) {
						mob_type = "SKELETON";
						meta_data = "acolyte";
					}
					
					if(mob_type.equalsIgnoreCase("daemon")) {
						mob_type = EntityType.PIG_ZOMBIE.getName();
						meta_data = "daemon";
					}
					
					if(mob_type.equalsIgnoreCase("MagmaCube")) {
						mob_type = EntityType.MAGMA_CUBE.getName();
						meta_data = "";
					}
					
					if(mob_type.equalsIgnoreCase("skeleton2") || mob_type.equalsIgnoreCase("wither") || mob_type.equalsIgnoreCase("skeleton1")) {
						mob_type = "SKELETON";
						meta_data = "wither";
					}
					
					if(mob_type.equalsIgnoreCase("spider1")) {
						mob_type = "SPIDER";
					}
					
					if(mob_type.equalsIgnoreCase("spider2")) {
						mob_type = "CAVE_SPIDER";
					}
					
					if(mob_type.equalsIgnoreCase("troll1")) {
						mob_type = "ZOMBIE";
						meta_data = "troll";
					}
					
					if(mob_type.equalsIgnoreCase("goblin")) {
						mob_type = "ZOMBIE";
						meta_data = "goblin";
					}
					
					if(mob_type.equalsIgnoreCase("bandit")) {
						mob_type = "SKELETON";
						meta_data = "bandit";
					}
					
					if(mob_type.equalsIgnoreCase("monk")) {
						mob_type = "SKELETON";
						meta_data = "monk";
					}
					
					if(mob_type.equalsIgnoreCase("golem")) {
						mob_type = "IRON_GOLEM";
					}
					
					if(mob_type.equalsIgnoreCase("mooshroom")) {
						mob_type = "MUSHROOM_COW";
					}
					
					if(mob_type.equalsIgnoreCase("lizardman")) {
						mob_type = "ZOMBIE";
						meta_data = "lizardman";
					}
					
					if(mob_type.equalsIgnoreCase("naga")) {
						mob_type = "ZOMBIE";
						meta_data = "naga";
					}
					
					if(mob_type.equalsIgnoreCase("tripoli1")) {
						mob_type = "SKELETON";
						meta_data = "tripoli solider";
					}
					
					if(mob_type.equalsIgnoreCase("ocelot")) {
						// Fix for leopards not being implemented, just spawn wolves.
						mob_type = "WOLF";
					}
					
					int tier = 1;
					boolean elite = false;
					try {
						tier = Integer.parseInt(lmob_data.split(":")[1].split("-")[0]);
					} catch(NumberFormatException nfe) {
						//nfe.printStackTrace();
						continue;
						// A # in the spawner was probably entered wrong.
					}
					
					if(lmob_data.contains("*")) {
						elite = true;
					}
					
					Location local_loc = getRandomLocation(loc, ((loc.getX() - min_xz) - max_xz), ((loc.getX() + min_xz) + max_xz), ((loc.getZ() - min_xz) - max_xz), ((loc.getZ() + min_xz) + max_xz));
					
					if((local_loc.getBlock().getType() != Material.AIR || local_loc.add(0, 1, 0).getBlock().getType() != Material.AIR)) {
						local_loc = loc.clone().add(0, 1, 0);
					} else {
						local_loc.subtract(0, 1, 0);
					}
					
					/*if ((local_loc.getBlock().getType() != Material.AIR || local_loc.add(0, 1, 0).getBlock().getType() != Material.AIR)) { 
						local_loc = getRandomLocation(loc, ((loc.getX() - min_xz) - max_xz), ((loc.getX() + min_xz) + max_xz), ((loc.getZ() - min_xz) - max_xz), ((loc.getZ() + min_xz) + max_xz));
					}
					else{
						local_loc.subtract(0, 1, 0);
					}*/
					
					//local_loc.subtract(0, 1, 0);
					
					/*if ((local_loc.getBlock().getType() != Material.AIR || local_loc.add(0, 1, 0).getBlock().getType() != Material.AIR)) { 
						//log.info("[MM] Cancelling mob spawn, could not find a spot for LOCATION: " + loc.toString());
						continue; // Don't spawn them, didn't find a spot.
					}
					else{
						local_loc.subtract(0, 1, 0);
					}

					local_loc.add(0, 1, 0);*/
					
					EntityType et = EntityType.fromName(mob_type);
					if(mob_type.equalsIgnoreCase("IRON_GOLEM")) {
						et = EntityType.IRON_GOLEM;
					}
					if(mob_type.equalsIgnoreCase("CAVE_SPIDER")) {
						et = EntityType.CAVE_SPIDER;
					}
					if(mob_type.equalsIgnoreCase("MUSHROOM_COW")) {
						et = EntityType.MUSHROOM_COW;
					}
					if(mob_type.equalsIgnoreCase("PIG_ZOMBIE")) {
						et = EntityType.PIG_ZOMBIE;
					}
					if(mob_type.equalsIgnoreCase("SKELETON")) {
						et = EntityType.SKELETON;
					}
					
					if(et == null) {
						et = EntityType.ZOMBIE;
						// Default to zombie if something went wrong.
					}
					
					if(DuelMechanics.isDamageDisabled(local_loc)) {
						continue; // Don't spawn in damage disabled.
					}
					
					//if(loc == null){
					// .get(0) is null occasionally on the mobs_being_spawned process list?
					//	continue;
					//}
					
					ArrayList<Object> main_thread = new ArrayList<Object>();
					main_thread.add(loc);
					main_thread.add(local_loc);
					main_thread.add(et);
					main_thread.add(tier);
					main_thread.add(mob_num);
					main_thread.add(elite);
					main_thread.add(meta_data);
					main_thread.add(custom_name);
					main_thread.add(tier_level);
					mobs_being_spawned.add(main_thread);
					
					//if(spawnTierMob(local_loc, et, tier, mob_num, loc, elite, meta_data) == true){
					// If we succeed to spawn the mob, we need to register them to the spawned_mobs list.
					List<Integer> spawned_mob_nums = new ArrayList<Integer>();
					if(spawned_mobs.containsKey(loc)) {
						spawned_mob_nums = spawned_mobs.get(loc);
						spawned_mob_nums.add(new Integer(mob_num));
					} else if(!(spawned_mobs.containsKey(loc))) {
						spawned_mob_nums = new ArrayList<Integer>(Arrays.asList(mob_num));
					}
					
					spawned_mobs.put(loc, spawned_mob_nums);
					new_data.remove(s); // Remove this entry from the local spawning data packet. Afterwards, we will update the Location, List<String> hashmap with this data.
					//}
					
					//last_mob_spawn_tick.put(loc, (cur_time));
				}
				
				loaded_mobs.put(loc, new_data);
				// Even if the new_data is an empty list, we'll put it back in so that the processPossibleSpawn() event doesn't think it has to process us again.
				// Instead, we will remove the location entry from loaded_mobs on a chunk unload / cleanup event.
				
				if(is_instance && new_data.size() <= 0) {
					loaded_mobs.remove(loc); // They should only be spawned once.
					// If new_data size > 0, they couldn't find a place to spawn.
				}
				
			} catch(NullPointerException npe) {
				log.info("Critical mob spawn failure.");
				npe.printStackTrace();
				continue;
			}
		}
		
		for(Location l : to_remove) {
			loaded_mobs.remove(l);
		}
		
	}
	
	// We have X and Z. 
	// We need to get a location that is:
	// X +/- min through X +/- max
	// Z +/- min through Z +/- max
	
	public Location getRandomLocation(Location l, double Xminimum, double Xmaximum, double Zminimum, double Zmaximum) {
		World world = l.getWorld();
		
		double randomX = 0;
		double randomZ = 0;
		
		double x = 0.0D;
		double y = 0.0D;
		double z = 0.0D;
		
		randomX = Xminimum + (int) (Math.random() * (Xmaximum - Xminimum + 1)); //get random X
		randomZ = Zminimum + (int) (Math.random() * (Zmaximum - Zminimum + 1)); //get random Z
		
		x = randomX;
		y = l.getY();
		z = randomZ;
		
		x = x + 0.5; // add .5 so they spawn in the middle of the block
		z = z + 0.5;
		y = y + 2.0;
		return new Location(world, x, y, z);
	}
	
	public static int getMHealth(Entity e) {
		/*if(!(mob_loot.containsKey(e))){
			return 1; // Not custom, or something is wrong.
		}*/
		if(!(mob_health.containsKey(e))) {
			// They're dead.
			//log.info("No health data for " + e.toString());
			return 0;
			//mob_health.put(e, calculateMobHP(e));
		}
		return mob_health.get(e);
	}
	
	public static int getBarLength(int tier) {
		if(tier == 1) { return 25; }
		if(tier == 2) { return 30; }
		if(tier == 3) { return 35; }
		if(tier == 4) { return 40; }
		if(tier == 5) { return 50; }
		return 25;
	}
	
	public static String generateOverheadBar(Entity ent, double cur_hp, double max_hp, int tier, boolean elite) {
		int max_bar = getBarLength(tier);
		
		ChatColor cc = null;
		
		DecimalFormat df = new DecimalFormat("##.#");
		double percent_hp = (double) (Math.round(100.0D * Double.parseDouble((df.format((cur_hp / max_hp)))))); // EX: 0.5054134131
		
		if(percent_hp <= 0 && cur_hp > 0) {
			percent_hp = 1;
		}
		
		if(BossMechanics.boss_map.containsKey(ent)) {
			max_bar = 60;
		}
		
		double percent_interval = (100.0D / max_bar);
		int bar_count = 0;
		
		cc = ChatColor.GREEN;
		if(percent_hp <= 45) {
			cc = ChatColor.YELLOW;
		}
		if(percent_hp <= 20) {
			cc = ChatColor.RED;
		}
		if(BossMechanics.boss_map.containsKey(ent)) {
			cc = ChatColor.GOLD;
		}
		if(special_attack.containsKey(ent) && cur_hp > 0) {
			cc = ChatColor.LIGHT_PURPLE;
		}
		
		String return_string = cc + ChatColor.BOLD.toString() + "║" + ChatColor.RESET.toString() + cc.toString() + "";
		if(elite || BossMechanics.boss_map.containsKey(ent)) {
			return_string += ChatColor.BOLD.toString();
		}
		
		while(percent_hp > 0 && bar_count < max_bar) {
			percent_hp -= percent_interval;
			bar_count++;
			return_string += "|";
		}
		
		return_string += ChatColor.BLACK.toString();
		
		if(elite) {
			return_string += ChatColor.BOLD.toString();
		}
		
		while(bar_count < max_bar) {
			return_string += "|";
			bar_count++;
		}
		
		return_string = return_string + cc + ChatColor.BOLD.toString() + "║";
		
		// TODO: Generate activity bar status
		/*if(special_attack.containsKey(ent) && cur_hp > 0){

			return_string += ChatColor.LIGHT_PURPLE.toString();

			int special_bar_length = 5;
			int charge_state = special_attack.get(ent);
			int charge_count = charge_state;

			while(charge_state > 0){
				return_string += "|";
				charge_state--;
			}

			return_string += ChatColor.BLACK.toString();

			while((special_bar_length - charge_count) > 0){
				return_string += "|";
				charge_count++;
			}

			return_string += cc.toString() + ChatColor.BOLD + "â•‘";
		}
		/*else{
			return_string += ChatColor.BLACK.toString() + "|||||" + cc.toString() + ChatColor.BOLD.toString() + "â•‘";
		}*/
		
		return return_string;
		// 20 Bars, that's 5% HP per bar
	}
	
	public static void subtractMHealth(Entity e, int amount) {
		if(!(mob_health.containsKey(e))) {
			log.info("[MonsterMechanics] Skipping subtractMHealth() for entity " + e.toString() + " due to no mob_health.");
			return; // No data available, GG.
		}
		int old_hp = mob_health.get(e);
		int new_hp = old_hp - amount;
		//int max_health = max_mob_health.get(e);
		
		boolean is_elite = false;
		int tier = getMobTier(e);
		//List<ItemStack> ent_gear = mob_loot.get(ent);
		ItemStack weapon = CraftItemStack.asBukkitCopy(((CraftEntity) e).getHandle().getEquipment()[0]);
		if(weapon.getEnchantments().containsKey(Enchantment.KNOCKBACK)) {
			//log.info("ELITE!");
			is_elite = true;
		}
		
		if(new_hp > 0) {
			mob_health.put(e, new_hp);
			if(max_mob_health.containsKey(e)) {
				LivingEntity le = (LivingEntity) e;
				le.setCustomName(generateOverheadBar(e, new_hp, max_mob_health.get(e), tier, is_elite));
				le.setCustomNameVisible(true);
			}
		} else if(new_hp <= 0) {
			mob_health.put(e, 0);
			final LivingEntity le = (LivingEntity) e;
			//System.out.print("SET MOB HEALTH TO 0");
			if(max_mob_health.containsKey(e)) {
				le.setCustomName(generateOverheadBar(e, 0, max_mob_health.get(e), tier, is_elite));
			}
			le.damage(le.getHealth());
			if(le.getVehicle() != null) {
				Entity mount = le.getVehicle();
				le.eject();
				mount.remove();
			}
			new BukkitRunnable() {
                public void run() {
                    //This addresses mobs being 1 hp.
                    if(le != null && !le.isDead()){
                        //This needs to be called so the mobs will be respawned and stuff.
                        List<ItemStack> drops = new ArrayList<ItemStack>();
                        if(mob_loot.containsKey(le)){
                            drops = mob_loot.get(le);
                        }
                        EntityDeathEvent e = new EntityDeathEvent(le, drops);
                        Bukkit.getPluginManager().callEvent(e);
                        le.setHealth(0);
                    }
                }
            }.runTaskLater(Main.plugin, 1L);
			
		}
	}
	
	public void setMHealth(Entity e, int health) {
		mob_health.put(e, health);
	}
	
	public void pushAwayEntity(Player p, Entity entity, double speed) {
		// Get velocity unit vector:
		org.bukkit.util.Vector unitVector = entity.getLocation().toVector().subtract(p.getLocation().toVector()).normalize();
		// Set speed and push entity:
		entity.setVelocity(unitVector.multiply(speed));
	}
	
	public static void pushAwayPlayer(Entity entity, Player p, double speed) {
		// Get velocity unit vector:
		org.bukkit.util.Vector unitVector = p.getLocation().toVector().subtract(entity.getLocation().toVector()).normalize();
		// Set speed and push entity:
		double e_y = entity.getLocation().getY();
		double p_y = p.getLocation().getY();
		
		Material m = p.getLocation().subtract(0, 1, 0).getBlock().getType();
		
		if((p_y - 1) <= e_y || m == Material.AIR) {
			p.setVelocity(unitVector.multiply(speed));
		}
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		if(mob_spawn_location.containsKey(p)) {
			Location loc = mob_spawn_location.get(p);
			loc.getBlock().setType(Material.AIR);
			mob_spawn_location.remove(p);
			mob_spawn_step.remove(p);
			mob_spawn_data.remove(p);
		}
		player_locations.remove(p.getName());
	}
	
	@EventHandler
	public void onEntityCombus(EntityCombustEvent event) {
		if(!(event.getEntity() instanceof Player)) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onChickenLay(ItemSpawnEvent event) {
		if(event.getEntity().getItemStack().getType() == Material.EGG) {
			for(Entity e : event.getEntity().getNearbyEntities(.5, .5, .5)) {
				if(e instanceof Chicken) {
					//Chicken laid it so cancel
					event.setCancelled(true);
					return;
				}
			}
		}
		if(event.getEntity().getItemStack().getType() == Material.LEASH) {
			//Should never really drop these items
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e) {
		// @zombie:1-1,skeleton:1-2,skeleton:2-3@30#1-5$
		Player p = e.getPlayer();
		if(e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			Location loc = e.getClickedBlock().getLocation();
			if(mob_spawns.containsKey(loc) && loc.getBlock().getType() == Material.MOB_SPAWNER) {
				String mob_data = mob_spawns.get(loc);
				String[] m_list = mob_data.substring(1, mob_data.lastIndexOf("@")).split(",");
				String formal_m_list = "";
				for(String s : m_list) {
					formal_m_list += s.split(":")[0] + ":" + s.substring(s.indexOf(":") + 1, s.indexOf("-")) + ", ";
				}
				p.sendMessage("");
				p.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + loc.getX() + "," + loc.getY() + "," + loc.getZ() + " MOB SPAWNER DATA");
				p.sendMessage(ChatColor.YELLOW + "mob_list: " + ChatColor.WHITE + formal_m_list);
				p.sendMessage(ChatColor.YELLOW + "respawn_delay: " + ChatColor.WHITE + mob_data.substring(mob_data.lastIndexOf("@") + 1, mob_data.indexOf("#")) + " seconds");
				p.sendMessage(ChatColor.YELLOW + "spawn_range: " + ChatColor.WHITE + mob_data.substring(mob_data.indexOf("#") + 1, mob_data.indexOf("$")) + " blocks");
				p.sendMessage(ChatColor.GRAY + "DEBUG: " + mob_data);
			}
		}
	}
	
	@EventHandler
	public void onAsyncChatEvent(AsyncPlayerChatEvent e) {
		Player p = e.getPlayer();
		if(!(mob_spawn_step.containsKey(p))) { return; }
		
		e.setCancelled(true);
		
		if(e.getMessage().equalsIgnoreCase("cancel")) {
			mob_spawn_step.remove(p);
			mob_spawn_data.remove(p);
			mob_spawn_location.get(p).getBlock().setType(Material.AIR);
			mob_spawn_location.remove(p);
			
			p.sendMessage(ChatColor.RED + "Mob spawner placement cancelled. No changes saved.");
			return;
		}
		
		int step = mob_spawn_step.get(p);
		String spawn_data = mob_spawn_data.get(p);
		// @zombie(custom_name):1-1,skeleton:1-2,skeleton:2-3@30#1-5$4%
		
		if(step == 0) {
			String mob_list = e.getMessage().replaceFirst(" ", "");
			
			int mob_num = 1;
			spawn_data = "@";
			
			if(!(mob_list.contains(","))) {
				p.sendMessage(ChatColor.RED + "No ',' present, if only spawning one mob, include a trailing ','");
				p.sendMessage(ChatColor.GRAY + "EX: " + ChatColor.DARK_GRAY + "zombie(custom_name):1,skeleton:1,skeleton:2");
				return;
			}
			if(!(mob_list.contains(":"))) {
				p.sendMessage(ChatColor.RED + "No ':TIER#' present with one or more mobs defined.");
				p.sendMessage(ChatColor.GRAY + "EX: " + ChatColor.DARK_GRAY + "zombie(custom_name):1,skeleton:1,skeleton:2");
				return;
			}
			
			String[] parse_mob_list = mob_list.split(",");
			for(String s : parse_mob_list) {
				if(!s.contains("enderman") && !s.contains("witch") && !s.contains("silverfish") && !s.contains("blaze") && !s.contains("magmacube") && !s.contains("acolyte") && !s.contains("daemon") && !s.contains("imp") && !s.contains("tripoli1") && !s.contains("ocelot") && !s.contains("naga") && !s.contains("lizardman") && !s.contains("spider2") && !s.contains("mooshroom") && !s.contains("wolf") && !s.contains("ocelot") && !s.contains("bat") && !s.contains("slime") && !s.contains("golem") && !s.contains("troll1") && !s.contains("skeleton2") && !s.contains("spider1") && !s.contains("zombie") && !s.contains("skeleton") && !(s.contains("goblin")) && !(s.contains("bandit")) && !(s.contains("monk")) && !(s.contains("cow")) && !(s.contains("pig") && !(s.contains("chicken")) && !(s.contains("sheep")))) {
					p.sendMessage(ChatColor.RED + "Invalid Mob '" + s + "' in mob list.");
					return;
				}
				if(!(s.contains(":"))) {
					p.sendMessage(ChatColor.RED + "No TIER defined for mob '" + s + "' in mob list.");
					return;
				}
				//String custom_name = "";
				if(s.contains("(")) {
					//custom_name = "(" + s.substring(s.indexOf("(") + 1, s.indexOf(")")) + ")";
				}
				
				spawn_data += s + "-" + mob_num + ",";
				mob_num++;
			}
			
			spawn_data += "@";
			
			if(InstanceMechanics.isInstance(p.getWorld().getName())) {
				mob_spawn_step.put(p, 2);
				spawn_data += "0#"; // 0 for respawn time, cause they won't respawn.
				p.sendMessage("");
				p.sendMessage(ChatColor.YELLOW + "Step 3 of 3: " + ChatColor.WHITE + "Please enter the min/max range (in blocks) from the mob spawner that mobs can spawn at.");
				p.sendMessage(ChatColor.YELLOW + "Note: You must enter a range with a min. val greater than 0, and a max. val less than 30.");
				p.sendMessage(ChatColor.GRAY + "EX: " + ChatColor.DARK_GRAY + "1-5" + ChatColor.GRAY + " -> Mobs will spawn between 1 and 5 blocks around the mob spawner.");
			} else {
				mob_spawn_step.put(p, 1);
				p.sendMessage("");
				p.sendMessage(ChatColor.YELLOW + "Step 2 of 3: " + ChatColor.WHITE + "Please enter the interval (in seconds) between each spawn event.");
				p.sendMessage(ChatColor.YELLOW + "Note: If the mob(s) selected to spawn at the interval are already alive, they will not spawn again.");
				p.sendMessage(ChatColor.GRAY + "EX: " + ChatColor.DARK_GRAY + "30" + ChatColor.GRAY + " -> Chance to respawn mobs every 30 seconds.");
			}
			
			mob_spawn_data.put(p, spawn_data);
			return;
		}
		
		if(step == 1) {
			int spawn_interval = 60; // Default 60 seconds if something goes
			// wrong.
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
			
			spawn_data += spawn_interval + "#";
			mob_spawn_step.put(p, 2);
			mob_spawn_data.put(p, spawn_data);
			
			p.sendMessage("");
			p.sendMessage(ChatColor.YELLOW + "Step 3 of 3: " + ChatColor.WHITE + "Please enter the min/max range (in blocks) from the mob spawner that mobs can spawn at.");
			p.sendMessage(ChatColor.YELLOW + "Note: You must enter a range with a min. val greater than 0, and a max. val less than 30.");
			p.sendMessage(ChatColor.GRAY + "EX: " + ChatColor.DARK_GRAY + "1-5" + ChatColor.GRAY + " -> Mobs will spawn between 1 and 5 blocks around the mob spawner.");
			return;
		}
		
		if(step == 2) {
			if(!(e.getMessage().contains("-"))) {
				p.sendMessage(ChatColor.RED + "No '-' present in message, please seperate min/max spawn range with a '-'");
				return;
			}
			
			int min, max;
			
			try {
				min = Integer.parseInt(e.getMessage().split("-")[0]);
				max = Integer.parseInt(e.getMessage().split("-")[1]);
			} catch(NumberFormatException nfe) {
				p.sendMessage(ChatColor.RED + "Invalid range format specified. Please follow the example format.");
				p.sendMessage(ChatColor.GRAY + "EX: " + ChatColor.DARK_GRAY + "1-5" + ChatColor.GRAY + " -> Mobs will spawn between 1 and 5 blocks around the mob spawner.");
				return;
			}
			
			if(min <= 0 || max > 30) {
				p.sendMessage(ChatColor.RED + "You either choose a minimum value that was too low (<0) or a maximum value that was too high (>30)");
				return;
			}
			
			spawn_data += min + "-" + max + "$";
			
			int delay = Integer.parseInt(spawn_data.substring(spawn_data.lastIndexOf("@") + 1, spawn_data.indexOf("#")));
			
			mob_spawn_step.remove(p);
			mob_spawn_data.remove(p);
			
			final Location mob_spawner_loc = mob_spawn_location.get(p);
			
			if(delay == 0 && InstanceMechanics.isInstance(p.getWorld().getName())) {
				HashMap<Location, String> instance_mob_spawns_copy = new HashMap<Location, String>();
				if(InstanceMechanics.instance_mob_spawns.containsKey(p.getWorld().getName())) {
					instance_mob_spawns_copy = InstanceMechanics.instance_mob_spawns.get(p.getWorld().getName());
				}
				instance_mob_spawns_copy.put(mob_spawner_loc, spawn_data);
				InstanceMechanics.instance_mob_spawns.put(p.getWorld().getName(), instance_mob_spawns_copy);
				
				List<String> new_data = new ArrayList<String>();
				for(String s : spawn_data.substring(spawn_data.indexOf("@") + 1, spawn_data.lastIndexOf("@")).split(",")) {
					if(s.equalsIgnoreCase("")) {
						continue;
					}
					int mob_num = Integer.parseInt(s.split(":")[1].split("-")[1]);
					new_data.add(mob_num + ":" + System.currentTimeMillis());
				}
				mob_spawns.put(mob_spawner_loc, spawn_data);
				loaded_mobs.put(mob_spawner_loc, new_data);
			} else {
				mob_spawns.put(mob_spawner_loc, spawn_data);
				
				List<String> new_data = new ArrayList<String>();
				for(String s : spawn_data.substring(spawn_data.indexOf("@") + 1, spawn_data.lastIndexOf("@")).split(",")) {
					if(s.equalsIgnoreCase("")) {
						continue;
					}
					int mob_num = Integer.parseInt(s.split(":")[1].split("-")[1]);
					new_data.add(mob_num + ":" + System.currentTimeMillis());
				}
				
				mob_to_spawn.put(mob_spawner_loc, new_data);
				loaded_mobs.put(mob_spawner_loc, new_data);
			}
			new BukkitRunnable() {
                public void run() {
                    mob_spawner_loc.getBlock().setType(Material.AIR);
                }
            }.runTask(Main.plugin);
			
			mob_spawn_location.remove(p);
			
			p.sendMessage("");
			p.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "MOB SPAWNER REGISTRATION COMPLETE.");
			p.sendMessage(ChatColor.GREEN + "The new mob spawn data has been synced with the spawning thread.");
			return;
		}
		
	}
	
	@EventHandler
	public void onVehicleEnter(VehicleEnterEvent e) {
		Entity ent = e.getEntered();
		if((e.getVehicle().getType() == EntityType.MINECART || e.getVehicle().getType() == EntityType.BOAT) && mob_health.containsKey(ent)) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		Location loc = e.getBlock().getLocation();
		if(mob_spawns.containsKey(loc)) {
			if(loc.getBlock().getType() == Material.MOB_SPAWNER) {
				// Unregister the mob spawner!
				Player p = e.getPlayer();
				
				if(InstanceMechanics.isInstance(p.getWorld().getName())) {
					HashMap<Location, String> instance_mob_spawns_copy = new HashMap<Location, String>();
					if(InstanceMechanics.instance_mob_spawns.containsKey(p.getWorld().getName())) {
						instance_mob_spawns_copy = InstanceMechanics.instance_mob_spawns.get(p.getWorld().getName());
					}
					instance_mob_spawns_copy.remove(loc);
					InstanceMechanics.instance_mob_spawns.put(p.getWorld().getName(), instance_mob_spawns_copy);
				} else {
					mob_spawns.remove(loc);
					mob_to_spawn.remove(loc);
				}
				
				p.sendMessage(ChatColor.GREEN + "MOB SPAWNER LOCATION UNREGISTERED.");
				p.sendMessage(ChatColor.GRAY + "DEBUG: " + mob_spawns.get(loc));
			}
		} else if(loc.getBlock().getType() == Material.MOB_SPAWNER) {
			Player p = e.getPlayer();
			if(!(mob_spawn_location.containsKey(p))) {
				e.setCancelled(true);
				p.sendMessage(ChatColor.YELLOW + "You did not initiate this mob spawner's registration, therfore you cannot destroy it.");
				p.sendMessage(ChatColor.GRAY + "It will automatically delete on a server reboot.");
				return;
			}
			mob_spawn_step.remove(p);
			mob_spawn_data.remove(p);
			mob_spawn_location.get(p).getBlock().setType(Material.AIR);
			mob_spawn_location.remove(p);
			p.sendMessage(ChatColor.RED + "Placement of new mob spawner location cancelled.");
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onBlockPlace(BlockPlaceEvent e) {
		Player p = e.getPlayer();
		Location loc = e.getBlock().getLocation();
		if(mob_spawns.containsKey(loc)) {
			p.sendMessage(ChatColor.RED + "A mob spawn point is registered at this location. Block placement cancelled.");
			p.sendMessage(ChatColor.GRAY + "Use " + ChatColor.BOLD + "/showms <radius>" + ChatColor.GRAY + " to visually view the mob points.");
			e.setCancelled(true);
			return;
		}
		
		if(!(e.getBlock().getType() == Material.MOB_SPAWNER)) { return; }
		
		if(!(p.isOp())) {
			p.setItemInHand(new ItemStack(Material.AIR));
			p.sendMessage(ChatColor.RED + "Illegal item detected, removing... (MOB SPAWNER)");
			p.sendMessage(ChatColor.GRAY + "Account flagged for manual review.");
			e.setCancelled(true);
			return;
		}
		
		if(!(Bukkit.getMotd().contains("US-0"))) {
			p.sendMessage(ChatColor.RED + "This action can only be performed on the US-0 development server.");
			p.sendMessage(ChatColor.GRAY + "Account flagged for manual review.");
			e.setCancelled(true);
			return;
		}
		
		if(!(p.getWorld().getName().equalsIgnoreCase(main_world_name)) && !(InstanceMechanics.isInstance(p.getWorld().getName()))) {
			// Realm. lolwut.
			p.sendMessage(ChatColor.RED + "Invalid world.");
			e.setCancelled(true);
			return;
		}
		
		if(mob_spawn_step.containsKey(p)) {
			e.setCancelled(true);
			p.sendMessage(ChatColor.RED + "You already have a pending mob spawner registration.");
			p.sendMessage(ChatColor.GRAY + "Relogging will clear all mob spawner registration");
			return;
		}
		
		mob_spawn_step.put(p, 0);
		mob_spawn_data.put(p, "");
		mob_spawn_location.put(p, e.getBlock().getLocation());
		
		p.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + "NEW MONSTER SPAWNER PLACED.");
		p.sendMessage(ChatColor.YELLOW + "Step 1 of 3: " + ChatColor.WHITE + "Please enter a ',' deliminated LIST of monsters that should spawn here and their tier seperated by a ':'.");
		p.sendMessage(ChatColor.YELLOW + "Mob Types: " + ChatColor.WHITE + "monk | enderman | witch | silverfish | blaze | MagmaCube | Daemon | Imp | Acolyte | Lizardman | Naga | Tripoli1 | Ocelot | Skeleton | Skeleton2 | Zombie | Golem | Goblin | Bandit | Spider1 | Spider2 | Troll1 | Cow | Bat | Ocelot | Wolf | Pig | Chicken | Sheep | Mooshroom");
		
		p.sendMessage(ChatColor.YELLOW + "To signify an ELITE mob, add a * suffix to the mob. (See example)");
		p.sendMessage(ChatColor.GRAY + "EX: " + ChatColor.DARK_GRAY + "skeleton(custom_name):1,skeleton*:1,zombie:2,skeleton:1" + ChatColor.GRAY + " -> Will have a chance to spawn 1X Tier 1 Elite Skeleton, 2X Tier 1 Skeletons and/or 1X Tier 2 Zombie.");
	}
	
	public static int getMobTier(Entity ent) {
		if(ent instanceof Ghast) { return 4; }
		if(ent.getPassenger() != null) {
			Entity r_ent = ent.getPassenger();
			ent = r_ent;
		}
		if(mob_tier.containsKey(ent)) { return mob_tier.get(ent); }
		LivingEntity le = (LivingEntity) ent;
		ItemStack i = le.getEquipment().getItemInHand();
		if(i == null) { return -1; // No tier.
		}
		int wep_tier = getItemTier(i);
		//log.info("[MonsterMechanics] No mob tier stored for entity " + ent.toString() + ", saving new value to memory. (" + wep_tier + ")");
		mob_tier.put(ent, wep_tier);
		return wep_tier;
	}
	
	@EventHandler
	public void onEntityCombustEvent(EntityCombustEvent e) {
		if(mob_health.containsKey(e.getEntity())) {
			e.setCancelled(true);
			e.getEntity().setFireTicks(0);
		}
	}
	
	@EventHandler
	public void onPotionSplashEffect(PotionSplashEvent e) {
		if(e.getEntity().getShooter() instanceof Witch) {
			LivingEntity le = (LivingEntity) e.getEntity().getShooter();
			if(mob_damage.containsKey(le)) {
				// Custom mob, witch throwing potion, do damage.
				List<Integer> dmg_range = mob_damage.get(le);
				int dmg = new Random().nextInt(dmg_range.get(1) - dmg_range.get(0)) + dmg_range.get(0);
				dmg = dmg * 2;
				for(Entity ent : e.getAffectedEntities()) {
					if(ent instanceof Player) {
						Player pl = (Player) ent;
						pl.damage(dmg, le);
						pl.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 60, 1));
					}
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
	public void onMonsterDamagedProcessEvent(EntityDamageEvent e) {
		Entity ent = e.getEntity();
		double dmg = e.getDamage();
		Entity attacker = null;
		if(e.getEntity() instanceof Player) return;
		
		/*if(ent.isInsideVehicle()){ // Riding an entity?
			Entity r_ent = ent.getVehicle();
			ent = r_ent;
		}*/
		
		if(ent.getPassenger() != null) {
			Entity r_ent = ent.getPassenger();
			ent = r_ent;
		}
		
		if(!(mob_health.containsKey(ent)) && !(PetMechanics.inv_pet_map.containsKey(ent))) {
			e.setCancelled(false);
			return;
		}
		
		if(ent instanceof Projectile) { return; }
		
		if(e.isCancelled()) { return; }
		
		if(e.getDamage() < 1) { return; }
		
		if((e.getCause() == DamageCause.FIRE || e.getCause() == DamageCause.FIRE_TICK) && ent.getWorld().getName().contains("fireydungeon")) {
			e.setCancelled(true);
			e.setDamage(0);
			return;
			// No monsters take fire damage in inferno.
		}
		
		if(e instanceof EntityDamageByEntityEvent) {
			attacker = ((EntityDamageByEntityEvent) e).getDamager();
		}
		
		if(attacker != null && attacker instanceof WitherSkull) {
			e.setCancelled(true);
			e.setDamage(0);
			return;
		}
		
		if(attacker != null && attacker instanceof Arrow) {
			LivingEntity shooter = (LivingEntity) ((Arrow) attacker).getShooter();
			if(!(shooter instanceof Player)) {
				// Don't allow other mobs to damage mobs w/ arrows.
				e.setCancelled(true);
				e.setDamage(0);
				return;
			}
		}
		
		if(ent instanceof LivingEntity) {
			LivingEntity le = (LivingEntity) ent;
			if(le.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
				// No damage for boss mobs with invinsibility.
				e.setCancelled(true);
				e.setDamage(0);
				try {
					ParticleEffect.sendToLocation(ParticleEffect.WITCH_MAGIC, le.getLocation().add(0, 0.5, 0), new Random().nextFloat(), new Random().nextFloat(), new Random().nextFloat(), 1F, 40);
				} catch(Exception err) {
					err.printStackTrace();
				}
				
				//le.getWorld().spawnParticle(le.getLocation().add(0, 0.5, 0), Particle.WITCH_MAGIC, 1F, 40);
				le.getWorld().playSound(le.getLocation(), Sound.ENDERMAN_TELEPORT, 0.5F, 0.5F);
				return;
			}
		}
		
		if(e.getCause() == DamageCause.FALL || e.getCause() == DamageCause.CONTACT || e.getCause() == DamageCause.DROWNING || e.getCause() == DamageCause.FIRE || e.getCause() == DamageCause.LAVA) {
			e.setCancelled(true);
			e.setDamage(0);
			ent.setFireTicks(0);
			return;
		}
		
		if(attacker != null && attacker instanceof Player && ent instanceof Enderman) {
			Player p_attacker = (Player) attacker;
			if(p_attacker.getLocation().distanceSquared(ent.getLocation()) <= 6) {
				// Ragdoll em.
				pushAwayPlayer(ent, p_attacker, 2F);
				p_attacker.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, (5 * 20), 0));
				p_attacker.playSound(p_attacker.getLocation(), Sound.ENDERMAN_SCREAM, 1F, 1F);
			}
		}
		
		if(!no_pathing.contains(ent) && (e.getCause() == DamageCause.SUFFOCATION || e.getCause() == DamageCause.VOID || e.getCause() == DamageCause.LAVA)) {
			e.setCancelled(true);
			e.setDamage(0);
			if(!(entities_to_remove.containsKey(ent))) {
				if(last_respawn.containsKey(ent) && ((System.currentTimeMillis() - last_respawn.get(ent)) <= 500) && !(InstanceMechanics.isInstance(ent.getWorld().getName()))) {
					// Took them less than 5 seconds to get glitched again...
					// Disable this spawn?
					no_pathing.add(ent);
					log.info("[MM] Disabling CLEANUP event for " + ent.toString() + " due to overzealous suffocation. (<500ms)");
					return;
				}
				Location home_spawn = getMobsHomeSpawner(ent);
				if(home_spawn != null) {
					//log.info("[MM] SUFF/VOID/LAVA EVENT -> " + ent.toString());
					entities_to_remove.put(ent, home_spawn);
				} else {
					ent.remove();
				}
			}
			return;
		}
		
		if(e.getCause() == DamageCause.FIRE_TICK) {
			double cur_hp = mob_health.get(ent);
			dmg = (cur_hp * 0.03D);
		}
		
		if(e.getCause() == DamageCause.POISON) {
			double cur_hp = mob_health.get(ent);
			dmg = (cur_hp * 0.02D);
		}
		
		if(BossMechanics.enraged_boss.contains(ent)) {
			dmg = (int) ((double) dmg / 10.0D);
			// 10% normal damage while enraged.
		}
		
		/*if(ent.getType() != EntityType.SPIDER && ent.getType() != EntityType.CAVE_SPIDER && ent.getLocation().getBlock().getType() == Material.WEB){
			ent.getLocation().getBlock().setType(Material.AIR);
		}*/
		
		/*int mob_tier = getMobTier(ent);
		if(mob_tier == 4 || mob_tier == 5){
			final Entity f_ent = ent;
			final Entity f_attacker = attacker;
			this.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable(){
				public void run() {
					f_ent.setVelocity(new Vector());

					// Get velocity unit vector:
					org.bukkit.util.Vector unitVector = f_ent.getLocation().toVector().subtract(f_ent.getLocation().toVector()).normalize();
					// Set speed and push entity:

					if(f_attacker != null){
						unitVector = f_ent.getLocation().toVector().subtract(f_attacker.getLocation().toVector()).normalize();

						f_ent.setVelocity(unitVector.multiply(0.10D));
						if(f_ent != null){
							f_ent.playEffect(EntityEffect.HURT);
						}
					}

				}
			}, 1L);
		}*/
		
		if(!(max_mob_health.containsKey(ent))) {
			// Beacon buff?
			return;
		}
		
		int max_health = max_mob_health.get(ent);
		double mhealth = mob_health.get(ent);
		
		if(attacker instanceof Player && mhealth == max_health && dmg >= max_health) {
			AchievmentMechanics.addAchievment(((Player) attacker).getName(), "One K.O");
		}
		
		// Rag'doll them maybe?
		if(attacker instanceof Player && (mhealth - dmg <= 0) && dmg >= (max_health * 0.80)) {
			pushAwayEntity(((Player) attacker), ent, 5.0);
		} else if(attacker instanceof Player && (mhealth - dmg <= 0) && dmg >= (max_health * 0.60)) {
			pushAwayEntity(((Player) attacker), ent, 3.0);
		} else if(attacker instanceof Player && (mhealth - dmg <= 0) && dmg >= (max_health * 0.30)) {
			pushAwayEntity(((Player) attacker), ent, 2.0);
		}
		
		subtractMHealth(ent, (int) dmg);
		
		if(attacker instanceof Player && dmg >= 450) {
			AchievmentMechanics.addAchievment(((Player) attacker).getName(), "Serious Strength");
		}
		
		if(e instanceof EntityDamageByEntityEvent) {
			ItemMechanics.MeleeDebugListener((EntityDamageByEntityEvent) e);
			ItemMechanics.ArrowDebugListener((EntityDamageByEntityEvent) e);
		}
		
		if(ent.hasMetadata("boss_type")) {
			String boss_type = ent.getMetadata("boss_type").get(0).asString();
			
			if(boss_type.equalsIgnoreCase("fire_demon")) {
				// 1. Knock back the attacker and set them on fire if within melee range (4 blocks)
				// 2. Spawn silverfish every 5% HP
				// 3. At 50%, jump on ghast
				// 4. At 10%, rebuff to 50%, adds now = magma cubes
				
				if(attacker instanceof Player) {
					Player p_attacker = (Player) attacker;
					if(p_attacker.getLocation().distanceSquared(ent.getLocation()) <= 16) {
						// Ragdoll em.
						pushAwayPlayer(ent, p_attacker, 2.5F);
						p_attacker.setFireTicks(80);
						p_attacker.playSound(p_attacker.getLocation(), Sound.FIRE_IGNITE, 1F, 1F);
					}
					
					double max_hp = max_mob_health.get(ent);
					double cur_hp = mob_health.get(ent);
					
					double percent_hp = ((cur_hp / max_hp) * 100.0D);
					double minion_wave = 0;
					percent_hp = Math.round(percent_hp);
					if(BossMechanics.minion_count.containsKey(ent)) {
						minion_wave = BossMechanics.minion_count.get(ent);
					}
					
					if(percent_hp <= 50) {
						// TODO: Jump on a ghast, but we need to make sure it's first time...
						if(!BossMechanics.boss_event_log.get(ent).contains("ghast")) {
							BossMechanics.boss_event_log.put(ent, "ghast");
							
							Ghast g = (Ghast) ((LivingEntity) spawnTierMob(new Location(ent.getWorld(), -54, 158, 646).add(0, 5, 0), EntityType.GHAST, 4, -1, new Location(ent.getWorld(), -54, 158, 646).add(0, 5, 0), false, "", "Abysal Wraith", true, 4)); //(Ghast)ent.getWorld().spawnEntity(ent.getLocation().add(0, 5, 0), EntityType.GHAST);
							g.setPassenger(ent);
							
							double boss_armor = mob_armor.get(ent);
							boss_armor += 50;
							mob_armor.put(ent, (int) boss_armor); // +50% ARMOR
							
							for(Player pl : ent.getWorld().getPlayers()) {
								pl.sendMessage(ChatColor.GOLD + "" + ChatColor.UNDERLINE + "The Infernal Abyss: " + ChatColor.WHITE + "The inferno will devour you!");
								pl.sendMessage(ChatColor.GRAY + "The Infernal Abyss has armored up! " + ChatColor.UNDERLINE + "+50% ARMOR!");
								pl.playSound(pl.getLocation(), Sound.GHAST_MOAN, 2F, 0.35F);
								pl.playSound(pl.getLocation(), Sound.ENDERDRAGON_GROWL, 2F, 0.85F);
							}
						}
					}
					
					if(percent_hp <= 10 && ent.getVehicle() != null) {
						// Almost dead, on the ghast still, let's dismount and heal.
						LivingEntity ghast = (LivingEntity) ent.getVehicle();
						ghast.playEffect(EntityEffect.DEATH);
						ent.eject();
						
						BossMechanics.boss_event_log.put(ent, "ghast,enrage");
						BossMechanics.enraged_boss.add(ent);
						
						subtractMHealth(ent, -(int) (max_hp / 2.0D));
						/*List<Integer> dmg_range = MonsterMechanics.mob_damage.get(ent);
						dmg_range.set(0, (int)Math.round(dmg_range.get(0) * 1.5));
						dmg_range.set(1, (int)Math.round(dmg_range.get(1) * 1.5));
						mob_damage.put(ent, dmg_range);*/
						
						for(Player pl : ent.getWorld().getPlayers()) {
							pl.sendMessage(ChatColor.GOLD + "" + ChatColor.UNDERLINE + "The Infernal Abyss: " + ChatColor.WHITE + "You... cannot... kill me IN MY OWN DOMAIN, FOOLISH MORTALS!");
							pl.sendMessage(ChatColor.GRAY + "The Infernal Abyss has become enraged! " + ChatColor.UNDERLINE + "+50% DMG!");
							pl.playSound(pl.getLocation(), Sound.ENDERDRAGON_GROWL, 2F, 0.85F);
							pl.playSound(pl.getLocation(), Sound.GHAST_DEATH, 2F, 0.85F);
						}
						
						List<Entity> minion_map = new ArrayList<Entity>();
						
						if(BossMechanics.minion_map.containsKey(ent)) {
							minion_map = BossMechanics.minion_map.get(ent);
						}
						minion_map.add(spawnTierMob(ent.getLocation(), EntityType.MAGMA_CUBE, 3, -1, ent.getLocation(), false, "", "Spawn of Inferno", true, 3));
						minion_map.add(spawnTierMob(ent.getLocation(), EntityType.MAGMA_CUBE, 3, -1, ent.getLocation(), false, "", "Spawn of Inferno", true, 3));
						minion_map.add(spawnTierMob(ent.getLocation(), EntityType.MAGMA_CUBE, 3, -1, ent.getLocation(), false, "", "Spawn of Inferno", true, 3));
						minion_map.add(spawnTierMob(ent.getLocation(), EntityType.MAGMA_CUBE, 3, -1, ent.getLocation(), false, "", "Spawn of Inferno", true, 3));
						minion_map.add(spawnTierMob(ent.getLocation(), EntityType.MAGMA_CUBE, 3, -1, ent.getLocation(), false, "", "Spawn of Inferno", true, 3));
					}
					
					if(minion_wave < (10 - (percent_hp / 10.0D)) && ent.getVehicle() == null) { // Don't spawn adds on the ghast pls.
						// Summon minions!
						Location loc = ent.getLocation();
						int minion_type = new Random().nextInt(2); // 0, 1
						List<Entity> minion_map = new ArrayList<Entity>();
						boolean enraged = false;
						
						if(BossMechanics.boss_event_log.get(ent).contains("enrage")) {
							enraged = true;
						}
						
						if(BossMechanics.minion_map.containsKey(ent)) {
							minion_map = BossMechanics.minion_map.get(ent);
						}
						
						if(minion_type == 0) {
							if(!enraged) {
								minion_map.add(spawnTierMob(loc, EntityType.SILVERFISH, 3, -1, loc, false, "", "Abyssal Demon", true, 3));
								minion_map.add(spawnTierMob(loc, EntityType.SILVERFISH, 3, -1, loc, false, "", "Abyssal Demon", true, 3));
								minion_map.add(spawnTierMob(loc, EntityType.SILVERFISH, 3, -1, loc, false, "", "Abyssal Demon", true, 3));
								minion_map.add(spawnTierMob(loc, EntityType.SILVERFISH, 3, -1, loc, false, "", "Abyssal Demon", true, 3));
							} else if(enraged) {
								minion_map.add(spawnTierMob(loc, EntityType.MAGMA_CUBE, 3, -1, loc, false, "", "Spawn of Inferno", true, 3));
								minion_map.add(spawnTierMob(loc, EntityType.MAGMA_CUBE, 3, -1, loc, false, "", "Spawn of Inferno", true, 3));
								minion_map.add(spawnTierMob(loc, EntityType.MAGMA_CUBE, 3, -1, loc, false, "", "Spawn of Inferno", true, 3));
								minion_map.add(spawnTierMob(loc, EntityType.MAGMA_CUBE, 3, -1, loc, false, "", "Spawn of Inferno", true, 3));
							}
						}
						
						if(minion_type == 1) {
							if(!enraged) {
								minion_map.add(spawnTierMob(loc, EntityType.SILVERFISH, 4, -1, loc, false, "", "Greater Abyssal Demon", true, 3));
								minion_map.add(spawnTierMob(loc, EntityType.SILVERFISH, 4, -1, loc, false, "", "Greater Abyssal Demon", true, 3));
							} else if(enraged) {
								minion_map.add(spawnTierMob(loc, EntityType.MAGMA_CUBE, 4, -1, loc, false, "", "Demonic Spawn of Inferno", true, 3));
								minion_map.add(spawnTierMob(loc, EntityType.MAGMA_CUBE, 4, -1, loc, false, "", "Demonic Spawn of Inferno", true, 3));
							}
						}
						
						for(Entity add : minion_map) {
							add.setFireTicks(Integer.MAX_VALUE);
						}
						
						try {
							ParticleEffect.sendToLocation(ParticleEffect.LARGE_SMOKE, ent.getLocation().add(0, 0.5, 0), new Random().nextFloat(), new Random().nextFloat(), new Random().nextFloat(), 1F, 100);
						} catch(Exception err) {
							err.printStackTrace();
						}
						
						//loc.getWorld().spawnParticle(loc, Particle.SMOKE, 1F, 100);
						
						for(Player pl : ent.getWorld().getPlayers()) {
							if(minion_type == 0) {
								pl.playSound(pl.getLocation(), Sound.GHAST_SCREAM2, 1F, 0.35F);
							}
							if(minion_type == 1) {
								pl.sendMessage(ChatColor.GOLD + "" + ChatColor.UNDERLINE + "The Infernal Abyss: " + ChatColor.WHITE + "Beyold, the powers of the inferno.");
								pl.playSound(pl.getLocation(), Sound.GHAST_SCREAM, 1F, 0.25F);
							}
						}
						
						minion_wave += 1;
						BossMechanics.minion_count.put(ent, minion_wave);
						BossMechanics.minion_map.put(ent, minion_map);
					}
				}
			}
			
			if(boss_type.equalsIgnoreCase("bandit_leader") && max_mob_health.containsKey(ent)) {
				double max_hp = max_mob_health.get(ent);
				double cur_hp = mob_health.get(ent);
				
				double percent_hp = ((cur_hp / max_hp) * 100.0D);
				double minion_wave = 0;
				percent_hp = Math.round(percent_hp);
				if(BossMechanics.minion_count.containsKey(ent)) {
					minion_wave = BossMechanics.minion_count.get(ent);
				}
				
				// TODO: Spawn adds every 10%, but every hit there's a chance for him to heal/buff them. 
				// When he enrages, enrage all the bandits left alive?
				
				if(minion_wave < (10 - (percent_hp / 10.0D))) {
					// Summon minions!
					Location loc = ent.getLocation();
					int minion_type = new Random().nextInt(3); // Always 0
					List<Entity> minion_map = new ArrayList<Entity>();
					
					if(BossMechanics.minion_map.containsKey(ent)) {
						minion_map = BossMechanics.minion_map.get(ent);
					}
					
					if(minion_type == 0) {
						minion_map.add(spawnTierMob(loc, EntityType.SKELETON, 1, -1, loc, true, "bandit", "Mayel's Elite Pirate", true, 3));
						minion_map.add(spawnTierMob(loc, EntityType.SKELETON, 1, -1, loc, false, "bandit", "Mayel's Pirate", true, 3));
						minion_map.add(spawnTierMob(loc, EntityType.SKELETON, 1, -1, loc, false, "bandit", "Mayel's Pirate", true, 3));
					}
					
					if(minion_type == 1) {
						minion_map.add(spawnTierMob(loc, EntityType.SKELETON, 1, -1, loc, false, "bandit", "Mayel's Pirate", true, 3));
						minion_map.add(spawnTierMob(loc, EntityType.SKELETON, 1, -1, loc, false, "bandit", "Mayel's Pirate", true, 3));
						minion_map.add(spawnTierMob(loc, EntityType.SKELETON, 1, -1, loc, false, "bandit", "Mayel's Pirate", true, 3));
						minion_map.add(spawnTierMob(loc, EntityType.SKELETON, 1, -1, loc, false, "bandit", "Mayel's Pirate", true, 3));
						minion_map.add(spawnTierMob(loc, EntityType.SKELETON, 1, -1, loc, false, "bandit", "Mayel's Pirate", true, 3));
					}
					
					if(minion_type == 2) {
						minion_map.add(spawnTierMob(loc, EntityType.SKELETON, 2, -1, loc, false, "bandit", "Mayel's Pirate Captain", true, 3));
						minion_map.add(spawnTierMob(loc, EntityType.SKELETON, 2, -1, loc, false, "bandit", "Mayel's Pirate Captain", true, 3));
						minion_map.add(spawnTierMob(loc, EntityType.SKELETON, 1, -1, loc, false, "bandit", "Mayel's Pirate", true, 3));
					}
					
					try {
						ParticleEffect.sendToLocation(ParticleEffect.SPELL, loc, new Random().nextFloat(), new Random().nextFloat(), new Random().nextFloat(), 1F, 100);
					} catch(Exception err) {
						err.printStackTrace();
					}
					
					//loc.getWorld().spawnParticle(loc, Particle.SPELL, 1F, 100);
					
					for(Player pl : ent.getWorld().getPlayers()) {
						pl.sendMessage(ChatColor.GOLD + "" + ChatColor.UNDERLINE + "Mayel The Cruel: " + ChatColor.WHITE + "Kill them my children, kill them all!");
						pl.sendMessage(ChatColor.GRAY + "Mayel's minions will grow stronger the longer they are alive -- Kill them!");
						pl.playSound(pl.getLocation(), Sound.GHAST_SCREAM2, 1F, 0.5F);
					}
					
					minion_wave += 1;
					BossMechanics.minion_count.put(ent, minion_wave);
					BossMechanics.minion_map.put(ent, minion_map);
				}
				
				int random_act = new Random().nextInt(100);
				if(random_act <= 10) {
					// Heal all bandits back to 100%
					if(BossMechanics.minion_map.containsKey(ent)) {
						List<Entity> minion_map = BossMechanics.minion_map.get(ent);
						for(Entity add : minion_map) {
							if(add != null && !add.isDead()) {
								double add_max_health = MonsterMechanics.max_mob_health.get(add);
								int deficet = (int) (add_max_health - MonsterMechanics.mob_health.get(add));
								MonsterMechanics.subtractMHealth(add, -deficet);
								try {
									ParticleEffect.sendToLocation(ParticleEffect.HAPPY_VILLAGER, ent.getLocation().add(0, 2, 0), new Random().nextFloat(), new Random().nextFloat(), new Random().nextFloat(), 0.5F, 5);
								} catch(Exception err) {
									err.printStackTrace();
								}
								//add.getLocation().getWorld().spawnParticle(add.getLocation().add(0, 2, 0), Particle.HAPPY_VILLAGER, 0.5F, 5);
							}
						}
						for(Player pl : ent.getWorld().getPlayers()) {
							pl.sendMessage(ChatColor.GOLD + "" + ChatColor.UNDERLINE + "Mayel The Cruel: " + ChatColor.WHITE + "Do not slow your assault, we must kill them all!");
							pl.sendMessage(ChatColor.GRAY + "Mayel has healed his bandits!");
							pl.playSound(pl.getLocation(), Sound.BURP, 1F, 0.5F); // TODO Sound.BREATH is no longer there
						}
					}
				}
				if(random_act >= 90) {
					// Buff all bandits DPS+ARM by 1%
					List<Entity> minion_map = BossMechanics.minion_map.get(ent);
					for(Entity add : minion_map) {
						if(add != null && !add.isDead()) {
							List<Integer> add_dmg = MonsterMechanics.mob_damage.get(add);
							int add_arm = MonsterMechanics.mob_armor.get(add);
							add_arm += 2;
							MonsterMechanics.mob_armor.put(add, add_arm);
							double min_dmg = add_dmg.get(0);
							double max_dmg = add_dmg.get(1);
							min_dmg = (min_dmg * 1.1) + 1;
							max_dmg = (max_dmg * 1.1) + 1;
							add_dmg.set(0, (int) Math.round(min_dmg));
							add_dmg.set(1, (int) Math.round(max_dmg));
							MonsterMechanics.mob_damage.put(add, add_dmg);
							
							try {
								ParticleEffect.sendToLocation(ParticleEffect.CRIT, ent.getLocation().add(0, 0.5, 0), new Random().nextFloat(), new Random().nextFloat(), new Random().nextFloat(), 1F, 10);
							} catch(Exception err) {
								err.printStackTrace();
							}
							//add.getLocation().getWorld().spawnParticle(add.getLocation().add(0, 2, 0), Particle.CRIT, 1F, 10);
						}
					}
					for(Player pl : ent.getWorld().getPlayers()) {
						pl.sendMessage(ChatColor.GOLD + "" + ChatColor.UNDERLINE + "Mayel The Cruel: " + ChatColor.WHITE + "I lend you my strength my brothers, crush these insolent fools!!");
						pl.sendMessage(ChatColor.GRAY + "Mayel has buffed his bandits, +10% DPS/ +2% ARM!");
						pl.playSound(pl.getLocation(), Sound.BURP, 1F, 0.5F); // TODO Sound.BREATH is no longer there
					}
				}
			}
			
			if(boss_type.equalsIgnoreCase("unholy_priest")) {
				if(!max_mob_health.containsKey(ent)) {
					e.setCancelled(true);
					return;
				}
				double max_hp = max_mob_health.get(ent);
				double cur_hp = mob_health.get(ent);
				
				double percent_hp = ((cur_hp / max_hp) * 100.0D);
				double minion_wave = 0;
				percent_hp = Math.round(percent_hp);
				if(BossMechanics.minion_count.containsKey(ent)) {
					minion_wave = BossMechanics.minion_count.get(ent);
				}
				
				if(percent_hp <= 30) {
					int heal_count = 0;
					if(BossMechanics.boss_heals.containsKey(ent)) {
						heal_count = BossMechanics.boss_heals.get(ent);
					}
					heal_count += 1;
					
					if(heal_count <= 3) {
						// Heal the boss.
						subtractMHealth(ent, -(int) (max_hp / 3.0D)); // Heal 30% of HP.
						if(heal_count == 1) {
							for(Player pl : ent.getWorld().getPlayers()) {
								pl.sendMessage(ChatColor.GOLD + "" + ChatColor.UNDERLINE + "Burick The Fanatic: " + ChatColor.WHITE + "Let the powers of Maltai channel into me and give me strength!");
								pl.playSound(pl.getLocation(), Sound.ENDERMAN_DEATH, 1F, 0.5F);
							}
						}
						
						if(heal_count == 2) {
							for(Player pl : ent.getWorld().getPlayers()) {
								pl.sendMessage(ChatColor.GOLD + "" + ChatColor.UNDERLINE + "Burick The Fanatic: " + ChatColor.WHITE + "You cannot kill that which is already condemned, foolish adventurer!");
								pl.playSound(pl.getLocation(), Sound.ENDERMAN_DEATH, 1F, 0.5F);
							}
						}
						
						if(heal_count == 3) {
							for(Player pl : ent.getWorld().getPlayers()) {
								pl.sendMessage(ChatColor.GOLD + "" + ChatColor.UNDERLINE + "Burick The Fanatic: " + ChatColor.WHITE + "As long as you breathe, I still have purpose, and you cannot kill a creature with purpose!");
								pl.playSound(pl.getLocation(), Sound.ENDERMAN_DEATH, 1F, 0.5F);
							}
						}
						
						BossMechanics.boss_heals.put(ent, heal_count);
					}
					/*else if(heal_count <= 4){
						for(Player pl : ent.getWorld().getPlayers()){
							pl.sendMessage(ChatColor.GOLD + "" + ChatColor.UNDERLINE + "Burick The Fanatic: " + ChatColor.WHITE + "Enough! Now you die!");
							pl.playSound(pl.getLocation(), Sound.ENDERMAN_DEATH, 1F, 0.5F);
						}
						BossMechanics.boss_heals.put(ent, heal_count);
					}*/
					// If he's already healed 3 times, stop.
				}
				
				if(percent_hp <= 10 && !(BossMechanics.enraged_boss.contains(ent))) {
					for(Player pl : ent.getWorld().getPlayers()) {
						pl.sendMessage(ChatColor.GOLD + "" + ChatColor.UNDERLINE + "Burick The Fanatic: " + ChatColor.WHITE + "Pain. Sufferring. Agony. These are the emotions you will be feeling for the rest of eternity!");
						pl.sendMessage(ChatColor.GOLD + "" + ChatColor.UNDERLINE + "Burick The Fanatic " + ChatColor.GOLD + "has become ENRAGED" + ChatColor.GOLD + " 2.5X DMG, +80% ARMOR, 2x SPEED!");
						pl.playSound(pl.getLocation(), Sound.ENDERMAN_DEATH, 0.8F, 0.5F);
						pl.playSound(pl.getLocation(), Sound.ENDERMAN_DEATH, 1.2F, 0.2F);
						pl.playSound(pl.getLocation(), Sound.ENDERMAN_DEATH, 0.8F, 1.2F);
					}
					LivingEntity le = (LivingEntity) ent;
					le.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));
					BossMechanics.enraged_boss.add(ent);
				}
				
				if(minion_wave < (10 - (percent_hp / 10.0D))) {
					// Summon minions!
					// Store all the spawned minions in a map to check when they're dead.\
					// Transform him into an invinsible blaze?
					Location loc = ent.getLocation();
					int minion_type = new Random().nextInt(5);
					List<Entity> minion_map = new ArrayList<Entity>();
					
					if(minion_type == 0) {
						minion_map.add(spawnTierMob(loc, EntityType.ZOMBIE, 3, -1, loc, false, "", "", true, 3));
						minion_map.add(spawnTierMob(loc, EntityType.ZOMBIE, 2, -1, loc, false, "", "", true, 3));
						minion_map.add(spawnTierMob(loc, EntityType.ZOMBIE, 2, -1, loc, false, "", "", true, 3));
					}
					if(minion_type == 1) {
						minion_map.add(spawnTierMob(loc, EntityType.ZOMBIE, 2, -1, loc, false, "", "", true, 3));
						minion_map.add(spawnTierMob(loc, EntityType.SKELETON, 2, -1, loc, false, "", "", true, 3));
						minion_map.add(spawnTierMob(loc, EntityType.SKELETON, 2, -1, loc, false, "", "", true, 3));
					}
					if(minion_type == 2) {
						minion_map.add(spawnTierMob(loc, EntityType.SKELETON, 2, -1, loc, false, "", "", true, 3));
						minion_map.add(spawnTierMob(loc, EntityType.SKELETON, 2, -1, loc, false, "", "", true, 3));
						minion_map.add(spawnTierMob(loc, EntityType.SKELETON, 1, -1, loc, false, "", "", true, 3));
						minion_map.add(spawnTierMob(loc, EntityType.SKELETON, 1, -1, loc, false, "", "", true, 3));
						minion_map.add(spawnTierMob(loc, EntityType.SKELETON, 1, -1, loc, false, "", "", true, 3));
						minion_map.add(spawnTierMob(loc, EntityType.SKELETON, 3, -1, loc, false, "", "", true, 3));
					}
					if(minion_type == 3) {
						minion_map.add(spawnTierMob(loc, EntityType.ZOMBIE, 2, -1, loc, true, "", "", true, 3));
						minion_map.add(spawnTierMob(loc, EntityType.ZOMBIE, 2, -1, loc, false, "", "", true, 3));
						minion_map.add(spawnTierMob(loc, EntityType.SKELETON, 3, -1, loc, false, "", "", true, 3));
						minion_map.add(spawnTierMob(loc, EntityType.SKELETON, 2, -1, loc, false, "", "", true, 3));
					}
					if(minion_type == 4) {
						minion_map.add(spawnTierMob(loc, EntityType.ZOMBIE, 2, -1, loc, false, "", "", true, 3));
						minion_map.add(spawnTierMob(loc, EntityType.SKELETON, 2, -1, loc, true, "", "", true, 3));
						minion_map.add(spawnTierMob(loc, EntityType.SKELETON, 2, -1, loc, false, "", "", true, 3));
					}
					
					try {
						ParticleEffect.sendToLocation(ParticleEffect.SPELL, loc, new Random().nextFloat(), new Random().nextFloat(), new Random().nextFloat(), 1F, 100);
					} catch(Exception err) {
						err.printStackTrace();
					}
					//loc.getWorld().spawnParticle(loc, Particle.SPELL, 1F, 100);
					
					for(Player pl : ent.getWorld().getPlayers()) {
						pl.sendMessage(ChatColor.GOLD + "" + ChatColor.UNDERLINE + "Burick The Fanatic: " + ChatColor.WHITE + "To me, my undead brethren! Rip these Andalucians to pieces!");
						pl.sendMessage(ChatColor.GRAY + "Burick uses the energy of his minions to create a forcefield around himself -- kill the minions!");
						pl.playSound(pl.getLocation(), Sound.ENDERMAN_SCREAM, 1F, 0.5F);
					}
					
					LivingEntity le = (LivingEntity) ent;
					le.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 15));
					
					minion_wave += 2;
					BossMechanics.minion_count.put(ent, minion_wave);
					BossMechanics.minion_map.put(ent, minion_map);
					BossMechanics.boss_saved_location.put(ent, ent.getLocation().add(0, 2, 0));
				}
			}
			
			if(boss_type.equalsIgnoreCase("onewolfedungeon")){
			    
			}
		}
		
		e.setDamage(0);
		
	}
	
	@EventHandler
	public void onEntityShootBow(EntityShootBowEvent event) {
		Entity ent = event.getEntity();
		boolean fire = false;
		if(ent.hasMetadata("etype")) {
			fire = ent.getMetadata("etype").get(0).asString().equalsIgnoreCase("fire");
		}
		
		if(power_strike.containsKey(ent) && power_strike.get(ent) >= 5) {
			// Double arrows!
			ent.getWorld().playSound(ent.getLocation(), Sound.ARROW_HIT, 1F, 1F);
			ent.getWorld().playSound(ent.getLocation(), Sound.ARROW_HIT, 1F, 1F);
			
			Random random = new Random();
			double b = random.nextInt(7) - 3;
			double c = b / 10;
			double d = random.nextInt(7) - 3;
			double e = d / 10;
			double f = random.nextInt(7) - 3;
			double g = f / 10;
			Vector vec = new Vector(c, e, g);
			Arrow arrow = ent.getWorld().spawn(ent.getLocation(), Arrow.class);
			
			arrow.setVelocity(ent.getVelocity().add(vec));
			arrow.setShooter((LivingEntity) ent);
			if(fire) {
				arrow.setFireTicks(80);
			}
			
			b = random.nextInt(7) - 3;
			c = b / 10;
			d = random.nextInt(7) - 3;
			e = d / 10;
			f = random.nextInt(7) - 3;
			g = f / 10;
			vec = new Vector(c, e, g);
			arrow = ent.getWorld().spawn(ent.getLocation(), Arrow.class);
			if(fire) {
				arrow.setFireTicks(80);
			}
			
			arrow.setVelocity(ent.getVelocity().add(vec));
			arrow.setShooter((LivingEntity) ent);
			
			power_strike.remove(ent);
			special_attack.remove(ent);
		}
		
		if(fire) {
			event.getProjectile().setFireTicks(80); // Fire arrow!
			Arrow a = (Arrow) event.getProjectile();
			a.setFireTicks(80);
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void MonsterKnocback(EntityDamageByEntityEvent e) {
		if(e.getCause() != DamageCause.ENTITY_ATTACK && e.getCause() != DamageCause.PROJECTILE) { return; }
		
		if(e.getDamage() <= 0) { return; }
		
		if(!(e.getEntity() instanceof Player)) { return; }
		
		if(e.getDamager() instanceof Player) { return; }
		
		Player p = (Player) e.getEntity();
		Entity ent = null;
		
		if(e.getCause() == DamageCause.PROJECTILE) {
			if(e.getDamager() instanceof Arrow) {
				Arrow a = ((Arrow) e.getDamager());
				ent = (Entity) a.getShooter();
			}
		}
		
		if(e.getCause() == DamageCause.ENTITY_ATTACK) {
			ent = e.getDamager();
		}
		
		if(ent == null) { return; }
		
		if(ent.isInsideVehicle()) { // Riding a spider?
			Entity r_ent = ent.getVehicle();
			ent = r_ent;
		}
		
		boolean is_elite = false;
		int mob_tier = getMobTier(ent);
		//List<ItemStack> ent_gear = mob_loot.get(ent);
		ItemStack weapon = CraftItemStack.asBukkitCopy(((CraftEntity) ent).getHandle().getEquipment()[0]);
		if(weapon.getEnchantments().containsKey(Enchantment.KNOCKBACK)) {
			//log.info("ELITE!");
			is_elite = true;
		}
		
		float push_value = 0;
		
		if(mob_tier == 2) {
			push_value = 1.25F;
		}
		if(mob_tier == 3) {
			push_value = 1.30F;
		}
		if(mob_tier == 4) {
			push_value = 1.3F;
		}
		if(mob_tier == 5) {
			push_value = 1.35F;
		}
		
		double distance = ent.getLocation().distanceSquared(p.getLocation());
		
		if(weapon.getType() == Material.BOW && distance <= 4.0D) {
			push_value = 1.20F;
		}
		if(weapon.getType() == Material.BOW && distance > 4.0D) {
			if(mob_tier >= 2) {
				push_value = 1.15F;
			} else {
				push_value = 0;
			}
		}
		
		if(is_elite == true) {
			push_value += 0.35F;
		}
		
		Location m_loc = ent.getLocation();
		Location p_loc = p.getLocation();
		
		if((p_loc.getY() - 0.50) <= m_loc.getY()) {
			int do_i_knock = new Random().nextInt(100);
			if(is_elite == false) {
				if(do_i_knock <= 30) {
					pushAwayPlayer(ent, p, push_value);
				}
			}
		}
		
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onMonsterDamagePlayerEvent(EntityDamageByEntityEvent e) {
		if(e.getCause() != DamageCause.ENTITY_ATTACK && e.getCause() != DamageCause.PROJECTILE) { return; }
		
		if(!(e.getEntity() instanceof Player)) { return; }
		
		Player p = (Player) e.getEntity();
		Entity ent = null;
		boolean bow = false;
		/*if(p.getNoDamageTicks() > 0){
			return;
		}*/
		
		if(e.getCause() == DamageCause.PROJECTILE) {
			if(e.getDamager() instanceof Arrow) {
				Arrow a = ((Arrow) e.getDamager());
				ent = (Entity) a.getShooter();
				bow = true;
			}
		}
		
		if(e.getCause() == DamageCause.ENTITY_ATTACK) {
			ent = e.getDamager();
		}
		
		if(ent != null && ent.getPassenger() != null) {
			Entity temp_ent = ent.getPassenger();
			ent = temp_ent;
		}
		
		if(ent == null) { return; }
		
		if(!(mob_damage.containsKey(ent))) {
			// No damage data stored for monster.
			return;
		}
		
		if(DuelMechanics.isDamageDisabled(ent.getLocation())) {
			if(!(entities_to_remove.containsKey(e))) {
			    if(getMobsHomeSpawner(ent) != null){
				entities_to_remove.put(ent, getMobsHomeSpawner(ent));
			    }
			}
			e.setCancelled(true);
			e.setDamage(0);
			return;
		}
		
		if(ent.getPassenger() != null) {
			Entity r_ent = ent.getPassenger();
			ent = r_ent;
		}
		
		if(!mob_last_hit.containsKey(ent)) {
			mob_last_hit.put(ent, System.currentTimeMillis());
	    } else if(mob_last_hit.containsKey(ent)) {
	        if((System.currentTimeMillis() - mob_last_hit.get(ent)) < Delay.MELEE.delay) {
	            e.setCancelled(true);
	            e.setDamage(0);
	            return; // A half-second hasn't passed since the mob last hit someone. 1/5th of a second
	        }
	        mob_last_hit.put(ent, System.currentTimeMillis());
	    }

		// if(e.getDamage() <= 0){return;} // ISSUE?
		
		boolean is_elite = false;
		//int mob_tier = getMobTier(ent);
		//List<ItemStack> ent_gear = mob_loot.get(ent);
		ItemStack weapon = CraftItemStack.asBukkitCopy(((CraftEntity) ent).getHandle().getEquipment()[0]);
		if(weapon.getEnchantments().containsKey(Enchantment.KNOCKBACK)) {
			//log.info("ELITE!");
			is_elite = true;
		}
		
		List<Integer> dmg_range = mob_damage.get(ent);
		int min_dmg = dmg_range.get(0);
		int max_dmg = dmg_range.get(1);
		
		if(max_dmg - min_dmg <= 0) {
			max_dmg = min_dmg + 1;
		}
		
		int dmg = new Random().nextInt(max_dmg - min_dmg) + min_dmg;
		
		player_slow.put(p.getName(), System.currentTimeMillis());
		p.setWalkSpeed(0.165F);
		
		if(PartyMechanics.party_map.containsKey(p.getName())) { // They're in a party.
			int mem_nearby = PartyMechanics.getPlayersInArea(PartyMechanics.party_map.get(p.getName()), ent.getLocation(), 75);
			if(mem_nearby >= 8) {
				dmg -= (dmg * 0.05D);
			}
		}
		
		if(whirlwind.containsKey(ent) && whirlwind.get(ent) >= 5) {
			dmg = dmg * 4;
		}
		
		if(power_strike.containsKey(ent) && power_strike.get(ent) >= 5) {
			// Extra DMG!
			if(!(BossMechanics.boss_map.containsKey(ent))) {
				dmg = dmg * 3;
				ent.getWorld().playSound(ent.getLocation(), Sound.EXPLODE, 1F, 0.3F); // Ou!
				
				try {
					ParticleEffect.sendToLocation(ParticleEffect.EXPLODE, p.getLocation().add(0, 1, 0), new Random().nextFloat(), new Random().nextFloat(), new Random().nextFloat(), 0.4F, 40);
				} catch(Exception err) {
					err.printStackTrace();
				}
				//p.getWorld().spawnParticle(p.getLocation().add(0, 1, 0), Particle.EXPLODE, 0.4F, 40);
				
				// Extra knockback!
				pushAwayPlayer(ent, p, 2F);
			} else if(BossMechanics.boss_map.containsKey(ent)) {
				String boss_name = BossMechanics.boss_map.get(ent);
				if(boss_name.equalsIgnoreCase("unholy_priest")) {
					dmg = dmg * 5;
					ent.getWorld().playSound(ent.getLocation(), Sound.ENDERMAN_SCREAM, 1F, 0.3F); // Ou!
					
					try {
						ParticleEffect.sendToLocation(ParticleEffect.LARGE_EXPLODE, p.getLocation().add(0, 1, 0), new Random().nextFloat(), new Random().nextFloat(), new Random().nextFloat(), 0.2F, 50);
					} catch(Exception err) {
						err.printStackTrace();
					}
					//p.getWorld().spawnParticle(p.getLocation().add(0, 1, 0), Particle.LARGE_EXPLODE, 0.2F, 50);
					
					// Extra knockback!
					pushAwayPlayer(ent, p, 4F);
				}
			}
			power_strike.remove(ent);
			special_attack.remove(ent);
			
			LivingEntity le = (LivingEntity) ent;
			le.setCustomName(generateOverheadBar(ent, mob_health.get(ent), max_mob_health.get(ent), getMobTier(ent), is_elite));
		}
		
		if(BossMechanics.enraged_boss.contains(ent)) {
			dmg = (int) ((double) dmg * 2.5D);
			// 2.5x damage while enraged.
		}
		if(bow){
		    /*Only do 60% damage - NERF*/
		    dmg = (int) (dmg * .6D);
		}
		e.setDamage(dmg);
		
	}
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerTargeted(EntityTargetLivingEntityEvent e){
	    if(e.isCancelled())return;
	    if(e.getTarget() instanceof Player){
	        mob_target.put(e.getEntity(), ((Player)e.getTarget()).getName());
	    }
	}
	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerDamageMonsterEvent(EntityDamageByEntityEvent e) {
		if(!(e.getDamager() instanceof Player) && !(e.getDamager() instanceof Projectile)) { return; }
		
		Entity ent = e.getEntity();
		
		if(ent instanceof Player) { return; }
		
		if(e.isCancelled() && e.getDamage() == 0) { return; }
		
		if(ent.getPassenger() != null) { // There is a passanger on the spider.
			Entity r_ent = ent.getPassenger();
			ent = r_ent;
		}
		
		if(ent instanceof Player) { return;
		// Need this check twice incase ent changed with the 'vehicle' chec
		}
		
		if(e.getDamager() instanceof WitherSkull) {
			e.setCancelled(true);
			e.setDamage(0);
			return;
		}
		
		if(e.getDamager() instanceof Player) {
			if(getMobType(ent, true).equalsIgnoreCase("goblin")) {
				Player pl = (Player) e.getDamager();
				pl.getWorld().playSound(ent.getLocation(), Sound.ZOMBIE_PIG_ANGRY, 1, 0.75F);
				//pl.playSound(pl.getLocation(), Sound.ZOMBIE_PIG_ANGRY, 4, 0.75F);
			}
			if(getMobType(ent, true).equalsIgnoreCase("forest troll")) {
				Player pl = (Player) e.getDamager();
				pl.getWorld().playSound(ent.getLocation(), Sound.ZOMBIE_PIG_IDLE, 1, 0.85F);
				//pl.playSound(pl.getLocation(), Sound.ZOMBIE_PIG_IDLE, 4, 0.85F);
				
			}
		}
		
		if(!(mob_health.containsKey(ent))) {
			if(ent instanceof LivingEntity) {
				LivingEntity le = (LivingEntity) ent;
				if(le.getEquipment().getArmorContents().length > 0) { // They have armor on, so something must have gone wrong.
					// TODO: Calculate HP instead.
					le.damage(le.getHealth()); // Just kill them.
					return;
				}
			}
			return;
		}
		
		if(mob_armor.containsKey(ent)) {
			double armor_val = mob_armor.get(ent) / 100.0D;
			double dmg = e.getDamage();
			
			double to_negate = armor_val * dmg;
			dmg = dmg - to_negate;
			e.setDamage((int) Math.round(dmg));
		}
		
		Player p = null;
		
		if(e.getDamager() instanceof Projectile) {
			if(e.getDamager() instanceof Arrow) {
				Arrow a = ((Arrow) e.getDamager());
				a.setBounce(false);
				if(a.getShooter() instanceof Player) {
					p = (Player) a.getShooter();
				}
			}
		}
		
		if(e.getCause() == DamageCause.ENTITY_ATTACK) {
			p = (Player) e.getDamager();
			player_slow.put(p.getName(), System.currentTimeMillis());
			p.setWalkSpeed(0.165F);
		}
		
		if(p != null) {
			mob_target.put(ent, p.getName());
			
			if(p.getItemInHand() != null) {
				int mob_tier = getMobTier(ent);
				int wep_tier = ItemMechanics.getItemTier(p.getItemInHand());
				
				if(wep_tier > mob_tier && mob_tier != -1) {
					int dif = wep_tier - mob_tier;
					/*if(dif == 1){
					RepairMechanics.subtractCustomDurability(p, p.getItemInHand(), 4, "wep");
					}*/
					if(dif == 2) {
						RepairMechanics.subtractCustomDurability(p, p.getItemInHand(), 4, "wep"); // 5
					}
					if(dif == 3) {
						RepairMechanics.subtractCustomDurability(p, p.getItemInHand(), 6, "wep"); // 10
					}
					if(dif == 4) {
						RepairMechanics.subtractCustomDurability(p, p.getItemInHand(), 8, "wep"); // 10
					}
				}
			}
			
			if(PartyMechanics.party_map.containsKey(p.getName())) { // They're in a party.
				int mem_nearby = PartyMechanics.getPlayersInArea(PartyMechanics.party_map.get(p.getName()), ent.getLocation(), 75);
				if(mem_nearby >= 8) {
					double dmg = e.getDamage();
					dmg += (dmg * 0.05D);
					e.setDamage((int) dmg);
				}
			}
		}
		
		//LivingEntity le = (LivingEntity)ent;
		ItemStack weapon = CraftItemStack.asBukkitCopy(((CraftEntity) ent).getHandle().getEquipment()[0]);
		boolean is_elite = false;
		if(weapon.getEnchantments().containsKey(Enchantment.KNOCKBACK)) {
			is_elite = true;
		}
		
		if(ent != null && is_elite && !(special_attack.containsKey(ent)) && (mob_health.get(ent) - e.getDamage()) > 0) {
			int do_i_whirlwind = new Random().nextInt(100);
			int mob_tier = getMobTier(ent);
			if(mob_tier == 1) {
				if(do_i_whirlwind <= 5) {
					whirlwind.put(ent, 1);
					special_attack.put(ent, 1);
					
					ent.getWorld().playSound(ent.getLocation(), Sound.CREEPER_HISS, 1F, 4.0F);
				}
			}
			if(mob_tier == 2) {
				if(do_i_whirlwind <= 7) {
					whirlwind.put(ent, 1);
					special_attack.put(ent, 1);
					
					ent.getWorld().playSound(ent.getLocation(), Sound.CREEPER_HISS, 1F, 4.0F);
				}
			}
			if(mob_tier == 3) {
				if(do_i_whirlwind <= 10) {
					whirlwind.put(ent, 1);
					special_attack.put(ent, 1);
					
					ent.getWorld().playSound(ent.getLocation(), Sound.CREEPER_HISS, 1F, 4.0F);
				}
			}
			if(mob_tier == 4) {
				if(do_i_whirlwind <= 13) {
					whirlwind.put(ent, 1);
					special_attack.put(ent, 1);
					
					ent.getWorld().playSound(ent.getLocation(), Sound.CREEPER_HISS, 1F, 4.0F);
				}
			}
			if(mob_tier == 5) {
				if(do_i_whirlwind <= 20) {
					whirlwind.put(ent, 1);
					special_attack.put(ent, 1);
					
					ent.getWorld().playSound(ent.getLocation(), Sound.CREEPER_HISS, 1F, 4.0F);
				}
			}
			if(mob_tier == -1 && BossMechanics.boss_map.containsKey(ent) && BossMechanics.boss_map.get(ent).equalsIgnoreCase("unholy priest")) {
				if(do_i_whirlwind <= 10) {
					whirlwind.put(ent, 1);
					special_attack.put(ent, 1);
					
					ent.getWorld().playSound(ent.getLocation(), Sound.CREEPER_HISS, 1F, 4.0F);
				}
			}
			
		}
		
		int mob_tier = getMobTier(ent);
		
		if(ent != null && !(special_attack.containsKey(ent)) && (mob_health.get(ent) - e.getDamage()) > 0) {
			int do_i_powerstrike = new Random().nextInt(100);
			if(mob_tier == 1) {
				if(do_i_powerstrike <= 5) {
					power_strike.put(ent, 1);
					special_attack.put(ent, 1);
					
					ent.getWorld().playSound(ent.getLocation(), Sound.PISTON_EXTEND, 1F, 2.0F);
				}
			}
			if(mob_tier == 2) {
				if(do_i_powerstrike <= 7) {
					power_strike.put(ent, 1);
					special_attack.put(ent, 1);
					
					ent.getWorld().playSound(ent.getLocation(), Sound.PISTON_EXTEND, 1F, 2.0F);
				}
			}
			if(mob_tier == 3) {
				if(do_i_powerstrike <= 10) {
					power_strike.put(ent, 1);
					special_attack.put(ent, 1);
					
					ent.getWorld().playSound(ent.getLocation(), Sound.PISTON_EXTEND, 1F, 2.0F);
				}
			}
			if(mob_tier == 4) {
				if(do_i_powerstrike <= 13) {
					power_strike.put(ent, 1);
					special_attack.put(ent, 1);
					
					ent.getWorld().playSound(ent.getLocation(), Sound.PISTON_EXTEND, 1F, 2.0F);
				}
			}
			if(mob_tier == 5) {
				if(do_i_powerstrike <= 20) {
					power_strike.put(ent, 1);
					special_attack.put(ent, 1);
					
					ent.getWorld().playSound(ent.getLocation(), Sound.PISTON_EXTEND, 1F, 2.0F);
				}
			}
			if(mob_tier == -1 && BossMechanics.boss_map.containsKey(ent) && BossMechanics.boss_map.get(ent).equalsIgnoreCase("unholy priest")) {
				if(do_i_powerstrike <= 5) {
					power_strike.put(ent, 1);
					special_attack.put(ent, 1);
					
					ent.getWorld().playSound(ent.getLocation(), Sound.PISTON_EXTEND, 1F, 2.0F);
				}
			}
		}
		
		mob_last_hurt.put(ent, System.currentTimeMillis());
	}
	
	public static String getMobType(Entity e, boolean system_name) {
		if(!system_name) {
			if(e.hasMetadata("mobname")) { return e.getMetadata("mobname").get(0).asString(); }
		}
		
		String mob_type = e.getType().name().substring(0, 1).toUpperCase() + e.getType().name().substring(1, e.getType().name().length()).toLowerCase();
		
		if(e instanceof LivingEntity) {
			try {
				LivingEntity le = (LivingEntity) e;
				if(le.getEquipment().getHelmet() != null && le.getEquipment().getHelmet().getType() == Material.SKULL_ITEM) {
					ItemStack h = le.getEquipment().getHelmet();
					net.minecraft.server.v1_7_R2.ItemStack mItem = CraftItemStack.asNMSCopy(h);
					NBTTagCompound tag = mItem.tag;
					String skin_name = tag.getString("SkullOwner");
					if(skin_name.equalsIgnoreCase("dEr_t0d") || skin_name.equalsIgnoreCase("niv330")) {
						mob_type = "Goblin";
						return mob_type;
					}
					if(skin_name.equalsIgnoreCase("hway234") || skin_name.equalsIgnoreCase("Xmattpt") || skin_name.equalsIgnoreCase("TheNextPaladin")) {
						mob_type = "Bandit";
						return mob_type;
					}
					if(skin_name.equalsIgnoreCase("Yhmen")) {
						mob_type = "Monk";
						return mob_type;
					}
					// MagmaCube | Daemon | Imp | Acolyte
					if(skin_name.equalsIgnoreCase("InfinityWarrior_")) {
						mob_type = "Acolyte";
						return mob_type;
					}
					if(skin_name.equalsIgnoreCase("ArcadiaMovies") || skin_name.equalsIgnoreCase("Malware")) {
						mob_type = "Forest Troll";
						return mob_type;
					}
					if(skin_name.equalsIgnoreCase("Das_Doktor")) {
						mob_type = "Naga";
						return mob_type;
					}
					if(skin_name.equalsIgnoreCase("Xmattpt")) {
						mob_type = "Tripoli Soldier";
						return mob_type;
					}
					if(skin_name.equalsIgnoreCase("_Kashi_")) {
						mob_type = "Lizardman";
						return mob_type;
					}
				} else if(le.getType() == EntityType.SKELETON) {
					int skelly_type = ((CraftSkeleton) e).getHandle().getSkeletonType();
					if(skelly_type == 0) {
						mob_type = "Skeleton";
						return mob_type;
					}
					if(skelly_type == 1) {
						mob_type = "Wither Skeleton";
						return mob_type;
					}
				} else if(le.getType() == EntityType.PIG_ZOMBIE) {
					PigZombie pz = (PigZombie) e;
					if(pz.isBaby()) {
						return "Imp";
					} else {
						return "Daemon";
					}
				}
			} catch(ClassCastException cce) {
				cce.printStackTrace();
			}
		}
		
		return mob_type;
	}
	
	public static String getMobType(Entity e) {
		if(e.hasMetadata("mobname")) { return ChatColor.stripColor(e.getMetadata("mobname").get(0).asString()); }
		
		String mob_type = e.getType().name().substring(0, 1).toUpperCase() + e.getType().name().substring(1, e.getType().name().length()).toLowerCase();
		try {
			if(e instanceof LivingEntity) {
				LivingEntity le = (LivingEntity) e;
				if(le.getEquipment().getHelmet() != null && le.getEquipment().getHelmet().getType() == Material.SKULL_ITEM) {
					ItemStack h = le.getEquipment().getHelmet();
					net.minecraft.server.v1_7_R2.ItemStack mItem = CraftItemStack.asNMSCopy(h);
					NBTTagCompound tag = mItem.tag;
					String skin_name = tag.getString("SkullOwner");
					if(skin_name.equalsIgnoreCase("dEr_t0d") || skin_name.equalsIgnoreCase("niv330")) {
						mob_type = "Goblin";
						return mob_type;
					}
					if(skin_name.equalsIgnoreCase("hway234") || skin_name.equalsIgnoreCase("Xmattpt") || skin_name.equalsIgnoreCase("TheNextPaladin")) {
						mob_type = "Bandit";
						return mob_type;
					}
					if(skin_name.equalsIgnoreCase("Yhmen")) {
						mob_type = "Monk";
						return mob_type;
					}
					if(skin_name.equalsIgnoreCase("ArcadiaMovies") || skin_name.equalsIgnoreCase("Malware")) {
						mob_type = "Forest Troll";
						return mob_type;
					}
					if( skin_name.equalsIgnoreCase("Das_Doktor")) {
						mob_type = "Naga";
						return mob_type;
					}
					if(skin_name.equalsIgnoreCase("Xmattpt")) {
						mob_type = "Tripoli Soldier";
						return mob_type;
					}
					if(skin_name.equalsIgnoreCase("_Kashi_")) {
						mob_type = "Lizardman";
						return mob_type;
					}
				} else if(le.getType() == EntityType.SKELETON) {
					int skelly_type = ((CraftSkeleton) e).getHandle().getSkeletonType();
					if(skelly_type == 0) {
						mob_type = "Skeleton";
						return mob_type;
					}
					if(skelly_type == 1) {
						mob_type = "Wither Skeleton";
						return mob_type;
					}
				} else if(le.getType() == EntityType.PIG_ZOMBIE) {
					PigZombie pz = (PigZombie) e;
					if(pz.isBaby()) {
						return "Imp";
					} else {
						return "Daemon";
					}
				}
			}
		} catch(ClassCastException cce) {
			cce.printStackTrace();
		}
		
		return mob_type;
	}
	
	public static int getPlayerTier(Player p) {
		String p_name = p.getName();
		if(ItemMechanics.player_tier.containsKey(p_name)) { return ItemMechanics.player_tier.get(p_name); }
		return getPlayerTier(p.getName(), p.getInventory().getArmorContents());
	}
	
	public static int getPlayerTier(String p_name, ItemStack[] armor) {
		int highest_tier = 1;
		for(ItemStack is : armor) {
			int is_tier = getItemTier(is);
			if(is_tier > highest_tier) {
				highest_tier = is_tier;
			}
		}
		ItemMechanics.player_tier.put(p_name, highest_tier);
		return highest_tier;
	}
	
	public static int getNPCItemTier(ItemStack is) {
		String name = is.getType().name().toLowerCase();
		if(name.contains("leather")) { return 1; }
		if(name.contains("chain")) { return 2; }
		if(name.contains("iron")) { return 3; }
		if(name.contains("diamond")) { return 4; }
		if(name.contains("gold")) { return 5; }
		return 1;
	}
	
	public static int getNPCTier(ItemStack[] armor) {
		int highest_tier = 1;
		for(ItemStack is : armor) {
			int is_tier = getNPCItemTier(is);
			if(is_tier > highest_tier) {
				highest_tier = is_tier;
			}
		}
		return highest_tier;
	}
	
	@EventHandler
	public void onEntityTargetEvent(final EntityTargetEvent e) {
		if(ignore_target_event.contains(e.getEntity())) {
			e.setCancelled(true);
			return;
		}
		
		if(!(e.getTarget() instanceof Player) || !(mob_tier.containsKey(e.getEntity()))) {
			e.setCancelled(true);
			return;
		}
		final Player p = (Player) e.getTarget();
		
		if(RealmMechanics.player_god_mode.containsKey(p.getName()) && !(p.getName().equalsIgnoreCase("Vaquxine"))) {
			ignore_target_event.add(e.getEntity());
			e.setCancelled(true);
			return;
		}
		
		final Entity ent = e.getEntity();
		
		if(whirlwind.containsKey(ent)) {
			ignore_target_event.add(e.getEntity());
			e.setCancelled(true);
			return;
		}
		
		int mob_tier = getMobTier(e.getEntity());
		int p_tier = getPlayerTier(p);
		
		if(!(InstanceMechanics.isInstance(p.getWorld().getName())) && (e.getReason() != TargetReason.TARGET_ATTACKED_ENTITY) && mob_tier != -1 && p_tier >= (mob_tier + 3)) {
			// Mobs should not target players that are >3 tiers above them in gear. T4 -> T1, T5 -> T1/2
			// UNLESS they're in dungeon!
			ignore_target_event.add(e.getEntity());
			e.setCancelled(true);
			return;
		}
		if(e.getEntity() instanceof Zombie && e.getTarget() instanceof Player) {
			if(e.getEntity().getLocation().distance(e.getTarget().getLocation()) > 15.0D) {
				//15 block radius for mob spawns * Fix for the extra long agro range
				e.setCancelled(true);
				return;
			}
		}
		async_entity_target.add(e);
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onMobSpawnEvent(CreatureSpawnEvent e) {
		if(e.getSpawnReason() != SpawnReason.CUSTOM) {
			e.setCancelled(true);
			return;
		}
		
		/*final Entity ent = e.getEntity();

		this.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
			public void run() {
				if(!(mob_health.containsKey(ent))){
					ent.remove();
				}
			}
		}, 10L);*/
		
	}
	
	/*@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerPickupItemEvent(PlayerPickupItemEvent e) {
		Player p = e.getPlayer();
		Item it = e.getItem();
		if (it.getType() != EntityType.DROPPED_ITEM) {
			return;
		}
		if (it.getType() == EntityType.ARROW) {
			return;
		}
		try {
			EntityItem ei = ((EntityItem) ((CraftItem) it).getHandle());
			if (!(drop_protection.containsKey(ei))) {
				return;
			}
			if (!drop_protection.get(ei).equalsIgnoreCase(p.getName())) {
				e.setCancelled(true);
			}
		} catch (ClassCastException cce) {
			return; // Don't do anything if it's not the right item.
		}
	}*/
	
	@EventHandler(ignoreCancelled = false, priority = EventPriority.MONITOR)
	public void onDeathCleanupEnchants(EntityDeathEvent e) {
		if(e.getEntity() instanceof Player) return;
		if(e.getDrops() == null)return;
		for(ItemStack i : e.getDrops()) {
			for(Enchantment n : Enchantment.values()) {
				i.removeEnchantment(n);
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler(ignoreCancelled = false)
	public void onEntityDeathEvent(final EntityDeathEvent e) {
		final Entity ent = e.getEntity();
		final String p_name = mob_target.get(ent);
		boolean never_unload_home = false;
		
		if(ent instanceof Player) {
			Player pl = (Player) ent;
			if(pl.hasMetadata("NPC")) {
				e.getDrops().clear();
				return;
			}
			return; // Not a custom monster, no one cares.
		}
		
		if(RecordMechanics.mob_kills.containsKey(p_name)) {
			int lmob_kills = RecordMechanics.mob_kills.get(p_name);
			lmob_kills = lmob_kills + 1;
			if(lmob_kills >= 100) {
				AchievmentMechanics.addAchievment(p_name, "Monster Hunter I");
				if(lmob_kills >= 300) {
					AchievmentMechanics.addAchievment(p_name, "Monster Hunter II");
					if(lmob_kills >= 500) {
						AchievmentMechanics.addAchievment(p_name, "Monster Hunter III");
						if(lmob_kills >= 1000) {
							AchievmentMechanics.addAchievment(p_name, "Monster Hunter IV");
							if(lmob_kills >= 1500) {
								AchievmentMechanics.addAchievment(p_name, "Monster Hunter V");
								if(lmob_kills >= 2000) {
									AchievmentMechanics.addAchievment(p_name, "Monster Hunter VI");
								}
							}
						}
					}
				}
				
			}
			RecordMechanics.mob_kills.put(p_name, lmob_kills);
		}
		/*if (!mob_health.containsKey(ent)) {
			e.getDrops().clear();
			e.setDroppedExp(0);
			return;
		}*/
		
		if(ent.getPassenger() != null) {
			Entity r_ent = ent.getPassenger();
			r_ent.playEffect(EntityEffect.DEATH);
			r_ent.remove();
		}
		
		if(ent.getVehicle() != null) {
			Entity mount = ent.getVehicle();
			ent.eject();
			mount.remove();
		}
		
		if(p_name != null && Bukkit.getPlayer(p_name) != null) {
			String kill_list = "";
			if(passive_hunter_achiev.containsKey(p_name)) {
				kill_list = passive_hunter_achiev.get(p_name);
			}
			if(ent.getType() == EntityType.PIG) {
				if(!(kill_list.contains(ent.getType().getName()))) {
					kill_list += ent.getType().getName() + ",";
				}
			}
			if(ent.getType() == EntityType.BAT) {
				if(!(kill_list.contains(ent.getType().getName()))) {
					kill_list += ent.getType().getName() + ",";
				}
				Player pl = Bukkit.getPlayer(p_name);
				if(pl.getItemInHand().getType() == Material.BOW && ent.getLastDamageCause().getCause() == DamageCause.PROJECTILE) {
					AchievmentMechanics.addAchievment(p_name, "Crack Shot");
				}
			}
			if(ent.getType() == EntityType.COW) {
				if(!(kill_list.contains(ent.getType().getName()))) {
					kill_list += ent.getType().getName() + ",";
				}
			}
			if(ent.getType() == EntityType.CHICKEN) {
				if(!(kill_list.contains(ent.getType().getName()))) {
					kill_list += ent.getType().getName() + ",";
				}
			}
			if(ent.getType() == EntityType.MUSHROOM_COW) {
				if(!(kill_list.contains(ent.getType().getName()))) {
					kill_list += ent.getType().getName() + ",";
				}
			}
			
			if(kill_list.split(",").length >= 5) {
				AchievmentMechanics.addAchievment(p_name, "Passive Hunter");
			}
			
			passive_hunter_achiev.put(p_name, kill_list);
		}
		
		approaching_mage_list.remove(ent);
		mob_last_hurt.remove(ent);
		mob_last_hit.remove(ent);
		power_strike.remove(ent);
		whirlwind.remove(ent);
		mob_yaw.remove(ent);
		special_attack.remove(ent);
		special_attack.remove(ent);
		ignore_target_event.remove(ent);
		async_entity_target.remove(ent);
		last_respawn.remove(ent);
		//mob_spawn_ownership.remove(ent);
		
		if(p_name != null && Bukkit.getPlayer(p_name) != null && !(hasCustomDrops(ent)) && !DuelMechanics.isDamageDisabled(ent.getLocation()) && mob_health.containsKey(ent) && mob_loot.containsKey(ent) && mob_target.containsKey(ent) && !BossMechanics.boss_map.containsKey(ent) && !(InstanceMechanics.isInstance(ent.getWorld().getName())) && ent.getWorld().getName().equalsIgnoreCase(Bukkit.getWorlds().get(0).getName())) {
			Player pl = Bukkit.getPlayer(p_name);
			ItemStack in_hand = pl.getItemInHand();
			int tier = ItemMechanics.getItemTier(in_hand);
			int mob_tier = MonsterMechanics.getMobTier(ent);
			if(mob_tier >= 4) {
				never_unload_home = true;
			}
			
			if((tier - mob_tier) <= 2) {
				final List<ItemStack> ent_gear = mob_loot.get(ent);
				
				// Thread t = new Thread(new Runnable() {
				//  public void run() {
				Player p = Bukkit.getPlayer(p_name);
				if(p.getWorld().getName().equalsIgnoreCase(ent.getWorld().getName())) { // && p.getLocation().distanceSquared(ent.getLocation()) <= 484
				
					double do_i_drop = new Random().nextInt(100); // 10% Chance.
					double do_i_drop_scroll = new Random().nextInt(100);
					// double do_i_drop_gems = new Random().nextInt(100);
					double drop_chance = 0;
					double gem_drop_chance = new Random().nextInt(100);
					double scroll_drop_chance = 0;
					// log.info(String.valueOf(gem_drop_chance));
					double gem_drop_amount = 0;
					double drop_multiplier = 1;
					boolean is_elite = false;
					// Elite = 1.5x money chance / item chance.
					ItemStack weapon = ent_gear.get(0);
					if(weapon.getEnchantments().containsKey(Enchantment.KNOCKBACK)) {
						is_elite = true;
					}
					
					if(is_elite == true) {
						drop_multiplier = 1.5;
					}
					
					double item_drop_multiplier = 1 + (ItemMechanics.ifind_data.get(p_name) / 100);
					double gold_drop_multiplier = 1 + (ItemMechanics.gfind_data.get(p_name) / 100);
					
					if(PartyMechanics.party_map.containsKey(p.getName())) { // They're in a party.
						int mem_nearby = PartyMechanics.getPlayersInArea(PartyMechanics.party_map.get(p.getName()), ent.getLocation(), 75);
						if(mem_nearby >= 4 && mem_nearby < 8) {
							item_drop_multiplier += (item_drop_multiplier * 0.005);
							gold_drop_multiplier += (item_drop_multiplier * 0.005);
						}
						if(mem_nearby >= 8) {
							item_drop_multiplier += (item_drop_multiplier * 0.01);
							gold_drop_multiplier += (item_drop_multiplier * 0.005);
						}
					}
					
					if(weapon.getType() == Material.WOOD_AXE || weapon.getType() == Material.WOOD_SWORD || weapon.getType() == Material.WOOD_SPADE || weapon.getType() == Material.WOOD_HOE || getItemTier(weapon) == 1) { // T1
						scroll_drop_chance = 2;
						drop_chance = 12;
						if(gem_drop_chance <= ((50 * gold_drop_multiplier) * drop_multiplier)) {
							gem_drop_amount = (new Random().nextInt(3 - 1) + 1) * gold_drop_multiplier;
						}
						if(scroll_drop_chance >= do_i_drop_scroll) {
							// Drop a scroll. Which one?
							int scroll_type = new Random().nextInt(2); // 0, 1
							if(scroll_type == 0) {
								ent.getWorld().dropItemNaturally(ent.getLocation(), CraftItemStack.asCraftCopy(TeleportationMechanics.makeUnstackable(TeleportationMechanics.Cyrennica_scroll)));
							}
							if(scroll_type == 1) {
								ent.getWorld().dropItemNaturally(ent.getLocation(), CraftItemStack.asCraftCopy(TeleportationMechanics.makeUnstackable(TeleportationMechanics.Harrison_scroll)));
							}
						}
					}
					
					if(weapon.getType() == Material.STONE_AXE || weapon.getType() == Material.STONE_SWORD || weapon.getType() == Material.STONE_SPADE || weapon.getType() == Material.STONE_HOE || getItemTier(weapon) == 2) { // T2
						scroll_drop_chance = 2;
						drop_chance = 5;
						if(gem_drop_chance <= ((40 * gold_drop_multiplier) * drop_multiplier)) {
							gem_drop_amount = (new Random().nextInt(12 - 2) + 2) * gold_drop_multiplier;
						}
						if(scroll_drop_chance >= do_i_drop_scroll) {
							// Drop a scroll. Which one?
							int scroll_type = new Random().nextInt(5); // 0, 1
							if(scroll_type == 0) {
								ent.getWorld().dropItemNaturally(ent.getLocation(), CraftItemStack.asCraftCopy(TeleportationMechanics.makeUnstackable(TeleportationMechanics.Cyrennica_scroll)));
							}
							if(scroll_type == 1) {
								ent.getWorld().dropItemNaturally(ent.getLocation(), CraftItemStack.asCraftCopy(TeleportationMechanics.makeUnstackable(TeleportationMechanics.Harrison_scroll)));
							}
							if(scroll_type == 2) {
								ent.getWorld().dropItemNaturally(ent.getLocation(), CraftItemStack.asCraftCopy(TeleportationMechanics.makeUnstackable(TeleportationMechanics.Dark_Oak_Tavern_scroll)));
							}
							if(scroll_type == 3) {
								ent.getWorld().dropItemNaturally(ent.getLocation(), CraftItemStack.asCraftCopy(TeleportationMechanics.makeUnstackable(TeleportationMechanics.Jagged_Rocks_Tavern)));
							}
							if(scroll_type == 4) {
								ent.getWorld().dropItemNaturally(ent.getLocation(), CraftItemStack.asCraftCopy(TeleportationMechanics.makeUnstackable(TeleportationMechanics.Tripoli_scroll)));
							}
						}
					}
					
					if(weapon.getType() == Material.IRON_AXE || weapon.getType() == Material.IRON_SWORD || weapon.getType() == Material.IRON_SPADE || weapon.getType() == Material.IRON_HOE || getItemTier(weapon) == 3) { // T3
						scroll_drop_chance = 2;
						drop_chance = 3;
						if(gem_drop_chance <= ((30 * gold_drop_multiplier) * drop_multiplier)) {
							gem_drop_amount = (new Random().nextInt(30 - 10) + 10) * gold_drop_multiplier;
						}
						if(scroll_drop_chance >= do_i_drop_scroll) {
							// Drop a scroll. Which one?
							int scroll_type = new Random().nextInt(5); // 0, 1, 2, 3
							if(scroll_type == 0) {
								ent.getWorld().dropItemNaturally(ent.getLocation(), CraftItemStack.asCraftCopy(TeleportationMechanics.makeUnstackable(TeleportationMechanics.Cyrennica_scroll)));
							}
							if(scroll_type == 1) {
								ent.getWorld().dropItemNaturally(ent.getLocation(), CraftItemStack.asCraftCopy(TeleportationMechanics.makeUnstackable(TeleportationMechanics.Dark_Oak_Tavern_scroll)));
							}
							if(scroll_type == 2) {
								ent.getWorld().dropItemNaturally(ent.getLocation(), CraftItemStack.asCraftCopy(TeleportationMechanics.makeUnstackable(TeleportationMechanics.Jagged_Rocks_Tavern)));
							}
							if(scroll_type == 3) {
								ent.getWorld().dropItemNaturally(ent.getLocation(), CraftItemStack.asCraftCopy(TeleportationMechanics.makeUnstackable(TeleportationMechanics.Swamp_safezone_scroll)));
							}
							if(scroll_type == 4) {
								ent.getWorld().dropItemNaturally(ent.getLocation(), CraftItemStack.asCraftCopy(TeleportationMechanics.makeUnstackable(TeleportationMechanics.Crestguard_keep_scroll)));
							}
						}
					}
					
					if(weapon.getType() == Material.DIAMOND_AXE || weapon.getType() == Material.DIAMOND_SWORD || weapon.getType() == Material.DIAMOND_HOE || weapon.getType() == Material.DIAMOND_SPADE || getItemTier(weapon) == 4) { // T4
						scroll_drop_chance = 1;
						drop_chance = 1;
						if(gem_drop_chance <= ((20 * gold_drop_multiplier) * drop_multiplier)) {
							gem_drop_amount = (new Random().nextInt(50 - 20) + 20) * gold_drop_multiplier;
						}
						if(scroll_drop_chance >= do_i_drop_scroll) {
							// Drop a scroll. Which one?
							int scroll_type = new Random().nextInt(2); // 0, 1
							if(scroll_type == 0) {
								ent.getWorld().dropItemNaturally(ent.getLocation(), CraftItemStack.asCraftCopy(TeleportationMechanics.makeUnstackable(TeleportationMechanics.Deadpeaks_Mountain_Camp_scroll)));
							}
							if(scroll_type == 1) {
								ent.getWorld().dropItemNaturally(ent.getLocation(), CraftItemStack.asCraftCopy(TeleportationMechanics.makeUnstackable(TeleportationMechanics.Swamp_safezone_scroll)));
							}
						}
					}
					if(weapon.getType() == Material.GOLD_AXE || weapon.getType() == Material.GOLD_SWORD || weapon.getType() == Material.GOLD_HOE || weapon.getType() == Material.GOLD_SPADE || getItemTier(weapon) == 5) { // T5
						scroll_drop_chance = 1;
						drop_chance = 1;
						if(gem_drop_chance <= ((35 * gold_drop_multiplier) * drop_multiplier)) {
							gem_drop_amount = (new Random().nextInt(200 - 75) + 75) * gold_drop_multiplier;
						}
						if(scroll_drop_chance >= do_i_drop_scroll) {
							// Drop a scroll. Which one?
							int scroll_type = new Random().nextInt(2); // 0, 1
							if(scroll_type == 0) {
								ent.getWorld().dropItemNaturally(ent.getLocation(), CraftItemStack.asCraftCopy(TeleportationMechanics.makeUnstackable(TeleportationMechanics.Deadpeaks_Mountain_Camp_scroll)));
							}
							if(scroll_type == 1) {
								ent.getWorld().dropItemNaturally(ent.getLocation(), CraftItemStack.asCraftCopy(TeleportationMechanics.makeUnstackable(TeleportationMechanics.Swamp_safezone_scroll)));
							}
						}
					}
					
					String mob_type = ChatColor.stripColor(getMobType(ent)).toLowerCase();
					if(mob_type.contains("imp") || mob_type.contains("daemon")) {
						scroll_drop_chance = 2;
						if(scroll_drop_chance >= do_i_drop_scroll) {
							// Drop a scroll. Which one?
							int scroll_type = new Random().nextInt(1); // 0
							if(scroll_type == 0) {
								ent.getWorld().dropItemNaturally(ent.getLocation(), CraftItemStack.asCraftCopy(TeleportationMechanics.makeUnstackable(TeleportationMechanics.Crestguard_keep_scroll)));
							}
						}
					}
					
					/*String mob_type = getMobType(ent);
					if(mob_type.equalsIgnoreCase("naga")){
						// GLOOMY
						scroll_drop_chance = 3;
						if(scroll_drop_chance >= do_i_drop_scroll){
							// Drop a scroll. Which one?
							int scroll_type = new Random().nextInt(1); // 0
							if(scroll_type == 0){
								ent.getWorld().dropItemNaturally(ent.getLocation(), CraftItemStack.asCraftCopy(TeleportationMechanics.makeUnstackable(TeleportationMechanics.Swamp_safezone_scroll)));
							}
						}
					}
					if(mob_type.equalsIgnoreCase("tripoli soldier") || mob_type.equalsIgnoreCase("lizardman")){
						// TRIPOLI
						scroll_drop_chance = 2;
						if(scroll_drop_chance >= do_i_drop_scroll){
							// Drop a scroll. Which one?
							int scroll_type = new Random().nextInt(1); // 0
							if(scroll_type == 0){
								ent.getWorld().dropItemNaturally(ent.getLocation(), CraftItemStack.asCraftCopy(TeleportationMechanics.makeUnstackable(TeleportationMechanics.Tripoli_scroll)));
							}
						}
					}*/
					
					drop_chance = (drop_chance * item_drop_multiplier);
					drop_chance = (drop_chance * drop_multiplier);
					
					if(is_elite) {
						if(mob_tier == 1) {
							drop_chance = 100;
						}
						if(mob_tier == 2) {
							drop_chance = 50;
						}
						if(mob_tier == 3) {
							drop_chance = 10;
						}
						if(mob_tier == 4) {
							drop_chance = 2;
						}
						if(mob_tier == 5) {
							drop_chance = 1;
						}
					}
					
					int do_i_drop_arrows = new Random().nextInt(100);
					int do_i_drop_quiver = new Random().nextInt(100);
					if(weapon.getType() == Material.BOW && do_i_drop_arrows <= 85) { // Drop some arrows!
						int amount_to_drop = new Random().nextInt(10) + 1;
					
						if(mob_tier == 1) {
							ItemStack arrow_loot = ItemMechanics.t1_arrow;
							arrow_loot.setAmount(amount_to_drop);
							ent.getWorld().dropItemNaturally(ent.getLocation(), arrow_loot);
						}
						
						if(mob_tier == 2) {
							ItemStack arrow_loot = ItemMechanics.t2_arrow;
							arrow_loot.setAmount(amount_to_drop);
							ent.getWorld().dropItemNaturally(ent.getLocation(), arrow_loot);
						}
						if(mob_tier == 3) {
							int drop_t4 = new Random().nextInt(100);
							ItemStack arrow_loot = null;
							if(drop_t4 <= 5) {
								arrow_loot = ItemMechanics.t4_arrow;
							} else {
								arrow_loot = ItemMechanics.t3_arrow;
							}
							arrow_loot.setAmount(amount_to_drop);
							ent.getWorld().dropItemNaturally(ent.getLocation(), arrow_loot);
							 if(do_i_drop_quiver <= 10){
		                            ent.getWorld().dropItemNaturally(ent.getLocation(), ItemMechanics.t1_quiver);
		                        }
						}
						if(mob_tier == 4) {
							int drop_t5 = new Random().nextInt(100);
							ItemStack arrow_loot = null;
							if(drop_t5 <= 15) {
								arrow_loot = ItemMechanics.t5_arrow;
							} else {
								arrow_loot = ItemMechanics.t4_arrow;
							}
							arrow_loot.setAmount(amount_to_drop);
							ent.getWorld().dropItemNaturally(ent.getLocation(), arrow_loot);
							 if(do_i_drop_quiver <= 10){
		                            ent.getWorld().dropItemNaturally(ent.getLocation(), ItemMechanics.t1_quiver);
		                        }
						}
						if(mob_tier == 5) {
							ItemStack arrow_loot = ItemMechanics.t5_arrow;
							arrow_loot.setAmount(amount_to_drop);
							ent.getWorld().dropItemNaturally(ent.getLocation(), arrow_loot);
							 if(do_i_drop_quiver <= 10){
		                            ent.getWorld().dropItemNaturally(ent.getLocation(), ItemMechanics.t1_quiver);
		                        }
						}
					}
					
					// EASTER EGG DROP CODE
					int do_i_drop_easter = new Random().nextInt(2000);
					if(mob_tier <= 3){
						if(do_i_drop_easter == 1){ // 0.05% chance
							Location loc = ent.getLocation();
							loc.getWorld().dropItemNaturally(loc, CraftItemStack.asCraftCopy(ItemMechanics.easter_egg));
						}
					}
					if(mob_tier > 3){ // 4, 5
						if(do_i_drop_easter <= 10){ // 0.5% chance
							Location loc = ent.getLocation();
							loc.getWorld().dropItemNaturally(loc, CraftItemStack.asCraftCopy(ItemMechanics.easter_egg));
						}
					}
					
					// HALLOWEEN DROP CODE
					/*int do_i_drop_candy = new Random().nextInt(1000);
					int do_i_drop_mask = new Random().nextInt(10000);
					ItemStack candy = CraftItemStack.asCraftCopy(Halloween.halloween_candy);
					ItemStack mask = CraftItemStack.asCraftCopy(Halloween.halloween_mask);
					
					if(mob_tier == 1 && do_i_drop_candy == 0){
						candy.setAmount(new Random().nextInt(4) + 2);
						ent.getLocation().getWorld().dropItemNaturally(ent.getLocation(), candy);}
						
					if(mob_tier == 2 && do_i_drop_candy <= 5){
						candy.setAmount(new Random().nextInt(4) + 2);
						ent.getLocation().getWorld().dropItemNaturally(ent.getLocation(), candy);}

					if(mob_tier == 3 && do_i_drop_candy <= 10){
						candy.setAmount(new Random().nextInt(4) + 2);
						ent.getLocation().getWorld().dropItemNaturally(ent.getLocation(), candy);}

					if(mob_tier == 4 && do_i_drop_candy <= 20){
						candy.setAmount(new Random().nextInt(4) + 2);
						ent.getLocation().getWorld().dropItemNaturally(ent.getLocation(), candy);}
						
					if(mob_tier == 5 && do_i_drop_candy <= 30){
						candy.setAmount(new Random().nextInt(4) + 2);
						ent.getLocation().getWorld().dropItemNaturally(ent.getLocation(), candy);}
						
						
					if(mob_tier == 1 && do_i_drop_mask == 0)
						ent.getLocation().getWorld().dropItemNaturally(ent.getLocation(), mask);
							
					if(mob_tier == 2 && do_i_drop_mask <= 2)
						ent.getLocation().getWorld().dropItemNaturally(ent.getLocation(), mask);

					if(mob_tier == 3 && do_i_drop_mask <= 5)
						ent.getLocation().getWorld().dropItemNaturally(ent.getLocation(), mask);

					if(mob_tier == 4 && do_i_drop_mask <= 7)
						ent.getLocation().getWorld().dropItemNaturally(ent.getLocation(), mask);
							
					if(mob_tier == 5 && do_i_drop_mask <= 12)
						ent.getLocation().getWorld().dropItemNaturally(ent.getLocation(), mask);*/
					
					// Orb of Peace DROP CODE
					/*int do_i_drop_oop = new Random().nextInt(100);
					if(mob_tier == 2){
						if(do_i_drop_oop <= 1){
							Location loc = ent.getLocation();
							loc.getWorld().dropItemNaturally(loc, CraftItemStack.asCraftCopy(ItemMechanics.orb_of_peace));
						}
					}
					if(mob_tier == 3){
						if(do_i_drop_oop <= 1){ 
							Location loc = ent.getLocation();
							loc.getWorld().dropItemNaturally(loc, CraftItemStack.asCraftCopy(ItemMechanics.orb_of_peace));
						}
					}
					if(mob_tier == 4){
						if(do_i_drop_oop <= 1){ 
							Location loc = ent.getLocation();
							loc.getWorld().dropItemNaturally(loc, CraftItemStack.asCraftCopy(ItemMechanics.orb_of_peace));
						}
					}
					if(mob_tier == 5){
						if(do_i_drop_oop <= 2){ 
							Location loc = ent.getLocation();
							loc.getWorld().dropItemNaturally(loc, CraftItemStack.asCraftCopy(ItemMechanics.orb_of_peace));
						}
					}*/
					
					if(mob_tier == 5 && !is_elite) { //  
						int chance = new Random().nextInt(1000);
						if(chance == 500 || chance == 501) { // 0.2% chance of it equalling that.
							drop_chance = 100;
						} else {
							drop_chance = 0;
						}
					}
					if(mob_tier == 4 && !is_elite) {
						int chance = new Random().nextInt(1000);
						if(chance <= 5) { // 1% chance of it equalling that.
							drop_chance = 100;
						} else {
							drop_chance = 0;
						}
					}
					
					if(loot_buff == true) {
						drop_chance *= 1.20;
					}
					
					if(drop_chance >= do_i_drop) {
						int drop_type = new Random().nextInt(2); // 0-1 = weapon, 1 = armor
						ItemStack i_gear = null;
						
						if(drop_type == 0) {
							i_gear = weapon;
						}
						
						if(drop_type == 1) {
							if((ent_gear.size() - 1) > 0) {
								int gear_index = new Random().nextInt(ent_gear.size() - 1) + 1; // Never 0.
								if(gear_index > (ent_gear.size() - 1)) {
									gear_index = (ent_gear.size() - 1);
								}
								
								i_gear = ent_gear.get(gear_index);
								
								if(i_gear.getTypeId() == 397 || i_gear.getTypeId() == 144) { // Set the drop to chest, since they'll have chest if they have helmet / mask.
									i_gear = ent_gear.get(2);
								}
							}
							
							else if((ent_gear.size() - 1) <= 0) {
								i_gear = ent_gear.get(0);
							}
							
						}
						
						int pre_random_dur = i_gear.getType().getMaxDurability();
						if(pre_random_dur <= 0) {
							pre_random_dur = 1;
						}
						
						int random_dur = new Random().nextInt(pre_random_dur) + (i_gear.getType().getMaxDurability() / 10);
						if(random_dur > i_gear.getType().getMaxDurability()) {
							random_dur = i_gear.getType().getMaxDurability();
						}
						i_gear.setDurability((short) random_dur);
						
						ItemMeta im = i_gear.getItemMeta();
						
						try {
							if(im.hasEnchants()) {
								for(Map.Entry<Enchantment, Integer> data : im.getEnchants().entrySet()) {
									i_gear.removeEnchantment(data.getKey());
								}
							}
							i_gear.removeEnchantment(Enchantment.LOOT_BONUS_MOBS);
							i_gear.removeEnchantment(Enchantment.KNOCKBACK);
							i_gear.removeEnchantment(EnchantMechanics.getCustomEnchant());
						} catch(NullPointerException npe) {
							npe.printStackTrace();
						}
						
						i_gear.setItemMeta(im);
						
						if(i_gear.getType() != Material.AIR) {
							ent.getWorld().dropItemNaturally(ent.getLocation(), i_gear);
						}
					}
					
					if(gem_drop_amount > 0) {
						// net.minecraft.server.ItemStack gems = (((CraftItemStack)
						// ).getHandle());
						while(gem_drop_amount > 64) {
							ItemStack i = MoneyMechanics.makeGems((int) 64);
							Location loc = ent.getLocation();
							loc.getWorld().dropItemNaturally(loc, i);
							gem_drop_amount = gem_drop_amount - 64;
						}
						
						ItemStack i = MoneyMechanics.makeGems((int) gem_drop_amount); // Drop the remainder.
						Location loc = ent.getLocation();
						loc.getWorld().dropItemNaturally(loc, i);
					}
				}
			}
		} else if(hasCustomDrops(ent)) {
			List<ItemStack> loot = getCustomDrops(ent, null);
			if(ent.getLocation().getWorld().getName().equalsIgnoreCase(main_world_name)) {
				never_unload_home = true;
			}
			if(loot != null && loot.size() > 0) {
				for(ItemStack is : loot) {
					if(is != null) {
					    if(InstanceMechanics.isInstance(ent.getWorld().getName())){
					        if(!ItemMechanics.isArmor(is) && !ItemMechanics.isWeapon(is)){
					            ent.getWorld().dropItemNaturally(ent.getLocation(), is);
					        }
					    }else{
					        ent.getWorld().dropItemNaturally(ent.getLocation(), is);
					    }
					}
				}
			}
		}
		
		if(!(ent instanceof Player)) {
			e.getDrops().clear();
		}
		
		if(mob_spawn_ownership.containsKey(ent)) {
			//Thread t = new Thread(new Runnable() {
			//	  public void run() {
			int mob_num = getMobsUniqueNumber(ent);
			Location spawner_loc = getMobsHomeSpawner(ent);
			
			if(never_unload_home == true) {
				Location chunk = spawner_loc.getChunk().getBlock(0, 0, 0).getLocation();
				if(!(never_unload_chunks.contains(chunk))) {
					never_unload_chunks.add(chunk);
				}
			}
			
			recent_loaded_chunks.put(spawner_loc.getChunk().getBlock(0, 0, 0).getLocation(), System.currentTimeMillis());
			// A mob was killed, so prevent unload anytime soon.
			
			if(spawned_mobs.containsKey(spawner_loc)) {
				List<Integer> spawned_mob_nums = spawned_mobs.get(spawner_loc);
				
				List<Entity> lspawned_mobs = new ArrayList<Entity>();
				if(local_spawned_mobs.containsKey(spawner_loc)) {
					lspawned_mobs = local_spawned_mobs.get(spawner_loc);
					lspawned_mobs.remove(ent);
					local_spawned_mobs.put(spawner_loc, lspawned_mobs);
				} else if(!(local_spawned_mobs.containsKey(spawner_loc))) {
					lspawned_mobs.remove(ent);
					local_spawned_mobs.put(spawner_loc, lspawned_mobs);
				}
				
				spawned_mob_nums.remove(new Integer(mob_num));
				spawned_mobs.put(spawner_loc, spawned_mob_nums);
				
				addMobToRespawnList(ent);
			} else if(!(spawned_mobs.containsKey(spawner_loc))) {
				mob_spawn_ownership.remove(ent);
				//no_delay_kills.remove(ent);
			}
		}
		
		mob_health.remove(ent);
		mob_level.remove(ent);
		max_mob_health.remove(ent);
		mob_damage.remove(ent);
		mob_tier.remove(ent);
		mob_loot.remove(ent);
		mob_target.remove(ent);
		
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onEntityDeath(EntityDeathEvent e){
	    if(e.getEntity() instanceof Player)return;
	    if(InstanceMechanics.isInstance(e.getEntity().getWorld().getName())){
	        //System.out.print("WAS GOING TO DROP THIS: " + e.getDrops().toString() + " FROM: " + e.getEntity());
	        //e.getDrops().clear();
	    }
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onItemSpawn(ItemSpawnEvent e){
	    if(e.getEntity().getItemStack().getType() == Material.SKULL_ITEM){
	        e.setCancelled(true);
	        e.getEntity().remove();
	        return;
	    }
	 
	    if(InstanceMechanics.isInstance(e.getEntity().getWorld().getName())){ 
	        if(e.getEntity().getItemStack().getDurability() == 0){
	                return;
	            }
	        if(e.getEntity().hasMetadata("boss_drop") || e.getEntity().hasMetadata("player_drop")){
	            Main.d(CC.RED + "DROP HAD SOME DATA DAWG");
	            e.setCancelled(false);
	            return;
	        }
	        //Dont remove boss_drops or player_drops
	        if(ItemMechanics.isArmor(e.getEntity().getItemStack()) || ItemMechanics.isWeapon(e.getEntity().getItemStack())){
	           
	            Main.d(e.getEntity().hasMetadata("boss_drop"));
	            e.setCancelled(true);
	            e.getEntity().remove();
	            System.out.print("ITEM WOULD HAVE DROPPED IN AN INSTANCE: " + e.getEntity().getItemStack().getType() + " DURA: " + e.getEntity().getItemStack().getDurability() + " MAX DURA: " + e.getEntity().getItemStack().getType().getMaxDurability());
	            return;
	        }
	    }
	}
	public Location getMobsHomeSpawner(Entity e) {
		// TODO: Parse out mob_spawn_ownership (x,y,z:#)
		/*
		 if(!(mob_spawn_ownership.contains(e))){ return new
		 Location(e.getWorld(), 0, 0, 0); }
		 */
		
		/*if(!(e instanceof LivingEntity)){
			return null;
		}*/
		
		if(!(mob_spawn_ownership.containsKey(e))) {
			//LivingEntity le = (LivingEntity)e;
			//le.damage(le.getHealth());
			return null;
		}
		String mob_ownership_data = mob_spawn_ownership.get(e);
		Location loc = new Location(e.getWorld(), Double.parseDouble(mob_ownership_data.split(":")[0].split(",")[0]), Double.parseDouble(mob_ownership_data.split(":")[0].split(",")[1]), Double.parseDouble(mob_ownership_data.split(":")[0].split(",")[2]));
		return loc;
	}
	
	public int getMobsUniqueNumber(Entity e) {
		String mob_ownership_data = mob_spawn_ownership.get(e);
		int mob_num = Integer.parseInt(mob_ownership_data.split(":")[1]);
		return mob_num;
	}
	
	public static ItemStack getHead(String player_name) {
		
		ItemStack c_mask = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
		net.minecraft.server.v1_7_R2.ItemStack mItem = CraftItemStack.asNMSCopy(c_mask);
		NBTTagCompound tag = mItem.tag = new NBTTagCompound();
		tag.setString("SkullOwner", player_name);
		return CraftItemStack.asBukkitCopy(mItem);
	}
	
	public static int getItemTier(ItemStack i) {
		try {
			String name = CraftItemStack.asNMSCopy(i).getTag().getCompound("display").getString("Name");
			if(name.contains(ChatColor.WHITE.toString())) { return 1; }
			if(name.contains(ChatColor.GREEN.toString())) { return 2; }
			if(name.contains(ChatColor.AQUA.toString())) { return 3; }
			if(name.contains(ChatColor.LIGHT_PURPLE.toString())) { return 4; }
			if(name.contains(ChatColor.YELLOW.toString())) { return 5; }
		} catch(NullPointerException npe) {
			return 1;
		}
		return 1;
	}
	
	public static ItemStack spawnRandomMeleeWeapon(int tier, boolean no_bow, boolean no_weapon) {
		if(no_weapon == true) {
			int wep_type = new Random().nextInt(2);
			if(wep_type == 0) {
				if(tier == 1) { return ItemGenerators.SwordGenorator(Material.WOOD_SWORD, false, null); }
				if(tier == 2) { return ItemGenerators.SwordGenorator(Material.STONE_SWORD, false, null); }
				if(tier == 3) { return ItemGenerators.SwordGenorator(Material.IRON_SWORD, false, null); }
				if(tier == 4) { return ItemGenerators.SwordGenorator(Material.DIAMOND_SWORD, false, null); }
				if(tier == 5) { return ItemGenerators.SwordGenorator(Material.GOLD_SWORD, false, null); }
			}
			
			if(wep_type == 1) {
				if(tier == 1) { return ItemGenerators.AxeGenorator(Material.WOOD_AXE, false, null); }
				if(tier == 2) { return ItemGenerators.AxeGenorator(Material.STONE_AXE, false, null); }
				if(tier == 3) { return ItemGenerators.AxeGenorator(Material.IRON_AXE, false, null); }
				if(tier == 4) { return ItemGenerators.AxeGenorator(Material.DIAMOND_AXE, false, null); }
				if(tier == 5) { return ItemGenerators.AxeGenorator(Material.GOLD_AXE, false, null); }
			}
		} else if(no_bow == true && no_weapon == false) {
			int wep_type = new Random().nextInt(4);
			if(wep_type == 0) {
				if(tier == 1) { return ItemGenerators.SwordGenorator(Material.WOOD_SWORD, false, null); }
				if(tier == 2) { return ItemGenerators.SwordGenorator(Material.STONE_SWORD, false, null); }
				if(tier == 3) { return ItemGenerators.SwordGenorator(Material.IRON_SWORD, false, null); }
				if(tier == 4) { return ItemGenerators.SwordGenorator(Material.DIAMOND_SWORD, false, null); }
				if(tier == 5) { return ItemGenerators.SwordGenorator(Material.GOLD_SWORD, false, null); }
			}
			
			if(wep_type == 1) {
				if(tier == 1) { return ItemGenerators.AxeGenorator(Material.WOOD_AXE, false, null); }
				if(tier == 2) { return ItemGenerators.AxeGenorator(Material.STONE_AXE, false, null); }
				if(tier == 3) { return ItemGenerators.AxeGenorator(Material.IRON_AXE, false, null); }
				if(tier == 4) { return ItemGenerators.AxeGenorator(Material.DIAMOND_AXE, false, null); }
				if(tier == 5) { return ItemGenerators.AxeGenorator(Material.GOLD_AXE, false, null); }
			}
			
			if(wep_type == 2) {
				if(tier == 1) { return ItemGenerators.PolearmGenorator(Material.WOOD_SPADE, false, null); }
				if(tier == 2) { return ItemGenerators.PolearmGenorator(Material.STONE_SPADE, false, null); }
				if(tier == 3) { return ItemGenerators.PolearmGenorator(Material.IRON_SPADE, false, null); }
				if(tier == 4) { return ItemGenerators.PolearmGenorator(Material.DIAMOND_SPADE, false, null); }
				if(tier == 5) { return ItemGenerators.PolearmGenorator(Material.GOLD_SPADE, false, null); }
			}
			if(wep_type == 3) {
				if(tier == 1) { return ItemGenerators.StaffGenorator(Material.WOOD_HOE, false, null); }
				if(tier == 2) { return ItemGenerators.StaffGenorator(Material.STONE_HOE, false, null); }
				if(tier == 3) { return ItemGenerators.StaffGenorator(Material.IRON_HOE, false, null); }
				if(tier == 4) { return ItemGenerators.StaffGenorator(Material.DIAMOND_HOE, false, null); }
				if(tier == 5) { return ItemGenerators.StaffGenorator(Material.GOLD_HOE, false, null); }
			}
		} else if(no_bow == false && no_weapon == false) {
			int wep_type = new Random().nextInt(5);
			if(wep_type == 0) {
				if(tier == 1) { return ItemGenerators.SwordGenorator(Material.WOOD_SWORD, false, null); }
				if(tier == 2) { return ItemGenerators.SwordGenorator(Material.STONE_SWORD, false, null); }
				if(tier == 3) { return ItemGenerators.SwordGenorator(Material.IRON_SWORD, false, null); }
				if(tier == 4) { return ItemGenerators.SwordGenorator(Material.DIAMOND_SWORD, false, null); }
				if(tier == 5) { return ItemGenerators.SwordGenorator(Material.GOLD_SWORD, false, null); }
			}
			
			if(wep_type == 1) {
				if(tier == 1) { return ItemGenerators.AxeGenorator(Material.WOOD_AXE, false, null); }
				if(tier == 2) { return ItemGenerators.AxeGenorator(Material.STONE_AXE, false, null); }
				if(tier == 3) { return ItemGenerators.AxeGenorator(Material.IRON_AXE, false, null); }
				if(tier == 4) { return ItemGenerators.AxeGenorator(Material.DIAMOND_AXE, false, null); }
				if(tier == 5) { return ItemGenerators.AxeGenorator(Material.GOLD_AXE, false, null); }
			}
			
			if(wep_type == 2) {
				if(tier == 1) { return ItemGenerators.BowGenorator(1, false, null); }
				if(tier == 2) { return ItemGenerators.BowGenorator(2, false, null); }
				if(tier == 3) { return ItemGenerators.BowGenorator(3, false, null); }
				if(tier == 4) { return ItemGenerators.BowGenorator(4, false, null); }
				if(tier == 5) { return ItemGenerators.BowGenorator(5, false, null); }
			}
			
			if(wep_type == 3) {
				if(tier == 1) { return ItemGenerators.PolearmGenorator(Material.WOOD_SPADE, false, null); }
				if(tier == 2) { return ItemGenerators.PolearmGenorator(Material.STONE_SPADE, false, null); }
				if(tier == 3) { return ItemGenerators.PolearmGenorator(Material.IRON_SPADE, false, null); }
				if(tier == 4) { return ItemGenerators.PolearmGenorator(Material.DIAMOND_SPADE, false, null); }
				if(tier == 5) { return ItemGenerators.PolearmGenorator(Material.GOLD_SPADE, false, null); }
			}
			
			if(wep_type == 4) {
				if(tier == 1) { return ItemGenerators.StaffGenorator(Material.WOOD_HOE, false, null); }
				if(tier == 2) { return ItemGenerators.StaffGenorator(Material.STONE_HOE, false, null); }
				if(tier == 3) { return ItemGenerators.StaffGenorator(Material.IRON_HOE, false, null); }
				if(tier == 4) { return ItemGenerators.StaffGenorator(Material.DIAMOND_HOE, false, null); }
				if(tier == 5) { return ItemGenerators.StaffGenorator(Material.GOLD_HOE, false, null); }
			}
		}
		return ItemGenerators.SwordGenorator(Material.WOOD_SWORD, false, null);
	}
	
	public static Entity spawnBossMob(Location l, EntityType et, String meta_data, String custom_name) {
		int mob_t = 0;
		Entity e = null;
		if(et == EntityType.WOLF){
		    //Main.d("SPAWNED A CUSTOM WOLF!");
		   /* net.minecraft.server.v1_7_R2.World ws = ((CraftWorld) l.getWorld()).getHandle();
            CustomWolf wolf = new CustomWolf(ws);
            wolf.setLocation(l.getX(), l.getY(), l.getZ(), l.getYaw(), l.getPitch());
            ws.addEntity(wolf);
            //Custom wolf to attack peoples
           // Main.d("LOCATION:" + wolf.getBukkitEntity().getLocation()); 
            e = wolf.getBukkitEntity();*/
		    e = l.getWorld().spawnEntity(l, et);
		}else{
		    e = l.getWorld().spawnEntity(l, et);
		}
		EntityLiving ent = ((CraftLivingEntity) e).getHandle();
		double hp_mult = 1D;
		double dmg_mult = 1D;
		custom_name = custom_name.replaceAll("_", "");
		
		if((meta_data.equalsIgnoreCase("wither") && e instanceof CraftSkeleton)) {
			// Make it a wither skeleton.
			((CraftSkeleton) e).getHandle().setSkeletonType(1);
		}
		
		if(meta_data.equalsIgnoreCase("bandit")) {
			int bandit_type = new Random().nextInt(3);
			String skin_name = "";
			
			if(bandit_type == 0) {
				skin_name = "hway234";
			}
			if(bandit_type == 1) {
				skin_name = "Xmattpt";
			}
			if(bandit_type == 2) {
				skin_name = "TheNextPaladin"; //niv330
			}
			ent.setEquipment(4, CraftItemStack.asNMSCopy(getHead(skin_name)));
			
			if(custom_name.equalsIgnoreCase("Mayel The Cruel") && e instanceof CraftSkeleton) {
				((CraftSkeleton) e).getHandle().setSkeletonType(1);
			}
		}
		
		ItemStack weapon = null, boots = null, legs = null, chest = null, helmet = null;
		List<ItemStack> gear_list = new ArrayList<ItemStack>();
		
		if(custom_name.equalsIgnoreCase("The Infernal Abyss")) {
			// TODO: Custom armor set.
		    hp_mult = 4D;
		    dmg_mult = 1.3D;
		    mob_t = 4;
			chest = ItemGenerators.customGenerator("infernalchest");
			legs = ItemGenerators.customGenerator("infernallegging");
			helmet = ItemGenerators.customGenerator("infernalhelmet");
			boots = ItemGenerators.customGenerator("infernalboot");
			weapon = ItemGenerators.customGenerator("infernalstaff");
			
			gear_list.add(weapon);
			gear_list.add(boots);
			gear_list.add(legs);
			gear_list.add(chest);
			gear_list.add(helmet);
			
			weapon.addUnsafeEnchantment(Enchantment.KNOCKBACK, 2);
			
			if(boots != null) {
				boots.addUnsafeEnchantment(EnchantMechanics.getCustomEnchant(), 1);
			}
			if(helmet != null) {
				helmet.addUnsafeEnchantment(EnchantMechanics.getCustomEnchant(), 1);
			}
			if(chest != null) {
				chest.addUnsafeEnchantment(EnchantMechanics.getCustomEnchant(), 1);
			}
			if(legs != null) {
				legs.addUnsafeEnchantment(EnchantMechanics.getCustomEnchant(), 1);
			}
			
			ent.setEquipment(0, CraftItemStack.asNMSCopy(weapon));
			ent.setEquipment(1, CraftItemStack.asNMSCopy(boots));
			ent.setEquipment(2, CraftItemStack.asNMSCopy(legs));
			ent.setEquipment(3, CraftItemStack.asNMSCopy(chest));
			ent.setEquipment(4, CraftItemStack.asNMSCopy(helmet));
			
			LivingEntity le = (LivingEntity) e;
			le.setFireTicks(Integer.MAX_VALUE); // Always burninggggg.
			le.setCustomName(ChatColor.GOLD.toString() + ChatColor.UNDERLINE.toString() + custom_name);
			le.setCustomNameVisible(true);
			le.setMetadata("mobname", new FixedMetadataValue(Main.plugin, ChatColor.GOLD.toString() + ChatColor.UNDERLINE.toString() + custom_name));
			le.setMetadata("boss_type", new FixedMetadataValue(Main.plugin, "fire_demon"));
		}
		
		if(custom_name.equalsIgnoreCase("Mayel The Cruel")) {
		    hp_mult = 6D;
		    dmg_mult = 1.3D;
		    mob_t = 1;
			chest = ItemGenerators.customGenerator("mayelchest");
			legs = ItemGenerators.customGenerator("mayelpants");
			//helmet = ItemGenerators.customGenerator("mayelhelmet"); Needs skin head
			boots = ItemGenerators.customGenerator("mayelboot");
			weapon = ItemGenerators.customGenerator("mayelbow");
			
			gear_list.add(weapon);
			gear_list.add(boots);
			gear_list.add(legs);
			gear_list.add(chest);
			//gear_list.add(helmet);
			
			weapon.addUnsafeEnchantment(Enchantment.KNOCKBACK, 2);
			
			if(boots != null) {
				boots.addUnsafeEnchantment(EnchantMechanics.getCustomEnchant(), 1);
			}
			if(helmet != null) {
				helmet.addUnsafeEnchantment(EnchantMechanics.getCustomEnchant(), 1);
			}
			if(chest != null) {
				chest.addUnsafeEnchantment(EnchantMechanics.getCustomEnchant(), 1);
			}
			if(legs != null) {
				legs.addUnsafeEnchantment(EnchantMechanics.getCustomEnchant(), 1);
			}
			
			ent.setEquipment(0, CraftItemStack.asNMSCopy(weapon));
			ent.setEquipment(1, CraftItemStack.asNMSCopy(boots));
			ent.setEquipment(2, CraftItemStack.asNMSCopy(legs));
			ent.setEquipment(3, CraftItemStack.asNMSCopy(chest));
			if(helmet != null) {
				// Will be null if we are using bandit head, silly.
				ent.setEquipment(4, CraftItemStack.asNMSCopy(helmet));
			}
			
			LivingEntity le = (LivingEntity) e;
			le.setCustomName(ChatColor.GOLD.toString() + ChatColor.UNDERLINE.toString() + custom_name);
			le.setCustomNameVisible(true);
			le.setMetadata("mobname", new FixedMetadataValue(Main.plugin, ChatColor.GOLD.toString() + ChatColor.UNDERLINE.toString() + custom_name));
			le.setMetadata("boss_type", new FixedMetadataValue(Main.plugin, "bandit_leader"));
		}
		
		if(custom_name.equalsIgnoreCase("Mad Bandit Pyromancer")) {
			// TODO: Custom armor set.
		    mob_t = 1;
		    dmg_mult = 2.5D;
			hp_mult = 6;
			boots = ItemGenerators.BootGenerator(2, false, null);
			legs = ItemGenerators.LeggingsGenerator(1, false, null);
			chest = ItemGenerators.ChestPlateGenerator(1, false, null);
			weapon = ItemGenerators.StaffGenorator(Material.STONE_HOE, false, null);
			
			gear_list.add(weapon);
			gear_list.add(boots);
			gear_list.add(legs);
			gear_list.add(chest);
			
			weapon.addUnsafeEnchantment(Enchantment.KNOCKBACK, 2);
			
			if(boots != null) {
				boots.addUnsafeEnchantment(EnchantMechanics.getCustomEnchant(), 1);
			}
			if(helmet != null) {
				helmet.addUnsafeEnchantment(EnchantMechanics.getCustomEnchant(), 1);
			}
			if(chest != null) {
				chest.addUnsafeEnchantment(EnchantMechanics.getCustomEnchant(), 1);
			}
			if(legs != null) {
				legs.addUnsafeEnchantment(EnchantMechanics.getCustomEnchant(), 1);
			}
			
			ent.setEquipment(0, CraftItemStack.asNMSCopy(weapon));
			ent.setEquipment(1, CraftItemStack.asNMSCopy(boots));
			ent.setEquipment(2, CraftItemStack.asNMSCopy(legs));
			ent.setEquipment(3, CraftItemStack.asNMSCopy(chest));
			
			LivingEntity le = (LivingEntity) e;
			le.setCustomName(ChatColor.GOLD.toString() + ChatColor.UNDERLINE.toString() + custom_name);
			le.setCustomNameVisible(true);
			le.setMetadata("mobname", new FixedMetadataValue(Main.plugin, ChatColor.GOLD.toString() + ChatColor.UNDERLINE.toString() + custom_name));
			le.setMetadata("boss_type", new FixedMetadataValue(Main.plugin, "tnt_bandit"));
		}
		if(custom_name.equalsIgnoreCase("Diner of Bones")){
		    mob_t = 1;
            dmg_mult = 2.5D;
            hp_mult = 6;
            boots = ItemGenerators.BootGenerator(4, false, null);
            legs = ItemGenerators.LeggingsGenerator(4, false, null);
            chest = ItemGenerators.ChestPlateGenerator(4, false, null);
            helmet = ItemGenerators.HelmetGenerator(4, false, null);
            weapon = ItemGenerators.AxeGenorator(Material.DIAMOND_AXE, false, null);
            
            gear_list.add(weapon);
            gear_list.add(boots);
            gear_list.add(legs);
            gear_list.add(chest);
            
            ent.setEquipment(0, CraftItemStack.asNMSCopy(weapon));
            ent.setEquipment(1, CraftItemStack.asNMSCopy(boots));
            ent.setEquipment(2, CraftItemStack.asNMSCopy(legs));
            ent.setEquipment(3, CraftItemStack.asNMSCopy(chest));
            
            LivingEntity le = (LivingEntity) e;
            le.setCustomName(ChatColor.GOLD.toString() + ChatColor.UNDERLINE.toString() + custom_name);
            le.setCustomNameVisible(true);
            le.setMetadata("mobname", new FixedMetadataValue(Main.plugin, ChatColor.GOLD.toString() + ChatColor.UNDERLINE.toString() + custom_name));
            le.setMetadata("boss_type", new FixedMetadataValue(Main.plugin, "wolf"));
            BossMechanics.boss_map.put(le, "aceron_wolf");
		}
		if(custom_name.equalsIgnoreCase("Wicked Gatekeeper")){
		   //TODO: WORK
		}
		if(custom_name.equalsIgnoreCase("Aceron the Wicked")){
		    hp_mult = 6D;
		    mob_t = 4;
		    dmg_mult = 2.5D;
		    boots = ItemGenerators.customGenerator("aceronboots");
		    legs = ItemGenerators.customGenerator("aceronlegs");
		    chest = ItemGenerators.customGenerator("aceronplate");
		    helmet = ItemGenerators.customGenerator("aceronhelms");
		    weapon = ItemGenerators.customGenerator("aceronsword");
		    
		    //Add the item to the gearlist
		    gear_list.add(weapon);
            gear_list.add(boots);
            gear_list.add(legs);
            gear_list.add(chest);
            gear_list.add(helmet);
            
            weapon.addUnsafeEnchantment(Enchantment.KNOCKBACK, 2);
            
            if(boots != null) {
                boots.addUnsafeEnchantment(EnchantMechanics.getCustomEnchant(), 1);
            }
            if(helmet != null) {
                helmet.addUnsafeEnchantment(EnchantMechanics.getCustomEnchant(), 1);
            }
            if(chest != null) {
                chest.addUnsafeEnchantment(EnchantMechanics.getCustomEnchant(), 1);
            }
            if(legs != null) {
                legs.addUnsafeEnchantment(EnchantMechanics.getCustomEnchant(), 1);
            }
            
            ent.setEquipment(0, CraftItemStack.asNMSCopy(weapon));
            ent.setEquipment(1, CraftItemStack.asNMSCopy(boots));
            ent.setEquipment(2, CraftItemStack.asNMSCopy(legs));
            ent.setEquipment(3, CraftItemStack.asNMSCopy(chest));
            ent.setEquipment(4, CraftItemStack.asNMSCopy(helmet));
            
            LivingEntity le = (LivingEntity) e;
            le.setCustomName(ChatColor.GOLD.toString() + ChatColor.UNDERLINE.toString() + custom_name);
            le.setCustomNameVisible(true);
            le.setMetadata("mobname", new FixedMetadataValue(Main.plugin, ChatColor.GOLD.toString() + ChatColor.UNDERLINE.toString() + custom_name));
            le.setMetadata("boss_type", new FixedMetadataValue(Main.plugin, "aceron"));
            //Atleast a 8 second cooldown on the first jump
            BossMechanics.last_jump.put(le, System.currentTimeMillis() +  (10 * 8));
		}
		if(custom_name.equalsIgnoreCase("Burick The Fanatic")) {
		    hp_mult = 6D;
		    dmg_mult = 2.5D;
		    mob_t= 3;
			boots = ItemGenerators.customGenerator("up_boots");
			legs = ItemGenerators.customGenerator("up_legs");
			chest = ItemGenerators.customGenerator("up_chest");
			helmet = ItemGenerators.customGenerator("up_helmet");
			weapon = ItemGenerators.customGenerator("up_axe");
			
			gear_list.add(weapon);
			gear_list.add(boots);
			gear_list.add(legs);
			gear_list.add(chest);
			gear_list.add(helmet);
			
			weapon.addUnsafeEnchantment(Enchantment.KNOCKBACK, 2);
			
			if(boots != null) {
				boots.addUnsafeEnchantment(EnchantMechanics.getCustomEnchant(), 1);
			}
			if(helmet != null) {
				helmet.addUnsafeEnchantment(EnchantMechanics.getCustomEnchant(), 1);
			}
			if(chest != null) {
				chest.addUnsafeEnchantment(EnchantMechanics.getCustomEnchant(), 1);
			}
			if(legs != null) {
				legs.addUnsafeEnchantment(EnchantMechanics.getCustomEnchant(), 1);
			}
			
			ent.setEquipment(0, CraftItemStack.asNMSCopy(weapon));
			ent.setEquipment(1, CraftItemStack.asNMSCopy(boots));
			ent.setEquipment(2, CraftItemStack.asNMSCopy(legs));
			ent.setEquipment(3, CraftItemStack.asNMSCopy(chest));
			ent.setEquipment(4, CraftItemStack.asNMSCopy(helmet));
			
			LivingEntity le = (LivingEntity) e;
			le.setCustomName(ChatColor.GOLD.toString() + ChatColor.UNDERLINE.toString() + custom_name);
			le.setCustomNameVisible(true);
			le.setMetadata("mobname", new FixedMetadataValue(Main.plugin, ChatColor.GOLD.toString() + ChatColor.UNDERLINE.toString() + custom_name));
			le.setMetadata("boss_type", new FixedMetadataValue(Main.plugin, "unholy_priest"));
		}
		
		double total_hp = 1;
		
		for(ItemStack i : gear_list) {
			if(i == null || i.getType() == Material.AIR || i == weapon) {
				continue;
			}
			total_hp += HealthMechanics.getHealthVal(i);
		}
		
		int total_armor = 0;
		
		for(ItemStack i : gear_list) {
			if(i == null || i.getType() == Material.AIR || i == weapon) {
				continue;
			}
			if(ItemMechanics.getArmorVal(i, false) == null) {
				continue;
			}
			List<Integer> armor_vals = new ArrayList<Integer>(ItemMechanics.getArmorVal(i, true));
			int median = armor_vals.get(0) + (armor_vals.get(1) - armor_vals.get(0));
			total_armor += median;
		}
		
		total_armor = (int) ((double) (total_armor * 2.0D));
		
		double total_armor_dmg = 0;
		
		for(ItemStack i : gear_list) {
			if(i == null || i.getType() == Material.AIR || i == weapon) {
				continue;
			}
			if(ItemMechanics.getDmgVal(i, false) == null) {
				continue;
			}
			List<Integer> armor_vals = new ArrayList<Integer>(ItemMechanics.getDmgVal(i, true));
			int median = armor_vals.get(0) + (armor_vals.get(1) - armor_vals.get(0));
			total_armor_dmg += median;
		}
		
		total_armor_dmg = total_armor_dmg / 100.0D;
		
		List<Integer> tdmg_range = ItemMechanics.getDmgRangeOfWeapon(weapon);
		/*NBTTagList description = CraftItemStack.asNMSCopy(weapon).getTag().getCompound("display").getList("Lore", 9);
		String raw_dmg_data = description.get(0).toString();
		raw_dmg_data = raw_dmg_data.replaceAll(ChatColor.RED.toString() + "DMG: ", "");
		raw_dmg_data = raw_dmg_data.replaceAll(" ", "");

		String[] dmg_data = raw_dmg_data.split("-");*/
		
		double min_dmg = tdmg_range.get(0);
		double max_dmg = tdmg_range.get(1);
		
		if(min_dmg < 1) {
			min_dmg = 1;
		}
		
		if(max_dmg < 1) {
			max_dmg = 1;
		}
		
		List<Integer> dmg_range = new ArrayList<Integer>();
		min_dmg += ((double) min_dmg * (double) total_armor_dmg);
		max_dmg += ((double) max_dmg * (double) total_armor_dmg);
		min_dmg = ((double) min_dmg * dmg_mult);
		max_dmg = ((double) max_dmg * dmg_mult);
		dmg_range.add((int) Math.round(min_dmg));
		dmg_range.add((int) Math.round(max_dmg));
		
		total_hp = (total_hp * hp_mult);
		
		if(custom_name.contains("Mad Bandit Pyromancer")) {
			total_hp = total_hp * 2.00D;
		}
		
		BossMechanics.boss_event_log.put(e, "");
		int mob_l = 1;
		int tier = mob_t;
        if(tier == 1){
            mob_l = 1;
        }else if(tier == 2){
            mob_l = 20;
        }else if(tier == 3){
            mob_l = 40;
        }else if(tier == 4){
            mob_l = 60;
        }else if(tier == 5){
            mob_l = 80;
        }
        if(ItemMechanics.isSword(weapon)){
            mob_l += new Random().nextInt(20);
        }else if(ItemMechanics.isAxe(weapon)){
            mob_l += new Random().nextInt(20);
        }else if(ItemMechanics.isStaff(weapon)){
            mob_l += new Random().nextInt(15);
        }else if(ItemMechanics.isPolearm(weapon)){
            mob_l += new Random().nextInt(10);
        }else if(ItemMechanics.isBow(weapon)){
            mob_l +=  new Random().nextInt(10);
        }
        
        mob_level.put(e, mob_l);
		max_mob_health.put(e, (int) total_hp);
		mob_health.put(e, (int) total_hp);
		mob_damage.put(e, dmg_range);
		mob_armor.put(e, total_armor);
		mob_tier.put(e, -1); // -1 = Boss!
		mob_last_hurt.put(e, System.currentTimeMillis());
		mob_last_hit.put(e, System.currentTimeMillis());
		
		return e;
	}
	
	@SuppressWarnings("deprecation")
	public static Entity spawnTierMob(Location l, EntityType et, int tier, int mob_num, Location mob_spawner_loc, boolean elite, String meta_data, String custom_name, boolean return_entity, int level) {
		
		Entity e = null;
		
		if(custom_name != null && custom_name.equalsIgnoreCase("Mad_Bandit_Pyromancer")) { return spawnBossMob(l, et, "bandit", "Mad Bandit Pyromancer"); }
		if(custom_name != null && custom_name.equalsIgnoreCase("Wicked_Gatekeeper")) {return spawnBossMob(l, et, "goblin", "Wicked Gatekeeper"); }
		if(et == EntityType.PIG_ZOMBIE) {
			et = EntityType.SKELETON;
			meta_data = "";
		}
		
		if(et == EntityType.SLIME) {
			int new_et = new Random().nextInt(3);
			if(new_et == 0) {
				et = EntityType.PIG;
			}
			if(new_et == 1) {
				et = EntityType.COW;
			}
			if(new_et == 2) {
				et = EntityType.CHICKEN;
			}
			if(new_et == 3) {
				et = EntityType.SHEEP;
			}
		}
		
		if(et == EntityType.PIG || et == EntityType.COW || et == EntityType.CHICKEN || et == EntityType.SHEEP || et == EntityType.MUSHROOM_COW) {
			e = l.getWorld().spawnEntity(l, et);
			Animals a = (Animals) e;
			int age = new Random().nextInt(2);
			if(age == 0) {
				a.setBaby();
			}
			if(age == 1) {
				a.setAdult();
			}
			
			a.setBreed(false);
			
			mob_health.put(e, 1);
			
			if(mob_num > -1) {
				mob_spawn_ownership.put(e, mob_spawner_loc.getX() + "," + mob_spawner_loc.getY() + "," + mob_spawner_loc.getZ() + ":" + mob_num);
			}
			
			LivingEntity le = (LivingEntity) e;
			le.setMetadata("mobname", new FixedMetadataValue(Main.plugin, et.name().substring(0, 1).toUpperCase() + et.name().substring(1, et.name().length()).toLowerCase()));
			return e;
		}
		
		if(et == EntityType.BAT) {
			e = l.getWorld().spawnEntity(l, et);
			mob_health.put(e, 1);
			
			if(mob_num > -1) {
				mob_spawn_ownership.put(e, mob_spawner_loc.getX() + "," + mob_spawner_loc.getY() + "," + mob_spawner_loc.getZ() + ":" + mob_num);
			}
			
			return e;
		}
		
		CopyOnWriteArrayList<ItemStack> gear_list = new CopyOnWriteArrayList<ItemStack>(Arrays.asList(new ItemStack(Material.AIR), new ItemStack(Material.AIR), new ItemStack(Material.AIR), new ItemStack(Material.AIR), new ItemStack(Material.AIR)));
		
		int gear_check = new Random().nextInt(3) + 1; // 1, 2, 3, 4
		net.minecraft.server.v1_7_R2.ItemStack weapon = null;
		ItemStack is_weapon = null;
		
		if(et == EntityType.WOLF || et == EntityType.IRON_GOLEM || et == EntityType.ENDERMAN || et == EntityType.BLAZE || et == EntityType.SILVERFISH || et == EntityType.WITCH || et == EntityType.MAGMA_CUBE || et == EntityType.SPIDER || et == EntityType.CAVE_SPIDER) {
			is_weapon = spawnRandomMeleeWeapon(tier, false, true);
		} else if(et == EntityType.PIG_ZOMBIE) {
			is_weapon = spawnRandomMeleeWeapon(tier, true, false);
		} else {
			is_weapon = spawnRandomMeleeWeapon(tier, false, false);
		}
		if(elite == true) {
			is_weapon.addUnsafeEnchantment(Enchantment.KNOCKBACK, 2);
		}
		weapon = CraftItemStack.asNMSCopy(is_weapon);
		
		//ent.setEquipment(0, weapon);
		gear_list.set(0, is_weapon);
		
		if(elite == true) {
			gear_check = 4;
		}
		
		if(tier == 3) {
			int type = new Random().nextInt(2); // 0, 1
			if(type == 0) {
				gear_check = 3;
			}
			if(type == 1) {
				gear_check = 4;
			}
		}
		if(tier >= 4) {
			gear_check = 4;
		}
		
		ItemStack boots = null, legs = null, chest = null, helmet = null;
		Random armor_type = new Random();
		int larmor_type = 0;
		
		while(gear_check > 0) {
			larmor_type = armor_type.nextInt(4) + 1; // 1, 2, 3, 4
			if(larmor_type == 1 && boots == null) {
				boots = ItemGenerators.BootGenerator(tier, false, null);
				if(elite == true) {
					boots.addUnsafeEnchantment(EnchantMechanics.getCustomEnchant(), 1);
				}
				gear_list.set(1, boots);
				gear_check = gear_check - 1;
			}
			if(larmor_type == 2 && legs == null) {
				legs = ItemGenerators.LeggingsGenerator(tier, false, null);
				if(elite == true) {
					legs.addUnsafeEnchantment(EnchantMechanics.getCustomEnchant(), 1);
				}
				gear_list.set(2, legs);
				gear_check = gear_check - 1;
			}
			if(larmor_type == 3 && chest == null) {
				chest = ItemGenerators.ChestPlateGenerator(tier, false, null);
				if(elite == true) {
					chest.addUnsafeEnchantment(EnchantMechanics.getCustomEnchant(), 1);
				}
				gear_list.set(3, chest);
				gear_check = gear_check - 1;
			}
			if(larmor_type == 4 && helmet == null) {
				helmet = ItemGenerators.HelmetGenerator(tier, false, null);
				if(elite == true) {
					helmet.addUnsafeEnchantment(EnchantMechanics.getCustomEnchant(), 1);
				}
				gear_list.set(4, helmet);
				gear_check = gear_check - 1;
			}
			
			continue;
		}
		
		if(hasCustomDrops(ChatColor.stripColor(custom_name))) {
			// We need to check if they have any armor or weapon, if so we'll need to give/equip it to them.
			List<ItemStack> drops = getCustomDrops(null, ChatColor.stripColor(custom_name));
			for(ItemStack is : drops) {
				if(is == null) {
					continue;
				}
				String mat_name = is.getType().name().toLowerCase();
				if(mat_name.contains("helmet") && !ItemMechanics.getArmorData(is).equalsIgnoreCase("no")) {
					helmet = is;
					if(elite == true) {
						helmet.addUnsafeEnchantment(EnchantMechanics.getCustomEnchant(), 1);
					}
					gear_list.set(4, helmet);
				}
				if(mat_name.contains("chestplate") && !ItemMechanics.getArmorData(is).equalsIgnoreCase("no")) {
					chest = is;
					if(elite == true) {
						chest.addUnsafeEnchantment(EnchantMechanics.getCustomEnchant(), 1);
					}
					gear_list.set(4, chest);
				}
				if(mat_name.contains("leggings") && !ItemMechanics.getArmorData(is).equalsIgnoreCase("no")) {
					legs = is;
					if(elite == true) {
						legs.addUnsafeEnchantment(EnchantMechanics.getCustomEnchant(), 1);
					}
					gear_list.set(4, legs);
				}
				if(mat_name.contains("boots") && !ItemMechanics.getArmorData(is).equalsIgnoreCase("no")) {
					boots = is;
					if(elite == true) {
						boots.addUnsafeEnchantment(EnchantMechanics.getCustomEnchant(), 1);
					}
					gear_list.set(4, boots);
				}
				
				if(!ItemMechanics.getDamageData(is).equalsIgnoreCase("no")) {
					is_weapon = is;
					if(elite == true) {
						is_weapon.addUnsafeEnchantment(EnchantMechanics.getCustomEnchant(), 1);
					}
					gear_list.set(0, is_weapon);
				}
				
			}
		}
		
		if(!meta_data.equalsIgnoreCase("")) {
			if(meta_data.equalsIgnoreCase("acolyte")) {
				
				if(helmet != null) {
					gear_list.remove(4);
				}
			}
			if(meta_data.equalsIgnoreCase("tripoli1")) {
				
				if(helmet != null) {
					gear_list.remove(4);
				}
			}
			if(meta_data.equalsIgnoreCase("naga")) {
				
				if(helmet != null) {
					gear_list.remove(4);
				}
			}
			if(meta_data.equalsIgnoreCase("lizardman")) {
				
				if(helmet != null) {
					gear_list.remove(4);
				}
			}
			if(meta_data.equalsIgnoreCase("troll1")) {
				
				if(helmet != null) {
					gear_list.remove(4);
				}
			}
			if(meta_data.equalsIgnoreCase("goblin")) {
				
				if(helmet != null) {
					gear_list.remove(4);
				}
			}
			if(meta_data.equalsIgnoreCase("bandit") || meta_data.equalsIgnoreCase("monk")) {
				
				if(chest == null) { // They don't normally have the torso.
					chest = ItemGenerators.ChestPlateGenerator(tier, false, null);
					if(elite == true) {
						chest.addUnsafeEnchantment(EnchantMechanics.getCustomEnchant(), 1);
					}
					gear_list.set(3, chest);
				}
				
				if(gear_check >= 4) {
					gear_list.remove(4);
				}
			}
		}
		
		double total_hp = 1;
		
		for(ItemStack i : gear_list) {
			if(i == null || i.getType() == Material.AIR || i == is_weapon) {
				continue;
			}
			total_hp += HealthMechanics.getHealthVal(i);
		}
		
		int total_armor = 0;
		
		for(ItemStack i : gear_list) {
			if(i == null || i.getType() == Material.AIR || i == is_weapon) {
				continue;
			}
			if(ItemMechanics.getArmorVal(i, false) == null) {
				continue;
			}
			List<Integer> armor_vals = new ArrayList<Integer>(ItemMechanics.getArmorVal(i, true));
			int median = armor_vals.get(0) + (armor_vals.get(1) - armor_vals.get(0));
			total_armor += median;
		}
		
		double total_armor_dmg = 0;
		
		for(ItemStack i : gear_list) {
			if(i == null || i.getType() == Material.AIR || i == is_weapon) {
				continue;
			}
			if(ItemMechanics.getDmgVal(i, false) == null) {
				continue;
			}
			List<Integer> armor_vals = new ArrayList<Integer>(ItemMechanics.getDmgVal(i, true));
			int median = armor_vals.get(0) + (armor_vals.get(1) - armor_vals.get(0));
			total_armor_dmg += median;
		}
		
		total_armor_dmg = total_armor_dmg / 100.0D;
		
		List<Integer> tdmg_range = ItemMechanics.getDmgRangeOfWeapon(is_weapon);
		/*NBTTagList description = CraftItemStack.asNMSCopy(is_weapon).getTag().getCompound("display").getList("Lore", 9);
		String raw_dmg_data = description.get(0).toString();
		raw_dmg_data = raw_dmg_data.replaceAll(ChatColor.RED.toString() + "DMG: ", "");
		raw_dmg_data = raw_dmg_data.replaceAll(" ", "");

		String[] dmg_data = raw_dmg_data.split("-");*/
		
		double min_dmg = tdmg_range.get(0);
		double max_dmg = tdmg_range.get(1);
		
		if(tier == 1) {
			if(elite == false) {
				min_dmg = (min_dmg * 0.8);
				max_dmg = (max_dmg * 0.8);
				total_hp = (total_hp * 0.40);
			}
			if(elite == true) {
				min_dmg = (min_dmg * 2.50);
				max_dmg = (max_dmg * 2.50);
				total_hp = (total_hp * 1.80);
			}
		}
		if(tier == 2) {
			if(elite == false) {
				min_dmg = (min_dmg * 0.9);
				max_dmg = (max_dmg * 0.9);
				total_hp = (total_hp * 0.9);
			}
			if(elite == true) {
				min_dmg = (min_dmg * 2.50);
				max_dmg = (max_dmg * 2.50);
				total_hp = (total_hp * 2.50);
			}
			
		}
		if(tier == 3) {
			if(elite == false) {
				min_dmg = (min_dmg * 1.20);
				max_dmg = (max_dmg * 1.20);
				total_hp = (total_hp * 1.20);
			}
			if(elite == true) {
				min_dmg = (min_dmg * 3.00);
				max_dmg = (max_dmg * 3.00);
				total_hp = (total_hp * 3.00);
			}
		}
		if(tier == 4) {
			if(elite == false) {
				min_dmg = (min_dmg * 1.4);
				max_dmg = (max_dmg * 1.4);
				total_hp = (total_hp * 1.4);
			}
			if(elite == true) {
				min_dmg = (min_dmg * 5.00);
				max_dmg = (max_dmg * 5.00);
				total_hp = (total_hp * 5.00);
			}
		}
		if(tier == 5) {
			if(elite == false) {
				min_dmg = (min_dmg * 1.50);
				max_dmg = (max_dmg * 1.50);
				total_hp = (total_hp * 1.50);
			}
			if(elite == true) {
				min_dmg = (min_dmg * 7.0);
				max_dmg = (max_dmg * 7.0);
				total_hp = (total_hp * 7.0);
			}
		}
		
		if(min_dmg < 1) {
			min_dmg = 1;
		}
		
		if(max_dmg < 1) {
			max_dmg = 1;
		}
		
		if(custom_name.contains("Mayel")) {
			total_hp = total_hp * 1.50D;
			min_dmg = ((double) min_dmg * 1.50D);
			max_dmg = ((double) max_dmg * 1.50D);
		}
		
		List<Integer> dmg_range = new ArrayList<Integer>();
		min_dmg += ((double) min_dmg * (double) total_armor_dmg);
		max_dmg += ((double) max_dmg * (double) total_armor_dmg);
		dmg_range.add((int) Math.round(min_dmg));
		dmg_range.add((int) Math.round(max_dmg));
		//Spawns the custom zombie if they have a bow
		if(et == EntityType.ZOMBIE && is_weapon != null && is_weapon.getType() == Material.BOW){
		    net.minecraft.server.v1_7_R2.World ws = ((CraftWorld) l.getWorld()).getHandle();
		    ZombieArcher za = new ZombieArcher(ws);
		    za.teleportTo(l, true);
		    ws.addEntity(za);
		    e = za.getBukkitEntity();
		}else if(et == EntityType.IRON_GOLEM) {
			net.minecraft.server.v1_7_R2.World ws = ((CraftWorld) l.getWorld()).getHandle();
			Golem golem = new Golem(ws);
			golem.setLocation(l.getX(), l.getY(), l.getZ(), l.getYaw(), l.getPitch());
			ws.addEntity(golem, SpawnReason.CUSTOM);
			e = golem.getBukkitEntity();
			((LivingEntity) e).getEquipment().setItemInHand(is_weapon);
		} else {
			e = l.getWorld().spawnEntity(l, et);
		}
		LivingEntity le = (LivingEntity) e;
		le.setRemoveWhenFarAway(false);
		EntityLiving ent = ((CraftLivingEntity) e).getHandle();
		if(((meta_data.equalsIgnoreCase("wither") || (elite == true && (meta_data.equalsIgnoreCase("bandit") || meta_data.equalsIgnoreCase("monk")))) && e instanceof CraftSkeleton)) {
			// Make it a wither skeleton.
			((CraftSkeleton) e).getHandle().setSkeletonType(1);
		}
		
		if(meta_data.equalsIgnoreCase("imp")) {
			PigZombie pz = (PigZombie) e;
			pz.setBaby(true);
		}
		
		ent.setEquipment(0, weapon);
		
		if(boots != null) {
			ent.setEquipment(1, CraftItemStack.asNMSCopy(gear_list.get(1)));
		}
		if(legs != null) {
			ent.setEquipment(2, CraftItemStack.asNMSCopy(gear_list.get(2)));
		}
		if(chest != null) {
			ent.setEquipment(3, CraftItemStack.asNMSCopy(gear_list.get(3)));
		}
		if(helmet != null && meta_data.equalsIgnoreCase("")) {
			ent.setEquipment(4, CraftItemStack.asNMSCopy(gear_list.get(4)));
		}
		
		if(!meta_data.equalsIgnoreCase("")) {
			if(meta_data.equalsIgnoreCase("acolyte")) {
				String skin_name = "InfinityWarrior_";
				ent.setEquipment(4, CraftItemStack.asNMSCopy(getHead(skin_name)));
			}
			
			if(meta_data.equalsIgnoreCase("tripoli solider")) {
				int solider_type = 0; //new Random().nextInt(2); // 0, 1
				String skin_name = "";
				
				if(solider_type == 0) {
					skin_name = "Xmattpt";
				}
				
				ent.setEquipment(4, CraftItemStack.asNMSCopy(getHead(skin_name)));
			}
			if(meta_data.equalsIgnoreCase("ocelot")) {
				Ocelot oc = (Ocelot) ent;
				oc.setAdult();
				oc.setCatType(Type.WILD_OCELOT);
			}
			if(meta_data.equalsIgnoreCase("naga")) {
				//int solider_type = new Random().nextInt(2); // 0, 1
				String skin_name = "";
				int solider_type = 1;
				/*if(solider_type == 0) {
					skin_name = "Migrosbudget";
				}*/
				if(solider_type == 1) {
					skin_name = "Das_Doktor";
				}
				
				ent.setEquipment(4, CraftItemStack.asNMSCopy(getHead(skin_name)));
			}
			if(meta_data.equalsIgnoreCase("lizardman")) {
				int solider_type = 0; //new Random().nextInt(2); // 0, 1
				String skin_name = "";
				
				if(solider_type == 0) {
					skin_name = "_Kashi_";
				}
				
				ent.setEquipment(4, CraftItemStack.asNMSCopy(getHead(skin_name)));
			}
			if(meta_data.equalsIgnoreCase("troll")) {
				int troll_type = new Random().nextInt(2);
				String skin_name = "";
				
				if(troll_type == 0) {
					skin_name = "ArcadiaMovies";
				}
				if(troll_type == 1) {
					skin_name = "Malware";
				}
				
				ent.setEquipment(4, CraftItemStack.asNMSCopy(getHead(skin_name)));
			}
			if(meta_data.equalsIgnoreCase("goblin")) {
				int goblin_type = new Random().nextInt(2);
				
				if(goblin_type == 0) {
					ent.setEquipment(4, CraftItemStack.asNMSCopy(getHead("dEr_t0d")));
				}
				if(goblin_type == 1) {
					ent.setEquipment(4, CraftItemStack.asNMSCopy(getHead("niv330")));
				}
				
			}
			if(meta_data.equalsIgnoreCase("bandit")) {
				
				if(chest == null) { // They don't normally have the torso.
					chest = gear_list.get(3);
					ent.setEquipment(3, CraftItemStack.asNMSCopy(chest));
				}
				
				int bandit_type = new Random().nextInt(3);
				String skin_name = "";
				
				if(bandit_type == 0) {
					skin_name = "hway234";
				}
				if(bandit_type == 1) {
					skin_name = "Xmattpt";
				}
				if(bandit_type == 2) {
					skin_name = "TheNextPaladin"; //niv330
				}
				ent.setEquipment(4, CraftItemStack.asNMSCopy(getHead(skin_name)));
			}
			if(meta_data.equalsIgnoreCase("monk")) {
				if(chest == null) { // They don't normally have the torso.
					chest = gear_list.get(3);
					ent.setEquipment(3, CraftItemStack.asNMSCopy(chest));
				}
				
				int bandit_type = 0;
				String skin_name = "";
				
				if(bandit_type == 0) {
					skin_name = "Yhmen";
				}
				
				ent.setEquipment(4, CraftItemStack.asNMSCopy(getHead(skin_name)));
			}
		}
		
		// HALLOWEEN
		/*if(new Random().nextInt(10) == 0){
			ent.setEquipment(4, CraftItemStack.asNMSCopy(new ItemStack(Material.JACK_O_LANTERN, 1)));
		}*/
		
		if(e.getType() == EntityType.ZOMBIE) {
			//EntityZombie ez = (EntityZombie)ent;
			/*if(elite){ TODO
				ez.bi = 0.32F;
			}*/
			//ez.updateGoals(); // This will select the AI for the zombie, fix for bow wielding zombies.
			// TODO Uhm?
		}
		
		if(e.getType() == EntityType.WOLF) {
			Wolf w = (Wolf) e;
			w.setAngry(true);
		}
		
		if(e.getType() == EntityType.MAGMA_CUBE) {
			MagmaCube mc = (MagmaCube) e;
			mc.setSize(3);
		}
		
		if(total_hp <= 1) {
			//log.info("[MM] Total_HP is 1 or bellow, gear size: " + gear_list.size());
			return null;
		}
		
		//e.setMetadata("drid", new FixedMetadataValue(this, 1));
		total_hp = total_hp + 1;
		
		if(InstanceMechanics.isInstance(l.getWorld().getName())) {
			// Instance monster modifiers!
			total_hp = (total_hp * 3.0D);
			dmg_range.set(0, (int) ((double) (dmg_range.get(0) * 1.5D)));
			dmg_range.set(1, (int) ((double) (dmg_range.get(1) * 1.5D)));
			total_armor = (int) (((double) total_armor * 1.5D));
		}
		int mob_l = 1;
		if(tier == 1){
		    mob_l = 1;
		}else if(tier == 2){
		    mob_l = 20;
		}else if(tier == 3){
		    mob_l = 40;
        }else if(tier == 4){
            mob_l = 60;
        }else if(tier == 5){
            mob_l = 80;
        }
		if(level == 1){
		    mob_l += new Random().nextInt(4);
		}else if(level == 2){
		    //40 -> 6 + random = 10 -> 50
		    mob_l += new Random().nextInt(4) + 6;
		}else if(level == 3){
		    mob_l += new Random().nextInt(4) + 11;
		}else if(level == 4){
		    mob_l += new Random().nextInt(4) + 15;
		}
		total_hp += total_hp * ((mob_l * .001)) + new Random().nextInt(mob_l);
		mob_tier.put(e, tier);
		mob_level.put(e, mob_l);
		max_mob_health.put(e, (int) total_hp);
		mob_health.put(e, (int) total_hp);
		mob_damage.put(e, dmg_range);
		mob_armor.put(e, total_armor);
		
		mob_last_hurt.put(e, System.currentTimeMillis());
		mob_last_hit.put(e, System.currentTimeMillis());
		
		for(ItemStack i : gear_list) {
			if(i == null || i.getType() == Material.AIR || i.getType() == Material.SKULL_ITEM) {
				gear_list.remove(i);
			}
		}
		
		mob_loot.put(e, gear_list);
		
		if(mob_num == -1) {
			// Do nothing, this mob is not tracked.
		}
		
		if(mob_num != -1) {
			mob_spawn_ownership.put(e, mob_spawner_loc.getX() + "," + mob_spawner_loc.getY() + "," + mob_spawner_loc.getZ() + ":" + mob_num);
			//log.info("[MonsterMechanics] Inserted ownership data for " + e.toString());
		}
		
		List<Entity> spawned_mobs = new ArrayList<Entity>();
		if(local_spawned_mobs.containsKey(mob_spawner_loc)) {
			spawned_mobs = local_spawned_mobs.get(mob_spawner_loc);
			spawned_mobs.add(e);
			local_spawned_mobs.put(mob_spawner_loc, spawned_mobs);
		} else if(!(local_spawned_mobs.containsKey(mob_spawner_loc))) {
			spawned_mobs.add(e);
			local_spawned_mobs.put(mob_spawner_loc, spawned_mobs);
		}
		
		e.teleport(l);
		
		String mob_name = "";
		if(meta_data != null && meta_data.length() > 1) {
			if(meta_data.contains(" ")) {
				mob_name = meta_data.substring(0, 1).toUpperCase() + meta_data.substring(1, meta_data.indexOf(" ")).toLowerCase() + (meta_data.substring(meta_data.indexOf(" ") + 1, meta_data.indexOf(" ") + 2).toUpperCase() + meta_data.substring(meta_data.indexOf(" ") + 2, meta_data.length()));
			} else {
				mob_name = meta_data.substring(0, 1).toUpperCase() + meta_data.substring(1, meta_data.length()).toLowerCase();
			}
		}
		if(et == EntityType.IRON_GOLEM) {
			mob_name = "Golem";
		}
		if(et == EntityType.SKELETON && meta_data.equalsIgnoreCase("wither")) {
			mob_name = "Chaos Skeleton";
		}
		if(mob_name == "") { // No custom meta_data
			String system_name = (et.getName().substring(0, 1).toUpperCase() + et.getName().substring(1, et.getName().length()).toLowerCase()).replaceAll("_", " ");
			mob_name = system_name;
		}
		
		// Now that the main name is set, we need to set the prefix/suffix based on their tier.
		
		// Ok, now we need to do checks for elemental damage
		String elemental_type = "";
		String elemental_prefix = "";
		int elemental_chance = new Random().nextInt(100);
		
		if(mob_name.equalsIgnoreCase("bandit") || mob_name.equalsIgnoreCase("monk")) { // Poison
			int do_i_elemental = 15;
			if(do_i_elemental >= elemental_chance) {
				// Give them elemental damage!
				elemental_type = "Poison";
				elemental_prefix = ChatColor.DARK_GREEN.toString(); //+ Chatcolor.BOLD.toString()  + "P";
				attachPotionEffect(le, 0x669900);
			}
		}
		if(et == EntityType.IRON_GOLEM) { // Ice
			int do_i_elemental = 30;
			if(do_i_elemental >= elemental_chance) {
				// Give them elemental damage!
				elemental_type = "Ice";
				elemental_prefix = ChatColor.BLUE.toString(); //+ Chatcolor.BOLD.toString()  + "I";
				attachPotionEffect(le, 0x33FFFF);
			}
		}
		if(et == EntityType.MAGMA_CUBE) {
			int do_i_elemental = 15;
			if(do_i_elemental >= elemental_chance) {
				// Give them elemental damage!
				elemental_type = "Fire";
				elemental_prefix = ChatColor.RED.toString(); //+ Chatcolor.BOLD.toString()  + "I";
				attachPotionEffect(le, 0xCC0033);
			}
		}
		if(et == EntityType.BLAZE) {
			int do_i_elemental = 15;
			if(do_i_elemental >= elemental_chance) {
				// Give them elemental damage!
				elemental_type = "Fire";
				elemental_prefix = ChatColor.RED.toString(); //+ Chatcolor.BOLD.toString()  + "I";
				attachPotionEffect(le, 0xCC0033);
			}
		}
		if(et == EntityType.WITCH) {
			int do_i_elemental = 15;
			if(do_i_elemental >= elemental_chance) {
				// Give them elemental damage!
				elemental_type = "Poison";
				elemental_prefix = ChatColor.DARK_GREEN.toString(); //+ Chatcolor.BOLD.toString()  + "I";
				attachPotionEffect(le, 0x669900);
			}
		}
		if(et == EntityType.SILVERFISH) {
			int do_i_elemental = 15;
			if(do_i_elemental >= elemental_chance) {
				// Give them elemental damage!
				elemental_type = "Ice";
				elemental_prefix = ChatColor.BLUE.toString(); //+ Chatcolor.BOLD.toString()  + "I";
				attachPotionEffect(le, 0x33FFFF);
			}
		}
		if(et == EntityType.ENDERMAN) {
			int do_i_elemental = 15;
			if(do_i_elemental >= elemental_chance) {
				// Give them elemental damage!
				elemental_type = "pure";
				elemental_prefix = ChatColor.GOLD.toString(); //+ Chatcolor.BOLD.toString()  + "I";
				attachPotionEffect(le, 0xFFFFFF);
			}
		}
		if(mob_name.equalsIgnoreCase("Acolyte")) {
			int do_i_elemental = 20;
			if(do_i_elemental >= elemental_chance) {
				// Give them elemental damage!
				elemental_type = "Fire";
				elemental_prefix = ChatColor.RED.toString(); //+ Chatcolor.BOLD.toString()  + "I";
				attachPotionEffect(le, 0xCC0033);
			}
		}
		if(mob_name.equalsIgnoreCase("Imp")) {
			int do_i_elemental = 15;
			if(do_i_elemental >= elemental_chance) {
				// Give them elemental damage!
				elemental_type = "Fire";
				elemental_prefix = ChatColor.RED.toString(); //+ Chatcolor.BOLD.toString()  + "I";
				attachPotionEffect(le, 0xCC0033);
			}
		}
		if(mob_name.equalsIgnoreCase("Daemon")) {
			int do_i_elemental = 10;
			if(do_i_elemental >= elemental_chance) {
				// Give them elemental damage!
				elemental_type = "pure";
				elemental_prefix = ChatColor.GOLD.toString(); //+ Chatcolor.BOLD.toString()  + "P";
				attachPotionEffect(le, 0xFFFFFF);
			}
		}
		if(mob_name.equalsIgnoreCase("zombie")) { // Fire
			int do_i_elemental = 10;
			if(do_i_elemental >= elemental_chance) {
				// Give them elemental damage!
				elemental_type = "Fire";
				elemental_prefix = ChatColor.RED.toString(); //+ Chatcolor.BOLD.toString()  + "F";
				attachPotionEffect(le, 0xCC0033);
			}
		}
		if(mob_name.equalsIgnoreCase("skeleton")) { // PURE DMG
			int do_i_elemental = 5;
			if(do_i_elemental >= elemental_chance) {
				// Give them elemental damage!
				elemental_type = "pure";
				elemental_prefix = ChatColor.GOLD.toString(); //+ Chatcolor.BOLD.toString()  + "P";
				attachPotionEffect(le, 0xFFFFFF);
			}
		}
		if(mob_name.equalsIgnoreCase("chaos skeleton")) { // PURE DMG
			int do_i_elemental = 5;
			if(do_i_elemental >= elemental_chance) {
				// Give them elemental damage!
				elemental_type = "pure";
				elemental_prefix = ChatColor.GOLD.toString(); //+ Chatcolor.BOLD.toString()  + "P";
				attachPotionEffect(le, 0xFFFFFF);
			}
		}
		if(mob_name.equalsIgnoreCase("troll")) {
			int do_i_elemental = 20;
			if(do_i_elemental >= elemental_chance) {
				// Give them elemental damage!
				elemental_type = "Poison";
				elemental_prefix = ChatColor.DARK_GREEN.toString(); //+ Chatcolor.BOLD.toString()  + "P";
				attachPotionEffect(le, 0x669900);
			}
		}
		if(mob_name.equalsIgnoreCase("goblin")) {
			int do_i_elemental = 20;
			if(do_i_elemental >= elemental_chance) {
				// Give them elemental damage!
				elemental_type = "Fire";
				elemental_prefix = ChatColor.RED.toString(); //+ Chatcolor.BOLD.toString()  + "F";
				attachPotionEffect(le, 0xCC0033);
			}
		}
		if(mob_name.equalsIgnoreCase("naga")) {
			int do_i_elemental = 25;
			if(do_i_elemental >= elemental_chance) {
				// Give them elemental damage!
				elemental_type = "Ice";
				elemental_prefix = ChatColor.BLUE.toString(); //+ Chatcolor.BOLD.toString()  + "I";
				attachPotionEffect(le, 0x33FFFF);
			}
		}
		if(mob_name.equalsIgnoreCase("tripoli soldier")) {
			int do_i_elemental = 3;
			if(do_i_elemental >= elemental_chance) {
				// Give them elemental damage!
				elemental_type = "pure";
				elemental_prefix = ChatColor.WHITE.toString(); //+ Chatcolor.BOLD.toString()  + "P";
				attachPotionEffect(le, 0xFFFFFF);
			}
		}
		if(mob_name.equalsIgnoreCase("lizardman")) {
			int do_i_elemental = 10;
			if(do_i_elemental >= elemental_chance) {
				// Give them elemental damage!
				elemental_type = "Fire";
				elemental_prefix = ChatColor.RED.toString(); //+ Chatcolor.BOLD.toString()  + "F";
				attachPotionEffect(le, 0xCC0033);
			}
		}
		if(mob_name.equalsIgnoreCase("spider") || et == EntityType.CAVE_SPIDER || mob_name.equalsIgnoreCase("wolf")) {
			int do_i_elemental = 10;
			if(do_i_elemental >= elemental_chance) {
				// Give them elemental damage!
				elemental_type = "Ice";
				elemental_prefix = ChatColor.BLUE.toString(); //+ Chatcolor.BOLD.toString()  + "F";
				attachPotionEffect(le, 0x33FFFF);
			}
		}
		
		if(tier == 1) {
			if(mob_name.equalsIgnoreCase("goblin") || mob_name.equalsIgnoreCase("zombie") || mob_name.equalsIgnoreCase("skeleton") || mob_name.equalsIgnoreCase("chaos skeleton") || mob_name.equalsIgnoreCase("daemon") || mob_name.equalsIgnoreCase("imp")) {
				// Demonic
				if(mob_name.equalsIgnoreCase("goblin") || mob_name.equalsIgnoreCase("imp")) {
					mob_name = "Ugly " + mob_name;
				}
				if(mob_name.equalsIgnoreCase("skeleton") || mob_name.equalsIgnoreCase("chaos skeleton")) {
					mob_name = "Broken " + mob_name;
				} else {
					mob_name = "Rotting " + mob_name;
				}
			}
			
			if(mob_name.equalsIgnoreCase("witch")) {
				mob_name = "Old Hag";
			}
			
			if(mob_name.equalsIgnoreCase("enderman")) {
				mob_name = "Apparation";
			}
			
			if(mob_name.equalsIgnoreCase("magmacube")) {
				mob_name = "Magma Cube";
				mob_name = "Weak " + mob_name;
			}
			
			if(mob_name.equalsIgnoreCase("blaze")) {
				mob_name = "Firey Spirit";
			}
			
			if(et == EntityType.IRON_GOLEM) {
				// Construct
				mob_name = "Stone " + mob_name;
			}
			
			if(mob_name.equalsIgnoreCase("bandit") || mob_name.equalsIgnoreCase("tripoli soldier") || mob_name.equalsIgnoreCase("acolyte")) {
				// Humanoid
				int ran = new Random().nextInt(4); // 0, 1, 2
				if(ran == 0) {
					mob_name = "Lazy " + mob_name;
				}
				if(ran == 1) {
					mob_name = "Old " + mob_name;
				}
				if(ran == 2) {
					mob_name = "Starving " + mob_name;
				}
				if(ran == 3) {
					mob_name = "Clumsy " + mob_name;
				}
			}
			
			if(mob_name.equalsIgnoreCase("monk")) {
				mob_name = "Crimson Acolyte";
			}
			
			if(mob_name.equalsIgnoreCase("naga") || mob_name.equalsIgnoreCase("troll") || mob_name.equalsIgnoreCase("lizardman")) {
				// Misc.
				if(mob_name.equalsIgnoreCase("troll")) {
					mob_name = mob_name + " Peon";
				} else {
					mob_name = "Weak " + mob_name;
				}
			}
			
			if(mob_name.equalsIgnoreCase("spider") || et == EntityType.CAVE_SPIDER || mob_name.equalsIgnoreCase("wolf") || mob_name.equalsIgnoreCase("silverfish")) {
				// Spiders.
				mob_name = "Harmless " + mob_name;
			}
			
		}
		
		if(tier == 2) {
			if(mob_name.equalsIgnoreCase("goblin") || mob_name.equalsIgnoreCase("zombie") || mob_name.equalsIgnoreCase("skeleton") || mob_name.equalsIgnoreCase("chaos skeleton") || mob_name.equalsIgnoreCase("daemon") || mob_name.equalsIgnoreCase("imp")) {
				// Demonic
				if(mob_name.equalsIgnoreCase("goblin") || mob_name.equalsIgnoreCase("imp")) {
					mob_name = "Angry " + mob_name;
				}
				if(mob_name.equalsIgnoreCase("skeleton") || mob_name.equalsIgnoreCase("chaos skeleton")) {
					mob_name = "Cracking " + mob_name;
				}
				if(mob_name.equalsIgnoreCase("zombie")) {
					mob_name = "Savaged " + mob_name;
				} else {
					mob_name = "Wandering " + mob_name;
				}
			}
			
			if(mob_name.equalsIgnoreCase("witch")) {
				mob_name = "Witch";
			}
			
			if(mob_name.equalsIgnoreCase("enderman")) {
				mob_name = "Possessed Spirit";
			}
			
			if(et == EntityType.MAGMA_CUBE) {
				mob_name = "Magma Cube";
				mob_name = "Bubbling " + mob_name;
			}
			
			if(mob_name.equalsIgnoreCase("blaze")) {
				mob_name = "Burning Apparition";
			}
			
			if(et == EntityType.IRON_GOLEM) {
				// Construct
				mob_name = "Ancient Stone " + mob_name;
			}
			
			if(mob_name.equalsIgnoreCase("bandit") || mob_name.equalsIgnoreCase("tripoli soldier") || mob_name.equalsIgnoreCase("acolyte")) {
				// Humanoid
				int ran = new Random().nextInt(2); // 0, 1
				if(ran == 0) {
					mob_name = "Horrible " + mob_name;
				}
				if(ran == 1) {
					mob_name = "Vengeful " + mob_name;
				}
			}
			
			if(mob_name.equalsIgnoreCase("monk")) {
				mob_name = "Crimson Crusader";
			}
			
			if(mob_name.equalsIgnoreCase("naga") || mob_name.equalsIgnoreCase("troll") || mob_name.equalsIgnoreCase("lizardman")) {
				// Misc.
				if(mob_name.equalsIgnoreCase("troll")) {
					if(weapon.getName().contains("bow")) {
						mob_name = "Spear Thrower " + mob_name;
					} else {
						mob_name = mob_name + " Fighter";
					}
				} else {
					mob_name = "Tough " + mob_name;
				}
			}
			
			if(mob_name.equalsIgnoreCase("spider") || et == EntityType.CAVE_SPIDER || mob_name.equalsIgnoreCase("wolf") || mob_name.equalsIgnoreCase("silverfish")) {
				// Spiders.
				mob_name = "Wild " + mob_name;
			}
		}
		
		if(tier == 3) {
			if(mob_name.equalsIgnoreCase("goblin") || mob_name.equalsIgnoreCase("zombie") || mob_name.equalsIgnoreCase("skeleton") || mob_name.equalsIgnoreCase("chaos skeleton") || mob_name.equalsIgnoreCase("daemon") || mob_name.equalsIgnoreCase("imp")) {
				// Demonic
				if(mob_name.equalsIgnoreCase("goblin") || mob_name.equalsIgnoreCase("imp")) {
					mob_name = "Warrior " + mob_name;
				}
				if(mob_name.equalsIgnoreCase("skeleton") || mob_name.equalsIgnoreCase("chaos skeleton")) {
					mob_name = "Demonic " + mob_name;
				} else {
					mob_name = "Greater " + mob_name;
				}
			}
			
			if(mob_name.equalsIgnoreCase("witch")) {
				mob_name = "Enchantress";
			}
			
			if(mob_name.equalsIgnoreCase("enderman")) {
				mob_name = "Specter";
			}
			
			if(et == EntityType.IRON_GOLEM) {
				// Construct
				mob_name = "Ironclad " + mob_name;
			}
			
			if(et == EntityType.MAGMA_CUBE) {
				mob_name = "Magma Cube";
				mob_name = "Unstable " + mob_name;
			}
			
			if(mob_name.equalsIgnoreCase("blaze")) {
				mob_name = "Incendiary Demon";
			}
			
			if(mob_name.equalsIgnoreCase("bandit") || mob_name.equalsIgnoreCase("tripoli soldier") || mob_name.equalsIgnoreCase("acolyte")) {
				// Humanoid
				int ran = new Random().nextInt(4); // 0, 1, 2, 3
				if(ran == 0) {
					mob_name = "Cruel " + mob_name;
				}
				if(ran == 1) {
					mob_name = mob_name + " Warrior";
				}
				if(ran == 2) {
					mob_name = "Mountain " + mob_name;
				}
				if(ran == 3) {
					mob_name = mob_name + " Champion";
				}
			}
			
			if(mob_name.equalsIgnoreCase("monk")) {
				mob_name = "Crimson Monk";
			}
			
			if(mob_name.equalsIgnoreCase("naga") || mob_name.equalsIgnoreCase("troll") || mob_name.equalsIgnoreCase("lizardman")) {
				// Misc.
				if(mob_name.equalsIgnoreCase("troll")) {
					mob_name = mob_name + " Warrior";
				} else {
					mob_name = "Giant " + mob_name;
				}
			}
			
			if(mob_name.equalsIgnoreCase("spider") || et == EntityType.CAVE_SPIDER || mob_name.equalsIgnoreCase("wolf") || mob_name.equalsIgnoreCase("silverfish")) {
				// Spiders.
				mob_name = "Fierce " + mob_name;
			}
		}
		
		if(tier == 4) {
			if(mob_name.equalsIgnoreCase("goblin") || mob_name.equalsIgnoreCase("zombie") || mob_name.equalsIgnoreCase("skeleton") || mob_name.equalsIgnoreCase("chaos skeleton") || mob_name.equalsIgnoreCase("daemon") || mob_name.equalsIgnoreCase("imp")) {
				// Demonic
				if(mob_name.equalsIgnoreCase("goblin") || mob_name.equalsIgnoreCase("imp")) {
					mob_name = "Armoured " + mob_name;
				}
				if(mob_name.equalsIgnoreCase("zombie")) {
					mob_name = "Demonic " + mob_name;
				} else {
					mob_name = mob_name + " Guardian";
				}
			}
			
			if(et == EntityType.MAGMA_CUBE) {
				mob_name = "Magma Cube";
				mob_name = "Boiling " + mob_name;
			}
			
			if(mob_name.equalsIgnoreCase("blaze")) {
				mob_name = "Inferno Demon";
			}
			
			if(mob_name.equalsIgnoreCase("witch")) {
				mob_name = "Witch Doctor";
			}
			
			if(mob_name.equalsIgnoreCase("enderman")) {
				mob_name = "Demonic Entity";
			}
			
			if(et == EntityType.IRON_GOLEM) {
				// Construct
				mob_name = "Enchanted Ironclad " + mob_name;
			}
			
			if(mob_name.equalsIgnoreCase("bandit") || mob_name.equalsIgnoreCase("tripoli soldier") || mob_name.equalsIgnoreCase("acolyte")) {
				// Humanoid
				int ran = new Random().nextInt(3); // 0, 1, 2, 3
				if(ran == 0) {
					mob_name = "Merciless " + mob_name;
				}
				if(ran == 1) {
					mob_name = mob_name + " Legend";
				}
				if(ran == 2) {
					mob_name = "Chief " + mob_name;
				}
			}
			
			if(mob_name.equalsIgnoreCase("monk")) {
				mob_name = "Crimson Paladin";
			}
			
			if(mob_name.equalsIgnoreCase("naga") || mob_name.equalsIgnoreCase("troll") || mob_name.equalsIgnoreCase("lizardman")) {
				// Misc.
				if(mob_name.equalsIgnoreCase("troll")) {
					mob_name = mob_name + " Shaman";
				} else {
					mob_name = "Gigantic " + mob_name;
				}
			}
			
			if(mob_name.equalsIgnoreCase("spider") || et == EntityType.CAVE_SPIDER || mob_name.equalsIgnoreCase("wolf") || mob_name.equalsIgnoreCase("silverfish")) {
				// Spiders.
				mob_name = "Dangerous " + mob_name;
			}
		}
		
		if(tier == 5) {
			if(mob_name.equalsIgnoreCase("goblin") || mob_name.equalsIgnoreCase("zombie") || mob_name.equalsIgnoreCase("skeleton") || mob_name.equalsIgnoreCase("chaos skeleton") || mob_name.equalsIgnoreCase("daemon") || mob_name.equalsIgnoreCase("imp")) {
				// Demonic
				mob_name = "Infernal " + mob_name;
			}
			
			if(et == EntityType.IRON_GOLEM) {
				// Construct
				mob_name = "Legendary Chaotic " + mob_name;
			}
			
			if(et == EntityType.MAGMA_CUBE) {
				mob_name = "Magma Cube";
				mob_name = "Unstoppable " + mob_name;
			}
			
			if(mob_name.equalsIgnoreCase("blaze")) {
				mob_name = "Source of Fire";
			}
			
			if(mob_name.equalsIgnoreCase("witch")) {
				mob_name = "Queen of Evil";
			}
			
			if(mob_name.equalsIgnoreCase("enderman")) {
				mob_name = "Phantom Revenant";
			}
			
			if(mob_name.equalsIgnoreCase("bandit") || mob_name.equalsIgnoreCase("tripoli soldier") || mob_name.equalsIgnoreCase("acolyte")) {
				// Humanoid
				if(mob_name.equalsIgnoreCase("bandit")) {
					mob_name = "King " + mob_name;
				} else {
					mob_name = "Captain " + mob_name;
				}
			}
			
			if(mob_name.equalsIgnoreCase("monk")) {
				mob_name = "Lord of the Crimson Order";
			}
			
			if(mob_name.equalsIgnoreCase("naga") || mob_name.equalsIgnoreCase("troll") || mob_name.equalsIgnoreCase("lizardman")) {
				// Misc.
				if(mob_name.equalsIgnoreCase("troll")) {
					mob_name = mob_name + " Chieftain";
				} else {
					mob_name = "Mythical " + mob_name;
				}
			}
			
			if(mob_name.equalsIgnoreCase("spider") || et == EntityType.CAVE_SPIDER || mob_name.equalsIgnoreCase("wolf") || mob_name.equalsIgnoreCase("silverfish")) {
				// Spiders.
				mob_name = "Lethal " + mob_name;
			}
		}
		
		if(elemental_type.length() > 0) {
			// elemental_prefix = color
			
			is_weapon.addUnsafeEnchantment(Enchantment.LOOT_BONUS_MOBS, 1);
			ent.setEquipment(0, CraftItemStack.asNMSCopy(is_weapon));
			
			mob_name = elemental_prefix + mob_name;
			String formal_elemental_type = elemental_type;
			if(formal_elemental_type.equalsIgnoreCase("pure")) {
				formal_elemental_type = "Holy";
				mob_name = elemental_prefix + formal_elemental_type + " " + mob_name;
			} else {
				try {
					mob_name = mob_name.substring(0, mob_name.lastIndexOf(" ")) + " " + formal_elemental_type + mob_name.substring(mob_name.lastIndexOf(" "), mob_name.length());
				} catch(StringIndexOutOfBoundsException err) {
					log.info("[MonsterMechanics] Error parsing mob name: " + mob_name);
					mob_name = elemental_prefix + mob_name;
					if(formal_elemental_type.equalsIgnoreCase("pure")) {
						formal_elemental_type = "Holy";
						mob_name = elemental_prefix + formal_elemental_type + " " + mob_name;
					}
				}
			}
			le.setMetadata("etype", new FixedMetadataValue(Main.plugin, elemental_type));
		}
		
		if(custom_name != "") {
			mob_name = custom_name.replaceAll("_", " ");
			
			mob_name = mob_name.replaceAll("&0", ChatColor.BLACK.toString());
			mob_name = mob_name.replaceAll("&1", ChatColor.DARK_BLUE.toString());
			mob_name = mob_name.replaceAll("&2", ChatColor.DARK_GREEN.toString());
			mob_name = mob_name.replaceAll("&3", ChatColor.DARK_AQUA.toString());
			mob_name = mob_name.replaceAll("&4", ChatColor.DARK_RED.toString());
			mob_name = mob_name.replaceAll("&5", ChatColor.DARK_PURPLE.toString());
			mob_name = mob_name.replaceAll("&6", ChatColor.GOLD.toString());
			mob_name = mob_name.replaceAll("&7", ChatColor.GRAY.toString());
			mob_name = mob_name.replaceAll("&8", ChatColor.DARK_GRAY.toString());
			mob_name = mob_name.replaceAll("&9", ChatColor.BLUE.toString());
			mob_name = mob_name.replaceAll("&a", ChatColor.GREEN.toString());
			mob_name = mob_name.replaceAll("&b", ChatColor.AQUA.toString());
			mob_name = mob_name.replaceAll("&c", ChatColor.RED.toString());
			mob_name = mob_name.replaceAll("&d", ChatColor.LIGHT_PURPLE.toString());
			mob_name = mob_name.replaceAll("&e", ChatColor.YELLOW.toString());
			mob_name = mob_name.replaceAll("&f", ChatColor.WHITE.toString());
			
			mob_name = mob_name.replaceAll("&u", ChatColor.UNDERLINE.toString());
			mob_name = mob_name.replaceAll("&s", ChatColor.BOLD.toString());
			mob_name = mob_name.replaceAll("&i", ChatColor.ITALIC.toString());
			mob_name = mob_name.replaceAll("&m", ChatColor.MAGIC.toString());
		}
		
		if(elite) {
			le.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));
		}
		
		if(tier == 4 || tier == 5) {
			int speed_chance = tier * 15;
			int do_i_speed = new Random().nextInt(100);
			
			if(speed_chance > do_i_speed) {
				le.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0));
			}
		}
		
		ChatColor tier_color = ProfessionMechanics.getTierColor(tier);
		String mob_name_meta = tier_color + mob_name;
		
		if(elite) {
			mob_name_meta = ChatColor.BLACK + "" + ChatColor.BOLD + "" + ChatColor.stripColor(mob_name);
		}
		
		le.setCustomName(mob_name_meta);
		le.setCustomNameVisible(true);
		
		if(le.getType() == EntityType.ENDERMAN) {
			enderman_list.add((Enderman) le);
		}
		
		le.setMetadata("mobname", new FixedMetadataValue(Main.plugin, tier_color + mob_name));
		
		int mount_chance = new Random().nextInt(100);
		if(mount_chance <= 10) {
			// Give them a mount!
			EntityType mount_type = null;
			/*if(meta_data.equalsIgnoreCase("bandit") || meta_data.equalsIgnoreCase("tripoli1")){
				mount_type = EntityType.HORSE;
			}
			if(meta_data.equalsIgnoreCase("zombie") || meta_data.equalsIgnoreCase("skeleton") || meta_data.equalsIgnoreCase("wither")){
				mount_type = EntityType.HORSE;
			}*/
			if(meta_data.equalsIgnoreCase("goblin") || meta_data.equalsIgnoreCase("daemon") || meta_data.equalsIgnoreCase("imp")) {
				mount_type = EntityType.SPIDER;
			}
			if(meta_data.equalsIgnoreCase("naga") || meta_data.equalsIgnoreCase("troll1") || meta_data.equalsIgnoreCase("lizardman")) {
				mount_type = EntityType.CAVE_SPIDER;
			}
			
			if(mount_type != null) {
				if(is_weapon.getType() == Material.BOW && mount_type == EntityType.HORSE) {
					Entity mount = le.getWorld().spawnEntity(le.getLocation(), mount_type);
					Horse h = (Horse) mount;
					if(meta_data.equalsIgnoreCase("bandit")) {
						h.setColor(Color.CHESTNUT);
					}
					if(meta_data.equalsIgnoreCase("tripoli1")) {
						h.setColor(Color.GRAY);
					}
					if(meta_data.equalsIgnoreCase("zombie")) {
						h.setVariant(Variant.UNDEAD_HORSE);
					}
					if(meta_data.equalsIgnoreCase("wither") || meta_data.equalsIgnoreCase("skeleton")) {
						h.setVariant(Variant.SKELETON_HORSE);
					}
					if(elite) {
						h.setCarryingChest(true);
					}
				} else if(!(mount_type == EntityType.HORSE)) {
					Entity mount = le.getWorld().spawnEntity(le.getLocation(), mount_type);
					mount.setPassenger(le);
				}
			}
		}
		
		return e;
	}
	public static int getMobLevel(Entity e){
	    if(!mob_level.containsKey(e)){
	        return 1;
	    }
	    return mob_level.get(e);
	}
	public boolean isHostile(EntityType e){
	    if(e == EntityType.SKELETON || e == EntityType.ZOMBIE || e == EntityType.SPIDER || e == EntityType.CAVE_SPIDER || e == EntityType.BLAZE || e == EntityType.WITCH || e == EntityType.WOLF || e == EntityType.ENDERMAN || e == EntityType.IRON_GOLEM || e == EntityType.SILVERFISH || e == EntityType.MAGMA_CUBE){
	        return true;
	    }
	    return false;
	}
	/*@EventHandler(priority = EventPriority.LOWEST)
	public void onGhostEntityHit(EntityDamageByEntityEvent e){
	    if(e.getEntity() instanceof Player)return;
	    if(!(e.getDamager() instanceof Player))return;
	    Entity ent = e.getEntity();
	    Player p = (Player)e.getDamager();
	    if(!mob_health.containsKey(ent) && isHostile(ent.getType())){
	        int total_hp = 10;
	        for(ItemStack is : ((LivingEntity)ent).getEquipment().getArmorContents()){
	            if(is == null || is.getType() == Material.AIR)continue;
	            total_hp += HealthMechanics.getHealthVal(is);
	        }
	        List<Integer> dmg_val = new ArrayList<Integer>(Arrays.asList(1, 5));
	        dmg_val = ItemMechanics.getDmgRangeOfWeapon(((LivingEntity)ent).getEquipment().getItemInHand());
	        mob_health.put(ent, total_hp);
	        ((LivingEntity)ent).setHealth(((LivingEntity)ent).getMaxHealth());
	        max_mob_health.put(ent, total_hp);
	        mob_damage.put(ent, dmg_val);
	        mob_last_hit.put(ent, System.currentTimeMillis());
	        mob_last_hurt.put(ent, System.currentTimeMillis());
	        mob_tier.put(ent, getMobTier(ent));
	        mob_target.put(ent, p.getName());
	       // Main.plugin.getServer().getConsoleSender().sendMessage(ChatColor.RED + "Entity " + ent.getType() + " was a null, so we created a new instance of it!"); 
	        //They are a ghost entity
	    }
	}*/
	public static int calculateMobHP(Entity e) {
		int total_hp = 10;
		
		if(mob_loot.containsKey(e)) {
			for(ItemStack i : mob_loot.get(e)) {
				if(i == null || i.getType() == Material.AIR) {
					continue;
				}
				total_hp += HealthMechanics.getHealthVal(i);
			}
		}
		
		return total_hp;
	}
}
