package minecade.dungeonrealms.ItemMechanics.commands;

import me.vilsol.betanpc.menus.TierCommandMenu;
import me.vilsol.itemgenerator.ItemGenerator;
import me.vilsol.menuengine.engine.MenuModel;
import minecade.dungeonrealms.enums.ItemRarity;
import minecade.dungeonrealms.enums.ItemTier;
import minecade.dungeonrealms.enums.ItemType;
import org.apache.commons.lang3.StringUtils;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CommandAddWeaponNewNew implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player p = (Player) sender;

		if(!p.isOp()) return true;
		
		// show GUI
		if(args.length == 0) {
		    MenuModel.getAllMenus().get(TierCommandMenu.class).getMenu().showToPlayer(p);
		    return true;
		}

		ItemTier tier = null;
		ItemType type = null;
		ItemRarity rarity = null;
		
		if (args[0].equalsIgnoreCase("random") && StringUtils.isNumeric(args[1])) {
		    if (Integer.valueOf(args[1]) > 30) return true;
		    for (int i = 0; i < Integer.valueOf(args[1]); i++) {
		        p.getInventory().addItem(new ItemGenerator().generateItem().getItem());
		    }
		    return true;
		}

		if(args.length >= 1){
			String s = args[0].toUpperCase();
			if(!s.equals("T1") && !s.equals("T2") && !s.equals("T3") && !s.equals("T4") && !s.equals("T5")) {
				p.sendMessage("No such tier: " + s);
				listUsage(p);
	            return true;
			}

			tier = ItemTier.valueOf(s);
		}

		if(args.length >= 2){
			String s = args[1].toUpperCase();
			if(!s.equals("STAFF") && !s.equals("AXE") && !s.equals("SWORD") && !s.equals("POLEARM") && !s.equals("BOW") && !s.equals("HELMET") && !s.equals("CHESTPLATE") && !s.equals("LEGGINGS") && !s.equals("BOOTS")){
				p.sendMessage("No such item: " + s);
				listUsage(p);
				return true;
			}

			type = ItemType.valueOf(s);
		}

		if(args.length >= 3){
			String s = args[2].toUpperCase();
			if(!s.equals("COMMON") && !s.equals("UNCOMMON") && !s.equals("RARE") && !s.equals("UNIQUE")){
				p.sendMessage("No such rarity: " + s);
				listUsage(p);
				return true;
			}

			rarity = ItemRarity.valueOf(s);
		}

		ItemGenerator generator = new ItemGenerator();
		generator.setTier(tier);
		generator.setRarity(rarity);
		generator.setType(type);
		generator.generateItem();

		ItemStack item = generator.getItem();

		p.getInventory().addItem(item);

		return true;
	}
	
	private void listUsage(Player p) {
	    p.sendMessage(ChatColor.RED + "Wrong usage: /addweaponnewnew to open up gui OR /addweaponnewnew <tier> [type] [rarity]");
        p.sendMessage(ChatColor.GREEN + "Item Tiers: " + ChatColor.DARK_GREEN + "T1, T2, T3, T4, T5");
        p.sendMessage(ChatColor.GREEN + "Item Types: " + ChatColor.DARK_GREEN + "STAFF, AXE, SWORD, POLEARM, BOW, HELMET, CHESTPLATE, LEGGINGS, BOOTS");
        p.sendMessage(ChatColor.GREEN + "Item Rarity: " + ChatColor.DARK_GREEN + "COMMON, UNCOMMON, RARE, UNIQUE");
	}
	
}
