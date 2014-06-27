package minecade.dungeonrealms.LevelMechanics;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import minecade.dungeonrealms.Main;
import minecade.dungeonrealms.AchievementMechanics.AchievementMechanics;
import minecade.dungeonrealms.CommunityMechanics.CommunityMechanics;
import minecade.dungeonrealms.HealthMechanics.HealthMechanics;
import minecade.dungeonrealms.ScoreboardMechanics.ScoreboardMechanics;
import minecade.dungeonrealms.database.ConnectionPool;
import minecade.dungeonrealms.enums.LogType;
import minecade.dungeonrealms.jsonlib.JsonBuilder;
import minecade.dungeonrealms.managers.PlayerManager;
import minecade.dungeonrealms.models.LogModel;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerLevel {

    private Player p;
    private String p_name;
    private int level;
    private int xp;
    private Entity last_mob_gained_from;
    
    // stat point stuff
    private int freePoints;
    private int strPoints;
    private int dexPoints;
    private int vitPoints;
    private int intPoints;
    private int tempFreePoints; // free points left before player clicks confirm
    private int tmrSecs; // timer for when to notify player of free points
    private int numWarnings; // number of free stat point warnings sent so far to prevent spam
    private int allocateSlot; // when a player is allocating a custom amount of points to a stat
    private int numResets; // number of stat resets the player has already had
    private boolean isResetting; // flag for if the player has talked to the reset NPC
    private String resetCode; // code for stat reset
    private int resetCost; // cost to reset stats for player
    public final static int POINTS_PER_LEVEL = 5; // points per level.  Change this to change the global value.
	public final static String FREE_STAT_NOTICE = ChatColor.GREEN + "***You have" + ChatColor.BOLD + " free "
			+ ChatColor.GREEN + "stat points!  Click here "
			+ ChatColor.GREEN + "or type /stats to allocate them.***";
	private static final String ALPHA_NUM = "123456789";

    @SuppressWarnings("deprecation")
	public PlayerLevel(String p_name, boolean aSync) {
        this.p_name = p_name;
        this.freePoints = 0;
        this.tempFreePoints = 0;
        this.strPoints = 0;
        this.dexPoints = 0;
        this.vitPoints = 0;
        this.intPoints = 0;
        this.tmrSecs = 0;
        this.numWarnings = 0;
        this.allocateSlot = -1;
        this.isResetting = false;
        this.resetCode = "";
        this.resetCost = 0;
        this.p = Bukkit.getPlayer(p_name);
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
            if (p == null)
                return;
            p.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + "          +" + ChatColor.YELLOW + (int) xp + ChatColor.BOLD + " EXP" + ChatColor.YELLOW
                    + ChatColor.GRAY + " [" + getXP() + ChatColor.BOLD + "/" + ChatColor.GRAY + (int) getEXPNeeded(getLevel()) + " EXP]");

        }
        if (p == null)
            return;
        CommunityMechanics.updateCombatPage(p);
    }

    public int getEXPNeeded(int level) {
        if (level == 1) {
            return 1000;
        }
        if (level >= 101) {
            return 0;
        }
        return (int) (100 * Math.pow(level, 2.24));
    }

    public void saveData(boolean remove) {
        final String name = p_name;
        Main.d("SAVED " + p_name + "'s DATA! Level: " + getLevel() + " XP: " + getXP());
        final int level = getLevel();
        final int xp = getXP();
        if (level == 0) {
            //Its bugged so dont save?
            //TODO: See why its saving twice?
            return;
        }
        new BukkitRunnable() {
            public void run() {
				try (PreparedStatement prest = ConnectionPool
						.getConnection()
						.prepareStatement(
								"UPDATE player_database SET player_level = ?, player_xp = ?, allocated_str = ?, allocated_dex = ?, allocated_int = ?, allocated_vit = ?, resets = ? WHERE p_name = ?")) {
                    prest.setInt(1, level);
                    prest.setInt(2, xp);
                    prest.setInt(3, strPoints);
                    prest.setInt(4, dexPoints);
                    prest.setInt(5, intPoints);
                    prest.setInt(6, vitPoints);
                    prest.setInt(7, numResets);
                    prest.setString(8, name);
                    prest.executeUpdate();
                    prest.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.runTaskAsynchronously(Main.plugin);
        if (remove) {
            PlayerManager.getPlayerModel(p_name).setPlayerLevel(null);
            return;
        }
    }

    public void levelUp(boolean alert) {
        setXP(0);
        if(getLevel() + 1 > 100){
            return;
        }
        setLevel(getLevel() + 1);
        freePoints += POINTS_PER_LEVEL;
        updateScoreboardLevel();
        if(getLevel() == 100){
            CommunityMechanics.sendPacketCrossServer("@level100@" + p_name + ":", -1, true);
            AchievementMechanics.addAchievement(p_name, "Overachiever");
            Bukkit.broadcastMessage(ChatColor.AQUA.toString() + ChatColor.BOLD + p_name + ChatColor.WHITE + " has reached level 100!");
        }
        new LogModel(LogType.LEVEL_UP, p_name, new JsonBuilder("level", getLevel()).getJson());

        if (alert) {
            if (p == null) {
                checkPlayer();
            }
            if (p == null)
                return;
            p.getWorld().playSound(p.getLocation(), Sound.LEVEL_UP, 1, .4F);
            p.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + "         " + " LEVEL UP! " + ChatColor.YELLOW + ChatColor.UNDERLINE + (getLevel() - 1)
                    + ChatColor.BOLD + " -> " + ChatColor.YELLOW + ChatColor.UNDERLINE + (getLevel()));
            p.playSound(p.getLocation(), Sound.LEVEL_UP, 0.5F, 1F);
            sendStatNoticeToPlayer(p);
        }
    }
    
    public void sendStatNoticeToPlayer() {
    	if (p != null) {
        	Main.getLevelMechanics().getFreePointsNotice().sendToPlayer(p);
    	}
    }
    
    public void sendStatNoticeToPlayer(Player p) {
        Main.getLevelMechanics().getFreePointsNotice().sendToPlayer(p);
    }

    public void updateScoreboardLevel() {
        if (p == null)
            return;
        ScoreboardMechanics.setPlayerLevel(getLevel(), p);
    }

    @SuppressWarnings("deprecation")
    public void checkPlayer() {
        this.p = Bukkit.getPlayer(p_name);
    }

    public void loadData() {
		try (PreparedStatement pst = ConnectionPool
				.getConnection()
				.prepareStatement(
						"SELECT player_level, player_xp, allocated_str, allocated_dex, allocated_int, allocated_vit, resets FROM player_database WHERE p_name = '"
								+ p_name + "'")) {
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
                setStrPoints(rs.getInt("allocated_str"));
                setDexPoints(rs.getInt("allocated_dex"));
                setIntPoints(rs.getInt("allocated_int"));
                setVitPoints(rs.getInt("allocated_vit"));
                setNumResets(rs.getInt("resets"));
                setFreePoints(level * POINTS_PER_LEVEL - (strPoints + dexPoints + intPoints + vitPoints));
                if (freePoints > 0) {
                	setTmrSecs(180);
                }
            }
            pst.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void sendInsertUpdate() {
		try (PreparedStatement pst = ConnectionPool
				.getConnection()
				.prepareStatement(
						"INSERT INTO player_database(p_name, player_level, player_xp, allocated_str, allocated_dex, allocated_int, allocated_vit) VALUES ('"
								+ p_name + "', 1, 0, 0, 0, 0, 0) ON DUPLICATE KEY UPDATE p_name = '" + p_name + "'")) {
            pst.executeUpdate();
            pst.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public String generateResetAuthenticationCode(Player p, String resets) {
        StringBuffer sb = new StringBuffer(4);
        for(int i = 0; i < 4; i++) {
            int ndx = (int) (Math.random() * ALPHA_NUM.length());
            sb.append(ALPHA_NUM.charAt(ndx));
        }

        return resets + sb.toString();
    }
    
    public void resetStatPoints() {
        this.strPoints = 0;
        this.dexPoints = 0;
        this.intPoints = 0;
        this.vitPoints = 0;
        this.freePoints = this.level * POINTS_PER_LEVEL;
        saveData(false);
    }

    /**
	 * @return The amount of free stat points the user currently has
	 */
	public int getFreePoints() {
		return freePoints;
	}

	/**
	 * @param Set the amount of free stat points for the user
	 */
	public void setFreePoints(int freePoints) {
		this.freePoints = freePoints;
	}

	public void setLevel(int level) {
        this.level = level;
        this.freePoints = level * POINTS_PER_LEVEL - (strPoints + dexPoints + intPoints + vitPoints);
        if (p != null) {
            ScoreboardMechanics.setPlayerLevel(getLevel(), p);
            if (freePoints > 0) {
            	sendStatNoticeToPlayer(p);
            }
            else if (freePoints < 0) {
            	strPoints = 0;
            	dexPoints = 0;
            	intPoints = 0;
            	vitPoints = 0;
            	freePoints = level * POINTS_PER_LEVEL;
            	p.sendMessage(ChatColor.RED + "Your level was decreased, so your stats have been reset.");
            }
        }
    }

    public void setXP(int xp) {
        this.xp = xp;
        if (p != null)
            HealthMechanics.setOverheadHP(p, HealthMechanics.getPlayerHP(p.getName()));
    }

    public int getLevel() {
        return level;
    }

    public int getXP() {
        return xp;
    }

	public int getStrPoints() {
		return strPoints;
	}

	public void setStrPoints(int strPoints) {
		this.strPoints = strPoints;
	}

	public int getDexPoints() {
		return dexPoints;
	}

	public void setDexPoints(int dexPoints) {
		this.dexPoints = dexPoints;
	}

	public int getVitPoints() {
		return vitPoints;
	}

	public void setVitPoints(int vitPoints) {
		this.vitPoints = vitPoints;
	}

	public int getIntPoints() {
		return intPoints;
	}

	public void setIntPoints(int intPoints) {
		this.intPoints = intPoints;
	}

	public int getTempFreePoints() {
		return tempFreePoints;
	}

	public void setTempFreePoints(int tempFreePoints) {
		this.tempFreePoints = tempFreePoints;
	}

	public int getTmrSecs() {
		return tmrSecs;
	}

	public void setTmrSecs(int tmrSecs) {
		this.tmrSecs = tmrSecs;
	}
	
	public void tickTmr() {
		this.tmrSecs--;
	}

    public boolean isResetting() {
        return isResetting;
    }

    public void setResetting(boolean isResetting) {
        this.isResetting = isResetting;
    }

    public int getAllocateSlot() {
        return allocateSlot;
    }

    public void setAllocateSlot(int allocateSlot) {
        this.allocateSlot = allocateSlot;
    }

    public int getNumWarnings() {
        return numWarnings;
    }

    public void setNumWarnings(int numWarnings) {
        this.numWarnings = numWarnings;
    }

    public int getNumResets() {
        return numResets;
    }

    public void setNumResets(int numResets) {
        this.numResets = numResets;
    }

    public String getResetCode() {
        return resetCode;
    }

    public void setResetCode(String resetCode) {
        this.resetCode = resetCode;
    }

    public int getResetCost() {
        return resetCost;
    }

    public void setResetCost(int resetCost) {
        this.resetCost = resetCost;
    }
    
}
