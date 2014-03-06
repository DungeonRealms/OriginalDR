package me.vaqxine.TeleportationMechanics;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import me.vaqxine.Main;
import me.vaqxine.DuelMechanics.DuelMechanics;
import me.vaqxine.HealthMechanics.HealthMechanics;
import me.vaqxine.Hive.Hive;
import me.vaqxine.Hive.ParticleEffect;
import me.vaqxine.ItemMechanics.ItemMechanics;
import me.vaqxine.KarmaMechanics.KarmaMechanics;
import me.vaqxine.RealmMechanics.RealmMechanics;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_7_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class TeleportationMechanics implements Listener {
	public static Logger log = Logger.getLogger("Minecraft");

	public static ItemStack Cyrennica_scroll = ItemMechanics.signNewCustomItem(Material.BOOK, (short)1, ChatColor.WHITE.toString() + 
			"" + ChatColor.BOLD.toString() + "Teleport:" + ChatColor.WHITE.toString() + " Cyrennica", ChatColor.GRAY.toString() + "Teleports the user to the grand City of Cyrennica.");

	public static ItemStack Harrison_scroll = ItemMechanics.signNewCustomItem(Material.BOOK, (short)2, ChatColor.WHITE.toString() + 
			"" + ChatColor.BOLD.toString() + "Teleport:" + ChatColor.WHITE.toString() + " Harrison Field", ChatColor.GRAY.toString() + "Teleports the user to Harrison Field.");

	public static ItemStack Dark_Oak_Tavern_scroll = ItemMechanics.signNewCustomItem(Material.BOOK, (short)3, ChatColor.WHITE.toString() + 
			"" + ChatColor.BOLD.toString() + "Teleport:" + ChatColor.WHITE.toString() + " Dark Oak Tavern", ChatColor.GRAY.toString() + "Teleports the user to the tavern in Dark Oak Forest.");

	public static ItemStack Deadpeaks_Mountain_Camp_scroll = ItemMechanics.signNewCustomItem(Material.BOOK, (short)4, ChatColor.WHITE.toString() + 
			"" + ChatColor.BOLD.toString() + "Teleport:" + ChatColor.WHITE.toString() + " Deadpeaks Mountain Camp", ChatColor.GRAY.toString() + "Teleports the user to the Deadpeaks." 
					+ "," + ChatColor.RED.toString() + "" + ChatColor.BOLD.toString() + "WARNING:" + ChatColor.RED.toString() + " CHAOTIC ZONE");

	public static ItemStack Jagged_Rocks_Tavern = ItemMechanics.signNewCustomItem(Material.BOOK, (short)5, ChatColor.WHITE.toString() + 
			"" + ChatColor.BOLD.toString() + "Teleport:" + ChatColor.WHITE.toString() + " Trollsbane Tavern", ChatColor.GRAY.toString() + "Teleports the user to Trollsbane Tavern.");
	
	public static ItemStack Tripoli_scroll = ItemMechanics.signNewCustomItem(Material.BOOK, (short)6, ChatColor.WHITE.toString() + 
			"" + ChatColor.BOLD.toString() + "Teleport:" + ChatColor.WHITE.toString() + " Tripoli", ChatColor.GRAY.toString() + "Teleports the user to Tripoli.");
	
	public static ItemStack Swamp_safezone_scroll = ItemMechanics.signNewCustomItem(Material.BOOK, (short)7, ChatColor.WHITE.toString() + 
			"" + ChatColor.BOLD.toString() + "Teleport:" + ChatColor.WHITE.toString() + " Gloomy Hollows ", ChatColor.GRAY.toString() + "Teleports the user to the Gloomy Hollows.");
	
	public static ItemStack Crestguard_keep_scroll = ItemMechanics.signNewCustomItem(Material.BOOK, (short)7, ChatColor.WHITE.toString() + 
			"" + ChatColor.BOLD.toString() + "Teleport:" + ChatColor.WHITE.toString() + " Crestguard Keep ", ChatColor.GRAY.toString() + "Teleports the user to the Crestguard Keep.");

	public static Location Harrison_Field;
	public static Location Dark_Oak_Tavern;
	public static Location Deadpeaks_Mountain_Camp;
	public static Location Trollsbane_tavern;
	public static Location Tripoli;
	public static Location Gloomy_Hollows;
	public static Location Crestguard_Keep;
	
	public static HashMap<String, String> tp_map = new HashMap<String, String>();
	public static ConcurrentHashMap<String, Integer> tp_effect = new ConcurrentHashMap<String, Integer>();
	public static HashMap<String, Location> tp_location = new HashMap<String, Location>();

	public static HashMap<String, Long> processing_move = new HashMap<String, Long>();
	// Player Name, Time of last movement check. -- Used for teleport_ regions.
	
	public static HashMap<String, Location> warp_map = new HashMap<String, Location>();
	// Player Name, Time of last movement check. -- Used for teleport_ regions.
	
	@SuppressWarnings("deprecation")
	public void onEnable(){
		log.info("[TeleportationMechanics] has been enabled.");
		Main.plugin.getServer().getPluginManager().registerEvents(this, Main.plugin);
		
		warp_map.put("overworld", new Location(Bukkit.getWorlds().get(0), -1158, 94, -515, 91F, 1F));
		warp_map.put("underworld", new Location(Bukkit.getWorlds().get(0), -362, 170, -3440, -90F, 1F));
		
		Harrison_Field = new Location(Bukkit.getWorlds().get(0), -594, 58, 687, 92.0F, 1F);
		Dark_Oak_Tavern = new Location(Bukkit.getWorlds().get(0), 280, 58, 1132, 2.0F, 1F);
		Deadpeaks_Mountain_Camp = new Location(Bukkit.getWorlds().get(0), -1173, 105, 1030, -88.0F, 1F);
		Trollsbane_tavern = new Location(Bukkit.getWorlds().get(0), 962, 94, 1069, -153.0F, 1F);
		Tripoli = new Location(Bukkit.getWorlds().get(0), -1320, 90, 370, 153F, 1F);
		Gloomy_Hollows = new Location(Bukkit.getWorlds().get(0), -590, 43, 0, 144F, 1F);
		Crestguard_Keep = new Location(Bukkit.getWorlds().get(0), -1428, 115, -489, 95F, 1F);
		
		
		
		// Teleports users in teleport_<warp_name> regions
		Main.plugin.getServer().getScheduler().scheduleAsyncRepeatingTask(Main.plugin, new Runnable() {
            public void run() {
            	for(Player pl : Main.plugin.getServer().getOnlinePlayers()){
					String region = DuelMechanics.getRegionName(pl.getLocation());
					if(region.startsWith("teleport_")){
						// Teleport!

						/*if(processing_move.containsKey(pl.getName()) && (System.currentTimeMillis() - processing_move.get(pl.getName())) <= (4 * 1000)){
							// Don't get them in a TP loop.
							continue;
						}*/

						String warp_name = region.substring(region.indexOf("_") + 1, region.length());
						
						if(!(warp_map.containsKey(warp_name))){
							continue;
						}
						
						Location warp = warp_map.get(warp_name);
						warp.setYaw(pl.getLocation().getYaw());
						processing_move.put(pl.getName(), System.currentTimeMillis() + 4000);
						pl.teleport(warp);
						
            	}
            }
         }
	 }, 5 * 20L, 1 * 20L);
		
		Main.plugin.getServer().getScheduler().scheduleSyncRepeatingTask(Main.plugin, new Runnable() {
            public void run() {
            	List<String> to_remove = new ArrayList<String>();
				for(Entry<String, Integer> data : tp_effect.entrySet()){
					String p_name = data.getKey();
					int seconds_left = data.getValue();

					if(seconds_left <= 0){
						if(Bukkit.getPlayer(p_name) != null){
							Player pl = Bukkit.getPlayer(p_name);
							try{
								ParticleEffect.sendToLocation(ParticleEffect.WITCH_MAGIC, pl.getLocation().add(0, 1, 0), new Random().nextFloat(), new Random().nextFloat(), new Random().nextFloat(), 0.20F, 200);
							} catch(Exception err){err.printStackTrace();}
							//pl.getWorld().spawnParticle(pl.getLocation().add(0, 1, 0), Particle.WITCH_MAGIC, 0.20F, 500);
						}
						
						try{
							teleportUser(p_name, tp_map.get(p_name));
						} catch(NullPointerException npe){
							to_remove.add(p_name);
							continue;
						}
						to_remove.add(p_name);
						continue;
					}
                  }
				

				for(String s : to_remove){
					tp_effect.remove(s);
					tp_map.remove(s);
				}
            }
	 }, 5 * 20L, 10L);
		
		Main.plugin.getServer().getScheduler().scheduleAsyncRepeatingTask(Main.plugin, new Runnable() {
			public void run() {    	
				List<String> to_remove = new ArrayList<String>();
				for(Entry<String, Integer> data : tp_effect.entrySet()){
					String p_name = data.getKey();
					int seconds_left = data.getValue();

					if(seconds_left <= 0){
						if(Bukkit.getPlayer(p_name) != null){
							Player pl = Bukkit.getPlayer(p_name);
							try{
								ParticleEffect.sendToLocation(ParticleEffect.WITCH_MAGIC, pl.getLocation().add(0, 1, 0), new Random().nextFloat(), new Random().nextFloat(), new Random().nextFloat(), 0.20F, 200);
							} catch(Exception err){err.printStackTrace();}
							//pl.getWorld().spawnParticle(pl.getLocation().add(0, 1, 0), Particle.WITCH_MAGIC, 0.20F, 500);
						}
						continue;
					}

					//TODO: Tick effect goes here.
					if(Bukkit.getPlayer(p_name) != null){
						Player pl = Bukkit.getPlayer(p_name);
						pl.sendMessage(ChatColor.WHITE + "" + ChatColor.BOLD + "CASTING" + ChatColor.WHITE + " ... " + seconds_left + ChatColor.BOLD + "s");

						double x = pl.getLocation().getX(); //(pet.getLocation().getX() + partner.getLocation().getX()) / 2;
						double y = pl.getLocation().getY(); //((pet.getLocation().getY() + partner.getLocation().getY()) / 2);
						double z = pl.getLocation().getZ(); //(pet.getLocation().getZ() + partner.getLocation().getZ()) / 2;

						if(seconds_left > 1){
							try{
								try{
									ParticleEffect.sendToLocation(ParticleEffect.PORTAL, pl.getLocation(), new Random().nextFloat(), new Random().nextFloat(), new Random().nextFloat(), 4F, 300);
									ParticleEffect.sendToLocation(ParticleEffect.WITCH_MAGIC, pl.getLocation(), new Random().nextFloat(), new Random().nextFloat(), new Random().nextFloat(), 1F, 200);
									
								} catch(Exception err){err.printStackTrace();}
								
								/*pl.getWorld().spawnParticle(pl.getLocation(), Particle.PORTAL, 4F, 300);
								pl.getWorld().spawnParticle(pl.getLocation(), Particle.WITCH_MAGIC, 1F, 200);*/
							} catch(ConcurrentModificationException cme){
								// Do nothing, keep ticking.
								//continue;
							}
						}

					}
					else{
						to_remove.add(p_name);
						continue;
					}

					seconds_left--;
					tp_effect.put(p_name, seconds_left);
				}

			}
		}, 5 * 20L, 20L);

	}

	public void onDisable() {
		log.info("[TeleportationMechanics] has been disabled.");
	}


	public String getScrollLocation(ItemStack is){
		if(!(is.hasItemMeta())){
			return null;
		}
		String i_name = ChatColor.stripColor(is.getItemMeta().getDisplayName());
		String tp_location = i_name.substring(i_name.indexOf(": ") + 2, i_name.length()).toLowerCase();
		return tp_location;
	}

	public boolean isScroll(ItemStack is){
		if(is.getType() != Material.BOOK){
			return false;
		}
		if(!(is.hasItemMeta())){
			return false;
		}
		if(!(is.getItemMeta().hasDisplayName())){
			return false;
		}
		if(!(is.getItemMeta().getDisplayName().toLowerCase().contains("teleport"))){
			return false;
		}
		return true;
	}

	public void teleportUser(String p_name, String type){
		
		if(Bukkit.getPlayer(p_name) != null){
			Player p = Bukkit.getPlayer(p_name);
			if(!(p.getWorld().getName().equalsIgnoreCase(Hive.main_world_name))){
				// They're in a realm.
				RealmMechanics.saved_locations.remove(p.getName());
			}
			
			if(p.getItemOnCursor() != null){
				if(p.getInventory().firstEmpty() != -1){
					ItemStack on_cursor = p.getItemOnCursor();
					p.setItemOnCursor(new ItemStack(Material.AIR));
					p.getInventory().addItem(on_cursor);
				}
			}
		}

		if(type.equalsIgnoreCase("cyrennica")){
			if(Bukkit.getPlayer(p_name) != null){
				Player pl = Bukkit.getPlayer(p_name);
				pl.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 40, 2));
				//pl.teleport(SpawnMechanics.getRandomSpawnPoint(pl.getName()).add(0, 1, 0));
				pl.teleport(new Location(Bukkit.getWorlds().get(0), -367, 83, 390));
				tp_map.remove(p_name);
			}
		}
		else if(type.contains("harrison")){
			if(Bukkit.getPlayer(p_name) != null){
				Player pl = Bukkit.getPlayer(p_name);
				pl.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 40, 2));
				pl.teleport(Harrison_Field);
				tp_map.remove(p_name);
			}
		}
		else if(type.contains("dark oak")){
			if(Bukkit.getPlayer(p_name) != null){
				Player pl = Bukkit.getPlayer(p_name);
				pl.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 40, 2));
				pl.teleport(Dark_Oak_Tavern);
				tp_map.remove(p_name);
			}
		}
		else if(type.contains("deadpeaks")){
			if(Bukkit.getPlayer(p_name) != null){
				Player pl = Bukkit.getPlayer(p_name);
				pl.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 40, 2));
				//new Location(Bukkit.getWorlds().get(0), -1173, 105, 1030, -88.0F, 1F);
				Location loc = getRandomLocation(Bukkit.getWorlds().get(0), -1165, -1180, 1020, 1030);
				pl.teleport(loc);
				tp_map.remove(p_name);
			}
		}
		else if(type.contains("trollsbane")){
			if(Bukkit.getPlayer(p_name) != null){
				Player pl = Bukkit.getPlayer(p_name);
				pl.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 40, 2));
				pl.teleport(Trollsbane_tavern);
				tp_map.remove(p_name);
			}
		}
		else if(type.contains("tripoli")){
			if(Bukkit.getPlayer(p_name) != null){
				Player pl = Bukkit.getPlayer(p_name);
				pl.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 40, 2));
				pl.teleport(Tripoli);
				tp_map.remove(p_name);
			}
		}
		else if(type.contains("gloomy")){
			if(Bukkit.getPlayer(p_name) != null){
				Player pl = Bukkit.getPlayer(p_name);
				pl.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 40, 2));
				pl.teleport(Gloomy_Hollows);
				tp_map.remove(p_name);
			}
		}
		else if(type.contains("crestguard")){
			if(Bukkit.getPlayer(p_name) != null){
				Player pl = Bukkit.getPlayer(p_name);
				pl.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 40, 2));
				pl.teleport(Crestguard_Keep);
				tp_map.remove(p_name);
			}
		}
	}

	public void runTeleportCast(Player pl, String type){
		double seconds_left = 5; // Default 3 seconds cast time.
		if(HealthMechanics.in_combat.containsKey(pl.getName())){
			long dif = ((HealthMechanics.HealthRegenCombatDelay * 1000) + HealthMechanics.in_combat.get(pl.getName())) - System.currentTimeMillis();
			seconds_left = (dif / 1000.0D) + 0.5D;
			seconds_left = Math.round(seconds_left);
		}

		if(seconds_left < 5){
			seconds_left += 5;
		}

		if(type.equalsIgnoreCase("cyrennica")){
			pl.sendMessage(ChatColor.WHITE + "" + ChatColor.BOLD + "CASTING " + ChatColor.WHITE + "Teleport Scroll: Cyrennica" + " ... " + (int)seconds_left + ChatColor.BOLD + "s");
			pl.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, (int)(seconds_left+3) * 20, 1));
			//TODO: Correct sound effect.
		}

		else if(type.contains("harrison")){
			pl.sendMessage(ChatColor.WHITE + "" + ChatColor.BOLD + "CASTING " + ChatColor.WHITE + "Teleport Scroll: Harrison's Field" + " ... " + (int)seconds_left + ChatColor.BOLD + "s");
			pl.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, (int)(seconds_left+3) * 20, 1));
			//TODO: Correct sound effect.
		}

		else if(type.contains("dark oak")){
			pl.sendMessage(ChatColor.WHITE + "" + ChatColor.BOLD + "CASTING " + ChatColor.WHITE + "Teleport Scroll: Dark Oak Tavern" + " ... " + (int)seconds_left + ChatColor.BOLD + "s");
			pl.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, (int)(seconds_left+3) * 20, 1));
			//TODO: Correct sound effect.
		}

		else if(type.contains("deadpeaks")){
			pl.sendMessage(ChatColor.WHITE + "" + ChatColor.BOLD + "CASTING " + ChatColor.WHITE + "Teleport Scroll: Deadpeaks Mountain Camp" + " ... " + (int)seconds_left + ChatColor.BOLD + "s");
			pl.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, (int)(seconds_left+3) * 20, 1));
			//TODO: Correct sound effect.
		}

		else if(type.contains("trollsbane")){
			pl.sendMessage(ChatColor.WHITE + "" + ChatColor.BOLD + "CASTING " + ChatColor.WHITE + "Teleport Scroll: Trollsbane Tavern" + " ... " + (int)seconds_left + ChatColor.BOLD + "s");
			pl.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, (int)(seconds_left+3) * 20, 1));
			//TODO: Correct sound effect.
		}
		
		else if(type.contains("tripoli")){
			pl.sendMessage(ChatColor.WHITE + "" + ChatColor.BOLD + "CASTING " + ChatColor.WHITE + "Teleport Scroll: Tripoli" + " ... " + (int)seconds_left + ChatColor.BOLD + "s");
			pl.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, (int)(seconds_left+3) * 20, 1));
			//TODO: Correct sound effect.
		}
		
		else if(type.contains("gloomy")){
			pl.sendMessage(ChatColor.WHITE + "" + ChatColor.BOLD + "CASTING " + ChatColor.WHITE + "Teleport Scroll: Gloomy Hollows" + " ... " + (int)seconds_left + ChatColor.BOLD + "s");
			pl.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, (int)(seconds_left+3) * 20, 1));
			//TODO: Correct sound effect.
		}

		else if(type.contains("crestguard")){
			pl.sendMessage(ChatColor.WHITE + "" + ChatColor.BOLD + "CASTING " + ChatColor.WHITE + "Teleport Scroll: Crestguard Keep" + " ... " + (int)seconds_left + ChatColor.BOLD + "s");
			pl.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, (int)(seconds_left+3) * 20, 1));
			//TODO: Correct sound effect.
		}
		
		pl.playSound(pl.getLocation(), Sound.AMBIENCE_CAVE, 1F, 1F); 

		/*double x = pl.getLocation().getX(); //(pet.getLocation().getX() + partner.getLocation().getX()) / 2;
		   	double y = pl.getLocation().getY(); //((pet.getLocation().getY() + partner.getLocation().getY()) / 2);
		   	double z = pl.getLocation().getZ(); //(pet.getLocation().getZ() + partner.getLocation().getZ()) / 2;

		   	Location safe_l = new Location(pl.getWorld(), (int)(x), (int)(y), (int)(z), pl.getLocation().getYaw(), pl.getLocation().getPitch());
		   	pl.teleport(safe_l);*/

		pl.eject();

		tp_map.put(pl.getName(), type);
		tp_effect.put(pl.getName(), (int)seconds_left);
		tp_location.put(pl.getName(), pl.getLocation());
	}

	public Location getRandomLocation(World world, int Xminimum, int Xmaximum, int Zminimum, int Zmaximum){
		int randomX = 0;
		int randomZ = 0;

		double x = 0.0D;
		double y = 0.0D;
		double z = 0.0D;

		randomX = Xminimum + (int)(Math.random() * (Xmaximum - Xminimum + 1)); //get random X
		randomZ = Zminimum + (int)(Math.random() * (Zmaximum - Zminimum + 1)); //get random Z

		x = Double.parseDouble(Integer.toString(randomX));
		y = Double.parseDouble(Integer.toString(world.getHighestBlockYAt(randomX, randomZ)));
		z = Double.parseDouble(Integer.toString(randomZ));

		x = x + 0.5; // add .5 so they spawn in the middle of the block
		z = z + 0.5;

		return new Location(world, x, y, z);
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e){
		Player pl = e.getPlayer();
		tp_map.remove(pl.getName());
		tp_effect.remove(pl.getName());
	}
	
	/*@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = false)
	public void onPlayerPickupItemEvent(PlayerPickupItemEvent e){
		Player p = e.getPlayer();
		if(e.getItem().getItemStack().getType() == Material.BOOK){
			e.setCancelled(true);
			if(p.getInventory().firstEmpty() != -1){
				int amount = e.getItem().getItemStack().getAmount();
				ItemStack scroll = CraftItemStack.asCraftCopy(e.getItem().getItemStack());
				scroll.setAmount(1);

				while(amount > 0 && p.getInventory().firstEmpty() != -1){
					p.getInventory().setItem(p.getInventory().firstEmpty(), scroll);
					p.updateInventory();
					amount--;
					if(amount > 0){
						ItemStack new_stack = e.getItem().getItemStack();
						new_stack.setAmount(amount);
						e.getItem().setItemStack(new_stack);
					}
				}
				if(amount <= 0){
					e.getItem().remove();
				}
				//p.getInventory().setItem(p.getInventory().firstEmpty(), scroll);
				p.playSound(p.getLocation(), Sound.ITEM_PICKUP, 1F, 1F);
			}
		}
	}*/

	@EventHandler
	public void onInventoryOpen(InventoryOpenEvent e){
		Inventory inv = e.getInventory();
		if(inv.contains(Material.EMPTY_MAP)){
			for(Entry<Integer, ? extends ItemStack> data : inv.all(Material.EMPTY_MAP).entrySet()){
				ItemStack is = data.getValue();
				int slot = data.getKey();

				if((is.getItemMeta().getDisplayName().toLowerCase().contains("teleport"))){
					is.setType(Material.BOOK);
					inv.setItem(slot, is);
				}
			}
		}
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e){
		Player p = e.getPlayer();
		Inventory inv = p.getInventory();
		if(inv.contains(Material.EMPTY_MAP)){
			for(Entry<Integer, ? extends ItemStack> data : inv.all(Material.EMPTY_MAP).entrySet()){
				try{
					ItemStack is = data.getValue();
					int slot = data.getKey();
					if((is.getItemMeta().getDisplayName().toLowerCase().contains("teleport"))){
						is.setType(Material.BOOK);
						inv.setItem(slot, is);
					}
				} catch(NullPointerException npe){
					continue;
				}
			}
		}

		if(inv.contains(Material.BOOK)){
			for(Entry<Integer, ? extends ItemStack> data : inv.all(Material.BOOK).entrySet()){
				try{
					ItemStack is = data.getValue();
					if(is.getAmount() > 1 && is.getItemMeta().getDisplayName().toLowerCase().contains("teleport")){
						ItemStack one = CraftItemStack.asCraftCopy(is);
						one.setAmount(1);
						int slot = data.getKey();
						while(p.getInventory().firstEmpty() != -1 && is.getAmount() > 1){
							p.getInventory().setItem(p.getInventory().firstEmpty(), makeUnstackable(one));
							is.setAmount(is.getAmount() - 1);

						}
						inv.setItem(slot, is); // Update the original stack.
					}
				} catch(NullPointerException npe){
					continue;
				}
			}
		}
	}

	/*@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onInventoryClick(InventoryClickEvent e){
		Player p = (Player)e.getWhoClicked();
		if((e.isShiftClick() && e.getCurrentItem() != null) || (e.getCursor() != null && e.getCurrentItem() != null)){
			ItemStack scroll = e.getCurrentItem();
			if(e.getCursor() != null){
				ItemStack cursor = e.getCursor();
				
				if(cursor.getType() == Material.BOOK && e.isDoubleClick()){
					e.setCancelled(true);
					e.setResult(Result.DENY);
					return;
				}
			}
			if(scroll.getType() != Material.BOOK){
				return;
			}
			if(!(e.isShiftClick())){
				if(e.getCursor().getType() == Material.BOOK){
					// Both the cursor and current item are TP books.
					e.setCancelled(true);
					ItemStack on_cur = e.getCursor();
					e.setCursor(scroll);
					e.setCurrentItem(on_cur);
					p.updateInventory();
					return;
				}
			}
			if(e.isShiftClick()){
				// Shift clicking a scroll.
				if(e.getInventory().getName().contains("@") || e.getInventory().getName().contains("Collection Bin") || e.getInventory().getName().contains(p.getName())){
					e.setCancelled(false);
					return;
				} // Shop/Trade handling.
				
				e.setCancelled(true);
				
				if(e.getInventory().firstEmpty() == -1){
					p.updateInventory();
					return;
				}
				e.setCurrentItem(new ItemStack(Material.AIR));
				if(!(e.getInventory().getName().equalsIgnoreCase("container.crafting"))){
					int negative_first_empty = -1;
					Inventory inv = e.getInventory();
					for(int slot = 9; slot >= 27; slot++){
						if(inv.getItem(slot).getType() == Material.AIR){
							negative_first_empty = slot;
							break;
						}
					}
					if(negative_first_empty != -1){
						e.getInventory().setItem(negative_first_empty, scroll);
					}
					else{
						e.getInventory().setItem(e.getInventory().firstEmpty(), scroll);
					}
				}
				else{
					p.getInventory().setItem(p.getInventory().firstEmpty(), scroll);
				}
				//p.updateInventory();
				return;
			}
		}
	}*/

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
	public void onPlayerInteract(PlayerInteractEvent e){
		Player p = e.getPlayer();
		/*if(e.isCancelled() && p.getWorld().getName().equalsIgnoreCase(Hive.main_world_name)){
			return;
		}*/
		if(Hive.server_swap.containsKey(p.getName())){
			return;
		}
		if(e.hasItem() && isScroll(e.getItem())){
			if(e.isCancelled() && e.getAction() != Action.RIGHT_CLICK_AIR){
				return;
			}
			
			e.setCancelled(true); // Cancel it from becoming an actual map.
			e.setUseInteractedBlock(Result.DENY);
			e.setUseItemInHand(Result.DENY);
			
			if(e.getAction() != Action.RIGHT_CLICK_AIR && e.getAction() != Action.RIGHT_CLICK_BLOCK){
				return;
			}
			/*if(e.hasBlock()){
				Block b = e.getClickedBlock();
				if(b.getType() == Material.CHEST || b.getType() == Material.WORKBENCH || b.getType() == Material.FURNACE || b.getType() == Material.ENDER_CHEST){
					return;
				}
			}*/

			if(tp_map.containsKey(p.getName())){
				p.updateInventory();
				return;
			}
			
			ItemStack scroll = e.getItem();
			if(scroll.getAmount() <= 1){
				p.setItemInHand(new ItemStack(Material.AIR));
				p.updateInventory();
			}
			else if(scroll.getAmount() > 1){
				int new_amount = scroll.getAmount() - 1;
				ItemStack scroll_1 = CraftItemStack.asCraftCopy(scroll);
				scroll_1.setAmount(1);

				p.sendMessage(ChatColor.RED + "Your teleportation scrolls were ilegally STACKED. They have been seperated and/or dropped.");

				while(new_amount > 0){
					new_amount--;
					if(p.getInventory().firstEmpty() != -1){
						p.getInventory().setItem(p.getInventory().firstEmpty(), makeUnstackable(scroll_1));
						continue;
					}
					// No room in inventory, drop.
					p.getWorld().dropItemNaturally(p.getLocation(), makeUnstackable(scroll_1));
				}
				//scroll.setAmount(new_amount);
				//p.setItemInHand(scroll);
				p.setItemInHand(new ItemStack(Material.AIR));
				p.updateInventory();
			}

			String tp_loc = getScrollLocation(scroll);
			if(!(tp_loc.equalsIgnoreCase("deadpeaks")) && KarmaMechanics.getRawAlignment(p.getName()).equalsIgnoreCase("evil")){
				p.sendMessage(ChatColor.RED + "You " + ChatColor.UNDERLINE + "cannot" + ChatColor.RED + " teleport to non-chaotic zones while chaotic.");
				p.sendMessage(ChatColor.GRAY + "Neutral in " + ChatColor.BOLD + KarmaMechanics.getSecondsUntilAlignmentChange(p.getName()) + "s");
				return;
			}
			runTeleportCast(p, tp_loc);
		}

	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onEntityDamage(EntityDamageEvent e){
		if(e.getDamage() <= 0){
			return;
		}
		if(e.getEntity() instanceof Player){
			Player pl = (Player)e.getEntity();
			if(tp_map.containsKey(pl.getName())){
				tp_effect.remove(pl.getName());
				tp_map.remove(pl.getName());
				pl.sendMessage(ChatColor.RED + "Teleportation - " + ChatColor.BOLD + "CANCELLED");
				pl.removePotionEffect(PotionEffectType.CONFUSION);
			}
		}
	}

	@EventHandler
	public void onPlayerMoveEvent(PlayerMoveEvent e){
		Player pl = e.getPlayer();
		if(tp_map.containsKey(pl.getName())){
			Location loc = tp_location.get(pl.getName());
			if(!(e.getTo().getWorld().getName().equalsIgnoreCase(loc.getWorld().getName())) || e.getTo().distanceSquared(loc) >= 2){
				tp_effect.remove(pl.getName());
				tp_map.remove(pl.getName());
				pl.sendMessage(ChatColor.RED + "Teleportation - " + ChatColor.BOLD + "CANCELLED");
				pl.removePotionEffect(PotionEffectType.CONFUSION);
			}
		}
	}

	public static ItemStack makeUnstackable(ItemStack is){
		short ran_dur = (short)(new Random().nextInt(32768)); // -32768 to 32768
		is.setDurability(ran_dur);
		return is;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){

		if(cmd.getName().equalsIgnoreCase("drtp")){
			Player p = (Player)sender;
			if(!(p.isOp())){
				return true;
			}
			
			p.getInventory().addItem(CraftItemStack.asCraftCopy(makeUnstackable(Cyrennica_scroll)));
			p.getInventory().addItem(CraftItemStack.asCraftCopy(makeUnstackable(Dark_Oak_Tavern_scroll)));
			p.getInventory().addItem(CraftItemStack.asCraftCopy(makeUnstackable(Harrison_scroll)));
			p.getInventory().addItem(CraftItemStack.asCraftCopy(makeUnstackable(Deadpeaks_Mountain_Camp_scroll)));
			p.getInventory().addItem(CraftItemStack.asCraftCopy(makeUnstackable(Jagged_Rocks_Tavern)));
			p.getInventory().addItem(CraftItemStack.asCraftCopy(makeUnstackable(Tripoli_scroll)));
			p.getInventory().addItem(CraftItemStack.asCraftCopy(makeUnstackable(Swamp_safezone_scroll)));
			p.getInventory().addItem(CraftItemStack.asCraftCopy(makeUnstackable(Crestguard_keep_scroll)));
		}
		return true;
	}


}
