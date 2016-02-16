package com.devirax.avoidthevoid.acts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerEggThrowEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;

import com.devirax.avoidthevoid.AvoidTheVoid;
import com.devirax.avoidthevoid.island.Island;
import com.devirax.avoidthevoid.island.IslandManager;
import com.devirax.avoidthevoid.utils.Messenger;
import com.devirax.avoidthevoid.utils.Messenger.State;


public class MobEggAct implements Listener {
	
	public static ArrayList<String> possibleThrowing = new ArrayList<String>();
	public static ArrayList<String> throwing = new ArrayList<String>();
	public static HashMap<String, UUID> toCatch = new HashMap<String, UUID>();
	
	public static void setup() {
		addRecipe();
	}
	
	public static void addRecipe() {
	    ShapelessRecipe recipe = new ShapelessRecipe(getMobCatcher()).addIngredient(Material.EGG).addIngredient(Material.DIAMOND).addIngredient(Material.DIAMOND);
        AvoidTheVoid.getPlugin().getServer().addRecipe(recipe);
	}
	
	public static ItemStack getMobCatcher() {
		ItemStack mobCatcher = new ItemStack(Material.EGG);
	    ItemMeta meta = (ItemMeta) mobCatcher.getItemMeta();
	    List<String> loreData = new ArrayList<String>();
	    loreData.add("Catches a mob");
	    meta.setDisplayName("Mob Catcher");
	    meta.setLore(loreData);
	    mobCatcher.setItemMeta(meta);
	    return mobCatcher;
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		if(e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			if(e.getPlayer().getItemInHand().equals(getMobCatcher())) {
				possibleThrowing.add(e.getPlayer().getName());
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onThrowHit(PlayerEggThrowEvent e) {
		/*if(possibleThrowing.contains(e.getPlayer().getName())) {
			throwing.add(e.getPlayer().getName());
			possibleThrowing.remove(e.getPlayer().getName());
			e.setHatching(false);
		}*/
		Player p = e.getPlayer();
		if(throwing.contains(p.getName())) {
			throwing.remove(p.getName());
			e.setHatching(false);
			if(!toCatch.containsKey(p.getName()))
				return;
			UUID mobid = toCatch.get(p.getName());
			Entity hit = null;
			for(Entity entity : e.getEgg().getLocation().getWorld().getEntities()) {
				if(entity.getUniqueId().equals(mobid))
					hit = entity;
			}
			if(hit == null)
				return;
			if(hit instanceof Player) {
				Messenger.message(p, "Did you seriously think that would've worked?", State.INFO);
				return;
			}
			Island is = IslandManager.getAbsoluteIslandForLocation(hit.getLocation());
			if(is == null) {
				Messenger.message(p, "You may only catch mobs on an island you're on", State.BAD);
				return;
			}
			if(!is.getOnlinePlayers().contains(p)) {
				Messenger.message(p, "You may only catch mobs on an island you're on", State.BAD);
				return;
			} else {
				if(toCatch.containsKey(p.getName()))  {
					if(!toCatch.get(p.getName()).equals(hit.getUniqueId()))
						return;
				} else {
					return;
				}
				Messenger.message(p, "You caught a "+hit.getType().name()+"!", State.GOOD);
				ItemStack mobEgg = new ItemStack(Material.MONSTER_EGG, 1, (short) hit.getType().getTypeId());
				hit.getLocation().getWorld().dropItem(hit.getLocation(), mobEgg);
				toCatch.remove(p.getName());
				hit.remove();
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onHit(EntityDamageByEntityEvent e) {
		if(e.getDamager() instanceof Egg) {
			Egg egg = (Egg) e.getDamager();
			if(egg.getShooter() instanceof Player) {
				Player p = (Player) egg.getShooter();
				if(possibleThrowing.contains(p.getName())) {
					throwing.add(p.getName());
					toCatch.put(p.getName(), e.getEntity().getUniqueId());
					possibleThrowing.remove(p.getName());
				}
			}
		}
		/*if(e.getDamager() instanceof Egg) {
			Egg egg = (Egg) e.getDamager();
			if(egg.getShooter() instanceof Player) {
				Player p = (Player) egg.getShooter();
				Entity hit = e.getEntity();
				if(throwing.contains(p.getName())) {
					throwing.remove(p.getName());
					if(hit instanceof Player) {
						Messenger.message(p, "Did you seriously think that would've worked?", State.INFO);
						return;
					}
					Island is = IslandManager.getAbsoluteIslandForLocation(e.getEntity().getLocation());
					if(is == null) {
						Messenger.message(p, "You may only catch mobs on an island you're on", State.BAD);
						return;
					}
					if(!is.getOnlinePlayers().contains(p)) {
						Messenger.message(p, "You may only catch mobs on an island you're on", State.BAD);
						return;
					} else {
						if(toRemove.contains(hit.getUniqueId()))
							return;
						Messenger.message(p, "You caught a "+hit.getType().name()+"!", State.GOOD);
						ItemStack mobEgg = new ItemStack(Material.MONSTER_EGG, 1, (short) hit.getType().getTypeId());
						hit.getLocation().getWorld().dropItem(hit.getLocation(), mobEgg);
						toRemove.add(hit.getUniqueId());
						hit.remove();
					}
				}
			}
		}*/
	}

}
