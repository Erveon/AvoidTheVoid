package com.devirax.avoidthevoid.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.devirax.avoidthevoid.island.Island;
import com.devirax.avoidthevoid.island.IslandManager;
import com.devirax.avoidthevoid.utils.Messenger;
import com.devirax.avoidthevoid.utils.Messenger.State;

public class Rename implements Listener {
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onChat(AsyncPlayerChatEvent e) {
		Player p = e.getPlayer();
		if(IslandManager.isRenaming(p)) {
			Island is = IslandManager.getIslandToRename(p);
			if(is != null) {
				e.setCancelled(true);
				if(e.getMessage().length() > 16) {
					Messenger.message(p, "The name you entered is too long (16 characters), please try again", State.BAD);
					return;
				} else {
					if(!isValid(e.getMessage())) {
						Messenger.message(p, "You cannot use special characters, please try again", State.BAD);
						return;
					}
					for(Island island : IslandManager.getIslandsFor(p)) {
						if(island.getName().equals(e.getMessage())) {
							Messenger.message(p, "You already have an island with that name, please try again", State.BAD);
							return;
						}
					}
					is.setName(e.getMessage());
					Messenger.message(p, "You have successfully renamed your island to '§9"+is.getName()+"§r'", State.GOOD);
				}
			}
			IslandManager.stopRenaming(p.getName());
		}
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		if(IslandManager.isRenaming(p)) {
			IslandManager.stopRenaming(p.getName());
		}
	}
	
	public boolean isValid(String s) {
		for(char c : s.toCharArray()) {
			if(!Character.isLetterOrDigit(c) && !String.valueOf(c).equals("-")) {
				return false;
			}
		}
		return true;
	}

}
