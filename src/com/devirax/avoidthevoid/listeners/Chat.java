package com.devirax.avoidthevoid.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.devirax.avoidthevoid.island.IslandManager;

public class Chat implements Listener {
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onChat(AsyncPlayerChatEvent e) {
		Player p = e.getPlayer();
		if(e.getFormat().contains("{atvp}"))
			e.setFormat(e.getFormat().replace("{atvp}", ""+IslandManager.getTotalLevel(p)));
	}

}
