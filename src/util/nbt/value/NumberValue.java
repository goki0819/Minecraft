package util.nbt.value;

import java.io.InputStream;
import java.math.BigDecimal;

import util.nbt.NBTValue;

public class NumberValue implements NBTValue{
	
	private BigDecimal value;
	
	public NumberValue(long value) {
		this.value=new BigDecimal(value);
	}
	public NumberValue(double value) {
		this.value=new BigDecimal(value);
	}
	
	@Override
	public void read(InputStream inputStream) {}
	
	public String toString() {return value.toString();}
}
