package me.ifamasssxd.levelmechanics;

import java.util.concurrent.ConcurrentHashMap;

import me.vaqxine.managers.PlayerManager;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class LevelMechanics implements Listener {


    // Player name, PlayerLevel data

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onAsyncLogin(AsyncPlayerPreLoginEvent e) {
        if (e.getLoginResult() == Result.KICK_OTHER)
            return;
        new PlayerLevel(e.getName());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        // No delay on the sql query
        getPlayerData(e.getPlayer()).saveData(true, true);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        getPlayerData(e.getPlayer().getName()).setPlayer(e.getPlayer());
    }

    public static void addXP(Player p, int xp) {
        getPlayerData(p).addXP(xp);
    }

    public static PlayerLevel getPlayerData(Player p) {
        if (PlayerManager.getPlayerModel(p.getName()).getPlayerLevel() != null) {
            PlayerLevel pl = new PlayerLevel(p.getName());
            PlayerManager.getPlayerModel(p).setPlayerLevel(pl);
            return pl;
        }
        return PlayerManager.getPlayerModel(p.getName()).getPlayerLevel();
    }

    public static PlayerLevel getPlayerData(String p_name) {
        if (PlayerManager.getPlayerModel(p_name).getPlayerLevel() != null) {
            return new PlayerLevel(p_name);
        }
        return PlayerManager.getPlayerModel(p_name).getPlayerLevel();
    }

    public static int getPlayerLevel(Player p) {
        if (PlayerManager.getPlayerModel(p).getPlayerLevel() != null) {
            return new PlayerLevel(p.getName()).getLevel();
        }
        return PlayerManager.getPlayerModel(p).getPlayerLevel().getLevel();
    }

    public static int getPlayerLevel(String p_name) {
        if (PlayerManager.getPlayerModel(p_name).getPlayerLevel() != null) {
            return new PlayerLevel(p_name).getLevel();
        }
        return PlayerManager.getPlayerModel(p_name).getPlayerLevel().getLevel();
    }
}
