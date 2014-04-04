package me.vaqxine.RestrictionMechanics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import me.vaqxine.Main;
import me.vaqxine.CommunityMechanics.CommunityMechanics;
import me.vaqxine.DuelMechanics.DuelMechanics;
import me.vaqxine.EcashMechanics.EcashMechanics;
import me.vaqxine.Hive.Hive;
import me.vaqxine.InstanceMechanics.InstanceMechanics;
import me.vaqxine.ItemMechanics.Halloween;
import me.vaqxine.ItemMechanics.ItemGenerators;
import me.vaqxine.ItemMechanics.ItemMechanics;
import me.vaqxine.MoneyMechanics.MoneyMechanics;
import me.vaqxine.MountMechanics.MountMechanics;
import me.vaqxine.PetMechanics.PetMechanics;
import me.vaqxine.ProfessionMechanics.ProfessionMechanics;
import me.vaqxine.RealmMechanics.RealmMechanics;
import me.vaqxine.RestrictionMechanics.commands.CommandList;
import me.vaqxine.RestrictionMechanics.commands.CommandZone;
import me.vaqxine.SpawnMechanics.SpawnMechanics;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_7_R2.inventory.CraftItemStack;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.LargeFireball;
import org.bukkit.entity.Player;
import org.bukkit.entity.SmallFireball;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.event.entity.SlimeSplitEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.LeatherArmorMeta;

public class RestrictionMechanics implements Listener {
	static Logger log = Logger.getLogger("Minecraft");
	
	public static HashMap<String, String> zone_type = new HashMap<String, String>();
	
	public static List<String> recent_craft = new ArrayList<String>();
	public static String main_world_name = "";
	RestrictionMechanics instance = null;
	
	public static List<String> in_inventory = new ArrayList<String>();
	// Players in an active inventory UI.
	
	public static List<Material> unstackable_items = new ArrayList<Material>();
	// A list of all materials that should have a max. of 1x per stack.
	
	public static List<String> recent_block_event = new ArrayList<String>();
	
	// A list of players who have a pending 'tp' anti-block glitch to prevent double dipping / teleporting up.
	
	@SuppressWarnings("deprecation")
	public void onEnable() {
		instance = this;
		Main.plugin.getServer().getPluginManager().registerEvents(this, Main.plugin);
		
		Main.plugin.getCommand("list").setExecutor(new CommandList());
		Main.plugin.getCommand("zone").setExecutor(new CommandZone());
		
		Main.plugin.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
			public void run() {
				loadCustomRecipes();
			}
		}, 10 * 20L);
		
		main_world_name = Bukkit.getWorlds().get(0).getName();
		
		unstackable_items.add(Material.BOOK);
		//unstackable_items.add(Material.RAW_FISH);
		//unstackable_items.add(Material.COOKED_FISH);
		unstackable_items.add(Material.BLAZE_ROD);
		unstackable_items.add(Material.BLAZE_POWDER);
		unstackable_items.add(Material.DIAMOND);
		unstackable_items.add(Material.FIREWORK);
		unstackable_items.add(Material.EYE_OF_ENDER);
		unstackable_items.add(Material.LEASH);
		unstackable_items.add(Material.ROTTEN_FLESH);
		unstackable_items.add(Material.BONE);
		unstackable_items.add(Material.GOLDEN_CARROT);
		unstackable_items.add(Material.JUKEBOX);
		unstackable_items.add(Material.PORTAL);
		unstackable_items.add(Material.GOLD_BLOCK);
		unstackable_items.add(Material.REDSTONE_TORCH_ON);
		unstackable_items.add(Material.SPIDER_EYE);
		unstackable_items.add(Material.PORTAL);
		
		Main.plugin.getServer().getScheduler().scheduleSyncRepeatingTask(Main.plugin, new Runnable() {
			public void run() {
				for(Player p : Main.plugin.getServer().getOnlinePlayers()) {
					Location to = p.getLocation();
					if(to.getZ() >= 1500 || to.getX() <= -1850 || to.getX() >= 1400 || to.getZ() <= -851) {
						if(isWithinAvalon(to)) {
							//So if they are in the location of avalon then they are fine. Otherwise they can be changed.
							continue;
						}
						if(p.isOp()) {
							//p.sendMessage("You would normally be TP'd, but you're an OP so I'm ignoring..."); Annoying
							continue;
						}
						if(p.isInsideVehicle()) {
							p.leaveVehicle();
						}
						p.teleport(SpawnMechanics.getRandomSpawnPoint(p.getName()));
						p.sendMessage(ChatColor.BLUE + "Oops! Your character was found outside the bounds of the map.");
					}
					
				}
			}
		}, 15 * 20L, 20L);
		
		Main.plugin.getServer().getScheduler().scheduleAsyncRepeatingTask(Main.plugin, new Runnable() {
			public void run() {
				for(Player p : Main.plugin.getServer().getOnlinePlayers()) {
					if(p == null || !(Hive.login_time.containsKey(p.getName()))) { return; }
					if((System.currentTimeMillis() - Hive.login_time.get(p.getName())) <= 1 * 1000) {
						continue;
					}
					Location l = p.getLocation();
					
					boolean isPvPDisabled = DuelMechanics.isPvPDisabled(l);
					boolean isDamageDisabled = DuelMechanics.isDamageDisabled(l);
					
					if(isPvPDisabled && isDamageDisabled) {
						if(zone_type.containsKey(p.getName()) && zone_type.get(p.getName()).equalsIgnoreCase("safe")) {
							continue;
						}
						zone_type.put(p.getName(), "safe");
						p.sendMessage(ChatColor.GREEN + "                " + ChatColor.BOLD + "*** SAFE ZONE (DMG-OFF) ***");
						//p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 25, 2));
						p.playSound(p.getLocation(), Sound.WITHER_SHOOT, 0.25F, 0.30F);
						continue;
					}
					if(isPvPDisabled && !isDamageDisabled) { // Damage is on, PvP OFF.
						if(zone_type.containsKey(p.getName()) && zone_type.get(p.getName()).equalsIgnoreCase("wilderness")) {
							continue;
						}
						zone_type.put(p.getName(), "wilderness");
						p.sendMessage(ChatColor.YELLOW + "           " + ChatColor.BOLD + "*** WILDERNESS (MOBS-ON, PVP-OFF) ***");
						//p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 25, 2));
						p.playSound(p.getLocation(), Sound.WITHER_SHOOT, 0.25F, 0.30F);
						continue;
					}
					if(!isPvPDisabled && !isDamageDisabled) { // Damage AND PvP is ON.
						if(zone_type.containsKey(p.getName()) && zone_type.get(p.getName()).equalsIgnoreCase("chaotic")) {
							continue;
						}
						zone_type.put(p.getName(), "chaotic");
						p.sendMessage(ChatColor.RED + "                " + ChatColor.BOLD + "*** CHAOTIC ZONE (PVP-ON) ***");
						//p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 25, 2));
						p.playSound(p.getLocation(), Sound.WITHER_SHOOT, 0.25F, 0.30F);
						continue;
					}
				}
			}
		}, 5 * 20L, 20L);
		
		log.info("[RestrictionMechanics] has been enabled.");
	}
	
	public void onDisable() {
		log.info("[RestrictionMechanics] has been disabled.");
	}
	
	public void removeHeads(Inventory inv) {
		inv.remove(Material.SKULL_ITEM);
	}
	
	@EventHandler
	public void onSlimeSplitEvent(SlimeSplitEvent e) {
		e.setCancelled(true);
	}
	
	@EventHandler
	public void onEntityChangeBlockEvent(EntityChangeBlockEvent e) {
		e.setCancelled(true);
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onHangingEntityPlaceEvent(HangingPlaceEvent e) {
		if(!e.getPlayer().isOp() && e.getEntity().getType() == EntityType.ITEM_FRAME) {
			e.setCancelled(true);
			e.getPlayer().updateInventory();
		}
	}
	
	@EventHandler
	public void onPlayerBucketEmptyEvent(PlayerBucketEmptyEvent e) {
		final Player pl = e.getPlayer();
		final Location l = pl.getLocation();
		
		if(!(pl.isOp()) && !pl.getWorld().getName().equalsIgnoreCase(pl.getName())) {
			e.setCancelled(true);
			if(!(recent_block_event.contains(pl.getName()))) {
				recent_block_event.add(pl.getName());
				Main.plugin.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
					public void run() {
						recent_block_event.remove(pl.getName());
						pl.teleport(l);
					}
				}, 8L);
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onBlockDispenseEvent(BlockDispenseEvent e) {
		ItemStack is = e.getItem();
		if(e.getBlock().getWorld().getName().equalsIgnoreCase(main_world_name) || InstanceMechanics.isInstance(e.getBlock().getWorld().getName())) {
			if(is.getType() == Material.EXP_BOTTLE || is.getType() == Material.EGG) {
				e.setCancelled(true);
			}
			if(!(is.getType() == Material.MINECART) && !(is.getType() == Material.ARROW) && !(is.getType() == Material.WATER) && !(is.getType() == Material.FIREWORK_CHARGE) && !(is.getType() == Material.LAVA) && !(is.getType() == Material.LAVA_BUCKET) && !(is.getType() == Material.WATER_BUCKET) && !(is.getType() == Material.getMaterial(385))) {
				e.setItem(new ItemStack(Material.AIR));
				e.setCancelled(true);
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		//removeHeads(e.getPlayer().getInventory());
		removeIllegalItems(e.getPlayer().getInventory());
		e.getPlayer().updateInventory();
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onInventoryOpenEvent(InventoryOpenEvent e) {
		//removeHeads(e.getInventory());
		Player pl = (Player) e.getPlayer();
		if(!(in_inventory.contains(pl.getName()))) {
			in_inventory.add(pl.getName());
		}
		
		if(e.getInventory().getName().startsWith("Bank Chest")) {
			removeIllegalItems(e.getInventory());
			int index = -1;
			for(ItemStack is : e.getInventory()) {
				index++;
				is = e.getInventory().getItem(index); // Make sure we're looking at correct item.
				if(is == null) {
					continue;
				}
				if(is.getType() == Material.BREAD && !RealmMechanics.isItemTradeable(is)) {
					// Remove illegal bread.
					e.getInventory().setItem(index, new ItemStack(Material.AIR));
				}
			}
			pl.updateInventory();
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = false)
	public void onInventoryCloseEvent(InventoryCloseEvent e) {
		final Player pl = (Player) e.getPlayer();
		
		Main.plugin.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
			public void run() {
				in_inventory.remove(pl.getName());
			}
		}, 10L);
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
	public void onCommandPreProcess(PlayerCommandPreprocessEvent e) {
		Player p = e.getPlayer();
		String cmd = e.getMessage();
		
		if(cmd.startsWith("/tpall") || cmd.startsWith("/say") || cmd.startsWith("/stop")) {
			log.info(cmd + " issued!");
			if(!(Main.isDev(p.getName()))) {
				e.setCancelled(true);
				return;
			}
		}
		
		if(cmd.startsWith("/list") || cmd.startsWith("/who") || cmd.startsWith("/online") || cmd.startsWith("/me")) {
			e.setCancelled(true);
			return;
		}
		
		if(p.isOp()) { return; }
		if(cmd.startsWith("/pl") || cmd.startsWith("/version") || cmd.startsWith("/help") || cmd.startsWith("/?")) {
			if(cmd.startsWith("/help") || cmd.startsWith("/?")) {
				p.sendMessage(ChatColor.WHITE + "Read the index of your Character Journal for a command list.");
			}
			e.setCancelled(true);
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerTeleport(PlayerTeleportEvent e) {
		Player pl = e.getPlayer();
		
		//log.info(pl.getName() + " - TELEPORT : " + e.getEventName().toString() + " - " + e.getCause().name() + " @ " + e.getTo().toString());
		
		if(pl.isOp()) { return; // Don't infrindge on OPs.
		}
		
		if(pl.getLastDamageCause() != null && (pl.getLastDamageCause().getCause() == DamageCause.SUFFOCATION || pl.getLastDamageCause().getCause() == DamageCause.VOID)) {
			// They need to be tp'd up.
			if(pl.isInsideVehicle()) {
				e.setCancelled(true);
			} else {
				return;
			}
			
			return;
		}
		
		Location to = e.getTo();
		Location from = e.getFrom();
		
		if(to == null || from == null) {
			e.setCancelled(true);
			return; // LOL?
		}
		
		if(!from.getWorld().getName().equalsIgnoreCase(main_world_name) || !to.getWorld().getName().equalsIgnoreCase(main_world_name)) { return; }
		
		if(!from.getWorld().getName().equalsIgnoreCase(to.getWorld().getName())) { return; }
		
		if((to.getY() - from.getY()) >= 10 || e.getCause() == TeleportCause.UNKNOWN) {
			// 20 blocks into air. (upwards)
			from.setY(to.getY());
			if(to.distanceSquared(from) <= 16) {
				// < 4 blocks, probably teleported up in air.
				e.setCancelled(true);
				e.setTo(e.getFrom());
				if(e.getCause() != TeleportCause.UNKNOWN) {
					log.info("[RestrictionMechanics] Player " + pl.getName() + " made improbable vertical movement, cancelled (teleport -> " + e.getCause().name() + ").");
				}
				return;
			}
		}
	}
	
	/*@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerMoveEvent(PlayerMoveEvent e){
		Player pl = e.getPlayer();
		if(pl.isOp()){
			return; // Don't infrindge on OPs.
		}

		if(pl.getLastDamageCause() != null && (pl.getLastDamageCause().getCause() == DamageCause.SUFFOCATION || pl.getLastDamageCause().getCause() == DamageCause.VOID)){
			// They need to be tp'd up.
			return;
		}

		Location to = e.getTo();
		Location from = e.getFrom();

		if(!from.getWorld().getName().equalsIgnoreCase(main_world_name) || !to.getWorld().getName().equalsIgnoreCase(main_world_name)){
			return;
		}

		if(!from.getWorld().getName().equalsIgnoreCase(to.getWorld().getName())){
			return;
		}

		if((to.getY() - from.getY()) >= 20){
			// 20 blocks into air.
			from.setY(to.getY());
			if(to.distanceSquared(from) <= 16){
				// < 4 blocks, probably teleported up in air.
				e.setCancelled(true);
				e.setTo(e.getFrom());
				log.info("[RestrictionMechanics] Player "  + pl.getName() + " made improbable vertical movement, cancelled (movement).");
				return;
			}
		}
	}*/
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onInventoryOpen(InventoryOpenEvent e) {
		//log.info(e.getInventory().getName());
		if(e.getPlayer().isOp()) { return; }
		
		if(e.getInventory().getName().equalsIgnoreCase("Repair")) {
			e.setCancelled(true);
		}
		if(e.getInventory().getName().contains("'s") && e.getInventory().getName().toLowerCase().contains("mule")) {
			e.setCancelled(true);
		}
		
		if(e.getPlayer().getWorld().getName().equalsIgnoreCase(main_world_name) || InstanceMechanics.isInstance(e.getPlayer().getWorld().getName())) {
			if(e.getInventory().getName().equalsIgnoreCase("container.dropper")) {
				log.info("(FLAG) Illegal inventory access - " + e.getPlayer().getName());
				e.setCancelled(true);
			}
			if(e.getInventory().getName().equalsIgnoreCase("container.dispenser")) {
				//log.info("(FLAG) Illegal inventory access - " + e.getPlayer().getName());
				e.setCancelled(true);
			}
			if(e.getInventory().getName().equalsIgnoreCase("container.hopper")) {
				e.setCancelled(true);
			}
			if(e.getInventory().getName().equalsIgnoreCase("container.minecart")) {
				e.setCancelled(true);
			}
		}
		if(e.getInventory().getName().equalsIgnoreCase("container.beacon")) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onPlayerEnterBed(PlayerBedEnterEvent e) {
		//Player pl = e.getPlayer();
		e.setCancelled(true);
	}
	
	/*@EventHandler
	public void onWeatherChangeEvent(WeatherChangeEvent e){
		if(e.toWeatherState()){
			World w = e.getWorld();
			w.setWeatherDuration(0);
			e.setCancelled(true);
		}
	}*/
	
	/*@EventHandler
	public void onInventorySwapEvent(InventorySwapEvent e){ // HOTBAR
		Player pl = (Player)e.getWhoClicked();
		if(!pl.getOpenInventory().getTopInventory().getName().equalsIgnoreCase("container.crafting")){
			e.setCancelled(true);
			pl.updateInventory();
		}
		if(e.getSlotType() == SlotType.ARMOR){
			e.setCancelled(true);
			pl.updateInventory();
		}
		if(e.getSlotType() == SlotType.RESULT){
			e.setCancelled(true);
			pl.updateInventory();
		}
	}*/
	
	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.LOWEST)
	public void onInventoryClickEvent(InventoryClickEvent e) {
		if(e.getInventory().getName().equalsIgnoreCase("container.furnace")) {
			Player pl = (Player) e.getWhoClicked();
			if(pl.isOp()) { return; }
			pl.sendMessage(ChatColor.RED + "This feature is not yet available in Andalucia.");
			e.setCancelled(true);
		}
		
		if(e.getInventory().getName().equalsIgnoreCase("container.crafting")) {
			ItemStack is = e.getCurrentItem();
			if(is != null && (DuelMechanics.isArmorIcon(is) || DuelMechanics.isWeaponIcon(is) || is.getType() == Material.THIN_GLASS || is.getType() == Material.MELON_STEM)) {
				e.setCurrentItem(new ItemStack(Material.AIR));
				((Player) e.getWhoClicked()).updateInventory();
			}
			return;
		}
		
		if(e.getInventory().getName().equalsIgnoreCase("Horse") || e.getInventory().getName().equalsIgnoreCase("Skeleton Horse") || e.getInventory().getName().equalsIgnoreCase("Undead Horse") || e.getInventory().getName().contains("Horse")) {
			final Player pl = (Player) e.getWhoClicked();
			
			if(e.getRawSlot() != -999 && (e.getInventory().getName().contains("Horse") || e.getRawSlot() == 0 || e.getRawSlot() == 1 || e.isShiftClick())) {
				if(e.getRawSlot() == 1 || (e.isShiftClick() && e.getCurrentItem() != null && MountMechanics.isMountArmor(e.getCurrentItem()))) {
					// Tell them they need to use mount upgrade differently.
					pl.sendMessage(ChatColor.RED + "Apply your horse armor by " + ChatColor.UNDERLINE + "dragging" + ChatColor.RED + " it ontop of the saddle in your inventory.");
					Main.plugin.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
						public void run() {
							pl.closeInventory();
						}
					}, 2L);
				}
				e.setCancelled(true);
				pl.updateInventory();
			}
			return;
		}
		
		if(e.getClick() == ClickType.DOUBLE_CLICK) {
			e.setCancelled(true);
			e.setResult(Result.DENY);
			Player pl = (Player) e.getWhoClicked();
			pl.updateInventory();
		}
		
		if(!e.getInventory().getName().equalsIgnoreCase("container.crafting") && e.getHotbarButton() != -1) {
			e.setCancelled(true);
			e.setResult(Result.DENY);
			Player pl = (Player) e.getWhoClicked();
			pl.updateInventory();
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onPlayerAnimation(PlayerAnimationEvent e) {
		try {
			Player pl = e.getPlayer();
			
			if(pl.getTargetBlock(null, 6).getType() == Material.ITEM_FRAME) {
				e.setCancelled(true);
			}
		} catch(Exception err) {
			return;
		}
	}
	
	@EventHandler
	public void onHangingBreakByEntity(HangingBreakByEntityEvent e) {
		if(e.getRemover() instanceof Player) {
			Player pl = (Player) e.getRemover();
			if(pl.getGameMode() == GameMode.CREATIVE) { return; }
		}
		
		if(e.getRemover().getWorld().getName().equalsIgnoreCase(main_world_name) || InstanceMechanics.isInstance(e.getRemover().getWorld().getName())) {
			e.setCancelled(true); // Cancel if we haven't returned by now.
		}
	}
	
	@EventHandler
	public void onPlayerShearEntityEvent(PlayerShearEntityEvent e) {
		if(e.getPlayer().isOp()) { return; }
		e.setCancelled(true);
	}
	
	@EventHandler
	public void onInventoryPickupItemEvent(InventoryPickupItemEvent e) {
		/**
		 * Called when a hopper or hopper minecart picks up a dropped item.
		 */
		ItemStack is = e.getItem().getItemStack();
		if(ItemMechanics.isArmor(is) || !ItemMechanics.getDamageData(is).equalsIgnoreCase("no") || is.getType() == Material.EMERALD || is.getType() == Material.PAPER || MoneyMechanics.isGemPouch(is)) {
			e.setCancelled(true);
		}
	}
	
	//@EventHandler
	public void InventoryMoveItemEvent(InventoryMoveItemEvent e) {
		ItemStack is = e.getItem();
		if(e.getSource().getType() == InventoryType.HOPPER || e.getInitiator().getType() == InventoryType.HOPPER) {
			if(!RealmMechanics.isItemTradeable(is) || ItemMechanics.isArmor(is) || !ItemMechanics.getDamageData(is).equalsIgnoreCase("no") || is.getType() == Material.EMERALD || is.getType() == Material.PAPER || MoneyMechanics.isGemPouch(is)) {
				e.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onLeavesDecayEvent(LeavesDecayEvent e) {
		e.setCancelled(true);
	}
	
	@EventHandler
	public void onBlockGrowEvent(BlockGrowEvent e) {
		e.setCancelled(true);
	}
	
	@EventHandler
	public void onBlockSpreadEvent(BlockSpreadEvent e) {
		if(e.getBlock().getWorld().getName().equalsIgnoreCase(main_world_name) || InstanceMechanics.isInstance(e.getBlock().getWorld().getName())) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onPistonExtendEvent(BlockPistonExtendEvent e) {
		Block being_broken = e.getBlock().getRelative(e.getDirection());
		Block next = being_broken.getRelative(e.getDirection());
		
		if(next.getType() == Material.MELON_BLOCK || being_broken.getType() == Material.MELON_BLOCK || next.getType() == Material.STEP || being_broken.getType() == Material.STEP) {
			e.setCancelled(true);
		}
		
		if(!next.getWorld().getName().equalsIgnoreCase(main_world_name) && !(InstanceMechanics.isInstance(next.getWorld().getName()))) {
			int realm_tier = RealmMechanics.getRealmTier(next.getWorld().getName());
			int max_size = RealmMechanics.getRealmSizeDimensions(realm_tier) + 16; // Add 16, because the default chunk (0,0) is never used, and 16 is lowest you can be.
			
			int max_y = 128; // + (max_size / 2)
			Block b = next;
			if(Math.round(b.getX() - 0.5) > max_size || Math.round(b.getX() - 0.5) < 16 || Math.round(b.getZ() - 0.5) > max_size || Math.round(b.getZ() - 0.5) < 16 || (b.getY() > (max_y + (max_size) + 1))) {
				e.setCancelled(true);
				return;
			}
		}
	}
	
	public boolean isVanillaArmor(ItemStack is) {
		Material m = is.getType();
		if(m == Material.DIAMOND_AXE || m == Material.DIAMOND_CHESTPLATE || m == Material.DIAMOND_BOOTS || m == Material.DIAMOND_HELMET || m == Material.DIAMOND_LEGGINGS || m == Material.DIAMOND_PICKAXE || m == Material.DIAMOND_SPADE || m == Material.DIAMOND_SWORD || m == Material.DIAMOND_HOE) {
			if(!(is.hasItemMeta() && is.getItemMeta().hasDisplayName())) { return true; }
		}
		if(m == Material.GOLD_AXE || m == Material.GOLD_CHESTPLATE || m == Material.GOLD_BOOTS || m == Material.GOLD_HELMET || m == Material.GOLD_LEGGINGS || m == Material.GOLD_PICKAXE || m == Material.GOLD_SPADE || m == Material.GOLD_SWORD || m == Material.GOLD_HOE) {
			if(!(is.hasItemMeta() && is.getItemMeta().hasDisplayName())) { return true; }
		}
		if(m == Material.IRON_AXE || m == Material.IRON_CHESTPLATE || m == Material.IRON_BOOTS || m == Material.IRON_HELMET || m == Material.IRON_LEGGINGS || m == Material.IRON_PICKAXE || m == Material.IRON_SPADE || m == Material.IRON_SWORD || m == Material.IRON_HOE) {
			if(!(is.hasItemMeta() && is.getItemMeta().hasDisplayName())) { return true; }
		}
		if(m == Material.STONE_AXE || m == Material.CHAINMAIL_CHESTPLATE || m == Material.CHAINMAIL_BOOTS || m == Material.CHAINMAIL_HELMET || m == Material.CHAINMAIL_LEGGINGS || m == Material.STONE_PICKAXE || m == Material.STONE_SPADE || m == Material.STONE_SWORD || m == Material.STONE_HOE) {
			if(!(is.hasItemMeta() && is.getItemMeta().hasDisplayName())) { return true; }
		}
		
		return false;
	}
	
	public boolean isWithinAvalon(Location l) {
		int x = l.getBlockX(), z = l.getBlockZ();
		if(x > -559 && x < 436) {//1000 blocks
			if(z > -3800 && z < -3105) {// 700 blocks
				return true;
			}
		}
		return false;
	}
	
	public void removeIllegalItems(Inventory inv) {
		// Armor icons, Weapon icons, bones, pumpkin vines
		int index = -1;
		for(ItemStack is : inv) {
			index++;
			if(is == null || is.getType() == Material.AIR) {
				continue;
			}
			if(InstanceMechanics.isDungeonItem(is) || isVanillaArmor(is) || DuelMechanics.isArmorIcon(is) || DuelMechanics.isWeaponIcon(is) || is.getType() == Material.GOLDEN_APPLE || (is.getType() == Material.GOLD_BLOCK && !(is.hasItemMeta() && is.getItemMeta().hasDisplayName())) || is.getType() == Material.THIN_GLASS || is.getType() == Material.MELON_STEM || (is.getType() == Material.INK_SACK && is.getDurability() == (short) 8) || (is.getType() == Material.INK_SACK && is.getDurability() == (short) 10)) {
				inv.setItem(index, new ItemStack(Material.AIR));
			}
			if(is.getType() == Material.INK_SACK && (!is.hasItemMeta() || !is.getItemMeta().hasDisplayName())) {
				inv.setItem(index, new ItemStack(Material.AIR));
			}
			if(is.getType() == Material.JACK_O_LANTERN && !(Halloween.isHalloweenMask(is)) && !(ItemMechanics.isArmor(is))) {
				inv.setItem(index, new ItemStack(Material.AIR));
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onPlayerInteractEvent(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		try {
			if((e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) && p.getTargetBlock(ProfessionMechanics.transparent, 8).getType() == Material.ITEM_FRAME) {
				e.setCancelled(true);
				e.setUseInteractedBlock(Result.DENY);
				e.setUseItemInHand(Result.DENY);
			}
		} catch(IllegalStateException ise) {
			e.setCancelled(true); // Assuume it shouldn't happen.
		}
	}
	
	@EventHandler
	public void onBlockFormEvent(BlockFormEvent e) {
		if(e.getNewState().getType() == Material.OBSIDIAN || e.getNewState().getType() == Material.COBBLESTONE || e.getBlock().getType() == Material.OBSIDIAN || e.getBlock().getType() == Material.COBBLESTONE) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onBlockFromTo(BlockFromToEvent event) {
		if(event.getBlock().getType() == Material.WATER || event.getBlock().getType() == Material.STATIONARY_WATER) {
			if(event.getToBlock().getType() == Material.CROPS || event.getToBlock().getType() == Material.SUGAR_CANE_BLOCK || event.getToBlock().getType() == Material.CACTUS || event.getToBlock().getType() == Material.RED_MUSHROOM || event.getToBlock().getType() == Material.BROWN_MUSHROOM || event.getToBlock().getType() == Material.LONG_GRASS) {
				event.setCancelled(true);
			}
		}
		
		if(event.getBlock().getType() == Material.PISTON_BASE || event.getBlock().getType() == Material.PISTON_MOVING_PIECE || event.getBlock().getType() == Material.PISTON_EXTENSION) {
			if(event.getToBlock().getType() == Material.MELON) {
				event.setCancelled(true);
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	// Prevents user from dropping an item when it's on their cursor and they close their inventory.
	public void onInventoryClose(InventoryCloseEvent e) {
		Player pl = (Player) e.getPlayer();
		if(e.getInventory().getName().equalsIgnoreCase("Guild Color Selector")) { return; }
		if(pl.getItemOnCursor() != null) {
			if(pl.getInventory().firstEmpty() != -1) {
				// They have some room.
				ItemStack on_cursor = pl.getItemOnCursor();
				pl.setItemOnCursor(new ItemStack(Material.AIR));
				pl.getInventory().addItem(on_cursor);
				pl.updateInventory();
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onItemDropEvent(PlayerDropItemEvent e) {
		ItemStack i = e.getItemDrop().getItemStack();
		final Player p = e.getPlayer();
		
		if(MountMechanics.isMount(i)) {
			e.getItemDrop().remove();
			p.getInventory().setItem(p.getInventory().firstEmpty(), i);
			Main.plugin.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
				public void run() {
					p.updateInventory();
				}
			}, 2L);
			return; // Hack-fix, for some reason saddles are marked as droppable tradeables. 
			// TODO: Find out why.
		}
		
		if(i.getType() == Material.NETHER_STAR || CommunityMechanics.isSocialBook(i) || (!(RealmMechanics.isItemTradeable(i)) && !(i.getType() == Material.PAPER))) {
			//e.setCancelled(true);// - Currently broken as of 11/9/12, causes meta data to delete.
			e.getItemDrop().remove();
			if(i.getType() == Material.NETHER_STAR || PetMechanics.isPermUntradeable(i) || CommunityMechanics.isSocialBook(i)) {
				if(PetMechanics.isPermUntradeable(i)) {
					p.getInventory().setItem(p.getInventory().firstEmpty(), i);
					Main.plugin.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
						public void run() {
							p.updateInventory();
						}
					}, 2L);
					return;
				}
				if(i.getType() == Material.NETHER_STAR || CommunityMechanics.isSocialBook(i)) {
					if(p.getInventory().firstEmpty() != -1) {
						p.getInventory().setItem(p.getInventory().firstEmpty(), i);
					} else {
						p.setItemOnCursor(i);
					}
					Main.plugin.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
						public void run() {
							p.updateInventory();
						}
					}, 2L);
					return;
				}
			}
			
			if(i.hasItemMeta() && i.getItemMeta() instanceof LeatherArmorMeta && ItemMechanics.getItemTier(i) > 1) {
				p.getInventory().addItem(i);
				Main.plugin.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
					public void run() {
						p.closeInventory();
						p.updateInventory();
					}
				}, 2L);
				p.sendMessage(ChatColor.RED + "You need to " + ChatColor.BOLD + "UNDYE" + ChatColor.RED + " this armor before dropping it.");
				p.sendMessage(ChatColor.GRAY + "Simply right click it to remove the guild dye.");
				return; // Do not delete.
			}
			
			p.updateInventory();
			p.playSound(p.getLocation(), Sound.FIZZ, 0.6F, 0.2F);
			p.sendMessage(ChatColor.GRAY + "This item was " + ChatColor.ITALIC + "untradeable" + ChatColor.GRAY + ", so it has " + ChatColor.UNDERLINE + "vanished.");
			
			Main.plugin.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
				public void run() {
					p.closeInventory();
				}
			}, 2L);
		}
	}
	
	@EventHandler
	public void onEntityInteract(EntityInteractEvent event) {
		if(event.getBlock().getType() == Material.SOIL && event.getEntity() instanceof Creature) event.setCancelled(true);
	}
	
	@EventHandler
	public void onVehicleDestroyEvent(VehicleDestroyEvent e) {
		if(e.getAttacker() instanceof Player) {
			Player pl = (Player) e.getAttacker();
			if(pl.getGameMode() == GameMode.CREATIVE || pl.getWorld().getName().equalsIgnoreCase(pl.getName()) || (RealmMechanics.build_list.containsKey(pl.getWorld().getName()) && RealmMechanics.build_list.get(pl.getWorld().getName()).contains(pl.getName()))) { return; }
			e.setCancelled(true);
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onBlockBreak(BlockBreakEvent e) {
		Player p = e.getPlayer();
		e.setExpToDrop(0);
		if(p.getGameMode() == GameMode.CREATIVE) { return; }
		
		if(!p.getWorld().getName().equalsIgnoreCase(p.getName()) && p.getGameMode() != GameMode.CREATIVE) { // Not their realm, in right mode.
			e.setCancelled(true);
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.HIGH)
	public void onBlockPlace(BlockPlaceEvent e) {
		final Player p = e.getPlayer();
		
		if(p.getGameMode() == GameMode.CREATIVE || p.getWorld().getName().equalsIgnoreCase(p.getName()) || (RealmMechanics.build_list.containsKey(p.getWorld().getName()) && RealmMechanics.build_list.get(p.getWorld().getName()).contains(p.getName()))) {
			
			Block in_slot = e.getBlock();
			Block next = e.getBlockAgainst();
			
			if((p.getItemInHand().getType() == Material.LAVA || p.getItemInHand().getType() == Material.LAVA_BUCKET) && (in_slot.getType() == Material.WATER || next.getType() == Material.WATER)) {
				e.setCancelled(true);
				p.updateInventory();
			}
			if((p.getItemInHand().getType() == Material.WATER || p.getItemInHand().getType() == Material.WATER_BUCKET) && (in_slot.getType() == Material.LAVA || next.getType() == Material.WATER)) {
				e.setCancelled(true);
				p.updateInventory();
			}
			
			if(in_slot.getType() == Material.ITEM_FRAME) {
				p.sendMessage("Item frame placement has been temporary DISABLED in realms.");
				e.setCancelled(true);
			}
			
			if(in_slot.getType() == Material.ENDER_CHEST) {
				if(!(p.isOp())) {
					e.setCancelled(true);
					p.setItemInHand(new ItemStack(Material.AIR));
					p.updateInventory();
				}
			}
			
			if(in_slot.getType() == Material.COAL_ORE || in_slot.getType() == Material.EMERALD_ORE || in_slot.getType() == Material.IRON_ORE || in_slot.getType() == Material.DIAMOND_ORE || in_slot.getType() == Material.GOLD_ORE) {
				e.setCancelled(true);
			}
			
			return;
		}
		
		if(!EcashMechanics.isMusicBox(e.getItemInHand()) && !p.getWorld().getName().equalsIgnoreCase(p.getName()) && p.getGameMode() != GameMode.CREATIVE) { // Not their realm, in right mode.
			e.setCancelled(true);
			final Location l = p.getLocation();
			
			if(!(recent_block_event.contains(p.getName()))) {
				recent_block_event.add(p.getName());
				Main.plugin.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
					public void run() {
						recent_block_event.remove(p.getName());
						p.teleport(l);
						p.updateInventory();
					}
				}, 10L);
			}
			
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onBlockBreakGlitch(BlockBreakEvent e) {
		final Player p = e.getPlayer();
		
		Block b = e.getBlock();
		
		if(b.getType() == Material.STONE || b.getType() == Material.COAL_ORE || b.getType() == Material.EMERALD_ORE || b.getType() == Material.IRON_ORE || b.getType() == Material.DIAMOND_ORE || b.getType() == Material.GOLD_ORE) { return; // Ore, not a block glitcher.
		}
		
		if(p.getGameMode() == GameMode.CREATIVE || p.getWorld().getName().equalsIgnoreCase(p.getName()) || (RealmMechanics.build_list.containsKey(p.getWorld().getName()) && RealmMechanics.build_list.get(p.getWorld().getName()).contains(p.getName()))) { return; }
		
		if(!p.getWorld().getName().equalsIgnoreCase(p.getName()) && p.getGameMode() != GameMode.CREATIVE) { // Not their realm, in right mode.
			e.setCancelled(true);
			
			if(!(e.getBlock().getType() == Material.CHEST) && !(e.getBlock().getType() == Material.LONG_GRASS) && !(e.getBlock().getType() == Material.YELLOW_FLOWER) && !(e.getBlock().getType() == Material.WHEAT) && !(e.getBlock().getType() == Material.BROWN_MUSHROOM) && !(e.getBlock().getType() == Material.RED_MUSHROOM) && !(e.getBlock().getType() == Material.VINE) && !(e.getBlock().getType() == Material.RED_ROSE) && !(e.getBlock().getType() == Material.FLOWER_POT) && !(e.getBlock().getType() == Material.CROPS) && !(e.getBlock().getType() == Material.TORCH) && !(e.getBlock().getType() == Material.SUGAR_CANE_BLOCK)) {
				final Location l = p.getLocation();
				
				if(!(recent_block_event.contains(p.getName()))) {
					recent_block_event.add(p.getName());
					Main.plugin.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
						public void run() {
							recent_block_event.remove(p.getName());
							p.teleport(l);
						}
					}, 8L);
				}
			}
			
		}
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		zone_type.remove(e.getPlayer().getName());
	}
	
	/*@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onPlayerFishEvent(PlayerFishEvent e){
		e.setExpToDrop(0);
		e.setCancelled(true);
		Player p = e.getPlayer();
		p.sendMessage(ChatColor.RED + "This feature is not yet available in Andalucia.");
	}*/
	
	@EventHandler
	public void onWeatherChange(WeatherChangeEvent e) {
		if(!(e.getWorld().getName().equalsIgnoreCase(main_world_name)) && !InstanceMechanics.isInstance(e.getWorld().getName())) {
			e.getWorld().setWeatherDuration(0);
			e.setCancelled(true);
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
	public void onBlockFromToEvent(BlockFromToEvent e) {
		if(e.getBlock().getType() == Material.PORTAL && e.getToBlock().getType() == Material.AIR) {
			e.setCancelled(true);
		}
		/*if(!(e.getToBlock().getWorld().getName().equalsIgnoreCase(main_world_name)) 
				&& (e.getToBlock().getType() == Material.STATIONARY_WATER || e.getToBlock().getType() == Material.STATIONARY_LAVA || e.getToBlock().getType() == Material.WATER || e.getToBlock().getType() == Material.LAVA)){
			// If it's not the main world and it's flowing, let's make sure it doesn't go outside of realm bounds.

			int realm_tier = RealmMechanics.getRealmTier(e.getToBlock().getWorld().getName());
			int max_size = RealmMechanics.getRealmSizeDimensions(realm_tier) + 16; // Add 16, because the default chunk (0,0) is never used, and 16 is lowest you can be.

			int max_y = 128; // + (max_size / 2)
			Block b = e.getToBlock();
				if(Math.round(b.getX() - 0.5) > max_size || Math.round(b.getX() - 0.5) < 16 || Math.round(b.getZ() - 0.5) > max_size || Math.round(b.getZ() - 0.5) < 16 || (b.getY() > (max_y + (max_size) + 1)) || (b.getY() < (max_y - (max_size) - 1))){
					e.setCancelled(true);
					return;
				}
		}*/
	}
	
	@EventHandler
	public void onBlockPhysicsEvent(BlockPhysicsEvent e) {
		if(e.getBlock().getType() == Material.PORTAL && e.getChangedType() == Material.AIR) {
			e.setCancelled(true);
		}
		
		if(e.getChangedType() == Material.PORTAL) {
			e.setCancelled(true);
		}
		
		if(e.getBlock().getType() == Material.LADDER && e.getChangedType() == Material.AIR) {
			e.setCancelled(true);
		}
		
		if(e.getBlock().getType() == Material.ITEM_FRAME && e.getChangedType() == Material.AIR) {
			e.setCancelled(true);
		}
		
		/*if(e.getChangedType() == Material.AIR && (e.getBlock().getWorld().getName().equalsIgnoreCase(main_world_name) || InstanceMechanics.isInstance(e.getBlock().getWorld().getName()))){
			//log.info(e.getBlock().toString() + " ---> " + e.getChangedType().name());
			e.setCancelled(true);
		}*/
	}
	
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
	public void preventDamage(EntityDamageEvent e) {
		if(e.getEntityType() == EntityType.DROPPED_ITEM || e.getEntityType() == EntityType.ITEM_FRAME || e.getEntityType() == EntityType.PAINTING) {
			e.setCancelled(true);
			e.setDamage(0);
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = false)
	public void onEntityDamageEvent(EntityDamageEvent e) {
		if(e instanceof EntityDamageByEntityEvent) {
			EntityDamageByEntityEvent edbee = (EntityDamageByEntityEvent) e;
			Entity attacker = edbee.getDamager();
			
			if(attacker instanceof SmallFireball || attacker instanceof Fireball || attacker instanceof LargeFireball || attacker.getType() == EntityType.ENDER_PEARL || attacker.getType() == EntityType.WITHER_SKULL || attacker.getType() == EntityType.SNOWBALL) {
				e.setCancelled(true);
				e.setDamage(0);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void explodeHeight(EntityExplodeEvent e) {
		if(e.getEntityType() == EntityType.WITHER_SKULL || e.getEntityType() == EntityType.SMALL_FIREBALL || e.getEntityType() == EntityType.FIREBALL || e.getEntity() instanceof LargeFireball) {
			e.setCancelled(true);
			e.setYield(0.0F);
		}
		if(e.getEntityType() == EntityType.PRIMED_TNT) {
			e.setYield(0.0F);
			if(e.getLocation().getWorld().getName().equalsIgnoreCase(main_world_name)) {
				e.setCancelled(true); // No TNT in main world.
			}
		}
	}
	
	@EventHandler
	public void onEntityPortalEvent(EntityPortalEvent e) {
		if(!(e.getEntity() instanceof Player)) {
			e.setCancelled(true);
			return;
		}
	}
	
	@EventHandler
	public void onEntityInteractEvent(EntityInteractEvent e) {
		if(e.getEntityType() == EntityType.PLAYER || e.getEntityType() == EntityType.DROPPED_ITEM) {
			return;
		} else {
			e.setCancelled(true);
		}
	}
	
	public void loadCustomRecipes() {
		ItemStack i = ItemGenerators.customGenerator("combined_key");
		ShapelessRecipe sr = new ShapelessRecipe(i);
		sr.addIngredient(1, Material.ICE);
		sr.addIngredient(1, Material.FIRE);
		Bukkit.addRecipe(sr);
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler(ignoreCancelled = true)
	public void onCraftItem(CraftItemEvent e) {
		ItemStack result = e.getRecipe().getResult();
		
		if(result.getType() == Material.FIREWORK_CHARGE) {
			if(!e.getWhoClicked().getWorld().getName().contains("fireydungeon")) {
				e.setCancelled(true);
				return;
			}
		}
		
		if(result.getType() == Material.WHEAT || result.getType() == Material.BREAD || result.getType() == Material.WOOD_SWORD || result.getType() == Material.STONE_SWORD || result.getType() == Material.IRON_SWORD || result.getType() == Material.DIAMOND_SWORD || result.getType() == Material.GOLD_SWORD || result.getType() == Material.BOW || result.getType() == Material.WOOD_AXE || result.getType() == Material.STONE_AXE || result.getType() == Material.IRON_AXE || result.getType() == Material.DIAMOND_AXE || result.getType() == Material.GOLD_AXE || result.getType() == Material.WOOD_SPADE || result.getType() == Material.STONE_SPADE || result.getType() == Material.IRON_SPADE || result.getType() == Material.DIAMOND_SPADE || result.getType() == Material.GOLD_SPADE || result.getType() == Material.WOOD_PICKAXE || result.getType() == Material.STONE_PICKAXE || result.getType() == Material.IRON_PICKAXE || result.getType() == Material.DIAMOND_PICKAXE || result.getType() == Material.GOLD_PICKAXE || result.getType() == Material.WOOD_HOE || result.getType() == Material.STONE_HOE || result.getType() == Material.IRON_HOE || result.getType() == Material.DIAMOND_HOE || result.getType() == Material.GOLD_HOE || result.getType() == Material.LEATHER_HELMET || result.getType() == Material.LEATHER_CHESTPLATE || result.getType() == Material.LEATHER_LEGGINGS || result.getType() == Material.LEATHER_BOOTS || result.getType() == Material.CHAINMAIL_HELMET || result.getType() == Material.CHAINMAIL_CHESTPLATE || result.getType() == Material.CHAINMAIL_LEGGINGS || result.getType() == Material.CHAINMAIL_BOOTS || result.getType() == Material.IRON_HELMET || result.getType() == Material.IRON_CHESTPLATE || result.getType() == Material.IRON_LEGGINGS || result.getType() == Material.IRON_BOOTS || result.getType() == Material.DIAMOND_HELMET || result.getType() == Material.DIAMOND_CHESTPLATE || result.getType() == Material.DIAMOND_LEGGINGS || result.getType() == Material.DIAMOND_BOOTS || result.getType() == Material.GOLD_HELMET || result.getType() == Material.GOLD_CHESTPLATE || result.getType() == Material.GOLD_LEGGINGS || result.getType() == Material.GOLD_BOOTS || result.getType() == Material.EMERALD_BLOCK || result.getType() == Material.EMERALD || result.getType() == Material.PAPER || result.getType() == Material.ANVIL || result.getType() == Material.CHEST || result.getType() == Material.FURNACE || result.getType() == Material.BEACON || result.getType() == Material.JUKEBOX || result.getType() == Material.ITEM_FRAME || result.getType() == Material.HOPPER || result.getType() == Material.TRAPPED_CHEST || result.getType() == Material.DROPPER || result.getType() == Material.FISHING_ROD || result.getType() == Material.DISPENSER || result.getType() == Material.INK_SACK || result.getType() == Material.IRON_FENCE || result.getType() == Material.MAP || result.getType() == Material.EMPTY_MAP || result.getType() == Material.BOOK || result.getType() == Material.ENCHANTMENT_TABLE || result.getType() == Material.BREWING_STAND || result.getType() == Material.JUKEBOX || result.getType() == Material.RAILS || result.getType() == Material.ACTIVATOR_RAIL || result.getType() == Material.POWERED_RAIL || result.getType() == Material.MINECART || result.getType() == Material.GOLD_INGOT || result.getType() == Material.GOLD_ORE || result.getType() == Material.GOLDEN_APPLE || result.getType() == Material.STORAGE_MINECART || result.getType() == Material.PISTON_BASE || result.getType() == Material.PISTON_STICKY_BASE || result.getType() == Material.CARROT_STICK || result.getType() == Material.LEASH || result.getType() == Material.NAME_TAG || result.getTypeId() == 417 || result.getTypeId() == 418 || result.getTypeId() == 419) {
			
			Player p = ((Player) e.getWhoClicked());
			if(p.isOp()) { return; }
			e.setCancelled(true);
			
			String item = result.getType().name();
			item = item.replaceAll("_", " ");
			item = item.replaceAll("WOOD", "WOODEN");
			
			item = item.substring(0, 1).toUpperCase() + item.substring(1, item.length()).toLowerCase();
			p.sendMessage(ChatColor.RED + "You cannot craft a(n) " + ChatColor.BOLD + item + ChatColor.RED + "");
			
			if(RealmMechanics.mat_shop_1.contains(result.getType()) || RealmMechanics.mat_shop_2.contains(result.getType())) {
				p.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + item + "s" + ChatColor.GRAY + " can be purchased from the realm item shop.");
			} else {
				p.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + item + "s" + ChatColor.GRAY + " can be obtained from loot chests all over Andalucia.");
			}
			return;
			
			//p.sendMessage(ChatColor.RED + "This recipe is currently " + ChatColor.BOLD + "DISABLED");
		}
	}
	
	@EventHandler
	public void onItemPickup(PlayerPickupItemEvent e) {
		ItemStack is = e.getItem().getItemStack();
		if(is.getType() == Material.MAGMA_CREAM && !(ItemMechanics.isOrbOfAlteration(is))) {
			e.setCancelled(true);
			Location loc = e.getItem().getLocation();
			e.getItem().remove();
			log.info("(FLAG) Illegal MAGMA_CREAM at: " + loc.getX() + ", " + loc.getY() + ", " + loc.getZ() + ".");
		}
		if(is.getType() == Material.GOLDEN_APPLE) {
			e.setCancelled(true);
			Location loc = e.getItem().getLocation();
			e.getItem().remove();
			log.info("(FLAG) Illegal GOLDEN_APPLE at: " + loc.getX() + ", " + loc.getY() + ", " + loc.getZ() + ".");
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerInteract(PlayerInteractEvent e) {
		final Player p = e.getPlayer();
		if(p.getGameMode() == GameMode.CREATIVE) { return; }
		
		if(e.getAction() == Action.LEFT_CLICK_BLOCK) {
			if(e.getClickedBlock().getType() == Material.FIRE) {
				e.setCancelled(true); // Block fire extinquish.
				e.getClickedBlock().setType(Material.FIRE);
				return;
			}
		}
		
		if(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			
			if(e.hasItem() && e.getItem().getType() == Material.GOLDEN_APPLE) {
				p.setItemInHand(new ItemStack(Material.AIR));
				log.info("[RestrictionMechanics] (FLAG) Player " + p.getName() + " used a GOLDEN APPLE.");
			}
			
			// Prevent eating decorative cakes.
			if(e.hasBlock() && e.getClickedBlock().getType() == Material.CAKE_BLOCK) {
				e.setCancelled(true);
				e.setUseInteractedBlock(Result.DENY);
				return;
			}
			
			if(e.hasBlock() && e.getClickedBlock().getType() == Material.COMMAND) {
				e.setCancelled(true); // If player is not in creative mode, cancel this.
				return;
			}
			
			if(e.hasItem()) {
				ItemStack i = e.getItem();
				Block in_spot = null;
				boolean update_inv = false;
				
				try {
					in_spot = p.getTargetBlock(ProfessionMechanics.transparent, 8);
				} catch(IllegalStateException ise) {
					e.setCancelled(true);
					p.updateInventory();
					return;
				}
				
				if(i.getType() == Material.REDSTONE_TORCH_ON && i.hasItemMeta()) {
					e.setCancelled(true);
					update_inv = true;
				}
				
				if(in_spot.getType() == Material.CAULDRON && i.hasItemMeta() && i.getItemMeta() instanceof LeatherArmorMeta) {
					// Prevents washing off armor.
					e.setCancelled(true);
					p.sendMessage(ChatColor.YELLOW + "To undye your armor, " + ChatColor.UNDERLINE + "RIGHT CLICK" + ChatColor.YELLOW + " the item in your inventory.");
					update_inv = true;
				}
				
				if(in_spot.getType() == Material.ITEM_FRAME) {
					e.setCancelled(true);
					update_inv = true;
				}
				
				if((p.getItemInHand().getType() == Material.LAVA || p.getItemInHand().getType() == Material.LAVA_BUCKET) && (in_spot.getType() == Material.WATER)) {
					e.setCancelled(true);
					update_inv = true;
				}
				if((p.getItemInHand().getType() == Material.WATER || p.getItemInHand().getType() == Material.WATER_BUCKET) && (in_spot.getType() == Material.LAVA)) {
					e.setCancelled(true);
					update_inv = true;
				}
				
				if((i.getType() == Material.WATER_BUCKET || i.getType() == Material.LAVA_BUCKET) && !p.getWorld().getName().equalsIgnoreCase(p.getName()) && p.getGameMode() == GameMode.SURVIVAL) { // Not their realm, in right mode.
					e.setCancelled(true);
					update_inv = true;
				}
				if(i.getType() == Material.WOOD_HOE || i.getType() == Material.STONE_HOE || i.getType() == Material.IRON_HOE || i.getType() == Material.DIAMOND_HOE || i.getType() == Material.GOLD_HOE) {
					e.setCancelled(true);
					update_inv = true;
				}
				
				if(update_inv == true) {
					Main.plugin.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
						public void run() {
							p.updateInventory();
						}
					}, 2L);
				}
			}
			if(e.hasBlock()) {
				Block b = e.getClickedBlock();
				/*if(b.getType() == Material.HOPPER || b.getType() == Material.HOPPER_MINECART){
					e.setCancelled(true);
					p.sendMessage(ChatColor.RED + "This feature has been temporarily " + ChatColor.BOLD + "DISABLED");
					return;
				}*/
				if(b.getType() == Material.DRAGON_EGG) {
					e.setCancelled(true);
				}
				if(b.getType() == Material.ENCHANTMENT_TABLE || b.getType() == Material.BREWING_STAND || b.getType() == Material.BED || b.getType() == Material.ITEM_FRAME || b.getType() == Material.BEACON) {
					e.setCancelled(true);
					if(b.getType() == Material.ENCHANTMENT_TABLE) {
						//p.sendMessage(ChatColor.RED + "You cannot give VANILLA ENCHANTMENTS to items in Andalucia.");
					}
					if(b.getType() == Material.BREWING_STAND) {
						//p.sendMessage(ChatColor.RED + "You cannot brew your own potions in Andalucia.");
					}
					if(b.getType() == Material.BEACON) {
						//p.sendMessage(ChatColor.RED + "Break this beacon to recieve a random BUFF.");
					}
				}
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	// false
	public void onPlayerPickupItemEvent(PlayerPickupItemEvent e) {
		Player p = e.getPlayer();
		if(unstackable_items.contains(e.getItem().getItemStack().getType()) && e.getItem().getItemStack().getType() != Material.ENCHANTED_BOOK) {
			e.setCancelled(true);
			if(p.getInventory().firstEmpty() != -1) {
				int amount = e.getItem().getItemStack().getAmount();
				ItemStack scroll = CraftItemStack.asCraftCopy(e.getItem().getItemStack());
				scroll.setAmount(1);
				
				while(amount > 0 && p.getInventory().firstEmpty() != -1) {
					p.getInventory().setItem(p.getInventory().firstEmpty(), scroll);
					p.updateInventory();
					amount--;
					if(amount > 0) {
						ItemStack new_stack = e.getItem().getItemStack();
						new_stack.setAmount(amount);
						e.getItem().setItemStack(new_stack);
					}
				}
				if(amount <= 0) {
					e.getItem().remove();
				}
				//p.getInventory().setItem(p.getInventory().firstEmpty(), scroll);
				p.playSound(p.getLocation(), Sound.ITEM_PICKUP, 1F, 1F);
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onInventoryDragEvent(InventoryDragEvent e) {
		String inv_name = e.getInventory().getName();
		Player pl = (Player) e.getWhoClicked();
		if(inv_name.equalsIgnoreCase("container.crafting") || inv_name.startsWith("Bank Chest")) { return; }
		e.setCancelled(true);
		pl.updateInventory();
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void unstackableItemHandler(InventoryClickEvent e) {
		Player p = (Player) e.getWhoClicked();
		if((e.isShiftClick() && e.getCurrentItem() != null) || (e.getCursor() != null && e.getCurrentItem() != null)) {
			
			ItemStack scroll = e.getCurrentItem();
			if(e.getCursor() != null) {
				ItemStack cursor = e.getCursor();
				
				if(unstackable_items.contains(cursor.getType()) && e.getClick() == ClickType.DOUBLE_CLICK) {
					e.setCancelled(true);
					e.setResult(Result.DENY);
					return;
				}
			}
			if(!unstackable_items.contains(scroll.getType())) { return; }
			
			if(!(e.isShiftClick())) {
				if(unstackable_items.contains(e.getCursor().getType())) {
					// Both the cursor and current item are TP books.
					e.setCancelled(true);
					ItemStack on_cur = e.getCursor();
					e.setCursor(scroll);
					e.setCurrentItem(on_cur);
					p.updateInventory();
					return;
				}
			}
			if(e.isShiftClick()) {
				if(e.getInventory().getName().contains("@") || e.getInventory().getName().contains("Collection Bin") || e.getInventory().getName().contains(p.getName())) {
					e.setCancelled(false);
					return;
				} // Shop/Trade handling.
				
				e.setCancelled(true);
				p.updateInventory();
				
				/*if(e.getInventory().firstEmpty() == -1){
					p.updateInventory();
					return;
				}*/
				
				/*if(e.getInventory().getName().equalsIgnoreCase("container.crafting")){
					Inventory p_inv = p.getInventory();
					ItemStack cur_item = e.getCurrentItem();
					int slot_to_move = -1;

					if(e.getRawSlot() < 36){
						// Inventory -> Hotbar
						slot_to_move = p_inv.firstEmpty();
					}
					else if(e.getRawSlot() >= 36 && e.getRawSlot() <= 44){
						// Hotbar -> Inventory
						for(int x = 9; x <= 35; x++){
							if(p_inv.getItem(x) == null || p_inv.getItem(x).getType() == Material.AIR){
								slot_to_move = x;
								break;
							}
						}
					}

					if(slot_to_move != -1){
						e.setCurrentItem(new ItemStack(Material.AIR));
						p_inv.setItem(slot_to_move, cur_item);
					}

					p.updateInventory();
					return;
				}*/
				return;
			}
		}
	}
	
}
