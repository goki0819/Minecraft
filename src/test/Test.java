package test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;

import util.Node;
import util.nbt.NBTElement;
import util.nbt.NBTTokenizer;
import util.nbt.value.ByteArray;
import util.nbt.value.LongArray;
import util.nbt.value.NumberValue;
import util.world.ChunkData;

public class Test {
	public static void main(String[] args) {test3();}
	
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
		String region="region/r.0.1.mca";
		File infile=new File("D:\\minecraft\\saves\\1_17_1\\"+region);
		
		try {
			FileInputStream fis=new FileInputStream(infile);
			List<ChunkData> chunks=ChunkData.getChunks(fis);
			
			//NBTElement.show(chunks.get(2).getNBTInfo());
			for(ChunkData c:chunks) {
				List<Node<NBTElement>> level=NBTElement.sort(c.getNBTInfo(), "10/10[Level]");
				level:for(Node<NBTElement> e:level){
					NumberValue x=(NumberValue) NBTElement.sort(e, "[xPos]").get(0).getElement().getValue();//from origin
					NumberValue z=(NumberValue) NBTElement.sort(e, "[zPos]").get(0).getElement().getValue();
					List<Node<NBTElement>> sections=NBTElement.sort(e, "9[Sections]/10");
					
					for(Node<NBTElement> s:sections) {
						NumberValue y=(NumberValue) NBTElement.sort(s, "[Y]").get(0).getElement().getValue();
						List<Node<NBTElement>> palettes=NBTElement.sort(s, "9[Palette]/10");
						List<Node<NBTElement>> blockStatesList=NBTElement.sort(s, "12[BlockStates]");
						List<Node<NBTElement>> blockLightList=NBTElement.sort(s, "12[BlockLight]");
						List<Node<NBTElement>> skyLightList=NBTElement.sort(s, "12[SkyLight]");
						int blockPos=y.getValue().byteValue()*16*16 + z.getValue().byteValue()*16 + x.getValue().byteValue();
						LongArray blockStates=null;
						ByteArray blockLight=null, skyLight=null;
						
						if(blockStatesList.size()>0)blockStates=(LongArray) blockStatesList.get(0).getElement().getValue();
						if(blockLightList.size()>0)blockLight=(ByteArray) blockLightList.get(0).getElement().getValue();
						if(skyLightList.size()>0)skyLight=(ByteArray) skyLightList.get(0).getElement().getValue();
						
						if(palettes.size()>0)System.out.println(palettes.size());
						
						for(Node<NBTElement> e2:palettes){
							String name=NBTElement.sort(e2, "8[Name]").get(0).getElement().getValue().toString();
							List<NBTElement> properties=Node.getElements(NBTElement.sort(e2, "10[Properties]/8"));
							
							System.out.println(name);
							properties.forEach((p)->{System.out.println(p.getName()+" "+p.getValue());});
						}
						if(blockStates!=null) {
							int n=(int) Math.ceil(Math.log(palettes.size()) / Math.log(2));
							System.out.println(palettes.size()+" "+n);
							byte[] bs_bin=blockStates.getValue();
							long[] bs_long=blockStates.getLongValue();
							for(int i=0;i<bs_long.length;i++) {
								byte[] b=new byte[8];
								System.arraycopy(bs_bin, i*8, b, 0, 8);
								int[] ids=null;
								
								if(bs_long.length==342) {
									ids=new int[12];
									
									ids[0]=(b[0]<<1 & 0x1E) | (b[1] >> 7 & 0x1);
									ids[1]=(b[1] >> 2&0x1F);
									ids[2]=(b[1]<<3 & 0x18) | (b[2] >> 5 & 0x5);
									ids[3]=(b[2] & 0x1F);
									ids[4]=(b[3]>>3 & 0x1F);
									ids[5]=(b[3]<<2 & 0x1C) | (b[4] >> 6 & 0x3);
									ids[6]=(b[4]>>1 & 0x1F);
									ids[7]=(b[4]<<4 & 0x10) | (b[5] >> 4 & 0xF);
									ids[8]=(b[5]<<1 & 0x1E) | (b[6] >> 7 & 0x1);
									ids[9]=(b[6]>>2 & 0x1F);
									ids[10]=(b[6]<<3 & 0x18) | (b[7] >> 5 & 0x5);
									ids[11]=(b[7] & 0x1F);
								}else if(bs_long.length==256){
									ids=new int[16];
									for(int j=0;j<8;j++) {
										ids[j*2]=b[j]>>4&0xF;
										ids[j*2+1]=b[j]&0xF;
									}
								}else {
//									System.out.println(palettes.size()+" "+blockStates.getValue().length +" "+blockPos);
//									System.out.print("["+i+"]_"+bs[i]+", "+b[0]+" "+b[1]+" "+b[2]+" "+b[3]+" "+b[4]+" "+b[5]+" "+b[6]+" "+b[7]+"        ");
//									for(int j=0;j<ids.length;j++)System.out.print(ids[j]+" ");
//									System.out.println();
									throw new IOException("illegal length. "+bs_long.length);
								}
								
//								System.out.println(palettes.size()+" "+blockStates.getValue().length +" "+blockPos);
								
								System.out.print("["+i+"]_"+bs_long[i]+"        ");
								for(int j=0;j<ids.length;j++)System.out.print(ids[j]+" ");
								System.out.println();
							}
						}
						if(blockLight!=null)System.out.println(Nibble4(blockLight.getValue(), blockPos));
						if(skyLight!=null)System.out.println(Nibble4(skyLight.getValue(), blockPos));
						
						//System.out.println(c.getX()+" "+c.getZ()+" "+x+" "+y.getValue().byteValue()+" "+z);
					}
					break level;
				}
			}
			
//			byte[] Blocks=new byte[4096];
//			fis.read(Blocks);
//			byte[] Add=new byte[2048];
//			fis.read(Add);
//			byte[] Data=new byte[2048];
//			fis.read(Data);
//			byte[] BlockLight=new byte[2048];
//			fis.read(BlockLight);
//			byte[] SkyLight=new byte[2048];
//			fis.read(SkyLight);
//			
//			for(int x=0;x<16;x++) {
//				int y=x, z=x;
//				int BlockPos = y*16*16 + z*16 + x;
//				byte BlockID_a = Blocks[BlockPos];
//				byte BlockID_b = Nibble4(Add, BlockPos);
//				short BlockID = (short)((BlockID_b << 8) | (BlockID_a & 0xff));//ByteBuffer.wrap(new byte[] {BlockID_b,BlockID_a}).getShort();
//				byte BlockData = Nibble4(Data, BlockPos);
//				byte Blocklight = Nibble4(BlockLight, BlockPos);
//				byte Skylight = Nibble4(SkyLight, BlockPos);
//				
//				System.out.println("["+x+","+y+","+z+"] "+BlockID+" "+String.format("%02X", BlockID_b)+String.format("%02X", BlockID_a));
//			}
			fis.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static byte Nibble4(byte[] arr, int index){
		return (byte) (index%2 == 0 ? (arr[index/2]&0x0F) : ((arr[index/2]>>4)&0x0F));
	}
	public static void test3() {
		String region="region/r.0.1.mca";
		File infile=new File("D:\\minecraft\\saves\\Survival\\"+region);
//		File infile=new File("D:\\minecraft\\saves\\1_17_1\\"+region);
		
		try {
			FileInputStream fis=new FileInputStream(infile);
			List<ChunkData> chunks=ChunkData.getChunks(fis);
			//NBTElement.show(chunks.get(0).getNBTInfo());
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
}
