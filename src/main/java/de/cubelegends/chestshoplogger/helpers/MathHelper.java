package de.cubelegends.chestshoplogger.helpers;

public class MathHelper {
	
	public static double round(double d, int decimals) {
		
		int factor = 1;
		
		for(int i = 0; i < decimals; i++) {
			factor = factor * 10;
		}
		
		if (d == 0) {
			return (double) 0;
		}
		
        return (double) Math.round(d * factor) / factor;
	}

}
