package minecade.dungeonrealms.ChatMechanics.commands;

import minecade.dungeonrealms.Main;
import minecade.dungeonrealms.ChatMechanics.ChatMechanics;
import minecade.dungeonrealms.CommunityMechanics.CommunityMechanics;
import minecade.dungeonrealms.EcashMechanics.EcashMechanics;
import minecade.dungeonrealms.PermissionMechanics.PermissionMechanics;
import minecade.dungeonrealms.TutorialMechanics.TutorialMechanics;
import minecade.dungeonrealms.jsonlib.JSONMessage;
import minecade.dungeonrealms.managers.PlayerManager;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CommandGL implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player p = (Player) sender;

		if(args.length <= 0) {
			p.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Incorrect Syntax. You must supply a message! " + ChatColor.RED + "/gl <MESSAGE>");
			return true;
		}

		if(TutorialMechanics.onTutorialIsland(p) && !(p.isOp())) {
			p.sendMessage(ChatColor.RED + "You " + ChatColor.UNDERLINE + "cannot" + ChatColor.RED + " chat while on tutorial island.");
			p.sendMessage(ChatColor.GRAY + "Either finish the tutorial or type /skip to enable chat.");
			return true;
		}

		if(ChatMechanics.mute_list.containsKey(p.getName())) {
			long time_left = ChatMechanics.mute_list.get(p.getName());
			p.sendMessage(ChatColor.RED + "You are currently " + ChatColor.BOLD + "GLOBALLY MUTED" + ChatColor.RED + ". You will be unmuted in " + time_left + " minute(s).");
			return true;
		}

		String msg = "";

		for(String s : args) {
			msg += s + " ";
		}

		String rank = PermissionMechanics.getRank(p.getName());

		if(PlayerManager.getPlayerModel(p).getGlobalChatDelay() != 0) {
			long old_time = PlayerManager.getPlayerModel(p).getGlobalChatDelay();
			long cur_time = System.currentTimeMillis();

			int personal_delay = ChatMechanics.GChat_Delay;
			ItemStack global_amp = EcashMechanics.tickGlobalAmplifier(p);
			if(global_amp != null) {
				// They have one!
				personal_delay *= 0.50D;

				// EcashMechanics.setMessagesLeftOnGlobalAmplifier(global_amp,
				// -1, true);
				// It will subtract from the item in getGlobalAmplifier.
			}

			if((cur_time - old_time) < (personal_delay * 1000) && !(p.isOp()) && !(rank.equalsIgnoreCase("GM")) && !(rank.equalsIgnoreCase("PMOD") && !(rank.equalsIgnoreCase("WD")))) {
				int s_delay_left = personal_delay - (int) ((cur_time - old_time) / 1000);
				p.sendMessage(ChatColor.RED + "You can send another GLOBAL MESSAGE in " + s_delay_left + ChatColor.BOLD + "s");
				return true;
			}
		}

		PlayerManager.getPlayerModel(p).setGlobalChatDelay(System.currentTimeMillis());

		boolean trade = false;
		boolean guild = false;
		if(ChatMechanics.hasTradeKeyword(msg)) {
			trade = true;
		}
		if (ChatMechanics.hasGuildKeyword(msg)) {
			guild = true;
		}

		String prefix = ChatMechanics.getPlayerPrefix(p);
		String message = ChatMechanics.fixCapsLock(msg);
	      if (PlayerManager.getPlayerModel(p).getToggleList() != null && PlayerManager.getPlayerModel(p).getToggleList().contains("global")) {
	            p.sendMessage(ChatColor.RED + "You currently have global messaging " + ChatColor.BOLD + "DISABLED." + ChatColor.RED
	                    + " Type '/toggleglobal' to re-enable.");
	            return true;
	        }
		JSONMessage filter = null;
		JSONMessage normal = null;
		String aprefix = p.getName() + ": " + ChatColor.WHITE;
		if(message.contains("@i@") && p.getItemInHand().getType() != Material.AIR) {
			String[] split = message.split("@i@");
			String after = "";
			String before = "";
			if(split.length > 0) before = split[0];
			if(split.length > 1) after = split[1];

			normal = new JSONMessage(prefix + ChatColor.WHITE + aprefix, ChatColor.WHITE);
			normal.addText(before + " ");
			normal.addItem(p.getItemInHand(), ChatColor.BOLD + "SHOW", ChatColor.UNDERLINE);
			normal.addText(after);

			filter = new JSONMessage(prefix + ChatColor.WHITE + aprefix, ChatColor.WHITE);
			filter.addText(ChatMechanics.censorMessage(before) + " ");
			filter.addItem(p.getItemInHand(), ChatColor.BOLD + "SHOW", ChatColor.UNDERLINE);
			filter.addText(ChatMechanics.censorMessage(after));
		}

		for(Player pl : Bukkit.getServer().getOnlinePlayers()) {
			if(CommunityMechanics.isPlayerOnIgnoreList(p, pl.getName()) || CommunityMechanics.isPlayerOnIgnoreList(pl, p.getName())) {
				continue; // Either sender has the sendie ignored or vise versa,
							// no need for them to be able to see each other's
							// messages.
			}
			if(!trade && PlayerManager.getPlayerModel(pl).getToggleList() != null && PlayerManager.getPlayerModel(pl).getToggleList().contains("global")) {
				continue; // They have global off, and only want to hear from
							// their buds.
			}
			if(trade && PlayerManager.getPlayerModel(pl).getToggleList() != null && PlayerManager.getPlayerModel(pl).getToggleList().contains("tchat")) {
				continue; // They have global off, and only want to hear from
							// their buds.
			}
			if(TutorialMechanics.onTutorialIsland(pl)) {
				continue; // Don't send global chat to players on tutorial
							// island.
			}

			if(normal != null){
				JSONMessage toSend = normal;
				if(ChatMechanics.hasAdultFilter(pl.getName())) {
					toSend = filter;
				}
				ChatColor p_color = ChatMechanics.getPlayerColor(p, pl);

				if(trade){
					toSend.setText(ChatColor.GREEN + "<" + ChatColor.BOLD + "T" + ChatColor.GREEN + ">" + " " + prefix + p_color + aprefix);
				}else if(guild) {
					toSend.setText(ChatColor.RED + "<" + ChatColor.BOLD + "GR" + ChatColor.RED + ">" + " " + prefix + p_color + aprefix);
				} else {
					toSend.setText(ChatColor.AQUA + "<" + ChatColor.BOLD + "G" + ChatColor.AQUA + ">" + " " + prefix + p_color + aprefix);
				}

				toSend.sendToPlayer(pl);
			}else{
				ChatColor p_color = ChatMechanics.getPlayerColor(p, p);

				if(trade){
					pl.sendMessage(ChatColor.GREEN + "<" + ChatColor.BOLD + "T" + ChatColor.GREEN + ">" + " " + prefix + p_color + aprefix + message);
				}else if (guild) {
					pl.sendMessage(ChatColor.RED + "<" + ChatColor.BOLD + "GR" + ChatColor.RED + ">" + " " + prefix + p_color + aprefix + message);
				} else {
					pl.sendMessage(ChatColor.AQUA + "<" + ChatColor.BOLD + "G" + ChatColor.AQUA + ">" + " " + prefix + p_color + aprefix + message);
				}
			}
		}

		Main.log.info(ChatColor.stripColor("" + "<" + "G" + ">" + " " + prefix + p.getName() + ": " + msg));

		return true;
	}

}
