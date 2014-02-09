package de.cubelegends.chestshoplogger;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.bukkit.plugin.java.JavaPlugin;

import de.cubelegends.chestshoplogger.handler.DBHandler;

public class ChestShopLogger extends JavaPlugin {
	
	private DBHandler db;
	
	public void onEnable() {
		
		// Load config
		this.saveDefaultConfig();
						
		// Load database
		db = new DBHandler(
				this.getConfig().getString("database.host"),
				this.getConfig().getInt("database.port"),
				this.getConfig().getString("database.user"),
				this.getConfig().getString("database.password"),
				this.getConfig().getString("database.database")
				);
		if(db.getConnection() == null) {
			this.getServer().getPluginManager().disablePlugin(this);
			return;
		}
		setupTables();
		db.closeConnection();
		
	}
	
	public void onDisable() {
		
	}
	
	private void setupTables() {
		try {
			PreparedStatement st = db.getConnection().prepareStatement(
					"CREATE TABLE IF NOT EXISTS chestshop_shop ("
					+ "id INT NOT NULL AUTO_INCREMENT PRIMARY KEY"
					+ ");"
					);
			st.execute();
			st.close();
			st = db.getConnection().prepareStatement(
					"CREATE TABLE IF NOT EXISTS chestshop_transaction ("
					+ "id INT NOT NULL AUTO_INCREMENT PRIMARY KEY"
					+ ");"
					);
			st.execute();
			st.close();
			db.closeConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
