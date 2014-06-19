package minecade.dungeonrealms;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

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
		if(this_server_num >= 99 && this_server_num <= 110) return true;
		return false;
    }
    
    public static String getShard(){
    	return Bukkit.getMotd().split(" ")[0];
    }
	
}
