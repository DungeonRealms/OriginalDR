package minecade.dungeonrealms.CommunityMechanics;

import java.util.List;
import java.util.Map.Entry;

public class CrossServerPacketThread extends Thread {
	@Override
	public void run() {
		while(true) {
			try {
				Thread.sleep(250);
			} catch(Exception err) {}
			for(Entry<String, List<Object>> edata : CommunityMechanics.social_query_list.entrySet()) {
				List<Object> data = edata.getValue();
				// CommunityMechanics.log.info("d2 - " + data.toString());
				String query = (String) data.get(0);
				String p_name = (String) data.get(1); // Will just be null if all_servers=true
				boolean all_servers = (boolean) data.get(2);
				int server_num = -1;
				if(all_servers == false) {
					server_num = CommunityMechanics.getPlayerServer(p_name, true);
				}
				CommunityMechanics.sendPacketCrossServer(query, server_num, all_servers);
				CommunityMechanics.social_query_list.remove(edata.getKey());
			}
			
		}
	}
}
