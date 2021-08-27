package util.world;

import java.util.Map;

public class Block{
	
	private BlockData data;
	private byte blockLight, skyLight;
	
	public Block(BlockData data, byte blockLight, byte skyLight) {
		this.data=data;
		this.blockLight=blockLight;
		this.skyLight=skyLight;
	}
	
	public String getID() {return data.getID();}
	public Map<String, String> getProperties() {return data.getProperties();}
	public byte getBlockLight() {return blockLight;}
	public byte getSkyLight() {return skyLight;}
	
	public String toString() {
		return data+" ,BlockLight:"+blockLight+" ,SkyLight:"+skyLight;
	}
}
