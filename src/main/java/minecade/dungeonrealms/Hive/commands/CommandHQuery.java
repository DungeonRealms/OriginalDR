package minecade.dungeonrealms.Hive.commands;

import minecade.dungeonrealms.CommunityMechanics.CommunityMechanics;
import minecade.dungeonrealms.Hive.Hive;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CommandHQuery implements CommandExecutor {
	
	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(sender instanceof Player) {
			Player p = (Player) sender;
			if(!(p.isOp())) { return true; }
			if(args.length == 2) {
				CommunityMechanics.sendPacketCrossServer("[toggleshard]", -1, true);
				p.sendMessage(ChatColor.RED + "Send toggleshard to all servers.");
			}
			if(args.length == 1) {
				ItemStack helm = new ItemStack(Material.getMaterial(Integer.parseInt(args[0])), 1);
				p.getInventory().setHelmet(helm);
			}
			if(args.length == 3) {
				Hive.restoreCorruptShops(true);
				// Wipes all IP banned players.
				/*PreparedStatement pst = null;
				try{
					pst = ConnectionPool.getConneciton().prepareStatement( 
							"SELECT pname FROM ban_list WHERE ip IS NOT NULL && pname IS NOT NULL");

					pst.execute();
					ResultSet rs = pst.getResultSet();

					while(rs.next()){
						String p_name = rs.getString("pname");
						try{
							pst = ConnectionPool.getConneciton().prepareStatement("DELETE FROM player_database where p_name='" + p_name + "'");
							pst.executeUpdate();
							pst = ConnectionPool.getConneciton().prepareStatement("DELETE FROM bank_database where p_name='" + p_name + "'");
							pst.executeUpdate();
							pst = ConnectionPool.getConneciton().prepareStatement("DELETE FROM shop_database where p_name='" + p_name + "'");
							pst.executeUpdate();
							pst = ConnectionPool.getConneciton().prepareStatement("DELETE FROM statistics where pname='" + p_name + "'");
							pst.executeUpdate();
							pst = ConnectionPool.getConneciton().prepareStatement("DELETE FROM statistics_old where pname='" + p_name + "'");
							pst.executeUpdate();
							pst = ConnectionPool.getConneciton().prepareStatement("DELETE FROM perm_statistics where pname='" + p_name + "'");
							pst.executeUpdate();
						} catch (SQLException ex) {
							Hive.log.log(Level.SEVERE, ex.getMessage(), ex);      

						} finally {
							try {
								if (pst != null) {
									pst.close();
								}

							} catch (SQLException ex) {
								Hive.log.log(Level.WARNING, ex.getMessage(), ex);
							}
						}

						log.info("Wiped IP banned player " + p_name);
					}

				} catch (SQLException ex) {
					Hive.log.log(Level.SEVERE, ex.getMessage(), ex);      

				} finally {
					try {
						if (pst != null) {
							pst.close();
						}

					} catch (SQLException ex) {
						Hive.log.log(Level.WARNING, ex.getMessage(), ex);
					}
				}*/
			}
		}
		/*Thread t = new Thread(new Runnable() {
			public void run() {
				List<String> reported_offline = new ArrayList<String>();
				PreparedStatement pst = null;
				try{
					pst = ConnectionPool.getConneciton().prepareStatement( 
							"SELECT p_name FROM player_database WHERE server_num = '-1'");

					pst.execute();
					ResultSet rs = pst.getResultSet();
					while(rs.next()){
						reported_offline.add(rs.getString("pname"));
					}

				} catch (SQLException ex) {
					Hive.log.log(Level.SEVERE, ex.getMessage(), ex);      

				} finally {
					try {
						if (pst != null) {
							pst.close();
						}

					} catch (SQLException ex) {
						Hive.log.log(Level.WARNING, ex.getMessage(), ex);
					}
				}

				for(String s : reported_offline){
					if(!(hasAccountData(s))){
						log.info("[HIVE] Corrupt player data, " + s);  
						deletePlayerServer(s);
					}				  
					//setPlayerServer(s, -2);
				}
			}
		});

		t.start();*/
		return true;
	}
	
}