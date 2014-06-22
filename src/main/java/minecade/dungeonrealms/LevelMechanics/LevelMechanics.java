package minecade.dungeonrealms.LevelMechanics;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import minecade.dungeonrealms.Main;
import minecade.dungeonrealms.InstanceMechanics.InstanceMechanics;
import minecade.dungeonrealms.LevelMechanics.StatsGUI.ConfirmItem;
import minecade.dungeonrealms.LevelMechanics.StatsGUI.DexterityItem;
import minecade.dungeonrealms.LevelMechanics.StatsGUI.DexterityStatsItem;
import minecade.dungeonrealms.LevelMechanics.StatsGUI.EmptySlot;
import minecade.dungeonrealms.LevelMechanics.StatsGUI.IntellectItem;
import minecade.dungeonrealms.LevelMechanics.StatsGUI.IntellectStatsItem;
import minecade.dungeonrealms.LevelMechanics.StatsGUI.StatsGUI;
import minecade.dungeonrealms.LevelMechanics.StatsGUI.StatsInfoItem;
import minecade.dungeonrealms.LevelMechanics.StatsGUI.StrengthItem;
import minecade.dungeonrealms.LevelMechanics.StatsGUI.StrengthStatsItem;
import minecade.dungeonrealms.LevelMechanics.StatsGUI.VitalityItem;
import minecade.dungeonrealms.LevelMechanics.StatsGUI.VitalityStatsItem;
import minecade.dungeonrealms.LevelMechanics.commands.CommandAddXP;
import minecade.dungeonrealms.LevelMechanics.commands.CommandNotice;
import minecade.dungeonrealms.LevelMechanics.commands.CommandStats;
import minecade.dungeonrealms.MonsterMechanics.Hologram;
import minecade.dungeonrealms.MonsterMechanics.MonsterMechanics;
import minecade.dungeonrealms.PartyMechanics.PartyMechanics;
import minecade.dungeonrealms.jsonlib.JSONMessage;
import minecade.dungeonrealms.managers.PlayerManager;
import minecade.dungeonrealms.models.PlayerModel;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
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
import org.bukkit.scheduler.BukkitRunnable;

public class LevelMechanics implements Listener {
	
    private JSONMessage freePointsNotice;

    // Player name, PlayerLevel data
    public LevelMechanics(){
        new BukkitRunnable() {
            public void run() {
                for(Player p : Bukkit.getOnlinePlayers()){
                    getPlayerData(p).updateScoreboardLevel();
                }
            }
        }.runTaskTimer(Main.plugin, 100, 10 * 20);
        new BukkitRunnable() {
			public void run() {
        		for (PlayerModel p : PlayerManager.getPlayerModels()) {
        			PlayerLevel pLevel = p.getPlayerLevel();
					if (pLevel != null && pLevel.getFreePoints() > 0) {
						if (pLevel.getTmrSecs() > 0) {
							pLevel.tickTmr();
						}
						else if (!p.getPlayer().getOpenInventory().getTitle().equalsIgnoreCase("Allocate Stat points")) {
							pLevel.sendStatNoticeToPlayer(p.getPlayer());
							pLevel.setTmrSecs(180);
						}
					}
        		}
        	}
        }.runTaskTimer(Main.plugin, 0, 1 * 20);
        String before = PlayerLevel.FREE_STAT_NOTICE.split("Click here")[0];
        String after = PlayerLevel.FREE_STAT_NOTICE.split("Click here")[1];
        freePointsNotice = new JSONMessage("", ChatColor.GRAY);
        freePointsNotice.addText(before);
		freePointsNotice.addRunCommand("Click " + ChatColor.UNDERLINE.toString() + ChatColor.BOLD + "HERE", ChatColor.GREEN,
				"/stats");
        freePointsNotice.addText(after, ChatColor.GREEN);
    }

    public void onEnable(){
        Main.plugin.getCommand("addxp").setExecutor(new CommandAddXP());
        Main.plugin.getCommand("stats").setExecutor(new CommandStats());
        Main.plugin.getCommand("statsnotice").setExecutor(new CommandNotice());
        
        // register items for stats GUI
        new StrengthItem().registerItem();
        new DexterityItem().registerItem();
        new IntellectItem().registerItem();
        new VitalityItem().registerItem();
        new ConfirmItem().registerItem();
        new EmptySlot().registerItem();
        new StrengthStatsItem().registerItem();
        new DexterityStatsItem().registerItem();
        new IntellectStatsItem().registerItem();
        new VitalityStatsItem().registerItem();
        new StatsInfoItem().registerItem();
        
        // register menu for stats GUI
        new StatsGUI();
    }
    
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

            addKillXP(killer, (LivingEntity) e.getEntity(), mob_level, true);
        }

    }

    @SuppressWarnings("deprecation")
    public static void addKillXP(Player player, LivingEntity kill, int mob_level, boolean first) {
        int level = getPlayerLevel(player);

        if (first) {
            if (PartyMechanics.party_map.containsKey(player.getName())) {
                List<String> members = PartyMechanics.party_map.get(player.getName()).getPartyMembers();
                Location v = player.getLocation();
                for (String s : members) {
                    if (s == player.getName())
                        continue;
                    Player p = Bukkit.getPlayerExact(s);
                    if (p == null)
                        continue;
                    Location l = p.getLocation();
                    if (l.getX() >= v.getX() - 20 && l.getX() <= v.getX() + 20) {
                        if (l.getZ() >= v.getZ() - 20 && l.getZ() <= v.getZ() + 20) {
                            addKillXP(p, kill, mob_level, false);
                        }
                    }
                }
            }
        }

        if (mob_level > (level + 10)) {
            if (PlayerManager.getPlayerModel(player).getToggleList() != null && PlayerManager.getPlayerModel(player).getToggleList().contains("debug")) {
                player.sendMessage(ChatColor.RED + "Your level was " + ChatColor.UNDERLINE + "lower" + ChatColor.RED
                        + " than 10 levels of this mob. No EXP granted.");
            }
            return;
        } else if (mob_level < (level - 8)) {
            /*if (PlayerManager.getPlayerModel(player).getToggleList() != null && PlayerManager.getPlayerModel(player).getToggleList().contains("debug")) {
                player.sendMessage(ChatColor.RED + "Your level was " + ChatColor.UNDERLINE + "greater" + ChatColor.RED
                        + " than 8 levels of this mob. No EXP granted.");
            }
            return; Disabled by Mayley's request */
        }

        int xp = calculateXP(player, kill, mob_level);

        if (PlayerManager.getPlayerModel(player).getToggleList() != null && PlayerManager.getPlayerModel(player).getToggleList().contains("indicator")) {
            Hologram xp_hologram = new Hologram(Main.plugin, ChatColor.GREEN.toString() + "+" + ChatColor.BOLD + xp + " XP");
            xp_hologram.show(kill.getLocation().add(0, 1, 0), 3, player);
        }

        addXP(player, xp);
    }

    public static int calculateXP(Player player, LivingEntity kill, int mob_level) {
    	int xp = (int) Math.round((6.5 * Math.pow(mob_level,1.35)) + 40 + new Random().nextInt(50));
        //int xp = mob_level * 15 + new Random().nextInt(50) + 5;
        //int level = getPlayerLevel(player);
        ItemStack weapon = kill.getEquipment().getItemInHand();

        //if (level - 8 > mob_level)
            //return 0;
        if (weapon.getEnchantments().containsKey(Enchantment.KNOCKBACK))
            xp *= 1.5;

        return xp;
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
    public void onPlayerJoin(final PlayerJoinEvent e) {
    	final PlayerLevel PLAYER_LEVEL = PlayerManager.getPlayerModel(e.getPlayer()).getPlayerLevel();
        PLAYER_LEVEL.setPlayer(e.getPlayer());
        new BukkitRunnable() {
            public void run() {
            	if(e == null || e.getPlayer() == null) return;
                PLAYER_LEVEL.updateScoreboardLevel();
            }
        }.runTaskLater(Main.plugin, 20 * 1);
        Main.plugin.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
			public void run() {
				if (PLAYER_LEVEL.getFreePoints() > 0) {
					PLAYER_LEVEL.sendStatNoticeToPlayer(e.getPlayer());
				}
			}
        }, 15L); // 15 ticks so the notice comes after motd, sub days, etc.
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
    }

    public static void addXP(Player p, int xp) {
        if(InstanceMechanics.isInstance(p.getWorld().getName())){
            getPlayerData(p).addXP(xp);
            return;
        }
        if (PartyMechanics.party_map.containsKey(p.getName())) {
        	if (PartyMechanics.party_map.get(p.getName()).getPartyMembers().size() >= 1) {
        		int newXP = (int) Math.round((xp * (1 - (0.1D * (PartyMechanics.party_map.get(p.getName()).getPartyMembers().size() - 1)))));
        		xp = newXP;
        	}
        }

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

    /**
     * Returns a list of the players with free stat points on this shard.
     */
	public static List<String> getPlayersWithFreeStatPoints() {
		List<String> players = new ArrayList<String>();
		for (PlayerModel p : PlayerManager.getPlayerModels()) {
			if (p.getPlayerLevel().getFreePoints() > 0) {
				players.add(p.getPlayer().getName());
			}
		}
		return players;
	}

	public JSONMessage getFreePointsNotice() {
		return freePointsNotice;
	}

	public void setFreePointsNotice(JSONMessage freePointsNotice) {
		this.freePointsNotice = freePointsNotice;
	}
	
}
