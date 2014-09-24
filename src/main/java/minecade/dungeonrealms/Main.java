package minecade.dungeonrealms;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import me.vilsol.betanpc.BetaNPC;
import me.vilsol.foodvendor.FoodVendor;
import me.vilsol.itemgenerator.ItemGenerator;
import minecade.dungeonrealms.AchievementMechanics.AchievementMechanics;
import minecade.dungeonrealms.BossMechanics.BossMechanics;
import minecade.dungeonrealms.ChatMechanics.ChatMechanics;
import minecade.dungeonrealms.CommunityMechanics.CommunityMechanics;
import minecade.dungeonrealms.DonationMechanics.DonationMechanics;
import minecade.dungeonrealms.DuelMechanics.DuelMechanics;
import minecade.dungeonrealms.EcashMechanics.EcashMechanics;
import minecade.dungeonrealms.EnchantMechanics.EnchantMechanics;
import minecade.dungeonrealms.FatigueMechanics.FatigueMechanics;
import minecade.dungeonrealms.GuildMechanics.GuildMechanics;
import minecade.dungeonrealms.HealthMechanics.HealthMechanics;
import minecade.dungeonrealms.HearthstoneMechanics.HearthstoneMechanics;
import minecade.dungeonrealms.Hive.Hive;
import minecade.dungeonrealms.HiveServer.HiveServer;
import minecade.dungeonrealms.InstanceMechanics.InstanceMechanics;
import minecade.dungeonrealms.ItemMechanics.ItemMechanics;
import minecade.dungeonrealms.KarmaMechanics.KarmaMechanics;
import minecade.dungeonrealms.LevelMechanics.LevelMechanics;
import minecade.dungeonrealms.LevelMechanics.commands.CommandSetLevel;
import minecade.dungeonrealms.LootMechanics.LootMechanics;
import minecade.dungeonrealms.MerchantMechanics.MerchantMechanics;
import minecade.dungeonrealms.ModerationMechanics.ModerationMechanics;
import minecade.dungeonrealms.MoneyMechanics.MoneyMechanics;
import minecade.dungeonrealms.MonsterMechanics.MonsterMechanics;
import minecade.dungeonrealms.MountMechanics.MountMechanics;
import minecade.dungeonrealms.PartyMechanics.PartyMechanics;
import minecade.dungeonrealms.PermissionMechanics.PermissionMechanics;
import minecade.dungeonrealms.PetMechanics.PetMechanics;
import minecade.dungeonrealms.PowerupMechanics.PowerupMechanics;
import minecade.dungeonrealms.ProfessionMechanics.ProfessionMechanics;
import minecade.dungeonrealms.RealmMechanics.RealmMechanics;
import minecade.dungeonrealms.RecordMechanics.RecordMechanics;
import minecade.dungeonrealms.RepairMechanics.RepairMechanics;
import minecade.dungeonrealms.RestrictionMechanics.RestrictionMechanics;
import minecade.dungeonrealms.ScoreboardMechanics.ScoreboardMechanics;
import minecade.dungeonrealms.ShopMechanics.ShopMechanics;
import minecade.dungeonrealms.SpawnMechanics.SpawnMechanics;
import minecade.dungeonrealms.SubscriberMechanics.SubscriberMechanics;
import minecade.dungeonrealms.TeleportationMechanics.TeleportationMechanics;
import minecade.dungeonrealms.TradeMechanics.TradeMechanics;
import minecade.dungeonrealms.TutorialMechanics.TutorialMechanics;
import minecade.dungeonrealms.WeatherMechanics.WeatherMechanics;
import minecade.dungeonrealms.config.Config;
import minecade.dungeonrealms.database.ConnectionPool;
import minecade.dungeonrealms.enums.CC;
import minecade.dungeonrealms.holograms.Hologram;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class Main extends JavaPlugin implements Listener {

    private static AchievementMechanics achievementMechanics;
    private static BossMechanics bossMechanics;
    private static ChatMechanics chatMechanics;
    private static CommunityMechanics communityMechanics;
    private static DonationMechanics donationMechanics;
    private static DuelMechanics duelMechanics;
    private static EcashMechanics ecashMechanics;
    private static EnchantMechanics enchantMechanics;
    private static FatigueMechanics fatigueMechanics;
    private static GuildMechanics guildMechanics;
    private static HealthMechanics healthMechanics;
    private static InstanceMechanics instanceMechanics;
    private static ItemMechanics itemMechanics;
    private static KarmaMechanics karmaMechanics;
    private static LootMechanics lootMechanics;
    private static MerchantMechanics merchantMechanics;
    private static ModerationMechanics moderationMechanics;
    private static MoneyMechanics moneyMechanics;
    private static MonsterMechanics monsterMechanics;
    private static MountMechanics mountMechanics;
    private static PartyMechanics partyMechanics;
    private static PermissionMechanics permissionMechanics;
    private static PetMechanics petMechanics;
    private static PowerupMechanics powerupMechanics;
    private static ProfessionMechanics professionMechanics;
    private static RealmMechanics realmMechanics;
    private static RecordMechanics recordMechanics;
    private static RepairMechanics repairMechanics;
    private static RestrictionMechanics restrictionMechanics;
    private static ShopMechanics shopMechanics;
    private static SpawnMechanics spawnMechanics;
    private static SubscriberMechanics subscriberMechanics;
    private static TeleportationMechanics teleportationMechanics;
    private static TradeMechanics tradeMechanics;
    private static TutorialMechanics tutorialMechanics;
    private static WeatherMechanics weatherMechanics;
    private static HearthstoneMechanics hearthstoneMechanics;
    private static LevelMechanics levelMechanics;
    private static Hive hive;
    private static HiveServer hiveServer;

    private static BetaNPC betaNPC;
    private static FoodVendor foodVendor;

    /**
     * Holds the <code>JavaPlugin</code> instance of the DungeonRealms plugin once enabled.
     */
    public static Main plugin;
    public static Logger log;

    private static final List<String> devs = Arrays.asList("EtherealTemplar", "Vilsol", "Rar349", "iFamasssxD");
    private static final List<String> masters = Arrays.asList("Bradez1571", "felipepcjr");

    @Override
    public void onEnable() {
        plugin = this;
        log = this.getLogger();

        int serverid = Integer.parseInt(getServer().getMotd().split("-")[1].split(" ")[0]);

        if (getServer().getMotd().contains("US-99") || getServer().getMotd().contains("US-B1") || (serverid >= 100 && serverid <= 110)) {
            Config.sql_url = "jdbc:mysql://" + Config.Hive_IP + ":" + Config.SQL_port + "/dungeonrealms_test";
            Config.realmPath = "/rdata/_beta-files_/";
        }

        getServer().getPluginManager().registerEvents(new ScoreboardMechanics(), this);
        getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(new LevelMechanics(), this);
        getServer().getPluginManager().registerEvents(new LogListener(), this);

        getCommand("isunomadyet").setExecutor(new CommandIsUnoMadYet());
        getCommand("setlevel").setExecutor(new CommandSetLevel());
        levelMechanics = new LevelMechanics();
        hearthstoneMechanics = new HearthstoneMechanics();
        achievementMechanics = new AchievementMechanics();
        bossMechanics = new BossMechanics();
        chatMechanics = new ChatMechanics();
        communityMechanics = new CommunityMechanics();
        donationMechanics = new DonationMechanics();
        duelMechanics = new DuelMechanics();
        ecashMechanics = new EcashMechanics();
        enchantMechanics = new EnchantMechanics();
        fatigueMechanics = new FatigueMechanics();
        guildMechanics = new GuildMechanics();
        healthMechanics = new HealthMechanics();
        instanceMechanics = new InstanceMechanics();
        itemMechanics = new ItemMechanics();
        karmaMechanics = new KarmaMechanics();
        lootMechanics = new LootMechanics();
        merchantMechanics = new MerchantMechanics();
        moderationMechanics = new ModerationMechanics();
        moneyMechanics = new MoneyMechanics();
        monsterMechanics = new MonsterMechanics();
        mountMechanics = new MountMechanics();
        partyMechanics = new PartyMechanics();
        permissionMechanics = new PermissionMechanics();
        petMechanics = new PetMechanics();
        powerupMechanics = new PowerupMechanics();
        professionMechanics = new ProfessionMechanics();
        realmMechanics = new RealmMechanics();
        recordMechanics = new RecordMechanics();
        repairMechanics = new RepairMechanics();
        restrictionMechanics = new RestrictionMechanics();
        shopMechanics = new ShopMechanics();
        spawnMechanics = new SpawnMechanics();
        subscriberMechanics = new SubscriberMechanics();
        teleportationMechanics = new TeleportationMechanics();
        tradeMechanics = new TradeMechanics();
        tutorialMechanics = new TutorialMechanics();
        weatherMechanics = new WeatherMechanics();
        hive = new Hive();
        hiveServer = new HiveServer();
        betaNPC = new BetaNPC();
        foodVendor = new FoodVendor();

        hive.onEnable();
        hearthstoneMechanics.onEnable();
        hiveServer.onEnable();
        achievementMechanics.onEnable();
        bossMechanics.onEnable();
        chatMechanics.onEnable();
        healthMechanics.onEnable();
        communityMechanics.onEnable();
        donationMechanics.onEnable();
        duelMechanics.onEnable();
        ecashMechanics.onEnable();
        enchantMechanics.onEnable();
        fatigueMechanics.onEnable();
        guildMechanics.onEnable();
        instanceMechanics.onEnable();
        itemMechanics.onEnable();
        karmaMechanics.onEnable();
        lootMechanics.onEnable();
        merchantMechanics.onEnable();
        moderationMechanics.onEnable();
        moneyMechanics.onEnable();
        monsterMechanics.onEnable();
        mountMechanics.onEnable();
        partyMechanics.onEnable();
        permissionMechanics.onEnable();
        petMechanics.onEnable();
        powerupMechanics.onEnable();
        professionMechanics.onEnable();
        realmMechanics.onEnable();
        recordMechanics.onEnable();
        repairMechanics.onEnable();
        restrictionMechanics.onEnable();
        shopMechanics.onEnable();
        spawnMechanics.onEnable();
        subscriberMechanics.onEnable();
        teleportationMechanics.onEnable();
        tradeMechanics.onEnable();
        tutorialMechanics.onEnable();
        weatherMechanics.onEnable();
        betaNPC.onEnable();
        levelMechanics.onEnable();
        foodVendor.onEnable();
        
        ItemGenerator.loadModifiers();

        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    ConnectionPool.refresh = true;
                } catch (NoClassDefFoundError e) {
                    System.err.println("Couldn't refresh connection. Class not found!");
                }
            }
        }.runTaskTimerAsynchronously(Main.plugin, 240 * 20L, 240 * 20L);

        new BukkitRunnable() {
            @Override
            public void run() {
                for (Hologram h : Hologram.getHolograms()) {
                    h.updateToNearbyPlayers();
                }
            }
        }.runTaskTimer(this, 20L * 5, 20L * 5);
    }

    @Override
    public void onDisable() {
        ConnectionPool.refresh = false;
        shopMechanics.onDisable();
        achievementMechanics.onDisable();
        bossMechanics.onDisable();
        chatMechanics.onDisable();
        communityMechanics.onDisable();
        duelMechanics.onDisable();
        ecashMechanics.onDisable();
        enchantMechanics.onDisable();
        fatigueMechanics.onDisable();
        guildMechanics.onDisable();
        healthMechanics.onDisable();
        instanceMechanics.onDisable();
        itemMechanics.onDisable();
        karmaMechanics.onDisable();
        lootMechanics.onDisable();
        merchantMechanics.onDisable();
        moderationMechanics.onDisable();
        moneyMechanics.onDisable();
        monsterMechanics.onDisable();
        mountMechanics.onDisable();
        partyMechanics.onDisable();
        permissionMechanics.onDisable();
        petMechanics.onDisable();
        powerupMechanics.onDisable();
        professionMechanics.onDisable();
        realmMechanics.onDisable();
        recordMechanics.onDisable();
        repairMechanics.onDisable();
        restrictionMechanics.onDisable();

        spawnMechanics.onDisable();
        subscriberMechanics.onDisable();
        teleportationMechanics.onDisable();
        tradeMechanics.onDisable();
        tutorialMechanics.onDisable();
        weatherMechanics.onDisable();
        hive.onDisable();
        hiveServer.onDisable();

    }

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent e) {
        for (Hologram h : Hologram.getHolograms()) {
            h.sendPacketsToPlayer(e.getPlayer());
        }
    }

    @EventHandler
    public void onChangeWorld(PlayerChangedWorldEvent e) {
        for (Hologram h : Hologram.getHolograms()) {
            if (h.getLocation().getWorld() == e.getPlayer().getWorld())
                h.sendPacketsToPlayer(e.getPlayer());
        }
    }

    /**
     * Debug
     */
    public static void d(Object o) {
        d(o, CC.CYAN);
    }

    public static void d(Object o, CC color) {
        Main.plugin.getLogger().log(Level.INFO, "{0}{1}{2}", new Object[]{color, o, CC.DEFAULT});
    }

    public static void dl(Object o) {
        String fullClassName = Thread.currentThread().getStackTrace()[2].getClassName();
        String className = fullClassName.substring(fullClassName.lastIndexOf(".") + 1);
        String methodName = Thread.currentThread().getStackTrace()[2].getMethodName();
        int lineNumber = Thread.currentThread().getStackTrace()[2].getLineNumber();
        Main.plugin.getLogger().log(Level.INFO, "{0}{1}.{2}():{3} - {4}{5}{6}",
                new Object[]{CC.MAGENTA, className, methodName, lineNumber, CC.CYAN, o, CC.DEFAULT});
    }

    /**
     * Checks if a player is a developer based on their name.
     * @param s the Minecraft username of the player to check.
     * @return true if the player is a developer, otherwise, false.
     */
    public static boolean isDev(String s) {
        return devs.contains(s);
    }

    /**
     * Checks if a player is a game master (GM), based on their name.
     * @param s the Minecraft username of the player to check.
     * @return true if the player is a game master or developer, otherwise, false.
     */
    public static boolean isMaster(String s) {
        return devs.contains(s) || masters.contains(s);
    }

    public static AchievementMechanics getAchievementMechanics() {
        return achievementMechanics;
    }

    public static void setAchievementMechanics(AchievementMechanics achievementMechanics) {
        Main.achievementMechanics = achievementMechanics;
    }

    public static BossMechanics getBossMechanics() {
        return bossMechanics;
    }

    public static void setBossMechanics(BossMechanics bossMechanics) {
        Main.bossMechanics = bossMechanics;
    }

    public static ChatMechanics getChatMechanics() {
        return chatMechanics;
    }

    public static void setChatMechanics(ChatMechanics chatMechanics) {
        Main.chatMechanics = chatMechanics;
    }

    public static CommunityMechanics getCommunityMechanics() {
        return communityMechanics;
    }

    public static void setCommunityMechanics(CommunityMechanics communityMechanics) {
        Main.communityMechanics = communityMechanics;
    }

    public static DonationMechanics getDonationMechanics() {
        return donationMechanics;
    }

    public static void setDonationMechanics(DonationMechanics donationMechanics) {
        Main.donationMechanics = donationMechanics;
    }

    public static DuelMechanics getDuelMechanics() {
        return duelMechanics;
    }

    public static void setDuelMechanics(DuelMechanics duelMechanics) {
        Main.duelMechanics = duelMechanics;
    }

    public static EcashMechanics getEcashMechanics() {
        return ecashMechanics;
    }

    public static void setEcashMechanics(EcashMechanics ecashMechanics) {
        Main.ecashMechanics = ecashMechanics;
    }

    public static EnchantMechanics getEnchantMechanics() {
        return enchantMechanics;
    }

    public static void setEnchantMechanics(EnchantMechanics enchantMechanics) {
        Main.enchantMechanics = enchantMechanics;
    }

    public static FatigueMechanics getFatigueMechanics() {
        return fatigueMechanics;
    }

    public static void setFatigueMechanics(FatigueMechanics fatigueMechanics) {
        Main.fatigueMechanics = fatigueMechanics;
    }

    public static GuildMechanics getGuildMechanics() {
        return guildMechanics;
    }

    public static void setGuildMechanics(GuildMechanics guildMechanics) {
        Main.guildMechanics = guildMechanics;
    }

    public static HealthMechanics getHealthMechanics() {
        return healthMechanics;
    }

    public static void setHealthMechanics(HealthMechanics healthMechanics) {
        Main.healthMechanics = healthMechanics;
    }

    public static InstanceMechanics getInstanceMechanics() {
        return instanceMechanics;
    }

    public static void setInstanceMechanics(InstanceMechanics instanceMechanics) {
        Main.instanceMechanics = instanceMechanics;
    }

    public static ItemMechanics getItemMechanics() {
        return itemMechanics;
    }

    public static void setItemMechanics(ItemMechanics itemMechanics) {
        Main.itemMechanics = itemMechanics;
    }

    public static KarmaMechanics getKarmaMechanics() {
        return karmaMechanics;
    }

    public static void setKarmaMechanics(KarmaMechanics karmaMechanics) {
        Main.karmaMechanics = karmaMechanics;
    }

    public static LootMechanics getLootMechanics() {
        return lootMechanics;
    }

    public static void setLootMechanics(LootMechanics lootMechanics) {
        Main.lootMechanics = lootMechanics;
    }

    public static MerchantMechanics getMerchantMechanics() {
        return merchantMechanics;
    }

    public static void setMerchantMechanics(MerchantMechanics merchantMechanics) {
        Main.merchantMechanics = merchantMechanics;
    }

    public static ModerationMechanics getModerationMechanics() {
        return moderationMechanics;
    }

    public static void setModerationMechanics(ModerationMechanics moderationMechanics) {
        Main.moderationMechanics = moderationMechanics;
    }

    public static MoneyMechanics getMoneyMechanics() {
        return moneyMechanics;
    }

    public static void setMoneyMechanics(MoneyMechanics moneyMechanics) {
        Main.moneyMechanics = moneyMechanics;
    }

    public static MonsterMechanics getMonsterMechanics() {
        return monsterMechanics;
    }

    public static void setMonsterMechanics(MonsterMechanics monsterMechanics) {
        Main.monsterMechanics = monsterMechanics;
    }

    public static MountMechanics getMountMechanics() {
        return mountMechanics;
    }

    public static void setMountMechanics(MountMechanics mountMechanics) {
        Main.mountMechanics = mountMechanics;
    }

    public static PartyMechanics getPartyMechanics() {
        return partyMechanics;
    }

    public static void setPartyMechanics(PartyMechanics partyMechanics) {
        Main.partyMechanics = partyMechanics;
    }

    public static PermissionMechanics getPermissionMechanics() {
        return permissionMechanics;
    }

    public static void setPermissionMechanics(PermissionMechanics permissionMechanics) {
        Main.permissionMechanics = permissionMechanics;
    }

    public static PetMechanics getPetMechanics() {
        return petMechanics;
    }

    public static void setPetMechanics(PetMechanics petMechanics) {
        Main.petMechanics = petMechanics;
    }

    public static PowerupMechanics getPowerupMechanics() {
        return powerupMechanics;
    }

    public static void setPowerupMechanics(PowerupMechanics powerupMechanics) {
        Main.powerupMechanics = powerupMechanics;
    }

    public static ProfessionMechanics getProfessionMechanics() {
        return professionMechanics;
    }

    public static void setProfessionMechanics(ProfessionMechanics professionMechanics) {
        Main.professionMechanics = professionMechanics;
    }

    public static RealmMechanics getRealmMechanics() {
        return realmMechanics;
    }

    public static void setRealmMechanics(RealmMechanics realmMechanics) {
        Main.realmMechanics = realmMechanics;
    }

    public static RecordMechanics getRecordMechanics() {
        return recordMechanics;
    }

    public static void setRecordMechanics(RecordMechanics recordMechanics) {
        Main.recordMechanics = recordMechanics;
    }

    public static RepairMechanics getRepairMechanics() {
        return repairMechanics;
    }

    public static void setRepairMechanics(RepairMechanics repairMechanics) {
        Main.repairMechanics = repairMechanics;
    }

    public static RestrictionMechanics getRestrictionMechanics() {
        return restrictionMechanics;
    }

    public static void setRestrictionMechanics(RestrictionMechanics restrictionMechanics) {
        Main.restrictionMechanics = restrictionMechanics;
    }

    public static ShopMechanics getShopMechanics() {
        return shopMechanics;
    }

    public static void setShopMechanics(ShopMechanics shopMechanics) {
        Main.shopMechanics = shopMechanics;
    }

    public static SpawnMechanics getSpawnMechanics() {
        return spawnMechanics;
    }

    public static void setSpawnMechanics(SpawnMechanics spawnMechanics) {
        Main.spawnMechanics = spawnMechanics;
    }

    public static SubscriberMechanics getSubscriberMechanics() {
        return subscriberMechanics;
    }

    public static void setSubscriberMechanics(SubscriberMechanics subscriberMechanics) {
        Main.subscriberMechanics = subscriberMechanics;
    }

    public static TeleportationMechanics getTeleportationMechanics() {
        return teleportationMechanics;
    }

    public static void setTeleportationMechanics(TeleportationMechanics teleportationMechanics) {
        Main.teleportationMechanics = teleportationMechanics;
    }

    public static TradeMechanics getTradeMechanics() {
        return tradeMechanics;
    }

    public static void setTradeMechanics(TradeMechanics tradeMechanics) {
        Main.tradeMechanics = tradeMechanics;
    }

    public static TutorialMechanics getTutorialMechanics() {
        return tutorialMechanics;
    }

    public static void setTutorialMechanics(TutorialMechanics tutorialMechanics) {
        Main.tutorialMechanics = tutorialMechanics;
    }

    public static WeatherMechanics getWeatherMechanics() {
        return weatherMechanics;
    }

    public static void setWeatherMechanics(WeatherMechanics weatherMechanics) {
        Main.weatherMechanics = weatherMechanics;
    }

    public static HearthstoneMechanics getHearthstoneMechanics() {
        return hearthstoneMechanics;
    }

    public static void setHearthstoneMechanics(HearthstoneMechanics hearthstoneMechanics) {
        Main.hearthstoneMechanics = hearthstoneMechanics;
    }

    public static LevelMechanics getLevelMechanics() {
        return levelMechanics;
    }

    public static void setLevelMechanics(LevelMechanics levelMechanics) {
        Main.levelMechanics = levelMechanics;
    }

    public static Hive getHive() {
        return hive;
    }

    public static void setHive(Hive hive) {
        Main.hive = hive;
    }

    public static HiveServer getHiveServer() {
        return hiveServer;
    }

    public static void setHiveServer(HiveServer hiveServer) {
        Main.hiveServer = hiveServer;
    }

    public static BetaNPC getBetaNPC() {
        return betaNPC;
    }

    public static void setBetaNPC(BetaNPC betaNPC) {
        Main.betaNPC = betaNPC;
    }

    public static Main getPlugin() {
        return plugin;
    }

    public static void setPlugin(Main plugin) {
        Main.plugin = plugin;
    }

    public static Logger getLog() {
        return log;
    }

    public static void setLog(Logger log) {
        Main.log = log;
    }

    public static List<String> getDevs() {
        return devs;
    }

    public static List<String> getMasters() {
        return masters;
    }

}