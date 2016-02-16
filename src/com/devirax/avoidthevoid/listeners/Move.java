package com.devirax.avoidthevoid.listeners;

import java.util.HashMap;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import com.devirax.avoidthevoid.AvoidTheVoid;
import com.devirax.avoidthevoid.island.Island;
import com.devirax.avoidthevoid.island.IslandManager;
import com.devirax.avoidthevoid.utils.Messenger;
import com.devirax.avoidthevoid.utils.Utils;
import com.devirax.avoidthevoid.utils.Messenger.State;

public class Move implements Listener {

	HashMap<String, String> onIsland = new HashMap<String, String>();
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onMove(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		if(!AvoidTheVoid.hasDatabaseConnection() && !e.getFrom().getBlock().equals(e.getTo().getBlock())) {
			Messenger.message(p, "You cannot do this whilst the plugin is in safe mode", State.BAD);
			p.teleport(e.getFrom());
			return;
		}
		if(!Utils.inIslandWorlds(p))
			return;
		Island island = IslandManager.getAbsoluteIslandForLocation(e.getTo());
		if(island != null) {
			if(!onIsland.containsKey(p.getName())) {
				onIsland.put(p.getName(), island.getOwner().getUsername());
				if(onIsland.get(p.getName()) != "null")
					p.sendMessage("§bYou have entered "+island.getName()+", "+onIsland.get(p.getName())+"'s island.");
				else
					p.sendMessage("§bYou have entered "+island.getName()+".");
			} else if(!onIsland.get(p.getName()).equals(island.getOwner().getUsername())) {
				onIsland.put(p.getName(), island.getOwner().getUsername());
				if(island.getOwner().getUsername() != "null")
					p.sendMessage("§bYou have entered "+island.getName()+", "+onIsland.get(p.getName())+"'s island.");
				else
					p.sendMessage("§bYou have entered "+island.getName()+".");
			}
		} else {
			if(onIsland.containsKey(p.getName())) {
				if(onIsland.get(p.getName()) != null)
					p.sendMessage("§bYou have left "+onIsland.get(p.getName())+"'s island.");
				else
					p.sendMessage("§bYou have left an island.");
				onIsland.remove(p.getName());
			}
		}
	}
	
}
