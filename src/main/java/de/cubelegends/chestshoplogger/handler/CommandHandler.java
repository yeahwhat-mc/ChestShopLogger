package de.cubelegends.chestshoplogger.handler;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.Acrobot.Breeze.Utils.MaterialUtil;

import de.cubelegends.chestshoplogger.ChestShopLogger;
import de.cubelegends.chestshoplogger.models.ShopModel;

public class CommandHandler implements CommandExecutor {
	
	private ChestShopLogger plugin;
	
	private final String PREFIX = ChatColor.DARK_GREEN + "[CSL] " + ChatColor.GRAY;
	
	public CommandHandler(ChestShopLogger plugin) {
		this.plugin = plugin;
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if(args.length == 2 && args[0].equalsIgnoreCase("tp")) {
			this.tp(sender, args[1]);
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
		
		
		if(!sender.hasPermission("csl.tp") && !sender.isOp()) {
			sender.sendMessage(PREFIX + "You don't have enough permissions to do this!");
			return;
		}
		
		try {
			id = Integer.parseInt(idStr);
		} catch (NumberFormatException e) {
			sender.sendMessage(PREFIX + "You've entered an invalid id!");
			return;
		}
		
		ShopModel shop = new ShopModel(plugin, id);
		
		if(shop.getID() == -1) {
			sender.sendMessage(PREFIX + "There is no shop with the id " + id + "!");
			return;
		}

		Player player = (Player) sender;
		player.teleport(shop.getTP());
		sender.sendMessage(PREFIX + "Welcome to shop " + id + "!");
		
	}
	
	private void find(CommandSender sender, String action, String itemName) {
		if(!sender.hasPermission("csl.find") && !sender.isOp()) {
			sender.sendMessage(PREFIX + "You don't have enough permissions to do this!");
			return;
		}
		
		ItemStack itemStack = MaterialUtil.getItem(itemName);
		
		if(itemStack == null) {
			sender.sendMessage(PREFIX + "There is no item, called " + itemName + "!");
			return;
		}
		
		itemName = MaterialUtil.getSignName(itemStack);
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
		sender.sendMessage(ChatColor.DARK_GRAY + "ID | Owner | Price | Amount");
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
			msg = msg + " | " + shop.getAmount();
			sender.sendMessage(msg);
		}
		
	}

}
