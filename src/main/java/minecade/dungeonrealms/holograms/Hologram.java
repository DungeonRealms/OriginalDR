package minecade.dungeonrealms.holograms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import minecade.dungeonrealms.Utils;
import net.minecraft.server.v1_8_R1.EntityHorse;
import net.minecraft.server.v1_8_R1.EntityPlayer;
import net.minecraft.server.v1_8_R1.EntityWitherSkull;
import net.minecraft.server.v1_8_R1.PacketPlayOutAttachEntity;
import net.minecraft.server.v1_8_R1.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_8_R1.PacketPlayOutSpawnEntity;
import net.minecraft.server.v1_8_R1.PacketPlayOutSpawnEntityLiving;
import net.minecraft.server.v1_8_R1.WorldServer;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R1.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class Hologram {
	
	private static final double distance = 0.23;
	private static List<Hologram> holograms = new ArrayList<Hologram>();
	private List<String> lines = new ArrayList<String>();
	private List<Integer> ids = new ArrayList<Integer>();
	private boolean show = false;
	private Location location;
	
	private HashMap<PacketPlayOutSpawnEntity, EntityWitherSkull> skull_entities = new HashMap<PacketPlayOutSpawnEntity, EntityWitherSkull>();
	private HashMap<PacketPlayOutSpawnEntityLiving, EntityHorse> horse_entities = new HashMap<PacketPlayOutSpawnEntityLiving, EntityHorse>();
	private HashMap<PacketPlayOutSpawnEntity, PacketPlayOutSpawnEntityLiving> packets = new HashMap<PacketPlayOutSpawnEntity, PacketPlayOutSpawnEntityLiving>();
	
	public static List<Hologram> getHolograms() {
		return holograms;
	}
	
	public Hologram(Location location, String... lines) {
		new Hologram(location, Arrays.asList(lines));
	}
	
	public Hologram(Location location, List<String> lines) {
		holograms.add(this);
		this.lines.addAll(lines);
		this.location = location;
	}
	
	public void setLines(String... lines) {
		boolean didshow = show;
		if(didshow) hide();
		this.lines = Arrays.asList(lines);
		if(didshow) show();
	}
	
	public void setLines(List<String> lines){
		boolean didshow = show;
		if(didshow) hide();
		this.lines = lines;
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
	
	public Location getLocation(){
		return location;
	}
	
	public List<String> getLines(){
		return lines;
	}
	
	public void show() {
		if(show) return;
		
		horse_entities.clear();
		skull_entities.clear();
		
		packets.clear();
		
		Location first = location.clone().add(0, (this.lines.size() / 2) * distance, 0);
		for(int i = 0; i < this.lines.size(); i++) {
			ids.addAll(showLine(first.clone(), this.lines.get(i)));
			first.subtract(0, distance, 0);
		}
		
		for(Player player : location.getWorld().getPlayers()) {
			sendPacketsToPlayer(player);
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
	
	public void destroy() {
		if(show) hide();
		holograms.remove(this);
	}
	
	public void updateToNearbyPlayers(){
		if(lines.size() == 0) return;
		if(horse_entities.size() == 0) return;

		Entity[] entities = Utils.getNearbyEntities(location, 32);
		
		for(Entity e : entities){
			if(!(e instanceof Player)) continue;
			sendPacketsToPlayer((Player) e);
		}
	}
	
	public void sendPacketsToPlayer(Player player) {
		for(PacketPlayOutSpawnEntity skull_packet : packets.keySet()) {
			EntityPlayer nmsPlayer = ((CraftPlayer) player).getHandle();
			nmsPlayer.playerConnection.sendPacket(packets.get(skull_packet));
			nmsPlayer.playerConnection.sendPacket(skull_packet);
			
			PacketPlayOutAttachEntity pa = new PacketPlayOutAttachEntity(0, horse_entities.get(packets.get(skull_packet)), skull_entities.get(skull_packet));
			nmsPlayer.playerConnection.sendPacket(pa);
		}
	}
	
	private List<Integer> showLine(Location loc, String text) {
		WorldServer world = ((CraftWorld) loc.getWorld()).getHandle();
		
		EntityWitherSkull skull = new EntityWitherSkull(world);
		skull.setLocation(loc.getX(), loc.getY() + 1 + 53.7, loc.getZ(), 0, 0);
		PacketPlayOutSpawnEntity skull_packet = new PacketPlayOutSpawnEntity(skull, 66);
		
		EntityHorse horse = new EntityHorse(world);
		horse.setLocation(loc.getX(), loc.getY() + 1.2 + 53.7, loc.getZ(), 0, 0);
		horse.setAge(-1700000);
		horse.setCustomName(text);
		horse.setCustomNameVisible(true);
		PacketPlayOutSpawnEntityLiving horse_packet = new PacketPlayOutSpawnEntityLiving(horse);
		
		skull_entities.put(skull_packet, skull);
		horse_entities.put(horse_packet, horse);
		
		packets.put(skull_packet, horse_packet);
		
		return Arrays.asList(skull.getId(), horse.getId());
	}
	
}
