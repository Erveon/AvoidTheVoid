package com.devirax.avoidthevoid.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.devirax.avoidthevoid.island.IslandManager;
import com.devirax.avoidthevoid.utils.Messenger;
import com.devirax.avoidthevoid.utils.Utils;
import com.devirax.avoidthevoid.utils.Messenger.State;

public class Create implements SubCommand {

	@Override
	public boolean onCommand(CommandSender sender, String[] args) {
		
		if(!(sender instanceof Player)) {
			sender.sendMessage("You may not execute this command from the console.");
			return true;
		}
		
		Player p = (Player) sender;
		
		if((IslandManager.getIslandsFor(p).size() - IslandManager.getMaxedIslandsFor(p).size()) >= Utils.getMaxIslandsFor(p) && !p.hasPermission("atv.unlimited")) {
			Messenger.message(sender, "You have to level your current islands before creating another", State.BAD);
			return true;
		}
		
		Messenger.message(p, "Creating island..", State.INFO);
		IslandManager.createIsland(p);
		
		return true;
	}
	
	

}
