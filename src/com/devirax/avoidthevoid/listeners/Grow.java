package com.devirax.avoidthevoid.listeners;

import java.util.ArrayList;

import org.bukkit.block.BlockState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.StructureGrowEvent;

import com.devirax.avoidthevoid.island.Island;
import com.devirax.avoidthevoid.island.IslandManager;
import com.sk89q.worldedit.Vector;

public class Grow implements Listener {
	
	@EventHandler
	public void onGrow(StructureGrowEvent e) {
		Island is = IslandManager.getAbsoluteIslandForLocation(e.getLocation());
		if(is == null)
			return;
		ArrayList<BlockState> toRemove = new ArrayList<BlockState>();
		for(BlockState b : e.getBlocks()) {
			if(!is.getAreaForLevel().contains(new Vector(b.getLocation().getBlockX(), b.getLocation().getBlockY(), b.getLocation().getBlockZ()))) {
				toRemove.add(b);
			}
		}
		e.getBlocks().removeAll(toRemove);
	}

}
