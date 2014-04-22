package me.vaqxine.BossMechanics;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import me.vaqxine.Main;
import me.vaqxine.MonsterMechanics.MonsterMechanics;
import me.vaqxine.enums.CC;
import me.vaqxine.managers.PlayerManager;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class AceronListener implements Listener {

    public ConcurrentHashMap<Entity, Entity> aceron_wolf = new ConcurrentHashMap<Entity, Entity>();
    public HashSet<Entity> spawned_wolf = new HashSet<Entity>();
    public ConcurrentHashMap<String, Long> players_greedy = new ConcurrentHashMap<String, Long>();

    @EventHandler
    public void minionDeath(EntityDeathEvent e) {
        if (e.getEntity().getWorld().getName().toLowerCase().contains("onewolfedungeon")) {
            // Dungeon
            if (BossMechanics.boss_map.containsKey(e.getEntity())) {
                if (e.getEntity() instanceof Wolf) {
                    if (aceron_wolf.containsValue(e.getEntity())) {
                        for (Entry<Entity, Entity> aceron_wolves : aceron_wolf.entrySet()) {
                            if (aceron_wolves.getValue().equals(e.getEntity())) {
                                aceron_wolf.remove(aceron_wolves.getKey());
                                BossMechanics.boss_map.remove(e.getEntity());
                                for (Player p : e.getEntity().getWorld().getPlayers()) {
                                    p.sendMessage(ChatColor.GOLD.toString() + ChatColor.UNDERLINE + "Aceron the Wicked:" + ChatColor.WHITE
                                            + " Noooo, I will avenge you Diner!");
                                    p.sendMessage(ChatColor.RED + "Aceron now has 1.5x armor!");
                                }
                                MonsterMechanics.mob_armor.put(e.getEntity(), (int) (MonsterMechanics.mob_armor.get(e.getEntity()) * 1.5));
                                break;
                            }
                        }
                    }
                }
                return;
            }
            for (Entry<Entity, List<Entity>> minions : BossMechanics.aceron_minions.entrySet()) {
                Entity boss = minions.getKey();
                List<Entity> ents = minions.getValue();
                List<Entity> to_remove = new ArrayList<Entity>();
                if (!ents.contains(e.getEntity()))
                    continue;
                ents.remove(e.getEntity());
                if (ents.size() <= 0) {
                    for (Player p : e.getEntity().getWorld().getPlayers()) {
                        p.sendMessage(ChatColor.GOLD + "" + ChatColor.UNDERLINE + "Aceron the Wicked:" + ChatColor.WHITE + " Lets get this over with!");
                    }
                    boss.teleport(BossMechanics.boss_saved_location.get(boss));
                    BossMechanics.boss_saved_location.remove(boss);
                    BossMechanics.aceron_minions.remove(boss);
                    BossMechanics.invincible_mob.remove(boss);
                } else {
                    for (Entity es : ents) {
                        if (es == null || es.isDead()) {
                            to_remove.add(es);
                        }

                    }
                    for (Entity remove : to_remove) {
                        ents.remove(remove);
                    }
                    if (ents.size() <= 0) {
                        boss.teleport(BossMechanics.boss_saved_location.get(boss));
                        BossMechanics.boss_saved_location.remove(boss);
                        BossMechanics.aceron_minions.remove(boss);
                        BossMechanics.invincible_mob.remove(boss);
                        return;
                    }
                    // Avoid making bugs

                    BossMechanics.aceron_minions.put(boss, ents);
                    BossMechanics.invincible_mob.add(boss);
                }
                break;
            }

        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        if (!areLocationsEqual(e.getTo(), e.getFrom())) {
            Player p = e.getPlayer();
            if (players_greedy.containsKey(p.getName())) {
                if (players_greedy.get(p.getName()) <= System.currentTimeMillis()) {
                    players_greedy.remove(p.getName());
                    return;
                }
                e.setCancelled(true);
                p.sendMessage(ChatColor.RED + "You are overcome with greed.");
                p.teleport(e.getFrom());
            }
        }
    }

    @EventHandler
    public void onPlayerLeaveResetGreed(PlayerQuitEvent e) {
        if (players_greedy.containsKey(e.getPlayer().getName())) {
            players_greedy.remove(e.getPlayer().getName());
            return;
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void playerPickupGreedy(PlayerPickupItemEvent e) {
        Player p = e.getPlayer();
        if (e.getItem().hasMetadata("greedy")) {
            e.setCancelled(true);
            e.getItem().remove();
            p.sendMessage(ChatColor.RED + "You have been overcome with greed for 5 seconds.");
            players_greedy.put(p.getName(), System.currentTimeMillis() + (5 * 1000));
            return;
        }
    }

    public boolean areLocationsEqual(Location first, Location second) {
        if ((first.getBlockX() == second.getBlockX()) && (first.getBlockY() == second.getBlockY()) && (first.getBlockZ() == second.getBlockZ())) {
            return true;
        }
        return false;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onWolfDamage(EntityDamageByEntityEvent e) {
        if (!(e.getEntity() instanceof Player))
            return;
        if (!(e.getDamager() instanceof Wolf))
            return;
        Entity wolf = e.getDamager();
        if (e.getDamage() <= 0 || e.isCancelled()) {
            return;
        }
        Player p = (Player) e.getEntity();
        if (aceron_wolf.containsValue(wolf)) {
            Entity boss = null;
            for (Entry<Entity, Entity> wolves : aceron_wolf.entrySet()) {
                if (wolves.getValue().equals(wolf)) {
                    boss = wolves.getKey();
                    break;
                }
            }
            if (boss == null) {
                return;
            }
            int should_i_crit = new Random().nextInt(100);
            int should_i_frenzy = new Random().nextInt(100);
            boolean critted = false;
            if (should_i_crit <= 30) {
                // Crit dat bitch
                // 50% crit
                e.setDamage(e.getDamage() * 1.5);
                if (PlayerManager.getPlayerModel(p).getToggleList() != null && PlayerManager.getPlayerModel(p).getToggleList().contains("debug")) {
                    p.sendMessage(ChatColor.RED + "          ** WOLF -> CRITICAL STIKE **");
                    p.playSound(p.getLocation(), Sound.BURP, 1, .3F);
                }
                critted = true;
            }
            if (should_i_frenzy <= 20 && !critted) {
                // Hits are throttled so it doesnt just double damage.
                if (PlayerManager.getPlayerModel(p).getToggleList() != null && PlayerManager.getPlayerModel(p).getToggleList().contains("debug")) {
                    p.sendMessage(ChatColor.RED + "          ** WOLF -> FRENZIED **");
                    p.playSound(p.getLocation(), Sound.BURP, 1, .3F);
                }
                e.setDamage(e.getDamage() * 2);
            }
            int amount_to_heal =  (int)(e.getDamage() * .2D);
            // Heal the boss
            Main.d("Healed the boss " + boss + " by " + CC.GREEN + amount_to_heal + " OLD HP: " + MonsterMechanics.getMHealth(boss) + " NEW HP: "
                    + (MonsterMechanics.getMHealth(boss) + amount_to_heal));
            MonsterMechanics.mob_health.put(boss, (MonsterMechanics.getMHealth(boss) + amount_to_heal));

            // p.sendMessage(ChatColor.GOLD.toString() + ChatColor.UNDERLINE + "Aceron the Wicked:" + ChatColor.WHITE +
            // " Thank you Diner, their health is much appreciated!") ;
        }
    }

    @EventHandler
    public void onPlayerDamageWithGreed(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Player)) {
            return;

        }
        Player p = (Player) e.getDamager();
        if (players_greedy.containsKey(p.getName())) {
            e.setCancelled(true);
            e.setDamage(0);
            return;
        }
    }

    @EventHandler
    public void onPlayerClick(PlayerInteractEvent e) {
        if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (players_greedy.contains(e.getPlayer().getName())){
                e.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDamage(EntityDamageEvent e) {
        if (e.getEntity().getWorld().getName().toLowerCase().contains("onewolfedungeon")) {
            if (BossMechanics.boss_map.containsKey(e.getEntity()) && BossMechanics.boss_map.get(e.getEntity()).equalsIgnoreCase("aceron")) {
                if (BossMechanics.invincible_mob.contains(e.getEntity()) || BossMechanics.aceron_minions.containsKey(e.getEntity())) {
                    e.setCancelled(true);
                    e.setDamage(0);
                    return;
                }
                if (e.getDamage() <= 0)
                    return;
                Entity boss = e.getEntity();
                int cur_hp = MonsterMechanics.getMHealth(boss);
                int max_hp = MonsterMechanics.getMaxMobHealth(boss);
                // Stupid Java Arithmetic -_-
                double percent_hp = (1.0f * cur_hp / max_hp) * 100;
                int should_i_retreat = new Random().nextInt(100);
                if (should_i_retreat <= 5 && !BossMechanics.is_jumping.contains(boss) && percent_hp > .5) { // 5% chance of that
                    BossMechanics.boss_saved_location.put(boss, boss.getLocation());
                    List<Entity> ents = new ArrayList<Entity>();
                    int amount_to_spawn = new Random().nextInt(2) + 3;
                    for (int i = 0; i < amount_to_spawn; i++) {
                        ents.add(MonsterMechanics.spawnTierMob(boss.getLocation(), Math.random() >= .5D ? EntityType.SKELETON : EntityType.ZOMBIE, 4, -1,
                                boss.getLocation(), false, "", "Greedy Slaves", true, 3));
                    }
                    BossMechanics.aceron_minions.put(boss, ents);
                    BossMechanics.invincible_mob.add(boss);
                    boss.teleport(new Location(boss.getWorld(), -52, 203, 18));
                    for (Player p : e.getEntity().getWorld().getPlayers()) {
                        p.sendMessage(ChatColor.GOLD + "" + ChatColor.UNDERLINE + "Aceron the Wicked:" + ChatColor.WHITE + " Ill be back!");
                    }
                    return;
                }
                
                if (percent_hp <= 30 && !BossMechanics.is_jumping.contains(boss) && !BossMechanics.invincible_mob.contains(boss)
                        && !spawned_wolf.contains(boss)) {
                    // They did 30% HP on aceron
                    Wolf wolf = (Wolf) MonsterMechanics.spawnBossMob(boss.getLocation(), EntityType.WOLF, "", "Diner of Bones", 4);
                    aceron_wolf.put(boss, wolf);
                    spawned_wolf.add(boss);
                    wolf.setAngry(true);
                    for (Player p : boss.getWorld().getPlayers()) {
                        p.sendMessage(ChatColor.GOLD.toString() + ChatColor.UNDERLINE + "Aceron the Wicked:" + ChatColor.WHITE + " Dinner is served.");
                    }

                }
            }
        }
    }
}
