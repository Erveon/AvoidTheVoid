package com.devirax.avoidthevoid.listeners;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import com.devirax.avoidthevoid.AvoidTheVoid;
import com.devirax.avoidthevoid.island.Island;
import com.devirax.avoidthevoid.island.IslandManager;
import com.devirax.avoidthevoid.utils.Messenger;
import com.devirax.avoidthevoid.utils.Messenger.State;
import com.devirax.avoidthevoid.utils.Utils;
import com.sk89q.worldedit.Vector;

public class Build implements Listener {
	
	@EventHandler
	public void onBuild(BlockPlaceEvent e) {
		Player p = e.getPlayer();
		if(!AvoidTheVoid.hasDatabaseConnection()) {
			Messenger.message(p, "You cannot do this whilst the plugin is in safe mode", State.BAD);
			e.setCancelled(true);
			return;
		}
		if(!Utils.inIslandWorlds(e.getBlock().getLocation()))
			return;
		boolean canBuild = false;
		if(p.hasPermission("atv.bypass")) {
			canBuild = true;
		} else {
			for(Island is : IslandManager.getIslandsFor(p)) {
				if(is.getArea().contains(new Vector(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()))) {
					if(is.getAreaForLevel().contains(new Vector(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()))) {
						canBuild = true;
					} else {
						Messenger.message(p, "You must level your island up to place blocks here", State.BAD);
						e.setCancelled(true);
						return;
					}
				}
			}
		}
		if(!canBuild) {
			Messenger.message(p, "You may only build on your own islands", State.BAD);
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onBreak(BlockBreakEvent e) {
		Player p = e.getPlayer();
		if(!AvoidTheVoid.hasDatabaseConnection()) {
			Messenger.message(p, "You cannot do this whilst the plugin is in safe mode", State.BAD);
			e.setCancelled(true);
			return;
		}
		if(!Utils.inIslandWorlds(e.getBlock().getLocation()))
			return;
		boolean canBuild = false;
		if(p.hasPermission("atv.bypass")) {
			canBuild = true;
		} else {
			for(Island is : IslandManager.getIslandsFor(p)) {
				if(is.getArea().contains(new Vector(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()))) {
					if(is.getAreaForLevel().contains(new Vector(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()))) {
						canBuild = true;
					} else {
						Messenger.message(p, "You must level your island up to break blocks here", State.BAD);
						e.setCancelled(true);
						return;
					}
				}
			}
		}
		if(!canBuild) {
			Messenger.message(p, "You may only build on your own islands", State.BAD);
			e.setCancelled(true);
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBreak(HangingBreakByEntityEvent e) {
		if(!(e.getRemover() instanceof Player)) {
			e.setCancelled(true);
			return;
		}
		Player p = (Player) e.getRemover();
		if(!AvoidTheVoid.hasDatabaseConnection()) {
			Messenger.message(p, "You cannot do this whilst the plugin is in safe mode", State.BAD);
			e.setCancelled(true);
			return;
		}
		if(!Utils.inIslandWorlds(e.getEntity().getLocation()))
			return;
		boolean canBuild = false;
		if(p.hasPermission("atv.bypass")) {
			canBuild = true;
		} else {
			for(Island is : IslandManager.getIslandsFor(p)) {
				if(is.getArea().contains(new Vector(e.getEntity().getLocation().getX(), e.getEntity().getLocation().getY(), e.getEntity().getLocation().getZ()))) {
					if(is.getAreaForLevel().contains(new Vector(e.getEntity().getLocation().getX(), e.getEntity().getLocation().getY(), e.getEntity().getLocation().getZ()))) {
						canBuild = true;
					} else {
						Messenger.message(p, "You must level your island up to break blocks here", State.BAD);
						e.setCancelled(true);
						return;
					}
				}
			}
		}
		if(!canBuild) {
			Messenger.message(p, "You may only build on your own islands", State.BAD);
			e.setCancelled(true);
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onHangingPlaceEvent(PlayerInteractEntityEvent e) {
		if(e.getRightClicked().getType() != EntityType.ITEM_FRAME)
			return;
		Player p = e.getPlayer();
		if(!AvoidTheVoid.hasDatabaseConnection()) {
			Messenger.message(p, "You cannot do this whilst the plugin is in safe mode", State.BAD);
			e.setCancelled(true);
			return;
		}
		if(!Utils.inIslandWorlds(e.getRightClicked().getLocation()))
			return;
		boolean canBuild = false;
		if(p.hasPermission("atv.bypass")) {
			canBuild = true;
		} else {
			for(Island is : IslandManager.getIslandsFor(p)) {
				if(is.getArea().contains(new Vector(e.getRightClicked().getLocation().getX(), e.getRightClicked().getLocation().getY(), e.getRightClicked().getLocation().getZ()))) {
					if(is.getAreaForLevel().contains(new Vector(e.getRightClicked().getLocation().getX(), e.getRightClicked().getLocation().getY(), e.getRightClicked().getLocation().getZ()))) {
						canBuild = true;
					} else {
						Messenger.message(p, "You must level your island up to break blocks here", State.BAD);
						e.setCancelled(true);
						return;
					}
				}
			}
		}
		if(!canBuild) {
			Messenger.message(p, "You may only build on your own islands", State.BAD);
			e.setCancelled(true);
		}
	}
	
	 @EventHandler(priority = EventPriority.HIGHEST)
	 public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent e) {
		 if(e.getEntityType().equals(EntityType.ITEM_FRAME) && (e.getDamager().getType().equals(EntityType.ARROW) || e.getDamager().getType().equals(EntityType.SNOWBALL)))
			 e.setCancelled(true);
	 }
	 
}
