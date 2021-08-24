package test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import util.Node;
import util.nbt.NBTElement;
import util.nbt.NBTTokenizer;

public class Test {
	public static void main(String[] args) {test1();}
	
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
	public static void test2() {
		String region="region/r.0.0.mca";
		
		String url=region;
		File infile=new File("D:\\minecraft\\saves\\Survival\\"+url);
		
		try {
			FileInputStream fis=new FileInputStream(infile);
			byte[] Blocks=new byte[4096];
			fis.read(Blocks);
			byte[] Add=new byte[2048];
			fis.read(Add);
			byte[] Data=new byte[2048];
			fis.read(Data);
			byte[] BlockLight=new byte[2048];
			fis.read(BlockLight);
			byte[] SkyLight=new byte[2048];
			fis.read(SkyLight);
			
			for(int x=0;x<16;x++) {
				int y=x, z=x;
				int BlockPos = y*16*16 + z*16 + x;
				byte BlockID_a = Blocks[BlockPos];
				byte BlockID_b = Nibble4(Add, BlockPos);
				short BlockID = (short)((BlockID_b << 8) | (BlockID_a & 0xff));//ByteBuffer.wrap(new byte[] {BlockID_b,BlockID_a}).getShort();
				byte BlockData = Nibble4(Data, BlockPos);
				byte Blocklight = Nibble4(BlockLight, BlockPos);
				byte Skylight = Nibble4(SkyLight, BlockPos);
				
				System.out.println("["+x+","+y+","+z+"] "+BlockID+" "+String.format("%02X", BlockID_b)+String.format("%02X", BlockID_a));
			}
			fis.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static byte Nibble4(byte[] arr, int index){
		return (byte) (index%2 == 0 ? (arr[index/2]&0x0F) : ((arr[index/2]>>4)&0x0F));
	}
}
