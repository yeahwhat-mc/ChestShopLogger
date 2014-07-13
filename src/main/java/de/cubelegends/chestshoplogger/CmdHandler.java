package de.cubelegends.chestshoplogger;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.cubelegends.chestshoplogger.managers.ShopManager;

public class CmdHandler implements CommandExecutor {
	
	private ChestShopLogger plugin;
	
	public CmdHandler(ChestShopLogger plugin) {
		this.plugin = plugin;
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		ShopManager sm = new ShopManager(plugin);
		
		if(sender instanceof Player) {
			
			Player player = (Player) sender;
			
			if(args.length == 2 && args[0].equalsIgnoreCase("tp")) {
				sm.tp(player, args[1]);
				return true;			
			}
			
		}
		
		if(args.length == 2 && args[0].equalsIgnoreCase("coords")) {
			sm.coords(sender, args[1]);
			return true;			
		}
		
		if(args.length == 3 && args[0].equalsIgnoreCase("find")) {
			sm.find(sender, args[1], args[2]);
			return true;			
		}

        help(sender);
		return true;
	}

    private void help(CommandSender sender) {

        if(sender.hasPermission("chestshoplogger.find")) {
            sender.sendMessage(ChatColor.GREEN + "/shop find buy <item> " + ChatColor.GRAY + "- Search for buyable items");
            sender.sendMessage(ChatColor.GREEN + "/shop find sell <item> " + ChatColor.GRAY + "- Search for sellable items");
            sender.sendMessage(ChatColor.GREEN + "/shop find player <player> " + ChatColor.GRAY + "- Search for player shops");
        }

        if(sender.hasPermission("chestshoplogger.tp")) {
            sender.sendMessage(ChatColor.GREEN + "/shop tp <id> " + ChatColor.GRAY + "- Teleport to a shop");
        }

        if(sender.hasPermission("chestshoplogger.coords")) {
            sender.sendMessage(ChatColor.GREEN + "/shop coords <id> " + ChatColor.GRAY + "- Display coordinates of a shop");
        }

    }

}
