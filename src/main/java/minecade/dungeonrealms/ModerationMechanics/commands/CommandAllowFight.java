package minecade.dungeonrealms.ModerationMechanics.commands;

import java.util.ArrayList;
import java.util.List;

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
                isGod = RealmMechanics.player_god_mode.containsKey(p.getName()) ? true : false;
                ModerationMechanics.allowsFight.add(p.getName());
                HealthMechanics.setPlayerHP(p.getName(), 50);
                if (isGod) RealmMechanics.player_god_mode.remove(p.getName());
                sender.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "You toggled on fight mode.");
            } else {
                ModerationMechanics.allowsFight.remove(p.getName());
                HealthMechanics.setPlayerHP(p.getName(), HealthMechanics.health_data.get(p.getName()));
                sender.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "You toggled off fight mode");
            }
        }
		return true;
	}
}
