package minecade.dungeonrealms.ModerationMechanics.commands;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandDRTPPos implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player p = null;
		if(sender instanceof Player) {
			p = (Player) sender;
		}
		
		if(p != null && !p.isOp()) { return true; }
		
		if(args.length != 3) {
			p.sendMessage("/drtppos X Y Z");
			return true;
		}
		
		double x = Double.parseDouble(args[0]);
		double y = Double.parseDouble(args[1]);
		double z = Double.parseDouble(args[2]);
		
		p.teleport(new Location(p.getWorld(), x, y, z));
		
		return true;
	}
	
}
