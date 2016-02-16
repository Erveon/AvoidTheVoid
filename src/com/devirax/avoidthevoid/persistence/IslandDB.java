package com.devirax.avoidthevoid.persistence;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Location;

import com.devirax.avoidthevoid.AvoidTheVoid;
import com.devirax.avoidthevoid.island.Island;
import com.devirax.avoidthevoid.island.IslandManager;
import com.devirax.avoidthevoid.island.sub.Member;
import com.devirax.avoidthevoid.island.sub.Owner;
import com.devirax.avoidthevoid.utils.Utils;

public class IslandDB {
	
	private static Connection connection = AvoidTheVoid.c;
	
	public static void saveIsland(Island is) {
		try {
	    	Statement statement = connection.createStatement();
			if(islandExists(is.getId())) {
		    	statement.executeUpdate("UPDATE `Islands` SET "
		    			+ "`Name` = '"+ is.getName() +"', "
		    			+ "`Experience` = '"+ is.getExperience() +"', "
		    			+ "`Owner` = '"+ is.getOwner().getSave() +"', "
		    			+ "`Members` = '"+ is.getMembersSave() +"', "
		    			+ "`X` = '"+ is.getX() +"', "
		    			+ "`Z` = '"+ is.getZ() +"', "
		    			+ "`Spawn` = '"+ is.getSaveSpawn() +"' "
		    			+ "WHERE `ID` = '"+is.getId()+"'");
			} else {
		    	statement.executeUpdate("INSERT INTO `Islands` (`ID`, `Name`, `Experience`, `Owner`, `Members`, `X`, `Z`, `Spawn`) "
		    			+ "Values ('"+ is.getId() + "', '"+ is.getName() +"', '"+ is.getExperience() +"', '"+ is.getOwner().getSave() +"', '"+ is.getMembersSave() +"', '"+ is.getX() +"', '"+ is.getZ() +"', '"+ is.getSaveSpawn() +"')");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void saveConvertedIslands() {
		try {
			if(connection.isClosed()) {
				System.out.println("Connection is closed, can't perform MySQL command.");
				return;
			}
			Statement statement = connection.createStatement();
			StringBuilder values = new StringBuilder();
			boolean first = true;
			values.append("INSERT INTO `Islands` (`ID`, `Name`, `Experience`, `Owner`, `Members`, `X`, `Z`, `Spawn`) Values (");
			for(Island is : IslandManager.getIslands()) {
				if(!first) {
					values.append(", ");
					first = false;
				}
				values.append("('"+ is.getId() + "', '"+ is.getName() +"', '"+ is.getExperience() +"', '"+ is.getOwner().getSave() +"', '"+ is.getMembersSave() +"', '"+ is.getX() +"', '"+ is.getZ() +"', '"+ is.getSaveSpawn() +"')");
			}
			values.append(")");
			statement.executeUpdate(values.toString());
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void removeIsland(Island is) {
		try {
			if(connection.isClosed()) {
				System.out.println("Connection is closed, can't perform MySQL command.");
				return;
			}
	    	Statement statement = connection.createStatement();
	    	String query = "DELETE FROM `Islands` WHERE `ID`='"+ is.getId() + "'";
	    	statement.execute(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static boolean islandExists(Integer id) throws SQLException {
		try {
	    	if(connection.isClosed()) {
				System.out.println("Connection is closed, can't perform MySQL command.");
				return false;
			}
	    	Statement statement = connection.createStatement();
	    	String querySelect = "SELECT `ID` FROM `Islands` WHERE `ID`='"+ id + "'";
	    	ResultSet rs = statement.executeQuery(querySelect);
	    	if (!rs.next()) {
	    		return false;
	    	}
	    	return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
    }
	
	@SuppressWarnings("unused")
	public static void loadTopIslands() {
		try {
			if(connection.isClosed()) {
				System.out.println("Connection is closed, can't perform MySQL command.");
				return;
			}
	    	Statement statement = connection.createStatement();
			ResultSet rs = statement.executeQuery("SELECT * FROM `Islands` ORDER BY Experience DESC LIMIT 10");
			HashMap<Integer, String> topIslands = new HashMap<>();
			int i = 1;
			Integer id;
			String name;
			Double experience;
			Owner owner;
			String[] memString;
			ArrayList<Member> members;
			Integer x, z;
			Location spawn;
	    	while(rs.next()) {
	    		id = rs.getInt(1);
	    		name = rs.getString(2);
	    		experience = rs.getDouble(3);
	    		UUID ownerid = null;
	    		try {
	    			ownerid = UUID.fromString(rs.getString(4));
	    		} catch(Exception e) {}
	    		if(ownerid == null) 
	    			System.out.println("COULD NOT LOAD ISLAND FOR UUID: "+rs.getString(4));
	    		owner = new Owner(ownerid);
	    		memString = rs.getString(5).split("#");
	    		members = new ArrayList<Member>();
	    		for(String s : memString) {
	    			UUID uuid = null;
	    			try {
	    				uuid = UUID.fromString(s);
	    			} catch(IllegalArgumentException e) {}
	    			if(uuid != null)
	    				members.add(new Member(uuid));
	    		}
	    		x = rs.getInt(6);
	    		z = rs.getInt(7);
	    		spawn = Utils.getLocForString(rs.getString(8));
	    		Integer experienceInt = (int) Math.ceil(experience);
				String experienceStr;
				if ((experience == Math.floor(experience)) && !Double.isInfinite(experience)) {
					experienceStr = experienceInt.toString();
				} else {
					experienceStr = String.format("%.1f", experience);
				}
	    		if(ownerid != null) {
	    			topIslands.put(i, "§a" + i + ") §b" + name + " [§6"+owner.getUsername()+"§b] §7- " + experienceStr);
	    			i++;
	    		}
	    	}
	    	IslandManager.setTopIslands(topIslands);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void loadIslands() {
		try {
			if(connection.isClosed()) {
				System.out.println("Connection is closed, can't perform MySQL command.");
				return;
			}
	    	Statement statement = connection.createStatement();
			ResultSet rs = statement.executeQuery("SELECT * FROM `Islands`");
			Integer id;
			String name;
			Double experience;
			Owner owner;
			String[] memString;
			ArrayList<Member> members;
			Integer x, z;
			Location spawn;
	    	while(rs.next()) {
	    		id = rs.getInt(1);
	    		name = rs.getString(2);
	    		experience = rs.getDouble(3);
	    		UUID ownerid = null;
	    		try {
	    			ownerid = UUID.fromString(rs.getString(4));
	    		} catch(Exception e) {}
	    		if(ownerid == null) 
	    			System.out.println("COULD NOT LOAD ISLAND FOR UUID: "+rs.getString(4));
	    		owner = new Owner(ownerid);
	    		memString = rs.getString(5).split("#");
	    		members = new ArrayList<Member>();
	    		for(String s : memString) {
	    			UUID uuid = null;
	    			try {
	    				uuid = UUID.fromString(s);
	    			} catch(IllegalArgumentException e) {}
	    			if(uuid != null)
	    				members.add(new Member(uuid));
	    		}
	    		x = rs.getInt(6);
	    		z = rs.getInt(7);
	    		spawn = Utils.getLocForString(rs.getString(8));
	    		if(ownerid != null)
	    			IslandManager.loadIsland(id, owner, members, x, z, name, experience, spawn);
	    	}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
