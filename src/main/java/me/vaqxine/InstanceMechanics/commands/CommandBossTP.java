package me.vaqxine.InstanceMechanics.commands;

import me.vaqxine.BossMechanics.BossMechanics;
import me.vaqxine.InstanceMechanics.InstanceMechanics;
import me.vaqxine.MonsterMechanics.MonsterMechanics;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class CommandBossTP implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		// /bosstp X Y Z
		// Checks to make sure enough % of mobs are dead then TP's players to boss room.
		if(args.length != 3) { return true; }
		if(!(sender instanceof BlockCommandSender)) { return true; }
		
		BlockCommandSender cb = (BlockCommandSender) sender;
		if(!InstanceMechanics.isInstance(cb.getBlock().getWorld().getName())) { return true; }
		
		if(InstanceMechanics.teleport_on_complete.containsKey(cb.getBlock().getWorld().getName())) { return true; // Don't spawn anymore, they're done.
		}
		
		double x = Double.parseDouble(args[0]);
		double y = Double.parseDouble(args[1]);
		double z = Double.parseDouble(args[2]);
		
		String instance_name = cb.getBlock().getWorld().getName();
		if(instance_name.contains(".")) {
			instance_name = instance_name.substring(0, instance_name.indexOf("."));
		}
		
		int total_mobs = 0;
		if(InstanceMechanics.total_mobs.containsKey(cb.getBlock().getWorld().getName())) {
			total_mobs = InstanceMechanics.total_mobs.get(cb.getBlock().getWorld().getName());
		}
		
		int alive_mobs = 0;
		for(LivingEntity le : cb.getBlock().getWorld().getLivingEntities()) {
			if(MonsterMechanics.mob_health.containsKey((Entity) le)) {
				alive_mobs++;
			}
		}
		//0 alive and 106 total so (106 - 0) -> 106 / 106 -> 1 * 100 -> 100 - 100
		double percent_alive = 100 - (((double) (total_mobs - alive_mobs) / ((double) total_mobs)) * 100.0D);
		
		if(percent_alive >= 25.0D && !instance_name.equalsIgnoreCase("OneWolfeDungeon")) {
			// Too many monsters still alive, no TP.
		    //150 -> 150 - (Percent alive = 50% -> 25% -> 25% / 100 -> .25 * 150 -> 37.5) -> 150 - 37.5 -> 112.5
			int mobs_to_kill = (int) (Math.round(((percent_alive - 25.0D) / 100.0D) * (double) total_mobs));
			if(mobs_to_kill > 0) {
				for(Player pl : cb.getBlock().getWorld().getPlayers()) {
					pl.sendMessage(ChatColor.RED + "You " + ChatColor.UNDERLINE + "cannot" + ChatColor.RED + " move on to the boss room of this instance until you kill at least " + ChatColor.BOLD + mobs_to_kill + ChatColor.RED + " more monsters.");
				}
				return true;
			}
		}
		
		if(InstanceMechanics.mob_kill_count.containsKey(cb.getBlock().getWorld().getName())) {
			int mobs_killed = InstanceMechanics.mob_kill_count.get(cb.getBlock().getWorld().getName());
			if((mobs_killed * 1.90) < total_mobs) {
				// They killed less than 75% of the mobs in the dungeon, yet they're somehow despawned. BUG ABUSEEEE.
				int mobs_to_kill = (int) (total_mobs - (Math.round(((mobs_killed * 1.90) / 100.0D) * (double) total_mobs)));
				
				for(Player pl : cb.getBlock().getWorld().getPlayers()) {
					pl.sendMessage(ChatColor.RED + "You " + ChatColor.UNDERLINE + "cannot" + ChatColor.RED + " move on to the boss room of this instance until you kill at least " + ChatColor.BOLD + mobs_to_kill + ChatColor.RED + " more monsters.");
					pl.sendMessage(ChatColor.GRAY + "The system has also detected that some mobs were forcibly despawned rather than slain. If this is the case, you will need to reset the instance.");
				}
				return true;
			}
		}
		
		for(final Player pl : cb.getBlock().getWorld().getPlayers()) {
			pl.teleport(new Location(pl.getWorld(), x, y + 2, z));
			pl.setFallDistance(0.0F);
			
			/*if(pl.getWorld().getName().contains("fireydungeon")){
				plugin_instance.getServer().getScheduler().scheduleSyncDelayedTask(plugin_instance, new Runnable() {
					public void run() {
						setPlayerEnvironment(pl, Environment.NETHER);
						pl.setFallDistance(0.0F);
					}
				}, 10L);
			}*/
		}
		
		if(cb.getBlock().getWorld().getName().contains("DODungeon")) {
			Location loc = new Location(cb.getBlock().getWorld(), -364, 60, -1.2);
			Entity boss = MonsterMechanics.spawnBossMob(loc, EntityType.SKELETON, "wither", "Burick The Fanatic");
			BossMechanics.boss_map.put(boss, "unholy_priest");
			for(Player pl : boss.getWorld().getPlayers()) {
				pl.sendMessage(ChatColor.GOLD + "" + ChatColor.UNDERLINE + "Burick The Fanatic: " + ChatColor.WHITE + "Ahahaha! You dare try to kill ME?! I am Burick, disciple of Goragath! None of you will leave this place alive!");
			}
			boss.getWorld().playSound(boss.getLocation(), Sound.ENDERDRAGON_HIT, 4F, 0.5F);
		}
		
		if(cb.getBlock().getWorld().getName().contains("T1Dungeon")) {
			Location loc = new Location(cb.getBlock().getWorld(), 529, 55, -313);
			Entity boss = MonsterMechanics.spawnBossMob(loc, EntityType.SKELETON, "bandit", "Mayel The Cruel");
			BossMechanics.boss_map.put(boss, "bandit_leader");
			for(Player pl : boss.getWorld().getPlayers()) {
				pl.sendMessage(ChatColor.GOLD + "" + ChatColor.UNDERLINE + "Mayel The Cruel: " + ChatColor.WHITE + "How dare you challenge ME, the leader of the Cyrene Bandits! To me, my brethern, let us crush these incolents!");
			}
			boss.getWorld().playSound(boss.getLocation(), Sound.AMBIENCE_CAVE, 1F, 1F);
		}
		
		if(cb.getBlock().getWorld().getName().contains("fireydungeon")) {
			Location loc = new Location(cb.getBlock().getWorld(), -54, 158, 646);
			Entity boss = MonsterMechanics.spawnBossMob(loc, EntityType.SKELETON, "wither", "The Infernal Abyss");
			BossMechanics.boss_map.put(boss, "fire_demon");
			for(Player pl : boss.getWorld().getPlayers()) {
				pl.sendMessage(ChatColor.GOLD + "" + ChatColor.UNDERLINE + "The Infernal Abyss: " + ChatColor.WHITE + "... I have nothing to say to you foolish mortals, except for this: Burn.");
			}
			boss.getWorld().playSound(boss.getLocation(), Sound.AMBIENCE_THUNDER, 1F, 1F);
		}
		
		if(cb.getBlock().getWorld().getName().contains("OneWolfeDungeon")) {
		    //TODO: SET THE LOCATION
			Location loc = new Location(cb.getBlock().getWorld(), -71, 176, 18); // TODO: onewolf - spawn his wolf pet as well
			Entity boss = MonsterMechanics.spawnBossMob(loc, EntityType.ZOMBIE, "goblin", "Aceron The Wicked");
			BossMechanics.boss_map.put(boss, "aceron");
			for(Player pl : boss.getWorld().getPlayers()) {
				pl.sendMessage(ChatColor.GOLD + "" + ChatColor.UNDERLINE + "Aceron The Wicked: " + ChatColor.WHITE + "I hope you find what you are looking for, because you wont be leaving here with it!");
			}
			boss.getWorld().playSound(boss.getLocation(), Sound.GHAST_MOAN, 1F, 0.25F);
		}
		return true;
	}
	
}