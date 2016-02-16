package com.devirax.avoidthevoid.listeners;

import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import com.devirax.avoidthevoid.island.Island;
import com.devirax.avoidthevoid.island.IslandManager;

public class Damage implements Listener {
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onDamage(EntityDamageByEntityEvent e) {
		Island is = IslandManager.getAbsoluteIslandForLocation(e.getEntity().getLocation());
		if(is == null)
			return;
		if(e.getDamager() instanceof Player) {
			if(!is.getOnlinePlayers().contains(e.getDamager())) {
				e.setCancelled(true);
			}
		} else if(e.getDamager() instanceof Slime) {
			if(e.getEntity() instanceof Player) {
				Player damaged = (Player) e.getEntity();
				if(!is.getOnlinePlayers().contains(damaged)) {
					e.setCancelled(true);
				}
			}
		}
	}

}
