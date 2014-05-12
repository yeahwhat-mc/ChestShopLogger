package de.cubelegends.chestshoplogger;

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
		
		return false;
	}

}
