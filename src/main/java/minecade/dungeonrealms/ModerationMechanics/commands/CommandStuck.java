package minecade.dungeonrealms.ModerationMechanics.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandStuck implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player p = null;
		if(sender instanceof Player) {
			p = (Player) sender;
		}
		
		/*if(args.length != 0){
			if(p != null){
				p.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Invalid Syntax. " + ChatColor.RED + "/stuck");
			}
			return true;
		}

		Location p_loc = p.getLocation();

		if(!p_loc.getWorld().getName().equalsIgnoreCase(Bukkit.getWorlds().get(0).getName())){
			p.sendMessage(ChatColor.RED + "You cannot use " + ChatColor.BOLD + "/stuck" + ChatColor.RED + " in a player owned realm.");
			return true;
		}*/
		
		p.sendMessage(ChatColor.RED + "This command has been " + ChatColor.BOLD + "DISABLED" + ChatColor.RED + " due to abuse.");
		p.sendMessage(ChatColor.GRAY + "If you are still in need of assistance, please contact a GM ingame, on the forums or teamspeak; or, submit a /report and " + ChatColor.UNDERLINE + "be sure to include your coordinates.");
		
		/*if(!HealthMechanics.in_combat.containsKey(p.getName())){
			if(used_stuck.contains(p.getName())){
				p.sendMessage(ChatColor.RED + "You have already used " + ChatColor.BOLD + "/stuck" + ChatColor.RED + " in this session.");
				p.sendMessage(ChatColor.GRAY + "If you are still in need of assistance, please contact a GM on the forums or teamspeak; or, submit a /report.");
				return true;
			}

			used_stuck.add(p.getName());

			if(last_unstuck.containsKey(p.getName())){
				long last_time = last_unstuck.get(p.getName());
				if((System.currentTimeMillis() - last_time) <= 360 * 1000){
					int difference = Math.round(((360 * 1000) - (System.currentTimeMillis() - last_time)) / 1000); 
					p.sendMessage(ChatColor.RED + "You cannot use " + ChatColor.BOLD + "/stuck" + ChatColor.RED + ". You may use it again in " + ChatColor.BOLD + difference + "s...");
					return true;
				}
			}
			particle_effects.put(p.getName(), 0);
			p.setVelocity(new Vector(0,1.0F,0));
			last_unstuck.put(p.getName(), System.currentTimeMillis());
			p.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "* UNSTUCK! *");
			return true;
			/*if(KarmaMechanics.getRawAlignment(p.getName()).equalsIgnoreCase("evil")){
					p.teleport(new Location(Bukkit.getWorlds().get(0), -414, 62, 620));
					p.sendMessage(ChatColor.GREEN + "* UNSTUCK! *");
					return true;
				}
				p.teleport(SpawnMechanics.getRandomSpawnPoint());
				p.sendMessage(ChatColor.GREEN + "* UNSTUCK! *");
				return true;
		}*/
		
		return true;
	}
	
}
