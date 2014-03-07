package me.vaqxine.ModerationMechanics.commands;

import me.vaqxine.MoneyMechanics.MoneyMechanics;

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
		if(sender instanceof Player){
			p = (Player)sender;
		}
		
		if(p != null){
			if(!(p.isOp())){
				return true;
			}
		}

		if(args.length <= 0){
			if(p != null){
				p.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Invalid Syntax. " + ChatColor.RED + "/banksee <PLAYER>");
			}
			return true;
		}

		String p_name = args[0];
		Inventory inv = Bukkit.createInventory(null, 54, "CLONE OF " + p_name);
		Inventory inv_clone = null;
		if(Bukkit.getPlayer(p_name) != null){
			p_name = Bukkit.getPlayer(p_name).getName();
		}
		if(MoneyMechanics.bank_contents.containsKey(p_name)){
			// Data is already locally downloaded, we're in luck.
			inv_clone = MoneyMechanics.bank_contents.get(p_name).get(0);
			for(ItemStack is : inv_clone){
				if(is == null || is.getType() == Material.AIR){
					continue;
				}
				inv.addItem(is);
			}
		}
		else{
			p.sendMessage(ChatColor.RED + "The player " + p_name + "'s bank data is not loaded, and therfore cannot be displayed.");
			p.sendMessage(ChatColor.GRAY + "In a later update, I will make it possible to view offline bank data.");
			return true;
		}

		if(inv != null){
			p.openInventory(inv);
			p.sendMessage(ChatColor.GREEN + "Displaying the current bank contents of " + p_name);
		}
		
		return true;
	}

}
