package me.vaqxine.DonationMechanics;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.vexsoftware.votifier.model.VotifierEvent;
import com.vexsoftware.votifier.model.Vote;
    
public class CustomEventListener implements Listener {
	DonationMechanics plugin = null;

	public CustomEventListener(DonationMechanics instance) { 
		plugin = instance;
	}
	
    @EventHandler(priority=EventPriority.NORMAL)
    public void onVotifierEvent(VotifierEvent event) {
        Vote vote = event.getVote();
    
        System.out.println("[VOTE] " + vote);
        String username = vote.getUsername();
        plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), "addec " + username + " 3");
    }
}
