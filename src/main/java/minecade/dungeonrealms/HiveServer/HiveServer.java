package minecade.dungeonrealms.HiveServer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.URISyntaxException;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import minecade.dungeonrealms.Main;
import minecade.dungeonrealms.CommunityMechanics.CommunityMechanics;
import minecade.dungeonrealms.HiveServer.commands.CommandClassicRollout;
import minecade.dungeonrealms.HiveServer.commands.CommandCycle;
import minecade.dungeonrealms.HiveServer.commands.CommandRollout;

import org.fusesource.jansi.Ansi;

public class HiveServer {
	
	public static String rootDir = "";
	
	Logger log = Logger.getLogger("Minecraft");
	
	public void onEnable() {
		setSystemPath();
		
		Main.plugin.getCommand("classicrollout").setExecutor(new CommandClassicRollout());
		Main.plugin.getCommand("cycle").setExecutor(new CommandCycle());
		Main.plugin.getCommand("rollout").setExecutor(new CommandRollout());
		
		log.info(Ansi.ansi().fg(Ansi.Color.CYAN).boldOff().toString() + "**************************");
		log.info(Ansi.ansi().fg(Ansi.Color.CYAN).boldOff().toString() + "[HIVE (Server Edition)] has been enabled.");
		log.info(Ansi.ansi().fg(Ansi.Color.CYAN).boldOff().toString() + "**************************" + Ansi.ansi().fg(Ansi.Color.WHITE).boldOff().toString());
	}
	
	public void onDisable() {
		log.info(Ansi.ansi().fg(Ansi.Color.RED).boldOff().toString() + "**************************");
		log.info(Ansi.ansi().fg(Ansi.Color.RED).boldOff().toString() + "[HIVE (Server Edition)] has been disabled.");
		log.info(Ansi.ansi().fg(Ansi.Color.RED).boldOff().toString() + "**************************" + Ansi.ansi().fg(Ansi.Color.WHITE).boldOff().toString());
	}
	
	public void setSystemPath() {
		CodeSource codeSource = HiveServer.class.getProtectionDomain().getCodeSource();
		File jarFile = null;
		try {
			jarFile = new File(codeSource.getLocation().toURI().getPath());
		} catch(URISyntaxException e1) {}
		rootDir = jarFile.getParentFile().getPath();
		int rep = rootDir.contains("/plugins") ? rootDir.indexOf("/plugins") : rootDir.indexOf("\\plugins");
		rootDir = rootDir.substring(0, rep);
	}
	
	public static boolean isThisRootMachine() {
		File f = new File("key");
		if(f.exists()) {
			return true;
		} else {
			return false;
		}
	}
	
	public static void send8008Packet(String input, String server_ip, boolean all) {
		
		Socket kkSocket = null;
		PrintWriter out = null;
		
		List<String> servers = new ArrayList<String>();
		if(server_ip != null) {
			servers.add(server_ip);
		}
		if(all) {
			for(String s : CommunityMechanics.server_list.values()) {
				servers.add(s);
			}
		}
		
		for(String s : servers) {
			// s == IP of server.a
			try {
				try {
					kkSocket = new Socket();
                    kkSocket.connect(new InetSocketAddress(s.contains(":") ? s.split(":")[0] : s, 8008), 2500);
					out = new PrintWriter(kkSocket.getOutputStream(), true);
				} catch(SocketTimeoutException e) {
					e.printStackTrace();
					Main.log.info(Ansi.ansi().fg(Ansi.Color.RED).boldOff().toString() + "[HIVE (Server Edition)] Failed to send payload to server @ " + s + " ; this server may be offline.");
					continue;
				}
				
				out.println(input);
				out.close();
				kkSocket.close();
				Main.log.info(Ansi.ansi().fg(Ansi.Color.CYAN).boldOff().toString() + "[HIVE (SERVER Edition)] Sent payload to " + s + "..." + Ansi.ansi().fg(Ansi.Color.WHITE).boldOff().toString());
			} catch(IOException e) {
				e.printStackTrace();
				Main.log.info(Ansi.ansi().fg(Ansi.Color.RED).boldOff().toString() + "[HIVE (Server Edition)] Failed to send payload to server @ " + s + "");
				continue;
			}
			
		}
	}
	
	public static void deleteFolder(File folder) {
		File[] files = folder.listFiles();
		if(files != null) { //some JVMs return null for empty dirs
			for(File f : files) {
				if(f.isDirectory()) {
					deleteFolder(f);
				} else {
					f.delete();
				}
			}
		}
		folder.delete();
	}
	
	public void copyDirectory(File sourceLocation, File targetLocation) throws IOException {
		if(sourceLocation.isDirectory()) {
			if(!targetLocation.exists()) {
				targetLocation.mkdir();
			}
			
			String[] children = sourceLocation.list();
			for(int i = 0; i < children.length; i++) {
				copyDirectory(new File(sourceLocation, children[i]), new File(targetLocation, children[i]));
			}
		} else {
			
			InputStream in = new FileInputStream(sourceLocation);
			OutputStream out = new FileOutputStream(targetLocation);
			
			// Copy the bits from instream to outstream
			byte[] buf = new byte[1024];
			int len;
			while((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			in.close();
			out.close();
		}
	}
	
	public static final void zipDirectory(File directory, File zip) throws IOException {
		ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zip));
		zip(directory, directory, zos);
		zos.close();
	}
	
	private static final void zip(File directory, File base, ZipOutputStream zos) throws IOException {
		File[] files = directory.listFiles();
		byte[] buffer = new byte[8192];
		int read = 0;
		for(int i = 0, n = files.length; i < n; i++) {
			if(files[i].isDirectory()) {
				zip(files[i], base, zos);
			} else {
				FileInputStream in = new FileInputStream(files[i]);
				ZipEntry entry = new ZipEntry(files[i].getPath().substring(base.getPath().length() + 1));
				zos.putNextEntry(entry);
				while(-1 != (read = in.read(buffer))) {
					zos.write(buffer, 0, read);
				}
				in.close();
			}
		}
	}
	
}
