package util.nbt.value;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import util.nbt.NBTValue;

public class ByteArray implements NBTValue{
	
	private byte[] value;
	
	@Override
	public void read(InputStream inputStream) throws IOException {
		byte[] len=new byte[4];
		inputStream.read(len);
		value=new byte[ByteBuffer.wrap(len).getInt()];
		inputStream.read(value);
	}
	
	public byte[] getValue() {return value;}
}
