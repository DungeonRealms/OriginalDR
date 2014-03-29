package me.vaqxine.ItemMechanics;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

import javax.swing.text.Utilities;

import me.vaqxine.Main;
import me.vaqxine.AchievmentMechanics.AchievmentMechanics;
import me.vaqxine.CommunityMechanics.CommunityMechanics;
import me.vaqxine.DuelMechanics.DuelMechanics;
import me.vaqxine.EnchantMechanics.EnchantMechanics;
import me.vaqxine.FatigueMechanics.FatigueMechanics;
import me.vaqxine.HealthMechanics.HealthMechanics;
import me.vaqxine.Hive.Hive;
import me.vaqxine.Hive.ParticleEffect;
import me.vaqxine.ItemMechanics.commands.CommandAddWeapon;
import me.vaqxine.ModerationMechanics.ModerationMechanics;
import me.vaqxine.MonsterMechanics.MonsterMechanics;
import me.vaqxine.MountMechanics.MountMechanics;
import me.vaqxine.PartyMechanics.PartyMechanics;
import me.vaqxine.PetMechanics.PetMechanics;
import me.vaqxine.ProfessionMechanics.ProfessionMechanics;
import me.vaqxine.RepairMechanics.RepairMechanics;
import net.minecraft.server.v1_7_R1.EntityLiving;
import net.minecraft.server.v1_7_R1.NBTTagCompound;
import net.minecraft.server.v1_7_R1.NBTTagList;
import net.minecraft.server.v1_7_R1.Packet;
import net.minecraft.server.v1_7_R1.PacketPlayOutWorldEvent;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Effect;
import org.bukkit.EntityEffect;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_7_R1.CraftServer;
import org.bukkit.craftbukkit.v1_7_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_7_R1.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_7_R1.inventory.CraftItemStack;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Horse;
import org.bukkit.entity.LargeFireball;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.SmallFireball;
import org.bukkit.entity.Snowball;
import org.bukkit.entity.WitherSkull;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class ItemMechanics implements Listener {

	private static ItemMechanics instance;
	private final static ItemGenerators iGen = new ItemGenerators(instance);

	static Logger log = Logger.getLogger("Minecraft");

	public static HashMap<String, ItemStack> custom_item_table = new HashMap<String, ItemStack>();
	// Item Name, Generated Item.

	static HashMap<Arrow, ItemStack> arrow_shooter = new HashMap<Arrow, ItemStack>();
	// Stores the Bow that shot an arrow, used to calculate damage done by arrows on hit events.

	public static HashMap<String, List<Integer>> armor_data = new HashMap<String, List<Integer>>();
	// Player_name, min%-max% ARMOR

	public static HashMap<String, List<Integer>> dmg_data = new HashMap<String, List<Integer>>();
	// Player_name, min%-max% DMG

	// All stats of players stored in memory {
	public static HashMap<String, Integer> str_data = new HashMap<String, Integer>();
	public static HashMap<String, Integer> dex_data = new HashMap<String, Integer>();
	public static HashMap<String, Integer> vit_data = new HashMap<String, Integer>();
	public static HashMap<String, Integer> int_data = new HashMap<String, Integer>();

	public static HashMap<String, Integer> fire_res_data = new HashMap<String, Integer>();
	public static HashMap<String, Integer> ice_res_data = new HashMap<String, Integer>();
	public static HashMap<String, Integer> poison_res_data = new HashMap<String, Integer>();

	public static HashMap<String, Integer> dodge_data = new HashMap<String, Integer>();
	public static HashMap<String, Integer> block_data = new HashMap<String, Integer>();
	public static HashMap<String, Integer> thorn_data = new HashMap<String, Integer>();
	public static HashMap<String, Integer> reflection_data = new HashMap<String, Integer>();

	public static HashMap<String, Integer> ifind_data = new HashMap<String, Integer>();
	public static HashMap<String, Integer> gfind_data = new HashMap<String, Integer>();
	// }

	public static HashMap<String, Long> last_orb_use = new HashMap<String, Long>();
	// Prevents spamming orbs like crazy. (possibly Depreciated)

	public static HashMap<String, Integer> player_tier = new HashMap<String, Integer>();

	static HashMap<String, List<ItemStack>> armor_contents = new HashMap<String, List<ItemStack>>();
	// Player Name, List of all items they have equipped. Used to generate stats in memory and determine when a piece of armor breaks, as the armor_contents size will be > that of the getArmorContents()

	public static CopyOnWriteArrayList<String> need_update = new CopyOnWriteArrayList<String>();
	// A list of player names that need their stats (loaded in memory) updated.
	// This is used instead of just on the inventoryClick() event because a delay is required between the end of the event and the indexes of the player being updated in our API.

	public static List<Player> no_negation = new ArrayList<Player>();
	// Don't allow a player to block, dodge, etc. More than once every 2ticks.

	public static List<String> processing_dmg_event = new ArrayList<String>();
	// Prevents infinite loops when using AOE damage.

	public static HashMap<Projectile, ItemStack> projectile_map = new HashMap<Projectile, ItemStack>();
	// Projectile, Weapon fired.

	public static List<String> processing_proj_event = new ArrayList<String>();
	// Prevents weapon swapping for projectile hit events.

	public static List<Entity> processing_ent_dmg_event = new ArrayList<Entity>();
	// Prevents infinite loops when using AOE damage. (from monsters)

	static HashMap<String, ItemStack> spoofed_weapon = new HashMap<String, ItemStack>();
	// Player_name, Weapon to use for damage event.

	public static ItemStack orb_of_peace = ItemMechanics.signNewCustomItem(Material.ENDER_PEARL, (short)1, ChatColor.GREEN.toString() + 
			"" + "Orb of Peace", ChatColor.GRAY.toString() + "Set realm to " + ChatColor.UNDERLINE + "SAFE ZONE" + ChatColor.GRAY + " for 1 hour(s).");

	@SuppressWarnings("deprecation")
	public static ItemStack orb_of_flight = ItemMechanics.signNewCustomItem(Material.getMaterial(402), (short)1, ChatColor.AQUA.toString() + 
			"" + "Orb of Flight", ChatColor.GRAY.toString() + "Enables " + ChatColor.UNDERLINE + "FLYING" + ChatColor.GRAY + " in realm for the owner " + "," + ChatColor.GRAY.toString() + "and all builders for 30 minute(s)."
					+ "," + ChatColor.RED.toString() + ChatColor.BOLD.toString() + "REQ:" + ChatColor.RED.toString() + " Active Orb of Peace");

	public static ItemStack easter_egg = ItemMechanics.signNewCustomItem(Material.EGG, (short)1, ChatColor.LIGHT_PURPLE.toString() + 
			"" + "Easter Egg", ChatColor.GRAY.toString() + "Instantly Heals " + ChatColor.UNDERLINE + "ALL" + ChatColor.GRAY.toString() + " health" + "," + ChatColor.LIGHT_PURPLE + "Rare");

	public static ItemStack t1_arrow = ItemMechanics.signNewCustomItem(Material.ARROW, (short)1, ChatColor.WHITE.toString() + 
			"" + "Wood Arrow", ChatColor.GRAY.toString() + "An arrow for " + ChatColor.ITALIC.toString() + "shortbows.");
	public static ItemStack t2_arrow = ItemMechanics.signNewCustomItem(Material.ARROW, (short)1, ChatColor.GREEN.toString() + 
			"" + "Stone Arrow", ChatColor.GRAY.toString() + "An arrow for " + ChatColor.ITALIC.toString() + "longbows.");
	public static ItemStack t3_arrow = ItemMechanics.signNewCustomItem(Material.ARROW, (short)1, ChatColor.AQUA.toString() + 
			"" + "Iron Arrow", ChatColor.GRAY.toString() + "An arrow for " + ChatColor.ITALIC.toString() + "magic bows.");
	public static ItemStack t4_arrow = ItemMechanics.signNewCustomItem(Material.ARROW, (short)1, ChatColor.LIGHT_PURPLE.toString() + 
			"" + "Ancient Arrow", ChatColor.GRAY.toString() + "An arrow for " + ChatColor.ITALIC.toString() + "ancient bows.");
	public static ItemStack t5_arrow = ItemMechanics.signNewCustomItem(Material.ARROW, (short)1, ChatColor.YELLOW.toString() + 
			"" + "Legendary Arrow", ChatColor.GRAY.toString() + "An arrow for " + ChatColor.ITALIC.toString() + "legendary bows.");

	public static CopyOnWriteArrayList<String> to_process_weapon = new CopyOnWriteArrayList<String>();

	static ChatColor red = ChatColor.RED;
	static ChatColor white = ChatColor.WHITE;
	ChatColor gray = ChatColor.GRAY;

	@SuppressWarnings("deprecation")
	public void onEnable() {
		Main.plugin.getServer().getPluginManager().registerEvents(this, Main.plugin);
		Main.plugin.getServer().getPluginManager().registerEvents(new Halloween(this), Main.plugin);
		instance = this;

		Main.plugin.getCommand("addweapon").setExecutor(new CommandAddWeapon());
		
		File home_dir = new File("plugins/ItemMechanics");

		if(!(home_dir.exists())){
			home_dir.mkdirs();
			new File("plugins/ItemMechanics/custom_items").mkdir();
		}

		loadCustomItemTemplates();
		// Loads all custom item templates in /custom_items into memory for use in other plugins.

		Main.plugin.getServer().getScheduler().scheduleAsyncRepeatingTask(Main.plugin, new Runnable() {
			public void run() {
				for(Player pl : Main.plugin.getServer().getOnlinePlayers()){
					if(pl.getInventory().getHelmet() != null && pl.getInventory().getHelmet().getType() == Material.JACK_O_LANTERN){
						try {
							ParticleEffect.sendToLocation(ParticleEffect.FLAME, pl.getLocation().add(0, 2.05, 0), new Random().nextFloat(), new Random().nextFloat(), new Random().nextFloat(), 0.01F, 8);
						} catch (Exception e) {
							e.printStackTrace();
						}
						//pl.getWorld().spawnParticle(pl.getLocation().add(0, 2.05, 0), Particle.FLAME, 0.01F, 8);
					}
				}
			}
		}, 5 * 20L, 20L);


		Main.plugin.getServer().getScheduler().scheduleAsyncRepeatingTask(Main.plugin, new Runnable() {
			public void run() {
				no_negation.clear();

				for(Player p : Main.plugin.getServer().getOnlinePlayers()){
					if(need_update.contains(p.getName())){continue;} // Equip event.
					if(p.getHealth() <= 0 || HealthMechanics.getPlayerHP(p.getName()) <= 0){
						continue;
					}
					if(!(armor_contents.containsKey(p.getName()))){
						updateArmorContentRecord(p);
						continue;
					}

					if(armor_contents.containsKey(p.getName()) && armor_contents.get(p.getName()).size() != getArmorContent(p).size()){
						updatePlayerStats(p.getName());
					}
				}
			}
		}, 40L, 2L);

		Main.plugin.getServer().getScheduler().scheduleAsyncRepeatingTask(Main.plugin, new Runnable() {
			public void run() {
				for(String s : to_process_weapon){
					if(Bukkit.getPlayer(s) == null || !(Hive.player_inventory.containsKey(s))){
						to_process_weapon.remove(s);
						continue;
					}
					Player pl = Bukkit.getPlayer(s);

					if(HealthMechanics.getPlayerHP(pl.getName()) <= 0 || pl.getHealth() <= 0){
						// Don't calculate current HP if level hasn't been set. (/shard fix)
						to_process_weapon.remove(s);
						continue;
					}

					str_data.put(pl.getName(), generateTotalStrVal(pl));
					dex_data.put(pl.getName(), generateTotalDexVal(pl));
					vit_data.put(pl.getName(), generateTotalVitVal(pl));
					int_data.put(pl.getName(), generateTotalIntVal(pl));

					double new_max_hp = HealthMechanics.generateMaxHP(pl);
					if(HealthMechanics.health_data.containsKey(pl.getName()) && new_max_hp != HealthMechanics.health_data.get(pl.getName())){
						HealthMechanics.health_data.put(pl.getName(), (int)new_max_hp);

						if(HealthMechanics.getPlayerHP(pl.getName()) > (int)new_max_hp){
							//pl.setLevel((int)new_max_hp);
							HealthMechanics.setPlayerHP(pl.getName(), (int)new_max_hp);
						}

						double d_level = HealthMechanics.getPlayerHP(pl.getName());

						double health_percent = d_level / new_max_hp;
						double new_health_display = (health_percent * 20.0D);
						//log.info(String.valueOf(health_percent));
						//log.info(String.valueOf(new_health_display));

						int conv_newhp_display = (int) Math.abs(new_health_display);
						if(conv_newhp_display < 1){
							conv_newhp_display = 1;
						}
						if(conv_newhp_display > 20){
							conv_newhp_display = 20;
						}

						pl.setHealth(conv_newhp_display);
					}
					/*int str_val = getStrVal(is);
					int dex_val = getDexVal(is);
					int vit_val = getVitVal(is);

					log.info(pl.getName() + " - " + is.getItemMeta().getDisplayName());

					if(str_val > 0){
						str_data.put(pl.getName(), generateTotalStrVal(pl));
					}
					if(dex_val > 0){
						dex_data.put(pl.getName(), generateTotalDexVal(pl));
					}
					if(vit_val > 0){
						vit_data.put(pl.getName(), generateTotalVitVal(pl));
					}*/

					to_process_weapon.remove(s);
				}
			}
		}, 10 * 20L, 4L);

		Main.plugin.getServer().getScheduler().scheduleAsyncRepeatingTask(Main.plugin, new Runnable() {
			public void run() {
				//if(need_update.isEmpty()){return;}
				List<String> to_remove = new ArrayList<String>();
				for(String p_name : need_update){
					if(Bukkit.getPlayer(p_name) == null){
						continue;
					}
					Player p = Bukkit.getPlayer(p_name);
					AchievmentMechanics.processArmorAchievments(p_name, p.getInventory().getArmorContents());

					//List<Integer> net_armor_vals = new ArrayList<Integer>(armor_data.get(p.getName()));
					if(!(armor_data.containsKey(p.getName()))){
						need_update.remove(p);
						continue;
					}

					if(p.getHealth() <= 0 || HealthMechanics.getPlayerHP(p.getName()) <= 0){
						continue;
					}

					if(DuelMechanics.duel_max_armor_tier.containsKey(p.getName()) && DuelMechanics.duel_map.containsKey(p.getName())){
						// Let's check if they're allowed to equip it.
						int max_armor = DuelMechanics.duel_max_armor_tier.get(p.getName());
						int armor_tier = -1;

						for(ItemStack is : p.getInventory().getArmorContents()){
							armor_tier = getItemTier(is);
							if(armor_tier > max_armor){
								// Do not allow.
								continue;
							}
						}
					}

					//int old_min_armor = armor_data.get(p.getName()).get(0);
					int old_max_armor = armor_data.get(p.getName()).get(1);

					//int old_min_dmg = dmg_data.get(p.getName()).get(0);
					int old_max_dmg = dmg_data.get(p.getName()).get(1);

					int old_thorns = thorn_data.get(p.getName());
					int old_block = block_data.get(p.getName());
					int old_dodge = dodge_data.get(p.getName());
					int old_reflection = reflection_data.get(p.getName());
					int old_ifind = ifind_data.get(p.getName());
					int old_gfind = gfind_data.get(p.getName());

					int old_str = str_data.get(p.getName());
					int old_dex = dex_data.get(p.getName());
					int old_vit = vit_data.get(p.getName());
					int old_int = int_data.get(p.getName());

					int old_fire_res = fire_res_data.get(p.getName());
					int old_ice_res = ice_res_data.get(p.getName());
					int old_poison_res = poison_res_data.get(p.getName());


					int new_thorns = generateTotalThornVal(p);
					int new_block = generateTotalBlockChance(p);
					int new_dodge = generateTotalDodgeChance(p);
					int new_reflection = generateTotalReflectChance(p);
					int new_ifind = generateTotalItemFindChance(p);
					int new_gfind = generateTotalGoldFindChance(p);
					int new_str = generateTotalStrVal(p);
					int new_dex = generateTotalDexVal(p);
					int new_vit = generateTotalVitVal(p);
					int new_int = generateTotalIntVal(p);
					int new_fire_res = generateTotalFireRes(p);
					int new_ice_res = generateTotalIceRes(p);
					int new_poison_res = generateTotalPoisonRes(p);


					DecimalFormat df = new DecimalFormat("#.##");

					armor_data.put(p.getName(), generateTotalArmorVal(p));
					dmg_data.put(p.getName(), generateTotalDmgVal(p));

					str_data.put(p.getName(), new_str);
					dex_data.put(p.getName(), new_dex);
					vit_data.put(p.getName(), new_vit);
					int_data.put(p.getName(), new_int);

					dodge_data.put(p.getName(), new_dodge);
					block_data.put(p.getName(), new_block);
					thorn_data.put(p.getName(), new_thorns);
					reflection_data.put(p.getName(), new_reflection);

					fire_res_data.put(p.getName(), new_fire_res);
					ice_res_data.put(p.getName(), new_ice_res);
					poison_res_data.put(p.getName(), new_poison_res);

					gfind_data.put(p.getName(), new_gfind);
					ifind_data.put(p.getName(), new_ifind);
					p.setWalkSpeed(getPlayerSpeed(p));

					FatigueMechanics.updateEnergyRegenData(p, true);
					HealthMechanics.generateHealthRegenAmount(p, true);
					HealthMechanics.generateMaxHP(p);

					int new_min_armor = armor_data.get(p.getName()).get(0);
					int new_max_armor = armor_data.get(p.getName()).get(1);	

					int new_min_dmg = dmg_data.get(p.getName()).get(0);
					int new_max_dmg = dmg_data.get(p.getName()).get(1);	

					if(old_max_armor != new_max_armor){
						if(old_max_armor > new_max_armor){
							p.sendMessage(ChatColor.RED + "-" + (old_max_armor - new_max_armor) + "% ARMOR [" + new_min_armor + " - " + new_max_armor + "%]");
						}
						else{
							p.sendMessage(ChatColor.GREEN + "+" + (new_max_armor - old_max_armor) + "% ARMOR [" + new_min_armor + " - " + new_max_armor + "%]");
						}
					}

					if(old_max_dmg != new_max_dmg){
						if(old_max_dmg > new_max_dmg){
							p.sendMessage(ChatColor.RED + "-" + (old_max_dmg - new_max_dmg) + "% DPS [" + new_min_dmg + " - " + new_max_dmg + "%]");
						}
						else{
							p.sendMessage(ChatColor.GREEN + "+" + (new_max_dmg - old_max_dmg) + "% DPS [" + new_min_dmg + " - " + new_max_dmg + "%]");
						}
					}

					if(old_str != new_str){
						if(old_str > new_str){
							p.sendMessage(ChatColor.RED + "-" + (old_str - new_str) + " STR [" + new_str + "]");
						}
						else{
							p.sendMessage(ChatColor.GREEN + "+" + (new_str - old_str) + " STR [" + new_str + "]");
						}
					}

					if(old_dex != new_dex){
						if(old_dex > new_dex){
							p.sendMessage(ChatColor.RED + "-" + (old_dex - new_dex) + " DEX [" + new_dex + "]");
						}
						else{
							p.sendMessage(ChatColor.GREEN + "+" + (new_dex - old_dex) + " DEX [" + new_dex + "]");
						}
					}

					if(old_vit != new_vit){
						if(old_vit > new_vit){
							p.sendMessage(ChatColor.RED + "-" + (old_vit - new_vit) + " VIT [" + new_vit + "]");
						}
						else{
							p.sendMessage(ChatColor.GREEN + "+" + (new_vit - old_vit) + " VIT [" + new_vit + "]");
						}
					}

					if(old_int != new_int){
						if(old_int > new_int){
							p.sendMessage(ChatColor.RED + "-" + (old_int - new_int) + " INT [" + new_int + "]");
						}
						else{
							p.sendMessage(ChatColor.GREEN + "+" + (new_int - old_int) + " INT [" + new_int + "]");
						}
					}

					if(old_fire_res != new_fire_res){
						if(old_fire_res > new_fire_res){
							p.sendMessage(ChatColor.RED + "-" + (old_fire_res - new_fire_res) + "% FIRE RESISTANCE [" + new_fire_res + "%]");
						}
						else{
							p.sendMessage(ChatColor.GREEN + "+" + (new_fire_res - old_fire_res) + "% FIRE RESISTANCE [" + new_fire_res + "%]");
						}
					}

					if(old_ice_res != new_ice_res){
						if(old_ice_res > new_ice_res){
							p.sendMessage(ChatColor.RED + "-" + (old_ice_res - new_ice_res) + "% ICE RESISTANCE [" + new_ice_res + "%]");
						}
						else{
							p.sendMessage(ChatColor.GREEN + "+" + (new_ice_res - old_ice_res) + "% ICE RESISTANCE [" + new_ice_res + "%]");
						}
					}

					if(old_poison_res != new_poison_res){
						if(old_poison_res > new_poison_res){
							p.sendMessage(ChatColor.RED + "-" + (old_poison_res - new_poison_res) + "% POISON RESISTANCE [" + new_poison_res + "%]");
						}
						else{
							p.sendMessage(ChatColor.GREEN + "+" + (new_poison_res - old_poison_res) + "% POISON RESISTANCE [" + new_poison_res + "%]");
						}
					}

					if(old_dodge != new_dodge){
						if(old_dodge > new_dodge){
							p.sendMessage(ChatColor.RED + "-" + (old_dodge - new_dodge) + "% DODGE [" + new_dodge + "%]");
						}
						else{
							p.sendMessage(ChatColor.GREEN + "+" + (new_dodge - old_dodge) + "% DODGE [" + new_dodge + "%]");
						}
					}

					if(old_block != new_block){
						if(old_block > new_block){
							p.sendMessage(ChatColor.RED + "-" + (old_block - new_block) + "% BLOCK [" + new_block + "%]");
						}
						else{
							p.sendMessage(ChatColor.GREEN + "+" + (new_block - old_block) + "% BLOCK [" + new_block + "%]");
						}
					}

					if(old_thorns != new_thorns){
						if(old_thorns > new_thorns){
							p.sendMessage(ChatColor.RED + "-" + (old_thorns - new_thorns) + "% THORNS [" + new_thorns + "% DMG]");
						}
						else{
							p.sendMessage(ChatColor.GREEN + "+" + (new_thorns - old_thorns) + "% THORNS [" + new_thorns + "% DMG]");
						}
					}

					if(old_reflection != new_reflection){
						if(old_reflection > new_reflection){
							p.sendMessage(ChatColor.RED + "-" + (old_reflection - new_reflection) + "% REFLECT [" + new_reflection + "%]");
						}
						else{
							p.sendMessage(ChatColor.GREEN + "+" + (new_reflection - old_reflection) + "% REFLECT [" + new_reflection + "%]");
						}
					}

					if(old_gfind != new_gfind){
						if(old_gfind > new_gfind){
							p.sendMessage(ChatColor.RED + "-" + df.format((((double)old_gfind / 100.00D) - ((double)new_gfind / 100.00D))) + "x GEM FIND [" + df.format((((double)new_gfind / 100.00D) + 1.00D)) + "x]");
						}
						else{
							p.sendMessage(ChatColor.GREEN + "+" + df.format((((double)new_gfind / 100.00D) - ((double)old_gfind / 100.00D))) + "x GEM FIND [" + df.format((((double)new_gfind / 100.00D) + 1.00D)) + "x]");
						}
					}

					if(old_ifind != new_ifind){
						if(old_ifind > new_ifind){
							p.sendMessage(ChatColor.RED + "-" + df.format((((double)old_ifind / 100.00D) - ((double)new_ifind / 100.00D))) + "x ITEM FIND [" + df.format((((double)new_ifind / 100.00D) + 1.00D)) + "x]");
						}
						else{
							p.sendMessage(ChatColor.GREEN + "+" + df.format((((double)new_ifind / 100.00D) - ((double)old_ifind / 100.00D))) + "x ITEM FIND [" + df.format((((double)new_ifind / 100.00D) + 1.00D)) + "x]");
						}
					}

					updateArmorContentRecord(p);
					to_remove.add(p_name);
					//log.info("[ItemMechanics] Updated all local calculations for player " + p.getName() + ".");
				}

				for(String s : to_remove){
					need_update.remove(s);
				}

			}
		}, 40L, 1L); 

		log.info("[ItemMechanics] V1.0 has been enabled.");
	}

	public void onDisable() {
		log.info("[ItemMechanics] V1.0 has been disabled.");
	}

	public void loadCustomItemTemplates(){
		int count = 0;

		for(File f : new File("plugins/ItemMechanics/custom_items/").listFiles()){
			if(f.getName().contains(".item")){
				String template_name = f.getName().replaceAll(".item", "");
				custom_item_table.put(template_name, ItemGenerators.customGenerator(template_name));
				count++;
			}
		}

		log.info("[ItemMechanics] Loaded " + count + " custom item templates into memory.");
	}

	public static void updateArmorContentRecord(Player p){
		List<ItemStack> i_list= new ArrayList<ItemStack>();
		int highest_tier = 1;

		for(ItemStack i : p.getInventory().getArmorContents()){
			if(i != null && !(i.getType() == Material.AIR)){
				if(getItemTier(i) > highest_tier){
					highest_tier = getItemTier(i);
				}
				i_list.add(new ItemStack(i.getType(), 1));
			}
		}
		armor_contents.put(p.getName(), i_list);
		player_tier.put(p.getName(), highest_tier);
	}

	public List<ItemStack> getArmorContent(Player p){
		List<ItemStack> i_list = new ArrayList<ItemStack>();
		for(ItemStack i : p.getInventory().getArmorContents()){
			if(i != null && !(i.getType() == Material.AIR)){
				i_list.add(new ItemStack(i.getType(), 1));
			}
		}

		return i_list;
	}

	public static String getDamageRange(ItemStack i){
		if(i != null && i.hasItemMeta() && i.getItemMeta().hasLore()){
			for(String s : i.getItemMeta().getLore()){
				if(s.contains(red.toString() + "DMG: ")){
					String dmg_data = s;
					dmg_data = dmg_data.replaceAll(red.toString() + "DMG: ", "");

					dmg_data = dmg_data.replaceAll(" ", "");

					int min_dmg = Integer.parseInt(dmg_data.split("-")[0]);
					int max_dmg = Integer.parseInt(dmg_data.split("-")[1]);

					String dmg_range = min_dmg + "-" + max_dmg;
					return dmg_range;
				}
			}
		}
		
		return "no";
	}

	public static void updatePlayerStats(String p_name){
		if(Bukkit.getPlayer(p_name) == null || !Bukkit.getPlayer(p_name).isOnline()){
			return;
		}
		Player p = Bukkit.getPlayer(p_name);
		//List<Integer> net_armor_vals = new ArrayList<Integer>(armor_data.get(p.getName()));
		if(!(armor_data.containsKey(p.getName()))){
			need_update.remove(p);
			return; 
		}

		if(p.getHealth() <= 0 || HealthMechanics.getPlayerHP(p.getName()) <= 0){
			return;
		}

		if(DuelMechanics.duel_max_armor_tier.containsKey(p.getName()) && DuelMechanics.duel_map.containsKey(p.getName())){
			// Let's check if they're allowed to equip it.
			int max_armor = DuelMechanics.duel_max_armor_tier.get(p.getName());
			int armor_tier = -1;

			for(ItemStack is : p.getInventory().getArmorContents()){
				armor_tier = getItemTier(is);
				if(armor_tier > max_armor){
					// Do not allow.
					continue;
				}
			}
		}

		//int old_min_armor = armor_data.get(p.getName()).get(0);
		int old_max_armor = armor_data.get(p.getName()).get(1);

		//int old_min_dmg = dmg_data.get(p.getName()).get(0);
		int old_max_dmg = dmg_data.get(p.getName()).get(1);

		int old_thorns = thorn_data.get(p.getName());
		int old_block = block_data.get(p.getName());
		int old_dodge = dodge_data.get(p.getName());
		int old_reflection = reflection_data.get(p.getName());
		int old_ifind = ifind_data.get(p.getName());
		int old_gfind = gfind_data.get(p.getName());

		int old_str = str_data.get(p.getName());
		int old_dex = dex_data.get(p.getName());
		int old_vit = vit_data.get(p.getName());
		int old_int = int_data.get(p.getName());

		int old_fire_res = fire_res_data.get(p.getName());
		int old_ice_res = ice_res_data.get(p.getName());
		int old_poison_res = poison_res_data.get(p.getName());


		int new_thorns = generateTotalThornVal(p);
		int new_block = generateTotalBlockChance(p);
		int new_dodge = generateTotalDodgeChance(p);
		int new_reflection = generateTotalReflectChance(p);
		int new_ifind = generateTotalItemFindChance(p);
		int new_gfind = generateTotalGoldFindChance(p);
		int new_str = generateTotalStrVal(p);
		int new_dex = generateTotalDexVal(p);
		int new_vit = generateTotalVitVal(p);
		int new_int = generateTotalIntVal(p);
		int new_fire_res = generateTotalFireRes(p);
		int new_ice_res = generateTotalIceRes(p);
		int new_poison_res = generateTotalPoisonRes(p);

		DecimalFormat df = new DecimalFormat("#.##");

		armor_data.put(p.getName(), generateTotalArmorVal(p));
		dmg_data.put(p.getName(), generateTotalDmgVal(p));

		str_data.put(p.getName(), new_str);
		dex_data.put(p.getName(), new_dex);
		vit_data.put(p.getName(), new_vit);
		int_data.put(p.getName(), new_int);

		dodge_data.put(p.getName(), new_dodge);
		block_data.put(p.getName(), new_block);
		thorn_data.put(p.getName(), new_thorns);
		reflection_data.put(p.getName(), new_reflection);

		fire_res_data.put(p.getName(), new_fire_res);
		ice_res_data.put(p.getName(), new_ice_res);
		poison_res_data.put(p.getName(), new_poison_res);

		gfind_data.put(p.getName(), new_gfind);
		ifind_data.put(p.getName(), new_ifind);
		p.setWalkSpeed(getPlayerSpeed(p));

		FatigueMechanics.updateEnergyRegenData(p, true);
		HealthMechanics.generateHealthRegenAmount(p, true);
		int new_max_hp = HealthMechanics.generateMaxHP(p);

		if(HealthMechanics.health_data.containsKey(p.getName()) && new_max_hp != HealthMechanics.health_data.get(p.getName())){
			HealthMechanics.health_data.put(p.getName(), (int)new_max_hp);

			if(HealthMechanics.getPlayerHP(p.getName()) > (int)new_max_hp){
				//p.setLevel((int)new_max_hp);
				HealthMechanics.setPlayerHP(p.getName(), (int)new_max_hp);
			}

			double d_level = HealthMechanics.getPlayerHP(p.getName());

			double health_percent = d_level / new_max_hp;
			double new_health_display = (health_percent * 20.0D);
			//log.info(String.valueOf(health_percent));
			//log.info(String.valueOf(new_health_display));

			int conv_newhp_display = (int) Math.abs(new_health_display);
			if(conv_newhp_display < 1){
				conv_newhp_display = 1;
			}
			if(conv_newhp_display > 20){
				conv_newhp_display = 20;
			}

			p.setHealth(conv_newhp_display);
		}

		int new_min_armor = armor_data.get(p.getName()).get(0);
		int new_max_armor = armor_data.get(p.getName()).get(1);	

		int new_min_dmg = dmg_data.get(p.getName()).get(0);
		int new_max_dmg = dmg_data.get(p.getName()).get(1);	

		if(old_max_armor != new_max_armor){
			if(old_max_armor > new_max_armor){
				p.sendMessage(ChatColor.RED + "-" + (old_max_armor - new_max_armor) + "% ARMOR [" + new_min_armor + " - " + new_max_armor + "%]");
			}
			else{
				p.sendMessage(ChatColor.GREEN + "+" + (new_max_armor - old_max_armor) + "% ARMOR [" + new_min_armor + " - " + new_max_armor + "%]");
			}
		}

		if(old_max_dmg != new_max_dmg){
			if(old_max_dmg > new_max_dmg){
				p.sendMessage(ChatColor.RED + "-" + (old_max_dmg - new_max_dmg) + "% DPS [" + new_min_dmg + " - " + new_max_dmg + "%]");
			}
			else{
				p.sendMessage(ChatColor.GREEN + "+" + (new_max_dmg - old_max_dmg) + "% DPS [" + new_min_dmg + " - " + new_max_dmg + "%]");
			}
		}

		if(old_str != new_str){
			if(old_str > new_str){
				p.sendMessage(ChatColor.RED + "-" + (old_str - new_str) + " STR [" + new_str + "]");
			}
			else{
				p.sendMessage(ChatColor.GREEN + "+" + (new_str - old_str) + " STR [" + new_str + "]");
			}
		}

		if(old_dex != new_dex){
			if(old_dex > new_dex){
				p.sendMessage(ChatColor.RED + "-" + (old_dex - new_dex) + " DEX [" + new_dex + "]");
			}
			else{
				p.sendMessage(ChatColor.GREEN + "+" + (new_dex - old_dex) + " DEX [" + new_dex + "]");
			}
		}

		if(old_vit != new_vit){
			if(old_vit > new_vit){
				p.sendMessage(ChatColor.RED + "-" + (old_vit - new_vit) + " VIT [" + new_vit + "]");
			}
			else{
				p.sendMessage(ChatColor.GREEN + "+" + (new_vit - old_vit) + " VIT [" + new_vit + "]");
			}
		}

		if(old_int != new_int){
			if(old_int > new_int){
				p.sendMessage(ChatColor.RED + "-" + (old_int - new_int) + " INT [" + new_int + "]");
			}
			else{
				p.sendMessage(ChatColor.GREEN + "+" + (new_int - old_int) + " INT [" + new_int + "]");
			}
		}

		if(old_fire_res != new_fire_res){
			if(old_fire_res > new_fire_res){
				p.sendMessage(ChatColor.RED + "-" + (old_fire_res - new_fire_res) + "% FIRE RESISTANCE [" + new_fire_res + "%]");
			}
			else{
				p.sendMessage(ChatColor.GREEN + "+" + (new_fire_res - old_fire_res) + "% FIRE RESISTANCE [" + new_fire_res + "%]");
			}
		}

		if(old_ice_res != new_ice_res){
			if(old_ice_res > new_ice_res){
				p.sendMessage(ChatColor.RED + "-" + (old_ice_res - new_ice_res) + "% ICE RESISTANCE [" + new_ice_res + "%]");
			}
			else{
				p.sendMessage(ChatColor.GREEN + "+" + (new_ice_res - old_ice_res) + "% ICE RESISTANCE [" + new_ice_res + "%]");
			}
		}

		if(old_poison_res != new_poison_res){
			if(old_poison_res > new_poison_res){
				p.sendMessage(ChatColor.RED + "-" + (old_poison_res - new_poison_res) + "% POISON RESISTANCE [" + new_poison_res + "%]");
			}
			else{
				p.sendMessage(ChatColor.GREEN + "+" + (new_poison_res - old_poison_res) + "% POISON RESISTANCE [" + new_poison_res + "%]");
			}
		}

		if(old_dodge != new_dodge){
			if(old_dodge > new_dodge){
				p.sendMessage(ChatColor.RED + "-" + (old_dodge - new_dodge) + "% DODGE [" + new_dodge + "%]");
			}
			else{
				p.sendMessage(ChatColor.GREEN + "+" + (new_dodge - old_dodge) + "% DODGE [" + new_dodge + "%]");
			}
		}

		if(old_block != new_block){
			if(old_block > new_block){
				p.sendMessage(ChatColor.RED + "-" + (old_block - new_block) + "% BLOCK [" + new_block + "%]");
			}
			else{
				p.sendMessage(ChatColor.GREEN + "+" + (new_block - old_block) + "% BLOCK [" + new_block + "%]");
			}
		}

		if(old_thorns != new_thorns){
			if(old_thorns > new_thorns){
				p.sendMessage(ChatColor.RED + "-" + (old_thorns - new_thorns) + "% THORNS [" + new_thorns + "% DMG]");
			}
			else{
				p.sendMessage(ChatColor.GREEN + "+" + (new_thorns - old_thorns) + "% THORNS [" + new_thorns + "% DMG]");
			}
		}

		if(old_reflection != new_reflection){
			if(old_reflection > new_reflection){
				p.sendMessage(ChatColor.RED + "-" + (old_reflection - new_reflection) + "% REFLECT [" + new_reflection + "%]");
			}
			else{
				p.sendMessage(ChatColor.GREEN + "+" + (new_reflection - old_reflection) + "% REFLECT [" + new_reflection + "%]");
			}
		}

		if(old_gfind != new_gfind){
			if(old_gfind > new_gfind){
				p.sendMessage(ChatColor.RED + "-" + df.format((((double)old_gfind / 100.00D) - ((double)new_gfind / 100.00D))) + "x GEM FIND [" + df.format((((double)new_gfind / 100.00D) + 1.00D)) + "x]");
			}
			else{
				p.sendMessage(ChatColor.GREEN + "+" + df.format((((double)new_gfind / 100.00D) - ((double)old_gfind / 100.00D))) + "x GEM FIND [" + df.format((((double)new_gfind / 100.00D) + 1.00D)) + "x]");
			}
		}

		if(old_ifind != new_ifind){
			if(old_ifind > new_ifind){
				p.sendMessage(ChatColor.RED + "-" + df.format((((double)old_ifind / 100.00D) - ((double)new_ifind / 100.00D))) + "x ITEM FIND [" + df.format((((double)new_ifind / 100.00D) + 1.00D)) + "x]");
			}
			else{
				p.sendMessage(ChatColor.GREEN + "+" + df.format((((double)new_ifind / 100.00D) - ((double)old_ifind / 100.00D))) + "x ITEM FIND [" + df.format((((double)new_ifind / 100.00D) + 1.00D)) + "x]");
			}
		}

		updateArmorContentRecord(p);
	}

	public static ItemStack generateNoobWeapon(){
		int wep_type = new Random().nextInt(2);
		if(wep_type == 0){
			return removeAttributes(signCustomItem(Material.WOOD_SWORD, (short)0, ChatColor.WHITE.toString() + "Training Sword", ChatColor.RED.toString() + "DMG: 3 - 4"));
		}
		return removeAttributes(signCustomItem(Material.WOOD_AXE, (short)0, ChatColor.WHITE.toString() + "Training Hatchet", ChatColor.RED.toString() + "DMG: 2 - 5"));
	}


	public static List<ItemStack> generateNoobArmor(){
		/*int wep_type = new Random().nextInt(2);
		if(wep_type == 0){
			return signCustomItem(Material.WOOD_SWORD, (short)0, ChatColor.WHITE.toString() + "Training Sword", ChatColor.RED.toString() + "DMG: 3 - 4");
		}
		return signCustomItem(Material.WOOD_AXE, (short)0, ChatColor.WHITE.toString() + "Training Hatchet", ChatColor.RED.toString() + "DMG: 2 - 5");*/
		List<ItemStack> armor = new ArrayList<ItemStack>();
		armor.add(removeAttributes(signCustomItem(Material.LEATHER_HELMET, (short)0, ChatColor.WHITE.toString() + "Leather Training Helmet", ChatColor.RED.toString() 
				+ "ARMOR: 0 - 1%" + "," + ChatColor.RED.toString() + "HP: +1" + "," + ChatColor.RED.toString() + "HP REGEN: +1 HP/s")));
		armor.add(removeAttributes(signCustomItem(Material.LEATHER_CHESTPLATE, (short)0, ChatColor.WHITE.toString() + "Leather Training Chestplate", ChatColor.RED.toString() 
				+ "ARMOR: 1 - 1%" + "," + ChatColor.RED.toString() + "HP: +3" + "," + ChatColor.RED.toString() + "HP REGEN: +2 HP/s")));
		armor.add(removeAttributes(signCustomItem(Material.LEATHER_LEGGINGS, (short)0, ChatColor.WHITE.toString() + "Leather Training Leggings", ChatColor.RED.toString() 
				+ "ARMOR: 1 - 1%" + "," + ChatColor.RED.toString() + "HP: +2" + "," + ChatColor.RED.toString() + "ENERGY REGEN: +1%")));
		armor.add(removeAttributes(signCustomItem(Material.LEATHER_BOOTS, (short)0, ChatColor.WHITE.toString() + "Leather Training Boots", ChatColor.RED.toString() 
				+ "ARMOR: 0 - 1%" + "," + ChatColor.RED.toString() + "HP: +1" + "," + ChatColor.RED.toString() + "HP REGEN: +1 HP/s")));
		return armor;
	}

	public static String getDamageData(ItemStack i){
		//CraftItemStack css = (CraftItemStack)i;
		try{
			try{
				try{
					//NBTTagList description = CraftItemStack.asNMSCopy(i).getTag().getCompound("display").getList("Lore", 0);
					List<String> lore = i.getItemMeta().getLore();
					
					//int x = 0;

					boolean elemental_dmg = false;
					String elemental_data = "";
					int edmg = 0;
					String leech_percent = "";

					boolean crit_dmg = false;
					boolean leech = false;
					boolean knockback = false;
					boolean blind = false;
					boolean str = false;
					boolean dex = false;
					boolean vit = false;
					boolean intel = false;
					boolean pure_dmg = false;
					boolean armor_pen = false;
					boolean vs_players = false;
					boolean vs_monsters = false;
					boolean accuracy = false;

					int str_atr = 0;
					int dex_atr = 0;
					int vit_atr = 0;
					int int_atr = 0;

					double vs_modifier = 0;

					int pure_dmg_val = 0;
					int armor_pen_val = 0;
					int accuracy_val = 0;

					List<String> all_attributes = new ArrayList<String>();

					for(String s : lore){
						if(!(s).startsWith(ChatColor.RED.toString())){
							//x++;
							continue;
						}

						all_attributes.add(s);
						if(s.contains("DMG: +")){
							elemental_dmg = true;
							elemental_data = s;
						}
						if(s.contains("STR:")){
							str = true;
							String str_string = s;
							str_atr = Integer.parseInt(str_string.substring(str_string.indexOf(":") + 3, str_string.length()));
						}
						if(s.contains("DEX:")){
							dex = true;
							String dex_string = s;
							dex_atr = Integer.parseInt(dex_string.substring(dex_string.indexOf(":") + 3, dex_string.length()));
						}
						if(s.contains("VIT:")){
							vit = true;
							String vit_string = s;
							vit_atr = Integer.parseInt(vit_string.substring(vit_string.indexOf(":") + 3, vit_string.length()));
						}
						if(s.contains("INT:")){
							intel = true;
							String int_string = s;
							int_atr = Integer.parseInt(int_string.substring(int_string.indexOf(":") + 3, int_string.length()));
						}
						if(s.contains("PURE DMG:")){
							pure_dmg = true;
							String pure_dmg_string = s;
							pure_dmg_val = Integer.parseInt(pure_dmg_string.substring(pure_dmg_string.indexOf(":") + 3, pure_dmg_string.length()));
						}
						if(s.contains("ACCURACY:")){
							accuracy = true;
							String accuracy_string = s;
							accuracy_val = Integer.parseInt(accuracy_string.substring(accuracy_string.indexOf(":") + 2, accuracy_string.indexOf("%")));
						}
						if(s.contains("ARMOR PENETRATION:")){
							armor_pen = true;
							String armor_pen_string = s;
							armor_pen_val = Integer.parseInt(armor_pen_string.substring(armor_pen_string.indexOf(":") + 2, armor_pen_string.indexOf("%")));
						}
						if(s.contains("CRITICAL HIT")
								|| (i.getType() == Material.WOOD_AXE || i.getType() == Material.STONE_AXE || i.getType() == Material.IRON_AXE || i.getType() == Material.DIAMOND_AXE || i.getType() == Material.GOLD_AXE)){

							int crit_chance = 0;

							if(s.contains("CRITICAL HIT")){
								String crit_data = s;
								crit_chance = Integer.parseInt(crit_data.substring(crit_data.indexOf(":") + 1, crit_data.indexOf("%")).replaceAll(" ", ""));
							}

							if((i.getType() == Material.WOOD_AXE || i.getType() == Material.STONE_AXE || i.getType() == Material.IRON_AXE || i.getType() == Material.DIAMOND_AXE || i.getType() == Material.GOLD_AXE)){
								crit_chance += 3;
							}

							if(dex == true){
								crit_chance += (dex_atr * 0.005); 
							}

							if(crit_chance >= new Random().nextInt(100)){
								crit_dmg = true;
							}
						}
						if(s.contains("LIFE STEAL")){
							String leech_data = s;
							leech = true;
							leech_percent = leech_data.substring(leech_data.indexOf(":") + 1, leech_data.indexOf("%")).replaceAll(" ", "");
						}
						if(s.contains("KNOCKBACK")){
							String kb_data = s;

							int kb_chance = Integer.parseInt(kb_data.substring(kb_data.indexOf(":") + 1, kb_data.indexOf("%")).replaceAll(" ", ""));
							if(kb_chance >= new Random().nextInt(100)){
								knockback = true;
							}
						}
						if(s.contains("BLIND")){
							String blind_data = s;

							int blind_chance = Integer.parseInt(blind_data.substring(blind_data.indexOf(":") + 1, blind_data.indexOf("%")).replaceAll(" ", ""));
							if(blind_chance >= new Random().nextInt(100)){
								blind = true;
							}
						}
						if(s.contains("vs. PLAYERS")){
							String vs_data = s;
							vs_modifier = Integer.parseInt(vs_data.substring(vs_data.indexOf("+") + 1, vs_data.indexOf("%")).replaceAll(" ", ""));
							vs_players = true;
						}
						if(s.contains("vs. MONSTERS")){
							String vs_data = s;
							vs_modifier = Integer.parseInt(vs_data.substring(vs_data.indexOf("+") + 1, vs_data.indexOf("%")).replaceAll(" ", ""));
							vs_monsters = true;
						}
						//x++;
					}

					String dmg_data = all_attributes.get(0);
					dmg_data = dmg_data.replaceAll(red.toString() + "DMG: ", "");

					dmg_data = dmg_data.replaceAll(" ", "");

					int min_dmg = Integer.parseInt(dmg_data.split("-")[0]);
					int max_dmg = Integer.parseInt(dmg_data.split("-")[1]);

					if((max_dmg - min_dmg) <= 0){
						max_dmg+=1;
					}

					if(min_dmg > max_dmg){
						min_dmg = 1;
						max_dmg = 2;
					}

					int dmg = new Random().nextInt(max_dmg - min_dmg) + min_dmg;

					if(elemental_dmg == true){
						edmg = Integer.parseInt(elemental_data.split("\\+")[1] );
					}

					String return_string = String.valueOf(dmg + edmg) + ":";

					if(str == true && (i.getType() == Material.WOOD_AXE || i.getType() == Material.IRON_AXE || i.getType() == Material.STONE_AXE 
							|| i.getType() == Material.DIAMOND_AXE || i.getType() == Material.GOLD_AXE)){
						double str_mod = 0.015D * str_atr;
						return_string = return_string + "str=" + str_mod + ":";
					}

					if(dex == true && (i.getType() == Material.BOW)){
						double dex_mod = 0.015D * dex_atr;
						return_string = return_string + "dex=" + dex_mod + ":";
					}

					if(vit == true && (i.getType() == Material.WOOD_SWORD || i.getType() == Material.IRON_SWORD || i.getType() == Material.STONE_SWORD 
							|| i.getType() == Material.DIAMOND_SWORD || i.getType() == Material.GOLD_SWORD)){
						double vit_mod = 0.015D * vit_atr;
						return_string = return_string + "vit=" + vit_mod + ":";
					}

					if(str == true){
						return_string = return_string + "str_raw=" + str_atr + ":";
					}

					if(dex == true){
						return_string = return_string + "dex_raw=" + dex_atr + ":";
					}

					if(vit == true){
						return_string = return_string + "vit_raw=" + vit_atr + ":";
					}

					if(intel == true){
						return_string = return_string + "int_raw=" + int_atr + ":";
					}

					if(accuracy == true){
						return_string = return_string + "accuracy=" + accuracy_val + ":";
					}

					if(elemental_dmg == true){
						String element_damage_type = elemental_data.substring(0, elemental_data.indexOf(" "));
						return_string = return_string + "edmg=" + element_damage_type + ":";
					}

					if(armor_pen == true){
						return_string = return_string + "armor_pen=" + armor_pen_val + ":";
					}

					if(pure_dmg == true){
						return_string = return_string + "pure_dmg=" + pure_dmg_val + ":";
					}

					if(crit_dmg == true){
						return_string = return_string + "crit=true" + ":";
					}

					if(leech == true){
						return_string = return_string + "leech=" + leech_percent + ":";
					}

					if(knockback == true){
						return_string = return_string + "knockback=true" + ":";
					}

					if(vs_players == true){
						return_string = return_string + "vs_players=" + vs_modifier + ":";
					}

					if(vs_monsters == true){
						return_string = return_string + "vs_monsters=" + vs_modifier + ":";
					}

					if(blind == true){
						return_string = return_string + "blind=true";
					}

					return return_string;
				} catch(NumberFormatException e){
					return "no";
				}
			} catch(IndexOutOfBoundsException e){
				return "no";
			}
		} catch(NullPointerException e){
			return "no";
		}
	}

	public static String getItemName(ItemStack i){
		CraftItemStack css = (CraftItemStack)i;
		String name = CraftItemStack.asNMSCopy(css).getTag().getCompound("display").getString("Name");
		if(name.contains(ChatColor.WHITE.toString())){
			name.replaceAll(ChatColor.WHITE.toString(), "");
		}
		if(name.contains(ChatColor.GREEN.toString())){
			name.replaceAll(ChatColor.GREEN.toString(), "");
		}
		if(name.contains(ChatColor.AQUA.toString())){
			name.replaceAll(ChatColor.AQUA.toString(), "");
		}
		if(name.contains(ChatColor.LIGHT_PURPLE.toString())){
			name.replaceAll(ChatColor.LIGHT_PURPLE.toString(), "");
		}
		if(name.contains(ChatColor.YELLOW.toString())){
			name.replaceAll(ChatColor.YELLOW.toString(), "");
		}

		return name;
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


	public static int getItemTier(ItemStack i){
		try{
			String name = i.getItemMeta().getDisplayName();
			if(name.contains(ChatColor.GREEN.toString())){
				return 2;
			}
			if(name.contains(ChatColor.AQUA.toString())){
				return 3;
			}
			if(name.contains(ChatColor.LIGHT_PURPLE.toString())){
				return 4;
			}
			if(name.contains(ChatColor.YELLOW.toString())){
				return 5;
			}
			if(name.contains(ChatColor.WHITE.toString())){
				return 1;
			}

			return 1;
		} catch(NullPointerException npe){
			//npe.printStackTrace();
			return 0;
		}
	}

	public static String getArmorData(ItemStack i){

		try{
			try{
				try{
					List<String> lore = i.getItemMeta().getLore();
					//NBTTagList description = CraftItemStack.asNMSCopy(i).getTag().getCompound("display").getList("Lore", 0);

					//int x = 0;

					boolean hp_regen = false,/* hp_increase = false,*/ energy_regen = false, str=false,dex=false,vit=false,intel=false, block = false, dodge = false, thorns = false, reflection = false, gold_find = false, item_find = false, speed_boost = false;
					boolean fire_res = false, ice_res = false, poison_res = false;

					int hp_regen_amount=0, energy_regen_amount=0, val_fire_res=0, val_ice_res=0, val_poison_res=0, dodge_chance=0, str_atr=0, dex_atr=0, vit_atr=0, int_atr=0, block_chance=0, thorns_amount=0, reflection_chance=0, item_find_amount=0, gold_find_amount=0;
					float speed_mod = 0.0F;

					List<String> all_attributes = new ArrayList<String>();

					for(String s : lore){
						if(!(s).startsWith(ChatColor.RED.toString())){
							//x++;
							continue;
						}

						all_attributes.add(s);
						if(s.contains("HP REGEN")){
							hp_regen = true;
							String hp_regen_data = s;
							hp_regen_amount = Integer.parseInt(hp_regen_data.substring(hp_regen_data.indexOf(":") + 3, hp_regen_data.lastIndexOf(" ")).replaceAll(" ", ""));
						}

						if(s.contains("ENERGY REGEN")){
							energy_regen = true;
							String energy_regen_data = s;
							energy_regen_amount = Integer.parseInt(energy_regen_data.substring(energy_regen_data.indexOf(":") + 3, energy_regen_data.indexOf("%")).replaceAll(" ", ""));
						}

						if(s.contains("FIRE RESISTANCE")){
							fire_res = true;
							String fire_res_data = s;
							val_fire_res = Integer.parseInt(fire_res_data.substring(fire_res_data.indexOf(":") + 2, fire_res_data.indexOf("%")).replaceAll(" ", ""));
						}

						if(s.contains("ICE RESISTANCE")){
							ice_res = true;
							String ice_res_data = s;
							val_ice_res = Integer.parseInt(ice_res_data.substring(ice_res_data.indexOf(":") + 2, ice_res_data.indexOf("%")).replaceAll(" ", ""));
						}

						if(s.contains("POISON RESISTANCE")){
							poison_res = true;
							String poison_res_data = s;
							val_poison_res = Integer.parseInt(poison_res_data.substring(poison_res_data.indexOf(":") + 2, poison_res_data.indexOf("%")).replaceAll(" ", ""));
						}

						if(s.contains("STR")){
							str = true;
							String stat_data = s;
							str_atr = Integer.parseInt(stat_data.substring(stat_data.indexOf(":") + 3, stat_data.length()).replaceAll(" ", ""));
						}

						if(s.contains("DEX")){
							dex = true;
							String stat_data = s;
							dex_atr = Integer.parseInt(stat_data.substring(stat_data.indexOf(":") + 3, stat_data.length()).replaceAll(" ", ""));
						}

						if(s.contains("VIT")){
							vit = true;
							String stat_data = s;
							vit_atr = Integer.parseInt(stat_data.substring(stat_data.indexOf(":") + 3, stat_data.length()).replaceAll(" ", ""));
						}

						if(s.contains("INT")){
							intel = true;
							String int_string = s;
							int_atr = Integer.parseInt(int_string.substring(int_string.indexOf(":") + 3, int_string.length()));
						}

						if(s.contains("SPEED BOOST")){
							speed_boost = true;
							String speed_boost_data = s;
							speed_mod = Float.parseFloat(speed_boost_data.substring(speed_boost_data.indexOf(":") + 3, speed_boost_data.indexOf("X")).replaceAll(" ", ""));
						}

						if(s.contains("DODGE")){
							String dodge_data = s;

							dodge_chance = Integer.parseInt(dodge_data.substring(dodge_data.indexOf(":") + 1, dodge_data.indexOf("%")).replaceAll(" ", ""));
							dodge = true;
						}

						if(s.contains("BLOCK")){
							String block_data = s;

							block_chance = Integer.parseInt(block_data.substring(block_data.indexOf(":") + 1, block_data.indexOf("%")).replaceAll(" ", ""));
							block = true;
						}

						if(s.contains("THORNS")){
							thorns = true;
							String thorns_data = s;
							thorns_amount = Integer.parseInt(thorns_data.substring(thorns_data.indexOf(":") + 1, thorns_data.indexOf("%")).replaceAll(" ", ""));
						}

						if(s.contains("REFLECTION")){
							String reflection_data = s;

							reflection_chance = Integer.parseInt(reflection_data.substring(reflection_data.indexOf(":") + 1, reflection_data.indexOf("%")).replaceAll(" ", ""));
							reflection = true;
						}

						if(s.contains("ITEM FIND")){
							item_find = true;
							String item_find_data = s;
							item_find_amount = Integer.parseInt(item_find_data.substring(item_find_data.indexOf(":") + 1, item_find_data.indexOf("%")).replaceAll(" ", ""));
						}

						if(s.contains("GEM FIND") || s.contains("GOLD FIND")){
							gold_find = true;
							String gold_find_data = s;
							gold_find_amount = Integer.parseInt(gold_find_data.substring(gold_find_data.indexOf(":") + 1, gold_find_data.indexOf("%")).replaceAll(" ", ""));
						}

						//x++;
					}

					String return_string = "";

					String larmor_data = all_attributes.get(0);
					if(larmor_data.contains("ARMOR")){
						larmor_data = larmor_data.replaceAll(ChatColor.RED.toString() + "ARMOR: ", "");
						larmor_data = larmor_data.replaceAll(" ", "");

						int min_armor = Integer.parseInt(larmor_data.split("-")[0]);
						int max_armor = Integer.parseInt(larmor_data.split("-")[1].replaceAll("%", ""));

						return_string = min_armor + " - " + max_armor + ":";
						//int armor = new Random().nextInt(max_armor);
						//if(armor < min_armor){armor = min_armor;}

					}
					else if(larmor_data.contains("DPS")){
						larmor_data = larmor_data.replaceAll(ChatColor.RED.toString() + "DPS: ", "");
						larmor_data = larmor_data.replaceAll(" ", "");

						int min_armor = Integer.parseInt(larmor_data.split("-")[0]);
						int max_armor = Integer.parseInt(larmor_data.split("-")[1].replaceAll("%", ""));

						return_string = "!" + min_armor + " - " + max_armor + ":";
					}

					if(return_string.length() <= 0){
						return "no";
					}

					String health_data = all_attributes.get(1);
					health_data = health_data.replaceAll(ChatColor.RED.toString() + "HP: \\+", "");
					return_string += health_data + "@:";

					if(hp_regen == true){
						return_string = return_string + "hp_regen=" + hp_regen_amount + "@hp_regen_split@:";
						//log.info("hp_regen = " + hp_regen_amount);
					}

					if(energy_regen == true){
						return_string = return_string + "energy_regen=" + energy_regen_amount + "@energy_regen_split@:";
					}

					if(fire_res == true){
						return_string = return_string + "fire_resistance=" + val_fire_res + ":";
					}

					if(ice_res == true){
						return_string = return_string + "ice_resistance=" + val_ice_res + ":";
					}

					if(poison_res == true){
						return_string = return_string + "poison_resistance=" + val_poison_res + ":";
					}

					if(str == true){
						return_string = return_string + "str=" + str_atr + ":";
					}

					if(dex == true){
						return_string = return_string + "dex=" + dex_atr + ":";
					}

					if(vit == true){
						return_string = return_string + "vit=" + vit_atr + ":";
					}

					if(intel == true){
						return_string = return_string + "int=" + int_atr + ":";
					}

					if(speed_boost == true){
						return_string = return_string + "speed=" + speed_mod + ":";
					}

					if(block == true){
						return_string = return_string + "block=" + block_chance + ":";
					}

					if(dodge == true){
						return_string = return_string + "dodge=" + dodge_chance + ":";
					}

					if(thorns == true){
						return_string = return_string + "thorns=" + thorns_amount + ":";
					}

					if(reflection == true){
						return_string = return_string + "reflection=" + reflection_chance + ":";
					}

					if(gold_find == true){
						return_string = return_string + "gold_find=" + gold_find_amount + ":";
					}

					if(item_find == true){
						return_string = return_string + "item_find=" + item_find_amount + ":";
					}

					return return_string;
				} catch(NumberFormatException e){
					//e.printStackTrace();
					return "no";
				}
			} catch(IndexOutOfBoundsException e){
				//e.printStackTrace();
				return "no";
			}
		} catch(NullPointerException e){
			//e.printStackTrace();
			return "no";
		}
	}

	public static List<Integer> generateTotalArmorVal(Player p){
		ItemStack[] armor_contents = p.getInventory().getArmorContents();
		//int total_armor_val = 0;
		List<Integer> net_armor_vals = new ArrayList<Integer>();

		for(ItemStack i : armor_contents){
			if(i.getType() == Material.AIR){continue;}
			if(getArmorVal(i, false) == null){continue;}
			List<Integer> temp_armor_vals = new ArrayList<Integer>(getArmorVal(i, true));

			int old_min = 0;
			int old_max = 0;

			if(net_armor_vals.size() > 0){
				old_min = net_armor_vals.get(0);
				old_max = net_armor_vals.get(1);
			}

			net_armor_vals.add(0, temp_armor_vals.get(0) + old_min);
			net_armor_vals.add(1, temp_armor_vals.get(1) + old_max);
		}

		if(net_armor_vals.size() < 1){
			net_armor_vals.add(0, 0);
			net_armor_vals.add(1, 0);
		}

		if(net_armor_vals.get(0) > 80 || net_armor_vals.get(1) > 80){
			if(net_armor_vals.get(0) > 80){
				// Let min. inherent itself if it's not >80.
				net_armor_vals.set(0, 80);
			}
			net_armor_vals.set(1, 80);
		}

		return net_armor_vals;
	}

	public static List<Integer> generateTotalDmgVal(Player p){
		ItemStack[] armor_contents = p.getInventory().getArmorContents();
		List<Integer> net_dmg_vals = new ArrayList<Integer>();

		for(ItemStack i : armor_contents){
			if(i.getType() == Material.AIR){continue;}
			if(getDmgVal(i, false) == null){continue;}
			List<Integer> temp_dmg_vals = new ArrayList<Integer>(getDmgVal(i, true));

			int old_min = 0;
			int old_max = 0;

			if(net_dmg_vals.size() > 0){
				old_min = net_dmg_vals.get(0);
				old_max = net_dmg_vals.get(1);
			}

			net_dmg_vals.add(0, temp_dmg_vals.get(0) + old_min);
			net_dmg_vals.add(1, temp_dmg_vals.get(1) + old_max);
		}

		if(net_dmg_vals.size() < 1){
			net_dmg_vals.add(0, 0);
			net_dmg_vals.add(1, 0);
		}

		if(net_dmg_vals.get(0) > 80 || net_dmg_vals.get(1) > 80){
			if(net_dmg_vals.get(0) > 80){
				// Let min. inherent itself if it's not >80.
				net_dmg_vals.set(0, 80);
			}
			net_dmg_vals.set(1, 80);
		}

		return net_dmg_vals;
	}

	public static int generateTotalThornVal(Player p){
		ItemStack[] armor_contents = p.getInventory().getArmorContents();
		int total_thorn_val = 0;
		for(ItemStack i : armor_contents){
			if(i.getType() == Material.AIR){continue;}
			total_thorn_val += getThornVal(i);
		}

		return total_thorn_val;
	}

	public static int generateTotalBlockChance(Player p){
		ItemStack[] armor_contents = p.getInventory().getArmorContents();
		int total_block_percent = -1;
		for(ItemStack i : armor_contents){
			if(i.getType() == Material.AIR){continue;}
			total_block_percent += getBlockVal(i);
		}

		return total_block_percent;
	}

	public static int generateTotalDodgeChance(Player p){
		ItemStack[] armor_contents = p.getInventory().getArmorContents();
		int total_dodge_percent = -1;
		for(ItemStack i : armor_contents){
			if(i.getType() == Material.AIR){continue;}
			total_dodge_percent += getDodgeVal(i);
		}

		return total_dodge_percent;
	}

	public static int generateTotalReflectChance(Player p){
		ItemStack[] armor_contents = p.getInventory().getArmorContents();
		int total_reflect_percent = -1;
		for(ItemStack i : armor_contents){
			if(i.getType() == Material.AIR){continue;}
			total_reflect_percent += getReflectionVal(i);
		}

		return total_reflect_percent;
	}

	public static int generateTotalGoldFindChance(Player p){
		ItemStack[] armor_contents = p.getInventory().getArmorContents();
		int total_gold_find_percent = 0;
		for(ItemStack i : armor_contents){
			if(i.getType() == Material.AIR){continue;}
			total_gold_find_percent += getGoldFindPercent(i);
		}

		return total_gold_find_percent;
	}

	public static int generateTotalItemFindChance(Player p){
		ItemStack[] armor_contents = p.getInventory().getArmorContents();
		int total_item_find_percent = 0;
		for(ItemStack i : armor_contents){
			if(i.getType() == Material.AIR){continue;}
			total_item_find_percent += getItemFindPercent(i);
		}

		return total_item_find_percent;
	}

	public static int generateTotalStrVal(Player p){
		ItemStack[] armor_contents = p.getInventory().getArmorContents();
		int total_str_val = 0;
		for(ItemStack i : armor_contents){
			if(i.getType() == Material.AIR){continue;}
			total_str_val += getStrVal(i);
		}

		if(getStrVal(p.getItemInHand()) != 0){
			total_str_val += getStrVal(p.getItemInHand());
		}

		return total_str_val;
	}

	public static int generateTotalDexVal(Player p){
		ItemStack[] armor_contents = p.getInventory().getArmorContents();
		int total_dex_val = 0;
		for(ItemStack i : armor_contents){
			if(i.getType() == Material.AIR){continue;}
			total_dex_val += getDexVal(i);
		}

		if(getDexVal(p.getItemInHand()) != 0){
			total_dex_val += getDexVal(p.getItemInHand());
		}

		return total_dex_val;
	}

	public static int generateTotalVitVal(Player p){
		ItemStack[] armor_contents = p.getInventory().getArmorContents();
		int total_vit_val = 0;
		for(ItemStack i : armor_contents){
			if(i.getType() == Material.AIR){continue;}
			total_vit_val += getVitVal(i);
		}

		if(getVitVal(p.getItemInHand()) != 0){
			total_vit_val += getVitVal(p.getItemInHand());
		}

		return total_vit_val;
	}

	public static int generateTotalIntVal(Player p){
		ItemStack[] armor_contents = p.getInventory().getArmorContents();
		int total_int_val = 0;
		for(ItemStack i : armor_contents){
			if(i.getType() == Material.AIR){continue;}
			total_int_val += getIntVal(i);
		}

		if(getIntVal(p.getItemInHand()) != 0){
			total_int_val += getIntVal(p.getItemInHand());
		}

		return total_int_val;
	}

	public static int generateTotalFireRes(Player p){
		ItemStack[] armor_contents = p.getInventory().getArmorContents();
		int total_fire_res_val = 0;
		for(ItemStack i : armor_contents){
			if(i.getType() == Material.AIR){continue;}
			total_fire_res_val += getFireResistance(i);
		}

		return total_fire_res_val;
	}

	public static int generateTotalIceRes(Player p){
		ItemStack[] armor_contents = p.getInventory().getArmorContents();
		int total_ice_res_val = 0;
		for(ItemStack i : armor_contents){
			if(i.getType() == Material.AIR){continue;}
			total_ice_res_val += getIceResistance(i);
		}

		return total_ice_res_val;
	}

	public static int generateTotalPoisonRes(Player p){
		ItemStack[] armor_contents = p.getInventory().getArmorContents();
		int total_poison_res_val = 0;
		for(ItemStack i : armor_contents){
			if(i.getType() == Material.AIR){continue;}
			total_poison_res_val += getPoisonResistance(i);
		}

		return total_poison_res_val;
	}

	public static int getAccuracyVal(ItemStack is){
		if(is == null || is.getType() == Material.AIR){
			return 0;
		}

		String dmg_data = getDamageData(is);
		int accuracy_val = 0;

		if(dmg_data.equalsIgnoreCase("no")){
			return 0;
		}

		if(dmg_data.contains("accuracy=")){
			String accuracy_string = dmg_data.split("accuracy=")[1];
			accuracy_val = Integer.parseInt(accuracy_string.substring(0, accuracy_string.indexOf(":")));
		}

		return accuracy_val;
	}

	public static int getStrVal(ItemStack i){
		String armor_data = "";

		if(i == null || i.getType() == Material.AIR){
			return 0;
		}

		if(!getArmorData(i).equalsIgnoreCase("no")){
			armor_data = getArmorData(i);
		}
		else if (!getDamageData(i).equalsIgnoreCase("no")){
			armor_data = getDamageData(i);
		}

		int str_val = 0;
		if(armor_data.contains("str=")  && !armor_data.contains("str_raw=")){
			String str_string = armor_data.split("str=")[1];
			str_val = Integer.parseInt(str_string.substring(0, str_string.indexOf(":")));
		}
		else if(armor_data.contains("str_raw=")){
			String str_string = armor_data.split("str_raw=")[1];
			str_val = Integer.parseInt(str_string.substring(0, str_string.indexOf(":")));
		}

		return str_val;
	}

	public static int getDexVal(ItemStack i){
		String armor_data = "";

		if(i == null || i.getType() == Material.AIR){
			return 0;
		}

		if(!getArmorData(i).equalsIgnoreCase("no")){
			armor_data = getArmorData(i);
		}
		else if (!getDamageData(i).equalsIgnoreCase("no")){
			armor_data = getDamageData(i);
		}

		int dex_val = 0;
		if(armor_data.contains("dex=") && !armor_data.contains("dex_raw=")){
			String dex_string = armor_data.split("dex=")[1];
			dex_val = Integer.parseInt(dex_string.substring(0, dex_string.indexOf(":")));
		}
		else if(armor_data.contains("dex_raw=")){
			String dex_string = armor_data.split("dex_raw=")[1];
			dex_val = Integer.parseInt(dex_string.substring(0, dex_string.indexOf(":")));
		}

		return dex_val;
	}

	public static int getVitVal(ItemStack i){
		String armor_data = "";

		if(i == null || i.getType() == Material.AIR){
			return 0;
		}

		if(!getArmorData(i).equalsIgnoreCase("no")){
			armor_data = getArmorData(i);
		}
		else if (!getDamageData(i).equalsIgnoreCase("no")){
			armor_data = getDamageData(i);
		}

		int vit_val = 0;
		if(armor_data.contains("vit=") && !armor_data.contains("vit_raw=")){
			String vit_string = armor_data.split("vit=")[1];
			vit_val = Integer.parseInt(vit_string.substring(0, vit_string.indexOf(":")));
		}
		else if(armor_data.contains("vit_raw=")){
			String vit_string = armor_data.split("vit_raw=")[1];
			vit_val = Integer.parseInt(vit_string.substring(0, vit_string.indexOf(":")));
		}
		return vit_val;
	}

	public static int getIntVal(ItemStack i){
		String armor_data = "";

		if(i == null || i.getType() == Material.AIR){
			return 0;
		}

		if(!getArmorData(i).equalsIgnoreCase("no")){
			armor_data = getArmorData(i);
		}
		else if (!getDamageData(i).equalsIgnoreCase("no")){
			armor_data = getDamageData(i);
		}

		int int_val = 0;
		if(armor_data.contains("int=") && !armor_data.contains("int_raw=")){
			String int_string = armor_data.split("int=")[1];
			int_val = Integer.parseInt(int_string.substring(0, int_string.indexOf(":")));
		}
		else if(armor_data.contains("int_raw=")){
			String int_string = armor_data.split("int_raw=")[1];
			int_val = Integer.parseInt(int_string.substring(0, int_string.indexOf(":")));
		}
		return int_val;
	}

	public static int getFireResistance(ItemStack i){
		String armor_data = getArmorData(i);
		int fire_res_percent = 0;
		if(armor_data.contains("fire_resistance=")){
			String fire_res_string = armor_data.split("fire_resistance=")[1];
			fire_res_percent = Integer.parseInt(fire_res_string.substring(0, fire_res_string.indexOf(":")));
		}

		return fire_res_percent;
	}

	public static int getIceResistance(ItemStack i){
		String armor_data = getArmorData(i);
		int ice_res_percent = 0;
		if(armor_data.contains("ice_resistance=")){
			String ice_res_string = armor_data.split("ice_resistance=")[1];
			ice_res_percent = Integer.parseInt(ice_res_string.substring(0, ice_res_string.indexOf(":")));
		}

		return ice_res_percent;
	}

	public static int getPoisonResistance(ItemStack i){
		String armor_data = getArmorData(i);
		int poison_res_percent = 0;
		if(armor_data.contains("poison_resistance=")){
			String poison_res_string = armor_data.split("poison_resistance=")[1];
			poison_res_percent = Integer.parseInt(poison_res_string.substring(0, poison_res_string.indexOf(":")));
		}

		return poison_res_percent;
	}

	public static int getDodgeVal(ItemStack i){
		String armor_data = getArmorData(i);
		int dodge_percent = 0;
		if(armor_data.contains("dodge=")){
			String dodge_string = armor_data.split("dodge=")[1];
			dodge_percent = Integer.parseInt(dodge_string.substring(0, dodge_string.indexOf(":")));
		}

		if(armor_data.contains("dex")){
			String dex_string = armor_data.split("dex=")[1];
			int dex_atr =Integer.parseInt(dex_string.substring(0, dex_string.indexOf(":")));
			double dex_armor_mod = 0.03D * dex_atr;

			dodge_percent += dex_armor_mod;
		}

		return dodge_percent;
	}

	public static int getBlockVal(ItemStack i){
		String armor_data = getArmorData(i);
		int block_percent = 0;
		if(armor_data.contains("block=")){
			String block_string = armor_data.split("block=")[1];
			block_percent = Integer.parseInt(block_string.substring(0, block_string.indexOf(":")));
		}

		if(armor_data.contains("vit")){
			String vit_string = armor_data.split("vit=")[1];
			int vit_atr =Integer.parseInt(vit_string.substring(0, vit_string.indexOf(":")));
			double vit_armor_mod = 0.03D * vit_atr;

			block_percent += vit_armor_mod;
		}

		return block_percent;
	}

	public static int getThornVal(ItemStack i){
		String armor_data = getArmorData(i);
		int thorn_percent = 0;
		if(armor_data.contains("thorns=")){
			String block_string = armor_data.split("thorns=")[1];
			thorn_percent = Integer.parseInt(block_string.substring(0, block_string.indexOf(":")));
		}

		return thorn_percent;
	}

	public static int getGoldFindPercent(ItemStack i){
		String armor_data = getArmorData(i);
		int gold_find_percent = 0;
		if(armor_data.contains("gold_find=")){
			String gold_find_string = armor_data.split("gold_find=")[1];
			gold_find_percent = Integer.parseInt(gold_find_string.substring(0, gold_find_string.indexOf(":")));
		}

		return gold_find_percent;
	}

	public static int getItemFindPercent(ItemStack i){
		String armor_data = getArmorData(i);
		int Item_find_percent = 0;
		if(armor_data.contains("item_find=")){
			String Item_find_string = armor_data.split("item_find=")[1];
			Item_find_percent = Integer.parseInt(Item_find_string.substring(0, Item_find_string.indexOf(":")));
		}

		return Item_find_percent;
	}

	public static int getReflectionVal(ItemStack i){
		String armor_data = getArmorData(i);
		int reflection_percent = 0;
		if(armor_data.contains("reflection=")){
			String reflection_string = armor_data.split("reflection=")[1];
			reflection_percent = Integer.parseInt(reflection_string.substring(0, reflection_string.indexOf(":")));
		}

		return reflection_percent;
	}

	public static float getPlayerSpeed(Player p){
		String armor_data = getArmorData(p.getInventory().getItem(36));
		float speed_boost = 0.0F;
		if(armor_data.contains("speed=")){
			String speed_string = armor_data.split("speed=")[1];
			speed_boost = Float.parseFloat(speed_string.substring(0, speed_string.indexOf(":")));
		}

		return speed_boost + 0.2F;
	}

	public static List<Integer> getArmorVal(ItemStack i, boolean with_str){		
		try{	
			if(getArmorData(i) == "no"){
				return null;
			}
			String armor_data = getArmorData(i);
			if(armor_data.contains("!")){
				return null; // DMG %, not armor
			}
			String armor_range = armor_data.substring(0, armor_data.indexOf(":"));
			List<Integer> net_armor_vals = new ArrayList<Integer>();

			int min_armor = Integer.parseInt(armor_range.split("-")[0].replaceAll(" ", ""));
			int max_armor = Integer.parseInt(armor_range.split("-")[1].replaceAll(" ", ""));

			if(with_str && armor_data.contains("str")){
				String str_string = armor_data.split("str=")[1];
				int str_atr =Integer.parseInt(str_string.substring(0, str_string.indexOf(":")));
				double str_armor_mod = 0.01D * str_atr;

				min_armor += str_armor_mod;
				max_armor += str_armor_mod;
			}
			/*int armor_val = new Random().nextInt(max_armor);
		if(armor_val < min_armor){
			armor_val = min_armor;
		}
		if(armor_val < 1){armor_val = 1;}*/
			net_armor_vals.add(0, min_armor);
			net_armor_vals.add(1, max_armor);
			return net_armor_vals;
		} catch(NullPointerException e){
			return null;
		}
	}

	public static List<Integer> getDmgVal(ItemStack i, boolean with_str){		
		try{	
			if(getArmorData(i) == "no"){
				return null;
			}
			String armor_data = getArmorData(i);
			//log.info(armor_data);
			if(!armor_data.contains("!")){
				return null; // Armor, not DMG %.
			}
			String armor_range = armor_data.substring(0, armor_data.indexOf(":"));
			List<Integer> net_armor_vals = new ArrayList<Integer>();

			armor_range = armor_range.replaceAll("!", "");

			int min_armor = Integer.parseInt(armor_range.split("-")[0].replaceAll(" ", ""));
			int max_armor = Integer.parseInt(armor_range.split("-")[1].replaceAll(" ", ""));
			/*int armor_val = new Random().nextInt(max_armor);
			if(armor_val < min_armor){
				armor_val = min_armor;
			}
			if(armor_val < 1){armor_val = 1;}*/

			if(with_str && armor_data.contains("str")){
				String str_string = armor_data.split("str=")[1];
				int str_atr =Integer.parseInt(str_string.substring(0, str_string.indexOf(":")));
				double str_armor_mod = 0.01D * str_atr;

				min_armor += str_armor_mod;
				max_armor += str_armor_mod;
			}

			net_armor_vals.add(0, min_armor);
			net_armor_vals.add(1, max_armor);
			return net_armor_vals;
		} catch(NullPointerException e){
			return null;
		}
	}

	public List<Integer> getTotalArmorVal(Player p){
		return armor_data.get(p.getName());
	}

	public int calculateArmorVal(Player p){
		List<Integer> net_armor_vals = armor_data.get(p.getName());
		if(!(armor_data.containsKey(p.getName()))){
			return 0;
		}

		int min_armor = net_armor_vals.get(0);
		int max_armor = net_armor_vals.get(1);

		int random_armor = 0;

		if(max_armor - min_armor > 0){
			random_armor = new Random().nextInt(max_armor - min_armor) + min_armor;
		}
		else if(max_armor == min_armor){
			random_armor = max_armor;
		}

		return random_armor;
	}

	public int getMaxHP(Player p){
		if(!HealthMechanics.health_data.containsKey(p.getName())){
			return 100;
		}
		return HealthMechanics.health_data.get(p.getName());
	}

	public static boolean isArmor(ItemStack i){
		if(getArmorData(i).equalsIgnoreCase("no")){
			return false;
		}
		else if(getArmorData(i).contains("@:")){
			return true;
		}
		return false;
	}

	public static boolean isWeapon(ItemStack i){
		if(getDamageData(i).equalsIgnoreCase("no")){
			return false;
		}
		else if(getDamageData(i).contains(":")){
			return true;
		}
		return false;
	}


	public static boolean isSlotEmpty(Player p, Material m){
		Inventory i = p.getInventory();
		if(m == Material.LEATHER_HELMET || m == Material.CHAINMAIL_HELMET || m == Material.IRON_HELMET || m == Material.DIAMOND_HELMET  || m == Material.GOLD_HELMET){
			if(i.getItem(39) == null){
				return true;
			}
		}

		if(m == Material.LEATHER_BOOTS || m == Material.CHAINMAIL_BOOTS || m == Material.IRON_BOOTS || m == Material.DIAMOND_BOOTS  || m == Material.GOLD_BOOTS){
			if(i.getItem(36) == null){
				return true;
			}
		}

		if(m == Material.LEATHER_CHESTPLATE || m == Material.CHAINMAIL_CHESTPLATE || m == Material.IRON_CHESTPLATE || m == Material.DIAMOND_CHESTPLATE  || m == Material.GOLD_CHESTPLATE){
			if(i.getItem(38) == null){
				return true;
			}
		}

		if(m == Material.LEATHER_LEGGINGS || m == Material.CHAINMAIL_LEGGINGS || m == Material.IRON_LEGGINGS || m == Material.DIAMOND_LEGGINGS  || m == Material.GOLD_LEGGINGS){
			if(i.getItem(37) == null){
				return true;
			}
		}

		return false;
	}

	public static int getRespectiveArmorSlot(Material m){
		if(m == Material.JACK_O_LANTERN || m == Material.LEATHER_HELMET || m == Material.CHAINMAIL_HELMET || m == Material.IRON_HELMET || m == Material.DIAMOND_HELMET  || m == Material.GOLD_HELMET || m == Material.PUMPKIN || m == Material.JACK_O_LANTERN){
			return 39;
		}

		if(m == Material.LEATHER_BOOTS || m == Material.CHAINMAIL_BOOTS || m == Material.IRON_BOOTS || m == Material.DIAMOND_BOOTS  || m == Material.GOLD_BOOTS){
			return 36;
		}

		if(m == Material.LEATHER_CHESTPLATE || m == Material.CHAINMAIL_CHESTPLATE || m == Material.IRON_CHESTPLATE || m == Material.DIAMOND_CHESTPLATE  || m == Material.GOLD_CHESTPLATE){
			return 38;
		}

		if(m == Material.LEATHER_LEGGINGS || m == Material.CHAINMAIL_LEGGINGS || m == Material.IRON_LEGGINGS || m == Material.DIAMOND_LEGGINGS  || m == Material.GOLD_LEGGINGS){
			return 37;
		}

		return 0;
	}

	public static String generateItemRarity(ItemStack is){
		if(is == null || !is.hasItemMeta() || !is.getItemMeta().hasLore()){
			return null;
		}

		if(ProfessionMechanics.isSkillItem(is)){
			return null;
		}

		if(!getDamageData(is).equalsIgnoreCase("no")){
			// Ok, so we're dealing with a weapon, think damage ranges.
			// Wands = dmg / 2
			// Polearms = dmg / 3

			String rarity = ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + "Common"; // Default to common.

			List<Integer> dmg_range = getDmgRangeOfWeapon(is);
			double min_dmg = dmg_range.get(0);
			double max_dmg = dmg_range.get(1);

			String mat_name = is.getType().name().toLowerCase();

			int enchant_count = EnchantMechanics.getEnchantCount(is);
			while(enchant_count > 0){
				enchant_count--;
				min_dmg = min_dmg * 0.95;
				max_dmg = max_dmg * 0.95;
			}

			if(mat_name.contains("hoe")){
				min_dmg = min_dmg * 2;
				max_dmg = max_dmg * 2;
			}

			if(mat_name.contains("spade")){
				min_dmg = min_dmg * 3;
				max_dmg = max_dmg * 3;
			}

			int tier = getItemTier(is);

			if(tier == 1){
				if(max_dmg > 3){
					rarity = ChatColor.GREEN.toString() + ChatColor.ITALIC.toString() + "Uncommon";
				}
				if(max_dmg > 6){
					rarity = ChatColor.AQUA.toString() + ChatColor.ITALIC.toString() + "Rare";
				}
				if(max_dmg >= 20){
					rarity = ChatColor.YELLOW.toString() + ChatColor.ITALIC.toString() + "Unique";
				}
			}

			if(tier == 2){
				if(max_dmg > 15){
					rarity = ChatColor.GREEN.toString() + ChatColor.ITALIC.toString() + "Uncommon";
				}
				if(max_dmg > 22){
					rarity = ChatColor.AQUA.toString() + ChatColor.ITALIC.toString() + "Rare";
				}
				if(max_dmg >= 55){
					rarity = ChatColor.YELLOW.toString() + ChatColor.ITALIC.toString() + "Unique";
				}
			}

			if(tier == 3){
				if(max_dmg > 40){
					rarity = ChatColor.GREEN.toString() + ChatColor.ITALIC.toString() + "Uncommon";
				}
				if(max_dmg > 65){
					rarity = ChatColor.AQUA.toString() + ChatColor.ITALIC.toString() + "Rare";
				}
				if(max_dmg >= 120){
					rarity = ChatColor.YELLOW.toString() + ChatColor.ITALIC.toString() + "Unique";
				}
			}

			if(tier == 4){
				if(max_dmg > 110){
					rarity = ChatColor.GREEN.toString() + ChatColor.ITALIC.toString() + "Uncommon";
				}
				if(max_dmg > 140){
					rarity = ChatColor.AQUA.toString() + ChatColor.ITALIC.toString() + "Rare";
				}
				if(max_dmg >= 210){
					rarity = ChatColor.YELLOW.toString() + ChatColor.ITALIC.toString() + "Unique";
				}
			}

			if(tier == 5){
				if(max_dmg > 200){
					rarity = ChatColor.GREEN.toString() + ChatColor.ITALIC.toString() + "Uncommon";
				}
				if(max_dmg > 250){
					rarity = ChatColor.AQUA.toString() + ChatColor.ITALIC.toString() + "Rare";
				}
				if(max_dmg >= 337){
					rarity = ChatColor.YELLOW.toString() + ChatColor.ITALIC.toString() + "Unique";
				}
			}

			return rarity;
		}
		else if(!getArmorData(is).equalsIgnoreCase("no")){
			// Armor. Helmet / 2, Boots / 2
			// Base it upon +HP

			String rarity = ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + "Common"; // Default to common.
			double health_val = HealthMechanics.getHealthVal(is);

			String mat_name = is.getType().name().toLowerCase();

			int enchant_count = EnchantMechanics.getEnchantCount(is);
			while(enchant_count > 0){
				enchant_count--;
				health_val = health_val * 0.95;
			}


			if(mat_name.contains("boots")){
				health_val = health_val * 2;
			}

			if(mat_name.contains("helmet")){
				health_val = health_val * 2;
			}

			int tier = getItemTier(is);

			if(tier == 1){
				if(health_val > 20){
					rarity = ChatColor.GREEN.toString() + ChatColor.ITALIC.toString() + "Uncommon";
				}
				if(health_val > 50){
					rarity = ChatColor.AQUA.toString() + ChatColor.ITALIC.toString() + "Rare";
				}
				if(health_val >= 120){
					rarity = ChatColor.YELLOW.toString() + ChatColor.ITALIC.toString() + "Unique";
				}
			}

			if(tier == 2){
				if(health_val > 80){
					rarity = ChatColor.GREEN.toString() + ChatColor.ITALIC.toString() + "Uncommon";
				}
				if(health_val > 250){
					rarity = ChatColor.AQUA.toString() + ChatColor.ITALIC.toString() + "Rare";
				}
				if(health_val >= 350){
					rarity = ChatColor.YELLOW.toString() + ChatColor.ITALIC.toString() + "Unique";
				}
			}

			if(tier == 3){
				if(health_val > 350){
					rarity = ChatColor.GREEN.toString() + ChatColor.ITALIC.toString() + "Uncommon";
				}
				if(health_val > 600){
					rarity = ChatColor.AQUA.toString() + ChatColor.ITALIC.toString() + "Rare";
				}
				if(health_val >= 800){
					rarity = ChatColor.YELLOW.toString() + ChatColor.ITALIC.toString() + "Unique";
				}
			}

			if(tier == 4){
				if(health_val > 800){
					rarity = ChatColor.GREEN.toString() + ChatColor.ITALIC.toString() + "Uncommon";
				}
				if(health_val > 1300){
					rarity = ChatColor.AQUA.toString() + ChatColor.ITALIC.toString() + "Rare";
				}
				if(health_val >= 2400){
					rarity = ChatColor.YELLOW.toString() + ChatColor.ITALIC.toString() + "Unique";
				}
			}

			if(tier == 5){
				if(health_val > 2500){
					rarity = ChatColor.GREEN.toString() + ChatColor.ITALIC.toString() + "Uncommon";
				}
				if(health_val > 3600){
					rarity = ChatColor.AQUA.toString() + ChatColor.ITALIC.toString() + "Rare";
				}
				if(health_val >= 5400){
					rarity = ChatColor.YELLOW.toString() + ChatColor.ITALIC.toString() + "Unique";
				}
			}
			return rarity;
		}

		return null; // Not an armor, not a weapon.
	}

	public static String getItemRarity(ItemStack is){
		if(is == null || !is.hasItemMeta() || !is.getItemMeta().hasLore()){
			return null;
		}

		List<String> lore = is.getItemMeta().getLore();
		for(String s : lore){
			if(s.contains(ChatColor.ITALIC.toString())){
				if(s.equalsIgnoreCase(ChatColor.YELLOW.toString() + ChatColor.ITALIC.toString() + "Unique")){
					return s;
				}
				if(s.equalsIgnoreCase(ChatColor.AQUA.toString() + ChatColor.ITALIC.toString() + "Rare")){
					return s;
				}
				if(s.equalsIgnoreCase(ChatColor.GREEN.toString() + ChatColor.ITALIC.toString() + "Uncommon")){
					return s;
				}
				if(s.equalsIgnoreCase(ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + "Common")){
					return s;
				}
			}
		}

		if(!ProfessionMechanics.isSkillItem(is) && (!getDamageData(is).equalsIgnoreCase("no") || !getArmorData(is).equalsIgnoreCase("no"))){
			// It's weapon/armor, should have rarity, must be pre-patch!
			return "no_rarity";
		}

		return ""; // No rarity line on item.
	}

	public static void addRarityToOldItems(Player pl){
		//int index = -1;
		for(ItemStack is : pl.getInventory().getContents()){
			//index++;
			if(is == null){
				continue;
			}

			if(getItemRarity(is) != null && getItemRarity(is).equalsIgnoreCase("no_rarity")){
				// This item needs some rarity!
				String rarity = generateItemRarity(is);
				List<String> lore = is.getItemMeta().getLore();
				List<String> new_lore = new ArrayList<String>();

				for(String s : lore){
					if(s.startsWith(ChatColor.RED.toString()) || s.startsWith(ChatColor.GRAY.toString())){
						new_lore.add(s);
						continue;
					}
					// Not a stat, not a description, inject rarity now.
					if(!(new_lore.contains(rarity))){
						new_lore.add(rarity);
					}
					new_lore.add(s);
				}

				if(!(new_lore.contains(rarity))){
					new_lore.add(rarity);
				}

				ItemMeta im = is.getItemMeta();
				im.setLore(new_lore);
				is.setItemMeta(im);
				//p.setItem(index, is);
			}
		}

		for(ItemStack is : pl.getInventory().getArmorContents()){
			if(is == null){
				continue;
			}

			if(getItemRarity(is) != null && getItemRarity(is).equalsIgnoreCase("no_rarity")){
				// This item needs some rarity!
				String rarity = generateItemRarity(is);
				List<String> lore = is.getItemMeta().getLore();
				List<String> new_lore = new ArrayList<String>();

				for(String s : lore){
					if(s.startsWith(ChatColor.RED.toString()) || s.startsWith(ChatColor.GRAY.toString())){
						new_lore.add(s);
						continue;
					}
					// Not a stat, not a description, inject rarity now.
					if(!(new_lore.contains(rarity))){
						new_lore.add(rarity);
					}
					new_lore.add(s);
				}

				if(!(new_lore.contains(rarity))){
					new_lore.add(rarity);
				}

				ItemMeta im = is.getItemMeta();
				im.setLore(new_lore);
				is.setItemMeta(im);

				String mat_name = is.getType().name().toLowerCase();

				if(mat_name.contains("helmet")){
					pl.getInventory().setHelmet(is);
					continue;
				}
				if(mat_name.contains("chest")){
					pl.getInventory().setChestplate(is);
					continue;
				}
				if(mat_name.contains("leg")){
					pl.getInventory().setLeggings(is);
					continue;
				}
				if(mat_name.contains("boots")){
					pl.getInventory().setBoots(is);
					continue;
				}
			}	
		}

	}

	public static void addRarityToOldItems(Inventory inv){
		int index = -1;
		for(ItemStack is : inv){
			index++;
			if(is == null){
				continue;
			}

			if(getItemRarity(is) != null && getItemRarity(is).equalsIgnoreCase("no_rarity")){
				// This item needs some rarity!
				String rarity = generateItemRarity(is);
				List<String> lore = is.getItemMeta().getLore();
				List<String> new_lore = new ArrayList<String>();

				for(String s : lore){
					if(s.startsWith(ChatColor.RED.toString()) || s.startsWith(ChatColor.GRAY.toString())){
						new_lore.add(s);
						continue;
					}
					// Not a stat, not a description, inject rarity now.
					if(!(new_lore.contains(rarity))){
						new_lore.add(rarity);
					}
					new_lore.add(s);
				}

				if(!(new_lore.contains(rarity))){
					new_lore.add(rarity);
				}

				ItemMeta im = is.getItemMeta();
				im.setLore(new_lore);
				is.setItemMeta(im);
				//
				inv.setItem(index, is);
			}
		}
	}

	public void fixBuggedDurability(Inventory inv){
		for(ItemStack is : inv.getContents()){
			if(is == null){
				continue;
			}
			if(isArmor(is) || isWeapon(is) || ProfessionMechanics.isSkillItem(is)){
				if(is.getDurability() < -1){
					is.setDurability((short)0);
				}
			}
		}
	}

	public boolean removeMagmaCream(Inventory inv){
		int index = -1;
		boolean found_one = false; 
		for(ItemStack is : inv.getContents()){
			index++;
			if(is == null){
				continue;
			}
			if(is.getType() == Material.MAGMA_CREAM && !ItemMechanics.isOrbOfAlteration(is)){
				inv.setItem(index, new ItemStack(Material.AIR));
				found_one = true;
			}
		}
		return found_one;
	}

	@EventHandler
	public void onPlayerOpenInventory(InventoryOpenEvent e){
		if(e.getInventory().getName().contains("Realm Material Store")){
			return;
		}
		if(e.getInventory().contains(Material.ARROW)){
			convertVanillaArrows(e.getInventory());
		}
		addRarityToOldItems(e.getInventory());
		fixBuggedDurability(e.getInventory());
		if(removeMagmaCream(e.getInventory())){
			Player pl = (Player)e.getPlayer();
			pl.sendMessage(ChatColor.RED + "You had an illegal item in your inventory (MAGMA_CREAM) -- your account has been flagged.");
			pl.sendMessage(ChatColor.GRAY + "Send an e-mail to staff@dungeonrealms.net within 24 hours to avoid your account being locked.");
			log.info("(FLAG) Player " + pl.getName() + " had MAGMA_CREAM, removed and warned.");
		}
		removeAttributes(e.getInventory());
	}

	//@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerAnimation(PlayerAnimationEvent e){
		Player pl = e.getPlayer();
		ItemStack is = pl.getItemInHand();
		if(is == null){
			return;
		}
		net.minecraft.server.v1_7_R1.ItemStack nms = CraftItemStack.asNMSCopy(is);
		try{
			if(pl != null && nms != null && is != null && nms.hasTag() && is.getMaxStackSize() == 1){
				pl.setItemInHand(removeAttributes(is));
			}
		} catch(NullPointerException npe){
			return;
		}
	}

	public static void removeAttributes(Inventory inv){
		int index = -1;
		for(final ItemStack is : inv.getContents()){
			index++;

			if(is == null){
				continue;
			}

			final net.minecraft.server.v1_7_R1.ItemStack nms = CraftItemStack.asNMSCopy(is);

			if(nms != null && nms.hasTag() && is.getMaxStackSize() == 1){
				Attributes attributes = new Attributes(is);
				attributes.clear();
				inv.setItem(index, attributes.getStack());
			}
			else if(nms != null && nms.hasTag()){
				NBTTagCompound nc = nms.getTag();
				nc.remove("AttributeName");
				nc.remove("AttributeModifiers");
				nms.setTag(nc);
				inv.setItem(index, CraftItemStack.asBukkitCopy(nms));
			}
		}
	}

	public static ItemStack removeAttributes(ItemStack is){
		if(is == null){
			return null;
		}

		final net.minecraft.server.v1_7_R1.ItemStack nms = CraftItemStack.asNMSCopy(is);

		if(nms != null && nms.hasTag() && is.getMaxStackSize() == 1){ //  && nms.getTag().hasKey("AttributeName")
			Attributes attributes = new Attributes(is);
			attributes.clear();
			return attributes.getStack();
		}

		return is; // No tag.
	}

	public static List<Integer> getDmgRangeOfWeapon(ItemStack is){
		List<Integer> dmg_range = new ArrayList<Integer>();
		if(is != null && is.hasItemMeta() && is.getItemMeta().hasLore()){
			List<String> lore = is.getItemMeta().getLore();
			for(String s : lore){
				if(s.startsWith(red.toString() + "DMG:")){
					s = s.replaceAll(red.toString() + "DMG: ", "");
					s = s.replaceAll(" ", "");

					int min_dmg = Integer.parseInt(s.split("-")[0]);
					int max_dmg = Integer.parseInt(s.split("-")[1]);

					dmg_range.add(min_dmg);
					dmg_range.add(max_dmg);
				}
			}
		}
		
		return dmg_range;
		
		
		/*List<Integer> dmg_range = new ArrayList<Integer>();
		NBTTagList description = CraftItemStack.asNMSCopy(is).getTag().getCompound("display").getList("Lore", 0);

		String dmg_data = description.get(0).toString();
		dmg_data = dmg_data.replaceAll(red.toString() + "DMG: ", "");

		dmg_data = dmg_data.replaceAll(" ", "");

		int min_dmg = Integer.parseInt(dmg_data.split("-")[0]);
		int max_dmg = Integer.parseInt(dmg_data.split("-")[1]);

		dmg_range.add(min_dmg);
		dmg_range.add(max_dmg);
		return dmg_range;*/
	}

	@EventHandler
	public void onPlayerItemHeld(PlayerItemHeldEvent e){
		final Player p = e.getPlayer(); // volumn, pitch
		if(p.getInventory().getItem(e.getNewSlot()) == null){
			return;
		}

		Main.plugin.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
			public void run() {
				if(!(to_process_weapon.contains(p.getName()))){
					to_process_weapon.add(p.getName());
				}
			}
		}, 2L);

		ItemStack i = p.getInventory().getItem(e.getNewSlot());
		if(i.getType() == Material.BOW){
			p.playSound(p.getLocation(), Sound.ITEM_BREAK, 1.0F, 1.4F);
		}
		if(i.getType() == Material.WOOD_SWORD || i.getType() == Material.STONE_SWORD || i.getType() == Material.IRON_SWORD || i.getType() == Material.DIAMOND_SWORD || i.getType() == Material.GOLD_SWORD){
			p.playSound(p.getLocation(), Sound.ITEM_BREAK, 1.0F, 1.4F);
		}
		if(i.getType() == Material.WOOD_AXE || i.getType() == Material.STONE_AXE || i.getType() == Material.IRON_AXE || i.getType() == Material.DIAMOND_AXE || i.getType() == Material.GOLD_AXE){
			p.playSound(p.getLocation(), Sound.ITEM_BREAK, 1.0F, 1.4F);
		}
		if(i.getType().name().toLowerCase().contains("spade")){
			p.playSound(p.getLocation(), Sound.ITEM_BREAK, 1.0F, 1.4F);
		}
		if(i.getType().name().toLowerCase().contains("hoe")){
			p.playSound(p.getLocation(), Sound.ITEM_BREAK, 1.0F, 1.4F);
		}

	}

	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerJoin(PlayerJoinEvent e){
		Player p = e.getPlayer();

		/*if(p.getInventory().firstEmpty() != -1){
			p.getInventory().addItem(ItemGenerators.BootGenerator(5, false, null));
		}
		if(p.getInventory().firstEmpty() != -1){
			p.getInventory().addItem(ItemGenerators.LeggingsGenerator(5, false, null));
		}
		if(p.getInventory().firstEmpty() != -1){
			p.getInventory().addItem(ItemGenerators.ChestPlateGenerator(5, false, null));
		}
		if(p.getInventory().firstEmpty() != -1){
			p.getInventory().addItem(ItemGenerators.HelmetGenerator(5, false, null));
		}
		if(p.getInventory().firstEmpty() != -1){
			p.getInventory().addItem(ItemGenerators.SwordGenorator(Material.GOLD_SWORD, false, null));
		}*/

		convertVanillaArrows(p.getInventory());
		fixBuggedDurability(p.getInventory());
		addRarityToOldItems(p);

		if(removeMagmaCream(p.getInventory())){
			p.sendMessage(ChatColor.RED + "You had an illegal item in your inventory (MAGMA_CREAM) -- your account has been flagged.");
			p.sendMessage(ChatColor.GRAY + "Send an e-mail to staff@dungeonrealms.net within 24 hours to avoid your account being locked.");
			log.info("(FLAG) Player " + p.getName() + " had MAGMA_CREAM, removed and warned.");
		}

		removeAttributes(p.getInventory());
		p.updateInventory();

		armor_data.put(p.getName(), generateTotalArmorVal(p));
		dmg_data.put(p.getName(), generateTotalDmgVal(p));

		dodge_data.put(p.getName(), generateTotalDodgeChance(p));
		block_data.put(p.getName(), generateTotalBlockChance(p));
		thorn_data.put(p.getName(), generateTotalThornVal(p));
		reflection_data.put(p.getName(), generateTotalReflectChance(p));

		str_data.put(p.getName(), generateTotalStrVal(p));
		dex_data.put(p.getName(), generateTotalDexVal(p));
		vit_data.put(p.getName(), generateTotalVitVal(p));
		int_data.put(p.getName(), generateTotalIntVal(p));

		fire_res_data.put(p.getName(), generateTotalFireRes(p));
		ice_res_data.put(p.getName(), generateTotalIceRes(p));
		poison_res_data.put(p.getName(), generateTotalPoisonRes(p));

		gfind_data.put(p.getName(), generateTotalGoldFindChance(p));
		ifind_data.put(p.getName(), generateTotalItemFindChance(p));

		p.setWalkSpeed(getPlayerSpeed(p));

		FatigueMechanics.updateEnergyRegenData(p, false);
		HealthMechanics.generateHealthRegenAmount(p, false);
	}

	public boolean isEasterEgg(ItemStack is){
		if(is.getType() == Material.EGG && is.getDurability() == (short)1 && is.hasItemMeta() && is.getItemMeta().hasDisplayName() && ChatColor.stripColor(is.getItemMeta().getDisplayName()).contains("Easter Egg")){
			return true;
		}
		return false;
	}

	@EventHandler
	public void onPlayerEatEasterEgg(PlayerInteractEvent e){
		if(e.hasItem() && isEasterEgg(e.getItem()) && (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK)){
			Player pl = e.getPlayer();
			e.setCancelled(true);

			if(pl.getItemInHand().getAmount() > 1){
				int amount = pl.getItemInHand().getAmount();
				amount -= 1;
				ItemStack is = pl.getItemInHand();
				is.setAmount(amount);
				pl.setItemInHand(is);
			}
			else if(pl.getItemInHand().getAmount() == 1){
				pl.setItemInHand(new ItemStack(Material.AIR));
			}

			pl.kickPlayer("Illegal Action - Account Flagged!");
			long unban_date = (System.currentTimeMillis() + (1000 * (72 * 3600)));
			String reason = "[AUTO] Illegal Item";
			ModerationMechanics.BanPlayer(pl.getName(), unban_date, reason, "Console", false);
			/*pl.setLevel(HealthMechanics.getMaxHealthValue(pl.getName()));
			pl.setHealth(20);
			pl.getWorld().spawnParticle(pl.getLocation().add(0, 2, 0), Particle.HEART, 0.35F, 10);
			pl.playSound(pl.getLocation(), Sound.EAT, 1F, 1F);
			pl.playSound(pl.getLocation(), Sound.LEVEL_UP, 1F, 1F);
			pl.updateInventory();*/
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onPlayerAttemptBowFire(PlayerInteractEvent e){
		if(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK){
			Player p = e.getPlayer();
			if(p.getItemInHand().getType() == Material.BOW){
				int bow_tier = getItemTier(p.getItemInHand());
				if(!doesPlayerHaveArrows(p, bow_tier)){
					p.playSound(p.getLocation(), Sound.IRONGOLEM_HIT, 0.3F, 2.0F);
					e.setCancelled(true);
					p.updateInventory();
				}
			}
			if(p.getItemInHand().getType() == Material.MAGMA_CREAM){
				p.sendMessage(ChatColor.RED + "To use an " + ChatColor.BOLD + "ORB OF ALTERATION" + ChatColor.RED + ", simply drag it ontop of the piece of equipment you wish to apply it to in your inventory.");
			}
		}
	}

	public void onArrowPickup(PlayerPickupItemEvent event){
		if(event.getItem() instanceof Arrow){
			event.setCancelled(true);
			event.getItem().remove();
		}
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onPlayerPickupItem(PlayerPickupItemEvent e){
		final ItemStack is = e.getItem().getItemStack();

		if(is.hasItemMeta() && is.getMaxStackSize() == 1){
			e.getItem().remove();
			e.setCancelled(true);
			e.getPlayer().getInventory().addItem(removeAttributes(is));
			e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.ITEM_PICKUP, 1F, 1F);
		}
	}

	public static boolean isOrbOfAlteration(ItemStack is){
		if(is.getType() == Material.MAGMA_CREAM && is.hasItemMeta() && is.getItemMeta().hasDisplayName() && is.getItemMeta().getDisplayName().toLowerCase().contains("orb of alteration")){
			return true;
		}
		return false;
	}

	public static void noKnockback(final LivingEntity le, final Location epicenter){
		Main.plugin.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable(){
			public void run() {
				le.setVelocity(new Vector());

				// Get velocity unit vector:
				org.bukkit.util.Vector unitVector = le.getLocation().toVector().subtract(le.getLocation().toVector()).normalize();
				// Set speed and push entity:

				if(epicenter != null){
					unitVector = le.getLocation().toVector().subtract(epicenter.toVector()).normalize();

					le.setVelocity(unitVector.multiply(0.10D));
				}

			}
		}, 1L);
	}

	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onOrbUse(InventoryClickEvent e){
		Player p = (Player)e.getWhoClicked();

		if(e.getCursor() == null){return;}
		if(e.getCurrentItem() == null){return;}
		ItemStack cursor = e.getCursor();
		ItemStack in_slot = e.getCurrentItem();

		if(!e.getInventory().getName().equalsIgnoreCase("container.crafting")){return;}
		if(e.getInventory().getViewers().size() > 1){return;}
		if(e.getSlotType() == SlotType.ARMOR){return;}


		if(isOrbOfAlteration(cursor) && (isArmor(in_slot) || !getDamageData(in_slot).equalsIgnoreCase("no"))){

			if(last_orb_use.containsKey(p.getName())){
				if((System.currentTimeMillis() - last_orb_use.get(p.getName())) < (1 * 500)){
					e.setCancelled(true);
					p.updateInventory();
					return;
				}
			}

			last_orb_use.put(p.getName(), System.currentTimeMillis());

			if(cursor.getAmount() == 1){
				e.setCancelled(true);
				e.setCursor(new ItemStack(Material.AIR));			
			}
			else if(cursor.getAmount() > 1){
				e.setCancelled(true);
				cursor.setAmount(cursor.getAmount() - 1);
				e.setCursor(cursor);
			}

			ItemStack in_slot_c = CraftItemStack.asCraftCopy(in_slot);

			if(in_slot_c.getType().name().toLowerCase().contains("axe")){
				e.setCurrentItem(ItemGenerators.AxeGenorator(in_slot_c.getType(), true, in_slot_c));
			}
			else if(in_slot_c.getType().name().toLowerCase().contains("sword")){
				e.setCurrentItem(ItemGenerators.SwordGenorator(in_slot_c.getType(), true, in_slot_c));
			}
			else if(in_slot_c.getType().name().toLowerCase().contains("spade")){
				e.setCurrentItem(ItemGenerators.PolearmGenorator(in_slot_c.getType(), true, in_slot_c));
			}
			else if(in_slot_c.getType().name().toLowerCase().contains("hoe")){
				e.setCurrentItem(ItemGenerators.StaffGenorator(in_slot_c.getType(), true, in_slot_c));
			}
			else if(in_slot_c.getType().name().toLowerCase().contains("bow")){
				e.setCurrentItem(ItemGenerators.BowGenorator(getItemTier(in_slot_c), true, in_slot_c));
			}
			else if(in_slot_c.getType().name().toLowerCase().contains("helmet")){
				e.setCurrentItem(ItemGenerators.HelmetGenerator(getItemTier(in_slot_c), true, in_slot_c));
			}
			else if(in_slot_c.getType().name().toLowerCase().contains("chest")){
				e.setCurrentItem(ItemGenerators.ChestPlateGenerator(getItemTier(in_slot_c), true, in_slot_c));
			}
			else if(in_slot_c.getType().name().toLowerCase().contains("leg")){
				//log.info("[IM] Leg generator launched.");
				e.setCurrentItem(ItemGenerators.LeggingsGenerator(getItemTier(in_slot_c), true, in_slot_c));
			}
			else if(in_slot_c.getType().name().toLowerCase().contains("boot")){
				e.setCurrentItem(ItemGenerators.BootGenerator(getItemTier(in_slot_c), true, in_slot_c));
			}

			ItemStack new_in_slot = e.getCurrentItem();

			if(in_slot_c.hasItemMeta() && in_slot_c.getItemMeta().hasDisplayName()){
				// Copy name if name is custom
				String name = in_slot_c.getItemMeta().getDisplayName();
				if(name.contains(ChatColor.ITALIC.toString()) && name.contains("EC")){ // Custom E-CASH name.
					ItemMeta im = new_in_slot.getItemMeta();
					im.setDisplayName(name); // Set it to the old name.
					new_in_slot.setItemMeta(im);
				}
			}

			p.updateInventory();

			if(in_slot.getItemMeta().getLore().size() < new_in_slot.getItemMeta().getLore().size()){
				p.getWorld().playSound(p.getLocation(), Sound.LEVEL_UP, 1.0F, 1.25F);
				try {
					ParticleEffect.sendToLocation(ParticleEffect.FIREWORKS_SPARK, p.getLocation().add(0, 2.5, 0), new Random().nextFloat(), new Random().nextFloat(), new Random().nextFloat(), 0.75F, 100);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				//p.getWorld().spawnParticle(p.getLocation().add(0, 8, 0), Particle.FIREWORKS_SPARK, 0.75F, 100);
				Firework fw = (Firework) p.getWorld().spawnEntity(p.getLocation(), EntityType.FIREWORK);
				FireworkMeta fwm = fw.getFireworkMeta();
				//Random r = new Random();   // TODO - UNUSED
				FireworkEffect effect = FireworkEffect.builder().flicker(false).withColor(Color.YELLOW).withFade(Color.YELLOW).with(Type.BURST).trail(true).build();
				fwm.addEffect(effect);
				fwm.setPower(0);
				fw.setFireworkMeta(fwm); 
			}
			else{
				// FAIL. Same or worse.
				p.getWorld().playSound(p.getLocation(), Sound.FIZZ, 2.0F, 1.25F);
				try {
					ParticleEffect.sendToLocation(ParticleEffect.LAVA, p.getLocation().add(0, 2.5, 0), new Random().nextFloat(), new Random().nextFloat(), new Random().nextFloat(), 1F, 75);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				//p.getWorld().spawnParticle(p.getLocation().add(0, 2.5, 0), Particle.LAVA, 1F, 75);
			}

			if(EnchantMechanics.getEnchantCount(in_slot) >= 4){
				// Glowing effect.
				EnchantMechanics.addGlow(in_slot);
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = false) // was high
	public void ArmorEvent(EntityDamageEvent e){
		Entity ent = e.getEntity();
		if(ent.getPassenger() != null){
			ent = ent.getPassenger();
		}
		if(!(ent.getType() == EntityType.PLAYER)){return;}
		Player p = (Player)ent;
		ItemStack wep = null;

		boolean block = false, dodge = false, reflection = false;
		double damage = e.getDamage();

		if(e.getCause() == DamageCause.ENTITY_ATTACK || e.getCause() == DamageCause.PROJECTILE){	
			Entity e_attacker = ((EntityDamageByEntityEvent)e).getDamager();
			if(e_attacker instanceof Projectile){
				if(e_attacker instanceof WitherSkull){
					e.setCancelled(true);
					e.setDamage(0);
					return;
				}
				// Set variables, change e_attacker to the entity that shot the arrow.
				EntityLiving el = ((CraftLivingEntity) ((Projectile)e_attacker).getShooter()).getHandle();
				e_attacker = (Entity) ((Projectile)e_attacker).getShooter();
				wep = (ItemStack)CraftItemStack.asCraftMirror(el.getEquipment(0));
			}
			if(e_attacker instanceof Player){
				Player p_attacker = (Player)e_attacker;
				wep = p_attacker.getItemInHand();

				// So the issue with this right now is that after 1 fireball hits from a barrage, it removes the data and
				// the others can be hotswapped with other weapons.
				if(processing_proj_event.contains(p_attacker.getName())){
					wep = spoofed_weapon.get(p_attacker.getName());
					//spoofed_weapon.remove(p_attacker.getName());
				}

				if(DuelMechanics.isPvPDisabled(p)){
					if(!(DuelMechanics.duel_map.containsKey(p.getName())) || !(DuelMechanics.duel_map.get(p.getName()).equalsIgnoreCase(p_attacker.getName()))){
						e.setCancelled(true);
						e.setDamage((double)0);
						return; // Neither of the players are in a duel.
					}

					if(DuelMechanics.duel_countdown.containsKey(p_attacker) || DuelMechanics.duel_countdown.containsKey(p)){
						e.setCancelled(true);
						e.setDamage((double)0);
						return; // It's cooldowning.
					}
				}
			}
			else if(e_attacker instanceof LivingEntity && !(e_attacker instanceof Arrow) && !(e_attacker instanceof Player)){
				// Monster attacking, store weapon -- this shouldn't ever get cancelled if the event has reached this point.
				EntityLiving el = ((CraftLivingEntity) e_attacker).getHandle();
				wep = (ItemStack)CraftItemStack.asCraftMirror(el.getEquipment(0));
			}

			if(p.isBlocking() && !(wep == null || wep.getType() == Material.WOOD_AXE || wep.getType() == Material.STONE_AXE || wep.getType() == Material.IRON_AXE || wep.getType() == Material.DIAMOND_AXE || wep.getType() == Material.GOLD_AXE)){
				// 80% chance to block 50% of incoming damage
				if(new Random().nextInt(100) <= 80){
					// Block 50% of incoming damage.
					damage = damage * 0.5;
				}
			}

			if(!(block_data.containsKey(p.getName()))){
				// They have a colored name for some fucked reason. return to avoid errors.
				return;
			}

			if(p.getPlayerListName().equalsIgnoreCase("") && p.getGameMode() == GameMode.CREATIVE){
				return;
			}

			if(!(armor_data.containsKey(p.getName()))){
				return;
			}

			int block_chance = block_data.get(p.getName());
			if(ProfessionMechanics.fish_bonus_block.containsKey(p.getName())){
				block_chance += ProfessionMechanics.fish_bonus_block.get(p.getName());
			}

			int dodge_chance = dodge_data.get(p.getName());
			int thorn_amount = thorn_data.get(p.getName());
			int reflection_chance = reflection_data.get(p.getName());

			int accuracy = getAccuracyVal(wep);

			if(!(no_negation.contains(p)) && reflection_chance >= new Random().nextInt(100)){ // !(no_negation.contains(p)) && 
				if(e_attacker instanceof Player){
					p.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "                        *REFLECT* (" + ((Player)e_attacker).getName() + ChatColor.GOLD + ")");
				}
				else{
					p.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "                        *REFLECT* (" + MonsterMechanics.getMobType(e_attacker, false) + ChatColor.GOLD + ")");
				}
				double old_damage = e.getDamage();

				//e.setCancelled(true);
				reflection = true;
				//p.setNoDamageTicks(30);
				no_negation.add(p);

				if(e_attacker instanceof Player){
					Player p_attacker = (Player) e_attacker;
					p_attacker.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "                   *OPPONENT REFLECT* (" + p.getName() + ChatColor.RED + ")");	
				}
				//le_attacker.damage(old_damage);

				if(!(e_attacker instanceof Player)){
					/*EntityDamageByEntityEvent edee = new EntityDamageByEntityEvent(ent, e_attacker, DamageCause.ENTITY_ATTACK, old_damage);
					Bukkit.getPluginManager().callEvent(edee);*/
					MonsterMechanics.subtractMHealth(e_attacker, (int)old_damage);
				}

				else if(e_attacker instanceof Player){
					Player p_attacker = (Player)e_attacker;
					if(!CommunityMechanics.isPlayerOnBuddyList(p.getName(), p_attacker.getName()) && !PartyMechanics.arePartyMembers(p.getName(), p_attacker.getName())){
						double max_hp = HealthMechanics.getMaxHealthValue(p_attacker.getName());

						double dmg = e.getDamage();
						double total_hp = HealthMechanics.getPlayerHP(p_attacker.getName());
						double new_hp = total_hp - dmg;

						if(new_hp <= 0 && DuelMechanics.duel_map.containsKey(p_attacker.getName())){
							new_hp = 1;
						}

						if(new_hp <= 0){ // They're dead.
							p_attacker.setHealth(0);
							return;
						}

						//p_attacker.setLevel((int)new_hp);
						HealthMechanics.setPlayerHP(p_attacker.getName(), (int)new_hp);

						double health_percent = (new_hp / max_hp);
						double new_health_display = (health_percent * 20.0D);
						int conv_newhp_display = (int) new_health_display;
						if(conv_newhp_display <= 0){
							conv_newhp_display = 1;
						}
						p_attacker.setHealth(conv_newhp_display);
						p_attacker.playEffect(EntityEffect.HURT);
					}
				}

				e.setDamage((double)0);
			}

			if(!(no_negation.contains(p)) && (dodge_chance - accuracy) >= new Random().nextInt(100) && reflection == false){
				if(e_attacker instanceof Player){
					p.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "                        *DODGE* (" + ((Player)e_attacker).getName() + ChatColor.GREEN + ")");
				}
				else{
					String mob_name = MonsterMechanics.getMobType(e_attacker, false);
					p.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "                        *DODGE* (" + mob_name + ChatColor.GREEN + ")");
				}

				p.playSound(p.getLocation(), Sound.ZOMBIE_INFECT, 2F, 1.5F);
				e.setDamage((double)0);
				dodge = true;
				no_negation.add(p);
				Entity attacker = ((EntityDamageByEntityEvent)e).getDamager();
				if(attacker instanceof Player){
					Player p_attacker = (Player) attacker;
					p_attacker.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "                   *OPPONENT DODGED* (" + p.getName() + ChatColor.RED + ")");		
				}
			}

			if(!(no_negation.contains(p)) && (block_chance - accuracy) >= new Random().nextInt(100) && reflection == false && dodge == false){
				if(e_attacker instanceof Player){
					p.sendMessage(ChatColor.DARK_GREEN + "" + ChatColor.BOLD + "                        *BLOCK* (" + ((Player)e_attacker).getName() + ChatColor.DARK_GREEN + ")");
				}
				else{
					String mob_name = MonsterMechanics.getMobType(e_attacker, false);
					p.sendMessage(ChatColor.DARK_GREEN + "" + ChatColor.BOLD + "                        *BLOCK* (" + mob_name + ChatColor.DARK_GREEN + ")");
				}

				p.playSound(p.getLocation(), Sound.ZOMBIE_METAL, 2F, 1.0F);
				//TODO: if(magic_attack == true){don't set damage to 0.}
				e.setDamage((double)0);
				block = true;
				no_negation.add(p);
				Entity attacker = ((EntityDamageByEntityEvent)e).getDamager();
				if(attacker instanceof Player){
					Player p_attacker = (Player) attacker;
					p_attacker.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "                   *OPPONENT BLOCKED* (" + p.getName() + ChatColor.RED + ")");	
				}
			}

			int thorn_damage_to_return = (int) (e.getDamage() * ((double)thorn_amount / 100.0D));
			if(thorn_damage_to_return < 1 && thorn_amount > 0){thorn_damage_to_return = 1;} // Always at least 1 DMG reflect.

			if(thorn_damage_to_return > 0){
				Packet particles = new PacketPlayOutWorldEvent(2001, (int)Math.round(p.getLocation().getBlockX()), (int)Math.round(p.getLocation().getBlockY() + 1), (int)Math.round(p.getLocation().getBlockZ()), 18, false);
				((CraftServer) Main.plugin.getServer()).getServer().getPlayerList().sendPacketNearby(p.getLocation().getBlockX(), p.getLocation().getBlockY(), p.getLocation().getBlockZ(), 24, ((CraftWorld) p.getWorld()).getHandle().dimension, particles);

				if(!(e_attacker instanceof Player)){
					/*EntityDamageByEntityEvent edee = new EntityDamageByEntityEvent(e.getEntity(), attacker, DamageCause.CUSTOM, thorn_damage_to_return);
					Bukkit.getPluginManager().callEvent(edee);*/
					MonsterMechanics.subtractMHealth(e_attacker, (int)thorn_damage_to_return);
				}
				else if(e_attacker instanceof Player){
					Player p_attacker = (Player)e_attacker;

					if(!CommunityMechanics.isPlayerOnBuddyList(p_attacker.getName(), p.getName()) && !PartyMechanics.arePartyMembers(p.getName(), p_attacker.getName())){
						double max_hp = HealthMechanics.getMaxHealthValue(p_attacker.getName());

						double dmg = thorn_damage_to_return;
						double total_hp = HealthMechanics.getPlayerHP(p_attacker.getName());
						double new_hp = total_hp - dmg;

						if(new_hp <= 0 && DuelMechanics.duel_map.containsKey(p_attacker.getName())){
							new_hp = 1;
						}

						if(new_hp <= 0){ // They're dead.
							//p_attacker.setHealth(0);
							return;
						}

						//p_attacker.setLevel((int)new_hp);
						HealthMechanics.setPlayerHP(p_attacker.getName(), (int)new_hp);

						double health_percent = (new_hp / max_hp);
						double new_health_display = (health_percent * 20.0D);
						int conv_newhp_display = (int) new_health_display;
						if(conv_newhp_display <= 0){
							conv_newhp_display = 1;
						}
						p_attacker.setHealth(conv_newhp_display);
						p_attacker.playEffect(EntityEffect.HURT);
					}
				}

			}

		}

		if(block || dodge){
			noKnockback(p,p.getLocation());
		}

		if(block == false && dodge == false && reflection == false){ //  && (!(e.isCancelled())
			// Mob elemental damage.
			Entity attacker = null;
			String elemental_type = null;

			if(e instanceof EntityDamageByEntityEvent){
				if(e.getCause() == DamageCause.ENTITY_ATTACK){	
					attacker = ((EntityDamageByEntityEvent)e).getDamager();
				}
				if(e.getCause() == DamageCause.PROJECTILE){
					if(!((((EntityDamageByEntityEvent)e).getDamager()) instanceof Projectile)){
						return;
					}
					attacker =  (Entity) ((Projectile)((EntityDamageByEntityEvent)e).getDamager()).getShooter();
				}
			}

			if(attacker != null){
				if(!(attacker instanceof Player)){
					// Monster.
					if(attacker instanceof LivingEntity){
						LivingEntity le_attacker = (LivingEntity)attacker;
						if(le_attacker.hasMetadata("etype")){
							elemental_type = le_attacker.getMetadata("etype").get(0).asString();
						}
					}
				}
				else if(attacker instanceof Player){
					// TODO: Player elemental DMG rewrite.
				}
			}

			/*if(elemental_type != null){
				// Elemental damage taking place.
				if(elemental_type.equalsIgnoreCase("poison")){
					p.getWorld().playEffect(p.getLocation().add(0, 0.5, 0), Effect.POTION_BREAK, 4);
					if(!(p.hasPotionEffect(PotionEffectType.POISON))){
						p.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 30, 1));
					}
				}
				if(elemental_type.equalsIgnoreCase("fire")){
					p.setFireTicks(20); // Ignite! (1s)
				}
				if(elemental_type.equalsIgnoreCase("ice")){
					p.getWorld().playEffect(p.getLocation().add(0, 0.5, 0), Effect.POTION_BREAK, 8194);
					if(!(p.hasPotionEffect(PotionEffectType.SLOW))){
						p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 30, 1));
					}
				}
				if(elemental_type.equalsIgnoreCase("pure")){
					p.getWorld().playEffect(p.getLocation().add(0, 0.5, 0), Effect.POTION_BREAK, 8204);
				}
			}*/

			double armor_percent = calculateArmorVal(p);
			if(ProfessionMechanics.fish_bonus_armor.containsKey(p.getName())){
				armor_percent += ProfessionMechanics.fish_bonus_armor.get(p.getName());
			}
			if(elemental_type != null){
				if(elemental_type.equalsIgnoreCase("pure")){
					armor_percent = 0; // No negation.e
				}
				else{
					// Ignore 80% of armor automatically. Check for resistance.
					armor_percent = armor_percent * 0.20; // 20% of original armor %.
					// Ok, now let's see if they have resistance we can add to this new %.
					double res_percent = 0;
					if(elemental_type.equalsIgnoreCase("fire")){
						if(fire_res_data.containsKey(p.getName())){
							res_percent = fire_res_data.get(p.getName());
						}
					}
					if(elemental_type.equalsIgnoreCase("ice")){
						if(ice_res_data.containsKey(p.getName())){
							res_percent = ice_res_data.get(p.getName());
						}
					}
					if(elemental_type.equalsIgnoreCase("poison")){
						if(poison_res_data.containsKey(p.getName())){
							res_percent = poison_res_data.get(p.getName());
						}
					}

					armor_percent += res_percent; // Add the % of resist as armor for the attack.
				}
			}

			double pure_dmg = 0;
			double armor_pen = 0;

			if(wep != null){
				// Let's see if weapon had pure_dmg OR armor_pen.
				String dmg_data = getDamageData(wep);

				for(String attr : dmg_data.split(":")){
					if(attr.contains("pure_dmg")){
						attr = attr.split("=")[1];
						pure_dmg = Integer.parseInt(attr.substring(0, attr.length()));
					}
					if(attr.contains("armor_pen")){
						attr = attr.split("=")[1];
						armor_pen = Integer.parseInt(attr.substring(0, attr.length()));
						armor_pen += (armor_pen * (dex_data.get(p.getName()) * 0.009));
					}
				}

				if(armor_pen > 0){
					armor_percent -= armor_pen;
				}

				if(wep.getType() == Material.BOW && e.getCause() == DamageCause.PROJECTILE && attacker != null){
					// Calculate how far the shot was, subtract from armor.
					double distance = p.getLocation().distanceSquared(attacker.getLocation());
					double block_count = Math.sqrt(distance);
					if(attacker instanceof Player){
						Player p_attacker = (Player)attacker;
						if(block_count >= 30){
							AchievmentMechanics.addAchievment(p_attacker.getName(), "The Hawkeye");
							if(block_count >= 40){
								AchievmentMechanics.addAchievment(p_attacker.getName(), "The Deadeye");
								if(block_count >= 60){
									AchievmentMechanics.addAchievment(p_attacker.getName(), "The Sniper");
								}
							}
						}
					}
					armor_percent -= block_count;
				}

				if(attacker != null && !processing_ent_dmg_event.contains(attacker) && MonsterMechanics.mob_health.containsKey((LivingEntity)attacker) && (wep.getType() == Material.WOOD_SPADE || wep.getType() == Material.STONE_SPADE || wep.getType() == Material.IRON_SPADE || wep.getType() == Material.DIAMOND_SPADE || wep.getType() == Material.GOLD_SPADE || attacker.getType() == EntityType.IRON_GOLEM)){
					processing_ent_dmg_event.add(attacker);
					// AoE on the idiot we damaging.
					List<Entity> aoe = e.getEntity().getNearbyEntities(2.5, 3, 2.5);
					for(Entity aoe_ent : aoe){
						// This creates infinite loop.
						if(aoe_ent instanceof Player){
							LivingEntity le_ent = (LivingEntity) aoe_ent;
							le_ent.damage(damage, attacker); 
						}
					}
					processing_ent_dmg_event.remove(attacker);
				}

			}

			int damage_to_reduce = (int)Math.round(((double)damage * ((double)armor_percent / 100.0D)));

			if(p.hasPotionEffect(PotionEffectType.DAMAGE_RESISTANCE)){
				double armor_modifier = 1.05D;
				int tier = 1;
				for(PotionEffect pe : p.getActivePotionEffects()){
					if(pe.getType() == PotionEffectType.DAMAGE_RESISTANCE){
						tier = pe.getAmplifier(); break;
					}
				}
				if(tier == 1){
					armor_modifier = 1.05D;
				}
				if(tier == 2){
					armor_modifier = 1.10D;
				}
				if(tier == 3){
					armor_modifier = 1.20D;
				}

				damage_to_reduce = (int)(damage_to_reduce * armor_modifier);
			}

			if(e.getDamage() > 0){
				double dmg = (double)(double) ((damage - damage_to_reduce) + pure_dmg);
				e.setDamage(dmg);

				if(dmg >= 450 && attacker instanceof Player){
					AchievmentMechanics.addAchievment(((Player)attacker).getName(), "Serious Strength");
				}
			}

			if(CommunityMechanics.toggle_list.containsKey(p.getName())){
				if(CommunityMechanics.toggle_list.get(p.getName()).contains("debug") && e.getDamage() > 0){
					//p.sendMessage(ChatColor.GRAY + "DEBUG: " + damage_to_reduce + " DMG (original: " + damage + ")"); -50HP [-5%A -> -25DMG]
					p.sendMessage(ChatColor.RED + "        " + ChatColor.BOLD + "-" + ChatColor.RED + (int)e.getDamage() + ChatColor.RED + ChatColor.BOLD + "HP" + ChatColor.GRAY + " [-" + calculateArmorVal(p) + "%A -> -" + damage_to_reduce + ChatColor.BOLD + "DMG" + ChatColor.GRAY + "] " + ChatColor.GREEN + "[" + (int)(HealthMechanics.getPlayerHP(p.getName()) - e.getDamage()) + ChatColor.BOLD + "HP" + ChatColor.GREEN + "]");
				}
			}
		}
	}

	public boolean doesPlayerHaveArrows(Player pl, int tier){
		for(ItemStack is : pl.getInventory().all(Material.ARROW).values()){
			if(getItemTier(is) == tier){
				return true;
			}
		}
		return false;
	}

	@SuppressWarnings("deprecation")
	public boolean subtractArrow(Player pl, int tier){
		for(ItemStack is : pl.getInventory().getContents()){
			if(is == null || is.getType() != Material.ARROW){
				continue;
			}
			int arrow_tier = getItemTier(is);
			if(arrow_tier == tier){
				int is_amount = is.getAmount();
				if(is.getAmount() > 1){
					is.setAmount(is_amount - 1);
				}
				else if(is.getAmount() == 1){
					pl.getInventory().remove(is);
				}
				pl.updateInventory();
				return true;
			}
		}
		return false;
	}

	public void convertVanillaArrows(Inventory inv){
		for(ItemStack is : inv.getContents()){
			if(is == null || is.getType() != Material.ARROW){
				continue;
			}
			if(getItemTier(is) == 0){
				// Normal arrow! Convert to T1.
				ItemMeta im = is.getItemMeta();
				im.setDisplayName(ChatColor.WHITE.toString() + "Wood Arrow");
				im.setLore(new ArrayList<String>(Arrays.asList(ChatColor.GRAY.toString() + "An arrow for " + ChatColor.ITALIC.toString() + "shortbows.")));
				is.setItemMeta(im);
			}
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.NORMAL)
	public void onEntityShootBowEvent(EntityShootBowEvent e){
		if(!(e.getEntity().getType() == EntityType.PLAYER)){return;}

		Player p = (Player) e.getEntity();

		if(DuelMechanics.isDamageDisabled(p.getLocation()) && !(DuelMechanics.duel_map.containsKey(p.getName()))){
			e.setCancelled(true);
			p.updateInventory();
			return;
		}

		int bow_tier = getItemTier(p.getItemInHand());
		if(!doesPlayerHaveArrows(p, bow_tier)){
			e.setCancelled(true);
			p.updateInventory();
			return;
		}

		if(subtractArrow(p, bow_tier) != true){
			e.setCancelled(true);
			p.updateInventory();
			return;
			// Failed to remove arrow.
		}

		Arrow a = (Arrow)e.getProjectile();
		arrow_shooter.put(a, p.getItemInHand());

		String dmg_data = getDamageData(p.getItemInHand());

		if(dmg_data.contains("edmg=")){
			String elemental_type = dmg_data.split("edmg=")[1].split(":")[0].replaceAll(red.toString(), "");
			if(elemental_type.equalsIgnoreCase("fire")){
				a.setFireTicks(80);
			}
			if(elemental_type.equalsIgnoreCase("ice")){

			}
			if(elemental_type.equalsIgnoreCase("poison")){

			}

		}

	}

	@EventHandler
	public void onProjectileHitEvent(ProjectileHitEvent e){
		if(!(e.getEntity().getType() == EntityType.ARROW)){return;}
		final Arrow a = (Arrow) e.getEntity();

		if(a.getShooter() == null || (((CraftLivingEntity) a.getShooter()).getType() != EntityType.PLAYER)){
			return;
		}

		//Player p = (Player) a.getShooter();
		if(!(arrow_shooter.containsKey(a))){
			return;
		}
		String dmg_data = getDamageData(arrow_shooter.get(a));

		if(dmg_data.contains("edmg=")){
			String elemental_type = dmg_data.split("edmg=")[1].split(":")[0].replaceAll(red.toString(), "");
			if(elemental_type.equalsIgnoreCase("ice")){
				a.getWorld().playEffect(a.getLocation().add(0, 0.5, 0), Effect.POTION_BREAK, 8194);
			}
			if(elemental_type.equalsIgnoreCase("poison")){
				a.getWorld().playEffect(a.getLocation().add(0, 0.5, 0), Effect.POTION_BREAK, 4);
			}
		}

		Main.plugin.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
			public void run() {
				if(a != null){
					// Prevents arrows from sticking all over the place.
					a.remove();
				}
			}
		},  2L);
	}

	//@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public static void MeleeDebugListener(final EntityDamageByEntityEvent e){
		if(!(e.getDamager() instanceof Player)){return;}
		final Player p_attacker = (Player) e.getDamager();
		Entity temp_ent = e.getEntity();

		if(temp_ent.getPassenger() != null){
			Entity r_ent = temp_ent.getPassenger();
			temp_ent = r_ent;
		}

		final Entity ent = temp_ent;

		if(!(ent instanceof LivingEntity)){
			return;
		}
		final LivingEntity le = (LivingEntity) ent;
		boolean is_player = false;

		//log.info("DEBUG DMG - " + e.getDamage());

		if(e.getDamage() <= 0){
			return;
		}

		if(le instanceof Player){
			is_player = true;
			Player p_hurt = (Player)le;
			if(DuelMechanics.isPvPDisabled(p_hurt)){
				if(!(DuelMechanics.duel_map.containsKey(p_attacker.getName())) || !(DuelMechanics.duel_map.get(p_attacker.getName()).equalsIgnoreCase(p_hurt.getName()))){
					return;
				}

				if(DuelMechanics.duel_countdown.containsKey(p_attacker) || DuelMechanics.duel_countdown.containsKey(p_hurt)){
					return;
				}
			}
		}

		final double dmg = e.getDamage();

		if(CommunityMechanics.toggle_list.containsKey(p_attacker.getName()) && CommunityMechanics.toggle_list.get(p_attacker.getName()).contains("debug")){
			final boolean f_is_player = is_player;
			Main.plugin.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
				public void run() {
					if(f_is_player == true){
						p_attacker.sendMessage(ChatColor.RED + "        " + (int)dmg + ChatColor.BOLD + " DMG" + ChatColor.RED + " -> " + ((Player)le).getName());
					}
					if(f_is_player == false){
						String mob_name = MonsterMechanics.getMobType(ent, false);
						p_attacker.sendMessage(ChatColor.RED + "        " + (int)dmg + ChatColor.BOLD + " DMG" + ChatColor.RED + " -> "  + mob_name + " [" + ((int)MonsterMechanics.getMHealth(ent)) + "HP]");
					}
				}
			},  1L);
		}
	}

	//@EventHandler(priority = EventPriority.HIGHEST)
	public static void ArrowDebugListener(final EntityDamageByEntityEvent e){
		if(!(e.getDamager().getType() == EntityType.ARROW)){return;}
		Arrow a = (Arrow) e.getDamager();
		if(e.getDamage() <= 0){
			return;
		}

		if(a.getShooter() != null && !(((CraftLivingEntity) a.getShooter()).getType() == EntityType.PLAYER)){
			return;
		}

		final Player p = (Player) a.getShooter();
		if(!(arrow_shooter.containsKey(a))){
			return;
		}

		boolean is_player = false;

		Entity temp_ent = e.getEntity();

		if(temp_ent.getPassenger() != null){
			Entity r_ent = temp_ent.getPassenger();
			temp_ent = r_ent;
		}

		final Entity ent = temp_ent;

		final LivingEntity le = (LivingEntity) ent;
		final double dmg = e.getDamage();


		if(PetMechanics.pet_map.containsKey(ent)){
			return;
		}

		if(le instanceof Player){
			is_player = true;
			Player p_shot = (Player)le;
			if(DuelMechanics.isPvPDisabled(p_shot)){
				if(!(DuelMechanics.duel_map.containsKey(p_shot.getName())) || !(DuelMechanics.duel_map.get(p_shot.getName()).equalsIgnoreCase(p.getName()))){
					le.setFireTicks(0);
					return;
				}
				if(DuelMechanics.duel_countdown.containsKey(p_shot) || DuelMechanics.duel_countdown.containsKey(p)){
					le.setFireTicks(0);
					return;
				}
			}
		}

		if(CommunityMechanics.toggle_list.get(p.getName()).contains("debug")){
			final boolean f_is_player = is_player;
			Main.plugin.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
				public void run() {
					//p_attacker.sendMessage(ChatColor.GRAY + "DEBUG: " + String.valueOf(e.getDamage()) + " DMG");
					if(f_is_player == true){
						p.sendMessage(ChatColor.RED + "        " + (int)dmg + ChatColor.BOLD + " DMG" + ChatColor.RED + " -> " + ((Player)le).getName());
					}
					if(f_is_player == false){
						String mob_name = MonsterMechanics.getMobType(ent, false);
						p.sendMessage(ChatColor.RED + "        " + (int)dmg + ChatColor.BOLD + " DMG" + ChatColor.RED + " -> "  + mob_name + " [" + ((int)MonsterMechanics.getMHealth(ent)) + "HP]");
					}
				}
			},  1L);
		}

	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public static void PDMG_MeleeDebugListener(EntityDamageByEntityEvent e){
		if(!(e.getDamager() instanceof Player)){return;}
		if(!(e.getEntity() instanceof Player)){return;}

		final Player p_attacker = (Player) e.getDamager();
		final Entity ent = e.getEntity();
		if(!(ent instanceof LivingEntity)){
			return;
		}
		final LivingEntity le = (LivingEntity) ent;
		boolean is_player = false;

		//log.info("DEBUG DMG - " + e.getDamage());

		if(e.getDamage() <= 0){
			return;
		}

		if(le instanceof Player){
			is_player = true;
			Player p_hurt = (Player)le;
			if(DuelMechanics.isPvPDisabled(p_hurt)){
				if(!(DuelMechanics.duel_map.containsKey(p_attacker.getName())) || !(DuelMechanics.duel_map.get(p_attacker.getName()).equalsIgnoreCase(p_hurt.getName()))){
					return;
				}

				if(DuelMechanics.duel_countdown.containsKey(p_attacker) || DuelMechanics.duel_countdown.containsKey(p_hurt)){
					return;
				}
			}
		}

		final double dmg = e.getDamage();

		if(CommunityMechanics.toggle_list.containsKey(p_attacker.getName()) && CommunityMechanics.toggle_list.get(p_attacker.getName()).contains("debug")){
			final boolean f_is_player = is_player;
			Main.plugin.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
				public void run() {
					if(f_is_player == true){
						p_attacker.sendMessage(ChatColor.RED + "        " + (int)dmg + ChatColor.BOLD + " DMG" + ChatColor.RED + " -> " + ((Player)le).getName());
					}
					if(f_is_player == false){
						String mob_name = MonsterMechanics.getMobType(ent, false);
						p_attacker.sendMessage(ChatColor.RED + "        " + (int)dmg + ChatColor.BOLD + " DMG" + ChatColor.RED + " -> "  + mob_name + " [" + ((int)MonsterMechanics.getMHealth(ent)) + "HP]");
					}
				}
			},  1L);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public static void PDMG_ArrowDebugListener(final EntityDamageByEntityEvent e){
		if(!(e.getDamager().getType() == EntityType.ARROW)){return;}
		if(!(e.getEntity() instanceof Player)){return;}
		Arrow a = (Arrow) e.getDamager();
		if(e.getDamage() <= 0){
			return;
		}

		if(a.getShooter() != null && !(((CraftLivingEntity) a.getShooter()).getType() == EntityType.PLAYER)){
			return;
		}

		final Player p = (Player) a.getShooter();
		if(!(arrow_shooter.containsKey(a))){
			return;
		}

		boolean is_player = false;
		final Entity ent = e.getEntity();
		final LivingEntity le = (LivingEntity) ent;
		final double dmg = e.getDamage();

		if(PetMechanics.pet_map.containsKey(ent)){
			return;
		}

		if(le instanceof Player){
			is_player = true;
			Player p_shot = (Player)le;
			if(DuelMechanics.isPvPDisabled(p_shot)){
				if(!(DuelMechanics.duel_map.containsKey(p_shot.getName())) || !(DuelMechanics.duel_map.get(p_shot.getName()).equalsIgnoreCase(p.getName()))){
					le.setFireTicks(0);
					return;
				}
				if(DuelMechanics.duel_countdown.containsKey(p_shot) || DuelMechanics.duel_countdown.containsKey(p)){
					le.setFireTicks(0);
					return;
				}
			}
		}

		if(CommunityMechanics.toggle_list.get(p.getName()).contains("debug")){
			final boolean f_is_player = is_player;
			Main.plugin.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
				public void run() {
					//p_attacker.sendMessage(ChatColor.GRAY + "DEBUG: " + String.valueOf(e.getDamage()) + " DMG");
					if(f_is_player == true){
						p.sendMessage(ChatColor.RED + "        " + (int)dmg + ChatColor.BOLD + " DMG" + ChatColor.RED + " -> " + ((Player)le).getName());
					}
					if(f_is_player == false){
						String mob_name = MonsterMechanics.getMobType(ent, false);
						p.sendMessage(ChatColor.RED + "        " + (int)dmg + ChatColor.BOLD + " DMG" + ChatColor.RED + " -> "  + mob_name + " [" + ((int)MonsterMechanics.getMHealth(ent)) + "HP]");
					}
				}
			},  1L);
		}

		//e.setDamage((double)0); // Prevent vanilla damage.
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onNPCHurt(EntityDamageEvent e){
		Entity ent = e.getEntity();
		if(ent instanceof Player){
			Player npc = (Player)ent;
			if(ent.hasMetadata("NPC") || npc.getPlayerListName().equalsIgnoreCase("")){
				if(e instanceof EntityDamageByEntityEvent){
					EntityDamageByEntityEvent edbee = (EntityDamageByEntityEvent)e;
					if(edbee.getDamager() instanceof Player){
						//Player pl = (Player)edbee.getDamager();
						/*if(EcashMechanics.personal_clones.containsKey(pl.getName())){
							String npc_name = npc.getName();
							String pl_name = pl.getName();

							if(pl_name.length() > 14){
								pl_name = pl_name.substring(0, 14);
							}

							if(ChatColor.stripColor(npc.getName()).equalsIgnoreCase(pl_name)){
								npc.getWorld().spawnParticle(npc.getLocation().add(0, 1, 0), Particle.CRIT, 1F, 20);
								npc.remove();
								EcashMechanics.personal_clones.remove(pl.getName());
								EcashMechanics.personal_clones_msg.remove(pl.getName());
								return;
							}
						}*/
					}
				}
				e.setCancelled(true);
				e.setDamage((double)0);
			} 
		}
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void ArrowListener(EntityDamageByEntityEvent e){
		if(!(e.getDamager().getType() == EntityType.ARROW)){return;}
		if(MountMechanics.mount_map.containsKey(e.getEntity())){
			return;
		}

		Arrow a = (Arrow) e.getDamager();
		if(a.getShooter() != null && !(((CraftLivingEntity) a.getShooter()).getType() == EntityType.PLAYER)){
			LivingEntity le = (LivingEntity)e.getEntity();
			if(DuelMechanics.isDamageDisabled(le.getLocation())){
				le.setFireTicks(0);
				e.setCancelled(true);
				e.setDamage((double)0);
			}
			return;
		}

		Player p = (Player) a.getShooter();
		HealthMechanics.in_combat.put(p.getName(), System.currentTimeMillis());
		//boolean is_player = false;
		Entity ent = e.getEntity();
		LivingEntity le = (LivingEntity) ent;

		if(PetMechanics.pet_map.containsKey(ent)){
			return;
		}

		if(le instanceof Player){
			//is_player = true;
			Player p_shot = (Player)le;
			if(DuelMechanics.isPvPDisabled(p_shot)){
				if(!(DuelMechanics.duel_map.containsKey(p_shot.getName())) || !(DuelMechanics.duel_map.get(p_shot.getName()).equalsIgnoreCase(p.getName()))){
					le.setFireTicks(0);
					e.setCancelled(true);
					e.setDamage((double)0);
					return;
				}
				if(DuelMechanics.duel_countdown.containsKey(p_shot) || DuelMechanics.duel_countdown.containsKey(p)){
					le.setFireTicks(0);
					e.setCancelled(true);
					e.setDamage((double)0);
					return;
				}
			}
		}


		boolean crit = false;
		String dmg_data = getDamageData(arrow_shooter.get(a));
		if(dmg_data.equalsIgnoreCase("no")){
			return;
		}
		double dmg = Double.parseDouble(dmg_data.substring(0, dmg_data.indexOf(":")));

		if(dmg_data.contains("vs_")){
			double vs_mod = 0;
			if(ent instanceof Player && dmg_data.contains("vs_players")){
				vs_mod = Double.parseDouble(dmg_data.split("vs_players=")[1].split(":")[0].replaceAll(red.toString(), ""));
				dmg = dmg + (int)((dmg * (vs_mod / 100.0D)));
			}
			if(MonsterMechanics.mob_health.containsKey(ent) && dmg_data.contains("vs_monsters")){
				vs_mod = Double.parseDouble(dmg_data.split("vs_monsters=")[1].split(":")[0].replaceAll(red.toString(), ""));
				dmg = dmg + (int)((dmg * (vs_mod / 100.0D)));
			}
		}

		if(dmg_data.contains("edmg=")){
			String elemental_type = dmg_data.split("edmg=")[1].split(":")[0].replaceAll(red.toString(), "");
			int tier = getItemTier(p.getItemInHand());
			if(elemental_type.equalsIgnoreCase("fire")){
				if(tier == 1){
					ent.setFireTicks(15);
				}
				if(tier == 2){
					ent.setFireTicks(25);
				}
				if(tier == 3){
					ent.setFireTicks(30);
				}
				if(tier == 4){
					ent.setFireTicks(35);
				}
				if(tier == 5){
					ent.setFireTicks(40);
				}
			}
			if(elemental_type.equalsIgnoreCase("ice")){
				le.getWorld().playEffect(le.getLocation().add(0, 1.3, 0), Effect.POTION_BREAK, 8194);

				int mob_tier = MonsterMechanics.getMobTier(ent);
				if(!le.hasPotionEffect(PotionEffectType.SLOW)){
					if(mob_tier == 4 || mob_tier == 5){
						le.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 30, 0));
					}
					else{
						if(tier == 1){
							le.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 40, 0));
						}
						if(tier == 2){
							le.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 50, 0));
						}
						if(tier == 3){
							le.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 40, 1));
						}
						if(tier == 4){
							le.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 50, 1));
						}
						if(tier == 5){
							le.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 60, 1));
						}
					}
				}
			}
			if(elemental_type.equalsIgnoreCase("poison")){
				// TODO: Green squigglies.
				le.getWorld().playEffect(le.getLocation().add(0, 1.3, 0), Effect.POTION_BREAK, 4);
				if(tier == 1){
					le.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 40, 0));
				}
				if(tier == 2){
					le.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 50, 0));
				}
				if(tier == 3){
					le.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 60, 1));
				}
				if(tier == 4){
					le.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 70, 1));
				}
				if(tier == 5){
					le.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 80, 1));
				}

			}
		}
		if(dmg_data.contains("crit=true")){
			try {
				ParticleEffect.sendToLocation(ParticleEffect.MAGIC_CRIT, ent.getLocation(), new Random().nextFloat(), new Random().nextFloat(), new Random().nextFloat(), 1F, 50);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			p.playSound(p.getLocation(), Sound.WOOD_CLICK, 1.5F, 0.5F);
			crit = true;
		}
		if(dmg_data.contains("leech=") || ProfessionMechanics.fish_bonus_lifesteal.containsKey(p.getName())){
			double leech_amount = 0;
			if(dmg_data.contains("leech=")){
				leech_amount = Integer.parseInt(dmg_data.split("leech=")[1].split(":")[0]);
			}
			if(ProfessionMechanics.fish_bonus_lifesteal.containsKey(p.getName())){
				leech_amount += ProfessionMechanics.fish_bonus_lifesteal.get(p.getName());
			}
			double leech_val = (double) (dmg * (0.01 * leech_amount));
			if(leech_val < 1){
				leech_val = 1;
			}
			if((leech_val + HealthMechanics.getPlayerHP(p.getName())) > getMaxHP(p)){
				//p.setLevel(getMaxHP(p));
				HealthMechanics.setPlayerHP(p.getName(), getMaxHP(p));
				p.setHealth(20);
			}
			else{
				HealthMechanics.setPlayerHP(p.getName(), ((int)(HealthMechanics.getPlayerHP(p.getName()) + leech_val)));
				double health_percent = ((double)HealthMechanics.getPlayerHP(p.getName())) / (double)getMaxHP(p);
				double new_health_display = health_percent * 20.0D;
				p.setHealth((int)new_health_display);
			}

			Packet particles = new PacketPlayOutWorldEvent(2001, (int)Math.round(p.getLocation().getX()), (int)Math.round(p.getLocation().getY() + 1), (int)Math.round(p.getLocation().getZ()), 152, false);
			((CraftServer) Main.plugin.getServer()).getServer().getPlayerList().sendPacketNearby(p.getLocation().getX(), p.getLocation().getY(), p.getLocation().getZ(), 24, ((CraftWorld) p.getWorld()).getHandle().dimension, particles);

			if(CommunityMechanics.toggle_list.get(p.getName()).contains("debug")){
				p.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "        +" + ChatColor.GREEN + (int)leech_val + ChatColor.BOLD + " HP" + ChatColor.GRAY + " [" + (int)(HealthMechanics.getPlayerHP(p.getName())) + "/" + (int)getMaxHP(p) + "HP]");
			}

		}
		if(dmg_data.contains("slow=true")){
			int mob_tier = MonsterMechanics.getMobTier(ent);
			if(!le.hasPotionEffect(PotionEffectType.SLOW)){
				if(mob_tier == 4 || mob_tier == 5){
					le.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 40, 1));
				}
				else{
					le.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 100, 1));
				}
			}
		}

		if(dmg_data.contains("blind=true") && !le.hasPotionEffect(PotionEffectType.BLINDNESS) && !le.hasMetadata("blind")){
			int tier = getItemTier(p.getItemInHand());
			boolean blind = true; 

			if(le.hasMetadata("blind")){
				long last_blind = le.getMetadata("blind").get(0).asLong();
				if((System.currentTimeMillis() - last_blind) <= (10 * 1000)){
					// Less than 10 seconds, do nothing.
					blind = false;
				}
			}

			if(blind){
				if(tier == 1){
					le.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 30, 1));
				}
				if(tier == 2){
					le.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 40, 1));
				}
				if(tier == 3){
					le.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 50, 1));
				}
				if(tier == 4){
					le.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 60, 1));
				}
				if(tier == 5){
					le.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 60, 1));
				}
				le.setMetadata("blind", new FixedMetadataValue(Main.plugin, System.currentTimeMillis()));
			}
		}

		List<Integer> dmg_modifications = new ArrayList<Integer>(ItemMechanics.dmg_data.get(p.getName()));
		if(dmg_modifications.get(0) > 0){ // It's not 0, so let's do something.
			int min_dmg_mod = dmg_modifications.get(0);
			int max_dmg_mod = dmg_modifications.get(1);
			double actual_dmg_mod = 0.0D;

			if(max_dmg_mod - min_dmg_mod <= 0){
				actual_dmg_mod = max_dmg_mod;
			}
			else{
				actual_dmg_mod = new Random().nextInt(max_dmg_mod - min_dmg_mod) + min_dmg_mod;
			}
			// This will be a % number, for ex. 5%.
			double percent_dmg_mod = actual_dmg_mod / 100.0D; 

			dmg += (int)(double)((double)dmg * (double)percent_dmg_mod);
		}

		if(dex_data.get(p.getName()) > 0 || dmg_data.contains("dex=")){
			double dex_mod = 0;
			if(dex_data.get(p.getName()) > 0){
				dex_mod = dex_data.get(p.getName());
			}
			if(dmg_data.contains("dex=")){
				dex_mod += Double.parseDouble(dmg_data.split("dex=")[1].split(":")[0]);
			}
			dmg += (int)(double)((double)dmg * (double)(((dex_mod * 0.015)/100.0D)));
		}

		if(ProfessionMechanics.fish_bonus_dmg.containsKey(p.getName())){
			dmg += (dmg * (ProfessionMechanics.fish_bonus_dmg.get(p.getName()) / 100.0D));
		}

		if(p.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE)){
			double dmg_modifier = 1.10D;
			int pot_tier = 1;
			for(PotionEffect pe : p.getActivePotionEffects()){
				if(pe.getType() == PotionEffectType.INCREASE_DAMAGE){
					pot_tier = pe.getAmplifier(); break;
				}
			}
			if(pot_tier == 1){
				dmg_modifier = 1.10D;
			}
			if(pot_tier == 2){
				dmg_modifier = 1.30D;
			}
			if(pot_tier == 3){
				dmg_modifier = 1.50D;
			}

			dmg = (int)(dmg * dmg_modifier);
		}

		if(crit == true){
			dmg = (int) Math.round((double)dmg * 1.5D);
		}

		e.setDamage((double)dmg);

	}

	/**
	 * Gets entities inside a cone.
	 * @see Utilities#getPlayersInCone(List, Location, int, int, int)
	 *
	 * @param entities - {@code List<Entity>}, list of nearby entities
	 * @param startpoint - {@code Location}, center point
	 * @param radius - {@code int}, radius of the circle
	 * @param degrees - {@code int}, angle of the cone
	 * @param direction - {@code int}, direction of the cone
	 * @return {@code List<Entity>} - entities in the cone
	 */
	public static List<Entity> getEntitiesInCone(List<Entity> entities, Location startpoint, int radius, int degrees, int direction){
		List<Entity> newEntities = new ArrayList<Entity>();

		int[] startPos = new int[] { (int)startpoint.getX(), (int)startpoint.getZ() };

		int[] endA = new int[] { (int)(radius * Math.cos(direction - (degrees / 2))), (int)(radius * Math.sin(direction - (degrees / 2))) };

		for(Entity e : entities){
			Location l = e.getLocation();       
			int[] entityVector = getVectorForPoints(startPos[0], startPos[1], l.getBlockX(), l.getBlockY());

			double angle = getAngleBetweenVectors(endA, entityVector);
			log.info("ANGLE - " + angle);
			if(Math.toDegrees(angle) < degrees && Math.toDegrees(angle) > 0)
				newEntities.add(e);
		}
		return newEntities;
	}
	/**
	 * Created an integer vector in 2d between two points
	 *
	 * @param x1 - {@code int}, X pos 1
	 * @param y1 - {@code int}, Y pos 1
	 * @param x2 - {@code int}, X pos 2
	 * @param y2 - {@code int}, Y pos 2
	 * @return {@code int[]} - vector
	 */
	public static int[] getVectorForPoints(int x1, int y1, int x2, int y2)
	{
		return new int[] { x2 - x1, y2 - y1 };
	}
	/**
	 * Get the angle between two vectors.
	 *
	 * @param vector1 - {@code int[]}, vector 1
	 * @param vector2 - {@code int[]}, vector 2
	 * @return {@code double} - angle
	 */
	public static double getAngleBetweenVectors(int[] vector1, int[] vector2)
	{
		return Math.atan2(vector2[1], vector2[0]) - Math.atan2(vector1[1], vector1[0]);
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void MeleeListener(EntityDamageByEntityEvent e){
		if(!(e.getDamager() instanceof Player)){return;}

		Player p_attacker = (Player) e.getDamager();
		Entity ent = e.getEntity();

		if(!(ent instanceof LivingEntity)){
			return;
		}
		if(MountMechanics.mount_map.containsKey(e.getEntity())){
			return;
		}
		if(processing_dmg_event.contains(p_attacker.getName())){
			return; // Prevents infinite loop w/ polearm AOE.
		}

		ItemStack weapon = p_attacker.getItemInHand();

		if(processing_proj_event.contains(p_attacker.getName())){
			if(spoofed_weapon.containsKey(p_attacker.getName())){
				weapon = spoofed_weapon.get(p_attacker.getName());
			}
			else{
				return;
			}
			//return; // Prevents hotswap w/ staffs
		}

		LivingEntity le = (LivingEntity) ent;
		//boolean is_player = false;

		if(le instanceof Player){

			//is_player = true;
			Player p_hurt = (Player)le;
			if(DuelMechanics.isPvPDisabled(p_hurt) || DuelMechanics.isPvPDisabled(p_attacker)){
				if(!(DuelMechanics.duel_map.containsKey(p_attacker.getName())) || !(DuelMechanics.duel_map.get(p_attacker.getName()).equalsIgnoreCase(p_hurt.getName()))){
					e.setCancelled(true);
					e.setDamage((double)0);
					return;
				}

				if(DuelMechanics.duel_countdown.containsKey(p_attacker) || DuelMechanics.duel_countdown.containsKey(p_hurt)){
					e.setCancelled(true);
					e.setDamage((double)0);
					return;
				}

			}

			if(!(DuelMechanics.duel_map.containsKey(p_attacker.getName())) && CommunityMechanics.toggle_list.containsKey(p_attacker.getName())){
				if(CommunityMechanics.toggle_list.get(p_attacker.getName()).contains("pvp")){
					// Don't damage another player.
					//p_attacker.sendMessage(ChatColor.RED + "You " + ChatColor.UNDERLINE + "cannot" + ChatColor.RED + " attack the lawful player (" + p.getName() + ") with /togglepvp enabled.");
					e.setCancelled(true);
					e.setDamage(0);
					return;
				}
			}

		}

		if(ent instanceof Player){
			p_attacker.getWorld().playSound(p_attacker.getLocation(), Sound.WOOD_CLICK, 1, 1.6F);
		}

		boolean crit = false;

		if((!e.isCancelled() || DuelMechanics.duel_map.containsKey(p_attacker.getName())) && getDamageData(weapon).equalsIgnoreCase("no")){
			e.setDamage((double)1);
			//return;
		}

		if(e.getDamage() <= 0){return;}

		if(weapon.getType() == Material.BOW){ // This is only called when the player left clicks with the bow.
			if(le.getType() == EntityType.PLAYER || (le.getPassenger() != null && le.getPassenger() instanceof Player)){
				Player attacked = null;
				if(le.getPassenger() != null && le.getPassenger() instanceof Player){
					attacked = (Player)le.getPassenger();
				}
				else{
					attacked = (Player)le;
				}
				if(!attacked.isBlocking()
						&& attacked.getWorld().getName().equalsIgnoreCase(Bukkit.getWorlds().get(0).getName()) 
						&& (!(DuelMechanics.isPvPDisabled(attacked.getLocation())) 
								|| (DuelMechanics.duel_map.containsKey(attacked.getName()) && DuelMechanics.duel_map.get(attacked.getName()).equalsIgnoreCase(p_attacker.getName())))){
					int tier = getItemTier(weapon);
					if((attacked.getLocation().getY() - p_attacker.getLocation().getY()) <= 0.5D){
						// TODO: Left click with bow effects.
						if(tier == 1){
							pushAwayEntity(p_attacker, ent, 1.2);
						}
						if(tier == 2){
							pushAwayEntity(p_attacker, ent, 1.5);
						}
						if(tier == 3){
							pushAwayEntity(p_attacker, ent, 1.8);
						}
						if(tier == 4){
							pushAwayEntity(p_attacker, ent, 2.0);
						}
						if(tier == 5){
							pushAwayEntity(p_attacker, ent, 2.2);
						}	
					}
				}
			}
			else{
				int tier = getItemTier(weapon);
				// TODO: Left click with bow effects.
				if(tier == 1){
					pushAwayEntity(p_attacker, ent, 1.5);
				}
				if(tier == 2){
					pushAwayEntity(p_attacker, ent, 1.8);
				}
				if(tier == 3){
					pushAwayEntity(p_attacker, ent, 2.1);
				}
				if(tier == 4){
					pushAwayEntity(p_attacker, ent, 2.4);
				}
				if(tier == 5){
					pushAwayEntity(p_attacker, ent, 3.0);
				}
			}

			e.setDamage((double)0);
			e.setCancelled(true);
			return;
		}

		if(weapon.getType() == Material.WOOD_SWORD || weapon.getType() == Material.STONE_SWORD || weapon.getType() == Material.IRON_SWORD || weapon.getType() == Material.DIAMOND_SWORD || weapon.getType() == Material.GOLD_SWORD){
			String dmg_data = getDamageData(weapon);

			double dmg = Double.parseDouble(dmg_data.substring(0, dmg_data.indexOf(":")));

			if(dmg_data.contains("vs_")){
				double vs_mod = 0;
				if(ent instanceof Player && dmg_data.contains("vs_players")){
					vs_mod = Double.parseDouble(dmg_data.split("vs_players=")[1].split(":")[0].replaceAll(red.toString(), ""));
					dmg = dmg + (int)((dmg * (vs_mod / 100.0D)));
				}
				if(MonsterMechanics.mob_health.containsKey(ent) && dmg_data.contains("vs_monsters")){
					vs_mod = Double.parseDouble(dmg_data.split("vs_monsters=")[1].split(":")[0].replaceAll(red.toString(), ""));
					dmg = dmg + (int)((dmg * (vs_mod / 100.0D)));
				}
			}

			if(dmg_data.contains("edmg=")){
				String elemental_type = dmg_data.split("edmg=")[1].split(":")[0].replaceAll(red.toString(), "");
				int tier = getItemTier(weapon);
				if(elemental_type.equalsIgnoreCase("fire")){
					if(tier == 1){
						ent.setFireTicks(15);
					}
					if(tier == 2){
						ent.setFireTicks(25);
					}
					if(tier == 3){
						ent.setFireTicks(30);
					}
					if(tier == 4){
						ent.setFireTicks(35);
					}
					if(tier == 5){
						ent.setFireTicks(40);
					}
				}
				if(elemental_type.equalsIgnoreCase("ice")){
					le.getWorld().playEffect(le.getLocation().add(0, 1.3, 0), Effect.POTION_BREAK, 8194);
					int mob_tier = MonsterMechanics.getMobTier(ent);
					if(!le.hasPotionEffect(PotionEffectType.SLOW)){
						if(mob_tier == 4 || mob_tier == 5){
							le.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 30, 0));
						}
						else{
							if(tier == 1){
								le.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 40, 0));
							}
							if(tier == 2){
								le.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 50, 0));
							}
							if(tier == 3){
								le.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 40, 1));
							}
							if(tier == 4){
								le.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 50, 1));
							}
							if(tier == 5){
								le.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 60, 1));
							}
						}
					}

				}
				if(elemental_type.equalsIgnoreCase("poison")){
					le.getWorld().playEffect(le.getLocation().add(0, 1.3, 0), Effect.POTION_BREAK, 4);
					if(tier == 1){
						le.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 40, 0));
					}
					if(tier == 2){
						le.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 50, 0));
					}
					if(tier == 3){
						le.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 60, 1));
					}
					if(tier == 4){
						le.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 70, 1));
					}
					if(tier == 5){
						le.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 80, 1));
					}

				}
			}
			if(dmg_data.contains("crit=true")){
				try {
					ParticleEffect.sendToLocation(ParticleEffect.MAGIC_CRIT, ent.getLocation(), new Random().nextFloat(), new Random().nextFloat(), new Random().nextFloat(), 1F, 50);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				p_attacker.playSound(p_attacker.getLocation(), Sound.WOOD_CLICK, 1.5F, 0.5F);
				crit = true;
			}
			if(dmg_data.contains("leech=") || ProfessionMechanics.fish_bonus_lifesteal.containsKey(p_attacker.getName())){
				double leech_amount = 0;
				if(dmg_data.contains("leech=")){
					leech_amount = Integer.parseInt(dmg_data.split("leech=")[1].split(":")[0]);
				}
				if(ProfessionMechanics.fish_bonus_lifesteal.containsKey(p_attacker.getName())){
					leech_amount += ProfessionMechanics.fish_bonus_lifesteal.get(p_attacker.getName());
				}
				//ent.playEffect(EntityEffect.WOLF_HEARTS);
				double leech_val = (double) (dmg * (0.01 * leech_amount));
				if(leech_val < 1){
					leech_val = 1;
				}
				if((leech_val + HealthMechanics.getPlayerHP(p_attacker.getName())) > getMaxHP(p_attacker)){
					//HealthMechanics.setPlayerHP(p_attacker.getName(), getMaxHP(p_attacker));
					HealthMechanics.setPlayerHP(p_attacker.getName(), getMaxHP(p_attacker));
					p_attacker.setHealth(20);
				}
				else{
					//leech_val -= 20 - p_attacker.getHealth();
					//HealthMechanics.setPlayerHP(p_attacker.getName(), (int)(HealthMechanics.getPlayerHP(p_attacker.getName()) + leech_val));
					HealthMechanics.setPlayerHP(p_attacker.getName(), (int)(HealthMechanics.getPlayerHP(p_attacker.getName()) + leech_val));
					double health_percent = ((double)HealthMechanics.getPlayerHP(p_attacker.getName())) / (double)getMaxHP(p_attacker);
					double new_health_display = health_percent * 20.0D;
					//log.info("" + new_health_display);
					p_attacker.setHealth((int)new_health_display);
				}

				double xpt = le.getLocation().getX(); //(p_attacker.getLocation().getX() + le.getLocation().getX()) / 2;
				double ypt = le.getLocation().getY(); //(p_attacker.getLocation().getY() + le.getLocation().getY()) / 2;
				double zpt = le.getLocation().getZ(); //(p_attacker.getLocation().getZ() + le.getLocation().getZ()) / 2;

				Packet particles = new PacketPlayOutWorldEvent(2001, (int)Math.round(xpt), (int)Math.round(ypt) + 1, (int)Math.round(zpt), 152, false);
				((CraftServer) Main.plugin.getServer()).getServer().getPlayerList().sendPacketNearby(p_attacker.getLocation().getX(), p_attacker.getLocation().getY(), p_attacker.getLocation().getZ(), 24, ((CraftWorld) p_attacker.getWorld()).getHandle().dimension, particles);

				if(CommunityMechanics.toggle_list.get(p_attacker.getName()).contains("debug")){
					p_attacker.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "        +" + ChatColor.GREEN + (int)leech_val + ChatColor.BOLD + " HP" + ChatColor.GRAY + " [" + (int)(HealthMechanics.getPlayerHP(p_attacker.getName())) + "/" + (int)getMaxHP(p_attacker) + "HP]");
				}

			}
			if(dmg_data.contains("knockback=true")){
				pushAwayEntity(p_attacker, ent, 1.5);
			}
			if(dmg_data.contains("blind=true") && !le.hasPotionEffect(PotionEffectType.BLINDNESS) && !le.hasMetadata("blind")){
				int tier = getItemTier(weapon);
				boolean blind = true; 

				if(le.hasMetadata("blind")){
					long last_blind = le.getMetadata("blind").get(0).asLong();
					if((System.currentTimeMillis() - last_blind) <= (10 * 1000)){
						// Less than 10 seconds, do nothing.
						blind = false;
					}
				}

				if(blind){
					if(tier == 1){
						le.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 30, 1));
					}
					if(tier == 2){
						le.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 40, 1));
					}
					if(tier == 3){
						le.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 50, 1));
					}
					if(tier == 4){
						le.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 60, 1));
					}
					if(tier == 5){
						le.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 60, 1));
					}
					le.setMetadata("blind", new FixedMetadataValue(Main.plugin, System.currentTimeMillis()));
				}
			}

			List<Integer> dmg_modifications = new ArrayList<Integer>(ItemMechanics.dmg_data.get(p_attacker.getName()));
			if(dmg_modifications.get(0) > 0){ // It's not 0, so let's do something.
				int min_dmg_mod = dmg_modifications.get(0);
				int max_dmg_mod = dmg_modifications.get(1);

				double actual_dmg_mod = 0.0D;

				if(max_dmg_mod - min_dmg_mod <= 0){
					actual_dmg_mod = max_dmg_mod;
				}
				else{
					actual_dmg_mod = new Random().nextInt(max_dmg_mod - min_dmg_mod) + min_dmg_mod;
				}
				// This will be a % number, for ex. 5%.
				double percent_dmg_mod = actual_dmg_mod / 100.0D; 

				dmg += (int)(double)((double)dmg * (double)percent_dmg_mod);
			}

			if(vit_data.get(p_attacker.getName()) > 0 || dmg_data.contains("vit=")){
				double vit_mod = 0;
				if(vit_data.get(p_attacker.getName()) > 0){
					vit_mod = vit_data.get(p_attacker.getName());
				}
				if(dmg_data.contains("vit=")){
					vit_mod += Double.parseDouble(dmg_data.split("vit=")[1].split(":")[0]);
				}
				dmg += (int)(double)((double)dmg * (double)(((vit_mod * 0.015)/100.0D)));
			}

			if(ProfessionMechanics.fish_bonus_dmg.containsKey(p_attacker.getName())){
				dmg += (dmg * (ProfessionMechanics.fish_bonus_dmg.get(p_attacker.getName()) / 100.0D));
			}

			if(crit == true){
				dmg = (int) Math.round((double)dmg * 1.5);
			}

			if(p_attacker.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE)){
				double dmg_modifier = 1.10D;
				int pot_tier = 1;
				for(PotionEffect pe : p_attacker.getActivePotionEffects()){
					if(pe.getType() == PotionEffectType.INCREASE_DAMAGE){
						pot_tier = pe.getAmplifier(); break;
					}
				}
				if(pot_tier == 0){
					dmg_modifier = 1.10D;
				}
				if(pot_tier == 1){
					dmg_modifier = 1.30D;
				}
				if(pot_tier == 2){
					dmg_modifier = 1.50D;
				}

				dmg = (int)Math.round(((double)dmg * (double)dmg_modifier));
			}

			//log.info("SWORD_DMG - " + dmg);
			e.setDamage((double)dmg);
		}


		if(weapon.getType() == Material.WOOD_AXE || weapon.getType() == Material.STONE_AXE || weapon.getType() == Material.IRON_AXE || weapon.getType() == Material.DIAMOND_AXE || weapon.getType() == Material.GOLD_AXE){
			String dmg_data = getDamageData(weapon);
			double dmg = Double.parseDouble(dmg_data.substring(0, dmg_data.indexOf(":")));

			if(dmg_data.contains("vs_")){
				double vs_mod = 0;
				if(ent instanceof Player && dmg_data.contains("vs_players")){
					vs_mod = Double.parseDouble(dmg_data.split("vs_players=")[1].split(":")[0].replaceAll(red.toString(), ""));
					dmg = dmg + (int)((dmg * (vs_mod / 100.0D)));
				}
				if(MonsterMechanics.mob_health.containsKey(ent) && dmg_data.contains("vs_monsters")){
					vs_mod = Double.parseDouble(dmg_data.split("vs_monsters=")[1].split(":")[0].replaceAll(red.toString(), ""));
					dmg = dmg + (int)((dmg * (vs_mod / 100.0D)));
				}
			}

			if(dmg_data.contains("edmg=")){
				String elemental_type = dmg_data.split("edmg=")[1].split(":")[0].replaceAll(red.toString(), "");
				int tier = getItemTier(weapon);
				if(elemental_type.equalsIgnoreCase("fire")){
					if(tier == 1){
						ent.setFireTicks(15);
					}
					if(tier == 2){
						ent.setFireTicks(25);
					}
					if(tier == 3){
						ent.setFireTicks(30);
					}
					if(tier == 4){
						ent.setFireTicks(35);
					}
					if(tier == 5){
						ent.setFireTicks(40);
					}
				}
				if(elemental_type.equalsIgnoreCase("ice")){
					le.getWorld().playEffect(le.getLocation().add(0, 1.3, 0), Effect.POTION_BREAK, 8194);
					int mob_tier = MonsterMechanics.getMobTier(ent);
					if(!le.hasPotionEffect(PotionEffectType.SLOW)){
						if(mob_tier == 4 || mob_tier == 5){
							le.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 30, 0));
						}
						else{
							if(tier == 1){
								le.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 40, 0));
							}
							if(tier == 2){
								le.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 50, 0));
							}
							if(tier == 3){
								le.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 40, 1));
							}
							if(tier == 4){
								le.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 50, 1));
							}
							if(tier == 5){
								le.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 60, 1));
							}
						}
					}
				}
				if(elemental_type.equalsIgnoreCase("poison")){
					// TODO: Green squigglies.
					le.getWorld().playEffect(le.getLocation().add(0, 1.3, 0), Effect.POTION_BREAK, 4);
					if(tier == 1){
						le.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 40, 0));
					}
					if(tier == 2){
						le.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 50, 0));
					}
					if(tier == 3){
						le.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 60, 1));
					}
					if(tier == 4){
						le.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 70, 1));
					}
					if(tier == 5){
						le.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 80, 1));
					}
				}
			}
			if(dmg_data.contains("crit=true")){
				try {
					ParticleEffect.sendToLocation(ParticleEffect.MAGIC_CRIT, ent.getLocation(), new Random().nextFloat(), new Random().nextFloat(), new Random().nextFloat(), 1F, 50);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				//p_attacker.getWorld().spawnParticle(ent.getLocation(), Particle.MAGIC_CRIT, 1F, 50);
				p_attacker.playSound(p_attacker.getLocation(), Sound.WOOD_CLICK, 1.5F, 0.5F);
				crit = true;
			}
			if(dmg_data.contains("leech=") || ProfessionMechanics.fish_bonus_lifesteal.containsKey(p_attacker.getName())){
				double leech_amount = 0;
				if(dmg_data.contains("leech=")){
					leech_amount = Integer.parseInt(dmg_data.split("leech=")[1].split(":")[0]);
				}
				if(ProfessionMechanics.fish_bonus_lifesteal.containsKey(p_attacker.getName())){
					leech_amount += ProfessionMechanics.fish_bonus_lifesteal.get(p_attacker.getName());
				}
				double leech_val = (double) (dmg * (0.01 * leech_amount));
				if(leech_val < 1){
					leech_val = 1;
				}
				if((leech_val + HealthMechanics.getPlayerHP(p_attacker.getName())) > getMaxHP(p_attacker)){
					//HealthMechanics.setPlayerHP(p_attacker.getName(), getMaxHP(p_attacker));
					HealthMechanics.setPlayerHP(p_attacker.getName(), getMaxHP(p_attacker));
					p_attacker.setHealth(20);
				}
				else{
					//HealthMechanics.setPlayerHP(p_attacker.getName(), (int)(HealthMechanics.getPlayerHP(p_attacker.getName()) + leech_val));
					HealthMechanics.setPlayerHP(p_attacker.getName(), (int)(HealthMechanics.getPlayerHP(p_attacker.getName()) + leech_val));
					double health_percent = ((double)HealthMechanics.getPlayerHP(p_attacker.getName())) / (double)getMaxHP(p_attacker);
					double new_health_display = health_percent * 20.0D;
					p_attacker.setHealth((int)new_health_display);
				}

				double xpt = le.getLocation().getX(); //(p_attacker.getLocation().getX() + le.getLocation().getX()) / 2;
				double ypt = le.getLocation().getY(); //(p_attacker.getLocation().getY() + le.getLocation().getY()) / 2;
				double zpt = le.getLocation().getZ(); //(p_attacker.getLocation().getZ() + le.getLocation().getZ()) / 2;

				Packet particles = new PacketPlayOutWorldEvent(2001, (int)Math.round(xpt), (int)Math.round(ypt) + 1, (int)Math.round(zpt), 152, false);
				((CraftServer) Main.plugin.getServer()).getServer().getPlayerList().sendPacketNearby(p_attacker.getLocation().getX(), p_attacker.getLocation().getY(), p_attacker.getLocation().getZ(), 24, ((CraftWorld) p_attacker.getWorld()).getHandle().dimension, particles);

				if(CommunityMechanics.toggle_list.get(p_attacker.getName()).contains("debug")){
					p_attacker.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "        +" + ChatColor.GREEN + (int)leech_val + ChatColor.BOLD + " HP" + ChatColor.GRAY + " [" + (int)(HealthMechanics.getPlayerHP(p_attacker.getName())) + "/" + (int)getMaxHP(p_attacker) + "HP]");
				}

			}
			if(dmg_data.contains("knockback=true")){
				pushAwayEntity(p_attacker, ent, 1.5);
			}
			if(dmg_data.contains("blind=true") && !le.hasPotionEffect(PotionEffectType.BLINDNESS) && !le.hasMetadata("blind")){
				int tier = getItemTier(weapon);
				boolean blind = true; 

				if(le.hasMetadata("blind")){
					long last_blind = le.getMetadata("blind").get(0).asLong();
					if((System.currentTimeMillis() - last_blind) <= (10 * 1000)){
						// Less than 10 seconds, do nothing.
						blind = false;
					}
				}

				if(blind){
					if(tier == 1){
						le.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 30, 1));
					}
					if(tier == 2){
						le.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 40, 1));
					}
					if(tier == 3){
						le.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 50, 1));
					}
					if(tier == 4){
						le.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 60, 1));
					}
					if(tier == 5){
						le.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 60, 1));
					}
					le.setMetadata("blind", new FixedMetadataValue(Main.plugin, System.currentTimeMillis()));
				}
			}

			List<Integer> dmg_modifications = new ArrayList<Integer>(ItemMechanics.dmg_data.get(p_attacker.getName()));
			if(dmg_modifications.get(0) > 0){ // It's not 0, so let's do something.
				int min_dmg_mod = dmg_modifications.get(0);
				int max_dmg_mod = dmg_modifications.get(1);
				double actual_dmg_mod = 0.0D;

				if(max_dmg_mod - min_dmg_mod <= 0){
					actual_dmg_mod = max_dmg_mod;
				}
				else{
					actual_dmg_mod = new Random().nextInt(max_dmg_mod - min_dmg_mod) + min_dmg_mod;
				}
				// This will be a % number, for ex. 5%.
				double percent_dmg_mod = actual_dmg_mod / 100.0D; 

				dmg += (int)(double)((double)dmg * (double)percent_dmg_mod);
			}

			if(str_data.get(p_attacker.getName()) > 0 || dmg_data.contains("str=")){
				double str_mod = 0;
				if(str_data.get(p_attacker.getName()) > 0){
					str_mod = str_data.get(p_attacker.getName());
				}
				if(dmg_data.contains("str=")){
					str_mod += Double.parseDouble(dmg_data.split("str=")[1].split(":")[0]);
				}
				dmg += (int)(double)((double)dmg * (double)(((str_mod * 0.015)/100.0D)));
			}

			if(ProfessionMechanics.fish_bonus_dmg.containsKey(p_attacker.getName())){
				dmg += (dmg * (ProfessionMechanics.fish_bonus_dmg.get(p_attacker.getName()) / 100.0D));
			}

			if(crit == true){
				dmg = (int) Math.round((double)dmg * 2);
			}

			if(p_attacker.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE)){
				double dmg_modifier = 1.10D;
				int pot_tier = 1;
				for(PotionEffect pe : p_attacker.getActivePotionEffects()){
					if(pe.getType() == PotionEffectType.INCREASE_DAMAGE){
						pot_tier = pe.getAmplifier(); break;
					}
				}
				if(pot_tier == 1){
					dmg_modifier = 1.10D;
				}
				if(pot_tier == 2){
					dmg_modifier = 1.30D;
				}
				if(pot_tier == 3){
					dmg_modifier = 1.50D;
				}

				dmg = (int)Math.round(((double)dmg * (double)dmg_modifier));
			}

			e.setDamage((double)dmg);
		}

		if(weapon.getType() == Material.WOOD_SPADE || weapon.getType() == Material.STONE_SPADE || weapon.getType() == Material.IRON_SPADE || weapon.getType() == Material.DIAMOND_SPADE || weapon.getType() == Material.GOLD_SPADE){

			processing_dmg_event.add(p_attacker.getName());

			String dmg_data = getDamageData(weapon);
			double dmg = Double.parseDouble(dmg_data.substring(0, dmg_data.indexOf(":")));

			if(dmg_data.contains("vs_")){
				double vs_mod = 0;
				if(ent instanceof Player && dmg_data.contains("vs_players")){
					vs_mod = Double.parseDouble(dmg_data.split("vs_players=")[1].split(":")[0].replaceAll(red.toString(), ""));
					dmg = dmg + (int)((dmg * (vs_mod / 100.0D)));
				}
				if(MonsterMechanics.mob_health.containsKey(ent) && dmg_data.contains("vs_monsters")){
					vs_mod = Double.parseDouble(dmg_data.split("vs_monsters=")[1].split(":")[0].replaceAll(red.toString(), ""));
					dmg = dmg + (int)((dmg * (vs_mod / 100.0D)));
				}
			}

			if(dmg_data.contains("edmg=")){
				String elemental_type = dmg_data.split("edmg=")[1].split(":")[0].replaceAll(red.toString(), "");
				int tier = getItemTier(weapon);
				if(elemental_type.equalsIgnoreCase("fire")){
					if(tier == 1){
						ent.setFireTicks(15);
					}
					if(tier == 2){
						ent.setFireTicks(25);
					}
					if(tier == 3){
						ent.setFireTicks(30);
					}
					if(tier == 4){
						ent.setFireTicks(35);
					}
					if(tier == 5){
						ent.setFireTicks(40);
					}
				}
				if(elemental_type.equalsIgnoreCase("ice")){
					le.getWorld().playEffect(le.getLocation().add(0, 1.3, 0), Effect.POTION_BREAK, 8194);
					int mob_tier = MonsterMechanics.getMobTier(ent);
					if(!le.hasPotionEffect(PotionEffectType.SLOW)){
						if(mob_tier == 4 || mob_tier == 5){
							le.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 30, 0));
						}
						else{
							if(tier == 1){
								le.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 40, 0));
							}
							if(tier == 2){
								le.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 50, 0));
							}
							if(tier == 3){
								le.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 40, 1));
							}
							if(tier == 4){
								le.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 50, 1));
							}
							if(tier == 5){
								le.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 60, 1));
							}
						}
					}
				}
				if(elemental_type.equalsIgnoreCase("poison")){
					// TODO: Green squigglies.
					le.getWorld().playEffect(le.getLocation().add(0, 1.3, 0), Effect.POTION_BREAK, 4);
					if(tier == 1){
						le.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 40, 0));
					}
					if(tier == 2){
						le.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 50, 0));
					}
					if(tier == 3){
						le.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 60, 1));
					}
					if(tier == 4){
						le.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 70, 1));
					}
					if(tier == 5){
						le.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 80, 1));
					}
				}
			}
			if(dmg_data.contains("crit=true")){
				try {
					ParticleEffect.sendToLocation(ParticleEffect.MAGIC_CRIT, ent.getLocation(), new Random().nextFloat(), new Random().nextFloat(), new Random().nextFloat(), 1F, 50);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				//p_attacker.getWorld().spawnParticle(ent.getLocation(), Particle.MAGIC_CRIT, 1F, 50);
				p_attacker.playSound(p_attacker.getLocation(), Sound.WOOD_CLICK, 1.5F, 0.5F);
				crit = true;
			}
			if(dmg_data.contains("leech=") || ProfessionMechanics.fish_bonus_lifesteal.containsKey(p_attacker.getName())){
				double leech_amount = 0;
				if(dmg_data.contains("leech=")){
					leech_amount = Integer.parseInt(dmg_data.split("leech=")[1].split(":")[0]);
				}
				if(ProfessionMechanics.fish_bonus_lifesteal.containsKey(p_attacker.getName())){
					leech_amount += ProfessionMechanics.fish_bonus_lifesteal.get(p_attacker.getName());
				}
				double leech_val = (double) (dmg * (0.01 * leech_amount));
				if(leech_val < 1){
					leech_val = 1;
				}
				if((leech_val + HealthMechanics.getPlayerHP(p_attacker.getName())) > getMaxHP(p_attacker)){
					//HealthMechanics.setPlayerHP(p_attacker.getName(), getMaxHP(p_attacker));
					HealthMechanics.setPlayerHP(p_attacker.getName(), getMaxHP(p_attacker));
					p_attacker.setHealth(20);
				}
				else{
					//HealthMechanics.setPlayerHP(p_attacker.getName(), (int)(HealthMechanics.getPlayerHP(p_attacker.getName()) + leech_val));
					HealthMechanics.setPlayerHP(p_attacker.getName(), (int)(HealthMechanics.getPlayerHP(p_attacker.getName()) + leech_val));
					double health_percent = ((double)HealthMechanics.getPlayerHP(p_attacker.getName())) / (double)getMaxHP(p_attacker);
					double new_health_display = health_percent * 20.0D;
					p_attacker.setHealth((int)new_health_display);
				}

				double xpt = le.getLocation().getX(); //(p_attacker.getLocation().getX() + le.getLocation().getX()) / 2;
				double ypt = le.getLocation().getY(); //(p_attacker.getLocation().getY() + le.getLocation().getY()) / 2;
				double zpt = le.getLocation().getZ(); //(p_attacker.getLocation().getZ() + le.getLocation().getZ()) / 2;

				Packet particles = new PacketPlayOutWorldEvent(2001, (int)Math.round(xpt), (int)Math.round(ypt) + 1, (int)Math.round(zpt), 152, false);
				((CraftServer) Main.plugin.getServer()).getServer().getPlayerList().sendPacketNearby(p_attacker.getLocation().getX(), p_attacker.getLocation().getY(), p_attacker.getLocation().getZ(), 24, ((CraftWorld) p_attacker.getWorld()).getHandle().dimension, particles);

				if(CommunityMechanics.toggle_list.get(p_attacker.getName()).contains("debug")){
					p_attacker.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "        +" + ChatColor.GREEN + (int)leech_val + ChatColor.BOLD + " HP" + ChatColor.GRAY + " [" + (int)(HealthMechanics.getPlayerHP(p_attacker.getName())) + "/" + (int)getMaxHP(p_attacker) + "HP]");
				}

			}
			if(dmg_data.contains("knockback=true")){
				pushAwayEntity(p_attacker, ent, 1.5);
			}
			if(dmg_data.contains("blind=true") && !le.hasPotionEffect(PotionEffectType.BLINDNESS) && !le.hasMetadata("blind")){
				int tier = getItemTier(weapon);
				boolean blind = true; 

				if(le.hasMetadata("blind")){
					long last_blind = le.getMetadata("blind").get(0).asLong();
					if((System.currentTimeMillis() - last_blind) <= (10 * 1000)){
						// Less than 10 seconds, do nothing.
						blind = false;
					}
				}

				if(blind){
					if(tier == 1){
						le.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 30, 1));
					}
					if(tier == 2){
						le.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 40, 1));
					}
					if(tier == 3){
						le.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 50, 1));
					}
					if(tier == 4){
						le.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 60, 1));
					}
					if(tier == 5){
						le.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 60, 1));
					}
					le.setMetadata("blind", new FixedMetadataValue(Main.plugin, System.currentTimeMillis()));
				}
			}

			List<Integer> dmg_modifications = new ArrayList<Integer>(ItemMechanics.dmg_data.get(p_attacker.getName()));
			if(dmg_modifications.get(0) > 0){ // It's not 0, so let's do something.
				int min_dmg_mod = dmg_modifications.get(0);
				int max_dmg_mod = dmg_modifications.get(1);
				double actual_dmg_mod = 0.0D;

				if(max_dmg_mod - min_dmg_mod <= 0){
					actual_dmg_mod = max_dmg_mod;
				}
				else{
					actual_dmg_mod = new Random().nextInt(max_dmg_mod - min_dmg_mod) + min_dmg_mod;
				}
				// This will be a % number, for ex. 5%.
				double percent_dmg_mod = actual_dmg_mod / 100.0D; 

				dmg += (int)(double)((double)dmg * (double)percent_dmg_mod);
			}

			if(str_data.get(p_attacker.getName()) > 0 || dmg_data.contains("str=")){
				double str_mod = 0;
				if(str_data.get(p_attacker.getName()) > 0){
					str_mod = str_data.get(p_attacker.getName());
				}
				if(dmg_data.contains("str=")){
					str_mod += Double.parseDouble(dmg_data.split("str=")[1].split(":")[0]);
				}
				dmg += (int)(double)((double)dmg * (double)(((str_mod * 0.015)/100.0D)));
			}

			if(ProfessionMechanics.fish_bonus_dmg.containsKey(p_attacker.getName())){
				dmg += (dmg * (ProfessionMechanics.fish_bonus_dmg.get(p_attacker.getName()) / 100.0D));
			}

			if(crit == true){
				dmg = (int) Math.round((double)dmg * 2);
			}

			if(p_attacker.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE)){
				double dmg_modifier = 1.10D;
				int pot_tier = 1;
				for(PotionEffect pe : p_attacker.getActivePotionEffects()){
					if(pe.getType() == PotionEffectType.INCREASE_DAMAGE){
						pot_tier = pe.getAmplifier(); break;
					}
				}
				if(pot_tier == 1){
					dmg_modifier = 1.10D;
				}
				if(pot_tier == 2){
					dmg_modifier = 1.30D;
				}
				if(pot_tier == 3){
					dmg_modifier = 1.50D;
				}

				dmg = (int)Math.round(((double)dmg * (double)dmg_modifier));
			}

			e.setDamage((double)dmg);
			pushAwayEntity(p_attacker, ent, 1.5F);

			if(dmg > 0){
				//List<Entity> aoe = getEntitiesInCone(p_attacker.getNearbyEntities(6, 4, 6), p_attacker.getLocation(), 5, (int)90, (int)p_attacker.getLocation().getYaw());
				List<Entity> aoe = e.getEntity().getNearbyEntities(2.5, 3, 2.5);
				//int count = 0;
				for(Entity aoe_ent : aoe){
					/*if(count > 5){
						processing_dmg_event.remove(p_attacker.getName());
						return; // TODO: Want to make this tier based?
					}*/
					// This creates infinite loop.
					if(aoe_ent instanceof LivingEntity && MonsterMechanics.mob_health.containsKey((LivingEntity)aoe_ent)){
						LivingEntity le_ent = (LivingEntity) aoe_ent;
						FatigueMechanics.last_attack.remove(p_attacker.getName());
						le_ent.damage(dmg, p_attacker); // TODO: Multiple debug messages

						final LivingEntity f_ent = le_ent;
						final LivingEntity f_attacker = p_attacker;

						Main.plugin.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable(){
							public void run() {
								f_ent.setVelocity(new Vector());

								// Get velocity unit vector:
								org.bukkit.util.Vector unitVector = f_ent.getLocation().toVector().subtract(f_ent.getLocation().toVector()).normalize();
								// Set speed and push entity:

								if(f_attacker != null){
									unitVector = f_ent.getLocation().toVector().subtract(f_attacker.getLocation().toVector()).normalize();

									f_ent.setVelocity(unitVector.multiply(0.10D));
									if(f_ent != null){
										f_ent.playEffect(EntityEffect.HURT);
									}
								}

							}
						}, 1L);

						//pushAwayEntity(p_attacker, le_ent, 1.5F);
						//count++;
					}
				}
			}

			processing_dmg_event.remove(p_attacker.getName());
		}

		if(weapon.getType() == Material.WOOD_HOE || weapon.getType() == Material.STONE_HOE || weapon.getType() == Material.IRON_HOE || weapon.getType() == Material.DIAMOND_HOE || weapon.getType() == Material.GOLD_HOE){
			String dmg_data = getDamageData(weapon);
			double dmg = Double.parseDouble(dmg_data.substring(0, dmg_data.indexOf(":")));

			if(dmg_data.contains("vs_")){
				double vs_mod = 0;
				if(ent instanceof Player && dmg_data.contains("vs_players")){
					vs_mod = Double.parseDouble(dmg_data.split("vs_players=")[1].split(":")[0].replaceAll(red.toString(), ""));
					dmg = dmg + (int)((dmg * (vs_mod / 100.0D)));
				}
				if(MonsterMechanics.mob_health.containsKey(ent) && dmg_data.contains("vs_monsters")){
					vs_mod = Double.parseDouble(dmg_data.split("vs_monsters=")[1].split(":")[0].replaceAll(red.toString(), ""));
					dmg = dmg + (int)((dmg * (vs_mod / 100.0D)));
				}
			}

			if(dmg_data.contains("edmg=")){
				String elemental_type = dmg_data.split("edmg=")[1].split(":")[0].replaceAll(red.toString(), "");
				int tier = getItemTier(weapon);
				if(elemental_type.equalsIgnoreCase("fire")){
					if(tier == 1){
						ent.setFireTicks(15);
					}
					if(tier == 2){
						ent.setFireTicks(25);
					}
					if(tier == 3){
						ent.setFireTicks(30);
					}
					if(tier == 4){
						ent.setFireTicks(35);
					}
					if(tier == 5){
						ent.setFireTicks(40);
					}
				}
				if(elemental_type.equalsIgnoreCase("ice")){
					le.getWorld().playEffect(le.getLocation().add(0, 1.3, 0), Effect.POTION_BREAK, 8194);
					int mob_tier = MonsterMechanics.getMobTier(ent);
					if(!le.hasPotionEffect(PotionEffectType.SLOW)){
						if(mob_tier == 4 || mob_tier == 5){
							le.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 30, 0));
						}
						else{
							if(tier == 1){
								le.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 40, 0));
							}
							if(tier == 2){
								le.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 50, 0));
							}
							if(tier == 3){
								le.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 40, 1));
							}
							if(tier == 4){
								le.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 50, 1));
							}
							if(tier == 5){
								le.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 60, 1));
							}
						}
					}
				}
				if(elemental_type.equalsIgnoreCase("poison")){
					// TODO: Green squigglies.
					le.getWorld().playEffect(le.getLocation().add(0, 1.3, 0), Effect.POTION_BREAK, 4);
					if(tier == 1){
						le.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 40, 0));
					}
					if(tier == 2){
						le.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 50, 0));
					}
					if(tier == 3){
						le.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 60, 1));
					}
					if(tier == 4){
						le.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 70, 1));
					}
					if(tier == 5){
						le.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 80, 1));
					}
				}
			}
			if(dmg_data.contains("crit=true")){
				try {
					ParticleEffect.sendToLocation(ParticleEffect.MAGIC_CRIT, ent.getLocation(), new Random().nextFloat(), new Random().nextFloat(), new Random().nextFloat(), 1F, 50);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				p_attacker.playSound(p_attacker.getLocation(), Sound.WOOD_CLICK, 1.5F, 0.5F);
				crit = true;
			}
			if(dmg_data.contains("leech=") || ProfessionMechanics.fish_bonus_lifesteal.containsKey(p_attacker.getName())){
				double leech_amount = 0;
				if(dmg_data.contains("leech=")){
					leech_amount = Integer.parseInt(dmg_data.split("leech=")[1].split(":")[0]);
				}
				if(ProfessionMechanics.fish_bonus_lifesteal.containsKey(p_attacker.getName())){
					leech_amount += ProfessionMechanics.fish_bonus_lifesteal.get(p_attacker.getName());
				}
				double leech_val = (double) (dmg * (0.01 * leech_amount));
				if(leech_val < 1){
					leech_val = 1;
				}
				if((leech_val + HealthMechanics.getPlayerHP(p_attacker.getName())) > getMaxHP(p_attacker)){
				    if(p_attacker.isDead())return;
					HealthMechanics.setPlayerHP(p_attacker.getName(), getMaxHP(p_attacker));
					p_attacker.setHealth(20);
				}
				else{
					HealthMechanics.setPlayerHP(p_attacker.getName(), (int)(HealthMechanics.getPlayerHP(p_attacker.getName()) + leech_val));
					double health_percent = ((double)HealthMechanics.getPlayerHP(p_attacker.getName())) / (double)getMaxHP(p_attacker);
					double new_health_display = health_percent * 20.0D;
					p_attacker.setHealth((int)new_health_display);
				}

				double xpt = le.getLocation().getX(); //(p_attacker.getLocation().getX() + le.getLocation().getX()) / 2;
				double ypt = le.getLocation().getY(); //(p_attacker.getLocation().getY() + le.getLocation().getY()) / 2;
				double zpt = le.getLocation().getZ(); //(p_attacker.getLocation().getZ() + le.getLocation().getZ()) / 2;

				Packet particles = new PacketPlayOutWorldEvent(2001, (int)Math.round(xpt), (int)Math.round(ypt) + 1, (int)Math.round(zpt), 152, false);
				((CraftServer) Main.plugin.getServer()).getServer().getPlayerList().sendPacketNearby(p_attacker.getLocation().getX(), p_attacker.getLocation().getY(), p_attacker.getLocation().getZ(), 24, ((CraftWorld) p_attacker.getWorld()).getHandle().dimension, particles);

				if(CommunityMechanics.toggle_list.get(p_attacker.getName()).contains("debug")){
					p_attacker.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "        +" + ChatColor.GREEN + (int)leech_val + ChatColor.BOLD + " HP" + ChatColor.GRAY + " [" + (int)(HealthMechanics.getPlayerHP(p_attacker.getName())) + "/" + (int)getMaxHP(p_attacker) + "HP]");
				}

			}
			if(dmg_data.contains("knockback=true")){
				pushAwayEntity(p_attacker, ent, 1.5);
			}
			if(dmg_data.contains("blind=true") && !le.hasPotionEffect(PotionEffectType.BLINDNESS) && !le.hasMetadata("blind")){
				int tier = getItemTier(weapon);
				boolean blind = true; 

				if(le.hasMetadata("blind")){
					long last_blind = le.getMetadata("blind").get(0).asLong();
					if((System.currentTimeMillis() - last_blind) <= (10 * 1000)){
						// Less than 10 seconds, do nothing.
						blind = false;
					}
				}

				if(blind){
					if(tier == 1){
						le.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 30, 1));
					}
					if(tier == 2){
						le.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 40, 1));
					}
					if(tier == 3){
						le.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 50, 1));
					}
					if(tier == 4){
						le.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 60, 1));
					}
					if(tier == 5){
						le.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 60, 1));
					}
					le.setMetadata("blind", new FixedMetadataValue(Main.plugin, System.currentTimeMillis()));
				}
			}

			List<Integer> dmg_modifications = new ArrayList<Integer>(ItemMechanics.dmg_data.get(p_attacker.getName()));
			if(dmg_modifications.get(0) > 0){ // It's not 0, so let's do something.
				int min_dmg_mod = dmg_modifications.get(0);
				int max_dmg_mod = dmg_modifications.get(1);
				double actual_dmg_mod = 0.0D;

				if(max_dmg_mod - min_dmg_mod <= 0){
					actual_dmg_mod = max_dmg_mod;
				}
				else{
					actual_dmg_mod = new Random().nextInt(max_dmg_mod - min_dmg_mod) + min_dmg_mod;
				}
				// This will be a % number, for ex. 5%.
				double percent_dmg_mod = actual_dmg_mod / 100.0D; 

				dmg += (int)(double)((double)dmg * (double)percent_dmg_mod);
			}

			if(int_data.get(p_attacker.getName()) > 0 || dmg_data.contains("int=")){
				double int_mod = 0;
				if(int_data.get(p_attacker.getName()) > 0){
					int_mod = int_data.get(p_attacker.getName());
				}
				if(dmg_data.contains("int=")){
					int_mod += Double.parseDouble(dmg_data.split("int=")[1].split(":")[0]);
				}
				dmg += (int)(double)((double)dmg * (double)(((int_mod * 0.015)/100.0D)));
			}

			if(ProfessionMechanics.fish_bonus_dmg.containsKey(p_attacker.getName())){
				dmg += (dmg * (ProfessionMechanics.fish_bonus_dmg.get(p_attacker.getName()) / 100.0D));
			}

			if(crit == true){
				dmg = (int) Math.round((double)dmg * 2);
			}

			if(p_attacker.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE)){
				double dmg_modifier = 1.10D;
				int pot_tier = 1;
				for(PotionEffect pe : p_attacker.getActivePotionEffects()){
					if(pe.getType() == PotionEffectType.INCREASE_DAMAGE){
						pot_tier = pe.getAmplifier(); break;
					}
				}
				if(pot_tier == 1){
					dmg_modifier = 1.10D;
				}
				if(pot_tier == 2){
					dmg_modifier = 1.30D;
				}
				if(pot_tier == 3){
					dmg_modifier = 1.50D;
				}

				dmg = (int)Math.round(((double)dmg * (double)dmg_modifier));
			}

			e.setDamage((double)dmg);

			// This is a wand, so we need to play a cute effect.
			try {
				ParticleEffect.sendToLocation(ParticleEffect.MAGIC_CRIT, ent.getLocation(), new Random().nextFloat(), new Random().nextFloat(), new Random().nextFloat(), 0.5F, 20);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			//p_attacker.getWorld().spawnParticle(e.getEntity().getLocation().add(0, 1.5, 0), Particle.MAGIC_CRIT, 0.5F, 20);
		}
	}

	/*public void shootFireball(Double speed, Location shootLocation) {
		org.bukkit.util.Vector directionVector = shootLocation.getDirection().normalize();
		double startShift = 2;
		Vector shootShiftVector = new Vector(directionVector.getX() * startShift, directionVector.getY() * startShift, directionVector.getZ() * startShift);
		shootLocation = shootLocation.add(shootShiftVector.getX(), shootShiftVector.getY(), shootShiftVector.getZ());

		Fireball fireballl = shootLocation.getWorld().spawn(shootLocation, Fireball.class);
		fireballl.setVelocity(directionVector.multiply(speed));

		if(fireballl instanceof Fireball){
			((Fireball) fireballl).setIsIncendiary(false);// Remove fire
			((Fireball) fireballl).setShooter(this.player.getPlayer());
		}
	}*/

	@EventHandler
	public void onPlayerTeleportEvent(PlayerTeleportEvent e){
		if(e.getCause() == TeleportCause.ENDER_PEARL){
			e.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityExplodePrimeEvent(ExplosionPrimeEvent e){
		if(e.getEntity() instanceof WitherSkull){
			Projectile p = (Projectile)e.getEntity();
			if(p.getShooter() instanceof Player){
				e.setFire(false);
				e.setRadius(0.0F);
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityExplodeEvent(EntityExplodeEvent e){
		if(e.getEntity() instanceof Fireball || e.getEntity() instanceof SmallFireball || e.getEntity() instanceof LargeFireball || e.getEntity() instanceof WitherSkull){
			e.setCancelled(true);
			e.setYield(0);
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onStaffProjectHit(ProjectileHitEvent e){
		Projectile proj = e.getEntity();
		LivingEntity target = null;

		if(!ProfessionMechanics.transparent.contains((byte)proj.getLocation().getBlock().getTypeId())){
			return; // Solid block, don't hit through walls.
		}

		if(projectile_map.containsKey(proj)){
			if(projectile_map.get(proj) == null && ((CraftWorld) proj.getShooter()).hasMetadata("boss_type")){
				// TNT_bandit shot this.
				Location target_loc = proj.getLocation();
				//target_loc.getWorld().createExplosion(target_loc.getX(), target_loc.getY() + 2, target_loc.getZ(), 5.0F, false, false);
				try {
					ParticleEffect.sendToLocation(ParticleEffect.LARGE_EXPLODE, target_loc.add(0, 1, 0), new Random().nextFloat(), new Random().nextFloat(), new Random().nextFloat(), 1F, 8);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				//target_loc.getWorld().spawnParticle(target_loc.add(0, 1, 0), Particle.LARGE_EXPLODE, 1F, 8);
				target_loc.getWorld().playSound(target_loc, Sound.EXPLODE, 2F, 1F);
				for(Entity ent : proj.getNearbyEntities(3, 3, 3)){
					if(ent instanceof Player){
						Player pl = (Player)ent;
						double max_hp = HealthMechanics.getMaxHealthValue(pl.getName());
						double dmg_lower = max_hp * 0.10D;
						double dmg_upper = max_hp * 0.20D;
						double dmg = new Random().nextInt((int) (dmg_upper - dmg_lower)) + dmg_lower;
						pl.damage(dmg, (Entity) proj.getShooter());
					}
				}
				projectile_map.remove(proj);
				return;
			}
			for(Entity ent : proj.getNearbyEntities(2, 1.5, 2)){
				if(ent instanceof Player || (ent instanceof LivingEntity && MonsterMechanics.mob_health.containsKey((LivingEntity)ent))){
					if(proj.getShooter() instanceof LivingEntity && MonsterMechanics.mob_health.containsKey((LivingEntity)proj.getShooter())){
						// If the shooter is a mob, don't let it damage another mob.
						if((ent instanceof LivingEntity && MonsterMechanics.mob_health.containsKey((LivingEntity)ent))){
							continue;
						}
					}
					if(ent instanceof Player){
						Player ptarget = (Player)ent;
						if(proj.getShooter() instanceof Player){
							Player pshooter = (Player)proj.getShooter();
							if(ptarget.getName().equalsIgnoreCase(((Player)proj.getShooter()).getName())){
								continue; // Don't hit urself.
							}
							if(DuelMechanics.isPvPDisabled(ptarget.getLocation()) && !(DuelMechanics.duel_map.containsKey(pshooter.getName()) && DuelMechanics.duel_map.get(pshooter.getName()).equalsIgnoreCase(ptarget.getName()))){
								continue; // Do not deal damage in PvP off zone.
							}
							/*String target_align = KarmaMechanics.getRawAlignment(ptarget.getName());
							String shooter_align = KarmaMechanics.getRawAlignment(pshooter.getName());

							if(target_align.equalsIgnoreCase("good") && CommunityMechanics.toggle_list.containsKey(pshooter.getName()))*/
						}
						if(DuelMechanics.isDamageDisabled(ent.getLocation())){
							// We need to check if the target is in a duel with the shooter.
							if(proj.getShooter() instanceof Player){
								// The projectile was shot by a player.
								Player pshoot = (Player)proj.getShooter();
								if(DuelMechanics.duel_map.containsKey(pshoot.getName()) && DuelMechanics.duel_map.get(pshoot.getName()).equalsIgnoreCase(ptarget.getName())){
									// They're in a duel.
									target = (LivingEntity)ent;
									break;
								}
							}

							// If the code reaches this point, we can't use them.
							continue;
						}
					}
					target = (LivingEntity)ent;
					break;
				}
			}
		}

		if(target != null){

			ItemStack wep = projectile_map.get(proj);
			if(!wep.getType().name().toLowerCase().contains("hoe")){
				return;
			}

			if(proj instanceof WitherSkull){
				//return;
				//log.info(proj.toString());
				//log.info(wep.toString());
			}

			if(proj instanceof SmallFireball){
				// Play the effect for it.
				try {
					ParticleEffect.sendToLocation(ParticleEffect.BUBBLE, e.getEntity().getLocation(), new Random().nextFloat(), new Random().nextFloat(), new Random().nextFloat(), 1F, 10);
				} catch (Exception e1) {e1.printStackTrace();}
				//e.getEntity().getWorld().spawnParticle(e.getEntity().getLocation(), Particle.BUBBLE, 1F, 10);
				e.getEntity().getWorld().playSound(e.getEntity().getLocation(), Sound.EXPLODE, 1F, 1F);
			}

			if(proj instanceof EnderPearl){
				try {
					ParticleEffect.sendToLocation(ParticleEffect.WITCH_MAGIC, e.getEntity().getLocation(), new Random().nextFloat(), new Random().nextFloat(), new Random().nextFloat(), 0.5F, 10);
				} catch (Exception e1) {e1.printStackTrace();}
				//e.getEntity().getWorld().spawnParticle(e.getEntity().getLocation(), Particle.WITCH_MAGIC, 0.50F, 10);
				e.getEntity().getWorld().playSound(e.getEntity().getLocation(), Sound.ENDERMAN_TELEPORT, 2F, 1.50F);
			}

			projectile_map.remove(proj);
			// Ok what we'll do is call damage event from the player who shot, then have a hashmap that overrides the weapon they have in hand. Woo.
			LivingEntity shooter = (LivingEntity) proj.getShooter();
			ItemStack wep_is = new ItemStack(Material.WOOD_SWORD);
			wep_is.setItemMeta(wep.getItemMeta());
			String dmg_data = getDamageData(wep_is);
			double dmg = Double.parseDouble(dmg_data.substring(0, dmg_data.indexOf(":")));

			if(shooter instanceof Player){
				Player pshooter = (Player)shooter;
				if(target instanceof Player){
					Player ptarget = (Player)target;
					if(pshooter.getName().equalsIgnoreCase(ptarget.getName())){
						projectile_map.remove(proj);
						return; // Don't hit yourself, foo.
					}
				}
				processing_proj_event.add(pshooter.getName());
				spoofed_weapon.put(pshooter.getName(), wep);
				target.damage(dmg, pshooter);
				spoofed_weapon.remove(pshooter.getName());
				processing_proj_event.remove(pshooter.getName());

				final Entity f_ent = target;
				final Entity f_attacker = pshooter;
				Main.plugin.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable(){
					public void run() {
						f_ent.setVelocity(new org.bukkit.util.Vector());
						// Get velocity unit vector:
						org.bukkit.util.Vector unitVector = f_ent.getLocation().toVector().subtract(f_ent.getLocation().toVector()).normalize();
						// Set speed and push entity:

						if(f_attacker != null){
							unitVector = f_ent.getLocation().toVector().subtract(f_attacker.getLocation().toVector()).normalize();

							f_ent.setVelocity(unitVector.multiply(0.10D));
							if(f_ent != null){
								f_ent.playEffect(EntityEffect.HURT);
							}
						}
					}
				}, 1L);
			}
			else{
				target.damage(dmg, shooter);
			}
		}
		projectile_map.remove(proj);
	}

	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerCancelled(InventoryClickEvent e){
	    if(e.isCancelled()){
	        ((Player)e.getWhoClicked()).updateInventory();
	    }
	}
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onPlayerShootWand(PlayerInteractEvent e){
		Player pl = e.getPlayer();

		if(!e.hasItem()){
			return;
		}

		ItemStack wep = e.getItem();
		if((e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) && (wep.getType() == Material.WOOD_HOE || wep.getType() == Material.STONE_HOE || wep.getType() == Material.IRON_HOE || wep.getType() == Material.DIAMOND_HOE || wep.getType() == Material.GOLD_HOE)){
			if(getDamageData(wep).equalsIgnoreCase("no") || (DuelMechanics.isDamageDisabled(pl.getLocation()) && !DuelMechanics.duel_map.containsKey(pl.getName()))){
				//pl.getWorld().spawnParticle(pl.getTargetBlock(null, 2).getLocation(), Particle.MAGIC_CRIT, 0.50F, 20);
				try {
					ParticleEffect.sendToLocation(ParticleEffect.MAGIC_CRIT, pl.getTargetBlock(null, 2).getLocation(), new Random().nextFloat(), new Random().nextFloat(), new Random().nextFloat(), 0.5F, 20);
				} catch (Exception e1) {e1.printStackTrace();}
				pl.playSound(pl.getLocation(), Sound.FIZZ, 1.0F, 1.25F);
				return; // Not a weapon, or in a safe zone.
			}

			if(pl.getExp() == 0.0F || FatigueMechanics.fatigue_effect.contains(pl.getName())){
				return;
			}

			if(FatigueMechanics.last_attack.containsKey(pl.getName()) && (System.currentTimeMillis() - FatigueMechanics.last_attack.get(pl.getName())) < 100){
				// Less than 100ms since last attack.
				e.setCancelled(true);
				return;
			}
			if(pl.isInsideVehicle()){
			    e.setCancelled(true);
			    return;
			}
			Projectile pj = null;

			if(getItemTier(wep) == 1){
				pj = pl.launchProjectile(Snowball.class);
				/*final org.bukkit.util.Vector direction = pl.getEyeLocation().getDirection().multiply(1.0);
			pj = (Projectile)pl.getWorld().spawn(pl.getEyeLocation().add(direction.getX(), direction.getY(), direction.getZ()), Snowball.class);
			pj.setShooter(pl);
			pj.setVelocity(direction);*/
			}
			if(getItemTier(wep) == 2){
				pj = pl.launchProjectile(SmallFireball.class);
				pj.setBounce(false);
				pj.setVelocity(pj.getVelocity().multiply(1.25));
				/*final org.bukkit.util.Vector direction = pl.getEyeLocation().getDirection().multiply(1.0);
			pj = (Projectile)pl.getWorld().spawn(pl.getEyeLocation().add(direction.getX(), direction.getY(), direction.getZ()), SmallFireball.class);
			pj.setShooter(pl);
			pj.setVelocity(direction);*/
			}
			if(getItemTier(wep) == 3){
				pj = pl.launchProjectile(EnderPearl.class);
				pj.setVelocity(pj.getVelocity().multiply(1.25));
				/*final org.bukkit.util.Vector direction = pl.getEyeLocation().getDirection().multiply(1.5);
			pj = (Projectile)pl.getWorld().spawn(pl.getEyeLocation().add(direction.getX(), direction.getY(), direction.getZ()), EnderPearl.class);
			pj.setShooter(pl);
			pj.setVelocity(direction);*/
			}
			if(getItemTier(wep) == 4){
				pj = pl.launchProjectile(WitherSkull.class);
				pj.setVelocity(pj.getVelocity().multiply(1.5));
				/*final org.bukkit.util.Vector direction = pl.getEyeLocation().getDirection().multiply(1.0);
			pj = (Projectile)pl.getWorld().spawn(pl.getEyeLocation().add(direction.getX(), direction.getY(), direction.getZ()), WitherSkull.class);
			pj.setShooter(pl);
			pj.setBounce(false);
			pj.setVelocity(direction);*/
			}
			if(getItemTier(wep) == 5){
				pj = pl.launchProjectile(SmallFireball.class);
				pj.setBounce(false);
				pj.setVelocity(pj.getVelocity().multiply(1.5));
				/*final org.bukkit.util.Vector direction = pl.getEyeLocation().getDirection().multiply(1.5);
				pj = (Projectile)pl.getWorld().spawn(pl.getEyeLocation().add(direction.getX(), direction.getY(), direction.getZ()), LargeFireball.class);
				pj.setShooter(pl);
				pj.setBounce(false);
				pj.setVelocity(direction);*/
			}

			projectile_map.put(pj, wep);
			pl.playSound(pl.getLocation(), Sound.SHOOT_ARROW, 1F, 0.25F);
			FatigueMechanics.removeEnergy(pl, FatigueMechanics.getEnergyCost(wep));

			if(DuelMechanics.duel_map.containsKey(pl.getName())){return;}
			RepairMechanics.subtractCustomDurability(pl, wep, 1, "wep");

			// Whatever projectile is used, we'll store data in a hashmap, use a repeating task to process it, then when it hits a player run a .damage event with the player as the responsible party.
			// This way, it will be treated as a melee attack even though it's ranged.

			// Arrow, Egg, EnderPearl, Fireball, Fish, LargeFireball, SmallFireball, Snowball, ThrownExpBottle, ThrownPotion, WitherSkull
		}
	}

	public static ItemStack setToHealingPotion(net.minecraft.server.v1_7_R1.ItemStack i){
		if(i == null){
			log.info("[ItemMechanics] NULL itemStack on setToHealingPotion()");
			return CraftItemStack.asBukkitCopy(i);
		}
		
		try{
			NBTTagCompound tag = new NBTTagCompound();
			tag.setByte("Id", (byte) 6);
			((NBTTagList)i.tag.get("CustomPotionEffects")).add(tag);
		} catch(NullPointerException npe){
			return CraftItemStack.asBukkitCopy(i);
		}
		return CraftItemStack.asBukkitCopy(i);
	}

	public static ItemStack signNewCustomItem(Material m, short meta_data, String name, String desc){
		ItemStack is = new ItemStack(m, 1, meta_data);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(name);
		
		List<String> new_lore = new ArrayList<String>();
		if(desc.contains(",")){
			for(String s : desc.split(",")){
				new_lore.add(s);
			}
		}
		else{
			new_lore.add(desc);
		}
		
		im.setLore(new_lore);
		is.setItemMeta(im);
		
		/*net.minecraft.server.v1_7_R1.ItemStack nms = CraftItemStack.asNMSCopy(iss);

		NBTTagCompound tag = nms.tag;
		tag = new NBTTagCompound();
		tag.setCompound("display", new NBTTagCompound());
		nms.tag = tag;
		tag = nms.tag.getCompound("display");

		for(String s : desc.split(",")){
			if(s.length() <= 1){continue;}
			NBTTagList lore = tag.getList("Lore");
			if(lore == null){lore = new NBTTagList("Lore");}

			lore.add(new NBTTagString("", s));
			tag.set("Lore", lore);
		}

		tag.setString("Name", name);
		nms.tag.setCompound("display", tag);
		nms.tag.set("CustomPotionEffects", new NBTTagList());

		nms.setTag(nms.tag);*/
		
		return setToHealingPotion(CraftItemStack.asNMSCopy(is));
	}

	public static ItemStack signCustomItem(Material m, short meta_data, String name, String desc){
		/*ItemStack iss = new ItemStack(m, 1, meta_data);
        CraftItemStack css = new CraftItemStack(iss);
        net.minecraft.server.ItemStack nms = css.getHandle();
        NBTTagCompound tag = nms.tag;
        tag = new NBTTagCompound();
        tag.setCompound("display", new NBTTagCompound());
        nms.tag = tag;
        tag = nms.tag.getCompound("display");

        for(String s : desc.split(",")){
        	if(s.length() <= 1){continue;}
            NBTTagList lore = tag.getList("Lore");
            if(lore == null){lore = new NBTTagList("Lore");}

            lore.add(new NBTTagString("", s));
            tag.set("Lore", lore);
        }

        tag.setString("Name", name);
        nms.tag.setCompound("display", tag);
        css.getHandle().setTag(nms.tag);
        return css;*/

		ItemStack iss = new ItemStack(m, 1, meta_data);
		ItemMeta im = iss.getItemMeta();
		List<String> new_lore = new ArrayList<String>();

		for(String s : desc.split(",")){
			if(s.length() <= 1){continue;}
			if(s.contains("#")){
				s = s.substring(s.lastIndexOf("#") + 1, s.length());
			}
			new_lore.add(s);
		}

		im.setLore(new_lore);
		im.setDisplayName(name);

		iss.setItemMeta(im);
		removeAttributes(iss);
		return iss;
	}

	@SuppressWarnings("static-access")
	public static ItemStack generateRandomTierItem(int tier){
		ItemStack i = null;
		int r = new Random().nextInt(9);
		if(r == 0){
			if(tier == 1){
				return iGen.SwordGenorator(Material.WOOD_SWORD, false, null);
			}
			if(tier == 2){
				return iGen.SwordGenorator(Material.STONE_SWORD, false, null);
			}
			if(tier == 3){
				return iGen.SwordGenorator(Material.IRON_SWORD, false, null);
			}
			if(tier == 4){
				return iGen.SwordGenorator(Material.DIAMOND_SWORD, false, null);
			}
			if(tier == 5){
				return iGen.SwordGenorator(Material.GOLD_SWORD, false, null);
			}
		}
		if(r == 1){
			if(tier == 1){
				return iGen.AxeGenorator(Material.WOOD_AXE, false, null);
			}
			if(tier == 2){
				return iGen.AxeGenorator(Material.STONE_AXE, false, null);
			}
			if(tier == 3){
				return iGen.AxeGenorator(Material.IRON_AXE, false, null);
			}
			if(tier == 4){
				return iGen.AxeGenorator(Material.DIAMOND_AXE, false, null);
			}
			if(tier == 5){
				return iGen.AxeGenorator(Material.GOLD_AXE, false, null);
			}
		}
		if(r == 2){
			if(tier == 1){
				return iGen.BowGenorator(tier, false, null);
			}
			if(tier == 2){
				return iGen.BowGenorator(tier, false, null);
			}
			if(tier == 3){
				return iGen.BowGenorator(tier, false, null);
			}
			if(tier == 4){
				return iGen.BowGenorator(tier, false, null);
			}
			if(tier == 5){
				return iGen.BowGenorator(tier, false, null);
			}
		}
		if(r == 3){
			if(tier == 1){
				return iGen.HelmetGenerator(tier, false, null);
			}
			if(tier == 2){
				return iGen.HelmetGenerator(tier, false, null);
			}
			if(tier == 3){
				return iGen.HelmetGenerator(tier, false, null);
			}
			if(tier == 4){
				return iGen.HelmetGenerator(tier, false, null);
			}
			if(tier == 5){
				return iGen.HelmetGenerator(tier, false, null);
			}
		}
		if(r == 4){
			if(tier == 1){
				return iGen.BootGenerator(tier, false, null);
			}
			if(tier == 2){
				return iGen.BootGenerator(tier, false, null);
			}
			if(tier == 3){
				return iGen.BootGenerator(tier, false, null);
			}
			if(tier == 4){
				return iGen.BootGenerator(tier, false, null);
			}
			if(tier == 5){
				return iGen.BootGenerator(tier, false, null);
			}
		}
		if(r == 5){
			if(tier == 1){
				return iGen.LeggingsGenerator(tier, false, null);
			}
			if(tier == 2){
				return iGen.LeggingsGenerator(tier, false, null);
			}
			if(tier == 3){
				return iGen.LeggingsGenerator(tier, false, null);
			}
			if(tier == 4){
				return iGen.LeggingsGenerator(tier, false, null);
			}
			if(tier == 5){
				return iGen.LeggingsGenerator(tier, false, null);
			}
		}
		if(r == 6){
			if(tier == 1){
				return iGen.ChestPlateGenerator(tier, false, null);
			}
			if(tier == 2){
				return iGen.ChestPlateGenerator(tier, false, null);
			}
			if(tier == 3){
				return iGen.ChestPlateGenerator(tier, false, null);
			}
			if(tier == 4){
				return iGen.ChestPlateGenerator(tier, false, null);
			}
			if(tier == 5){
				return iGen.ChestPlateGenerator(tier, false, null);
			}
		}
		if(r == 7){
			if(tier == 1){
				return iGen.PolearmGenorator(Material.WOOD_SPADE, false, null);
			}
			if(tier == 2){
				return iGen.PolearmGenorator(Material.STONE_SPADE, false, null);
			}
			if(tier == 3){
				return iGen.PolearmGenorator(Material.IRON_SPADE, false, null);
			}
			if(tier == 4){
				return iGen.PolearmGenorator(Material.DIAMOND_SPADE, false, null);
			}
			if(tier == 5){
				return iGen.PolearmGenorator(Material.GOLD_SPADE, false, null);
			}
		}
		if(r == 8){
			if(tier == 1){
				return iGen.StaffGenorator(Material.WOOD_HOE, false, null);
			}
			if(tier == 2){
				return iGen.StaffGenorator(Material.STONE_HOE, false, null);
			}
			if(tier == 3){
				return iGen.StaffGenorator(Material.IRON_HOE, false, null);
			}
			if(tier == 4){
				return iGen.StaffGenorator(Material.DIAMOND_HOE, false, null);
			}
			if(tier == 5){
				return iGen.StaffGenorator(Material.GOLD_HOE, false, null);
			}
		}
		return i;
	}

}
