package util.nbt.value;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import util.nbt.NBTValue;

public class ListValue implements NBTValue{
	
	private int id, count;
	
	@Override
	public void read(InputStream inputStream) throws IOException {
		id=inputStream.read();
		byte[] c=new byte[4];
		inputStream.read(c);
		count=ByteBuffer.wrap(c).getInt();
	}

	public int getId() {
		return id;
	}

	public int getCount() {
		return count;
	}

	public String toString() {
		return "[ID:"+id+", Count:"+count+"]";
	}
}
