package me.vaqxine.MoneyMechanics.commands;

import me.vaqxine.Main;
import me.vaqxine.Hive.Hive;
import me.vaqxine.MoneyMechanics.MoneyMechanics;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_7_R2.inventory.CraftItemStack;
import org.bukkit.entity.Player;

public class CommandMNote implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player p = (Player) sender;
		if(!(Main.isDev(p.getName()))) { return true; }
		
		if(args.length == 0) {
			p.sendMessage(ChatColor.YELLOW + "USAGE: /mnote create <value (in Gems)>");
			return true;
		}
		if(args[0].equalsIgnoreCase("pouch")) {
			p.getInventory().addItem(CraftItemStack.asCraftCopy(MoneyMechanics.t1_gem_pouch));
			p.getInventory().addItem(CraftItemStack.asCraftCopy(MoneyMechanics.t2_gem_pouch));
			p.getInventory().addItem(CraftItemStack.asCraftCopy(MoneyMechanics.t3_gem_pouch));
			p.getInventory().addItem(CraftItemStack.asCraftCopy(MoneyMechanics.t4_gem_pouch));
			//p.getInventory().addItem(CraftItemStack.asCraftCopy(t1_gem_pouch));
		}
		if(args[0].equalsIgnoreCase("upload")) {
			MoneyMechanics.uploadBankDatabaseData(p.getName(), false);
			p.sendMessage("All GEMS data uploaded to SQL.");
			return true;
		}
		if(args[0].equalsIgnoreCase("download")) {
			MoneyMechanics.downloadBankDatabaseData(p.getName());
			p.sendMessage("All GEMS data downloaded and loaded into memory.");
			return true;
		}
		if(args[0].equalsIgnoreCase("create")) {
			if(!(args.length == 2)) {
				p.sendMessage(ChatColor.YELLOW + "USAGE: /mnote create <value (in Gems)>");
				return true;
			}
			try {
				Integer.parseInt(args[1]);
			} catch(NumberFormatException e) {
				p.sendMessage(ChatColor.RED + "Invalid Gems amount specified. Must be a whole number.");
				return true;
			}
			int amount = Integer.parseInt(args[1]);
			if(amount <= 0) {
				p.sendMessage(ChatColor.RED + "Please specify a positive integer above 0.");
				return true;
			}
			if(amount > 999999) {
				p.sendMessage(ChatColor.RED + "You cannot store more than 999,999 Gems in one bank note.");
				return true;
			}
			
			if(!Hive.isHiveOnline()) {
				p.sendMessage(ChatColor.RED + "This server is currently desynced from the HIVE, this action cannot be completed at this time.");
				return true;
			}
			
			MoneyMechanics.addMoneyCert(p, amount, true);
		} else {
			p.sendMessage(ChatColor.YELLOW + "USAGE: /mnote create <value (in Gems)>");
		}
		return true;
	}
	
}
