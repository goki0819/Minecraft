package util.nbt.value;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class LongArray implements NBTReadableValue<byte[]>{
	
	private byte[] value;
	
	@Override
	public void read(InputStream inputStream) throws IOException {
		byte[] len=new byte[4];
		inputStream.read(len);
		value=new byte[ByteBuffer.wrap(len).getInt() * 8];
		inputStream.read(value);
	}
	
	public byte[] getValue() {return value;}
	public long[] getLongValue() {
		long[] l=new long[value.length/8];
		for(int i=0;i<l.length;i++) l[i]=ByteBuffer.wrap(value, i*8, 8).getLong();
		return l;
	}
	public String toString() {return "LongArray["+value.length/8+"]";}
}
