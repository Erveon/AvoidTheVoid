package com.devirax.avoidthevoid.GUI;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import com.devirax.avoidthevoid.island.Island;
import com.devirax.avoidthevoid.island.sub.Member;
import com.devirax.avoidthevoid.utils.Messenger;
import com.devirax.avoidthevoid.utils.Utils;
import com.devirax.avoidthevoid.utils.Messenger.State;

public class MemberGUI implements Listener {
	
	public static void openGUI(Player p, Island is) {
		
		Inventory gui = Bukkit.createInventory((InventoryHolder) p, 9, "Members - "+is.getName());
		
		gui.addItem(getAddMember());
		gui.addItem(getKickMember());
		gui.addItem(promoteMember());
		
		gui.setItem(8, getBack());
		
		p.openInventory(gui);
		
	}
	
	public static void OpenOptionGUI(Player p, Island is, boolean kick) {
		Inventory gui;
		
		if(kick) {
			if(is.getMembers().isEmpty()) {
				Messenger.message(p, "You don't have any members to remove", State.BAD);
				return;
			}
			gui = Bukkit.createInventory((InventoryHolder) p, Utils.getSlotCount(is.getMembers().size()), "Kick member - "+is.getName());
		} else {
			if(is.getMembers().isEmpty()) {
				Messenger.message(p, "You don't have any members to promote", State.BAD);
				return;
			}
			gui = Bukkit.createInventory((InventoryHolder) p, Utils.getSlotCount(is.getMembers().size()), "Give ownership - "+is.getName());
		}

		for(Member member : is.getMembers()) {
			gui.addItem(Utils.getHead(member.getUsername()));
		}
		
		p.openInventory(gui);
	}
	
	@EventHandler
	public void onClick(InventoryClickEvent e) {
		Player p = (Player) e.getWhoClicked();
		ItemStack is = e.getCurrentItem();
		if(is == null)
			return;
		if(e.getInventory().getName().startsWith("Members - ")) {
			Island isl = Utils.getSelectedIsland(p);
			if(isl == null) {
				p.closeInventory();
				return;
			}
			e.setCancelled(true);
			if(is.equals(getAddMember())) {
				if(isl.getMembers().size() >= Utils.getMaxMembers(isl)) {
					Messenger.message(p, "You have reached the maxmimum amount of members, higher donator ranks get more members", State.BAD);
					return;
				}
				p.closeInventory();
				isl.initiateInvite(p);
				return;
			} else if(is.equals(getKickMember())) {
				OpenOptionGUI(p, isl, true);
				return;
			} else if(is.equals(promoteMember())) {
				OpenOptionGUI(p, isl, false);
				return;
			} else if(is.equals(getBack())) {
				SettingsGUI.openGUIForIsland(p, isl);
				return;
			}
		} else if(e.getInventory().getName().startsWith("Kick member - ")) {
			Island isl = Utils.getSelectedIsland(p);
			if(isl == null) {
				p.closeInventory();
				return;
			}
			e.setCancelled(true);
			if(is.hasItemMeta()) {
				ItemMeta im = is.getItemMeta();
				if(im instanceof SkullMeta) {
					SkullMeta sm = (SkullMeta) im;
					isl.removeMember(sm.getOwner(), true);
					OpenOptionGUI(p, isl, true);
				}
			}
		} else if(e.getInventory().getName().startsWith("Give ownership - ")) {
			Island isl = Utils.getSelectedIsland(p);
			if(isl == null) {
				p.closeInventory();
				return;
			}
			e.setCancelled(true);
			if(is.hasItemMeta()) {
				ItemMeta im = is.getItemMeta();
				if(im instanceof SkullMeta) {
					SkullMeta sm = (SkullMeta) im;
					isl.promoteToOwner(sm.getOwner());
					p.closeInventory();
				}
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	public static ItemStack getAddMember() {
		ItemStack is = new ItemStack(Material.getMaterial(342));
		ItemMeta im = is.getItemMeta();
		
		im.setDisplayName("§aAdd Member");
		is.setItemMeta(im);
		
		return is;
	}
	
	@SuppressWarnings("deprecation")
	public static ItemStack getKickMember() {
		ItemStack is = new ItemStack(Material.getMaterial(343));
		ItemMeta im = is.getItemMeta();
		
		im.setDisplayName("§4Remove Member");
		is.setItemMeta(im);
		
		return is;
	}
	
	public static ItemStack promoteMember() {
		ItemStack is = new ItemStack(Material.DIAMOND);
		ItemMeta im = is.getItemMeta();
		
		im.setDisplayName("§aGive Ownership");
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
	

}
