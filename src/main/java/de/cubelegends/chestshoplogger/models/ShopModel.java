package de.cubelegends.chestshoplogger.models;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.Acrobot.Breeze.Utils.PriceUtil;
import com.Acrobot.ChestShop.Events.ShopCreatedEvent;
import com.Acrobot.ChestShop.Events.ShopDestroyedEvent;

import de.cubelegends.chestshoplogger.ChestShopLogger;
import de.cubelegends.chestshoplogger.db.DBHandler;
import de.cubelegends.chestshoplogger.utils.ShopUtil;

public class ShopModel {

	public final static int IDNOTFOUND = 0;
	public final static int BUYACTION = 0;
	public final static int SELLACTION = 1;
	
	private ChestShopLogger plugin;
	private DBHandler db;
	
	private int id = IDNOTFOUND;
	private Location loc;
	private Location tp;
	private String owner;
	private UUID ownerUUID;
	private int maxAmount;
	private double buyPrice;
	private double sellPrice;
	private String itemName;
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
	
	public static void create(ChestShopLogger plugin, ShopCreatedEvent event) {
		String world = event.getSign().getWorld().getName();	
		int x = event.getSign().getX();
		int y = event.getSign().getY();
		int z = event.getSign().getZ();
		double tpX = event.getPlayer().getLocation().getX();
		double tpY = event.getPlayer().getLocation().getY();
		double tpZ = event.getPlayer().getLocation().getZ();
		float tpYaw = event.getPlayer().getLocation().getYaw();
		float tpPitch = event.getPlayer().getLocation().getPitch();
		String owner = event.getSignLine((short) 0);
		String ownerUUID = "";
		if(plugin.getServer().getPlayer(owner) != null) {
			Player player = plugin.getServer().getPlayer(owner);
			if(player != null) {
				ownerUUID = player.getUniqueId().toString();
			}
		}
		int maxAmount = Integer.parseInt(event.getSignLine((short) 1));
		double buyPrice = PriceUtil.getBuyPrice(event.getSignLine((short) 2));
		double sellPrice = PriceUtil.getSellPrice(event.getSignLine((short) 2));
		String itemName = ShopUtil.getItemName(event.getSignLine((short) 3));
		long created = System.currentTimeMillis();
		
		try {
			PreparedStatement st = plugin.getDBHandler().getConnection().prepareStatement("INSERT INTO chestshop_shop (world, x, y, z, tpx, tpy, tpz, tpyaw, tppitch, owner, owneruuid, maxamount, buyprice, sellprice, itemname, created) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
			st.setString(1, world);
			st.setInt(2, x);
			st.setInt(3, y);
			st.setInt(4, z);
			st.setDouble(5, tpX);
			st.setDouble(6, tpY);
			st.setDouble(7, tpZ);
			st.setFloat(8, tpYaw);
			st.setFloat(9, tpPitch);
			st.setString(10, owner);
			st.setString(11, ownerUUID);
			st.setInt(12, maxAmount);
			st.setDouble(13, buyPrice);
			st.setDouble(14, sellPrice);
			st.setString(15, itemName);
			st.setLong(16, created);
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
	
	public static List<ShopModel> findShops(ChestShopLogger plugin, int action, String itemName) {
		List<ShopModel> shops = new ArrayList<ShopModel>();
		
		try {
			PreparedStatement st = null;
			switch(action) {
			case BUYACTION:
				st = plugin.getDBHandler().getConnection().prepareStatement("SELECT * FROM chestshop_shop WHERE itemname = ? AND buyprice != -1 ORDER BY buyprice / maxamount ASC");
				break;
			case SELLACTION:
				st = plugin.getDBHandler().getConnection().prepareStatement("SELECT * FROM chestshop_shop WHERE itemname = ? AND sellprice != -1 ORDER BY sellprice / maxamount DESC");
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
			plugin.getDBHandler().closeConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return shops;
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
			tp = new Location(
					plugin.getServer().getWorld(rs.getString("world")),
					rs.getDouble("tpx"),
					rs.getDouble("tpy"),
					rs.getDouble("tpz"),
					rs.getFloat("tpYaw"),
					rs.getFloat("tpPitch")
					);
			owner = rs.getString("owner");
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
			PreparedStatement st = db.getConnection().prepareStatement("UPDATE chestshop_shop SET "
					+ "world = ?,"
					+ "x = ?,"
					+ "y = ?,"
					+ "z = ?,"
					+ "tpx = ?,"
					+ "tpy = ?,"
					+ "tpz = ?,"
					+ "tpyaw = ?,"
					+ "tppitch = ?,"
					+ "owmer = ?,"
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
			st.setString(10, owner);
			if(ownerUUID == null) {
				st.setString(11, "");
			} else {
				st.setString(11, ownerUUID.toString());
			}
			st.setInt(12, maxAmount);
			st.setDouble(13, buyPrice);
			st.setDouble(14, sellPrice);
			st.setString(15, itemName);
			st.setLong(16, created);
			st.setInt(17, id);
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
	
	public Location getTP() {
		return tp;
	}
	
	public String getOwner() {
		return owner;
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
	
	public void setOwner(String owner) {
		this.owner = owner;
		if(plugin.getServer().getPlayer(owner) != null) {
			ownerUUID = plugin.getServer().getPlayer(owner).getUniqueId();
		}
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
