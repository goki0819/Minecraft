package util.world;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class World {
	
	private Map<String,Region> regions=new HashMap<String, Region>();
	private File regionDir;
	
	public World(File regionDir) throws IOException {
		this.regionDir=regionDir;
	}
	
	/**
	 * @param x (0~512)
	 * @param y (0~256)
	 * @param z (0~512)
	 * @return
	 * @throws IOException 
	 */
	public Block getBlock(int x, int y, int z) throws IOException {
		int rx = (x/512) + (x<0 ? -1 : 0);
		int rz = (z/512) + (z<0 ? -1 : 0);
		String key=rx+"_"+rz;
		
		if(regions.containsKey(key)) {
			Region region=regions.get(key);
			
			if(region==null)return Block.AIR;
			
			return regions.get(key).getBlock(x-rx*512, y, z-rz*512);
		}
		
		Region region=Region.loadRegion(regionDir, rx, rz);
		regions.put(key, region);
		
		if(region==null)return Block.AIR;
		
		System.err.println("chunk["+key+"] loaded.");
		return region.getBlock(x-rx*512, y, z-rz*512);
	}
}
