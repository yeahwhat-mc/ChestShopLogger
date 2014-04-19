package de.cubelegends.chestshoplogger.models;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import com.Acrobot.ChestShop.Events.TransactionEvent;
import com.Acrobot.ChestShop.Events.TransactionEvent.TransactionType;

import de.cubelegends.chestshoplogger.ChestShopLogger;

public class TransactionModel {
	
	public static void create(ChestShopLogger plugin, TransactionEvent event) {
		Location loc = event.getSign().getLocation();
		ShopModel shop = new ShopModel(plugin, loc);
		
		String type = "unknown";
		if(event.getTransactionType().equals(TransactionType.BUY)) {
			type = "buy";
		} else if(event.getTransactionType().equals(TransactionType.SELL)) {
			type = "sell";
		}
		double price = event.getPrice();
		long date = System.currentTimeMillis();
		
		int amount = 0;
		for(ItemStack itemStack : event.getStock()) {
			amount = amount + itemStack.getAmount();
		}
		
		if(shop.getID() != 0) {
			try {			
				PreparedStatement st = plugin.getDBHandler().getConnection().prepareStatement("INSERT INTO chestshop_transaction (shopid, client, clientuuid, type, amount, price, date) VALUES(?, ?, ?, ?, ?, ?, ?)");
				st.setInt(1, shop.getID());
				st.setString(2, event.getClient().getName());
				st.setString(3, event.getClient().getUniqueId().toString());
				st.setString(4, type);
				st.setInt(5, amount);
				st.setDouble(6, price);
				st.setLong(7, date);
				st.execute();
				st.close();
				plugin.getDBHandler().closeConnection();
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
	}

}
