package com.devirax.avoidthevoid.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.devirax.avoidthevoid.island.Island;
import com.devirax.avoidthevoid.island.IslandManager;
import com.devirax.avoidthevoid.utils.Messenger;
import com.devirax.avoidthevoid.utils.Utils;
import com.devirax.avoidthevoid.utils.Messenger.State;

public class Invite implements SubCommand {

	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, String[] args) {
		if(!(sender instanceof Player))
			return true;
		
		Player p = (Player) sender;
		
		if(args.length != 1) {
			Messenger.message(p, "Please provide a valid name, replace spaces with underscores.", State.BAD);
			return true;
		}
		
		String name = args[0];
		
		Island is = IslandManager.getAbsoluteIslandForLocation(p.getLocation());
		if(is == null) {
			Messenger.message(p, "You must be on one of your own islands to do this.", State.BAD);
			return true;
		}
		
		if(p.equals(is.getOwner().getPlayer())) {
			for(Player player : Bukkit.getOnlinePlayers()) {
				if(player.getName().equalsIgnoreCase(name)) {
					if(is.getOnlinePlayers().contains(player)) {
						Messenger.message(p, "§b"+player.getName()+"§f is already on §9"+is.getName(), State.BAD);
						return true;
					} else if(Utils.getMaxIslandsFor(player) <= IslandManager.getIslandsFor(player).size()) {
						Messenger.message(p, "§b"+player.getName()+"§f has reached their maximum amount of islands", State.BAD);
						return true;
					}
					is.invite(player);
					return true;
				}
			}
			Messenger.message(p, "§b"+name + "§f is not online or does not exist.", State.BAD);
		} else {
			Messenger.message(p, "You must own the island to do this.", State.BAD);
		}
		return true;
	}
	

}
