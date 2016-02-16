package com.devirax.avoidthevoid;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import net.milkbowl.vault.permission.Permission;

import org.bukkit.Bukkit;
import org.bukkit.World.Environment;
import org.bukkit.WorldType;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.devirax.avoidthevoid.GUI.MemberGUI;
import com.devirax.avoidthevoid.GUI.SettingsGUI;
import com.devirax.avoidthevoid.acts.ActManager;
import com.devirax.avoidthevoid.commands.CommandHandler;
import com.devirax.avoidthevoid.island.IslandManager;
import com.devirax.avoidthevoid.island.Nether;
import com.devirax.avoidthevoid.listeners.Build;
import com.devirax.avoidthevoid.listeners.Chat;
import com.devirax.avoidthevoid.listeners.Damage;
import com.devirax.avoidthevoid.listeners.Death;
import com.devirax.avoidthevoid.listeners.Experience;
import com.devirax.avoidthevoid.listeners.Grow;
import com.devirax.avoidthevoid.listeners.Interact;
import com.devirax.avoidthevoid.listeners.Invite;
import com.devirax.avoidthevoid.listeners.Lever;
import com.devirax.avoidthevoid.listeners.Loaders;
import com.devirax.avoidthevoid.listeners.Move;
import com.devirax.avoidthevoid.listeners.OnePointEight;
import com.devirax.avoidthevoid.listeners.Piston;
import com.devirax.avoidthevoid.listeners.Rename;
import com.devirax.avoidthevoid.listeners.Target;
import com.devirax.avoidthevoid.persistence.MySQL;
import com.devirax.avoidthevoid.utils.Utils;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVWorldManager;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;

public class AvoidTheVoid extends JavaPlugin {
	
	static Plugin p;
	static Integer maxIslandLevel = 15;
	
	static File schematicFolder;
	static File otherSchematicFolder;
	static File netherSchematicFolder;
	
	public static Connection c = null;
	public MySQL mySQL;
	public String host, port, database, user, password;
	
	static boolean noDatabase = false;
	
	public static Permission permission = null;
	
	@SuppressWarnings("deprecation")
	@Override
	public void onEnable() {
		p = this;
		this.saveDefaultConfig();
		Utils.loadConfig();
		initDatabase();
		if(mySQL.checkConnection())
			IslandManager.load();
		
		setupPermissions();
		
		PluginManager pm = getServer().getPluginManager();
		
		pm.registerEvents(new OnePointEight(), this);
		pm.registerEvents(new Build(), this);
		pm.registerEvents(new SettingsGUI(), this);
		pm.registerEvents(new MemberGUI(), this);
		pm.registerEvents(new Rename(), this);
		pm.registerEvents(new Invite(), this);
		pm.registerEvents(new Chat(), this);
		pm.registerEvents(new Loaders(), this);
		pm.registerEvents(new Move(), this);
		pm.registerEvents(new Piston(), this);
		pm.registerEvents(new Grow(), this);
		pm.registerEvents(new Interact(), this);
		pm.registerEvents(new Experience(), this);
		pm.registerEvents(new Damage(), this);
		pm.registerEvents(new Lever(), this);
		pm.registerEvents(new Nether(), this);
		pm.registerEvents(new Death(), this);
		pm.registerEvents(new Target(), this);
		
		setCommands();
		
		checkWorlds();
		initSchematics();
		ActManager.setup();
		//Lever.startRemoveLeverTimer();
		for(Player pl : Bukkit.getOnlinePlayers()) {
			IslandManager.loadTotalLevel(pl);
			if(pl.getLocation().getWorld().equals(Bukkit.getWorld("Asteroid"))) {
				if(!pl.getActivePotionEffects().contains(PotionEffectType.JUMP))
					pl.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 5));
			}
		}
		IslandManager.saveTimer();
	}
	
	@Override
	public void onDisable() {
		IslandManager.saveAllUnsavedIslands();
		ActManager.disable();
	}
	
	public static Plugin getPlugin() {
		return p;
	}
	
	public static Integer getMaxIslandLevel() {
		return maxIslandLevel;
	}
	
	public void setCommands() {
        this.getCommand("AvoidTheVoid").setExecutor((CommandExecutor)new CommandHandler(getPlugin()));
    }
	
	public static boolean hasDatabaseConnection() {
		return !noDatabase;
	}
	
	public void checkWorlds() {
		String worldName = getConfig().getString("islandworld");
		MVWorldManager worldManager = getMultiverse().getMVWorldManager();
		MultiverseWorld islandWorld = worldManager.getMVWorld(worldName);
		if(islandWorld == null) {
			worldManager.addWorld(worldName, Environment.NORMAL, "0", WorldType.FLAT, false, "EmptyWorldGenerator");
		}
		String netherName = getConfig().getString("islandworld") + "_nether";
		MultiverseWorld netherWorld = worldManager.getMVWorld(netherName);
		if(netherWorld == null) {
			worldManager.addWorld(netherName, Environment.NETHER, "0", WorldType.FLAT, false, "EmptyWorldGenerator");
		}
	}
	
	public static WorldEditPlugin getWorldEdit() {
		Plugin worldEdit = Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
		if ((worldEdit instanceof WorldEditPlugin)) {
			return (WorldEditPlugin)worldEdit;
		}
		return null;
	}
	
	public static MultiverseCore getMultiverse() {
		return (MultiverseCore) Bukkit.getServer().getPluginManager().getPlugin("Multiverse-Core");
	}
	
	public static File getSchematicFolder() {
		return schematicFolder;
	}
	
	public static File getOtherSchematicFolder() {
		return otherSchematicFolder;
	}
	
	public static File getNetherSchematicFolder() {
		return netherSchematicFolder;
	}
	
	public void initSchematics() {
		File f = new File(this.getDataFolder()+"/schematics");
		if(!f.exists()) {
			f.mkdirs();
		}
		schematicFolder = f;
		File f2 = new File(this.getDataFolder()+"/otherschematics");
		if(!f2.exists()) {
			f2.mkdirs();
		}
		otherSchematicFolder = f2;
		File f3 = new File(this.getDataFolder()+"/netherschematic");
		if(!f3.exists()) {
			f3.mkdirs();
		}
		netherSchematicFolder = f3;
		Utils.loadSchematics();
	}
	
	public void initDatabase() {
		this.host = getConfig().getString("Database.host");
		this.port = getConfig().getString("Database.port");
		this.database = getConfig().getString("Database.database");
		this.user = getConfig().getString("Database.user");
		this.password = getConfig().getString("Database.password");
		
		this.mySQL = new MySQL(this, host, port, database, user, password);
		c = this.mySQL.openConnection();

		if(mySQL.checkConnection()) {
			try {
				Statement statement = c.createStatement();
				statement.executeUpdate("CREATE TABLE IF NOT EXISTS `Islands` ("
						+ " `ID` INT(16) NOT NULL,"
						+ " `Name` TINYTEXT,"
						+ " `Experience` DOUBLE,"
						+ " `Owner` TEXT,"
						+ " `Members` TEXT,"
						+ " `X` INT,"
						+ " `Z` INT,"
						+ " `Spawn` TEXT,"
						+ "PRIMARY KEY (`ID`)"
						+ ")");
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} else {
			noDatabase = true;
		}
		
	}
	
	private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
        if (permissionProvider != null) {
            permission = permissionProvider.getProvider();
        }
        return (permission != null);
    }

}
