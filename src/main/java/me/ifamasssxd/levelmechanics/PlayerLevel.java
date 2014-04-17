package me.ifamasssxd.levelmechanics;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.entity.Player;

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
    public void addXP(int xp){
        
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
