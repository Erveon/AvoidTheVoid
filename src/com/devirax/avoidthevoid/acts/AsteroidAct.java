package com.devirax.avoidthevoid.acts;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.WorldType;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.devirax.avoidthevoid.AvoidTheVoid;
import com.devirax.avoidthevoid.utils.Messenger;
import com.devirax.avoidthevoid.utils.Messenger.State;
import com.devirax.avoidthevoid.utils.Utils;
import com.onarandombox.MultiverseCore.api.MVWorldManager;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.world.DataException;

@SuppressWarnings("deprecation")
public class AsteroidAct implements Listener {
	
	public static String worldName = "Asteroid";
	public static Integer resetMinutes = 30;
	
	public static ArrayList<Location> stoneLocations = new ArrayList<Location>();
	
	public static ArrayList<String> canTeleport = new ArrayList<String>();
	
	public static void setup() {
		MVWorldManager worldManager = AvoidTheVoid.getMultiverse().getMVWorldManager();
		MultiverseWorld world = worldManager.getMVWorld(worldName);
		if(world == null) {
			worldManager.addWorld(worldName, Environment.NORMAL, "0", WorldType.FLAT, false, "EmptyWorldGenerator");
			generate();
		}
		setStoneLocations();
		if(AvoidTheVoid.getPlugin().getConfig().contains("moonreset")) {
			resetMinutes = AvoidTheVoid.getPlugin().getConfig().getInt("moonreset");
		}
		timer();
	}
	
	public static void disable() {
		regenerate();
		AvoidTheVoid.getPlugin().getConfig().set("moonreset", resetMinutes);
		AvoidTheVoid.getPlugin().saveConfig();
	}
	
	public static void timer() {
		Bukkit.getScheduler().runTaskTimer(AvoidTheVoid.getPlugin(), new Runnable() {

			@Override
			public void run() {
				if(resetMinutes <= 0) {
					regenerate();
					Messenger.messageServer("The §bAsteroid Belt§f has been reset", State.INFO);
					resetMinutes = 30;
				} else {
					resetMinutes--;
					if(resetMinutes == 20 || resetMinutes == 10 || resetMinutes == 5) {
						Messenger.messageServer("The §bAsteroid Belt §fwill be reset in §b"+resetMinutes+"§f minutes", State.INFO);
					} else if(resetMinutes == 1) {
						Messenger.messageServer("The §bAsteroid Belt §fwill be reset in §b"+resetMinutes+"§f minute", State.INFO);
					}
				}
			}
			
		}, 1, 20 * 60);
	}
	
	public static void setStoneLocations() {
		for(int x = -100; x <= 100; x++) {
			for(int y = 0; y <= 50; y++) {
				for(int z = -100; z <= 100; z++) {
					Location loc = new Location(getWorld(), x, y, z);
					if(loc.getBlock().getType().equals(Material.STONE) 
							|| loc.getBlock().getType().equals(Material.DIAMOND_ORE)
							|| loc.getBlock().getType().equals(Material.IRON_ORE)
							|| loc.getBlock().getType().equals(Material.GOLD_ORE)
							|| loc.getBlock().getType().equals(Material.COAL_ORE)
							|| loc.getBlock().getType().equals(Material.GLOWING_REDSTONE_ORE)
							|| loc.getBlock().getType().equals(Material.LAPIS_ORE)
							|| loc.getBlock().getType().equals(Material.REDSTONE_ORE)) {
						stoneLocations.add(loc);
					}
				}
			}
		}
		//Bukkit.broadcastMessage("[Debug] Set "+stoneLocations.size()+" resource spawn locations.");
	}
	
	public static void generate() {
		try {
			CuboidClipboard cc = CuboidClipboard.loadSchematic(Utils.getAsteroids().get(0));
			EditSession es = new EditSession(new BukkitWorld(getWorld()), 999999999);
			cc.paste(es, new Vector(0, 30, 0), false);
		} catch (MaxChangedBlocksException | DataException | IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void regenerate() {
		//Integer diamonds = stoneLocations.size() / 900;
		//Integer gold = stoneLocations.size() / 850;
		Integer lapis = stoneLocations.size() / 810;
		Integer iron = stoneLocations.size() / 610;
		//Integer redstone = stoneLocations.size() / 530;
		Integer coal = stoneLocations.size() / 420;
		//Bukkit.broadcastMessage("[Debug] Generated: "+coal+" coal, "+redstone+" redstone, "+iron+" iron, "+gold+" gold and "+diamonds+" diamonds.");
		for(Location loc : stoneLocations) {
			if(!loc.getBlock().getType().equals(Material.STONE)) {
				loc.getBlock().setType(Material.STONE);
			}
		}
		/*for(int i = 0; i < diamonds; i++) {
			Integer rand = new Random().nextInt(stoneLocations.size());
			stoneLocations.get(rand).getBlock().setType(Material.DIAMOND_ORE);
		}
		for(int i = 0; i < gold; i++) {
			Integer rand = new Random().nextInt(stoneLocations.size());
			stoneLocations.get(rand).getBlock().setType(Material.GOLD_ORE);
		}*/
		for(int i = 0; i < iron; i++) {
			Integer rand = new Random().nextInt(stoneLocations.size());
			stoneLocations.get(rand).getBlock().setType(Material.IRON_ORE);
		}
		/*for(int i = 0; i < redstone; i++) {
			Integer rand = new Random().nextInt(stoneLocations.size());
			stoneLocations.get(rand).getBlock().setType(Material.REDSTONE_ORE);
		}*/
		for(int i = 0; i < coal; i++) {
			Integer rand = new Random().nextInt(stoneLocations.size());
			stoneLocations.get(rand).getBlock().setType(Material.COAL_ORE);
		}
		for(int i = 0; i < lapis; i++) {
			Integer rand = new Random().nextInt(stoneLocations.size());
			stoneLocations.get(rand).getBlock().setType(Material.LAPIS_ORE);
		}
	}
	
	public static World getWorld() {
		return Bukkit.getServer().getWorld(worldName);
	}
	
	//EVENTS
	
	@EventHandler
	public void onDamage(EntityDamageEvent e) {
		if(e.getEntity() instanceof Player) {
			Player p = (Player) e.getEntity();
			if(!p.getLocation().getWorld().equals(getWorld()))
				return;
			if(e.getCause().equals(DamageCause.VOID))
				p.setHealth(0.0);
			if(e.getCause().equals(DamageCause.FALL))
				e.setCancelled(true);
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onBreak(BlockBreakEvent e) {
		if(e.getPlayer().getLocation().getWorld().equals(getWorld())) {
			/*if(e.getBlock().getType().equals(Material.STONE)) {
				e.getBlock().setType(Material.AIR);
				return;
			}*/
			if(!e.getBlock().getType().equals(Material.COAL_ORE) 
					&& !e.getBlock().getType().equals(Material.IRON_ORE)
					&& !e.getBlock().getType().equals(Material.GOLD_ORE)
					&& !e.getBlock().getType().equals(Material.REDSTONE_ORE)
					&& !e.getBlock().getType().equals(Material.GLOWING_REDSTONE_ORE)
					&& !e.getBlock().getType().equals(Material.LAPIS_ORE)
					&& !e.getBlock().getType().equals(Material.DIAMOND_ORE)) {
				e.setCancelled(true);
				Messenger.message(e.getPlayer(), "You cannot destroy this type of block on asteroids", State.BAD);
				return;
			}
		}
	}
	
	@EventHandler
	public void onPlace(BlockPlaceEvent e) {
		if(e.getPlayer().getLocation().getWorld().equals(getWorld()) && !e.getPlayer().getName().equalsIgnoreCase("Laekh")) {
			e.setCancelled(true);
			Messenger.message(e.getPlayer(), "You cannot place blocks on the asteroids", State.BAD);
		}
	}
	
	@EventHandler
	public void onFluid(PlayerBucketEmptyEvent e) {
		if(e.getPlayer().getLocation().getWorld().equals(getWorld())) {
			e.setCancelled(true);
			Messenger.message(e.getPlayer(), "You cannot place blocks on the asteroids", State.BAD);
		}
	}
	
	@EventHandler
	public void onTeleport(PlayerTeleportEvent e) {
		if(e.getFrom().getWorld().equals(getWorld())) {
			if(canTeleport.contains(e.getPlayer().getName()) || e.getCause().equals(TeleportCause.ENDER_PEARL) || e.getCause().equals(TeleportCause.UNKNOWN)) {
				for(PotionEffect potion : e.getPlayer().getActivePotionEffects()) {
					if(potion.getType().equals(PotionEffectType.JUMP)) {
						if(potion.getDuration() > 1000000) {
							e.getPlayer().removePotionEffect(PotionEffectType.JUMP);
						}
					}
				}
				e.getPlayer().setAllowFlight(true);
				return;
			}
			e.setCancelled(true);
			Messenger.message(e.getPlayer(), "You have to use the rocket to get back to earth", State.BAD);
			return;
		} else if(e.getTo().getWorld().equals(getWorld())) {
			if(canTeleport.contains(e.getPlayer().getName()) || e.getCause().equals(TeleportCause.ENDER_PEARL) || e.getCause().equals(TeleportCause.UNKNOWN)) {
				if(!e.getPlayer().getActivePotionEffects().contains(PotionEffectType.JUMP))
					e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 5));
				e.getPlayer().setFlying(false);
				e.getPlayer().setAllowFlight(false);
				return;
			}
			e.setCancelled(true);
			Messenger.message(e.getPlayer(), "You have to use the rocket to go to the asteroid belt", State.BAD);
			return;
		}
	}

	public static boolean canTeleport(Player p) {
		if(canTeleport.contains(p.getName()))
			return true;
		return false;
	}
	
	@EventHandler
	public void onEchest(InventoryOpenEvent e) {
		if(!e.getPlayer().getLocation().getWorld().equals(getWorld()))
			return;
		if(e.getInventory().getType().equals(InventoryType.ENDER_CHEST)) {
			Player p = (Player) e.getPlayer();
			p.sendMessage("§4The vacuum of space stops you from opening your enderchest.");
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onExplode(EntityExplodeEvent e) {
		e.blockList().clear();
	}
	
	@EventHandler
	public void onFly(PlayerToggleFlightEvent e) {
		if(e.getPlayer().getLocation().getWorld().equals(getWorld())) {
			e.getPlayer().sendMessage("§4You cannot fly in space, silly!");
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onMove(PlayerMoveEvent e) {
		if(e.getPlayer().getLocation().getWorld().equals(getWorld())) {
			if(!e.getPlayer().getActivePotionEffects().contains(PotionEffectType.JUMP)) {
				e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 5));
			}
		}
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		/*canTeleport.add(e.getPlayer().getName());
		if(e.getPlayer().getLocation().getWorld().equals(getWorld())) {
			e.getPlayer().teleport(Utils.getSpawn());
		}
		canTeleport.remove(e.getPlayer().getName());*/
	}
	
	@EventHandler
	public void onSpawn(EntitySpawnEvent e) {
		if(e.getLocation().getWorld().equals(getWorld())) {
			if(e.getEntityType().equals(EntityType.ZOMBIE)) {
				Zombie z = (Zombie) e.getEntity();
				z.getEquipment().setHelmet(new ItemStack(Material.GLASS));
				z.getEquipment().setHelmetDropChance(0);
			} else if(e.getEntityType().equals(EntityType.SKELETON)) {
				Skeleton s = (Skeleton) e.getEntity();
				s.getEquipment().setHelmet(new ItemStack(Material.GLASS));
				s.getEquipment().setHelmetDropChance(0);
			}
		}
	}

}
