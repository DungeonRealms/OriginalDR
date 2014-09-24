package minecade.dungeonrealms.ModerationMechanics.commands;

import minecade.dungeonrealms.ModerationMechanics.ModerationMechanics;
import minecade.dungeonrealms.MoneyMechanics.MoneyMechanics;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class CommandBankSee implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player p = null;
		if(sender instanceof Player) {
			p = (Player) sender;
		}
		
		if(p != null) {
			if(!(p.isOp())) { return true; }
		}
		
		if(args.length <= 0) {
			if(p != null) {
				p.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Invalid Syntax. " + ChatColor.RED + "/banksee <PLAYER>");
			}
			return true;
		}
		
		String p_name = args[0];
		if(ModerationMechanics.looking_into_offline_bank.containsKey(p.getName())) {
			/* They already loaded a bank up. */
			if(!ModerationMechanics.looking_into_offline_bank.get(p.getName()).equalsIgnoreCase(p_name)) {
				p.sendMessage(ChatColor.RED + "You have already loaded a bank inventory.");
				p.sendMessage(ChatColor.GRAY + "Please do /unloadbanksee to unload them and try again.");
				return true;
			}
			//Otherwise they should already be loaded so let it handle the code.
		}
		Inventory inv = Bukkit.createInventory(null, 54, "CLONE OF " + p_name);
		Inventory inv_clone = null;
		if(Bukkit.getPlayer(p_name) != null) {
			p_name = Bukkit.getPlayer(p_name).getName();
		}
		if(MoneyMechanics.bank_contents.containsKey(p_name)) {
			// Data is already locally downloaded, we're in luck.
			inv_clone = MoneyMechanics.bank_contents.get(p_name).get(0);
			for(ItemStack is : inv_clone) {
				if(is == null || is.getType() == Material.AIR) {
					continue;
				}
				inv.addItem(is);
			}
		} else {
			final boolean loaded = MoneyMechanics.downloadBankDatabaseData(p_name);
			p.sendMessage(ChatColor.RED + "Loading " + p_name + "'s bank from the database.");
			p.sendMessage(ChatColor.GRAY + "Please wait..");
			if(!loaded) {
				p.sendMessage(ChatColor.RED + "There was an issue loading " + p_name + " bank.");
				return true;
			}
			inv_clone = MoneyMechanics.bank_contents.get(p_name).get(0);
			for(ItemStack is : inv_clone) {
				if(is == null || is.getType() == Material.AIR) {
					continue;
				}
				inv.addItem(is);
			}
			ModerationMechanics.looking_into_offline_bank.put(p.getName(), p_name);
			// p.sendMessage(ChatColor.RED + "The player " + p_name + "'s bank data is not loaded, and therfore cannot be displayed.");
			// p.sendMessage(ChatColor.GRAY + "In a later update, I will make it possible to view offline bank data.");
		}
		
		if(inv != null) {
			p.openInventory(inv);
			p.sendMessage(ChatColor.GREEN + "Displaying the current bank contents of " + p_name);
		}
		
		return true;
	}
	
}
