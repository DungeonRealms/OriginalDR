package minecade.dungeonrealms.enums;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public enum ItemTier {
	
	T1, T2, T3, T4, T5;
	
	public ChatColor getTierColor(){
		switch(this){
			case T1:
				return ChatColor.WHITE;
			case T2:
				return ChatColor.GREEN;
			case T3:
				return ChatColor.AQUA;
			case T4:
				return ChatColor.LIGHT_PURPLE;
			case T5:
				return ChatColor.YELLOW;
		}
		
		return null;
	}
	
	public static ItemTier getTierFromInt(int tier) {
        switch (tier) {
        case 1:
            return T1;
        case 2:
            return T2;
        case 3:
            return T3;
        case 4:
            return T4;
        case 5:
            return T5;
        }
        return null;
    }
	
	/**
	 * Gets the ItemTier of a material.  DOES NOT CHECK IF ITEM IS ARMOR OR WEAPON!  Does not support 
	 * bows because they are all wood.
	 * @param m
	 * @return
	 */
	public static ItemTier getTierFromMaterial(Material m) { // TODO: make this check if item is armor or weapon, am too lazy atm
	    String name = m.toString().toLowerCase();
        if (name.startsWith("leather") || name.startsWith("wood")) {
            return T1;
        }
        else if (name.startsWith("chainmail") || name.startsWith("stone")) {
            return T2;
        }
        else if (name.startsWith("iron")) {
            return T3;
        }
        else if (name.startsWith("diamond")) {
            return T4;
        }
        else if (name.startsWith("gold")) {
            return T5;
        }
        else if (name.equalsIgnoreCase("Bow")) {
            
        }
        return null;
    }
	
	/**
	 * Gets the ItemTier of a Bukkit ItemStack by getting the color of its name.  
	 * Inaccurate for items that have the same color in their name as tiers!
	 * @param is
	 * @return
	 */
	public static ItemTier getTierFromItem(ItemStack is) {
	    try {
            String name = is.getItemMeta().getDisplayName();
            if (name.contains(ChatColor.GREEN.toString())) {
                return T2;
            }
            if (name.contains(ChatColor.AQUA.toString())) {
                return T3;
            }
            if (name.contains(ChatColor.LIGHT_PURPLE.toString())) {
                return T4;
            }
            if (name.contains(ChatColor.YELLOW.toString())) {
                return T5;
            }
            if (name.contains(ChatColor.WHITE.toString())) {
                return T1;
            }

            return T1;
        } catch (NullPointerException npe) {
            // npe.printStackTrace();
            return null;
        }
	}
	
}
