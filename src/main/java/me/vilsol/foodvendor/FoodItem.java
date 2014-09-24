package me.vilsol.foodvendor;

import me.vilsol.betanpc.utils.Utils;
import me.vilsol.menuengine.engine.BonusItem;
import me.vilsol.menuengine.engine.ChatCallback;
import me.vilsol.menuengine.engine.MenuItem;
import minecade.dungeonrealms.ItemMechanics.ItemMechanics;
import minecade.dungeonrealms.LevelMechanics.LevelMechanics;
import minecade.dungeonrealms.RealmMechanics.RealmMechanics;
import minecade.dungeonrealms.ShopMechanics.ShopMechanics;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;

public class FoodItem implements MenuItem, ChatCallback, BonusItem<Integer> {
	
	public static final ItemStack T1_COMMON_FOOD = ItemMechanics.signCustomItem(Material.POTATO_ITEM, (short) 1, ChatColor.WHITE + "Plowed Potato", ChatColor.RED + "+9 HP/s for " + ChatColor.BOLD + "30 SECONDS" + "," + ChatColor.RED + "Moving will " + ChatColor.BOLD + "CANCEL" + ChatColor.RED + " the effect" + "," + ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + "The staple crop of Andalucia. Definitely not rotten" + "," + ChatColor.GRAY.toString() + ChatColor.ITALIC + "Common");
	public static final ItemStack T1_UNIQUE_FOOD = ItemMechanics.signCustomItem(Material.BAKED_POTATO, (short) 1, ChatColor.WHITE + "Loaded Potato Skin", ChatColor.RED + "+20 HP/s for " + ChatColor.BOLD + "30 SECONDS" + "," + ChatColor.RED + "Moving will " + ChatColor.BOLD + "CANCEL" + ChatColor.RED + " the effect" + "," + ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + "Extremely tasty." + "," + ChatColor.YELLOW.toString() + ChatColor.ITALIC + "Unique");
	public static final ItemStack T2_COMMON_FOOD = ItemMechanics.signCustomItem(Material.RAW_CHICKEN, (short) 1, ChatColor.GREEN + "Uncooked Chicken", ChatColor.RED + "+42 HP/s for " + ChatColor.BOLD + "30 SECONDS" + "," + ChatColor.RED + "Moving will " + ChatColor.BOLD + "CANCEL" + ChatColor.RED + " the effect" + "," + ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + "This may or may not be safe to eat..." + "," + ChatColor.GRAY.toString() + ChatColor.ITALIC + "Common");
	public static final ItemStack T2_UNIQUE_FOOD = ItemMechanics.signCustomItem(Material.COOKED_CHICKEN, (short) 1, ChatColor.GREEN + "Roast Chicken", ChatColor.RED + "+58 HP/s for " + ChatColor.BOLD + "30 SECONDS" + "," + ChatColor.RED + "Moving will " + ChatColor.BOLD + "CANCEL" + ChatColor.RED + " the effect" + "," + ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + "Warm and toasty. Delicious too." + "," + ChatColor.YELLOW.toString() + ChatColor.ITALIC + "Unique");
	public static final ItemStack T3_COMMON_FOOD = ItemMechanics.signCustomItem(Material.PORK, (short) 1, ChatColor.AQUA + "Salted Pork", ChatColor.RED + "+90 HP/s for " + ChatColor.BOLD + "30 SECONDS" + "," + ChatColor.RED + "Moving will " + ChatColor.BOLD + "CANCEL" + ChatColor.RED + " the effect" + "," + ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + "Bringing in the bacon." + "," + ChatColor.GRAY.toString() + ChatColor.ITALIC + "Common");
	public static final ItemStack T3_UNIQUE_FOOD = ItemMechanics.signCustomItem(Material.GRILLED_PORK, (short) 1, ChatColor.AQUA + "Seasoned Pork", ChatColor.RED + "+164 HP/s for " + ChatColor.BOLD + "30 SECONDS" + "," + ChatColor.RED + "Moving will " + ChatColor.BOLD + "CANCEL" + ChatColor.RED + " the effect" + "," + ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + "Bacon. Except tastier (is that possible?)" + "," + ChatColor.YELLOW.toString() + ChatColor.ITALIC + "Unique");
	public static final ItemStack T4_COMMON_FOOD = ItemMechanics.signCustomItem(Material.RAW_BEEF, (short) 1, ChatColor.LIGHT_PURPLE + "Frozen Steak", ChatColor.RED + "+290 HP/s for " + ChatColor.BOLD + "30 SECONDS" + "," + ChatColor.RED + "Moving will " + ChatColor.BOLD + "CANCEL" + ChatColor.RED + " the effect" + "," + ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + "Stop complaining. Your dog would sure love to eat this, " + "," + ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + "so don't be picky!" + "," + ChatColor.GRAY.toString() + ChatColor.ITALIC + "Common");
	public static final ItemStack T4_UNIQUE_FOOD = ItemMechanics.signCustomItem(Material.COOKED_BEEF, (short) 1, ChatColor.LIGHT_PURPLE + "Rare Sizzling Steak", ChatColor.RED + "+430 HP/s for " + ChatColor.BOLD + "30 SECONDS" + "," + ChatColor.RED + "Moving will " + ChatColor.BOLD + "CANCEL" + ChatColor.RED + " the effect" + "," + ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + "Real men get their steaks rare." + "," + ChatColor.YELLOW.toString() + ChatColor.ITALIC + "Unique");
	public static final ItemStack T5_COMMON_FOOD = ItemMechanics.signCustomItem(Material.GOLDEN_APPLE, (short) 0, ChatColor.YELLOW + "King's Apple", ChatColor.RED + "+750 HP/s for " + ChatColor.BOLD + "30 SECONDS" + "," + ChatColor.RED + "Moving will " + ChatColor.BOLD + "CANCEL" + ChatColor.RED + " the effect" + "," + ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + "A meal fit for a king." + "," + ChatColor.GRAY.toString() + ChatColor.ITALIC + "Common");
	public static final ItemStack T5_UNIQUE_FOOD = ItemMechanics.signCustomItem(Material.GOLDEN_APPLE, (short) 1, ChatColor.YELLOW + "Enchanted King's Apple", ChatColor.RED + "+1060 HP/s for " + ChatColor.BOLD + "30 SECONDS" + "," + ChatColor.RED + "Moving will " + ChatColor.BOLD + "CANCEL" + ChatColor.RED + " the effect" + "," + ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + "A powerful king's battle snack." + "," + ChatColor.YELLOW.toString() + ChatColor.ITALIC + "Unique");
	
	private int slot;
	private ItemStack selectedItem;
	
	@Override
	public void onChatMessage(AsyncPlayerChatEvent e) {
		Player p = e.getPlayer();
		String msg = e.getMessage();
		int cost = ShopMechanics.getPrice(selectedItem);
		
		if(!(Utils.isInteger(msg)) && !msg.equalsIgnoreCase("cancel") || (!(Utils.isInteger(msg)) && Integer.valueOf(msg) % 4 != 0)) {
			p.sendMessage(ChatColor.RED + "Please enter a number between 1 and 64 inclusive that is a multiple of 4.");
		} else if(msg.equalsIgnoreCase("cancel")) {
			p.sendMessage(ChatColor.RED + "Purchase - " + ChatColor.BOLD + "CANCELLED");
			ChatCallback.locked_players.remove(p);
		} else {
			if(Integer.valueOf(msg) >= 1 && Integer.valueOf(msg) <= 64) {
				int quantity = Integer.valueOf(msg);
				if(RealmMechanics.doTheyHaveEnoughMoney(p, quantity * cost)) {
					if(p.getInventory().firstEmpty() == -1) {
						p.sendMessage(ChatColor.RED + "Please clear some inventory space first.");
						ChatCallback.locked_players.remove(p);
						return;
					}
					RealmMechanics.subtractMoney(p, cost);
					
					ItemStack buyingItem = selectedItem.clone();
					buyingItem.setAmount(quantity);
					p.getInventory().setItem(p.getInventory().firstEmpty(), ShopMechanics.removePrice(buyingItem));
					
					p.sendMessage(ChatColor.GREEN + "Transaction successful.");
					p.playSound(p.getLocation(), Sound.ORB_PICKUP, 1F, 1F);
					p.updateInventory();
					ChatCallback.locked_players.remove(p);
				} else {
					p.sendMessage(ChatColor.RED + "You don't have enough GEM(s) for " + quantity + "x of this item.");
					p.sendMessage(ChatColor.RED + "COST: " + quantity * cost + "g");
					ChatCallback.locked_players.remove(p);
				}
			}
		}
	}
	
	@Override
	public void execute(Player p, ClickType arg1) {
		int price = ShopMechanics.getPrice(selectedItem);
		if(!LevelMechanics.canPlayerUseTier(p, ItemMechanics.getItemTier(selectedItem))) {
			p.sendMessage(ChatColor.RED + "This item requires " + ChatColor.UNDERLINE + "at least" + ChatColor.RED + " level " + LevelMechanics.getLevelToUse(ItemMechanics.getItemTier(selectedItem)) + " to use this item.");
			p.sendMessage(ChatColor.GRAY + "Do you really want to purchase it?  If not, type cancel below.");
		}
		p.sendMessage(ChatColor.GREEN + "Enter the " + ChatColor.BOLD + "QUANTITY" + ChatColor.GREEN + " in a multiple of 4 you'd like to purchase.");
		p.sendMessage(ChatColor.GRAY + "MAX: 64X (" + price * 64 + "g), OR " + price + "g/each.");
		ChatCallback.locked_players.put(p, this);
		p.closeInventory();
	}
	
	@Override
	public ItemStack getItem() {
		switch(slot) {
			default:
			case 0:
				selectedItem = ShopMechanics.setPrice(T1_COMMON_FOOD.clone(), 2);
				break;
			case 1:
				selectedItem = ShopMechanics.setPrice(T1_UNIQUE_FOOD.clone(), 4);
				break;
			case 2:
				selectedItem = ShopMechanics.setPrice(T2_COMMON_FOOD.clone(), 6);
				break;
			case 3:
				selectedItem = ShopMechanics.setPrice(T2_UNIQUE_FOOD.clone(), 9);
				break;
			case 4:
				selectedItem = ShopMechanics.setPrice(T3_COMMON_FOOD.clone(), 15);
				break;
			case 5:
				selectedItem = ShopMechanics.setPrice(T3_UNIQUE_FOOD.clone(), 21);
				break;
			case 6:
				selectedItem = ShopMechanics.setPrice(T4_COMMON_FOOD.clone(), 35);
				break;
			case 7:
				selectedItem = ShopMechanics.setPrice(T4_UNIQUE_FOOD.clone(), 60);
				break;
			case 8:
				selectedItem = ShopMechanics.setPrice(T5_COMMON_FOOD.clone(), 95);
				break;
			case 9:
				selectedItem = ShopMechanics.setPrice(T5_UNIQUE_FOOD.clone(), 100);
				break;
		}
		
		return selectedItem;
	}
	
	@Override
	public void registerItem() {
		MenuItem.items.put(this.getClass(), this);
	}
	
	@Override
	public void setBonusData(Integer arg0) {
		this.slot = arg0;
	}
	
}
