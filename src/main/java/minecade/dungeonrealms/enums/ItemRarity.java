package minecade.dungeonrealms.enums;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

public enum ItemRarity {
	
	COMMON, UNCOMMON, RARE, UNIQUE;
	
	public static ItemRarity getRarityFromItem(ItemStack is) {
        if (!is.hasItemMeta() || !is.getItemMeta().hasLore()) return null;
        for (String line : is.getItemMeta().getLore()) {
            for (ItemRarity rarity : values()) {
                if (line.toLowerCase().contains(rarity.toString().toLowerCase())) return valueOf(ChatColor.stripColor(line).toUpperCase());
            }
        }
	    return null;
	}
	
}
