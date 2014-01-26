package me.vaqxine.ProxyMechanics;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;

public class ThreadPool extends Thread {
	@Override
	public void run(){
		while(true){
			try {Thread.sleep(250);} catch (InterruptedException e) {}
			for(String query : ProxyMechanics.sql_query){
				Connection con = null;
				PreparedStatement pst = null;

				try {
					pst = ConnectionPool.getConneciton().prepareStatement(query);
					pst.executeUpdate();

				} catch (SQLException ex) {

				} finally {
					try {
						if (pst != null) {
							pst.close();
						}
						if (con != null) {
							con.close();
						}

					} catch (SQLException ex) {
						ex.printStackTrace();
					}
				}

				ProxyMechanics.sql_query.remove(query);
			}
		}
	}
}

