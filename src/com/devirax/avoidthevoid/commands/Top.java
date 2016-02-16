package com.devirax.avoidthevoid.commands;

import java.util.HashMap;

import org.bukkit.command.CommandSender;

import com.devirax.avoidthevoid.island.IslandManager;

public class Top implements SubCommand {

	@Override
	public boolean onCommand(CommandSender sender, String[] args) {
		HashMap<Integer, String> top = IslandManager.topIslands;
		if(top.size() > 10) {
			sender.sendMessage("§cSomething went wrong when displaying the top islands (Over10)");
			return true;
		}
		for(int i = 1; i <= 10; i++) {
			if(top.containsKey(i))
				sender.sendMessage(top.get(i));
			else
				sender.sendMessage("§cFailed to get number " + i);
		}
		sender.sendMessage("§aThe top islands will be refreshed in " + timeConversion(IslandManager.topIslandCounter));
		return true;
		/*if(args.length == 1) {
			if(args[0].equalsIgnoreCase("players")) {
				ArrayList<String> topPlayers = IslandManager.getTopPlayers(10);
				Messenger.message(sender, "Top Players", State.GOOD);
				for(int i = 0; i < topPlayers.size(); i++) {
					Messenger.message(sender, (i+1)+". "+topPlayers.get(i)+" - Level "+IslandManager.getTotalLevel(topPlayers.get(i)), State.GOOD);
				}
			} else if(args[0].equalsIgnoreCase("islands")) {
				ArrayList<Island> topIslands = IslandManager.getTopIslands(10);
				Messenger.message(sender, "Top Islands", State.GOOD);
				for(int i = 0; i < topIslands.size(); i++) {
					Island is = topIslands.get(i);
					Double experienceDouble = is.getExperience();
					Integer experienceInt = (int) Math.ceil(is.getExperience());
					String experience;
					if ((experienceDouble == Math.floor(experienceDouble)) && !Double.isInfinite(experienceDouble)) {
					    experience = experienceInt.toString();
					} else {
						experience = String.format("%.1f", experienceDouble);
					}
					Messenger.message(sender, (i+1)+". "+is.getOwner().getUsername()+" ("+is.getName()+") - Level "+is.getLevel()+" ("+experience+"exp)", State.GOOD);
				}
			}
		}
		return true;*/
	}
	
	private String timeConversion(int totalSeconds) {

	    final int MINUTES_IN_AN_HOUR = 60;
	    final int SECONDS_IN_A_MINUTE = 60;

	    String seconds = totalSeconds % SECONDS_IN_A_MINUTE + "";
	    int totalMinutes = totalSeconds / SECONDS_IN_A_MINUTE;
	    int minutes = totalMinutes % MINUTES_IN_AN_HOUR;
	    
	    if(Integer.parseInt(seconds) < 10) {
	    	seconds = "0" + seconds;
	    }

	    return minutes + ":" + seconds;
	}

}
