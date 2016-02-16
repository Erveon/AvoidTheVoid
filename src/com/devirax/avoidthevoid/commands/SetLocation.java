package com.devirax.avoidthevoid.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.devirax.avoidthevoid.AvoidTheVoid;
import com.devirax.avoidthevoid.utils.Messenger;
import com.devirax.avoidthevoid.utils.Utils;
import com.devirax.avoidthevoid.utils.Messenger.State;

public class SetLocation implements SubCommand {

	@Override
	public boolean onCommand(CommandSender sender, String[] args) {
		if(!(sender instanceof Player)) {
			sender.sendMessage("You may not execute this command from the console.");
			return true;
		}
		
		Player p = (Player) sender;
		
		if(!p.isOp()) {
			return true;
		}
		
		if(args.length != 1)
			return true;
		
		if(args[0].equalsIgnoreCase("moon")) {
			String locString = Utils.getStringForLoc(p.getLocation());
			AvoidTheVoid.getPlugin().getConfig().set(args[0].toLowerCase(), locString);
			Utils.loadLocations();
		} else if(args[0].equalsIgnoreCase("home")) {
			String locString = Utils.getStringForLoc(p.getLocation());
			AvoidTheVoid.getPlugin().getConfig().set(args[0].toLowerCase(), locString);
			Utils.loadLocations();
		}
		
		Messenger.message(sender, "The spawn has successfully been set", State.GOOD);
		
		return true;
	}
	
	

}
