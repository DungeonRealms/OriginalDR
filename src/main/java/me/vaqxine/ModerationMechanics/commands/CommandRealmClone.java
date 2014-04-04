package me.vaqxine.ModerationMechanics.commands;

import java.io.File;

import me.vaqxine.RealmMechanics.RealmMechanics;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandRealmClone implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player p = null;
		if(sender instanceof Player) {
			p = (Player) sender;
		}
		
		if(p != null) {
			if(!(p.isOp())) { return true; }
		}
		
		if(args.length <= 0) {
			if(p != null) {
				p.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Invalid Syntax. " + ChatColor.RED + "/realmclone <PLAYER>");
			}
			return true;
		}
		
		final String p_name = args[0];
		final Player mod = p;
		
		if(!Bukkit.getMotd().contains("US-0")) {
			mod.sendMessage("What you thinking? Trying to use /realmclone on a public server? Go to US-0.");
			return true;
		}
		
		Bukkit.getServer().unloadWorld(Bukkit.getWorld(p.getName()), false);
		File world_root = new File(RealmMechanics.rootDir + "/" + p.getName());
		RealmMechanics.deleteFolder(world_root);
		
		mod.sendMessage(ChatColor.RED + "CLONING REALM OF " + p_name + " ....");
		//mod.sendMessage(ChatColor.RED + "YOU WILL NEED TO MANUALLY PLACE PORTAL ONCE THE DOWNLOAD IS COMPLETE.");
		Location portal_location = p.getLocation().add(0, 1, 0);
		p.getWorld().playEffect(portal_location, Effect.ENDER_SIGNAL, 20, 5);
		p.playSound(p.getLocation(), Sound.ENDERMAN_TELEPORT, 5F, 1.25F);
		RealmMechanics.has_portal.put(p.getName(), true);
		RealmMechanics.makePortal(p.getName(), portal_location.subtract(0, 2, 0), 60);
		
		Thread t = new Thread(new Runnable() {
			public void run() {
				RealmMechanics.realmHandler(mod, p_name);
			}
		});
		
		t.start();
		
		return true;
	}
	
}
