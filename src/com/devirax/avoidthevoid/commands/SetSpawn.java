package com.devirax.avoidthevoid.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.devirax.avoidthevoid.AvoidTheVoid;
import com.devirax.avoidthevoid.utils.Messenger;
import com.devirax.avoidthevoid.utils.Utils;
import com.devirax.avoidthevoid.utils.Messenger.State;

public class SetSpawn implements SubCommand {

	@Override
	public boolean onCommand(CommandSender sender, String[] args) {
		
		if(!(sender instanceof Player)) {
			sender.sendMessage("You may not execute this command from the console.");
			return true;
		}
		
		Player p = (Player) sender;
		
		if(!p.isOp() && !p.hasPermission("atv.setspawn")) {
			return true;
		}
		
		String spawnString = p.getLocation().getWorld().getName()
				+"&"+p.getLocation().getBlockX()
				+"&"+p.getLocation().getBlockY()
				+"&"+p.getLocation().getBlockZ()
				+"&"+p.getLocation().getYaw()
				+"&"+p.getLocation().getPitch();
		
		AvoidTheVoid.getPlugin().getConfig().set("spawn", spawnString);
		Utils.loadSpawn();
		
		Messenger.message(sender, "The spawn has successfully been set", State.GOOD);
		
		return true;
	}

}
