package me.vaqxine.LevelMechanics;

import java.util.Random;

import me.vaqxine.Main;
import me.vaqxine.MonsterMechanics.Hologram;
import me.vaqxine.MonsterMechanics.MonsterMechanics;
import me.vaqxine.managers.PlayerManager;

import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

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
        if (!MonsterMechanics.isHostile(e.getEntity().getType())) {
            return;
        }
        if ((MonsterMechanics.getMHealth(e.getEntity()) - e.getDamage()) <= 0) {
            int mob_level = MonsterMechanics.getMobLevel(e.getEntity());
            Main.d(mob_level);
            if (!(e.getDamager() instanceof Player) && !(e.getDamager() instanceof Projectile))
                return;
            Player killer = null;
            if (e.getDamager() instanceof Projectile) {
                if (!(((Projectile) e.getDamager()).getShooter() instanceof Player)) {
                    return;
                }
                killer = (Player) ((Projectile) e.getDamager()).getShooter();
            } else {
                killer = (Player) e.getDamager();
            }
            if (MonsterMechanics.getEntityDamageTracker(e.getEntity()) != null) {
                Player actual_killer = MonsterMechanics.getEntityDamageTracker(e.getEntity()).getMostDamageDone();
                if (actual_killer != null) {
                    if (actual_killer != null && actual_killer.isOnline() && actual_killer != killer) {
                        killer.sendMessage(ChatColor.RED + actual_killer.getName() + " has dealt more damage to this mob then you. Thus they received the XP.");
                        killer = actual_killer;
                    }
                }
            }
            PlayerLevel pl = getPlayerData(killer);
            if (pl.getLastEntityKilled() != null && pl.getLastEntityKilled().equals(e.getEntity())) {
                return;
            }
            pl.setLastEntityKilled(e.getEntity());
            int level = getPlayerLevel(killer);
            int xp = mob_level * 15 + new Random().nextInt(50) + 5;
            if ((level + 8) <= (mob_level)) {
                // No XP
                killer.sendMessage(ChatColor.RED + "Your level was " + ChatColor.UNDERLINE + "greater" + ChatColor.RED
                        + " then 8 levels of this mob. No EXP granted.");
                xp = 0;
            }
            if (level >= (mob_level + 8)) {
                // mob_level = 10 p_level = 15
                // .1 * (15-10 -> 5)
                killer.sendMessage(ChatColor.RED + "Your level was " + ChatColor.UNDERLINE + "less" + ChatColor.RED
                        + " then 8 levels of this mob. 60% EXP granted.");
                xp *= .6;
            }
            boolean is_elite = false;
            ItemStack weapon = ((LivingEntity) e.getEntity()).getEquipment().getItemInHand();
            if (weapon.getEnchantments().containsKey(Enchantment.KNOCKBACK)) {
                // log.info("ELITE!");
                is_elite = true;
            }
            if (is_elite) {
                // 2.3x XP for elites
                xp *= 2.3;
            }
            if (PlayerManager.getPlayerModel(killer).getToggleList() != null && PlayerManager.getPlayerModel(killer).getToggleList().contains("indicator")) {
                Hologram xp_hologram = new Hologram(Main.plugin, ChatColor.GREEN.toString() + "+" + ChatColor.BOLD + xp + " XP");
                xp_hologram.show(e.getEntity().getLocation().add(0, 1, 0), 3, killer);
            }
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
        PlayerManager.getPlayerModel(e.getPlayer()).getPlayerLevel().setPlayer(e.getPlayer());
        PlayerManager.getPlayerModel(e.getPlayer()).getPlayerLevel().updateScoreboardLevel();
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
