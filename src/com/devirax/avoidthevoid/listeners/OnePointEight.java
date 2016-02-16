package com.devirax.avoidthevoid.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.devirax.avoidthevoid.AvoidTheVoid;
import com.devirax.avoidthevoid.utils.Utils;

public class OnePointEight implements Listener {
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		final Player p = e.getPlayer();
		if(!Utils.isPlayerRightVersion(p)) {
			Bukkit.getScheduler().scheduleSyncDelayedTask(AvoidTheVoid.getPlugin(), new Runnable() {
				@Override
				public void run() {
					p.sendMessage(ChatColor.DARK_PURPLE+"|********************************************************|");
					p.sendMessage(ChatColor.DARK_PURPLE+"|********************************************************|");
					p.sendMessage(ChatColor.DARK_PURPLE+"|**"+ChatColor.DARK_RED+"This server is best experienced on MC Version: 1.8"+ChatColor.DARK_PURPLE+"**|");
					p.sendMessage(ChatColor.DARK_PURPLE+"|********************************************************|");
					p.sendMessage(ChatColor.DARK_PURPLE+"|********************************************************|");
				}
			}, 10);
		}
	}

}
