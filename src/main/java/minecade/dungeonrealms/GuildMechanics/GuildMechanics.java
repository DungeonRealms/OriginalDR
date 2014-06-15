package minecade.dungeonrealms.GuildMechanics;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import minecade.dungeonrealms.Main;
import minecade.dungeonrealms.Utils;
import minecade.dungeonrealms.ChatMechanics.ChatMechanics;
import minecade.dungeonrealms.CommunityMechanics.CommunityMechanics;
import minecade.dungeonrealms.DuelMechanics.DuelMechanics;
import minecade.dungeonrealms.GuildMechanics.commands.CommandG;
import minecade.dungeonrealms.GuildMechanics.commands.CommandGAccept;
import minecade.dungeonrealms.GuildMechanics.commands.CommandGBanner;
import minecade.dungeonrealms.GuildMechanics.commands.CommandGBio;
import minecade.dungeonrealms.GuildMechanics.commands.CommandGDecline;
import minecade.dungeonrealms.GuildMechanics.commands.CommandGDemote;
import minecade.dungeonrealms.GuildMechanics.commands.CommandGInvite;
import minecade.dungeonrealms.GuildMechanics.commands.CommandGKick;
import minecade.dungeonrealms.GuildMechanics.commands.CommandGMotd;
import minecade.dungeonrealms.GuildMechanics.commands.CommandGPromote;
import minecade.dungeonrealms.GuildMechanics.commands.CommandGQuit;
import minecade.dungeonrealms.GuildMechanics.commands.CommandGuild;
import minecade.dungeonrealms.GuildMechanics.commands.CommandGuildSetLeader;
import minecade.dungeonrealms.Hive.Hive;
import minecade.dungeonrealms.ItemMechanics.ItemMechanics;
import minecade.dungeonrealms.KarmaMechanics.KarmaMechanics;
import minecade.dungeonrealms.PermissionMechanics.PermissionMechanics;
import minecade.dungeonrealms.RealmMechanics.RealmMechanics;
import minecade.dungeonrealms.RepairMechanics.RepairMechanics;
import minecade.dungeonrealms.database.ConnectionPool;

import org.apache.commons.lang.StringEscapeUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_7_R2.inventory.CraftItemStack;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.material.Dye;
import org.mcsg.double0negative.tabapi.TabAPI;

@SuppressWarnings("deprecation")
public class GuildMechanics implements Listener {
	static Logger log = Logger.getLogger("Minecraft");
	
	public static GuildMechanics instance = null;
	
	public static final Color DEFAULT_LEATHER_COLOR = Color.fromRGB(0xA06540);
	
	public static ItemStack guild_dye = ItemMechanics.signCustomItem(Material.EXP_BOTTLE, (short) 0, ChatColor.AQUA.toString() + "Guild Armor Dye", ChatColor.GRAY.toString() + "Click onto a piece of armor to color it.");
	public static ItemStack color_selector_divider = ItemMechanics.signCustomItem(Material.PISTON_MOVING_PIECE, (short) 0, "", "");
	public ItemStack gray_button = ItemMechanics.signCustomItem(Material.INK_SACK, (short) 8, ChatColor.YELLOW.toString() + "Click to CONFIRM Color Choice.", ChatColor.GRAY.toString() + "You can change this color later for a fee.");
	
	public static ItemStack guild_emblem = ItemMechanics.signCustomItem(Material.EYE_OF_ENDER, (short) 1, ChatColor.DARK_AQUA.toString() + ChatColor.BOLD.toString() + "Guild Emblem", ChatColor.GRAY.toString() + "Guild:" + ChatColor.GRAY.toString() + " Legendary Heroes" + "," + ChatColor.DARK_AQUA + "Right Click: " + ChatColor.GRAY.toString() + "Open Guild Portal" + "," + ChatColor.DARK_AQUA + "Left Click: " + ChatColor.GRAY.toString() + "Invite to Guild" + ",");
	
	private static final String ALPHA_NUM = "123456789";
	
	private static SecureRandom random = new SecureRandom();
	
	public static HashMap<String, Color> guild_colors = new HashMap<String, Color>();
	// Guild Name, Leather "color" NBT.
	
	public static ConcurrentHashMap<String, Integer> guild_creation_name_check = new ConcurrentHashMap<String, Integer>();
	// 0 = pending, 1 = oscar mike, -1 = we have a problem.
	// Player Name, Check Complete -- This is because we need to SQL query to see if guild name/handle exists.
	
	public static HashMap<String, String> guild_member_server = new HashMap<String, String>();
	// Player Name, Server String
	// This is used for the TAB menu to show what server they're on if not local.
	
	//public static HashMap<String, Team> guild_teams = new HashMap<String, Team>();
	// Guild Name, Leather "color" NBT.
	
	public static HashMap<String, List<String>> guild_map = new HashMap<String, List<String>>();
	// Guild Name, Guild Members
	
	public static HashMap<String, String> guild_motd = new HashMap<String, String>();
	// Guild Name, Guild MOTD
	
	public static HashMap<String, String> guild_bio_dynamic = new HashMap<String, String>();
	// Guild Owner Player Name, Guild Biography (being written)
	
	public static HashMap<String, String> guild_bio = new HashMap<String, String>();
	// Guild Name, Guild Biography
	
	public static HashMap<String, List<String>> guild_map_clone = new HashMap<String, List<String>>();
	// Guild Name, Guild Members
	
	public static HashMap<String, Integer> guild_server = new HashMap<String, Integer>();
	// Guild Name, Home Server (serv_code, +1000EU)
	
	public static HashMap<String, String> guild_handle_map = new HashMap<String, String>();
	// Guild Name, Chat Shorthand "EXA" - Extreme X Action
	
	public static HashMap<String, String> player_guilds = new HashMap<String, String>();
	// Player Name, Guild Name
	
	public static HashMap<String, Integer> player_guild_rank = new HashMap<String, Integer>();
	// Player Name, Guild Rank(1, 2, 3)
	// 1 = member
	// 2 = officer
	// 3 = founder
	
	public static HashMap<String, String> guild_invite = new HashMap<String, String>();
	// Player Name, Guild Invited To
	
	public static HashMap<String, String> guild_inviter = new HashMap<String, String>();
	// Player Name, Player Who Invited
	
	public static HashMap<String, Long> guild_invite_time = new HashMap<String, Long>();
	// Player Name, Time of Invite (for timeout)
	
	public static HashMap<String, Integer> guild_creation = new HashMap<String, Integer>();
	// Player Name, Creation Step
	// 1 = Guild Name
	// 2 = Home Server
	// 3 = Guild Color Combination
	// 4 = Color #2
	// 5 = Summary + Confirm
	
	public static HashMap<String, List<String>> guild_creation_data = new HashMap<String, List<String>>();
	// Player Name, Guild Data
	// Guild Data:
	// [guild_name, guild_tag, server_num, guild_color(hex)]
	
	public static ConcurrentHashMap<String, Location> guild_creation_npc_location = new ConcurrentHashMap<String, Location>();
	// Player Name, Location of Guild Registrar
	
	public static HashMap<String, String> guild_creation_code = new HashMap<String, String>();
	// Player Name, Guild Creation Code To Enter
	
	public static List<String> guild_only = new ArrayList<String>();
	// Used for guild chat.
	
	public static List<String> guild_quit_confirm = new ArrayList<String>();
	
	// Used for confirming /gquit
	
	public void onEnable() {
		instance = this;
		Bukkit.getServer().getPluginManager().registerEvents(this, Main.plugin);
		
		Main.plugin.getCommand("g").setExecutor(new CommandG());
		Main.plugin.getCommand("gaccept").setExecutor(new CommandGAccept());
		Main.plugin.getCommand("gbanner").setExecutor(new CommandGBanner());
		Main.plugin.getCommand("gbio").setExecutor(new CommandGBio());
		Main.plugin.getCommand("gdecline").setExecutor(new CommandGDecline());
		Main.plugin.getCommand("gdemote").setExecutor(new CommandGDemote());
		Main.plugin.getCommand("ginvite").setExecutor(new CommandGInvite());
		Main.plugin.getCommand("gkick").setExecutor(new CommandGKick());
		Main.plugin.getCommand("gmotd").setExecutor(new CommandGMotd());
		Main.plugin.getCommand("gpromote").setExecutor(new CommandGPromote());
		Main.plugin.getCommand("gquit").setExecutor(new CommandGQuit());
		Main.plugin.getCommand("guild").setExecutor(new CommandGuild());
		Main.plugin.getCommand("gsetleader").setExecutor(new CommandGuildSetLeader());
		
		// Player movement event for guild creation.
		Bukkit.getServer().getScheduler().scheduleAsyncRepeatingTask(Main.plugin, new Runnable() {
			public void run() {
				for(String s : guild_creation_npc_location.keySet()) {
					if(Bukkit.getPlayer(s) != null) {
						Player pl = Bukkit.getPlayer(s);
						if(!pl.getWorld().getName().equalsIgnoreCase(guild_creation_npc_location.get(s).getWorld().getName()) || pl.getLocation().distanceSquared(guild_creation_npc_location.get(s)) >= 64.0D) {
							// >8 blocks from start.
							guild_creation.remove(pl.getName());
							guild_creation_npc_location.remove(pl.getName());
							guild_creation_data.remove(pl.getName());
							guild_creation_name_check.remove(pl.getName());
							pl.sendMessage(ChatColor.GRAY + "Guild Registrar: " + ChatColor.WHITE + "Goodbye!");
							return;
						}
					}
				}
			}
		}, 10 * 20L, 1 * 20L);
		
		// Handles guild invite expiration/timeout.
		Bukkit.getServer().getScheduler().scheduleAsyncRepeatingTask(Main.plugin, new Runnable() {
			public void run() {
				for(Entry<String, Long> data : guild_invite_time.entrySet()) {
					String p_name = data.getKey();
					long time = data.getValue();
					
					if((System.currentTimeMillis() - time) >= (30 * 1000)) {
						// 30s invite timeout.
						String party_owner = guild_invite.get(p_name);
						guild_invite.remove(p_name);
						guild_inviter.remove(p_name);
						guild_invite_time.remove(p_name);
						if(Bukkit.getPlayer(p_name) != null) {
							Player pl = Bukkit.getPlayer(p_name);
							pl.sendMessage(ChatColor.RED + "Guild invite from " + ChatColor.BOLD + party_owner + ChatColor.RED + " expired.");
						}
						if(Bukkit.getPlayer(party_owner) != null) {
							Player pl = Bukkit.getPlayer(party_owner);
							pl.sendMessage(ChatColor.RED + "Guild invite to " + ChatColor.BOLD + p_name + ChatColor.RED + " has expired.");
						}
					}
				}
			}
		}, 5 * 20L, 20L);
		
		// Refreshes Tab API menu thing.
		Bukkit.getServer().getScheduler().scheduleAsyncRepeatingTask(Main.plugin, new Runnable() {
			public void run() {
				for(Player pl : Bukkit.getServer().getOnlinePlayers()) {
					if(Hive.login_time.containsKey(pl.getName()) && (System.currentTimeMillis() - Hive.login_time.get(pl.getName()) > 5000)) {
						updateGuildTabList(pl);
					}
				}
			}
		}, 12 * 20L, 6 * 20L);
		log.info("[GuildMechanics] has been ENABLED.");
	}
	
	public void onDisable() {
		log.info("[GuildMechanics] has been DISABLED.");
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerJoin(PlayerJoinEvent e) {
		final Player pl = e.getPlayer();
		if(inGuild(pl.getName())) {
			guildMemberJoin(pl); // Sends player join message to all guildies.
			final String g_name = getGuild(pl.getName());
			
			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
				public void run() {
					updateGuildTabList(pl);
					if(guild_motd.containsKey(g_name) && guild_motd.get(g_name) != null) {
						String motd = guild_motd.get(g_name);
						pl.sendMessage(ChatColor.DARK_AQUA + "<" + ChatColor.BOLD + guild_handle_map.get(g_name) + ChatColor.DARK_AQUA + "> " + ChatColor.BOLD + "MOTD: " + ChatColor.DARK_AQUA + motd);
					} else {
						pl.sendMessage(ChatColor.DARK_AQUA + "<" + ChatColor.BOLD + guild_handle_map.get(g_name) + ChatColor.DARK_AQUA + "> " + ChatColor.BOLD + "MOTD: " + ChatColor.DARK_AQUA + "No message of the day set. Use /gmotd <motd> to create one.");
					}
					
					for(String s : getOnlineGuildMembers(getGuild(pl.getName()))) {
						// For each online guild member, update their guild tab list.
						ChatColor.stripColor(s);
						updateGuildTabList(Bukkit.getPlayer(s));
					}
				}
			}, 10L);
		}
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		Player pl = e.getPlayer();
		final String p_name = pl.getName();
		if(inGuild(pl.getName())) {
			final String g_name = getGuild(pl.getName());
			guildMemberQuit(pl);
			
			// Remove local guild data from them.
			boolean members_online = false;
			for(Player online : Bukkit.getOnlinePlayers()) {
				if(pl.getName().equalsIgnoreCase(online.getName())) {
					continue; // Same person.
				}
				if(areGuildies(pl.getName(), online.getName())) {
					members_online = true;
					break;
				}
			}
			
			if(members_online == false) {
				// No more members online, delete guild data.
				
				guild_map.remove(g_name);
				guild_handle_map.remove(g_name);
				guild_colors.remove(g_name);
				guild_server.remove(g_name);
				guild_motd.remove(g_name);
				guild_bio.remove(g_name);
				log.info("[GuildMechanics] Detected no more guild members of guild " + g_name + " are online, deleting local data.");
			}
			
			else if(members_online == true) {
				
				Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
					public void run() {
						if(guild_map.containsKey(p_name)) {
							for(String s : getOnlineGuildMembers(g_name)) {
								// For each online guild member, update their guild tab list.
								ChatColor.stripColor(s);
								updateGuildTabList(Bukkit.getPlayer(s));
							}
						}
					}
				}, 2L);
				
			}
			
			player_guilds.remove(pl.getName());
			guild_only.remove(pl.getName());
			player_guild_rank.remove(pl.getName());
		}
		//TODO: Remove local guild data if no more guildies are online.
	}
	
	@EventHandler
	public void onAsyncPlayerChatEvent(AsyncPlayerChatEvent e) {
		Player pl = e.getPlayer();
		
		if(guild_bio_dynamic.containsKey(pl.getName())) {
			// They're writing their guild bio!
			e.setCancelled(true);
			
			// Add on to the bio.
			String bio = guild_bio_dynamic.get(pl.getName());
			
			String msg = ChatMechanics.censorMessage(e.getMessage());
			if(msg.equalsIgnoreCase("cancel")) {
				pl.sendMessage(ChatColor.RED + "/gbio - " + ChatColor.BOLD + "CANCELLED");
				guild_bio_dynamic.remove(pl.getName());
				return;
			}
			if(msg.equalsIgnoreCase("confirm")) {
				// TODO: SQL, tell guild, etc.
				setGuildBIO(getGuild(pl.getName()), bio);
				guild_bio_dynamic.remove(pl.getName());
				return;
			}
			
			if((bio.length() + msg.length()) > 512) {
				// Too long.
				int length = (bio.length() + msg.length());
				int overflow = length - 512;
				pl.sendMessage(ChatColor.RED + "Your guild biography would be " + length + " characters long with this addition, that's " + ChatColor.UNDERLINE + overflow + " more characters than allowed.");
				pl.sendMessage(ChatColor.GRAY + "No additional text has been added to the biography.");
				return;
			}
			
			if(bio.length() > 0) {
				bio += " ";
			}
			bio += msg;
			
			guild_bio_dynamic.put(pl.getName(), bio);
			pl.sendMessage(ChatColor.GREEN + "Biography appended. " + ChatColor.BOLD.toString() + bio.length() + "/512 characters.");
		}
		
		if(guild_quit_confirm.contains(pl.getName())) {
			e.setCancelled(true);
			String msg = ChatColor.stripColor(e.getMessage());
			if(msg.equalsIgnoreCase("y")) {
				pl.sendMessage(ChatColor.RED + "You have " + ChatColor.BOLD + "QUIT" + ChatColor.RED + " your guild.");
				String g_name = getGuild(pl.getName());
				leaveGuild(pl.getName());
				
				if(!guild_map.containsKey(g_name)) {
					guild_quit_confirm.remove(pl.getName());
					return;
				}
				
				for(String s : getGuildMembers(g_name)) {
					if(Bukkit.getPlayer(ChatColor.stripColor(s)) == null) {
						continue;
					}
					Player guildie = Bukkit.getPlayer(s);
					guildie.sendMessage(ChatColor.DARK_AQUA + "<" + ChatColor.BOLD + guild_handle_map.get(g_name) + ChatColor.DARK_AQUA + "> " + ChatColor.DARK_AQUA + pl.getName() + ChatColor.GRAY + " has " + ChatColor.UNDERLINE + "left" + ChatColor.GRAY + " the guild.");
				}
				
				String message_to_send = "[gquit]" + pl.getName() + "," + g_name;
				sendGuildMessageCrossServer(message_to_send);
				
				guild_quit_confirm.remove(pl.getName());
				return;
			} else {
				guild_quit_confirm.remove(pl.getName());
				pl.sendMessage(ChatColor.RED + "/gquit - " + ChatColor.BOLD + "CANCELLED");
				return;
			}
		}
		
		if(guild_creation.containsKey(pl.getName())) { // We're making a guild, woo!
			e.setCancelled(true);
			String msg = ChatColor.stripColor(e.getMessage());
			
			if(msg.equalsIgnoreCase("cancel")) {
				// Cancel guild creation.
				guild_creation.remove(pl.getName());
				guild_creation_npc_location.remove(pl.getName());
				guild_creation_data.remove(pl.getName());
				guild_creation_name_check.remove(pl.getName());
				pl.sendMessage(ChatColor.GRAY + "Guild Registrar: " + ChatColor.WHITE + "Goodbye!");
				return;
			}
			
			int step = guild_creation.get(pl.getName());
			
			pl.sendMessage(""); // Space break.
			
			if(step == 0) { // Y / N on guild creation
				if(msg.equalsIgnoreCase("y")) {
					if(!RealmMechanics.doTheyHaveEnoughMoney(pl, 5000)) {
						pl.sendMessage(ChatColor.GRAY + "Guild Registrar: " + ChatColor.WHITE + "You do not have enough GEM(s) -- 5,000, to create a guild.");
						guild_creation.remove(pl.getName());
						guild_creation_npc_location.remove(pl.getName());
						guild_creation_data.remove(pl.getName());
						guild_creation_name_check.remove(pl.getName());
						return;
					}
					if(inGuild(pl.getName())) {
						pl.sendMessage(ChatColor.GRAY + "Guild Registrar: " + ChatColor.WHITE + "You are already part of a guild. You'll need to /gquit before creating another.");
						guild_creation.remove(pl.getName());
						guild_creation_npc_location.remove(pl.getName());
						guild_creation_data.remove(pl.getName());
						guild_creation_name_check.remove(pl.getName());
						return;
					}
					pl.sendMessage(ChatColor.GRAY + "Guild Registrar: " + ChatColor.WHITE + "Ok, please enter your " + ChatColor.UNDERLINE + "formal guild name" + ChatColor.WHITE + ", this should be your FULL GUILD NAME, you will enter a shorter 'handle' later.");
					pl.sendMessage(ChatColor.GRAY + "You may type 'cancel' at any time to stop this guild creation.");
					guild_creation.put(pl.getName(), 1);
					return;
				} else {
					guild_creation.remove(pl.getName());
					guild_creation_npc_location.remove(pl.getName());
					guild_creation_data.remove(pl.getName());
					guild_creation_name_check.remove(pl.getName());
					pl.sendMessage(ChatColor.GRAY + "Guild Registrar: " + ChatColor.WHITE + "Goodbye!");
					return;
				}
			}
			
			if(step == 1) { // Guild name given, check for illegal types.
				String guild_name = e.getMessage();
				Pattern pattern = Pattern.compile("^([A-Za-z]|[0-9])+$");
				String guild_replace = guild_name;
				Matcher matcher = pattern.matcher(guild_replace.replace(" ", ""));
				if (!matcher.find()) {
				    pl.sendMessage(ChatColor.RED + "You guild name can only contain alphanumerical values.");
				    return;
				}
				if(guild_creation_name_check.containsKey(pl.getName())) {
					// SQL is going.
					if(guild_creation_name_check.get(pl.getName()) == 0) {
						// Still SQL querying.
						pl.sendMessage(ChatColor.GRAY + "Guild Registrar: " + ChatColor.WHITE + "Sorry, I'm not quite done checking my records yet. Come back in a few seconds.");
						return;
					} else if(guild_creation_name_check.get(pl.getName()) == -1) {
						// The name already exists!
						pl.sendMessage(ChatColor.GRAY + "Guild Registrar: " + ChatColor.WHITE + "I'm sorry, but a guild with the name '" + ChatColor.GRAY + guild_creation_data.get(pl.getName()).get(0) + ChatColor.WHITE + "' already exists. Please choose a different name.");
						guild_creation_name_check.remove(pl.getName());
						guild_creation_data.remove(pl.getName()); // No longer need name cached.
						return;
					} else if(guild_creation_name_check.get(pl.getName()) == 1) {
						// Woot!
						pl.sendMessage(ChatColor.GRAY + "Guild Registrar: " + ChatColor.WHITE + "Ok, your guild will be formally known as        " + ChatColor.DARK_AQUA + guild_creation_data.get(pl.getName()).get(0) + ChatColor.WHITE + ", now please enter a " + ChatColor.UNDERLINE + "guild prefix handle.");
						pl.sendMessage(ChatColor.GRAY.toString() + ChatColor.ITALIC + "This 'prefix handle' can be between 2-3 letters and will appear before all chat messages sent by guild members.");
						guild_creation.put(pl.getName(), 2);
						guild_creation_name_check.remove(pl.getName());
						return;
					}
				}
				
				if(ChatMechanics.hasBadWord(guild_name)) {
					pl.sendMessage(ChatColor.GRAY + "Guild Registrar: " + ChatColor.WHITE + "Your guild name has an illegal/censored word in it. Please enter an alternative name.");
					return;
				}
				
				if(guild_name.length() > 16) {
					pl.sendMessage(ChatColor.GRAY + "Guild Registrar: " + ChatColor.WHITE + "Your guild name exceeds the maximum length of 16 characters.");
					pl.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "You were " + (guild_name.length() - 16) + " characters over the limit.");
					return;
				}
				
				if(doesGuildExistLocal(guild_name)) {
					pl.sendMessage(ChatColor.GRAY + "Guild Registrar: " + ChatColor.WHITE + "I'm sorry, but a guild with the name '" + ChatColor.GRAY + guild_name + ChatColor.WHITE + "' already exists. Please choose a different name.");
					return;
				}
				
				guild_creation_name_check.put(pl.getName(), 0); // Check is NOT complete.
				pl.sendMessage(ChatColor.GRAY + "Guild Registrar: " + ChatColor.WHITE + "Before we move on, I'll need to check my records to ensure no other guilds on other shards already have the name '" + guild_name + "'. " + ChatColor.UNDERLINE + "Please talk to me again in a few seconds.");
				processGuildExistRequest(pl.getName(), guild_name);
				
				List<String> guild_data = new ArrayList<String>(Arrays.asList(guild_name));
				guild_creation_data.put(pl.getName(), guild_data);
			}
			
			if(step == 2) { // Guild handle given. [CCG]
				String guild_handle = e.getMessage();
				Pattern pattern = Pattern.compile("^([A-Za-z]|[0-9])+$");
				Matcher matcher = pattern.matcher(guild_handle);
				if (!matcher.find()) {
				    pl.sendMessage(ChatColor.RED + "Your guild tag can only contain alphanumerical values. Please try again");
				    return;
				}
				if((guild_creation_name_check.containsKey(pl.getName()))) {
					if(guild_creation_name_check.get(pl.getName()) == 0) {
						// Still SQL querying.
						pl.sendMessage(ChatColor.GRAY + "Guild Registrar: " + ChatColor.WHITE + "Sorry, I'm not quite done checking my records yet. Come back in a few seconds.");
						return;
					} else if(guild_creation_name_check.get(pl.getName()) == -1) {
						// The name already exists!
						pl.sendMessage(ChatColor.GRAY + "Guild Registrar: " + ChatColor.WHITE + "I'm sorry, but a guild with the tag '[" + ChatColor.GRAY + guild_creation_data.get(pl.getName()).get(1) + ChatColor.WHITE + "]' already exists. Please choose a different guild tag.");
						guild_creation_name_check.remove(pl.getName());
						
						List<String> guild_data = guild_creation_data.get(pl.getName());
						guild_data.remove(1);
						guild_creation_data.put(pl.getName(), guild_data); // No longer need handle cached.
						return;
						
					} else if(guild_creation_name_check.get(pl.getName()) == 1) {
						// Woot!
						guild_handle = guild_creation_data.get(pl.getName()).get(1);
						pl.sendMessage(ChatColor.GRAY + "Guild Registrar: " + ChatColor.WHITE + "Your guild handle to the masses of Andalucia shall be " + ChatColor.DARK_AQUA + "[" + ChatColor.GRAY + guild_handle + ChatColor.DARK_AQUA + "]" + ChatColor.WHITE + ". The next step is to choose your " + ChatColor.UNDERLINE + "guild's color" + ChatColor.WHITE + ". This color will be used for dyed guild armor and your ingame chat prefixes.");
						pl.sendMessage(ChatColor.GRAY.toString() + ChatColor.ITALIC + "Right click the Guild Registrar to open the color picking menu.");
						guild_creation.put(pl.getName(), 3);
						guild_creation_name_check.remove(pl.getName());
						return;
					}
				}
				
				if(ChatMechanics.hasBadWord(guild_handle) || guild_handle.contains(" ") || guild_handle.equalsIgnoreCase("gm") || guild_handle.equalsIgnoreCase("dev") || guild_handle.equalsIgnoreCase("OP")) {
					pl.sendMessage(ChatColor.GRAY + "Guild Registrar: " + ChatColor.WHITE + "Your guild handle has an illegal/censored word in it. Please enter an alternative handle -- no spaces allowed.");
					return;
				}
				
				if(guild_handle.length() > 3) {
					pl.sendMessage(ChatColor.GRAY + "Guild Registrar: " + ChatColor.WHITE + "Your guild name exceeds the maximum length of 3 characters.");
					return;
				}
				
				if(doesGuildHandlerExistLocal(guild_handle)) {
					pl.sendMessage(ChatColor.GRAY + "Guild Registrar: " + ChatColor.WHITE + "I'm sorry, but a guild with the tag '" + ChatColor.GRAY + guild_handle + ChatColor.WHITE + "' already exists. Please choose a different guild tag.");
					return;
				}
				
				/*pl.sendMessage(ChatColor.GRAY + "Guild Registrar: " + ChatColor.WHITE + "Your guild handle to the masses of Andalucia shall be " + ChatColor.DARK_AQUA + "[" + ChatColor.GRAY + guild_handle + ChatColor.DARK_AQUA + "]" + ChatColor.WHITE + ". The next step is to choose your " + ChatColor.UNDERLINE + "guild's color" + ChatColor.WHITE + ". This color will be used for dyed guild armor and your ingame chat prefixes.");
				pl.sendMessage(ChatColor.GRAY.toString() + ChatColor.ITALIC + "Right click the Guild Registrar to open the color picking menu.");
				guild_creation.put(pl.getName(), 3);*/
				guild_creation_name_check.put(pl.getName(), 0); // Check is NOT complete.
				pl.sendMessage(ChatColor.GRAY + "Guild Registrar: " + ChatColor.WHITE + "Once again, I'll need to check my records to ensure no other guilds on other shards already have the tag '" + guild_handle + "'. " + ChatColor.UNDERLINE + "Please speak to me again in a few seconds.");
				processGuildTagExistRequest(pl.getName(), guild_handle);
				
				List<String> guild_data = guild_creation_data.get(pl.getName());
				guild_data.add(guild_handle);
				guild_creation_data.put(pl.getName(), guild_data);
			}
			
			if(step == 3) {
				pl.sendMessage(ChatColor.GRAY.toString() + ChatColor.ITALIC + "Right click the Guild Registrar to open the color picking menu.");
				return;
			}
			
			if(step == 4) { // Colors given, summary given, check if they confirm creation & process.
				if(e.getMessage().equalsIgnoreCase(getUpgradeAuthenticationCode(pl.getName()))) {
					
					if(!RealmMechanics.doTheyHaveEnoughMoney(pl, 5000)) {
						pl.sendMessage(ChatColor.RED + "You do not have enough GEM(s) to create a guild.");
						pl.sendMessage(ChatColor.RED + "COST: 5,000 Gem(s)");
						return;
					}
					
					RealmMechanics.subtractMoney(pl, 5000);
					pl.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "-" + ChatColor.RED + 5000 + ChatColor.BOLD + "G");
					pl.sendMessage(ChatColor.GREEN + "Guild created.");
					
					List<String> guild_data = guild_creation_data.get(pl.getName());
					String guild_name = guild_data.get(0);
					String guild_handle = guild_data.get(1);
					Color c = Color.fromBGR(Integer.parseInt(guild_data.get(2)));
					
					guild_creation.remove(pl.getName());
					guild_creation_npc_location.remove(pl.getName());
					guild_creation_data.remove(pl.getName());
					guild_creation_name_check.remove(pl.getName());
					
					guild_map.put(guild_name, new ArrayList<String>(Arrays.asList(pl.getName() + ":3")));
					guild_colors.put(guild_name, c);
					guild_handle_map.put(guild_name, guild_handle);
					
					player_guilds.put(pl.getName(), guild_name);
					player_guild_rank.put(pl.getName(), 3); // 3 = founder
					
					pl.sendMessage("");
					pl.sendMessage(ChatColor.GRAY + "Guild Registrar: " + ChatColor.WHITE + "Congratulations, you are now the proud owner of the '" + guild_name + "' guild!");
					pl.sendMessage(ChatColor.GRAY + "Guild Registrar: " + ChatColor.WHITE + "Hold [TAB] on your keyboard to view your guild's dashboard. To dye your armor with your guild colors, see the " + ChatColor.UNDERLINE + "Item Vendor");
					pl.sendMessage(ChatColor.GRAY + "You can now chat in your guild chat with " + ChatColor.BOLD + "/g <msg>" + ChatColor.GRAY + ", invite players with " + ChatColor.BOLD + "/ginvite <player>" + ChatColor.GRAY + " and much more -- Check out your character journal for more information!");
					
					setupGuildTeam(guild_name);
					KarmaMechanics.sendAlignColor(pl, pl); // Update overhead name.
					createGuildSQL(guild_name, guild_handle, c.asBGR(), pl.getName());
					updateGuildTabList(pl);
				} else {
					if(e.getMessage().equalsIgnoreCase("cancel")) {
						guild_creation.remove(pl.getName());
						guild_creation_npc_location.remove(pl.getName());
						guild_creation_data.remove(pl.getName());
						guild_creation_name_check.remove(pl.getName());
						pl.sendMessage(ChatColor.GRAY + "Guild Registrar: " + ChatColor.WHITE + "Goodbye!");
						return;
					} else {
						// They typed code wrong.
						pl.sendMessage(ChatColor.GRAY + "Please enter either the confirmation code to create your guild for 5,000G, '" + ChatColor.DARK_AQUA + getUpgradeAuthenticationCode(pl.getName()) + ChatColor.GRAY + "'" + ChatColor.GRAY + " " + ChatColor.UNDERLINE + "OR" + ChatColor.GRAY + " Type '" + ChatColor.DARK_AQUA + "cancel" + ChatColor.GRAY + " to reset this entire process.");
						return;
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onPlayerInteractEntity(PlayerInteractEntityEvent e) {
		Player pl = e.getPlayer();
		if(e.getRightClicked() instanceof Player) {
			Player trader = (Player) e.getRightClicked();
			if(!(trader.hasMetadata("NPC"))) { return; } // Only NPC's matter.
			if(!(ChatColor.stripColor(trader.getName()).equalsIgnoreCase("Guild Registrar"))) { return; } // Only 'Trader' should do anything.
			e.setCancelled(true);
			
			pl.sendMessage("");
			// Now we'll check if they have a pending guild creation task and if so we'll just remind them of what to do.
			if(guild_creation.containsKey(pl.getName())) {
				int step = guild_creation.get(pl.getName());
				if(step == 0) {
					pl.sendMessage(ChatColor.GRAY + "Guild Registrar: " + ChatColor.WHITE + "Hello, " + ChatColor.UNDERLINE + pl.getName() + ChatColor.WHITE + ", I'm the guild registrar, would you like to create a guild today? Please note that it will cost 5,000 GEM(s). (" + ChatColor.GREEN.toString() + ChatColor.BOLD + "Y" + ChatColor.WHITE + " / " + ChatColor.RED.toString() + ChatColor.BOLD + "N" + ChatColor.WHITE + ")");
				}
				if(step == 1) {
					if(!(guild_creation_name_check.containsKey(pl.getName()))) {
						pl.sendMessage(ChatColor.GRAY + "Guild Registrar: " + ChatColor.WHITE + "Ok, please enter your " + ChatColor.UNDERLINE + "formal guild name" + ChatColor.WHITE + ", this should be your FULL GUILD NAME, you will enter a shorter 'handle' later.");
						pl.sendMessage(ChatColor.GRAY + "You may type 'cancel' at any time to stop this guild creation.");
						return;
					} else if(guild_creation_name_check.get(pl.getName()) == 0) {
						// Still SQL querying.
						pl.sendMessage(ChatColor.GRAY + "Guild Registrar: " + ChatColor.WHITE + "Sorry, I'm not quite done checking my records yet. Come back in a few seconds.");
						return;
					} else if(guild_creation_name_check.get(pl.getName()) == -1) {
						// The name already exists!
						pl.sendMessage(ChatColor.GRAY + "Guild Registrar: " + ChatColor.WHITE + "I'm sorry, but a guild with the name '" + ChatColor.GRAY + guild_creation_data.get(pl.getName()).get(0) + ChatColor.WHITE + "' already exists. Please choose a different name.");
						guild_creation_name_check.remove(pl.getName());
						guild_creation_data.remove(pl.getName()); // No longer need name cached.
						return;
						
					} else if(guild_creation_name_check.get(pl.getName()) == 1) {
						// Woot!
						pl.sendMessage(ChatColor.GRAY + "Guild Registrar: " + ChatColor.WHITE + "Ok, your guild will be formally known as " + ChatColor.DARK_AQUA + guild_creation_data.get(pl.getName()).get(0) + ChatColor.WHITE + ", now please enter a " + ChatColor.UNDERLINE + "guild prefix handle.");
						pl.sendMessage(ChatColor.GRAY.toString() + ChatColor.ITALIC + "This 'prefix handle' can be between 2-3 letters and will appear before all chat messages sent by guild members.");
						guild_creation.put(pl.getName(), 2);
						guild_creation_name_check.remove(pl.getName());
					}
				}
				if(step == 2) {
					if(!(guild_creation_name_check.containsKey(pl.getName()))) {
						if(guild_creation_data.containsKey(pl.getName())) {
							String guild_name = guild_creation_data.get(pl.getName()).get(0);
							pl.sendMessage(ChatColor.GRAY + "Guild Registrar: " + ChatColor.WHITE + "Ok, your guild will be formally known as " + ChatColor.DARK_AQUA + guild_name + ChatColor.WHITE + ", now please enter a guild prefix handle.");
							pl.sendMessage(ChatColor.GRAY + "This 'prefix handle' can be between 2-3 letters and will appear before all chat messages sent by guild members.");
						} else {
							// Something has gone wrong.
							pl.sendMessage(ChatColor.GRAY + "Guild Registrar: " + ChatColor.WHITE + "Glitch in the Mat-" + ChatColor.MAGIC + "rix... Something has gone wrong, scotty.");
							return;
						}
						return;
					} else if(guild_creation_name_check.get(pl.getName()) == 0) {
						// Still SQL querying.
						pl.sendMessage(ChatColor.GRAY + "Guild Registrar: " + ChatColor.WHITE + "Sorry, I'm not quite done checking my records yet. Come back in a few seconds.");
						return;
					} else if(guild_creation_name_check.get(pl.getName()) == -1) {
						// The name already exists!
						pl.sendMessage(ChatColor.GRAY + "Guild Registrar: " + ChatColor.WHITE + "I'm sorry, but a guild with the tag '[" + ChatColor.GRAY + guild_creation_data.get(pl.getName()).get(1) + ChatColor.WHITE + "]' already exists. Please choose a different guild tag.");
						guild_creation_name_check.remove(pl.getName());
						List<String> guild_data = guild_creation_data.get(pl.getName());
						guild_data.remove(1);
						guild_creation_data.put(pl.getName(), guild_data); // No longer need handle cached.
						return;
						
					} else if(guild_creation_name_check.get(pl.getName()) == 1) {
						// Woot!
						String guild_handle = guild_creation_data.get(pl.getName()).get(1);
						pl.sendMessage(ChatColor.GRAY + "Guild Registrar: " + ChatColor.WHITE + "Your guild handle to the masses of Andalucia shall be " + ChatColor.DARK_AQUA + "[" + ChatColor.GRAY + guild_handle + ChatColor.DARK_AQUA + "]" + ChatColor.WHITE + ". The next step is to choose your " + ChatColor.UNDERLINE + "guild's color" + ChatColor.WHITE + ". This color will be used for dyed guild armor and your ingame chat prefixes.");
						pl.sendMessage(ChatColor.GRAY.toString() + ChatColor.ITALIC + "Right click the Guild Registrar to open the color picking menu.");
						guild_creation.put(pl.getName(), 3);
						guild_creation_name_check.remove(pl.getName());
					}
				}
				if(step == 3) {
					// Open color picking menu.
					Inventory color_inv = Bukkit.createInventory(null, 45, "Guild Color Selector");
					int x = 0;
					while(x < 45) {
						color_inv.setItem(x, color_selector_divider);
						x++;
					}
					color_inv.setItem(17, new ItemStack(Material.LEATHER_HELMET));
					color_inv.setItem(26, new ItemStack(Material.LEATHER_CHESTPLATE));
					color_inv.setItem(35, new ItemStack(Material.LEATHER_LEGGINGS));
					color_inv.setItem(44, new ItemStack(Material.LEATHER_BOOTS));
					
					color_inv.setItem(27, new ItemStack(Material.INK_SACK, 1, (short) 0));
					color_inv.setItem(28, new ItemStack(Material.INK_SACK, 1, (short) 1));
					color_inv.setItem(29, new ItemStack(Material.INK_SACK, 1, (short) 2));
					color_inv.setItem(30, new ItemStack(Material.INK_SACK, 1, (short) 3));
					color_inv.setItem(31, new ItemStack(Material.INK_SACK, 1, (short) 4));
					color_inv.setItem(32, new ItemStack(Material.INK_SACK, 1, (short) 5));
					color_inv.setItem(33, new ItemStack(Material.INK_SACK, 1, (short) 6));
					color_inv.setItem(36, new ItemStack(Material.INK_SACK, 1, (short) 7));
					color_inv.setItem(37, new ItemStack(Material.INK_SACK, 1, (short) 8));
					color_inv.setItem(38, new ItemStack(Material.INK_SACK, 1, (short) 9));
					color_inv.setItem(39, new ItemStack(Material.INK_SACK, 1, (short) 10));
					color_inv.setItem(40, new ItemStack(Material.INK_SACK, 1, (short) 11));
					color_inv.setItem(41, new ItemStack(Material.INK_SACK, 1, (short) 12));
					color_inv.setItem(42, new ItemStack(Material.INK_SACK, 1, (short) 13));
					
					color_inv.setItem(18, new ItemStack(Material.INK_SACK, 1, (short) 14));
					color_inv.setItem(19, new ItemStack(Material.INK_SACK, 1, (short) 15));
					
					color_inv.setItem(3, new ItemStack(Material.AIR));
					color_inv.setItem(4, new ItemStack(Material.THIN_GLASS));
					color_inv.setItem(5, new ItemStack(Material.AIR));
					
					color_inv.setItem(0, CraftItemStack.asCraftCopy(gray_button));
					
					pl.openInventory(color_inv);
				}
				if(step == 4) {
					List<String> guild_data = guild_creation_data.get(pl.getName());
					String guild_name = guild_data.get(0);
					String guild_handle = guild_data.get(1);
					String color = guild_data.get(2);
					
					pl.sendMessage(ChatColor.GRAY + "              *** " + ChatColor.DARK_AQUA + ChatColor.BOLD + "Guild Creation Confirmation" + ChatColor.GRAY + " ***");
					pl.sendMessage(ChatColor.GRAY + "Guild Name: " + ChatColor.WHITE + guild_name);
					pl.sendMessage(ChatColor.GRAY + "Guild Handle: " + ChatColor.DARK_AQUA + "[" + ChatColor.GRAY + guild_handle + ChatColor.DARK_AQUA + "]");
					pl.sendMessage(ChatColor.GRAY + "Guild Color: " + ChatColor.WHITE + "" + color);
					pl.sendMessage(ChatColor.GRAY + "Cost: " + ChatColor.GREEN + "5,000G");
					pl.sendMessage("");
					pl.sendMessage(ChatColor.GRAY + "Enter the code " + ChatColor.DARK_AQUA + ChatColor.BOLD.toString() + getUpgradeAuthenticationCode(pl.getName()) + ChatColor.GRAY + " to confirm your guild creation.");
					pl.sendMessage("");
					pl.sendMessage("" + ChatColor.RED + ChatColor.BOLD + "WARNING:" + ChatColor.RED + " Guild creation is " + ChatColor.BOLD + ChatColor.RED + "NOT" + ChatColor.RED + " reversible or refundable. Type 'cancel' to void this entire purchase.");
					pl.sendMessage("");
				}
				
				return; // Do nothing else, they need to tell us something.
			}
			
			if(Utils.isBeta()){
				pl.sendMessage(ChatColor.RED + "You " + ChatColor.UNDERLINE + "cannot" + ChatColor.RED + " do this on the " + ChatColor.UNDERLINE + "Beta Servers" + ChatColor.RED + "!");
				return;
			}
			
			pl.sendMessage(ChatColor.GRAY + "Guild Registrar: " + ChatColor.WHITE + "Hello, " + ChatColor.UNDERLINE + pl.getName() + ChatColor.WHITE + ", I'm the guild registrar, would you like to create a guild today? Please note that it will cost 5,000 GEM(s). (" + ChatColor.GREEN.toString() + ChatColor.BOLD + "Y" + ChatColor.WHITE + " / " + ChatColor.RED.toString() + ChatColor.BOLD + "N" + ChatColor.WHITE + ")");
			guild_creation.put(pl.getName(), 0);
			guild_creation_npc_location.put(pl.getName(), trader.getLocation());
		}
	}
	
	@EventHandler
	public void onItemDrop(PlayerDropItemEvent e) {
		Player pl = e.getPlayer();
		if(pl.getOpenInventory().getTopInventory().getName().equalsIgnoreCase("Guild Color Selector")) {
			e.setCancelled(true);
			pl.updateInventory();
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onColorPicker(InventoryClickEvent e) {
		if(!(e.getInventory().getName().equalsIgnoreCase("Guild Color Selector")) && !e.getWhoClicked().getOpenInventory().getTopInventory().getName().equalsIgnoreCase("Guild Color Selector")) { return; }
		final Player pl = (Player) e.getWhoClicked();
		if(!(e.isLeftClick()) || e.isShiftClick()) {
			e.setCancelled(true);
			e.setResult(Result.DENY);
			pl.updateInventory();
			return;
		}
		
		if(e.getRawSlot() > 44) { // They're accessing their own inventory, no reason to.
			e.setCancelled(true);
			e.setResult(Result.DENY);
			pl.updateInventory();
			return;
		}
		
		if(e.getCurrentItem() != null && e.getCurrentItem().getType() == Material.PISTON_MOVING_PIECE) { // Blocker item.
			e.setCancelled(true);
			e.setResult(Result.DENY);
			pl.updateInventory();
			return;
		}
		
		if((e.getCurrentItem() != null && e.getCurrentItem().getType() != Material.INK_SACK && e.getCurrentItem().getType() != Material.AIR)) { // Trying to move anything but the dyes.
			e.setCancelled(true);
			e.setResult(Result.DENY);
			pl.updateInventory();
			return;
		}
		
		if((e.getRawSlot() == 3 || e.getRawSlot() == 5)) {
			// Update leather armor.
			ItemStack i_dye = null;
			if(e.getCursor().getType() == Material.INK_SACK) { // Placing a new dye in slots.
				e.setCancelled(true);
				
				i_dye = e.getCursor();
				if(e.getCurrentItem() != null && e.getCurrentItem().getType() != Material.AIR) {
					e.setCursor(e.getCurrentItem());
				} else {
					e.setCursor(new ItemStack(Material.AIR));
				}
				
				e.setCurrentItem(i_dye);
			} else if((e.getCursor() == null || e.getCursor().getType() == Material.AIR) && (e.getCurrentItem() != null && e.getCurrentItem().getType() == Material.INK_SACK)) {
				// Picking up a dye, AKA removing.
				e.setCancelled(true);
				
				e.setCursor(e.getCurrentItem());
				e.setCurrentItem(new ItemStack(Material.AIR));
			}
			
			Dye d1 = null;
			Dye d2 = null;
			
			if(i_dye != null) {
				d1 = new Dye(Material.INK_SACK, (byte) i_dye.getDurability());
			}
			
			if(e.getRawSlot() == 3) {
				if(e.getInventory().getItem(5) != null) {
					d2 = new Dye(Material.INK_SACK, (byte) e.getInventory().getItem(5).getDurability());
				}
			}
			if(e.getRawSlot() == 5) {
				if(e.getInventory().getItem(3) != null) {
					d2 = new Dye(Material.INK_SACK, (byte) e.getInventory().getItem(3).getDurability());
				}
			}
			
			Color c = null;
			if(d1 != null && d2 != null) {
				c = Color.WHITE.mixDyes(d1.getColor(), d2.getColor());
			} else {
				if(d1 != null) {
					c = d1.getColor().getColor();
				} else if(d2 != null) {
					c = d2.getColor().getColor();
				} else {
					c = DEFAULT_LEATHER_COLOR; // Default
				}
			}
			
			ItemStack helmet = new ItemStack(Material.LEATHER_HELMET);
			LeatherArmorMeta lam = (LeatherArmorMeta) helmet.getItemMeta();
			if(c != null) {
				lam.setColor(c);
				helmet.setItemMeta(lam);
			}
			
			ItemStack chest = new ItemStack(Material.LEATHER_CHESTPLATE);
			LeatherArmorMeta lam2 = (LeatherArmorMeta) chest.getItemMeta();
			if(c != null) {
				lam2.setColor(c);
				chest.setItemMeta(lam2);
			}
			
			ItemStack leggings = new ItemStack(Material.LEATHER_LEGGINGS);
			LeatherArmorMeta lam3 = (LeatherArmorMeta) leggings.getItemMeta();
			if(c != null) {
				lam3.setColor(c);
				leggings.setItemMeta(lam3);
			}
			
			ItemStack boots = new ItemStack(Material.LEATHER_BOOTS);
			LeatherArmorMeta lam4 = (LeatherArmorMeta) boots.getItemMeta();
			if(c != null) {
				lam4.setColor(c);
				boots.setItemMeta(lam4);
			}
			
			e.getInventory().setItem(17, helmet);
			e.getInventory().setItem(26, chest);
			e.getInventory().setItem(35, leggings);
			e.getInventory().setItem(44, boots);
			
			pl.updateInventory();
			return;
		}
		
		if(e.getRawSlot() == 0) { // Confirm button.
			// TODO: Confirm choices. Move to step 4.
			e.setCancelled(true);
			e.setResult(Result.DENY);
			
			Dye d1 = null;
			Dye d2 = null;
			
			if(e.getInventory().getItem(3) != null) {
				d1 = new Dye(Material.INK_SACK, (byte) e.getInventory().getItem(3).getDurability());
			}
			
			if(e.getInventory().getItem(4) != null) {
				d2 = new Dye(Material.INK_SACK, (byte) e.getInventory().getItem(4).getDurability());
			}
			
			Color c = null;
			if(d1 != null && d2 != null) {
				c = Color.WHITE.mixDyes(d1.getColor(), d2.getColor());
			} else {
				if(d1 != null) {
					c = d1.getColor().getColor();
				}
				if(d2 != null) {
					c = d2.getColor().getColor();
				}
			}
			
			if(c == null) { // No colors were selected, fail.
				pl.sendMessage(ChatColor.RED + "You must choose at least " + ChatColor.BOLD + "ONE" + ChatColor.RED + " primary guild color.");
				pl.updateInventory();
				return;
			}
			
			c = ((LeatherArmorMeta) e.getInventory().getItem(17).getItemMeta()).getColor();
			
			guild_creation.put(pl.getName(), 4);
			List<String> guild_data = guild_creation_data.get(pl.getName());
			guild_data.add("" + c.asBGR());
			guild_creation_data.put(pl.getName(), guild_data);
			
			final String guild_name = guild_data.get(0);
			final String guild_handle = guild_data.get(1);
			final int color_rgb = c.asRGB();
			
			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
				public void run() {
					pl.closeInventory();
					pl.sendMessage(ChatColor.GRAY + "Guild Registrar: " + ChatColor.WHITE + "Ok, thank you. Let me show you a quick summary of your guild.");
					pl.sendMessage("");
					
					pl.sendMessage(ChatColor.GRAY + "              *** " + ChatColor.DARK_AQUA + ChatColor.BOLD + "Guild Creation Confirmation" + ChatColor.GRAY + " ***");
					pl.sendMessage(ChatColor.GRAY + "Guild Name: " + ChatColor.WHITE + guild_name);
					pl.sendMessage(ChatColor.GRAY + "Guild Handle: " + ChatColor.DARK_AQUA + "[" + ChatColor.GRAY + guild_handle + ChatColor.DARK_AQUA + "]");
					pl.sendMessage(ChatColor.GRAY + "Guild Color: " + ChatColor.WHITE + "" + color_rgb);
					pl.sendMessage(ChatColor.GRAY + "Cost: " + ChatColor.DARK_AQUA + "5,000" + ChatColor.BOLD + "G");
					pl.sendMessage("");
					pl.sendMessage(ChatColor.GRAY + "Enter the code " + ChatColor.DARK_AQUA + ChatColor.BOLD.toString() + getUpgradeAuthenticationCode(pl.getName()) + ChatColor.GRAY + " to confirm your guild creation.");
					pl.sendMessage("");
					pl.sendMessage("" + ChatColor.RED + ChatColor.BOLD + "WARNING:" + ChatColor.RED + " Guild creation is " + ChatColor.BOLD + ChatColor.RED + "NOT" + ChatColor.RED + " reversible or refundable. Type 'cancel' to void this entire purchase.");
					pl.sendMessage("");
				}
			}, 2L);
		}
		
	}
	
	@EventHandler
	public void onColorPickerClose(InventoryCloseEvent e) {
		if(!(e.getInventory().getName().equalsIgnoreCase("Guild Color Selector"))) { return; }
		Player pl = (Player) e.getPlayer();
		pl.sendMessage(ChatColor.WHITE + "To use the color picker, simply " + ChatColor.UNDERLINE + "drag the two dyes you'd like to mix" + ChatColor.WHITE + " together to the empty slots on the top row of the inventory, view the result in the leather armor displayed, and then click the confirm button in the upper right.");
		pl.sendMessage(ChatColor.GRAY.toString() + ChatColor.ITALIC + "Right click the Guild Registrar to open the color picking menu.");
		// TODO: Remind them what to do.
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		Player pl = (Player) e.getWhoClicked();
		if(!(e.getInventory().getName().equalsIgnoreCase("container.crafting"))) { return; // Not their own inventory.
		}
		if(e.isShiftClick()) { return; }
		if(e.isLeftClick() && e.getCursor() != null && e.getCurrentItem() != null) {
			if(isGuildDye(e.getCursor())) {
				// We're using a dye, but on what?
				if(ItemMechanics.isArmor(e.getCurrentItem())) {
					if(!(RealmMechanics.isItemTradeable(e.getCurrentItem()))) { return; // Don't allow dye on already dyed armor.
					}
					if(!(inGuild(pl.getName()))) {
						//e.setCancelled(true);
						pl.sendMessage(ChatColor.RED + "You cannot dye your armor unless you are in an " + ChatColor.UNDERLINE + "active guild.");
						//pl.updateInventory();
						return;
					}
					// Ok, now we need to change the piece of armor to leather and set the dye color.
					
					ItemStack o_armor = e.getCurrentItem();
					double dur_percent = RepairMechanics.getPercentForDurabilityValue(o_armor, "armor");
					
					if(dur_percent <= 10) {
						pl.sendMessage(ChatColor.RED + "You " + ChatColor.UNDERLINE + "cannot" + ChatColor.RED + " dye such a damaged piece of armor -- repair it first.");
						return;
					}
					
					ItemStack n_armor = CraftItemStack.asCraftCopy(o_armor);
					if(n_armor.getType() == Material.CHAINMAIL_HELMET || n_armor.getType() == Material.IRON_HELMET || n_armor.getType() == Material.DIAMOND_HELMET || n_armor.getType() == Material.GOLD_HELMET) {
						n_armor.setType(Material.LEATHER_HELMET);
					}
					if(n_armor.getType() == Material.CHAINMAIL_CHESTPLATE || n_armor.getType() == Material.IRON_CHESTPLATE || n_armor.getType() == Material.DIAMOND_CHESTPLATE || n_armor.getType() == Material.GOLD_CHESTPLATE) {
						n_armor.setType(Material.LEATHER_CHESTPLATE);
					}
					if(n_armor.getType() == Material.CHAINMAIL_LEGGINGS || n_armor.getType() == Material.IRON_LEGGINGS || n_armor.getType() == Material.DIAMOND_LEGGINGS || n_armor.getType() == Material.GOLD_LEGGINGS) {
						n_armor.setType(Material.LEATHER_LEGGINGS);
					}
					if(n_armor.getType() == Material.CHAINMAIL_BOOTS || n_armor.getType() == Material.IRON_BOOTS || n_armor.getType() == Material.DIAMOND_BOOTS || n_armor.getType() == Material.GOLD_BOOTS) {
						n_armor.setType(Material.LEATHER_BOOTS);
					}
					
					LeatherArmorMeta lam = (LeatherArmorMeta) n_armor.getItemMeta();
					lam.setColor(getGuildColor(pl.getName()));
					n_armor.setItemMeta(lam);
					
					e.setCancelled(true);
					ItemStack dye = e.getCursor();
					if(dye.getAmount() > 1) {
						int new_amount = dye.getAmount() - 1;
						dye.setAmount(new_amount);
						e.setCursor(dye);
					} else if(dye.getAmount() == 1) {
						e.setCursor(new ItemStack(Material.AIR));
					}
					
					n_armor.setDurability((short) 0);
					RepairMechanics.setCustomDurability(n_armor, RepairMechanics.blocks_per_armor, "armor", true);
					
					n_armor = RealmMechanics.makeUntradeable(n_armor);
					RepairMechanics.subtractCustomDurability(pl, n_armor, (1500 - (1500 * (dur_percent / 100))), "armor", true);
					e.setCurrentItem(n_armor);
					
					pl.updateInventory();
					pl.sendMessage(ChatColor.YELLOW + "To undye your armor, " + ChatColor.UNDERLINE + "RIGHT CLICK" + ChatColor.YELLOW + " the item in your inventory.");
				}
			}
		}
		if(e.isRightClick() && e.getCurrentItem() != null && (e.getCursor() == null || e.getCursor().getType() == Material.AIR)) {
			
			if(ItemMechanics.isArmor(e.getCurrentItem())) {
				ItemStack armor = e.getCurrentItem();
				
				double dur_percent = RepairMechanics.getPercentForDurabilityValue(armor, "armor");
				//log.info("dur_percent=" + dur_percent);
				if(isColoredArmor(armor)) {
					// Remove the DYE.
					int armor_tier = ItemMechanics.getItemTier(armor);
					if(armor.getType() == Material.LEATHER_HELMET) {
						if(armor_tier == 1) {
							setColor(armor, DEFAULT_LEATHER_COLOR);
						}
						if(armor_tier == 2) {
							armor.setType(Material.CHAINMAIL_HELMET);
						}
						if(armor_tier == 3) {
							armor.setType(Material.IRON_HELMET);
						}
						if(armor_tier == 4) {
							armor.setType(Material.DIAMOND_HELMET);
						}
						if(armor_tier == 5) {
							armor.setType(Material.GOLD_HELMET);
						}
					}
					
					if(armor.getType() == Material.LEATHER_CHESTPLATE) {
						if(armor_tier == 1) {
							setColor(armor, DEFAULT_LEATHER_COLOR);
						}
						if(armor_tier == 2) {
							armor.setType(Material.CHAINMAIL_CHESTPLATE);
						}
						if(armor_tier == 3) {
							armor.setType(Material.IRON_CHESTPLATE);
						}
						if(armor_tier == 4) {
							armor.setType(Material.DIAMOND_CHESTPLATE);
						}
						if(armor_tier == 5) {
							armor.setType(Material.GOLD_CHESTPLATE);
						}
					}
					
					if(armor.getType() == Material.LEATHER_LEGGINGS) {
						if(armor_tier == 1) {
							setColor(armor, DEFAULT_LEATHER_COLOR);
						}
						if(armor_tier == 2) {
							armor.setType(Material.CHAINMAIL_LEGGINGS);
						}
						if(armor_tier == 3) {
							armor.setType(Material.IRON_LEGGINGS);
						}
						if(armor_tier == 4) {
							armor.setType(Material.DIAMOND_LEGGINGS);
						}
						if(armor_tier == 5) {
							armor.setType(Material.GOLD_LEGGINGS);
						}
					}
					
					if(armor.getType() == Material.LEATHER_BOOTS) {
						if(armor_tier == 1) {
							setColor(armor, DEFAULT_LEATHER_COLOR);
						}
						if(armor_tier == 2) {
							armor.setType(Material.CHAINMAIL_BOOTS);
						}
						if(armor_tier == 3) {
							armor.setType(Material.IRON_BOOTS);
						}
						if(armor_tier == 4) {
							armor.setType(Material.DIAMOND_BOOTS);
						}
						if(armor_tier == 5) {
							armor.setType(Material.GOLD_BOOTS);
						}
					}
					
					armor = makeTradeable(armor);
					armor.setDurability((short) 0);
					RepairMechanics.setCustomDurability(armor, RepairMechanics.blocks_per_armor, "armor", true);
					
					RepairMechanics.subtractCustomDurability(pl, armor, (1500.0D - (1500.0D * (dur_percent / 100.0D))), "armor", true);
					
					//RepairMechanics.setDurabilityValueForPercent(armor, dur_percent, "armor");
					if(armor.getType().name().toLowerCase().contains("leather")) {
						setColor(armor, DEFAULT_LEATHER_COLOR);
					}
					e.setCurrentItem(armor);
					e.setCursor(GuildMechanics.guild_dye);
					pl.updateInventory();
				}
				
			}
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onEntityDamageByEntityEvent(EntityDamageEvent e) {
		
		if(!(e instanceof EntityDamageByEntityEvent)) { return; // Don't care.
		}
		
		if(e.getCause() == DamageCause.ENTITY_ATTACK) {
			EntityDamageByEntityEvent edbee = (EntityDamageByEntityEvent) e;
			
			if(e.getEntity() instanceof Player && edbee.getDamager() instanceof Player) {
				Player attacker = (Player) edbee.getDamager();
				Player hurt = (Player) e.getEntity();
				
				if(areGuildies(attacker.getName(), hurt.getName())) {
					if(DuelMechanics.duel_map.containsKey(attacker.getName()) && DuelMechanics.duel_map.containsKey(hurt.getName())) { return; }
					e.setCancelled(true); // Friendly fire is OFF.
					e.setDamage(0);
					//hurt.getWorld().spawnParticle(hurt.getLocation().add(0, 1, 0), Particle.MAGIC_CRIT, 1F, 50);
				}
			}
		}
		
		if(e.getCause() == DamageCause.PROJECTILE) {
			EntityDamageByEntityEvent edbee = (EntityDamageByEntityEvent) e;
			
			if(e.getEntity() instanceof Player && edbee.getDamager() instanceof Arrow) {
				Arrow a = (Arrow) edbee.getDamager();
				if(a.getShooter() instanceof Player) {
					Player shooter = (Player) a.getShooter();
					Player hurt = (Player) e.getEntity();
					
					if(areGuildies(shooter.getName(), hurt.getName())) {
						if(DuelMechanics.duel_map.containsKey(shooter.getName()) && DuelMechanics.duel_map.containsKey(hurt.getName())) { return; }
						e.setCancelled(true); // Friendly fire is OFF.
						e.setDamage(0);
						//hurt.getWorld().spawnParticle(hurt.getLocation().add(0, 1, 0), Particle.MAGIC_CRIT, 1F, 50);
					}
				}
			}
		}
		
	}
	
	@EventHandler(priority = EventPriority.LOW)
	public void onEXPBottleThrow(PlayerInteractEvent e) {
		if(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if(e.hasItem() && isGuildDye(e.getItem())) {
				e.setCancelled(true);
				e.setUseInteractedBlock(Result.ALLOW);
				e.setUseItemInHand(Result.DENY);
			}
		}
	}
	
	public static ItemStack makeTradeable(ItemStack is) {
		ItemMeta im = is.getItemMeta();
		
		List<String> cur_lore = new ArrayList<String>();
		String r_s = "";
		
		if(im != null) {
			cur_lore = im.getLore();
		}
		if(cur_lore == null) {
			cur_lore = new ArrayList<String>();
		}
		
		if(cur_lore != null) {
			for(String s : cur_lore) {
				if(s.contains("Untradeable")) {
					r_s = s;
					break;
				}
			}
			
			cur_lore.remove(r_s);
		}
		
		im.setLore(cur_lore);
		
		is.setItemMeta(im);
		return is;
	}
	
	public static String getLocalServerName() {
		String motd = Bukkit.getMotd();
		String server_name = motd.substring(0, motd.indexOf(" "));
		return server_name;
	}
	
	public static List<String> getGuildOfficers(String g_name) {
		List<String> officers = new ArrayList<String>();
		
		for(String s : guild_map.get(g_name)) {
			String p_name = s.substring(0, s.indexOf(":"));
			int rank = Integer.parseInt(s.substring(s.indexOf(":") + 1, s.length()));
			if(rank == 2) {
				if(Bukkit.getPlayer(p_name) != null) {
					officers.add(ChatColor.GREEN.toString() + p_name);
				} else if(guild_member_server.containsKey(p_name)) {
					officers.add(ChatColor.YELLOW.toString() + guild_member_server.get(p_name) + " " + s);
				} else {
					officers.add(ChatColor.GRAY.toString() + p_name);
				}
			}
		}
		
		return officers;
	}
	
	public static String getGuildOwner(String g_name) {
		for(String s : guild_map.get(g_name)) {
			String p_name = s.substring(0, s.indexOf(":"));
			int rank = Integer.parseInt(s.substring(s.indexOf(":") + 1, s.length()));
			if(rank == 3) {
				if(Bukkit.getPlayer(p_name) != null) {
					return ChatColor.GREEN.toString() + p_name;
				} else if(guild_member_server.containsKey(p_name)) {
					return ChatColor.YELLOW.toString() + guild_member_server.get(p_name) + " " + p_name;
				} else {
					return ChatColor.GRAY.toString() + p_name;
				}
			}
		}
		
		return null;
	}
	
	public static List<String> getOnlineGuildMembers(String g_name) {
		List<String> mems = new ArrayList<String>();
		
		for(String s : getGuildMembers(g_name)) {
			if(Bukkit.getPlayer(s) != null) {
				mems.add(ChatColor.GREEN.toString() + s);
			} else if(guild_member_server.containsKey(s)) {
				mems.add(ChatColor.YELLOW.toString() + guild_member_server.get(s) + " " + s);
			} else {
				mems.add(ChatColor.GRAY.toString() + s);
			}
		}
		
		return mems;
	}
	
	public static int getOnlineGuildCount(String g_name) {
		int count = 0;
		for(String s : getOnlineGuildMembers(g_name)) {
			if(getGuildRank(ChatColor.stripColor(s), g_name) == 1 && (s.contains(ChatColor.YELLOW.toString()) || s.contains(ChatColor.GREEN.toString()))) {
				count++;
			}
		}
		return count;
	}
	
	public static int getGuildMemberCount(String g_name) {
		int count = 0;
		for(String s : getOnlineGuildMembers(g_name)) {
			if(s.contains(" ")) {
				// THey're on another server.
				s = s.substring(s.indexOf(" ") + 1, s.length());
			}
			if(getGuildRank(ChatColor.stripColor(s), g_name) == 1) {
				count++;
			}
		}
		return count;
	}
	
	public static int getOnlineOfficerCount(String g_name) {
		int count = 0;
		for(String s : getGuildOfficers(g_name)) {
			if(s.contains(ChatColor.YELLOW.toString()) || s.contains(ChatColor.GREEN.toString())) {
				count++;
			}
		}
		return count;
	}
	
	public static int getTotalOfficerCount(String g_name) {
		return getGuildOfficers(g_name).size();
	}
	
	/*public static int getAdjustedPlayerCount(){
		double real_player_count = (double)Bukkit.getOnlinePlayers().length;
		double fake_player_count = 0;
		
		if(real_player_count <= 20){
			fake_player_count = real_player_count;
		}
		else if(real_player_count > 20 && real_player_count < 108){
			fake_player_count = real_player_count + (real_player_count * 0.30);
		}
		else if(real_player_count >= 105 && real_player_count < 140){
			fake_player_count = real_player_count + (real_player_count * ((140 - real_player_count) * 0.30));
		}
		else if(real_player_count >= 140){
			fake_player_count = real_player_count;
		}
	return (int)fake_player_count;
	}*/
	
	public static void clearGuildTabList(Player pl, boolean update) {
		int x = 0;
		int y = 0;
		
		while(x < TabAPI.getVertSize()) {
			TabAPI.setTabString(Main.plugin, pl, x, y, "" + TabAPI.nextNull());
			x++;
		}
		y = 1;
		x = 0;
		while(x < TabAPI.getVertSize()) {
			TabAPI.setTabString(Main.plugin, pl, x, y, "" + TabAPI.nextNull());
			x++;
		}
		y = 2;
		x = 0;
		while(x < TabAPI.getVertSize()) {
			TabAPI.setTabString(Main.plugin, pl, x, y, "" + TabAPI.nextNull());
			x++;
		}
		
		if(update) {
			TabAPI.updatePlayer(pl);
		}
	}
	
	public static void updateGuildTabList(final Player pl) {
		if(pl == null) { return; }
		String g_name = getGuild(pl.getName());
		if(pl == null || !(inGuild(pl.getName())) || g_name == null) {
			TabAPI.setTabString(Main.plugin, pl, 0, 0, ChatColor.GRAY + "*------------");
			TabAPI.setTabString(Main.plugin, pl, 0, 1, ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "    Guild UI");
			TabAPI.setTabString(Main.plugin, pl, 0, 2, ChatColor.GRAY + "  ----------*");
			
			TabAPI.setTabString(Main.plugin, pl, 2, 1, ChatColor.DARK_AQUA + "" + "Guild Name");
			TabAPI.setTabString(Main.plugin, pl, 3, 1, ChatColor.GRAY + "N/A");
			
			TabAPI.setTabString(Main.plugin, pl, 17, 1, ChatColor.DARK_AQUA + "Shard " + ChatColor.GRAY.toString() + getLocalServerName(), 0);
			
			int online_players = Bukkit.getOnlinePlayers().length;
			int max_players = Bukkit.getMaxPlayers();
			
			online_players = (int) Math.round(online_players * 2.5D);
			
			if(online_players > max_players) {
				// So if  the spoofed amount is > the maximum, we're going to take away 5-15 of the online count so more players can join.
				online_players = max_players - (new Random().nextInt(15 - 5) + 5);
			}
			
			if(Bukkit.getOnlinePlayers().length + 10 >= max_players) {
				// Ok, now if the actual length+10 more is > maximum, we're full for real, so no more spoofing is needed.
				online_players = Bukkit.getOnlinePlayers().length;
			}
			
			if(Bukkit.getOnlinePlayers().length <= 5) {
				// Less than 5 people on, don't spoof. 5 -> 6 / 13
				online_players = Bukkit.getOnlinePlayers().length;
			}
			
			TabAPI.setTabString(Main.plugin, pl, 18, 1, ChatColor.GRAY.toString() + online_players + " / " + max_players, 0);
			TabAPI.updatePlayer(pl);
			
			return; // Not in a guild, yikes.
		}
		
		clearGuildTabList(pl, false);
		String guild_owner = getGuildOwner(g_name);
		
		TabAPI.setPriority(Main.plugin, pl, 1);
		
		TabAPI.setTabString(Main.plugin, pl, 0, 0, TabAPI.nextNull() + ChatColor.GRAY + "*-------------");
		TabAPI.setTabString(Main.plugin, pl, 0, 1, ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "    Guild UI");
		TabAPI.setTabString(Main.plugin, pl, 0, 2, ChatColor.GRAY + "  ---------*");
		
		TabAPI.setTabString(Main.plugin, pl, 2, 1, ChatColor.DARK_AQUA + "" + "Guild Name");
		TabAPI.setTabString(Main.plugin, pl, 3, 1, ChatColor.GRAY + g_name);
		
		TabAPI.setTabString(Main.plugin, pl, 5, 1, ChatColor.DARK_AQUA + "" + "Guild Owner", 0);
		
		if(guild_owner.contains(ChatColor.GREEN.toString()) || guild_owner.contains(ChatColor.YELLOW.toString())) {
			if(guild_owner.contains(ChatColor.YELLOW.toString())) {
				// Change to (gray) US-0 (green) Username
				guild_owner = ChatColor.stripColor(guild_owner);
				guild_owner.replaceAll(ChatColor.YELLOW.toString(), "");
				guild_owner = ChatColor.GRAY.toString() + guild_owner.substring(0, guild_owner.indexOf(" ")) + " " + ChatColor.GREEN.toString() + guild_owner.substring(guild_owner.indexOf(" ") + 1, guild_owner.length());
			}
			TabAPI.setTabString(Main.plugin, pl, 6, 1, guild_owner, 0);
		} else {
			TabAPI.setTabString(Main.plugin, pl, 6, 1, guild_owner, 9999999);
		}
		
		TabAPI.setTabString(Main.plugin, pl, 17, 1, ChatColor.DARK_AQUA + "Shard " + ChatColor.GRAY.toString() + getLocalServerName(), 0);
		
		int online_players = Bukkit.getOnlinePlayers().length;
		int max_players = Bukkit.getMaxPlayers();
		
		online_players = (int) Math.round(online_players * 2.5D);
		
		if(online_players > max_players) {
			// So if  the spoofed amount is > the maximum, we're going to take away 5-15 of the online count so more players can join.
			online_players = max_players - (new Random().nextInt(15 - 5) + 5);
		}
		
		if(Bukkit.getOnlinePlayers().length + 10 >= max_players) {
			// Ok, now if the actual length+10 more is > maximum, we're full for real, so no more spoofing is needed.
			online_players = Bukkit.getOnlinePlayers().length;
		}
		
		if(Bukkit.getOnlinePlayers().length <= 5) {
			// Less than 5 people on, don't spoof. 5 -> 6 / 13
			online_players = Bukkit.getOnlinePlayers().length;
		}
		
		TabAPI.setTabString(Main.plugin, pl, 18, 1, ChatColor.GRAY.toString() + online_players + " / " + max_players, 0);
		
		TabAPI.setTabString(Main.plugin, pl, 2, 0, ChatColor.DARK_AQUA + "" + "Officers [" + getOnlineOfficerCount(g_name) + "/" + getTotalOfficerCount(g_name) + "]", 0);
		
		TabAPI.setTabString(Main.plugin, pl, 2, 2, ChatColor.DARK_AQUA + "" + "Members [" + getOnlineGuildCount(g_name) + "/" + getGuildMemberCount(g_name) + "]", 0);
		
		List<String> members = getOnlineGuildMembers(g_name);
		List<String> pruned_members = new ArrayList<String>();
		List<String> to_remove_mem = new ArrayList<String>();
		// pruned_members = a list of 10 members, prioritizing online first.
		
		for(String s : members) {
			String s_copy = s;
			if(pruned_members.size() >= 16) {
				break;
			}
			if(s_copy.contains(" ")) {
				// THey're on another server.
				s_copy = s.substring(s.indexOf(" ") + 1, s.length());
			}
			if(getGuildRank(ChatColor.stripColor(s_copy), g_name) != 1) {
				continue;
			}
			if(s.contains(ChatColor.GREEN.toString())) {
				to_remove_mem.add(s);
				pruned_members.add(s);
			}
		}
		
		for(String s : to_remove_mem) {
			members.remove(s);
		}
		
		for(String s : members) {
			String s_copy = s;
			if(pruned_members.size() >= 16) {
				break;
			}
			if(s_copy.contains(" ")) {
				// THey're on another server.
				s_copy = s.substring(s.indexOf(" ") + 1, s.length());
			}
			if(getGuildRank(ChatColor.stripColor(s_copy), g_name) != 1) {
				continue;
			}
			if(s.contains(ChatColor.YELLOW.toString())) {
				to_remove_mem.add(s);
				pruned_members.add(s);
			}
		}
		
		for(String s : to_remove_mem) {
			members.remove(s);
		}
		
		if(pruned_members.size() < 16) {
			for(String s : members) {
				if(pruned_members.size() >= 16) {
					break;
				}
				if(getGuildRank(ChatColor.stripColor(s), g_name) != 1) {
					continue;
				}
				pruned_members.add(s);
			}
		}
		
		int x = 2;
		for(String s : pruned_members) {
			x++;
			if(x >= 20) {
				break;
			}
			if(s.contains(ChatColor.GREEN.toString()) || s.contains(ChatColor.YELLOW.toString())) {
				if(s.contains(ChatColor.YELLOW.toString())) {
					// Change to (gray) US-0 (green) Username
					s = ChatColor.stripColor(s);
					s.replaceAll(ChatColor.YELLOW.toString(), "");
					s = ChatColor.GRAY.toString() + s.substring(0, s.indexOf(" ")) + " " + ChatColor.GREEN.toString() + s.substring(s.indexOf(" ") + 1, s.length());
				}
				TabAPI.setTabString(Main.plugin, pl, x, 2, s, 50);
			} else {
				TabAPI.setTabString(Main.plugin, pl, x, 2, s, 9999999);
			}
		}
		
		if(pruned_members.size() == 0) {
			TabAPI.setTabString(Main.plugin, pl, 3, 2, ChatColor.GRAY.toString() + "N/A" + TabAPI.nextNull());
		}
		
		x = 2; // Reset X for parsing.
		List<String> officers = getGuildOfficers(g_name);
		List<String> pruned_officers = new ArrayList<String>();
		List<String> to_remove_office = new ArrayList<String>();
		
		for(String s : officers) {
			if(pruned_officers.size() > 16) {
				break;
			}
			if(s.contains(ChatColor.GREEN.toString())) {
				to_remove_office.add(s);
				pruned_officers.add(s);
			}
		}
		
		for(String s : to_remove_office) {
			officers.remove(s);
		}
		
		if(pruned_officers.size() < 16) {
			for(String s : officers) {
				if(pruned_officers.size() > 16) {
					break;
				}
				String s_copy = s;
				if(s_copy.contains(" ")) {
					// THey're on another server.
					s_copy = s.substring(s.indexOf(" ") + 1, s.length());
				}
				if(s.contains(ChatColor.YELLOW.toString())) {
					pruned_officers.add(s);
					to_remove_office.add(s);
				}
			}
		}
		
		for(String s : to_remove_office) {
			officers.remove(s);
		}
		
		if(pruned_officers.size() < 16) {
			for(String s : officers) {
				if(pruned_officers.size() > 16) {
					break;
				}
				pruned_officers.add(s);
			}
		}
		
		// pruned_officers = a list of 10 officers, prioritizing online first.
		for(String s : pruned_officers) {
			x++;
			if(x >= 20) {
				break;
			}
			if(s.contains(ChatColor.GREEN.toString()) || s.contains(ChatColor.YELLOW.toString())) {
				if(s.contains(ChatColor.YELLOW.toString())) {
					// Change to (gray) US-0 (green) Username
					s = ChatColor.stripColor(s);
					s.replaceAll(ChatColor.YELLOW.toString(), "");
					s = ChatColor.GRAY.toString() + s.substring(0, s.indexOf(" ")) + " " + ChatColor.GREEN.toString() + s.substring(s.indexOf(" ") + 1, s.length());
				}
				TabAPI.setTabString(Main.plugin, pl, x, 0, s, 50);
			} else {
				TabAPI.setTabString(Main.plugin, pl, x, 0, s, 9999999);
			}
		}
		
		if(pruned_officers.size() == 0) {
			// Yuck. No officers.
			TabAPI.setTabString(Main.plugin, pl, 3, 0, ChatColor.GRAY + "N/A" + TabAPI.nextNull());
		}
		
		TabAPI.updatePlayer(pl);
	}
	
	public static void setupGuildTeam(String g_name) {
		String fixed_gname = guild_handle_map.get(g_name);
		
		if((fixed_gname + ".default").length() > 16) {
			// Name is too long, let's cut off from g_name.
			// .default = 8
			fixed_gname = fixed_gname.substring(0, 8);
		}
		
		//team.setAllowFriendlyFire(false);
	}
	
	public static void setupGuildData(String p_name, String guild_name) {
		if(guild_name == null || guild_name.equalsIgnoreCase("") || guild_name.length() <= 0) { return; }
		
		if(guild_name.equalsIgnoreCase("null")) { return; }
		
		if(!(guild_map.containsKey(guild_name))) {
			// We need to download guild data as this is the first user to login from the guild.
			log.info("[GuildMechanics] DOWNLOADING guild data for guild: '" + guild_name + "'");
			if(downloadGuildDataSQL(guild_name, false) == false) {
				// Failed to download guild data, does not exist.
				log.info("[GuildMechanics] Guild " + guild_name + " does not exist, removing " + p_name + " from guild.");
				return;
			}
			setupGuildTeam(guild_name);
		}
		
		player_guilds.put(p_name, guild_name);
		player_guild_rank.put(p_name, getGuildRank(p_name));
	}
	
	public static String getPlayerGuildSQL(String p_name) {
		PreparedStatement pst = null;
		
		try {
			pst = ConnectionPool.getConnection().prepareStatement("SELECT guild_name FROM player_database WHERE p_name = '" + p_name + "'");
			
			pst.execute();
			ResultSet rs = pst.getResultSet();
			
			if(!(rs.next())) {
				log.info("[GuildMechanics] No guild data found for " + p_name + ", return null.");
				return null;
			}
			
			String guild_name = rs.getString("guild_name");
			if(guild_name == null || guild_name.equalsIgnoreCase("") || guild_name.length() <= 0) { return null; }
			return guild_name;
			
		} catch(SQLException ex) {
			log.log(Level.SEVERE, ex.getMessage(), ex);
			
		} finally {
			try {
				if(pst != null) {
					pst.close();
				}
				
			} catch(SQLException ex) {
				log.log(Level.WARNING, ex.getMessage(), ex);
			}
		}
		
		return null;
	}
	
	// Sends new guild data to SQL and sets the owner as a member.
	public void createGuildSQL(final String guild_name, final String guild_handle, final int guild_color, final String guild_owner) {
		
		Hive.sql_query.add("INSERT INTO guilds (guild_name, guild_handle, guild_color, members, guild_server_num) VALUES('" + guild_name + "', '" + guild_handle + "', '" + guild_color + "', '" + guild_owner + ":3," + "', '" + "-1" + "')");
		Hive.sql_query.add("INSERT INTO player_database (p_name, guild_name) VALUES('" + guild_owner + "', '" + guild_name + "') ON DUPLICATE KEY UPDATE guild_name='" + guild_name + "'");
		
		/*Thread t = new Thread(new Runnable() {
			public void run() {
				PreparedStatement pst = null;

				try {
					pst = ConnectionPool.getConnection().prepareStatement( 
							);

					pst.executeUpdate();

					pst = ConnectionPool.getConnection().prepareStatement( 
							);

					pst.executeUpdate();

				} catch (SQLException ex) {
					log.log(Level.SEVERE, ex.getMessage(), ex);  

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
		});	  

		t.start();*/
	}
	
	public static void processGuildTagExistRequest(final String p_name, final String g_tag) {
		Thread t = new Thread(new Runnable() {
			public void run() {
				PreparedStatement pst = null;
				
				try {
					pst = ConnectionPool.getConnection().prepareStatement("SELECT guild_handle FROM guilds WHERE guild_handle = '" + g_tag + "'");
					
					pst.execute();
					ResultSet rs = pst.getResultSet();
					
					if(!(rs.next())) {
						// The guild tag does not exist!
						if(guild_creation_name_check.containsKey(p_name)) {
							guild_creation_name_check.put(p_name, 1);
						}
						return;
					}
					
					String guild_name = rs.getString("guild_handle");
					if(guild_name != null) {
						// The guild tag exists.
						if(guild_creation_name_check.containsKey(p_name)) {
							guild_creation_name_check.put(p_name, -1);
						}
						return;
					}
					
				} catch(SQLException ex) {
					log.log(Level.SEVERE, ex.getMessage(), ex);
					
				} finally {
					try {
						if(pst != null) {
							pst.close();
						}
						
					} catch(SQLException ex) {
						log.log(Level.WARNING, ex.getMessage(), ex);
					}
				}
				
				if(guild_creation_name_check.containsKey(p_name)) {
					guild_creation_name_check.put(p_name, -1);
				}
				return; // Assume it exists if something goes wrong.
			}
		});
		
		t.start();
	}
	
	public static void processGuildExistRequest(final String p_name, final String g_name) {
		Thread t = new Thread(new Runnable() {
			public void run() {
				PreparedStatement pst = null;
				
				try {
					pst = ConnectionPool.getConnection().prepareStatement("SELECT guild_name FROM guilds WHERE guild_name = '" + g_name + "'");
					
					pst.execute();
					ResultSet rs = pst.getResultSet();
					
					if(!(rs.next())) {
						// The guild does not exist!
						if(guild_creation_name_check.containsKey(p_name)) {
							guild_creation_name_check.put(p_name, 1);
						}
						return;
					}
					
					String guild_name = rs.getString("guild_name");
					if(guild_name != null) {
						// The guild exists.
						if(guild_creation_name_check.containsKey(p_name)) {
							guild_creation_name_check.put(p_name, -1);
						}
						return;
					}
					
				} catch(SQLException ex) {
					log.log(Level.SEVERE, ex.getMessage(), ex);
					
				} finally {
					try {
						if(pst != null) {
							pst.close();
						}
						
					} catch(SQLException ex) {
						log.log(Level.WARNING, ex.getMessage(), ex);
					}
				}
				
				if(guild_creation_name_check.containsKey(p_name)) {
					guild_creation_name_check.put(p_name, -1);
				}
				return; // Assume it exists if something goes wrong.
			}
		});
		
		t.start();
	}
	
	public static void setMOTDSQL(final String g_name, final String motd) {
		
		String safe_motd = StringEscapeUtils.escapeSql(motd);
		
		if(!(safe_motd.startsWith("'"))) {
			safe_motd = "'" + safe_motd;
		}
		
		if(!(safe_motd.endsWith("'"))) {
			safe_motd = safe_motd + "'";
		}
		
		if(safe_motd.startsWith("''")) {
			safe_motd = safe_motd.substring(1, safe_motd.length());
		}
		
		if(safe_motd.endsWith("''")) {
			safe_motd = safe_motd.substring(0, safe_motd.length() - 1);
		}
		
		Hive.sql_query.add("INSERT INTO guilds (guild_name, motd) VALUES('" + g_name + "', " + safe_motd + ") ON DUPLICATE KEY UPDATE motd=" + "" + safe_motd + "");
		
		/*Thread t = new Thread(new Runnable() {
			public void run() {
				PreparedStatement pst = null;

				

				try {
					pst = ConnectionPool.getConnection().prepareStatement("INSERT INTO guilds (guild_name, motd) VALUES('" + g_name + "', " + safe_motd + ") ON DUPLICATE KEY UPDATE motd=" + "" + safe_motd + "");

					pst.executeUpdate();


				} catch (SQLException ex) {
					log.log(Level.SEVERE, ex.getMessage(), ex);  

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
		});	  

		t.start();*/
	}
	
	public static void setBIOSQL(final String g_name, final String bio) {
		
		String safe_bio = StringEscapeUtils.escapeSql(bio);
		
		if(!(safe_bio.startsWith("'"))) {
			safe_bio = "'" + safe_bio;
		}
		
		if(!(safe_bio.endsWith("'"))) {
			safe_bio = safe_bio + "'";
		}
		
		if(safe_bio.startsWith("''")) {
			safe_bio = safe_bio.substring(1, safe_bio.length());
		}
		
		if(safe_bio.endsWith("''")) {
			safe_bio = safe_bio.substring(0, safe_bio.length() - 1);
		}
		
		Hive.sql_query.add("INSERT INTO guilds (guild_name, biography) VALUES('" + g_name + "', " + safe_bio + ") ON DUPLICATE KEY UPDATE biography=" + "" + safe_bio + "");
		
		/*Thread t = new Thread(new Runnable() {
			public void run() {
				PreparedStatement pst = null;

				String safe_bio = StringEscapeUtils.escapeSql(bio);

				if(!(safe_bio.startsWith("'"))){
					safe_bio = "'" + safe_bio;
				}

				if(!(safe_bio.endsWith("'"))){
					safe_bio = safe_bio + "'";
				}

				if(safe_bio.startsWith("''")){
					safe_bio = safe_bio.substring(1, safe_bio.length());
				}

				if(safe_bio.endsWith("''")){
					safe_bio = safe_bio.substring(0, safe_bio.length() - 1);
				}

				try {
					pst = ConnectionPool.getConnection().prepareStatement( 
							"INSERT INTO guilds (guild_name, biography) VALUES('" + g_name + "', " + safe_bio + ") ON DUPLICATE KEY UPDATE biography=" + "" + safe_bio + "");

					pst.executeUpdate();


				} catch (SQLException ex) {
					log.log(Level.SEVERE, ex.getMessage(), ex);  

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
		});	  

		t.start();*/
	}
	
	public static void setGuildBannerSQL(final String g_name, final String banner_url) {
		
		String safe_banner_url = StringEscapeUtils.escapeSql(banner_url);
		
		if(!(safe_banner_url.startsWith("'"))) {
			safe_banner_url = "'" + safe_banner_url;
		}
		
		if(!(safe_banner_url.endsWith("'"))) {
			safe_banner_url = safe_banner_url + "'";
		}
		
		if(safe_banner_url.startsWith("''")) {
			safe_banner_url = safe_banner_url.substring(1, safe_banner_url.length());
		}
		
		if(safe_banner_url.endsWith("''")) {
			safe_banner_url = safe_banner_url.substring(0, safe_banner_url.length() - 1);
		}
		
		Hive.sql_query.add("INSERT INTO guilds (guild_name, guild_banner) VALUES('" + g_name + "', " + safe_banner_url + ") ON DUPLICATE KEY UPDATE guild_banner=" + "" + safe_banner_url + "");
		
		/*Thread t = new Thread(new Runnable() {
			public void run() {
				PreparedStatement pst = null;

				String safe_banner_url = StringEscapeUtils.escapeSql(banner_url);

				if(!(safe_banner_url.startsWith("'"))){
					safe_banner_url = "'" + safe_banner_url;
				}

				if(!(safe_banner_url.endsWith("'"))){
					safe_banner_url = safe_banner_url + "'";
				}

				if(safe_banner_url.startsWith("''")){
					safe_banner_url = safe_banner_url.substring(1, safe_banner_url.length());
				}

				if(safe_banner_url.endsWith("''")){
					safe_banner_url = safe_banner_url.substring(0, safe_banner_url.length() - 1);
				}

				try {
					pst = ConnectionPool.getConnection().prepareStatement( 
							"INSERT INTO guilds (guild_name, guild_banner) VALUES('" + g_name + "', " + safe_banner_url + ") ON DUPLICATE KEY UPDATE guild_banner=" + "" + safe_banner_url + "");

					pst.executeUpdate();


				} catch (SQLException ex) {
					log.log(Level.SEVERE, ex.getMessage(), ex);  

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
		});	  

		t.start();*/
	}
	
	public static void deleteGuildSQL(final String guild_name) {
		Thread t = new Thread(new Runnable() {
			public void run() {
				PreparedStatement pst = null;
				
				try {
					pst = ConnectionPool.getConnection().prepareStatement("DELETE FROM guilds WHERE guild_name='" + guild_name + "'");
					
					pst.executeUpdate();
					
					log.info("[GuildMechanics] Deleted guild: " + guild_name);
					
				} catch(SQLException ex) {
					log.log(Level.SEVERE, ex.getMessage(), ex);
					
				} finally {
					try {
						if(pst != null) {
							pst.close();
						}
						
					} catch(SQLException ex) {
						log.log(Level.WARNING, ex.getMessage(), ex);
					}
				}
			}
		});
		
		t.start();
	}
	
	public static void updateGuildSQL(final String guild_name) {
		String guild_handle = guild_handle_map.get(guild_name);
		int guild_color = guild_colors.get(guild_name).asBGR();
		int home_server = 1;
		
		List<String> local_members = guild_map.get(guild_name);
		String member_string = "";
		for(String s : local_members) {
			member_string += s + ",";
		}
		
		Hive.sql_query.add("INSERT INTO guilds (guild_name, guild_handle, guild_color, members, guild_server_num) VALUES('" + guild_name + "', '" + guild_handle + "', '" + guild_color + "', '" + member_string + "', '" + home_server + "')" + " ON DUPLICATE KEY UPDATE members='" + member_string + "', guild_server_num='" + home_server + "'");
		
		/*Thread t = new Thread(new Runnable() {
			public void run() {
				String guild_handle = guild_handle_map.get(guild_name);
				int guild_color = guild_colors.get(guild_name).asBGR();
				int home_server = 1;

				List<String> local_members = guild_map.get(guild_name);
				String member_string = "";
				for(String s : local_members){
					member_string += s + ",";
				}

				PreparedStatement pst = null;

				try {
					pst = ConnectionPool.getConnection().prepareStatement( 
							"INSERT INTO guilds (guild_name, guild_handle, guild_color, members, guild_server_num) VALUES('" + guild_name + "', '" + guild_handle + "', '" + guild_color + "', '" + member_string + "', '" + home_server + "')"
									+ " ON DUPLICATE KEY UPDATE members='" + member_string + "', guild_server_num='" + home_server + "'");

					pst.executeUpdate();

					log.info("[GuildMechanics] Updated SQL database for '" + guild_name + "' data.");

				} catch (SQLException ex) {
					log.log(Level.SEVERE, ex.getMessage(), ex);  

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
		});	  

		t.start();*/
	}
	
	public static void setPlayerGuildSQL(final String p_name, final String g_name, final boolean none) {
		
		if(none == true) {
			Hive.sql_query.add("INSERT INTO player_database (p_name, guild_name) VALUES('" + p_name + "', '') ON DUPLICATE KEY UPDATE guild_name = '" + null + "'");
		} else if(none == false) {
			Hive.sql_query.add("INSERT INTO player_database (p_name, guild_name) VALUES('" + p_name + "', '" + g_name + "') ON DUPLICATE KEY UPDATE guild_name = '" + g_name + "'");
		}
		
		/*Thread t = new Thread(new Runnable() {
			public void run() {
				PreparedStatement pst = null;

				try {	
					if(none == true){
						pst = ConnectionPool.getConnection().prepareStatement( 
								"INSERT INTO player_database (p_name, guild_name) VALUES('" + p_name + "', '') ON DUPLICATE KEY UPDATE guild_name = '" + null + "'");
					}
					else if(none == false){
						pst = ConnectionPool.getConnection().prepareStatement( 
								"INSERT INTO player_database (p_name, guild_name) VALUES('" + p_name + "', '" + g_name + "') ON DUPLICATE KEY UPDATE guild_name = '" + g_name + "'");
					}

					pst.executeUpdate();

				} catch (SQLException ex) {
					log.log(Level.SEVERE, ex.getMessage(), ex);  

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
		});	  

		t.start();*/
	}
	
	public static boolean downloadGuildDataSQL(String g_name, boolean temp) {
		PreparedStatement pst = null;
		
		try {
			pst = ConnectionPool.getConnection().prepareStatement("SELECT guild_handle, guild_color, guild_server_num, members, motd, biography FROM guilds WHERE guild_name = '" + g_name + "'");
			
			pst.execute();
			ResultSet rs = pst.getResultSet();
			
			if(!(rs.next())) {
				log.info("[GuildMechanics] No guild data found for GUILD: " + g_name + ", return null.");
				return false;
			}
			
			String guild_handle = rs.getString("guild_handle");
			Color c = Color.fromBGR(rs.getInt("guild_color"));
			int guild_server_num = rs.getInt("guild_server_num");
			String members = rs.getString("members");
			String motd = rs.getString("motd");
			String bio = rs.getString("biography");
			
			List<String> list_members = new ArrayList<String>();
			for(String s : members.split(",")) {
				if(s != null && s.length() > 0) {
					list_members.add(s);
				}
			}
			
			if(temp == false) {
				guild_map.put(g_name, list_members);
				guild_colors.put(g_name, c);
				guild_handle_map.put(g_name, guild_handle);
				guild_server.put(g_name, guild_server_num);
				guild_motd.put(g_name, motd);
				guild_bio.put(g_name, bio);
			} else if(temp == true) {
				guild_map_clone.put(g_name, list_members);
			}
			
			log.info("[GuildMechanics] Downloaded guild data for " + g_name + ".");
			
		} catch(SQLException ex) {
			log.log(Level.SEVERE, ex.getMessage(), ex);
			
		} finally {
			try {
				if(pst != null) {
					pst.close();
				}
				
			} catch(SQLException ex) {
				log.log(Level.WARNING, ex.getMessage(), ex);
			}
		}
		
		return true;
	}
	
	public static void setGuildRank(String p_name, int rank_num) {
		if(!(inGuild(p_name)) || !guild_map.containsKey((getGuild(p_name)))) { return; // Not in a guild.
		}
		
		List<String> guild_data = guild_map.get(getGuild(p_name));
		List<String> new_guild_data = new ArrayList<String>();
		
		for(String s : guild_data) {
			if(s.toLowerCase().startsWith(p_name.toLowerCase())) {
				// Inject new rank.
				s = s.substring(0, s.indexOf(":"));
				s = s + ":" + rank_num;
				log.info("[GuildMechanics] Set guild rank of " + p_name + " to: " + rank_num + "(" + s + ")");
			}
			new_guild_data.add(s);
		}
		
		player_guild_rank.put(p_name, rank_num);
		guild_map.put(getGuild(p_name), new_guild_data);
	}
	
	public static void leaveGuild(String p_name) {
		String g_name = player_guilds.get(p_name);
		int rank_num = getGuildRank(p_name);
		
		player_guilds.remove(p_name);
		guild_only.remove(p_name);
		player_guild_rank.remove(p_name);
		
		List<String> guild_members = guild_map.get(g_name);
		guild_members.remove(p_name + ":" + rank_num);
		guild_map.put(g_name, guild_members);
		
		if(rank_num == 3) {
			// Guild leader just quit. Disband guild. Local, Remote, SQL.
			for(String s : getGuildMembers(g_name)) {
				if(Bukkit.getPlayer(s) != null) {
					Player pl = Bukkit.getPlayer(s);
					pl.sendMessage("");
					pl.sendMessage(ChatColor.RED + "Your guild, '" + ChatColor.DARK_AQUA + g_name + ChatColor.RED + "', has been disbanded by your leader, " + p_name + ".");
				}
				leaveGuild(s, g_name, false);
			}
			
			String packet_data = "[gdisband]" + g_name;
			sendGuildMessageCrossServer(packet_data);
		}
		
		setPlayerGuildSQL(p_name, null, true);
		if(rank_num != 3) {
			// Rank_num 3, guild is deleted bellow.
			updateGuildSQL(g_name);
		}
		
		if(Bukkit.getPlayer(p_name) != null) {
			Player pl = Bukkit.getPlayer(p_name);
			KarmaMechanics.sendAlignColor(pl, pl);
			removeGuildColors(pl, pl.getInventory());
			clearGuildTabList(pl, true);
			updateGuildTabList(pl);
		}
		
		if(rank_num == 3) {
			// Delete local guild data.
			guild_map.remove(g_name);
			guild_handle_map.remove(g_name);
			guild_colors.remove(g_name);
			guild_server.remove(g_name);
			guild_motd.remove(g_name);
			guild_bio.remove(g_name);
			deleteGuildSQL(g_name);
		}
	}
	
	public static void leaveGuild(String p_name, String g_name, boolean update_sql) {
		player_guilds.remove(p_name);
		player_guild_rank.remove(p_name);
		guild_only.remove(p_name);
		
		List<String> guild_members = guild_map.get(g_name);
		String key_s = "";
		
		for(String s : guild_members) {
			if(s.substring(0, s.indexOf(":")).equalsIgnoreCase(p_name)) {
				key_s = s;
				break;
			}
		}
		
		if(key_s == "") { return; // Never found them in guild data.
		}
		
		guild_members.remove(key_s);
		guild_map.put(g_name, guild_members);
		
		setPlayerGuildSQL(p_name, null, true);
		
		if(update_sql == true) {
			updateGuildSQL(g_name);
		}
		
		if(Bukkit.getPlayer(p_name) != null) {
			Player pl = Bukkit.getPlayer(p_name);
			KarmaMechanics.sendAlignColor(pl, pl);
			removeGuildColors(pl, pl.getInventory());
			clearGuildTabList(pl, true);
			updateGuildTabList(pl);
		}
	}
	
	public static void addPlayerToGuild(String p_name, String g_name) {
		guild_invite.remove(p_name);
		guild_inviter.remove(p_name);
		guild_invite_time.remove(p_name);
		
		List<String> guild_members = guild_map.get(g_name);
		guild_members.add(p_name + ":1");
		guild_map.put(g_name, guild_members);
		player_guilds.put(p_name, g_name);
		player_guild_rank.put(p_name, 1); // member
		
		if(Bukkit.getPlayer(p_name) != null) {
			Player pl = Bukkit.getPlayer(p_name);
			KarmaMechanics.sendAlignColor(pl, pl);
			pl.performCommand("gmotd");
		}
	}
	
	public void guildMemberJoin(Player pl) {
		// Send message to guildies on all servers that they've logged in.
		String g_name = player_guilds.get(pl.getName());
		List<Player> local_online = new ArrayList<Player>();
		for(String s : getGuildMembers(g_name)) {
			if(Bukkit.getPlayer(s) != null) {
				// Online locally.
				local_online.add(Bukkit.getPlayer(s));
			}
		}
		
		for(Player guildie : local_online) {
			if(guildie.getName().equalsIgnoreCase(pl.getName())) {
				continue; // Same person.
			}
			if(CommunityMechanics.socialQuery(pl.getName(), guildie.getName(), "CHECK_BUD") && CommunityMechanics.socialQuery(guildie.getName(), pl.getName(), "CHECK_BUD")) {
				continue; // No need, they get buddy notification.
			}
			guildie.sendMessage(ChatColor.DARK_AQUA + "<" + ChatColor.BOLD + guild_handle_map.get(g_name) + ChatColor.DARK_AQUA + "> " + ChatColor.GRAY + pl.getName() + " has joined your server.");
		}
		
		String server_name = Bukkit.getMotd().substring(0, Bukkit.getMotd().indexOf(" "));
		
		final String packet_data = "[join]" + pl.getName() + "," + g_name + "@" + server_name;
		
		Thread t = new Thread(new Runnable() {
			public void run() {
				sendGuildMessageCrossServer(packet_data);
			}
		});
		
		t.start();
	}
	
	public void guildMemberQuit(Player pl) {
		// Send message to guildies on all servers that they've logged out.
		String g_name = player_guilds.get(pl.getName());
		List<Player> local_online = new ArrayList<Player>();
		for(String s : getGuildMembers(g_name)) {
			if(Bukkit.getPlayer(s) != null) {
				// Online locally.
				local_online.add(Bukkit.getPlayer(s));
			}
		}
		
		for(Player guildie : local_online) {
			if(guildie.getName().equalsIgnoreCase(pl.getName())) {
				continue; // Same person.
			}
			if(CommunityMechanics.socialQuery(pl.getName(), guildie.getName(), "CHECK_BUD") && CommunityMechanics.socialQuery(guildie.getName(), pl.getName(), "CHECK_BUD")) {
				continue; // No need, they get buddy notification.
			}
			guildie.sendMessage(ChatColor.DARK_AQUA + "<" + ChatColor.BOLD + guild_handle_map.get(g_name) + ChatColor.DARK_AQUA + "> " + ChatColor.GRAY + pl.getName() + " has logged out.");
		}
		
		String server_name = Bukkit.getMotd().substring(0, Bukkit.getMotd().indexOf(" "));
		
		final String packet_data = "[quit]" + pl.getName() + "," + g_name + "@" + server_name;
		
		Thread t = new Thread(new Runnable() {
			public void run() {
				sendGuildMessageCrossServer(packet_data);
			}
		});
		
		t.start();
	}
	
	public static void setLocalGuildMOTD(String g_name, String motd) {
		// Local only, it's what sockets will run.
		guild_motd.put(g_name, motd);
		
		for(String s : getGuildMembers(g_name)) {
			if(Bukkit.getPlayer(s) != null) {
				Player pl = Bukkit.getPlayer(s);
				pl.sendMessage(ChatColor.DARK_AQUA + "<" + ChatColor.BOLD + guild_handle_map.get(g_name) + ChatColor.DARK_AQUA + "> " + ChatColor.BOLD + "MOTD: " + ChatColor.DARK_AQUA + motd);
			}
		}
	}
	
	public static void setLocalGuildBIO(String g_name, String bio) {
		// Local only, it's what sockets will run.
		guild_bio.put(g_name, bio);
		
		for(String s : getGuildMembers(g_name)) {
			if(Bukkit.getPlayer(s) != null) {
				Player pl = Bukkit.getPlayer(s);
				pl.sendMessage(ChatColor.DARK_AQUA + "<" + ChatColor.BOLD + guild_handle_map.get(g_name) + ChatColor.DARK_AQUA + "> " + ChatColor.BOLD + "A new " + ChatColor.UNDERLINE + "guild biography" + ChatColor.DARK_AQUA + " has been written. Type /gbio to view it.");
			}
		}
	}
	
	public static void setGuildMOTD(String g_name, String motd) {
		// Locally, socket, sql.
		// Local.
		setLocalGuildMOTD(g_name, motd);
		
		// Socket.
		String socket_data = "[gmotd]" + g_name + "$" + motd + "$";
		sendGuildMessageCrossServer(socket_data);
		
		// SQL.
		setMOTDSQL(g_name, motd);
	}
	
	public static void setGuildBIO(String g_name, String bio) {
		// Locally, socket, sql.
		// Local.
		//setLocalGuildMOTD(g_name, motd);
		setLocalGuildBIO(g_name, bio);
		
		// Socket.
		String socket_data = "[gbio]" + g_name + "$" + bio + "$";
		sendGuildMessageCrossServer(socket_data);
		
		// SQL.
		//setMOTDSQL(g_name, motd);
		setBIOSQL(g_name, bio);
	}
	
	public static int getGuildRank(String p_name) {
		if(player_guild_rank.containsKey(p_name)) { return player_guild_rank.get(p_name); }
		
		List<String> member_list = guild_map.get(player_guilds.get(p_name));
		
		p_name = p_name.toLowerCase();
		for(String s : member_list) {
			if(s.toLowerCase().contains(p_name)) {
				int rank = Integer.parseInt(s.split(":")[1]);
				player_guild_rank.put(p_name, rank);
				return rank;
			}
		}
		
		return 0; // Not in guild.
	}
	
	public static int getGuildRank(String p_name, String g_name) {
		if(player_guild_rank.containsKey(p_name)) { return player_guild_rank.get(p_name); }
		
		List<String> member_list = guild_map.get(g_name);
		
		p_name = p_name.toLowerCase();
		for(String s : member_list) {
			if(s.toLowerCase().contains(p_name)) {
				int rank = Integer.parseInt(s.split(":")[1]);
				return rank;
			}
		}
		
		return 0; // Not in guild.
	}
	
	public boolean isColoredArmor(ItemStack is) {
		if(is.hasItemMeta() && is.getItemMeta() instanceof LeatherArmorMeta) {
			if(ItemMechanics.getItemTier(is) == 1) {
				// It's leather to start with, if it's just brown fuck it.
				//Color c = ((LeatherArmorMeta)is.getItemMeta()).getColor();
				//if(c == null || c == DEFAULT_LEATHER_COLOR){
				return false; // Normal leather.
				//}
			}
			return true;
		}
		return false;
	}
	
	public static String generateUpgradeAuthenticationCode(String p_name) {
		StringBuffer sb = new StringBuffer(4);
		for(int i = 0; i < 4; i++) {
			int ndx = (int) (Math.random() * ALPHA_NUM.length());
			sb.append(ALPHA_NUM.charAt(ndx));
		}
		
		return sb.toString();
	}
	
	public static String getUpgradeAuthenticationCode(String p_name) {
		if(guild_creation_code.containsKey(p_name)) {
			return guild_creation_code.get(p_name);
		} else {
			String s = generateUpgradeAuthenticationCode(p_name);
			guild_creation_code.put(p_name, s);
			return s;
		}
	}
	
	public boolean isGuildDye(ItemStack is) {
		if(is.getType() == Material.EXP_BOTTLE && is.hasItemMeta() && is.getItemMeta().hasDisplayName() && ChatColor.stripColor(is.getItemMeta().getDisplayName()).equalsIgnoreCase("Guild Armor Dye")) { return true; }
		return false;
	}
	
	public boolean doesGuildExistLocal(String guild_name) {
		if(guild_map.containsKey(guild_name)) { return true; }
		// Ok, what if it's just not loaded? 
		
		return false;
	}
	
	public boolean doesGuildHandlerExistLocal(String guild_name) {
		if(guild_handle_map.containsKey(guild_name)) { return true; }
		
		return false;
	}
	
	public static boolean inGuild(String p_name) {
		if(player_guilds.containsKey(p_name)) { return true; }
		return false;
	}
	
	public static String getGuild(String p_name) {
		if(!(inGuild(p_name))) { return null; }
		String guild_name = player_guilds.get(p_name);
		return guild_name;
	}
	
	public static String getGuildPrefix(String p_name) {
		// Returns nothing if not in guild.
		if(!(inGuild(p_name))) { return ""; }
		String g_name = getGuild(p_name);
		String prefix = "[" + guild_handle_map.get(g_name) + "] ";
		//prefix = ChatColor.DARK_AQUA + "[" + ChatColor.GRAY + prefix + ChatColor.DARK_AQUA + "]" + " " + ChatColor.RESET;
		return prefix;
	}
	
	public static boolean areGuildies(String p1, String p2) {
		if(!(inGuild(p1)) || !(inGuild(p2))) { return false;
		// One of them is not even in a guild.
		}
		
		String p1_guild = getGuild(p1);
		String p2_guild = getGuild(p2);
		
		if(p1_guild.equalsIgnoreCase(p2_guild)) { return true; }
		return false;
	}
	
	public static boolean inSpecificGuild(String p_name, String g_name) {
		if(!guild_map.containsKey(g_name)) { return false; }
		
		String[] members = getGuildMembers(g_name);
		for(String s : members) {
			if(s.equalsIgnoreCase(p_name)) { return true; }
		}
		
		return false;
	}
	
	public static String[] getGuildMembers(String g_name) {
		String all_members = "";
		for(String s : guild_map.get(g_name)) {
			String p_name = s.split(":")[0];
			all_members += p_name + ",";
		}
		
		return all_members.split(",");
	}
	
	public static int getRankNum(String p_name) {
		// 1 = member
		// 2 = officer
		// 3 = founder
		if(!(inGuild(p_name))) { return 0; }
		return getGuildRank(p_name);
	}
	
	public static boolean isGuildLeader(String p_name) {
		if(getRankNum(p_name) == 3) { return true; }
		return false;
	}
	
	public static boolean isGuildOfficer(String p_name) {
		if(getRankNum(p_name) == 2) { return true; }
		return false;
	}
	
	public Color getGuildColor(String p_name) {
		if(!(inGuild(p_name))) { return null; }
		return guild_colors.get(getGuild(p_name));
		// Returns NBT_INT value to be assigned to "color" for chestplate
	}
	
	public void setColor(ItemStack is, Color color) {
		LeatherArmorMeta lam = (LeatherArmorMeta) is.getItemMeta();
		lam.setColor(color);
		is.setItemMeta(lam);
	}
	
	public static void removeGuildColors(Player pl, Inventory inv) {
		for(ItemStack is : pl.getInventory().getArmorContents()) {
			if(is == null) {
				continue;
			}
			if(is.hasItemMeta() && is.getItemMeta() instanceof LeatherArmorMeta) {
				int tier = ItemMechanics.getItemTier(is);
				// Need to check if they're in another guild and if colors match/don't match.
				LeatherArmorMeta lam = (LeatherArmorMeta) is.getItemMeta();
				if(inGuild(pl.getName())) {
					try {
						Color g_color = guild_colors.get(pl.getName());
						if(lam.getColor().asBGR() == g_color.asBGR()) {
							// Color is the same, nothing to do here.
							continue;
						}
					} catch(NullPointerException npe) {
						continue;
					}
				}
				
				if(tier == 1) {
					// We just need to remove the color, done.
					lam.setColor(DEFAULT_LEATHER_COLOR);
					is.setItemMeta(lam);
				}
				
				double dur_percent = RepairMechanics.getPercentForDurabilityValue(is, "armor");
				
				if(is.getType() == Material.LEATHER_HELMET) {
					if(tier == 2) {
						is.setType(Material.CHAINMAIL_HELMET);
					}
					if(tier == 3) {
						is.setType(Material.IRON_HELMET);
					}
					if(tier == 4) {
						is.setType(Material.DIAMOND_HELMET);
					}
					if(tier == 5) {
						is.setType(Material.GOLD_HELMET);
					}
				}
				
				if(is.getType() == Material.LEATHER_CHESTPLATE) {
					if(tier == 2) {
						is.setType(Material.CHAINMAIL_CHESTPLATE);
					}
					if(tier == 3) {
						is.setType(Material.IRON_CHESTPLATE);
					}
					if(tier == 4) {
						is.setType(Material.DIAMOND_CHESTPLATE);
					}
					if(tier == 5) {
						is.setType(Material.GOLD_CHESTPLATE);
					}
				}
				
				if(is.getType() == Material.LEATHER_LEGGINGS) {
					if(tier == 2) {
						is.setType(Material.CHAINMAIL_LEGGINGS);
					}
					if(tier == 3) {
						is.setType(Material.IRON_LEGGINGS);
					}
					if(tier == 4) {
						is.setType(Material.DIAMOND_LEGGINGS);
					}
					if(tier == 5) {
						is.setType(Material.GOLD_LEGGINGS);
					}
				}
				
				if(is.getType() == Material.LEATHER_BOOTS) {
					if(tier == 2) {
						is.setType(Material.CHAINMAIL_BOOTS);
					}
					if(tier == 3) {
						is.setType(Material.IRON_BOOTS);
					}
					if(tier == 4) {
						is.setType(Material.DIAMOND_BOOTS);
					}
					if(tier == 5) {
						is.setType(Material.GOLD_BOOTS);
					}
				}
				
				if(is.getDurability() != (short) 0) {
					is.setDurability((short) 0);
					RepairMechanics.subtractCustomDurability(pl, is, (1500 - (1500 * (dur_percent / 100))), "armor", true);
				}
				makeTradeable(is);
			}
		}
		for(ItemStack is : inv.getContents()) {
			if(is == null) {
				continue;
			}
			if(is.hasItemMeta() && is.getItemMeta() instanceof LeatherArmorMeta) {
				int tier = ItemMechanics.getItemTier(is);
				// Need to check if they're in another guild and if colors match/don't match.
				LeatherArmorMeta lam = (LeatherArmorMeta) is.getItemMeta();
				if(inGuild(pl.getName())) {
					try {
						Color g_color = guild_colors.get(pl.getName());
						if(lam.getColor().asBGR() == g_color.asBGR()) {
							// Color is the same, nothing to do here.
							continue;
						}
					} catch(NullPointerException npe) {
						continue;
					}
				}
				
				if(tier == 1) {
					// We just need to remove the color, done.
					lam.setColor(DEFAULT_LEATHER_COLOR);
					is.setItemMeta(lam);
				}
				
				double dur_percent = RepairMechanics.getPercentForDurabilityValue(is, "armor");
				
				if(is.getType() == Material.LEATHER_HELMET) {
					if(tier == 2) {
						is.setType(Material.CHAINMAIL_HELMET);
					}
					if(tier == 3) {
						is.setType(Material.IRON_HELMET);
					}
					if(tier == 4) {
						is.setType(Material.DIAMOND_HELMET);
					}
					if(tier == 5) {
						is.setType(Material.GOLD_HELMET);
					}
				}
				
				if(is.getType() == Material.LEATHER_CHESTPLATE) {
					if(tier == 2) {
						is.setType(Material.CHAINMAIL_CHESTPLATE);
					}
					if(tier == 3) {
						is.setType(Material.IRON_CHESTPLATE);
					}
					if(tier == 4) {
						is.setType(Material.DIAMOND_CHESTPLATE);
					}
					if(tier == 5) {
						is.setType(Material.GOLD_CHESTPLATE);
					}
				}
				
				if(is.getType() == Material.LEATHER_LEGGINGS) {
					if(tier == 2) {
						is.setType(Material.CHAINMAIL_LEGGINGS);
					}
					if(tier == 3) {
						is.setType(Material.IRON_LEGGINGS);
					}
					if(tier == 4) {
						is.setType(Material.DIAMOND_LEGGINGS);
					}
					if(tier == 5) {
						is.setType(Material.GOLD_LEGGINGS);
					}
				}
				
				if(is.getType() == Material.LEATHER_BOOTS) {
					if(tier == 2) {
						is.setType(Material.CHAINMAIL_BOOTS);
					}
					if(tier == 3) {
						is.setType(Material.IRON_BOOTS);
					}
					if(tier == 4) {
						is.setType(Material.DIAMOND_BOOTS);
					}
					if(tier == 5) {
						is.setType(Material.GOLD_BOOTS);
					}
				}
				
				if(is.getDurability() != (short) 0) {
					is.setDurability((short) 0);
					RepairMechanics.subtractCustomDurability(pl, is, (1500 - (1500 * (dur_percent / 100))), "armor", true);
				}
				makeTradeable(is);
			}
		}
	}
	
	public static void promoteToOfficer(String s_to_promote, Player p_owner) {
		// We've already performed all necessary checks at this point.
		if(!(areGuildies(s_to_promote, p_owner.getName()))) {
			// Just incase they quit at an exact moment or something.
			p_owner.sendMessage(ChatColor.RED + s_to_promote + " is no longer in your guild.");
			return;
		}
		
		setGuildRank(s_to_promote, 2);
		player_guild_rank.put(s_to_promote, 2);
		
		// Tell the world!
		p_owner.sendMessage(ChatColor.DARK_AQUA + "You have " + ChatColor.UNDERLINE + "promoted" + ChatColor.DARK_AQUA + " " + s_to_promote + " to the rank of " + ChatColor.BOLD + "GUILD OFFICER" + ChatColor.GREEN + ".");
		if(Bukkit.getPlayer(s_to_promote) != null) {
			Player to_promote = Bukkit.getPlayer(s_to_promote);
			to_promote.sendMessage(ChatColor.DARK_AQUA + "You have been " + ChatColor.UNDERLINE + "promoted" + ChatColor.DARK_AQUA + " to the rank of " + ChatColor.BOLD + "GUILD OFFICER" + ChatColor.DARK_AQUA + " in " + getGuild(s_to_promote));
		}
		String g_name = getGuild(s_to_promote);
		
		for(String mem : getGuildMembers(getGuild(s_to_promote))) {
			// TODO: Cross-server congratulations.
			if(Bukkit.getPlayer(mem) != null) {
				Player p_mem = Bukkit.getPlayer(mem);
				p_mem.sendMessage(ChatColor.DARK_AQUA.toString() + "<" + ChatColor.BOLD + guild_handle_map.get(g_name) + ChatColor.DARK_AQUA + ">" + ChatColor.GREEN + " " + s_to_promote + " has been " + ChatColor.UNDERLINE + "promoted" + ChatColor.GREEN + " to the rank of " + ChatColor.BOLD + "GUILD OFFICER" + ChatColor.GREEN + ".");
			}
		}
		
		String message_to_send = "[gpromote]" + s_to_promote + "," + g_name + ":2_";
		sendGuildMessageCrossServer(message_to_send);
		
		// Now we need to update the guild data in SQL.
		updateGuildSQL(g_name);
	}
	
	public static void promoteToOwnerInSpecificGuild(Player sender, String user_to_set_owner, String guild_name) {
		if (PermissionMechanics.isGM(sender.getName()) || sender.isOp()) {
			if (guild_map.containsKey(guild_name)) {
				if (inGuild(user_to_set_owner) && getGuild(user_to_set_owner).equals(guild_name)) {
					setGuildRank(getGuildOwner(guild_name), 1);
					setGuildRank(user_to_set_owner, 3);
					
					sender.sendMessage(ChatColor.GREEN + "You set " + ChatColor.UNDERLINE + user_to_set_owner + ChatColor.GREEN + " as guild owner of the guild " + ChatColor.UNDERLINE + guild_name + ChatColor.GREEN + ".");
					for (String members : getGuildMembers(getGuild(user_to_set_owner))) {
						Player pl = Bukkit.getPlayer(members);
						if (pl != null) {
							pl.sendMessage(ChatColor.DARK_AQUA.toString() + "<" + ChatColor.BOLD + GuildMechanics.guild_handle_map.get(guild_name)
									+ ChatColor.DARK_AQUA + ">" + ChatColor.AQUA + " " + ChatColor.UNDERLINE + sender.getName() + ChatColor.GRAY + " has set " + ChatColor.AQUA  + "" + ChatColor.UNDERLINE + user_to_set_owner + ChatColor.GRAY + " as the " + ChatColor.BOLD + "LEADER" + ChatColor.GRAY + " of your guild.");
						}
						continue;
					}
					String message_to_send = "[gpromote]" + user_to_set_owner + "," + guild_name + ":3_" + sender.getName();

					sendGuildMessageCrossServer(message_to_send);
					updateGuildSQL(guild_name);
					Main.log.info("<" + GuildMechanics.guild_handle_map.get(guild_name) + "> " + sender.getName() + " set " + user_to_set_owner + " as leader of the guild " + guild_name);
					return;
				}
				sender.sendMessage(ChatColor.RED + "The user " + ChatColor.UNDERLINE + user_to_set_owner + ChatColor.RED + " that you're trying to set as leader of " + ChatColor.UNDERLINE + guild_name + ChatColor.RED + " is in a different guild! (" + ChatColor.UNDERLINE + getGuild(user_to_set_owner) + ChatColor.RED + ")"); 
				Main.log.info("<" + GuildMechanics.guild_handle_map.get(guild_name) + "> " + sender.getName() + " tried to set " + user_to_set_owner + " as leader of the guild " + guild_name);
				return;
			}
			sender.sendMessage(ChatColor.RED + "The guild " + ChatColor.UNDERLINE + guild_name + ChatColor.RED + " does not exist in our database.");
			Main.log.info("<" + GuildMechanics.guild_handle_map.get(guild_name) + "> " + sender.getName() + " tried to set " + user_to_set_owner + " as leader of the guild " + guild_name);
			return;
		}
		sender.sendMessage(ChatColor.RED + "You are not authorized to change other guilds' leaders. /gsetleader <PLAYER>");
		Main.log.info("<" + GuildMechanics.guild_handle_map.get(guild_name) + "> " + sender.getName() + " tried to set " + user_to_set_owner + " as leader of the guild " + guild_name);
	}
	
	public static void promoteToOwnerInOwnGuild(Player owner, String new_owner) {
		if (!inGuild(owner.getName())) {
			owner.sendMessage(ChatColor.RED + "You " + ChatColor.UNDERLINE + "aren't" + ChatColor.RED + " in a guild!");
			return;
		}
		if (getGuildOwner(getGuild(owner.getName())).equals(owner.getName())) {
			owner.sendMessage(ChatColor.RED + "You " + ChatColor.UNDERLINE + "aren't" + ChatColor.RED + " the owner of this guild!");
			return;
		}
		
		if (inGuild(new_owner) && getGuild(new_owner).equals(getGuild(owner.getName()))) {
			setGuildRank(owner.getName(), 2); // Let's not completely remove the original owner from power and leave him as officer.
			setGuildRank(new_owner, 3);
			
			owner.sendMessage(ChatColor.GREEN + "You promoted " + ChatColor.UNDERLINE + new_owner + ChatColor.GREEN + " to guild owner of your guild.");
			for (String members : getGuildMembers(getGuild(owner.getName()))) {
				Player pl = Bukkit.getPlayer(members);
				if (pl != null) {
					pl.sendMessage(ChatColor.DARK_AQUA + "<" + ChatColor.BOLD + GuildMechanics.guild_handle_map.get(getGuild(owner.getName()))
							+ ChatColor.DARK_AQUA + ">" + ChatColor.AQUA + " " + ChatColor.UNDERLINE + owner.getName() + ChatColor.GRAY + " has set " + ChatColor.AQUA  + "" + ChatColor.UNDERLINE + new_owner + ChatColor.GRAY + " as the " + ChatColor.BOLD + "LEADER" + ChatColor.GRAY + " of your guild.");
				}
				continue;
			}
			String message_to_send = "[gpromote]" + new_owner + "," + getGuild(owner.getName()) + ":3_" + owner.getName();
			
			sendGuildMessageCrossServer(message_to_send);
			updateGuildSQL(getGuild(new_owner));
		}
		owner.sendMessage(ChatColor.RED + "The user " + ChatColor.UNDERLINE + new_owner + ChatColor.RED + " is not in your guild.");
		Main.log.info("<" + GuildMechanics.guild_handle_map.get(getGuild(owner.getName())) + "> " + owner.getName() + " tried to set " + new_owner + " as leader of the guild " + getGuild(owner.getName()));
	}
	
	public static void demoteOfficer(String s_to_demote, Player p_owner) {
		// We've already performed all necessary checks at this point.
		if(!(areGuildies(s_to_demote, p_owner.getName()))) {
			// Just incase they quit at an exact moment or something.
			p_owner.sendMessage(ChatColor.RED + s_to_demote + " is no longer in your guild.");
			return;
		}
		
		setGuildRank(s_to_demote, 1);
		
		// Tell the world!
		p_owner.sendMessage(ChatColor.RED + "You have " + ChatColor.UNDERLINE + "demoted" + ChatColor.RED + s_to_demote + " to the rank of " + ChatColor.BOLD + "GUILD MEMBER.");
		if(Bukkit.getPlayer(s_to_demote) != null) {
			Bukkit.getPlayer(s_to_demote).sendMessage(ChatColor.RED + "You have been " + ChatColor.UNDERLINE + "demoted" + ChatColor.RED + "to the rank of " + ChatColor.BOLD + "GUILD MEMBER" + ChatColor.RED + " in " + getGuild(s_to_demote));
		}
		
		String g_name = getGuild(s_to_demote);
		
		for(String mem : getGuildMembers(getGuild(s_to_demote))) {
			if(Bukkit.getPlayer(mem) != null) {
				Player p_mem = Bukkit.getPlayer(mem);
				p_mem.sendMessage(ChatColor.DARK_AQUA.toString() + "<" + ChatColor.BOLD + guild_handle_map.get(g_name) + ChatColor.DARK_AQUA + ">" + ChatColor.RED + " " + s_to_demote + " has been " + ChatColor.UNDERLINE + "demoted" + ChatColor.RED + " to the rank of " + ChatColor.BOLD + "GUILD MEMBER.");
			}
		}
		
		String message_to_send = "[gdemote]" + s_to_demote + "," + g_name + ":1_";
		sendGuildMessageCrossServer(message_to_send);
		
		// Now we need to update the guild data in SQL.
		updateGuildSQL(g_name);
	}
	
	public static void inviteToGuild(Player to_invite, Player p_owner) {
		if(getRankNum(p_owner.getName()) < 2) {
			p_owner.sendMessage(ChatColor.RED.toString() + "You are NOT an officer / founder of the " + ChatColor.BOLD.toString() + player_guilds.get(p_owner.getName()) + " guild.");
			p_owner.sendMessage(ChatColor.GRAY.toString() + "Type " + ChatColor.BOLD.toString() + "/gquit" + ChatColor.GRAY + " to quit your current guild.");
			return;
		}
		
		if(player_guilds.containsKey(to_invite.getName())) {
			if(areGuildies(to_invite.getName(), p_owner.getName())) {
				p_owner.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + to_invite.getName() + ChatColor.RED + " is already in your guild.");
				p_owner.sendMessage(ChatColor.GRAY + "Type /gkick " + to_invite.getName() + " to kick them out.");
			} else {
				p_owner.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + to_invite.getName() + ChatColor.YELLOW + " is already in another guild.");
			}
			return;
		}
		if(guild_invite.containsKey(to_invite.getName())) {
			p_owner.sendMessage(ChatColor.RED + to_invite.getName() + " has a pending guild invite.");
			return;
		}
		
		to_invite.sendMessage("");
		to_invite.sendMessage(ChatColor.DARK_AQUA.toString() + ChatColor.BOLD + p_owner.getName() + ChatColor.GRAY + " has invited you to join their guild, " + ChatColor.DARK_AQUA + getGuild(p_owner.getName()) + ChatColor.GRAY + ". To accept, type " + ChatColor.DARK_AQUA.toString() + "/gaccept" + ChatColor.GRAY + " to decline, type " + ChatColor.DARK_AQUA.toString() + "/gdecline");
		to_invite.sendMessage("");
		p_owner.sendMessage(ChatColor.GRAY + "You have invited " + ChatColor.BOLD.toString() + ChatColor.DARK_AQUA + to_invite.getName() + ChatColor.GRAY + " to join your guild.");
		
		guild_invite.put(to_invite.getName(), getGuild(p_owner.getName()));
		guild_inviter.put(to_invite.getName(), p_owner.getName());
		guild_invite_time.put(to_invite.getName(), System.currentTimeMillis());
	}
	
	public static String nextSessionId() {
		return new BigInteger(130, random).toString(32);
	}
	
	public static void sendGuildMessageCrossServer(String packet_data) {
		// player_join_packet_data = [join]pname,gname
		// packet_data = &to_guild/from@US-0: packet_data contents here.
		// server_ip = IP.
		
		List<Object> query = new ArrayList<Object>();
		query.add(packet_data);
		query.add(null);
		query.add(true); // Always all servers.
		CommunityMechanics.social_query_list.put(nextSessionId(), query);
	}
	
	public static void sendMessageToGuild(Player sender, String raw_msg) {
		List<Player> to_send_local = new ArrayList<Player>();
		
		String g_name = player_guilds.get(sender.getName());
		
		for(String mem : getGuildMembers(player_guilds.get(sender.getName()))) {
			if(Bukkit.getPlayer(mem) != null && !(sender.getName().equalsIgnoreCase(mem))) {
				Player pmem = Bukkit.getPlayer(mem);
				if(pmem.getName().equalsIgnoreCase(mem)) {
					to_send_local.add(pmem);
				}
				// They're online locally.
			}
		}
		
		to_send_local.add(sender);
		
		for(Player pl : to_send_local) {
			ChatColor p_color = ChatMechanics.getPlayerColor(sender, pl);
			String prefix = ChatMechanics.getPlayerPrefix(sender.getName(), false);
			
			String personal_msg = raw_msg;
			if(ChatMechanics.hasAdultFilter(pl.getName())) {
				personal_msg = ChatMechanics.censorMessage(personal_msg);
			}
			
			if(personal_msg.endsWith(" ")) {
				personal_msg = personal_msg.substring(0, personal_msg.length() - 1);
			}
			
			personal_msg = ChatMechanics.fixCapsLock(personal_msg);
			
			pl.sendMessage(ChatColor.DARK_AQUA.toString() + "<" + ChatColor.BOLD + guild_handle_map.get(g_name) + ChatColor.DARK_AQUA + ">" + ChatColor.GRAY + " " + prefix + p_color + sender.getName() + ": " + ChatColor.GRAY + personal_msg);
		}
		
		// TODO: Send packet to all online servers, has servers check if any guildies are on to recieve the message.
		log.info("<G> " + sender.getName() + ": " + raw_msg);
		
		String local_server = Bukkit.getMotd().substring(0, Bukkit.getMotd().indexOf(" "));
		String message_to_send = "&" + g_name + "/" + sender.getName() + "@" + local_server + ":" + raw_msg;
		sendGuildMessageCrossServer(message_to_send);
	}
	
}
