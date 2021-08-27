package test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import util.Node;
import util.nbt.NBTElement;
import util.nbt.NBTTokenizer;
import util.world.ChunkData;
import util.world.SectionData;
import util.world.World;

public class Test {
	public static void main(String[] args) {test2();}
	
	public static void test1() {
		String region="region/r.0.0.mca";
		String level="level.dat";
		String map="data/map_0.dat";
		String playerdata="playerdata/07a8a1c9-0802-4423-8aaf-1ff2e5175c0b.dat";
		
		String url=region;
		File infile=new File("D:\\minecraft\\saves\\Survival\\"+url);
		
		try {
			NBTTokenizer tkn=new NBTTokenizer(infile);
			Node<NBTElement> root=tkn.getRootNode();
			NBTElement.show(root);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static byte Nibble4(byte[] arr, int index){
		return (byte) (index%2 == 0 ? (arr[index/2]&0x0F) : ((arr[index/2]>>4)&0x0F));
	}
	public static void test2() {
		String region="region/r.0.0.mca";
		File infile=new File("D:\\minecraft\\saves\\Survival\\"+region);
//		File infile=new File("D:\\minecraft\\saves\\1_17_1\\"+region);
		
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
}
