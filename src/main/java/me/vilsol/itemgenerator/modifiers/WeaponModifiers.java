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
			super(Arrays.asList(ItemType.SWORD), 100, r + "DMG: ", null, false);
			
			setOrderPriority(0);
			
			addCondition(new ModifierCondition(ItemTier.T1, ItemRarity.COMMON, new ModifierRange(ModifierType.TRIPLE, 1, 3, 7)));
			addCondition(new ModifierCondition(ItemTier.T1, ItemRarity.UNCOMMON, new ModifierRange(ModifierType.TRIPLE, 8, 10, 14)));
			addCondition(new ModifierCondition(ItemTier.T1, ItemRarity.RARE, new ModifierRange(ModifierType.TRIPLE, 15, 17, 21)));
			addCondition(new ModifierCondition(ItemTier.T1, ItemRarity.UNIQUE, new ModifierRange(ModifierType.TRIPLE, 22, 24, 28)));

			addCondition(new ModifierCondition(ItemTier.T2, ItemRarity.COMMON, new ModifierRange(ModifierType.TRIPLE, 30, 34, 40)));
			addCondition(new ModifierCondition(ItemTier.T2, ItemRarity.UNCOMMON, new ModifierRange(ModifierType.TRIPLE, 41, 45, 51)));
			addCondition(new ModifierCondition(ItemTier.T2, ItemRarity.RARE, new ModifierRange(ModifierType.TRIPLE, 52, 56, 62)));
			addCondition(new ModifierCondition(ItemTier.T2, ItemRarity.UNIQUE, new ModifierRange(ModifierType.TRIPLE, 63, 67, 73)));

			addCondition(new ModifierCondition(ItemTier.T3, ItemRarity.COMMON, new ModifierRange(ModifierType.TRIPLE, 90, 98, 110)));
			addCondition(new ModifierCondition(ItemTier.T3, ItemRarity.UNCOMMON, new ModifierRange(ModifierType.TRIPLE, 111, 119, 131)));
			addCondition(new ModifierCondition(ItemTier.T3, ItemRarity.RARE, new ModifierRange(ModifierType.TRIPLE, 132, 140, 152)));
			addCondition(new ModifierCondition(ItemTier.T3, ItemRarity.UNIQUE, new ModifierRange(ModifierType.TRIPLE, 153, 161, 173)));

			addCondition(new ModifierCondition(ItemTier.T4, ItemRarity.COMMON, new ModifierRange(ModifierType.TRIPLE, 210, 222, 242)));
			addCondition(new ModifierCondition(ItemTier.T4, ItemRarity.UNCOMMON, new ModifierRange(ModifierType.TRIPLE, 243, 255, 275)));
			addCondition(new ModifierCondition(ItemTier.T4, ItemRarity.RARE, new ModifierRange(ModifierType.TRIPLE, 276, 288, 308)));
			addCondition(new ModifierCondition(ItemTier.T4, ItemRarity.UNIQUE, new ModifierRange(ModifierType.TRIPLE, 309, 321, 341)));

			addCondition(new ModifierCondition(ItemTier.T5, ItemRarity.COMMON, new ModifierRange(ModifierType.TRIPLE, 400, 416, 456)));
			addCondition(new ModifierCondition(ItemTier.T5, ItemRarity.UNCOMMON, new ModifierRange(ModifierType.TRIPLE, 457, 473, 513)));
			addCondition(new ModifierCondition(ItemTier.T5, ItemRarity.RARE, new ModifierRange(ModifierType.TRIPLE, 514, 530, 570)));
			addCondition(new ModifierCondition(ItemTier.T5, ItemRarity.UNIQUE, new ModifierRange(ModifierType.TRIPLE, 571, 587, 627)));
		}
		
	}
	
	public class AxeDamage extends ItemModifier {

        public AxeDamage() {
            super(Arrays.asList(ItemType.AXE), 100, r + "DMG: ", null, false);
            
            setOrderPriority(0);
            
            addCondition(new ModifierCondition(ItemTier.T1, ItemRarity.COMMON, new ModifierRange(ModifierType.TRIPLE, 1, 4, 10)));
            addCondition(new ModifierCondition(ItemTier.T1, ItemRarity.UNCOMMON, new ModifierRange(ModifierType.TRIPLE, 11, 14, 20)));
            addCondition(new ModifierCondition(ItemTier.T1, ItemRarity.RARE, new ModifierRange(ModifierType.TRIPLE, 21, 24, 30)));
            addCondition(new ModifierCondition(ItemTier.T1, ItemRarity.UNIQUE, new ModifierRange(ModifierType.TRIPLE, 31, 34, 40)));

            addCondition(new ModifierCondition(ItemTier.T2, ItemRarity.COMMON, new ModifierRange(ModifierType.TRIPLE, 40, 46, 56)));
            addCondition(new ModifierCondition(ItemTier.T2, ItemRarity.UNCOMMON, new ModifierRange(ModifierType.TRIPLE, 57, 63, 73)));
            addCondition(new ModifierCondition(ItemTier.T2, ItemRarity.RARE, new ModifierRange(ModifierType.TRIPLE, 74, 80, 90)));
            addCondition(new ModifierCondition(ItemTier.T2, ItemRarity.UNIQUE, new ModifierRange(ModifierType.TRIPLE, 91, 97, 107)));

            addCondition(new ModifierCondition(ItemTier.T3, ItemRarity.COMMON, new ModifierRange(ModifierType.TRIPLE, 115, 124, 144)));
            addCondition(new ModifierCondition(ItemTier.T3, ItemRarity.UNCOMMON, new ModifierRange(ModifierType.TRIPLE, 145, 154, 174)));
            addCondition(new ModifierCondition(ItemTier.T3, ItemRarity.RARE, new ModifierRange(ModifierType.TRIPLE, 175, 184, 204)));
            addCondition(new ModifierCondition(ItemTier.T3, ItemRarity.UNIQUE, new ModifierRange(ModifierType.TRIPLE, 205, 214, 234)));

            addCondition(new ModifierCondition(ItemTier.T4, ItemRarity.COMMON, new ModifierRange(ModifierType.TRIPLE, 240, 252, 292)));
            addCondition(new ModifierCondition(ItemTier.T4, ItemRarity.UNCOMMON, new ModifierRange(ModifierType.TRIPLE, 293, 305, 345)));
            addCondition(new ModifierCondition(ItemTier.T4, ItemRarity.RARE, new ModifierRange(ModifierType.TRIPLE, 346, 358, 398)));
            addCondition(new ModifierCondition(ItemTier.T4, ItemRarity.UNIQUE, new ModifierRange(ModifierType.TRIPLE, 399, 411, 451)));

            addCondition(new ModifierCondition(ItemTier.T5, ItemRarity.COMMON, new ModifierRange(ModifierType.TRIPLE, 470, 486, 536)));
            addCondition(new ModifierCondition(ItemTier.T5, ItemRarity.UNCOMMON, new ModifierRange(ModifierType.TRIPLE, 537, 553, 603)));
            addCondition(new ModifierCondition(ItemTier.T5, ItemRarity.RARE, new ModifierRange(ModifierType.TRIPLE, 604, 620, 670)));
            addCondition(new ModifierCondition(ItemTier.T5, ItemRarity.UNIQUE, new ModifierRange(ModifierType.TRIPLE, 671, 687, 737)));
        }
        
    }
	
	public class StaffDamage extends ItemModifier {

        public StaffDamage() {
            super(Arrays.asList(ItemType.STAFF), 100, r + "DMG: ", null, false);
            
            setOrderPriority(0);
            
            addCondition(new ModifierCondition(ItemTier.T1, ItemRarity.COMMON, new ModifierRange(ModifierType.TRIPLE, 1, 2, 5)));
            addCondition(new ModifierCondition(ItemTier.T1, ItemRarity.UNCOMMON, new ModifierRange(ModifierType.TRIPLE, 6, 7, 10)));
            addCondition(new ModifierCondition(ItemTier.T1, ItemRarity.RARE, new ModifierRange(ModifierType.TRIPLE, 11, 12, 15)));
            addCondition(new ModifierCondition(ItemTier.T1, ItemRarity.UNIQUE, new ModifierRange(ModifierType.TRIPLE, 16, 17, 20)));

            addCondition(new ModifierCondition(ItemTier.T2, ItemRarity.COMMON, new ModifierRange(ModifierType.TRIPLE, 25, 27, 33)));
            addCondition(new ModifierCondition(ItemTier.T2, ItemRarity.UNCOMMON, new ModifierRange(ModifierType.TRIPLE, 34, 36, 42)));
            addCondition(new ModifierCondition(ItemTier.T2, ItemRarity.RARE, new ModifierRange(ModifierType.TRIPLE, 43, 45, 51)));
            addCondition(new ModifierCondition(ItemTier.T2, ItemRarity.UNIQUE, new ModifierRange(ModifierType.TRIPLE, 52, 54, 60)));

            addCondition(new ModifierCondition(ItemTier.T3, ItemRarity.COMMON, new ModifierRange(ModifierType.TRIPLE, 80, 84, 96)));
            addCondition(new ModifierCondition(ItemTier.T3, ItemRarity.UNCOMMON, new ModifierRange(ModifierType.TRIPLE, 97, 99, 105)));
            addCondition(new ModifierCondition(ItemTier.T3, ItemRarity.RARE, new ModifierRange(ModifierType.TRIPLE, 106, 108, 114)));
            addCondition(new ModifierCondition(ItemTier.T3, ItemRarity.UNIQUE, new ModifierRange(ModifierType.TRIPLE, 115, 117, 123)));

            addCondition(new ModifierCondition(ItemTier.T4, ItemRarity.COMMON, new ModifierRange(ModifierType.TRIPLE, 140, 148, 172)));
            addCondition(new ModifierCondition(ItemTier.T4, ItemRarity.UNCOMMON, new ModifierRange(ModifierType.TRIPLE, 173, 181, 205)));
            addCondition(new ModifierCondition(ItemTier.T4, ItemRarity.RARE, new ModifierRange(ModifierType.TRIPLE, 206, 214, 238)));
            addCondition(new ModifierCondition(ItemTier.T4, ItemRarity.UNIQUE, new ModifierRange(ModifierType.TRIPLE, 239, 247, 271)));

            addCondition(new ModifierCondition(ItemTier.T5, ItemRarity.COMMON, new ModifierRange(ModifierType.TRIPLE, 300, 316, 346)));
            addCondition(new ModifierCondition(ItemTier.T5, ItemRarity.UNCOMMON, new ModifierRange(ModifierType.TRIPLE, 347, 363, 393)));
            addCondition(new ModifierCondition(ItemTier.T5, ItemRarity.RARE, new ModifierRange(ModifierType.TRIPLE, 394, 410, 440)));
            addCondition(new ModifierCondition(ItemTier.T5, ItemRarity.UNIQUE, new ModifierRange(ModifierType.TRIPLE, 441, 457, 487)));
        }
        
	}

	public class PolearmDamage extends ItemModifier {

	    public PolearmDamage() {
	        super(Arrays.asList(ItemType.POLEARM), 100, r + "DMG: ", null, false);

	        setOrderPriority(0);

	        addCondition(new ModifierCondition(ItemTier.T1, ItemRarity.COMMON, new ModifierRange(ModifierType.TRIPLE, 1, 3, 7)));
	        addCondition(new ModifierCondition(ItemTier.T1, ItemRarity.UNCOMMON, new ModifierRange(ModifierType.TRIPLE, 8, 10, 14)));
	        addCondition(new ModifierCondition(ItemTier.T1, ItemRarity.RARE, new ModifierRange(ModifierType.TRIPLE, 15, 17, 21)));
	        addCondition(new ModifierCondition(ItemTier.T1, ItemRarity.UNIQUE, new ModifierRange(ModifierType.TRIPLE, 22, 24, 28)));

	        addCondition(new ModifierCondition(ItemTier.T2, ItemRarity.COMMON, new ModifierRange(ModifierType.TRIPLE, 35, 39, 46)));
	        addCondition(new ModifierCondition(ItemTier.T2, ItemRarity.UNCOMMON, new ModifierRange(ModifierType.TRIPLE, 47, 51, 58)));
	        addCondition(new ModifierCondition(ItemTier.T2, ItemRarity.RARE, new ModifierRange(ModifierType.TRIPLE, 59, 63, 70)));
	        addCondition(new ModifierCondition(ItemTier.T2, ItemRarity.UNIQUE, new ModifierRange(ModifierType.TRIPLE, 71, 75, 82)));

	        addCondition(new ModifierCondition(ItemTier.T3, ItemRarity.COMMON, new ModifierRange(ModifierType.TRIPLE, 95, 101, 114)));
	        addCondition(new ModifierCondition(ItemTier.T3, ItemRarity.UNCOMMON, new ModifierRange(ModifierType.TRIPLE, 115, 121, 134)));
	        addCondition(new ModifierCondition(ItemTier.T3, ItemRarity.RARE, new ModifierRange(ModifierType.TRIPLE, 135, 141, 154)));
	        addCondition(new ModifierCondition(ItemTier.T3, ItemRarity.UNIQUE, new ModifierRange(ModifierType.TRIPLE, 155, 161, 174)));

	        addCondition(new ModifierCondition(ItemTier.T4, ItemRarity.COMMON, new ModifierRange(ModifierType.TRIPLE, 190, 199, 224)));
	        addCondition(new ModifierCondition(ItemTier.T4, ItemRarity.UNCOMMON, new ModifierRange(ModifierType.TRIPLE, 225, 234, 259)));
	        addCondition(new ModifierCondition(ItemTier.T4, ItemRarity.RARE, new ModifierRange(ModifierType.TRIPLE, 260, 269, 294)));
	        addCondition(new ModifierCondition(ItemTier.T4, ItemRarity.UNIQUE, new ModifierRange(ModifierType.TRIPLE, 295, 304, 329)));

	        addCondition(new ModifierCondition(ItemTier.T5, ItemRarity.COMMON, new ModifierRange(ModifierType.TRIPLE, 350, 365, 400)));
	        addCondition(new ModifierCondition(ItemTier.T5, ItemRarity.UNCOMMON, new ModifierRange(ModifierType.TRIPLE, 401, 416, 451)));
	        addCondition(new ModifierCondition(ItemTier.T5, ItemRarity.RARE, new ModifierRange(ModifierType.TRIPLE, 452, 467, 502)));
	        addCondition(new ModifierCondition(ItemTier.T5, ItemRarity.UNIQUE, new ModifierRange(ModifierType.TRIPLE, 503, 518, 553)));
	    }

	}

	public class BowDamage extends ItemModifier {

	    public BowDamage() {
	        super(Arrays.asList(ItemType.BOW), 100, r + "DMG: ", null, false);

	        setOrderPriority(0);

	        addCondition(new ModifierCondition(ItemTier.T1, ItemRarity.COMMON, new ModifierRange(ModifierType.TRIPLE, 1, 8, 17)));
	        addCondition(new ModifierCondition(ItemTier.T1, ItemRarity.UNCOMMON, new ModifierRange(ModifierType.TRIPLE, 18, 25, 34)));
	        addCondition(new ModifierCondition(ItemTier.T1, ItemRarity.RARE, new ModifierRange(ModifierType.TRIPLE, 35, 42, 51)));
	        addCondition(new ModifierCondition(ItemTier.T1, ItemRarity.UNIQUE, new ModifierRange(ModifierType.TRIPLE, 52, 59, 68)));

	        addCondition(new ModifierCondition(ItemTier.T2, ItemRarity.COMMON, new ModifierRange(ModifierType.TRIPLE, 78, 92, 108)));
	        addCondition(new ModifierCondition(ItemTier.T2, ItemRarity.UNCOMMON, new ModifierRange(ModifierType.TRIPLE, 109, 123, 138)));
	        addCondition(new ModifierCondition(ItemTier.T2, ItemRarity.RARE, new ModifierRange(ModifierType.TRIPLE, 140, 154, 170)));
	        addCondition(new ModifierCondition(ItemTier.T2, ItemRarity.UNIQUE, new ModifierRange(ModifierType.TRIPLE, 171, 185, 201)));

	        addCondition(new ModifierCondition(ItemTier.T3, ItemRarity.COMMON, new ModifierRange(ModifierType.TRIPLE, 220, 240, 268)));
	        addCondition(new ModifierCondition(ItemTier.T3, ItemRarity.UNCOMMON, new ModifierRange(ModifierType.TRIPLE, 269, 289, 317)));
	        addCondition(new ModifierCondition(ItemTier.T3, ItemRarity.RARE, new ModifierRange(ModifierType.TRIPLE, 318, 338, 366)));
	        addCondition(new ModifierCondition(ItemTier.T3, ItemRarity.UNIQUE, new ModifierRange(ModifierType.TRIPLE, 367, 387, 415)));

	        addCondition(new ModifierCondition(ItemTier.T4, ItemRarity.COMMON, new ModifierRange(ModifierType.TRIPLE, 440, 468, 516)));
	        addCondition(new ModifierCondition(ItemTier.T4, ItemRarity.UNCOMMON, new ModifierRange(ModifierType.TRIPLE, 517, 545, 593)));
	        addCondition(new ModifierCondition(ItemTier.T4, ItemRarity.RARE, new ModifierRange(ModifierType.TRIPLE, 594, 622, 670)));
	        addCondition(new ModifierCondition(ItemTier.T4, ItemRarity.UNIQUE, new ModifierRange(ModifierType.TRIPLE, 671, 699, 747)));

	        addCondition(new ModifierCondition(ItemTier.T5, ItemRarity.COMMON, new ModifierRange(ModifierType.TRIPLE, 790, 826, 922)));
	        addCondition(new ModifierCondition(ItemTier.T5, ItemRarity.UNCOMMON, new ModifierRange(ModifierType.TRIPLE, 923, 959, 1055)));
	        addCondition(new ModifierCondition(ItemTier.T5, ItemRarity.RARE, new ModifierRange(ModifierType.TRIPLE, 1056, 1092, 1188)));
	        addCondition(new ModifierCondition(ItemTier.T5, ItemRarity.UNIQUE, new ModifierRange(ModifierType.TRIPLE, 1189, 1225, 1321)));
	    }

	}
	
	/* disabled as of patch 1.9 by Mayley's request
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
	*/
	
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
			super(Arrays.asList(ItemType.SWORD), -1, r + "ACCURACY: ", "%");
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
