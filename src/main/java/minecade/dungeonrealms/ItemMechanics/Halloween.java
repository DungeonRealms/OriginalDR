package minecade.dungeonrealms.ItemMechanics;

import java.util.Random;

import minecade.dungeonrealms.Main;
import minecade.dungeonrealms.EnchantMechanics.EnchantMechanics;
import minecade.dungeonrealms.MerchantMechanics.MerchantMechanics;
import minecade.dungeonrealms.PowerupMechanics.PowerupMechanics;
import minecade.dungeonrealms.RealmMechanics.RealmMechanics;
import minecade.dungeonrealms.RepairMechanics.RepairMechanics;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class Halloween implements Listener {
	
	static ItemMechanics plugin = null;
	public static ItemStack halloween_mask = null;
	public static ItemStack halloween_candy = null;
	
	public Halloween(ItemMechanics instance) {
		plugin = instance;
		halloween_mask = ItemGenerators.customGenerator("halloween_mask");
		halloween_candy = ItemGenerators.customGenerator("halloween_candy");
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPumpkinMaskRemove(InventoryClickEvent e) {
		if(e.isRightClick() && e.getCurrentItem() != null && (e.getCursor() == null || e.getCursor().getType() == Material.AIR)) {
			Player pl = (Player) e.getWhoClicked();
			ItemStack armor = e.getCurrentItem();
			
			double dur_percent = RepairMechanics.getPercentForDurabilityValue(armor, "armor");
//			ItemMechanics.log.info("dur_percent=" + dur_percent); right click is used for stat allocation.  too much spam
			
			if(armor.getType() == Material.JACK_O_LANTERN && ItemMechanics.isArmor(armor)) {
				// Remove the mask.
				int armor_tier = ItemMechanics.getItemTier(armor);
				if(armor.getType() == Material.JACK_O_LANTERN) {
					if(armor_tier == 1) {
						armor.setType(Material.LEATHER_HELMET);
					}
					if(armor_tier == 2) {
						armor.setType(Material.CHAINMAIL_HELMET);
					}
					if(armor_tier == 3) {
						armor.setType(Material.IRON_HELMET);
					}
					if(armor_tier == 4) {
						armor.setType(Material.DIAMOND_HELMET);
					}
					if(armor_tier == 5) {
						armor.setType(Material.GOLD_HELMET);
					}
				}
				
				armor.setDurability((short) 0);
				RepairMechanics.setCustomDurability(armor, RepairMechanics.blocks_per_armor, "armor", true);
				RepairMechanics.subtractCustomDurability(pl, armor, (1500.0D - (1500.0D * (dur_percent / 100.0D))), "armor", true);
				
				e.setCurrentItem(armor);
				e.setCursor(halloween_mask);
				pl.updateInventory();
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPumpkinMaskEquip(InventoryClickEvent e) {
		Player pl = (Player) e.getWhoClicked();
		if(!e.isShiftClick() && (e.isLeftClick())) {
			if(e.getCursor() != null && isHalloweenMask(e.getCursor())) {
				if(e.getCurrentItem() != null && e.getCurrentItem().getType() != Material.AIR) {
					if(ItemMechanics.isArmor(e.getCurrentItem()) && e.getCurrentItem().getType().name().toLowerCase().contains("helmet")) {
						if(!(RealmMechanics.isItemTradeable(e.getCurrentItem()))) {
							pl.sendMessage(ChatColor.RED + "You cannot mask this item.");
							return; // Don't allow pumpkin on dyed armor for ex.
						}
						if(!(e.getInventory().getName().equalsIgnoreCase("container.crafting"))) {
							pl.sendMessage(ChatColor.RED + "You must be in your own inventory to equip this item.");
							return;
						}
						
						ItemStack n_armor = e.getCurrentItem();
						double dur_percent = RepairMechanics.getPercentForDurabilityValue(n_armor, "armor");
						
						n_armor.setType(Material.JACK_O_LANTERN);
						
						// Apply pumpkin to helmet.
						e.setCancelled(true);
						ItemStack mask = e.getCursor();
						if(mask.getAmount() > 1) {
							int new_amount = mask.getAmount() - 1;
							mask.setAmount(new_amount);
							e.setCursor(mask);
						} else if(mask.getAmount() == 1) {
							e.setCursor(new ItemStack(Material.AIR));
						}
						
						n_armor.setDurability((short) 1500);
						RepairMechanics.setCustomDurability(n_armor, RepairMechanics.blocks_per_armor, "armor", true);
						RepairMechanics.subtractCustomDurability(pl, n_armor, (1500 - (1500 * (dur_percent / 100))), "armor", true);
						e.setCurrentItem(n_armor);
						
						pl.playSound(pl.getLocation(), Sound.GHAST_MOAN, 1F, 1F);
						pl.updateInventory();
						pl.sendMessage(ChatColor.YELLOW + "To remove your mask, " + ChatColor.UNDERLINE + "RIGHT CLICK" + ChatColor.YELLOW + " the masked item.");
						pl.sendMessage(ChatColor.GRAY.toString() + ChatColor.UNDERLINE + "Note:" + ChatColor.GRAY + " Durability is still being taken from this item, even thought the bar doesn't display while masked.");
						return;
					}
				}
			} else if(e.getRawSlot() == 5 && ItemMechanics.isArmor(e.getCursor()) && e.getCursor().getType() == Material.JACK_O_LANTERN) {
				/*pl.getInventory().setHelmet(e.getCursor());
				e.setCursor(new ItemStack(Material.AIR));
				pl.updateInventory();*/
				pl.sendMessage(ChatColor.RED + "To attach your mask, equip a helmet, then apply the mask to the equipped helmet.");
				return;
			}
		}
		
		// If code reaches this point, this is not a halloween mask, don't let them equip.
		if(e.getCursor() != null && !(isHalloweenMask(e.getCursor())) && !(ItemMechanics.isArmor(e.getCursor())) && (e.getCursor().getType() == Material.PUMPKIN || e.getCursor().getType() == Material.JACK_O_LANTERN)) {
			e.setCancelled(true);
			e.setCursor(new ItemStack(Material.AIR));
			pl.updateInventory();
		}
		
		if(e.getCurrentItem() != null && !(isHalloweenMask(e.getCurrentItem())) && !(ItemMechanics.isArmor(e.getCurrentItem())) && (e.getCurrentItem().getType() == Material.PUMPKIN || e.getCurrentItem().getType() == Material.JACK_O_LANTERN)) {
			e.setCancelled(true);
			e.setCurrentItem(new ItemStack(Material.AIR));
			pl.updateInventory();
		}
	}
	
	//@EventHandler
	public void onTrickOrTreat(PlayerInteractEntityEvent e) {
		if(e.getRightClicked() instanceof Player) {
			Player trader = (Player) e.getRightClicked();
			if(trader.hasMetadata("NPC") && ChatColor.stripColor(trader.getName()).equalsIgnoreCase("Trick Or Treat")) {
				Player pl = e.getPlayer();
				
				if(isHalloweenCandy(pl.getItemInHand())) {
					e.setCancelled(true);
					
					if(pl.getInventory().firstEmpty() == -1) {
						pl.sendMessage(ChatColor.RED + "Please make some space in your inventory before speaking to the Trick or Treater.");
						return;
					}
					
					ItemStack candy = pl.getItemInHand();
					if(candy.getAmount() > 1) {
						int new_amount = candy.getAmount() - 1;
						candy.setAmount(new_amount);
						pl.setItemInHand(candy);
					} else if(candy.getAmount() == 1) {
						pl.setItemInHand(new ItemStack(Material.AIR));
					}
					
					int trick_or_treat = new Random().nextInt(2);
					if(trick_or_treat == 0) {
						// Trick
						int trick_type = new Random().nextInt(5);
						if(trick_type == 0) {
							pl.setHealth(1);
						}
						if(trick_type == 1) {
							pl.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20 * 20, 1));
						}
						if(trick_type == 2) {
							pl.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 20 * 20, 2));
						}
						if(trick_type == 3) {
							pl.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20 * 20, 3));
						}
						if(trick_type == 4) {
							pl.setVelocity(new Vector(0, 8, 0));
						}
						
						pl.playSound(pl.getLocation(), Sound.CREEPER_HISS, 1F, 0.5F);
						pl.sendMessage(ChatColor.GRAY + "Trick Or Treat: " + ChatColor.WHITE + "AHAHAHA! You got " + ChatColor.RED + "TRICKED!");
					} else if(trick_or_treat == 1) {
						// Treat
						int treat_type = new Random().nextInt(100);
						if(treat_type == 0) {
							// Free orb
							if(pl.getInventory().firstEmpty() != -1) {
								pl.getInventory().addItem(MerchantMechanics.orb_of_alteration);
							}
							Main.plugin.getServer().broadcastMessage(ChatColor.GOLD + "Congratulations to " + pl.getName() + " -- they just got a(n) " + ChatColor.LIGHT_PURPLE + "Orb of Alteration" + ChatColor.GOLD + " from the Trick or Treater!");
						}
						if(treat_type == 1) {
							// Free enchant scroll
							ItemStack prize = null;
							
							if(pl.getInventory().firstEmpty() != -1) {
								int scroll_tier = new Random().nextInt(5) + 1;
								int scroll_type = new Random().nextInt(3);
								if(scroll_tier == 1) if(scroll_type == 0) prize = EnchantMechanics.t1_wep_scroll;
								if(scroll_type == 1) prize = EnchantMechanics.t1_armor_scroll;
								if(scroll_type == 2) prize = EnchantMechanics.t1_white_scroll;
								
								if(scroll_tier == 2) if(scroll_type == 0) prize = EnchantMechanics.t2_wep_scroll;
								if(scroll_type == 1) prize = EnchantMechanics.t2_armor_scroll;
								if(scroll_type == 2) prize = EnchantMechanics.t2_white_scroll;
								
								if(scroll_tier == 3) if(scroll_type == 0) prize = EnchantMechanics.t3_wep_scroll;
								if(scroll_type == 1) prize = EnchantMechanics.t3_armor_scroll;
								if(scroll_type == 2) prize = EnchantMechanics.t3_white_scroll;
								
								if(scroll_tier == 4) if(scroll_type == 0) prize = EnchantMechanics.t4_wep_scroll;
								if(scroll_type == 1) prize = EnchantMechanics.t4_armor_scroll;
								if(scroll_type == 2) prize = EnchantMechanics.t4_white_scroll;
								
								if(scroll_tier == 5) if(scroll_type == 0) prize = EnchantMechanics.t5_wep_scroll;
								if(scroll_type == 1) prize = EnchantMechanics.t5_armor_scroll;
								if(scroll_type == 2) prize = EnchantMechanics.t5_white_scroll;
								
								pl.getInventory().addItem(prize);
								Main.plugin.getServer().broadcastMessage(ChatColor.GOLD + "Congratulations to " + pl.getName() + " -- they just got a(n) " + prize.getItemMeta().getDisplayName() + ChatColor.GOLD + " from the Trick or Treater!");
							}
						}
						if(treat_type > 1) {
							// Random buff for 15 minutes.
							PowerupMechanics.handleBeaconEffect(pl, (15 * 60));
						}
						
						pl.playSound(pl.getLocation(), Sound.ORB_PICKUP, 1F, 1F);
						pl.sendMessage(ChatColor.GRAY + "Trick Or Treat: " + ChatColor.WHITE + "Here, have a " + ChatColor.GREEN + "TREAT!");
						pl.updateInventory();
					}
				} else {
					pl.sendMessage(ChatColor.GRAY + "Trick Or Treat: " + ChatColor.WHITE.toString() + "Come back when you have some candy for me.");
					pl.sendMessage(ChatColor.GRAY.toString() + ChatColor.ITALIC + "Monsters all over Andalucia are droping small bags of candy!");
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onCandyEat(PlayerInteractEvent e) {
		Player pl = e.getPlayer();
		if(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if(e.hasItem() && isHalloweenCandy(e.getItem())) {
				ItemStack candy = e.getItem();
				if(candy.getAmount() > 1) {
					int new_amount = candy.getAmount() - 1;
					candy.setAmount(new_amount);
					pl.setItemInHand(candy);
				} else if(candy.getAmount() == 1) {
					pl.setItemInHand(new ItemStack(Material.AIR));
				}
				
				if(pl.getFoodLevel() < 20) {
					pl.setFoodLevel(pl.getFoodLevel() + 1);
					pl.setSaturation(pl.getSaturation() + 1F);
				}
				
				pl.playSound(pl.getLocation(), Sound.EAT, 1F, 2F);
				pl.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, (4 * 20), 1));
				pl.updateInventory();
			}
		}
	}
	
	// Used when removing from head, the only data that will match it item type? Assuming they can't equip non-unique pumpkins to start.
	public static boolean isHalloweenMask(ItemStack is) {
		if(is != null && is.hasItemMeta() && is.getType() == Material.JACK_O_LANTERN) {
			// Make sure they're not fake / old ones.
			if(isHolidayItem(is) && is.getItemMeta().hasDisplayName() && is.getItemMeta().getDisplayName().contains(ChatColor.LIGHT_PURPLE.toString())) { return true; }
		}
		return false;
	}
	
	public boolean isHalloweenCandy(ItemStack is) {
		if(is != null && is.hasItemMeta() && is.getType() == Material.SUGAR) {
			// Make sure they're not fake / old ones.
			if(is.getItemMeta().hasDisplayName() && is.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.GREEN.toString() + "Halloween Candy")) { return true; }
		}
		return false;
	}
	
	public static boolean isHolidayItem(ItemStack is) {
		if(is != null && is.hasItemMeta() && is.getItemMeta().hasLore()) {
			for(String s : is.getItemMeta().getLore()) {
				if(s.equalsIgnoreCase(ChatColor.GREEN.toString() + "Holiday Item")) { return true; }
			}
		}
		return false;
	}
}
