package me.vaqxine.MonsterMechanics.commands;

import me.vaqxine.MonsterMechanics.MonsterMechanics;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandShowMS implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player p = (Player) sender;
		
		if(!(p.isOp())) { return true; }
		
		if(args.length != 1) {
			p.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Incorrect Syntax. " + ChatColor.RED + "/showms <radius>");
			return true;
		}
		
		int radius = Integer.parseInt(args[0]);
		Location loc = p.getLocation();
		World w = loc.getWorld();
		int i, j, k;
		int x = (int) loc.getX();
		int y = (int) loc.getY();
		int z = (int) loc.getZ();
		int count = 0;
		for(i = -radius; i <= radius; i++) {
			for(j = -radius; j <= radius; j++) {
				for(k = -radius; k <= radius; k++) {
					loc = w.getBlockAt(x + i, y + j, z + k).getLocation();
					if(MonsterMechanics.mob_spawns.containsKey(loc)) {
					    count++;
						loc.getBlock().setType(Material.MOB_SPAWNER);
					}
				}
			}
		}
		
		p.sendMessage(ChatColor.YELLOW + "Displaying " + count + " mob spawners in a " + radius + " block radius...");
		p.sendMessage(ChatColor.YELLOW + "Local spawning will be disabled while they are visible.");
		p.sendMessage(ChatColor.GRAY + "Break them to unregister the spawn point.");
		
		return true;
	}
	
}