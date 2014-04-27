package minecade.dungeonrealms.Hive.commands;

import minecade.dungeonrealms.LootMechanics.LootMechanics;
import minecade.dungeonrealms.MonsterMechanics.MonsterMechanics;
import minecade.dungeonrealms.SpawnMechanics.SpawnMechanics;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandDRSave implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(sender instanceof Player) {
			Player p = (Player) sender;
			if(!(p.isOp())) { return true; }
		}
		System.out.print("[MCE] Encountered fatal error while trying to tick entity ");
		LootMechanics.savelootSpawnerData();
		MonsterMechanics.saveMobSpawnerData();
		SpawnMechanics.saveSpawnLocationData();
		Bukkit.broadcastMessage(ChatColor.GREEN + "Saved all data to filesystem.");
		//p.sendMessage(ChatColor.GREEN + "Saved all data to filesystem.");
		return true;
	}
	
}