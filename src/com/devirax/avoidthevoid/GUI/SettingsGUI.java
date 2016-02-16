package com.devirax.avoidthevoid.GUI;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.devirax.avoidthevoid.AvoidTheVoid;
import com.devirax.avoidthevoid.island.Island;
import com.devirax.avoidthevoid.island.IslandManager;
import com.devirax.avoidthevoid.utils.Messenger;
import com.devirax.avoidthevoid.utils.Messenger.State;
import com.devirax.avoidthevoid.utils.Utils;
import com.sk89q.worldedit.Vector;

public class SettingsGUI implements Listener {
	
	public static HashMap<String, ArrayList<Integer>> tempIslands = new HashMap<String, ArrayList<Integer>>();
	
	public static void openGUI(Player p)  {
		
		ArrayList<Island> islands = IslandManager.getIslandsFor(p);
		ArrayList<Island> invitedIslands = IslandManager.getInvitedIslands(p);
		
		Integer islandCount = islands.size() + invitedIslands.size();
		
		Inventory gui = Bukkit.createInventory((InventoryHolder) p, Utils.getSlotCount(islandCount), "§2Islands");
		ArrayList<Integer> islandTemp = new ArrayList<Integer>();
		Integer slot = 0;
		
		for(Island is : islands) {
			if(slot < 54) {
				gui.setItem(slot, getIslandInfo(p, is));
				slot++;
				islandTemp.add(is.getId());
			}
		}
		
		for(Island is : invitedIslands) {
			if(slot < 54) {
				gui.setItem(slot, getInvitedIslandInfo(p, is));
				slot++;
				islandTemp.add(is.getId());
			}
		}
		
		tempIslands.put(p.getName(), islandTemp);
		if(slot < 54)
			gui.addItem(getCreateIsland());
		
		p.openInventory(gui);
		
	}
	
	public static void openGUIForIsland(Player p, Island is) {
		Inventory gui = Bukkit.createInventory((InventoryHolder) p, 9, "§2Settings - §9"+is.getName());
		
		gui.addItem(getTeleportToIsland());
		
		boolean owner = is.getOwner().getUsername().equalsIgnoreCase(p.getName());
		
		if(owner) {
			gui.addItem(getRename());
			gui.addItem(getSetHome());
			gui.addItem(getMembers());
		}
		
		gui.addItem(getExpel());
		
		if(owner) {
			gui.addItem(getRemoveIsland());
		} else {
			gui.addItem(getLeaveIsland());
		}
		
		gui.setItem(8, getBack());
		
		p.openInventory(gui);
	}
	
	public static void openConfirmDeletion(Player p, Island is) {
		Inventory gui = Bukkit.createInventory((InventoryHolder) p, 9, "§4DELETE §9"+is.getName().toUpperCase()+"§4?");
		
		gui.addItem(getConfirmDeletion());
		
		gui.setItem(8, getBack());
		
		p.openInventory(gui);
	}
	
	public static void openConfirmLeave(Player p, Island is) {
		Inventory gui = Bukkit.createInventory((InventoryHolder) p, 9, "§4LEAVE §9"+is.getName().toUpperCase()+"§4?");
		
		gui.addItem(getConfirmLeave());
		
		gui.setItem(8, getBack());
		
		p.openInventory(gui);
	}

	@EventHandler
	public void onClick(InventoryClickEvent e) {
		Player p = (Player) e.getWhoClicked();
		ItemStack is = e.getCurrentItem();
		if(is == null)
			return;
		if(e.getInventory().getName().equalsIgnoreCase("§2Islands")) {
			e.setCancelled(true);
			if(is.equals(getCreateIsland())) {
				AvoidTheVoid.getPlugin().getServer().dispatchCommand((CommandSender) p, "atv create") ;
			} else {
				if(!is.hasItemMeta())
					return;
				if(!is.getItemMeta().hasDisplayName())
					return;
				if(is.getType().equals(Material.GRASS)) {
					if(!tempIslands.containsKey(p.getName())) {
						p.closeInventory();
						return;
					}
					Island island = IslandManager.getIslandById(tempIslands.get(p.getName()).get(e.getSlot()));
					if(island == null) {
						p.closeInventory();
						Messenger.message(p, "Oops, something went wrong. Try again!", State.BAD);
						return;
					}
					Utils.setSelectedIsland(p, island);
					openGUIForIsland(p, island);
					return;
				} else if(is.getType().equals(Material.DIRT)) {
					if(e.getClick().equals(ClickType.LEFT)) {
						Island island = IslandManager.getIslandById(tempIslands.get(p.getName()).get(e.getSlot()));
						island.acceptInvite(p);
						openGUI(p);
						return;
					} else if(e.getClick().equals(ClickType.RIGHT)) {
						Island island = IslandManager.getIslandById(tempIslands.get(p.getName()).get(e.getSlot()));
						island.declineInvite(p);
						openGUI(p);
						return;
					}
				}
			}
		} else if(e.getInventory().getName().startsWith("§2Settings")) {
			e.setCancelled(true);
			Island isl = Utils.getSelectedIsland(p);
			if(isl == null) {
				p.closeInventory();
				return;
			}
			if(is.equals(getTeleportToIsland())) {
				isl.teleport(p);
				return;
			} else if(is.equals(getRename())) {
				isl.rename(p);
				p.closeInventory();
				return;
			} else if(is.equals(getSetHome())) {
				if(isl.getArea().contains(new Vector(p.getLocation().getBlockX(), p.getLocation().getBlockY(), p.getLocation().getBlockZ()))) {
					isl.setSpawn(p.getLocation());
					Messenger.message(p, "The spawn has successfully been set", State.GOOD);
					p.closeInventory();
				} else {
					Messenger.message(p, "You cannot set the island's spawn outside of its perimeter", State.BAD);
					p.closeInventory();
				}
				return;
			} else if(is.equals(getMembers())) {
				MemberGUI.openGUI(p, isl);
				return;
			}  else if(is.equals(getExpel())) {
				AvoidTheVoid.getPlugin().getServer().dispatchCommand(p, "atv expel "+isl.getId());
				return;
			} else if(is.equals(getBack())) {
				openGUI(p);
				return;
			}  else if(is.equals(getRemoveIsland())) {
				openConfirmDeletion(p, isl);
				return;
			}  else if(is.equals(getLeaveIsland())) {
				openConfirmLeave(p, isl);
				return;
			}
		} else if(e.getInventory().getName().startsWith("§4DELETE")) {
			e.setCancelled(true);
			Island isl = Utils.getSelectedIsland(p);
			if(isl == null) {
				p.closeInventory();
				return;
			}
			if(is.equals(getConfirmDeletion())) {
				IslandManager.removeIsland(isl);
				return;
			} else if(is.equals(getBack())) {
				openGUIForIsland(p, isl);
				return;
			} 
		} else if(e.getInventory().getName().startsWith("§4LEAVE")) {
			e.setCancelled(true);
			Island isl = Utils.getSelectedIsland(p);
			if(isl == null) {
				p.closeInventory();
				return;
			}
			if(is.equals(getConfirmLeave())) {
				isl.removeMember(p.getName(), false);
				p.closeInventory();
				return;
			} else if(is.equals(getBack())) {
				openGUIForIsland(p, isl);
				return;
			} 
		}
	}
	
	public static ItemStack getConfirmLeave() {
		ItemStack is = new ItemStack(Material.FIRE);
		ItemMeta im = is.getItemMeta();
		
		im.setDisplayName("§4LEAVE ISLAND");
		im.setLore(Arrays.asList("", "§4THERE IS NO WAY BACK", "§4CLICK TO CONFIRM"));
		is.setItemMeta(im);
		
		return is;
	}
	
	public static ItemStack getLeaveIsland() {
		ItemStack is = new ItemStack(Material.FIRE);
		ItemMeta im = is.getItemMeta();
		
		im.setDisplayName("§4Leave island");
		is.setItemMeta(im);
		
		return is;
	}
	
	public static ItemStack getConfirmDeletion() {
		ItemStack is = new ItemStack(Material.FIRE);
		ItemMeta im = is.getItemMeta();
		
		im.setDisplayName("§4REMOVE ISLAND");
		im.setLore(Arrays.asList("", "§4THERE IS NO WAY BACK", "§4CLICK TO CONFIRM DELETION"));
		is.setItemMeta(im);
		
		return is;
	}
	
	public static ItemStack getRemoveIsland() {
		ItemStack is = new ItemStack(Material.FIRE);
		ItemMeta im = is.getItemMeta();
		
		im.setDisplayName("§4Remove island");
		is.setItemMeta(im);
		
		return is;
	}
	
	public static ItemStack getBack() {
		ItemStack is = new ItemStack(Material.NETHER_STAR);
		ItemMeta im = is.getItemMeta();
		
		im.setDisplayName("§e§l← Back");
		is.setItemMeta(im);
		
		return is;
	}
	
	public static ItemStack getMembers() {
		ItemStack is = new ItemStack(Material.SKULL_ITEM, 1, (byte) 3);
		ItemMeta im = is.getItemMeta();
		
		im.setDisplayName("§aMembers");
		is.setItemMeta(im);
		
		return is;
	}
	
	public static ItemStack getTeleportToIsland() {
		ItemStack is = new ItemStack(Material.ENDER_PEARL);
		ItemMeta im = is.getItemMeta();
		
		im.setDisplayName("§aTeleport");
		is.setItemMeta(im);
		
		return is;
	}
	
	public static ItemStack getSetHome() {
		ItemStack is = new ItemStack(Material.WOOD_DOOR);
		ItemMeta im = is.getItemMeta();
		
		im.setDisplayName("§aSet Home");
		is.setItemMeta(im);
		
		return is;
	}

	public static ItemStack getRename() {
		ItemStack is = new ItemStack(Material.BOOK_AND_QUILL);
		ItemMeta im = is.getItemMeta();
		
		im.setDisplayName("§aRename Island");
		is.setItemMeta(im);
		
		return is;
	}
	
	public static ItemStack getCreateIsland() {
		ItemStack is = new ItemStack(Material.BOOK_AND_QUILL);
		ItemMeta im = is.getItemMeta();
		
		im.setDisplayName("§aCreate a new island");
		is.setItemMeta(im);
		
		return is;
	}
	
	public static ItemStack getExpel() {
		ItemStack is = new ItemStack(Material.COAL);
		ItemMeta im = is.getItemMeta();
		
		im.setDisplayName("§aExpel Outsiders");
		is.setItemMeta(im);
		
		return is;
	}
	
	public static ItemStack getIslandInfo(Player p, Island island) {
		ItemStack is = new ItemStack(Material.GRASS, island.getLevel());
		ItemMeta im = is.getItemMeta();
		
		im.setDisplayName("§a"+island.getName());
		
		String rank = "Member";
		if(island.getOwner().getUsername().equalsIgnoreCase(p.getName())) {
			rank = "Owner";
		}
		
		Double experienceDouble = island.getExperienceToLevel();
		Integer experienceInt = (int) Math.ceil(island.getExperienceToLevel());
		String experience;
		if(experienceDouble.equals(-1.0)) {
			experience = "Max level";
		} else if ((experienceDouble == Math.floor(experienceDouble)) && !Double.isInfinite(experienceDouble)) {
		    experience = experienceInt.toString();
		} else {
			experience = String.format("%.1f", experienceDouble);
		}
		
		Integer population = 1 + island.getMembers().size();
		if(rank.equalsIgnoreCase("owner")) {
			im.setLore(Arrays.asList(" ", "§2Rank: §7"+rank, "§2Level: §7"+island.getLevel(), "§2Exp to level: §7"+experience, "§2Population: §7"+population, " ", "§aClick for settings."));
		} else {
			im.setLore(Arrays.asList(" ", "§2Owner: §7"+island.getOwner().getUsername(), "§2Rank: §7"+rank, "§2Level: §7"+island.getLevel(), "§2Exp to level: §7"+experience, "§2Population: §7"+population, " ", "§aClick for settings."));
		}
		
		is.setItemMeta(im);
		return is;
	}
	
	public static ItemStack getInvitedIslandInfo(Player p, Island island) {
		ItemStack is = new ItemStack(Material.DIRT, island.getLevel());
		ItemMeta im = is.getItemMeta();
		
		im.setDisplayName("§a"+island.getName());
		
		String rank = "Invited";
		if(island.getOwner().getUsername().equalsIgnoreCase(p.getName())) {
			rank = "Owner";
		}
		
		Double experienceDouble = island.getExperienceToLevel();
		Integer experienceInt = (int) Math.ceil(island.getExperienceToLevel());
		String experience;
		if(experienceDouble.equals(-1.0)) {
			experience = "Max level";
		} else if ((experienceDouble == Math.floor(experienceDouble)) && !Double.isInfinite(experienceDouble)) {
		    experience = experienceInt.toString();
		} else {
			experience = String.format("%.1f", experienceDouble);
		}
		
		Integer population = 1 + island.getMembers().size();
		if(rank.equalsIgnoreCase("owner")) {
			im.setLore(Arrays.asList(" ", "§2Rank: §7"+rank, "§2Level: §7"+island.getLevel(), "§2Exp to level: §7"+ experience, "§2Population: §7"+population, " ", "§aLeft click to accept.", "§cRight click to decline."));
		} else {
			im.setLore(Arrays.asList(" ", "§2Owner: §7"+island.getOwner().getUsername(), "§2Rank: §7"+rank, "§2Level: §7"+island.getLevel(), "§2Exp to level: §7"+experience, "§2Population: §7"+population, " ", "§aLeft click to accept.", "§cRight click to decline."));
		}
		
		is.setItemMeta(im);
		return is;
	}
	
}
