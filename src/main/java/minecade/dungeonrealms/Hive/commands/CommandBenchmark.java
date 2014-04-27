package minecade.dungeonrealms.Hive.commands;

import minecade.dungeonrealms.Main;
import minecade.dungeonrealms.MonsterMechanics.MonsterMechanics;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandBenchmark implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(sender instanceof Player) { return true; }
		
		Main.log.info("MonsterMechanics.loaded_mobs = " + MonsterMechanics.loaded_mobs.size());
		Main.log.info("MonsterMechanics.mob_health = " + MonsterMechanics.mob_health.size());
		Main.log.info("MonsterMechanics.mob_target = " + MonsterMechanics.mob_target.size());
		Main.log.info("MonsterMechanics.player_locations = " + MonsterMechanics.player_locations.size());
		Main.log.info("MonsterMechanics.async_entity_target = " + MonsterMechanics.async_entity_target.size());
		Main.log.info("MonsterMechanics.entities_to_kill = " + MonsterMechanics.entities_to_kill.size());
		Main.log.info("MonsterMechanics.entities_to_remove = " + MonsterMechanics.entities_to_remove.size());
		Main.log.info("MonsterMechanics.mob_spawn_ownership = " + MonsterMechanics.mob_spawn_ownership.size());
		Main.log.info("MonsterMechanics.mob_last_hit = " + MonsterMechanics.mob_last_hit.size());
		Main.log.info("MonsterMechanics.mob_last_hurt = " + MonsterMechanics.mob_last_hurt.size());
		Main.log.info("MonsterMechanics.no_delay = " + MonsterMechanics.no_delay_kills.size());
		return true;
	}
	
}