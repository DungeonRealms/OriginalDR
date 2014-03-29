package me.vaqxine.PowerupMechanics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

import me.vaqxine.Main;
import me.vaqxine.HealthMechanics.HealthMechanics;
import me.vaqxine.Hive.Hive;
import me.vaqxine.Hive.ParticleEffect;
import me.vaqxine.LootMechanics.LootMechanics;
import me.vaqxine.MonsterMechanics.MonsterMechanics;
import me.vaqxine.TutorialMechanics.TutorialMechanics;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.Inventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PowerupMechanics
implements Listener
{
	static Logger log = Logger.getLogger("Minecraft");

	static HashMap<Block, String> powerup_map = new HashMap<Block, String>();

	public List<String> effect_list = new ArrayList<String>(Arrays.asList("+10% Damage", "+30% Damage", "+50% Damage", "Night Vision", "Speed I", "Speed II", "+5% Armor", "+10% Armor", "+20% Armor", "Water Breathing", "Fire Resistance", "Instant Health (100%)", "Instant Health (50%)"));
	static PowerupMechanics instance;
	public double beacon_percent = 0.075D; // 5%
	public double loot_chest_count = 0;
	public static List<Location> l_loot_spawns = new ArrayList<Location>();
	ConcurrentHashMap<Location, Inventory> spawned_loot_chests;
	public static List<Location> beacons = new ArrayList<Location>();
	public static CopyOnWriteArrayList<Entity> ender_crystals = new CopyOnWriteArrayList<Entity>();

	public void onEnable()
	{
		instance = this;
		Main.plugin.getServer().getPluginManager().registerEvents(this, Main.plugin);

		/*getServer().getScheduler().scheduleAsyncDelayedTask(this, new Runnable() {
        public void run() {
        	l_loot_spawns.clear();

           for(Location loc : LootMechanics.loot_chest_inv.keySet()){
        	   l_loot_spawns.add(loc);
           } 
           loot_chest_count = l_loot_spawns.size();
        }
      }
      , 2 * 20L);*/

		Main.plugin.getServer().getScheduler().scheduleSyncRepeatingTask(Main.plugin, new Runnable() {
			public void run() {  
				l_loot_spawns.clear();

				for(Location loc : LootMechanics.loot_chest_inv.keySet()){
					l_loot_spawns.add(loc);
				} 
				loot_chest_count = l_loot_spawns.size();
				// Get all the new locations, we don't want to edit unloaded chunks.

				if(loot_chest_count > 0){
					renewBeacons();
				}
			}
		}
		, 33 * 20L, 5 * 20L);

		log.info("[PowerupMechanics] has been enabled.");
	}

	public void onDisable() {
		cleanupBeacons();
		log.info("[PowerupMechanics] has been disabled.");
	}

	public void cleanupBeacons() {
		for (Location l : beacons) {
			l.getBlock().setType(Material.AIR);
			l.add(0.0D, 1.0D, 0.0D).getBlock().setType(Material.AIR);
			for(Entity e : ender_crystals){
				if(e.getLocation().distanceSquared(l) <= 4){
					e.remove();
					break;
				}
			}
		}

		for(Entity e : Main.plugin.getServer().getWorlds().get(0).getEntities()){
			if(e instanceof EnderCrystal){
				e.remove();
			}
		}

		ender_crystals.clear();
	}

	public void renewBeacons() {
		List<Location> keys = new ArrayList<Location>(l_loot_spawns);

		double alive_beacons = powerup_map.size();
		double beacons_to_make = (loot_chest_count * beacon_percent) - alive_beacons;
		//log.info("*****" + alive_beacons);
		//log.info("" + beacons_to_make);
		double beacons_made = 0.0D;
		try{
			int attempts = 0;
			while(beacons_made <= beacons_to_make && attempts < loot_chest_count){
				attempts++;
				int i = new Random().nextInt((int)loot_chest_count);
				Location l = keys.get(i);

				boolean chunk_loaded = false;
				for(Location ploc : MonsterMechanics.player_locations.values()){
					if(!ploc.getWorld().getName().equalsIgnoreCase(l.getWorld().getName())){continue;}
					ploc.setY(l.getY());
					double distance = ploc.distanceSquared(l);
					if(distance <= 6400){ // 80 blocks, chunk is loaded.
						chunk_loaded = true;
						break;
						// Somewhat near.
					}
				}

				if(!chunk_loaded){
					continue;
				}

				if (l.getBlock().getType() == Material.BEDROCK){
					continue;
				}

				if (l.add(0.0D, 1.0D, 0.0D).getBlock().getType() != Material.AIR) {
					continue;
				}

				if(TutorialMechanics.onTutorialIsland(l)){
					continue;
				}
				
				l.subtract(0.0D, 1.0D, 0.0D);
				l.getBlock().setType(Material.AIR);

				beacons.add(l);

				final Location final_l = l;

				Main.plugin.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
					@SuppressWarnings("deprecation")
					public void run() {

						Location location = final_l;
						location.setY(location.getY());
						Block bedrock = location.getBlock();
						bedrock.setTypeId(7);
						location = bedrock.getLocation();
						location.setX(location.getX() + 0.5D);
						location.setZ(location.getZ() + 0.5D);
						EnderCrystal enderCrystal = (EnderCrystal)location.getWorld().spawn(location, EnderCrystal.class);
						ender_crystals.add((Entity)enderCrystal);

						PowerupMechanics.powerup_map.put(location.getBlock(), "");
						/*Location new_l = final_l;
            if ((new_l.getBlock().getType() != Material.AIR) || (new_l.add(0.0D, 1.0D, 0.0D).getBlock().getType() != Material.AIR)) return;
            new_l.subtract(0.0D, 1.0D, 0.0D);
            new_l.getBlock().setType(Material.DIAMOND_BLOCK);
            new_l.add(0.0D, 1.0D, 0.0D).getBlock().setType(Material.BEACON);
            PowerupMechanics.powerup_map.put(new_l.getBlock(), "");
            new_l.subtract(0.0D, 1.0D, 0.0D);*/
					}
				}, 20L);

				beacons_made += 1.0D;
			}

		} catch(Exception err){
			err.printStackTrace(); // Wait 30s and try again.
			return;
		}

	}

	public static void handleBeaconEffect(Player p, int seconds_duration) {
		if(seconds_duration == -1)
			seconds_duration = 90;
		
		int random_effect = new Random().nextInt(instance.effect_list.size() - 1);
		String effect = (String)instance.effect_list.get(random_effect);
		String format_effect = effect;

		List<Player> effected_players = new ArrayList<Player>();
		effected_players.add(p);

		p.getWorld().playSound(p.getLocation(), Sound.ENDERDRAGON_HIT, 5F, 1.5F);

		for (Entity e : p.getNearbyEntities(8.0D, 8.0D, 8.0D)) {
			if ((e instanceof Player)) {
				Player ep = (Player)e;
				effected_players.add(ep);
			}
		}

		if (effect.contains("Damage")) {
			int percent = Integer.parseInt(effect.substring(1, effect.indexOf("%")));

			int tier = 0;
			if (percent == 10) {
				tier = 0;
			}
			if (percent == 30) {
				tier = 1;
			}
			if (percent == 50) {
				tier = 2;
			}

			for (Player pl : effected_players) {
				pl.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
				pl.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, (seconds_duration * 20), tier));
			}
		}
		int tier;
		if (effect.contains("Armor")) {
			int percent = Integer.parseInt(effect.substring(1, effect.indexOf("%")));

			tier = 0;
			if (percent == 5) {
				tier = 0;
			}
			if (percent == 10) {
				tier = 1;
			}
			if (percent == 20) {
				tier = 2;
			}

			for (Player pl : effected_players) {
				pl.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
				pl.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, (seconds_duration * 20), tier));
			}

		}

		if (effect.equalsIgnoreCase("Speed I")) {
			for (Player pl : effected_players) {
				pl.removePotionEffect(PotionEffectType.SPEED);
				pl.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, (seconds_duration * 20), 0));
			}
		}

		if (effect.equalsIgnoreCase("Speed II")) {
			for (Player pl : effected_players) {
				pl.removePotionEffect(PotionEffectType.SPEED);
				pl.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, (seconds_duration * 20), 1));
			}
		}

		if (effect.equalsIgnoreCase("Night Vision")) {
			for (Player pl : effected_players) {
				pl.removePotionEffect(PotionEffectType.NIGHT_VISION);
				pl.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, (seconds_duration * 20), 0));
			}
		}

		if (effect.equalsIgnoreCase("Invisibility")) {
			for (Player pl : effected_players) {
				pl.removePotionEffect(PotionEffectType.INVISIBILITY);
				pl.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, (seconds_duration * 20), 0));
			}
		}

		if (effect.equalsIgnoreCase("Jump Boost")) {
			for (Player pl : effected_players) {
				pl.removePotionEffect(PotionEffectType.JUMP);
				pl.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, (seconds_duration * 20), 2));
			}
		}

		if (effect.equalsIgnoreCase("Fire Resistance")) {
			for (Player pl : effected_players) {
				pl.removePotionEffect(PotionEffectType.FIRE_RESISTANCE);
				pl.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, (seconds_duration * 20), 0));
			}
		}

		if (effect.equalsIgnoreCase("Water Breathing")) {
			for (Player pl : effected_players) {
				pl.removePotionEffect(PotionEffectType.WATER_BREATHING);
				pl.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, (seconds_duration * 20), 0));
			}
		}

		if (effect.equalsIgnoreCase("Instant Health (100%)")) {
			for (Player pl : effected_players) {
				int max_hp = HealthMechanics.getMaxHealthValue(pl.getName());
				//pl.setLevel(max_hp);
				HealthMechanics.setPlayerHP(pl.getName(), max_hp);
				pl.setHealth(20);
			}
		}

		if (effect.equalsIgnoreCase("Instant Health (50%)")) {
			for (Player pl : effected_players) {
				double max_hp = HealthMechanics.getMaxHealthValue(pl.getName());
				double half_max = max_hp / 2.0D;
				double cur_hp = HealthMechanics.getPlayerHP(pl.getName());

				double health_percent = (cur_hp + half_max) / max_hp;
				double new_health_display = health_percent * 20.0D;
				if (new_health_display >= 19.5D) {
					if (health_percent >= 1.0D) {
						new_health_display = 20.0D;
					}
					else {
						new_health_display = 19.0D;
					}
				}
				if (new_health_display < 1.0D) {
					new_health_display = 1.0D;
				}

				HealthMechanics.setPlayerHP(pl.getName(), (int)(cur_hp + half_max));
				//pl.setLevel((int)(cur_hp + half_max));
				p.setHealth((int)new_health_display);
			}

		}

		for (Player pl : effected_players)
			if (!effect.contains("Instant")) {
				pl.sendMessage(ChatColor.BLUE + "" + ChatColor.BOLD + "           " + format_effect + " Buff [" + seconds_duration + "s]");
			}
			else
				pl.sendMessage(ChatColor.BLUE + "" + ChatColor.BOLD + "           " + format_effect);
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent e){
		World w = e.getBlock().getWorld();
		if(w.getName().equalsIgnoreCase(Bukkit.getWorlds().get(0).getName())){
			if(e.getBlock().getType() == Material.CHEST){
				e.getBlock().getDrops().clear();
			}
		}
	}


	// Prevent fire damage from ender crystals!
	@EventHandler(priority = EventPriority.LOW)
	public void onEntityDamage(EntityDamageEvent e){
		if(e.getCause() == DamageCause.FIRE || e.getCause() == DamageCause.FIRE_TICK){
			if(e.getEntity() instanceof Player){
				Player pl = (Player)e.getEntity();
				for(Entity ent : pl.getNearbyEntities(2, 2, 2)){
					if(ent instanceof EnderCrystal){
						e.setCancelled(true);
						e.setDamage(0);
						pl.setFireTicks(0);
						break;

					}
				}
			}
		}
	}

	@EventHandler
	public void onBlockIgniteEvent(BlockIgniteEvent e){
		//if(e.getIgnitingEntity() instanceof EnderCrystal){
		e.setCancelled(true);
		//}
	}

	@EventHandler(priority=EventPriority.LOWEST)
	public void onEnderCrystalInteract(EntityDamageEvent e) {
		if ((e.getEntity() instanceof EnderCrystal) && e.getEntity().getWorld().getName().equalsIgnoreCase(Hive.main_world_name)) {
			e.setCancelled(true);
			e.setDamage(0);

			if(e instanceof EntityDamageByEntityEvent){
				EntityDamageByEntityEvent edee = (EntityDamageByEntityEvent)e;
				if(edee.getDamager() instanceof Player){
					Player pl = (Player)edee.getDamager();
					if(TutorialMechanics.onTutorialIsland(pl)){
						return; // Do nothing.
					}
					Block b = e.getEntity().getLocation().subtract(0, 1, 0).getBlock();
					if(b.getType() == Material.BEDROCK){
						powerup_map.remove(b);
						b.setType(Material.AIR);
						beacons.remove(b.getLocation());
						LootMechanics.last_loot_spawn_tick.put(b.getLocation(), Long.valueOf(System.currentTimeMillis()));
						b.getLocation().add(0, 1, 0).getBlock().setType(Material.AIR);

						try {
							ParticleEffect.sendToLocation(ParticleEffect.MAGIC_CRIT, b.getLocation().add(0, 1, 0), new Random().nextFloat(), new Random().nextFloat(), new Random().nextFloat(), 1F, 50);
						} catch (Exception e1) {e1.printStackTrace();}
						//b.getWorld().spawnParticle(b.getLocation().add(0, 1, 0), Particle.MAGIC_CRIT, 1F, 50);

						handleBeaconEffect(pl, -1);
					}


					ender_crystals.remove(e.getEntity());
					e.getEntity().remove();
				}
			}
		}  
	}
/*
	@EventHandler
	public void onPlayerInteractEvent(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		boolean tick_set = false;

		if(!(p.getWorld().getName().equalsIgnoreCase(Bukkit.getWorlds().get(0).getName()))){
			return;
		}

		if(TutorialMechanics.onTutorialIsland(p)){
			return; // Do nothing.
		}

		if (e.getAction() == Action.LEFT_CLICK_BLOCK) {
			Block b = e.getClickedBlock();
			if ((b.getType() == Material.BEDROCK) || (b.getType() == Material.FIRE)) {
				if (b.getType() == Material.BEDROCK) {
					Block b2 = b.getLocation().add(0.0D, 1.0D, 0.0D).getBlock();
					if (b2.getType() == Material.FIRE) {
						LootMechanics.last_loot_spawn_tick.put(b.getLocation(), Long.valueOf(System.currentTimeMillis()));
						b = b2;
						tick_set = true;
					}
					else if (b2.getType() != Material.FIRE) {
						return;
					}
				}

				if (!tick_set) {
					LootMechanics.last_loot_spawn_tick.put(b.getLocation().subtract(0.0D, 1.0D, 0.0D), Long.valueOf(System.currentTimeMillis()));
				}

				powerup_map.remove(b);
				b.setType(Material.AIR);
				beacons.remove(b.getLocation().subtract(0.0D, 1.0D, 0.0D));
				b.getLocation().subtract(0.0D, 1.0D, 0.0D).getBlock().setType(Material.AIR);

				for(Entity ent : b.getChunk().getEntities()){
					if(!(ent instanceof EnderCrystal)){
						continue;
					}
					if(ent.getLocation().distanceSquared(b.getLocation()) <= 4){
						ender_crystals.remove(ent);
						ent.remove();
						break;
					}
				}

				//Packet particles = new Packet61WorldEvent(2001, (int)Math.round(b.getLocation().getX()), (int)Math.round(b.getLocation().getY()), (int)Math.round(b.getLocation().getZ()), 138, false);
				//((CraftServer) getServer()).getServer().getPlayerList().sendPacketNearby(b.getLocation().getX(), b.getLocation().getY(), b.getLocation().getZ(), 24, ((CraftWorld) b.getWorld()).getHandle().dimension, particles);

				try {
					ParticleEffect.sendToLocation(ParticleEffect.MAGIC_CRIT, b.getLocation().add(0, 1, 0), new Random().nextFloat(), new Random().nextFloat(), new Random().nextFloat(), 1F, 50);
				} catch (Exception e1) {e1.printStackTrace();}
				//b.getWorld().spawnParticle(b.getLocation().add(0, 1, 0), Particle.MAGIC_CRIT, 1F, 50);

				handleBeaconEffect(p, -1);
			}
		}
	}*/
}