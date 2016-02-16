package com.devirax.avoidthevoid.utils;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.SkullMeta;

import com.devirax.avoidthevoid.AvoidTheVoid;
import com.devirax.avoidthevoid.island.Island;
import com.devirax.avoidthevoid.island.IslandManager;
import com.devirax.avoidthevoid.listeners.Lever.Destination;

public class Utils {
	
	static Location spawn, home, moon;
	static HashMap<Destination, String> levers = new HashMap<Destination, String>();
	static Integer maxIslands = 2;
	static World islandWorld;
	static World netherWorld;
	
	static ArrayList<File> schematics = new ArrayList<File>();
	static ArrayList<File> nether = new ArrayList<File>();
	static ArrayList<File> asteroids = new ArrayList<File>();
	public static HashMap<String, Integer> selectedIsland = new HashMap<String, Integer>();
	
	public static void loadConfig() {
		setWorlds();
		loadDestinations();
	}
	
	public static Island getSelectedIsland(Player p) {
		if(selectedIsland.containsKey(p.getName()))
			return IslandManager.getIslandById(selectedIsland.get(p.getName()));
		return null;
	}
	
	public static void setSelectedIsland(Player p, Island is) {
		selectedIsland.put(p.getName(), is.getId());
	}
	
	public static void setWorlds() {
		islandWorld = Bukkit.getServer().getWorld(AvoidTheVoid.getPlugin().getConfig().getString("islandworld"));
		netherWorld = Bukkit.getServer().getWorld(AvoidTheVoid.getPlugin().getConfig().getString("islandworld") + "_nether");
	}
	
	public static boolean inIslandWorlds(Player p) {
		if(p.getLocation().getWorld().equals(getIslandWorld())
				|| p.getLocation().getWorld().equals(getNetherWorld()))
			return true;
		return false;
	}
	
	public static boolean inIslandWorlds(Location loc) {
		if(loc.getWorld().equals(getIslandWorld())
				|| loc.getWorld().equals(getNetherWorld()))
			return true;
		return false;
	}
	
	public static World getIslandWorld() {
		return islandWorld;
	}
	
	public static World getNetherWorld() {
		return netherWorld;
	}
	
	public static Integer getMaxMembers(Island is) {
		UUID owner = is.getOwner().getUUID();
		if(AvoidTheVoid.permission.playerHas(Utils.getSpawnWorldName(), Bukkit.getOfflinePlayer(owner), "atv.members.50")) {
			return 50;
		} else if(AvoidTheVoid.permission.playerHas(Utils.getSpawnWorldName(), Bukkit.getOfflinePlayer(owner), "atv.members.30")) {
			return 30;
		} else if(AvoidTheVoid.permission.playerHas(Utils.getSpawnWorldName(), Bukkit.getOfflinePlayer(owner), "atv.members.20")) {
			return 20;
		} else if(AvoidTheVoid.permission.playerHas(Utils.getSpawnWorldName(), Bukkit.getOfflinePlayer(owner), "atv.members.14")) {
			return 14;
		}  else if(AvoidTheVoid.permission.playerHas(Utils.getSpawnWorldName(), Bukkit.getOfflinePlayer(owner), "atv.members.9")) {
			return 9;
		} else if(AvoidTheVoid.permission.playerHas(Utils.getSpawnWorldName(), Bukkit.getOfflinePlayer(owner), "atv.members.6")) {
			return 6;
		} else if(AvoidTheVoid.permission.playerHas(Utils.getSpawnWorldName(), Bukkit.getOfflinePlayer(owner), "atv.members.4")) {
			return 4;
		} else {
			return 3;
		}
	}
	
	public static Integer getMaxIslandsFor(Player p) {
		if(p.hasPermission("atv.unlimited"))
			return 999;
		if(p.hasPermission("atv.islands.50"))
			return 50;
		if(p.hasPermission("atv.islands.30"))
			return 30;
		if(p.hasPermission("atv.islands.20"))
			return 20;
		if(p.hasPermission("atv.islands.14"))
			return 14;
		if(p.hasPermission("atv.islands.8"))
			return 8;
		if(p.hasPermission("atv.islands.5"))
			return 5;
		if(IslandManager.getTotalLevel(p) >= 150)
			return 5;
		if(IslandManager.getTotalLevel(p) >= 100)
			return 4;
		if(p.hasPermission("atv.islands.3"))
			return 3;
		if(IslandManager.getTotalLevel(p) >= 50)
			return 3;
		return getMaxIslands();
	}
	
	public static void loadSchematics() {
		File[] schems = AvoidTheVoid.getSchematicFolder().listFiles();
		for(File schem : schems) {
			if(schem.getName().endsWith(".schematic")) {
				schematics.add(schem);
			}
		}
		File[] schems2 = AvoidTheVoid.getOtherSchematicFolder().listFiles();
		for(File schem : schems2) {
			if(schem.getName().endsWith(".schematic")) {
				asteroids.add(schem);
			}
		}
		File[] schems3 = AvoidTheVoid.getNetherSchematicFolder().listFiles();
		for(File schem : schems3) {
			if(schem.getName().endsWith(".schematic")) {
				nether.add(schem);
			}
		}
		Integer schemSize = schematics.size() + asteroids.size() + nether.size();
		System.out.print("[ATV] Loaded "+schemSize+" schematic(s).");
	}
	
	public static Entity[]  getNearbyEntities(Location l, int radius){
		int chunkRadius = radius < 16 ? 1 : (radius - (radius % 16))/16;
		HashSet<Entity> radiusEntities = new HashSet<Entity>();
		for (int chX = 0 -chunkRadius; chX <= chunkRadius; chX ++){
			for (int chZ = 0 -chunkRadius; chZ <= chunkRadius; chZ++){
				int x=(int) l.getX(),y=(int) l.getY(),z=(int) l.getZ();
				for (Entity e : new Location(l.getWorld(),x+(chX*16),y,z+(chZ*16)).getChunk().getEntities()){
					if (e.getLocation().distance(l) <= radius && e.getLocation().getBlock() != l.getBlock()) radiusEntities.add(e);
				}
			}
		}
		return radiusEntities.toArray(new Entity[radiusEntities.size()]);
	}
	
	public static ArrayList<File> getSchematics() {
		return schematics;
	}
	
	public static ArrayList<File> getNether() {
		return nether;
	}
	
	public static ArrayList<File> getAsteroids() {
		return asteroids;
	}
	
	private static Integer getMaxIslands() {
		return maxIslands;
	}
	
    public static boolean isPlayerRightVersion(final Player player) {
        return ((CraftPlayer)player).getHandle().playerConnection.networkManager.getVersion() >= 47;
    }
    
    public static void loadSpawn() {
    	AvoidTheVoid.getPlugin().saveConfig();
    	AvoidTheVoid.getPlugin().reloadConfig();
    	String configLoc = AvoidTheVoid.getPlugin().getConfig().getString("spawn");
    	spawn = getLocForString(configLoc);
    }
    
    public static void loadLocations() {
    	AvoidTheVoid.getPlugin().saveConfig();
    	AvoidTheVoid.getPlugin().reloadConfig();
    	String configLoc;
    	if(AvoidTheVoid.getPlugin().getConfig().contains("moon")) {
    		configLoc = AvoidTheVoid.getPlugin().getConfig().getString("moon");
    		moon = getLocForString(configLoc);
    	}
    	if(AvoidTheVoid.getPlugin().getConfig().contains("home")) {
        	configLoc = AvoidTheVoid.getPlugin().getConfig().getString("home");
        	home = getLocForString(configLoc);
    	}
    }
    
    public static Location getLocForString(String s) {
    	String[] locSplit = s.split("&");
    	Location loc = new Location(
    			Bukkit.getWorld(locSplit[0]), 
    			Integer.parseInt(locSplit[1]), 
    			Integer.parseInt(locSplit[2]), 
    			Integer.parseInt(locSplit[3]), 
    			Float.parseFloat(locSplit[4]),
    			Float.parseFloat(locSplit[5]));
    	return loc;
    }
    
    public static String getStringForLoc(Location loc) {
    	String locString = loc.getWorld().getName()
				+"&"+loc.getBlockX()
				+"&"+loc.getBlockY()
				+"&"+loc.getBlockZ()
				+"&"+loc.getYaw()
				+"&"+loc.getPitch();
    	return locString;
    }
    
    public static String getSpawnWorldName() {
    	AvoidTheVoid.getPlugin().saveConfig();
    	AvoidTheVoid.getPlugin().reloadConfig();
    	String configLoc = AvoidTheVoid.getPlugin().getConfig().getString("spawn");
    	String[] locSplit = configLoc.split("&");
    	return locSplit[0];
    }
    
    public static Location getSpawn() {
    	if(spawn == null)
    		loadSpawn();
    	return spawn;
    }
    
    public static Location getLocation(Destination dest) {
    	if(dest.equals(Destination.MOON)) {
	    	if(moon == null)
	    		loadLocations();
	    	return moon;
    	} else if(dest.equals(Destination.HOME)) {
    		if(home == null)
	    		loadLocations();
	    	return home;
    	}
    	return null;
    }
    
    public static Location getLever(Destination dest) {
    	if(!levers.containsKey(dest))
    		loadDestination(dest);
    	return getLocForString(levers.get(dest));
    }
    
    public static void loadDestination(Destination dest) {
    	if(AvoidTheVoid.getPlugin().getConfig().contains(dest.toString().toLowerCase()+"lever"))
    		levers.put(dest, AvoidTheVoid.getPlugin().getConfig().getString(dest.toString().toLowerCase()+"lever"));
    }
    
    public static void loadDestinations() {
    	for(Destination dest : Destination.values())
    		loadDestination(dest);
    }
    
    public static ItemStack getHead(String name) {
        ItemStack skull = new ItemStack(Material.SKULL_ITEM);
        
        skull.setDurability((short)3);
        SkullMeta sm = (SkullMeta) skull.getItemMeta();
        sm.setOwner(name);
        sm.setDisplayName(ChatColor.AQUA + "" + name);
        skull.setItemMeta(sm);

        return skull;
    }
    
	public static Integer getSlotCount(int i) {
		if(i < 9) {
			return 9;
        } else if(i <= 18) {
        	return 18;
        } else if(i <= 27) {
        	return 27;
        } else if(i <= 36) {
        	return 36;
        } else if(i <= 45) {
        	return 45;
        } else {
        	return 54;
        }
	}
	
	private final static Color[] colors = new Color[] { Color.AQUA, Color.BLACK,  Color.BLUE, Color.FUCHSIA, Color.GRAY, Color.GREEN, Color.LIME, Color.MAROON, Color.NAVY, Color.OLIVE, Color.ORANGE, Color.PURPLE, Color.RED, Color.SILVER, Color.TEAL, Color.WHITE, Color.YELLOW };
	
	public static void shootFirework(Player p) {
		Firework fw = (Firework) p.getWorld().spawn(p.getLocation(), Firework.class);
		FireworkMeta fwMeta = fw.getFireworkMeta();
		boolean flicker = Math.random() < 0.5;
		boolean trail = Math.random() < 0.5;
		fwMeta.addEffect(FireworkEffect.builder().withColor(colors[new Random().nextInt(colors.length)]).flicker(flicker).trail(trail).build());
		fw.setFireworkMeta(fwMeta);
	}
	
	public static void shootFireworks(final Player p) {
		shootFirework(p);
		Bukkit.getScheduler().scheduleSyncDelayedTask(AvoidTheVoid.getPlugin(), new Runnable() {
			@Override
			public void run() {
				shootFirework(p);
				Bukkit.getScheduler().scheduleSyncDelayedTask(AvoidTheVoid.getPlugin(), new Runnable() {
					@Override
					public void run() {
						shootFirework(p);
						Bukkit.getScheduler().scheduleSyncDelayedTask(AvoidTheVoid.getPlugin(), new Runnable() {
							@Override
							public void run() {
								shootFirework(p);
								Bukkit.getScheduler().scheduleSyncDelayedTask(AvoidTheVoid.getPlugin(), new Runnable() {
									@Override
									public void run() {
										shootFirework(p);
										Bukkit.getScheduler().scheduleSyncDelayedTask(AvoidTheVoid.getPlugin(), new Runnable() {
											@Override
											public void run() {
												shootFirework(p);
												Bukkit.getScheduler().scheduleSyncDelayedTask(AvoidTheVoid.getPlugin(), new Runnable() {
													@Override
													public void run() {
														shootFirework(p);
													}
												}, 20);
											}
										}, 20);
									}
								}, 20);
							}
						}, 20);
					}
				}, 20);
			}
		}, 20);
	}
	
	public static double round(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();

	    BigDecimal bd = new BigDecimal(value);
	    bd = bd.setScale(places, RoundingMode.HALF_UP);
	    return bd.doubleValue();
	}

}
