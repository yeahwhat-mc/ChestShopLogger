package de.cubelegends.chestshoplogger.cmds;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.cubelegends.chestshoplogger.ChestShopLogger;
import de.cubelegends.chestshoplogger.models.ShopModel;
import de.cubelegends.chestshoplogger.utils.ShopUtil;

public class ShopCmd implements CommandExecutor {
	
	private ChestShopLogger plugin;
	
	public ShopCmd(ChestShopLogger plugin) {
		this.plugin = plugin;
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if(args.length == 2 && args[0].equalsIgnoreCase("tp")) {
			this.tp(sender, args[1]);
			return true;			
		}
		
		if(args.length == 2 && args[0].equalsIgnoreCase("coord")) {
			this.coord(sender, args[1]);
			return true;			
		}
		
		if(args.length == 3 && args[0].equalsIgnoreCase("find")) {
			this.find(sender, args[1], args[2]);
			return true;			
		}
		
		return false;
	}
	
	private void tp(CommandSender sender, String idStr) {
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
		
		if(shop.getID() == ShopModel.IDNOTFOUND) {
			sender.sendMessage(ChestShopLogger.PREFIX + "There is no shop with the id " + id + "!");
			return;
		}

		Player player = (Player) sender;
		player.teleport(shop.getTP());
		sender.sendMessage(ChestShopLogger.PREFIX + "Welcome to shop " + id + "!");
		
	}
	
	private void coord(CommandSender sender, String idStr) {
		int id = 0;
		
		if(!(sender instanceof Player)) {
			sender.sendMessage("This command can only be executed by a player!");
			return;
		}
		
		if(!sender.hasPermission("chestshoplogger.coord") && !sender.isOp()) {
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
		
		if(shop.getID() == ShopModel.IDNOTFOUND) {
			sender.sendMessage(ChestShopLogger.PREFIX + "There is no shop with the id " + id + "!");
			return;
		}

		Location loc = shop.getLoc();
		sender.sendMessage(ChestShopLogger.PREFIX + "Shop " + id + " is located in " + loc.getWorld() + " at " + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ() + ".");
		
	}
	
	private void find(CommandSender sender, String action, String dirtyName) {
		if(!sender.hasPermission("chestshoplogger.find") && !sender.isOp()) {
			sender.sendMessage(ChestShopLogger.PREFIX + "You don't have enough permissions to do this!");
			return;
		}
		
		String itemName = ShopUtil.getItemName(dirtyName);
		
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
		sender.sendMessage(ChatColor.DARK_GRAY + "ID | Owner | Price | Max. amount");
		for(ShopModel shop : shops) {
			String msg = ChatColor.GREEN + "" + shop.getID() + ChatColor.GRAY + " | " + shop.getOwner() + " | ";
			switch(action) {
			case "sell":
				msg = msg + shop.getSellPrice();		
				break;
			default:
				msg = msg + shop.getBuyPrice();
				break;
			}
			msg = msg + " | " + shop.getMaxAmount();
			sender.sendMessage(msg);
		}
		
	}

}
