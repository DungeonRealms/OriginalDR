package me.vaqxine.ItemMechanics;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

import me.vaqxine.EnchantMechanics.EnchantMechanics;
import me.vaqxine.HealthMechanics.HealthMechanics;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemGenerators {

	static Logger log = Logger.getLogger("Minecraft");
	static ChatColor red = ChatColor.RED;

	public static ItemMechanics plugin;
	public ItemGenerators(ItemMechanics instance){
		plugin = instance;
	}

	@SuppressWarnings("deprecation")
	public static ItemStack customGenerator(String template_name){
		File template = new File("plugins/ItemMechanics/custom_items/" + template_name + ".item");
		if(!(template.exists())){
			return null; // No such custom template!
		}

		int item_id = -1;
		String item_name = "";
		List<String> item_lore = new ArrayList<String>();

		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(template));

			String line = "";
			while((line = reader.readLine()) != null){
				if(line.startsWith("item_name=")){
					line = line.replaceAll("&0", ChatColor.BLACK.toString());
					line = line.replaceAll("&1", ChatColor.DARK_BLUE.toString());
					line = line.replaceAll("&2", ChatColor.DARK_GREEN.toString());
					line = line.replaceAll("&3", ChatColor.DARK_AQUA.toString());
					line = line.replaceAll("&4", ChatColor.DARK_RED.toString());
					line = line.replaceAll("&5", ChatColor.DARK_PURPLE.toString());
					line = line.replaceAll("&6", ChatColor.GOLD.toString());
					line = line.replaceAll("&7", ChatColor.GRAY.toString());
					line = line.replaceAll("&8", ChatColor.DARK_GRAY.toString());
					line = line.replaceAll("&9", ChatColor.BLUE.toString());
					line = line.replaceAll("&a", ChatColor.GREEN.toString());
					line = line.replaceAll("&b", ChatColor.AQUA.toString());
					line = line.replaceAll("&c", ChatColor.RED.toString());
					line = line.replaceAll("&d", ChatColor.LIGHT_PURPLE.toString());
					line = line.replaceAll("&e", ChatColor.YELLOW.toString());
					line = line.replaceAll("&f", ChatColor.WHITE.toString());
					
					line = line.replaceAll("&u", ChatColor.UNDERLINE.toString());
					line = line.replaceAll("&s", ChatColor.BOLD.toString());
					line = line.replaceAll("&i", ChatColor.ITALIC.toString());
					line = line.replaceAll("&m", ChatColor.MAGIC.toString());
					
					item_name = line.substring(line.indexOf("=") + 1, line.length());
				}
				else if(line.startsWith("item_id=")){
					item_id = Integer.parseInt(line.substring(line.indexOf("=") + 1, line.length()));
				}
				else{
					// It's lore!
					line = line.replaceAll("&0", ChatColor.BLACK.toString());
					line = line.replaceAll("&1", ChatColor.DARK_BLUE.toString());
					line = line.replaceAll("&2", ChatColor.DARK_GREEN.toString());
					line = line.replaceAll("&3", ChatColor.DARK_AQUA.toString());
					line = line.replaceAll("&4", ChatColor.DARK_RED.toString());
					line = line.replaceAll("&5", ChatColor.DARK_PURPLE.toString());
					line = line.replaceAll("&6", ChatColor.GOLD.toString());
					line = line.replaceAll("&7", ChatColor.GRAY.toString());
					line = line.replaceAll("&8", ChatColor.DARK_GRAY.toString());
					line = line.replaceAll("&9", ChatColor.BLUE.toString());
					line = line.replaceAll("&a", ChatColor.GREEN.toString());
					line = line.replaceAll("&b", ChatColor.AQUA.toString());
					line = line.replaceAll("&c", ChatColor.RED.toString());
					line = line.replaceAll("&d", ChatColor.LIGHT_PURPLE.toString());
					line = line.replaceAll("&e", ChatColor.YELLOW.toString());
					line = line.replaceAll("&f", ChatColor.WHITE.toString());
					
					line = line.replaceAll("&u", ChatColor.UNDERLINE.toString());
					line = line.replaceAll("&s", ChatColor.BOLD.toString());
					line = line.replaceAll("&i", ChatColor.ITALIC.toString());
					line = line.replaceAll("&m", ChatColor.MAGIC.toString());
					
					if(line.contains("(")){
						// Number range!
						String line_copy = line;
						for(String s : line_copy.split("\\(")){
							if(!(s.contains("~"))){
								continue;
							}
							int lower = Integer.parseInt(s.substring(0, s.indexOf("~")));
							int upper = Integer.parseInt(s.substring(s.indexOf("~") + 1, s.indexOf(")")));
						
							int val = new Random().nextInt((upper - lower)) + lower;
							line = line.replace("(" + lower + "~" + upper + ")", String.valueOf(val));
						}
					}
					
					item_lore.add(line);
				}
			}
			reader.close();

		} catch (Exception e) {
			log.info("Template error - " + template_name);
			e.printStackTrace();
		}
		
		ItemStack is = new ItemStack(Material.getMaterial(item_id), 1);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(item_name);
		im.setLore(item_lore);
		is.setItemMeta(im);
		
		String rarity = ItemMechanics.generateItemRarity(is);
		if(rarity != null){
			// Add rarity if needed.
			item_lore.add(rarity);
			im.setLore(item_lore);
			is.setItemMeta(im);
		}
		
		Attributes attributes = new Attributes(is);
		attributes.clear();
	
		return attributes.getStack();
	}

	public static ItemStack LeggingsGenerator(int tier, boolean reroll, ItemStack original){
		Random r = new Random();
		Material m = null;
		boolean hp_regen = false, /*hp_increase = false, */ energy_regen = false, block = false, dodge = false, thorns = false, reflection = false, gem_find = false, item_find = false;
		int hp_regen_percent = 0;
		int hp_increase_amount = 0;
		int energy_regen_percent = 0;
		int block_percent = 0;
		int dodge_percent = 0;
		int thorns_percent = 0;
		int reflection_percent = 0;
		int gem_find_percent = 0;
		int item_find_percent = 0;

		/* int hp_regen_chance = 0;
		int hp_increase_chance = 0;
		int energy_regen_chance = 0; */
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

		if(tier == 1){
			armor_name = "Leather Leggings";
			tag = ChatColor.WHITE;
			m = Material.LEATHER_LEGGINGS;

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
			block_min = 1;
			dodge_min = 1;
			thorns_min = 1; // 0.1%
			reflection_min = 1; // 0.1%
			gem_find_min = 1; // 0.1%
			item_find_min = 1; // 0.1%

			hp_regen_max = 15;
			energy_regen_max = 5;
			block_max = 5;
			dodge_max = 5;
			thorns_max = 2;
			reflection_max = 1;
			gem_find_max = 5;
			item_find_max = 2;

		}
		if(tier == 2){
			armor_name = "Chainmail Leggings";
			tag = ChatColor.GREEN;
			m = Material.CHAINMAIL_LEGGINGS;

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
			block_min = 1;
			dodge_min = 1;
			thorns_min = 1; 
			reflection_min = 1; 
			gem_find_min = 1; 
			item_find_min = 1; // 0.1%

			hp_regen_max = 25; 
			energy_regen_max = 7;
			block_max = 8;
			dodge_max = 8;
			thorns_max = 3;
			reflection_max = 2;
			gem_find_max = 8;
			item_find_max = 2;
		}
		if(tier == 3){
			armor_name = "Platemail Leggings";
			tag = ChatColor.AQUA;
			m = Material.IRON_LEGGINGS;

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
			block_min = 1;
			dodge_min = 1;
			thorns_min = 1; 
			reflection_min = 1; 
			gem_find_min = 3; 
			item_find_min = 1; // 0.1%

			hp_regen_max = 55;
			energy_regen_max = 9;
			block_max = 10;
			dodge_max = 10;
			thorns_max = 5;
			reflection_max = 4;
			gem_find_max = 15;
			item_find_max = 3;
		}
		if(tier == 4){
			armor_name = "Magic Platemail Leggings";
			tag = ChatColor.LIGHT_PURPLE;
			m = Material.DIAMOND_LEGGINGS;

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
			block_min = 1;
			dodge_min = 1;
			thorns_min = 2; 
			reflection_min = 2; 
			gem_find_min = 5; 
			item_find_min = 1; // 0.1%

			hp_regen_max = 75;
			energy_regen_max = 12;
			block_max = 12;
			dodge_max = 12;
			thorns_max = 9;
			reflection_max = 5;
			gem_find_max = 20;
			item_find_max = 4;
		}
		if(tier == 5){
			armor_name = "Legendary Platemail Leggings";
			tag = ChatColor.YELLOW;
			m = Material.GOLD_LEGGINGS;

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
			block_min = 1;
			dodge_min = 1;
			thorns_min = 2; 
			reflection_min = 2; 
			gem_find_min = 5; 
			item_find_min = 1; // 0.1%

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
		if(armor_range_check <= 80){ 
			rarity = ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + "Common";
			if(tier == 1){
				//int min_num = 1 + 1;
				//int max_num = 2 + 1;

				min_armor = 1;
				max_armor = 1;

				int min_hp = 10;
				int max_hp = 20;

				hp_increase_amount = r.nextInt(max_hp - min_hp) + min_hp;
			}
			if(tier == 2){
				int min_num = 1 + 1;
				int max_num = 2 + 1;

				min_armor = r.nextInt(max_num - min_num) + (min_num - 1);
				max_armor = r.nextInt(max_num - min_armor) + min_armor;

				int min_hp = 60;
				int max_hp = 80;

				hp_increase_amount = r.nextInt(max_hp - min_hp) + min_hp;
			}
			if(tier == 3){
				int min_num = 5 + 1;
				int max_num = 6 + 1;


				min_armor = r.nextInt(max_num - min_num) + (min_num - 1);
				max_armor = r.nextInt(max_num - min_armor) + min_armor;

				int min_hp = 200;
				int max_hp = 350;

				hp_increase_amount = r.nextInt(max_hp - min_hp) + min_hp;
			}
			if(tier == 4){
				int min_num = 8 + 1;
				int max_num = 9 + 1;

				min_armor = r.nextInt(max_num - min_num) + (min_num - 1);
				max_armor = r.nextInt(max_num - min_armor) + min_armor;

				int min_hp = 600;
				int max_hp = 800;

				hp_increase_amount = r.nextInt(max_hp - min_hp) + min_hp;
			}
			if(tier == 5){
				int min_num = 11 + 1;
				int max_num = 12 + 1;

				min_armor = r.nextInt(max_num - min_num) + (min_num - 1);
				max_armor = r.nextInt(max_num - min_armor) + min_armor;

				int min_hp = 1500;
				int max_hp = 2500;

				hp_increase_amount = r.nextInt(max_hp - min_hp) + min_hp;
			}
		}

		if(armor_range_check > 80 && armor_range_check < 95){
			rarity = ChatColor.GREEN.toString() + ChatColor.ITALIC.toString() + "Uncommon";
			if(tier == 1){
				int min_num = 1 + 1;
				int max_num = 2 + 1;

				min_armor = r.nextInt(max_num - min_num) + (min_num - 1);
				max_armor = r.nextInt(max_num - min_armor) + min_armor;

				int min_hp = 20;
				int max_hp = 50;

				hp_increase_amount = r.nextInt(max_hp - min_hp) + min_hp;
			}
			if(tier == 2){
				int min_num = 3 + 1;
				int max_num = 4 + 1;

				min_armor = r.nextInt(max_num - min_num) + (min_num - 1);
				max_armor = r.nextInt(max_num - min_armor) + min_armor;

				int min_hp = 100;
				int max_hp = 250;

				hp_increase_amount = r.nextInt(max_hp - min_hp) + min_hp;
			}
			if(tier == 3){
				int min_num = 6 + 1;
				int max_num = 9 + 1;


				min_armor = r.nextInt(max_num - min_num) + (min_num - 1);
				max_armor = r.nextInt(max_num - min_armor) + min_armor;

				int min_hp = 350;
				int max_hp = 600;

				hp_increase_amount = r.nextInt(max_hp - min_hp) + min_hp;
			}
			if(tier == 4){
				int min_num = 10 + 1;
				int max_num = 11 + 1;

				min_armor = r.nextInt(max_num - min_num) + (min_num - 1);
				max_armor = r.nextInt(max_num - min_armor) + min_armor;

				int min_hp = 800;
				int max_hp = 1300;

				hp_increase_amount = r.nextInt(max_hp - min_hp) + min_hp;
			}
			if(tier == 5){
				int min_num = 13 + 1;
				int max_num = 14 + 1;

				min_armor = r.nextInt(max_num - min_num) + (min_num - 1);
				max_armor = r.nextInt(max_num - min_armor) + min_armor;

				int min_hp = 2500;
				int max_hp = 3600;

				hp_increase_amount = r.nextInt(max_hp - min_hp) + min_hp;
			}
		}

		if(armor_range_check >= 95 && armor_range_check != 99){
			rarity = ChatColor.AQUA.toString() + ChatColor.ITALIC.toString() + "Rare";
			if(tier == 1){
				int min_num = 1 + 1;
				int max_num = 2 + 1;

				min_armor = r.nextInt(max_num - min_num) + (min_num - 1);
				max_armor = r.nextInt(max_num - min_armor) + min_armor;

				int min_hp = 80;
				int max_hp = 120;

				hp_increase_amount = r.nextInt(max_hp - min_hp) + min_hp;
			}
			if(tier == 2){
				int min_num = 5 + 1;
				int max_num = 6 + 1;

				min_armor = r.nextInt(max_num - min_num) + (min_num - 1);
				max_armor = r.nextInt(max_num - min_armor) + min_armor;

				int min_hp = 250;
				int max_hp = 350;

				hp_increase_amount = r.nextInt(max_hp - min_hp) + min_hp;
			}
			if(tier == 3){
				int min_num = 8 + 1;
				int max_num = 9 + 1;


				min_armor = r.nextInt(max_num - min_num) + (min_num - 1);
				max_armor = r.nextInt(max_num - min_armor) + min_armor;

				int min_hp = 600;
				int max_hp = 800;

				hp_increase_amount = r.nextInt(max_hp - min_hp) + min_hp;
			}
			if(tier == 4){
				int min_num = 11 + 1;
				int max_num = 12 + 1;

				min_armor = r.nextInt(max_num - min_num) + (min_num - 1);
				max_armor = r.nextInt(max_num - min_armor) + min_armor;

				int min_hp = 1300;
				int max_hp = 2400;

				hp_increase_amount = r.nextInt(max_hp - min_hp) + min_hp;
			}
			if(tier == 5){
				int min_num = 16 + 1;
				int max_num = 17 + 1;

				min_armor = r.nextInt(max_num - min_num) + (min_num - 1);
				max_armor = r.nextInt(max_num - min_armor) + min_armor;

				int min_hp = 3600;
				int max_hp = 5400;

				hp_increase_amount = r.nextInt(max_hp - min_hp) + min_hp;
			}
		}

		if(armor_range_check == 99){
			rarity = ChatColor.YELLOW.toString() + ChatColor.ITALIC.toString() + "Unique";
			if(tier == 1){
				int min_num = 1 + 1;
				int max_num = 2 + 1;

				min_armor = r.nextInt(max_num - min_num) + (min_num - 1);
				max_armor = r.nextInt(max_num - min_armor) + min_armor;

				int min_hp = 80;
				int max_hp = 120;

				hp_increase_amount = r.nextInt(max_hp - min_hp) + min_hp;
			}
			if(tier == 2){
				int min_num = 5 + 1;
				int max_num = 6 + 1;

				min_armor = r.nextInt(max_num - min_num) + (min_num - 1);
				max_armor = r.nextInt(max_num - min_armor) + min_armor;

				int min_hp = 250;
				int max_hp = 350;

				hp_increase_amount = r.nextInt(max_hp - min_hp) + min_hp;
			}
			if(tier == 3){
				int min_num = 8 + 1;
				int max_num = 9 + 1;


				min_armor = r.nextInt(max_num - min_num) + (min_num - 1);
				max_armor = r.nextInt(max_num - min_armor) + min_armor;

				int min_hp = 600;
				int max_hp = 800;

				hp_increase_amount = r.nextInt(max_hp - min_hp) + min_hp;
			}
			if(tier == 4){
				int min_num = 11 + 1;
				int max_num = 12 + 1;

				min_armor = r.nextInt(max_num - min_num) + (min_num - 1);
				max_armor = r.nextInt(max_num - min_armor) + min_armor;

				int min_hp = 1300;
				int max_hp = 2400;

				hp_increase_amount = r.nextInt(max_hp - min_hp) + min_hp;
			}
			if(tier == 5){
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

		if(min_armor == 0){
			min_armor = 1;
		}

		if(max_armor == 0){
			min_armor = 1;
			max_armor = 1;
		}

		int regen_type = r.nextInt(4); // 0, 1.

		if(regen_type >= 1){  // HP/s HP REGEN
			hp_regen = true;
		//hp_regen_max = (int) Math.round((hp_increase_amount / 10) + (hp_increase_amount * 0.05));
		//if(hp_regen_max < 1){hp_regen_max = 1;}
			double regen_val = hp_increase_amount * 0.05;
			if(regen_val < 1){regen_val = 1;}

			int real_hp_regen_max = (int) ((regen_val) + (hp_increase_amount / 10)) - r.nextInt((int) Math.round(regen_val));
			if(real_hp_regen_max < 1){real_hp_regen_max = 1;}
			//hp_regen_percent = r.nextInt(real_hp_regen_max) + 1;
			hp_regen_percent = r.nextInt(hp_regen_max - hp_regen_min) + hp_regen_min;
		}
		else if(regen_type == 0){
			energy_regen = true;
			energy_regen_percent = r.nextInt(energy_regen_max - energy_regen_min) + energy_regen_min; 
			energy_regen_percent = energy_regen_percent / 2;

			if(energy_regen_percent < 1){
				energy_regen_percent = 1;
			}
		}

		/*if(r.nextInt(100) <= energy_regen_chance){
			energy_regen = true;
			energy_regen_percent = r.nextInt(energy_regen_max - energy_regen_min) + energy_regen_min; 
		}*/

		if(r.nextInt(100) <= block_chance){ 
			block = true;
			block_percent = r.nextInt(block_max - block_min) + block_min; // 1 - 10%
		}

		if(r.nextInt(100) <= dodge_chance){ // 1% chance.
			dodge = true;
			dodge_percent = r.nextInt(dodge_max - dodge_min) + dodge_min; // 1 - 3%
		}

		if(r.nextInt(100) <= thorns_chance){ // 1% chance.
			thorns = true;
			thorns_percent = r.nextInt(thorns_max - thorns_min) + thorns_min; // 1 - 3%
		}

		if(r.nextInt(100) <= reflection_chance){ // 1% chance.
			reflection = true;
			try{
				reflection_percent = r.nextInt(reflection_max - reflection_min) + reflection_min; // 1 - 3%
			} catch(IllegalArgumentException iae){
				reflection_percent = reflection_min;
			}
		}

		if(r.nextInt(100) <= gem_find_chance){ // 1% chance.			
			gem_find = true;
			gem_find_percent = r.nextInt(gem_find_max - gem_find_min) + gem_find_min; // 1 - 3%
		}

		if(r.nextInt(1000) <= item_find_chance){ // 1% chance.
			item_find = true;
			item_find_percent = r.nextInt(item_find_max - item_find_min) + item_find_min; // 1 - 3%
		}


		int armor_type = new Random().nextInt(2); // 0 or 1.

		@SuppressWarnings("unused")
		String armor_data = "";
		int enchant_count = 0;
		if(reroll == true && original != null){
			//log.info("[IM] Leggings reroll.");
			rarity = ItemMechanics.getItemRarity(original);
			enchant_count = EnchantMechanics.getEnchantCount(original);
			armor_type = 2; // Ignore the normal check.
			List<Integer> vals = new ArrayList<Integer>();
			if(ItemMechanics.getDmgVal(original, false) != null){
				vals = ItemMechanics.getDmgVal(original, false);
				min_armor = vals.get(0);
				max_armor = vals.get(1);
				armor_data = m.name().toUpperCase() + "#" + min_armor + "-" + max_armor + ":";
				armor_description += red.toString() + "DPS: " + min_armor + " - " + max_armor + "%,";	
			}
			if(ItemMechanics.getArmorVal(original, false) != null){
				vals = ItemMechanics.getArmorVal(original, false);
				min_armor = vals.get(0);
				max_armor = vals.get(1);
				armor_data = m.name().toUpperCase() + "#" + min_armor + "-" + max_armor + ":";
				armor_description += red.toString() + "ARMOR: " + min_armor + " - " + max_armor + "%,";	
			}

			hp_increase_amount = HealthMechanics.getHealthVal(original);
			String oarmor_data = ItemMechanics.getArmorData(original);
			if(oarmor_data.contains("hp_regen")){
				int health_regen_val = Integer.parseInt(oarmor_data.substring(oarmor_data.indexOf("hp_regen=") + 9, oarmor_data.indexOf("@hp_regen_split@")));
				hp_regen = true;
				energy_regen = false;
				hp_regen_percent = health_regen_val;
			}
			if(oarmor_data.contains("energy_regen")){
				int energy_regen_val = Integer.parseInt(oarmor_data.substring(oarmor_data.indexOf("energy_regen=") + 13, oarmor_data.indexOf("@energy_regen_split@")));
				hp_regen = false;
				energy_regen = true;
				energy_regen_percent = energy_regen_val;
			}
		}
		if(armor_type == 0){
			armor_data = m.name().toUpperCase() + "#" + min_armor + "-" + max_armor + ":";
			armor_description += red.toString() + "ARMOR: " + min_armor + " - " + max_armor + "%,";	
		}
		if(armor_type == 1){
			armor_data = m.name().toUpperCase() + "#" + min_armor + "-" + max_armor + ":";
			armor_description += red.toString() + "DPS: " + min_armor + " - " + max_armor + "%,";	
		}

		if(hp_increase_amount <= 0){
			hp_increase_amount = 1;
		}

		armor_data += "hp_increase=" + hp_increase_amount + "@";
		//armor_name = armor_name + " of Fortitude";
		armor_description += red.toString() + "HP: +" + hp_increase_amount + ",";

		if(hp_regen == true){
			armor_data += "hp_regen=" + hp_regen_percent + "@hp_regen_split@:";
			armor_description += red.toString() + "HP REGEN: +" + hp_regen_percent + " HP/s,";
		}

		if(energy_regen == true){
			armor_data += "energy_regen=" + energy_regen_percent + ":";
			armor_name = armor_name + " of Fortitude";
			armor_description += red.toString() + "ENERGY REGEN: +" + energy_regen_percent + "%,";
			// Take from first index of energy_regen= to next index of : from that point.
		}

		int stat_chance = r.nextInt(100);
		int stat_type = r.nextInt(4); // 0, 1, 2, 3
		String stat_string = "";
		String short_stat_string = "";
		int stat_val = 1;
		if(tier == 1){
			stat_val = new Random().nextInt(15) + 1;
			if(stat_chance <= 25){
				stat_chance = r.nextInt(100);
				if(stat_type == 0){
					stat_string = "strength";
					short_stat_string = "STR";
				}
				if(stat_type == 1){
					stat_string = "dexterity";
					short_stat_string = "DEX";
				}
				if(stat_type == 2){
					stat_string = "vitality";
					short_stat_string = "VIT";
				}
				if(stat_type == 3){
					stat_string = "intellect";
					short_stat_string = "INT";
				}
				
				armor_data += stat_string + "=" + stat_val + ":";
				armor_description += red.toString() + short_stat_string + ": +" + stat_val + ",";
			}
		}
		if(tier == 2){
			if(stat_chance <= 20){
				stat_val = new Random().nextInt(35) + 1;
				stat_chance = r.nextInt(100);
				if(stat_type == 0){
					stat_string = "strength";
					short_stat_string = "STR";
				}
				if(stat_type == 1){
					stat_string = "dexterity";
					short_stat_string = "DEX";
				}
				if(stat_type == 2){
					stat_string = "vitality";
					short_stat_string = "VIT";
				}
				if(stat_type == 3){
					stat_string = "intellect";
					short_stat_string = "INT";
				}

				armor_data += stat_string + "=" + stat_val + ":";
				armor_description += red.toString() + short_stat_string + ": +" + stat_val + ",";
			}
			if(stat_chance <= 5){
				stat_val = new Random().nextInt(35) + 1;
				stat_chance = r.nextInt(100);
				if(stat_type >= 0 && stat_type < 3){
					stat_type++;
				}
				else if(stat_type == 3){
					stat_type--;
				}

				if(stat_type == 0){
					stat_string = "strength";
					short_stat_string = "STR";
				}
				if(stat_type == 1){
					stat_string = "dexterity";
					short_stat_string = "DEX";
				}
				if(stat_type == 2){
					stat_string = "vitality";
					short_stat_string = "VIT";
				}
				if(stat_type == 3){
					stat_string = "intellect";
					short_stat_string = "INT";
				}

				armor_data += stat_string + "=" + stat_val + ":";
				armor_description += red.toString() + short_stat_string + ": +" + stat_val + ",";
			}
		}
		if(tier == 3){
			if(stat_chance <= 15){
				stat_val = new Random().nextInt(75) + 1;
				stat_chance = r.nextInt(100);
				if(stat_type == 0){
					stat_string = "strength";
					short_stat_string = "STR";
				}
				if(stat_type == 1){
					stat_string = "dexterity";
					short_stat_string = "DEX";
				}
				if(stat_type == 2){
					stat_string = "vitality";
					short_stat_string = "VIT";
				}
				if(stat_type == 3){
					stat_string = "intellect";
					short_stat_string = "INT";
				}

				armor_data += stat_string + "=" + stat_val + ":";
				armor_description += red.toString() + short_stat_string + ": +" + stat_val + ",";
			}
			if(stat_chance <= 5){
				stat_val = new Random().nextInt(75) + 1;
				stat_chance = r.nextInt(100);
				if(stat_type >= 0 && stat_type < 3){
					stat_type++;
				}
				else if(stat_type == 3){
					stat_type = 0;
				}

				if(stat_type == 0){
					stat_string = "strength";
					short_stat_string = "STR";
				}
				if(stat_type == 1){
					stat_string = "dexterity";
					short_stat_string = "DEX";
				}
				if(stat_type == 2){
					stat_string = "vitality";
					short_stat_string = "VIT";
				}
				if(stat_type == 3){
					stat_string = "intellect";
					short_stat_string = "INT";
				}

				armor_data += stat_string + "=" + stat_val + ":";
				armor_description += red.toString() + short_stat_string + ": +" + stat_val + ",";
			}
			if(stat_chance <= 1){
				stat_val = new Random().nextInt(75) + 1;
				stat_chance = r.nextInt(100);
				if(stat_type >= 0 && stat_type < 3){
					stat_type++;
				}
				else if(stat_type == 3){
					stat_type = 0;
				}

				if(stat_type == 0){
					stat_string = "strength";
					short_stat_string = "STR";
				}
				if(stat_type == 1){
					stat_string = "dexterity";
					short_stat_string = "DEX";
				}
				if(stat_type == 2){
					stat_string = "vitality";
					short_stat_string = "VIT";
				}
				if(stat_type == 3){
					stat_string = "intellect";
					short_stat_string = "INT";
				}

				armor_data += stat_string + "=" + stat_val + ":";
				armor_description += red.toString() + short_stat_string + ": +" + stat_val + ",";
			}
		}
		if(tier == 4){
			if(stat_chance <= 15){
				stat_val = new Random().nextInt(115) + 1;
				stat_chance = r.nextInt(100);
				if(stat_type == 0){
					stat_string = "strength";
					short_stat_string = "STR";
				}
				if(stat_type == 1){
					stat_string = "dexterity";
					short_stat_string = "DEX";
				}
				if(stat_type == 2){
					stat_string = "vitality";
					short_stat_string = "VIT";
				}
				if(stat_type == 3){
					stat_string = "intellect";
					short_stat_string = "INT";
				}

				armor_data += stat_string + "=" + stat_val + ":";
				armor_description += red.toString() + short_stat_string + ": +" + stat_val + ",";
			}
			if(stat_chance <= 9){
				stat_val = new Random().nextInt(115) + 1;
				stat_chance = r.nextInt(100);
				if(stat_type >= 0 && stat_type < 3){
					stat_type++;
				}
				else if(stat_type == 3){
					stat_type = 0;
				}

				if(stat_type == 0){
					stat_string = "strength";
					short_stat_string = "STR";
				}
				if(stat_type == 1){
					stat_string = "dexterity";
					short_stat_string = "DEX";
				}
				if(stat_type == 2){
					stat_string = "vitality";
					short_stat_string = "VIT";
				}
				if(stat_type == 3){
					stat_string = "intellect";
					short_stat_string = "INT";
				}

				armor_data += stat_string + "=" + stat_val + ":";
				armor_description += red.toString() + short_stat_string + ": +" + stat_val + ",";
			}
			if(stat_chance <= 4){
				stat_val = new Random().nextInt(115) + 1;
				stat_chance = r.nextInt(100);
				if(stat_type >= 0 && stat_type < 3){
					stat_type++;
				}
				else if(stat_type == 3){
					stat_type = 0;
				}

				if(stat_type == 0){
					stat_string = "strength";
					short_stat_string = "STR";
				}
				if(stat_type == 1){
					stat_string = "dexterity";
					short_stat_string = "DEX";
				}
				if(stat_type == 2){
					stat_string = "vitality";
					short_stat_string = "VIT";
				}
				if(stat_type == 3){
					stat_string = "intellect";
					short_stat_string = "INT";
				}

				armor_data += stat_string + "=" + stat_val + ":";
				armor_description += red.toString() + short_stat_string + ": +" + stat_val + ",";
			}
		}
		if(tier == 5){
			if(stat_chance <= 20){
				stat_val = new Random().nextInt(315) + 1;
				stat_chance = r.nextInt(100);
				if(stat_type == 0){
					stat_string = "strength";
					short_stat_string = "STR";
				}
				if(stat_type == 1){
					stat_string = "dexterity";
					short_stat_string = "DEX";
				}
				if(stat_type == 2){
					stat_string = "vitality";
					short_stat_string = "VIT";
				}
				if(stat_type == 3){
					stat_string = "intellect";
					short_stat_string = "INT";
				}

				armor_data += stat_string + "=" + stat_val + ":";
				armor_description += red.toString() + short_stat_string + ": +" + stat_val + ",";
			}
			if(stat_chance <= 10){
				stat_val = new Random().nextInt(315) + 1;
				stat_chance = r.nextInt(100);
				if(stat_type >= 0 && stat_type < 3){
					stat_type++;
				}
				else if(stat_type == 3){
					stat_type = 0;
				}

				if(stat_type == 0){
					stat_string = "strength";
					short_stat_string = "STR";
				}
				if(stat_type == 1){
					stat_string = "dexterity";
					short_stat_string = "DEX";
				}
				if(stat_type == 2){
					stat_string = "vitality";
					short_stat_string = "VIT";
				}
				if(stat_type == 3){
					stat_string = "intellect";
					short_stat_string = "INT";
				}

				armor_data += stat_string + "=" + stat_val + ":";
				armor_description += red.toString() + short_stat_string + ": +" + stat_val + ",";
			}
			if(stat_chance <= 5){
				stat_val = new Random().nextInt(315) + 1;
				if(stat_type >= 0 && stat_type < 3){
					stat_type++;
				}
				else if(stat_type == 3){
					stat_type = 0;
				}

				if(stat_type == 0){
					stat_string = "strength";
					short_stat_string = "STR";
				}
				if(stat_type == 1){
					stat_string = "dexterity";
					short_stat_string = "DEX";
				}
				if(stat_type == 2){
					stat_string = "vitality";
					short_stat_string = "VIT";
				}
				if(stat_type == 3){
					stat_string = "intellect";
					short_stat_string = "INT";
				}

				armor_data += stat_string + "=" + stat_val + ":";
				armor_description += red.toString() + short_stat_string + ": +" + stat_val + ",";
			}
		}

		// Resistances
		int res_chance = r.nextInt(100);
		int res_type = r.nextInt(3); // 0-fire, 1-ice, 2-poison
		String res_string = "";
		String res_shorthand = "";
		int res_val = -1;

		if(tier == 1){
			if(res_chance <= 15){
				res_val = new Random().nextInt(5) + 1;
			}
		}
		if(tier == 2){
			if(res_chance <= 15){
				res_val = new Random().nextInt(7) + 1;
			}
		}
		if(tier == 3){
			if(res_chance <= 25){
				res_val = new Random().nextInt(20) + 1;
			}
		}
		if(tier == 4){
			if(res_chance <= 20){
				res_val = new Random().nextInt(32) + 1;
			}
		}
		if(tier == 5){
			if(res_chance <= 30){
				res_val = new Random().nextInt(45) + 1;
			}
		}

		if(res_type == 0){
			res_string = "fire_resistance";
			res_shorthand = "FIRE";
		}
		if(res_type == 1){
			res_string = "ice_resistance";
			res_shorthand = "ICE";
		}
		if(res_type == 2){
			res_string = "poison_resistance";
			res_shorthand = "POISON";
		}

		if(res_val > 0){
			// We have some resistance.
			armor_data += res_string + "=" + res_val + ":";
			armor_description += red.toString() + res_shorthand + " RESISTANCE: " + res_val + "%,";
			if(!armor_name.contains("of")){
				armor_name = armor_name + " of ";
			}
			else{
				armor_name = armor_name + " and ";
			}
			
			
			if(res_type == 0){
				armor_name = armor_name + "Fire Resist";
			}
			if(res_type == 1){
				armor_name = armor_name + "Ice Resist";
			}
			if(res_type == 2){
				armor_name = armor_name + "Poison Resist";
			}
		}

		if(dodge == true){
			armor_data += "dodge=" + dodge_percent + ":";;
			armor_name = "Agile " + armor_name;
			armor_description += red.toString() + "DODGE: " + dodge_percent + "%,";
		}

		if(reflection == true){
			armor_data += "reflection=" + reflection_percent + ":";
			armor_name = "Reflective " + armor_name;
			armor_description += red.toString() + "REFLECTION: " + reflection_percent + "%,";
		}

		if(hp_regen == true){
			armor_name = "Mending " + armor_name;
		}

		if(block == true){
			armor_data += "block=" + block_percent + ":";;
			armor_name = "Protective " + armor_name;
			armor_description += red.toString() + "BLOCK: " + block_percent + "%,";
		}

		if(gem_find == true){
			armor_data += "gem_find=" + gem_find_percent + ":";;
			if(armor_name.contains("of")){
				armor_name = armor_name + " Golden";
			}
			else{
				armor_name = armor_name + " of Pickpocketing";
			}
			armor_description += red.toString() + "GEM FIND: " + gem_find_percent + "%,";
		}

		if(item_find == true){
			armor_data += "item_find=" + item_find_percent;
			if(armor_name.contains("of")){
				armor_name = armor_name + " Treasure";
			}	
			else{
				armor_name = armor_name + " of Treasure";
			}
			armor_description += red.toString() + "ITEM FIND: " + item_find_percent + "%,";
		}


		if(thorns == true){
			armor_data += "thorns=" + thorns_percent + ":";;
			if(armor_name.contains("of")){
				armor_name = armor_name + " Spikes";
			}
			else{
				armor_name = armor_name + " of Thorns";
			}
			armor_description += red.toString() + "THORNS: " + thorns_percent + "% DMG,";
		}



		/*if(hp_regen == true){
			armor_data += "ls=" + hp_regen_percent + ":";
			armor_name = "Lifestealing " + armor_name; 
			armor_description += red.toString() + "LIFE STEAL: " + hp_regen_percent + "%,";
			// hp_regen == .split(":")[2]
		}


		if(block == true){
			armor_data += "crit=" + block_percent + ":";
			armor_name = "Deadly " + armor_name;
			armor_description += red.toString() + "CRITICAL HIT: " + block_percent + "%,";
		}


		if(hp_increase == true){
			armor_data += "edmg=" + element_dmg_type + "," + element_dmg_amount + ":";

			if(dodge == true){
				armor_name = armor_name + " " + element_dmg_type.substring(0, 1).toUpperCase() + element_dmg_type.substring(1, element_dmg_type.length());
			}
			if(dodge == false){
				armor_name = armor_name + " of " + element_dmg_type.substring(0, 1).toUpperCase() + element_dmg_type.substring(1, element_dmg_type.length());
			}

			armor_description += red.toString() + element_dmg_type.toUpperCase() + " DMG: +" + element_dmg_amount + ",";
			// hp_increase == .split(":")[3].split(",")[1]
			// edmg=fire,3
		}*/

		armor_description += "," + rarity;
		
		if(enchant_count > 0){
			armor_name = ChatColor.RED + "[+" + enchant_count + "]" + " " + tag.toString() + armor_name;;
		}
		else{
			armor_name = tag.toString() + armor_name;
		}
		//log.info(armor_name + " = " + armor_data);

		return ItemMechanics.signCustomItem(m, (short)0, armor_name, armor_description);
	}

	public static ItemStack ChestPlateGenerator(int tier, boolean reroll, ItemStack original){
		Random r = new Random();
		Material m = null;
		boolean hp_regen = false, /* hp_increase = false, */ energy_regen = false, block = false, dodge = false, thorns = false, reflection = false, gem_find = false, item_find = false;
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

		if(tier == 1){
			armor_name = "Leather Chestplate";
			tag = ChatColor.WHITE;
			m = Material.LEATHER_CHESTPLATE;

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
			block_min = 1;
			dodge_min = 1;
			thorns_min = 1; // 0.1%
			reflection_min = 1; // 0.1%
			gem_find_min = 1; // 0.1%
			item_find_min = 1; // 0.1%

			hp_regen_max = 15;
			energy_regen_max = 5;
			block_max = 5;
			dodge_max = 5;
			thorns_max = 2;
			reflection_max = 1;
			gem_find_max = 5;
			item_find_max = 1;

		}
		if(tier == 2){
			armor_name = "Chainmail";
			tag = ChatColor.GREEN;
			m = Material.CHAINMAIL_CHESTPLATE;

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
			block_min = 1;
			dodge_min = 1;
			thorns_min = 1; 
			reflection_min = 1; 
			gem_find_min = 1; 
			item_find_min = 1; // 0.1%

			hp_regen_max = 25; 
			energy_regen_max = 7;
			block_max = 8;
			dodge_max = 8;
			thorns_max = 3;
			reflection_max = 2;
			gem_find_max = 8;
			item_find_max = 2;
		}
		if(tier == 3){
			armor_name = "Platemail";
			tag = ChatColor.AQUA;
			m = Material.IRON_CHESTPLATE;

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
			block_min = 1;
			dodge_min = 1;
			thorns_min = 1; 
			reflection_min = 1; 
			gem_find_min = 3; 
			item_find_min = 1; // 0.1%

			hp_regen_max = 55;
			energy_regen_max = 9;
			block_max = 10;
			dodge_max = 10;
			thorns_max = 5;
			reflection_max = 4;
			gem_find_max = 15;
			item_find_max = 3;
		}
		if(tier == 4){
			armor_name = "Magic Platemail";
			tag = ChatColor.LIGHT_PURPLE;
			m = Material.DIAMOND_CHESTPLATE;

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
			block_min = 1;
			dodge_min = 1;
			thorns_min = 2; 
			reflection_min = 2; 
			gem_find_min = 5; 
			item_find_min = 1; // 0.1%

			hp_regen_max = 75;
			energy_regen_max = 12;
			block_max = 12;
			dodge_max = 12;
			thorns_max = 9;
			reflection_max = 5;
			gem_find_max = 20;
			item_find_max = 4;
		}
		if(tier == 5){
			armor_name = "Legendary Platemail";
			tag = ChatColor.YELLOW;
			m = Material.GOLD_CHESTPLATE;

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
			block_min = 1;
			dodge_min = 1;
			thorns_min = 2; 
			reflection_min = 2; 
			gem_find_min = 5; 
			item_find_min = 1; // 0.1%

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
		if(armor_range_check <= 80){ 
			rarity = ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + "Common";
			if(tier == 1){
				//int min_num = 1 + 1;
				//int max_num = 2 + 1;

				min_armor = 1;
				max_armor = 1;

				int min_hp = 10;
				int max_hp = 20;

				hp_increase_amount = r.nextInt(max_hp - min_hp) + min_hp;
			}
			if(tier == 2){
				int min_num = 1 + 1;
				int max_num = 2 + 1;

				min_armor = r.nextInt(max_num - min_num) + (min_num - 1);
				max_armor = r.nextInt(max_num - min_armor) + min_armor;

				int min_hp = 60;
				int max_hp = 80;

				hp_increase_amount = r.nextInt(max_hp - min_hp) + min_hp;
			}
			if(tier == 3){
				int min_num = 5 + 1;
				int max_num = 6 + 1;


				min_armor = r.nextInt(max_num - min_num) + (min_num - 1);
				max_armor = r.nextInt(max_num - min_armor) + min_armor;

				int min_hp = 200;
				int max_hp = 350;

				hp_increase_amount = r.nextInt(max_hp - min_hp) + min_hp;
			}
			if(tier == 4){
				int min_num = 8 + 1;
				int max_num = 9 + 1;

				min_armor = r.nextInt(max_num - min_num) + (min_num - 1);
				max_armor = r.nextInt(max_num - min_armor) + min_armor;

				int min_hp = 600;
				int max_hp = 800;

				hp_increase_amount = r.nextInt(max_hp - min_hp) + min_hp;
			}
			if(tier == 5){
				int min_num = 11 + 1;
				int max_num = 12 + 1;

				min_armor = r.nextInt(max_num - min_num) + (min_num - 1);
				max_armor = r.nextInt(max_num - min_armor) + min_armor;

				int min_hp = 1500;
				int max_hp = 2500;

				hp_increase_amount = r.nextInt(max_hp - min_hp) + min_hp;
			}
		}

		if(armor_range_check > 80 && armor_range_check < 95){
			rarity = ChatColor.GREEN.toString() + ChatColor.ITALIC.toString() + "Uncommon";
			if(tier == 1){
				int min_num = 1 + 1;
				int max_num = 2 + 1;

				min_armor = r.nextInt(max_num - min_num) + (min_num - 1);
				max_armor = r.nextInt(max_num - min_armor) + min_armor;

				int min_hp = 20;
				int max_hp = 50;

				hp_increase_amount = r.nextInt(max_hp - min_hp) + min_hp;
			}
			if(tier == 2){
				int min_num = 3 + 1;
				int max_num = 4 + 1;

				min_armor = r.nextInt(max_num - min_num) + (min_num - 1);
				max_armor = r.nextInt(max_num - min_armor) + min_armor;

				int min_hp = 100;
				int max_hp = 250;

				hp_increase_amount = r.nextInt(max_hp - min_hp) + min_hp;
			}
			if(tier == 3){
				int min_num = 6 + 1;
				int max_num = 9 + 1;


				min_armor = r.nextInt(max_num - min_num) + (min_num - 1);
				max_armor = r.nextInt(max_num - min_armor) + min_armor;

				int min_hp = 350;
				int max_hp = 600;

				hp_increase_amount = r.nextInt(max_hp - min_hp) + min_hp;
			}
			if(tier == 4){
				int min_num = 10 + 1;
				int max_num = 11 + 1;

				min_armor = r.nextInt(max_num - min_num) + (min_num - 1);
				max_armor = r.nextInt(max_num - min_armor) + min_armor;

				int min_hp = 800;
				int max_hp = 1300;

				hp_increase_amount = r.nextInt(max_hp - min_hp) + min_hp;
			}
			if(tier == 5){
				int min_num = 13 + 1;
				int max_num = 14 + 1;

				min_armor = r.nextInt(max_num - min_num) + (min_num - 1);
				max_armor = r.nextInt(max_num - min_armor) + min_armor;

				int min_hp = 2500;
				int max_hp = 3600;

				hp_increase_amount = r.nextInt(max_hp - min_hp) + min_hp;
			}
		}

		if(armor_range_check >= 95 && armor_range_check != 99){
			rarity = ChatColor.AQUA.toString() + ChatColor.ITALIC.toString() + "Rare";
			if(tier == 1){
				int min_num = 1 + 1;
				int max_num = 2 + 1;

				min_armor = r.nextInt(max_num - min_num) + (min_num - 1);
				max_armor = r.nextInt(max_num - min_armor) + min_armor;

				int min_hp = 80;
				int max_hp = 120;

				hp_increase_amount = r.nextInt(max_hp - min_hp) + min_hp;
			}
			if(tier == 2){
				int min_num = 5 + 1;
				int max_num = 6 + 1;

				min_armor = r.nextInt(max_num - min_num) + (min_num - 1);
				max_armor = r.nextInt(max_num - min_armor) + min_armor;

				int min_hp = 250;
				int max_hp = 350;

				hp_increase_amount = r.nextInt(max_hp - min_hp) + min_hp;
			}
			if(tier == 3){
				int min_num = 8 + 1;
				int max_num = 9 + 1;


				min_armor = r.nextInt(max_num - min_num) + (min_num - 1);
				max_armor = r.nextInt(max_num - min_armor) + min_armor;

				int min_hp = 600;
				int max_hp = 800;

				hp_increase_amount = r.nextInt(max_hp - min_hp) + min_hp;
			}
			if(tier == 4){
				int min_num = 11 + 1;
				int max_num = 12 + 1;

				min_armor = r.nextInt(max_num - min_num) + (min_num - 1);
				max_armor = r.nextInt(max_num - min_armor) + min_armor;

				int min_hp = 1300;
				int max_hp = 2400;

				hp_increase_amount = r.nextInt(max_hp - min_hp) + min_hp;
			}
			if(tier == 5){
				int min_num = 16 + 1;
				int max_num = 17 + 1;

				min_armor = r.nextInt(max_num - min_num) + (min_num - 1);
				max_armor = r.nextInt(max_num - min_armor) + min_armor;

				int min_hp = 3600;
				int max_hp = 5400;

				hp_increase_amount = r.nextInt(max_hp - min_hp) + min_hp;
			}
		}

		if(armor_range_check == 99){
			rarity = ChatColor.YELLOW.toString() + ChatColor.ITALIC.toString() + "Unique";
			if(tier == 1){
				int min_num = 1 + 1;
				int max_num = 2 + 1;

				min_armor = r.nextInt(max_num - min_num) + (min_num - 1);
				max_armor = r.nextInt(max_num - min_armor) + min_armor;

				int min_hp = 80;
				int max_hp = 120;

				hp_increase_amount = r.nextInt(max_hp - min_hp) + min_hp;
			}
			if(tier == 2){
				int min_num = 5 + 1;
				int max_num = 6 + 1;

				min_armor = r.nextInt(max_num - min_num) + (min_num - 1);
				max_armor = r.nextInt(max_num - min_armor) + min_armor;

				int min_hp = 250;
				int max_hp = 350;

				hp_increase_amount = r.nextInt(max_hp - min_hp) + min_hp;
			}
			if(tier == 3){
				int min_num = 8 + 1;
				int max_num = 9 + 1;


				min_armor = r.nextInt(max_num - min_num) + (min_num - 1);
				max_armor = r.nextInt(max_num - min_armor) + min_armor;

				int min_hp = 600;
				int max_hp = 800;

				hp_increase_amount = r.nextInt(max_hp - min_hp) + min_hp;
			}
			if(tier == 4){
				int min_num = 11 + 1;
				int max_num = 12 + 1;

				min_armor = r.nextInt(max_num - min_num) + (min_num - 1);
				max_armor = r.nextInt(max_num - min_armor) + min_armor;

				int min_hp = 1300;
				int max_hp = 2400;

				hp_increase_amount = r.nextInt(max_hp - min_hp) + min_hp;
			}
			if(tier == 5){
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

		if(min_armor == 0){
			min_armor = 1;
		}

		if(max_armor == 0){
			min_armor = 1;
			max_armor = 1;
		}

		int regen_type = r.nextInt(2); // 0, 1.

		if(regen_type == 0){  // HP/s HP REGEN
			hp_regen = true;
		//hp_regen_max = (int) Math.round((hp_increase_amount / 10) + (hp_increase_amount * 0.05));
		//if(hp_regen_max < 1){hp_regen_max = 1;}
			double regen_val = hp_increase_amount * 0.05;
			if(regen_val < 1){regen_val = 1;}

			int real_hp_regen_max = (int) ((regen_val) + (hp_increase_amount / 10)) - r.nextInt((int) Math.round(regen_val));
			if(real_hp_regen_max < 1){real_hp_regen_max = 1;}
			//hp_regen_percent = r.nextInt(real_hp_regen_max) + 1;
			hp_regen_percent = r.nextInt(hp_regen_max - hp_regen_min) + hp_regen_min;
		}
		else if(regen_type == 1){
			energy_regen = true;
			energy_regen_percent = r.nextInt(energy_regen_max - energy_regen_min) + energy_regen_min; 
			energy_regen_percent = energy_regen_percent / 2;

			if(energy_regen_percent < 1){
				energy_regen_percent = 1;
			}
		}

		if(r.nextInt(100) <= block_chance){ 
			block = true;
			block_percent = r.nextInt(block_max - block_min) + block_min; // 1 - 10%
		}

		if(r.nextInt(100) <= dodge_chance){ // 1% chance.
			dodge = true;
			dodge_percent = r.nextInt(dodge_max - dodge_min) + dodge_min; // 1 - 3%
		}

		if(r.nextInt(100) <= thorns_chance){ // 1% chance.
			thorns = true;
			thorns_percent = r.nextInt(thorns_max - thorns_min) + thorns_min; // 1 - 3%
		}

		if(r.nextInt(100) <= reflection_chance){ // 1% chance.
			reflection = true;
			try{
				reflection_percent = r.nextInt(reflection_max - reflection_min) + reflection_min; // 1 - 3%
			} catch(IllegalArgumentException iae){
				reflection_percent = reflection_min;
			}
		}

		if(r.nextInt(100) <= gem_find_chance){ // 1% chance.			
			gem_find = true;
			gem_find_percent = r.nextInt(gem_find_max - gem_find_min) + gem_find_min; // 1 - 3%
		}

		if(r.nextInt(1000) <= item_find_chance){ // 1% chance.
			item_find = true;
			if(item_find_max - item_find_min <= 0){
				item_find_max = 2;
				item_find_min = 1;
			}

			item_find_percent = r.nextInt(item_find_max - item_find_min) + item_find_min; // 1 - 3%
		}

		// x = durability
		int armor_type = new Random().nextInt(2); // 0 or 1.

		@SuppressWarnings("unused")
		String armor_data = "";
		int enchant_count = 0;
		if(reroll == true && original != null){
			rarity = ItemMechanics.getItemRarity(original);
			enchant_count = EnchantMechanics.getEnchantCount(original);
			armor_type = 2; // Ignore the normal check.
			List<Integer> vals = new ArrayList<Integer>();
			if(ItemMechanics.getDmgVal(original, false) != null){
				vals = ItemMechanics.getDmgVal(original, false);
				min_armor = vals.get(0);
				max_armor = vals.get(1);
				armor_data = m.name().toUpperCase() + "#" + min_armor + "-" + max_armor + ":";
				armor_description += red.toString() + "DPS: " + min_armor + " - " + max_armor + "%,";	
			}
			if(ItemMechanics.getArmorVal(original, false) != null){
				vals = ItemMechanics.getArmorVal(original, false);
				min_armor = vals.get(0);
				max_armor = vals.get(1);
				armor_data = m.name().toUpperCase() + "#" + min_armor + "-" + max_armor + ":";
				armor_description += red.toString() + "ARMOR: " + min_armor + " - " + max_armor + "%,";	
			}

			hp_increase_amount = HealthMechanics.getHealthVal(original);
			String oarmor_data = ItemMechanics.getArmorData(original);
			if(oarmor_data.contains("hp_regen")){
				int health_regen_val = Integer.parseInt(oarmor_data.substring(oarmor_data.indexOf("hp_regen=") + 9, oarmor_data.indexOf("@hp_regen_split@")));
				hp_regen = true;
				energy_regen = false;
				hp_regen_percent = health_regen_val;
			}
			if(oarmor_data.contains("energy_regen")){
				int energy_regen_val = Integer.parseInt(oarmor_data.substring(oarmor_data.indexOf("energy_regen=") + 13, oarmor_data.indexOf("@energy_regen_split@")));
				hp_regen = false;
				energy_regen = true;
				energy_regen_percent = energy_regen_val;
			}
		}
		if(armor_type == 0){
			armor_data = m.name().toUpperCase() + "#" + min_armor + "-" + max_armor + ":";
			armor_description += red.toString() + "ARMOR: " + min_armor + " - " + max_armor + "%,";	
		}
		if(armor_type == 1){
			armor_data = m.name().toUpperCase() + "#" + min_armor + "-" + max_armor + ":";
			armor_description += red.toString() + "DPS: " + min_armor + " - " + max_armor + "%,";	
		}

		if(hp_increase_amount <= 0){
			hp_increase_amount = 1;
		}

		armor_data += "hp_increase=" + hp_increase_amount + "@";
		//armor_name = armor_name + " of Fortitude";
		armor_description += red.toString() + "HP: +" + hp_increase_amount + ",";

		if(hp_regen == true){
			armor_data += "hp_regen=" + hp_regen_percent + "@hp_regen_split@:";
			armor_description += red.toString() + "HP REGEN: +" + hp_regen_percent + " HP/s,";
		}

		if(energy_regen == true){
			armor_data += "energy_regen=" + energy_regen_percent + ":";
			armor_name = armor_name + " of Fortitude";
			armor_description += red.toString() + "ENERGY REGEN: +" + energy_regen_percent + "%,";
			// Take from first index of energy_regen= to next index of : from that point.
		}

		int stat_chance = r.nextInt(100);
		int stat_type = r.nextInt(4); // 0, 1, 2
		String stat_string = "";
		String short_stat_string = "";
		int stat_val = 1;
		if(tier == 1){
			stat_val = new Random().nextInt(15) + 1;
			if(stat_chance <= 25){
				stat_chance = r.nextInt(100);
				if(stat_type == 0){
					stat_string = "strength";
					short_stat_string = "STR";
				}
				if(stat_type == 1){
					stat_string = "dexterity";
					short_stat_string = "DEX";
				}
				if(stat_type == 2){
					stat_string = "vitality";
					short_stat_string = "VIT";
				}
				if(stat_type == 3){
					stat_string = "intellect";
					short_stat_string = "INT";
				}
				
				armor_data += stat_string + "=" + stat_val + ":";
				armor_description += red.toString() + short_stat_string + ": +" + stat_val + ",";
			}
		}
		if(tier == 2){
			if(stat_chance <= 20){
				stat_val = new Random().nextInt(35) + 1;
				stat_chance = r.nextInt(100);
				if(stat_type == 0){
					stat_string = "strength";
					short_stat_string = "STR";
				}
				if(stat_type == 1){
					stat_string = "dexterity";
					short_stat_string = "DEX";
				}
				if(stat_type == 2){
					stat_string = "vitality";
					short_stat_string = "VIT";
				}
				if(stat_type == 3){
					stat_string = "intellect";
					short_stat_string = "INT";
				}

				armor_data += stat_string + "=" + stat_val + ":";
				armor_description += red.toString() + short_stat_string + ": +" + stat_val + ",";
			}
			if(stat_chance <= 5){
				stat_val = new Random().nextInt(35) + 1;
				stat_chance = r.nextInt(100);
				if(stat_type >= 0 && stat_type < 3){
					stat_type++;
				}
				else if(stat_type == 3){
					stat_type--;
				}

				if(stat_type == 0){
					stat_string = "strength";
					short_stat_string = "STR";
				}
				if(stat_type == 1){
					stat_string = "dexterity";
					short_stat_string = "DEX";
				}
				if(stat_type == 2){
					stat_string = "vitality";
					short_stat_string = "VIT";
				}
				if(stat_type == 3){
					stat_string = "intellect";
					short_stat_string = "INT";
				}

				armor_data += stat_string + "=" + stat_val + ":";
				armor_description += red.toString() + short_stat_string + ": +" + stat_val + ",";
			}
		}
		if(tier == 3){
			if(stat_chance <= 15){
				stat_val = new Random().nextInt(75) + 1;
				stat_chance = r.nextInt(100);
				if(stat_type == 0){
					stat_string = "strength";
					short_stat_string = "STR";
				}
				if(stat_type == 1){
					stat_string = "dexterity";
					short_stat_string = "DEX";
				}
				if(stat_type == 2){
					stat_string = "vitality";
					short_stat_string = "VIT";
				}
				if(stat_type == 3){
					stat_string = "intellect";
					short_stat_string = "INT";
				}

				armor_data += stat_string + "=" + stat_val + ":";
				armor_description += red.toString() + short_stat_string + ": +" + stat_val + ",";
			}
			if(stat_chance <= 5){
				stat_val = new Random().nextInt(75) + 1;
				stat_chance = r.nextInt(100);
				if(stat_type >= 0 && stat_type < 3){
					stat_type++;
				}
				else if(stat_type == 3){
					stat_type = 0;
				}

				if(stat_type == 0){
					stat_string = "strength";
					short_stat_string = "STR";
				}
				if(stat_type == 1){
					stat_string = "dexterity";
					short_stat_string = "DEX";
				}
				if(stat_type == 2){
					stat_string = "vitality";
					short_stat_string = "VIT";
				}
				if(stat_type == 3){
					stat_string = "intellect";
					short_stat_string = "INT";
				}

				armor_data += stat_string + "=" + stat_val + ":";
				armor_description += red.toString() + short_stat_string + ": +" + stat_val + ",";
			}
			if(stat_chance <= 1){
				stat_val = new Random().nextInt(75) + 1;
				stat_chance = r.nextInt(100);
				if(stat_type >= 0 && stat_type < 3){
					stat_type++;
				}
				else if(stat_type == 3){
					stat_type = 0;
				}

				if(stat_type == 0){
					stat_string = "strength";
					short_stat_string = "STR";
				}
				if(stat_type == 1){
					stat_string = "dexterity";
					short_stat_string = "DEX";
				}
				if(stat_type == 2){
					stat_string = "vitality";
					short_stat_string = "VIT";
				}
				if(stat_type == 3){
					stat_string = "intellect";
					short_stat_string = "INT";
				}

				armor_data += stat_string + "=" + stat_val + ":";
				armor_description += red.toString() + short_stat_string + ": +" + stat_val + ",";
			}
		}
		if(tier == 4){
			if(stat_chance <= 15){
				stat_val = new Random().nextInt(115) + 1;
				stat_chance = r.nextInt(100);
				if(stat_type == 0){
					stat_string = "strength";
					short_stat_string = "STR";
				}
				if(stat_type == 1){
					stat_string = "dexterity";
					short_stat_string = "DEX";
				}
				if(stat_type == 2){
					stat_string = "vitality";
					short_stat_string = "VIT";
				}
				if(stat_type == 3){
					stat_string = "intellect";
					short_stat_string = "INT";
				}

				armor_data += stat_string + "=" + stat_val + ":";
				armor_description += red.toString() + short_stat_string + ": +" + stat_val + ",";
			}
			if(stat_chance <= 9){
				stat_val = new Random().nextInt(115) + 1;
				stat_chance = r.nextInt(100);
				if(stat_type >= 0 && stat_type < 3){
					stat_type++;
				}
				else if(stat_type == 3){
					stat_type = 0;
				}

				if(stat_type == 0){
					stat_string = "strength";
					short_stat_string = "STR";
				}
				if(stat_type == 1){
					stat_string = "dexterity";
					short_stat_string = "DEX";
				}
				if(stat_type == 2){
					stat_string = "vitality";
					short_stat_string = "VIT";
				}
				if(stat_type == 3){
					stat_string = "intellect";
					short_stat_string = "INT";
				}

				armor_data += stat_string + "=" + stat_val + ":";
				armor_description += red.toString() + short_stat_string + ": +" + stat_val + ",";
			}
			if(stat_chance <= 4){
				stat_val = new Random().nextInt(115) + 1;
				stat_chance = r.nextInt(100);
				if(stat_type >= 0 && stat_type < 3){
					stat_type++;
				}
				else if(stat_type == 3){
					stat_type = 0;
				}

				if(stat_type == 0){
					stat_string = "strength";
					short_stat_string = "STR";
				}
				if(stat_type == 1){
					stat_string = "dexterity";
					short_stat_string = "DEX";
				}
				if(stat_type == 2){
					stat_string = "vitality";
					short_stat_string = "VIT";
				}
				if(stat_type == 3){
					stat_string = "intellect";
					short_stat_string = "INT";
				}

				armor_data += stat_string + "=" + stat_val + ":";
				armor_description += red.toString() + short_stat_string + ": +" + stat_val + ",";
			}
		}
		if(tier == 5){
			if(stat_chance <= 20){
				stat_val = new Random().nextInt(315) + 1;
				stat_chance = r.nextInt(100);
				if(stat_type == 0){
					stat_string = "strength";
					short_stat_string = "STR";
				}
				if(stat_type == 1){
					stat_string = "dexterity";
					short_stat_string = "DEX";
				}
				if(stat_type == 2){
					stat_string = "vitality";
					short_stat_string = "VIT";
				}
				if(stat_type == 3){
					stat_string = "intellect";
					short_stat_string = "INT";
				}

				armor_data += stat_string + "=" + stat_val + ":";
				armor_description += red.toString() + short_stat_string + ": +" + stat_val + ",";
			}
			if(stat_chance <= 10){
				stat_val = new Random().nextInt(315) + 1;
				stat_chance = r.nextInt(100);
				if(stat_type >= 0 && stat_type < 3){
					stat_type++;
				}
				else if(stat_type == 3){
					stat_type = 0;
				}

				if(stat_type == 0){
					stat_string = "strength";
					short_stat_string = "STR";
				}
				if(stat_type == 1){
					stat_string = "dexterity";
					short_stat_string = "DEX";
				}
				if(stat_type == 2){
					stat_string = "vitality";
					short_stat_string = "VIT";
				}
				if(stat_type == 3){
					stat_string = "intellect";
					short_stat_string = "INT";
				}

				armor_data += stat_string + "=" + stat_val + ":";
				armor_description += red.toString() + short_stat_string + ": +" + stat_val + ",";
			}
			if(stat_chance <= 5){
				stat_val = new Random().nextInt(315) + 1;
				if(stat_type >= 0 && stat_type < 3){
					stat_type++;
				}
				else if(stat_type == 3){
					stat_type = 0;
				}

				if(stat_type == 0){
					stat_string = "strength";
					short_stat_string = "STR";
				}
				if(stat_type == 1){
					stat_string = "dexterity";
					short_stat_string = "DEX";
				}
				if(stat_type == 2){
					stat_string = "vitality";
					short_stat_string = "VIT";
				}
				if(stat_type == 3){
					stat_string = "intellect";
					short_stat_string = "INT";
				}

				armor_data += stat_string + "=" + stat_val + ":";
				armor_description += red.toString() + short_stat_string + ": +" + stat_val + ",";
			}
		}

		// Resistances
		int res_chance = r.nextInt(100);
		int res_type = r.nextInt(3); // 0-fire, 1-ice, 2-poison
		String res_string = "";
		String res_shorthand = "";
		int res_val = -1;

		if(tier == 1){
			if(res_chance <= 15){
				res_val = new Random().nextInt(5) + 1;
			}
		}
		if(tier == 2){
			if(res_chance <= 15){
				res_val = new Random().nextInt(7) + 1;
			}
		}
		if(tier == 3){
			if(res_chance <= 25){
				res_val = new Random().nextInt(20) + 1;
			}
		}
		if(tier == 4){
			if(res_chance <= 20){
				res_val = new Random().nextInt(32) + 1;
			}
		}
		if(tier == 5){
			if(res_chance <= 30){
				res_val = new Random().nextInt(45) + 1;
			}
		}

		if(res_type == 0){
			res_string = "fire_resistance";
			res_shorthand = "FIRE";
		}
		if(res_type == 1){
			res_string = "ice_resistance";
			res_shorthand = "ICE";
		}
		if(res_type == 2){
			res_string = "poison_resistance";
			res_shorthand = "POISON";
		}

		if(res_val > 0){
			// We have some resistance.
			armor_data += res_string + "=" + res_val + ":";
			armor_description += red.toString() + res_shorthand + " RESISTANCE: " + res_val + "%,";
			if(!armor_name.contains("of")){
				armor_name = armor_name + " of ";
			}
			else{
				armor_name = armor_name + " and ";
			}
			
			
			if(res_type == 0){
				armor_name = armor_name + "Fire Resist";
			}
			if(res_type == 1){
				armor_name = armor_name + "Ice Resist";
			}
			if(res_type == 2){
				armor_name = armor_name + "Poison Resist";
			}
		}

		if(dodge == true){
			armor_data += "dodge=" + dodge_percent + ":";;
			armor_name = "Agile " + armor_name;
			armor_description += red.toString() + "DODGE: " + dodge_percent + "%,";
		}

		if(reflection == true){
			armor_data += "reflection=" + reflection_percent + ":";;
			armor_name = "Reflective " + armor_name;
			armor_description += red.toString() + "REFLECTION: " + reflection_percent + "%,";
		}

		if(hp_regen == true){
			armor_name = "Mending " + armor_name;
		}

		if(block == true){
			armor_data += "block=" + block_percent + ":";;
			armor_name = "Protective " + armor_name;
			armor_description += red.toString() + "BLOCK: " + block_percent + "%,";
		}

		if(gem_find == true){
			armor_data += "gem_find=" + gem_find_percent + ":";;
			if(armor_name.contains("of")){
				armor_name = armor_name + " Golden";
			}
			else{
				armor_name = armor_name + " of Pickpocketing";
			}
			armor_description += red.toString() + "GEM FIND: " + gem_find_percent + "%,";
		}

		if(item_find == true){
			armor_data += "item_find=" + item_find_percent;
			if(armor_name.contains("of")){
				armor_name = armor_name + " Treasure";
			}	
			else{
				armor_name = armor_name + " of Treasure";
			}
			armor_description += red.toString() + "ITEM FIND: " + item_find_percent + "%,";
		}


		if(thorns == true){
			armor_data += "thorns=" + thorns_percent + ":";;
			if(armor_name.contains("of")){
				armor_name = armor_name + " Spikes";
			}
			else{
				armor_name = armor_name + " of Thorns";
			}
			armor_description += red.toString() + "THORNS: " + thorns_percent + "% DMG";
		}



		/*if(hp_regen == true){
			armor_data += "ls=" + hp_regen_percent + ":";
			armor_name = "Lifestealing " + armor_name; 
			armor_description += red.toString() + "LIFE STEAL: " + hp_regen_percent + "%,";
			// hp_regen == .split(":")[2]
		}


		if(block == true){
			armor_data += "crit=" + block_percent + ":";
			armor_name = "Deadly " + armor_name;
			armor_description += red.toString() + "CRITICAL HIT: " + block_percent + "%,";
		}


		if(hp_increase == true){
			armor_data += "edmg=" + element_dmg_type + "," + element_dmg_amount + ":";

			if(dodge == true){
				armor_name = armor_name + " " + element_dmg_type.substring(0, 1).toUpperCase() + element_dmg_type.substring(1, element_dmg_type.length());
			}
			if(dodge == false){
				armor_name = armor_name + " of " + element_dmg_type.substring(0, 1).toUpperCase() + element_dmg_type.substring(1, element_dmg_type.length());
			}

			armor_description += red.toString() + element_dmg_type.toUpperCase() + " DMG: +" + element_dmg_amount + ",";
			// hp_increase == .split(":")[3].split(",")[1]
			// edmg=fire,3
		}*/

		armor_description += "," + rarity;
		
		if(enchant_count > 0){
			armor_name = ChatColor.RED + "[+" + enchant_count + "]" + " " + tag.toString() + armor_name;;
		}
		else{
			armor_name = tag.toString() + armor_name;
		}
		//log.info(armor_name + " = " + armor_data);

		return ItemMechanics.signCustomItem(m, (short)0, armor_name, armor_description);
	}

	public static ItemStack BootGenerator(int tier, boolean reroll, ItemStack original){
		Random r = new Random();
		Material m = null;
		boolean hp_regen = false, /* hp_increase = false, */ energy_regen = false, block = false, dodge = false, thorns = false, reflection = false, gem_find = false, item_find = false, speed_boost = false;
		int hp_regen_percent = 0;
		int hp_increase_amount = 0;
		int energy_regen_percent = 0;
		int block_percent = 0;
		int dodge_percent = 0;
		int thorns_percent = 0;
		int reflection_percent = 0;
		int gem_find_percent = 0;
		int item_find_percent = 0;
		float speed_level = 0;

		//int hp_regen_chance = 0;
		//int hp_increase_chance = 0;
		//int energy_regen_chance = 0;
		int block_chance = 0;
		int dodge_chance = 0;
		int thorns_chance = 0;
		int reflection_chance = 0;
		int gem_find_chance = 0;
		int item_find_chance = 0;
		//int speed_boost_chance = 0;

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

		if(tier == 1){
			armor_name = "Leather Boots";
			tag = ChatColor.WHITE;
			m = Material.LEATHER_BOOTS;

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
			block_min = 1;
			dodge_min = 1;
			thorns_min = 1; // 0.1%
			reflection_min = 1; // 0.1%
			gem_find_min = 1; // 0.1%
			item_find_min = 1; // 0.1%

			hp_regen_max = 15;
			energy_regen_max = 5;
			block_max = 5;
			dodge_max = 5;
			thorns_max = 2;
			reflection_max = 2;
			gem_find_max = 5;
			item_find_max = 2;

		}
		if(tier == 2){
			armor_name = "Chainmail Boots";
			tag = ChatColor.GREEN;
			m = Material.CHAINMAIL_BOOTS;

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
			block_min = 1;
			dodge_min = 1;
			thorns_min = 1; 
			reflection_min = 1; 
			gem_find_min = 1; 
			item_find_min = 1; // 0.1%

			hp_regen_max = 25; 
			energy_regen_max = 7;
			block_max = 8;
			dodge_max = 8;
			thorns_max = 3;
			reflection_max = 2;
			gem_find_max = 8;
			item_find_max = 2;
		}
		if(tier == 3){
			armor_name = "Platemail Boots";
			tag = ChatColor.AQUA;
			m = Material.IRON_BOOTS;

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
			block_min = 1;
			dodge_min = 1;
			thorns_min = 1; 
			reflection_min = 1; 
			gem_find_min = 3; 
			item_find_min = 1; // 0.1%

			hp_regen_max = 55;
			energy_regen_max = 9;
			block_max = 10;
			dodge_max = 10;
			thorns_max = 5;
			reflection_max = 4;
			gem_find_max = 15;
			item_find_max = 3;
		}
		if(tier == 4){
			armor_name = "Magic Platemail Boots";
			tag = ChatColor.LIGHT_PURPLE;
			m = Material.DIAMOND_BOOTS;

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
			block_min = 1;
			dodge_min = 1;
			thorns_min = 2; 
			reflection_min = 2; 
			gem_find_min = 5; 
			item_find_min = 1; // 0.1%

			hp_regen_max = 75;
			energy_regen_max = 12;
			block_max = 12;
			dodge_max = 12;
			thorns_max = 9;
			reflection_max = 5;
			gem_find_max = 20;
			item_find_max = 4;
		}
		if(tier == 5){
			armor_name = "Legendary Platemail Boots";
			tag = ChatColor.YELLOW;
			m = Material.GOLD_BOOTS;

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
			block_min = 1;
			dodge_min = 1;
			thorns_min = 2; 
			reflection_min = 2; 
			gem_find_min = 5; 
			item_find_min = 1; // 0.1%

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
		if(armor_range_check <= 80){ 
			rarity = ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + "Common";
			if(tier == 1){
				//int min_num = 1 + 1;
				//int max_num = 2 + 1;

				min_armor = 1;
				max_armor = 1;

				int min_hp = 10;
				int max_hp = 20;

				hp_increase_amount = r.nextInt(max_hp - min_hp) + min_hp;
			}
			if(tier == 2){
				int min_num = 1 + 1;
				int max_num = 2 + 1;

				min_armor = r.nextInt(max_num - min_num) + (min_num - 1);
				max_armor = r.nextInt(max_num - min_armor) + min_armor;

				int min_hp = 60;
				int max_hp = 80;

				hp_increase_amount = r.nextInt(max_hp - min_hp) + min_hp;
			}
			if(tier == 3){
				int min_num = 5 + 1;
				int max_num = 6 + 1;


				min_armor = r.nextInt(max_num - min_num) + (min_num - 1);
				max_armor = r.nextInt(max_num - min_armor) + min_armor;

				int min_hp = 200;
				int max_hp = 350;

				hp_increase_amount = r.nextInt(max_hp - min_hp) + min_hp;
			}
			if(tier == 4){
				int min_num = 8 + 1;
				int max_num = 9 + 1;

				min_armor = r.nextInt(max_num - min_num) + (min_num - 1);
				max_armor = r.nextInt(max_num - min_armor) + min_armor;

				int min_hp = 600;
				int max_hp = 800;

				hp_increase_amount = r.nextInt(max_hp - min_hp) + min_hp;
			}
			if(tier == 5){
				int min_num = 11 + 1;
				int max_num = 12 + 1;

				min_armor = r.nextInt(max_num - min_num) + (min_num - 1);
				max_armor = r.nextInt(max_num - min_armor) + min_armor;

				int min_hp = 1500;
				int max_hp = 2500;

				hp_increase_amount = r.nextInt(max_hp - min_hp) + min_hp;
			}
		}

		if(armor_range_check > 80 && armor_range_check < 95){
			rarity = ChatColor.GREEN.toString() + ChatColor.ITALIC.toString() + "Uncommon";
			if(tier == 1){
				int min_num = 1 + 1;
				int max_num = 2 + 1;

				min_armor = r.nextInt(max_num - min_num) + (min_num - 1);
				max_armor = r.nextInt(max_num - min_armor) + min_armor;

				int min_hp = 20;
				int max_hp = 50;

				hp_increase_amount = r.nextInt(max_hp - min_hp) + min_hp;
			}
			if(tier == 2){
				int min_num = 3 + 1;
				int max_num = 4 + 1;

				min_armor = r.nextInt(max_num - min_num) + (min_num - 1);
				max_armor = r.nextInt(max_num - min_armor) + min_armor;

				int min_hp = 100;
				int max_hp = 250;

				hp_increase_amount = r.nextInt(max_hp - min_hp) + min_hp;
			}
			if(tier == 3){
				int min_num = 6 + 1;
				int max_num = 9 + 1;


				min_armor = r.nextInt(max_num - min_num) + (min_num - 1);
				max_armor = r.nextInt(max_num - min_armor) + min_armor;

				int min_hp = 350;
				int max_hp = 600;

				hp_increase_amount = r.nextInt(max_hp - min_hp) + min_hp;
			}
			if(tier == 4){
				int min_num = 10 + 1;
				int max_num = 11 + 1;

				min_armor = r.nextInt(max_num - min_num) + (min_num - 1);
				max_armor = r.nextInt(max_num - min_armor) + min_armor;

				int min_hp = 800;
				int max_hp = 1300;

				hp_increase_amount = r.nextInt(max_hp - min_hp) + min_hp;
			}
			if(tier == 5){
				int min_num = 13 + 1;
				int max_num = 14 + 1;

				min_armor = r.nextInt(max_num - min_num) + (min_num - 1);
				max_armor = r.nextInt(max_num - min_armor) + min_armor;

				int min_hp = 2500;
				int max_hp = 3600;

				hp_increase_amount = r.nextInt(max_hp - min_hp) + min_hp;
			}
		}

		if(armor_range_check >= 95 && armor_range_check != 99){
			rarity = ChatColor.AQUA.toString() + ChatColor.ITALIC.toString() + "Rare";
			if(tier == 1){
				int min_num = 1 + 1;
				int max_num = 2 + 1;

				min_armor = r.nextInt(max_num - min_num) + (min_num - 1);
				max_armor = r.nextInt(max_num - min_armor) + min_armor;

				int min_hp = 80;
				int max_hp = 120;

				hp_increase_amount = r.nextInt(max_hp - min_hp) + min_hp;
			}
			if(tier == 2){
				int min_num = 5 + 1;
				int max_num = 6 + 1;

				min_armor = r.nextInt(max_num - min_num) + (min_num - 1);
				max_armor = r.nextInt(max_num - min_armor) + min_armor;

				int min_hp = 250;
				int max_hp = 350;

				hp_increase_amount = r.nextInt(max_hp - min_hp) + min_hp;
			}
			if(tier == 3){
				int min_num = 8 + 1;
				int max_num = 9 + 1;


				min_armor = r.nextInt(max_num - min_num) + (min_num - 1);
				max_armor = r.nextInt(max_num - min_armor) + min_armor;

				int min_hp = 600;
				int max_hp = 800;

				hp_increase_amount = r.nextInt(max_hp - min_hp) + min_hp;
			}
			if(tier == 4){
				int min_num = 11 + 1;
				int max_num = 12 + 1;

				min_armor = r.nextInt(max_num - min_num) + (min_num - 1);
				max_armor = r.nextInt(max_num - min_armor) + min_armor;

				int min_hp = 1300;
				int max_hp = 2400;

				hp_increase_amount = r.nextInt(max_hp - min_hp) + min_hp;
			}
			if(tier == 5){
				int min_num = 16 + 1;
				int max_num = 17 + 1;

				min_armor = r.nextInt(max_num - min_num) + (min_num - 1);
				max_armor = r.nextInt(max_num - min_armor) + min_armor;

				int min_hp = 3600;
				int max_hp = 5400;

				hp_increase_amount = r.nextInt(max_hp - min_hp) + min_hp;
			}
		}

		if(armor_range_check == 99){
			rarity = ChatColor.YELLOW.toString() + ChatColor.ITALIC.toString() + "Unique";
			if(tier == 1){
				int min_num = 1 + 1;
				int max_num = 2 + 1;

				min_armor = r.nextInt(max_num - min_num) + (min_num - 1);
				max_armor = r.nextInt(max_num - min_armor) + min_armor;

				int min_hp = 80;
				int max_hp = 120;

				hp_increase_amount = r.nextInt(max_hp - min_hp) + min_hp;
			}
			if(tier == 2){
				int min_num = 5 + 1;
				int max_num = 6 + 1;

				min_armor = r.nextInt(max_num - min_num) + (min_num - 1);
				max_armor = r.nextInt(max_num - min_armor) + min_armor;

				int min_hp = 250;
				int max_hp = 350;

				hp_increase_amount = r.nextInt(max_hp - min_hp) + min_hp;
			}
			if(tier == 3){
				int min_num = 8 + 1;
				int max_num = 9 + 1;


				min_armor = r.nextInt(max_num - min_num) + (min_num - 1);
				max_armor = r.nextInt(max_num - min_armor) + min_armor;

				int min_hp = 600;
				int max_hp = 800;

				hp_increase_amount = r.nextInt(max_hp - min_hp) + min_hp;
			}
			if(tier == 4){
				int min_num = 11 + 1;
				int max_num = 12 + 1;

				min_armor = r.nextInt(max_num - min_num) + (min_num - 1);
				max_armor = r.nextInt(max_num - min_armor) + min_armor;

				int min_hp = 1300;
				int max_hp = 2400;

				hp_increase_amount = r.nextInt(max_hp - min_hp) + min_hp;
			}
			if(tier == 5){
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

		double temp_min_armor = (double)min_armor;
		double temp_max_armor = (double)max_armor;

		double temp_hp_increase_amount = (double)hp_increase_amount;

		min_armor = (int)(temp_min_armor * 0.50); // 50% of the values of chestplate / leggings.
		max_armor = (int)(temp_max_armor * 0.50); // 50% of the values of chestplate / leggings.

		hp_increase_amount = (int)(temp_hp_increase_amount * 0.50); // 50% of HP increase value.

		if(min_armor == 0){
			min_armor = 1;
		}

		if(max_armor == 0){
			min_armor = 1;
			max_armor = 1;
		}

		int regen_type = r.nextInt(2); // 0, 1.

		if(regen_type == 0){  // HP/s HP REGEN
			hp_regen = true;
			//hp_regen_max = (int) Math.round((hp_increase_amount / 10) + (hp_increase_amount * 0.05));
			//if(hp_regen_max < 1){hp_regen_max = 1;}
			double regen_val = hp_increase_amount * 0.05;
			if(regen_val < 1){regen_val = 1;}

			int real_hp_regen_max = (int) ((regen_val) + (hp_increase_amount / 10)) - r.nextInt((int) Math.round(regen_val));
			if(real_hp_regen_max < 1){real_hp_regen_max = 1;}
			//hp_regen_percent = r.nextInt(real_hp_regen_max) + 1;
			hp_regen_percent = r.nextInt(hp_regen_max - hp_regen_min) + hp_regen_min;
		}
		else if(regen_type == 1){
			energy_regen = true;
			energy_regen_percent = r.nextInt(energy_regen_max - energy_regen_min) + energy_regen_min; 
			energy_regen_percent = energy_regen_percent / 2;

			if(energy_regen_percent < 1){
				energy_regen_percent = 1;
			}
		}

		if(r.nextInt(100) <= block_chance){ 
			block = true;
			block_percent = r.nextInt(block_max - block_min) + block_min; // 1 - 10%
		}

		if(r.nextInt(100) <= dodge_chance){ // 1% chance.
			dodge = true;
			dodge_percent = r.nextInt(dodge_max - dodge_min) + dodge_min; // 1 - 3%
		}

		if(r.nextInt(100) <= thorns_chance){ // 1% chance.
			thorns = true;
			thorns_percent = r.nextInt(thorns_max - thorns_min) + thorns_min; // 1 - 3%
		}

		if(r.nextInt(100) <= reflection_chance){ // 1% chance.
			reflection = true;
			try{
				reflection_percent = r.nextInt(reflection_max - reflection_min) + reflection_min; // 1 - 3%
			} catch(IllegalArgumentException iae){
				reflection_percent = reflection_min;
			}
		}

		if(r.nextInt(100) <= gem_find_chance){ // 1% chance.			
			gem_find = true;
			gem_find_percent = r.nextInt(gem_find_max - gem_find_min) + gem_find_min; // 1 - 3%
		}

		if(r.nextInt(1000) <= item_find_chance){ // 1% chance.
			item_find = true;
			item_find_percent = r.nextInt(item_find_max - item_find_min) + item_find_min; // 1 - 3%
		}

		// x = durability
		int armor_type = new Random().nextInt(2); // 0 or 1.

		@SuppressWarnings("unused")
		String armor_data = "";
		int enchant_count = 0;
		if(reroll == true && original != null){
			rarity = ItemMechanics.getItemRarity(original);
			enchant_count = EnchantMechanics.getEnchantCount(original);
			armor_type = 2; // Ignore the normal check.
			List<Integer> vals = new ArrayList<Integer>();
			if(ItemMechanics.getDmgVal(original, false) != null){
				vals = ItemMechanics.getDmgVal(original, false);
				min_armor = vals.get(0);
				max_armor = vals.get(1);
				armor_data = m.name().toUpperCase() + "#" + min_armor + "-" + max_armor + ":";
				armor_description += red.toString() + "DPS: " + min_armor + " - " + max_armor + "%,";	
			}
			if(ItemMechanics.getArmorVal(original, false) != null){
				vals = ItemMechanics.getArmorVal(original, false);
				min_armor = vals.get(0);
				max_armor = vals.get(1);
				armor_data = m.name().toUpperCase() + "#" + min_armor + "-" + max_armor + ":";
				armor_description += red.toString() + "ARMOR: " + min_armor + " - " + max_armor + "%,";	
			}

			hp_increase_amount = HealthMechanics.getHealthVal(original);
			String oarmor_data = ItemMechanics.getArmorData(original);
			if(oarmor_data.contains("hp_regen")){
				int health_regen_val = Integer.parseInt(oarmor_data.substring(oarmor_data.indexOf("hp_regen=") + 9, oarmor_data.indexOf("@hp_regen_split@")));
				hp_regen = true;
				energy_regen = false;
				hp_regen_percent = health_regen_val;
			}
			if(oarmor_data.contains("energy_regen")){
				int energy_regen_val = Integer.parseInt(oarmor_data.substring(oarmor_data.indexOf("energy_regen=") + 13, oarmor_data.indexOf("@energy_regen_split@")));
				hp_regen = false;
				energy_regen = true;
				energy_regen_percent = energy_regen_val;
			}
		}
		if(armor_type == 0){
			armor_data = m.name().toUpperCase() + "#" + min_armor + "-" + max_armor + ":";
			armor_description += red.toString() + "ARMOR: " + min_armor + " - " + max_armor + "%,";	
		}
		if(armor_type == 1){
			armor_data = m.name().toUpperCase() + "#" + min_armor + "-" + max_armor + ":";
			armor_description += red.toString() + "DPS: " + min_armor + " - " + max_armor + "%,";	
		}

		if(hp_increase_amount <= 0){
			hp_increase_amount = 1;
		}

		armor_data += "hp_increase=" + hp_increase_amount + "@";
		//armor_name = armor_name + " of Fortitude";
		armor_description += red.toString() + "HP: +" + hp_increase_amount + ",";

		if(hp_regen == true){
			armor_data += "hp_regen=" + hp_regen_percent + "@hp_regen_split@:";
			armor_description += red.toString() + "HP REGEN: +" + hp_regen_percent + " HP/s,";
		}

		if(energy_regen == true){
			armor_data += "energy_regen=" + energy_regen_percent + ":";
			armor_name = armor_name + " of Fortitude";
			armor_description += red.toString() + "ENERGY REGEN: +" + energy_regen_percent + "%,";
			// Take from first index of energy_regen= to next index of : from that point.
		}

		int stat_chance = r.nextInt(100);
		int stat_type = r.nextInt(4); // 0, 1, 2
		String stat_string = "";
		String short_stat_string = "";
		int stat_val = 1;
		if(tier == 1){
			stat_val = new Random().nextInt(15) + 1;
			if(stat_chance <= 25){
				stat_chance = r.nextInt(100);
				if(stat_type == 0){
					stat_string = "strength";
					short_stat_string = "STR";
				}
				if(stat_type == 1){
					stat_string = "dexterity";
					short_stat_string = "DEX";
				}
				if(stat_type == 2){
					stat_string = "vitality";
					short_stat_string = "VIT";
				}
				if(stat_type == 3){
					stat_string = "intellect";
					short_stat_string = "INT";
				}

				armor_data += stat_string + "=" + stat_val + ":";
				armor_description += red.toString() + short_stat_string + ": +" + stat_val + ",";
			}
		}
		if(tier == 2){
			if(stat_chance <= 20){
				stat_val = new Random().nextInt(35) + 1;
				stat_chance = r.nextInt(100);
				if(stat_type == 0){
					stat_string = "strength";
					short_stat_string = "STR";
				}
				if(stat_type == 1){
					stat_string = "dexterity";
					short_stat_string = "DEX";
				}
				if(stat_type == 2){
					stat_string = "vitality";
					short_stat_string = "VIT";
				}
				if(stat_type == 3){
					stat_string = "intellect";
					short_stat_string = "INT";
				}

				armor_data += stat_string + "=" + stat_val + ":";
				armor_description += red.toString() + short_stat_string + ": +" + stat_val + ",";
			}
			if(stat_chance <= 5){
				stat_val = new Random().nextInt(35) + 1;
				stat_chance = r.nextInt(100);
				if(stat_type >= 0 && stat_type < 3){
					stat_type++;
				}
				else if(stat_type == 3){
					stat_type--;
				}

				if(stat_type == 0){
					stat_string = "strength";
					short_stat_string = "STR";
				}
				if(stat_type == 1){
					stat_string = "dexterity";
					short_stat_string = "DEX";
				}
				if(stat_type == 2){
					stat_string = "vitality";
					short_stat_string = "VIT";
				}
				if(stat_type == 3){
					stat_string = "intellect";
					short_stat_string = "INT";
				}

				armor_data += stat_string + "=" + stat_val + ":";
				armor_description += red.toString() + short_stat_string + ": +" + stat_val + ",";
			}
		}
		if(tier == 3){
			if(stat_chance <= 15){
				stat_val = new Random().nextInt(75) + 1;
				stat_chance = r.nextInt(100);
				if(stat_type == 0){
					stat_string = "strength";
					short_stat_string = "STR";
				}
				if(stat_type == 1){
					stat_string = "dexterity";
					short_stat_string = "DEX";
				}
				if(stat_type == 2){
					stat_string = "vitality";
					short_stat_string = "VIT";
				}
				if(stat_type == 3){
					stat_string = "intellect";
					short_stat_string = "INT";
				}

				armor_data += stat_string + "=" + stat_val + ":";
				armor_description += red.toString() + short_stat_string + ": +" + stat_val + ",";
			}
			if(stat_chance <= 5){
				stat_val = new Random().nextInt(75) + 1;
				stat_chance = r.nextInt(100);
				if(stat_type >= 0 && stat_type < 3){
					stat_type++;
				}
				else if(stat_type == 3){
					stat_type = 0;
				}

				if(stat_type == 0){
					stat_string = "strength";
					short_stat_string = "STR";
				}
				if(stat_type == 1){
					stat_string = "dexterity";
					short_stat_string = "DEX";
				}
				if(stat_type == 2){
					stat_string = "vitality";
					short_stat_string = "VIT";
				}
				if(stat_type == 3){
					stat_string = "intellect";
					short_stat_string = "INT";
				}

				armor_data += stat_string + "=" + stat_val + ":";
				armor_description += red.toString() + short_stat_string + ": +" + stat_val + ",";
			}
			if(stat_chance <= 1){
				stat_val = new Random().nextInt(75) + 1;
				stat_chance = r.nextInt(100);
				if(stat_type >= 0 && stat_type < 3){
					stat_type++;
				}
				else if(stat_type == 3){
					stat_type = 0;
				}

				if(stat_type == 0){
					stat_string = "strength";
					short_stat_string = "STR";
				}
				if(stat_type == 1){
					stat_string = "dexterity";
					short_stat_string = "DEX";
				}
				if(stat_type == 2){
					stat_string = "vitality";
					short_stat_string = "VIT";
				}
				if(stat_type == 3){
					stat_string = "intellect";
					short_stat_string = "INT";
				}

				armor_data += stat_string + "=" + stat_val + ":";
				armor_description += red.toString() + short_stat_string + ": +" + stat_val + ",";
			}
		}
		if(tier == 4){
			if(stat_chance <= 15){
				stat_val = new Random().nextInt(115) + 1;
				stat_chance = r.nextInt(100);
				if(stat_type == 0){
					stat_string = "strength";
					short_stat_string = "STR";
				}
				if(stat_type == 1){
					stat_string = "dexterity";
					short_stat_string = "DEX";
				}
				if(stat_type == 2){
					stat_string = "vitality";
					short_stat_string = "VIT";
				}
				if(stat_type == 3){
					stat_string = "intellect";
					short_stat_string = "INT";
				}

				armor_data += stat_string + "=" + stat_val + ":";
				armor_description += red.toString() + short_stat_string + ": +" + stat_val + ",";
			}
			if(stat_chance <= 9){
				stat_val = new Random().nextInt(115) + 1;
				stat_chance = r.nextInt(100);
				if(stat_type >= 0 && stat_type < 3){
					stat_type++;
				}
				else if(stat_type == 3){
					stat_type = 0;
				}

				if(stat_type == 0){
					stat_string = "strength";
					short_stat_string = "STR";
				}
				if(stat_type == 1){
					stat_string = "dexterity";
					short_stat_string = "DEX";
				}
				if(stat_type == 2){
					stat_string = "vitality";
					short_stat_string = "VIT";
				}
				if(stat_type == 3){
					stat_string = "intellect";
					short_stat_string = "INT";
				}

				armor_data += stat_string + "=" + stat_val + ":";
				armor_description += red.toString() + short_stat_string + ": +" + stat_val + ",";
			}
			if(stat_chance <= 4){
				stat_val = new Random().nextInt(115) + 1;
				stat_chance = r.nextInt(100);
				if(stat_type >= 0 && stat_type < 3){
					stat_type++;
				}
				else if(stat_type == 3){
					stat_type = 0;
				}

				if(stat_type == 0){
					stat_string = "strength";
					short_stat_string = "STR";
				}
				if(stat_type == 1){
					stat_string = "dexterity";
					short_stat_string = "DEX";
				}
				if(stat_type == 2){
					stat_string = "vitality";
					short_stat_string = "VIT";
				}
				if(stat_type == 3){
					stat_string = "intellect";
					short_stat_string = "INT";
				}

				armor_data += stat_string + "=" + stat_val + ":";
				armor_description += red.toString() + short_stat_string + ": +" + stat_val + ",";
			}
		}
		if(tier == 5){
			if(stat_chance <= 20){
				stat_val = new Random().nextInt(315) + 1;
				stat_chance = r.nextInt(100);
				if(stat_type == 0){
					stat_string = "strength";
					short_stat_string = "STR";
				}
				if(stat_type == 1){
					stat_string = "dexterity";
					short_stat_string = "DEX";
				}
				if(stat_type == 2){
					stat_string = "vitality";
					short_stat_string = "VIT";
				}
				if(stat_type == 3){
					stat_string = "intellect";
					short_stat_string = "INT";
				}

				armor_data += stat_string + "=" + stat_val + ":";
				armor_description += red.toString() + short_stat_string + ": +" + stat_val + ",";
			}
			if(stat_chance <= 10){
				stat_val = new Random().nextInt(315) + 1;
				stat_chance = r.nextInt(100);
				if(stat_type >= 0 && stat_type < 3){
					stat_type++;
				}
				else if(stat_type == 3){
					stat_type = 0;
				}

				if(stat_type == 0){
					stat_string = "strength";
					short_stat_string = "STR";
				}
				if(stat_type == 1){
					stat_string = "dexterity";
					short_stat_string = "DEX";
				}
				if(stat_type == 2){
					stat_string = "vitality";
					short_stat_string = "VIT";
				}
				if(stat_type == 3){
					stat_string = "intellect";
					short_stat_string = "INT";
				}

				armor_data += stat_string + "=" + stat_val + ":";
				armor_description += red.toString() + short_stat_string + ": +" + stat_val + ",";
			}
			if(stat_chance <= 5){
				stat_val = new Random().nextInt(315) + 1;
				if(stat_type >= 0 && stat_type < 3){
					stat_type++;
				}
				else if(stat_type == 3){
					stat_type = 0;
				}

				if(stat_type == 0){
					stat_string = "strength";
					short_stat_string = "STR";
				}
				if(stat_type == 1){
					stat_string = "dexterity";
					short_stat_string = "DEX";
				}
				if(stat_type == 2){
					stat_string = "vitality";
					short_stat_string = "VIT";
				}
				if(stat_type == 3){
					stat_string = "intellect";
					short_stat_string = "INT";
				}

				armor_data += stat_string + "=" + stat_val + ":";
				armor_description += red.toString() + short_stat_string + ": +" + stat_val + ",";
			}
		}

		if(speed_boost == true){
			armor_data += "speed=" + speed_level + ":";
			armor_name = "Swift " + armor_name;
			armor_description += red.toString() + "SPEED BOOST: " + speed_level + "X,";
			// Take from first index of energy_regen= to next index of : from that point.
		}

		// Resistances
		int res_chance = r.nextInt(100);
		int res_type = r.nextInt(3); // 0-fire, 1-ice, 2-poison
		String res_string = "";
		String res_shorthand = "";
		int res_val = -1;

		if(tier == 1){
			if(res_chance <= 15){
				res_val = new Random().nextInt(5) + 1;
			}
		}
		if(tier == 2){
			if(res_chance <= 15){
				res_val = new Random().nextInt(7) + 1;
			}
		}
		if(tier == 3){
			if(res_chance <= 25){
				res_val = new Random().nextInt(20) + 1;
			}
		}
		if(tier == 4){
			if(res_chance <= 20){
				res_val = new Random().nextInt(32) + 1;
			}
		}
		if(tier == 5){
			if(res_chance <= 30){
				res_val = new Random().nextInt(45) + 1;
			}
		}

		if(res_type == 0){
			res_string = "fire_resistance";
			res_shorthand = "FIRE";
		}
		if(res_type == 1){
			res_string = "ice_resistance";
			res_shorthand = "ICE";
		}
		if(res_type == 2){
			res_string = "poison_resistance";
			res_shorthand = "POISON";
		}

		if(res_val > 0){
			// We have some resistance.
			armor_data += res_string + "=" + res_val + ":";
			armor_description += red.toString() + res_shorthand + " RESISTANCE: " + res_val + "%,";
			if(!armor_name.contains("of")){
				armor_name = armor_name + " of ";
			}
			else{
				armor_name = armor_name + " and ";
			}
			
			
			if(res_type == 0){
				armor_name = armor_name + "Fire Resist";
			}
			if(res_type == 1){
				armor_name = armor_name + "Ice Resist";
			}
			if(res_type == 2){
				armor_name = armor_name + "Poison Resist";
			}
		}

		if(dodge == true){
			armor_data += "dodge=" + dodge_percent + ":";;
			armor_name = "Agile " + armor_name;
			armor_description += red.toString() + "DODGE: " + dodge_percent + "%,";
		}

		if(reflection == true){
			armor_data += "reflection=" + reflection_percent + ":";;
			armor_name = "Reflective " + armor_name;
			armor_description += red.toString() + "REFLECTION: " + reflection_percent + "%,";
		}

		if(hp_regen == true){
			armor_name = "Mending " + armor_name;
		}

		if(block == true){
			armor_data += "block=" + block_percent + ":";;
			armor_name = "Protective " + armor_name;
			armor_description += red.toString() + "BLOCK: " + block_percent + "%,";
		}

		if(gem_find == true){
			armor_data += "gem_find=" + gem_find_percent + ":";;
			if(armor_name.contains("of")){
				armor_name = armor_name + " Golden";
			}
			else{
				armor_name = armor_name + " of Pickpocketing";
			}
			armor_description += red.toString() + "GEM FIND: " + gem_find_percent + "%,";
		}

		if(item_find == true){
			armor_data += "item_find=" + item_find_percent;
			if(armor_name.contains("of")){
				armor_name = armor_name + " Treasure";
			}	
			else{
				armor_name = armor_name + " of Treasure";
			}
			armor_description += red.toString() + "ITEM FIND: " + item_find_percent + "%,";
		}


		if(thorns == true){
			armor_data += "thorns=" + thorns_percent + ":";;
			if(armor_name.contains("of")){
				armor_name = armor_name + " Spikes";
			}
			else{
				armor_name = armor_name + " of Thorns";
			}
			armor_description += red.toString() + "THORNS: " + thorns_percent + "% DMG";
		}



		/*if(hp_regen == true){
			armor_data += "ls=" + hp_regen_percent + ":";
			armor_name = "Lifestealing " + armor_name; 
			armor_description += red.toString() + "LIFE STEAL: " + hp_regen_percent + "%,";
			// hp_regen == .split(":")[2]
		}


		if(block == true){
			armor_data += "crit=" + block_percent + ":";
			armor_name = "Deadly " + armor_name;
			armor_description += red.toString() + "CRITICAL HIT: " + block_percent + "%,";
		}


		if(hp_increase == true){
			armor_data += "edmg=" + element_dmg_type + "," + element_dmg_amount + ":";

			if(dodge == true){
				armor_name = armor_name + " " + element_dmg_type.substring(0, 1).toUpperCase() + element_dmg_type.substring(1, element_dmg_type.length());
			}
			if(dodge == false){
				armor_name = armor_name + " of " + element_dmg_type.substring(0, 1).toUpperCase() + element_dmg_type.substring(1, element_dmg_type.length());
			}

			armor_description += red.toString() + element_dmg_type.toUpperCase() + " DMG: +" + element_dmg_amount + ",";
			// hp_increase == .split(":")[3].split(",")[1]
			// edmg=fire,3
		}*/

		armor_description += "," + rarity;
		
		if(enchant_count > 0){
			armor_name = ChatColor.RED + "[+" + enchant_count + "]" + " " + tag.toString() + armor_name;;
		}
		else{
			armor_name = tag.toString() + armor_name;
		}
		//log.info(armor_name + " = " + armor_data);

		return ItemMechanics.signCustomItem(m, (short)0, armor_name, armor_description);
	}

	public static ItemStack HelmetGenerator(int tier, boolean reroll, ItemStack original){
		Random r = new Random();
		Material m = null;
		boolean hp_regen = false, /* hp_increase = false, */ energy_regen = false, block = false, dodge = false, thorns = false, reflection = false, gem_find = false, item_find = false;
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
		//int block_min = 0;
		//int dodge_min = 0;
		//int thorns_min = 0;
		//int reflection_min = 0;
		//int gem_find_min = 0;
		//int item_find_min = 0;

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

		if(tier == 1){
			armor_name = "Leather Coif";
			tag = ChatColor.WHITE;
			m = Material.LEATHER_HELMET;

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
			//block_min = 1;
			//dodge_min = 1;
			//thorns_min = 1; // 0.1%
			//reflection_min = 1; // 0.1%
			//gem_find_min = 1; // 0.1%
			//item_find_min = 1; // 0.1%

			hp_regen_max = 15;
			energy_regen_max = 5;
			block_max = 5;
			dodge_max = 5;
			thorns_max = 2;
			reflection_max = 1;
			gem_find_max = 5;
			item_find_max = 1;

		}
		if(tier == 2){
			armor_name = "Medium Helmet";
			tag = ChatColor.GREEN;
			m = Material.CHAINMAIL_HELMET;

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
			//block_min = 1;
			//dodge_min = 1;
			//thorns_min = 1; 
			//reflection_min = 1; 
			//gem_find_min = 1; 
			//item_find_min = 1; // 0.1%

			hp_regen_max = 25; 
			energy_regen_max = 7;
			block_max = 8;
			dodge_max = 8;
			thorns_max = 3;
			reflection_max = 2;
			gem_find_max = 8;
			item_find_max = 2;
		}
		if(tier == 3){
			armor_name = "Full Helmet";
			tag = ChatColor.AQUA;
			m = Material.IRON_HELMET;

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
			//block_min = 1;
			//dodge_min = 1;
			//thorns_min = 1; 
			//reflection_min = 1; 
			//gem_find_min = 3; 
			//item_find_min = 1; // 0.1%

			hp_regen_max = 55;
			energy_regen_max = 9;
			block_max = 10;
			dodge_max = 10;
			thorns_max = 5;
			reflection_max = 4;
			gem_find_max = 15;
			item_find_max = 3;
		}
		if(tier == 4){
			armor_name = "Ancient Full Helmet";
			tag = ChatColor.LIGHT_PURPLE;
			m = Material.DIAMOND_HELMET;

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
			//block_min = 1;
			//dodge_min = 1;
			//thorns_min = 2; 
			//reflection_min = 2; 
			//gem_find_min = 5; 
			//item_find_min = 1; // 0.1%

			hp_regen_max = 75;
			energy_regen_max = 12;
			block_max = 12;
			dodge_max = 12;
			thorns_max = 9;
			reflection_max = 5;
			gem_find_max = 20;
			item_find_max = 4;
		}
		if(tier == 5){
			armor_name = "Legendary Full Helmet";
			tag = ChatColor.YELLOW;
			m = Material.GOLD_HELMET;

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
			//block_min = 1;
			//dodge_min = 1;
			//thorns_min = 2; 
			//reflection_min = 2; 
			//gem_find_min = 5; 
			//item_find_min = 1; // 0.1%

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
		if(armor_range_check <= 80){ 
			rarity = ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + "Common";
			if(tier == 1){
				//int min_num = 1 + 1;
				//int max_num = 2 + 1;

				min_armor = 1;
				max_armor = 1;

				int min_hp = 10;
				int max_hp = 20;

				hp_increase_amount = r.nextInt(max_hp - min_hp) + min_hp;
			}
			if(tier == 2){
				int min_num = 1 + 1;
				int max_num = 2 + 1;

				min_armor = r.nextInt(max_num - min_num) + (min_num - 1);
				max_armor = r.nextInt(max_num - min_armor) + min_armor;

				int min_hp = 60;
				int max_hp = 80;

				hp_increase_amount = r.nextInt(max_hp - min_hp) + min_hp;
			}
			if(tier == 3){
				int min_num = 5 + 1;
				int max_num = 6 + 1;


				min_armor = r.nextInt(max_num - min_num) + (min_num - 1);
				max_armor = r.nextInt(max_num - min_armor) + min_armor;

				int min_hp = 200;
				int max_hp = 350;

				hp_increase_amount = r.nextInt(max_hp - min_hp) + min_hp;
			}
			if(tier == 4){
				int min_num = 8 + 1;
				int max_num = 9 + 1;

				min_armor = r.nextInt(max_num - min_num) + (min_num - 1);
				max_armor = r.nextInt(max_num - min_armor) + min_armor;

				int min_hp = 600;
				int max_hp = 800;

				hp_increase_amount = r.nextInt(max_hp - min_hp) + min_hp;
			}
			if(tier == 5){
				int min_num = 11 + 1;
				int max_num = 12 + 1;

				min_armor = r.nextInt(max_num - min_num) + (min_num - 1);
				max_armor = r.nextInt(max_num - min_armor) + min_armor;

				int min_hp = 1500;
				int max_hp = 2500;

				hp_increase_amount = r.nextInt(max_hp - min_hp) + min_hp;
			}
		}

		if(armor_range_check > 80 && armor_range_check < 95){
			rarity = ChatColor.GREEN.toString() + ChatColor.ITALIC.toString() + "Uncommon";
			if(tier == 1){
				int min_num = 1 + 1;
				int max_num = 2 + 1;

				min_armor = r.nextInt(max_num - min_num) + (min_num - 1);
				max_armor = r.nextInt(max_num - min_armor) + min_armor;

				int min_hp = 20;
				int max_hp = 50;

				hp_increase_amount = r.nextInt(max_hp - min_hp) + min_hp;
			}
			if(tier == 2){
				int min_num = 3 + 1;
				int max_num = 4 + 1;

				min_armor = r.nextInt(max_num - min_num) + (min_num - 1);
				max_armor = r.nextInt(max_num - min_armor) + min_armor;

				int min_hp = 100;
				int max_hp = 250;

				hp_increase_amount = r.nextInt(max_hp - min_hp) + min_hp;
			}
			if(tier == 3){
				int min_num = 6 + 1;
				int max_num = 9 + 1;


				min_armor = r.nextInt(max_num - min_num) + (min_num - 1);
				max_armor = r.nextInt(max_num - min_armor) + min_armor;

				int min_hp = 350;
				int max_hp = 600;

				hp_increase_amount = r.nextInt(max_hp - min_hp) + min_hp;
			}
			if(tier == 4){
				int min_num = 10 + 1;
				int max_num = 11 + 1;

				min_armor = r.nextInt(max_num - min_num) + (min_num - 1);
				max_armor = r.nextInt(max_num - min_armor) + min_armor;

				int min_hp = 800;
				int max_hp = 1300;

				hp_increase_amount = r.nextInt(max_hp - min_hp) + min_hp;
			}
			if(tier == 5){
				int min_num = 13 + 1;
				int max_num = 14 + 1;

				min_armor = r.nextInt(max_num - min_num) + (min_num - 1);
				max_armor = r.nextInt(max_num - min_armor) + min_armor;

				int min_hp = 2500;
				int max_hp = 3600;

				hp_increase_amount = r.nextInt(max_hp - min_hp) + min_hp;
			}
		}

		if(armor_range_check >= 95 && armor_range_check != 99){
			rarity = ChatColor.AQUA.toString() + ChatColor.ITALIC.toString() + "Rare";
			if(tier == 1){
				int min_num = 1 + 1;
				int max_num = 2 + 1;

				min_armor = r.nextInt(max_num - min_num) + (min_num - 1);
				max_armor = r.nextInt(max_num - min_armor) + min_armor;

				int min_hp = 80;
				int max_hp = 120;

				hp_increase_amount = r.nextInt(max_hp - min_hp) + min_hp;
			}
			if(tier == 2){
				int min_num = 5 + 1;
				int max_num = 6 + 1;

				min_armor = r.nextInt(max_num - min_num) + (min_num - 1);
				max_armor = r.nextInt(max_num - min_armor) + min_armor;

				int min_hp = 250;
				int max_hp = 350;

				hp_increase_amount = r.nextInt(max_hp - min_hp) + min_hp;
			}
			if(tier == 3){
				int min_num = 8 + 1;
				int max_num = 9 + 1;


				min_armor = r.nextInt(max_num - min_num) + (min_num - 1);
				max_armor = r.nextInt(max_num - min_armor) + min_armor;

				int min_hp = 600;
				int max_hp = 800;

				hp_increase_amount = r.nextInt(max_hp - min_hp) + min_hp;
			}
			if(tier == 4){
				int min_num = 11 + 1;
				int max_num = 12 + 1;

				min_armor = r.nextInt(max_num - min_num) + (min_num - 1);
				max_armor = r.nextInt(max_num - min_armor) + min_armor;

				int min_hp = 1300;
				int max_hp = 2400;

				hp_increase_amount = r.nextInt(max_hp - min_hp) + min_hp;
			}
			if(tier == 5){
				int min_num = 16 + 1;
				int max_num = 17 + 1;

				min_armor = r.nextInt(max_num - min_num) + (min_num - 1);
				max_armor = r.nextInt(max_num - min_armor) + min_armor;

				int min_hp = 3600;
				int max_hp = 5400;

				hp_increase_amount = r.nextInt(max_hp - min_hp) + min_hp;
			}
		}

		if(armor_range_check == 99){
			rarity = ChatColor.YELLOW.toString() + ChatColor.ITALIC.toString() + "Unique";
			if(tier == 1){
				int min_num = 1 + 1;
				int max_num = 2 + 1;

				min_armor = r.nextInt(max_num - min_num) + (min_num - 1);
				max_armor = r.nextInt(max_num - min_armor) + min_armor;

				int min_hp = 80;
				int max_hp = 120;

				hp_increase_amount = r.nextInt(max_hp - min_hp) + min_hp;
			}
			if(tier == 2){
				int min_num = 5 + 1;
				int max_num = 6 + 1;

				min_armor = r.nextInt(max_num - min_num) + (min_num - 1);
				max_armor = r.nextInt(max_num - min_armor) + min_armor;

				int min_hp = 250;
				int max_hp = 350;

				hp_increase_amount = r.nextInt(max_hp - min_hp) + min_hp;
			}
			if(tier == 3){
				int min_num = 8 + 1;
				int max_num = 9 + 1;


				min_armor = r.nextInt(max_num - min_num) + (min_num - 1);
				max_armor = r.nextInt(max_num - min_armor) + min_armor;

				int min_hp = 600;
				int max_hp = 800;

				hp_increase_amount = r.nextInt(max_hp - min_hp) + min_hp;
			}
			if(tier == 4){
				int min_num = 11 + 1;
				int max_num = 12 + 1;

				min_armor = r.nextInt(max_num - min_num) + (min_num - 1);
				max_armor = r.nextInt(max_num - min_armor) + min_armor;

				int min_hp = 1300;
				int max_hp = 2400;

				hp_increase_amount = r.nextInt(max_hp - min_hp) + min_hp;
			}
			if(tier == 5){
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
		
		double temp_min_armor = (double)min_armor;
		double temp_max_armor = (double)max_armor;

		double temp_hp_increase_amount = (double)hp_increase_amount;

		min_armor = (int)(temp_min_armor * 0.50); // 50% of the values of chestplate / leggings.
		max_armor = (int)(temp_max_armor * 0.50); // 50% of the values of chestplate / leggings.

		hp_increase_amount = (int)(temp_hp_increase_amount * 0.50); // 50% of HP increase value.

		if(min_armor == 0){
			min_armor = 1;
		}

		if(max_armor == 0){
			min_armor = 1;
			max_armor = 1;
		}

		int regen_type = r.nextInt(2); // 0, 1.

		if(regen_type == 0){  // HP/s HP REGEN
			hp_regen = true;
			//hp_regen_max = (int) Math.round((hp_increase_amount / 10) + (hp_increase_amount * 0.05));
			//if(hp_regen_max < 1){hp_regen_max = 1;}
			double regen_val = hp_increase_amount * 0.05;
			if(regen_val < 1){regen_val = 1;}

			int real_hp_regen_max = (int) ((regen_val) + (hp_increase_amount / 10)) - r.nextInt((int) Math.round(regen_val));
			if(real_hp_regen_max < 1){real_hp_regen_max = 1;}
			//hp_regen_percent = r.nextInt(real_hp_regen_max) + 1;
			hp_regen_percent = r.nextInt(hp_regen_max - hp_regen_min) + hp_regen_min;
		}
		else if(regen_type == 1){
			energy_regen = true;
			energy_regen_percent = r.nextInt(energy_regen_max - energy_regen_min) + energy_regen_min; 
			energy_regen_percent = energy_regen_percent / 2;

			if(energy_regen_percent < 1){
				energy_regen_percent = 1;
			}
		}

		if(r.nextInt(100) <= block_chance){ 
			block = true;
			block_percent = r.nextInt(block_max) + 1; // 1 - 10%
		}

		if(r.nextInt(100) <= dodge_chance){ // 1% chance.
			dodge = true;
			dodge_percent = r.nextInt(dodge_max) + 1; // 1 - 3%
		}

		if(r.nextInt(100) <= thorns_chance){ // 1% chance.
			thorns = true;
			thorns_percent = r.nextInt(thorns_max) + 1; // 1 - 3%
		}

		if(r.nextInt(100) <= reflection_chance){ // 1% chance.
			reflection = true;
			reflection_percent = r.nextInt(reflection_max) + 1; // 1 - 3%
		}

		if(r.nextInt(100) <= gem_find_chance){ // 1% chance.

			gem_find = true;
			gem_find_percent = r.nextInt(gem_find_max) + 1; // 1 - 3%
		}

		if(r.nextInt(100) <= item_find_chance){ // 1% chance.
			item_find = true;
			item_find_percent = r.nextInt(item_find_max) + 1; // 1 - 3%
		}

		// x = durability

		int armor_type = new Random().nextInt(2); // 0 or 1.

		@SuppressWarnings("unused")
		String armor_data = "";
		int enchant_count = 0;
		if(reroll == true && original != null){
			armor_type = 2; // Ignore the normal check.
			rarity = ItemMechanics.getItemRarity(original);
			enchant_count = EnchantMechanics.getEnchantCount(original);
			List<Integer> vals = new ArrayList<Integer>();
			if(ItemMechanics.getDmgVal(original, false) != null){
				vals = ItemMechanics.getDmgVal(original, false);
				min_armor = vals.get(0);
				max_armor = vals.get(1);
				armor_data = m.name().toUpperCase() + "#" + min_armor + "-" + max_armor + ":";
				armor_description += red.toString() + "DPS: " + min_armor + " - " + max_armor + "%,";	
			}
			if(ItemMechanics.getArmorVal(original, false) != null){
				vals = ItemMechanics.getArmorVal(original, false);
				min_armor = vals.get(0);
				max_armor = vals.get(1);
				armor_data = m.name().toUpperCase() + "#" + min_armor + "-" + max_armor + ":";
				armor_description += red.toString() + "ARMOR: " + min_armor + " - " + max_armor + "%,";	
			}

			hp_increase_amount = HealthMechanics.getHealthVal(original);
			String oarmor_data = ItemMechanics.getArmorData(original);
			if(oarmor_data.contains("hp_regen")){
				int health_regen_val = Integer.parseInt(oarmor_data.substring(oarmor_data.indexOf("hp_regen=") + 9, oarmor_data.indexOf("@hp_regen_split@")));
				hp_regen = true;
				energy_regen = false;
				hp_regen_percent = health_regen_val;
			}
			if(oarmor_data.contains("energy_regen")){
				int energy_regen_val = Integer.parseInt(oarmor_data.substring(oarmor_data.indexOf("energy_regen=") + 13, oarmor_data.indexOf("@energy_regen_split@")));
				hp_regen = false;
				energy_regen = true;
				energy_regen_percent = energy_regen_val;
			}
		}

		if(armor_type == 0){
			armor_data = m.name().toUpperCase() + "#" + min_armor + "-" + max_armor + ":";
			armor_description += red.toString() + "ARMOR: " + min_armor + " - " + max_armor + "%,";	
		}
		if(armor_type == 1){
			armor_data = m.name().toUpperCase() + "#" + min_armor + "-" + max_armor + ":";
			armor_description += red.toString() + "DPS: " + min_armor + " - " + max_armor + "%,";	
		}

		if(hp_increase_amount <= 0){
			hp_increase_amount = 1;
		}

		armor_data += "hp_increase=" + hp_increase_amount + "@";
		//armor_name = armor_name + " of Fortitude";
		armor_description += red.toString() + "HP: +" + hp_increase_amount + ",";

		if(hp_regen == true){
			armor_data += "hp_regen=" + hp_regen_percent + "@hp_regen_split@:";
			armor_description += red.toString() + "HP REGEN: +" + hp_regen_percent + " HP/s,";
		}

		if(energy_regen == true){
			armor_data += "energy_regen=" + energy_regen_percent + ":";
			armor_name = armor_name + " of Fortitude";
			armor_description += red.toString() + "ENERGY REGEN: +" + energy_regen_percent + "%,";
			// Take from first index of energy_regen= to next index of : from that point.
		}

		int stat_chance = r.nextInt(100);
		int stat_type = r.nextInt(4); // 0, 1, 2
		String stat_string = "";
		String short_stat_string = "";
		int stat_val = 1;
		if(tier == 1){
			stat_val = new Random().nextInt(15) + 1;
			if(stat_chance <= 25){
				stat_chance = r.nextInt(100);
				if(stat_type == 0){
					stat_string = "strength";
					short_stat_string = "STR";
				}
				if(stat_type == 1){
					stat_string = "dexterity";
					short_stat_string = "DEX";
				}
				if(stat_type == 2){
					stat_string = "vitality";
					short_stat_string = "VIT";
				}
				if(stat_type == 3){
					stat_string = "intellect";
					short_stat_string = "INT";
				}

				armor_data += stat_string + "=" + stat_val + ":";
				armor_description += red.toString() + short_stat_string + ": +" + stat_val + ",";
			}
		}
		if(tier == 2){
			if(stat_chance <= 20){
				stat_val = new Random().nextInt(35) + 1;
				stat_chance = r.nextInt(100);
				if(stat_type == 0){
					stat_string = "strength";
					short_stat_string = "STR";
				}
				if(stat_type == 1){
					stat_string = "dexterity";
					short_stat_string = "DEX";
				}
				if(stat_type == 2){
					stat_string = "vitality";
					short_stat_string = "VIT";
				}
				if(stat_type == 3){
					stat_string = "intellect";
					short_stat_string = "INT";
				}

				armor_data += stat_string + "=" + stat_val + ":";
				armor_description += red.toString() + short_stat_string + ": +" + stat_val + ",";
			}
			if(stat_chance <= 5){
				stat_val = new Random().nextInt(35) + 1;
				stat_chance = r.nextInt(100);
				if(stat_type >= 0 && stat_type < 3){
					stat_type++;
				}
				else if(stat_type == 3){
					stat_type--;
				}

				if(stat_type == 0){
					stat_string = "strength";
					short_stat_string = "STR";
				}
				if(stat_type == 1){
					stat_string = "dexterity";
					short_stat_string = "DEX";
				}
				if(stat_type == 2){
					stat_string = "vitality";
					short_stat_string = "VIT";
				}
				if(stat_type == 3){
					stat_string = "intellect";
					short_stat_string = "INT";
				}

				armor_data += stat_string + "=" + stat_val + ":";
				armor_description += red.toString() + short_stat_string + ": +" + stat_val + ",";
			}
		}
		if(tier == 3){
			if(stat_chance <= 15){
				stat_val = new Random().nextInt(75) + 1;
				stat_chance = r.nextInt(100);
				if(stat_type == 0){
					stat_string = "strength";
					short_stat_string = "STR";
				}
				if(stat_type == 1){
					stat_string = "dexterity";
					short_stat_string = "DEX";
				}
				if(stat_type == 2){
					stat_string = "vitality";
					short_stat_string = "VIT";
				}
				if(stat_type == 3){
					stat_string = "intellect";
					short_stat_string = "INT";
				}

				armor_data += stat_string + "=" + stat_val + ":";
				armor_description += red.toString() + short_stat_string + ": +" + stat_val + ",";
			}
			if(stat_chance <= 5){
				stat_val = new Random().nextInt(75) + 1;
				stat_chance = r.nextInt(100);
				if(stat_type >= 0 && stat_type < 3){
					stat_type++;
				}
				else if(stat_type == 3){
					stat_type = 0;
				}

				if(stat_type == 0){
					stat_string = "strength";
					short_stat_string = "STR";
				}
				if(stat_type == 1){
					stat_string = "dexterity";
					short_stat_string = "DEX";
				}
				if(stat_type == 2){
					stat_string = "vitality";
					short_stat_string = "VIT";
				}
				if(stat_type == 3){
					stat_string = "intellect";
					short_stat_string = "INT";
				}

				armor_data += stat_string + "=" + stat_val + ":";
				armor_description += red.toString() + short_stat_string + ": +" + stat_val + ",";
			}
			if(stat_chance <= 1){
				stat_val = new Random().nextInt(75) + 1;
				stat_chance = r.nextInt(100);
				if(stat_type >= 0 && stat_type < 3){
					stat_type++;
				}
				else if(stat_type == 3){
					stat_type = 0;
				}

				if(stat_type == 0){
					stat_string = "strength";
					short_stat_string = "STR";
				}
				if(stat_type == 1){
					stat_string = "dexterity";
					short_stat_string = "DEX";
				}
				if(stat_type == 2){
					stat_string = "vitality";
					short_stat_string = "VIT";
				}
				if(stat_type == 3){
					stat_string = "intellect";
					short_stat_string = "INT";
				}

				armor_data += stat_string + "=" + stat_val + ":";
				armor_description += red.toString() + short_stat_string + ": +" + stat_val + ",";
			}
		}
		if(tier == 4){
			if(stat_chance <= 15){
				stat_val = new Random().nextInt(115) + 1;
				stat_chance = r.nextInt(100);
				if(stat_type == 0){
					stat_string = "strength";
					short_stat_string = "STR";
				}
				if(stat_type == 1){
					stat_string = "dexterity";
					short_stat_string = "DEX";
				}
				if(stat_type == 2){
					stat_string = "vitality";
					short_stat_string = "VIT";
				}
				if(stat_type == 3){
					stat_string = "intellect";
					short_stat_string = "INT";
				}

				armor_data += stat_string + "=" + stat_val + ":";
				armor_description += red.toString() + short_stat_string + ": +" + stat_val + ",";
			}
			if(stat_chance <= 9){
				stat_val = new Random().nextInt(115) + 1;
				stat_chance = r.nextInt(100);
				if(stat_type >= 0 && stat_type < 3){
					stat_type++;
				}
				else if(stat_type == 3){
					stat_type = 0;
				}

				if(stat_type == 0){
					stat_string = "strength";
					short_stat_string = "STR";
				}
				if(stat_type == 1){
					stat_string = "dexterity";
					short_stat_string = "DEX";
				}
				if(stat_type == 2){
					stat_string = "vitality";
					short_stat_string = "VIT";
				}
				if(stat_type == 3){
					stat_string = "intellect";
					short_stat_string = "INT";
				}

				armor_data += stat_string + "=" + stat_val + ":";
				armor_description += red.toString() + short_stat_string + ": +" + stat_val + ",";
			}
			if(stat_chance <= 4){
				stat_val = new Random().nextInt(115) + 1;
				stat_chance = r.nextInt(100);
				if(stat_type >= 0 && stat_type < 3){
					stat_type++;
				}
				else if(stat_type == 3){
					stat_type = 0;
				}

				if(stat_type == 0){
					stat_string = "strength";
					short_stat_string = "STR";
				}
				if(stat_type == 1){
					stat_string = "dexterity";
					short_stat_string = "DEX";
				}
				if(stat_type == 2){
					stat_string = "vitality";
					short_stat_string = "VIT";
				}
				if(stat_type == 3){
					stat_string = "intellect";
					short_stat_string = "INT";
				}

				armor_data += stat_string + "=" + stat_val + ":";
				armor_description += red.toString() + short_stat_string + ": +" + stat_val + ",";
			}
		}
		if(tier == 5){
			if(stat_chance <= 20){
				stat_val = new Random().nextInt(315) + 1;
				stat_chance = r.nextInt(100);
				if(stat_type == 0){
					stat_string = "strength";
					short_stat_string = "STR";
				}
				if(stat_type == 1){
					stat_string = "dexterity";
					short_stat_string = "DEX";
				}
				if(stat_type == 2){
					stat_string = "vitality";
					short_stat_string = "VIT";
				}
				if(stat_type == 3){
					stat_string = "intellect";
					short_stat_string = "INT";
				}
				
				armor_data += stat_string + "=" + stat_val + ":";
				armor_description += red.toString() + short_stat_string + ": +" + stat_val + ",";
			}
			if(stat_chance <= 10){
				stat_val = new Random().nextInt(315) + 1;
				stat_chance = r.nextInt(100);
				if(stat_type >= 0 && stat_type < 3){
					stat_type++;
				}
				else if(stat_type == 3){
					stat_type = 0;
				}

				if(stat_type == 0){
					stat_string = "strength";
					short_stat_string = "STR";
				}
				if(stat_type == 1){
					stat_string = "dexterity";
					short_stat_string = "DEX";
				}
				if(stat_type == 2){
					stat_string = "vitality";
					short_stat_string = "VIT";
				}
				if(stat_type == 3){
					stat_string = "intellect";
					short_stat_string = "INT";
				}
				
				armor_data += stat_string + "=" + stat_val + ":";
				armor_description += red.toString() + short_stat_string + ": +" + stat_val + ",";
			}
			if(stat_chance <= 5){
				stat_val = new Random().nextInt(315) + 1;
				if(stat_type >= 0 && stat_type < 3){
					stat_type++;
				}
				else if(stat_type == 3){
					stat_type = 0;
				}

				if(stat_type == 0){
					stat_string = "strength";
					short_stat_string = "STR";
				}
				if(stat_type == 1){
					stat_string = "dexterity";
					short_stat_string = "DEX";
				}
				if(stat_type == 2){
					stat_string = "vitality";
					short_stat_string = "VIT";
				}
				if(stat_type == 3){
					stat_string = "intellect";
					short_stat_string = "INT";
				}
				
				armor_data += stat_string + "=" + stat_val + ":";
				armor_description += red.toString() + short_stat_string + ": +" + stat_val + ",";
			}
		}

		// Resistances
		int res_chance = r.nextInt(100);
		int res_type = r.nextInt(3); // 0-fire, 1-ice, 2-poison
		String res_string = "";
		String res_shorthand = "";
		int res_val = -1;

		if(tier == 1){
			if(res_chance <= 15){
				res_val = new Random().nextInt(5) + 1;
			}
		}
		if(tier == 2){
			if(res_chance <= 15){
				res_val = new Random().nextInt(7) + 1;
			}
		}
		if(tier == 3){
			if(res_chance <= 25){
				res_val = new Random().nextInt(20) + 1;
			}
		}
		if(tier == 4){
			if(res_chance <= 20){
				res_val = new Random().nextInt(32) + 1;
			}
		}
		if(tier == 5){
			if(res_chance <= 30){
				res_val = new Random().nextInt(45) + 1;
			}
		}

		if(res_type == 0){
			res_string = "fire_resistance";
			res_shorthand = "FIRE";
		}
		if(res_type == 1){
			res_string = "ice_resistance";
			res_shorthand = "ICE";
		}
		if(res_type == 2){
			res_string = "poison_resistance";
			res_shorthand = "POISON";
		}

		if(res_val > 0){
			// We have some resistance.
			armor_data += res_string + "=" + res_val + ":";
			armor_description += red.toString() + res_shorthand + " RESISTANCE: " + res_val + "%,";
			if(!armor_name.contains("of")){
				armor_name = armor_name + " of ";
			}
			else{
				armor_name = armor_name + " and ";
			}
			
			
			if(res_type == 0){
				armor_name = armor_name + "Fire Resist";
			}
			if(res_type == 1){
				armor_name = armor_name + "Ice Resist";
			}
			if(res_type == 2){
				armor_name = armor_name + "Poison Resist";
			}
		}

		if(dodge == true){
			armor_data += "dodge=" + dodge_percent + ":";;
			armor_name = "Agile " + armor_name;
			armor_description += red.toString() + "DODGE: " + dodge_percent + "%,";
		}

		if(reflection == true){
			armor_data += "reflection=" + reflection_percent + ":";;
			armor_name = "Reflective " + armor_name;
			armor_description += red.toString() + "REFLECTION: " + reflection_percent + "%,";
		}

		if(hp_regen == true){
			armor_name = "Mending " + armor_name;
		}

		if(block == true){
			armor_data += "block=" + block_percent + ":";;
			armor_name = "Protective " + armor_name;
			armor_description += red.toString() + "BLOCK: " + block_percent + "%,";
		}

		if(gem_find == true){
			armor_data += "gem_find=" + gem_find_percent + ":";;
			if(armor_name.contains("of")){
				armor_name = armor_name + " Golden";
			}
			else{
				armor_name = armor_name + " of Pickpocketing";
			}
			armor_description += red.toString() + "GEM FIND: " + gem_find_percent + "%,";
		}

		if(item_find == true){
			armor_data += "item_find=" + item_find_percent;
			if(armor_name.contains("of")){
				armor_name = armor_name + " Treasure";
			}	
			else{
				armor_name = armor_name + " of Treasure";
			}
			armor_description += red.toString() + "ITEM FIND: " + item_find_percent + "%,";
		}


		if(thorns == true){
			armor_data += "thorns=" + thorns_percent + ":";;
			if(armor_name.contains("of")){
				armor_name = armor_name + " Spikes";
			}
			else{
				armor_name = armor_name + " of Thorns";
			}
			armor_description += red.toString() + "THORNS: " + thorns_percent + "% DMG,";
		}



		/*if(hp_regen == true){
			armor_data += "ls=" + hp_regen_percent + ":";
			armor_name = "Lifestealing " + armor_name; 
			armor_description += red.toString() + "LIFE STEAL: " + hp_regen_percent + "%,";
			// hp_regen == .split(":")[2]
		}


		if(block == true){
			armor_data += "crit=" + block_percent + ":";
			armor_name = "Deadly " + armor_name;
			armor_description += red.toString() + "CRITICAL HIT: " + block_percent + "%,";
		}


		if(hp_increase == true){
			armor_data += "edmg=" + element_dmg_type + "," + element_dmg_amount + ":";

			if(dodge == true){
				armor_name = armor_name + " " + element_dmg_type.substring(0, 1).toUpperCase() + element_dmg_type.substring(1, element_dmg_type.length());
			}
			if(dodge == false){
				armor_name = armor_name + " of " + element_dmg_type.substring(0, 1).toUpperCase() + element_dmg_type.substring(1, element_dmg_type.length());
			}

			armor_description += red.toString() + element_dmg_type.toUpperCase() + " DMG: +" + element_dmg_amount + ",";
			// hp_increase == .split(":")[3].split(",")[1]
			// edmg=fire,3
		}*/
		
		armor_description += "," + rarity;

		if(enchant_count > 0){
			armor_name = ChatColor.RED + "[+" + enchant_count + "]" + " " + tag.toString() + armor_name;;
		}
		else{
			armor_name = tag.toString() + armor_name;
		}
		//log.info(armor_name + " = " + armor_data);

		return ItemMechanics.signCustomItem(m, (short)0, armor_name, armor_description);
	}

	public static ItemStack BowGenorator(int tier, boolean reroll, ItemStack original){
		Random r = new Random();
		boolean life_steal = false, elemental_dmg = false, slow = false, crit_hit = false, blind = false/*, specific_dmg = false*/;
		int life_steal_percent = 0;
		int slow_percent = 0;
		int crit_hit_percent = 0;
		int blind_percent = 0;

		int life_steal_chance = 0;
		int slow_chance = 0;
		int crit_hit_chance = 0;
		int blind_chance = 0;
		int edmg_chance = 0;

		int life_steal_max = 0;
		int slow_max = 0;
		int crit_hit_max = 0;
		int blind_max = 0;
		int edmg_max = 0;
		
		int vs_modifier_max = 0;
		int vs_modifier_chance = 0;
		int vs_modifier_amount = 0;
		String vs_modifier_type = "";
		
		//int accuracy_chance = 0;
		//int accuracy_max = 0;
		//int accuracy_val = 0;
		//boolean accuracy = false;
		
		int element_dmg_amount = 0;
		String element_dmg_type = "";

		String wep_name = "";
		String wep_description = "";

		ChatColor tag = ChatColor.WHITE;

		if(tier == 1){
			wep_name = "Shortbow";
			tag = ChatColor.WHITE;

			life_steal_chance = 2;
			slow_chance = 3;
			crit_hit_chance = 2;
			blind_chance = 3;
			edmg_chance = 6;

			vs_modifier_chance = 6;
			vs_modifier_max = 10;
			
			//accuracy_chance = 8;
			//accuracy_max = 10;
			
			life_steal_max = 30;
			slow_max = 3;
			crit_hit_max = 2;
			blind_max = 5;
			edmg_max = 8;
		}
		if(tier == 2){
			wep_name = "Longbow";
			tag = ChatColor.GREEN;

			life_steal_chance = 4;
			slow_chance = 10;
			crit_hit_chance = 5;
			blind_chance = 5;
			edmg_chance = 9;
			
			vs_modifier_chance = 9;
			vs_modifier_max = 12;
			
			//accuracy_chance = 12;
			//accuracy_max = 12;

			life_steal_max = 15;
			slow_max = 4;
			crit_hit_max = 4;
			blind_max = 7;
			edmg_max = 15;
		}
		if(tier == 3){
			wep_name = "Magic Bow";
			tag = ChatColor.AQUA;

			life_steal_chance = 5;
			slow_chance = 13;
			crit_hit_chance = 8;
			blind_chance = 8;
			edmg_chance = 10;
			
			vs_modifier_chance = 10;
			vs_modifier_max = 15;
			
			//accuracy_chance = 15;
			//accuracy_max = 25;
			
			life_steal_max = 12;
			slow_max = 5;
			crit_hit_max = 5;
			blind_max = 9;
			edmg_max = 25;
		}
		if(tier == 4){
			wep_name = "Ancient Bow";
			tag = ChatColor.LIGHT_PURPLE;

			life_steal_chance = 7;
			slow_chance = 16;
			crit_hit_chance = 9;
			blind_chance = 9;
			edmg_chance = 15;
			
			vs_modifier_chance = 12;
			vs_modifier_max = 20;

			//accuracy_chance = 20;
			//accuracy_max = 28;
			
			life_steal_max = 10;
			slow_max = 7;
			crit_hit_max = 6;
			blind_max = 9;
			edmg_max = 45;
		}

		if(tier == 5){
			wep_name = "Legendary Bow";
			tag = ChatColor.YELLOW;

			life_steal_chance = 8;
			slow_chance = 20;
			crit_hit_chance = 7;
			blind_chance = 11;
			edmg_chance = 20;
			
			vs_modifier_chance = 15;
			vs_modifier_max = 15;

			//accuracy_chance = 15;
			//accuracy_max = 35;
			
			life_steal_max = 8;
			slow_max = 10;
			crit_hit_max = 10;
			blind_max = 11;
			edmg_max = 75;
		}

		int dmg_range_check = new Random().nextInt(100);
		double min_dmg = 0;
		double max_dmg = 0;
		String rarity = "";
		
		if(dmg_range_check <= 80){ // Tier 1 (low)
			rarity = ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + "Common";
			if(tier == 1){
				int min_min_dmg = 1;
				int max_min_dmg = 2 + 1; // +1 to offset the random gayness.

				int max_max_dmg = 3 + 1; // ^

				min_dmg = r.nextInt(max_min_dmg - min_min_dmg) + min_min_dmg;
				max_dmg = r.nextInt(max_max_dmg - min_min_dmg) + min_dmg;
			}
			if(tier == 2){
				int min_min_dmg = 10;
				int max_min_dmg = 12 + 1; // +1 to offset the random gayness.

				int max_max_dmg = 15 + 1; // ^

				min_dmg = r.nextInt(max_min_dmg - min_min_dmg) + min_min_dmg;
				max_dmg = r.nextInt(max_max_dmg - min_min_dmg) + min_dmg;
			}
			if(tier == 3){
				int min_min_dmg = 25;
				int max_min_dmg = 30 + 1; // +1 to offset the random gayness.

				int max_max_dmg = 40 + 1; // ^

				min_dmg = r.nextInt(max_min_dmg - min_min_dmg) + min_min_dmg;
				max_dmg = r.nextInt(max_max_dmg - min_min_dmg) + min_dmg;
			}
			if(tier == 4){
				int min_min_dmg = 65;
				int max_min_dmg = 80 + 1; // +1 to offset the random gayness.

				int max_max_dmg = 110 + 1; // ^

				min_dmg = r.nextInt(max_min_dmg - min_min_dmg) + min_min_dmg;
				max_dmg = r.nextInt(max_max_dmg - min_min_dmg) + min_dmg;
			}
			if(tier == 5){
				int min_min_dmg = 130;
				int max_min_dmg = 140 + 1; // +1 to offset the random gayness.

				int max_max_dmg = 200 + 1; // ^

				min_dmg = r.nextInt(max_min_dmg - min_min_dmg) + min_min_dmg;
				max_dmg = r.nextInt(max_max_dmg - min_min_dmg) + min_dmg;
			}
		}

		if(dmg_range_check > 80 && dmg_range_check < 95){ // Tier 1 (med)
			rarity = ChatColor.GREEN.toString() + ChatColor.ITALIC.toString() + "Uncommon";
			if(tier == 1){
				int min_min_dmg = 3;
				int max_min_dmg = 5 + 1; // +1 to offset the random gayness.

				int max_max_dmg = 6 + 1; // ^

				min_dmg = r.nextInt(max_min_dmg - min_min_dmg) + min_min_dmg;
				max_dmg = r.nextInt(max_max_dmg - min_min_dmg) + min_dmg;
			}
			if(tier == 2){
				int min_min_dmg = 16;
				int max_min_dmg = 18 + 1; // +1 to offset the random gayness.

				int max_max_dmg = 22 + 1; // ^

				min_dmg = r.nextInt(max_min_dmg - min_min_dmg) + min_min_dmg;
				max_dmg = r.nextInt(max_max_dmg - min_min_dmg) + min_dmg;
			}
			if(tier == 3){
				int min_min_dmg = 30;
				int max_min_dmg = 35 + 1; // +1 to offset the random gayness.

				int max_max_dmg = 65 + 1; // ^

				min_dmg = r.nextInt(max_min_dmg - min_min_dmg) + min_min_dmg;
				max_dmg = r.nextInt(max_max_dmg - min_min_dmg) + min_dmg;
			}
			if(tier == 4){
				int min_min_dmg = 70;
				int max_min_dmg = 85 + 1; // +1 to offset the random gayness.

				int max_max_dmg = 140 + 1; // ^

				min_dmg = r.nextInt(max_min_dmg - min_min_dmg) + min_min_dmg;
				max_dmg = r.nextInt(max_max_dmg - min_min_dmg) + min_dmg;
			}
			if(tier == 5){
				int min_min_dmg = 150;
				int max_min_dmg = 160 + 1; // +1 to offset the random gayness.

				int max_max_dmg = 250 + 1; // ^

				min_dmg = r.nextInt(max_min_dmg - min_min_dmg) + min_min_dmg;
				max_dmg = r.nextInt(max_max_dmg - min_min_dmg) + min_dmg;
			}
		}

		if(dmg_range_check >= 95 && dmg_range_check != 99){ // Tier 1 (high)
			rarity = ChatColor.AQUA.toString() + ChatColor.ITALIC.toString() + "Rare";
			if(tier == 1){
				int min_min_dmg = 6;
				int max_min_dmg = 9 + 1; // +1 to offset the random gayness.

				int max_max_dmg = 20 + 1; // ^

				min_dmg = r.nextInt(max_min_dmg - min_min_dmg) + min_min_dmg;
				max_dmg = r.nextInt(max_max_dmg - min_min_dmg) + min_dmg;
			}
			if(tier == 2){
				int min_min_dmg = 20;
				int max_min_dmg = 30 + 1; // +1 to offset the random gayness.

				int max_max_dmg = 55 + 1; // ^

				min_dmg = r.nextInt(max_min_dmg - min_min_dmg) + min_min_dmg;
				max_dmg = r.nextInt(max_max_dmg - min_min_dmg) + min_dmg;
			}
			if(tier == 3){
				int min_min_dmg = 30;
				int max_min_dmg = 60 + 1; // +1 to offset the random gayness.

				int max_max_dmg = 120 + 1; // ^

				min_dmg = r.nextInt(max_min_dmg - min_min_dmg) + min_min_dmg;
				max_dmg = r.nextInt(max_max_dmg - min_min_dmg) + min_dmg;
			}
			if(tier == 4){
				int min_min_dmg = 90;
				int max_min_dmg = 110 + 1; // +1 to offset the random gayness.

				int max_max_dmg = 210 + 1; // ^

				min_dmg = r.nextInt(max_min_dmg - min_min_dmg) + min_min_dmg;
				max_dmg = r.nextInt(max_max_dmg - min_min_dmg) + min_dmg;
			}
			if(tier == 5){
				int min_min_dmg = 160;
				int max_min_dmg = 230 + 1; // +1 to offset the random gayness.

				int max_max_dmg = 337 + 1; // ^

				min_dmg = r.nextInt(max_min_dmg - min_min_dmg) + min_min_dmg;
				max_dmg = r.nextInt(max_max_dmg - min_min_dmg) + min_dmg;
			}
		}
		
		if(dmg_range_check == 99){ // Tier 1 (high)
			rarity = ChatColor.YELLOW.toString() + ChatColor.ITALIC.toString() + "Unique";
			if(tier == 1){
				int min_min_dmg = 6;
				int max_min_dmg = 9 + 1; // +1 to offset the random gayness.

				int max_max_dmg = 20 + 1; // ^

				min_dmg = r.nextInt(max_min_dmg - min_min_dmg) + min_min_dmg;
				max_dmg = r.nextInt(max_max_dmg - min_min_dmg) + min_dmg;
			}
			if(tier == 2){
				int min_min_dmg = 20;
				int max_min_dmg = 30 + 1; // +1 to offset the random gayness.

				int max_max_dmg = 55 + 1; // ^

				min_dmg = r.nextInt(max_min_dmg - min_min_dmg) + min_min_dmg;
				max_dmg = r.nextInt(max_max_dmg - min_min_dmg) + min_dmg;
			}
			if(tier == 3){
				int min_min_dmg = 30;
				int max_min_dmg = 60 + 1; // +1 to offset the random gayness.

				int max_max_dmg = 120 + 1; // ^

				min_dmg = r.nextInt(max_min_dmg - min_min_dmg) + min_min_dmg;
				max_dmg = r.nextInt(max_max_dmg - min_min_dmg) + min_dmg;
			}
			if(tier == 4){
				int min_min_dmg = 90;
				int max_min_dmg = 110 + 1; // +1 to offset the random gayness.

				int max_max_dmg = 210 + 1; // ^

				min_dmg = r.nextInt(max_min_dmg - min_min_dmg) + min_min_dmg;
				max_dmg = r.nextInt(max_max_dmg - min_min_dmg) + min_dmg;
			}
			if(tier == 5){
				int min_min_dmg = 160;
				int max_min_dmg = 230 + 1; // +1 to offset the random gayness.

				int max_max_dmg = 337 + 1; // ^

				min_dmg = r.nextInt(max_min_dmg - min_min_dmg) + min_min_dmg;
				max_dmg = r.nextInt(max_max_dmg - min_min_dmg) + min_dmg;
			}
			
			min_dmg *= 1.1;
			max_dmg *= 1.1;
		}
		
		min_dmg = Math.round(min_dmg * 2.0);
		max_dmg = Math.round(max_dmg * 2.0);

		if(min_dmg < 1){
			min_dmg = 1;
		}
		
		if(max_dmg < 1){
			max_dmg = 1;
		}

		if(r.nextInt(100) <= life_steal_chance){ 
			life_steal = true;
			life_steal_percent = r.nextInt(life_steal_max) + 1; 
		}

		if(r.nextInt(100) <= edmg_chance){
			elemental_dmg = true;
			element_dmg_amount = r.nextInt(edmg_max) + 1;
			int elem_type_r = r.nextInt(3);
			if(elem_type_r == 0){
				element_dmg_type = "fire";
				element_dmg_amount -= (int)((double)element_dmg_amount * 0.15D);
			}
			if(elem_type_r == 1){
				element_dmg_type = "ice";
			}
			if(elem_type_r == 2){
				element_dmg_type = "poison";
				element_dmg_amount -= (int)((double)element_dmg_amount * 0.10D);
			}
		}

		if(r.nextInt(100) <= slow_chance){
			slow = true;
			slow_percent = r.nextInt(slow_max) + 1; 
		}

		if(r.nextInt(100) <= crit_hit_chance){ 
			crit_hit = true;
			crit_hit_percent = r.nextInt(crit_hit_max) + 1; // 1 - 10%
		}

		if(r.nextInt(100) <= blind_chance){ // 1% chance.
			blind = true;
			blind_percent = r.nextInt(blind_max) + 1; // 1 - 3%
		}

		int enchant_count = 0;
		// x = durability
		if(reroll == true && original != null){ // Keep the old DMG values instead.
			rarity = ItemMechanics.getItemRarity(original);
			enchant_count = EnchantMechanics.getEnchantCount(original);
			List<Integer> dmg_values = ItemMechanics.getDmgRangeOfWeapon(original);
			min_dmg = dmg_values.get(0);
			max_dmg = dmg_values.get(1);
		}
		@SuppressWarnings("unused")
		String weapon_data = "BOW" + "#" + min_dmg + "-" + max_dmg + ":";
		wep_description += red.toString() + "DMG: " + (int)min_dmg + " - " + (int)max_dmg + ",";
		// wood_bow,1|23-25:ls=3:edmg=fire,2:slow=3

		int stat_chance = r.nextInt(100);
		int stat_type = r.nextInt(4); // 0, 1, 2
		String stat_string = "";
		String short_stat_string = "";
		int stat_val = 1;
		if(tier == 1){
			stat_val = new Random().nextInt(15) + 1;
			if(stat_chance <= 25){
				stat_chance = r.nextInt(100);
				if(stat_type == 0){
					stat_string = "strength";
					short_stat_string = "STR";
				}
				if(stat_type == 1){
					stat_string = "dexterity";
					short_stat_string = "DEX";
				}
				if(stat_type == 2){
					stat_string = "vitality";
					short_stat_string = "VIT";
				}
				if(stat_type == 3){
					stat_string = "intellect";
					short_stat_string = "INT";
				}
				
				weapon_data += stat_string + "=" + stat_val + ":";
				wep_description += red.toString() + short_stat_string + ": +" + stat_val + ",";
			}
		}
		if(tier == 2){
			if(stat_chance <= 20){
				stat_val = new Random().nextInt(35) + 1;
				stat_chance = r.nextInt(100);
				if(stat_type == 0){
					stat_string = "strength";
					short_stat_string = "STR";
				}
				if(stat_type == 1){
					stat_string = "dexterity";
					short_stat_string = "DEX";
				}
				if(stat_type == 2){
					stat_string = "vitality";
					short_stat_string = "VIT";
				}
				if(stat_type == 3){
					stat_string = "intellect";
					short_stat_string = "INT";
				}
				

				weapon_data += stat_string + "=" + stat_val + ":";
				wep_description += red.toString() + short_stat_string + ": +" + stat_val + ",";
			}
			if(stat_chance <= 5){
				stat_val = new Random().nextInt(35) + 1;
				stat_chance = r.nextInt(100);
				if(stat_type >= 0 && stat_type < 3){
					stat_type++;
				}
				else if(stat_type == 3){
					stat_type--;
				}

				if(stat_type == 0){
					stat_string = "strength";
					short_stat_string = "STR";
				}
				if(stat_type == 1){
					stat_string = "dexterity";
					short_stat_string = "DEX";
				}
				if(stat_type == 2){
					stat_string = "vitality";
					short_stat_string = "VIT";
				}
				if(stat_type == 3){
					stat_string = "intellect";
					short_stat_string = "INT";
				}
				

				weapon_data += stat_string + "=" + stat_val + ":";
				wep_description += red.toString() + short_stat_string + ": +" + stat_val + ",";
			}
		}
		if(tier == 3){
			if(stat_chance <= 15){
				stat_val = new Random().nextInt(75) + 1;
				stat_chance = r.nextInt(100);
				if(stat_type == 0){
					stat_string = "strength";
					short_stat_string = "STR";
				}
				if(stat_type == 1){
					stat_string = "dexterity";
					short_stat_string = "DEX";
				}
				if(stat_type == 2){
					stat_string = "vitality";
					short_stat_string = "VIT";
				}
				if(stat_type == 3){
					stat_string = "intellect";
					short_stat_string = "INT";
				}
				

				weapon_data += stat_string + "=" + stat_val + ":";
				wep_description += red.toString() + short_stat_string + ": +" + stat_val + ",";
			}
			if(stat_chance <= 5){
				stat_val = new Random().nextInt(75) + 1;
				stat_chance = r.nextInt(100);
				if(stat_type >= 0 && stat_type < 3){
					stat_type++;
				}
				else if(stat_type == 3){
					stat_type = 0;
				}

				if(stat_type == 0){
					stat_string = "strength";
					short_stat_string = "STR";
				}
				if(stat_type == 1){
					stat_string = "dexterity";
					short_stat_string = "DEX";
				}
				if(stat_type == 2){
					stat_string = "vitality";
					short_stat_string = "VIT";
				}
				if(stat_type == 3){
					stat_string = "intellect";
					short_stat_string = "INT";
				}
				

				weapon_data += stat_string + "=" + stat_val + ":";
				wep_description += red.toString() + short_stat_string + ": +" + stat_val + ",";
			}
			if(stat_chance <= 1){
				stat_val = new Random().nextInt(75) + 1;
				stat_chance = r.nextInt(100);
				if(stat_type >= 0 && stat_type < 3){
					stat_type++;
				}
				else if(stat_type == 3){
					stat_type = 0;
				}

				if(stat_type == 0){
					stat_string = "strength";
					short_stat_string = "STR";
				}
				if(stat_type == 1){
					stat_string = "dexterity";
					short_stat_string = "DEX";
				}
				if(stat_type == 2){
					stat_string = "vitality";
					short_stat_string = "VIT";
				}
				if(stat_type == 3){
					stat_string = "intellect";
					short_stat_string = "INT";
				}
				

				weapon_data += stat_string + "=" + stat_val + ":";
				wep_description += red.toString() + short_stat_string + ": +" + stat_val + ",";
			}
		}
		if(tier == 4){
			if(stat_chance <= 15){
				stat_val = new Random().nextInt(115) + 1;
				stat_chance = r.nextInt(100);
				if(stat_type == 0){
					stat_string = "strength";
					short_stat_string = "STR";
				}
				if(stat_type == 1){
					stat_string = "dexterity";
					short_stat_string = "DEX";
				}
				if(stat_type == 2){
					stat_string = "vitality";
					short_stat_string = "VIT";
				}
				if(stat_type == 3){
					stat_string = "intellect";
					short_stat_string = "INT";
				}
				

				weapon_data += stat_string + "=" + stat_val + ":";
				wep_description += red.toString() + short_stat_string + ": +" + stat_val + ",";
			}
			if(stat_chance <= 9){
				stat_val = new Random().nextInt(115) + 1;
				stat_chance = r.nextInt(100);
				if(stat_type >= 0 && stat_type < 3){
					stat_type++;
				}
				else if(stat_type == 3){
					stat_type = 0;
				}

				if(stat_type == 0){
					stat_string = "strength";
					short_stat_string = "STR";
				}
				if(stat_type == 1){
					stat_string = "dexterity";
					short_stat_string = "DEX";
				}
				if(stat_type == 2){
					stat_string = "vitality";
					short_stat_string = "VIT";
				}
				if(stat_type == 3){
					stat_string = "intellect";
					short_stat_string = "INT";
				}
				

				weapon_data += stat_string + "=" + stat_val + ":";
				wep_description += red.toString() + short_stat_string + ": +" + stat_val + ",";
			}
			if(stat_chance <= 4){
				stat_val = new Random().nextInt(115) + 1;
				stat_chance = r.nextInt(100);
				if(stat_type >= 0 && stat_type < 3){
					stat_type++;
				}
				else if(stat_type == 3){
					stat_type = 0;
				}

				if(stat_type == 0){
					stat_string = "strength";
					short_stat_string = "STR";
				}
				if(stat_type == 1){
					stat_string = "dexterity";
					short_stat_string = "DEX";
				}
				if(stat_type == 2){
					stat_string = "vitality";
					short_stat_string = "VIT";
				}
				if(stat_type == 3){
					stat_string = "intellect";
					short_stat_string = "INT";
				}
				

				weapon_data += stat_string + "=" + stat_val + ":";
				wep_description += red.toString() + short_stat_string + ": +" + stat_val + ",";
			}
		}
		if(tier == 5){
			if(stat_chance <= 20){
				stat_val = new Random().nextInt(315) + 1;
				stat_chance = r.nextInt(100);
				if(stat_type == 0){
					stat_string = "strength";
					short_stat_string = "STR";
				}
				if(stat_type == 1){
					stat_string = "dexterity";
					short_stat_string = "DEX";
				}
				if(stat_type == 2){
					stat_string = "vitality";
					short_stat_string = "VIT";
				}
				if(stat_type == 3){
					stat_string = "intellect";
					short_stat_string = "INT";
				}
				

				weapon_data += stat_string + "=" + stat_val + ":";
				wep_description += red.toString() + short_stat_string + ": +" + stat_val + ",";
			}
			if(stat_chance <= 10){
				stat_val = new Random().nextInt(315) + 1;
				stat_chance = r.nextInt(100);
				if(stat_type >= 0 && stat_type < 3){
					stat_type++;
				}
				else if(stat_type == 3){
					stat_type = 0;
				}

				if(stat_type == 0){
					stat_string = "strength";
					short_stat_string = "STR";
				}
				if(stat_type == 1){
					stat_string = "dexterity";
					short_stat_string = "DEX";
				}
				if(stat_type == 2){
					stat_string = "vitality";
					short_stat_string = "VIT";
				}
				if(stat_type == 3){
					stat_string = "intellect";
					short_stat_string = "INT";
				}
				

				weapon_data += stat_string + "=" + stat_val + ":";
				wep_description += red.toString() + short_stat_string + ": +" + stat_val + ",";
			}
			if(stat_chance <= 5){
				stat_val = new Random().nextInt(315) + 1;
				if(stat_type >= 0 && stat_type < 3){
					stat_type++;
				}
				else if(stat_type == 3){
					stat_type = 0;
				}

				if(stat_type == 0){
					stat_string = "strength";
					short_stat_string = "STR";
				}
				if(stat_type == 1){
					stat_string = "dexterity";
					short_stat_string = "DEX";
				}
				if(stat_type == 2){
					stat_string = "vitality";
					short_stat_string = "VIT";
				}
				if(stat_type == 3){
					stat_string = "intellect";
					short_stat_string = "INT";
				}
				

				weapon_data += stat_string + "=" + stat_val + ":";
				wep_description += red.toString() + short_stat_string + ": +" + stat_val + ",";
			}
		}

		if(slow == true){
			weapon_data += "slow=" + slow_percent + ":";
			wep_name = "Snaring " + wep_name;
			wep_description += red.toString() + "SLOW: " + slow_percent + "%,";
			// Take from first index of slow= to next index of : from that point.
		}

		if(blind == true){
			weapon_data += "blind=" + blind_percent;
			wep_name = wep_name + " of Blindness";
			wep_description += red.toString() + "BLIND: " + blind_percent + "%,";
		}

		if(life_steal == true){
			weapon_data += "ls=" + life_steal_percent + ":";
			wep_name = "Lifestealing " + wep_name; 
			wep_description += red.toString() + "LIFE STEAL: " + life_steal_percent + "%,";
			// life_steal == .split(":")[2]
		}


		if(crit_hit == true){
			weapon_data += "crit=" + crit_hit_percent + ":";
			wep_name = "Deadly " + wep_name;
			wep_description += red.toString() + "CRITICAL HIT: " + crit_hit_percent + "%,";
		}


		if(vs_modifier_max > 0 && r.nextInt(100) <= vs_modifier_chance){
			vs_modifier_amount = r.nextInt(vs_modifier_max) + 1;
			int type = r.nextInt(2);
			if(type == 0){
				vs_modifier_type = "monsters";
			}
			else if(type == 1){
				vs_modifier_type = "players";
			}
		}
		
		if(vs_modifier_amount > 0){
			String vs_modifier_data = "vs_" + vs_modifier_type;
			weapon_data += vs_modifier_data + "=" + vs_modifier_amount + ":";
			if(vs_modifier_data.contains("monsters")){
				if(wep_name.contains("of")){
					wep_name = wep_name + " Slaying";
				}
				else{
					wep_name = wep_name + " of Slaying";
				}
			}
			if(vs_modifier_data.contains("players")){
				if(wep_name.contains("of")){
					wep_name = wep_name + " Slaughter";
				}
				else{
					wep_name = wep_name + " of Slaughter";
				}
			}
			wep_description += red.toString() + "vs. " + vs_modifier_type.toUpperCase() + ": +" + vs_modifier_amount + "% DMG,";
		}
		
		if(elemental_dmg == true){
			weapon_data += "edmg=" + element_dmg_type + "," + element_dmg_amount + ":";

			if(blind == true || wep_name.contains("of")){
				wep_name = wep_name + " " + element_dmg_type.substring(0, 1).toUpperCase() + element_dmg_type.substring(1, element_dmg_type.length());
			}
			if(blind == false){
				wep_name = wep_name + " of " + element_dmg_type.substring(0, 1).toUpperCase() + element_dmg_type.substring(1, element_dmg_type.length());
			}

			wep_description += red.toString() + element_dmg_type.toUpperCase() + " DMG: +" + element_dmg_amount + ",";
			// elemental_dmg == .split(":")[3].split(",")[1]
			// edmg=fire,3
		}

		wep_description += rarity;
		
		if(enchant_count > 0){
			wep_name = ChatColor.RED + "[+" + enchant_count + "]" + " " + tag.toString() + wep_name;
		}
		else{
			wep_name = tag.toString() + wep_name;
		}
		//log.info(wep_name + " = " + weapon_data);

		return ItemMechanics.signCustomItem(Material.BOW, (short)0, wep_name, wep_description);
	}

	public static ItemStack AxeGenorator(Material m, boolean reroll, ItemStack original){
		Random r = new Random();
		boolean life_steal = false, elemental_dmg = false, knockback = false, crit_hit = false, blind = false, /*specific_dmg = false,*/ pure_dmg = false, armor_pen = false;
		//int axe_modifier = 0;

		int life_steal_percent = 0;
		int knockback_percent = 0;
		int crit_hit_percent = 0;
		int blind_percent = 0;

		int life_steal_chance = 0;
		int knockback_chance = 0;
		int crit_hit_chance = 0;
		int blind_chance = 0;
		int edmg_chance = 0;

		int life_steal_max = 0;
		int knockback_max = 0;
		int crit_hit_max = 0;
		int blind_max = 0;
		int edmg_max = 0;
		//int energy_cost = 0;
		
		int vs_modifier_max = 0;
		int vs_modifier_chance = 0;
		int vs_modifier_amount = 0;
		String vs_modifier_type = "";

		int pure_dmg_chance = 0;
		int pure_dmg_max = 0;
		int pure_dmg_val = 0;

		//int accuracy_chance = 0;
		//int accuracy_max = 0;
		int accuracy_val = 0;
		boolean accuracy = false;
		
		int armor_pen_chance = 0;
		int armor_pen_max = 0;
		int armor_pen_val = 0;


		int tier = 0;
		int element_dmg_amount = 0;
		String element_dmg_type = "";

		String wep_name = "";
		String wep_description = "";

		ChatColor tag = ChatColor.WHITE;

		if(m == Material.WOOD_AXE){
			wep_name = "Hatchet";
			tag = ChatColor.WHITE;
			//energy_cost = 12;
			tier = 1;

			life_steal_chance = 2;
			knockback_chance = 5;
			crit_hit_chance = 4;
			blind_chance = 3;
			edmg_chance = 6;

			vs_modifier_chance = 6;
			vs_modifier_max = 10;
			
			//accuracy_chance = 8;
			//accuracy_max = 10;
			
			pure_dmg_chance = 6;
			armor_pen_chance = 20;
			pure_dmg_max = 5;
			armor_pen_max = 1;

			life_steal_max = 30;
			knockback_max = 4;
			crit_hit_max = 3;
			blind_max = 5;
			edmg_max = 4;

		}
		if(m == Material.STONE_AXE){
			wep_name = "Great Axe";
			tag = ChatColor.GREEN;
			//energy_cost = 14;
			tier = 2;

			life_steal_chance = 4;
			knockback_chance = 13;
			crit_hit_chance = 6;
			blind_chance = 5;
			edmg_chance = 9;

			vs_modifier_chance = 9;
			vs_modifier_max = 12;
			
			//accuracy_chance = 12;
			//accuracy_max = 12;
			
			pure_dmg_chance = 9;
			armor_pen_chance = 20;
			pure_dmg_max = 8;
			armor_pen_max = 3;

			life_steal_max = 15;
			knockback_max = 8;
			crit_hit_max = 6;
			blind_max = 7;
			edmg_max = 9;

		}
		if(m == Material.IRON_AXE){
			wep_name = "War Axe";
			tag = ChatColor.AQUA;
			//energy_cost = 16;
			tier = 3;

			life_steal_chance = 5;
			knockback_chance = 15;
			crit_hit_chance = 10;
			blind_chance = 8;
			edmg_chance = 10;

			vs_modifier_chance = 10;
			vs_modifier_max = 15;
			
			//accuracy_chance = 15;
			//accuracy_max = 25;
			
			pure_dmg_chance = 5;
			armor_pen_chance = 25;
			pure_dmg_max = 15;
			armor_pen_max = 5;

			life_steal_max = 12;
			knockback_max = 12;
			crit_hit_max = 8;
			blind_max = 9;
			edmg_max = 15;
		}
		if(m == Material.DIAMOND_AXE){
			wep_name = "Ancient Axe";
			tag = ChatColor.LIGHT_PURPLE;
			//energy_cost = 20;
			tier = 4;

			life_steal_chance = 7;
			knockback_chance = 19;
			crit_hit_chance = 12;
			blind_chance = 9;
			edmg_chance = 15;

			vs_modifier_chance = 12;
			vs_modifier_max = 20;
			
			//accuracy_chance = 20;
			//accuracy_max = 28;
			
			pure_dmg_chance = 5;
			armor_pen_chance = 20;
			pure_dmg_max = 25;
			armor_pen_max = 8;

			life_steal_max = 10;
			knockback_max = 17;
			crit_hit_max = 10;
			blind_max = 9;
			edmg_max = 25;
		}

		if(m == Material.GOLD_AXE){
			wep_name = "Legendary Axe";
			tag = ChatColor.YELLOW;
			//energy_cost = 25;
			tier = 5;

			life_steal_chance = 8;
			knockback_chance = 20;
			crit_hit_chance = 12;
			blind_chance = 11;
			edmg_chance = 20;

			vs_modifier_chance = 15;
			vs_modifier_max = 15;
			
			//accuracy_chance = 15;
			//accuracy_max = 35;
			
			pure_dmg_chance = 10;
			armor_pen_chance = 15;
			pure_dmg_max = 45;
			armor_pen_max = 10;

			life_steal_max = 8;
			knockback_max = 25;
			crit_hit_max = 11;
			blind_max = 11;
			edmg_max = 55;
		}

		int dmg_range_check = new Random().nextInt(100);
		int min_dmg = 0;
		int max_dmg = 0;

		String rarity = "";
		
		if(dmg_range_check <= 80){ // Tier 1 (low)
			rarity = ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + "Common";
			if(m == Material.WOOD_AXE){
				int min_min_dmg = 1;
				int max_min_dmg = 2 + 1; // +1 to offset the random gayness.

				int max_max_dmg = 3 + 1; // ^

				min_dmg = r.nextInt(max_min_dmg - min_min_dmg) + min_min_dmg;
				max_dmg = r.nextInt(max_max_dmg - min_min_dmg) + min_dmg;
			}
			if(m == Material.STONE_AXE){
				int min_min_dmg = 10;
				int max_min_dmg = 12 + 1; // +1 to offset the random gayness.

				int max_max_dmg = 15 + 1; // ^

				min_dmg = r.nextInt(max_min_dmg - min_min_dmg) + min_min_dmg;
				max_dmg = r.nextInt(max_max_dmg - min_min_dmg) + min_dmg;
			}
			if(m == Material.IRON_AXE){
				int min_min_dmg = 25;
				int max_min_dmg = 30 + 1; // +1 to offset the random gayness.

				int max_max_dmg = 40 + 1; // ^

				min_dmg = r.nextInt(max_min_dmg - min_min_dmg) + min_min_dmg;
				max_dmg = r.nextInt(max_max_dmg - min_min_dmg) + min_dmg;
			}
			if(m == Material.DIAMOND_AXE){
				int min_min_dmg = 65;
				int max_min_dmg = 80 + 1; // +1 to offset the random gayness.

				int max_max_dmg = 110 + 1; // ^

				min_dmg = r.nextInt(max_min_dmg - min_min_dmg) + min_min_dmg;
				max_dmg = r.nextInt(max_max_dmg - min_min_dmg) + min_dmg;
			}
			if(m == Material.GOLD_AXE){
				int min_min_dmg = 130;
				int max_min_dmg = 140 + 1; // +1 to offset the random gayness.

				int max_max_dmg = 200 + 1; // ^

				min_dmg = r.nextInt(max_min_dmg - min_min_dmg) + min_min_dmg;
				max_dmg = r.nextInt(max_max_dmg - min_min_dmg) + min_dmg;
			}
		}

		if(dmg_range_check > 80 && dmg_range_check < 95){ // Tier 1 (med)
			rarity = ChatColor.GREEN.toString() + ChatColor.ITALIC.toString() + "Uncommon";
			if(m == Material.WOOD_AXE){
				int min_min_dmg = 3;
				int max_min_dmg = 5 + 1; // +1 to offset the random gayness.

				int max_max_dmg = 6 + 1; // ^

				min_dmg = r.nextInt(max_min_dmg - min_min_dmg) + min_min_dmg;
				max_dmg = r.nextInt(max_max_dmg - min_min_dmg) + min_dmg;
			}
			if(m == Material.STONE_AXE){
				int min_min_dmg = 16;
				int max_min_dmg = 18 + 1; // +1 to offset the random gayness.

				int max_max_dmg = 22 + 1; // ^

				min_dmg = r.nextInt(max_min_dmg - min_min_dmg) + min_min_dmg;
				max_dmg = r.nextInt(max_max_dmg - min_min_dmg) + min_dmg;
			}
			if(m == Material.IRON_AXE){
				int min_min_dmg = 30;
				int max_min_dmg = 35 + 1; // +1 to offset the random gayness.

				int max_max_dmg = 65 + 1; // ^

				min_dmg = r.nextInt(max_min_dmg - min_min_dmg) + min_min_dmg;
				max_dmg = r.nextInt(max_max_dmg - min_min_dmg) + min_dmg;
			}
			if(m == Material.DIAMOND_AXE){
				int min_min_dmg = 70;
				int max_min_dmg = 85 + 1; // +1 to offset the random gayness.

				int max_max_dmg = 140 + 1; // ^

				min_dmg = r.nextInt(max_min_dmg - min_min_dmg) + min_min_dmg;
				max_dmg = r.nextInt(max_max_dmg - min_min_dmg) + min_dmg;
			}
			if(m == Material.GOLD_AXE){
				int min_min_dmg = 150;
				int max_min_dmg = 160 + 1; // +1 to offset the random gayness.

				int max_max_dmg = 250 + 1; // ^

				min_dmg = r.nextInt(max_min_dmg - min_min_dmg) + min_min_dmg;
				max_dmg = r.nextInt(max_max_dmg - min_min_dmg) + min_dmg;
			}
		}

		if(dmg_range_check >= 95 && dmg_range_check != 99){ // Tier 1 (high)
			rarity = ChatColor.AQUA.toString() + ChatColor.ITALIC.toString() + "Rare";
			if(m == Material.WOOD_AXE){
				int min_min_dmg = 6;
				int max_min_dmg = 9 + 1; // +1 to offset the random gayness.

				int max_max_dmg = 20 + 1; // ^

				min_dmg = r.nextInt(max_min_dmg - min_min_dmg) + min_min_dmg;
				max_dmg = r.nextInt(max_max_dmg - min_min_dmg) + min_dmg;
			}
			if(m == Material.STONE_AXE){
				int min_min_dmg = 20;
				int max_min_dmg = 30 + 1; // +1 to offset the random gayness.

				int max_max_dmg = 55 + 1; // ^

				min_dmg = r.nextInt(max_min_dmg - min_min_dmg) + min_min_dmg;
				max_dmg = r.nextInt(max_max_dmg - min_min_dmg) + min_dmg;
			}
			if(m == Material.IRON_AXE){
				int min_min_dmg = 30;
				int max_min_dmg = 60 + 1; // +1 to offset the random gayness.

				int max_max_dmg = 120 + 1; // ^

				min_dmg = r.nextInt(max_min_dmg - min_min_dmg) + min_min_dmg;
				max_dmg = r.nextInt(max_max_dmg - min_min_dmg) + min_dmg;
			}
			if(m == Material.DIAMOND_AXE){
				int min_min_dmg = 90;
				int max_min_dmg = 110 + 1; // +1 to offset the random gayness.

				int max_max_dmg = 210 + 1; // ^

				min_dmg = r.nextInt(max_min_dmg - min_min_dmg) + min_min_dmg;
				max_dmg = r.nextInt(max_max_dmg - min_min_dmg) + min_dmg;
			}
			if(m == Material.GOLD_AXE){
				int min_min_dmg = 160;
				int max_min_dmg = 230 + 1; // +1 to offset the random gayness.

				int max_max_dmg = 337 + 1; // ^

				min_dmg = r.nextInt(max_min_dmg - min_min_dmg) + min_min_dmg;
				max_dmg = r.nextInt(max_max_dmg - min_min_dmg) + min_dmg;
			}
		}
		
		if(dmg_range_check == 99){ // Tier 1 (high)
			rarity = ChatColor.YELLOW.toString() + ChatColor.ITALIC.toString() + "Unique";
			if(m == Material.WOOD_AXE){
				int min_min_dmg = 6;
				int max_min_dmg = 9 + 1; // +1 to offset the random gayness.

				int max_max_dmg = 20 + 1; // ^

				min_dmg = r.nextInt(max_min_dmg - min_min_dmg) + min_min_dmg;
				max_dmg = r.nextInt(max_max_dmg - min_min_dmg) + min_dmg;
			}
			if(m == Material.STONE_AXE){
				int min_min_dmg = 20;
				int max_min_dmg = 30 + 1; // +1 to offset the random gayness.

				int max_max_dmg = 55 + 1; // ^

				min_dmg = r.nextInt(max_min_dmg - min_min_dmg) + min_min_dmg;
				max_dmg = r.nextInt(max_max_dmg - min_min_dmg) + min_dmg;
			}
			if(m == Material.IRON_AXE){
				int min_min_dmg = 30;
				int max_min_dmg = 60 + 1; // +1 to offset the random gayness.

				int max_max_dmg = 120 + 1; // ^

				min_dmg = r.nextInt(max_min_dmg - min_min_dmg) + min_min_dmg;
				max_dmg = r.nextInt(max_max_dmg - min_min_dmg) + min_dmg;
			}
			if(m == Material.DIAMOND_AXE){
				int min_min_dmg = 90;
				int max_min_dmg = 110 + 1; // +1 to offset the random gayness.

				int max_max_dmg = 210 + 1; // ^

				min_dmg = r.nextInt(max_min_dmg - min_min_dmg) + min_min_dmg;
				max_dmg = r.nextInt(max_max_dmg - min_min_dmg) + min_dmg;
			}
			if(m == Material.GOLD_AXE){
				int min_min_dmg = 160;
				int max_min_dmg = 230 + 1; // +1 to offset the random gayness.

				int max_max_dmg = 337 + 1; // ^

				min_dmg = r.nextInt(max_min_dmg - min_min_dmg) + min_min_dmg;
				max_dmg = r.nextInt(max_max_dmg - min_min_dmg) + min_dmg;
			}
			
			min_dmg = (int)(min_dmg * 1.1);
			max_dmg = (int)(max_dmg * 1.1);
		}
		
		min_dmg *= 1.2;
		max_dmg *= 1.2;

		if(min_dmg < 1){
			min_dmg = 1;
		}
		if(max_dmg < 1){
			max_dmg = 1;
		}

		if(r.nextInt(100) <= life_steal_chance){ 
			life_steal = true;
			life_steal_percent = r.nextInt(life_steal_max) + 1; 
		}

		if(r.nextInt(100) <= edmg_chance){
			elemental_dmg = true;
			element_dmg_amount = r.nextInt(edmg_max) + 1;
			int elem_type_r = r.nextInt(3);
			if(elem_type_r == 0){
				element_dmg_type = "fire";
				element_dmg_amount -= (int)((double)element_dmg_amount * 0.15D);
			}
			if(elem_type_r == 1){
				element_dmg_type = "ice";
			}
			if(elem_type_r == 2){
				element_dmg_type = "poison";
				element_dmg_amount -= (int)((double)element_dmg_amount * 0.10D);
			}
		}

		if(r.nextInt(100) <= pure_dmg_chance){
			pure_dmg = true;
			pure_dmg_val = r.nextInt(pure_dmg_max) + 1;
		}

		if(r.nextInt(100) <= armor_pen_chance){
			armor_pen = true;
			armor_pen_val = r.nextInt(armor_pen_max) + 1;
		}

		if(r.nextInt(100) <= knockback_chance){
			knockback = true;
			knockback_percent = r.nextInt(knockback_max) + 1; 
		}

		if(r.nextInt(100) <= crit_hit_chance){ 
			crit_hit = true;
			crit_hit_percent = r.nextInt(crit_hit_max) + 1; // 1 - 10%
		}

		if(r.nextInt(100) <= blind_chance){ // 1% chance.
			blind = true;
			blind_percent = r.nextInt(blind_max) + 1; // 1 - 3%
		}

		int enchant_count = 0;
		// x = durability
		if(reroll == true && original != null){ // Keep the old DMG values instead.
			rarity = ItemMechanics.getItemRarity(original);
			enchant_count = EnchantMechanics.getEnchantCount(original);
			List<Integer> dmg_values = ItemMechanics.getDmgRangeOfWeapon(original);
			min_dmg = dmg_values.get(0);
			max_dmg = dmg_values.get(1);
		}
		@SuppressWarnings("unused")
		String weapon_data = m.name().toUpperCase() + "#" + min_dmg + "-" + max_dmg + ":";
		wep_description += red.toString() + "DMG: " + min_dmg + " - " + max_dmg + ",";
		// wood_axe,1|23-25:ls=3:edmg=fire,2:kb=3

		int stat_chance = r.nextInt(100);
		int stat_type = r.nextInt(4); // 0, 1, 2
		String stat_string = "";
		String short_stat_string = "";
		int stat_val = 1;
		if(tier == 1){
			stat_val = new Random().nextInt(15) + 1;
			if(stat_chance <= 25){
				stat_chance = r.nextInt(100);
				if(stat_type == 0){
					stat_string = "strength";
					short_stat_string = "STR";
				}
				if(stat_type == 1){
					stat_string = "dexterity";
					short_stat_string = "DEX";
				}
				if(stat_type == 2){
					stat_string = "vitality";
					short_stat_string = "VIT";
				}
				if(stat_type == 3){
					stat_string = "intellect";
					short_stat_string = "INT";
				}
				

				weapon_data += stat_string + "=" + stat_val + ":";
				wep_description += red.toString() + short_stat_string + ": +" + stat_val + ",";
			}
		}
		if(tier == 2){
			if(stat_chance <= 20){
				stat_val = new Random().nextInt(35) + 1;
				stat_chance = r.nextInt(100);
				if(stat_type == 0){
					stat_string = "strength";
					short_stat_string = "STR";
				}
				if(stat_type == 1){
					stat_string = "dexterity";
					short_stat_string = "DEX";
				}
				if(stat_type == 2){
					stat_string = "vitality";
					short_stat_string = "VIT";
				}
				if(stat_type == 3){
					stat_string = "intellect";
					short_stat_string = "INT";
				}
				

				weapon_data += stat_string + "=" + stat_val + ":";
				wep_description += red.toString() + short_stat_string + ": +" + stat_val + ",";
			}
			if(stat_chance <= 5){
				stat_val = new Random().nextInt(35) + 1;
				stat_chance = r.nextInt(100);
				if(stat_type >= 0 && stat_type < 3){
					stat_type++;
				}
				else if(stat_type == 3){
					stat_type--;
				}

				if(stat_type == 0){
					stat_string = "strength";
					short_stat_string = "STR";
				}
				if(stat_type == 1){
					stat_string = "dexterity";
					short_stat_string = "DEX";
				}
				if(stat_type == 2){
					stat_string = "vitality";
					short_stat_string = "VIT";
				}
				if(stat_type == 3){
					stat_string = "intellect";
					short_stat_string = "INT";
				}
				

				weapon_data += stat_string + "=" + stat_val + ":";
				wep_description += red.toString() + short_stat_string + ": +" + stat_val + ",";
			}
		}
		if(tier == 3){
			if(stat_chance <= 15){
				stat_val = new Random().nextInt(75) + 1;
				stat_chance = r.nextInt(100);
				if(stat_type == 0){
					stat_string = "strength";
					short_stat_string = "STR";
				}
				if(stat_type == 1){
					stat_string = "dexterity";
					short_stat_string = "DEX";
				}
				if(stat_type == 2){
					stat_string = "vitality";
					short_stat_string = "VIT";
				}
				if(stat_type == 3){
					stat_string = "intellect";
					short_stat_string = "INT";
				}
				

				weapon_data += stat_string + "=" + stat_val + ":";
				wep_description += red.toString() + short_stat_string + ": +" + stat_val + ",";
			}
			if(stat_chance <= 5){
				stat_val = new Random().nextInt(75) + 1;
				stat_chance = r.nextInt(100);
				if(stat_type >= 0 && stat_type < 3){
					stat_type++;
				}
				else if(stat_type == 3){
					stat_type = 0;
				}

				if(stat_type == 0){
					stat_string = "strength";
					short_stat_string = "STR";
				}
				if(stat_type == 1){
					stat_string = "dexterity";
					short_stat_string = "DEX";
				}
				if(stat_type == 2){
					stat_string = "vitality";
					short_stat_string = "VIT";
				}
				if(stat_type == 3){
					stat_string = "intellect";
					short_stat_string = "INT";
				}
				
				weapon_data += stat_string + "=" + stat_val + ":";
				wep_description += red.toString() + short_stat_string + ": +" + stat_val + ",";
			}
			if(stat_chance <= 1){
				stat_val = new Random().nextInt(75) + 1;
				stat_chance = r.nextInt(100);
				if(stat_type >= 0 && stat_type < 3){
					stat_type++;
				}
				else if(stat_type == 3){
					stat_type = 0;
				}

				if(stat_type == 0){
					stat_string = "strength";
					short_stat_string = "STR";
				}
				if(stat_type == 1){
					stat_string = "dexterity";
					short_stat_string = "DEX";
				}
				if(stat_type == 2){
					stat_string = "vitality";
					short_stat_string = "VIT";
				}
				if(stat_type == 3){
					stat_string = "intellect";
					short_stat_string = "INT";
				}
				

				weapon_data += stat_string + "=" + stat_val + ":";
				wep_description += red.toString() + short_stat_string + ": +" + stat_val + ",";
			}
		}
		if(tier == 4){
			if(stat_chance <= 15){
				stat_val = new Random().nextInt(115) + 1;
				stat_chance = r.nextInt(100);
				if(stat_type == 0){
					stat_string = "strength";
					short_stat_string = "STR";
				}
				if(stat_type == 1){
					stat_string = "dexterity";
					short_stat_string = "DEX";
				}
				if(stat_type == 2){
					stat_string = "vitality";
					short_stat_string = "VIT";
				}
				if(stat_type == 3){
					stat_string = "intellect";
					short_stat_string = "INT";
				}
				

				weapon_data += stat_string + "=" + stat_val + ":";
				wep_description += red.toString() + short_stat_string + ": +" + stat_val + ",";
			}
			if(stat_chance <= 9){
				stat_val = new Random().nextInt(115) + 1;
				stat_chance = r.nextInt(100);
				if(stat_type >= 0 && stat_type < 3){
					stat_type++;
				}
				else if(stat_type == 3){
					stat_type = 0;
				}

				if(stat_type == 0){
					stat_string = "strength";
					short_stat_string = "STR";
				}
				if(stat_type == 1){
					stat_string = "dexterity";
					short_stat_string = "DEX";
				}
				if(stat_type == 2){
					stat_string = "vitality";
					short_stat_string = "VIT";
				}
				if(stat_type == 3){
					stat_string = "intellect";
					short_stat_string = "INT";
				}
				

				weapon_data += stat_string + "=" + stat_val + ":";
				wep_description += red.toString() + short_stat_string + ": +" + stat_val + ",";
			}
			if(stat_chance <= 4){
				stat_val = new Random().nextInt(115) + 1;
				stat_chance = r.nextInt(100);
				if(stat_type >= 0 && stat_type < 3){
					stat_type++;
				}
				else if(stat_type == 3){
					stat_type = 0;
				}

				if(stat_type == 0){
					stat_string = "strength";
					short_stat_string = "STR";
				}
				if(stat_type == 1){
					stat_string = "dexterity";
					short_stat_string = "DEX";
				}
				if(stat_type == 2){
					stat_string = "vitality";
					short_stat_string = "VIT";
				}
				if(stat_type == 3){
					stat_string = "intellect";
					short_stat_string = "INT";
				}
				

				weapon_data += stat_string + "=" + stat_val + ":";
				wep_description += red.toString() + short_stat_string + ": +" + stat_val + ",";
			}
		}
		if(tier == 5){
			if(stat_chance <= 20){
				stat_val = new Random().nextInt(315) + 1;
				stat_chance = r.nextInt(100);
				if(stat_type == 0){
					stat_string = "strength";
					short_stat_string = "STR";
				}
				if(stat_type == 1){
					stat_string = "dexterity";
					short_stat_string = "DEX";
				}
				if(stat_type == 2){
					stat_string = "vitality";
					short_stat_string = "VIT";
				}
				if(stat_type == 3){
					stat_string = "intellect";
					short_stat_string = "INT";
				}
				

				weapon_data += stat_string + "=" + stat_val + ":";
				wep_description += red.toString() + short_stat_string + ": +" + stat_val + ",";
			}
			if(stat_chance <= 10){
				stat_val = new Random().nextInt(315) + 1;
				stat_chance = r.nextInt(100);
				if(stat_type >= 0 && stat_type < 3){
					stat_type++;
				}
				else if(stat_type == 3){
					stat_type = 0;
				}

				if(stat_type == 0){
					stat_string = "strength";
					short_stat_string = "STR";
				}
				if(stat_type == 1){
					stat_string = "dexterity";
					short_stat_string = "DEX";
				}
				if(stat_type == 2){
					stat_string = "vitality";
					short_stat_string = "VIT";
				}
				if(stat_type == 3){
					stat_string = "intellect";
					short_stat_string = "INT";
				}
				

				weapon_data += stat_string + "=" + stat_val + ":";
				wep_description += red.toString() + short_stat_string + ": +" + stat_val + ",";
			}
			if(stat_chance <= 5){
				stat_val = new Random().nextInt(315) + 1;
				if(stat_type >= 0 && stat_type < 3){
					stat_type++;
				}
				else if(stat_type == 3){
					stat_type = 0;
				}

				if(stat_type == 0){
					stat_string = "strength";
					short_stat_string = "STR";
				}
				if(stat_type == 1){
					stat_string = "dexterity";
					short_stat_string = "DEX";
				}
				if(stat_type == 2){
					stat_string = "vitality";
					short_stat_string = "VIT";
				}
				if(stat_type == 3){
					stat_string = "intellect";
					short_stat_string = "INT";
				}
				

				weapon_data += stat_string + "=" + stat_val + ":";
				wep_description += red.toString() + short_stat_string + ": +" + stat_val + ",";
			}
		}

		if(pure_dmg == true){
			weapon_data += "pure_dmg=" + pure_dmg_val + ":";
			wep_name = "Pure " + wep_name;
			wep_description += red.toString() + "PURE DMG: +" + pure_dmg_val + ",";
		}
		
		if(accuracy == true){
			weapon_data += "accuracy=" + knockback_percent + ":";
			wep_name = "Accurate " + wep_name;
			wep_description += red.toString() + "ACCURACY: " + accuracy_val + "%,";
			// Take from first index of kb= to next index of : from that point.
		}

		if(knockback == true){
			weapon_data += "kb=" + knockback_percent + ":";
			wep_name = "Brute " + wep_name;
			wep_description += red.toString() + "KNOCKBACK: " + knockback_percent + "%,";
			// Take from first index of kb= to next index of : from that point.
		}

		if(blind == true){
			weapon_data += "blind=" + blind_percent;
			wep_name = wep_name + " of Blindness";
			wep_description += red.toString() + "BLIND: " + blind_percent + "%,";
		}

		if(life_steal == true){
			weapon_data += "ls=" + life_steal_percent + ":";
			wep_name = "Vampyric " + wep_name; 
			wep_description += red.toString() + "LIFE STEAL: " + life_steal_percent + "%,";
			// life_steal == .split(":")[2]
		}


		if(crit_hit == true){
			weapon_data += "crit=" + crit_hit_percent + ":";
			wep_name = "Deadly " + wep_name;
			wep_description += red.toString() + "CRITICAL HIT: " + crit_hit_percent + "%,";
		}

		if(armor_pen == true){
			weapon_data += "armor_pen=" + armor_pen_val + ":";
			wep_name = "Penetrating " + wep_name;
			wep_description += red.toString() + "ARMOR PENETRATION: " + armor_pen_val + "%,";
		}

		if(vs_modifier_max > 0 && r.nextInt(100) <= vs_modifier_chance){
			vs_modifier_amount = r.nextInt(vs_modifier_max) + 1;
			int type = r.nextInt(2);
			if(type == 0){
				vs_modifier_type = "monsters";
			}
			else if(type == 1){
				vs_modifier_type = "players";
			}
		}
		
		if(vs_modifier_amount > 0){
			String vs_modifier_data = "vs_" + vs_modifier_type;
			weapon_data += vs_modifier_data + "=" + vs_modifier_amount + ":";
			if(vs_modifier_data.contains("monsters")){
				if(wep_name.contains("of")){
					wep_name = wep_name + " Slaying";
				}
				else{
					wep_name = wep_name + " of Slaying";
				}
			}
			if(vs_modifier_data.contains("players")){
				if(wep_name.contains("of")){
					wep_name = wep_name + " Slaughter";
				}
				else{
					wep_name = wep_name + " of Slaughter";
				}
			}
			wep_description += red.toString() + "vs. " + vs_modifier_type.toUpperCase() + ": +" + vs_modifier_amount + "% DMG,";
		}

		if(elemental_dmg == true){
			weapon_data += "edmg=" + element_dmg_type + "," + element_dmg_amount + ":";

			if(blind == true || wep_name.contains("of")){
				wep_name = wep_name + " " + element_dmg_type.substring(0, 1).toUpperCase() + element_dmg_type.substring(1, element_dmg_type.length());
			}
			if(blind == false){
				wep_name = wep_name + " of " + element_dmg_type.substring(0, 1).toUpperCase() + element_dmg_type.substring(1, element_dmg_type.length());
			}

			wep_description += red.toString() + element_dmg_type.toUpperCase() + " DMG: +" + element_dmg_amount + ",";
			// elemental_dmg == .split(":")[3].split(",")[1]
			// edmg=fire,3
		}

		wep_description += rarity;

		if(enchant_count > 0){
			wep_name = ChatColor.RED + "[+" + enchant_count + "]" + " " + tag.toString() + wep_name;
		}
		else{
			wep_name = tag.toString() + wep_name;
		}
		//log.info(wep_name + " = " + weapon_data);

		Attributes attributes = new Attributes(ItemMechanics.signCustomItem(m, (short)0, wep_name, wep_description));
		attributes.clear();
	
		return attributes.getStack();
		
	}

	public static ItemStack SwordGenorator(Material m, boolean reroll, ItemStack original){
		Random r = new Random();
		boolean life_steal = false, elemental_dmg = false, knockback = false, crit_hit = false, pure_dmg = false, armor_pen = false, blind = false/*, specific_dmg = false*/;
		int life_steal_percent = 0;
		int knockback_percent = 0;
		int crit_hit_percent = 0;
		int blind_percent = 0;

		int life_steal_chance = 0;
		int knockback_chance = 0;
		int crit_hit_chance = 0;
		int blind_chance = 0;
		int edmg_chance = 0;

		int life_steal_max = 0;
		int knockback_max = 0;
		int crit_hit_max = 0;
		int blind_max = 0;
		int edmg_max = 0;

		int vs_modifier_max = 0;
		int vs_modifier_chance = 0;
		int vs_modifier_amount = 0;
		String vs_modifier_type = "";
		
		//int pure_dmg_chance = 0;
		//int pure_dmg_max = 0;
		int pure_dmg_val = 0;

		int accuracy_chance = 0;
		int accuracy_max = 0;
		int accuracy_val = 0;
		boolean accuracy = false;
		
		//int armor_pen_chance = 0;
		//int armor_pen_max = 0;
		int armor_pen_val = 0;
		
		//int energy_cost = 0;
		int tier = 1;
		
		int element_dmg_amount = 0;
		String element_dmg_type = "";

		String wep_name = "";
		String wep_description = "";

		ChatColor tag = ChatColor.WHITE;

		if(m == Material.WOOD_SWORD){
			wep_name = "Shortsword";
			tag = ChatColor.WHITE;
			//energy_cost = 12;
			tier = 1;

			life_steal_chance = 2;
			knockback_chance = 3;
			crit_hit_chance = 2;
			blind_chance = 3;
			edmg_chance = 6;

			vs_modifier_chance = 6;
			vs_modifier_max = 10;
			
			accuracy_chance = 8;
			accuracy_max = 10;
			
			//pure_dmg_chance = 6;
			//armor_pen_chance = 20;
			//pure_dmg_max = 5;
			//armor_pen_max = 1;

			life_steal_max = 30;
			knockback_max = 3;
			crit_hit_max = 2;
			blind_max = 5;
			edmg_max = 4;
		}
		if(m == Material.STONE_SWORD){
			wep_name = "Broadsword";
			tag = ChatColor.GREEN;
			//energy_cost = 14;
			tier = 2;

			life_steal_chance = 4;
			knockback_chance = 10;
			crit_hit_chance = 5;
			blind_chance = 5;
			edmg_chance = 9;

			vs_modifier_chance = 9;
			vs_modifier_max = 12;
			
			accuracy_chance = 12;
			accuracy_max = 12;
			
			//pure_dmg_chance = 9;
			//armor_pen_chance = 20;
			//pure_dmg_max = 8;
			//armor_pen_max = 3;

			life_steal_max = 15;
			knockback_max = 6;
			crit_hit_max = 4;
			blind_max = 7;
			edmg_max = 9;
		}
		if(m == Material.IRON_SWORD){
			wep_name = "Magic Sword";
			tag = ChatColor.AQUA;
			//energy_cost = 16;
			tier = 3;

			life_steal_chance = 5;
			knockback_chance = 13;
			crit_hit_chance = 8;
			blind_chance = 8;
			edmg_chance = 10;
			
			vs_modifier_chance = 10;
			vs_modifier_max = 15;
			
			accuracy_chance = 15;
			accuracy_max = 25;

			//pure_dmg_chance = 5;
			//armor_pen_chance = 25;
			//pure_dmg_max = 15;
			//armor_pen_max = 5;

			life_steal_max = 12;
			knockback_max = 12;
			crit_hit_max = 5;
			blind_max = 9;
			edmg_max = 15;
		}
		if(m == Material.DIAMOND_SWORD){
			wep_name = "Ancient Sword";
			tag = ChatColor.LIGHT_PURPLE;
			//energy_cost = 20;
			tier = 4;

			life_steal_chance = 10;
			knockback_chance = 16;
			crit_hit_chance = 9;
			blind_chance = 9;
			edmg_chance = 15;
			
			vs_modifier_chance = 12;
			vs_modifier_max = 20;
			
			accuracy_chance = 20;
			accuracy_max = 28;

			//pure_dmg_chance = 5;
			//armor_pen_chance = 20;
			//pure_dmg_max = 25;
			//armor_pen_max = 8;

			life_steal_max = 7;
			knockback_max = 15;
			crit_hit_max = 6;
			blind_max = 9;
			edmg_max = 25;
		}

		if(m == Material.GOLD_SWORD){
			wep_name = "Legendary Sword";
			tag = ChatColor.YELLOW;
			//energy_cost = 25;
			tier = 5;

			life_steal_chance = 8;
			knockback_chance = 20;
			crit_hit_chance = 7;
			blind_chance = 11;
			edmg_chance = 20;

			vs_modifier_chance = 15;
			vs_modifier_max = 15;
			
			accuracy_chance = 15;
			accuracy_max = 35;
			
			//pure_dmg_chance = 10;
			//armor_pen_chance = 15;
			//pure_dmg_max = 45;
			//armor_pen_max = 10;

			life_steal_max = 8;
			knockback_max = 20;
			crit_hit_max = 10;
			blind_max = 11;
			edmg_max = 55;
		}

		int dmg_range_check = new Random().nextInt(100);
		int min_dmg = 0;
		int max_dmg = 0;
		String rarity = "";
		
		if(dmg_range_check <= 80){ // Tier 1 (low)
			rarity = ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + "Common";
			if(m == Material.WOOD_SWORD){
				int min_min_dmg = 1;
				int max_min_dmg = 2 + 1; // +1 to offset the random gayness.

				int max_max_dmg = 3 + 1; // ^

				min_dmg = r.nextInt(max_min_dmg - min_min_dmg) + min_min_dmg;
				max_dmg = r.nextInt(max_max_dmg - min_min_dmg) + min_dmg;
			}
			if(m == Material.STONE_SWORD){
				int min_min_dmg = 10;
				int max_min_dmg = 12 + 1; // +1 to offset the random gayness.

				int max_max_dmg = 15 + 1; // ^

				min_dmg = r.nextInt(max_min_dmg - min_min_dmg) + min_min_dmg;
				max_dmg = r.nextInt(max_max_dmg - min_min_dmg) + min_dmg;
			}
			if(m == Material.IRON_SWORD){
				int min_min_dmg = 25;
				int max_min_dmg = 30 + 1; // +1 to offset the random gayness.

				int max_max_dmg = 40 + 1; // ^

				min_dmg = r.nextInt(max_min_dmg - min_min_dmg) + min_min_dmg;
				max_dmg = r.nextInt(max_max_dmg - min_min_dmg) + min_dmg;
			}
			if(m == Material.DIAMOND_SWORD){
				int min_min_dmg = 65;
				int max_min_dmg = 80 + 1; // +1 to offset the random gayness.

				int max_max_dmg = 110 + 1; // ^

				min_dmg = r.nextInt(max_min_dmg - min_min_dmg) + min_min_dmg;
				max_dmg = r.nextInt(max_max_dmg - min_min_dmg) + min_dmg;
			}
			if(m == Material.GOLD_SWORD){
				int min_min_dmg = 130;
				int max_min_dmg = 140 + 1; // +1 to offset the random gayness.

				int max_max_dmg = 200 + 1; // ^

				min_dmg = r.nextInt(max_min_dmg - min_min_dmg) + min_min_dmg;
				max_dmg = r.nextInt(max_max_dmg - min_min_dmg) + min_dmg;
			}
		}

		if(dmg_range_check > 80 && dmg_range_check < 95){ // Tier 1 (med)
			rarity = ChatColor.GREEN.toString() + ChatColor.ITALIC.toString() + "Uncommon";
			if(m == Material.WOOD_SWORD){
				int min_min_dmg = 3;
				int max_min_dmg = 5 + 1; // +1 to offset the random gayness.

				int max_max_dmg = 6 + 1; // ^

				min_dmg = r.nextInt(max_min_dmg - min_min_dmg) + min_min_dmg;
				max_dmg = r.nextInt(max_max_dmg - min_min_dmg) + min_dmg;
			}
			if(m == Material.STONE_SWORD){
				int min_min_dmg = 16;
				int max_min_dmg = 18 + 1; // +1 to offset the random gayness.

				int max_max_dmg = 22 + 1; // ^

				min_dmg = r.nextInt(max_min_dmg - min_min_dmg) + min_min_dmg;
				max_dmg = r.nextInt(max_max_dmg - min_min_dmg) + min_dmg;
			}
			if(m == Material.IRON_SWORD){
				int min_min_dmg = 30;
				int max_min_dmg = 35 + 1; // +1 to offset the random gayness.

				int max_max_dmg = 65 + 1; // ^

				min_dmg = r.nextInt(max_min_dmg - min_min_dmg) + min_min_dmg;
				max_dmg = r.nextInt(max_max_dmg - min_min_dmg) + min_dmg;
			}
			if(m == Material.DIAMOND_SWORD){
				int min_min_dmg = 70;
				int max_min_dmg = 85 + 1; // +1 to offset the random gayness.

				int max_max_dmg = 140 + 1; // ^

				min_dmg = r.nextInt(max_min_dmg - min_min_dmg) + min_min_dmg;
				max_dmg = r.nextInt(max_max_dmg - min_min_dmg) + min_dmg;
			}
			if(m == Material.GOLD_SWORD){
				int min_min_dmg = 150;
				int max_min_dmg = 160 + 1; // +1 to offset the random gayness.

				int max_max_dmg = 250 + 1; // ^

				min_dmg = r.nextInt(max_min_dmg - min_min_dmg) + min_min_dmg;
				max_dmg = r.nextInt(max_max_dmg - min_min_dmg) + min_dmg;
			}
		}

		if(dmg_range_check >= 95 && dmg_range_check != 99){ // Tier 1 (high)
			rarity = ChatColor.AQUA.toString() + ChatColor.ITALIC.toString() + "Rare";
			if(m == Material.WOOD_SWORD){
				int min_min_dmg = 6;
				int max_min_dmg = 9 + 1; // +1 to offset the random gayness.

				int max_max_dmg = 20 + 1; // ^

				min_dmg = r.nextInt(max_min_dmg - min_min_dmg) + min_min_dmg;
				max_dmg = r.nextInt(max_max_dmg - min_min_dmg) + min_dmg;
			}
			if(m == Material.STONE_SWORD){
				int min_min_dmg = 20;
				int max_min_dmg = 30 + 1; // +1 to offset the random gayness.

				int max_max_dmg = 55 + 1; // ^

				min_dmg = r.nextInt(max_min_dmg - min_min_dmg) + min_min_dmg;
				max_dmg = r.nextInt(max_max_dmg - min_min_dmg) + min_dmg;
			}
			if(m == Material.IRON_SWORD){
				int min_min_dmg = 30;
				int max_min_dmg = 60 + 1; // +1 to offset the random gayness.

				int max_max_dmg = 120 + 1; // ^

				min_dmg = r.nextInt(max_min_dmg - min_min_dmg) + min_min_dmg;
				max_dmg = r.nextInt(max_max_dmg - min_min_dmg) + min_dmg;
			}
			if(m == Material.DIAMOND_SWORD){
				int min_min_dmg = 90;
				int max_min_dmg = 110 + 1; // +1 to offset the random gayness.

				int max_max_dmg = 210 + 1; // ^

				min_dmg = r.nextInt(max_min_dmg - min_min_dmg) + min_min_dmg;
				max_dmg = r.nextInt(max_max_dmg - min_min_dmg) + min_dmg;
			}
			if(m == Material.GOLD_SWORD){
				int min_min_dmg = 160;
				int max_min_dmg = 230 + 1; // +1 to offset the random gayness.

				int max_max_dmg = 337 + 1; // ^

				min_dmg = r.nextInt(max_min_dmg - min_min_dmg) + min_min_dmg;
				max_dmg = r.nextInt(max_max_dmg - min_min_dmg) + min_dmg;
			}
		}
		
		if(dmg_range_check == 99){ // Tier 1 (high)
			rarity = ChatColor.YELLOW.toString() + ChatColor.ITALIC.toString() + "Unique";
			if(m == Material.WOOD_SWORD){
				int min_min_dmg = 6;
				int max_min_dmg = 9 + 1; // +1 to offset the random gayness.

				int max_max_dmg = 20 + 1; // ^

				min_dmg = r.nextInt(max_min_dmg - min_min_dmg) + min_min_dmg;
				max_dmg = r.nextInt(max_max_dmg - min_min_dmg) + min_dmg;
			}
			if(m == Material.STONE_SWORD){
				int min_min_dmg = 20;
				int max_min_dmg = 30 + 1; // +1 to offset the random gayness.

				int max_max_dmg = 55 + 1; // ^

				min_dmg = r.nextInt(max_min_dmg - min_min_dmg) + min_min_dmg;
				max_dmg = r.nextInt(max_max_dmg - min_min_dmg) + min_dmg;
			}
			if(m == Material.IRON_SWORD){
				int min_min_dmg = 30;
				int max_min_dmg = 60 + 1; // +1 to offset the random gayness.

				int max_max_dmg = 120 + 1; // ^

				min_dmg = r.nextInt(max_min_dmg - min_min_dmg) + min_min_dmg;
				max_dmg = r.nextInt(max_max_dmg - min_min_dmg) + min_dmg;
			}
			if(m == Material.DIAMOND_SWORD){
				int min_min_dmg = 90;
				int max_min_dmg = 110 + 1; // +1 to offset the random gayness.

				int max_max_dmg = 210 + 1; // ^

				min_dmg = r.nextInt(max_min_dmg - min_min_dmg) + min_min_dmg;
				max_dmg = r.nextInt(max_max_dmg - min_min_dmg) + min_dmg;
			}
			if(m == Material.GOLD_SWORD){
				int min_min_dmg = 160;
				int max_min_dmg = 230 + 1; // +1 to offset the random gayness.

				int max_max_dmg = 337 + 1; // ^

				min_dmg = r.nextInt(max_min_dmg - min_min_dmg) + min_min_dmg;
				max_dmg = r.nextInt(max_max_dmg - min_min_dmg) + min_dmg;
			}
			
			min_dmg = (int)(min_dmg * 1.1);
			max_dmg = (int)(max_dmg * 1.1);
		}

		if(min_dmg <= 0){
			min_dmg = 1;
		}
		if(max_dmg <= 0){
			max_dmg = 1;
		}

		if(r.nextInt(100) <= life_steal_chance){ 
			life_steal = true;
			life_steal_percent = r.nextInt(life_steal_max) + 1; 
		}

		if(r.nextInt(100) <= edmg_chance){
			elemental_dmg = true;
			element_dmg_amount = r.nextInt(edmg_max) + 1;
			int elem_type_r = r.nextInt(3);
			if(elem_type_r == 0){
				element_dmg_type = "fire";
				element_dmg_amount -= (int)((double)element_dmg_amount * 0.15D);
			}
			if(elem_type_r == 1){
				element_dmg_type = "ice";
			}
			if(elem_type_r == 2){
				element_dmg_type = "poison";
				element_dmg_amount -= (int)((double)element_dmg_amount * 0.10D);
			}
		}

		if(r.nextInt(100) <= accuracy_chance){
			accuracy = true;
			accuracy_val = r.nextInt(accuracy_max) + 1;
		}

		if(r.nextInt(100) <= knockback_chance){
			knockback = true;
			knockback_percent = r.nextInt(knockback_max) + 1; 
		}

		if(r.nextInt(100) <= crit_hit_chance){ 
			crit_hit = true;
			crit_hit_percent = r.nextInt(crit_hit_max) + 1; // 1 - 10%
		}

		if(r.nextInt(100) <= blind_chance){ // 1% chance.
			blind = true;
			blind_percent = r.nextInt(blind_max) + 1; // 1 - 3%
		}
		
		int enchant_count = 0;
		// x = durability
		if(reroll == true && original != null){ // Keep the old DMG values instead.
			rarity = ItemMechanics.getItemRarity(original);
			enchant_count = EnchantMechanics.getEnchantCount(original);
			List<Integer> dmg_values = ItemMechanics.getDmgRangeOfWeapon(original);
			min_dmg = dmg_values.get(0);
			max_dmg = dmg_values.get(1);
		}

		@SuppressWarnings("unused")
		String weapon_data = m.name().toUpperCase() + "#" + min_dmg + "-" + max_dmg + ":";
		wep_description += red.toString() + "DMG: " + min_dmg + " - " + max_dmg + ",";
		// wood_sword,1|23-25:ls=3:edmg=fire,2:kb=3

		int stat_chance = r.nextInt(100);
		int stat_type = r.nextInt(4); // 0, 1, 2
		String stat_string = "";
		String short_stat_string = "";
		int stat_val = 1;
		if(tier == 1){
			stat_val = new Random().nextInt(15) + 1;
			if(stat_chance <= 25){
				stat_chance = r.nextInt(100);
				if(stat_type == 0){
					stat_string = "strength";
					short_stat_string = "STR";
				}
				if(stat_type == 1){
					stat_string = "dexterity";
					short_stat_string = "DEX";
				}
				if(stat_type == 2){
					stat_string = "vitality";
					short_stat_string = "VIT";
				}
				if(stat_type == 3){
					stat_string = "intellect";
					short_stat_string = "INT";
				}
				

				weapon_data += stat_string + "=" + stat_val + ":";
				wep_description += red.toString() + short_stat_string + ": +" + stat_val + ",";
			}
		}
		if(tier == 2){
			if(stat_chance <= 20){
				stat_val = new Random().nextInt(35) + 1;
				stat_chance = r.nextInt(100);
				if(stat_type == 0){
					stat_string = "strength";
					short_stat_string = "STR";
				}
				if(stat_type == 1){
					stat_string = "dexterity";
					short_stat_string = "DEX";
				}
				if(stat_type == 2){
					stat_string = "vitality";
					short_stat_string = "VIT";
				}
				if(stat_type == 3){
					stat_string = "intellect";
					short_stat_string = "INT";
				}
				

				weapon_data += stat_string + "=" + stat_val + ":";
				wep_description += red.toString() + short_stat_string + ": +" + stat_val + ",";
			}
			if(stat_chance <= 5){
				stat_val = new Random().nextInt(35) + 1;
				stat_chance = r.nextInt(100);
				if(stat_type >= 0 && stat_type < 3){
					stat_type++;
				}
				else if(stat_type == 3){
					stat_type--;
				}

				if(stat_type == 0){
					stat_string = "strength";
					short_stat_string = "STR";
				}
				if(stat_type == 1){
					stat_string = "dexterity";
					short_stat_string = "DEX";
				}
				if(stat_type == 2){
					stat_string = "vitality";
					short_stat_string = "VIT";
				}
				if(stat_type == 3){
					stat_string = "intellect";
					short_stat_string = "INT";
				}
				

				weapon_data += stat_string + "=" + stat_val + ":";
				wep_description += red.toString() + short_stat_string + ": +" + stat_val + ",";
			}
		}
		if(tier == 3){
			if(stat_chance <= 15){
				stat_val = new Random().nextInt(75) + 1;
				stat_chance = r.nextInt(100);
				if(stat_type == 0){
					stat_string = "strength";
					short_stat_string = "STR";
				}
				if(stat_type == 1){
					stat_string = "dexterity";
					short_stat_string = "DEX";
				}
				if(stat_type == 2){
					stat_string = "vitality";
					short_stat_string = "VIT";
				}
				if(stat_type == 3){
					stat_string = "intellect";
					short_stat_string = "INT";
				}
				

				weapon_data += stat_string + "=" + stat_val + ":";
				wep_description += red.toString() + short_stat_string + ": +" + stat_val + ",";
			}
			if(stat_chance <= 5){
				stat_val = new Random().nextInt(75) + 1;
				stat_chance = r.nextInt(100);
				if(stat_type >= 0 && stat_type < 3){
					stat_type++;
				}
				else if(stat_type == 3){
					stat_type = 0;
				}

				if(stat_type == 0){
					stat_string = "strength";
					short_stat_string = "STR";
				}
				if(stat_type == 1){
					stat_string = "dexterity";
					short_stat_string = "DEX";
				}
				if(stat_type == 2){
					stat_string = "vitality";
					short_stat_string = "VIT";
				}
				if(stat_type == 3){
					stat_string = "intellect";
					short_stat_string = "INT";
				}
				

				weapon_data += stat_string + "=" + stat_val + ":";
				wep_description += red.toString() + short_stat_string + ": +" + stat_val + ",";
			}
			if(stat_chance <= 1){
				stat_val = new Random().nextInt(75) + 1;
				stat_chance = r.nextInt(100);
				if(stat_type >= 0 && stat_type < 3){
					stat_type++;
				}
				else if(stat_type == 3){
					stat_type = 0;
				}

				if(stat_type == 0){
					stat_string = "strength";
					short_stat_string = "STR";
				}
				if(stat_type == 1){
					stat_string = "dexterity";
					short_stat_string = "DEX";
				}
				if(stat_type == 2){
					stat_string = "vitality";
					short_stat_string = "VIT";
				}
				if(stat_type == 3){
					stat_string = "intellect";
					short_stat_string = "INT";
				}
				

				weapon_data += stat_string + "=" + stat_val + ":";
				wep_description += red.toString() + short_stat_string + ": +" + stat_val + ",";
			}
		}
		if(tier == 4){
			if(stat_chance <= 15){
				stat_val = new Random().nextInt(115) + 1;
				stat_chance = r.nextInt(100);
				if(stat_type == 0){
					stat_string = "strength";
					short_stat_string = "STR";
				}
				if(stat_type == 1){
					stat_string = "dexterity";
					short_stat_string = "DEX";
				}
				if(stat_type == 2){
					stat_string = "vitality";
					short_stat_string = "VIT";
				}
				if(stat_type == 3){
					stat_string = "intellect";
					short_stat_string = "INT";
				}
				

				weapon_data += stat_string + "=" + stat_val + ":";
				wep_description += red.toString() + short_stat_string + ": +" + stat_val + ",";
			}
			if(stat_chance <= 9){
				stat_val = new Random().nextInt(115) + 1;
				stat_chance = r.nextInt(100);
				if(stat_type >= 0 && stat_type < 3){
					stat_type++;
				}
				else if(stat_type == 3){
					stat_type = 0;
				}

				if(stat_type == 0){
					stat_string = "strength";
					short_stat_string = "STR";
				}
				if(stat_type == 1){
					stat_string = "dexterity";
					short_stat_string = "DEX";
				}
				if(stat_type == 2){
					stat_string = "vitality";
					short_stat_string = "VIT";
				}
				if(stat_type == 3){
					stat_string = "intellect";
					short_stat_string = "INT";
				}
				

				weapon_data += stat_string + "=" + stat_val + ":";
				wep_description += red.toString() + short_stat_string + ": +" + stat_val + ",";
			}
			if(stat_chance <= 4){
				stat_val = new Random().nextInt(115) + 1;
				stat_chance = r.nextInt(100);
				if(stat_type >= 0 && stat_type < 3){
					stat_type++;
				}
				else if(stat_type == 3){
					stat_type = 0;
				}

				if(stat_type == 0){
					stat_string = "strength";
					short_stat_string = "STR";
				}
				if(stat_type == 1){
					stat_string = "dexterity";
					short_stat_string = "DEX";
				}
				if(stat_type == 2){
					stat_string = "vitality";
					short_stat_string = "VIT";
				}
				if(stat_type == 3){
					stat_string = "intellect";
					short_stat_string = "INT";
				}
				

				weapon_data += stat_string + "=" + stat_val + ":";
				wep_description += red.toString() + short_stat_string + ": +" + stat_val + ",";
			}
		}
		if(tier == 5){
			if(stat_chance <= 20){
				stat_val = new Random().nextInt(315) + 1;
				stat_chance = r.nextInt(100);
				if(stat_type == 0){
					stat_string = "strength";
					short_stat_string = "STR";
				}
				if(stat_type == 1){
					stat_string = "dexterity";
					short_stat_string = "DEX";
				}
				if(stat_type == 2){
					stat_string = "vitality";
					short_stat_string = "VIT";
				}
				if(stat_type == 3){
					stat_string = "intellect";
					short_stat_string = "INT";
				}
				

				weapon_data += stat_string + "=" + stat_val + ":";
				wep_description += red.toString() + short_stat_string + ": +" + stat_val + ",";
			}
			if(stat_chance <= 10){
				stat_val = new Random().nextInt(315) + 1;
				stat_chance = r.nextInt(100);
				if(stat_type >= 0 && stat_type < 3){
					stat_type++;
				}
				else if(stat_type == 3){
					stat_type = 0;
				}

				if(stat_type == 0){
					stat_string = "strength";
					short_stat_string = "STR";
				}
				if(stat_type == 1){
					stat_string = "dexterity";
					short_stat_string = "DEX";
				}
				if(stat_type == 2){
					stat_string = "vitality";
					short_stat_string = "VIT";
				}
				if(stat_type == 3){
					stat_string = "intellect";
					short_stat_string = "INT";
				}
				

				weapon_data += stat_string + "=" + stat_val + ":";
				wep_description += red.toString() + short_stat_string + ": +" + stat_val + ",";
			}
			if(stat_chance <= 5){
				stat_val = new Random().nextInt(315) + 1;
				if(stat_type >= 0 && stat_type < 3){
					stat_type++;
				}
				else if(stat_type == 3){
					stat_type = 0;
				}

				if(stat_type == 0){
					stat_string = "strength";
					short_stat_string = "STR";
				}
				if(stat_type == 1){
					stat_string = "dexterity";
					short_stat_string = "DEX";
				}
				if(stat_type == 2){
					stat_string = "vitality";
					short_stat_string = "VIT";
				}
				if(stat_type == 3){
					stat_string = "intellect";
					short_stat_string = "INT";
				}
				

				weapon_data += stat_string + "=" + stat_val + ":";
				wep_description += red.toString() + short_stat_string + ": +" + stat_val + ",";
			}
		}

		if(pure_dmg == true){
			weapon_data += "pure_dmg=" + pure_dmg_val + ":";
			wep_name = "Pure " + wep_name;
			wep_description += red.toString() + "PURE DMG: +" + pure_dmg_val + ",";
		}
		
		if(accuracy == true){
			weapon_data += "accuracy=" + knockback_percent + ":";
			wep_name = "Accurate " + wep_name;
			wep_description += red.toString() + "ACCURACY: " + accuracy_val + "%,";
			// Take from first index of kb= to next index of : from that point.
		}

		if(knockback == true){
			weapon_data += "kb=" + knockback_percent + ":";
			wep_name = "Brute " + wep_name;
			wep_description += red.toString() + "KNOCKBACK: " + knockback_percent + "%,";
			// Take from first index of kb= to next index of : from that point.
		}

		if(blind == true){
			weapon_data += "blind=" + blind_percent;
			wep_name = wep_name + " of Blindness";
			wep_description += red.toString() + "BLIND: " + blind_percent + "%,";
		}

		if(life_steal == true){
			weapon_data += "ls=" + life_steal_percent + ":";
			wep_name = "Vampyric " + wep_name; 
			wep_description += red.toString() + "LIFE STEAL: " + life_steal_percent + "%,";
			// life_steal == .split(":")[2]
		}

		if(crit_hit == true){
			weapon_data += "crit=" + crit_hit_percent + ":";
			wep_name = "Deadly " + wep_name;
			wep_description += red.toString() + "CRITICAL HIT: " + crit_hit_percent + "%,";
		}

		if(armor_pen == true){
			weapon_data += "armor_pen=" + armor_pen_val + ":";
			wep_name = "Penetrating " + wep_name;
			wep_description += red.toString() + "ARMOR PENETRATION: " + armor_pen_val + "%,";
		}
		
		if(r.nextInt(100) <= vs_modifier_chance){
			vs_modifier_amount = r.nextInt(vs_modifier_max) + 1;
			int type = r.nextInt(2);
			if(type == 0){
				vs_modifier_type = "monsters";
			}
			else if(type == 1){
				vs_modifier_type = "players";
			}
		}
		
		if(vs_modifier_amount > 0){
			String vs_modifier_data = "vs_" + vs_modifier_type;
			weapon_data += vs_modifier_data + "=" + vs_modifier_amount + ":";
			if(vs_modifier_data.contains("monsters")){
				if(wep_name.contains("of")){
					wep_name = wep_name + " Slaying";
				}
				else{
					wep_name = wep_name + " of Slaying";
				}
			}
			if(vs_modifier_data.contains("players")){
				if(wep_name.contains("of")){
					wep_name = wep_name + " Slaughter";
				}
				else{
					wep_name = wep_name + " of Slaughter";
				}
			}
			wep_description += red.toString() + "vs. " + vs_modifier_type.toUpperCase() + ": +" + vs_modifier_amount + "% DMG,";
		}

		if(elemental_dmg == true){
			weapon_data += "edmg=" + element_dmg_type + "," + element_dmg_amount + ":";

			if(blind == true || wep_name.contains("of")){
				wep_name = wep_name + " " + element_dmg_type.substring(0, 1).toUpperCase() + element_dmg_type.substring(1, element_dmg_type.length());
			}
			if(blind == false){
				wep_name = wep_name + " of " + element_dmg_type.substring(0, 1).toUpperCase() + element_dmg_type.substring(1, element_dmg_type.length());
			}

			wep_description += red.toString() + element_dmg_type.toUpperCase() + " DMG: +" + element_dmg_amount + ",";
			// elemental_dmg == .split(":")[3].split(",")[1]
			// edmg=fire,3
		}

		wep_description += rarity;
		
		if(enchant_count > 0){
			wep_name = ChatColor.RED + "[+" + enchant_count + "]" + " " + tag.toString() + wep_name;
		}
		else{
			wep_name = tag.toString() + wep_name;
		}
		//log.info(wep_name + " = " + weapon_data);

		Attributes attributes = new Attributes(ItemMechanics.signCustomItem(m, (short)0, wep_name, wep_description));
		attributes.clear();
	
		return attributes.getStack();
	}
	
	public static ItemStack PolearmGenorator(Material m, boolean reroll, ItemStack original){
		Random r = new Random();
		boolean life_steal = false, elemental_dmg = false, knockback = false, crit_hit = false, pure_dmg = false, armor_pen = false, blind = false/*, specific_dmg = false*/;
		int life_steal_percent = 0;
		int knockback_percent = 0;
		int crit_hit_percent = 0;
		int blind_percent = 0;

		int life_steal_chance = 0;
		int knockback_chance = 0;
		int crit_hit_chance = 0;
		int blind_chance = 0;
		int edmg_chance = 0;

		int life_steal_max = 0;
		int knockback_max = 0;
		int crit_hit_max = 0;
		int blind_max = 0;
		int edmg_max = 0;

		int vs_modifier_max = 0;
		int vs_modifier_chance = 0;
		int vs_modifier_amount = 0;
		String vs_modifier_type = "";
		
		//int pure_dmg_chance = 0;
		//int pure_dmg_max = 0;
		int pure_dmg_val = 0;

		//int accuracy_chance = 0;
		//int accuracy_max = 0;
		int accuracy_val = 0;
		boolean accuracy = false;
		
		//int armor_pen_chance = 0;
		//int armor_pen_max = 0;
		int armor_pen_val = 0;

		//int energy_cost = 0;
		int tier = 1;

		int element_dmg_amount = 0;
		String element_dmg_type = "";

		String wep_name = "";
		String wep_description = "";

		ChatColor tag = ChatColor.WHITE;

		if(m == Material.WOOD_SPADE){
			wep_name = "Spear";
			tag = ChatColor.WHITE;
			//energy_cost = 12;
			tier = 1;

			life_steal_chance = 2;
			knockback_chance = 3;
			crit_hit_chance = 2;
			blind_chance = 3;
			edmg_chance = 6;

			vs_modifier_chance = 6;
			vs_modifier_max = 10;
			
			//accuracy_chance = 8;
			//accuracy_max = 10;
			
			//pure_dmg_chance = 6;
			//armor_pen_chance = 20;
			//pure_dmg_max = 5;
			//armor_pen_max = 1;

			life_steal_max = 30;
			knockback_max = 3;
			crit_hit_max = 2;
			blind_max = 5;
			edmg_max = 4;
		}
		if(m == Material.STONE_SPADE){
			wep_name = "Halberd";
			tag = ChatColor.GREEN;
			//energy_cost = 14;
			tier = 2;

			life_steal_chance = 4;
			knockback_chance = 10;
			crit_hit_chance = 5;
			blind_chance = 5;
			edmg_chance = 9;
			
			vs_modifier_chance = 9;
			vs_modifier_max = 12;
			
			//accuracy_chance = 12;
			//accuracy_max = 12;

			//pure_dmg_chance = 9;
			//armor_pen_chance = 20;
			//pure_dmg_max = 8;
			//armor_pen_max = 3;

			life_steal_max = 15;
			knockback_max = 6;
			crit_hit_max = 4;
			blind_max = 7;
			edmg_max = 9;
		}
		if(m == Material.IRON_SPADE){
			wep_name = "Magic Polearm";
			tag = ChatColor.AQUA;
			//energy_cost = 16;
			tier = 3;

			life_steal_chance = 5;
			knockback_chance = 13;
			crit_hit_chance = 8;
			blind_chance = 8;
			edmg_chance = 10;

			vs_modifier_chance = 10;
			vs_modifier_max = 15;
			
			//accuracy_chance = 15;
			//accuracy_max = 25;
			
			//pure_dmg_chance = 5;
			//armor_pen_chance = 25;
			//pure_dmg_max = 15;
			//armor_pen_max = 5;

			life_steal_max = 12;
			knockback_max = 12;
			crit_hit_max = 5;
			blind_max = 9;
			edmg_max = 15;
		}
		if(m == Material.DIAMOND_SPADE){
			wep_name = "Ancient Polearm";
			tag = ChatColor.LIGHT_PURPLE;
			//energy_cost = 20;
			tier = 4;

			life_steal_chance = 10;
			knockback_chance = 16;
			crit_hit_chance = 9;
			blind_chance = 9;
			edmg_chance = 15;

			vs_modifier_chance = 12;
			vs_modifier_max = 20;
			
			//accuracy_chance = 20;
			//accuracy_max = 28;
			
			//pure_dmg_chance = 5;
			//armor_pen_chance = 20;
			//pure_dmg_max = 25;
			//armor_pen_max = 8;

			life_steal_max = 7;
			knockback_max = 15;
			crit_hit_max = 6;
			blind_max = 9;
			edmg_max = 25;
		}

		if(m == Material.GOLD_SPADE){
			wep_name = "Legendary Polearm";
			tag = ChatColor.YELLOW;
			//energy_cost = 25;
			tier = 5;

			life_steal_chance = 8;
			knockback_chance = 20;
			crit_hit_chance = 7;
			blind_chance = 11;
			edmg_chance = 20;

			vs_modifier_chance = 15;
			vs_modifier_max = 15;
			
			//accuracy_chance = 15;
			//accuracy_max = 35;
			
			//pure_dmg_chance = 10;
			//armor_pen_chance = 15;
			//pure_dmg_max = 45;
			//armor_pen_max = 10;

			life_steal_max = 8;
			knockback_max = 20;
			crit_hit_max = 10;
			blind_max = 11;
			edmg_max = 55;
		}

		int dmg_range_check = new Random().nextInt(100);
		int min_dmg = 0;
		int max_dmg = 0;
		String rarity = "";
		
		if(dmg_range_check <= 80){ // Tier 1 (low)
			rarity = ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + "Common";
			if(m == Material.WOOD_SPADE){
				int min_min_dmg = 1;
				int max_min_dmg = 2 + 1; // +1 to offset the random gayness.

				int max_max_dmg = 3 + 1; // ^

				min_dmg = r.nextInt(max_min_dmg - min_min_dmg) + min_min_dmg;
				max_dmg = r.nextInt(max_max_dmg - min_min_dmg) + min_dmg;
			}
			if(m == Material.STONE_SPADE){
				int min_min_dmg = 10;
				int max_min_dmg = 12 + 1; // +1 to offset the random gayness.

				int max_max_dmg = 15 + 1; // ^

				min_dmg = r.nextInt(max_min_dmg - min_min_dmg) + min_min_dmg;
				max_dmg = r.nextInt(max_max_dmg - min_min_dmg) + min_dmg;
			}
			if(m == Material.IRON_SPADE){
				int min_min_dmg = 25;
				int max_min_dmg = 30 + 1; // +1 to offset the random gayness.

				int max_max_dmg = 40 + 1; // ^

				min_dmg = r.nextInt(max_min_dmg - min_min_dmg) + min_min_dmg;
				max_dmg = r.nextInt(max_max_dmg - min_min_dmg) + min_dmg;
			}
			if(m == Material.DIAMOND_SPADE){
				int min_min_dmg = 65;
				int max_min_dmg = 80 + 1; // +1 to offset the random gayness.

				int max_max_dmg = 110 + 1; // ^

				min_dmg = r.nextInt(max_min_dmg - min_min_dmg) + min_min_dmg;
				max_dmg = r.nextInt(max_max_dmg - min_min_dmg) + min_dmg;
			}
			if(m == Material.GOLD_SPADE){
				int min_min_dmg = 130;
				int max_min_dmg = 140 + 1; // +1 to offset the random gayness.

				int max_max_dmg = 200 + 1; // ^

				min_dmg = r.nextInt(max_min_dmg - min_min_dmg) + min_min_dmg;
				max_dmg = r.nextInt(max_max_dmg - min_min_dmg) + min_dmg;
			}
		}

		if(dmg_range_check > 80 && dmg_range_check < 95){ // Tier 1 (med)
			rarity = ChatColor.GREEN.toString() + ChatColor.ITALIC.toString() + "Uncommon";
			if(m == Material.WOOD_SPADE){
				int min_min_dmg = 3;
				int max_min_dmg = 5 + 1; // +1 to offset the random gayness.

				int max_max_dmg = 6 + 1; // ^

				min_dmg = r.nextInt(max_min_dmg - min_min_dmg) + min_min_dmg;
				max_dmg = r.nextInt(max_max_dmg - min_min_dmg) + min_dmg;
			}
			if(m == Material.STONE_SPADE){
				int min_min_dmg = 16;
				int max_min_dmg = 18 + 1; // +1 to offset the random gayness.

				int max_max_dmg = 22 + 1; // ^

				min_dmg = r.nextInt(max_min_dmg - min_min_dmg) + min_min_dmg;
				max_dmg = r.nextInt(max_max_dmg - min_min_dmg) + min_dmg;
			}
			if(m == Material.IRON_SPADE){
				int min_min_dmg = 30;
				int max_min_dmg = 35 + 1; // +1 to offset the random gayness.

				int max_max_dmg = 65 + 1; // ^

				min_dmg = r.nextInt(max_min_dmg - min_min_dmg) + min_min_dmg;
				max_dmg = r.nextInt(max_max_dmg - min_min_dmg) + min_dmg;
			}
			if(m == Material.DIAMOND_SPADE){
				int min_min_dmg = 70;
				int max_min_dmg = 85 + 1; // +1 to offset the random gayness.

				int max_max_dmg = 140 + 1; // ^

				min_dmg = r.nextInt(max_min_dmg - min_min_dmg) + min_min_dmg;
				max_dmg = r.nextInt(max_max_dmg - min_min_dmg) + min_dmg;
			}
			if(m == Material.GOLD_SPADE){
				int min_min_dmg = 150;
				int max_min_dmg = 160 + 1; // +1 to offset the random gayness.

				int max_max_dmg = 250 + 1; // ^

				min_dmg = r.nextInt(max_min_dmg - min_min_dmg) + min_min_dmg;
				max_dmg = r.nextInt(max_max_dmg - min_min_dmg) + min_dmg;
			}
		}

		if(dmg_range_check >= 95 && dmg_range_check != 99){ // Tier 1 (high)
			rarity = ChatColor.AQUA.toString() + ChatColor.ITALIC.toString() + "Rare";
			if(m == Material.WOOD_SPADE){
				int min_min_dmg = 6;
				int max_min_dmg = 9 + 1; // +1 to offset the random gayness.

				int max_max_dmg = 20 + 1; // ^

				min_dmg = r.nextInt(max_min_dmg - min_min_dmg) + min_min_dmg;
				max_dmg = r.nextInt(max_max_dmg - min_min_dmg) + min_dmg;
			}
			if(m == Material.STONE_SPADE){
				int min_min_dmg = 20;
				int max_min_dmg = 30 + 1; // +1 to offset the random gayness.

				int max_max_dmg = 55 + 1; // ^

				min_dmg = r.nextInt(max_min_dmg - min_min_dmg) + min_min_dmg;
				max_dmg = r.nextInt(max_max_dmg - min_min_dmg) + min_dmg;
			}
			if(m == Material.IRON_SPADE){
				int min_min_dmg = 30;
				int max_min_dmg = 60 + 1; // +1 to offset the random gayness.

				int max_max_dmg = 120 + 1; // ^

				min_dmg = r.nextInt(max_min_dmg - min_min_dmg) + min_min_dmg;
				max_dmg = r.nextInt(max_max_dmg - min_min_dmg) + min_dmg;
			}
			if(m == Material.DIAMOND_SPADE){
				int min_min_dmg = 90;
				int max_min_dmg = 110 + 1; // +1 to offset the random gayness.

				int max_max_dmg = 210 + 1; // ^

				min_dmg = r.nextInt(max_min_dmg - min_min_dmg) + min_min_dmg;
				max_dmg = r.nextInt(max_max_dmg - min_min_dmg) + min_dmg;
			}
			if(m == Material.GOLD_SPADE){
				int min_min_dmg = 160;
				int max_min_dmg = 230 + 1; // +1 to offset the random gayness.

				int max_max_dmg = 337 + 1; // ^

				min_dmg = r.nextInt(max_min_dmg - min_min_dmg) + min_min_dmg;
				max_dmg = r.nextInt(max_max_dmg - min_min_dmg) + min_dmg;
			}
		}
		
		if(dmg_range_check == 99){ // Tier 1 (high)
			rarity = ChatColor.YELLOW.toString() + ChatColor.ITALIC.toString() + "Unique";
			if(m == Material.WOOD_SPADE){
				int min_min_dmg = 6;
				int max_min_dmg = 9 + 1; // +1 to offset the random gayness.

				int max_max_dmg = 20 + 1; // ^

				min_dmg = r.nextInt(max_min_dmg - min_min_dmg) + min_min_dmg;
				max_dmg = r.nextInt(max_max_dmg - min_min_dmg) + min_dmg;
			}
			if(m == Material.STONE_SPADE){
				int min_min_dmg = 20;
				int max_min_dmg = 30 + 1; // +1 to offset the random gayness.

				int max_max_dmg = 55 + 1; // ^

				min_dmg = r.nextInt(max_min_dmg - min_min_dmg) + min_min_dmg;
				max_dmg = r.nextInt(max_max_dmg - min_min_dmg) + min_dmg;
			}
			if(m == Material.IRON_SPADE){
				int min_min_dmg = 30;
				int max_min_dmg = 60 + 1; // +1 to offset the random gayness.

				int max_max_dmg = 120 + 1; // ^

				min_dmg = r.nextInt(max_min_dmg - min_min_dmg) + min_min_dmg;
				max_dmg = r.nextInt(max_max_dmg - min_min_dmg) + min_dmg;
			}
			if(m == Material.DIAMOND_SPADE){
				int min_min_dmg = 90;
				int max_min_dmg = 110 + 1; // +1 to offset the random gayness.

				int max_max_dmg = 210 + 1; // ^

				min_dmg = r.nextInt(max_min_dmg - min_min_dmg) + min_min_dmg;
				max_dmg = r.nextInt(max_max_dmg - min_min_dmg) + min_dmg;
			}
			if(m == Material.GOLD_SPADE){
				int min_min_dmg = 160;
				int max_min_dmg = 230 + 1; // +1 to offset the random gayness.

				int max_max_dmg = 337 + 1; // ^

				min_dmg = r.nextInt(max_min_dmg - min_min_dmg) + min_min_dmg;
				max_dmg = r.nextInt(max_max_dmg - min_min_dmg) + min_dmg;
			}
			
			min_dmg = (int)(min_dmg * 1.1);
			max_dmg = (int)(max_dmg * 1.1);
		}

		min_dmg = (int)(min_dmg / 2.0D);
		max_dmg = (int)(max_dmg / 2.0D);
		
		if(min_dmg < 1){
			min_dmg = 1;
		}
		if(max_dmg < 1){
			max_dmg = 1;
		}

		if(r.nextInt(100) <= life_steal_chance){ 
			life_steal = true;
			life_steal_percent = r.nextInt(life_steal_max) + 1; 
		}

		if(r.nextInt(100) <= edmg_chance){
			elemental_dmg = true;
			element_dmg_amount = r.nextInt(edmg_max) + 1;
			int elem_type_r = r.nextInt(3);
			if(elem_type_r == 0){
				element_dmg_type = "fire";
				element_dmg_amount -= (int)((double)element_dmg_amount * 0.15D);
			}
			if(elem_type_r == 1){
				element_dmg_type = "ice";
			}
			if(elem_type_r == 2){
				element_dmg_type = "poison";
				element_dmg_amount -= (int)((double)element_dmg_amount * 0.10D);
			}
		}

		if(r.nextInt(100) <= knockback_chance){
			knockback = true;
			knockback_percent = r.nextInt(knockback_max) + 1; 
		}

		if(r.nextInt(100) <= crit_hit_chance){ 
			crit_hit = true;
			crit_hit_percent = r.nextInt(crit_hit_max) + 1; // 1 - 10%
		}

		if(r.nextInt(100) <= blind_chance){ // 1% chance.
			blind = true;
			blind_percent = r.nextInt(blind_max) + 1; // 1 - 3%
		}

		int enchant_count = 0;
		// x = durability
		if(reroll == true && original != null){ // Keep the old DMG values instead.
			rarity = ItemMechanics.getItemRarity(original);
			enchant_count = EnchantMechanics.getEnchantCount(original);
			List<Integer> dmg_values = ItemMechanics.getDmgRangeOfWeapon(original);
			min_dmg = dmg_values.get(0);
			max_dmg = dmg_values.get(1);
		}

		@SuppressWarnings("unused")
		String weapon_data = m.name().toUpperCase() + "#" + min_dmg + "-" + max_dmg + ":";
		wep_description += red.toString() + "DMG: " + min_dmg + " - " + max_dmg + ",";
		// wood_SPADE,1|23-25:ls=3:edmg=fire,2:kb=3

		int stat_chance = r.nextInt(100);
		int stat_type = r.nextInt(4); // 0, 1, 2
		String stat_string = "";
		String short_stat_string = "";
		int stat_val = 1;
		if(tier == 1){
			stat_val = new Random().nextInt(15) + 1;
			if(stat_chance <= 25){
				stat_chance = r.nextInt(100);
				if(stat_type == 0){
					stat_string = "strength";
					short_stat_string = "STR";
				}
				if(stat_type == 1){
					stat_string = "dexterity";
					short_stat_string = "DEX";
				}
				if(stat_type == 2){
					stat_string = "vitality";
					short_stat_string = "VIT";
				}
				if(stat_type == 3){
					stat_string = "intellect";
					short_stat_string = "INT";
				}
				

				weapon_data += stat_string + "=" + stat_val + ":";
				wep_description += red.toString() + short_stat_string + ": +" + stat_val + ",";
			}
		}
		if(tier == 2){
			if(stat_chance <= 20){
				stat_val = new Random().nextInt(35) + 1;
				stat_chance = r.nextInt(100);
				if(stat_type == 0){
					stat_string = "strength";
					short_stat_string = "STR";
				}
				if(stat_type == 1){
					stat_string = "dexterity";
					short_stat_string = "DEX";
				}
				if(stat_type == 2){
					stat_string = "vitality";
					short_stat_string = "VIT";
				}
				if(stat_type == 3){
					stat_string = "intellect";
					short_stat_string = "INT";
				}
				

				weapon_data += stat_string + "=" + stat_val + ":";
				wep_description += red.toString() + short_stat_string + ": +" + stat_val + ",";
			}
			if(stat_chance <= 5){
				stat_val = new Random().nextInt(35) + 1;
				stat_chance = r.nextInt(100);
				if(stat_type >= 0 && stat_type < 3){
					stat_type++;
				}
				else if(stat_type == 3){
					stat_type--;
				}

				if(stat_type == 0){
					stat_string = "strength";
					short_stat_string = "STR";
				}
				if(stat_type == 1){
					stat_string = "dexterity";
					short_stat_string = "DEX";
				}
				if(stat_type == 2){
					stat_string = "vitality";
					short_stat_string = "VIT";
				}
				if(stat_type == 3){
					stat_string = "intellect";
					short_stat_string = "INT";
				}
				

				weapon_data += stat_string + "=" + stat_val + ":";
				wep_description += red.toString() + short_stat_string + ": +" + stat_val + ",";
			}
		}
		if(tier == 3){
			if(stat_chance <= 15){
				stat_val = new Random().nextInt(75) + 1;
				stat_chance = r.nextInt(100);
				if(stat_type == 0){
					stat_string = "strength";
					short_stat_string = "STR";
				}
				if(stat_type == 1){
					stat_string = "dexterity";
					short_stat_string = "DEX";
				}
				if(stat_type == 2){
					stat_string = "vitality";
					short_stat_string = "VIT";
				}
				if(stat_type == 3){
					stat_string = "intellect";
					short_stat_string = "INT";
				}
				

				weapon_data += stat_string + "=" + stat_val + ":";
				wep_description += red.toString() + short_stat_string + ": +" + stat_val + ",";
			}
			if(stat_chance <= 5){
				stat_val = new Random().nextInt(75) + 1;
				stat_chance = r.nextInt(100);
				if(stat_type >= 0 && stat_type < 3){
					stat_type++;
				}
				else if(stat_type == 3){
					stat_type = 0;
				}

				if(stat_type == 0){
					stat_string = "strength";
					short_stat_string = "STR";
				}
				if(stat_type == 1){
					stat_string = "dexterity";
					short_stat_string = "DEX";
				}
				if(stat_type == 2){
					stat_string = "vitality";
					short_stat_string = "VIT";
				}
				if(stat_type == 3){
					stat_string = "intellect";
					short_stat_string = "INT";
				}
				

				weapon_data += stat_string + "=" + stat_val + ":";
				wep_description += red.toString() + short_stat_string + ": +" + stat_val + ",";
			}
			if(stat_chance <= 1){
				stat_val = new Random().nextInt(75) + 1;
				stat_chance = r.nextInt(100);
				if(stat_type >= 0 && stat_type < 3){
					stat_type++;
				}
				else if(stat_type == 3){
					stat_type = 0;
				}

				if(stat_type == 0){
					stat_string = "strength";
					short_stat_string = "STR";
				}
				if(stat_type == 1){
					stat_string = "dexterity";
					short_stat_string = "DEX";
				}
				if(stat_type == 2){
					stat_string = "vitality";
					short_stat_string = "VIT";
				}
				if(stat_type == 3){
					stat_string = "intellect";
					short_stat_string = "INT";
				}
				

				weapon_data += stat_string + "=" + stat_val + ":";
				wep_description += red.toString() + short_stat_string + ": +" + stat_val + ",";
			}
		}
		if(tier == 4){
			if(stat_chance <= 15){
				stat_val = new Random().nextInt(115) + 1;
				stat_chance = r.nextInt(100);
				if(stat_type == 0){
					stat_string = "strength";
					short_stat_string = "STR";
				}
				if(stat_type == 1){
					stat_string = "dexterity";
					short_stat_string = "DEX";
				}
				if(stat_type == 2){
					stat_string = "vitality";
					short_stat_string = "VIT";
				}
				if(stat_type == 3){
					stat_string = "intellect";
					short_stat_string = "INT";
				}
				

				weapon_data += stat_string + "=" + stat_val + ":";
				wep_description += red.toString() + short_stat_string + ": +" + stat_val + ",";
			}
			if(stat_chance <= 9){
				stat_val = new Random().nextInt(115) + 1;
				stat_chance = r.nextInt(100);
				if(stat_type >= 0 && stat_type < 3){
					stat_type++;
				}
				else if(stat_type == 3){
					stat_type = 0;
				}

				if(stat_type == 0){
					stat_string = "strength";
					short_stat_string = "STR";
				}
				if(stat_type == 1){
					stat_string = "dexterity";
					short_stat_string = "DEX";
				}
				if(stat_type == 2){
					stat_string = "vitality";
					short_stat_string = "VIT";
				}
				if(stat_type == 3){
					stat_string = "intellect";
					short_stat_string = "INT";
				}
				

				weapon_data += stat_string + "=" + stat_val + ":";
				wep_description += red.toString() + short_stat_string + ": +" + stat_val + ",";
			}
			if(stat_chance <= 4){
				stat_val = new Random().nextInt(115) + 1;
				stat_chance = r.nextInt(100);
				if(stat_type >= 0 && stat_type < 3){
					stat_type++;
				}
				else if(stat_type == 3){
					stat_type = 0;
				}

				if(stat_type == 0){
					stat_string = "strength";
					short_stat_string = "STR";
				}
				if(stat_type == 1){
					stat_string = "dexterity";
					short_stat_string = "DEX";
				}
				if(stat_type == 2){
					stat_string = "vitality";
					short_stat_string = "VIT";
				}
				if(stat_type == 3){
					stat_string = "intellect";
					short_stat_string = "INT";
				}
				

				weapon_data += stat_string + "=" + stat_val + ":";
				wep_description += red.toString() + short_stat_string + ": +" + stat_val + ",";
			}
		}
		if(tier == 5){
			if(stat_chance <= 20){
				stat_val = new Random().nextInt(315) + 1;
				stat_chance = r.nextInt(100);
				if(stat_type == 0){
					stat_string = "strength";
					short_stat_string = "STR";
				}
				if(stat_type == 1){
					stat_string = "dexterity";
					short_stat_string = "DEX";
				}
				if(stat_type == 2){
					stat_string = "vitality";
					short_stat_string = "VIT";
				}
				if(stat_type == 3){
					stat_string = "intellect";
					short_stat_string = "INT";
				}
				

				weapon_data += stat_string + "=" + stat_val + ":";
				wep_description += red.toString() + short_stat_string + ": +" + stat_val + ",";
			}
			if(stat_chance <= 10){
				stat_val = new Random().nextInt(315) + 1;
				stat_chance = r.nextInt(100);
				if(stat_type >= 0 && stat_type < 3){
					stat_type++;
				}
				else if(stat_type == 3){
					stat_type = 0;
				}

				if(stat_type == 0){
					stat_string = "strength";
					short_stat_string = "STR";
				}
				if(stat_type == 1){
					stat_string = "dexterity";
					short_stat_string = "DEX";
				}
				if(stat_type == 2){
					stat_string = "vitality";
					short_stat_string = "VIT";
				}
				if(stat_type == 3){
					stat_string = "intellect";
					short_stat_string = "INT";
				}
				

				weapon_data += stat_string + "=" + stat_val + ":";
				wep_description += red.toString() + short_stat_string + ": +" + stat_val + ",";
			}
			if(stat_chance <= 5){
				stat_val = new Random().nextInt(315) + 1;
				if(stat_type >= 0 && stat_type < 3){
					stat_type++;
				}
				else if(stat_type == 3){
					stat_type = 0;
				}

				if(stat_type == 0){
					stat_string = "strength";
					short_stat_string = "STR";
				}
				if(stat_type == 1){
					stat_string = "dexterity";
					short_stat_string = "DEX";
				}
				if(stat_type == 2){
					stat_string = "vitality";
					short_stat_string = "VIT";
				}
				if(stat_type == 3){
					stat_string = "intellect";
					short_stat_string = "INT";
				}
				

				weapon_data += stat_string + "=" + stat_val + ":";
				wep_description += red.toString() + short_stat_string + ": +" + stat_val + ",";
			}
		}

		if(pure_dmg == true){
			weapon_data += "pure_dmg=" + pure_dmg_val + ":";
			wep_name = "Pure " + wep_name;
			wep_description += red.toString() + "PURE DMG: +" + pure_dmg_val + ",";
		}
		
		if(accuracy == true){
			weapon_data += "accuracy=" + knockback_percent + ":";
			wep_name = "Accurate " + wep_name;
			wep_description += red.toString() + "ACCURACY: " + accuracy_val + "%,";
			// Take from first index of kb= to next index of : from that point.
		}

		if(knockback == true){
			weapon_data += "kb=" + knockback_percent + ":";
			wep_name = "Brute " + wep_name;
			wep_description += red.toString() + "KNOCKBACK: " + knockback_percent + "%,";
			// Take from first index of kb= to next index of : from that point.
		}

		if(blind == true){
			weapon_data += "blind=" + blind_percent;
			wep_name = wep_name + " of Blindness";
			wep_description += red.toString() + "BLIND: " + blind_percent + "%,";
		}

		if(life_steal == true){
			weapon_data += "ls=" + life_steal_percent + ":";
			wep_name = "Vampyric " + wep_name; 
			wep_description += red.toString() + "LIFE STEAL: " + life_steal_percent + "%,";
			// life_steal == .split(":")[2]
		}

		if(crit_hit == true){
			weapon_data += "crit=" + crit_hit_percent + ":";
			wep_name = "Deadly " + wep_name;
			wep_description += red.toString() + "CRITICAL HIT: " + crit_hit_percent + "%,";
		}

		if(armor_pen == true){
			weapon_data += "armor_pen=" + armor_pen_val + ":";
			wep_name = "Penetrating " + wep_name;
			wep_description += red.toString() + "ARMOR PENETRATION: " + armor_pen_val + "%,";
		}

		if(r.nextInt(100) <= vs_modifier_chance){
			vs_modifier_amount = r.nextInt(vs_modifier_max) + 1;
			int type = r.nextInt(2);
			if(type == 0){
				vs_modifier_type = "monsters";
			}
			else if(type == 1){
				vs_modifier_type = "players";
			}
		}
		
		if(vs_modifier_amount > 0){
			String vs_modifier_data = "vs_" + vs_modifier_type;
			weapon_data += vs_modifier_data + "=" + vs_modifier_amount + ":";
			if(vs_modifier_data.contains("monsters")){
				if(wep_name.contains("of")){
					wep_name = wep_name + " Slaying";
				}
				else{
					wep_name = wep_name + " of Slaying";
				}
			}
			if(vs_modifier_data.contains("players")){
				if(wep_name.contains("of")){
					wep_name = wep_name + " Slaughter";
				}
				else{
					wep_name = wep_name + " of Slaughter";
				}
			}
			wep_description += red.toString() + "vs. " + vs_modifier_type.toUpperCase() + ": +" + vs_modifier_amount + "% DMG,";
		}
		
		if(elemental_dmg == true){
			weapon_data += "edmg=" + element_dmg_type + "," + element_dmg_amount + ":";

			if(blind == true || wep_name.contains("of")){
				wep_name = wep_name + " " + element_dmg_type.substring(0, 1).toUpperCase() + element_dmg_type.substring(1, element_dmg_type.length());
			}
			if(blind == false){
				wep_name = wep_name + " of " + element_dmg_type.substring(0, 1).toUpperCase() + element_dmg_type.substring(1, element_dmg_type.length());
			}

			wep_description += red.toString() + element_dmg_type.toUpperCase() + " DMG: +" + element_dmg_amount + ",";
			// elemental_dmg == .split(":")[3].split(",")[1]
			// edmg=fire,3
		}

		wep_description += rarity;

		if(enchant_count > 0){
			wep_name = ChatColor.RED + "[+" + enchant_count + "]" + " " + tag.toString() + wep_name;
		}
		else{
			wep_name = tag.toString() + wep_name;
		}
		//log.info(wep_name + " = " + weapon_data);

		Attributes attributes = new Attributes(ItemMechanics.signCustomItem(m, (short)0, wep_name, wep_description));
		attributes.clear();
	
		return attributes.getStack();
	}

	public static ItemStack StaffGenorator(Material m, boolean reroll, ItemStack original){
		Random r = new Random();
		boolean life_steal = false, elemental_dmg = false, knockback = false, crit_hit = false, pure_dmg = false, armor_pen = false, blind = false/*, specific_dmg = false*/;
		int life_steal_percent = 0;
		int knockback_percent = 0;
		int crit_hit_percent = 0;
		int blind_percent = 0;

		int life_steal_chance = 0;
		int knockback_chance = 0;
		int crit_hit_chance = 0;
		int blind_chance = 0;
		int edmg_chance = 0;

		int life_steal_max = 0;
		int knockback_max = 0;
		int crit_hit_max = 0;
		int blind_max = 0;
		int edmg_max = 0;

		int vs_modifier_max = 0;
		int vs_modifier_chance = 0;
		int vs_modifier_amount = 0;
		String vs_modifier_type = "";
		
		//int pure_dmg_chance = 0;
		//int pure_dmg_max = 0;
		int pure_dmg_val = 0;

		//int accuracy_chance = 0;
		//int accuracy_max = 0;
		int accuracy_val = 0;
		boolean accuracy = false;
		
		//int armor_pen_chance = 0;
		//int armor_pen_max = 0;
		int armor_pen_val = 0;

		//int energy_cost = 0;
		int tier = 1;

		int element_dmg_amount = 0;
		String element_dmg_type = "";

		String wep_name = "";
		String wep_description = "";

		ChatColor tag = ChatColor.WHITE;

		if(m == Material.WOOD_HOE){
			wep_name = "Staff";
			tag = ChatColor.WHITE;
			//energy_cost = 12;
			tier = 1;

			life_steal_chance = 2;
			knockback_chance = 3;
			crit_hit_chance = 2;
			blind_chance = 3;
			edmg_chance = 6;

			vs_modifier_chance = 6;
			vs_modifier_max = 10;
			
			//accuracy_chance = 8;
			//accuracy_max = 10;
			
			//pure_dmg_chance = 6;
			//armor_pen_chance = 20;
			//pure_dmg_max = 5;
			//armor_pen_max = 1;

			life_steal_max = 30;
			knockback_max = 3;
			crit_hit_max = 2;
			blind_max = 5;
			edmg_max = 4;
		}
		if(m == Material.STONE_HOE){
			wep_name = "Battlestaff";
			tag = ChatColor.GREEN;
			//energy_cost = 14;
			tier = 2;

			life_steal_chance = 4;
			knockback_chance = 10;
			crit_hit_chance = 5;
			blind_chance = 5;
			edmg_chance = 9;

			vs_modifier_chance = 9;
			vs_modifier_max = 12;
			
			//accuracy_chance = 12;
			//accuracy_max = 12;
			
			//pure_dmg_chance = 9;
			//armor_pen_chance = 20;
			//pure_dmg_max = 8;
			//armor_pen_max = 3;

			life_steal_max = 15;
			knockback_max = 6;
			crit_hit_max = 4;
			blind_max = 7;
			edmg_max = 9;
		}
		if(m == Material.IRON_HOE){
			wep_name = "Wizard Staff";
			tag = ChatColor.AQUA;
			//energy_cost = 16;
			tier = 3;

			life_steal_chance = 5;
			knockback_chance = 13;
			crit_hit_chance = 8;
			blind_chance = 8;
			edmg_chance = 10;

			vs_modifier_chance = 10;
			vs_modifier_max = 15;
			
			//accuracy_chance = 15;
			//accuracy_max = 25;
			
			//pure_dmg_chance = 5;
			//armor_pen_chance = 25;
			//pure_dmg_max = 15;
			//armor_pen_max = 5;

			life_steal_max = 12;
			knockback_max = 12;
			crit_hit_max = 5;
			blind_max = 9;
			edmg_max = 15;
		}
		if(m == Material.DIAMOND_HOE){
			wep_name = "Ancient Staff";
			tag = ChatColor.LIGHT_PURPLE;
			//energy_cost = 20;
			tier = 4;

			life_steal_chance = 10;
			knockback_chance = 16;
			crit_hit_chance = 9;
			blind_chance = 9;
			edmg_chance = 15;
			
			vs_modifier_chance = 12;
			vs_modifier_max = 20;

			//accuracy_chance = 20;
			//accuracy_max = 28;
			
			//pure_dmg_chance = 5;
			//armor_pen_chance = 20;
			//pure_dmg_max = 25;
			//armor_pen_max = 8;

			life_steal_max = 7;
			knockback_max = 15;
			crit_hit_max = 6;
			blind_max = 9;
			edmg_max = 25;
		}

		if(m == Material.GOLD_HOE){
			wep_name = "Legendary Staff";
			tag = ChatColor.YELLOW;
			//energy_cost = 25;
			tier = 5;

			life_steal_chance = 8;
			knockback_chance = 20;
			crit_hit_chance = 7;
			blind_chance = 11;
			edmg_chance = 20;

			vs_modifier_chance = 15;
			vs_modifier_max = 15;
			
			//accuracy_chance = 15;
			//accuracy_max = 35;
			
			//pure_dmg_chance = 10;
			//armor_pen_chance = 15;
			//pure_dmg_max = 45;
			//armor_pen_max = 10;

			life_steal_max = 8;
			knockback_max = 20;
			crit_hit_max = 10;
			blind_max = 11;
			edmg_max = 55;
		}

		int dmg_range_check = new Random().nextInt(100);
		int min_dmg = 0;
		int max_dmg = 0;
		String rarity = "";
		
		if(dmg_range_check <= 80){ // Tier 1 (low)
			rarity = ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + "Common";
			if(m == Material.WOOD_HOE){
				int min_min_dmg = 1;
				int max_min_dmg = 2 + 1; // +1 to offset the random gayness.

				int max_max_dmg = 3 + 1; // ^

				min_dmg = r.nextInt(max_min_dmg - min_min_dmg) + min_min_dmg;
				max_dmg = r.nextInt(max_max_dmg - min_min_dmg) + min_dmg;
			}
			if(m == Material.STONE_HOE){
				int min_min_dmg = 10;
				int max_min_dmg = 12 + 1; // +1 to offset the random gayness.

				int max_max_dmg = 15 + 1; // ^

				min_dmg = r.nextInt(max_min_dmg - min_min_dmg) + min_min_dmg;
				max_dmg = r.nextInt(max_max_dmg - min_min_dmg) + min_dmg;
			}
			if(m == Material.IRON_HOE){
				int min_min_dmg = 25;
				int max_min_dmg = 30 + 1; // +1 to offset the random gayness.

				int max_max_dmg = 40 + 1; // ^

				min_dmg = r.nextInt(max_min_dmg - min_min_dmg) + min_min_dmg;
				max_dmg = r.nextInt(max_max_dmg - min_min_dmg) + min_dmg;
			}
			if(m == Material.DIAMOND_HOE){
				int min_min_dmg = 65;
				int max_min_dmg = 80 + 1; // +1 to offset the random gayness.

				int max_max_dmg = 110 + 1; // ^

				min_dmg = r.nextInt(max_min_dmg - min_min_dmg) + min_min_dmg;
				max_dmg = r.nextInt(max_max_dmg - min_min_dmg) + min_dmg;
			}
			if(m == Material.GOLD_HOE){
				int min_min_dmg = 130;
				int max_min_dmg = 140 + 1; // +1 to offset the random gayness.

				int max_max_dmg = 200 + 1; // ^

				min_dmg = r.nextInt(max_min_dmg - min_min_dmg) + min_min_dmg;
				max_dmg = r.nextInt(max_max_dmg - min_min_dmg) + min_dmg;
			}
		}

		if(dmg_range_check > 80 && dmg_range_check < 95){ // Tier 1 (med)
			rarity = ChatColor.GREEN.toString() + ChatColor.ITALIC.toString() + "Uncommon";
			if(m == Material.WOOD_HOE){
				int min_min_dmg = 3;
				int max_min_dmg = 5 + 1; // +1 to offset the random gayness.

				int max_max_dmg = 6 + 1; // ^

				min_dmg = r.nextInt(max_min_dmg - min_min_dmg) + min_min_dmg;
				max_dmg = r.nextInt(max_max_dmg - min_min_dmg) + min_dmg;
			}
			if(m == Material.STONE_HOE){
				int min_min_dmg = 16;
				int max_min_dmg = 18 + 1; // +1 to offset the random gayness.

				int max_max_dmg = 22 + 1; // ^

				min_dmg = r.nextInt(max_min_dmg - min_min_dmg) + min_min_dmg;
				max_dmg = r.nextInt(max_max_dmg - min_min_dmg) + min_dmg;
			}
			if(m == Material.IRON_HOE){
				int min_min_dmg = 30;
				int max_min_dmg = 35 + 1; // +1 to offset the random gayness.

				int max_max_dmg = 65 + 1; // ^

				min_dmg = r.nextInt(max_min_dmg - min_min_dmg) + min_min_dmg;
				max_dmg = r.nextInt(max_max_dmg - min_min_dmg) + min_dmg;
			}
			if(m == Material.DIAMOND_HOE){
				int min_min_dmg = 70;
				int max_min_dmg = 85 + 1; // +1 to offset the random gayness.

				int max_max_dmg = 140 + 1; // ^

				min_dmg = r.nextInt(max_min_dmg - min_min_dmg) + min_min_dmg;
				max_dmg = r.nextInt(max_max_dmg - min_min_dmg) + min_dmg;
			}
			if(m == Material.GOLD_HOE){
				int min_min_dmg = 150;
				int max_min_dmg = 160 + 1; // +1 to offset the random gayness.

				int max_max_dmg = 250 + 1; // ^

				min_dmg = r.nextInt(max_min_dmg - min_min_dmg) + min_min_dmg;
				max_dmg = r.nextInt(max_max_dmg - min_min_dmg) + min_dmg;
			}
		}

		if(dmg_range_check >= 95 && dmg_range_check != 99){ // Tier 1 (high)
			rarity = ChatColor.AQUA.toString() + ChatColor.ITALIC.toString() + "Rare";
			if(m == Material.WOOD_HOE){
				int min_min_dmg = 6;
				int max_min_dmg = 9 + 1; // +1 to offset the random gayness.

				int max_max_dmg = 20 + 1; // ^

				min_dmg = r.nextInt(max_min_dmg - min_min_dmg) + min_min_dmg;
				max_dmg = r.nextInt(max_max_dmg - min_min_dmg) + min_dmg;
			}
			if(m == Material.STONE_HOE){
				int min_min_dmg = 20;
				int max_min_dmg = 30 + 1; // +1 to offset the random gayness.

				int max_max_dmg = 55 + 1; // ^

				min_dmg = r.nextInt(max_min_dmg - min_min_dmg) + min_min_dmg;
				max_dmg = r.nextInt(max_max_dmg - min_min_dmg) + min_dmg;
			}
			if(m == Material.IRON_HOE){
				int min_min_dmg = 30;
				int max_min_dmg = 60 + 1; // +1 to offset the random gayness.

				int max_max_dmg = 120 + 1; // ^

				min_dmg = r.nextInt(max_min_dmg - min_min_dmg) + min_min_dmg;
				max_dmg = r.nextInt(max_max_dmg - min_min_dmg) + min_dmg;
			}
			if(m == Material.DIAMOND_HOE){
				int min_min_dmg = 90;
				int max_min_dmg = 110 + 1; // +1 to offset the random gayness.

				int max_max_dmg = 210 + 1; // ^

				min_dmg = r.nextInt(max_min_dmg - min_min_dmg) + min_min_dmg;
				max_dmg = r.nextInt(max_max_dmg - min_min_dmg) + min_dmg;
			}
			if(m == Material.GOLD_HOE){
				int min_min_dmg = 160;
				int max_min_dmg = 230 + 1; // +1 to offset the random gayness.

				int max_max_dmg = 337 + 1; // ^

				min_dmg = r.nextInt(max_min_dmg - min_min_dmg) + min_min_dmg;
				max_dmg = r.nextInt(max_max_dmg - min_min_dmg) + min_dmg;
			}
		}
		
		if(dmg_range_check == 99){ // Tier 1 (high)
			rarity = ChatColor.YELLOW.toString() + ChatColor.ITALIC.toString() + "Unique";
			if(m == Material.WOOD_HOE){
				int min_min_dmg = 6;
				int max_min_dmg = 9 + 1; // +1 to offset the random gayness.

				int max_max_dmg = 20 + 1; // ^

				min_dmg = r.nextInt(max_min_dmg - min_min_dmg) + min_min_dmg;
				max_dmg = r.nextInt(max_max_dmg - min_min_dmg) + min_dmg;
			}
			if(m == Material.STONE_HOE){
				int min_min_dmg = 20;
				int max_min_dmg = 30 + 1; // +1 to offset the random gayness.

				int max_max_dmg = 55 + 1; // ^

				min_dmg = r.nextInt(max_min_dmg - min_min_dmg) + min_min_dmg;
				max_dmg = r.nextInt(max_max_dmg - min_min_dmg) + min_dmg;
			}
			if(m == Material.IRON_HOE){
				int min_min_dmg = 30;
				int max_min_dmg = 60 + 1; // +1 to offset the random gayness.

				int max_max_dmg = 120 + 1; // ^

				min_dmg = r.nextInt(max_min_dmg - min_min_dmg) + min_min_dmg;
				max_dmg = r.nextInt(max_max_dmg - min_min_dmg) + min_dmg;
			}
			if(m == Material.DIAMOND_HOE){
				int min_min_dmg = 90;
				int max_min_dmg = 110 + 1; // +1 to offset the random gayness.

				int max_max_dmg = 210 + 1; // ^

				min_dmg = r.nextInt(max_min_dmg - min_min_dmg) + min_min_dmg;
				max_dmg = r.nextInt(max_max_dmg - min_min_dmg) + min_dmg;
			}
			if(m == Material.GOLD_HOE){
				int min_min_dmg = 160;
				int max_min_dmg = 230 + 1; // +1 to offset the random gayness.

				int max_max_dmg = 337 + 1; // ^

				min_dmg = r.nextInt(max_min_dmg - min_min_dmg) + min_min_dmg;
				max_dmg = r.nextInt(max_max_dmg - min_min_dmg) + min_dmg;
			}
			
			min_dmg = (int)(min_dmg * 1.1);
			max_dmg = (int)(max_dmg * 1.1);
		}

		min_dmg = (int)(min_dmg / 2.0D);
		max_dmg = (int)(max_dmg / 2.0D);
		
		if(min_dmg < 1){
			min_dmg = 1;
		}
		if(max_dmg < 1){
			max_dmg = 1;
		}

		if(r.nextInt(100) <= life_steal_chance){ 
			life_steal = true;
			life_steal_percent = r.nextInt(life_steal_max) + 1; 
		}

		if(r.nextInt(100) <= edmg_chance){
			elemental_dmg = true;
			element_dmg_amount = r.nextInt(edmg_max) + 1;
			int elem_type_r = r.nextInt(3);
			if(elem_type_r == 0){
				element_dmg_type = "fire";
				element_dmg_amount -= (int)((double)element_dmg_amount * 0.15D);
			}
			if(elem_type_r == 1){
				element_dmg_type = "ice";
			}
			if(elem_type_r == 2){
				element_dmg_type = "poison";
				element_dmg_amount -= (int)((double)element_dmg_amount * 0.10D);
			}
		}

		if(r.nextInt(100) <= knockback_chance){
			knockback = true;
			knockback_percent = r.nextInt(knockback_max) + 1; 
		}

		if(r.nextInt(100) <= crit_hit_chance){ 
			crit_hit = true;
			crit_hit_percent = r.nextInt(crit_hit_max) + 1; // 1 - 10%
		}

		if(r.nextInt(100) <= blind_chance){ // 1% chance.
			blind = true;
			blind_percent = r.nextInt(blind_max) + 1; // 1 - 3%
		}

		int enchant_count = 0;
		// x = durability
		if(reroll == true && original != null){ // Keep the old DMG values instead.
			enchant_count = EnchantMechanics.getEnchantCount(original);
			rarity = ItemMechanics.getItemRarity(original);
			List<Integer> dmg_values = ItemMechanics.getDmgRangeOfWeapon(original);
			min_dmg = dmg_values.get(0);
			max_dmg = dmg_values.get(1);
		}

		@SuppressWarnings("unused")
		String weapon_data = m.name().toUpperCase() + "#" + min_dmg + "-" + max_dmg + ":";
		wep_description += red.toString() + "DMG: " + min_dmg + " - " + max_dmg + ",";
		// wood_HOE,1|23-25:ls=3:edmg=fire,2:kb=3

		int stat_chance = r.nextInt(100);
		int stat_type = r.nextInt(4); // 0, 1, 2
		String stat_string = "";
		String short_stat_string = "";
		int stat_val = 1;
		if(tier == 1){
			stat_val = new Random().nextInt(15) + 1;
			if(stat_chance <= 25){
				stat_chance = r.nextInt(100);
				if(stat_type == 0){
					stat_string = "strength";
					short_stat_string = "STR";
				}
				if(stat_type == 1){
					stat_string = "dexterity";
					short_stat_string = "DEX";
				}
				if(stat_type == 2){
					stat_string = "vitality";
					short_stat_string = "VIT";
				}
				if(stat_type == 3){
					stat_string = "intellect";
					short_stat_string = "INT";
				}
				

				weapon_data += stat_string + "=" + stat_val + ":";
				wep_description += red.toString() + short_stat_string + ": +" + stat_val + ",";
			}
		}
		if(tier == 2){
			if(stat_chance <= 20){
				stat_val = new Random().nextInt(35) + 1;
				stat_chance = r.nextInt(100);
				if(stat_type == 0){
					stat_string = "strength";
					short_stat_string = "STR";
				}
				if(stat_type == 1){
					stat_string = "dexterity";
					short_stat_string = "DEX";
				}
				if(stat_type == 2){
					stat_string = "vitality";
					short_stat_string = "VIT";
				}
				if(stat_type == 3){
					stat_string = "intellect";
					short_stat_string = "INT";
				}
				

				weapon_data += stat_string + "=" + stat_val + ":";
				wep_description += red.toString() + short_stat_string + ": +" + stat_val + ",";
			}
			if(stat_chance <= 5){
				stat_val = new Random().nextInt(35) + 1;
				stat_chance = r.nextInt(100);
				if(stat_type >= 0 && stat_type < 3){
					stat_type++;
				}
				else if(stat_type == 3){
					stat_type--;
				}

				if(stat_type == 0){
					stat_string = "strength";
					short_stat_string = "STR";
				}
				if(stat_type == 1){
					stat_string = "dexterity";
					short_stat_string = "DEX";
				}
				if(stat_type == 2){
					stat_string = "vitality";
					short_stat_string = "VIT";
				}
				if(stat_type == 3){
					stat_string = "intellect";
					short_stat_string = "INT";
				}
				

				weapon_data += stat_string + "=" + stat_val + ":";
				wep_description += red.toString() + short_stat_string + ": +" + stat_val + ",";
			}
		}
		if(tier == 3){
			if(stat_chance <= 15){
				stat_val = new Random().nextInt(75) + 1;
				stat_chance = r.nextInt(100);
				if(stat_type == 0){
					stat_string = "strength";
					short_stat_string = "STR";
				}
				if(stat_type == 1){
					stat_string = "dexterity";
					short_stat_string = "DEX";
				}
				if(stat_type == 2){
					stat_string = "vitality";
					short_stat_string = "VIT";
				}
				if(stat_type == 3){
					stat_string = "intellect";
					short_stat_string = "INT";
				}
				

				weapon_data += stat_string + "=" + stat_val + ":";
				wep_description += red.toString() + short_stat_string + ": +" + stat_val + ",";
			}
			if(stat_chance <= 5){
				stat_val = new Random().nextInt(75) + 1;
				stat_chance = r.nextInt(100);
				if(stat_type >= 0 && stat_type < 3){
					stat_type++;
				}
				else if(stat_type == 3){
					stat_type = 0;
				}

				if(stat_type == 0){
					stat_string = "strength";
					short_stat_string = "STR";
				}
				if(stat_type == 1){
					stat_string = "dexterity";
					short_stat_string = "DEX";
				}
				if(stat_type == 2){
					stat_string = "vitality";
					short_stat_string = "VIT";
				}
				if(stat_type == 3){
					stat_string = "intellect";
					short_stat_string = "INT";
				}
				

				weapon_data += stat_string + "=" + stat_val + ":";
				wep_description += red.toString() + short_stat_string + ": +" + stat_val + ",";
			}
			if(stat_chance <= 1){
				stat_val = new Random().nextInt(75) + 1;
				stat_chance = r.nextInt(100);
				if(stat_type >= 0 && stat_type < 3){
					stat_type++;
				}
				else if(stat_type == 3){
					stat_type = 0;
				}

				if(stat_type == 0){
					stat_string = "strength";
					short_stat_string = "STR";
				}
				if(stat_type == 1){
					stat_string = "dexterity";
					short_stat_string = "DEX";
				}
				if(stat_type == 2){
					stat_string = "vitality";
					short_stat_string = "VIT";
				}
				if(stat_type == 3){
					stat_string = "intellect";
					short_stat_string = "INT";
				}
				

				weapon_data += stat_string + "=" + stat_val + ":";
				wep_description += red.toString() + short_stat_string + ": +" + stat_val + ",";
			}
		}
		if(tier == 4){
			if(stat_chance <= 15){
				stat_val = new Random().nextInt(115) + 1;
				stat_chance = r.nextInt(100);
				if(stat_type == 0){
					stat_string = "strength";
					short_stat_string = "STR";
				}
				if(stat_type == 1){
					stat_string = "dexterity";
					short_stat_string = "DEX";
				}
				if(stat_type == 2){
					stat_string = "vitality";
					short_stat_string = "VIT";
				}
				if(stat_type == 3){
					stat_string = "intellect";
					short_stat_string = "INT";
				}
				

				weapon_data += stat_string + "=" + stat_val + ":";
				wep_description += red.toString() + short_stat_string + ": +" + stat_val + ",";
			}
			if(stat_chance <= 9){
				stat_val = new Random().nextInt(115) + 1;
				stat_chance = r.nextInt(100);
				if(stat_type >= 0 && stat_type < 3){
					stat_type++;
				}
				else if(stat_type == 3){
					stat_type = 0;
				}

				if(stat_type == 0){
					stat_string = "strength";
					short_stat_string = "STR";
				}
				if(stat_type == 1){
					stat_string = "dexterity";
					short_stat_string = "DEX";
				}
				if(stat_type == 2){
					stat_string = "vitality";
					short_stat_string = "VIT";
				}
				if(stat_type == 3){
					stat_string = "intellect";
					short_stat_string = "INT";
				}
				

				weapon_data += stat_string + "=" + stat_val + ":";
				wep_description += red.toString() + short_stat_string + ": +" + stat_val + ",";
			}
			if(stat_chance <= 4){
				stat_val = new Random().nextInt(115) + 1;
				stat_chance = r.nextInt(100);
				if(stat_type >= 0 && stat_type < 3){
					stat_type++;
				}
				else if(stat_type == 3){
					stat_type = 0;
				}

				if(stat_type == 0){
					stat_string = "strength";
					short_stat_string = "STR";
				}
				if(stat_type == 1){
					stat_string = "dexterity";
					short_stat_string = "DEX";
				}
				if(stat_type == 2){
					stat_string = "vitality";
					short_stat_string = "VIT";
				}
				if(stat_type == 3){
					stat_string = "intellect";
					short_stat_string = "INT";
				}
				

				weapon_data += stat_string + "=" + stat_val + ":";
				wep_description += red.toString() + short_stat_string + ": +" + stat_val + ",";
			}
		}
		if(tier == 5){
			if(stat_chance <= 20){
				stat_val = new Random().nextInt(315) + 1;
				stat_chance = r.nextInt(100);
				if(stat_type == 0){
					stat_string = "strength";
					short_stat_string = "STR";
				}
				if(stat_type == 1){
					stat_string = "dexterity";
					short_stat_string = "DEX";
				}
				if(stat_type == 2){
					stat_string = "vitality";
					short_stat_string = "VIT";
				}
				if(stat_type == 3){
					stat_string = "intellect";
					short_stat_string = "INT";
				}
				

				weapon_data += stat_string + "=" + stat_val + ":";
				wep_description += red.toString() + short_stat_string + ": +" + stat_val + ",";
			}
			if(stat_chance <= 10){
				stat_val = new Random().nextInt(315) + 1;
				stat_chance = r.nextInt(100);
				if(stat_type >= 0 && stat_type < 3){
					stat_type++;
				}
				else if(stat_type == 3){
					stat_type = 0;
				}

				if(stat_type == 0){
					stat_string = "strength";
					short_stat_string = "STR";
				}
				if(stat_type == 1){
					stat_string = "dexterity";
					short_stat_string = "DEX";
				}
				if(stat_type == 2){
					stat_string = "vitality";
					short_stat_string = "VIT";
				}
				if(stat_type == 3){
					stat_string = "intellect";
					short_stat_string = "INT";
				}
				

				weapon_data += stat_string + "=" + stat_val + ":";
				wep_description += red.toString() + short_stat_string + ": +" + stat_val + ",";
			}
			if(stat_chance <= 5){
				stat_val = new Random().nextInt(315) + 1;
				if(stat_type >= 0 && stat_type < 3){
					stat_type++;
				}
				else if(stat_type == 3){
					stat_type = 0;
				}

				if(stat_type == 0){
					stat_string = "strength";
					short_stat_string = "STR";
				}
				if(stat_type == 1){
					stat_string = "dexterity";
					short_stat_string = "DEX";
				}
				if(stat_type == 2){
					stat_string = "vitality";
					short_stat_string = "VIT";
				}
				if(stat_type == 3){
					stat_string = "intellect";
					short_stat_string = "INT";
				}
				

				weapon_data += stat_string + "=" + stat_val + ":";
				wep_description += red.toString() + short_stat_string + ": +" + stat_val + ",";
			}
		}

		if(pure_dmg == true){
			weapon_data += "pure_dmg=" + pure_dmg_val + ":";
			wep_name = "Pure " + wep_name;
			wep_description += red.toString() + "PURE DMG: +" + pure_dmg_val + ",";
		}
		
		if(accuracy == true){
			weapon_data += "accuracy=" + knockback_percent + ":";
			wep_name = "Accurate " + wep_name;
			wep_description += red.toString() + "ACCURACY: " + accuracy_val + "%,";
			// Take from first index of kb= to next index of : from that point.
		}

		if(knockback == true){
			weapon_data += "kb=" + knockback_percent + ":";
			wep_name = "Brute " + wep_name;
			wep_description += red.toString() + "KNOCKBACK: " + knockback_percent + "%,";
			// Take from first index of kb= to next index of : from that point.
		}

		if(blind == true){
			weapon_data += "blind=" + blind_percent;
			wep_name = wep_name + " of Blindness";
			wep_description += red.toString() + "BLIND: " + blind_percent + "%,";
		}

		if(life_steal == true){
			weapon_data += "ls=" + life_steal_percent + ":";
			wep_name = "Vampyric " + wep_name; 
			wep_description += red.toString() + "LIFE STEAL: " + life_steal_percent + "%,";
			// life_steal == .split(":")[2]
		}

		if(crit_hit == true){
			weapon_data += "crit=" + crit_hit_percent + ":";
			wep_name = "Deadly " + wep_name;
			wep_description += red.toString() + "CRITICAL HIT: " + crit_hit_percent + "%,";
		}

		if(armor_pen == true){
			weapon_data += "armor_pen=" + armor_pen_val + ":";
			wep_name = "Penetrating " + wep_name;
			wep_description += red.toString() + "ARMOR PENETRATION: " + armor_pen_val + "%,";
		}
		
		if(r.nextInt(100) <= vs_modifier_chance){
			vs_modifier_amount = r.nextInt(vs_modifier_max) + 1;
			int type = r.nextInt(2);
			if(type == 0){
				vs_modifier_type = "monsters";
			}
			else if(type == 1){
				vs_modifier_type = "players";
			}
		}
		
		if(vs_modifier_amount > 0){
			String vs_modifier_data = "vs_" + vs_modifier_type;
			weapon_data += vs_modifier_data + "=" + vs_modifier_amount + ":";
			if(vs_modifier_data.contains("monsters")){
				if(wep_name.contains("of")){
					wep_name = wep_name + " Slaying";
				}
				else{
					wep_name = wep_name + " of Slaying";
				}
			}
			if(vs_modifier_data.contains("players")){
				if(wep_name.contains("of")){
					wep_name = wep_name + " Slaughter";
				}
				else{
					wep_name = wep_name + " of Slaughter";
				}
			}
			wep_description += red.toString() + "vs. " + vs_modifier_type.toUpperCase() + ": +" + vs_modifier_amount + "% DMG,";
		}

		if(elemental_dmg == true){
			weapon_data += "edmg=" + element_dmg_type + "," + element_dmg_amount + ":";

			if(blind == true || wep_name.contains("of")){
				wep_name = wep_name + " " + element_dmg_type.substring(0, 1).toUpperCase() + element_dmg_type.substring(1, element_dmg_type.length());
			}
			if(blind == false){
				wep_name = wep_name + " of " + element_dmg_type.substring(0, 1).toUpperCase() + element_dmg_type.substring(1, element_dmg_type.length());
			}

			wep_description += red.toString() + element_dmg_type.toUpperCase() + " DMG: +" + element_dmg_amount + ",";
			// elemental_dmg == .split(":")[3].split(",")[1]
			// edmg=fire,3
		}

		wep_description += rarity;

		if(enchant_count > 0){
			wep_name = ChatColor.RED + "[+" + enchant_count + "]" + " " + tag.toString() + wep_name;
		}
		else{
			wep_name = tag.toString() + wep_name;
		}
		//log.info(wep_name + " = " + weapon_data);

		Attributes attributes = new Attributes(ItemMechanics.signCustomItem(m, (short)0, wep_name, wep_description));
		attributes.clear();
	
		return attributes.getStack();
	}
}
