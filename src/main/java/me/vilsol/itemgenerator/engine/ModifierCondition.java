package me.vilsol.itemgenerator.engine;

import minecade.dungeonrealms.enums.ItemRarity;
import minecade.dungeonrealms.enums.ItemTier;

public class ModifierCondition {
	
	private ItemTier tier;
	private ItemRarity rarity;
	private ModifierRange range;
	
	public ModifierCondition(ItemTier tier, ItemRarity rarity, ModifierRange range){
		this.tier = tier;
		this.rarity = rarity;
		this.range = range;
	}
	
	public boolean doesConclude(ItemTier tier, ItemRarity rarity){
		if(this.tier != null && this.tier != tier) return false;
		if(this.rarity != null && this.rarity != rarity) return false;
		return true;
	}
	
	public ModifierRange getRange(){
		return range;
	}
	
}
