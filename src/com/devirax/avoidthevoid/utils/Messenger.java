package com.devirax.avoidthevoid.utils;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.devirax.avoidthevoid.island.Island;

public class Messenger {
	
	public enum State { GOOD, BAD, INFO };
	
	public static void messageIsland(Island is, String message, State state) {
		if(is == null)
			return;
		switch(state) {
		case BAD:
			for(Player p : is.getOnlinePlayers())
				p.sendMessage("§4»§r "+message+" §4«");
			break;
		case GOOD:
			for(Player p : is.getOnlinePlayers())
				p.sendMessage("§a»§r "+message+" §a«");
			break;
		case INFO:
			for(Player p : is.getOnlinePlayers())
				p.sendMessage("§9»§r "+message+" §9«");
			break;
		default:
			break;
		}
	}
	
	public static void message(Player p, String message, State state) {
		switch(state) {
		case BAD:
			p.sendMessage("§4»§r "+message+" §4«");
			break;
		case GOOD:
			p.sendMessage("§a»§r "+message+" §a«");
			break;
		case INFO:
			p.sendMessage("§9»§r "+message+" §9«");
			break;
		default:
			break;
		}
	}
	
	public static void message(CommandSender sender, String message, State state) {
		switch(state) {
		case BAD:
			sender.sendMessage("§4»§r "+message+" §4«");
			break;
		case GOOD:
			sender.sendMessage("§a»§r "+message+" §a«");
			break;
		case INFO:
			sender.sendMessage("§9»§r "+message+" §9«");
			break;
		default:
			break;
		}
	}
	
	@SuppressWarnings("deprecation")
	public static void messageServer(String message, State state) {
		switch(state) {
		case BAD:
			for(Player pl : Bukkit.getOnlinePlayers())
				pl.sendMessage("§4»§r "+message+" §4«");
			break;
		case GOOD:
			for(Player pl : Bukkit.getOnlinePlayers())
				pl.sendMessage("§a»§r "+message+" §a«");
			break;
		case INFO:
			for(Player pl : Bukkit.getOnlinePlayers())
				pl.sendMessage("§9»§r "+message+" §9«");
			break;
		default:
			break;
		}
	}

}
