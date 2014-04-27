package minecade.dungeonrealms.DonationMechanics;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map.Entry;
import java.util.logging.Level;

public class RankThread extends Thread {
	@Override
	public void run() {
		while(true) {
			try {
				Thread.sleep(1000);
			} catch(Exception err) {}
			for(Entry<String, String> data : DonationMechanics.async_set_rank.entrySet()) {
				String p_name = data.getKey();
				String rank = data.getValue();
				
				Connection con = null;
				PreparedStatement pst = null;
				
				try {
					con = DriverManager.getConnection(DonationMechanics.sql_url, DonationMechanics.sql_user, DonationMechanics.sql_password);
					pst = con.prepareStatement("INSERT INTO player_database (p_name, rank)" + " VALUES" + "('" + p_name + "', '" + rank + "') ON DUPLICATE KEY UPDATE rank='" + rank + "'");
					
					pst.executeUpdate();
					DonationMechanics.log.info("[DonationMechanics] Set rank of player " + p_name + " to " + rank);
					
				} catch(SQLException ex) {
					DonationMechanics.log.log(Level.SEVERE, ex.getMessage(), ex);
					
				} finally {
					try {
						if(pst != null) {
							pst.close();
						}
						if(con != null) {
							con.close();
						}
						
					} catch(SQLException ex) {
						DonationMechanics.log.log(Level.WARNING, ex.getMessage(), ex);
					}
				}
				
				DonationMechanics.async_set_rank.remove(p_name);
			}
		}
	}
}
