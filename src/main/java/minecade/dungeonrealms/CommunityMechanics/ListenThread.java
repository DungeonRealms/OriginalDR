package minecade.dungeonrealms.CommunityMechanics;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import minecade.dungeonrealms.Main;
import minecade.dungeonrealms.config.Config;

import org.bukkit.Bukkit;

public class ListenThread extends Thread {
	InetAddress lAddress;
	
	@SuppressWarnings("resource")
	public void run() {
		ServerSocket ss;
		int port = CommunityMechanics.server_list.get(Main.getCommunityMechanics().getServerNum()).contains(":") ? Integer.parseInt(CommunityMechanics.server_list
                .get(Main.getCommunityMechanics().getServerNum()).split(":")[1])
                : Config.transfer_port;
		try {
			
			lAddress = InetAddress.getByName(Bukkit.getIp());
            ss = new ServerSocket(port, 200, lAddress);
            CommunityMechanics.log.info("[CommunityMechanics] LISTENING on port " + port + " @ " + Bukkit.getIp()
                    + " ...");
			
			while(true) {
				final Socket clientSocket = ss.accept();
				String ip = clientSocket.getInetAddress().getHostAddress();
				
				if(!(CommunityMechanics.ip_whitelist.contains(ip))) {
					CommunityMechanics.log.info("[CommunityMechanics] Illegal connection on port " + port + " by " + ip);
					clientSocket.close();
					continue;
				}
				
				Thread process = new Thread(new ConnectProtocol(clientSocket, ip));
				process.start();
			}
		} catch(IOException e) {
			e.printStackTrace();
			CommunityMechanics.log.info("Could not listen on port: " + port);
			return;
		}
	}
	
}
