package minecade.dungeonrealms.enums;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

public enum ItemRarity {
	
	COMMON, UNCOMMON, RARE, UNIQUE;
	
	public static ItemRarity getRarityFromItem(ItemStack is) {
	    return valueOf(ChatColor.stripColor(is.getItemMeta().getLore().get(is.getItemMeta().getLore().size() - 1)).toUpperCase());
	}
	
}
