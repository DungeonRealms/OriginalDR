package minecade.dungeonrealms.LootMechanics.commands;

import minecade.dungeonrealms.ItemMechanics.ItemMechanics;
import minecade.dungeonrealms.MonsterMechanics.MonsterMechanics;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CommandLoot implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player p = (Player) sender;
		if(!p.isOp()) return true;
		if(args.length > 0 && args[0].equalsIgnoreCase("chunk")) {
			p.getInventory().addItem(ItemMechanics.signNewCustomItem(Material.MAGMA_CREAM, (short) 0, ChatColor.LIGHT_PURPLE.toString() + "Orb of Alteration", ChatColor.GRAY.toString() + "Randomizes bonus stats of selected equipment"));
			if(MonsterMechanics.loaded_chunks.contains(p.getLocation().getChunk().getBlock(0, 0, 0).getLocation())) {
				p.sendMessage("LOADED");
			} else {
				p.sendMessage("NOT LOADED");
			}
			return true;
			//p.sendMessage(MonsterMechanics.loaded_chunks.contains(p.getLocation().getChunk().getBlock(0, 0, 0).getLocation()));
		}
		p.getInventory().addItem(new ItemStack(Material.GLOWSTONE, 10));
		p.sendMessage(ChatColor.YELLOW + "Added 10X Chest Spawn Blocks to inventory.");
		return true;
	}
	
}