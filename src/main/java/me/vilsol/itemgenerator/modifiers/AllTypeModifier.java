package me.vilsol.itemgenerator.modifiers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;

import me.vilsol.itemgenerator.engine.ItemModifier;
import me.vilsol.itemgenerator.engine.ModifierCondition;
import me.vilsol.itemgenerator.engine.ModifierRange;
import me.vilsol.itemgenerator.engine.ModifierType;
import minecade.dungeonrealms.enums.ItemTier;
import minecade.dungeonrealms.enums.ItemType;

public class AllTypeModifier {
	
	public static List<ItemType> all = Arrays.asList(ItemType.AXE, ItemType.BOOTS, ItemType.BOW, ItemType.CHESTPLATE, ItemType.HELMET, ItemType.LEGGINGS, ItemType.POLEARM, ItemType.STAFF, ItemType.SWORD);

	public class Strength extends ItemModifier {

		public Strength() {
			super(all, -1, ChatColor.RED + "STR: +", null);
			List<Class<? extends ItemModifier>> cantContain = new ArrayList<Class<? extends ItemModifier>>();
			cantContain.addAll(Arrays.asList(Strength.class));
			addCondition(new ModifierCondition(ItemTier.T1, null, new ModifierRange(ModifierType.STATIC, 1, 15), 25).setCantContain(cantContain));
			addCondition(new ModifierCondition(ItemTier.T2, null, new ModifierRange(ModifierType.STATIC, 1, 35), 20).setCantContain(cantContain).setBonus(new ModifierCondition(ItemTier.T2, null, new ModifierRange(ModifierType.STATIC, 1, 35), 5).setReplacement(cantContain)));
			addCondition(new ModifierCondition(ItemTier.T3, null, new ModifierRange(ModifierType.STATIC, 1, 35), 15).setCantContain(cantContain).setBonus(new ModifierCondition(ItemTier.T3, null, new ModifierRange(ModifierType.STATIC, 1, 35), 5).setReplacement(cantContain).setBonus(new ModifierCondition(ItemTier.T3, null, new ModifierRange(ModifierType.STATIC, 1, 35), 1).setReplacement(cantContain))));
			addCondition(new ModifierCondition(ItemTier.T4, null, new ModifierRange(ModifierType.STATIC, 1, 75), 15).setCantContain(cantContain).setBonus(new ModifierCondition(ItemTier.T4, null, new ModifierRange(ModifierType.STATIC, 1, 35), 9).setReplacement(cantContain).setBonus(new ModifierCondition(ItemTier.T4, null, new ModifierRange(ModifierType.STATIC, 1, 35), 4).setReplacement(cantContain))));
			addCondition(new ModifierCondition(ItemTier.T5, null, new ModifierRange(ModifierType.STATIC, 1, 75), 20).setCantContain(cantContain).setBonus(new ModifierCondition(ItemTier.T5, null, new ModifierRange(ModifierType.STATIC, 1, 35), 10).setReplacement(cantContain).setBonus(new ModifierCondition(ItemTier.T5, null, new ModifierRange(ModifierType.STATIC, 1, 35), 5).setReplacement(cantContain))));
		}
		
	}

	public class Dexterity extends ItemModifier {

		public Dexterity() {
			super(all, -1, ChatColor.RED + "DEX: +", null);
			List<Class<? extends ItemModifier>> cantContain = new ArrayList<Class<? extends ItemModifier>>();
			cantContain.addAll(Arrays.asList(Dexterity.class));
			addCondition(new ModifierCondition(ItemTier.T1, null, new ModifierRange(ModifierType.STATIC, 1, 15), 25).setCantContain(cantContain));
			addCondition(new ModifierCondition(ItemTier.T2, null, new ModifierRange(ModifierType.STATIC, 1, 35), 20).setCantContain(cantContain).setBonus(new ModifierCondition(ItemTier.T2, null, new ModifierRange(ModifierType.STATIC, 1, 35), 5).setReplacement(cantContain)));
			addCondition(new ModifierCondition(ItemTier.T3, null, new ModifierRange(ModifierType.STATIC, 1, 35), 15).setCantContain(cantContain).setBonus(new ModifierCondition(ItemTier.T3, null, new ModifierRange(ModifierType.STATIC, 1, 35), 5).setReplacement(cantContain).setBonus(new ModifierCondition(ItemTier.T3, null, new ModifierRange(ModifierType.STATIC, 1, 35), 1).setReplacement(cantContain))));
			addCondition(new ModifierCondition(ItemTier.T4, null, new ModifierRange(ModifierType.STATIC, 1, 75), 15).setCantContain(cantContain).setBonus(new ModifierCondition(ItemTier.T4, null, new ModifierRange(ModifierType.STATIC, 1, 35), 9).setReplacement(cantContain).setBonus(new ModifierCondition(ItemTier.T4, null, new ModifierRange(ModifierType.STATIC, 1, 35), 4).setReplacement(cantContain))));
			addCondition(new ModifierCondition(ItemTier.T5, null, new ModifierRange(ModifierType.STATIC, 1, 75), 20).setCantContain(cantContain).setBonus(new ModifierCondition(ItemTier.T5, null, new ModifierRange(ModifierType.STATIC, 1, 35), 10).setReplacement(cantContain).setBonus(new ModifierCondition(ItemTier.T5, null, new ModifierRange(ModifierType.STATIC, 1, 35), 5).setReplacement(cantContain))));
		}
		
	}

	public class Intelligence extends ItemModifier {

		public Intelligence() {
			super(all, -1, ChatColor.RED + "INT: +", null);
			List<Class<? extends ItemModifier>> cantContain = new ArrayList<Class<? extends ItemModifier>>();
			cantContain.addAll(Arrays.asList(Intelligence.class));
			addCondition(new ModifierCondition(ItemTier.T1, null, new ModifierRange(ModifierType.STATIC, 1, 15), 25).setCantContain(cantContain));
			addCondition(new ModifierCondition(ItemTier.T2, null, new ModifierRange(ModifierType.STATIC, 1, 35), 20).setCantContain(cantContain).setBonus(new ModifierCondition(ItemTier.T2, null, new ModifierRange(ModifierType.STATIC, 1, 35), 5).setReplacement(cantContain)));
			addCondition(new ModifierCondition(ItemTier.T3, null, new ModifierRange(ModifierType.STATIC, 1, 35), 15).setCantContain(cantContain).setBonus(new ModifierCondition(ItemTier.T3, null, new ModifierRange(ModifierType.STATIC, 1, 35), 5).setReplacement(cantContain).setBonus(new ModifierCondition(ItemTier.T3, null, new ModifierRange(ModifierType.STATIC, 1, 35), 1).setReplacement(cantContain))));
			addCondition(new ModifierCondition(ItemTier.T4, null, new ModifierRange(ModifierType.STATIC, 1, 75), 15).setCantContain(cantContain).setBonus(new ModifierCondition(ItemTier.T4, null, new ModifierRange(ModifierType.STATIC, 1, 35), 9).setReplacement(cantContain).setBonus(new ModifierCondition(ItemTier.T4, null, new ModifierRange(ModifierType.STATIC, 1, 35), 4).setReplacement(cantContain))));
			addCondition(new ModifierCondition(ItemTier.T5, null, new ModifierRange(ModifierType.STATIC, 1, 75), 20).setCantContain(cantContain).setBonus(new ModifierCondition(ItemTier.T5, null, new ModifierRange(ModifierType.STATIC, 1, 35), 10).setReplacement(cantContain).setBonus(new ModifierCondition(ItemTier.T5, null, new ModifierRange(ModifierType.STATIC, 1, 35), 5).setReplacement(cantContain))));
		}
		
	}

	public class Vitality extends ItemModifier {

		public Vitality() {
			super(all, -1, ChatColor.RED + "VIT: +", null);
			List<Class<? extends ItemModifier>> cantContain = new ArrayList<Class<? extends ItemModifier>>();
			cantContain.addAll(Arrays.asList(Vitality.class));
			addCondition(new ModifierCondition(ItemTier.T1, null, new ModifierRange(ModifierType.STATIC, 1, 15), 25).setCantContain(cantContain));
			addCondition(new ModifierCondition(ItemTier.T2, null, new ModifierRange(ModifierType.STATIC, 1, 35), 20).setCantContain(cantContain).setBonus(new ModifierCondition(ItemTier.T2, null, new ModifierRange(ModifierType.STATIC, 1, 35), 5).setReplacement(cantContain)));
			addCondition(new ModifierCondition(ItemTier.T3, null, new ModifierRange(ModifierType.STATIC, 1, 35), 15).setCantContain(cantContain).setBonus(new ModifierCondition(ItemTier.T3, null, new ModifierRange(ModifierType.STATIC, 1, 35), 5).setReplacement(cantContain).setBonus(new ModifierCondition(ItemTier.T3, null, new ModifierRange(ModifierType.STATIC, 1, 35), 1).setReplacement(cantContain))));
			addCondition(new ModifierCondition(ItemTier.T4, null, new ModifierRange(ModifierType.STATIC, 1, 75), 15).setCantContain(cantContain).setBonus(new ModifierCondition(ItemTier.T4, null, new ModifierRange(ModifierType.STATIC, 1, 35), 9).setReplacement(cantContain).setBonus(new ModifierCondition(ItemTier.T4, null, new ModifierRange(ModifierType.STATIC, 1, 35), 4).setReplacement(cantContain))));
			addCondition(new ModifierCondition(ItemTier.T5, null, new ModifierRange(ModifierType.STATIC, 1, 75), 20).setCantContain(cantContain).setBonus(new ModifierCondition(ItemTier.T5, null, new ModifierRange(ModifierType.STATIC, 1, 35), 10).setReplacement(cantContain).setBonus(new ModifierCondition(ItemTier.T5, null, new ModifierRange(ModifierType.STATIC, 1, 35), 5).setReplacement(cantContain))));
		}
		
	}
	
}
