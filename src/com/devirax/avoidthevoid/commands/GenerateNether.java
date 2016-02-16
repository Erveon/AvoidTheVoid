package com.devirax.avoidthevoid.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.devirax.avoidthevoid.island.Island;
import com.devirax.avoidthevoid.island.IslandManager;
import com.devirax.avoidthevoid.utils.Messenger;
import com.devirax.avoidthevoid.utils.Messenger.State;

public class GenerateNether implements SubCommand {

	@Override
	public boolean onCommand(CommandSender sender, String[] args) {
		
		if(sender instanceof Player) {
			if(!((Player) sender).isOp())
				return true;
		}
		
		if(args.length == 1) {
			Integer id = Integer.valueOf(args[0]);
			Island is = IslandManager.getIslandById(id);
			if(is == null)
				return true;
			is.generateNether();
			Messenger.message(sender, "The nether has successfully generated for that island", State.GOOD);
			Messenger.messageIsland(is, "The nether has been generated", State.GOOD);
		}
		
		return true;
	}

}
