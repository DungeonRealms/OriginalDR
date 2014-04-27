package minecade.dungeonrealms.InstanceMechanics.commands;

import minecade.dungeonrealms.InstanceMechanics.InstanceMechanics;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandDRReplaceNear implements CommandExecutor {
	
	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(args.length != 3) { return true; }
		if(!(sender instanceof BlockCommandSender)) { return true; }
		
		BlockCommandSender cb = (BlockCommandSender) sender;
		
		int radius = Integer.parseInt(args[0]);
		int from_id = Integer.parseInt(args[1]);
		int to_id = Integer.parseInt(args[2]);
		
		for(Block b : InstanceMechanics.getNearbyBlocks(cb.getBlock().getLocation(), radius)) {
			if(b.getTypeId() == from_id) {
				b.setType(Material.getMaterial(to_id));
			}
		}
		return true;
	}
	
}