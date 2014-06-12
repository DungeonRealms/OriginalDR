package minecade.dungeonrealms;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import me.vilsol.betanpc.BetaNPC;
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

    public static Main plugin;
    public static Logger log;

    private static List<String> devs = Arrays.asList("Vilsol", "iFamasssxD", "Vaquxine", "Azubuso", "EtherealTemplar");
    private static List<String> masters = Arrays.asList("Bradez1571", "felipepcjr");

    public void onEnable() {
        plugin = this;
        log = this.getLogger();

        int serverid = Integer.parseInt(getServer().getMotd().split("-")[1].split(" ")[0]);

        if (getServer().getMotd().contains("US-99") || getServer().getMotd().contains("US-B1") || (serverid >= 100 && serverid <= 110)) {
            Config.sql_url = "jdbc:mysql://" + Config.Hive_IP + ":" + Config.SQL_port + "/dungeonrealms_test";
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
        if (o == null) {
            Main.plugin.getLogger().info(color + "null" + CC.DEFAULT);
            return;
        }
        Main.plugin.getLogger().info(color + o.toString() + CC.DEFAULT);
    }

    public static void dl(Object o) {
        String fullClassName = Thread.currentThread().getStackTrace()[2].getClassName();
        String className = fullClassName.substring(fullClassName.lastIndexOf(".") + 1);
        String methodName = Thread.currentThread().getStackTrace()[2].getMethodName();
        int lineNumber = Thread.currentThread().getStackTrace()[2].getLineNumber();
        Main.plugin.getLogger().info(CC.MAGENTA + className + "." + methodName + "():" + lineNumber + " - " + CC.CYAN + o + CC.DEFAULT);
    }

    public static boolean isDev(String s) {
        return devs.contains(s);
    }

    public static boolean isMaster(String s) {
        if (devs.contains(s))
            return true;
        if (masters.contains(s))
            return true;
        return false;
    }

}
