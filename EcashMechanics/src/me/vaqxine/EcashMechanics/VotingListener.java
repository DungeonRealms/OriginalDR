package me.vaqxine.EcashMechanics;


public class VotingListener implements VoteListener {
	EcashMechanics plugin = null;

	public VotingListener(EcashMechanics instance) { 
		plugin = instance;
	}
	
    public void voteMade(Vote vote) {
    	System.out.println("[VOTE] " + vote);
        String username = vote.getUsername();
        plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), "addec " + username + " 1");
    }
	
}
