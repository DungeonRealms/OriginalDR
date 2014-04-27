package minecade.dungeonrealms.Hive.commands;

import minecade.dungeonrealms.Main;
import minecade.dungeonrealms.DuelMechanics.DuelMechanics;
import minecade.dungeonrealms.HealthMechanics.HealthMechanics;
import minecade.dungeonrealms.Hive.Hive;
import minecade.dungeonrealms.InstanceMechanics.InstanceMechanics;
import minecade.dungeonrealms.LootMechanics.LootMechanics;
import minecade.dungeonrealms.MountMechanics.MountMechanics;
import minecade.dungeonrealms.TutorialMechanics.TutorialMechanics;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandShard implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		final Player p = (Player) sender;
		if(Hive.no_shard) {
			p.sendMessage(ChatColor.RED + "This feature is " + ChatColor.UNDERLINE + "temporarily" + ChatColor.RED + " disabled while we troubleshoot.");
			return true;
		}
		if(TutorialMechanics.onTutorialIsland(p)) {
			p.sendMessage(ChatColor.RED + "You " + ChatColor.UNDERLINE + "cannot" + ChatColor.RED + " change game shards while on Tutorial Island.");
			return true;
		}
		if(HealthMechanics.in_combat.containsKey(p.getName())) {
			double seconds_left = 0;
			long dif = ((HealthMechanics.HealthRegenCombatDelay * 1000) + HealthMechanics.in_combat.get(p.getName())) - System.currentTimeMillis();
			seconds_left = (dif / 1000.0D) + 0.5D;
			seconds_left = Math.round(seconds_left);
			
			p.sendMessage(ChatColor.RED + "You " + ChatColor.UNDERLINE + "cannot" + ChatColor.RED + " change shards while in combat.");
			p.sendMessage(ChatColor.GRAY + "Try again in about " + seconds_left + ChatColor.BOLD + "s");
			return true;
		}
		if(!DuelMechanics.isDamageDisabled(p.getLocation()) && LootMechanics.isMonsterNearPlayer(p, 16) && !p.isOp()) {
			p.sendMessage(ChatColor.RED + "You " + ChatColor.UNDERLINE + "cannot" + ChatColor.RED + " change shards with hostile monsters nearby.");
			p.sendMessage(ChatColor.GRAY + "Eliminate all monsters in a 16x16 area and try again.");
			return true;
		}
		if((Hive.seconds_to_reboot <= 10 && Hive.restart_inc) || Hive.server_lock || Hive.local_ddos || Hive.hive_ddos) {
			p.sendMessage(ChatColor.RED + "You " + ChatColor.UNDERLINE + "cannot" + ChatColor.RED + " change shards at the moment.");
			p.sendMessage(ChatColor.GRAY + "The servers are likely preparing to reboot or enter maintenance mode.");
			return true;
		}
		if(Hive.server_swap.containsKey(p.getName()) || Hive.server_swap_pending.containsKey(p.getName())) {
			p.sendMessage(ChatColor.RED + "You " + ChatColor.UNDERLINE + "cannot" + ChatColor.RED + " change shards while you have another shard request pending.");
			return true;
		}
		if(DuelMechanics.duel_map.containsKey(p.getName())) {
			p.sendMessage(ChatColor.RED + "You " + ChatColor.UNDERLINE + "cannot" + ChatColor.RED + " change shards while in a duel.");
			return true;
		}
		if(InstanceMechanics.isInstance(p.getWorld().getName())) {
			p.sendMessage(ChatColor.RED + "You " + ChatColor.UNDERLINE + "cannot" + ChatColor.RED + " change shards while in an instance.");
			return true;
		}
		if(!(p.getWorld().getName().equalsIgnoreCase(Hive.main_world_name))) {
			p.sendMessage(ChatColor.RED + "You " + ChatColor.UNDERLINE + "cannot" + ChatColor.RED + " change shards while in a realm.");
			return true;
		}
		if(MountMechanics.mount_map.containsKey(p.getName())) {
			p.sendMessage(ChatColor.RED + "You " + ChatColor.UNDERLINE + "cannot" + ChatColor.RED + " change shards while on a mount.");
			return true;
		}
		if(!p.isOp() && !DuelMechanics.isDamageDisabled(p.getLocation()) && Hive.login_time.containsKey(p.getName()) && (((System.currentTimeMillis() - Hive.login_time.get(p.getName())) / 1000.0D) <= 300)) {
			int seconds_left = 300 - ((int) ((System.currentTimeMillis() - Hive.login_time.get(p.getName())) / 1000.0D));
			p.sendMessage(ChatColor.RED + "You " + ChatColor.UNDERLINE + "cannot" + ChatColor.RED + " change shards while in a wilderness / chaotic zone for another " + ChatColor.UNDERLINE + seconds_left + ChatColor.BOLD + "s");
			p.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "This delay is to prevent resource, monster, and treasure farming abuse.");
			return true;
		}
		
		Hive.server_swap_pending.put(p.getName(), ""); // Prevent packet abuse, put a map here immediatly.
		Main.plugin.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
			public void run() {
				// Delay the openning of the inventory to prevent packet abuse.
				p.openInventory(Hive.getShardInventory());
			}
		}, 2L);
		return true;
	}
	
}
