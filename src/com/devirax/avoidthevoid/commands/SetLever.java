package com.devirax.avoidthevoid.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.devirax.avoidthevoid.listeners.Lever;
import com.devirax.avoidthevoid.listeners.Lever.Destination;
import com.devirax.avoidthevoid.utils.Messenger;
import com.devirax.avoidthevoid.utils.Messenger.State;

public class SetLever implements SubCommand {

	@Override
	public boolean onCommand(CommandSender sender, String[] args) {
		
		if(!(sender instanceof Player)) {
			sender.sendMessage("You may not execute this command from the console.");
			return true;
		}
		
		Player p = (Player) sender;
		
		if(!p.isOp())
			return true;
		
		if(args.length == 0) {
			invalidDestination(p);
			return true;
		} else if(args.length == 1) {
			for(Destination dest : Destination.values()) {
				if(args[0].equalsIgnoreCase(dest.toString())) {
					Lever.setSettingLever(p, dest);
					Messenger.message(p, "Setting lever for destination: "+dest.toString(), State.GOOD);
					return true;
				}
			}
			invalidDestination(p);
			return true;
		}
		return true;
	}
	
	public void invalidDestination(Player p) {
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < Destination.values().length; i++) {
			sb.append(Destination.values()[i].toString());
			if(i != (Destination.values().length - 1)) {
				sb.append(", ");
			}
		}
		Messenger.message(p, "Invalid destination. Destinations: "+sb.toString(), State.BAD);
	}

}
