package util.nbt;

import java.io.IOException;
import java.io.InputStream;

public interface NBTValue {
	public void read(InputStream inputStream) throws IOException;
}
