package util.world;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;

import util.Node;
import util.nbt.NBTElement;
import util.nbt.NBTTokenizer;

public class ChunkData implements Comparable<ChunkData>{
	private int offset,sectorIndex,sectorCount,timeStamp,x,z;
	/**
	 * 1*16*1 Sections
	 */
	private SectionData[] sections;
	private Node<NBTElement> root;
	
	private ChunkData(int i, byte[] offsets, byte[] timeStamp) {
		this.offset=ByteBuffer.wrap(offsets, i*4, 4).getInt();
		this.sectorIndex = offset>>8;
		this.sectorCount = offset&0xFF;
		
		this.timeStamp=ByteBuffer.wrap(timeStamp, i*4, 4).getInt();
		
		this.x=i%32;
		this.z=i/32;
	}
	
	public int getTimeStamp() {return timeStamp;}
	public int getSectionX() {return x;}
	public int getSectionZ() {return z;}
	/**
	 * @param x (0~15)
	 * @param y (0~255)
	 * @param z (0~15)
	 * @return
	 */
	public Block getBlock(int x, int y, int z) {
		return sections[y/16]!=null ? sections[y/16].getBlock(x, y%16, z) : Block.AIR;
	}
	public SectionData[] getSections() {return sections;}
	public Node<NBTElement> getNBTInfo(){return root;}
	@Override
	public int compareTo(ChunkData data) {
		return this.sectorIndex>data.sectorIndex ? 1:-1;
	}
	private void readChunkFormat(InputStream in) throws IOException {
		byte[] len_bin=new byte[4];
		byte[] data=new byte[4096*sectorCount-5];
		in.read(len_bin);
		int v=in.read();
		in.read(data);
		
		InputStream zin=null;
		if(v==1)zin = new GZIPInputStream(new ByteArrayInputStream(data));
		else if(v==2)zin = new InflaterInputStream(new ByteArrayInputStream(data));
		else throw new IOException("Illegal compression type. version must be 1 or 2. (v="+v+")");
		
		ByteArrayOutputStream baos=new ByteArrayOutputStream();
		int len;
		byte[] b=new byte[1024 * 4];
		
		while ((len = zin.read(b)) != -1) {
            baos.write(b, 0, len);
        }
		zin.close();
		
		ByteArrayInputStream inputStream = new ByteArrayInputStream(baos.toByteArray());
		NBTTokenizer tkn=new NBTTokenizer(inputStream);
		this.root=tkn.getRootNode();
		
		this.sections=SectionData.getSections(root);
	}
	
	public static ChunkData[] getChunks(File file) throws IOException {return getChunks(new FileInputStream(file));}
	public static ChunkData[] getChunks(InputStream in) throws IOException{
		byte[] offsets=new byte[4096];
		byte[] timeStamp=new byte[4096];
		int currentSectorIndex=2;
		List<ChunkData> cs=new ArrayList<>();
		
		in.read(offsets);
		in.read(timeStamp);
		
		for(int i=0;i<1024;i++) {
			ChunkData c=new ChunkData(i, offsets, timeStamp);
			if(c.sectorIndex>=2)cs.add(c);
		}
		
		Collections.sort(cs);
		
		for(int i=0;i<cs.size();i++) {
			ChunkData d=cs.get(i);
			int skip=d.sectorIndex-currentSectorIndex;
			currentSectorIndex=d.sectorIndex+d.sectorCount;
			
			in.skip(4096*skip);
			
			d.readChunkFormat(in);
		}
		
		ChunkData[] out=new ChunkData[1024];
		
		for(ChunkData cd:cs) {
			out[cd.getSectionZ()*32+cd.getSectionX()]=cd;
		}
		
		return out;
	}
}
