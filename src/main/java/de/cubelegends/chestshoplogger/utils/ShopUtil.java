package de.cubelegends.chestshoplogger.utils;

import org.bukkit.inventory.ItemStack;

import com.Acrobot.Breeze.Utils.MaterialUtil;

public class ShopUtil {

	public static String getItemName(String dirtyName) {
		String itemName = "Unknown";
		ItemStack itemStack = MaterialUtil.getItem(dirtyName);		
		if(itemStack != null) {
			itemName = MaterialUtil.getName(itemStack, true);
		}		
		return itemName;
	}
	
}
