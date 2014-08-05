package minecade.dungeonrealms.enums;

import minecade.dungeonrealms.EnchantMechanics.EnchantMechanics;
import minecade.dungeonrealms.ItemMechanics.ItemMechanics;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

public enum ItemRarity {
	
	COMMON, UNCOMMON, RARE, UNIQUE;
	
	public static ItemRarity getRarityFromItem(ItemStack is) {
	    if (EnchantMechanics.hasProtection(is) && ItemMechanics.isLegacy(is)) return valueOf(ChatColor.stripColor(is.getItemMeta().getLore().get(is.getItemMeta().getLore().size() - 3)).toUpperCase());
	    else if (EnchantMechanics.hasProtection(is) || ItemMechanics.isLegacy(is)) return valueOf(ChatColor.stripColor(is.getItemMeta().getLore().get(is.getItemMeta().getLore().size() - 2)).toUpperCase());
	    return valueOf(ChatColor.stripColor(is.getItemMeta().getLore().get(is.getItemMeta().getLore().size() - 1)).toUpperCase());
	}
	
}
