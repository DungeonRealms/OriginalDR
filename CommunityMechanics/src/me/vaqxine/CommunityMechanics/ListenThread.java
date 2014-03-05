package me.vaqxine.CommunityMechanics;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import me.vaqxine.Hive.Hive;

import org.bukkit.Bukkit;

public class ListenThread extends Thread {
	InetAddress lAddress;

	public void run() {
		ServerSocket ss;
		try {

			lAddress = InetAddress.getByName(Bukkit.getIp());
			ss = new ServerSocket(Hive.transfer_port, 200, lAddress);
			CommunityMechanics.log.info("[CommunityMechanics] LISTENING on port " + Hive.transfer_port + " @ " + Bukkit.getIp() + " ...");


			while(true){
				final Socket clientSocket = ss.accept();
				String ip = clientSocket.getInetAddress().getHostAddress();
				if(!(CommunityMechanics.ip_whitelist.contains(ip))){
					CommunityMechanics.log.info("[CommunityMechanics] Illegal connection on port " + Hive.transfer_port + " by " + ip);
					clientSocket.close();
					continue;
				}
				Thread process = new Thread(new ConnectProtocol(clientSocket));
				process.start();
			} 

		} catch (IOException e) {
			e.printStackTrace();
			CommunityMechanics.log.info("Could not listen on port: " + Hive.transfer_port);
			return;
		}
	}

}
