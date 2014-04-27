package minecade.dungeonrealms.GuildMechanics.commands;

import minecade.dungeonrealms.GuildMechanics.GuildMechanics;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandGuild implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		final Player p = (Player) sender;
		if(!(p.isOp())) { return true; }
		p.getInventory().addItem(GuildMechanics.guild_dye);
		p.getInventory().addItem(GuildMechanics.guild_emblem);
		p.getItemInHand().setDurability((short) 20);
		return true;
	}
	
}