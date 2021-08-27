package util.nbt.value;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class ListValue implements NBTReadableValue<Integer>{
	
	private int id, size;
	
	@Override
	public void read(InputStream inputStream) throws IOException {
		id=inputStream.read();
		byte[] c=new byte[4];
		inputStream.read(c);
		size=ByteBuffer.wrap(c).getInt();
	}

	public int getId() {
		return id;
	}
	
	/**
	 * @return size of list
	 */
	public Integer getValue() {
		return size;
	}

	public String toString() {
		return "[ID:"+id+", Size:"+size+"]";
	}
}
