package me.vilsol.itemgenerator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import me.vilsol.itemgenerator.engine.ItemModifier;
import me.vilsol.itemgenerator.engine.ModifierCondition;
import me.vilsol.itemgenerator.modifiers.ArmorModifiers;
import me.vilsol.itemgenerator.modifiers.WeaponModifiers;
import minecade.dungeonrealms.enums.ItemRarity;
import minecade.dungeonrealms.enums.ItemTier;
import minecade.dungeonrealms.enums.ItemType;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemGenerator {
	
	public static HashMap<Class<? extends ItemModifier>, ItemModifier> modifiers = new HashMap<Class<? extends ItemModifier>, ItemModifier>();
	public static List<ItemModifier> modifierObjects = new ArrayList<ItemModifier>();
	
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
		
		final HashMap<ModifierCondition, ItemModifier> conditions = new HashMap<ModifierCondition, ItemModifier>();
		
		Collections.shuffle(modifierObjects);
		
		for(ItemModifier modifier : modifierObjects){
			if(modifier.canApply(type)){
				ModifierCondition mc = modifier.tryModifier(meta, tier, rarity, type, mobTier);
				if(mc != null){
					conditions.put(mc, modifier);
					ModifierCondition bonus = mc.getBonus();
					while(bonus != null){
						String prefix = modifier.getPrefix(meta);
						String suffix = modifier.getSuffix(meta);
						
						if(bonus.getReplacement() != null && bonus.getReplacement().size() > 0){
							ItemModifier replacement = ItemGenerator.modifiers.get(bonus.getReplacement().get(new Random().nextInt(bonus.getReplacement().size())));
							prefix = replacement.getPrefix(meta);
							suffix = replacement.getSuffix(meta);
						}
						
						bonus.setChosenPrefix(prefix);
						bonus.setChosenSuffix(suffix);
						
						conditions.put(bonus, modifier);
						
						bonus = bonus.getBonus();
					}
				}
			}
		}

		List<ModifierCondition> order = new ArrayList<ModifierCondition>();
		
		for(Object ob : Arrays.asList(conditions.keySet().toArray())){
			ModifierCondition mc = (ModifierCondition) ob;
			if(!mc.canApply(conditions.keySet())){
				conditions.remove(mc);
			}else{
				order.add(mc);
			}
		}
		
		Collections.sort(order, new Comparator<ModifierCondition>() {

			@Override
			public int compare(ModifierCondition mc1, ModifierCondition mc2) {
				return conditions.get(mc1).getOrderPriority() - conditions.get(mc2).getOrderPriority();
			}
			
		});
		
		for(ModifierCondition mc : order){
			ItemModifier im = conditions.get(mc);
			
			int belowChance = (mc.getChance() < 0) ? im.getChance() : mc.getChance();

			if(r.nextInt(100) < belowChance){
				meta = im.applyModifier(mc, meta);
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
		WeaponModifiers wm = new WeaponModifiers();
		wm.new Accuracy();
		wm.new ArmorPenetration();
		wm.new Blind();
		wm.new Critical();
		wm.new Damage();
		wm.new Elemental();
		wm.new ElementalBow();
		wm.new Knockback();
		wm.new LifeSteal();
		wm.new Pure();
		wm.new Slow();
		wm.new StrDexVitInt();
		wm.new SwordDamage();
		wm.new Versus();
		
		ArmorModifiers am = new ArmorModifiers();
		am.new Block();
		am.new Dodge();
		am.new EnergyRegen();
		am.new GemFind();
		am.new HP();
		am.new ChestplateHP();
		am.new HPRegen();
		am.new ItemFind();
		am.new MainArmor();
		am.new OtherArmor();
		am.new Reflection();
		am.new Resistances();
		am.new StrDexVitInt();
		am.new Thorns();
	}
	
}
