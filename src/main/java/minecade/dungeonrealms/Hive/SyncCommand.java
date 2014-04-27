package minecade.dungeonrealms.Hive;

import java.sql.SQLException;

import minecade.dungeonrealms.MoneyMechanics.MoneyMechanics;
import minecade.dungeonrealms.ShopMechanics.ShopMechanics;

public class SyncCommand extends Thread {
	public void run() {
		while(true) {
			try {
				Thread.sleep(250);
			} catch(InterruptedException e) {}
			int count = 0;
			if(Hive.sync_queue.size() <= 0) {
				continue;
			}
			for(String p_name : Hive.sync_queue) {
				if(!(Hive.shutting_down) && !(ShopMechanics.shop_shutdown) && !(Hive.pending_upload.contains(p_name))) {
					try {
						Hive.uploadPlayerDatabaseData(p_name); // Location, Inventory
						MoneyMechanics.uploadBankDatabaseData(p_name, false);
						ShopMechanics.uploadShopDatabaseData(p_name, false);
					} catch(SQLException err) {
						err.printStackTrace();
						continue;
					}
					;
				}
				count++;
			}
			Hive.sync_queue.clear();
			Hive.log.info("[Hive (SyncCommand)] Uploaded records of " + count + " player(s).");
		}
	}
}
