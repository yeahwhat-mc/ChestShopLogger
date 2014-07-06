package de.cubelegends.chestshoplogger.models;

import java.sql.Connection;
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
		
		if(!shop.exists()) {
			ShopModel.create(plugin, event.getSign(), event.getClient());
			shop = new ShopModel(plugin, loc);
		}

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
		
		try {
				
			Connection con = plugin.getDBHandler().open();
			PreparedStatement st = con.prepareStatement("INSERT INTO chestshop_transaction (shopid, clientuuid, type, amount, price, date) VALUES(?, ?, ?, ?, ?, ?)");
			st.setInt(1, shop.getID());
			st.setString(2, event.getClient().getUniqueId().toString());
			st.setString(3, type);
			st.setInt(4, amount);
			st.setDouble(5, price);
			st.setLong(6, date);
			st.execute();
			st.close();
			con.close();
				
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}

}
