package minecade.dungeonrealms.ItemMechanics.mobarmor;

import java.util.Random;

import minecade.dungeonrealms.ItemMechanics.ItemMechanics;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class Tier1Gear {
    static ChatColor red = ChatColor.RED;

    public static ItemStack MobChestPlateGenerator(int mob_tier_level) {
        Random r = new Random();
        Material m = null;
        boolean hp_regen = false, energy_regen = false, block = false, dodge = false, thorns = false, reflection = false, gem_find = false, item_find = false;
        int hp_regen_percent = 0;
        int hp_increase_amount = 0;
        int energy_regen_percent = 0;
        int block_percent = 0;
        int dodge_percent = 0;
        int thorns_percent = 0;
        int reflection_percent = 0;
        int gem_find_percent = 0;
        int item_find_percent = 0;
        int block_chance = 0;
        int dodge_chance = 0;
        int thorns_chance = 0;
        int reflection_chance = 0;
        int gem_find_chance = 0;
        int item_find_chance = 0;

        int hp_regen_min = 0;
        int energy_regen_min = 0;
        int block_min = 0;
        int dodge_min = 0;
        int thorns_min = 0;
        int reflection_min = 0;
        int gem_find_min = 0;
        int item_find_min = 0;

        int hp_regen_max = 0;
        int energy_regen_max = 0;
        int block_max = 0;
        int dodge_max = 0;
        int thorns_max = 0;
        int reflection_max = 0;
        int gem_find_max = 0;
        int item_find_max = 0;

        String armor_name = "";
        String armor_description = "";

        ChatColor tag = ChatColor.WHITE;

        armor_name = "Leather Chestplate";
        tag = ChatColor.WHITE;
        m = Material.LEATHER_CHESTPLATE;

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
        int min_armor = 0;
        int max_armor = 0;
        String rarity = "";
        rarity = ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + "Common";
        // int min_num = 1 + 1;
        // int max_num = 2 + 1;

        min_armor = 1;
        max_armor = 1;

        int min_hp = 10;
        int max_hp = 20;

        hp_increase_amount = r.nextInt(max_hp - min_hp) + min_hp;
        if (min_armor == 0) {
            min_armor = 1;
        }

        if (max_armor == 0) {
            min_armor = 1;
            max_armor = 1;
        }

        int regen_type = r.nextInt(2); // 0, 1.

        if (regen_type == 0) { // HP/s HP REGEN
            hp_regen = true;
            double regen_val = hp_increase_amount * 0.05;
            if (regen_val < 1) {
                regen_val = 1;
            }

            int real_hp_regen_max = (int) ((regen_val) + (hp_increase_amount / 10)) - r.nextInt((int) Math.round(regen_val));
            if (real_hp_regen_max < 1) {
                real_hp_regen_max = 1;
            }
            // hp_regen_percent = r.nextInt(real_hp_regen_max) + 1;
            hp_regen_percent = r.nextInt(hp_regen_max - hp_regen_min) + hp_regen_min;
        } else if (regen_type == 1) {
            energy_regen = true;
            energy_regen_percent = r.nextInt(energy_regen_max - energy_regen_min) + energy_regen_min;
            energy_regen_percent = energy_regen_percent / 2;

            if (energy_regen_percent < 1) {
                energy_regen_percent = 1;
            }
        }

        if (r.nextInt(100) <= block_chance) {
            block = true;
            block_percent = r.nextInt(block_max - block_min) + block_min; // 1 - 10%
        }

        if (r.nextInt(100) <= dodge_chance) { // 1% chance.
            dodge = true;
            dodge_percent = r.nextInt(dodge_max - dodge_min) + dodge_min; // 1 - 3%
        }

        if (r.nextInt(100) <= thorns_chance) { // 1% chance.
            thorns = true;
            thorns_percent = r.nextInt(thorns_max - thorns_min) + thorns_min; // 1 - 3%
        }

        if (r.nextInt(100) <= reflection_chance) { // 1% chance.
            reflection = true;
            try {
                reflection_percent = r.nextInt(reflection_max - reflection_min) + reflection_min; // 1 - 3%
            } catch (IllegalArgumentException iae) {
                reflection_percent = reflection_min;
            }
        }

        if (r.nextInt(100) <= gem_find_chance) { // 1% chance.
            gem_find = true;
            gem_find_percent = r.nextInt(gem_find_max - gem_find_min) + gem_find_min; // 1 - 3%
        }

        if (r.nextInt(1000) <= item_find_chance) { // 1% chance.
            item_find = true;
            if (item_find_max - item_find_min <= 0) {
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
        if (armor_type == 0) {
            armor_data = m.name().toUpperCase() + "#" + min_armor + "-" + max_armor + ":";
            armor_description += red.toString() + "ARMOR: " + min_armor + " - " + max_armor + "%,";
        }
        if (armor_type == 1) {
            armor_data = m.name().toUpperCase() + "#" + min_armor + "-" + max_armor + ":";
            armor_description += red.toString() + "DPS: " + min_armor + " - " + max_armor + "%,";
        }

        if (hp_increase_amount <= 0) {
            hp_increase_amount = 1;
        }

        armor_data += "hp_increase=" + hp_increase_amount + "@";
        armor_description += red.toString() + "HP: +" + hp_increase_amount + ",";

        if (hp_regen == true) {
            armor_data += "hp_regen=" + hp_regen_percent + "@hp_regen_split@:";
            armor_description += red.toString() + "HP REGEN: +" + hp_regen_percent + " HP/s,";
        }

        if (energy_regen == true) {
            armor_data += "energy_regen=" + energy_regen_percent + ":";
            armor_name = armor_name + " of Fortitude";
            armor_description += red.toString() + "ENERGY REGEN: +" + energy_regen_percent + "%,";
        }

        int stat_chance = r.nextInt(100);
        int stat_type = r.nextInt(4); // 0, 1, 2
        String stat_string = "";
        String short_stat_string = "";
        int stat_val = 1;
        stat_val = new Random().nextInt(15) + 1;
        if (stat_chance <= 25) {
            stat_chance = r.nextInt(100);
            if (stat_type == 0) {
                stat_string = "strength";
                short_stat_string = "STR";
            }
            if (stat_type == 1) {
                stat_string = "dexterity";
                short_stat_string = "DEX";
            }
            if (stat_type == 2) {
                stat_string = "vitality";
                short_stat_string = "VIT";
            }
            if (stat_type == 3) {
                stat_string = "intellect";
                short_stat_string = "INT";
            }

            armor_data += stat_string + "=" + stat_val + ":";
            armor_description += red.toString() + short_stat_string + ": +" + stat_val + ",";
        }

        int res_chance = r.nextInt(100);
        int res_type = r.nextInt(3); // 0-fire, 1-ice, 2-poison
        String res_string = "";
        String res_shorthand = "";
        int res_val = -1;

        if (res_chance <= 15) {
            res_val = new Random().nextInt(5) + 1;
        }

        if (res_type == 0) {
            res_string = "fire_resistance";
            res_shorthand = "FIRE";
        }
        if (res_type == 1) {
            res_string = "ice_resistance";
            res_shorthand = "ICE";
        }
        if (res_type == 2) {
            res_string = "poison_resistance";
            res_shorthand = "POISON";
        }

        if (res_val > 0) {
            armor_data += res_string + "=" + res_val + ":";
            armor_description += red.toString() + res_shorthand + " RESISTANCE: " + res_val + "%,";
            if (!armor_name.contains("of")) {
                armor_name = armor_name + " of ";
            } else {
                armor_name = armor_name + " and ";
            }

            if (res_type == 0) {
                armor_name = armor_name + "Fire Resist";
            }
            if (res_type == 1) {
                armor_name = armor_name + "Ice Resist";
            }
            if (res_type == 2) {
                armor_name = armor_name + "Poison Resist";
            }
        }

        if (dodge == true) {
            armor_data += "dodge=" + dodge_percent + ":";
            armor_name = "Agile " + armor_name;
            armor_description += red.toString() + "DODGE: " + dodge_percent + "%,";
        }

        if (reflection == true) {
            armor_data += "reflection=" + reflection_percent + ":";
            armor_name = "Reflective " + armor_name;
            armor_description += red.toString() + "REFLECTION: " + reflection_percent + "%,";
        }

        if (hp_regen == true) {
            armor_name = "Mending " + armor_name;
        }

        if (block == true) {
            armor_data += "block=" + block_percent + ":";
            ;
            armor_name = "Protective " + armor_name;
            armor_description += red.toString() + "BLOCK: " + block_percent + "%,";
        }

        if (gem_find == true) {
            armor_data += "gem_find=" + gem_find_percent + ":";
            ;
            if (armor_name.contains("of")) {
                armor_name = armor_name + " Golden";
            } else {
                armor_name = armor_name + " of Pickpocketing";
            }
            armor_description += red.toString() + "GEM FIND: " + gem_find_percent + "%,";
        }

        if (item_find == true) {
            armor_data += "item_find=" + item_find_percent;
            if (armor_name.contains("of")) {
                armor_name = armor_name + " Treasure";
            } else {
                armor_name = armor_name + " of Treasure";
            }
            armor_description += red.toString() + "ITEM FIND: " + item_find_percent + "%,";
        }

        if (thorns == true) {
            armor_data += "thorns=" + thorns_percent + ":";
            ;
            if (armor_name.contains("of")) {
                armor_name = armor_name + " Spikes";
            } else {
                armor_name = armor_name + " of Thorns";
            }
            armor_description += red.toString() + "THORNS: " + thorns_percent + "% DMG";
        }

        armor_description += "," + rarity;

        if (enchant_count > 0) {
            armor_name = ChatColor.RED + "[+" + enchant_count + "]" + " " + tag.toString() + armor_name;
        } else {
            armor_name = tag.toString() + armor_name;
        }

        return ItemMechanics.signCustomItem(m, (short) 0, armor_name, armor_description);
    }

    public static ItemStack MobHelmetGenerator(int mob_tier_level) {
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

        int block_chance = 0;
        int dodge_chance = 0;
        int thorns_chance = 0;
        int reflection_chance = 0;
        int gem_find_chance = 0;
        int item_find_chance = 0;

        int hp_regen_min = 0;
        int energy_regen_min = 0;

        int hp_regen_max = 0;
        int energy_regen_max = 0;
        int block_max = 0;
        int dodge_max = 0;
        int thorns_max = 0;
        int reflection_max = 0;
        int gem_find_max = 0;
        int item_find_max = 0;

        String armor_name = "";
        String armor_description = "";

        ChatColor tag = ChatColor.WHITE;

        armor_name = "Leather Coif";
        tag = ChatColor.WHITE;
        m = Material.LEATHER_HELMET;

        // hp_regen_chance = 10;
        // energy_regen_chance = 10;
        block_chance = 5;
        dodge_chance = 5;
        thorns_chance = 3;
        reflection_chance = 3;
        gem_find_chance = 5;
        item_find_chance = 5;

        hp_regen_min = 5;
        energy_regen_min = 1;
        // block_min = 1;
        // dodge_min = 1;
        // thorns_min = 1; // 0.1%
        // reflection_min = 1; // 0.1%
        // gem_find_min = 1; // 0.1%
        // item_find_min = 1; // 0.1%

        hp_regen_max = 15;
        energy_regen_max = 5;
        block_max = 5;
        dodge_max = 5;
        thorns_max = 2;
        reflection_max = 1;
        gem_find_max = 5;
        item_find_max = 1;

        int armor_range_check = new Random().nextInt(100);
        int min_armor = 0;
        int max_armor = 0;
        String rarity = "";
        if (mob_tier_level == 1) {
            rarity = ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + "Common";
            // int min_num = 1 + 1;
            // int max_num = 2 + 1;

            min_armor = 1;
            max_armor = 1;

            int min_hp = 10;
            int max_hp = 20;

            hp_increase_amount = r.nextInt(max_hp - min_hp) + min_hp;
        } else if (mob_tier_level == 2) {
            if (armor_range_check <= 70) {
                rarity = ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + "Common";
                // int min_num = 1 + 1;
                // int max_num = 2 + 1;

                min_armor = 1;
                max_armor = 1;

                int min_hp = 10;
                int max_hp = 20;

                hp_increase_amount = r.nextInt(max_hp - min_hp) + min_hp;
            } else {
                rarity = ChatColor.GREEN.toString() + ChatColor.ITALIC.toString() + "Uncommon";
                int min_num = 1 + 1;
                int max_num = 2 + 1;

                min_armor = r.nextInt(max_num - min_num) + (min_num - 1);
                max_armor = r.nextInt(max_num - min_armor) + min_armor;

                int min_hp = 20;
                int max_hp = 50;

                hp_increase_amount = r.nextInt(max_hp - min_hp) + min_hp;
            }
        } else if (mob_tier_level == 3) {
            if (armor_range_check <= 20) {
                rarity = ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + "Common";
                // int min_num = 1 + 1;
                // int max_num = 2 + 1;

                min_armor = 1;
                max_armor = 1;

                int min_hp = 10;
                int max_hp = 20;

                hp_increase_amount = r.nextInt(max_hp - min_hp) + min_hp;
            } else if (armor_range_check > 20 && armor_range_check <= 90) {
                rarity = ChatColor.GREEN.toString() + ChatColor.ITALIC.toString() + "Uncommon";
                int min_num = 1 + 1;
                int max_num = 2 + 1;

                min_armor = r.nextInt(max_num - min_num) + (min_num - 1);
                max_armor = r.nextInt(max_num - min_armor) + min_armor;

                int min_hp = 20;
                int max_hp = 50;

                hp_increase_amount = r.nextInt(max_hp - min_hp) + min_hp;
            } else {
                rarity = ChatColor.AQUA.toString() + ChatColor.ITALIC.toString() + "Rare";
                int min_num = 1 + 1;
                int max_num = 2 + 1;

                min_armor = r.nextInt(max_num - min_num) + (min_num - 1);
                max_armor = r.nextInt(max_num - min_armor) + min_armor;

                int min_hp = 80;
                int max_hp = 120;

                hp_increase_amount = r.nextInt(max_hp - min_hp) + min_hp;
            }
        } else if (mob_tier_level == 4) {
            if (armor_range_check <= 10) {
                rarity = ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + "Common";
                // int min_num = 1 + 1;
                // int max_num = 2 + 1;

                min_armor = 1;
                max_armor = 1;

                int min_hp = 10;
                int max_hp = 20;

                hp_increase_amount = r.nextInt(max_hp - min_hp) + min_hp;
            } else if (armor_range_check > 10 && armor_range_check <= 30) {
                rarity = ChatColor.GREEN.toString() + ChatColor.ITALIC.toString() + "Uncommon";
                int min_num = 1 + 1;
                int max_num = 2 + 1;

                min_armor = r.nextInt(max_num - min_num) + (min_num - 1);
                max_armor = r.nextInt(max_num - min_armor) + min_armor;

                int min_hp = 20;
                int max_hp = 50;

                hp_increase_amount = r.nextInt(max_hp - min_hp) + min_hp;
            } else if (armor_range_check > 30 && armor_range_check <= 99) {
                rarity = ChatColor.AQUA.toString() + ChatColor.ITALIC.toString() + "Rare";
                int min_num = 1 + 1;
                int max_num = 2 + 1;

                min_armor = r.nextInt(max_num - min_num) + (min_num - 1);
                max_armor = r.nextInt(max_num - min_armor) + min_armor;

                int min_hp = 80;
                int max_hp = 120;

                hp_increase_amount = r.nextInt(max_hp - min_hp) + min_hp;
            } else if (armor_range_check == 100) {
                rarity = ChatColor.YELLOW.toString() + ChatColor.ITALIC.toString() + "Unique";
                int min_num = 1 + 1;
                int max_num = 2 + 1;

                min_armor = r.nextInt(max_num - min_num) + (min_num - 1);
                max_armor = r.nextInt(max_num - min_armor) + min_armor;

                int min_hp = 80;
                int max_hp = 120;

                hp_increase_amount = r.nextInt(max_hp - min_hp) + min_hp;

                min_armor *= 1.1;
                max_armor *= 1.1;
                hp_increase_amount *= 1.1;
            }
        } else {
            rarity = ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + "Common";
            // int min_num = 1 + 1;
            // int max_num = 2 + 1;

            min_armor = 1;
            max_armor = 1;

            int min_hp = 10;
            int max_hp = 20;

            hp_increase_amount = r.nextInt(max_hp - min_hp) + min_hp;
        }
        double temp_min_armor = (double) min_armor;
        double temp_max_armor = (double) max_armor;

        double temp_hp_increase_amount = (double) hp_increase_amount;

        min_armor = (int) (temp_min_armor * 0.50); // 50% of the values of chestplate / leggings.
        max_armor = (int) (temp_max_armor * 0.50); // 50% of the values of chestplate / leggings.

        hp_increase_amount = (int) (temp_hp_increase_amount * 0.50); // 50% of HP increase value.

        if (min_armor == 0) {
            min_armor = 1;
        }

        if (max_armor == 0) {
            min_armor = 1;
            max_armor = 1;
        }

        int regen_type = r.nextInt(2); // 0, 1.

        if (regen_type == 0) { // HP/s HP REGEN
            hp_regen = true;
            // hp_regen_max = (int) Math.round((hp_increase_amount / 10) + (hp_increase_amount * 0.05));
            // if(hp_regen_max < 1){hp_regen_max = 1;}
            double regen_val = hp_increase_amount * 0.05;
            if (regen_val < 1) {
                regen_val = 1;
            }

            int real_hp_regen_max = (int) ((regen_val) + (hp_increase_amount / 10)) - r.nextInt((int) Math.round(regen_val));
            if (real_hp_regen_max < 1) {
                real_hp_regen_max = 1;
            }
            // hp_regen_percent = r.nextInt(real_hp_regen_max) + 1;
            hp_regen_percent = r.nextInt(hp_regen_max - hp_regen_min) + hp_regen_min;
        } else if (regen_type == 1) {
            energy_regen = true;
            energy_regen_percent = r.nextInt(energy_regen_max - energy_regen_min) + energy_regen_min;
            energy_regen_percent = energy_regen_percent / 2;

            if (energy_regen_percent < 1) {
                energy_regen_percent = 1;
            }
        }

        if (r.nextInt(100) <= block_chance) {
            block = true;
            block_percent = r.nextInt(block_max) + 1; // 1 - 10%
        }

        if (r.nextInt(100) <= dodge_chance) { // 1% chance.
            dodge = true;
            dodge_percent = r.nextInt(dodge_max) + 1; // 1 - 3%
        }

        if (r.nextInt(100) <= thorns_chance) { // 1% chance.
            thorns = true;
            thorns_percent = r.nextInt(thorns_max) + 1; // 1 - 3%
        }

        if (r.nextInt(100) <= reflection_chance) { // 1% chance.
            reflection = true;
            reflection_percent = r.nextInt(reflection_max) + 1; // 1 - 3%
        }

        if (r.nextInt(100) <= gem_find_chance) { // 1% chance.

            gem_find = true;
            gem_find_percent = r.nextInt(gem_find_max) + 1; // 1 - 3%
        }

        if (r.nextInt(100) <= item_find_chance) { // 1% chance.
            item_find = true;
            item_find_percent = r.nextInt(item_find_max) + 1; // 1 - 3%
        }

        // x = durability

        int armor_type = new Random().nextInt(2); // 0 or 1.

        @SuppressWarnings("unused")
        String armor_data = "";
        int enchant_count = 0;

        if (armor_type == 0) {
            armor_data = m.name().toUpperCase() + "#" + min_armor + "-" + max_armor + ":";
            armor_description += red.toString() + "ARMOR: " + min_armor + " - " + max_armor + "%,";
        }
        if (armor_type == 1) {
            armor_data = m.name().toUpperCase() + "#" + min_armor + "-" + max_armor + ":";
            armor_description += red.toString() + "DPS: " + min_armor + " - " + max_armor + "%,";
        }

        if (hp_increase_amount <= 0) {
            hp_increase_amount = 1;
        }

        armor_data += "hp_increase=" + hp_increase_amount + "@";
        // armor_name = armor_name + " of Fortitude";
        armor_description += red.toString() + "HP: +" + hp_increase_amount + ",";

        if (hp_regen == true) {
            armor_data += "hp_regen=" + hp_regen_percent + "@hp_regen_split@:";
            armor_description += red.toString() + "HP REGEN: +" + hp_regen_percent + " HP/s,";
        }

        if (energy_regen == true) {
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
        stat_val = new Random().nextInt(15) + 1;
        if (stat_chance <= 25) {
            stat_chance = r.nextInt(100);
            if (stat_type == 0) {
                stat_string = "strength";
                short_stat_string = "STR";
            }
            if (stat_type == 1) {
                stat_string = "dexterity";
                short_stat_string = "DEX";
            }
            if (stat_type == 2) {
                stat_string = "vitality";
                short_stat_string = "VIT";
            }
            if (stat_type == 3) {
                stat_string = "intellect";
                short_stat_string = "INT";
            }

            armor_data += stat_string + "=" + stat_val + ":";
            armor_description += red.toString() + short_stat_string + ": +" + stat_val + ",";
        }

        // Resistances
        int res_chance = r.nextInt(100);
        int res_type = r.nextInt(3); // 0-fire, 1-ice, 2-poison
        String res_string = "";
        String res_shorthand = "";
        int res_val = -1;

        if (res_chance <= 15) {
            res_val = new Random().nextInt(5) + 1;
        }

        if (res_type == 0) {
            res_string = "fire_resistance";
            res_shorthand = "FIRE";
        }
        if (res_type == 1) {
            res_string = "ice_resistance";
            res_shorthand = "ICE";
        }
        if (res_type == 2) {
            res_string = "poison_resistance";
            res_shorthand = "POISON";
        }

        if (res_val > 0) {
            // We have some resistance.
            armor_data += res_string + "=" + res_val + ":";
            armor_description += red.toString() + res_shorthand + " RESISTANCE: " + res_val + "%,";
            if (!armor_name.contains("of")) {
                armor_name = armor_name + " of ";
            } else {
                armor_name = armor_name + " and ";
            }

            if (res_type == 0) {
                armor_name = armor_name + "Fire Resist";
            }
            if (res_type == 1) {
                armor_name = armor_name + "Ice Resist";
            }
            if (res_type == 2) {
                armor_name = armor_name + "Poison Resist";
            }
        }

        if (dodge == true) {
            armor_data += "dodge=" + dodge_percent + ":";
            ;
            armor_name = "Agile " + armor_name;
            armor_description += red.toString() + "DODGE: " + dodge_percent + "%,";
        }

        if (reflection == true) {
            armor_data += "reflection=" + reflection_percent + ":";
            ;
            armor_name = "Reflective " + armor_name;
            armor_description += red.toString() + "REFLECTION: " + reflection_percent + "%,";
        }

        if (hp_regen == true) {
            armor_name = "Mending " + armor_name;
        }

        if (block == true) {
            armor_data += "block=" + block_percent + ":";
            ;
            armor_name = "Protective " + armor_name;
            armor_description += red.toString() + "BLOCK: " + block_percent + "%,";
        }

        if (gem_find == true) {
            armor_data += "gem_find=" + gem_find_percent + ":";
            ;
            if (armor_name.contains("of")) {
                armor_name = armor_name + " Golden";
            } else {
                armor_name = armor_name + " of Pickpocketing";
            }
            armor_description += red.toString() + "GEM FIND: " + gem_find_percent + "%,";
        }

        if (item_find == true) {
            armor_data += "item_find=" + item_find_percent;
            if (armor_name.contains("of")) {
                armor_name = armor_name + " Treasure";
            } else {
                armor_name = armor_name + " of Treasure";
            }
            armor_description += red.toString() + "ITEM FIND: " + item_find_percent + "%,";
        }

        if (thorns == true) {
            armor_data += "thorns=" + thorns_percent + ":";
            ;
            if (armor_name.contains("of")) {
                armor_name = armor_name + " Spikes";
            } else {
                armor_name = armor_name + " of Thorns";
            }
            armor_description += red.toString() + "THORNS: " + thorns_percent + "% DMG,";
        }

        armor_description += "," + rarity;

        if (enchant_count > 0) {
            armor_name = ChatColor.RED + "[+" + enchant_count + "]" + " " + tag.toString() + armor_name;
            ;
        } else {
            armor_name = tag.toString() + armor_name;
        }
        // log.info(armor_name + " = " + armor_data);

        return ItemMechanics.signCustomItem(m, (short) 0, armor_name, armor_description);
    }
}
