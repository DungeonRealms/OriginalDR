package me.ifamasssxd.levelmechanics;

import java.util.Random;

import me.vaqxine.Main;
import me.vaqxine.MonsterMechanics.MonsterMechanics;
import me.vaqxine.managers.PlayerManager;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class LevelMechanics implements Listener {

    // Player name, PlayerLevel data

    @EventHandler
    public void onAsyncLogin(AsyncPlayerPreLoginEvent e) {
        new PlayerLevel(e.getName(), false);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDeathEvent(EntityDamageByEntityEvent e) {
        if (e.getEntity() instanceof Player)
            return;
        if (e.getDamage() <= 0) {
            return;
        }
        if ((MonsterMechanics.getMHealth(e.getEntity()) - e.getDamage()) <= 0) {
            int mob_level = MonsterMechanics.getMobLevel(e.getEntity());
            Main.d(mob_level);
            if (!(e.getDamager() instanceof Player))
                return;
            Player killer = (Player) e.getDamager();
            int level = getPlayerLevel(killer);
            int xp = mob_level * 15 + new Random().nextInt(50) + 5;
            if ((level + 5) < mob_level) {
                // No XP
                xp = 0;
            }

            double multiplier = 1D;
            if (level > mob_level) {
                // Higher level so 70% XP

                multiplier *= .7D;
            }
            xp *= multiplier;
            addXP(killer, xp);
        }

    }

    public static int getLevelToUse(int tier) {
        if (tier == 1) {
            return 1;
        } else if (tier == 2) {
            return 20;
        } else if (tier == 3) {
            return 40;
        } else if (tier == 4) {
            return 60;
        } else if (tier == 5) {
            return 80;
        }
        return 1;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        // No delay on the sql query
        getPlayerData(e.getPlayer()).saveData(false, true);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        PlayerManager.getPlayerModel(e.getPlayer().getName()).getPlayerLevel().setPlayer(e.getPlayer());
        // Main.d("SETTING THE PLAYERS PLAYER DATA!", CC.RED);
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

    public static int getPlayerTier(Player p) {
        int level = getPlayerLevel(p);
        if (level < 20) {
            return 1;
        } else if (level >= 20 && level < 40) {
            return 2;
        } else if (level >= 40 && level < 60) {
            return 3;
        } else if (level >= 60 && level < 80) {
            return 4;
        } else if (level >= 80) {
            return 5;
        }
        return 1;
    }

    public static PlayerLevel getPlayerData(Player p) {
        if (PlayerManager.getPlayerModel(p.getName()).getPlayerLevel() == null) {
            PlayerLevel pl = new PlayerLevel(p.getName(), true);
            PlayerManager.getPlayerModel(p).setPlayerLevel(pl);
            return pl;
        }
        return PlayerManager.getPlayerModel(p.getName()).getPlayerLevel();
    }

    public static PlayerLevel getPlayerData(String p_name) {
        if (PlayerManager.getPlayerModel(p_name).getPlayerLevel() == null) {
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
        if (PlayerManager.getPlayerModel(p_name).getPlayerLevel() == null) {
            return new PlayerLevel(p_name, true).getLevel();
        }
        return PlayerManager.getPlayerModel(p_name).getPlayerLevel().getLevel();
    }
}
