package com.devirax.avoidthevoid.listeners;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPistonExtendEvent;

import com.devirax.avoidthevoid.island.Island;
import com.devirax.avoidthevoid.island.IslandManager;

public class Piston implements Listener {
	
	Map<Location, Location> valid = new HashMap<Location, Location>();
	
	@EventHandler
	public void onPistonExtend(BlockPistonExtendEvent e) {
		Location loc = e.getBlock().getLocation();
		Location nextLoc = new Location(loc.getWorld(), loc.getX() + e.getDirection().getModX(), loc.getY() + e.getDirection().getModY(), loc.getZ() + e.getDirection().getModZ());
		if(e.getDirection().getModX() > 0) {
			nextLoc = new Location(loc.getWorld(), loc.getX() + e.getDirection().getModX() + e.getLength(), loc.getY() + e.getDirection().getModY(), loc.getZ() + e.getDirection().getModZ());
		} else if(e.getDirection().getModX() < 0) {
			nextLoc = new Location(loc.getWorld(), loc.getX() + e.getDirection().getModX() - e.getLength(), loc.getY() + e.getDirection().getModY(), loc.getZ() + e.getDirection().getModZ());
		} else if(e.getDirection().getModZ() < 0) {
			nextLoc = new Location(loc.getWorld(), loc.getX() + e.getDirection().getModX(), loc.getY() + e.getDirection().getModY(), loc.getZ() + e.getDirection().getModZ() - e.getLength());
		} else if(e.getDirection().getModZ() > 0) {
			nextLoc = new Location(loc.getWorld(), loc.getX() + e.getDirection().getModX(), loc.getY() + e.getDirection().getModY(), loc.getZ() + e.getDirection().getModZ() + e.getLength());
		}
		if(valid.containsKey(loc)) {
			if(valid.get(loc).equals(nextLoc)) {
				return;
			}
		}
		Island is = IslandManager.getAbsoluteIslandForLocation(loc);
		if(is != null) {
			Island checkIs = IslandManager.getAbsoluteIslandForLocation(nextLoc);
			if(checkIs == null) {
				e.setCancelled(true);
			} else if(is != checkIs) {
				e.setCancelled(true);
			} else {
				valid.put(loc, nextLoc);
			}
		}
	}

}
