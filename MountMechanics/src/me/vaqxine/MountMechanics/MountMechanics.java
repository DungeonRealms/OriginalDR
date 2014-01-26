package me.vaqxine.MountMechanics;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

import me.vaqxine.AchievmentMechanics.AchievmentMechanics;
import me.vaqxine.CommunityMechanics.CommunityMechanics;
import me.vaqxine.DuelMechanics.DuelMechanics;
import me.vaqxine.EcashMechanics.EcashMechanics;
import me.vaqxine.HealthMechanics.HealthMechanics;
import me.vaqxine.Hive.Hive;
import me.vaqxine.Hive.ParticleEffect;
import me.vaqxine.ItemMechanics.ItemMechanics;
import me.vaqxine.KarmaMechanics.KarmaMechanics;
import me.vaqxine.MoneyMechanics.MoneyMechanics;
import me.vaqxine.PetMechanics.ConnectionPool;
import me.vaqxine.PetMechanics.PetMechanics;
import me.vaqxine.ProfessionMechanics.ProfessionMechanics;
import me.vaqxine.RealmMechanics.RealmMechanics;
import me.vaqxine.ShopMechanics.ShopMechanics;
import net.minecraft.server.v1_7_R1.EntityHorse;
import net.minecraft.server.v1_7_R1.EntityInsentient;
import net.minecraft.server.v1_7_R1.EntityLeash;
import net.minecraft.server.v1_7_R1.EntityPig;
import net.minecraft.server.v1_7_R1.EntityPlayer;
import net.minecraft.server.v1_7_R1.GenericAttributes;
import net.minecraft.server.v1_7_R1.NBTTagList;
import net.minecraft.server.v1_7_R1.Packet;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_7_R1.CraftServer;
import org.bukkit.craftbukkit.v1_7_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_7_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_7_R1.entity.CraftHorse;
import org.bukkit.craftbukkit.v1_7_R1.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_7_R1.entity.CraftPig;
import org.bukkit.craftbukkit.v1_7_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_7_R1.inventory.CraftItemStack;
import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Horse.Variant;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Horse.Color;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.spigotmc.event.entity.EntityDismountEvent;

// .TODO: Code death mechanics for dropping items of mule
// .TODO: Code upgrade mechanics
// TODO: Code shard merchant for upgrades
// .TODO: Fix random vertical TP
// TODO: (?) Code cross-server trasfer of the mount itself?
// TODO: /shard <#> quick shard hop

public class MountMechanics extends JavaPlugin implements Listener {
	static Logger log = Logger.getLogger("Minecraft");

	public static ItemStack t1_mule = ItemMechanics.signCustomItem(Material.LEASH, (short)1, ChatColor.GREEN.toString() + "Old Storage Mule", ChatColor.RED.toString() 
			+ "Storage Size: 9 Items " + "," + ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + "An old worn-out storage mule." + "," + ChatColor.GRAY.toString() + "Permanent Untradeable");

	public static ItemStack t2_mule = ItemMechanics.signCustomItem(Material.LEASH, (short)1, ChatColor.AQUA.toString() + "Adventurer's Storage Mule", ChatColor.RED.toString() 
			+ "Storage Size: 18 Items " + "," + ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + "A storage mule with an extended inventory." + "," + ChatColor.GRAY.toString() + "Permanent Untradeable");

	public static ItemStack t3_mule = ItemMechanics.signCustomItem(Material.LEASH, (short)1, ChatColor.LIGHT_PURPLE.toString() + "Royal Storage Mule", ChatColor.RED.toString() 
			+ "Storage Size: 27 Items " + "," + ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + "A royal storage mule with a huge inventory." + "," + ChatColor.GRAY.toString() + "Permanent Untradeable");

	public static ItemStack t2_mule_upgrade = ItemMechanics.signCustomItem(Material.TRAPPED_CHEST, (short)1, ChatColor.AQUA.toString() + "Adventurer's Storage Mule Chest", ChatColor.RED.toString() 
			+ "18 Max Storage Size" + "," + ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + "Apply to your " + ChatColor.GREEN.toString() + "Old Storage Mule" + ChatColor.GRAY.toString() + " to expand its inventory!" + "," + ChatColor.GRAY.toString() + "Permanent Untradeable");

	public static ItemStack t3_mule_upgrade = ItemMechanics.signCustomItem(Material.TRAPPED_CHEST, (short)1, ChatColor.LIGHT_PURPLE.toString() + "Royal Storage Mule Chest", ChatColor.RED.toString() 
			+ "27 Max Storage Size" + "," + ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + "Apply to your " + ChatColor.AQUA.toString() + "Adventurer's Storage Mule" + "," + ChatColor.GRAY.toString() + "to further expand its inventory!" + "," + ChatColor.GRAY.toString() + "Permanent Untradeable");

	public static ItemStack t2_horse_mount = ItemMechanics.signCustomItem(Material.SADDLE, (short)1, ChatColor.GREEN.toString() + "Old Horse Mount", ChatColor.RED.toString() + "Speed: 120%" + "," + ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + "An old brown starter horse." + "," + ChatColor.GRAY.toString() + "Permanent Untradeable");
	public static ItemStack t3_horse_mount = ItemMechanics.signCustomItem(Material.SADDLE, (short)1, ChatColor.AQUA.toString() + "Traveler's Horse Mount", ChatColor.RED.toString() + "Speed: 140%" + "," + ChatColor.RED.toString() + "Jump: 105%" + "," + ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + "A standard healthy horse." + "," + ChatColor.GRAY.toString() + "Permanent Untradeable");
	public static ItemStack t4_horse_mount = ItemMechanics.signCustomItem(Material.SADDLE, (short)1, ChatColor.LIGHT_PURPLE.toString() + "Knight's Horse Mount", ChatColor.RED.toString() + "Speed: 160%" + "," + ChatColor.RED.toString() + "Jump: 110%" + "," + ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + "A fast well-bred horse." + "," + ChatColor.GRAY.toString() + "Permanent Untradeable");
	public static ItemStack t5_horse_mount = ItemMechanics.signCustomItem(Material.SADDLE, (short)1, ChatColor.YELLOW.toString() + "War Stallion Mount", ChatColor.RED.toString() + "Speed: 200%" + "," + ChatColor.RED.toString() + "Jump: 120%" + "," + ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + "A trusty powerful steed." + "," + ChatColor.GRAY.toString() + "Permanent Untradeable");

	public static ItemStack t3_horse_armor = ItemMechanics.signCustomItem(Material.getMaterial(417), (short)1, ChatColor.AQUA.toString() + "Iron Horse Armor", ChatColor.RED.toString() + "Speed: 140%" + "," + ChatColor.RED.toString() + "Jump: 105%" + "," + ChatColor.RED.toString() + ChatColor.BOLD + "REQ:" + ChatColor.GREEN.toString() + " Old Horse Mount" + "," + ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + "A well made Iron-plated armor piece for your mount.");
	public static ItemStack t4_horse_armor = ItemMechanics.signCustomItem(Material.getMaterial(419), (short)1, ChatColor.LIGHT_PURPLE.toString() + "Diamond Horse Armor", ChatColor.RED.toString() + "Speed: 160%" + "," + ChatColor.RED.toString() + "Jump: +110%" + "," + ChatColor.RED.toString() + ChatColor.BOLD.toString() + "REQ:" + ChatColor.AQUA + " Traveler's Horse Mount" + "," + ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + "Powerful brilliant diamond horse armor.");
	public static ItemStack t5_horse_armor = ItemMechanics.signCustomItem(Material.getMaterial(418), (short)1, ChatColor.YELLOW.toString() + "Gold Horse Armor", ChatColor.RED.toString() + "Speed: +200%" + "," + ChatColor.RED.toString() + "Jump: +120%" + "," + ChatColor.RED.toString() + ChatColor.BOLD.toString() + "REQ:" + ChatColor.LIGHT_PURPLE.toString() + " Knight's Horse Mount" + "," + ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + "A legendary forged intricate golden armour piece.");

	ItemStack divider = new ItemStack(Material.THIN_GLASS, 1);

	public static HashMap<String, Integer> mount_being_bought = new HashMap<String, Integer>();
	// Used for Animal Tamer shop.

	// DEPRECIATED, was used with pig mount.
	//public static HashMap<String, Long> jump_delay = new HashMap<String, Long>();

	public static HashMap<String, Location> horse_safe_location = new HashMap<String, Location>();
	// Used as a hack for horse movement. Still needs some work.

	public static ConcurrentHashMap<String, Integer> summon_mount = new ConcurrentHashMap<String, Integer>();
	// Mount summoning delay.

	public static HashMap<String, Location> summon_location = new HashMap<String, Location>();
	// Mount summoning location.

	public static HashMap<String, ItemStack> summon_item = new HashMap<String, ItemStack>();
	// Determines type of mount that is summoned. (horse/mule)

	public static ConcurrentHashMap<String, Entity> mount_map = new ConcurrentHashMap<String, Entity>();
	// Player_Name, Their mount

	public static ConcurrentHashMap<Entity, String> inv_mount_map = new ConcurrentHashMap<Entity, String>();
	// ENTITY, Mount Owner (if exists)

	public static ConcurrentHashMap<String, Entity> mule_map = new ConcurrentHashMap<String, Entity>();
	// Player_Name, Their mule

	public static ConcurrentHashMap<Entity, String> inv_mule_map = new ConcurrentHashMap<Entity, String>();
	// Mule, Owner (player_name)

	public static ConcurrentHashMap<String, Inventory> mule_inventory = new ConcurrentHashMap<String, Inventory>();
	// PLAYER_NAME, Mule inventory 

	public static HashMap<String, String> mule_itemlist_string = new HashMap<String, String>();
	// PLAYER_NAME, Mule item list (string)

	Inventory TradeWindow = Bukkit.createInventory(null, 18, "Animal Tamer");

	CopyOnWriteArrayList<String> in_shop = new CopyOnWriteArrayList<String>();
	// Determines if a player is in the animal tamer shop.

	ArrayList<Entity> tied_to_ground = new ArrayList<Entity>();
	// Contains all mules tied to the ground.

	static MountMechanics instance = null;

	HashSet<Byte> transparent = new HashSet<Byte>();
	// DEPRECIATED (?)
	// Transparent blocks that should be ignored on getTargetBlock()

	@SuppressWarnings("deprecation")
	public void onEnable(){
		instance = this;
		getServer().getPluginManager().registerEvents(this, this);

		TradeWindow.setItem(0, ShopMechanics.setPrice(t2_horse_mount, 3000));
		TradeWindow.setItem(1, ShopMechanics.setPrice(t3_horse_armor, 7000));
		TradeWindow.setItem(2, ShopMechanics.setPrice(t4_horse_armor, 15000));
		TradeWindow.setItem(3, ShopMechanics.setPrice(t5_horse_armor, 30000));
		
		TradeWindow.setItem(9, ShopMechanics.setPrice(t1_mule, 4000));

		transparent.add((byte)0);
		transparent.add((byte)31);
		transparent.add((byte)8);
		transparent.add((byte)9);
		transparent.add((byte)40);
		transparent.add((byte)37);
		transparent.add((byte)38);
		transparent.add((byte)39);
		transparent.add((byte)90);

		transparent.add((byte)53);
		transparent.add((byte)67);
		transparent.add((byte)108);
		transparent.add((byte)109);
		transparent.add((byte)114);
		transparent.add((byte)128);
		transparent.add((byte)134);
		transparent.add((byte)135);
		transparent.add((byte)136);
		transparent.add((byte)156);

		this.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			public void run() {
				for(Entry<String, Integer> data : summon_mount.entrySet()){
					final String p_name = data.getKey();
					int seconds = data.getValue();

					if(Bukkit.getPlayer(p_name) == null){
						summon_mount.remove(p_name);
						summon_location.remove(p_name);
						summon_item.remove(p_name);
						continue;
					}

					final Player pl = Bukkit.getPlayer(p_name);
					seconds--;

					if(seconds <= 0){
						summon_mount.remove(p_name);
						summon_location.remove(p_name);

						ItemStack is = summon_item.get(p_name);
						int tier = ItemMechanics.getItemTier(is);

						summon_item.remove(p_name);
						if(isMule(is)){
							Entity emule = pl.getWorld().spawnEntity(pl.getTargetBlock(transparent, 4).getLocation().add(0, 1, 0), EntityType.HORSE);
							Horse h = (Horse)emule;
							EntityHorse eh = ((EntityHorse) ((CraftHorse)h).getHandle());

							h.setVariant(Variant.DONKEY);
							h.setCarryingChest(true);
							h.setTamed(true);
							h.setOwner((AnimalTamer)pl);
							h.setColor(Color.BROWN);

							LivingEntity le = (LivingEntity)emule;
							le.setCustomNameVisible(false);
							le.setCustomName(ProfessionMechanics.getTierColor(tier) + pl.getName() + "'s " + is.getItemMeta().getDisplayName());
							
							// ((EntityInsentient)((CraftEntity)(Entity)emule).getHandle()).b((net.minecraft.server.v1_7_R1.Entity)((CraftPlayer)pl).getHandle(), true);
							// Tie them to a freaking rope
							h.setLeashHolder(pl);

							eh.getAttributeInstance(GenericAttributes.d).setValue(0.35D); // 0.2 Default (speed)

							try{
								ParticleEffect.sendToLocation(ParticleEffect.CRIT, emule.getLocation().add(0, 1.5, 0), new Random().nextFloat(), new Random().nextFloat(), new Random().nextFloat(), 0.25F, 60);
							} catch(Exception err){err.printStackTrace();}
							//pl.getWorld().spawnParticle(emule.getLocation().add(0, 1.5, 0), Particle.CRIT, 0.25F, 60);

							mule_map.put(p_name, emule);
							inv_mule_map.put(emule, p_name);	
						}
						if(isMount(is)){
							Entity emount = pl.getWorld().spawnEntity(pl.getLocation(), EntityType.HORSE);
							Horse h = (Horse)emount;
							EntityHorse eh = ((EntityHorse) ((CraftHorse)h).getHandle());

							h.setTamed(true);
							h.setOwner((AnimalTamer)pl);
							h.setColor(Color.BROWN);

							/*if(p_name.equalsIgnoreCase("Vaquxine")){
								h.setColor(Color.WHITE);
								Entity boat = pl.getWorld().spawnEntity(pl.getLocation(), EntityType.BOAT);
								boat.setPassenger(pl);
							}*/
							if(p_name.equalsIgnoreCase("availer")){
								h.setColor(Color.WHITE);
							}

							boolean skeleton_skin = EcashMechanics.hasSkeletonHorseSkin(pl);
							boolean undead_skin = EcashMechanics.hasZombieHorseSkin(pl);

							if(undead_skin){
								h.setVariant(Variant.UNDEAD_HORSE);
							}

							if(skeleton_skin){
								h.setVariant(Variant.SKELETON_HORSE);
							}							

							h.setDomestication(100);
							eh.inventoryChest.setItem(0, CraftItemStack.asNMSCopy(new ItemStack(Material.SADDLE)));

							if(tier == 3){
								if(!undead_skin && !skeleton_skin){
									eh.inventoryChest.setItem(1, CraftItemStack.asNMSCopy(new ItemStack(Material.getMaterial(417))));
								}
								eh.getAttributeInstance(GenericAttributes.d).setValue(0.25D); // 0.2 Default
								eh.getAttributeInstance(eh.attributeJumpStrength).setValue(0.75D); // 0.7 Default
							}
							if(tier == 4){
								if(!undead_skin && !skeleton_skin){
									eh.inventoryChest.setItem(1, CraftItemStack.asNMSCopy(new ItemStack(Material.getMaterial(419))));
								}
								eh.getAttributeInstance(GenericAttributes.d).setValue(0.30D);
								eh.getAttributeInstance(eh.attributeJumpStrength).setValue(0.80D);
							}
							if(tier == 5){
								if(!undead_skin && !skeleton_skin){
									eh.inventoryChest.setItem(1, CraftItemStack.asNMSCopy(new ItemStack(Material.getMaterial(418))));
								}
								eh.getAttributeInstance(GenericAttributes.d).setValue(0.40D);
								eh.getAttributeInstance(eh.attributeJumpStrength).setValue(0.9D);
							}
							
							h.setPassenger(pl);
							
							if(mule_map.containsKey(pl.getName())){
								Entity ent = mule_map.get(pl.getName());
								setLeash(ent, pl);
							}
							
							mount_map.put(pl.getName(), emount);
							inv_mount_map.put(emount, pl.getName());

							try{
								ParticleEffect.sendToLocation(ParticleEffect.CRIT, emount.getLocation().add(0, 1, 0), new Random().nextFloat(), new Random().nextFloat(), new Random().nextFloat(), 0.5F, 80);
							} catch(Exception err){err.printStackTrace();}
							
							//pl.getWorld().spawnParticle(emount.getLocation().add(0, 1, 0), Particle.CRIT, 0.5F, 80);

							instance.getServer().getScheduler().scheduleSyncDelayedTask(instance, new Runnable() {
								public void run() {
									if(!(summon_mount.containsKey(p_name))){
										return;
									}
									for(Player player : Bukkit.getOnlinePlayers()){
										player.showPlayer(pl);
									}
								}
							}, 10L);
						}

						continue;
					}

					pl.sendMessage(ChatColor.WHITE + "" + ChatColor.BOLD + "SUMMONING" + ChatColor.WHITE + " ... " + seconds + ChatColor.BOLD + "s");

					ItemStack is = summon_item.get(p_name);
					if(isMount(is)){
						try{
							ParticleEffect.sendToLocation(ParticleEffect.SPELL, pl.getLocation().add(0, 0.15, 0), new Random().nextFloat(), new Random().nextFloat(), new Random().nextFloat(), 0.5F, 80);
						} catch(Exception err){err.printStackTrace();}
						
						//pl.getWorld().spawnParticle(pl.getLocation().add(0, 0.15, 0), Particle.SPELL, 0.5F, 80);
					}
					else if(isMule(is)){
						try{
							ParticleEffect.sendToLocation(ParticleEffect.SPELL, pl.getLocation().add(0, 0.25, 0), new Random().nextFloat(), new Random().nextFloat(), new Random().nextFloat(), 0.5F, 75);
						} catch(Exception err){err.printStackTrace();}
						//pl.getWorld().spawnParticle(pl.getLocation().add(0, 0.25, 0), Particle.CRIT, 0.5F, 75);
						//pl.getWorld().spawnParticle(pl.getTargetBlock(ProfessionMechanics.transparent, 4).getLocation().add(0, 1.0, 0), Particle.CRIT, 0.5F, 75);
					}

					summon_mount.put(p_name, seconds);
				}
			}
		}, 5 * 20L, 20L);

		this.getServer().getScheduler().scheduleAsyncRepeatingTask(this, new Runnable() {
			public void run() {
				for(Entity mount : inv_mount_map.keySet()){
					if(Bukkit.getPlayer(inv_mount_map.get(mount)) != null){
						Player prider = (Player)Bukkit.getPlayer(inv_mount_map.get(mount));
						Player on_horse = null;
						LivingEntity lmount = (LivingEntity)mount;
						if(lmount.getPassenger() != null){
							on_horse = (Player)lmount.getPassenger();
						}
						if(lmount.getPassenger() == null || (prider != null && (HealthMechanics.in_combat.containsKey(prider.getName()) || (on_horse != null && !(prider.getName()).equalsIgnoreCase(on_horse.getName()))))){ // (lmount.getPassenger() == null && !(no_eject.contains(prider.getName()))) || 
							inv_mount_map.remove(mount);
							mount_map.remove(prider.getName());
							mount.eject();
							mount.remove();
							continue;
						}
					}
				}

				/*for(Entry<Entity, String> entry : inv_mule_map.entrySet()){
					String p_name = entry.getValue();
					Entity ent = entry.getKey();
					if(Bukkit.getPlayer(p_name) != null){
						Player pl = Bukkit.getPlayer(p_name);
						Location to_loc = null;
						to_loc = pl.getLocation();

						walkTo((LivingEntity)ent, to_loc.getX(), to_loc.getY() + 1, to_loc.getZ(), 1.0F);
					}
				}*/

			}
		}, 5 * 20L, 1L);

		this.getServer().getScheduler().scheduleAsyncRepeatingTask(this, new Runnable() {
			public void run() {
				for(Entry<Entity,String> data : inv_mount_map.entrySet()){
					Entity mount = data.getKey();
					String p_name = data.getValue();
					
					if(getServer().getPlayer(p_name) != null){
						Player p_rider = (Player)getServer().getPlayer(p_name);
						if(p_rider.getVehicle() == null){
							inv_mount_map.remove(mount);
							mount.remove();
							continue;
						}
						
						Horse h = (Horse)p_rider.getVehicle();

						String rider_align = KarmaMechanics.getRawAlignment(p_rider.getName());
						Location to_loc = h.getLocation();

						if((rider_align.equalsIgnoreCase("evil") && DuelMechanics.isPvPDisabled(to_loc)) || DuelMechanics.isRestrictedArea(to_loc)){
								h.eject();
								h.remove();
								mount_map.remove(p_rider.getName());
								inv_mount_map.remove(mount);
								
								if(horse_safe_location.containsKey(p_name)){
									p_rider.teleport(horse_safe_location.get(p_name));
								}
								else{
									// Cyrennica.
									p_rider.teleport(new Location(Bukkit.getWorlds().get(0), -378, 84, 355, 37F, 1F));
								}
							

							p_rider.updateInventory();

							if(DuelMechanics.isPvPDisabled(to_loc)){
								p_rider.sendMessage(ChatColor.RED + "You " + ChatColor.UNDERLINE + "cannot" + ChatColor.RED + " enter " + ChatColor.BOLD.toString() + "NON-PVP" + ChatColor.RED + " zones with a chaotic alignment.");
							}
							else if(DuelMechanics.isRestrictedArea(to_loc)){
								p_rider.sendMessage(ChatColor.GRAY + ChatColor.ITALIC.toString() + "A magical force prevents your mount from going in that direction.");
							}

							return;
						}

						// Store this as a "safe" location.
						horse_safe_location.put(p_name, p_rider.getLocation());
					
					}
				}
			}
		}, 20 * 20L, 10L);


		this.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			public void run() {
				for(Entry<Entity, String> data : inv_mule_map.entrySet()){
					Entity ent = data.getKey();
					String p_name = data.getValue();

					if(Bukkit.getPlayer(p_name) == null){
						ent.remove();
						inv_mule_map.remove(ent);
						continue;
					}

					Player pl = Bukkit.getPlayer(p_name);
					if(!pl.getWorld().getName().equalsIgnoreCase(ent.getWorld().getName()) || pl.getLocation().distanceSquared(ent.getLocation()) >= 1600){ // 40 blocks.
						// Teleport, leash mount to player.
						ent.teleport(pl);
						setLeash(ent, pl);
						tied_to_ground.remove(ent);
					}
				}
			}
		}, 20 * 20L, 4 * 20L);


		log.info("[MountMechanics] has been ENABLED.");
	}

	public void onDisable() {
		log.info("[MountMechanics] has been disabled.");
	}

	public void pushAwayEntity(Player p, Entity entity, double speed) {
		if(entity instanceof Horse){
			return;
		}
		// Get velocity unit vector:
		org.bukkit.util.Vector unitVector = entity.getLocation().toVector().subtract(p.getLocation().toVector()).normalize();
		// Set speed and push entity:
		entity.setVelocity(unitVector.multiply(speed));
	}
	
	public static void setLeash(Entity emule, Player pl){
		// TODO: Remove arrow they may be tied to.
		//((EntityInsentient)((CraftEntity)(Entity)emule).getHandle()).b((net.minecraft.server.v1_7_R1.Entity)((CraftPlayer)pl).getHandle(), true);
		((Horse)emule).setLeashHolder(pl);
	}

	public static void setLeash(Entity emule, Location target_loc){
		Entity ent = target_loc.getWorld().spawnEntity(target_loc.subtract(0, 2, 0), EntityType.ARROW);
		((Horse)emule).setLeashHolder(ent);
		//((EntityInsentient)((CraftEntity)(Entity)emule).getHandle()).b((net.minecraft.server.v1_7_R1.Entity)((CraftEntity)ent).getHandle(), true);
	}

	public boolean leashedToGround(Entity ent){

		if(tied_to_ground.contains(ent)){
			return true;
		}
		return false;

		/*EntityInsentient ei = ((EntityInsentient)((CraftEntity)ent).getHandle());
		EntityLeash el = (EntityLeash)ei.bI();
		if(el.getBukkitEntity() instanceof Arrow){
			return true;
		}
		return false;*/
	}	

	public static ItemStack setShardPrice(ItemStack i, double price, int tier){
		boolean rename = false;
		String o_name = "";
		try {
			try {
				o_name = CraftItemStack.asNMSCopy(i).getTag().getCompound("display").getString("Name");
				rename = true;
				// log.info(o_name);
			} catch (NullPointerException npe) {
				rename = false;
			}
		} catch (ClassCastException cce) {
			rename = false;
		}

		List<String> old_lore = new ArrayList<String>();
		ItemMeta im = i.getItemMeta();
		ChatColor tier_color = ProfessionMechanics.getTierColor(tier);

		if(i != null && i.hasItemMeta() && i.getItemMeta().hasLore()){
			for(String s : im.getLore()){
				old_lore.add(s);
			}

			if(rename == true && o_name.length() > 0){
				im.setDisplayName(o_name);
			} 

			old_lore.add(ChatColor.WHITE.toString() + (int)price + tier_color + " Portal Key Shards");
			im.setLore(old_lore);
			i.setItemMeta(im);
		}

		if(i != null && !(i.hasItemMeta())){
			old_lore.add(ChatColor.WHITE.toString() + (int)price + tier_color + " Portal Key Shards");
			im.setLore(old_lore);
			i.setItemMeta(im);
		}

		return i;
	}

	public static double getShardPrice(ItemStack i){
		if(i != null && i.hasItemMeta() && i.getItemMeta().hasLore()){
			List<String> lore = i.getItemMeta().getLore();
			for(String s : lore){
				s = ChatColor.stripColor(s);
				if(s.contains("Portal Key Shards")){
					return Double.parseDouble(ChatColor.stripColor((s.substring(0, s.indexOf(" ")))));
				}
			}
		}
		
		return 0;
		
		
		/*try {
			NBTTagList description = CraftItemStack.asNMSCopy(is).getTag().getCompound("display").getList("Lore", 0);
			int x = 0;
			while (description.size() > x) {
				if (description.get(x).toString().contains("Portal Key Shards")) {
					String content = description.get(x).toString();
					return Double.parseDouble(ChatColor.stripColor((content.substring(0, content.indexOf(" ")))));
				}
				x++;
			}

		} catch (NullPointerException e) {
			return 0;
		}
		return 0;*/
	}

	public static double getShardPriceTier(ItemStack is){
		
		if(is != null && is.hasItemMeta() && is.getItemMeta().hasLore()){
			List<String> lore = is.getItemMeta().getLore();
			for(String s : lore){
				if(s.contains(ChatColor.YELLOW.toString())){
					return 5;
				}
				if(s.contains(ChatColor.LIGHT_PURPLE.toString())){
					return 4;
				}
				if(s.contains(ChatColor.AQUA.toString())){
					return 3;
				}
				if(s.contains(ChatColor.GREEN.toString())){
					return 2;
				}
				if(s.contains(ChatColor.WHITE.toString())){
					return 1;
				}
				
				return 1;
			}
		}
		
		return 0;
		
		/*try {
			NBTTagList description = CraftItemStack.asNMSCopy(is).getTag().getCompound("display").getList("Lore", 0);
			int x = 0;
			while (description.size() > x) {
				if (description.get(x).toString().contains("Portal Key Shards")) {
					String content = description.get(x).toString();

					if(content.contains(ChatColor.YELLOW.toString())){
						return 5;
					}
					if(content.contains(ChatColor.LIGHT_PURPLE.toString())){
						return 4;
					}
					if(content.contains(ChatColor.AQUA.toString())){
						return 3;
					}
					if(content.contains(ChatColor.GREEN.toString())){
						return 2;
					}
					if(content.contains(ChatColor.WHITE.toString())){
						return 1;
					}

					return 1;
				}
				x++;
			}

		} catch (NullPointerException e) {
			return 0;
		}
		return 0;*/
	}

	public static void dismount(Player p_rider){
		if(mount_map.containsKey(p_rider.getName())){
			Entity ent = mount_map.get(p_rider.getName());
			ent.eject();
			ent.remove();
			mount_map.remove(p_rider.getName());
			inv_mount_map.remove(ent);
		}
	}

	public static void convertMounts(Inventory inv){
		int index = -1;
		for(ItemStack is : inv.getContents()){
			index++;
			if(is == null){
				continue;
			}
			if(isMount(is)){
				if(is.getItemMeta().getDisplayName().contains("Pig")){
					inv.setItem(index, ShopMechanics.removePrice(t2_horse_mount));
				}
			}
		}
	}

	public static void removeAllPrices(Inventory inv){
		int index = -1;
		for(ItemStack is : inv.getContents()){
			index++;
			if(is == null){
				continue;
			}
			if(ShopMechanics.getPrice(is) != -1){
				inv.setItem(index, ShopMechanics.removePrice(is));
			}
		}
	}

	public static boolean isMount(ItemStack is){
		if(is != null && is.getType() == Material.SADDLE && is.hasItemMeta() && is.getItemMeta().hasDisplayName() && is.getItemMeta().getDisplayName().toLowerCase().contains("mount")){
			return true;
		}
		return false;
	}

	public static boolean isMountStick(ItemStack is){
		if(is.hasItemMeta() && is.getItemMeta().hasDisplayName() && is.getItemMeta().getDisplayName().toLowerCase().contains("mount") && PetMechanics.isPermUntradeable(is)){
			return true;
		}
		return false;
	}

	public static boolean isMountArmor(ItemStack is){
		if(is != null && (is.getTypeId() == 417 || is.getTypeId() == 418 || is.getTypeId() == 419) && is.hasItemMeta() && is.getItemMeta().hasDisplayName()){
			return true;
		}
		return false;
	}

	public static boolean isMule(ItemStack is){
		if(is != null && is.getType() == Material.LEASH && is.hasItemMeta() && is.getItemMeta().hasDisplayName() && is.getItemMeta().getDisplayName().toLowerCase().contains("mule")){
			return true;
		}
		return false;
	}

	public static boolean isMuleUpgrade(ItemStack is){
		if(is != null && is.getType() == Material.TRAPPED_CHEST && is.hasItemMeta() && is.getItemMeta().hasDisplayName() && is.getItemMeta().getDisplayName().toLowerCase().contains("mule chest")){
			return true;
		}
		return false;
	}

	public static int getHighestMuleTier(String p_name){
		ItemStack mule = null;
		int mule_tier = 0;

		if(Bukkit.getPlayer(p_name) != null){
			Player pl = Bukkit.getPlayer(p_name);

			for(ItemStack is : pl.getInventory()){
				if(is == null){
					continue;
				}
				if(isMule(is)){
					// This is what we want.
					if(mule == null){
						mule = is;
						mule_tier = ItemMechanics.getItemTier(mule);
					}
					else if(ItemMechanics.getItemTier(is) > mule_tier){
						mule = is;
						mule_tier = ItemMechanics.getItemFindPercent(is);
					}
				}
			}

			if(MoneyMechanics.bank_contents.containsKey(pl.getName())){
				for(Inventory inv : MoneyMechanics.bank_contents.get(pl.getName())){
					for(ItemStack is : inv){
						if(is == null){
							continue;
						}
						if(isMule(is)){
							// This is what we want.
							if(mule == null){
								mule = is;
								mule_tier = ItemMechanics.getItemTier(mule);
							}
							else if(ItemMechanics.getItemTier(is) > mule_tier){
								mule = is;
								mule_tier = ItemMechanics.getItemFindPercent(is);
							}
						}
					}
				}
			}
		}

		if(mule != null){
			// TODO: Anything?
		}

		return mule_tier; // 0 = no mule.
	}

	private boolean walkTo(LivingEntity livingEntity, double x, double y, double z, float speed) {
		return ((EntityInsentient) ((CraftLivingEntity)livingEntity).getHandle()).getNavigation().a(x, y, z, 1.95F);
	}

	public void changeStickToSaddle(Player pl){
		for(ItemStack is : pl.getInventory().getContents()){
			if(is == null || is.getType() == Material.AIR){
				continue;
			}
			if(isMountStick(is)){
				is.setType(Material.SADDLE);
				pl.updateInventory();
				break;
			}
		}
	}

	public void changeSaddleToStick(Player pl){
		for(ItemStack is : pl.getInventory().getContents()){
			if(is == null || is.getType() == Material.AIR){
				continue;
			}
			if(isMount(is)){
				is.setType(Material.CARROT_STICK);
				pl.updateInventory();
				break;
			}
		}
	}

	public static int getMuleSlots(int tier){
		if(tier == 2){
			return 9;
		}
		if(tier == 3){
			return 18;
		}
		if(tier == 4){
			return 27;
		}

		return 9; // Should never be called.
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent e){
		if(e.getBlock().getType() == Material.TRAPPED_CHEST){
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onInventoryOpen(InventoryOpenEvent e){
		if(e.getInventory().getName().equalsIgnoreCase("Donkey")){
			e.setCancelled(true);
			// Hijack this, open the custom inventory instead
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerQuit(PlayerQuitEvent e){
		Player pl = e.getPlayer();
		if(mount_map.containsKey(pl.getName())){
			Entity mount = mount_map.get(pl.getName());
			inv_mount_map.remove(mount);
			mount_map.remove(pl.getName());			
			changeStickToSaddle(pl);
			mount.eject();
			mount.remove();
		}
		if(mule_map.containsKey(pl.getName())){
			Entity mule = mule_map.get(pl.getName());
			inv_mule_map.remove(mule);
			mule_map.remove(pl.getName());
			mule.remove();
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerMountEntity(VehicleEnterEvent e){
		if(e.getVehicle() instanceof Horse){
			final Location loc = e.getEntered().getLocation();

			Entity evehicle = (Entity)e.getVehicle();
			if(inv_mule_map.containsKey(evehicle)){
				e.setCancelled(true); 
				if(e.getEntered() instanceof Player){
					final Player pl = (Player)e.getEntered();

					this.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
						public void run() {
							pl.teleport(loc);
						}
					}, 1L);
				}
				// Do not mount mules.
			}
		}
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e){
		Player pl = e.getEntity();
		if(mule_map.containsKey(pl.getName())){
			Entity mount = mule_map.get(pl.getName());
			inv_mule_map.remove(e.getEntity());
			mule_map.remove(pl.getName());
			mount.remove();
		}	
	}
	
	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerInteractEntity(PlayerInteractEntityEvent e){
		final Player p = e.getPlayer();
		final Location loc = p.getLocation();
		if(e.getRightClicked() instanceof Horse){
			Entity ent = e.getRightClicked();
			if(inv_mule_map.containsKey(ent) && inv_mule_map.get(ent).equalsIgnoreCase(e.getPlayer().getName())){
				// Open mule inventory.
				e.setCancelled(true); // Don't mount it.

				this.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
					public void run() {
						p.teleport(loc);

						Inventory mule_inv = null;
						if(mule_inventory.containsKey(p.getName())){
							// We already rendered the inventory.
							mule_inv = mule_inventory.get(p.getName());
						}
						else{
							// We haven't rendered the inventory yet, let's render it.
							// This should NEVER be called, render is called on mount summon.
							mule_inv = Bukkit.createInventory(null, 9, "Mobile Storage Chest");
						}

						p.openInventory(mule_inv);
						p.playSound(p.getLocation(), Sound.CHEST_OPEN, 1F, 1F);
					}
				}, 1L);

				// TODO: Check if the idiot mule is tied to ground like an idiot.
				if(!leashedToGround(ent)){
					setLeash(ent, p);
				}
			}
		}
		if(e.getRightClicked() instanceof Pig){
			if(p.getItemInHand().getType() == Material.SADDLE){
				e.setCancelled(true);
				p.updateInventory();
				return;
			}
		}
		if(e.getRightClicked() instanceof Player){
			Player trader = (Player)e.getRightClicked();
			if(!(trader.hasMetadata("NPC"))){return;} // Only NPC's matter.
			if(!(ChatColor.stripColor(trader.getName()).equalsIgnoreCase("Animal Tamer"))){return;} // Only 'Animal Tamer' should do anything.
			e.setCancelled(true);

			if(mount_being_bought.containsKey(p.getName())){
				p.sendMessage(ChatColor.RED + "You have a pending transaction. Type 'cancel' to void it.");
				return;
			}

			//p.sendMessage(ChatColor.GRAY + "Animal Tamer: " + ChatColor.WHITE + "I have nothing to sell you yet, come back in a bit.");

			p.playSound(p.getLocation(), Sound.WOOD_CLICK, 1.0F, 1.0F);
			in_shop.add(p.getName());
			p.openInventory(TradeWindow);
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onMuleClickInventory(InventoryClickEvent e){
		if(e.getInventory().getName().equalsIgnoreCase("Mobile Storage Chest")){
			Player pl = (Player)e.getWhoClicked();
			if(mule_inventory.containsKey(pl.getName()) && pl.getHealth() > 0){
				mule_inventory.put(pl.getName(), pl.getOpenInventory().getTopInventory());
			}
		}
	}
	
	//@EventHandler(priority = EventPriority.MONITOR)
	public void onInventoryClose(InventoryCloseEvent e){
		if(e.getInventory().getName().equalsIgnoreCase("Mobile Storage Chest")){
			Player pl = (Player)e.getPlayer();
			if(mule_inventory.containsKey(pl.getName()) && pl.getHealth() > 0){
				mule_inventory.put(pl.getName(), e.getInventory());
				pl.playSound(pl.getLocation(), Sound.CHEST_CLOSE, 1F, 1F);
			}
		}
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onAsyncPlayerChatEvent(AsyncPlayerChatEvent e){
		Player pl = e.getPlayer();
		if(mount_being_bought.containsKey(pl.getName())){
			e.setCancelled(true);
			int slot = mount_being_bought.get(pl.getName());
			int price = ShopMechanics.getPrice(TradeWindow.getItem(slot));
			if(e.getMessage().equalsIgnoreCase("y")){
				if(!RealmMechanics.doTheyHaveEnoughMoney(pl, price)){
					pl.sendMessage(ChatColor.RED + "You do not have enough gems to purchase this mount.");
					pl.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "COST: " + ChatColor.RED + price + ChatColor.BOLD + "G");
					mount_being_bought.remove(pl.getName());
					return;
				}

				if(pl.getInventory().firstEmpty() == -1){
					pl.sendMessage(ChatColor.RED + "No space available in inventory. Type 'cancel' or clear some room.");
					return;
				}

				RealmMechanics.subtractMoney(pl, price);
				ItemStack product = TradeWindow.getItem(slot);
				pl.getInventory().setItem(pl.getInventory().firstEmpty(), ShopMechanics.removePrice(CraftItemStack.asCraftCopy(product)));
				pl.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "-" + ChatColor.RED + price + ChatColor.BOLD + "G");
				pl.sendMessage(ChatColor.GREEN + "Transaction successful.");
				if(isMount(product)){
					AchievmentMechanics.addAchievment(pl.getName(), "Saddle Up!");
					pl.sendMessage(ChatColor.GRAY + "You are now the proud owner of a mount -- " + ChatColor.UNDERLINE + "to summon your new mount, simply right click with the saddle in your player's hand.");
				}
				mount_being_bought.remove(pl.getName());
			}
			else{
				pl.sendMessage(ChatColor.RED + "Purchase - " + ChatColor.BOLD + "CANCELLED");
				mount_being_bought.remove(pl.getName());
			}
		}
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent e){
		Player pl = (Player)e.getWhoClicked();
		if(isMuleUpgrade(e.getCursor()) && isMule(e.getCurrentItem())){
			int mule_tier = ItemMechanics.getItemTier(e.getCurrentItem());
			int mule_armor_tier = ItemMechanics.getItemTier(e.getCursor());

			if((mule_armor_tier - 1) != mule_tier){
				// Wrong armor type.
				return;
			}

			e.setCancelled(true);
			e.setCursor(new ItemStack(Material.AIR));

			if(mule_tier == 2){
				e.setCurrentItem(CraftItemStack.asCraftCopy(t2_mule));
			}
			else if(mule_tier == 3){
				e.setCurrentItem(CraftItemStack.asCraftCopy(t3_mule));
			}

			pl.playSound(pl.getLocation(), Sound.LEVEL_UP, 1F, 1F);
			pl.updateInventory();

			if(pl.getOpenInventory().getTopInventory().getTitle().equalsIgnoreCase("Mobile Storage Chest")){
				pl.closeInventory(); // Close inventory incase they're viewing the mule.
			}

			// Now we need to upgrade inventory slots as well if it's already loaded.
			if(mule_inventory.containsKey(pl.getName())){
				Inventory old_mule_inventory = mule_inventory.get(pl.getName());
				Inventory new_mule_inventory = Bukkit.createInventory(null, getMuleSlots(ItemMechanics.getItemTier(e.getCurrentItem())), "Mobile Storage Chest");

				int index = -1;
				for(ItemStack is : old_mule_inventory.getContents()){
					index++;
					if(is == null){
						continue;
					}

					new_mule_inventory.setItem(index, is);
				}

				mule_inventory.put(pl.getName(), new_mule_inventory);
			}
		}
		if(isMountArmor(e.getCursor()) && isMount(e.getCurrentItem())){
			// Make sure they're the correct tier.
			int mount_tier = ItemMechanics.getItemTier(e.getCurrentItem());
			int mount_armor_tier = ItemMechanics.getItemTier(e.getCursor());

			if((mount_armor_tier - 1) != mount_tier){
				// Wrong armor type.
				pl.sendMessage(ChatColor.RED + "You " + ChatColor.UNDERLINE + "cannot" + ChatColor.RED + " apply this tier of armor to this tier horse.");
				return;
			}

			e.setCancelled(true);
			e.setCursor(new ItemStack(Material.AIR));

			if(mount_tier == 2){
				e.setCurrentItem(CraftItemStack.asCraftCopy(t3_horse_mount));
			}
			else if(mount_tier == 3){
				e.setCurrentItem(CraftItemStack.asCraftCopy(t4_horse_mount));
			}
			else if(mount_tier == 4){
				e.setCurrentItem(CraftItemStack.asCraftCopy(t5_horse_mount));
			}

			pl.playSound(pl.getLocation(), Sound.LEVEL_UP, 1F, 1F);
			pl.updateInventory();
		}
	}

	@EventHandler
	public void onInventoryClickEvent(InventoryClickEvent e){
		final Player pl = (Player)e.getWhoClicked();
		if(!(e.getInventory().getName().contains("Animal Tamer")) || !(in_shop.contains(pl.getName()))){
			return;
		}

		e.setCancelled(true);

		if((e.getRawSlot() <= 3 && e.getRawSlot() >= 0) || e.getRawSlot() == 9){
			if(e.getRawSlot() == 9){
				// Mule, make sure they don't already have one.
				if(getHighestMuleTier(pl.getName()) != 0){
					pl.sendMessage(ChatColor.RED.toString() + "You already own a mule, you're only allowed to own " + ChatColor.UNDERLINE + "one.");
					return;
				}
			}

			this.getServer().getScheduler().scheduleAsyncDelayedTask(this, new Runnable() {
				public void run() {
					pl.closeInventory();
					in_shop.remove(pl.getName());
				}
			}, 2L);

			int price = ShopMechanics.getPrice(e.getCurrentItem());
			String i_name = e.getCurrentItem().getItemMeta().getDisplayName();

			if(!RealmMechanics.doTheyHaveEnoughMoney(pl, price)){
				pl.sendMessage(ChatColor.RED + "You do not have enough gems to purchase this mount.");
				pl.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "COST: " + ChatColor.RED + price + ChatColor.BOLD + "G");
				return;
			}
			else{
				mount_being_bought.put(pl.getName(), e.getRawSlot());
				pl.sendMessage(ChatColor.GRAY + "The '" + i_name + ChatColor.GRAY + "' costs " + ChatColor.GREEN + ChatColor.BOLD + price + " GEM(s)" + ChatColor.GRAY + ".");
				pl.sendMessage(ChatColor.GRAY + "This item is non-refundable. Type " + ChatColor.GREEN + ChatColor.BOLD + "Y" + ChatColor.GRAY + " to confirm.");
			}

			return;
			// BUYING MOUNT.
		}
	}

	// DEPRECIATED, does not work w/ horses, using AsyncTask.
	/*@EventHandler
	public void onVehicleMove(VehicleMoveEvent e){
		if(e.getVehicle() instanceof Horse){
			Horse h = (Horse)e.getVehicle();
			if(h.getPassenger() != null && h.getPassenger() instanceof Player){
				Player rider = (Player)h.getPassenger();
				String rider_align = KarmaMechanics.getRawAlignment(rider.getName());
				Location to_loc = e.getTo();

				if((rider_align.equalsIgnoreCase("evil") && DuelMechanics.isPvPDisabled(to_loc)) || DuelMechanics.isRestrictedArea(to_loc)){
					if(mount_map.containsKey(rider.getName())){
						Entity ent = mount_map.get(rider.getName());
						ent.eject();
						ent.remove();
						mount_map.remove(rider.getName());
						inv_mount_map.remove(ent);
					}
					rider.updateInventory();

					if(DuelMechanics.isPvPDisabled(to_loc)){
						rider.sendMessage(ChatColor.RED + "You " + ChatColor.UNDERLINE + "cannot" + ChatColor.RED + " enter " + ChatColor.BOLD.toString() + "NON-PVP" + ChatColor.RED + " zones with a chaotic alignment.");
					}
					else if(DuelMechanics.isRestrictedArea(to_loc)){
						rider.sendMessage(ChatColor.GRAY + ChatColor.ITALIC.toString() + "A magical force prevents your mount from going in that direction.");
					}

					return; // Don't go to a non-chaotic zone OR restricted zones (as a chaotic player), Mr. Horse.
				}
			}
		}
	}*/

	/*@EventHandler
	public void onPlayerTeleport(PlayerTeleportEvent e){
		final Player new_p = e.getPlayer();

		/instance.getServer().getScheduler().scheduleSyncDelayedTask(instance, new Runnable() {
			public void run() {
				if(new_p == null || !(new_p.isOnline())){return;}
				for(String s : mount_map.keySet()){
					if(Bukkit.getPlayer(s) != null){
						Player pl = Bukkit.getPlayer(s);
						//new_p.hidePlayer(pl);
					}
				}
			}
		}, 40L);
	}*/

	@EventHandler
	public void onInventoryOpenEvent(InventoryOpenEvent e){
		convertMounts(e.getInventory());
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e){
		changeStickToSaddle(e.getPlayer());
		convertMounts(e.getPlayer().getInventory());
		removeAllPrices(e.getPlayer().getInventory());
		final Player new_p = e.getPlayer();
		instance.getServer().getScheduler().scheduleSyncDelayedTask(instance, new Runnable() {
			public void run() {
				if(new_p == null || !(new_p.isOnline())){return;}
				for(String s : mount_map.keySet()){
					if(Bukkit.getPlayer(s) != null){
						Player pl = Bukkit.getPlayer(s);
						new_p.showPlayer(pl);
					}
				}
			}
		}, 40L);
	}

	@EventHandler
	public void onPlayerTeleport(PlayerTeleportEvent e){
		final Player p = e.getPlayer();
		instance.getServer().getScheduler().scheduleSyncDelayedTask(instance, new Runnable() {
			public void run() {
				if(p == null || !(p.isOnline())){return;}
				for(String s : mount_map.keySet()){
					if(Bukkit.getPlayer(s) != null){
						Player pl = Bukkit.getPlayer(s);
						p.showPlayer(pl);
					}
				}
			}
		}, 40L);
	}

	/*@EventHandler
	public void onPlayerToggleSneak(PlayerToggleSneakEvent e){
			Player pl = e.getPlayer();
			if(pl.getVehicle() != null){
				Entity mount = pl.getVehicle();
				if(inv_mount_map.containsKey(mount)){
					if(pl.getLocation().subtract(0, 1, 0).getBlock().getType() != Material.AIR)
					if(pl.getLocation().subtract(0, 1, 0).getBlock().getType() != Material.AIR && !(jump_delay.containsKey(pl.getName())) || (System.currentTimeMillis() - jump_delay.get(pl.getName())) >= (1 * 1000)){
						mount.setVelocity(new Vector(mount.getVelocity().getX() * 6, 0.60F, mount.getVelocity().getZ() * 6));
					}
					jump_delay.put(pl.getName(), System.currentTimeMillis());
					e.setCancelled(true);
				}
			}
	}*/

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e){
		if(e.hasItem() && e.getItem().getType() == Material.CARROT_STICK){
			e.getItem().setDurability((short)0);
		}
		if(e.hasItem()){
			Player pl = e.getPlayer();
			ItemStack i_mount = e.getItem();

			if(mule_map.containsKey(pl.getName())){
				if(!leashedToGround(mule_map.get(pl.getName())) && (e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.LEFT_CLICK_BLOCK)){
					Location target_loc = pl.getTargetBlock(ProfessionMechanics.transparent, 6).getLocation();
					setLeash(mule_map.get(pl.getName()), target_loc);
					tied_to_ground.add(mule_map.get(pl.getName()));
				}
				else if(leashedToGround(mule_map.get(pl.getName())) && isMule(e.getItem())){
					// Unleash from ground, teleport to player.
					mule_map.get(pl.getName()).teleport(pl.getLocation().add(0, 1, 0));
					setLeash(mule_map.get(pl.getName()), pl);
					tied_to_ground.remove(mule_map.get(pl.getName()));
				}
			}

			if((isMule(e.getItem()) && mule_map.containsKey(pl.getName())) || (isMount(e.getItem()) && mount_map.containsKey(pl.getName()))){
				return; // Do not resummon a summoned mount.
			}

			if((isMount(i_mount) && pl.getVehicle() == null) || isMule(i_mount) && (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK)){
				e.setCancelled(true); // Cancel actual interraction event for saddles.

				if(DuelMechanics.duel_map.containsKey(pl.getName())){
					pl.sendMessage(ChatColor.RED + "You " + ChatColor.UNDERLINE + "cannot" + ChatColor.RED + " use mounts in duels.");
					return;
				}

				if(!pl.getWorld().getName().equalsIgnoreCase(Bukkit.getWorlds().get(0).getName())){
					pl.sendMessage(ChatColor.RED + "You " + ChatColor.UNDERLINE + "cannot" + ChatColor.RED + " summon your mount outside of Andalucia.");
					return;
				}

				double seconds_left = 5; // Default 5 seconds cast time.
				if(HealthMechanics.in_combat.containsKey(pl.getName())){
					long dif = ((HealthMechanics.HealthRegenCombatDelay * 1000) + HealthMechanics.in_combat.get(pl.getName())) - System.currentTimeMillis();
					seconds_left = (dif / 1000.0D) + 0.5D;
					seconds_left = Math.round(seconds_left);
				}

				if(seconds_left < 5){
					seconds_left = 5;
				}

				/*if(isMule(e.getItem())){
					seconds_left += 5;
				}*/

				pl.sendMessage(ChatColor.WHITE + "" + ChatColor.BOLD + "SUMMONING " + ChatColor.UNDERLINE + i_mount.getItemMeta().getDisplayName() + ChatColor.WHITE + " ... " + (int)seconds_left + ChatColor.BOLD + "s");

				if(isMule(i_mount)){
					// Generate mule inventory
					if(!(mule_inventory.containsKey(pl.getName()))){ // No inventory data, generate some.
						if(mule_itemlist_string.containsKey(pl.getName())){
							//Inventory mule_inventory = Bukkit.createInventory(null, getMuleSlots(ItemMechanics.getItemTier(i_mount)), "Mobile Storage Chest");
							String inventory_string = mule_itemlist_string.get(pl.getName());
							mule_inventory.put(pl.getName(), Hive.convertStringToInventory(null, inventory_string, "Mobile Storage Chest", getMuleSlots(ItemMechanics.getItemTier(i_mount))));
							mule_itemlist_string.remove(pl.getName());
						}
						else{
							// They have no inventory. Generate one.
							mule_inventory.put(pl.getName(), Bukkit.createInventory(null, getMuleSlots(ItemMechanics.getItemTier(i_mount)), "Mobile Storage Chest"));
							mule_itemlist_string.remove(pl.getName());
						}
					}
				}

				summon_mount.put(pl.getName(), (int)seconds_left);
				summon_item.put(pl.getName(), i_mount);
				summon_location.put(pl.getName(), pl.getLocation());
				return;
			}

			if(isMount(i_mount) && pl.getVehicle() != null && pl.getVehicle() instanceof Horse){
				// DISMOUNT!
				e.setCancelled(true);
				if(mount_map.containsKey(pl.getName())){
					Entity ent = mount_map.get(pl.getName());
					ent.eject();
					ent.remove();
					mount_map.remove(pl.getName());
					inv_mount_map.remove(ent);
				}
			}
		}
	}

	@EventHandler
	public void onEntityDismount(EntityDismountEvent e){
		if(e.getDismounted() instanceof Player){
			Player pl = (Player)e.getDismounted();
			Entity horse  = e.getEntity();
			// PROBLEM: If they mount a mule, then dismount, never removed from mount_map.
			if(horse instanceof Horse && mount_map.containsKey(pl.getName())){
				Entity ent = mount_map.get(pl.getName());
				ent.eject();
				ent.remove();
				mount_map.remove(pl.getName());
				inv_mount_map.remove(ent);
			}
		}
	}

	@EventHandler
	public void onPlayerMoveEvent(PlayerMoveEvent e){
		Player pl = e.getPlayer();
		if(summon_location.containsKey(pl.getName())){
			Location loc = summon_location.get(pl.getName());
			if(!pl.getWorld().getName().equalsIgnoreCase(loc.getWorld().getName()) || e.getTo().distanceSquared(loc) >= 2){
				summon_location.remove(pl.getName());
				summon_mount.remove(pl.getName());
				summon_item.remove(pl.getName());
				pl.sendMessage(ChatColor.RED + "Mount Summon - " + ChatColor.BOLD + "CANCELLED");
			}
		}
	}

	@EventHandler
	public void onPlayerAnimation(PlayerAnimationEvent e){
		Player pl = e.getPlayer();
		if(summon_mount.containsKey(pl.getName())){
			summon_location.remove(pl.getName());
			summon_mount.remove(pl.getName());
			summon_item.remove(pl.getName());
			pl.sendMessage(ChatColor.RED + "Mount Summon - " + ChatColor.BOLD + "CANCELLED");
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onEntityDeath(EntityDeathEvent e){
		if(e.getEntity() instanceof Horse || inv_mule_map.containsKey(e.getEntity())){
			e.getDrops().clear();
			if(e.getEntity().getPassenger() != null && e.getEntity().getPassenger() instanceof Player){
				Player p_rider = (Player)e.getEntity().getPassenger();
				e.getEntity().eject();
				inv_mount_map.remove(e.getEntity());
				mount_map.remove(p_rider.getName());
				for(Player player : Bukkit.getOnlinePlayers()){
					player.showPlayer(p_rider);
				}
			}
		}	
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onEntityDamage(EntityDamageEvent e){
		if(e.getEntity() instanceof Player && e.getDamage() > 0 && !(e.isCancelled())){
			Player pl = (Player)e.getEntity();
			if(summon_mount.containsKey(pl.getName())){
				summon_mount.remove(pl.getName());
				summon_location.remove(pl.getName());
				summon_item.remove(pl.getName());
				pl.sendMessage(ChatColor.RED + "Mount Summon - " + ChatColor.BOLD + "CANCELLED");
				return;
			}
			if(mount_map.containsKey(pl.getName())){
				HealthMechanics.in_combat.put(pl.getName(), System.currentTimeMillis());
				Entity mount = mount_map.get(pl.getName());
				inv_mount_map.remove(mount);
				mount_map.remove(pl.getName());
				mount.eject();
				mount.remove();
				if(mount instanceof Horse){
					e.setCancelled(true);
					e.setDamage(0.0D);
				}
				return;
			}
		}

		if(inv_mule_map.containsKey(e.getEntity())){
			e.setCancelled(true);
			e.setDamage(0.0D);

			if(e instanceof EntityDamageByEntityEvent){
				Entity damager = ((EntityDamageByEntityEvent)e).getDamager();
				if(damager instanceof Player){
					if(inv_mule_map.get(e.getEntity()).equalsIgnoreCase(((Player)damager).getName())){
						// It's the owner of the mule!
						try {
							ParticleEffect.sendToLocation(ParticleEffect.CRIT, e.getEntity().getLocation().add(0, 1, 0), new Random().nextFloat(), new Random().nextFloat(), new Random().nextFloat(), 2.0F, 40);
						} catch (Exception e1) {e1.printStackTrace();}
						//e.getEntity().getWorld().spawnParticle(e.getEntity().getLocation().add(0, 1, 0), Particle.CRIT, 2.0F, 40);
						
						e.getEntity().remove();
						((Player)damager).playSound(damager.getLocation(), Sound.WOOD_CLICK, 1F, 0.5F);
						inv_mule_map.remove(e.getEntity());
						mule_map.remove(((Player)damager).getName());
					}
				}
			}
		}

		if(inv_mount_map.containsKey(e.getEntity())){
			if(DuelMechanics.isDamageDisabled(e.getEntity().getLocation())){
				e.setCancelled(true);
				e.setDamage(0);
				return;
			}

			if(e.getEntity() instanceof Horse){
				Horse h = (Horse)e.getEntity();

				if(h.getPassenger() != null && h.getPassenger() instanceof Player){
					Player p_rider = (Player)h.getPassenger();
					if(!(e.isCancelled()) && e.getDamage() > 0 && e.getCause() != DamageCause.FALL){
						HealthMechanics.in_combat.put(p_rider.getName(), System.currentTimeMillis());
					}

					e.setCancelled(true);
					e.setDamage(0.0D);
				}
			}

			/*if(e.getCause() != DamageCause.ENTITY_ATTACK){
				e.setCancelled(true);
				e.setDamage(0);
				return;
			}*/
		}
	}

	@EventHandler
	public void onVehicleDamageEvent(VehicleDamageEvent e){
		if(e.getVehicle() instanceof Horse){
			Horse h = (Horse)e.getVehicle();
			if(inv_mount_map.containsKey(e.getVehicle())){
				if(DuelMechanics.isDamageDisabled(h.getLocation())){
					e.setCancelled(true);
					e.setDamage(0.0D);
					return;
				}
				else{
					if(h.getPassenger() != null && h.getPassenger() instanceof Player){
						Player p_rider = (Player)h.getPassenger();
						HealthMechanics.in_combat.put(p_rider.getName(), System.currentTimeMillis());
						e.setCancelled(true);
						e.setDamage(0.0D);
					}
				}
			}
		}
	}

	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if (cmd.getName().equalsIgnoreCase("mount")) {
			Player p = (Player) sender;

			if(!(p.isOp())){
				return true;
			}

			p.getInventory().addItem(ShopMechanics.removePrice(t1_mule));
			p.getInventory().addItem(ShopMechanics.removePrice(t2_mule));
			p.getInventory().addItem(ShopMechanics.removePrice(t2_mule_upgrade));
			p.getInventory().addItem(ShopMechanics.removePrice(t3_mule_upgrade));
			
			if(p.getName().equalsIgnoreCase("Vaquxine")){
				for(Entity ent : p.getNearbyEntities(4, 4, 4)){
					if(ent instanceof Horse){
						ent.remove();
					}
				}
			}
		}

		return true;
	}

}
