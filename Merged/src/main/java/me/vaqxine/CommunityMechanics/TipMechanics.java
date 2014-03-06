package me.vaqxine.CommunityMechanics;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public class TipMechanics implements Listener {
	public static CommunityMechanics plugin; public TipMechanics(CommunityMechanics instance) { 
		plugin = instance;
	}

	public static List<String> tips = new ArrayList<String>();
	// Contains all tips that haven't been used.

	public static List<String> used_tips = new ArrayList<String>();
	// Tips given in current game session, prevent a lot of repeats.

	public void displayRandomTip(){
		String tip = getRandomTip();
		if(tip == null){
			return;
		}
		
		for(Player pl : Bukkit.getServer().getOnlinePlayers()){
			if(CommunityMechanics.toggle_list.containsKey(pl.getName()) && CommunityMechanics.toggle_list.get(pl.getName()).contains("tips")){
				continue; // Skip them, they don't want tips.
			}
			pl.sendMessage(ChatColor.YELLOW.toString() + ChatColor.BOLD.toString() + ">>" + ChatColor.YELLOW + " TIP - " + ChatColor.GRAY + tip);
		}
	}
	
	public String getRandomTip(){
		if(tips.size() == 0 && used_tips.size() == 0){
			// No tips loaded.
			return null;
		}
		
		if(tips.size() == 0){
			// Used all tips, refresh.
			for(String s : used_tips){
				tips.add(s);
			}
			used_tips.clear();
		}

		int ran_index = new Random().nextInt(tips.size());
		String tip = tips.get(ran_index);

		used_tips.add(tip);
		tips.remove(tip);
		return tip;
	}

	public void loadTips(){
		try{ 
			File file = new File("plugins/CommunityMechanics/tips.txt");
			if(!(file.exists())){
				file.createNewFile();
			}
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line = "";
			int count = 0;

			while((line = reader.readLine()) != null){
				try{
					tips.add(line);
					count++;
				} catch(NullPointerException npe){
					npe.printStackTrace();
					continue;
				}
			}
			reader.close();
			plugin.log.info("[TipMechanics] " + count + " Gameplay Tips have been LOADED.");

		} catch(Exception err){
			err.printStackTrace();
		}
	}
}
