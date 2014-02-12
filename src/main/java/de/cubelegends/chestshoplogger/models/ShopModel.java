package de.cubelegends.chestshoplogger.models;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.Location;

import com.Acrobot.Breeze.Utils.PriceUtil;
import com.Acrobot.ChestShop.Events.ShopCreatedEvent;
import com.Acrobot.ChestShop.Events.ShopDestroyedEvent;

import de.cubelegends.chestshoplogger.ChestShopLogger;
import de.cubelegends.chestshoplogger.handler.DBHandler;

public class ShopModel {
	
	private ChestShopLogger plugin;
	private DBHandler db;
	
	private int id = -1;
	private Location loc;
	private String owner;
	private String ownerUID;
	private int amount;
	private double buyPrice;
	private double sellPrice;
	private String item;
	private long created;

	public ShopModel(ChestShopLogger plugin, int id) {
		this.plugin = plugin;
		this.db = plugin.getDBHandler();
		
		fetchData(id);
	}
	
	public ShopModel(ChestShopLogger plugin, Location loc) {
		this.plugin = plugin;
		this.db = plugin.getDBHandler();
		
		fetchData(loc);
	}
	
	public ShopModel(ChestShopLogger plugin, ResultSet rs) {
		this.plugin = plugin;
		this.db = plugin.getDBHandler();
		
		fetchData(rs);
	}
	
	public static void create(ChestShopLogger plugin, ShopCreatedEvent e) {
		String world = e.getSign().getWorld().getName();	
		int x = e.getSign().getX();
		int y = e.getSign().getY();
		int z = e.getSign().getZ();
		String owner = e.getSignLine((short) 0);
		String ownerUID = "";
		if(plugin.getServer().getPlayer(owner) != null) {
			ownerUID = plugin.getServer().getPlayer(owner).getUniqueId().toString();
		}
		int amount = Integer.parseInt(e.getSignLine((short) 1));
		double buyPrice = PriceUtil.getBuyPrice(e.getSignLine((short) 2));
		double sellPrice = PriceUtil.getSellPrice(e.getSignLine((short) 2));
		String item = e.getSignLine((short) 3);
		long created = System.currentTimeMillis();
		
		try {
			PreparedStatement st = plugin.getDBHandler().getConnection().prepareStatement("INSERT INTO chestshop_shop (world, x, y, z, owner, owneruid, amount, buyprice, sellprice, item, created) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
			st.setString(1, world);
			st.setInt(2, x);
			st.setInt(3, y);
			st.setInt(4, z);
			st.setString(5, owner);
			st.setString(6, ownerUID);
			st.setInt(7, amount);
			st.setDouble(8, buyPrice);
			st.setDouble(9, sellPrice);
			st.setString(10, item);
			st.setLong(11, created);
			st.execute();
			st.close();
			plugin.getDBHandler().closeConnection();
		} catch (SQLException ex) {
			ex.printStackTrace();
		}		
	}
	
	public static void delete(ChestShopLogger plugin, ShopDestroyedEvent e) {
		ShopModel shop = new ShopModel(plugin, e.getSign().getLocation());
		
		try {
			PreparedStatement st = plugin.getDBHandler().getConnection().prepareStatement("DELETE FROM chestshop_shop WHERE id = ?");
			st.setInt(1, shop.getID());
			st.execute();
			st.close();
			st = plugin.getDBHandler().getConnection().prepareStatement("DELETE FROM chestshop_transaction WHERE shopid = ?");
			st.setInt(1, shop.getID());
			st.execute();
			st.close();
			plugin.getDBHandler().closeConnection();
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}
	
	public void fetchData(int id) {
		try {
			PreparedStatement st = db.getConnection().prepareStatement("SELECT * FROM chestshop_shop WHERE id = ?");
			st.setInt(1, id);
			ResultSet rs = st.executeQuery();
			if(rs.next()) {
				fetchData(rs);
			}
			rs.close();
			st.close();
			db.closeConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void fetchData(Location loc) {
		try {
			PreparedStatement st = db.getConnection().prepareStatement("SELECT * FROM chestshop_shop WHERE world = ? AND x = ? AND y = ? AND z = ?");
			st.setString(1, loc.getWorld().getName());
			st.setInt(2, loc.getBlockX());
			st.setInt(3, loc.getBlockY());
			st.setInt(4, loc.getBlockZ());
			ResultSet rs = st.executeQuery();
			if(rs.next()) {
				fetchData(rs);
			}
			rs.close();
			st.close();
			db.closeConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void fetchData(ResultSet rs) {
		try {
			id = rs.getInt("id");
			loc = new Location(
					plugin.getServer().getWorld(rs.getString("world")),
					rs.getInt("x"),
					rs.getInt("y"),
					rs.getInt("z")
					);
			owner = rs.getString("owner");
			ownerUID = rs.getString("ownerUID");
			amount = rs.getInt("amount");
			buyPrice = rs.getDouble("buyprice");
			sellPrice = rs.getDouble("sellprice");
			item = rs.getString("item");
			created = rs.getLong("created");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void pushData() {
		try {
			PreparedStatement st = db.getConnection().prepareStatement("UPDATE chestshop_shop SET "
					+ "world = ?,"
					+ "x = ?,"
					+ "y = ?,"
					+ "z = ?,"
					+ "owmer = ?,"
					+ "owneruid = ?,"
					+ "amount = ?,"
					+ "buyprice = ?,"
					+ "sellprice = ?,"
					+ "item = ?,"
					+ "created = ? WHERE id = ?");
			st.setString(1, loc.getWorld().getName());
			st.setInt(2, loc.getBlockX());
			st.setInt(3, loc.getBlockY());
			st.setInt(4, loc.getBlockZ());
			st.setString(5, owner);
			st.setString(6, ownerUID);
			st.setInt(7, amount);
			st.setDouble(8, buyPrice);
			st.setDouble(9, sellPrice);
			st.setString(10, item);
			st.setLong(11, created);
			st.setInt(12, id);
			st.execute();
			st.close();
			db.closeConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public int getID() {
		return id;
	}
	
	public Location getLoc() {
		return loc;
	}
	
	public String getOwner() {
		return owner;
	}
	
	public String getOwnerUID() {
		return ownerUID;
	}
	
	public int getAmount() {
		return amount;
	}
	
	public double getBuyPrice() {
		return buyPrice;
	}
	
	public double getSellPrice() {
		return sellPrice;
	}
	
	public String getItem() {
		return item;
	}
	
	public long getCreated() {
		return created;
	}
	
	public void setLoc(Location loc) {
		this.loc = loc;
	}
	
	public void setOwner(String owner) {
		this.owner = owner;
		if(plugin.getServer().getPlayer(owner) != null) {
			ownerUID = plugin.getServer().getPlayer(owner).getUniqueId().toString();
		}
	}
	
	public void setAmount(int amount) {
		this.amount = amount;
	}
	
	public void setBuyPrice(double buyPrice) {
		this.buyPrice = buyPrice;
	}
	
	public void setSellPrice(double sellPrice) {
		this.sellPrice = sellPrice;
	}
	
	public void setItem(String item) {
		this.item = item;
	}
	
}
