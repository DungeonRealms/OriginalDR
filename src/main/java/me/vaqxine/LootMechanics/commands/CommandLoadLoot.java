package me.vaqxine.LootMechanics.commands;

import me.vaqxine.LootMechanics.LootMechanics;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandLoadLoot implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player p = (Player)sender;
		if(!p.isOp()) return true;
		LootMechanics.loadlootSpawnTemplates();
		LootMechanics.loadGameWorldlootSpawnerData();
		p.sendMessage(ChatColor.GREEN + "Loaded all loot spawn templates into memory.");
		return true;
	}
	
}