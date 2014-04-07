package me.vaqxine.FatigueMechanics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

import me.vaqxine.Main;
import me.vaqxine.DuelMechanics.DuelMechanics;
import me.vaqxine.FatigueMechanics.commands.CommandFat;
import me.vaqxine.Hive.ParticleEffect;
import me.vaqxine.InstanceMechanics.InstanceMechanics;
import me.vaqxine.ItemMechanics.ItemMechanics;
import me.vaqxine.MonsterMechanics.MonsterMechanics;
import me.vaqxine.ProfessionMechanics.ProfessionMechanics;
import me.vaqxine.RepairMechanics.RepairMechanics;
import me.vaqxine.TutorialMechanics.TutorialMechanics;
import net.minecraft.server.v1_7_R2.NBTTagCompound;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_7_R2.inventory.CraftItemStack;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleSprintEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BlockIterator;

public class FatigueMechanics implements Listener {
	static Logger log = Logger.getLogger("Minecraft");
	public static ConcurrentHashMap<Player, Integer> fatigue_effect = new ConcurrentHashMap<Player, Integer>();
	public static HashMap<String, Float> energy_regen_data = new HashMap<String, Float>();
	public static HashMap<String, Float> old_energy = new HashMap<String, Float>();
	
	public static HashMap<String, Long> last_attack = new HashMap<String, Long>();
	// Last time a player issued an entityDamage event.
	
	public static CopyOnWriteArrayList<Player> sprinting = new CopyOnWriteArrayList<Player>();
	public static CopyOnWriteArrayList<Player> starving = new CopyOnWriteArrayList<Player>();
	
	public String main_world_name = "";
	
	static FatigueMechanics instance = null;
	
	public void onEnable() {
		Bukkit.getServer().getPluginManager().registerEvents(this, Main.plugin);
		main_world_name = Bukkit.getWorlds().get(0).getName();
		instance = this;
		
		Main.plugin.getCommand("fat").setExecutor(new CommandFat());
		
		// Nasty hack to prevent sprinting while starving.
		new BukkitRunnable() {
			@Override
			public void run() {
				blockSprinting();
			}
		}.runTaskTimerAsynchronously(Main.plugin, 2 * 20L, 5L);
		
		// Handles energy regen event.
		new BukkitRunnable() {
			@Override
			public void run() {
				replenishEnergy();
			}
		}.runTaskTimerAsynchronously(Main.plugin, 2 * 20L, 3L);
		
		// Handles the 2 second 'delay' when you run out of energy.
		Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(Main.plugin, new Runnable() {
			public void run() {
				ClearFatiguePlayers();
			}
		}, 2 * 20L, 25L);
		
		// Remove energy for sprinting. AND Handles starving player visual effects.
		new BukkitRunnable() {
			@Override
			public void run() {
				for(Player p : Bukkit.getOnlinePlayers()) {
					if(p.isSprinting()) {
						if(DuelMechanics.isDamageDisabled(p.getLocation()) && !(DuelMechanics.duel_map.containsKey(p.getName())) && !(TutorialMechanics.onIsland.contains(p.getName()))) {
							continue;
						}
						removeEnergy(p, 0.15F); // ORIGINAL: 0.15F
					}
					
				}
				for(Player p : starving) {
					p.removePotionEffect(PotionEffectType.HUNGER);
					if(p.getFoodLevel() <= 0) {
						p.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 40, 0)); // 80
					} else {
						starving.remove(p.getName());
					}
					//p.setFoodLevel(0);
				}
			}
		}.runTaskTimerAsynchronously(Main.plugin, 2 * 20L, 10L);
		
		log.info("[FatigueMechanics] V1.0 has been enabled.");
	}
	
	public void onDisable() {
		log.info("[FatigueMechanics] V1.0 has been disabled.");
	}
	
	public void blockSprinting() {
		for(Player p : fatigue_effect.keySet()) {
			p.setSprinting(false);
		}
	}
	
	public ItemStack clearModifiers(ItemStack item) {
		net.minecraft.server.v1_7_R2.ItemStack is = CraftItemStack.asNMSCopy(item);
		if(!is.hasTag()) { return item; }
		
		NBTTagCompound tag = is.tag;
		tag.remove("AttributeModifiers");
		
		is.tag = tag;
		is.setTag(tag);
		return CraftItemStack.asBukkitCopy(is);
	}
	
	public static void updateEnergyRegenData(Player p, boolean echo) {
		int old_total_regen = 0;
		if(energy_regen_data.containsKey(p.getName())) {
			old_total_regen = (int) (100 * energy_regen_data.get(p.getName())) - 10;
		}
		
		float new_total_regen = generateEnergyRegenAmount(p);
		int i_new_total_regen = (int) (new_total_regen * 100) - 10;
		energy_regen_data.put(p.getName(), new_total_regen);
		
		if((old_total_regen != i_new_total_regen) && echo == true) {
			if(old_total_regen > i_new_total_regen) {
				p.sendMessage(ChatColor.RED + "-" + (old_total_regen - i_new_total_regen) + "% ENERGY REGEN [" + (i_new_total_regen + 100) + "%]");
			} else {
				p.sendMessage(ChatColor.GREEN + "+" + (i_new_total_regen - old_total_regen) + "% ENERGY REGEN [" + (i_new_total_regen + 100) + "%]");
			}
		}
	}
	
	public void ClearFatiguePlayers() {
		HashMap<Player, Integer> fatigue_effect_mirror = new HashMap<Player, Integer>(fatigue_effect);
		for(Entry<Player, Integer> entry : fatigue_effect_mirror.entrySet()) {
			Player p = entry.getKey();
			int i = entry.getValue();
			
			if(i >= 1) {
				p.removePotionEffect(PotionEffectType.SLOW_DIGGING);
				fatigue_effect.remove(p);
				p.setExp(0.10F);
				updatePlayerLevel(p);
				continue;
			}
			
			i++;
			fatigue_effect.put(p, i);
		}
	}
	
	public static void updatePlayerLevel(Player p) {
		float exp = p.getExp();
		double percent = exp * 100.0D;
		if(percent > 100) {
			percent = 100;
		}
		if(percent < 0) {
			percent = 0;
		}
		p.setLevel((int) percent);
	}
	
	public static float getEnergyPercent(Player p) {
		return p.getExp();
	}
	
	public void addEnergy(Player p, float add) {
		float current_xp = getEnergyPercent(p);
		if(current_xp == 1) { return; }
		p.setExp(getEnergyPercent(p) + add);
		updatePlayerLevel(p);
	}
	
	public static void removeEnergy(Player p, float remove) {
		if(p.hasMetadata("last_energy")) {
			if((System.currentTimeMillis() - p.getMetadata("last_energy").get(0).asLong()) < 75) { return; // Less than 100ms since last energy taken, skip.
			}
		}
		p.setMetadata("last_energy", new FixedMetadataValue(Main.plugin, System.currentTimeMillis()));
		
		float current_xp = getEnergyPercent(p);
		old_energy.put(p.getName(), current_xp);
		if(current_xp <= 0) { return; }
		if((getEnergyPercent(p) - remove) <= 0) {
			fatigue_effect.put(p, 0);
			p.setExp(0.0F);
			updatePlayerLevel(p);
			//sendPotionPacket(p, new PotionEffect(PotionEffectType.SLOW_DIGGING, 50, 4));
			p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 50, 4));
			// Add slow swing
			return;
		}
		p.setExp(getEnergyPercent(p) - remove);
		updatePlayerLevel(p);
	}
	
	public void replenishEnergy() {
		for(Player p : Bukkit.getServer().getOnlinePlayers()) {
			if(p.getPlayerListName().equalsIgnoreCase("")) {
				continue;
			}
			if(p.hasMetadata("NPC")) {
				continue;
			}
			
			if(getEnergyPercent(p) == 1.0F) {
				continue;
			}
			if(getEnergyPercent(p) > 1.0F) {
				p.setExp(1.0F);
				updatePlayerLevel(p);
				continue;
			}
			
			if(!fatigue_effect.containsKey(p)) { // If they don't have the 2 second 'no regen' delay.
				float res_amount = getEnergyRegainPercent(p.getName());
				if(starving.contains(p)) {
					res_amount = 0.05F;
					// They're starving, static, slow regen rate.
				}
				
				res_amount = res_amount / 6.3F; // 6.3, 5.8, 5
				
				if(ProfessionMechanics.fish_energy_regen.containsKey(p.getName())) {
					res_amount += (ProfessionMechanics.fish_energy_regen.get(p.getName()) / 400.0F);
				}
				
				addEnergy(p, res_amount);
			}
		}
	}
	
	public float getEnergyRegainPercent(String p_name) {
		if(!energy_regen_data.containsKey(p_name)) {
			energy_regen_data.put(p_name, 0.10F);
		}
		
		float energy_regen = energy_regen_data.get(p_name);
		
		if(ItemMechanics.int_data.containsKey(p_name) && (ItemMechanics.int_data.get(p_name) > 0)) {
			double int_mod = 0;
			if(ItemMechanics.int_data.get(p_name) > 0) {
				int_mod = ItemMechanics.int_data.get(p_name);
			}
			
			if(Bukkit.getPlayer(p_name) != null) {
				Player pl = Bukkit.getPlayer(p_name);
				String dmg_data = ItemMechanics.getDamageData(pl.getItemInHand());
				if(dmg_data.contains("int=")) {
					int_mod += Double.parseDouble(dmg_data.split("int=")[1].split(":")[0]);
				}
			}
			
			//energy_regen += (int)(double)((double)energy_regen * (double)(((int_mod * 0.015)/100.0D)));
			energy_regen += (float) ((float) (((int_mod * 0.015F) / 100.0F)));
		}
		
		return energy_regen;
	}
	
	public float getEnergyRegenVal(ItemStack i) {
		String armor_data = getArmorData(i);
		if(armor_data.contains("energy_regen")) {
			int energy_regen_val = Integer.parseInt(armor_data.substring(armor_data.indexOf("energy_regen=") + 13, armor_data.indexOf("@energy_regen_split@")));
			float f_total_regen = ((float) energy_regen_val / 100.0F);
			f_total_regen = f_total_regen + 0.10F;
			return f_total_regen;
		}
		return 0.00F;
	}
	
	public static float generateEnergyRegenAmount(Player p) {
		ItemStack[] is = p.getInventory().getArmorContents();
		int total_regen = 0;
		for(ItemStack armor : is) {
			if(armor.getType() == Material.AIR) {
				continue;
			}
			String armor_data = getArmorData(armor);
			if(armor_data.contains("energy_regen")) {
				int energy_regen_val = Integer.parseInt(armor_data.substring(armor_data.indexOf("energy_regen=") + 13, armor_data.indexOf("@energy_regen_split@")));
				total_regen += energy_regen_val;
			}
		}
		
		// TODO: Uncomment 0.10F on 1.1
		float f_total_regen = ((float) total_regen / 100.0F); //* 0.50F;
		f_total_regen = f_total_regen + 0.10F;
		
		return f_total_regen;
	}
	
	public static String getArmorData(ItemStack i) {
		return ItemMechanics.getArmorData(i);
	}
	
	public static float getEnergyCost(ItemStack i) {
		Material m = i.getType();
		
		if(m == Material.AIR) { return 0.05F; }
		
		if(m == Material.WOOD_SWORD) { return 0.06F; }
		if(m == Material.STONE_SWORD) { return 0.071F; }
		if(m == Material.IRON_SWORD) { return 0.0833F; }
		if(m == Material.DIAMOND_SWORD) { return 0.125F; }
		if(m == Material.GOLD_SWORD) { return 0.135F; }
		
		if(m == Material.WOOD_AXE) { return 0.0721F * 1.1F; }
		if(m == Material.STONE_AXE) { return 0.0833F * 1.1F; }
		if(m == Material.IRON_AXE) { return 0.10F * 1.1F; }
		if(m == Material.DIAMOND_AXE) { return 0.125F * 1.1F; }
		if(m == Material.GOLD_AXE) { return 0.135F * 1.1F; }
		
		if(m == Material.WOOD_SPADE) { return 0.0721F; }
		if(m == Material.STONE_SPADE) { return 0.0833F; }
		if(m == Material.IRON_SPADE) { return 0.10F; }
		if(m == Material.DIAMOND_SPADE) { return 0.125F; }
		if(m == Material.GOLD_SPADE) { return 0.135F; }
		
		if(m == Material.WOOD_HOE) { return 0.0721F / 1.1F; }
		if(m == Material.STONE_HOE) { return 0.0833F / 1.1F; }
		if(m == Material.IRON_HOE) { return 0.10F / 1.1F; }
		if(m == Material.DIAMOND_HOE) { return 0.125F / 1.1F; }
		if(m == Material.GOLD_HOE) { return 0.135F / 1.1F; }
		
		if(m == Material.BOW) { // Arrow shooting. Bow punch will be addressed at event level.
			int tier = ItemMechanics.getItemTier(i);
			if(tier == 1) { return 0.12F; }
			if(tier == 2) { return 0.142F; }
			if(tier == 3) { return 0.1666F; }
			if(tier == 4) { return 0.20F; }
			if(tier == 5) { return 0.25F; }
		}
		
		return 0.10F;
	}
	
	public void disableSprint(final Player p) {
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
			public void run() {
				p.setSprinting(false);
			}
		}, 1L);
	}
	
	public LivingEntity getTarget(Player pl, boolean livingentity) {
		List<Entity> nearbyE = pl.getNearbyEntities(4.0D, 4.0D, 4.0D);
		ArrayList<LivingEntity> livingE = new ArrayList<LivingEntity>();
		
		for(Entity e : nearbyE) {
			if(e instanceof LivingEntity) {
				livingE.add((LivingEntity) e);
			}
		}
		
		LivingEntity target = null;
		BlockIterator bItr = null;
		try {
			bItr = new BlockIterator(pl, 4);
		} catch(IllegalStateException ise) {
			return null;
		}
		Block block;
		Location loc;
		int bx, by, bz;
		double ex, ey, ez;
		// loop through player's line of sight
		while(bItr.hasNext()) {
			block = bItr.next();
			bx = block.getX();
			by = block.getY();
			bz = block.getZ();
			// check for entities near this block in the line of sight
			for(LivingEntity e : livingE) {
				if(!MonsterMechanics.mob_health.containsKey(e) && !(e instanceof Player)) {
					continue; // Not something we'll be damaging.
				}
				loc = e.getLocation();
				ex = loc.getX();
				ey = loc.getY();
				ez = loc.getZ();
				if((bx - .75 <= ex && ex <= bx + 1.75) && (bz - .75 <= ez && ez <= bz + 1.75) && (by - 1 <= ey && ey <= by + 2.5)) {
					// entity is close enough, set target and stop
					target = (LivingEntity) e;
					break;
				}
			}
		}
		
		return target;
	}
	
	public Player getTarget(Player trader) {
		List<Entity> nearbyE = trader.getNearbyEntities(2.0D, 2.0D, 2.0D);
		ArrayList<Player> livingE = new ArrayList<Player>();
		
		for(Entity e : nearbyE) {
			if(e.getType() == EntityType.PLAYER) {
				livingE.add((Player) e);
			}
		}
		
		Player target = null;
		BlockIterator bItr = null;
		try {
			bItr = new BlockIterator(trader, 2);
		} catch(IllegalStateException ise) {
			return null;
		}
		Block block;
		Location loc;
		int bx, by, bz;
		double ex, ey, ez;
		// loop through player's line of sight
		while(bItr.hasNext()) {
			block = bItr.next();
			bx = block.getX();
			by = block.getY();
			bz = block.getZ();
			// check for entities near this block in the line of sight
			for(LivingEntity e : livingE) {
				loc = e.getLocation();
				ex = loc.getX();
				ey = loc.getY();
				ez = loc.getZ();
				if((bx - .75 <= ex && ex <= bx + 1.75) && (bz - .75 <= ez && ez <= bz + 1.75) && (by - 1 <= ey && ey <= by + 2.5)) {
					// entity is close enough, set target and stop
					target = (Player) e;
					break;
				}
			}
		}
		
		return target;
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
	public void onPlayerAnimation(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		
		ItemStack weapon = e.getItem();
		
		if(weapon == null || !e.hasItem()) {
			weapon = new ItemStack(Material.AIR);
		}
		
		if(!(e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK)) { return; }
		
		if(p.getWorld().getName().equalsIgnoreCase(main_world_name) && e.hasBlock() && (e.getClickedBlock().getType() == Material.LONG_GRASS)) {
			e.setCancelled(true);
			e.setUseItemInHand(Result.DENY);
			return;
		}
		
		if(ItemMechanics.getDamageData(weapon).equalsIgnoreCase("no") && (weapon.getType() != Material.AIR || (weapon.getType() == Material.AIR && !p.getWorld().getName().equalsIgnoreCase(main_world_name)))) {
			// Not a weapon, who cares. Will do 1 DMG.
			return;
		}
		
		if(ProfessionMechanics.isSkillItem(weapon)) { return; // It's a skill item.
		}
		
		//if(getTarget(p) != null || (!p.getWorld().getName().equalsIgnoreCase(main_world_name) && !InstanceMechanics.isInstance(p.getWorld().getName()))){return;} // They have a target. The energy will be taken on damage event thingy.
		// TODO: Make all swings take energy, beware of mining!!
		/*Block b = null;
		Material m = null;
		try{
			b = p.getTargetBlock(ProfessionMechanics.transparent, 4);
			m = b.getType();
		} catch(IllegalStateException ise){
			m = Material.AIR;
			//return; // Don't take away energy.
		}
		if(m == Material.LONG_GRASS){return;} // They might have a target, they might not, but they're hitting a solid block. (right click anvil fix).*/
		
		/*Iif(ItemMechanics.getDamageData(weapon).equalsIgnoreCase("no") && weapon.getType() != Material.AIR){
			// Not a weapon, who cares. Will do 1 DMG.
			return;
		}*/
		
		float energy_cost = getEnergyCost(weapon);
		
		if(weapon.getType() == Material.BOW) {
			energy_cost = energy_cost + 0.15F; // Add 15% more for a bow punch than arrow.
			p.playSound(p.getLocation(), Sound.PISTON_EXTEND, 1F, 1.5F);
			// TODO: Knockback!
		}
		
		if(fatigue_effect.containsKey(p)) {
			e.setUseItemInHand(Result.DENY);
			e.setCancelled(true);
			if(p.getWorld().getName().equalsIgnoreCase(Bukkit.getWorlds().get(0).getName()) || InstanceMechanics.isInstance(p.getWorld().getName())) {
				//p.playEffect(EntityEffect.HURT);
				p.playSound(p.getLocation(), Sound.WOLF_PANT, 10F, 1.5F);
			}
			return;
		}
		
		if(last_attack.containsKey(p.getName()) && (System.currentTimeMillis() - last_attack.get(p.getName())) < 100) {
			// Less than 100ms since last attack. -- Don't take any energy.
			e.setUseItemInHand(Result.DENY);
			e.setCancelled(true);
			return;
		}
		
		//if(getTarget(p, true) == null){ // If not null, take on damage event.
		removeEnergy(p, energy_cost);
		//}
		
		/*if(b == null || m == Material.AIR){
			removeEnergy(p, energy_cost);
		}
		else{
			if(getTarget(p, true) == null){
				// Hitting a block -- animation event is called 3x cause minecraft sucks,
				energy_cost = (energy_cost / 3);
			}
			removeEnergy(p, energy_cost);
		}*/
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
		if(e.getDamager() instanceof Player) {
			if(e.getCause() == DamageCause.CUSTOM) { return; }
			Player p = (Player) e.getDamager();
			ItemStack weapon = p.getItemInHand();
			
			float energy_cost = getEnergyCost(weapon);
			
			if(weapon.getType() == Material.BOW) {
				energy_cost = energy_cost + 0.15F; // Add 15% more for a bow punch than arrow.
				// TODO: Knockback!
			}
			
			if(fatigue_effect.containsKey(p)) {
				e.setCancelled(true);
				if(p.getWorld().getName().equalsIgnoreCase(Bukkit.getWorlds().get(0).getName()) || InstanceMechanics.isInstance(p.getWorld().getName())) {
					//p.playEffect(EntityEffect.HURT);
					p.playSound(p.getLocation(), Sound.WOLF_PANT, 12F, 1.5F);
					if(!(e.getEntity() instanceof Player)) {
						try {
							ParticleEffect.sendToLocation(ParticleEffect.CRIT, e.getEntity().getLocation().add(0, 1, 0), new Random().nextFloat(), new Random().nextFloat(), new Random().nextFloat(), 0.75F, 40);
						} catch(Exception e1) {
							e1.printStackTrace();
						}
					}
				}
				return;
			}
			
			if(last_attack.containsKey(p.getName()) && (System.currentTimeMillis() - last_attack.get(p.getName())) < 100) {
				// Less than 100ms since last attack.
				if(!(ItemMechanics.processing_proj_event.contains(p.getName()))) {
					e.setCancelled(true);
					e.setDamage(0);
					return;
				}
			}
			
			last_attack.put(p.getName(), System.currentTimeMillis());
			
			if(!ItemMechanics.processing_proj_event.contains(p.getName()) && (!ItemMechanics.getDamageData(weapon).equalsIgnoreCase("no") || (p.getWorld().getName().equalsIgnoreCase(main_world_name)))) {
				// Remove energy.
				removeEnergy(p, energy_cost);
			}
			
			if(!e.isCancelled() && e.getDamage() > 0 && p.getItemInHand() != null && p.getItemInHand().getType() != Material.AIR) {
				ItemStack in_hand = p.getItemInHand();
				if(in_hand.getType() == Material.WOOD_SWORD || in_hand.getType() == Material.STONE_SWORD || in_hand.getType() == Material.IRON_SWORD || in_hand.getType() == Material.DIAMOND_SWORD || in_hand.getType() == Material.GOLD_SWORD || in_hand.getType() == Material.WOOD_AXE || in_hand.getType() == Material.STONE_AXE || in_hand.getType() == Material.IRON_AXE || in_hand.getType() == Material.DIAMOND_AXE || in_hand.getType() == Material.GOLD_AXE || in_hand.getType() == Material.WOOD_SPADE || in_hand.getType() == Material.STONE_SPADE || in_hand.getType() == Material.IRON_SPADE || in_hand.getType() == Material.DIAMOND_SPADE || in_hand.getType() == Material.GOLD_SPADE || in_hand.getType() == Material.BOW) {
					// It's a weapon!
					if(DuelMechanics.isDamageDisabled(p.getLocation())) { return; } // No durabillity loss in safe zones.
					if(DuelMechanics.duel_map.containsKey(p.getName())) { return; }
					
					RepairMechanics.subtractCustomDurability(p, in_hand, 1, "wep");
					//log.info(String.valueOf(getCustomDurability(in_hand, "wep")));
				}
				
			}
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void FoodLevelChange(FoodLevelChangeEvent event) {
		if(!(event.getEntity() instanceof Player)) { return; }
		Player p = (Player) event.getEntity();
		if(event.getFoodLevel() < p.getFoodLevel()) { // Make sure they're loosing food level.
			int r = new Random().nextInt(4); // 0, 1, 2, 3
			if(r >= 1) { // Cancel 75% of the time.
				event.setCancelled(true);
				return;
			}
		}
		if(event.getFoodLevel() > 0 && starving.contains(p)) {
			starving.remove(p);
			p.removePotionEffect(PotionEffectType.HUNGER);
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
	public void onPlayerExpChangeEvent(PlayerExpChangeEvent e) {
		e.setAmount(0);
	}
	
	@EventHandler(priority = EventPriority.LOW)
	public void onEntityDeath(EntityDeathEvent e) {
		e.setDroppedExp(0);
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerJoin(PlayerJoinEvent e) {
		final Player p = e.getPlayer();
		if(p.getFoodLevel() <= 0) {
			if(!(starving.contains(p))) {
				starving.add(p);
				Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
					public void run() {
						p.sendMessage(ChatColor.DARK_GREEN + "" + ChatColor.BOLD + "                        *STARVING*");
					}
				}, 20L);
			}
		}
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		starving.remove(p);
		sprinting.remove(p);
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onEntityDamage(EntityDamageEvent e) {
		/*if(e.getCause() == DamageCause.POISON){
			if(e.getEntity() instanceof Player){
				Player p = (Player)e.getEntity();
				if(starving.contains(p)){
					e.setCancelled(true);
					e.setDamage(0);
					return;
				}
			}
		}*/
		if(e.getCause() == DamageCause.STARVATION) {
			e.setCancelled(true);
			e.setDamage(0);
			
			Player p = (Player) e.getEntity();
			if(!(p.hasPotionEffect(PotionEffectType.HUNGER))) {
				p.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 40, 0)); // 50
				if(!(starving.contains(p))) {
					starving.add(p);
					p.sendMessage(ChatColor.DARK_GREEN + "" + ChatColor.BOLD + "                        *STARVING*");
				}
			}
			//removeEnergy(p, 0.20F);
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerToggleSprint(PlayerToggleSprintEvent e) {
		Player p = e.getPlayer();
		
		boolean dmg_disabled = DuelMechanics.isDamageDisabled(p.getLocation());
		
		if(p.getExp() <= 0.0F && (!dmg_disabled || TutorialMechanics.onIsland.contains(p))) { //fatigue_effect.containsKey(p)
			disableSprint(p);
			sprinting.remove(p);
			return;
		} else if(e.isSprinting()) {
			sprinting.add(p);
			if(!dmg_disabled || TutorialMechanics.onIsland.contains(p)) {
				removeEnergy(p, 0.15F);
			}
		} else {
			sprinting.remove(p);
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerFireBow(EntityShootBowEvent e) {
		if(!(e.getEntity().getType() == EntityType.PLAYER)) { return; }
		Player p = (Player) e.getEntity();
		ItemStack i = p.getItemInHand();
		
		float energy_cost = getEnergyCost(i);
		
		if(p.getExp() <= 0.0F) { //fatigue_effect.containsKey(p)
			e.setCancelled(true);
			return;
		}
		
		removeEnergy(p, energy_cost);
	}
	
	@EventHandler(priority = EventPriority.LOW)
	public void onEntityDamageEntity(EntityDamageByEntityEvent e) {
		
		if(e.getDamager().getType() == EntityType.PLAYER) {
			Player p = (Player) e.getDamager();
			
			float energy = p.getExp();
			if(old_energy.containsKey(p.getName())) {
				energy = old_energy.get(p.getName()); // Fix for player interact taking the energy.
			}
			
			if(p.getExp() <= 0.0F) {
				if(energy <= 0.0F) {
					e.setCancelled(true);
					e.setDamage(0);
					return;
				}
				old_energy.put(p.getName(), 0.0F);
			}
		}
	}
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e) {
		Player p = e.getEntity();
		p.removePotionEffect(PotionEffectType.HUNGER);
		sprinting.remove(p);
		fatigue_effect.remove(p);
		starving.remove(p);
	}
	
}
