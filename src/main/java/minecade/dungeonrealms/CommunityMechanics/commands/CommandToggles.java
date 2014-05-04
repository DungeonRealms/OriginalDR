package minecade.dungeonrealms.CommunityMechanics.commands;

import minecade.dungeonrealms.CommunityMechanics.CommunityMechanics;
import minecade.dungeonrealms.ItemMechanics.ItemMechanics;
import minecade.dungeonrealms.managers.PlayerManager;

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
		
		int toggle_count = 17;
		Inventory toggle_menu = Bukkit.createInventory(null, 18, "Toggle Menu");
		ItemStack divider = ItemMechanics.signCustomItem(Material.THIN_GLASS, (short) 0, " ", "");
		
		if(PlayerManager.getPlayerModel(p).getToggleList() == null || PlayerManager.getPlayerModel(p).getToggleList().size() == 0){
			// No toggles, show all red.
			int x = -1;
			while(x < (toggle_count - 1)) {
				x++;
				String toggle = CommunityMechanics.toggle_map.get(x);
				toggle_menu.setItem(x, CommunityMechanics.generateToggleButton(toggle, false));
			}
		} else {
			// Some toggles.
			int x = -1;
			while(x < (toggle_count - 1)) {
				x++;
				String toggle = CommunityMechanics.toggle_map.get(x);
				if(toggle.equalsIgnoreCase("toggletradechat")) {
					toggle_menu.setItem(x, CommunityMechanics.generateToggleButton(toggle, PlayerManager.getPlayerModel(p).getToggleList().contains("tchat")));
				} else {
					toggle_menu.setItem(x, CommunityMechanics.generateToggleButton(toggle, PlayerManager.getPlayerModel(p).getToggleList().contains(toggle.replaceAll("toggle", ""))));
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
