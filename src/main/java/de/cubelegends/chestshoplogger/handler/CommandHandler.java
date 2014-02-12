package de.cubelegends.chestshoplogger.handler;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.cubelegends.chestshoplogger.ChestShopLogger;
import de.cubelegends.chestshoplogger.models.ShopModel;

public class CommandHandler implements CommandExecutor {
	
	private ChestShopLogger plugin;
	
	private final String PREFIX = ChatColor.GREEN + "[CSL] " + ChatColor.GRAY;
	
	public CommandHandler(ChestShopLogger plugin) {
		this.plugin = plugin;
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if(args.length == 2 && args[0].equalsIgnoreCase("tp")) {
			
			if(!(sender instanceof Player)) {
				sender.sendMessage("This command can only be executed by a player!");
				return true;
			}
			
			Player player = (Player) sender;
			this.tp(player, args[1]);
			
		}
		
		return false;
	}
	
	private void tp(Player player, String idStr) {
		int id = 0;
		
		if(!player.hasPermission("csl.tp")) {
			player.sendMessage(PREFIX + "You don't have enough permissions to do this!");
			return;
		}
		
		try {
			id = Integer.parseInt(idStr);
		} catch (NumberFormatException e) {
			player.sendMessage(PREFIX + "You've entered an invalid id!");
			return;
		}
		
		ShopModel shop = new ShopModel(plugin, id);
		
		if(shop.getID() == -1) {
			player.sendMessage(PREFIX + "There is no shop with the id " + id + "!");
			return;
		}
		
		player.teleport(shop.getLoc());
		player.sendMessage(PREFIX + "Welcome to shop " + id + "!");
		
	}

}
