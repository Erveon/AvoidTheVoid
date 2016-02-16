package com.devirax.avoidthevoid.island.sub;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Member {
	
	UUID uuid;
	
	public Member(UUID uuid) {
		this.uuid = uuid;
	}
	
	public UUID getUUID() {
		return uuid;
	}
	
	public Player getPlayer() {
		return Bukkit.getPlayer(uuid);
	}
	
	public String getUsername() {
		return Bukkit.getOfflinePlayer(getUUID()).getName();
	}
	
}