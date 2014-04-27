package minecade.dungeonrealms.EcashMechanics;

import org.bukkit.Bukkit;

import com.vexsoftware.votifier.model.Vote;
import com.vexsoftware.votifier.model.VoteListener;

public class VotingListener implements VoteListener {
	EcashMechanics plugin = null;
	
	public VotingListener(EcashMechanics instance) {
		plugin = instance;
	}
	
	public void voteMade(Vote vote) {
		System.out.println("[VOTE] " + vote);
		String username = vote.getUsername();
		Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "addec " + username + " 1");
	}
	
}
