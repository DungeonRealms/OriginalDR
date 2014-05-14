package me.vilsol.itemgenerator.engine;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import minecade.dungeonrealms.enums.ItemRarity;
import minecade.dungeonrealms.enums.ItemTier;
import minecade.dungeonrealms.enums.ItemType;

import org.bukkit.inventory.meta.ItemMeta;

public abstract class ItemModifier {
	
	private List<ModifierCondition> conditions = new ArrayList<ModifierCondition>(); 
	private List<ItemType> possibleApplicants;
	private int chance = 0;
	private String prefix;
	private String suffix;
	
	public ItemModifier(List<ItemType> possibleApplicants, int chance, String prefix, String suffix){
		this.possibleApplicants = possibleApplicants;
		this.chance = chance;
		this.prefix = prefix;
		this.suffix = suffix;
	}
	
	public boolean canApply(ItemType type){
		if(possibleApplicants == null) return false;
		return possibleApplicants.contains(type);
	}
	
	protected void addCondition(ItemTier tier, ItemRarity rarity, ModifierRange range){
		conditions.add(new ModifierCondition(tier, rarity, range));
	}
	
	protected void addCondition(ModifierCondition condition){
		conditions.add(condition);
	}
	
	public ItemMeta tryModifier(ItemMeta meta, ItemTier tier, ItemRarity rarity, ItemType type, int mobTier, boolean override){
		Random r = new Random();
		if((r.nextInt(100) < chance) || override){
			for(ModifierCondition condition : conditions){
				if(condition.doesConclude(tier, rarity)){
					String random = condition.getRange().generateRandom();
					random = ((prefix != null) ? prefix : "") + random + ((suffix != null) ? suffix : "");
					meta.getLore().add(random);
					break;
				}
			}
		}
		return meta;
	}
	
	public ItemMeta tryModifier(ItemMeta meta, ItemTier tier, ItemRarity rarity, ItemType type, int mobTier){
		return tryModifier(meta, tier, rarity, type, mobTier, false);
	}
	
	public ItemMeta tryModifier(ItemMeta meta, ItemTier tier, ItemRarity rarity, ItemType type){
		return tryModifier(meta, tier, rarity, type, -1, false);
	}
	
}
