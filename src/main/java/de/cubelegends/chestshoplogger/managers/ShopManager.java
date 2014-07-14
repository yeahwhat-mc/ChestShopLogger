package de.cubelegends.chestshoplogger.managers;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.Acrobot.Breeze.Utils.MaterialUtil;

import de.cubelegends.chestshoplogger.ChestShopLogger;
import de.cubelegends.chestshoplogger.models.PlayerModel;
import de.cubelegends.chestshoplogger.models.ShopModel;

public class ShopManager {

private ChestShopLogger plugin;
	
	public ShopManager(ChestShopLogger plugin) {
		this.plugin = plugin;
	}
	
	public void tp(Player player, String idStr) {
		
		int id;
		
		if(!player.hasPermission("chestshoplogger.tp")) {
			player.sendMessage(ChestShopLogger.PREFIX + "You don't have enough permissions to do this!");
			return;
		}
		
		try {
			id = Integer.parseInt(idStr);
		} catch (NumberFormatException e) {
			player.sendMessage(ChestShopLogger.PREFIX + "You've entered an invalid id!");
			return;
		}
		
		ShopModel shop = new ShopModel(plugin, id);
		
		if(!shop.exists()) {
			player.sendMessage(ChestShopLogger.PREFIX + "There is no shop " + getIDString(id) + "!");
			return;
		}
		
		Block blockAbove = new Location(shop.getTP().getWorld(), shop.getTP().getX(), shop.getTP().getY() + 1, shop.getTP().getZ()).getBlock();
		if(shop.getTP().getBlock().getType().isSolid() || blockAbove.getType().isSolid()) {
			player.sendMessage(ChestShopLogger.PREFIX + "The destination is obstructed!");
			return;
		}

		player.teleport(shop.getTP());
		player.sendMessage(ChestShopLogger.PREFIX + "Welcome to shop " + getIDString(id) + "!");
		
	}
	
	public void coords(CommandSender sender, String idStr) {
		
		int id;
		
		if(!sender.hasPermission("chestshoplogger.coords")) {
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
		
		if(!shop.exists()) {
			sender.sendMessage(ChestShopLogger.PREFIX + "There is no shop " + getIDString(id) + "!");
			return;
		}

		Location loc = shop.getLoc();
		sender.sendMessage(ChestShopLogger.PREFIX + "Shop " + getIDString(id) + " is located in " + loc.getWorld().getName() + " at " + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ() + ".");
		
	}
	
	public void find(CommandSender sender, String option, String value) {
		
		if(!sender.hasPermission("chestshoplogger.find")) {
			sender.sendMessage(ChestShopLogger.PREFIX + "You don't have enough permissions to do this!");
			return;
		}

		String itemName;
		List<ShopModel> shops = null;
		
		switch(option) {
		
		case "sell":
			
			itemName = getItemName(value);
			
			if(itemName.equals("Unknown")) {
				sender.sendMessage(ChestShopLogger.PREFIX + "There is no item, called " + value + "!");
				return;
			}
			
			shops = ShopModel.getShopsWitchOfferSell(plugin, itemName);
			sender.sendMessage(ChatColor.DARK_GREEN + "========== Sell " + itemName + " ==========");
			break;
			
		case "buy":
			
			itemName = getItemName(value);
			
			if(itemName.equals("Unknown")) {
				sender.sendMessage(ChestShopLogger.PREFIX + "There is no item, called " + value + "!");
				return;
			}
			
			shops = ShopModel.getShopsWitchOfferBuy(plugin, itemName);
			sender.sendMessage(ChatColor.DARK_GREEN + "========== Buy " + itemName + " ==========");
			break;
			
		case "player":
			
			if(!PlayerModel.exists(plugin, value)) {
				sender.sendMessage(ChestShopLogger.PREFIX + "The player " + value + " isn't known by the plugin!");
				return;
			}
			
			PlayerModel player = new PlayerModel(plugin, PlayerModel.getUUID(plugin, value));
			
			shops = ShopModel.getShopsByPlayer(plugin, player.getUUID());
			sender.sendMessage(ChatColor.DARK_GREEN + "========== Shops from " + player.getName() + " ==========");
			break;
			
		}
		
		if(shops == null) {
			sender.sendMessage(ChestShopLogger.PREFIX + "\"" + option + "\" is not a valid search option!");
			return;
		}
		
		if(shops.size() == 0) {
			sender.sendMessage(ChatColor.GRAY + "There were no results, sorry! :(");
		}
		
		for(ShopModel shop : shops) {
			
			PlayerModel playerModel = new PlayerModel(plugin, shop.getOwnerUUID());
			String msg = "";
			switch(option) {
			
			case "sell":
				msg = msg + ChatColor.GRAY + "Sell " + ChatColor.GREEN + shop.getMaxAmount() + "x ";
				msg = msg + ChatColor.GRAY + "for " + ChatColor.GREEN + shop.getSellPrice() + " ";
				if(playerModel.exists())
					msg = msg + ChatColor.GRAY + "to " + ChatColor.GREEN + playerModel.getName() + " ";
				msg = msg + ChatColor.GRAY + "at " + getIDString(shop.getID());
				break;
				
			case "buy":
				msg = msg + ChatColor.GRAY + "Buy " + ChatColor.GREEN + shop.getMaxAmount() + "x ";
				msg = msg + ChatColor.GRAY + "for " + ChatColor.GREEN + shop.getBuyPrice() + " ";
				if(playerModel.exists())
					msg = msg + ChatColor.GRAY + "from " + ChatColor.GREEN + playerModel.getName() + " ";
				msg = msg + ChatColor.GRAY + "at " + getIDString(shop.getID());
				break;
				
			case "player":
				msg = msg + ChatColor.GRAY + "The player owns a " + ChatColor.GREEN + shop.getItemName() + " shop ";
				msg = msg + ChatColor.GRAY + "with the id " + getIDString(shop.getID());
				break;
				
			}
			
			sender.sendMessage(msg);
			
		}
		
	}
	
	public static String getItemName(String dirtyName) {
		String itemName = "Unknown";
		ItemStack itemStack = MaterialUtil.getItem(dirtyName);		
		if(itemStack != null) {
			itemName = MaterialUtil.getName(itemStack, true);
		}		
		return itemName;
	}
	
	private String getIDString(int id) {
		return ChatColor.YELLOW + "#" + id + ChatColor.GRAY + "";
	}
	
}
