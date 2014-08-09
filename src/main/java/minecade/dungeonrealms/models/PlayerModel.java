package minecade.dungeonrealms.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import minecade.dungeonrealms.HearthstoneMechanics.Hearthstone;
import minecade.dungeonrealms.ItemMechanics.ItemMechanics;
import minecade.dungeonrealms.ItemMechanics.PlayerArrowReplace;
import minecade.dungeonrealms.LevelMechanics.PlayerLevel;
import minecade.dungeonrealms.PartyMechanics.Party;
import minecade.dungeonrealms.holograms.Hologram;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.WeatherType;
import org.bukkit.block.Block;
import org.bukkit.block.Furnace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

@SuppressWarnings("deprecation")
public class PlayerModel {
	
	private String name;
	private String achievements;
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
	private Hearthstone hearthstone;
	private Location hearthstoneLocation;
	private int hearthstoneTimer;
	private boolean pendingUpload;
	private boolean noUpload;
	private boolean beingUploaded;
	private boolean locked;
	private boolean firstLogin;
	private boolean onlineToday;
	private boolean killingSelf;
	private List<Object> remotePlayerData = new ArrayList<Object>();
	private String localPlayerIP;
	private List<String> playerIP = new ArrayList<String>();
	private Inventory playerInventory;
	private Location playerLocation;
	private double playerHP;
	private int playerLevel;
	private int playerFoodLevel;
	private ItemStack[] playerArmorContents;
	private int playerEcash;
	private int playerSDaysLeft;
	private List<Integer> playerPortalShards = new ArrayList<Integer>();
	private long playerFirstLogin;
	private String playerBio;
	private ItemStack playerItemInHand;
	private Inventory playerMuleInventory;
	private long logoutTime;
	private long lastSync;
	private int safeLogout;
	private String toKick;
	private int forumUserGroup;
	private long loginTime;
	private String serverSwap;
	private Location serverSwapLocation;
	private String serverSwapPending;
	private boolean loaded;
	private List<Integer> totalTiming = new ArrayList<Integer>();
	private int averageTiming;
	private long instanceTiming;
	private String playerInstance;
	private List<String> instanceParty = new ArrayList<String>();
	private Location savedLocationInstance;
	private long processingMove;
	private List<Integer> armorData = new ArrayList<Integer>();
	private List<Integer> damageData = new ArrayList<Integer>();
	private PlayerArrowReplace arrowReplace;
	private int strength;
	private int dexterity;
	private int vitality;
	private int intelligence;
	private int fireResistance;
	private int iceResistance;
	private int poisonResistance;
	private int dodge;
	private int block;
	private int thorn;
	private int reflection;
	private int itemFind;
	private int gemFind;
	private long lastOrbUse;
	private int tier;
	private List<ItemStack> armorContents = new ArrayList<ItemStack>();
	private boolean needUpdate;
	private boolean noNegation;
	private boolean processingDamageEvent;
	private boolean processingProjectileEvent;
	private ItemStack spoofedWeapon;
	private boolean processWeapon;
	private String alignment;
	private int alignTime;
	private String plastHit;
	private long lastHit;
	private long lastPlayerAttack;
	private List<ItemStack> savedGear = new ArrayList<ItemStack>();
	private String lostGear;
	private Location savedLocation;
	private int lootSpawnStep;
	private String lootSpawnData;
	private Location lootSpawnLocation;
	private Location lastOpenedLootChest;
	private int itemBeingBought;
	private int orbBeingBought;
	private int dyeBeingBought;
	private int skillBeingBought;
	private int ecashItemBeingBought;
	private int shardItemBeingBought;
	private String lookingIntoOfflineBank;
	private int reportStep;
	private int particleEffects;
	private String reportData;
	private long lastUnstuck;
	private int muteCount;
	private int kickCount;
	private int banCount;
	private boolean usedStuck;
	private boolean vanished;
	private List<Inventory> bankContents = new ArrayList<Inventory>();
	private int bankLevel;
	private String bankUpgradeCode;
	private int bank;
	private int split;
	private int withdraw;
	private String withdrawType;
	private int mobSpawnStep;
	private String mobSpawnData;
	private Location mobSpawnLocation;
	private long playerSlow;
	private String passiveHunter;
	private long lastMobMessage;
	private int mountBeingBought;
	private Location horseSageLocation;
	private int summonMount;
	private Location summonLocation;
	private ItemStack summonItem;
	private Entity mount;
	private Entity mule;
	private Inventory muleInventory;
	private String muleItemList;
	private boolean inShop;
	private String partyInvite;
	private long partyInviteTime;
	private Party party;
	private String partyLoot;
	private int partyLootIndex;
	private boolean partyOnly;
	private String rank;
	private int rankForumGroup;
	private List<String> pets = new ArrayList<String>();
	private long petSpawnDelay;
	private List<Entity> petEntities = new ArrayList<Entity>();
	private Location namingPet;
	private String petType;
	private List<String> phraseList = new ArrayList<String>();
	private int slowMining;
	private long lastSwing;
	private Location playerFishingSpot;
	private HashMap<Block, Furnace> furnaceInventory = new HashMap<Block, Furnace>();
	private boolean ignoreFurnaceOpenEvent;
	private int fishCaughtCount;
	private int fishHealthRegen;
	private int fishEnergyRegen;
	private int fishBonusDamage;
	private int fishBonusArmor;
	private int fishBonusBlock;
	private int fishBonusLifesteal;
	private int fishBonusCriticalHit;
	private int currentItemBeingBought;
	private ItemStack itemStackBeingBought;
	private int shopPage;
	private String shopCurrency;
	private long recentMovement;
	private Location savedOutOfRealmLocation;
	private String portalMapCoords;
	private Location inventoryPortalMap;
	private boolean hasPortal;
	private long portalCooldown;
	private String realmUpgradeCode;
	private String realmPercent;
	private String realmTitle;
	private int realmTier;
	private int savedLevels;
	private boolean realmLoadedStatus;
	private long safeRealm;
	private long flyingRealm;
	private long realmResetCooldown;
	private List<String> buildList = new ArrayList<String>();
	private boolean readyWorld;
	private boolean corruptWorld;
	private long godMode;
	private int deaths;
	private List<Integer> kills = new ArrayList<Integer>();
	private int mobKills;
	private List<Integer> duelStatistics = new ArrayList<Integer>();
	private int inventoryUpdate;
	private long warnedDurability;
	private ItemStack repair;
	private Item itemRepair;
	private Location anvil;
	private int repairState;
	private String zoneType;
	private boolean recentCraft;
	private boolean inInventory;
	private boolean recentBlockEvent;
	private int shopLevel;
	private Block inverseShop;
	private Inventory shopStock;
	private ItemStack itemBeingStocked;
	private int shopServer;
	private String shopBeingBrowsed;
	private String shopUpgradeCode;
	private long lastShopOpen;
	private Inventory collectionBin;
	private boolean priceUpdateNeeded;
	private boolean openingShop;
	private boolean needSQLUpdate;
	private String tp;
	private int tpEffect;
	private Location tpLocation;
	private long processingMoveTeleport;
	private Location warp;
	private Player trade;
	private Player tradePartners;
	private Inventory tradeSecure;
	private long lastInventoryClose;
	private List<String> quest = new ArrayList<String>();
	private List<String> completionDelay = new ArrayList<String>();
	private boolean skipConfirm;
	private boolean leaveConfirm;
	private boolean enchantScroll;
	private boolean onIsland;
	private WeatherType weather;
	private long lastLocalLogin;
	private PlayerLevel player_level;
	private int regenFoodBonus;
	private boolean isBuyingItem;
	private int regenTimer = -1;
	private Location teleportLoc;
	public PlayerModel(String name) {
		this.name = name;
	}
	
	public Player getPlayer() {
		return Bukkit.getPlayerExact(name);
	}
	
	public OfflinePlayer getOfflinePlayer() {
		return Bukkit.getOfflinePlayer(name);
	}

	public String getAchievements() {
		return achievements;
	}
	public void setPlayerLevel(PlayerLevel pl){
	    this.player_level = pl;
	}
	
	public PlayerLevel getPlayerLevel(){
	    return player_level;
	}
	public void setAchievements(String achievements) {
		this.achievements = achievements;
	}

	public long getGlobalChatDelay() {
		return globalChatDelay;
	}

	public void setGlobalChatDelay(long globalChatDelay) {
		this.globalChatDelay = globalChatDelay;
	}

	public Location getDeathLocation() {
		return deathLocation;
	}

	public void setDeathLocation(Location deathLocation) {
		this.deathLocation = deathLocation;
	}

	public long getMuteTime() {
		return muteTime;
	}

	public void setMuteTime(long muteTime) {
		this.muteTime = muteTime;
	}

	public Hologram getChatHologram() {
		return chatHologram;
	}

	public void setChatHologram(Hologram chatHologram) {
		this.chatHologram = chatHologram;
	}

	public String getLastReply() {
		return lastReply;
	}

	public void setLastReply(String lastReply) {
		this.lastReply = lastReply;
	}

	public long getLastPMTime() {
		return lastPMTime;
	}

	public void setLastPMTime(long lastPMTime) {
		this.lastPMTime = lastPMTime;
	}

	public List<String> getBuddyList() {
		return buddyList;
	}

	public void setBuddyList(List<String> buddyList) {
		this.buddyList = buddyList;
	}

	public List<String> getLocalConfirmedBuddies() {
		return localConfirmedBuddies;
	}

	public void setLocalConfirmedBuddies(List<String> localConfirmedBuddies) {
		this.localConfirmedBuddies = localConfirmedBuddies;
	}

	public List<String> getIgnoreList() {
		return ignoreList;
	}

	public void setIgnoreList(List<String> ignoreList) {
		this.ignoreList = ignoreList;
	}

	public List<String> getLocalConfirmedIgnores() {
		return localConfirmedIgnores;
	}

	public void setLocalConfirmedIgnores(List<String> localConfirmedIgnores) {
		this.localConfirmedIgnores = localConfirmedIgnores;
	}

	public List<String> getToggleList() {
		return toggleList;
	}

	public void setToggleList(List<String> toggleList) {
		this.toggleList = toggleList;
	}

	public long getLastOnline() {
		return lastOnline;
	}

	public void setLastOnline(long lastOnline) {
		this.lastOnline = lastOnline;
	}

	public long getLastBookClick() {
		return lastBookClick;
	}

	public void setLastBookClick(long lastBookClick) {
		this.lastBookClick = lastBookClick;
	}

	public long getRollDelay() {
		return rollDelay;
	}

	public void setRollDelay(long rollDelay) {
		this.rollDelay = rollDelay;
	}

	public int getServerNum() {
		return serverNum;
	}

	public void setServerNum(int serverNum) {
		this.serverNum = serverNum;
	}

	public int getDestructionWandUseCount() {
		return destructionWandUseCount;
	}

	public void setDestructionWandUseCount(int destructionWandUseCount) {
		this.destructionWandUseCount = destructionWandUseCount;
	}

	public long getDestructionWandCooldown() {
		return destructionWandCooldown;
	}

	public void setDestructionWandCooldown(long destructionWandCooldown) {
		this.destructionWandCooldown = destructionWandCooldown;
	}

	public long getDestructionWandLastUse() {
		return destructionWandLastUse;
	}

	public void setDestructionWandLastUse(long destructionWandLastUse) {
		this.destructionWandLastUse = destructionWandLastUse;
	}

	public boolean isFlameTrail() {
		return flameTrail;
	}

	public void setFlameTrail(boolean flameTrail) {
		this.flameTrail = flameTrail;
	}

	public boolean isFlamingArmor() {
		return flamingArmor;
	}

	public void setFlamingArmor(boolean flamingArmor) {
		this.flamingArmor = flamingArmor;
	}

	public boolean isMusicSprites() {
		return musicSprites;
	}

	public void setMusicSprites(boolean musicSprites) {
		this.musicSprites = musicSprites;
	}

	public boolean isDemonicAura() {
		return demonicAura;
	}

	public void setDemonicAura(boolean demonicAura) {
		this.demonicAura = demonicAura;
	}

	public boolean isGoldCurse() {
		return goldCurse;
	}

	public void setGoldCurse(boolean goldCurse) {
		this.goldCurse = goldCurse;
	}

	public Location getMusicBoxPlacement() {
		return musicBoxPlacement;
	}

	public void setMusicBoxPlacement(Location musicBoxPlacement) {
		this.musicBoxPlacement = musicBoxPlacement;
	}

	public Location getMusicBoxLocation() {
		return musicBoxLocation;
	}

	public void setMusicBoxLocation(Location musicBoxLocation) {
		this.musicBoxLocation = musicBoxLocation;
	}

	public long getMusicBoxCooldown() {
		return musicBoxCooldown;
	}

	public void setMusicBoxCooldown(long musicBoxCooldown) {
		this.musicBoxCooldown = musicBoxCooldown;
	}

	public String getEcashStorage() {
		return ecashStorage;
	}

	public void setEcashStorage(String ecashStorage) {
		this.ecashStorage = ecashStorage;
	}

	public int getFatigueEffect() {
		return fatigueEffect;
	}

	public void setFatigueEffect(int fatigueEffect) {
		this.fatigueEffect = fatigueEffect;
	}

	public float getEnergyRegenData() {
		return energyRegenData;
	}

	public void setEnergyRegenData(float energyRegenData) {
		this.energyRegenData = energyRegenData;
	}

	public float getOldEnergy() {
		return oldEnergy;
	}

	public void setOldEnergy(float oldEnergy) {
		this.oldEnergy = oldEnergy;
	}

	public long getLastAttack() {
		return lastAttack;
	}

	public void setLastAttack(long lastAttack) {
		this.lastAttack = lastAttack;
	}

	public boolean isSprinting() {
		return sprinting;
	}

	public void setSprinting(boolean sprinting) {
		this.sprinting = sprinting;
	}

	public boolean isStarving() {
		return starving;
	}

	public void setStarving(boolean starving) {
		this.starving = starving;
	}

	public int getHealth() {
		return health;
	}

	public void setHealth(int health) {
		this.health = health;
	}

	public int getThrownPotion() {
		return thrownPotion;
	}

	public void setThrownPotion(int thrownPotion) {
		this.thrownPotion = thrownPotion;
	}

	public int getHealthData() {
		return healthData;
	}

	public void setHealthData(int healthData) {
		this.healthData = healthData;
	}

	public int getHealthRegen() {
		return healthRegen;
	}

	public void setHealthRegen(int healthRegen) {
		this.healthRegen = healthRegen;
	}

	public long getInCombat() {
		return inCombat;
	}

	public void setInCombat(long inCombat) {
		this.inCombat = inCombat;
	}

	public long getLastEnvironmentalDamage() {
		return lastEnvironmentalDamage;
	}

	public void setLastEnvironmentalDamage(long lastEnvironmentalDamage) {
		this.lastEnvironmentalDamage = lastEnvironmentalDamage;
	}

	public Location getLastHitLocation() {
		return lastHitLocation;
	}

	public void setLastHitLocation(Location lastHitLocation) {
		this.lastHitLocation = lastHitLocation;
	}

	public int getNoobPlayerWarning() {
		return noobPlayerWarning;
	}

	public void setNoobPlayerWarning(int noobPlayerWarning) {
		this.noobPlayerWarning = noobPlayerWarning;
	}

	public boolean isCombatLogger() {
		return combatLogger;
	}

	public void setCombatLogger(boolean combatLogger) {
		this.combatLogger = combatLogger;
	}

	public boolean isNoobPlayer() {
		return noobPlayer;
	}

	public void setNoobPlayer(boolean noobPlayer) {
		this.noobPlayer = noobPlayer;
	}

	public Hearthstone getHearthstone() {
		return hearthstone;
	}

	public void setHearthstone(Hearthstone hearthstone) {
		this.hearthstone = hearthstone;
	}

	public Location getHearthstoneLocation() {
		return hearthstoneLocation;
	}

	public void setHearthstoneLocation(Location hearthstoneLocation) {
		this.hearthstoneLocation = hearthstoneLocation;
	}

	public int getHearthstoneTimer() {
		return hearthstoneTimer;
	}

	public void setHearthstoneTimer(int hearthstoneTimer) {
		this.hearthstoneTimer = hearthstoneTimer;
	}

	public boolean isPendingUpload() {
		return pendingUpload;
	}

	public void setPendingUpload(boolean pendingUpload) {
		this.pendingUpload = pendingUpload;
	}

	public boolean isNoUpload() {
		return noUpload;
	}

	public void setNoUpload(boolean noUpload) {
		this.noUpload = noUpload;
	}

	public boolean isBeingUploaded() {
		return beingUploaded;
	}

	public void setBeingUploaded(boolean beingUploaded) {
		this.beingUploaded = beingUploaded;
	}

	public boolean isLocked() {
		return locked;
	}

	public void setLocked(boolean locked) {
		this.locked = locked;
	}

	public boolean isFirstLogin() {
		return firstLogin;
	}

	public void setFirstLogin(boolean firstLogin) {
		this.firstLogin = firstLogin;
	}

	public boolean isOnlineToday() {
		return onlineToday;
	}

	public void setOnlineToday(boolean onlineToday) {
		this.onlineToday = onlineToday;
	}

	public boolean isKillingSelf() {
		return killingSelf;
	}

	public void setKillingSelf(boolean killingSelf) {
		this.killingSelf = killingSelf;
	}

	public List<Object> getRemotePlayerData() {
		return remotePlayerData;
	}

	public void setRemotePlayerData(List<Object> remotePlayerData) {
		this.remotePlayerData = remotePlayerData;
	}

	public String getLocalPlayerIP() {
		return localPlayerIP;
	}

	public void setLocalPlayerIP(String localPlayerIP) {
		this.localPlayerIP = localPlayerIP;
	}

	public List<String> getPlayerIP() {
		return playerIP;
	}

	public void setPlayerIP(List<String> playerIP) {
		this.playerIP = playerIP;
	}

	public Inventory getPlayerInventory() {
		return playerInventory;
	}

	public void setPlayerInventory(Inventory playerInventory) {
		this.playerInventory = playerInventory;
	}

	public Location getPlayerLocation() {
		return playerLocation;
	}

	public void setPlayerLocation(Location playerLocation) {
		this.playerLocation = playerLocation;
	}

	public double getPlayerHP() {
		return playerHP;
	}

	public void setPlayerHP(double playerHP) {
		this.playerHP = playerHP;
	}

	public int getPlayerLevels() {
		return playerLevel;
	}

	public void setPlayerLevel(int playerLevel) {
		this.playerLevel = playerLevel;
	}

	public int getPlayerFoodLevel() {
		return playerFoodLevel;
	}

	public void setPlayerFoodLevel(int playerFoodLevel) {
		this.playerFoodLevel = playerFoodLevel;
	}

	public ItemStack[] getPlayerArmorContents() {
		return playerArmorContents;
	}

	public void setPlayerArmorContents(ItemStack[] playerArmorContents) {
		this.playerArmorContents = playerArmorContents;
	}

	public int getPlayerEcash() {
		return playerEcash;
	}

	public void setPlayerEcash(int playerEcash) {
		this.playerEcash = playerEcash;
	}

	public int getPlayerSDaysLeft() {
		return playerSDaysLeft;
	}

	public void setPlayerSDaysLeft(int playerSDaysLeft) {
		this.playerSDaysLeft = playerSDaysLeft;
	}

	public List<Integer> getPlayerPortalShards() {
		return playerPortalShards;
	}

	public void setPlayerPortalShards(List<Integer> playerPortalShards) {
		this.playerPortalShards = playerPortalShards;
	}

	public long getPlayerFirstLogin() {
		return playerFirstLogin;
	}

	public void setPlayerFirstLogin(long playerFirstLogin) {
		this.playerFirstLogin = playerFirstLogin;
	}

	public String getPlayerBio() {
		return playerBio;
	}

	public void setPlayerBio(String playerBio) {
		this.playerBio = playerBio;
	}

	public ItemStack getPlayerItemInHand() {
		return playerItemInHand;
	}

	public void setPlayerItemInHand(ItemStack playerItemInHand) {
		this.playerItemInHand = playerItemInHand;
	}

	public Inventory getPlayerMuleInventory() {
		return playerMuleInventory;
	}

	public void setPlayerMuleInventory(Inventory playerMuleInventory) {
		this.playerMuleInventory = playerMuleInventory;
	}

	public long getLogoutTime() {
		return logoutTime;
	}

	public void setLogoutTime(long logoutTime) {
		this.logoutTime = logoutTime;
	}

	public long getLastSync() {
		return lastSync;
	}

	public void setLastSync(long lastSync) {
		this.lastSync = lastSync;
	}

	public int getSafeLogout() {
		return safeLogout;
	}

	public void setSafeLogout(int safeLogout) {
		this.safeLogout = safeLogout;
	}

	public String getToKick() {
		return toKick;
	}

	public void setToKick(String toKick) {
		this.toKick = toKick;
	}

	public int getForumUserGroup() {
		return forumUserGroup;
	}

	public void setForumUserGroup(int forumUserGroup) {
		this.forumUserGroup = forumUserGroup;
	}

	public long getLoginTime() {
		return loginTime;
	}

	public void setLoginTime(long loginTime) {
		this.loginTime = loginTime;
	}

	public String getServerSwap() {
		return serverSwap;
	}

	public void setServerSwap(String serverSwap) {
		this.serverSwap = serverSwap;
	}

	public Location getServerSwapLocation() {
		return serverSwapLocation;
	}

	public void setServerSwapLocation(Location serverSwapLocation) {
		this.serverSwapLocation = serverSwapLocation;
	}

	public String getServerSwapPending() {
		return serverSwapPending;
	}

	public void setServerSwapPending(String serverSwapPending) {
		this.serverSwapPending = serverSwapPending;
	}

	public boolean isLoaded() {
		return loaded;
	}

	public void setLoaded(boolean loaded) {
		this.loaded = loaded;
	}

	public List<Integer> getTotalTiming() {
		return totalTiming;
	}

	public void setTotalTiming(List<Integer> totalTiming) {
		this.totalTiming = totalTiming;
	}

	public int getAverageTiming() {
		return averageTiming;
	}

	public void setAverageTiming(int averageTiming) {
		this.averageTiming = averageTiming;
	}

	public long getInstanceTiming() {
		return instanceTiming;
	}

	public void setInstanceTiming(long instanceTiming) {
		this.instanceTiming = instanceTiming;
	}

	public String getPlayerInstance() {
		return playerInstance;
	}

	public void setPlayerInstance(String playerInstance) {
		this.playerInstance = playerInstance;
	}

	public List<String> getInstanceParty() {
		return instanceParty;
	}

	public void setInstanceParty(List<String> instanceParty) {
		this.instanceParty = instanceParty;
	}

	public Location getSavedLocationInstance() {
		return savedLocationInstance;
	}

	public void setSavedLocationInstance(Location savedLocationInstance) {
		this.savedLocationInstance = savedLocationInstance;
	}

	public long getProcessingMove() {
		return processingMove;
	}

	public void setProcessingMove(long processingMove) {
		this.processingMove = processingMove;
	}

	public List<Integer> getArmorData() {
		return armorData;
	}

	public void setArmorData(List<Integer> armorData) {
		this.armorData = armorData;
	}

	public List<Integer> getDamageData() {
		return damageData;
	}

	public void setDamageData(List<Integer> damageData) {
		this.damageData = damageData;
	}

	public PlayerArrowReplace getArrowReplace() {
		return arrowReplace;
	}

	public void setArrowReplace(PlayerArrowReplace arrowReplace) {
		this.arrowReplace = arrowReplace;
	}

	public int getStrength() {
		return strength;
	}

	public void setStrength(int strength) {
		this.strength = strength;
	}

	public int getDexterity() {
		return dexterity;
	}

	public void setDexterity(int dexterity) {
		this.dexterity = dexterity;
	}

	public int getVitality() {
		return vitality;
	}

	public void setVitality(int vitality) {
		this.vitality = vitality;
	}

	public int getIntelligence() {
		return intelligence;
	}

	public void setIntelligence(int intelligence) {
		this.intelligence = intelligence;
	}

	public int getFireResistance() {
		return fireResistance;
	}

	public void setFireResistance(int fireResistance) {
		this.fireResistance = fireResistance;
	}

	public int getIceResistance() {
		return iceResistance;
	}

	public void setIceResistance(int iceResistance) {
		this.iceResistance = iceResistance;
	}

	public int getPoisonResistance() {
		return poisonResistance;
	}

	public void setPoisonResistance(int poisonResistance) {
		this.poisonResistance = poisonResistance;
	}

	public int getDodge() {
		return dodge;
	}

	public void setDodge(int dodge) {
		this.dodge = dodge;
	}

	public int getBlock() {
		return block;
	}

	public void setBlock(int block) {
		this.block = block;
	}

	public int getThorn() {
		return thorn;
	}

	public void setThorn(int thorn) {
		this.thorn = thorn;
	}

	public int getReflection() {
		return reflection;
	}

	public void setReflection(int reflection) {
		this.reflection = reflection;
	}

	public int getItemFind() {
		return itemFind;
	}

	public void setItemFind(int itemFind) {
		this.itemFind = itemFind;
	}

	public int getGemFind() {
		return gemFind;
	}

	public void setGemFind(int gemFind) {
		this.gemFind = gemFind;
	}

	public long getLastOrbUse() {
		return lastOrbUse;
	}

	public void setLastOrbUse(long lastOrbUse) {
		this.lastOrbUse = lastOrbUse;
	}

	public int getTier() {
		return tier;
	}

	public void setTier(int tier) {
		this.tier = tier;
	}

	public List<ItemStack> getArmorContents() {
		return armorContents;
	}

	public void setArmorContents(List<ItemStack> armorContents) {
		this.armorContents = armorContents;
	}

	public boolean isNeedUpdate() {
		return needUpdate;
	}

	public void setNeedUpdate(boolean needUpdate) {
		this.needUpdate = needUpdate;
	}

	public boolean isNoNegation() {
		return noNegation;
	}

	public void setNoNegation(boolean noNegation) {
		this.noNegation = noNegation;
	}

	public boolean isProcessingDamageEvent() {
		return processingDamageEvent;
	}

	public void setProcessingDamageEvent(boolean processingDamageEvent) {
		this.processingDamageEvent = processingDamageEvent;
	}

	public boolean isProcessingProjectileEvent() {
		return processingProjectileEvent;
	}

	public void setProcessingProjectileEvent(boolean processingProjectileEvent) {
		this.processingProjectileEvent = processingProjectileEvent;
	}

	public ItemStack getSpoofedWeapon() {
		return spoofedWeapon;
	}

	public void setSpoofedWeapon(ItemStack spoofedWeapon) {
		this.spoofedWeapon = spoofedWeapon;
	}

	public boolean isProcessWeapon() {
		return processWeapon;
	}

	public void setProcessWeapon(boolean processWeapon) {
		this.processWeapon = processWeapon;
	}

	public String getAlignment() {
		return alignment;
	}

	public void setAlignment(String alignment) {
		this.alignment = alignment;
	}

	public int getAlignTime() {
		return alignTime;
	}

	public void setAlignTime(int alignTime) {
		this.alignTime = alignTime;
	}

	public String getPlastHit() {
		return plastHit;
	}

	public void setPlastHit(String plastHit) {
		this.plastHit = plastHit;
	}

	public long getLastHit() {
		return lastHit;
	}

	public void setLastHit(long lastHit) {
		this.lastHit = lastHit;
	}

	public long getLastPlayerAttack() {
		return lastPlayerAttack;
	}

	public void setLastPlayerAttack(long lastPlayerAttack) {
		this.lastPlayerAttack = lastPlayerAttack;
	}

	public List<ItemStack> getSavedGear() {
		return savedGear;
	}

	public void setSavedGear(List<ItemStack> savedGear) {
		this.savedGear = savedGear;
	}

	public String getLostGear() {
		return lostGear;
	}

	public void setLostGear(String lostGear) {
		this.lostGear = lostGear;
	}

	public Location getSavedLocation() {
		return savedLocation;
	}

	public void setSavedLocation(Location savedLocation) {
		this.savedLocation = savedLocation;
	}

	public int getLootSpawnStep() {
		return lootSpawnStep;
	}

	public void setLootSpawnStep(int lootSpawnStep) {
		this.lootSpawnStep = lootSpawnStep;
	}

	public String getLootSpawnData() {
		return lootSpawnData;
	}

	public void setLootSpawnData(String lootSpawnData) {
		this.lootSpawnData = lootSpawnData;
	}

	public Location getLootSpawnLocation() {
		return lootSpawnLocation;
	}

	public void setLootSpawnLocation(Location lootSpawnLocation) {
		this.lootSpawnLocation = lootSpawnLocation;
	}

	public Location getLastOpenedLootChest() {
		return lastOpenedLootChest;
	}

	public void setLastOpenedLootChest(Location lastOpenedLootChest) {
		this.lastOpenedLootChest = lastOpenedLootChest;
	}

	public int getItemBeingBought() {
		return itemBeingBought;
	}

	public void setItemBeingBought(int itemBeingBought) {
		this.itemBeingBought = itemBeingBought;
	}

	public int getOrbBeingBought() {
		return orbBeingBought;
	}

	public void setOrbBeingBought(int orbBeingBought) {
		this.orbBeingBought = orbBeingBought;
	}

	public int getDyeBeingBought() {
		return dyeBeingBought;
	}

	public void setDyeBeingBought(int dyeBeingBought) {
		this.dyeBeingBought = dyeBeingBought;
	}

	public int getSkillBeingBought() {
		return skillBeingBought;
	}

	public void setSkillBeingBought(int skillBeingBought) {
		this.skillBeingBought = skillBeingBought;
	}

	public int getEcashItemBeingBought() {
		return ecashItemBeingBought;
	}

	public void setEcashItemBeingBought(int ecashItemBeingBought) {
		this.ecashItemBeingBought = ecashItemBeingBought;
	}

	public int getShardItemBeingBought() {
		return shardItemBeingBought;
	}

	public void setShardItemBeingBought(int shardItemBeingBought) {
		this.shardItemBeingBought = shardItemBeingBought;
	}

	public String getLookingIntoOfflineBank() {
		return lookingIntoOfflineBank;
	}

	public void setLookingIntoOfflineBank(String lookingIntoOfflineBank) {
		this.lookingIntoOfflineBank = lookingIntoOfflineBank;
	}

	public int getReportStep() {
		return reportStep;
	}

	public void setReportStep(int reportStep) {
		this.reportStep = reportStep;
	}

	public int getParticleEffects() {
		return particleEffects;
	}

	public void setParticleEffects(int particleEffects) {
		this.particleEffects = particleEffects;
	}

	public String getReportData() {
		return reportData;
	}

	public void setReportData(String reportData) {
		this.reportData = reportData;
	}

	public long getLastUnstuck() {
		return lastUnstuck;
	}

	public void setLastUnstuck(long lastUnstuck) {
		this.lastUnstuck = lastUnstuck;
	}

	public int getMuteCount() {
		return muteCount;
	}

	public void setMuteCount(int muteCount) {
		this.muteCount = muteCount;
	}

	public int getKickCount() {
		return kickCount;
	}

	public void setKickCount(int kickCount) {
		this.kickCount = kickCount;
	}

	public int getBanCount() {
		return banCount;
	}

	public void setBanCount(int banCount) {
		this.banCount = banCount;
	}

	public boolean isUsedStuck() {
		return usedStuck;
	}

	public void setUsedStuck(boolean usedStuck) {
		this.usedStuck = usedStuck;
	}

	public boolean isVanished() {
		return vanished;
	}

	public void setVanished(boolean vanished) {
		this.vanished = vanished;
	}

	public List<Inventory> getBankContents() {
		return bankContents;
	}

	public void setBankContents(List<Inventory> bankContents) {
		this.bankContents = bankContents;
	}

	public int getBankLevel() {
		return bankLevel;
	}

	public void setBankLevel(int bankLevel) {
		this.bankLevel = bankLevel;
	}

	public String getBankUpgradeCode() {
		return bankUpgradeCode;
	}

	public void setBankUpgradeCode(String bankUpgradeCode) {
		this.bankUpgradeCode = bankUpgradeCode;
	}

	public int getBank() {
		return bank;
	}

	public void setBank(int bank) {
		this.bank = bank;
	}

	public int getSplit() {
		return split;
	}

	public void setSplit(int split) {
		this.split = split;
	}

	public int getWithdraw() {
		return withdraw;
	}

	public void setWithdraw(int withdraw) {
		this.withdraw = withdraw;
	}

	public String getWithdrawType() {
		return withdrawType;
	}

	public void setWithdrawType(String withdrawType) {
		this.withdrawType = withdrawType;
	}

	public int getMobSpawnStep() {
		return mobSpawnStep;
	}

	public void setMobSpawnStep(int mobSpawnStep) {
		this.mobSpawnStep = mobSpawnStep;
	}

	public String getMobSpawnData() {
		return mobSpawnData;
	}

	public void setMobSpawnData(String mobSpawnData) {
		this.mobSpawnData = mobSpawnData;
	}

	public Location getMobSpawnLocation() {
		return mobSpawnLocation;
	}

	public void setMobSpawnLocation(Location mobSpawnLocation) {
		this.mobSpawnLocation = mobSpawnLocation;
	}

	public long getPlayerSlow() {
		return playerSlow;
	}

	public void setPlayerSlow(long playerSlow) {
		this.playerSlow = playerSlow;
	}

	public String getPassiveHunter() {
		return passiveHunter;
	}

	public void setPassiveHunter(String passiveHunter) {
		this.passiveHunter = passiveHunter;
	}

	public long getLastMobMessage() {
		return lastMobMessage;
	}

	public void setLastMobMessage(long lastMobMessage) {
		this.lastMobMessage = lastMobMessage;
	}

	public int getMountBeingBought() {
		return mountBeingBought;
	}

	public void setMountBeingBought(int mountBeingBought) {
		this.mountBeingBought = mountBeingBought;
	}

	public Location getHorseSageLocation() {
		return horseSageLocation;
	}

	public void setHorseSageLocation(Location horseSageLocation) {
		this.horseSageLocation = horseSageLocation;
	}

	public int getSummonMount() {
		return summonMount;
	}

	public void setSummonMount(int summonMount) {
		this.summonMount = summonMount;
	}

	public Location getSummonLocation() {
		return summonLocation;
	}

	public void setSummonLocation(Location summonLocation) {
		this.summonLocation = summonLocation;
	}

	public ItemStack getSummonItem() {
		return summonItem;
	}

	public void setSummonItem(ItemStack summonItem) {
		this.summonItem = summonItem;
	}

	public Entity getMount() {
		return mount;
	}

	public void setMount(Entity mount) {
		this.mount = mount;
	}

	public Entity getMule() {
		return mule;
	}

	public void setMule(Entity mule) {
		this.mule = mule;
	}

	public Inventory getMuleInventory() {
		return muleInventory;
	}

	public void setMuleInventory(Inventory muleInventory) {
		this.muleInventory = muleInventory;
	}

	public String getMuleItemList() {
		return muleItemList;
	}

	public void setMuleItemList(String muleItemList) {
		this.muleItemList = muleItemList;
	}

	public boolean isInShop() {
		return inShop;
	}

	public void setInShop(boolean inShop) {
		this.inShop = inShop;
	}

	public String getPartyInvite() {
		return partyInvite;
	}

	public void setPartyInvite(String partyInvite) {
		this.partyInvite = partyInvite;
	}

	public long getPartyInviteTime() {
		return partyInviteTime;
	}

	public void setPartyInviteTime(long partyInviteTime) {
		this.partyInviteTime = partyInviteTime;
	}

	public Party getParty() {
		return party;
	}

	public void setParty(Party party) {
		this.party = party;
	}

	public String getPartyLoot() {
		return partyLoot;
	}

	public void setPartyLoot(String partyLoot) {
		this.partyLoot = partyLoot;
	}

	public int getPartyLootIndex() {
		return partyLootIndex;
	}

	public void setPartyLootIndex(int partyLootIndex) {
		this.partyLootIndex = partyLootIndex;
	}

	public boolean isPartyOnly() {
		return partyOnly;
	}

	public void setPartyOnly(boolean partyOnly) {
		this.partyOnly = partyOnly;
	}

	public String getRank() {
		return rank;
	}

	public void setRank(String rank) {
		this.rank = rank;
	}

	public int getRankForumGroup() {
		return rankForumGroup;
	}

	public void setRankForumGroup(int rankForumGroup) {
		this.rankForumGroup = rankForumGroup;
	}

	public List<String> getPets() {
		return pets;
	}

	public void setPets(List<String> pets) {
		this.pets = pets;
	}

	public long getPetSpawnDelay() {
		return petSpawnDelay;
	}

	public void setPetSpawnDelay(long petSpawnDelay) {
		this.petSpawnDelay = petSpawnDelay;
	}

	public List<Entity> getPetEntities() {
		return petEntities;
	}

	public void setPetEntities(List<Entity> petEntities) {
		this.petEntities = petEntities;
	}

	public Location getNamingPet() {
		return namingPet;
	}

	public void setNamingPet(Location namingPet) {
		this.namingPet = namingPet;
	}

	public String getPetType() {
		return petType;
	}

	public void setPetType(String petType) {
		this.petType = petType;
	}

	public List<String> getPhraseList() {
		return phraseList;
	}

	public void setPhraseList(List<String> phraseList) {
		this.phraseList = phraseList;
	}

	public int getSlowMining() {
		return slowMining;
	}

	public void setSlowMining(int slowMining) {
		this.slowMining = slowMining;
	}

	public long getLastSwing() {
		return lastSwing;
	}

	public void setLastSwing(long lastSwing) {
		this.lastSwing = lastSwing;
	}

	public Location getPlayerFishingSpot() {
		return playerFishingSpot;
	}

	public void setPlayerFishingSpot(Location playerFishingSpot) {
		this.playerFishingSpot = playerFishingSpot;
	}

	public HashMap<Block, Furnace> getFurnaceInventory() {
		return furnaceInventory;
	}

	public void setFurnaceInventory(HashMap<Block, Furnace> furnaceInventory) {
		this.furnaceInventory = furnaceInventory;
	}

	public boolean isIgnoreFurnaceOpenEvent() {
		return ignoreFurnaceOpenEvent;
	}

	public void setIgnoreFurnaceOpenEvent(boolean ignoreFurnaceOpenEvent) {
		this.ignoreFurnaceOpenEvent = ignoreFurnaceOpenEvent;
	}

	public int getFishCaughtCount() {
		return fishCaughtCount;
	}

	public void setFishCaughtCount(int fishCaughtCount) {
		this.fishCaughtCount = fishCaughtCount;
	}

	public int getFishHealthRegen() {
		return fishHealthRegen;
	}

	public void setFishHealthRegen(int fishHealthRegen) {
		this.fishHealthRegen = fishHealthRegen;
	}

	public int getFishEnergyRegen() {
		return fishEnergyRegen;
	}

	public void setFishEnergyRegen(int fishEnergyRegen) {
		this.fishEnergyRegen = fishEnergyRegen;
	}

	public int getFishBonusDamage() {
		return fishBonusDamage;
	}

	public void setFishBonusDamage(int fishBonusDamage) {
		this.fishBonusDamage = fishBonusDamage;
	}

	public int getFishBonusArmor() {
		return fishBonusArmor;
	}

	public void setFishBonusArmor(int fishBonusArmor) {
		this.fishBonusArmor = fishBonusArmor;
	}

	public int getFishBonusBlock() {
		return fishBonusBlock;
	}

	public void setFishBonusBlock(int fishBonusBlock) {
		this.fishBonusBlock = fishBonusBlock;
	}

	public int getFishBonusLifesteal() {
		return fishBonusLifesteal;
	}

	public void setFishBonusLifesteal(int fishBonusLifesteal) {
		this.fishBonusLifesteal = fishBonusLifesteal;
	}

	public int getFishBonusCriticalHit() {
		return fishBonusCriticalHit;
	}

	public void setFishBonusCriticalHit(int fishBonusCriticalHit) {
		this.fishBonusCriticalHit = fishBonusCriticalHit;
	}

	public int getCurrentItemBeingBought() {
		return currentItemBeingBought;
	}

	public void setCurrentItemBeingBought(int currentItemBeingBought) {
		this.currentItemBeingBought = currentItemBeingBought;
	}

	public int getShopPage() {
		return shopPage;
	}

	public void setShopPage(int shopPage) {
		this.shopPage = shopPage;
	}

	public String getShopCurrency() {
		return shopCurrency;
	}

	public void setShopCurrency(String shopCurrency) {
		this.shopCurrency = shopCurrency;
	}

	public long getRecentMovement() {
		return recentMovement;
	}

	public void setRecentMovement(long recentMovement) {
		this.recentMovement = recentMovement;
	}

	public Location getSavedOutOfRealmLocation() {
		return savedOutOfRealmLocation;
	}

	public void setSavedOutOfRealmLocation(Location savedOutOfRealmLocation) {
		this.savedOutOfRealmLocation = savedOutOfRealmLocation;
	}

	public String getPortalMapCoords() {
		return portalMapCoords;
	}

	public void setPortalMapCoords(String portalMapCoords) {
		this.portalMapCoords = portalMapCoords;
	}

	public Location getInventoryPortalMap() {
		return inventoryPortalMap;
	}

	public void setInventoryPortalMap(Location inventoryPortalMap) {
		this.inventoryPortalMap = inventoryPortalMap;
	}

	public boolean isHasPortal() {
		return hasPortal;
	}

	public void setHasPortal(boolean hasPortal) {
		this.hasPortal = hasPortal;
	}

	public long getPortalCooldown() {
		return portalCooldown;
	}

	public void setPortalCooldown(long portalCooldown) {
		this.portalCooldown = portalCooldown;
	}

	public String getRealmUpgradeCode() {
		return realmUpgradeCode;
	}

	public void setRealmUpgradeCode(String realmUpgradeCode) {
		this.realmUpgradeCode = realmUpgradeCode;
	}

	public String getRealmPercent() {
		return realmPercent;
	}

	public void setRealmPercent(String realmPercent) {
		this.realmPercent = realmPercent;
	}

	public String getRealmTitle() {
		return realmTitle;
	}

	public void setRealmTitle(String realmTitle) {
		this.realmTitle = realmTitle;
	}

	public int getRealmTier() {
		return realmTier;
	}

	public void setRealmTier(int realmTier) {
		this.realmTier = realmTier;
	}

	public int getSavedLevels() {
		return savedLevels;
	}

	public void setSavedLevels(int savedLevels) {
		this.savedLevels = savedLevels;
	}

	public boolean isRealmLoadedStatus() {
		return realmLoadedStatus;
	}

	public void setRealmLoadedStatus(boolean realmLoadedStatus) {
		this.realmLoadedStatus = realmLoadedStatus;
	}

	public long getSafeRealm() {
		return safeRealm;
	}

	public void setSafeRealm(long safeRealm) {
		this.safeRealm = safeRealm;
	}

	public long getFlyingRealm() {
		return flyingRealm;
	}

	public void setFlyingRealm(long flyingRealm) {
		this.flyingRealm = flyingRealm;
	}

	public long getRealmResetCooldown() {
		return realmResetCooldown;
	}

	public void setRealmResetCooldown(long realmResetCooldown) {
		this.realmResetCooldown = realmResetCooldown;
	}

	public List<String> getBuildList() {
		return buildList;
	}

	public void setBuildList(List<String> buildList) {
		this.buildList = buildList;
	}

	public boolean isReadyWorld() {
		return readyWorld;
	}

	public void setReadyWorld(boolean readyWorld) {
		this.readyWorld = readyWorld;
	}

	public boolean isCorruptWorld() {
		return corruptWorld;
	}

	public void setCorruptWorld(boolean corruptWorld) {
		this.corruptWorld = corruptWorld;
	}

	public long getGodMode() {
		return godMode;
	}

	public void setGodMode(long godMode) {
		this.godMode = godMode;
	}

	public int getDeaths() {
		return deaths;
	}

	public void setDeaths(int deaths) {
		this.deaths = deaths;
	}

	public List<Integer> getKills() {
		return kills;
	}

	public void setKills(List<Integer> kills) {
		this.kills = kills;
	}

	public int getMobKills() {
		return mobKills;
	}

	public void setMobKills(int mobKills) {
		this.mobKills = mobKills;
	}

	public List<Integer> getDuelStatistics() {
		return duelStatistics;
	}

	public void setDuelStatistics(List<Integer> duelStatistics) {
		this.duelStatistics = duelStatistics;
	}

	public int getInventoryUpdate() {
		return inventoryUpdate;
	}

	public void setInventoryUpdate(int inventoryUpdate) {
		this.inventoryUpdate = inventoryUpdate;
	}

	public long getWarnedDurability() {
		return warnedDurability;
	}

	public void setWarnedDurability(long warnedDurability) {
		this.warnedDurability = warnedDurability;
	}

	public ItemStack getRepair() {
		return repair;
	}

	public void setRepair(ItemStack repair) {
		this.repair = repair;
	}

	public Item getItemRepair() {
		return itemRepair;
	}

	public void setItemRepair(Item itemRepair) {
		this.itemRepair = itemRepair;
	}

	public Location getAnvil() {
		return anvil;
	}

	public void setAnvil(Location anvil) {
		this.anvil = anvil;
	}

	public int getRepairState() {
		return repairState;
	}

	public void setRepairState(int repairState) {
		this.repairState = repairState;
	}

	public String getZoneType() {
		return zoneType;
	}

	public void setZoneType(String zoneType) {
		this.zoneType = zoneType;
	}

	public boolean isRecentCraft() {
		return recentCraft;
	}

	public void setRecentCraft(boolean recentCraft) {
		this.recentCraft = recentCraft;
	}

	public boolean isInInventory() {
		return inInventory;
	}

	public void setInInventory(boolean inInventory) {
		this.inInventory = inInventory;
	}

	public boolean isRecentBlockEvent() {
		return recentBlockEvent;
	}

	public void setRecentBlockEvent(boolean recentBlockEvent) {
		this.recentBlockEvent = recentBlockEvent;
	}

	public int getShopLevel() {
		return shopLevel;
	}

	public void setShopLevel(int shopLevel) {
		this.shopLevel = shopLevel;
	}

	public Block getInverseShop() {
		return inverseShop;
	}

	public void setInverseShop(Block inverseShop) {
		this.inverseShop = inverseShop;
	}

	public Inventory getShopStock() {
		return shopStock;
	}

	public void setShopStock(Inventory shopStock) {
		this.shopStock = shopStock;
	}

	public ItemStack getItemBeingStocked() {
		return itemBeingStocked;
	}

	public void setItemBeingStocked(ItemStack itemBeingStocked) {
		this.itemBeingStocked = itemBeingStocked;
	}

	public int getShopServer() {
		return shopServer;
	}

	public void setShopServer(int shopServer) {
		this.shopServer = shopServer;
	}

	public String getShopBeingBrowsed() {
		return shopBeingBrowsed;
	}

	public void setShopBeingBrowsed(String shopBeingBrowsed) {
		this.shopBeingBrowsed = shopBeingBrowsed;
	}

	public String getShopUpgradeCode() {
		return shopUpgradeCode;
	}

	public void setShopUpgradeCode(String shopUpgradeCode) {
		this.shopUpgradeCode = shopUpgradeCode;
	}

	public long getLastShopOpen() {
		return lastShopOpen;
	}

	public void setLastShopOpen(long lastShopOpen) {
		this.lastShopOpen = lastShopOpen;
	}

	public Inventory getCollectionBin() {
		return collectionBin;
	}

	public void setCollectionBin(Inventory collectionBin) {
		this.collectionBin = collectionBin;
	}

	public boolean isPriceUpdateNeeded() {
		return priceUpdateNeeded;
	}

	public void setPriceUpdateNeeded(boolean priceUpdateNeeded) {
		this.priceUpdateNeeded = priceUpdateNeeded;
	}

	public boolean isOpeningShop() {
		return openingShop;
	}

	public void setOpeningShop(boolean openingShop) {
		this.openingShop = openingShop;
	}

	public boolean isNeedSQLUpdate() {
		return needSQLUpdate;
	}

	public void setNeedSQLUpdate(boolean needSQLUpdate) {
		this.needSQLUpdate = needSQLUpdate;
	}

	public String getTp() {
		return tp;
	}

	public void setTp(String tp) {
		this.tp = tp;
	}

	public int getTpEffect() {
		return tpEffect;
	}

	public void setTpEffect(int tpEffect) {
		this.tpEffect = tpEffect;
	}

	public Location getTpLocation() {
		return tpLocation;
	}

	public void setTpLocation(Location tpLocation) {
		this.tpLocation = tpLocation;
	}

	public long getProcessingMoveTeleport() {
		return processingMoveTeleport;
	}

	public void setProcessingMoveTeleport(long processingMoveTeleport) {
		this.processingMoveTeleport = processingMoveTeleport;
	}

	public Location getWarp() {
		return warp;
	}

	public void setWarp(Location warp) {
		this.warp = warp;
	}

	public Player getTrade() {
		return trade;
	}

	public void setTrade(Player trade) {
		this.trade = trade;
	}

	public Player getTradePartners() {
		return tradePartners;
	}

	public void setTradePartners(Player tradePartners) {
		this.tradePartners = tradePartners;
	}

	public Inventory getTradeSecure() {
		return tradeSecure;
	}

	public void setTradeSecure(Inventory tradeSecure) {
		this.tradeSecure = tradeSecure;
	}

	public long getLastInventoryClose() {
		return lastInventoryClose;
	}

	public void setLastInventoryClose(long lastInventoryClose) {
		this.lastInventoryClose = lastInventoryClose;
	}

	public List<String> getQuest() {
		return quest;
	}

	public void setQuest(List<String> quest) {
		this.quest = quest;
	}

	public List<String> getCompletionDelay() {
		return completionDelay;
	}

	public void setCompletionDelay(List<String> completionDelay) {
		this.completionDelay = completionDelay;
	}

	public boolean isSkipConfirm() {
		return skipConfirm;
	}

	public void setSkipConfirm(boolean skipConfirm) {
		this.skipConfirm = skipConfirm;
	}

	public boolean isLeaveConfirm() {
		return leaveConfirm;
	}

	public void setLeaveConfirm(boolean leaveConfirm) {
		this.leaveConfirm = leaveConfirm;
	}

	public boolean isEnchantScroll() {
		return enchantScroll;
	}

	public void setEnchantScroll(boolean enchantScroll) {
		this.enchantScroll = enchantScroll;
	}

	public boolean isOnIsland() {
		return onIsland;
	}

	public void setOnIsland(boolean onIsland) {
		this.onIsland = onIsland;
	}

	public WeatherType getWeather() {
		return weather;
	}

	public void setWeather(WeatherType weather) {
		this.weather = weather;
	}

	public long getLastLocalLogin() {
		return lastLocalLogin;
	}

	public void setLastLocalLogin(long lastLocalLogin) {
		this.lastLocalLogin = lastLocalLogin;
	}
	
	public void updateStats() {
	    Player p = getPlayer();
	    ItemMechanics.str_data.put(name, ItemMechanics.generateTotalStrVal(p));
	    ItemMechanics.dex_data.put(name, ItemMechanics.generateTotalDexVal(p));
	    ItemMechanics.int_data.put(name, ItemMechanics.generateTotalIntVal(p));
	    ItemMechanics.vit_data.put(name, ItemMechanics.generateTotalVitVal(p));
	}

    public int getRegenFoodBonus() {
        return regenFoodBonus;
    }

    public void setRegenFoodBonus(int regenFoodBonus) {
        this.regenFoodBonus = regenFoodBonus;
    }

    public boolean isBuyingItem() {
        return isBuyingItem;
    }

    public void setBuyingItem(boolean isBuyingItem) {
        this.isBuyingItem = isBuyingItem;
    }


    public ItemStack getItemStackBeingBought() {
        return itemStackBeingBought;
    }

    public void setItemStackBeingBought(ItemStack itemStackBeingBought) {
        this.itemStackBeingBought = itemStackBeingBought;
    }

    public int getRegenTimer() {
        return regenTimer;
    }

    public void setRegenTimer(int regenTimer) {
        this.regenTimer = regenTimer;
    }

    public Location getTeleportLoc() {
        return teleportLoc;
    }

    public void setTeleportLoc(Location teleportLoc) {
        this.teleportLoc = teleportLoc;
    }
	
}
