package de.cubelegends.chestshoplogger.handler;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.cubelegends.chestshoplogger.ChestShopLogger;

public class CommandHandler implements CommandExecutor {
	
	private ChestShopLogger plugin;
	private DBHandler db;
	
	private final String PREFIX = ChatColor.GREEN + "[CSL] " + ChatColor.GRAY;
	
	public CommandHandler(ChestShopLogger plugin) {
		this.plugin = plugin;
		this.db = plugin.getDBHandler();
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
		
		Location loc = null;
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
		
		try {
			PreparedStatement st = db.getConnection().prepareStatement("SELECT world, coordx, coordy, coordz FROM chestshop_shop WHERE id = ?");
			st.setInt(1, id);
			ResultSet rs = st.executeQuery();
			
			if(rs.next()) {
				loc = new Location(
						plugin.getServer().getWorld(rs.getString("world")),
						rs.getInt("coordx") + 0.5,
						rs.getInt("coordy"),
						rs.getInt("coordz") + 0.5
						);
			} else {
				player.sendMessage(PREFIX + "There is no shop with the id " + id + "!");
				return;
			}
			
			rs.close();
			st.close();
			db.closeConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		player.teleport(loc);
		player.sendMessage(PREFIX + "Welcome to shop " + id + "!");
		
	}

}
