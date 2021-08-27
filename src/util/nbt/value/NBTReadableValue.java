package util.nbt.value;

import java.io.IOException;
import java.io.InputStream;

import util.nbt.NBTValue;

public interface NBTReadableValue<T> extends NBTValue<T>{
	public void read(InputStream inputStream) throws IOException;
}
