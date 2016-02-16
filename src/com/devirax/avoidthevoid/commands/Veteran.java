package com.devirax.avoidthevoid.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.devirax.avoidthevoid.utils.Messenger;
import com.devirax.avoidthevoid.utils.Messenger.State;

public class Veteran implements SubCommand {

	@Override
	public boolean onCommand(CommandSender sender, String[] args) {
		
		if(sender instanceof Player) {
			if(!((Player) sender).hasPermission("atv.setveteran"))
				return true;
		}
		
		if(args.length == 1) {
			Player other = Bukkit.getPlayer(args[0]);
			if(other == null) {
				Messenger.message(sender, "The user §b"+args[0]+"§r is not online or does not exist", State.BAD);
				return true;
			}
			Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "pex user "+args[0]+" group add veteran");
			Messenger.message(sender, "Veteran status has been given to §b"+args[0], State.GOOD);
			Messenger.message(other, "You have recieved the veteran status", State.GOOD);
		} else {
			Messenger.message(sender, "Usage: /veteran username", State.BAD);
			return true;
		}
		
		return true;
	}

}
