package minecade.dungeonrealms.ModerationMechanics.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import minecade.dungeonrealms.HealthMechanics.HealthMechanics;
import minecade.dungeonrealms.ModerationMechanics.ModerationMechanics;
import minecade.dungeonrealms.RealmMechanics.RealmMechanics;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandAllowFight implements CommandExecutor {

	private Map<String, Integer> user_health = new HashMap<>();
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player p = null;

		if (sender instanceof Player) {
			p = (Player) sender;
		}

		if (p != null) {
			if (!p.isOp()) return true;
		}

		if (args.length > 0) {
			if (p != null) {
				p.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Invalid Syntax. " + ChatColor.RED + "/allowfight");
			}
		return true;
		}

		if (p != null) {
			boolean isGod;
			if (!ModerationMechanics.allowsFight.contains(p.getName())) {
				isGod = RealmMechanics.player_god_mode.containsKey(p.getName());
				ModerationMechanics.allowsFight.add(p.getName());
				user_health.put(p.getName(), HealthMechanics.health_data.get(p.getName()));
				HealthMechanics.health_data.put(p.getName(), 50);
				HealthMechanics.setPlayerHP(p.getName(), 50);
				if (isGod) RealmMechanics.player_god_mode.remove(p.getName());
				sender.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "You toggled on fight mode.");
			} else {
				ModerationMechanics.allowsFight.remove(p.getName());
				HealthMechanics.health_data.put(p.getName(), user_health.get(p.getName()));
				user_health.remove(p.getName());
				HealthMechanics.generateMaxHP(p);
				sender.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "You toggled off fight mode");
			}
		}
		return true;
	}
}
