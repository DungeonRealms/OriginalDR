package me.vilsol.itemgenerator.engine;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import me.vilsol.itemgenerator.ItemGenerator;
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
		ItemGenerator.modifiers.put(this.getClass(), this);
	}
	
	public boolean canApply(ItemType type){
		if(possibleApplicants == null) return false;
		return possibleApplicants.contains(type);
	}
	
	protected void addCondition(ItemTier tier, ItemRarity rarity, ModifierRange range){
		conditions.add(new ModifierCondition(tier, rarity, range, -1));
	}
	
	protected void addCondition(ModifierCondition condition){
		conditions.add(condition);
	}
	
	public ItemMeta tryModifier(ItemMeta meta, ItemTier tier, ItemRarity rarity, ItemType type, int mobTier, boolean override){
		Random r = new Random();
		for(ModifierCondition condition : conditions){
			ModifierCondition mc = condition;
			System.out.println(1);
			if(mc.doesConclude(tier, rarity, meta)){
				while(mc != null){
					if(mc.doesConclude(tier, rarity, meta)){
						int belowChance = (mc.getChance() < 0) ? chance : mc.getChance();
						String prefix = this.prefix;
						String suffix = this.suffix;
						
						if(mc.getReplacement() != null && mc.getReplacement().size() > 0){
							ItemModifier replacement = ItemGenerator.modifiers.get(mc.getReplacement().get(r.nextInt(mc.getReplacement().size())));
							if(replacement != null){
								prefix = replacement.getPrefix(meta);
								suffix = replacement.getSuffix(meta);
							}
						}
	
						if(r.nextInt(100) < belowChance  || override){
							System.out.println("Succeeded: " + prefix + " under " + belowChance);
							String random = mc.getRange().generateRandom();
							random = ((prefix != null) ? prefix : "") + random + ((suffix != null) ? suffix : "");
							List<String> lore = meta.getLore();
							lore.add(random);
							meta.setLore(lore);
						}
					}
					mc = mc.getBonus();
				}
				break;
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

	public String getPrefix(ItemMeta meta) {
		return prefix;
	}

	public String getSuffix(ItemMeta meta) {
		return suffix;
	}
	
}
