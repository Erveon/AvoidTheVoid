package com.devirax.avoidthevoid.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.devirax.avoidthevoid.island.Island;
import com.devirax.avoidthevoid.island.IslandManager;
import com.devirax.avoidthevoid.utils.Messenger;
import com.devirax.avoidthevoid.utils.Messenger.State;

public class Calc implements SubCommand {

	@Override
	public boolean onCommand(CommandSender sender, String[] args) {
		
		if(!(sender instanceof Player))
			return true;
		
		/*IslandManager.loadTotalLevel((Player) sender); 
		Messenger.message(sender, "Recalculated level", State.GOOD);*/
		
		Player p = (Player) sender;
		
		Island is = IslandManager.getAbsoluteIslandForLocation(p.getLocation());
		if(is == null) {
			Messenger.message(p, "You must be on an island to do this", State.BAD);
			return true;
		}
		
		Messenger.message(p, "§b" + is.getName() + " [§6"+is.getOwner().getUsername()+"§b] §7- "+ is.getExperience(), State.INFO);
		
		return true;
	}

}
