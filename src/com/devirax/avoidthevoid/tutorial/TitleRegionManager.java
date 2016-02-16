package com.devirax.avoidthevoid.tutorial;

import java.util.ArrayList;

import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.regions.CuboidRegion;

public class TitleRegionManager {
	
	public ArrayList<TitleRegion> regions = new ArrayList<TitleRegion>();
	
	Vector v1, v2;
	
	public void addRegion(Vector v1, Vector v2) {
		this.v1 = v1;
		this.v2 = v2;
	}
	
	public CuboidRegion getRegion() {
		return new CuboidRegion(v1, v2);
	}
	
}
