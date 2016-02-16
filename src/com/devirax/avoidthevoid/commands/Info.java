package com.devirax.avoidthevoid.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.devirax.avoidthevoid.island.Island;
import com.devirax.avoidthevoid.island.IslandManager;
import com.devirax.avoidthevoid.utils.Messenger;
import com.devirax.avoidthevoid.utils.Messenger.State;

public class Info implements SubCommand {

	@Override
	public boolean onCommand(CommandSender sender, String[] args) {
		
		if(!(sender instanceof Player))
			return true;
		
		Player p = (Player) sender;
		
		Messenger.message(sender, "Islands loaded: "+IslandManager.getIslands().size(), State.INFO);
		Messenger.message(sender, "Next island would be: "+IslandManager.generateId(), State.INFO);
		Island is = IslandManager.getAbsoluteIslandForLocation(p.getLocation());
		if(is == null)
			Messenger.message(sender, "You're not standing on an island", State.INFO);
		else
			Messenger.message(sender, "You're standing on island "+is.getId(), State.INFO);
		if(IslandManager.hasPermission(p.getUniqueId(), is))
			Messenger.message(sender, "You have permission to edit this island", State.INFO);
		else 
			Messenger.message(sender, "You don't have permission to edit this island", State.INFO);
		if(args.length == 1) {
			Integer id = null;
			try {
				id = Integer.parseInt(args[0]);
			} catch(Exception e) {};
			if(id == null)
				return true;
			Island island = IslandManager.getIslandById(id);
			if(island == null)
				Messenger.message(sender, "Island "+id+" is NULL", State.INFO);
			else
				Messenger.message(sender, "Island "+id+" is NOT NULL", State.INFO);
		}
		return true;
	}

}
