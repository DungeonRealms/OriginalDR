package me.ifamasssxd.levelmechanics;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import me.vaqxine.Main;
import me.vaqxine.CommunityMechanics.CommunityMechanics;
import me.vaqxine.Hive.Hive;
import me.vaqxine.database.ConnectionPool;
import me.vaqxine.managers.PlayerManager;

public class PlayerLevel {

    Player p;
    String p_name;
    int level;
    int xp;
    long next_xp_gain_available;

    public PlayerLevel(String p_name, boolean aSync) {
        this.p_name = p_name;
        next_xp_gain_available = System.currentTimeMillis();
        if (aSync) {
            new BukkitRunnable() {
                public void run() {
                    // TODO Auto-generated method stub
                    loadData();
                }
            }.runTaskAsynchronously(Main.plugin);
        } else {
            loadData();
        }

        PlayerManager.getPlayerModel(p_name).setPlayerLevel(this);
    }

    public void setPlayer(Player p) {
        this.p = p;
    }

    public void addXP(int xp) {
        // Throttle XP gain by a few seconds
        if (next_xp_gain_available > System.currentTimeMillis())
            return;
        // 3 second delay
        next_xp_gain_available = System.currentTimeMillis() + 3000;
        int xp_needed = getEXPNeeded(getLevel());
        if (getXP() + xp >= xp_needed) {
            int xp_remaining = (getXP() + xp) - xp_needed;
            levelUp(true);
            if (xp_remaining > 0) {
                addXP(xp_remaining);
            }
        } else {
            // No remaining xp
            setXP(getXP() + xp);
        }
        //saveData(true, false);
        if (PlayerManager.getPlayerModel(p_name).getToggleList() != null && PlayerManager.getPlayerModel(p_name).getToggleList().contains("debug")) {
            if (p == null) {
                checkPlayer();
            }
            p.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + "          +" + ChatColor.YELLOW + (int) xp + ChatColor.BOLD + " EXP" + ChatColor.YELLOW
                    + ChatColor.GRAY + " [" + getXP() + ChatColor.BOLD + "/" + ChatColor.GRAY + (int) getEXPNeeded(getLevel()) + " EXP]");

        }
        CommunityMechanics.generateCommBook(p);
    }

    public int getEXPNeeded(int level) {
        if (level >= 0) {
            if (level == 1) {
                return 300; // formula doens't work on level 1.
            }
            if (level == 100) {
                return 0;
            }
            int previous_level = level - 1;
            return (int) (Math.pow((previous_level), 2) + ((previous_level) * 20) + 200 + ((previous_level) * 4) + getEXPNeeded(previous_level));
        }
        return 0;
    }

    public void saveData(boolean useHive, boolean remove) {
        final String pst = "UPDATE player_database SET player_level = " + getLevel() + ", player_xp = " + getXP() + " WHERE p_name = '" + p_name + "';";
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
        if (remove) {
            PlayerManager.getPlayerModel(p_name).setPlayerLevel(null);
            return;
        }
    }

    public void levelUp(boolean alert) {

        setXP(0);
        if (alert) {
            if (p == null) {
                checkPlayer();
            }
            p.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + "         " + " LEVEL UP! " + ChatColor.YELLOW + ChatColor.UNDERLINE + (getLevel())
                    + ChatColor.BOLD + " -> " + ChatColor.YELLOW + ChatColor.UNDERLINE + (getLevel() + 1));
            p.playSound(p.getLocation(), Sound.LEVEL_UP, 0.5F, 1F);
        }
        setLevel(getLevel() + 1);
    }

    @SuppressWarnings("deprecation")
    public void checkPlayer() {
        this.p = Bukkit.getPlayer(p_name);
    }

    public void loadData() {
        try (PreparedStatement pst = ConnectionPool.getConnection().prepareStatement(
                "SELECT player_level, player_xp FROM player_database WHERE p_name = '" + p_name + "'")) {
            ResultSet rs = pst.executeQuery();
            if (!rs.first()) {
                Main.d(p_name + " was loaded for the first time.");
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
                "INSERT INTO player_database(p_name, player_level, player_xp) VALUES ('" + p_name + "', 1, 0) ON DUPLICATE KEY UPDATE p_name = '" + p_name
                        + "'")) {
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
