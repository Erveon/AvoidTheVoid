package com.devirax.avoidthevoid.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.devirax.avoidthevoid.acts.AsteroidAct;

public class Act implements SubCommand {

	@Override
	public boolean onCommand(CommandSender sender, String[] args) {
		
		if(sender instanceof Player) {
			if(!((Player) sender).isOp())
				return true;
		}
		
		if(args.length == 2) {
			if(args[0].equalsIgnoreCase("asteroid")) {
				if(args[1].equalsIgnoreCase("reset")) {
					AsteroidAct.regenerate();
				}
			}
		}
		
		return true;
	}
	
	

}
