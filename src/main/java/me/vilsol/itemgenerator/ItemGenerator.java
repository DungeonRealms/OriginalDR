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
import minecade.dungeonrealms.EnchantMechanics.EnchantMechanics;
import minecade.dungeonrealms.ItemMechanics.ItemMechanics;
import minecade.dungeonrealms.enums.ItemRarity;
import minecade.dungeonrealms.enums.ItemTier;
import minecade.dungeonrealms.enums.ItemType;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemGenerator {
	
	public static HashMap<Class<? extends ItemModifier>, ItemModifier> modifiers = new HashMap<Class<? extends ItemModifier>, ItemModifier>();
	public static List<ItemModifier> modifierObjects = new ArrayList<ItemModifier>();
	
	private ItemType type;
	private ItemTier tier;
	private ItemRarity rarity;
	
	private int mobTier = -1;
	private boolean isReroll = false;
    private int pLevel;
	
	private ItemStack item;
	private ItemStack origItem; // for rerolling
	
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
	
	public ItemGenerator setReroll(boolean reroll) {
	    this.isReroll = reroll;
	    return this;
	}
	
	public ItemGenerator setOrigItem(ItemStack origItem) {
        this.origItem = origItem;
        return this;
    }

    public ItemGenerator setPLevel(int level) {
        this.pLevel = level;
        return this;
    }

	@SuppressWarnings("unchecked")
    public ItemGenerator generateItem(){
	    ItemTier tier = this.tier;
        ItemType type = this.type;
        ItemRarity rarity = this.rarity;
        
	    if (isReroll && origItem != null && (ItemMechanics.isArmor(origItem) || ItemMechanics.isWeapon(origItem))) {
    		tier = ItemTier.getTierFromMaterial(origItem.getType());
    		type = ItemType.getTypeFromMaterial(origItem.getType());
    		rarity = ItemRarity.getRarityFromItem(origItem);
	    }
	    
	    if (isReroll && origItem != null && origItem.getType() == Material.BOW) {
	        tier = ItemTier.getTierFromItem(origItem);
	    }
		
		Random r = new Random();

		if(tier == null) tier = ItemTier.values()[r.nextInt(ItemTier.values().length - 1)];
		if(type == null) type = ItemType.values()[r.nextInt(ItemType.values().length - 1)];
		if(rarity == null) rarity = ItemRarity.values()[r.nextInt(ItemRarity.values().length - 1)];
		
		ItemStack item = isReroll && origItem != null && (ItemMechanics.isArmor(origItem) || ItemMechanics.isWeapon(origItem)) ? origItem : new ItemStack(type.getTier(tier));
		ItemMeta meta = item.getItemMeta().clone();
		
		if (!isReroll)
		    meta.setLore(new ArrayList<String>());
		else {
		    if (ItemMechanics.isWeapon(origItem))
		        meta.setLore(meta.getLore().subList(0, 1)); // strips everything except for dmg
		    else if (ItemMechanics.isArmor(origItem))
		        meta.setLore(meta.getLore().subList(0, 3)); // strips everything except for dps/armor, hp, and energy/hp regen
		}
		
		final HashMap<ModifierCondition, ItemModifier> conditions = new HashMap<ModifierCondition, ItemModifier>();
		
		Collections.shuffle(modifierObjects);
		
		for(ItemModifier modifier : modifierObjects){
			if(modifier.canApply(type)){
			    if (isReroll && !modifier.isIncludeOnReroll()) continue;
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
			    ItemModifier im = conditions.get(mc);
	            
	            int belowChance = (mc.getChance() < 0) ? im.getChance() : mc.getChance();

	            if (r.nextInt(100) < belowChance) {
	                order.add(mc);
	            }
	            else {
	                conditions.remove(mc);
	            }
			}
		}
		
		for (ItemModifier modifier : conditions.values()) {
		    for (ModifierCondition mc : (List<ModifierCondition>) ((ArrayList<ModifierCondition>) order).clone()) {
		        if (!(mc.checkCantContain(modifier.getClass()))) {
		            order.remove(mc);
		        }
		    }
        }
		
		Collections.sort(order, new Comparator<ModifierCondition>() {

			@Override
			public int compare(ModifierCondition mc1, ModifierCondition mc2) {
				return conditions.get(mc1).getOrderPriority() - conditions.get(mc2).getOrderPriority();
			}
			
		});

		String modName = "";
        String name = tier.getTierColor().toString();
        String[] bonuses = new String[24];
        Arrays.fill(bonuses, "");
        
        // name armor with energy or hp/s being rerolled correctly
        if (isReroll && origItem != null && origItem.hasItemMeta() && origItem.getItemMeta().hasLore() && ItemMechanics.isArmor(origItem)) {
            
            for (String line : origItem.getItemMeta().getLore()) {

                if (!line.contains(":")) continue;
                
                if (ChatColor.stripColor(line.substring(0, line.indexOf(":"))).equals("ENERGY REGEN")) {
                    bonuses[11] = "ENERGY REGEN";
                }
                else if (ChatColor.stripColor(line.substring(0, line.indexOf(":"))).equals("HP REGEN")) {
                    bonuses[2] = "HP REGEN";
                }
                
            }
            
        }
        
		for (ModifierCondition mc : order) {
		    ItemModifier im = conditions.get(mc);
		    meta = im.applyModifier(mc, meta);
		    modName = ChatColor.stripColor(mc.getChosenPrefix().substring(0, mc.getChosenPrefix().indexOf(":")));
		    
            // apply the prefixes/suffixes to priority array
            switch (modName) {
            // ARMOR PREFIXES
            case "DODGE":
                bonuses[0] = "DODGE";
                break;
            case "REFLECTION":
                bonuses[1] = "REFLECTION";
                break;
            case "HP REGEN":
                bonuses[2] = "HP REGEN";
                break;
            case "BLOCK":
                bonuses[3] = "BLOCK";
                break;
            // WEAPON PREFIXES
            case "PURE DMG":
                bonuses[4] = "PURE DMG";
                break;
            case "ACCURACY":
                bonuses[5] = "ACCURACY";
                break;
            case "KNOCKBACK":
                bonuses[6] = "KNOCKBACK";
                break;
            case "SLOW":
                bonuses[7] = "SLOW";
                break;
            case "LIFE STEAL":
                bonuses[8] = "LIFE STEAL";
                break;
            case "CRITICAL HIT":
                bonuses[9] = "CRITICAL HIT";
                break;
            case "ARMOR PENETRATION":
                bonuses[10] = "ARMOR PENETRATION";
                break;
            // ARMOR SUFFIXES
            case "ENERGY REGEN":
                bonuses[11] = "ENERGY REGEN";
                break;
            case "FIRE RESISTANCE":
                bonuses[12] = "FIRE RESISTANCE";
                break;
            case "ICE RESISTANCE":
                bonuses[13] = "ICE RESISTANCE";
                break;
            case "POISON RESISTANCE":
                bonuses[14] = "POISON RESISTANCE";
                break;
            case "GEM FIND":
                bonuses[15] = "GEM FIND";
                break;
            case "ITEM FIND":
                bonuses[16] = "ITEM FIND";
                break;
            case "THORNS":
                bonuses[17] = "THORNS";
                break;
            // WEAPON SUFFIXES
            case "BLIND":
                bonuses[18] = "BLIND";
                break;
            case "vs. MONSTERS":
                bonuses[19] = "vs. MONSTERS";
                break;
            case "vs. PLAYERS":
                bonuses[20] = "vs. PLAYERS";
                break;
            case "FIRE DMG":
                bonuses[21] = "FIRE DMG";
                break;
            case "ICE DMG":
                bonuses[22] = "ICE DMG";
                break;
            case "POISON DMG":
                bonuses[23] = "POISON DMG";
                break;
            default:
                break;
            }
		}
		
		for (int i = 0; i < bonuses.length; i++) {
            // apply the prefixes/suffixes to item name
            switch (bonuses[i]) {
            // ARMOR PREFIXES
            case "DODGE":
                name += "Agile ";
                break;
            case "REFLECTION":
                name += "Reflective ";
                break;
            case "HP REGEN":
                name += "Mending ";
                break;
            case "BLOCK":
                name += "Protective ";
                break;
            // WEAPON PREFIXES
            case "PURE DMG":
                name += "Pure ";
                break;
            case "ACCURACY":
                name += "Accurate ";
                break;
            case "KNOCKBACK":
                name += "Brute ";
                break;
            case "SLOW":
                name += "Snaring ";
                break;
            case "LIFE STEAL":
                name += "Vampyric ";
                break;
            case "CRITICAL HIT":
                name += "Deadly ";
                break;
            case "ARMOR PENETRATION":
                name += "Penetrating ";
                break;
            // ARMOR SUFFIXES
            case "ENERGY REGEN":
                name += (name.contains(type.getTierName(tier)) ? " of Fortitude" : type.getTierName(tier) + " of Fortitude");
                break;
            case "FIRE RESISTANCE":
                name += (name.contains(type.getTierName(tier)) ? " and Fire Resist" : type.getTierName(tier) + " of Fire Resist");
                break;
            case "ICE RESISTANCE":
                name += (name.contains(type.getTierName(tier)) ? " and Ice Resist" : type.getTierName(tier) + " of Ice Resist");
                break;
            case "POISON RESISTANCE":
                name += (name.contains(type.getTierName(tier)) ? " and Poison Resist" : type.getTierName(tier) + " of Poison Resist");
                break;
            case "GEM FIND":
                name += (name.contains(type.getTierName(tier)) ? " Golden" : type.getTierName(tier) + " of Pickpocketing");
                break;
            case "ITEM FIND":
                name += (name.contains(type.getTierName(tier)) ? " Treasure" : type.getTierName(tier) + " of Treasure");
                break;
            case "THORNS":
                name += (name.contains(type.getTierName(tier)) ? " Spikes" : type.getTierName(tier) + " of Thorns");
                break;
            // WEAPON SUFFIXES
            case "BLIND":
                name += type.getTierName(tier) + " of Blindness";
                break;
            case "vs. MONSTERS":
                name += (name.contains(type.getTierName(tier)) ? " Slaying" : type.getTierName(tier) + " of Slaying");
                break;
            case "vs. PLAYERS":
                name += (name.contains(type.getTierName(tier)) ? " Slaughter" : type.getTierName(tier) + " of Slaughter");
                break;
            case "FIRE DMG":
                name += (name.contains(type.getTierName(tier)) ? " Fire" : type.getTierName(tier) + " of Fire");
                break;
            case "ICE DMG":
                name += (name.contains(type.getTierName(tier)) ? " Ice" : type.getTierName(tier) + " of Ice");
                break;
            case "POISON DMG":
                name += (name.contains(type.getTierName(tier)) ? " Poison" : type.getTierName(tier) + " of Poison");
                break;
            default:
                break;
            }
		}
		
		List<String> lore = meta.getLore();
		if(rarity == ItemRarity.COMMON) lore.add(ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + "Common");
		if(rarity == ItemRarity.UNCOMMON) lore.add(ChatColor.GREEN.toString() + ChatColor.ITALIC.toString() + "Uncommon");
		if(rarity == ItemRarity.RARE) lore.add(ChatColor.AQUA.toString() + ChatColor.ITALIC.toString() + "Rare");
		if(rarity == ItemRarity.UNIQUE) lore.add(ChatColor.YELLOW.toString() + ChatColor.ITALIC.toString() + "Unique");
		
		if (isReroll && ItemMechanics.isLegacy(origItem)) lore.add (ChatColor.GOLD.toString() + ChatColor.BOLD + "LEGACY");

        // add custom EC lore
        if (isReroll && origItem != null && origItem.hasItemMeta() && origItem.getItemMeta().hasLore()) {
            for (String line : origItem.getItemMeta().getLore()) {
                if (line.contains(ChatColor.GOLD.toString()) && line.contains(ChatColor.ITALIC.toString())) {
                    lore.add(line);
                }
            }
        }

        if (isReroll && EnchantMechanics.hasProtection(origItem)) lore.add(ChatColor.GREEN.toString() + ChatColor.BOLD + "PROTECTED");
		
		meta.setLore(lore);
		
		if (!(name.contains(type.getTierName(tier)))) name += type.getTierName(tier);
		
		int oldEnchantCount = EnchantMechanics.getEnchantCount(origItem);
		if (isReroll && oldEnchantCount > 0) {
		    name = ChatColor.RED + "[+" + oldEnchantCount + "] " + ChatColor.RESET + name;
		}
		    
		meta.setDisplayName(name);
		if (isReroll && (ItemMechanics.isECNamed(origItem) || ItemMechanics.isCustomNamed(origItem))) meta.setDisplayName(origItem.getItemMeta().getDisplayName());
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
		wm.new SwordDamage();
		wm.new AxeDamage();
		wm.new StaffDamage();
		wm.new PolearmDamage();
		wm.new BowDamage();
		wm.new Elemental();
		wm.new ElementalBow();
		wm.new Knockback();
		wm.new LifeSteal();
		wm.new Pure();
		wm.new Slow();
//		wm.new StrDexVitInt(); disabled as of patch 1.9 by Mayley's request
		wm.new SwordDamage();
		wm.new Versus();
		
		ArmorModifiers am = new ArmorModifiers();
		am.new Block();
		am.new Dodge();
		am.new EnergyRegen();
		am.new GemFind();
//		am.new HP();
		am.new ChestplateHP();
		am.new LeggingsHP();
		am.new OtherHP();
		am.new MainDPS();
		am.new OtherDPS();
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
