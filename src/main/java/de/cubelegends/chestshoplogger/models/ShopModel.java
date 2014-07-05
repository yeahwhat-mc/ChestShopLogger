package de.cubelegends.chestshoplogger.models;

import com.Acrobot.Breeze.Utils.PriceUtil;
import com.Acrobot.ChestShop.Events.ShopCreatedEvent;
import com.Acrobot.ChestShop.Events.ShopDestroyedEvent;

import de.cubelegends.chestshoplogger.ChestShopLogger;
import de.cubelegends.chestshoplogger.managers.ShopManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

public class ShopModel {

	public final static int BUYACTION = 0;
	public final static int SELLACTION = 1;
	
	private ChestShopLogger plugin;
	
	private int id;
	private Location loc;
	private Location tp;
	private UUID ownerUUID;
	private int maxAmount;
	private double buyPrice;
	private double sellPrice;
	private String itemName;
	private long created;

	public ShopModel(ChestShopLogger plugin, int id) {
		this.plugin = plugin;
		fetchData(id);
	}
	
	public ShopModel(ChestShopLogger plugin, Location loc) {
		this.plugin = plugin;
		fetchData(loc);
	}
	
	public ShopModel(ChestShopLogger plugin, ResultSet rs) {
		this.plugin = plugin;
		fetchData(rs);
	}
	
	public static void create(ChestShopLogger plugin, ShopCreatedEvent event) {
		create(plugin, event.getSign(), event.getSignLines(), event.getPlayer());
	}
	
	public static void create(ChestShopLogger plugin, Sign sign, Player player) {
		create(plugin, sign, sign.getLines(), player);
	}
        
	public static void create(ChestShopLogger plugin, Sign sign, String[] signLines, Player player) {
		String world = sign.getWorld().getName();	
		int x = sign.getX();
		int y = sign.getY();
		int z = sign.getZ();
		double tpX = player.getLocation().getX();
		double tpY = player.getLocation().getY();
		double tpZ = player.getLocation().getZ();
		float tpYaw = player.getLocation().getYaw();
		float tpPitch = player.getLocation().getPitch();
		String ownerName = signLines[0];
		UUID ownerUUID = PlayerModel.getUUID(plugin, ownerName);
		int maxAmount = Integer.parseInt(signLines[1]);
		double buyPrice = PriceUtil.getBuyPrice(signLines[2]);
		double sellPrice = PriceUtil.getSellPrice(signLines[2]);
		String itemName = ShopManager.getItemName(signLines[3]);
		long created = System.currentTimeMillis();
		
		try {
			
			Connection con = plugin.getDBHandler().open();
			PreparedStatement st = con.prepareStatement("INSERT INTO chestshop_shop (world, x, y, z, tpx, tpy, tpz, tpyaw, tppitch, owneruuid, maxamount, buyprice, sellprice, itemname, created) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
			st.setString(1, world);
			st.setInt(2, x);
			st.setInt(3, y);
			st.setInt(4, z);
			st.setDouble(5, tpX);
			st.setDouble(6, tpY);
			st.setDouble(7, tpZ);
			st.setFloat(8, tpYaw);
			st.setFloat(9, tpPitch);
			if(ownerUUID != null) st.setString(10, ownerUUID.toString());
			else st.setString(10, null);
			st.setInt(11, maxAmount);
			st.setDouble(12, buyPrice);
			st.setDouble(13, sellPrice);
			st.setString(14, itemName);
			st.setLong(15, created);
			st.execute();
			st.close();
			con.close();
			
		} catch (SQLException ex) {
			ex.printStackTrace();
		}		
	}
	
	public static void delete(ChestShopLogger plugin, ShopDestroyedEvent e) {
		ShopModel shop = new ShopModel(plugin, e.getSign().getLocation());
		
		try {
			
			Connection con = plugin.getDBHandler().open();
			PreparedStatement st = con.prepareStatement("DELETE FROM chestshop_shop WHERE id = ?");
			st.setInt(1, shop.getID());
			st.execute();
			st.close();
			st = con.prepareStatement("DELETE FROM chestshop_transaction WHERE shopid = ?");
			st.setInt(1, shop.getID());
			st.execute();
			st.close();
			con.close();
			
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}
	
	public static List<ShopModel> findShops(ChestShopLogger plugin, int action, String itemName) {
		List<ShopModel> shops = new ArrayList<ShopModel>();
		
		try {
			
			Connection con = plugin.getDBHandler().open();
			PreparedStatement st = null;
			switch(action) {
			case BUYACTION:
				st = con.prepareStatement("SELECT * FROM chestshop_shop WHERE itemname = ? AND buyprice != -1 ORDER BY buyprice / maxamount ASC");
				break;
			case SELLACTION:
				st = con.prepareStatement("SELECT * FROM chestshop_shop WHERE itemname = ? AND sellprice != -1 ORDER BY sellprice / maxamount DESC");
				break;
			}
			st.setString(1, itemName);
			ResultSet rs = st.executeQuery();
			
			while(rs.next()) {
				ShopModel shop = new ShopModel(plugin, rs);
				shops.add(shop);
			}
			
			rs.close();
			st.close();
			con.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return shops;
	}
	
	public void fetchData(int id) {
		try {

			Connection con = plugin.getDBHandler().open();
			PreparedStatement st = con.prepareStatement("SELECT * FROM chestshop_shop WHERE id = ?");
			st.setInt(1, id);
			ResultSet rs = st.executeQuery();
			if(rs.next()) {
				fetchData(rs);
			}
			rs.close();
			st.close();
			con.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void fetchData(Location loc) {
		try {
			
			Connection con = plugin.getDBHandler().open();
			PreparedStatement st = con.prepareStatement("SELECT * FROM chestshop_shop WHERE world = ? AND x = ? AND y = ? AND z = ?");
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
			con.close();
			
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
			tp = new Location(
					plugin.getServer().getWorld(rs.getString("world")),
					rs.getDouble("tpx"),
					rs.getDouble("tpy"),
					rs.getDouble("tpz"),
					rs.getFloat("tpYaw"),
					rs.getFloat("tpPitch")
					);
			try {
				ownerUUID = UUID.fromString(rs.getString("owneruuid"));
			} catch(IllegalArgumentException ex) {
				
			} catch(NullPointerException ex) {
				
			}
			maxAmount = rs.getInt("maxAmount");
			buyPrice = rs.getDouble("buyprice");
			sellPrice = rs.getDouble("sellprice");
			itemName = rs.getString("itemname");
			created = rs.getLong("created");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void pushData() {
		try {
			
			Connection con = plugin.getDBHandler().open();
			PreparedStatement st = con.prepareStatement("UPDATE chestshop_shop SET "
					+ "world = ?,"
					+ "x = ?,"
					+ "y = ?,"
					+ "z = ?,"
					+ "tpx = ?,"
					+ "tpy = ?,"
					+ "tpz = ?,"
					+ "tpyaw = ?,"
					+ "tppitch = ?,"
					+ "owneruuid = ?,"
					+ "maxamount = ?,"
					+ "buyprice = ?,"
					+ "sellprice = ?,"
					+ "itemname = ?,"
					+ "created = ? WHERE id = ?");
			st.setString(1, loc.getWorld().getName());
			st.setInt(2, loc.getBlockX());
			st.setInt(3, loc.getBlockY());
			st.setInt(4, loc.getBlockZ());
			st.setDouble(5, tp.getX());
			st.setDouble(6, tp.getY());
			st.setDouble(7, tp.getZ());
			st.setFloat(8, tp.getYaw());
			st.setFloat(9, tp.getPitch());
			if(ownerUUID == null) {
				st.setString(10, "");
			} else {
				st.setString(10, ownerUUID.toString());
			}
			st.setInt(11, maxAmount);
			st.setDouble(12, buyPrice);
			st.setDouble(13, sellPrice);
			st.setString(14, itemName);
			st.setLong(15, created);
			st.setInt(16, id);
			st.execute();
			st.close();
			con.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public boolean exists() {
		return id != 0;
	}
	
	public int getID() {
		return id;
	}
	
	public Location getLoc() {
		return loc;
	}
	
	public Location getTP() {
		return tp;
	}
	
	public UUID getOwnerUUID() {
		return ownerUUID;
	}
	
	public int getMaxAmount() {
		return maxAmount;
	}
	
	public double getBuyPrice() {
		return buyPrice;
	}
	
	public double getSellPrice() {
		return sellPrice;
	}
	
	public String getItemName() {
		return itemName;
	}
	
	public long getCreated() {
		return created;
	}
	
	public void setLoc(Location loc) {
		this.loc = loc;
	}
	
	public void setTP(Location tp) {
		this.tp = tp;
	}
	
	public void setOwnerUUID(UUID ownerUUID) {
		this.ownerUUID = ownerUUID;
	}
	
	public void setMaxAmount(int maxAmount) {
		this.maxAmount = maxAmount;
	}
	
	public void setBuyPrice(double buyPrice) {
		this.buyPrice = buyPrice;
	}
	
	public void setSellPrice(double sellPrice) {
		this.sellPrice = sellPrice;
	}
	
	public void setItemName(String itemName) {
		this.itemName = itemName;
	}
	
}
