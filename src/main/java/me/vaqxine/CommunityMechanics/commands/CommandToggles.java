package me.vaqxine.CommunityMechanics.commands;

import me.vaqxine.CommunityMechanics.CommunityMechanics;
import me.vaqxine.ItemMechanics.ItemMechanics;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class CommandToggles implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		final Player p = (Player) sender;
		
		if(cmd.getName().equalsIgnoreCase("crypt")) {
			if(p != null) {
				if(!(p.isOp())) { return true; }
			}
			
			return true;
		}
		
		if(!(args.length == 0)) {
			p.sendMessage(ChatColor.RED + "Invalid Command.");
			p.sendMessage(ChatColor.GRAY + "Usage: /toggles");
			p.sendMessage(ChatColor.GRAY + "Description: Displays currently active toggles.");
			return true;
		}
		
		int toggle_count = 16;
		Inventory toggle_menu = Bukkit.createInventory(null, 18, "Toggle Menu");
		ItemStack divider = ItemMechanics.signCustomItem(Material.THIN_GLASS, (short) 0, " ", "");
		
		if(!(CommunityMechanics.toggle_list.containsKey(p.getName()))) {
			// No toggles, show all red.
			int x = -1;
			while(x < (toggle_count - 1)) {
				x++;
				String toggle = CommunityMechanics.toggle_map.get(x);
				toggle_menu.setItem(x, CommunityMechanics.generateToggleButton(toggle, false));
			}
		} else if(CommunityMechanics.toggle_list.containsKey(p.getName())) {
			// Some toggles.
			int x = -1;
			while(x < (toggle_count - 1)) {
				x++;
				String toggle = CommunityMechanics.toggle_map.get(x);
				if(toggle.equalsIgnoreCase("toggletradechat")) {
					toggle_menu.setItem(x, CommunityMechanics.generateToggleButton(toggle, CommunityMechanics.toggle_list.get(p.getName()).contains("tchat")));
				} else {
					toggle_menu.setItem(x, CommunityMechanics.generateToggleButton(toggle, CommunityMechanics.toggle_list.get(p.getName()).contains(toggle.replaceAll("toggle", ""))));
				}
				
			}
		}
		
		int x = -1;
		while(x < (toggle_menu.getSize() - 1)) {
			x++;
			if(toggle_menu.getItem(x) == null || toggle_menu.getItem(x).getType() == Material.AIR) {
				toggle_menu.setItem(x, divider);
			}
		}
		
		p.openInventory(toggle_menu);
		return true;
	}
	
}
