package me.vilsol.itemgenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import me.vilsol.itemgenerator.engine.ItemModifier;
import minecade.dungeonrealms.enums.ItemRarity;
import minecade.dungeonrealms.enums.ItemTier;
import minecade.dungeonrealms.enums.ItemType;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemGenerator {
	
	private static List<ItemModifier> modifiers = new ArrayList<ItemModifier>();
	
	private ItemType type;
	private ItemTier tier;
	private ItemRarity rarity;
	
	private int mobTier = -1;
	
	private ItemStack item;
	
	public ItemGenerator setType(ItemType type){
		this.type = type;
		return this;
	}
	
	public ItemGenerator setTier(ItemTier tier){
		this.tier = tier;
		return this;
	}
	
	public ItemGenerator setRarity(ItemRarity rarity){
		this.rarity = rarity;
		return this;
	}
	
	public ItemGenerator setMobTier(int mobTier){
		this.mobTier = mobTier;
		return this;
	}
	
	public ItemGenerator generateItem(){
		ItemTier tier = this.tier;
		ItemType type = this.type;
		ItemRarity rarity = this.rarity;
		
		Random r = new Random();

		if(tier == null) tier = ItemTier.values()[r.nextInt(ItemTier.values().length - 1)];
		if(type == null) type = ItemType.values()[r.nextInt(ItemType.values().length - 1)];
		if(rarity == null) rarity = ItemRarity.values()[r.nextInt(ItemRarity.values().length - 1)];
		
		ItemStack item = new ItemStack(type.getTier(tier));
		ItemMeta meta = item.getItemMeta();
		meta.setLore(new ArrayList<String>());
		
		for(ItemModifier modifier : modifiers){
			if(modifier.canApply(type)){
				modifier.tryModifier(meta, tier, rarity, type, mobTier);
			}
		}
		
		this.item = item;
		
		return this;
	}
	
	public ItemStack getItem(){
		return item;
	}
	
	public static void loadModifiers(){
		
	}
	
}
