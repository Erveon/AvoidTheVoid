package com.devirax.avoidthevoid.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import com.devirax.avoidthevoid.AvoidTheVoid;
import com.devirax.avoidthevoid.island.Island;
import com.devirax.avoidthevoid.island.IslandManager;
import com.devirax.avoidthevoid.utils.Messenger;
import com.devirax.avoidthevoid.utils.Utils;
import com.devirax.avoidthevoid.utils.Messenger.State;
import com.sk89q.worldedit.Vector;

public class Interact implements Listener {
	
	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		if(!AvoidTheVoid.hasDatabaseConnection()) {
			Messenger.message(p, "You cannot do this whilst the plugin is in safe mode", State.BAD);
			e.setCancelled(true);
			return;
		}
		if(!Utils.inIslandWorlds(p))
			return;
		if(e.getClickedBlock() == null)
			return;
		boolean canBuild = false;
		if(p.hasPermission("atv.bypass")) {
			canBuild = true;
		} else {
			for(Island is : IslandManager.getIslandsFor(p)) {
				if(is.getArea().contains(new Vector(e.getClickedBlock().getX(), e.getClickedBlock().getY(), e.getClickedBlock().getZ()))) {
					canBuild = true;
				}
			}
		}
		if(!canBuild)
			e.setCancelled(true);
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEntityEvent e) {
		Player p = e.getPlayer();
		if(!AvoidTheVoid.hasDatabaseConnection()) {
			Messenger.message(p, "You cannot do this whilst the plugin is in safe mode", State.BAD);
			e.setCancelled(true);
			return;
		}
		if(!Utils.inIslandWorlds(p))
			return;
		if(e.getRightClicked() == null)
			return;
		boolean canBuild = false;
		if(p.hasPermission("atv.bypass")) {
			canBuild = true;
		} else {
			for(Island is : IslandManager.getIslandsFor(p)) {
				if(is.getArea().contains(new Vector(e.getRightClicked().getLocation().getX(), e.getRightClicked().getLocation().getY(), e.getRightClicked().getLocation().getZ()))) {
					canBuild = true;
				}
			}
		}
		if(!canBuild)
			e.setCancelled(true);
	}
	
	@EventHandler
	public void onInteract(PlayerBucketEmptyEvent e) {
		Player p = e.getPlayer();
		if(!AvoidTheVoid.hasDatabaseConnection()) {
			Messenger.message(p, "You cannot do this whilst the plugin is in safe mode", State.BAD);
			e.setCancelled(true);
			return;
		}
		if(!Utils.inIslandWorlds(p))
			return;
		if(e.getBlockClicked() == null)
			return;
		boolean canBuild = false;
		if(p.hasPermission("atv.bypass")) {
			canBuild = true;
		} else {
			for(Island is : IslandManager.getIslandsFor(p)) {
				if(is.getArea().contains(new Vector(e.getBlockClicked().getLocation().getX(), e.getBlockClicked().getLocation().getY(), e.getBlockClicked().getLocation().getZ()))) {
					canBuild = true;
				}
			}
		}
		if(!canBuild)
			e.setCancelled(true);
	}
	
	@EventHandler
	public void onInteract(PlayerBucketFillEvent e) {
		Player p = e.getPlayer();
		if(!AvoidTheVoid.hasDatabaseConnection()) {
			Messenger.message(p, "You cannot do this whilst the plugin is in safe mode", State.BAD);
			e.setCancelled(true);
			return;
		}
		if(!Utils.inIslandWorlds(p))
			return;
		if(e.getBlockClicked() == null)
			return;
		boolean canBuild = false;
		if(p.hasPermission("atv.bypass")) {
			canBuild = true;
		} else {
			for(Island is : IslandManager.getIslandsFor(p)) {
				if(is.getArea().contains(new Vector(e.getBlockClicked().getLocation().getX(), e.getBlockClicked().getLocation().getY(), e.getBlockClicked().getLocation().getZ()))) {
					canBuild = true;
				}
			}
		}
		if(!canBuild)
			e.setCancelled(true);
	}

}
