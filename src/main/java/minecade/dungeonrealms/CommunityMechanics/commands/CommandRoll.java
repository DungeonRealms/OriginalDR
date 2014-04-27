package minecade.dungeonrealms.CommunityMechanics.commands;

import java.util.Random;

import minecade.dungeonrealms.ChatMechanics.ChatMechanics;
import minecade.dungeonrealms.CommunityMechanics.CommunityMechanics;
import minecade.dungeonrealms.managers.PlayerManager;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class CommandRoll implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		final Player p = (Player) sender;
		
		if(cmd.getName().equalsIgnoreCase("crypt")) {
			if(p != null) {
				if(!(p.isOp())) { return true; }
			}
			
			return true;
		}
		
		if(args.length != 1) {
			p.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Incorrect Syntax. " + ChatColor.GRAY + "/roll <1-10000>");
			return true;
		}
		
		if(args[0].length() > 5) {
			p.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Incorrect Syntax. " + ChatColor.GRAY + "/roll <1-10000>");
			return true;
		}
		
		String number = args[0];
		Random dice = new Random();
		
		if(number.contains("-")) {
			p.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Non-Positive Max Number. " + ChatColor.RED + "/roll <1-10000>");
			return true;
		}
		
		if(PlayerManager.getPlayerModel(p).getRollDelay() > 0){
			long last_roll = PlayerManager.getPlayerModel(p).getRollDelay();
			if((System.currentTimeMillis() - last_roll) <= 1000) {
				// Less than a second since last roll, stop spamming.
				return true;
			}
		}
		PlayerManager.getPlayerModel(p).setRollDelay(System.currentTimeMillis());
		
		int roll;
		try {
			roll = dice.nextInt(Integer.parseInt(number) + 1);
		} catch(NumberFormatException nfe) {
			p.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Non-Numeric Max Number. " + ChatColor.RED + "/roll <1-10000>");
			return true;
		}
		Location loc = p.getLocation();
		
		for(Entity ent : p.getNearbyEntities(20, 20, 20)) {
			if(!(ent instanceof Player)) {
				continue;
			}
			Player player = (Player) ent;
			if(player.getLocation().distanceSquared(loc) < 400 && !(CommunityMechanics.isPlayerOnIgnoreList(player, p.getName()))) {
				player.sendMessage(ChatColor.BOLD + "" + ChatColor.GRAY + ChatMechanics.getPlayerPrefix(p.getName(), true) + ChatMechanics.getPlayerColor(p, player) + p.getName() + ChatColor.GRAY + " has rolled a " + ChatColor.UNDERLINE + ChatColor.BOLD + roll + ChatColor.GRAY + " out of " + ChatColor.UNDERLINE + ChatColor.BOLD + args[0] + ".");
			}
		}
		
		p.sendMessage(ChatColor.BOLD + "" + ChatColor.GRAY + ChatMechanics.getPlayerPrefix(p.getName(), true) + ChatMechanics.getPlayerColor(p, p) + p.getName() + ChatColor.GRAY + " has rolled a " + ChatColor.UNDERLINE + ChatColor.BOLD + roll + ChatColor.GRAY + " out of " + ChatColor.UNDERLINE + ChatColor.BOLD + args[0] + ".");
		return true;
	}
	
}