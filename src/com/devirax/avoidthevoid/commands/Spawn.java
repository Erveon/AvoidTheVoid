package com.devirax.avoidthevoid.commands;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.devirax.avoidthevoid.AvoidTheVoid;
import com.devirax.avoidthevoid.utils.Utils;

public class Spawn implements SubCommand {

	@Override
	public boolean onCommand(CommandSender sender, String[] args) {
		if(!(sender instanceof Player)) {
			sender.sendMessage("You may not execute this command from the console.");
			return true;
		}
		
		Player p = (Player) sender;
		
		World spawnWorld = Bukkit.getWorld(Utils.getSpawnWorldName());
		if(spawnWorld == null)
			AvoidTheVoid.getPlugin().getServer().createWorld(new WorldCreator(Utils.getSpawn().getWorld().getName()));
		
		if(Utils.getSpawn() == null) {
			p.sendMessage("Null!");
			return true;
		}
		p.teleport(Utils.getSpawn());
		return true;
	}
	
	

}
