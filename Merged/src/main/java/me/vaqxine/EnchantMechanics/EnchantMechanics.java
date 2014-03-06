package me.vaqxine.EnchantMechanics;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

import me.vaqxine.Main;
import me.vaqxine.HealthMechanics.HealthMechanics;
import me.vaqxine.Hive.ParticleEffect;
import me.vaqxine.ItemMechanics.ItemMechanics;
import me.vaqxine.ProfessionMechanics.ProfessionMechanics;
import net.minecraft.server.v1_7_R1.NBTTagCompound;
import net.minecraft.server.v1_7_R1.NBTTagList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_7_R1.inventory.CraftItemStack;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;

public class EnchantMechanics implements Listener {
	static Logger log = Logger.getLogger("Minecraft");
	
	// WEAPON
	
	public static ItemStack t1_wep_scroll = ItemMechanics.signNewCustomItem(Material.EMPTY_MAP, (short)1, ChatColor.WHITE.toString() + 
			"" + ChatColor.BOLD.toString() + "Scroll:" + ChatColor.WHITE.toString() + " Enchant Wooden Weapon", ChatColor.RED.toString() + "+5% DMG" + "," 
					+ ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + "Weapon will VANISH if enchant above +3 FAILS.");
	
	public static ItemStack t2_wep_scroll = ItemMechanics.signNewCustomItem(Material.EMPTY_MAP, (short)1, ChatColor.WHITE.toString() + 
			"" + ChatColor.BOLD.toString() + "Scroll:" + ChatColor.GREEN.toString() + " Enchant Stone Weapon", ChatColor.RED.toString() + "+5% DMG" + "," 
					+ ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + "Weapon will VANISH if enchant above +3 FAILS.");
	
	public static ItemStack t3_wep_scroll = ItemMechanics.signNewCustomItem(Material.EMPTY_MAP, (short)1, ChatColor.WHITE.toString() + 
			"" + ChatColor.BOLD.toString() + "Scroll:" + ChatColor.AQUA.toString() + " Enchant Iron Weapon", ChatColor.RED.toString() + "+5% DMG" + "," 
					+ ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + "Weapon will VANISH if enchant above +3 FAILS.");
	
	public static ItemStack t4_wep_scroll = ItemMechanics.signNewCustomItem(Material.EMPTY_MAP, (short)1, ChatColor.WHITE.toString() + 
			"" + ChatColor.BOLD.toString() + "Scroll:" + ChatColor.LIGHT_PURPLE.toString() + " Enchant Diamond Weapon", ChatColor.RED.toString() + "+5% DMG" + "," 
					+ ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + "Weapon will VANISH if enchant above +3 FAILS.");
	
	public static ItemStack t5_wep_scroll = ItemMechanics.signNewCustomItem(Material.EMPTY_MAP, (short)1, ChatColor.WHITE.toString() + 
			"" + ChatColor.BOLD.toString() + "Scroll:" + ChatColor.YELLOW.toString() + " Enchant Gold Weapon", ChatColor.RED.toString() + "+5% DMG" + "," 
					+ ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + "Weapon will VANISH if enchant above +3 FAILS.");
	
	// ARMOR 
	
	public static ItemStack t1_armor_scroll = ItemMechanics.signNewCustomItem(Material.EMPTY_MAP, (short)1, ChatColor.WHITE.toString() + 
			"" + ChatColor.BOLD.toString() + "Scroll:" + ChatColor.WHITE.toString() + " Enchant Leather Armor", ChatColor.RED.toString() 
			+ "+5% HP" + "," + ChatColor.RED.toString() + "+5% HP REGEN" + "," + ChatColor.GRAY.toString() + "      - OR -    " + "," + ChatColor.RED.toString() + "+1% ENERGY REGEN" + ","
					+ ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + "Armor will VANISH if enchant above +3 FAILS.");
	
	public static ItemStack t2_armor_scroll = ItemMechanics.signNewCustomItem(Material.EMPTY_MAP, (short)1, ChatColor.WHITE.toString() + 
			"" + ChatColor.BOLD.toString() + "Scroll:" + ChatColor.GREEN.toString() + " Enchant Chainmail Armor", ChatColor.RED.toString() 
			+ "+5% HP" + "," + ChatColor.RED.toString() + "+5% HP REGEN" + "," + ChatColor.GRAY.toString() + "      - OR -    " + "," + ChatColor.RED.toString() + "+1% ENERGY REGEN" + ","
					+ ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + "Armor will VANISH if enchant above +3 FAILS.");
	
	public static ItemStack t3_armor_scroll = ItemMechanics.signNewCustomItem(Material.EMPTY_MAP, (short)1, ChatColor.WHITE.toString() + 
			"" + ChatColor.BOLD.toString() + "Scroll:" + ChatColor.AQUA.toString() + " Enchant Iron Armor", ChatColor.RED.toString() 
			+ "+5% HP" + "," + ChatColor.RED.toString() + "+5% HP REGEN" + "," + ChatColor.GRAY.toString() + "      - OR -    " + "," + ChatColor.RED.toString() + "+1% ENERGY REGEN" + ","
					+ ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + "Armor will VANISH if enchant above +3 FAILS.");
	
	public static ItemStack t4_armor_scroll = ItemMechanics.signNewCustomItem(Material.EMPTY_MAP, (short)1, ChatColor.WHITE.toString() + 
			"" + ChatColor.BOLD.toString() + "Scroll:" + ChatColor.LIGHT_PURPLE.toString() + " Enchant Diamond Armor", ChatColor.RED.toString() 
			+ "+5% HP" + "," + ChatColor.RED.toString() + "+5% HP REGEN" + "," + ChatColor.GRAY.toString() + "      - OR -    " + "," + ChatColor.RED.toString() + "+1% ENERGY REGEN" + ","
					+ ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + "Armor will VANISH if enchant above +3 FAILS.");
	
	public static ItemStack t5_armor_scroll = ItemMechanics.signNewCustomItem(Material.EMPTY_MAP, (short)1, ChatColor.WHITE.toString() + 
			"" + ChatColor.BOLD.toString() + "Scroll:" + ChatColor.YELLOW.toString() + " Enchant Gold Armor", ChatColor.RED.toString() 
			+ "+5% HP" + "," + ChatColor.RED.toString() + "+5% HP REGEN" + "," + ChatColor.GRAY.toString() + "      - OR -    " + "," + ChatColor.RED.toString() + "+1% ENERGY REGEN" + ","
					+ ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + "Armor will VANISH if enchant above +3 FAILS.");
	
	// PROTECTION
	
	public static ItemStack t1_white_scroll = ItemMechanics.signNewCustomItem(Material.EMPTY_MAP, (short)1, ChatColor.WHITE.toString() + 
			"" + ChatColor.BOLD.toString() + "White Scroll:" + ChatColor.WHITE.toString() + " Protect Leather/Wood Equipment", ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + "Apply to any T1 item to " + ChatColor.UNDERLINE + "prevent" + ChatColor.GRAY.toString() + " it" + "," 
					+ ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + "from being destroyed if the next" + "," + ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + "enchantment scroll (up to +8) fails.");
	
	public static ItemStack t2_white_scroll = ItemMechanics.signNewCustomItem(Material.EMPTY_MAP, (short)1, ChatColor.WHITE.toString() + 
			"" + ChatColor.BOLD.toString() + "White Scroll:" + ChatColor.GREEN.toString() + " Protect Stone/Chainmail Equipment", ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + "Apply to any T2 item to " + ChatColor.UNDERLINE + "prevent" + ChatColor.GRAY.toString() + " it" + "," 
					+ ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + "from being destroyed if the next" + "," + ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + "enchantment scroll (up to +8) fails.");
	
	public static ItemStack t3_white_scroll = ItemMechanics.signNewCustomItem(Material.EMPTY_MAP, (short)1, ChatColor.WHITE.toString() + 
			"" + ChatColor.BOLD.toString() + "White Scroll:" + ChatColor.AQUA.toString() + " Protect Iron Equipment", ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + "Apply to any T3 item to " + ChatColor.UNDERLINE + "prevent" + ChatColor.GRAY.toString() + " it" + "," 
					+ ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + "from being destroyed if the next" + "," + ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + "enchantment scroll (up to +8) fails.");
	
	public static ItemStack t4_white_scroll = ItemMechanics.signNewCustomItem(Material.EMPTY_MAP, (short)1, ChatColor.WHITE.toString() + 
			"" + ChatColor.BOLD.toString() + "White Scroll:" + ChatColor.LIGHT_PURPLE.toString() + " Protect Diamond Equipment", ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + "Apply to any T4 item to " + ChatColor.UNDERLINE + "prevent" + ChatColor.GRAY.toString() + " it" + "," 
					+ ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + "from being destroyed if the next" + "," + ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + "enchantment scroll (up to +8) fails.");
	
	public static ItemStack t5_white_scroll = ItemMechanics.signNewCustomItem(Material.EMPTY_MAP, (short)1, ChatColor.WHITE.toString() + 
			"" + ChatColor.BOLD.toString() + "White Scroll:" + ChatColor.YELLOW.toString() + " Protect Gold Equipment", ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + "Apply to any T5 item to " + ChatColor.UNDERLINE + "prevent" + ChatColor.GRAY.toString() + " it" + "," 
					+ ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + "from being destroyed if the next" + "," + ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + "enchantment scroll (up to +8) fails.");
	
	public void onEnable() {
		Bukkit.getServer().getPluginManager().registerEvents(this, Main.plugin);
		log.info("[EnchantMechanics] V1.0 has been enabled.");
	}
	
	public void onDisable() {
		log.info("[EnchantMechanics] V1.0 has been disabled.");
	}
	
	public boolean isEnchantScroll(ItemStack is){
		if(is.getType() != Material.EMPTY_MAP){
			return false;
		}
		if(!(is.hasItemMeta())){
			return false;
		}
		if(!(is.getItemMeta().hasDisplayName())){
			return false;
		}
		if(!(ChatColor.stripColor(is.getItemMeta().getDisplayName().toLowerCase()).contains("scroll: enchant"))){
			return false;
		}
		return true;
	}
	
	public boolean isWhiteScroll(ItemStack is){
		if(is.getType() != Material.EMPTY_MAP){
			return false;
		}
		if(!(is.hasItemMeta())){
			return false;
		}
		if(!(is.getItemMeta().hasDisplayName())){
			return false;
		}
		if(!(ChatColor.stripColor(is.getItemMeta().getDisplayName().toLowerCase()).contains("white scroll: protect"))){
			return false;
		}
		return true;
	}
	
	
	public boolean isCorrectScroll(ItemStack scroll, ItemStack in_slot){
		 // First we check weapon/armor scroll type.
		 String in_raw_name = in_slot.getType().name().toLowerCase();
		 String scroll_type = scroll.getItemMeta().getDisplayName().toLowerCase();
		 if(scroll_type.contains("weapon")){
			 if(!in_raw_name.contains("axe") && !in_raw_name.contains("sword") && !in_raw_name.contains("bow") && !in_raw_name.contains("spade") && !in_raw_name.contains("hoe")){
				 return false;
			 }
		 }
		 if(scroll_type.contains("armor")){
			 if(!in_raw_name.contains("boot") && !in_raw_name.contains("helmet") && !in_raw_name.contains("leg") && !in_raw_name.contains("chest")){
				 return false;
			 }
		 }
		 
		 // Secondly, we check the tier.
		 int item_tier = ItemMechanics.getItemTier(in_slot);
		 int scroll_tier = ItemMechanics.getItemTier(scroll);
		 
		 if(item_tier != scroll_tier){
			 return false;
		 }
		 
		 return true;
	}
	
	public static boolean hasProtection(ItemStack is){
		if(!(is.hasItemMeta())){
			return false;
		}
		if(!(is.getItemMeta().hasLore())){
			return false;
		}
		
		for(String s : is.getItemMeta().getLore()){
			if(s.equalsIgnoreCase(ChatColor.GREEN.toString() + ChatColor.BOLD.toString() + "PROTECTED")){
				return true;
			}
		}
		
		return false;
	}
	
	public static ItemStack removeProtection(ItemStack is){
		if(!(is.hasItemMeta())){
			return is;
		}
		if(!(is.getItemMeta().hasLore())){
			return is;
		}
		
		ItemMeta im = is.getItemMeta();
		List<String> lore = im.getLore();
		lore.remove(ChatColor.GREEN.toString() + ChatColor.BOLD.toString() + "PROTECTED");
		im.setLore(lore);
		is.setItemMeta(im);
		
		return is;
	}
	
	public static ItemStack addProtection(ItemStack is){
		if(!(is.hasItemMeta())){
			return is;
		}
		if(!(is.getItemMeta().hasLore())){
			return is;
		}
		
		ItemMeta im = is.getItemMeta();
		List<String> lore = im.getLore();
		lore.add(ChatColor.GREEN.toString() + ChatColor.BOLD.toString() + "PROTECTED");
		im.setLore(lore);
		is.setItemMeta(im);
		
		return is;
	}
	
	public static int getEnchantCount(ItemStack is){
		try{
		if(!(is.hasItemMeta())){
			return 0;
		}
		if(!(is.getItemMeta().hasDisplayName())){
			return 0;
		}
		String name = ChatColor.stripColor(is.getItemMeta().getDisplayName());
		if(name.startsWith("[")){
			int enchant_count = Integer.parseInt(name.substring(name.indexOf("+") + 1, name.lastIndexOf("]")));
			return enchant_count;		
		}
		} catch(Exception e){
			return 0;
		}
		return 0; // No (+#), must be unenchanted.
	}
	
    public static void addGlow(org.bukkit.inventory.ItemStack stack) {
        net.minecraft.server.v1_7_R1.ItemStack nmsStack = (net.minecraft.server.v1_7_R1.ItemStack) getField(stack, "handle");
        NBTTagCompound compound = nmsStack.tag;
 
        // Initialize the compound if we need to
        if (compound == null) {
            compound = new NBTTagCompound();
            nmsStack.tag = compound;
        }
 
        // Empty enchanting compound
        compound.set("ench", new NBTTagList());
    }
 
    private static Object getField(Object obj, String name) {
        try {
            Field field = obj.getClass().getDeclaredField(name);
            field.setAccessible(true);
 
            return field.get(obj);
        } catch (Exception e) {
            // We don't care
            throw new RuntimeException("Unable to retrieve field content.", e);
        }
    }
	
	public ItemStack enchantWeapon(ItemStack wep, int enchant_level){
		String old_name = wep.getItemMeta().getDisplayName();
		if(old_name.contains("]")){
			// It was enchanted before, so we need to trim.
			old_name = old_name.substring(old_name.indexOf("]") + 1, old_name.length());
		}
		
		if(old_name.startsWith(" ")){
			old_name = old_name.substring(1, old_name.length());
		}
		
		String new_name = ChatColor.RED + "[+" + enchant_level + "]" + " " + ChatColor.RESET + old_name;
		List<Integer> dmg_range = ItemMechanics.getDmgRangeOfWeapon(wep);
		double o_min_dmg = dmg_range.get(0);
		double o_max_dmg = dmg_range.get(1);
		
		double min_dmg = o_min_dmg + (o_min_dmg * 0.05D);
		double max_dmg = o_max_dmg + (o_max_dmg * 0.05D);
		
		if((min_dmg - o_min_dmg) < 1){
			min_dmg = min_dmg + 1;
		}
		
		if((max_dmg - o_min_dmg) < 1){
			max_dmg = max_dmg + 1;
		}
		
		ItemMeta im = wep.getItemMeta();
		
		List<String> new_lore = new ArrayList<String>();
		
		for(String s : im.getLore()){
			new_lore.add(s);
		}
		
		new_lore.set(0, ChatColor.RED.toString() + "DMG: " + (int)min_dmg + " - " + (int)max_dmg);
		
		im.setDisplayName(new_name);
		im.setLore(new_lore);
		wep.setItemMeta(im);
		
		if(enchant_level >= 4){
			// Glowing effect.
			addGlow(wep);
		}	
		
		return ItemMechanics.removeAttributes(wep);
	}
	
	public ItemStack enchantArmor(ItemStack arm, int enchant_level){
		String old_name = arm.getItemMeta().getDisplayName();
		if(old_name.contains("]")){
			// It was enchanted before, so we need to trim.
			old_name = old_name.substring(old_name.indexOf("]") + 1, old_name.length());
		}
		
		if(old_name.startsWith(" ")){
			old_name = old_name.substring(1, old_name.length());
		}
		
		String new_name = ChatColor.RED + "[+" + enchant_level + "]" + " " + ChatColor.RESET + old_name;
		double o_hp_gain = HealthMechanics.getHealthVal(arm);
		double hp_gain = o_hp_gain + (o_hp_gain * 0.05D);
		
		if((hp_gain - o_hp_gain) < 1){
			hp_gain = hp_gain + 1;
		}
		
		ItemMeta im = arm.getItemMeta();
		
		List<String> new_lore = new ArrayList<String>();
		
		for(String s : im.getLore()){
			new_lore.add(s);
		}
		
		new_lore.set(1, ChatColor.RED.toString() + "HP: +" + (int)hp_gain);
		
		String regen_string = new_lore.get(2);
		if(regen_string.contains("ENERGY REGEN")){
			// Energy.
			int o_energy_regen = Integer.parseInt(regen_string.substring(regen_string.indexOf("+") + 1, regen_string.indexOf("%")));
			int energy_regen = o_energy_regen + 1;
			regen_string = regen_string.replace("+" + o_energy_regen + "%", "+" + energy_regen + "%");
			new_lore.set(2, regen_string);
		}
		else if(regen_string.contains("HP REGEN")){
			// HP.
			double o_hp_regen = Double.parseDouble(regen_string.substring(regen_string.indexOf("+") + 1, regen_string.lastIndexOf(" ")));
			double hp_regen = o_hp_regen + (o_hp_regen * 0.05D);
			if((hp_regen - o_hp_regen) < 1){
				hp_regen = hp_regen + 1;
			}
			regen_string = regen_string.replace("+" + (int)o_hp_regen + " HP/s", "+" + (int)hp_regen + " HP/s");
			new_lore.set(2, regen_string);
		}
		
		im.setDisplayName(new_name);
		im.setLore(new_lore);
		arm.setItemMeta(im);
		
		if(enchant_level >= 4){
			// Glowing effect.
			addGlow(arm);
		}	
		
		return ItemMechanics.removeAttributes(arm);
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerJoin(final PlayerJoinEvent e){
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
			public void run() {    	        	
				//Inventory inv = e.getPlayer().getInventory();
				List<ItemStack> to_glow = new ArrayList<ItemStack>();
				for(ItemStack is : e.getPlayer().getInventory()){
					if(is == null || is.getType() == Material.AIR){
						continue;
					}
					
					if(getEnchantCount(is) >= 4){
						to_glow.add(is);
					}
				}
				
				ItemStack helmet, chest, legs, boots;
				helmet = e.getPlayer().getInventory().getHelmet();
				chest = e.getPlayer().getInventory().getChestplate();
				legs = e.getPlayer().getInventory().getLeggings();
				boots = e.getPlayer().getInventory().getBoots();
				
				if(helmet != null && getEnchantCount(helmet) >= 4){
					ItemStack is = CraftItemStack.asCraftCopy(helmet);
					addGlow(is);
					e.getPlayer().getInventory().setHelmet(is);
				}
				if(chest != null && getEnchantCount(chest) >= 4){
					ItemStack is = CraftItemStack.asCraftCopy(chest);
					addGlow(is);
					e.getPlayer().getInventory().setChestplate(is);
				}
				if(legs != null && getEnchantCount(legs) >= 4){
					ItemStack is = CraftItemStack.asCraftCopy(legs);
					addGlow(is);
					e.getPlayer().getInventory().setLeggings(is);
				}
				if(boots != null && getEnchantCount(boots) >= 4){
					ItemStack is = CraftItemStack.asCraftCopy(boots);
					addGlow(is);
					e.getPlayer().getInventory().setBoots(is);
				}
				
				for(ItemStack is : to_glow){
					for(int slot : e.getPlayer().getInventory().all(is).keySet()){
						addGlow(is);
						e.getPlayer().getInventory().setItem(slot, is);
					}
				}
				
				e.getPlayer().updateInventory();
			}
		}, 10L);
	}
	
	@EventHandler
	public void onInventoryOpen(InventoryOpenEvent e){
		Inventory inv = e.getInventory();
		List<ItemStack> to_glow = new ArrayList<ItemStack>();
		for(ItemStack is : inv.getContents()){
			if(is == null || is.getType() == Material.AIR){
				continue;
			}
			
			if(getEnchantCount(is) >= 4){
				to_glow.add(is);
			}
		}
		
		for(ItemStack is : to_glow){
			for(int slot : inv.all(is).keySet()){
				addGlow(is);
				//log.info(is.toString());
				inv.setItem(slot, is);
			}
		}
	}
	
	@EventHandler
	public void onScrollUse(InventoryClickEvent e){
		if(e.getCursor() == null){return;}
		if(e.getCurrentItem() == null){return;}
		ItemStack cursor = e.getCursor();
		ItemStack in_slot = e.getCurrentItem();
		boolean win = true;
		
		if(!e.getInventory().getName().equalsIgnoreCase("container.crafting")){return;}
		if(e.getInventory().getViewers().size() > 1){return;}
		if(e.getSlotType() == SlotType.ARMOR){return;}
		
		Player p = (Player)e.getWhoClicked();
		
		if(isWhiteScroll(cursor) && !(ProfessionMechanics.isSkillItem(in_slot)) && (ItemMechanics.isArmor(in_slot) || !ItemMechanics.getDamageData(in_slot).equalsIgnoreCase("no"))){
			// Add protection.
			
			int cursor_tier = ItemMechanics.getItemTier(cursor);
			int in_slot_tier = ItemMechanics.getItemTier(in_slot);
			
			if(cursor_tier != in_slot_tier){
				return;
			}
			
			if(hasProtection(in_slot)){
				p.sendMessage(ChatColor.RED + "This item already has 'protected' enchantment status.");
				e.setCancelled(true);
				p.updateInventory();
				return;
			}
		
			if(cursor.getAmount() == 1){
				e.setCancelled(true);
				e.setCursor(new ItemStack(Material.AIR));			
			}
			else if(cursor.getAmount() > 1){
				e.setCancelled(true);
				cursor.setAmount(cursor.getAmount() - 1);
				e.setCursor(cursor);
			}
			
			e.setCurrentItem(addProtection(in_slot));
			p.sendMessage(ChatColor.GREEN + "Your " + in_slot.getItemMeta().getDisplayName() + ChatColor.GREEN + " is now protected -- even if an enchant scroll fails, it will " + ChatColor.UNDERLINE + "NOT" + ChatColor.GREEN + " be destroyed up to +8 status.");
			p.playSound(p.getLocation(), Sound.LEVEL_UP, 1F, 1F);
			
        	Firework fw = (Firework) p.getWorld().spawnEntity(p.getLocation(), EntityType.FIREWORK);
        	FireworkMeta fwm = fw.getFireworkMeta();
        	Random r = new Random();   
        	FireworkEffect effect = FireworkEffect.builder().flicker(false).withColor(Color.GREEN).withFade(Color.GREEN).with(Type.STAR).trail(true).build();
        	fwm.addEffect(effect);
        	fwm.setPower(0);
        	fw.setFireworkMeta(fwm); 
		}
		
		if(isEnchantScroll(cursor) && !(ProfessionMechanics.isSkillItem(in_slot)) && (ItemMechanics.isArmor(in_slot) || !ItemMechanics.getDamageData(in_slot).equalsIgnoreCase("no"))){
			
			if(!isCorrectScroll(cursor, in_slot)){
				return;
			}
			
			boolean white_scroll = hasProtection(in_slot);
			
			int old_enchant = getEnchantCount(in_slot);
			
			if(old_enchant >= 12){
				p.sendMessage(ChatColor.RED + "This item is already enchanted +12, cannot apply more stats.");
				e.setCancelled(true);
				p.updateInventory();
				return;
			}
			
			if(cursor.getAmount() == 1){
				e.setCancelled(true);
				e.setCursor(new ItemStack(Material.AIR));			
			}
			else if(cursor.getAmount() > 1){
				e.setCancelled(true);
				cursor.setAmount(cursor.getAmount() - 1);
				e.setCursor(cursor);
			}
			
			if(old_enchant >= 3){
				int win_chance = new Random().nextInt(100);
				int fail_percent = 0;
				if(old_enchant == 3){
					fail_percent = 30;
				}
				if(old_enchant == 4){
					fail_percent = 40;
				}
				if(old_enchant == 5){
					fail_percent = 50;
				}
				if(old_enchant == 6){
					fail_percent = 65;
				}
				if(old_enchant == 7){
					fail_percent = 75;
				}
				if(old_enchant == 8){
					fail_percent = 80;
				}
				if(old_enchant == 9){
					fail_percent = 85;
				}
				if(old_enchant == 10){
					fail_percent = 90;
				}
				if(old_enchant == 11){
					fail_percent = 95;
				}
				
				if(win_chance < fail_percent){
					win = false;
					// Fail.
				}
				else if(win_chance >= fail_percent){
					win = true;
				}
			}

			
			if(win == true){
				if(!ItemMechanics.getDamageData(in_slot).equalsIgnoreCase("no")){
					// Scrolling a weapon.
					// 	+5% DMG
					e.setCurrentItem(enchantWeapon(in_slot, (old_enchant+1)));
				}
				if(ItemMechanics.isArmor(in_slot)){
					// Scrolling a weapon.
					// 	+5% DMG
					e.setCurrentItem(enchantArmor(in_slot, (old_enchant+1)));
				}
		
				p.updateInventory();
				p.getWorld().playSound(p.getLocation(), Sound.LEVEL_UP, 1.0F, 1.25F);
				//p.getWorld().spawnParticle(p.getLocation().add(0, 8, 0), Particle.FIREWORKS_SPARK, 0.75F, 50);
	        	Firework fw = (Firework) p.getWorld().spawnEntity(p.getLocation(), EntityType.FIREWORK);
	        	FireworkMeta fwm = fw.getFireworkMeta();
	        	Random r = new Random();   
	        	FireworkEffect effect = FireworkEffect.builder().flicker(false).withColor(Color.YELLOW).withFade(Color.YELLOW).with(Type.BURST).trail(true).build();
	        	fwm.addEffect(effect);
            	fwm.setPower(0);
            	fw.setFireworkMeta(fwm); 
			}
			
			else if(win == false){
				// FAIL. 
				if(!white_scroll || old_enchant >= 8){
					e.setCurrentItem(new ItemStack(Material.AIR));
				}
				else if(white_scroll && old_enchant < 8){
					p.sendMessage(ChatColor.RED + "Your enchantment scroll " + ChatColor.UNDERLINE + "FAILED" + ChatColor.RED + " but since you had white scroll protection, your item did not vanish.");
				}
				p.updateInventory();
				p.getWorld().playSound(p.getLocation(), Sound.FIZZ, 2.0F, 1.25F);
				
				try {
					ParticleEffect.sendToLocation(ParticleEffect.LAVA, p.getLocation().add(0, 2.5, 0), new Random().nextFloat(), new Random().nextFloat(), new Random().nextFloat(), 1F, 75);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				
	        	/*Firework fw = (Firework) p.getWorld().spawnEntity(p.getLocation(), EntityType.FIREWORK);
	        	FireworkMeta fwm = fw.getFireworkMeta();
	        	Random r = new Random();   
	        	FireworkEffect effect = FireworkEffect.builder().flicker(false).withColor(Color.BLACK).withFade(Color.GRAY).with(Type.BURST).trail(true).build();
	        	fwm.addEffect(effect);
            	fwm.setPower(0);
            	fw.setFireworkMeta(fwm);*/  
			}
			
			if(white_scroll){
				e.setCurrentItem(removeProtection(e.getCurrentItem()));
				p.updateInventory();
			}
		}
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e){
		Player p = e.getPlayer();
		if(e.hasItem() && e.getItem().getType() == Material.EMPTY_MAP){
			e.setCancelled(true); // Prevent map creation.
			e.setUseItemInHand(Result.DENY);
			e.setUseInteractedBlock(Result.DENY);
			p.sendMessage(ChatColor.RED + "To use a " + ChatColor.BOLD + "SCROLL" + ChatColor.RED + ", simply drag it ontop of the piece of equipment you wish to apply it to in your inventory.");
			p.updateInventory();
		}
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
		Player p = (Player)sender;
	
		if(cmd.getName().equalsIgnoreCase("drenchant")){
			if(!p.isOp()){
				return true;
			}
			p.getInventory().addItem(t1_wep_scroll);
			p.getInventory().addItem(t2_wep_scroll);
			p.getInventory().addItem(t3_wep_scroll);
			p.getInventory().addItem(t4_wep_scroll);
			p.getInventory().addItem(t5_wep_scroll);
			
			p.getInventory().addItem(t1_armor_scroll);
			p.getInventory().addItem(t2_armor_scroll);
			p.getInventory().addItem(t3_armor_scroll);
			p.getInventory().addItem(t4_armor_scroll);
			p.getInventory().addItem(t5_armor_scroll);
			
			p.getInventory().addItem(t1_white_scroll);
			p.getInventory().addItem(t2_white_scroll);
			p.getInventory().addItem(t3_white_scroll);
			p.getInventory().addItem(t4_white_scroll);
			p.getInventory().addItem(t5_white_scroll);
			// TODO: Debug command.
		}

	 return true;
	 }
}
