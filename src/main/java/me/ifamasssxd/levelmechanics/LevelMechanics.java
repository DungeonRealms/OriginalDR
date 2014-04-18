package me.ifamasssxd.levelmechanics;

import java.util.Random;

import me.vaqxine.Main;
import me.vaqxine.MonsterMechanics.MonsterMechanics;
import me.vaqxine.enums.CC;
import me.vaqxine.managers.PlayerManager;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class LevelMechanics implements Listener {

    // Player name, PlayerLevel data

    @EventHandler
    public void onAsyncLogin(AsyncPlayerPreLoginEvent e) {
        Main.d("CALLED FOR " + e.getName(), CC.BLUE);
        new PlayerLevel(e.getName(), false);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityDeathEvent(EntityDeathEvent e) {
        if (e.getEntity() instanceof Player)
            return;
        EntityDamageEvent eDamage = e.getEntity().getLastDamageCause();
        if (eDamage == null) {
            Main.d("There was no EntityDamageEvent..");
            return;
        }
        // It wasnt a player kill
        if (!(eDamage instanceof EntityDamageByEntityEvent)) {
            Main.d("WASNT ENTITYDAMAGEBYENTITY");
            return;
        }

        EntityDamageByEntityEvent event = (EntityDamageByEntityEvent) eDamage;
        Main.d("Was entityDamageEvent");
        // Not a player damage or arrow
        if (!(event.getDamager() instanceof Player) && !(event.getDamager() instanceof Arrow)) {
            Main.d("The damage wasnt by a player though");
            return;
        }
        // TODO: MAKE MOBS HAVE LEVELS
        Main.d("The damage was a player!  Yea!");
        int mob_level = MonsterMechanics.getMobLevel(e.getEntity());
        Main.d(mob_level);
        Player killer = (Player) event.getDamager();
        int level = getPlayerLevel(killer);
        // EX Tier 5 -> 500XP
        int xp = mob_level * 15 + new Random().nextInt(50) + 5;
        double multiplier = 1D;
        if (level > mob_level) {
            // Higher level so 70% XP
            multiplier *= .7D;
        }
        xp *= multiplier;
        addXP(killer, xp);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        // No delay on the sql query
        getPlayerData(e.getPlayer()).saveData(false, true);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent e) {
        PlayerManager.getPlayerModel(e.getPlayer().getName()).getPlayerLevel().setPlayer(e.getPlayer());
        Main.d("SETTING THE PLAYERS PLAYER DATA!", CC.RED);
    }

    public static void addXP(Player p, int xp) {
        getPlayerData(p).addXP(xp);
    }

    public static boolean canPlayerUseTier(Player p, int tier) {
        int level = getPlayerLevel(p);
        if (tier == 1 || tier == 2 && level >= 20 || tier == 3 && level >= 40 || tier == 4 && level >= 60 || tier == 5 && level >= 80) {
            return true;
        }
        return false;
    }

    public static PlayerLevel getPlayerData(Player p) {
        if (PlayerManager.getPlayerModel(p.getName()).getPlayerLevel() != null) {
            PlayerLevel pl = new PlayerLevel(p.getName(), true);
            PlayerManager.getPlayerModel(p).setPlayerLevel(pl);
            return pl;
        }
        return PlayerManager.getPlayerModel(p.getName()).getPlayerLevel();
    }

    public static PlayerLevel getPlayerData(String p_name) {
        if (PlayerManager.getPlayerModel(p_name).getPlayerLevel() != null) {
            return new PlayerLevel(p_name, true);
        }
        return PlayerManager.getPlayerModel(p_name).getPlayerLevel();
    }

    public static int getPlayerLevel(Player p) {
        if (PlayerManager.getPlayerModel(p).getPlayerLevel() == null) {
            // Create an instance of it
            return new PlayerLevel(p.getName(), true).getLevel();
        }
        if (PlayerManager.getPlayerModel(p) == null) {
            System.out.print("PLAYER MODEL WAS NULL!");
            return 0;
        }
        if (PlayerManager.getPlayerModel(p).getPlayerLevel() == null) {
            System.out.print("PLAYER LEVEL WAS NULL IN PLAYER MODEL!");
            return 0;
        }
        return PlayerManager.getPlayerModel(p).getPlayerLevel().getLevel();
    }

    public static int getPlayerLevel(String p_name) {
        if (PlayerManager.getPlayerModel(p_name).getPlayerLevel() != null) {
            return new PlayerLevel(p_name, true).getLevel();
        }
        return PlayerManager.getPlayerModel(p_name).getPlayerLevel().getLevel();
    }
}
