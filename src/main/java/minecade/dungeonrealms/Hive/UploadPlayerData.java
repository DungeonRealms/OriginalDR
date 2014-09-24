package minecade.dungeonrealms.Hive;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import minecade.dungeonrealms.Main;
import minecade.dungeonrealms.CommunityMechanics.CommunityMechanics;
import minecade.dungeonrealms.HealthMechanics.HealthMechanics;
import minecade.dungeonrealms.HearthstoneMechanics.HearthstoneMechanics;
import minecade.dungeonrealms.KarmaMechanics.KarmaMechanics;
import minecade.dungeonrealms.LevelMechanics.LevelMechanics;
import minecade.dungeonrealms.MoneyMechanics.MoneyMechanics;
import minecade.dungeonrealms.MountMechanics.MountMechanics;
import minecade.dungeonrealms.RealmMechanics.RealmMechanics;
import minecade.dungeonrealms.RecordMechanics.RecordMechanics;
import minecade.dungeonrealms.ShopMechanics.ShopMechanics;
import minecade.dungeonrealms.managers.PlayerManager;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.fusesource.jansi.Ansi;

public class UploadPlayerData extends Thread {
	
	String p_name = "";
	int g_attempts = 0;
	static int g_up_attempts = 0;
	
	public UploadPlayerData(String safe_pname) {
		p_name = safe_pname;
	}
	
	public void run() {
		
		while(Hive.local_ddos || Hive.hive_ddos) { // Don't even try to upload, there's no connectivity mate.
			try {
				Thread.sleep(5000);
			} catch(InterruptedException e) {}
			continue;
		}
		
		try {
			File data = new File(Hive.rootDir + "/" + Hive.main_world_name + "/players/" + p_name + ".dat");
			if(!(Hive.no_upload.contains(p_name))) { // Don't upload data if they're in the no_upload list, just set them offline.
			
				Hive.uploadPlayerDatabaseData(p_name);
				final int n_money = MoneyMechanics.bank_map.get(p_name) + RealmMechanics.getMoneyInInventory(Hive.player_inventory.get(p_name));
				final int n_pdeaths = RecordMechanics.player_deaths.get(p_name);
				final int n_unlawful_kills = RecordMechanics.player_kills.get(p_name).get(0);
				final int n_lawful_kills = RecordMechanics.player_kills.get(p_name).get(1);
				final int n_lmob_kills = RecordMechanics.mob_kills.get(p_name);
				final int n_duel_lose = RecordMechanics.duel_statistics.get(p_name).get(0);
				final int n_duel_win = RecordMechanics.duel_statistics.get(p_name).get(1);
				LevelMechanics.getPlayerData(p_name).saveData(true);
				RecordMechanics.updateStatisticData(p_name, n_money, n_pdeaths, n_unlawful_kills, n_lawful_kills, n_lmob_kills, n_duel_win, n_duel_lose);
				
				Hive.player_inventory.remove(p_name);
				Hive.player_location.remove(p_name);
				Hive.player_hp.remove(p_name);
				Hive.player_level.remove(p_name);
				Hive.player_food_level.remove(p_name);
				Hive.player_armor_contents.remove(p_name);
				Hive.player_ecash.remove(p_name);
				Hive.player_ip.remove(p_name);
				Hive.player_portal_shards.remove(p_name);
				Hive.player_first_login.remove(p_name);
				
				Hive.local_player_ip.remove(p_name);
				Hive.remote_player_data.remove(p_name);
				Hive.last_sync.remove(p_name);
				Hive.forum_usergroup.remove(p_name);
				Hive.login_time.remove(p_name);
				
				MoneyMechanics.uploadBankDatabaseData(p_name, true);
				ShopMechanics.uploadShopDatabaseData(p_name, true);
				HearthstoneMechanics.saveData(p_name);
				if(RealmMechanics.isWorldLoaded(p_name)) {
					RealmMechanics.uploadWorld(p_name, p_name);
				}
				
				KarmaMechanics.align_map.remove(p_name);
				KarmaMechanics.align_time.remove(p_name);
				
				PlayerManager.getPlayerModel(p_name).setIgnoreList(new ArrayList<String>());
				PlayerManager.getPlayerModel(p_name).setBuddyList(new ArrayList<String>());
				
				HealthMechanics.noob_player_warning.remove(p_name);
				HealthMechanics.noob_players.remove(p_name);
				
				RealmMechanics.realm_title.remove(p_name);
				//RealmMechanics.realm_tier.remove(p_name);
				
				MountMechanics.mule_inventory.remove(p_name);
				MountMechanics.mule_itemlist_string.remove(p_name);
			} else if(Hive.no_upload.contains(p_name)) {
				// Just delete some local data.
				Hive.player_inventory.remove(p_name);
				Hive.player_location.remove(p_name);
				Hive.player_hp.remove(p_name);
				Hive.player_level.remove(p_name);
				Hive.player_food_level.remove(p_name);
				Hive.player_armor_contents.remove(p_name);
				Hive.player_ecash.remove(p_name);
				KarmaMechanics.align_map.remove(p_name);
				KarmaMechanics.align_time.remove(p_name);
				PlayerManager.getPlayerModel(p_name).setIgnoreList(new ArrayList<String>());
				PlayerManager.getPlayerModel(p_name).setBuddyList(new ArrayList<String>());
				PlayerManager.getPlayerModel(p_name).setToggleList(new ArrayList<String>());
				HealthMechanics.noob_player_warning.remove(p_name);
				HealthMechanics.noob_players.remove(p_name);
				RealmMechanics.realm_title.remove(p_name);
				//RealmMechanics.realm_tier.remove(p_name);
				MountMechanics.mule_inventory.remove(p_name);
				MountMechanics.mule_itemlist_string.remove(p_name);
			}
			
			if(Hive.server_swap.containsKey(p_name) && Main.plugin.getServer().getPlayer(p_name) != null) {
				Player pl = Main.plugin.getServer().getPlayer(p_name);
				String server_prefix = Hive.server_swap.get(p_name);
				
				Hive.setPlayerOffline(p_name, 0, true); // Instant set offline!
				
				List<Object> qdata = new ArrayList<Object>();
				qdata.add("@server_num@" + p_name + ":" + Hive.getServerNumFromPrefix(server_prefix));
				qdata.add(null);
				qdata.add(true);
				CommunityMechanics.social_query_list.put(p_name, qdata);
				
				Thread.sleep(50);
				//CommunityMechanics.sendPacketCrossServer("@server_num@" + p_name + ":" + Hive.getServerNumFromPrefix(server_prefix), -1, true);
				PlayerManager.getPlayerModel(p_name).setServerNum(Hive.getServerNumFromPrefix(server_prefix));
				Thread.sleep(50);
				
				ByteArrayOutputStream b = new ByteArrayOutputStream();
				DataOutputStream out = new DataOutputStream(b);
				try {
					out.writeUTF("Connect");
					out.writeUTF(server_prefix);
				} catch(IOException eee) {
					Bukkit.getLogger().info("You'll never see me!");
				}
				
				pl.sendPluginMessage(Main.plugin, "BungeeCord", b.toByteArray());
				//CommunityMechanics.sendPacketCrossServer("@server_num@" + p_name + ":" + -1, -1, true);
				// Tells players the user has left the old server.
			}
			
			if(!(Hive.server_swap.containsKey(p_name))) {
				data.delete();
			}
			Hive.lockout_players.remove(p_name);
			Hive.no_upload.remove(p_name);
			Hive.loaded_players.remove(p_name); // no longer loaded hehe.
			
			if(!Hive.player_to_npc.containsKey(p_name) && !(Hive.server_swap.containsKey(p_name))) { // && !(HealthMechanics.in_combat.containsKey(p_name))){
				// They don't have an NPC spawned, so let them be logged out ASAP
				// If they have an NPC spawned, this will be handled by the async timer.
				Hive.setPlayerOffline(p_name, 1);
			}
			
			Hive.pending_upload.remove(p_name);
			RealmMechanics.player_god_mode.remove(p_name);
			Hive.log.info(Ansi.ansi().fg(Ansi.Color.YELLOW).boldOff().toString() + "[HIVE (SLAVE Edition)] Player data for " + p_name + " uploaded (quit event)." + Ansi.ansi().fg(Ansi.Color.WHITE).boldOff().toString());
		} catch(Exception e) {
			e.printStackTrace();
			if(e instanceof FileNotFoundException) {
				Hive.log.info("[HIVE (SLAVE Edition)] Failed to upload player data for " + p_name + " due to the player data not being present.");
				return; // We can't do anything, the file is long gone.
			}
			if(e instanceof NullPointerException) {
				// Some data not present, skip upload.
				Hive.log.info("[HIVE (SLAVE Edition)] Failed to upload player data for " + p_name + " due to the player data not being present.");
				return; // We can't do anything, the data is long gone.
			}
			
			try {
				// Something went wrong, meaning we could have desync problem. Let's lock them out from all servers and try again.
				if(!(Hive.lockout_players.contains(p_name))) {
					Hive.lockout_players.add(p_name);
				}
				try {
					Thread.sleep(1000);
				} catch(InterruptedException e1) {}
				g_attempts++;
				if(g_attempts > 5 && !Hive.local_ddos && !Hive.hive_ddos && Hive.isHiveOnline()) {
					Hive.log.info("Failed to upload data for player " + p_name + " 5 times, and the HIVE is reported as online, SKIPPING...");
					Hive.setPlayerOffline(p_name, 1);
					Hive.pending_upload.remove(p_name);
					return;
				}
				this.run(); // Rerun the method and try again.
				return;
			} catch(Exception e2) {
				e2.printStackTrace();
				Hive.log.info("Something SERIOUSLY FUCKED up on player data upload for " + p_name);
				return;
			}
			
		}
	}
	
}
