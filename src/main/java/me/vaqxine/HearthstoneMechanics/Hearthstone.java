package me.vaqxine.HearthstoneMechanics;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import me.vaqxine.database.ConnectionPool;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class Hearthstone {
    Location tp_loc;
    long timer;
    String tp_name;
    Player p;
    String p_name;

    public Hearthstone(String p_name) {
        this.p_name = p_name;
        loadData(p_name);
        HearthstoneMechanics.hearthstone_map.put(p_name, this);
    }
    public void setPlayer(Player p){
        this.p = p;
    }
    public void loadData(String p_name){
        try(PreparedStatement pst = ConnectionPool.getConnection().prepareStatement("SELECT * FROM hearthstone WHERE p_name = ?")){
            pst.setString(1, p_name);
            ResultSet rst = pst.executeQuery();
            if(!rst.next()){
                tp_loc = HearthstoneMechanics.spawn_map.get("Spawn");
                tp_name = "Spawn";
                return;
            }
           //TODO: Download the data from tables and set their spawns
        }catch(SQLException sqlE){
            sqlE.printStackTrace();
        }
    }

    public void setLocation(Location l) {
        this.tp_loc = l;
    }

    public Location getLocation() {
        return tp_loc;
    }

    public void setLocationName(String name) {
        this.tp_name = name;
    }

    public String getName() {
        return tp_name;
    }
}
