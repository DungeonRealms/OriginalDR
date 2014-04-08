package me.vaqxine.TutorialMechanics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import me.vaqxine.Main;
import me.vaqxine.AchievmentMechanics.AchievmentMechanics;
import me.vaqxine.DuelMechanics.DuelMechanics;
import me.vaqxine.EnchantMechanics.EnchantMechanics;
import me.vaqxine.Hive.Hive;
import me.vaqxine.ItemMechanics.ItemMechanics;
import me.vaqxine.MerchantMechanics.MerchantMechanics;
import me.vaqxine.ProfessionMechanics.ProfessionMechanics;
import me.vaqxine.RealmMechanics.RealmMechanics;
import me.vaqxine.ScoreboardMechanics.ScoreboardMechanics;
import me.vaqxine.ShopMechanics.ShopMechanics;
import me.vaqxine.TutorialMechanics.commands.CommandSkip;
import me.vaqxine.TutorialMechanics.commands.CommandTutorial;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Rotation;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_7_R2.inventory.CraftItemStack;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class TutorialMechanics implements Listener {
	Logger log = Logger.getLogger("Minecraft");
	
	public static Location tutorialSpawn = null;
	// Spawn point of tutorial island.
	
	public static final String tutorialRegion = "tutorial_island";
	// Region name of tutorial island.
	
	public static HashMap<String, List<String>> quest_map = new HashMap<String, List<String>>();
	// Player_name, List of remaining NPC names to be spoken too.
	
	public static HashMap<String, List<String>> completion_delay = new HashMap<String, List<String>>();
	// Player_name, List of NPC names who have a timer event to tell them they've completed running. (used for rewards)
	
	public static List<String> skip_confirm = new ArrayList<String>();
	// Confirm skipping of tutorial island.
	
	List<String> leave_confirm = new ArrayList<String>();
	// Confirm leaving tutorial island.
	
	List<String> got_enchant_scroll = new ArrayList<String>();
	// Already got an enchant scroll.
	
	public static List<String> onIsland = new ArrayList<String>();
	
	@SuppressWarnings("deprecation")
	public void onEnable() {
		Main.plugin.getServer().getPluginManager().registerEvents(this, Main.plugin);
		tutorialSpawn = new Location(Bukkit.getWorlds().get(0), 824, 48, -103, 124F, 1F);
		
		Main.plugin.getCommand("skip").setExecutor(new CommandSkip());
		Main.plugin.getCommand("tutorial").setExecutor(new CommandTutorial());
		
		Main.plugin.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
			public void run() {
				Location chest_shop_ex1 = new Location(Bukkit.getWorlds().get(0), 900, 39, -203);
				Location chest_shop_ex2 = new Location(Bukkit.getWorlds().get(0), 901, 39, -203);
				
				ShopMechanics.assignShopNameplate("Example Shop", chest_shop_ex1.getBlock().getLocation(), chest_shop_ex2.getBlock().getLocation());
				ShopMechanics.setStoreColor(chest_shop_ex1.getBlock(), ChatColor.GREEN);
				
				Location location = new Location(Bukkit.getWorlds().get(0), 891, 39, -119);
				
				for(Entity ent : location.getChunk().getEntities()) {
					if(ent.getType() == EntityType.ENDER_CRYSTAL) { return; // Already exists.
					}
				}
				
				location.setY(location.getY());
				Block bedrock = location.getBlock();
				bedrock.setTypeId(7);
				location = bedrock.getLocation();
				location.setX(location.getX() + 0.5D);
				location.setZ(location.getZ() + 0.5D);
				location.getWorld().spawn(location, EnderCrystal.class);
			}
		}, 20 * 20L);
		
		Main.plugin.getServer().getScheduler().scheduleAsyncRepeatingTask(Main.plugin, new Runnable() {
			public void run() {
				for(Player pl : Main.plugin.getServer().getOnlinePlayers()) {
					if(onTutorialIsland(pl)) {
						if(!onIsland.contains(pl.getName())) onIsland.add(pl.getName());
						ScoreboardMechanics.addPlayerToTeam("TI", pl);
						if(!(pl.hasPotionEffect(PotionEffectType.INVISIBILITY))) {
							pl.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 15));
						}
						if(!(quest_map.containsKey(pl.getName()))) {
							List<String> quests_left = new ArrayList<String>(Arrays.asList("Master Miner", "Master Marksmen", "Master Fisherman", "Master Duelist", "Equipment Master", "Interface Guide", "Item Enchanter", "Armor Guide", "Alignment Guide", ChatColor.YELLOW.toString() + "Neutral Guide", ChatColor.RED.toString() + "Chaotic Guide"));
							quest_map.put(pl.getName(), quests_left);
							completion_delay.put(pl.getName(), new ArrayList<String>());
						}
						pl.setSneaking(true);
					} else {
						if(pl.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
							pl.removePotionEffect(PotionEffectType.INVISIBILITY);
						}
					}
				}
			}
		}, 10 * 20L, 5L);
		
		log.info("[TutorialMechanics] has been enabled.");
	}
	
	public void onDisable() {
		log.info("[TutorialMechanics] has been disabled.");
	}
	
	@EventHandler
	public void onPlayerToggleSneak(PlayerToggleSneakEvent e) {
		Player pl = e.getPlayer();
		if(TutorialMechanics.onTutorialIsland(pl)) {
			e.setCancelled(true);
			pl.setSneaking(true);
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityDamage(EntityDamageEvent e) {
		if(e.getEntity() instanceof Player) {
			Player pl = (Player) e.getEntity();
			if(onTutorialIsland(pl)) {
				e.setCancelled(true);
				e.setDamage(0);
			}
		}
	}
	@EventHandler
	public void onTPOUT(PlayerTeleportEvent e){
	    if(onIsland.contains(e.getPlayer().getName())){
	        onIsland.remove(e.getPlayer().getName());
	        System.out.print("Removed " + e.getPlayer().getName() + " from the onIsland HashMap!");
	    }
	}
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent e) {
		Player pl = e.getPlayer();
		Location from = e.getFrom();
		Location to = e.getTo();
		if(onIsland.contains(pl.getName()) && !DuelMechanics.getRegionName(to).equalsIgnoreCase(tutorialRegion)) {
			// Don't let them off the island!
			e.setCancelled(true);
			pl.teleport(from);
			pl.sendMessage(ChatColor.RED + "You " + ChatColor.UNDERLINE + "must" + ChatColor.RED + " either finish the tutorial or skip it with /skip to get off tutorial island.");
		}
		if(quest_map.containsKey(pl.getName()) && quest_map.get(pl.getName()).contains("Island Greeter")) {
			// Don't let them leave first room yet!
			
			if(e.getTo().distanceSquared(tutorialSpawn) >= 100) {
				e.setCancelled(true);
				pl.teleport(tutorialSpawn);
				pl.sendMessage(ChatColor.GRAY + "Complete your current objective before continuing the tutorial.");
				pl.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + "OBJECTIVE: " + ChatColor.GRAY + "Speak with the Island Greeter.");
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerJoin(PlayerJoinEvent e) {
		final Player pl = e.getPlayer();
		if(onTutorialIsland(pl)) {
			
			for(ItemStack is : pl.getInventory()) {
				if(is == null || is.getType() == Material.AIR || is.getType() == Material.WRITTEN_BOOK || is.getType() == Material.QUARTZ || is.getType() == Material.NETHER_STAR || is.getType() == Material.MONSTER_EGG) {
					continue;
				}
				pl.getInventory().remove(is);
			}
			pl.updateInventory();
			
			ScoreboardMechanics.addPlayerToTeam("TI", pl);
			pl.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 15));
			pl.setSneaking(true);
			
			Main.plugin.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
				public void run() {
					pl.teleport(tutorialSpawn); // Put them back at the start.
				}
			}, 10L);
			
			if(!(quest_map.containsKey(pl.getName()))) {
				List<String> quests_left = new ArrayList<String>(Arrays.asList("Island Greeter", "Master Miner", "Master Marksmen", "Master Fisherman", "Master Duelist", "Equipment Master", "Interface Guide", "Item Enchanter", "Armor Guide", "Alignment Guide", ChatColor.YELLOW.toString() + "Neutral Guide", ChatColor.RED.toString() + "Chaotic Guide"));
				quest_map.put(pl.getName(), quests_left);
			}
			
			completion_delay.put(pl.getName(), new ArrayList<String>());
			
			pl.sendMessage("");
			pl.sendMessage("");
			pl.sendMessage("");
			pl.sendMessage("");
			pl.sendMessage("");
			pl.sendMessage("");
			pl.sendMessage("");
			pl.sendMessage("");
			pl.sendMessage("");
			pl.sendMessage("");
			pl.sendMessage("");
			pl.sendMessage("");
			pl.sendMessage("");
			pl.sendMessage("");
			pl.sendMessage("");
			pl.sendMessage("");
			pl.sendMessage("");
			pl.sendMessage("");
			pl.sendMessage("");
			pl.sendMessage("");
			pl.sendMessage("");
			pl.sendMessage("");
			pl.sendMessage("");
			pl.sendMessage("");
			pl.sendMessage("");
			pl.sendMessage("");
			pl.sendMessage(ChatColor.YELLOW + "                    Welcome to " + ChatColor.UNDERLINE + "Dungeon Realms!");
			pl.sendMessage(ChatColor.GRAY + "Before you dive into the mystical world of Andalucia and discover all of its wonders, you are encouraged to go through this short introductory " + ChatColor.YELLOW + "Tutorial Island.");
			pl.sendMessage("");
			pl.sendMessage(ChatColor.GRAY + "You'll get a crash course on game mechanics and " + ChatColor.UNDERLINE + "get free loot" + ChatColor.GRAY + " just for completing it!");
			pl.sendMessage("");
			pl.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + "OBJECTIVE: " + ChatColor.GRAY + "Speak with the Island Greeter.");
		}
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		Player pl = e.getPlayer();
		if(onTutorialIsland(pl)) {
			ScoreboardMechanics.removePlayerFromTeam("TI", pl);
			pl.removePotionEffect(PotionEffectType.INVISIBILITY);
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.LOWEST)
	public void onItemDrop(PlayerDropItemEvent e) {
		Player pl = e.getPlayer();
		if(onTutorialIsland(pl)) {
			e.setCancelled(true);
			pl.updateInventory();
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onMapDrop(PlayerDropItemEvent e) {
		Player pl = e.getPlayer();
		if(!(e.isCancelled())) {
			// The maps gonna drop! DESTROY IT!
			if(e.getItemDrop().getItemStack().getType() == Material.MAP) {
				e.getItemDrop().remove();
				if(pl.getItemInHand().getType() == Material.MAP) {
					pl.setItemInHand(new ItemStack(Material.AIR));
				} else if(pl.getItemOnCursor().getType() == Material.MAP) {
					pl.setItemOnCursor(new ItemStack(Material.AIR));
				}
				
				pl.playSound(pl.getLocation(), Sound.BAT_TAKEOFF, 1F, 2F);
				pl.updateInventory();
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = false)
	public void onMapInteract(HangingBreakByEntityEvent e) {
		if(e.getRemover() instanceof Player && e.getEntity() instanceof ItemFrame) {
			Player pl = (Player) e.getRemover();
			ItemFrame iframe = (ItemFrame) e.getEntity();
			if(pl.getInventory().firstEmpty() != -1 && (iframe.getItem().getType() == Material.MAP)) {
				/*for(Entity ent : pl.getNearbyEntities(4, 4, 4)){
					if(ent instanceof ItemFrame){
						iframe = (ItemFrame)ent;
						if(iframe.getItem().getType() == Material.MAP || iframe.getItem().getType() == Material.EMPTY_MAP){
							break;
						}
						else{
							iframe = null;
						}
					}
				}*/
				
				if(iframe != null) {
					ItemStack map = iframe.getItem();
					if(!(pl.getInventory().contains(map))) {
						pl.getInventory().addItem(map);
						pl.updateInventory();
						pl.playSound(pl.getLocation(), Sound.BAT_TAKEOFF, 1F, 0.8F);
						AchievmentMechanics.addAchievment(pl.getName(), "Cartographer");
					}
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityDamageEvent(EntityDamageByEntityEvent event) {
		if(event.getEntity().getType() == EntityType.ITEM_FRAME) {
			ItemFrame is = (ItemFrame) event.getEntity();
			is.setItem(is.getItem());
			is.setRotation(Rotation.NONE);
			event.setCancelled(true);
			if(event.getDamager() instanceof Player) {
				if(is.getItem().getType() != Material.MAP) return;
				Player plr = (Player) event.getDamager();
				if(plr.getInventory().contains(is.getItem())) { return; }
				plr.getInventory().addItem(is.getItem());
			}
			return;
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityInteract(PlayerInteractEntityEvent event) {
		if(event.getRightClicked().getType() == EntityType.ITEM_FRAME) {
			event.setCancelled(true);
			ItemFrame is = (ItemFrame) event.getRightClicked();
			is.setItem(is.getItem());
			is.setRotation(Rotation.NONE);
			return;
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerInteractNPC(PlayerInteractEntityEvent e) {
		final Player pl = e.getPlayer();
		if(!(onTutorialIsland(pl))) { return; // Don't care.
		}
		
		Entity e_npc = e.getRightClicked();
		if(!(e_npc instanceof Player)) { return; }
		Player npc = (Player) e_npc;
		if(!(npc.hasMetadata("NPC"))) { return; } // Only NPC's matter.
		if(npc.getName().equalsIgnoreCase("Ship Sailor")) {
			e.setCancelled(true);
			pl.performCommand("skip");
			return;
		}
		
		if(npc.getName().equalsIgnoreCase("Ship Captain")) {
			// Check to see if they're ready to head to the mainland.
			if(quest_map.containsKey(pl.getName()) && quest_map.get(pl.getName()).size() > 0) {
				List<String> all_quests = new ArrayList<String>(Arrays.asList("Island Greeter", "Master Miner", "Master Marksmen", "Master Fisherman", "Master Duelist", "Equipment Master", "Interface Guide", "Item Enchanter", "Armor Guide", "Alignment Guide", ChatColor.RED.toString() + "Chaotic Guide", ChatColor.YELLOW.toString() + "Neutral Guide"));
				List<String> quest_list = quest_map.get(pl.getName());
				if(quest_list.size() > 0) {
					pl.sendMessage("");
					for(String s : all_quests) {
						if(quest_list.contains(s)) {
							pl.sendMessage(ChatColor.RED.toString() + s);
							continue; // They haven't completed this one.
						}
						pl.sendMessage(ChatColor.GREEN.toString() + s + ChatColor.GREEN + " - DONE!");
					}
				}
				pl.sendMessage(ChatColor.GRAY + "Ship Captain: " + ChatColor.WHITE + "You cannot leave until you speak with " + ChatColor.UNDERLINE.toString() + quest_list.size() + ChatColor.WHITE + " more Guide(s)!");
				
				e.setCancelled(true);
				return; // They don't get to leave yet!
			}
			// Ok, ask them if they're ready to leave!
			leave_confirm.add(pl.getName());
			pl.sendMessage("");
			pl.sendMessage(ChatColor.GRAY + "Ship Captain: " + ChatColor.WHITE + "Are you ready to start ye adventure " + pl.getName() + "?"); //+ " " + ChatColor.GREEN.toString() + ChatColor.BOLD.toString() + "Y " + ChatColor.GRAY.toString() + "/" + ChatColor.RED.toString() + ChatColor.BOLD.toString() + " N");
			pl.sendMessage(ChatColor.GRAY + "Type either '" + ChatColor.GREEN + "Y" + ChatColor.GRAY + "' or '" + ChatColor.RED + "N" + ChatColor.GRAY + "' -- Yes or No; Once you leave this island you can never come back, your epic adventure in the lands of Andalucia will begin!");
			pl.sendMessage("");
			e.setCancelled(true);
			return;
		}
		
		if(npc.getName().equalsIgnoreCase("Island Greeter")) {
			// Give the player 1x gem.
			// TODO: Should be a banker...
		}
		
		if(npc.getName().equalsIgnoreCase("Master Miner") && !(quest_map.get(pl.getName()).contains("Master Miner")) && !(completion_delay.get(pl.getName()).contains(npc.getName()))) {
			// Give the player 5x coal ore, tell them to trade it.
			if(!(pl.getInventory().contains(Material.COAL_ORE))) {
				e.setCancelled(true);
				ItemStack reward = (CraftItemStack.asCraftCopy(ProfessionMechanics.coal_ore));
				reward.setAmount(5);
				if(pl.getInventory().firstEmpty() != -1) {
					pl.getInventory().addItem(reward);
					pl.sendMessage(ChatColor.GRAY.toString() + "Master Miner: " + ChatColor.WHITE.toString() + "Ahh, here be some ore for ye' time. You could trade it with the Merchant!");
					pl.playSound(pl.getLocation(), Sound.ITEM_PICKUP, 1F, 1F);
				}
			}
		}
		
		if(npc.getName().equalsIgnoreCase("Master Fisherman") && !(quest_map.get(pl.getName()).contains("Master Fisherman")) && !(completion_delay.get(pl.getName()).contains(npc.getName()))) {
			// Give the player 1x raw fish, tell them to cook it.
			if(!(pl.getInventory().contains(Material.RAW_FISH)) && !(pl.getInventory().contains(Material.COOKED_FISH))) {
				e.setCancelled(true);
				ItemStack reward = RealmMechanics.makeUntradeable(CraftItemStack.asCraftCopy(ProfessionMechanics.getFishDrop(1)));
				if(pl.getInventory().firstEmpty() != -1) {
					pl.getInventory().addItem(reward);
					pl.sendMessage(ChatColor.GRAY.toString() + "Master Fisherman: " + ChatColor.WHITE.toString() + "Here's a freshly caught " + reward.getItemMeta().getDisplayName() + "! You should cook it over by the fire.");
					pl.playSound(pl.getLocation(), Sound.ITEM_PICKUP, 1F, 1F);
				}
			}
		}
		
		if(npc.getName().equalsIgnoreCase("Master Duelist") && !(quest_map.get(pl.getName()).contains("Master Duelist")) && !(completion_delay.get(pl.getName()).contains(npc.getName()))) {
			// Give the player a sword.
			if(!(pl.getInventory().contains(Material.WOOD_SWORD)) && !(pl.getInventory().contains(Material.WOOD_AXE))) {
				e.setCancelled(true);
				ItemStack reward = RealmMechanics.makeUntradeable(CraftItemStack.asCraftCopy(ItemMechanics.generateNoobWeapon()));
				if(pl.getInventory().firstEmpty() != -1) {
					pl.getInventory().addItem(reward);
					pl.sendMessage(ChatColor.GRAY.toString() + "Master Duelist: " + ChatColor.WHITE.toString() + "Right then, here's a training weapon -- give it a few swings at the dummy targets!");
					pl.playSound(pl.getLocation(), Sound.ITEM_PICKUP, 1F, 1F);
				}
			}
		}
		
		if(npc.getName().equalsIgnoreCase("Master Marksmen") && !(quest_map.get(pl.getName()).contains("Master Marksmen")) && !(completion_delay.get(pl.getName()).contains(npc.getName()))) {
			// Give the player 5x T1 arrows
			if(!(pl.getInventory().contains(Material.ARROW))) {
				e.setCancelled(true);
				ItemStack reward = RealmMechanics.makeUntradeable(CraftItemStack.asCraftCopy(ItemMechanics.t1_arrow));
				reward.setAmount(10);
				if(pl.getInventory().firstEmpty() != -1) {
					pl.getInventory().addItem(reward);
					pl.sendMessage(ChatColor.GRAY.toString() + "Master Marksmen: " + ChatColor.WHITE.toString() + "Here are some freshly fletched arrows you may use in your adventures. Farewall, " + pl.getName() + ".");
					pl.playSound(pl.getLocation(), Sound.ITEM_PICKUP, 1F, 1F);
				}
			}
		}
		
		if(npc.getName().equalsIgnoreCase("Armor Guide") && !(quest_map.get(pl.getName()).contains("Armor Guide")) && !(completion_delay.get(pl.getName()).contains(npc.getName()))) {
			// Give the player 1x T1 Scrap
			if(!(pl.getInventory().contains(Material.LEATHER))) {
				e.setCancelled(true);
				ItemStack reward = RealmMechanics.makeUntradeable(CraftItemStack.asCraftCopy(MerchantMechanics.T1_scrap));
				if(pl.getInventory().firstEmpty() != -1) {
					pl.getInventory().addItem(reward);
					pl.sendMessage(ChatColor.GRAY.toString() + "Armor Guide: " + ChatColor.WHITE.toString() + "Gah! Phew! Nearly burnt me'self there, here's an armor scrap for listening to an old man ramble. Use it to repair your equipment in the field.");
					pl.playSound(pl.getLocation(), Sound.ITEM_PICKUP, 1F, 1F);
				}
			}
		}
		
		if(npc.getName().equalsIgnoreCase("Item Enchanter") && !(quest_map.get(pl.getName()).contains("Item Enchanter")) && !(completion_delay.get(pl.getName()).contains(npc.getName()))) {
			// Give the player 1x T1 Weapon Scroll, tell them to use it.
			if(!(pl.getInventory().contains(Material.EMPTY_MAP)) && !(got_enchant_scroll.contains(pl.getName()))) {
				e.setCancelled(true);
				ItemStack reward = RealmMechanics.makeUntradeable(CraftItemStack.asCraftCopy(EnchantMechanics.t1_wep_scroll));
				if(pl.getInventory().firstEmpty() != -1) {
					pl.getInventory().addItem(reward);
					got_enchant_scroll.add(pl.getName());
					pl.sendMessage(ChatColor.GRAY.toString() + "Item Enchanter: " + ChatColor.WHITE.toString() + "Use this enchantment scroll on your weapon to increase its potency.");
					pl.playSound(pl.getLocation(), Sound.ITEM_PICKUP, 1F, 1F);
				}
			}
		}
		
		if(quest_map.containsKey(pl.getName())) {
			List<String> quests_left = quest_map.get(pl.getName());
			if(quests_left.contains(npc.getName())) {
				// This is someone the player has to talk to! Update the quest!
				if(completion_delay.containsKey(pl.getName())) {
					List<String> lcd = completion_delay.get(pl.getName());
					lcd.add(npc.getName());
					completion_delay.put(pl.getName(), lcd);
				} else {
					completion_delay.put(pl.getName(), new ArrayList<String>(Arrays.asList(npc.getName())));
				}
				
				quests_left.remove(npc.getName());
				quest_map.put(pl.getName(), quests_left);
				
				final String npc_name = npc.getName();
				Main.plugin.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
					public void run() {
						List<String> lcd = completion_delay.get(pl.getName());
						lcd.remove(npc_name);
						completion_delay.put(pl.getName(), lcd);
						pl.sendMessage(ChatColor.GREEN + ChatColor.BOLD.toString() + "       OBJECTIVE COMPLETE:" + ChatColor.GREEN.toString() + " Speak to " + ChatColor.UNDERLINE + npc_name + ChatColor.GREEN.toString() + "!");
						if(npc_name.equalsIgnoreCase("Island Greeter")) {
							pl.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "Your next objective is to follow the road out of the house and meet your first guide.");
						} else {
							if(npc_name.equalsIgnoreCase("Master Miner") || npc_name.equalsIgnoreCase("Master Fisherman") || npc_name.equalsIgnoreCase("Master Duelist") || npc_name.equalsIgnoreCase("Master Marksmen") || npc_name.equalsIgnoreCase("Armor Guide") || npc_name.equalsIgnoreCase("Item Enchanter")) {
								pl.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "Claim your " + ChatColor.UNDERLINE + "reward" + ChatColor.RESET + ChatColor.GRAY + ChatColor.ITALIC + " by speaking to " + npc_name + ", then continue down the road.");
							} else {
								pl.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "To discover your next objective, finish speaking with the " + npc_name + ChatColor.GRAY + ", then continue down the road.");
							}
						}
						pl.playSound(pl.getLocation(), Sound.LEVEL_UP, 1F, 4.0F);
					}
				}, 6 * 20L);
				
			}
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onAsyncChatEvent(AsyncPlayerChatEvent e) {
		final Player pl = e.getPlayer();
		
		if(skip_confirm.contains(pl.getName())) {
			e.setCancelled(true);
			if(e.getMessage().equalsIgnoreCase("y")) {
				// TODO: Move them to the main land, give items, etc.
				leaveTutorial(pl);
			} else {
				pl.sendMessage(ChatColor.RED + "Tutorial Skip - " + ChatColor.BOLD + "CANCELLED");
			}
			skip_confirm.remove(pl.getName());
			return;
		}
		
		if(leave_confirm.contains(pl.getName())) {
			e.setCancelled(true);
			if(e.getMessage().equalsIgnoreCase("y")) {
				// TODO: Move them to the main land, give items, etc.
				pl.sendMessage(ChatColor.GRAY + "Ship Captain: " + ChatColor.WHITE + "Well avast! You've completed the entire " + ChatColor.UNDERLINE + "Tutorial Island!" + ChatColor.WHITE + " As a token of our gratitude, here's a " + ChatColor.UNDERLINE + "set of rough leather armor" + ChatColor.WHITE + " to get you started on your adventure!");
				for(ItemStack is : ItemMechanics.generateNoobArmor()) {
					pl.getInventory().addItem(RealmMechanics.makeUntradeable(is));
				}
				
				Main.plugin.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
					public void run() {
						pl.sendMessage(ChatColor.GRAY + "Ship Captain: " + ChatColor.WHITE + "Argh! We'll be casting off in a few moments!");
					}
				}, 2 * 20L);
				
				Main.plugin.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
					public void run() {
						leaveTutorial(pl);
					}
				}, 5 * 20L);
			} else {
				pl.sendMessage(ChatColor.GRAY + "Ship Captain: " + ChatColor.WHITE + "Argh! Speak to me when ye ready to leave!");
			}
			leave_confirm.remove(pl.getName());
			return;
		}
		
		if(pl.isOp()) { return; // OP's can speak.
		}
		if(onTutorialIsland(pl)) {
			e.setCancelled(true);
			pl.sendMessage(ChatColor.RED + "You " + ChatColor.UNDERLINE + "cannot" + ChatColor.RED + " chat while on tutorial island.");
			pl.sendMessage(ChatColor.GRAY + "Either finish the tutorial or type /skip to enable chat.");
		}
	}
	
	public static boolean onTutorialIsland(Location loc) {
		if(loc == null) { return false; }
		if(DuelMechanics.getRegionName(loc).equalsIgnoreCase(tutorialRegion)) { return true; }
		return false;
	}
	
	public static boolean onTutorialIsland(Player pl) {
		if(pl.getLocation() == null) { return false; }
		if(DuelMechanics.getRegionName(pl.getLocation()).equalsIgnoreCase(tutorialRegion)) { return true; }
		return false;
	}
	
	public void leaveTutorial(Player pl) {
		quest_map.remove(pl.getName());
		completion_delay.remove(pl.getName());
		leave_confirm.remove(pl.getName());
		skip_confirm.remove(pl.getName());
		ScoreboardMechanics.removePlayerFromTeam("TI", pl);
		pl.setSneaking(false);
		
		try {
			pl.getInventory().setItem(pl.getInventory().firstEmpty(), RealmMechanics.makeUntradeable(ItemMechanics.signNewCustomItem(Material.POTION, (short) 1, ChatColor.WHITE.toString() + "Minor Health Potion", ChatColor.GRAY.toString() + "A potion that restores " + ChatColor.WHITE.toString() + "10HP")));
			pl.getInventory().setItem(pl.getInventory().firstEmpty(), RealmMechanics.makeUntradeable(ItemMechanics.signNewCustomItem(Material.POTION, (short) 1, ChatColor.WHITE.toString() + "Minor Health Potion", ChatColor.GRAY.toString() + "A potion that restores " + ChatColor.WHITE.toString() + "10HP")));
			pl.getInventory().setItem(pl.getInventory().firstEmpty(), RealmMechanics.makeUntradeable(ItemMechanics.signNewCustomItem(Material.POTION, (short) 1, ChatColor.WHITE.toString() + "Minor Health Potion", ChatColor.GRAY.toString() + "A potion that restores " + ChatColor.WHITE.toString() + "10HP")));
			pl.getInventory().setItem(pl.getInventory().firstEmpty(), RealmMechanics.makeUntradeable(new ItemStack(Material.BREAD, 1)));
			pl.getInventory().setItem(pl.getInventory().firstEmpty(), RealmMechanics.makeUntradeable(new ItemStack(Material.BREAD, 1)));
			
			if(!(pl.getInventory().contains(Material.WOOD_AXE)) && !(pl.getInventory().contains(Material.WOOD_SWORD))) {
				pl.getInventory().addItem(RealmMechanics.makeUntradeable(ItemMechanics.generateNoobWeapon()));
			}
		} catch(ArrayIndexOutOfBoundsException err) {
			// No space in inventory for some reason LOL?
			err.printStackTrace();
			// Keep going.
		}
		
		pl.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 5 * 20, 2));
		//pl.teleport(new Location(Bukkit.getWorlds().get(0), -203, 35, 214));
		pl.teleport(new Location(Bukkit.getWorlds().get(0), -378, 84, 355, 37F, 1F));
		//pl.teleport(new Location(Bukkit.getWorlds().get(0), -398, 85, 376, 87F, 1F));
		pl.playSound(pl.getLocation(), Sound.LEVEL_UP, 1F, 0.8F);
		
		pl.sendMessage("");
		pl.sendMessage("");
		pl.sendMessage("");
		pl.sendMessage("");
		pl.sendMessage("");
		pl.sendMessage("");
		pl.sendMessage("");
		pl.sendMessage("");
		pl.sendMessage("");
		pl.sendMessage("");
		pl.sendMessage("");
		pl.sendMessage("");
		pl.sendMessage("");
		pl.sendMessage("");
		pl.sendMessage("");
		pl.sendMessage("");
		pl.sendMessage("");
		pl.sendMessage("");
		pl.sendMessage("");
		pl.sendMessage("");
		pl.sendMessage(ChatColor.WHITE + "" + ChatColor.BOLD + "              Dungeon Realms Patch " + Hive.version);
		pl.sendMessage(ChatColor.GRAY + "                    " + "http://www.dungeonrealms.net/");
		pl.sendMessage("");
		pl.sendMessage(ChatColor.YELLOW + "                 " + "You are on the " + ChatColor.BOLD + Hive.MOTD.substring(0, Hive.MOTD.indexOf(" ")) + ChatColor.YELLOW + " shard.");
		pl.sendMessage(ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + " Type /shard to change your server instance at any time.");
		pl.sendMessage("");
	}
	
}
