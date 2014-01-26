package me.vaqxine.ProxyMechanics;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ChatColor;

public class ServerListener implements Runnable {
	private Socket clientSocket;

	public ServerListener(Socket s) {
		this.clientSocket = s;
	}

	@Override
	public void run() {
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			String inputLine;

			while ((inputLine = in.readLine()) != null) {

				if(inputLine.startsWith("@population@")){
					// @population@US-1:50/150
					String server_prefix = inputLine.substring(inputLine.lastIndexOf("@") + 1, inputLine.indexOf(":"));
					int online_players = Integer.parseInt(inputLine.substring(inputLine.indexOf(":") + 1, inputLine.indexOf("/")));
					int max_players = Integer.parseInt(inputLine.substring(inputLine.indexOf("/") + 1, inputLine.length()));
					int server_num = ProxyMechanics.getServerNumFromPrefix(server_prefix);
					
					if(!(ProxyMechanics.server_list.containsKey(server_num))){
						return;
					}
					
					if(online_players > 0 && ProxyMechanics.crashed_servers.contains(server_prefix)){
						ProxyMechanics.crashed_servers.remove(server_prefix);
					}
					
					ProxyMechanics.last_ping.put(server_num, System.currentTimeMillis());
					ProxyMechanics.server_population.put(server_num, new ArrayList<Integer>(Arrays.asList(online_players, max_players)));
					return;
				}
				
				if(inputLine.startsWith("[cycle]")){
					// Reboot the proxy in 60 seconds along with the servers.
						// Extra 20 seconds for the ~20 seconds it takes for servers to boot + load memory.
					System.out.println(ChatColor.YELLOW + ">> Rebooting PROXY in 90 seconds...");
					ProxyMechanics.reboot_soon = true;
					Thread shutdown = new Thread(new Runnable(){
						public void run(){
							try{Thread.sleep(60 * 1000);} catch(Exception err){};
							System.out.println(ChatColor.YELLOW + ">> Rebooting PROXY in 30 seconds...");
							try{Thread.sleep(30 * 1000);} catch(Exception err){};
							System.out.println(ChatColor.YELLOW + ">> Rebooting PROXY...");
							BungeeCord.getInstance().stop();
						}
					});
					
					shutdown.start();
				}
				
				if(inputLine.startsWith("[rank]")){
					// [forumgroup]p_name@new_rank
					String p_name = inputLine.substring(inputLine.indexOf("]") + 1, inputLine.indexOf("@"));
					String rank = inputLine.substring(inputLine.indexOf("@") + 1, inputLine.length());
					ProxyMechanics.user_rank.put(p_name, rank);
				}
				
				if(inputLine.startsWith("[ipban]")){
					// [ipban]IP
					String ip = inputLine.substring(inputLine.indexOf("]") + 1, inputLine.length());
					ProxyMechanics.banned_IP.add(ip);
				}
				
				if(inputLine.startsWith("[ban]")){
					// [ban]p_name@unban_date
					String ban_player = inputLine.substring(inputLine.indexOf("]") + 1, inputLine.indexOf("@"));
					long unban_date = System.currentTimeMillis() + Long.parseLong(inputLine.substring(inputLine.indexOf("@") + 1, inputLine.length()));
					ProxyMechanics.ban_database.put(ban_player.toLowerCase(), unban_date);

					System.out.println("[Listener] Fufilled [BAN] request for user " + ban_player + ".");
				}
				
				if(inputLine.startsWith("[unban]")){
					String unban_player = inputLine.substring(inputLine.indexOf("]") + 1, inputLine.length());
					ProxyMechanics.ban_database.remove(unban_player.toLowerCase());
					ProxyMechanics.ipban_database.remove(unban_player.toLowerCase());
					
					System.out.println("[Listener] Fufilled [UNBAN] request for user " + unban_player + ".");
				}
				
				if(inputLine.startsWith("[crash]")){
					String crashed_server = inputLine.substring(inputLine.indexOf("]") + 1, inputLine.length());
					if(!(ProxyMechanics.crashed_servers.contains(crashed_server))){
						ProxyMechanics.crashed_servers.add(crashed_server);
					}
					System.out.println(ChatColor.RED + ">> Server " + crashed_server + " has reported as OFFLINE!");
				}
				
				if(inputLine.startsWith("[online]")){
					String online_server = inputLine.substring(inputLine.indexOf("]") + 1, inputLine.length());
					if(ProxyMechanics.crashed_servers.contains(online_server)){
						ProxyMechanics.crashed_servers.remove(online_server);
					}
					System.out.println(ChatColor.GREEN + ">> Server " + online_server + " has reported as ONLINE.");
				}
				
			}
		} catch(Exception err){
			err.printStackTrace();
		}
	}
}
