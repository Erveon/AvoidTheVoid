package com.devirax.avoidthevoid.island;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.devirax.avoidthevoid.AvoidTheVoid;
import com.devirax.avoidthevoid.island.sub.Member;
import com.devirax.avoidthevoid.island.sub.Owner;
import com.devirax.avoidthevoid.persistence.IslandDB;
import com.devirax.avoidthevoid.utils.Messenger;
import com.devirax.avoidthevoid.utils.Messenger.State;
import com.devirax.avoidthevoid.utils.Utils;
import com.sk89q.worldedit.Vector;

public class IslandManager {
	
	public static ArrayList<Island> islands = new ArrayList<Island>();
	public static HashMap<String, Island> toRename = new HashMap<String, Island>();
	public static HashMap<String, Island> toInvite = new HashMap<String, Island>();
	public static ArrayList<ArrayList<Integer>> takenCoords = new ArrayList<ArrayList<Integer>>();
	public static HashMap<String, Integer> totalLevel = new HashMap<String, Integer>();
	public static HashMap<UUID, ArrayList<Island>> permittedIslands = new HashMap<UUID, ArrayList<Island>>();
	public static HashMap<UUID, ArrayList<Island>> invitedIslands = new HashMap<UUID, ArrayList<Island>>();
	public static HashMap<Integer, String> topIslands = new HashMap<Integer, String>();
	
	public static int topIslandCounter = 300;
	
	public static void load() {
		IslandDB.loadIslands();
		topIslandTask();
	}
	
	public static void topIslandTask() {
		IslandDB.loadTopIslands();
		
		new BukkitRunnable() {

			@Override
			public void run() {
				if(topIslandCounter > 0) {
					--topIslandCounter;
				} else {
					IslandDB.loadTopIslands();
					topIslandCounter = 300;
				}
			}
			
		}.runTaskTimerAsynchronously(AvoidTheVoid.getPlugin(), 0, 20);
	}
	
	//Called from IslandDB
	public static void setTopIslands(HashMap<Integer, String> topIslands) {
		IslandManager.topIslands = topIslands;
	}
	
	public static Map<Integer, String> getTopIslands() {
		return topIslands;
	}
	
	public static void createIsland(Player p) {
		UUID id = p.getUniqueId();
		if(id == null) {
			Messenger.message(p, "Could not contact Mojang for your UUID, please try again in a few minutes.", State.BAD);
			return;
		}
		Island is = new Island(p, id);
		addToTakenCoords(is);
		islands.add(is);
		saveIsland(is);
	}

	public static void saveAllIslands() {
		for(Island is : getIslands()) {
			saveIsland(is);
		}
	}
	
	public static void saveAllUnsavedIslands() {
		new BukkitRunnable() {
			@Override
			public void run() {
				for(Island is : getIslands()) {
					if(is.toSave())
						saveIsland(is);
				}
			}
		}.runTaskAsynchronously(AvoidTheVoid.getPlugin());
	}
	
	public static void saveAllConvertedIslands() {
		(new SaveThread()).start();
	}
	
	public static void saveTimer() {
		Bukkit.getScheduler().runTaskTimer(AvoidTheVoid.getPlugin(), new Runnable() {
			@Override
			public void run() {
				if(AvoidTheVoid.hasDatabaseConnection())
					saveAllUnsavedIslands();
			}
		}, 5 * 60 * 20, 5 * 60 * 20);
	}
	
	public static void saveIsland(Island is) {
		IslandDB.saveIsland(is);
		for(Player p : is.getOnlinePlayers())
			IslandManager.loadTotalLevel(p);
	}
	
	public static void loadIsland(Integer id, Owner owner, ArrayList<Member> members, Integer x, Integer z, String name, Double experience, Location spawn) {
		Island is = new Island(id, owner, members, x, z, name, experience, spawn);
		addToTakenCoords(is);
		islands.add(is);
	}

	@SuppressWarnings("deprecation")
	public static void removeIsland(Island is) {
		is.delete();
		islands.remove(is);
		IslandDB.removeIsland(is);
		for(Player pl : Bukkit.getOnlinePlayers()) {
			loadTotalLevel(pl);
		}
	}
	
	public static ArrayList<Island> getIslands() {
		return islands;
	}
	
	public static HashMap<String, UUID> getPlayers() {
		HashMap<String, UUID> players = new HashMap<String, UUID>();
		for(Island is : getIslands()) {
			for(Member member : is.getMembers()) {
				if(players.containsKey(Bukkit.getOfflinePlayer(member.getUUID()).getName()))
					continue;
				players.put(Bukkit.getOfflinePlayer(member.getUUID()).getName(), member.getUUID());
			}
			if(!players.containsKey(Bukkit.getOfflinePlayer(is.getOwner().getUUID()).getName()))
				players.put(Bukkit.getOfflinePlayer(is.getOwner().getUUID()).getName(), is.getOwner().getUUID());
		}
		return players;
	}
	
	public static Island getAbsoluteIslandForLocation(Location loc) {
		Island island = null;
		if(!Utils.inIslandWorlds(loc))
			return island;
		for(Island is : IslandManager.getIslands()) {
			if(is.getArea().contains(new Vector(loc.getBlock().getX(), loc.getBlock().getY(), loc.getBlock().getZ()))) {
				island = is;
			}
		}
		return island;
	}
	
	public static Island getIslandById(Integer id) {
		for(Island is : getIslands()) {
			if(is.getId().equals(id))
				return is;
		}
		return null;
	}
	
	public static Integer generateId() {
		Integer i = 0;
		while(getIslandById(i) != null)
			i++;
		return i;
	}
	
	public static Integer currId = -1;
	
	public static Integer generateQuickId() {
		currId++;
		return currId;
	}
	
	public static ArrayList<Island> getIslandsFor(UUID id) {
		ArrayList<Island> playerIslands = new ArrayList<Island>();
		for(Island is : islands) {
			if(is.equals(null))
				continue;
			if(is.getOwner().getUUID().equals(id))
				playerIslands.add(is);
			for(Member member : is.getMembers()) {
				if(member.getUUID().equals(id))
					playerIslands.add(is);
			}
		}
		return playerIslands;
	}
	
	public static ArrayList<Island> getIslandsFor(Player p) {
		UUID id = p.getUniqueId();
		if(id == null) {
			Messenger.message(p, "Could not contact Mojang for your UUID, please try again later.", State.BAD);
			return new ArrayList<Island>();
		}
		/*for(Island is : islands) {
			if(is.getOwner().getUUID().equals(UUIDAPI.getUUID(p.getName())))
				playerIslands.add(is);
			for(Member member : is.getMembers()) {
				if(member.getUUID().equals(UUIDAPI.getUUID(p.getName())))
					playerIslands.add(is);
			}
		}*/
		return getPermittedIslands(id);
	}
	
	public static ArrayList<Island> getInvitedIslands(Player p) {
		ArrayList<Island> playerIslands = new ArrayList<Island>();
		UUID id = p.getUniqueId();
		if(id == null)
			return playerIslands;
		for(Island is : getInvitedIslands(id)) {
			if(is.isInvited(p))
				playerIslands.add(is);
		}
		return playerIslands;
	}
	
	public static ArrayList<Island> getMaxedIslandsFor(Player p) {
		ArrayList<Island> playerIslands = new ArrayList<Island>();
		UUID id = p.getUniqueId();
		if(id == null) {
			Messenger.message(p, "Could not contact Mojang for your UUID, please try again later.", State.BAD);
			return playerIslands;
		}
		for(Island is : getPermittedIslands(id)) {
			if(is.getOwner().getUUID().equals(id) && is.getLevel() == AvoidTheVoid.getMaxIslandLevel()) {
				playerIslands.add(is);
			}
		}
		return playerIslands;
	}
	
	public static ArrayList<Integer> getFreeCoords() {
		ArrayList<Integer> coords = new ArrayList<Integer>();
		
		Integer x = 0;
		Integer z = 0;
		Integer prevPosX = 0;
		Integer prevPosZ = 0;
		Integer prevNegX = 0;
		Integer prevNegZ = 0;
		
		boolean xTurn = true;
		boolean positiveX = true;
		boolean positiveZ = true;
		
		while(true) {
			coords.add(x);
			coords.add(z);
			if(!takenCoords.contains(coords)) {
				return coords;
			} else {
				if(xTurn) {
					if(positiveX) {
						x++;
						if(prevPosX < x) {
							prevPosX = x;
							xTurn = !xTurn;
							positiveX = !positiveX;
						}
					} else {
						x--;
						if(prevNegX > x) {
							prevNegX = x;
							xTurn = !xTurn;
							positiveX = !positiveX;
						}
					}
				} else {
					if(positiveZ) {
						z++;
						if(prevPosZ < z) {
							prevPosZ = z;
							xTurn = !xTurn;
							positiveZ = !positiveZ;
						}
					} else {
						z--;
						if(prevNegZ > z) {
							prevNegZ = z;
							xTurn = !xTurn;
							positiveZ = !positiveZ;
						}
					}
				}
				coords.clear();
			}
		}
		
	}
	
	public static void addToTakenCoords(Island is) {
		ArrayList<Integer> coords = new ArrayList<Integer>();
		coords.add(is.getX());
		coords.add(is.getZ());
		takenCoords.add(coords);
	}
	
	public static void reloadTakenCoords() {
		ArrayList<ArrayList<Integer>> coords = new ArrayList<ArrayList<Integer>>();
		for(Island is : islands) {
			ArrayList<Integer> islandCoords = new ArrayList<Integer>();
			islandCoords.add(is.getX());
			islandCoords.add(is.getZ());
			coords.add(islandCoords);
		}
		takenCoords = coords;
	}
	
	public static boolean isRenaming(Player p) {
		if(toRename.containsKey(p.getName()))
			return true;
		return false;
	}
	
	public static Island getIslandToRename(Player p) {
		if(toRename.containsKey(p.getName()))
			return toRename.get(p.getName());
		return null;
	}
	
	public static void stopRenaming(String name) {
		if(toRename.containsKey(name))
			toRename.remove(name);
	}
	
	public static void startRenaming(Player p, Island is) {
		if(!toRename.containsKey(p.getName()))
			toRename.put(p.getName(), is);
	}
	
	public static boolean isInviting(Player p) {
		if(toInvite.containsKey(p.getName()))
			return true;
		return false;
	}
	
	public static Island getIslandToInviteFrom(Player p) {
		if(toInvite.containsKey(p.getName()))
			return toInvite.get(p.getName());
		return null;
	}
	
	public static void stopInviting(String name) {
		if(toInvite.containsKey(name))
			toInvite.remove(name);
	}
	
	public static void startInviting(Player p, Island is) {
		if(!toInvite.containsKey(p.getName()))
			toInvite.put(p.getName(), is);
	}
	
	public static void loadTotalLevel(Player p) {
		Integer level = 0;
		for(Island is : getIslandsFor(p))
			level = level + is.getLevel();
		totalLevel.put(p.getName(), level);
	}
	
	public static void loadTotalLevel(String username, UUID id) {
		if(totalLevel.containsKey(username))
			return;
		Integer level = 0;
		for(Island is : getIslandsFor(id)) {
			level = level + is.getLevel();
		}
		totalLevel.put(username, level);
	}
	
	public static Integer getTotalLevel(Player p) {
		if(totalLevel.containsKey(p.getName()))
			return totalLevel.get(p.getName());
		return 0;
	}
	
	public static Integer getTotalLevel(String name) {
		if(totalLevel.containsKey(name))
			return totalLevel.get(name);
		return -1;
	}
	
	public static void forget(Player p) {
		if(totalLevel.containsKey(p.getName()))
			totalLevel.remove(p.getName());
	}
	
	public static ArrayList<Island> getTopIslands(int amount) {
		ArrayList<Island> topIslands = new ArrayList<Island>();
		Double highest = 0.0;
		Island tempHighest = null;
		for(int i = 0; i < amount; i++) {
			for(Island is : islands) {
				if(topIslands.contains(is))
					continue;
				if(is.getExperience() > highest) {
					tempHighest = is;
					highest = is.getExperience();
				}
			}
			if(tempHighest != null) {
				topIslands.add(tempHighest);
				highest = 0.0;
			}
		}
		return topIslands;
	}
	
	public static ArrayList<String> getTopPlayers(int amount) {
		ArrayList<String> topPlayers = new ArrayList<String>();
		Integer highest = 0;
		String tempHighest = null;
		for(int i = 0; i < amount; i++) {
			for(Entry<String, UUID> player : getPlayers().entrySet()) {
				if(topPlayers.contains(player.getKey()))
					continue;
				if(getTotalLevel(player.getKey()) == -1)
					loadTotalLevel(player.getKey(), player.getValue());
				Integer level = getTotalLevel(player.getKey());
				if(level > highest) {
					tempHighest = player.getKey();
					highest = level;
				}
			}
			if(tempHighest != null) {
				topPlayers.add(tempHighest);
				highest = 0;
			}
		}
		return topPlayers;
	}
	
	public static ArrayList<Island> getInvitedIslands(UUID id) {
		if(!invitedIslands.containsKey(id))
			return new ArrayList<Island>();
		return invitedIslands.get(id);
	}
	
	public static void addInvitation(UUID id, Island is) {
		ArrayList<Island> invIslands = new ArrayList<Island>();
		if(!invitedIslands.containsKey(id)) {
			invIslands.add(is);
			invitedIslands.put(id, invIslands);
			return;
		}
		invIslands.addAll(invitedIslands.get(id));
		invIslands.add(is);
		invitedIslands.put(id, invIslands);
	}
	
	public static void removeInvitation(UUID id, Island is) {
		if(!invitedIslands.containsKey(id))
			return;
		if(invitedIslands.get(id).contains(is))
			invitedIslands.get(id).remove(is);
	}
	
	public static ArrayList<Island> getPermittedIslands(UUID id) {
		if(!permittedIslands.containsKey(id))
			return new ArrayList<Island>();
		return permittedIslands.get(id);
	}
	
	public static boolean hasPermission(UUID id, Island is) {
		if(!permittedIslands.containsKey(id))
			return false;
		return permittedIslands.get(id).contains(is);
	}
	
	public static void addPermission(UUID id, Island is) {
		ArrayList<Island> permIslands = new ArrayList<Island>();
		if(!permittedIslands.containsKey(id)) {
			permIslands.add(is);
			permittedIslands.put(id, permIslands);
			return;
		}
		permIslands.addAll(permittedIslands.get(id));
		if(!permIslands.contains(is))
			permIslands.add(is);
		permittedIslands.put(id, permIslands);
	}
	
	public static void removePermission(UUID id, Island is) {
		if(!permittedIslands.containsKey(id))
			return;
		if(permittedIslands.get(id).contains(is))
			permittedIslands.get(id).remove(is);
	}

}


class SaveThread extends Thread {
	
	public void run() {
		for(Island is : IslandManager.getIslands()) {
			try {
				is.save();
				System.out.println("Saved "+is.getName());
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println("Saved all islands");
		this.interrupt();
	}
	
}