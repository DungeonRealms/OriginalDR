package me.vaqxine.MerchantMechanics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

import me.vaqxine.Main;
import me.vaqxine.AchievmentMechanics.AchievmentMechanics;
import me.vaqxine.DonationMechanics.DonationMechanics;
import me.vaqxine.DuelMechanics.DuelMechanics;
import me.vaqxine.EcashMechanics.EcashMechanics;
import me.vaqxine.EnchantMechanics.EnchantMechanics;
import me.vaqxine.GuildMechanics.GuildMechanics;
import me.vaqxine.HealthMechanics.HealthMechanics;
import me.vaqxine.Hive.Hive;
import me.vaqxine.Hive.ParticleEffect;
import me.vaqxine.InstanceMechanics.InstanceMechanics;
import me.vaqxine.ItemMechanics.ItemMechanics;
import me.vaqxine.MoneyMechanics.MoneyMechanics;
import me.vaqxine.MountMechanics.MountMechanics;
import me.vaqxine.PermissionMechanics.PermissionMechanics;
import me.vaqxine.ProfessionMechanics.ProfessionMechanics;
import me.vaqxine.RealmMechanics.RealmMechanics;
import me.vaqxine.RepairMechanics.RepairMechanics;
import me.vaqxine.ShopMechanics.ShopMechanics;
import me.vaqxine.TradeMechanics.TradeMechanics;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_7_R2.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

public class MerchantMechanics implements Listener {
	static Logger log = Logger.getLogger("Minecraft");
	
	public static List<String> in_npc_shop = new ArrayList<String>();
	
	ItemStack divider = new ItemStack(Material.THIN_GLASS, 1);
	
	public static ItemStack T1_scrap = ItemMechanics.signNewCustomItem(Material.LEATHER, (short) 0, ChatColor.WHITE.toString() + "Leather Armor Scrap", ChatColor.GRAY.toString() + "Recovers 3% Durability of " + ChatColor.WHITE.toString() + "Leather Equipment");
	public static ItemStack T2_scrap = ItemMechanics.signNewCustomItem(Material.IRON_FENCE, (short) 0, ChatColor.GREEN.toString() + "Chainmail Armor Scrap", ChatColor.GRAY.toString() + "Recovers 3% Durability of " + ChatColor.GREEN.toString() + "Chainmail Equipment");
	public static ItemStack T3_scrap = ItemMechanics.signNewCustomItem(Material.INK_SACK, (short) 7, ChatColor.AQUA.toString() + "Iron Armor Scrap", ChatColor.GRAY.toString() + "Recovers 3% Durability of " + ChatColor.AQUA.toString() + "Iron Equipment");
	public static ItemStack T4_scrap = ItemMechanics.signNewCustomItem(Material.INK_SACK, (short) 12, ChatColor.LIGHT_PURPLE.toString() + "Diamond Armor Scrap", ChatColor.GRAY.toString() + "Recovers 3% Durability of " + ChatColor.LIGHT_PURPLE.toString() + "Diamond Equipment");
	public static ItemStack T5_scrap = ItemMechanics.signNewCustomItem(Material.INK_SACK, (short) 11, ChatColor.YELLOW.toString() + "Golden Armor Scrap", ChatColor.GRAY.toString() + "Recovers 3% Durability of " + ChatColor.YELLOW.toString() + "Gold Equipment");
	
	public static ItemStack pickaxe_scroll = ItemMechanics.signNewCustomItem(Material.EMPTY_MAP, (short) 1, ChatColor.WHITE.toString() + "" + ChatColor.BOLD.toString() + "Scroll:" + ChatColor.YELLOW.toString() + " Pickaxe Enchant", ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + "Imbues a pickaxe with special attribute(s).");
	
	public static ItemStack fishing_scroll = ItemMechanics.signNewCustomItem(Material.EMPTY_MAP, (short) 1, ChatColor.WHITE.toString() + "" + ChatColor.BOLD.toString() + "Scroll:" + ChatColor.YELLOW.toString() + " Fishingrod Enchant", ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + "Imbues a fishingrod with special attribute(s).");
	
	public static ItemStack orb_of_alteration = ItemMechanics.signNewCustomItem(Material.MAGMA_CREAM, (short) 0, ChatColor.LIGHT_PURPLE.toString() + "Orb of Alteration", ChatColor.GRAY.toString() + "Randomizes bonus stats of selected equipment");
	
	public static Inventory eCashVendor = null;
	// Used for /ecash
	
	public static Inventory shardMerchant = null;
	
	public static ItemStack t1_pot = ItemMechanics.signNewCustomItem(Material.POTION, (short) 1, ChatColor.WHITE.toString() + "Minor Health Potion", ChatColor.GRAY.toString() + "A potion that restores " + ChatColor.WHITE.toString() + "15HP");
	public static ItemStack t2_pot = ItemMechanics.signNewCustomItem(Material.POTION, (short) 5, ChatColor.GREEN.toString() + "Health Potion", ChatColor.GRAY.toString() + "A potion that restores " + ChatColor.GREEN.toString() + "75HP");
	public static ItemStack t3_pot = ItemMechanics.signNewCustomItem(Material.POTION, (short) 9, ChatColor.AQUA.toString() + "Major Health Potion", ChatColor.GRAY.toString() + "A potion that restores " + ChatColor.AQUA.toString() + "300HP");
	public static ItemStack t4_pot = ItemMechanics.signNewCustomItem(Material.POTION, (short) 12, ChatColor.LIGHT_PURPLE.toString() + "Superior Health Potion", ChatColor.GRAY.toString() + "A potion that restores " + ChatColor.LIGHT_PURPLE.toString() + "750HP");
	public static ItemStack t5_pot = ItemMechanics.signNewCustomItem(Material.POTION, (short) 3, ChatColor.YELLOW.toString() + "Legendary Health Potion", ChatColor.GRAY.toString() + "A potion that restores " + ChatColor.YELLOW.toString() + "1800HP");
	
	public static ItemStack t1_s_pot = ItemMechanics.signNewCustomItem(Material.POTION, (short) 16385, ChatColor.WHITE.toString() + "Minor Splash Health Potion", ChatColor.GRAY.toString() + "A potion that restores " + ChatColor.WHITE.toString() + "15HP," + ChatColor.GRAY.toString() + "to players in a 4x4 AREA");
	public static ItemStack t2_s_pot = ItemMechanics.signNewCustomItem(Material.POTION, (short) 16389, ChatColor.GREEN.toString() + "Splash Health Potion", ChatColor.GRAY.toString() + "A potion that restores " + ChatColor.GREEN.toString() + "40HP," + ChatColor.GRAY.toString() + "to players in a 4x4 AREA");
	public static ItemStack t3_s_pot = ItemMechanics.signNewCustomItem(Material.POTION, (short) 16393, ChatColor.AQUA.toString() + "Major Splash Health Potion", ChatColor.GRAY.toString() + "A potion that restores " + ChatColor.AQUA.toString() + "150HP," + ChatColor.GRAY.toString() + "to players in a 4x4 AREA");
	public static ItemStack t4_s_pot = ItemMechanics.signNewCustomItem(Material.POTION, (short) 16396, ChatColor.LIGHT_PURPLE.toString() + "Superior Splash Health Potion", ChatColor.GRAY.toString() + "A potion that restores " + ChatColor.LIGHT_PURPLE.toString() + "375HP," + ChatColor.GRAY.toString() + "to players in a 4x4 AREA");
	public static ItemStack t5_s_pot = ItemMechanics.signNewCustomItem(Material.POTION, (short) 16387, ChatColor.YELLOW.toString() + ChatColor.YELLOW.toString() + "Legendary Splash Health Potion", ChatColor.GRAY.toString() + "A potion that restores " + ChatColor.YELLOW.toString() + "900HP," + ChatColor.GRAY.toString() + "to players in a 4x4 AREA");
	
	public static HashMap<String, Integer> item_being_bought = new HashMap<String, Integer>();
	public static HashMap<String, Integer> orb_being_bought = new HashMap<String, Integer>();
	public static HashMap<String, Integer> dye_being_bought = new HashMap<String, Integer>();
	public static HashMap<String, Integer> skill_being_bought = new HashMap<String, Integer>();
	public static HashMap<String, Integer> ecash_item_being_bought = new HashMap<String, Integer>();
	public static HashMap<String, Integer> shard_item_being_bought = new HashMap<String, Integer>();
	
	public static ItemStack firework_wand = ItemMechanics.signNewCustomItem(Material.BLAZE_ROD, (short) 3, ChatColor.GOLD.toString() + "Firework Wand", ChatColor.GRAY.toString() + ChatColor.ITALIC + "An explosive wand that makes beautiful fireworks." + "," + ChatColor.GRAY + "Permanent Untradeable");
	public static ItemStack flaming_armor = ItemMechanics.signNewCustomItem(Material.INK_SACK, (short) 1, ChatColor.GOLD.toString() + "Blazing Armor", ChatColor.GRAY.toString() + ChatColor.ITALIC + "Adds a firey effect around the player." + "," + ChatColor.GRAY + "Permanent Untradeable");
	public static ItemStack flame_trail = ItemMechanics.signNewCustomItem(Material.BLAZE_POWDER, (short) 1, ChatColor.GOLD.toString() + "Flame Trail", ChatColor.GRAY.toString() + ChatColor.ITALIC + "Leave a trail of fire everywhere you go." + "," + ChatColor.GRAY + "Permanent Untradeable");
	public static ItemStack old_music_box = ItemMechanics.signNewCustomItem(Material.JUKEBOX, (short) 1, ChatColor.GOLD.toString() + "Mobile Musicbox", ChatColor.GRAY.toString() + ChatColor.ITALIC + "Place this musicbox anywhere to play music for all!" + "," + ChatColor.GRAY + "Permanent Untradeable");
	@SuppressWarnings("deprecation")
	public static ItemStack musical_spirit = ItemMechanics.signNewCustomItem(Material.getMaterial(382), (short) 1, ChatColor.GOLD.toString() + "Musical Spirit", ChatColor.GRAY.toString() + ChatColor.ITALIC + "Adds a musical particle trail to your character." + "," + ChatColor.GRAY + "Permanent Untradeable");
	@SuppressWarnings("deprecation")
	public static ItemStack global_microphone = ItemMechanics.signNewCustomItem(Material.getMaterial(401), (short) 1, ChatColor.GOLD.toString() + "Global Messenger", ChatColor.GOLD.toString() + "Uses: " + ChatColor.GRAY.toString() + "1" + "," + ChatColor.GRAY.toString() + ChatColor.ITALIC + "Sends a message to all players on " + ChatColor.UNDERLINE + "ALL SHARDS." + "," + ChatColor.GRAY + "Permanent Untradeable");
	@SuppressWarnings("deprecation")
	public static ItemStack global_delay_buff = ItemMechanics.signNewCustomItem(Material.getMaterial(368), (short) 1, ChatColor.GOLD.toString() + "Global Chat Amplifier", ChatColor.GOLD + "Messages: " + ChatColor.GRAY + "500" + "," + ChatColor.GRAY.toString() + ChatColor.ITALIC + "50% Decreased Global Chat Delay for 500 messages." + "," + ChatColor.GRAY + "Permanent Untradeable");
	public static ItemStack increased_drops = ItemMechanics.signNewCustomItem(Material.DIAMOND, (short) 1, ChatColor.GOLD.toString() + "Global Loot Buff", ChatColor.GOLD.toString() + "Duration: " + ChatColor.GRAY + "30 minutes" + "," + ChatColor.GOLD.toString() + "Uses: " + ChatColor.GRAY + "1" + "," + ChatColor.GRAY.toString() + ChatColor.ITALIC + "Increases all loot drop chances for everyone" + "," + ChatColor.GRAY.toString() + ChatColor.ITALIC + "by 20% across " + ChatColor.UNDERLINE + "ALL SHARDS." + "," + ChatColor.GRAY + "Permanent Untradeable");
	
	public static ItemStack skeleton_horse = ItemMechanics.signNewCustomItem(Material.BONE, (short) 3, ChatColor.GOLD.toString() + "Skeleton Horse Skin", ChatColor.GRAY.toString() + ChatColor.ITALIC + "Transforms your horse into a conjured skeletal beast." + "," + ChatColor.GRAY + "Permanent Untradeable");
	public static ItemStack undead_horse = ItemMechanics.signNewCustomItem(Material.ROTTEN_FLESH, (short) 3, ChatColor.GOLD.toString() + "Zombie Horse Skin", ChatColor.GRAY.toString() + ChatColor.ITALIC + "Transforms your horse into a demonic death charger." + "," + ChatColor.GRAY + "Permanent Untradeable");
	public static ItemStack item_lore_tag = ItemMechanics.signNewCustomItem(Material.ENCHANTED_BOOK, (short) 3, ChatColor.GOLD.toString() + "Item Lore Book", ChatColor.GOLD.toString() + "Uses: " + ChatColor.GRAY + "1" + "," + ChatColor.GRAY.toString() + ChatColor.ITALIC + "Apply to any tradeable item to" + "," + ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + "add a custom line of lore text." + "," + ChatColor.GRAY + "Permanent Untradeable");
	@SuppressWarnings("deprecation")
	public static ItemStack item_ownership_tag = ItemMechanics.signNewCustomItem(Material.getMaterial(421), (short) 3, ChatColor.GOLD.toString() + "Item Name Tag", ChatColor.GOLD.toString() + "Uses: " + ChatColor.GRAY + "1" + "," + ChatColor.GRAY.toString() + ChatColor.ITALIC + "Apply to any weapon or armor piece" + "," + ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + "to give it a custom display name." + "," + ChatColor.GRAY + "Permanent Untradeable");
	public static ItemStack profession_exp_boost = ItemMechanics.signNewCustomItem(Material.GOLDEN_CARROT, (short) 3, ChatColor.GOLD.toString() + "Global Skill EXP Buff", ChatColor.GOLD.toString() + "Duration: " + ChatColor.GRAY + "30 minutes" + "," + ChatColor.GOLD.toString() + "Uses: " + ChatColor.GRAY + "1" + "," + ChatColor.GRAY.toString() + ChatColor.ITALIC + "Increases ALL mining / fishing EXP for everyone" + "," + ChatColor.GRAY.toString() + ChatColor.ITALIC + "by 20% across " + ChatColor.UNDERLINE + "ALL SHARDS." + "," + ChatColor.GRAY + "Permanent Untradeable");
	
	public static ItemStack lore_remover = ItemMechanics.signNewCustomItem(Material.POTION, (short) 0, ChatColor.GOLD.toString() + "Item Lore Removal", ChatColor.GOLD.toString() + "Uses: " + ChatColor.GRAY + "1" + "," + ChatColor.GOLD.toString() + ChatColor.GRAY.toString() + ChatColor.ITALIC + "Apply to any tradeable item to" + "," + ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + "remove all custom lines of lore text." + "," + ChatColor.GRAY + "Permanent Untradeable");
	public static ItemStack ice_path = ItemMechanics.signNewCustomItem(Material.ICE, (short) 1, ChatColor.GOLD.toString() + "Icey Path", ChatColor.GOLD.toString() + ChatColor.GRAY.toString() + ChatColor.ITALIC + "Transform into an ice demon" + "," + ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + "ice particles will follow your every move." + "," + ChatColor.GRAY + "Permanent Untradeable");
	
	public static ItemStack demonic_aura = ItemMechanics.signNewCustomItem(Material.PORTAL, (short) 1, ChatColor.GOLD.toString() + "Demonic Aura", ChatColor.GOLD.toString() + ChatColor.GRAY.toString() + ChatColor.ITALIC + "Cloaks your character in" + "," + ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + "a chilling demonic aura." + "," + ChatColor.GRAY + "Permanent Untradeable");
	// Witch magic + play evil sounds randomly.
	
	public static ItemStack golden_curse = ItemMechanics.signNewCustomItem(Material.GOLD_BLOCK, (short) 1, ChatColor.GOLD.toString() + "Golden Curse", ChatColor.GOLD.toString() + ChatColor.GRAY.toString() + ChatColor.ITALIC + "Everything you touch shall" + "," + ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + "turn to gold for all." + "," + ChatColor.GRAY + "Permanent Untradeable");
	// Whitelist of blocks that turn into gold blocks for X seconds whenever the player comes in contact with them., play a cool sound maybe.
	
	public static ItemStack personal_clone = ItemMechanics.signNewCustomItem(Material.SPIDER_EYE, (short) 1, ChatColor.GOLD.toString() + "Personal Clone", ChatColor.GOLD.toString() + ChatColor.GRAY.toString() + ChatColor.ITALIC + "Creates a temporary clone of" + "," + ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + "yourself at your position." + "," + ChatColor.GRAY + "Permanent Untradeable");
	// Spawns NPC of the player with their armor, disappears after X seconds in a puff of smoke, maybe have them say something cool?
	
	@SuppressWarnings("deprecation")
	public static ItemStack destruction_wand = ItemMechanics.signNewCustomItem(Material.getMaterial(76), (short) 1, ChatColor.GOLD.toString() + "Destructo Wand", ChatColor.GOLD.toString() + ChatColor.GRAY.toString() + ChatColor.ITALIC + "The power of lightning and" + "," + ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + "explosions at the tip of your fingers." + "," + ChatColor.GRAY + "Permanent Untradeable");
	
	// Fires lightning / fireballs, has a cooldown, they do no real damage.
	
	//public static ItemStack beggar_bowl = ItemMechanics.signNewCustomItem(Material.BOWL, (short)1, ChatColor.GOLD.toString() + "Villager Summon", ChatColor.GOLD.toString() + ChatColor.GRAY.toString() + ChatColor.ITALIC + "Creates a temporary clone of" + "," + ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + "yourself at your position" + "," + ChatColor.GRAY + "Permanent Untradeable");
	// Spawns NPC of the player with their armor, disappears after X seconds in a puff of smoke, maybe have them say something cool?
	
	public void onEnable() {
		ItemMeta im = divider.getItemMeta();
		im.setDisplayName(" ");
		divider.setItemMeta(im);
		
		eCashVendor = Bukkit.createInventory(null, 18, "E-CASH Vendor");
		shardMerchant = Bukkit.createInventory(null, 9, "Dungeoneer");
		setupECASHInventory();
		setupShardInventory();
		
		Main.plugin.getServer().getPluginManager().registerEvents(this, Main.plugin);
		log.info("[MerchantMechanics] has been enabled.");
	}
	
	public void onDisable() {
		log.info("[MerchantMechanics] has been disabled.");
	}
	
	public void setupECASHInventory() {
		eCashVendor.setItem(0, Hive.setECASHPrice(CraftItemStack.asCraftCopy(firework_wand), 999));
		eCashVendor.setItem(1, Hive.setECASHPrice(CraftItemStack.asCraftCopy(flaming_armor), 699));
		eCashVendor.setItem(2, Hive.setECASHPrice(CraftItemStack.asCraftCopy(flame_trail), 599));
		eCashVendor.setItem(3, Hive.setECASHPrice(CraftItemStack.asCraftCopy(musical_spirit), 599));
		eCashVendor.setItem(4, Hive.setECASHPrice(CraftItemStack.asCraftCopy(old_music_box), 1199));
		eCashVendor.setItem(5, Hive.setECASHPrice(CraftItemStack.asCraftCopy(global_microphone), 199));
		eCashVendor.setItem(6, Hive.setECASHPrice(CraftItemStack.asCraftCopy(global_delay_buff), 399));
		eCashVendor.setItem(7, Hive.setECASHPrice(CraftItemStack.asCraftCopy(increased_drops), 3999));
		
		eCashVendor.setItem(8, Hive.setECASHPrice(CraftItemStack.asCraftCopy(skeleton_horse), 399));
		eCashVendor.setItem(9, Hive.setECASHPrice(CraftItemStack.asCraftCopy(undead_horse), 299));
		eCashVendor.setItem(10, Hive.setECASHPrice(CraftItemStack.asCraftCopy(item_lore_tag), 99));
		eCashVendor.setItem(11, Hive.setECASHPrice(CraftItemStack.asCraftCopy(item_ownership_tag), 199));
		eCashVendor.setItem(12, Hive.setECASHPrice(CraftItemStack.asCraftCopy(profession_exp_boost), 3999));
		eCashVendor.setItem(13, Hive.setECASHPrice(CraftItemStack.asCraftCopy(lore_remover), 299));
		eCashVendor.setItem(14, Hive.setECASHPrice(CraftItemStack.asCraftCopy(demonic_aura), 666));
		eCashVendor.setItem(15, Hive.setECASHPrice(CraftItemStack.asCraftCopy(personal_clone), 1499));
		eCashVendor.setItem(16, Hive.setECASHPrice(CraftItemStack.asCraftCopy(golden_curse), 899));
		eCashVendor.setItem(17, Hive.setECASHPrice(CraftItemStack.asCraftCopy(destruction_wand), 999));
	}
	
	public void setupShardInventory() {
		shardMerchant.setItem(0, MountMechanics.setShardPrice(CraftItemStack.asCraftCopy(MountMechanics.t2_mule_upgrade), 5000, 3));
		shardMerchant.setItem(1, MountMechanics.setShardPrice(CraftItemStack.asCraftCopy(MountMechanics.t3_mule_upgrade), 8000, 4));
		shardMerchant.setItem(2, MountMechanics.setShardPrice(CraftItemStack.asCraftCopy(EnchantMechanics.t1_white_scroll), 1500, 1));
		shardMerchant.setItem(3, MountMechanics.setShardPrice(CraftItemStack.asCraftCopy(EnchantMechanics.t2_white_scroll), 1500, 2));
		shardMerchant.setItem(4, MountMechanics.setShardPrice(CraftItemStack.asCraftCopy(EnchantMechanics.t3_white_scroll), 1500, 3));
		shardMerchant.setItem(5, MountMechanics.setShardPrice(CraftItemStack.asCraftCopy(EnchantMechanics.t4_white_scroll), 1500, 4));
		shardMerchant.setItem(6, MountMechanics.setShardPrice(CraftItemStack.asCraftCopy(EnchantMechanics.t5_white_scroll), 1500, 5));
	}
	
	private static String generateTitle(String lPName, String rPName) {
		String title = "  " + lPName;
		while((title.length() + rPName.length()) < (30)) {
			title += " ";
		}
		return title += rPName;
	}
	
	public static List<ItemStack> generateSkillScrolls(ItemStack tool) {
		List<ItemStack> scroll_list = new ArrayList<ItemStack>();
		String skill = ProfessionMechanics.getSkillType(tool);
		
		ItemStack blank_scroll = null;
		
		if(skill.equalsIgnoreCase("mining")) {
			blank_scroll = CraftItemStack.asCraftCopy(pickaxe_scroll);
		}
		if(skill.equalsIgnoreCase("fishing")) {
			blank_scroll = CraftItemStack.asCraftCopy(fishing_scroll);
		}
		
		for(String s : tool.getItemMeta().getLore()) {
			if(s.startsWith(ChatColor.RED.toString())) {
				// A custom thing.
				List<String> new_lore = new ArrayList<String>();
				new_lore.add(s);
				for(String lore : blank_scroll.getItemMeta().getLore()) {
					new_lore.add(lore);
				}
				ItemStack written_scroll = CraftItemStack.asCraftCopy(blank_scroll);
				ItemMeta im = blank_scroll.getItemMeta();
				im.setLore(new_lore);
				written_scroll.setItemMeta(im);
				scroll_list.add(written_scroll);
			}
		}
		
		return scroll_list;
	}
	
	public static List<ItemStack> generateMerchantOffer(List<ItemStack> player_offer) {
		List<ItemStack> merchant_offer = new ArrayList<ItemStack>();
		List<ItemStack> to_remove = new ArrayList<ItemStack>();
		int t1_scraps = 0, t2_scraps = 0, t3_scraps = 0, t4_scraps = 0, t5_scraps = 0;
		int t1_arrow = 0, t2_arrow = 0, t3_arrow = 0, t4_arrow = 0/*, t5_arrow = 0*/;
		int t1_ore = 0, t2_ore = 0, t3_ore = 0, t4_ore = 0, t5_ore = 0;
		int t1_pot = 0, t2_pot = 0, t3_pot = 0, t4_pot = 0/*, t5_pot = 0*/;
		int t1_s_pot = 0, t2_s_pot = 0, t3_s_pot = 0, t4_s_pot = 0/*, t5_s_pot = 0*/;
		
		for(ItemStack is : player_offer) {
			if(is == null || is.getType() == Material.AIR) {
				continue;
			}
			
			if(ProfessionMechanics.isSkillItem(is) && ProfessionMechanics.getItemLevel(is) >= 20) {
				List<ItemStack> scroll_list = generateSkillScrolls(is);
				
				for(ItemStack scroll : scroll_list) {
					merchant_offer.add(scroll);
				}
			}
			
			if(is.getType() == Material.POTION) {
				int tier = HealthMechanics.getPotionTier(is);
				
				if(tier == 1) {
					if(is.getDurability() < 1000) {
						t1_pot += is.getAmount();
					} else if(is.getDurability() > 1000) {
						t1_s_pot += is.getAmount();
					}
				}
				if(tier == 2) {
					if(is.getDurability() < 1000) {
						t2_pot += is.getAmount();
					} else if(is.getDurability() > 1000) {
						t2_s_pot += is.getAmount();
					}
				}
				if(tier == 3) {
					if(is.getDurability() < 1000) {
						t3_pot += is.getAmount();
					} else if(is.getDurability() > 1000) {
						t3_s_pot += is.getAmount();
					}
				}
				if(tier == 4) {
					if(is.getDurability() < 1000) {
						t4_pot += is.getAmount();
					} else if(is.getDurability() > 1000) {
						t4_s_pot += is.getAmount();
					}
				}
				if(tier == 5) {
					if(is.getDurability() < 1000) {
						//t5_pot += is.getAmount();
					} else if(is.getDurability() > 1000) {
						//t5_s_pot += is.getAmount();
					}
				}
				to_remove.add(is);
			}
			
			if(is.getType() == Material.COAL_ORE || is.getType() == Material.EMERALD_ORE || is.getType() == Material.IRON_ORE || is.getType() == Material.DIAMOND_ORE || is.getType() == Material.GOLD_ORE) {
				int tier = ProfessionMechanics.getOreTier(is.getType());
				// fix scrap prices, add pouches.
				if(tier == 1) {
					t1_ore += is.getAmount();
				}
				if(tier == 2) {
					t2_ore += is.getAmount();
				}
				if(tier == 3) {
					t3_ore += is.getAmount();
				}
				if(tier == 4) {
					t4_ore += is.getAmount();
				}
				if(tier == 5) {
					t5_ore += is.getAmount();
				}
				to_remove.add(is);
			}
		}
		
		for(ItemStack is : player_offer) {
			if(is == null || is.getType() == Material.AIR) {
				continue;
			}
			
			if(RepairMechanics.isArmorScrap(is)) {
				int tier = ItemMechanics.getItemTier(is);
				if(tier == 1) {
					t1_scraps += is.getAmount();
				}
				if(tier == 2) {
					t2_scraps += is.getAmount();
				}
				if(tier == 3) {
					t3_scraps += is.getAmount();
				}
				if(tier == 4) {
					t4_scraps += is.getAmount();
				}
				if(tier == 5) {
					t5_scraps += is.getAmount();
				}
				to_remove.add(is);
			}
		}
		
		for(ItemStack is : player_offer) {
			if(is == null || is.getType() == Material.AIR) {
				continue;
			}
			
			if(is.getType() == Material.ARROW) {
				int tier = ItemMechanics.getItemTier(is);
				if(tier == 1) {
					t1_arrow += is.getAmount();
				}
				if(tier == 2) {
					t2_arrow += is.getAmount();
				}
				if(tier == 3) {
					t3_arrow += is.getAmount();
				}
				if(tier == 4) {
					t4_arrow += is.getAmount();
				}
				if(tier == 5) {
					//t5_arrow += is.getAmount();
				}
				to_remove.add(is);
			}
		}
		
		for(ItemStack is : to_remove) {
			player_offer.remove(is);
			//  Remove the item so it's not processed. We'll process scraps/arrows directly from above variables.
		}
		
		for(ItemStack is : player_offer) {
			// TODO: Pricing formula.
			if(is == null || is.getType() == Material.AIR) {
				continue;
			}
			//merchant_offer.add(new ItemStack(Material.CAKE));
			int tier = ItemMechanics.getItemTier(is);
			if(!(is.getType() == Material.MAGMA_CREAM) && !(is.getType() == Material.PAPER) && !(RepairMechanics.isArmorScrap(is)) && (!ItemMechanics.getDamageData(is).equalsIgnoreCase("no") || ItemMechanics.getArmorData(is) != null)) {
				
				if(!DuelMechanics.isArmorIcon(is) && !DuelMechanics.isWeaponIcon(is)) {
					int payout = 0;
					Material m = is.getType();
					String m_name = m.name().toLowerCase();
					
					if(m_name.contains("axe") || m_name.contains("sword") || m_name.contains("leg") || m_name.contains("bow") || m_name.contains("spade") || m_name.contains("hoe")) {
						payout = 2;
					}
					
					if(m_name.contains("helmet") || m_name.contains("boot")) {
						payout = 1;
					}
					
					if(m_name.contains("chest")) {
						payout = 3;
					}
					
					if(tier == 1) {
						ItemStack scrap = CraftItemStack.asCraftCopy(T2_scrap);
						scrap.setAmount(payout);
						merchant_offer.add(scrap);
					}
					if(tier == 2) {
						ItemStack scrap = CraftItemStack.asCraftCopy(T3_scrap);
						scrap.setAmount(payout);
						merchant_offer.add(scrap);
					}
					if(tier == 3) {
						ItemStack scrap = CraftItemStack.asCraftCopy(T4_scrap);
						scrap.setAmount(payout);
						merchant_offer.add(scrap);
					}
					if(tier == 4) {
						ItemStack scrap = CraftItemStack.asCraftCopy(T5_scrap);
						scrap.setAmount(payout);
						merchant_offer.add(scrap);
					}
					if(tier == 5) {
						ItemStack scrap = CraftItemStack.asCraftCopy(T5_scrap);
						scrap.setAmount(payout * 2);
						merchant_offer.add(scrap);
					}
				}
			}
			
			if(ItemMechanics.isOrbOfAlteration(is)) {
				int orb_count = is.getAmount();
				int payout = 20 * orb_count;
				while(payout > 64) {
					ItemStack scrap = CraftItemStack.asCraftCopy(T5_scrap);
					scrap.setAmount(64);
					merchant_offer.add(scrap);
					payout -= 64;
				}
				ItemStack scrap = CraftItemStack.asCraftCopy(T5_scrap);
				scrap.setAmount(payout);
				merchant_offer.add(scrap);
			}
		}
		
		if(t1_arrow > 0) {
			int to_give = t1_arrow / 3;
			while(to_give >= 64) {
				to_give -= 64;
				ItemStack arrow = CraftItemStack.asCraftCopy(ItemMechanics.t2_arrow);
				arrow.setAmount(64);
				merchant_offer.add(arrow);
			}
			ItemStack arrow = CraftItemStack.asCraftCopy(ItemMechanics.t2_arrow);
			arrow.setAmount(to_give);
			merchant_offer.add(arrow);
		}
		
		if(t2_arrow > 0) {
			int to_give = t2_arrow / 3;
			while(to_give >= 64) {
				to_give -= 64;
				ItemStack arrow = CraftItemStack.asCraftCopy(ItemMechanics.t3_arrow);
				arrow.setAmount(64);
				merchant_offer.add(arrow);
			}
			ItemStack arrow = CraftItemStack.asCraftCopy(ItemMechanics.t3_arrow);
			arrow.setAmount(to_give);
			merchant_offer.add(arrow);
		}
		
		if(t3_arrow > 0) {
			int to_give = t3_arrow / 3;
			while(to_give >= 64) {
				to_give -= 64;
				ItemStack arrow = CraftItemStack.asCraftCopy(ItemMechanics.t4_arrow);
				arrow.setAmount(64);
				merchant_offer.add(arrow);
			}
			ItemStack arrow = CraftItemStack.asCraftCopy(ItemMechanics.t4_arrow);
			arrow.setAmount(to_give);
			merchant_offer.add(arrow);
		}
		
		if(t4_arrow > 0) {
			int to_give = t4_arrow / 3;
			while(to_give >= 64) {
				to_give -= 64;
				ItemStack arrow = CraftItemStack.asCraftCopy(ItemMechanics.t5_arrow);
				arrow.setAmount(64);
				merchant_offer.add(arrow);
			}
			ItemStack arrow = CraftItemStack.asCraftCopy(ItemMechanics.t5_arrow);
			arrow.setAmount(to_give);
			merchant_offer.add(arrow);
		}
		
		if(t1_pot > 0) {
			while(t1_pot >= 8) {
				t1_pot -= 8;
				ItemStack pot = CraftItemStack.asCraftCopy(MerchantMechanics.t2_pot);
				merchant_offer.add(pot);
			}
		}
		
		if(t2_pot > 0) {
			while(t2_pot >= 8) {
				t2_pot -= 8;
				ItemStack pot = CraftItemStack.asCraftCopy(MerchantMechanics.t3_pot);
				merchant_offer.add(pot);
			}
		}
		
		if(t3_pot > 0) {
			while(t3_pot >= 5) {
				t3_pot -= 5;
				ItemStack pot = CraftItemStack.asCraftCopy(MerchantMechanics.t4_pot);
				merchant_offer.add(pot);
			}
		}
		
		if(t4_pot > 0) {
			while(t4_pot >= 5) {
				t4_pot -= 5;
				ItemStack pot = CraftItemStack.asCraftCopy(MerchantMechanics.t5_pot);
				merchant_offer.add(pot);
			}
		}
		
		if(t1_s_pot > 0) {
			while(t1_s_pot >= 8) {
				t1_s_pot -= 8;
				ItemStack pot = CraftItemStack.asCraftCopy(MerchantMechanics.t2_s_pot);
				merchant_offer.add(pot);
			}
		}
		
		if(t2_s_pot > 0) {
			while(t2_s_pot >= 8) {
				t2_s_pot -= 8;
				ItemStack pot = CraftItemStack.asCraftCopy(MerchantMechanics.t3_s_pot);
				merchant_offer.add(pot);
			}
		}
		
		if(t3_s_pot > 0) {
			while(t3_s_pot >= 5) {
				t3_s_pot -= 5;
				ItemStack pot = CraftItemStack.asCraftCopy(MerchantMechanics.t4_s_pot);
				merchant_offer.add(pot);
			}
		}
		
		if(t4_s_pot > 0) {
			while(t4_s_pot >= 5) {
				t4_s_pot -= 5;
				ItemStack pot = CraftItemStack.asCraftCopy(MerchantMechanics.t5_s_pot);
				merchant_offer.add(pot);
			}
		}
		
		if(t1_ore > 0) {
			while(t1_ore >= 100) {
				t1_ore -= 100;
				ItemStack pouch = CraftItemStack.asCraftCopy(MoneyMechanics.t1_gem_pouch);
				merchant_offer.add(pouch);
			}
			
			int payout = t1_ore * 2;
			while(payout > 64) {
				payout -= 64;
				ItemStack scrap = CraftItemStack.asCraftCopy(T1_scrap);
				scrap.setAmount(64);
				merchant_offer.add(scrap);
			}
			ItemStack scrap = CraftItemStack.asCraftCopy(T1_scrap);
			scrap.setAmount(payout);
			merchant_offer.add(scrap);
		}
		if(t2_ore > 0) {
			while(t2_ore >= 150) {
				t2_ore -= 150;
				ItemStack pouch = CraftItemStack.asCraftCopy(MoneyMechanics.t2_gem_pouch);
				merchant_offer.add(pouch);
			}
			
			while(t2_ore >= 70) {
				t2_ore -= 70;
				ItemStack pouch = CraftItemStack.asCraftCopy(MoneyMechanics.t1_gem_pouch);
				merchant_offer.add(pouch);
			}
			
			int payout = t2_ore * 1;
			while(payout > 64) {
				payout -= 64;
				ItemStack scrap = CraftItemStack.asCraftCopy(T2_scrap);
				scrap.setAmount(64);
				merchant_offer.add(scrap);
			}
			ItemStack scrap = CraftItemStack.asCraftCopy(T2_scrap);
			scrap.setAmount(payout);
			merchant_offer.add(scrap);
		}
		if(t3_ore > 0) {
			while(t3_ore >= 200) {
				t3_ore -= 200;
				ItemStack pouch = CraftItemStack.asCraftCopy(MoneyMechanics.t3_gem_pouch);
				merchant_offer.add(pouch);
			}
			while(t3_ore >= 100) {
				t3_ore -= 100;
				ItemStack pouch = CraftItemStack.asCraftCopy(MoneyMechanics.t2_gem_pouch);
				merchant_offer.add(pouch);
			}
			while(t3_ore >= 40) {
				t3_ore -= 40;
				ItemStack pouch = CraftItemStack.asCraftCopy(MoneyMechanics.t1_gem_pouch);
				merchant_offer.add(pouch);
			}
			
			int payout = t3_ore / 2;
			while(payout > 64) {
				payout -= 64;
				ItemStack scrap = CraftItemStack.asCraftCopy(T3_scrap);
				scrap.setAmount(64);
				merchant_offer.add(scrap);
			}
			ItemStack scrap = CraftItemStack.asCraftCopy(T3_scrap);
			scrap.setAmount(payout);
			merchant_offer.add(scrap);
		}
		if(t4_ore > 0) {
			while(t4_ore >= 140) {
				t4_ore -= 140;
				ItemStack pouch = CraftItemStack.asCraftCopy(MoneyMechanics.t3_gem_pouch);
				merchant_offer.add(pouch);
			}
			while(t4_ore >= 80) {
				t4_ore -= 80;
				ItemStack pouch = CraftItemStack.asCraftCopy(MoneyMechanics.t2_gem_pouch);
				merchant_offer.add(pouch);
			}
			while(t4_ore >= 35) {
				t4_ore -= 35;
				ItemStack pouch = CraftItemStack.asCraftCopy(MoneyMechanics.t1_gem_pouch);
				merchant_offer.add(pouch);
			}
			
			int payout = t4_ore / 2;
			while(payout > 64) {
				payout -= 64;
				ItemStack scrap = CraftItemStack.asCraftCopy(T4_scrap);
				scrap.setAmount(64);
				merchant_offer.add(scrap);
			}
			ItemStack scrap = CraftItemStack.asCraftCopy(T4_scrap);
			scrap.setAmount(payout);
			merchant_offer.add(scrap);
		}
		if(t5_ore > 0) {
			while(t5_ore >= 80) {
				t5_ore -= 80;
				ItemStack pouch = CraftItemStack.asCraftCopy(MoneyMechanics.t4_gem_pouch);
				merchant_offer.add(pouch);
			}
			while(t5_ore >= 60) {
				t5_ore -= 60;
				ItemStack pouch = CraftItemStack.asCraftCopy(MoneyMechanics.t3_gem_pouch);
				merchant_offer.add(pouch);
			}
			while(t5_ore >= 40) {
				t5_ore -= 40;
				ItemStack pouch = CraftItemStack.asCraftCopy(MoneyMechanics.t2_gem_pouch);
				merchant_offer.add(pouch);
			}
			while(t5_ore >= 20) {
				t5_ore -= 20;
				ItemStack pouch = CraftItemStack.asCraftCopy(MoneyMechanics.t1_gem_pouch);
				merchant_offer.add(pouch);
			}
			
			int payout = t5_ore / 2;
			while(payout > 64) {
				payout -= 64;
				ItemStack scrap = CraftItemStack.asCraftCopy(T5_scrap);
				scrap.setAmount(64);
				merchant_offer.add(scrap);
			}
			ItemStack scrap = CraftItemStack.asCraftCopy(T5_scrap);
			scrap.setAmount(payout);
			merchant_offer.add(scrap);
		}
		
		if(t1_scraps > 0) {
			/*while(t1_scraps >= 480){
				t1_scraps -= 480;
				ItemStack orb = CraftItemStack.asCraftCopy(orb_of_alteration);
				merchant_offer.add(orb);
			}*/
			
			while(t1_scraps >= 80) {
				t1_scraps -= 80;
				ItemStack scroll = CraftItemStack.asCraftCopy(EnchantMechanics.t1_wep_scroll);
				merchant_offer.add(scroll);
			}
			
			while(t1_scraps >= 70) {
				t1_scraps -= 70;
				ItemStack scroll = CraftItemStack.asCraftCopy(EnchantMechanics.t1_armor_scroll);
				merchant_offer.add(scroll);
			}
			
			int payout = t1_scraps / 2;
			while(payout > 64) {
				ItemStack scrap = CraftItemStack.asCraftCopy(T2_scrap);
				scrap.setAmount(64);
				merchant_offer.add(scrap);
				payout -= 64;
			}
			ItemStack scrap = CraftItemStack.asCraftCopy(T2_scrap);
			scrap.setAmount(payout);
			merchant_offer.add(scrap);
		}
		if(t2_scraps > 0) {
			/*while(t2_scraps >= 240){
				t2_scraps -= 240;
				ItemStack orb = CraftItemStack.asCraftCopy(orb_of_alteration);
				merchant_offer.add(orb);
			}*/
			
			while(t2_scraps >= 140) {
				t2_scraps -= 140;
				ItemStack scroll = CraftItemStack.asCraftCopy(EnchantMechanics.t2_wep_scroll);
				merchant_offer.add(scroll);
			}
			
			while(t2_scraps >= 125) {
				t2_scraps -= 125;
				ItemStack scroll = CraftItemStack.asCraftCopy(EnchantMechanics.t2_armor_scroll);
				merchant_offer.add(scroll);
			}
			
			int payout = 2 * t2_scraps;
			while(payout > 64) {
				ItemStack scrap = CraftItemStack.asCraftCopy(T1_scrap);
				scrap.setAmount(64);
				merchant_offer.add(scrap);
				payout -= 64;
			}
			ItemStack scrap = CraftItemStack.asCraftCopy(T1_scrap);
			scrap.setAmount(payout);
			merchant_offer.add(scrap);
		}
		if(t3_scraps > 0) {
			
			while(t3_scraps >= 110) {
				t3_scraps -= 110;
				ItemStack scroll = CraftItemStack.asCraftCopy(EnchantMechanics.t3_wep_scroll);
				merchant_offer.add(scroll);
			}
			
			while(t3_scraps >= 100) {
				t3_scraps -= 100;
				ItemStack scroll = CraftItemStack.asCraftCopy(EnchantMechanics.t3_armor_scroll);
				merchant_offer.add(scroll);
			}
			
			int payout = 2 * t3_scraps;
			while(payout > 64) {
				ItemStack scrap = CraftItemStack.asCraftCopy(T2_scrap);
				scrap.setAmount(64);
				merchant_offer.add(scrap);
				payout -= 64;
			}
			ItemStack scrap = CraftItemStack.asCraftCopy(T2_scrap);
			scrap.setAmount(payout);
			merchant_offer.add(scrap);
		}
		if(t4_scraps > 0) {
			while(t4_scraps >= 88) {
				t4_scraps -= 88;
				ItemStack scroll = CraftItemStack.asCraftCopy(EnchantMechanics.t4_wep_scroll);
				merchant_offer.add(scroll);
			}
			
			while(t4_scraps >= 80) {
				t4_scraps -= 80;
				ItemStack scroll = CraftItemStack.asCraftCopy(EnchantMechanics.t4_armor_scroll);
				merchant_offer.add(scroll);
			}
			
			while(t4_scraps >= 120) {
				t4_scraps -= 120;
				ItemStack orb = CraftItemStack.asCraftCopy(orb_of_alteration);
				merchant_offer.add(orb);
			}
			
			int payout = 2 * t4_scraps;
			while(payout > 64) {
				ItemStack scrap = CraftItemStack.asCraftCopy(T3_scrap);
				scrap.setAmount(64);
				merchant_offer.add(scrap);
				payout -= 64;
			}
			ItemStack scrap = CraftItemStack.asCraftCopy(T3_scrap);
			scrap.setAmount(payout);
			merchant_offer.add(scrap);
		}
		if(t5_scraps > 0) {
			while(t5_scraps >= 33) {
				t5_scraps -= 33;
				ItemStack scroll = CraftItemStack.asCraftCopy(EnchantMechanics.t5_wep_scroll);
				merchant_offer.add(scroll);
			}
			
			while(t5_scraps >= 30) {
				t5_scraps -= 30;
				ItemStack scroll = CraftItemStack.asCraftCopy(EnchantMechanics.t5_armor_scroll);
				merchant_offer.add(scroll);
			}
			
			while(t5_scraps >= 30) {
				t5_scraps -= 30;
				ItemStack orb = CraftItemStack.asCraftCopy(orb_of_alteration);
				merchant_offer.add(orb);
			}
			
			int payout = 3 * t5_scraps;
			while(payout > 64) {
				ItemStack scrap = CraftItemStack.asCraftCopy(T4_scrap);
				scrap.setAmount(64);
				merchant_offer.add(scrap);
				payout -= 64;
			}
			ItemStack scrap = CraftItemStack.asCraftCopy(T4_scrap);
			scrap.setAmount(payout);
			merchant_offer.add(scrap);
		}
		
		return merchant_offer;
	}
	
	public static boolean isTradeButton(ItemStack is) {
		if(is == null) { return false; }
		if(is.getType() == Material.INK_SACK && (is.getDurability() == (short) 8 || is.getDurability() == (short) 10)) {
			if(is.hasItemMeta() && is.getItemMeta().hasDisplayName()) {
				String item_name = is.getItemMeta().getDisplayName();
				if(item_name.contains("Trade") || item_name.contains("Duel")) { return true; }
			}
		}
		return false;
	}
	
	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerOpenDungeoneer(PlayerInteractEntityEvent e) {
		Player p = e.getPlayer();
		if(e.getRightClicked() instanceof Player) {
			Player trader = (Player) e.getRightClicked();
			if(!(trader.hasMetadata("NPC"))) { return; } // Only NPC's matter.
			if(!(ChatColor.stripColor(trader.getName()).equalsIgnoreCase("Dungeoneer"))) { return; } // Only 'Trader' should do anything.
			e.setCancelled(true);
			
			p.playSound(p.getLocation(), Sound.WOOD_CLICK, 1.0F, 1.0F);
			if(in_npc_shop.contains(p.getName())) {
				in_npc_shop.remove(p.getName());
			}
			in_npc_shop.add(p.getName());
			
			p.openInventory(shardMerchant);
		}
	}
	
	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerInteractEntity(PlayerInteractEntityEvent e) {
		Player p = e.getPlayer();
		if(e.getRightClicked() instanceof Player) {
			Player trader = (Player) e.getRightClicked();
			if(!(trader.hasMetadata("NPC"))) { return; } // Only NPC's matter.
			if(!(ChatColor.stripColor(trader.getName()).equalsIgnoreCase("Merchant"))) { return; } // Only 'Trader' should do anything.
			e.setCancelled(true);
			
			final Inventory TradeWindow = Bukkit.createInventory(null, 27, generateTitle(p.getName(), "Merchant"));
			TradeWindow.setItem(4, divider);
			TradeWindow.setItem(13, divider);
			TradeWindow.setItem(22, divider);
			TradeWindow.setItem(0, TradeMechanics.setIinfo(new ItemStack(Material.INK_SACK, 1, (short) 8), ChatColor.YELLOW.toString() + "Click to ACCEPT Trade", ""));
			//TradeWindow.setItem(8, TradeMechanics.setIinfo(new ItemStack(Material.INK_SACK, 1, (short) 8), ChatColor.YELLOW.toString() + "Click to ACCEPT Trade", ""));
			p.playSound(p.getLocation(), Sound.WOOD_CLICK, 1.0F, 1.0F);
			if(in_npc_shop.contains(p.getName())) {
				in_npc_shop.remove(p.getName());
			}
			in_npc_shop.add(p.getName());
			p.openInventory(TradeWindow);
		}
	}
	
	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerOpenVendor(PlayerInteractEntityEvent e) {
		Player p = e.getPlayer();
		if(e.getRightClicked() instanceof Player) {
			Player trader = (Player) e.getRightClicked();
			if(!(trader.hasMetadata("NPC"))) { return; } // Only NPC's matter.
			if(!(ChatColor.stripColor(trader.getName()).equalsIgnoreCase("Item Vendor"))) { return; } // Only 'Trader' should do anything.
			e.setCancelled(true);
			
			final Inventory TradeWindow = Bukkit.createInventory(null, 9, "Item Vendor");
			TradeWindow.setItem(0, ShopMechanics.setPrice(CraftItemStack.asCraftCopy(ItemMechanics.orb_of_peace), 400));
			TradeWindow.setItem(1, ShopMechanics.setPrice(CraftItemStack.asCraftCopy(ItemMechanics.orb_of_flight), 1000));
			TradeWindow.setItem(2, ShopMechanics.setPrice(CraftItemStack.asCraftCopy(GuildMechanics.guild_dye), 1000));
			//TradeWindow.setItem(8, TradeMechanics.setIinfo(new ItemStack(Material.INK_SACK, 1, (short) 8), ChatColor.YELLOW.toString() + "Click to ACCEPT Trade", ""));
			p.playSound(p.getLocation(), Sound.WOOD_CLICK, 1.0F, 1.0F);
			if(in_npc_shop.contains(p.getName())) {
				in_npc_shop.remove(p.getName());
			}
			in_npc_shop.add(p.getName());
			p.openInventory(TradeWindow);
		}
	}
	
	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerOpenSkillTrainer(PlayerInteractEntityEvent e) {
		Player p = e.getPlayer();
		if(e.getRightClicked() instanceof Player) {
			Player trader = (Player) e.getRightClicked();
			if(!(trader.hasMetadata("NPC"))) { return; } // Only NPC's matter.
			if(!(ChatColor.stripColor(trader.getName()).equalsIgnoreCase("Skill Trainer"))) { return; } // Only 'Trader' should do anything.
			e.setCancelled(true);
			
			final Inventory TradeWindow = Bukkit.createInventory(null, 9, "Skill Trainer");
			TradeWindow.setItem(0, ShopMechanics.setPrice(CraftItemStack.asCraftCopy(ProfessionMechanics.t1_pickaxe), 100));
			TradeWindow.setItem(1, ShopMechanics.setPrice(CraftItemStack.asCraftCopy(ProfessionMechanics.t1_fishing), 100));
			//TradeWindow.setItem(2, CraftItemStack.asCraftCopy(ProfessionMechanics.hoe_example));
			//TradeWindow.setItem(8, TradeMechanics.setIinfo(new ItemStack(Material.INK_SACK, 1, (short) 8), ChatColor.YELLOW.toString() + "Click to ACCEPT Trade", ""));
			p.playSound(p.getLocation(), Sound.WOOD_CLICK, 1.0F, 1.0F);
			if(in_npc_shop.contains(p.getName())) {
				in_npc_shop.remove(p.getName());
			}
			in_npc_shop.add(p.getName());
			p.openInventory(TradeWindow);
		}
	}
	
	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerOpenEcashStorage(PlayerInteractEntityEvent e) {
		Player p = e.getPlayer();
		if(e.getRightClicked() instanceof Player) {
			Player trader = (Player) e.getRightClicked();
			if(!(trader.hasMetadata("NPC"))) { return; } // Only NPC's matter.
			if(!(ChatColor.stripColor(trader.getName()).equalsIgnoreCase("E-CASH Storage"))) { return; }
			e.setCancelled(true);
			
			if(EcashMechanics.ecash_storage_map.containsKey(p.getName())) {
				Inventory ecash_inv = Hive.convertStringToInventory(null, EcashMechanics.ecash_storage_map.get(p.getName()), "E-Cash Storage", 54);
				p.playSound(p.getLocation(), Sound.WOOD_CLICK, 1.0F, 1.0F);
				boolean found_stuff = false;
				for(ItemStack is : ecash_inv) {
					if(is == null || is.getType() == Material.AIR) continue;
					if(MountMechanics.isMount(is) || MountMechanics.isMule(is)) {
						ecash_inv.remove(is);
						found_stuff = true;
						continue;
					}
				}
				if(found_stuff) {
					p.sendMessage(ChatColor.RED + "A mount/mule was found in your E-Cash Storage, it has been removed!");
					for(Player GM : Bukkit.getOnlinePlayers()) {
						if(PermissionMechanics.isGM(GM.getName())) {
							GM.sendMessage(ChatColor.AQUA.toString() + ChatColor.UNDERLINE + p.getName() + ChatColor.AQUA + " has been found with a mule/mount in their E-Cash Storage.");
						}
					}
				}
				if(in_npc_shop.contains(p.getName())) {
					in_npc_shop.remove(p.getName());
				}
				in_npc_shop.add(p.getName());
				p.openInventory(ecash_inv);
			} else {
				String rank = PermissionMechanics.getRank(p.getName());
				if(!(rank.contains("sub")) && !(p.isOp()) && !(rank.contains("pmod"))) {
					p.sendMessage(ChatColor.GRAY + "E-Cash Storage: " + ChatColor.WHITE.toString() + "I'm sorry, but I only provide item storing services to Dungeon Realm's subscribers! Gain access today at " + ChatColor.UNDERLINE + "store.dungeonrealms.net");
					return;
				}
				// First time.
				Inventory ecash_inv = Bukkit.createInventory(null, 54, "E-Cash Storage");
				p.playSound(p.getLocation(), Sound.WOOD_CLICK, 1.0F, 1.0F);
				if(in_npc_shop.contains(p.getName())) {
					in_npc_shop.remove(p.getName());
				}
				in_npc_shop.add(p.getName());
				p.openInventory(ecash_inv);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerOpenECashVendor(PlayerInteractEntityEvent e) {
		Player p = e.getPlayer();
		if(e.getRightClicked() instanceof Player) {
			Player trader = (Player) e.getRightClicked();
			if(!(trader.hasMetadata("NPC"))) { return; } // Only NPC's matter.
			if(!(ChatColor.stripColor(trader.getName()).equalsIgnoreCase("E-CASH Vendor"))) { return; }
			e.setCancelled(true);
			
			p.playSound(p.getLocation(), Sound.WOOD_CLICK, 1.0F, 1.0F);
			if(in_npc_shop.contains(p.getName())) {
				in_npc_shop.remove(p.getName());
			}
			in_npc_shop.add(p.getName());
			p.openInventory(eCashVendor);
		}
	}
	
	@SuppressWarnings({ "deprecation" })
	@EventHandler
	public void onAsyncPlayerChatEvent(AsyncPlayerChatEvent e) {
		Player pl = e.getPlayer();
		if(shard_item_being_bought.containsKey(pl.getName())) {
			e.setCancelled(true);
			
			if(e.getMessage().equalsIgnoreCase("cancel")) {
				pl.sendMessage(ChatColor.RED + "Purchase of item - " + ChatColor.BOLD + "CANCELLED");
				shard_item_being_bought.remove(pl.getName());
				pl.updateInventory();
				return;
			}
			
			if(pl.getInventory().firstEmpty() == -1) {
				pl.sendMessage(ChatColor.RED + "No space available in inventory. Type 'cancel' or clear some room.");
				return;
			}
			
			int amount_to_buy = 1;
			int price_per = 100;
			int shard_tier = 0;
			int space_available = 0;
			int i_amount = 64;
			
			int slot = shard_item_being_bought.get(pl.getName());
			if(slot == 0) {
				price_per = 5000;
				shard_tier = 3;
			}
			if(slot == 1) {
				price_per = 8000;
				shard_tier = 4;
			}
			if(slot == 2) {
				price_per = 1500;
				shard_tier = 1;
			}
			if(slot == 3) {
				price_per = 1500;
				shard_tier = 2;
			}
			if(slot == 4) {
				price_per = 1500;
				shard_tier = 3;
			}
			if(slot == 5) {
				price_per = 1500;
				shard_tier = 4;
			}
			if(slot == 6) {
				price_per = 1500;
				shard_tier = 5;
			}
			
			try {
				amount_to_buy = Integer.parseInt(e.getMessage());
			} catch(NumberFormatException ex) {
				pl.sendMessage(ChatColor.RED + "Please enter a valid integer, or type 'cancel' to void this item purchase.");
				return;
			}
			
			for(ItemStack is : pl.getInventory().getContents()) {
				if(is == null || is.getType() == Material.AIR) {
					space_available++;
					continue;
				}
			}
			
			if(amount_to_buy <= 0) {
				pl.sendMessage(ChatColor.RED + "You cannot purchase a NON-POSITIVE number.");
				return;
			}
			
			if(amount_to_buy > space_available) {
				pl.sendMessage(ChatColor.RED + "No space available in inventory. Type 'cancel' or clear some room.");
				return;
			}
			
			final int total_price = amount_to_buy * price_per;
			
			if(amount_to_buy > i_amount) {
				pl.sendMessage(ChatColor.RED + "There are only [" + ChatColor.BOLD + i_amount + ChatColor.RED + "] available.");
				return;
			}
			
			int shard_count = InstanceMechanics.getPortalShardCount(pl.getName(), shard_tier);
			if(total_price > 0 && (shard_count < total_price)) {
				pl.sendMessage(ChatColor.RED + "You do not have enough Portal Key Shards to complete this purchase.");
				pl.sendMessage(ChatColor.GRAY + "" + amount_to_buy + " X " + price_per + " PKS/ea = " + total_price + " PKS.");
				pl.sendMessage(ChatColor.GRAY + "Defeat " + ChatColor.UNDERLINE + "Instanced Dungeons" + ChatColor.GRAY + " to obtain Portal Key Shards.");
				return;
			}
			
			InstanceMechanics.subtractShards(pl.getName(), shard_tier, total_price);
			
			/*if(total_price > 0 && !Hive.doTheyHaveEnoughECASH(pl.getName(), total_price)){
					pl.sendMessage(ChatColor.RED + "You do not have enough E-CASH to complete this purchase.");
					pl.sendMessage(ChatColor.GRAY + "" + amount_to_buy + " X " + price_per + " EC/ea = " + total_price + " EC.");
					pl.sendMessage(ChatColor.GRAY + "Purchase more E-CASH at store.dungeonrealms.net to make this awesome purchase!");
					return;	
				}

				int total_ecash = Hive.player_ecash.get(pl.getName());
				total_ecash -= total_price;

				final String p_name = pl.getName();
				final int ftotal_ecash = total_ecash;

				Hive.player_ecash.put(pl.getName(), total_ecash);
				Thread t = new Thread(new Runnable(){
					public void run(){
						DonationMechanics.setECASH_SQL(p_name, ftotal_ecash);
					}
				});
				t.start();*/
			
			ItemStack product = null;
			if(slot == 0) {
				product = ShopMechanics.removePrice(CraftItemStack.asCraftCopy(MountMechanics.t2_mule_upgrade));
				product.setDurability((short) new Random().nextInt(32000));
			}
			if(slot == 1) {
				product = ShopMechanics.removePrice(CraftItemStack.asCraftCopy(MountMechanics.t3_mule_upgrade));
				product.setDurability((short) new Random().nextInt(32000));
			}
			if(slot == 2) {
				product = ShopMechanics.removePrice(CraftItemStack.asCraftCopy(EnchantMechanics.t1_white_scroll));
			}
			if(slot == 3) {
				product = ShopMechanics.removePrice(CraftItemStack.asCraftCopy(EnchantMechanics.t2_white_scroll));
			}
			if(slot == 4) {
				product = ShopMechanics.removePrice(CraftItemStack.asCraftCopy(EnchantMechanics.t3_white_scroll));
			}
			if(slot == 5) {
				product = ShopMechanics.removePrice(CraftItemStack.asCraftCopy(EnchantMechanics.t4_white_scroll));
			}
			if(slot == 6) {
				product = ShopMechanics.removePrice(CraftItemStack.asCraftCopy(EnchantMechanics.t5_white_scroll));
			}
			
			pl.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "-" + ChatColor.RED + total_price + ChatColor.BOLD + "PKS");
			pl.sendMessage(ChatColor.GREEN + "Transaction successful.");
			pl.playSound(pl.getLocation(), Sound.ORB_PICKUP, 1F, 1F);
			
			try {
				ParticleEffect.sendToLocation(ParticleEffect.HAPPY_VILLAGER, pl.getLocation().add(0, 2.5, 0), new Random().nextFloat(), new Random().nextFloat(), new Random().nextFloat(), 0.5F, 20);
			} catch(Exception e1) {
				e1.printStackTrace();
			}
			//pl.getWorld().spawnParticle(pl.getLocation().add(0, 2.5, 0), Particle.HAPPY_VILLAGER, 0.5F, 20);
			while(amount_to_buy > 0) {
				amount_to_buy--;
				pl.getInventory().setItem(pl.getInventory().firstEmpty(), product);
			}
			pl.updateInventory();
			
			shard_item_being_bought.remove(pl.getName());
			
			// TODO: Price check, give the item, take shards.
		}
		if(ecash_item_being_bought.containsKey(pl.getName())) {
			e.setCancelled(true);
			
			if(e.getMessage().equalsIgnoreCase("cancel")) {
				pl.sendMessage(ChatColor.RED + "Purchase of item - " + ChatColor.BOLD + "CANCELLED");
				ecash_item_being_bought.remove(pl.getName());
				pl.updateInventory();
				return;
			}
			
			if(pl.getInventory().firstEmpty() == -1) {
				pl.sendMessage(ChatColor.RED + "No space available in inventory. Type 'cancel' or clear some room.");
				return;
			}
			
			int space_available = 0;
			
			int amount_to_buy = 1;
			int price_per = 100;
			int i_amount = 64;
			
			int slot = ecash_item_being_bought.get(pl.getName());
			price_per = (int) Hive.getECASHPrice(eCashVendor.getItem(slot));
			/*if(slot == 0){
					price_per = 999;
				}
				if(slot == 1){
					price_per = 699;
				}
				if(slot == 2){
					price_per = 599;
				}
				if(slot == 3){
					price_per = 599;
				}
				if(slot == 4){
					price_per = 1199;
				}
				if(slot == 5){
					price_per = 199;
				}
				if(slot == 6){
					price_per = 399;
				}
				if(slot == 7){
					price_per = 3999;
				}
				if(slot == 8){
					price_per = 399;
				}
				if(slot == 9){
					price_per = 299;
				}
				if(slot == 10){
					price_per = 99;
				}
				if(slot == 11){
					price_per = 199;
				}
				if(slot == 12){
					price_per = 3999;
				}*/
			
			try {
				amount_to_buy = Integer.parseInt(e.getMessage());
			} catch(NumberFormatException ex) {
				pl.sendMessage(ChatColor.RED + "Please enter a valid integer, or type 'cancel' to void this item purchase.");
				return;
			}
			
			for(ItemStack is : pl.getInventory().getContents()) {
				if(is == null || is.getType() == Material.AIR) {
					space_available++;
					continue;
				}
			}
			
			if(amount_to_buy <= 0) {
				pl.sendMessage(ChatColor.RED + "You cannot purchase a NON-POSITIVE number.");
				return;
			}
			
			if(amount_to_buy > space_available) {
				pl.sendMessage(ChatColor.RED + "No space available in inventory. Type 'cancel' or clear some room.");
				return;
			}
			
			final int total_price = amount_to_buy * price_per;
			
			if(amount_to_buy > i_amount) {
				pl.sendMessage(ChatColor.RED + "There are only [" + ChatColor.BOLD + i_amount + ChatColor.RED + "] available.");
				return;
			}
			
			if(total_price > 0 && !Hive.doTheyHaveEnoughECASH(pl.getName(), total_price)) {
				pl.sendMessage(ChatColor.RED + "You do not have enough E-CASH to complete this purchase.");
				pl.sendMessage(ChatColor.GRAY + "" + amount_to_buy + " X " + price_per + " EC/ea = " + total_price + " EC.");
				pl.sendMessage(ChatColor.GRAY + "Purchase more E-CASH at store.dungeonrealms.net to make this awesome purchase!");
				return;
			}
			
			int total_ecash = Hive.player_ecash.get(pl.getName());
			total_ecash -= total_price;
			
			final String p_name = pl.getName();
			final int ftotal_ecash = total_ecash;
			
			Hive.player_ecash.put(pl.getName(), total_ecash);
			Thread t = new Thread(new Runnable() {
				public void run() {
					DonationMechanics.setECASH_SQL(p_name, ftotal_ecash);
				}
			});
			t.start();
			
			ItemStack product = null;
			product = ShopMechanics.removePrice(CraftItemStack.asCraftCopy(eCashVendor.getItem(slot)));
			if(slot != 1 && slot != 13) {
				product.setDurability((short) new Random().nextInt(32000));
			}
			
			/*if(slot == 0){
					product = ShopMechanics.removePrice(CraftItemStack.asCraftCopy(firework_wand));
					product.setDurability((short)new Random().nextInt(32000));
				}
				if(slot == 1){
					product = ShopMechanics.removePrice(CraftItemStack.asCraftCopy(flaming_armor));
					// Durability must be 1.
				}
				if(slot == 2){
					product = ShopMechanics.removePrice(CraftItemStack.asCraftCopy(flame_trail));
					product.setDurability((short)new Random().nextInt(32000));
				}
				if(slot == 3){
					product = ShopMechanics.removePrice(CraftItemStack.asCraftCopy(musical_spirit));
					product.setDurability((short)new Random().nextInt(32000));
				}
				if(slot == 4){
					product = ShopMechanics.removePrice(CraftItemStack.asCraftCopy(old_music_box));
					product.setDurability((short)new Random().nextInt(32000));
				}
				if(slot == 5){
					product = ShopMechanics.removePrice(CraftItemStack.asCraftCopy(global_microphone));
					product.setDurability((short)new Random().nextInt(32000));
				}
				if(slot == 6){
					product = ShopMechanics.removePrice(CraftItemStack.asCraftCopy(global_delay_buff));
					product.setDurability((short)new Random().nextInt(32000));
				}
				if(slot == 7){
					product = ShopMechanics.removePrice(CraftItemStack.asCraftCopy(increased_drops));
					product.setDurability((short)new Random().nextInt(32000));
				}
				if(slot == 8){
					product = ShopMechanics.removePrice(CraftItemStack.asCraftCopy(skeleton_horse));
					product.setDurability((short)new Random().nextInt(32000));
				}
				if(slot == 9){
					product = ShopMechanics.removePrice(CraftItemStack.asCraftCopy(undead_horse));
					product.setDurability((short)new Random().nextInt(32000));
				}
				if(slot == 10){
					product = ShopMechanics.removePrice(CraftItemStack.asCraftCopy(item_lore_tag));
					product.setDurability((short)new Random().nextInt(32000));
				}
				if(slot == 11){
					product = ShopMechanics.removePrice(CraftItemStack.asCraftCopy(item_ownership_tag));
					product.setDurability((short)new Random().nextInt(32000));
				}
				if(slot == 12){
					product = ShopMechanics.removePrice(CraftItemStack.asCraftCopy(profession_exp_boost));
					product.setDurability((short)new Random().nextInt(32000));
				}*/
			
			pl.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "-" + ChatColor.RED + total_price + ChatColor.BOLD + "EC");
			pl.sendMessage(ChatColor.GREEN + "Transaction successful.");
			pl.playSound(pl.getLocation(), Sound.ORB_PICKUP, 1F, 1F);
			try {
				ParticleEffect.sendToLocation(ParticleEffect.HAPPY_VILLAGER, pl.getLocation().add(0, 2.5, 0), new Random().nextFloat(), new Random().nextFloat(), new Random().nextFloat(), 0.5F, 20);
			} catch(Exception e1) {
				e1.printStackTrace();
			}
			//pl.getWorld().spawnParticle(pl.getLocation().add(0, 2.5, 0), Particle.HAPPY_VILLAGER, 0.5F, 20);
			while(amount_to_buy > 0) {
				amount_to_buy--;
				pl.getInventory().setItem(pl.getInventory().firstEmpty(), product);
			}
			pl.updateInventory();
			
			AchievmentMechanics.addAchievment(pl.getName(), "Supporter");
			
			if(product.getType() == Material.JUKEBOX) {
				AchievmentMechanics.addAchievment(p_name, "The Bard");
			}
			
			ecash_item_being_bought.remove(pl.getName());
		}
		
		if(skill_being_bought.containsKey(pl.getName())) {
			e.setCancelled(true);
			
			if(e.getMessage().equalsIgnoreCase("cancel")) {
				pl.sendMessage(ChatColor.RED + "Purchase of item - " + ChatColor.BOLD + "CANCELLED");
				skill_being_bought.remove(pl.getName());
				pl.updateInventory();
				return;
			}
			
			if(pl.getInventory().firstEmpty() == -1) {
				pl.sendMessage(ChatColor.RED + "No space available in inventory. Type 'cancel' or clear some room.");
				return;
			}
			
			int space_available = 0;
			
			int amount_to_buy = 1;
			int price_per = 100;
			int i_amount = 64;
			
			try {
				amount_to_buy = Integer.parseInt(e.getMessage());
			} catch(NumberFormatException ex) {
				pl.sendMessage(ChatColor.RED + "Please enter a valid integer, or type 'cancel' to void this item purchase.");
				return;
			}
			
			for(ItemStack is : pl.getInventory().getContents()) {
				if(is == null || is.getType() == Material.AIR) {
					space_available++;
					continue;
				}
			}
			
			if(amount_to_buy <= 0) {
				pl.sendMessage(ChatColor.RED + "You cannot purchase a NON-POSITIVE number.");
				return;
			}
			
			if(amount_to_buy > space_available) {
				pl.sendMessage(ChatColor.RED + "No space available in inventory. Type 'cancel' or clear some room.");
				return;
			}
			
			final int total_price = amount_to_buy * price_per;
			
			if(amount_to_buy > i_amount) {
				pl.sendMessage(ChatColor.RED + "There are only [" + ChatColor.BOLD + i_amount + ChatColor.RED + "] available.");
				return;
			}
			
			if(total_price > 0 && !RealmMechanics.doTheyHaveEnoughMoney(pl, total_price)) {
				pl.sendMessage(ChatColor.RED + "You do not have enough GEM(s) to complete this purchase.");
				pl.sendMessage(ChatColor.GRAY + "" + amount_to_buy + " X " + price_per + " gem(s)/ea = " + total_price + " gem(s).");
				return;
			}
			
			RealmMechanics.subtractMoney(pl, total_price);
			ItemStack product = null;
			if(skill_being_bought.get(pl.getName()) == 0) {
				product = ShopMechanics.removePrice(CraftItemStack.asCraftCopy(ProfessionMechanics.t1_pickaxe));
			}
			if(skill_being_bought.get(pl.getName()) == 1) {
				product = ShopMechanics.removePrice(CraftItemStack.asCraftCopy(ProfessionMechanics.t1_fishing));
			}
			
			pl.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "-" + ChatColor.RED + total_price + ChatColor.BOLD + "G");
			pl.sendMessage(ChatColor.GREEN + "Transaction successful.");
			pl.playSound(pl.getLocation(), Sound.ORB_PICKUP, 1F, 1F);
			try {
				ParticleEffect.sendToLocation(ParticleEffect.HAPPY_VILLAGER, pl.getLocation().add(0, 2.5, 0), new Random().nextFloat(), new Random().nextFloat(), new Random().nextFloat(), 0.5F, 20);
			} catch(Exception e1) {
				e1.printStackTrace();
			}
			//pl.getWorld().spawnParticle(pl.getLocation().add(0, 2.5, 0), Particle.HAPPY_VILLAGER, 0.5F, 20);
			while(amount_to_buy > 0) {
				amount_to_buy--;
				pl.getInventory().setItem(pl.getInventory().firstEmpty(), product);
			}
			pl.updateInventory();
			
			skill_being_bought.remove(pl.getName());
		}
		
		if(item_being_bought.containsKey(pl.getName())) {
			e.setCancelled(true);
			
			if(e.getMessage().equalsIgnoreCase("cancel")) {
				pl.sendMessage(ChatColor.RED + "Purchase of item - " + ChatColor.BOLD + "CANCELLED");
				item_being_bought.remove(pl.getName());
				pl.updateInventory();
				return;
			}
			
			if(pl.getInventory().firstEmpty() == -1) {
				pl.sendMessage(ChatColor.RED + "No space available in inventory. Type 'cancel' or clear some room.");
				return;
			}
			
			int amount_to_buy = 1;
			int price_per = -1;
			int i_amount = 64;
			
			int item_slot = item_being_bought.get(pl.getName());
			ItemStack product = null;
			
			if(item_slot == 0) {
				price_per = 400; // Orb of peace
				product = ShopMechanics.removePrice(CraftItemStack.asCraftCopy(ItemMechanics.orb_of_peace));
			}
			if(item_slot == 1) {
				price_per = 1000; // Orb of flight
				product = ShopMechanics.removePrice(CraftItemStack.asCraftCopy(ItemMechanics.orb_of_flight));
			}
			if(item_slot == 2) {
				price_per = 1000; // Guild armor dye
				product = ShopMechanics.removePrice(CraftItemStack.asCraftCopy(GuildMechanics.guild_dye));
			}
			
			try {
				amount_to_buy = Integer.parseInt(e.getMessage());
			} catch(NumberFormatException ex) {
				pl.sendMessage(ChatColor.RED + "Please enter a valid integer, or type 'cancel' to void this item purchase.");
				return;
			}
			
			if(amount_to_buy <= 0) {
				pl.sendMessage(ChatColor.RED + "You cannot purchase a NON-POSITIVE number.");
				return;
			}
			
			final int total_price = amount_to_buy * price_per;
			
			if(amount_to_buy > i_amount) {
				pl.sendMessage(ChatColor.RED + "There are only [" + ChatColor.BOLD + i_amount + ChatColor.RED + "] available.");
				return;
			}
			
			if(total_price > 0 && !RealmMechanics.doTheyHaveEnoughMoney(pl, total_price)) {
				pl.sendMessage(ChatColor.RED + "You do not have enough GEM(s) to complete this purchase.");
				pl.sendMessage(ChatColor.GRAY + "" + amount_to_buy + " X " + price_per + " gem(s)/ea = " + total_price + " gem(s).");
				return;
			}
			
			RealmMechanics.subtractMoney(pl, total_price);
			product.setAmount(amount_to_buy);
			
			pl.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "-" + ChatColor.RED + total_price + ChatColor.BOLD + "G");
			pl.sendMessage(ChatColor.GREEN + "Transaction successful.");
			pl.playSound(pl.getLocation(), Sound.ORB_PICKUP, 1F, 1F);
			try {
				ParticleEffect.sendToLocation(ParticleEffect.HAPPY_VILLAGER, pl.getLocation().add(0, 2.5, 0), new Random().nextFloat(), new Random().nextFloat(), new Random().nextFloat(), 0.5F, 20);
			} catch(Exception e1) {
				e1.printStackTrace();
			}
			//pl.getWorld().spawnParticle(pl.getLocation().add(0, 2.5, 0), Particle.HAPPY_VILLAGER, 0.5F, 20);
			pl.getInventory().setItem(pl.getInventory().firstEmpty(), product);
			pl.updateInventory();
			
			item_being_bought.remove(pl.getName());
		}
	}
	
	@EventHandler
	public void onDungeoneerClick(InventoryClickEvent e) {
		final Player pl = (Player) e.getWhoClicked();
		if(!(e.getInventory().getName().equalsIgnoreCase("Dungeoneer"))) { //  || !(in_npc_shop.contains(pl.getName()))
			return;
		}
		
		e.setCancelled(true);
		
		if(e.getRawSlot() <= 6 && e.getRawSlot() >= 0) {
			in_npc_shop.remove(pl.getName());
			
			new BukkitRunnable() {
				@Override
				public void run() {
					pl.closeInventory();
				}
			}.runTaskLaterAsynchronously(Main.plugin, 2L);
			
			int price = (int) MountMechanics.getShardPrice(e.getCurrentItem());
			int total_price = price * 64;
			int tier = (int) MountMechanics.getShardPriceTier(e.getCurrentItem());
			int shard_count = InstanceMechanics.getPortalShardCount(pl.getName(), tier);
			ChatColor cc = ProfessionMechanics.getTierColor(tier);
			
			if(shard_count < price) {
				pl.sendMessage(ChatColor.RED + "You do " + ChatColor.UNDERLINE + "NOT" + ChatColor.RED + " have enough " + cc + "Portal Key Shards" + ChatColor.RED + " to buy a " + ChatColor.UNDERLINE.toString() + e.getCurrentItem().getItemMeta().getDisplayName() + ".");
				pl.sendMessage(ChatColor.GOLD + "" + ChatColor.RED + "COST: " + cc + price + ChatColor.BOLD + cc + " Portal Key Shards");
				pl.sendMessage(ChatColor.GRAY + "Defeat " + ChatColor.UNDERLINE + "Instanced Dungeons" + ChatColor.GRAY + " to obtain Portal Key Shards.");
				return;
			} else {
				shard_item_being_bought.put(pl.getName(), e.getRawSlot());
				pl.sendMessage(ChatColor.GREEN + "Enter the " + ChatColor.BOLD + "QUANTITY" + ChatColor.GREEN + " you'd like to purchase.");
				pl.sendMessage(ChatColor.GRAY + "MAX: " + 64 + "X (" + total_price + "PKS), OR " + price + "PKS/each.");
			}
			
			return;
		}
	}
	
	@EventHandler
	public void onECASHInvClick(InventoryClickEvent e) {
		final Player pl = (Player) e.getWhoClicked();
		if(!(e.getInventory().getName().equalsIgnoreCase("E-CASH Vendor"))) { // || !(in_npc_shop.contains(pl.getName()))
			return;
		}
		
		e.setCancelled(true);
		
		if(e.getRawSlot() <= 17 && e.getRawSlot() >= 0) {
			in_npc_shop.remove(pl.getName());
			
			new BukkitRunnable() {
				@Override
				public void run() {
					pl.closeInventory();
				}
			}.runTaskLaterAsynchronously(Main.plugin, 2L);
			
			int price = (int) Hive.getECASHPrice(e.getCurrentItem());
			int total_price = price * 64;
			
			if(!Hive.doTheyHaveEnoughECASH(pl.getName(), price)) {
				pl.sendMessage(ChatColor.RED + "You do " + ChatColor.UNDERLINE + "NOT" + ChatColor.RED + " have enough E-CASH to buy a " + ChatColor.UNDERLINE.toString() + e.getCurrentItem().getItemMeta().getDisplayName() + ".");
				pl.sendMessage(ChatColor.GOLD + "" + ChatColor.GOLD + "COST: " + ChatColor.GOLD + price + ChatColor.GOLD + ChatColor.BOLD + " EC");
				pl.sendMessage(ChatColor.GRAY + "Purchase E-CASH at store.dungeonrealms.net");
				return;
			} else {
				ecash_item_being_bought.put(pl.getName(), e.getRawSlot());
				pl.sendMessage(ChatColor.GREEN + "Enter the " + ChatColor.BOLD + "QUANTITY" + ChatColor.GREEN + " you'd like to purchase.");
				pl.sendMessage(ChatColor.GRAY + "MAX: " + 64 + "X (" + total_price + "EC), OR " + price + "EC/each.");
			}
			
			return;
		}
	}
	
	@EventHandler
	public void onSkillTrainerInvClick(InventoryClickEvent e) {
		final Player pl = (Player) e.getWhoClicked();
		if(!(e.getInventory().getName().equalsIgnoreCase("Skill Trainer"))) { return; }
		
		e.setCancelled(true);
		
		if(e.getRawSlot() == 0 || e.getRawSlot() == 1) {
			
			in_npc_shop.remove(pl.getName());
			
			new BukkitRunnable() {
				@Override
				public void run() {
					pl.closeInventory();
				}
			}.runTaskLaterAsynchronously(Main.plugin, 2L);
			
			int total_price = 100 * 64;
			int price = 100;
			
			if(!RealmMechanics.doTheyHaveEnoughMoney(pl, 100)) {
				pl.sendMessage(ChatColor.RED + "You do NOT have enough gems to purchase this " + ChatColor.BOLD.toString() + e.getCurrentItem().getItemMeta().getDisplayName() + ".");
				pl.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "COST: " + ChatColor.RED + "100" + ChatColor.BOLD + "G");
				return;
			} else {
				skill_being_bought.put(pl.getName(), e.getRawSlot());
				pl.sendMessage(ChatColor.GREEN + "Enter the " + ChatColor.BOLD + "QUANTITY" + ChatColor.GREEN + " you'd like to purchase.");
				pl.sendMessage(ChatColor.GRAY + "MAX: " + 64 + "X (" + total_price + "g), OR " + price + "g/each.");
			}
			
			return;
		}
	}
	
	@EventHandler
	public void onInventoryClickEvent(InventoryClickEvent e) {
		final Player pl = (Player) e.getWhoClicked();
		if(!(e.getInventory().getName().equalsIgnoreCase("Item Vendor"))) { return; }
		
		e.setCancelled(true);
		
		if(e.getRawSlot() == 1) {
			in_npc_shop.remove(pl.getName());
			
			new BukkitRunnable() {
				@Override
				public void run() {
					pl.closeInventory();
				}
			}.runTaskLaterAsynchronously(Main.plugin, 2L);
			
			int total_price = 1000 * 64;
			int price = 1000;
			
			if(!RealmMechanics.doTheyHaveEnoughMoney(pl, 1000)) {
				pl.sendMessage(ChatColor.RED + "You do NOT have enough gems to purchase this Orb of Flight.");
				pl.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "COST: " + ChatColor.RED + "1,000" + ChatColor.BOLD + "G");
				return;
			} else {
				item_being_bought.put(pl.getName(), 1);
				pl.sendMessage(ChatColor.GREEN + "Enter the " + ChatColor.BOLD + "QUANTITY" + ChatColor.GREEN + " you'd like to purchase.");
				pl.sendMessage(ChatColor.GRAY + "MAX: " + 64 + "X (" + total_price + "g), OR " + price + "g/each.");
			}
			
			return;
		}
		
		if(e.getRawSlot() == 2) {
			in_npc_shop.remove(pl.getName());
			
			new BukkitRunnable() {
				@Override
				public void run() {
					pl.closeInventory();
				}
			}.runTaskLaterAsynchronously(Main.plugin, 2L);
			
			int total_price = 200 * 64;
			int price = 200;
			
			if(!RealmMechanics.doTheyHaveEnoughMoney(pl, 200)) {
				pl.sendMessage(ChatColor.RED + "You do NOT have enough gems to purchase this Guild Armor Dye.");
				pl.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "COST: " + ChatColor.RED + "200" + ChatColor.BOLD + "G");
				return;
			} else {
				item_being_bought.put(pl.getName(), 2);
				pl.sendMessage(ChatColor.GREEN + "Enter the " + ChatColor.BOLD + "QUANTITY" + ChatColor.GREEN + " you'd like to purchase.");
				pl.sendMessage(ChatColor.GRAY + "MAX: " + 64 + "X (" + total_price + "g), OR " + price + "g/each.");
			}
			
			return;
			// BUYING GUILD ARMOR DYE
		}
		
		if(RealmMechanics.isOrbOfPeace(e.getCurrentItem()) && e.getRawSlot() == 0) {
			in_npc_shop.remove(pl.getName());
			
			new BukkitRunnable() {
				@Override
				public void run() {
					pl.closeInventory();
				}
			}.runTaskLaterAsynchronously(Main.plugin, 2L);
			
			int total_price = 400 * 64;
			int price = 400;
			
			if(!RealmMechanics.doTheyHaveEnoughMoney(pl, 400)) {
				pl.sendMessage(ChatColor.RED + "You do NOT have enough gems to purchase this Orb of Peace.");
				pl.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "COST: " + ChatColor.RED + "400" + ChatColor.BOLD + "G");
				return;
			} else {
				item_being_bought.put(pl.getName(), 0); // 0 == orb of peace
				pl.sendMessage(ChatColor.GREEN + "Enter the " + ChatColor.BOLD + "QUANTITY" + ChatColor.GREEN + " you'd like to purchase.");
				pl.sendMessage(ChatColor.GRAY + "MAX: " + 64 + "X (" + total_price + "g), OR " + price + "g/each.");
			}
			
			return;
			// BUYING ORB OF PEACE
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onMerchantEvent(InventoryClickEvent e) {
		// || !(in_npc_shop.contains(((Player)e.getWhoClicked()).getName()))
		if(!(e.getInventory().getName().contains("Merchant"))) { return; }
		
		final Player clicker = (Player) e.getWhoClicked();
		Inventory tradeWin = e.getInventory();
		int slot_num = e.getRawSlot();
		
		/*if(e.isLeftClick() && e.getCursor() != null && ((slot_num < 27) || slot_num == 0 || slot_num == 1 || slot_num == 2 || slot_num == 3 || slot_num == 9 || slot_num == 10 || slot_num == 11 || slot_num == 12 || slot_num == 18 || slot_num == 19 || slot_num == 20 || slot_num == 21)){
			e.setCancelled(true);

			ItemStack current = new ItemStack(Material.AIR);
			ItemStack cursor = CraftItemStack.asCraftCopy(e.getCursor());
			if(e.getCurrentItem() != null){
				current = e.getCurrentItem();
			}

			e.setCursor(current);
			e.setCurrentItem(cursor);
			clicker.updateInventory();
			// Hack for double click stack.
		}*/
		
		if(!(e.isShiftClick()) || (e.isShiftClick() && slot_num < 27)) {
			if(!(e.getSlotType() == SlotType.CONTAINER)) { return; }
			if(e.getInventory().getType() == InventoryType.PLAYER) { return; }
			//if(e.getInventory() != clicker.getOpenInventory().getTopInventory()){return;}
			if(slot_num > 26 || slot_num < 0) { return; }
			
			if(!(slot_num == 0 || slot_num == 1 || slot_num == 2 || slot_num == 3 || slot_num == 9 || slot_num == 10 || slot_num == 11 || slot_num == 12 || slot_num == 18 || slot_num == 19 || slot_num == 20 || slot_num == 21) && !(slot_num > 27)) {
				// This prevents user from stealing other side.
				e.setCancelled(true);
				tradeWin.setItem(slot_num, tradeWin.getItem(slot_num));
				clicker.setItemOnCursor(e.getCursor());
				clicker.updateInventory();
			} else if(!(e.isShiftClick())) {
				// If the slot is one we're allowed to access.
				if((e.getCursor() == null || e.getCursor().getType() == Material.AIR) && e.getCurrentItem() != null && !(isTradeButton(e.getCurrentItem()))) {
					e.setCancelled(true);
					ItemStack in_slot = tradeWin.getItem(slot_num);
					tradeWin.setItem(slot_num, new ItemStack(Material.AIR));
					e.setCursor(in_slot);
					clicker.updateInventory();
				} else if((e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR) && e.getCursor() != null) {
					e.setCancelled(true);
					ItemStack on_cur = e.getCursor();
					tradeWin.setItem(slot_num, on_cur);
					e.setCursor(new ItemStack(Material.AIR));
					clicker.updateInventory();
				} else if(e.getCurrentItem() != null && e.getCursor() != null && !(isTradeButton(e.getCurrentItem()))) {
					e.setCancelled(true);
					ItemStack on_cur = e.getCursor();
					ItemStack in_slot = e.getCurrentItem();
					e.setCursor(in_slot);
					e.setCurrentItem(on_cur);
					clicker.updateInventory();
				}
			}
		}
		
		if(e.isShiftClick() && slot_num < 26) {
			if(!(slot_num == 0 || slot_num == 1 || slot_num == 2 || slot_num == 3 || slot_num == 9 || slot_num == 10 || slot_num == 11 || slot_num == 12 || slot_num == 18 || slot_num == 19 || slot_num == 20 || slot_num == 21) && !(slot_num > 27)) {
				// This prevents user from stealing other side.
				e.setCancelled(true);
				if(tradeWin.getItem(slot_num) != null && tradeWin.getItem(slot_num).getType() != Material.AIR) {
					tradeWin.setItem(slot_num, tradeWin.getItem(slot_num));
					clicker.updateInventory();
				}
			} else if(!(isTradeButton(e.getCurrentItem()))) {
				e.setCancelled(true);
				ItemStack in_slot = e.getCurrentItem();
				if(clicker.getInventory().firstEmpty() != -1) {
					tradeWin.setItem(slot_num, new ItemStack(Material.AIR));
					clicker.getInventory().setItem(clicker.getInventory().firstEmpty(), in_slot);
					clicker.updateInventory();
				}
			}
		}
		
		if(e.isShiftClick() && slot_num >= 27 && !(e.isCancelled()) && !(e.getCurrentItem().getType() == Material.BOOK)) {
			e.setCancelled(true);
			ItemStack to_move = e.getCurrentItem();
			//int to_move_slot = e.getRawSlot();
			int local_to_move_slot = e.getSlot();
			int x = -1;
			while(x < 26) {
				x++;
				if(!(x == 0 || x == 1 || x == 2 || x == 3 || x == 9 || x == 10 || x == 11 || x == 12 || x == 18 || x == 19 || x == 20 || x == 21)) {
					continue;
				}
				ItemStack i = tradeWin.getItem(x);
				if(!(i == null)) {
					continue;
				}
				
				tradeWin.setItem(x, to_move);
				if(tradeWin.getItem(x) != null) {
					tradeWin.getItem(x).setAmount(to_move.getAmount());
				}
				
				//clicker.getInventory().remove(local_to_move_slot);
				clicker.getInventory().setItem(local_to_move_slot, new ItemStack(Material.AIR));
				clicker.updateInventory();
				break;
			}
			
		}
		
		List<ItemStack> player_offer = new ArrayList<ItemStack>();
		int x = -1;
		while(x < 26) {
			x++;
			if(!(x == 0 || x == 1 || x == 2 || x == 3 || x == 9 || x == 10 || x == 11 || x == 12 || x == 18 || x == 19 || x == 20 || x == 21)) {
				continue;
			}
			ItemStack i = tradeWin.getItem(x);
			if(i == null || i.getType() == Material.AIR || isTradeButton(i)) {
				continue;
			}
			player_offer.add(i);
		}
		
		List<ItemStack> new_offer = generateMerchantOffer(player_offer);
		
		x = -1;
		while(x < 26) {
			x++;
			if((x == 0 || x == 1 || x == 2 || x == 3 || x == 4 || x == 9 || x == 10 || x == 11 || x == 12 || x == 13 || x == 22 || x == 18 || x == 19 || x == 20 || x == 21)) {
				continue;
			}
			
			tradeWin.setItem(x, new ItemStack(Material.AIR));
		}
		
		x = -1;
		//boolean empty = false;
		while(x < 26) {
			x++;
			//empty = true;
			
			if(new_offer.size() > 0) {
				if((x == 0 || x == 1 || x == 2 || x == 3 || x == 4 || x == 9 || x == 10 || x == 11 || x == 12 || x == 13 || x == 22 || x == 18 || x == 19 || x == 20 || x == 21)) {
					continue;
				}
				
				int index = new_offer.size() - 1;
				ItemStack i = new_offer.get(index);
				tradeWin.setItem(x, i);
				new_offer.remove(index);
				//empty = false;
			}
			/*if(empty == true){
				 tradeWin.remove(x);
			 } */
			
		}
		
		clicker.updateInventory();
		// DEPRECIATED -- Fixed .doubleClick event.
		/*if(!isTradeButton(e.getCurrentItem())){
		x = -1;
		while(x < 26){
			x++;
				if((x == 0 || x == 1 || x == 2 || x == 3 || x == 4 || x == 9 || x == 10 || x == 11 || x == 12 || x == 13 || x == 22 || x == 18 || x == 19 || x == 20 || x == 21)){
					continue;
				}

				ItemStack i = new ItemStack(Material.AIR);
				tradeWin.setItem(x, i);
		}
		}*/
		
		if(isTradeButton(e.getCurrentItem())) {
			// TODO: Accept. Nullify left side, give player right side.
			e.setCancelled(true);
			if(e.getCurrentItem().getDurability() == 8) { // Gray button
				int slots_available = 0;
				int slots_needed = 0;
				
				e.getCurrentItem().setDurability((short) 10);
				e.setCurrentItem(TradeMechanics.setIinfo(new ItemStack(Material.INK_SACK, 1, (short) 10), ChatColor.GREEN.toString() + "Trade ACCEPTED.", ChatColor.GRAY.toString() + ""));
				clicker.playSound(clicker.getLocation(), Sound.BLAZE_HIT, 1F, 2.0F);
				
				for(ItemStack i : clicker.getInventory()) {
					if(i == null || i.getType() == Material.AIR) {
						slots_available++;
					}
				}
				
				int slot_var = -1;
				while(slot_var < 26) {
					slot_var++;
					if(!(slot_var == 5 || slot_var == 6 || slot_var == 7 || slot_var == 8 || slot_var == 14 || slot_var == 15 || slot_var == 16 || slot_var == 17 || slot_var == 23 || slot_var == 24 || slot_var == 25 || slot_var == 26)) {
						continue;
					}
					ItemStack i = tradeWin.getItem(slot_var);
					if(i == null || i.getType() == Material.AIR || isTradeButton(i) || i.getType() == Material.THIN_GLASS) {
						continue;
					}
					
					slots_needed++;
				}
				
				if(slots_available < slots_needed) {
					clicker.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Not enough room.");
					clicker.sendMessage(ChatColor.GRAY + "You need " + ChatColor.BOLD + (slots_needed - slots_available) + ChatColor.RED + " more free slots to complete this trade.");
					Main.plugin.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
						public void run() {
							InventoryCloseEvent close_clicker = new InventoryCloseEvent(clicker.getOpenInventory());
							Bukkit.getServer().getPluginManager().callEvent(close_clicker);
						}
					}, 2L);
					return;
				}
				
				slot_var = -1;
				while(slot_var < 26) {
					slot_var++;
					if(!(slot_var == 5 || slot_var == 6 || slot_var == 7 || slot_var == 8 || slot_var == 14 || slot_var == 15 || slot_var == 16 || slot_var == 17 || slot_var == 23 || slot_var == 24 || slot_var == 25 || slot_var == 26)) {
						continue;
					}
					ItemStack i = tradeWin.getItem(slot_var);
					if(i == null || i.getType() == Material.AIR || isTradeButton(i) || i.getType() == Material.THIN_GLASS) {
						continue;
					}
					if(i.getType() == Material.EMERALD) {
						i = MoneyMechanics.makeGems(i.getAmount());
					}
					clicker.getInventory().setItem(clicker.getInventory().firstEmpty(), (i));
				}
				clicker.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "Trade accepted.");
				clicker.playSound(clicker.getLocation(), Sound.BLAZE_HIT, 1F, 1.5F);
				tradeWin.clear();
				
				in_npc_shop.remove(clicker.getName());
				
				Main.plugin.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
					public void run() {
						clicker.updateInventory();
						clicker.closeInventory();
					}
				}, 2L);
				
				return; // Trade accepted, GTFO.
			}
			
			clicker.updateInventory();
		}
		
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onPlayerCloseInventory(InventoryCloseEvent e) {
		Player closer = (Player) e.getPlayer();
		if(in_npc_shop.contains(closer.getName())) {
			Inventory tradeInv = e.getInventory();
			if(!tradeInv.getName().contains("Merchant")) {
				in_npc_shop.remove(closer.getName());
				return;
			}
			int slot_var = -1;
			while(slot_var < 26) {
				slot_var++;
				if(!(slot_var == 0 || slot_var == 1 || slot_var == 2 || slot_var == 3 || slot_var == 9 || slot_var == 10 || slot_var == 11 || slot_var == 12 || slot_var == 18 || slot_var == 19 || slot_var == 20 || slot_var == 21)) {
					continue;
				}
				ItemStack i = tradeInv.getItem(slot_var);
				if(i == null || i.getType() == Material.AIR || isTradeButton(i) || i.getType() == Material.THIN_GLASS) {
					continue;
				}
				
				if(i.getType() == Material.EMERALD) {
					i = MoneyMechanics.makeGems(i.getAmount());
				}
				
				if(closer.getInventory().firstEmpty() == -1) { // TODO: Need to automatically stack items on cancel.
					closer.getWorld().dropItemNaturally(closer.getLocation(), i);
				} else {
					closer.getInventory().setItem(closer.getInventory().firstEmpty(), (i));
				}
			}
			
			closer.getOpenInventory().getTopInventory().clear();
			//closer.closeInventory();
			closer.updateInventory();
			closer.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + "Trade cancelled.");
			in_npc_shop.remove(closer.getName());
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		Player closer = e.getPlayer();
		if(in_npc_shop.contains(closer.getName())) {
			Inventory tradeInv = closer.getOpenInventory().getTopInventory();
			int slot_var = -1;
			while(slot_var < 26) {
				slot_var++;
				if(!(slot_var == 0 || slot_var == 1 || slot_var == 2 || slot_var == 3 || slot_var == 9 || slot_var == 10 || slot_var == 11 || slot_var == 12 || slot_var == 18 || slot_var == 19 || slot_var == 20 || slot_var == 21)) {
					continue;
				}
				ItemStack i = tradeInv.getItem(slot_var);
				if(i == null || i.getType() == Material.AIR || isTradeButton(i) || i.getType() == Material.THIN_GLASS) {
					continue;
				}
				
				if(i.getType() == Material.EMERALD) {
					i = MoneyMechanics.makeGems(i.getAmount());
				}
				
				if(closer.getInventory().firstEmpty() == -1) { // TODO: Need to automatically stack items on cancel.
					closer.getWorld().dropItemNaturally(closer.getLocation(), i);
				} else {
					closer.getInventory().setItem(closer.getInventory().firstEmpty(), (i));
				}
			}
			
			closer.getOpenInventory().getTopInventory().clear();
			closer.closeInventory();
			closer.updateInventory();
			in_npc_shop.remove(closer.getName());
		}
	}
}
