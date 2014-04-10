package me.vaqxine.models;

import java.util.ArrayList;
import java.util.List;

import me.vaqxine.holograms.Hologram;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

@SuppressWarnings("deprecation")
public class PlayerModel {
	
	private String name;
	private List<String> achievements = new ArrayList<String>();
	private long globalChatDelay;
	private Location deathLocation;
	private long muteTime;
	private Hologram chatHologram;
	private String lastReply;
	private long lastPMTime;
	private List<String> buddyList = new ArrayList<String>();
	private List<String> localConfirmedBuddies = new ArrayList<String>();
	private List<String> ignoreList = new ArrayList<String>();
	private List<String> localConfirmedIgnores = new ArrayList<String>();
	private List<String> toggleList = new ArrayList<String>(); // TODO Convert toggles to ENUM's
	private long lastOnline;
	private long lastBookClick;
	private long rollDelay;
	private int serverNum;
	private int destructionWandUseCount;
	private long destructionWandCooldown;
	private long destructionWandLastUse;
	private boolean flameTrail;
	private boolean flamingArmor;
	private boolean musicSprites;
	private boolean demonicAura;
	private boolean goldCurse;
	private Location musicBoxPlacement;
	private Location musicBoxLocation;
	private long musicBoxCooldown;
	private String ecashStorage;
	private int fatigueEffect;
	private float energyRegenData;
	private float oldEnergy;
	private long lastAttack;
	private boolean sprinting;
	private boolean starving;
	private int health;
	private int thrownPotion;
	private int healthData;
	private int healthRegen;
	private long inCombat;
	private long lastEnvironmentalDamage;
	private Location lastHitLocation;
	private int noobPlayerWarning;
	private boolean combatLogger;
	private boolean noobPlayer;
	
	public PlayerModel(String name) {
		this.name = name;
	}
	
	public Player getPlayer() {
		return Bukkit.getPlayer(name);
	}
	
	public OfflinePlayer getOfflinePlayer() {
		return Bukkit.getOfflinePlayer(name);
	}
	
}
