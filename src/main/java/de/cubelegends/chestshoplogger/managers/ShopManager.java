package de.cubelegends.chestshoplogger.managers;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.cubelegends.chestshoplogger.ChestShopLogger;
import de.cubelegends.chestshoplogger.helpers.MathHelper;
import de.cubelegends.chestshoplogger.helpers.ShopHelper;
import de.cubelegends.chestshoplogger.models.ShopModel;

public class ShopManager {

private ChestShopLogger plugin;
	
	public ShopManager(ChestShopLogger plugin) {
		this.plugin = plugin;
	}
	
	public void tp(CommandSender sender, String idStr) {
		
		int id = 0;
		
		if(!(sender instanceof Player)) {
			sender.sendMessage("This command can only be executed by a player!");
			return;
		}
		
		if(!sender.hasPermission("chestshoplogger.tp") && !sender.isOp()) {
			sender.sendMessage(ChestShopLogger.PREFIX + "You don't have enough permissions to do this!");
			return;
		}
		
		try {
			id = Integer.parseInt(idStr);
		} catch (NumberFormatException e) {
			sender.sendMessage(ChestShopLogger.PREFIX + "You've entered an invalid id!");
			return;
		}
		
		ShopModel shop = new ShopModel(plugin, id);
		
		if(shop.exists()) {
			sender.sendMessage(ChestShopLogger.PREFIX + "There is no shop with the id " + id + "!");
			return;
		}
		
		Block blockAbove = new Location(shop.getTP().getWorld(), shop.getTP().getX(), shop.getTP().getY() + 1, shop.getTP().getZ()).getBlock();
		if(shop.getTP().getBlock().getType().isSolid() || blockAbove.getType().isSolid()) {
			sender.sendMessage(ChestShopLogger.PREFIX + "The destination is obstructed!");
			return;
		}

		Player player = (Player) sender;
		player.teleport(shop.getTP());
		sender.sendMessage(ChestShopLogger.PREFIX + "Welcome to shop " + id + "!");
		
	}
	
	public void coords(CommandSender sender, String idStr) {
		
		int id = 0;
		
		if(!sender.hasPermission("chestshoplogger.coords") && !sender.isOp()) {
			sender.sendMessage(ChestShopLogger.PREFIX + "You don't have enough permissions to do this!");
			return;
		}
		
		try {
			id = Integer.parseInt(idStr);
		} catch (NumberFormatException e) {
			sender.sendMessage(ChestShopLogger.PREFIX + "You've entered an invalid id!");
			return;
		}
		
		ShopModel shop = new ShopModel(plugin, id);
		
		if(shop.exists()) {
			sender.sendMessage(ChestShopLogger.PREFIX + "There is no shop with the id " + id + "!");
			return;
		}

		Location loc = shop.getLoc();
		sender.sendMessage(ChestShopLogger.PREFIX + "Shop " + id + " is located in " + loc.getWorld().getName() + " at " + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ() + ".");
		
	}
	
	public void find(CommandSender sender, String action, String dirtyName) {
		
		if(!sender.hasPermission("chestshoplogger.find") && !sender.isOp()) {
			sender.sendMessage(ChestShopLogger.PREFIX + "You don't have enough permissions to do this!");
			return;
		}
		
		String itemName = ShopHelper.getItemName(dirtyName);
		
		if(itemName.equals("Unknown")) {
			sender.sendMessage(ChestShopLogger.PREFIX + "There is no item, called " + dirtyName + "!");
			return;
		}
		
		List<ShopModel> shops;
		
		switch(action) {
		case "sell":
			shops = ShopModel.findShops(plugin, ShopModel.SELLACTION, itemName);			
			break;
		default:
			shops = ShopModel.findShops(plugin, ShopModel.BUYACTION, itemName);
			break;
		}
		
		sender.sendMessage(ChatColor.DARK_GREEN + "========== Search results ==========");
		sender.sendMessage(ChatColor.DARK_GRAY + "ID | Owner | Price | Price per item | Max. amount");
		for(ShopModel shop : shops) {
			String msg = ChatColor.GREEN + "" + shop.getID();
			msg = msg + ChatColor.DARK_GRAY + " | " + ChatColor.GRAY + shop.getOwner();
			switch(action) {
			case "sell":
				msg = msg + ChatColor.DARK_GRAY + " | " + ChatColor.GRAY + shop.getSellPrice();
				msg = msg + ChatColor.DARK_GRAY + " | " + ChatColor.GRAY + MathHelper.round(shop.getSellPrice() / shop.getMaxAmount(), 2);
				break;
			default:
				msg = msg + ChatColor.DARK_GRAY + " | " + ChatColor.GRAY + shop.getBuyPrice();
				msg = msg + ChatColor.DARK_GRAY + " | " + ChatColor.GRAY + MathHelper.round(shop.getBuyPrice() / shop.getMaxAmount(), 2);
				break;
			}
			msg = msg + ChatColor.DARK_GRAY + " | " + ChatColor.GRAY + shop.getMaxAmount();
			sender.sendMessage(msg);
		}
		
	}
	
}
