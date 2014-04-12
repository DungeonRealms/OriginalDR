package me.vaqxine.DuelMechanics;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import me.vaqxine.Main;
import me.vaqxine.ChatMechanics.ChatMechanics;
import me.vaqxine.CommunityMechanics.CommunityMechanics;
import me.vaqxine.DuelMechanics.commands.CommandToggleDuel;
import me.vaqxine.GuildMechanics.GuildMechanics;
import me.vaqxine.HealthMechanics.HealthMechanics;
import me.vaqxine.ItemMechanics.ItemMechanics;
import me.vaqxine.KarmaMechanics.KarmaMechanics;
import me.vaqxine.MerchantMechanics.MerchantMechanics;
import me.vaqxine.MoneyMechanics.MoneyMechanics;
import me.vaqxine.MountMechanics.MountMechanics;
import me.vaqxine.PetMechanics.PetMechanics;
import me.vaqxine.RealmMechanics.RealmMechanics;
import me.vaqxine.RecordMechanics.RecordMechanics;
import me.vaqxine.RestrictionMechanics.RestrictionMechanics;
import me.vaqxine.ScoreboardMechanics.ScoreboardMechanics;
import me.vaqxine.ShopMechanics.ShopMechanics;
import me.vaqxine.TradeMechanics.TradeMechanics;
import me.vaqxine.TutorialMechanics.TutorialMechanics;
import me.vaqxine.enums.CC;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.StateFlag.State;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

@SuppressWarnings("deprecation")
public class DuelMechanics implements Listener {
	Logger log = Logger.getLogger("Minecraft");
	
	// No Regen
	// No Armor Change
	// No Weapon Change
	// TODO: Despawn pets
	
	// These are just items used for the dueling menu.
	public static ItemStack divider = ItemMechanics.signNewCustomItem(Material.BONE, (short) 0, " ", "");
	public static ItemStack gray_button = ItemMechanics.signNewCustomItem(Material.INK_SACK, (short) 8, ChatColor.YELLOW.toString() + "Click to ACCEPT Duel Stake", "");
	public static ItemStack green_button = ItemMechanics.signNewCustomItem(Material.INK_SACK, (short) 10, ChatColor.GREEN.toString() + "Duel ACCEPTED.", ChatColor.GRAY.toString() + "Modify the stake to unaccept.");
	
	public static ItemStack t0_armor_icon = ItemMechanics.signNewCustomItem(Material.getMaterial(111), (short) 0, ChatColor.WHITE.toString() + "Armor Tier Limit", ChatColor.RED + "Tier 0 [NO ARMOR]" + "," + ChatColor.GRAY + "You will not be able to use ANY" + "," + ChatColor.GRAY + "armor in this duel.");
	
	public static ItemStack t1_armor_icon = ItemMechanics.signNewCustomItem(Material.LEATHER_CHESTPLATE, (short) 0, ChatColor.WHITE.toString() + "Armor Tier Limit", ChatColor.RED + "Tier 1" + "," + ChatColor.GRAY + "You will not be able to use ANY" + "," + ChatColor.GRAY + "armor above " + ChatColor.WHITE + ChatColor.UNDERLINE + "TIER 1");
	
	public static ItemStack t2_armor_icon = ItemMechanics.signNewCustomItem(Material.CHAINMAIL_CHESTPLATE, (short) 0, ChatColor.GREEN.toString() + "Armor Tier Limit", ChatColor.RED + "Tier 2" + "," + ChatColor.GRAY + "You will not be able to use ANY" + "," + ChatColor.GRAY + "armor above " + ChatColor.GREEN + ChatColor.UNDERLINE + "TIER 2");
	
	public static ItemStack t3_armor_icon = ItemMechanics.signNewCustomItem(Material.IRON_CHESTPLATE, (short) 0, ChatColor.AQUA.toString() + "Armor Tier Limit", ChatColor.RED + "Tier 3" + "," + ChatColor.GRAY + "You will not be able to use ANY" + "," + ChatColor.GRAY + "armor above " + ChatColor.AQUA + ChatColor.UNDERLINE + "TIER 3");
	
	public static ItemStack t4_armor_icon = ItemMechanics.signNewCustomItem(Material.DIAMOND_CHESTPLATE, (short) 0, ChatColor.LIGHT_PURPLE.toString() + "Armor Tier Limit", ChatColor.RED + "Tier 4" + "," + ChatColor.GRAY + "You will not be able to use ANY" + "," + ChatColor.GRAY + "armor above " + ChatColor.LIGHT_PURPLE + ChatColor.UNDERLINE + "TIER 4");
	
	public static ItemStack t5_armor_icon = ItemMechanics.signNewCustomItem(Material.GOLD_CHESTPLATE, (short) 0, ChatColor.YELLOW.toString() + "Armor Tier Limit", ChatColor.RED + "Tier 5" + "," + ChatColor.GRAY + "You will not be able to use ANY" + "," + ChatColor.GRAY + "armor above " + ChatColor.YELLOW + ChatColor.UNDERLINE + "TIER 5");
	
	public static ItemStack t0_weapon_icon = ItemMechanics.signNewCustomItem(Material.getMaterial(397), (short) 3, ChatColor.WHITE.toString() + "Weapon Tier Limit", ChatColor.RED + "Tier 0 [FISTS]" + "," + ChatColor.GRAY + "You will not be able to use ANY" + "," + ChatColor.GRAY + "weapons in this duel.");
	
	public static ItemStack t1_weapon_icon = ItemMechanics.signNewCustomItem(Material.WOOD_SWORD, (short) 0, ChatColor.WHITE.toString() + "Weapon Tier Limit", ChatColor.RED + "Tier 1" + "," + ChatColor.GRAY + "You will not be able to use ANY" + "," + ChatColor.GRAY + "weapon above " + ChatColor.WHITE + ChatColor.UNDERLINE + "TIER 1");
	
	public static ItemStack t2_weapon_icon = ItemMechanics.signNewCustomItem(Material.STONE_SWORD, (short) 0, ChatColor.GREEN.toString() + "Weapon Tier Limit", ChatColor.RED + "Tier 2" + "," + ChatColor.GRAY + "You will not be able to use ANY" + "," + ChatColor.GRAY + "weapon above " + ChatColor.GREEN + ChatColor.UNDERLINE + "TIER 2");
	
	public static ItemStack t3_weapon_icon = ItemMechanics.signNewCustomItem(Material.IRON_SWORD, (short) 0, ChatColor.AQUA.toString() + "Weapon Tier Limit", ChatColor.RED + "Tier 3" + "," + ChatColor.GRAY + "You will not be able to use ANY" + "," + ChatColor.GRAY + "weapon above " + ChatColor.AQUA + ChatColor.UNDERLINE + "TIER 3");
	
	public static ItemStack t4_weapon_icon = ItemMechanics.signNewCustomItem(Material.DIAMOND_SWORD, (short) 0, ChatColor.LIGHT_PURPLE.toString() + "Weapon Tier Limit", ChatColor.RED + "Tier 4" + "," + ChatColor.GRAY + "You will not be able to use ANY" + "," + ChatColor.GRAY + "weapon above " + ChatColor.LIGHT_PURPLE + ChatColor.UNDERLINE + "TIER 4");
	
	public static ItemStack t5_weapon_icon = ItemMechanics.signNewCustomItem(Material.GOLD_SWORD, (short) 0, ChatColor.YELLOW.toString() + "Weapon Tier Limit", ChatColor.RED + "Tier 5" + "," + ChatColor.GRAY + "You will not be able to use ANY" + "," + ChatColor.GRAY + "weapon above " + ChatColor.YELLOW + ChatColor.UNDERLINE + "TIER 5");
	// These are just items used for the dueling menu.
	
	public static HashMap<String, String> duel_map = new HashMap<String, String>();
	public static HashMap<String, String> duel_request = new HashMap<String, String>();
	public static HashMap<String, Integer> duel_max_armor_tier = new HashMap<String, Integer>();
	public static HashMap<String, Integer> duel_max_weapon_tier = new HashMap<String, Integer>();
	
	static HashMap<String, Inventory> duel_stake = new HashMap<String, Inventory>();
	
	public static HashMap<Player, Integer> duel_countdown = new HashMap<Player, Integer>();
	public static HashMap<Player, Integer> duel_request_cooldown = new HashMap<Player, Integer>();
	
	static HashMap<Player, Inventory> duel_secure = new HashMap<Player, Inventory>();
	static HashMap<String, Location> duel_start_location = new HashMap<String, Location>();
	
	List<String> in_duel_window = new ArrayList<String>();
	public static List<Player> in_duel = new ArrayList<Player>();
	public static List<String> warned_players = new ArrayList<String>();
	
	static WorldGuardPlugin wg = null;
	static DuelMechanics instance = null;
	
	public void onEnable() {
		Bukkit.getServer().getPluginManager().registerEvents(this, Main.plugin);
		instance = this;
		wg = getWorldGuard();
		
		Main.plugin.getCommand("toggleduel").setExecutor(new CommandToggleDuel());
		
		Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(Main.plugin, new Runnable() {
			public void run() {
				tickRequestCooldown();
				sendCountdown();
			}
		}, 40L, 20L);
		
		new BukkitRunnable() {
			@Override
			public void run() {
				warned_players.clear();
				fakePlayerMoveEvent();
			}
		}.runTaskTimerAsynchronously(Main.plugin, 5 * 20L, 30L);
		
		log.info("[DuelMechanics] has been enabled.");
	}
	
	public void onDisable() {
		log.info("[DuelMechanics] has been disabled.");
	}
	
	public void fakePlayerMoveEvent() {
		List<String> lduel_requests = new ArrayList<String>();
		for(String s : duel_request.keySet()) {
			lduel_requests.add(s);
		}
		for(String s : lduel_requests) {
			if(Bukkit.getPlayer(s) == null) {
				continue;
			}
			Player p = Bukkit.getPlayer(s);
			String opponent = duel_request.get(s);
			if(opponent == null || Bukkit.getPlayer(opponent) == null) {
				continue;
			}
			Player p_enemy = Bukkit.getPlayer(opponent);
			if(!(p.getWorld().getName().equalsIgnoreCase(p_enemy.getWorld().getName())) || p.getLocation().distanceSquared(p_enemy.getLocation()) > 100.0D) {
				// Cancel duel request.
				p.sendMessage(ChatColor.RED + "The user " + p_enemy.getName() + " is now " + ChatColor.BOLD + " >10 blocks " + ChatColor.RED + "away from you, and therfore cancelled their duel request.");
				p_enemy.sendMessage(ChatColor.RED + "The user " + p.getName() + " is now " + ChatColor.BOLD + " >10 blocks " + ChatColor.RED + "away from you, and therfore cancelled their duel request.");
				
				if(p.getOpenInventory().getTopInventory().getTitle().contains(p.getName())) {
					p.closeInventory();
				}
				if(p_enemy.getOpenInventory().getTopInventory().getTitle().contains(p_enemy.getName())) {
					p_enemy.closeInventory();
				}
				
				duel_request.remove(p.getName());
				duel_request.remove(p_enemy.getName());
			}
		}
	}
	
	public boolean isRequestCooldownOver(Player p) {
		if(duel_request_cooldown.containsKey(p)) { return false; }
		return true;
	}
	
	public int getRequestCooldownLeft(Player p) {
		return duel_request_cooldown.get(p);
	}
	
	public void tickRequestCooldown() {
		if(duel_request_cooldown.size() <= 0) { return; }
		
		HashMap<Player, Integer> duel_request_cooldown_mirror = new HashMap<Player, Integer>(duel_request_cooldown);
		for(Map.Entry<Player, Integer> entry : duel_request_cooldown_mirror.entrySet()) {
			Player p = entry.getKey();
			int val = entry.getValue();
			val--;
			
			if(val <= 0) {
				duel_request_cooldown.remove(p);
				continue;
			}
			
			duel_request_cooldown.put(p, val);
		}
	}
	
	public void sendCountdown() {
		if(duel_countdown.size() > 0) {
			HashMap<Player, Integer> duel_countdown_mirror = new HashMap<Player, Integer>(duel_countdown);
			for(Map.Entry<Player, Integer> entry : duel_countdown_mirror.entrySet()) {
				Player p = entry.getKey();
				if(p == null || !p.isOnline()) {
					duel_countdown.remove(p);
					if(duel_map.containsKey(p.getName())) {
						Player opponent = Bukkit.getPlayer(duel_map.get(p.getName()));
						opponent.sendMessage(ChatColor.YELLOW + "Opponent logged out, duel cancelled.");
						duel_countdown.remove(opponent);
					}
					continue;
				}
				Integer val = entry.getValue();
				val--;
				p.sendMessage(ChatColor.YELLOW + "" + val + "...");
				if(val <= 0) {
					p.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "FIGHT!");
					in_duel.add(p);
					duel_countdown.remove(p);
					
					if(duel_map.containsKey(p.getName()) && Bukkit.getPlayer(duel_map.get(p.getName())) != null) {
						duel_countdown.remove(Bukkit.getPlayer(duel_map.get(p.getName())));
					}
					continue;
				}
				duel_countdown.put(p, val);
			}
		}
	}
	
	private WorldGuardPlugin getWorldGuard() {
		Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("WorldGuard");
		
		// WorldGuard may not be loaded
		if(plugin == null || !(plugin instanceof WorldGuardPlugin)) { return null; // Maybe you want throw an exception instead
		}
		
		return (WorldGuardPlugin) plugin;
	}
	
	public void setDuelColors(Player p1, Player p2) {
		CommunityMechanics.setColor(p1, ChatColor.RED);
		CommunityMechanics.setColor(p2, ChatColor.RED);
		/*ChatColor red = ChatColor.RED;

		String r_name1 = p1.getName();
		String r_name2 = p2.getName();

		EntityPlayer ent_p1 = ((CraftPlayer) p1).getHandle();
		EntityPlayer ent_p2 = ((CraftPlayer) p2).getHandle();

		net.minecraft.server.v1_7_R1.ItemStack ent1_boots = null, ent1_legs = null, ent1_chest = null, ent1_head = null;
		net.minecraft.server.v1_7_R1.ItemStack ent2_boots = null, ent2_legs = null, ent2_chest = null, ent2_head = null;

		if(ent_p1.getEquipment(1) != null){
			ent1_boots = ent_p1.getEquipment(1);
		}
		if(ent_p1.getEquipment(2) != null){
			ent1_legs = ent_p1.getEquipment(2);
		}
		if(ent_p1.getEquipment(3) != null){
			ent1_chest = ent_p1.getEquipment(3);
		}
		if(ent_p1.getEquipment(4) != null){
			ent1_head =ent_p1.getEquipment(4);
		}

		if(ent_p2.getEquipment(1) != null){
			ent2_boots = ent_p2.getEquipment(1);
		}
		if(ent_p2.getEquipment(2) != null){
			ent2_legs = ent_p2.getEquipment(2);
		}
		if(ent_p2.getEquipment(3) != null){
			ent2_chest = ent_p2.getEquipment(3);
		}
		if(ent_p2.getEquipment(4) != null){
			ent2_head =ent_p2.getEquipment(4);
		}

		ent_p1.name = red.toString() + ChatColor.stripColor(p1.getName());
		ent_p2.name = red.toString() + ChatColor.stripColor(p2.getName());

		((CraftPlayer) p1).getHandle().playerConnection.sendPacket(new Packet20NamedEntitySpawn(ent_p2));
		((CraftPlayer) p2).getHandle().playerConnection.sendPacket(new Packet20NamedEntitySpawn(ent_p1));

		List<Packet> ent1_pack_list = new ArrayList<Packet>();
		if(ent1_boots != null){
			ent1_pack_list.add(new Packet5EntityEquipment(ent_p1.id, 1, ent1_boots));
		}
		if(ent1_legs != null){
			ent1_pack_list.add(new Packet5EntityEquipment(ent_p1.id, 2, ent1_legs));
		}
		if(ent1_chest != null){
			ent1_pack_list.add(new Packet5EntityEquipment(ent_p1.id, 3, ent1_chest));
		}
		if(ent1_head != null){
			ent1_pack_list.add(new Packet5EntityEquipment(ent_p1.id, 4, ent1_head));
		}

		List<Packet> ent2_pack_list = new ArrayList<Packet>();
		if(ent2_boots != null){
			ent2_pack_list.add(new Packet5EntityEquipment(ent_p2.id, 1, ent2_boots));
		}
		if(ent2_legs != null){
			ent2_pack_list.add(new Packet5EntityEquipment(ent_p2.id, 2, ent2_legs));
		}
		if(ent2_chest != null){
			ent2_pack_list.add(new Packet5EntityEquipment(ent_p2.id, 3, ent2_chest));
		}
		if(ent2_head != null){
			ent2_pack_list.add(new Packet5EntityEquipment(ent_p2.id, 4, ent2_head));
		}

		for(Packet pa : ent1_pack_list){
			((CraftPlayer) p2).getHandle().playerConnection.sendPacket(pa);
		}

		for(Packet pa : ent2_pack_list){
			((CraftPlayer) p1).getHandle().playerConnection.sendPacket(pa);
		}

		ent_p1.name = ChatColor.stripColor(r_name1);
		ent_p2.name = ChatColor.stripColor(r_name2);*/
	}
	
	public void restoreColors(Player p1, Player p2) {
		ScoreboardMechanics.removePlayerFromTeam("red", p1);
		ScoreboardMechanics.removePlayerFromTeam("red", p2);
		
		if(GuildMechanics.inGuild(p1.getName())) {
			String g_name = GuildMechanics.guild_handle_map.get(GuildMechanics.getGuild(p1.getName()));
			
			String fixed_gname = g_name;
			
			if((g_name + ".default").length() > 16) {
				// Name is too long, let's cut off from g_name.
				// .default = 8
				fixed_gname = g_name.substring(0, 8);
			}
			
			ScoreboardMechanics.removePlayerFromTeam(fixed_gname + ".chaotic", p1);
			
			return;
		}
		if(GuildMechanics.inGuild(p2.getName())) {
			String g_name = GuildMechanics.guild_handle_map.get(GuildMechanics.getGuild(p2.getName()));
			
			String fixed_gname = g_name;
			
			if((g_name + ".default").length() > 16) {
				// Name is too long, let's cut off from g_name.
				// .default = 8
				fixed_gname = g_name.substring(0, 8);
			}
			
			ScoreboardMechanics.removePlayerFromTeam(fixed_gname + ".chaotic", p2);
			
			return;
		}
		
		KarmaMechanics.sendAlignColor(p2, p1);
		KarmaMechanics.sendAlignColor(p1, p2);
	}
	
	public void startDuel(Player attacker, Player attacked) {
		duel_request.remove(attacked.getName());
		duel_request.remove(attacker.getName());
		
		attacker.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "Duel stake ACCEPTED -> Opponent: " + ChatColor.GREEN + "" + ChatColor.BOLD + attacked.getName() + ChatColor.GREEN + "");
		attacker.sendMessage(ChatColor.YELLOW + "Duel will begin in 10 seconds...");
		
		attacked.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "Duel stake ACCEPTED -> Opponent: " + ChatColor.GREEN + "" + ChatColor.BOLD + attacker.getName() + ChatColor.GREEN + "");
		attacked.sendMessage(ChatColor.YELLOW + "Duel will begin in 10 seconds...");
		
		setDuelColors(attacker, attacked);
		
		MountMechanics.summon_mount.remove(attacker.getName());
		MountMechanics.summon_mount.remove(attacked.getName());
		
		attacker.eject();
		attacked.eject();
		
		duel_countdown.put(attacker, 10);
		duel_countdown.put(attacked, 10);
	}
	
	public boolean removeIllegalArmor(Player pl) {
		int max_armor_tier = duel_max_armor_tier.get(pl.getName());
		// Remove any illegal armor the player may be wearing at the start of the duel.
		ItemStack helmet = pl.getInventory().getHelmet();
		ItemStack chest = pl.getInventory().getChestplate();
		ItemStack legs = pl.getInventory().getLeggings();
		ItemStack boots = pl.getInventory().getBoots();
		
		try {
			if(helmet != null && (ItemMechanics.getItemTier(helmet) > max_armor_tier)) {
				pl.getInventory().setItem(pl.getInventory().firstEmpty(), helmet);
				pl.getInventory().setHelmet(new ItemStack(Material.AIR));
			}
			if(chest != null && (ItemMechanics.getItemTier(chest) > max_armor_tier)) {
				pl.getInventory().setItem(pl.getInventory().firstEmpty(), chest);
				pl.getInventory().setChestplate(new ItemStack(Material.AIR));
			}
			if(legs != null && (ItemMechanics.getItemTier(legs) > max_armor_tier)) {
				pl.getInventory().setItem(pl.getInventory().firstEmpty(), legs);
				pl.getInventory().setLeggings(new ItemStack(Material.AIR));
			}
			if(boots != null && (ItemMechanics.getItemTier(boots) > max_armor_tier)) {
				pl.getInventory().setItem(pl.getInventory().firstEmpty(), boots);
				pl.getInventory().setBoots(new ItemStack(Material.AIR));
			}
			
			final String p_name = pl.getName();
			
			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
				public void run() {
					ItemMechanics.updatePlayerStats(p_name);
				}
			}, 2L);
			
		} catch(ArrayIndexOutOfBoundsException err) {
			// ArrayIndexOutOfBounds thrown when no space?
			//err.printStackTrace();
			return false;
		}
		
		return true; // We guuci.
	}
	
	public static boolean isArmorIcon(ItemStack is) {
		if(is != null && is.hasItemMeta() && is.getItemMeta().hasDisplayName() && is.getItemMeta().getDisplayName().contains("Armor Tier Limit") && !(is.getItemMeta().getDisplayName().contains(ChatColor.GOLD.toString()))) { return true; }
		return false;
	}
	
	public static boolean isWeaponIcon(ItemStack is) {
		if(is != null && is.hasItemMeta() && is.getItemMeta().hasDisplayName() && is.getItemMeta().getDisplayName().contains("Weapon Tier Limit") && !(is.getItemMeta().getDisplayName().contains(ChatColor.GOLD.toString()))) { return true; }
		return false;
	}
	
	public ItemStack cycleArmorIcon(ItemStack is) {
		// is = The previous armor icon.
		int previous_tier = ItemMechanics.getItemTier(is);
		if(is.getType() == Material.getMaterial(111)) { return t1_armor_icon; }
		if(previous_tier == 1) { return t2_armor_icon; }
		if(previous_tier == 2) { return t3_armor_icon; }
		if(previous_tier == 3) { return t4_armor_icon; }
		if(previous_tier == 4) { return t5_armor_icon; }
		if(previous_tier == 5) { return t0_armor_icon; }
		return t0_armor_icon; // Default.
	}
	
	public ItemStack cycleWeaponIcon(ItemStack is) {
		// is = The previous armor icon.
		int previous_tier = ItemMechanics.getItemTier(is);
		if(is.getType() == Material.getMaterial(397)) { return t1_weapon_icon; }
		if(previous_tier == 1) { return t2_weapon_icon; }
		if(previous_tier == 2) { return t3_weapon_icon; }
		if(previous_tier == 3) { return t4_weapon_icon; }
		if(previous_tier == 4) { return t5_weapon_icon; }
		if(previous_tier == 5) { return t0_weapon_icon; }
		return t0_weapon_icon; // Default.
	}
	
	public void loadDuelMenu(final Player attacker, final Player attacked) {
		
		duel_start_location.put(attacker.getName(), attacker.getLocation());
		duel_start_location.put(attacked.getName(), attacked.getLocation());
		
		final Inventory DuelWindow = Bukkit.createInventory(null, 36, TradeMechanics.generateTitle(attacked.getName(), attacker.getName()));
		DuelWindow.setItem(4, divider);
		DuelWindow.setItem(13, divider);
		DuelWindow.setItem(22, divider);
		DuelWindow.setItem(0, gray_button);
		DuelWindow.setItem(8, gray_button);
		
		DuelWindow.setItem(27, divider);
		DuelWindow.setItem(28, divider);
		DuelWindow.setItem(29, divider);
		DuelWindow.setItem(31, divider);
		
		DuelWindow.setItem(30, divider);
		DuelWindow.setItem(32, divider);
		
		// 1.1
		DuelWindow.setItem(30, t5_armor_icon);
		DuelWindow.setItem(32, t5_weapon_icon);
		
		DuelWindow.setItem(33, divider);
		DuelWindow.setItem(34, divider);
		DuelWindow.setItem(35, divider);
		
		attacker.openInventory(DuelWindow);
		attacked.openInventory(DuelWindow);
		
		attacker.sendMessage(ChatColor.YELLOW + "Duel Menu Opened.");
		attacker.sendMessage(ChatColor.GRAY + "Place staked items here.");
		attacker.sendMessage(ChatColor.GRAY + "No stakes required.");
		
		attacked.sendMessage(ChatColor.YELLOW + "Duel Menu Opened.");
		attacked.sendMessage(ChatColor.GRAY + "Place staked items here.");
		attacked.sendMessage(ChatColor.GRAY + "No stakes required.");
		
		attacker.playSound(attacker.getLocation(), Sound.WOOD_CLICK, 1F, 0.8F);
		attacked.playSound(attacked.getLocation(), Sound.WOOD_CLICK, 1F, 0.8F);
		
		in_duel_window.add(attacked.getName());
		in_duel_window.add(attacker.getName());
		
		duel_map.remove(attacker.getName());
		duel_map.remove(attacked.getName());
		
		duel_max_weapon_tier.remove(attacker.getName());
		duel_max_armor_tier.remove(attacker.getName());
		
		duel_max_weapon_tier.remove(attacked.getName());
		duel_max_armor_tier.remove(attacked.getName());
		
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
			public void run() {
				if(in_duel_window.contains(attacked.getName()) && in_duel_window.contains(attacker.getName())) {
					duel_map.put(attacker.getName(), attacked.getName());
					duel_map.put(attacked.getName(), attacker.getName());
				}
			}
		}, 4L);
	}
	
	@EventHandler(priority = EventPriority.LOW)
	public void DuelWindowQuitMonitor(PlayerQuitEvent e) {
		Player closer = e.getPlayer();
		if(!(in_duel_window.contains(closer.getName()))) { return; }
		
		if(!(duel_map.containsKey(closer.getName()))) {
			// This could be an issue since we have a 10 tick delay on opening the menu...
			return;
		}
		
		if(Bukkit.getPlayer(duel_map.get(closer.getName())) == null) {
			// They'd have to quit at exact same time...
			String close_partner = duel_map.get(closer.getName());
			duel_map.remove(closer.getName());
			duel_map.remove(close_partner);
			duel_request.remove(closer.getName());
			duel_request.remove(close_partner);
			duel_secure.remove(closer);
			in_duel_window.remove(closer.getName());
			in_duel_window.remove(close_partner);
			return;
		}
		
		Player duel_partner = Bukkit.getPlayer(duel_map.get(closer.getName()));
		
		if(duel_map.containsKey(closer.getName())) {
			boolean left_side = false;
			Inventory duelInv = closer.getOpenInventory().getTopInventory();
			
			if(duel_request.containsKey(closer.getName())) {
				left_side = true;
			}
			if(duel_request.containsKey(duel_partner.getName())) {
				left_side = false;
			}
			
			int slot_var = -1;
			if(left_side == true) {
				while(slot_var <= 27) {
					slot_var++;
					if(!(slot_var == 0 || slot_var == 1 || slot_var == 2 || slot_var == 3 || slot_var == 9 || slot_var == 10 || slot_var == 11 || slot_var == 12 || slot_var == 18 || slot_var == 19 || slot_var == 20 || slot_var == 21)) {
						continue;
					}
					ItemStack i = duelInv.getItem(slot_var);
					if(i == null || i.getType() == Material.AIR || MerchantMechanics.isTradeButton(i) || i.getType() == Material.BONE) {
						continue;
					}
					if(i.getType() == Material.EMERALD) {
						i = MoneyMechanics.makeGems(MoneyMechanics.getCountMeta(i));
					}
					closer.getInventory().setItem(closer.getInventory().firstEmpty(), i);
					
				}
				
				slot_var = -1;
				
				while(slot_var <= 27) {
					slot_var++;
					if(!(slot_var == 5 || slot_var == 6 || slot_var == 7 || slot_var == 8 || slot_var == 14 || slot_var == 15 || slot_var == 16 || slot_var == 17 || slot_var == 23 || slot_var == 24 || slot_var == 25 || slot_var == 26)) {
						continue;
					}
					ItemStack i = duelInv.getItem(slot_var);
					if(i == null || i.getType() == Material.AIR || MerchantMechanics.isTradeButton(i) || i.getType() == Material.BONE) {
						continue;
					}
					if(i.getType() == Material.EMERALD) {
						i = MoneyMechanics.makeGems(MoneyMechanics.getCountMeta(i));
					}
					duel_partner.getInventory().setItem(duel_partner.getInventory().firstEmpty(), i);
				}
			}
			
			if(left_side == false) {
				while(slot_var <= 27) {
					slot_var++;
					if(!(slot_var == 0 || slot_var == 1 || slot_var == 2 || slot_var == 3 || slot_var == 9 || slot_var == 10 || slot_var == 11 || slot_var == 12 || slot_var == 18 || slot_var == 19 || slot_var == 20 || slot_var == 21)) {
						continue;
					}
					ItemStack i = duelInv.getItem(slot_var);
					if(i == null || i.getType() == Material.AIR || MerchantMechanics.isTradeButton(i) || i.getType() == Material.BONE) {
						continue;
					}
					if(i.getType() == Material.EMERALD) {
						i = MoneyMechanics.makeGems(MoneyMechanics.getCountMeta(i));
					}
					duel_partner.getInventory().setItem(duel_partner.getInventory().firstEmpty(), i);
				}
				slot_var = -1;
				
				while(slot_var <= 27) {
					slot_var++;
					if(!(slot_var == 5 || slot_var == 6 || slot_var == 7 || slot_var == 8 || slot_var == 14 || slot_var == 15 || slot_var == 16 || slot_var == 17 || slot_var == 23 || slot_var == 24 || slot_var == 25 || slot_var == 26)) {
						continue;
					}
					ItemStack i = duelInv.getItem(slot_var);
					if(i == null || i.getType() == Material.AIR || MerchantMechanics.isTradeButton(i) || i.getType() == Material.BONE) {
						continue;
					}
					if(i.getType() == Material.EMERALD) {
						i = MoneyMechanics.makeGems(MoneyMechanics.getCountMeta(i));
					}
					closer.getInventory().setItem(closer.getInventory().firstEmpty(), i);
				}
			}
			
			if(closer.getOpenInventory().getTopInventory().getName().contains(closer.getName())) {
				closer.getOpenInventory().getTopInventory().clear();
			}
			
			if(duel_partner.getOpenInventory().getTopInventory().getName().contains(duel_partner.getName())) {
				duel_partner.getOpenInventory().getTopInventory().clear();
			}
			
			duel_map.remove(closer.getName());
			duel_map.remove(duel_partner.getName());
			duel_max_weapon_tier.remove(closer.getName());
			duel_max_armor_tier.remove(closer.getName());
			duel_max_weapon_tier.remove(duel_partner.getName());
			duel_max_armor_tier.remove(duel_partner.getName());
			duel_request.remove(closer.getName());
			duel_request.remove(duel_partner.getName());
			duel_secure.remove(closer);
			duel_secure.remove(duel_partner);
			in_duel_window.remove(closer.getName());
			in_duel_window.remove(duel_partner.getName());
			
			duel_partner.closeInventory();
			
			duel_partner.sendMessage(ChatColor.RED + closer.getName() + " logged out, duel cancelled.");
		}
	}
	
	@EventHandler
	public void onInventoryOpenEvent(InventoryOpenEvent e) {
		Player p = (Player) e.getPlayer();
		if(in_duel_window.contains(p.getName()) && duel_map.containsKey(p.getName()) && Bukkit.getPlayer(duel_map.get(p.getName())) != null) {
			if(!e.getInventory().getName().toLowerCase().contains(p.getName().toLowerCase())) {
				e.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onSpoilersMenuCloseEvent(InventoryCloseEvent e) {
		if(!e.getInventory().getName().equalsIgnoreCase("Spoils")) { return; }
		Player p = (Player) e.getPlayer();
		Inventory spoils = e.getInventory();
		
		for(ItemStack i : spoils.getContents()) {
			if(i != null && i.getType() != Material.AIR) {
				if(p.getInventory().firstEmpty() == -1) {
					p.getWorld().dropItem(p.getLocation(), i);
					continue;
				}
				p.getInventory().setItem(p.getInventory().firstEmpty(), i);
			}
		}
		
		p.updateInventory();
	}
	
	@EventHandler
	public void onInventoryCloseEvent(InventoryCloseEvent e) {
		Player closer = (Player) e.getPlayer();
		if(!(e.getInventory().getName().contains(closer.getName())) || !e.getInventory().contains(Material.BONE)) { return; }
		if(!in_duel_window.contains(closer.getName())) { return; }
		if(!(duel_map.containsKey(closer.getName()))) { return; }
		Player trade_partner = Bukkit.getPlayer(duel_map.get(closer.getName()));
		
		in_duel_window.remove(closer.getName());
		in_duel_window.remove(trade_partner.getName());
		
		boolean left_side = false;
		Inventory tradeInv = closer.getOpenInventory().getTopInventory();
		
		if(duel_request.containsKey(closer.getName())) {
			left_side = true;
		}
		if(duel_request.containsKey(trade_partner.getName())) {
			left_side = false;
		}
		
		int slot_var = -1;
		if(left_side == true) {
			while(slot_var <= 27) {
				slot_var++;
				if(!(slot_var == 0 || slot_var == 1 || slot_var == 2 || slot_var == 3 || slot_var == 9 || slot_var == 10 || slot_var == 11 || slot_var == 12 || slot_var == 18 || slot_var == 19 || slot_var == 20 || slot_var == 21)) {
					continue;
				}
				ItemStack i = tradeInv.getItem(slot_var);
				if(i == null || i.getType() == Material.AIR || MerchantMechanics.isTradeButton(i) || i.getType() == Material.BONE) {
					continue;
				}
				if(i.getType() == Material.EMERALD) {
					i = MoneyMechanics.makeGems(MoneyMechanics.getCountMeta(i));
				}
				//closer.getInventory().setItem(closer.getInventory().firstEmpty(), i);
				closer.getInventory().setItem(closer.getInventory().firstEmpty(), i);
				
			}
			
			slot_var = -1;
			
			while(slot_var <= 27) {
				slot_var++;
				if(!(slot_var == 5 || slot_var == 6 || slot_var == 7 || slot_var == 8 || slot_var == 14 || slot_var == 15 || slot_var == 16 || slot_var == 17 || slot_var == 23 || slot_var == 24 || slot_var == 25 || slot_var == 26)) {
					continue;
				}
				ItemStack i = tradeInv.getItem(slot_var);
				if(i == null || i.getType() == Material.AIR || MerchantMechanics.isTradeButton(i) || i.getType() == Material.BONE) {
					continue;
				}
				if(i.getType() == Material.EMERALD) {
					i = MoneyMechanics.makeGems(MoneyMechanics.getCountMeta(i));
				}
				trade_partner.getInventory().setItem(trade_partner.getInventory().firstEmpty(), i);
			}
		}
		
		if(left_side == false) {
			while(slot_var <= 27) {
				slot_var++;
				if(!(slot_var == 0 || slot_var == 1 || slot_var == 2 || slot_var == 3 || slot_var == 9 || slot_var == 10 || slot_var == 11 || slot_var == 12 || slot_var == 18 || slot_var == 19 || slot_var == 20 || slot_var == 21)) {
					continue;
				}
				ItemStack i = tradeInv.getItem(slot_var);
				if(i == null || i.getType() == Material.AIR || MerchantMechanics.isTradeButton(i) || i.getType() == Material.BONE) {
					continue;
				}
				if(i.getType() == Material.EMERALD) {
					i = MoneyMechanics.makeGems(MoneyMechanics.getCountMeta(i));
				}
				trade_partner.getInventory().setItem(trade_partner.getInventory().firstEmpty(), i);
			}
			slot_var = -1;
			
			while(slot_var <= 27) {
				slot_var++;
				if(!(slot_var == 5 || slot_var == 6 || slot_var == 7 || slot_var == 8 || slot_var == 14 || slot_var == 15 || slot_var == 16 || slot_var == 17 || slot_var == 23 || slot_var == 24 || slot_var == 25 || slot_var == 26)) {
					continue;
				}
				ItemStack i = tradeInv.getItem(slot_var);
				if(i == null || i.getType() == Material.AIR || MerchantMechanics.isTradeButton(i) || i.getType() == Material.BONE) {
					continue;
				}
				if(i.getType() == Material.EMERALD) {
					i = MoneyMechanics.makeGems(MoneyMechanics.getCountMeta(i));
				}
				closer.getInventory().setItem(closer.getInventory().firstEmpty(), i);
			}
		}
		
		if(closer.getOpenInventory().getTopInventory().getName().contains(closer.getName())) {
			closer.getOpenInventory().getTopInventory().clear();
		}
		
		if(trade_partner.getOpenInventory().getTopInventory().getName().contains(trade_partner.getName())) {
			trade_partner.getOpenInventory().getTopInventory().clear();
		}
		
		duel_map.remove(closer.getName());
		duel_map.remove(trade_partner.getName());
		
		duel_max_weapon_tier.remove(closer.getName());
		duel_max_armor_tier.remove(closer.getName());
		duel_max_weapon_tier.remove(trade_partner.getName());
		duel_max_armor_tier.remove(trade_partner.getName());
		
		duel_request.remove(closer.getName());
		duel_request.remove(trade_partner.getName());
		
		duel_secure.remove(closer);
		duel_secure.remove(trade_partner);
		
		closer.closeInventory();
		trade_partner.closeInventory();
		
		closer.sendMessage(ChatColor.YELLOW + "Duel cancelled.");
		trade_partner.sendMessage(ChatColor.YELLOW + "Duel cancelled by " + closer.getName() + ".");
		
		trade_partner.updateInventory();
		closer.updateInventory();
	}
	
	@EventHandler
	public void onPlayerRecieveChatEvent(AsyncPlayerChatEvent e) {
		if(e.getMessage().equalsIgnoreCase(ChatColor.DARK_RED.toString() + "You are in a no-PvP area.")) {
			e.setCancelled(true);
			e.setMessage("");
		}
	}
	
	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerDeath(PlayerDeathEvent e) {
		Player p = (Player) e.getEntity();
		if(in_duel_window.contains(p)) {
			p.closeInventory();
		}
		if(in_duel.contains(p)) {
			// They were in a duel, this shouldn't be possible. Save all their items.
			//p.setLevel(50); // Give them a level value so it reconginizes illegit death.
			
			final Player attacker = Bukkit.getPlayer(duel_map.get(p.getName()));
			Player attacked = p;
			restoreColors(attacker, attacked);
			in_duel.remove(attacker);
			in_duel.remove(attacked);
			
			duel_max_weapon_tier.remove(attacker.getName());
			duel_max_armor_tier.remove(attacker.getName());
			duel_max_weapon_tier.remove(attacked.getName());
			duel_max_armor_tier.remove(attacked.getName());
			
			duel_map.remove(attacker.getName());
			duel_map.remove(attacked.getName());
			duel_request_cooldown.put(attacker, 10);
			duel_request_cooldown.put(attacked, 10);
			
			ChatColor attacker_color = ChatMechanics.getPlayerColor(attacker, attacker);
			String attacker_prefix = ChatMechanics.getPlayerPrefix(attacker);
			
			ChatColor attacked_color = ChatMechanics.getPlayerColor(attacked, attacked);
			String attacked_prefix = ChatMechanics.getPlayerPrefix(attacked);
			
			attacker.sendMessage(attacker_color + attacker_prefix + attacker.getName() + ChatColor.GREEN + " has " + ChatColor.UNDERLINE + "KNOCKED OUT" + " " + attacked_color + attacked_prefix + attacked.getName() + ChatColor.GREEN + " in a duel.");
			
			for(Entity ent : attacker.getNearbyEntities(48, 48, 48)) {
				if(ent instanceof Player) {
					Player pl = (Player) ent;
					
					ChatColor pl_attacker_color = ChatMechanics.getPlayerColor(attacker, pl);
					String pl_attacker_prefix = ChatMechanics.getPlayerPrefix(attacker);
					
					ChatColor pl_attacked_color = ChatMechanics.getPlayerColor(attacked, pl);
					String pl_attacked_prefix = ChatMechanics.getPlayerPrefix(attacked);
					
					pl.sendMessage(pl_attacker_color + pl_attacker_prefix + attacker.getName() + ChatColor.GREEN + " has " + ChatColor.UNDERLINE + "KNOCKED OUT" + " " + ChatColor.WHITE + pl_attacked_color + pl_attacked_prefix + attacked.getName() + ChatColor.GREEN + " in a duel.");
				}
			}
			if(duel_stake.containsKey(attacker.getName())) {
				rewardLoot(attacker, attacked);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onPlayerInventoryClick(InventoryClickEvent e) {
		if(!(e.getWhoClicked().getType() == EntityType.PLAYER)) { return; }
		final Player clicker = (Player) e.getWhoClicked();
		if(!(duel_map.containsKey(clicker.getName()))) { return; }
		if(!(in_duel_window.contains(clicker.getName()))) { return; }
		Inventory tradeWin = e.getInventory();
		
		if(e.getCurrentItem() == null) { return; }
		Material m = e.getCurrentItem().getType();
		Material cursor = e.getCursor().getType();
		boolean left_side = false;
		
		if(e.getRawSlot() == 4 || e.getRawSlot() == 13 || e.getRawSlot() == 22 || e.getCurrentItem().getType() == Material.THIN_GLASS || e.getCurrentItem().getType() == Material.BONE) {
			e.setCancelled(true);
			//clicker.sendMessage(ChatColor.RED + "You can't do that.");
			// TODO: Remove Debug message ^
			return;
		}
		
		if(e.getCurrentItem().getType() == Material.NETHER_STAR || e.getCurrentItem().getType() == Material.QUARTZ|| CommunityMechanics.isSocialBook(e.getCurrentItem()) || !(RealmMechanics.isItemTradeable(e.getCursor())) || !(RealmMechanics.isItemTradeable(e.getCurrentItem()))) {
			e.setCancelled(true);
			clicker.sendMessage(ChatColor.RED + "Untradeable Item.");
			return;
		}
		
		// 1.1
		if(isArmorIcon(e.getCurrentItem())) {
			e.setCancelled(true); // Cancel inventory event.
			// Cycle through the possible armor / weapons.
			ItemStack previous = e.getCurrentItem();
			e.setCurrentItem(cycleArmorIcon(previous)); // Cycle the armor icon.
			if(duel_secure.containsKey(clicker)) {
				duel_secure.remove(clicker);
				duel_secure.remove(duel_map.get(clicker.getName()));
				//tradeWin.getItem(0).setDurability((short)8);
				//tradeWin.getItem(8).setDurability((short)8);
				tradeWin.setItem(0, gray_button);
				tradeWin.setItem(8, gray_button);
				clicker.sendMessage(ChatColor.RED + "Duel rules modified, unaccepted.");
				Bukkit.getPlayer(duel_map.get(clicker.getName())).sendMessage(ChatColor.RED + "Duel rules modified by " + clicker.getName() + ", unaccepted.");
				//duel_map.get(clicker).updateInventory();
				clicker.updateInventory();
				Bukkit.getPlayer(duel_map.get(clicker.getName())).updateInventory();
			}
			return;
		}
		
		if(isWeaponIcon(e.getCurrentItem())) {
			e.setCancelled(true); // Cancel inventory event.
			// Cycle through the possible armor / weapons.
			ItemStack previous = e.getCurrentItem();
			e.setCurrentItem(cycleWeaponIcon(previous)); // Cycle the armor icon.
			if(duel_secure.containsKey(clicker)) {
				duel_secure.remove(clicker);
				duel_secure.remove(duel_map.get(clicker.getName()));
				//tradeWin.getItem(0).setDurability((short)8);
				//tradeWin.getItem(8).setDurability((short)8);
				tradeWin.setItem(0, gray_button);
				tradeWin.setItem(8, gray_button);
				clicker.sendMessage(ChatColor.RED + "Duel rules modified, unaccepted.");
				Bukkit.getPlayer(duel_map.get(clicker.getName())).sendMessage(ChatColor.RED + "Duel rules modified by " + clicker.getName() + ", unaccepted.");
				//duel_map.get(clicker).updateInventory();
				clicker.updateInventory();
				Bukkit.getPlayer(duel_map.get(clicker.getName())).updateInventory();
			}
			return;
		}
		
		int slot_num = e.getRawSlot();
		
		if(duel_request.containsKey(clicker.getName())) {
			// Left Side
			left_side = true;
			if(!(e.isShiftClick()) || (e.isShiftClick() && slot_num < 27)) {
				if(!(e.getSlotType() == SlotType.CONTAINER)) { return; }
				if(e.getInventory().getType() == InventoryType.PLAYER) { return; }
				//if(e.getInventory() != clicker.getOpenInventory().getTopInventory()){return;}
				if(slot_num >= 27) { return; }
				if(!(slot_num == 0 || slot_num == 1 || slot_num == 2 || slot_num == 3 || slot_num == 9 || slot_num == 10 || slot_num == 11 || slot_num == 12 || slot_num == 18 || slot_num == 19 || slot_num == 20 || slot_num == 21) && !(slot_num > 27)) {
					e.setCancelled(true);
					tradeWin.setItem(slot_num, tradeWin.getItem(slot_num));
					clicker.updateInventory();
					//clicker.sendMessage(ChatColor.RED + "That item isn't yours!");
					return;
				}
			}
		}
		if(!(duel_request.containsKey(clicker.getName()))) {
			// Right Side
			left_side = false;
			if(!(e.isShiftClick()) || (e.isShiftClick() && slot_num < 27)) {
				if(!(e.getSlotType() == SlotType.CONTAINER)) { return; }
				if(e.getInventory().getType() == InventoryType.PLAYER) { return; }
				if(e.getInventory().getItem(0) == null || e.getInventory().getItem(0).getType() != Material.INK_SACK) { return; }
				//if(e.getInventory() != clicker.getOpenInventory().getTopInventory()){return;}
				if(slot_num >= 27) { return; }
				//if(e.getInventory().getViewers().size() <= 1){return;}
				if(!(slot_num == 5 || slot_num == 6 || slot_num == 7 || slot_num == 8 || slot_num == 14 || slot_num == 15 || slot_num == 16 || slot_num == 17 || slot_num == 23 || slot_num == 24 || slot_num == 25 || slot_num == 26) && !(slot_num > 27)) {
					e.setCancelled(true);
					tradeWin.setItem(slot_num, tradeWin.getItem(slot_num));
					clicker.updateInventory();
					if(MerchantMechanics.isTradeButton(e.getCurrentItem())) {
						clicker.sendMessage(ChatColor.RED + "Wrong button.");
					} else {
						// clicker.sendMessage(ChatColor.RED + "That item isn't yours!");
					}
					return;
				}
			}
		}
		
		if(!(MerchantMechanics.isTradeButton(e.getCurrentItem())) && !(m == Material.AIR && cursor == Material.AIR)) {
			if(duel_secure.containsKey(clicker)) {
				duel_secure.remove(clicker);
				duel_secure.remove(duel_map.get(clicker.getName()));
				//tradeWin.getItem(0).setDurability((short)8);
				//tradeWin.getItem(8).setDurability((short)8);
				tradeWin.setItem(0, gray_button);
				tradeWin.setItem(8, gray_button);
				clicker.sendMessage(ChatColor.RED + "Duel stake modified, unaccepted.");
				Bukkit.getPlayer(duel_map.get(clicker.getName())).sendMessage(ChatColor.RED + "Duel stake modified by " + clicker.getName() + ", unaccepted.");
				//duel_map.get(clicker).updateInventory();
				clicker.updateInventory();
			}
		}
		
		if(e.isShiftClick() && slot_num > 27 && !(e.isCancelled())) {
			e.setCancelled(true);
			ItemStack to_move = e.getCurrentItem();
			int local_to_move_slot = e.getSlot();
			int x = -1;
			if(left_side == true) {
				while(x <= 27) {
					x++;
					if(!(x == 0 || x == 1 || x == 2 || x == 3 || x == 9 || x == 10 || x == 11 || x == 12 || x == 18 || x == 19 || x == 20 || x == 21)) {
						continue;
					}
					ItemStack i = tradeWin.getItem(x);
					if(!(i == null)) {
						continue;
					}
					//log.info("derp");
					tradeWin.setItem(x, to_move);
					//e.getCurrentItem().setType(Material.AIR);
					//e.getCursor().setType(Material.AIR);
					clicker.getInventory().remove(local_to_move_slot);
					clicker.getInventory().setItem(local_to_move_slot, new ItemStack(Material.AIR));
					clicker.updateInventory();
					break;
				}
			}
			if(left_side == false) {
				while(x <= 27) {
					x++;
					if(!(x == 5 || x == 6 || x == 7 || x == 8 || x == 14 || x == 15 || x == 16 || x == 17 || x == 23 || x == 24 || x == 25 || x == 26)) {
						continue;
					}
					ItemStack i = tradeWin.getItem(x);
					if(!(i == null)) {
						continue;
					}
					//log.info("derp2");
					tradeWin.setItem(x, to_move);
					//e.getCurrentItem().setType(Material.AIR);
					//e.getCursor().setType(Material.AIR);
					clicker.getInventory().remove(local_to_move_slot);
					clicker.getInventory().setItem(local_to_move_slot, new ItemStack(Material.AIR));
					clicker.updateInventory();
					break;
				}
			}
		}
		
		if(MerchantMechanics.isTradeButton(e.getCurrentItem())) {
			e.setCancelled(true);
			if(!(clicker.getItemOnCursor() == null || clicker.getItemOnCursor().getType() == Material.AIR)) {
				clicker.updateInventory();
				return;
			}
			if(e.getCurrentItem().getDurability() == 8) { // Gray button
				//e.getCurrentItem().setDurability((short) 10);
				clicker.playSound(clicker.getLocation(), Sound.BLAZE_HIT, 1F, 2.0F);
				e.setCurrentItem(green_button);
				if(tradeWin.getItem(0).getDurability() == (short) 10 && tradeWin.getItem(8).getDurability() == (short) 10) {
					final Player tradie = Bukkit.getPlayer(duel_map.get(clicker.getName()));
					clicker.playSound(clicker.getLocation(), Sound.BLAZE_HIT, 1F, 1.5F);
					tradie.playSound(tradie.getLocation(), Sound.BLAZE_HIT, 1F, 1.5F);
					
					// 1.1
					int max_armor_tier = ItemMechanics.getItemTier(e.getInventory().getItem(30));
					int max_weapon_tier = ItemMechanics.getItemTier(e.getInventory().getItem(32));
					
					if(e.getInventory().getItem(30).getType() == Material.getMaterial(111)) {
						max_armor_tier = 0;
					}
					if(e.getInventory().getItem(32).getType() == Material.getMaterial(397)) {
						max_weapon_tier = 0;
					}
					
					duel_max_weapon_tier.put(clicker.getName(), max_weapon_tier);
					duel_max_weapon_tier.put(tradie.getName(), max_weapon_tier);
					
					duel_max_armor_tier.put(clicker.getName(), max_armor_tier);
					duel_max_armor_tier.put(tradie.getName(), max_armor_tier);
					
					boolean clicker_good = removeIllegalArmor(clicker);
					boolean tradie_good = removeIllegalArmor(tradie);
					
					if(clicker_good && tradie_good) {
						startDuel(clicker, tradie);
					} else {
						if(!clicker_good) {
							clicker.sendMessage(ChatColor.RED + "You do " + ChatColor.UNDERLINE + "NOT" + ChatColor.RED + " have enough space in your inventory to disequip your restricted armor.");
							tradie.sendMessage(ChatColor.RED + "Your opponent does not have enough space in their inventory.");
						}
						if(!tradie_good) {
							tradie.sendMessage(ChatColor.RED + "You do " + ChatColor.UNDERLINE + "NOT" + ChatColor.RED + " have enough space in your inventory to disequip your restricted armor.");
							clicker.sendMessage(ChatColor.RED + "Your opponent does not have enough space in their inventory.");
						}
						
						e.setCurrentItem(gray_button);
						return;
					}
					
					Inventory loot = Bukkit.createInventory(null, 27, "Spoils");
					
					tradeWin.setItem(0, new ItemStack(Material.AIR));
					tradeWin.setItem(4, new ItemStack(Material.AIR));
					tradeWin.setItem(8, new ItemStack(Material.AIR));
					tradeWin.setItem(13, new ItemStack(Material.AIR));
					tradeWin.setItem(22, new ItemStack(Material.AIR));
					
					tradeWin.setItem(30, new ItemStack(Material.AIR));
					tradeWin.setItem(32, new ItemStack(Material.AIR));
					
					for(ItemStack i : tradeWin.getContents()) {
						if(i == null) {
							continue;
						}
						if(!(i.getType() == Material.AIR) && !(i.getType() == Material.BONE) && (!(MerchantMechanics.isTradeButton(i))) && !(i.getDurability() == 8 || i.getDurability() == 10) && !(isArmorIcon(i)) && !(isWeaponIcon(i))) {
							loot.setItem(loot.firstEmpty(), i);
						}
					}
					
					if(loot.getSize() > 0) {
						duel_stake.put(tradie.getName(), loot);
						duel_stake.put(clicker.getName(), loot);
					}
					
					duel_secure.remove(clicker);
					duel_secure.remove(tradie);
					in_duel_window.remove(clicker.getName());
					in_duel_window.remove(tradie.getName());
					
					Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
						public void run() {
							tradie.updateInventory();
							clicker.updateInventory();
							tradie.closeInventory();
							clicker.closeInventory();
						}
					}, 1L);
					
					//tradeWin.clear();
				} else {
					duel_secure.put(clicker, tradeWin);
					duel_secure.put(Bukkit.getPlayer(duel_map.get(clicker.getName())), tradeWin);
					clicker.sendMessage(ChatColor.YELLOW + "Duel accepted, waiting for " + ChatColor.BOLD + duel_map.get(clicker.getName()) + ChatColor.YELLOW + "...");
					Bukkit.getPlayer(duel_map.get(clicker.getName())).sendMessage(ChatColor.GREEN + clicker.getName() + " has accepted the duel stake.");
					Bukkit.getPlayer(duel_map.get(clicker.getName())).sendMessage(ChatColor.GRAY + "Click the gray button (dye) to confirm.");
				}
				
			}
		}
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		Player quitter = e.getPlayer();
		
		if(duel_countdown.containsKey(quitter)) {
			duel_countdown.remove(quitter);
		}
		
		duel_request_cooldown.remove(quitter);
		
		if(duel_map.containsKey(quitter.getName())) {
			final Player opponent = Bukkit.getPlayer(duel_map.get(quitter.getName()));
			opponent.sendMessage(ChatColor.RED + quitter.getName() + " has quit the game, and therefor forfeited the duel.");
			duel_map.remove(quitter.getName());
			duel_map.remove(opponent.getName());
			in_duel.remove(quitter);
			in_duel.remove(opponent);
			duel_stake.remove(quitter.getName());
			duel_countdown.remove(opponent.getName());
			duel_countdown.remove(quitter.getName());
			in_duel_window.remove(quitter.getName());
			in_duel_window.remove(opponent.getName());
			
			if(duel_stake.containsKey(opponent.getName())) {
				rewardLoot(opponent, quitter);
			}
			
			restoreColors(opponent, quitter);
		}
		
		if(duel_request.containsKey(quitter.getName())) {
			String challenged_name = duel_request.get(quitter.getName());
			Player challenged = Bukkit.getPlayer(challenged_name);
			if(challenged != null) {
				challenged.sendMessage(ChatColor.RED + quitter.getName() + " has quit the game, and therefor cancelled his duel request.");
			}
			duel_request.remove(quitter.getName());
			duel_request.remove(challenged_name);
		}
	}
	
	@EventHandler(ignoreCancelled = false, priority = EventPriority.HIGHEST)
	public void ArrowShootManager(EntityShootBowEvent e) {
		if(!(e.isCancelled())) { return; }
		if(!(e.getEntity().getType() == EntityType.PLAYER)) { return; }
		if(!(e.getProjectile().getType() == EntityType.ARROW)) { return; }
		
		Player attacker = (Player) e.getEntity();
		
		if(!(duel_map.containsKey(attacker.getName()))) { return; }
		
		if(duel_countdown.containsKey(attacker.getName())) { return; } // Duel hasn't started yet here.
		
		e.setCancelled(false);
		// Cancel any previously made event cancels... if this doesn't work, just do direct damage lool.
	}
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		if(!(duel_map.containsKey(p.getName()))) { return; }
		Location l = e.getTo();
		//Location safe_loc = e.getFrom(); TODO - UNUSED
		Location duel_start_area = duel_start_location.get(p.getName());
		
		/*if(!isDamageDisabled(l)){
			// Entering a chaotic zone -- stop them!
			p.sendMessage(ChatColor.RED + "You cannot enter " + ChatColor.UNDERLINE + "damage enabled" + ChatColor.RED + " zones while in a duel.");
			if(l.getWorld().getName().equalsIgnoreCase(duel_start_area.getWorld().getName())){
				e.setCancelled(true);
				p.teleport(safe_loc);
			}
			return;
		}*/
		
		if(!l.getWorld().getName().equalsIgnoreCase(duel_start_area.getWorld().getName()) || ((l.distanceSquared(duel_start_area) >= 2500) && !(p.getLocation().subtract(0, 1, 0).getBlock().getType() == Material.AIR))) { // 50 blocks.
			if(!warned_players.contains(p.getName())) {
				p.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "WARNING:" + ChatColor.RED + " You are too far from the DUEL START POINT, please turn back or you will " + ChatColor.UNDERLINE + "FORFEIT.");
				warned_players.add(p.getName());
			}
			/*if(l.getWorld().getName().equalsIgnoreCase(duel_start_area.getWorld().getName())){
				e.setCancelled(true);
				p.teleport(safe_loc);
			}*/
		}
		if(!l.getWorld().getName().equalsIgnoreCase(duel_start_area.getWorld().getName()) || l.distanceSquared(duel_start_area) >= 3600 || !DuelMechanics.isDamageDisabled(l)) {
			if(DuelMechanics.isDamageDisabled(l)) {
				p.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "You have FORFEITED the duel due to entering a wilderness / chaotic zone.");
			} else {
				p.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "You have FORFEITED the duel due to moving too far from the starting point.");
			}
			Player attacked = p;
			Player attacker = Bukkit.getPlayer(duel_map.get(p.getName()));
			duel_request_cooldown.put(attacker, 10);
			duel_request_cooldown.put(attacked, 10);
			duel_map.remove(attacker.getName());
			duel_map.remove(attacked.getName());
			in_duel.remove(attacker);
			in_duel.remove(attacked);
			restoreColors(attacker, attacked);
			
			ChatColor attacker_color = ChatMechanics.getPlayerColor(attacker, attacker);
			String attacker_prefix = ChatMechanics.getPlayerPrefix(attacker);
			
			ChatColor attacked_color = ChatMechanics.getPlayerColor(attacked, attacked);
			String attacked_prefix = ChatMechanics.getPlayerPrefix(attacked);
			
			RecordMechanics.incrementDuelStats(attacker.getName(), true);
			RecordMechanics.incrementDuelStats(attacked.getName(), true);
			
			attacker.sendMessage(attacker_color + attacker_prefix + attacker.getName() + ChatColor.GREEN + " has " + ChatColor.UNDERLINE + "DEFEATED" + ChatColor.RESET + " " + attacked_color + attacked_prefix + attacked.getName() + ChatColor.GREEN + " in a duel.");
			for(Entity ent : attacker.getNearbyEntities(48, 48, 48)) {
				if(ent instanceof Player) {
					Player pl = (Player) ent;
					
					ChatColor pl_attacker_color = ChatMechanics.getPlayerColor(attacker, pl);
					String pl_attacker_prefix = ChatMechanics.getPlayerPrefix(attacker);
					
					ChatColor pl_attacked_color = ChatMechanics.getPlayerColor(attacked, pl);
					String pl_attacked_prefix = ChatMechanics.getPlayerPrefix(attacked);
					
					pl.sendMessage(pl_attacker_color + pl_attacker_prefix + attacker.getName() + ChatColor.GREEN + " has " + ChatColor.UNDERLINE + "DEFEATED" + ChatColor.RESET + " " + pl_attacked_color + pl_attacked_prefix + attacked.getName() + ChatColor.GREEN + " in a duel.");
				}
			}
			
			attacked.closeInventory();
			attacker.closeInventory();
			
			if(duel_stake.containsKey(attacker.getName())) {
				rewardLoot(attacker, attacked);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.LOW)
	public void restrictWeapons(EntityDamageByEntityEvent e) {
		if(e.getDamager() instanceof Player) {
			Player pl = (Player) e.getDamager();
			if(pl.getItemInHand() == null || pl.getItemInHand().getType() == Material.AIR) { return; }
			
			if(duel_max_weapon_tier.containsKey(pl.getName()) && duel_map.containsKey(pl.getName())) {
				int weapon_tier = ItemMechanics.getItemTier(pl.getItemInHand());
				int max_weapon_tier = duel_max_weapon_tier.get(pl.getName());
				if(weapon_tier > max_weapon_tier) {
					e.setDamage(0);
					e.setCancelled(true);
					pl.sendMessage(ChatColor.RED + "You " + ChatColor.UNDERLINE + "cannot" + ChatColor.RED + " use this weapon in this duel.");
				}
			}
		}
		
		if(e.getDamager() instanceof Projectile) {
			Projectile pj = (Projectile) e.getDamager();
			if(pj.getShooter() instanceof Player) {
				Player pl = (Player) pj.getShooter();
				if(duel_max_weapon_tier.containsKey(pl.getName()) && duel_map.containsKey(pl.getName())) {
					int weapon_tier = ItemMechanics.getItemTier(pl.getItemInHand());
					if(ItemMechanics.projectile_map.containsKey(pj)) {
						weapon_tier = ItemMechanics.getItemTier(ItemMechanics.projectile_map.get(pj));
					}
					int max_weapon_tier = duel_max_weapon_tier.get(pl.getName());
					if(weapon_tier > max_weapon_tier) {
						e.setDamage(0);
						e.setCancelled(true);
						pl.sendMessage(ChatColor.RED + "You " + ChatColor.UNDERLINE + "cannot" + ChatColor.RED + " use this weapon in this duel.");
					}
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityShootBow(EntityShootBowEvent e) {
		if(e.getEntity() instanceof Player) {
			Player pl = (Player) e.getEntity();
			if(duel_max_weapon_tier.containsKey(pl.getName()) && duel_map.containsKey(pl.getName())) {
				int weapon_tier = ItemMechanics.getItemTier(pl.getItemInHand());
				int max_weapon_tier = duel_max_weapon_tier.get(pl.getName());
				if(weapon_tier > max_weapon_tier) {
					e.setCancelled(true);
					pl.sendMessage(ChatColor.RED + "You " + ChatColor.UNDERLINE + "cannot" + ChatColor.RED + " use this weapon in this duel.");
				}
			}
		}
	}
	
	@EventHandler(ignoreCancelled = false, priority = EventPriority.HIGHEST)
	public void DuelManager(EntityDamageEvent e) {
		if(!(e.getEntity().getType() == EntityType.PLAYER)) { return; }
		if(e.getEntity().hasMetadata("NPC")) {
			e.setCancelled(true);
			return;
		}
		
		final Player attacked = (Player) e.getEntity();
		
		if(!(duel_map.containsKey(attacked.getName()))) { return; }
		
		Entity ent_attacker = null;
		
		if(!(e instanceof EntityDamageByEntityEvent)) { return; // Nothing to do here.
		}
		
		ent_attacker = ((EntityDamageByEntityEvent) e).getDamager();
		if(ent_attacker instanceof Arrow) {
			ent_attacker = (Entity) ((Arrow) ent_attacker).getShooter();
		}
		
		if(!(ent_attacker.getType() == EntityType.PLAYER)) { return; }
		
		final Player attacker = (Player) ent_attacker;
		
		if(in_duel_window.contains(attacked.getName())) {
			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
				public void run() {
					attacked.updateInventory();
					attacked.closeInventory();
					in_duel_window.remove(attacked.getName());
				}
			}, 1L);
			String partner = null;
			if(duel_map.containsKey(attacked.getName())) {
				partner = duel_map.get(attacked.getName());
				duel_map.remove(partner);
				duel_map.remove(attacked.getName());
				restoreColors(attacker, attacked);
			}
			if(duel_start_location.containsKey(attacked.getName())) {
				duel_start_location.remove(attacked.getName());
				if(partner != null) {
					duel_start_location.remove(partner);
				}
			}
			
			attacked.sendMessage(ChatColor.RED + "Damage taken before duel was accepted - " + ChatColor.BOLD + "DUEL CANCELLED");
			if(partner != null && Bukkit.getPlayer(partner) != null) {
				final Player p_partner = Bukkit.getPlayer(partner);
				p_partner.sendMessage(ChatColor.RED + "Your opponent took damage before the duel was accepted - " + ChatColor.BOLD + "DUEL CANCELLED");
				Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
					public void run() {
						p_partner.closeInventory();
						p_partner.updateInventory();
						in_duel_window.remove(p_partner.getName());
					}
				}, 1L);
			}
			
			e.setCancelled(true);
			e.setDamage(0);
			return;
		}
		
		if(!(e.getCause() == DamageCause.ENTITY_ATTACK) && !(e.getCause() == DamageCause.PROJECTILE)) {
			// Fire, fall, etc damage.
			if(HealthMechanics.getPlayerHP(attacked.getName()) - e.getDamage() <= 1) {
				String attacker_name = duel_map.get(attacked.getName());
				
				Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
					public void run() {
						attacked.updateInventory();
						attacked.closeInventory();
					}
				}, 1L);
				
				// They would die from this, save them, cancel duel.
				RealmMechanics.player_god_mode.put(attacked.getName(), System.currentTimeMillis());
				attacked.setFireTicks(0);
				attacked.setFallDistance(0.0F);
				attacked.removePotionEffect(PotionEffectType.POISON);
				attacked.setHealth(1);
				HealthMechanics.setPlayerHP(attacked.getName(), 1);
				//attacked.setLevel(1);
				Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
					public void run() {
						RealmMechanics.player_god_mode.remove(attacked.getName());
					}
				}, 3 * 20L);
				
				if(Bukkit.getPlayer(attacker_name) != null) {
					restoreColors(attacker, attacked);
					duel_request_cooldown.put(attacker, 10);
					duel_map.remove(attacker.getName());
					duel_max_weapon_tier.remove(attacker.getName());
					duel_max_armor_tier.remove(attacker.getName());
					in_duel.remove(attacker);
					
					ChatColor attacker_color = ChatMechanics.getPlayerColor(attacker, attacker);
					String attacker_prefix = ChatMechanics.getPlayerPrefix(attacker);
					
					ChatColor attacked_color = ChatMechanics.getPlayerColor(attacked, attacked);
					String attacked_prefix = ChatMechanics.getPlayerPrefix(attacked);
					
					RecordMechanics.incrementDuelStats(attacker.getName(), true);
					RecordMechanics.incrementDuelStats(attacked.getName(), true);
					
					attacker.sendMessage(attacker_color + attacker_prefix + attacker.getName() + ChatColor.GREEN + " has " + ChatColor.UNDERLINE + "DEFEATED" + ChatColor.RESET + " " + attacked_color + attacked_prefix + attacked.getName() + ChatColor.GREEN + " in a duel.");
					
					if(duel_stake.containsKey(attacker.getName())) {
						rewardLoot(attacker, attacked);
					}
				}
				
				duel_request_cooldown.put(attacked, 10);
				duel_map.remove(attacked.getName());
				duel_max_weapon_tier.remove(attacked.getName());
				duel_max_armor_tier.remove(attacked.getName());
				in_duel.remove(attacked);
				
				for(Entity ent : attacker.getNearbyEntities(48, 48, 48)) {
					if(ent instanceof Player) {
						Player pl = (Player) ent;
						
						ChatColor pl_attacker_color = ChatMechanics.getPlayerColor(attacker, pl);
						String pl_attacker_prefix = ChatMechanics.getPlayerPrefix(attacker);
						
						ChatColor pl_attacked_color = ChatMechanics.getPlayerColor(attacked, pl);
						String pl_attacked_prefix = ChatMechanics.getPlayerPrefix(attacked);
						
						pl.sendMessage(pl_attacker_color + pl_attacker_prefix + attacker.getName() + ChatColor.GREEN + " has " + ChatColor.UNDERLINE + "DEFEATED" + ChatColor.RESET + " " + pl_attacked_color + pl_attacked_prefix + attacked.getName() + ChatColor.GREEN + " in a duel.");
					}
				}
				
				e.setCancelled(true);
				e.setDamage(0);
				return;
			}
			
			return;
		}
		
		if(!(duel_map.containsKey(attacker.getName())) || !(duel_map.containsKey(attacked.getName()))) { return; }
		if(duel_request.containsKey(attacker.getName()) || duel_request.containsKey(attacked.getName())) {
			e.setDamage(0);
			return;
		}
		
		if((duel_countdown.containsKey(attacker) || duel_countdown.containsKey(attacked))) {
			attacker.sendMessage(ChatColor.RED + "The duel has " + ChatColor.BOLD + "NOT" + ChatColor.RED + " started.");
			e.setDamage(0);
			return;
		} // Duel hasn't started yet here.
		
		if(duel_map.get(attacker.getName()).equalsIgnoreCase(attacked.getName())) {
			if((HealthMechanics.getPlayerHP(attacked.getName()) - e.getDamage() <= 1)) {
				RealmMechanics.player_god_mode.put(attacked.getName(), System.currentTimeMillis());
				attacked.setFireTicks(0);
				attacked.setFallDistance(0.0F);
				attacked.removePotionEffect(PotionEffectType.POISON);
				attacked.setHealth(10);
				HealthMechanics.setPlayerHP(attacked.getName(), 10);
				//attacked.setLevel(1);
				Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
					public void run() {
						RealmMechanics.player_god_mode.remove(attacked.getName());
					}
				}, 3 * 20L);
				
				restoreColors(attacker, attacked);
				duel_request_cooldown.put(attacker, 10);
				duel_request_cooldown.put(attacked, 10);
				duel_map.remove(attacker.getName());
				duel_map.remove(attacked.getName());
				duel_max_weapon_tier.remove(attacker.getName());
				duel_max_armor_tier.remove(attacker.getName());
				duel_max_weapon_tier.remove(attacked.getName());
				duel_max_armor_tier.remove(attacked.getName());
				in_duel.remove(attacker);
				in_duel.remove(attacked);
				
				ChatColor attacker_color = ChatMechanics.getPlayerColor(attacker, attacker);
				String attacker_prefix = ChatMechanics.getPlayerPrefix(attacker);
				
				ChatColor attacked_color = ChatMechanics.getPlayerColor(attacked, attacked);
				String attacked_prefix = ChatMechanics.getPlayerPrefix(attacked);
				
				RecordMechanics.incrementDuelStats(attacker.getName(), true);
				//They lost
				RecordMechanics.incrementDuelStats(attacked.getName(), false);
				
				attacker.sendMessage(attacker_color + attacker_prefix + attacker.getName() + ChatColor.GREEN + " has " + ChatColor.UNDERLINE + "DEFEATED" + ChatColor.RESET + " " + attacked_color + attacked_prefix + attacked.getName() + ChatColor.GREEN + " in a duel.");
				for(Entity ent : attacker.getNearbyEntities(48, 48, 48)) {
					if(ent instanceof Player) {
						Player pl = (Player) ent;
						
						ChatColor pl_attacker_color = ChatMechanics.getPlayerColor(attacker, pl);
						String pl_attacker_prefix = ChatMechanics.getPlayerPrefix(attacker);
						
						ChatColor pl_attacked_color = ChatMechanics.getPlayerColor(attacked, pl);
						String pl_attacked_prefix = ChatMechanics.getPlayerPrefix(attacked);
						
						pl.sendMessage(pl_attacker_color + pl_attacker_prefix + attacker.getName() + ChatColor.GREEN + " has " + ChatColor.UNDERLINE + "DEFEATED" + ChatColor.RESET + " " + pl_attacked_color + pl_attacked_prefix + attacked.getName() + ChatColor.GREEN + " in a duel.");
					}
				}
				
				if(duel_stake.containsKey(attacker.getName())) {
					rewardLoot(attacker, attacked);
				}
				e.setCancelled(true);
				System.out.print("DUEL > DMG: " + e.getDamage() + " Winner: " + attacker.getName() + " Loser: " + attacked.getName() + " Losers HP:" + attacked.getHealth() + " HP: " + HealthMechanics.getPlayerHP(attacked.getName()));
				e.setDamage(0);
				return;
			}
			
			e.setCancelled(true);
			return;
			// Cancel any previously made event cancels... if this doesn't work, just do direct damage lool.
		}
		
		e.setCancelled(true);
		e.setDamage(0);
	}
	
	public void rewardLoot(final Player winner, Player looser) {
		Inventory loot = duel_stake.get(winner.getName());
		
		for(ItemStack is : loot.getContents()) {
			if(is == null) {
				continue;
			}
			if(PetMechanics.isPermUntradeable(is) || !(RealmMechanics.isItemTradeable(is))) {
				loot.remove(is);
			}
		}
		
		Player new_winner = Bukkit.getPlayer(winner.getName());
		new_winner.openInventory(loot);
		new_winner.sendMessage(ChatColor.YELLOW + "Loot menu opened.");
		
		duel_stake.remove(winner.getName());
		duel_stake.remove(looser.getName());
	}
	
	public boolean InWGRegion(Player p) {
		try {
			Class<?> bukkitUtil = wg.getClass().getClassLoader().loadClass("com.sk89q.worldguard.bukkit.BukkitUtil");
			Method toVector = bukkitUtil.getMethod("toVector", Block.class);
			Vector blockVector = (Vector) toVector.invoke(null, p.getLocation().getBlock());
			
			List<String> regionSet = wg.getGlobalRegionManager().get(p.getWorld()).getApplicableRegionsIDs(blockVector);
			return regionSet.size() > 0;
		} catch(Exception e) {}
		return false;
	}
	
	public static boolean isRestrictedArea(Location l) {
		try {
			Class<?> bukkitUtil = wg.getClass().getClassLoader().loadClass("com.sk89q.worldguard.bukkit.BukkitUtil");
			Method toVector = bukkitUtil.getMethod("toVector", Block.class);
			Vector blockVector = (Vector) toVector.invoke(null, l.getBlock());
			
			List<String> regionSet = wg.getGlobalRegionManager().get(l.getWorld()).getApplicableRegionsIDs(blockVector);
			
			if(regionSet.size() < 1) { return false; }
			
			for(String region : regionSet) {
				if(wg.getGlobalRegionManager().get(l.getWorld()).getRegion(region).getFlags().containsKey(DefaultFlag.ENTRY)) {
					State entry_flag = (State) wg.getGlobalRegionManager().get(l.getWorld()).getRegion(region).getFlags().get(DefaultFlag.ENTRY);
					if(entry_flag != State.ALLOW) { return true; }
				} else {
					continue;
				}
			}
		} catch(Exception e) {
			
		}
		return false;
	}
	
	public static boolean isPvPDisabled(Player p) {
		try {
			Class<?> bukkitUtil = wg.getClass().getClassLoader().loadClass("com.sk89q.worldguard.bukkit.BukkitUtil");
			Method toVector = bukkitUtil.getMethod("toVector", Block.class);
			Vector blockVector = (Vector) toVector.invoke(null, p.getLocation().getBlock());
			
			List<String> regionSet = wg.getGlobalRegionManager().get(p.getWorld()).getApplicableRegionsIDs(blockVector);
			
			if(regionSet.size() < 1) { return false; }
			
			boolean return_flag = false;
			int return_priority = -1;
			
			for(String region : regionSet) {
				if(wg.getGlobalRegionManager().get(p.getWorld()).getRegion(region).getFlags().containsKey(DefaultFlag.PVP)) {
					State pvp_flag = (State) wg.getGlobalRegionManager().get(p.getWorld()).getRegion(region).getFlags().get(DefaultFlag.PVP);
					int region_priority = wg.getGlobalRegionManager().get(p.getWorld()).getRegion(region).getPriority();
					
					if(return_priority == -1) {
						return_flag = (pvp_flag != State.ALLOW);
						return_priority = region_priority;
						continue;
					}
					if(region_priority > return_priority) {
						return_flag = (pvp_flag != State.ALLOW);
						return_priority = region_priority;
					}
				} else {
					continue;
				}
			}
			
			return return_flag;
		} catch(Exception e) {
			
		}
		return false;
	}
	
	public static void setPvPOff(final World w) {
		new BukkitRunnable() {
			@Override
			public void run() {
				//int dimensions = RealmMechanics.getRealmSizeDimensions(RealmMechanics.getRealmTier(w.getName())); // TODO - UNUSED
				if(wg.getGlobalRegionManager().get(w).hasRegion(w.getName() + "-" + "realm")) { return; }
				BlockVector loc1 = new Vector(0, 0, 0).toBlockVector();
				BlockVector loc2 = new Vector(128 + 18, 256, 128 + 18).toBlockVector();
				ProtectedRegion pr = new ProtectedCuboidRegion(w.getName() + "-" + "realm", loc1, loc2);
				pr.setFlag(DefaultFlag.PVP, State.DENY);
				pr.setFlag(DefaultFlag.MOB_DAMAGE, State.DENY);
				pr.setFlag(DefaultFlag.CHEST_ACCESS, State.ALLOW);
				pr.setFlag(DefaultFlag.USE, State.ALLOW);
				pr.setFlag(DefaultFlag.BUILD, State.ALLOW);
				wg.getGlobalRegionManager().get(w).addRegion(pr);
			}
		}.runTaskLaterAsynchronously(Main.plugin, 1L);
		
	}
	
	public static void setPvPOn(World w) {
		if(wg.getGlobalRegionManager().get(w).hasRegion("realm")) {
			wg.getGlobalRegionManager().get(w).removeRegion("realm");
		}
	}
	
	public static boolean isDamageDisabled(Location l) {
		try {
			Class<?> bukkitUtil = wg.getClass().getClassLoader().loadClass("com.sk89q.worldguard.bukkit.BukkitUtil");
			Method toVector = bukkitUtil.getMethod("toVector", Block.class);
			Vector blockVector = (Vector) toVector.invoke(null, l.getBlock());
			
			List<String> regionSet = wg.getGlobalRegionManager().get(l.getWorld()).getApplicableRegionsIDs(blockVector);
			
			if(regionSet.size() < 1) { return false; }
			
			boolean return_flag = false;
			int return_priority = -1;
			
			for(String region : regionSet) {
				if(wg.getGlobalRegionManager().get(l.getWorld()).getRegion(region).getFlags().containsKey(DefaultFlag.MOB_DAMAGE)) {
					State mob_dmg_flag = (State) wg.getGlobalRegionManager().get(l.getWorld()).getRegion(region).getFlags().get(DefaultFlag.MOB_DAMAGE);
					int region_priority = wg.getGlobalRegionManager().get(l.getWorld()).getRegion(region).getPriority();
					
					if(return_priority == -1) {
						return_flag = (mob_dmg_flag != State.ALLOW);
						return_priority = region_priority;
						continue;
					}
					if(region_priority > return_priority) {
						return_flag = (mob_dmg_flag != State.ALLOW);
						return_priority = region_priority;
					}
				} else {
					continue;
				}
			}
			
			return return_flag;
		} catch(Exception e) {
			
		}
		return false;
	}
	
	public static boolean isPvPDisabled(Location l) {
		try {
			Class<?> bukkitUtil = wg.getClass().getClassLoader().loadClass("com.sk89q.worldguard.bukkit.BukkitUtil");
			Method toVector = bukkitUtil.getMethod("toVector", Block.class);
			Vector blockVector = (Vector) toVector.invoke(null, l.getBlock());
			
			if(l == null || l.getWorld() == null) { return true; }
			
			List<String> regionSet = wg.getGlobalRegionManager().get(l.getWorld()).getApplicableRegionsIDs(blockVector);
			
			if(regionSet.size() < 1) { return false; }
			
			boolean return_flag = false;
			int return_priority = -1;
			
			for(String region : regionSet) {
				if(wg.getGlobalRegionManager().get(l.getWorld()).getRegion(region).getFlags().containsKey(DefaultFlag.PVP)) {
					State pvp_flag = (State) wg.getGlobalRegionManager().get(l.getWorld()).getRegion(region).getFlags().get(DefaultFlag.PVP);
					int region_priority = wg.getGlobalRegionManager().get(l.getWorld()).getRegion(region).getPriority();
					
					if(return_priority == -1) {
						return_flag = (pvp_flag != State.ALLOW);
						return_priority = region_priority;
						continue;
					}
					if(region_priority > return_priority) {
						return_flag = (pvp_flag != State.ALLOW);
						return_priority = region_priority;
					}
				} else {
					continue;
				}
			}
			
			return return_flag;
			
		} catch(Exception e) {
			
		}
		return false;
	}
	
	public static String getRegionName(Location l) {
		
		try {
			ApplicableRegionSet set = wg.getRegionManager(l.getWorld()).getApplicableRegions(l);
			if(set.size() == 0) return "";
			
			String returning = "";
			int priority = -1;
			for(ProtectedRegion s : set) {
				if(s.getPriority() > priority) {
					if(!s.getId().equals("")) {
						returning = s.getId();
						priority = s.getPriority();
					}
				}
			}
			
			return returning;
			
		} catch(Exception e) {
			Main.d("Region error!", CC.RED);
		}
		return "";
	}
	
	@EventHandler(ignoreCancelled = false, priority = EventPriority.MONITOR)
	public void DuelChallengeDetector(EntityDamageEvent e) {
		if(!(e.isCancelled())) { return; }
		if(!(e.getEntity().getType() == EntityType.PLAYER)) { return; }
		if(!(e.getCause() == DamageCause.ENTITY_ATTACK)) { return; }
		if(!(e instanceof EntityDamageByEntityEvent)) { return; }
		Entity ent_attacker = ((EntityDamageByEntityEvent) e).getDamager();
		if(!(ent_attacker.getType() == EntityType.PLAYER)) { return; }
		
		Player attacker = (Player) ent_attacker;
		Player attacked = (Player) e.getEntity();
		
		if(TutorialMechanics.onTutorialIsland(attacker) || TutorialMechanics.onTutorialIsland(attacked)) { return; // No dueling on tutorial island!
		}
		
		if(((EntityDamageByEntityEvent) e).getDamager() == null) { return; }
		
		/*if(HealthMechanics.in_combat.containsKey(attacker.getName())){
			return;
		}*/
		
		if(e.getEntity().hasMetadata("NPC")) { return; }
		
		if(!isPvPDisabled(attacked)) { return; }
		
		if(CommunityMechanics.isSocialBook(attacker.getItemInHand())) { return; }
		
		if(attacker.getItemInHand().getType() == Material.NETHER_STAR || attacker.getItemInHand().getType() == Material.QUARTZ) { return; }
		
		if(duel_map.containsKey(attacked.getName())) {
			if(!(duel_map.get(attacked.getName())).equalsIgnoreCase(attacker.getName())) {
				attacker.sendMessage(ChatColor.YELLOW + attacked.getName() + " is already in a duel.");
				return;
			}
			return;
		}
		
		if(duel_map.containsKey(attacker.getName())) {
			attacker.sendMessage(ChatColor.RED + "That is not your opponent!");
			return;
		}
		
		if(duel_request_cooldown.containsKey(attacker)) {
			if(!(duel_request.containsKey(attacked.getName()))) {
				attacker.sendMessage(ChatColor.YELLOW + "Please wait " + ChatColor.BOLD + duel_request_cooldown.get(attacker) + "s" + ChatColor.YELLOW + " before issuing another duel request.");
			}
			if(duel_request.containsKey(attacker.getName())) {
				attacker.sendMessage(ChatColor.GRAY + "You have a Pending Challenge vs. " + ChatColor.BOLD + duel_request.get(attacker.getName()) + "");
			}
			return;
		}
		
		if(duel_request.containsKey(attacked.getName())) {
			// TODO: Overwrite duel request. If request exists, start duel.
			if(duel_request.get(attacked.getName()).equalsIgnoreCase(attacker.getName())) {
				
				if(attacked.getOpenInventory().getTopInventory().getName().startsWith("Bank Chest") || attacked.getOpenInventory().getTopInventory().getName().contains("@") || attacked.getOpenInventory().getTopInventory().getName().equalsIgnoreCase("Loot Chest") || attacked.getOpenInventory().getTopInventory().getName().equalsIgnoreCase("Collection Bin") || attacked.getOpenInventory().getTopInventory().getName().equalsIgnoreCase("Chest") || attacked.getOpenInventory().getTopInventory().getName().equalsIgnoreCase("Realm Material Store") || attacked.getOpenInventory().getTopInventory().getName().contains("     ") || attacked.getOpenInventory().getTopInventory().getName().contains("container.chest") || attacked.getOpenInventory().getTopInventory().getName().contains("container.bigchest") || ShopMechanics.current_item_being_bought.containsKey(attacked.getName()) || ShopMechanics.current_item_being_stocked.containsKey(attacked.getName()) || RealmMechanics.current_item_being_bought.containsKey(attacked.getName()) || MoneyMechanics.split_map.containsKey(attacked) || RestrictionMechanics.in_inventory.contains(attacked.getName())) {
					attacker.sendMessage(ChatColor.YELLOW + attacked.getName() + " is currently busy.");
					e.setCancelled(true);
					return;
				}
				
				// TODO: Start duel.
				e.setCancelled(true);
				e.setDamage(0);
				loadDuelMenu(attacker, attacked);
				//startDuel(attacker, attacked);
				return;
			} else {
				attacker.sendMessage(ChatColor.YELLOW + attacked.getName() + " already has a pending duel request from another user.");
				return;
				//duel_request.remove(attacker.getName());
				// TODO: Do nothing, let it get to end of function to send new request.
			}
		}
		
		/*if(duel_request.containsKey(attacker.getName())){
			// TODO: Cancel previous request, make a new one with new opponent.
			attacker.sendMessage(ChatColor.YELLOW + "You have a pending duel request against " + duel_request.get(attacker.getName()) + ".");
			//attacker.sendMessage(ChatColor.GRAY + "Wait ~30 seconds for this request to expire.");
			return;
		}*/
		
		if(CommunityMechanics.toggle_list.get(attacker.getName()).contains("duel") || CommunityMechanics.toggle_list.get(attacked.getName()).contains("duel") || CommunityMechanics.isPlayerOnIgnoreList(attacked, attacker.getName()) || CommunityMechanics.isPlayerOnIgnoreList(attacker, attacked.getName())) {
			if(CommunityMechanics.toggle_list.get(attacker.getName()).contains("duel")) {
				attacker.sendMessage(ChatColor.RED + "You currently have dueling requests " + ChatColor.UNDERLINE + "DISABLED." + ChatColor.RED + " To re-enable dueling, type '/toggleduel'");
				return;
			}
			if(CommunityMechanics.toggle_list.get(attacked.getName()).contains("duel")) {
				attacker.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + attacked.getName() + ChatColor.RED + " has dueling " + ChatColor.UNDERLINE + "DISABLED.");
				return;
			}
			// At this point, the attacked or attacker is on ignore.
			attacker.sendMessage(ChatColor.YELLOW + attacked.getName() + " has dueling " + ChatColor.UNDERLINE + "DISABLED.");
			return;
		}
		
		if(DuelMechanics.isPvPDisabled(attacked.getLocation()) && !DuelMechanics.isDamageDisabled(attacked.getLocation())) {
			//if(!(attacker.isSneaking())){
			return;
			//}
		} // PvP-OFF DMG-ON
		
		duel_request.put(attacker.getName(), attacked.getName());
		duel_request_cooldown.put(attacker, 5);
		
		attacker.sendMessage(ChatColor.YELLOW + "Duel request sent to " + ChatColor.BOLD + attacked.getName() + ChatColor.YELLOW + ".");
		attacked.sendMessage(ChatColor.YELLOW + "Duel request recieved from " + ChatColor.BOLD + attacker.getName() + ChatColor.YELLOW + ", to accept, hit them back.");
		
	}
}
