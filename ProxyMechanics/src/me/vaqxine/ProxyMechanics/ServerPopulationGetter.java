package me.vaqxine.ProxyMechanics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map.Entry;

import me.vaqxine.query.MCQuery;
import me.vaqxine.query.QueryResponse;

public class ServerPopulationGetter extends Thread {
	@Override
	public void run(){
		while(true){
			try{Thread.sleep(10 * 1000);}catch(InterruptedException ie){}
			updateServerPopulations();
		}
	}
	
	public void updateServerPopulations(){
		for(Entry<Integer, String> data : ProxyMechanics.server_list.entrySet()){
			int server_num = data.getKey();
			String ip = data.getValue();
			try{
				MCQuery mcQuery = new MCQuery(ip, 25152);
				QueryResponse response = mcQuery.basicStat();
				int online_players = response.getOnlinePlayers();
				int max_players = response.getMaxPlayers();
				String motd = response.getMOTD();

				ProxyMechanics.server_population.put(server_num, new ArrayList<Integer>(Arrays.asList(online_players, max_players)));
				ProxyMechanics.server_motd.put(server_num, motd);
				
				if(online_players > 0 && ProxyMechanics.crashed_servers.contains(ProxyMechanics.getServerPrefixFromNum(server_num))){
					ProxyMechanics.crashed_servers.remove(ProxyMechanics.getServerPrefixFromNum(server_num));
				}
			} catch(Exception err){
				ProxyMechanics.server_population.put(server_num, new ArrayList<Integer>(Arrays.asList(0, 0)));
				ProxyMechanics.server_motd.put(server_num, null);
				continue;
			}
		}
	}
}
