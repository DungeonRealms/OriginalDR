package me.vaqxine.MoneyMechanics;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import me.vaqxine.Main;
import me.vaqxine.AchievmentMechanics.AchievmentMechanics;
import me.vaqxine.CommunityMechanics.CommunityMechanics;
import me.vaqxine.Hive.Hive;
import me.vaqxine.InstanceMechanics.InstanceMechanics;
import me.vaqxine.ItemMechanics.ItemMechanics;
import me.vaqxine.PetMechanics.PetMechanics;
import me.vaqxine.RealmMechanics.RealmMechanics;
import me.vaqxine.ShopMechanics.ShopMechanics;
import me.vaqxine.TradeMechanics.TradeMechanics;
import me.vaqxine.TutorialMechanics.TutorialMechanics;

import org.apache.commons.lang.StringEscapeUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_7_R1.inventory.CraftItemStack;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.plugin.java.JavaPlugin;

public class MoneyMechanics implements Listener {

	static HashMap<String, Integer> download_timeout = new HashMap<String, Integer>();
	// DEPRECIATED: Used for download recurrency to ensure data goes up if there's SQL error.

	static HashMap<String, Integer> upload_timeout = new HashMap<String, Integer>();
	// DEPRECIATED: Used for upload recurrency to ensure data goes up if there's SQL error.

	public static ConcurrentHashMap<String, List<Inventory>> bank_contents = new ConcurrentHashMap<String, List<Inventory>>();
	// Bank inventory.

	public static ConcurrentHashMap<String, Integer> bank_level = new ConcurrentHashMap<String, Integer>();
	// Bank level.

	static HashMap<Player, String> bank_upgrade_codes = new HashMap<Player, String>();
	// Unique upgrade codes for bank upgrade.

	public static HashMap<String, Integer> bank_map = new HashMap<String, Integer>();
	// Net worth of player's cash stack in bank.

	public static HashMap<Player, Integer> split_map = new HashMap<Player, Integer>();
	// Net worth of the original note being split.

	static HashMap<Player, Integer> withdraw_map = new HashMap<Player, Integer>();
	// Amount to withdraw.

	static HashMap<Player, String> withdraw_type = new HashMap<Player, String>();
	// Type of withdrawl, gems or gem note.

	public static boolean no_bank_use = false;
	// Enabled 10 seconds out from server reboot.

	private static final String ALPHA_NUM =  
			"123456789";  

	static Logger log = Logger.getLogger("Minecraft");

	public static ItemStack t1_gem_pouch = ItemMechanics.signCustomItem(Material.INK_SACK, (short)0, ChatColor.WHITE.toString() + "Small Gem Pouch" + ChatColor.GREEN + ChatColor.BOLD.toString() + " 0g", 
			ChatColor.GRAY.toString() + "A small linen pouch that holds " + ChatColor.BOLD + "100g");

	public static ItemStack t2_gem_pouch = ItemMechanics.signCustomItem(Material.INK_SACK, (short)0, ChatColor.GREEN.toString() + "Medium Gem Sack" + ChatColor.GREEN +  ChatColor.BOLD.toString() + " 0g", 
			ChatColor.GRAY.toString() + "A medium wool sack that holds " + ChatColor.BOLD + "150g");

	public static ItemStack t3_gem_pouch = ItemMechanics.signCustomItem(Material.INK_SACK, (short)0, ChatColor.AQUA.toString() + "Large Gem Satchel" + ChatColor.GREEN +  ChatColor.BOLD.toString() + " 0g", 
			ChatColor.GRAY.toString() + "A large leather satchel that holds " + ChatColor.BOLD + "200g");

	public static ItemStack t4_gem_pouch = ItemMechanics.signCustomItem(Material.INK_SACK, (short)0, ChatColor.LIGHT_PURPLE.toString() + "Gigantic Gem Container" + ChatColor.GREEN +  ChatColor.BOLD.toString() + " 0g", 
			ChatColor.GRAY.toString() + "A giant container that holds " + ChatColor.BOLD + "300g");

	@SuppressWarnings("deprecation")
	public void onEnable() {
		Main.plugin.getServer().getPluginManager().registerEvents(this, Main.plugin);

		Main.plugin.getServer().getScheduler().scheduleSyncRepeatingTask(Main.plugin, new Runnable() {
			public void run() {
				if(no_bank_use == true){
					for(Player pl : Bukkit.getOnlinePlayers()){
						if(pl.getOpenInventory().getTitle().startsWith("Bank Chest")){
							if(pl.getItemOnCursor() != null && pl.getItemOnCursor().getType() != Material.AIR){
								ItemStack on_cursor = pl.getItemOnCursor();
								pl.setItemOnCursor(new ItemStack(Material.AIR));
								pl.getInventory().addItem(on_cursor);
							}
							pl.closeInventory();
							pl.updateInventory();
							pl.sendMessage(ChatColor.GRAY + "Banker: " + ChatColor.WHITE + "Sorry, the bank is closed for a minute.");
						}
					}
				}
			}
		}, 10 * 20L, 1 * 20L);

		Main.plugin.getServer().getScheduler().scheduleAsyncRepeatingTask(Main.plugin, new Runnable() {
			public void run() {
				if(Hive.shutting_down == false){
					ConnectionPool.refresh = true;
				}
			}
		}, 240 * 20L, 240 * 20L);

		log.info("[MoneyMechanics] has been enabled.");
	}

	public void onDisable() {
		ConnectionPool.refresh = false;
		int attempts = 0;
		while(bank_contents.size() > 0 && attempts <= 100){
			attempts++;
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		log.info("[MoneyMechanics] has been disabled.");
	}

	public static int getBankSlots(int level){
		if(level == 0){
			return 9;
		}
		if(level == 1){
			return 18;
		}
		if(level == 2){
			return 27;
		}
		if(level == 3){
			return 36;
		}
		if(level == 4){
			return 45;
		}
		if(level == 5){
			return 54;
		}
		if(level == 6){
			return 63;
		}
		if(level == 7){
			return 72;
		}
		if(level == 8){
			return 81;
		}
		if(level == 9){
			return 90;
		}
		if(level == 10){
			return 99;
		}
		if(level == 11){
			return 108;
		}
		if(level == 12){
			return 117;
		}
		if(level == 13){
			return 126;
		}
		if(level == 14){
			return 135;
		}
		if(level == 15){
			return 144;
		}
		if(level == 16){
			return 153;
		}
		if(level == 17){
			return 162;
		}

		return 9;
	}

	public static ItemStack generateArrowButton(String type, int current_page, int max_pages){
		if(type.equalsIgnoreCase("next")){
			return ItemMechanics.signCustomItem(Material.ARROW, (short)1, ChatColor.YELLOW.toString() 
					+ "Next Page " + ChatColor.BOLD.toString() + "->", ChatColor.GRAY.toString() + "Page " + current_page + "/" + max_pages);
		}
		else if(type.equalsIgnoreCase("previous")){
			return ItemMechanics.signCustomItem(Material.ARROW, (short)1, ChatColor.YELLOW.toString() + ChatColor.BOLD + "<-" + ChatColor.YELLOW.toString() 
					+ " Previous Page ", ChatColor.GRAY.toString() + "Page " + current_page + "/" + max_pages);
		}
		return null;
	}

	public static Inventory legacyBankStringToInventory(final String p_name, String bank_string){
		int bank_level = MoneyMechanics.bank_level.get(p_name);
		int slots = getBankSlots(bank_level);
		Inventory bank_contents = Bukkit.createInventory(null, slots, "Bank Chest (1/1)");
		String bank_content_data = bank_string;
		boolean has_nbt = false;
		int slot_x = -1;

		if(bank_content_data.equalsIgnoreCase("empty")){
			MoneyMechanics.bank_contents.put(p_name, new ArrayList<Inventory>(Arrays.asList(bank_contents))); 
			return null; // Return empty list.
		}

		String partial_data = "";

		for(String s : bank_content_data.split("_")){
			if(s.length() <= 0){continue;}
			has_nbt = false;

			slot_x++;

			if(s.equalsIgnoreCase("AIR")){
				slot_x--;
				/*try{
					bank_contents.setItem(slot_x, new ItemStack(Material.AIR));
				} catch(ArrayIndexOutOfBoundsException err){
					err.printStackTrace();
					continue;
				}*/
				continue;
			}

			if(partial_data.length() > 0){
				s = partial_data + "_" + s;
				partial_data = "";
			}

			String[] s_sub = s.split("=");

			if(s.contains("@")){
				has_nbt = true;
			}

			int type_id = Integer.parseInt(s_sub[0]);
			int amount = Integer.parseInt(s_sub[1]);
			short meta_data = Short.parseShort(s_sub[2].split("@")[0]);

			ItemStack i = new ItemStack(type_id, amount, meta_data);

			if(has_nbt == true){
				String nbt_data = "";
				try{
					try{
						nbt_data = s.substring(s.indexOf("@") + 1, s.lastIndexOf("@"));
						String nbt_name = nbt_data.substring(nbt_data.indexOf("#") + 1, nbt_data.lastIndexOf("#"));
						nbt_data = nbt_data.replace("#" + nbt_name + "#", "");
						if(nbt_data.contains("#")){
							nbt_data = nbt_data.substring(nbt_data.lastIndexOf("#") + 1, nbt_data.lastIndexOf("@"));
						}

						if(nbt_name.contains("Harrison")){
							nbt_name = ChatColor.BOLD.toString() + "Teleport:" + ChatColor.WHITE.toString() + " Harrison Field";
						}

						if(nbt_name.equalsIgnoreCase("NOPE")){
							nbt_name = ChatColor.WHITE.toString() + RealmMechanics.getFormalMatName(Material.getMaterial(type_id), meta_data);
						}

						ItemStack custom_i = ItemMechanics.signCustomItem(Material.getMaterial(type_id), meta_data, nbt_name, nbt_data);

						if(s.contains("[larm1]")){
							// Set leather color.
							ItemMeta im = custom_i.getItemMeta();
							LeatherArmorMeta lam = (LeatherArmorMeta)im;
							int bgr_color = Integer.parseInt(s.substring((s.indexOf("[larm1]") + 7), s.indexOf("[larm2]")));
							lam.setColor(Color.fromBGR(bgr_color));
							custom_i.setItemMeta(lam);
						}

						if(custom_i.getType() == Material.POTION && custom_i.getDurability() > 0){
							// Renames potion to Instant Heal.
							custom_i = ItemMechanics.signNewCustomItem(Material.getMaterial(type_id), meta_data, nbt_name, nbt_data);
						}

						if(nbt_name.contains("Sheep") && nbt_name.contains("''")){
							// Sheep O' Luck
							custom_i = PetMechanics.generatePetEgg(EntityType.SHEEP, "green");
						}

						custom_i.setAmount(amount);
						bank_contents.setItem(slot_x, custom_i);
						continue;

					} catch(StringIndexOutOfBoundsException e){
						//log.info(nbt_data);
						partial_data = s;
						slot_x--;
						continue;
					}
				} catch(ArrayIndexOutOfBoundsException err){
					continue;
				}
			}

			else{
				try{
					bank_contents.setItem(slot_x, i);
				} catch(ArrayIndexOutOfBoundsException err){
					continue;
				}
			}
		}

		return bank_contents;
	}

	public void updateStaticCashStack(final Player p){
		if(!(bank_map.containsKey(p.getName())))
			return;
		
		int new_net = bank_map.get(p.getName());
		ItemStack new_static_stack = generateStaticCashStack(new_net);
		int bank_level = MoneyMechanics.bank_level.get(p.getName());
		
		if(bank_level >= 6){ // Multiple pages! WOO!
			// Put it 4 slots to the left of last slot -- middle of the bottom row.
			if(p.getOpenInventory().getTopInventory().getName().startsWith("Bank Chest")){
				// Instant-update if they're viewing any specific page.
				int inventory_size = p.getOpenInventory().getTopInventory().getSize();
				
				Inventory bank_chest = p.getOpenInventory().getTopInventory();
				bank_chest.setItem((inventory_size - 5), new_static_stack);
			}
			if(bank_contents.containsKey(p.getName())){
				// Now update all other pages as well for the hashmap.
				List<Inventory> bank_pages = new ArrayList<Inventory>();
				for(Inventory inv : bank_contents.get(p.getName())){
					int inventory_size = inv.getSize();
					inv.setItem((inventory_size - 5), new_static_stack);
					bank_pages.add(inv);
				}
				
				bank_contents.put(p.getName(), bank_pages);
			}
		}

		if(bank_level <= 5){ // Only 1 page, no bottom row.
			int slots = getBankSlots(bank_level);

			if(p.getOpenInventory().getTopInventory().getName().startsWith("Bank Chest")){
				Inventory bank_chest = p.getOpenInventory().getTopInventory();
				bank_chest.setItem((slots - 1), new_static_stack);
			}

			if(bank_contents.containsKey(p.getName()) && !(p.getOpenInventory().getTopInventory().getName().startsWith("Bank Chest"))){
				Inventory bank_chest = bank_contents.get(p.getName()).get(0);
				for(ItemStack i : bank_chest){
					if(i != null && i.getType() == Material.EMERALD){
						bank_chest.remove(i);
					} 
				}
				bank_chest.setItem((slots - 1), new_static_stack);
				List<Inventory> bank_pages = bank_contents.get(p.getName());
				bank_pages.set(0, bank_contents.get(p.getName()).get(0));
				bank_contents.put(p.getName(), bank_pages);
			}
		}

	}

	public static ItemStack generateStaticCashStack(int new_amount){
		return ItemMechanics.signCustomItem(Material.EMERALD, (short)0, ChatColor.GREEN.toString() + new_amount + ChatColor.GREEN.toString() + "" + ChatColor.BOLD.toString() + " GEM(s)", ChatColor.GRAY.toString() + "" + ChatColor.GREEN.toString() 
				+ "Left Click" + ChatColor.GRAY.toString() + " to withdraw " + ChatColor.GREEN + ChatColor.BOLD + "RAW GEMS" + "," + ChatColor.GRAY.toString() + "" + ChatColor.GREEN.toString() 
				+ "Right Click" + ChatColor.GRAY.toString() + " to create " + ChatColor.GREEN + ChatColor.BOLD + "A GEM NOTE" + "," + ChatColor.GREEN.toString() 
				+ "Middle Click" + ChatColor.GRAY.toString() + " to upgrade your bank size.");
	}

	public static boolean downloadBankDatabaseData(final String p_name){
		PreparedStatement pst = null;

		// TODO: Does this cause issues if they somehow log in before information is done uploading?
		/*bank_map.remove(p_name);
		bank_level.remove(p_name);
		bank_contents.remove(p_name);*/
		
		try {
			pst = ConnectionPool.getConneciton().prepareStatement(
					"SELECT money, level, content FROM bank_database WHERE p_name = '" + p_name + "'");

			pst.execute();
			
			
			ResultSet rs = pst.getResultSet();
			if(!rs.next()){
				bank_map.put(p_name, 0);
				bank_level.put(p_name, 0);
				bank_contents.put(p_name, new ArrayList<Inventory>(Arrays.asList(Bukkit.createInventory(null, 9, "Bank Chest (1/1)"))));
				return true;
			}

			int money = rs.getInt("money");
			int level = rs.getInt("level");
			String bank_content = rs.getString("content");

			if(money < 0){
				// Negative balance? 
				money = 0;
			}

			if(level == -2){
				// We messed up badly, calculate level.
				level = 0;
				while(bank_content != null && bank_content.contains("@item@") && (bank_content.split("@item@").length > (getBankSlots(level) - 1)) && level < 17){
					level++;
				}
			}

			Inventory inv = null;
			/*while(bank_content != null && bank_content.contains("@item@") && (bank_content.split("@item@").length > (getBankSlots(level) - 1)) && level < 5){
				level++;
			}*/

			List<Inventory> bank_pages = new ArrayList<Inventory>();
			int max_slots = getBankSlots(level);

			if(bank_content != null && !(bank_content.equalsIgnoreCase("null"))){
				int total_pages = 1;
				int current_page = 1;
				if(max_slots > 54 && !(bank_content.contains("@page_break@"))){
					bank_content += "@page_break@";
				}

				if(bank_content.contains("@page_break@")){
					// They need to have multiple pages if it reaches this point.
					total_pages = bank_content.split("@page_break@").length;
					int last_page_slots = max_slots - ((total_pages - 1) * 54); // The amount of slots to allocate to the last page. All other pages = 54 storage, 63 for extra row.

					//log.info("total_pages - " + total_pages);
					
					for(String s_bank_content : bank_content.split("@page_break@")){
						if(s_bank_content.contains("@item@")){
							int page_slots = 63; // All pages are 63 slots except the last page.
							if(current_page == total_pages && total_pages > 1){
								page_slots = last_page_slots + 9; // Add 9, need the last row for the 'back' button.
							}
							//log.info("page_slots - " + page_slots);
							Inventory page = Hive.convertStringToInventory(null, s_bank_content, "Bank Chest (" + current_page + "/" + total_pages + ")", page_slots);

							if(page_slots == 63){
								int start_slot = 54 - 1;
								int end_slot = 63 - 1;
								boolean empty = true;

								start_slot--; // Offset by 1 since we add first.
								while(start_slot < end_slot){
									start_slot++;
									if(page.getItem(start_slot) != null && page.getItem(start_slot).getType() != Material.AIR){
										empty = false;
										break;
									}
								}

								//if(empty){
									if(current_page == 1){
										page.setItem(54, RealmMechanics.divider);
									}
									else{
										page.setItem(54, generateArrowButton("previous", current_page, total_pages));
									}
									page.setItem(55, RealmMechanics.divider);
									page.setItem(56, RealmMechanics.divider);
									page.setItem(57, RealmMechanics.divider);
									page.setItem(58, RealmMechanics.divider);
									page.setItem(59, RealmMechanics.divider);
									page.setItem(60, RealmMechanics.divider);
									page.setItem(61, RealmMechanics.divider);
									if(current_page == total_pages){ // Last page, no 'next'
										page.setItem(62, RealmMechanics.divider);
									}
									else{
										page.setItem(62, generateArrowButton("next", current_page, total_pages));
									}
								//}
								//else if(!empty){
									// They have items in the last row -- this should not be possible unless they have it from an old bug. We won't let them change pages I suppose?
									// TODO: What do we do with these noobs?
								//}
							}

							bank_pages.add(page);
							current_page++;
						}
					}
				}
				else if(!(bank_content.contains("@page_break@"))){
						Inventory inv_page = Hive.convertStringToInventory(null, bank_content, "Bank Chest (1/1)", getBankSlots(level)); 
						bank_pages.add(inv_page);
				}
			}

			else if(bank_pages.isEmpty()){
				bank_pages.add(Bukkit.createInventory(null, getBankSlots(level), "Bank Chest (1/1)"));
			}

			bank_contents.put(p_name, bank_pages);
			bank_map.put(p_name, money);
			bank_level.put(p_name, level);
			
			return true;

		} catch (SQLException ex) {
			log.log(Level.SEVERE, ex.getMessage(), ex);
			return false;

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

	public static void addMoneyToOfflinePlayerBank(final String p_name, int amount_to_add){
		Hive.sql_query.add("INSERT INTO bank_database (p_name, money)"
				+ " VALUES"
				+ "('"+ p_name + "', '"+ amount_to_add +"') ON DUPLICATE KEY UPDATE money=bank_database.money+" + amount_to_add + "");

		/*PreparedStatement pst = null;

		try {
			pst = ConnectionPool.getConneciton().prepareStatement(
					"SELECT money FROM bank_database WHERE p_name = '" + p_name + "'");

			pst.execute();
			ResultSet rs = pst.getResultSet();
			if(!rs.next()){return;}

			int money = rs.getInt("money");

			if(money < 0){
				// Negative balance? 
				money = 0;
			}

			money += amount_to_add;

			pst = ConnectionPool.getConneciton().prepareStatement( 
					"INSERT INTO bank_database (p_name, money)"
							+ " VALUES"
							+ "('"+ p_name + "', '"+ money +"') ON DUPLICATE KEY UPDATE money='" + money + "'");

			pst.executeUpdate();

			return;

		} catch (SQLException ex) {
			log.log(Level.SEVERE, ex.getMessage(), ex);
			return;

		} finally {
			try {
				if (pst != null) {
					pst.close();
				}

			} catch (SQLException ex) {
				log.log(Level.WARNING, ex.getMessage(), ex);
			}
		}*/
	}

	public static void uploadBankDatabaseData(final String p_name, boolean remove_when_done){
		String final_bank_content = null;
		int final_bank_net = -2;
		int final_bank_level = -2;

		if(bank_contents.containsKey(p_name)){
			for(Inventory inv : bank_contents.get(p_name)){
				String local_inv = Hive.convertInventoryToString(p_name, inv, false);
				final_bank_content += local_inv + "@page_break@"; // TODO: Randomize
			}

			if(final_bank_content.endsWith("@page_break@")){
				// Trim.
				final_bank_content = final_bank_content.substring(0, final_bank_content.lastIndexOf("@page_break@"));
			}

			// DEPRECIATED, multi-page banks.
			//final_bank_content = Hive.convertInventoryToString(p_name, bank_contents.get(p_name), false);
		}

		if(bank_map.containsKey(p_name)){
			final_bank_net = bank_map.get(p_name);
		}

		if(bank_level.containsKey(p_name)){
			final_bank_level = bank_level.get(p_name);
		}
		
		if(final_bank_content == null || final_bank_level == -2 || final_bank_net == -2){
			log.info("[MoneyMechanics] Skipping bank_database upload for " + p_name + ", data does not exist.");
			return; // Do not upload, something is wrong.
		}

		Connection con = null;
		PreparedStatement pst = null;

		try {
			pst = ConnectionPool.getConneciton().prepareStatement( 
					"INSERT INTO bank_database (p_name, content, money, level)"
							+ " VALUES"
							+ "('"+ p_name + "', '"+ StringEscapeUtils.escapeSql(final_bank_content) +"', '" + final_bank_net + "', '" + final_bank_level + "') ON DUPLICATE KEY UPDATE content = '" 
							+ StringEscapeUtils.escapeSql(final_bank_content) + "', money='" + final_bank_net + "', level='" + final_bank_level + "'");

			pst.executeUpdate();
			
			/*Hive.sql_query.add("INSERT INTO bank_database (p_name, content, money, level)"
							+ " VALUES"
							+ "('"+ p_name + "', '"+ final_bank_content +"', '" + final_bank_net + "', '" + final_bank_level + "') ON DUPLICATE KEY UPDATE content = '" 
							+ final_bank_content + "', money='" + final_bank_net + "', level='" + final_bank_level + "'");*/

		} catch(Exception err){
			err.printStackTrace();
		} finally {
			try {
				if (pst != null) {
					pst.close();
				}
				if (con != null) {
					con.close();
				}

			} catch (SQLException ex) {
				log.log(Level.WARNING, ex.getMessage(), ex);
			}
		}

		if(remove_when_done){
			bank_contents.remove(p_name);
			bank_map.remove(p_name);
			bank_level.remove(p_name);
		}

	}

	public static ItemStack signBankNote(ItemStack i, String name, String desc){
		ItemStack iss = i;
		List<String> new_lore = new ArrayList<String>();

		for(String s : desc.split(",")){
			if(s.length() <= 1){continue;}
			new_lore.add(s);
		}

		ItemMeta im = iss.getItemMeta();
		im.setLore(new_lore);
		im.setDisplayName(name);

		iss.setItemMeta(im);

		return iss;
	}


	public ItemStack markSplitting(ItemStack i){ // Mark the note being split as "Splitting" super annoying to do lol.
		List<String> new_lore = new ArrayList<String>();
		for(String s : i.getItemMeta().getLore()){
			if(!(s.contains("<SPLIT>")) && !(s.contains("Untradeable"))){
				new_lore.add(s);
			}
		}

		new_lore.add(ChatColor.RED.toString() + "<SPLIT>");
		new_lore.add(ChatColor.GRAY.toString() + "Untradeable");

		ItemMeta im = i.getItemMeta();
		im.setLore(new_lore);
		im.setDisplayName(ChatColor.GREEN.toString() + "Bank Note");

		i.setItemMeta(im);

		return i;
	}

	public ItemStack setCountMeta(ItemStack is, int amount){
		net.minecraft.server.v1_7_R1.ItemStack nmsstack = CraftItemStack.asNMSCopy(is);
		nmsstack.tag.setInt("count", amount);
		org.bukkit.inventory.ItemStack i = CraftItemStack.asCraftMirror(nmsstack);
		return i;
	}

	public static int getCountMeta(ItemStack is){
		net.minecraft.server.v1_7_R1.ItemStack nmsstack = CraftItemStack.asNMSCopy(is);

		int amount = 0;
		try{
			amount = nmsstack.tag.getInt("count");
		} catch(NullPointerException npe){
			return is.getAmount();
		}
		return amount;
	}

	public static ItemStack makeGems(int amount){
		ItemStack i = new ItemStack(Material.EMERALD, amount);
		List<String> new_lore = new ArrayList<String>(Arrays.asList(ChatColor.GRAY.toString() + "The currency of Andalucia"));

		ItemMeta im = i.getItemMeta();
		im.setLore(new_lore);

		im.setDisplayName(ChatColor.WHITE.toString() + "Gem");
		i.setItemMeta(im);
		i.setAmount(amount);

		return i;
	}

	public ItemStack markNotSplitting(ItemStack i){ // Mark the note being split as "Splitting" super annoying to do lol.
		ItemStack iss = i;

		List<String> new_lore = new ArrayList<String>();
		for(String s : i.getItemMeta().getLore()){
			if(!(s.contains("<SPLIT>")) && !(s.contains("Untradeable"))){
				new_lore.add(s);
			}
		}

		ItemMeta im = i.getItemMeta();
		im.setLore(new_lore);
		im.setDisplayName(ChatColor.GREEN.toString() + "Bank Note");

		i.setItemMeta(im);

		return i;

	}

	public boolean isSplitting(ItemStack i){

		if(i != null && i.hasItemMeta() && i.getItemMeta().hasLore()){
			List<String> lore = i.getItemMeta().getLore();
			for(String s : lore){
				s = ChatColor.stripColor(s);
				if(s.contains("<SPLIT>")){
					return true;
				}
			}
		}
		
		return false;
		
		/*NBTTagList description = CraftItemStack.asNMSCopy(i).getTag().getCompound("display").getList("Lore", 9);
		int x = 0;
		while(description.size() > x){
			if(description.get(x).toString().contains("<SPLIT>")){
				return true;
			}
			x++;
		}
		return false;*/
	}

	public static boolean isItemUnbankable(ItemStack is){
		if(is == null){
			return false;
		}
		if(!RealmMechanics.isItemTradeable(is)){
			// Not tradeable.
			if(is.getType() == Material.BREAD || (is.getType() == Material.POTION && is.getDurability() == (short)1)){
				return true;
			}
		}
		return false;
	}

	public int getSplitSlot(Player p){
		HashMap<Integer, ? extends ItemStack> invItems = p.getInventory().all(Material.PAPER);

		for (Map.Entry<Integer, ? extends ItemStack> entry : invItems
				.entrySet()) {
			int index = entry.getKey();
			ItemStack item = entry.getValue();
			if(isSplitting(item)){
				return index;
			}
		}

		return -1;
	}

	public static boolean isGemPouch(ItemStack is){
		if(is.getType() == Material.INK_SACK && is.getDurability() == (short)0 && is.hasItemMeta() && is.getItemMeta().hasDisplayName() && is.getItemMeta().getDisplayName().endsWith("g")){
			return true;
		}
		return false;
	}

	public static int getGemPouchWorth(ItemStack is){
		ItemMeta im = is.getItemMeta();
		String i_name = ChatColor.stripColor(im.getDisplayName());
		int worth = Integer.parseInt(i_name.substring(i_name.lastIndexOf(" ") + 1, i_name.lastIndexOf("g")));
		return worth;
	}

	public int getMaxPouchCapacity(ItemStack is){
		String i_name = is.getItemMeta().getDisplayName().toLowerCase();
		if(i_name.contains("small")){
			return 100;
		}
		if(i_name.contains("medium")){
			return 150;
		}
		if(i_name.contains("large")){
			return 200;
		}
		if(i_name.contains("gigantic")){
			return 300;
		}
		return 0;
	}

	public static void setPouchWorth(ItemStack is, int new_worth){
		ItemMeta im = is.getItemMeta();
		String i_name = im.getDisplayName();
		String new_i_name = i_name.substring(0, i_name.lastIndexOf(" ")); // Don't want the ##g.
		new_i_name = new_i_name + " " + ChatColor.GREEN.toString() + ChatColor.BOLD.toString() + new_worth + "g";
		im.setDisplayName(new_i_name);
		is.setItemMeta(im);
	}

	public static void addMoneyCert(Player p, int amount, boolean echo){
		ItemStack money = null;
		short real_id = 777;
		money = new ItemStack(Material.PAPER, 1, real_id);

		if(p.getInventory().firstEmpty() == -1){
			p.getWorld().dropItem(p.getLocation(), signBankNote(money, ChatColor.GREEN.toString() + "Bank Note", ChatColor.WHITE.toString() + ChatColor.BOLD.toString() +  "Value:" + ChatColor.WHITE.toString() + " " + amount + " Gems" + "," + ChatColor.GRAY.toString() + "Exchange at any bank for GEM(s)"));
			p.sendMessage(ChatColor.RED + "Because you had no room in your inventory, your new bank note has been placed at your character's feet.");
		}
		else{
			p.getInventory().setItem(p.getInventory().firstEmpty(), signBankNote(money, ChatColor.GREEN.toString() + "Bank Note", ChatColor.WHITE.toString() + ChatColor.BOLD.toString() +  "Value:" + ChatColor.WHITE.toString() + " " + amount + " Gems" + "," + ChatColor.GRAY.toString() + "Exchange at any bank for GEM(s)"));
			if(echo == true){
				p.sendMessage(ChatColor.GREEN + "You have signed a bank note for the value of " + ChatColor.BOLD + amount + " GEM(s).");
			}
		}

		p.playSound(p.getLocation(), Sound.ENDERDRAGON_WINGS, 1F, 1.2F);
	}   

	public static void updateMoney(Player p, int slot, int new_amount){
		p.getInventory().setItem(slot, signBankNote(new ItemStack(Material.PAPER, 1, (short)777), ChatColor.GREEN.toString() + "Bank Note", ChatColor.WHITE.toString() + ChatColor.BOLD.toString() +  "Value:" + ChatColor.WHITE.toString() + " " + new_amount + " Gems" + "," + ChatColor.GRAY.toString() + "Exchange at any bank for GEM(s)"));
		//addMoneyCert(p, new_amount, false); Depreciated.
	}

	public static boolean isNextArrow(ItemStack is){
		if(is != null && is.getType() == Material.ARROW && is.hasItemMeta() && is.getItemMeta().hasDisplayName() && is.getDurability() == (short)1 && is.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.YELLOW.toString() 
				+ "Next Page " + ChatColor.BOLD.toString() + "->")){
			return true;
		}
		return false;
	}

	public static boolean isPreviousArrow(ItemStack is){
		if(is != null && is.getType() == Material.ARROW && is.hasItemMeta() && is.getItemMeta().hasDisplayName() && is.getDurability() == (short)1 && is.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.YELLOW.toString() + ChatColor.BOLD + "<-" + ChatColor.YELLOW.toString() 
				+ " Previous Page ")){
			return true;
		}
		return false;
	}

	public static int getBankNoteValue(ItemStack i){
		if(i != null && i.hasItemMeta() && i.getItemMeta().hasLore()){
			List<String> lore = i.getItemMeta().getLore();
			for(String s : lore){
				s = ChatColor.stripColor(s);
				if(s.startsWith("Value:")){
					int value = Integer.parseInt(s.substring(s.indexOf(" ") + 1, s.lastIndexOf(" ")));
					return value;
				}
			}
		}
		
		return 0;
		
		/*if(!(i instanceof CraftItemStack)){
			return 0;
		}
		CraftItemStack css = (CraftItemStack)i;
		try{
			try{
				try{
					NBTTagList description = CraftItemStack.asNMSCopy(css).getTag().getCompound("display").getList("Lore", 9);
					String value_string = description.get(0).toString();

					int value = Integer.parseInt(value_string.substring(value_string.indexOf(" ") + 1, value_string.lastIndexOf(" ")));

					return value;

				} catch(NumberFormatException e){
					return 0;
				}
			} catch(IndexOutOfBoundsException e){
				return 0;
			}
		} catch(NullPointerException e){
			return 0;
		}*/
	}


	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e){
		final Player p = e.getPlayer();

		bank_upgrade_codes.remove(p);

		if(split_map.containsKey(p)){
			int slot = getSplitSlot(p);
			ItemStack i = p.getInventory().getItem(slot);
			p.getInventory().setItem(slot, markNotSplitting(i));
			split_map.remove(p);
		}

	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onItemDrop(PlayerDropItemEvent e){
		if(e.getItemDrop().getItemStack().getType() == Material.PAPER){
			Player p = e.getPlayer();
			if(split_map.containsKey(p)){
				p.sendMessage(ChatColor.RED + "Please finish splitting your bank note before attempting to drop any other notes. Type 'cancel' to void the split.");
				e.setCancelled(true);
			}
		}
	}

	private String insertCommas(String str)
	{
		if(str.length() < 4){
			return str;
		}
		return insertCommas(str.substring(0, str.length() - 3)) + "," + str.substring(str.length() - 3, str.length());
	}

	public String getUpgradeAuthenticationCode(Player p){
		if(bank_upgrade_codes.containsKey(p)){
			return bank_upgrade_codes.get(p);
		}
		else{
			return null;
		}
	}

	public int getBankUpgradeCost(int new_tier){
		if(new_tier == 1){return 200;}
		if(new_tier == 2){return 500;}
		if(new_tier == 3){return 1200;}
		if(new_tier == 4){return 3000;}
		if(new_tier == 5){return 7000;}
		if(new_tier >= 6){return 15000;} // All tiers above 6 cost 15,000.
		if(new_tier == 7){return 18000;}
		if(new_tier == 8){return 20000;}
		if(new_tier == 9){return 22000;}
		if(new_tier == 10){return 24000;}
		if(new_tier == 11){return 26000;}
		if(new_tier == 12){return 28000;}
		if(new_tier == 13){return 30000;}
		if(new_tier == 14){return 32000;}
		if(new_tier == 15){return 34000;}
		if(new_tier == 16){return 36000;}
		if(new_tier == 17){return 40000;}
		return 0;
	}

	public void upgradeBank(Player p, int new_level, boolean interval){
		int r_new_level = 0;

		if(interval == true){
			r_new_level = MoneyMechanics.bank_level.get(p.getName()) + 1;
		}
		else if(interval == false){
			r_new_level = new_level;
		}

		bank_level.put(p.getName(), r_new_level);
		List<Inventory> bank_pages = bank_contents.get(p.getName());
		int total_pages = 1;
		int current_page = 1;

		if(new_level >= 6){
			total_pages++;
			current_page++;
		}
		if(new_level >= 12){
			total_pages++;
			current_page++;
		}

		if(new_level == 6 || new_level == 12){
			// Add new page. Next button on the previous page.
			Inventory new_page = Bukkit.createInventory(null, 9 + 9, "Bank Chest (" + current_page + "/" + total_pages + ")"); // 9 slots, extra  9 at bottom for previous button.

			new_page.setItem(9, generateArrowButton("previous", current_page, total_pages));
			new_page.setItem(10, RealmMechanics.divider);
			new_page.setItem(11, RealmMechanics.divider);
			new_page.setItem(12, RealmMechanics.divider);
			new_page.setItem(13, RealmMechanics.divider);
			new_page.setItem(14, RealmMechanics.divider);
			new_page.setItem(15, RealmMechanics.divider);
			new_page.setItem(16, RealmMechanics.divider);
			new_page.setItem(17, RealmMechanics.divider);

			int previous_page_num = bank_contents.get(p.getName()).size() - 1;
			Inventory old_previous_page = bank_contents.get(p.getName()).get(previous_page_num);
			Inventory new_previous_page = Bukkit.createInventory(null, 54 + 9, "Bank Chest (" + (previous_page_num + 1) + "/" + total_pages + ")");

			// Copy old single page into a multi-page supported system.
			int index = -1;
			for(ItemStack is : old_previous_page.getContents()){
				index++;
				if(is == null || is.getType() == Material.AIR || is.getType() == Material.EMERALD){
					continue;
				}
				new_previous_page.setItem(index, is);
			}

			if(bank_contents.get(p.getName()).size() <= 1){ 
				new_previous_page.setItem(54, RealmMechanics.divider);
			}
			else{
				new_previous_page.setItem(54, generateArrowButton("previous", current_page, total_pages));
			}
			//new_previous_page.setItem(54, RealmMechanics.divider);
			new_previous_page.setItem(55, RealmMechanics.divider);
			new_previous_page.setItem(56, RealmMechanics.divider);
			new_previous_page.setItem(57, RealmMechanics.divider);
			new_previous_page.setItem(58, RealmMechanics.divider);
			new_previous_page.setItem(59, RealmMechanics.divider);
			new_previous_page.setItem(60, RealmMechanics.divider);
			new_previous_page.setItem(61, RealmMechanics.divider);
			new_previous_page.setItem(62, generateArrowButton("next", current_page, total_pages));

			bank_pages.set(previous_page_num, new_previous_page);
			bank_pages.add(new_page);
			return; // Don't do anything else.
		}
		else if(new_level <= 5){
			Inventory i = Bukkit.createInventory(null, getBankSlots(r_new_level), "Bank Chest (1/1)");
			for(ItemStack is : bank_contents.get(p.getName()).get(bank_contents.get(p.getName()).size() - 1)){
				if(is != null && !(is.getType() == Material.AIR) && is.getType() != Material.EMERALD){
					i.setItem(i.firstEmpty(), is);
				}
			}

			bank_pages.set(0, i);
		}
		else if(new_level > 6){
			// We need to get the last page and upgrade it.
			int last_page_index = bank_contents.get(p.getName()).size() - 1;
			int last_page_slots = getBankSlots(new_level) - ((total_pages - 1) * 54); // Will this work?
			
			Inventory i = Bukkit.createInventory(null, last_page_slots + 9, "Bank Chest (" + current_page + "/" + total_pages + ")");
			for(ItemStack is : bank_contents.get(p.getName()).get(last_page_index)){
				if(is != null && !(is.getType() == Material.AIR) && is.getType() != Material.EMERALD && is.getType() != Material.PISTON_MOVING_PIECE && !isPreviousArrow(is) && !isNextArrow(is)){
					i.setItem(i.firstEmpty(), is);
				}
			}
			
				i.setItem(last_page_slots, generateArrowButton("previous", current_page, total_pages));
				i.setItem(last_page_slots + 1, RealmMechanics.divider);
				i.setItem(last_page_slots + 2, RealmMechanics.divider);
				i.setItem(last_page_slots + 3, RealmMechanics.divider);
				i.setItem(last_page_slots + 4, RealmMechanics.divider);
				i.setItem(last_page_slots + 5, RealmMechanics.divider);
				i.setItem(last_page_slots + 6, RealmMechanics.divider);
				i.setItem(last_page_slots + 7, RealmMechanics.divider);
				i.setItem(last_page_slots + 8, RealmMechanics.divider);

				bank_pages.set(last_page_index, i);
		}

		bank_contents.put(p.getName(), bank_pages);
	}

	public boolean giveGems(Player p, int Gems_worth){
		if(Gems_worth > 64){
			int space_needed = Math.round(Gems_worth / 64) + 1;
			int count = 0;
			ItemStack[] contents = p.getInventory().getContents();
			for (int z = 0; z < contents.length; z++) {
				if (contents[z] == null || contents[z].getType() == Material.AIR){
					count++;
				}
			}
			int empty_slots = count;

			if(space_needed > empty_slots){
				p.sendMessage(ChatColor.RED + "You do not have enough space in your inventory to withdraw " + Gems_worth + " GEM(s).");
				p.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "REQ: " + space_needed + " slots");
				return false;
			}
		}

		while(Gems_worth > 0){
			while(Gems_worth > 64){
				if(p.getInventory().firstEmpty() == -1){
					p.getWorld().dropItemNaturally(p.getLocation(), makeGems(Gems_worth));
					p.sendMessage(ChatColor.RED + "" + ChatColor.UNDERLINE + "WARNING: " + ChatColor.RED + "Not all GEMS fit in your inventory, the remainder have been dropped on the ground nearby.");
					Gems_worth = 0;
				}
				p.getInventory().setItem(p.getInventory().firstEmpty(), makeGems(64));
				Gems_worth -= 64;
			}

			if(p.getInventory().firstEmpty() == -1){
				p.getWorld().dropItemNaturally(p.getLocation(), makeGems(Gems_worth));
				p.sendMessage(ChatColor.RED + "" + ChatColor.UNDERLINE + "WARNING: " + ChatColor.RED + "Not all GEMS fit in your inventory, the remainder have been dropped on the ground nearby.");
				Gems_worth = 0;
			}
			else if(p.getInventory().firstEmpty() != -1){
				p.getInventory().setItem(p.getInventory().firstEmpty(), makeGems(Gems_worth));
				//p.getInventory().addItem(new ItemStack(Material.EMERALD, Gems_worth));
				Gems_worth = 0;
			}
		}
		if(Gems_worth > 0){
			p.getWorld().dropItemNaturally(p.getLocation(), makeGems(Gems_worth));
			p.sendMessage(ChatColor.RED + "" + ChatColor.UNDERLINE + "WARNING: " + ChatColor.RED + "Not all GEMS fit in your inventory, the remainder have been dropped on the ground nearby.");
			Gems_worth = 0;
		}
		p.updateInventory();
		return true;
	}


	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerChatEvent(AsyncPlayerChatEvent e){
		Player p = e.getPlayer();
		if(!split_map.containsKey(p) && !withdraw_map.containsKey(p) && !bank_upgrade_codes.containsKey(p)){return;}
		e.setCancelled(true);

		if(bank_upgrade_codes.containsKey(p)){
			String auth_code = getUpgradeAuthenticationCode(p);
			int new_tier = 0;
			if(auth_code == null){return;}
			e.setCancelled(true);

			if(e.getMessage().contains(auth_code)){
				new_tier = bank_level.get(p.getName()) + 1; //Integer.parseInt(auth_code.substring(0, 1));

				/*if(new_tier > 5){
					p.sendMessage(ChatColor.RED + "You cannot upgrade your bank; already at highest available tier.");
					bank_upgrade_codes.remove(p);
					return;
				}*/

				int cost = getBankUpgradeCost(new_tier);
				if(!(RealmMechanics.doTheyHaveEnoughMoney(p, cost))){
					p.sendMessage(ChatColor.RED + "You do not have enough gems to purchase this upgrade. Upgrade cancelled.");
					p.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "COST: " + ChatColor.RED + cost + ChatColor.BOLD + "G");
					bank_upgrade_codes.remove(p);
					return;
				}

				RealmMechanics.subtractMoney(p, cost);
				upgradeBank(p, new_tier, true);
				bank_upgrade_codes.remove(p);
				p.sendMessage("");
				p.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "*** BANK UPGRADE TO LEVEL " + new_tier + " COMPLETE ***");
				p.sendMessage(ChatColor.GRAY + "You now have " + getBankSlots(MoneyMechanics.bank_level.get(p.getName())) + " bank slots available.");
				p.playSound(p.getLocation(), Sound.LEVEL_UP, 1F, 1.25F);
			}
			else{
				p.sendMessage(ChatColor.RED + "Invalid authentication code entered. Bank upgrade cancelled.");
				bank_upgrade_codes.remove(p);
			}
		}

		if(withdraw_map.containsKey(p)){
			int net_worth = bank_map.get(p.getName());
			int withdraw_amount;
			if(e.getMessage().equals("cancel")){
				withdraw_map.remove(p);
				withdraw_type.remove(p);
				p.sendMessage(ChatColor.RED + "Withdrawl operation - " + ChatColor.BOLD + "CANCELLED");
				//p.sendMessage(ChatColor.GRAY + "Banker: " + ChatColor.WHITE + "Goodbye!");
				return;
			}
			try{
				withdraw_amount = Integer.parseInt(e.getMessage());
			} catch(NumberFormatException ex){
				p.sendMessage(ChatColor.RED + "Please enter a NUMBER, the amount you'd like To WITHDRAW from your bank account. Or type 'cancel' to void the withdrawl.");
				return;
			}

			if(withdraw_amount > net_worth){
				//p.sendMessage(ChatColor.RED + "You cannot withdraw more Gems than you have stored.");
				p.sendMessage(ChatColor.GRAY + "Banker: " + ChatColor.WHITE + "I'm sorry sir, but you only have " + net_worth + " GEM(s) stored in our bank.");
				p.sendMessage(ChatColor.GRAY + "You cannot withdraw more GEMS than you have stored.");
				//p.sendMessage(ChatColor.GREEN + "Current Balance: " + bank_map.get(p.getName()) + " Gem(s)");
				return;
			}

			if(withdraw_amount <= 0){
				p.sendMessage(ChatColor.RED + "You must enter a POSITIVE amount.");
				return;
			}

			if(withdraw_type.get(p).equalsIgnoreCase("raw")){
				withdraw_map.remove(p);
				withdraw_type.remove(p);

				boolean result = giveGems(p, withdraw_amount);

				if(result == true){
					p.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "New Balance: " + ChatColor.GREEN +  (net_worth - withdraw_amount) + " GEM(s)");
					p.sendMessage(ChatColor.GRAY + "You have withdrawn " + withdraw_amount + " GEM(s) from your bank account.");
					p.sendMessage(ChatColor.GRAY + "Banker: " + ChatColor.WHITE + "Here are your Gems, thank you for your business!");
					p.playSound(p.getLocation(), Sound.ORB_PICKUP, 1F, 1F);
					bank_map.put(p.getName(), (net_worth - withdraw_amount));
				}
			}
			else if(withdraw_type.get(p).equalsIgnoreCase("note")){
				withdraw_map.remove(p);
				withdraw_type.remove(p);
				bank_map.put(p.getName(), (net_worth - withdraw_amount));
				addMoneyCert(p, withdraw_amount, false);
				p.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "New Balance: " + ChatColor.GREEN +  (net_worth - withdraw_amount) + " GEM(s)");
				//p.sendMessage(ChatColor.GRAY + "You have converted " + withdraw_amount + " GEM(s) from your bank account into a " + ChatColor.BOLD.toString() + "GEM NOTE.");
				p.sendMessage(ChatColor.GRAY + "Banker: " + ChatColor.WHITE + "Here are your Gems, thank you for your business!");
			}


		}

		if(split_map.containsKey(p)){
			ItemStack cert = p.getInventory().getItem(getSplitSlot(p));
			int Gem_val = getBankNoteValue(cert);
			int split_amount;
			if(e.getMessage().equals("cancel")){
				split_map.remove(p);
				int slot = getSplitSlot(p);
				ItemStack i = p.getInventory().getItem(slot);
				p.getInventory().setItem(slot, markNotSplitting(i));
				p.sendMessage(ChatColor.RED + "Bank note operation cancelled.");
				return;
			}
			try{
				split_amount = Integer.parseInt(e.getMessage());
			} catch(NumberFormatException ex){
				p.sendMessage(ChatColor.RED + "Please enter a NUMBER, the amount you'd like to sign off from your bank note valued at " + Gem_val + " Gems.");
				return;
			}

			int dif = Gem_val - split_amount;

			if(dif <= 0){
				p.sendMessage(ChatColor.RED + "This bank note is only worth " + Gem_val + " gem(s).");
				return;
			}

			if(split_amount <= 0){
				p.sendMessage(ChatColor.RED + "You must split a POSITIVE amount.");
				return;
			}

			split_map.remove(p);

			if(!Hive.isHiveOnline()){
				p.sendMessage(ChatColor.RED + "This server is currently desynced from the HIVE, this action cannot be completed at this time.");
				return;
			}

			addMoneyCert(p, split_amount, true);
			int slot = getSplitSlot(p);
			ItemStack i = p.getInventory().getItem(slot);
			p.getInventory().setItem(slot, signBankNote(i, ChatColor.GREEN.toString() + "Bank Note", ChatColor.WHITE.toString() + ChatColor.BOLD.toString() +  "Value:" + ChatColor.WHITE.toString() + " " + dif + " Gems" + "," + ChatColor.GRAY.toString() + "Exchange at any bank for GEM(s)"));
		}

	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onPlayerInteract(PlayerInteractEvent e){
		ItemStack i = e.getItem();

		if(e.hasItem() && isGemPouch(e.getItem())){
			e.setCancelled(true);
			e.setUseInteractedBlock(Result.DENY);
			e.setUseItemInHand(Result.DENY);
			Player pl = e.getPlayer();
			pl.sendMessage(ChatColor.YELLOW + "To " + ChatColor.BOLD + "DEPOSIT" + ChatColor.YELLOW + " GEM(s), drag them onto the pouch in your inventory. To " + ChatColor.BOLD + "WITHDRAW" 
					+ ChatColor.YELLOW + " GEM(s), " + ChatColor.UNDERLINE + "right click" + ChatColor.YELLOW + " the pouch in your inventory.");
			pl.sendMessage(ChatColor.GRAY + "You can deposit gems in pouches directly into your bank by dragging them into your bank chest like you would GEM NOTES.");
			return;
		}

		if(i == null){return;}

		if(i.getType() == Material.PAPER && (e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK)){
			Player p = e.getPlayer();

			if(withdraw_map.containsKey(p)){
				p.sendMessage(ChatColor.RED + "You cannot split bank notes while you have a pending withdrawl request. Type 'cancel' or finish that operation first.");
				return;
			}

			if(split_map.containsKey(p)){
				p.sendMessage(ChatColor.RED + "You already have a pending bank note split request. Type 'cancel' or finish that operation first.");
				return;
			}

			if(i.getAmount() > 1){
				return; // Do not split stacked notes.
			}

			// Prevent any interaction events -- bank chest, block break (realm) etc.
			e.setUseItemInHand(Result.DENY);
			e.setUseInteractedBlock(Result.DENY);
			e.setCancelled(true);

			int cert_val = getBankNoteValue(i);
			p.sendMessage(ChatColor.GRAY + "This bank note is worth " + ChatColor.GREEN + cert_val + " Gems." + ChatColor.GRAY + " Please enter the amount you'd like to sign an additional bank note for. Alternatively, type " + ChatColor.RED + "'cancel'" + ChatColor.GRAY + " to stop this operation.");
			split_map.put(p, 0);
			p.setItemInHand(markSplitting(i));
		}
	}

	public static boolean isThereABankChestNear(Block b, int maxradius) {
		BlockFace[] faces = {BlockFace.UP, BlockFace.NORTH, BlockFace.EAST};
		BlockFace[][] orth = {{BlockFace.NORTH, BlockFace.EAST}, {BlockFace.UP, BlockFace.EAST}, {BlockFace.NORTH, BlockFace.UP}};
		for (int r = 0; r <= maxradius; r++) {
			for (int s = 0; s < 6; s++) {
				BlockFace f = faces[s%3];
				BlockFace[] o = orth[s%3];
				if (s >= 3)
					f = f.getOppositeFace();
				if(!(b.getRelative(f, r) == null)){
					Block c = b.getRelative(f, r);

					for (int x = -r; x <= r; x++) {
						for (int y = -r; y <= r; y++) {
							Block a = c.getRelative(o[0], x).getRelative(o[1], y);
							if (a.getType() == Material.ENDER_CHEST)
								return true;
						}
					}
				}
			}
		}
		return false;
	}

	public void generateUpgradeAuthenticationCode(Player p, String tier){
		StringBuffer sb = new StringBuffer(4);  
		for (int i=0;  i<4;  i++) {  
			int ndx = (int)(Math.random()*ALPHA_NUM.length());  
			sb.append(ALPHA_NUM.charAt(ndx));  
		}  

		bank_upgrade_codes.put(p, tier + sb.toString());
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onPlayerOpenChest(PlayerInteractEvent e){
		final Player p = (Player) e.getPlayer();

		if(e.getAction() == Action.RIGHT_CLICK_BLOCK){
			if(split_map.containsKey(p)){
				e.setCancelled(true);
				p.sendMessage(ChatColor.RED + "You cannot perform that action while splitting a bank note. Type 'cancel' and try again.");
				return;
			}
		}

		if(e.getAction() == Action.LEFT_CLICK_BLOCK && e.getClickedBlock().getType() == Material.ENDER_CHEST){
			e.setCancelled(true);

			if(!p.getWorld().getName().equalsIgnoreCase(Bukkit.getWorlds().get(0).getName()) && !(InstanceMechanics.isInstance(p.getWorld().getName()))){
				String w_name = p.getWorld().getName();
				if(!Bukkit.getOfflinePlayer(w_name).isOp()){
					e.setCancelled(true);
					p.sendMessage(ChatColor.RED + "You cannot access bank chests in player owned realms.");
					return;
				}
			}

			if(!ShopMechanics.hasCollectionBinItems(p.getName())){
				p.sendMessage(ChatColor.RED + "You have no items in your collection bin. To access your bank account, " + ChatColor.UNDERLINE + "RIGHT CLICK" + ChatColor.RED + " the bank chest.");
				return;
			}

			if(withdraw_map.containsKey(p)){
				e.setCancelled(true);
				p.sendMessage(ChatColor.RED + "You cannot perform that action while you have a pending withdrawl request. Type 'cancel' and try again.");
				return;
			}

			if(no_bank_use == true){
				e.setCancelled(true);
				p.sendMessage(ChatColor.RED + "You cannot access your bank while the server is rebooting.");
				return;
			}

			updateStaticCashStack(p);

			Inventory echest = bank_contents.get(p.getName()).get(0);
			/*if(Hive.forum_usergroup.get(p.getName()) == 9 && !(p.getInventory().contains(Material.MONSTER_EGG)) && !(echest.contains(Material.MONSTER_EGG))){
					echest.addItem(PetMechanics.generatePetEgg(EntityType.ZOMBIE));
				}*/
			p.openInventory(echest);
			p.playSound(p.getLocation(), Sound.CHEST_OPEN, 1F, 1F);

		}

		if(e.getAction() == Action.RIGHT_CLICK_BLOCK && e.getClickedBlock().getType() == Material.ENDER_CHEST){
			e.setCancelled(true);

			if(!p.getWorld().getName().equalsIgnoreCase(Bukkit.getWorlds().get(0).getName()) && !(InstanceMechanics.isInstance(p.getWorld().getName()))){
				String w_name = p.getWorld().getName();
				if(!Bukkit.getOfflinePlayer(w_name).isOp()){
					e.setCancelled(true);
					p.sendMessage(ChatColor.RED + "You " +ChatColor.UNDERLINE + "cannot" + ChatColor.RED + " access bank chests in player owned realms.");
					log.info("[MoneyMechanics] Player " + p.getName() + " accessed ender chest in " + w_name + ".");
					return;
				}
			}

			if(withdraw_map.containsKey(p)){
				e.setCancelled(true);
				p.sendMessage(ChatColor.RED + "You cannot perform that action while you have a pending withdrawl request. Type 'cancel' and try again.");
				return;
			}

			if(p.isSneaking() && !TutorialMechanics.onTutorialIsland(p)){
				if(!(bank_level.containsKey(p.getName()))){
					log.info("[MoneyMechanics] Could not get cached bank level value for: " + p.getName());
					// TODO: Why does this every get thrown? ^
				}
				int bank_tier = MoneyMechanics.bank_level.get(p.getName());
				int next_bank_tier = bank_tier + 1;
				if(next_bank_tier >= 18){
					p.sendMessage(ChatColor.RED + "Your bank is already at it's maximum size. (162 slots)");
					return;
				}
				int upgrade_cost = getBankUpgradeCost(next_bank_tier);
				generateUpgradeAuthenticationCode(p, String.valueOf(next_bank_tier));

				p.sendMessage("");
				p.sendMessage(ChatColor.DARK_GRAY + "           *** " + ChatColor.GREEN + ChatColor.BOLD + "Bank Upgrade Confirmation" + ChatColor.DARK_GRAY + " ***");
				p.sendMessage(ChatColor.DARK_GRAY + "           CURRENT Slots: " + ChatColor.GREEN + getBankSlots(bank_tier) + ChatColor.DARK_GRAY + "          NEW Slots: " + ChatColor.GREEN + getBankSlots(next_bank_tier));
				//p.sendMessage(ChatColor.DARK_GRAY + "FROM Tier " + ChatColor.GREEN + bank_tier + ChatColor.DARK_GRAY + " TO " + ChatColor.GREEN + next_bank_tier);
				p.sendMessage(ChatColor.DARK_GRAY + "                  Upgrade Cost: " + ChatColor.GREEN + "" + upgrade_cost + " Gem(s)");
				p.sendMessage("");
				p.sendMessage(ChatColor.GREEN + "Enter the code '" + ChatColor.BOLD + getUpgradeAuthenticationCode(p) + ChatColor.GREEN + "' to confirm your upgrade.");
				p.sendMessage("");
				p.sendMessage("" + ChatColor.RED + ChatColor.BOLD + "WARNING:" + ChatColor.RED + " Bank upgrades are " + ChatColor.BOLD + ChatColor.RED + "NOT" + ChatColor.RED + " reversible or refundable. Type 'cancel' to void this upgrade request.");
				p.sendMessage("");
				return;
			}

			if(no_bank_use == true){
				e.setCancelled(true);
				p.sendMessage(ChatColor.RED + "You cannot access your bank while the server is rebooting.");
				return;
			}

			if(TutorialMechanics.onTutorialIsland(p.getLocation())){
				e.setCancelled(true);
				p.sendMessage(ChatColor.RED + "You " + ChatColor.UNDERLINE + "cannot" + ChatColor.RED + " access your bank while on Tutorial Island.");
				return;
			}

			if(ShopMechanics.hasCollectionBinItems(p.getName())){
				p.sendMessage(ChatColor.GRAY + "Banker: " + ChatColor.WHITE + "Hello " + p.getName() + ", you have some items waiting for you in your collection bin! If you want to access your bank inventory instead, just " + ChatColor.UNDERLINE + "SNEAK + LEFT CLICK" + ChatColor.WHITE + " the bank chest.");
				Inventory cb = ShopMechanics.collection_bin.get(p.getName());
				List<ItemStack> new_contents = new ArrayList<ItemStack>();
				for(ItemStack is : cb.getContents()){
					if(is == null || is.getType() == Material.AIR){
						continue;
					}
					new_contents.add(ShopMechanics.removePrice(is));
				}

				cb.clear();

				for(ItemStack is : new_contents){
					cb.setItem(cb.firstEmpty(), is);
				}

				p.openInventory(cb);
				return;
			}

			updateStaticCashStack(p);

			if(!(bank_contents.containsKey(p.getName()) || !(bank_map.containsKey(p.getName())))){
				log.info("[MoneyMechanics] Failed to load bank data for " + p.getName() + ".");
				return;
			}

			Inventory echest = bank_contents.get(p.getName()).get(0);

			p.openInventory(echest);
			p.playSound(p.getLocation(), Sound.CHEST_OPEN, 1F, 1F);
		}
	}

	@EventHandler
	public void onInventoryOpenEvent(InventoryOpenEvent e){
		// Cancel any splitting.
		Player p = (Player) e.getPlayer();

		if(split_map.containsKey(p)){
			e.setCancelled(true);
			p.sendMessage(ChatColor.RED + "You " + ChatColor.UNDERLINE + "cannot" + ChatColor.RED + " perform that action while splitting a bank note. Type 'cancel' and try again.");
		}
	}

	public boolean isStaticGem(ItemStack is){
		if(is != null && is.getType() == Material.EMERALD && is.hasItemMeta() && is.getItemMeta().hasDisplayName() && is.getItemMeta().getDisplayName().endsWith(ChatColor.BOLD.toString() + " GEM(s)")){
			return true;
		}
		return false;
	}
	

	public void unstackAllPouches(Player pl){
		Inventory inv = pl.getInventory();
		for(ItemStack is : inv.getContents()){
			if(is == null || is.getType() == Material.AIR){
				continue;
			}
			if(isGemPouch(is)){
				if(is.getAmount() > 1){
					int amount = is.getAmount();
					ItemStack one_pouch = CraftItemStack.asCraftCopy(is);
					one_pouch.setAmount(1);
					is.setAmount(1);
					while(amount > 1){
						amount--;
						if(inv.firstEmpty() != -1){
							inv.setItem(inv.firstEmpty(), one_pouch);
						}
						else{
							// Full inventory. Drop.
							pl.getWorld().dropItem(pl.getLocation(), one_pouch);
						}
					}
				}
			}
		}
	}

	@EventHandler
	public void onInventoryCloseEvent(InventoryCloseEvent e) throws IOException{
		Player p = (Player) e.getPlayer();

		if(e.getInventory().getName().equalsIgnoreCase("container.crafting")){
			unstackAllPouches(p);
		}

		if(e.getInventory().getName().equalsIgnoreCase("Collection Bin")){
			// They can't possible have a store open if they have a collection bin.
			ShopMechanics.shop_stock.remove(p.getName());
			ShopMechanics.need_sql_update.add(p.getName());
			
			Inventory cb = e.getInventory();
			if(cb.contains(Material.NETHER_STAR)){
				cb.remove(Material.NETHER_STAR);
			}

			if(cb.contains(Material.WRITTEN_BOOK)){
				for(ItemStack is : cb.all(Material.WRITTEN_BOOK).values()){
					if(CommunityMechanics.isSocialBook(is)){
						cb.remove(is);
						break;
					}
				}
			}
			//log.info(String.valueOf(cb.getContents().length));
			int items_left = 0;
			for(ItemStack i : cb.getContents()){
				if(i != null && i.getType() != Material.AIR){
					items_left++;
				}
			}
			if(items_left <= 0){
				ShopMechanics.collection_bin.remove(p.getName());
				ShopMechanics.shop_stock.remove(p.getName());
				ShopMechanics.need_sql_update.add(p.getName()); // Remove shop_backup data since collection_bin is now empty.
				p.sendMessage(ChatColor.GRAY + "Banker: " + ChatColor.WHITE + "Enjoy your items.");
			} 
			else if(items_left > 0){
				ShopMechanics.collection_bin.put(p.getName(), cb);
				p.sendMessage(ChatColor.GRAY + "Banker: " + ChatColor.WHITE + "There are still some items in your collection bin!");
			}

			p.playSound(p.getLocation(), Sound.CHEST_CLOSE, 1F, 1F);
		}

		if(e.getInventory().getName().startsWith("Bank Chest")){
			if(e.getInventory().firstEmpty() == -1 && e.getInventory().getSize() <= 45){
				// TODO: Rewrite. Multiple pages. Get bank level.
				p.sendMessage(ChatColor.GRAY + "Banker: " + ChatColor.WHITE + "To purchase more bank slots, " + ChatColor.GREEN + ChatColor.BOLD + "SNEAK + RIGHT-CLICK" + ChatColor.WHITE + " the banking chest.");
			}

			if(e.getInventory().contains(Material.NETHER_STAR)){
				e.getInventory().remove(Material.NETHER_STAR);
			}

			if(e.getInventory().contains(Material.WRITTEN_BOOK)){
				for(ItemStack is : e.getInventory().all(Material.WRITTEN_BOOK).values()){
					if(CommunityMechanics.isSocialBook(is)){
						e.getInventory().remove(is);
						break;
					}
				}
			}

			if(e.getInventory().contains(Material.PAPER)){
				for(Entry<Integer, ? extends ItemStack> data : e.getInventory().all(Material.PAPER).entrySet()){
					int slot = data.getKey();
					ItemStack is = data.getValue();
					if(is.hasItemMeta() && is.getItemMeta().hasDisplayName() && is.getItemMeta().getDisplayName().contains("Bank Note")){
						int amount = getBankNoteValue(is);
						e.getInventory().setItem(slot, new ItemStack(Material.AIR));
						int net = bank_map.get(p.getName());
						net+=amount;
						p.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "+" + ChatColor.GREEN + amount + ChatColor.BOLD + "G" + ChatColor.GREEN + ", " + ChatColor.BOLD + "New Balance: " + ChatColor.GREEN + net + " GEM(s)");
						bank_map.put(p.getName(), net);
						updateStaticCashStack(p); 
						p.playSound(p.getLocation(), Sound.ORB_PICKUP, 1F, 1F);
					}
				}
			}

			String title = e.getInventory().getTitle();
			int page = Integer.parseInt(title.substring(title.lastIndexOf("(") + 1, title.lastIndexOf("/")));
			List<Inventory> bank_pages = bank_contents.get(p.getName());
			bank_pages.set((page - 1), e.getInventory());
			bank_contents.put(p.getName(), bank_pages);

			int junk_count = 0;
			int weapon_count = 0;
			for(ItemStack is : e.getInventory()){
				if(is == null){
					continue;
				}
				if(!ItemMechanics.getDamageData(is).equalsIgnoreCase("no")){
					weapon_count++;
					continue;
				}
				if(is.getType() == Material.ENDER_PEARL || is.getType() == Material.MELON_BLOCK){
					junk_count += is.getAmount();
				}
			}
			
			if(junk_count >= 100){
				AchievmentMechanics.addAchievment(p.getName(), "I might need it later!");
			}
			
			if(weapon_count >= 10){
				AchievmentMechanics.addAchievment(p.getName(), "Personal Arsenal");
			}
			
			// Achievment Checks.
			if(bank_map.containsKey(p.getName())){
				int bank_net = bank_map.get(p.getName());
				if(bank_net >= 100){
					AchievmentMechanics.addAchievment(p.getName(), "Acquire Currency I");
					if(bank_net >= 1000){
						AchievmentMechanics.addAchievment(p.getName(), "Acquire Currency II");
						if(bank_net >= 5000){
							AchievmentMechanics.addAchievment(p.getName(), "Acquire Currency III");
							if(bank_net >= 10000){
								AchievmentMechanics.addAchievment(p.getName(), "Acquire Currency IV");
								if(bank_net >= 50000){
									AchievmentMechanics.addAchievment(p.getName(), "Acquire Currency V");
									if(bank_net >= 100000){
										AchievmentMechanics.addAchievment(p.getName(), "Acquire Currency VI");
										if(bank_net >= 500000){
											AchievmentMechanics.addAchievment(p.getName(), "Acquire Currency VII");
											if(bank_net >= 1000000){
												AchievmentMechanics.addAchievment(p.getName(), "Acquire Currency IX");
											}
										}
									}
								}
							}
						}
					}
				}

			}
			
			p.playSound(p.getLocation(), Sound.CHEST_CLOSE, 1F, 1F);
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true) // false?
	public void onPlayerPickupItemEvent(PlayerPickupItemEvent e){
		Player p = e.getPlayer();

		if(isGemPouch(e.getItem().getItemStack())){
			if(p.getInventory().firstEmpty() == -1){
				e.setCancelled(true);
				return;
			}
		}

		if(e.getItem().getItemStack().getType() == Material.PAPER){
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
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onGemPouchStack(InventoryClickEvent e){
		Player pl = (Player)e.getWhoClicked();
		if((e.isShiftClick() && e.getCurrentItem() != null) || (e.getCursor() != null && e.getCurrentItem() != null)){
			ItemStack scroll = e.getCurrentItem();
			if(e.getCursor() != null){
				ItemStack cursor = e.getCursor();

				if(isGemPouch(cursor) && e.getClick() == ClickType.DOUBLE_CLICK){
					e.setCancelled(true);
					e.setResult(Result.DENY);
					pl.updateInventory();
					return;
				}
			}
			if(!isGemPouch(scroll)){
				return;
			}
			if(!(e.isShiftClick())){
				if(isGemPouch(e.getCursor())){
					// Both the cursor and current item are empty maps.
					e.setCancelled(true);
					ItemStack on_cur = e.getCursor();
					e.setCursor(scroll);
					e.setCurrentItem(on_cur);
					pl.updateInventory();
					return;
				}
			}
			if(e.isShiftClick()){
				// Shift clicking a scroll.
				if(e.getInventory().getName().contains("@") || e.getInventory().getName().contains("Collection Bin") || e.getInventory().getName().contains(pl.getName()) || e.getInventory().getName().contains("Merchant")){
					e.setCancelled(false);
					return;
				} // Shop handling.
				e.setCancelled(true);
				if(e.getInventory().firstEmpty() == -1){
					pl.updateInventory();
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
					pl.getInventory().setItem(pl.getInventory().firstEmpty(), scroll);
				}
				pl.updateInventory();
				return;
			}
		}
	}

	/*@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onGemPouchClickEvent(InventoryClickEvent e){
		Player pl = (Player)e.getWhoClicked();
		if(e.getCurrentItem() != null || e.getCursor() != null){
			ItemStack current = null;
			ItemStack cursor = null;
			if(e.getCurrentItem() != null){
				current = e.getCurrentItem();
				if(isGemPouch(current)){
					if(current.getAmount() > 1){
						// TODO: Unstack
						unstackAllPouches(pl);
						return; // Once is enough.
					}
				}
			}
			if(e.getCursor() != null){
				cursor = e.getCursor();
				if(isGemPouch(cursor)){
					if(cursor.getAmount() > 1){
						// TODO: Unstack
						unstackAllPouches(pl);
						return;
					}
				}
			}
		}
	}*/

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onGemPouchWithdraw(InventoryClickEvent e){
		Player pl = (Player)e.getWhoClicked();
		if(e.isLeftClick() || e.isShiftClick() || (e.getCursor() != null && e.getCursor().getType() != Material.AIR) || e.getCurrentItem() == null || !(isGemPouch(e.getCurrentItem()))){
			return; // We don't care. Right click only.
		}

		if(!pl.getOpenInventory().getTopInventory().getName().equalsIgnoreCase("container.crafting")){
			return;
		}

		ItemStack pouch = e.getCurrentItem();

		if(pouch.getAmount() > 1){
			return;
		}

		int current_value = getGemPouchWorth(pouch);
		if(current_value > 64){
			// Withdraw only 64.
			e.setCancelled(true);
			setPouchWorth(pouch, (current_value - 64));
			e.setCursor(makeGems(64));
			//e.setCurrentItem(pouch);
			pl.updateInventory();
			pl.playSound(pl.getLocation(), Sound.ORB_PICKUP, 1F, 0.75F);
		}
		else if(current_value <= 64){
			e.setCancelled(true);
			setPouchWorth(pouch, 0);
			e.setCursor(makeGems(current_value));
			//e.setCurrentItem(pouch);
			pl.updateInventory();
			pl.playSound(pl.getLocation(), Sound.ORB_PICKUP, 1F, 0.75F);
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onGemPouchDeposit(InventoryClickEvent e){
		Player pl = (Player)e.getWhoClicked();
		if(e.isRightClick() || e.getCursor() == null || e.getCurrentItem() == null || e.getCursor().getType() != Material.EMERALD || !(isGemPouch(e.getCurrentItem()))){
			return; // We don't care.
		}

		if(!pl.getOpenInventory().getTopInventory().getName().equalsIgnoreCase("container.crafting")){
			return;
		}

		ItemStack cursor = e.getCursor();
		ItemStack pouch = e.getCurrentItem();

		int cursor_worth = cursor.getAmount(); // Amount of GEM(s)...
		int max_capacity = getMaxPouchCapacity(pouch);
		int current_value = getGemPouchWorth(pouch);

		if(pouch.getAmount() > 1){
			return;
		}

		if(max_capacity == current_value){
			return; // We ain't gonna do anything.
		}

		if((cursor_worth + current_value) > max_capacity){
			// We can't fit all the gems!
			e.setCancelled(true);
			int to_deposit = (max_capacity - current_value);
			cursor.setAmount(cursor_worth - to_deposit);
			e.setCursor(cursor);
			setPouchWorth(pouch, (max_capacity));
			pl.updateInventory();
			pl.playSound(pl.getLocation(), Sound.ORB_PICKUP, 1F, 1F);
		}
		else if((cursor_worth + current_value) <= max_capacity){
			e.setCancelled(true);
			e.setCursor(new ItemStack(Material.AIR));
			setPouchWorth(pouch, (cursor_worth + current_value));
			pl.updateInventory();
			pl.playSound(pl.getLocation(), Sound.ORB_PICKUP, 1F, 1F);
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onPlayerClickBank(InventoryClickEvent e){
		final Player p = (Player)e.getWhoClicked();
		
		if(e.getInventory().getName().startsWith("Bank Chest")){
			Inventory inv = e.getInventory();
			ItemStack clicked = e.getCurrentItem();

			if(isNextArrow(clicked)){
				e.setCancelled(true);
				
				String title = inv.getTitle();
				final int current_page = Integer.parseInt(title.substring(title.lastIndexOf("(") + 1, title.lastIndexOf("/")));

				Main.plugin.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
					public void run() {
						p.closeInventory();
					}
				}, 1L);

				Main.plugin.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
					public void run() {
						p.openInventory(bank_contents.get(p.getName()).get(current_page)); // Don't need to subtract, NEXT page.
						p.playSound(p.getLocation(), Sound.BAT_TAKEOFF, 1F, 1.2F);
					}
				}, 2L);
				return;
			}

			if(isPreviousArrow(clicked)){
				e.setCancelled(true);
				
				String title = inv.getTitle();
				final int current_page = Integer.parseInt(title.substring(title.lastIndexOf("(") + 1, title.lastIndexOf("/")));
				
				Main.plugin.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
					public void run() {
						p.closeInventory();
					}
				}, 1L);

				Main.plugin.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
					public void run() {
						p.openInventory(bank_contents.get(p.getName()).get(current_page - 2)); // Subtract 1 for index, 1 for previous page. Page 2 - > Page 0 .get(0) (which is actually page 1)
						p.playSound(p.getLocation(), Sound.BAT_TAKEOFF, 1F, 1.2F);
					}
				}, 2L);
				return;
			}
			if(inv.getSize() >= 63){
				if(e.getRawSlot() >= 54 && e.getRawSlot() <= 62){
					e.setCancelled(true);
					// They're clicking either the piston heads or previous/next button.
					if(clicked.getType() == Material.PISTON_MOVING_PIECE){
						p.updateInventory();
						return; // Do nothing else, they can't move these.
					}

					// If the code reaches this point, they have a bugged bottom row.
					// TODO: Fix it for them?
				}
			}
			
			
			if(isStaticGem(e.getCurrentItem())){
				e.setCancelled(true); // They're clicking on the money stack.

				final int cur_val = bank_map.get(p.getName());
				if(e.isShiftClick()){
					return;
				}

				if(e.getClick() == ClickType.MIDDLE){ //TODO: Is this middle click?
					if(!(bank_level.containsKey(p.getName()))){
						log.info("[MoneyMechanics] Could not get cached bank level value for: " + p.getName());
						// TODO: Why does this every get thrown? ^
					}
									
					final int bank_tier = MoneyMechanics.bank_level.get(p.getName());
					final int next_bank_tier = bank_tier + 1;
					if(next_bank_tier >= 18){
						p.sendMessage(ChatColor.RED + "Your bank is already at it's maximum size. (162 slots)");
						return;
					}
					final int upgrade_cost = getBankUpgradeCost(next_bank_tier);
					generateUpgradeAuthenticationCode(p, String.valueOf(next_bank_tier));

					Main.plugin.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
						public void run() {
							p.updateInventory();
							p.closeInventory();
							p.playSound(p.getLocation(), Sound.CHEST_CLOSE, 1F, 1F);
							p.sendMessage("");
							p.sendMessage(ChatColor.DARK_GRAY + "           *** " + ChatColor.GREEN + ChatColor.BOLD + "Bank Upgrade Confirmation" + ChatColor.DARK_GRAY + " ***");
							p.sendMessage(ChatColor.DARK_GRAY + "           CURRENT Slots: " + ChatColor.GREEN + getBankSlots(bank_tier) + ChatColor.DARK_GRAY + "          NEW Slots: " + ChatColor.GREEN + getBankSlots(next_bank_tier));
							//p.sendMessage(ChatColor.DARK_GRAY + "FROM Tier " + ChatColor.GREEN + bank_tier + ChatColor.DARK_GRAY + " TO " + ChatColor.GREEN + next_bank_tier);
							p.sendMessage(ChatColor.DARK_GRAY + "                  Upgrade Cost: " + ChatColor.GREEN + "" + upgrade_cost + " Gem(s)");
							p.sendMessage("");
							p.sendMessage(ChatColor.GREEN + "Enter the code '" + ChatColor.BOLD + getUpgradeAuthenticationCode(p) + ChatColor.GREEN + "' to confirm your upgrade.");
							p.sendMessage("");
							p.sendMessage("" + ChatColor.RED + ChatColor.BOLD + "WARNING:" + ChatColor.RED + " Bank upgrades are " + ChatColor.BOLD + ChatColor.RED + "NOT" + ChatColor.RED + " reversible or refundable. Type 'cancel' to void this upgrade request.");
							p.sendMessage("");
						}
					}, 2L);
					
					return;
				}
				
				if(e.isLeftClick()){ // Withdraw raw gems.
					if(e.getCursor() != null && e.getCursor().getType() != Material.AIR){
						// They have something in their hand, not an emerald. Idiots.
						return;
					}

					if(cur_val <= 0){
						p.sendMessage(ChatColor.RED + "You currently have no GEM(s) stored in your bank.");
						return;
					}

					withdraw_map.put(p, cur_val);
					withdraw_type.put(p, "raw");

					Main.plugin.getServer().getScheduler().scheduleAsyncDelayedTask(Main.plugin, new Runnable() {
						public void run() {
							p.updateInventory();
							p.closeInventory();
							p.playSound(p.getLocation(), Sound.CHEST_CLOSE, 1F, 1F);
							p.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "Current Balance: " + ChatColor.GREEN + cur_val + " GEM(s)");
							p.sendMessage(ChatColor.GRAY + "Banker: " + ChatColor.WHITE + "How much would you like to WITHDRAW today, " + p.getDisplayName() + "?");
							p.sendMessage(ChatColor.GRAY + "Please enter the amount you'd like To WITHDRAW. Alternatively, type " + ChatColor.RED + "'cancel'" + ChatColor.GRAY + " to void this operation.");
						}
					}, 2L);


					return;
				}
				if(e.isRightClick()){ // Sign a bank note.
					withdraw_map.put(p, cur_val);
					withdraw_type.put(p, "note");

					Main.plugin.getServer().getScheduler().scheduleAsyncDelayedTask(Main.plugin, new Runnable() {
						public void run() {
							p.updateInventory();
							p.closeInventory();
							p.playSound(p.getLocation(), Sound.CHEST_CLOSE, 1F, 1F);
							p.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "Current Balance: " + ChatColor.GREEN + cur_val + " GEM(s)");
							p.sendMessage(ChatColor.GRAY + "Banker: " + ChatColor.WHITE + "How much would you like to CONVERT today, " + p.getDisplayName() + "?");
							p.sendMessage(ChatColor.GRAY + "Please enter the amount you'd like To CONVERT into a gem note. Alternatively, type " + ChatColor.RED + "'cancel'" + ChatColor.GRAY + " to void this operation.");
						}
					}, 2L);

					return;
				}
				return;
			}	  

			if(isItemUnbankable(e.getCurrentItem()) || isItemUnbankable(e.getCursor())){
				e.setCancelled(true);
				e.setResult(Result.DENY);
				p.updateInventory();
				p.sendMessage(ChatColor.RED + "You " + ChatColor.UNDERLINE + "cannot" + ChatColor.RED + " bank this item, as it is part of your spawn kit.");
			}
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onPlayerClickInventory(InventoryClickEvent event){
		if(!(event.getWhoClicked() instanceof Player)){
			return;
		}
		
		final Player p = (Player)event.getWhoClicked();

		if(event.getCurrentItem() != null && event.getCurrentItem().getType() == Material.PISTON_MOVING_PIECE){
			event.setCancelled(true);
			p.updateInventory();
		}
		
		if(event.getCursor().getType() == Material.PAPER && event.getClick() == ClickType.DOUBLE_CLICK){
			event.setCancelled(true);
			event.setResult(Result.DENY);
			p.updateInventory();
			return;
		}

		if(event.getCursor() == null && event.getCurrentItem().getType() == Material.PAPER && event.isShiftClick()){
			event.setCancelled(true);
			return; //TODO: Formal fix, make it not stack on shift click.
		}

		if(event.getCursor() != null && event.getCurrentItem() != null && event.getCursor().getType() == Material.PAPER && event.getCurrentItem().getType() == Material.PAPER && !TradeMechanics.trade_map.containsKey(p)){
			if(split_map.containsKey(p)){
				p.sendMessage(ChatColor.RED + "You cannot combine bank notes while you are already splitting one. Type 'cancel' and then try again.");
				event.setCancelled(true);
				return;
			}

			if(event.getCursor().getAmount() > 1 || event.getCurrentItem().getAmount() > 1){
				event.setCancelled(true);
				return;
			}

			ItemStack map_cursor = event.getCursor();
			ItemStack map_current = event.getCurrentItem();
			event.setCancelled(true);
			int map1_val = getBankNoteValue(map_cursor);
			int map2_val = getBankNoteValue(map_current);
			int new_val = map1_val + map2_val;
			if(new_val > 999999){
				p.sendMessage(ChatColor.RED + "You cannot sign more than 999,999 Gems to a single bank note.");
				return;
			}
			//event.setCancelled(true);
			event.setCursor(new ItemStack(Material.AIR, 1));
			event.setCurrentItem(signBankNote(event.getCurrentItem(), ChatColor.GREEN.toString() + "Bank Note", ChatColor.WHITE.toString() + ChatColor.BOLD.toString() +  "Value:" + ChatColor.WHITE.toString() + " " + new_val + " Gems" + "," + ChatColor.GRAY.toString() + "Exchange at any bank for GEM(s)"));
			//p.getInventory().getItem(event.getSlot()).setAmount(1);
			p.updateInventory();
			p.sendMessage(ChatColor.GRAY + "You've combined bank notes " + ChatColor.ITALIC + map1_val + "G + " + map2_val + "G" + ChatColor.GRAY + " into one bank note with the value of " + ChatColor.BOLD + new_val + "G.");
			p.playSound(p.getLocation(), Sound.ENDERDRAGON_WINGS, 1F, 1.2F);
			return;
		}

		if(event.isCancelled()){
			return;
		}

		if(event.getInventory().getName().startsWith("Bank Chest") &&
				(((event.getCursor() != null && event.getCursor().getType() == Material.EMERALD) || (event.getCursor() != null && event.getCursor().getType() == Material.PAPER) || (event.getCursor() != null && isGemPouch(event.getCursor())))
						|| (event.isShiftClick() && ((event.getCurrentItem() != null && event.getCurrentItem().getType() == Material.EMERALD) 
								|| (event.getCurrentItem() != null && event.getCurrentItem().getType() == Material.PAPER) || (event.getCurrentItem() != null && isGemPouch(event.getCurrentItem())))))){
			//((((event.getCursor() != null && event.getCursor().getType() == Material.EMERALD) || (event.getCurrentItem() != null && event.getCurrentItem().getType() == Material.EMERALD)))
			//|| (((event.getCursor() != null && event.getCursor().getType() == Material.PAPER) || (event.getCurrentItem() != null && event.getCurrentItem().getType() == Material.PAPER))))){

			if(event.getSlotType() == SlotType.OUTSIDE || event.getSlotType() == SlotType.ARMOR || event.getSlotType() == SlotType.FUEL || event.getSlotType() == SlotType.RESULT){return;}  
			ItemStack to_deposit = null;

			if(event.isShiftClick() && event.getRawSlot() == (event.getInventory().getSize() - 1)){
				event.setCancelled(true);
				return;
			}

			if(event.isShiftClick()){
				int bank_slots = event.getInventory().getSize(); //getBankSlots(MoneyMechanics.bank_level.get(p.getName()));
				int slot = event.getRawSlot();

				if(bank_slots > (slot + 1)){ // It's the bank chest's inventory lol
					return;
				}

				to_deposit = event.getCurrentItem();
				if(isGemPouch(to_deposit)){
					if(getGemPouchWorth(to_deposit) != 0){
						// Do nothing, we'll set value to 0 shortly.
						event.setCancelled(true);
					}
				}
				else{
					event.setCurrentItem(new ItemStack(Material.AIR));
					event.setCancelled(true);
				}
				p.updateInventory();
			} else if(!(event.isShiftClick())){
				int bank_slots = getBankSlots(MoneyMechanics.bank_level.get(p.getName()));
				int slot = event.getRawSlot();

				if(bank_slots < (slot + 1)){ // Not the bank's inventory lol.
					return;
				}

				to_deposit = event.getCursor();
				if(isGemPouch(to_deposit)){
					if(getGemPouchWorth(to_deposit) != 0){
						// Do nothing, we'll set value to 0 shortly.
						event.setCancelled(true);
					}
				}
				else{
					event.setCursor(new ItemStack(Material.AIR));
					event.setCancelled(true);
				}
				//p.updateInventory();
				//log.info("4IS THE EVENT CANCELLED? : " + event.isCancelled());
			}

			int amount_to_deposit = 0;

			if(to_deposit.getType() == Material.EMERALD){
				amount_to_deposit = to_deposit.getAmount();
			}
			else if(to_deposit.getType() == Material.PAPER){
				amount_to_deposit = getBankNoteValue(to_deposit) * to_deposit.getAmount();
			}
			else if(isGemPouch(to_deposit)){
				if(to_deposit.getAmount() != 1){
					event.setCancelled(true);
					return; // Don't deposit if stacked.
				}
				if(getGemPouchWorth(to_deposit) != 0){
					amount_to_deposit = getGemPouchWorth(to_deposit);
					setPouchWorth(to_deposit, 0);
					if(!(event.isShiftClick())){
						p.getInventory().addItem(to_deposit);
						event.setCursor(new ItemStack(Material.AIR));
						p.updateInventory();
					}
				}
			}

			int cur_val = bank_map.get(p.getName());

			if(amount_to_deposit > 0){
				bank_map.put(p.getName(), amount_to_deposit + cur_val);
				p.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "+" + ChatColor.GREEN + amount_to_deposit + ChatColor.BOLD + "G" + ChatColor.GREEN + ", " + ChatColor.BOLD + "New Balance: " + ChatColor.GREEN + (amount_to_deposit + cur_val) + " GEM(s)");
				//p.sendMessage(ChatColor.GREEN + "Deposited " + amount_to_deposit + " GEM(s) into your bank chest. Your NEW balance is " + ChatColor.BOLD + (amount_to_deposit + cur_val) + " GEM(s)");
				//p.sendMessage(ChatColor.GRAY + "To DEPOSIT gems, " + ChatColor.UNDERLINE + "RIGHT CLICK" + ChatColor.GRAY + " the cauldron with gems in your character's hand. To WITHDRAW gems, "
				//	+ ChatColor.UNDERLINE + "LEFT CLICK" + ChatColor.GRAY + " the cauldron.");
				updateStaticCashStack(p); 
				p.playSound(p.getLocation(), Sound.ORB_PICKUP, 1F, 1F);
				p.updateInventory(); 
				return;
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onPlayerPickupItem(PlayerPickupItemEvent event){
		final Player p = event.getPlayer();
		ItemStack i = event.getItem().getItemStack();
		final int amount = i.getAmount();
		final Item it = event.getItem();
		int gems_left_to_give = amount;
		boolean pouch_used = false;

		if(i.getType() == Material.EMERALD){
			if (it.hasMetadata("nopickup") && it.getMetadata("nopickup").get(0).asBoolean() == true){
				event.setCancelled(true);
				it.remove();
				return;
			}

			for(Entry<Integer, ? extends ItemStack> data : p.getInventory().all(Material.INK_SACK).entrySet()){
				ItemStack is = data.getValue();
				int index = data.getKey();

				if(gems_left_to_give <= 0){
					break;
				}

				if(is.getAmount() > 1){
					continue; // Do not put gems into stacked items.
				}

				if(isGemPouch(is)){
					// Put in gem pouches
					int max = getMaxPouchCapacity(is);
					int current = getGemPouchWorth(is);

					if(current < max){
						pouch_used = true;
						// Not full.
						if((current + gems_left_to_give) <= max){
							// Add it all to pouch.
							setPouchWorth(is, current+gems_left_to_give);
							gems_left_to_give = 0;
							break;
						}
						else{
							// Add as much as we can.
							int what_we_can_hold = max - current;

							setPouchWorth(is, max);
							gems_left_to_give = gems_left_to_give - what_we_can_hold;
						}
					}
				}
			}

			if(gems_left_to_give <= 0){
				event.setCancelled(true);
				event.getItem().remove(); // No more drop needed.
			}
			else if(gems_left_to_give > 0 && gems_left_to_give != amount && pouch_used){
				Location loc = event.getItem().getLocation();
				event.getItem().remove();
				event.setCancelled(true);
				event.getPlayer().getWorld().dropItemNaturally(loc, MoneyMechanics.makeGems(gems_left_to_give));
				return;
			}

			Main.plugin.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
				public void run() {
					if((it == null || it.isDead())){
						p.playSound(p.getLocation(), Sound.ORB_PICKUP, 1F, 1F);
						if(CommunityMechanics.toggle_list.containsKey(p.getName()) && CommunityMechanics.toggle_list.get(p.getName()).contains("debug")){
							p.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "                                 +" + ChatColor.GREEN + (int)(amount) + ChatColor.BOLD + "G" + ChatColor.GREEN);
						}
					}
				}
			}, 2L);
		}
	}


	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
		Player p = (Player)sender;
		if(cmd.getName().equalsIgnoreCase("mnote")){

			if(!(p.getName().equalsIgnoreCase("Vaquxine")) && !(p.getName().equalsIgnoreCase("Availer"))){
				return true;
			}

			if(args.length == 0){
				p.sendMessage(ChatColor.YELLOW + "USAGE: /mnote create <value (in Gems)>");
				return true;
			}
			if(args[0].equalsIgnoreCase("pouch")){
				p.getInventory().addItem(CraftItemStack.asCraftCopy(t1_gem_pouch));
				p.getInventory().addItem(CraftItemStack.asCraftCopy(t2_gem_pouch));
				p.getInventory().addItem(CraftItemStack.asCraftCopy(t3_gem_pouch));
				p.getInventory().addItem(CraftItemStack.asCraftCopy(t4_gem_pouch));
				//p.getInventory().addItem(CraftItemStack.asCraftCopy(t1_gem_pouch));
			}
			if(args[0].equalsIgnoreCase("upload")){
				uploadBankDatabaseData(p.getName(), false);
				p.sendMessage("All GEMS data uploaded to SQL.");
				return true;
			}
			if(args[0].equalsIgnoreCase("download")){
				downloadBankDatabaseData(p.getName());
				p.sendMessage("All GEMS data downloaded and loaded into memory.");
				return true;
			}
			if(args[0].equalsIgnoreCase("create")){
				if(!(args.length == 2)){
					p.sendMessage(ChatColor.YELLOW + "USAGE: /mnote create <value (in Gems)>");
					return true;
				}
				try{
					Integer.parseInt(args[1]);}catch(NumberFormatException e){
						p.sendMessage(ChatColor.RED + "Invalid Gems amount specified. Must be a whole number.");
						return true;
					}
				int amount = Integer.parseInt(args[1]);
				if(amount <= 0){
					p.sendMessage(ChatColor.RED + "Please specify a positive integer above 0.");
					return true;
				}
				if(amount > 999999){
					p.sendMessage(ChatColor.RED + "You cannot store more than 999,999 Gems in one bank note.");
					return true;
				}

				if(!Hive.isHiveOnline()){
					p.sendMessage(ChatColor.RED + "This server is currently desynced from the HIVE, this action cannot be completed at this time.");
					return true;
				}

				addMoneyCert(p, amount, true);
			}
			else{
				p.sendMessage(ChatColor.YELLOW + "USAGE: /mnote create <value (in Gems)>");
			}
		}

		return true;

	}

}
