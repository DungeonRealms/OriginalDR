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
import me.vaqxine.ShopMechanics.ShopMechanics;
import me.vaqxine.SpawnMechanics.SpawnMechanics;
import me.vaqxine.SubscriberMechanics.SubscriberMechanics;
import me.vaqxine.TeleportationMechanics.TeleportationMechanics;
import me.vaqxine.TradeMechanics.TradeMechanics;
import me.vaqxine.TutorialMechanics.TutorialMechanics;
import me.vaqxine.WeatherMechanics.WeatherMechanics;
import me.vaqxine.database.ConnectionPool;
import me.vaqxine.enums.CC;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class Main extends JavaPlugin {

	public static AchievmentMechanics achievmentMechanics;
	public static BossMechanics bossMechanics;
	public static ChatMechanics chatMechanics;
	public static CommunityMechanics communityMechanics;
	public static DonationMechanics donationMechanics;
	public static DuelMechanics duelMechanics;
	public static EcashMechanics ecashMechanics;
	public static EnchantMechanics enchantMechanics;
	public static FatigueMechanics fatigueMechanics;
	public static GuildMechanics guildMechanics;
	public static HealthMechanics healthMechanics;
	public static InstanceMechanics instanceMechanics;
	public static ItemMechanics itemMechanics;
	public static KarmaMechanics karmaMechanics;
	public static LootMechanics lootMechanics;
	public static MerchantMechanics merchantMechanics;
	public static ModerationMechanics moderationMechanics;
	public static MoneyMechanics moneyMechanics;
	public static MonsterMechanics monsterMechanics;
	public static MountMechanics mountMechanics;
	public static PartyMechanics partyMechanics;
	public static PermissionMechanics permissionMechanics;
	public static PetMechanics petMechanics;
	public static PowerupMechanics powerupMechanics;
	public static ProfessionMechanics professionMechanics;
	public static RealmMechanics realmMechanics;
	public static RecordMechanics recordMechanics;
	public static RepairMechanics repairMechanics;
	public static RestrictionMechanics restrictionMechanics;
	public static ShopMechanics shopMechanics;
	public static SpawnMechanics spawnMechanics;
	public static SubscriberMechanics subscriberMechanics;
	public static TeleportationMechanics teleportationMechanics;
	public static TradeMechanics tradeMechanics;
	public static TutorialMechanics tutorialMechanics;
	public static WeatherMechanics weatherMechanics;
	public static Hive hive;
	public static HiveServer hiveServer;
	
	public static Main plugin;
	public static Logger log;
	
	public static List<String> devs = Arrays.asList("Vilsol", "iFamasssxD", "Vaquxine", "Availer");
	
	public void onEnable(){
		plugin = this;
		log = this.getLogger();
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
		
		new BukkitRunnable(){
			@Override
			public void run() {
				try{
					ConnectionPool.refresh = true;
				} catch (NoClassDefFoundError e){
					System.err.println("Couldn't refresh connection. Class not found!");
				}
			}
		}.runTaskTimerAsynchronously(Main.plugin, 240 * 20L, 240 * 20L);
	}
	
	public void onDisable(){
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
	
	/**
	 * Debug
	 */
	public static void d(Object o){
		Main.plugin.getLogger().info(CC.CYAN + o.toString() + CC.WHITE);
	}
	
	public static boolean isDev(String s){
		return devs.contains(s);
	}
	
}
