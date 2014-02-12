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
			PreparedStatement st = db.getConnection().prepareStatement("INSERT INTO chestshop_shop (coordx, coordy, coordz, world, player, playeruid, amount, buyprice, sellprice, item, date) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
			st.setInt(1, coordX);
			st.setInt(2, coordY);
			st.setInt(3, coordZ);
			st.setString(4, world);
			st.setString(5, player);
			st.setString(6, playerUID);
			st.setInt(7, amount);
			st.setDouble(8, buyPrice);
			st.setDouble(9, sellPrice);
			st.setString(10, item);
			st.setLong(11, date);
			st.execute();
			st.close();
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
			PreparedStatement st = db.getConnection().prepareStatement("DELETE FROM chestshop_shop WHERE id = ?");
			st.setInt(1, shopID);
			st.execute();
			st.close();
			st = db.getConnection().prepareStatement("DELETE FROM chestshop_transaction WHERE shopid = ?");
			st.setInt(1, shopID);
			st.execute();
			st.close();
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
				PreparedStatement st = db.getConnection().prepareStatement("INSERT INTO chestshop_transaction (shopid, client, type, price, date) VALUES(?, ?, ?, ?, ?)");
				st.setInt(1, shopID);
				st.setString(2, client);
				st.setString(3, type);
				st.setDouble(4, price);
				st.setLong(5, date);
				st.execute();
				st.close();
				db.closeConnection();
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
		
	}
	
	private int getShopID(int coordX, int coordY, int coordZ) {
		int id = 0;
		
		try {			
			PreparedStatement st = db.getConnection().prepareStatement("SELECT id FROM chestshop_shop WHERE coordx = ? AND coordy = ? AND coordz = ?");
			st.setInt(1, coordX);
			st.setInt(2, coordY);
			st.setInt(3, coordZ);
			ResultSet rs = st.executeQuery();
			if(rs.next()) {
				id = rs.getInt("id");
			}
			st.close();
			db.closeConnection();
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		
		return id;
	}

}
