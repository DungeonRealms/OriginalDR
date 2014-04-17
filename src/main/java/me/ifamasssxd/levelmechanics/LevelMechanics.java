package me.ifamasssxd.levelmechanics;

import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;
import org.bukkit.event.player.PlayerJoinEvent;

public class LevelMechanics implements Listener {

    public static ConcurrentHashMap<String, PlayerLevel> player_level = new ConcurrentHashMap<String, PlayerLevel>();

    // Player name, PlayerLevel data

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onAsyncLogin(AsyncPlayerPreLoginEvent e) {
        if (e.getLoginResult() == Result.KICK_OTHER)
            return;
        new PlayerLevel(e.getName());
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {

    }
    
    public static void addXP(Player p, int xp){
        getPlayerLevel(p).addXP(xp);
    }
    public static int getPlayerLevel(Player p) {
        if (!player_level.containsKey(p.getName())) {
            return new PlayerLevel(p.getName()).getLevel();
        }
        return player_level.get(p.getName()).getLevel();
    }
    
    public static int getPlayerLevel(String p_name) {
        if (!player_level.containsKey(p_name)) {
            return new PlayerLevel(p_name).getLevel();
        }
        return player_level.get(p_name).getLevel();
    }
}
