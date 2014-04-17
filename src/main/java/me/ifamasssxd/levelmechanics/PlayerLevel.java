package me.ifamasssxd.levelmechanics;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import me.vaqxine.Main;
import me.vaqxine.Hive.Hive;
import me.vaqxine.database.ConnectionPool;

public class PlayerLevel {

    Player p;
    String p_name;
    int level;
    int xp;

    public PlayerLevel(String p_name) {
        this.p_name = p_name;
        loadData();
        LevelMechanics.player_level.put(p_name, this);
    }

    public void setPlayer(Player p) {
        this.p = p;
    }

    public void addXP(int xp) {
        
    }
    
    public static int getEXPNeeded(int level, String stat) {
        if(stat.equalsIgnoreCase("mining")) {
            if(level == 1) { return 176; // formula doens't work on level 1.
            }
            if(level == 75) { return 0; // green bar
            }
            int previous_level = level - 1;
            return (int) (Math.pow((previous_level), 2) + ((previous_level) * 20) + 150 + ((previous_level) * 4) + getEXPNeeded((previous_level), stat));
        }
        return 0;
    }
    public void saveData(boolean useHive, boolean remove) {
        final String pst = "UPDATE player_database SET player_level = " + getLevel() + ", player_xp = " + getXP() + " WHERE p_name = " + p_name + ";";
        if (useHive) {
            Hive.sql_query.add(pst);
        } else {
            new BukkitRunnable() {
                public void run() {
                    try (PreparedStatement prest = ConnectionPool.getConnection().prepareStatement(pst)) {
                        prest.executeUpdate();
                        prest.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }.runTaskAsynchronously(Main.plugin);

        }
        if(remove){
            LevelMechanics.player_level.remove(p_name);
            return;
        }
    }

    public void loadData() {
        try (PreparedStatement pst = ConnectionPool.getConnection().prepareStatement("SELECT player_level, player_xp FROM player_database WHERE p_name = ?;")) {
            pst.setString(1, p_name);
            ResultSet rs = pst.executeQuery();
            if (!rs.first()) {
                sendInsertUpdate();
                // Newcomer in these parts.
                setLevel(1);
                setXP(0);
                pst.close();
                return;
            } else {
                setLevel(rs.getInt("player_level"));
                setXP(rs.getInt("player_xp"));
            }
            pst.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void sendInsertUpdate() {
        try (PreparedStatement pst = ConnectionPool.getConnection().prepareStatement(
                "INSERT INTO player_database(p_name, player_level, player_xp) VALUES (?, 1, 0) ON DUPLICATE KEY UPDATE p_name = ?")) {
            pst.setString(1, p_name);
            pst.setString(2, p_name);
            pst.executeUpdate();
            pst.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void setXP(int xp) {
        this.xp = xp;
    }

    public int getLevel() {
        return level;
    }

    public int getXP() {
        return xp;
    }

}
