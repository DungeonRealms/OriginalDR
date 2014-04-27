package minecade.dungeonrealms.ChatMechanics;

public class LoginProcessThread extends Thread {
	public void run() {
		while(true) {
			try {
				Thread.sleep(250);
			} catch(InterruptedException e) {}
			if(ChatMechanics.async_mute_update.size() <= 0) {
				continue;
			}
			for(String p_name : ChatMechanics.async_mute_update) {
				ChatMechanics.getMuteStateSQL(p_name);
			}
			ChatMechanics.async_mute_update.clear();
		}
	}
}
