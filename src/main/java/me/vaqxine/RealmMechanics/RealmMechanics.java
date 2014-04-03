package me.vaqxine.RealmMechanics;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.security.CodeSource;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import me.Bogdacutu.VoidGenerator.VoidGeneratorGenerator;
import me.vaqxine.Main;
import me.vaqxine.AchievmentMechanics.AchievmentMechanics;
import me.vaqxine.ChatMechanics.ChatMechanics;
import me.vaqxine.CommunityMechanics.CommunityMechanics;
import me.vaqxine.DuelMechanics.DuelMechanics;
import me.vaqxine.EcashMechanics.EcashMechanics;
import me.vaqxine.FatigueMechanics.FatigueMechanics;
import me.vaqxine.HealthMechanics.HealthMechanics;
import me.vaqxine.Hive.Hive;
import me.vaqxine.Hive.ParticleEffect;
import me.vaqxine.InstanceMechanics.InstanceMechanics;
import me.vaqxine.ItemMechanics.ItemMechanics;
import me.vaqxine.KarmaMechanics.KarmaMechanics;
import me.vaqxine.LootMechanics.LootMechanics;
import me.vaqxine.ModerationMechanics.ModerationMechanics;
import me.vaqxine.MoneyMechanics.MoneyMechanics;
import me.vaqxine.MountMechanics.MountMechanics;
import me.vaqxine.PetMechanics.PetMechanics;
import me.vaqxine.RealmMechanics.commands.CommandRealm;
import me.vaqxine.RealmMechanics.commands.CommandResetRealm;
import me.vaqxine.RealmMechanics.commands.CommandSetRealmTier;
import me.vaqxine.RepairMechanics.RepairMechanics;
import me.vaqxine.ShopMechanics.ShopMechanics;
import me.vaqxine.SpawnMechanics.SpawnMechanics;
import me.vaqxine.TradeMechanics.TradeMechanics;
import me.vaqxine.TutorialMechanics.TutorialMechanics;
import net.minecraft.server.v1_7_R2.DataWatcher;
import net.minecraft.server.v1_7_R2.Packet;
import net.minecraft.server.v1_7_R2.PacketPlayOutEntityMetadata;
import net.minecraft.server.v1_7_R2.PacketPlayOutWorldEvent;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.craftbukkit.v1_7_R2.CraftServer;
import org.bukkit.craftbukkit.v1_7_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_7_R2.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_7_R2.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_7_R2.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_7_R2.inventory.CraftItemStack;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

public class RealmMechanics implements Listener {
	static Logger log = Logger.getLogger("Minecraft");

	public static boolean shutting_down = false;
	// Set to =true when onDisable() is run.

	// Realm Shop Items {
	public static ItemStack divider = ItemMechanics.signCustomItem(Material.THIN_GLASS, (short)0, " ", "");
	public static ItemStack next_page = ItemMechanics.signCustomItem(Material.ARROW, (short)0, ChatColor.YELLOW.toString() + "Next Page " + ChatColor.BOLD.toString() + "->", ChatColor.GRAY.toString() + "Page 1/2");
	public static ItemStack previous_page = ItemMechanics.signCustomItem(Material.ARROW, (short)0, ChatColor.YELLOW.toString() + ChatColor.BOLD + "<-" + ChatColor.YELLOW.toString() + " Previous Page ", ChatColor.GRAY.toString() + "Page 2/2");
	// }

	public static String rootDir = "";
	// Main directory of the server.

	private static final String ALPHA_NUM =  
			"123456789";  

	static List<String> locked_realms = new ArrayList<String>();
	// Not currently used, but determines of a realm is locked or not.

	static List<String> upgrading_realms = new ArrayList<String>();
	// List of realms being currently upgraded.

	public static List<String> uploading_realms = new ArrayList<String>();
	// Locks users in the list out of local server so the realm can be uploaded w/o interruption.

	static HashMap<Item, String> dropped_item_ownership = new HashMap<Item, String>();
	// Prevents using realm as a mule for items -- on player death any items with a certain player's ownership are deleted.
	static HashMap<Item, Long> dropped_item_timer = new HashMap<Item, Long>();
	//The timer for a dropped item
	static HashMap<Item, String> dropped_item_owner = new HashMap<Item, String>();
	//Dropped item owner
	public static HashMap<String, Integer> current_item_being_bought = new HashMap<String, Integer>();
	// Item being bought from the realm shop.

	public static HashMap<String, Integer> shop_page = new HashMap<String, Integer>();
	// The page the user is on in the realm shop -- #1/#2

	public static HashMap<String, String> shop_currency = new HashMap<String, String>();
	// ECash or Gems

	public static HashMap<String, Long> recent_movement = new HashMap<String, Long>();
	// Used to prevent players from placing portals ontop of AFK players. Contains time of last player movement.

	public static HashMap<String, Location> saved_locations = new HashMap<String, Location>();
	// Locations of all the places in the REAL world for players in realms. Used to TP them out.

	public static ConcurrentHashMap<Location, String> portal_map = new ConcurrentHashMap<Location, String>();
	// Location, Owner of Realm / Realm Name
	
	public static ConcurrentHashMap<String, String> portal_map_coords = new ConcurrentHashMap<String, String>();
	// Player Name, Location (x,y,z format) of the lower realm block.

	public static ConcurrentHashMap<String, Location> inv_portal_map = new ConcurrentHashMap<String, Location>();
	// PLAYER_NAME, Their realm portal location.

	public static HashMap<String, Boolean> has_portal = new HashMap<String, Boolean>();
	// Does a given player have a portal? (possibly depreciated)

	static HashMap<String, Long> portal_cooldown = new HashMap<String, Long>();
	// Cooldown for placing realm portals between servers.

	static HashMap<String, List<Player>> offline_player_realms = new HashMap<String, List<Player>>();
	// List of players in a realm of a player who has logged out. This list will be referenced in order to kick all the players out / warn them when the owner logs out.

	static HashMap<Player, String> realm_upgrade_codes = new HashMap<Player, String>();
	// Unique code to upgrade realm.

	static ConcurrentHashMap<String, String> realm_percent = new ConcurrentHashMap<String, String>();
	// % Complete of a realm upgrade.

	static ConcurrentHashMap<String, List<Location>> block_process_list = new ConcurrentHashMap<String, List<Location>>();
	// Lists of blocks to process on aSync thread for realm upgrades.

	public static HashMap<String, String> realm_title = new HashMap<String, String>();
	// Realm descriptions.

	public static HashMap<String, Integer> realm_tier = new HashMap<String, Integer>();
	// Realm tiers.

	public static HashMap<String, Integer> saved_levels = new HashMap<String, Integer>();
	// Saved player level (HP) for traveling between realms -- fixes a vanilla bug.

	public static volatile HashMap<String, Boolean> realm_loaded_status = new HashMap<String, Boolean>();
	// Setup on login, everytime players try to open a realm and this =true, it will update SQL.
	// TODO: Change this to sockets.

	public static ConcurrentHashMap<String, Long> safe_realms = new ConcurrentHashMap<String, Long>();
	// Realm name, The time is was set to safe + 60mins.

	public static ConcurrentHashMap<String, Long> flying_realms = new ConcurrentHashMap<String, Long>();
	// Realm name, The time is was set to flying + 30mins.

	public static HashMap<String, Long> realm_reset_cd = new HashMap<String, Long>();
	// Prevents abuse of /resetrealm.

	public static HashMap<String, List<String>> build_list = new HashMap<String, List<String>>();
	// List of builders for realms.
	// TODO: Make it permenant?

	public static List<String> ready_worlds = new ArrayList<String>();
	// Worlds ready for a bunch of misc. tasks such as chunk shortening, item drop managment, etc.

	public static List<String> corrupt_world = new ArrayList<String>();
	// Ensures a realm can attempt to DL/Load at least twice before being condemned as corrupt. 

	public static HashMap<String, Long> player_god_mode = new HashMap<String, Long>();
	// The player is in god mode, the time they were set to godmode.

	public static volatile CopyOnWriteArrayList<String> async_realm_status = new CopyOnWriteArrayList<String>();
	// Handles RealmStatusThread() queries. (isRealmLoadedSQL())

	public Thread RealmStatusThread;

	// Realm SHOP {
	public static Inventory mat_shop_1 = Bukkit.getServer().createInventory(null, 63, "Realm Material Store (1/3)");
	public static Inventory mat_shop_2 = Bukkit.getServer().createInventory(null, 63, "Realm Material Store (2/3)");
	public static Inventory mat_shop_3 = Bukkit.getServer().createInventory(null, 63, "Realm Material Store (3/3)");
	// }

	static RealmMechanics instance;
	public static String main_world_name = "";

	@SuppressWarnings("deprecation")
	public void onEnable() {
		instance = this;
		main_world_name = Bukkit.getWorlds().get(0).getName();

		Main.plugin.getCommand("realm").setExecutor(new CommandRealm());
		Main.plugin.getCommand("resetrealm").setExecutor(new CommandResetRealm());
		Main.plugin.getCommand("setrealmtier").setExecutor(new CommandSetRealmTier());
		
		setSystemPath();
		cleanupRealmFolders();

		Main.plugin.getServer().getPluginManager().registerEvents(this, Main.plugin);

		loadMatShop();
		convertMatShop(); // Converts durability to NBT Price tags.

		File f = new File(rootDir + "/realms/backup");
		deleteFolder(f); // Delete old backups.

		if(!(f.exists())){
			new File(rootDir + "/realms/up").mkdirs();
			new File(rootDir + "/realms/down").mkdirs();
			new File(rootDir + "/realms/backup").mkdirs();
			log.info("[RealmMechanics] Realm upload/download folders created.");
		}

		RealmStatusThread = new RealmStatusThread();
		RealmStatusThread.start();
		// Handles all isRealmLoaded() SQL queries and local memory updates for determing realm's loaded server.

		Main.plugin.getServer().getScheduler().scheduleSyncRepeatingTask(Main.plugin, new Runnable() {
			public void run() {
				for(final Player pl : Bukkit.getOnlinePlayers()){
					if(!pl.getWorld().getName().equalsIgnoreCase(main_world_name) && pl.getLocation().getY() < 0){
						if(saved_locations.containsKey(pl.getName())){
							String realm_name = pl.getWorld().getName();

							if(!(pl.getName().equalsIgnoreCase(pl.getWorld().getName()))){
								pl.sendMessage(ChatColor.LIGHT_PURPLE + "You have left " + ChatColor.BOLD + realm_name + "'s" + ChatColor.LIGHT_PURPLE + " realm.");
							}
							else{
								pl.sendMessage(ChatColor.LIGHT_PURPLE + "You have left " + ChatColor.BOLD + "YOUR" + ChatColor.LIGHT_PURPLE + " realm.");
							}

							player_god_mode.put(pl.getName(), System.currentTimeMillis());
							pl.setFallDistance(0.0F);
							pl.setAllowFlight(false);
							pl.teleport(saved_locations.get(pl.getName()));
							FatigueMechanics.sprinting.remove(pl);
							pl.setSprinting(false);
							
							Main.plugin.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
								public void run() {
									pl.closeInventory();
								}
							}, 2L);

							saved_locations.remove(pl.getName());

							Main.plugin.getServer().getScheduler().scheduleAsyncDelayedTask(Main.plugin, new Runnable() {
								public void run() {
									player_god_mode.remove(pl.getName());
								}
							}, 60L); 


						}
						else{
							if(InstanceMechanics.saved_location_instance.containsKey(pl.getName())){
								pl.teleport(InstanceMechanics.saved_location_instance.get(pl.getName()));
							}
							else if(pl.getWorld().getName().equalsIgnoreCase(Bukkit.getWorlds().get(0).getName()) || InstanceMechanics.isInstance(pl.getWorld().getName())){
								pl.teleport(SpawnMechanics.getRandomSpawnPoint(pl.getName()));
							}
						}


					}
				}
			}
		}, 10 * 20L, 1 * 20L);

		Main.plugin.getServer().getScheduler().scheduleSyncRepeatingTask(Main.plugin, new Runnable() {
			public void run() {
				for(Entry<String, Long> data : safe_realms.entrySet()){
					String r_name = data.getKey();
					long o_time = data.getValue();

					if((System.currentTimeMillis() - o_time) >= (3600 * 1000)){
						if(Bukkit.getWorld(r_name) != null){
							if(Bukkit.getPlayer(r_name) != null){
								Player pl = Bukkit.getPlayer(r_name);
								pl.sendMessage(ChatColor.RED + "Your realm is now once again a " + ChatColor.BOLD + "CHAOTIC" + ChatColor.RED + " zone.");
							}
							DuelMechanics.setPvPOn(Bukkit.getWorld(r_name));
						}
						safe_realms.remove(r_name);
						if(flying_realms.containsKey(r_name)){
							if(Bukkit.getPlayer(r_name) != null){
								Player pl = Bukkit.getPlayer(r_name);
								pl.sendMessage(ChatColor.GRAY + "Due to this, your " + ChatColor.UNDERLINE + "Orb of Flight" + ChatColor.GRAY + " has also expired.");
							}
							flying_realms.remove(r_name);
						}
					}
					else{
						if(Bukkit.getWorld(r_name) != null){
							World w = Bukkit.getWorld(r_name);
							try{
								ParticleEffect.sendToLocation(ParticleEffect.HAPPY_VILLAGER, w.getSpawnLocation().add(0.5D, 1.5D, 0.5D), 0, 0, 0, 0.02F, 20);
							} catch(Exception err){err.printStackTrace();}
							//w.spawnParticle(w.getSpawnLocation().add(0.5, 1.5, 0.5), Particle.HAPPY_VILLAGER, 0.02F, 20);
						}
					}
				}

				for(Entry<String, Long> data : flying_realms.entrySet()){
					try{
						String r_name = data.getKey();
						long o_time = data.getValue();

						if((System.currentTimeMillis() - o_time) >= (1800 * 1000)){
							if(Bukkit.getWorld(r_name) != null){
								if(Bukkit.getPlayer(r_name) != null){
									Player pl = Bukkit.getPlayer(r_name);
									pl.sendMessage(ChatColor.RED + "Your " + ChatColor.UNDERLINE + "Orb of Flight" + ChatColor.RED + " effect has expired.");
								}
								for(Player pl : Bukkit.getWorld(r_name).getPlayers()){
									pl.setAllowFlight(false);
								}
							}

							flying_realms.remove(r_name);
						}
						else if(Bukkit.getWorld(r_name) != null && Bukkit.getWorld(r_name).getPlayers() != null){
							for(Player pl : Bukkit.getWorld(r_name).getPlayers()){
								if(pl.getName().equalsIgnoreCase(pl.getWorld().getName()) || (build_list.containsKey(pl.getWorld().getName()) && build_list.get(pl.getWorld().getName()).contains(pl.getName()))){
									if(!(pl.getAllowFlight())){
										pl.setAllowFlight(true);
									}
								}
							}
							if(Bukkit.getWorld(r_name) != null){
								World w = Bukkit.getWorld(r_name);
								try{
									ParticleEffect.sendToLocation(ParticleEffect.CLOUD, w.getSpawnLocation().add(0.5D, 1.5D, 0.5D), 0, 0, 0, 0.02F, 20);
								} catch(Exception err){err.printStackTrace();}
								//w.spawnParticle(w.getSpawnLocation().add(0.5, 1.5, 0.5), Particle.CLOUD, 0.02F, 20);
							}
						}

					} catch(Exception err){
						err.printStackTrace();
						continue;
					}
				}
			}

		}, 10 * 20L, 3 * 20L);


		Main.plugin.getServer().getScheduler().scheduleSyncRepeatingTask(Main.plugin, new Runnable() {
			public void run() {
				for(Entry<String, String> data : portal_map_coords.entrySet()){
					String p_name = data.getKey();
					String loc_string = data.getValue();
					double x = Double.parseDouble(loc_string.split(",")[0]);
					double y = Double.parseDouble(loc_string.split(",")[1]);
					double z = Double.parseDouble(loc_string.split(",")[2]);
					
					Location loc = new Location(Main.plugin.getServer().getWorld(main_world_name), x, y, z).clone();
					final Location f_loc = loc.clone();
					try{
						if(Bukkit.getPlayer(p_name) != null){
							Player pl = Bukkit.getPlayer(p_name);
							if(pl.isOp()){
								try{
									ParticleEffect.sendToLocation(ParticleEffect.ENCHANTMENT_TABLE, loc.add(0.5D, 2D, 0.5D), 0, 0, 0, 0.25F, 75);
								} catch(Exception err){err.printStackTrace();}
								//loc.getWorld().spawnParticle(loc.add(0.5, 2, 0.5), Particle.ENCHANTMENT_TABLE, 0.25F, 75);
							}
							if(!(uploading_realms.contains(pl.getName())) && safe_realms.containsKey(pl.getName())){
							   loc.subtract(.5D, 2D, .5D);
								try{
									ParticleEffect.sendToLocation(ParticleEffect.HAPPY_VILLAGER, loc.add(0.5D, 1.5D, 0.5D), 0, 0, 0, 0.02F, 20);
									
								} catch(Exception err){err.printStackTrace();}
								//loc.getWorld().spawnParticle(loc.add(0.5, 1.5, 0.5), Particle.HAPPY_VILLAGER, 0.02F, 20);
							}
							if(!(uploading_realms.contains(pl.getName())) && flying_realms.containsKey(pl.getName())){
							    loc.subtract(.5D, 1.5D, .5D);
								try{
									ParticleEffect.sendToLocation(ParticleEffect.CLOUD, loc.add(0.5D, 1.5D, 0.5D), 0, 0,0 , 0.02F, 20);
								} catch(Exception err){err.printStackTrace();}
								//loc.getWorld().spawnParticle(loc.add(0.5, 1.5, 0.5), Particle.CLOUD, 0.02F, 20);
							}
						}
						
						f_loc.getBlock().setType(Material.PORTAL);
						f_loc.subtract(0, 1, 0).getBlock().setType(Material.PORTAL);
						if(portal_map.containsKey(p_name)){
							portal_map.put(loc, p_name); // Refresh this.
						}
						/*if(loc.subtract(0, 1, 0).getBlock().getType() == Material.AIR){
							loc.getBlock().setType(Material.PORTAL);
						}*/
						//loc.add(0, 1, 0);
					} catch(ConcurrentModificationException cme){
						cme.printStackTrace();
						continue;
					}
				}
			}
		}, 5 * 20L, 1 * 20L);

		Main.plugin.getServer().getScheduler().scheduleSyncRepeatingTask(Main.plugin, new Runnable() {
			public void run() {
				if(block_process_list.isEmpty()){return;}
				for (Map.Entry<String, List<Location>> entry : block_process_list.entrySet()) {
					String w_name = entry.getKey();
					try{
						World w = Bukkit.getWorld(w_name);
						int limy = (128 - getRealmSizeDimensions(getRealmTier(w.getName())));
						CopyOnWriteArrayList<Location> loc_list = new CopyOnWriteArrayList<Location>(entry.getValue());

						int x = 0;

						for(Location loc : loc_list){
							if(x >= 512){break;}
							if(loc.getBlock().getY() > 127){
								if(loc.getBlock().getType() == Material.AIR){
									loc.getBlock().setType(Material.GRASS);
								}
							}
							else if(loc.getBlock().getY() <= limy + 1){
								if(loc.getBlock().getType() == Material.AIR){
									loc.getBlock().setType(Material.BEDROCK);
								}

							}else{
								if(loc.getBlock().getType() == Material.AIR){
									loc.getBlock().setType(Material.DIRT);
								}
							}

							loc_list.remove(loc);
							x++;
						}

						if(loc_list.isEmpty()){
							Player p = Bukkit.getPlayer(w.getName());
							p.sendMessage(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "REALM UPGRADE COMPLETE.");
							p.playSound(p.getLocation(), Sound.LEVEL_UP, 1F, 1.25F);
							upgrading_realms.remove(w.getName());
							realm_percent.remove(w.getName());
							block_process_list.remove(w.getName());
						} else{
							block_process_list.put(w.getName(), loc_list);
							int total_area = getRealmSizeDimensions(getRealmTier(w.getName()) + 1);
							total_area = total_area * total_area * total_area;
							int complete_area = total_area - loc_list.size();

							double percent = (((double)complete_area / (double)total_area) * 100.0D);
							DecimalFormat oneDigit = new DecimalFormat("#.##");
							realm_percent.put(w.getName(), oneDigit.format(percent));
						}
					} catch(NullPointerException e){
						block_process_list.remove(w_name);
						continue;
					}
				}

			}
		}, 2 * 20L, 10L);

		Main.plugin.getServer().getScheduler().scheduleSyncRepeatingTask(Main.plugin, new Runnable() {

			public void run() {
				for(OfflinePlayer opl : Bukkit.getOperators()){
					if(opl.isOnline() && !(Main.isDev(opl.getName()))){
						if(!(player_god_mode.containsKey(opl.getName()))){
							player_god_mode.put(opl.getName(), System.currentTimeMillis() + 999999999);
							Player pl = (Player)opl.getPlayer();
							//pl.setLevel(9999);
							HealthMechanics.setPlayerHP(pl.getName(), 10000);
						}
					}
				}

				for(World w : Bukkit.getServer().getWorlds()){
					if(w.getName().equalsIgnoreCase(main_world_name)){
						continue;
					}
					if(InstanceMechanics.isInstance(w.getName())){
						continue;
					}

					if(w.getPlayers().size() <= 0 && ready_worlds.contains(w.getName()) && w.getLoadedChunks().length > 1){
						if(w.getEntities().size() > 0){
							for(Entity e : w.getEntities()){
								if(e.getType() == EntityType.DROPPED_ITEM){
									e.remove();
									Item i = (Item)e;
									dropped_item_ownership.remove(i);
									// Remove all dropped items if realm is empty, prevent abuse of storing mechanics.
								}
							}
						}

						try{
							for(Chunk c : w.getLoadedChunks()){
								c.unload(true, true);
							}
						} catch(Exception err){
							err.printStackTrace();
							continue; // Next world.
						}
						continue;
						// There are no players, no need for rest of function.
					}

					Location in_realm = w.getSpawnLocation();

					if(!(in_realm.getBlock().getType() == Material.PORTAL)){
						in_realm.getBlock().setType(Material.PORTAL);
					}

					in_realm.subtract(0, 1, 0);
					if(!(in_realm.getBlock().getType() == Material.PORTAL)){
						in_realm.getBlock().setType(Material.PORTAL);
					}

					in_realm.add(0, 1, 0);
				}
			}
		}, 1 * 20L, 4 * 20L);

		/*Thread realm_backup = new Thread(new Runnable(){
			public void run(){
				while(true){
					try {
						Thread.sleep(120 * 1000);
					} catch (InterruptedException e) {e.printStackTrace();}

					for(World w : getServer().getWorlds()){
						if(Hive.server_frozen == true){
							return;
						}

						if(w.getName().equalsIgnoreCase(RealmMechanics.main_world_name)){
							continue; // Main world, idc.
						}

						if(InstanceMechanics.isInstance(w.getName())){
							continue;
						}

						if(uploading_realms.contains(w.getName())){
							continue;
						}

						w.save();

						try {
							File prev_backup = new File(rootDir + "/realms/backup/" + w.getName() + ".zip");
							if(prev_backup.exists()){
								prev_backup.delete();
							}
							zipDirectory(new File(w.getName()), new File(rootDir + "/realms/backup/" + w.getName() + ".zip"));
						} catch (IOException e) {
							e.printStackTrace();
							continue;
						}

					}
				}
			}
		});

		realm_backup.start();*/

		log.info("[RealmMechanics] has been ENABLED. (V1.0_1)");
	}

	public void onDisable() {
		shutting_down = true;
		purgePortals();

		int attempts = 0;

		/*while(uploading_realms.size() > 0 && attempts <= 200){ // Try for 20 seconds.
			log.info("[RealmMechanics] WORLDS LOADED: " + Bukkit.getWorlds().size());
			log.info("[RealmMechanics] REALMS LOADED: " + uploading_realms.size());
			attempts++;
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}*/

		if(uploading_realms.size() > 0 || Bukkit.getWorlds().size() > 1){
			for(World w : Bukkit.getWorlds()){
				if(w.getName().contains("DungeonRealms")){
					continue;
				}

				log.info("[RealmMechanics] Found a loaded world that should have been uploaded, " + w.getName());
				final String w_name = w.getName();

				try{
					uploadWorld(w_name);
				} catch(NoSuchElementException nsee){
					attempts = 0;
					while(uploading_realms.contains(w_name) && attempts <= 10){
						attempts++;
						uploadWorld(w_name);
					}
					continue;
				}

			}
		}

		for(String w_name : offline_player_realms.keySet()){
			setRealmLoadStatusSQL(w_name, false);
			log.info("[RealmMechanics] Set realm " + w_name + " as 'OFFLINE' as server is rebooting.");
		}

		log.info("[RealmMechanics] has been disabled.");
	}


	public void cleanupRealmFolders(){
		File[] files = new File(rootDir).listFiles();
		for(File f : files){
			String name = f.getName().replace(".dat", "");
			if(f.isDirectory()){
				if(name.equalsIgnoreCase("logs") || name.equalsIgnoreCase("plugins") || name.contains("DungeonRealms") || name.contains("players_backup") || name.contains("plugins.old") || name.contains("realms") 
						|| name.equalsIgnoreCase("template") || name.equalsIgnoreCase("key")){
					continue; 
					// Don't delete these.
				}
				deleteFolder(f);
				log.info("[RealmMechanics] Cleaned up directory: " + name + " ... deleted.");
			}
		}
	}

	/*public void fakePlayerMoveEvent(){
		for(String s : inv_portal_map.keySet()){
			if(Bukkit.getPlayer(s) != null){
				Player p = Bukkit.getPlayer(s);
				if(p.getWorld().getName().equalsIgnoreCase(Bukkit.getWorlds().get(0).getName()) || InstanceMechanics.isInstance(p.getWorld().getName())){
					// They're in the main world.
					if(inv_portal_map.containsKey(p.getName())){
						Location portal_loc = inv_portal_map.get(p.getName());
						Location p_loc = p.getLocation();
						if(p_loc.distanceSquared(portal_loc) > 1024){
							portal_map.remove(portal_loc);
							inv_portal_map.remove(p.getName());
							portal_loc.getBlock().setType(Material.AIR);
							portal_loc.add(0, 1, 0).getBlock().setType(Material.AIR);

							p.sendMessage(ChatColor.LIGHT_PURPLE + "Your realm entrance portal has closed because you've traveled " + ChatColor.BOLD + ">32 blocks" + ChatColor.LIGHT_PURPLE + " away.");
							has_portal.remove(p.getName());
						}
					}
				}
			}
		}
	}*/

	public static void setRealmLoadStatusSQL(String p_name, boolean loaded){
		int load_var = 0;
		if(loaded == true){
			load_var = 1;
		}

		Hive.sql_query.add(
				"INSERT INTO player_database (p_name, realm_loaded)"
						+ " VALUES"
						+ "('"+ p_name + "', '"+ load_var +"') ON DUPLICATE KEY UPDATE realm_loaded = '" + load_var + "'");

	}

	// Depreciated, RealmStatusThread()
	/*public static boolean isRealmLoadedSQL(String p_name){
	}*/

	public int getSecondsOfWildernessLeft(String r_name){
		if(!(safe_realms.containsKey(r_name))){
			return 0;
		}
		long o_time = safe_realms.get(r_name);
		long seconds_left = 3600 - ((System.currentTimeMillis() - o_time) / 1000);
		int i_seconds_left = (int)seconds_left;
		return i_seconds_left;
	}

	public int getSecondsOfFlyingLeft(String r_name){
		if(!(flying_realms.containsKey(r_name))){
			return 0;
		}
		long o_time = flying_realms.get(r_name);
		long seconds_left = 1800 - ((System.currentTimeMillis() - o_time) / 1000);
		int i_seconds_left = (int)seconds_left;
		return i_seconds_left;
	}


	@SuppressWarnings("deprecation")
	public void loadMatShop(){
		mat_shop_1.setItem(0, new ItemStack(Material.DIRT, 1));
		mat_shop_1.setItem(1, new ItemStack(Material.SAND, 3));
		mat_shop_1.setItem(2, new ItemStack(Material.STONE, 3));
		mat_shop_1.setItem(3, new ItemStack(Material.LOG, 5, (short) 0));
		mat_shop_1.setItem(4, new ItemStack(Material.LOG, 6, (short) 1));
		mat_shop_1.setItem(5, new ItemStack(Material.LOG, 6, (short) 2));
		mat_shop_1.setItem(6, new ItemStack(Material.SANDSTONE, 10));
		mat_shop_1.setItem(7, new ItemStack(Material.SANDSTONE, 15, (short) 1));
		mat_shop_1.setItem(8, new ItemStack(Material.SANDSTONE, 25, (short) 2));
		mat_shop_1.setItem(9, new ItemStack(Material.WOOL, 3));
		mat_shop_1.setItem(10, new ItemStack(Material.WOOL, 6, (short) 1));
		mat_shop_1.setItem(11, new ItemStack(Material.WOOL, 6, (short) 2));
		mat_shop_1.setItem(12, new ItemStack(Material.GLASS, 5));
		mat_shop_1.setItem(13, new ItemStack(Material.GLOWSTONE, 10));
		mat_shop_1.setItem(14, new ItemStack(Material.REDSTONE, 10));
		mat_shop_1.setItem(15, new ItemStack(Material.COAL, 2));
		mat_shop_1.setItem(16, new ItemStack(Material.IRON_INGOT, 10));
		mat_shop_1.setItem(17, new ItemStack(Material.CLAY, 15));

		mat_shop_1.setItem(18, new ItemStack(Material.WOOL, 6, (short) 3));
		mat_shop_1.setItem(19, new ItemStack(Material.WOOL, 6, (short) 4));
		mat_shop_1.setItem(20, new ItemStack(Material.WOOL, 6, (short) 5));

		mat_shop_1.setItem(21, new ItemStack(Material.WATER, 5));
		mat_shop_1.setItem(30, new ItemStack(Material.LAVA, 15));

		mat_shop_1.setItem(22, new ItemStack(Material.ICE, 3));
		mat_shop_1.setItem(23, new ItemStack(Material.OBSIDIAN, 55));
		mat_shop_1.setItem(24, new ItemStack(Material.LAPIS_BLOCK, 40));
		mat_shop_1.setItem(25, new ItemStack(Material.QUARTZ, 50));
		mat_shop_1.setItem(26, new ItemStack(Material.FIRE, 10));

		mat_shop_1.setItem(27, new ItemStack(Material.WOOL, 6, (short) 6));
		mat_shop_1.setItem(28, new ItemStack(Material.WOOL, 6, (short) 7));
		mat_shop_1.setItem(29, new ItemStack(Material.WOOL, 6, (short) 8));

		mat_shop_1.setItem(31, new ItemStack(Material.MOSSY_COBBLESTONE, 10));
		mat_shop_1.setItem(32, new ItemStack(Material.COBBLESTONE, 3));
		mat_shop_1.setItem(33, new ItemStack(Material.SMOOTH_BRICK, 8));
		mat_shop_1.setItem(34, new ItemStack(Material.SMOOTH_BRICK, 7, (short) 1));
		mat_shop_1.setItem(35, new ItemStack(Material.LEAVES, 2));


		mat_shop_1.setItem(36, new ItemStack(Material.WOOL, 6, (short) 9));
		mat_shop_1.setItem(37, new ItemStack(Material.WOOL, 6, (short) 10));
		mat_shop_1.setItem(38, new ItemStack(Material.WOOL, 6, (short) 11));

		mat_shop_1.setItem(39, new ItemStack(Material.WEB, 20));
		mat_shop_1.setItem(40, new ItemStack(Material.SNOW_BLOCK, 10));
		mat_shop_1.setItem(41, new ItemStack(Material.SMOOTH_BRICK, 12, (short) 2));
		mat_shop_1.setItem(42, new ItemStack(Material.SMOOTH_BRICK, 12, (short) 3));
		mat_shop_1.setItem(43, new ItemStack(Material.NETHER_BRICK, 60));
		mat_shop_1.setItem(44, new ItemStack(Material.INK_SACK, 10, (short) 15));

		mat_shop_1.setItem(45, new ItemStack(Material.WOOL, 6, (short) 12));
		mat_shop_1.setItem(46, new ItemStack(Material.WOOL, 6, (short) 13));
		mat_shop_1.setItem(47, new ItemStack(Material.WOOL, 6, (short) 14));
		//mat_shop_1.setItem(48, new ItemStack(Material.PUMPKIN, 25));

		mat_shop_1.setItem(49, new ItemStack(Material.GRASS, 2));
		mat_shop_1.setItem(50, new ItemStack(Material.MYCEL, 35));
		mat_shop_1.setItem(51, new ItemStack(Material.SOUL_SAND, 55));
		mat_shop_1.setItem(52, new ItemStack(Material.NETHERRACK, 40));
		mat_shop_1.setItem(53, new ItemStack(Material.SPONGE, 30));

		mat_shop_2.setItem(0, new ItemStack(Material.CACTUS, 30));
		mat_shop_2.setItem(1, new ItemStack(Material.CLAY_BRICK, 10));
		mat_shop_2.setItem(2, new ItemStack(Material.LEAVES, 3, (short) 1));
		mat_shop_2.setItem(3, new ItemStack(Material.LEAVES, 3, (short) 2));
		mat_shop_2.setItem(4, new ItemStack(Material.LEAVES, 3, (short) 3));
		mat_shop_2.setItem(5, new ItemStack(Material.LOG, 8, (short) 3));
		mat_shop_2.setItem(6, new ItemStack(Material.RED_ROSE, 5));
		mat_shop_2.setItem(7, new ItemStack(Material.YELLOW_FLOWER, 5));
		mat_shop_2.setItem(8, new ItemStack(Material.LONG_GRASS, 5));

		mat_shop_2.setItem(9, new ItemStack(Material.DISPENSER, 64, (short)100));
		mat_shop_2.setItem(10, new ItemStack(Material.HOPPER, 64, (short)150));
		mat_shop_2.setItem(11, new ItemStack(Material.DROPPER, 64, (short)120));
		mat_shop_2.setItem(12, new ItemStack(Material.MINECART, 64, (short)500));
		mat_shop_2.setItem(13, new ItemStack(Material.BOOKSHELF, 50, (short)0));
		mat_shop_2.setItem(14, new ItemStack(Material.RAILS, 20));
		mat_shop_2.setItem(15, new ItemStack(Material.POWERED_RAIL, 20));
		mat_shop_2.setItem(16, new ItemStack(Material.ACTIVATOR_RAIL, 20));
		mat_shop_2.setItem(17, new ItemStack(111, 15));

		/*mat_shop_2.setItem(18, new ItemStack(Material.JUKEBOX, 64, (short)1200));
		mat_shop_2.setItem(19, new ItemStack(Material.RECORD_3, 64, (short)500));
		mat_shop_2.setItem(20, new ItemStack(Material.RECORD_4, 64, (short)500));
		mat_shop_2.setItem(21, new ItemStack(Material.RECORD_5, 64, (short)500));*/
		
		mat_shop_2.setItem(18, new ItemStack(Material.STAINED_CLAY, 8, (short)11));
		mat_shop_2.setItem(19, new ItemStack(Material.STAINED_CLAY, 8, (short)12));
		mat_shop_2.setItem(20, new ItemStack(Material.STAINED_CLAY, 8, (short)13));
		mat_shop_2.setItem(21, new ItemStack(Material.STAINED_CLAY, 8, (short)14));

		//mat_shop_2.setItem(22, new ItemStack(Material.PISTON_BASE, 64, (short)80));
		//mat_shop_2.setItem(23, new ItemStack(Material.PISTON_STICKY_BASE, 64, (short)120));
		mat_shop_2.setItem(22, new ItemStack(Material.SAPLING, 15, (short)1));
		mat_shop_2.setItem(23, new ItemStack(Material.SAPLING, 15, (short)2));
		mat_shop_2.setItem(24, new ItemStack(Material.SAPLING, 15, (short)3));


		mat_shop_2.setItem(25, new ItemStack(Material.QUARTZ_BLOCK, 50, (short)0));
		mat_shop_2.setItem(26, new ItemStack(Material.QUARTZ_BLOCK, 55, (short)1));
		mat_shop_2.setItem(27, new ItemStack(Material.QUARTZ_BLOCK, 60, (short)2));
		mat_shop_2.setItem(28, new ItemStack(Material.QUARTZ_BLOCK, 60, (short)3));
		mat_shop_2.setItem(29, new ItemStack(Material.QUARTZ_BLOCK, 60, (short)4));

		mat_shop_2.setItem(30, new ItemStack(397, 50, (short)0));
		mat_shop_2.setItem(31, new ItemStack(397, 50, (short)1));
		mat_shop_2.setItem(32, new ItemStack(397, 50, (short)2));
		mat_shop_2.setItem(33, new ItemStack(397, 50, (short)4));
		
		mat_shop_2.setItem(34, new ItemStack(Material.HAY_BLOCK, 30, (short)0));
		mat_shop_2.setItem(35, new ItemStack(Material.CARPET, 30, (short)0));
		mat_shop_2.setItem(36, new ItemStack(Material.HARD_CLAY, 55, (short)0));
		mat_shop_2.setItem(37, new ItemStack(Material.COAL_BLOCK, 45, (short)0));
		mat_shop_2.setItem(38, new ItemStack(Material.DAYLIGHT_DETECTOR, 200, (short)0));
		mat_shop_2.setItem(39, new ItemStack(Material.FLOWER_POT_ITEM, 45, (short)0));
		mat_shop_2.setItem(40, new ItemStack(Material.REDSTONE_LAMP_OFF, 55, (short)0));
		mat_shop_2.setItem(41, new ItemStack(Material.VINE, 50, (short)0));
		mat_shop_2.setItem(42, new ItemStack(Material.STAINED_CLAY, 8, (short)0));
		
		mat_shop_2.setItem(43, new ItemStack(Material.STAINED_CLAY, 8, (short)1));
		mat_shop_2.setItem(44, new ItemStack(Material.STAINED_CLAY, 8, (short)2));
		mat_shop_2.setItem(45, new ItemStack(Material.STAINED_CLAY, 8, (short)3));
		mat_shop_2.setItem(46, new ItemStack(Material.STAINED_CLAY, 8, (short)4));
		mat_shop_2.setItem(47, new ItemStack(Material.STAINED_CLAY, 8, (short)5));
		mat_shop_2.setItem(48, new ItemStack(Material.STAINED_CLAY, 8, (short)6));
		mat_shop_2.setItem(49, new ItemStack(Material.STAINED_CLAY, 8, (short)7));
		mat_shop_2.setItem(50, new ItemStack(Material.STAINED_CLAY, 8, (short)8));
		mat_shop_2.setItem(51, new ItemStack(Material.STAINED_CLAY, 8, (short)9));
		mat_shop_2.setItem(52, new ItemStack(Material.STAINED_CLAY, 8, (short)10));
		mat_shop_2.setItem(53, new ItemStack(Material.getMaterial(100), 15, (short)14));
		
		mat_shop_3.setItem(0, new ItemStack(Material.STAINED_CLAY, 8, (short)11));
		mat_shop_3.setItem(1, new ItemStack(Material.STAINED_CLAY, 8, (short)12));
		mat_shop_3.setItem(2, new ItemStack(Material.STAINED_CLAY, 8, (short)13));
		mat_shop_3.setItem(3, new ItemStack(Material.STAINED_CLAY, 8, (short)14));
		mat_shop_3.setItem(4, new ItemStack(Material.STAINED_CLAY, 8, (short)15));
		
		mat_shop_3.setItem(5, new ItemStack(Material.REDSTONE_ORE, 20, (short)0));
		mat_shop_3.setItem(6, new ItemStack(Material.BEDROCK, 60, (short)0));
		mat_shop_3.setItem(7, new ItemStack(Material.GRAVEL, 4, (short)0));
		mat_shop_3.setItem(8, new ItemStack(Material.getMaterial(99), 15, (short)0));
		mat_shop_3.setItem(9, new ItemStack(Material.getMaterial(99), 15, (short)15));
		mat_shop_3.setItem(10, new ItemStack(Material.getMaterial(99), 15, (short)14));
		//mat_shop_3.setItem(8, new ItemStack(Material.COAL_ORE, 10, (short)0));
		//mat_shop_3.setItem(9, new ItemStack(Material.IRON_ORE, 15, (short)0));
		//mat_shop_3.setItem(10, new ItemStack(Material.EMERALD_ORE, 20, (short)0));
		//mat_shop_3.setItem(11, new ItemStack(Material.DIAMOND_ORE, 30, (short)0));
		//mat_shop_3.setItem(12, new ItemStack(Material.GOLD_ORE, 50, (short)0));
		//mat_shop_3.setItem(13, new ItemStack(Material.REDSTONE_ORE, 20, (short)0));
		//mat_shop_3.setItem(14, new ItemStack(Material.BEDROCK, 60, (short)0));
	}

	public void convertMatShop(){
		int x = -1;
		while((x + 1) < mat_shop_1.getContents().length){
			x++;
			ItemStack is = mat_shop_1.getItem(x);
			if(is == null || is.getType() == Material.AIR){
				mat_shop_1.setItem(x, CraftItemStack.asCraftCopy(divider));
				continue;
			}
			double price_each = is.getAmount();
			double ecash_price = 1;
			if(price_each == 64){
				price_each = is.getDurability();
			}
			if(is.getType() == Material.HOPPER){
				price_each = 150;
			}
			if(is.getType() == Material.DROPPER){
				price_each = 120;
			}
			if(is.getType() == Material.DISPENSER){
				price_each = 100;
			}
			if(is.getType() == Material.JUKEBOX){
				price_each = 1200;
			}
			if(is.getType() == Material.DAYLIGHT_DETECTOR){
				price_each = 200;
			}

			is.setAmount(1);
			ecash_price = price_each / 20;
			price_each = price_each / 2;
			if(ecash_price > 1){
				ecash_price = Math.round(ecash_price);
			}
			if(ecash_price < 1){
				ecash_price = 1;
			}
			if(price_each < 1){
				price_each = 1;
			}
			ItemStack is_mod = Hive.setECASHPrice(ShopMechanics.setPrice(is, (int)price_each), ecash_price);
			List<String> lore = is_mod.getItemMeta().getLore();
			lore.add(ChatColor.ITALIC + "" + ChatColor.GRAY + "Left click to use gems, Right click to use E-CASH.");
			ItemMeta im = is_mod.getItemMeta();
			im.setLore(lore);
			is_mod.setItemMeta(im);
			mat_shop_1.setItem(x, is_mod);
		}

		x = -1;
		while((x + 1) < mat_shop_2.getContents().length){
			x++;
			ItemStack is = mat_shop_2.getItem(x);
			if(is == null || is.getType() == Material.AIR){
				mat_shop_2.setItem(x, CraftItemStack.asCraftCopy(divider));
				continue;
			}
			double price_each = is.getAmount();
			double ecash_price = 1;
			if(price_each == 64){
				price_each = is.getDurability();
			}
			if(is.getType() == Material.HOPPER){
				price_each = 150;
			}
			if(is.getType() == Material.DROPPER){
				price_each = 120;
			}
			if(is.getType() == Material.DISPENSER){
				price_each = 100;
			}
			if(is.getType() == Material.JUKEBOX){
				price_each = 1200;
			}
			if(is.getType() == Material.PISTON_BASE){
				price_each = 80;
			}
			if(is.getType() == Material.PISTON_STICKY_BASE){
				price_each = 120;
			}
			if(is.getType() == Material.DAYLIGHT_DETECTOR){
				price_each = 200;
			}
			
			is.setAmount(1);
			ecash_price = price_each / 20;
			price_each = price_each / 2;
			if(ecash_price > 1){
				ecash_price = Math.round(ecash_price);
			}
			if(ecash_price < 1){
				ecash_price = 1;
			}
			if(price_each < 1){
				price_each = 1;
			}
			ItemStack is_mod = Hive.setECASHPrice(ShopMechanics.setPrice(is, (int)price_each), ecash_price);
			List<String> lore = new ArrayList<String>(); 
			if(is_mod.hasItemMeta() && is_mod.getItemMeta().hasLore()){
				lore = is_mod.getItemMeta().getLore();
			}
			
			lore.add(ChatColor.ITALIC + "" + ChatColor.GRAY + "Left click to use gems, Right click to use E-CASH.");
			ItemMeta im = is_mod.getItemMeta();
			im.setLore(lore);
			is_mod.setItemMeta(im);
			mat_shop_2.setItem(x, is_mod);
		}
		
		x = -1;
		while((x + 1) < mat_shop_3.getContents().length){
			x++;
			ItemStack is = mat_shop_3.getItem(x);
			if(is == null || is.getType() == Material.AIR){
				mat_shop_3.setItem(x, CraftItemStack.asCraftCopy(divider));
				continue;
			}
			double price_each = is.getAmount();
			double ecash_price = 1;
			if(price_each == 64){
				price_each = is.getDurability();
			}
			if(is.getType() == Material.HOPPER){
				price_each = 150;
			}
			if(is.getType() == Material.DROPPER){
				price_each = 120;
			}
			if(is.getType() == Material.DISPENSER){
				price_each = 100;
			}
			if(is.getType() == Material.JUKEBOX){
				price_each = 1200;
			}
			if(is.getType() == Material.PISTON_BASE){
				price_each = 80;
			}
			if(is.getType() == Material.PISTON_STICKY_BASE){
				price_each = 120;
			}
			if(is.getType() == Material.DAYLIGHT_DETECTOR){
				price_each = 200;
			}
			
			is.setAmount(1);
			ecash_price = price_each / 20;
			price_each = price_each / 2;
			if(ecash_price > 1){
				ecash_price = Math.round(ecash_price);
			}
			if(ecash_price < 1){
				ecash_price = 1;
			}
			if(price_each < 1){
				price_each = 1;
			}
			ItemStack is_mod = Hive.setECASHPrice(ShopMechanics.setPrice(is, (int)price_each), ecash_price);
			List<String> lore = new ArrayList<String>(); 
			if(is_mod.hasItemMeta() && is_mod.getItemMeta().hasLore()){
				lore = is_mod.getItemMeta().getLore();
			}
			
			lore.add(ChatColor.ITALIC + "" + ChatColor.GRAY + "Left click to use gems, Right click to use E-CASH.");
			ItemMeta im = is_mod.getItemMeta();
			im.setLore(lore);
			is_mod.setItemMeta(im);
			mat_shop_3.setItem(x, is_mod);
		}

		mat_shop_1.setItem(62, next_page);
		mat_shop_2.setItem(54, previous_page);
		mat_shop_2.setItem(62, next_page);
		mat_shop_3.setItem(54, previous_page);
	}

	public static ItemStack fixItemName(ItemStack iss, String name){
		ItemMeta im = iss.getItemMeta();
		im.setDisplayName(name);
		iss.setItemMeta(im);
		return iss;
	}

	public static boolean isOrbOfPeace(ItemStack is){
		//public static ItemStack orb_of_peace = ItemMechanics.signNewCustomItem(Material.ENDER_PEARL, (short)1, ChatColor.AQUA.toString() + 
		//	"" + "Orb of Peace", ChatColor.GRAY.toString() + "Turns realm non-PVP for 1 hour(s).");
		if(is.getType() == Material.ENDER_PEARL && is.hasItemMeta() && is.getItemMeta().hasDisplayName() && is.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.GREEN.toString() + "" + "Orb of Peace")){
			return true;
		}
		return false;
	}

	@SuppressWarnings("deprecation")
	public static boolean isOrbOfFlight(ItemStack is){
		//public static ItemStack orb_of_peace = ItemMechanics.signNewCustomItem(Material.ENDER_PEARL, (short)1, ChatColor.AQUA.toString() + 
		//	"" + "Orb of Peace", ChatColor.GRAY.toString() + "Turns realm non-PVP for 1 hour(s).");
		if(is.getTypeId() == 402 && is.hasItemMeta() && is.getItemMeta().hasDisplayName() && is.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.AQUA.toString() + "" + "Orb of Flight")){
			return true;
		}
		return false;
	}

	public void purgePortals(){
		for (Map.Entry<Location, String> entry : portal_map.entrySet()){
			Location portal_loc = entry.getKey();
			portal_loc.getBlock().setType(Material.AIR);
			portal_loc.add(0, 1, 0).getBlock().setType(Material.AIR);
			String parse_loc = String.valueOf(portal_loc.getX() + "," + portal_loc.getY() + "," + portal_loc.getZ());
			log.info("[RealmMechanics] Cleaned up portal at: " + parse_loc);
		}
	}

	// Uploads realms on a server crash.
	public static void uploadLocalRealms(){
		File folder = new File(rootDir + "/realms/backup");
		for(File f : folder.listFiles()){
			if(!f.getName().endsWith(".zip")){continue;}
			try {
				String realm_name = f.getName().replaceAll(".zip", "");
				URL url = new URL("ftp://" + Hive.ftp_user + ":" + Hive.ftp_pass + "@" + Hive.Hive_IP + "/rdata/" + realm_name + ".zip");
				URLConnection urlc;

				urlc = url.openConnection();

				OutputStream out = urlc.getOutputStream(); 
				InputStream is = new FileInputStream(rootDir + "/realms/backup/" + realm_name + ".zip");

				byte buf[]=new byte[1024];
				int len;

				while((len=is.read(buf)) > 0){
					out.write(buf,0,len);
				}

				out.close();
				is.close();

				log.info("[RealmMechanics] Realm data for realm: " + realm_name + " uploaded.");
				f.delete();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void setSystemPath(){
		CodeSource codeSource = RealmMechanics.class.getProtectionDomain().getCodeSource();
		File jarFile = null;
		try {jarFile = new File(codeSource.getLocation().toURI().getPath());} catch (URISyntaxException e1) {}
		rootDir = jarFile.getParentFile().getPath();
		int rep = rootDir.contains("/plugins") ? rootDir.indexOf("/plugins") : rootDir.indexOf("\\plugins");
		rootDir = rootDir.substring(0, rep);
	}


	public void copyDirectory(File sourceLocation , File targetLocation) throws IOException {
		if (sourceLocation.isDirectory()) {
			if (!targetLocation.exists()) {
				targetLocation.mkdir();
			}

			String[] children = sourceLocation.list();
			for (int i=0; i<children.length; i++) {
				copyDirectory(new File(sourceLocation, children[i]),
						new File(targetLocation, children[i]));
			}
		} else {

			InputStream in = new FileInputStream(sourceLocation);
			OutputStream out = new FileOutputStream(targetLocation);

			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			in.close();
			out.close();
		}
	}

	public static void startPortalCooldown(String pname){
		long last_login = CommunityMechanics.getLastLogin(pname, false);
		long currentTime = System.currentTimeMillis();

		if((currentTime - last_login) < (140 * 1000)){ // Last login was within 2 minutes ago.
			portal_cooldown.put(pname, (currentTime - last_login));
		}
		else{
			portal_cooldown.remove(pname);
		}
	}

	public boolean cooldownOver(Player player){
		if(!portal_cooldown.containsKey(player.getName())){
			return true;
		}

		long oldTime = portal_cooldown.get(player.getName());
		long currentTime = System.currentTimeMillis();

		if((currentTime - oldTime) >= (140 * 1000)){
			return true;
		} else {
			return false;
		}
	}

	public String getItemName(ItemStack i){
		//CraftItemStack css = new CraftItemStack(i);
		//String name = css.getHandle().getTag().getCompound("display").getString("Name");
		String name = i.getItemMeta().getDisplayName();
		if(name.contains(ChatColor.WHITE.toString())){
			name.replaceAll(ChatColor.WHITE.toString(), "");
		}
		if(name.contains(ChatColor.GREEN.toString())){
			name.replaceAll(ChatColor.GREEN.toString(), "");
		}
		if(name.contains(ChatColor.AQUA.toString())){
			name.replaceAll(ChatColor.AQUA.toString(), "");
		}
		if(name.contains(ChatColor.LIGHT_PURPLE.toString())){
			name.replaceAll(ChatColor.LIGHT_PURPLE.toString(), "");
		}
		if(name.contains(ChatColor.YELLOW.toString())){
			name.replaceAll(ChatColor.YELLOW.toString(), "");
		}

		return name;
	}

	@SuppressWarnings("deprecation")
	public static void handle2MinCD(final String p_name, World p_realm){
		List<Player> plist = new ArrayList<Player>();
		for(Player pl : p_realm.getPlayers()){
			pl.sendMessage(ChatColor.RED + "The owner of this realm has LOGGED OUT.");
			pl.sendMessage(ChatColor.RED + "You will be kicked out of the realm immediately.");
			plist.add(pl);
		}

		uploading_realms.remove(p_realm.getName());
		offline_player_realms.put(p_name, plist);

		setRealmLoadStatusSQL(p_name, true);

		Main.plugin.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
			public void run() {
				if(Bukkit.getServer().getWorld(p_name) == null){
					return; // World ain't loaded anymore.
				}
				if(!offline_player_realms.containsKey(p_name)){
					// The owner of the realm has logged back in, therfore this function is no longer necessary. Goodbye.
					return;
				}

				uploading_realms.add(p_name);
				// Lock the player out of server at this point, so he can't fuck anything up durring the upload.

				World p_realm = Bukkit.getWorld(p_name);

				for(Player pl : p_realm.getPlayers()){
					pl.sendMessage(ChatColor.RED + "You have been kicked out of the realm.");
					if(saved_locations.containsKey(pl.getName())){
						Location l = saved_locations.get(pl.getName());
						pl.teleport(l);
					}
					else{
						pl.teleport(SpawnMechanics.getRandomSpawnPoint(pl.getName()));
					}
				}

				final String p_realm_name = p_realm.getName();

				Thread t = new Thread(new Runnable(){
					public void run(){
						uploadWorld(p_realm_name);
					}
				});

				t.start();

			}
		}, 1l);
	}

	public boolean isShopBlock(Material m){
		if(m == Material.DIRT || m == Material.SAND || m == Material.WOOL || m == Material.STONE || m == Material.LOG || m == Material.SANDSTONE || m == Material.GLOWSTONE || m == Material.ICE ||
				m == Material.LOG || m == Material.CLAY || m == Material.OBSIDIAN || m == Material.LAPIS_BLOCK || m == Material.MOSSY_COBBLESTONE || m == Material.SMOOTH_BRICK || m == Material.SNOW_BLOCK || m == Material.PUMPKIN || m == Material.NETHER_BRICK ||
				m == Material.MELON_BLOCK || m == Material.MYCEL || m == Material.SOUL_SAND || m == Material.NETHER_BRICK || m == Material.SPONGE){
			return true;
		}
		return false;
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
	public void onBlockBreak(BlockBreakEvent e){
		Player p = e.getPlayer();
		if(p.getGameMode() == GameMode.CREATIVE){return;}
		if(p.getWorld().getName().equalsIgnoreCase(main_world_name) || InstanceMechanics.isInstance(p.getWorld().getName())){return;}
		if(!p.getWorld().getName().equalsIgnoreCase(p.getName())){
			Block b = e.getBlock();
			String realm_owner = b.getWorld().getName();
			e.setCancelled(true);

			if(!(build_list.containsKey(realm_owner) && build_list.get(realm_owner).contains(p.getName())) && !(p.getWorld().getName().equalsIgnoreCase(main_world_name))){
				p.sendMessage(ChatColor.RED + "You aren't authorized to build in " + realm_owner + "'s realm.");
				p.sendMessage(ChatColor.GRAY + realm_owner + " will have to " + ChatColor.UNDERLINE + "Sneak Left Click" + ChatColor.GRAY + " you with their Realm Portal Rune to add you to their builder list.");
				return;
			}
		}

		if(upgrading_realms.contains(p.getWorld().getName())){
			e.setCancelled(true);
			p.sendMessage(ChatColor.YELLOW + "Block events are disabled while your realm upgrades.");
			return;
		}

		e.setCancelled(true);
		return;

	}


	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerQuit(PlayerQuitEvent e) throws IOException{
		final Player p = e.getPlayer();
		has_portal.remove(p.getName());
		World orig_w = p.getWorld();

		if(inv_portal_map.containsKey(p.getName())){
			Location l = inv_portal_map.get(p.getName());
			portal_map_coords.remove(p.getName());
			inv_portal_map.remove(p.getName());
			portal_map.remove(l);
			l.getBlock().setType(Material.AIR);
			l.subtract(0, 1, 0).getBlock().setType(Material.AIR);
		}

		if(!(orig_w.getName().equalsIgnoreCase(main_world_name))){
			if(saved_locations.containsKey(p.getName())){
				//final String pname = p.getName();
				Location safe = saved_locations.get(p.getName());
				p.teleport(safe);
				saved_locations.remove(p.getName());
			}
			else{
				p.teleport(SpawnMechanics.getRandomSpawnPoint(p.getName()));
			}

			if(Hive.server_frozen == false){
				// Cannot saveData on playerQuit if main thread is frozen...
				p.saveData();
			}
		}


		if(saved_locations.containsKey(p.getName())){
			saved_locations.remove(p.getName()); // Make sure it's gone.
		}

		if(!doesWorldExistLocal(p.getName())){
			return;
			// If the player doesn't have a realm, we don't need to do anything else.
		}

		if(isWorldLoaded(p.getName())){
			// The player has a world and it's loaded, so we need to unload/process it before doing anything else.
			upgrading_realms.remove(p.getName());
			realm_percent.remove(p.getName());
			block_process_list.remove(p.getName());
			ready_worlds.remove(p.getName());

			uploading_realms.add(p.getName());
			// The processing takes place in Hive's multithreaded QUIT event.
		}

	}

	public static void uploadWorld(String world_name){
		try{
			World w = Bukkit.getWorld(world_name);

			safe_realms.remove(world_name);
			flying_realms.remove(world_name);
			build_list.remove(world_name);

			Bukkit.unloadWorld(w, true);

			zipDirectory(new File(world_name), new File(rootDir + "/realms/up/" + world_name + ".zip"));
			log.info("[RealmMechanics] " + world_name + "'s realm is now uploading...");

			URL url = new URL("ftp://" + Hive.ftp_user + ":" + Hive.ftp_pass + "@" + Hive.Hive_IP + "/rdata/" + world_name + ".zip");
			URLConnection urlc = url.openConnection();
			OutputStream out = urlc.getOutputStream(); 

			InputStream is = new FileInputStream(rootDir + "/realms/up/" + world_name + ".zip");

			byte buf[]=new byte[1024];
			int len;

			while((len=is.read(buf)) > 0){
				out.write(buf,0,len);
			}

			out.close();
			is.close();

			new File(rootDir + "/realms/down/" + world_name + ".zip").delete();

			log.info("[RealmMechanics] Uploaded realm for " + world_name);
			uploading_realms.remove(world_name);

		} catch (Exception ex) {
			ex.printStackTrace();
			log.info("[RealmMechanics] Fatal error. Major shit went wrong."); 
		}

		setRealmLoadStatusSQL(world_name, false);
	}



	public static void uploadWorld(String p_name, String world_name){

		if(!(uploading_realms.contains(p_name))){
			uploading_realms.add(p_name);
		}

		safe_realms.remove(world_name);

		World p_realm = null;
		if(Bukkit.getWorld(world_name) == null){
			return; // No such world.
		}
		p_realm = Bukkit.getWorld(world_name);

		String p_realm_name = "";
		if(p_realm != null){
			p_realm_name = p_realm.getName(); // Need to store the world name since we're going to unload it.
			if(p_realm.getPlayers().size() >= 1 && Hive.shutting_down == false){
				// There are some players left in the realm, we give them a 2 minute grace period.
				if(!offline_player_realms.containsKey(p_name) && shutting_down == false){
					handle2MinCD(p_name, p_realm);
					return;
				}
			}

			if(p_realm.getPlayers().size() <= 0 || Hive.shutting_down == true){
				// No players left in the realm, let's close this bitch.

				p_realm.setTime(0);
				Bukkit.unloadWorld(p_realm, true);
			}
		}

		final String p_safe_realm_name = p_realm_name;
		locked_realms.remove(p_name);

		if(shutting_down == false){
			try{
				//Thread.sleep(1000);
				zipDirectory(new File(rootDir + "/" + p_safe_realm_name), new File(rootDir + "/realms/up/" + p_safe_realm_name + ".zip"));
				log.info("[RealmMechanics] " + p_safe_realm_name + "'s realm is now uploading...");

				URL url = new URL("ftp://" + Hive.ftp_user + ":" + Hive.ftp_pass + "@" + Hive.Hive_IP + "/rdata/" + p_safe_realm_name + ".zip");
				URLConnection urlc = url.openConnection();
				OutputStream out = urlc.getOutputStream(); 

				InputStream is = new FileInputStream(rootDir + "/realms/up/" + p_safe_realm_name + ".zip");

				byte buf[]=new byte[1024];
				int len;

				while((len=is.read(buf)) > 0){
					out.write(buf,0,len);
				}

				out.close();
				is.close();

				new File(rootDir + "/realms/down/" + p_safe_realm_name + ".zip").delete();
				// We can delete this if the server isn't offline, cause it will have been uploaded.


				log.info("[RealmMechanics] Uploaded realm for " + p_safe_realm_name);

			} catch (Exception ex) {
				ex.printStackTrace();
				log.info("[RealmMechanics] Fatal error. Major shit went wrong.");
			}

			uploading_realms.remove(p_safe_realm_name);
		}

		if(shutting_down == true){
			try{
				zipDirectory(new File(rootDir + "/" + p_safe_realm_name), new File(rootDir + "/realms/up/" + p_safe_realm_name + ".zip"));
				log.info("[RealmMechanics] " + p_safe_realm_name + "'s realm is now uploading...");

				URL url = new URL("ftp://" + Hive.ftp_user + ":" + Hive.ftp_pass + "@" + Hive.Hive_IP + "/rdata/" + p_safe_realm_name + ".zip");
				URLConnection urlc = url.openConnection();
				OutputStream out = urlc.getOutputStream(); 

				InputStream is = new FileInputStream(rootDir + "/realms/up/" + p_safe_realm_name + ".zip");

				byte buf[]=new byte[1024];
				int len;

				while((len=is.read(buf)) > 0){
					out.write(buf,0,len);
				}

				out.close();
				is.close();

				new File(rootDir + "/realms/down/" + p_safe_realm_name + ".zip").delete();
				// Delete download copy. We'll delete /up/ on next login.

				log.info("[RealmMechanics] Uploaded realm for " + p_safe_realm_name);

			} catch (Exception ex) {
				ex.printStackTrace();
				log.info("[RealmMechanics] Fatal error. Major shit went wrong."); 	
			}

			uploading_realms.remove(p_safe_realm_name);
		}

		return;
	}

	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockPlaceEvent(BlockPlaceEvent e){
		Player p = e.getPlayer();
		if(p.getGameMode() == GameMode.CREATIVE){return;}
		String block_world_name = e.getBlock().getWorld().getName();
		if(e.getBlock().getType() == Material.PORTAL){
			e.setCancelled(true);
			return;
		}
		
		if(!p.isOp() && (e.getBlock().getType() == Material.TRAPPED_CHEST || e.getBlock().getType() == Material.GOLD_BLOCK)){
			if(e.getBlock().getType() == Material.TRAPPED_CHEST){
				p.sendMessage(ChatColor.RED + "You " + ChatColor.UNDERLINE + "cannot" + ChatColor.RED + " place this " + e.getBlock().getType().name().toUpperCase() + " as it is an illegal item.");
			}
			p.updateInventory();
			e.setCancelled(true);
			return;
		}

		if(block_world_name.equalsIgnoreCase(main_world_name) || InstanceMechanics.isInstance(p.getWorld().getName())){
			return;// RestrictionMechanics.
		}

		if(EcashMechanics.isMusicBox(e.getItemInHand())){
			return;
		}

		String realm_owner = block_world_name;
		if(block_world_name.equalsIgnoreCase(p.getName()) || (build_list.containsKey(realm_owner) && build_list.get(realm_owner).contains(p.getName()))){
			if(upgrading_realms.contains(block_world_name)){
				e.setCancelled(true);
				p.updateInventory();
				p.sendMessage(ChatColor.YELLOW + "Block events are disabled while your realm upgrades.");
				return;
			}
			// Their own realm.
			int realm_tier = getRealmTier(block_world_name);
			int max_size = getRealmSizeDimensions(realm_tier) + 16; // Add 16, because the default chunk (0,0) is never used, and 16 is lowest you can be.

			int max_y = 128; // + (max_size / 2)
			Block b = e.getBlock();
			if(!(p.isOp())){
				if(Math.round(b.getX() - 0.5) > max_size || Math.round(b.getX() - 0.5) < 16 || Math.round(b.getZ() - 0.5) > max_size || Math.round(b.getZ() - 0.5) < 16 || (b.getY() > (max_y + (max_size) + 1)) || (b.getY() < (max_y - (max_size) - 1))){
					e.setCancelled(true);
					p.updateInventory();
					return;
				}
			}
			if(e.getBlock().getType() == Material.ITEM_FRAME){
				e.setCancelled(true);
				p.updateInventory();
				return;
			}
			if(p.getItemInHand() != null && RepairMechanics.isArmorScrap(p.getItemInHand())){
				e.setCancelled(true);
				p.updateInventory();
				return;
			}

			e.setCancelled(false);
			if(e.getBlock().getTypeId() == 78){
				int amount = p.getItemInHand().getAmount();
				if(amount > 1){
					p.getItemInHand().setAmount(amount - 1);
				}
				else{
					p.setItemInHand(new ItemStack(Material.AIR));
				}
			}
			return;
		}

		e.setCancelled(true);
		p.updateInventory();

		if(!(p.getWorld().getName().equalsIgnoreCase(main_world_name))){
			p.sendMessage(ChatColor.RED + "You aren't authorized to build in " + block_world_name + "'s realm.");
			p.sendMessage(ChatColor.GRAY + block_world_name + " will have to " + ChatColor.UNDERLINE + "Sneak Left Click" + ChatColor.GRAY + " you with their Realm Portal Rune to add you to their builder list.");
		}
	}


	@EventHandler
	public void onPlayerChatEvent(AsyncPlayerChatEvent e){
		Player p = e.getPlayer();
		String auth_code = getUpgradeAuthenticationCode(p);
		int new_tier = 0;
		if(auth_code == null){return;}
		e.setCancelled(true);

		if(e.getMessage().contains(auth_code)){
			new_tier = Integer.parseInt(auth_code.substring(0, 1));
			int cost = getRealmUpgradeCost(new_tier);
			if(!(doTheyHaveEnoughMoney(p, cost))){
				p.sendMessage(ChatColor.RED + "You do not have enough GEM(s) to purchase this upgrade. Upgrade cancelled.");
				p.sendMessage(ChatColor.RED + "COST: " + cost + " Gem(s)");
				realm_upgrade_codes.remove(p);
				return;
			}

			subtractMoney(p, cost);
			final int tier = new_tier;
			final Player player = p;
			new BukkitRunnable(){
			    public void run(){
			        upgradeRealm(player, tier, true);
			    }
			}.runTask(Main.plugin);
			
			realm_upgrade_codes.remove(p);
			p.sendMessage("");
			/*p.sendMessage(ChatColor.LIGHT_PURPLE + "*** REALM UPGRADE TO TIER " + new_tier + " ACTIVATED ***");
			p.sendMessage(ChatColor.GRAY + "0%");*/
			upgrading_realms.add(p.getWorld().getName());
		}
		else{
			p.sendMessage(ChatColor.RED + "Invalid authentication code entered. Realm upgrade cancelled.");
			realm_upgrade_codes.remove(p);
		}
	}

	@SuppressWarnings({ "deprecation", "static-access" })
	@EventHandler
	public void onPlayerEnterAmount(AsyncPlayerChatEvent e){
		if(!(current_item_being_bought.containsKey(e.getPlayer().getName()))){
			return;
		}

		e.setCancelled(true);
		Player p = e.getPlayer();

		if (e.getMessage().equalsIgnoreCase("cancel")) {
			p.sendMessage(ChatColor.RED + "Realm Shop Purchase - " + ChatColor.BOLD + "CANCELLED.");
			current_item_being_bought.remove(p.getName());
			shop_page.remove(p.getName());
			p.updateInventory();
			return;
		}

		if(p.getInventory().firstEmpty() == -1){
			p.sendMessage(ChatColor.RED + "No space available in inventory. Type 'cancel' or clear some room.");
			return;
		}

		int amount_to_buy = 0;
		try{
			amount_to_buy = Integer.parseInt(e.getMessage());
		} catch(NumberFormatException nfe){
			p.sendMessage(ChatColor.RED
					+ "Please enter a valid integer, or type 'cancel' to void this item purchase.");
			return;
		}

		if(amount_to_buy > 64){
			p.sendMessage(ChatColor.RED + "You " + ChatColor.UNDERLINE + "cannot" + ChatColor.RED + " buy MORE than " + ChatColor.BOLD + "64x" + ChatColor.RED + " of a material per transaction.");
			return;
		}

		if(amount_to_buy <= 0){
			p.sendMessage(ChatColor.RED + "You " + ChatColor.UNDERLINE + "cannot" + ChatColor.RED + " buy LESS than " + ChatColor.BOLD + "1x" + ChatColor.RED + " of a material per transaction.");
			return;
		}

		int slot = current_item_being_bought.get(p.getName());
		int shop_page = this.shop_page.get(p.getName());
		ItemStack i = null;

		if(shop_page == 1){
			i = mat_shop_1.getItem(slot);
		}
		else if (shop_page == 2){
			i = mat_shop_2.getItem(slot);
		}
		else if (shop_page == 3){
			i = mat_shop_3.getItem(slot);
		}
		
		String currency = shop_currency.get(p.getName());

		if(currency.equalsIgnoreCase("gems")){
			int price_per = ShopMechanics.getPrice(i);
			int total_price = amount_to_buy * price_per;

			if(!doTheyHaveEnoughMoney(p, total_price)){
				p.sendMessage(ChatColor.RED + "You do not have enough GEM(s) to complete this purchase.");
				p.sendMessage(ChatColor.GRAY + "" + amount_to_buy + " X " + price_per + " gem(s)/ea = " + total_price + " gem(s).");
				return;	
			}

			subtractMoney(p, total_price);
			p.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "-" + ChatColor.RED + total_price + ChatColor.BOLD + "G");
			p.sendMessage(ChatColor.GREEN + "Transaction successful.");

			p.getInventory().setItem(p.getInventory().firstEmpty(), new ItemStack(i.getType(), amount_to_buy, i.getDurability()));
		}
		else if(currency.equalsIgnoreCase("ecash")){
			double price_per = Hive.getECASHPrice(i);
			double per_ecash = 1;
			if(price_per < 1){
				per_ecash = Math.round((1 / (price_per)));
			}
			int total_price = (int)(amount_to_buy * price_per);
			amount_to_buy = (int)(amount_to_buy * per_ecash);

			if(!Hive.doTheyHaveEnoughECASH(p.getName(), total_price)){
				p.sendMessage(ChatColor.RED + "You do not have enough E-CASH to complete this purchase.");
				p.sendMessage(ChatColor.GRAY + "" + amount_to_buy + " X " + price_per + " EC/ea = " + total_price + " EC.");
				p.sendMessage(ChatColor.GRAY + "Purchase more at store.dungeonrealms.net -- instant delivery!");
				return;	
			}

			double needed_slots = amount_to_buy / 64;
			double available_slots = 0;
			for(ItemStack is : p.getInventory().getContents()){
				if(is == null || is.getType() == Material.AIR){
					available_slots++;
				}
			}

			if(needed_slots > available_slots){
				p.sendMessage(ChatColor.RED + "Not enough space available in inventory. Type 'cancel' or clear some room.");
				p.sendMessage(ChatColor.GRAY + "You will need " + needed_slots + " slots.");
				return;
			}

			int net_ecash = Hive.player_ecash.get(p.getName());
			net_ecash -= total_price;

			Hive.player_ecash.put(p.getName(), net_ecash);

			final String fp_name = p.getName();
			final int fnet_ecash = net_ecash;

			EcashMechanics.setECASH_SQL(fp_name, fnet_ecash);
			/*Thread t = new Thread(new Runnable(){
				public void run(){
					DonationMechanics.setECASH_SQL(fp_name, fnet_ecash);
				}
			});
			t.start();*/

			p.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "-" + ChatColor.RED + total_price + ChatColor.BOLD + " E-CASH");
			p.sendMessage(ChatColor.GREEN + "Transaction successful.");

			while(amount_to_buy > 64){
				amount_to_buy -= 64;
				p.getInventory().setItem(p.getInventory().firstEmpty(), makeUntradeable(new ItemStack(i.getType(), 64, i.getDurability())));
			}
			if(amount_to_buy > 0){
				p.getInventory().setItem(p.getInventory().firstEmpty(), makeUntradeable(new ItemStack(i.getType(), amount_to_buy, i.getDurability())));
			}
		}

		//p.getInventory().setItem(p.getInventory().firstEmpty(), makeUntradeable(new ItemStack(i.getType(), amount_to_buy, i.getDurability())));


		current_item_being_bought.remove(p.getName());
		shop_currency.remove(p.getName());
		RealmMechanics.shop_page.remove(p.getName());


		p.updateInventory();
	}

	public int getAvailableSlots(Inventory i){
		int count = 0;
		for(ItemStack is : i.getContents()){
			if(is == null || is.getType() == Material.AIR){
				count++;
			}
		}
		return count;
	}

	public int getUsedSlots(Inventory i){
		int count = 0;
		for(ItemStack is : i.getContents()){
			if(is != null && is.getType() != Material.AIR){
				count++;
			}
		}
		return count;
	}


	public boolean isTherePortalLocationNear(Location loc, int radius){
		double rad = Math.pow(radius, 2);
		for(Location p_loc : portal_map.keySet()){
			if(p_loc.distanceSquared(loc) <= rad){
				return true;
			}
		}
		return false;
	}

	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerCreativeBlockBreak(PlayerInteractEvent e){
		Player p = e.getPlayer();
		if(!(e.hasBlock()) || e.getAction() != Action.LEFT_CLICK_BLOCK){
			return;
		}
		if(p.getGameMode() == GameMode.CREATIVE){return;}
		if(p.getWorld().getName().equalsIgnoreCase(main_world_name) || InstanceMechanics.isInstance(p.getWorld().getName())){return;}
		if(!p.getWorld().getName().equalsIgnoreCase(p.getName())){
			Block b = e.getClickedBlock();
			String realm_owner = b.getWorld().getName();
			e.setCancelled(true);

			if(!(build_list.containsKey(realm_owner) && build_list.get(realm_owner).contains(p.getName())) && !(p.getWorld().getName().equalsIgnoreCase(main_world_name))){
				//p.sendMessage(ChatColor.RED + "You aren't authorized to build in " + realm_owner + "'s realm.");
				//p.sendMessage(ChatColor.GRAY + realm_owner + " will have to " + ChatColor.UNDERLINE + "Sneak Left Click" + ChatColor.GRAY + " you with their Realm Portal Rune to add you to their builder list.");
				return;
			}
		}
		Block b = e.getClickedBlock();
		if(upgrading_realms.contains(p.getWorld().getName())){
			e.setCancelled(true);
			//p.sendMessage(ChatColor.YELLOW + "Block events are disabled while your realm upgrades.");
			return; // Let the block break event deal with it.
		}
		
		e.setCancelled(true);
		Material m = b.getType();
		if(m == Material.AIR || m == Material.PORTAL){return;}
		ItemStack loot = (new ItemStack(b.getType(), 1, b.getData()));
		//ItemStack loot = makeUntradeable(new ItemStack(b.getType(), 1, b.getData()));

		if(b.getType() == Material.WATER || b.getType() == Material.STATIONARY_WATER || b.getType() == Material.LAVA || b.getType() == Material.STATIONARY_LAVA){
			return; // Don't break water / lava, use bucket.
		}

		if(DuelMechanics.duel_map.containsKey(p.getName())){
			return;
		}
		
		if(b.getType() == Material.CHEST){ //  || b.getType() == Material.TRAPPED_CHEST - Idiots abused a bug to get infinite trapped chests.
			Chest c = (Chest)b.getState();
			Inventory c_inv = c.getInventory();
			int in_chest = getUsedSlots(c_inv);
			int available_on_player = getAvailableSlots(p.getInventory());
			if(in_chest > available_on_player){
				p.sendMessage(ChatColor.RED + "You do not have enough room in your inventory for all the items in this chest.");
				p.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + "REQ: " + in_chest + " slots");
				return;
			}
			for(ItemStack is : c_inv.getContents()){
				if(is != null && is.getType() != Material.AIR){
					p.getInventory().setItem(p.getInventory().firstEmpty(), is);
				}
			}

			c_inv.clear();
		}

		if(b.getType() == Material.ITEM_FRAME){
			b.setType(Material.AIR);
			loot.setTypeId(0);
		}

		if(b.getType() == Material.SKULL){
			loot.setType(Material.SKULL_ITEM);
		}

		if(b.getType() == Material.REDSTONE_TORCH_ON){
			loot.setType(Material.REDSTONE_TORCH_OFF);
		}
		
		if(b.getTypeId() == 64 || b.getTypeId() == 71){
			// Door break, ensure they don't get the stupid bottom part.
			Location l = b.getLocation();
			int block_id = b.getTypeId();
			if(l.add(0, 1, 0).getBlock().getTypeId() == block_id){
				// Door.
				if(block_id == 64){
					loot.setTypeId(324);
				}
				if(block_id == 71){
					loot.setTypeId(330);
				}
			}

			l.subtract(0, 1, 0);
			if(l.subtract(0, 1, 0).getBlock().getTypeId() == block_id){
				if(block_id == 64){
					loot.setTypeId(324);
				}
				if(block_id == 71){
					loot.setTypeId(330);
				}
				l.getBlock().setType(Material.AIR);
			}
		}

		if(b.getType() == Material.REDSTONE_WIRE){
			loot = new ItemStack(Material.REDSTONE, 1);
			//loot.setDurability((short)0);
		}

		if(b.getType() == Material.PISTON_BASE){
			loot.setTypeId(33);
		}

		if(b.getType() == Material.PISTON_MOVING_PIECE || b.getType() == Material.PISTON_EXTENSION){
			loot.setTypeId(0);
		}

		if(b.getType() == Material.PISTON_STICKY_BASE){
			loot.setTypeId(29);
		}

		if(b.getType() == Material.WALL_SIGN){
			loot.setType(Material.SIGN);
		}

		if(b.getType() == Material.SIGN_POST){
			loot.setType(Material.SIGN);
		}

		if(b.getType() == Material.BED){
			loot.setType(Material.AIR);
		}

		if(b.getType() == Material.BED_BLOCK){
			loot.setType(Material.AIR);
		}

		if(b.getType() == Material.REDSTONE_TORCH_OFF){
			loot.setType(Material.REDSTONE_TORCH_ON);
		}

		if(b.getTypeId() == 93 || b.getTypeId() == 94){
			loot.setTypeId(356);
		}

		Packet particles = new PacketPlayOutWorldEvent(2001, (int)Math.round(b.getLocation().getX()), (int)Math.round(b.getLocation().getY()), (int)Math.round(b.getLocation().getZ()), b.getTypeId(), false);
		((CraftServer) Main.plugin.getServer()).getServer().getPlayerList().sendPacketNearby(b.getLocation().getX(), b.getLocation().getY(), b.getLocation().getZ(), 24, ((CraftWorld) b.getWorld()).getHandle().dimension, particles);

		b.setType(Material.AIR);

		int amount = loot.getAmount();
		int max_stack = loot.getMaxStackSize();
		Inventory i = p.getInventory();
		int slot = -1;

		HashMap<Integer, ? extends ItemStack> invItems = i.all(loot.getType());
		for (Map.Entry<Integer, ? extends ItemStack> entry : invItems
				.entrySet()) {
			ItemStack item = entry.getValue();
			int stackAmount = item.getAmount();
			if(item.getDurability() != loot.getDurability()){continue;}
			if((stackAmount + amount) <= max_stack){
				slot = entry.getKey(); 
				item.setAmount(item.getAmount() + amount);
				b.setType(Material.AIR); 
				p.getInventory().setItem(slot, item); 
				p.updateInventory(); break; // Set stack more, no need to add a new item to it.
			}
		}

		if(slot == -1){
			// We never found a stack to add it to.
			if(p.getInventory().firstEmpty() == -1){
				// No space!
				p.sendMessage(ChatColor.RED + "No inventory space.");
				e.setCancelled(true);
				b.setType(m); // Revert the block.
				return;
			}				
			// There's room!
			p.getInventory().setItem(p.getInventory().firstEmpty(), loot);	
		}

		p.updateInventory();
	}


	@SuppressWarnings("deprecation")
	@EventHandler
	public void onOrbUse(PlayerInteractEvent e){
		Player pl = e.getPlayer();

		if(isOrbOfFlight(pl.getItemInHand()) && (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK)){
			e.setCancelled(true);
			e.setUseItemInHand(Result.DENY);
			if(!pl.getWorld().getName().equalsIgnoreCase(pl.getName())){
				// Trying to use in a realm that isn't theres'.
				pl.sendMessage(ChatColor.RED + "You may only use an " + ChatColor.UNDERLINE + "Orb of Flight" + ChatColor.RED + " in your OWN realm.");
				pl.updateInventory();
				return;
			}

			if(!(safe_realms.containsKey(pl.getWorld().getName()))){
				pl.sendMessage(ChatColor.RED + "You can only use an " + ChatColor.UNDERLINE + "Orb of Flight" + ChatColor.RED + " in a realm with an active ORB OF PEACE effect.");
				pl.updateInventory();
				return;
			}

			int amount = pl.getItemInHand().getAmount();
			ItemStack in_hand = pl.getItemInHand();

			if(amount <= 1){
				pl.setItemInHand(new ItemStack(Material.AIR));
			}
			else if(amount > 1){
				amount--;
				in_hand.setAmount(amount);
				pl.setItemInHand(in_hand);
			}
			pl.updateInventory();

			flying_realms.put(pl.getName(), System.currentTimeMillis());
			pl.sendMessage("");
			pl.sendMessage(ChatColor.AQUA + "Your realm will now be a " + ChatColor.BOLD + "FLY ENABLED ZONE" + ChatColor.AQUA + " for 30 minute(s), or until logout.");
			pl.sendMessage(ChatColor.GRAY + "Only YOU and anyone you add to your build list will be able to fly in your realm.");
			pl.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "FLYING ENABLED");
			pl.setAllowFlight(true);

			for(Player visitor : Bukkit.getWorld(pl.getName()).getPlayers()){
				if(build_list.containsKey(pl.getName()) && build_list.get(pl.getName()).contains(visitor)){
					visitor.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "FLYING ENABLED");
					visitor.setAllowFlight(true);
				}
			}

			pl.getWorld().playSound(pl.getLocation(), Sound.LEVEL_UP, 1F, 1F);
			
			try{
				ParticleEffect.sendToLocation(ParticleEffect.CLOUD, pl.getLocation().add(0, 1, 0), 0, 0, 0, 0.05F, 20);
			} catch(Exception err){err.printStackTrace();}
			
			/*pl.getWorld().spawnParticle(pl.getLocation().add(0, 1, 0), Particle.CLOUD, 0.05F, 20);
			pl.getWorld().spawnParticle(pl.getWorld().getSpawnLocation().add(0.5, 1.5, 0.5), Particle.CLOUD, 0.05F, 20);*/
		}

		if(isOrbOfPeace(pl.getItemInHand()) && (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK)){
			e.setCancelled(true);
			e.setUseItemInHand(Result.DENY);
			
			if(KarmaMechanics.getRawAlignment(pl.getName()).equalsIgnoreCase("evil")){
				pl.sendMessage(ChatColor.RED + "You " + ChatColor.UNDERLINE + "cannot" + ChatColor.RED + " use an orb of peace while chaotic.");
				return;
			}
			
			if(!pl.getWorld().getName().equalsIgnoreCase(pl.getName())){
				// Trying to use in a realm that isn't theres'.
				pl.sendMessage(ChatColor.RED + "You may only use an " + ChatColor.UNDERLINE + "Orb of Peace" + ChatColor.RED + " in your OWN realm.");
				pl.updateInventory();
				return;
			}

			int amount = pl.getItemInHand().getAmount();
			ItemStack in_hand = pl.getItemInHand();

			if(amount <= 1){
				pl.setItemInHand(new ItemStack(Material.AIR));
			}
			else if(amount > 1){
				amount--;
				in_hand.setAmount(amount);
				pl.setItemInHand(in_hand);
			}
			pl.updateInventory();

			safe_realms.put(pl.getName(), System.currentTimeMillis());
			DuelMechanics.setPvPOff(pl.getWorld());
			pl.sendMessage("");
			pl.sendMessage(ChatColor.GREEN + "Your realm will now be a " + ChatColor.BOLD + "SAFE ZONE" + ChatColor.GREEN + " for 1 hour(s), or until logout.");
			pl.sendMessage(ChatColor.GRAY + "All damage in your realm will be disabled for this time period.");
			pl.getWorld().playEffect(pl.getLocation(), Effect.ENDER_SIGNAL, 10);
			try{
				ParticleEffect.sendToLocation(ParticleEffect.HAPPY_VILLAGER, pl.getWorld().getSpawnLocation().add(0.5, 1.5, 0.5), 0, 0,0 , 0.05F, 20);
			} catch(Exception err){err.printStackTrace();}
			//pl.getWorld().spawnParticle(pl.getWorld().getSpawnLocation().add(0.5, 1.5, 0.5), Particle.HAPPY_VILLAGER, 0.05F, 20);
		}
	}


	@SuppressWarnings("deprecation")
	@EventHandler
	public void onPlayerInteractEvent(PlayerInteractEvent e){
		final Player p = e.getPlayer();

		if(!(e.isCancelled()) && e.hasBlock() && e.getClickedBlock().getType() == Material.PORTAL && e.getAction() == Action.RIGHT_CLICK_BLOCK && (p.getWorld().getName().equalsIgnoreCase(Bukkit.getWorlds().get(0).getName()) || InstanceMechanics.isInstance(p.getWorld().getName()))){
			Block portal = e.getClickedBlock();
			String realm_owner = "";
			for(Entry<Location, String> entry : portal_map.entrySet()){
				Location l = entry.getKey();
				String owner_name = entry.getValue();

				if(l.distanceSquared(portal.getLocation()) <= 4){
					realm_owner = owner_name;
					//log.info(owner_name);
					break;
				}
			}

			p.sendMessage(ChatColor.LIGHT_PURPLE + "This portal teleports to " + ChatColor.BOLD + ChatMechanics.getPlayerPrefix(realm_owner, true) + realm_owner + "'s" + ChatColor.LIGHT_PURPLE + " Realm");

			if(safe_realms.containsKey(realm_owner)){

				if(flying_realms.containsKey(realm_owner)){
					p.sendMessage(ChatColor.GRAY.toString() + "Realm Type: " + ChatColor.GREEN + "SAFE " + ChatColor.GRAY + "for " + getSecondsOfWildernessLeft(realm_owner) + "s │ " + ChatColor.GRAY + "Flying " + ChatColor.AQUA + "" +  ChatColor.UNDERLINE + "ENABLED" + ChatColor.GRAY + " for " + getSecondsOfFlyingLeft(realm_owner) + "s");
				}
				else{
					p.sendMessage(ChatColor.GRAY.toString() + "Realm Type: " + ChatColor.GREEN + "SAFE " + ChatColor.GRAY + "for " + getSecondsOfWildernessLeft(realm_owner) + "s");
				}
				//getSecondsOfWildernessLeft
			}
			else{
				p.sendMessage(ChatColor.GRAY.toString() + "Realm Type: " + ChatColor.RED + "CHAOTIC ");

			}

			if(Bukkit.getWorld(realm_owner) != null){
				p.sendMessage(ChatColor.GRAY + "Realm Population: " + Bukkit.getWorld(realm_owner).getPlayers().size() + " player(s)");
			}
			if(realm_title.containsKey(realm_owner) && !(realm_title.get(realm_owner) == null) && !realm_title.get(realm_owner).equalsIgnoreCase("null")){
				p.sendMessage(ChatColor.GRAY + realm_title.get(realm_owner));
			}
			else{
				p.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "No description.");
			}

			//p.sendMessage(ChatColor.YELLOW + "Realm Status: " + locked);

			return;
		}

		if(e.hasBlock() && e.getClickedBlock().getType() == Material.PORTAL && e.getAction() == Action.LEFT_CLICK_BLOCK){
			Block portal = e.getClickedBlock();
			Location portal_l = inv_portal_map.get(p.getName());

			/*for(Entry<Location, String> data : portal_map.entrySet()){
				Location loc = data.getKey();
				if(loc.distanceSquared(portal_l) <= 4){

				}
			}*/

			if(portal_l == null || !portal.getWorld().getName().equalsIgnoreCase(portal_l.getWorld().getName()) || portal.getLocation().distanceSquared(portal_l) > 4){
				Packet particles = new PacketPlayOutWorldEvent(2001, (int)Math.round(portal.getLocation().getX()), (int)Math.round(portal.getLocation().getY()), (int)Math.round(portal.getLocation().getZ()), 90, false);
				((CraftServer) Main.plugin.getServer()).getServer().getPlayerList().sendPacketNearby(portal.getLocation().getX(), portal.getLocation().getY(), portal.getLocation().getZ(), 24, ((CraftWorld) portal.getWorld()).getHandle().dimension, particles);			
				return;
			}

			if(portal_l.getWorld().getName().equalsIgnoreCase(p.getWorld().getName()) && portal.getLocation().distanceSquared(portal_l) <= 4){
				Location l = portal_l;

				has_portal.remove(p.getName());
				portal_map_coords.remove(p.getName());
				portal_map.remove(l);
				inv_portal_map.remove(p.getName());

				l.getBlock().setType(Material.AIR);

				if(l.add(0, 1, 0).getBlock().getType() == Material.PORTAL){
					l.getBlock().setType(Material.AIR);
				}

				l.subtract(0, 1, 0);

				if(l.subtract(0, 1, 0).getBlock().getType() == Material.PORTAL){
					l.getBlock().setType(Material.AIR);
				}

				p.playSound(p.getLocation(), Sound.ENDERMAN_TELEPORT, 5F, 0.75F);

				l.add(0, 1, 0);

				//p.sendMessage(ChatColor.RED + "Portal " + ChatColor.BOLD + "CLOSED");
				return;
			}
			return;
		}

		if(!(e.hasItem())){return;}

		if(e.getItem().getType() == Material.NETHER_STAR){
			e.setCancelled(true);
			p.updateInventory();
		}


		if(p.isSneaking() && e.getItem().getType() == Material.NETHER_STAR && (e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR)){

			if(TutorialMechanics.onTutorialIsland(p)){
				p.sendMessage(ChatColor.RED + "You " + ChatColor.UNDERLINE + "cannot" + ChatColor.RED + " open a portal to your realm until you have completed Tutorial Island.");
				return;
			}

			if(!(isRealmOwner(p))){
				p.sendMessage(ChatColor.RED + "You must be inside your realm to upgrade it.");
				return;
			}

			if(upgrading_realms.contains(p.getName())){
				p.sendMessage(ChatColor.RED + "Your realm is already upgrading.");
				return;
			}

			int realm_tier = getRealmTier(p.getName());
			int next_realm_tier = realm_tier + 1;
			if(next_realm_tier > 7){
				p.sendMessage(ChatColor.RED + "Your realm is already at the largest possible size.");
				return;
			}
			int upgrade_cost = getRealmUpgradeCost(next_realm_tier);
			generateUpgradeAuthenticationCode(p, String.valueOf(next_realm_tier));

			p.sendMessage("");
			p.sendMessage(ChatColor.DARK_GRAY + "           *** " + ChatColor.LIGHT_PURPLE + ChatColor.BOLD + "Realm Upgrade Confirmation" + ChatColor.DARK_GRAY + " ***");
			p.sendMessage(ChatColor.DARK_GRAY + "FROM Tier " + ChatColor.LIGHT_PURPLE + realm_tier + ChatColor.DARK_GRAY + " TO " + ChatColor.LIGHT_PURPLE + next_realm_tier);
			p.sendMessage(ChatColor.DARK_GRAY + "Upgrade Cost: " + ChatColor.LIGHT_PURPLE + "" + upgrade_cost + " Gem(s)");
			p.sendMessage("");
			p.sendMessage(ChatColor.LIGHT_PURPLE + "Enter the code " + ChatColor.BOLD + getUpgradeAuthenticationCode(p) + ChatColor.LIGHT_PURPLE + " to confirm your upgrade.");
			p.sendMessage("");
			p.sendMessage("" + ChatColor.RED + ChatColor.BOLD + "WARNING:" + ChatColor.RED + " Realm upgrades are " + ChatColor.BOLD + ChatColor.RED + "NOT" + ChatColor.RED + " reversible or refundable. Type 'cancel' to void this upgrade request.");
			p.sendMessage("");
			return;
		} 

		if((e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK) && e.getItem().getType() == Material.NETHER_STAR){
			/*if(!(p.isSneaking()) && p.getWorld().getName().equalsIgnoreCase(main_world_name)){
				if(e.getAction() == Action.LEFT_CLICK_BLOCK){
					p.sendMessage(ChatColor.RED + "You cannot open the realm shop outside of your realm.");
				}
				return;
				// They can open it anywhere but main world.
			}*/
			if(current_item_being_bought.containsKey(p.getName())){
				p.sendMessage(ChatColor.RED + "You have a " + ChatColor.UNDERLINE + "pending" + ChatColor.RED + " Realm Shop transaction, type '" + ChatColor.GRAY + "cancel" + ChatColor.RED + "' and try again.");
				return;
			}
			if(p.isSneaking()){
				e.setCancelled(true);

				if(TutorialMechanics.onTutorialIsland(p)){
					p.sendMessage(ChatColor.RED + "You " + ChatColor.UNDERLINE + "cannot" + ChatColor.RED + " open a portal to your realm until you have completed Tutorial Island.");
					return;
				}


				Player target = getTarget(p);
				if(target == null){
					p.sendMessage(ChatColor.RED + "To allow a player to build in your realm, you need to " + ChatColor.UNDERLINE + "SNEAK LEFT CLICK" + ChatColor.RED + " them with this Realm Portal Rune.");
					p.sendMessage(ChatColor.GRAY + "Once enabled, they will be able to modify your realm for the rest of your current game session, or until you logout.");
					return;
				}
				if(target.hasMetadata("NPC") || target.getPlayerListName().equalsIgnoreCase("")){
					return;
				}
				if(build_list.containsKey(p.getName()) && build_list.get(p.getName()).contains(target.getName())){
					// Remove.
					List<String> lbuild_list = new ArrayList<String>();
					if(build_list.containsKey(p.getName())){
						lbuild_list = build_list.get(p.getName());
					}
					lbuild_list.remove(target.getName());
					build_list.put(p.getName(), lbuild_list);

					p.sendMessage(ChatColor.RED + "" + ChatColor.UNDERLINE + "REMOVED " + ChatColor.RED + "" + ChatColor.BOLD + target.getName() + ChatColor.RED + " from your realm builder list.");
					p.sendMessage(ChatColor.GRAY + target.getName() + " can no longer place/destroy blocks in your realm.");
					target.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "REMOVED " + ChatColor.RED + "from " + p.getName() + "'s builder list.");
					target.sendMessage(ChatColor.GRAY + "You can no longer place/destroy blocks in their realm.");
					target.setAllowFlight(false);
					return;
				}
				else{
					// Add.
					if(!(CommunityMechanics.isPlayerOnBuddyList(p, target.getName()))){
						p.sendMessage(ChatColor.RED + "Cannot add a non-buddy to realm build list.");
						p.sendMessage(ChatColor.GRAY + "Type '" + ChatColor.BOLD + "/add " + target.getName() + ChatColor.GRAY + "' to add them to your buddy list.");
						return;
					}
					List<String> lbuild_list = new ArrayList<String>();
					if(build_list.containsKey(p.getName())){
						lbuild_list = build_list.get(p.getName());
					}
					lbuild_list.add(target.getName());
					build_list.put(p.getName(), lbuild_list);
					p.sendMessage(ChatColor.GREEN + "" + ChatColor.UNDERLINE + "ADDED " + ChatColor.GREEN + "" + ChatColor.BOLD + target.getName() + ChatColor.GREEN + " to your realm builder list.");
					p.sendMessage(ChatColor.GRAY + target.getName() + " can now place/destroy blocks in your realm until you logout of your current game session.");
					target.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "ADDED" + ChatColor.GREEN + " to " + p.getName() + "'s build list.");
					target.sendMessage(ChatColor.GRAY + "You can now place/destroy blocks in their realm until the end of their game session.");

					AchievmentMechanics.addAchievment(p.getName(), "Creative Companion");
					
					
					if(flying_realms.containsKey(p.getWorld().getName()) && DuelMechanics.isDamageDisabled(p.getWorld().getSpawnLocation()) && safe_realms.containsKey(p.getWorld().getName())){
						target.setAllowFlight(true);
						target.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "FLYING ENABLED");
					}

				}

				return; // Damage event to add user to build list.
			}
			else if(!(p.isSneaking())){
				if(HealthMechanics.in_combat.containsKey(p.getName())){
					p.sendMessage(ChatColor.RED + "You " + ChatColor.UNDERLINE + "cannot" + ChatColor.RED + " open the realm shop while in combat.");
					p.sendMessage(ChatColor.GRAY + "Wait " + ChatColor.UNDERLINE + "a few seconds" + ChatColor.GRAY + " and try again.");
					return;
				}
				p.openInventory(mat_shop_1);
			}
		}

		if(!(e.hasBlock())){return;}
		String block_world_name = e.getClickedBlock().getWorld().getName();
		ItemStack i = e.getItem();
		boolean new_realm = true;

		if(e.getAction() == Action.RIGHT_CLICK_BLOCK && i.getType() == Material.NETHER_STAR){
			e.setCancelled(true);

			if(TutorialMechanics.onTutorialIsland(p)){
				p.sendMessage(ChatColor.RED + "You " + ChatColor.UNDERLINE + "cannot" + ChatColor.RED + " open a portal to your realm until you have completed Tutorial Island.");
				return;
			}

			if(!(block_world_name.equalsIgnoreCase(main_world_name)) && !(block_world_name.equalsIgnoreCase(p.getName()))){
				if(InstanceMechanics.isInstance(block_world_name)){
					p.sendMessage(ChatColor.RED + "You " + ChatColor.UNDERLINE + "cannot" + ChatColor.RED + " open your realm portal while inside a dungeon instance.");
				}
				else{
					p.sendMessage(ChatColor.RED + "You " + ChatColor.UNDERLINE + "cannot" + ChatColor.RED + " open your realm portal inside another realm.");
				}
				return;
			}

			if(!(block_world_name.equalsIgnoreCase(p.getName())) && (isTherePortalLocationNear(e.getClickedBlock().getLocation().add(0, 1, 0), 5) || isThereAPortalNear(e.getClickedBlock().getLocation().add(0, 1, 0).getBlock(), 5))){
				p.sendMessage(ChatColor.RED + "You " + ChatColor.UNDERLINE + "cannot" + ChatColor.RED + " open a realm portal so close to another."); // Too close to another portal.
				p.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + "REQ:" + ChatColor.GRAY + " >3 blocks away.");
				return;
			}

			if(Hive.hive_ddos == true){
				p.sendMessage(ChatColor.RED + "The hive server is currently offline, and no cached version of your realm is available.");
				p.sendMessage(ChatColor.GRAY + "You'll be able to access your realm again once the hive server is back online.");
				return;
			}

			final Location portal_location = e.getClickedBlock().getLocation();

			if(!(portal_location.add(0, 1, 0).getBlock().getType() == Material.AIR) || !(portal_location.add(0, 1, 0).getBlock().getType() == Material.AIR) || e.getClickedBlock().getLocation().getBlock().getType() == Material.CHEST || e.getClickedBlock().getLocation().getBlock().getType() == Material.ENDER_CHEST || e.getClickedBlock().getLocation().getBlock().getType() == Material.PORTAL || e.getClickedBlock().getLocation().getBlock().getType() == Material.ANVIL){
				p.sendMessage(ChatColor.RED + "You " + ChatColor.UNDERLINE + "cannot" + ChatColor.RED + " open a realm portal here.");
				return;
			}

			if(!(block_world_name.equalsIgnoreCase(p.getName())) && LootMechanics.loot_spawns.containsKey(e.getClickedBlock().getLocation().add(0, 1, 0))){
				p.sendMessage(ChatColor.RED + "You " + ChatColor.UNDERLINE + "cannot" + ChatColor.RED + " open a realm portal here.");
				return;
			}

			if(realm_loaded_status.containsKey(p.getName()) && realm_loaded_status.get(p.getName()) == true){
				p.sendMessage(ChatColor.RED + "Your realm is still LOADED on another server.");
				p.sendMessage(ChatColor.GRAY + "Wait 2 minute(s) and try again, or rejoin the other server.");
				async_realm_status.add(p.getName());

				return;
			}

			if(HealthMechanics.in_combat.containsKey(p.getName())){
				p.sendMessage(ChatColor.RED + "You " + ChatColor.UNDERLINE + "cannot" + ChatColor.RED + " place a realm portal while in combat.");
				p.sendMessage(ChatColor.GRAY + "Wait " + ChatColor.UNDERLINE + "a few seconds" + ChatColor.GRAY + " and try again.");
				return;
			}

			if(!p.isOp()){
				if(!(block_world_name.equalsIgnoreCase(p.getName())) && TradeMechanics.getTarget(p) != null || MoneyMechanics.isThereABankChestNear(e.getClickedBlock(), 10)){
					p.sendMessage(ChatColor.RED + "You " + ChatColor.UNDERLINE + "cannot" + ChatColor.RED + " place your realm portal here.");
					return;
				}
			}

			if(ShopMechanics.isThereALadderNear(e.getClickedBlock(), 2)){
				p.sendMessage(ChatColor.RED + "You " + ChatColor.UNDERLINE + "cannot" + ChatColor.RED + " place your realm portal here.");
				return;
			}

			Location portal_loc = e.getClickedBlock().getLocation().add(0, 1.5, 0);
			for(Player pl : portal_loc.getWorld().getPlayers()){
				if(pl.getName().equalsIgnoreCase(p.getName())){continue;}
				if(!(pl.getWorld().getName().equalsIgnoreCase(portal_loc.getWorld().getName()))){
					continue;
				}
				if(pl.getLocation().distanceSquared(portal_loc) <= 2){
					p.sendMessage(ChatColor.RED + "You " + ChatColor.UNDERLINE + "cannot" + ChatColor.RED + " place your realm portal here.");
					return;
				}
			}


			if(block_world_name.equalsIgnoreCase(p.getName())){
				if(upgrading_realms.contains(block_world_name)){
					e.setCancelled(true);
					p.sendMessage(ChatColor.YELLOW + "Portal events are disabled while your realm upgrades.");
					return;
				}
				Block b = e.getClickedBlock().getLocation().add(0, 1, 0).getBlock();
				Location old_portal = Bukkit.getWorld(block_world_name).getSpawnLocation().subtract(0, 1, 0).clone();
				Location new_portal = new Location(b.getWorld(), b.getX(), b.getY(), b.getZ());
				old_portal.getBlock().setType(Material.AIR);
				old_portal.add(0, 1, 0).getBlock().setType(Material.AIR);
				Bukkit.getWorld(block_world_name).setSpawnLocation(new_portal.getBlockX(), new_portal.getBlockY() + 1, new_portal.getBlockZ());
				new_portal.getBlock().setType(Material.PORTAL);
				new_portal.add(0, 1, 0).getBlock().setType(Material.PORTAL);
				return;
			}

			if(has_portal.containsKey(p.getName()) && !(inv_portal_map.containsKey(p.getName()))){
				return; // Their realm is JUST loading.
			}

			// TODO: This probably causes unlinking
			if(has_portal.containsKey(p.getName()) && inv_portal_map.containsKey(p.getName())){
				Location l = inv_portal_map.get(p.getName());
				l.getBlock().setType(Material.AIR);
				l.subtract(0, 1, 0).getBlock().setType(Material.AIR);
				portal_map_coords.remove(p.getName());
				inv_portal_map.remove(p.getName());
				l.add(0, 1, 0);
				portal_map.remove(l);
			}

			p.getWorld().playEffect(portal_location, Effect.ENDER_SIGNAL, 10);
			p.playSound(p.getLocation(), Sound.ENDERMAN_TELEPORT, 5F, 1.25F);
			has_portal.put(p.getName(), true);
			p.setItemInHand(makeTeleportRune(p));
			makePortal(p.getName(), portal_location.subtract(0, 2, 0), 60);

			if(new_realm == true){ // We don't need to load the world if we're just moving the portal.
				/*Thread t = new Thread(new Runnable() {
					public void run() {
						realmHandler(p, p.getName());
					}
				});

				t.start();*/

				Main.plugin.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
					public void run() {
						realmHandler(p, p.getName());
					}
				}, 1L);
			}
		}

	}

	public Player getTarget(Player trader) {
		List<Entity> nearbyE = trader.getNearbyEntities(4.0D, 4.0D, 4.0D);
		ArrayList<Player> livingE = new ArrayList<Player>();

		for (Entity e : nearbyE) {
			if (e.getType() == EntityType.PLAYER) {
				livingE.add((Player) e);
			}
		}

		Player target = null;
		BlockIterator bItr = new BlockIterator(trader, 4);
		Block block;
		Location loc;
		int bx, by, bz;
		double ex, ey, ez;
		// loop through player's line of sight
		while (bItr.hasNext()) {
			block = bItr.next();
			bx = block.getX();
			by = block.getY();
			bz = block.getZ();
			// check for entities near this block in the line of sight
			for (LivingEntity e : livingE) {
				loc = e.getLocation();
				ex = loc.getX();
				ey = loc.getY();
				ez = loc.getZ();
				if ((bx-.75 <= ex && ex <= bx+1.75) && (bz-.75 <= ez && ez <= bz+1.75) && (by-1 <= ey && ey <= by+2.5)) {
					// entity is close enough, set target and stop
					target = (Player) e;
					break;
				}
			}
		}

		return target;
	}

	// Prevents flying players from flying out of realm bounds.
	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerMove(PlayerMoveEvent e){
		Player p = e.getPlayer();

		if(ModerationMechanics.vanish_list.contains(p.getName())){
			return; // Don't particle vanish'd mods.
		}

		if(p.isFlying() && p.getAllowFlight() == true && !p.getWorld().getName().equalsIgnoreCase(main_world_name) && !(InstanceMechanics.isInstance(p.getWorld().getName()))){
			// They can fly, they're in a realm where they're allowed to fly, don't let them fly into void plz.
			Location to = e.getTo();
			Location safe_loc = e.getFrom();

			try{
				ParticleEffect.sendToLocation(ParticleEffect.CLOUD, p.getLocation(), 0, 0, 0, 0.05F, 20);
			} catch(Exception err){err.printStackTrace();}
			//p.getWorld().spawnParticle(p.getLocation(), Particle.CLOUD, 0.05F, 20);

			if(!(p.isOp())){
				int realm_tier = getRealmTier(p.getWorld().getName());
				int max_size = getRealmSizeDimensions(realm_tier) + 16; // Add 16, because the default chunk (0,0) is never used, and 16 is lowest you can be.

				int max_y = 128; // + (max_size / 2)
				Block b = to.getBlock();

				if(!(b.getWorld().getName().equalsIgnoreCase(p.getWorld().getName()))){
					return; // no worries.
				}

				if(Math.round(b.getX() - 0.5) > max_size || Math.round(b.getX() - 0.5) < 16 || Math.round(b.getZ() - 0.5) > max_size || Math.round(b.getZ() - 0.5) < 16 || (b.getY() > (max_y + (max_size) + 1)) || (b.getY() < (max_y - (max_size) - 1))){
					e.setCancelled(true);
					p.teleport(safe_loc);
					return;
				}
			}
		}
	}


	public static void generateUpgradeAuthenticationCode(Player p, String tier){
		StringBuffer sb = new StringBuffer(4);  
		for (int i=0;  i<4;  i++) {  
			int ndx = (int)(Math.random()*ALPHA_NUM.length());  
			sb.append(ALPHA_NUM.charAt(ndx));  
		}  

		realm_upgrade_codes.put(p, tier + sb.toString());
	}

	public static String getUpgradeAuthenticationCode(Player p){
		if(realm_upgrade_codes.containsKey(p)){
			return realm_upgrade_codes.get(p);
		}
		else{
			return null;
		}
	}

	public static ItemStack genCustomItem(Material m, short meta_data, String name, String desc){
		return ItemMechanics.signCustomItem(m, meta_data, name, desc);
	}

	@SuppressWarnings("deprecation")
	public void removePistonHeads(Player pl){
		for(ItemStack is : pl.getInventory().getContents()){
			if(is != null && is.getType() == Material.THIN_GLASS && is.hasItemMeta() && is.getItemMeta().hasDisplayName() && is.getItemMeta().getDisplayName().equalsIgnoreCase(" ")){
				pl.getInventory().removeItem(is);
			}
		}
		pl.updateInventory();
	}

	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.MONITOR) // After location is loaded.
	public void onPlayerJoin(PlayerJoinEvent e){
		final Player p = e.getPlayer();
		p.setAllowFlight(false);

		removePistonHeads(p);

		new File(rootDir + "/realms/up/" + p.getName() + ".zip").delete();
		// Delete any old /up/ files on join.

		if(doesWorldExistLocal(p.getName()) && !(isWorldLoaded(p.getName()))){
			File world_root = new File(p.getName());
			deleteFolder(world_root);
			log.info("[RealmMechanics] Found a realm that should't be on here. Deleting for player " + p.getName());
		}

		if(offline_player_realms.containsKey(p.getName())){
			for(Player pl : offline_player_realms.get(p.getName())){
				if(pl.getWorld().getName().equalsIgnoreCase(p.getName())){
					pl.sendMessage(ChatColor.GREEN + "The realm owner has REJOINED the server.");
					pl.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "Kick timeout cancelled.");
				}
			}
			offline_player_realms.remove(p.getName());
			uploading_realms.remove(p.getName());
			setRealmLoadStatusSQL(p.getName(), false);
		}


		if(p.getInventory().all(Material.NETHER_STAR).size() > 1){
			for(Entry<Integer, ? extends ItemStack> data : p.getInventory().all(Material.NETHER_STAR).entrySet()){
				p.getInventory().remove(data.getValue());
				log.info("[RealmMechanics] Player " + p.getName() + " had more than one realm rune, removed all but 1!");
			}
		}

		int slot = -1;
		if(p.getInventory().contains(Material.NETHER_STAR)){
			slot = p.getInventory().first(Material.NETHER_STAR);
		}

		if(slot == -1){
			if(p.getInventory().getItem(7) == null || p.getInventory().getItem(7).getType() == Material.AIR){
				p.getInventory().setItem(7, makeTeleportRune(p));
			}
			else{
				if(p.getInventory().firstEmpty() != -1){
					p.getInventory().setItem(p.getInventory().firstEmpty(), makeTeleportRune(p));
				}
			}
		}

		p.updateInventory();

		String w_name = p.getWorld().getName();
		if(!w_name.equalsIgnoreCase(Bukkit.getWorlds().get(0).getName())){
			p.teleport(SpawnMechanics.getRandomSpawnPoint(p.getName()));
		}

	}

	@EventHandler
	public void onInventoryCloseEvent(InventoryCloseEvent e){
		Player p = (Player)e.getPlayer();

		if(e.getInventory().getName().equalsIgnoreCase("container.chest") || e.getInventory().getName().equalsIgnoreCase("container.chestDouble") 
				|| e.getInventory().getName().equalsIgnoreCase("container.minecart") || e.getInventory().getName().equalsIgnoreCase("container.dispenser")
				|| e.getInventory().getName().equalsIgnoreCase("container.dropper") || e.getInventory().getName().equalsIgnoreCase("container.hopper")){

			if(p.isOp()){
				return;
			}

			List<ItemStack> to_remove = new ArrayList<ItemStack>();
			List<ItemStack> to_drop = new ArrayList<ItemStack>();
			for(ItemStack is : e.getInventory().getContents()){
				if(is == null || is.getType() == Material.AIR){
					continue;
				}
				if(!(isItemTradeable(is)) || PetMechanics.isPermUntradeable(is) || CommunityMechanics.isSocialBook(is) || is.getType() == Material.NETHER_STAR){
					to_remove.add(is);
				}
				if(ItemMechanics.isArmor(is) || !ItemMechanics.getDamageData(is).equalsIgnoreCase("no") || is.getType() == Material.EMERALD || is.getType() == Material.PAPER || MoneyMechanics.isGemPouch(is)){
					to_drop.add(is);
				}
			}

			for(ItemStack is : to_remove){
				e.getInventory().remove(is);
			}

			for(ItemStack is : to_drop){
				if(to_remove.contains(is)){
					continue;
				}
				e.getInventory().remove(is);
				p.getWorld().dropItemNaturally(p.getLocation(), is);
			}

			if(to_remove.size() > 0){
				p.sendMessage(ChatColor.RED + "You " + ChatColor.UNDERLINE + "cannot" + ChatColor.RED + " deposit weapons, armor, or gems in realm blocks.");
				p.sendMessage(ChatColor.GRAY + "Deposit those items in your BANK CHEST.");
			}
		}
	}

	public static ItemStack makeTeleportRune(Player p){
		int tier = getRealmTier(p.getName());
		int dimensions = getRealmSizeDimensions(tier);
		if(dimensions == 0){
			dimensions = 16;
			return genCustomItem(Material.NETHER_STAR, (short)0, ChatColor.LIGHT_PURPLE.toString() + ChatColor.BOLD.toString() + "Realm Portal Rune", ChatColor.GRAY.toString() + "Tier: " + tier + "/7 [" + dimensions + "x" + dimensions + "x" + dimensions + "]," + ChatColor.LIGHT_PURPLE.toString() + "Right-Click:" + ChatColor.GRAY.toString() + " Generate Realm" + "," + ChatColor.GRAY.toString() + "Walk through the portal you create to go to" + "," + ChatColor.GRAY.toString() + "your Player Owned Realm. Here you can build a" + "," + ChatColor.GRAY.toString() + "perfect haven for your character." + "," + ChatColor.GRAY.toString() + "Let your creativity go wild.");
		}
		else{
			return genCustomItem(Material.NETHER_STAR, (short)0, ChatColor.LIGHT_PURPLE.toString() + ChatColor.BOLD.toString() + "Realm Portal Rune", ChatColor.GRAY.toString() + "Tier: " + tier + "/7 [" + dimensions + "x" + dimensions + "x" + dimensions + "]," + ChatColor.LIGHT_PURPLE.toString() + "Right Click:" + ChatColor.GRAY.toString() + " Open Portal," + ChatColor.LIGHT_PURPLE.toString() + "Left Click:" + ChatColor.GRAY.toString() + " Realm Shop," + ChatColor.LIGHT_PURPLE.toString() + "Sneak-Right Click:" + ChatColor.GRAY.toString()  + " Upgrade Realm," + ChatColor.LIGHT_PURPLE.toString() + "Sneak-Left Click: " + ChatColor.GRAY + "Add Builder");
		}
	}

	public static int getMoneyInInventory(Inventory i){
		int net_worth = 0;

		HashMap<Integer, ? extends ItemStack> invItems = i.all(Material.EMERALD);
		for (Map.Entry<Integer, ? extends ItemStack> entry : invItems
				.entrySet()) {
			ItemStack item = entry.getValue();
			int stackAmount = item.getAmount();

			net_worth += stackAmount;
		}

		HashMap<Integer, ? extends ItemStack> gem_pouches = i.all(Material.INK_SACK);
		for (Map.Entry<Integer, ? extends ItemStack> entry : gem_pouches.entrySet()) {
			ItemStack item = entry.getValue();

			if(!MoneyMechanics.isGemPouch(item)){
				continue;
			}

			int worth = MoneyMechanics.getGemPouchWorth(item);

			net_worth += worth;
		}

		HashMap<Integer, ? extends ItemStack> bank_notes = i.all(Material.PAPER);
		for (Map.Entry<Integer, ? extends ItemStack> entry : bank_notes
				.entrySet()) {
			ItemStack item = entry.getValue();
			int bank_note_val = MoneyMechanics.getBankNoteValue(item);

			net_worth += bank_note_val;
		}

		return net_worth;
	}
	
	public static int getMoneyInInventory(Player p){
		Inventory i = p.getInventory();
		int net_worth = 0;

		HashMap<Integer, ? extends ItemStack> invItems = i.all(Material.EMERALD);
		for (Map.Entry<Integer, ? extends ItemStack> entry : invItems
				.entrySet()) {
			ItemStack item = entry.getValue();
			int stackAmount = item.getAmount();

			net_worth += stackAmount;
		}

		HashMap<Integer, ? extends ItemStack> gem_pouches = i.all(Material.INK_SACK);
		for (Map.Entry<Integer, ? extends ItemStack> entry : gem_pouches.entrySet()) {
			ItemStack item = entry.getValue();

			if(!MoneyMechanics.isGemPouch(item)){
				continue;
			}

			int worth = MoneyMechanics.getGemPouchWorth(item);

			net_worth += worth;
		}

		HashMap<Integer, ? extends ItemStack> bank_notes = i.all(Material.PAPER);
		for (Map.Entry<Integer, ? extends ItemStack> entry : bank_notes
				.entrySet()) {
			ItemStack item = entry.getValue();
			int bank_note_val = MoneyMechanics.getBankNoteValue(item);

			net_worth += bank_note_val;
		}

		return net_worth;
	}

	public static boolean doTheyHaveEnoughMoney(Player p, int amount){
		Inventory i = p.getInventory();
		int paid_off = 0;

		HashMap<Integer, ? extends ItemStack> invItems = i.all(Material.EMERALD);
		for (Map.Entry<Integer, ? extends ItemStack> entry : invItems
				.entrySet()) {
			ItemStack item = entry.getValue();
			int stackAmount = item.getAmount();

			if((paid_off + stackAmount) <= amount){
				paid_off += stackAmount;
			}

			else{
				int to_take = amount - paid_off;
				paid_off += to_take;
			}

			if(paid_off >= amount){   
				return true; // WE CAN AFFORD IT! WOO!
			}

		}

		HashMap<Integer, ? extends ItemStack> gem_pouches = i.all(Material.INK_SACK);
		for (Map.Entry<Integer, ? extends ItemStack> entry : gem_pouches.entrySet()) {
			ItemStack item = entry.getValue();

			if(!MoneyMechanics.isGemPouch(item)){
				continue;
			}

			int worth = MoneyMechanics.getGemPouchWorth(item);

			if((paid_off + worth) <= amount){
				paid_off += worth;
			}

			else{
				int to_take = amount - paid_off;
				paid_off += to_take;
			}

			if(paid_off >= amount){   
				return true; // WE CAN AFFORD IT! WOO!
			}

		}

		// If code reaches this point, it's still not all paid off.

		HashMap<Integer, ? extends ItemStack> bank_notes = i.all(Material.PAPER);
		for (Map.Entry<Integer, ? extends ItemStack> entry : bank_notes
				.entrySet()) {
			ItemStack item = entry.getValue();
			int bank_note_val = MoneyMechanics.getBankNoteValue(item);

			if((paid_off + bank_note_val) <= amount){
				paid_off += bank_note_val;
			}

			else{
				int to_take = amount - paid_off;
				paid_off += to_take;
			}

			if(paid_off >= amount){   
				return true; // They can pay it! WOO!
			}

		}

		return false;
	}

	@SuppressWarnings("deprecation")
	public static void subtractMoney(Player p, int amount){
		Inventory i = p.getInventory();
		int paid_off = 0;

		if(amount <= 0){
			return; // It's free.
		}

		HashMap<Integer, ? extends ItemStack> invItems = i.all(Material.EMERALD);
		for (Map.Entry<Integer, ? extends ItemStack> entry : invItems
				.entrySet()) {
			int index = entry.getKey();
			ItemStack item = entry.getValue();
			int stackAmount = item.getAmount();

			if((paid_off + stackAmount) <= amount){
				p.getInventory()
				.setItem(
						index,
						new ItemStack(Material.AIR));
				paid_off += stackAmount;
			}

			else{
				int to_take = amount - paid_off;
				p.getInventory()
				.setItem(index, MoneyMechanics.makeGems(stackAmount - to_take));
				paid_off += to_take;
			}

			if(paid_off >= amount){   
				p.updateInventory();
				break;
			}
		}

		HashMap<Integer, ? extends ItemStack> gem_pouches = i.all(Material.INK_SACK);
		for (Map.Entry<Integer, ? extends ItemStack> entry : gem_pouches.entrySet()) {
			ItemStack item = entry.getValue();

			if(!MoneyMechanics.isGemPouch(item)){
				continue;
			}

			int worth = MoneyMechanics.getGemPouchWorth(item);

			if((paid_off + worth) <= amount){
				paid_off += worth;
				MoneyMechanics.setPouchWorth(item, 0);
			}

			else{
				int to_take = amount - paid_off;
				paid_off += to_take;
				MoneyMechanics.setPouchWorth(item, worth - to_take);
			}

			if(paid_off >= amount){   
				p.updateInventory();
				break;
			}

		}

		// They still aren't paid off!

		HashMap<Integer, ? extends ItemStack> bank_notes = i.all(Material.PAPER);
		for (Map.Entry<Integer, ? extends ItemStack> entry : bank_notes
				.entrySet()) {
			ItemStack item = entry.getValue();
			int bank_note_val = MoneyMechanics.getBankNoteValue(item);
			int index = entry.getKey();

			if((paid_off + bank_note_val) <= amount){
				p.getInventory()
				.setItem(
						index,
						new ItemStack(Material.AIR));
				paid_off += bank_note_val;
			}

			else{
				int to_take = amount - paid_off;
				paid_off += to_take;
				MoneyMechanics.updateMoney(p, index, (bank_note_val - to_take));
			}

			if(paid_off >= amount){   
				p.updateInventory();
				break;
			}

		} 
	}


	public static String getFormalMatName(Material m, short dur){
		String base_name = m.name();		
		if(base_name.equalsIgnoreCase("wool") && dur == 0){
			base_name = "white wool";
		}
		if(base_name.equalsIgnoreCase("wool") && dur == 1){
			base_name = "orange wool";
		}
		if(base_name.equalsIgnoreCase("wool") && dur == 2){
			base_name = "magenta wool";
		}
		if(base_name.equalsIgnoreCase("wool") && dur == 3){
			base_name = "light blue wool";
		}
		if(base_name.equalsIgnoreCase("wool") && dur == 4){
			base_name = "yellow wool";
		}
		if(base_name.equalsIgnoreCase("wool") && dur == 5){
			base_name = "lime wool";
		}
		if(base_name.equalsIgnoreCase("wool") && dur == 6){
			base_name = "pink wool";
		}
		if(base_name.equalsIgnoreCase("wool") && dur == 7){
			base_name = "gray wool";
		}
		if(base_name.equalsIgnoreCase("wool") && dur == 8){
			base_name = "light gray wool";
		}
		if(base_name.equalsIgnoreCase("wool") && dur == 9){
			base_name = "cyan wool";
		}
		if(base_name.equalsIgnoreCase("wool") && dur == 10){
			base_name = "purple wool";
		}
		if(base_name.equalsIgnoreCase("wool") && dur == 11){
			base_name = "blue wool";
		}
		if(base_name.equalsIgnoreCase("wool") && dur == 12){
			base_name = "brown wool";
		}
		if(base_name.equalsIgnoreCase("wool") && dur == 13){
			base_name = "green wool";
		}
		if(base_name.equalsIgnoreCase("wool") && dur == 14){
			base_name = "red wool";
		}
		if(base_name.equalsIgnoreCase("wool") && dur == 15){
			base_name = "black wool";
		}

		if(base_name.equalsIgnoreCase("log")){
			if(dur == 0)
				base_name = "oak wood";

			if(dur == 1)
				base_name = "spruce wood";

			if(dur == 2)
				base_name = "birch wood";
		}

		if(base_name.equalsIgnoreCase("sandstone")){
			if(dur == 1)
				base_name = "chiseled sandstone";

			if(dur == 2)
				base_name = "smooth sandstone";
		}

		if(base_name.equalsIgnoreCase("smooth_brick")){
			if(dur == 1)
				base_name = "mossy stone brick";

			if(dur == 2)
				base_name = "cracked stone bricks";

			if(dur == 2)
				base_name = "chiseled stone bricks";
		}

		if(base_name.equalsIgnoreCase("long_grass")){
			base_name = "grass";
		}

		base_name = base_name.replaceAll("_", "");

		String formal_name = base_name.substring(0, 1).toUpperCase() + base_name.substring(1, base_name.length()).toLowerCase();
		return formal_name;
	}

	public static ItemStack makeUntradeable(ItemStack itemStack){
		ItemMeta im = itemStack.getItemMeta();

		boolean rename = false;
		String o_name = "";
		try {
			try {
				o_name = im.getDisplayName();
				rename = true;
			} catch (NullPointerException npe) {
				rename = false;
			}
		} catch (ClassCastException cce) {
			rename = false;
		}

		if (rename == true && o_name != null && o_name.length() > 0) {
			im.setDisplayName(o_name);
		}

		List<String> cur_lore = new ArrayList<String>();

		if(im != null){
			cur_lore = im.getLore();
		}
		if(cur_lore == null){
			cur_lore = new ArrayList<String>();
		}
		cur_lore.add(ChatColor.GRAY.toString() + "Untradeable");
		im.setLore(cur_lore);

		itemStack.setItemMeta(im);
		return itemStack;
	}


	public static boolean isItemTradeable(ItemStack i){

		if(i != null && i.hasItemMeta() && i.getItemMeta().hasLore()){
			List<String> lore = i.getItemMeta().getLore();
			for(String s : lore){
				if(ChatColor.stripColor(s).toLowerCase().equalsIgnoreCase("untradeable") || ChatColor.stripColor(s).toLowerCase().equalsIgnoreCase("permanent untradeable")){
					return false;
				}
			}
		}
		
		return true;
		
		/*try{
			if(i.hasItemMeta() && i.getItemMeta().hasLore()){
				for(String s : i.getItemMeta().getLore()){
					if(ChatColor.stripColor(s).toLowerCase().equalsIgnoreCase("untradeable") || ChatColor.stripColor(s).toLowerCase().equalsIgnoreCase("permanent untradeable")){
						return false;
					}
				}
			}
			
			NBTTagList description = CraftItemStack.asNMSCopy(i).getTag().getCompound("display").getList("Lore", 0);

			if(description.get(description.size() - 1).toString().contains("Untradeable")){
				return false;
			}

		} catch (Exception e) {
			return true;
		}
		return true;*/
	}

	public static void disableAllEffects(Player player, final LivingEntity entity){
		CraftEntity ce = (CraftEntity)((Entity)entity);
		
		final DataWatcher dw = new DataWatcher((net.minecraft.server.v1_7_R2.Entity)ce.getHandle());
		dw.a(8, Byte.valueOf((byte)0));
		dw.watch(8, Byte.valueOf((byte)0x00FF00));

		PacketPlayOutEntityMetadata packet = new PacketPlayOutEntityMetadata(entity.getEntityId(), dw, false);
		((CraftPlayer)player).getHandle().playerConnection.sendPacket(packet);

		DataWatcher dwReal = ((CraftLivingEntity)entity).getHandle().getDataWatcher();
		dw.watch(8, dwReal.getByte(8));
		packet = new PacketPlayOutEntityMetadata(entity.getEntityId(), dw, false);
		((CraftPlayer)player).getHandle().playerConnection.sendPacket(packet);
	}

	public static void playPotionEffect(final Player player, final LivingEntity entity, int color, int duration) {
		CraftEntity ce = (CraftEntity)((Entity)entity);
		
		final DataWatcher dw = new DataWatcher((net.minecraft.server.v1_7_R2.Entity)ce.getHandle());
		dw.a(8, Byte.valueOf((byte)0));
		dw.watch(8, Byte.valueOf((byte)color));

		PacketPlayOutEntityMetadata packet = new PacketPlayOutEntityMetadata(entity.getEntityId(), dw, false);
		((CraftPlayer)player).getHandle().playerConnection.sendPacket(packet);

		new BukkitRunnable(){
			@Override
			public void run() {
				DataWatcher dwReal = ((CraftLivingEntity)entity).getHandle().getDataWatcher();
				dw.watch(8, dwReal.getByte(8));
				PacketPlayOutEntityMetadata packet = new PacketPlayOutEntityMetadata(entity.getEntityId(), dw, false);
				((CraftPlayer)player).getHandle().playerConnection.sendPacket(packet);
			}
		}.runTaskLaterAsynchronously(Main.plugin, duration);
	}


	@SuppressWarnings("deprecation")
	@EventHandler
	public void onItemDropFromInv(InventoryClickEvent e){
		Player p = (Player)e.getWhoClicked();

		Inventory top = p.getOpenInventory().getTopInventory();

		if(!top.getName().contains("E-Cash Storage") && !top.getName().contains(" Merchant") && !top.getName().startsWith("Bank Chest") && !(top.getName().equalsIgnoreCase("Collection Bin")) && !(top.getName().equalsIgnoreCase("container.crafting"))){ // Some interaction occuring in the top inventory section.

			ItemStack is = null;
			if(e.isShiftClick()){
				is = e.getCurrentItem();
				if(e.getRawSlot() >= top.getSize()){
					if(!(isItemTradeable(is)) && !(is.getType() == Material.PAPER)){
						e.setCancelled(true);
						p.updateInventory();
						p.sendMessage(ChatColor.RED + "You " + ChatColor.UNDERLINE + "cannot" + ChatColor.RED + " perform this action with an " + ChatColor.ITALIC + "untradeable" + ChatColor.RED + " item.");
						// Prevent untradeable items from being put in other inventories.
					}
				}
			}
			else if(!(e.isShiftClick())){
				is = e.getCursor();
				if(e.getRawSlot() < top.getSize()){
					if(!(isItemTradeable(is)) && !(is.getType() == Material.PAPER)){
						e.setCancelled(true);
						p.updateInventory();
						p.sendMessage(ChatColor.RED + "You " + ChatColor.UNDERLINE + "cannot" + ChatColor.RED + " perform this action with an " + ChatColor.ITALIC + "untradeable" + ChatColor.RED + " item.");
						// Prevent untradeable items from being put in other inventories.
					}
				}
			}

		}
		if(e.getInventory().getType() == InventoryType.PLAYER){
			if(e.getCursor() != null && e.getCurrentItem() == null && e.getSlotType() == SlotType.OUTSIDE){
				ItemStack i = e.getCursor();
				if(!PetMechanics.isPermUntradeable(i) && !(isItemTradeable(i)) && !(i.getType() == Material.PAPER)){
					e.setCursor(new ItemStack(Material.AIR));
					p.updateInventory();
					p.playSound(p.getLocation(), Sound.FIZZ, 1.0F, 0.2F);
				}
				if(PetMechanics.isPermUntradeable(i)){
					e.setCancelled(true);
					p.updateInventory();
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onItemDropEvent(PlayerDropItemEvent e){
		Player p =  e.getPlayer();
		if(!p.getWorld().getName().equalsIgnoreCase(main_world_name) && !InstanceMechanics.isInstance(p.getWorld().getName())){
			Item it = e.getItemDrop();
			dropped_item_ownership.put(it, p.getName());
		}
		
	}
	@EventHandler
	public void onPlayerDropAddOwner(PlayerDropItemEvent e){
	    Player p = e.getPlayer();
	    dropped_item_owner.put(e.getItemDrop(), p.getName());
	    //Add a 5 second delay
	    dropped_item_timer.put(e.getItemDrop(), System.currentTimeMillis() + 5000);
	}
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerPickup(PlayerPickupItemEvent event){
	    Player p = event.getPlayer();
	    Item i = event.getItem();
	    if(dropped_item_timer.containsKey(i) && dropped_item_timer.get(i) >= System.currentTimeMillis()){
	        //The timer hasnt expired so check the player
	        if(!dropped_item_owner.containsKey(i) && !dropped_item_owner.get(i).equalsIgnoreCase(p.getName())){
	            event.setCancelled(true);
	            return;
	        }
	    }
	}
	@EventHandler(priority = EventPriority.HIGHEST) // Should fire AFTER KarmaMechanic's processing Permenant Untradeables.
	public void onPlayerDeathEvent(PlayerDeathEvent e){
		List<ItemStack> li = e.getDrops();
		List<ItemStack> to_remove = new ArrayList<ItemStack>();
		boolean equipped_armor = false;
		int saved_armor = 0;

		if(e.getDrops().isEmpty()){return;}
		Player pl = (Player)e.getEntity();

		List<String> armor_names = new ArrayList<String>();

		for(ItemStack is : pl.getInventory().getArmorContents()){
			if(is == null || !(is.hasItemMeta())){
				continue;
			}
			armor_names.add(is.getItemMeta().getDisplayName());
		}


		for(ItemStack i : li){
			equipped_armor = false;
			if(i == null){
				continue;
			}
			if((!PetMechanics.isPermUntradeable(i) && !isItemTradeable(i)) || i.getType() == Material.NETHER_STAR || CommunityMechanics.isSocialBook(i)){
				if(i.hasItemMeta() && i.getItemMeta() instanceof LeatherArmorMeta && saved_armor < 4){
					if(i.getItemMeta().getLore().contains("ARMOR") || i.getItemMeta().getLore().contains("DPS")){
						if(armor_names.contains(i.getItemMeta().getDisplayName())){
							saved_armor++;
							equipped_armor = true;
						}
					}

					if(equipped_armor == true){
						continue; // Don't delete dyed armor.
					}
				}
				to_remove.add(i);
			}
		}	   

		for(ItemStack i : to_remove){
			e.getDrops().remove(i);
		}

		Player p = e.getEntity();
		if(isWorldLoaded(p.getName())){
			for(Entity ent : Bukkit.getWorld(p.getName()).getEntities()){
				if(ent instanceof Item){
					Item i = (Item)ent;
					if(dropped_item_ownership.containsKey(i) && dropped_item_ownership.get(i).equalsIgnoreCase(p.getName())){
						ent.remove();
						dropped_item_ownership.remove(i);
					}
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerRespawn(PlayerRespawnEvent e){
		Player dead = e.getPlayer();

		if(dead.getLocation().getWorld().getName().equalsIgnoreCase(e.getRespawnLocation().getWorld().getName()) && dead.getLocation().distanceSquared(e.getRespawnLocation()) <= 2){
			// They're respawning on themselves, ignore.
				// Non-legit death, they won't loose any items.
			return;
		}
		
		int slot = -1;
		if(dead.getInventory().contains(Material.NETHER_STAR)){
			slot = dead.getInventory().first(Material.NETHER_STAR);
		}

		if(slot != -1){
			return; // Do nothing they have their rune.
			//dead.getInventory().setItem(slot, makeTeleportRune(dead));
		}

		if(slot == -1){
			if(dead.getInventory().getItem(7) == null || dead.getInventory().getItem(7).getType() == Material.AIR){
				dead.getInventory().setItem(7, makeTeleportRune(dead));
			}
			else{
				dead.getInventory().setItem(dead.getInventory().firstEmpty(), makeTeleportRune(dead));
			}
		}

	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void MaterialShopInventoryEvent(InventoryClickEvent e){
		if(!(e.getWhoClicked() instanceof Player)){
			return;
		}
		final Player p = (Player) e.getWhoClicked();

		if(!(e.getInventory().getName().contains("Realm Material Store"))){
			return;
		}
		
		if(e.getCurrentItem() != null && MoneyMechanics.isDivider(e.getCurrentItem())){
			e.setCancelled(true); // Divider.
			p.updateInventory();
			return;
		}

		if(e.getRawSlot() <= 53){
			e.setCancelled(true);
			if(e.getCurrentItem() != null){
				//Material m = e.getCurrentItem().getType();
				//String format_name = getFormalMatName(m, e.getCurrentItem().getDurability());
				double price = ShopMechanics.getPrice(e.getCurrentItem());
				double ecash_price = Hive.getECASHPrice(e.getCurrentItem());

				if(price == -1 || ecash_price == -1){
					return; // No price.
				}

				if(e.getCurrentItem().getType() == Material.AIR){
					return;
				}

				current_item_being_bought.put(p.getName(), e.getRawSlot());
				shop_page.put(p.getName(), Integer.parseInt(e.getInventory().getName().substring(e.getInventory().getName().lastIndexOf("(") + 1, e.getInventory().getName().lastIndexOf("/"))));

				Main.plugin.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
					public void run() {
						p.closeInventory();
					}
				}, 2L);

				if(e.isLeftClick()){
					p.sendMessage(ChatColor.GREEN + "Enter the " + ChatColor.BOLD + "QUANTITY" + ChatColor.GREEN + " (1-64) you'd like to purchase.");
					p.sendMessage(ChatColor.GRAY + "This material costs " + ChatColor.GREEN + price + "G/each.");
					shop_currency.put(p.getName(), "gems");
				}
				if(e.isRightClick()){
					p.sendMessage(ChatColor.GREEN + "Enter the " + ChatColor.BOLD + "QUANTITY" + ChatColor.GREEN + " (1-64) you'd like to purchase.");
					p.sendMessage(ChatColor.GRAY + "This material costs " + ChatColor.GOLD + ecash_price + "EC/each.");
					double per_ecash = ecash_price;
					if(ecash_price < 1){
						per_ecash = Math.round((1 / (ecash_price)));
						p.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "This means you will get " + per_ecash + "x blocks for each E-CASH you use. So a quantity of 1 = " + per_ecash + " blocks.");
					}
					shop_currency.put(p.getName(), "ecash");
				}
				return;

			}
		}

		if(e.getRawSlot() == 62 && e.getInventory().getName().contains("1/3")){
			e.setCancelled(true); 
			p.updateInventory();
			Main.plugin.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
				public void run() {
					//p.closeInventory(); Doesnt need to close first just looks bad
					p.openInventory(mat_shop_2);
					p.playSound(p.getLocation(), Sound.BAT_TAKEOFF, 1F, 1.2F); // Page turn sound.
				}
			}, 2L);
			// TODO: Next page.
		}

		if(e.getRawSlot() == 62 && e.getInventory().getName().contains("2/3")){
			e.setCancelled(true); 
			p.updateInventory();
			Main.plugin.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
				public void run() {
					//p.closeInventory();
					p.openInventory(mat_shop_3);
					p.playSound(p.getLocation(), Sound.BAT_TAKEOFF, 1F, 1.2F); // Page turn sound.
				}
			}, 2L);
			// TODO: Next page.
		}
		
		if(e.getRawSlot() == 54 && e.getInventory().getName().contains("2/3")){
			e.setCancelled(true); 
			p.updateInventory();
			Main.plugin.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
				public void run() {
					//p.closeInventory();
					p.openInventory(mat_shop_1);
					p.playSound(p.getLocation(), Sound.BAT_TAKEOFF, 1F, 1.2F); // Page turn sound.
				}
			}, 2L);
			// TODO: Next page.
		}
		
		if(e.getRawSlot() == 54 && e.getInventory().getName().contains("3/3")){
			e.setCancelled(true); 
			p.updateInventory();
			Main.plugin.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
				public void run() {
					//p.closeInventory();
					p.openInventory(mat_shop_2);
					p.playSound(p.getLocation(), Sound.BAT_TAKEOFF, 1F, 1.2F); // Page turn sound.
				}
			}, 2L);
			// TODO: Next page.
		}
	}

	@EventHandler
	public void onInventoryInteractEvent(InventoryClickEvent e){
		if(e.getSlotType() == SlotType.OUTSIDE && e.getCursor() == null){return;}
		Player p = (Player)e.getWhoClicked();
		if(e.getCurrentItem() == null){return;}

		if(e.getCurrentItem().getType() == Material.NETHER_STAR && !p.getOpenInventory().getTopInventory().getName().equalsIgnoreCase("container.crafting")){
			e.setCancelled(true);
		}
		if(e.getCursor().getType() == Material.NETHER_STAR && !p.getOpenInventory().getTopInventory().getName().equalsIgnoreCase("container.crafting")){
			e.setCancelled(true);
		}		
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onInventoryClickEvent(InventoryClickEvent e){
		Player p = (Player)e.getWhoClicked();

		if(!e.getInventory().getName().equalsIgnoreCase("container.chest") && !e.getInventory().getName().equalsIgnoreCase("container.chestDouble") && !(e.getInventory().getName().equalsIgnoreCase("container.minecart")) 
				&& !(e.getInventory().getName().equalsIgnoreCase("container.dispenser")) && !(e.getInventory().getName().equalsIgnoreCase("container.hopper")) && !(e.getInventory().getName().equalsIgnoreCase("container.dropper"))){
			return;
		}
		
		if(p.getWorld().getName().equalsIgnoreCase(main_world_name) || p.isOp()){
			return;
		}
		
		if(!(e.getInventory().getName().equalsIgnoreCase("container.hopper")) && e.getAction() == InventoryAction.PICKUP_ALL && p != null){
			// Trying to grab all items from a chest in a realm.
			String realm_name = p.getWorld().getName();
			if(!(realm_name.equalsIgnoreCase(p.getName()) || (build_list.containsKey(realm_name) && build_list.get(realm_name).contains(p.getName())))){
				e.setCancelled(true);
				e.setResult(Result.DENY);
				return;
			}
		}
		
		if(p.isOp()){
			return;
		}

		int slot_num = e.getRawSlot();
		if(slot_num < e.getInventory().getSize()){
			// An item inside the chest is being clicked.
			if(e.isShiftClick() || (e.getCursor() == null && e.getCurrentItem() != null)){
				return; // Don't care if they're moving stuff OUT of inventory.
			}

			if(e.getCursor() != null){
				// They're placing an item into the chest.
				ItemStack cursor = e.getCursor();
				if(ItemMechanics.isArmor(cursor) || !ItemMechanics.getDamageData(cursor).equalsIgnoreCase("no") || cursor.getType() == Material.EMERALD || cursor.getType() == Material.PAPER || MoneyMechanics.isGemPouch(cursor)){
					e.setCancelled(true);
					e.setCursor(cursor);
					p.updateInventory();
					p.sendMessage(ChatColor.RED + "You " + ChatColor.UNDERLINE + "cannot" + ChatColor.RED + " deposit weapons, armor, or gems in realm blocks.");
					p.sendMessage(ChatColor.GRAY + "Deposit those items in your BANK CHEST.");
				}
			}
		}
		else if(slot_num >= e.getInventory().getSize()){
			// Clicking in own inventory.
			if(e.isShiftClick()){
				ItemStack in_slot = e.getCurrentItem();
				if(ItemMechanics.isArmor(in_slot) || !ItemMechanics.getDamageData(in_slot).equalsIgnoreCase("no") || in_slot.getType() == Material.EMERALD || in_slot.getType() == Material.PAPER || MoneyMechanics.isGemPouch(in_slot)){
					e.setCancelled(true);
					e.setCurrentItem(in_slot);
					p.updateInventory();
					p.sendMessage(ChatColor.RED + "You " + ChatColor.UNDERLINE + "cannot" + ChatColor.RED + " deposit weapons, armor, or gems in realm blocks.");
					p.sendMessage(ChatColor.GRAY + "Deposit those items in your BANK CHEST.");
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
	public void EntityDamageEvent(EntityDamageEvent e){
		Entity oent = e.getEntity();
		Entity ent = e.getEntity();
		//log.info(e.getCause().toString());
		if(ent.getPassenger() != null){
			ent = ent.getPassenger();
		}
		if(!(ent instanceof Player)){return;}

		final Player p = (Player) ent;

		if(e.getCause() != DamageCause.VOID){return;}

		if(p.getWorld().getName().equalsIgnoreCase(main_world_name) || InstanceMechanics.isInstance(p.getWorld().getName())){return;}
		String realm_name = p.getWorld().getName(); 

		e.setCancelled(true);
		e.setDamage(0);

		Location l = null;
		if(saved_locations.containsKey(p.getName())){
			l = saved_locations.get(p.getName());
		}
		else{
			l = SpawnMechanics.getRandomSpawnPoint(p.getName());
			p.sendMessage(ChatColor.RED + "Woops! Something has gone wrong in the teleportation matrix of this realm. Your saved location could not be loaded, so you have been safetly transported back to Cyrennica.");
		}

		player_god_mode.put(p.getName(), System.currentTimeMillis());
		p.setFallDistance(0.0F);
		if(oent instanceof Player){
			p.teleport(l);
			FatigueMechanics.sprinting.remove(p);
			p.setSprinting(false);
			p.setAllowFlight(false);
		}
		else{
			oent.teleport(l);
		}

		if(!(p.getName().equalsIgnoreCase(p.getWorld().getName()))){
			p.sendMessage(ChatColor.LIGHT_PURPLE + "You have left " + ChatColor.BOLD + realm_name + "'s" + ChatColor.LIGHT_PURPLE + " realm.");
		}
		else{
			p.sendMessage(ChatColor.LIGHT_PURPLE + "You have left " + ChatColor.BOLD + "YOUR" + ChatColor.LIGHT_PURPLE + " realm.");
		}

		new BukkitRunnable(){
			@Override
			public void run() {
				p.closeInventory();
			}
		}.runTaskLaterAsynchronously(Main.plugin, 2L);
		saved_locations.remove(p.getName());

		Main.plugin.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
			public void run() {
				player_god_mode.remove(p.getName());
			}
		}, 60L); 

	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerPreChangeWorldEvent (PlayerChangedWorldEvent e){
		Player p = e.getPlayer();
		int cur_hp = HealthMechanics.getPlayerHP(p.getName());
		saved_levels.put(p.getName(), cur_hp);
	}


	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerChangedWorldEvent (PlayerChangedWorldEvent e){
		final World from = e.getFrom();
		final Player p = e.getPlayer();
		if(from.getName().equalsIgnoreCase(Bukkit.getWorlds().get(0).getName()) && !(InstanceMechanics.isInstance(p.getWorld().getName()))){
			player_god_mode.put(p.getName(), System.currentTimeMillis());

			p.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "INVINCIBILITY (15s)");
			p.sendMessage(ChatColor.GRAY + "You will " + ChatColor.UNDERLINE + "NOT" + ChatColor.GRAY.toString() + " be flagged as 'combat logged' while invincible.");	

			Main.plugin.getServer().getScheduler().scheduleAsyncDelayedTask(Main.plugin, new Runnable() {
				public void run() {
					if(!p.getWorld().getName().equalsIgnoreCase(main_world_name) && (p.getLocation().getBlock().getType() == Material.PORTAL || p.getLocation().add(0, 1, 0).getBlock().getType() == Material.PORTAL)){
						Location out = p.getLocation();
						out.setY(-10);
						p.teleport(out);
						// TP them out immediatly.
					}
					p.setFireTicks(0);
					p.setFallDistance(0.0F);
					player_god_mode.remove(p.getName());
				}
			}, 10 * 20L);

			Main.plugin.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
				public void run() {
					if(p.getWorld().getName().equalsIgnoreCase(main_world_name)){
						return;
					}
					if(!p.getWorld().getName().equalsIgnoreCase(main_world_name) && (p.getLocation().getBlock().getType() == Material.PORTAL || p.getLocation().add(0, 1, 0).getBlock().getType() == Material.PORTAL)){
						Location out = p.getLocation();
						out.setY(-10);
						p.teleport(out);
						// TP them out immediatly.
					}
					p.setFireTicks(0);
					p.setFallDistance(0.0F);
					player_god_mode.remove(p.getName());
				}
			}, 17 * 20L);

			Main.plugin.getServer().getScheduler().scheduleAsyncDelayedTask(Main.plugin, new Runnable() {
				public void run() {
					/*if(saved_levels.containsKey(p.getName())){
						p.setLevel(saved_levels.get(p.getName()));
						HealthMechanics.setPlayerHP(p.getName(), saved_levels.get(p.getName()));
					}*/

					p.setExp(1F);
					saved_levels.remove(p.getName());
					playPotionEffect(p, p, 0x00FFFB, 300);
					for(Player pl : p.getWorld().getPlayers()){
						playPotionEffect(pl, p, 0x00FFFB, 300);
					}
				}
			}, 5L);
		}

		else if (!(from.getName().equalsIgnoreCase(Bukkit.getWorlds().get(0).getName()))){
			Main.plugin.getServer().getScheduler().scheduleAsyncDelayedTask(Main.plugin, new Runnable() {
				public void run() {
					p.setFireTicks(0);
					p.setFallDistance(0.0F);

					/*if(saved_levels.containsKey(p.getName())){
						p.setLevel(saved_levels.get(p.getName()));
					}*/

					p.setExp(1F);
					saved_levels.remove(p.getName());
				}
			}, 2L);
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onEntityDamage(EntityDamageEvent e){
		if(e.getEntity() instanceof Player){
			Player p = (Player)e.getEntity();
			if(player_god_mode.containsKey(p.getName())){
				e.setCancelled(true);
				e.setDamage(0);
			}
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onEntityDamageEntityEvent(EntityDamageByEntityEvent e){
		if(e.getDamager() instanceof Player){
			Player p = (Player)e.getDamager();
			if(player_god_mode.containsKey(p.getName())){
				e.setCancelled(true);
				e.setDamage(0);
			}
		}
	}

	@EventHandler
	public void onPlayerMoveUpdate(PlayerMoveEvent e){
		Player p = e.getPlayer();
		recent_movement.put(p.getName(), System.currentTimeMillis());
	}

	@EventHandler
	public void onPlayerPortalEvent(PlayerPortalEvent e){
		final Player p = e.getPlayer();

		if(Hive.server_swap.containsKey(p.getName())){
			e.setCancelled(true);
			return;
		}

		if(p.getWorld().getName().equalsIgnoreCase(main_world_name)){
			Location ploc = p.getLocation();
			World to_realm = null;
			Location port_loc = null;
			String realm_name = "";
			long last_time = recent_movement.get(p.getName());

			if((System.currentTimeMillis() - last_time) > 6000){
				e.setCancelled(true);
				return; // They shouldn't be teleported, they havcne't moved.
			}

			if(MountMechanics.summon_mount.containsKey(p.getName())){
				e.setCancelled(true);
				return;
			}

			for (Map.Entry<Location, String> entry : portal_map.entrySet()){
				Location portal_loc = entry.getKey();
				realm_name = entry.getValue();

				if(ploc.distanceSquared(portal_loc) <= 4){
					// TELEPORT BITCHES.
					port_loc = portal_loc;
					to_realm = Main.plugin.getServer().getWorld(realm_name);
					break;
				}
			}

			if(to_realm == null){
				e.setCancelled(true);
				// TODO: Better error message here.
				p.sendMessage(ChatColor.RED + "This realm portal is " + ChatColor.UNDERLINE + "not" + ChatColor.RED + " linked to a destination.");
				p.sendMessage(ChatColor.GRAY + "If this is your portal, please try to replace it with your Realm Portal Rune.");
				if(port_loc == null){
					port_loc = p.getLocation();
				}
				if(port_loc != null){
					portal_map_coords.remove(p.getName());
					portal_map.remove(port_loc);
					inv_portal_map.remove(realm_name);
					if(port_loc.getBlock().getType() == Material.PORTAL){
						port_loc.getBlock().setType(Material.AIR);
					}
					if(port_loc.add(0, 1, 0).getBlock().getType() == Material.PORTAL){
						port_loc.getBlock().setType(Material.AIR);
					}
				}
				return;
			}

			if(!(isWorldLoaded(to_realm.getName()))){
				e.setCancelled(true);
				p.sendMessage(ChatColor.RED + "This realm is not loaded.");
				return;
			}

			if(locked_realms.contains(to_realm.getName()) && !(to_realm.getName().equalsIgnoreCase(p.getName()))){
				e.setCancelled(true);
				p.sendMessage(ChatColor.RED + "This realm is locked.");
				return;
			}

			if(HealthMechanics.in_combat.containsKey(p.getName())){
				e.setCancelled(true);
				p.sendMessage(ChatColor.RED + "You " + ChatColor.UNDERLINE + "cannot" + ChatColor.RED + " place a realm portal while in combat.");
				p.sendMessage(ChatColor.GRAY + "Wait " + ChatColor.UNDERLINE + "a few seconds" + ChatColor.GRAY + " and try again.");
				return;
			}

			if(realm_percent.containsKey(to_realm.getName())){
				e.setCancelled(true);
				String percent_done = realm_percent.get(to_realm.getName());
				p.sendMessage(ChatColor.RED + "This realm is currently UPGRADING. " + ChatColor.BOLD + percent_done + "% Complete.");
				return;
			}

			if(KarmaMechanics.getRawAlignment(p.getName()).equalsIgnoreCase("evil") && safe_realms.containsKey(to_realm.getName())){
				p.sendMessage(ChatColor.RED + "Due to your CHAOTIC alignment, you " + ChatColor.UNDERLINE + "cannot" + ChatColor.RED + " enter this realm.");
				e.setCancelled(true);
				return;
			}

			saved_locations.put(p.getName(), p.getLocation());
			//Location in_realm = mvwm.getMVWorld(to_realm).getSpawnLocation();
			Location in_realm = to_realm.getSpawnLocation();
			e.setTo(in_realm);
			to_realm.setTime(0);
			in_realm.getBlock().setType(Material.PORTAL);
			in_realm.subtract(0, 1, 0).getBlock().setType(Material.PORTAL);
			in_realm.add(0, 1, 0);

			new BukkitRunnable(){
				@Override
				public void run() {
					p.closeInventory();
				}
			}.runTaskLaterAsynchronously(Main.plugin, 2L);
			
			//final String f_realm_name = to_realm.getName();


			if(!(p.getName().equalsIgnoreCase(to_realm.getName()))){
				p.sendMessage(ChatColor.LIGHT_PURPLE + "You have entered " + ChatColor.BOLD + ChatMechanics.getPlayerPrefix(to_realm.getName(), true) + to_realm.getName() + "'s" + ChatColor.LIGHT_PURPLE + " realm.");
				if(realm_title.containsKey(to_realm.getName()) && realm_title.get(to_realm.getName()) != null){
					p.sendMessage(ChatColor.GRAY + realm_title.get(to_realm.getName()));
				}
			}
			else{
				p.sendMessage(ChatColor.LIGHT_PURPLE + "You have returned to " + ChatColor.BOLD + "YOUR" + ChatColor.LIGHT_PURPLE + " realm.");
				if(realm_title.containsKey(to_realm.getName()) && realm_title.get(to_realm.getName()) != null){
					p.sendMessage(ChatColor.GRAY + realm_title.get(to_realm.getName()));
				}
			}

			if(flying_realms.containsKey(to_realm.getName()) && safe_realms.containsKey(to_realm.getName())){
				if(p.getName().equalsIgnoreCase(to_realm.getName()) || (build_list.containsKey(to_realm.getName()) && build_list.get(to_realm.getName()).contains(p.getName()))){
					p.setAllowFlight(true);
					p.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "FLYING ENABLED");
					// Builder or owner == they can fly.
				}
			}

		}

		if(!(p.getWorld().getName().equalsIgnoreCase(main_world_name)) && !(InstanceMechanics.isInstance(p.getWorld().getName()))){
			// Bring them back home.
			p.setAllowFlight(false);

			if(saved_locations.containsKey(p.getName())){
				final String realm_name = p.getWorld().getName();
				final Location home = saved_locations.get(p.getName());

				if(!(p.getName().equalsIgnoreCase(p.getWorld().getName()))){
					p.sendMessage(ChatColor.LIGHT_PURPLE + "You have left " + ChatColor.BOLD + ChatMechanics.getPlayerPrefix(realm_name, true) + realm_name + "'s" + ChatColor.LIGHT_PURPLE + " realm.");
				}
				else{
					p.sendMessage(ChatColor.LIGHT_PURPLE + "You have left " + ChatColor.BOLD + "YOUR" + ChatColor.LIGHT_PURPLE + " realm.");
				}

				p.teleport(home);
				new BukkitRunnable(){
					@Override
					public void run() {
						p.closeInventory();
					}
				}.runTaskLaterAsynchronously(Main.plugin, 2L);
				current_item_being_bought.remove(p.getName());

				e.setCancelled(true);
			}
			else{
				p.teleport(SpawnMechanics.getRandomSpawnPoint(p.getName()));
				p.sendMessage(ChatColor.RED + "Woops! Something has gone wrong in the teleportation matrix of this realm. Your saved location could not be loaded, so you have been safetly transported back to Cyrennica.");
			}
		}

	}


	public static void makePortal(final String to_realm, final Location l, int for_time){
		l.add(0, 1, 0).getBlock().setType(Material.PORTAL);
		l.add(0, 1, 0).getBlock().setType(Material.PORTAL);
		
		portal_map.put(l, to_realm);
		inv_portal_map.put(to_realm, l);
		portal_map_coords.put(to_realm, l.getBlockX() + "," + l.getBlockY() + "," + l.getBlockZ());
	}

	public static void deleteFolder(File folder) {
		File[] files = folder.listFiles();
		if(files!=null) { //some JVMs return null for empty dirs
			for(File f: files) {
				if(f.isDirectory()) {
					deleteFolder(f);
				} else {
					f.delete();
				}
			}
		}
		folder.delete();
	}


	public void fixChunks(final World w){
		//int x = 0;

		if(w == null){
			return;
		}
		try{
			if(w.getLoadedChunks().length <= 0){
				return;
			}
		} catch(NoSuchElementException nsee){
			nsee.printStackTrace();
			return;
		}

		Chunk[] clist = w.getLoadedChunks().clone();

		for(Chunk c : clist){
			try{
				//Chunk c = e.getChunk();
				int xchunk = c.getX();
				int zchunk = c.getZ();
				int chunk_lim = 2;
				int realm_level = getRealmTier(w.getName());
				if(realm_level == 0){return;}
				if(realm_level == 1){chunk_lim = 2;}
				if(realm_level == 2){chunk_lim = 3;}
				if(realm_level == 3){chunk_lim = 4;}
				if(realm_level == 4){chunk_lim = 5;}
				if(realm_level == 5){chunk_lim = 6;}
				if(realm_level == 6){chunk_lim = 7;}
				if(realm_level == 7){chunk_lim = 8;}

				chunk_lim++; // Fix for player not loading into realm after first person due to chunk errors.

				if((zchunk < -chunk_lim || zchunk > chunk_lim) || (xchunk < -chunk_lim || xchunk > chunk_lim)){
					if(c.isLoaded()){
						c.unload(true, true);
					}
				}
			} catch (ConcurrentModificationException cme){
				cme.printStackTrace();
				continue;
			}
		}
	}

	public static boolean doesWorldExistLocal(String wname){
		if(new File(rootDir + "/" + wname).exists() && new File(rootDir + "/" + wname).isDirectory()){
			return true;
		}
		return false;
	}

	public static boolean doesWorldExistGlobal(String wname){
		try {
			URL url = new URL("ftp://" + Hive.ftp_user + ":" + Hive.ftp_pass + "@" + Hive.Hive_IP + "/rdata/" + wname + ".zip");
			URLConnection urlc;
			urlc = url.openConnection();
			InputStream is = urlc.getInputStream(); 
			is.close();
		} catch (IOException first_login) {
			log.info("[RealmMechanics -> Hive] Realm '" + wname + "' does not exist.");
			return false;
		}
		return true;
	}

	public static boolean isWorldLoaded(String wname){
		for(World w : Bukkit.getServer().getWorlds()){
			if(w.getName().equalsIgnoreCase(wname)){
				return true;
			}
		}
		return false;
	}

	public void upgradeRealm(Player p, int new_tier, boolean single_upgrade){
		if(!(isWorldLoaded(p.getName()))){
			p.sendMessage(ChatColor.RED + "Your realm must be loaded locally to modify its size.");
			return;
		}

		if(!(p.getWorld().getName().equalsIgnoreCase(p.getName()))){
			p.sendMessage(ChatColor.RED + "You must be inside your realm to modify its size.");
			return;
		}

		if(new_tier >= 2){
			AchievmentMechanics.addAchievment(p.getName(), "Expanding I");
			if(new_tier >= 4){
				AchievmentMechanics.addAchievment(p.getName(), "Expanding II");
				if(new_tier >= 6){
					AchievmentMechanics.addAchievment(p.getName(), "Expanding III");
					if(new_tier == 7){
						AchievmentMechanics.addAchievment(p.getName(), "Expanding IV");
					}
				}
			}
		}
		
		if(single_upgrade == true){
			int old_tier = getRealmTier(p.getName());
			int next_tier = old_tier + 1;
			setRealmTier(p, next_tier);
			return;
		}

		if(single_upgrade == false){
			setRealmTier(p, new_tier);
			return;
		}

	}

	public int getRealmUpgradeCost(int new_tier){
		if(new_tier == 2){return 800;}
		if(new_tier == 3){return 1600;}
		if(new_tier == 4){return 8000;}
		if(new_tier == 5){return 15000;}
		if(new_tier == 6){return 35000;}
		if(new_tier == 7){return 70000;}
		return 0;
	}

	public static int getRealmSizeDimensions(int tier){
		if(tier == 0){return 16;}
		if(tier == 1){return 16;}
		if(tier == 2){return 22;}
		if(tier == 3){return 32;}
		if(tier == 4){return 45;}
		if(tier == 5){return 64;}
		if(tier == 6){return 82;}
		if(tier == 7){return 128;}
		return 0;
	}

	@SuppressWarnings("deprecation")
	public void setRealmTier(final Player p, final int new_tier){
		if(!(isWorldLoaded(p.getName()))){
			p.sendMessage(ChatColor.RED + "Your realm must be loaded locally to modify its size.");
			return;
		}

		if(!(p.getWorld().getName().equalsIgnoreCase(p.getName()))){
			p.sendMessage(ChatColor.RED + "You must be inside your realm to modify its size.");
			return;
		}

		int old_tier = getRealmTier(p.getName());
		int old_bottom_y = 16;
		int new_bottom_y = 16;

		if(old_tier == 0){old_bottom_y = 16; new_bottom_y = 22;}
		if(old_tier == 1){old_bottom_y = 16; new_bottom_y = 22;}
		if(old_tier == 2){old_bottom_y = 22; new_bottom_y = 32;}
		if(old_tier == 3){old_bottom_y = 32; new_bottom_y = 45;}
		if(old_tier == 4){old_bottom_y = 45; new_bottom_y = 64;}
		if(old_tier == 5){old_bottom_y = 64; new_bottom_y = 82;}
		if(old_tier == 6){old_bottom_y = 82; new_bottom_y = 128;}
		if(old_tier == 7){old_bottom_y = 128; new_bottom_y = 256;}
		World old_world = p.getWorld();

		log.info(old_tier + "," + new_tier);
		realm_tier.put(p.getName(), new_tier);

		for(Player pl : p.getWorld().getPlayers()){
			Location l = null;
			if(saved_locations.containsKey(pl.getName())){
				l = saved_locations.get(pl.getName());
			}
			else if(!(saved_locations.containsKey(pl.getName()))){
				l = SpawnMechanics.getRandomSpawnPoint(p.getName());
			}

			pl.setNoDamageTicks(20);
			pl.setFallDistance(0.0F);
			pl.teleport(l);
			if(!pl.getName().equalsIgnoreCase(p.getName())){ // Realm resident.
				pl.sendMessage(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + p.getName() + ChatColor.LIGHT_PURPLE + "'s realm is currently UPGRADING to " + ChatColor.BOLD + new_bottom_y + "x" + new_bottom_y + ChatColor.LIGHT_PURPLE + ", you have been kicked out of the realm while the upgrade takes place.");
			}
			else if(pl.getName().equalsIgnoreCase(p.getName())){ // Realm owner.
				pl.sendMessage(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "YOUR" + ChatColor.LIGHT_PURPLE + " realm is currently UPGRADING to " + ChatColor.BOLD + new_bottom_y + "x" + new_bottom_y + ChatColor.LIGHT_PURPLE + ", you have been kicked out of the realm while the upgrade takes place.");
			}
			saved_locations.remove(pl.getName());
		}

		if(new_tier == 1){
			generateRealmBlocks(old_world, 22, 22, 22, old_bottom_y);
		}
		if(new_tier == 2){
			generateRealmBlocks(old_world, 22, 22, 22, old_bottom_y);
		}
		if(new_tier == 3){
			generateRealmBlocks(old_world, 32, 32, 32, old_bottom_y);
		}
		if(new_tier == 4){
			generateRealmBlocks(old_world, 45, 45, 45, old_bottom_y);
		}
		if(new_tier == 5){
			generateRealmBlocks(old_world, 64, 64, 64, old_bottom_y);
		}
		if(new_tier == 6){
			generateRealmBlocks(old_world, 82, 82, 82, old_bottom_y);
		}
		if(new_tier == 7){
			generateRealmBlocks(old_world, 128, 128, 128, old_bottom_y);
		}

		int slot = -1;
		if(p.getInventory().contains(Material.NETHER_STAR)){
			slot = p.getInventory().first(Material.NETHER_STAR);
		}

		if(slot != -1){
			p.getInventory().setItem(slot, makeTeleportRune(p));
		}

		if(slot == -1){
			if(p.getInventory().getItem(7) == null || p.getInventory().getItem(7).getType() == Material.AIR){
				p.getInventory().setItem(7, makeTeleportRune(p));
			}
			else{
				p.getInventory().setItem(p.getInventory().firstEmpty(), makeTeleportRune(p));
			}
		}


		p.updateInventory();
	}

	public static int getRealmTier(String p_name){
		if(!(realm_tier.containsKey(p_name))){
			//log.info(p_name);
			return 1;
		}
		int tier = realm_tier.get(p_name);
		if(tier <= 0){
			tier = 1;
		}
		return tier;
	}


	@SuppressWarnings("deprecation")
	public static boolean isThereAPortalNear(Block b, int maxradius) {
		BlockFace[] faces = {BlockFace.UP, BlockFace.NORTH, BlockFace.EAST};
		BlockFace[][] orth = {{BlockFace.NORTH, BlockFace.EAST}, {BlockFace.UP, BlockFace.EAST}, {BlockFace.NORTH, BlockFace.UP}};
		for (int r = 0; r <= maxradius; r++) {
			for (int s = 0; s < 6; s++) {
				BlockFace f = faces[s%3];
				BlockFace[] o = orth[s%3];
				if (s >= 3)
					f = f.getOppositeFace();
				if(!(b.getRelative(f, r) == null)){
					Block c = b.getRelative(f, r);

					for (int x = -r; x <= r; x++) {
						for (int y = -r; y <= r; y++) {
							Block a = c.getRelative(o[0], x).getRelative(o[1], y);
							if (a.getTypeId() == 90)
								return true;
						}
					}
				}
			}
		}
		return false;
	}

	public void generateRealmBlocks(final World w, final int o_limx, final int o_limy, final int o_limz, final int o_oldy){

		Thread t = new Thread(new Runnable() {
			public void run() {
				List<Location> update_locs = new ArrayList<Location>();

				int limx = o_limx + 16;
				int limz = o_limz + 16; // To account for not using chunk 0.

				int x=0,y=128,z=0;
				int oldx = o_oldy;
				int oldz = o_oldy;
				int limy = (128 - o_limy);
				int oldy = (128 - o_oldy) - 1; // Subtract an extra 1 for bedrock border area.

				// BEDROCK
				for (x = 16; x < limx; x++)
				{
					for (z = 16; z < limz; z++)
					{
						update_locs.add(new Location(w, x, limy + 1, z));
					}
				}

				// DIRT
				for (x = 16; x < limx; x++)
				{
					for (y = 127; y > (limy + 1); y--)
					{
						for (z = 16; z < limz; z++)
						{
							Block b = w.getBlockAt(new Location(w, x, y, z));
							if(b.getType() != Material.AIR && b.getType() != Material.BEDROCK){continue;}
							if(b.getType() == Material.BEDROCK || y - 1 <= oldy || x >= oldx || z >= oldz){
								update_locs.add(new Location(w, x, y, z));
							}
						}
					}
				}

				y = 128;

				// GRASS
				for (x = 16; x < limx; x++)
				{
					for (z = 16; z < limz; z++)
					{
						update_locs.add(new Location(w, x, 128, z));
					}
				}

				block_process_list.put(w.getName(), update_locs);

			}
		});

		t.start();
	}

	@SuppressWarnings("deprecation")
	public static void realmHandler(final Player p, String realm_name){
		if(realm_name.contains("§")){
			String color_code_to_remove = realm_name.substring(0, realm_name.indexOf("�") + 2);
			realm_name = realm_name.replaceAll(color_code_to_remove, "");
		}
		if(doesWorldExistLocal(realm_name) && !(isWorldLoaded(realm_name))){
			Bukkit.createWorld(new WorldCreator(realm_name));
			p.sendMessage(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "                   " + "* Realm Portal OPENED *");
			if(realm_title.containsKey(p.getName()) && realm_title.get(p.getName()) != null && !(realm_title.get(p.getName()).equalsIgnoreCase("null"))){
				p.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + "Description: " + ChatColor.GRAY + realm_title.get(p.getName()));
			}
			else{
				p.sendMessage(ChatColor.GRAY + "Type /realm <TITLE> to set the description of your realm, it will be displayed to all visitors.");
			}
			final String safe_realm_name = realm_name;

			new BukkitRunnable(){
				@Override
				public void run() {
					ready_worlds.add(safe_realm_name);
				}
			}.runTaskLaterAsynchronously(Main.plugin, 100L);

			return;
		}
		if(isWorldLoaded(realm_name)){
			p.sendMessage(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "                   " + "* Realm Portal OPENED *");
			if(realm_title.containsKey(p.getName()) && realm_title.get(p.getName()) != null && !(realm_title.get(p.getName()).equalsIgnoreCase("null"))){
				p.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + "Description: " + ChatColor.GRAY + realm_title.get(p.getName()));
			}
			else{
				p.sendMessage(ChatColor.GRAY + "Type /realm <TITLE> to set the description of your realm, it will be displayed to all visitors.");
			}

			return;
		}

		if(doesWorldExistGlobal(realm_name)){
			// Download it.
			safe_realms.remove(realm_name); // Remove old safe data.
			//p.sendMessage(ChatColor.GRAY + "DOWNLOADING your realm data from hive, please wait...");
			downloadRealm(realm_name, p); //Downloads, Makes World File, Extracts.

			WorldCreator wc = new WorldCreator(p.getName());
			wc.type(WorldType.FLAT);
			wc.generateStructures(false);
			World w = Bukkit.createWorld(wc);
	
			if(w.getBlockAt(new Location(w, w.getSpawnLocation().getX(), 0 , w.getSpawnLocation().getZ())).getType() == Material.BEDROCK){
				// Glitched realm.
				if(!(corrupt_world.contains(p.getName()))){
					p.sendMessage(ChatColor.RED + "Your realm did NOT load.");
					p.sendMessage(ChatColor.GRAY + "Retrying download, please wait...");
					corrupt_world.add(p.getName());
					Bukkit.unloadWorld(w, false);
					File world_root = new File(rootDir + "/" + p.getName());
					deleteFolder(world_root);
					realmHandler(p, realm_name);
				}
				else if(corrupt_world.contains(p.getName())){
					// World is corrupt on the HIVE.
					p.performCommand("resetrealm");
					p.sendMessage(ChatColor.RED + "The system has been forced to RESET your realm due to corruption.");
					p.sendMessage(ChatColor.GRAY + "Please /report this issue.");
				}
				return;
				//p.performCommand("resetrealm");
			}

			int p_realm_tier = realm_tier.get(p.getName());
			if(p_realm_tier == 0 || p_realm_tier == 1){ // p_realm_tier == 1 || 
				// Possibly fucked up realm index.
				boolean set = false;
				if(w.getBlockAt(new Location(w, 36, 120, 36)).getType() != Material.AIR){
					if(w.getBlockAt(new Location(w, 46, 120, 46)).getType() != Material.AIR){
						if(w.getBlockAt(new Location(w, 60, 120, 60)).getType() != Material.AIR){
							if(w.getBlockAt(new Location(w, 78, 120, 78)).getType() != Material.AIR){
								if(w.getBlockAt(new Location(w, 96, 120, 96)).getType() != Material.AIR){
									if(w.getBlockAt(new Location(w, 142, 120, 142)).getType() != Material.AIR){
										realm_tier.put(p.getName(), 7);
										set = true;
									}
									if(set == false){
										realm_tier.put(p.getName(), 6);
										set = true;
									}
								}
								if(set == false){
									realm_tier.put(p.getName(), 5);
									set = true;
								}
							}
							if(set == false){
								realm_tier.put(p.getName(), 4);
								set = true;
							}
						}
						if(set == false){
							realm_tier.put(p.getName(), 3);
							set = true;
						}
					}
					if(set == false){
						realm_tier.put(p.getName(), 2);
						set = true;
					}
				}
			}


			p.sendMessage(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "                   " + "* Realm Portal OPENED *");
			if(realm_title.containsKey(p.getName()) && realm_title.get(p.getName()) != null && !(realm_title.get(p.getName()).equalsIgnoreCase("null"))){
				p.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + "Description: " + ChatColor.GRAY + realm_title.get(p.getName()));
			}
			else{
				p.sendMessage(ChatColor.GRAY + "Type /realm <TITLE> to set the description of your realm, it will be displayed to all visitors.");
			}

			final String safe_realm_name = p.getName();

			new BukkitRunnable(){
				@Override
				public void run() {
					ready_worlds.add(safe_realm_name);
				}
			}.runTaskLaterAsynchronously(Main.plugin, 100L);
			
			return;
		}

		if(!(doesWorldExistGlobal(realm_name))){
			// Make it. (first time realm.)
			generateBlankRealm(p, realm_name);
			p.sendMessage(ChatColor.GRAY + "Generating blank realm...");
			p.sendMessage("");
			p.sendMessage(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "                " + "* REALM CREATED *");
			if(realm_title.containsKey(p.getName()) && realm_title.get(p.getName()) != null  && !(realm_title.get(p.getName()).equalsIgnoreCase("null"))){
				p.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + "Description: " + ChatColor.GRAY + realm_title.get(p.getName()));
			}
			else{
				p.sendMessage(ChatColor.GRAY + "Type /realm <TITLE> to set the description of your realm, it will be displayed to all visitors.");
			}

			final String safe_realm_name = realm_name;

			new BukkitRunnable(){
				@Override
				public void run() {
					ready_worlds.add(safe_realm_name);
				}
			}.runTaskLaterAsynchronously(Main.plugin, 100L);

			realm_tier.put(p.getName(), 1);

			int slot = -1;
			if(p.getInventory().contains(Material.NETHER_STAR)){
				slot = p.getInventory().first(Material.NETHER_STAR);
			}

			if(slot != -1){
				p.getInventory().setItem(slot, makeTeleportRune(p));
			}

			if(slot == -1){
				if(p.getInventory().getItem(7) == null || p.getInventory().getItem(7).getType() == Material.AIR){
					p.getInventory().setItem(7, makeTeleportRune(p));
				}
				else{
					p.getInventory().setItem(p.getInventory().firstEmpty(), makeTeleportRune(p));
				}
			}

			p.updateInventory();

		}
	}

	public static boolean isHiveOnline() {
		try  
		{  
			Socket ServerSok = new Socket(Hive.Hive_IP, Hive.FTP_port);
			ServerSok.close();  
			return true;
		}  
		catch (Exception e)  
		{  
			return false;   
		}  
	}

	public static void downloadRealm(String realm_name, Player p){
		new File(rootDir + "/realms/down/" + p.getName() + ".zip").delete();
		try {
			URL url = new URL("ftp://" + Hive.ftp_user + ":" + Hive.ftp_pass + "@" + Hive.Hive_IP + "/rdata/" + realm_name + ".zip");
			URLConnection urlc;

			urlc = url.openConnection();

			InputStream is = urlc.getInputStream(); 
			OutputStream out = new FileOutputStream(rootDir + "/realms/down/" + realm_name + ".zip");

			byte buf[]=new byte[1024];
			int len;

			while((len=is.read(buf))>0){
				out.write(buf,0,len);
			}

			out.close();
			is.close();

		} catch (IOException first_login) {
			log.info("[RealmMechanics] Fatal error occured, realm " + realm_name + " does not exist when it said it did!");
			return;
		}

		File world_root = new File(rootDir + "/" + p.getName());
		deleteFolder(world_root);
		unzipArchive(new File(rootDir + "/realms/down/" + realm_name + ".zip"), world_root);

		log.info("[RealmMechanics] Downloaded realm from HIVE and extracted, attempting to load...");  
	}

	public boolean isRealmOwner(Player p){
		if(p.getWorld().getName().equalsIgnoreCase(p.getName())){
			return true;
		}
		return false;
	}

	public static void generateBlankRealm(Player owner, String realm_name){
		downloadRealm("realm_template", owner);

		WorldCreator wc = new WorldCreator(realm_name);
		wc.type(WorldType.FLAT);
		wc.generateStructures(false);
		wc.generator(new VoidGeneratorGenerator());
		World w = Bukkit.createWorld(wc);
		//w.setAnimalSpawnLimit(0);
		//w.setAutoSave(true);
		//w.setKeepSpawnInMemory(false);
		w.setSpawnLocation(7, 130, 8);

		//fixchunks(w);
		//w.save();

		//setRealmTierSQL(owner, 1);
		//owner.getInventory().setItem(7, makeTeleportRune(owner));
		//owner.updateInventory();

		log.info("[RealmMechanics] Blank realm " + realm_name + " created.");
		//Void generator makes that.
		w.getBlockAt(0, 64, 0).setType(Material.AIR);
		 int x=0,y=128,z=0;
		 Vector s = new Vector(16, 128, 16);

		 log.info("d1");

			// GRASS
			for (x = s.getBlockX(); x < 32; x++)
			{
				for (z = s.getBlockZ(); z < 32; z++)
				{
					w.getBlockAt(new Location(w, x, y, z)).setType(Material.GRASS);
				}
			}


			// DIRT
			for (x = s.getBlockX(); x < 32; x++)
			{
				for (y = 127; y >= 112; y--)
				{
					for (z = s.getBlockZ(); z < 32; z++)
					{
						w.getBlockAt(new Location(w, x, y, z)).setType(Material.DIRT);
					}
				}
			}

		 	// BEDROCK
			for (x = s.getBlockX(); x < 32; x++)
			{
				for (z = s.getBlockZ(); z < 32; z++)
				{
					w.getBlockAt(new Location(w, x, y, z)).setType(Material.BEDROCK);
				}
			}

			 log.info("d2");

			//Grass @ y:128
			//Dirt @ y:127 - y:112
			//Bedrock @ y:111

			// 16x16 editable area, woot!

			// [START] Grass Trim Task.
			// [END] Grass Trim Task

	}

	public static void resetRealm(Player p){
		if(inv_portal_map.containsKey(p.getName())){
			Location l = inv_portal_map.get(p.getName());
			l.getBlock().setType(Material.AIR);
			l.add(0, 1, 0).getBlock().setType(Material.AIR);
			portal_map_coords.remove(p.getName());
			inv_portal_map.remove(p.getName());
			has_portal.remove(p.getName());
			l.subtract(0, 1, 0);
			portal_map.remove(l);
		}
		if(Bukkit.getWorld(p.getName()) != null){
			World realm = Bukkit.getWorld(p.getName());
			for(Player pl : realm.getPlayers()){
				pl.teleport(saved_locations.get(pl.getName()));
				pl.sendMessage(ChatColor.RED + "You have been kicked out of the realm as it is resetting.");
			}
			Main.plugin.getServer().unloadWorld(p.getName(), true);
		}
		File world_root = new File(p.getName());
		deleteFolder(world_root);
		// Deleted messed up realm. Now generating a new one.
		generateBlankRealm(p, p.getName());
		//getServer().unloadWorld(p.getName(), true);
		p.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "REALM RESET RUNNING..."+ ChatColor.RED + " 100%");

	}

	public static final void zipDirectory( File directory, File zip ) throws IOException {
		ZipOutputStream zos = new ZipOutputStream( new FileOutputStream( zip ) );
		zip( directory, directory, zos );
		zos.close();
	}

	private static final void zip(File directory, File base,
			ZipOutputStream zos) throws IOException {
		File[] files = directory.listFiles();
		byte[] buffer = new byte[8192];
		int read = 0;
		for (int i = 0, n = files.length; i < n; i++) {
			if (files[i].isDirectory()) {
				zip(files[i], base, zos);
			} else {
				FileInputStream in = new FileInputStream(files[i]);
				ZipEntry entry = new ZipEntry(files[i].getPath().substring(
						base.getPath().length() + 1));
				zos.putNextEntry(entry);
				while (-1 != (read = in.read(buffer))) {
					zos.write(buffer, 0, read);
				}
				in.close();
			}
		}
	}

	@SuppressWarnings("rawtypes")
	public static void unzipArchive(File archive, File outputDir) {
		try {
			ZipFile zipfile = new ZipFile(archive);
			for (Enumeration e = zipfile.entries(); e.hasMoreElements(); ) {
				ZipEntry entry = (ZipEntry) e.nextElement();
				unzipEntry(zipfile, entry, outputDir);
			}
			zipfile.close();
		} catch (Exception e) {
			log.info("Error while extracting file " + archive);
		}
	}

	private static void unzipEntry(ZipFile zipfile, ZipEntry entry, File outputDir) throws IOException {
		if (entry.isDirectory()) {
			createDir(new File(outputDir, entry.getName()));
			return;
		}

		File outputFile = new File(outputDir, entry.getName());
		if (!outputFile.getParentFile().exists()){
			createDir(outputFile.getParentFile());
		}

		//log.info("Extracting: " + entry);
		InputStream inputStream = zipfile.getInputStream(entry);
		FileOutputStream outputStream = new FileOutputStream(outputFile);

		try {
			byte[] buf = new byte[2048];
			int r = inputStream.read(buf);
			while(r != -1) {
				outputStream.write(buf, 0, r);
				r = inputStream.read(buf);
			}
		} catch(FileNotFoundException e) {
			e.printStackTrace();
		} catch(IOException e) {
			e.printStackTrace();
		} finally {
			if(outputStream != null) {
				try {
					outputStream.close();
				} catch(IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private static void createDir(File dir) {
		log.info("Creating dir "+dir.getName());
		if(!dir.mkdirs()) throw new RuntimeException("Can not create dir "+dir);
	}	  

}


