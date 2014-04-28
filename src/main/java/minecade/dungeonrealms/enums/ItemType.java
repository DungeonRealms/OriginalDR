package minecade.dungeonrealms.enums;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import minecade.dungeonrealms.EnchantMechanics.EnchantMechanics;
import minecade.dungeonrealms.HealthMechanics.HealthMechanics;
import minecade.dungeonrealms.ItemMechanics.ItemMechanics;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public enum ItemType {
	
	STAFF(Material.WOOD_HOE, "", Material.STONE_HOE, "", Material.IRON_HOE, "", Material.DIAMOND_HOE, "", Material.GOLD_HOE, ""),
	AXE(Material.WOOD_AXE, "", Material.STONE_AXE, "", Material.IRON_AXE, "", Material.DIAMOND_AXE, "", Material.GOLD_AXE, ""),
	SWORD(Material.WOOD_SWORD, "", Material.STONE_SWORD, "", Material.IRON_SWORD, "", Material.DIAMOND_SWORD, "", Material.GOLD_SWORD, ""),
	POLEARM(Material.WOOD_SPADE, "", Material.STONE_SPADE, "", Material.IRON_SPADE, "", Material.DIAMOND_SPADE, "", Material.GOLD_SPADE, ""),
	BOW(Material.BOW, "Shortbow", Material.BOW, "", Material.BOW, "", Material.BOW, "", Material.BOW, ""),
	
	HELMET(Material.LEATHER_HELMET, "Leather Coif", Material.CHAINMAIL_HELMET, "Medium Helmet", Material.IRON_HELMET, "Full Helmet", Material.DIAMOND_HELMET, "Ancient Full Helmet", Material.GOLD_HELMET, "Legendary Full Helmet"),
	CHESTPLATE(Material.LEATHER_CHESTPLATE, "Leather Chestplate", Material.CHAINMAIL_CHESTPLATE, "Chainmail", Material.IRON_CHESTPLATE, "Platemail", Material.DIAMOND_CHESTPLATE, "Magic Platemail", Material.GOLD_CHESTPLATE, "Legendary Platemail"),
	LEGGINGS(Material.LEATHER_LEGGINGS, "Leather Leggings", Material.CHAINMAIL_LEGGINGS, "Chainmail Leggings", Material.IRON_LEGGINGS, "Platemail Leggings", Material.DIAMOND_LEGGINGS, "Magic Platemail Leggings", Material.GOLD_LEGGINGS, "Legendary Platemail Leggings"),
	BOOTS(Material.LEATHER_BOOTS, "Leather Boots", Material.CHAINMAIL_BOOTS, "Chainmail Boots", Material.IRON_BOOTS, "Platemail Boots", Material.DIAMOND_BOOTS, "Magic Platemail Boots", Material.GOLD_BOOTS, "Legendary Platemail Boots");
	
	private Material t1;
	private String t1Name;
	
	private Material t2;
	private String t2Name;
	
	private Material t3;
	private String t3Name;
	
	private Material t4;
	private String t4Name;
	
	private Material t5;
	private String t5Name;
	
	private ItemType(Material t1, String t1Name, Material t2, String t2Name, Material t3, String t3Name, Material t4, String t4Name, Material t5, String t5Name) {
		this.t1 = t1;
		this.t1Name = t1Name;
		
		this.t2 = t2;
		this.t2Name = t2Name;
		
		this.t3 = t3;
		this.t3Name = t3Name;
		
		this.t4 = t4;
		this.t4Name = t4Name;
		
		this.t5 = t5;
		this.t5Name = t5Name;
	}
	
	public static ItemType getTypeFromMaterial(Material m) {
		for(ItemType i : values()) {
			if(i.t1 == m) return i;
			if(i.t2 == m) return i;
			if(i.t3 == m) return i;
			if(i.t4 == m) return i;
			if(i.t5 == m) return i;
		}
		return null;
	}
	
	public static boolean isWeapon(ItemType t) {
		if(t != STAFF && t != AXE && t != SWORD && t != POLEARM && t != BOW) return false;
		return true;
	}
	
	public static boolean isArmor(ItemType t) {
		if(t != HELMET && t != CHESTPLATE && t != LEGGINGS && t != BOOTS) return false;
		return true;
	}
	
	public ItemStack generateWeapon(ItemTier tier, ItemStack original) {
		ItemType type = getTypeFromMaterial(this.t1);
		if(!isWeapon(type)) return null;
		
		return null;
	}
	
	public ItemStack generateArmor(ItemTier tier, ItemStack original, int moblevel) {
		ItemType type = getTypeFromMaterial(this.t1);
		if(!isArmor(type)) return null;
		Random r = new Random();
		Material m = null;
		boolean hp_regen = false, /* hp_increase = false, */energy_regen = false, block = false, dodge = false, thorns = false, reflection = false, gem_find = false, item_find = false;
		int hp_regen_percent = 0;
		int hp_increase_amount = 0;
		int energy_regen_percent = 0;
		int block_percent = 0;
		int dodge_percent = 0;
		int thorns_percent = 0;
		int reflection_percent = 0;
		int gem_find_percent = 0;
		int item_find_percent = 0;
		
		//int hp_regen_chance = 0;
		//int hp_increase_chance = 0;
		//int energy_regen_chance = 0;
		int block_chance = 0;
		int dodge_chance = 0;
		int thorns_chance = 0;
		int reflection_chance = 0;
		int gem_find_chance = 0;
		int item_find_chance = 0;
		
		int hp_regen_min = 0;
		//int hp_increase_min = 0;
		int energy_regen_min = 0;
		int block_min = 0;
		int dodge_min = 0;
		int thorns_min = 0;
		int reflection_min = 0;
		int gem_find_min = 0;
		int item_find_min = 0;
		
		int hp_regen_max = 0;
		//int hp_increase_max = 0;
		int energy_regen_max = 0;
		int block_max = 0;
		int dodge_max = 0;
		int thorns_max = 0;
		int reflection_max = 0;
		int gem_find_max = 0;
		int item_find_max = 0;
		
		//int element_dmg_amount = 0;
		//String element_dmg_type = "";
		
		String armor_name = "";
		String armor_description = "";
		
		ChatColor tag = ChatColor.WHITE;
		
		if(tier == ItemTier.T1) {
			armor_name = type.t1Name;
			tag = ChatColor.WHITE;
			m = this.t1;
			
			//hp_regen_chance = 10;
			//energy_regen_chance = 10;
			block_chance = 5;
			dodge_chance = 5;
			thorns_chance = 3;
			reflection_chance = 3;
			gem_find_chance = 5;
			item_find_chance = 5;
			
			hp_regen_min = 5;
			energy_regen_min = 1;
			if(type != HELMET){
				block_min = 1;
				dodge_min = 1;
				thorns_min = 1; // 0.1%
				reflection_min = 1; // 0.1%
				gem_find_min = 1; // 0.1%
				item_find_min = 1; // 0.1%
			}
				
			hp_regen_max = 15;
			energy_regen_max = 5;
			block_max = 5;
			dodge_max = 5;
			thorns_max = 2;
			reflection_max = 1;
			gem_find_max = 5;
			item_find_max = 1;
			
		}
		if(tier == ItemTier.T2) {
			armor_name = this.t2Name;
			tag = ChatColor.GREEN;
			m = this.t2;
			
			//hp_regen_chance = 20;
			//energy_regen_chance = 15;
			block_chance = 9;
			dodge_chance = 9;
			thorns_chance = 5;
			reflection_chance = 5;
			gem_find_chance = 5;
			item_find_chance = 5;
			
			hp_regen_min = 10;
			energy_regen_min = 3;
			if(type != HELMET){
				block_min = 1;
				dodge_min = 1;
				thorns_min = 1; 
				reflection_min = 1; 
				gem_find_min = 1; 
				item_find_min = 1; // 0.1%
			}
			
			hp_regen_max = 25;
			energy_regen_max = 7;
			block_max = 8;
			dodge_max = 8;
			thorns_max = 3;
			reflection_max = 2;
			gem_find_max = 8;
			item_find_max = 2;
		}
		if(tier == ItemTier.T3) {
			armor_name = this.t3Name;
			tag = ChatColor.AQUA;
			m = this.t3;
			
			//hp_regen_chance = 40;
			//energy_regen_chance = 19;
			block_chance = 15;
			dodge_chance = 15;
			thorns_chance = 10;
			reflection_chance = 10;
			gem_find_chance = 5;
			item_find_chance = 5;
			
			hp_regen_min = 35;
			energy_regen_min = 5;
			if(type != HELMET){
				block_min = 1;
				dodge_min = 1;
				thorns_min = 1; 
				reflection_min = 1; 
				gem_find_min = 3; 
				item_find_min = 1; // 0.1%
			}
			
			hp_regen_max = 55;
			energy_regen_max = 9;
			block_max = 10;
			dodge_max = 10;
			thorns_max = 5;
			reflection_max = 4;
			gem_find_max = 15;
			item_find_max = 3;
		}
		if(tier == ItemTier.T4) {
			armor_name = this.t4Name;
			tag = ChatColor.LIGHT_PURPLE;
			m = this.t4;
			
			//hp_regen_chance = 55;
			//energy_regen_chance = 24;
			block_chance = 25;
			dodge_chance = 25;
			thorns_chance = 13;
			reflection_chance = 13;
			gem_find_chance = 5;
			item_find_chance = 5;
			
			hp_regen_min = 60;
			energy_regen_min = 7;
			if(type != HELMET){
				block_min = 1;
				dodge_min = 1;
				thorns_min = 2; 
				reflection_min = 2; 
				gem_find_min = 5; 
				item_find_min = 1; // 0.1%
			}
			
			hp_regen_max = 75;
			energy_regen_max = 12;
			block_max = 12;
			dodge_max = 12;
			thorns_max = 9;
			reflection_max = 5;
			gem_find_max = 20;
			item_find_max = 4;
		}
		if(tier == ItemTier.T5) {
			armor_name = this.t5Name;
			tag = ChatColor.YELLOW;
			m = this.t5;
			
			//hp_regen_chance = 70;
			//energy_regen_chance = 30;
			block_chance = 30;
			dodge_chance = 30;
			thorns_chance = 20;
			reflection_chance = 15;
			gem_find_chance = 5;
			item_find_chance = 5;
			
			hp_regen_min = 80;
			energy_regen_min = 7;
			if(type != HELMET){
				block_min = 1;
				dodge_min = 1;
				thorns_min = 2; 
				reflection_min = 2; 
				gem_find_min = 5; 
				item_find_min = 1; // 0.1%
			}
			
			hp_regen_max = 120;
			energy_regen_max = 12;
			block_max = 12;
			dodge_max = 12;
			thorns_max = 9;
			reflection_max = 5;
			gem_find_max = 20;
			item_find_max = 4;
		}
		
		int armor_range_check = new Random().nextInt(100);
		int min_armor = 0;
		int max_armor = 0;
		String rarity = "";
		if(armor_range_check <= 80 && (moblevel != -1) ? moblevel >= 1 : true) {
			rarity = ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + "Common";
			if(tier == ItemTier.T1) {
				//int min_num = 1 + 1;
				//int max_num = 2 + 1;
				
				min_armor = 1;
				max_armor = 1;
				
				int min_hp = 10;
				int max_hp = 20;
				
				hp_increase_amount = r.nextInt(max_hp - min_hp) + min_hp;
			}
			if(tier == ItemTier.T2) {
				int min_num = 1 + 1;
				int max_num = 2 + 1;
				
				min_armor = r.nextInt(max_num - min_num) + (min_num - 1);
				max_armor = r.nextInt(max_num - min_armor) + min_armor;
				
				int min_hp = 60;
				int max_hp = 80;
				
				hp_increase_amount = r.nextInt(max_hp - min_hp) + min_hp;
			}
			if(tier == ItemTier.T3) {
				int min_num = 5 + 1;
				int max_num = 6 + 1;
				
				min_armor = r.nextInt(max_num - min_num) + (min_num - 1);
				max_armor = r.nextInt(max_num - min_armor) + min_armor;
				
				int min_hp = 200;
				int max_hp = 350;
				
				hp_increase_amount = r.nextInt(max_hp - min_hp) + min_hp;
			}
			if(tier == ItemTier.T4) {
				int min_num = 8 + 1;
				int max_num = 9 + 1;
				
				min_armor = r.nextInt(max_num - min_num) + (min_num - 1);
				max_armor = r.nextInt(max_num - min_armor) + min_armor;
				
				int min_hp = 600;
				int max_hp = 800;
				
				hp_increase_amount = r.nextInt(max_hp - min_hp) + min_hp;
			}
			if(tier == ItemTier.T5) {
				int min_num = 11 + 1;
				int max_num = 12 + 1;
				
				min_armor = r.nextInt(max_num - min_num) + (min_num - 1);
				max_armor = r.nextInt(max_num - min_armor) + min_armor;
				
				int min_hp = 1500;
				int max_hp = 2500;
				
				hp_increase_amount = r.nextInt(max_hp - min_hp) + min_hp;
			}
		}
		
		if(armor_range_check > 80 && armor_range_check < 95 && (moblevel != -1) ? moblevel >= 2 : true) {
			rarity = ChatColor.GREEN.toString() + ChatColor.ITALIC.toString() + "Uncommon";
			if(tier == ItemTier.T1) {
				int min_num = 1 + 1;
				int max_num = 2 + 1;
				
				min_armor = r.nextInt(max_num - min_num) + (min_num - 1);
				max_armor = r.nextInt(max_num - min_armor) + min_armor;
				
				int min_hp = 20;
				int max_hp = 50;
				
				hp_increase_amount = r.nextInt(max_hp - min_hp) + min_hp;
			}
			if(tier == ItemTier.T2) {
				int min_num = 3 + 1;
				int max_num = 4 + 1;
				
				min_armor = r.nextInt(max_num - min_num) + (min_num - 1);
				max_armor = r.nextInt(max_num - min_armor) + min_armor;
				
				int min_hp = 100;
				int max_hp = 250;
				
				hp_increase_amount = r.nextInt(max_hp - min_hp) + min_hp;
			}
			if(tier == ItemTier.T3) {
				int min_num = 6 + 1;
				int max_num = 9 + 1;
				
				min_armor = r.nextInt(max_num - min_num) + (min_num - 1);
				max_armor = r.nextInt(max_num - min_armor) + min_armor;
				
				int min_hp = 350;
				int max_hp = 600;
				
				hp_increase_amount = r.nextInt(max_hp - min_hp) + min_hp;
			}
			if(tier == ItemTier.T4) {
				int min_num = 10 + 1;
				int max_num = 11 + 1;
				
				min_armor = r.nextInt(max_num - min_num) + (min_num - 1);
				max_armor = r.nextInt(max_num - min_armor) + min_armor;
				
				int min_hp = 800;
				int max_hp = 1300;
				
				hp_increase_amount = r.nextInt(max_hp - min_hp) + min_hp;
			}
			if(tier == ItemTier.T5) {
				int min_num = 13 + 1;
				int max_num = 14 + 1;
				
				min_armor = r.nextInt(max_num - min_num) + (min_num - 1);
				max_armor = r.nextInt(max_num - min_armor) + min_armor;
				
				int min_hp = 2500;
				int max_hp = 3600;
				
				hp_increase_amount = r.nextInt(max_hp - min_hp) + min_hp;
			}
		}
		
		if(armor_range_check >= 95 && armor_range_check != 99 && (moblevel != -1) ? moblevel >= 3 : true) {
			rarity = ChatColor.AQUA.toString() + ChatColor.ITALIC.toString() + "Rare";
			if(tier == ItemTier.T1) {
				int min_num = 1 + 1;
				int max_num = 2 + 1;
				
				min_armor = r.nextInt(max_num - min_num) + (min_num - 1);
				max_armor = r.nextInt(max_num - min_armor) + min_armor;
				
				int min_hp = 80;
				int max_hp = 120;
				
				hp_increase_amount = r.nextInt(max_hp - min_hp) + min_hp;
			}
			if(tier == ItemTier.T2) {
				int min_num = 5 + 1;
				int max_num = 6 + 1;
				
				min_armor = r.nextInt(max_num - min_num) + (min_num - 1);
				max_armor = r.nextInt(max_num - min_armor) + min_armor;
				
				int min_hp = 250;
				int max_hp = 350;
				
				hp_increase_amount = r.nextInt(max_hp - min_hp) + min_hp;
			}
			if(tier == ItemTier.T3) {
				int min_num = 8 + 1;
				int max_num = 9 + 1;
				
				min_armor = r.nextInt(max_num - min_num) + (min_num - 1);
				max_armor = r.nextInt(max_num - min_armor) + min_armor;
				
				int min_hp = 600;
				int max_hp = 800;
				
				hp_increase_amount = r.nextInt(max_hp - min_hp) + min_hp;
			}
			if(tier == ItemTier.T4) {
				int min_num = 11 + 1;
				int max_num = 12 + 1;
				
				min_armor = r.nextInt(max_num - min_num) + (min_num - 1);
				max_armor = r.nextInt(max_num - min_armor) + min_armor;
				
				int min_hp = 1300;
				int max_hp = 2400;
				
				hp_increase_amount = r.nextInt(max_hp - min_hp) + min_hp;
			}
			if(tier == ItemTier.T5) {
				int min_num = 16 + 1;
				int max_num = 17 + 1;
				
				min_armor = r.nextInt(max_num - min_num) + (min_num - 1);
				max_armor = r.nextInt(max_num - min_armor) + min_armor;
				
				int min_hp = 3600;
				int max_hp = 5400;
				
				hp_increase_amount = r.nextInt(max_hp - min_hp) + min_hp;
			}
		}
		
		if(armor_range_check == 99 && (moblevel != -1) ? moblevel >= 4 : true) {
			rarity = ChatColor.YELLOW.toString() + ChatColor.ITALIC.toString() + "Unique";
			if(tier == ItemTier.T1) {
				int min_num = 1 + 1;
				int max_num = 2 + 1;
				
				min_armor = r.nextInt(max_num - min_num) + (min_num - 1);
				max_armor = r.nextInt(max_num - min_armor) + min_armor;
				
				int min_hp = 80;
				int max_hp = 120;
				
				hp_increase_amount = r.nextInt(max_hp - min_hp) + min_hp;
			}
			if(tier == ItemTier.T2) {
				int min_num = 5 + 1;
				int max_num = 6 + 1;
				
				min_armor = r.nextInt(max_num - min_num) + (min_num - 1);
				max_armor = r.nextInt(max_num - min_armor) + min_armor;
				
				int min_hp = 250;
				int max_hp = 350;
				
				hp_increase_amount = r.nextInt(max_hp - min_hp) + min_hp;
			}
			if(tier == ItemTier.T3) {
				int min_num = 8 + 1;
				int max_num = 9 + 1;
				
				min_armor = r.nextInt(max_num - min_num) + (min_num - 1);
				max_armor = r.nextInt(max_num - min_armor) + min_armor;
				
				int min_hp = 600;
				int max_hp = 800;
				
				hp_increase_amount = r.nextInt(max_hp - min_hp) + min_hp;
			}
			if(tier == ItemTier.T4) {
				int min_num = 11 + 1;
				int max_num = 12 + 1;
				
				min_armor = r.nextInt(max_num - min_num) + (min_num - 1);
				max_armor = r.nextInt(max_num - min_armor) + min_armor;
				
				int min_hp = 1300;
				int max_hp = 2400;
				
				hp_increase_amount = r.nextInt(max_hp - min_hp) + min_hp;
			}
			if(tier == ItemTier.T5) {
				int min_num = 16 + 1;
				int max_num = 17 + 1;
				
				min_armor = r.nextInt(max_num - min_num) + (min_num - 1);
				max_armor = r.nextInt(max_num - min_armor) + min_armor;
				
				int min_hp = 3600;
				int max_hp = 5400;
				
				hp_increase_amount = r.nextInt(max_hp - min_hp) + min_hp;
			}
			
			min_armor *= 1.1;
			max_armor *= 1.1;
			hp_increase_amount *= 1.1;
		}
		
		if(moblevel != -1){
			if(rarity == "") {
	            rarity = ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + "Common";
	            if(tier == ItemTier.T1) {
	                //int min_num = 1 + 1;
	                //int max_num = 2 + 1;
	                
	                min_armor = 1;
	                max_armor = 1;
	                
	                int min_hp = 10;
	                int max_hp = 20;
	                
	                hp_increase_amount = r.nextInt(max_hp - min_hp) + min_hp;
	            }
	            if(tier == ItemTier.T2) {
	                int min_num = 1 + 1;
	                int max_num = 2 + 1;
	                
	                min_armor = r.nextInt(max_num - min_num) + (min_num - 1);
	                max_armor = r.nextInt(max_num - min_armor) + min_armor;
	                
	                int min_hp = 60;
	                int max_hp = 80;
	                
	                hp_increase_amount = r.nextInt(max_hp - min_hp) + min_hp;
	            }
	            if(tier == ItemTier.T3) {
	                int min_num = 5 + 1;
	                int max_num = 6 + 1;
	                
	                min_armor = r.nextInt(max_num - min_num) + (min_num - 1);
	                max_armor = r.nextInt(max_num - min_armor) + min_armor;
	                
	                int min_hp = 200;
	                int max_hp = 350;
	                
	                hp_increase_amount = r.nextInt(max_hp - min_hp) + min_hp;
	            }
	            if(tier == ItemTier.T4) {
	                int min_num = 8 + 1;
	                int max_num = 9 + 1;
	                
	                min_armor = r.nextInt(max_num - min_num) + (min_num - 1);
	                max_armor = r.nextInt(max_num - min_armor) + min_armor;
	                
	                int min_hp = 600;
	                int max_hp = 800;
	                
	                hp_increase_amount = r.nextInt(max_hp - min_hp) + min_hp;
	            }
	            if(tier == ItemTier.T5) {
	                int min_num = 11 + 1;
	                int max_num = 12 + 1;
	                
	                min_armor = r.nextInt(max_num - min_num) + (min_num - 1);
	                max_armor = r.nextInt(max_num - min_armor) + min_armor;
	                
	                int min_hp = 1500;
	                int max_hp = 2500;
	                
	                hp_increase_amount = r.nextInt(max_hp - min_hp) + min_hp;
	            }
	        }
		}
		
		double temp_min_armor = (double) min_armor;
		double temp_max_armor = (double) max_armor;
		
		double temp_hp_increase_amount = (double) hp_increase_amount;
		
		min_armor = (int) (temp_min_armor * 0.50); // 50% of the values of chestplate / leggings.
		max_armor = (int) (temp_max_armor * 0.50); // 50% of the values of chestplate / leggings.
		
		hp_increase_amount = (int) (temp_hp_increase_amount * 0.50); // 50% of HP increase value.
		
		if(min_armor == 0) {
			min_armor = 1;
		}
		
		if(max_armor == 0) {
			min_armor = 1;
			max_armor = 1;
		}
		
		int regen_type = r.nextInt(2); // 0, 1.
		
		if(regen_type == 0) { // HP/s HP REGEN
			hp_regen = true;
			//hp_regen_max = (int) Math.round((hp_increase_amount / 10) + (hp_increase_amount * 0.05));
			//if(hp_regen_max < 1){hp_regen_max = 1;}
			double regen_val = hp_increase_amount * 0.05;
			if(regen_val < 1) {
				regen_val = 1;
			}
			
			int real_hp_regen_max = (int) ((regen_val) + (hp_increase_amount / 10)) - r.nextInt((int) Math.round(regen_val));
			if(real_hp_regen_max < 1) {
				real_hp_regen_max = 1;
			}
			//hp_regen_percent = r.nextInt(real_hp_regen_max) + 1;
			hp_regen_percent = r.nextInt(hp_regen_max - hp_regen_min) + hp_regen_min;
		} else if(regen_type == 1) {
			energy_regen = true;
			energy_regen_percent = r.nextInt(energy_regen_max - energy_regen_min) + energy_regen_min;
			energy_regen_percent = energy_regen_percent / 2;
			
			if(energy_regen_percent < 1) {
				energy_regen_percent = 1;
			}
		}
		
		if(r.nextInt(100) <= block_chance) {
			block = true;
			block_percent = r.nextInt(block_max - block_min) + ((block_min > 0) ? block_min : 1); // 1 - 10%
		}
		
		if(r.nextInt(100) <= dodge_chance) { // 1% chance.
			dodge = true;
			dodge_percent = r.nextInt(dodge_max - dodge_min) + ((dodge_min > 0) ? dodge_min : 1); // 1 - 3%
		}
		
		if(r.nextInt(100) <= thorns_chance) { // 1% chance.
			thorns = true;
			thorns_percent = r.nextInt(thorns_max - thorns_min) + ((thorns_min > 0) ? thorns_min : 1); // 1 - 3%
		}
		
		if(r.nextInt(100) <= reflection_chance) { // 1% chance.
			reflection = true;
			reflection_percent = r.nextInt(reflection_max - reflection_min) + ((reflection_min > 0) ? reflection_min : 1); // 1 - 3%
		}
		
		if(r.nextInt(100) <= gem_find_chance) { // 1% chance.
		
			gem_find = true;
			gem_find_percent = r.nextInt(gem_find_max - gem_find_min) + ((gem_find_min > 0) ? gem_find_min : 1); // 1 - 3%
		}
		
		if(r.nextInt(100) <= item_find_chance) { // 1% chance.
			item_find = true;
			item_find_percent = r.nextInt(item_find_max - item_find_min) + ((item_find_min > 0) ? item_find_min : 1); // 1 - 3%
		}
		
		// x = durability
		
		int armor_type = new Random().nextInt(2); // 0 or 1.
		
		@SuppressWarnings("unused")
		String armor_data = "";
		int enchant_count = 0;
		if(original != null) {
			armor_type = 2; // Ignore the normal check.
			rarity = ItemMechanics.getItemRarity(original);
			enchant_count = EnchantMechanics.getEnchantCount(original);
			List<Integer> vals = new ArrayList<Integer>();
			if(ItemMechanics.getDmgVal(original, false) != null) {
				vals = ItemMechanics.getDmgVal(original, false);
				min_armor = vals.get(0);
				max_armor = vals.get(1);
				armor_data = m.name().toUpperCase() + "#" + min_armor + "-" + max_armor + ":";
				armor_description += ChatColor.RED.toString() + "DPS: " + min_armor + " - " + max_armor + "%,";
			}
			if(ItemMechanics.getArmorVal(original, false) != null) {
				vals = ItemMechanics.getArmorVal(original, false);
				min_armor = vals.get(0);
				max_armor = vals.get(1);
				armor_data = m.name().toUpperCase() + "#" + min_armor + "-" + max_armor + ":";
				armor_description += ChatColor.RED.toString() + "ARMOR: " + min_armor + " - " + max_armor + "%,";
			}
			
			hp_increase_amount = HealthMechanics.getHealthVal(original);
			String oarmor_data = ItemMechanics.getArmorData(original);
			if(oarmor_data.contains("hp_regen")) {
				int health_regen_val = Integer.parseInt(oarmor_data.substring(oarmor_data.indexOf("hp_regen=") + 9, oarmor_data.indexOf("@hp_regen_split@")));
				hp_regen = true;
				energy_regen = false;
				hp_regen_percent = health_regen_val;
			}
			if(oarmor_data.contains("energy_regen")) {
				int energy_regen_val = Integer.parseInt(oarmor_data.substring(oarmor_data.indexOf("energy_regen=") + 13, oarmor_data.indexOf("@energy_regen_split@")));
				hp_regen = false;
				energy_regen = true;
				energy_regen_percent = energy_regen_val;
			}
		}
		
		if(armor_type == 0) {
			armor_data = m.name().toUpperCase() + "#" + min_armor + "-" + max_armor + ":";
			armor_description += ChatColor.RED.toString() + "ARMOR: " + min_armor + " - " + max_armor + "%,";
		}
		if(armor_type == 1) {
			armor_data = m.name().toUpperCase() + "#" + min_armor + "-" + max_armor + ":";
			armor_description += ChatColor.RED.toString() + "DPS: " + min_armor + " - " + max_armor + "%,";
		}
		
		if(hp_increase_amount <= 0) {
			hp_increase_amount = 1;
		}
		
		armor_data += "hp_increase=" + hp_increase_amount + "@";
		//armor_name = armor_name + " of Fortitude";
		armor_description += ChatColor.RED.toString() + "HP: +" + hp_increase_amount + ",";
		
		if(hp_regen == true) {
			armor_data += "hp_regen=" + hp_regen_percent + "@hp_regen_split@:";
			armor_description += ChatColor.RED.toString() + "HP REGEN: +" + hp_regen_percent + " HP/s,";
		}
		
		if(energy_regen == true) {
			armor_data += "energy_regen=" + energy_regen_percent + ":";
			armor_name = armor_name + " of Fortitude";
			armor_description += ChatColor.RED.toString() + "ENERGY REGEN: +" + energy_regen_percent + "%,";
			// Take from first index of energy_regen= to next index of : from that point.
		}
		
		int stat_chance = r.nextInt(100);
		int stat_type = r.nextInt(4); // 0, 1, 2
		String stat_string = "";
		String short_stat_string = "";
		int stat_val = 1;
		if(tier == ItemTier.T1) {
			stat_val = new Random().nextInt(15) + 1;
			if(stat_chance <= 25) {
				stat_chance = r.nextInt(100);
				if(stat_type == 0) {
					stat_string = "strength";
					short_stat_string = "STR";
				}
				if(stat_type == 1) {
					stat_string = "dexterity";
					short_stat_string = "DEX";
				}
				if(stat_type == 2) {
					stat_string = "vitality";
					short_stat_string = "VIT";
				}
				if(stat_type == 3) {
					stat_string = "intellect";
					short_stat_string = "INT";
				}
				
				armor_data += stat_string + "=" + stat_val + ":";
				armor_description += ChatColor.RED.toString() + short_stat_string + ": +" + stat_val + ",";
			}
		}
		if(tier == ItemTier.T2) {
			if(stat_chance <= 20) {
				stat_val = new Random().nextInt(35) + 1;
				stat_chance = r.nextInt(100);
				if(stat_type == 0) {
					stat_string = "strength";
					short_stat_string = "STR";
				}
				if(stat_type == 1) {
					stat_string = "dexterity";
					short_stat_string = "DEX";
				}
				if(stat_type == 2) {
					stat_string = "vitality";
					short_stat_string = "VIT";
				}
				if(stat_type == 3) {
					stat_string = "intellect";
					short_stat_string = "INT";
				}
				
				armor_data += stat_string + "=" + stat_val + ":";
				armor_description += ChatColor.RED.toString() + short_stat_string + ": +" + stat_val + ",";
			}
			if(stat_chance <= 5) {
				stat_val = new Random().nextInt(35) + 1;
				stat_chance = r.nextInt(100);
				if(stat_type >= 0 && stat_type < 3) {
					stat_type++;
				} else if(stat_type == 3) {
					stat_type--;
				}
				
				if(stat_type == 0) {
					stat_string = "strength";
					short_stat_string = "STR";
				}
				if(stat_type == 1) {
					stat_string = "dexterity";
					short_stat_string = "DEX";
				}
				if(stat_type == 2) {
					stat_string = "vitality";
					short_stat_string = "VIT";
				}
				if(stat_type == 3) {
					stat_string = "intellect";
					short_stat_string = "INT";
				}
				
				armor_data += stat_string + "=" + stat_val + ":";
				armor_description += ChatColor.RED.toString() + short_stat_string + ": +" + stat_val + ",";
			}
		}
		if(tier == ItemTier.T3) {
			if(stat_chance <= 15) {
				stat_val = new Random().nextInt(75) + 1;
				stat_chance = r.nextInt(100);
				if(stat_type == 0) {
					stat_string = "strength";
					short_stat_string = "STR";
				}
				if(stat_type == 1) {
					stat_string = "dexterity";
					short_stat_string = "DEX";
				}
				if(stat_type == 2) {
					stat_string = "vitality";
					short_stat_string = "VIT";
				}
				if(stat_type == 3) {
					stat_string = "intellect";
					short_stat_string = "INT";
				}
				
				armor_data += stat_string + "=" + stat_val + ":";
				armor_description += ChatColor.RED.toString() + short_stat_string + ": +" + stat_val + ",";
			}
			if(stat_chance <= 5) {
				stat_val = new Random().nextInt(75) + 1;
				stat_chance = r.nextInt(100);
				if(stat_type >= 0 && stat_type < 3) {
					stat_type++;
				} else if(stat_type == 3) {
					stat_type = 0;
				}
				
				if(stat_type == 0) {
					stat_string = "strength";
					short_stat_string = "STR";
				}
				if(stat_type == 1) {
					stat_string = "dexterity";
					short_stat_string = "DEX";
				}
				if(stat_type == 2) {
					stat_string = "vitality";
					short_stat_string = "VIT";
				}
				if(stat_type == 3) {
					stat_string = "intellect";
					short_stat_string = "INT";
				}
				
				armor_data += stat_string + "=" + stat_val + ":";
				armor_description += ChatColor.RED.toString() + short_stat_string + ": +" + stat_val + ",";
			}
			if(stat_chance <= 1) {
				stat_val = new Random().nextInt(75) + 1;
				stat_chance = r.nextInt(100);
				if(stat_type >= 0 && stat_type < 3) {
					stat_type++;
				} else if(stat_type == 3) {
					stat_type = 0;
				}
				
				if(stat_type == 0) {
					stat_string = "strength";
					short_stat_string = "STR";
				}
				if(stat_type == 1) {
					stat_string = "dexterity";
					short_stat_string = "DEX";
				}
				if(stat_type == 2) {
					stat_string = "vitality";
					short_stat_string = "VIT";
				}
				if(stat_type == 3) {
					stat_string = "intellect";
					short_stat_string = "INT";
				}
				
				armor_data += stat_string + "=" + stat_val + ":";
				armor_description += ChatColor.RED.toString() + short_stat_string + ": +" + stat_val + ",";
			}
		}
		if(tier == ItemTier.T4) {
			if(stat_chance <= 15) {
				stat_val = new Random().nextInt(115) + 1;
				stat_chance = r.nextInt(100);
				if(stat_type == 0) {
					stat_string = "strength";
					short_stat_string = "STR";
				}
				if(stat_type == 1) {
					stat_string = "dexterity";
					short_stat_string = "DEX";
				}
				if(stat_type == 2) {
					stat_string = "vitality";
					short_stat_string = "VIT";
				}
				if(stat_type == 3) {
					stat_string = "intellect";
					short_stat_string = "INT";
				}
				
				armor_data += stat_string + "=" + stat_val + ":";
				armor_description += ChatColor.RED.toString() + short_stat_string + ": +" + stat_val + ",";
			}
			if(stat_chance <= 9) {
				stat_val = new Random().nextInt(115) + 1;
				stat_chance = r.nextInt(100);
				if(stat_type >= 0 && stat_type < 3) {
					stat_type++;
				} else if(stat_type == 3) {
					stat_type = 0;
				}
				
				if(stat_type == 0) {
					stat_string = "strength";
					short_stat_string = "STR";
				}
				if(stat_type == 1) {
					stat_string = "dexterity";
					short_stat_string = "DEX";
				}
				if(stat_type == 2) {
					stat_string = "vitality";
					short_stat_string = "VIT";
				}
				if(stat_type == 3) {
					stat_string = "intellect";
					short_stat_string = "INT";
				}
				
				armor_data += stat_string + "=" + stat_val + ":";
				armor_description += ChatColor.RED.toString() + short_stat_string + ": +" + stat_val + ",";
			}
			if(stat_chance <= 4) {
				stat_val = new Random().nextInt(115) + 1;
				stat_chance = r.nextInt(100);
				if(stat_type >= 0 && stat_type < 3) {
					stat_type++;
				} else if(stat_type == 3) {
					stat_type = 0;
				}
				
				if(stat_type == 0) {
					stat_string = "strength";
					short_stat_string = "STR";
				}
				if(stat_type == 1) {
					stat_string = "dexterity";
					short_stat_string = "DEX";
				}
				if(stat_type == 2) {
					stat_string = "vitality";
					short_stat_string = "VIT";
				}
				if(stat_type == 3) {
					stat_string = "intellect";
					short_stat_string = "INT";
				}
				
				armor_data += stat_string + "=" + stat_val + ":";
				armor_description += ChatColor.RED.toString() + short_stat_string + ": +" + stat_val + ",";
			}
		}
		if(tier == ItemTier.T5) {
			if(stat_chance <= 20) {
				stat_val = new Random().nextInt(315) + 1;
				stat_chance = r.nextInt(100);
				if(stat_type == 0) {
					stat_string = "strength";
					short_stat_string = "STR";
				}
				if(stat_type == 1) {
					stat_string = "dexterity";
					short_stat_string = "DEX";
				}
				if(stat_type == 2) {
					stat_string = "vitality";
					short_stat_string = "VIT";
				}
				if(stat_type == 3) {
					stat_string = "intellect";
					short_stat_string = "INT";
				}
				
				armor_data += stat_string + "=" + stat_val + ":";
				armor_description += ChatColor.RED.toString() + short_stat_string + ": +" + stat_val + ",";
			}
			if(stat_chance <= 10) {
				stat_val = new Random().nextInt(315) + 1;
				stat_chance = r.nextInt(100);
				if(stat_type >= 0 && stat_type < 3) {
					stat_type++;
				} else if(stat_type == 3) {
					stat_type = 0;
				}
				
				if(stat_type == 0) {
					stat_string = "strength";
					short_stat_string = "STR";
				}
				if(stat_type == 1) {
					stat_string = "dexterity";
					short_stat_string = "DEX";
				}
				if(stat_type == 2) {
					stat_string = "vitality";
					short_stat_string = "VIT";
				}
				if(stat_type == 3) {
					stat_string = "intellect";
					short_stat_string = "INT";
				}
				
				armor_data += stat_string + "=" + stat_val + ":";
				armor_description += ChatColor.RED.toString() + short_stat_string + ": +" + stat_val + ",";
			}
			if(stat_chance <= 5) {
				stat_val = new Random().nextInt(315) + 1;
				if(stat_type >= 0 && stat_type < 3) {
					stat_type++;
				} else if(stat_type == 3) {
					stat_type = 0;
				}
				
				if(stat_type == 0) {
					stat_string = "strength";
					short_stat_string = "STR";
				}
				if(stat_type == 1) {
					stat_string = "dexterity";
					short_stat_string = "DEX";
				}
				if(stat_type == 2) {
					stat_string = "vitality";
					short_stat_string = "VIT";
				}
				if(stat_type == 3) {
					stat_string = "intellect";
					short_stat_string = "INT";
				}
				
				armor_data += stat_string + "=" + stat_val + ":";
				armor_description += ChatColor.RED.toString() + short_stat_string + ": +" + stat_val + ",";
			}
		}
		
		// Resistances
		int res_chance = r.nextInt(100);
		int res_type = r.nextInt(3); // 0-fire, 1-ice, 2-poison
		String res_string = "";
		String res_shorthand = "";
		int res_val = -1;
		
		if(tier == ItemTier.T1) {
			if(res_chance <= 15) {
				res_val = new Random().nextInt(5) + 1;
			}
		}
		if(tier == ItemTier.T2) {
			if(res_chance <= 15) {
				res_val = new Random().nextInt(7) + 1;
			}
		}
		if(tier == ItemTier.T3) {
			if(res_chance <= 25) {
				res_val = new Random().nextInt(20) + 1;
			}
		}
		if(tier == ItemTier.T4) {
			if(res_chance <= 20) {
				res_val = new Random().nextInt(32) + 1;
			}
		}
		if(tier == ItemTier.T5) {
			if(res_chance <= 30) {
				res_val = new Random().nextInt(45) + 1;
			}
		}
		
		if(res_type == 0) {
			res_string = "fire_resistance";
			res_shorthand = "FIRE";
		}
		if(res_type == 1) {
			res_string = "ice_resistance";
			res_shorthand = "ICE";
		}
		if(res_type == 2) {
			res_string = "poison_resistance";
			res_shorthand = "POISON";
		}
		
		if(res_val > 0) {
			// We have some resistance.
			armor_data += res_string + "=" + res_val + ":";
			armor_description += ChatColor.RED.toString() + res_shorthand + " RESISTANCE: " + res_val + "%,";
			if(!armor_name.contains("of")) {
				armor_name = armor_name + " of ";
			} else {
				armor_name = armor_name + " and ";
			}
			
			if(res_type == 0) {
				armor_name = armor_name + "Fire Resist";
			}
			if(res_type == 1) {
				armor_name = armor_name + "Ice Resist";
			}
			if(res_type == 2) {
				armor_name = armor_name + "Poison Resist";
			}
		}
		
		if(dodge == true) {
			armor_data += "dodge=" + dodge_percent + ":";
			armor_name = "Agile " + armor_name;
			armor_description += ChatColor.RED.toString() + "DODGE: " + dodge_percent + "%,";
		}
		
		if(reflection == true) {
			armor_data += "reflection=" + reflection_percent + ":";
			armor_name = "Reflective " + armor_name;
			armor_description += ChatColor.RED.toString() + "REFLECTION: " + reflection_percent + "%,";
		}
		
		if(hp_regen == true) {
			armor_name = "Mending " + armor_name;
		}
		
		if(block == true) {
			armor_data += "block=" + block_percent + ":";
			armor_name = "Protective " + armor_name;
			armor_description += ChatColor.RED.toString() + "BLOCK: " + block_percent + "%,";
		}
		
		if(gem_find == true) {
			armor_data += "gem_find=" + gem_find_percent + ":";
			if(armor_name.contains("of")) {
				armor_name = armor_name + " Golden";
			} else {
				armor_name = armor_name + " of Pickpocketing";
			}
			armor_description += ChatColor.RED.toString() + "GEM FIND: " + gem_find_percent + "%,";
		}
		
		if(item_find == true) {
			armor_data += "item_find=" + item_find_percent;
			if(armor_name.contains("of")) {
				armor_name = armor_name + " Treasure";
			} else {
				armor_name = armor_name + " of Treasure";
			}
			armor_description += ChatColor.RED.toString() + "ITEM FIND: " + item_find_percent + "%,";
		}
		
		if(thorns == true) {
			armor_data += "thorns=" + thorns_percent + ":";
			if(armor_name.contains("of")) {
				armor_name = armor_name + " Spikes";
			} else {
				armor_name = armor_name + " of Thorns";
			}
			armor_description += ChatColor.RED.toString() + "THORNS: " + thorns_percent + "% DMG,";
		}
		
		/*if(hp_regen == true){
		    armor_data += "ls=" + hp_regen_percent + ":";
		    armor_name = "Lifestealing " + armor_name; 
		    armor_description += ChatColor.RED.toString() + "LIFE STEAL: " + hp_regen_percent + "%,";
		    // hp_regen == .split(":")[2]
		}


		if(block == true){
		    armor_data += "crit=" + block_percent + ":";
		    armor_name = "Deadly " + armor_name;
		    armor_description += ChatColor.RED.toString() + "CRITICAL HIT: " + block_percent + "%,";
		}


		if(hp_increase == true){
		    armor_data += "edmg=" + element_dmg_type + "," + element_dmg_amount + ":";

		    if(dodge == true){
		        armor_name = armor_name + " " + element_dmg_type.substring(0, 1).toUpperCase() + element_dmg_type.substring(1, element_dmg_type.length());
		    }
		    if(dodge == false){
		        armor_name = armor_name + " of " + element_dmg_type.substring(0, 1).toUpperCase() + element_dmg_type.substring(1, element_dmg_type.length());
		    }

		    armor_description += ChatColor.RED.toString() + element_dmg_type.toUpperCase() + " DMG: +" + element_dmg_amount + ",";
		    // hp_increase == .split(":")[3].split(",")[1]
		    // edmg=fire,3
		}*/
		
		armor_description += "," + rarity;
		
		if(enchant_count > 0) {
			armor_name = ChatColor.RED + "[+" + enchant_count + "]" + " " + tag.toString() + armor_name;
		} else {
			armor_name = tag.toString() + armor_name;
		}
		//log.info(armor_name + " = " + armor_data);
		
		return ItemMechanics.signCustomItem(m, (short) 0, armor_name, armor_description);
	}
	
}
