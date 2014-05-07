package me.vilsol.betanpc.enums;


public enum ItemType {
	
	HELMET, CHESTPLATE, LEGGINGS, BOOTS, SWORD, AXE, STAFF, POLEARM, BOW;
	
	public minecade.dungeonrealms.enums.ItemType getDRType(){
		switch(this){
			case AXE:
				return minecade.dungeonrealms.enums.ItemType.AXE;
			case BOOTS:
				return minecade.dungeonrealms.enums.ItemType.BOOTS;
			case BOW:
				return minecade.dungeonrealms.enums.ItemType.BOW;
			case CHESTPLATE:
				return minecade.dungeonrealms.enums.ItemType.CHESTPLATE;
			case HELMET:
				return minecade.dungeonrealms.enums.ItemType.HELMET;
			case LEGGINGS:
				return minecade.dungeonrealms.enums.ItemType.LEGGINGS;
			case POLEARM:
				return minecade.dungeonrealms.enums.ItemType.POLEARM;
			case STAFF:
				return minecade.dungeonrealms.enums.ItemType.STAFF;
			case SWORD:
				return minecade.dungeonrealms.enums.ItemType.SWORD;
		}
		
		return null;
	}
	
}
