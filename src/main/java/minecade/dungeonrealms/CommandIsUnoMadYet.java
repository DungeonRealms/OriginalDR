package minecade.dungeonrealms;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandIsUnoMadYet implements CommandExecutor {

	public static List<String> messages = Arrays.asList(
			"You better run...",
			"Nah he's cool",
			"Shit is about to go down",
			"He ain't even there",
			"Uno who?",
			"Hes fine...",
			"About to break down",
			"He's ravin...!"
			);
			
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		sender.sendMessage(messages.get(new Random().nextInt(messages.size())));
		return true;
	}
	
	
}
