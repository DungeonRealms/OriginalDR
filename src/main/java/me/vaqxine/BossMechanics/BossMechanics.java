package me.vaqxine.BossMechanics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import me.vaqxine.Main;
import me.vaqxine.BossMechanics.commands.CommandSpawnBoss;
import me.vaqxine.EnchantMechanics.EnchantMechanics;
import me.vaqxine.HealthMechanics.HealthMechanics;
import me.vaqxine.Hive.ParticleEffect;
import me.vaqxine.InstanceMechanics.InstanceMechanics;
import me.vaqxine.ItemMechanics.ItemGenerators;
import me.vaqxine.MoneyMechanics.MoneyMechanics;
import me.vaqxine.MonsterMechanics.MonsterMechanics;
import me.vaqxine.enums.CC;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class BossMechanics implements Listener {
	/*
	 * Bosses: UNHOLY_PRIEST
	 */
	Logger log = Logger.getLogger("Minecraft");
	
	public static ConcurrentHashMap<Entity, String> boss_map = new ConcurrentHashMap<Entity, String>();
	// Entity, Boss Type
	
	public static ConcurrentHashMap<Entity, Double> minion_count = new ConcurrentHashMap<Entity, Double>();
	// (Unholy Priest) Entity, Amount of Minion Waves spawned. 9=max, 1=90% hp
	// left
	
	public static ConcurrentHashMap<Entity, List<Entity>> minion_map = new ConcurrentHashMap<Entity, List<Entity>>();
	// (Unholy Priest) Entity, Current Active Wave of Entities
	// Also used for bandit_leader's mates.
	
	public static ConcurrentHashMap<Entity, Integer> boss_heals = new ConcurrentHashMap<Entity, Integer>();
	// (Unholy Priest) Entity, # of times the boss has healed.
	
	public static HashMap<Entity, Location> boss_saved_location = new HashMap<Entity, Location>();
	// Used to make sure boss doesn't move from this spot.
	
	public static HashMap<Entity, String> boss_event_log = new HashMap<Entity, String>();
	// Contains events the boss has already gone through.
	
	/*
	 * public static ConcurrentHashMap<Entity, Entity> boss_link = new
	 * ConcurrentHashMap<Entity, Entity>(); // (Unholy Priest) Entity, The Blaze
	 * Entity
	 */
	
	public static List<Entity> enraged_boss = new ArrayList<Entity>();
	
	/*- he should heal himself three times before dying at 20-30% HP
	- He should have an AoE move with his axe (whirlwind)
	- He should be able to summon minions everytime they are killed and he loses 10% HP.
	- He should have a powerstrike that does high knockback, 5% chance of occuring.
	- When he is going to die, i.e the last 10%, he "rages" particle effects and beings to hit a lot harder.
	 */
	
	public void onEnable() {
		
		Main.plugin.getCommand("spawnboss").setExecutor(new CommandSpawnBoss());
		
		Bukkit.getServer().getPluginManager().registerEvents(this, Main.plugin);
		
		new BukkitRunnable() {
			@Override
			public void run() {
				for(Entity ent : enraged_boss) {
					try {
						ParticleEffect.sendToLocation(ParticleEffect.PORTAL, ent.getLocation(), new Random().nextFloat(), new Random().nextFloat(), new Random().nextFloat(), 0.2F, 50);
						ParticleEffect.sendToLocation(ParticleEffect.WITCH_MAGIC, ent.getLocation(), new Random().nextFloat(), new Random().nextFloat(), new Random().nextFloat(), 0.2F, 20);
					} catch(Exception e) {
						System.err.println(CC.YELLOW + "Unable to spawn boss particles!" + CC.DEFAULT);
					}
				}
			}
		}.runTaskTimerAsynchronously(Main.plugin, 10L * 20L, 10L);
		
		new BukkitRunnable() {
			@Override
			public void run() {
				
			}
		};
		
		new BukkitRunnable() {
			@Override
			public void run() {
				List<Entity> remove = new ArrayList<Entity>();
				for(Entry<Entity, List<Entity>> data : minion_map.entrySet()) {
					final Entity boss = data.getKey();
					if(!(boss_saved_location.containsKey(boss))) {
						continue;
					}
					List<Entity> minions = data.getValue();
					boolean minion_alive = false;
					for(Entity ent : minions) {
						if(ent != null && MonsterMechanics.mob_health.containsKey(ent)) {
							minion_alive = true;
							break;
						}
					}
					
					if(minion_alive == false) {
						LivingEntity le_boss = (LivingEntity) boss;
						boss_saved_location.remove(boss);
						le_boss.removePotionEffect(PotionEffectType.INVISIBILITY);
						remove.add(boss);
						for(Player pl : boss.getWorld().getPlayers()) {
							pl.sendMessage(ChatColor.GOLD + "" + ChatColor.UNDERLINE + "Burick The Fanatic: " + ChatColor.WHITE + "Face me, pathetic creatures!");
						}
					} else {
						LivingEntity le = (LivingEntity) boss;
						if(le.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
						    new BukkitRunnable(){

                                public void run() {
                                   try {
								ParticleEffect.sendToLocation(ParticleEffect.PORTAL, boss.getLocation().add(0, 1, 0), new Random().nextFloat(), new Random().nextFloat(), new Random().nextFloat(), 0.2F, 50);
							} catch(Exception e) {
								e.printStackTrace();
							} // TODO Auto-generated method stub
                                    
                                }
						    }.runTask(Main.plugin);
							
							boss.setVelocity(new Vector(0, 0.40F, 0));
						}
					}
				}
				
				for(Entity e : remove) {
					minion_map.remove(e);
				}
			}
		}.runTaskTimerAsynchronously(Main.plugin, 10L * 20L, 20L);
		
		new BukkitRunnable() {
			@Override
			public void run() {
				List<Entity> remove = new ArrayList<Entity>();
				for(Entry<Entity, List<Entity>> data : minion_map.entrySet()) {
					Entity boss = data.getKey();
					LivingEntity le = (LivingEntity) boss;
					if(le == null || le.isDead()) {
						remove.add(boss);
					}
					if(le.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
						if(boss_saved_location.containsKey(boss) && (boss.getLocation().distanceSquared(boss_saved_location.get(boss)) > 64)) {
							le.teleport(boss_saved_location.get(boss));
						}
					}
				}
				
				for(Entity e : remove) {
					minion_map.remove(e);
				}
			}
		}.runTaskTimerAsynchronously(Main.plugin, 10L * 20L, 2L);
		
		Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(Main.plugin, new Runnable() {
			public void run() {
				for(Entity boss : boss_map.keySet()) {
					String boss_type = boss.getMetadata("boss_type").get(0).asString();
					if(boss_type.equalsIgnoreCase("fire_demon") && boss.getVehicle() == null && boss.isOnGround()) {
						// Ignite the floor beneath this beast.
						Location loc = boss.getLocation();// .add(0, 1, 0);
						if(loc.getBlock().getType() == Material.AIR) {
							loc.getBlock().setType(Material.FIRE);
							// Chance of spawning some shit.
							if(new Random().nextInt(20) == 0) {
								Entity add = MonsterMechanics.spawnTierMob(loc, EntityType.MAGMA_CUBE, 3, -1, loc, false, "", "Spawn of Inferno", true);
								add.setFireTicks(Integer.MAX_VALUE);
							}
						}
					}
				}
			}
		}, 10 * 20L, 5L);
		
		log.info("[BossMechanics] has been enabled.");
	}
	
	public void onDisable() {
		log.info("[BossMechanics] has been disabled.");
	}
	
	public static List<Block> getNearbyBlocks(Location loc, int maxradius) {
		List<Block> return_list = new ArrayList<Block>();
		BlockFace[] faces = { BlockFace.UP, BlockFace.NORTH, BlockFace.EAST };
		BlockFace[][] orth = { { BlockFace.NORTH, BlockFace.EAST }, { BlockFace.UP, BlockFace.EAST }, { BlockFace.NORTH, BlockFace.UP } };
		for(int r = 0; r <= maxradius; r++) {
			for(int s = 0; s < 6; s++) {
				BlockFace f = faces[s % 3];
				BlockFace[] o = orth[s % 3];
				if(s >= 3) f = f.getOppositeFace();
				if(!(loc.getBlock().getRelative(f, r) == null)) {
					Block c = loc.getBlock().getRelative(f, r);
					
					for(int x = -r; x <= r; x++) {
						for(int y = -r; y <= r; y++) {
							Block a = c.getRelative(o[0], x).getRelative(o[1], y);
							return_list.add(a);
						}
					}
				}
			}
		}
		return return_list;
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onBossDeath(EntityDeathEvent e) {
		final Entity ent = e.getEntity();
		if(boss_map.containsKey(ent)) {
			
			boolean final_boss = false;
			String server_message = "";
			
			if(boss_map.get(ent).equalsIgnoreCase("unholy_priest") ||  boss_map.get(ent).equalsIgnoreCase("aceron") || boss_map.get(ent).equalsIgnoreCase("bandit_leader") || boss_map.get(ent).equalsIgnoreCase("fire_demon")) {
				final_boss = true;
			}
			
			// TODO: Rewards! (tokens, custom drops, etc?)
			if(boss_map.get(ent).equalsIgnoreCase("unholy_priest")) {
				for(Block b : getNearbyBlocks(ent.getLocation(), 10)) {
					if(b.getType() == Material.FIRE) {
						b.setType(Material.AIR);
					}
				}
				
				server_message = ChatColor.GOLD.toString() + ChatColor.BOLD + ">> " + ChatColor.GOLD + "The corrupt Unholy Priest " + ChatColor.UNDERLINE + "Burick The Fanatic" + ChatColor.RESET + ChatColor.GOLD + " has been slain by a group of adventurers!";
				
				try {
					ParticleEffect.sendToLocation(ParticleEffect.FIREWORKS_SPARK, ent.getLocation().add(0, 2, 0), new Random().nextFloat(), new Random().nextFloat(), new Random().nextFloat(), 0.2F, 200);
				} catch(Exception err) {
					err.printStackTrace();
				}
				
				LivingEntity le = (LivingEntity) ent;
				int do_i_drop_gear = new Random().nextInt(100);
				if(do_i_drop_gear < 80) { // 80% chance!
					List<ItemStack> possible_drops = new ArrayList<ItemStack>();
					for(ItemStack is : le.getEquipment().getArmorContents()) {
						if(is == null || is.getType() == Material.AIR || is.getTypeId() == 144 || is.getTypeId() == 397) { // Monster
																															// heads
																															// (bandit)!
							continue;
						}
						ItemMeta im = is.getItemMeta();
						if(im.hasEnchants()) {
							for(Map.Entry<Enchantment, Integer> data : im.getEnchants().entrySet()) {
								is.removeEnchantment(data.getKey());
							}
						}
						is.removeEnchantment(Enchantment.LOOT_BONUS_MOBS);
						is.removeEnchantment(Enchantment.KNOCKBACK);
						is.removeEnchantment(EnchantMechanics.getCustomEnchant());
						is.setItemMeta(im);
						
						possible_drops.add(is);
					}
					
					ItemStack weapon = le.getEquipment().getItemInHand();
					ItemMeta im = weapon.getItemMeta();
					if(im.hasEnchants()) {
						for(Map.Entry<Enchantment, Integer> data : im.getEnchants().entrySet()) {
							im.removeEnchant(data.getKey());
						}
					}
					weapon.removeEnchantment(Enchantment.LOOT_BONUS_MOBS);
					weapon.removeEnchantment(Enchantment.KNOCKBACK);
					weapon.removeEnchantment(EnchantMechanics.getCustomEnchant());
					weapon.setItemMeta(im);
					
					possible_drops.add(weapon);
					
					ItemStack reward = possible_drops.get(new Random().nextInt(possible_drops.size()));
					Entity item = ent.getWorld().dropItemNaturally(ent.getLocation(), reward);
					item.setMetadata("boss_drop", new FixedMetadataValue(Main.plugin, ""));
				}
				
				int gem_drop = new Random().nextInt(2500 - 1000) + 1000;
				while(gem_drop > 0) {
					gem_drop -= 5;
					ent.getWorld().dropItemNaturally(ent.getLocation(), MoneyMechanics.makeGems(5));
				}
			}
			
			if(boss_map.get(ent).equalsIgnoreCase("fire_demon")) {
				
				try {
					ParticleEffect.sendToLocation(ParticleEffect.HUGE_EXPLOSION, ent.getLocation().add(0, 2, 0), new Random().nextFloat(), new Random().nextFloat(), new Random().nextFloat(), 1F, 50);
				} catch(Exception err) {
					err.printStackTrace();
				}
				
				final_boss = false;
				
				for(LivingEntity le : ent.getWorld().getLivingEntities()) {
					if(le instanceof Player) {
						continue;
					}
					if(MonsterMechanics.mob_health.containsKey(le)) {
						le.damage(Integer.MAX_VALUE);
						le.remove();
					}
				}
				
				for(Player pl : ent.getWorld().getPlayers()) {
					pl.sendMessage(ChatColor.GOLD + "" + ChatColor.UNDERLINE + "The Infernal Abyss: " + ChatColor.WHITE + "You...have... defeated me...ARGHHHH!!!!!");
					MonsterMechanics.pushAwayPlayer(ent, pl, 6.0F);
					pl.playSound(pl.getLocation(), Sound.EXPLODE, 1F, 1F);
					pl.playSound(pl.getLocation(), Sound.ENDERDRAGON_DEATH, 2F, 2F);
				}
				
				server_message = ChatColor.GOLD.toString() + ChatColor.BOLD + ">> " + ChatColor.GOLD + "The evil fire demon known as " + ChatColor.UNDERLINE + "The Infernal Abyss" + ChatColor.RESET + ChatColor.GOLD + " has been slain by a group of adventurers!";
				final String f_server_message = server_message;
				
				Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
					public void run() {
						try {
							ParticleEffect.sendToLocation(ParticleEffect.FIREWORKS_SPARK, ent.getLocation().add(0, 2, 0), new Random().nextFloat(), new Random().nextFloat(), new Random().nextFloat(), 0.5F, 200);
						} catch(Exception err) {
							err.printStackTrace();
						}
						
						LivingEntity le = (LivingEntity) ent;
						int do_i_drop_gear = new Random().nextInt(100);
						if(do_i_drop_gear < 80) { // 80% chance!
							List<ItemStack> possible_drops = new ArrayList<ItemStack>();
							for(ItemStack is : le.getEquipment().getArmorContents()) {
								if(is == null || is.getType() == Material.AIR || is.getTypeId() == 144 || is.getTypeId() == 397) { // Monster
																																	// heads
																																	// (bandit)!
									continue;
								}
								ItemMeta im = is.getItemMeta();
								if(im.hasEnchants()) {
									for(Map.Entry<Enchantment, Integer> data : im.getEnchants().entrySet()) {
										is.removeEnchantment(data.getKey());
									}
								}
								is.removeEnchantment(Enchantment.LOOT_BONUS_MOBS);
								is.removeEnchantment(Enchantment.KNOCKBACK);
								im.removeEnchant(EnchantMechanics.getCustomEnchant());
								is.setItemMeta(im);
								
								possible_drops.add(is);
							}
							
							ItemStack weapon = le.getEquipment().getItemInHand();
							ItemMeta im = weapon.getItemMeta();
							if(im.hasEnchants()) {
								for(Map.Entry<Enchantment, Integer> data : im.getEnchants().entrySet()) {
									weapon.removeEnchantment(data.getKey());
								}
							}
							weapon.removeEnchantment(Enchantment.LOOT_BONUS_MOBS);
							weapon.removeEnchantment(Enchantment.KNOCKBACK);
							weapon.removeEnchantment(EnchantMechanics.getCustomEnchant());
							weapon.setItemMeta(im);
							
							possible_drops.add(weapon);
							
							ItemStack reward = possible_drops.get(new Random().nextInt(possible_drops.size()));
							Entity item = ent.getWorld().dropItemNaturally(ent.getLocation(), reward);
		                    item.setMetadata("boss_drop", new FixedMetadataValue(Main.plugin, ""));
						}
						
						int gem_drop = new Random().nextInt(12000 - 10000) + 10000;
						while(gem_drop > 0) {
							gem_drop -= 500;
							short real_id = 777;
							ItemStack money = new ItemStack(Material.PAPER, 1, real_id);
							ent.getWorld().dropItemNaturally(ent.getLocation(), MoneyMechanics.signBankNote(money, ChatColor.GREEN.toString() + "Bank Note", ChatColor.WHITE.toString() + ChatColor.BOLD.toString() + "Value:" + ChatColor.WHITE.toString() + " " + 500 + " Gems" + "," + ChatColor.GRAY.toString() + "Exchange at any bank for GEM(s)"));
						}
						
						String instance_name = ent.getWorld().getName();
						List<String> party_members = InstanceMechanics.instance_party.get(instance_name);
						String adventurers = "";
						for(String s : party_members) {
							adventurers += s + ", ";
						}
						
						if(adventurers.endsWith(", ")) {
							adventurers = adventurers.substring(0, adventurers.lastIndexOf(","));
						}
						
						final String f_adv = adventurers;
						
						if(InstanceMechanics.isInstance(instance_name)) {
							InstanceMechanics.teleport_on_complete.put(instance_name, 60);
						}
						
						for(Player pl : Bukkit.getServer().getOnlinePlayers()) {
							pl.sendMessage(f_server_message);
							pl.sendMessage(ChatColor.GRAY + "Group: " + f_adv);
						}
						
						boss_map.remove(ent);
					}
				}, 60L);
				
			}
			
			if(boss_map.get(ent).equalsIgnoreCase("bandit_leader")) {
				server_message = ChatColor.GOLD.toString() + ChatColor.BOLD + ">> " + ChatColor.GOLD + "The cunning bandit lord " + ChatColor.UNDERLINE + "Mayel The Cruel" + ChatColor.RESET + ChatColor.GOLD + " has been slain by a group of adventurers!";
				try {
					ParticleEffect.sendToLocation(ParticleEffect.FIREWORKS_SPARK, ent.getLocation().add(0, 2, 0), new Random().nextFloat(), new Random().nextFloat(), new Random().nextFloat(), 0.5F, 200);
				} catch(Exception err) {
					err.printStackTrace();
				}
				
				LivingEntity le = (LivingEntity) ent;
				int do_i_drop_gear = new Random().nextInt(100);
				if(do_i_drop_gear < 100) { // 100% chance!
					List<ItemStack> possible_drops = new ArrayList<ItemStack>();
					for(ItemStack is : le.getEquipment().getArmorContents()) {
						if(is == null || is.getType() == Material.AIR || is.getTypeId() == 144 || is.getTypeId() == 397) { // Monster
																															// heads
																															// (bandit)!
							continue;
						}
						ItemMeta im = is.getItemMeta();
						if(im.hasEnchants()) {
							for(Map.Entry<Enchantment, Integer> data : im.getEnchants().entrySet()) {
								is.removeEnchantment(data.getKey());
							}
						}
						is.removeEnchantment(Enchantment.LOOT_BONUS_MOBS);
						is.removeEnchantment(Enchantment.KNOCKBACK);
						is.removeEnchantment(EnchantMechanics.getCustomEnchant());
						is.setItemMeta(im);
						
						possible_drops.add(is);
					}
					
					possible_drops.add(ItemGenerators.customGenerator("mayelhelmet"));
					
					ItemStack weapon = le.getEquipment().getItemInHand();
					ItemMeta im = weapon.getItemMeta();
					if(im.hasEnchants()) {
						for(Map.Entry<Enchantment, Integer> data : im.getEnchants().entrySet()) {
							weapon.removeEnchantment(data.getKey());
						}
					}
					
					weapon.removeEnchantment(Enchantment.LOOT_BONUS_MOBS);
					weapon.removeEnchantment(Enchantment.KNOCKBACK);
					weapon.removeEnchantment(EnchantMechanics.getCustomEnchant());
					weapon.setItemMeta(im);
					
					possible_drops.add(weapon);
					
					ItemStack reward = possible_drops.get(new Random().nextInt(possible_drops.size()));
					Entity item = ent.getWorld().dropItemNaturally(ent.getLocation(), reward);
                    item.setMetadata("boss_type", new FixedMetadataValue(Main.plugin, ""));
				}
				
				int gem_drop = new Random().nextInt(250 - 100) + 100;
				while(gem_drop > 0) {
					gem_drop -= 5;
					ent.getWorld().dropItemNaturally(ent.getLocation(), MoneyMechanics.makeGems(5));
				}
			}
			
			if(boss_map.get(ent).equalsIgnoreCase("tnt_bandit")) {
				// EXPLODE!
				for(Player pl : ent.getWorld().getPlayers()) {
					pl.sendMessage(ChatColor.GOLD + "" + ChatColor.UNDERLINE + "Mad Bandit Pyromancer: " + ChatColor.WHITE + "I won't be defeated so easily! Now you all die!");
					pl.sendMessage(ChatColor.RED.toString() + ChatColor.UNDERLINE + "Mad Bandit Pyromancer has booby-trapped his body with explosives! Get away!");
					pl.playSound(pl.getLocation(), Sound.FUSE, 1F, 1F);
				}
				Entity tnt = ent.getWorld().spawn(ent.getLocation(), TNTPrimed.class);
				((TNTPrimed) tnt).setFuseTicks(80);
				final Location explosion = ent.getLocation();
				
				Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
					public void run() {
						try {
							ParticleEffect.sendToLocation(ParticleEffect.HUGE_EXPLOSION, ent.getLocation().add(0, 1.5, 0), new Random().nextFloat(), new Random().nextFloat(), new Random().nextFloat(), 1F, 200);
						} catch(Exception err) {
							err.printStackTrace();
						}
						
						explosion.getWorld().playSound(explosion, Sound.EXPLODE, 10F, 1F);
						for(Player pl : explosion.getWorld().getPlayers()) {
							if(pl.getLocation().distanceSquared(explosion) <= 64) {
								// Hurt them.
								// Deal 90% of health in DMG
								double max_health = HealthMechanics.getMaxHealthValue(pl.getName());
								double dmg = max_health * 0.90;
								pl.damage(dmg, ent);
							}
						}
					}
				}, 80L);
				
			}
			
			
			if(final_boss) {
				// This will move them all out.
				String instance_name = ent.getWorld().getName();
				List<String> party_members = InstanceMechanics.instance_party.get(instance_name);
				String adventurers = "";
				for(String s : party_members) {
					adventurers += s + ", ";
				}
				
				if(adventurers.endsWith(", ")) {
					adventurers = adventurers.substring(0, adventurers.lastIndexOf(","));
				}
				
				final String f_adv = adventurers;
				
				if(InstanceMechanics.isInstance(instance_name)) {
					InstanceMechanics.teleport_on_complete.put(instance_name, 60);
				}
				
				if(server_message.length() > 0) {
					final String f_server_message = server_message;
					new BukkitRunnable() {
						@Override
						public void run() {
							for(Player pl : Bukkit.getServer().getOnlinePlayers()) {
								pl.sendMessage(f_server_message);
								pl.sendMessage(ChatColor.GRAY + "Group: " + f_adv);
							}
						}
					}.runTaskLater(Main.plugin, 30L);
				}
			}
			
			boss_map.remove(ent);
		}
	}
	
}
