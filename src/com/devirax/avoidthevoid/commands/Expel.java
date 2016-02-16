package com.devirax.avoidthevoid.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.devirax.avoidthevoid.island.Island;
import com.devirax.avoidthevoid.island.IslandManager;
import com.devirax.avoidthevoid.utils.Messenger;
import com.devirax.avoidthevoid.utils.Messenger.State;

public class Expel implements SubCommand {

	@Override
	public boolean onCommand(CommandSender sender, String[] args) {
		
		if(!(sender instanceof Player)) {
			sender.sendMessage("You may not execute this command from the console.");
			return true;
		}
		
		Player p = (Player) sender;
		
		if(args.length == 0) {
			Island is = IslandManager.getAbsoluteIslandForLocation(p.getLocation());
			if(is == null) {
				Messenger.message(p, "You must be on one of your islands to do this", State.BAD);
				return true;
			}
			is.expel(p);
		} else if(args.length == 1) {
			String toExpel = args[0];
			Island is = IslandManager.getAbsoluteIslandForLocation(p.getLocation());
			if(is == null) {
				Messenger.message(p, "You must be on one of your islands to do this", State.BAD);
				return true;
			}
			is.expel(p, toExpel);
		}
		
		return true;
	}

}
