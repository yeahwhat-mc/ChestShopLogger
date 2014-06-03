package de.cubelegends.chestshoplogger.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import de.cubelegends.chestshoplogger.ChestShopLogger;
import de.cubelegends.chestshoplogger.models.PlayerModel;

public class JoinListener implements Listener {
	
	private ChestShopLogger plugin;
	
	public JoinListener(ChestShopLogger plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler(priority=EventPriority.NORMAL)
	public void onPlayerJoin(PlayerJoinEvent event) {
		PlayerModel player = new PlayerModel(plugin, event.getPlayer().getUniqueId());
		if(player.getUUID() == null) {
			PlayerModel.create(plugin, event.getPlayer());
		} else {
			if(!player.getName().equals(event.getPlayer().getName())) {
				player.setName(event.getPlayer().getName());
				player.pushData();
			}
		}
	}

}
