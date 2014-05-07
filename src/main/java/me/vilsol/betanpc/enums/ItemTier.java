package me.vilsol.betanpc.enums;

import org.bukkit.ChatColor;
import org.bukkit.Material;

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
	
	public minecade.dungeonrealms.enums.ItemTier getDRTier(){
		switch(this){
			case T1:
				return minecade.dungeonrealms.enums.ItemTier.T1;
			case T2:
				return minecade.dungeonrealms.enums.ItemTier.T2;
			case T3:
				return minecade.dungeonrealms.enums.ItemTier.T3;
			case T4:
				return minecade.dungeonrealms.enums.ItemTier.T4;
			case T5:
				return minecade.dungeonrealms.enums.ItemTier.T5;
		}
		
		return null;
	}
	
	@SuppressWarnings("incomplete-switch")
	public Material getMaterialFromType(ItemType t){
		if(t == null) return null;
		if(t == ItemType.BOW) return Material.BOW;
		
		switch(this){
			case T1:
				switch(t){
					case AXE:
						return Material.WOOD_AXE;
					case BOOTS:
						return Material.LEATHER_BOOTS;
					case CHESTPLATE:
						return Material.LEATHER_CHESTPLATE;
					case HELMET:
						return Material.LEATHER_HELMET;
					case LEGGINGS:
						return Material.LEATHER_LEGGINGS;
					case POLEARM:
						return Material.WOOD_SPADE;
					case STAFF:
						return Material.WOOD_HOE;
					case SWORD:
						return Material.WOOD_SWORD;
				}
			case T2:
				switch(t){
					case AXE:
						return Material.STONE_AXE;
					case BOOTS:
						return Material.CHAINMAIL_BOOTS;
					case CHESTPLATE:
						return Material.CHAINMAIL_CHESTPLATE;
					case HELMET:
						return Material.CHAINMAIL_HELMET;
					case LEGGINGS:
						return Material.CHAINMAIL_LEGGINGS;
					case POLEARM:
						return Material.STONE_SPADE;
					case STAFF:
						return Material.STONE_HOE;
					case SWORD:
						return Material.STONE_SWORD;
				}
			case T3:
				switch(t){
					case AXE:
						return Material.IRON_AXE;
					case BOOTS:
						return Material.IRON_BOOTS;
					case CHESTPLATE:
						return Material.IRON_CHESTPLATE;
					case HELMET:
						return Material.IRON_HELMET;
					case LEGGINGS:
						return Material.IRON_LEGGINGS;
					case POLEARM:
						return Material.IRON_SPADE;
					case STAFF:
						return Material.IRON_HOE;
					case SWORD:
						return Material.IRON_SWORD;
				}
			case T4:
				switch(t){
					case AXE:
						return Material.DIAMOND_AXE;
					case BOOTS:
						return Material.DIAMOND_BOOTS;
					case CHESTPLATE:
						return Material.DIAMOND_CHESTPLATE;
					case HELMET:
						return Material.DIAMOND_HELMET;
					case LEGGINGS:
						return Material.DIAMOND_LEGGINGS;
					case POLEARM:
						return Material.DIAMOND_SPADE;
					case STAFF:
						return Material.DIAMOND_HOE;
					case SWORD:
						return Material.DIAMOND_SWORD;
				}
			case T5:
				switch(t){
					case AXE:
						return Material.GOLD_AXE;
					case BOOTS:
						return Material.GOLD_BOOTS;
					case CHESTPLATE:
						return Material.GOLD_CHESTPLATE;
					case HELMET:
						return Material.GOLD_HELMET;
					case LEGGINGS:
						return Material.GOLD_LEGGINGS;
					case POLEARM:
						return Material.GOLD_SPADE;
					case STAFF:
						return Material.GOLD_HOE;
					case SWORD:
						return Material.GOLD_SWORD;
				}
		}
		
		return null;
	}
	
}
