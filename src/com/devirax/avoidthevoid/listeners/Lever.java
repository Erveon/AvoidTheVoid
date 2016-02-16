package com.devirax.avoidthevoid.listeners;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import com.devirax.avoidthevoid.AvoidTheVoid;
import com.devirax.avoidthevoid.acts.AsteroidAct;
import com.devirax.avoidthevoid.utils.Messenger;
import com.devirax.avoidthevoid.utils.TitleManager;
import com.devirax.avoidthevoid.utils.Messenger.State;
import com.devirax.avoidthevoid.utils.Utils;

public class Lever implements Listener {
	
	public enum Destination { MOON, HOME };
	public static HashMap<String, Destination> settingLever = new HashMap<String, Destination>();
	public static HashMap<String, HashMap<Destination, Integer>> toLaunch = new HashMap<String, HashMap<Destination, Integer>>();
	
	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		if(e.getClickedBlock() == null)
			return;
		if(!e.getClickedBlock().getType().equals(Material.LEVER))
			return;
		for(Destination dest : Destination.values()) {
			if(isSettingLever(p, dest)) {
				AvoidTheVoid.getPlugin().getConfig().set(dest.toString().toLowerCase()+"lever", Utils.getStringForLoc(e.getClickedBlock().getLocation()));
				AvoidTheVoid.getPlugin().saveConfig();
				AvoidTheVoid.getPlugin().reloadConfig();
				stopSettingLever(p);
				Messenger.message(p, "Successfully set the "+dest.toString().toLowerCase()+" lever", State.GOOD);
				return;
			}
			if(Utils.getLever(dest) == null)
				continue;
			if(Utils.getLever(dest).equals(e.getClickedBlock().getLocation())) {
				launch(p, dest);
				return;
			}
		}
	}
	
	public void launch(Player p, Destination dest) {
		if(dest.equals(Destination.HOME)) {
			AsteroidAct.canTeleport.add(p.getName());
			p.teleport(Utils.getLocation(Destination.HOME));
			AsteroidAct.canTeleport.remove(p.getName());
			p.playSound(p.getLocation(), Sound.FIREWORK_LAUNCH, 2, 0);
			p.playSound(p.getLocation(), Sound.FIREWORK_LAUNCH, 2, 0);
			p.playSound(p.getLocation(), Sound.FIREWORK_LAUNCH, 2, 0);
			return;
		} else if(dest.equals(Destination.MOON)) {
			/*if(toLaunch.containsKey(p.getName())) {
				if(toLaunch.get(p.getName()).containsKey(dest)) {
					toLaunch.remove(p.getName());
					AsteroidAct.canTeleport.add(p.getName());
					p.teleport(Utils.getLocation(Destination.MOON));
					AsteroidAct.canTeleport.remove(p.getName());
					p.playSound(p.getLocation(), Sound.FIREWORK_LAUNCH, 2, 0);
					p.playSound(p.getLocation(), Sound.FIREWORK_LAUNCH, 2, 0);
					p.playSound(p.getLocation(), Sound.FIREWORK_LAUNCH, 2, 0);
					return;
				}
			}*/
			TitleManager.sendTimings(p, 20, 40, 20);
			TitleManager.sendTitle(p, "{\"text\":\"\",\"extra\":[{\"text\":\"!! WARNING !!\",\"color\":\"dark_red\"}]}");
			Messenger.message(p, "§4WARNING: THE ONLY WAY BACK IS THE ROCKET", State.BAD);
			Messenger.message(p, "§4YOU LOSE ALL YOUR ITEMS ON DEATH! ENTER AT OWN RISK!", State.BAD);
			//Messenger.message(p, "§4SWITCH AGAIN TO CONFIRM!", State.BAD);
			HashMap<Destination, Integer> info = new HashMap<Destination, Integer>();
			info.put(dest, 15);
			//toLaunch.put(p.getName(), info);

			//toLaunch.remove(p.getName());
			AsteroidAct.canTeleport.add(p.getName());
			p.teleport(Utils.getLocation(Destination.MOON));
			AsteroidAct.canTeleport.remove(p.getName());
			p.playSound(p.getLocation(), Sound.FIREWORK_LAUNCH, 2, 0);
			p.playSound(p.getLocation(), Sound.FIREWORK_LAUNCH, 2, 0);
			p.playSound(p.getLocation(), Sound.FIREWORK_LAUNCH, 2, 0);
			return;
		}
	}
	
	public static boolean isSettingLever(Player p, Destination dest) {
		if(settingLever.containsKey(p.getName())) {
			if(settingLever.get(p.getName()).equals(dest))
				return true;
		}
		return false;
	}
	
	public static void setSettingLever(Player p, Destination dest) {
		settingLever.put(p.getName(), dest);
	}
	
	public static void stopSettingLever(Player p) {
		if(settingLever.containsKey(p.getName()))
			settingLever.remove(p.getName());
	}
	
	public void startRemoveLeverTimer() {
		Bukkit.getScheduler().scheduleSyncRepeatingTask(AvoidTheVoid.getPlugin(), new Runnable() {
			@Override
			public void run() {
				for(String name : toLaunch.keySet()) {
					if(toLaunch.containsKey(name)) {
						for(Destination dest : Destination.values()) {
							if(toLaunch.get(name).containsKey(dest)) {
								if(toLaunch.get(name).get(dest) == 1)
									toLaunch.remove(name);
								else {
									HashMap<Destination, Integer> info = new HashMap<Destination, Integer>();
									info.put(dest, toLaunch.get(name).get(dest) - 1);
									toLaunch.put(name, info);
								}
							}
						}
					}
				}
			}
			
		}, 20, 20);
	}

}
