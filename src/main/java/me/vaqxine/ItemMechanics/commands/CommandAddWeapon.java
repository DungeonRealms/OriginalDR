package me.vaqxine.ItemMechanics.commands;

import java.util.Arrays;

import me.vaqxine.Main;
import me.vaqxine.EnchantMechanics.EnchantMechanics;
import me.vaqxine.ItemMechanics.Attributes;
import me.vaqxine.ItemMechanics.Halloween;
import me.vaqxine.ItemMechanics.ItemGenerators;
import me.vaqxine.ItemMechanics.ItemMechanics;
import me.vaqxine.MerchantMechanics.MerchantMechanics;
import me.vaqxine.RealmMechanics.RealmMechanics;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class CommandAddWeapon implements CommandExecutor {

	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player p = (Player)sender;
		
		if(!(p.isOp())){
			return true;
		}
		//getDamage(p.getItemInHand());

		if(args.length == 0){
			p.getInventory().clear();
			p.updateInventory();
			p.sendMessage(ChatColor.YELLOW + "Inventory Cleared.");
			return true;
		}

		if(args[0].equalsIgnoreCase("repair")){
			ItemStack hand = p.getItemInHand();
			hand.setDurability((short)0);
			p.setItemInHand(hand);
			Attributes attributes = new Attributes(p.getItemInHand());
			attributes.clear();
			//p.getInventory().addItem(attributes.getStack()); // TODO Seriously?
			p.updateInventory();
			return true;
		}

		if(args[0].equalsIgnoreCase("orb")){
			p.getInventory().addItem(ItemMechanics.orb_of_peace);
			p.getInventory().addItem(MerchantMechanics.orb_of_alteration);
			p.getInventory().addItem(MerchantMechanics.orb_of_alteration);
			p.getInventory().addItem(MerchantMechanics.orb_of_alteration);
			p.getInventory().addItem(MerchantMechanics.orb_of_alteration);
			p.getInventory().addItem(MerchantMechanics.orb_of_alteration);
		}

		if(args[0].equalsIgnoreCase("spooky")){
			p.getInventory().addItem(Halloween.halloween_candy);
			p.getInventory().addItem(Halloween.halloween_mask);
		}

		if(!(Main.isDev(p.getName()))){
			return true;
		}
		
		if(args[0].equalsIgnoreCase("noob")){
			for(ItemStack is : ItemMechanics.generateNoobArmor()){
				p.getInventory().addItem(RealmMechanics.makeUntradeable(is));
			}
		}
		if(args[0].equalsIgnoreCase("enchantwhite")){
		    p.getInventory().addItem(EnchantMechanics.t5_white_scroll);
		    p.getInventory().addItem(EnchantMechanics.t1_white_scroll);
		    p.getInventory().addItem(EnchantMechanics.t2_white_scroll);
		    p.getInventory().addItem(EnchantMechanics.t3_white_scroll);
		    p.getInventory().addItem(EnchantMechanics.t4_white_scroll);
		}
		if(args[0].equalsIgnoreCase("enchantarmor")){
		    p.getInventory().addItem(EnchantMechanics.t1_armor_scroll);
		    p.getInventory().addItem(EnchantMechanics.t2_armor_scroll);
		    p.getInventory().addItem(EnchantMechanics.t3_armor_scroll);
		    p.getInventory().addItem(EnchantMechanics.t4_armor_scroll);
		    p.getInventory().addItem(EnchantMechanics.t5_armor_scroll);
		}
		if(args[0].equalsIgnoreCase("enchantweapon")){
		    p.getInventory().addItem(EnchantMechanics.t1_wep_scroll);
		    p.getInventory().addItem(EnchantMechanics.t2_wep_scroll);
		    p.getInventory().addItem(EnchantMechanics.t3_wep_scroll);
		    p.getInventory().addItem(EnchantMechanics.t4_wep_scroll);
		    p.getInventory().addItem(EnchantMechanics.t5_wep_scroll);
		}
		if(args[0].equalsIgnoreCase("sword_of_famasss")){
		    ItemStack is = new ItemStack(Material.GOLD_SWORD);
		    ItemMeta im = is.getItemMeta();
		    im.setLore(Arrays.asList(ChatColor.RED + "DMG: 10000 - 15000", ChatColor.RED + "VIT: +1000"));
		    im.setDisplayName(ChatColor.YELLOW + "Majestic Sword of iFamasssxD :)");
		    is.setItemMeta(im);
		    p.getInventory().addItem(is);
		}
		if(args[0].equalsIgnoreCase("1")){
			p.getInventory().addItem(ItemMechanics.generateRandomTierItem(1));
		}
		if(args[0].equalsIgnoreCase("2")){
			p.getInventory().addItem(ItemMechanics.generateRandomTierItem(2));
		}
		if(args[0].equalsIgnoreCase("3")){
			p.getInventory().addItem(ItemMechanics.generateRandomTierItem(3));
		}
		if(args[0].equalsIgnoreCase("4")){
			p.getInventory().addItem(ItemMechanics.generateRandomTierItem(4));
		}
		if(args[0].equalsIgnoreCase("5")){
			p.getInventory().addItem(ItemMechanics.generateRandomTierItem(5));
		}
		if(args[0].equalsIgnoreCase("arrow")){
			p.getInventory().addItem(ItemMechanics.t1_arrow);
			p.getInventory().addItem(ItemMechanics.t2_arrow);
			p.getInventory().addItem(ItemMechanics.t3_arrow);
			p.getInventory().addItem(ItemMechanics.t4_arrow);
			p.getInventory().addItem(ItemMechanics.t5_arrow);
		}
		if(args[0].equalsIgnoreCase("egg")){
			p.getInventory().addItem(ItemMechanics.easter_egg);
		}
		if(args[0].equalsIgnoreCase("pots")){
			p.getInventory().addItem(MerchantMechanics.t1_pot);
			p.getInventory().addItem(MerchantMechanics.t2_pot);
			p.getInventory().addItem(MerchantMechanics.t3_pot);
			p.getInventory().addItem(MerchantMechanics.t4_pot);
			p.getInventory().addItem(MerchantMechanics.t5_pot);

			p.getInventory().addItem(MerchantMechanics.t1_s_pot);
			p.getInventory().addItem(MerchantMechanics.t2_s_pot);
			p.getInventory().addItem(MerchantMechanics.t3_s_pot);
			p.getInventory().addItem(MerchantMechanics.t4_s_pot);
			p.getInventory().addItem(MerchantMechanics.t5_s_pot);
		}
		else{
			ItemStack custom_i = ItemGenerators.customGenerator(args[0]);
			if(custom_i != null){
				p.getInventory().addItem(custom_i);
			}
		}
		//addCustomItem(p, Material.STONE_SWORD, white.toString() + "Flaming Sword", red.toString() + "DMG: 1~5" + "," + red.toString() + "FIRE DMG: +1");
		return true;
	}
	
}