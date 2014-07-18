package me.vilsol.betanpc;

import java.util.Arrays;

import me.vilsol.betanpc.items.arrowmenu.SpawnTierFiveArrow;
import me.vilsol.betanpc.items.arrowmenu.SpawnTierFourArrow;
import me.vilsol.betanpc.items.arrowmenu.SpawnTierOneArrow;
import me.vilsol.betanpc.items.arrowmenu.SpawnTierThreeArrow;
import me.vilsol.betanpc.items.arrowmenu.SpawnTierTwoArrow;
import me.vilsol.betanpc.items.mainmenu.ChangeLevel;
import me.vilsol.betanpc.items.mainmenu.ExitMenu;
import me.vilsol.betanpc.items.mainmenu.SpawnGems;
import me.vilsol.betanpc.items.mainmenu.SpawnItem;
import me.vilsol.betanpc.items.mainmenu.SpawnMiscItems;
import me.vilsol.betanpc.items.mainmenu.SpawnProfessionItem;
import me.vilsol.betanpc.items.miscitemmenu.SpawnArrows;
import me.vilsol.betanpc.items.miscitemmenu.SpawnFood;
import me.vilsol.betanpc.items.miscitemmenu.SpawnOrbsOfAlteration;
import me.vilsol.betanpc.items.miscitemmenu.SpawnPotions;
import me.vilsol.betanpc.items.miscitemmenu.SpawnScrolls;
import me.vilsol.betanpc.items.miscitemmenu.SpawnTeleportBooks;
import me.vilsol.betanpc.items.potionmenu.SpawnTierFivePotion;
import me.vilsol.betanpc.items.potionmenu.SpawnTierFourPotion;
import me.vilsol.betanpc.items.potionmenu.SpawnTierOnePotion;
import me.vilsol.betanpc.items.potionmenu.SpawnTierThreePotion;
import me.vilsol.betanpc.items.potionmenu.SpawnTierTwoPotion;
import me.vilsol.betanpc.items.professionmenu.SpawnFishingRod;
import me.vilsol.betanpc.items.professionmenu.SpawnPickaxe;
import me.vilsol.betanpc.items.scrollmenu.SpawnTierFiveScrolls;
import me.vilsol.betanpc.items.scrollmenu.SpawnTierFourScrolls;
import me.vilsol.betanpc.items.scrollmenu.SpawnTierOneScrolls;
import me.vilsol.betanpc.items.scrollmenu.SpawnTierThreeScrolls;
import me.vilsol.betanpc.items.scrollmenu.SpawnTierTwoScrolls;
import me.vilsol.betanpc.items.spawnmenu.Boots;
import me.vilsol.betanpc.items.spawnmenu.Chestplate;
import me.vilsol.betanpc.items.spawnmenu.Helmet;
import me.vilsol.betanpc.items.spawnmenu.Leggings;
import me.vilsol.betanpc.items.spawnmenu.Weapon;
import me.vilsol.betanpc.items.spawnmenu.rarity.Common;
import me.vilsol.betanpc.items.spawnmenu.rarity.Rare;
import me.vilsol.betanpc.items.spawnmenu.rarity.Uncommon;
import me.vilsol.betanpc.items.spawnmenu.rarity.Unique;
import me.vilsol.betanpc.items.spawnmenu.weapons.Axe;
import me.vilsol.betanpc.items.spawnmenu.weapons.Bow;
import me.vilsol.betanpc.items.spawnmenu.weapons.Polearm;
import me.vilsol.betanpc.items.spawnmenu.weapons.Return;
import me.vilsol.betanpc.items.spawnmenu.weapons.Staff;
import me.vilsol.betanpc.items.spawnmenu.weapons.Sword;
import me.vilsol.betanpc.items.teleportmenu.TeleportToCrestguard;
import me.vilsol.betanpc.items.teleportmenu.TeleportToCyrennica;
import me.vilsol.betanpc.items.teleportmenu.TeleportToDarkOakTavern;
import me.vilsol.betanpc.items.teleportmenu.TeleportToDeadPeaks;
import me.vilsol.betanpc.items.teleportmenu.TeleportToGloomyHallows;
import me.vilsol.betanpc.items.teleportmenu.TeleportToHarrisonsFields;
import me.vilsol.betanpc.items.teleportmenu.TeleportToTripoli;
import me.vilsol.betanpc.items.teleportmenu.TeleportToTrollsbane;
import me.vilsol.betanpc.items.tiermenu.TierFive;
import me.vilsol.betanpc.items.tiermenu.TierFour;
import me.vilsol.betanpc.items.tiermenu.TierOne;
import me.vilsol.betanpc.items.tiermenu.TierThree;
import me.vilsol.betanpc.items.tiermenu.TierTwo;
import me.vilsol.betanpc.items.utils.BackToMainMenu;
import me.vilsol.betanpc.menus.ArrowMenu;
import me.vilsol.betanpc.menus.ItemSpawnCommandMenu;
import me.vilsol.betanpc.menus.ItemSpawnMenu;
import me.vilsol.betanpc.menus.MainMenu;
import me.vilsol.betanpc.menus.MiscItemMenu;
import me.vilsol.betanpc.menus.PotionMenu;
import me.vilsol.betanpc.menus.ProfessionMenu;
import me.vilsol.betanpc.menus.ScrollMenu;
import me.vilsol.betanpc.menus.TeleportMenu;
import me.vilsol.betanpc.menus.TierCommandMenu;
import me.vilsol.betanpc.menus.TierMenu;
import me.vilsol.menuengine.engine.MenuModel;
import minecade.dungeonrealms.Main;
import minecade.dungeonrealms.Utils;
import minecade.dungeonrealms.holograms.Hologram;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class BetaNPC implements Listener {
	
	public void onEnable(){
		// Register Items
		
		new ChangeLevel().registerItem();
		new SpawnGems().registerItem();
		new SpawnProfessionItem().registerItem();
		new SpawnFishingRod().registerItem();
		new SpawnPickaxe().registerItem();
		new BackToMainMenu().registerItem();
		new ExitMenu().registerItem();
		new SpawnMiscItems().registerItem();
		new SpawnOrbsOfAlteration().registerItem();
		new SpawnFood().registerItem();
		new TeleportToCrestguard().registerItem();
		new TeleportToCyrennica().registerItem();
		new TeleportToDarkOakTavern().registerItem();
		new TeleportToDeadPeaks().registerItem();
		new TeleportToGloomyHallows().registerItem();
		new TeleportToHarrisonsFields().registerItem();
		new TeleportToTripoli().registerItem();
		new TeleportToTrollsbane().registerItem();
		new SpawnTeleportBooks().registerItem();
		new SpawnArrows().registerItem();
		new SpawnTierOneArrow().registerItem();
		new SpawnTierTwoArrow().registerItem();
		new SpawnTierThreeArrow().registerItem();
		new SpawnTierFourArrow().registerItem();
		new SpawnTierFiveArrow().registerItem();
		new SpawnTierOneScrolls().registerItem();
		new SpawnTierTwoScrolls().registerItem();
		new SpawnTierThreeScrolls().registerItem();
		new SpawnTierFourScrolls().registerItem();
		new SpawnTierFiveScrolls().registerItem();
		new SpawnScrolls().registerItem();
		new SpawnPotions().registerItem();
		new SpawnTierOnePotion().registerItem();
		new SpawnTierTwoPotion().registerItem();
		new SpawnTierThreePotion().registerItem();
		new SpawnTierFourPotion().registerItem();
		new SpawnTierFivePotion().registerItem();
		new TierOne().registerItem();
		new TierTwo().registerItem();
		new TierThree().registerItem();
		new TierFour().registerItem();
		new TierFive().registerItem();
		new SpawnItem().registerItem();
		new Helmet().registerItem();
		new Chestplate().registerItem();
		new Leggings().registerItem();
		new Boots().registerItem();
		new Weapon().registerItem();
		new Axe().registerItem();
		new Bow().registerItem();
		new Polearm().registerItem();
		new Return().registerItem();
		new Staff().registerItem();
		new Sword().registerItem();
		new Common().registerItem();
		new Uncommon().registerItem();
		new Rare().registerItem();
		new Unique().registerItem();
		
		// Register Menus
		
		new MainMenu();
		new ProfessionMenu();
		new MiscItemMenu();
		new TeleportMenu();
		new ArrowMenu();
		new ScrollMenu();
		new PotionMenu();
		new TierMenu();
		new TierCommandMenu();
		new ItemSpawnMenu();
		new ItemSpawnCommandMenu();

        Bukkit.getServer().getPluginManager().registerEvents(this, Main.plugin);

        if (Utils.getShard().equals("US-99") || Utils.isBeta()) {
            new Hologram(new Location(Bukkit.getServer().getWorlds().get(0), -378, 85, 368), Arrays.asList(
                    ChatColor.GREEN.toString() + ChatColor.UNDERLINE.toString() + ChatColor.BOLD + "Welcome to the "
                            + ChatColor.AQUA.toString() + ChatColor.UNDERLINE.toString() + ChatColor.BOLD + "patch 1.9"
                            + ChatColor.GREEN.toString() + ChatColor.UNDERLINE.toString() + ChatColor.BOLD
                            + " Public Test Shard!", " ", ChatColor.BOLD + "One of the main changes we have made is ",
                    ChatColor.BOLD + "that item tier restrictions have changed to ", ChatColor.BOLD
                            + "10 levels, down from 20. This means you can wear ", ChatColor.BOLD
                            + "T5 at level 40.  Please test this and everything else!")).show();
            new Hologram(new Location(Bukkit.getServer().getWorlds().get(0), -388, 85, 368), Arrays.asList(
                    ChatColor.LIGHT_PURPLE.toString() + ChatColor.UNDERLINE + "New HP/s Food", " ",
                    ChatColor.BOLD + "Type " + ChatColor.AQUA + ChatColor.BOLD + "/drfood" + ChatColor.WHITE
                            + ChatColor.BOLD.toString() + " to sample the new HP/s food ", ChatColor.BOLD
                            + "we are adding in patch 1.9! Also try " + ChatColor.AQUA + ChatColor.BOLD + "/stats"
                            + ChatColor.WHITE + ChatColor.BOLD + " to view ", ChatColor.BOLD
                            + "the new stat bonuses from leveling.")).show();
            new Hologram(new Location(Bukkit.getServer().getWorlds().get(0), -368, 85, 368), Arrays.asList(
                    ChatColor.LIGHT_PURPLE.toString() + ChatColor.UNDERLINE + "Item Value Rebalancing", " ",
                    ChatColor.BOLD + "Item values on gear have been rebalanced! They are now ", ChatColor.BOLD
                            + "similar to the values on items during Closed Beta. Go kill ", ChatColor.BOLD
                            + "some mobs to see for yourself! Your old items now have a ", ChatColor.GOLD.toString()
                            + ChatColor.BOLD + "\"LEGACY\"" + ChatColor.WHITE + ChatColor.BOLD + " tag on them."))
                    .show();
            new Hologram(new Location(Bukkit.getServer().getWorlds().get(0), -388, 85, 359), Arrays.asList(
                    ChatColor.LIGHT_PURPLE.toString() + ChatColor.UNDERLINE + "Beta Vendor and Data Transfer", " ",
                    ChatColor.BOLD + "In the interest of testing, we have mirrored all of ", ChatColor.BOLD
                            + "the player data from the live shards. This means ", ChatColor.BOLD
                            + "you have the same items as you do on the live ", ChatColor.BOLD
                            + "shards, but everything you do on this shard is separate! ", ChatColor.BOLD
                            + "The beta vendor is also disabled but may be ", ChatColor.BOLD + "added later.")).show();
            new Hologram(new Location(Bukkit.getServer().getWorlds().get(0), -368, 85, 359), Arrays.asList(
                    ChatColor.LIGHT_PURPLE.toString() + ChatColor.UNDERLINE + "THINGS WIP", " ", ChatColor.BOLD
                            + "We have many things that we are working on currently. ", ChatColor.BOLD
                            + "ATM they are not yet available on the beta shard, ", ChatColor.BOLD
                            + "but as we finish them, we will release them onto ", ChatColor.BOLD
                            + "US-100 for player testing! (Mob pathing is coming soon™)")).show();
        }

    }
	
	@EventHandler
	public void onPlayerClickNPC(PlayerInteractEntityEvent e) {
		if (!(e.getRightClicked() instanceof Player)) return;
		Player trader = (Player) e.getRightClicked();
		if (!(trader.hasMetadata("NPC"))) return;
		if (!(ChatColor.stripColor(trader.getName()).equalsIgnoreCase("Beta Vendor"))) return;
		if(Utils.isBeta() || Bukkit.getMotd().contains("US-99")){
			e.setCancelled(true);
			MenuModel.menus.get(MainMenu.class).getMenu().showToPlayer(e.getPlayer());
		}else{
			e.getPlayer().sendMessage(me.vilsol.betanpc.utils.Utils.NPC + "HOW DID YOU FIND ME?");
		}
	}
	
}
