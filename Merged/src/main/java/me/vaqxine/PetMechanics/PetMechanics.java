package me.vaqxine.PetMechanics;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import me.vaqxine.Main;
import me.vaqxine.ChatMechanics.ChatMechanics;
import me.vaqxine.DonationMechanics.DonationMechanics;
import me.vaqxine.DuelMechanics.DuelMechanics;
import me.vaqxine.EcashMechanics.EcashMechanics;
import me.vaqxine.Hive.Hive;
import me.vaqxine.Hive.ParticleEffect;
import me.vaqxine.InstanceMechanics.InstanceMechanics;
import me.vaqxine.ItemMechanics.ItemMechanics;
import me.vaqxine.MoneyMechanics.MoneyMechanics;
import me.vaqxine.MountMechanics.MountMechanics;
import me.vaqxine.RealmMechanics.RealmMechanics;
import me.vaqxine.TeleportationMechanics.TeleportationMechanics;
import net.minecraft.server.v1_7_R1.EntityCreature;
import net.minecraft.server.v1_7_R1.EntityCreeper;
import net.minecraft.server.v1_7_R1.EntityInsentient;

import org.apache.commons.lang.StringEscapeUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.EntityEffect;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_7_R1.entity.CraftCreeper;
import org.bukkit.craftbukkit.v1_7_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_7_R1.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_7_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_7_R1.inventory.CraftItemStack;
import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.Bat;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Horse.Style;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.MushroomCow;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Zombie;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPortalEnterEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public class PetMechanics implements Listener {
	static Logger log = Logger.getLogger("Minecraft");

	public static String templatePath_s = "plugins/PetMechanics/pet_messages/";
	public static File templatePath = new File(templatePath_s);

	public static HashMap<String, List<String>> player_pets = new HashMap<String, List<String>>();

	public static HashMap<String, Long> pet_spawn_delay = new HashMap<String, Long>();

	public static volatile ConcurrentHashMap<String, List<Entity>> pet_map = new ConcurrentHashMap<String, List<Entity>>();
	public static ConcurrentHashMap<Entity, String> inv_pet_map = new ConcurrentHashMap<Entity, String>();

	public static HashMap<Entity, List<Entity>> baby_chick_map = new HashMap<Entity, List<Entity>>();
	public static HashMap<Entity, Integer> chicken_count = new HashMap<Entity, Integer>();
	public static HashMap<Entity, Entity> chicken_eggs = new HashMap<Entity, Entity>();

	public static HashMap<String, Location> naming_pet = new HashMap<String, Location>();
	public static HashMap<String, String> pet_type = new HashMap<String, String>();
	public static HashMap<String, List<String>> phrase_list = new HashMap<String, List<String>>();

	public static HashMap<Entity, Entity> zombie_eating = new HashMap<Entity, Entity>();
	public static HashMap<Entity, Entity> creeper_chase = new HashMap<Entity, Entity>();
	public static HashMap<Entity, Long> creeper_firework = new HashMap<Entity, Long>();

	public static List<Item> fake_gems = new ArrayList<Item>();

	public static List<org.bukkit.entity.Horse.Color> horse_color_list = new ArrayList<org.bukkit.entity.Horse.Color>();
	public static List<org.bukkit.entity.Horse.Style> horse_style_list = new ArrayList<org.bukkit.entity.Horse.Style>();

	PetMechanics instance = null;

	@SuppressWarnings("deprecation")
	public void onEnable() {
		Main.plugin.getServer().getPluginManager().registerEvents(this, Main.plugin);
		loadPetMessageTemplates();
		instance = this;

		for(org.bukkit.entity.Horse.Color horse_color : org.bukkit.entity.Horse.Color.values()){
			horse_color_list.add(horse_color);
		}

		for(Style horse_style : Style.values()){
			horse_style_list.add(horse_style);
		}

		Main.plugin.getServer().getScheduler().scheduleAsyncRepeatingTask(Main.plugin, new Runnable() {
			public void run() {
				ConnectionPool.refresh = true;
			}
		}, 200 * 20L, 200 * 20L);

		Main.plugin.getServer().getScheduler().scheduleAsyncRepeatingTask(Main.plugin, new Runnable() {
			public void run() {
				for(Entity ent : inv_pet_map.keySet()){
					if(ent != null && ent.getType() == EntityType.CREEPER && ((Creeper)ent).isPowered() && !ent.isDead()){
						long last_time = 0L;

						if(creeper_firework.containsKey(ent)){
							last_time = creeper_firework.get(ent);
						}

						if((System.currentTimeMillis() - last_time) >= (20 * 1000)){
							Color c1 = null;
							Color c2 = null;
							org.bukkit.FireworkEffect.Type t = null;

							int color_type = new Random().nextInt(3);
							if(color_type == 0){
								c1 = Color.RED;
							}
							if(color_type == 1){
								c1 = Color.WHITE;
							}
							if(color_type == 2){
								c1 = Color.BLUE;
							}

							color_type = new Random().nextInt(3);
							if(color_type == 0){
								if(c1 != Color.RED){
									c2 = Color.RED;
								}
								else{
									c2 = Color.BLUE;
								}
							}
							if(color_type == 1){
								if(c1 != Color.WHITE){
									c2 = Color.WHITE;
								}
								else{
									c2 = Color.RED;
								}
							}
							if(color_type == 2){
								if(c1 != Color.BLUE){
									c2 = Color.BLUE;
								}
								else{
									c2 = Color.WHITE;
								}
							}

							t = org.bukkit.FireworkEffect.Type.BALL;

							Firework fw = (Firework) ent.getWorld().spawnEntity(ent.getLocation().add(0, 1, 0), EntityType.FIREWORK);
							FireworkMeta fwm = fw.getFireworkMeta();
							Random r = new Random();   
							FireworkEffect effect = FireworkEffect.builder().flicker(false).withColor(c1).withFade(c2).with(t).trail(true).build();
							fwm.addEffect(effect);
							fwm.setPower(0);
							fw.setFireworkMeta(fwm);

							creeper_firework.put(ent, System.currentTimeMillis());
						}
					}
				}
			}
		}, 20 * 20L, 5 * 20L);

		Main.plugin.getServer().getScheduler().scheduleSyncRepeatingTask(Main.plugin, new Runnable() {
			public void run() {
				for(Entity ent : inv_pet_map.keySet()){
					if(ent != null && ent.getType() == EntityType.MUSHROOM_COW){
						MushroomCow ms = (MushroomCow)ent;
						ms.setBaby();
						ms.setAgeLock(true);
					}
					if(ent != null && ent.getType() == EntityType.SHEEP){
						Sheep s = (Sheep)ent;
						s.setBaby();
						s.setAgeLock(true);
					}
				}
			}
		}, 180 * 20L, 60 * 20L);

		Main.plugin.getServer().getScheduler().scheduleSyncRepeatingTask(Main.plugin, new Runnable() {
			public void run() {
				for(Entity ent : inv_pet_map.keySet()){
					if(ent.getType() == EntityType.GIANT){
						String owner = inv_pet_map.get(ent);
						ent.remove();
						removePetFromSpawnedList(owner, ent);
						inv_pet_map.remove(ent);
					}
				}
			}
		}, 10 * 20L, 40 * 20L);

		Main.plugin.getServer().getScheduler().scheduleSyncRepeatingTask(Main.plugin, new Runnable() {
			public void run() {
				List<Item> to_remove = new ArrayList<Item>();
				if(fake_gems.size() <= 0){return;}
				for(Item it : fake_gems){
					if(!(it.hasMetadata("droptime"))){
						it.remove();				
						to_remove.add(it);
						continue;
					}
					long drop_time = it.getMetadata("droptime").get(0).asLong();
					if((System.currentTimeMillis() - drop_time) > 1500){
						it.remove();
						to_remove.add(it);
					}
				}

				for(Item it : to_remove){
					fake_gems.remove(it);
				}
			}
		}, 10 * 20L, 10L);

		Main.plugin.getServer().getScheduler().scheduleSyncRepeatingTask(Main.plugin, new Runnable() {
			public void run() {
				for(Entry<Entity, Entity> data : creeper_chase.entrySet()){
					Entity creeper = data.getKey();
					Entity player = data.getValue();
					LivingEntity le = (LivingEntity)creeper;
					try {
						ParticleEffect.sendToLocation(ParticleEffect.EXPLODE, le.getLocation().add(0, 1.5, 0), new Random().nextFloat(), new Random().nextFloat(), new Random().nextFloat(), 1F, 20);
					} catch (Exception e) {e.printStackTrace();}
					//le.getWorld().spawnParticle(le.getLocation().add(0, 1.5, 0), Particle.LARGE_EXPLODE, 1F, 20);
					le.getWorld().playSound(le.getLocation(), Sound.EXPLODE, 2F, 1F);
				}
				creeper_chase.clear();
			}
		}, 10 * 20L, 15L);

		Main.plugin.getServer().getScheduler().scheduleSyncRepeatingTask(Main.plugin, new Runnable() {
			public void run() {
				for(Entry<Entity, String> data : inv_pet_map.entrySet()){
					Entity e = data.getKey();
					String p_name = data.getValue();

					if(Bukkit.getPlayer(p_name) == null){
						continue;
					}
					Player p = Bukkit.getPlayer(p_name);
					LivingEntity le = (LivingEntity)e;
					String lptt = getPetType(e);			

					if(lptt == null || le == null){
						continue;
					}

					if(lptt.equalsIgnoreCase("Sheep O' Luck") && (le.getWorld().getName().equalsIgnoreCase(Bukkit.getWorlds().get(0).getName()) || InstanceMechanics.isInstance(le.getWorld().getName()))){
						int drop_gem = 1;//new Random().nextInt(2); // 0, 1
						if(drop_gem == 1){
							ItemStack fake_gem = new ItemStack(Material.EMERALD, 1);
							Item i = le.getWorld().dropItem(le.getLocation(), fake_gem);
							i.setMetadata("nopickup", new FixedMetadataValue(Main.plugin, true));
							i.setMetadata("droptime", new FixedMetadataValue(Main.plugin, System.currentTimeMillis()));
							i.setVelocity(new Vector(0, 0.1, 0));
							fake_gems.add(i);
						}	
					}

					if(lptt.equalsIgnoreCase("Easter's Chicken") && (getChickCount(e) < 2)){
						Egg egg = (Egg)le.getWorld().spawnEntity(le.getLocation().add(0, 1, 0), EntityType.EGG);
						egg.setBounce(true);
						//egg.setMetadata("mother", new FixedMetadataValue(instance, e));
						chicken_eggs.put((Entity)egg, e);
						setChickCount(e, 1, true);
					}
				}
			}
		}, 10 * 20L, 8 * 20L);

		Main.plugin.getServer().getScheduler().scheduleAsyncRepeatingTask(Main.plugin, new Runnable() {
			public void run() {
				for(Entry<Entity, List<Entity>> data : baby_chick_map.entrySet()){
					Entity mother = data.getKey();

					for(Entity baby : data.getValue()){
						LivingEntity le = (LivingEntity)baby;

						if(mother == null || mother.isDead()){
							le.remove();
						}

						if(le.getWorld().getName().equalsIgnoreCase(mother.getWorld().getName())){
							double dist = le.getLocation().distanceSquared(mother.getLocation());
							if(dist >= 100){ //256
								le.teleport(mother);
							}
							else if(dist < 9){
								walkTo(le, mother.getLocation().getX(), mother.getLocation().getY(), mother.getLocation().getZ(), 1.2F);
							}
						}
						else{
							le.teleport(mother);
						}
					}
				}
			}
		}, 8 * 20L, 15L);

		Main.plugin.getServer().getScheduler().scheduleAsyncRepeatingTask(Main.plugin, new Runnable() {
			public void run() {
				HashMap<Entity, String> to_update = new HashMap<Entity, String>();
				List<Entity> to_remove = new ArrayList<Entity>();
				for(Entry<Entity, String> data : inv_pet_map.entrySet()){
					Entity e = data.getKey();
					String p_name = data.getValue();

					Player p = Bukkit.getPlayer(p_name);
					LivingEntity le = (LivingEntity)e;
					if(p != null){
						float speed = 1.2F;

						String lptt = getPetType(e);

						if(lptt.equalsIgnoreCase("Baby Horse")){
							speed = 0.5F;
						}
						/*if(lptt.equalsIgnoreCase("Creeper of Independence")){
							speed = 1.2F;
						}

						if(lptt.equalsIgnoreCase("Sheep O' Luck")){
							speed = 1.2F;
						}

						if(lptt.equalsIgnoreCase("Baby Mooshroom")){
							speed = 1.2F;
						}

						if(lptt.equalsIgnoreCase("Easter's Chicken")){
							speed = 1.2F;
						}

						if(lptt.equalsIgnoreCase("Beta Slime")){
							speed = 1.2F;
						}*/

						if(lptt.equalsIgnoreCase("Jeepers Creepers")){
							speed = 1.2F;
							int hiss = new Random().nextInt(25); // 0, 1, 2, 3, 4
							if(hiss == 0){
								EntityCreeper ec = (EntityCreeper)(((CraftCreeper)le).getHandle());
								ec.a(1);
								le.getWorld().playSound(le.getLocation(), Sound.CREEPER_HISS, 1F, 1F);
							}
							int explode = new Random().nextInt(100);
							if(explode == 0){

								for(Entity ent : le.getNearbyEntities(10, 10, 10)){
									if(ent instanceof Player){
										Player p_ent = (Player)ent;
										if(p_ent.hasMetadata("NPC") || p_ent.getPlayerListName().equalsIgnoreCase("")){
											continue;
										}
										if(inv_pet_map.get(le).equalsIgnoreCase(p_ent.getName())){
											continue;
										}
										EntityCreeper ec = (EntityCreeper)(((CraftCreeper)le).getHandle());
										CraftPlayer cp = (CraftPlayer)p_ent;
										ec.setTarget((net.minecraft.server.v1_7_R1.Entity)cp.getHandle());
										creeper_chase.put(e, ent);
										walkTo(le, p_ent.getLocation().getX(), p_ent.getLocation().getY(), p_ent.getLocation().getZ(), 2.0F);
										break;
									}
								}
							}
						}

						if(lptt.equalsIgnoreCase("Baby Cat")){
							speed = 1.20F;
							EntityCreature ec = (EntityCreature) ((CraftEntity) e).getHandle();
							CraftPlayer cp = (CraftPlayer)p;
							ec.setTarget((net.minecraft.server.v1_7_R1.Entity)cp.getHandle());
						}

						if(lptt.equalsIgnoreCase("Beta Slime")){
								try {
									ParticleEffect.sendToLocation(ParticleEffect.HAPPY_VILLAGER, le.getLocation(), new Random().nextFloat(), new Random().nextFloat(), new Random().nextFloat(), 0.8F, 10);
								} catch (Exception err) {err.printStackTrace();}
						}

						if(le.getWorld().getName().equalsIgnoreCase(p.getWorld().getName())){
							try{
								double dis = p.getLocation().distanceSquared(le.getLocation());
								if(lptt.equalsIgnoreCase("Spooky Bats")){
									dis = dis + 225;
								}
								if(dis <= 256 && dis >= 9){
									if(!(zombie_eating.containsKey(e))){
										if(le instanceof Horse){
											((Horse)le).setTarget(p);
											MountMechanics.setLeash(le, p);
										}
										walkTo(le, p.getLocation().getX(), p.getLocation().getY(), p.getLocation().getZ(), speed);
									}
									else if(zombie_eating.containsKey(e)){
										if(dis >= 25){
											zombie_eating.remove(e);
											e.leaveVehicle();
											walkTo(le, p.getLocation().getX(), p.getLocation().getY(), p.getLocation().getZ(), speed);
										}
									}
								}
								else if(dis > 256){ // They're further than 16 blocks.
									zombie_eating.remove(e);
									e.leaveVehicle();
									le.teleport(p.getLocation().add(0, 2, 0));
								}
							} catch(IllegalArgumentException iae){
								// Don't just continue, remove the fucker!
								inv_pet_map.remove(le);
								le.remove();
								removePetFromSpawnedList(p.getName(), le);
								continue;
							}
						}
					}
				}

			}
		}
		, 10 * 20L, 5L);

		Main.plugin.getServer().getScheduler().scheduleSyncRepeatingTask(Main.plugin, new Runnable() {
			public void run() {
				for(Entity pet : inv_pet_map.keySet()){
					Entity partner = null;
					String owner_name = inv_pet_map.get(pet);
					if(pet.getType() == EntityType.MUSHROOM_COW){
						for(Entity ent : pet.getNearbyEntities(3, 3, 3)){
							if(ent.getType() == EntityType.MUSHROOM_COW){
								partner = ent;
								break;
							}
						}

						if(partner == null){continue;}
						double x = (pet.getLocation().getX() + partner.getLocation().getX()) / 2;
						double y = ((pet.getLocation().getY() + partner.getLocation().getY()) / 2) + 1;
						double z = (pet.getLocation().getZ() + partner.getLocation().getZ()) / 2;

						Location midpoint = new Location(pet.getWorld(), x, y, z);
						
						try {
							ParticleEffect.sendToLocation(ParticleEffect.HEART, midpoint, new Random().nextFloat(), new Random().nextFloat(), new Random().nextFloat(), 2F, 50);
						} catch (Exception err) {err.printStackTrace();}
						
						//pet.getWorld().spawnParticle(midpoint, Particle.HEART, 2F, 50);
					}

					if(pet.getType() == EntityType.OCELOT){
						for(Entity ent : pet.getNearbyEntities(2, 2, 2)){
							if(ent.getType() == EntityType.OCELOT){
								partner = ent;
								break;
							}
						}

						if(partner == null){continue;}

						LivingEntity le = (LivingEntity)pet;

						walkTo(le, partner.getLocation().getX(), partner.getLocation().getY(), partner.getLocation().getZ(), 1.20F);

						partner.playEffect(EntityEffect.HURT);
					}

					if(pet.getType() == EntityType.ZOMBIE){
						if(Bukkit.getPlayer(owner_name) != null){
							Player owner = Bukkit.getPlayer(owner_name);
							try{
								if(pet.getWorld().getName().equalsIgnoreCase(owner.getWorld().getName()) && (pet.getLocation().distanceSquared(owner.getLocation()) >= 25.0D || !DuelMechanics.isDamageDisabled(owner.getLocation()))){
									continue;
								}
							} catch(IllegalArgumentException iae){
								inv_pet_map.remove(pet);
								pet.remove();
								removePetFromSpawnedList(owner.getName(), pet);
								continue;
							}
						}

						for(Entity ent : pet.getNearbyEntities(3, 3, 3)){
							if(ent.getType() == EntityType.PLAYER){
								Player p = (Player)ent;
								if(owner_name.equalsIgnoreCase(p.getName())){
									continue; // Owner.
								}
								if(p.getName().contains("[S]")){
									continue; // Shop.
								}
								if(p.getLocation().add(0, 1, 0).getBlock().getType() != Material.AIR){
									continue; // No space.
								}
								if(TeleportationMechanics.tp_map.containsKey(p.getName())){
									continue;
								}
								if(p.hasMetadata("NPC")){
									continue;
								}
								if(p.getPlayerListName().equalsIgnoreCase("")){
									continue;
								}
								partner = ent;
								break;
							}
						}

						if(zombie_eating.containsKey(pet)){
							partner = zombie_eating.get(pet);
							if(!partner.getWorld().getName().equalsIgnoreCase(pet.getWorld().getName())){
								partner = null;
								zombie_eating.remove(pet);
							}
						}

						partner = null;
						if(partner == null){continue;}


						LivingEntity le = (LivingEntity)pet;

						walkTo(le, partner.getLocation().getX(), partner.getLocation().getY(), partner.getLocation().getZ(), 1.20F);
						zombie_eating.put(pet, partner);

						if(le.getLocation().distanceSquared(partner.getLocation()) <= 4){	        			   
							//walkTo(le, partner.getLocation().getX(), partner.getLocation().getY(), partner.getLocation().getZ(), 1.20F);
							EntityCreature ec = (EntityCreature) ((CraftEntity) pet).getHandle();
							CraftPlayer cp = (CraftPlayer)partner;
							if(!pet.isInsideVehicle() && !(cp.isInsideVehicle())){
								ec.mount((net.minecraft.server.v1_7_R1.Entity)cp.getHandle());
							}

							//double x = partner.getLocation().getX(); //(pet.getLocation().getX() + partner.getLocation().getX()) / 2;
							//double y = partner.getLocation().getY(); //((pet.getLocation().getY() + partner.getLocation().getY()) / 2);
							//double z = partner.getLocation().getZ(); //(pet.getLocation().getZ() + partner.getLocation().getZ()) / 2;

							try {
								ParticleEffect.sendToLocation(ParticleEffect.HEART, partner.getLocation().add(0, 2, 0), new Random().nextFloat(), new Random().nextFloat(), new Random().nextFloat(), 1.0F, 1);
							} catch (Exception err) {err.printStackTrace();}
							
							//partner.getWorld().spawnParticle(partner.getLocation().add(0, 2, 0), Particle.HEART, 1.0F, 1);

						}
					}
				}

			}
		}
		, 10 * 20L, 15L);

		log.info("[PetMechanics] has been enabled.");
	}

	public void onDisable() {
		log.info("[PetMechanics] has been disabled.");
	}

	public int getChickCount(Entity e){
		if(!(chicken_count.containsKey(e))){
			return 0;
		}
		else{
			return chicken_count.get(e);
		}
	}

	public void setChickCount(Entity e, int amount, boolean add_one){
		int cur_amount = getChickCount(e);
		if(add_one == true){
			chicken_count.put(e, cur_amount+1);
		}
		else{
			chicken_count.put(e, amount);
		}
	}

	public void addBabyChicken(Entity mother, Entity baby){
		List<Entity> baby_list = new ArrayList<Entity>();
		if(baby_chick_map.containsKey(mother)){
			baby_list = baby_chick_map.get(mother);
		}

		baby_list.add(baby);
		baby_chick_map.put(mother, baby_list);
	}

	@EventHandler
	public void onProjectileHitEvent(ProjectileHitEvent e){
		if(e.getEntity() instanceof Egg){
			Entity e_egg = e.getEntity();
			if(chicken_eggs.containsKey(e.getEntity())){
				// Hatch it!
				Entity mother = chicken_eggs.get(e.getEntity());
				Chicken c = (Chicken)e_egg.getWorld().spawnEntity(e_egg.getLocation().add(0, 1, 0), EntityType.CHICKEN);
				c.setBaby();
				c.setAgeLock(true);
				addBabyChicken(mother, c);
			}
		}
	}

	@EventHandler
	public void onSheepEatGrass(EntityChangeBlockEvent e) {
		EntityType a = EntityType.SHEEP;
		if (e.getEntity().getType() == a) {
			if (e.getTo() == Material.DIRT) {
				e.setCancelled(true);
			}
		}
	}

	public static void addPetToPlayer(String p_name, String pet){
		List<String> pet_list = downloadPetData(p_name);
		String pet_string = "";

		for(String s : pet_list){
			pet_string = pet_string + s + ",";
		}

		if(pet_list.contains(pet)){
			return; // Already in list.
		}

		pet_string = pet_string + pet + ",";
		if(pet_string.endsWith(",")){
			pet_string = pet_string.substring(0, pet_string.length() - 1);
		}

		StringEscapeUtils.escapeSql(pet_string); 

		try {
			PreparedStatement pst = ConnectionPool.getConneciton().prepareStatement( 
					"INSERT INTO player_database (p_name, pets)"
							+ " VALUES"
							+ "('" + p_name + "', '" + pet_string +"') ON DUPLICATE KEY UPDATE pets = '" + pet_string + "'");

			pst.executeUpdate();

			if (pst != null) {
				pst.close();
			}

		} catch (SQLException ex) {
			log.log(Level.SEVERE, ex.getMessage(), ex);
		}

		if(Bukkit.getPlayer(p_name) != null){
			Player pl = Bukkit.getPlayer(p_name);
			if(pl.getInventory().firstEmpty() != -1){
				// Add egg.
				if(pet.equalsIgnoreCase("baby_zombie")){
					if(!(inEcashStorage(pl, pet)) && !(PetMechanics.containsPet(pl.getInventory(), pet)) && !(PetMechanics.containsPet(MoneyMechanics.bank_contents.get(pl.getName()), pet))){
						if(pl.getInventory().firstEmpty() != -1){
							pl.getInventory().addItem(PetMechanics.generatePetEgg(EntityType.ZOMBIE, ""));
						}
					}
				}
				if(pet.equalsIgnoreCase("baby_mooshroom")){
					if(!(inEcashStorage(pl, pet)) && !(PetMechanics.containsPet(pl.getInventory(), pet)) && !(PetMechanics.containsPet(MoneyMechanics.bank_contents.get(pl.getName()), pet))){
						if(pl.getInventory().firstEmpty() != -1){
							pl.getInventory().addItem(PetMechanics.generatePetEgg(EntityType.MUSHROOM_COW, ""));
						}
					}
				}
				if(pet.equalsIgnoreCase("baby_cat")){
					if(!(inEcashStorage(pl, pet)) && !(PetMechanics.containsPet(pl.getInventory(), pet)) && !(PetMechanics.containsPet(MoneyMechanics.bank_contents.get(pl.getName()), pet))){
						if(pl.getInventory().firstEmpty() != -1){
							pl.getInventory().addItem(PetMechanics.generatePetEgg(EntityType.OCELOT, ""));
						}
					}
				}
				if(pet.equalsIgnoreCase("lucky_baby_sheep")){
					if(!(inEcashStorage(pl, pet)) && !(PetMechanics.containsPet(pl.getInventory(), pet)) && !(PetMechanics.containsPet(MoneyMechanics.bank_contents.get(pl.getName()), pet))){
						if(pl.getInventory().firstEmpty() != -1){
							pl.getInventory().addItem(PetMechanics.generatePetEgg(EntityType.SHEEP, "green"));
						}
					}
				}
				if(pet.equalsIgnoreCase("easter_chicken")){
					if(!(inEcashStorage(pl, pet)) && !(PetMechanics.containsPet(pl.getInventory(), pet)) && !(PetMechanics.containsPet(MoneyMechanics.bank_contents.get(pl.getName()), pet))){
						if(pl.getInventory().firstEmpty() != -1){
							pl.getInventory().addItem(PetMechanics.generatePetEgg(EntityType.CHICKEN, ""));
						}
					}
				}
				if(pet.equalsIgnoreCase("april_creeper")){
					if(!(inEcashStorage(pl, pet)) && !(PetMechanics.containsPet(pl.getInventory(), pet)) && !(PetMechanics.containsPet(MoneyMechanics.bank_contents.get(pl.getName()), pet))){
						if(pl.getInventory().firstEmpty() != -1){
							pl.getInventory().addItem(PetMechanics.generatePetEgg(EntityType.CREEPER, ""));
						}
					}
				}
				if(pet.equalsIgnoreCase("beta_slime")){
					if(!(inEcashStorage(pl, pet)) && !(PetMechanics.containsPet(pl.getInventory(), pet)) && !(PetMechanics.containsPet(MoneyMechanics.bank_contents.get(pl.getName()), pet))){
						if(pl.getInventory().firstEmpty() != -1){
							pl.getInventory().addItem(PetMechanics.generatePetEgg(EntityType.SLIME, ""));
						}
					}
				}
				if(pet.equalsIgnoreCase("july_creeper")){
					if(!(inEcashStorage(pl, pet)) && !(PetMechanics.containsPet(pl.getInventory(), pet)) && !(PetMechanics.containsPet(MoneyMechanics.bank_contents.get(pl.getName()), pet))){
						if(pl.getInventory().firstEmpty() != -1){
							pl.getInventory().addItem(PetMechanics.generatePetEgg(EntityType.CREEPER, "july"));
						}
					}
				}
				if(pet.equalsIgnoreCase("baby_horse")){
					if(!(inEcashStorage(pl, pet)) && !(PetMechanics.containsPet(pl.getInventory(), pet)) && !(PetMechanics.containsPet(MoneyMechanics.bank_contents.get(pl.getName()), pet))){
						if(pl.getInventory().firstEmpty() != -1){
							pl.getInventory().addItem(PetMechanics.generatePetEgg(EntityType.HORSE, ""));
						}
					}
				}
				if(pet.equalsIgnoreCase("spooky_bat")){
					if(!(inEcashStorage(pl, pet)) && !(PetMechanics.containsPet(pl.getInventory(), pet)) && !(PetMechanics.containsPet(MoneyMechanics.bank_contents.get(pl.getName()), pet))){
						if(pl.getInventory().firstEmpty() != -1){
							pl.getInventory().addItem(PetMechanics.generatePetEgg(EntityType.BAT, ""));
						}
					}
				}
			}
		}
	}

	public static List<String> downloadPetData(String pname){
		if(player_pets.containsKey(pname)){
			return player_pets.get(pname);
		}

		PreparedStatement pst = null;
		List<String> pet_data = new ArrayList<String>();

		try {
			//con = DriverManager.getConnection(Hive.sql_url, Hive.sql_user, Hive.sql_password);

			pst = ConnectionPool.getConneciton().prepareStatement(
					"SELECT pets FROM player_database WHERE p_name = '" + pname + "'");

			pst.execute();
			ResultSet rs = pst.getResultSet();
			if(!rs.next()){return pet_data;}
			String pet_list = rs.getString("pets");
			if(pet_list == null || !pet_list.contains(",")){
				// Just return a blank list.
				if(pet_list != null){
					pet_data.add(pet_list); // Only 1 pet maybe?
				}
				return pet_data;
			}
			for(String s : pet_list.split(",")){
				pet_data.add(s);
			}
			return pet_data;

		} catch (SQLException ex) {
			log.log(Level.SEVERE, ex.getMessage(), ex);
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return null;

		} finally {
			try {
				if (pst != null) {
					pst.close();
				}

			} catch (SQLException ex) {
				log.log(Level.WARNING, ex.getMessage(), ex);
			}
		}

	}

	public void loadPetMessageTemplates(){
		int count = 0;

		try {
			for(File f : templatePath.listFiles()){
				List<String> lmsg_template = new ArrayList<String>();
				String tn = f.getName();
				if(tn.endsWith(".msg")){
					BufferedReader reader = new BufferedReader(new FileReader(f));
					String line = "";
					while((line = reader.readLine()) != null){
						lmsg_template.add(line);
					}
					reader.close();
				}
				phrase_list.put(tn.replaceAll(".msg", ""), lmsg_template);
				count++;
			}

			log.info("[PetMechanics] " + count + " PET MESSAGES TEMPLATE profiles have been LOADED.");
		}
		catch (IOException ioe){
			ioe.printStackTrace();
		}
	}

	private boolean walkTo(LivingEntity livingEntity, double x, double y, double z, float speed) {
		return ((EntityInsentient) ((CraftLivingEntity)livingEntity).getHandle()).getNavigation().a(x, y, z, speed);
	}

	public static ItemStack generatePetEgg(EntityType et, String s_meta_data){
		short meta_data = (short) et.ordinal();
		String pet_name = "";
		if(et == EntityType.ZOMBIE){
			pet_name = "Baby Zombie";
			meta_data = 54;
		}
		else if(et == EntityType.MUSHROOM_COW){
			pet_name = "Baby Mooshroom";
			meta_data = 96;
		}
		else if(et == EntityType.OCELOT){
			pet_name = "Baby Cat";
			meta_data = 98;
		}
		else if(et == EntityType.SHEEP){
			if(s_meta_data.equalsIgnoreCase("green")){
				pet_name = "Sheep O' Luck";
				meta_data = 50;
				// Creeper spawner egg ID
			}
		}
		else if(et == EntityType.CHICKEN){
			pet_name = "Easter's Chicken";
			meta_data = 93;
		}
		else if(et == EntityType.CREEPER){
			if(s_meta_data.equalsIgnoreCase("")){
				pet_name = "Jeepers Creepers";
				meta_data = 55;
			}
			else if(s_meta_data.equalsIgnoreCase("july")){
				pet_name = "Creeper of Independence";
				meta_data = 62;
			}
		}
		else if(et == EntityType.HORSE){
			if(s_meta_data.equalsIgnoreCase("")){
				pet_name = "Baby Horses";
				meta_data = 100;
			}
		}
		else if(et == EntityType.SLIME){
			pet_name = "Beta Slime";
			meta_data = 66;
		}
		else if(et == EntityType.BAT){
			pet_name = "Spooky Bats";
			meta_data = 65;
		}
		return ItemMechanics.signCustomItem(Material.MONSTER_EGG, meta_data, ChatColor.GREEN.toString() + "??? " + ChatColor.GRAY.toString() + "[" + pet_name + "]", ChatColor.GREEN.toString() + "Right Click: " + ChatColor.GRAY.toString() + "Summon / Dismiss Pet" + "," + ChatColor.GREEN.toString() + "Left Click: " + ChatColor.GRAY.toString() + "Rename Pet" + "," + ChatColor.GRAY.toString() + "Permanent Untradeable");
	}

	public static String getPetType(ItemStack is){
		CraftItemStack css = (CraftItemStack)is;
		String uncut_name = CraftItemStack.asNMSCopy(css).getTag().getCompound("display").getString("Name");
		String format_name = ChatColor.stripColor(uncut_name.substring(uncut_name.indexOf("[") + 1, uncut_name.lastIndexOf("]")));
		return format_name;
	}

	public static Entity spawnRandomHorse(Location loc, boolean baby){
		Entity eh = loc.getWorld().spawnEntity(loc, EntityType.HORSE);
		Horse h = (Horse)eh;

		if(baby){
			h.setBaby();
		}

		org.bukkit.entity.Horse.Color hcolor = horse_color_list.get(new Random().nextInt(horse_color_list.size() - 1));
		h.setColor(hcolor);

		Style hstyle = horse_style_list.get(new Random().nextInt(horse_style_list.size() - 1));
		h.setStyle(hstyle);

		return h;
	}

	public String getDBPetType(String formal_name){
		if(formal_name.equalsIgnoreCase("baby zombie")){
			return "baby_zombie";
		}
		if(formal_name.equalsIgnoreCase("baby mooshroom")){
			return "baby_mooshroom";
		}
		if(formal_name.equalsIgnoreCase("baby cat")){
			return "baby_cat";
		}
		if(formal_name.equalsIgnoreCase("sheep o' luck")){
			return "lucky_baby_sheep";
		}
		if(formal_name.equalsIgnoreCase("easter's chicken")){
			return "easter_chicken";
		}
		if(formal_name.equalsIgnoreCase("Jeepers Creepers")){
			return "april_creeper";
		}
		if(formal_name.equalsIgnoreCase("Beta Slime")){
			return "beta_slime";
		}
		if(formal_name.equalsIgnoreCase("Creeper of Independence")){
			return "july_creeper";
		}
		if(formal_name.equalsIgnoreCase("Baby Horses")){
			return "baby_horse";
		}
		if(formal_name.equalsIgnoreCase("Spooky Bats")){
			return "spooky_bat";
		}
		return formal_name.replaceAll(" ", "");
	}

	public String getPetType(Entity e){
		if(e == null){
			return "???";
		}
		if(e.getType() == EntityType.ZOMBIE){
			return "Baby Zombie";
		}
		if(e.getType() == EntityType.MUSHROOM_COW){
			return "Baby Mooshroom";
		}
		if(e.getType() == EntityType.OCELOT){
			return "Baby Cat";
		}
		if(e.getType() == EntityType.SHEEP){
			return "Sheep O' Luck";
		}
		if(e.getType() == EntityType.CHICKEN){
			return "Easter's Chicken";
		}
		if(e.getType() == EntityType.CREEPER && !((Creeper)e).isPowered()){
			return "Jeepers Creepers";
		}
		if(e.getType() == EntityType.GIANT){
			return "Giant";
		}
		if(e.getType() == EntityType.SLIME){
			return "Beta Slime";
		}
		if(e.getType() == EntityType.CREEPER && ((Creeper)e).isPowered()){
			return "Creeper of Independence";
		}
		if(e.getType() == EntityType.HORSE){
			return "Baby Horses";
		}
		if(e.getType() == EntityType.BAT){
			return "Spooky Bats";
		}
		return "???";
	}

	public String getPetName(ItemStack is){
		String raw_name = is.getItemMeta().getDisplayName();
		String pet_name = ChatColor.stripColor(raw_name);
		pet_name = pet_name.substring(0, pet_name.indexOf(" "));
		return pet_name.replaceAll(" ", "");
	}

	public String getPetName(Entity e){
		try{
			String raw_name = e.getMetadata("petname").get(0).asString();
			String pet_name = ChatColor.stripColor(raw_name);
			if(pet_name.endsWith(" ")){
				pet_name = pet_name.substring(0, pet_name.length() - 1);
			}
			return pet_name.replaceAll(" ", "");
		} catch(Exception err){
			err.printStackTrace();
			return "???";
		}
	}

	public static boolean isPermUntradeable(ItemStack is){
		try{
			List<String> att_list = is.getItemMeta().getLore();
			for(String s : att_list){
				if(s.contains("Permanent Untradeable")){
					return true;
				}
			}
		} catch (Exception e){
			return false;
		}
		return false;
	}


	/*public ItemStack makePermUntradeable(ItemStack is){
		TODO
	}*/


	public String getRandomStatement(String petType){
		List<String> responses = phrase_list.get(petType);
		int pick_phrase = new Random().nextInt(responses.size());
		String phrase = responses.get(pick_phrase);
		return phrase;
	}

	public static boolean inEcashStorage(Player pl, String pet_type){
		short lf_meta_data = -1;

		if(!(EcashMechanics.ecash_storage_map.containsKey(pl.getName()))){
			return false;
		}

		Inventory ecash_storage_inv = Hive.convertStringToInventory(null, EcashMechanics.ecash_storage_map.get(pl.getName()), "E-Cash Storage", 54);

		if(pet_type.equalsIgnoreCase("baby_zombie")){
			lf_meta_data = 54;
		}
		if(pet_type.equalsIgnoreCase("baby_mooshroom")){
			lf_meta_data = 96;
		}
		if(pet_type.equalsIgnoreCase("baby_cat")){
			lf_meta_data = 98;
		}
		if(pet_type.equalsIgnoreCase("lucky_baby_sheep")){
			lf_meta_data = 50;
		}
		if(pet_type.equalsIgnoreCase("easter_chicken")){
			lf_meta_data = 93;
		}
		if(pet_type.equalsIgnoreCase("april_creeper")){
			lf_meta_data = 55;
		}
		if(pet_type.equalsIgnoreCase("july_creeper")){
			lf_meta_data = 62;
		}
		if(pet_type.equalsIgnoreCase("beta_slime")){
			lf_meta_data = 66;
		}
		if(pet_type.equalsIgnoreCase("baby_horse")){
			lf_meta_data = 100;
		}
		if(pet_type.equalsIgnoreCase("spooky_bat")){
			lf_meta_data = 65;
		}

		for(ItemStack is : ecash_storage_inv){
			if(is == null || is.getType() == Material.AIR){continue;}
			if(!(is.getType() == Material.MONSTER_EGG)){continue;}

			short meta_data = is.getDurability();
			//log.info("" + meta_data);
			if(meta_data == lf_meta_data){
				return true;
			}
		}
		
		return false;
	}

	public static boolean containsPet(List<Inventory> to_check, String pet_type){  
		short lf_meta_data = -1;
		if(pet_type.equalsIgnoreCase("baby_zombie")){
			lf_meta_data = 54;
		}
		if(pet_type.equalsIgnoreCase("baby_mooshroom")){
			lf_meta_data = 96;
		}
		if(pet_type.equalsIgnoreCase("baby_cat")){
			lf_meta_data = 98;
		}
		if(pet_type.equalsIgnoreCase("lucky_baby_sheep")){
			lf_meta_data = 50;
		}
		if(pet_type.equalsIgnoreCase("easter_chicken")){
			lf_meta_data = 93;
		}
		if(pet_type.equalsIgnoreCase("april_creeper")){
			lf_meta_data = 55;
		}
		if(pet_type.equalsIgnoreCase("july_creeper")){
			lf_meta_data = 62;
		}
		if(pet_type.equalsIgnoreCase("beta_slime")){
			lf_meta_data = 66;
		}
		if(pet_type.equalsIgnoreCase("baby_horse")){
			lf_meta_data = 100;
		}
		if(pet_type.equalsIgnoreCase("spooky_bat")){
			lf_meta_data = 65;
		}

		for(Inventory inv : to_check){
			for(ItemStack is : inv){
				if(is == null || is.getType() == Material.AIR){continue;}
				if(!(is.getType() == Material.MONSTER_EGG)){continue;}

				short meta_data = is.getDurability();
				//log.info("" + meta_data);
				if(meta_data == lf_meta_data){
					return true;
				}
			}
		}

		return false;
	}

	public static boolean containsPet(Inventory to_check, String pet_type){  
		short lf_meta_data = -1;
		if(pet_type.equalsIgnoreCase("baby_zombie")){
			lf_meta_data = 54;
		}
		if(pet_type.equalsIgnoreCase("baby_mooshroom")){
			lf_meta_data = 96;
		}
		if(pet_type.equalsIgnoreCase("baby_cat")){
			lf_meta_data = 98;
		}
		if(pet_type.equalsIgnoreCase("lucky_baby_sheep")){
			lf_meta_data = 50;
		}
		if(pet_type.equalsIgnoreCase("easter_chicken")){
			lf_meta_data = 93;
		}
		if(pet_type.equalsIgnoreCase("april_creeper")){
			lf_meta_data = 55;
		}
		if(pet_type.equalsIgnoreCase("july_creeper")){
			lf_meta_data = 62;
		}
		if(pet_type.equalsIgnoreCase("beta_slime")){
			lf_meta_data = 66;
		}
		if(pet_type.equalsIgnoreCase("baby_horse")){
			lf_meta_data = 100;
		}
		if(pet_type.equalsIgnoreCase("spooky_bat")){
			lf_meta_data = 65;
		}
		for(ItemStack is : to_check.getContents()){
			if(is == null || is.getType() == Material.AIR){continue;}
			if(!(is.getType() == Material.MONSTER_EGG)){continue;}

			short meta_data = is.getDurability();
			//log.info("" + meta_data);
			if(meta_data == lf_meta_data){
				return true;
			}
		}

		return false;
	}

	public static void removeDuplicatePetEggs(Inventory inv){
		List<String> pet_names = new ArrayList<String>();
		for(Entry<Integer, ? extends ItemStack> data : inv.all(Material.MONSTER_EGG).entrySet()){
			ItemStack is = data.getValue();
			int index = data.getKey();

			if(is.hasItemMeta() && is.getItemMeta().hasDisplayName()){
				String type = getPetType(is);
				if(pet_names.contains(type)){
					// Already exists, remove duplicate.
					inv.setItem(index, new ItemStack(Material.AIR));
					continue;
				}
				pet_names.add(type);
			}
		}
	}

	@EventHandler
	public void onInventoryClickEvent(InventoryClickEvent e){
		String inv_name = e.getInventory().getName();
		Player p = (Player)e.getWhoClicked();
		if(inv_name.contains("@") || inv_name.contains(p.getName())){
			if(e.getCurrentItem() != null){
				ItemStack cur_item = e.getCurrentItem();
				if(isPermUntradeable(cur_item) || !(RealmMechanics.isItemTradeable(cur_item))){
					e.setCancelled(true);
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerJoinEvent(PlayerJoinEvent e){
		final Player p = e.getPlayer();

		pet_map.put(p.getName(), new ArrayList<Entity>());

		if(!(player_pets.containsKey(p.getName()))){
			return;
		}

		//log.info("[PetMechanics] player_pets: " + player_pets.get(p.getName()));

		int index = -1;
		for(ItemStack is : p.getInventory()){
			index++;
			if(is == null){continue;}

			if(is.getType() == Material.MONSTER_EGG){
				if(is.getDurability() == (short)55){
					if(is.hasItemMeta() && is.getItemMeta().getDisplayName().contains("Creeper of Independence")){
						p.getInventory().setItem(index, new ItemStack(Material.AIR));
					}
				}
			}
		}

		Main.plugin.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
			public void run() {
				if(!(player_pets.containsKey(p.getName()))){
					// They may have logged out or d/c'd within these 10 ticks.
					return;
				}
				for(String s : player_pets.get(p.getName())){
					//log.info(s);
					if(s.equalsIgnoreCase("baby_zombie")){
						//ItemStack beta_pet = new ItemStack(Material.MONSTER_EGG, 1, (short)54);
						if(!(inEcashStorage(p, s)) && !(containsPet(p.getInventory(), s)) && !(containsPet(MoneyMechanics.bank_contents.get(p.getName()), s))){
							if(p.getInventory().firstEmpty() != -1){
								p.getInventory().addItem(generatePetEgg(EntityType.ZOMBIE, ""));
							}
						}
					}
					if(s.equalsIgnoreCase("baby_mooshroom")){
						//ItemStack pet = new ItemStack(Material.MONSTER_EGG, 1, (short)96);
						if(!(inEcashStorage(p, s)) && !(containsPet(p.getInventory(), s)) && !(containsPet(MoneyMechanics.bank_contents.get(p.getName()), s))){
							if(p.getInventory().firstEmpty() != -1){
								p.getInventory().addItem(generatePetEgg(EntityType.MUSHROOM_COW, ""));
							}
						}
					}
					if(s.equalsIgnoreCase("baby_cat")){
						//ItemStack pet = new ItemStack(Material.MONSTER_EGG, 1, (short)96);
						if(!(inEcashStorage(p, s)) && !(containsPet(p.getInventory(), s)) && !(containsPet(MoneyMechanics.bank_contents.get(p.getName()), s))){
							if(p.getInventory().firstEmpty() != -1){
								p.getInventory().addItem(generatePetEgg(EntityType.OCELOT, ""));
							}
						}
					}
					if(s.equalsIgnoreCase("lucky_baby_sheep")){
						//ItemStack pet = new ItemStack(Material.MONSTER_EGG, 1, (short)96);
						if(!(inEcashStorage(p, s)) && !(containsPet(p.getInventory(), s)) && !(containsPet(MoneyMechanics.bank_contents.get(p.getName()), s))){
							if(p.getInventory().firstEmpty() != -1){
								p.getInventory().addItem(generatePetEgg(EntityType.SHEEP, "green"));
							}
						}
					}
					if(s.equalsIgnoreCase("easter_chicken")){
						//ItemStack pet = new ItemStack(Material.MONSTER_EGG, 1, (short)96);
						if(!(inEcashStorage(p, s)) && !(containsPet(p.getInventory(), s)) && !(containsPet(MoneyMechanics.bank_contents.get(p.getName()), s))){
							if(p.getInventory().firstEmpty() != -1){
								p.getInventory().addItem(generatePetEgg(EntityType.CHICKEN, ""));
							}
						}
					}
					if(s.equalsIgnoreCase("april_creeper")){
						//ItemStack pet = new ItemStack(Material.MONSTER_EGG, 1, (short)96);
						if(!(inEcashStorage(p, s)) && !(containsPet(p.getInventory(), s)) && !(containsPet(MoneyMechanics.bank_contents.get(p.getName()), s))){
							if(p.getInventory().firstEmpty() != -1){
								p.getInventory().addItem(generatePetEgg(EntityType.CREEPER, ""));
							}
						}
					}
					if(s.equalsIgnoreCase("beta_slime")){
						//ItemStack pet = new ItemStack(Material.MONSTER_EGG, 1, (short)96);
						if(!(inEcashStorage(p, s)) && !(containsPet(p.getInventory(), s)) && !(containsPet(MoneyMechanics.bank_contents.get(p.getName()), s))){
							if(p.getInventory().firstEmpty() != -1){
								p.getInventory().addItem(generatePetEgg(EntityType.SLIME, ""));
							}
						}
					}
					if(s.equalsIgnoreCase("july_creeper")){
						//ItemStack pet = new ItemStack(Material.MONSTER_EGG, 1, (short)96);
						if(!(inEcashStorage(p, s)) && !(containsPet(p.getInventory(), s)) && !(containsPet(MoneyMechanics.bank_contents.get(p.getName()), s))){
							if(p.getInventory().firstEmpty() != -1){
								p.getInventory().addItem(generatePetEgg(EntityType.CREEPER, "july"));
							}
						}
					}
					if(s.equalsIgnoreCase("baby_horse")){
						//ItemStack pet = new ItemStack(Material.MONSTER_EGG, 1, (short)96);
						if(!(inEcashStorage(p, s)) && !(containsPet(p.getInventory(), s)) && !(containsPet(MoneyMechanics.bank_contents.get(p.getName()), s))){
							if(p.getInventory().firstEmpty() != -1){
								p.getInventory().addItem(generatePetEgg(EntityType.HORSE, ""));
							}
						}
					}
					if(s.equalsIgnoreCase("spooky_bat")){
						//ItemStack pet = new ItemStack(Material.MONSTER_EGG, 1, (short)96);
						if(!(inEcashStorage(p, s)) && !(containsPet(p.getInventory(), s)) && !(containsPet(MoneyMechanics.bank_contents.get(p.getName()), s))){
							if(p.getInventory().firstEmpty() != -1){
								p.getInventory().addItem(generatePetEgg(EntityType.BAT, ""));
							}
						}
					}
				}

				if(Bukkit.getPlayer(p.getName()) != null){
					removeDuplicatePetEggs(Bukkit.getPlayer(p.getName()).getInventory());
					Bukkit.getPlayer(p.getName()).updateInventory();
				}
			}
		}, 10L);

	}


	@EventHandler
	public void onPlayerQuitEvent(PlayerQuitEvent e){
		Player p = e.getPlayer();
		if(p.getPassenger() != null){
			Entity ent = p.getPassenger();
			if(ent.getType() == EntityType.ZOMBIE){
				zombie_eating.remove(ent);
			}
			p.eject();
		}
		player_pets.remove(e.getPlayer().getName());
	}

	@EventHandler
	public void onEntityPortalEnterEvent(EntityPortalEnterEvent e){
		if(!(e.getEntity() instanceof Player)){
			Entity ent = e.getEntity();
			if(inv_pet_map.containsKey(ent)){
				String owner_name = inv_pet_map.get(ent);
				Player owner = Bukkit.getPlayer(owner_name);
				if(owner != null){
					ent.teleport(owner);
				}
			}
		}
	}

	public void addPetToSpawnedList(String p_name, Entity pet){
		if(!(pet_map.containsKey(p_name))){
			return; // They're not even in map, so.
		}
		List<Entity> pet_list = pet_map.get(p_name);
		if(!(pet_list.contains(pet))){
			pet_list.add(pet);
		}
		pet_map.put(p_name, pet_list);
	}

	public void removePetFromSpawnedList(String p_name, Entity pet){
		List<Entity> pet_list = pet_map.get(p_name);
		if((pet_list.contains(pet))){
			pet_list.remove(pet);
		}
		pet_map.put(p_name, pet_list);
	}

	@EventHandler
	public void onPlayerChangeWorldEvent(PlayerChangedWorldEvent e){
		final Player p = e.getPlayer();
		if(pet_map.containsKey(p.getName())){
			CopyOnWriteArrayList<Entity> ent_list = new CopyOnWriteArrayList<Entity>(pet_map.get(p.getName()));
			for(final Entity ent : ent_list){
				final String old_name = ChatMechanics.censorMessage(getPetName(ent));
				inv_pet_map.remove(ent);
				ent.remove();
				removePetFromSpawnedList(p.getName(), ent);
				Main.plugin.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
					public void run() {
						// After a second passes and they're in the new world, give them a new pet.
						String lptt = getPetType(ent);
						Entity new_e = null;
						if(lptt.equalsIgnoreCase("Baby Zombie")){
							new_e = p.getWorld().spawnEntity(p.getLocation().add(0, 2, 0), EntityType.ZOMBIE);
							Zombie z = (Zombie)new_e;

							z.setCustomName(old_name);
							z.setCustomNameVisible(false);

							z.setBaby(true);
							//z.setCustomName(old_name);
							new_e.setMetadata("petname", new FixedMetadataValue(Main.plugin, old_name));
						}
						if(lptt.equalsIgnoreCase("Baby Mooshroom")){
							new_e = p.getWorld().spawnEntity(p.getLocation().add(0, 2, 0), EntityType.MUSHROOM_COW);
							MushroomCow ms = (MushroomCow)new_e;
							ms.setBaby();
							ms.setAgeLock(true);
							ms.setCustomName(old_name);
							p.playSound(p.getLocation(), Sound.COW_IDLE, 1F, 1F);
							new_e.setMetadata("petname", new FixedMetadataValue(Main.plugin, old_name));
						}
						if(lptt.equalsIgnoreCase("Baby Cat")){
							new_e = p.getWorld().spawnEntity(p.getLocation().add(0, 2, 0), EntityType.OCELOT);
							Ocelot oc = (Ocelot)new_e;
							oc.setBaby();
							oc.setAgeLock(true);
							oc.setTamed(true);
							oc.setOwner((AnimalTamer)p);
							oc.setCustomName(old_name);
							//oc.setSitting(false);
							p.playSound(p.getLocation(), Sound.CAT_MEOW, 1F, 1F);
							new_e.setMetadata("petname", new FixedMetadataValue(Main.plugin, old_name));
						}
						if(lptt.equalsIgnoreCase("Sheep O' Luck")){
							new_e = p.getWorld().spawnEntity(p.getLocation().add(0, 2, 0), EntityType.SHEEP);
							Sheep s = (Sheep)new_e;
							s.setBaby();
							s.setBreed(false);
							s.setAgeLock(true);
							s.setColor(DyeColor.LIME);
							s.setCustomName(old_name);
							//oc.setSitting(false);
							p.playSound(p.getLocation(), Sound.SHEEP_IDLE, 1F, 1F);
							new_e.setMetadata("petname", new FixedMetadataValue(Main.plugin, old_name));
						}
						if(lptt.equalsIgnoreCase("Easter's Chicken")){
							new_e = p.getWorld().spawnEntity(p.getLocation().add(0, 2, 0), EntityType.CHICKEN);
							Chicken c = (Chicken)new_e;
							c.setBreed(false);
							c.setCustomName(old_name);
							//oc.setSitting(false);
							p.playSound(p.getLocation(), Sound.CHICKEN_IDLE, 1F, 1F);
							new_e.setMetadata("petname", new FixedMetadataValue(Main.plugin, old_name));
						}
						if(lptt.equalsIgnoreCase("Jeepers Creepers")){
							new_e = p.getWorld().spawnEntity(p.getLocation().add(0, 2, 0), EntityType.CREEPER);
							Creeper cr = (Creeper)new_e;
							cr.setCanPickupItems(false);
							cr.setCustomName(old_name);
							//oc.setSitting(false);
							p.playSound(p.getLocation(), Sound.CREEPER_HISS, 1F, 1F);
							new_e.setMetadata("petname", new FixedMetadataValue(Main.plugin, old_name));
						}
						if(lptt.equalsIgnoreCase("Beta Slime")){
							new_e = p.getWorld().spawnEntity(p.getLocation().add(0, 2, 0), EntityType.SLIME);
							Slime s = (Slime)new_e;
							s.setSize(1);
							s.setCanPickupItems(false);
							s.setCustomName(old_name);
							p.playSound(p.getLocation(), Sound.SLIME_WALK, 1F, 1F);
							new_e.setMetadata("petname", new FixedMetadataValue(Main.plugin, old_name));
						}
						if(lptt.equalsIgnoreCase("Creeper of Independence")){
							new_e = p.getWorld().spawnEntity(p.getLocation().add(0, 2, 0), EntityType.CREEPER);
							Creeper cr = (Creeper)new_e;
							cr.setCanPickupItems(false);
							cr.setCustomName(old_name);
							cr.setPowered(true);
							p.playSound(p.getLocation(), Sound.CREEPER_HISS, 0.5F, 1F);
							new_e.setMetadata("petname", new FixedMetadataValue(Main.plugin, old_name));
						}
						/*if(lptt.equalsIgnoreCase("Spooky Bats")){
							for(int x = 3; x>0; x--){
								new_e = p.getWorld().spawnEntity(p.getLocation().add(0, 2, 0), EntityType.BAT);
								Bat b = (Bat)new_e;

								b.setCanPickupItems(false);
								b.setCustomName(old_name);
								new_e.setMetadata("petname", new FixedMetadataValue(instance, old_name));
								inv_pet_map.put(new_e, p.getName());
								addPetToSpawnedList(p.getName(), new_e);
							}

							p.playSound(p.getLocation(), Sound.BAT_TAKEOFF, 0.5F, 1F);
						}*/
						if(lptt.equalsIgnoreCase("Baby Horses")){
							Location loc = p.getLocation().add(0, 2, 0);

							//for(int x = 0; x >= 3; x++){
							Horse h1 = (Horse)spawnRandomHorse(loc, true);
							h1.setCanPickupItems(false);
							h1.setCustomName(old_name);
							h1.setTamed(true);
							h1.setOwner((AnimalTamer)p);
							h1.setTarget(p);

							((Entity)h1).setMetadata("petname", new FixedMetadataValue(Main.plugin, old_name));
							inv_pet_map.put((Entity)h1, p.getName());
							addPetToSpawnedList(p.getName(), (Entity)h1);
							//}

							// TODO: Horse sound.
							//p.playSound(p.getLocation(), Sound.COW_IDLE, 0.5F, 1F);
						}

						if(new_e != null && !(inv_pet_map.containsKey(new_e))){
							inv_pet_map.put(new_e, p.getName());
							addPetToSpawnedList(p.getName(), new_e);
						}
					}
				}, 30L);
			}
		}
	}

	@EventHandler
	public void onEntityTarget(EntityTargetEvent e){
		Entity ent = e.getEntity();
		if(ent.getPassenger() != null){
			ent = ent.getPassenger();
		}
		if(inv_pet_map.containsKey(ent)){
			e.setCancelled(true);
		}
	}


	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
	public void onEntityDamageEvent(EntityDamageEvent e){
		Entity ent = e.getEntity();
		if(ent.getPassenger() != null){
			ent = ent.getPassenger();
		}
		if(inv_pet_map.containsKey(ent) || (ent.getType() == EntityType.CHICKEN && !((Chicken)ent).isAdult())){ // The entity being attacked is a pet.
			e.setCancelled(true);
			e.setDamage(0);
		}

		if(e instanceof EntityDamageByEntityEvent){
			EntityDamageByEntityEvent edbee = (EntityDamageByEntityEvent)e;
			Entity attacker = edbee.getDamager();
			if(inv_pet_map.containsKey(attacker)){ // The entity attacking is a pet.
				e.setCancelled(true);
				e.setDamage(0);
			}
		}

	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e){
		String p_name = e.getPlayer().getName();
		if(pet_map.containsKey(p_name)){
			List<Entity> pet_list = pet_map.get(p_name);
			for(Entity pet : pet_list){
				if(pet == null){
					continue;
				}
				pet.remove();
				inv_pet_map.remove(pet);
			}
			pet_map.remove(p_name);
		}
	}


	@EventHandler
	public void onEntityInteract(PlayerInteractEntityEvent e){
		Entity ent = e.getRightClicked();
		if(ent.getPassenger() != null){
			ent = ent.getPassenger();
		}
		if(inv_pet_map.containsKey(ent)){
			Player p = e.getPlayer();
			String owner_name = inv_pet_map.get(ent);
			String pet_name = getPetName(ent);
			String pet_type = getPetType(ent);

			if(pet_type.equalsIgnoreCase("Baby Cat")){
				if(p.getName().equalsIgnoreCase(owner_name)){
					Ocelot oc = (Ocelot)ent;
					if(oc.isSitting()){
						oc.setSitting(false);
					}
					else{
						oc.setSitting(true);
					}
				}
			}

			if(pet_name.endsWith(" ")){
				pet_name = pet_name.replaceAll(" ", " ");
			}

			p.sendMessage(ChatColor.GREEN + pet_name + ChatColor.GREEN + ": " + ChatColor.GRAY + getRandomStatement(pet_type));

			if(((CraftEntity)ent).getHandle() instanceof EntityCreature){
				EntityCreature ec = (EntityCreature) ((CraftEntity) ent).getHandle();
				CraftPlayer cp = (CraftPlayer)p;
				ec.setTarget((net.minecraft.server.v1_7_R1.Entity)cp.getHandle());
			}

			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e){
		String p_name = e.getEntity().getName();
		if(pet_map.containsKey(p_name)){
			List<Entity> pet_list = pet_map.get(p_name);
			for(Entity pet : pet_list){
				pet.remove();
				inv_pet_map.remove(pet);
			}
			pet_map.put(p_name, new ArrayList<Entity>());
		}
	}

	@EventHandler
	public void onAsyncPlayerChatEvent(AsyncPlayerChatEvent e){
		Player p = e.getPlayer();
		if(naming_pet.containsKey(p.getName())){
			e.setCancelled(true);
			String name = e.getMessage();
			if(name.equalsIgnoreCase("cancel")){
				p.sendMessage(ChatColor.RED + "Pet Naming - " + ChatColor.BOLD + "CANCELLED");
				if(!(pet_map.containsKey(p.getName()))){
					p.sendMessage(ChatColor.GRAY + "You cannot summon this pet until it has a name.");
				}
				naming_pet.remove(p.getName());
				return;
			}
			if(name.contains(" ") || name.contains("[") || name.contains("]")){
				p.sendMessage(ChatColor.RED + "Invalid character in name. No '[', ']', or blank spaces allowed.");
				return;
			}
			if(name.length() > 16){
				p.sendMessage(ChatColor.RED + "Name length exceeds maximum of 16 characters. [" + String.valueOf(name.length()) + "/16]");
				return;
			}

			name = ChatMechanics.censorMessage(name);

			if(name.endsWith(" ")){
				name = name.substring(0, name.length() - 1);
			}

			String lpet_type = pet_type.get(p.getName());
			Location l = naming_pet.get(p.getName());

			Entity pet = null;

			if(lpet_type.equalsIgnoreCase("Baby Zombie")){
				pet = l.getWorld().spawnEntity(l, EntityType.ZOMBIE);
				final Zombie z = (Zombie)pet;

				z.setBaby(true);
				z.setCustomName(name);

				p.playSound(p.getLocation(), Sound.ZOMBIE_HURT, 1F, 1.5F);
			}

			if(lpet_type.equalsIgnoreCase("Baby Mooshroom")){
				pet = l.getWorld().spawnEntity(l, EntityType.MUSHROOM_COW);
				MushroomCow ms = (MushroomCow)pet;
				ms.setBaby();
				ms.setAgeLock(true);
				ms.setCustomName(name);
				p.playSound(p.getLocation(), Sound.COW_IDLE, 1F, 1F);
			}

			if(lpet_type.equalsIgnoreCase("Baby Cat")){
				pet = l.getWorld().spawnEntity(l, EntityType.OCELOT);
				Ocelot oc = (Ocelot)pet;
				oc.setBaby();
				oc.setAgeLock(true);
				oc.setTamed(true);
				oc.setOwner((AnimalTamer)p);
				oc.setCustomName(name);
				//oc.setSitting(false);
				p.playSound(p.getLocation(), Sound.CAT_MEOW, 1F, 1F);
			}
			if(lpet_type.equalsIgnoreCase("Sheep O' Luck")){
				pet = p.getWorld().spawnEntity(p.getLocation().add(0, 2, 0), EntityType.SHEEP);
				Sheep s = (Sheep)pet;
				s.setBaby();
				s.setBreed(false);
				s.setAgeLock(true);
				s.setColor(DyeColor.LIME);
				s.setCustomName(name);
				p.playSound(p.getLocation(), Sound.SHEEP_IDLE, 1F, 1F);
			}
			if(lpet_type.equalsIgnoreCase("Easter's Chicken")){
				pet = p.getWorld().spawnEntity(p.getLocation().add(0, 2, 0), EntityType.CHICKEN);
				Chicken c = (Chicken)pet;
				c.setBreed(false);
				c.setCustomName(name);
				//oc.setSitting(false);
				p.playSound(p.getLocation(), Sound.CHICKEN_IDLE, 1F, 1F);
			}
			if(lpet_type.equalsIgnoreCase("Jeepers Creepers")){
				pet = p.getWorld().spawnEntity(p.getLocation().add(0, 2, 0), EntityType.CREEPER);
				Creeper cr = (Creeper)pet;
				cr.setCanPickupItems(false);
				cr.setCustomName(name);
				//oc.setSitting(false);
				p.playSound(p.getLocation(), Sound.CREEPER_HISS, 1F, 1F);
			}
			if(lpet_type.equalsIgnoreCase("Beta Slime")){
				pet = p.getWorld().spawnEntity(p.getLocation().add(0, 2, 0), EntityType.SLIME);
				Slime s = (Slime)pet;
				s.setSize(1);
				s.setCanPickupItems(false);
				s.setCustomName(name);
				//oc.setSitting(false);
				p.playSound(p.getLocation(), Sound.SLIME_WALK, 1F, 1F);
			}
			if(lpet_type.equalsIgnoreCase("Creeper of Independence")){
				pet = p.getWorld().spawnEntity(p.getLocation().add(0, 2, 0), EntityType.CREEPER);
				Creeper cr = (Creeper)pet;
				cr.setCanPickupItems(false);
				cr.setCustomName(name);
				cr.setPowered(true);
				p.playSound(p.getLocation(), Sound.CREEPER_HISS, 1F, 0.5F);
				pet.setMetadata("petname", new FixedMetadataValue(Main.plugin, name));
			}
			if(lpet_type.equalsIgnoreCase("Baby Horses")){
				Location loc = p.getLocation().add(0, 2, 0);

				Horse h1 = (Horse)spawnRandomHorse(loc, true);
				h1.setCanPickupItems(false);
				h1.setCustomName(name);
				h1.setTamed(true);
				h1.setOwner((AnimalTamer)p);
				((Entity)h1).setMetadata("petname", new FixedMetadataValue(Main.plugin, name));
				pet = (Entity)h1;

				// TODO: Horse sound.
				//p.playSound(p.getLocation(), Sound.COW_IDLE, 0.5F, 1F);
			}
			if(lpet_type.equalsIgnoreCase("Spooky Bats")){
				for(int x = 3; x>0; x--){
					pet = p.getWorld().spawnEntity(p.getLocation().add(0, 2, 0), EntityType.BAT);
					Bat b = (Bat)pet;

					b.setCanPickupItems(false);
					b.setCustomName(name);
					pet.setMetadata("petname", new FixedMetadataValue(Main.plugin, name));
					inv_pet_map.put(pet, p.getName());
					addPetToSpawnedList(p.getName(), pet);
				}

				p.playSound(p.getLocation(), Sound.BAT_TAKEOFF, 0.5F, 1F);
			}

			for(ItemStack is : p.getInventory().getContents()){
				if(is != null && is.getType() != Material.AIR && is.hasItemMeta()){
					if(is.getItemMeta().hasDisplayName() && is.getItemMeta().getDisplayName().contains(lpet_type)){
						ItemMeta im = is.getItemMeta();
						im.setDisplayName(ChatColor.GREEN.toString() + name + ChatColor.GRAY.toString() + " [" + lpet_type + "]");
						is.setItemMeta(im);
						break;
					}
				}
			}

			List<Entity> to_remove = new ArrayList<Entity>();

			for(Entity ent : pet_map.get(p.getName())){
				if(getPetType(ent).equalsIgnoreCase(lpet_type)){
					ent.remove();
					if(getPetType(ent).equalsIgnoreCase("Easter's Chicken")){
						if(baby_chick_map.containsKey(ent)){
							for(Entity baby : baby_chick_map.get(ent)){
								LivingEntity baby_le = (LivingEntity)baby;
								baby_le.damage(baby_le.getHealth());
							}
							baby_chick_map.remove(ent);
							chicken_count.remove(ent);
						}
					}
					inv_pet_map.remove(ent);
					to_remove.add(ent);
				}
			}

			for(Entity ent : to_remove){
				removePetFromSpawnedList(p.getName(), ent);
			}

			if(!(inv_pet_map.containsKey(pet))){
				addPetToSpawnedList(p.getName(), pet);
				inv_pet_map.put(pet, p.getName());
			}

			naming_pet.remove(p.getName());

			if(((CraftEntity)pet).getHandle() instanceof EntityCreature){
				EntityCreature ec = (EntityCreature) ((CraftEntity) pet).getHandle();
				CraftPlayer cp = (CraftPlayer)p;
				ec.setTarget((net.minecraft.server.v1_7_R1.Entity)cp.getHandle());
			}


			pet.setMetadata("petname", new FixedMetadataValue(Main.plugin, name));
			p.sendMessage(ChatColor.GREEN + "You have named your pet: " + ChatColor.BOLD + name);
			p.sendMessage(ChatColor.GRAY + "If you ever want to rename him, just left click him with the pet spawning item equipped.");
			p.sendMessage(ChatColor.GREEN + name + ChatColor.GREEN + ": " + ChatColor.GRAY + getRandomStatement(lpet_type));
		}
	}

	@EventHandler
	public void onInventoryOpen(InventoryOpenEvent e){
		Player p = (Player)e.getPlayer();
		if(naming_pet.containsKey(p.getName())){
			p.sendMessage(ChatColor.RED + "Pet Naming " + ChatColor.BOLD + "CANCELLED.");
			p.sendMessage(ChatColor.GRAY + "You cannot summon this pet until it has a name.");
			naming_pet.remove(p.getName());
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onInventoryClick(InventoryClickEvent e){
		Player p = (Player)e.getWhoClicked();
		if(!(pet_map.containsKey(p.getName()))){return;} // No pet, don't care.
		Inventory top = p.getOpenInventory().getTopInventory();

		if(top.getName().startsWith("Bank Chest") || (top.getName().equalsIgnoreCase("Collection Bin"))){ // Some interaction occuring in the top inventory section.

			ItemStack is = null;
			if(e.isShiftClick()){
				is = e.getCurrentItem();
				if(e.getRawSlot() >= top.getSize()){
					if(isPermUntradeable(is) && is.getType() == Material.MONSTER_EGG){ // It's a PERM-U.
						// We need to dismiss the pet, since it's getting deposited.
						for(Entity pet : pet_map.get(p.getName())){
							String spawned_type = getPetType(pet);
							String spawning_type = getPetType(is);

							if(spawned_type.equalsIgnoreCase(spawning_type)){ // Dismiss pet.
								removePetFromSpawnedList(p.getName(), pet);
								inv_pet_map.remove(pet);
								LivingEntity le = (LivingEntity)pet;
								le.damage(le.getHealth());
								if(le instanceof Horse){
									le.remove();
								}
								le.playEffect(EntityEffect.DEATH);
								if(spawned_type.equalsIgnoreCase("Baby Zombie")){
									p.playSound(p.getLocation(), Sound.ZOMBIE_DEATH, 1F, 1.5F);
								}
								if(spawned_type.equalsIgnoreCase("Baby Mooshroom")){
									p.playSound(p.getLocation(), Sound.COW_HURT, 1F, 1F);
								}
								if(spawned_type.equalsIgnoreCase("Baby Cat")){
									p.playSound(p.getLocation(), Sound.CAT_PURREOW, 1F, 1F);
								}
								if(spawned_type.equalsIgnoreCase("Sheep O' Luck")){
									p.playSound(p.getLocation(), Sound.SHEEP_SHEAR, 1F, 1F);
								}
								if(spawned_type.equalsIgnoreCase("Easter's Chicken")){
									p.playSound(p.getLocation(), Sound.CHICKEN_HURT, 1F, 1F);
									if(baby_chick_map.containsKey(pet)){
										for(Entity baby : baby_chick_map.get(pet)){
											LivingEntity baby_le = (LivingEntity)baby;
											baby_le.damage(baby_le.getHealth());
										}
										baby_chick_map.remove(pet);
										chicken_count.remove(pet);
									}
								}
								if(spawned_type.equalsIgnoreCase("Jeepers Creepers")){
									p.playSound(p.getLocation(), Sound.CREEPER_DEATH, 1F, 1F);
								}
								if(spawned_type.equalsIgnoreCase("Creeper of Independence")){
									p.playSound(p.getLocation(), Sound.CREEPER_DEATH, 1F, 0.5F);
								}
								if(spawned_type.equalsIgnoreCase("Beta Slime")){
									p.playSound(p.getLocation(), Sound.SLIME_ATTACK, 1F, 1F);
								}
								if(spawned_type.equalsIgnoreCase("Baby Horses")){
									p.playSound(p.getLocation(), Sound.COW_HURT, 1F, 0.2F);
								}
								if(spawned_type.equalsIgnoreCase("Spooky Bats")){
									p.playSound(p.getLocation(), Sound.BAT_TAKEOFF, 1F, 1F);
								}
								break;
							}
						}
					}
				}
			}
			else if(!(e.isShiftClick())){
				is = e.getCursor();
				if(e.getRawSlot() < top.getSize()){
					if(isPermUntradeable(is)  && is.getType() == Material.MONSTER_EGG){ // It's a PERM-U.
						// We need to dismiss the pet, since it's getting deposited.
						for(Entity pet : pet_map.get(p.getName())){
							String spawned_type = getPetType(pet);
							String spawning_type = getPetType(is);

							if(spawned_type.equalsIgnoreCase(spawning_type)){ // Dismiss pet.
								removePetFromSpawnedList(p.getName(), pet);
								inv_pet_map.remove(pet);
								LivingEntity le = (LivingEntity)pet;
								le.damage(le.getHealth());
								if(le instanceof Horse){
									le.remove();
								}
								le.playEffect(EntityEffect.DEATH);
								if(spawned_type.equalsIgnoreCase("Baby Zombie")){
									p.playSound(p.getLocation(), Sound.ZOMBIE_DEATH, 1F, 1.5F);
								}
								if(spawned_type.equalsIgnoreCase("Baby Mooshroom")){
									p.playSound(p.getLocation(), Sound.COW_HURT, 1F, 1F);
								}
								if(spawned_type.equalsIgnoreCase("Baby Cat")){
									p.playSound(p.getLocation(), Sound.CAT_PURREOW, 1F, 1F);
								}
								if(spawned_type.equalsIgnoreCase("Sheep O' Luck")){
									p.playSound(p.getLocation(), Sound.SHEEP_SHEAR, 1F, 1F);
								}
								if(spawned_type.equalsIgnoreCase("Easter's Chicken")){
									p.playSound(p.getLocation(), Sound.CHICKEN_HURT, 1F, 1F);
									if(baby_chick_map.containsKey(pet)){
										for(Entity baby : baby_chick_map.get(pet)){
											LivingEntity baby_le = (LivingEntity)baby;
											baby_le.damage(baby_le.getHealth());
										}
										baby_chick_map.remove(pet);
										chicken_count.remove(pet);
									}
								}
								if(spawned_type.equalsIgnoreCase("Jeepers Creepers")){
									p.playSound(p.getLocation(), Sound.CREEPER_DEATH, 1F, 1F);
								}
								if(spawned_type.equalsIgnoreCase("Beta Slime")){
									p.playSound(p.getLocation(), Sound.SLIME_ATTACK, 1F, 1F);
								}
								if(spawned_type.equalsIgnoreCase("Creeper of Independence")){
									p.playSound(p.getLocation(), Sound.CREEPER_DEATH, 1F, 0.5F);
								}
								if(spawned_type.equalsIgnoreCase("Baby Horses")){
									p.playSound(p.getLocation(), Sound.COW_HURT, 1F, 0.2F);
								}
								if(spawned_type.equalsIgnoreCase("Spooky Bats")){
									p.playSound(p.getLocation(), Sound.BAT_DEATH, 1F, 1F);
								}
							}
						}
					}
				}
			}
		}
	}

	@EventHandler
	public void onPlayerAnimationEvent(PlayerAnimationEvent e){
		Player p = e.getPlayer();
		if(p.getItemInHand().getType() == Material.MONSTER_EGG || p.getItemInHand().getType() == Material.MONSTER_EGGS){
			e.setCancelled(true);
			return;
		}
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
	public void onPlayerInteract(PlayerInteractEvent e){
		final Player p = e.getPlayer();
		if(e.hasItem() && e.getItem().getType() == Material.MONSTER_EGG){ // It's a pet!
			e.setCancelled(true);
			e.setUseItemInHand(Result.DENY);
			e.setUseInteractedBlock(Result.DENY);

			final ItemStack in_hand = e.getItem();

			if(e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR){ // Spawn / Dismiss pet.
				String pet_type = getPetType(in_hand);
				String raw_pet_type = getDBPetType(pet_type);

				if(!(player_pets.get(p.getName()).contains(raw_pet_type))){
					if(raw_pet_type.equalsIgnoreCase("baby_zombie")){
						/*if(Hive.forum_usergroup.get(p.getName()) == 9){
							PetMechanics.addPetToPlayer(p.getName(), "baby_zombie");
							// This causes (for the period the player is logged in) that every right click an additiona' baby_zombie' is added to their pet string. Jesus.
						}*/
						/*else{
							p.setItemInHand(new ItemStack(Material.AIR));
							p.sendMessage(ChatColor.RED + "You are NOT authorized to own this pet.");
							p.sendMessage(ChatColor.GRAY + "Account flagged for manual review.");
							return;
						}*/
					}
					else{ // Any other pet.
						log.info("[PetMechanics] ALERT: Player " + p.getName() + " has summoned " + raw_pet_type + ", but was not cleared by the system.");
						/*p.setItemInHand(new ItemStack(Material.AIR));
						p.sendMessage(ChatColor.RED + "You are NOT authorized to own this pet.");
						p.sendMessage(ChatColor.GRAY + "Account flagged for manual review.");
						return;*/
					}
				}

				if(pet_map.containsKey(p.getName())){
					for(Entity ent : pet_map.get(p.getName())){
						String spawned_type = getPetType(ent);
						String spawning_type = getPetType(e.getItem());

						if(!spawned_type.equalsIgnoreCase(spawning_type)){
							//p.sendMessage(ChatColor.RED + "You already have a different pet, a " + spawned_type + " out. Please dismiss that pet before summoning another.");
							continue;
						}

						// If code reaches this point, the pet is already out so kill it.
						removePetFromSpawnedList(p.getName(), ent);
						inv_pet_map.remove(ent);
						LivingEntity le = (LivingEntity)ent;
						le.damage(le.getHealth());
						if(le instanceof Horse){
							le.remove();
						}
						le.playEffect(EntityEffect.DEATH);
						if(pet_type.equalsIgnoreCase("Baby Zombie")){
							p.playSound(p.getLocation(), Sound.ZOMBIE_DEATH, 1F, 1.5F);
						}
						if(spawned_type.equalsIgnoreCase("Baby Mooshroom")){
							p.playSound(p.getLocation(), Sound.COW_HURT, 1F, 1F);
						}
						if(spawned_type.equalsIgnoreCase("Baby Cat")){
							p.playSound(p.getLocation(), Sound.CAT_PURREOW, 1F, 1F);
						}
						if(spawned_type.equalsIgnoreCase("Sheep O' Luck")){
							p.playSound(p.getLocation(), Sound.SHEEP_SHEAR, 1F, 1F);
						}
						if(spawned_type.equalsIgnoreCase("Easter's Chicken")){
							p.playSound(p.getLocation(), Sound.CHICKEN_HURT, 1F, 1F);
							if(baby_chick_map.containsKey(ent)){
								for(Entity baby : baby_chick_map.get(ent)){
									LivingEntity baby_le = (LivingEntity)baby;
									baby_le.damage(baby_le.getHealth());
								}
								baby_chick_map.remove(ent);
								chicken_count.remove(ent);
							}
						}
						if(spawned_type.equalsIgnoreCase("Jeepers Creepers")){
							p.playSound(p.getLocation(), Sound.CREEPER_DEATH, 1F, 1F);
						}
						if(spawned_type.equalsIgnoreCase("Beta Slime")){
							p.playSound(p.getLocation(), Sound.SLIME_ATTACK, 1F, 1F);
						}
						if(spawned_type.equalsIgnoreCase("Creeper of Independence")){
							p.playSound(p.getLocation(), Sound.CREEPER_DEATH, 1F, 0.5F);
						}
						if(spawned_type.equalsIgnoreCase("Baby Horses")){
							p.playSound(p.getLocation(), Sound.COW_HURT, 1F, 0.2F);
						}
						if(spawned_type.equalsIgnoreCase("Spooky Bats")){
							p.playSound(p.getLocation(), Sound.BAT_DEATH, 1F, 1F);
						}
						return;
					}
				}

				if(e.getAction() == Action.RIGHT_CLICK_BLOCK){
					if(pet_map.containsKey(p.getName()) && pet_map.get(p.getName()).size() >= 5){
						p.sendMessage(ChatColor.RED + "You may only summon up to " + ChatColor.UNDERLINE + "5" + ChatColor.RED + " pets at a time.");
						return;
					}

					Location l = e.getClickedBlock().getLocation().add(0, 1, 0);
					if(naming_pet.containsKey(p.getName())){
						p.sendMessage(ChatColor.GREEN + "Please enter a " + ChatColor.BOLD + "NAME" + ChatColor.GREEN + " for your pet.");
						p.sendMessage(ChatColor.GRAY + "The name should be between 1-16 characters and cannot contain '[', ']', or blank spaces.");
						naming_pet.put(p.getName(), l);
						return;
					}

					String pet_name = getPetName(e.getItem());
					//log.info(pet_name);

					if(pet_name.equalsIgnoreCase("???")){
						p.sendMessage(ChatColor.GREEN + "Please enter a " + ChatColor.BOLD + "NAME" + ChatColor.GREEN + " for your pet.");
						p.sendMessage(ChatColor.GRAY + "The name should be between 1-16 characters and cannot contain '[', ']', or blank spaces.");
						PetMechanics.pet_type.put(p.getName(), pet_type);
						naming_pet.put(p.getName(), l);
					}

					else if(!pet_name.equalsIgnoreCase("???")){

						if(pet_spawn_delay.containsKey(p.getName())){
							long ms_diff = (System.currentTimeMillis() - pet_spawn_delay.get(p.getName()));
							if(ms_diff <= (3 * 1000)){
								p.sendMessage(ChatColor.RED + "You can summon another pet in " + ChatColor.BOLD + (3 - (int)(ms_diff / 1000)) + "s");
								e.setCancelled(true);
								return;
							}
						}

						pet_spawn_delay.put(p.getName(), System.currentTimeMillis());

						Entity pet = null;
						if(pet_type.equalsIgnoreCase("Baby Zombie")){
							pet = l.getWorld().spawnEntity(l, EntityType.ZOMBIE);
							final Zombie z = (Zombie)pet;

							z.setBaby(true);
							z.setCustomName(ChatMechanics.censorMessage(getPetName(in_hand)));

							p.playSound(p.getLocation(), Sound.ZOMBIE_HURT, 1F, 1.5F);
						}

						if(pet_type.equalsIgnoreCase("Baby Mooshroom")){
							pet = l.getWorld().spawnEntity(l, EntityType.MUSHROOM_COW);
							MushroomCow ms = (MushroomCow)pet;
							ms.setBaby();
							ms.setAgeLock(true);
							ms.setCustomName(ChatMechanics.censorMessage(getPetName(in_hand)));
							p.playSound(p.getLocation(), Sound.COW_IDLE, 1F, 1F);
						}

						if(pet_type.equalsIgnoreCase("Baby Cat")){
							pet = l.getWorld().spawnEntity(l, EntityType.OCELOT);
							Ocelot oc = (Ocelot)pet;
							oc.setBaby();
							oc.setAgeLock(true);
							oc.setTamed(true);
							oc.setOwner((AnimalTamer)p);
							oc.setCustomName(ChatMechanics.censorMessage(getPetName(in_hand)));
							//oc.setSitting(false);
							p.playSound(p.getLocation(), Sound.CAT_MEOW, 1F, 1F);
						}
						if(pet_type.equalsIgnoreCase("Sheep O' Luck")){
							pet = p.getWorld().spawnEntity(p.getLocation().add(0, 2, 0), EntityType.SHEEP);
							Sheep s = (Sheep)pet;
							s.setBaby();
							s.setBreed(false);
							s.setAgeLock(true);
							s.setColor(DyeColor.LIME);
							s.setCustomName(ChatMechanics.censorMessage(getPetName(in_hand)));
							p.playSound(p.getLocation(), Sound.SHEEP_IDLE, 1F, 1F);
						}
						if(pet_type.equalsIgnoreCase("Easter's Chicken")){
							pet = p.getWorld().spawnEntity(p.getLocation().add(0, 2, 0), EntityType.CHICKEN);

							Chicken c = (Chicken)pet;
							c.setBreed(false);
							c.setCustomName(ChatMechanics.censorMessage(getPetName(in_hand)));
							p.playSound(p.getLocation(), Sound.CHICKEN_IDLE, 1F, 1F);
						}
						if(pet_type.equalsIgnoreCase("Jeepers Creepers")){
							pet = p.getWorld().spawnEntity(p.getLocation().add(0, 2, 0), EntityType.CREEPER);

							Creeper cr = (Creeper)pet;
							cr.setCanPickupItems(false);
							cr.setCustomName(ChatMechanics.censorMessage(getPetName(in_hand)));
							p.playSound(p.getLocation(), Sound.CREEPER_HISS, 1F, 1F);
						}
						if(pet_type.equalsIgnoreCase("Beta Slime")){
							pet = p.getWorld().spawnEntity(p.getLocation().add(0, 2, 0), EntityType.SLIME);

							Slime s = (Slime)pet;
							s.setSize(1);
							s.setCanPickupItems(false);
							s.setCustomName(ChatMechanics.censorMessage(getPetName(in_hand)));
							p.playSound(p.getLocation(), Sound.SLIME_WALK, 1F, 1F);
						}
						if(pet_type.equalsIgnoreCase("Creeper of Independence")){
							pet = p.getWorld().spawnEntity(p.getLocation().add(0, 2, 0), EntityType.CREEPER);
							Creeper cr = (Creeper)pet;
							cr.setCanPickupItems(false);
							cr.setCustomName(ChatMechanics.censorMessage(getPetName(in_hand)));
							cr.setPowered(true);
							p.playSound(p.getLocation(), Sound.CREEPER_HISS, 0.5F, 1F);
						}
						if(pet_type.equalsIgnoreCase("Spooky Bats")){
							for(int x = 3; x>0; x--){
								pet = p.getWorld().spawnEntity(p.getLocation().add(0, 2, 0), EntityType.BAT);
								Bat b = (Bat)pet;

								b.setCanPickupItems(false);
								b.setCustomName(ChatMechanics.censorMessage(getPetName(in_hand)));
								pet.setMetadata("petname", new FixedMetadataValue(Main.plugin, ChatMechanics.censorMessage(getPetName(in_hand))));
								inv_pet_map.put(pet, p.getName());
								addPetToSpawnedList(p.getName(), pet);
							}

							p.playSound(p.getLocation(), Sound.BAT_TAKEOFF, 0.5F, 1F);
						}
						if(pet_type.equalsIgnoreCase("Baby Horses")){
							Location loc = p.getLocation().add(0, 2, 0);
							Horse h1 = (Horse)spawnRandomHorse(loc, true);
							h1.setCanPickupItems(false);
							h1.setCustomName(ChatMechanics.censorMessage(getPetName(in_hand)));
							h1.setTamed(true);
							h1.setOwner((AnimalTamer)p);
							((Entity)h1).setMetadata("petname", new FixedMetadataValue(Main.plugin, ChatMechanics.censorMessage(getPetName(in_hand))));
							pet = (Entity)h1;

							// TODO: Horse sound.
							//p.playSound(p.getLocation(), Sound.COW_IDLE, 0.5F, 1F);
						}

						if(!(inv_pet_map.containsKey(pet))){
							addPetToSpawnedList(p.getName(), pet);
							inv_pet_map.put(pet, p.getName());
						}

						PetMechanics.pet_type.put(p.getName(), pet_type);

						if(((CraftEntity)pet).getHandle() instanceof EntityCreature){
							EntityCreature ec = (EntityCreature) ((CraftEntity) pet).getHandle();
							CraftPlayer cp = (CraftPlayer)p;
							ec.setTarget((net.minecraft.server.v1_7_R1.Entity)cp.getHandle());
						}

						p.sendMessage(ChatColor.GREEN + ChatMechanics.censorMessage(pet_name) + ": " + ChatColor.GRAY + getRandomStatement(pet_type));
						pet.setMetadata("petname", new FixedMetadataValue(Main.plugin, ChatMechanics.censorMessage(pet_name)));
					}
				}
			}

			if(e.getAction() == Action.LEFT_CLICK_BLOCK || e.getAction() == Action.LEFT_CLICK_AIR){ // Teleport / Move pet.

				if(!getPetName(e.getItem()).equalsIgnoreCase("???")){
					Location l = null;
					if(e.hasBlock()){
						l = e.getClickedBlock().getLocation().add(0, 1, 0);
					}
					else{
						l = p.getLocation().add(0, 1, 0);
					}
					String pet_type = getPetType(e.getItem());
					p.sendMessage(ChatColor.GREEN + "Please enter a new " + ChatColor.BOLD + "NAME" + ChatColor.GREEN + " for your pet.");
					p.sendMessage(ChatColor.GRAY + "The name should be between 1-16 characters and cannot contain '[', ']', or blank spaces.");

					PetMechanics.pet_type.put(p.getName(), pet_type);
					naming_pet.put(p.getName(), l);
				}

				/*Entity ent = pet_map.get(p.getName());
				  ent.teleport(e.getClickedBlock().getLocation().add(0, 1, 0));
				  p.playSound(p.getLocation(), Sound.ITEM_PICKUP, 0.5F, 1.25F);*/
			}

			/*this.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
				 public void run() {
				  if(p.getItemInHand() == null || p.getItemInHand().getType() == Material.AIR){
						p.setItemInHand(in_hand);
						p.updateInventory();
					}
				  }
				}, 4L);*/

		}
	}

	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){

		if(cmd.getName().equalsIgnoreCase("pet")){
			Player p = (Player)sender;
			if(!(p.isOp())){return true;}
			p.getInventory().addItem(generatePetEgg(EntityType.BAT, ""));
			p.sendMessage("[DEBUG] Field 0x0000c2 -> null");

			for(Entity ent : p.getNearbyEntities(32, 32, 32)){
				if(ent instanceof Horse){
					ent.remove();
				}
			}
		}

		if(cmd.getName().equalsIgnoreCase("addpet")){
			Player p = null;
			if(sender instanceof Player){
				p = (Player)sender;
				if(!(p.isOp())){return true;}
			}
			if(args.length != 2){
				if(p != null){
					p.sendMessage("Incorrect Syntax. " + "/addpet <player> <pet>");
					return true;
				}
				log.info("[PetMechanics] Incorrect syntax. /addpet <player> <pet>");
			}

			String player = args[0];
			String pet = args[1];

			addPetToPlayer(player, pet);
			if(Bukkit.getPlayer(player) == null){
				DonationMechanics.sendPacketCrossServer("[addpet]" + player + ":" + pet, -1, true);
			}

			log.info("[PetMechanics] Added pet '" + pet + "' to player " + player + ".");
			if(p != null){
				p.sendMessage("Added pet '" + pet + "' to player " + player + ".");
			}
		}

		return true;
	}
}
