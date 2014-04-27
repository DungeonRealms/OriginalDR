package minecade.dungeonrealms.RealmMechanics;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import minecade.dungeonrealms.database.ConnectionPool;

public class RealmStatusThread extends Thread {
	@Override
	public void run() {
		while(true) {
			try {
				Thread.sleep(250);
			} catch(Exception err) {}
			for(String p_name : RealmMechanics.async_realm_status) {
				Connection con = null;
				PreparedStatement pst = null;
				
				try {
					pst = ConnectionPool.getConnection().prepareStatement("SELECT realm_loaded FROM player_database WHERE p_name = '" + p_name + "'");
					
					pst.execute();
					ResultSet rs = pst.getResultSet();
					if(rs.next() == false) {
						RealmMechanics.realm_loaded_status.put(p_name, false);
						RealmMechanics.async_realm_status.remove(p_name);
						continue;
					}
					
					Boolean loaded = rs.getBoolean("realm_loaded");
					RealmMechanics.realm_loaded_status.put(p_name, loaded);
					RealmMechanics.async_realm_status.remove(p_name);
					continue;
					
				} catch(SQLException ex) {
					ex.printStackTrace();
					
				} finally {
					try {
						if(pst != null) {
							pst.close();
						}
						if(con != null) {
							con.close();
						}
						
					} catch(SQLException ex) {
						ex.printStackTrace();
					}
				}
				
				RealmMechanics.realm_loaded_status.put(p_name, false);
				RealmMechanics.async_realm_status.remove(p_name);
				continue;
			}
		}
	}
}
