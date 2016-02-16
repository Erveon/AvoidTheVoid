package com.devirax.avoidthevoid.listeners;

import org.bukkit.CropState;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTameEvent;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.event.world.StructureGrowEvent;
import org.bukkit.material.Crops;
import org.bukkit.material.MaterialData;

import com.devirax.avoidthevoid.island.Island;
import com.devirax.avoidthevoid.island.IslandManager;

public class Experience implements Listener {
	
	@EventHandler
	public void onGrow(StructureGrowEvent e) {
		Island is = IslandManager.getAbsoluteIslandForLocation(e.getLocation());
		if(is == null)
			return;
		if(e.isFromBonemeal())
			is.addExperience(e.getPlayer(), 6);
		else
			is.addExperience(15);
	}
	
	@EventHandler
	public void onDeath(EntityDeathEvent e) {
		Island is = IslandManager.getAbsoluteIslandForLocation(e.getEntity().getLocation());
		if(is == null)
			return;
		if(e.getEntity().getKiller() != null) {
			if(is.getOnlinePlayers().contains(e.getEntity().getKiller())) {
				switch(e.getEntity().getType()) {
				case CHICKEN:
				case HORSE:
				case BAT:
				case MAGMA_CUBE:
				case OCELOT:
				case SILVERFISH:
				case SLIME:
				case SNOWMAN:
				case SQUID:
				case VILLAGER:
					is.addExperience(e.getEntity().getKiller(), 2);
					break;
				case BLAZE:
				case CAVE_SPIDER:
				case COW:
				case CREEPER:
				case PIG:
				case SHEEP:
				case SKELETON:
				case SPIDER:
				case WITCH:
				case WOLF:
				case ZOMBIE:
				case IRON_GOLEM:
					is.addExperience(e.getEntity().getKiller(), 5);
					break;
				case ENDERMAN:
				case GHAST:
				case GIANT:
				case WITHER:
					is.addExperience(e.getEntity().getKiller(), 6);
					break;
				default:
					break;
				}
			}
		} else {
			switch(e.getEntity().getType()) {
			case CHICKEN:
			case HORSE:
			case BAT:
			case OCELOT:
			case SILVERFISH:
			case SLIME:
			case SNOWMAN:
			case SQUID:
			case VILLAGER:
				is.addExperience(1);
				break;
			case BLAZE:
			case CAVE_SPIDER:
			case COW:
			case CREEPER:
			case PIG:
			case SHEEP:
			case SKELETON:
			case SPIDER:
			case WITCH:
			case WOLF:
			case ZOMBIE:
			case IRON_GOLEM:
				is.addExperience(3);
				break;
			case ENDERMAN:
			case GIANT:
			case WITHER:
				is.addExperience(5);
				break;
			default:
				break;
			}
		}
	}
	
	@EventHandler
	public void onBreak(BlockBreakEvent e) {
		Island is = IslandManager.getAbsoluteIslandForLocation(e.getBlock().getLocation());
		if(is == null)
			return;
		if(is.getOnlinePlayers().contains(e.getPlayer())) {
			if(isFullyGrownCrop(e.getBlock())) {
				is.addExperience(e.getPlayer(), 10);
			} else {
				is.addExperience(e.getPlayer(), 0.1);
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	public boolean isFullyGrownCrop(Block b) {
		MaterialData md = b.getState().getData();
		if (md instanceof Crops) {
			return ((Crops) md).getState() == CropState.RIPE;
		} else if (b.getType() == Material.CARROT || b.getType() == Material.POTATO) {
			return md.getData() == 7;
		}
		return false;
	}
	
	@EventHandler
	public void onPlace(BlockPlaceEvent e) {
		Island is = IslandManager.getAbsoluteIslandForLocation(e.getBlock().getLocation());
		if(is == null)
			return;
		if(is.getOnlinePlayers().contains(e.getPlayer())) {
			is.addExperience(e.getPlayer(), 0.1);
		}
	}
	
	/*@EventHandler
    public void onFromTo(BlockFromToEvent e) {
		Island is = IslandManager.getAbsoluteIslandForLocation(e.getBlock().getLocation());
		if(is == null)
			return;
        Material type = e.getBlock().getType();
        if(type.equals(Material.WATER) || type.equals(Material.LAVA) || type.equals(Material.STATIONARY_LAVA) || type.equals(Material.STATIONARY_WATER)) {
            Block b = e.getToBlock();
            Material toType = b.getType();
            if(toType.equals(Material.AIR)) {
                if(generatesCobble(type, b))
                    is.addExperience(1);
            }
        }
    }*/
 
    private final BlockFace[] faces = new BlockFace[] { BlockFace.SELF, BlockFace.UP,  BlockFace.DOWN, BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST };
 
    public boolean generatesCobble(Material type, Block b) {
        Material mirrorID1 = (type.equals(Material.WATER) || type.equals(Material.STATIONARY_WATER) ? Material.LAVA : Material.WATER);
        Material mirrorID2 = (type.equals(Material.WATER) || type.equals(Material.STATIONARY_WATER) ? Material.STATIONARY_LAVA : Material.STATIONARY_LAVA);
        for(BlockFace face : faces) {
            Block r = b.getRelative(face, 1);
            if(r.getType() == mirrorID1 || r.getType() == mirrorID2)
                return true;
        }
        return false;
    }
    
    @EventHandler
    public void onSpawn(CreatureSpawnEvent e) {
		Island is = IslandManager.getAbsoluteIslandForLocation(e.getEntity().getLocation());
		if(is == null)
			return;
        if(e.getSpawnReason().equals(SpawnReason.BREEDING))
        	is.addExperience(8);
    }
    
    @EventHandler
	public void onCatch(PlayerFishEvent e) {
    	Island is = IslandManager.getAbsoluteIslandForLocation(e.getPlayer().getLocation());
		if(is == null)
			return;
		if(e.getCaught() != null)
			is.addExperience(e.getPlayer(), 8);
	}
    
    @EventHandler
   	public void onShear(PlayerShearEntityEvent e) {
       	Island is = IslandManager.getAbsoluteIslandForLocation(e.getPlayer().getLocation());
   		if(is == null)
   			return;
   		is.addExperience(e.getPlayer(), 5);
   	}
    
    @EventHandler
   	public void onTame(EntityTameEvent e) {
       	Island is = IslandManager.getAbsoluteIslandForLocation(((Player) e.getOwner()).getLocation());
   		if(is == null)
   			return;
   		is.addExperience((Player) e.getOwner(), 15);
   	}
   	
   	@EventHandler
   	public void onSmelt(FurnaceSmeltEvent e) {
       	Island is = IslandManager.getAbsoluteIslandForLocation(e.getBlock().getLocation());
   		if(is == null)
   			return;
   		switch(e.getResult().getType()) {
   		case BAKED_POTATO:
   		case COOKED_BEEF:
   		case COOKED_CHICKEN:
   		case COOKED_FISH:
   		case PORK:
   		case STONE:
   		case COAL:
   		case GLASS:
   			is.addExperience(1);
			break;
   		case IRON_INGOT:
   		case GOLD_INGOT:
   	   		is.addExperience(4);
   			break;
		default:
			break;
   		}
   	}
   	
   	@EventHandler
   	public void onBrew(BrewEvent e) {
       	Island is = IslandManager.getAbsoluteIslandForLocation(e.getBlock().getLocation());
   		if(is == null)
   			return;
   		is.addExperience(5);
   	}
   	
   	@EventHandler
   	public void onTame(EnchantItemEvent e) {
       	Island is = IslandManager.getAbsoluteIslandForLocation(e.getEnchantBlock().getLocation());
   		if(is == null)
   			return;
   		is.addExperience(e.getEnchanter(), e.getExpLevelCost()/2);
   	}

}
