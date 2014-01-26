package me.vaqxine.ProxyMechanics;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

public class SocketPool extends Thread{
	@Override
	public void run(){
		while(true){
			try {Thread.sleep(500);} catch (InterruptedException e) {}
			for(Entry<String, String> query_data : ProxyMechanics.socket_pool.entrySet()){
				Socket kkSocket = null;
				PrintWriter out = null;

				String query = query_data.getKey();
				String IP = query_data.getValue();
				boolean send_all = false;
				List<String> target_list = new ArrayList<String>();
				
				if(IP.equalsIgnoreCase("*")){
					for(String s : ProxyMechanics.server_list.values()){
						target_list.add(s);
					}
				}
				else{ // Singular target
					target_list.add(IP);
				}
				
				try {
					for(String ip : target_list){
						kkSocket = new Socket();
						kkSocket.connect(new InetSocketAddress(ip, ProxyMechanics.transfer_port), 500);
						out = new PrintWriter(kkSocket.getOutputStream(), true);
						out.println(query);
					}
					
				} catch (IOException e) {
					if(kkSocket != null){
						try {kkSocket.close();} catch (IOException err) {}
					}
					if(out != null){
						out.close();
					}
					continue;
				}

				if(kkSocket != null){
					try {kkSocket.close();} catch (IOException err) {}
				}
				
				if(out != null){
					out.close();
				}

				ProxyMechanics.socket_pool.remove(query);
			}
		}
	}
}
