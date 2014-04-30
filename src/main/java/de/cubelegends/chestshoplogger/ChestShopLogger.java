package de.cubelegends.chestshoplogger;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import de.cubelegends.chestshoplogger.cmds.ShopCmd;
import de.cubelegends.chestshoplogger.db.DBHandler;
import de.cubelegends.chestshoplogger.listeners.ChestShopListener;

public class ChestShopLogger extends JavaPlugin {
	
	public static final String PREFIX = ChatColor.DARK_GREEN + "[ChestShopLogger] " + ChatColor.GRAY;
	
	private DBHandler db;
	
	public void onEnable() {
		
		// Load config
		this.getConfig().addDefault("database.host", "localhost");
		this.getConfig().addDefault("database.port", 3306);
		this.getConfig().addDefault("database.user", "root");
		this.getConfig().addDefault("database.password", "");
		this.getConfig().addDefault("database.database", "bukkit");
		this.getConfig().addDefault("database.tableVersion", 1);
		this.getConfig().options().copyDefaults(true);
		this.saveConfig();
						
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
		getCommand("shop").setExecutor(new ShopCmd(this));
		
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
					+ "owneruuid VARCHAR(50),"
					+ "maxamount INT,"
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
					+ "clientuuid VARCHAR(50),"
					+ "type VARCHAR(10),"
					+ "amount INT,"
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
