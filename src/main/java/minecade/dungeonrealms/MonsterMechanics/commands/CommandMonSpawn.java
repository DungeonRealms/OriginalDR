package minecade.dungeonrealms.MonsterMechanics.commands;

import java.util.HashSet;

import minecade.dungeonrealms.MonsterMechanics.MonsterMechanics;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class CommandMonSpawn implements CommandExecutor {
	
	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(sender instanceof BlockCommandSender) {
			BlockCommandSender cb = (BlockCommandSender) sender;
			Location loc = cb.getBlock().getLocation().add(0, 2, 0);
			if(args.length != 6) { return true; }
			
			String mob_type = args[0];
			int tier = Integer.parseInt(args[1]);
			boolean elite = false;
			if(args[2].equalsIgnoreCase("true")) {
				elite = true;
			}
			String meta_data = args[3];
			String custom_name = args[4];
			
			if(meta_data.equalsIgnoreCase("null")) {
				meta_data = "";
			}
			if(custom_name.equalsIgnoreCase("null")) {
				custom_name = "";
			}
			
			EntityType et = EntityType.fromName(mob_type.toUpperCase());
			if(mob_type.equalsIgnoreCase("IRON_GOLEM")) {
				et = EntityType.IRON_GOLEM;
			}
			if(mob_type.equalsIgnoreCase("CAVE_SPIDER")) {
				et = EntityType.CAVE_SPIDER;
			}
			if(mob_type.equalsIgnoreCase("MUSHROOM_COW")) {
				et = EntityType.MUSHROOM_COW;
			}
			if(mob_type.equalsIgnoreCase("PIG_ZOMBIE")) {
				//et = EntityType.PIG_ZOMBIE;
				et = EntityType.ZOMBIE;
			}
			
			if(mob_type.equalsIgnoreCase("imp")) {
				mob_type = EntityType.PIG_ZOMBIE.getName();
				meta_data = "imp";
			}
			
			if(mob_type.equalsIgnoreCase("acolyte")) {
				mob_type = "SKELETON";
				meta_data = "acolyte";
			}
			
			if(mob_type.equalsIgnoreCase("daemon")) {
				mob_type = EntityType.PIG_ZOMBIE.getName();
				meta_data = "daemon";
			}
			
			if(mob_type.equalsIgnoreCase("MagmaCube")) {
				mob_type = EntityType.MAGMA_CUBE.getName();
				meta_data = "";
			}
			
			if(mob_type.equalsIgnoreCase("skeleton2") || mob_type.equalsIgnoreCase("wither") || mob_type.equalsIgnoreCase("skeleton1")) {
				mob_type = "SKELETON";
				meta_data = "wither";
			}
			
			if(mob_type.equalsIgnoreCase("spider1")) {
				mob_type = "SPIDER";
			}
			
			if(mob_type.equalsIgnoreCase("spider2")) {
				mob_type = "CAVE_SPIDER";
			}
			
			if(mob_type.equalsIgnoreCase("troll1")) {
				mob_type = "ZOMBIE";
				meta_data = "troll";
			}
			
			if(mob_type.equalsIgnoreCase("goblin")) {
				mob_type = "ZOMBIE";
				meta_data = "goblin";
			}
			
			if(mob_type.equalsIgnoreCase("bandit")) {
				mob_type = "SKELETON";
				meta_data = "bandit";
			}
			
			if(mob_type.equalsIgnoreCase("monk")) {
				mob_type = "SKELETON";
				meta_data = "monk";
			}
			
			if(mob_type.equalsIgnoreCase("golem")) {
				mob_type = "IRON_GOLEM";
			}
			
			if(mob_type.equalsIgnoreCase("mooshroom")) {
				mob_type = "MUSHROOM_COW";
			}
			
			if(mob_type.equalsIgnoreCase("lizardman")) {
				mob_type = "ZOMBIE";
				meta_data = "lizardman";
			}
			
			if(mob_type.equalsIgnoreCase("naga")) {
				mob_type = "ZOMBIE";
				meta_data = "naga";
			}
			
			if(mob_type.equalsIgnoreCase("tripoli1")) {
				mob_type = "SKELETON";
				meta_data = "tripoli solider";
			}
			
			if(mob_type.equalsIgnoreCase("enderman")) {
				mob_type = EntityType.ENDERMAN.getName();
				meta_data = "enderman";
			}
			
			if(mob_type.equalsIgnoreCase("witch")) {
				mob_type = EntityType.WITCH.getName();
				meta_data = "witch";
			}
			
			if(mob_type.equalsIgnoreCase("silverfish")) {
				mob_type = EntityType.SILVERFISH.getName();
				meta_data = "silverfish";
			}
			
			if(mob_type.equalsIgnoreCase("blaze")) {
				mob_type = EntityType.BLAZE.getName();
				meta_data = "blaze";
			}
			
			if(et == null) {
				et = EntityType.ZOMBIE;
				// Default to zombie if something went wrong.
			}
			int level_tier = 1;
			try{
			    level_tier = Integer.parseInt(args[5]);
			}catch(Exception e){
			    level_tier = 1;
			}
			MonsterMechanics.spawnTierMob(loc, et, tier, -1, loc, elite, meta_data, custom_name, true, level_tier);
		}
		
		if(sender instanceof Player) {
			Player p = (Player) sender;
			
			if(!(p.isOp())) { return true; }
			
			if(args.length != 6) {
				p.sendMessage("/monspawn <mob_type> <tier> <elite> (meta_data) (custom_name) (level_tier)");
				p.sendMessage("EX: " + ChatColor.GRAY + "/monspawn SKELETON 2 false null null");
				p.sendMessage(ChatColor.YELLOW + "Mob Types: " + ChatColor.WHITE + "monk | enderman | witch | silverfish | blaze | Magmacube | Daemon | Imp | Acolyte | Lizardman | Naga | Tripoli1 | Ocelot | Skeleton | Skeleton2 | Zombie | Golem | Goblin | Bandit | Spider1 | Spider2 | Troll1 | Cow | Bat | Ocelot | Wolf | Pig | Chicken | Sheep | Mooshroom");
				
				p.sendMessage(ChatColor.RED + "<elite> must be either 'true' or 'false'");
				p.sendMessage(ChatColor.GRAY + "(meta_data) and (custom_name) are optional, do not use meta_data unless you talk to Vaquxine, custom_name cannot contain spaces. Use 'null' if you do not want to set a value for these two options.");
				p.sendMessage(ChatColor.RED + "(level_tier) must be a number between 1 - 4 and will determine the level tier of the mob. EX: (level_tier 1) with a mob tier of 3 will make the mobs level 40 - 44");
				return true;
			}
			
			// /monspawn mob_type tier elite meta_data name
			
			Location l = p.getTargetBlock((HashSet<Byte>)null, 128).getLocation().add(0, 1, 0);
			/*public static boolean spawnTierMob(Location l, EntityType et, int tier, int mob_num,
				Location mob_spawner_loc, boolean elite, String meta_data, String custom_name) {*/
			
			String mob_type = args[0];
			int tier = Integer.parseInt(args[1]);
			int level_tier = Integer.parseInt(args[5]);
			boolean elite = false;
			if(args[2].equalsIgnoreCase("true")) {
				elite = true;
			}
			String meta_data = args[3];
			String custom_name = args[4];
			
			if(meta_data.equalsIgnoreCase("null")) {
				meta_data = "";
			}
			if(custom_name.equalsIgnoreCase("null")) {
				custom_name = "";
			}
			
			EntityType et = EntityType.fromName(mob_type.toUpperCase());
			
			if(mob_type.equalsIgnoreCase("imp")) {
				mob_type = EntityType.PIG_ZOMBIE.getName();
				meta_data = "imp";
			}
			
			if(mob_type.equalsIgnoreCase("acolyte")) {
				mob_type = "SKELETON";
				meta_data = "acolyte";
			}
			
			if(mob_type.equalsIgnoreCase("daemon")) {
				mob_type = EntityType.PIG_ZOMBIE.getName();
				meta_data = "daemon";
			}
			
			if(mob_type.equalsIgnoreCase("MagmaCube")) {
				mob_type = EntityType.MAGMA_CUBE.getName();
				meta_data = "";
			}
			
			if(mob_type.equalsIgnoreCase("skeleton2") || mob_type.equalsIgnoreCase("wither") || mob_type.equalsIgnoreCase("skeleton1")) {
				mob_type = "SKELETON";
				meta_data = "wither";
			}
			
			if(mob_type.equalsIgnoreCase("spider1")) {
				mob_type = "SPIDER";
			}
			
			if(mob_type.equalsIgnoreCase("spider2")) {
				mob_type = "CAVE_SPIDER";
			}
			
			if(mob_type.equalsIgnoreCase("troll1")) {
				mob_type = "ZOMBIE";
				meta_data = "troll";
			}
			
			if(mob_type.equalsIgnoreCase("goblin")) {
				mob_type = "ZOMBIE";
				meta_data = "goblin";
			}
			
			if(mob_type.equalsIgnoreCase("bandit")) {
				mob_type = "SKELETON";
				meta_data = "bandit";
			}
			
			if(mob_type.equalsIgnoreCase("monk")) {
				mob_type = "SKELETON";
				meta_data = "monk";
			}
			
			if(mob_type.equalsIgnoreCase("golem")) {
				mob_type = "IRON_GOLEM";
			}
			
			if(mob_type.equalsIgnoreCase("mooshroom")) {
				mob_type = "MUSHROOM_COW";
			}
			
			if(mob_type.equalsIgnoreCase("lizardman")) {
				mob_type = "ZOMBIE";
				meta_data = "lizardman";
			}
			
			if(mob_type.equalsIgnoreCase("naga")) {
				mob_type = "ZOMBIE";
				meta_data = "naga";
			}
			
			if(mob_type.equalsIgnoreCase("tripoli1")) {
				mob_type = "SKELETON";
				meta_data = "tripoli solider";
			}
			
			if(mob_type.equalsIgnoreCase("IRON_GOLEM")) {
				et = EntityType.IRON_GOLEM;
			}
			if(mob_type.equalsIgnoreCase("CAVE_SPIDER")) {
				et = EntityType.CAVE_SPIDER;
			}
			if(mob_type.equalsIgnoreCase("MUSHROOM_COW")) {
				et = EntityType.MUSHROOM_COW;
			}
			if(mob_type.equalsIgnoreCase("PIG_ZOMBIE")) {
				et = EntityType.PIG_ZOMBIE;
			}
			
			if(mob_type.equalsIgnoreCase("enderman")) {
				mob_type = EntityType.ENDERMAN.getName();
				meta_data = "enderman";
			}
			
			if(mob_type.equalsIgnoreCase("witch")) {
				mob_type = EntityType.WITCH.getName();
				meta_data = "witch";
			}
			
			if(mob_type.equalsIgnoreCase("silverfish")) {
				mob_type = EntityType.SILVERFISH.getName();
				meta_data = "silverfish";
			}
			
			if(mob_type.equalsIgnoreCase("blaze")) {
				mob_type = EntityType.BLAZE.getName();
				meta_data = "blaze";
			}
			
			if(et == null) {
				et = EntityType.ZOMBIE;
				// Default to zombie if something went wrong.
			}
			
			MonsterMechanics.spawnTierMob(l, et, tier, -1, l, elite, meta_data, custom_name, true, level_tier);
		}
		return true;
	}
	
}