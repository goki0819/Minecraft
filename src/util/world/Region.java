package util.world;

import java.io.File;
import java.io.IOException;

public class Region {
	
	/**
	 * 32*1*32 Chunk
	 */
	private ChunkData[] chunks;
	
	private Region(File file) throws IOException {
		this.chunks=ChunkData.getChunks(file);
	}
	
	/**
	 * @param x (0~512)
	 * @param y (0~256)
	 * @param z (0~512)
	 * @return
	 */
	public Block getBlock(int x, int y, int z) {
		return chunks[z/16*32+x/16].getBlock(x%16, y, z%16);
	}
	public ChunkData[] getChunks() {
		return chunks;
	}
	
	public static Region loadRegion(File file,int x,int z) throws IOException {
		File f=new File(file.getPath()+"/r."+x+"."+z+".mca");
		
		if(!f.exists())return null;
		
		System.err.println("Region file:"+f.getPath()+" loaded.");
		return new Region(f);
	}
}
