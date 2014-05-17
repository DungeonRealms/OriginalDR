package me.vilsol.itemgenerator.modifiers;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import me.vilsol.itemgenerator.engine.ItemModifier;
import me.vilsol.itemgenerator.engine.ModifierCondition;
import me.vilsol.itemgenerator.engine.ModifierRange;
import me.vilsol.itemgenerator.engine.ModifierType;
import minecade.dungeonrealms.enums.ItemRarity;
import minecade.dungeonrealms.enums.ItemTier;
import minecade.dungeonrealms.enums.ItemType;

import org.bukkit.ChatColor;
import org.bukkit.inventory.meta.ItemMeta;

public class WeaponModifiers {

	public static List<ItemType> weapons = Arrays.asList(ItemType.AXE, ItemType.BOW, ItemType.POLEARM, ItemType.STAFF, ItemType.SWORD);
	
	public static List<String> elements = Arrays.asList("FIRE", "ICE", "POISON");
	public static List<Double> elementMultipliers = Arrays.asList(0.15D, 1D, 0.10D);

	public static List<String> versus = Arrays.asList("MONSTERS", "PLAYERS");
	
	public static ChatColor r = ChatColor.RED;

	public class SwordDamage extends ItemModifier {

		public SwordDamage() {
			super(Arrays.asList(ItemType.SWORD), 100, r + "DMG: ", null);
			
			setOrderPriority(0);
			
			addCondition(new ModifierCondition(ItemTier.T1, ItemRarity.COMMON, new ModifierRange(ModifierType.TRIPLE, 1, 2, 5)));
			addCondition(new ModifierCondition(ItemTier.T1, ItemRarity.UNCOMMON, new ModifierRange(ModifierType.TRIPLE, 3, 5, 8)));
			addCondition(new ModifierCondition(ItemTier.T1, ItemRarity.RARE, new ModifierRange(ModifierType.TRIPLE, 6, 9, 23)));
			addCondition(new ModifierCondition(ItemTier.T1, ItemRarity.UNIQUE, new ModifierRange(ModifierType.TRIPLE, 6, 9, 23)));

			addCondition(new ModifierCondition(ItemTier.T2, ItemRarity.COMMON, new ModifierRange(ModifierType.TRIPLE, 10, 12, 17)));
			addCondition(new ModifierCondition(ItemTier.T2, ItemRarity.UNCOMMON, new ModifierRange(ModifierType.TRIPLE, 16, 18, 24)));
			addCondition(new ModifierCondition(ItemTier.T2, ItemRarity.RARE, new ModifierRange(ModifierType.TRIPLE, 20, 30, 65)));
			addCondition(new ModifierCondition(ItemTier.T2, ItemRarity.UNIQUE, new ModifierRange(ModifierType.TRIPLE, 20, 30, 65)));

			addCondition(new ModifierCondition(ItemTier.T3, ItemRarity.COMMON, new ModifierRange(ModifierType.TRIPLE, 25, 30, 45)));
			addCondition(new ModifierCondition(ItemTier.T3, ItemRarity.UNCOMMON, new ModifierRange(ModifierType.TRIPLE, 30, 35, 70)));
			addCondition(new ModifierCondition(ItemTier.T3, ItemRarity.RARE, new ModifierRange(ModifierType.TRIPLE, 30, 60, 150)));
			addCondition(new ModifierCondition(ItemTier.T3, ItemRarity.UNIQUE, new ModifierRange(ModifierType.TRIPLE, 30, 60, 150)));

			addCondition(new ModifierCondition(ItemTier.T4, ItemRarity.COMMON, new ModifierRange(ModifierType.TRIPLE, 65, 80, 125)));
			addCondition(new ModifierCondition(ItemTier.T4, ItemRarity.UNCOMMON, new ModifierRange(ModifierType.TRIPLE, 70, 85, 155)));
			addCondition(new ModifierCondition(ItemTier.T4, ItemRarity.RARE, new ModifierRange(ModifierType.TRIPLE, 90, 110, 220)));
			addCondition(new ModifierCondition(ItemTier.T4, ItemRarity.UNIQUE, new ModifierRange(ModifierType.TRIPLE, 90, 110, 220)));

			addCondition(new ModifierCondition(ItemTier.T5, ItemRarity.COMMON, new ModifierRange(ModifierType.TRIPLE, 130, 140, 210)));
			addCondition(new ModifierCondition(ItemTier.T5, ItemRarity.UNCOMMON, new ModifierRange(ModifierType.TRIPLE, 150, 160, 260)));
			addCondition(new ModifierCondition(ItemTier.T5, ItemRarity.RARE, new ModifierRange(ModifierType.TRIPLE, 160, 230, 407)));
			addCondition(new ModifierCondition(ItemTier.T5, ItemRarity.UNIQUE, new ModifierRange(ModifierType.TRIPLE, 160, 230, 407)));
		}
		
	}

	public class Damage extends ItemModifier {

		public Damage() {
			super(Arrays.asList(ItemType.AXE, ItemType.BOW, ItemType.POLEARM, ItemType.STAFF), 100, r + "DMG: ", null);
			
			setOrderPriority(0);
			
			addCondition(new ModifierCondition(ItemTier.T1, ItemRarity.COMMON, new ModifierRange(ModifierType.TRIPLE, 1, 1, 3)));
			addCondition(new ModifierCondition(ItemTier.T1, ItemRarity.UNCOMMON, new ModifierRange(ModifierType.TRIPLE, 2, 3, 4)));
			addCondition(new ModifierCondition(ItemTier.T1, ItemRarity.RARE, new ModifierRange(ModifierType.TRIPLE, 3, 5, 12)));
			addCondition(new ModifierCondition(ItemTier.T1, ItemRarity.UNIQUE, new ModifierRange(ModifierType.TRIPLE, 3, 5, 12)));

			addCondition(new ModifierCondition(ItemTier.T2, ItemRarity.COMMON, new ModifierRange(ModifierType.TRIPLE, 5, 6, 9)));
			addCondition(new ModifierCondition(ItemTier.T2, ItemRarity.UNCOMMON, new ModifierRange(ModifierType.TRIPLE, 8, 9, 12)));
			addCondition(new ModifierCondition(ItemTier.T2, ItemRarity.RARE, new ModifierRange(ModifierType.TRIPLE, 10, 15, 33)));
			addCondition(new ModifierCondition(ItemTier.T2, ItemRarity.UNIQUE, new ModifierRange(ModifierType.TRIPLE, 10, 15, 33)));

			addCondition(new ModifierCondition(ItemTier.T3, ItemRarity.COMMON, new ModifierRange(ModifierType.TRIPLE, 13, 15, 23)));
			addCondition(new ModifierCondition(ItemTier.T3, ItemRarity.UNCOMMON, new ModifierRange(ModifierType.TRIPLE, 15, 18, 35)));
			addCondition(new ModifierCondition(ItemTier.T3, ItemRarity.RARE, new ModifierRange(ModifierType.TRIPLE, 15, 30, 75)));
			addCondition(new ModifierCondition(ItemTier.T3, ItemRarity.UNIQUE, new ModifierRange(ModifierType.TRIPLE, 15, 30, 75)));

			addCondition(new ModifierCondition(ItemTier.T4, ItemRarity.COMMON, new ModifierRange(ModifierType.TRIPLE, 33, 40, 63)));
			addCondition(new ModifierCondition(ItemTier.T4, ItemRarity.UNCOMMON, new ModifierRange(ModifierType.TRIPLE, 35, 43, 78)));
			addCondition(new ModifierCondition(ItemTier.T4, ItemRarity.RARE, new ModifierRange(ModifierType.TRIPLE, 45, 55, 110)));
			addCondition(new ModifierCondition(ItemTier.T4, ItemRarity.UNIQUE, new ModifierRange(ModifierType.TRIPLE, 45, 55, 110)));

			addCondition(new ModifierCondition(ItemTier.T5, ItemRarity.COMMON, new ModifierRange(ModifierType.TRIPLE, 65, 70, 105)));
			addCondition(new ModifierCondition(ItemTier.T5, ItemRarity.UNCOMMON, new ModifierRange(ModifierType.TRIPLE, 75, 80, 130)));
			addCondition(new ModifierCondition(ItemTier.T5, ItemRarity.RARE, new ModifierRange(ModifierType.TRIPLE, 80, 115, 204)));
			addCondition(new ModifierCondition(ItemTier.T5, ItemRarity.UNIQUE, new ModifierRange(ModifierType.TRIPLE, 80, 115, 204)));
		}
		
	}
	
	public class StrDexVitInt extends ItemModifier {

		public StrDexVitInt() {
			super(weapons, -1, null, null);
			setOrderPriority(11);
			addCondition(new ModifierCondition(ItemTier.T1, null, new ModifierRange(ModifierType.STATIC, 1, 15), 25));
			addCondition(new ModifierCondition(ItemTier.T2, null, new ModifierRange(ModifierType.STATIC, 1, 35), 20).setBonus(new ModifierCondition(ItemTier.T2, null, new ModifierRange(ModifierType.STATIC, 1, 35), 5).setReplacement(StrDexVitInt.class)));
			addCondition(new ModifierCondition(ItemTier.T3, null, new ModifierRange(ModifierType.STATIC, 1, 35), 15).setBonus(new ModifierCondition(ItemTier.T3, null, new ModifierRange(ModifierType.STATIC, 1, 35), 5).setReplacement(StrDexVitInt.class).setBonus(new ModifierCondition(ItemTier.T3, null, new ModifierRange(ModifierType.STATIC, 1, 35), 1).setReplacement(StrDexVitInt.class))));
			addCondition(new ModifierCondition(ItemTier.T4, null, new ModifierRange(ModifierType.STATIC, 1, 75), 15).setBonus(new ModifierCondition(ItemTier.T4, null, new ModifierRange(ModifierType.STATIC, 1, 35), 9).setReplacement(StrDexVitInt.class).setBonus(new ModifierCondition(ItemTier.T4, null, new ModifierRange(ModifierType.STATIC, 1, 35), 4).setReplacement(StrDexVitInt.class))));
			addCondition(new ModifierCondition(ItemTier.T5, null, new ModifierRange(ModifierType.STATIC, 1, 75), 20).setBonus(new ModifierCondition(ItemTier.T5, null, new ModifierRange(ModifierType.STATIC, 1, 35), 10).setReplacement(StrDexVitInt.class).setBonus(new ModifierCondition(ItemTier.T5, null, new ModifierRange(ModifierType.STATIC, 1, 35), 5).setReplacement(StrDexVitInt.class))));
		}
		
		@Override
		public String getPrefix(ItemMeta meta){
			List<String> allStats = Arrays.asList(r + "STR: +", r + "INT: +", r + "VIT: +", r + "DEX: +");
			return allStats.get(new Random().nextInt(allStats.size()));
		}
		
	}
	
	public class Critical extends ItemModifier {

		public Critical() {
			super(weapons, -1, r + "CRITICAL HIT: ", "%");
			setOrderPriority(4);
			addCondition(new ModifierCondition(ItemTier.T1, null, new ModifierRange(ModifierType.STATIC, 1, 2), 2));
			addCondition(new ModifierCondition(ItemTier.T2, null, new ModifierRange(ModifierType.STATIC, 1, 4), 5));
			addCondition(new ModifierCondition(ItemTier.T3, null, new ModifierRange(ModifierType.STATIC, 1, 5), 8));
			addCondition(new ModifierCondition(ItemTier.T4, null, new ModifierRange(ModifierType.STATIC, 1, 6), 9));
			addCondition(new ModifierCondition(ItemTier.T5, null, new ModifierRange(ModifierType.STATIC, 1, 10), 7));
		}
		
	}
	
	public class LifeSteal extends ItemModifier {

		public LifeSteal() {
			super(weapons, -1, r + "LIFE STEAL: ", "%");
			setOrderPriority(2);
			addCondition(new ModifierCondition(ItemTier.T1, null, new ModifierRange(ModifierType.STATIC, 1, 30), 2));
			addCondition(new ModifierCondition(ItemTier.T2, null, new ModifierRange(ModifierType.STATIC, 1, 15), 4));
			addCondition(new ModifierCondition(ItemTier.T3, null, new ModifierRange(ModifierType.STATIC, 1, 12), 5));
			addCondition(new ModifierCondition(ItemTier.T4, null, new ModifierRange(ModifierType.STATIC, 1, 7), 10));
			addCondition(new ModifierCondition(ItemTier.T5, null, new ModifierRange(ModifierType.STATIC, 1, 8), 8));
		}
		
	}
	
	public class Knockback extends ItemModifier {

		public Knockback() {
			super(Arrays.asList(ItemType.AXE, ItemType.POLEARM, ItemType.STAFF, ItemType.SWORD), -1, r + "KNOCKBACK: ", "%");
			setOrderPriority(7);
			addCondition(new ModifierCondition(ItemTier.T1, null, new ModifierRange(ModifierType.STATIC, 1, 3), 3));
			addCondition(new ModifierCondition(ItemTier.T2, null, new ModifierRange(ModifierType.STATIC, 1, 6), 10));
			addCondition(new ModifierCondition(ItemTier.T3, null, new ModifierRange(ModifierType.STATIC, 1, 12), 13));
			addCondition(new ModifierCondition(ItemTier.T4, null, new ModifierRange(ModifierType.STATIC, 1, 15), 16));
			addCondition(new ModifierCondition(ItemTier.T5, null, new ModifierRange(ModifierType.STATIC, 1, 20), 20));
		}
		
	}
	
	public class Blind extends ItemModifier {

		public Blind() {
			super(weapons, -1, r + "BLIND: ", "%");
			setOrderPriority(8);
			addCondition(new ModifierCondition(ItemTier.T1, null, new ModifierRange(ModifierType.STATIC, 1, 5), 3));
			addCondition(new ModifierCondition(ItemTier.T2, null, new ModifierRange(ModifierType.STATIC, 1, 7), 5));
			addCondition(new ModifierCondition(ItemTier.T3, null, new ModifierRange(ModifierType.STATIC, 1, 9), 8));
			addCondition(new ModifierCondition(ItemTier.T4, null, new ModifierRange(ModifierType.STATIC, 1, 9), 9));
			addCondition(new ModifierCondition(ItemTier.T5, null, new ModifierRange(ModifierType.STATIC, 1, 11), 11));
		}
		
	}
	
	public class Slow extends ItemModifier {

		public Slow() {
			super(Arrays.asList(ItemType.BOW), -1, r + "SLOW: ", "%");
			setOrderPriority(6);
			addCondition(new ModifierCondition(ItemTier.T1, null, new ModifierRange(ModifierType.STATIC, 1, 3), 3));
			addCondition(new ModifierCondition(ItemTier.T2, null, new ModifierRange(ModifierType.STATIC, 1, 4), 10));
			addCondition(new ModifierCondition(ItemTier.T3, null, new ModifierRange(ModifierType.STATIC, 1, 5), 13));
			addCondition(new ModifierCondition(ItemTier.T4, null, new ModifierRange(ModifierType.STATIC, 1, 7), 16));
			addCondition(new ModifierCondition(ItemTier.T5, null, new ModifierRange(ModifierType.STATIC, 1, 10), 20));
		}
		
	}

	public class Elemental extends ItemModifier {

		public Elemental() {
			super(Arrays.asList(ItemType.AXE, ItemType.POLEARM, ItemType.STAFF, ItemType.SWORD), -1, null, null);
			setOrderPriority(9);
			addCondition(new ModifierCondition(ItemTier.T1, null, new ModifierRange(ModifierType.STATIC, 1, 4), 6));
			addCondition(new ModifierCondition(ItemTier.T2, null, new ModifierRange(ModifierType.STATIC, 1, 9), 9));
			addCondition(new ModifierCondition(ItemTier.T3, null, new ModifierRange(ModifierType.STATIC, 1, 15), 10));
			addCondition(new ModifierCondition(ItemTier.T4, null, new ModifierRange(ModifierType.STATIC, 1, 25), 15));
			addCondition(new ModifierCondition(ItemTier.T5, null, new ModifierRange(ModifierType.STATIC, 1, 55), 20));
		}
		
		@Override
		public String getPrefix(ItemMeta meta){
			return r + elements.get(new Random().nextInt(elements.size())) + " DMG: +";
		}
		
	}

	public class ElementalBow extends ItemModifier {

		public ElementalBow() {
			super(Arrays.asList(ItemType.BOW), -1, null, null);
			addCondition(new ModifierCondition(ItemTier.T1, null, new ModifierRange(ModifierType.STATIC, 1, 8), 6));
			addCondition(new ModifierCondition(ItemTier.T2, null, new ModifierRange(ModifierType.STATIC, 1, 15), 9));
			addCondition(new ModifierCondition(ItemTier.T3, null, new ModifierRange(ModifierType.STATIC, 1, 25), 10));
			addCondition(new ModifierCondition(ItemTier.T4, null, new ModifierRange(ModifierType.STATIC, 1, 45), 15));
			addCondition(new ModifierCondition(ItemTier.T5, null, new ModifierRange(ModifierType.STATIC, 1, 75), 20));
		}
		
		@Override
		public String getPrefix(ItemMeta meta){
			return r + elements.get(new Random().nextInt(elements.size())) + " DMG: +";
		}
		
	}
	
	public class Versus extends ItemModifier {

		public Versus() {
			super(weapons, -1, null, "% DMG");
			setOrderPriority(1);
			addCondition(new ModifierCondition(ItemTier.T1, null, new ModifierRange(ModifierType.STATIC, 1, 10), 6));
			addCondition(new ModifierCondition(ItemTier.T2, null, new ModifierRange(ModifierType.STATIC, 1, 12), 9));
			addCondition(new ModifierCondition(ItemTier.T3, null, new ModifierRange(ModifierType.STATIC, 1, 15), 10));
			addCondition(new ModifierCondition(ItemTier.T4, null, new ModifierRange(ModifierType.STATIC, 1, 20), 12));
			addCondition(new ModifierCondition(ItemTier.T5, null, new ModifierRange(ModifierType.STATIC, 1, 15), 15));
		}
		
		@Override
		public String getPrefix(ItemMeta meta){
			return r + "vs. " + versus.get(new Random().nextInt(versus.size())) + ": +";
		}
		
	}
	
	public class Pure extends ItemModifier {

		public Pure() {
			super(Arrays.asList(ItemType.AXE), -1, r + "PURE DMG: +", null);
			setOrderPriority(10);
			addCondition(new ModifierCondition(ItemTier.T1, null, new ModifierRange(ModifierType.STATIC, 1, 5), 6));
			addCondition(new ModifierCondition(ItemTier.T2, null, new ModifierRange(ModifierType.STATIC, 1, 8), 9));
			addCondition(new ModifierCondition(ItemTier.T3, null, new ModifierRange(ModifierType.STATIC, 1, 15), 5));
			addCondition(new ModifierCondition(ItemTier.T4, null, new ModifierRange(ModifierType.STATIC, 1, 25), 5));
			addCondition(new ModifierCondition(ItemTier.T5, null, new ModifierRange(ModifierType.STATIC, 1, 45), 10));
		}
		
	}
	
	public class Accuracy extends ItemModifier {

		public Accuracy() {
			super(Arrays.asList(ItemType.SWORD, ItemType.STAFF, ItemType.POLEARM), -1, r + "ACCURACY: ", "%");
			setOrderPriority(3);
			addCondition(new ModifierCondition(ItemTier.T1, null, new ModifierRange(ModifierType.STATIC, 1, 10), 8));
			addCondition(new ModifierCondition(ItemTier.T2, null, new ModifierRange(ModifierType.STATIC, 1, 12), 12));
			addCondition(new ModifierCondition(ItemTier.T3, null, new ModifierRange(ModifierType.STATIC, 1, 25), 15));
			addCondition(new ModifierCondition(ItemTier.T4, null, new ModifierRange(ModifierType.STATIC, 1, 28), 20));
			addCondition(new ModifierCondition(ItemTier.T5, null, new ModifierRange(ModifierType.STATIC, 1, 35), 15));
		}
		
	}
	
	public class ArmorPenetration extends ItemModifier {

		public ArmorPenetration() {
			super(Arrays.asList(ItemType.AXE), -1, r + "ARMOR PENETRATION: ", "%");
			setOrderPriority(5);
			addCondition(new ModifierCondition(ItemTier.T1, null, new ModifierRange(ModifierType.STATIC, 1, 1), 20));
			addCondition(new ModifierCondition(ItemTier.T2, null, new ModifierRange(ModifierType.STATIC, 1, 3), 20));
			addCondition(new ModifierCondition(ItemTier.T3, null, new ModifierRange(ModifierType.STATIC, 1, 5), 25));
			addCondition(new ModifierCondition(ItemTier.T4, null, new ModifierRange(ModifierType.STATIC, 1, 8), 20));
			addCondition(new ModifierCondition(ItemTier.T5, null, new ModifierRange(ModifierType.STATIC, 1, 10), 15));
		}
		
	}
	
}
