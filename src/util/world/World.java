package util.world;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class World {
	
	private ChunkData[] chunks;
	
	public World(File file) throws IOException {
		chunks=ChunkData.getChunks(file);
	}
	public World(InputStream inputStream) throws IOException {
		chunks=ChunkData.getChunks(inputStream);
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
}
