package com.devirax.avoidthevoid.island;

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.PigZombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import com.devirax.avoidthevoid.utils.Utils;
import com.sk89q.worldedit.Vector;

public class Nether implements Listener {
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPortal(PlayerPortalEvent e) {
		if(!e.getCause().equals(TeleportCause.NETHER_PORTAL))
			return;
		Island is = IslandManager.getAbsoluteIslandForLocation(e.getPlayer().getLocation());
		if(is == null)
			return;
		if(e.getPlayer().getLocation().getWorld().equals(Utils.getIslandWorld())) {
			Location tp = findNetherPortal(is, Utils.getNetherWorld());
			if(tp != null) {
				e.getPlayer().teleport(tp);
			} else {
				Location spawnLoc = new Location(Utils.getNetherWorld(), is.getAbsoluteX(), 40, is.getAbsoluteZ());
				e.getPlayer().teleport(spawnLoc);
			}
		} else if(e.getPlayer().getLocation().getWorld().equals(Utils.getNetherWorld())) {
			Location tp = findNetherPortal(is, Utils.getIslandWorld());
			if(tp != null) {
				e.getPlayer().teleport(tp);
			} else {
				e.getPlayer().teleport(is.getSpawn());
			}
		}
	}
	
	@EventHandler
	public void onSpawn(CreatureSpawnEvent e) {
		if(e.getEntity() instanceof PigZombie) {
			if(e.getSpawnReason().equals(SpawnReason.NATURAL)) {
				if(e.getLocation().getWorld().equals(Utils.getNetherWorld())) {
					Integer rand = new Random().nextInt(50);
					if(rand == 1) {
						e.setCancelled(true);
						e.getLocation().getWorld().spawnEntity(e.getLocation(), EntityType.BLAZE);
					}
				}
			}
		}
	}
	
	public Location findNetherPortal(Island is, World world) {
		Location loc = null;
		
		boolean direction;
		Vector pos1 = is.getArea().getPos1();
		Vector pos2 = is.getArea().getPos2();
		
		if(pos1.getBlockX() < pos2.getBlockX())
			direction = true;
		else
			direction = false;
		if(direction) {
			for(int x = pos1.getBlockX(); x <= pos2.getBlockX(); x++) {
				for(int y = pos1.getBlockY(); y <= pos2.getBlockY(); y++) {
					for(int z = pos1.getBlockZ(); z <= pos2.getBlockZ(); z++) {
						Block block = world.getBlockAt(new Location(world, x, y, z));
						if(block.getType().equals(Material.PORTAL)) {
							Block blockDown = world.getBlockAt(new Location(world, x, y-1, z));
							if(!blockDown.equals(Material.PORTAL)) {
								return blockDown.getLocation();
							}
						}
					}
				}
			}
		} else {
			for(int x = pos2.getBlockX(); x <= pos1.getBlockX(); x++) {
				for(int y = pos2.getBlockY(); y <= pos1.getBlockY(); y++) {
					for(int z = pos2.getBlockZ(); z <= pos1.getBlockZ(); z++) {
						Block block = world.getBlockAt(new Location(world, x, y, z));
						if(block.getType().equals(Material.PORTAL)) {
							Block blockDown = world.getBlockAt(new Location(world, x, y-1, z));
							if(!blockDown.equals(Material.PORTAL)) {
								return blockDown.getLocation();
							}
						}
					}
				}
			}
		}
		
		return loc;
	}

}
