package minecade.dungeonrealms.Hive;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;

import minecade.dungeonrealms.database.ConnectionPool;
import minecade.dungeonrealms.models.LogModel;

public class ThreadPool extends Thread {
	@Override
	public void run() {
		while(true) {
			try {
				Thread.sleep(250);
			} catch(InterruptedException e) {}
			for(String query : Hive.sql_query) {
				Connection con = null;
				PreparedStatement pst = null;
				
				try {
					pst = ConnectionPool.getConnection().prepareStatement(query);
					pst.executeUpdate();
					
					Hive.log.info("[Hive] ASYNC Executed query: " + query);
					
				} catch(SQLException ex) {
					Hive.log.log(Level.SEVERE, ex.getMessage(), ex);
					
				} finally {
					try {
						if(pst != null) {
							pst.close();
						}
						if(con != null) {
							con.close();
						}
						
					} catch(SQLException ex) {
						Hive.log.log(Level.WARNING, ex.getMessage(), ex);
					}
				}
				
				Hive.sql_query.remove(query);
			}

			String log_example = "INSERT INTO logs (type, player, time, data) VALUES (?, ?, ?, ?);";
			
			for(LogModel log : Hive.logs) {
				Connection con = null;
				PreparedStatement pst = null;
				
				try {
					pst = ConnectionPool.getConnection().prepareStatement(log_example);
					pst.setString(1, log.type.name());
					pst.setString(2, log.player);
					pst.setLong(3, log.time);
					pst.setString(4, (log.data != null) ? log.data.toString() : "");
					pst.executeUpdate();
				} catch(SQLException ex) {
					Hive.log.log(Level.SEVERE, ex.getMessage(), ex);
					
				} finally {
					try {
						if(pst != null) {
							pst.close();
						}
						if(con != null) {
							con.close();
						}
						
					} catch(SQLException ex) {
						Hive.log.log(Level.WARNING, ex.getMessage(), ex);
					}
				}
				
				Hive.logs.remove(log);
			}
		}
	}
}
