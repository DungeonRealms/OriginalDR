package me.vaqxine.ModerationMechanics.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_7_R2.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class CommandArmorSee implements CommandExecutor {

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
				p.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Invalid Syntax. " + ChatColor.RED + "/armorsee <PLAYER>");
			}
			return true;
		}

		String p_name = args[0];
		Inventory inv = Bukkit.createInventory(null, 9, "ARMOR OF " + p_name);
		//Inventory inv_clone = null; TODO What is this for (Unused)
		if(Bukkit.getPlayer(p_name) != null){
			Player victim = Bukkit.getPlayer(p_name);
			for(ItemStack is : victim.getInventory().getArmorContents()){
				inv.addItem(CraftItemStack.asCraftCopy(is));
			}
		}
		else{
			p.sendMessage(ChatColor.RED + "The player " + p_name + "'s armor data is not loaded, and therfore cannot be displayed.");
			p.sendMessage(ChatColor.GRAY + "In a later update, I will make it possible to view offline armor data.");
			return true;
		}

		if(inv != null){
			p.openInventory(inv);
			p.sendMessage(ChatColor.GREEN + "Displaying the current armor contents of " + p_name);
		}
		
		return true;
	}

}
