package minecade.dungeonrealms.LevelMechanics;

import java.util.ArrayList;
import java.util.List;

import me.vilsol.menuengine.engine.DynamicMenuModel;
import me.vilsol.menuengine.engine.MenuItem;
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
import minecade.dungeonrealms.RealmMechanics.RealmMechanics;
import minecade.dungeonrealms.jsonlib.JSONMessage;
import minecade.dungeonrealms.managers.PlayerManager;
import minecade.dungeonrealms.models.PlayerModel;

import org.apache.commons.lang.math.NumberUtils;
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
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
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
        		for (Player p : Bukkit.getOnlinePlayers()) {
        		    if (p == null || PlayerManager.getPlayerModel(p) == null)
        		        continue;
        			PlayerLevel pLevel = PlayerManager.getPlayerModel(p).getPlayerLevel();
        			if (pLevel == null)
        			    continue;
					if (pLevel.getFreePoints() > 0) {
						if (pLevel.getTmrSecs() > 0) {
							pLevel.tickTmr();
						}
						else if ((p.getOpenInventory() == null || !ChatColor.stripColor(p.getPlayer().getOpenInventory().getTitle()).equalsIgnoreCase("Stat Points")) && pLevel.getNumWarnings() < 5) {
							pLevel.sendStatNoticeToPlayer(p.getPlayer());
							pLevel.setTmrSecs(180);
							pLevel.setNumWarnings(pLevel.getNumWarnings() + 1);
						}
						else if (ChatColor.stripColor(p.getPlayer().getOpenInventory().getTitle()).equalsIgnoreCase("Stat Points")) {
						    pLevel.setTmrSecs(180);
						}
					}
        		}
        	}
        }.runTaskTimer(Main.plugin, 0, 1 * 20);
        String before = PlayerLevel.FREE_STAT_NOTICE.split("HERE")[0];
        String after = PlayerLevel.FREE_STAT_NOTICE.split("HERE")[1];
        freePointsNotice = new JSONMessage("", ChatColor.GRAY);
        freePointsNotice.addText(before);
		freePointsNotice.addRunCommand(ChatColor.UNDERLINE.toString() + ChatColor.BOLD + "HERE", ChatColor.GREEN,
				"/stats");
        freePointsNotice.addText(after);
    }

    public void onEnable(){
        Main.plugin.getCommand("addxp").setExecutor(new CommandAddXP());
        Main.plugin.getCommand("stat").setExecutor(new CommandStats());
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
    
    @EventHandler (priority = EventPriority.LOWEST)
    public void onPlayerResetStatRespond(AsyncPlayerChatEvent e) {
        if (!( PlayerManager.getPlayerModel(e.getPlayer()).getPlayerLevel().isResetting())) return;
        
        Player p = e.getPlayer();
        PlayerLevel pLevel = PlayerManager.getPlayerModel(p).getPlayerLevel();
        String msg = e.getMessage();
        
        e.setCancelled(true);
        
        if (msg.equals(pLevel.getResetCode()) && RealmMechanics.doTheyHaveEnoughMoney(p, pLevel.getResetCost())) {
            RealmMechanics.subtractMoney(p, pLevel.getResetCost());
            pLevel.resetStatPoints();
            p.sendMessage(ChatColor.GREEN.toString() + ChatColor.BOLD + "            *** STAT POINTS RESET ***");
            pLevel.setNumResets(pLevel.getNumResets() + 1);
        }
        else if (msg.equals(pLevel.getResetCode())) {
            p.sendMessage(ChatColor.RED + "You do not have enough gems to reset your stats. Reset cancelled.");
            p.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "COST: " + ChatColor.RED + pLevel.getResetCost() + ChatColor.BOLD + "G");
        }
        else if (msg.equalsIgnoreCase("cancel")) {
            p.sendMessage(ChatColor.RED + "Stat Reset - " + ChatColor.BOLD + "CANCELLED");
        }
        else {
            p.sendMessage(ChatColor.RED + "Invalid code entered.  Stat Reset " + ChatColor.BOLD + "CANCELLED");
        }
        
        pLevel.setResetting(false);
    }
    
    @EventHandler (priority = EventPriority.LOWEST)
    public void onPlayerSpecifyStatPoints(AsyncPlayerChatEvent e) {
        if (PlayerManager.getPlayerModel(e.getPlayer()).getPlayerLevel().getAllocateSlot() != -1) {
            Player p = e.getPlayer();
            String msg = e.getMessage();
            e.setCancelled(true);
            if (NumberUtils.isNumber(msg) && !msg.equals("0")) {
                int points = Integer.parseInt(msg);
                MenuItem item = null;
                PlayerLevel pLevel = PlayerManager.getPlayerModel(p).getPlayerLevel();
                
                if (DynamicMenuModel.getLastMenuObject(p) != null) {
                    item = DynamicMenuModel.getLastMenuObject(p).getDynamicItems()
                            .get(PlayerManager.getPlayerModel(p).getPlayerLevel().getAllocateSlot());
                }
                if (item == null) {
                    return;
                }
                
                if (points < 0) {
                    if (item instanceof StrengthItem) {
                        if (Math.abs(points) <= ((StrengthItem) item).getPoints() - pLevel.getStrPoints()) {
                            ((StrengthItem) item).setPoints(((StrengthItem) item).getPoints() + points);
                        }
                        else if (Math.abs(points) > ((StrengthItem) item).getPoints() - pLevel.getStrPoints()) {
                            p.sendMessage(ChatColor.RED + "You do " + ChatColor.BOLD + "not" + ChatColor.RED
                                    + " have enough allocated points to do this.");
                            DynamicMenuModel.openLastMenuObject(p);
                            PlayerManager.getPlayerModel(p).getPlayerLevel().setAllocateSlot(-1);
                            return;
                        }
                    }
                    else if (item instanceof DexterityItem) {
                        if (Math.abs(points) <= ((DexterityItem) item).getPoints() - pLevel.getDexPoints()) {
                            ((DexterityItem) item).setPoints(((DexterityItem) item).getPoints() + points);
                        }
                        else {
                            p.sendMessage(ChatColor.RED + "You do " + ChatColor.BOLD + "not" + ChatColor.RED
                                    + " have enough allocated points to do this.");
                            DynamicMenuModel.openLastMenuObject(p);
                            PlayerManager.getPlayerModel(p).getPlayerLevel().setAllocateSlot(-1);
                            return;
                        }
                    }
                    else if (item instanceof IntellectItem) {
                        if (Math.abs(points) <= ((IntellectItem) item).getPoints() - pLevel.getIntPoints()) {
                            ((IntellectItem) item).setPoints(((IntellectItem) item).getPoints() + points);
                        }
                        else {
                            p.sendMessage(ChatColor.RED + "You do " + ChatColor.BOLD + "not" + ChatColor.RED
                                    + " have enough allocated points to do this.");
                            DynamicMenuModel.openLastMenuObject(p);
                            PlayerManager.getPlayerModel(p).getPlayerLevel().setAllocateSlot(-1);
                            return;
                        }
                    }
                    else if (item instanceof VitalityItem) {
                        if (Math.abs(points) <= ((VitalityItem) item).getPoints() - pLevel.getVitPoints()) {
                            ((VitalityItem) item).setPoints(((VitalityItem) item).getPoints() + points);
                        }
                        else {
                            p.sendMessage(ChatColor.RED + "You do " + ChatColor.BOLD + "not" + ChatColor.RED
                                    + " have enough allocated points to do this.");
                            DynamicMenuModel.openLastMenuObject(p);
                            PlayerManager.getPlayerModel(p).getPlayerLevel().setAllocateSlot(-1);
                            return;
                        }
                    }
                    
                    p.sendMessage(ChatColor.RED + "Unallocated " + ChatColor.BOLD.toString() + ChatColor.UNDERLINE + Math.abs(points)
                            + ChatColor.RED + (points > 1 ? " points " : " point ") + "from "
                            + ChatColor.stripColor(item.getItem().getItemMeta().getDisplayName()).toUpperCase() + ".");
                    pLevel.setFreePoints(pLevel.getFreePoints() + Math.abs(points));
                    DynamicMenuModel.openLastMenuObject(p);
                }
                else {
                    
                    if (!(pLevel.getTempFreePoints() >= points)) {
                        p.sendMessage(ChatColor.RED + "You do " + ChatColor.BOLD + "not" + ChatColor.RED
                                + " have enough points to do this.");
                        PlayerManager.getPlayerModel(p).getPlayerLevel().setAllocateSlot(-1);
                        DynamicMenuModel.openLastMenuObject(p);
                        return;
                    }
                    
                    if (item instanceof StrengthItem) {
                        if (((StrengthItem) item).getPoints() + points <= 600) {
                            ((StrengthItem) item).setPoints(((StrengthItem) item).getPoints() + points);
                        }
                        else {
                            p.sendMessage(ChatColor.RED + "Allocating " + msg + " points would exceed the 600 point limit.  Please input a lower number, or type cancel.");
                            return;
                        }
                    }
                    else if (item instanceof DexterityItem) {
                        if (((DexterityItem) item).getPoints() + points <= 600) {
                            ((DexterityItem) item).setPoints(((DexterityItem) item).getPoints() + points);
                        }
                        else {
                            p.sendMessage(ChatColor.RED + "Allocating " + msg + " points would exceed the 600 point limit.  Please input a lower number, or type cancel.");
                            return;
                        }
                    }
                    else if (item instanceof IntellectItem) {
                        if (((IntellectItem) item).getPoints() + points <= 600) {
                            ((IntellectItem) item).setPoints(((IntellectItem) item).getPoints() + points);
                        }
                        else {
                            p.sendMessage(ChatColor.RED + "Allocating " + msg + " points would exceed the 600 point limit.  Please input a lower number, or type cancel.");
                            return;
                        }
                    }
                    else if (item instanceof VitalityItem) {
                        if (((VitalityItem) item).getPoints() + points <= 600) {
                            ((VitalityItem) item).setPoints(((VitalityItem) item).getPoints() + points);
                        }
                        else {
                            p.sendMessage(ChatColor.RED + "Allocating " + msg + " points would exceed the 600 point limit.  Please input a lower number, or type cancel.");
                            return;
                        }
                    }
                    
                    p.sendMessage(ChatColor.GREEN + "Allocated " + ChatColor.BOLD.toString() + ChatColor.UNDERLINE + msg
                            + ChatColor.GREEN + (points > 1 ? " points " : " point ") + "to "
                            + ChatColor.stripColor(item.getItem().getItemMeta().getDisplayName()).toUpperCase() + ".");
                    pLevel.setTempFreePoints(pLevel.getTempFreePoints() - points);
                    DynamicMenuModel.openLastMenuObject(p);
                }
                
            }
            else if (msg.equalsIgnoreCase("cancel") || msg.equals("0")) {
                p.sendMessage(ChatColor.RED + "Stat Allocation - " + ChatColor.BOLD + "CANCELLED");
                DynamicMenuModel.openLastMenuObject(p);
            }
            else {
                p.sendMessage(ChatColor.RED + "Invalid input.  Please specify a number or type cancel.");
                return;
            }
            // reset this so player can chat
            PlayerManager.getPlayerModel(p).getPlayerLevel().setAllocateSlot(-1);
        }
    }
    
    @EventHandler
    public void onLevelResetNPCInteract(PlayerInteractEntityEvent e) {
        if (!(e.getRightClicked() instanceof Player)) return;
        Player trader = (Player) e.getRightClicked();
        if (!(trader.hasMetadata("NPC"))) return;
        if (!(ChatColor.stripColor(trader.getName()).equalsIgnoreCase("Wizard"))) return;
        e.setCancelled(true);
        Player p = e.getPlayer(); 
        PlayerLevel pLevel = PlayerManager.getPlayerModel(p).getPlayerLevel();
        if (pLevel.isResetting()) return;
        pLevel.setResetCode(pLevel.generateResetAuthenticationCode(p, String.valueOf(pLevel.getNumResets() + 1)));
        int resetCost = (int) ((1000. * Math.pow(1.8, (pLevel.getNumResets() + 1))) - ((1000. * Math.pow(1.8, (pLevel.getNumResets() + 1))) % 1000));
        pLevel.setResetCost(resetCost > 60000 ? 60000 : (int) ((1000. * Math.pow(1.8, (pLevel.getNumResets() + 1))) - ((1000. * Math.pow(1.8, (pLevel.getNumResets() + 1))) % 1000)));
        p.sendMessage("");
        p.sendMessage(ChatColor.DARK_GRAY + "           *** " + ChatColor.GREEN + ChatColor.BOLD + "Stat Reset Confirmation" + ChatColor.DARK_GRAY + " ***");
        p.sendMessage(ChatColor.DARK_GRAY + "           TOTAL Points: " + ChatColor.GREEN + pLevel.getLevel() * PlayerLevel.POINTS_PER_LEVEL + ChatColor.DARK_GRAY + "          SPENT Points: " + ChatColor.GREEN + (pLevel.getLevel() * PlayerLevel.POINTS_PER_LEVEL - pLevel.getFreePoints()));
        // p.sendMessage(ChatColor.DARK_GRAY + "FROM Tier " + ChatColor.GREEN + bank_tier + ChatColor.DARK_GRAY + " TO " + ChatColor.GREEN +
        // next_bank_tier);
        p.sendMessage(ChatColor.DARK_GRAY + "                  Reset Cost: " + ChatColor.GREEN + "" + pLevel.getResetCost() + " Gem(s)");
        p.sendMessage("");
        p.sendMessage(ChatColor.GREEN + "Enter the code '" + ChatColor.BOLD + pLevel.getResetCode() + ChatColor.GREEN + "' to confirm your reset.");
        p.sendMessage("");
        p.sendMessage("" + ChatColor.RED + ChatColor.BOLD + "WARNING:" + ChatColor.RED + " Stat resets are " + ChatColor.BOLD + ChatColor.RED + "NOT" + ChatColor.RED + " reversible or refundable. Each time you reset your stats the price will increase for the next reset. Type 'cancel' to void this request.");
        p.sendMessage("");
        pLevel.setResetting(true);
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
    
    @EventHandler
    public void onStatsWindowClose(InventoryCloseEvent e) {
        if (ChatColor.stripColor(e.getInventory().getTitle()).equalsIgnoreCase("Stat Points")) {
            if (PlayerManager.getPlayerModel((Player) e.getPlayer()).getPlayerLevel().getFreePoints() > 0) {
                PlayerManager.getPlayerModel((Player) e.getPlayer()).getPlayerLevel().setTmrSecs(180);
            }
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

        /* disabled as of patch 1.9
        if (mob_level > (level + 10)) {
            if (PlayerManager.getPlayerModel(player).getToggleList() != null && PlayerManager.getPlayerModel(player).getToggleList().contains("debug")) {
                player.sendMessage(ChatColor.RED + "Your level was " + ChatColor.UNDERLINE + "lower" + ChatColor.RED
                        + " than 10 levels of this mob. No EXP granted.");
            }
            return;
        } else if (mob_level < (level - 8)) {
            if (PlayerManager.getPlayerModel(player).getToggleList() != null && PlayerManager.getPlayerModel(player).getToggleList().contains("debug")) {
                player.sendMessage(ChatColor.RED + "Your level was " + ChatColor.UNDERLINE + "greater" + ChatColor.RED
                        + " than 8 levels of this mob. No EXP granted.");
            }
            return; Disabled by Mayley's request
        }
        */
        
        int xp = 0;
        
        if (mob_level > level + 10) {  // limit mob xp calculation to 10 levels above player level
            xp = calculateXP(player, kill, level + 10);
        }
        else {
            xp  = calculateXP(player, kill, mob_level);
        }
//        else if (mob_level >= level - 10) {  // mob level is within range of 10 levels below/10 levels above, normal calculation
//            xp = calculateXP(player, kill, mob_level);    now disabled.  mobs 10 levels below give EXP as well on mayley's request
//        }

        if (PlayerManager.getPlayerModel(player).getToggleList() != null && PlayerManager.getPlayerModel(player).getToggleList().contains("indicator")) {
            Hologram xp_hologram = new Hologram(Main.plugin, ChatColor.GREEN.toString() + "+" + ChatColor.BOLD + xp + " XP");
            xp_hologram.show(kill.getLocation().add(0, 1, 0), 3, player);
        }

        addXP(player, xp);
    }

    public static int calculateXP(Player player, LivingEntity kill, int mob_level) {
//    	int xp = (int) Math.round((6.5 * Math.pow(mob_level,1.35)) + 40 + new Random().nextInt(50)); old exp formula
    	int pLevel = PlayerManager.getPlayerModel(player).getPlayerLevel().getLevel();
    	int xp = (int) (((pLevel * 5) + 45) * (1 + 0.05 * (pLevel + (mob_level - pLevel)))); // patch 1.9 exp formula
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
            return 10;
        } else if (tier == 3) {
            return 20;
        } else if (tier == 4) {
            return 30;
        } else if (tier == 5) {
            return 40;
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
        if (tier == 1 || tier == 2 && level >= 10 || tier == 3 && level >= 20 || tier == 4 && level >= 30 || tier == 5 && level >= 40) {
            return true;
        }
        return false;
    }

    public static int getPlayerTier(Player p) {
        int level = getPlayerLevel(p);
        if (level < 10) {
            return 1;
        } else if (level >= 10 && level < 20) {
            return 2;
        } else if (level >= 20 && level < 30) {
            return 3;
        } else if (level >= 30 && level < 40) {
            return 4;
        } else if (level >= 40) {
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
