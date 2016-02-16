package com.devirax.avoidthevoid.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
import com.devirax.avoidthevoid.utils.Utils;

public class Invite implements Listener {
	
	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.LOWEST)
	public void onChat(AsyncPlayerChatEvent e) {
		Player p = e.getPlayer();
		if(IslandManager.isInviting(p)) {
			e.setMessage(ChatColor.stripColor(e.getMessage()));
			Island is = IslandManager.getIslandToInviteFrom(p);
			if(is != null) {
				e.setCancelled(true);
				if(!isValid(p, e.getMessage())) {
					Messenger.message(p, "You cannot use special characters", State.BAD);
					Messenger.message(p, "If you're sure it's valid, go to your island and use the command /atv invite (username)", State.BAD);
					IslandManager.stopInviting(p.getName());
					return;
				}
				for(Player pl : Bukkit.getOnlinePlayers()) {
					if(pl.getName().equalsIgnoreCase(e.getMessage())) {
						if(is.getOnlinePlayers().contains(pl)) {
							Messenger.message(p, "§b"+pl.getName()+"§f is already on §9"+is.getName(), State.BAD);
							IslandManager.stopInviting(p.getName());
							return;
						} else if(Utils.getMaxIslandsFor(pl) <= IslandManager.getIslandsFor(pl).size()) {
							Messenger.message(p, "§b"+pl.getName()+"§f has reached their maximum amount of islands", State.BAD);
							IslandManager.stopInviting(p.getName());
							return;
						}
						is.invite(pl);
						IslandManager.stopInviting(p.getName());
						return;
					}
				}
				Messenger.message(p, "§b"+e.getMessage()+" §fis not online or does not exist", State.BAD);
				IslandManager.stopInviting(p.getName());
				return;
			}
		}
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		if(IslandManager.isRenaming(p)) {
			IslandManager.stopRenaming(p.getName());
		}
	}
	
	public boolean isValid(Player p, String s) {
		for(char c : s.toCharArray()) {
			if(!Character.isLetterOrDigit(c) && !isNumber(c) &&!String.valueOf(c).equals("_")) {
				p.sendMessage("§4"+c+" is not a valid character");
				return false;
			}
		}
		return true;
	}
	
	public boolean isNumber(char c) {
		try {
			Integer.parseInt(""+c);
			return true;
		} catch(Exception ignored) {
			return false;
		}
	}

}
