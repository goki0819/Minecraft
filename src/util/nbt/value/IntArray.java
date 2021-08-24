package util.nbt.value;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import util.nbt.NBTValue;

public class IntArray implements NBTValue{
	
	private int[] value;
	
	@Override
	public void read(InputStream inputStream) throws IOException {
		byte[] len=new byte[4];
		inputStream.read(len);
		
		value=new int[ByteBuffer.wrap(len).getInt()];
		byte[] v=new byte[4];
		for(int i=0;i<value.length;i++) {
			inputStream.read(v);
			value[i]=ByteBuffer.wrap(v).getInt();
		}
	}
	
	public int[] getValue() {return value;}
}
