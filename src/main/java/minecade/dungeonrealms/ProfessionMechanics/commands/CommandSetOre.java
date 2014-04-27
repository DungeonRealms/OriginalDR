package minecade.dungeonrealms.ProfessionMechanics.commands;

import minecade.dungeonrealms.ProfessionMechanics.ProfessionMechanics;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CommandSetOre implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player p = (Player) sender;
		
		if(!(p.isOp())) { return true; }
		
		if(args.length != 1) {
			p.sendMessage("/setore [none, t1, t2, t3, t4, t5]");
			p.sendMessage("t1 = coal ore");
			p.sendMessage("t2 = emerald ore");
			p.sendMessage("t3 = iron ore");
			p.sendMessage("t4 = diamond ore");
			p.sendMessage("t5 = gold ore");
			return true;
			
		}
		
		String ore_type = args[0];
		
		if(ore_type.equalsIgnoreCase("none")) {
			ProfessionMechanics.ore_place.remove(p.getName());
			p.sendMessage(ChatColor.RED + "Your block placements will no longer be recorded as ore locations.");
			return true;
		}
		
		ProfessionMechanics.ore_place.put(p.getName(), ore_type);
		p.setItemInHand(new ItemStack(Material.STONE, -1));
		p.setGameMode(GameMode.CREATIVE);
		p.sendMessage(ChatColor.GOLD + "Now placing: '" + ore_type + "' ore.");
		p.sendMessage(ChatColor.GRAY + "The game will record every location where you place these stone blocks as a spawn point for this ore. Type '/setore none' to stop.");
		return true;
	}
	
}