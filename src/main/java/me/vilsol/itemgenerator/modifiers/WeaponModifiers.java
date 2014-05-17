package me.vilsol.itemgenerator.modifiers;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import me.vilsol.itemgenerator.engine.ItemModifier;
import me.vilsol.itemgenerator.engine.ModifierCondition;
import me.vilsol.itemgenerator.engine.ModifierRange;
import me.vilsol.itemgenerator.engine.ModifierType;
import minecade.dungeonrealms.enums.ItemTier;
import minecade.dungeonrealms.enums.ItemType;

import org.bukkit.ChatColor;
import org.bukkit.inventory.meta.ItemMeta;

public class WeaponModifiers {
	
	public static List<ItemType> all = Arrays.asList(ItemType.AXE, ItemType.BOOTS, ItemType.BOW, ItemType.CHESTPLATE, ItemType.HELMET, ItemType.LEGGINGS, ItemType.POLEARM, ItemType.STAFF, ItemType.SWORD);


	public class StrDexVitInt extends ItemModifier {

		public StrDexVitInt() {
			super(all, -1, null, null);
			addCondition(new ModifierCondition(ItemTier.T1, null, new ModifierRange(ModifierType.STATIC, 1, 15), 25));
			addCondition(new ModifierCondition(ItemTier.T2, null, new ModifierRange(ModifierType.STATIC, 1, 35), 20).setBonus(new ModifierCondition(ItemTier.T2, null, new ModifierRange(ModifierType.STATIC, 1, 35), 5).setReplacement(StrDexVitInt.class)));
			addCondition(new ModifierCondition(ItemTier.T3, null, new ModifierRange(ModifierType.STATIC, 1, 35), 15).setBonus(new ModifierCondition(ItemTier.T3, null, new ModifierRange(ModifierType.STATIC, 1, 35), 5).setReplacement(StrDexVitInt.class).setBonus(new ModifierCondition(ItemTier.T3, null, new ModifierRange(ModifierType.STATIC, 1, 35), 1).setReplacement(StrDexVitInt.class))));
			addCondition(new ModifierCondition(ItemTier.T4, null, new ModifierRange(ModifierType.STATIC, 1, 75), 15).setBonus(new ModifierCondition(ItemTier.T4, null, new ModifierRange(ModifierType.STATIC, 1, 35), 9).setReplacement(StrDexVitInt.class).setBonus(new ModifierCondition(ItemTier.T4, null, new ModifierRange(ModifierType.STATIC, 1, 35), 4).setReplacement(StrDexVitInt.class))));
			addCondition(new ModifierCondition(ItemTier.T5, null, new ModifierRange(ModifierType.STATIC, 1, 75), 20).setBonus(new ModifierCondition(ItemTier.T5, null, new ModifierRange(ModifierType.STATIC, 1, 35), 10).setReplacement(StrDexVitInt.class).setBonus(new ModifierCondition(ItemTier.T5, null, new ModifierRange(ModifierType.STATIC, 1, 35), 5).setReplacement(StrDexVitInt.class))));
		}
		
		@Override
		public String getPrefix(ItemMeta meta){
			ChatColor r = ChatColor.RED;
			List<String> allStats = Arrays.asList(r + "STR: +", r + "INT: +", r + "VIT: +", r + "DEX: +");
			return allStats.get(new Random().nextInt(allStats.size()));
		}
		
	}
	
}
