package com.devirax.avoidthevoid.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import com.devirax.avoidthevoid.AvoidTheVoid;
import com.devirax.avoidthevoid.island.Island;
import com.devirax.avoidthevoid.island.IslandManager;
import com.devirax.avoidthevoid.utils.Messenger;
import com.devirax.avoidthevoid.utils.Messenger.State;
import com.devirax.avoidthevoid.utils.Utils;

public class Loaders implements Listener {

	@EventHandler
	public void onLogin(final PlayerJoinEvent e) {
		new BukkitRunnable() {

			@Override
			public void run() {
				Player p = e.getPlayer();
				if(!AvoidTheVoid.hasDatabaseConnection()) {
					Messenger.message(p, "AVOID THE VOID IS IN SAFEMODE", State.BAD);
					p.teleport(Utils.getSpawn());
					return;
				}
				if(e.getPlayer().getLocation().getWorld().equals(Bukkit.getWorld("Asteroid"))) {
					if(!e.getPlayer().getActivePotionEffects().contains(PotionEffectType.JUMP))
						e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 5));
				}
				IslandManager.loadTotalLevel(p);
				if(IslandManager.getIslandsFor(p).size() > 0) {
					for(final Island is : IslandManager.getIslandsFor(p)) {
						new BukkitRunnable() {

							@Override
							public void run() {
								if(!is.hasNether()) {
									is.generateNether();
									System.out.println("Generating nether for islands #"+is.getId());
								}
							}
							
						}.runTask(AvoidTheVoid.getPlugin());
					}
				}
				e.setJoinMessage("");
			}
			
		}.runTaskAsynchronously(AvoidTheVoid.getPlugin());
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		IslandManager.forget(e.getPlayer());
		e.setQuitMessage("");
	}
	
}
