package minecade.dungeonrealms.Hive.commands;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;

import minecade.dungeonrealms.Main;
import minecade.dungeonrealms.Hive.Hive;
import minecade.dungeonrealms.database.ConnectionPool;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandWipe implements CommandExecutor {
	
	@SuppressWarnings("resource")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(sender instanceof Player) {
			Player p = (Player) sender;
			if(!(p.isOp())) { return true; }
			if(args.length != 1) {
				p.sendMessage("Syntax. /wipe <player>");
				return true;
			}
			
			String p_name = args[0];
			
			if(Bukkit.getPlayer(p_name) != null) {
				Bukkit.getPlayer(p_name).kickPlayer("Your account is being reset.");
			}
			
			PreparedStatement pst = null;
			try {
				pst = ConnectionPool.getConnection().prepareStatement("DELETE FROM player_database where p_name='" + p_name + "'");
				pst.executeUpdate();
				pst = ConnectionPool.getConnection().prepareStatement("DELETE FROM bank_database where p_name='" + p_name + "'");
				pst.executeUpdate();
				pst = ConnectionPool.getConnection().prepareStatement("DELETE FROM shop_database where p_name='" + p_name + "'");
				pst.executeUpdate();
				pst = ConnectionPool.getConnection().prepareStatement("DELETE FROM statistics where pname='" + p_name + "'");
				pst.executeUpdate();
				pst = ConnectionPool.getConnection().prepareStatement("DELETE FROM statistics_old where pname='" + p_name + "'");
				pst.executeUpdate();
				pst = ConnectionPool.getConnection().prepareStatement("DELETE FROM perm_statistics where pname='" + p_name + "'");
				pst.executeUpdate();
			} catch(SQLException ex) {
				Hive.log.log(Level.SEVERE, ex.getMessage(), ex);
				
			} finally {
				try {
					if(pst != null) {
						pst.close();
					}
					
				} catch(SQLException ex) {
					Hive.log.log(Level.WARNING, ex.getMessage(), ex);
				}
			}
			
			p.sendMessage("Player '" + p_name + "' has been wiped from all databases.");
			return true;
		}
		
		if(args.length != 1) {
			Main.log.info("Syntax. /wipe <player>");
		}
		
		String p_name = args[0];
		
		PreparedStatement pst = null;
		try {
			pst = ConnectionPool.getConnection().prepareStatement("DELETE FROM player_database where p_name='" + p_name + "'");
			pst.executeUpdate();
			pst = ConnectionPool.getConnection().prepareStatement("DELETE FROM bank_database where p_name='" + p_name + "'");
			pst.executeUpdate();
			pst = ConnectionPool.getConnection().prepareStatement("DELETE FROM shop_database where p_name='" + p_name + "'");
			pst.executeUpdate();
			pst = ConnectionPool.getConnection().prepareStatement("DELETE FROM statistics where pname='" + p_name + "'");
			pst.executeUpdate();
			pst = ConnectionPool.getConnection().prepareStatement("DELETE FROM statistics_old where pname='" + p_name + "'");
			pst.executeUpdate();
			pst = ConnectionPool.getConnection().prepareStatement("DELETE FROM perm_statistics where pname='" + p_name + "'");
			pst.executeUpdate();
		} catch(SQLException ex) {
			Hive.log.log(Level.SEVERE, ex.getMessage(), ex);
			
		} finally {
			try {
				if(pst != null) {
					pst.close();
				}
				
			} catch(SQLException ex) {
				Hive.log.log(Level.WARNING, ex.getMessage(), ex);
			}
		}
		
		Main.log.info("Player '" + p_name + "' has been wiped from all databases.");
		return true;
	}
	
}