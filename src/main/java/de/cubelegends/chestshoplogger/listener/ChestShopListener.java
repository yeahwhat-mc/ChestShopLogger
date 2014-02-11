package de.cubelegends.chestshoplogger.listener;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.Acrobot.Breeze.Utils.PriceUtil;
import com.Acrobot.ChestShop.Events.ShopCreatedEvent;
import com.Acrobot.ChestShop.Events.ShopDestroyedEvent;
import com.Acrobot.ChestShop.Events.TransactionEvent;
import com.Acrobot.ChestShop.Events.TransactionEvent.TransactionType;

import de.cubelegends.chestshoplogger.ChestShopLogger;
import de.cubelegends.chestshoplogger.handler.DBHandler;

public class ChestShopListener implements Listener {
	
	private ChestShopLogger plugin;
	private DBHandler db;
	
	public ChestShopListener(ChestShopLogger plugin) {
		this.plugin = plugin;
		this.db = plugin.getDBHandler();
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onShopCreate(ShopCreatedEvent e) {
		
		int coordX = e.getSign().getX();
		int coordY = e.getSign().getY();
		int coordZ = e.getSign().getZ();
		String world = e.getSign().getWorld().getName();
		String player = e.getSignLine((short) 0);
		String playerUID = "";
		if(plugin.getServer().getPlayer(player) != null) {
			playerUID = plugin.getServer().getPlayer(player).getUniqueId().toString();
		}
		int amount = Integer.parseInt(e.getSignLine((short) 1));
		double buyPrice = PriceUtil.getBuyPrice(e.getSignLine((short) 2));
		double sellPrice = PriceUtil.getSellPrice(e.getSignLine((short) 2));
		String item = e.getSignLine((short) 3);
		long date = System.currentTimeMillis();
		
		try {
			PreparedStatement ps = db.getConnection().prepareStatement("INSERT INTO chestshop_shop (coordx, coordy, coordz, world, player, playeruid, amount, buyprice, sellprice, item, date) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
			ps.setInt(1, coordX);
			ps.setInt(2, coordY);
			ps.setInt(3, coordZ);
			ps.setString(4, world);
			ps.setString(5, player);
			ps.setString(6, playerUID);
			ps.setInt(7, amount);
			ps.setDouble(8, buyPrice);
			ps.setDouble(9, sellPrice);
			ps.setString(10, item);
			ps.setLong(11, date);
			ps.execute();
			ps.close();
			db.closeConnection();
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onShopDestroy(ShopDestroyedEvent e) {
		
		int coordX = e.getSign().getX();
		int coordY = e.getSign().getY();
		int coordZ = e.getSign().getZ();
		int shopID = this.getShopID(coordX, coordY, coordZ);
		
		try {
			PreparedStatement ps = db.getConnection().prepareStatement("DELETE FROM chestshop_shop WHERE id = ?");
			ps.setInt(1, shopID);
			ps.execute();
			ps.close();
			ps = db.getConnection().prepareStatement("DELETE FROM chestshop_transaction WHERE shopid = ?");
			ps.setInt(1, shopID);
			ps.execute();
			ps.close();
			db.closeConnection();
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onTransaction(TransactionEvent e) {
		
		int coordX = e.getSign().getX();
		int coordY = e.getSign().getY();
		int coordZ = e.getSign().getZ();
		int shopID = this.getShopID(coordX, coordY, coordZ);
		String client = e.getClient().getName();
		String type = "unknown";
		if(e.getTransactionType().equals(TransactionType.BUY)) {
			type = "buy";
		} else if(e.getTransactionType().equals(TransactionType.SELL)) {
			type = "sell";
		}
		double price = e.getPrice();
		long date = System.currentTimeMillis();
		
		if(shopID != 0) {
			try {			
				PreparedStatement ps = db.getConnection().prepareStatement("INSERT INTO chestshop_transaction (shopid, client, type, price, date) VALUES(?, ?, ?, ?, ?)");
				ps.setInt(1, shopID);
				ps.setString(2, client);
				ps.setString(3, type);
				ps.setDouble(4, price);
				ps.setLong(5, date);
				ps.execute();
				ps.close();
				db.closeConnection();
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
		
	}
	
	private int getShopID(int coordX, int coordY, int coordZ) {
		int id = 0;
		
		try {			
			PreparedStatement ps = db.getConnection().prepareStatement("SELECT id FROM chestshop_shop WHERE coordx = ? AND coordy = ? AND coordz = ?");
			ps.setInt(1, coordX);
			ps.setInt(2, coordY);
			ps.setInt(3, coordZ);
			ResultSet rs = ps.executeQuery();
			if(rs.next()) {
				id = rs.getInt("id");
			}
			ps.close();
			db.closeConnection();
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		
		return id;
	}

}
