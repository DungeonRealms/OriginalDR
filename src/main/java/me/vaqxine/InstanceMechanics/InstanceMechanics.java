package me.vaqxine.InstanceMechanics;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import me.vaqxine.Main;
import me.vaqxine.AchievmentMechanics.AchievmentMechanics;
import me.vaqxine.CommunityMechanics.CommunityMechanics;
import me.vaqxine.DuelMechanics.DuelMechanics;
import me.vaqxine.HealthMechanics.HealthMechanics;
import me.vaqxine.Hive.Hive;
import me.vaqxine.Hive.ParticleEffect;
import me.vaqxine.InstanceMechanics.commands.CommandBossTP;
import me.vaqxine.InstanceMechanics.commands.CommandDRLightning;
import me.vaqxine.InstanceMechanics.commands.CommandDRReplaceNear;
import me.vaqxine.InstanceMechanics.commands.CommandDebuffCrystal;
import me.vaqxine.InstanceMechanics.commands.CommandISay;
import me.vaqxine.InstanceMechanics.commands.CommandInstance;
import me.vaqxine.LootMechanics.LootMechanics;
import me.vaqxine.MonsterMechanics.MonsterMechanics;
import me.vaqxine.MountMechanics.MountMechanics;
import me.vaqxine.PartyMechanics.PartyMechanics;
import me.vaqxine.SpawnMechanics.SpawnMechanics;
import me.vaqxine.database.ConnectionPool;
import net.minecraft.server.v1_7_R2.EntityPlayer;
import net.minecraft.server.v1_7_R2.EntityTracker;
import net.minecraft.server.v1_7_R2.EntityTrackerEntry;
import net.minecraft.server.v1_7_R2.PacketPlayOutRespawn;
import net.minecraft.server.v1_7_R2.WorldServer;
import net.minecraft.util.org.apache.commons.io.FileUtils;
import net.minecraft.util.org.apache.commons.io.IOUtils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.WorldCreator;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_7_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_7_R2.entity.CraftPlayer;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class InstanceMechanics implements Listener {
	
	// TODO: No dueling in instances.
	
	static Logger log = Logger.getLogger("Minecraft");
	static InstanceMechanics plugin_instance = null;
	
	File home_dir = null;
	
	public static HashMap<String, List<Integer>> total_timing = new HashMap<String, List<Integer>>();
	// Player Name, All times of an instance template.
	
	public static HashMap<String, Integer> avg_timing = new HashMap<String, Integer>();
	// Player Name, Time of entrance.
	
	public static HashMap<String, Long> instance_timing = new HashMap<String, Long>();
	// Player Name, Time of entrance.
	
	public static HashMap<String, String> player_instance = new HashMap<String, String>();
	// Player Name, Active Instance
	
	public static HashMap<String, List<String>> instance_party = new HashMap<String, List<String>>();
	// Instance Name, Party List<String> of play names.
	
	public static volatile ConcurrentHashMap<String, Boolean> instance_loaded = new ConcurrentHashMap<String, Boolean>();
	// Instance Name, True/False -- used for cross server communication.
	
	public static volatile ConcurrentHashMap<String, HashMap<Location, String>> instance_loot = new ConcurrentHashMap<String, HashMap<Location, String>>();
	// Instance Name (template / template.#), HashMap of Location,String of Location,Chest Template to spawn/monitor.
	
	public static volatile ConcurrentHashMap<String, HashMap<Location, Inventory>> instance_loot_inv = new ConcurrentHashMap<String, HashMap<Location, Inventory>>();
	// Instance Name (template / template.#), HashMap of Location,Inventory of loot chest -- this will only be populated by loaded instances.
	
	public static volatile ConcurrentHashMap<String, HashMap<Location, String>> instance_mob_spawns = new ConcurrentHashMap<String, HashMap<Location, String>>();
	// Instance Name (template), HashMap of Location,String of Location,Chest Template to spawn/monitor.
	
	public static HashMap<String, Location> saved_location_instance = new HashMap<String, Location>();
	// Player Name, Location in main world
	
	public static HashMap<String, Long> processing_move = new HashMap<String, Long>();
	// Player Name, Time of last movement check.
	
	public static HashMap<String, Integer> total_mobs = new HashMap<String, Integer>();
	// Instance Name, Total mob count of an instance.
	
	public static ConcurrentHashMap<String, Integer> instance_wither_timer = new ConcurrentHashMap<String, Integer>();
	// Instance Name, Seconds left in wither effect, if it ticks to 0 1hp everyone
	
	public static HashMap<String, Integer> mob_kill_count = new HashMap<String, Integer>();
	// Instance Name, Total count of mobs killed in instance thus far. (prevents chunk unloading for killing)
	
	public static ConcurrentHashMap<String, Integer> teleport_on_complete = new ConcurrentHashMap<String, Integer>();
	// INSTANCE NAME, TIME UNTIL TP + UNLOAD (seconds)
	
	public static HashMap<String, String> instance_template = new HashMap<String, String>();
	// Template_name, Formal Dungeon Name
	
	public static List<String> teleport_on_load = new ArrayList<String>();
	// Used for /instance load command.
	
	static List<String> open_instances = new ArrayList<String>();
	
	// Used to determine when to TP players / when an instance has been primed.
	
	/*public static ItemStack dungeon_token = ItemMechanics.signNewCustomItem(Material.COAL, (short)1, ChatColor.LIGHT_PURPLE + "Portal Key Fragment", ChatColor.GRAY + "Exchange at the Dungeoneer for epic equipment." + ","
			+ ChatColor.GRAY.toString() + "" + ChatColor.ITALIC.toString() + "A sharded fragment from the great portal of Maltai.");*/
	
	// The Anihilator
	// The Devastator
	// y:5
	
	@SuppressWarnings("deprecation")
	public void onEnable() {
		plugin_instance = this;
		Bukkit.getServer().getPluginManager().registerEvents(this, Main.plugin);
		
		Main.plugin.getCommand("bosstp").setExecutor(new CommandBossTP());
		Main.plugin.getCommand("debuffcrystal").setExecutor(new CommandDebuffCrystal());
		Main.plugin.getCommand("drlightning").setExecutor(new CommandDRLightning());
		Main.plugin.getCommand("drreplacenear").setExecutor(new CommandDRReplaceNear());
		Main.plugin.getCommand("instance").setExecutor(new CommandInstance());
		Main.plugin.getCommand("isay").setExecutor(new CommandISay());
		
		instance_template.put("DODungeon", "Varenglade");
		instance_template.put("fireydungeon", "Infernal Abyss");
		instance_template.put("T1Dungeon", "Bandit Trove");
		instance_template.put("OneWolfeDungeon", "The Dark Depths of Aceron");
		
		Thread download = new Thread(new Runnable() {
			public void run() {
				downloadInstanceTimings();
				reCalculateAvgTime("DODungeon");
				reCalculateAvgTime("fireydungeon");
				reCalculateAvgTime("T1Dungeon");
				reCalculateAvgTime("OneWolfeDungeon");
			}
		});
		
		download.start();
		
		// This folder will be used for all .zip instance maps.
		home_dir = new File("plugins/InstanceMechanics");
		
		if(!(home_dir.exists())) {
			home_dir.mkdirs();
			new File("plugins/InstanceMechanics/dungeons").mkdir();
			new File("plugins/InstanceMechanics/loot").mkdir();
			new File("plugins/InstanceMechanics/mob_spawns").mkdir();
		}
		
		// Checks for wither ownage
		Bukkit.getServer().getScheduler().scheduleAsyncRepeatingTask(Main.plugin, new Runnable() {
			public void run() {
				for(Entry<String, String> data : player_instance.entrySet()) {
					String p_name = data.getKey();
					String instance_name = data.getValue();
					
					if(instance_name.contains("fireydungeon") && Bukkit.getServer().getPlayer(p_name) != null) {
						Player pl = Bukkit.getServer().getPlayer(p_name);
						/*if(pl.getLocation().getY() <= 10 && pl.getWorld().getName().contains("fireydungeon")){
							pl.damage(pl.getHealth());
							continue;
						}*/
						if(pl.hasPotionEffect(PotionEffectType.WITHER)) {
							for(PotionEffect pe : pl.getActivePotionEffects()) {
								if(pe.getType() == PotionEffectType.WITHER) {
									if(!(instance_wither_timer.containsKey(pl.getWorld().getName()))) {
										instance_wither_timer.put(pl.getWorld().getName(), (pe.getDuration() / 20) - 1);
									}
								}
							}
						}
					}
				}
				
				for(Entry<String, Integer> data : instance_wither_timer.entrySet()) {
					String instance_name = data.getKey();
					
					if(Bukkit.getServer().getWorld(instance_name) == null) {
						instance_wither_timer.remove(instance_name);
						continue;
					}
					
					int seconds_left = data.getValue();
					
					seconds_left--;
					
					if(seconds_left == 30) {
						for(Player pl : Bukkit.getServer().getWorld(instance_name).getPlayers()) {
							pl.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD.toString() + ">> " + ChatColor.RED + "You have " + ChatColor.UNDERLINE + seconds_left + "s" + ChatColor.RED + " left until the inferno consumes you.");
						}
					}
					
					if(seconds_left == 10) {
						for(Player pl : Bukkit.getServer().getWorld(instance_name).getPlayers()) {
							pl.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD.toString() + ">> " + ChatColor.RED + "You have " + ChatColor.UNDERLINE + seconds_left + "s" + ChatColor.RED + " left until the inferno consumes you. " + ChatColor.UNDERLINE + "BREAK A BEACON TO GAIN ANOTHER 90s!");
							
						}
					}
					
					if(seconds_left <= 1) {
						for(Player pl : Bukkit.getServer().getWorld(instance_name).getPlayers()) {
							pl.setHealth(1);
							//pl.setLevel(1);
							HealthMechanics.setPlayerHP(pl.getName(), 1);
							pl.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "You have been drained of nearly all your life by the power of the inferno.");
						}
						instance_wither_timer.remove(instance_name);
						continue;
					}
					
					instance_wither_timer.put(instance_name, seconds_left);
					
				}
			}
		}, 10 * 20L, 20L);
		
		// Ticks teleport_on_complete to teleport users out of dungeon after boss is downed.
		Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(Main.plugin, new Runnable() {
			public void run() {
				for(Entry<String, Integer> data : teleport_on_complete.entrySet()) {
					String i_name = data.getKey();
					String l_instance_template = i_name.substring(0, i_name.indexOf("."));
					String formal_dungeon_name = instance_template.get(l_instance_template);
					int seconds_left = data.getValue();
					
					if(Bukkit.getWorld(i_name) == null) {
						// Instance is unloaded.
						teleport_on_complete.remove(i_name);
						continue;
					}
					
					if(seconds_left == 60 || seconds_left == 50 || seconds_left == 40 || seconds_left == 30 || seconds_left == 20 || seconds_left == 10 || seconds_left <= 5) {
						int party_size = Bukkit.getWorld(i_name).getPlayers().size();
						for(Player pl : Bukkit.getWorld(i_name).getPlayers()) {
							if(seconds_left == 60) {
								pl.sendMessage("");
								pl.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + " * CONGRATULATIONS! You have defeated the " + ChatColor.UNDERLINE + "'" + formal_dungeon_name + "'" + ChatColor.RESET + ChatColor.GREEN + ChatColor.BOLD + " Dungeon! *");
								pl.sendMessage("");
								
								if(formal_dungeon_name.equalsIgnoreCase("Varenglade")) {
									AchievmentMechanics.addAchievment(pl.getName(), "Burick the Fanatic");
									
									if(party_size == 1) {
										AchievmentMechanics.addAchievment(pl.getName(), "Braving Burick");
									}
								}
								
								if(formal_dungeon_name.equalsIgnoreCase("Bandit Trove")) {
									AchievmentMechanics.addAchievment(pl.getName(), "Mayel the Cruel");
								}
								
								if(formal_dungeon_name.equalsIgnoreCase("Infernal Abyss")) {
									AchievmentMechanics.addAchievment(pl.getName(), "The Infernal Abyss");
								}
								
								String instance_template = pl.getWorld().getName();
								instance_template = instance_template.substring(0, instance_template.indexOf("."));
								
								int tier = getDungeonTier(instance_template);
								giveTokens(pl.getName(), tier, instance_timing.get(pl.getWorld().getName()), instance_template);
							}
							
							pl.sendMessage(ChatColor.RED + "You will be teleported out of the instance in " + seconds_left + ChatColor.BOLD + "s ...");
							
						}
						
						if(seconds_left == 60) {
							String instance_complete_string = "";
							if(total_timing.containsKey(l_instance_template)) {
								for(int i : total_timing.get(l_instance_template)) {
									instance_complete_string = i + ",";
								}
							}
							
							long cur_time = System.currentTimeMillis();
							double seconds = (int) Math.round(((cur_time - instance_timing.get(i_name)) / 1000.0D));
							instance_complete_string += (int) seconds + ",";
							
							CommunityMechanics.sendPacketCrossServer("@instance_time@" + l_instance_template + ":" + (int) seconds, -1, true);
							Hive.sql_query.add("INSERT INTO instance (instance_template, times) VALUES('" + l_instance_template + "', '" + instance_complete_string + "') ON DUPLICATE KEY UPDATE times='" + instance_complete_string + "'");
						}
						
						seconds_left--;
					}
					
					if(seconds_left == 0) {
						// Teleport them all out -- the unload takes place inside of removeFromInstanceParty();
						for(Player pl : Bukkit.getWorld(i_name).getPlayers()) {
							if(isInstance(pl.getWorld().getName()) && saved_location_instance.containsKey(pl.getName())) {
								// TODO: Give them tokens!
								
								try {
									pl.teleport(saved_location_instance.get(pl.getName()));
									saved_location_instance.remove(pl.getName());
								} catch(NullPointerException npe) {
									// Do nothing.
								}
								
								// Inside an instance.
								removeFromInstanceParty(pl.getName());
								
							}
						}
					}
					
					seconds_left--;
					teleport_on_complete.put(i_name, seconds_left);
				}
			}
		}, 10 * 20L, 1 * 20L);
		
		// Used as a cross-over for asyncLoadNewInstance -- tells/allows players into the instance once it's loaded.
		Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(Main.plugin, new Runnable() {
			public void run() {
				for(Entry<String, Boolean> data : instance_loaded.entrySet()) {
					boolean loaded = data.getValue();
					
					if(loaded == true) {
						continue; // We don't care.
					}
					
					String instance_name = data.getKey();
					String template_name = instance_name;
					if(instance_name.contains(".")) {
						template_name = instance_name.substring(0, instance_name.lastIndexOf("."));
					}
					if(template_name.contains("DODungeon")) {
						template_name = "Varenglade";
					}
					if(template_name.contains("fireydungeon")) {
						template_name = "Infernal Abyss";
					}
					if(template_name.contains("T1Dungeon")) {
						template_name = "Bandit Trove";
					}
					if(template_name.contains("OneWolfeDungeon")) {
						template_name = "The Dark Depths of Aceron";
					}
					if(open_instances.contains(instance_name) && Bukkit.getServer().getWorld(instance_name) != null) {
						instance_loaded.put(instance_name, true);
						World w = Bukkit.getServer().getWorld(instance_name);
						
						// The instance is now loaded.
						for(String s : instance_party.get(instance_name)) {
							if(Bukkit.getServer().getPlayer(s) != null) {
								final Player pl = Bukkit.getServer().getPlayer(s);
								saved_location_instance.put(pl.getName(), pl.getLocation());
								if(!(teleport_on_load.contains(pl.getName()))) {
									pl.sendMessage(ChatColor.LIGHT_PURPLE.toString() + "<" + ChatColor.BOLD + "P" + ChatColor.LIGHT_PURPLE + ">" + " Your party leader has started the '" + ChatColor.UNDERLINE + template_name + ChatColor.LIGHT_PURPLE + "' dungeon.");
								} else { // The person that initiated the instance loading.
									pl.teleport(w.getSpawnLocation());
									pl.setFallDistance(0.0F);
									
									if(instance_name.contains("fireydungeon")) {
										Main.plugin.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
											public void run() {
												setPlayerEnvironment(pl, Environment.NETHER);
												pl.setFallDistance(0.0F);
											}
										}, 20L);
										
									}
									
									teleport_on_load.remove(pl.getName());
									pl.sendMessage(ChatColor.LIGHT_PURPLE.toString() + "<" + ChatColor.BOLD + "P" + ChatColor.LIGHT_PURPLE + ">" + " You have started the '" + ChatColor.UNDERLINE + template_name + ChatColor.LIGHT_PURPLE + "' dungeon.");
									instance_timing.put(w.getName(), System.currentTimeMillis());
									
									final String w_name = w.getName();
									
									Main.plugin.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
										public void run() {
											int total_mob_count = 0;
											World w = Bukkit.getWorld(w_name);
											
											for(LivingEntity le : w.getLivingEntities()) {
												if(le != null && MonsterMechanics.mob_health.containsKey(le)) {
													total_mob_count++;
												}
											}
											
											total_mobs.put(w.getName(), total_mob_count);
											mob_kill_count.put(w.getName(), 0);
											log.info("[InstanceMechanics] Total mob count for instance " + w.getName() + ": " + total_mob_count);
										}
									}, 40L);
									
								}
							}
						}
						continue;
					}
				}
			}
		}, 10 * 20L, 1 * 20L);
		
		// Non-main thread playerMove() event.
		Main.plugin.getServer().getScheduler().scheduleSyncRepeatingTask(Main.plugin, new Runnable() {
			public void run() {
				for(final Player pl : Main.plugin.getServer().getOnlinePlayers()) {
					if(teleport_on_load.contains(pl.getName())) {
						continue; // Pending dungeon load already taking place.
					}
					
					String region = DuelMechanics.getRegionName(pl.getLocation());
					if(region.startsWith("instance_")) {
						// Instance time!
						
						if(processing_move.containsKey(pl.getName()) && (System.currentTimeMillis() - processing_move.get(pl.getName())) <= (5 * 1000)) {
							// Don't get them in a TP loop.
							continue;
						}
						
						MountMechanics.dismount(pl);
						
						String instance_name = region.substring(region.indexOf("_") + 1, region.length());
						if(instance_name.equalsIgnoreCase("dodungeon")) {
							instance_name = "DODungeon";
						}
						if(instance_name.equalsIgnoreCase("t1dungeon")) {
							instance_name = "T1Dungeon";
						}
						if(instance_name.equalsIgnoreCase("onewolfedungeon")) {
							instance_name = "OneWolfeDungeon";
						}
						boolean party_in_instance = false;
						String party_instance = "";
						
						if(PartyMechanics.hasParty(pl.getName())) {
							// This will ensure only the approved people can join existing instances.
							for(String s : PartyMechanics.party_map.get(pl.getName()).getPartyMembers()) {
								if(s.equalsIgnoreCase(pl.getName())) {
									continue;
								}
								
								if(player_instance.containsKey(s)) {
									// Ok, so some member is in an instance.
									String mem_instance = player_instance.get(s);
									
									party_in_instance = true;
									party_instance = mem_instance;
									
									mem_instance = mem_instance.substring(0, mem_instance.lastIndexOf("."));
									if(mem_instance.equalsIgnoreCase(instance_name)) {
										// Let's group them up UNLESS they've already been locked out.
										
										if((!(PartyMechanics.isPartyLeader(pl.getName())) && !(player_instance.containsKey(pl.getName()))) || !instance_party.get(player_instance.get(s)).contains(pl.getName())) {
											// They're in the right party and the right instance, but they either weren't in the party when the instance was made or they have already died in the instance once.
											pl.sendMessage(ChatColor.RED + "You " + ChatColor.UNDERLINE + "cannot" + ChatColor.RED + " enter this instance until your party has defeated it or given up.");
											pl.sendMessage(ChatColor.GRAY + "You cannot help them because you were not in the party when the instance was started.");
											//e.setCancelled(true);
											//pl.teleport(e.getFrom());
											continue;
										} else {
											break; // They're OK, they can join their friends.
										}
									}
									
									pl.sendMessage(ChatColor.RED + "Your party is already inside a " + ChatColor.UNDERLINE + "different" + ChatColor.RED + " instanced dungeon.");
									pl.sendMessage(ChatColor.GRAY + "You'll need to either leave your current party or wait for them to finish their run.");
									//e.setCancelled(true);
									//pl.teleport(e.getFrom());
									continue;
								}
							}
							
							if(party_in_instance == true) {
								// If we've gotten this far, they are going to join the same instance as their friends.
								boolean teleported = false;
								for(String s : instance_party.get(party_instance)) {
									if(s.equalsIgnoreCase(pl.getName())) {
										continue;
									}
									
									if(Bukkit.getPlayer(s).getWorld().getName().equalsIgnoreCase(party_instance)) {
										Bukkit.getPlayer(s).sendMessage(ChatColor.LIGHT_PURPLE.toString() + "<" + ChatColor.BOLD + "P" + ChatColor.LIGHT_PURPLE + ">" + ChatColor.GRAY + " " + pl.getName() + " has " + ChatColor.GREEN + ChatColor.UNDERLINE + "joined" + ChatColor.GRAY + " the dungeon.");
										
										if(teleported == false) {
											pl.teleport(Bukkit.getPlayer(s).getLocation());
											pl.setFallDistance(0.0F);
											
											if(pl.getLocation().getWorld().getName().contains("fireydungeon")) {
												Main.plugin.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
													public void run() {
														setPlayerEnvironment(pl, Environment.NETHER);
														pl.setFallDistance(0.0F);
													}
												}, 20L);
											}
											teleported = true;
										}
									}
								}
								
								continue;
							}
						}
						
						if(!(PartyMechanics.hasParty(pl.getName()))) {
							PartyMechanics.createParty(pl.getName(), pl, null);
							// Make their own party!
						}
						
						if(!(PartyMechanics.isPartyLeader(pl.getName()))) {
							pl.sendMessage(ChatColor.RED + "You are " + ChatColor.UNDERLINE + "NOT" + ChatColor.RED + " the party leader.");
							pl.sendMessage(ChatColor.GRAY + "Only the party leader can start a new dungeon instance.");
							//e.setCancelled(true);
							//pl.teleport(e.getFrom());
							continue;
						}
						
						if(instance_party.size() >= 6 || (Bukkit.getMotd().contains("US-1 ") && instance_party.size() >= 2)) {
							processing_move.put(pl.getName(), System.currentTimeMillis() + 6000);
							pl.sendMessage(ChatColor.RED + "All available dungeon instances are " + ChatColor.UNDERLINE + "full" + ChatColor.RED + " on this shard.");
							pl.sendMessage(ChatColor.GRAY + "Use /shard to hop to a different one.");
							continue;
						}
						
						String new_instance = linkInstanceToParty(instance_name, PartyMechanics.party_map.get(pl.getName()).getPartyMembers(), false); // Define new instance.
						if(!(teleport_on_load.contains(pl.getName()))) {
							teleport_on_load.add(pl.getName());
						}
						
						String formal_dungeon_name = "";
						if(instance_name.contains(".")) {
							formal_dungeon_name = instance_template.get(instance_name.substring(0, instance_name.indexOf(".")));
						} else {
							formal_dungeon_name = instance_template.get(instance_name);
						}
						
						pl.sendMessage(ChatColor.GRAY + "Loading Instance: '" + ChatColor.UNDERLINE + formal_dungeon_name + ChatColor.GRAY + "' -- Please wait...");
						
						syncLoadNewInstance(new_instance, false, false); // Load new instance.
						continue;
					}
					
					if(region.startsWith("exit_instance")) {
						// Teleport them out!
						if(isInstance(pl.getWorld().getName()) && saved_location_instance.containsKey(pl.getName())) {
							// Inside an instance.
							removeFromInstanceParty(pl.getName());
							try {
								pl.teleport(saved_location_instance.get(pl.getName()));
								saved_location_instance.remove(pl.getName());
							} catch(NullPointerException npe) {
								// Do nothing.
							}
						}
						if(PartyMechanics.hasParty(pl.getName())) {
							for(String s : PartyMechanics.party_map.get(pl.getName()).getPartyMembers()) {
								if(s.equalsIgnoreCase(pl.getName())) {
									continue;
								}
								Bukkit.getPlayer(s).sendMessage(ChatColor.LIGHT_PURPLE.toString() + "<" + ChatColor.BOLD + "P" + ChatColor.LIGHT_PURPLE + ">" + ChatColor.GRAY + " " + pl.getName() + " has " + ChatColor.RED + ChatColor.UNDERLINE + "left" + ChatColor.GRAY + " the dungeon.");
							}
						}
					}
				}
			}
		}, 5 * 20L, 1 * 20L);
		
		log.info("[InstanceMechanics] has been ENABLED.");
	}
	
	public void onDisable() {
		for(String s : instance_loaded.keySet()) {
			// For every instance that is loaded...
			asyncUnloadWorld(s);
		}
		
		int attempts = 0;
		while(Main.plugin.getServer().getWorlds().size() > 1 && attempts <= 100) {
			boolean quit = true;
			for(World w : Bukkit.getWorlds()) {
				if(!w.getName().contains("DungeonRealms")) {
					quit = false;
					break;
				}
			}
			if(quit) break;
			try {
				Thread.sleep(500);
			} catch(InterruptedException e) {
				e.printStackTrace();
			}
			attempts++;
		}
		
		log.info("[InstanceMechanics] has been DISABLED.");
	}
	
	public void tickMobCounter(String dungeon_name, int amount) {
		if(dungeon_name == null) return;
		if(amount == 0) return;
		if(!mob_kill_count.containsKey(dungeon_name)) mob_kill_count.put(dungeon_name, 0);
		int i = mob_kill_count.get(dungeon_name);
		i += amount;
		mob_kill_count.put(dungeon_name, i);
	}
	
	public void downloadInstanceTimings() {
		PreparedStatement pst;
		
		try {
			pst = ConnectionPool.getConnection().prepareStatement("SELECT instance_template, times FROM instance");
			
			pst.execute();
			ResultSet rs = pst.getResultSet();
			
			if(!(rs.next())) {
				log.info("[InstanceMechanics] No instance timing data found.");
				return;
			}
			
			rs.beforeFirst();
			while(rs.next()) {
				int count = 0;
				String instance_template = rs.getString("instance_template");
				String time_string = rs.getString("times");
				List<Integer> instance_times = new ArrayList<Integer>();
				for(String s : time_string.split(",")) {
					count++;
					instance_times.add(Integer.parseInt(s));
				}
				
				total_timing.put(instance_template, instance_times);
				log.info("[InstanceMechanics] Loaded " + count + " completion times for instance " + instance_template);
			}
			
		} catch(SQLException e) {
			e.printStackTrace();
			return;
		}
		
		return;
	}
	
	public String getGrade(double percent) {
		if(percent >= 30) {
			return "A";
		} else if(percent >= 15) {
			return "B";
		} else if(percent >= -15) {
			return "C";
		} else if(percent >= -30) {
			return "D";
		} else if(percent < -30) { return "F"; }
		return "C";
	}
	
	public void giveTokens(String p_name, int tier, long start_time, String world_template) {
		int tokens_to_give = 0;
		long cur_time = System.currentTimeMillis();
		
		double seconds = (int) Math.round(((cur_time - start_time) / 1000.0D));
		
		String instance_template = world_template;
		if(instance_template.contains(".")) {
			instance_template = instance_template.substring(0, instance_template.lastIndexOf("."));
		}
		
		double avg_seconds = avg_timing.get(instance_template);
		double percent = (seconds / avg_seconds);
		
		if(percent < 1 && percent > 0) { // (0.00001 - 0.999999)
			// 0% +, they finished the dungeon either as fast or faster than the avg.
			percent = Math.abs((percent - 1) * 100.0D);
			// Subtract by 1 and take abs. value to get the % difference between the two.
			// In this case, how much better was this attempt than the previous?
		} else if(percent > 1) { // (1.00001 - infinity)
			// Less than 0%, they're worse than avg.
			percent = Math.abs((percent - 1) * 100.0D);
			// 1.1 - 1 = 0.10, that's 10%, which would mean they did 10% WORSE than avg.
			// This value is the NEGATIVE %, because they did WORSE than avg.
			percent = -percent;
		}
		
		if(avg_seconds == -1) {
			// No values set, they get a C no matter what.
			percent = 0;
		}
		
		// A = 30% 
		// B = 15%
		// C = -15% - 15%
		// D = -15% - 30%
		// F = -30%
		
		ChatColor tier_color = ChatColor.WHITE;
		if(tier == 1) {
			tier_color = ChatColor.WHITE;
		}
		if(tier == 2) {
			tier_color = ChatColor.GREEN;
		}
		if(tier == 3) {
			tier_color = ChatColor.AQUA;
		}
		if(tier == 4) {
			tier_color = ChatColor.LIGHT_PURPLE;
		}
		if(tier == 5) {
			tier_color = ChatColor.YELLOW;
		}
		
		if(percent >= 30 && tokens_to_give == 0) {
			// A
			tokens_to_give = 1300 + new Random().nextInt(250);
		} else if(percent >= 15 && tokens_to_give == 0) {
			// B
			tokens_to_give = 800 + new Random().nextInt(333);
		} else if(percent >= -15 && tokens_to_give == 0) {
			// C
			tokens_to_give = 750 + new Random().nextInt(250);
		} else if(percent >= -30 && tokens_to_give == 0) {
			// D
			tokens_to_give = 400 + new Random().nextInt(200);
		} else if(percent < -30 && tokens_to_give == 0) {
			// F
			tokens_to_give = 100 + new Random().nextInt(50);
		}
		
		if(Bukkit.getPlayer(p_name) != null) {
			Player pl = Bukkit.getPlayer(p_name);
			pl.sendMessage(tier_color.toString() + ChatColor.BOLD + "       " + "Dungeon Grade: " + getGrade(percent));
			pl.sendMessage(tier_color + "You have gained " + ChatColor.UNDERLINE + tokens_to_give + " Portal Shards" + tier_color + " for completing this Dungeon.");
		}
		
		int tier_index = tier - 1;
		List<Integer> portal_shards = Hive.player_portal_shards.get(p_name);
		
		int current_shards = portal_shards.get(tier_index);
		current_shards += tokens_to_give;
		portal_shards.set(tier_index, current_shards);
		
		Hive.player_portal_shards.put(p_name, portal_shards);
	}
	
	public static int getDungeonTier(String instance_template) {
		if(instance_template.contains("DODungeon")) { return 3; }
		if(instance_template.contains("T1Dungeon")) { return 1; }
		if(instance_template.contains("fireydungeon")) { return 4; }
		if(instance_template.contains("OneWolfeDungeon")) { return 4; }
		return 1; // Undefined, T1.
	}
	
	public static void reCalculateAvgTime(String instance_template) {
		if(total_timing.containsKey(instance_template)) {
			int total_val = 0;
			int avg_val = 0;
			for(int i : total_timing.get(instance_template)) {
				total_val += i;
			}
			avg_val = total_val / total_timing.size();
			avg_timing.put(instance_template, avg_val);
			System.out.print("Average time for instance: " + instance_template + " is " + avg_val + "s.");
		} else {
			// We have no timings... -1 seconds.
			System.out.print("There is not instance time for the template " + instance_template);
			avg_timing.put(instance_template, -1);
		}
	}
	
	public static void setBlockAreaType(World w, double x1, double y1, double z1, double x2, double y2, double z2, Material m) {
		double orig_x = x1;
		double orig_z = z1;
		
		while(y1 <= y2) {
			x1 = orig_x;
			while(x1 <= x2) {
				z1 = orig_z;
				while(z1 <= z2) {
					w.getBlockAt(new Location(w, x1, y1, z1)).setType(m);
					z1++;
				}
				x1++;
			}
			y1++;
		}
	}
	
	public static int getPortalShardCount(String p_name, int tier) {
		if(!(Hive.player_portal_shards.containsKey(p_name))) { return 0; }
		int tier_index = tier - 1;
		return Hive.player_portal_shards.get(p_name).get(tier_index);
	}
	
	public static void subtractShards(String p_name, int tier, int amount) {
		if(Hive.player_portal_shards.containsKey(p_name)) {
			List<Integer> portal_shards = Hive.player_portal_shards.get(p_name);
			int old_amount = portal_shards.get((tier - 1));
			portal_shards.set((tier - 1), old_amount - amount);
			Hive.player_portal_shards.put(p_name, portal_shards);
		}
	}
	
	public static void setPlayerEnvironment(Player pl, Environment env) {
		int dimension = 0;
		if(env == Environment.NORMAL) {
			dimension = 0;
		}
		if(env == Environment.NETHER) {
			dimension = -1;
		}
		if(env == Environment.THE_END) {
			dimension = 1;
		}
		
		if(pl == null) return;
		boolean fly = pl.isFlying();
		CraftPlayer cp = (CraftPlayer) pl;
		EntityPlayer ep = (EntityPlayer) cp.getHandle();
		WorldServer worldserver = ((CraftWorld) pl.getWorld()).getHandle();
		
		PacketPlayOutRespawn packet_a = new PacketPlayOutRespawn((byte) dimension, worldserver.difficulty, worldserver.getWorldData().getType(), ep.playerInteractManager.getGameMode());
		
		cp.getHandle().playerConnection.sendPacket(packet_a);
		Chunk c = pl.getLocation().getChunk();
		for(int x = -6; x < 6; x++)
			for(int z = -6; z < 6; z++)
				pl.getWorld().refreshChunk(c.getX() + x, c.getZ() + z);
		
		pl.setFlying(fly);
		updatePlayerView(pl);
		updateEntities(pl);
	}
	
	// This just updated any open inventory they may have.
	private static void updatePlayerView(Player player) {
		if((player == null) || (((CraftPlayer) player).getHandle().activeContainer == null)) return;
		((CraftPlayer) player).getHandle().updateInventory(((CraftPlayer) player).getHandle().activeContainer);
	}
	
	// So this (I assume) untracks the player and retracks them for entities within 32x32x32...
	@SuppressWarnings("unchecked")
	private static void updateEntities(Player player) {
		WorldServer ws = ((CraftWorld) player.getWorld()).getHandle();
		EntityTracker tracker = ws.tracker;
		for(Entity ent : player.getNearbyEntities(32.0D, 32.0D, 32.0D)) {
			EntityTrackerEntry entry = (EntityTrackerEntry) tracker.trackedEntities.get(ent.getEntityId());
			List<EntityPlayer> nms = new ArrayList<EntityPlayer>();
			nms.add(((CraftPlayer) player).getHandle());
			entry.trackedPlayers.removeAll(nms);
			entry.scanPlayers(nms);
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityDeath(EntityDeathEvent e) {
		Entity ent = e.getEntity();
		if(isInstance(ent.getWorld().getName())) {
			String wname = ent.getWorld().getName();
			tickMobCounter(wname, 1);
			
			if(ent.hasMetadata("mobname")) {
				String custom_name = ChatColor.stripColor(ent.getMetadata("mobname").get(0).asString());
				if(ent.getType() == EntityType.ENDERMAN || custom_name.equalsIgnoreCase("The Devastator") || custom_name.equalsIgnoreCase("The Annihilator")) {
					for(Player pl : ent.getWorld().getPlayers()) {
						pl.removePotionEffect(PotionEffectType.WITHER);
						instance_wither_timer.remove(pl.getWorld().getName());
					}
				}
				
				if(custom_name.equalsIgnoreCase("Wicked Gate Keeper")) {
					setBlockAreaType(ent.getWorld(), 206, 35, 72, 206, 38, 75, Material.AIR);
					for(Player pl : ent.getWorld().getPlayers()) {
						pl.playSound(pl.getLocation(), Sound.DOOR_OPEN, 1F, 1F);
						pl.sendMessage(ChatColor.RED + "Wicked Gate Keeper: " + ChatColor.WHITE + "Hah! You may have defeated me, but you only further your own destruction.");
						pl.sendMessage(ChatColor.YELLOW + "You hear a gateway open nearby.");
					}
				}
				if(custom_name.equalsIgnoreCase("Wicked Sargent Derricks")) {
					setBlockAreaType(ent.getWorld(), 9, 82, 4, 11, 85, 4, Material.AIR);
					for(Player pl : ent.getWorld().getPlayers()) {
						pl.playSound(pl.getLocation(), Sound.DOOR_OPEN, 1F, 1F);
						pl.sendMessage(ChatColor.RED + "Wicked Sargent Derricks: " + ChatColor.WHITE + "Death... will come... for all.");
						pl.sendMessage(ChatColor.YELLOW + "You hear a gateway open nearby.");
					}
				}
				if(custom_name.equalsIgnoreCase("Wicked Captian Roedock")) {
					setBlockAreaType(ent.getWorld(), 167, 134, -9, 169, 137, -9, Material.AIR);
					for(Player pl : ent.getWorld().getPlayers()) {
						pl.playSound(pl.getLocation(), Sound.DOOR_OPEN, 1F, 1F);
						pl.sendMessage(ChatColor.RED + "Wicked Captian Roedock: " + ChatColor.WHITE + "Your ignorance will be your undoing, foolish Andalucians.");
						pl.sendMessage(ChatColor.YELLOW + "You hear a gateway open nearby.");
					}
				}
				if(custom_name.equalsIgnoreCase("Devious Demon")) {
					setBlockAreaType(ent.getWorld(), -25, 170, -5, -23, 173, -5, Material.AIR);
					for(Player pl : ent.getWorld().getPlayers()) {
						pl.playSound(pl.getLocation(), Sound.DOOR_OPEN, 1F, 1F);
						pl.sendMessage(ChatColor.RED + "Devious Demon: " + ChatColor.WHITE + "How dare you... defy the powers of Akatan");
						pl.sendMessage(ChatColor.YELLOW + "You hear a gateway open nearby.");
					}
				}
			}
			if(!(e instanceof Player)){
			    e.getDrops().clear();
			}
		}
	}
	
	@EventHandler
	public void onPlayerLogin(PlayerLoginEvent e) {
		String p_name = e.getPlayer().getName();
		processing_move.put(p_name, System.currentTimeMillis() + 8000);
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerRespawn(PlayerRespawnEvent e) {
		Player pl = e.getPlayer();
		if(pl.getWorld().getName().equalsIgnoreCase(e.getRespawnLocation().getWorld().getName()) && pl.getLocation().distanceSquared(e.getRespawnLocation()) <= 2) {
			// They're respawning on themselves, ignore.
			// Non-legit death, they won't loose any items.
			return;
		}
		
		saved_location_instance.remove(pl.getName());
		
		if(InstanceMechanics.isInstance(pl.getWorld().getName())) {
			removeFromInstanceParty(pl.getName());
		}
	}
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent e) {
		Player pl = e.getPlayer();
		if(teleport_on_load.contains(pl.getName())) {
			e.setCancelled(true);
			// Don't let them move while it's loading up.
			return;
		}
	}
	
	@EventHandler
	public void unChunkUnload(ChunkUnloadEvent e) {
		if(isInstance(e.getWorld().getName()) && instance_loaded.containsKey(e.getWorld().getName())) {
			e.setCancelled(true);
			/*if(!(e.getWorld().getName().contains("T1Dungeon"))){
				e.setCancelled(true);
			}*/
			/*int mob_count = 0;
			for(Entity ent : e.getChunk().getEntities()){
				if(ent instanceof LivingEntity && MonsterMechanics.mob_health.containsKey(ent)){
					mob_count++;
				}
			}
			if(mob_count > 0){
				e.setCancelled(true); // Don't unload chunks w/ entities
			}*/
			// Never unload chunks in instances.
		}
	}
	
	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerQuit(PlayerQuitEvent e) {
		Player pl = e.getPlayer();
		
		teleport_on_load.remove(pl.getName());
		
		if(saved_location_instance.containsKey(pl.getName())) {
			// Inside an instance.
			pl.teleport(saved_location_instance.get(pl.getName()));
			saved_location_instance.remove(pl.getName());
		}
		removeFromInstanceParty(pl.getName());
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onEnderCrystalInteract(EntityDamageEvent e) {
		if((e.getEntity() instanceof EnderCrystal) && e.getEntity().getWorld().getName().contains("fireydungeon")) {
			e.setCancelled(true);
			e.setDamage(0);
			
			if(e instanceof EntityDamageByEntityEvent) {
				EntityDamageByEntityEvent edee = (EntityDamageByEntityEvent) e;
				if(edee.getDamager() instanceof Player) {
					Player pl = (Player) edee.getDamager();
					Block b = e.getEntity().getLocation().subtract(0, 1, 0).getBlock();
					if(b.getType() == Material.BEDROCK) {
						b.setType(Material.AIR);
						b.getLocation().add(0, 1, 0).getBlock().setType(Material.AIR);
					}
					
					try {
						ParticleEffect.sendToLocation(ParticleEffect.MAGIC_CRIT, b.getLocation().add(0, 1, 0), new Random().nextFloat(), new Random().nextFloat(), new Random().nextFloat(), 1F, 50);
					} catch(Exception e1) {
						e1.printStackTrace();
					}
					//b.getWorld().spawnParticle(b.getLocation().add(0, 1, 0), Particle.MAGIC_CRIT, 1F, 50);
					instance_wither_timer.put(b.getWorld().getName(), 90);
					
					for(Player p : pl.getWorld().getPlayers()) {
						p.removePotionEffect(PotionEffectType.WITHER);
						p.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, (int) (90 * 20L), 0));
						p.playSound(p.getLocation(), Sound.ENDERDRAGON_HIT, 5F, 1.5F);
						p.sendMessage(ChatColor.YELLOW + "Debuff timer refreshed, " + ChatColor.UNDERLINE + HealthMechanics.getMaxHealthValue(p.getName()) + " DMG " + ChatColor.YELLOW + "will be inflicted in 90s unless another beacon is activated.");
					}
					
					e.getEntity().remove();
				}
			}
		}
	}
	
	@EventHandler
	public void onPlayerInteractEvent(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		//boolean tick_set = false; // TODO - UNUSED
		
		if(!(p.getWorld().getName().contains("fireydungeon"))) { return; }
		
		if(e.getAction() == Action.LEFT_CLICK_BLOCK) {
			Block b = e.getClickedBlock();
			if((b.getType() == Material.BEDROCK) || (b.getType() == Material.FIRE)) {
				if(b.getType() == Material.BEDROCK) {
					Block b2 = b.getLocation().add(0.0D, 1.0D, 0.0D).getBlock();
					if(b2.getType() == Material.FIRE) {
						b = b2;
						//tick_set = true;
					} else if(b2.getType() != Material.FIRE) { return; }
				}
				
				b.setType(Material.AIR);
				b.getLocation().subtract(0.0D, 1.0D, 0.0D).getBlock().setType(Material.AIR);
				
				for(Entity ent : b.getChunk().getEntities()) {
					if(!(ent instanceof EnderCrystal)) {
						continue;
					}
					if(ent.getLocation().distanceSquared(b.getLocation()) <= 4) {
						ent.remove();
						break;
					}
				}
				
				try {
					ParticleEffect.sendToLocation(ParticleEffect.MAGIC_CRIT, b.getLocation().add(0, 1, 0), new Random().nextFloat(), new Random().nextFloat(), new Random().nextFloat(), 1F, 50);
				} catch(Exception e1) {
					e1.printStackTrace();
				}
				//b.getWorld().spawnParticle(b.getLocation().add(0, 1, 0), Particle.MAGIC_CRIT, 1F, 50);
				instance_wither_timer.put(b.getWorld().getName(), 90);
				
				for(Player pl : p.getWorld().getPlayers()) {
					pl.removePotionEffect(PotionEffectType.WITHER);
					pl.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, (int) (90 * 20L), 0));
					pl.playSound(pl.getLocation(), Sound.ENDERDRAGON_HIT, 5F, 1.5F);
					pl.sendMessage(ChatColor.YELLOW + "Debuff timer refreshed, " + ChatColor.UNDERLINE + HealthMechanics.getMaxHealthValue(pl.getName()) + " DMG " + ChatColor.YELLOW + "will be inflicted in 90s unless another beacon is activated.");
				}
			}
		}
	}
	
	public static boolean isDungeonItem(ItemStack is) {
		if(is != null && is.hasItemMeta() && is.getItemMeta().hasLore()) {
			List<String> lore = is.getItemMeta().getLore();
			for(String s : lore) {
				//s = ChatColor.stripColor(s);
				if(s.contains(ChatColor.RED.toString() + "Dungeon Item")) { return true; }
			}
		}
		return false;
	}
	
	@SuppressWarnings("deprecation")
	public static void removeFromInstanceParty(String p_name) {
		if(!(player_instance.containsKey(p_name))) { return; // They're not in an instance.
		}
		
		if(Bukkit.getPlayer(p_name) != null) {
			Player pl = Bukkit.getPlayer(p_name);
			for(ItemStack is : pl.getInventory()) {
				if(isDungeonItem(is)) {
					pl.getInventory().remove(is);
				}
			}
			pl.updateInventory();
		}
		
		processing_move.put(p_name, System.currentTimeMillis() + 6000);
		
		String instance = player_instance.get(p_name);
		player_instance.remove(p_name);
		
		if(!(instance_party.containsKey(instance))) {
			log.info("[InstanceMechanics] Major data error while trying to remove player " + p_name + " from instance " + instance);
			return; // WTF?
		}
		
		List<String> party = new ArrayList<String>(instance_party.get(instance));
		if(party != null && party.contains(p_name)) {
			party.remove(p_name);
		}
		
		if(party.size() > 0) {
			instance_party.put(instance, party);
		} else {
			instance_party.remove(instance);
		}
		
		// If the instance whitelist is empty or no players are left in the instance, it's time to unload it.
		if(party.size() <= 0 || Bukkit.getWorld(instance).getPlayers().size() == 0) {
			asyncUnloadWorld(instance);
			instance_party.remove(instance);
		}
		
	}
	
	public static void syncLoadNewInstance(final String instance, final boolean load_template, boolean new_template) {
		String instance_template = "";
		if(load_template == false) {
			instance_template = instance.substring(0, instance.lastIndexOf("."));
		} else if(load_template == true) {
			if(instance_template.contains(".")) {
				instance_template = instance.substring(0, instance.lastIndexOf("."));
			} else {
				instance_template = instance;
			}
		}
		
		final String f_instance_template = instance_template;
		
		if(!(new File("plugins/InstanceMechanics/dungeons/" + instance_template + ".zip").exists())) {
			// The instance name is invalid.
			log.info("[InstanceMechanics] Could not locate instance '" + instance_template + "' in /dungeons/");
			return;
		}
		
		if(Bukkit.getWorld(instance) != null || instance_loaded.containsKey(instance)) {
			// The instance already exists! Yikes!
			log.info("[InstanceMechanics] Couldn't load instance: " + instance + " -- the system said it wasn't loaded when it was!");
			return;
		}
		
		instance_loaded.put(instance, false);
		log.info("[InstanceMechanics] CREATING INSTANCE: " + instance);
		
		unzipArchive(new File("plugins/InstanceMechanics/dungeons/" + f_instance_template + ".zip"), new File(instance));
		// We've unarchived the world, now it's time to load the world / world settings.
		
		if(new File(instance + "/" + "uid.dat").exists()) {
			// Delete that shit.
			new File(instance + "/" + "uid.dat").delete();
		}
		
		deleteFolder(new File(instance + "/players"));
		// Ensure no wierd data is going to be in that world.
		
		if(load_template == false) {
			try {
				FileUtils.copyDirectory(new File("plugins/WorldGuard/worlds/" + f_instance_template), new File("plugins/WorldGuard/worlds/" + instance));
			} catch(IOException e) {
				e.printStackTrace();
			}
		}
		
		WorldCreator wc = new WorldCreator(instance);
		wc.generateStructures(false);
		Main.plugin.getServer().createWorld(wc);
		// Load the world.
		
		/*CraftWorld cw = (CraftWorld)w;
		cw.viewDistance = 4;*/
		// TODO
		
		if(!(instance_loot.containsKey(instance))) {
			LootMechanics.loadInstancelootSpawnerData(instance);
			// Loads the .loot template file into memory buffer, converts strings to locations to then be spawned in with SpawnInstanceLootChests();
		}
		if(!(instance_mob_spawns.containsKey(instance))) {
			MonsterMechanics.loadInstanceMobSpawnerData(instance);
			// Loads the .dat template file of the mob spawner locations and the spawn strings, converts strings to locations.
			// The mobs will then be spawned by the MobSpawnEvent() function in MonsterMechanics.
		}
		
		// If the file doesn't exist, put new hashmaps in as this might be the first creation.
		if(!(instance_loot.containsKey(instance))) {
			instance_loot.put(instance, new HashMap<Location, String>());
		}
		if(!(instance_mob_spawns.containsKey(instance))) {
			instance_mob_spawns.put(instance, new HashMap<Location, String>());
		}
		
		LootMechanics.SpawnInstanceLootChests(instance);
		// Spawns in actual chests and decides on each inventory, stores in memory buffer.
		
		//instance_loaded.put(instance, true);
		open_instances.add(instance);
		log.info("[InstanceMechanics] LOADED INSTANCE: " + instance);
		
	}
	
	public static void asyncUnloadWorld(final String world_name) {
		boolean save_as_template = true;
		if(world_name.contains(".")) {
			save_as_template = false;
		}
		
		if(Bukkit.getWorld(world_name) == null) {
			log.info("[InstanceMechanics] No instance with the name " + world_name + " loaded. Returning out of asyncUnloadWorld.");
			return;
		}
		
		for(Player pl : Bukkit.getWorld(world_name).getPlayers()) {
			if(saved_location_instance.containsKey(pl.getName())) {
				pl.teleport(saved_location_instance.get(pl.getName()));
			} else {
				pl.teleport(SpawnMechanics.getRandomSpawnPoint(pl.getName()));
			}
			saved_location_instance.remove(pl.getName());
			player_instance.remove(pl.getName());
		}
		
		if(instance_party.containsKey(world_name)) {
			for(String s : instance_party.get(world_name)) {
				// For every player who is on the instance whitelist...
				player_instance.remove(s); // Remove their instance alignment!
			}
		}
		
		// Now all players have been kicked out.
		instance_party.remove(world_name);
		
		final boolean f_save_as_template = save_as_template;
		
		if(save_as_template) {
			for(Entity ent : Main.plugin.getServer().getWorld(world_name).getEntities()) {
				// Clear all the entities/monsters
				if(MonsterMechanics.mob_health.containsKey(ent)) {
					ent.remove();
				}
			}
		}
		
		log.info("[InstanceMechanics] AsyncUnloading Instance: " + world_name);
		
		for(Location loc : instance_mob_spawns.get(world_name).keySet()) {
			MonsterMechanics.mob_spawns.remove(loc);
			MonsterMechanics.loaded_mobs.remove(loc);
		}
		
		if(f_save_as_template) {
			Main.plugin.getServer().unloadWorld(world_name, true);
			LootMechanics.savelootSpawnerData();
			MonsterMechanics.saveMobSpawnerData();
		} else {
			Main.plugin.getServer().unloadWorld(world_name, false);
		}
		
		if(!(f_save_as_template)) {
			deleteFolder(new File(world_name));
			deleteFolder(new File("plugins/WorldGuard/worlds/" + world_name));
			instance_loot.remove(world_name);
			instance_mob_spawns.remove(world_name);
			instance_loot_inv.remove(world_name);
		} else {
			try {
				new File("plugins/InstanceMechanics/dungeons/" + world_name + ".zip").delete();
				zipDirectory(new File(world_name), new File("plugins/InstanceMechanics/dungeons/" + world_name + ".zip"));
				deleteFolder(new File(world_name));
				log.info("[InstanceMechanics] Saved instance template for " + world_name + ".");
			} catch(IOException e) {
				e.printStackTrace();
			}
		}
		
		open_instances.remove(world_name);
		instance_loaded.remove(world_name);
		instance_timing.remove(world_name);
		total_mobs.remove(world_name);
		mob_kill_count.remove(world_name);
		log.info("[InstanceMechanics] Unloaded Instance: " + world_name);
		
	}
	
	public static String linkInstanceToParty(String instance_template, List<String> party, boolean template) {
		String new_instance = instance_template;
		
		if(!(template)) {
			new_instance = instance_template + "." + getNextInstanceNumber(instance_template);
		}
		
		for(String s : party) {
			if(Bukkit.getPlayer(s) != null) {
				log.info("Adding " + s + " to instance_party of " + new_instance);
				player_instance.put(s, new_instance);
			}
		}
		
		instance_party.put(new_instance, party);
		return new_instance;
	}
	
	public static int getNextInstanceNumber(String w_name) {
		int next_num = 0;
		File f = new File(w_name + "." + next_num);
		while(f.exists() || instance_loaded.containsKey(w_name + "." + next_num)) {
			next_num++;
			f = new File(w_name + "." + next_num);
		}
		
		return next_num;
	}
	
	public boolean instanceLoaded(String instance_name) {
		if(instance_loaded.containsKey(instance_name)) {
			if(instance_loaded.get(instance_name) == true) { return true; }
		}
		
		return false;
	}
	
	public static boolean isInstance(String world_name) {
		if(instance_loaded.containsKey(world_name) || world_name.contains(".") || new File("plugins/InstanceMechanics/dungeons/" + world_name + ".zip").exists()) { return true; }
		return false;
	}
	
	public static boolean isDungeonToken(ItemStack is) {
		if(is.getType() == Material.COAL && is.getDurability() == (short) 1 && is.hasItemMeta() && is.getItemMeta().hasDisplayName() && is.getItemMeta().getDisplayName().contains("Portal Key Fragment")) { return true; }
		return false;
	}
	
	public static List<Block> getNearbyBlocks(Location loc, int maxradius) {
		List<Block> return_list = new ArrayList<Block>();
		BlockFace[] faces = { BlockFace.UP, BlockFace.NORTH, BlockFace.EAST };
		BlockFace[][] orth = { { BlockFace.NORTH, BlockFace.EAST }, { BlockFace.UP, BlockFace.EAST }, { BlockFace.NORTH, BlockFace.UP } };
		for(int r = 0; r <= maxradius; r++) {
			for(int s = 0; s < 6; s++) {
				BlockFace f = faces[s % 3];
				BlockFace[] o = orth[s % 3];
				if(s >= 3) f = f.getOppositeFace();
				if(!(loc.getBlock().getRelative(f, r) == null)) {
					Block c = loc.getBlock().getRelative(f, r);
					
					for(int x = -r; x <= r; x++) {
						for(int y = -r; y <= r; y++) {
							Block a = c.getRelative(o[0], x).getRelative(o[1], y);
							return_list.add(a);
						}
					}
				}
			}
		}
		return return_list;
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if(cmd.getName().equalsIgnoreCase("instance")) {
			
		}
		
		return true; // Always return true.
	}
	
	public static void unzipArchive(File archive, File outputDir) {
		try {
			ZipFile zipfile = new ZipFile(archive);
			for(@SuppressWarnings("rawtypes")
			Enumeration e = zipfile.entries(); e.hasMoreElements();) {
				ZipEntry entry = (ZipEntry) e.nextElement();
				unzipEntry(zipfile, entry, outputDir);
			}
			zipfile.close();
		} catch(Exception e) {
			log.info("Error while extracting file " + archive);
		}
	}
	
	private static void unzipEntry(ZipFile zipfile, ZipEntry entry, File outputDir) throws IOException {
		if(entry.isDirectory()) {
			createDir(new File(outputDir, entry.getName()));
			return;
		}
		
		File outputFile = new File(outputDir, entry.getName());
		if(!outputFile.getParentFile().exists()) {
			createDir(outputFile.getParentFile());
		}
		
		BufferedInputStream inputStream = new BufferedInputStream(zipfile.getInputStream(entry));
		BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(outputFile));
		
		try {
			IOUtils.copy(inputStream, outputStream);
		} finally {
			outputStream.close();
			inputStream.close();
		}
	}
	
	public static final void zipDirectory(File directory, File zip) throws IOException {
		ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zip));
		zip(directory, directory, zos);
		zos.close();
	}
	
	private static final void zip(File directory, File base, ZipOutputStream zos) throws IOException {
		File[] files = directory.listFiles();
		byte[] buffer = new byte[8192];
		int read = 0;
		for(int i = 0, n = files.length; i < n; i++) {
			if(files[i].isDirectory()) {
				zip(files[i], base, zos);
			} else {
				FileInputStream in = new FileInputStream(files[i]);
				ZipEntry entry = new ZipEntry(files[i].getPath().substring(base.getPath().length() + 1));
				zos.putNextEntry(entry);
				while(-1 != (read = in.read(buffer))) {
					zos.write(buffer, 0, read);
				}
				in.close();
			}
		}
	}
	
	private static void createDir(File dir) {
		log.info("Creating dir " + dir.getName());
		if(!dir.mkdirs()) throw new RuntimeException("Can not create dir " + dir);
	}
	
	public static void deleteFolder(File folder) {
		File[] files = folder.listFiles();
		if(files != null) { //some JVMs return null for empty dirs
			for(File f : files) {
				if(f.isDirectory()) {
					deleteFolder(f);
				} else {
					f.delete();
				}
			}
		}
		folder.delete();
	}
	
}
