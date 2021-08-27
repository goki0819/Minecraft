package util.nbt.value;

import java.io.InputStream;
import java.math.BigDecimal;

public class NumberValue implements NBTReadableValue<Number>{
	
	private Number value;
	
	public NumberValue(int value) {
		this.value=new BigDecimal(value);
	}
	public NumberValue(long value) {
		this.value=new BigDecimal(value);
	}
	public NumberValue(double value) {
		this.value=new BigDecimal(value);
	}
	
	@Override @Deprecated
	public void read(InputStream inputStream) {}
	
	public Number getValue() {return value;}
	public String toString() {return value.toString();}
}
