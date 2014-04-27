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

public class CommandSetFish implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player p = (Player) sender;
		
		if(!(p.isOp())) { return true; }
		
		if(args.length != 1) {
			p.sendMessage("/setfish [none, t1, t2, t3, t4, t5]");
			return true;
		}
		
		String fish_type = args[0];
		
		if(fish_type.equalsIgnoreCase("none")) {
			ProfessionMechanics.fishing_place.remove(p.getName());
			p.sendMessage(ChatColor.RED + "Your block placements will no longer be recorded as fishing locations.");
			return true;
		}
		
		ProfessionMechanics.fishing_place.put(p.getName(), fish_type);
		p.setItemInHand(new ItemStack(Material.WATER_LILY, -1));
		p.setGameMode(GameMode.CREATIVE);
		p.sendMessage(ChatColor.GOLD + "Now placing: '" + fish_type + "' fishing spots.");
		p.sendMessage(ChatColor.GRAY + "The game will record every location where you place these lilypads as a fishing spot for the tier. Type '/setfish none' to stop.");
		p.sendMessage(ChatColor.RED + "Please also note that any water spot within 10 blocks of a placed fishing spot will inherent the spot's properties.");
		return true;
	}
	
}