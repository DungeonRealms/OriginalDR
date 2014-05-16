package me.vilsol.itemgenerator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import me.vilsol.itemgenerator.engine.ItemModifier;
import me.vilsol.itemgenerator.modifiers.AllTypeModifier;
import minecade.dungeonrealms.enums.ItemRarity;
import minecade.dungeonrealms.enums.ItemTier;
import minecade.dungeonrealms.enums.ItemType;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemGenerator {
	
	public static HashMap<Class<? extends ItemModifier>, ItemModifier> modifiers = new HashMap<Class<? extends ItemModifier>, ItemModifier>();
	
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
		
		for(ItemModifier modifier : modifiers.values()){
			if(modifier.canApply(type)){
				meta = modifier.tryModifier(meta, tier, rarity, type, mobTier);
			}
		}
		
		List<String> lore = meta.getLore();
		if(rarity == ItemRarity.COMMON) lore.add(ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + "Common");
		if(rarity == ItemRarity.UNCOMMON) lore.add(ChatColor.GREEN.toString() + ChatColor.ITALIC.toString() + "Uncommon");
		if(rarity == ItemRarity.RARE) lore.add(ChatColor.AQUA.toString() + ChatColor.ITALIC.toString() + "Rare");
		if(rarity == ItemRarity.UNIQUE) lore.add(ChatColor.YELLOW.toString() + ChatColor.ITALIC.toString() + "Unique");
		
		meta.setLore(lore);
		meta.setDisplayName(tier.getTierColor() + type.getTierName(tier));
		
		item.setItemMeta(meta);
		
		this.item = item;
		
		return this;
	}
	
	public ItemStack getItem(){
		return item;
	}
	
	public static void loadModifiers(){
		AllTypeModifier all = new AllTypeModifier();
		all.new StrDexVitInt();
	}
	
}
