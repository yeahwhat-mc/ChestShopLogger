package de.cubelegends.chestshoplogger.yaml;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import de.cubelegends.chestshoplogger.ChestShopLogger;

public abstract class BasicYaml {
	
	protected ChestShopLogger plugin;
	protected String filename;
	protected File file;
	protected FileConfiguration config;
	
	public BasicYaml(ChestShopLogger plugin, String filename) {
		this.plugin = plugin;
		this.filename = filename;
		
		file = new File(plugin.getDataFolder(), filename);
		this.reloadConfig();
	}
	
	public void reloadConfig() {
		if (file == null) {
			return;
		}
		config = YamlConfiguration.loadConfiguration(file);
	}
	
	public void saveConfig() {
		if (config == null || file == null) {
			return;
		}
		try {
			config.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public FileConfiguration getConfig() {
		return config;
	}

}
