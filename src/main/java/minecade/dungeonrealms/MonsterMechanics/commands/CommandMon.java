package minecade.dungeonrealms.MonsterMechanics.commands;

import java.util.HashSet;
import java.util.Random;

import minecade.dungeonrealms.MonsterMechanics.MonsterMechanics;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CommandMon implements CommandExecutor {
	
	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player p = (Player) sender;
		
		if(!(p.isOp())) { return true; }
		
		Location l = p.getTargetBlock((HashSet<Byte>)null, 128).getLocation().add(0, 1, 0);
		
		if(args.length == 2) {
			p.getInventory().addItem(new ItemStack(Material.MOB_SPAWNER, 10));
			p.sendMessage(ChatColor.YELLOW + "Dropped it like it's hot.");
			return true;
		}
		
		if(args.length != 1) {
			p.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Incorrect Syntax. " + ChatColor.RED + "/mon <tier #>");
			return true;
		}
		
		int tier = 1;
		
		try {
			tier = Integer.parseInt(args[0]);
		} catch(NumberFormatException nfe) {
			p.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Non-Numeric Tier. " + ChatColor.RED + "/mon <tier #>");
			return true;
		}
		
		if(tier > 5 || tier <= 0) {
			p.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Invalid Tier." + ChatColor.RED + "/mon <tier #(1-5)>");
			return true;
		}
		
		MonsterMechanics.spawnTierMob(l, EntityType.SKELETON, tier, -1, p.getLocation(), false, "", "", true, new Random().nextInt(3) + 1);
		return true;
	}
	
}