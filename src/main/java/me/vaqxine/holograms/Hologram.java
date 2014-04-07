package me.vaqxine.holograms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.server.v1_7_R2.EntityHorse;
import net.minecraft.server.v1_7_R2.EntityPlayer;
import net.minecraft.server.v1_7_R2.EntityWitherSkull;
import net.minecraft.server.v1_7_R2.PacketPlayOutAttachEntity;
import net.minecraft.server.v1_7_R2.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_7_R2.PacketPlayOutSpawnEntity;
import net.minecraft.server.v1_7_R2.PacketPlayOutSpawnEntityLiving;
import net.minecraft.server.v1_7_R2.WorldServer;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_7_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_7_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class Hologram {
	
	private static final double distance = 0.23;
	private static List<Hologram> holograms = new ArrayList<Hologram>();
	private List<String> lines = new ArrayList<String>();
	private List<Integer> ids = new ArrayList<Integer>();
	private boolean show = false;
	private Location location;

	private EntityWitherSkull skull;
	private EntityHorse horse;
	
	private PacketPlayOutSpawnEntity skull_packet;
	private PacketPlayOutSpawnEntityLiving horse_packet;
	
	public static List<Hologram> getHolograms() {
		return holograms;
	}
	
	public Hologram(Location location, String... lines) {
		holograms.add(this);
		this.lines.addAll(Arrays.asList(lines));
		this.location = location;
	}
	
	public void setLines(String... lines) {
		boolean didshow = show;
		if(didshow) hide();
		this.lines = Arrays.asList(lines);
		if(didshow) show();
	}
	
	public void setLocation(Location location) {
		boolean didshow = show;
		if(didshow) hide();
		this.location = location.clone();
		if(didshow) show();
	}
	
	public void setLocationAbove(Location location) {
		boolean didshow = show;
		if(didshow) hide();
		this.location = location.clone().add(0, 1.2, 0);
		if(didshow) show();
	}
	
	public void show() {
		if(show) return;
		
		Location first = location.clone().add(0, (this.lines.size() / 2) * distance, 0);
		for(int i = 0; i < this.lines.size(); i++) {
			ids.addAll(showLine(first.clone(), this.lines.get(i)));
			first.subtract(0, distance, 0);
		}
		
		show = true;
	}
	
	public void hide() {
		if(!show) return;
		
		int[] ints = new int[this.ids.size()];
		for(int j = 0; j < ints.length; j++) {
			ints[j] = ids.get(j);
		}
		
		PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(ints);
		
		for(Player player : Bukkit.getOnlinePlayers()) {
			((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
		}
		
		show = false;
	}
	
	public void destroy(){
		if(show) hide();
		holograms.remove(this);
	}
	
	public void sendPacketsToPlayer(Player player){
		EntityPlayer nmsPlayer = ((CraftPlayer) player).getHandle();
		nmsPlayer.playerConnection.sendPacket(horse_packet);
		nmsPlayer.playerConnection.sendPacket(skull_packet);
		
		PacketPlayOutAttachEntity pa = new PacketPlayOutAttachEntity(0, horse, skull);
		nmsPlayer.playerConnection.sendPacket(pa);
	}
	
	private List<Integer> showLine(Location loc, String text) {
		WorldServer world = ((CraftWorld) loc.getWorld()).getHandle();
		
		skull = new EntityWitherSkull(world);
		skull.setLocation(loc.getX(), loc.getY() + 1 + 53.7, loc.getZ(), 0, 0);
		skull_packet = new PacketPlayOutSpawnEntity(skull, 66);
		
		horse = new EntityHorse(world);
		horse.setLocation(loc.getX(), loc.getY() + 1.2 + 53.7, loc.getZ(), 0, 0);
		horse.setAge(-1700000);
		horse.setCustomName(text);
		horse.setCustomNameVisible(true);
		horse_packet = new PacketPlayOutSpawnEntityLiving(horse);
		
		for(Player player : loc.getWorld().getPlayers()) {
			sendPacketsToPlayer(player);
		}
		
		return Arrays.asList(skull.getId(), horse.getId());
	}
	
}
