package com.devirax.avoidthevoid.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetEvent;

import com.devirax.avoidthevoid.island.Island;
import com.devirax.avoidthevoid.island.IslandManager;

public class Target implements Listener {
	
	@EventHandler
	public void onTarget(EntityTargetEvent e) {
		Island is = IslandManager.getAbsoluteIslandForLocation(e.getEntity().getLocation());
		if(is == null)
			return;
		if(e.getTarget() instanceof Player) {
			if(!is.getOnlinePlayers().contains(e.getTarget())) {
				e.setCancelled(true);
			}
		}
	}

}
