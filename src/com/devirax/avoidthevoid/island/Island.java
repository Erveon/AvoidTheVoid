package com.devirax.avoidthevoid.island;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.devirax.avoidthevoid.AvoidTheVoid;
import com.devirax.avoidthevoid.island.sub.Member;
import com.devirax.avoidthevoid.island.sub.Owner;
import com.devirax.avoidthevoid.utils.Messenger;
import com.devirax.avoidthevoid.utils.Messenger.State;
import com.devirax.avoidthevoid.utils.TitleManager;
import com.devirax.avoidthevoid.utils.Utils;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.world.DataException;

@SuppressWarnings("deprecation")
public class Island {
	
	Integer id = -1;
	Owner owner;
	ArrayList<Member> members;
	Integer x, z, level;
	String name;
	Location spawn;
	Double experience;
	boolean toSave = false;
	
	ArrayList<String> invited = new ArrayList<String>();
	
	//Loads an already existing island
	public Island(Integer id, Owner owner, ArrayList<Member> members, Integer x, Integer z, String name, Double experience, Location spawn) {
		this.id = id;
		this.owner = owner;
		this.members = new ArrayList<Member>();
		for(Member mem : members) {
			boolean canAdd = true;
			for(Member currMem : this.members) {
				if(currMem.getUUID().equals(mem.getUUID()))
					canAdd = false;
			}
			if(canAdd)
				this.members.add(mem);
		}
		this.x = x;
		this.z = z;
		this.name = name;
		this.experience = experience;
		this.level = getLevelForExperience(experience);
		this.spawn = spawn;
		IslandManager.addPermission(owner.getUUID(), this);
		for(Member mem : members)
			IslandManager.addPermission(mem.getUUID(), this);
	}
	
	//Creates a new island
	public Island(Player p, UUID id) {
		this.id = IslandManager.generateId();
		this.owner = new Owner(id);
		this.members = new ArrayList<Member>();
		ArrayList<Integer> coords = IslandManager.getFreeCoords();
		this.x = coords.get(0);
		this.z = coords.get(1);
		this.level = 1;
		this.experience = 0.0;
		this.name = "Island " + (IslandManager.getIslandsFor(p).size() + 1);
		Vector vectorCreateLoc = new Vector(getAbsoluteX(), 40, getAbsoluteZ());
		generateVoid();
		//TODO custom schematic
		try {
			CuboidClipboard cc = CuboidClipboard.loadSchematic(Utils.getSchematics().get(0));
			EditSession es = new EditSession(new BukkitWorld(Utils.getIslandWorld()), 999999999);
			cc.paste(es, vectorCreateLoc, false);
		} catch (MaxChangedBlocksException | DataException | IOException e) {
			e.printStackTrace();
		}
		generateNether();
		Location createLoc = new Location(Utils.getIslandWorld(), getAbsoluteX(), 40, getAbsoluteZ());
		fillChests();
		p.teleport(createLoc);
		this.spawn = createLoc;
		IslandManager.addPermission(id, this);
		save();
	}
	
	public boolean hasNether() {
		Vector minPos = getArea().getMinimumPoint();
		Vector maxPos = getArea().getMaximumPoint();
		for (int x = minPos.getBlockX(); x <= maxPos.getBlockX(); x++) {
	        for (int y = minPos.getBlockY(); y <= maxPos.getBlockY(); y++) {
	            for (int z = minPos.getBlockZ(); z <= maxPos.getBlockZ(); z++) {
	            	final Block block = Utils.getNetherWorld().getBlockAt(new Location(Utils.getNetherWorld(), x, y, z));
	            	if(block.getType() != Material.AIR) {
	            		return true;
	            	}
	            }
	        }
	    }
		return false;
	}
	
	public void generateNether() {
		Vector vectorCreateLoc = new Vector(getAbsoluteX(), 40, getAbsoluteZ());
		try {
			CuboidClipboard cc = CuboidClipboard.loadSchematic(Utils.getNether().get(0));
			EditSession es = new EditSession(new BukkitWorld(Utils.getNetherWorld()), 999999999);
			cc.paste(es, vectorCreateLoc, false);
		} catch (MaxChangedBlocksException | DataException | IOException e) {
			e.printStackTrace();
		}
	}
	
	public void generateVoid() {
		boolean direction;
		if(getArea().getPos1().getBlockX() < getArea().getPos2().getBlockX())
			direction = true;
		else
			direction = false;
		if(direction) {
			for(int x = getArea().getPos1().getBlockX(); x <= getArea().getPos2().getBlockX(); x++) {
				for(int y = getArea().getPos1().getBlockY(); y <= getArea().getPos2().getBlockY(); y++) {
					for(int z = getArea().getPos1().getBlockZ(); z <= getArea().getPos2().getBlockZ(); z++) {
						Block block = Utils.getIslandWorld().getBlockAt(new Location(Utils.getIslandWorld(), x, y, z));
						if(!block.getType().equals(Material.AIR))
							block.setType(Material.AIR);
						block = Utils.getIslandWorld().getBlockAt(new Location(Utils.getNetherWorld(), x, y, z));
						if(!block.getType().equals(Material.AIR))
							block.setType(Material.AIR);
					}
				}
			}
		} else {
			for(int x = getArea().getPos2().getBlockX(); x <= getArea().getPos1().getBlockX(); x++) {
				for(int y = getArea().getPos2().getBlockY(); y <= getArea().getPos1().getBlockY(); y++) {
					for(int z = getArea().getPos2().getBlockZ(); z <= getArea().getPos1().getBlockZ(); z++) {
						Block block = Utils.getIslandWorld().getBlockAt(new Location(Utils.getIslandWorld(), x, y, z));
						if(!block.getType().equals(Material.AIR))
							block.setType(Material.AIR);
						block = Utils.getIslandWorld().getBlockAt(new Location(Utils.getNetherWorld(), x, y, z));
						if(!block.getType().equals(Material.AIR))
							block.setType(Material.AIR);
					}
				}
			}
		}
		Location loc = new Location(Utils.getIslandWorld(), getArea().getCenter().getBlockX(), getArea().getCenter().getBlockY(), getArea().getCenter().getBlockZ());
		Entity[] entities = Utils.getNearbyEntities(loc, 150);
		for(int i = 0; i < entities.length; i++) {
			entities[i].remove();
		}
	}
	
	public void fillChests() {
		boolean direction;
		if(getArea().getPos1().getBlockX() < getArea().getPos2().getBlockX())
			direction = true;
		else
			direction = false;
		if(direction) {
			for(int x = getArea().getPos1().getBlockX(); x <= getArea().getPos2().getBlockX(); x++) {
				for(int y = getArea().getPos1().getBlockY(); y <= getArea().getPos2().getBlockY(); y++) {
					for(int z = getArea().getPos1().getBlockZ(); z <= getArea().getPos2().getBlockZ(); z++) {
						Block block = Utils.getIslandWorld().getBlockAt(new Location(Utils.getIslandWorld(), x, y, z));
						if(block.getType().equals(Material.CHEST)) {
							Chest chest = (Chest) block.getState();
							Inventory inv = chest.getInventory();
							inv.clear();
							inv.addItem(new ItemStack(Material.LAVA_BUCKET));
							inv.addItem(new ItemStack(Material.WATER_BUCKET));
							inv.addItem(new ItemStack(Material.WATER_BUCKET));
							inv.addItem(new ItemStack(Material.SEEDS));
							inv.addItem(new ItemStack(Material.PUMPKIN_SEEDS));
							inv.addItem(new ItemStack(Material.SAPLING));
							inv.addItem(new ItemStack(Material.RED_MUSHROOM));
							inv.addItem(new ItemStack(Material.BROWN_MUSHROOM));
							inv.addItem(new ItemStack(Material.APPLE, 10));
							inv.addItem(new ItemStack(Material.MELON, 10));
							inv.addItem(new ItemStack(Material.BONE, 3));
							inv.addItem(new ItemStack(Material.TORCH, 5));
						}
					}
				}
			}
		} else {
			for(int x = getArea().getPos2().getBlockX(); x <= getArea().getPos1().getBlockX(); x++) {
				for(int y = getArea().getPos2().getBlockY(); y <= getArea().getPos1().getBlockY(); y++) {
					for(int z = getArea().getPos2().getBlockZ(); z <= getArea().getPos1().getBlockZ(); z++) {
						Block block = Utils.getIslandWorld().getBlockAt(new Location(Utils.getIslandWorld(), x, y, z));
						if(block.getType().equals(Material.CHEST)) {
							Chest chest = (Chest) block.getState();
							Inventory inv = chest.getInventory();
							inv.clear();
							inv.addItem(new ItemStack(Material.LAVA_BUCKET));
							inv.addItem(new ItemStack(Material.WATER_BUCKET));
							inv.addItem(new ItemStack(Material.WATER_BUCKET));
							inv.addItem(new ItemStack(Material.SEEDS));
							inv.addItem(new ItemStack(Material.PUMPKIN_SEEDS));
							inv.addItem(new ItemStack(Material.SAPLING));
							inv.addItem(new ItemStack(Material.RED_MUSHROOM));
							inv.addItem(new ItemStack(Material.BROWN_MUSHROOM));
							inv.addItem(new ItemStack(Material.APPLE, 10));
							inv.addItem(new ItemStack(Material.MELON, 10));
							inv.addItem(new ItemStack(Material.BONE, 3));
							inv.addItem(new ItemStack(Material.TORCH, 5));
						}
					}
				}
			}
		}
	}
	
	public boolean toSave() {
		return toSave;
	}
	
	public void save() {
		toSave = false;
		IslandManager.saveIsland(this);
	}
	
	public void delete() {
		for(Player pl : Bukkit.getOnlinePlayers()) {
			Location loc = pl.getLocation();
			if(getArea().contains(new Vector(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ())) && Utils.inIslandWorlds(pl))
				pl.teleport(Utils.getSpawn());
		}
		generateVoid();
		Messenger.messageIsland(this, "§b"+Bukkit.getOfflinePlayer(getOwner().getUUID()).getName()+"§f's §9"+getName()+"§f has been deleted!", State.INFO);
		IslandManager.removePermission(owner.getUUID(), this);
		for(Member mem : getMembers())
			IslandManager.removePermission(mem.getUUID(), this);
		members.clear();
		owner = null;
	}
	
	public Integer getId() {
		return id;
	}
	
	public void teleport(Player p) {
		p.teleport(getSpawn());
	}
	
	public void setSpawn(Location spawn) {
		this.spawn = spawn;
		save();
	}
	
	public Location getSpawn() {
		return spawn;
	}
	
	public String getSaveSpawn() {
		String spawnString = spawn.getWorld().getName()
				+"&"+spawn.getBlockX()
				+"&"+spawn.getBlockY()
				+"&"+spawn.getBlockZ()
				+"&"+spawn.getYaw()
				+"&"+spawn.getPitch();
		return spawnString;
	}
	
	public void setLevel(Integer level) {
		this.level = level;
		if(getOwner().getPlayer() != null)
			IslandManager.loadTotalLevel(getOwner().getPlayer());
		for(Member mem : getMembers()) {
			if(mem.getPlayer() != null)
				IslandManager.loadTotalLevel(mem.getPlayer());
		}
	}
	
	public Double getExperience() {
		return experience;
	}
	
	public Double getExperienceToLevel() {
		if(getLevel().equals(AvoidTheVoid.getMaxIslandLevel()))
			return -1.0;
		return getExperienceForLevel(getLevel()+1) - getExperience();
	}
	
	public Double getExperienceForLevel(Integer level) {
		if(level == 1) {
			return 0.0;
		} else if(level == 2) {
			return Math.ceil(level * 4 * Math.pow(2, (double)level-1d));
		} else {
			return getExperienceForLevel(level-1) + Math.ceil(level * 4 * Math.pow(2, (double)level-1d));
		}
	}
	
	public Integer getLevelForExperience(Double experience) {
		for(int i = 1; i <= AvoidTheVoid.getMaxIslandLevel(); i++) {
			if(getExperienceForLevel(i) <= experience && getExperienceForLevel(i+1) > experience)
				return i;
		}
		return 0;
	}
	
	public void addExperience(Player p, double experience) {
		if(p.hasPermission("atv.bonusexp.200"))
			experience = experience*4;
		else if(p.hasPermission("atv.bonusexp.300"))
			experience = experience*3;
		else if(p.hasPermission("atv.bonusexp.100"))
			experience = experience*2;
		else if(p.hasPermission("atv.bonusexp.75"))
			experience = experience + (experience*0.75);
		else if(p.hasPermission("atv.bonusexp.50"))
			experience = experience + (experience/2);
		else if(p.hasPermission("atv.bonusexp.25"))
			experience = experience + (experience*0.25);
		else if(p.hasPermission("atv.bonusexp.10"))
			experience = experience + (experience*0.10);
		this.experience = Utils.round(this.experience, 2) + Utils.round(experience, 2);
		checkLevel();
		toSave = true;
	}
	
	public void addExperience(double experience) {
		this.experience = Utils.round(this.experience, 2) + Utils.round(experience, 2);
		checkLevel();
		toSave = true;
	}
	
	public void checkLevel() {
		if(getLevelForExperience(getExperience()) > getLevel() && !getLevel().equals(50)) {
			levelUp();
		}
	}
	
	public void levelUp() {
		setLevel(level + 1);
		Messenger.messageIsland(this, "§9"+getName()+"§f has reached level §4"+getLevel()+"§f, congratulations! ", State.GOOD);
		for(Player pl : getOnlinePlayers()) {
			Utils.shootFireworks(pl);
			TitleManager.sendTimings(pl, 20, 40, 20);
			TitleManager.sendTitle(pl, "{\"text\":\"\",\"extra\":[{\"text\":\"Level up!\",\"color\":\"green\"}]}");
		}
	}
	
	public Integer getX() {
		return x;
	}
	
	public Integer getZ() {
		return z;
	}
	
	public Integer getAbsoluteX() {
		return x * 100 + 48;
	}
	
	public Integer getAbsoluteZ() {
		return z * 100 + 48;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
		save();
	}
	
	public Member addMember(Player p) {
		if(p.getUniqueId() == null) {
			Messenger.message(p, "Could not contact Mojang for your UUID, please try again later.", State.BAD);
			return null;
		}
		UUID id = p.getUniqueId();
		Member member = new Member(id);
		if(members.contains(member))
			return member;
		IslandManager.addPermission(id, this);
		members.add(member);
		save();
		IslandManager.loadTotalLevel(p);
		return member;
	}
	
	public void addMember(String name, UUID uuid) {
		Member member = new Member(uuid);
		if(members.contains(member))
			return;
		IslandManager.addPermission(uuid, this);
		members.add(member);
		save();
	}
	
	public void removeMember(String name, boolean kicked) {
		Member member = null;
		for(Member mem : getMembers()) {
			if((mem.getUUID().equals(Bukkit.getOfflinePlayer(name).getUniqueId()))) {
				if(member != null) {
					if(Bukkit.getPlayer(name) != null)
						Bukkit.getPlayer(name).sendMessage("§4Woops, couldn't leave the island. We're working on it.");
					return;
				}
				member = mem;
			}
		}
		if(member != null) {
			IslandManager.removePermission(member.getUUID(), this);
			members.remove(member);
			if(member.getPlayer() != null) {
				IslandManager.loadTotalLevel(member.getPlayer());
				Location loc = member.getPlayer().getLocation();
				if(getArea().contains(new Vector(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ())) && Utils.inIslandWorlds(member.getPlayer()))
					member.getPlayer().teleport(Utils.getSpawn());
				if(kicked)
					Messenger.message(member.getPlayer(), "You have been removed from §b"+getOwner().getUsername()+"§f's §9"+this.getName(), State.INFO);
				else
					Messenger.message(member.getPlayer(), "You have left §b"+getOwner().getUsername()+"§f's §9"+this.getName(), State.INFO);
			}
			save();
			if(kicked)
				Messenger.messageIsland(this, "§b"+member.getUsername()+"§f has been removed from §9"+this.getName(), State.INFO);
			else
				Messenger.messageIsland(this, "§b"+member.getUsername()+"§f has left §9"+this.getName(), State.INFO);
		}
	}
	
	public void promoteToOwner(String name) {
		Member member = null;
		for(Member mem : getMembers()) {
			if(mem.getUsername().equalsIgnoreCase(name))
				member = mem;
		}
		if(member != null) {
			IslandManager.removePermission(getOwner().getUUID(), this);
			addMember(getOwner().getUsername(), getOwner().getUUID());
			owner = new Owner(member.getUUID());
			save();
			Messenger.messageIsland(this, "§b"+getOwner().getUsername()+"§f has been assigned ownership of §9"+this.getName(), State.INFO);
		}
	}
	
	public CuboidRegion getArea() {
		return new CuboidRegion(new Vector(getAbsoluteX() - 46, 0, getAbsoluteZ() - 46) , new Vector(getAbsoluteX() + 50, 256, getAbsoluteZ() + 50));
	}
	
	public CuboidRegion getAreaForLevel() {
		return new CuboidRegion(new Vector(getAbsoluteX() - 23 - (level * 5), 0, getAbsoluteZ() - 23 - (level * 5)) , new Vector(getAbsoluteX() + 23 + (level * 5), 256, getAbsoluteZ() + 23 + (level * 5)));
	}
	
	public Integer getLevel() {
		return level;
	}

	public Owner getOwner() {
		return owner;
	}
	
	public ArrayList<Member> getMembers() {
		return members;
	}
	
	public ArrayList<String> getMemberUsers() {
		ArrayList<String> users = new ArrayList<String>();
		for(Member mem : getMembers()) {
			users.add(mem.getUsername());
		}
		return users;
	}
	
	public String getMembersSave() {
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for(Member member : getMembers()) {
			if(!first) {
				sb.append("#"+member.getUUID());
			} else {
				sb.append(""+member.getUUID());
				first = false;
			}
		}
		String save = sb.toString();
		if(save.length() == 0) {
			return " ";
		}
		return sb.toString();
	}
	
	public boolean isInvited(Player p) {
		if(invited.contains(p.getName()))
			return true;
		return false;
	}
	
	public ArrayList<Player> getOnlinePlayers() {
		ArrayList<Player> pl = new ArrayList<Player>();
		for(Member member : getMembers()) {
			UUID id = member.getUUID();
			if(id == null)
				continue;
			Player p = Bukkit.getPlayer(id);
			if(p != null)
				pl.add(p);
		}
		Player owner = Bukkit.getPlayer(getOwner().getUUID());
		if(owner != null)
			pl.add(owner);
		return pl;
	}
	
	public void acceptInvite(Player p) {
		if(invited.contains(p.getName())) {
			UUID id = p.getUniqueId();
			if(id != null)
				IslandManager.removeInvitation(id, this);
			invited.remove(p.getName());
			if(getMemberUsers().contains(p.getName()))
				return;
			addMember(p);
			Messenger.messageIsland(this, "§b"+p.getName()+"§f has joined §9"+this.getName()+"§f!", State.GOOD);
			p.closeInventory();
			this.teleport(p);
		}
	}
	
	public void declineInvite(Player p) {
		if(invited.contains(p.getName())) {
			UUID id = p.getUniqueId();
			if(id != null)
				IslandManager.removeInvitation(id, this);
			invited.remove(p.getName());
			Messenger.messageIsland(this, "§b"+p.getName()+"§f has decline their invite for §9"+this.getName()+"§f.", State.BAD);
			Messenger.message(p, "You have declined your invite to §9"+this.getName()+"§f.", State.INFO);
		}
	}

	public void rename(Player p) {
		IslandManager.startRenaming(p, this);
		Messenger.message(p, "Type the name you would like to give your island", State.INFO);
	}
	
	public void initiateInvite(Player p) {
		IslandManager.startInviting(p, this);
		Messenger.message(p, "Type the name of the person you would like to invite to the island", State.INFO);
	}

	public void invite(Player p) {
		UUID id = p.getUniqueId();
		if(id != null)
			IslandManager.addInvitation(id, this);
		invited.add(p.getName());
		Messenger.messageIsland(this, "§b"+p.getName()+" §fhas been invited to §9"+this.getName()+"", State.INFO);
		Messenger.message(p, "You've been invited to §9"+this.getName()+", §b" + getOwner().getUsername()+"§r's island", State.INFO);
		runInviteTimer(p.getName());
	}
	
	public void runInviteTimer(final String name) {
		final Island island = this;
		Bukkit.getScheduler().runTaskLater(AvoidTheVoid.getPlugin(), new Runnable() {
			@Override
			public void run() {
				UUID id = Bukkit.getOfflinePlayer(name).getUniqueId();
				if(id != null)
					IslandManager.removeInvitation(id, island);
				invited.remove(name);
			}
		}, 5 * 60 * 20);
	}

	public void expel(Player p) {
		if(getOnlinePlayers().contains(p)) {
			for(Player pl : Bukkit.getOnlinePlayers()) {
				if(Utils.inIslandWorlds(pl)) {
					if(getArea().contains(new Vector(pl.getLocation().getBlockX(), pl.getLocation().getBlockY(), pl.getLocation().getBlockZ()))) {
						if(!getOnlinePlayers().contains(pl)) {
							pl.teleport(Utils.getSpawn());
							Messenger.message(pl, "You have been expelled from the island", State.INFO);
						}
					}
				}
			}
			Messenger.message(p, "You have expelled all outsiders from the island", State.GOOD);
		} else {
			Messenger.message(p, "You cannot expel people from an island that isn't yours", State.BAD);
		}
	}
	
	public void expel(Player p, String toExpel) {
		if(getOnlinePlayers().contains(p)) {
			Player other = Bukkit.getPlayer(toExpel);
			if(other == null) {
				Messenger.message(p, "The player §b" + toExpel +" §fdoes not exist or is not online.", State.BAD);
			} else {
				if(Utils.inIslandWorlds(other)) {
					if(getArea().contains(new Vector(other.getLocation().getBlockX(), other.getLocation().getBlockY(), other.getLocation().getBlockZ()))) {
						if(!getOnlinePlayers().contains(other)) {
							other.teleport(Utils.getSpawn());
							Messenger.message(other, "You have been expelled from the island", State.INFO);
						} else {
							Messenger.message(p, "You cannot expel people from an island they're added to", State.BAD);
						}
					} else {
						Messenger.message(p, "That player is not on this island", State.BAD);
					}
				}
			}
			Messenger.message(p, "You have expelled all outsiders from the island", State.GOOD);
		} else {
			Messenger.message(p, "You cannot expel people from an island that isn't yours", State.BAD);
		}
	}

}