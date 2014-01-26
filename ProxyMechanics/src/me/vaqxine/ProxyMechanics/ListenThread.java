package me.vaqxine.ProxyMechanics;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;

import net.md_5.bungee.BungeeCord;

public class ListenThread extends Thread {
	InetAddress lAddress;

	public void run() {
		ServerSocket ss;
		try {

			lAddress = InetAddress.getByName(ProxyMechanics.Proxy_IP);
			ss = new ServerSocket(ProxyMechanics.transfer_port, 1000, lAddress);
			System.out.println("[ProxyMechanics] LISTENING on port " + ProxyMechanics.transfer_port + "...");


			while(true){
				Socket clientSocket = ss.accept();
				String ip = clientSocket.getInetAddress().getHostAddress();
				if(!(ProxyMechanics.ip_whitelist.contains(ip))){
					System.out.println("[ProxyMechanics] Illegal connection on port " + ProxyMechanics.transfer_port + " by " + ip);
					clientSocket.close();
					continue;
				}
				Thread process = new Thread(new ServerListener(clientSocket));
				process.start(); 
			} 

		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("FATAL ERROR: Could not listen on port: " + ProxyMechanics.transfer_port);
			return;
		}

	}

}
