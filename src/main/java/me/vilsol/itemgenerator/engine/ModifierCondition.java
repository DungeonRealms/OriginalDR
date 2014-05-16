package me.vilsol.itemgenerator.engine;

import java.util.ArrayList;
import java.util.List;

import me.vilsol.itemgenerator.ItemGenerator;
import minecade.dungeonrealms.enums.ItemRarity;
import minecade.dungeonrealms.enums.ItemTier;

import org.bukkit.inventory.meta.ItemMeta;

public class ModifierCondition {
	
	private ItemTier tier;
	private ItemRarity rarity;
	private ModifierRange range;
	private int chance;
	private List<Class<? extends ItemModifier>> cantContain;
	private ModifierCondition bonus;
	private List<Class<? extends ItemModifier>> replacement;
	
	public ModifierCondition(ItemTier tier, ItemRarity rarity, ModifierRange range, int chance){
		this.tier = tier;
		this.rarity = rarity;
		this.range = range;
		this.chance = chance;
	}
	
	public ModifierCondition setBonus(ModifierCondition bonus){
		this.bonus = bonus;
		return this;
	}
	
	public ModifierCondition setReplacement(Class<? extends ItemModifier> replacement){
		this.replacement = new ArrayList<Class<? extends ItemModifier>>();
		this.replacement.add(replacement);
		return this;
	}
	
	public ModifierCondition setReplacement(List<Class<? extends ItemModifier>> replacement){
		this.replacement = replacement;
		return this;
	}
	
	public ModifierCondition setCantContain(Class<? extends ItemModifier> cantContain){
		this.cantContain = new ArrayList<Class<? extends ItemModifier>>();
		this.cantContain.add(cantContain);
		return this;
	}
	
	public ModifierCondition setCantContain(List<Class<? extends ItemModifier>> cantContain){
		this.cantContain = cantContain;
		return this;
	}
	
	public boolean doesConclude(ItemTier tier, ItemRarity rarity, ItemMeta meta){
		if(this.tier != null && this.tier != tier) return false;
		if(this.rarity != null && this.rarity != rarity) return false;
		if(this.cantContain != null && this.cantContain.size() > 0){
			if(meta.getLore() != null || meta.getLore().size() > 0){
				for(Class<? extends ItemModifier> c : cantContain){
					ItemModifier m = ItemGenerator.modifiers.get(c);
					for(String s : meta.getLore()){
						if(m.getPrefix(meta) != null && s.startsWith(m.getPrefix(meta))) return false;
						if(m.getSuffix(meta) != null && s.endsWith(m.getSuffix(meta))) return false;
					}
				}
			}
		}
		return true;
	}
	
	public ModifierRange getRange(){
		return range;
	}
	
	public int getChance(){
		return chance;
	}
	
	public ModifierCondition getBonus(){
		return bonus;
	}
	
	public List<Class<? extends ItemModifier>> getReplacement(){
		return replacement;
	}
	
}
