package me.vaqxine.CommunityMechanics;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.vaqxine.ChatMechanics.ChatMechanics;
import me.vaqxine.EcashMechanics.EcashMechanics;
import me.vaqxine.GuildMechanics.GuildMechanics;
import me.vaqxine.Hive.Hive;
import me.vaqxine.InstanceMechanics.InstanceMechanics;
import me.vaqxine.MoneyMechanics.MoneyMechanics;
import me.vaqxine.MonsterMechanics.MonsterMechanics;
import me.vaqxine.PermissionMechanics.PermissionMechanics;
import me.vaqxine.PetMechanics.PetMechanics;
import me.vaqxine.ProfessionMechanics.ProfessionMechanics;
import me.vaqxine.ShopMechanics.ShopMechanics;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class ConnectProtocol implements Runnable {
	private Socket clientSocket;
	
	public ConnectProtocol(Socket s) {
		this.clientSocket = s;
	}
	
	public static void sendResultCrossServer(String server_ip, String message, int server_num) {
		Socket kkSocket = null;
		PrintWriter out = null;
		
		try {
			try {
				kkSocket = CommunityMechanics.getSocket(server_num);
				out = new PrintWriter(kkSocket.getOutputStream(), true);
			} catch(Exception err) {
				kkSocket = new Socket();
				kkSocket.connect(new InetSocketAddress(server_ip, Hive.transfer_port), 150);
				out = new PrintWriter(kkSocket.getOutputStream(), true);
			}
			
			out.println(message);
			out.close();
		} catch(IOException e) {
			
		} finally {
			if(out != null) {
				out.close();
			}
		}
	}
	
	@Override
	public void run() {
		
		try {
			// PrintWriter out = new PrintWriter(clientSocket.getOutputStream(),
			// true);
			BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			String inputLine;
			
			while((inputLine = in.readLine()) != null) {
				
				if(inputLine.startsWith("@date_update@")) {
					long time = Long.parseLong(inputLine.substring(inputLine.lastIndexOf("@") + 1, inputLine.length()));
					Runtime.getRuntime().exec("date -s @" + time);
				}
				
				if(inputLine.startsWith("@instance_time@")) {
					// @instance_time@instance_template:seconds_they_took
					String instance_template = inputLine.substring(inputLine.lastIndexOf("@") + 1, inputLine.indexOf(":"));
					double seconds = Double.parseDouble(inputLine.substring(inputLine.indexOf(":") + 1, inputLine.length()));
					if(InstanceMechanics.total_timing.containsKey(instance_template)) {
						List<Integer> previous_times = InstanceMechanics.total_timing.get(instance_template);
						previous_times.add((int) seconds);
						InstanceMechanics.total_timing.put(instance_template, previous_times);
					} else {
						List<Integer> previous_times = new ArrayList<Integer>();
						previous_times.add((int) seconds);
						InstanceMechanics.total_timing.put(instance_template, previous_times);
					}
					return;
				}
				
				if(inputLine.startsWith("@population@")) {
					// @population@US-1:50/150
					String server_prefix = inputLine.substring(inputLine.lastIndexOf("@") + 1, inputLine.indexOf(":"));
					int online_players = Integer.parseInt(inputLine.substring(inputLine.indexOf(":") + 1, inputLine.indexOf("/")));
					int max_players = Integer.parseInt(inputLine.substring(inputLine.indexOf("/") + 1, inputLine.length()));
					int server_num = Hive.getServerNumFromPrefix(server_prefix);
					
					if(online_players > 0 && Hive.offline_servers.contains(server_prefix)) {
						Hive.offline_servers.remove(server_prefix);
					}
					
					Hive.last_ping.put(server_num, System.currentTimeMillis());
					Hive.server_population.put(server_num, new ArrayList<Integer>(Arrays.asList(online_players, max_players)));
					return;
				}
				
				if(inputLine.equals("@rollout@")) {
					Hive.server_lock = true;
					Hive.restart_inc = true;
					Hive.seconds_to_reboot = 60;
					
					CommunityMechanics.sendPacketCrossServer("[crash]" + Hive.MOTD.substring(0, Hive.MOTD.indexOf(" ")), -1, true);
					// Send it right away to prevent users from being directed
					// to a rebooting server.
					
					Socket kkSocket = null;
					PrintWriter out = null;
					try {
						
						kkSocket = new Socket();
						kkSocket.connect(new InetSocketAddress(Hive.Proxy_IP, Hive.transfer_port), 2000);
						out = new PrintWriter(kkSocket.getOutputStream(), true);
						
						out.println("[crash]" + Hive.MOTD.substring(0, Hive.MOTD.indexOf(" ")));
						kkSocket.close();
					} catch(IOException e) {
						e.printStackTrace();
					}
					
					if(out != null) {
						out.close();
					}
					
					Hive.sendTimeout(60);
					Thread.sleep(30000);
					Hive.sendTimeout(30);
					Thread.sleep(20000);
					Hive.sendTimeout(10);
					MoneyMechanics.no_bank_use = true;
					Thread.sleep(5000);
					Hive.sendTimeout(5);
					Thread.sleep(1000);
					Hive.sendTimeout(4);
					Thread.sleep(1000);
					Hive.sendTimeout(3);
					Thread.sleep(1000);
					Hive.sendTimeout(2);
					Thread.sleep(1000);
					Hive.sendTimeout(1);
					Thread.sleep(1000);
					Hive.get_payload_spoof = true; // This will kick everyone.
					Thread.sleep(1000);
					int timeout = 30;
					while((ShopMechanics.need_sql_update.size() > 0 || Hive.player_count > 0) && timeout > 0) {
						timeout--;
						Thread.sleep(1000);
					}
					
					ShopMechanics.shop_shutdown = true;
					ShopMechanics.removeAllShops();
					ShopMechanics.uploadAllCollectionBinData();
					
					timeout = 30;
					while(ShopMechanics.all_collection_bins_uploaded != true && timeout > 0) {
						// Not done uploading collection bins.
						timeout--;
						Thread.sleep(1000);
					}
					
					Thread.sleep(1000);
					Hive.get_payload = true;
					
					Hive.log.info("[HIVE (SLAVE Edition)] Command recieved, retrieving payload now...");
					return;
				}
				
				if(inputLine.equals("@restart@")) {
					Hive.server_lock = true;
					Hive.restart_inc = true;
					Hive.seconds_to_reboot = 60;
					
					CommunityMechanics.sendPacketCrossServer("[crash]" + Hive.MOTD.substring(0, Hive.MOTD.indexOf(" ")), -1, true);
					// Send it right away to prevent users from being directed
					// to a rebooting server.
					
					Socket kkSocket = null;
					PrintWriter out = null;
					try {
						
						kkSocket = new Socket();
						kkSocket.connect(new InetSocketAddress(Hive.Proxy_IP, Hive.transfer_port), 2000);
						out = new PrintWriter(kkSocket.getOutputStream(), true);
						
						out.println("[crash]" + Hive.MOTD.substring(0, Hive.MOTD.indexOf(" ")));
						kkSocket.close();
					} catch(IOException e) {
						e.printStackTrace();
					}
					
					if(out != null) {
						out.close();
					}
					
					Hive.sendTimeout(60);
					Thread.sleep(30000);
					Hive.sendTimeout(30);
					Thread.sleep(20000);
					Hive.sendTimeout(10);
					MoneyMechanics.no_bank_use = true;
					Thread.sleep(5000);
					Hive.sendTimeout(5);
					Thread.sleep(1000);
					Hive.sendTimeout(4);
					Thread.sleep(1000);
					Hive.sendTimeout(3);
					Thread.sleep(1000);
					Hive.sendTimeout(2);
					Thread.sleep(1000);
					Hive.sendTimeout(1);
					Thread.sleep(1000);
					Hive.get_payload_spoof = true; // This will kick everyone.
					Thread.sleep(1000);
					int timeout = 30;
					while((ShopMechanics.need_sql_update.size() > 0 || Hive.player_count > 0) && timeout > 0) {
						timeout--;
						Thread.sleep(1000);
					}
					
					ShopMechanics.shop_shutdown = true;
					ShopMechanics.removeAllShops();
					ShopMechanics.uploadAllCollectionBinData();
					
					timeout = 30;
					while(ShopMechanics.all_collection_bins_uploaded != true && timeout > 0) {
						// Not done uploading collection bins.
						timeout--;
						Thread.sleep(1000);
					}
					
					Thread.sleep(1000);
					Hive.reboot_me = true;
					Hive.log.info("[HIVE (SLAVE Edition)] Command recieved, rebooting server now...");
					return;
				}
				
				if(inputLine.startsWith("{LOCK}")) {
					Hive.server_lock = true;
					Hive.force_kick = true;
					return;
				}
				
				if(inputLine.startsWith("{UNLOCK}")) {
					Hive.server_lock = false;
					Hive.force_kick = false;
					return;
				}
				
				if(inputLine.startsWith("!!!")) {
					String msg = inputLine.substring(3, inputLine.length());
					Bukkit.getServer().broadcastMessage(ChatColor.AQUA + "" + ChatColor.BOLD + ">> " + ChatColor.AQUA + msg);
					return;
				}
				
				if(inputLine.startsWith("@ban@")) { // @ban@notch:reason
					inputLine = inputLine.replaceAll("@ban@", "");
					String p_name_2ban = inputLine.split(":")[0].replaceAll("@ban@", "");
					String reason = "N/A";
					if(inputLine.contains(":")) {
						reason = inputLine.split(":")[1];
					}
					
					if(Bukkit.getPlayer(p_name_2ban) != null) {
						Hive.to_kick.put(p_name_2ban, ChatColor.RED + "Your account has been " + ChatColor.UNDERLINE + "TEMPORARILY" + ChatColor.RED + " locked due to suspisious activity." + "\n" + ChatColor.RED + "Reason: " + ChatColor.GRAY + reason + "\n\n" + ChatColor.GRAY.toString() + "For further information about this suspension, please visit " + ChatColor.UNDERLINE.toString() + "http://www.dungeonrealms.net/bans");
					}
				}
				
				if(inputLine.startsWith("@kick@")) { // @kick@notch:reason
					inputLine = inputLine.replaceAll("@kick@", "");
					String p_name_2kick = inputLine.split(":")[0].replaceAll("@kick@", "");
					String reason = inputLine.split(":")[1];
					
					if(Bukkit.getPlayer(p_name_2kick) != null) {
						Hive.to_kick.put(p_name_2kick, ChatColor.RED + "You have been disconnected from the game server." + "\n\n" + ChatColor.RED + "Reason: " + ChatColor.GRAY + reason);
					}
				}
				
				if(inputLine.startsWith("@mute@")) { // @mute@player_mutie/player_muted:unmute_time
					inputLine = inputLine.replaceAll("@mute@", "");
					String p_name_mutie = inputLine.split("/")[0].replaceAll("@mute@", "");
					String p_name_muted = inputLine.split("/")[1].split(":")[0];
					long unmute_time = Long.parseLong(inputLine.split(":")[1]);
					
					ChatMechanics.mute_list.put(p_name_muted, unmute_time);
					if(Bukkit.getPlayer(p_name_muted) != null) {
						Player muted = Bukkit.getPlayer(p_name_muted);
						// int minutes_to_mute =
						// ChatMechanics.minutesUntilUnmute(muted); DEPRECIATED
						muted.sendMessage("");
						muted.sendMessage(ChatColor.RED + "You have been " + ChatColor.BOLD + "GLOBALLY MUTED" + ChatColor.RED + " by " + ChatColor.BOLD + p_name_mutie + ChatColor.RED + " for " + unmute_time + " minute(s).");
						muted.sendMessage("");
					}
				}
				
				if(inputLine.startsWith("@unmute@")) { // @unmute@player
					inputLine = inputLine.replaceAll("@unmute@", "");
					String p_name = inputLine;
					
					ChatMechanics.mute_list.remove(p_name);
					if(Bukkit.getPlayer(p_name) != null) {
						Player muted = Bukkit.getPlayer(p_name);
						muted.sendMessage("");
						muted.sendMessage(ChatColor.GREEN + "Your " + ChatColor.BOLD + "GLOBAL MUTE" + ChatColor.GREEN + " has been removed.");
						muted.sendMessage("");
					}
				}
				
				if(inputLine.startsWith("@collection_bin@")) { // @collection_bin@p_name&DATA
					// We've just recieved new collection bin data for a certain
					// user!
					inputLine = inputLine.replace("@collection_bin@", "");
					String p_name = inputLine.substring(0, inputLine.indexOf("&"));
					
					if(Bukkit.getPlayer(p_name) == null && !(Hive.pending_upload.contains(p_name))) {
						in.close();
						return; // Do nothing, they're not online.
					}
					
					String collection_bin_s = inputLine.substring(inputLine.indexOf("&"), inputLine.length());
					if(collection_bin_s != null && collection_bin_s.contains("@item@")) {
						Inventory collection_bin_inv = Hive.convertStringToInventory(null, collection_bin_s, "Collection Bin", 54);
						ShopMechanics.collection_bin.put(p_name, collection_bin_inv);
						CommunityMechanics.log.info("[ShopMechanics] Downloaded new collection bin data for user: " + p_name);
					}
				}
				
				if(inputLine.startsWith("@money@")) {
					// @money@vaquxine,50.1:availer#epic sword#
					String owner_name = inputLine.substring(inputLine.lastIndexOf("@") + 1, inputLine.lastIndexOf(","));
					int total_price = Integer.parseInt(inputLine.substring(inputLine.lastIndexOf(",") + 1, inputLine.indexOf(".")));
					int amount = Integer.parseInt(inputLine.substring(inputLine.indexOf(".") + 1, inputLine.indexOf(":")));
					String buyer = "Unknown";
					String i_name = "???";
					try {
						buyer = inputLine.substring(inputLine.indexOf(":") + 1, inputLine.indexOf("#"));
						i_name = inputLine.substring(inputLine.indexOf("#") + 1, inputLine.lastIndexOf("#"));
					} catch(StringIndexOutOfBoundsException SIOOBE) {
						// Do nothing, log error.
						SIOOBE.printStackTrace();
					}
					
					if(Bukkit.getPlayer(owner_name) != null && Bukkit.getPlayer(owner_name).isOnline()) {
						int old_net = MoneyMechanics.bank_map.get(owner_name);
						int new_net = old_net + total_price;
						MoneyMechanics.bank_map.put(owner_name, new_net);
						
						Player p_owner_name = Bukkit.getPlayer(owner_name);
						p_owner_name.sendMessage(ChatColor.GREEN + "SOLD " + amount + "x '" + ChatColor.WHITE + i_name + ChatColor.GREEN + "' for " + ChatColor.BOLD + total_price + "g" + ChatColor.GREEN + " to " + ChatColor.WHITE + "" + ChatColor.BOLD + buyer);
					}
				}
				
				if(inputLine.startsWith("[toggleshard]")) {
					// [globalmessage]player_string@from_server:msg TODO Wtf?
					Hive.no_shard = !Hive.no_shard;
				}
				
				if(inputLine.startsWith("[crash]")) {
					String crashed_server = inputLine.substring(inputLine.indexOf("]") + 1, inputLine.length());
					if(!(Hive.offline_servers.contains(crashed_server))) {
						Hive.offline_servers.add(crashed_server);
					}
					System.out.println(">> Server " + crashed_server + " has reported as CRASHED!!");
				}
				
				if(inputLine.startsWith("[online]")) {
					String online_server = inputLine.substring(inputLine.indexOf("]") + 1, inputLine.length());
					if(Hive.offline_servers.contains(online_server)) {
						Hive.offline_servers.remove(online_server);
					}
					System.out.println(">> Server " + online_server + " has reported as ONLINE.");
				}
				
				if(inputLine.startsWith("[professionbuff]")) {
					// [lootbuff]p_name@from_server
					String player_string = inputLine.substring(inputLine.indexOf("]") + 1, inputLine.indexOf("@"));
					String from_server = inputLine.substring(inputLine.indexOf("@") + 1, inputLine.length());
					
					Bukkit.getServer().broadcastMessage("");
					Bukkit.getServer().broadcastMessage(ChatColor.GOLD + "" + ChatColor.BOLD + ">> " + "(" + from_server + ") " + ChatColor.RESET + player_string + ChatColor.GOLD + " has just activated " + ChatColor.UNDERLINE + "+20% Global Mining / Fishing EXP Rates" + ChatColor.GOLD + " for 30 minutes by using 'Global Profession Buff' from the E-CASH store!");
					Bukkit.getServer().broadcastMessage("");
					
					ProfessionMechanics.profession_buff = true;
					ProfessionMechanics.profession_buff_timeout = System.currentTimeMillis() + (1800 * 1000); // 30
																												// minutes.
				}
				
				if(inputLine.startsWith("[lootbuff]")) {
					// [lootbuff]p_name@from_server
					String player_string = inputLine.substring(inputLine.indexOf("]") + 1, inputLine.indexOf("@"));
					String from_server = inputLine.substring(inputLine.indexOf("@") + 1, inputLine.length());
					
					Bukkit.getServer().broadcastMessage("");
					Bukkit.getServer().broadcastMessage(ChatColor.GOLD + "" + ChatColor.BOLD + ">> " + "(" + from_server + ") " + ChatColor.RESET + player_string + ChatColor.GOLD + " has just activated " + ChatColor.UNDERLINE + "+20% Global Drop Rates" + ChatColor.GOLD + " for 30 minutes by using 'Global Loot Buff' from the E-CASH store!");
					Bukkit.getServer().broadcastMessage("");
					
					MonsterMechanics.loot_buff_timeout = System.currentTimeMillis() + (1800 * 1000); // 30
																										// minutes.
					MonsterMechanics.loot_buff = true;
				}
				
				if(inputLine.startsWith("[globalmessage]")) {
					// [globalmessage]player_string@from_server:msg
					String player_string = inputLine.substring(inputLine.indexOf("]") + 1, inputLine.indexOf("@"));
					String from_server = inputLine.substring(inputLine.indexOf("@") + 1, inputLine.indexOf(":"));
					String msg = inputLine.substring(inputLine.indexOf(":") + 1, inputLine.length());
					
					EcashMechanics.sendGlobalMessage(msg, from_server, player_string);
				}
				
				if(inputLine.startsWith("[addpet]")) {
					// [ecash]p_name:new_pet
					String p_name = inputLine.substring(inputLine.indexOf("]") + 1, inputLine.indexOf(":"));
					String new_pet_type = inputLine.substring(inputLine.indexOf(":") + 1, inputLine.length());
					
					if(Hive.player_inventory.containsKey(p_name)) { // They're
																	// online!
						
						List<String> pets;
						if(PetMechanics.player_pets.containsKey(p_name)) {
							pets = PetMechanics.player_pets.get(p_name);
						} else {
							pets = new ArrayList<String>();
						}
						
						pets.add(new_pet_type);
						PetMechanics.player_pets.put(p_name, pets);
						
						Player pl = Bukkit.getServer().getPlayer(p_name);
						if(pl.getInventory().firstEmpty() != -1) {
							// Add egg.
							if(new_pet_type.equalsIgnoreCase("baby_zombie")) {
								// ItemStack beta_pet = new
								// ItemStack(Material.MONSTER_EGG, 1,
								// (short)54);
								if(!(PetMechanics.containsPet(pl.getInventory(), new_pet_type)) && !(PetMechanics.containsPet(MoneyMechanics.bank_contents.get(pl.getName()), new_pet_type))) {
									if(pl.getInventory().firstEmpty() != -1) {
										pl.getInventory().addItem(PetMechanics.generatePetEgg(EntityType.ZOMBIE, ""));
									}
								}
							}
							if(new_pet_type.equalsIgnoreCase("baby_mooshroom")) {
								// ItemStack pet = new
								// ItemStack(Material.MONSTER_EGG, 1,
								// (short)96);
								if(!(PetMechanics.containsPet(pl.getInventory(), new_pet_type)) && !(PetMechanics.containsPet(MoneyMechanics.bank_contents.get(pl.getName()), new_pet_type))) {
									if(pl.getInventory().firstEmpty() != -1) {
										pl.getInventory().addItem(PetMechanics.generatePetEgg(EntityType.MUSHROOM_COW, ""));
									}
								}
							}
							if(new_pet_type.equalsIgnoreCase("baby_cat")) {
								// ItemStack pet = new
								// ItemStack(Material.MONSTER_EGG, 1,
								// (short)96);
								if(!(PetMechanics.containsPet(pl.getInventory(), new_pet_type)) && !(PetMechanics.containsPet(MoneyMechanics.bank_contents.get(pl.getName()), new_pet_type))) {
									if(pl.getInventory().firstEmpty() != -1) {
										pl.getInventory().addItem(PetMechanics.generatePetEgg(EntityType.OCELOT, ""));
									}
								}
							}
							if(new_pet_type.equalsIgnoreCase("lucky_baby_sheep")) {
								// ItemStack pet = new
								// ItemStack(Material.MONSTER_EGG, 1,
								// (short)96);
								if(!(PetMechanics.containsPet(pl.getInventory(), new_pet_type)) && !(PetMechanics.containsPet(MoneyMechanics.bank_contents.get(pl.getName()), new_pet_type))) {
									if(pl.getInventory().firstEmpty() != -1) {
										pl.getInventory().addItem(PetMechanics.generatePetEgg(EntityType.SHEEP, "green"));
									}
								}
							}
							if(new_pet_type.equalsIgnoreCase("easter_chicken")) {
								// ItemStack pet = new
								// ItemStack(Material.MONSTER_EGG, 1,
								// (short)96);
								if(!(PetMechanics.containsPet(pl.getInventory(), new_pet_type)) && !(PetMechanics.containsPet(MoneyMechanics.bank_contents.get(pl.getName()), new_pet_type))) {
									if(pl.getInventory().firstEmpty() != -1) {
										pl.getInventory().addItem(PetMechanics.generatePetEgg(EntityType.CHICKEN, ""));
									}
								}
							}
							if(new_pet_type.equalsIgnoreCase("april_creeper")) {
								// ItemStack pet = new
								// ItemStack(Material.MONSTER_EGG, 1,
								// (short)96);
								if(!(PetMechanics.containsPet(pl.getInventory(), new_pet_type)) && !(PetMechanics.containsPet(MoneyMechanics.bank_contents.get(pl.getName()), new_pet_type))) {
									if(pl.getInventory().firstEmpty() != -1) {
										pl.getInventory().addItem(PetMechanics.generatePetEgg(EntityType.CREEPER, ""));
									}
								}
							}
							if(new_pet_type.equalsIgnoreCase("beta_slime")) {
								// ItemStack pet = new
								// ItemStack(Material.MONSTER_EGG, 1,
								// (short)96);
								if(!(PetMechanics.containsPet(pl.getInventory(), new_pet_type)) && !(PetMechanics.containsPet(MoneyMechanics.bank_contents.get(pl.getName()), new_pet_type))) {
									if(pl.getInventory().firstEmpty() != -1) {
										pl.getInventory().addItem(PetMechanics.generatePetEgg(EntityType.SLIME, ""));
									}
								}
							}
							if(new_pet_type.equalsIgnoreCase("july_creeper")) {
								// ItemStack pet = new
								// ItemStack(Material.MONSTER_EGG, 1,
								// (short)96);
								if(!(PetMechanics.containsPet(pl.getInventory(), new_pet_type)) && !(PetMechanics.containsPet(MoneyMechanics.bank_contents.get(pl.getName()), new_pet_type))) {
									if(pl.getInventory().firstEmpty() != -1) {
										pl.getInventory().addItem(PetMechanics.generatePetEgg(EntityType.CREEPER, "july"));
									}
								}
							}
							if(new_pet_type.equalsIgnoreCase("baby_horse")) {
								// ItemStack pet = new
								// ItemStack(Material.MONSTER_EGG, 1,
								// (short)96);
								if(!(PetMechanics.containsPet(pl.getInventory(), new_pet_type)) && !(PetMechanics.containsPet(MoneyMechanics.bank_contents.get(pl.getName()), new_pet_type))) {
									if(pl.getInventory().firstEmpty() != -1) {
										pl.getInventory().addItem(PetMechanics.generatePetEgg(EntityType.HORSE, ""));
									}
								}
							}
						}
					}
				}
				
				if(inputLine.startsWith("[ecash]")) {
					// [ecash]p_name:new_net
					String p_name = inputLine.substring(inputLine.indexOf("]") + 1, inputLine.indexOf(":"));
					int ecash = Integer.parseInt(inputLine.substring(inputLine.indexOf(":") + 1, inputLine.length()));
					
					if(Hive.player_inventory.containsKey(p_name)) { // They're
																	// online!
						int amount = ecash;
						if(Hive.player_ecash.containsKey(p_name)) {
							amount = ecash - Hive.player_ecash.get(p_name);
						}
						Hive.player_ecash.put(p_name, ecash);
						if(Bukkit.getPlayer(p_name) != null) {
							Player pl = Bukkit.getPlayer(p_name);
							pl.sendMessage(ChatColor.GOLD + "  +" + amount + ChatColor.BOLD + " E-CASH");
							pl.playSound(pl.getLocation(), Sound.ORB_PICKUP, 1F, 1F);
						}
					}
				}
				
				if(inputLine.startsWith("[forum_group]")) {
					// Locally save a player's forum group
					String p_name = inputLine.substring(inputLine.indexOf("]") + 1, inputLine.indexOf(":"));
					int rank = Integer.parseInt(inputLine.substring(inputLine.indexOf(":") + 1, inputLine.length()));
					
					Hive.forum_usergroup.put(p_name, rank);
				}
				
				if(inputLine.startsWith("[rank_map]")) {
					// Locally save a player's rank.
					String p_name = inputLine.substring(inputLine.indexOf("]") + 1, inputLine.indexOf(":"));
					String rank = inputLine.substring(inputLine.indexOf(":") + 1, inputLine.length());
					
					PermissionMechanics.rank_map.put(p_name, rank);
				}
				
				if(inputLine.startsWith("@server_num@")) {
					// New server number data for a player.
					String p_name = inputLine.substring(inputLine.lastIndexOf("@") + 1, inputLine.indexOf(":"));
					int server_num = Integer.parseInt(inputLine.substring(inputLine.indexOf(":") + 1, inputLine.length()));
					
					CommunityMechanics.player_server_num.put(p_name, server_num);
				}
				
				if(inputLine.startsWith("[sq_online]")) {
					// Send login data to all buddies.
					String p_name = inputLine.substring(inputLine.lastIndexOf("]") + 1, inputLine.indexOf(":"));
					String server_name = inputLine.substring(inputLine.indexOf(":") + 1, inputLine.length());
					OfflinePlayer op = Bukkit.getServer().getOfflinePlayer(p_name);
					
					if(op.isOp()) {
						in.close();
						return;
					}
					
					for(Player pl : Bukkit.getServer().getOnlinePlayers()) {
						if(CommunityMechanics.buddy_list.containsKey(pl.getName())) {
							List<String> lbuddy_list = CommunityMechanics.buddy_list.get(pl.getName());
							if(lbuddy_list.contains(p_name)) {
								// Tell them! and update book!
								
								pl.sendMessage(ChatColor.YELLOW + p_name + " has joined " + server_name + ".");
								pl.playSound(pl.getLocation(), Sound.ORB_PICKUP, 2F, 1.2F);
								CommunityMechanics.updateCommBook(pl);
							}
						}
					}
				}
				
				if(inputLine.startsWith("[sq_offline]")) {
					// Send login data to all buddies.
					String p_name = inputLine.substring(inputLine.lastIndexOf("]") + 1, inputLine.indexOf(":"));
					String server_name = inputLine.substring(inputLine.indexOf(":") + 1, inputLine.length());
					OfflinePlayer op = Bukkit.getServer().getOfflinePlayer(p_name);
					
					if(op.isOp()) {
						in.close();
						return;
					}
					
					for(Player pl : Bukkit.getServer().getOnlinePlayers()) {
						if(CommunityMechanics.buddy_list.containsKey(pl.getName())) {
							List<String> lbuddy_list = CommunityMechanics.buddy_list.get(pl.getName());
							if(lbuddy_list.contains(p_name)) {
								// Tell them! and update book!
								pl.sendMessage(ChatColor.YELLOW + p_name + " has logged out of " + server_name + ".");
								pl.playSound(pl.getLocation(), Sound.ORB_PICKUP, 2F, 0.5F);
							}
						}
					}
				}
				
				// START GUILDS
				if(inputLine.startsWith("[gdisband]")) {
					// [gdisband]g_name
					
					String g_name = inputLine.substring(inputLine.indexOf("]") + 1, inputLine.length());
					
					if(!(GuildMechanics.guild_map.containsKey(g_name))) {
						in.close();
						return; // Nothing to do here.
					}
					
					for(String s : GuildMechanics.getGuildMembers(g_name)) {
						if(Bukkit.getPlayer(s) != null) {
							Player pl = Bukkit.getPlayer(s);
							pl.sendMessage("");
							pl.sendMessage(ChatColor.RED + "Your guild, '" + ChatColor.DARK_AQUA + g_name + ChatColor.RED + "', has been disbanded by your leader.");
						}
						GuildMechanics.leaveGuild(s);
					}
					
					GuildMechanics.guild_map.remove(g_name);
					GuildMechanics.guild_handle_map.remove(g_name);
					GuildMechanics.guild_colors.remove(g_name);
					GuildMechanics.guild_server.remove(g_name);
					GuildMechanics.guild_motd.remove(g_name);
				}
				
				if(inputLine.startsWith("[gbio]")) {
					// [gbio]g_name$bio$
					String g_name = inputLine.substring(inputLine.indexOf("]") + 1, inputLine.indexOf("$"));
					String bio = inputLine.substring(inputLine.indexOf("$") + 1, inputLine.lastIndexOf("$"));
					
					if(!(GuildMechanics.guild_map.containsKey(g_name))) {
						in.close();
						return; // Nothing to do here.
					}
					
					GuildMechanics.setLocalGuildBIO(g_name, bio);
				}
				
				if(inputLine.startsWith("[gmotd]")) {
					// [gmotd]g_name$motd$
					String g_name = inputLine.substring(inputLine.indexOf("]") + 1, inputLine.indexOf("$"));
					String motd = inputLine.substring(inputLine.indexOf("$") + 1, inputLine.lastIndexOf("$"));
					
					if(!(GuildMechanics.guild_map.containsKey(g_name))) {
						in.close();
						return; // Nothing to do here.
					}
					
					GuildMechanics.setLocalGuildMOTD(g_name, motd);
				}
				
				if(inputLine.startsWith("[gadd]")) {
					// [gadd]p_name,g_name:p_inviter
					String p_name = inputLine.substring(inputLine.indexOf("]") + 1, inputLine.indexOf(","));
					String g_name = inputLine.substring(inputLine.indexOf(",") + 1, inputLine.indexOf(":"));
					String p_inviter = inputLine.substring(inputLine.indexOf(":") + 1, inputLine.length());
					
					if(!(GuildMechanics.guild_map.containsKey(g_name))) {
						in.close();
						return; // Nothing to do here.
					}
					
					GuildMechanics.addPlayerToGuild(p_name, g_name);
					
					for(String s : GuildMechanics.getGuildMembers(g_name)) {
						if(Bukkit.getPlayer(ChatColor.stripColor(s)) != null) {
							Player pty_mem = Bukkit.getPlayer(ChatColor.stripColor(s));
							pty_mem.sendMessage(ChatColor.DARK_AQUA + "<" + ChatColor.BOLD + GuildMechanics.guild_handle_map.get(g_name) + ChatColor.DARK_AQUA + "> " + ChatColor.DARK_AQUA.toString() + p_name + ChatColor.GRAY.toString() + " has " + ChatColor.UNDERLINE + "joined" + ChatColor.GRAY + " your guild. [INVITE: " + ChatColor.ITALIC + p_inviter + ChatColor.GRAY + "]");
						}
					}
					
				}
				
				if(inputLine.startsWith("[gdemote]")) {
					// [gdemote]p_name,g_name:rank
					String p_name = inputLine.substring(inputLine.indexOf("]") + 1, inputLine.indexOf(","));
					String g_name = inputLine.substring(inputLine.indexOf(",") + 1, inputLine.indexOf(":"));
					int rank = Integer.parseInt(inputLine.substring(inputLine.indexOf(":") + 1, inputLine.length()));
					
					if(!(GuildMechanics.guild_map.containsKey(g_name))) {
						in.close();
						return; // Nothing to do here.
					}
					
					GuildMechanics.setGuildRank(p_name, rank); // rank == 2 for
																// officer.
					
					// Tell the world!
					if(Bukkit.getPlayer(p_name) != null) {
						Player demoted = Bukkit.getPlayer(p_name);
						demoted.sendMessage(ChatColor.RED + "You have been " + ChatColor.UNDERLINE + "demoted" + ChatColor.RED + "to the rank of " + ChatColor.BOLD + "GUILD MEMBER" + ChatColor.RED + " in " + g_name);
					}
					
					for(String s : GuildMechanics.getOnlineGuildMembers(g_name)) {
						if(Bukkit.getPlayer(ChatColor.stripColor(s)) != null) {
							Player pl = Bukkit.getPlayer(ChatColor.stripColor(s));
							pl.sendMessage(ChatColor.DARK_AQUA.toString() + "<" + ChatColor.BOLD + GuildMechanics.guild_handle_map.get(g_name) + ChatColor.DARK_AQUA + ">" + ChatColor.RED + " " + p_name + " has been " + ChatColor.UNDERLINE + "demoted" + ChatColor.RED + " to the rank of " + ChatColor.BOLD + "GUILD MEMBER.");
						}
					}
				}
				
				if(inputLine.startsWith("[gpromote]")) {
					// [gpromote]p_name,g_name:rank
					String p_name = inputLine.substring(inputLine.indexOf("]") + 1, inputLine.indexOf(","));
					String g_name = inputLine.substring(inputLine.indexOf(",") + 1, inputLine.indexOf(":"));
					int rank = Integer.parseInt(inputLine.substring(inputLine.indexOf(":") + 1, inputLine.length()));
					
					if(!(GuildMechanics.guild_map.containsKey(g_name))) {
						in.close();
						return; // Nothing to do here.
					}
					
					GuildMechanics.setGuildRank(p_name, rank); // rank == 2 for
																// officer.
					
					// Tell the world!
					if(Bukkit.getPlayer(p_name) != null) {
						Player promoted = Bukkit.getPlayer(p_name);
						promoted.sendMessage(ChatColor.DARK_AQUA + "You have been " + ChatColor.UNDERLINE + "promoted" + ChatColor.DARK_AQUA + "to the rank of " + ChatColor.BOLD + "GUILD OFFICER" + ChatColor.DARK_AQUA + " in " + GuildMechanics.getGuild(p_name));
					}
					
					for(String s : GuildMechanics.getOnlineGuildMembers(g_name)) {
						if(Bukkit.getPlayer(ChatColor.stripColor(s)) != null) {
							Player pl = Bukkit.getPlayer(ChatColor.stripColor(s));
							pl.sendMessage(ChatColor.DARK_AQUA.toString() + "<" + ChatColor.BOLD + GuildMechanics.guild_handle_map.get(g_name) + ChatColor.DARK_AQUA + ">" + ChatColor.GREEN + " " + p_name + " has been " + ChatColor.UNDERLINE + "promoted" + ChatColor.GREEN + " to the rank of " + ChatColor.BOLD + "GUILD OFFICER.");
						}
					}
				}
				
				if(inputLine.startsWith("[gquit]")) {
					// [gquit]p_name,g_name
					String p_name = inputLine.substring(inputLine.indexOf("]") + 1, inputLine.indexOf(","));
					String g_name = inputLine.substring(inputLine.indexOf(",") + 1, inputLine.length());
					
					if(!(GuildMechanics.guild_map.containsKey(g_name))) {
						in.close();
						return; // Nothing to do here.
					}
					
					GuildMechanics.leaveGuild(p_name, g_name, true);
					// Leave guild on every server so there's no data
					// inconsistancies. FTW.
					
					for(String s : GuildMechanics.getOnlineGuildMembers(g_name)) {
						if(Bukkit.getPlayer(ChatColor.stripColor(s)) != null) {
							Player pl = Bukkit.getPlayer(ChatColor.stripColor(s));
							pl.sendMessage(ChatColor.DARK_AQUA + "<" + ChatColor.BOLD + GuildMechanics.guild_handle_map.get(g_name) + ChatColor.DARK_AQUA + "> " + ChatColor.DARK_AQUA + p_name + ChatColor.GRAY + " has " + ChatColor.UNDERLINE + "left" + ChatColor.GRAY + " the guild.");
						}
					}
				}
				
				if(inputLine.startsWith("[gkick]")) {
					// [gkick]p_name,g_name:p_kicker
					String p_name = inputLine.substring(inputLine.indexOf("]") + 1, inputLine.indexOf(","));
					String g_name = inputLine.substring(inputLine.indexOf(",") + 1, inputLine.indexOf(":"));
					String p_kicker = inputLine.substring(inputLine.indexOf(":") + 1, inputLine.length());
					
					if(!(GuildMechanics.guild_map.containsKey(g_name))) {
						in.close();
						return; // Nothing to do here.
					}
					
					GuildMechanics.leaveGuild(p_name, g_name, true);
					// Leave guild on every server so there's no data
					// inconsistancies. FTW.
					
					for(String s : GuildMechanics.getOnlineGuildMembers(g_name)) {
						if(Bukkit.getPlayer(ChatColor.stripColor(s)) != null) {
							Player pl = Bukkit.getPlayer(ChatColor.stripColor(s));
							pl.sendMessage(ChatColor.DARK_AQUA + "<" + ChatColor.BOLD + GuildMechanics.guild_handle_map.get(g_name) + ChatColor.DARK_AQUA + "> " + ChatColor.DARK_AQUA + p_name + " has been " + ChatColor.UNDERLINE + "kicked" + ChatColor.DARK_AQUA + " by " + p_kicker + ".");
						}
					}
				}
				
				if(inputLine.startsWith("[quit]")) {
					// [join]p_name,g_name@server_name
					String p_name = inputLine.substring(inputLine.indexOf("]") + 1, inputLine.indexOf(","));
					String g_name = inputLine.substring(inputLine.indexOf(",") + 1, inputLine.indexOf("@"));
					String server_name = inputLine.substring(inputLine.indexOf("@") + 1, inputLine.length());
					
					GuildMechanics.guild_member_server.remove(p_name);
					
					if(!(GuildMechanics.guild_map.containsKey(g_name))) {
						in.close();
						return; // Nothing to do here.
					}
					
					for(String g_member : GuildMechanics.getGuildMembers(g_name)) {
						Player pl_g_member = null;
						if(Bukkit.getPlayer(g_member) != null) {
							pl_g_member = Bukkit.getPlayer(g_member);
						}
						
						if(pl_g_member == null) {
							continue;
						}
						
						if(p_name.equalsIgnoreCase(pl_g_member.getName())) {
							continue; // Don't tell ourselves. (server
										// transfers)
						}
						
						/*
						 * if(CommunityMechanics.socialQuery(g_member, p_name,
						 * "CHECK_BUD")){ continue; // They're buddies, let the
						 * buddy plugin take care of telling them they logged
						 * in. }
						 */
						
						pl_g_member.sendMessage(ChatColor.DARK_AQUA + "<" + ChatColor.BOLD + GuildMechanics.guild_handle_map.get(g_name) + ChatColor.DARK_AQUA + "> " + ChatColor.GRAY + p_name + " has logged out of " + server_name + ".");
					}
					
				}
				// TODO: Add /shard to character journal
				// TODO: Fix all the inconsistancies with buddy login/logout
				// Players should ONLY get messages about a player logging
				// in/out if ONE player has them added and they are NOT on other
				// player's ignore list.
				
				if(inputLine.startsWith("[join]")) {
					// [join]p_name,g_name@server_name
					String p_name = inputLine.substring(inputLine.indexOf("]") + 1, inputLine.indexOf(","));
					String g_name = inputLine.substring(inputLine.indexOf(",") + 1, inputLine.indexOf("@"));
					String server_name = inputLine.substring(inputLine.indexOf("@") + 1, inputLine.length());
					
					GuildMechanics.guild_member_server.put(p_name, server_name);
					// Set local data so that tab list will show them as online
					// another server.
					
					if(!(GuildMechanics.guild_map.containsKey(g_name))) {
						in.close();
						return; // Nothing to do here.
					}
					
					for(String g_member : GuildMechanics.getGuildMembers(g_name)) {
						Player pl_g_member = null;
						if(Bukkit.getPlayer(g_member) != null) {
							pl_g_member = Bukkit.getPlayer(g_member);
						}
						
						if(pl_g_member == null) {
							continue;
						}
						
						if(p_name.equalsIgnoreCase(pl_g_member.getName())) {
							continue; // Don't tell ourselves. (server
										// transfers)
						}
						
						/*
						 * if(CommunityMechanics.socialQuery(g_member, p_name,
						 * "CHECK_BUD")){ continue; // They're buddies, let the
						 * buddy plugin take care of telling them they logged
						 * in. }
						 */
						
						pl_g_member.sendMessage(ChatColor.DARK_AQUA + "<" + ChatColor.BOLD + GuildMechanics.guild_handle_map.get(g_name) + ChatColor.DARK_AQUA + "> " + ChatColor.GRAY + p_name + " has joined " + server_name + ".");
						
					}
					
				}
				
				if(inputLine.startsWith("&")) {
					// packet_data = to_guild/from@US-0: packet_data contents
					// here.
					String g_name = inputLine.substring(1, inputLine.indexOf("/"));
					
					if(!(GuildMechanics.guild_map.containsKey(g_name))) {
						in.close();
						return; // Nothing to do here.
					}
					
					String p_sender = inputLine.substring(inputLine.indexOf("/") + 1, inputLine.indexOf("@"));
					String sender_server_name = inputLine.substring(inputLine.indexOf("@") + 1, inputLine.indexOf(":"));
					String raw_msg = inputLine.substring(inputLine.indexOf(":") + 1, inputLine.length());
					
					for(String g_member : GuildMechanics.getGuildMembers(g_name)) {
						Player pl_g_member = null;
						if(Bukkit.getPlayer(g_member) != null) {
							pl_g_member = Bukkit.getPlayer(g_member);
						}
						
						if(pl_g_member == null || !pl_g_member.getName().equalsIgnoreCase(g_member)) {
							continue;
						}
						
						ChatColor p_color = ChatMechanics.getPlayerColor(p_sender, g_member);
						String prefix = ChatMechanics.getPlayerPrefix(p_sender, false);
						
						String personal_msg = raw_msg;
						if(ChatMechanics.hasAdultFilter(g_member)) {
							personal_msg = ChatMechanics.censorMessage(personal_msg);
						}
						
						if(personal_msg.endsWith(" ")) {
							personal_msg = personal_msg.substring(0, personal_msg.length() - 1);
						}
						
						personal_msg = ChatMechanics.fixCapsLock(personal_msg);
						
						pl_g_member.sendMessage(ChatColor.DARK_AQUA.toString() + "<" + ChatColor.BOLD + GuildMechanics.guild_handle_map.get(g_name) + ChatColor.DARK_AQUA + ">" + " " + ChatColor.GRAY + "" + sender_server_name + "" + ChatColor.GRAY + " " + prefix + p_color + p_sender + ": " + ChatColor.GRAY + personal_msg);
					}
				}
				
				// END GUILDS
				
				if(inputLine.startsWith("#")) { // Reply to a "^" message.
					// #player_name_to_tell/player_name_about=offline OR
					// #player_name_to_tell/player_name_about=online,US-0:MESSAGE
					String p_name_to_tell = inputLine.substring(1, inputLine.indexOf("/"));
					String p_name_about = inputLine.substring(inputLine.indexOf("/") + 1, inputLine.indexOf("="));
					
					String status = inputLine.substring(inputLine.indexOf("=") + 1, inputLine.indexOf(","));
					if(status.equalsIgnoreCase("offline")) {
						if(Bukkit.getPlayer(p_name_to_tell) != null) {
							Player p_to_tell = Bukkit.getPlayer(p_name_to_tell);
							p_to_tell.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + p_name_about + ChatColor.RED + " is OFFLINE.");
						}
						in.close();
						return;
					}
					
					if(status.equalsIgnoreCase("notells")) {
						Bukkit.getPlayer(p_name_to_tell).sendMessage(ChatColor.RED + "" + ChatColor.BOLD + p_name_about + ChatColor.RED + " currently has private messages " + ChatColor.UNDERLINE + "DISABLED.");
						in.close();
						return;
					}
					
					if(status.equalsIgnoreCase("online")) {
						if(Bukkit.getPlayer(p_name_to_tell) != null) {
							Player p_to_tell = Bukkit.getPlayer(p_name_to_tell);
							String server_name = inputLine.substring(inputLine.indexOf(",") + 1, inputLine.indexOf(":"));
							String message = inputLine.substring(inputLine.indexOf(":") + 1, inputLine.length());
							p_to_tell.sendMessage(ChatColor.DARK_GRAY + "" + ChatColor.BOLD + "TO (" + server_name + ") " + ChatColor.RESET + p_name_about + ":" + ChatColor.WHITE + message);
						}
						in.close();
						return;
					}
					in.close();
					return;
				}
				
				if(inputLine.startsWith("^")) { // Sending message.
					inputLine = inputLine.substring(inputLine.indexOf("^") + 1, inputLine.length());
					
					String p_to_name = inputLine.substring(0, inputLine.indexOf("/"));
					String guild_prefix = inputLine.substring(inputLine.indexOf("/") + 1, inputLine.indexOf("@"));
					String p_from_name = inputLine.substring(inputLine.indexOf("@") + 1, inputLine.indexOf(";"));
					String from_server = inputLine.substring(inputLine.indexOf(";") + 1, inputLine.indexOf(":"));
					String message = inputLine.substring(inputLine.indexOf(":") + 1, inputLine.length());
					
					int server_num = Integer.parseInt(from_server.split("-")[1]);
					if(from_server.contains("EU-")) {
						server_num += 1000;
					}
					if(from_server.contains("BR-")) {
						server_num += 2000;
					}
					String server_ip = CommunityMechanics.server_list.get(server_num);
					if(!(PermissionMechanics.rank_map.containsKey(p_from_name))) {
						PermissionMechanics.downloadRank(p_from_name); // Get
																		// rank
																		// data
																		// of
																		// player
																		// on
																		// local
																		// server.
					}
					
					if(Bukkit.getPlayer(p_to_name) != null) {
						Player p_to = Bukkit.getPlayer(p_to_name);
						if(!PermissionMechanics.getRank(p_from_name).equalsIgnoreCase("gm") && !PermissionMechanics.getRank(p_from_name).equalsIgnoreCase("pmod") && CommunityMechanics.toggle_list.get(p_to.getName()).contains("tells") && !(CommunityMechanics.isPlayerOnBuddyList(p_to, p_from_name))) {
							String result = "#" + p_from_name + "/" + p_to_name + "=" + "notells,";
							sendResultCrossServer(server_ip, result, server_num);
							in.close();
							return;
						}
						
						if(!PermissionMechanics.getRank(p_from_name).equalsIgnoreCase("gm") && !PermissionMechanics.getRank(p_from_name).equalsIgnoreCase("pmod") && CommunityMechanics.isPlayerOnIgnoreList(p_to, p_from_name)) {
							String result = "#" + p_from_name + "/" + p_to_name + "=" + "offline,";
							sendResultCrossServer(server_ip, result, server_num);
							in.close();
							return;
						}
						
						ChatColor c = ChatColor.GRAY;
						
						c = ChatMechanics.getPlayerColor(p_from_name, p_to.getName());
						String from_prefix = ChatMechanics.getPlayerPrefix(p_from_name, true);
						
						ChatColor to_c = ChatMechanics.getPlayerColor(p_to.getName(), p_from_name);
						String to_prefix = ChatMechanics.getPlayerPrefix(p_to.getName(), true);
						
						String to_personal_msg = message;
						if(ChatMechanics.hasAdultFilter(p_to.getName())) {
							to_personal_msg = "";
							for(String s : message.split(" ")) {
								for(String bad : ChatMechanics.bad_words) {
									if(s.contains(bad)) {
										s = s.replaceAll(bad, "****");
									}
								}
								to_personal_msg += s + " ";
							}
						}
						
						p_to.sendMessage(ChatColor.DARK_GRAY + "" + ChatColor.BOLD + "FROM (" + from_server + ") " + ChatColor.WHITE + guild_prefix + ChatColor.RESET + from_prefix + c + p_from_name + ":" + ChatColor.WHITE + to_personal_msg);
						
						if(!(CommunityMechanics.last_reply.containsKey(p_to.getName())) || !CommunityMechanics.last_reply.get(p_to.getName()).equalsIgnoreCase(p_from_name)) {
							p_to.playSound(p_to.getLocation(), Sound.CHICKEN_EGG_POP, 2F, 1.2F);
							CommunityMechanics.last_reply.put(p_to.getName(), p_from_name);
						}
						
						String result = "#" + p_from_name + "/" + to_prefix + to_c.toString() + p_to_name + "=" + "online," + Bukkit.getMotd().substring(0, Bukkit.getMotd().indexOf(" ")) + ":" + message;
						sendResultCrossServer(server_ip, result, server_num);
						CommunityMechanics.log.info(p_from_name + "@" + from_server + " -> " + p_to_name + " " + message);
					} else if(Bukkit.getPlayer(p_to_name) == null) {
						String result = "#" + p_from_name + "/" + p_to_name + "=" + "offline,";
						sendResultCrossServer(server_ip, result, server_num);
					}
				}
				
			}
			
		} catch(Exception e) {
			e.printStackTrace();
			return;
		}
	}
}
