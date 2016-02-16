package com.devirax.avoidthevoid.acts;

import org.bukkit.plugin.PluginManager;

import com.devirax.avoidthevoid.AvoidTheVoid;

public class ActManager {
	
	public static void setup() {
		AsteroidAct.setup();
		//MobEggAct.setup();
		PluginManager pm = AvoidTheVoid.getPlugin().getServer().getPluginManager();
		pm.registerEvents(new AsteroidAct(), AvoidTheVoid.getPlugin());
		//pm.registerEvents(new MobEggAct(), AvoidTheVoid.getPlugin());
	}
	
	public static void disable() {
		AsteroidAct.disable();
	}

}
