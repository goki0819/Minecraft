package util.world;

import java.util.List;

import util.Node;
import util.nbt.NBTElement;
import util.nbt.value.ByteArray;
import util.nbt.value.LongArray;
import util.nbt.value.NumberValue;

public class SectionData {
	
	private int y=0;
	private BlockData[] blocks;
	private int[] blockStates;
	private byte[] blockLight, skyLight;
	
	private SectionData(Node<NBTElement> sectionNode) {
		NumberValue y=(NumberValue) NBTElement.sort(sectionNode, "[Y]").get(0).getElement().getValue();
		this.y=y.getValue().byteValue()&0xFF;
		
		List<Node<NBTElement>> palettes=NBTElement.sort(sectionNode, "9[Palette]/10");
		this.blocks=BlockData.getBlocks(palettes);
		
		List<Node<NBTElement>> blockStatesList=NBTElement.sort(sectionNode, "12[BlockStates]");
		List<Node<NBTElement>> blockLightList=NBTElement.sort(sectionNode, "12[BlockLight]");
		List<Node<NBTElement>> skyLightList=NBTElement.sort(sectionNode, "12[SkyLight]");
		LongArray blockStates=null;
		ByteArray blockLight=null, skyLight=null;
		
		if(blockStatesList.size()>0) {
			blockStates=(LongArray) blockStatesList.get(0).getElement().getValue();
		}
		if(blockLightList.size()>0) {
			blockLight=(ByteArray) blockLightList.get(0).getElement().getValue();
			this.blockLight=blockLight.getValue();
		}
		if(skyLightList.size()>0) {
			skyLight=(ByteArray) skyLightList.get(0).getElement().getValue();
			this.skyLight=skyLight.getValue();
		}
		
		if(blockStates!=null) {
			int n=(int) Math.ceil(Math.log(palettes.size()) / Math.log(2));
			//System.out.println(palettes.size()+" "+n);
			byte[] bs_bin=blockStates.getValue();
			long[] bs_long=blockStates.getLongValue();
			
			if(bs_long.length==410)for(BlockData bd:blocks)System.out.println(bd);
			for(int i=0;i<bs_long.length;i++) {
				byte[] b=new byte[8];
				System.arraycopy(bs_bin, i*8, b, 0, 8);
				int[] ids=null;
				
				if(bs_long.length==410) {
					ids=new int[10];
					
					ids[0]=(b[0]<<2 & 0x3C) | (b[1] >> 6 & 0x3);
					ids[1]=(b[1] & 0x3F);
					ids[2]=(b[2]>>2 & 0x3F);
					ids[3]=(b[2]<<4 & 0x30) | (b[3] >> 4 & 0xF);
					ids[4]=(b[3]<<2 & 0x3C) | (b[4] >> 6 & 0x3);
					ids[5]=(b[4] & 0x3F);
					ids[6]=(b[5]>>2 & 0x3F);
					ids[7]=(b[5]<<4 & 0x30) | (b[6] >> 4 & 0xF);
					ids[8]=(b[6]<<2 & 0x3C) | (b[7] >> 6 & 0x3);
					ids[9]=(b[7] & 0x3F);
					
					System.out.print("["+i+"]_"+bs_long[i]+"        ");
					for(int j=0;j<ids.length;j++)System.out.print(ids[j]+" ");
					System.out.println();
				}else if(bs_long.length==342 || bs_long.length==320) {
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
//					System.out.println(palettes.size()+" "+blockStates.getValue().length +" "+blockPos);
//					System.out.print("["+i+"]_"+bs[i]+", "+b[0]+" "+b[1]+" "+b[2]+" "+b[3]+" "+b[4]+" "+b[5]+" "+b[6]+" "+b[7]+"        ");
//					for(int j=0;j<ids.length;j++)System.out.print(ids[j]+" ");
//					System.out.println();
					throw new IllegalArgumentException("illegal length. "+bs_long[i]+" "+bs_long.length);
				}
				
//				System.out.println(palettes.size()+" "+blockStates.getValue().length +" "+blockPos);
				
//				System.out.print("["+i+"]_"+bs_long[i]+"        ");
//				for(int j=0;j<ids.length;j++)System.out.print(ids[j]+" ");
//				System.out.println();
			}
		}
	}
	
	public int getY() {return y;}
	
	private static byte Nibble4(byte[] arr, int index){
		return (byte) (index%2 == 0 ? (arr[index/2]&0x0F) : ((arr[index/2]>>4)&0x0F));
	}
	
	public static SectionData[] getSections(Node<NBTElement> root){
		SectionData[] sections=new SectionData[256];
		List<Node<NBTElement>> slist=NBTElement.sort(root, "10/10[Level]/9[Sections]/10");
		
		for(Node<NBTElement> s:slist) {
			SectionData sd=new SectionData(s);
			sections[sd.getY()]=sd;
		}
		
		return sections;
	}
}
