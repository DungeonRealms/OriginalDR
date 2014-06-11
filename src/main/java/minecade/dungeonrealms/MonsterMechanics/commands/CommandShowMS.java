package minecade.dungeonrealms.MonsterMechanics.commands;

import minecade.dungeonrealms.MonsterMechanics.MonsterMechanics;
import minecade.dungeonrealms.jsonlib.JSONMessage;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandShowMS implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Player p = (Player) sender;

        if (!(p.isOp())) {
            return true;
        }

        if (args.length != 1) {
            p.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Incorrect Syntax. " + ChatColor.RED + "/showms <radius>");
            return true;
        }

        int radius = Integer.parseInt(args[0]);
        Location loc = p.getLocation();
        World w = loc.getWorld();
        int i, j, k;
        int x = (int) loc.getX();
        int y = (int) loc.getY();
        int z = (int) loc.getZ();
        int count = 0;
        for (i = -radius; i <= radius; i++) {
            for (j = -radius; j <= radius; j++) {
                for (k = -radius; k <= radius; k++) {
                    loc = w.getBlockAt(x + i, y + j, z + k).getLocation();
                    if (MonsterMechanics.mob_spawns.containsKey(loc)) {
                        //if (loc.getBlock().getType() == Material.AIR) {
                            count++;
                            loc.getBlock().setType(Material.MOB_SPAWNER);
                            JSONMessage m = new JSONMessage("", ChatColor.GRAY);
                            m.addRunCommand(ChatColor.GRAY + "Spawner: X:" + loc.getBlockX() + " Y:" + loc.getBlockY() + " Z:" + loc.getBlockZ(), ChatColor.GRAY, "/drtppos " + (0.5D + loc.getBlockX()) + " " + (0.5D + loc.getBlockY()) + " " + (0.5D + loc.getBlockZ()) );
                            m.sendToPlayer(p);
                        //}
                    }
                }
            }
        }

        p.sendMessage(ChatColor.YELLOW + "Displaying " + count + " mob spawners in a " + radius + " block radius...");
        p.sendMessage(ChatColor.YELLOW + "Local spawning will be disabled while they are visible.");
        p.sendMessage(ChatColor.GRAY + "Break them to unregister the spawn point.");

        return true;
    }

}