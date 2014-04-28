package minecade.dungeonrealms.LevelMechanics;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import minecade.dungeonrealms.Main;
import minecade.dungeonrealms.CommunityMechanics.CommunityMechanics;
import minecade.dungeonrealms.HealthMechanics.HealthMechanics;
import minecade.dungeonrealms.Hive.Hive;
import minecade.dungeonrealms.PartyMechanics.PartyMechanics;
import minecade.dungeonrealms.ScoreboardMechanics.ScoreboardMechanics;
import minecade.dungeonrealms.database.ConnectionPool;
import minecade.dungeonrealms.managers.PlayerManager;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerLevel {

    Player p;
    String p_name;
    int level;
    int xp;
    Entity last_mob_gained_from;

    public PlayerLevel(String p_name, boolean aSync) {
        this.p_name = p_name;
        if (aSync) {
            new BukkitRunnable() {
                public void run() {
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

    public Entity getLastEntityKilled() {
        return last_mob_gained_from;
    }

    public void setLastEntityKilled(Entity e) {
        this.last_mob_gained_from = e;
    }

    public void addXP(int xp) {
        // Throttle XP gain by a few seconds
        if (getLevel() >= 100)
            return;
        // 3 second delay
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
        // saveData(true, false);
        if (PlayerManager.getPlayerModel(p_name).getToggleList() != null && PlayerManager.getPlayerModel(p_name).getToggleList().contains("debug")) {
            if (p == null) {
                checkPlayer();
            }
            p.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + "          +" + ChatColor.YELLOW + (int) xp + ChatColor.BOLD + " EXP" + ChatColor.YELLOW
                    + ChatColor.GRAY + " [" + getXP() + ChatColor.BOLD + "/" + ChatColor.GRAY + (int) getEXPNeeded(getLevel()) + " EXP]");

        }
        CommunityMechanics.updateCombatPage(p);
    }

    public int getEXPNeeded(int level) {
        if (level == 1) {
            return 1000;
        }
        if (level >= 101) {
            return 0;
        }
        return (int) (400 * Math.pow(level, 1.6));
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
            p.getWorld().playSound(p.getLocation(), Sound.LEVEL_UP, 1, .4F);
            p.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + "         " + " LEVEL UP! " + ChatColor.YELLOW + ChatColor.UNDERLINE + (getLevel())
                    + ChatColor.BOLD + " -> " + ChatColor.YELLOW + ChatColor.UNDERLINE + (getLevel() + 1));
            p.playSound(p.getLocation(), Sound.LEVEL_UP, 0.5F, 1F);
        }
        setLevel(getLevel() + 1);
        updateScoreboardLevel();
    }

    public void updateScoreboardLevel() {
        ScoreboardMechanics.setPlayerLevel(getLevel(), p);
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
        if(p != null){
            ScoreboardMechanics.setPlayerLevel(getLevel(), p);
        }
    }

    public void setXP(int xp) {
        this.xp = xp;
        if(p != null) HealthMechanics.setOverheadHP(p, HealthMechanics.getPlayerHP(p.getName()));
    }

    public int getLevel() {
        return level;
    }

    public int getXP() {
        return xp;
    }

}
