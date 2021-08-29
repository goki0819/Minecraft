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
	private byte[] blockStates=new byte[4096];
	private byte[] blockLight=null, skyLight=null;
	
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
			setBlock(blockStates);
		}
		if(blockLightList.size()>0) {
			blockLight=(ByteArray) blockLightList.get(0).getElement().getValue();
			this.blockLight=blockLight.getValue();
		}
		if(skyLightList.size()>0) {
			skyLight=(ByteArray) skyLightList.get(0).getElement().getValue();
			this.skyLight=skyLight.getValue();
		}
	}
	
	public int getSectionY() {return y;}
	/**
	 * @param x (0~16)
	 * @param y (0~16)
	 * @param z (0~16)
	 * @return
	 */
	public Block getBlock(int x, int y, int z) {
		int blockPos=y*16*16+z*16+x;
		byte bl = blockLight==null ? 0 : Nibble4(blockLight, blockPos);
		byte sl = skyLight==null ? 0 : Nibble4(skyLight, blockPos);
		
		if(blocks==null)return Block.AIR;
		return new Block(blocks[blockStates[blockPos]], bl, sl);
	}
	
	private void setBlock(LongArray blockStates) {
		byte[] bs_bin=blockStates.getValue();
		long[] bs_long=blockStates.getLongValue();
		
		if(bs_long.length==410) {
			int index=0;
			for(int i=0;i<bs_long.length;i++) {
				byte[] b=new byte[8];
				System.arraycopy(bs_bin, i*8, b, 0, 8);
				byte[] ids=new byte[10];
				
				ids[0]=(byte) ((b[0]<<2 & 0x3C) | (b[1] >> 6 & 0x3));
				ids[1]=(byte) (b[1] & 0x3F);
				ids[2]=(byte) (b[2]>>2 & 0x3F);
				ids[3]=(byte) ((b[2]<<4 & 0x30) | (b[3] >> 4 & 0xF));
				ids[4]=(byte) ((b[3]<<2 & 0x3C) | (b[4] >> 6 & 0x3));
				ids[5]=(byte) (b[4] & 0x3F);
				ids[6]=(byte) (b[5]>>2 & 0x3F);
				ids[7]=(byte) ((b[5]<<4 & 0x30) | (b[6] >> 4 & 0xF));
				ids[8]=(byte) ((b[6]<<2 & 0x3C) | (b[7] >> 6 & 0x3));
				ids[9]=(byte) (b[7] & 0x3F);
				
				for(int j=ids.length-1; j>=0 && index<this.blockStates.length; j--) {
					this.blockStates[index++]=ids[j];
				}
			}
		}else if(bs_long.length==384) {//6bit
			for(int i=0;i<this.blockStates.length;i++) {
				int bit=i*6;
				int longIndex=bit/64;
				int longBit=bit%64;
				int byteIndex=longBit/8;
				int bitIndex=longBit%8;
				int realByteIndex=longIndex*8+7-byteIndex;
				byte bs1=(byte) (bs_bin[realByteIndex]>>bitIndex);
				
				if(bitIndex<=2)this.blockStates[i] = (byte) (bs1&0x3F);
				else {
					byte bs2 = (byte) ((realByteIndex%8==0 ? bs_bin[realByteIndex+15] : bs_bin[realByteIndex-1])<<(8-bitIndex));
					if(bitIndex==3)this.blockStates[i]=(byte) ((bs1 & 0x1F) | (bs2 & 0x20));
					else if(bitIndex==4)this.blockStates[i]=(byte) ((bs1 & 0xF) | (bs2 & 0x30));
					else if(bitIndex==5)this.blockStates[i]=(byte) ((bs1 & 0x7) | (bs2 & 0x38));
					else if(bitIndex==6)this.blockStates[i]=(byte) ((bs1 & 0x3) | (bs2 & 0x3C));
					else if(bitIndex==7)this.blockStates[i]=(byte) ((bs1 & 0x1) | (bs2 & 0x3E));
				}
			}
		}else if(bs_long.length==342) {
			int index=0;
			for(int i=0;i<bs_long.length;i++) {
				byte[] b=new byte[8];
				System.arraycopy(bs_bin, i*8, b, 0, 8);
				byte[] ids=new byte[12];
				
				ids[0]=(byte) ((b[0]<<1 & 0x1E) | (b[1] >> 7 & 0x1));
				ids[1]=(byte) (b[1]>>2&0x1F);
				ids[2]=(byte) ((b[1]<<3 & 0x18) | (b[2] >> 5 & 0x7));
				ids[3]=(byte) (b[2] & 0x1F);
				ids[4]=(byte) (b[3]>>3 & 0x1F);
				ids[5]=(byte) ((b[3]<<2 & 0x1C) | (b[4] >> 6 & 0x3));
				ids[6]=(byte) (b[4]>>1 & 0x1F);
				ids[7]=(byte) ((b[4]<<4 & 0x10) | (b[5] >> 4 & 0xF));
				ids[8]=(byte) ((b[5]<<1 & 0x1E) | (b[6] >> 7 & 0x1));
				ids[9]=(byte) (b[6]>>2 & 0x1F);
				ids[10]=(byte) ((b[6]<<3 & 0x18) | (b[7] >> 5 & 0x7));
				ids[11]=(byte) (b[7] & 0x1F);
				
				for(int j=ids.length-1; j>=0 && index<this.blockStates.length; j--) {
					this.blockStates[index++]=ids[j];
				}
			}
		}else if(bs_long.length==320) {
			for(int i=0;i<this.blockStates.length;i++) {
				int bit=i*5;
				int longIndex=bit/64;
				int longBit=bit%64;
				int byteIndex=longBit/8;
				int bitIndex=longBit%8;
				int realByteIndex=longIndex*8+7-byteIndex;
				byte bs1=(byte) (bs_bin[realByteIndex]>>bitIndex);
				
				if(bitIndex<=3)this.blockStates[i] = (byte) (bs1&0x1F);
				else {
					byte bs2 = (byte) ((realByteIndex%8==0 ? bs_bin[realByteIndex+15] : bs_bin[realByteIndex-1])<<(8-bitIndex));
					if(bitIndex==4)this.blockStates[i]=(byte) ((bs1 & 0xF) | (bs2 & 0x10));
					else if(bitIndex==5)this.blockStates[i]=(byte) ((bs1 & 0x7) | (bs2 & 0x18));
					else if(bitIndex==6)this.blockStates[i]=(byte) ((bs1 & 0x3) | (bs2 & 0x1C));
					else if(bitIndex==7)this.blockStates[i]=(byte) ((bs1 & 0x1) | (bs2 & 0x1E));
				}
			}
		}else if(bs_long.length==256) {
			int index=0;
			for(int i=0;i<bs_long.length;i++) {
				byte[] b=new byte[8];
				System.arraycopy(bs_bin, i*8, b, 0, 8);
				byte[] ids=new byte[16];
				for(int j=0;j<8;j++) {
					ids[j*2]=(byte) (b[j]>>4&0xF);
					ids[j*2+1]=(byte) (b[j]&0xF);
				}
				
				for(int j=ids.length-1; j>=0 && index<this.blockStates.length; j--) {
					this.blockStates[index++]=ids[j];
				}
			}
		}else {
//			System.out.println(palettes.size()+" "+blockStates.getValue().length +" "+blockPos);
//			System.out.print("["+i+"]_"+bs[i]+", "+b[0]+" "+b[1]+" "+b[2]+" "+b[3]+" "+b[4]+" "+b[5]+" "+b[6]+" "+b[7]+"        ");
//			for(int j=0;j<ids.length;j++)System.out.print(ids[j]+" ");
//			System.out.println();
			throw new IllegalArgumentException("illegal length. "+bs_long.length);
		}
	}
	private static byte Nibble4(byte[] arr, int index){
		return (byte) (index%2 == 0 ? (arr[index/2]&0x0F) : ((arr[index/2]>>4)&0x0F));
	}
	
	public static SectionData[] getSections(Node<NBTElement> root){
		SectionData[] sections=new SectionData[256];
		List<Node<NBTElement>> slist=NBTElement.sort(root, "10/10[Level]/9[Sections]/10");
		
		for(Node<NBTElement> s:slist) {
			SectionData sd=new SectionData(s);
			sections[sd.getSectionY()]=sd;
		}
		
		return sections;
	}
}
