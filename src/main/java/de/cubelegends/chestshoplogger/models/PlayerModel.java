package de.cubelegends.chestshoplogger.models;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import org.bukkit.entity.Player;

import de.cubelegends.chestshoplogger.ChestShopLogger;
import de.cubelegends.chestshoplogger.db.DBHandler;

public class PlayerModel {

	private DBHandler db;
	
	private UUID uuid = null;
	private String name;
	
	public PlayerModel(ChestShopLogger plugin, UUID uuid) {
		this.db = plugin.getDBHandler();
		if(uuid != null) {
			fetchData(uuid);
		}
	}
	
	public PlayerModel(ChestShopLogger plugin, ResultSet rs) {
		this.db = plugin.getDBHandler();
		
		fetchData(rs);
	}
	
	public static void create(ChestShopLogger plugin, Player player) {
		try {
			
			PreparedStatement st = plugin.getDBHandler().getConnection().prepareStatement("INSERT INTO chestshop_player (uuid, name) VALUES (?, ?)");
			st.setString(1, player.getUniqueId().toString());
			st.setString(2, player.getName());
			st.execute();
			st.close();
			plugin.getDBHandler().closeConnection();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void delete(ChestShopLogger plugin, Player player) {
		try {
			
			PreparedStatement st = plugin.getDBHandler().getConnection().prepareStatement("DELETE FROM chestshop_player WHERE uuid = ?");
			st.setString(1, player.getUniqueId().toString());
			st.execute();
			st.close();
			plugin.getDBHandler().closeConnection();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static UUID getUUID(ChestShopLogger plugin, String name) {
		UUID uuid = null;
		try {
			PreparedStatement st = plugin.getDBHandler().getConnection().prepareStatement("SELECT uuid FROM chestshop_player WHERE name = ?");
			st.setString(1, name);
			ResultSet rs = st.executeQuery();
			if(rs.next()) {
				uuid = UUID.fromString(rs.getString("uuid"));
			}
			rs.close();
			st.close();
			plugin.getDBHandler().closeConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return uuid;
	}
	
	public void fetchData(UUID uuid) {
		try {
			PreparedStatement st = db.getConnection().prepareStatement("SELECT * FROM chestshop_player WHERE uuid = ?");
			st.setString(1, uuid.toString());
			ResultSet rs = st.executeQuery();
			if(rs.next()) {
				fetchData(rs);
			}
			rs.close();
			st.close();
			db.closeConnection();
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
			PreparedStatement st = db.getConnection().prepareStatement("UPDATE chestshop_player SET "
					+ "name = ? WHERE uuid = ?");
			st.setString(1, name);
			st.setString(2, uuid.toString());
			st.execute();
			st.close();
			db.closeConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}
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
