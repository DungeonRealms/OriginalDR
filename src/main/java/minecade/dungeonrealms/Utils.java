package minecade.dungeonrealms;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.server.v1_7_R4.EntityHuman;
import net.minecraft.server.v1_7_R4.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_7_R4.PacketPlayOutEntityEquipment;
import net.minecraft.server.v1_7_R4.PacketPlayOutNamedEntitySpawn;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_7_R4.inventory.CraftItemStack;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class Utils {
	
    public static Entity[] getNearbyEntities(Location l, int radius){
        int chunkRadius = radius < 16 ? 1 : (radius - (radius % 16))/16;
        List<Entity> radiusEntities = new ArrayList<Entity>();
            for (int chX = 0 -chunkRadius; chX <= chunkRadius; chX ++){
                for (int chZ = 0 -chunkRadius; chZ <= chunkRadius; chZ++){
                    int x=(int) l.getX(),y=(int) l.getY(),z=(int) l.getZ();
                    for (Entity e : new Location(l.getWorld(),x+(chX*16),y,z+(chZ*16)).getChunk().getEntities()){
                        if (e.getLocation().distance(l) <= radius && e.getLocation().getBlock() != l.getBlock()) radiusEntities.add(e);
                    }
                }
            }
        return radiusEntities.toArray(new Entity[radiusEntities.size()]);
    }
    
    public static long getTime(){
    	return Calendar.getInstance().getTimeInMillis();
    }
    
    public static boolean isBeta(){
    	int this_server_num = Integer.parseInt(Bukkit.getMotd().split("-")[1].split(" ")[0]);
		if(this_server_num >= 100 && this_server_num <= 110) return true;
		return false;
    }
    
    public static String getShard(){
    	return Bukkit.getMotd().split(" ")[0];
    }
    
    /**
     * Get key from a HashMap with a value
     */
    public static <T, E> T getKeyByValue(Map<T, E> map, E value) {
        for (Entry<T, E> entry : map.entrySet()) {
            if (value.equals(entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }
    
    public static void refreshPlayerEquipment(Player player, Player forWhom) {

        if (forWhom.canSee(player) && player.getWorld().equals(forWhom.getWorld())) {
            final int id = player.getEntityId();
            final EntityHuman human = ((CraftPlayer) player).getHandle();
            final CraftPlayer otherGuyC = (CraftPlayer) forWhom;
            otherGuyC.getHandle().playerConnection.sendPacket(new PacketPlayOutEntityDestroy(id));
            otherGuyC.getHandle().playerConnection.sendPacket(new PacketPlayOutNamedEntitySpawn(human));
            otherGuyC.getHandle().playerConnection.sendPacket(new PacketPlayOutEntityEquipment(id, 0, CraftItemStack
                    .asNMSCopy(player.getItemInHand())));
            otherGuyC.getHandle().playerConnection.sendPacket(new PacketPlayOutEntityEquipment(id, 1, CraftItemStack
                    .asNMSCopy(player.getInventory().getBoots())));
            otherGuyC.getHandle().playerConnection.sendPacket(new PacketPlayOutEntityEquipment(id, 2, CraftItemStack
                    .asNMSCopy(player.getInventory().getLeggings())));
            otherGuyC.getHandle().playerConnection.sendPacket(new PacketPlayOutEntityEquipment(id, 3, CraftItemStack
                    .asNMSCopy(player.getInventory().getChestplate())));
            otherGuyC.getHandle().playerConnection.sendPacket(new PacketPlayOutEntityEquipment(id, 4, CraftItemStack
                    .asNMSCopy(player.getInventory().getHelmet())));
        }

    }
    
}
