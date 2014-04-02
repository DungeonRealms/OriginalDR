package me.vaqxine.ProfessionMechanics;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import me.vaqxine.Main;
import me.vaqxine.AchievmentMechanics.AchievmentMechanics;
import me.vaqxine.CommunityMechanics.CommunityMechanics;
import me.vaqxine.DuelMechanics.DuelMechanics;
import me.vaqxine.EnchantMechanics.EnchantMechanics;
import me.vaqxine.HealthMechanics.HealthMechanics;
import me.vaqxine.Hive.ParticleEffect;
import me.vaqxine.ItemMechanics.ItemMechanics;
import me.vaqxine.MerchantMechanics.MerchantMechanics;
import me.vaqxine.MoneyMechanics.MoneyMechanics;
import me.vaqxine.PetMechanics.PetMechanics;
import me.vaqxine.ProfessionMechanics.commands.CommandHideFish;
import me.vaqxine.ProfessionMechanics.commands.CommandProf;
import me.vaqxine.ProfessionMechanics.commands.CommandSetFish;
import me.vaqxine.ProfessionMechanics.commands.CommandSetOre;
import me.vaqxine.ProfessionMechanics.commands.CommandShowFish;
import me.vaqxine.RealmMechanics.RealmMechanics;
import me.vaqxine.RepairMechanics.RepairMechanics;
import net.minecraft.server.v1_7_R2.Packet;
import net.minecraft.server.v1_7_R2.PacketPlayOutWorldEvent;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.Furnace;
import org.bukkit.craftbukkit.v1_7_R2.CraftServer;
import org.bukkit.craftbukkit.v1_7_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_7_R2.inventory.CraftItemStack;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerFishEvent.State;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

// TODO: (maybe)Add 2-step fishing spot creation process so that min/max fish until respawn is needed can be defined.
// TODO: Figure out why fishingrod level up doesn't work.
// TODO: Fish abilities on consumtion, ignore full hunger, etc.

public class ProfessionMechanics implements Listener {
	static Logger log = Logger.getLogger("Minecraft");

	static ProfessionMechanics instance = null;

	public static boolean profession_buff = false;
	// Used for loot buffs.

	public static long profession_buff_timeout = 0L;
	// Used to determine experation of EXP buff.

	public static HashMap<ItemStack, Integer> item_exp = new HashMap<ItemStack, Integer>();
	// Local data for the EXP count on an item, stored temporarily, not necessarily in real time.

	public static HashMap<String, Integer> slow_mining = new HashMap<String, Integer>();
	// Player_NAME, Level of debuff

	public static ConcurrentHashMap<String, Long> last_swing = new ConcurrentHashMap<String, Long>();
	// Last time a player swung their pickaxe. Used for slowed gather rates.

	public static ConcurrentHashMap<Location, Material> ore_location = new ConcurrentHashMap<Location, Material>();
	// Location, Type of Ore to respawn.

	public static ConcurrentHashMap<Location, Long> ore_respawn = new ConcurrentHashMap<Location, Long>();
	// Location, Time until respawn.

	public static HashMap<String, String> ore_place = new HashMap<String, String>();
	// Ore placement map for placing new ore nodes.

	public static HashMap<String, Location> player_fishing_spot = new HashMap<String, Location>();
	// PLAYER_NAME, the last spot they cast their line into.

	public static ConcurrentHashMap<Location, Integer> fishing_location = new ConcurrentHashMap<Location, Integer>();
	// Location, Tier of fish at location

	public static ConcurrentHashMap<Location, Long> fishing_respawn = new ConcurrentHashMap<Location, Long>();
	// Location, Time until respawn.

	public static ConcurrentHashMap<Location, Integer> fish_count = new ConcurrentHashMap<Location, Integer>();
	// Location, Remaining fish catch attempts in the spot until it despawns.

	public static HashMap<String, String> fishing_place = new HashMap<String, String>();
	// Fishing spot placement map for placing new fishing spots

	public static HashMap<String, HashMap<Block, Furnace>> furnace_inventory = new HashMap<String, HashMap<Block, Furnace>>();
	// PLAYER_NAME, Map<Block, Inventory>

	public List<String> ignoreFurnaceOpenEvent = new ArrayList<String>();
	// Used to fix infinite loop in InventoryOpen() for instanced furnaces.

	public ConcurrentHashMap<Location, List<Location>> fishingParticles = new ConcurrentHashMap<Location, List<Location>>();
	// Epicenter location (placed fishing spawn), then a list of all the locations that could have water effects nearby.

	public static volatile ConcurrentHashMap<Location, Material> ores_to_spawn = new ConcurrentHashMap<Location, Material>();
	// Epicenter location (placed fishing spawn), then a list of all the locations that could have water effects nearby.

	public static HashMap<String, Integer> fish_caught_count = new HashMap<String, Integer>();
	// PLAYER_NAME, Fish caught (in session, for achievments)

	// Custom Fish Effects {
	public static HashMap<String, Integer> fish_health_regen = new HashMap<String, Integer>();
	// PLAYER_NAME, Amount to heal per tick, We'll use regen effect instead of timer.

	public static HashMap<String, Integer> fish_energy_regen = new HashMap<String, Integer>();
	// PLAYER_NAME, Extra % of Energy Per Tick, apply to Fatigue Mechanics.

	public static HashMap<String, Integer> fish_bonus_dmg = new HashMap<String, Integer>();
	// PLAYER_NAME, Extra DMG% per hit, use ItemMechanics(), apply to dmg calculation on attack

	public static HashMap<String, Integer> fish_bonus_armor = new HashMap<String, Integer>();
	// PLAYER_NAME, Extra ARMOR% per hit, use ItemMechanics(), apply to armor% calculation on hit

	public static HashMap<String, Integer> fish_bonus_block = new HashMap<String, Integer>();
	// PLAYER_NAME, Extra Block%, use ItemMechanics(), apply to block check.

	public static HashMap<String, Integer> fish_bonus_lifesteal = new HashMap<String, Integer>();
	// PLAYER_NAME, Extra Lifesteal %, use ItemMechanics, when we calculate lifesteal %

	public static HashMap<String, Integer> fish_bonus_critical_hit = new HashMap<String, Integer>();
	// PLAYER_NAME, Extra Critical Chance %, use ItemMechanics, when we calculate crit hit chance.
	// }



	public static ItemStack hoe_example = ItemMechanics.signCustomItem(Material.WOOD_HOE, (short)0, ChatColor.WHITE.toString() + "Novice Hoe", ChatColor.GRAY.toString() + "Level: " 
			+ ChatColor.WHITE.toString() + "1" + "," + ChatColor.GRAY + "0 / 176" 
			+ "," + ChatColor.GRAY.toString() + "EXP: " + ChatColor.RED.toString() + "||||||||||||||||||||||||||||||||||||||||||||||||||"
			+ "," + ChatColor.GRAY.toString() + ChatColor.ITALIC 
			+ "A jagged hoe made of wood."
			+ "," + ChatColor.RED.toString() + "UNAVAILABLE");

	public static ItemStack fishing_example = ItemMechanics.signCustomItem(Material.FISHING_ROD, (short)0, ChatColor.WHITE.toString() + "Basic Fishingrod", ChatColor.GRAY.toString() + "Level: " 
			+ ChatColor.WHITE.toString() + "1" + "," + ChatColor.GRAY + "0 / 176" 
			+ "," + ChatColor.GRAY.toString() + "EXP: " + ChatColor.RED.toString() + "||||||||||||||||||||||||||||||||||||||||||||||||||" 
			+ "," + ChatColor.GRAY.toString() + ChatColor.ITALIC 
			+ "A fishing rod made of wood and thread."
			+ "," + ChatColor.RED.toString() + "UNAVAILABLE");

	public static ItemStack t1_fishing = ItemMechanics.signCustomItem(Material.FISHING_ROD, (short)0, ChatColor.WHITE.toString() + "Basic Fishingrod", ChatColor.GRAY.toString() + "Level: " 
			+ ChatColor.WHITE.toString() + "1" + "," + ChatColor.GRAY + "0 / " + getEXPNeeded(1, "fishing")  
			+ "," + ChatColor.GRAY.toString() + "EXP: " + ChatColor.RED.toString() + "||||||||||||||||||||||||||||||||||||||||||||||||||" 
			+ "," + ChatColor.GRAY.toString() + ChatColor.ITALIC 
			+ "A fishing rod made of wood and thread.");

	public static ItemStack t2_fishing = ItemMechanics.signCustomItem(Material.FISHING_ROD, (short)0, ChatColor.GREEN.toString() + "Advanced Fishingrod", ChatColor.GRAY.toString() + "Level: " 
			+ ChatColor.GREEN.toString() + "20" + "," + ChatColor.GRAY + "0 / " + getEXPNeeded(20, "fishing") 
			+ "," + ChatColor.GRAY.toString() + "EXP: " + ChatColor.RED.toString() + "||||||||||||||||||||||||||||||||||||||||||||||||||" 
			+ "," + ChatColor.GRAY.toString() + ChatColor.ITALIC 
			+ "A fishing rod made of oak wood and thread.");

	public static ItemStack t3_fishing = ItemMechanics.signCustomItem(Material.FISHING_ROD, (short)0, ChatColor.AQUA.toString() + "Expert Fishingrod", ChatColor.GRAY.toString() + "Level: " 
			+ ChatColor.AQUA.toString() + "40" + "," + ChatColor.GRAY + "0 / " + getEXPNeeded(40, "fishing")  
			+ "," + ChatColor.GRAY.toString() + "EXP: " + ChatColor.RED.toString() + "||||||||||||||||||||||||||||||||||||||||||||||||||" 
			+ "," + ChatColor.GRAY.toString() + ChatColor.ITALIC 
			+ "A fishing rod made of ancient oak wood and spider silk.");

	public static ItemStack t4_fishing = ItemMechanics.signCustomItem(Material.FISHING_ROD, (short)0, ChatColor.LIGHT_PURPLE.toString() + "Supreme Fishingrod", ChatColor.GRAY.toString() + "Level: " 
			+ ChatColor.LIGHT_PURPLE.toString() + "60" + "," + ChatColor.GRAY + "0 / " + getEXPNeeded(60, "fishing") 
			+ "," + ChatColor.GRAY.toString() + "EXP: " + ChatColor.RED.toString() + "||||||||||||||||||||||||||||||||||||||||||||||||||" 
			+ "," + ChatColor.GRAY.toString() + ChatColor.ITALIC 
			+ "A fishing rod made of jungle bamboo and spider silk.");

	public static ItemStack t5_fishing = ItemMechanics.signCustomItem(Material.FISHING_ROD, (short)0, ChatColor.YELLOW.toString() + "Master Fishingrod", ChatColor.GRAY.toString() + "Level: " 
			+ ChatColor.YELLOW.toString() + "80" + "," + ChatColor.GRAY + "0 / " + getEXPNeeded(80, "fishing") 
			+ "," + ChatColor.GRAY.toString() + "EXP: " + ChatColor.RED.toString() + "||||||||||||||||||||||||||||||||||||||||||||||||||" 
			+ "," + ChatColor.GRAY.toString() + ChatColor.ITALIC 
			+ "A fishing rod made of rich mahogany and enchanted silk.");

	public static ItemStack t1_pickaxe = ItemMechanics.signCustomItem(Material.WOOD_PICKAXE, (short)0, ChatColor.WHITE.toString() + "Novice Pickaxe", ChatColor.GRAY.toString() + "Level: " 
			+ ChatColor.WHITE.toString() + "1" + "," + ChatColor.GRAY + "0 / 176" 
			+ "," + ChatColor.GRAY.toString() + "EXP: " + ChatColor.RED.toString() + "||||||||||||||||||||||||||||||||||||||||||||||||||" 
			+ "," + ChatColor.GRAY.toString() + ChatColor.ITALIC 
			+ "A pickaxe made out of sturdy wood.");

	public static ItemStack t2_pickaxe = ItemMechanics.signCustomItem(Material.STONE_PICKAXE, (short)0, ChatColor.GREEN.toString() + "Apprentice Pickaxe", ChatColor.GRAY.toString() + "Level: " 
			+ ChatColor.GREEN.toString() + "20" + "," + ChatColor.GRAY + "0 / " + getEXPNeeded(20, "mining") 
			+ "," + ChatColor.GRAY.toString() + "EXP: " + ChatColor.RED.toString() + "||||||||||||||||||||||||||||||||||||||||||||||||||" 
			+ "," + ChatColor.GRAY.toString() + ChatColor.ITALIC 
			+ "A pickaxe made out of cave stone.");

	public static ItemStack t3_pickaxe = ItemMechanics.signCustomItem(Material.IRON_PICKAXE, (short)0, ChatColor.AQUA.toString() + "Expert Pickaxe", ChatColor.GRAY.toString() + "Level: " 
			+ ChatColor.AQUA.toString() + "40" + "," + ChatColor.GRAY + "0 / " + getEXPNeeded(40, "mining")  
			+ "," + ChatColor.GRAY.toString() + "EXP: " + ChatColor.RED.toString() + "||||||||||||||||||||||||||||||||||||||||||||||||||" 
			+ "," + ChatColor.GRAY.toString() + ChatColor.ITALIC 
			+ "A pickaxe made out of forged iron.");

	public static ItemStack t4_pickaxe = ItemMechanics.signCustomItem(Material.DIAMOND_PICKAXE, (short)0, ChatColor.LIGHT_PURPLE.toString() + "Supreme Pickaxe", ChatColor.GRAY.toString() + "Level: " 
			+ ChatColor.LIGHT_PURPLE.toString() + "60" + "," + ChatColor.GRAY + "0 / " + getEXPNeeded(60, "mining")  
			+ "," + ChatColor.GRAY.toString() + "EXP: " + ChatColor.RED.toString() + "||||||||||||||||||||||||||||||||||||||||||||||||||" 
			+ "," + ChatColor.GRAY.toString() + ChatColor.ITALIC 
			+ "A pickaxe made out of hardened diamond.");

	public static ItemStack t5_pickaxe = ItemMechanics.signCustomItem(Material.GOLD_PICKAXE, (short)0, ChatColor.YELLOW.toString() + "Master Pickaxe", ChatColor.GRAY.toString() + "Level: " 
			+ ChatColor.YELLOW.toString() + "80" + "," + ChatColor.GRAY + "0 / " + getEXPNeeded(80, "mining")  
			+ "," + ChatColor.GRAY.toString() + "EXP: " + ChatColor.RED.toString() + "||||||||||||||||||||||||||||||||||||||||||||||||||" 
			+ "," + ChatColor.GRAY.toString() + ChatColor.ITALIC 
			+ "A pickaxe made out of reinforced gold.");

	public static ItemStack coal_ore = ItemMechanics.signCustomItem(Material.COAL_ORE, (short)0, ChatColor.WHITE.toString() + "Coal Ore", ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() 
			+ "A chunk of coal ore.");
	public static ItemStack emerald_ore = ItemMechanics.signCustomItem(Material.EMERALD_ORE, (short)0, ChatColor.GREEN.toString() + "Emerald Ore", ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() 
			+ "An unrefined piece of emerald ore.");
	public static ItemStack iron_ore = ItemMechanics.signCustomItem(Material.IRON_ORE, (short)0, ChatColor.AQUA.toString() + "Iron Ore", ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() 
			+ "A piece of raw iron.");
	public static ItemStack diamond_ore = ItemMechanics.signCustomItem(Material.DIAMOND_ORE, (short)0, ChatColor.LIGHT_PURPLE.toString() + "Diamond Ore", ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() 
			+ "A sharp chunk of diamond ore.");
	public static ItemStack gold_ore = ItemMechanics.signCustomItem(Material.GOLD_ORE, (short)0, ChatColor.YELLOW.toString() + "Gold Ore", ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() 
			+ "A sparkling piece of gold ore.");

	public static HashSet<Byte> transparent = new HashSet<Byte>();

	public static InventoryHolder spoof_furnace = null;

	public static int splashCounter = 10;
	
	@SuppressWarnings("deprecation")
	public void onEnable(){
		instance = this;

		Main.plugin.getCommand("hidefish").setExecutor(new CommandHideFish());
		Main.plugin.getCommand("prof").setExecutor(new CommandProf());
		Main.plugin.getCommand("setfish").setExecutor(new CommandSetFish());
		Main.plugin.getCommand("setore").setExecutor(new CommandSetOre());
		Main.plugin.getCommand("showfish").setExecutor(new CommandShowFish());
		
		loadSkillLocationData();
		Main.plugin.getServer().getPluginManager().registerEvents(this, Main.plugin);

		//spoof_furnace = ((Furnace)(Bukkit.getWorlds().get(0).getBlockAt(-336, 84, 415).getState())).getInventory().getHolder();

		transparent.add((byte)0);
		transparent.add((byte)31);
		transparent.add((byte)8);
		transparent.add((byte)9);
		transparent.add((byte)40);
		transparent.add((byte)37);
		transparent.add((byte)38);
		transparent.add((byte)39);
		transparent.add((byte)90);

		transparent.add((byte)53);
		transparent.add((byte)67);
		transparent.add((byte)108);
		transparent.add((byte)109);
		transparent.add((byte)114);
		transparent.add((byte)128);
		transparent.add((byte)134);
		transparent.add((byte)135);
		transparent.add((byte)136);
		transparent.add((byte)156);

		transparent.add((byte)106);
		transparent.add((byte)104);
		transparent.add((byte)105);
		transparent.add((byte)85);
		transparent.add((byte)113);
		transparent.add((byte)107);

		transparent.add((byte)141);
		transparent.add((byte)142);
		transparent.add((byte)132);
		transparent.add((byte)127);
		transparent.add((byte)140);
		transparent.add((byte)101);
		transparent.add((byte)102);

		// Builds the index of fishingParticles to be referenced later to spawn particle effects.
		Main.plugin.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
			public void run() {
				generateFishingParticleBlockList();
				populateFishingSpots();
			}
		}, 14 * 20L);

		Main.plugin.getServer().getScheduler().scheduleSyncRepeatingTask(Main.plugin, new Runnable() {
			public void run() {
				for(Entry<Location, Material> data : ores_to_spawn.entrySet()){
					Location loc = data.getKey();
					Material m = data.getValue();

					loc.getBlock().setType(m);
					ores_to_spawn.remove(loc);
				}
			}
		}, 9 * 20L, 5 * 20L);

		// This task prevents any abuse that would allow a player to mine a rock faster than they should be able to.
		/*this.getServer().getScheduler().scheduleAsyncRepeatingTask(this, new Runnable() {
			public void run() {
				for(Entry<String, Long> data : last_swing.entrySet()){
					String p_name = data.getKey();

					if(getServer().getPlayer(p_name) != null){
						Player pl = getServer().getPlayer(p_name);
						Block b = pl.getTargetBlock(transparent, 8);
						ItemStack is = pl.getItemInHand();

						if(b.getType() == Material.STONE){
							sendPotionPacket(pl, new PotionEffect(PotionEffectType.SLOW_DIGGING, Integer.MAX_VALUE, 4));
							last_swing.put(pl.getName(), System.currentTimeMillis());
							continue;
						}

						if(b.getType() == Material.COAL_ORE || b.getType() == Material.EMERALD_ORE || b.getType() == Material.IRON_ORE || b.getType() == Material.DIAMOND_ORE || b.getType() == Material.GOLD_ORE){
							if((isSkillItem(is)) && (getSkillType(is).equalsIgnoreCase("mining"))){	
								int pickaxe_tier = getItemTier(is);
								int ore_tier = getOreTier(b.getType());

								if(pickaxe_tier < ore_tier){
									// Cannot mine. Too low level of pickaxe. 
									sendPotionPacket(pl, new PotionEffect(PotionEffectType.SLOW_DIGGING, Integer.MAX_VALUE, 4));
									//slow_mining.put(pl.getName(), true);
									last_swing.put(pl.getName(), System.currentTimeMillis());
									continue;
								}

								if(slow_mining.containsKey(pl.getName())){
									int current_debuff = slow_mining.get(pl.getName());
									int expected_debuff = getDebuffLevel(pickaxe_tier, ore_tier);
									if(current_debuff == expected_debuff){
										continue; // Do nothing, it's handled.
									}
								}
								removeMobEffect(pl, PotionEffectType.SLOW_DIGGING.getId());

								int diff = pickaxe_tier - ore_tier;

								if(diff >= 3){
									sendPotionPacket(pl, new PotionEffect(PotionEffectType.FAST_DIGGING, Integer.MAX_VALUE, 0));
									removeMobEffect(pl, PotionEffectType.SLOW_DIGGING.getId());
									pl.removePotionEffect(PotionEffectType.SLOW_DIGGING);
									slow_mining.put(pl.getName(), 0);
									last_swing.put(pl.getName(), System.currentTimeMillis());
								}

								if(pickaxe_tier == 4 || pickaxe_tier == 5){
									if(pickaxe_tier == 4){
										if(diff == 0){ // Diamond ore.
											sendPotionPacket(pl, new PotionEffect(PotionEffectType.SLOW_DIGGING, Integer.MAX_VALUE, 3));
											slow_mining.put(pl.getName(), 3);
										}
										if(diff == 1){ // Iron ore.
											sendPotionPacket(pl, new PotionEffect(PotionEffectType.SLOW_DIGGING, Integer.MAX_VALUE, 3));
											slow_mining.put(pl.getName(), 3);
										}
									}
									if(pickaxe_tier == 5){
										if(diff == 0){ // Gold ore.
											sendPotionPacket(pl, new PotionEffect(PotionEffectType.SLOW_DIGGING, Integer.MAX_VALUE, 3));
											slow_mining.put(pl.getName(), 3);
										}
										if(diff == 1){ // Diamond ore.
											sendPotionPacket(pl, new PotionEffect(PotionEffectType.SLOW_DIGGING, Integer.MAX_VALUE, 1));
											slow_mining.put(pl.getName(), 1);
										}
									}
								}
								else{
									if(diff == 0){
										// Lvl 2 debuff
										sendPotionPacket(pl, new PotionEffect(PotionEffectType.SLOW_DIGGING, Integer.MAX_VALUE, 2));
										slow_mining.put(pl.getName(), 2);
									}
									if(diff == 1){
										// Lvl 1 debuff
										sendPotionPacket(pl, new PotionEffect(PotionEffectType.SLOW_DIGGING, Integer.MAX_VALUE, 1));
										slow_mining.put(pl.getName(), 1);
									}
									if(diff == 2){
										// Lvl 0 debuff
										sendPotionPacket(pl, new PotionEffect(PotionEffectType.SLOW_DIGGING, Integer.MAX_VALUE, 0));
										slow_mining.put(pl.getName(), 0);
									}
								}

								if(diff > 2){
									last_swing.remove(pl.getName());
									slow_mining.remove(pl.getName());

									removeMobEffect(pl, PotionEffectType.SLOW_DIGGING.getId());
									pl.removePotionEffect(PotionEffectType.SLOW_DIGGING);
									removeMobEffect(pl, PotionEffectType.FAST_DIGGING.getId());
									pl.removePotionEffect(PotionEffectType.FAST_DIGGING);
								}

								// It's an ore, we may be slowing them down.
								last_swing.put(pl.getName(), System.currentTimeMillis());
							}
						}
					}
				}
			}
		}, 20 * 20L, 1L);*/


		Main.plugin.getServer().getScheduler().scheduleAsyncRepeatingTask(Main.plugin, new Runnable() {
			public void run() {
				for(Entry<Location, Long> data : ore_respawn.entrySet()){
					Location loc = data.getKey();
					long break_time = data.getValue();
					if(!(ore_location.containsKey(loc))){
						log.info("[ProfessionMechanics] Corrupt ore spawn location at " + loc.toString());
						ore_respawn.remove(loc);
						continue;
					}
					Material m = ore_location.get(loc);

					long respawn_delay = 60;

					if(m == Material.COAL_ORE){
						respawn_delay = 2 * 60;
					}
					if(m == Material.EMERALD_ORE){
						respawn_delay = 5 * 60;
					}
					if(m == Material.IRON_ORE){
						respawn_delay = 10 * 60;
					}
					if(m == Material.DIAMOND_ORE){
						respawn_delay = 20 * 60;
					}
					if(m == Material.GOLD_ORE){
						respawn_delay = 40 * 60;
					}

					if((System.currentTimeMillis() - break_time) >= (respawn_delay * 1000)){
						// It's time to respawn the ore!
						ores_to_spawn.put(loc, m);
						ore_respawn.remove(loc);
					}

				}
			}
		}, 5 * 20L, 2 * 20L);

		Main.plugin.getServer().getScheduler().scheduleAsyncRepeatingTask(Main.plugin, new Runnable() {
			public void run() {
				for(Entry<String, Long> data : last_swing.entrySet()){
					String p_name = data.getKey();
					/*if(!(slow_mining.containsKey(p_name))){
						continue;
					}*/

					long time = data.getValue();

					if((System.currentTimeMillis() - time) >= (1000)){
						// It's been 1 seconds since last animation.
						last_swing.remove(p_name);
						slow_mining.remove(p_name);

						if(Main.plugin.getServer().getPlayer(p_name) != null){
							Player p = Main.plugin.getServer().getPlayer(p_name);
							//removeMobEffect(p, PotionEffectType.SLOW_DIGGING.getId());
							p.removePotionEffect(PotionEffectType.SLOW_DIGGING);
							//removeMobEffect(p, PotionEffectType.FAST_DIGGING.getId());
							p.removePotionEffect(PotionEffectType.FAST_DIGGING);
						}
						// Remove them from slow mining, remove effect.
					}
				}
			}
		}, 10 * 20L, 5L);

		Main.plugin.getServer().getScheduler().scheduleAsyncRepeatingTask(Main.plugin, new Runnable() {
			public void run() {
				int chance = splashCounter * splashCounter;
				if(splashCounter == 1) splashCounter = 21;
				splashCounter--;
				Random r = new Random();
				
				if(fishingParticles.size() <= 0){
					return; // Do nothing.
				}

				try{
					for(Entry<Location, List<Location>> data : fishingParticles.entrySet()){
						Location epicenter = data.getKey();
						int tier = fishing_location.get(epicenter);
						if((System.currentTimeMillis() - fishing_respawn.get(epicenter)) <= (getFishingSpotRespawnTime(tier) * 1000)){
							continue; // Not time to respawn fish yet.
						}
						try {
							ParticleEffect.sendToLocation(ParticleEffect.SPLASH, epicenter, r.nextFloat(), r.nextFloat(), r.nextFloat(), 0.4F, 20);
						} catch (Exception e1) {e1.printStackTrace();}
						//epicenter.getWorld().spawnParticle(epicenter, Particle.SPLASH, 0.4F, 20);
						
						for(Location loc : data.getValue()){
							if(r.nextInt(chance) == 1){
								try {
									ParticleEffect.sendToLocation(ParticleEffect.SPLASH, loc, r.nextFloat(), r.nextFloat(), r.nextFloat(), 0.4F, 20);
								} catch (Exception e1) {e1.printStackTrace();}
								//loc.getWorld().spawnParticle(loc, Particle.SPLASH, 0.4F, 20);
							}
						}
					}
				} catch(ConcurrentModificationException cme){
					return;
				}
			}
		}, 10 * 20L, 10L);

		Main.plugin.getServer().getScheduler().scheduleSyncRepeatingTask(Main.plugin, new Runnable() {
			public void run() {
				if(profession_buff == true){
					if((System.currentTimeMillis() - profession_buff_timeout) > 0){
						// Time to stop the fun.
						profession_buff = false;
						Bukkit.broadcastMessage(ChatColor.GOLD + "" + ChatColor.BOLD + ">> " + ChatColor.GOLD + "The " + ChatColor.UNDERLINE + "+20% Global Profession Rates" + ChatColor.GOLD + " has expired.");
					}
				}
			}
		}, 15 * 20L, 1 * 20L);


		/*this.getServer().getScheduler().scheduleAsyncRepeatingTask(this, new Runnable() {
			public void run() {
				for(Player pl : getServer().getOnlinePlayers()){
					if(pl.getOpenInventory().getTopInventory().getName().equalsIgnoreCase("container.furnace")){
						ContainerBlock cb = (ContainerBlock)pl.getOpenInventory().getTopInventory().getHolder();
						Furnace f = (Furnace)cb;
						f.setCookTime((short)1);
					}
				}
			}
		}, 10 * 20L, 5L);*/

		log.info("[ProfessionMechanics] has been ENABLED.");
	}

	public void onDisable() {
		saveSkillLocationData();
		log.info("[ProfessionMechanics] has been disabled.");
	}

	public int getDebuffLevel(int pickaxe_tier, int ore_tier){
		int diff = pickaxe_tier - ore_tier;

		if(diff >= 3){
			return 0;
		}

		if(pickaxe_tier == 4 || pickaxe_tier == 5){
			if(pickaxe_tier == 4){
				if(diff == 0){ // Diamond ore.
					return 3;
				}
				if(diff == 1){ // Iron ore.
					return 3;
				}
			}
			if(pickaxe_tier == 5){
				if(diff == 0){ // Gold ore.
					return 3;
				}
				if(diff == 1){ // Diamond ore.
					return 1;
				}
			}
		}
		else{
			if(diff == 0){
				// Lvl 2 debuff
				return 2;
			}
			if(diff == 1){
				// Lvl 1 debuff
				return 1;
			}
			if(diff == 2){
				// Lvl 0 debuff
				return 0;
			}
		}

		return -1;
	}

	public int getFishingSpotRespawnTime(int spot_tier){
		if(spot_tier == 1){
			return 360;
		}
		if(spot_tier == 2){
			return 360;
		}
		if(spot_tier == 3){
			return 360;
		}
		if(spot_tier == 4){
			return 600;
		}
		if(spot_tier == 5){
			return 600;
		}

		return 60; // Unknown.
	}

	public void populateFishingSpots(){
		for(Entry<Location, Integer> data : fishing_location.entrySet()){
			int tier = data.getValue();
			Location fish_loc = data.getKey();

			fish_count.put(fish_loc, getRandomFishCount(tier));
		}
	}

	public void generateFishingParticleBlockList(){
		int count = 0;

		for(Entry<Location, Integer> data : fishing_location.entrySet()){
			Location epicenter = data.getKey();
			List<Location> lfishingParticles = new ArrayList<Location>();

			int radius = 10;

			Location location = epicenter;
			for (int x = -(radius); x <= radius; x++)
			{
				for (int y = -(radius); y <= radius; y++)
				{
					for (int z = -(radius); z <= radius; z++)
					{
						Location loc = location.getBlock().getRelative(x, y, z).getLocation();
						if(loc.getBlock().getType() == Material.WATER || loc.getBlock().getType() == Material.STATIONARY_WATER){
							if(loc.add(0, 1, 0).getBlock().getType() == Material.AIR){
								if(!(lfishingParticles.contains(loc))){
									lfishingParticles.add(loc);
									count++;
								}
							}
						}
					}
				}
			}

			fishingParticles.put(epicenter, lfishingParticles);
		}

		log.info("[ProfessionMechanics] Loaded a total of " + count + " possible FISHING PARTICLE locations.");
	}

	public int getRandomFishCount(int spot_tier){
		// TODO: Set proper, balanced values.
		if(spot_tier == 1){
			return (new Random().nextInt(100) + 100);
		}
		if(spot_tier == 2){
			return (new Random().nextInt(90) + 90);
		}
		if(spot_tier == 3){
			return (new Random().nextInt(75) + 75);
		}
		if(spot_tier == 4){
			return (new Random().nextInt(50) + 50);
		}
		if(spot_tier == 5){
			return (new Random().nextInt(30) + 30);
		}

		return 1;
	}

	public static void saveSkillLocationData(){
		String all_dat = "";
		int count = 0;

		for (Entry<Location, Material> entry : ore_location.entrySet()) {
			Location loc = entry.getKey();
			Block b = loc.getBlock();
			Material m = entry.getValue();

			all_dat += loc.getBlockX() + "," + b.getLocation().getBlockY() + "," + loc.getBlockZ() + "=" + m.name() + "\r\n";
			count++;
		}

		if(all_dat.length() > 1){
			try {
				DataOutputStream dos = new DataOutputStream(new FileOutputStream("plugins/ProfessionMechanics/ore_spawns.dat", false));
				dos.writeBytes(all_dat + "\n");
				dos.close();
			} catch (IOException e) {}
		}

		log.info("[ProfessionMechanics] " + count + " ORE SPAWN locations have been SAVED.");

		all_dat = "";
		count = 0;

		for (Entry<Location, Integer> entry : fishing_location.entrySet()) {
			Location loc = entry.getKey();
			Block b = loc.getBlock();
			Integer tier = entry.getValue();

			all_dat += loc.getBlockX() + "," + b.getLocation().getBlockY() + "," + loc.getBlockZ() + "=" + tier + "\r\n";
			count++;
		}

		if(all_dat.length() > 1){
			try {
				DataOutputStream dos = new DataOutputStream(new FileOutputStream("plugins/ProfessionMechanics/fishing_spawns.dat", false));
				dos.writeBytes(all_dat + "\n");
				dos.close();
			} catch (IOException e) {}
		}

		log.info("[ProfessionMechanics] " + count + " FISHING SPOT locations have been SAVED.");
	}

	public void loadSkillLocationData(){
		int count = 0;
		try
		{
			File file = new File("plugins/ProfessionMechanics/ore_spawns.dat");
			if(!(file.exists())){
				file.createNewFile();
			}
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line = "";
			while((line = reader.readLine()) != null){
				if(line.contains("=")){
					try{
						String[] cords = line.split("=")[0].split(",");
						Location loc = new Location(Bukkit.getWorlds().get(0), Double.parseDouble(cords[0]), Double.parseDouble(cords[1]), Double.parseDouble(cords[2]));

						String material_data = line.split("=")[1];
						Material m = Material.getMaterial(material_data);

						ore_location.put(loc, m);
						ore_respawn.put(loc, 0L); // Instant respawn.

						count++;
					} catch(NullPointerException npe){
						npe.printStackTrace();
						continue;
					}
				}
			}
			reader.close();
			log.info("[ProfessionMechanics] " + count + " ORE SPAWN locations have been LOADED.");

			count = 0;

			file = new File("plugins/ProfessionMechanics/fishing_spawns.dat");
			if(!(file.exists())){
				file.createNewFile();
			}
			reader = new BufferedReader(new FileReader(file));
			line = "";
			while((line = reader.readLine()) != null){
				if(line.contains("=")){
					String[] cords = line.split("=")[0].split(",");
					Location loc = new Location(Bukkit.getWorlds().get(0), Double.parseDouble(cords[0]), Double.parseDouble(cords[1]), Double.parseDouble(cords[2]));

					int tier = Integer.parseInt(line.split("=")[1]);

					fishing_location.put(loc, tier);
					fishing_respawn.put(loc, 0L); // Instant respawn.

					count++;
				}
			}
			reader.close();
			log.info("[ProfessionMechanics] " + count + " FISHING SPOT locations have been LOADED.");
		}
		catch (IOException ioe){ioe.printStackTrace();}
	}

	// Used in repair mechanics.
	public static ItemStack resetToNoviceSkillItem(ItemStack max_item){
		// Transform the pick back to T1.
		ItemStack new_item = null;
		String skill = getSkillType(max_item);
		String item_name = "";
		if(skill.equalsIgnoreCase("mining")){
			item_name = "Pickaxe";
		}
		if(skill.equalsIgnoreCase("fishing")){
			item_name = "Fishingrod";
		}

		if(skill.equalsIgnoreCase("mining")){
			new_item = new ItemStack(Material.WOOD_PICKAXE, 1);
		}
		if(skill.equalsIgnoreCase("fishing")){
			new_item = new ItemStack(Material.FISHING_ROD, 1);
		}

		List<String> new_lore = new ArrayList<String>();
		for(String s : max_item.getItemMeta().getLore()){
			s = s.replaceAll(ChatColor.YELLOW.toString(), ChatColor.WHITE.toString());
			if(s.contains("Level:")){
				s = ChatColor.GRAY + "Level: " + ChatColor.WHITE.toString() + "1";
			}
			if(s.contains("EXP:")){
				s = ChatColor.GRAY.toString() + "EXP: " + ChatColor.RED.toString() + "||||||||||||||||||||||||||||||||||||||||||||||||||";
			}

			/*if(skill.equalsIgnoreCase("mining")){
				s = ChatColor.GRAY.toString() + ChatColor.ITALIC + "A pickaxe made out of wood.";
			}
			if(skill.equalsIgnoreCase("fishing")){
				s = ChatColor.GRAY.toString() + ChatColor.ITALIC + "A fishing rod made of wood and thread.";
			}*/

			new_lore.add(s);
		}

		ItemMeta im = max_item.getItemMeta();
		im.setLore(new_lore);
		im.setDisplayName(ChatColor.WHITE.toString() + "Novice " + item_name);
		if(new_item != null){
			new_item.setItemMeta(im);
			return new_item;
		}
		return max_item;
	}

	public static int getItemTier(ItemStack is){
		Material m = is.getType();
		if(m == Material.FISHING_ROD){
			return ItemMechanics.getItemTier(is);
		}
		if(m == Material.WOOD_PICKAXE){
			return 1;
		}
		if(m == Material.STONE_PICKAXE){
			return 2;
		}
		if(m == Material.IRON_PICKAXE){
			return 3;
		}
		if(m == Material.DIAMOND_PICKAXE){
			return 4;
		}
		if(m == Material.GOLD_PICKAXE){
			return 5;
		}
		return 1;
	}

	public static int getOreTier(Material m){
		if(m == Material.COAL_ORE){
			return 1;
		}
		if(m == Material.EMERALD_ORE){
			return 2;
		}
		if(m == Material.IRON_ORE){
			return 3;
		}
		if(m == Material.DIAMOND_ORE){
			return 4;
		}
		if(m == Material.GOLD_ORE){
			return 5;
		}
		return 1;
	}

	public static String getItemDescription(ItemStack is){
		Material m = is.getType();
		if(m == Material.WOOD_PICKAXE){
			return "A pickaxe made out of wood.";
		}
		if(m == Material.STONE_PICKAXE){
			return "A pickaxe made out of stone.";
		}
		if(m == Material.IRON_PICKAXE){
			return "A pickaxe made out of iron.";
		}
		if(m == Material.DIAMOND_PICKAXE){
			return "A pickaxe made out of diamond.";
		}
		if(m == Material.GOLD_PICKAXE){
			return "A pickaxe made out of gold.";
		}
		if(m == Material.FISHING_ROD){
			int tier = ItemMechanics.getItemTier(is);
			if(tier == 1){
				return "A fishing rod made of wood and thread.";
			}
			if(tier == 2){
				return "A fishing rod made of oak wood and thread.";
			}
			if(tier == 3){
				return "A fishing rod made of ancient oak wood and spider silk.";
			}
			if(tier == 4){
				return "A fishing rod made of jungle bamboo and spider silk.";
			}
			if(tier == 5){
				return "A fishing rod made of rich mahogany and enchanted silk..";
			}
			return "A fishingrod.";
		}

		return "An unknown item.";
	}


	public static boolean isSkillItem(ItemStack is){
		if(is.getType() == Material.WOOD_PICKAXE || is.getType() == Material.STONE_PICKAXE || is.getType() == Material.IRON_PICKAXE || is.getType() == Material.DIAMOND_PICKAXE || is.getType() == Material.GOLD_PICKAXE){
			if(is.hasItemMeta() && is.getItemMeta().hasDisplayName()){
				return true;
			}
		}
		if(is.getType() == Material.FISHING_ROD){
			if(is.hasItemMeta() && is.getItemMeta().hasDisplayName()){
				return true;
			}
		}
		return false;
	}

	public static int getItemLevel(ItemStack is){
		if(!(is.hasItemMeta())){
			return 0;
		}
		if(!(is.getItemMeta().hasLore())){
			return 0;
		}

		for(String s : is.getItemMeta().getLore()){
			if(s.contains("Level:")){
				s = ChatColor.stripColor(s);
				int level = Integer.parseInt(s.substring(s.indexOf(" ") + 1, s.length()));
				return level;
			}
		}

		return -1; // Nothing was found!
	}

	public static int getEXPNeeded(int level, String stat){
		if(stat.equalsIgnoreCase("mining")){
			if(level == 1){
				return 176; // formula doens't work on level 1.
			}
			if(level == 100){
				return 0; // green bar
			}
			int previous_level = level - 1;
			return (int)(Math.pow((previous_level), 2) + ((previous_level) * 20) + 150 + ((previous_level) * 4) + getEXPNeeded((previous_level), stat));
		}
		if(stat.equalsIgnoreCase("fishing")){
			if(level == 1){
				return 176; // formula doens't work on level 1.
			}
			if(level == 100){
				return 0; // green bar
			}
			int previous_level = level - 1;
			return (int)(Math.pow((previous_level), 2) + ((previous_level) * 20) + 150 + ((previous_level) * 4) + getEXPNeeded((previous_level), stat));
		}
		return 0;
	}

	public static ChatColor getTierColor(int tier){
		if(tier == 1){
			return ChatColor.WHITE;
		}
		if(tier == 2){
			return ChatColor.GREEN;
		}
		if(tier == 3){
			return ChatColor.AQUA;
		}
		if(tier == 4){
			return ChatColor.LIGHT_PURPLE;
		}
		if(tier == 5){
			return ChatColor.YELLOW;
		}
		return ChatColor.WHITE;
	}

	public static int getItemEXP(ItemStack is){
		if(!(is.hasItemMeta())){
			return 0;
		}
		if(!(is.getItemMeta().hasLore())){
			return 0;
		}

		if(item_exp.containsKey(is)){
			return item_exp.get(is);
		}

		// We need to generate the EXP based off the bar.
		for(String s : is.getItemMeta().getLore()){
			if(s.contains("/")){
				String exp_string = s.substring(0, s.indexOf(" "));
				double current_exp = Integer.parseInt(ChatColor.stripColor(exp_string));

				item_exp.put(is, (int)current_exp);
				return (int)current_exp;
			}
		}

		return 0; // Could not find bar or something.

	}

	public int getTreasureFindChance(ItemStack is){
		int chance = 0;

		if(!(isSkillItem(is))){
			return chance;
		}

		for(String s : is.getItemMeta().getLore()){
			if(s.contains("TREASURE FIND")){
				chance = Integer.parseInt(s.substring(s.lastIndexOf(" ") + 1, s.lastIndexOf("%")));
				return chance;
			}
		}

		return chance;
	}

	public int getJunkFindChance(ItemStack is){
		int chance = 0;

		if(!(isSkillItem(is))){
			return chance;
		}

		for(String s : is.getItemMeta().getLore()){
			if(s.contains("JUNK FIND")){
				chance = Integer.parseInt(s.substring(s.lastIndexOf(" ") + 1, s.lastIndexOf("%")));
				return chance;
			}
		}

		return chance;
	}

	public int getDoubleDropChance(ItemStack is){
		int chance = 0;

		if(!(isSkillItem(is))){
			return chance;
		}

		for(String s : is.getItemMeta().getLore()){
			if(s.contains("DOUBLE")){
				chance = Integer.parseInt(s.substring(s.lastIndexOf(" ") + 1, s.lastIndexOf("%")));
				return chance;
			}
		}

		return chance;
	}

	public int getTripleDropChance(ItemStack is){
		int chance = 0;

		if(!(isSkillItem(is))){
			return chance;
		}

		for(String s : is.getItemMeta().getLore()){
			if(s.contains("TRIPLE")){
				chance = Integer.parseInt(s.substring(s.lastIndexOf(" ") + 1, s.lastIndexOf("%")));
				return chance;
			}
		}

		return chance;
	}

	public int getGemFindChance(ItemStack is){
		int chance = 0;

		if(!(isSkillItem(is)) || !(getSkillType(is).equalsIgnoreCase("mining"))){
			return chance;
		}

		for(String s : is.getItemMeta().getLore()){
			if(s.contains("GEM FIND")){
				chance = Integer.parseInt(s.substring(s.lastIndexOf(" ") + 1, s.lastIndexOf("%")));
				return chance;
			}
		}

		return chance;
	}

	public int getSuccessChance(ItemStack is){
		int chance = 0;

		if(!(isSkillItem(is))){
			return chance;
		}

		for(String s : is.getItemMeta().getLore()){
			if(s.contains("SUCCESS")){
				chance = Integer.parseInt(s.substring(s.lastIndexOf("+") + 1, s.lastIndexOf("%")));
				return chance;
			}
		}

		return chance;
	}

	public int getDurabilityBuff(ItemStack is){
		int buff = 0;

		if(!(isSkillItem(is))){
			return buff;
		}

		for(String s : is.getItemMeta().getLore()){
			if(s.contains("DURABILITY")){
				buff = Integer.parseInt(s.substring(s.lastIndexOf("+") + 1, s.lastIndexOf("%")));
				return buff;
			}
		}

		return buff;
	}

	public static String getRandomStatBuff(int cur_tier, String skill){

		if(skill.equalsIgnoreCase("fishing")){
			int buff_type = new Random().nextInt(6);
			/*
			 * 0 = Double Fish
			 * 1 = Chance for Treasure
			 * 2 = Chance of success increase
			 * 3 = Triple Fish
			 * 4 = Durability Increase
			 * */

			if(cur_tier == 2){
				if(buff_type == 0 || buff_type == 1){
					int buff_percent = new Random().nextInt(5) + 1; 
					return ChatColor.RED.toString() + "DOUBLE FISH: " + buff_percent + "%";
				}
				if(buff_type == 2){
					int buff_percent = new Random().nextInt(2) + 1; 
					return ChatColor.RED.toString() + "FISHING SUCCESS: +" + buff_percent + "%";
				}
				if(buff_type == 3){
					int buff_percent = new Random().nextInt(2) + 1; 
					return ChatColor.RED.toString() + "TRIPLE FISH: " + buff_percent + "%";
				}
				if(buff_type == 4){
					int buff_percent = new Random().nextInt(10) + 1; 
					return ChatColor.RED.toString() + "DURABILITY: +" + buff_percent + "%";
				}
				if(buff_type == 5){
					int buff_percent = new Random().nextInt(11) + 1; 
					return ChatColor.RED.toString() + "JUNK FIND: " + buff_percent + "%";
				}
			}

			if(cur_tier == 3){
				if(buff_type == 0 || buff_type == 1){
					int buff_percent = new Random().nextInt(9) + 1; 
					return ChatColor.RED.toString() + "DOUBLE FISH: " + buff_percent + "%";
				}
				if(buff_type == 2){
					int buff_percent = new Random().nextInt(2) + 3; 
					return ChatColor.RED.toString() + "FISHING SUCCESS: +" + buff_percent + "%";
				}
				if(buff_type == 3){
					int buff_percent = new Random().nextInt(3) + 1; 
					return ChatColor.RED.toString() + "TRIPLE FISH: " + buff_percent + "%";
				}
				if(buff_type == 4){
					int buff_percent = new Random().nextInt(15) + 1; 
					return ChatColor.RED.toString() + "DURABILITY: +" + buff_percent + "%";
				}
				if(buff_type == 5){
					int buff_percent = new Random().nextInt(12) + 1; 
					return ChatColor.RED.toString() + "JUNK FIND: " + buff_percent + "%";
				}
			}

			if(cur_tier == 4){
				if(buff_type == 0){
					int buff_percent = new Random().nextInt(13) + 1; 
					return ChatColor.RED.toString() + "DOUBLE FISH: " + buff_percent + "%";
				}
				if(buff_type == 1){
					int buff_percent = 1; 
					return ChatColor.RED.toString() + "TREASURE FIND: " + buff_percent + "%";
				}
				if(buff_type == 2){
					int buff_percent = new Random().nextInt(2) + 4; 
					return ChatColor.RED.toString() + "FISHING SUCCESS: +" + buff_percent + "%";
				}
				if(buff_type == 3){
					int buff_percent = new Random().nextInt(4) + 1; 
					return ChatColor.RED.toString() + "TRIPLE FISH: " + buff_percent + "%";
				}
				if(buff_type == 4){
					int buff_percent = new Random().nextInt(20) + 1; 
					return ChatColor.RED.toString() + "DURABILITY: +" + buff_percent + "%";
				}
				if(buff_type == 5){
					int buff_percent = new Random().nextInt(13) + 1; 
					return ChatColor.RED.toString() + "JUNK FIND: " + buff_percent + "%";
				}
			}

			if(cur_tier == 5){
				if(buff_type == 0){
					int buff_percent = new Random().nextInt(24) + 1; 
					return ChatColor.RED.toString() + "DOUBLE FISH: " + buff_percent + "%";
				}
				if(buff_type == 1){
					int buff_percent = 1; 
					return ChatColor.RED.toString() + "TREASURE FIND: " + buff_percent + "%";
				}
				if(buff_type == 2){
					int buff_percent = new Random().nextInt(6) + 1; 
					return ChatColor.RED.toString() + "FISHING SUCCESS: +" + buff_percent + "%";
				}
				if(buff_type == 3){
					int buff_percent = new Random().nextInt(5) + 1; 
					return ChatColor.RED.toString() + "TRIPLE FISH: " + buff_percent + "%";
				}
				if(buff_type == 4){
					int buff_percent = new Random().nextInt(25) + 1; 
					return ChatColor.RED.toString() + "DURABILITY: +" + buff_percent + "%";
				}
				if(buff_type == 5){
					int buff_percent = new Random().nextInt(15) + 1; 
					return ChatColor.RED.toString() + "JUNK FIND: " + buff_percent + "%";
				}
			}
		}

		if(skill.equalsIgnoreCase("mining")){
			int buff_type = new Random().nextInt(5);
			/* 0 = Double Ore
			 * 1 = Chance for Gems
			 * 2 = Chance of success increase
			 * 3 = Triple Ore
			 * 4 = Durability increase 
			 */

			if(cur_tier == 2){
				if(buff_type == 0){
					int buff_percent = new Random().nextInt(5) + 1; // 1-5%
					return ChatColor.RED.toString() + "DOUBLE ORE: " + buff_percent + "%";
				}
				if(buff_type == 1){
					int buff_percent = new Random().nextInt(3) + 1; // 1-5%
					return ChatColor.RED.toString() + "GEM FIND: " + buff_percent + "%";
				}
				if(buff_type == 2){
					int buff_percent = new Random().nextInt(2) + 1; // 1-5%
					return ChatColor.RED.toString() + "MINING SUCCESS: +" + buff_percent + "%";
				}
				if(buff_type == 3){
					int buff_percent = new Random().nextInt(2) + 1; // 1-5%
					return ChatColor.RED.toString() + "TRIPLE ORE: " + buff_percent + "%";
				}
				if(buff_type == 4){
					int buff_percent = new Random().nextInt(5) + 1; // 1-5%
					return ChatColor.RED.toString() + "DURABILITY: +" + buff_percent + "%";
				}
			}

			if(cur_tier == 3){
				if(buff_type == 0){
					int buff_percent = new Random().nextInt(9) + 1; // 1-5%
					return ChatColor.RED.toString() + "DOUBLE ORE: " + buff_percent + "%";
				}
				if(buff_type == 1){
					int buff_percent = new Random().nextInt(5) + 1; // 1-5%
					return ChatColor.RED.toString() + "GEM FIND: " + buff_percent + "%";
				}
				if(buff_type == 2){
					int buff_percent = new Random().nextInt(3) + 2; // 1-5%
					return ChatColor.RED.toString() + "MINING SUCCESS: +" + buff_percent + "%";
				}
				if(buff_type == 3){
					int buff_percent = new Random().nextInt(3) + 1; // 1-5%
					return ChatColor.RED.toString() + "TRIPLE ORE: " + buff_percent + "%";
				}
				if(buff_type == 4){
					int buff_percent = new Random().nextInt(10) + 1; // 1-5%
					return ChatColor.RED.toString() + "DURABILITY: +" + buff_percent + "%";
				}
			}

			if(cur_tier == 4){
				if(buff_type == 0){
					int buff_percent = new Random().nextInt(13) + 1; // 1-5%
					return ChatColor.RED.toString() + "DOUBLE ORE: " + buff_percent + "%";
				}
				if(buff_type == 1){
					int buff_percent = new Random().nextInt(8) + 1; // 1-5%
					return ChatColor.RED.toString() + "GEM FIND: " + buff_percent + "%";
				}
				if(buff_type == 2){
					int buff_percent = new Random().nextInt(4) + 3; // 1-5%
					return ChatColor.RED.toString() + "MINING SUCCESS: +" + buff_percent + "%";
				}
				if(buff_type == 3){
					int buff_percent = new Random().nextInt(4) + 1; // 1-5%
					return ChatColor.RED.toString() + "TRIPLE ORE: " + buff_percent + "%";
				}
				if(buff_type == 4){
					int buff_percent = new Random().nextInt(15) + 1; // 1-5%
					return ChatColor.RED.toString() + "DURABILITY: +" + buff_percent + "%";
				}
			}

			if(cur_tier == 5){
				if(buff_type == 0){
					int buff_percent = new Random().nextInt(17) + 1; // 1-5%
					return ChatColor.RED.toString() + "DOUBLE ORE: " + buff_percent + "%";
				}
				if(buff_type == 1){
					int buff_percent = new Random().nextInt(11) + 1; // 1-5%
					return ChatColor.RED.toString() + "GEM FIND: " + buff_percent + "%";
				}
				if(buff_type == 2){
					int buff_percent = new Random().nextInt(5) + 4; // 1-5%
					return ChatColor.RED.toString() + "MINING SUCCESS: +" + buff_percent + "%";
				}
				if(buff_type == 3){
					int buff_percent = new Random().nextInt(5) + 1; // 1-5%
					return ChatColor.RED.toString() + "TRIPLE ORE: " + buff_percent + "%";
				}
				if(buff_type == 4){
					int buff_percent = new Random().nextInt(20) + 1; // 1-5%
					return ChatColor.RED.toString() + "DURABILITY: +" + buff_percent + "%";
				}
			}
		}
		return "";
	}

	public static void addEXP(Player pl, ItemStack is, int amount, String skill){

		if(profession_buff){
			amount = (int)Math.round(amount * 1.20D); // +20%
		}

		int cur_exp = getItemEXP(is);
		boolean lvl_100 = false;
		ItemStack orig_is = CraftItemStack.asCraftCopy(is);
		cur_exp += amount;
		int new_level = 0;

		if(getItemLevel(is) == 100){
			return; // NO EXP can be gained.
		}

		ItemMeta im = is.getItemMeta();

		int needed_exp = getEXPNeeded(getItemLevel(is), skill);

		if(CommunityMechanics.toggle_list.containsKey(pl.getName()) && CommunityMechanics.toggle_list.get(pl.getName()).contains("debug")){
			pl.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + "          +" + ChatColor.YELLOW + (int)amount + ChatColor.BOLD + " EXP" + ChatColor.YELLOW
					+ ChatColor.GRAY + " [" + cur_exp + ChatColor.BOLD + "/" + ChatColor.GRAY + (int)needed_exp + " EXP]");
		}

		if(cur_exp >= needed_exp){
			// LEVEL UP!
			boolean new_stat = false;
			new_level = (getItemLevel(is) + 1);

			String item_name = "";
			if(skill.equalsIgnoreCase("mining")){
				item_name = "Pickaxe";
			}
			if(skill.equalsIgnoreCase("fishing")){
				item_name = "Fishingrod";
			}

			pl.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + "         " + item_name.toUpperCase() + " LEVEL UP! " 
					+ ChatColor.YELLOW + ChatColor.UNDERLINE + (new_level - 1) + ChatColor.BOLD + " -> " + ChatColor.YELLOW + ChatColor.UNDERLINE + new_level);
			pl.playSound(pl.getLocation(), Sound.LEVEL_UP, 0.5F, 1F);

			if(new_level == 20){
				new_stat = true;
				if(skill.equalsIgnoreCase("mining")){
					is.setType(Material.STONE_PICKAXE);
					AchievmentMechanics.addAchievment(pl.getName(), "Leveling up your pickaxe I");
				}
				else if(skill.equalsIgnoreCase("fishing")){
					AchievmentMechanics.addAchievment(pl.getName(), "Leveling up your Rod I");
				}
				im.setDisplayName(ChatColor.GREEN.toString() + "Apprentice " + item_name);
			}
			if(new_level == 40){
				new_stat = true;
				if(skill.equalsIgnoreCase("mining")){
					is.setType(Material.IRON_PICKAXE);
					AchievmentMechanics.addAchievment(pl.getName(), "Leveling up your pickaxe II");
				}
				else if(skill.equalsIgnoreCase("fishing")){
					AchievmentMechanics.addAchievment(pl.getName(), "Leveling up your Rod II");
				}
				im.setDisplayName(ChatColor.AQUA.toString() + "Expert " + item_name);
			}
			if(new_level == 60){
				new_stat = true;
				if(skill.equalsIgnoreCase("mining")){
					is.setType(Material.DIAMOND_PICKAXE);
					AchievmentMechanics.addAchievment(pl.getName(), "Leveling up your pickaxe III");
				}
				else if(skill.equalsIgnoreCase("fishing")){
					AchievmentMechanics.addAchievment(pl.getName(), "Leveling up your Rod III");
				}
				im.setDisplayName(ChatColor.LIGHT_PURPLE.toString() + "Supreme " + item_name);
			}
			if(new_level == 80){
				new_stat = true;
				if(skill.equalsIgnoreCase("mining")){
					is.setType(Material.GOLD_PICKAXE);
					AchievmentMechanics.addAchievment(pl.getName(), "Leveling up your pickaxe IV");
				}
				else if(skill.equalsIgnoreCase("fishing")){
					AchievmentMechanics.addAchievment(pl.getName(), "Leveling up your Rod IV");
				}
				if(AchievmentMechanics.hasAchievment(pl.getName(), "Leveling up your pickaxe IV") && AchievmentMechanics.hasAchievment(pl.getName(), "Leveling up your Rod IV")){
					AchievmentMechanics.addAchievment(pl.getName(), "The Skill Master");
				}
				im.setDisplayName(ChatColor.YELLOW.toString() + "Master " + item_name);
			}
			if(new_level == 100){
				new_stat = true;
				lvl_100 = true;
				im.setDisplayName(ChatColor.YELLOW.toString() + "Grand Master " + item_name);
				pl.sendMessage(ChatColor.YELLOW + "Congratulations! Your " + item_name.toLowerCase() + " has reached " + ChatColor.UNDERLINE + "LVL 100" + ChatColor.YELLOW + " this means you can no longe repair it. You now have TWO options.");
				pl.sendMessage(ChatColor.YELLOW.toString() + ChatColor.BOLD.toString() + "(1) " + ChatColor.YELLOW + "You can exchange the " + item_name.toLowerCase() + " at the merchant for a 'Buff Token' that will hold all the custom stats of your " + item_name.toLowerCase() + " and may be applied to a new " + item_name.toLowerCase() + ".");
				pl.sendMessage(ChatColor.YELLOW.toString() + ChatColor.BOLD.toString() + "(2) " + ChatColor.YELLOW + "If you continue to use this " + item_name.toLowerCase() + " until it runs out of durability, it will transform into a LVL 1 " + item_name.toLowerCase() + ", but it will retain all its custom stats.");
				pl.sendMessage("");
				// TODO: Exchange for T1?
			}

			List<String> new_lore = new ArrayList<String>();
			for(String s : is.getItemMeta().getLore()){
				if(s.contains("Level:")){
					// Hijack this, put our own value in.
					s = ChatColor.GRAY.toString() + "Level: " + getTierColor(getItemTier(is)) + new_level;
					//log.info("new_level= " + new_level);
				}

				if(s.contains(ChatColor.ITALIC.toString())){
					// Inject random stat buff if needed.
					if(new_stat == true){

						if(is.getItemMeta().getLore().size() >= 7){

						}

						String stat = "";

						//if(skill.equalsIgnoreCase("fishing")){
						if(getItemTier(is) < 5){
							stat = getRandomStatBuff(getItemTier(is)+1, skill);
						}
						else{
							stat = getRandomStatBuff(getItemTier(is), skill);
						}
						//}
						/*else{
							stat = getRandomStatBuff(getItemTier(is), skill);
						}*/

						String raw_stat = stat.substring(0, stat.lastIndexOf(" "));
						String delim = " ";
						if(stat.contains("+")){
							delim = "+";
						}
						int stat_val = Integer.parseInt(stat.substring(stat.lastIndexOf(delim) + 1, stat.lastIndexOf("%")));

						boolean guuci = false;
						while(guuci == false){
							boolean exists = false;
							for(String val : new_lore){
								if(val.contains(raw_stat)){
									exists = true;
									int cur_stat_val = Integer.parseInt(val.substring(val.lastIndexOf(delim) + 1, val.lastIndexOf("%")));
									if(stat_val > cur_stat_val){ // New stat is better.
										new_lore.remove(val);
										new_lore.add(stat); // Remove old stat, add new one to lore list.
										if(CommunityMechanics.toggle_list.containsKey(pl.getName()) && CommunityMechanics.toggle_list.get(pl.getName()).contains("debug")){
											pl.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + "       " + item_name.toUpperCase() + " UPGRADED: " + stat);
										}
										guuci = true; // We're good.
										break;
									}
									stat = getRandomStatBuff(getItemTier(is)+1, skill);
									raw_stat = stat.substring(0, stat.lastIndexOf(" "));
									guuci = true;
									break;
								}
							}
							if(exists == false){
								new_lore.add(stat);
								if(CommunityMechanics.toggle_list.containsKey(pl.getName()) && CommunityMechanics.toggle_list.get(pl.getName()).contains("debug")){
									pl.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + "       " + item_name.toUpperCase() + " GAINED: " + stat);
								}
								guuci = true;
							}
						}
					}

					s = ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + getItemDescription(is);

				}
				new_lore.add(s);
			}

			im.setLore(new_lore);
			is.setItemMeta(im);

			cur_exp = 0;
			needed_exp = getEXPNeeded(new_level, skill);
		}

		List<String> new_lore = new ArrayList<String>();
		for(String s : is.getItemMeta().getLore()){
			if(s.contains("/")){
				s = ChatColor.GRAY.toString() + cur_exp + " / " + needed_exp;
			}
			if(s.contains("EXP:")){
				// Hijack this, put our own value in.
				if(lvl_100 == false){
					s = ChatColor.GRAY.toString() + "EXP: " + generateEXPBar(cur_exp, needed_exp, 50);
				}
				else if(lvl_100 == true){
					s = ChatColor.GRAY.toString() + "EXP: " + ChatColor.YELLOW.toString() + "||||||||||||||||||||||||||||||||||||||||||||||||||";
				}
			}
			new_lore.add(s);
		}

		im.setLore(new_lore);
		item_exp.remove(orig_is);
		is.setItemMeta(im);

		if(new_level == 100){
			EnchantMechanics.addGlow(is);
		}

		RepairMechanics.setCustomDurability(is, RepairMechanics.getCustomDurability(orig_is, "wep"), "wep", true);

		item_exp.put(is, cur_exp);
	}

	public static String generateEXPBar(double cur_exp, double needed_exp, int how_many_bars){
		int max_bar = how_many_bars;

		String return_string = ChatColor.GREEN.toString() + "";

		DecimalFormat df = new DecimalFormat("##.#");
		double percent_exp = (double)(Math.round(100.0D * Double.parseDouble((df.format((cur_exp / needed_exp)))))); // EX: 0.5054134131

		if(percent_exp <= 0 && cur_exp > 0){
			percent_exp = 1;
		}

		double percent_interval = (100.0D / max_bar);
		int bar_count = 0;

		while(percent_exp > 0 && bar_count < max_bar){
			percent_exp -= percent_interval;
			bar_count++;
			return_string += "|";
		}

		return_string += ChatColor.RED.toString();

		while(bar_count < max_bar){
			return_string += "|";
			bar_count++;
		}

		return return_string;
	}

	public int getBreakChance(ItemStack is){
		Material m = is.getType();
		int win = 50; // Default is 50%.
		int i_level = getItemLevel(is);
		if(m == Material.WOOD_PICKAXE){
			win += ((i_level) * 2); // +2% per level. 
		}
		if(m == Material.STONE_PICKAXE){
			win += ((i_level - 20) * 2); // +2% per level. 
		}
		if(m == Material.IRON_PICKAXE){
			win += ((i_level - 40) * 2); // +2% per level. 
		}
		if(m == Material.DIAMOND_PICKAXE){
			win += ((i_level - 60) * 2); // +2% per level. 
		}
		if(m == Material.GOLD_PICKAXE){
			win += ((i_level - 80) * 2); // +2% per level. 
		}
		return win;
	}

	public int getOreEXP(Material m){
		if(m == Material.COAL_ORE){
			return 90 + new Random().nextInt(35);
		}
		if(m == Material.EMERALD_ORE){
			return 275 + new Random().nextInt(35);
		}
		if(m == Material.IRON_ORE){
			return 460 + new Random().nextInt(80);
		}
		if(m == Material.DIAMOND_ORE){
			return 820 + new Random().nextInt(40);
		}
		if(m == Material.GOLD_ORE){
			return 1025 + new Random().nextInt(55);
		}
		return 1;
	}

	public int getFishEXP(int tier){
		if(tier == 1){
			return (int)(2.5D * (250 + new Random().nextInt((int)(250 * 0.3D))));
		}
		if(tier == 2){
			return (int)(2.5D * (430 + new Random().nextInt((int)(430 * 0.3D))));
		}
		if(tier == 3){
			return (int)(2.5D * (820 + new Random().nextInt((int)(820 * 0.3D))));
		}
		if(tier == 4){
			return (int)(2.5D * (1050 + new Random().nextInt((int)(1050 * 0.3D))));
		}
		if(tier == 5){
			return (int)(2.5D * (1230 + new Random().nextInt((int)(1230 * 0.3D))));
		}
		return 1;
	}

	public static ItemStack getFishDrop(int tier){
		//ItemMechanics.signCustomItem(Material.GOLD_ORE, (short)0, ChatColor.YELLOW.toString() + "Gold Ore", ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() 
		//+ "A sparkling piece of gold ore.");

		int fish_type = new Random().nextInt(3); // 0, 1, 2
		String fish_name = "";
		int hunger_to_heal = 0;

		int buff_chance = 0;
		int do_i_buff = new Random().nextInt(100);

		boolean fish_buff = false;
		String fish_buff_s = "";

		if(tier == 1){
			buff_chance = 20;
			hunger_to_heal = 10;//%

			if(fish_type == 0){
				fish_name = ChatColor.WHITE.toString() + "Shrimp";
			}
			if(fish_type == 1){
				fish_name = ChatColor.WHITE.toString() + "Anchovies";
			}
			if(fish_type == 2){
				fish_name = ChatColor.WHITE.toString() + "Crayfish";
			}

			if(buff_chance >= do_i_buff){
				fish_buff = true;
				int buff_type = new Random().nextInt(100);
				int buff_val = 0;
				if(buff_type >= 0 && buff_type <= 15){
					// Of Power (DMG) 1-2%
					buff_val = new Random().nextInt(2) + 1;
					fish_name += " of Lesser Power";
					fish_buff_s = ChatColor.RED.toString() + "+" + buff_val + "% DMG " + ChatColor.GRAY.toString() + "(20s)";
				}
				if(buff_type > 15 && buff_type <= 25){
					// Of Health 1-3% HP (instant heal)
					buff_val = new Random().nextInt(3) + 1;
					fish_name =  ChatColor.WHITE.toString() + "Small, Healing " + fish_name;
					fish_buff_s = ChatColor.RED.toString() + "+" + buff_val + "% HP " + ChatColor.GRAY.toString() + "(instant)";
				}
				if(buff_type > 25 && buff_type <= 50){
					// Of Speed 15 seconds of speed I.
					fish_name += " of Lesser Agility";
					fish_buff_s = ChatColor.RED.toString() + "SPEED (I) BUFF " + ChatColor.GRAY.toString() + "(15s)";
				}
				if(buff_type > 50 && buff_type <= 60){
					// Of Satiety, fill up 20% of food (2 full squares)
					buff_val = 20;
					fish_name += " of Minor Satiety";
					fish_buff_s = ChatColor.RED.toString() + "-" + buff_val + "% HUNGER " + ChatColor.GRAY.toString() + "(instant)";
				}
				if(buff_type > 60 && buff_type <= 70){
					// Of Defence (ARMOR%) 1-2% ARMOR
					buff_val = new Random().nextInt(2) + 1;
					fish_name += " of Weak Defense";
					fish_buff_s = ChatColor.RED.toString() + "+" + buff_val + "% ARMOR " + ChatColor.GRAY.toString() + "(20s)";
				}
				if(buff_type > 70){
					// Nightvision for 60 seconds.
					buff_val = new Random().nextInt(2) + 1;
					fish_name += " of Vision";
					fish_buff_s = ChatColor.RED.toString() + "NIGHTVISION (I) BUFF " + ChatColor.GRAY.toString() + "(30s)";
				}
			}
		}

		if(tier == 2){
			buff_chance = 25;
			hunger_to_heal = 20;//%

			if(fish_type == 0){
				fish_name = ChatColor.GREEN.toString() + "Heron";
			}
			if(fish_type == 1){
				fish_name = ChatColor.GREEN.toString() + "Herring";
			}
			if(fish_type == 2){
				fish_name = ChatColor.GREEN.toString() + "Sardine";
			}

			if(buff_chance >= do_i_buff){
				fish_buff = true;
				int buff_type = new Random().nextInt(100);
				int buff_val = 0;
				if(buff_type >= 0 && buff_type <= 10){
					// Of Power (DMG) 1-2%
					buff_val = new Random().nextInt(3) + 1;
					fish_name += " of Power";
					fish_buff_s = ChatColor.RED.toString() + "+" + buff_val + "% DMG " + ChatColor.GRAY.toString() + "(25s)";
				}
				if(buff_type > 10 && buff_type <= 15){
					// Of HP REGEN
					buff_val = new Random().nextInt(5) + 5;
					fish_name += " of Regeneration";
					fish_buff_s = ChatColor.RED.toString() + "REGEN " + buff_val + "% HP " + ChatColor.GRAY.toString() + "(over 10s)";
				}
				if(buff_type > 15 && buff_type <= 20){
					// OF BLOCK%
					buff_val = new Random().nextInt(5) + 1;
					fish_name += " of Blocking";
					fish_buff_s = ChatColor.RED.toString() + "+" + buff_val + "% BLOCK " + ChatColor.GRAY.toString() + "(25s)";
				}
				if(buff_type > 20 && buff_type <= 30){
					// Of Health (instant heal)
					buff_val = new Random().nextInt(5) + 1;
					fish_name =  ChatColor.GREEN.toString() + "Healing " + fish_name;
					fish_buff_s = ChatColor.RED.toString() + "+" + buff_val + "% HP " + ChatColor.GRAY.toString() + "(instant)";
				}
				if(buff_type > 30 && buff_type <= 55){
					fish_name += " of Agility";
					fish_buff_s = ChatColor.RED.toString() + "SPEED (I) BUFF " + ChatColor.GRAY.toString() + "(20s)";
				}
				if(buff_type > 55 && buff_type <= 65){
					// Of Satiety, fill up 20% of food (2 full squares)
					buff_val = 25;
					fish_name += " of Satiety";
					fish_buff_s = ChatColor.RED.toString() + "-" + buff_val + "% HUNGER " + ChatColor.GRAY.toString() + "(instant)";
				}
				if(buff_type > 65 && buff_type <= 75){
					// Of Defence (ARMOR%) 1-2% ARMOR
					buff_val = new Random().nextInt(3) + 1;
					fish_name += " of Defense";
					fish_buff_s = ChatColor.RED.toString() + "+" + buff_val + "% ARMOR " + ChatColor.GRAY.toString() + "(25s)";
				}
				if(buff_type > 75){
					// Nightvision for 60 seconds.
					buff_val = new Random().nextInt(2) + 1;
					fish_name += " of Vision";
					fish_buff_s = ChatColor.RED.toString() + "NIGHTVISION (I) BUFF " + ChatColor.GRAY.toString() + "(45s)";
				}
			}
		}

		if(tier == 3){
			buff_chance = 33;
			hunger_to_heal = 30;//%

			if(fish_type == 0){
				fish_name = ChatColor.AQUA.toString() + "Salmon";
			}
			if(fish_type == 1){
				fish_name = ChatColor.AQUA.toString() + "Trout";
			}
			if(fish_type == 2){
				fish_name = ChatColor.AQUA.toString() + "Cod";
			}

			if(buff_chance >= do_i_buff){
				fish_buff = true;
				int buff_type = new Random().nextInt(100);
				int buff_val = 0;
				if(buff_type >= 0 && buff_type <= 10){
					// Of Power (DMG) 1-2%
					buff_val = new Random().nextInt(3) + 3;
					fish_name += " of Greater Power";
					fish_buff_s = ChatColor.RED.toString() + "+" + buff_val + "% DMG " + ChatColor.GRAY.toString() + "(30s)";
				}
				if(buff_type > 10 && buff_type <= 15){
					// Of HP REGEN
					buff_val = new Random().nextInt(11) + 5;
					fish_name += " of Mighty Regeneration";
					fish_buff_s = ChatColor.RED.toString() + "REGEN " + buff_val + "% HP " + ChatColor.GRAY.toString() + "(over 10s)";
				}
				if(buff_type > 15 && buff_type <= 20){
					// OF BLOCK%
					buff_val = new Random().nextInt(5) + 1;
					fish_name += " of Blocking";
					fish_buff_s = ChatColor.RED.toString() + "+" + buff_val + "% BLOCK " + ChatColor.GRAY.toString() + "(30s)";
				}
				if(buff_type > 20 && buff_type <= 30){
					// Of Health (instant heal)
					buff_val = new Random().nextInt(4) + 4;
					fish_name =  ChatColor.AQUA.toString() + "Large, Healing " + fish_name;
					fish_buff_s = ChatColor.RED.toString() + "+" + buff_val + "% HP " + ChatColor.GRAY.toString() + "(instant)";
				}
				if(buff_type > 30 && buff_type <= 55){
					fish_name += " of Lasting Agility";
					fish_buff_s = ChatColor.RED.toString() + "SPEED (I) BUFF " + ChatColor.GRAY.toString() + "(30s)";
				}
				if(buff_type > 55 && buff_type <= 65){
					// Of Satiety, fill up 20% of food (2 full squares)
					buff_val = 30;
					fish_name += " of Great Satiety";
					fish_buff_s = ChatColor.RED.toString() + "-" + buff_val + "% HUNGER " + ChatColor.GRAY.toString() + "(instant)";
				}
				if(buff_type > 65 && buff_type <= 75){
					// Of Defence (ARMOR%) 1-2% ARMOR
					buff_val = new Random().nextInt(3) + 3;
					fish_name += " of Mighty Defense";
					fish_buff_s = ChatColor.RED.toString() + "+" + buff_val + "% ARMOR " + ChatColor.GRAY.toString() + "(30s)";
				}
				if(buff_type > 75){
					// Nightvision for 60 seconds.
					buff_val = new Random().nextInt(2) + 1;
					fish_name += " of Lasting Vision";
					fish_buff_s = ChatColor.RED.toString() + "NIGHTVISION (I) BUFF " + ChatColor.GRAY.toString() + "(60s)";
				}
			}
		}

		if(tier == 4){
			buff_chance = 33;
			hunger_to_heal = 40;//%

			if(fish_type == 0){
				fish_name = ChatColor.LIGHT_PURPLE.toString() + "Lobster";
			}
			if(fish_type == 1){
				fish_name = ChatColor.LIGHT_PURPLE.toString() + "Tuna";
			}
			if(fish_type == 2){
				fish_name = ChatColor.LIGHT_PURPLE.toString() + "Bass";
			}

			int buff_time = new Random().nextInt(10) + 40; // Up to 49s.

			if(buff_chance >= do_i_buff){
				fish_buff = true;
				int buff_type = new Random().nextInt(100);
				int buff_val = 0;
				if(buff_type >= 0 && buff_type <= 10){
					// Of Power (DMG) 1-2%
					buff_val = new Random().nextInt(6) + 5;
					fish_name += " of Ancient Power";
					fish_buff_s = ChatColor.RED.toString() + "+" + buff_val + "% DMG " + ChatColor.GRAY.toString() + "(" + buff_time + "s)";
				}
				if(buff_type > 10 && buff_type <= 15){
					// Of HP REGEN
					buff_val = new Random().nextInt(6) + 10;
					fish_name += " of Enhanced Regeneration";
					fish_buff_s = ChatColor.RED.toString() + "REGEN " + buff_val + "% HP " + ChatColor.GRAY.toString() + "(over 10s)";
				}
				if(buff_type > 15 && buff_type <= 20){
					// OF BLOCK%
					buff_val = new Random().nextInt(5) + 4;
					fish_name += " of Greater Blocking";
					fish_buff_s = ChatColor.RED.toString() + "+" + buff_val + "% BLOCK " + ChatColor.GRAY.toString() + "(" + buff_time + "s)";
				}
				if(buff_type > 20 && buff_type <= 30){
					// Of Health (instant heal)
					buff_val = new Random().nextInt(4) + 4;
					fish_name = ChatColor.LIGHT_PURPLE.toString() + "Healthy " + fish_name;
					fish_buff_s = ChatColor.RED.toString() + "+" + buff_val + "% HP " + ChatColor.GRAY.toString() + "(instant)";
				}
				if(buff_type > 30 && buff_type <= 55){
					fish_name += " of Bursting Agility";
					fish_buff_s = ChatColor.RED.toString() + "SPEED (II) BUFF " + ChatColor.GRAY.toString() + "(15s)";
				}
				if(buff_type > 55 && buff_type <= 65){
					// Of Satiety, fill up 20% of food (2 full squares)
					buff_val = 30;
					fish_name = ChatColor.LIGHT_PURPLE.toString() + "Huge " + fish_name;
					fish_buff_s = ChatColor.RED.toString() + "-" + buff_val + "% HUNGER " + ChatColor.GRAY.toString() + "(instant)";
				}
				if(buff_type > 65 && buff_type <= 75){
					// Of Defence (ARMOR%) 1-2% ARMOR
					buff_val = new Random().nextInt(5) + 4;
					fish_name += " of Fortified Defense";
					fish_buff_s = ChatColor.RED.toString() + "+" + buff_val + "% ARMOR " + ChatColor.GRAY.toString() + "(" + buff_time + "s)";
				}
				if(buff_type >= 75 && buff_type < 80){
					// Vampirism
					buff_val = new Random().nextInt(2) + 4;
					fish_name = "Albino " + ChatColor.LIGHT_PURPLE.toString() + fish_name;
					fish_buff_s = ChatColor.RED.toString() + "+" + buff_val + "% LIFESTEAL " + ChatColor.GRAY.toString() + "(" + buff_time + "s)";
				}
				if(buff_type > 80){
					// Nightvision for 60 seconds.
					fish_name += " of Eagle Vision";
					fish_buff_s = ChatColor.RED.toString() + "NIGHTVISION (II) BUFF " + ChatColor.GRAY.toString() + "(" + buff_time + "s)";
				}
			}
		}

		if(tier == 5){
			buff_chance = 45;
			hunger_to_heal = 50;//%

			if(fish_type == 0){
				fish_name = ChatColor.YELLOW.toString() + "Shark";
			}
			if(fish_type == 1){
				fish_name = ChatColor.YELLOW.toString() + "Swordfish";
			}
			if(fish_type == 2){
				fish_name = ChatColor.YELLOW.toString() + "Monkfish";
			}

			int buff_time = new Random().nextInt(11) + 50; // Up to 60s.

			if(buff_chance >= do_i_buff){
				fish_buff = true;
				int buff_type = new Random().nextInt(100);
				int buff_val = 0;
				if(buff_type >= 0 && buff_type <= 10){
					// Of Power (DMG) 1-2%
					buff_val = new Random().nextInt(11) + 5;
					fish_name += " of Legendary Power";
					fish_buff_s = ChatColor.RED.toString() + "+" + buff_val + "% DMG " + ChatColor.GRAY.toString() + "(" + buff_time + "s)";
				}
				if(buff_type > 10 && buff_type <= 15){
					// Of HP REGEN
					buff_val = new Random().nextInt(6) + 10;
					fish_name += " of Extreme Regeneration";
					fish_buff_s = ChatColor.RED.toString() + "REGEN " + buff_val + "% HP " + ChatColor.GRAY.toString() + "(over 10s)";
				}
				if(buff_type > 15 && buff_type <= 20){
					// OF BLOCK%
					buff_val = new Random().nextInt(5) + 4;
					fish_name += " of Greater Blocking";
					fish_buff_s = ChatColor.RED.toString() + "+" + buff_val + "% BLOCK " + ChatColor.GRAY.toString() + "(" + buff_time + "s)";
				}
				if(buff_type > 20 && buff_type <= 30){
					// Of Health (instant heal)
					buff_val = new Random().nextInt(6) + 5;
					fish_name = ChatColor.YELLOW.toString() + "Legendary " + fish_name + " of Medicine";
					fish_buff_s = ChatColor.RED.toString() + "+" + buff_val + "% HP " + ChatColor.GRAY.toString() + "(instant)";
				}
				if(buff_type > 30 && buff_type <= 45){
					fish_name += " of Godlike Speed";
					fish_buff_s = ChatColor.RED.toString() + "SPEED (II) BUFF " + ChatColor.GRAY.toString() + "(30s)";
				}
				if(buff_type > 45 && buff_type <= 50){
					// Of Satiety, fill up 20% of food (2 full squares)
					buff_val = 40;
					fish_name = ChatColor.YELLOW.toString() + "Gigantic " + fish_name;
					fish_buff_s = ChatColor.RED.toString() + "-" + buff_val + "% HUNGER " + ChatColor.GRAY.toString() + "(instant)";
				}
				if(buff_type > 50 && buff_type <= 60){
					// Of Defence (ARMOR%) 1-2% ARMOR
					buff_val = new Random().nextInt(6) + 5;
					fish_name = ChatColor.YELLOW.toString() + "Hardended " + fish_name + " of Legendary Defense";
					fish_buff_s = ChatColor.RED.toString() + "+" + buff_val + "% ARMOR " + ChatColor.GRAY.toString() + "(" + buff_time + "s)";
				}
				if(buff_type > 60 && buff_type <= 65){
					// Vampirism
					buff_val = new Random().nextInt(5) + 3;
					fish_name = "Albino " + ChatColor.YELLOW.toString() + fish_name;
					fish_buff_s = ChatColor.RED.toString() + "+" + buff_val + "% LIFESTEAL " + ChatColor.GRAY.toString() + "(" + buff_time + "s)";
				}
				if(buff_type > 65 && buff_type <= 85){
					// Nightvision for 60 seconds.
					fish_name += " of Omniscient Vision";
					fish_buff_s = ChatColor.RED.toString() + "NIGHTVISION (II) BUFF " + ChatColor.GRAY.toString() + "(" + (buff_time + 60) + "s)";
				}
				if(buff_type > 85 && buff_type <= 90){
					// Critical hit bonus
					buff_val = new Random().nextInt(5) + 1;
					fish_name = "Perfect " + ChatColor.YELLOW.toString() + fish_name + " of Accuracy";
					fish_buff_s = ChatColor.RED.toString() + "+" + buff_val + "% CRITICAL HIT " + ChatColor.GRAY.toString() + "(" + buff_time + "s)";
				}
				if(buff_type > 90){
					// Energy Regen Buff
					buff_val = new Random().nextInt(5) + 1;
					fish_name = ChatColor.YELLOW.toString() + fish_name + " of Hidden Energy";
					fish_buff_s = ChatColor.RED.toString() + "+" + buff_val + "% ENERGY REGEN " + ChatColor.GRAY.toString() + "(" + buff_time + "s)";
				}
			}
		}

		List<String> fish_lore = new ArrayList<String>();
		if(fish_buff == true){
			fish_lore.add(fish_buff_s);
		}
		fish_lore.add(ChatColor.RED + "-" + hunger_to_heal + "% HUNGER " + ChatColor.GRAY.toString() + "(instant)");
		fish_lore.add(getFishLore(fish_name));

		//short ran_dur = (short)new Random().nextInt(32767);
		// Helps prevent stackability.

		if(fish_name.contains(ChatColor.WHITE.toString())){
			fish_name = ChatColor.WHITE.toString() + "Raw " + fish_name; 
		}
		if(fish_name.contains(ChatColor.GREEN.toString())){
			fish_name = ChatColor.GREEN.toString() + "Raw " + fish_name; 
		}
		if(fish_name.contains(ChatColor.AQUA.toString())){
			fish_name = ChatColor.AQUA.toString() + "Raw " + fish_name; 
		}
		if(fish_name.contains(ChatColor.LIGHT_PURPLE.toString())){
			fish_name = ChatColor.LIGHT_PURPLE.toString() + "Raw " + fish_name; 
		}
		if(fish_name.contains(ChatColor.YELLOW.toString())){
			fish_name = ChatColor.YELLOW.toString() + "Raw " + fish_name; 
		}

		//ItemStack fish = new ItemStack(Material.RAW_FISH, 1, ran_dur);
		ItemStack fish = new ItemStack(Material.RAW_FISH, 1);
		ItemMeta im = fish.getItemMeta();
		im.setDisplayName(fish_name);
		im.setLore(fish_lore);
		fish.setItemMeta(im);
		return fish;
	}

	public static String getFishLore(String fish_name){
		if(fish_name.toLowerCase().contains("shrimp")){
			return ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + "A pink scaled crustacean.";
		}
		if(fish_name.toLowerCase().contains("anchovies")){
			return ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + "A small blue oily fish.";
		}
		if(fish_name.toLowerCase().contains("crayfish")){
			return ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + "A lobster-like and brown crustacean.";
		}

		if(fish_name.toLowerCase().contains("carp")){
			return ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + "A large silverscaled fish.";
		}
		if(fish_name.toLowerCase().contains("herring")){
			return ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + "A colourful and medium sized fish.";
		}
		if(fish_name.toLowerCase().contains("sardine")){
			return ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + "A small and oily green fish.";
		}

		if(fish_name.toLowerCase().contains("salmon")){
			return ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + "A beautiful jumping fish.";
		}
		if(fish_name.toLowerCase().contains("trout")){
			return ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + "A non-migrating Salmon.";
		}
		if(fish_name.toLowerCase().contains("cod")){
			return ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + "A cold water deep sea fish.";
		}

		if(fish_name.toLowerCase().contains("lobster")){
			return ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + "A large red crustacean.";
		}
		if(fish_name.toLowerCase().contains("tuna")){
			return ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + "A large sapphire blue fish.";
		}
		if(fish_name.toLowerCase().contains("bass")){
			return ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + "A very large and white fish.";
		}

		if(fish_name.toLowerCase().contains("shark")){
			return ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + "A terrifying and massive predator.";
		}
		if(fish_name.toLowerCase().contains("swordfish")){
			return ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + "An elongated fish with a long bill.";
		}
		if(fish_name.toLowerCase().contains("monkfish")){
			return ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + "A flat large and scary looking fish.";
		}

		return ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + "A freshly caught fish.";
	}

	public ItemStack getOreDrop(Material m){
		if(m == Material.COAL_ORE){
			return CraftItemStack.asCraftCopy(coal_ore);
		}
		if(m == Material.EMERALD_ORE){
			return CraftItemStack.asCraftCopy(emerald_ore);
		}
		if(m == Material.IRON_ORE){
			return CraftItemStack.asCraftCopy(iron_ore);
		}
		if(m == Material.DIAMOND_ORE){
			return CraftItemStack.asCraftCopy(diamond_ore);
		}
		if(m == Material.GOLD_ORE){
			return CraftItemStack.asCraftCopy(gold_ore);
		}
		return CraftItemStack.asCraftCopy(coal_ore);
	}

	public boolean isSkillEnchantScroll(ItemStack is){
		if(is.getType() != Material.EMPTY_MAP){
			return false;
		}
		if(!(is.hasItemMeta())){
			return false;
		}
		if(!(is.getItemMeta().hasDisplayName())){
			return false;
		}
		if(!(ChatColor.stripColor(is.getItemMeta().getDisplayName().toLowerCase()).contains("scroll: pickaxe")) && !(ChatColor.stripColor(is.getItemMeta().getDisplayName().toLowerCase()).contains("scroll: fishingrod"))){
			return false;
		}
		return true;
	}

	public static String getSkillType(ItemStack is){
		if(is.getType() == Material.WOOD_PICKAXE || is.getType() == Material.STONE_PICKAXE || is.getType() == Material.IRON_PICKAXE || is.getType() == Material.DIAMOND_PICKAXE || is.getType() == Material.GOLD_PICKAXE){
			return "mining";
		}
		if(is.getType() == Material.FISHING_ROD){
			return "fishing";
		}
		return null;
	}

	public void applySkillEnchant (ItemStack is, String enchant){
		List<String> new_lore = new ArrayList<String>();
		String skill = getSkillType(is);

		for(String s : is.getItemMeta().getLore()){
			if(s.contains(ChatColor.ITALIC.toString())){
				String stat = enchant;
				String raw_stat = stat.substring(0, stat.lastIndexOf(" "));
				String delim = " ";
				if(stat.contains("+")){
					delim = "+";
				}
				int stat_val = Integer.parseInt(stat.substring(stat.lastIndexOf(delim) + 1, stat.lastIndexOf("%")));

				boolean guuci = false;
				while(guuci == false){
					boolean exists = false;
					for(String val : new_lore){
						if(val.contains(raw_stat)){
							exists = true;
							int cur_stat_val = Integer.parseInt(val.substring(val.lastIndexOf(delim) + 1, val.lastIndexOf("%")));
							if(stat_val > cur_stat_val){ // New stat is better.
								new_lore.remove(val);
								new_lore.add(stat); // Remove old stat, add new one to lore list.
								guuci = true; // We're good.
								break;
							}
							stat = getRandomStatBuff(getItemTier(is), skill);
							raw_stat = stat.substring(0, stat.lastIndexOf(" "));
							guuci = true;
							break;
						}
					}
					if(exists == false){
						new_lore.add(stat);
						guuci = true;
					}
				}
				s = ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + getItemDescription(is);
			}

			new_lore.add(s);
		}

		ItemMeta im = is.getItemMeta();
		im.setLore(new_lore);
		is.setItemMeta(im);
	}

	public String getItemEnchant(ItemStack scroll){
		for(String s : scroll.getItemMeta().getLore()){
			if (s.startsWith(ChatColor.RED.toString())){
				// The enchant line!
				return s;
			}
		}
		return "";
	}

	public int getFishingSpotTier(Location loc){
		double closest_spot_distance_sqr = -1;
		Location closest_loc = null;
		for(Location fish_loc : fishing_location.keySet()){
			if(!(loc.getWorld().getName().equalsIgnoreCase(fish_loc.getWorld().getName()))){
				continue;
			}
			double dist_sqr = loc.distanceSquared(fish_loc);
			if(dist_sqr <= 100){
				// Within 10 blocks.

				if((System.currentTimeMillis() - fishing_respawn.get(fish_loc)) <= (getFishingSpotRespawnTime(fishing_location.get(fish_loc)) * 1000)){
					continue; // No fish spawned, don't even count it as a spot.
				}
				else if(fish_count.get(fish_loc) == 0){
					// So there's no cooldown left, but there aren't any fish, populate!
					fish_count.put(fish_loc, getRandomFishCount(fishing_location.get(fish_loc)));
				}

				if(closest_spot_distance_sqr != -1){
					if(dist_sqr < closest_spot_distance_sqr){
						closest_loc = fish_loc;
						closest_spot_distance_sqr = dist_sqr;
						continue;
					}
				}
				else{
					closest_loc = fish_loc;
				}
			}
		}

		if(closest_loc == null){
			return -1; // No spot within 50 blocks.
		}

		return fishing_location.get(closest_loc);
	}

	public Location getFishingSpot(Location loc){
		double closest_spot_distance_sqr = -1;
		Location closest_loc = null;
		for(Location fish_loc : fishing_location.keySet()){
			double dist_sqr = loc.distanceSquared(fish_loc);
			if(dist_sqr <= 100){
				// Within 10 blocks.

				if((System.currentTimeMillis() - fishing_respawn.get(fish_loc)) <= (getFishingSpotRespawnTime(fishing_location.get(fish_loc)) * 1000)){
					continue; // No fish spawned, don't even count it as a spot.
				}
				else if(fish_count.get(fish_loc) == 0){
					// So there's no cooldown left, but there aren't any fish, populate!
					fish_count.put(fish_loc, getRandomFishCount(fishing_location.get(fish_loc)));
				}

				if(closest_spot_distance_sqr != -1){
					if(dist_sqr < closest_spot_distance_sqr){
						closest_loc = fish_loc;
						closest_spot_distance_sqr = dist_sqr;
						continue;
					}
				}
				else{
					closest_loc = fish_loc;
				}
			}
		}

		if(closest_loc == null){
			return null; // No spot within 50 blocks.
		}

		return closest_loc;
	}

	public boolean isCustomFish(ItemStack is){
		if(is != null && is.getType() == Material.COOKED_FISH && is.hasItemMeta() && is.getItemMeta().hasDisplayName() && is.getItemMeta().hasLore()){
			return true;
		}
		return false;
	}

	public boolean isCustomRawFish(ItemStack is){
		if(is != null && is.getType() == Material.RAW_FISH && is.hasItemMeta() && is.getItemMeta().hasDisplayName() && is.getItemMeta().hasLore()){
			return true;
		}
		return false;
	}

	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerItemConsumeEvent(PlayerItemConsumeEvent e){
		final ItemStack is = e.getItem();
		final Player pl = e.getPlayer();
		final int food_level = pl.getFoodLevel();

		if(is.getType() == Material.ROTTEN_FLESH || is.getType() == Material.GOLDEN_CARROT || pl.getLocation().getBlock().getType() == Material.PORTAL || is.getType() == Material.SPIDER_EYE){
			if(!PetMechanics.isPermUntradeable(is)){
				return;
			}
			e.setCancelled(true);
			pl.updateInventory();
			Main.plugin.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
				public void run() {
					pl.setFoodLevel(food_level);
				}
			}, 4L);
			return;
		}

		if(DuelMechanics.duel_map.containsKey(pl.getName())){
			e.setCancelled(true);
			pl.updateInventory();
			pl.sendMessage(ChatColor.RED + "You " + ChatColor.UNDERLINE + "cannot" + ChatColor.RED + " use consumables while in a duel.");
			return;
		}

		if(isCustomRawFish(is) || is.getType() == Material.POTION){ // isCustomFish(is) || 
			if(isCustomRawFish(is)){
				e.getPlayer().sendMessage(ChatColor.RED + "You " + ChatColor.UNDERLINE + "cannot" + ChatColor.RED + " consume raw fish. Cook it using a heat source first.");
				e.getPlayer().sendMessage("");
			}
			e.setCancelled(true); // Don't ever eat the fish, this should never be called, but just incase.
			return;
		}

		if(isCustomFish(is)){
			e.setCancelled(true);
			ItemStack fish = pl.getItemInHand();
			if(fish.getAmount() > 1){
				// Subtract just 1.
				fish.setAmount(fish.getAmount() - 1);
				pl.setItemInHand(fish);
			}
			else if(fish.getAmount() <= 1){
				pl.setItemInHand(new ItemStack(Material.AIR));
			}
			pl.updateInventory();
			/*pl.getWorld().playSound(pl.getLocation(), Sound.EAT, 1F, 1F);
			this.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
				public void run() {
					pl.setItemInHand(new ItemStack(Material.AIR));
					pl.updateInventory();
				}
			}, 1L);
			this.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
				public void run() {
					pl.getWorld().playSound(pl.getLocation(), Sound.EAT, 1F, 1.5F);
				}
			}, 4L);*/
			// Ok, so now we need to see what we need to do for the player when they eat this. Let's handle the food aspect first.
			List<String> lore = is.getItemMeta().getLore();
			int food_to_restore = 0;
			for(String s : lore){
				if(s.contains("% HUNGER")){
					double percent = Integer.parseInt(s.substring(s.indexOf("-") + 1, s.indexOf("%")));
					int local_amount = (int)((percent / 100.0D) * 20D);
					food_to_restore += local_amount;
				}
			}

			int cur_food = pl.getFoodLevel();
			if(cur_food + food_to_restore >= 20){
				pl.setFoodLevel(20);
				pl.setSaturation(20);
			}
			else{
				pl.setFoodLevel(cur_food + food_to_restore);
				pl.setSaturation(cur_food + food_to_restore);
			}

			// Ok, food handled, now let's handle any misc. buffs that the fish might have.
			for(String s : lore){
				s = ChatColor.stripColor(s);
				if(s.contains("% HP (instant)")){
					// Instant heal.
					double percent_to_heal = Double.parseDouble(s.substring(s.indexOf("+") + 1, s.indexOf("%"))) / 100;
					double max_hp = HealthMechanics.getMaxHealthValue(pl.getName());
					int amount_to_heal = (int)Math.round((percent_to_heal * max_hp));
					double current_hp = HealthMechanics.getPlayerHP(pl.getName());
					if(current_hp + 1 > max_hp){continue;} // They have max HP.
					//amount_to_heal += getHealthRegenAmount(p);

					if(CommunityMechanics.toggle_list.containsKey(pl.getName()) && CommunityMechanics.toggle_list.get(pl.getName()).contains("debug")){
						pl.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "+" + ChatColor.GREEN + (int)amount_to_heal + ChatColor.BOLD + " HP" + ChatColor.GREEN + " FROM " + is.getItemMeta().getDisplayName() + ChatColor.GRAY + " [" + ((int)current_hp + (int)amount_to_heal) + "/" + (int)max_hp + "HP]");
					}

					if((current_hp + amount_to_heal) >= max_hp){
						pl.setHealth(20);
						//HealthMechanics.setPlayerHP(pl.getName(), (int)max_hp);
						HealthMechanics.setPlayerHP(pl.getName(), (int)max_hp);
						continue; // Full HP.
					}

					else if(pl.getHealth() <= 19 && ((current_hp + amount_to_heal) < max_hp)){
						HealthMechanics.setPlayerHP(pl.getName(), (int)(HealthMechanics.getPlayerHP(pl.getName()) + amount_to_heal));
						double health_percent = (HealthMechanics.getPlayerHP(pl.getName()) + amount_to_heal) / max_hp;
						double new_health_display = health_percent * 20;
						if(new_health_display > 19){
							if(health_percent >= 1){
								new_health_display = 20;
							}
							else if(health_percent < 1){
								new_health_display = 19;
							}
						}
						if(new_health_display < 1){
							new_health_display = 1;
						}
						pl.setHealth((int)new_health_display);

					}
				}

				if(s.startsWith("REGEN")){
					// Regen % of HP over X seconds.
					double percent_to_regen = Double.parseDouble(s.substring(s.indexOf(" ") + 1, s.indexOf("%"))) / 100.0D;
					int regen_interval = Integer.parseInt(s.substring(s.lastIndexOf(" ") + 1, s.lastIndexOf("s")));
					double max_hp = HealthMechanics.getMaxHealthValue(pl.getName());

					final int amount_to_regen_per_interval = (int)(max_hp * percent_to_regen) / regen_interval;
					pl.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "      " + ChatColor.GREEN + (int)amount_to_regen_per_interval + ChatColor.BOLD + " HP/s" + ChatColor.GREEN + " FROM " + is.getItemMeta().getDisplayName() + ChatColor.GRAY + " [" + regen_interval + "s]");

					fish_health_regen.put(pl.getName(), amount_to_regen_per_interval);
					pl.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, (int) (regen_interval * 20), 1));
					Main.plugin.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
						public void run() {
							pl.removePotionEffect(PotionEffectType.REGENERATION);
							fish_health_regen.remove(pl.getName());
							pl.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "   " + amount_to_regen_per_interval + " HP/s " + ChatColor.RED + "FROM " + is.getItemMeta().getDisplayName() + ChatColor.RED + " " + ChatColor.UNDERLINE + "EXPIRED");
						}
					}, regen_interval * 20L);
				}

				if(s.startsWith("SPEED")){
					// Speed effect.
					String tier_symbol = s.substring(s.indexOf("(") + 1, s.indexOf(")"));
					int effect_tier = 0;
					if(tier_symbol.equalsIgnoreCase("I")){
						effect_tier = 1;
					}
					if(tier_symbol.equalsIgnoreCase("II")){
						effect_tier = 2;
					}

					int effect_time = Integer.parseInt(s.substring(s.lastIndexOf("(") + 1, s.lastIndexOf("s")));
					pl.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, effect_time * 20, effect_tier));
				}

				if(s.startsWith("NIGHTVISION")){
					// Night vision effect
					String tier_symbol = s.substring(s.indexOf("(") + 1, s.indexOf(")"));
					int effect_tier = 0;
					if(tier_symbol.equalsIgnoreCase("I")){
						effect_tier = 1;
					}
					if(tier_symbol.equalsIgnoreCase("II")){
						effect_tier = 2;
					}

					int effect_time = Integer.parseInt(s.substring(s.lastIndexOf("(") + 1, s.lastIndexOf("s")));
					pl.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, effect_time * 20, effect_tier));
				}

				if(s.contains("ENERGY REGEN")){
					// Increased energy regen.
					final int bonus_percent = Integer.parseInt(s.substring(s.indexOf("+") + 1, s.indexOf("%")));
					int effect_time = Integer.parseInt(s.substring(s.lastIndexOf("(") + 1, s.lastIndexOf("s")));

					fish_energy_regen.put(pl.getName(), bonus_percent);
					RealmMechanics.playPotionEffect(pl, (LivingEntity)pl, 0xa47c48, (effect_time * 20));
					pl.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "      " + ChatColor.GREEN + (int)bonus_percent + ChatColor.BOLD + " Energy/s" + ChatColor.GREEN + " FROM " + is.getItemMeta().getDisplayName() + ChatColor.GRAY + " [" + effect_time + "s]");


					Main.plugin.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
						public void run() {
							fish_energy_regen.remove(pl.getName());
							pl.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + " +" + bonus_percent + "% Energy " + ChatColor.RED + "FROM " + is.getItemMeta().getDisplayName() + ChatColor.RED + " " + ChatColor.UNDERLINE + "EXPIRED");
						}
					}, effect_time * 20L);
				}

				if(s.contains("% DMG")){
					final int bonus_percent = Integer.parseInt(s.substring(s.indexOf("+") + 1, s.indexOf("%")));
					int effect_time = Integer.parseInt(s.substring(s.lastIndexOf("(") + 1, s.lastIndexOf("s")));

					fish_bonus_dmg.put(pl.getName(), bonus_percent);
					RealmMechanics.playPotionEffect(pl, (LivingEntity)pl, 0xdd0000, (effect_time * 20));
					pl.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "      +" + ChatColor.GREEN + (int)bonus_percent + ChatColor.BOLD + "% DMG" + ChatColor.GREEN + " FROM " + is.getItemMeta().getDisplayName() + ChatColor.GRAY + " [" + effect_time + "s]");


					Main.plugin.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
						public void run() {
							fish_bonus_dmg.remove(pl.getName());
							pl.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + " +" + bonus_percent + "% DMG " + ChatColor.RED + "FROM " + is.getItemMeta().getDisplayName() + ChatColor.RED + " " + ChatColor.UNDERLINE + "EXPIRED");
						}
					}, effect_time * 20L);
				}

				if(s.contains("% ARMOR")){
					final int bonus_percent = Integer.parseInt(s.substring(s.indexOf("+") + 1, s.indexOf("%")));
					int effect_time = Integer.parseInt(s.substring(s.lastIndexOf("(") + 1, s.lastIndexOf("s")));

					fish_bonus_armor.put(pl.getName(), bonus_percent);
					RealmMechanics.playPotionEffect(pl, (LivingEntity)pl, 0xacacac, (effect_time * 20));
					pl.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "+" + ChatColor.GREEN + (int)bonus_percent + ChatColor.BOLD + "% ARMOR" + ChatColor.GREEN + " FROM " + is.getItemMeta().getDisplayName() + ChatColor.GRAY + " [" + effect_time + "s]");


					Main.plugin.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
						public void run() {
							fish_bonus_armor.remove(pl.getName());
							pl.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "+" + bonus_percent + "% ARMOR " + ChatColor.RED + "FROM " + is.getItemMeta().getDisplayName() + ChatColor.RED + " " + ChatColor.UNDERLINE + "EXPIRED");
						}
					}, effect_time * 20L);
				}

				if(s.contains("% BLOCK")){
					final int bonus_percent = Integer.parseInt(s.substring(s.indexOf("+") + 1, s.indexOf("%")));
					int effect_time = Integer.parseInt(s.substring(s.lastIndexOf("(") + 1, s.lastIndexOf("s")));

					fish_bonus_block.put(pl.getName(), bonus_percent);
					RealmMechanics.playPotionEffect(pl, (LivingEntity)pl, 0x014421, (effect_time * 20));
					pl.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "+" + ChatColor.GREEN + (int)bonus_percent + ChatColor.BOLD + "% BLOCK" + ChatColor.GREEN + " FROM " + is.getItemMeta().getDisplayName() + ChatColor.GRAY + " [" + effect_time + "s]");


					Main.plugin.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
						public void run() {
							fish_bonus_block.remove(pl.getName());
							pl.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "+" + bonus_percent + "% BLOCK " + ChatColor.RED + "FROM " + is.getItemMeta().getDisplayName() + ChatColor.RED + " " + ChatColor.UNDERLINE + "EXPIRED");
						}
					}, effect_time * 20L);
				}

				if(s.contains("% LIFESTEAL")){
					final int bonus_percent = Integer.parseInt(s.substring(s.indexOf("+") + 1, s.indexOf("%")));
					int effect_time = Integer.parseInt(s.substring(s.lastIndexOf("(") + 1, s.lastIndexOf("s")));

					fish_bonus_lifesteal.put(pl.getName(), bonus_percent);
					RealmMechanics.playPotionEffect(pl, (LivingEntity)pl, 0x4d2177, (effect_time * 20));
					pl.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "+" + ChatColor.GREEN + (int)bonus_percent + ChatColor.BOLD + "% LIFESTEAL" + ChatColor.GREEN + " FROM " + is.getItemMeta().getDisplayName() + ChatColor.GRAY + " [" + effect_time + "s]");


					Main.plugin.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
						public void run() {
							fish_bonus_lifesteal.remove(pl.getName());
							pl.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "+" + bonus_percent + "% LIFESTEAL " + ChatColor.RED + "FROM " + is.getItemMeta().getDisplayName() + ChatColor.RED + " " + ChatColor.UNDERLINE + "EXPIRED");
						}
					}, effect_time * 20L);
				}

				if(s.contains("% CRIT")){
					final int bonus_percent = Integer.parseInt(s.substring(s.indexOf("+") + 1, s.indexOf("%")));
					int effect_time = Integer.parseInt(s.substring(s.lastIndexOf("(") + 1, s.lastIndexOf("s")));

					fish_bonus_critical_hit.put(pl.getName(), bonus_percent);
					RealmMechanics.playPotionEffect(pl, (LivingEntity)pl, 0xe52d00, (effect_time * 20));
					pl.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "+" + ChatColor.GREEN + (int)bonus_percent + ChatColor.BOLD + "% CRIT" + ChatColor.GREEN + " FROM " + is.getItemMeta().getDisplayName() + ChatColor.GRAY + " [" + effect_time + "s]");


					Main.plugin.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
						public void run() {
							fish_bonus_critical_hit.remove(pl.getName());
							pl.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "+" + bonus_percent + "% CRIT " + ChatColor.RED + "FROM " + is.getItemMeta().getDisplayName() + ChatColor.RED + " " + ChatColor.UNDERLINE + "EXPIRED");
						}
					}, effect_time * 20L);
				}
			}

		}
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e){
		final Player pl = e.getPlayer();
		Main.plugin.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
			public void run() {
				if(profession_buff == true){
					int minutes_left = (int)(((profession_buff_timeout - System.currentTimeMillis()) / 1000.0D) / 60.0D);
					if(pl != null){
						pl.sendMessage("");
						pl.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + ">> " + ChatColor.UNDERLINE + "+20% Global EXP Rates" + ChatColor.GOLD + " is active for " + ChatColor.UNDERLINE + minutes_left + ChatColor.RESET + ChatColor.GOLD + " more minute(s)!");
						pl.sendMessage("");
					}
				}
			}
		}, 40L);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerInteract(PlayerInteractEvent e){
		final Player pl = e.getPlayer();
		if(e.hasItem() && !DuelMechanics.duel_map.containsKey(pl.getName()) && (e.getAction() == Action.RIGHT_CLICK_AIR || (HealthMechanics.in_combat.containsKey(pl.getName()) && e.getAction() == Action.RIGHT_CLICK_BLOCK))){
			final ItemStack is = e.getItem();
			if(isCustomFish(is) && pl.getFoodLevel() >= 20){
				e.setUseInteractedBlock(Result.DENY);

				pl.getWorld().playSound(pl.getLocation(), Sound.EAT, 1F, 1F);
				Main.plugin.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
					@SuppressWarnings("deprecation")
					public void run() {
						// TODO: This could be abused with a macro.
						if(isCustomFish(pl.getItemInHand())){
							ItemStack fish = pl.getItemInHand();
							if(fish.getAmount() > 1){
								// Subtract just 1.
								fish.setAmount(fish.getAmount() - 1);
								pl.setItemInHand(fish);
							}
							else if(fish.getAmount() <= 1){
								pl.setItemInHand(new ItemStack(Material.AIR));
							}

							pl.updateInventory();
						}
					}
				}, 1L);
				Main.plugin.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
					public void run() {
						pl.getWorld().playSound(pl.getLocation(), Sound.EAT, 1F, 1.5F);
					}
				}, 4L);
				// Ok, so now we need to see what we need to do for the player when they eat this. Let's handle the food aspect first.
				List<String> lore = is.getItemMeta().getLore();
				int food_to_restore = 0;
				for(String s : lore){
					if(s.contains("% HUNGER")){
						double percent = Integer.parseInt(s.substring(s.indexOf("-") + 1, s.indexOf("%")));
						int local_amount = (int)((percent / 100.0D) * 20D);
						food_to_restore += local_amount;
					}
				}

				int cur_food = pl.getFoodLevel();
				if(cur_food + food_to_restore >= 20){
					pl.setFoodLevel(20);
					pl.setSaturation(20);
				}
				else{
					pl.setFoodLevel(cur_food + food_to_restore);
					pl.setSaturation(pl.getSaturation() + food_to_restore);
				}

				// Ok, food handled, now let's handle any misc. buffs that the fish might have.
				for(String s : lore){
					s = ChatColor.stripColor(s);
					if(s.contains("% HP (instant)")){
						// Instant heal.
						double percent_to_heal = Double.parseDouble(s.substring(s.indexOf("+") + 1, s.indexOf("%"))) / 100;
						double max_hp = HealthMechanics.getMaxHealthValue(pl.getName());
						int amount_to_heal = (int)Math.round((percent_to_heal * max_hp));
						double current_hp = HealthMechanics.getPlayerHP(pl.getName());
						if(current_hp + 1 > max_hp){continue;} // They have max HP.
						//amount_to_heal += getHealthRegenAmount(p);

						if(CommunityMechanics.toggle_list.containsKey(pl.getName()) && CommunityMechanics.toggle_list.get(pl.getName()).contains("debug")){
							pl.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "+" + ChatColor.GREEN + (int)amount_to_heal + ChatColor.BOLD + " HP" + ChatColor.GREEN + " FROM " + is.getItemMeta().getDisplayName() + ChatColor.GRAY + " [" + ((int)current_hp + (int)amount_to_heal) + "/" + (int)max_hp + "HP]");
						}

						if((current_hp + amount_to_heal) >= max_hp){
							pl.setHealth(20);
							HealthMechanics.setPlayerHP(pl.getName(), (int)max_hp);
							continue; // Full HP.
						}

						else if(pl.getHealth() <= 19 && ((current_hp + amount_to_heal) < max_hp)){
							HealthMechanics.setPlayerHP(pl.getName(), (int)(HealthMechanics.getPlayerHP(pl.getName()) + amount_to_heal));
							double health_percent = (HealthMechanics.getPlayerHP(pl.getName()) + amount_to_heal) / max_hp;
							double new_health_display = health_percent * 20;
							if(new_health_display > 19){
								if(health_percent >= 1){
									new_health_display = 20;
								}
								else if(health_percent < 1){
									new_health_display = 19;
								}
							}
							if(new_health_display < 1){
								new_health_display = 1;
							}
							pl.setHealth((int)new_health_display);

						}
					}

					if(s.startsWith("REGEN")){
						// Regen % of HP over X seconds.
						double percent_to_regen = Double.parseDouble(s.substring(s.indexOf(" ") + 1, s.indexOf("%"))) / 100.0D;
						int regen_interval = Integer.parseInt(s.substring(s.lastIndexOf(" ") + 1, s.lastIndexOf("s")));
						double max_hp = HealthMechanics.getMaxHealthValue(pl.getName());

						final int amount_to_regen_per_interval = (int)(max_hp * percent_to_regen) / regen_interval;
						pl.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "      " + ChatColor.GREEN + (int)amount_to_regen_per_interval + ChatColor.BOLD + " HP/s" + ChatColor.GREEN + " FROM " + is.getItemMeta().getDisplayName() + ChatColor.GRAY + " [" + regen_interval + "s]");

						fish_health_regen.put(pl.getName(), amount_to_regen_per_interval);
						pl.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, (int) (regen_interval + (regen_interval * 0.25)), 1));
						Main.plugin.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
							public void run() {
								pl.removePotionEffect(PotionEffectType.REGENERATION);
								fish_health_regen.remove(pl.getName());
								pl.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "   " + amount_to_regen_per_interval + " HP/s " + ChatColor.RED + "FROM " + is.getItemMeta().getDisplayName() + ChatColor.RED + " " + ChatColor.UNDERLINE + "EXPIRED");
							}
						}, regen_interval * 20L);
					}

					if(s.startsWith("SPEED")){
						// Speed effect.
						String tier_symbol = s.substring(s.indexOf("(") + 1, s.indexOf(")"));
						int effect_tier = 0;
						if(tier_symbol.equalsIgnoreCase("I")){
							effect_tier = 1;
						}
						if(tier_symbol.equalsIgnoreCase("II")){
							effect_tier = 2;
						}

						int effect_time = Integer.parseInt(s.substring(s.lastIndexOf("(") + 1, s.lastIndexOf("s")));
						pl.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, effect_time * 20, effect_tier));
					}

					if(s.startsWith("NIGHTVISION")){
						// Night vision effect
						String tier_symbol = s.substring(s.indexOf("(") + 1, s.indexOf(")"));
						int effect_tier = 0;
						if(tier_symbol.equalsIgnoreCase("I")){
							effect_tier = 1;
						}
						if(tier_symbol.equalsIgnoreCase("II")){
							effect_tier = 2;
						}

						int effect_time = Integer.parseInt(s.substring(s.lastIndexOf("(") + 1, s.lastIndexOf("s")));
						pl.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, effect_time * 20, effect_tier));
					}

					if(s.contains("ENERGY REGEN")){
						// Increased energy regen.
						final int bonus_percent = Integer.parseInt(s.substring(s.indexOf("+") + 1, s.indexOf("%")));
						int effect_time = Integer.parseInt(s.substring(s.lastIndexOf("(") + 1, s.lastIndexOf("s")));

						fish_energy_regen.put(pl.getName(), bonus_percent);
						RealmMechanics.playPotionEffect(pl, (LivingEntity)pl, 0xa47c48, (effect_time * 20));
						pl.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "      " + ChatColor.GREEN + (int)bonus_percent + ChatColor.BOLD + " Energy/s" + ChatColor.GREEN + " FROM " + is.getItemMeta().getDisplayName() + ChatColor.GRAY + " [" + effect_time + "s]");


						Main.plugin.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
							public void run() {
								fish_energy_regen.remove(pl.getName());
								pl.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + " +" + bonus_percent + "% Energy " + ChatColor.RED + "FROM " + is.getItemMeta().getDisplayName() + ChatColor.RED + " " + ChatColor.UNDERLINE + "EXPIRED");
							}
						}, effect_time * 20L);
					}

					if(s.contains("% DMG")){
						final int bonus_percent = Integer.parseInt(s.substring(s.indexOf("+") + 1, s.indexOf("%")));
						int effect_time = Integer.parseInt(s.substring(s.lastIndexOf("(") + 1, s.lastIndexOf("s")));

						fish_bonus_dmg.put(pl.getName(), bonus_percent);
						RealmMechanics.playPotionEffect(pl, (LivingEntity)pl, 0xdd0000, (effect_time * 20));
						pl.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "+" + ChatColor.GREEN + (int)bonus_percent + ChatColor.BOLD + "% DMG" + ChatColor.GREEN + " FROM " + is.getItemMeta().getDisplayName() + ChatColor.GRAY + " [" + effect_time + "s]");


						Main.plugin.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
							public void run() {
								fish_bonus_dmg.remove(pl.getName());
								pl.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "+" + bonus_percent + "% DMG " + ChatColor.RED + "FROM " + is.getItemMeta().getDisplayName() + ChatColor.RED + " " + ChatColor.UNDERLINE + "EXPIRED");
							}
						}, effect_time * 20L);
					}

					if(s.contains("% ARMOR")){
						final int bonus_percent = Integer.parseInt(s.substring(s.indexOf("+") + 1, s.indexOf("%")));
						int effect_time = Integer.parseInt(s.substring(s.lastIndexOf("(") + 1, s.lastIndexOf("s")));

						fish_bonus_armor.put(pl.getName(), bonus_percent);
						RealmMechanics.playPotionEffect(pl, (LivingEntity)pl, 0xacacac, (effect_time * 20));
						pl.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "+" + ChatColor.GREEN + (int)bonus_percent + ChatColor.BOLD + "% ARMOR" + ChatColor.GREEN + " FROM " + is.getItemMeta().getDisplayName() + ChatColor.GRAY + " [" + effect_time + "s]");


						Main.plugin.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
							public void run() {
								fish_bonus_armor.remove(pl.getName());
								pl.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "+" + bonus_percent + "% ARMOR " + ChatColor.RED + "FROM " + is.getItemMeta().getDisplayName() + ChatColor.RED + " " + ChatColor.UNDERLINE + "EXPIRED");
							}
						}, effect_time * 20L);
					}

					if(s.contains("% BLOCK")){
						final int bonus_percent = Integer.parseInt(s.substring(s.indexOf("+") + 1, s.indexOf("%")));
						int effect_time = Integer.parseInt(s.substring(s.lastIndexOf("(") + 1, s.lastIndexOf("s")));

						fish_bonus_block.put(pl.getName(), bonus_percent);
						RealmMechanics.playPotionEffect(pl, (LivingEntity)pl, 0x014421, (effect_time * 20));
						pl.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "+" + ChatColor.GREEN + (int)bonus_percent + ChatColor.BOLD + "% BLOCK" + ChatColor.GREEN + " FROM " + is.getItemMeta().getDisplayName() + ChatColor.GRAY + " [" + effect_time + "s]");


						Main.plugin.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
							public void run() {
								fish_bonus_block.remove(pl.getName());
								pl.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "+" + bonus_percent + "% BLOCK " + ChatColor.RED + "FROM " + is.getItemMeta().getDisplayName() + ChatColor.RED + " " + ChatColor.UNDERLINE + "EXPIRED");
							}
						}, effect_time * 20L);
					}

					if(s.contains("% LIFESTEAL")){
						final int bonus_percent = Integer.parseInt(s.substring(s.indexOf("+") + 1, s.indexOf("%")));
						int effect_time = Integer.parseInt(s.substring(s.lastIndexOf("(") + 1, s.lastIndexOf("s")));

						fish_bonus_lifesteal.put(pl.getName(), bonus_percent);
						RealmMechanics.playPotionEffect(pl, (LivingEntity)pl, 0x4d2177, (effect_time * 20));
						pl.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "+" + ChatColor.GREEN + (int)bonus_percent + ChatColor.BOLD + "% LIFESTEAL" + ChatColor.GREEN + " FROM " + is.getItemMeta().getDisplayName() + ChatColor.GRAY + " [" + effect_time + "s]");


						Main.plugin.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
							public void run() {
								fish_bonus_lifesteal.remove(pl.getName());
								pl.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "+" + bonus_percent + "% LIFESTEAL " + ChatColor.RED + "FROM " + is.getItemMeta().getDisplayName() + ChatColor.RED + " " + ChatColor.UNDERLINE + "EXPIRED");
							}
						}, effect_time * 20L);
					}

					if(s.contains("% CRIT")){
						final int bonus_percent = Integer.parseInt(s.substring(s.indexOf("+") + 1, s.indexOf("%")));
						int effect_time = Integer.parseInt(s.substring(s.lastIndexOf("(") + 1, s.lastIndexOf("s")));

						fish_bonus_critical_hit.put(pl.getName(), bonus_percent);
						RealmMechanics.playPotionEffect(pl, (LivingEntity)pl, 0xe52d00, (effect_time * 20));
						pl.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "+" + ChatColor.GREEN + (int)bonus_percent + ChatColor.BOLD + "% CRIT" + ChatColor.GREEN + " FROM " + is.getItemMeta().getDisplayName() + ChatColor.GRAY + " [" + effect_time + "s]");


						Main.plugin.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
							public void run() {
								fish_bonus_critical_hit.remove(pl.getName());
								pl.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "+" + bonus_percent + "% CRIT " + ChatColor.RED + "FROM " + is.getItemMeta().getDisplayName() + ChatColor.RED + " " + ChatColor.UNDERLINE + "EXPIRED");
							}
						}, effect_time * 20L);
					}
				}

			}
		}
	}

	@EventHandler
	public void onEntityRegainHealth(EntityRegainHealthEvent e){
		if(e.getEntity() instanceof Player && e.getRegainReason() == RegainReason.MAGIC_REGEN){
			Player pl = (Player)e.getEntity();
			if(fish_health_regen.containsKey(pl.getName())){
				e.setCancelled(true);

				int amount_to_regen = fish_health_regen.get(pl.getName());

				double max_hp = HealthMechanics.getMaxHealthValue(pl.getName());
				int amount_to_heal = (int)amount_to_regen;
				double current_hp = HealthMechanics.getPlayerHP(pl.getName());
				if(current_hp + 1 > max_hp){return;} // They have max HP.
				//amount_to_heal += getHealthRegenAmount(p);

				if(CommunityMechanics.toggle_list.containsKey(pl.getName()) && CommunityMechanics.toggle_list.get(pl.getName()).contains("debug")){
					pl.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "          +" + ChatColor.GREEN + (int)amount_to_heal + ChatColor.BOLD + " HP" + ChatColor.GRAY + " [" + ((int)current_hp + (int)amount_to_heal) + "/" + (int)max_hp + "HP]");
				}

				if((current_hp + amount_to_heal) >= max_hp){
					pl.setHealth(20);
					HealthMechanics.setPlayerHP(pl.getName(), (int)max_hp);
					return; // Full HP.
				}

				else if(pl.getHealth() <= 19 && ((current_hp + amount_to_heal) < max_hp)){
					HealthMechanics.setPlayerHP(pl.getName(), (int)(HealthMechanics.getPlayerHP(pl.getName()) + amount_to_heal));
					//HealthMechanics.setPlayerHP(pl.getName(), (int)(HealthMechanics.getPlayerHP(pl.getName()) + amount_to_heal));
					double health_percent = (HealthMechanics.getPlayerHP(pl.getName()) + amount_to_heal) / max_hp;
					double new_health_display = health_percent * 20;
					if(new_health_display > 19){
						if(health_percent >= 1){
							new_health_display = 20;
						}
						else if(health_percent < 1){
							new_health_display = 19;
						}
					}
					if(new_health_display < 1){
						new_health_display = 1;
					}
					pl.setHealth((int)new_health_display);
				}
			}
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = false)
	public void onPlayerCastRod(PlayerInteractEvent e){
		// TODO: Prevent fishing in no-fish / too high of tier areas.
		Player pl = e.getPlayer();
		if(RepairMechanics.repair_map.containsKey(pl.getName()) || (e.hasBlock() && e.getClickedBlock().getType() == Material.ANVIL)){
			return; // Do nothing, they're repairing the stupid fishingrod.
		}

		if(e.hasItem() && e.getItem().getType() == Material.FISHING_ROD && (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK)){
			ItemStack is = e.getItem();
			if(!isSkillItem(is)){
				e.setCancelled(true);
				e.setUseItemInHand(Result.DENY);
				pl.sendMessage(ChatColor.RED + "You cannot fish with this type of fishing rod.");
				pl.sendMessage(ChatColor.GRAY + "You'll need to purchase a Novice Fishingrod from a " + ChatColor.UNDERLINE + "Skill Trainer.");
				return;
				// Do not allow fishing with incorrect types of fishing rods.
			}

			Location target_loc = pl.getTargetBlock(null, 8).getLocation();

			int spot_tier = getFishingSpotTier(target_loc);
			if(spot_tier == -1){
				// No spot nearby.
				pl.sendMessage(ChatColor.RED + "There are " + ChatColor.UNDERLINE + "no" + ChatColor.RED + " populated fishing spots near this location.");
				pl.sendMessage(ChatColor.GRAY + "Look for particles above water blocks to signify active fishing spots.");
				e.setCancelled(true);
				e.setUseItemInHand(Result.DENY);
				e.setUseInteractedBlock(Result.DENY);
				return;
			}

			int fishing_rod_level = getItemLevel(is);
			if(fishing_rod_level < spot_tier){
				int level_req = 0;
				if(spot_tier == 2){
					level_req = 20;
				}
				if(spot_tier == 3){
					level_req = 40;
				}
				if(spot_tier == 4){
					level_req = 60;
				}
				if(spot_tier == 5){
					level_req = 80;
				}
				pl.sendMessage(ChatColor.RED + "You are " + ChatColor.UNDERLINE + "not" + ChatColor.RED + " an experienced enough fisher to fish in this location.");
				pl.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + "REQ:" + ChatColor.GRAY + " LVL " + level_req + "+ Fishingrod");
				e.setCancelled(true);
				e.setUseItemInHand(Result.DENY);
				return;
			}

			Location fishing_loc = getFishingSpot(target_loc);
			player_fishing_spot.put(pl.getName(), fishing_loc);		
		}
	}

	public int getNextLevelUp(int tier){
		if(tier == 1){
			return 20;
		}
		if(tier == 2){
			return 40;
		}
		if(tier == 3){
			return 60;
		}
		if(tier == 4){
			return 80;
		}
		if(tier == 5){
			return 100;
		}
		return -1;
	}

	@EventHandler
	public void onPlayerFish(PlayerFishEvent e){
		final Player pl = e.getPlayer();
		e.setExpToDrop(0);

		if(!(player_fishing_spot.containsKey(pl.getName())) || !(isSkillItem(pl.getItemInHand())) || !(getSkillType(pl.getItemInHand()).equalsIgnoreCase("fishing"))){
			e.setCancelled(true);
			return; // Get out of here.
		}

		if(e.getState() == State.FAILED_ATTEMPT || e.getState() == State.CAUGHT_ENTITY){
			RepairMechanics.subtractCustomDurability(pl, pl.getItemInHand(), 1, "wep");
		}
		else if(e.getState() == State.CAUGHT_FISH){
			RepairMechanics.subtractCustomDurability(pl, pl.getItemInHand(), 2, "wep");
		}

		if(e.getState() == State.CAUGHT_FISH){
			e.setCancelled(true);

			// TODO: Generate random fish based off spot.
			final Location fish_loc = player_fishing_spot.get(pl.getName());
			final int spot_tier = fishing_location.get(fish_loc);
			// TODO: Change fish_count of location stored in player_fishing_spot, subtract 1 for each attempt.

			int lfish_count = fish_count.get(fish_loc);
			lfish_count--;
			fish_count.put(fish_loc, lfish_count);

			if(lfish_count <= 0){
				// No more fish!
				fishing_respawn.put(fish_loc, System.currentTimeMillis());
			}

			pl.sendMessage(ChatColor.GRAY + "You examine your catch... ");
			Main.plugin.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
				public void run() {
					int do_i_get_fish = new Random().nextInt(100);

					int item_tier = getItemTier(pl.getItemInHand());
					int success_rate = 0;

					if(item_tier > spot_tier){
						success_rate = 100;
					}
					if(item_tier == spot_tier){
						success_rate = 50 + (2 * (20 - Math.abs((getNextLevelUp(item_tier) - getItemLevel(pl.getItemInHand())))));
					}

					int success_mod = getSuccessChance(pl.getItemInHand());
					success_rate += success_mod; // %CHANCE

					if(isSkillItem(pl.getItemInHand()) && getSkillType(pl.getItemInHand()).equalsIgnoreCase("fishing") && success_rate >= do_i_get_fish){
						// They get fish!
						ItemStack fish = getFishDrop(spot_tier);
						if(pl.getInventory().firstEmpty() != -1){
							pl.getInventory().setItem(pl.getInventory().firstEmpty(), fish);
						}
						else{
							// Full inventory!
							pl.getWorld().dropItem(pl.getLocation(), fish);
						}
						pl.sendMessage(ChatColor.GREEN + "... you caught some " + fish.getItemMeta().getDisplayName() + ChatColor.GREEN + "!");

						// Special Effects!
						int doi_double_drop = new Random().nextInt(100) + 1;
						if(getDoubleDropChance(pl.getItemInHand()) >= doi_double_drop){
							fish = getFishDrop(spot_tier);
							if(pl.getInventory().firstEmpty() != -1){
								pl.getInventory().setItem(pl.getInventory().firstEmpty(), fish);
							}
							else{
								// Full inventory!
								pl.getWorld().dropItem(pl.getLocation(), fish);
							}
							if(CommunityMechanics.toggle_list.containsKey(pl.getName()) && CommunityMechanics.toggle_list.get(pl.getName()).contains("debug")){
								pl.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + "          DOUBLE FISH CATCH" + ChatColor.YELLOW + " (2x)");
							}
						}

						int doi_triple_drop = new Random().nextInt(100) + 1;
						if(getTripleDropChance(pl.getItemInHand()) >= doi_triple_drop){
							fish = getFishDrop(spot_tier);
							if(pl.getInventory().firstEmpty() != -1){
								pl.getInventory().setItem(pl.getInventory().firstEmpty(), fish);
							}
							else{
								// Full inventory!
								pl.getWorld().dropItem(pl.getLocation(), fish);
							}

							fish = getFishDrop(spot_tier);
							if(pl.getInventory().firstEmpty() != -1){
								pl.getInventory().setItem(pl.getInventory().firstEmpty(), fish);
							}
							else{
								// Full inventory!
								pl.getWorld().dropItem(pl.getLocation(), fish);
							}
							if(CommunityMechanics.toggle_list.containsKey(pl.getName()) && CommunityMechanics.toggle_list.get(pl.getName()).contains("debug")){
								pl.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + "          TRIPLE FISH CATCH" + ChatColor.YELLOW + " (3x)");
							}
						}

						int junk_chance = getJunkFindChance(pl.getItemInHand());
						if(junk_chance >= (new Random().nextInt(100) + 1)){
							int junk_type = new Random().nextInt(100) + 1; // 0, 1, 2
							ItemStack junk = null;

							if(junk_type <= 70){
								if(spot_tier == 1){
									junk = ItemMechanics.t1_arrow;
									junk.setAmount(42 + new Random().nextInt(7));
								}
								if(spot_tier == 2){
									junk = ItemMechanics.t2_arrow;
									junk.setAmount(27 + new Random().nextInt(7));
								}
								if(spot_tier == 3){
									junk = ItemMechanics.t3_arrow;
									junk.setAmount(26 + new Random().nextInt(7));
								}
								if(spot_tier == 4){
									junk = ItemMechanics.t4_arrow;
									junk.setAmount(24 + new Random().nextInt(7));
								}
								if(spot_tier == 5){
									junk = ItemMechanics.t5_arrow;
									junk.setAmount(21 + new Random().nextInt(7));
								}
							}

							if(junk_type > 70 && junk_type < 95){
								if(spot_tier == 1){
									junk = MerchantMechanics.t1_pot;
									junk.setAmount(5 + new Random().nextInt(3));
								}
								if(spot_tier == 2){
									junk = MerchantMechanics.t2_pot;
									junk.setAmount(4 + new Random().nextInt(3));
								}
								if(spot_tier == 3){
									junk = MerchantMechanics.t3_pot;
									junk.setAmount(2 + new Random().nextInt(3));
								}
								if(spot_tier == 4){
									junk = MerchantMechanics.t4_pot;
									junk.setAmount(1 + new Random().nextInt(3));
								}
								if(spot_tier == 5){
									junk = MerchantMechanics.t5_pot;
									junk.setAmount(1 + new Random().nextInt(3));
								}
							}

							if(junk_type >= 95){
								if(spot_tier == 1){
									junk = MerchantMechanics.T1_scrap;
									junk.setAmount(20 + new Random().nextInt(7));
								}
								if(spot_tier == 2){
									junk = MerchantMechanics.T2_scrap;
									junk.setAmount(15 + new Random().nextInt(7));
								}
								if(spot_tier == 3){
									junk = MerchantMechanics.T3_scrap;
									junk.setAmount(10 + new Random().nextInt(7));
								}
								if(spot_tier == 4){
									junk = MerchantMechanics.T4_scrap;
									junk.setAmount(5 + new Random().nextInt(7));
								}
								if(spot_tier == 5){
									junk = MerchantMechanics.T5_scrap;
									junk.setAmount(2 + new Random().nextInt(6));
								}
							}

							if(junk != null){
								int item_count = junk.getAmount();
								if(junk.getType() == Material.POTION){
									// Not stackable.
									int amount = junk.getAmount();
									ItemStack single_junk = junk;
									single_junk.setAmount(1);
									while(amount > 0){
										amount--;
										if(pl.getInventory().firstEmpty() != -1){
											pl.getInventory().setItem(pl.getInventory().firstEmpty(), single_junk);
										}
										else{
											// Full inventory!
											pl.getWorld().dropItem(pl.getLocation(), single_junk);
										}
									}
								}
								else{
									if(pl.getInventory().firstEmpty() != -1){
										pl.getInventory().setItem(pl.getInventory().firstEmpty(), junk);
									}
									else{
										// Full inventory!
										pl.getWorld().dropItem(pl.getLocation(), junk);
									}
								}

								pl.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + "  YOU FOUND SOME JUNK! -- " + item_count + "x " + junk.getItemMeta().getDisplayName());
							}
						}

						int treasure_chance = getTreasureFindChance(pl.getItemInHand());
						if(treasure_chance >= (new Random().nextInt(300) + 1)){
							// Give em treasure!
							int treasure_type = new Random().nextInt(3); // 0, 1
							ItemStack treasure = null;
							if(treasure_type == 0){
								// OOA
								treasure = CraftItemStack.asCraftCopy(MerchantMechanics.orb_of_alteration);
							}
							if(treasure_type == 1){
								// OOF
								treasure = CraftItemStack.asCraftCopy(ItemMechanics.orb_of_flight);
							}
							if(treasure_type == 2){
								// OOP
								treasure = CraftItemStack.asCraftCopy(ItemMechanics.orb_of_peace);
							}

							if(pl.getInventory().firstEmpty() != -1){
								pl.getInventory().setItem(pl.getInventory().firstEmpty(), treasure);
							}
							else{
								// Full inventory!
								pl.getWorld().dropItem(pl.getLocation(), treasure);
							}

							pl.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + "  YOU FOUND SOME TREASURE! -- a(n) " + treasure.getItemMeta().getDisplayName());
						}

						// TODO: Multiple fish catches = better chievs.

						if(fish_caught_count.containsKey(pl.getName())){
							int o_f = fish_caught_count.get(pl.getName());
							o_f++;
							fish_caught_count.put(pl.getName(), o_f);
						}
						else{
							fish_caught_count.put(pl.getName(), 1);
						}

						int fish_caught = fish_caught_count.get(pl.getName());
						if(fish_caught >= 1){
							AchievmentMechanics.addAchievment(pl.getName(), "Gone Fishing I");
							if(fish_caught >= 10){
								AchievmentMechanics.addAchievment(pl.getName(), "Gone Fishing II");
								if(fish_caught >= 25){
									AchievmentMechanics.addAchievment(pl.getName(), "Gone Fishing III");
									if(fish_caught >= 50){
										AchievmentMechanics.addAchievment(pl.getName(), "Gone Fishing IV");
										if(fish_caught >= 100){
											AchievmentMechanics.addAchievment(pl.getName(), "Gone Fishing V");
											if(fish_caught >= 200){
												AchievmentMechanics.addAchievment(pl.getName(), "Gone Fishing VI");
											}
										}
									}
								}
							}
						}


						addEXP(pl, pl.getItemInHand(), getFishEXP(spot_tier), "fishing");

					}
					else{
						// They don't get fish!
						pl.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "... and find nothing!");
					}
				}
			}, 10L);
		}
	}

	@EventHandler
	public void onPlayerOpenFurnace(InventoryOpenEvent e){
		if(e.getInventory().getName().equalsIgnoreCase("container.furnace")){
			// Open a custom instance of the furnace for the player.
			// And ignite it!
			e.setCancelled(true);
		}

		/*Player pl = (Player)e.getPlayer();
			if(ignoreFurnaceOpenEvent.contains(pl.getName())){
				return; // Anti-loop
			}

			e.setCancelled(true); // Don't open vanilla inventory.

			Block b = pl.getTargetBlock(transparent, 8);

			if(furnace_inventory.containsKey(pl.getName())){
				HashMap<Block, Furnace> instanced_furnaces = furnace_inventory.get(pl.getName());
				if(instanced_furnaces.containsKey(b)){
					Furnace f = instanced_furnaces.get(b);
					f.setBurnTime((short)32000);
					f.setCookTime((short)2);

					ignoreFurnaceOpenEvent.add(pl.getName());
					pl.openInventory(f.getInventory());
					ignoreFurnaceOpenEvent.remove(pl.getName());
					return;
				}
			}

			//Inventory furnace = Bukkit.createInventory(null, InventoryType.FURNACE);
			Furnace f = (Furnace)b.getState();
			f.setBurnTime((short)32000);
			f.setCookTime((short)2);

			if(furnace_inventory.containsKey(pl.getName())){
				HashMap<Block, Furnace> instanced_furnaces = furnace_inventory.get(pl.getName());
				instanced_furnaces.put(b, f);
				furnace_inventory.put(pl.getName(), instanced_furnaces);
			}
			else if(!(furnace_inventory.containsKey(pl.getName()))){
				HashMap<Block, Furnace> instanced_furnaces = new HashMap<Block, Furnace>();
				instanced_furnaces.put(b, f);
				furnace_inventory.put(pl.getName(), instanced_furnaces);
			}

			ignoreFurnaceOpenEvent.add(pl.getName());
			pl.openInventory(f.getInventory());
			ignoreFurnaceOpenEvent.remove(pl.getName());

		}*/
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onPlayerUseFire(PlayerInteractEvent e){
		Player pl = e.getPlayer();
		if(e.getAction() == Action.RIGHT_CLICK_BLOCK && e.hasItem() && e.getItem().getType() == Material.RAW_FISH){
			Block b = pl.getTargetBlock(null, 8);

			if(b.getType() == Material.FIRE || b.getType() == Material.LAVA || b.getType() == Material.STATIONARY_LAVA || b.getType() == Material.FURNACE || b.getType() == Material.BURNING_FURNACE){
				// Cook fish
				ItemStack raw_fish = e.getItem();
				if(!(isCustomRawFish(raw_fish))){
					return;
				}
				raw_fish.setType(Material.COOKED_FISH);
				ItemMeta im = raw_fish.getItemMeta();
				String display_name = im.getDisplayName();
				display_name = display_name.replaceAll("Raw", "Cooked");
				im.setDisplayName(display_name);
				raw_fish.setItemMeta(im);
				pl.setItemInHand(raw_fish);

				pl.getWorld().playSound(b.getLocation(), Sound.FIZZ, 0.5F, 0.05F);
				pl.updateInventory();
			}
			else{
				pl.sendMessage(ChatColor.YELLOW + "To cook, " + ChatColor.UNDERLINE + "RIGHT CLICK" + ChatColor.YELLOW + " any heat source.");
				pl.sendMessage(ChatColor.GRAY + "Ex. Fire, Lava, Furance");
				e.setUseItemInHand(Result.DENY);
				e.setUseInteractedBlock(Result.DENY);
			}
		}
		if(e.getAction() == Action.RIGHT_CLICK_AIR && e.hasItem() && e.getItem().getType() == Material.RAW_FISH){
			pl.sendMessage(ChatColor.YELLOW + "To cook, " + ChatColor.UNDERLINE + "RIGHT CLICK" + ChatColor.YELLOW + " any heat source.");
			pl.sendMessage(ChatColor.GRAY + "Ex. Fire, Lava, Furance");
			e.setUseItemInHand(Result.DENY);
			e.setUseInteractedBlock(Result.DENY);
		}
	}

	@EventHandler
	public void onFishCook(FurnaceSmeltEvent e){
		if(!(e.getResult().getType() == Material.COOKED_FISH)){
			e.setCancelled(true);
			return;
		}

		ItemStack raw_fish = e.getSource();
		raw_fish.setType(Material.COOKED_FISH);
		ItemMeta im = raw_fish.getItemMeta();
		String display_name = im.getDisplayName();
		display_name = display_name.replaceAll("Raw", "Cooked");
		im.setDisplayName(display_name);
		raw_fish.setItemMeta(im);
		e.setResult(raw_fish); // Now cooked!

	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onScrollUse(InventoryClickEvent e){
		if(e.getCursor() == null){return;}
		if(e.getCurrentItem() == null){return;}
		ItemStack cursor = e.getCursor();
		ItemStack in_slot = e.getCurrentItem();
		//boolean win = true;

		if(!e.getInventory().getName().equalsIgnoreCase("container.crafting")){return;}
		if(e.getInventory().getViewers().size() > 1){return;}
		if(e.getSlotType() == SlotType.ARMOR){return;}

		Player p = (Player)e.getWhoClicked();

		if(isSkillEnchantScroll(cursor) && isSkillItem(in_slot)){

			if(cursor.getAmount() == 1){
				e.setCancelled(true);
				e.setCursor(new ItemStack(Material.AIR));			
			}
			else if(cursor.getAmount() > 1){
				e.setCancelled(true);
				cursor.setAmount(cursor.getAmount() - 1);
				e.setCursor(cursor);
			}


			applySkillEnchant(in_slot, getItemEnchant(cursor));

			p.updateInventory();
			p.getWorld().playSound(p.getLocation(), Sound.LEVEL_UP, 1.0F, 1.25F);
			//p.getWorld().spawnParticle(p.getLocation().add(0, 8, 0), Particle.FIREWORKS_SPARK, 0.75F, 50);
			Firework fw = (Firework) p.getWorld().spawnEntity(p.getLocation(), EntityType.FIREWORK);
			FireworkMeta fwm = fw.getFireworkMeta();
			//Random r = new Random();   
			FireworkEffect effect = FireworkEffect.builder().flicker(false).withColor(Color.YELLOW).withFade(Color.YELLOW).with(Type.BURST).trail(true).build();
			fwm.addEffect(effect);
			fwm.setPower(0);
			fw.setFireworkMeta(fwm); 
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onPlayerAnimation(PlayerAnimationEvent e){
		Player pl = e.getPlayer();
		ItemStack is = pl.getItemInHand();

		if(!(pl.getWorld().getName().equalsIgnoreCase(RealmMechanics.main_world_name))){
			return; // No realm mining pls.
		}

		if(!(isSkillItem(is)) || !(getSkillType(is).equalsIgnoreCase("mining"))){
			return;
		}

		Block b = null;

		// TODO: This event really only needs to fire is slow_digging isn't on the player...
		try{
			b = pl.getTargetBlock(transparent, 8);
		} catch(Exception err){
			if(slow_mining.containsKey(pl.getName())){
				if(slow_mining.get(pl.getName()) == 4){
					e.setCancelled(true);
				}
			}
			return;
		}

		if(b == null){
			e.setCancelled(true);
			return;
		}

		if(b.getType() == Material.STONE){
			pl.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, Integer.MAX_VALUE, 4));
			//sendPotionPacket(pl, new PotionEffect(PotionEffectType.SLOW_DIGGING, Integer.MAX_VALUE, 4));
			last_swing.put(pl.getName(), System.currentTimeMillis());
			//slow_mining.put(pl.getName(), true);
			e.setCancelled(true);
			return;
		}

		if(b.getType() == Material.COAL_ORE || b.getType() == Material.EMERALD_ORE || b.getType() == Material.IRON_ORE || b.getType() == Material.DIAMOND_ORE || b.getType() == Material.GOLD_ORE){
			if((isSkillItem(is)) && (getSkillType(is).equalsIgnoreCase("mining"))){	
				int pickaxe_tier = getItemTier(is);
				int ore_tier = getOreTier(b.getType());

				if(pickaxe_tier < ore_tier){
					// Cannot mine. Too low level of pickaxe. 
					pl.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, Integer.MAX_VALUE, 4));
					//sendPotionPacket(pl, new PotionEffect(PotionEffectType.SLOW_DIGGING, Integer.MAX_VALUE, 4));
					//slow_mining.put(pl.getName(), true);
					last_swing.put(pl.getName(), System.currentTimeMillis());
					e.setCancelled(true);
					return;
				}

				if(slow_mining.containsKey(pl.getName())){
					return; // Do nothing, it's handled.
				}
				pl.removePotionEffect(PotionEffectType.SLOW_DIGGING);
				//removeMobEffect(pl, PotionEffectType.SLOW_DIGGING.getId());

				int diff = pickaxe_tier - ore_tier;

				if(diff >= 3){
					if(pickaxe_tier == 5){
						pl.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, Integer.MAX_VALUE, 1));
						//sendPotionPacket(pl, new PotionEffect(PotionEffectType.FAST_DIGGING, Integer.MAX_VALUE, 1));
					}
					else{
						pl.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, Integer.MAX_VALUE, 0));
						//sendPotionPacket(pl, new PotionEffect(PotionEffectType.FAST_DIGGING, Integer.MAX_VALUE, 0));
					}
					pl.removePotionEffect(PotionEffectType.SLOW_DIGGING);
					//removeMobEffect(pl, PotionEffectType.SLOW_DIGGING.getId());
					slow_mining.put(pl.getName(), 0);
					last_swing.put(pl.getName(), System.currentTimeMillis());
				}

				if(pickaxe_tier == 4 || pickaxe_tier == 5){
					if(pickaxe_tier == 4){
						if(diff == 0){ // Diamond ore.
							pl.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, Integer.MAX_VALUE, 3));
							//sendPotionPacket(pl, new PotionEffect(PotionEffectType.SLOW_DIGGING, Integer.MAX_VALUE, 3));
							slow_mining.put(pl.getName(), 3);
						}
						if(diff == 1){ // Iron ore.
							pl.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, Integer.MAX_VALUE, 3));
							//sendPotionPacket(pl, new PotionEffect(PotionEffectType.SLOW_DIGGING, Integer.MAX_VALUE, 3));
							slow_mining.put(pl.getName(), 3);
						}
					}
					if(pickaxe_tier == 5){
						if(diff == 0){ // Gold ore.
							pl.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, Integer.MAX_VALUE, 3));
							//sendPotionPacket(pl, new PotionEffect(PotionEffectType.SLOW_DIGGING, Integer.MAX_VALUE, 3));
							slow_mining.put(pl.getName(), 3);
						}
						if(diff == 1){ // Diamond ore.
							pl.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, Integer.MAX_VALUE, 2));
							//sendPotionPacket(pl, new PotionEffect(PotionEffectType.SLOW_DIGGING, Integer.MAX_VALUE, 2));
							slow_mining.put(pl.getName(), 1);
						}
					}
				}
				else{
					if(diff == 0){
						// Lvl 2 debuff
						if(pickaxe_tier == 2){
							pl.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, Integer.MAX_VALUE, 1));
							//sendPotionPacket(pl, new PotionEffect(PotionEffectType.SLOW_DIGGING, Integer.MAX_VALUE, 1));
						}
						else{
							pl.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, Integer.MAX_VALUE, 2));
							//sendPotionPacket(pl, new PotionEffect(PotionEffectType.SLOW_DIGGING, Integer.MAX_VALUE, 2));
						}
						slow_mining.put(pl.getName(), 2);
					}
					if(diff == 1){
						// Lvl 1 debuff
						pl.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, Integer.MAX_VALUE, 1));
						//sendPotionPacket(pl, new PotionEffect(PotionEffectType.SLOW_DIGGING, Integer.MAX_VALUE, 1));
						slow_mining.put(pl.getName(), 1);
					}
					if(diff == 2){
						// Lvl 0 debuff
						pl.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, Integer.MAX_VALUE, 0));
						//sendPotionPacket(pl, new PotionEffect(PotionEffectType.SLOW_DIGGING, Integer.MAX_VALUE, 0));
						slow_mining.put(pl.getName(), 0);
					}
				}

				if(diff > 2){
					last_swing.remove(pl.getName());
					slow_mining.remove(pl.getName());

					//removeMobEffect(pl, PotionEffectType.SLOW_DIGGING.getId());
					pl.removePotionEffect(PotionEffectType.SLOW_DIGGING);
					//removeMobEffect(pl, PotionEffectType.FAST_DIGGING.getId());
					pl.removePotionEffect(PotionEffectType.FAST_DIGGING);
				}

				// It's an ore, we may be slowing them down.
				last_swing.put(pl.getName(), System.currentTimeMillis());
			}
			else if(!(isSkillItem(is)) || !(getSkillType(is).equalsIgnoreCase("mining"))){
				e.setCancelled(true);
				return;
			}
		}
		else{
			// Not an ore.
			last_swing.remove(pl.getName());
			slow_mining.remove(pl.getName());

			//removeMobEffect(pl, PotionEffectType.SLOW_DIGGING.getId());
			pl.removePotionEffect(PotionEffectType.SLOW_DIGGING);
			//removeMobEffect(pl, PotionEffectType.FAST_DIGGING.getId());
			pl.removePotionEffect(PotionEffectType.FAST_DIGGING);
		}
	}

	@EventHandler
	public void onBlockUnregister(BlockBreakEvent e){
		Player pl = e.getPlayer();
		if(!(pl.isOp())){
			return;
		}
		if(ore_place.containsKey(pl.getName())){
			Block b = e.getBlock();
			Location b_loc = b.getLocation();
			Location r_loc = null;
			for(Location loc : ore_location.keySet()){
				if(loc.distanceSquared(b_loc) <= 1){
					r_loc = loc;
					break;
				}
			}
			if(r_loc != null){
				ore_location.remove(r_loc);
				ore_respawn.remove(r_loc);
				pl.sendMessage(ChatColor.GOLD + "Unregistered ore block placed at: " + r_loc.getX() + ", " + r_loc.getY() + ", " + r_loc.getZ());
				r_loc.getBlock().setType(Material.AIR);
			}
		}

		if(fishing_place.containsKey(pl.getName())){
			Block b = e.getBlock();
			Location b_loc = b.getLocation();
			Location r_loc = null;
			for(Location loc : fishing_location.keySet()){
				if(loc.distanceSquared(b_loc) <= 1){
					r_loc = loc;
					break;
				}
			}
			if(r_loc != null){
				fishing_location.remove(r_loc);
				fishing_respawn.remove(r_loc);
				pl.sendMessage(ChatColor.GOLD + "Unregistered fishing spot placed at: " + r_loc.getX() + ", " + r_loc.getY() + ", " + r_loc.getZ());
				r_loc.getBlock().setType(Material.AIR);
			}
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onBlockBreak(BlockBreakEvent e){
		Player pl = e.getPlayer();
		ItemStack is = pl.getItemInHand();

		Block b = e.getBlock();
		if(b.getType() == Material.COAL_ORE || b.getType() == Material.EMERALD_ORE || b.getType() == Material.IRON_ORE || b.getType() == Material.DIAMOND_ORE || b.getType() == Material.GOLD_ORE){
			if(!(isSkillItem(is)) || !(getSkillType(is).equalsIgnoreCase("mining"))){
				e.setCancelled(true);
				return;
			}
			if(!pl.getWorld().getName().equalsIgnoreCase(Bukkit.getWorlds().get(0).getName())){
				e.setCancelled(true);
				return;
			}
			if(!(ore_location.containsKey(b.getLocation()))){
				pl.sendMessage(ChatColor.GRAY.toString() + ChatColor.ITALIC + "You cannot mine this ore.");
				e.setCancelled(true);
				return;
			}
			int pickaxe_tier = getItemTier(is);
			int ore_tier = getOreTier(b.getType());

			if(pickaxe_tier < ore_tier){
				// Cannot mine. Too low level of pickaxe. 
				e.setCancelled(true);
				return;
			}
			if(isSkillItem(is) && getSkillType(is).equalsIgnoreCase("mining")){
				if(pickaxe_tier == ore_tier){
					int break_chance = getBreakChance(is);
					break_chance += getSuccessChance(is);
					int do_i_break = new Random().nextInt(100);
					if(do_i_break > break_chance){
						e.setCancelled(true);

						RepairMechanics.subtractCustomDurability(pl, pl.getItemInHand(), 2, "wep");
						if(pl.getItemInHand().getType() == Material.AIR){
							pl.setItemInHand(new ItemStack(Material.AIR, 1));
							e.getPlayer().setItemInHand(new ItemStack(Material.AIR));
							pl.updateInventory();
						}

						pl.playSound(pl.getLocation(), Sound.DIG_STONE, 1F, 0.75F);

						Packet particles = new PacketPlayOutWorldEvent(2001, (int)Math.round(b.getLocation().getBlockX()), (int)Math.round(b.getLocation().getBlockY()), (int)Math.round(b.getLocation().getBlockZ()), 1, false);
						((CraftServer) Main.plugin.getServer()).getServer().getPlayerList().sendPacketNearby(b.getLocation().getBlockX(), b.getLocation().getBlockY() + 1, b.getLocation().getBlockZ(), 24, ((CraftWorld) pl.getWorld()).getHandle().dimension, particles);

						pl.sendMessage(ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + "You fail to gather any ore.");

						b.setType(Material.STONE);
						ore_respawn.put(b.getLocation(), System.currentTimeMillis());
						return; // Do NOT break.
					}
				}
				// BREAK the ore, give loot.
				Material m = b.getType();
				e.setCancelled(true);
				b.setType(Material.STONE);

				addEXP(pl, is, getOreEXP(m), "mining");

				RepairMechanics.subtractCustomDurability(pl, pl.getItemInHand(), 1, "wep");
				if(pl.getItemInHand().getType() == Material.AIR){
					pl.setItemInHand(new ItemStack(Material.AIR, 1));
					e.getPlayer().setItemInHand(new ItemStack(Material.AIR));
					pl.updateInventory();
					return; // It broke.
				}

				ore_respawn.put(b.getLocation(), System.currentTimeMillis());

				ItemStack drop = getOreDrop(m);
				int doi_double_drop = new Random().nextInt(100) + 1;
				if(getDoubleDropChance(is) >= doi_double_drop){
					drop.setAmount(2);
					if(CommunityMechanics.toggle_list.containsKey(pl.getName()) && CommunityMechanics.toggle_list.get(pl.getName()).contains("debug")){
						pl.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + "          DOUBLE ORE DROP" + ChatColor.YELLOW + " (2x)");
					}
				}

				int doi_triple_drop = new Random().nextInt(100) + 1;
				if(getTripleDropChance(is) >= doi_triple_drop){
					drop.setAmount(3);
					if(CommunityMechanics.toggle_list.containsKey(pl.getName()) && CommunityMechanics.toggle_list.get(pl.getName()).contains("debug")){
						pl.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + "          TRIPLE ORE DROP" + ChatColor.YELLOW + " (3x)");
					}
				}

				int doi_drop_gems = new Random().nextInt(100) + 1;
				if(getGemFindChance(is) >= doi_drop_gems){

					int amount_to_drop = 0;
					if(ore_tier == 1){
						amount_to_drop = new Random().nextInt(20) + 1;
					}
					if(ore_tier == 2){
						amount_to_drop = new Random().nextInt(40 - 20) + 20;
					}
					if(ore_tier == 3){
						amount_to_drop = new Random().nextInt(60 - 40) + 40;
					}
					if(ore_tier == 4){
						amount_to_drop = new Random().nextInt(90 - 70) + 70;
					}
					if(ore_tier == 5){
						amount_to_drop = new Random().nextInt(110 - 90) + 90;
					}

					amount_to_drop = (int)((double)amount_to_drop * 0.80D);

					/*if(pl.getInventory().firstEmpty() != -1){
						pl.getInventory().addItem(MoneyMechanics.makeGems(amount_to_drop));
					}
					else{*/
					// Drop on floor so that the itemPickup event is fired, put gems in pouch first, etc, etc.
					pl.getWorld().dropItemNaturally(pl.getLocation(), MoneyMechanics.makeGems(amount_to_drop));
					//}

					if(CommunityMechanics.toggle_list.containsKey(pl.getName()) && CommunityMechanics.toggle_list.get(pl.getName()).contains("debug")){
						pl.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + "          FOUND " + amount_to_drop + " GEM(s)" + ChatColor.YELLOW + "");
					}
				}

				if(pl.getInventory().firstEmpty() != -1){
					pl.getInventory().addItem(drop);
				}
				else{
					if(pl.getInventory().contains(drop.getType())){
						for(Entry<Integer, ? extends ItemStack> data : pl.getInventory().all(drop.getType()).entrySet()){
							ItemStack in_inv = data.getValue();
							if(in_inv.getAmount() + drop.getAmount() <= 64){
								int amount = in_inv.getAmount();
								in_inv.setAmount(amount + drop.getAmount());
								pl.getInventory().setItem(data.getKey(), in_inv);
								pl.updateInventory();
								return;
							}
						}
					}
					b.getWorld().dropItem(b.getLocation(), drop);
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onOreSpotPlace(BlockPlaceEvent e){
		Player pl = e.getPlayer();
		if(!(ore_place.containsKey(pl.getName()))){
			return;
		}

		if(!(pl.getItemInHand().getType() == Material.STONE)){
			return;
		}

		Block b = e.getBlock();

		Material type = null;
		String ore_type = ore_place.get(pl.getName());
		if(ore_type.equalsIgnoreCase("t1")){
			type = Material.COAL_ORE;
		}
		if(ore_type.equalsIgnoreCase("t2")){
			type = Material.EMERALD_ORE;
		}
		if(ore_type.equalsIgnoreCase("t3")){
			type = Material.IRON_ORE;
		}
		if(ore_type.equalsIgnoreCase("t4")){
			type = Material.DIAMOND_ORE;
		}
		if(ore_type.equalsIgnoreCase("t5")){
			type = Material.GOLD_ORE;
		}

		ore_location.put(b.getLocation(), type);
		ore_respawn.put(b.getLocation(), System.currentTimeMillis());
		pl.sendMessage(ChatColor.GREEN + "Registered new " + type.name() + " location at " + b.getLocation().getX() + ", " + b.getLocation().getY() + ", " + b.getLocation().getZ());
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onFishingSpotPlace(BlockPlaceEvent e){
		Player pl = e.getPlayer();
		if(!(fishing_place.containsKey(pl.getName()))){
			return;
		}

		if(!(pl.getItemInHand().getType() == Material.WATER_LILY)){
			return;
		}

		Block b = e.getBlock();

		String fishing_type = fishing_place.get(pl.getName());
		int fish_tier = Integer.parseInt(fishing_type.replaceAll("t", ""));

		fishing_location.put(b.getLocation(), fish_tier);
		fishing_respawn.put(b.getLocation(), System.currentTimeMillis());
		pl.sendMessage(ChatColor.GREEN + "Registered new tier " + fish_tier + " fishing spot at " + b.getLocation().getX() + ", " + b.getLocation().getY() + ", " + b.getLocation().getZ());

		e.setCancelled(true); // Don't actually place lilypad.
	}

	@EventHandler
	public void onAsyncPlayerChatEvent(AsyncPlayerChatEvent e){
		// TODO: 2-step authentication.
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onInventoryOpen(InventoryOpenEvent e){
		Player pl = (Player)e.getPlayer();
		if(slow_mining.containsKey(pl.getName())){
			last_swing.remove(pl.getName());
			slow_mining.remove(pl.getName());

			//removeMobEffect(pl, PotionEffectType.SLOW_DIGGING.getId());
			pl.removePotionEffect(PotionEffectType.SLOW_DIGGING);
			//removeMobEffect(pl, PotionEffectType.FAST_DIGGING.getId());
			pl.removePotionEffect(PotionEffectType.FAST_DIGGING);
		}
	}

}