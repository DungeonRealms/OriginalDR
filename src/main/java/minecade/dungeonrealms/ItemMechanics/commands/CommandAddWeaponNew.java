package minecade.dungeonrealms.ItemMechanics.commands;

import minecade.dungeonrealms.enums.ItemRarity;
import minecade.dungeonrealms.enums.ItemTier;
import minecade.dungeonrealms.enums.ItemType;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CommandAddWeaponNew implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) return true;
		Player p = (Player) sender;

		if (!p.isOp()) return true;

		if(args.length < 1) {
			p.sendMessage(ChatColor.RED + "Wrong usage: /addweaponnew <tier> [type] [rarity]");
			p.sendMessage(ChatColor.GREEN + "Item Tiers: " + ChatColor.DARK_GREEN + "T1, T2, T3, T4, T5");
			p.sendMessage(ChatColor.GREEN + "Item Types: " + ChatColor.DARK_GREEN + "STAFF, AXE, SWORD, POLEARM, BOW, HELMET, CHESTPLATE, LEGGINGS, BOOTS");
			p.sendMessage(ChatColor.GREEN + "Item Rarity: " + ChatColor.DARK_GREEN + "COMMON, UNCOMMON, RARE, UNIQUE");
			return true;
		}

		ItemTier tier = null;
		ItemType type = null;
		ItemRarity rarity = null;

		if(args.length >= 1){
			String s = args[0].toUpperCase();
			if(!s.equals("T1") && !s.equals("T2") && !s.equals("T3") && !s.equals("T4") && !s.equals("T5")) {
				p.sendMessage("No such tier: " + s);
				return true;
			}

			tier = ItemTier.valueOf(s);
		}

		if(args.length >= 2){
			String s = args[1].toUpperCase();
			if(!s.equals("STAFF") && !s.equals("AXE") && !s.equals("SWORD") && !s.equals("POLEARM") && !s.equals("BOW") && !s.equals("HELMET") && !s.equals("CHESTPLATE") && !s.equals("LEGGINGS") && !s.equals("BOOTS")){
				p.sendMessage("No such item: " + s);
				return true;
			}

			type = ItemType.valueOf(s);
		}

		if(args.length >= 3){
			String s = args[2].toUpperCase();
			if(!s.equals("COMMON") && !s.equals("UNCOMMON") && !s.equals("RARE") && !s.equals("UNIQUE")){
				p.sendMessage("No such rarity: " + s);
				return true;
			}

			rarity = ItemRarity.valueOf(s);
		}


		ItemStack item = null;
		if(ItemType.isArmor(type)){
			item = type.generateArmor(tier, null, -1, rarity);
		}else{
			item = type.generateWeapon(tier, null, rarity);
		}

		p.getInventory().addItem(item);

		return true;
	}
}
