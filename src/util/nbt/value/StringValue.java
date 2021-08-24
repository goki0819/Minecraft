package util.nbt.value;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import util.nbt.NBTValue;

public class StringValue implements NBTValue{

	private String value;
	
	@Override
	public void read(InputStream inputStream) throws IOException {
		byte[] len=new byte[2];
		inputStream.read(len);
		byte[] str=new byte[ByteBuffer.wrap(len).getShort()];
		inputStream.read(str);
		value=new String(str, StandardCharsets.UTF_8);
	}
	
	public String toString() {return value;}
}
