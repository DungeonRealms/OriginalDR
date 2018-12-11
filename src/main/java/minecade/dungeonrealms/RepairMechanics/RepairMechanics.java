package minecade.dungeonrealms.RepairMechanics;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import minecade.dungeonrealms.Main;
import minecade.dungeonrealms.Utils;
import minecade.dungeonrealms.DuelMechanics.DuelMechanics;
import minecade.dungeonrealms.EnchantMechanics.EnchantMechanics;
import minecade.dungeonrealms.HealthMechanics.HealthMechanics;
import minecade.dungeonrealms.InstanceMechanics.InstanceMechanics;
import minecade.dungeonrealms.ItemMechanics.ItemMechanics;
import minecade.dungeonrealms.ProfessionMechanics.ProfessionMechanics;
import minecade.dungeonrealms.RealmMechanics.RealmMechanics;
import minecade.dungeonrealms.managers.PlayerManager;
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
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.Repairable;
import org.bukkit.util.Vector;

public class RepairMechanics implements Listener {
	static Logger log = Logger.getLogger("Minecraft");
	
	static HashMap<String, Integer> inventory_update = new HashMap<String, Integer>();
	static HashMap<String, Long> warned_durability = new HashMap<String, Long>();
	
	public static HashMap<Player, ItemStack> repair_map = new HashMap<Player, ItemStack>();
	static HashMap<Player, Item> item_repair_map = new HashMap<Player, Item>();
	static HashMap<Player, Location> anvil_map = new HashMap<Player, Location>();
	static ConcurrentHashMap<Player, Integer> repair_state = new ConcurrentHashMap<Player, Integer>();
	
	public static int attempts_per_pickaxe = 1000;
	public static int hits_per_weapon = 1500;
	public static int blocks_per_armor = 1500;
	//private static HashMap<Player, HashMap<ArmorPosition, ItemStack>> playerArmor = new HashMap<Player, HashMap<ArmorPosition, ItemStack>>();
	
	static RepairMechanics instance = null;
	
	@SuppressWarnings("deprecation")
	public void onEnable() {
		instance = this;
		Main.plugin.getServer().getPluginManager().registerEvents(this, Main.plugin);
		
		Main.plugin.getServer().getScheduler().runTaskTimerAsynchronously(Main.plugin, new Runnable() {
			public void run() {
				for(Entry<Player, Integer> data : repair_state.entrySet()) {
					int step = data.getValue();
					Player p = data.getKey();
					
					if(step < 3) { // 3 "booms"
						Item floater = item_repair_map.get(p);
						if(floater == null || !(item_repair_map.containsKey(p))) {
							repair_state.remove(p);
							continue;
						}
						Block anvil = floater.getLocation().getBlock();
						int particleID = 1;
						//log.info(floater.getType().getName().toUpperCase());
						if(floater.getItemStack().getType().name().toUpperCase().contains("BOW")) {
							particleID = 5;
						}
						if(floater.getItemStack().getType().name().toUpperCase().contains("LEATHER")) {
							particleID = 25;
						}
						if(floater.getItemStack().getType().name().toUpperCase().contains("WOOD")) {
							particleID = 5;
						}
						if(floater.getItemStack().getType().name().toUpperCase().contains("CHAIN")) {
							particleID = 30;
						}
						if(floater.getItemStack().getType().name().toUpperCase().contains("STONE")) {
							particleID = 1;
						}
						if(floater.getItemStack().getType().name().toUpperCase().contains("IRON")) {
							particleID = 42;
						}
						if(floater.getItemStack().getType().name().toUpperCase().contains("DIAMOND")) {
							particleID = 57;
						}
						if(floater.getItemStack().getType().name().toUpperCase().contains("GOLD")) {
							particleID = 41;
						}
						
						Packet particles = new PacketPlayOutWorldEvent(2001, (int) Math.round(anvil.getLocation().getX()), (int) Math.round(anvil.getLocation().getY()), (int) Math.round(anvil.getLocation().getZ()), particleID, false);
						((CraftServer) Main.plugin.getServer()).getServer().getPlayerList().sendPacketNearby(anvil.getLocation().getX(), anvil.getLocation().getY(), anvil.getLocation().getZ(), 24, ((CraftWorld) anvil.getWorld()).getHandle().dimension, particles);
						
						step += 1;
						repair_state.put(p, step);
					}
					
					if(step >= 3) {
						ItemStack i = repair_map.get(p);
						/*if(i.getItemMeta() instanceof LeatherArmorMeta){
							LeatherArmorMeta lam = (LeatherArmorMeta) i.getItemMeta();
							i.setItemMeta(lam);
						}*/
						Item im = item_repair_map.get(p);
						im.remove();
						item_repair_map.remove(p);
						repair_map.remove(p);
						repair_state.remove(p);
						i.setDurability((short) 0); // Repair!
						
						if(i.getType() == Material.WOOD_SWORD || i.getType() == Material.STONE_SWORD || i.getType() == Material.IRON_SWORD || i.getType() == Material.DIAMOND_SWORD || i.getType() == Material.GOLD_SWORD || i.getType() == Material.WOOD_AXE || i.getType() == Material.STONE_AXE || i.getType() == Material.IRON_AXE || i.getType() == Material.DIAMOND_AXE || i.getType() == Material.GOLD_AXE || i.getType() == Material.BOW) {
							// It's a weapon!
							setCustomDurability(i, hits_per_weapon, "wep", false);
						}
						
						else {
							setCustomDurability(i, blocks_per_armor, "armor", false);
						}
						
						if(p.getInventory().firstEmpty() != -1) {
							p.getInventory().setItem(p.getInventory().firstEmpty(), i);
							p.updateInventory();
						} else {
							p.getWorld().dropItemNaturally(p.getLocation(), i);
						}
						p.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "ITEM REPAIRED");
					}
				}
			}
		}, 10 * 20L, 5L);
		
		log.info("[RepairMechanics] v1.1 has been enabled.");
	}
	
	public void onDisable() {
		log.info("[RepairMechanics] has been disabled.");
	}
	
	public static double getPercentForDurabilityValue(ItemStack i, String item_type) {
		double max_dur = i.getType().getMaxDurability();
		double cur_dur = i.getType().getMaxDurability() - i.getDurability();
		if(i.getType() == Material.JACK_O_LANTERN) {
			max_dur = 1500;
			cur_dur = 1500 - i.getDurability();
			;
		}
		double dur_percent = (cur_dur / max_dur);
		//log.info("x" + (double)Math.round(dur_percent * 100));
		if(item_type.equalsIgnoreCase("wep")) { return (double) Math.round(dur_percent * (hits_per_weapon / 15)); // Will return 5%, for ex. 5% = 50 hits.
		}
		if(item_type.equalsIgnoreCase("armor")) { return (double) Math.round(dur_percent * (blocks_per_armor / 15)); // Will return 5%, for ex. 5% = 50 hits.
		}
		return 0;
	}
	
	public static double getDurabilityValueForPercent(ItemStack i, double hits_left, String item_type) { // hits_left is out of 1,000.
		double hits_left_percent = 0;
		if(item_type.equalsIgnoreCase("wep")) {
			hits_left_percent = (hits_left / hits_per_weapon);
		}
		if(item_type.equalsIgnoreCase("armor")) {
			hits_left_percent = (hits_left / blocks_per_armor);
		}
		//log.info("yt" + hits_left_percent);
		double max_dur = i.getType().getMaxDurability();
		if(i.getType() == Material.JACK_O_LANTERN) {
			max_dur = 1500;
		}
		double dur_percent = max_dur - (max_dur * hits_left_percent);
		//log.info("y" + (double)Math.round(dur_percent));
		if(dur_percent == max_dur) {
			dur_percent = max_dur - 1;
		}
		return (double) Math.round(dur_percent);
	}
	
	public static void setDurabilityValueForPercent(ItemStack i, double new_percent, String item_type) {
		// new_percent must be between 0 and 100. (1-99)
		if(!ItemMechanics.isArmor(i) && ItemMechanics.getDamageData(i).equalsIgnoreCase("no") && !ProfessionMechanics.isSkillItem(i)) { return; // Not a weapon / armor.
		}
		
		double new_dur = getDurabilityValueForPercent(i, new_percent, item_type); // Will return durability value based on % given.
		if(new_dur < 1 && new_percent < 99) {
			new_dur = 1; // Prevent item from buggging out.
		}
		//log.info("AAA - " + new_dur);
		i.setDurability((short) new_dur);
	}
	
	public static double getCustomDurability(ItemStack i, String item_type) {
		try {
			
			Repairable rp = (Repairable) i.getItemMeta();
			double durability = rp.getRepairCost();
			
			if(durability > 0) { return durability; }
			if(durability < 0) { return 0; }
			// If durability is 0 or bellow, we need to calculate what it should be.
			double dur_percent = 0;
			if(item_type.equalsIgnoreCase("wep")) {
				dur_percent = getPercentForDurabilityValue(i, item_type);
				setCustomDurability(i, (double) (dur_percent * 15), item_type, true); // dur_percent = 5%, X 10 = 50 hits.
				dur_percent = dur_percent * 15;
			}
			if(item_type.equalsIgnoreCase("armor")) {
				dur_percent = getPercentForDurabilityValue(i, item_type);
				setCustomDurability(i, (double) (dur_percent * 15), item_type, true); // dur_percent = 5%, X 10 = 50 hits.
				dur_percent = dur_percent * 15;
			}
			
			return (double) dur_percent;
			
		} catch(Exception e) {
			e.printStackTrace();
			log.info("[RepairMechanics] Item durability not registered, getting % value and registering. " + i.toString());
			
			double dur_percent = getPercentForDurabilityValue(i, item_type);
			if(item_type.equalsIgnoreCase("wep")) {
				setCustomDurability(i, (double) (dur_percent * 15), item_type, true); // dur_percent = 5%, X 10 = 50 hits.
				dur_percent = dur_percent * 15;
			}
			if(item_type.equalsIgnoreCase("armor")) {
				setCustomDurability(i, (double) (dur_percent * 15), item_type, true); // dur_percent = 5%, X 10 = 50 hits.
				dur_percent = dur_percent * 15;
			}
			
			return dur_percent;
		}
	}
	
	@SuppressWarnings("deprecation")
	public static void subtractCustomDurability(final Player p, final ItemStack i, double amount, String item_type, boolean ignoreSafe) {
		double cur_dur = getCustomDurability(i, item_type);
		double new_dur = (cur_dur - amount);
		
		if(!ItemMechanics.isArmor(i) && ItemMechanics.getDamageData(i).equalsIgnoreCase("no") && !ProfessionMechanics.isSkillItem(i)) { return; // Not a weapon / armor.
		}
		
		if(new_dur <= 0.1D) {
			if(i.getType() == Material.SKULL_ITEM) {
				p.getInventory().setHelmet(new ItemStack(Material.AIR));
				p.updateInventory();
				return;
			}
			if(item_type.equalsIgnoreCase("wep")) { // Break the weapon!
				if(ProfessionMechanics.isSkillItem(i) && ProfessionMechanics.getItemLevel(i) == 100) {
					// TODO: Transform to T1 pickaxe w/ stats.
					p.setItemInHand(ProfessionMechanics.resetToNoviceSkillItem(i));
					p.playSound(p.getLocation(), Sound.LEVEL_UP, 1F, 1F);
				} else {
					p.setItemInHand(new ItemStack(Material.AIR, 1));
					p.playSound(p.getLocation(), Sound.ITEM_BREAK, 1F, 1F);
					p.updateInventory();
					
					Main.plugin.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
						public void run() {
							if(p.getItemInHand() == i) {
								p.setItemInHand(new ItemStack(Material.AIR, 1));
								//	p.playSound(p.getLocation(), Sound.ITEM_BREAK, 1F, 1F);
								p.updateInventory();
							}
						}
					}, 10L);
				}
				return;
			}
			if(item_type.equalsIgnoreCase("armor")) {
				if(i.getType() == Material.LEATHER_BOOTS || i.getType() == Material.CHAINMAIL_BOOTS || i.getType() == Material.IRON_BOOTS || i.getType() == Material.DIAMOND_BOOTS || i.getType() == Material.GOLD_BOOTS) {
					p.getInventory().setBoots(new ItemStack(Material.AIR, 1));
				}
				if(i.getType() == Material.LEATHER_LEGGINGS || i.getType() == Material.CHAINMAIL_LEGGINGS || i.getType() == Material.IRON_LEGGINGS || i.getType() == Material.DIAMOND_LEGGINGS || i.getType() == Material.GOLD_LEGGINGS) {
					p.getInventory().setLeggings(new ItemStack(Material.AIR, 1));
				}
				if(i.getType() == Material.LEATHER_CHESTPLATE || i.getType() == Material.CHAINMAIL_CHESTPLATE || i.getType() == Material.IRON_CHESTPLATE || i.getType() == Material.DIAMOND_CHESTPLATE || i.getType() == Material.GOLD_CHESTPLATE) {
					p.getInventory().setChestplate(new ItemStack(Material.AIR, 1));
				}
				if(i.getType() == Material.LEATHER_HELMET || i.getType() == Material.CHAINMAIL_HELMET || i.getType() == Material.IRON_HELMET || i.getType() == Material.DIAMOND_HELMET || i.getType() == Material.GOLD_HELMET) {
					p.getInventory().setHelmet(new ItemStack(Material.AIR, 1));
				}
				p.updateInventory();
				
				int old_max = HealthMechanics.health_data.get(p.getName());
				int new_max = HealthMechanics.generateMaxHP(p);
				int dif = old_max - new_max;
				
				String new_armor_name = ChatColor.GRAY.toString() + "NOTHING";
				CraftItemStack css = (CraftItemStack) i;
				String i_name = "";
				
				if(HealthMechanics.hasCustomName(i)) {
					i_name = CraftItemStack.asNMSCopy(css).getTag().getCompound("display").getString("Name");
				} else {
					i_name = i.getType().toString();
				}
				
				Random r = new Random();
				float minX = 0.20f;
				float maxX = 0.25f;
				float pitch_mod = 1.0F - (r.nextFloat() * (maxX - minX) + minX);
				p.playSound(p.getLocation(), Sound.ITEM_BREAK, 1.00F, pitch_mod);
				
				HealthMechanics.health_data.put(p.getName(), new_max);
				ItemMechanics.need_update.add(p.getName());
				
				HealthMechanics.setPlayerHP(p.getName(), new_max);
				//p.setLevel(new_max);
				p.setHealth(20);
				
				p.sendMessage("");
				p.sendMessage(ChatColor.WHITE + "" + i_name + "" + ChatColor.WHITE + ChatColor.BOLD + " -> " + ChatColor.WHITE + "" + new_armor_name + "");
				p.sendMessage(ChatColor.RED + "-" + dif + " MAX HP [" + new_max + "/" + new_max + "HP]");
				
			}
		}
		
		else if(new_dur > 0.1D) {
			setCustomDurability(i, (double) new_dur, item_type, true);
		}
	}
	
	@SuppressWarnings("deprecation")
	public static void subtractCustomDurability(final Player p, final ItemStack i, double amount, String item_type) {
		
		if(DuelMechanics.isDamageDisabled(p.getLocation()) && (!(ProfessionMechanics.isSkillItem(i)))) {
			amount = 0;
		}
		
		boolean update_inv = true;
		
		double cur_dur = getCustomDurability(i, item_type);
		double new_dur = (cur_dur - amount);
		
		if(!ItemMechanics.isArmor(i) && ItemMechanics.getDamageData(i).equalsIgnoreCase("no") && !ProfessionMechanics.isSkillItem(i)) { return; // Not a weapon / armor.
		}
		
		if((!warned_durability.containsKey(i)) || ((System.currentTimeMillis() - warned_durability.get(i)) > 60 * 1000)) {
			
			if(item_type.equalsIgnoreCase("armor") && new_dur <= 150 && new_dur >= 140) {
				// 10%
				p.playSound(p.getLocation(), Sound.ANVIL_BREAK, 0.5F, 1F);
				p.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "      *10% DURABILITY " + ChatColor.RED + "LEFT ON " + i.getItemMeta().getDisplayName() + ChatColor.RED.toString() + "*");
				warned_durability.put(i.getItemMeta().getDisplayName(), System.currentTimeMillis());
			}
			
			if(item_type.equalsIgnoreCase("armor") && new_dur <= 30 && new_dur >= 20) {
				// 2%
				p.playSound(p.getLocation(), Sound.ANVIL_BREAK, 0.5F, 1F);
				p.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "      *2% DURABILITY " + ChatColor.RED + "LEFT ON " + i.getItemMeta().getDisplayName() + ChatColor.RED.toString() + "*");
				warned_durability.put(i.getItemMeta().getDisplayName(), System.currentTimeMillis());
			}
			
			if(item_type.equalsIgnoreCase("wep") && new_dur <= 100 && new_dur >= 90) {
				// 10%
				p.playSound(p.getLocation(), Sound.ANVIL_BREAK, 0.5F, 1F);
				p.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "      *10% DURABILITY " + ChatColor.RED + "LEFT ON " + i.getItemMeta().getDisplayName() + ChatColor.RED.toString() + "*");
				warned_durability.put(i.getItemMeta().getDisplayName(), System.currentTimeMillis());
			}
			
			if(item_type.equalsIgnoreCase("wep") && new_dur <= 20 && new_dur >= 10) {
				// 2%
				p.playSound(p.getLocation(), Sound.ANVIL_BREAK, 0.5F, 1F);
				p.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "      *2% DURABILITY " + ChatColor.RED + "LEFT ON " + i.getItemMeta().getDisplayName() + ChatColor.RED.toString() + "*");
				warned_durability.put(i.getItemMeta().getDisplayName(), System.currentTimeMillis());
			}
			
		}
		
		if(new_dur <= 0.1D) {
			if(i.getType() == Material.SKULL_ITEM) {
				p.getInventory().setHelmet(new ItemStack(Material.AIR));
				p.updateInventory();
				return;
			}
			if(item_type.equalsIgnoreCase("wep")) { // Break the weapon!
				if(ProfessionMechanics.isSkillItem(i) && ProfessionMechanics.getItemLevel(i) == 100) {
					// TODO: Transform to T1 pickaxe w/ stats.
					p.setItemInHand(ProfessionMechanics.resetToNoviceSkillItem(i));
					p.playSound(p.getLocation(), Sound.LEVEL_UP, 1F, 1F);
				} else {
					p.setItemInHand(new ItemStack(Material.AIR, 1));
					p.playSound(p.getLocation(), Sound.ITEM_BREAK, 1F, 1F);
					p.updateInventory();
					
					Main.plugin.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
						public void run() {
							if(p.getItemInHand() == i) {
								p.setItemInHand(new ItemStack(Material.AIR, 1));
								//	p.playSound(p.getLocation(), Sound.ITEM_BREAK, 1F, 1F);
								p.updateInventory();
							}
						}
					}, 10L);
				}
				return;
			}
			if(item_type.equalsIgnoreCase("armor")) {
				if(i.getType() == Material.LEATHER_BOOTS || i.getType() == Material.CHAINMAIL_BOOTS || i.getType() == Material.IRON_BOOTS || i.getType() == Material.DIAMOND_BOOTS || i.getType() == Material.GOLD_BOOTS) {
					p.getInventory().setBoots(new ItemStack(Material.AIR, 1));
				}
				if(i.getType() == Material.LEATHER_LEGGINGS || i.getType() == Material.CHAINMAIL_LEGGINGS || i.getType() == Material.IRON_LEGGINGS || i.getType() == Material.DIAMOND_LEGGINGS || i.getType() == Material.GOLD_LEGGINGS) {
					p.getInventory().setLeggings(new ItemStack(Material.AIR, 1));
				}
				if(i.getType() == Material.LEATHER_CHESTPLATE || i.getType() == Material.CHAINMAIL_CHESTPLATE || i.getType() == Material.IRON_CHESTPLATE || i.getType() == Material.DIAMOND_CHESTPLATE || i.getType() == Material.GOLD_CHESTPLATE) {
					p.getInventory().setChestplate(new ItemStack(Material.AIR, 1));
				}
				if(i.getType() == Material.LEATHER_HELMET || i.getType() == Material.CHAINMAIL_HELMET || i.getType() == Material.IRON_HELMET || i.getType() == Material.DIAMOND_HELMET || i.getType() == Material.GOLD_HELMET) {
					p.getInventory().setHelmet(new ItemStack(Material.AIR, 1));
				}
				p.updateInventory();
				
				int old_max = HealthMechanics.health_data.get(p.getName());
				int new_max = HealthMechanics.generateMaxHP(p);
				int dif = old_max - new_max;
				
				String new_armor_name = ChatColor.GRAY.toString() + "NOTHING";
				CraftItemStack css = (CraftItemStack) i;
				String i_name = "";
				
				if(HealthMechanics.hasCustomName(i)) {
					i_name = CraftItemStack.asNMSCopy(css).getTag().getCompound("display").getString("Name");
				} else {
					i_name = i.getType().toString();
				}
				
				Random r = new Random();
				float minX = 0.20f;
				float maxX = 0.25f;
				float pitch_mod = 1.0F - (r.nextFloat() * (maxX - minX) + minX);
				p.playSound(p.getLocation(), Sound.ITEM_BREAK, 1.00F, pitch_mod);
				
				HealthMechanics.health_data.put(p.getName(), new_max);
				ItemMechanics.need_update.add(p.getName());
				
				HealthMechanics.setPlayerHP(p.getName(), new_max);
				//p.setLevel(new_max);
				p.setHealth(20);
				
				p.sendMessage("");
				p.sendMessage(ChatColor.WHITE + "" + i_name + "" + ChatColor.WHITE + ChatColor.BOLD + " -> " + ChatColor.WHITE + "" + new_armor_name + "");
				p.sendMessage(ChatColor.RED + "-" + dif + " MAX HP [" + new_max + "/" + new_max + "HP]");
				
			}
		}
		
		else if(new_dur > 0.1D) {
			setCustomDurability(i, (double) new_dur, item_type, true);
		}
		
		if(update_inv == true && item_type.equalsIgnoreCase("wep")) {
			p.updateInventory();
		}
	}
	
	public static void setCustomDurability(ItemStack i, double new_dur, String item_type, boolean update_bar) { // Custom Durability = hits left.
		try {
			
			Repairable rp = (Repairable) i.getItemMeta();
			rp.setRepairCost((int) new_dur);
			i.setItemMeta((ItemMeta) rp);
			
			int enchant_level = EnchantMechanics.getEnchantCount(i);
			if(enchant_level >= 4) {
				// Glowing effect.
				EnchantMechanics.addGlow(i);
			}
			
			if(update_bar == true) {
				setDurabilityValueForPercent(i, new_dur, item_type); // Takes care of durability bar.
			}
		} catch(Exception e) {
			log.info("[RepairMechanics] Item durability could not be set, culprit: " + i.toString());
		}
	}
	
	public int getRepairCost(ItemStack i) {
		double repair_cost = 0;
		int item_tier = ItemMechanics.getItemTier(i);
		String dmg_range = ItemMechanics.getDamageRange(i);
		String armor_range = ItemMechanics.getArmorData(i);
		
		if(armor_range.equalsIgnoreCase("no") && dmg_range.equalsIgnoreCase("no") && !(ProfessionMechanics.isSkillItem(i))) { return 0; // Not a custom item.
		}
		if(item_tier == 0) { return 0; // Not a custom item.
		}
		
		if (Utils.isBeta()) return 1; // free on beta shards
		
		if(dmg_range.equalsIgnoreCase("no") && !(armor_range.equalsIgnoreCase("no"))) { // It's a piece of armor.	
			double avg_armor = Integer.parseInt(armor_range.split("-")[0].replaceAll(" ", "").replace("!", "")) + Integer.parseInt(armor_range.split("-")[1].substring(0, armor_range.split("-")[1].indexOf(":")).replaceAll(" ", "").replace("!", ""));
			avg_armor = avg_armor / 2; // Get the average of the two added values.
			double percent_durability_left = getPercentForDurabilityValue(i, "armor");
			if(percent_durability_left > 99) {
				percent_durability_left = 99;
			}
			double armor_cost = avg_armor * 1; // This is the cost PER PERCENT
			
			double global_multiplier = 0.30 - 0.06; // Additional 0.06 less
			double multiplier = 1;
			double missing_percent = 100 - percent_durability_left;
			double total_armor_cost = missing_percent * armor_cost;
			
			if(item_tier == 1) {
				multiplier = 1.0;
				repair_cost = total_armor_cost * multiplier;
			}
			if(item_tier == 2) {
				multiplier = 1.25;
				repair_cost = total_armor_cost * multiplier;
			}
			if(item_tier == 3) {
				multiplier = 1.5;
				repair_cost = total_armor_cost * multiplier;
			}
			if(item_tier == 4) {
				multiplier = 3.75;
				repair_cost = total_armor_cost * multiplier;
			}
			if(item_tier == 5) {
				multiplier = 6.0;
				repair_cost = total_armor_cost * multiplier;
			}
			
			repair_cost = repair_cost * global_multiplier;
		}
		
		if(armor_range.equalsIgnoreCase("no") && !(dmg_range.equalsIgnoreCase("no"))) { // It's a weapon.
			double avg_dmg = (Integer.parseInt((dmg_range.split("-")[0])) + Integer.parseInt(dmg_range.split("-")[1])) / 2; // Average DMG
			double dmg_cost = avg_dmg * 0.1; // This is the cost PER PERCENT
			
			double percent_durability_left = getPercentForDurabilityValue(i, "wep");
			if(percent_durability_left > 99) {
				percent_durability_left = 99;
			}
			
			double global_multiplier = 0.25 - 0.05;
			double multiplier = 1.0; // 100%
			double missing_percent = 100 - percent_durability_left;
			double total_dmg_cost = missing_percent * dmg_cost;
			
			if(item_tier == 1) {
				multiplier = 1.0;
				repair_cost = total_dmg_cost * multiplier;
			}
			if(item_tier == 2) {
				multiplier = 1.25;
				repair_cost = total_dmg_cost * multiplier;
			}
			if(item_tier == 3) {
				multiplier = 2.0;
				repair_cost = total_dmg_cost * multiplier;
			}
			if(item_tier == 4) {
				multiplier = 6.0;
				repair_cost = total_dmg_cost * multiplier;
			}
			if(item_tier == 5) {
				multiplier = 9.0;
				repair_cost = total_dmg_cost * multiplier;
			}
			
			repair_cost = repair_cost * global_multiplier;
		}
		
		if(ProfessionMechanics.isSkillItem(i)) {
			if(ProfessionMechanics.getItemLevel(i) == 100) { return 0; // Cannot repair LVL 100.
			}
			double dmg_cost = Math.pow(ProfessionMechanics.getItemLevel(i), 2) / 100D; // This is the cost PER PERCENT
			double percent_durability_left = getPercentForDurabilityValue(i, "wep");
			if(percent_durability_left > 99) {
				percent_durability_left = 99;
			}
			
			double global_multiplier = 0.8;
			double multiplier = 1.0; // 100%
			double missing_percent = 100 - percent_durability_left;
			double total_dmg_cost = missing_percent * dmg_cost;
			
			if(item_tier == 1) {
				multiplier = 0.5;
				repair_cost = total_dmg_cost * multiplier;
			}
			if(item_tier == 2) {
				multiplier = 0.75;
				repair_cost = total_dmg_cost * multiplier;
			}
			if(item_tier == 3) {
				multiplier = 1.0;
				repair_cost = total_dmg_cost * multiplier;
			}
			if(item_tier == 4) {
				multiplier = 2.0;
				repair_cost = total_dmg_cost * multiplier;
			}
			if(item_tier == 5) {
				multiplier = 3.0;
				repair_cost = total_dmg_cost * multiplier;
			}
			
			repair_cost = repair_cost * global_multiplier;
		}
		
		if(repair_cost < 1) {
			repair_cost = 1;
		}
		
		return (int) Math.round(repair_cost);
	}
	
	public static boolean isArmorScrap(ItemStack is) {
		if(is.getType() == Material.LEATHER || is.getType() == Material.IRON_FENCE || is.getType() == Material.INK_SACK) {
			if(is.getType() == Material.INK_SACK) {
				short meta = is.getDurability();
				if(meta != 7 && meta != 11 && meta != 12) { return false; }
			}
			if(is.hasItemMeta() && is.getItemMeta().hasDisplayName()) {
				if(is.getItemMeta().getDisplayName().toLowerCase().contains("scrap")) { return true; }
			}
		}
		return false;
	}
	
	public double getPercentRecoverOfScrap(int tier) {
		if(tier == 1) { return 3.0D; }
		if(tier == 2) { return 3.0D; }
		if(tier == 3) { return 3.0D; }
		if(tier == 4) { return 3.0D; }
		if(tier == 5) { return 3.0D; }
		return 0.0D;
	}
	
	/*@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
	public void onHitEvent(EntityDamageEvent e) {
		if(!(e.getEntity() instanceof Player)) return;
		Player p = (Player) e.getEntity();
		if(!playerArmor.containsKey(p)) playerArmor.put(p, new HashMap<ArmorPosition, ItemStack>());
		HashMap<ArmorPosition, ItemStack> armor = playerArmor.get(p);
		if(p.getInventory().getHelmet() != null && p.getInventory().getHelmet().getType() != Material.AIR) armor.put(ArmorPosition.HEAD, p.getInventory().getHelmet());
		if(p.getInventory().getChestplate() != null && p.getInventory().getChestplate().getType() != Material.AIR) armor.put(ArmorPosition.CHEST, p.getInventory().getChestplate());
		if(p.getInventory().getLeggings() != null && p.getInventory().getLeggings().getType() != Material.AIR) armor.put(ArmorPosition.LEGS, p.getInventory().getLeggings());
		if(p.getInventory().getBoots() != null && p.getInventory().getBoots().getType() != Material.AIR) armor.put(ArmorPosition.BOOTS, p.getInventory().getBoots());
	}*/
	//Calling this in another class now gg
	/*long last_tell = System.currentTimeMillis();
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = false)
	public void onArmorDamageEvent(EntityDamageEvent e) {
		if(!(e.getEntity() instanceof Player)) { return; }
		Player p = (Player) e.getEntity();
		if(!(p.getGameMode() == GameMode.SURVIVAL)) { 
		    System.out.print("(EDE) Cancelled due to being in creative!!");
		    return; 
		    }
		if(e.getDamage() <= 1) {
		    if(System.currentTimeMillis() > last_tell){
		    System.out.print("Damage was less then 1!");
		    //10 second intervals
		    last_tell = System.currentTimeMillis() + 10000;
		    }
		    return; 
		 }
		
		if(p.getInventory().getBoots() != null && p.getInventory().getBoots().getType() != Material.AIR) {
			ItemStack boots = p.getInventory().getBoots();
			subtractCustomDurability(p, boots, 1, "armor");
			log.info("BOOTS: " + getCustomDurability(boots, "armor"));
		}
		if(p.getInventory().getLeggings() != null && p.getInventory().getLeggings().getType() != Material.AIR) {
			ItemStack Leggings = p.getInventory().getLeggings();
			subtractCustomDurability(p, Leggings, 1, "armor");
			log.info("LEGS: " + getCustomDurability(Leggings, "armor"));
		}
		if(p.getInventory().getChestplate() != null && p.getInventory().getChestplate().getType() != Material.AIR) {
			ItemStack Chestplate = p.getInventory().getChestplate();
			subtractCustomDurability(p, Chestplate, 1, "armor");
			log.info("CHEST: " + getCustomDurability(Chestplate, "armor"));
		}
		if(p.getInventory().getHelmet() != null && p.getInventory().getHelmet().getType() != Material.AIR) {
			ItemStack Helmet = p.getInventory().getHelmet();
			subtractCustomDurability(p, Helmet, 1, "armor");
			log.info("HELMET: " + getCustomDurability(Helmet, "armor"));
		}
	}*/
	
	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityDamge(EntityDamageEvent e) {
		if(e.getEntity() instanceof Player) {
			Player p = (Player) e.getEntity();
			if(repair_map.containsKey(p.getName())) {
				ItemStack i = repair_map.get(p);
				repair_map.remove(p);
				anvil_map.remove(p);
				Item im = item_repair_map.get(p);
				im.remove();
				item_repair_map.remove(p);
				p.sendMessage(ChatColor.RED + "Item Repair - " + ChatColor.BOLD + "CANCELLED");
				if(p.getInventory().firstEmpty() != -1) {
					p.getInventory().setItem(p.getInventory().firstEmpty(), i);
					p.updateInventory();
				} else {
					p.getWorld().dropItemNaturally(p.getLocation(), i); // If they have no room, drop the item.
				}
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onPlayerUseScrapMat(InventoryClickEvent e) {
		if(e.getCursor() == null) { return; }
		if(e.getCurrentItem() == null) { return; }
		ItemStack cursor = e.getCursor();
		ItemStack in_slot = e.getCurrentItem();
		if(!e.getInventory().getName().equalsIgnoreCase("container.crafting")) { return; }
		if(e.getInventory().getViewers().size() > 1) { return; }
		if(e.getSlotType() == SlotType.ARMOR) { return; }
		
		Player p = (Player) e.getWhoClicked();
		if(isArmorScrap(cursor) && (ItemMechanics.isArmor(in_slot) || !ItemMechanics.getDamageData(in_slot).equalsIgnoreCase("no") || ProfessionMechanics.isSkillItem(in_slot))) {
			if(ProfessionMechanics.isSkillItem(in_slot) && ProfessionMechanics.getItemLevel(in_slot) == 100) { return; // Cannot repair LVL 100 pickaxes.
			}
			String item_type = "";
			
			if(!ItemMechanics.getDamageData(in_slot).equalsIgnoreCase("no") || ProfessionMechanics.isSkillItem(in_slot)) {
				item_type = "wep";
			}
			if(ItemMechanics.isArmor(in_slot)) {
				item_type = "armor";
			}
			
			int scrap_tier = ItemMechanics.getItemTier(cursor);
			int item_tier = ItemMechanics.getItemTier(in_slot);
			
			if((scrap_tier) != item_tier) { return;
			// Not the right kind of scrap.
			}
			
			double percent_to_recover = getPercentRecoverOfScrap(scrap_tier);
			if(percent_to_recover == 0.0D) {
				// Not meant to recover anything, gold or something.
				return;
			}
			
			if(in_slot.getDurability() == 0) { return; }
			
			if(cursor.getAmount() == 1) {
				e.setCancelled(true);
				e.setCursor(new ItemStack(Material.AIR));
			} else if(cursor.getAmount() > 1) {
				e.setCancelled(true);
				cursor.setAmount(cursor.getAmount() - 1);
				e.setCursor(cursor);
			}
			
			// Now we give durability back to the item it's being applied on.
			double percent_remaining = 100.0D;
			percent_remaining = getPercentForDurabilityValue(in_slot, item_type);
			
			if(percent_remaining + percent_to_recover >= 99.0D) {
				if(item_type.equalsIgnoreCase("wep")) {
					setCustomDurability(in_slot, hits_per_weapon, "wep", true);
				}
				if(item_type.equalsIgnoreCase("armor")) {
					setCustomDurability(in_slot, blocks_per_armor, "armor", true);
				}
				p.updateInventory();
			}
			
			else if(percent_remaining + percent_to_recover < 99.0D) {
				//setDurabilityValueForPercent(in_slot, percent_remaining + percent_to_recover, item_type);
				double percent_to_add = (percent_to_recover / 100.D);
				double current_dur = getCustomDurability(in_slot, item_type);
				if(item_type.equalsIgnoreCase("wep")) {
					setCustomDurability(in_slot, current_dur + (hits_per_weapon * percent_to_add), "wep", true);
				}
				if(item_type.equalsIgnoreCase("armor")) {
					setCustomDurability(in_slot, current_dur + (blocks_per_armor * percent_to_add), "armor", true);
				}
				p.updateInventory();
			}
			
			int particleID = 1;
			//log.info(floater.getType().getName().toUpperCase());
			if(in_slot.getType().name().toUpperCase().contains("BOW")) {
				particleID = 5;
			}
			if(in_slot.getType().name().toUpperCase().contains("LEATHER")) {
				particleID = 25;
			}
			if(in_slot.getType().name().toUpperCase().contains("WOOD")) {
				particleID = 5;
			}
			if(in_slot.getType().name().toUpperCase().contains("CHAIN")) {
				particleID = 30;
			}
			if(in_slot.getType().name().toUpperCase().contains("STONE")) {
				particleID = 1;
			}
			if(in_slot.getType().name().toUpperCase().contains("IRON")) {
				particleID = 42;
			}
			if(in_slot.getType().name().toUpperCase().contains("DIAMOND")) {
				particleID = 57;
			}
			if(in_slot.getType().name().toUpperCase().contains("GOLD")) {
				particleID = 41;
			}
			
			Packet particles = new PacketPlayOutWorldEvent(2001, (int) Math.round(p.getLocation().getX()), (int) Math.round(p.getLocation().getY() + 2), (int) Math.round(p.getLocation().getZ()), particleID, false);
			((CraftServer) Main.plugin.getServer()).getServer().getPlayerList().sendPacketNearby(p.getLocation().getX(), p.getLocation().getY(), p.getLocation().getZ(), 36, ((CraftWorld) p.getWorld()).getHandle().dimension, particles);
			
			if(PlayerManager.getPlayerModel(p).getToggleList() != null){
				if(PlayerManager.getPlayerModel(p).getToggleList().contains("debug")){
					double new_percent = getPercentForDurabilityValue(in_slot, item_type);
					p.sendMessage(ChatColor.GREEN + "                  " + ChatColor.BOLD + "+" + ChatColor.GREEN + percent_to_recover + "% DURABILITY" + ChatColor.BOLD + " -> " + ChatColor.GREEN + new_percent + "% TOTAL");
				}
			}
			
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onPlayerChatAsyncEvent(AsyncPlayerChatEvent e) {
		if(!(anvil_map.containsKey(e.getPlayer()))) { return; } // We have no business.
		Player p = e.getPlayer();
		ItemStack i = repair_map.get(p);
		e.setCancelled(true);
		
		if(e.getMessage().equalsIgnoreCase("n")) {
			if(p.getInventory().firstEmpty() == -1) {
				p.sendMessage(ChatColor.RED + "You don't have enough room in your inventory to cancel this repair request.");
				return;
			}
			repair_map.remove(p);
			anvil_map.remove(p);
			Item im = item_repair_map.get(p);
			im.remove();
			item_repair_map.remove(p);
			p.sendMessage(ChatColor.RED + "Item Repair - " + ChatColor.BOLD + "CANCELLED");
			p.getInventory().setItem(p.getInventory().firstEmpty(), i);
			p.updateInventory();
			return;
		}
		
		if(e.getMessage().equalsIgnoreCase("y")) {
			int repair_cost = getRepairCost(i);
			if(p.getInventory().firstEmpty() == -1) {
				p.sendMessage(ChatColor.RED + "You don't have enough room in your inventory to confirm this repair request.");
				return;
			}
			
			if(!(RealmMechanics.doTheyHaveEnoughMoney(p, repair_cost))) {
				p.sendMessage(ChatColor.RED + "You do not have enough " + ChatColor.BOLD + "GEM(s)" + ChatColor.RED + " to repair this item.");
				repair_map.remove(p);
				anvil_map.remove(p);
				Item im = item_repair_map.get(p);
				im.remove();
				item_repair_map.remove(p);
				p.getInventory().setItem(p.getInventory().firstEmpty(), i);
				p.updateInventory();
				return;
			}
			
			RealmMechanics.subtractMoney(p, repair_cost);
			p.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "-" + ChatColor.RED + repair_cost + ChatColor.BOLD + "G");
			anvil_map.remove(p);
			
			p.playSound(p.getLocation(), Sound.ANVIL_USE, 1F, 1F);
			repair_state.put(p, 0);
			return;
		}
		
		// If they reach this point, entered wrong message.
		p.sendMessage(ChatColor.RED + "Invalid option.");
		p.sendMessage(ChatColor.GRAY + "Type " + ChatColor.GREEN + "" + ChatColor.BOLD + "Y" + ChatColor.GRAY + " to confirm this repair. Or type " + ChatColor.RED + ChatColor.BOLD + "N" + ChatColor.GRAY + " to cancel.");
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		if(repair_map.containsKey(p)) {
			ItemStack i = repair_map.get(p);
			repair_map.remove(p);
			anvil_map.remove(p);
			Item im = item_repair_map.get(p);
			im.remove();
			item_repair_map.remove(p);
			p.getInventory().setItem(p.getInventory().firstEmpty(), i);
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent e) {
		if(!(anvil_map.containsKey(e.getPlayer()))) { return; }
		Location new_loc = e.getTo();
		if(new_loc.distanceSquared(anvil_map.get(e.getPlayer())) > 36.0D) {
			Player p = e.getPlayer();
			ItemStack i = repair_map.get(p);
			repair_map.remove(p);
			anvil_map.remove(p);
			Item im = item_repair_map.get(p);
			im.remove();
			item_repair_map.remove(p);
			p.sendMessage(ChatColor.RED + "Item Repair - " + ChatColor.BOLD + "CANCELLED");
			p.sendMessage(ChatColor.GRAY + "You moved too far away from the anvil!");
			p.getInventory().setItem(p.getInventory().firstEmpty(), i);
			p.updateInventory();
			return;
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onEntityShootBowEvent(EntityShootBowEvent e) {
		if(!(e.getEntity().getType() == EntityType.PLAYER)) { return; }
		
		Player p = (Player) e.getEntity();
		//Arrow a = (Arrow)e.getProjectile();
		
		subtractCustomDurability(p, e.getBow(), 1, "wep");
	}
	
	   @EventHandler (priority = EventPriority.LOWEST)
	    public void onPlayerEnvironmentDamage(EntityDamageEvent e) {
	        if (!(e.getEntity() instanceof Player)) return;
        if (e.getCause() != DamageCause.LAVA && e.getCause() != DamageCause.DROWNING
                && e.getCause() != DamageCause.SUFFOCATION && e.getCause() != DamageCause.CONTACT)
            return;
	        
	        Player p = (Player) e.getEntity();
	        
	        e.setCancelled(true);
	        
	        // adjust vanilla damage to damage as a percentage of player's max health
	        if (e.getDamage() < 20) p.damage((e.getDamage() / 20) * HealthMechanics.getMaxHealthValue(p.getName()));
	        
	        if(p.getInventory().getBoots() != null && p.getInventory().getBoots().getType() != Material.AIR) {
	            ItemStack boots = p.getInventory().getBoots();
	            subtractCustomDurability(p, boots, 1, "armor");
	            log.info("BOOTS: " + getCustomDurability(boots, "armor"));
	        }
	        if(p.getInventory().getLeggings() != null && p.getInventory().getLeggings().getType() != Material.AIR) {
	            ItemStack Leggings = p.getInventory().getLeggings();
	            subtractCustomDurability(p, Leggings, 1, "armor");
	            log.info("LEGS: " + getCustomDurability(Leggings, "armor"));
	        }
	        if(p.getInventory().getChestplate() != null && p.getInventory().getChestplate().getType() != Material.AIR) {
	            ItemStack Chestplate = p.getInventory().getChestplate();
	            subtractCustomDurability(p, Chestplate, 1, "armor");
	            log.info("CHEST: " + getCustomDurability(Chestplate, "armor"));
	        }
	        if(p.getInventory().getHelmet() != null && p.getInventory().getHelmet().getType() != Material.AIR) {
	            ItemStack Helmet = p.getInventory().getHelmet();
	            subtractCustomDurability(p, Helmet, 1, "armor");
	            log.info("HELMET: " + getCustomDurability(Helmet, "armor"));
	        }
	    }

	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = false)
	public void onPlayerInteractEvent(PlayerInteractEvent e) {
		
		Player p = e.getPlayer();
		ItemStack i = p.getItemInHand(); // e.getItem();
		
		if(isArmorScrap(i)) {
			p.sendMessage(ChatColor.YELLOW + "To repair an item with an " + ChatColor.BOLD + "ARMOR SCRAP" + ChatColor.YELLOW + " you need to drag the armor scrap onto the item you want to repair in your inventory.");
			return;
		}
		
		if(e.getAction() == Action.RIGHT_CLICK_BLOCK && e.hasBlock() && e.getClickedBlock().getType() == Material.ANVIL) {
			e.setCancelled(true);
			
			if(!p.getWorld().getName().equalsIgnoreCase(Bukkit.getWorlds().get(0).getName()) && !(InstanceMechanics.isInstance(p.getWorld().getName()))) {
				String w_name = p.getWorld().getName();
				if(!Bukkit.getOfflinePlayer(w_name).isOp()) {
					e.setCancelled(true);
					p.sendMessage(ChatColor.RED + "You " + ChatColor.UNDERLINE + "cannot" + ChatColor.RED + " access anvils in player owned realms.");
					log.info("[RepairMechanics] Player " + p.getName() + " accessed anvil in " + w_name + ".");
					return;
				}
			}
			
			if(repair_map.containsKey(p)) {
				p.sendMessage(ChatColor.RED + "You have a pending repair request. Type 'N' to cancel.");
				return;
			}
			
			if(!(e.hasItem())) {
				p.sendMessage(ChatColor.YELLOW + "Equip the item to repair and " + ChatColor.UNDERLINE + "RIGHT CLICK" + ChatColor.YELLOW + " the ANVIL.");
				p.sendMessage(ChatColor.GRAY + "Or, if you have an item scrap, drag it ontop of the item in your inventory.");
				return;
			}
			
			int repair_cost = getRepairCost(i);
			if(repair_cost <= 0) {
				p.sendMessage(ChatColor.RED + "You " + ChatColor.UNDERLINE + "cannot" + ChatColor.RED + " repair this item.");
				return; // Not a custom item.
			}
			
			if(i.getDurability() <= 0) {
				p.sendMessage(ChatColor.YELLOW + "This item is " + ChatColor.UNDERLINE + "NOT" + ChatColor.YELLOW + " damaged.");
				return;
			}
			
			if(!(RealmMechanics.doTheyHaveEnoughMoney(p, repair_cost))) {
				p.sendMessage(ChatColor.RED + "You do not have enough " + ChatColor.BOLD + "GEM(s)" + ChatColor.RED + " to repair this item.");
				p.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "COST: " + ChatColor.RED + repair_cost + ChatColor.BOLD + "G");
				repair_map.remove(p);
				return;
			}
			
			Location loc = e.getClickedBlock().getLocation();
			
			loc.setX(loc.getX() + 0.5);
			loc.setY(loc.getY() + 1.125);
			loc.setZ(loc.getZ() + 0.5);
			Item im = loc.getWorld().dropItem(loc, i);
			im.setPickupDelay(1000000000);
			
			// Force the item to move up, to prevent it from glitching out of the block on the wrong side, to get it nicely on the block
			im.setVelocity(new Vector(0, 0.1, 0));
			item_repair_map.put(p, im);
			
			String i_name = ItemMechanics.getItemName(i);
			p.setItemInHand(new ItemStack(Material.AIR));
			
			p.sendMessage(ChatColor.YELLOW + "It will cost " + ChatColor.GREEN + "" + ChatColor.BOLD + repair_cost + "G" + ChatColor.YELLOW + " to repair '" + i_name + ChatColor.YELLOW + "'");
			p.sendMessage(ChatColor.GRAY + "Type " + ChatColor.GREEN + "" + ChatColor.BOLD + "Y" + ChatColor.GRAY + " to confirm this repair. Or type " + ChatColor.RED + ChatColor.BOLD + "N" + ChatColor.GRAY + " to cancel.");
			repair_map.put(p, i);
			anvil_map.put(p, e.getClickedBlock().getLocation());
			
		}
	}
}
