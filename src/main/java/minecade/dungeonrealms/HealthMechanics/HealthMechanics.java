package minecade.dungeonrealms.HealthMechanics;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import me.confuser.barapi.BarAPI;
import minecade.dungeonrealms.Main;
import minecade.dungeonrealms.AchievmentMechanics.AchievmentMechanics;
import minecade.dungeonrealms.DuelMechanics.DuelMechanics;
import minecade.dungeonrealms.FatigueMechanics.FatigueMechanics;
import minecade.dungeonrealms.Hive.Hive;
import minecade.dungeonrealms.InstanceMechanics.InstanceMechanics;
import minecade.dungeonrealms.ItemMechanics.ItemMechanics;
import minecade.dungeonrealms.KarmaMechanics.KarmaMechanics;
import minecade.dungeonrealms.LevelMechanics.LevelMechanics;
import minecade.dungeonrealms.LevelMechanics.PlayerLevel;
import minecade.dungeonrealms.MonsterMechanics.MonsterMechanics;
import minecade.dungeonrealms.MountMechanics.MountMechanics;
import minecade.dungeonrealms.ProfessionMechanics.ProfessionMechanics;
import minecade.dungeonrealms.RealmMechanics.RealmMechanics;
import minecade.dungeonrealms.ScoreboardMechanics.ScoreboardMechanics;
import minecade.dungeonrealms.SpawnMechanics.SpawnMechanics;
import minecade.dungeonrealms.TutorialMechanics.TutorialMechanics;
import minecade.dungeonrealms.managers.PlayerManager;
import net.minecraft.server.v1_7_R2.EntityLiving;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.EntityEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_7_R2.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_7_R2.inventory.CraftItemStack;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import de.kumpelblase2.remoteentities.api.DespawnReason;
import de.kumpelblase2.remoteentities.api.RemoteEntity;

@SuppressWarnings("deprecation")
public class HealthMechanics implements Listener {
	static Logger log = Logger.getLogger("Minecraft");
	
	public static int HealthRegenCombatDelay = 10;
	// Interval (in seconds) between last instance of a combat acivity and the start of auto HP regen.
	
	public static ConcurrentHashMap<String, Integer> player_health = new ConcurrentHashMap<String, Integer>();
	
	public static HashMap<String, Integer> player_potion_event = new HashMap<String, Integer>();
	// Get the value of the potion being drink.
	
	public static HashMap<String, Integer> thrown_potion_map = new HashMap<String, Integer>();
	// Stores the potion data of thrown item.
	
	public static HashMap<String, Integer> health_data = new HashMap<String, Integer>();
	// Max data of a given player, used in many different plugins, CommunityMechanics, ItemMechanics
	
	public static HashMap<String, Integer> health_regen_data = new HashMap<String, Integer>();
	// HP/s value per player.
	
	public static ConcurrentHashMap<String, Long> in_combat = new ConcurrentHashMap<String, Long>();
	// A list of all players in combat and the last time they were in a combat situation.
	
	public static HashMap<String, Long> last_environ_dmg = new HashMap<String, Long>();
	// Last time environ dmg was taken, prevents spam damage bug from fire contact.
	
	public static HashMap<String, Location> last_hit_location = new HashMap<String, Location>();
	// Location of player the last time they were hit -- used in KarmaMechanics
	
	public static ConcurrentHashMap<String, Integer> move_potion_slot = new ConcurrentHashMap<String, Integer>();
	// The slot (#) the potion being moved is.
	
	public static ConcurrentHashMap<String, ItemStack> move_potion_type = new ConcurrentHashMap<String, ItemStack>();
	// The ItemStack value of moved potion.
	
	public static ConcurrentHashMap<String, Long> move_potion_delay = new ConcurrentHashMap<String, Long>();
	// The time the potion was drinken to gurantee proper delay in movement.
	
	public static HashMap<String, Integer> noob_player_warning = new HashMap<String, Integer>();
	// 3 warnings for noobie players to stop being in combat before they loose their status.
	
	public static List<String> combat_logger = new ArrayList<String>();
	// Combat loggers who have logged in, treated slightly differently on death, they drop nothing. 
	// Just need to process what they keep.
	
	// Overhead HP stuff. {
	//public static ScoreboardManager manager;
	//public static Scoreboard board;
	//public static Objective objective;
	// }
	
	public static List<String> noob_players = new ArrayList<String>();
	// Noobie players, TODO: Recode this so it can be removed on death.
	
	public static HealthMechanics plugin = null;
	
	public void onEnable() {
		Bukkit.getServer().getPluginManager().registerEvents(this, Main.plugin);
		
		plugin = this;
		
		//manager = Bukkit.getScoreboardManager();
		//board = manager.getNewScoreboard();
		//objective = board.registerNewObjective("hpdisplay", "dummy");
		//objective.setDisplaySlot(DisplaySlot.BELOW_NAME);
		//objective.setDisplayName("§c" + "❤");
		
		// "in combat" Handler, removes players from the in_combat list after 'HealthRegenCombatDelay' is over.
		Bukkit.getServer().getScheduler().scheduleAsyncRepeatingTask(Main.plugin, new Runnable() {
			public void run() {
				if(!in_combat.isEmpty()) {
					Map<String, Long> hmash = new HashMap<String, Long>(in_combat);
					for(Map.Entry<String, Long> entry : hmash.entrySet()) {
						String p_name = entry.getKey();
						if(cooldownOver(p_name)) {
							in_combat.remove(p_name);
						}
					}
				}
			}
		}, 40L, 20L);
		
		// Refreshes overhead HP values
		/*
		Bukkit.getServer().getScheduler().scheduleAsyncRepeatingTask(Main.plugin, new Runnable() {
			public void run() {
				for(Player pl : Bukkit.getServer().getOnlinePlayers()){
					try{
						if(pl.getScoreboard() != board && getPlayerHP(pl.getName()) > 0){
							pl.setScoreboard(board);
						}
					} catch(Exception err){
						// Thrown on freshly joined players.
						continue;
					}
				}
			}
		}, 11  * 20L, 1L); */
		
		// Refreshes SQL Connection Pool.
		/*this.getServer().getScheduler().scheduleAsyncRepeatingTask(this, new Runnable() {
			public void run() {
				ConnectionPool.refresh = true;
			}
		}, 20 * 20L, 200 * 20L);*/
		
		// Handles setting overhead HP value.
		Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(Main.plugin, new Runnable() {
			public void run() {
				for(Player pl : Bukkit.getServer().getOnlinePlayers()) {
					if(getPlayerHP(pl.getName()) > 0) {
						setOverheadHP(pl, getPlayerHP(pl.getName()));
					}
				}
			}
		}, 5 * 20L, 5L);
		
		// Handles auto health regeneration event.
		Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(Main.plugin, new Runnable() {
			public void run() {
				HealthAutoRegen();
			}
		}, 25L, 20L);
		
		// Handles auto-move of potions after drink.
		Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(Main.plugin, new Runnable() {
			public void run() {
				String p_name = "";
				try {
					for(Map.Entry<String, Integer> entry : move_potion_slot.entrySet()) {
						// 9 - 36
						p_name = entry.getKey();
						int old_slot = entry.getValue();
						long delay = move_potion_delay.get(p_name);
						if((delay - System.currentTimeMillis()) > 0) { // 1 second delay.
							continue; // Not time yet.
						}
						ItemStack pot = move_potion_type.get(p_name);
						int pot_tier = getPotionTier(pot);
						
						if(Bukkit.getPlayer(p_name) == null && !Bukkit.getPlayer(p_name).isOnline()) {
							move_potion_slot.remove(p_name);
							move_potion_type.remove(p_name);
							move_potion_delay.remove(p_name);
							continue;
						}
						
						Player p = Bukkit.getPlayer(p_name);
						
						int slot = -1;
						int alt_slot = -1;
						int last_resort_slot = -1;
						
						if(p == null) {
							move_potion_slot.remove(p_name);
							move_potion_type.remove(p_name);
							move_potion_delay.remove(p_name);
							continue;
						}
						
						Inventory inv = p.getInventory();
						for(int x = 9; x <= 36; x++) {
							ItemStack is = inv.getItem(x);
							if(is == null || is.getType() == Material.AIR) {
								continue;
							}
							if(is.getType() == Material.POTION) {
								if(is.getDurability() == pot.getDurability()) { // A perfect match.
									slot = x;
									break;
								} else if(is.getDurability() != pot.getDurability() && getPotionTier(pot) == pot_tier) { // A splash / non-splash of the same tier of potion. 
									alt_slot = x;
									continue;
								} else {
									last_resort_slot = x;
									continue;
								}
							}
						}
						
						if(slot == -1) { // No normal pots, let's try for splash; since the inventory did contain SOME potion.
							slot = alt_slot;
							if(alt_slot == -1) {
								slot = last_resort_slot;
							}
						}
						
						if(slot == -1 && alt_slot == -1 && last_resort_slot == -1) { // Found nothing.
							move_potion_slot.remove(p_name);
							move_potion_type.remove(p_name);
							move_potion_delay.remove(p_name);
							continue;
						}
						
						if(p.getInventory().getItem(old_slot) != null && p.getInventory().getItem(old_slot).getType() != Material.POTION && p.getInventory().getItem(old_slot).getType() != Material.AIR) {
							// The slot assigned isn't empty, uh-oh!
							move_potion_slot.remove(p_name);
							move_potion_type.remove(p_name);
							move_potion_delay.remove(p_name);
							continue;
						}
						
						if(p.getInventory().getItem(slot).getType() != Material.POTION) {
							// The slot to move from is not a potion!
							move_potion_slot.remove(p_name);
							move_potion_type.remove(p_name);
							move_potion_delay.remove(p_name);
							continue;
						}
						
						ItemStack i = p.getInventory().getItem(slot);
						p.getInventory().setItem(slot, new ItemStack(Material.AIR));
						try {
							p.getInventory().setItem(old_slot, i);
						} catch(ArrayIndexOutOfBoundsException err) {
							p.updateInventory();
							move_potion_slot.remove(p_name);
							move_potion_type.remove(p_name);
							move_potion_delay.remove(p_name);
							continue;
						}
						p.updateInventory();
						move_potion_slot.remove(p_name);
						move_potion_type.remove(p_name);
						move_potion_delay.remove(p_name);
						
					}
					
				} catch(Exception e) {
					e.printStackTrace();
					move_potion_slot.remove(p_name);
					move_potion_type.remove(p_name);
					move_potion_delay.remove(p_name);
				}
			}
		}, 4 * 20L, 1L);
		
		log.info("[HealthMechanics] V1.0 has been enabled.");
	}
	
	public void onDisable() {
		log.info("[HealthMechanics] V1.0 has been disabled.");
	}
	
	public static void setOverheadHP(Player pl, int hp) {
		ScoreboardMechanics.setOverheadHP(pl, hp);
		if(!pl.hasMetadata("NPC") && !pl.getPlayerListName().equalsIgnoreCase("")) {
			double max_hp = HealthMechanics.getMaxHealthValue(pl.getName());
			double health_percent = (hp / max_hp);
			PlayerLevel lvl = LevelMechanics.getPlayerData(pl);
			if(lvl != null){
				String levelData = ChatColor.AQUA.toString() + ChatColor.BOLD.toString() + "LVL " + ChatColor.AQUA.toString() + lvl.getLevel();
				
				//String xpData = ChatColor.GREEN.toString() + ChatColor.BOLD.toString() + "XP " + ChatColor.GREEN.toString() + lvl.getXP() + ChatColor.GREEN.toString() + ChatColor.BOLD.toString() + " / " + ChatColor.GREEN.toString() + lvl.getEXPNeeded(lvl.getLevel());
				int xplevel = (int) Math.round( ((lvl.getXP() * 1D) / (lvl.getEXPNeeded(lvl.getLevel()) * 1D )) * 100 );
				String xpData = ChatColor.GREEN.toString() + ChatColor.BOLD.toString() + "XP " + ChatColor.GREEN.toString() + xplevel + "%";
				
				String dash = ChatColor.BLACK.toString() + ChatColor.BOLD + " - ";
				BarAPI.setMessage(pl, levelData + dash + ChatColor.LIGHT_PURPLE.toString() + ChatColor.BOLD.toString() + "HP " + ChatColor.LIGHT_PURPLE + getPlayerHP(pl.getName()) + ChatColor.BOLD.toString() + " / " + ChatColor.LIGHT_PURPLE.toString() + getMaxHealthValue(pl.getName()) + dash + xpData, (float) (health_percent * 100F));
			}else{
				BarAPI.setMessage(pl, ChatColor.LIGHT_PURPLE.toString() + ChatColor.BOLD.toString() + "HP " + ChatColor.LIGHT_PURPLE + getPlayerHP(pl.getName()) + ChatColor.BOLD.toString() + " / " + ChatColor.LIGHT_PURPLE.toString() + getMaxHealthValue(pl.getName()), (float) (health_percent * 100F));
			}
			//FakeDragon.setStatus(pl, ChatColor.LIGHT_PURPLE.toString() + ChatColor.BOLD.toString() + "HP " + ChatColor.LIGHT_PURPLE + getPlayerHP(pl.getName()) + ChatColor.BOLD.toString() + " / " + ChatColor.LIGHT_PURPLE.toString() + getMaxHealthValue(pl.getName()), (int)(health_percent * 100));
			//PacketUtils.displayTextBar(ChatColor.LIGHT_PURPLE.toString() + ChatColor.BOLD.toString() + "HP " + ChatColor.LIGHT_PURPLE + getPlayerHP(pl.getName()) + ChatColor.BOLD.toString() + " / " + ChatColor.LIGHT_PURPLE.toString() + getMaxHealthValue(pl.getName()), pl);
		}
		//PacketUtils.displayLoadingBar(text, completeText, player, healthAdd, delay, loadUp);
		//PacketUtils.displayLoadingBar(ChatColor.LIGHT_PURPLE.toString() + ChatColor.BOLD.toString() + "HP " + ChatColor.LIGHT_PURPLE + hp + ChatColor.BOLD.toString() + " / " + ChatColor.LIGHT_PURPLE.toString() + getMaxHealthValue(pl.getName()), "", pl, 50, false);
	}
	
	public static String generateUnderheadBar(double cur_hp, double max_hp) {
		int max_bar = 30;
		
		ChatColor cc = null;
		
		DecimalFormat df = new DecimalFormat("##.#");
		double percent_hp = (double) (Math.round(100.0D * Double.parseDouble((df.format((cur_hp / max_hp)))))); // EX: 0.5054134131
		
		if(percent_hp <= 0 && cur_hp > 0) {
			percent_hp = 1;
		}
		
		double percent_interval = (100.0D / max_bar);
		int bar_count = 0;
		
		cc = ChatColor.GREEN;
		if(percent_hp <= 40) {
			cc = ChatColor.YELLOW;
		}
		if(percent_hp <= 15) {
			cc = ChatColor.RED;
		}
		
		String return_string = cc.toString() + "";
		
		while(percent_hp > 0 && bar_count < max_bar) {
			percent_hp -= percent_interval;
			bar_count++;
			return_string += "|";
		}
		
		return_string += ChatColor.BLACK.toString();
		
		while(bar_count < max_bar) {
			return_string += "|";
			bar_count++;
		}
		
		return return_string;
		// 20 Bars, that's 5% HP per bar
	}
	
	public static int getPotionTier(ItemStack i) {
		return ItemMechanics.getItemTier(i);
	}
	
	public void HealthAutoRegen() {
		for(Player p : Bukkit.getServer().getOnlinePlayers()) {
			try {
				if(getPlayerHP(p.getName()) <= 0 && p.getHealth() <= 0) {
					continue;
				} // They're dead anyway.
				if(FatigueMechanics.starving.contains(p)) {
					continue;
				}
				if(in_combat.containsKey(p.getName())) {
					continue;
				}
				
				if(!(in_combat.containsKey(p.getName()))) {
					double max_hp = getMaxHealthValue(p.getName());
					double current_hp = getPlayerHP(p.getName());
					double amount_to_heal = 5;
					if(current_hp + 1 > max_hp) {
						
						if(p.getHealth() != 20) {
							p.setHealth(20);
						}
						continue;
					} // They're already full.
					
					amount_to_heal += getHealthRegenAmount(p);
					
					if((current_hp + amount_to_heal) >= max_hp) { // We don't need to overheal.
						p.setHealth(20);
						//p.setLevel((int)max_hp);
						HealthMechanics.setPlayerHP(p.getName(), (int) max_hp);
						setPlayerHP(p.getName(), (int) max_hp);
						continue;
					}
					
					else if(p.getHealth() <= 19 && ((current_hp + amount_to_heal) < max_hp)) {
						//p.setLevel((int)(p.getLevel() + amount_to_heal));
						setPlayerHP(p.getName(), (int) (getPlayerHP(p.getName()) + amount_to_heal));
						double health_percent = (getPlayerHP(p.getName()) + amount_to_heal) / max_hp;
						double new_health_display = health_percent * 20;
						if(new_health_display >= 19.50) { // It will be 20 hearts...
							if(health_percent >= 1.0D) { // If we should have full HP. 
								new_health_display = 20;
							} else { // If we should not have full HP.
								new_health_display = 19;
							}
						}
						if(new_health_display < 1) {
							new_health_display = 1;
						}
						p.setHealth((int) new_health_display);
						continue;
					}
				}
			} catch(NullPointerException npe) {
				npe.printStackTrace();
				continue;
			}
		}
	}
	
	public static boolean isCombatLogger(String p_name) {
		if(combat_logger.contains(p_name)) { return true; }
		return false;
	}
	
	public static void setPlayerHP(String p_name, int hp) {
		/*if(plugin.getServer().getPlayer(p_name) != null){
			Player pl = plugin.getServer().getPlayer(p_name);
			pl.setMetadata("dr_hp", new FixedMetadataValue(plugin, hp));
		}*/
		
		player_health.put(p_name, hp);
	}
	
	public static int getPlayerHP(String p_name) {
		if(player_health.containsKey(p_name)) { return player_health.get(p_name); }
		// If the data doesn't exist, return default?
		return 50;
		
		/*if(plugin.getServer().getPlayer(p_name) != null){
			Player pl = plugin.getServer().getPlayer(p_name);
			if(pl.hasMetadata("dr_hp")){
				return pl.getMetadata("dr_hp").get(0).asInt();
			}
		}
		return -1;*/
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerDeathEvent(PlayerDeathEvent e) {
		//log.info("DEATH - " + e.getEntity().getLastDamageCause().getCause().name());
		final Player p = (Player) e.getEntity();
		
		p.setExp(0.0F);
		p.setLevel(0);
		
		if(Hive.player_to_npc.containsKey(p.getName())) { // It was an NPC that died!
			RemoteEntity n = Hive.player_to_npc.get(p.getName());
			p.playEffect(EntityEffect.DEATH);
			
			String align = null;
			if(Hive.player_to_npc_align.containsKey(p.getName())) {
				align = Hive.player_to_npc_align.get(p.getName());
			}
			
			boolean neutral_boots = false, neutral_legs = false, neutral_chest = false, neutral_helmet = false, neutral_weapon = false;
			
			/*if(align != null && align.equalsIgnoreCase("neutral")){
				align = "evil"; // Temp. fix -- just drop everything if they combat log as neutrals.
			}*/
			
			if(align != null && align.equalsIgnoreCase("neutral")) {
				// 50% of weapon dropping, 25% for every piece of equipped armor.
				String lost_gear_s = "";
				if(new Random().nextInt(100) <= 50) {
					neutral_weapon = true;
					lost_gear_s += "0" + ",";
				}
				
				if(new Random().nextInt(100) <= 25) {
					int index = new Random().nextInt(4);
					if(index == 0) {
						neutral_boots = true;
						lost_gear_s += "1" + ",";
					}
					if(index == 1) {
						neutral_legs = true;
						lost_gear_s += "2" + ",";
					}
					if(index == 2) {
						neutral_chest = true;
						lost_gear_s += "3" + ",";
					}
					if(index == 3) {
						neutral_helmet = true;
						lost_gear_s += "4" + ",";
					}
				}
				
				if(lost_gear_s.length() > 0) {
					Hive.sql_query.add("INSERT INTO player_database(p_name, lost_gear) VALUES('" + p.getName() + "', '" + lost_gear_s + "') ON DUPLICATE KEY UPDATE lost_gear='" + lost_gear_s + "'");
					// Now when the player logs in, the server will know exactly which items to take away.
				}
			}
			
			List<ItemStack> p_inv = Hive.npc_inventory.get(n);
			if(align != null && !align.equalsIgnoreCase("evil")) {
				if(!neutral_weapon && !ProfessionMechanics.isSkillItem(p_inv.get(0)) && p_inv.get(0) != null && !ItemMechanics.getDamageData(p_inv.get(0)).equalsIgnoreCase("no")) {
					try {
						p_inv.remove(0); // Remove first hand weapon drop.
					} catch(IndexOutOfBoundsException ioobe) {
						// Why is this even thrown?
					}
				}
			}
			
			for(ItemStack is : p_inv) {
				if(align != null && !align.equalsIgnoreCase("evil")) {
					// They're not chaotic.
					if(ProfessionMechanics.isSkillItem(is)) {
						// Do not drop pickaxes, fishingrods.
						continue;
					}
				}
				p.getWorld().dropItemNaturally(p.getLocation(), is);
			}
			
			if(align != null && (align.equalsIgnoreCase("evil") || align.equalsIgnoreCase("neutral"))) {
				// Drop armor as well if chaotic.
				for(ItemStack is : Hive.npc_armor.get(n)) {
					if(is == null || is.getType() == Material.AIR) {
						continue;
					}
					if(align.equalsIgnoreCase("neutral")) {
						if(is.getType().name().toLowerCase().contains("helmet") && !neutral_helmet) {
							continue; // Do not drop.
						}
						if(is.getType().name().toLowerCase().contains("leggings") && !neutral_legs) {
							continue; // Do not drop.
						}
						if(is.getType().name().toLowerCase().contains("chestplate") && !neutral_chest) {
							continue; // Do not drop.
						}
						if(is.getType().name().toLowerCase().contains("boots") && !neutral_boots) {
							continue; // Do not drop.
						}
					}
					p.getWorld().dropItemNaturally(p.getLocation(), is);
				}
			}
			
			if(Hive.player_mule_inventory.containsKey(p.getName())) {
				// They have a mule in their inventory whose items need to be dropped.
				for(ItemStack is : Hive.player_mule_inventory.get(p.getName())) {
					if(is == null || is.getType() == Material.AIR) {
						continue;
					}
					p.getWorld().dropItemNaturally(p.getLocation(), is);
				}
			}
			
			Hive.npc_inventory.remove(n);
			Hive.npc_armor.remove(n);
			Hive.player_to_npc.remove(p.getName());
			Hive.player_to_npc_align.remove(p.getName());
			Hive.player_item_in_hand.remove(p.getName());
			Hive.player_mule_inventory.remove(p.getName());
			n.despawn(DespawnReason.CUSTOM);
		}
	}
	
	public static int getMaxHealthValue(String p_name) {
		if(health_data.containsKey(p_name)) { return health_data.get(p_name); }
		return 1; // Never even setMaxHealth. Impossible?
	}
	
	public void setLocalMaxHealth(String p_name, double new_max_hp) {
		health_data.put(p_name, (int) new_max_hp);
	}
	
	public int getHealthRegenAmount(Player p) {
		if(health_regen_data.containsKey(p.getName())) {
			return health_regen_data.get(p.getName());
		} else {
			return 0;
		}
	}
	
	public static void generateHealthRegenAmount(Player p, boolean echo) {
		ItemStack[] is = p.getInventory().getArmorContents();
		int total_regen = 0;
		for(ItemStack armor : is) {
			if(armor.getType() == Material.AIR) {
				continue;
			}
			String armor_data = ItemMechanics.getArmorData(armor);
			if(armor_data.contains("hp_regen")) {
				int health_regen_val = Integer.parseInt(armor_data.substring(armor_data.indexOf("hp_regen=") + 9, armor_data.indexOf("@hp_regen_split@")));
				total_regen += health_regen_val;
			}
		}
		
		int old_regen = 0;
		if(health_regen_data.containsKey(p.getName())) {
			old_regen = health_regen_data.get(p.getName());
		}
		int new_regen = total_regen;
		
		if((old_regen != new_regen) && echo == true) {
			if(old_regen > new_regen) {
				p.sendMessage(ChatColor.RED + "-" + (old_regen - new_regen) + " HP/s [" + new_regen + "HP/s]");
			} else {
				p.sendMessage(ChatColor.GREEN + "+" + (new_regen - old_regen) + " HP/s [" + new_regen + "HP/s]");
			}
		}
		
		health_regen_data.put(p.getName(), total_regen);
	}
	
	public static boolean cooldownOver(String p_name) {
		if(!in_combat.containsKey(p_name)) { return true; }
		
		long oldTime = getOldTime(p_name);
		long currentTime = System.currentTimeMillis();
		if(currentTime - oldTime >= (HealthRegenCombatDelay * 1000)) {
			
			return true;
		} else {
			return false;
		}
	}
	
	public static boolean hasCustomName(ItemStack i) {
		try {
			try {
				String fake_var = CraftItemStack.asNMSCopy(i).getTag().getCompound("display").getString("Name");
				// log.info(fake_var);
				if(fake_var != null && fake_var.length() > 0) { return true; }
			} catch(NullPointerException npe) {
				return false;
			}
			
		} catch(ClassCastException cce) {
			return false;
		}
		
		return false;
	}
	
	public static boolean hasLore(ItemStack i) {
		if(i.hasItemMeta()) {
			if(i.getItemMeta().hasLore()) { return true; }
		}
		return false;
	}
	
	public static long getOldTime(String p_name) {
		return in_combat.get(p_name);
	}
	
	public static int generateMaxHP(Player p) {
		ItemStack[] armor_contents = p.getInventory().getArmorContents();
		double total_health = 0;
		for(ItemStack i : armor_contents) {
			if(i.getType() == Material.AIR) {
				continue;
			}
			total_health += getHealthVal(i);
		}
		
		total_health += 50;
		// Adds the default 50.
		
		if(ItemMechanics.vit_data.containsKey(p.getName())) {
			int vit_atr = ItemMechanics.vit_data.get(p.getName());
			double vit_armor_mod = 0.05D * vit_atr; // This will return the modifier number... 100 * 0.05 = 5, so I need 105% HP.
			total_health += ((double) total_health * (double) (vit_armor_mod / 100.0D));
		}
		
		/*if(total_health > 32767){
			total_health = 32767;
		}*/
		
		return (int) (total_health);
	}
	
	public static int generateMaxHP(Entity e) {
		EntityLiving ent = ((CraftLivingEntity) e).getHandle();
		net.minecraft.server.v1_7_R2.ItemStack[] armor_contents = ent.getEquipment();
		double total_health = 0;
		for(net.minecraft.server.v1_7_R2.ItemStack i : armor_contents) {
			ItemStack is = CraftItemStack.asBukkitCopy(i);
			if(is.getType() == Material.AIR) {
				continue;
			}
			total_health += getHealthVal(is);
		}
		
		int tier = MonsterMechanics.getMobTier(e);
		
		boolean elite = false;
		
		if(e instanceof LivingEntity) {
			
			LivingEntity le = (LivingEntity) e;
			ItemStack i = le.getEquipment().getItemInHand();
			
			if(i.getEnchantments().size() > 0) {
				elite = true;
			}
			
			if(tier == 1) {
				if(elite == false) {
					total_health = (total_health * 0.50);
				}
				if(elite == true) {
					total_health = (total_health * 1.80);
				}
			}
			if(tier == 2) {
				if(elite == false) {
					total_health = (total_health * 0.85);
				}
				if(elite == true) {
					total_health = (total_health * 2.50);
				}
				
			}
			if(tier == 3) {
				if(elite == false) {
					total_health = (total_health * 1.00);
				}
				if(elite == true) {
					total_health = (total_health * 2.60);
				}
			}
			if(tier == 4) {
				if(elite == false) {
					total_health = (total_health * 1.10);
				}
				if(elite == true) {
					total_health = (total_health * 2.70);
				}
			}
			if(tier == 5) {
				if(elite == false) {
					total_health = (total_health * 1.40);
				}
				if(elite == true) {
					total_health = (total_health * 3.0);
				}
			}
		}
		
		return (int) (total_health + 1);
	}
	
	public static int getHealthVal(ItemStack armor) {
		String armor_data = ItemMechanics.getArmorData(armor);
		if(armor_data.equalsIgnoreCase("no")) { return 0; }
		
		int health_val = Integer.parseInt(armor_data.substring(armor_data.indexOf(":") + 1, armor_data.indexOf("@")));
		
		return health_val;
	}
	
	public static int getPotionRestoreVal(ItemStack i) {
		int pot_tier = getPotionTier(i);
		if(pot_tier == 1) { return 15; }
		if(pot_tier == 2) { return 75; }
		if(pot_tier == 3) { return 300; }
		if(pot_tier == 4) { return 750; }
		if(pot_tier == 5) { return 1800; }
		return 0;
	}
	
	@EventHandler
	public void onPotionSplashEvent(PotionSplashEvent e) {
		ThrownPotion tp = e.getPotion();
		e.setCancelled(true);
		
		if(tp.getShooter() instanceof Player) {
			Player p = (Player) tp.getShooter();
			if(!(thrown_potion_map.containsKey(p.getName()))) { return; }
			int amount_to_heal = thrown_potion_map.get(p.getName());
			thrown_potion_map.remove(p.getName());
			for(LivingEntity le : e.getAffectedEntities()) {
				if(!(le instanceof Player)) {
					continue;
				}
				Player pl = (Player) le;
				double max_hp = getMaxHealthValue(pl.getName());
				double current_hp = getPlayerHP(p.getName());
				if(current_hp + 1 > max_hp) {
					continue;
				} // They have max HP.
				if(current_hp <= 0 || p.isDead() || !p.isOnline()){
				    continue;
				}
				//amount_to_heal += getHealthRegenAmount(p);
				
				if(PlayerManager.getPlayerModel(pl).getToggleList() != null && PlayerManager.getPlayerModel(pl).getToggleList().contains("debug")){
					pl.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "       +" + ChatColor.GREEN + (int) amount_to_heal + ChatColor.BOLD + " HP" + ChatColor.GREEN + " FROM " + p.getName() + ChatColor.GRAY + " [" + ((int) current_hp + (int) amount_to_heal) + "/" + (int) max_hp + "HP]");
				}
				
				if((current_hp + amount_to_heal) >= max_hp) {
					pl.setHealth(20);
					//pl.setLevel((int)max_hp);
					setPlayerHP(pl.getName(), (int) max_hp);
					continue; // Full HP.
				}
				
				else if(pl.getHealth() <= 19 && ((current_hp + amount_to_heal) < max_hp)) {
					//pl.setLevel((int)(pl.getLevel() + amount_to_heal));
					setPlayerHP(pl.getName(), (int) (getPlayerHP(pl.getName()) + amount_to_heal));
					double health_percent = (getPlayerHP(pl.getName()) + amount_to_heal) / max_hp;
					double new_health_display = health_percent * 20;
					if(new_health_display > 19) {
						if(health_percent >= 1) {
							new_health_display = 20;
						} else if(health_percent < 1) {
							new_health_display = 19;
						}
					}
					if(new_health_display < 1) {
						new_health_display = 1;
					}
					pl.setHealth((int) new_health_display);
					
				}
			}
		}
		
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
	public void onPlayerDrinkPotion(PlayerInteractEvent e) {
		if(e.hasItem() && (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK)) {
			/*if(e.isCancelled() && e.getAction() != Action.RIGHT_CLICK_AIR){
				return;
			}*/
			final Player p = e.getPlayer();
			final ItemStack i = e.getItem();
			
			/*if(e.hasBlock()){
				if(e.getClickedBlock().getType() == Material.CHEST || e.getClickedBlock().getType() == Material.ENDER_CHEST || e.getClickedBlock().getType() == Material.ANVIL || e.getClickedBlock().getType() == Material.WOOD_DOOR || e.getClickedBlock().getType() == Material.LEVER || e.getClickedBlock().getType() == Material.STONE_BUTTON || e.getClickedBlock().getType() == Material.TRAP_DOOR || e.getClickedBlock().getType() == Material.PORTAL
						|| e.getClickedBlock().getType() == Material.DISPENSER){
					return; // Interactable block, don't drink it by mistake.
				}
			}*/
			
			if(Hive.server_swap.containsKey(p.getName())) { return; }
			
			if(i.getType() == Material.POTION && (i.getDurability() == 16385 || i.getDurability() == 16389 || i.getDurability() == 16393 || i.getDurability() == 16396 || i.getDurability() == 16387)) {
				
				if(DuelMechanics.duel_map.containsKey(p.getName())) {
					e.setCancelled(true);
					p.updateInventory();
					p.sendMessage(ChatColor.RED + "You " + ChatColor.UNDERLINE + "cannot" + ChatColor.RED + " use consumables while in a duel.");
					return;
				}
				
				int potion_val = getPotionRestoreVal(i);
				potion_val = (potion_val / 2);
				
				if(potion_val <= 10) {
					potion_val = 15;
				}
				if(potion_val == 37 || potion_val == 38) {
					potion_val = 40;
				}
				
				if(thrown_potion_map.containsKey(p.getName())) {
					e.setCancelled(true);
					p.updateInventory();
					return;
				}
				
				e.setCancelled(false);
				e.setUseInteractedBlock(Result.DENY);
				e.setUseItemInHand(Result.ALLOW);
				thrown_potion_map.put(p.getName(), potion_val);
			}
			if(i.getType() == Material.POTION && (i.getDurability() == 1 || i.getDurability() == 5 || i.getDurability() == 9 || i.getDurability() == 12 || i.getDurability() == 3)) {
				/*if(move_potion_slot.containsKey(p.getName())){ 
					e.setCancelled(true);
					p.updateInventory();
					return;
				}*/
				
				if(DuelMechanics.duel_map.containsKey(p.getName())) {
					e.setCancelled(true);
					p.updateInventory();
					p.sendMessage(ChatColor.RED + "You " + ChatColor.UNDERLINE + "cannot" + ChatColor.RED + " use consumables while in a duel.");
					return;
				}
				
				final int pot_tier = getPotionTier(i);
				if(pot_tier == 0) { return; } // It's not custom.
				final int to_restore = getPotionRestoreVal(i);
				
				//player_potion_event.put(p.getName(), getPotionRestoreVal(i));
				Bukkit.getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
					public void run() {
						//if(player_potion_event.containsKey(p.getName())){
						
						int slot = -1;
						Inventory inv = p.getInventory();
						List<ItemStack> hot_bar_inv = new ArrayList<ItemStack>();
						for(int x = 0; x <= 8; x++) {
							hot_bar_inv.add(inv.getItem(x));
						}
						
						if(p.getItemInHand().getType() != Material.POTION) { return; // Do no more.
						}
						
						p.setItemInHand(new ItemStack(Material.AIR));
						p.playSound(p.getLocation(), Sound.DRINK, 1F, 1F);
						p.updateInventory();
						
						inv = p.getInventory(); // Refresh inventory.
						for(int y = 0; y <= 8; y++) {
							ItemStack istack = inv.getItem(y);
							if(istack == null && hot_bar_inv.get(y) == null) {
								continue;
							}
							if(((istack == null && hot_bar_inv.get(y) != null)) || istack.getType() != hot_bar_inv.get(y).getType()) { // The changed item!
								slot = y; // The slot we'll move the next potion to.
								break;
							}
						}
						
						double amount_to_heal = to_restore;
						//player_potion_event.remove(p.getName());
						// TODO: Restore health.
						double max_hp = getMaxHealthValue(p.getName());
						double current_hp = getPlayerHP(p.getName());
						
						if(slot == -1) {
							slot = p.getInventory().firstEmpty();
						}
						
						if(current_hp + 1 > max_hp) { // They have max HP.
							if(p.getInventory().contains(Material.POTION)) {
								move_potion_slot.put(p.getName(), slot);
								move_potion_type.put(p.getName(), i);
								move_potion_delay.put(p.getName(), System.currentTimeMillis() + (1 * 500));
							}
							
							return;
						}
						
						if(PlayerManager.getPlayerModel(p).getToggleList().contains("debug")){
							p.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "       +" + ChatColor.GREEN + (int) amount_to_heal + ChatColor.BOLD + " HP" + ChatColor.GRAY + " [" + (int) (current_hp + amount_to_heal) + "/" + (int) max_hp + "HP]");
						}
						
						if((current_hp + amount_to_heal) >= max_hp) {
							p.setHealth(20);
							//p.setLevel((int)max_hp);
							setPlayerHP(p.getName(), (int) max_hp);
							
							if(p.getInventory().contains(Material.POTION)) {
								move_potion_slot.put(p.getName(), slot);
								move_potion_type.put(p.getName(), i);
								move_potion_delay.put(p.getName(), System.currentTimeMillis() + (1 * 500));
							}
							
							return; // Full HP.
						}
						
						else if(p.getHealth() <= 19 && ((current_hp + amount_to_heal) < max_hp)) {
							
							double health_percent = (current_hp + amount_to_heal) / max_hp;
							double new_health_display = health_percent * 20.0D;
							if(new_health_display > 19) {
								if(health_percent >= 1) {
									new_health_display = 20;
								} else if(health_percent < 1) {
									new_health_display = 19;
								}
							}
							if(new_health_display < 1) {
								new_health_display = 1;
							}
							setPlayerHP(p.getName(), (int) (current_hp + amount_to_heal));
							//p.setLevel((int)(current_hp + amount_to_heal));
							p.setHealth((int) new_health_display);
						}
						
						if(p.getInventory().contains(Material.POTION)) {
							move_potion_slot.put(p.getName(), slot);
							move_potion_type.put(p.getName(), i);
							move_potion_delay.put(p.getName(), System.currentTimeMillis() + (1 * 500));
						}
						
					}
					
				}, 2L);
			}
		}
	}
	
	@EventHandler
	public void onPlayerClickEntity(PlayerInteractEntityEvent e) {
		Player pl = e.getPlayer();
		ItemStack is = pl.getItemInHand();
		if(is != null && (is.getType() != Material.AIR)) {
			ItemStack item = is;
			Player p = e.getPlayer();
			if(ItemMechanics.getArmorData(item).equalsIgnoreCase("no")) { return; } // Not a piece of armor.
			if(p.getInventory().getItem(ItemMechanics.getRespectiveArmorSlot(item.getType())) != null) { return; } // There's an item equipped.
			e.setCancelled(true);
			/*InventoryClickEvent ice = new InventoryClickEvent(p.getOpenInventory(), SlotType.QUICKBAR, p.getInventory().first(item), false, true);
			ice.setCurrentItem(item);
			ice.setCancelled(false);
			ice.setResult(Result.DENY);
			Bukkit.getPluginManager().callEvent(ice);*/
			p.updateInventory();
		}
	}
	
	@EventHandler
	public void onPlayerRightClickEquip(PlayerInteractEvent e) {
		if(e.hasItem() && (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK)) {
			ItemStack item = e.getItem();
			if(item.getType() == Material.POTION) { return; }
			Player p = e.getPlayer();
			
			if(Hive.server_swap.containsKey(p.getName())) { return; }
			
			if(item.getType() == Material.DIAMOND_HELMET || item.getType() == Material.DIAMOND_CHESTPLATE || item.getType() == Material.DIAMOND_LEGGINGS || item.getType() == Material.DIAMOND_BOOTS || item.getType() == Material.IRON_HELMET || item.getType() == Material.IRON_CHESTPLATE || item.getType() == Material.IRON_LEGGINGS || item.getType() == Material.IRON_BOOTS || item.getType() == Material.CHAINMAIL_HELMET || item.getType() == Material.CHAINMAIL_CHESTPLATE || item.getType() == Material.CHAINMAIL_LEGGINGS || item.getType() == Material.CHAINMAIL_BOOTS || item.getType() == Material.GOLD_HELMET || item.getType() == Material.GOLD_CHESTPLATE || item.getType() == Material.GOLD_LEGGINGS || item.getType() == Material.GOLD_BOOTS || item.getType() == Material.LEATHER_HELMET || item.getType() == Material.LEATHER_CHESTPLATE || item.getType() == Material.LEATHER_LEGGINGS || item.getType() == Material.LEATHER_BOOTS) {
				if(!(hasCustomName(item)) || !(hasLore(item))) {
					e.setCancelled(true);
					e.setUseItemInHand(Result.DENY);
					p.updateInventory();
					return;
				}
			}
			
			if(ItemMechanics.getArmorData(item).equalsIgnoreCase("no")) { return; } // Not a piece of armor.
			if(p.getInventory().getItem(ItemMechanics.getRespectiveArmorSlot(item.getType())) != null) { return; } // There's an item equipped.
			e.setCancelled(true);
			e.setUseItemInHand(Result.DENY);
			p.updateInventory();
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onItemHeldChangeEvent(PlayerItemHeldEvent e) {
		if(player_potion_event.containsKey(e.getPlayer().getName())) {
			player_potion_event.remove(e.getPlayer().getName());
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void preventFakeHealthPotions(InventoryClickEvent e) {
		Player p = (Player) e.getWhoClicked();
		if(player_potion_event.containsKey(p.getName())) {
			player_potion_event.remove(p.getName());
		}
	}
	
	@SuppressWarnings("unused")
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onInventoryClickEvent(InventoryClickEvent e) {
		if(e.getSlotType() != SlotType.ARMOR && !(e.isShiftClick())) { return; }
		
		final Player p = (Player) e.getWhoClicked();
		
		if(!(e.isLeftClick()) && !(e.isShiftClick()) && !(e.isRightClick())) { return; }
		
		if(!(e.isShiftClick()) && (e.getCursor() != null) && (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR) && e.getSlotType() == SlotType.ARMOR) {
			if(!(e.getSlot() == ItemMechanics.getRespectiveArmorSlot(e.getCursor().getType()))) { return; }
		}
		
		if(!e.getInventory().getName().equalsIgnoreCase("container.crafting")) { return; }
		
		ItemStack old_armor = e.getCurrentItem();
		ItemStack new_armor = e.getCursor();
		
		String old_armor_name = "???";
		String new_armor_name = "???";
		
		Material m = new_armor.getType();
		
		if(e.isShiftClick() && e.getSlotType() == SlotType.CRAFTING) { return;
		// Idiots.
		}
		
		if(!(e.isShiftClick()) && new_armor != null && new_armor.getType() != Material.AIR && (!(hasCustomName(new_armor)) || !(hasLore(new_armor)))) {
			e.setCancelled(true);
			p.updateInventory();
			return; // Not custom.
		}
		
		if(e.isShiftClick() && (e.getSlot() != 36 && e.getSlot() != 37 && e.getSlot() != 38 && e.getSlot() != 39)) { // Equpping, not de-equipping.
			if(!ItemMechanics.isSlotEmpty(p, e.getCurrentItem().getType())) { return; }
			//if(!(ItemMechanics.isArmor(e.getCurrentItem()))){return;} Moved bellow.
			if(!(e.getInventory().getType() == InventoryType.CRAFTING)) { return; }
			
			new_armor = e.getCurrentItem();
			m = new_armor.getType();
			old_armor = p.getInventory().getItem(ItemMechanics.getRespectiveArmorSlot(m));
		}
		
		if(((e.isShiftClick()) && new_armor != null && new_armor.getType() != Material.AIR) && (!(hasCustomName(new_armor)) || !(hasLore(new_armor)))) {
			e.setCancelled(true);
			if(!e.getInventory().getName().contains("Merchant")) {
				p.updateInventory();
			}
			return; // Not custom.
		}
		
		if(e.isShiftClick() && !(ItemMechanics.isArmor(e.getCurrentItem()))) {
			// This check must occur after the above check, otherwise non-custom items just return "no" and isArmor = false -> function returns.
			return;
		}
		
		if(new_armor != null && DuelMechanics.duel_max_armor_tier.containsKey(p.getName()) && DuelMechanics.duel_map.containsKey(p.getName())) {
			// Let's check if they're allowed to equip it.
			int max_armor = DuelMechanics.duel_max_armor_tier.get(p.getName());
			int armor_tier = ItemMechanics.getItemTier(new_armor);
			
			if(armor_tier > max_armor) {
				// Do not allow.
				p.sendMessage(ChatColor.RED + "You " + ChatColor.UNDERLINE + "cannot" + ChatColor.RED + " use this tier of armor in this duel.");
				/*if(!e.isShiftClick()){
					e.setCursor(new_armor);
					e.setCurrentItem(new ItemStack(Material.AIR));
				}*/
				e.setCancelled(true);
				p.updateInventory();
				final String p_name = p.getName();
				
				Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
					public void run() {
						ItemMechanics.updatePlayerStats(p_name);
					}
				}, 2L);
				return;
			}
		}
		
		boolean subtract_hp = false;
		int max_health = getMaxHealthValue(p.getName());
		if((new_armor == null || new_armor.getType() == Material.AIR) && old_armor != null && old_armor.getType() != Material.AIR) {
			// Removing armor.
			if(old_armor == null) { return; }
			
			if(e.isShiftClick() && p.getInventory().firstEmpty() == -1) { // No room to disequip.
				return;
			}
			
			int old_health_mod = getHealthVal(old_armor);
			int new_max_hp = max_health - old_health_mod;
			
			new_armor_name = ChatColor.GRAY.toString() + "NOTHING";
			CraftItemStack css = (CraftItemStack) old_armor;
			
			if(hasCustomName(old_armor)) {
				old_armor_name = CraftItemStack.asNMSCopy(css).getTag().getCompound("display").getString("Name");
			} else {
				old_armor_name = old_armor.getType().toString();
			}
			
			Random r = new Random();
			float minX = 0.20f;
			float maxX = 0.25f;
			float pitch_mod = 1.0F + (r.nextFloat() * (maxX - minX) + minX);
			p.playSound(p.getLocation(), Sound.ITEM_BREAK, 1.00F, pitch_mod);
			
			int cur_hp = getPlayerHP(p.getName());
			if(cur_hp > new_max_hp) {
				cur_hp = new_max_hp;
			}
			
			p.sendMessage("");
			p.sendMessage(ChatColor.WHITE + "" + old_armor_name + "" + ChatColor.WHITE + ChatColor.BOLD + " -> " + ChatColor.WHITE + "" + new_armor_name + "");
			p.sendMessage(ChatColor.RED + "-" + old_health_mod + " MAX HP [" + cur_hp + "/" + new_max_hp + "HP]");
		}
		
		if(!(e.isShiftClick()) && (new_armor != null) && e.getSlotType() == SlotType.ARMOR) { // (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR) &&
			if(!(e.getSlot() == ItemMechanics.getRespectiveArmorSlot(new_armor.getType()))) {
				Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
					public void run() {
						double new_max_hp = generateMaxHP(p);
						setLocalMaxHealth(p.getName(), (int) new_max_hp);
						
						if(getPlayerHP(p.getName()) > (int) new_max_hp) {
							//p.setLevel((int)new_max_hp);
							setPlayerHP(p.getName(), (int) new_max_hp);
						}
						
						double d_level = getPlayerHP(p.getName());
						
						double health_percent = d_level / new_max_hp;
						double new_health_display = (health_percent * 20.0D);
						//log.info(String.valueOf(health_percent));
						//log.info(String.valueOf(new_health_display));
						
						int conv_newhp_display = (int) Math.abs(new_health_display);
						if(conv_newhp_display < 1) {
							conv_newhp_display = 1;
						}
						if(conv_newhp_display > 20) {
							conv_newhp_display = 20;
						}
						
						p.setHealth(conv_newhp_display);
						
					}
				}, 1L);
				return;
			}
		}
		
		if(!(new_armor == null) && new_armor.getType() != Material.AIR) { // Equpping new armor.
			int old_health_mod = 0;
			if(old_armor == null || old_armor.getType() == Material.AIR) {
				old_health_mod = 0;
			} else {
				old_health_mod = getHealthVal(old_armor);
			}
			
			int new_health_mod = getHealthVal(new_armor);
			
			int hp_to_add = new_health_mod - old_health_mod;
			if(hp_to_add <= 0) {
				subtract_hp = true;
				hp_to_add = Math.abs(hp_to_add);
			}
			
			if(subtract_hp == true) {
				int new_max_hp = max_health - hp_to_add;
				
				if(old_armor == null || old_armor.getType() == Material.AIR) {
					old_armor_name = ChatColor.GRAY.toString() + "NOTHING";
				} else if(old_armor != null && old_armor.getType() != Material.AIR) {
					old_armor_name = CraftItemStack.asNMSCopy(old_armor).getTag().getCompound("display").getString("Name");
				}
				
				new_armor_name = CraftItemStack.asNMSCopy(new_armor).getTag().getCompound("display").getString("Name");
				Random r = new Random();
				float minX = 0.20f;
				float maxX = 0.25f;
				float pitch_mod = 1.0F + (r.nextFloat() * (maxX - minX) + minX);
				p.playSound(p.getLocation(), Sound.ITEM_BREAK, 1.00F, pitch_mod);
				
				int cur_hp = getPlayerHP(p.getName());
				if(cur_hp > new_max_hp) {
					cur_hp = new_max_hp;
				}
				
				p.sendMessage("");
				p.sendMessage(ChatColor.WHITE + "" + old_armor_name + "" + ChatColor.WHITE + ChatColor.BOLD + " -> " + ChatColor.WHITE + "" + new_armor_name + "");
				p.sendMessage(ChatColor.RED + "-" + hp_to_add + " MAX HP [" + cur_hp + "/" + new_max_hp + "HP]");
			}
			
			if(subtract_hp == false) {
				
				double new_max_hp = max_health + hp_to_add;
				
				if(old_armor == null || old_armor.getType() == Material.AIR) {
					old_armor_name = ChatColor.GRAY.toString() + "NOTHING";
				} else if(old_armor != null && old_armor.getType() != Material.AIR) {
					old_armor_name = CraftItemStack.asNMSCopy(old_armor).getTag().getCompound("display").getString("Name");
				}
				
				new_armor_name = CraftItemStack.asNMSCopy(new_armor).getTag().getCompound("display").getString("Name");
				Random r = new Random();
				float minX = 0.20f;
				float maxX = 0.25f;
				float pitch_mod = 1.0F + (r.nextFloat() * (maxX - minX) + minX);
				p.playSound(p.getLocation(), Sound.ITEM_BREAK, 1.00F, pitch_mod);
				
				int cur_hp = getPlayerHP(p.getName());
				if(cur_hp > new_max_hp) {
					cur_hp = (int) new_max_hp;
				}
				
				/*if(e.isShiftClick() && e.getCursor() == null && e.getResult() == Result.DENY){
					e.setResult(Result.DENY);
					e.setCancelled(true);
					new_armor = e.getCurrentItem();
					p.getInventory().setItem(ItemMechanics.getRespectiveArmorSlot(new_armor.getType()), new_armor);
					e.setCurrentItem(new ItemStack(Material.AIR));
				}*/
				// Nasty hack.
				
				p.sendMessage("");
				p.sendMessage(ChatColor.WHITE + "" + old_armor_name + "" + ChatColor.WHITE + ChatColor.BOLD + " -> " + ChatColor.WHITE + "" + new_armor_name + "");
				p.sendMessage(ChatColor.GREEN + "+" + hp_to_add + " MAX HP [" + cur_hp + "/" + (int) new_max_hp + "HP]");
			}
		}
		
		ItemMechanics.need_update.add(p.getName());
		// This will process the rest of their armor values.
		
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
			public void run() {
				double new_max_hp = generateMaxHP(p);
				setLocalMaxHealth(p.getName(), (int) new_max_hp);
				
				if(getPlayerHP(p.getName()) > (int) new_max_hp) {
					setPlayerHP(p.getName(), (int) new_max_hp);
					//p.setLevel((int)new_max_hp);
				}
				
				double d_level = getPlayerHP(p.getName());
				
				double health_percent = d_level / new_max_hp;
				double new_health_display = (health_percent * 20.0D);
				
				int conv_newhp_display = (int) Math.abs(new_health_display);
				if(conv_newhp_display < 1) {
					conv_newhp_display = 1;
				}
				if(conv_newhp_display > 20) {
					conv_newhp_display = 20;
				}
				
				p.setHealth(conv_newhp_display);
				
			}
		}, 1L);
		
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerJoinEvent(PlayerJoinEvent e) {
		final Player p = e.getPlayer();
		p.setFireTicks(0);
		p.setFallDistance(0.0F);
		
		if(!(Hive.player_first_login.containsKey(p.getName()))) {
			// Not a new player, conversion conflict.
			Hive.player_first_login.put(p.getName(), 1L);
		}
		
		if(noob_players.contains(p.getName()) && (System.currentTimeMillis() - Hive.player_first_login.get(p.getName())) <= (24 * 3600000)) {
			//noob_players.add(p.getName());
			long hours = (System.currentTimeMillis() - Hive.player_first_login.get(p.getName())) / 3600000;
			int i_hours = 24 - (int) hours;
			p.sendMessage("");
			p.sendMessage(ChatColor.RED + "You have " + ChatColor.BOLD + i_hours + "h " + ChatColor.RED + "left in your 'Newbie Protection'. After this time expires, you will loose items as you normally would when PK'd.");
			p.sendMessage(ChatColor.GRAY.toString() + ChatColor.UNDERLINE + "WARNING: " + ChatColor.GRAY + "If you engage in PvP with another player, you will forfeit this protection.");
		}
		
		boolean combat_log = isCombatLogger(p.getName());
		
		if(combat_log) {
			// Combat logger!
			
			/*p.getInventory().clear(); // Nullify inventory.
			p.getInventory().setHelmet(new ItemStack(Material.AIR));
			p.getInventory().setChestplate(new ItemStack(Material.AIR));
			p.getInventory().setLeggings(new ItemStack(Material.AIR));
			p.getInventory().setBoots(new ItemStack(Material.AIR));*/
			
			//combat_logger.add(p.getName());
			
			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
				public void run() { // Delay due to delay on HP set.
					//p.setLevel(0);
					//setPlayerHP(p.getName(), 0);
					if(Bukkit.getPlayer(p.getName()) != null) {
						Player p_updated = Bukkit.getPlayer(p.getName());
						if(p_updated.getHealth() > 0) {
							p_updated.setHealth(0); // Kill that bitch.
							p_updated.setExp(0.0F);
						}
					}
				}
			}, 10L);
			
			// Remove mule items I suppose.
			if(p.getInventory().contains(Material.LEASH)) {
				Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
					public void run() {
						MountMechanics.mule_itemlist_string.remove(p.getName());
						MountMechanics.mule_inventory.remove(p.getName());
					}
				}, 10L);
			}
			
			p.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "* YOU LOGGED OUT IN COMBAT, AND YOU WERE KILLED *");
			return;
		}
		
		if(!combat_log) {
			// They did not combat log. We'll give them 3 seconds of invinsibility on login.
			p.setFireTicks(0);
			p.setFallDistance(0.0F);
			RealmMechanics.player_god_mode.put(p.getName(), System.currentTimeMillis());
			RealmMechanics.playPotionEffect(p, p, 0x00FFFB, 80);
			for(Player pl : p.getWorld().getPlayers()) {
				RealmMechanics.playPotionEffect(pl, p, 0x00FFFB, 80);
			}
			
			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
				public void run() {
					RealmMechanics.player_god_mode.remove(p.getName());
				}
			}, 3 * 20L);
		}
		
		if(getPlayerHP(p.getName()) <= 0 && Hive.first_login.contains(p.getName())) {
			// Fix for new players.
			//p.setLevel(50);
			setPlayerHP(p.getName(), 50);
		}
		
		if(getPlayerHP(p.getName()) <= 0 && p.getHealth() <= 0) {
			// They're dead.
			p.teleport(SpawnMechanics.getRandomSpawnPoint(p.getName()));
		}
		
		int gear_maxhp = generateMaxHP(p); // Armor based max-hp.
		float old_energy = p.getExp();
		p.setExp(old_energy);
		
		if(getPlayerHP(p.getName()) <= 0 && p.getHealth() > 0) {
			//p.setLevel(gear_maxhp);
			setPlayerHP(p.getName(), gear_maxhp);
		}
		
		if(getPlayerHP(p.getName()) >= 1) {
			setOverheadHP(p, getPlayerHP(p.getName()));
		}
		
		health_data.put(p.getName(), gear_maxhp);
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		thrown_potion_map.remove(p.getName());
		player_potion_event.remove(p.getName());
		move_potion_slot.remove(p.getName());
		noob_players.remove(p.getName());
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerDeath(PlayerDeathEvent e) {
		Player p = (Player) e.getEntity();
		p.playSound(p.getLocation(), Sound.WITHER_SPAWN, 1F, 1F);
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerRespawn(PlayerRespawnEvent e) {
		Player p = e.getPlayer();
		if(TutorialMechanics.onTutorialIsland(p)) { return; }
		
		if(p.getLocation().getWorld().getName().equalsIgnoreCase(e.getRespawnLocation().getWorld().getName()) && p.getLocation().distanceSquared(e.getRespawnLocation()) <= 2) {
			// They're respawning on themselves, ignore.
			// Non-legit death, they won't loose any items.
			return;
		}
		
		final String p_name = p.getName();
		
		if((PlayerManager.getPlayerModel(p_name).getDeathLocation() != null && !DuelMechanics.isDamageDisabled(PlayerManager.getPlayerModel(p_name).getDeathLocation())) && !(PlayerManager.getPlayerModel(p_name).getToggleList() != null && PlayerManager.getPlayerModel(p_name).getToggleList().contains("starterpack"))) {
			// Make sure they didn't die in a safe zone.
			try {
				p.getInventory().setItem(p.getInventory().firstEmpty(), RealmMechanics.makeUntradeable(ItemMechanics.generateNoobWeapon()));
				
				p.getInventory().setItem(p.getInventory().firstEmpty(), RealmMechanics.makeUntradeable(ItemMechanics.signNewCustomItem(Material.POTION, (short) 1, ChatColor.WHITE.toString() + "Minor Health Potion", ChatColor.GRAY.toString() + "A potion that restores " + ChatColor.WHITE.toString() + "10HP")));
				p.getInventory().setItem(p.getInventory().firstEmpty(), RealmMechanics.makeUntradeable(ItemMechanics.signNewCustomItem(Material.POTION, (short) 1, ChatColor.WHITE.toString() + "Minor Health Potion", ChatColor.GRAY.toString() + "A potion that restores " + ChatColor.WHITE.toString() + "10HP")));
				p.getInventory().setItem(p.getInventory().firstEmpty(), RealmMechanics.makeUntradeable(ItemMechanics.signNewCustomItem(Material.POTION, (short) 1, ChatColor.WHITE.toString() + "Minor Health Potion", ChatColor.GRAY.toString() + "A potion that restores " + ChatColor.WHITE.toString() + "10HP")));
				p.getInventory().setItem(p.getInventory().firstEmpty(), RealmMechanics.makeUntradeable(new ItemStack(Material.BREAD, 1)));
				p.getInventory().setItem(p.getInventory().firstEmpty(), RealmMechanics.makeUntradeable(new ItemStack(Material.BREAD, 1)));
			} catch(Exception err) {
				err.printStackTrace();
				// Do nothing. Called when they have no room (duel death bug)
			}
		}
		
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
			public void run() {
				Player p = Bukkit.getPlayer(p_name);
				if(p != null) {
					//p.setLevel(50);
					setPlayerHP(p.getName(), 50);
				}
			}
		}, 10L);
		
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
			public void run() {
				Player p = Bukkit.getPlayer(p_name);
				if(p != null) {
					//p.setLevel(50);
					setPlayerHP(p.getName(), 50);
					setLocalMaxHealth(p.getName(), 50);
					p.setHealth(20);
					p.setFoodLevel(20);
					p.setExp(1.0F);
					p.setWalkSpeed(0.2F);
					
					in_combat.remove(p.getName()); // Remove them from combat once they've respawned.
					p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 60, 1));
					p.playSound(p.getLocation(), Sound.ZOMBIE_UNFECT, 1F, 1.5F);
				}
			}
		}, 5L);
		
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
			public void run() {
				Player p = Bukkit.getPlayer(p_name);
				if(p != null) {
					ItemMechanics.generateTotalArmorVal(p);
					ItemMechanics.generateTotalBlockChance(p);
					ItemMechanics.generateTotalDodgeChance(p);
					ItemMechanics.generateTotalGoldFindChance(p);
					ItemMechanics.generateTotalItemFindChance(p);
					ItemMechanics.generateTotalReflectChance(p);
					ItemMechanics.generateTotalThornVal(p);
				}
			}
		}, 5L);
		
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void EntityRegainHealthEvent(EntityRegainHealthEvent e) {
		if(!(e.getEntity() instanceof Player)) { return; }
		if(e.getRegainReason() == RegainReason.SATIATED) {
			e.setAmount(0);
			e.setCancelled(true);
			return;
		}
		
		e.setCancelled(true);
		Player p = (Player) e.getEntity();
		double regain_amount = e.getAmount();
		double total_hp = getPlayerHP(p.getName());
		double max_hp = getMaxHealthValue(p.getName());
		
		if(total_hp == max_hp) { return; }
		
		if(total_hp + regain_amount >= max_hp) {
			//p.setLevel((int)max_hp);
			setPlayerHP(p.getName(), (int) max_hp);
			p.setHealth(20);
			return;
		}
		
		//p.setLevel((int)(regain_amount + total_hp));
		setPlayerHP(p.getName(), (int) (regain_amount + total_hp));
		double health_percent = (total_hp + regain_amount) / max_hp;
		double new_health_display = health_percent * 20;
		
		if(new_health_display >= 20) {
			if(health_percent >= 1) {
				new_health_display = 20;
			} else {
				new_health_display = 19;
			}
		}
		
		p.setHealth((int) new_health_display);
	}
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = false)
	// We don't need to worry about 'in combat' for duels anyway, so.
	public void onEntityDamageEntityEvent(EntityDamageByEntityEvent e) {
		if(!(e.getDamager() instanceof Player)) { return; }
		
		Player p = (Player) e.getDamager();
		if(e.getDamage() > 0) {
			in_combat.put(p.getName(), System.currentTimeMillis());
			p.playSound(p.getLocation(), Sound.HURT_FLESH, 1F, 1F); // Cool sound effect WAAAAAYYY!
		}
		
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onNPCDamageEvent(EntityDamageEvent e) {
		Entity ent = e.getEntity();
		if(ent.hasMetadata("NPC")) {
			Player p = (Player) ent;
			if(Hive.player_to_npc.containsKey(p.getName())) { return; // They're a combat NPC, they should be hurt.
			}
			
			e.setCancelled(true);
			e.setDamage(0);
		}
	}
	
	@EventHandler
	public void onForeignDamageEvent(EntityDamageEvent e) {
		Entity ent = e.getEntity();
		if(ent.getPassenger() != null) {
			ent = ent.getPassenger();
		}
		if(!(ent.getType() == EntityType.PLAYER)) { return; }
		if(e.getCause() == DamageCause.ENTITY_ATTACK || e.getCause() == DamageCause.PROJECTILE || e.getCause() == DamageCause.CUSTOM) { return; }
		
		Player p = (Player) ent;
		
		if(DuelMechanics.isDamageDisabled(p.getLocation()) && !(e.getCause() == DamageCause.SUFFOCATION || e.getCause() == DamageCause.VOID)) {
			e.setCancelled(true);
			e.setDamage(0);
			return; // No damage in DMG-OFF.
		}
		
		double dmg = e.getDamage();
		double max_hp = getMaxHealthValue(p.getName());
		
		if(RealmMechanics.player_god_mode.containsKey(p.getName())) {
			e.setCancelled(true);
			e.setDamage(0);
			return;
		}
		
		if(last_environ_dmg.containsKey(p.getName())) {
			long last_time = last_environ_dmg.get(p.getName());
			if((System.currentTimeMillis() - last_time) <= 800) { // 1 dmg_event/sec.
				e.setCancelled(true);
				e.setDamage(0);
				return;
			}
		}
		
		if(e.getCause() == DamageCause.ENTITY_EXPLOSION) {
			ItemStack wep = p.getItemInHand();
			if(wep.getType() == Material.WOOD_HOE || wep.getType() == Material.STONE_HOE || wep.getType() == Material.IRON_HOE || wep.getType() == Material.DIAMOND_HOE || wep.getType() == Material.GOLD_HOE) {
				e.setDamage(0);
				e.setCancelled(true);
				return;
			}
		}
		
		if(e.getCause() == DamageCause.WITHER) {
			e.setDamage(0);
			e.setCancelled(true);
			return;
		}
		
		if(e.getCause() == DamageCause.FALL) {
			double blocks = dmg;
			if(blocks >= 2) {
				dmg = (max_hp * 0.02D) * blocks;
			}
			if(dmg > getPlayerHP(p.getName())) {
				// This would normally kill them, let's be nice for now as this could
				// be causing insta-death bugs on /shard etc etc
				dmg = (getPlayerHP(p.getName()) - 1);
			}
			
			if(blocks >= 49 && dmg <= getPlayerHP(p.getName())) {
				AchievmentMechanics.addAchievment(p.getName(), "Leap of Faith");
			}
		}
		
		if(e.getCause() == DamageCause.DROWNING) {
			dmg = (max_hp * 0.04D);
		}
		
		if(e.getCause() == DamageCause.FIRE_TICK) {
			if(!(p.hasPotionEffect(PotionEffectType.FIRE_RESISTANCE)) && !(DuelMechanics.duel_map.containsKey(p.getName()))) {
				dmg = (max_hp * 0.01D);
			} else {
				dmg = 0;
			}
		}
		
		if(e.getCause() == DamageCause.LAVA && !(p.hasPotionEffect(PotionEffectType.FIRE_RESISTANCE))) {
			if(!(p.hasPotionEffect(PotionEffectType.FIRE_RESISTANCE))) {
				dmg = (max_hp * 0.03D);
			} else {
				dmg = 0;
			}
		}
		
		if(e.getCause() == DamageCause.FIRE && !(p.hasPotionEffect(PotionEffectType.FIRE_RESISTANCE))) {
			if(!(p.hasPotionEffect(PotionEffectType.FIRE_RESISTANCE))) {
				dmg = (max_hp * 0.03D);
			} else {
				dmg = 0;
			}
		}
		
		if(e.getCause() == DamageCause.POISON) {
			if(!(DuelMechanics.duel_map.containsKey(p.getName()))) {
				dmg = (max_hp * 0.01D);
			} else {
				dmg = 0;
			}
		}
		
		if(e.getCause() == DamageCause.SUFFOCATION) {
			dmg = 0;
			Location p_loc = p.getLocation();
			while((p_loc.getBlock().getType() != Material.AIR || p_loc.add(0, 1, 0).getBlock().getType() != Material.AIR) && p_loc.getY() < 255) {
				p_loc.add(0, 1, 0);
				// Already adds in the function above.
			}
			
			p.setLastDamageCause(e);
			p.teleport(p_loc);
		}
		
		if(e.getCause() == DamageCause.VOID) {
			if((p.getWorld().getName().equalsIgnoreCase(Bukkit.getWorlds().get(0).getName()) || InstanceMechanics.isInstance(p.getWorld().getName()))) {
				/*if(p.getWorld().getName().contains("fireydungeon.")){
					p.damage(p.getHealth());
					return;
				}*/
				
				// TODO: Instance teleportation on void fall.
				dmg = 0;
				Location p_loc = p.getLocation();
				while((p_loc.getBlock().getType() == Material.AIR && p_loc.getY() < 255) || p_loc.getY() <= 0) {
					// 	They're falling into empty space. Move them up to where blocks exist, then let suffocation take over.
					p_loc.add(0, 1, 0);
				}
				if(p_loc.getY() >= 250) {
					// Teleport them to spawn.
					if(InstanceMechanics.saved_location_instance.containsKey(p.getName())) {
						p_loc = InstanceMechanics.saved_location_instance.get(p.getName());
					} else {
						p_loc = SpawnMechanics.getRandomSpawnPoint(p.getName());
					}
				}
				p_loc.add(0, 1, 0);
				
				p.setLastDamageCause(e);
				p.teleport(p_loc);
			}
		}
		
		if(dmg > 0 && !e.isCancelled()) {
			if(KarmaMechanics.getRawAlignment(p.getName()).equalsIgnoreCase("good") && KarmaMechanics.plast_hit.containsKey(p.getName()) && (System.currentTimeMillis() - KarmaMechanics.last_hit_time.get(p.getName()) <= (6 * 1000))) {
				if(Bukkit.getPlayer(KarmaMechanics.plast_hit.get(p.getName())) != null) {
					Player p_attacker = Bukkit.getPlayer(KarmaMechanics.plast_hit.get(p.getName()));
					if(PlayerManager.getPlayerModel(p_attacker).getToggleList() != null){
						if(PlayerManager.getPlayerModel(p_attacker).getToggleList().contains("chaos")){
							// Don't kill the player.
							//p_attacker.sendMessage(ChatColor.RED + "You " + ChatColor.UNDERLINE + "cannot" + ChatColor.RED + " attack the lawful player (" + p.getName() + ") with /togglepvp enabled.");
							e.setCancelled(true);
							e.setDamage(0);
							return;
						}
					}
				}
			}
		}
		
		MonsterMechanics.player_slow.put(p.getName(), System.currentTimeMillis());
		
		if(dmg > 0 && !e.isCancelled()) {
			p.setWalkSpeed(0.165F);
		}
		
		last_environ_dmg.put(p.getName(), System.currentTimeMillis());
		if(!(e.isCancelled())) {
			e.setDamage((int) dmg);
		}
		if(dmg <= 0) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onArrowBounceHit(EntityDamageByEntityEvent e) {
		if(e.getCause() == DamageCause.PROJECTILE) {
			if(e.getDamager() instanceof Arrow) {
				Arrow a = (Arrow) e.getDamager();
				a.setBounce(false);
				a.remove();
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
	public void onEntityDamageEvent(EntityDamageEvent e) {
		Entity ent = e.getEntity();
		if(ent.getPassenger() != null) {
			ent = ent.getPassenger();
		}
		if(!(ent.getType() == EntityType.PLAYER)) { return; }
		
		if(e.getDamage() <= 0) {
			e.setCancelled(true);
			return;
		}
		
		if(e.getCause() == DamageCause.STARVATION || e.getCause() == DamageCause.SUICIDE || e.getCause() == DamageCause.SUFFOCATION || e.getCause() == DamageCause.VOID) {
			e.setDamage(0);
			e.setCancelled(true);
			return;
		}
		
		Player p = (Player) ent;
		
		if(RealmMechanics.player_god_mode.containsKey(p.getName())) {
			e.setCancelled(true);
			e.setDamage(0);
			return;
		}
		
		last_hit_location.put(p.getName(), p.getLocation());
		
		Entity e_attacker = null;
		
		if(e instanceof EntityDamageByEntityEvent && (e.getCause() == DamageCause.ENTITY_ATTACK || e.getCause() == DamageCause.PROJECTILE)) {
			if(e.getCause() == DamageCause.ENTITY_ATTACK) {
				e_attacker = ((EntityDamageByEntityEvent) e).getDamager();
			} else {
				try {
					Arrow a = (Arrow) ((EntityDamageByEntityEvent) e).getDamager();
					e_attacker = (Entity) a.getShooter();
				} catch(ClassCastException cce) {
					e.setDamage(1);
					return;
				}
			}
		}
		
		//e.isCancelled() && 
		if(e.isCancelled() && ((DuelMechanics.isDamageDisabled(p.getLocation())) || (e_attacker != null && e_attacker instanceof Player && DuelMechanics.isPvPDisabled(e_attacker.getLocation()))) && (!(Hive.player_to_npc.containsKey(p.getName())))) {
			// If it's not an NPC, the event is cancelled, and damage/pvp is disabled, we return out of here.
			if(e_attacker instanceof Player && !(DuelMechanics.duel_map.containsKey(((Player) e_attacker).getName()))) {
				e.setDamage(0);
			}
			if(!(DuelMechanics.duel_map.containsKey(p.getName())) || RealmMechanics.player_god_mode.containsKey(p.getName())) {
				// They're not in a duel, kill the damage. OR They have post-duel godmode, either way kill the damage.				
				e.setDamage(0);
			}
			e.setCancelled(true);
			return;
		}
		
		if(!(e.getCause() == DamageCause.FALL)) {
			in_combat.put(p.getName(), System.currentTimeMillis());
		}
		
		p.setLastDamageCause(e);
		
		double max_hp = getMaxHealthValue(p.getName());
		
		if(getPlayerHP(p.getName()) > max_hp) {
			max_hp = generateMaxHP(p);
			health_data.put(p.getName(), (int) max_hp);
			//TODO: this could cause an exploit; use need_update ?
		}
		
		double dmg = e.getDamage();
		//log.info("" + dmg);
		
		double total_hp = getPlayerHP(p.getName());
		double new_hp = total_hp - dmg;
		
		if(KarmaMechanics.getRawAlignment(p.getName()).equalsIgnoreCase("good") && KarmaMechanics.plast_hit.containsKey(p.getName()) && (System.currentTimeMillis() - KarmaMechanics.last_hit_time.get(p.getName()) <= (6 * 1000))) {
			// Looks like someone will be punished if they die, let's see if they should be KO'd.
			
			// Check to see if they have /togglepvp
			if(Bukkit.getPlayer(KarmaMechanics.plast_hit.get(p.getName())) != null) {
				Player p_attacker = Bukkit.getPlayer(KarmaMechanics.plast_hit.get(p.getName()));
				if(PlayerManager.getPlayerModel(p_attacker).getToggleList() != null){
					if(PlayerManager.getPlayerModel(p_attacker).getToggleList().contains("chaos")){
						// Don't kill the player.
						p_attacker.sendMessage(ChatColor.RED + "You " + ChatColor.UNDERLINE + "cannot" + ChatColor.RED + " attack the lawful player (" + p.getName() + ") with /togglepvp enabled.");
						e.setCancelled(true);
						e.setDamage(0);
						return;
					}
				}
			}
		}
		
		if(new_hp <= 0) { // They're dead.
			if(noob_players.contains(p.getName()) && KarmaMechanics.plast_hit.containsKey(p.getName())) {
				// PvP kill on a noob.
				if((System.currentTimeMillis() - Hive.player_first_login.get(p.getName())) <= (24 * 3600000)) {
					long hours = (System.currentTimeMillis() - Hive.player_first_login.get(p.getName())) / 3600000;
					int i_hours = 24 - (int) hours;
					String killer_name = KarmaMechanics.plast_hit.get(p.getName());
					p.teleport(SpawnMechanics.getRandomSpawnPoint(p.getName()));
					p.setHealth(1); // They're in a duel, 1hp them instead.
					//p.setLevel(1);
					setPlayerHP(p.getName(), 1);
					e.setDamage(0);
					p.sendMessage("");
					p.sendMessage(ChatColor.RED + "You have " + ChatColor.BOLD + i_hours + "h " + ChatColor.RED + "left in your Newbie Protection. Afterwards you will lose items on death to players.");
					if(Bukkit.getPlayer(killer_name) != null) {
						Player killer = Bukkit.getPlayer(killer_name);
						killer.sendMessage(ChatColor.RED + "You have killed a player under Newbie Protection [" + ChatColor.BOLD + p.getName() + ChatColor.RED + "] You will not recieve loot, chaotic points or kills.");
					}
					return;
				} else {
					noob_players.remove(p.getName());
				}
				
			}
			if(!DuelMechanics.duel_map.containsKey(p.getName()) && !(DuelMechanics.isDamageDisabled(p.getLocation()))) {
				// They're not in a duel, not in a cooldown, and damage is enabled.
				PlayerManager.getPlayerModel(p).setDeathLocation(p.getLocation());
				p.setSneaking(false);
				p.setHealth(0);
				//p.setLevel(0);
				setPlayerHP(p.getName(), 0);
				p.playEffect(EntityEffect.DEATH);
				return;
			}
			p.setHealth(1); // They're in a duel, 1hp them instead.
			setPlayerHP(p.getName(), 1);
			//p.setLevel(1);
			e.setCancelled(true);
			return;
		}
		
		setPlayerHP(p.getName(), (int) new_hp);
		//p.setLevel((int)new_hp);
		
		double health_percent = (new_hp / max_hp);
		double new_health_display = (health_percent * 20.0D);
		
		int conv_newhp_display = (int) new_health_display;
		if(conv_newhp_display <= 0) {
			conv_newhp_display = 1;
		}
		if(conv_newhp_display > 20) {
			conv_newhp_display = 20;
		}
		p.setHealth(conv_newhp_display);
		
		e.setDamage(0);
		e.setCancelled(true);
		
		if(dmg > 0) {
			// PLAYER KNOCKBACK CODE **
			if(e_attacker != null) {
				org.bukkit.util.Vector unitVector = p.getLocation().toVector().subtract(e_attacker.getLocation().toVector()).normalize();
				p.setVelocity(unitVector.multiply(0.5F));
			}
			p.playEffect(EntityEffect.HURT);
		}
		
	}
}
