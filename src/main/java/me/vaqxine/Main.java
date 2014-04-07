package me.vaqxine;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import me.vaqxine.AchievmentMechanics.AchievmentMechanics;
import me.vaqxine.BossMechanics.BossMechanics;
import me.vaqxine.ChatMechanics.ChatMechanics;
import me.vaqxine.CommunityMechanics.CommunityMechanics;
import me.vaqxine.DonationMechanics.DonationMechanics;
import me.vaqxine.DuelMechanics.DuelMechanics;
import me.vaqxine.EcashMechanics.EcashMechanics;
import me.vaqxine.EnchantMechanics.EnchantMechanics;
import me.vaqxine.FatigueMechanics.FatigueMechanics;
import me.vaqxine.GuildMechanics.GuildMechanics;
import me.vaqxine.HealthMechanics.HealthMechanics;
import me.vaqxine.HearthstoneMechanics.HearthstoneMechanics;
import me.vaqxine.Hive.Hive;
import me.vaqxine.HiveServer.HiveServer;
import me.vaqxine.InstanceMechanics.InstanceMechanics;
import me.vaqxine.ItemMechanics.ItemMechanics;
import me.vaqxine.KarmaMechanics.KarmaMechanics;
import me.vaqxine.LootMechanics.LootMechanics;
import me.vaqxine.MerchantMechanics.MerchantMechanics;
import me.vaqxine.ModerationMechanics.ModerationMechanics;
import me.vaqxine.MoneyMechanics.MoneyMechanics;
import me.vaqxine.MonsterMechanics.MonsterMechanics;
import me.vaqxine.MountMechanics.MountMechanics;
import me.vaqxine.PartyMechanics.PartyMechanics;
import me.vaqxine.PermissionMechanics.PermissionMechanics;
import me.vaqxine.PetMechanics.PetMechanics;
import me.vaqxine.PowerupMechanics.PowerupMechanics;
import me.vaqxine.ProfessionMechanics.ProfessionMechanics;
import me.vaqxine.RealmMechanics.RealmMechanics;
import me.vaqxine.RecordMechanics.RecordMechanics;
import me.vaqxine.RepairMechanics.RepairMechanics;
import me.vaqxine.RestrictionMechanics.RestrictionMechanics;
import me.vaqxine.ScoreboardMechanics.ScoreboardMechanics;
import me.vaqxine.ShopMechanics.ShopMechanics;
import me.vaqxine.SpawnMechanics.SpawnMechanics;
import me.vaqxine.SubscriberMechanics.SubscriberMechanics;
import me.vaqxine.TeleportationMechanics.TeleportationMechanics;
import me.vaqxine.TradeMechanics.TradeMechanics;
import me.vaqxine.TutorialMechanics.TutorialMechanics;
import me.vaqxine.WeatherMechanics.WeatherMechanics;
import me.vaqxine.database.ConnectionPool;
import me.vaqxine.enums.CC;
import me.vaqxine.holograms.Hologram;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class Main extends JavaPlugin implements Listener {
	
	private static AchievmentMechanics achievmentMechanics;
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
	private static Hive hive;
	private static HiveServer hiveServer;
	
	public static Main plugin;
	public static Logger log;
	
	private static List<String> devs = Arrays.asList("Vilsol", "iFamasssxD", "Vaquxine", "felipepcjr");
	
	public void onEnable() {
		plugin = this;
		log = this.getLogger();
		
		getServer().getPluginManager().registerEvents(new ScoreboardMechanics(), this);
		getServer().getPluginManager().registerEvents(this, this);
		
		hearthstoneMechanics = new HearthstoneMechanics();
		achievmentMechanics = new AchievmentMechanics();
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
		
		hive.onEnable();
		hearthstoneMechanics.onEnable();
		hiveServer.onEnable();
		achievmentMechanics.onEnable();
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
		
		new BukkitRunnable() {
			@Override
			public void run() {
				try {
					ConnectionPool.refresh = true;
				} catch(NoClassDefFoundError e) {
					System.err.println("Couldn't refresh connection. Class not found!");
				}
			}
		}.runTaskTimerAsynchronously(Main.plugin, 240 * 20L, 240 * 20L);
	}
	
	public void onDisable() {
		ConnectionPool.refresh = false;
		achievmentMechanics.onDisable();
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
		shopMechanics.onDisable();
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
	public void onPlayerJoinEvent(PlayerJoinEvent e){
		for(Hologram h : Hologram.getHolograms()){
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
		if(o == null){
			Main.plugin.getLogger().info(color + "null" + CC.DEFAULT);
			return;
		}
		Main.plugin.getLogger().info(color + o.toString() + CC.DEFAULT);
	}
	
	public static void dl(Object o){
		String fullClassName = Thread.currentThread().getStackTrace()[2].getClassName();  
		String className = fullClassName.substring(fullClassName.lastIndexOf(".") + 1);
		String methodName = Thread.currentThread().getStackTrace()[2].getMethodName();
		int lineNumber = Thread.currentThread().getStackTrace()[2].getLineNumber();
		Main.plugin.getLogger().info(CC.MAGENTA + className + "." + methodName + "():" + lineNumber + " - " + CC.CYAN + o + CC.DEFAULT);
	}
	
	public static boolean isDev(String s) {
		return devs.contains(s);
	}
	
}
