package de.cubelegends.chestshoplogger;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.bukkit.plugin.java.JavaPlugin;

import de.cubelegends.chestshoplogger.handler.CommandHandler;
import de.cubelegends.chestshoplogger.handler.DBHandler;
import de.cubelegends.chestshoplogger.listener.ChestShopListener;
import de.cubelegends.chestshoplogger.yaml.ConfigYAML;

public class ChestShopLogger extends JavaPlugin {
	
	private DBHandler db;
	
	public void onEnable() {
		
		// Load config
		new ConfigYAML(this);
						
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
		
		// Register events
		getServer().getPluginManager().registerEvents(new ChestShopListener(this), this);
		
		// Register command executor
		getCommand("csl").setExecutor(new CommandHandler(this));
		getCommand("shop").setExecutor(new CommandHandler(this));
		
	}
	
	public void onDisable() {
		
	}
	
	public DBHandler getDBHandler() {
		return db;
	}
	
	private void setupTables() {
		try {
			PreparedStatement st = db.getConnection().prepareStatement(
					"CREATE TABLE IF NOT EXISTS chestshop_shop ("
					+ "id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,"
					+ "world VARCHAR(50),"
					+ "x INT,"
					+ "y INT,"
					+ "z INT,"
					+ "tpx DOUBLE,"
					+ "tpy DOUBLE,"
					+ "tpz DOUBLE,"
					+ "tpyaw DOUBLE,"
					+ "tppitch DOUBLE,"
					+ "owner VARCHAR(50),"
					+ "owneruid VARCHAR(50),"
					+ "amount INT,"
					+ "buyprice DOUBLE,"
					+ "sellprice DOUBLE,"
					+ "itemname VARCHAR(50),"
					+ "created BIGINT"
					+ ");"
					);
			st.execute();
			st.close();
			st = db.getConnection().prepareStatement(
					"CREATE TABLE IF NOT EXISTS chestshop_transaction ("
					+ "id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,"
					+ "shopid INT,"
					+ "client VARCHAR(50),"
					+ "type VARCHAR(10),"
					+ "price DOUBLE,"
					+ "date BIGINT"
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
