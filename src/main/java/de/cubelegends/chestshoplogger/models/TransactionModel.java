package de.cubelegends.chestshoplogger.models;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.bukkit.Location;

import com.Acrobot.ChestShop.Events.TransactionEvent;
import com.Acrobot.ChestShop.Events.TransactionEvent.TransactionType;

import de.cubelegends.chestshoplogger.ChestShopLogger;

public class TransactionModel {
	
	public static void create(ChestShopLogger plugin, TransactionEvent e) {
		Location loc = e.getSign().getLocation();
		ShopModel shop = new ShopModel(plugin, loc);
		
		String client = e.getClient().getName();
		String type = "unknown";
		if(e.getTransactionType().equals(TransactionType.BUY)) {
			type = "buy";
		} else if(e.getTransactionType().equals(TransactionType.SELL)) {
			type = "sell";
		}
		double price = e.getPrice();
		long date = System.currentTimeMillis();
		
		if(shop.getID() != 0) {
			try {			
				PreparedStatement st = plugin.getDBHandler().getConnection().prepareStatement("INSERT INTO chestshop_transaction (shopid, client, type, price, date) VALUES(?, ?, ?, ?, ?)");
				st.setInt(1, shop.getID());
				st.setString(2, client);
				st.setString(3, type);
				st.setDouble(4, price);
				st.setLong(5, date);
				st.execute();
				st.close();
				plugin.getDBHandler().closeConnection();
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
	}

}
