package me.vaqxine.CloudMechanics;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.firefang.ip2c.Country;
import net.firefang.ip2c.IP2Country;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

// TODO: ALTER TABLE player_database ADD COLUMN last_server TINYTEXT

public class CloudMechanics extends JavaPlugin implements org.bukkit.event.Listener {
	Logger log = Logger.getLogger("Minecraft");

	List<String> us_servers = new ArrayList<String>(Arrays.asList("US-1", "US-2"));
	List<String> eu_servers = new ArrayList<String>(Arrays.asList("EU-1"));
	
	CloudMechanics plugin = null;
	
	@SuppressWarnings("deprecation")
	public void onEnable() {
		plugin = this;
		getServer().getPluginManager().registerEvents(this, this);
		log.info("[CloudMechanics] has been enabled.");
	}

	public void onDisable() {
		log.info("[CloudMechanics] has been disabled.");
	}

	@EventHandler
	public void onPlayerJoing(PlayerJoinEvent e){
		final Player pl = e.getPlayer();
		this.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
			public void run() {
				pl.kickPlayer(ChatColor.RED.toString() + "Invalid game session."
						+ "\n" + ChatColor.GRAY + "Please connect again."
						+ "\n\n" + ChatColor.GRAY + ChatColor.ITALIC + "http://www.dungeonrealms.net/");
			}
		}, 2L);

	}

	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
		Player p = (Player)sender;
		if(!(p.isOp())){
			return true;
		}
		
		if(cmd.getName().equalsIgnoreCase("cloud")){
			ByteArrayOutputStream b = new ByteArrayOutputStream();
	        DataOutputStream out = new DataOutputStream(b);
	        try {
	          out.writeUTF("Connect");
	          out.writeUTF(args[0]);
	        } catch (IOException eee) {
	          Bukkit.getLogger().info("You'll never see me!");
	        }
	        
	        p.sendPluginMessage(this.plugin, "BungeeCord", b.toByteArray());
		}
	
		return true;
	}
}
