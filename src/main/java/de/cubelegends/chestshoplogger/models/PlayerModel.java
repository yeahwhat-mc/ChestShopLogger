package de.cubelegends.chestshoplogger.models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import org.bukkit.entity.Player;

import de.cubelegends.chestshoplogger.ChestShopLogger;

public class PlayerModel {

	private ChestShopLogger plugin;
	
	private UUID uuid = null;
	private String name;
	
	public PlayerModel(ChestShopLogger plugin, UUID uuid) {
		this.plugin = plugin;
		if(uuid != null) {
			fetchData(uuid);
		}
	}
	
	public PlayerModel(ChestShopLogger plugin, ResultSet rs) {
		this.plugin = plugin;
		fetchData(rs);
	}
	
	public static void create(ChestShopLogger plugin, Player player) {
		try {
			
			Connection con = plugin.getDBHandler().open();
			PreparedStatement st = con.prepareStatement("INSERT INTO chestshop_player (uuid, name) VALUES (?, ?)");
			st.setString(1, player.getUniqueId().toString());
			st.setString(2, player.getName());
			st.execute();
			st.close();
			con.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void delete(ChestShopLogger plugin, Player player) {
		try {

			Connection con = plugin.getDBHandler().open();
			PreparedStatement st = con.prepareStatement("DELETE FROM chestshop_player WHERE uuid = ?");
			st.setString(1, player.getUniqueId().toString());
			st.execute();
			st.close();
			con.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static boolean exists(ChestShopLogger plugin, String name) {
		return getUUID(plugin, name) != null;
	}
	
	public static UUID getUUID(ChestShopLogger plugin, String name) {
		UUID uuid = null;
		try {
			
			Connection con = plugin.getDBHandler().open();
			PreparedStatement st = con.prepareStatement("SELECT uuid FROM chestshop_player WHERE name = ?");
			st.setString(1, name);
			ResultSet rs = st.executeQuery();
			if(rs.next()) {
				uuid = UUID.fromString(rs.getString("uuid"));
			}
			rs.close();
			st.close();
			con.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return uuid;
	}
	
	public void fetchData(UUID uuid) {
		try {

			Connection con = plugin.getDBHandler().open();
			PreparedStatement st = con.prepareStatement("SELECT * FROM chestshop_player WHERE uuid = ?");
			st.setString(1, uuid.toString());
			ResultSet rs = st.executeQuery();
			if(rs.next()) {
				fetchData(rs);
			}
			rs.close();
			st.close();
			con.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void fetchData(ResultSet rs) {
		try {
			uuid = UUID.fromString(rs.getString("uuid"));
			name = rs.getString("name");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void pushData() {
		try {

			Connection con = plugin.getDBHandler().open();
			PreparedStatement st = con.prepareStatement("UPDATE chestshop_player SET "
					+ "name = ? WHERE uuid = ?");
			st.setString(1, name);
			st.setString(2, uuid.toString());
			st.execute();
			st.close();
			con.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public boolean exists() {
		return uuid != null;
	}
	
	public UUID getUUID() {
		return uuid;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

}
