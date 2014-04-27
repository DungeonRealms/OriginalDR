package minecade.dungeonrealms.ProfessionMechanics.commands;

import minecade.dungeonrealms.ProfessionMechanics.ProfessionMechanics;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandShowFish implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player p = (Player) sender;
		
		if(!(p.isOp())) { return true; }
		
		if(args.length != 1) {
			p.sendMessage("/showfish <radius>");
			return true;
		}
		
		int radius = Integer.parseInt(args[0]);
		Location loc = p.getLocation();
		World w = loc.getWorld();
		int i, j, k;
		int x = (int) loc.getX();
		int y = (int) loc.getY();
		int z = (int) loc.getZ();
		
		for(i = -radius; i <= radius; i++) {
			for(j = -radius; j <= radius; j++) {
				for(k = -radius; k <= radius; k++) {
					loc = w.getBlockAt(x + i, y + j, z + k).getLocation();
					if(ProfessionMechanics.fishing_location.containsKey(loc)) {
						loc.getBlock().setType(Material.WATER_LILY);
					}
				}
			}
		}
		return true;
	}
	
}