package test;

import java.io.File;

import util.nbt.NBTElement;
import util.world.ChunkData;
import util.world.Region;
import util.world.World;

public class Test {
	public static void main(String[] args) {test2();}
	
	public static void test1() {
		String region="region/r.0.0.mca";
		File infile=new File("D:\\minecraft\\saves\\Survival\\"+region);
		try {
			World world=new World(infile);
			for(int x=100;x<132;x++) {
				for(int z=100;z<132;z++) {
					System.out.print(world.getBlock(x, 65, z).getID()+" ");
				}
				System.out.println();
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static byte Nibble4(byte[] arr, int index){
		return (byte) (index%2 == 0 ? (arr[index/2]&0x0F) : ((arr[index/2]>>4)&0x0F));
	}
	public static void test2() {
		String regionName="region";
		File infile=new File("D:\\minecraft\\saves\\Survival\\"+regionName);
		
		try {
			World world=new World(infile);
			Region region=world.getRegion(0, 0);
			ChunkData[] chunks=region.getChunks();
			
			NBTElement.show(chunks[0].getNBTInfo());
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
}
