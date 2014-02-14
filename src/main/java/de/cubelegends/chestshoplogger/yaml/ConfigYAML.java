package de.cubelegends.chestshoplogger.yaml;

import org.bukkit.configuration.file.FileConfiguration;

import de.cubelegends.chestshoplogger.ChestShopLogger;

public class ConfigYAML {
	
	public ConfigYAML(ChestShopLogger plugin) {
		FileConfiguration config = plugin.getConfig();

		config.addDefault("database.host", "localhost");
		config.addDefault("database.port", 3306);
		config.addDefault("database.user", "root");
		config.addDefault("database.password", "");
		config.addDefault("database.database", "bukkit");
		config.addDefault("database.tableVersion", 1);
		config.options().copyDefaults(true);
		
		plugin.saveConfig();
	}
	
}
