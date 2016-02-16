package com.devirax.avoidthevoid.island.sub;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Owner {
	
	UUID uuid;
	
	public Owner(UUID uuid) {
		this.uuid = uuid;
	}
	
	public UUID getUUID() {
		return uuid;
	}
	
	public Player getPlayer() {
		return Bukkit.getPlayer(uuid);
	}
	
	public String getUsername() {
		String name = Bukkit.getOfflinePlayer(getUUID()).getName();
		if(name == null)
			return "null";
		return name;
	}
	
	public String getSave() {
		return ""+getUUID();
	}
	
}